/**
 * ===================== 關卡 3 =====================
 * 用 AI 模型來推薦飲料，而不是 if/else 規則。
 * 它實作（implements）跟 RuleBasedRecommender「一樣」的介面。
 *
 * 檢查：.\run.bat check   （用假的 AI 檢查，不需要網路）
 * 接著：到 Main.java 換成這個類別，再執行 .\run.bat（用真的 AI）
 * ==================================================
 */
public class LLMRecommender implements DrinkRecommender {

    private final LLMClient client;

    public LLMRecommender(LLMClient client) {
        this.client = client;
    }

    @Override
    public String recommend(String mood) {
        // TODO 3a：組出要給 AI 的 prompt（一個 String），內容要包含：
        //          - AI 的角色，例如 "你是一家手搖飲店親切的店員。"
        //          - 菜單：用 Menu.describe()
        //          - 客人的心情（參數 mood）
        //          - 你希望它怎麼回答，例如 "請只從菜單中推薦一杯飲料、甜度、冰塊和配料，
        //            用繁體中文、兩句話以內回答。"
        String prompt = "";

        // TODO 3b：用 client.generate(prompt) 把 prompt 送出去，並回傳 AI 的答案。
        //          generate() 有可能失敗（沒網路、金鑰錯誤、用量超過……）。
        //          用 try/catch 讓程式「不會當掉」，在 catch 裡回傳一句友善的訊息，
        //          並附上 e.getMessage()。
        return "TODO";
    }
}
