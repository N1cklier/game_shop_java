package gameshop.exception;

public class PlayerNotFoundException extends ShopException {

    public PlayerNotFoundException(int playerId) {
        super("Игрок с ID " + playerId + " не найден");
    }
}
