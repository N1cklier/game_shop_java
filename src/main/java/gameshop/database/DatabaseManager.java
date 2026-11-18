package gameshop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DEFAULT_URL = "jdbc:sqlite:database/game_shop.db";

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty(
                "gameshop.db.url",
                DEFAULT_URL
        );

        Connection connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}
