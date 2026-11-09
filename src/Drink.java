import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 所有飲料的父類別（parent class）。
 *
 * abstract（抽象）= 不能直接 new Drink(...)，
 * 一定要建立「具體的飲料」，例如 new MilkTea(...)。
 */
public abstract class Drink {

    // private = 只有這個類別自己可以直接改這些欄位（封裝 encapsulation）
    private final SugarLevel sugar;
    private final IceLevel ice;
    private final List<Topping> toppings = new ArrayList<>();

    protected Drink(SugarLevel sugar, IceLevel ice) {
        if (sugar == null || ice == null) {
            throw new IllegalArgumentException("甜度和冰塊不能是 null");
        }
        this.sugar = sugar;
        this.ice = ice;
    }

    /** 每一種飲料都「必須」告訴我們它的名字。 */
    public abstract String getName();

    /** 每一種飲料都「必須」告訴我們它的基本價格（NT$，不含配料）。 */
    public abstract int getBasePrice();

    public void addTopping(Topping topping) {
        if (topping == null) {
            throw new IllegalArgumentException("配料不能是 null");
        }
        toppings.add(topping);
    }

    /**
     * 最終價格 = 基本價格 + 所有配料。
     * 這個規則「所有飲料都一樣」，所以寫在父類別。
     * getBasePrice() 每種飲料不一樣 → 這就是多型（polymorphism）。
     */
    public int getPrice() {
        int total = getBasePrice();
        for (Topping t : toppings) {
            total += t.getPrice();
        }
        return total;
    }

    public SugarLevel getSugar() {
        return sugar;
    }

    public IceLevel getIce() {
        return ice;
    }

    /** 唯讀的清單：外面的程式可以看，但不能修改。 */
    public List<Topping> getToppings() {
        return Collections.unmodifiableList(toppings);
    }

    /** 例如：奶茶（半糖、少冰）+ 珍珠 */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName())
          .append("（").append(sugar.getLabel()).append("、").append(ice.getLabel()).append("）");
        for (Topping t : toppings) {
            sb.append(" + ").append(t.getLabel());
        }
        return sb.toString();
    }
}
