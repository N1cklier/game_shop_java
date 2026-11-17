package gameshop;

// Импортируем класс для подключения к базе данных
import gameshop.database.DatabaseManager;

// Импортируем базовое исключение магазина
import gameshop.exception.ShopException;

// Импортируем модели игрока и предмета
import gameshop.model.Item;
import gameshop.model.Player;

// Импортируем репозитории для получения данных
import gameshop.repository.ItemRepository;
import gameshop.repository.PlayerRepository;

// Импортируем интерфейс и его реализацию
import gameshop.service.ShopService;
import gameshop.service.ShopServiceImpl;

// Импортируем инструменты для работы с БД
import java.sql.Connection;
import java.sql.SQLException;

// Импортируем коллекции и вспомогательные классы
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Главный класс программы
public class Main {

    // Точка входа в приложение
    public static void main(String[] args) {

        // Создаём объект для считывания команд пользователя
        Scanner scanner = new Scanner(System.in);

        // Создаём сервис обработки покупок
        ShopService shopService = new ShopServiceImpl();

        // Создаём репозитории для работы с игроками и предметами
        PlayerRepository playerRepository = new PlayerRepository();

        ItemRepository itemRepository = new ItemRepository();

        // ID игрока, от имени которого выполняются покупки
        int playerId = 1;

        try {
            // Основной цикл работы игрового магазина
            while (true) {

                System.out.println("\n=== ИГРОВОЙ МАГАЗИН ===");

                // Открываем соединение с базой данных
                try (Connection connection = DatabaseManager.getConnection()) {

                    // Получаем информацию об игроке по его ID
                    Optional<Player> player = playerRepository.findById(connection, playerId);

                    // Проверяем, существует ли игрок
                    if (player.isEmpty()) {
                        System.out.println("Игрок не найден.");
                        return;
                    }

                    // Отображаем имя игрока
                    System.out.println("Игрок: " + player.get().getNickname());

                    // Отображаем текущий баланс игрока
                    System.out.println("Баланс: " + player.get().getBalance());

                    // Получаем список всех предметов магазина
                    List<Item> items = itemRepository.findAll(connection);

                    // Выводим заголовок списка товаров
                    System.out.println("\nДоступные предметы:");

                    // Перебираем все предметы и выводим их данные
                    for (Item item : items) {
                        System.out.println(item.getItemId() + ". " + item.getName() + " — " + item.getPrice());
                    }
                }

                // Предлагаем пользователю выйти из магазина
                System.out.println("\n0. Выход");

                // Запрашиваем ID предмета для покупки
                System.out.print("Выберите ID предмета: ");

                // Проверяем, что пользователь ввёл целое число
                if (!scanner.hasNextInt()) {

                    // Выводим сообщение об ошибке ввода
                    System.out.println("Введите целое число.");

                    // Удаляем некорректное значение из ввода
                    scanner.next();

                    // Начинаем следующую итерацию цикла
                    continue;
                }

                // Считываем ID выбранного предмета
                int itemId = scanner.nextInt();

                // Если введён 0, завершаем работу магазина
                if (itemId == 0) {
                    System.out.println("Выход из магазина.");
                    break;
                }

                // Передаём данные о покупке в сервис
                try {
                    shopService.processPurchase(playerId, itemId);

                    // Обрабатываем ошибки, связанные с покупкой
                } catch (ShopException exception) {

                    // Выводим сообщение о возникшей ошибке
                    System.out.println("Ошибка: " + exception.getMessage());
                }
            }

        // Обрабатываем ошибки подключения и работы с БД
        } catch (SQLException exception) {

            System.out.println("Ошибка базы данных: " + exception.getMessage());

        } finally {

            // Закрываем объект считывания пользовательского ввода
            scanner.close();
        }
    }
}
