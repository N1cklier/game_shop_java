package gameshop.repository;

import gameshop.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepository {

    public Optional<Item> findById(
            Connection connection, int itemId) throws SQLException {

        String sql = "SELECT * FROM ITEM WHERE item_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Item item = new Item(
                            result.getInt("item_id"),
                            result.getString("name"),
                            result.getInt("price"),
                            result.getString("description")
                    );

                    return Optional.of(item);
                }
            }
        }

        return Optional.empty();
    }

    public List<Item> findAll(Connection connection) throws SQLException {

        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM ITEM";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                items.add(new Item(
                        result.getInt("item_id"),
                        result.getString("name"),
                        result.getInt("price"),
                        result.getString("description")
                ));
            }
        }

        return items;
    }
}
