import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 負責跟 Google 的 Gemini API 溝通（免費的 Gemma 模型也在這裡）。
 * 已完成：歡迎閱讀，但不用修改。
 *
 * 呼叫 generate("...") 時發生的事：
 *   1. 組出 JSON：  {"contents":[{"role":"user","parts":[{"text":"..."}]}]}
 *   2. 用 POST 送到  {baseUrl}/models/{model}:generateContent
 *      並把金鑰放在 "x-goog-api-key" 這個 header
 *   3. 讀回傳的 JSON，取出 candidates[0].content.parts[].text
 *
 * Google 的伺服器偶爾會回 500（忙碌），所以：
 *   - 先用主要模型（Gemma）試，失敗會自動重試幾次
 *   - 還是不行，就換成備用模型（fallbackModel）
 */
public class GeminiClient implements LLMClient {

    private static final int MAIN_ATTEMPTS = 3;
    private static final int FALLBACK_ATTEMPTS = 2;

    private final Config config;
    private final HttpClient http;
    private String lastModelUsed = "";

    public GeminiClient(Config config) {
        this.config = config;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .proxy(ProxySelector.getDefault()) // 學校有 proxy 也能用
                .build();
    }

    @Override
    public String generate(String prompt) throws Exception {
        Exception last;
        try {
            return generateWith(config.getModel(), prompt, MAIN_ATTEMPTS);
        } catch (BusyException e) {
            last = e;
        }
        if (!config.getFallbackModel().isEmpty()) {
            try {
                return generateWith(config.getFallbackModel(), prompt, FALLBACK_ATTEMPTS);
            } catch (BusyException e) {
                last = e;
            }
        }
        throw new Exception(last.getMessage());
    }

    /** 最後一次成功回答的是哪個模型。 */
    public String getLastModelUsed() {
        return lastModelUsed;
    }

    /** 用指定的模型呼叫，伺服器忙碌時最多試 attempts 次。 */
    String generateWith(String model, String prompt, int attempts) throws Exception {
        BusyException last = null;
        for (int i = 0; i < attempts; i++) {
            try {
                String text = callOnce(model, prompt);
                lastModelUsed = model;
                return text;
            } catch (BusyException e) {
                last = e;
                if (i < attempts - 1) Thread.sleep(1000L * (i + 1)); // 等 1 秒、2 秒再試
            }
        }
        throw last;
    }

    private String callOnce(String model, String prompt) throws Exception {
        String body = "{\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":"
                + MiniJson.quote(prompt) + "}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + "/models/" + model + ":generateContent"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (java.net.http.HttpTimeoutException e) {
            throw new BusyException("等太久沒有回應（逾時），請再試一次。");
        } catch (java.io.IOException e) {
            throw new Exception("連不上 Google 伺服器，請檢查網路連線（" + e.getClass().getSimpleName() + "）");
        }

        int status = response.statusCode();
        if (status == 200) {
            return extractText(response.body());
        }
        String message = "[" + model + "] " + explainError(status, response.body());
        if (status == 429 || status == 500 || status == 503 || status == 504) {
            throw new BusyException(message); // 暫時性的錯誤：可以重試或換模型
        }
        throw new Exception(message);
    }

    /** 暫時性的錯誤（伺服器忙碌、逾時、用量超過），值得重試。 */
    static class BusyException extends Exception {
        BusyException(String message) {
            super(message);
        }
    }

    /** 從回傳的 JSON 取出答案文字，並跳過模型「思考過程」的部分。 */
    @SuppressWarnings("unchecked")
    static String extractText(String json) throws Exception {
        Map<String, Object> root = (Map<String, Object>) MiniJson.parse(json);
        List<Object> candidates = (List<Object>) root.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new Exception("模型沒有回答（可能被安全機制擋下）。原始回應：" + shorten(json));
        }
        Map<String, Object> first = (Map<String, Object>) candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) first.get("content");
        if (content == null || content.get("parts") == null) {
            throw new Exception("模型回傳空白答案。finishReason=" + first.get("finishReason"));
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : (List<Object>) content.get("parts")) {
            Map<String, Object> part = (Map<String, Object>) o;
            if (Boolean.TRUE.equals(part.get("thought"))) continue; // 跳過思考過程
            Object text = part.get("text");
            if (text != null) sb.append(text);
        }
        return sb.toString().trim();
    }

    /** 把 HTTP 錯誤代碼翻成看得懂的建議。 */
    @SuppressWarnings("unchecked")
    static String explainError(int status, String body) {
        String apiMessage = "";
        try {
            Map<String, Object> root = (Map<String, Object>) MiniJson.parse(body);
            Map<String, Object> err = (Map<String, Object>) root.get("error");
            if (err != null && err.get("message") != null) apiMessage = err.get("message").toString();
        } catch (RuntimeException ignored) {
            apiMessage = shorten(body);
        }
        String advice;
        switch (status) {
            case 400: advice = "請求錯誤，最常見原因：API 金鑰不對或少複製了幾個字。請到 AI Studio 重新複製。"; break;
            case 401:
            case 403: advice = "金鑰沒有權限。請確認金鑰完整，或在 AI Studio 建立一組新的金鑰。"; break;
            case 404: advice = "找不到模型。請檢查 api_key.properties 裡的 model= 是否和 AI Studio 的模型名稱一樣。"; break;
            case 429: advice = "超過使用次數限制，請等一分鐘再試。"; break;
            case 500:
            case 503:
            case 504: advice = "Google 伺服器忙碌中，請等幾秒再試。"; break;
            default:  advice = "未預期的錯誤。";
        }
        return "HTTP " + status + " - " + advice + (apiMessage.isEmpty() ? "" : "\n   Google 說：" + apiMessage);
    }

    private static String shorten(String s) {
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }
}
