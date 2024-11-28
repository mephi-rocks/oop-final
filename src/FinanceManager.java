import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceManager {
    private Map<String, User> users;
    private User currentUser;

    public FinanceManager() {
        System.out.println("Автоматическая загрузка данных из файла 'users.dat'...");
        this.users = DataManager.loadUsersData();
        System.out.println("Данные пользователей успешно загружены.");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в систему управления финансами!");

        while (true) {
            System.out.println("\nДоступные команды: login, switch, add, summary, setbudget, transfer, balance, stats, help, save, load, exit");
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "login":
                    authorizeUser(scanner);
                    break;
                case "switch":
                    switchUser(scanner);
                    break;
                case "add":
                    if (currentUser != null) addTransaction(scanner);
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "summary":
                    if (currentUser != null) currentUser.getWallet().displaySummary();
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "setbudget":
                    if (currentUser != null) setBudget(scanner);
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "transfer":
                    if (currentUser != null) transferFunds(scanner);
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "balance":
                    if (currentUser != null) displayBalance();
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "stats":
                    if (currentUser != null) displayCategoryStats();
                    else System.out.println("Сначала выполните вход (login).");
                    break;
                case "help":
                    displayHelp();
                    break;
                case "save":
                    DataManager.saveUsersData(users);
                    System.out.println("Данные были сохранены.");
                    break;
                case "load":
                    users = DataManager.loadUsersData();
                    System.out.println("Данные были загружены.");
                    break;
                case "exit":
                    DataManager.saveUsersData(users);
                    System.out.println("Данные сохранены. Выход из программы.");
                    return;
                default:
                    System.out.println("Неизвестная команда. Используйте команду 'help' для получения списка доступных команд.");
                    break;
            }
        }
    }

    private void authorizeUser(Scanner scanner) {
        System.out.println("Введите ваш логин:");
        String username = scanner.nextLine();
        System.out.println("Введите ваш пароль:");
        String password = scanner.nextLine();

        currentUser = users.get(username);
        if (currentUser == null) {
            System.out.println("Пользователь не найден, создаю нового.");
            currentUser = new User(username, password);
            users.put(username, currentUser);
        } else if (!currentUser.getPassword().equals(password)) {
            System.out.println("Неверный пароль. Повторите попытку.");
            currentUser = null;
        } else {
            System.out.println("Успешный вход.");
        }
    }

    private void switchUser(Scanner scanner) {
        if (users.isEmpty()) {
            System.out.println("Нет доступных пользователей. Пожалуйста, выполните вход сначала.");
            return;
        }
        System.out.println("Введите имя пользователя, на которого переключиться:");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            currentUser = users.get(username);
            System.out.println("Переключен на пользователя " + username);
        } else {
            System.out.println("Пользователь не найден.");
        }
    }

    private void addTransaction(Scanner scanner) {
        System.out.println("Введите категорию:");
        String category = scanner.nextLine();
        System.out.println("Введите сумму:");
        double amount = Double.parseDouble(scanner.nextLine());
        try {
            System.out.println("Введите тип транзакции (ДОХОД/РАСХОД):");
            TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());
            Transaction transaction = new Transaction(category, amount, type);
            Wallet wallet = currentUser.getWallet();
            wallet.addTransaction(transaction);

            Budget budget = wallet.getBudgets().get(category);
            if (budget != null && budget.isLimitExceeded()) {
                NotificationService.notifyWarning("Вы превысили бюджет по категории " + category);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный тип транзакции. Пожалуйста введите 'ДОХОД' или 'РАСХОД'.");
            return;
        }
    }

    private void setBudget(Scanner scanner) {
        System.out.println("Введите категорию для бюджета:");
        String category = scanner.nextLine();
        System.out.println("Введите лимит бюджета:");
        double limit = Double.parseDouble(scanner.nextLine());

        currentUser.getWallet().setBudget(category, limit);
        System.out.println("Бюджет установлен для " + category);
    }

    private void transferFunds(Scanner scanner) {
        System.out.println("Введите категорию, с которой хотите перевести:");
        String fromCategory = scanner.nextLine();
        System.out.println("Введите категорию, в которую хотите перевести:");
        String toCategory = scanner.nextLine();
        System.out.println("Введите сумму перевода:");
        double amount = Double.parseDouble(scanner.nextLine());

        if (currentUser.getWallet().transferBetweenCategories(fromCategory, toCategory, amount)) {
            System.out.println("Средства успешно перенесены.");
        } else {
            System.out.println("Ошибка при переносе средств. Проверьте корректность данных.");
        }
    }

    private void displayBalance() {
        double balance = currentUser.getWallet().getBalance();
        System.out.println("Текущий баланс: " + balance);
    }

    private void displayCategoryStats() {
        System.out.println("Статистика по категориям:");

        // Создаем карту для подсчета потраченных средств по категориям на основе транзакций
        Map<String, Double> expensesByCategory = new HashMap<>();

        // Перебираем все транзакции и суммируем расходы по категориям
        for (Transaction transaction : currentUser.getWallet().getTransactions()) {
            if (transaction.getType() == TransactionType.РАСХОД) {
                expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
            }
        }

        // Отображаем данные по каждой категории
        currentUser.getWallet().getBudgets().forEach((category, budget) -> {
            double spent = expensesByCategory.getOrDefault(category, 0.0);
            System.out.println("Категория: " + category +
                    ", Затрачено: " + spent +
                    ", Лимит: " + budget.getLimit() +
                    ", Остаток: " + (budget.getLimit() - spent) +
                    ", Превышение лимита: " + (spent > budget.getLimit() ? "Да" : "Нет"));
        });
    }

    private void displayHelp() {
        System.out.println("Команды приложения:");
        System.out.println("login     - Войти в систему или создать нового пользователя");
        System.out.println("switch    - Переключиться на другого пользователя");
        System.out.println("add       - Добавить новую транзакцию");
        System.out.println("summary   - Показать сводку по финансам");
        System.out.println("setbudget - Установить бюджет на категорию");
        System.out.println("transfer  - Перенести средства между категориями");
        System.out.println("balance   - Показать текущий баланс");
        System.out.println("stats     - Показать статистику по категориям");
        System.out.println("save      - Сохранить данные в файл");
        System.out.println("load      - Загрузить данные из файла");
        System.out.println("help      - Вывести это сообщение");
        System.out.println("exit      - Выйти из программы");
    }

    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.start();
    }
}