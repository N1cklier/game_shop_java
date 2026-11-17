
package gameshop.model;

public class InventoryEntry {
    private int playerId;
    private int itemId;
    private int quantity;

    public InventoryEntry(int playerId, int itemId, int quantity) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Количество предметов должно быть положительным"
            );
        }
        this.quantity = quantity;
    }
}
