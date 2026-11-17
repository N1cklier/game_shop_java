package gameshop.model;

public class Item {
    private int itemId;
    private String name;
    private int price;
    private String description;

    public Item(int itemId, String name, int price,
                String description) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
