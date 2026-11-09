/**
 * 檢查 Java、API 金鑰和網路是不是都正常（已完成，不用修改）。
 * setup.bat 會自動執行它，你也可以用 .\run.bat setup 執行。
 */
public class CheckSetup {

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
        System.out.println("[OK] 找到 API 金鑰 " + config.getMaskedKey() + "，模型 = " + config.getModel());

        System.out.println("...  正在傳送測試訊息給 AI（大約 10 秒）");
        try {
            String reply = new GeminiClient(config).generate("請只回覆這句話：設定完成，準備來杯手搖飲！");
            System.out.println("[OK] AI 回覆：" + reply);
            System.out.println("\n全部正常，可以開始上課了！\n");
        } catch (Exception e) {
            System.out.println("[X]  呼叫 AI 失敗：\n   " + e.getMessage());
            System.out.println("\n請把這個畫面給助教看。\n");
            System.exit(2);
        }
    }
}
