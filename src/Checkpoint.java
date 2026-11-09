/**
 * 檢查你的程式（已完成，不用修改）。
 * 執行：.\run.bat check
 */
public class Checkpoint {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("\n========== 關卡 1：FruitTea ==========");
        checkpoint1();
        System.out.println("\n========== 關卡 2：Order ==========");
        checkpoint2();
        System.out.println("\n========== 關卡 3：LLMRecommender ==========");
        checkpoint3();

        System.out.println("\n==========================================");
        System.out.println("  通過 " + passed + " 項，失敗 " + failed + " 項");
        System.out.println(failed == 0 ? "  全部完成！接著做 Main.java 的步驟 4。" : "  加油！先修第一個 [FAIL]。");
        System.out.println("==========================================\n");
    }

    private static void checkpoint1() {
        Drink tea = new FruitTea(SugarLevel.HALF, IceLevel.REGULAR);
        check("FruitTea 的名字是「水果茶」", "水果茶".equals(tea.getName()),
                "目前是「" + tea.getName() + "」");
        check("FruitTea 的基本價格是 60", tea.getBasePrice() == 60,
                "目前是 " + tea.getBasePrice());
        tea.addTopping(Topping.PEARLS);
        check("水果茶加珍珠是 70 元", tea.getPrice() == 70,
                "目前是 " + tea.getPrice());
    }

    private static void checkpoint2() {
        Order order = new Order();
        order.addDrink(new MilkTea(SugarLevel.HALF, IceLevel.REGULAR));   // 50
        Drink fruit = new FruitTea(SugarLevel.LIGHT, IceLevel.NO_ICE);    // 60
        fruit.addTopping(Topping.PUDDING);                                 // +15
        order.addDrink(fruit);
        check("addDrink() 有把飲料加進清單", order.getDrinks().size() == 2,
                "清單裡有 " + order.getDrinks().size() + " 杯");
        check("奶茶 + 水果茶加布丁，getTotal() 是 125", order.getTotal() == 125,
                "目前是 " + order.getTotal());
        check("空的訂單總計是 0", new Order().getTotal() == 0, "");
        boolean threw = false;
        try {
            new Order().addDrink(null);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("addDrink(null) 會丟出 IllegalArgumentException", threw, "沒有丟出例外");
    }

    private static void checkpoint3() {
        // 假的 AI：記下收到的 prompt，回傳固定答案
        final String[] seen = {null};
        LLMClient fakeAi = prompt -> {
            seen[0] = prompt;
            return "FAKE-AI-ANSWER";
        };
        String answer = new LLMRecommender(fakeAi).recommend("考完期中好想睡");
        check("recommend() 有把 prompt 送給 AI", seen[0] != null && !seen[0].isEmpty(),
                "沒有呼叫 client.generate()，或 prompt 是空的");
        check("prompt 裡有客人的心情",
                seen[0] != null && seen[0].contains("考完期中好想睡"), "prompt 裡找不到 mood");
        check("prompt 裡有菜單（Menu.describe()）",
                seen[0] != null && seen[0].contains("奶茶") && seen[0].contains("珍珠"),
                "prompt 裡找不到菜單");
        check("recommend() 回傳 AI 的答案",
                answer != null && answer.contains("FAKE-AI-ANSWER"), "目前回傳「" + answer + "」");

        // 壞掉的 AI：程式不應該當掉
        LLMClient brokenAi = prompt -> {
            throw new Exception("沒有網路（模擬）");
        };
        String name = "AI 出錯時，recommend() 回傳訊息而不是當掉";
        try {
            String r = new LLMRecommender(brokenAi).recommend("開心");
            check(name, r != null && !r.isEmpty() && !r.equals("TODO"), "目前回傳「" + r + "」");
        } catch (Exception e) {
            check(name, false, "程式當掉了：" + e);
        }
    }

    private static void check(String name, boolean ok, String detail) {
        if (ok) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            failed++;
            System.out.println("  [FAIL] " + name + (detail.isEmpty() ? "" : "（" + detail + "）"));
        }
    }
}
