/**
 * ===================== 關卡 1 =====================
 * 完成這個類別，可以參考 MilkTea.java。
 *
 *   - 名字是 "水果茶"
 *   - 基本價格是 60
 *
 * 檢查：.\run.bat check
 * ==================================================
 */
public class FruitTea extends Drink {

    public FruitTea(SugarLevel sugar, IceLevel ice) {
        super(sugar, ice);
    }

    @Override
    public String getName() {
        // TODO 1a：回傳這杯飲料的名字
        return "TODO";
    }

    @Override
    public int getBasePrice() {
        // TODO 1b：回傳這杯飲料的基本價格
        return 0;
    }
}
