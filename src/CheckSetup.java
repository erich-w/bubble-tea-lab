/**
 * 檢查 Java、API 金鑰和網路是不是都正常（已完成，不用修改）。
 * setup.bat 會自動執行它，你也可以用 .\run.bat setup 執行。
 */
public class CheckSetup {

    private static final String TEST_PROMPT = "請只回覆這句話：設定完成，準備來杯手搖飲！";

    public static void main(String[] args) {
        System.out.println("\n--- 環境檢查 ---");
        System.out.println("[OK] Java " + System.getProperty("java.version") + " 可以正常使用");

        Config config;
        try {
            config = Config.load();
        } catch (IllegalStateException e) {
            System.out.println("[X]  " + e.getMessage());
            System.exit(1);
            return;
        }
        System.out.println("[OK] 找到 API 金鑰 " + config.getMaskedKey());

        GeminiClient client = new GeminiClient(config);
        System.out.println("...  正在測試主要模型 " + config.getModel() + "（大約 10 秒）");
        boolean mainOk = test(client, config.getModel(), 3);

        boolean fallbackOk = false;
        if (!config.getFallbackModel().isEmpty()) {
            System.out.println("...  正在測試備用模型 " + config.getFallbackModel());
            fallbackOk = test(client, config.getFallbackModel(), 2);
        }

        if (mainOk || fallbackOk) {
            System.out.println("\n全部正常，可以開始上課了！"
                    + (mainOk ? "" : "（Gemma 目前忙碌，程式會自動改用備用模型）") + "\n");
        } else {
            System.out.println("\n兩個模型都無法使用，請把這個畫面給助教看。\n");
            System.exit(2);
        }
    }

    private static boolean test(GeminiClient client, String model, int attempts) {
        try {
            String reply = client.generateWith(model, TEST_PROMPT, attempts);
            System.out.println("[OK] " + model + " 回覆：" + reply);
            return true;
        } catch (Exception e) {
            System.out.println("[X]  " + model + " 失敗：\n   " + e.getMessage());
            return false;
        }
    }
}
