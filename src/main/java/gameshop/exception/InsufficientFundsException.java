package gameshop.exception;

public class InsufficientFundsException extends ShopException {

    public InsufficientFundsException(int balance, int price) {
        super("Недостаточно средств. Баланс: " + balance + ", стоимость: " + price);
    }
}
