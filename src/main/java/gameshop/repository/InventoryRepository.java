package gameshop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryRepository {

    public void addItem(
            Connection connection,
            int playerId,
            int itemId) throws SQLException {

        String sql = """
                INSERT INTO INVENTORY (player_id, item_id, quantity)
                VALUES (?, ?, 1)
                ON CONFLICT(player_id, item_id)
                DO UPDATE SET quantity = quantity + 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);
            statement.setInt(2, itemId);

            statement.executeUpdate();
        }
    }
}
