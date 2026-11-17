package gameshop.exception;

public class ItemNotFoundException extends ShopException {

    public ItemNotFoundException(int itemId) {
        super("Предмет с ID " + itemId + " не найден");
    }
}
