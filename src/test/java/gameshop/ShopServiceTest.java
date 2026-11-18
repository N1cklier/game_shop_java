package gameshop;

import gameshop.database.DatabaseManager;
import gameshop.exception.*;
import gameshop.service.ShopService;
import gameshop.service.ShopServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @TempDir
    Path tempDir;

    private ShopService shopService;

    @BeforeEach
    void setUp() throws Exception {
        String dbPath = tempDir.resolve("test.db").toString();

        System.setProperty(
                "gameshop.db.url",
                "jdbc:sqlite:" + dbPath
        );

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE PLAYER (
                    player_id INTEGER PRIMARY KEY,
                    nickname TEXT NOT NULL,
                    balance INTEGER NOT NULL
                )
            """);

            statement.execute("""
                CREATE TABLE ITEM (
                    item_id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    price INTEGER NOT NULL,
                    description TEXT
                )
            """);

            statement.execute("""
                CREATE TABLE INVENTORY (
                    player_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    PRIMARY KEY (player_id, item_id),
                    FOREIGN KEY (player_id)
                        REFERENCES PLAYER(player_id),
                    FOREIGN KEY (item_id)
                        REFERENCES ITEM(item_id)
                )
            """);

            statement.execute("""
                CREATE TABLE PURCHASE (
                    purchase_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    purchase_date TEXT NOT NULL,
                    FOREIGN KEY (player_id)
                        REFERENCES PLAYER(player_id),
                    FOREIGN KEY (item_id)
                        REFERENCES ITEM(item_id)
                )
            """);

            statement.execute("""
                INSERT INTO PLAYER
                VALUES (1, 'TestPlayer', 1000)
            """);

            statement.execute("""
                INSERT INTO ITEM
                VALUES
                (1, 'Health Potion', 150, 'Test item'),
                (2, 'Rare Crystal', 700, 'Test item')
            """);
        }

        shopService = new ShopServiceImpl();
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("gameshop.db.url");
    }

    private int getBalance() throws SQLException {
        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT balance FROM PLAYER WHERE player_id = 1")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private int getQuantity() throws SQLException {
        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT quantity FROM INVENTORY " +
                             "WHERE player_id = 1 AND item_id = 1")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getPurchaseCount() throws SQLException {
        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM PURCHASE")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    void successfulPurchase() throws Exception {
        shopService.processPurchase(1, 1);

        assertEquals(850, getBalance());
        assertEquals(1, getQuantity());
        assertEquals(1, getPurchaseCount());
    }

    @Test
    void insufficientFunds() throws Exception {
        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate("UPDATE PLAYER SET balance = 100 WHERE player_id = 1");
        }

        assertThrows(
                InsufficientFundsException.class,
                () -> shopService.processPurchase(1, 1)
        );

        assertEquals(100, getBalance());
        assertEquals(0, getQuantity());
        assertEquals(0, getPurchaseCount());
    }

    @Test
    void itemNotFound() {
        assertThrows(
                ItemNotFoundException.class,
                () -> shopService.processPurchase(1, 999)
        );
    }

    @Test
    void playerNotFound() {
        assertThrows(
                PlayerNotFoundException.class,
                () -> shopService.processPurchase(999, 1)
        );
    }

    @Test
    void repeatedPurchaseIncreasesQuantity() throws Exception {
        shopService.processPurchase(1, 1);
        shopService.processPurchase(1, 1);

        assertEquals(700, getBalance());
        assertEquals(2, getQuantity());
        assertEquals(2, getPurchaseCount());
    }

    @Test
    void failedPurchaseRollsBackAllChanges() throws Exception {

        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement()) {

            s.execute("""
                CREATE TRIGGER fail_purchase
                BEFORE INSERT ON PURCHASE
                BEGIN
                    SELECT RAISE(ABORT, 'Test failure');
                END
            """);
        }

        assertThrows(
                ShopException.class,
                () -> shopService.processPurchase(1, 1)
        );

        assertEquals(1000, getBalance());
        assertEquals(0, getQuantity());
        assertEquals(0, getPurchaseCount());
    }
}
