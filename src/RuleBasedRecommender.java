/** 用 if/else 規則做推薦（已完成）。 */
public class RuleBasedRecommender implements DrinkRecommender {

    @Override
    public String recommend(String mood) {
        String m = mood.toLowerCase();
        if (has(m, "累", "想睡", "考試", "期中", "報告", "tired", "sleepy", "exam")) {
            return "奶茶，全糖，加布丁。補充一下能量吧！";
        }
        if (has(m, "熱", "流汗", "hot")) {
            return "水果茶，微糖，正常冰。消暑一下！";
        }
        if (has(m, "難過", "壓力", "煩", "失戀", "sad", "stress")) {
            return "奶茶，半糖，加珍珠。來一杯療癒一下。";
        }
        return "奶茶，半糖少冰加珍珠。經典不會錯。";
    }

    private static boolean has(String text, String... words) {
        for (String w : words) {
            if (text.contains(w)) return true;
        }
        return false;
    }
}
