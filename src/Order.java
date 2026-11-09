import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ===================== 關卡 2 =====================
 * 一筆訂單裡有很多杯飲料。請完成 addDrink() 和 getTotal()。
 *
 * 檢查：.\run.bat check
 * ==================================================
 */
public class Order {

    private final List<Drink> drinks = new ArrayList<>();

    public void addDrink(Drink drink) {
        // TODO 2a：如果 drink 是 null，就 throw new IllegalArgumentException("飲料不能是 null")
        //          否則把它加進 drinks 清單
    }

    public int getTotal() {
        // TODO 2b：把清單裡每一杯飲料的價格加起來，回傳總金額
        //          提示：用 for-each 迴圈和 drink.getPrice()
        //          想一想：為什麼你不用知道它是奶茶還是水果茶？
        return 0;
    }

    /** 唯讀的飲料清單（已完成）。 */
    public List<Drink> getDrinks() {
        return Collections.unmodifiableList(drinks);
    }

    /** 印出收據（已完成）。 */
    public void printReceipt() {
        System.out.println("------------- 收據 -------------");
        for (Drink d : drinks) {
            System.out.printf("NT$%4d  %s%n", d.getPrice(), d);
        }
        System.out.println("--------------------------------");
        System.out.printf("NT$%4d  總計%n", getTotal());
        System.out.println();
    }
}
