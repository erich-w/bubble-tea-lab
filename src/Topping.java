/** 配料。每一種配料都知道自己的名字和加價（NT$）。 */
public enum Topping {
    PEARLS("珍珠", 10),
    PUDDING("布丁", 15),
    COCONUT_JELLY("椰果", 10);

    private final String label;
    private final int price;

    Topping(String label, int price) {
        this.label = label;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public int getPrice() {
        return price;
    }
}
