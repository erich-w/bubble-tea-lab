/**
 * 已完成的範例，寫 FruitTea 的時候可以參考它。
 *
 * extends Drink = 奶茶「是一種」飲料（IS-A）。
 * 它繼承 Drink 的所有東西，並且完成 Drink 留下來的 abstract 方法。
 */
public class MilkTea extends Drink {

    public MilkTea(SugarLevel sugar, IceLevel ice) {
        super(sugar, ice); // 把甜度、冰塊交給父類別 Drink 的建構子
    }

    @Override
    public String getName() {
        return "奶茶";
    }

    @Override
    public int getBasePrice() {
        return 50;
    }
}
