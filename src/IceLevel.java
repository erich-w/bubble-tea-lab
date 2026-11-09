/** 冰塊選項。 */
public enum IceLevel {
    NO_ICE("去冰"),
    LIGHT_ICE("微冰"),
    LESS_ICE("少冰"),
    REGULAR("正常冰");

    private final String label;

    IceLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
