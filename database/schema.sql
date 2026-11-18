PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS PLAYER (
    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
    nickname VARCHAR(50) NOT NULL,
    balance INTEGER NOT NULL CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS ITEM (
    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL CHECK (price >= 0),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS PURCHASE (
    purchase_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    price INTEGER NOT NULL CHECK (price >= 0),
    purchase_date TEXT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES PLAYER(player_id),
    FOREIGN KEY (item_id) REFERENCES ITEM(item_id)
);

CREATE TABLE IF NOT EXISTS INVENTORY (
    player_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (player_id, item_id),
    FOREIGN KEY (player_id) REFERENCES PLAYER(player_id),
    FOREIGN KEY (item_id) REFERENCES ITEM(item_id)
);
