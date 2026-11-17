package gameshop.service;

import gameshop.database.DatabaseManager;
import gameshop.exception.*;

import gameshop.model.Item;
import gameshop.model.Player;

import gameshop.repository.*;

import java.sql.Connection;
import java.sql.SQLException;

// Реализация интерфейса ShopService
public class ShopServiceImpl implements ShopService {

    // Репозитории для работы с данными
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseRepository purchaseRepository;

    // Конструктор: создаём необходимые репозитории
    public ShopServiceImpl() {
        playerRepository = new PlayerRepository();
        itemRepository = new ItemRepository();
        inventoryRepository = new InventoryRepository();
        purchaseRepository = new PurchaseRepository();
    }

    // Основной метод обработки покупки
    @Override
    public void processPurchase(int playerId, int itemId) throws ShopException {

        // Открываем соединение с базой данных
        try (Connection connection =
                     DatabaseManager.getConnection()) {

            // Начинаем транзакцию
            connection.setAutoCommit(false);

            try {
                // Ищем игрока по ID
                // Если игрок отсутствует, выбрасываем исключение
                Player player = playerRepository
                        .findById(connection, playerId)
                        .orElseThrow(() ->
                                new PlayerNotFoundException(playerId));

                // Ищем предмет в магазине
                // Если предмет отсутствует, выбрасываем исключение
                Item item = itemRepository
                        .findById(connection, itemId)
                        .orElseThrow(() ->
                                new ItemNotFoundException(itemId));

                // Проверяем, хватает ли игроку игровой валюты
                if (player.getBalance() < item.getPrice()) {
                    throw new InsufficientFundsException(
                            player.getBalance(),
                            item.getPrice()
                    );
                }

                // Вычисляем новый баланс после покупки
                int newBalance =
                        player.getBalance() - item.getPrice();

                // Списываем стоимость предмета с баланса
                playerRepository.updateBalance(
                        connection, playerId, newBalance
                );

                // Добавляем предмет в инвентарь игрока
                inventoryRepository.addItem(
                        connection, playerId, itemId
                );

                // Сохраняем информацию о покупке в истории
                purchaseRepository.create(
                        connection,
                        playerId,
                        itemId,
                        item.getPrice()
                );

                // Подтверждаем транзакцию
                // Все изменения сохраняются в базе данных
                connection.commit();

                // Выводим информацию об успешной покупке
                System.out.println("Покупка успешно совершена!");
                System.out.println("Предмет: " + item.getName());
                System.out.println("Списано: " + item.getPrice());
                System.out.println("Остаток баланса: " + newBalance);

            } catch (ShopException | SQLException | RuntimeException exception) {

                // При ошибке отменяем все изменения транзакции
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }

                // Если возникло предусмотренное исключение, передаём его вызывающему коду
                if (exception instanceof ShopException) {
                    throw (ShopException) exception;
                }

                // Остальные ошибки оборачиваем в ShopException
                throw new ShopException(
                        "Ошибка при обработке покупки",
                        exception
                );
            }

        } catch (SQLException exception) {

            // Обрабатываем ошибку подключения к базе данных
            throw new ShopException(
                    "Не удалось подключиться к базе данных",
                    exception
            );
        }
    }
}
