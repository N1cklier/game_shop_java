
package gameshop.repository;

import gameshop.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PlayerRepository {

    // Поиск игрока по его идентификатору
    public Optional<Player> findById(Connection connection, int playerId) throws SQLException {

        // SQL-запрос для получения данных игрока
        String sql = "SELECT * FROM PLAYER WHERE player_id = ?";

        // Подготавливаем запрос к выполнению
        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // Передаём ID игрока в запрос
            statement.setInt(1, playerId);

            // Выполняем запрос и получаем результат
            try (ResultSet result = statement.executeQuery()) {

                // Если игрок найден, создаём объект Player
                if (result.next()) {

                    Player player = new Player(
                            result.getInt("player_id"),
                            result.getString("nickname"),
                            result.getInt("balance")
                    );

                    // Возвращаем найденного игрока
                    return Optional.of(player);
                }
            }
        }

        // Если игрок не найден, возвращаем пустой результат
        return Optional.empty();
    }

    // Изменение баланса игрока
    public void updateBalance(
            Connection connection,
            int playerId,
            int newBalance) throws SQLException {

        // SQL-запрос для обновления баланса
        String sql = "UPDATE PLAYER SET balance = ? WHERE player_id = ?";

        // Подготавливаем запрос
        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            // Устанавливаем новое значение баланса
            statement.setInt(1, newBalance);

            // Указываем ID игрока, которому меняем баланс
            statement.setInt(2, playerId);

            // Выполняем обновление записи
            statement.executeUpdate();
        }
    }
}
