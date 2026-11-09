/** 甜度選項，跟真的手搖飲店一樣。enum（列舉）= 一組固定的選項。 */
public enum SugarLevel {
    NO_SUGAR("無糖"),
    LIGHT("微糖"),
    HALF("半糖"),
    LESS("少糖"),
    FULL("全糖");

    private final String label;

    SugarLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
