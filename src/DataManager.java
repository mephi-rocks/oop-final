import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private static final String USERS_FILE = "users.dat";

    public static void saveUsersData(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, User> loadUsersData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Файл данных не найден или не может быть загружен. Создается новая база данных.");
            return new HashMap<>();
        }
    }
}