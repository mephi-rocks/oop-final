import java.io.Serializable;

// Класс для представления пользователя
public class User implements Serializable {
    private String username; // Имя пользователя
    private String password; // Пароль пользователя
    private Wallet wallet; // Кошелек пользователя для управления финансами

    // Конструктор, инициализирующий пользователя
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    // Геттеры для получения полей объекта
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Wallet getWallet() {
        return wallet;
    }
}