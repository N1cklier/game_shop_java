package gameshop.model;

public class Purchase {
    private int purchaseId;
    private int playerId;
    private int itemId;
    private int price;
    private String purchaseDate;

    public Purchase(int purchaseId, int playerId, int itemId,
                    int price, String purchaseDate) {
        this.purchaseId = purchaseId;
        this.playerId = playerId;
        this.itemId = itemId;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
}
