/**
 * interface（介面）是一份「合約」：
 * 只要是 DrinkRecommender，就一定要有 recommend() 方法。
 * 它只規定「做什麼」，不管「怎麼做」。
 *
 * Main.java 只認識這個介面，所以我們可以把「怎麼做」
 * （簡單規則 → AI）整個換掉，其他程式完全不用改。
 */
public interface DrinkRecommender {

    /** 根據客人現在的心情，回傳一個飲料推薦。 */
    String recommend(String mood);
}
