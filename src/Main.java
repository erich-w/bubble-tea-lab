import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("         歡迎光臨 OOP 手搖飲");
        System.out.println("=====================================\n");

        // ---------- 第 1 部分：一筆訂單 ----------
        Order order = new Order();

        Drink first = new MilkTea(SugarLevel.HALF, IceLevel.LESS_ICE);
        first.addTopping(Topping.PEARLS);

        Drink second = new FruitTea(SugarLevel.LIGHT, IceLevel.REGULAR);
        second.addTopping(Topping.COCONUT_JELLY);
        second.addTopping(Topping.PUDDING);

        order.addDrink(first);
        order.addDrink(second);
        order.printReceipt();

        // ---------- 第 2 部分：飲料推薦 ----------
        DrinkRecommender recommender = new RuleBasedRecommender();

        // ===================== 步驟 4 =====================
        // 換成 AI 版本：把上面那行註解掉，再把下面這行的註解拿掉。
        // 這個檔案其他地方「完全不用改」。這就是介面的威力。
        //
        // DrinkRecommender recommender = new LLMRecommender(new GeminiClient(Config.load()));
        // ==================================================

        Scanner in = new Scanner(System.in, consoleCharset());
        while (true) {
            System.out.print("你今天心情如何？（輸入 q 離開）：");
            if (!in.hasNextLine()) break;
            String mood = in.nextLine().trim();
            if (mood.equalsIgnoreCase("q")) break;
            if (mood.isEmpty()) continue;
            System.out.println("店員：" + recommender.recommend(mood) + "\n");
        }
        System.out.println("謝謝光臨！");
    }

    /** 讓 Windows 終端機的中文輸入正常（不用管這段）。 */
    private static Charset consoleCharset() {
        return System.console() != null ? System.console().charset() : Charset.defaultCharset();
    }
}
