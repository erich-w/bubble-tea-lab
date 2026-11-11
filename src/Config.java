import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 從 api_key.properties 讀取你的 API 金鑰（已完成，不用修改）。
 *
 * 為什麼要放在另一個檔案？因為金鑰「絕對不能」寫在程式碼裡。
 * api_key.properties 已經列在 .gitignore，所以不會被上傳到 GitHub。
 */
public final class Config {

    public static final String FILE_NAME = "api_key.properties";
    public static final String DEFAULT_MODEL = "gemma-4-26b-a4b-it";
    /** Gemma 忙碌時改用的備用模型。在 api_key.properties 寫 fallbackModel= （留空）可以關掉。 */
    public static final String DEFAULT_FALLBACK_MODEL = "gemini-3.5-flash";
    public static final String DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    private final String apiKey;
    private final String model;
    private final String fallbackModel;
    private final String baseUrl;

    private Config(String apiKey, String model, String fallbackModel, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.fallbackModel = fallbackModel;
        this.baseUrl = baseUrl;
    }

    public static Config load() {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            throw new IllegalStateException(
                "找不到 " + FILE_NAME + "（位置：" + Paths.get("").toAbsolutePath() + "）"
                + "\n  -> 請執行 setup.bat，或把 api_key.properties.example 複製成 "
                + FILE_NAME + " 並貼上你的金鑰。");
        }
        Properties p = new Properties();
        try {
            String text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            text = text.replace("\uFEFF", ""); // 有些編輯器會加看不見的 BOM 字元
            p.load(new StringReader(text));
        } catch (IOException e) {
            throw new IllegalStateException("無法讀取 " + FILE_NAME + "：" + e.getMessage());
        }
        String key = clean(p.getProperty("apiKey"));
        if (key.isEmpty() || key.equals("PASTE_YOUR_KEY_HERE")) {
            throw new IllegalStateException(FILE_NAME + " 裡的 apiKey 是空的，請把金鑰貼在 apiKey= 後面");
        }
        String model = clean(p.getProperty("model"));
        String fallback = p.getProperty("fallbackModel") == null
                ? DEFAULT_FALLBACK_MODEL : clean(p.getProperty("fallbackModel"));
        String baseUrl = clean(p.getProperty("baseUrl"));
        return new Config(key,
                model.isEmpty() ? DEFAULT_MODEL : model,
                fallback,
                baseUrl.isEmpty() ? DEFAULT_BASE_URL : baseUrl);
    }

    private static String clean(String s) {
        if (s == null) return "";
        // 去掉空白、引號和看不見的 BOM
        return s.replace("﻿", "").trim().replaceAll("^\"|\"$", "");
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    /** 空字串 = 不使用備用模型。 */
    public String getFallbackModel() {
        return fallbackModel;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /** "AIza...k9Xq"：只顯示頭尾，可以安全印在螢幕上。 */
    public String getMaskedKey() {
        if (apiKey.length() <= 8) return "****";
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
