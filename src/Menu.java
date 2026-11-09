import java.util.StringJoiner;

/** 把菜單變成文字（已完成）。關卡 3 寫 AI 提示詞（prompt）時會用到。 */
public final class Menu {

    private Menu() {
        // 工具類別，不需要建立物件
    }

    public static String describe() {
        StringJoiner drinks = new StringJoiner("、", "飲料：", "\n");
        Drink[] all = {
            new MilkTea(SugarLevel.FULL, IceLevel.REGULAR),
            new FruitTea(SugarLevel.FULL, IceLevel.REGULAR)
        };
        for (Drink d : all) {
            drinks.add(d.getName() + "（NT$" + d.getBasePrice() + "）");
        }
        StringJoiner toppings = new StringJoiner("、", "配料：", "\n");
        for (Topping t : Topping.values()) {
            toppings.add(t.getLabel() + "（+NT$" + t.getPrice() + "）");
        }
        StringJoiner sugar = new StringJoiner("、", "甜度：", "\n");
        for (SugarLevel s : SugarLevel.values()) {
            sugar.add(s.getLabel());
        }
        StringJoiner ice = new StringJoiner("、", "冰塊：", "");
        for (IceLevel i : IceLevel.values()) {
            ice.add(i.getLabel());
        }
        return drinks.toString() + toppings + sugar + ice;
    }
}
