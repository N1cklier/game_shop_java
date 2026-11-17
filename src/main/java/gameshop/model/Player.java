package gameshop.model;

public class Player {
    private int playerId;
    private String nickname;
    private int balance;

    public Player(int playerId, String nickname, int balance) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.balance = balance;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException(
                    "Баланс не может быть отрицательным"
            );
        }
        this.balance = balance;
    }
}
