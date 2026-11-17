package gameshop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PurchaseRepository {

    public void create(
            Connection connection,
            int playerId,
            int itemId,
            int price) throws SQLException {

        String sql = """
                INSERT INTO PURCHASE
                (player_id, item_id, price, purchase_date)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);
            statement.setInt(2, itemId);
            statement.setInt(3, price);
            statement.setString(4, LocalDateTime.now().toString());

            statement.executeUpdate();
        }
    }
}
