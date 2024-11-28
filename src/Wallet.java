
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

// Класс для управления финансами пользователя
public class Wallet implements Serializable {
    private List<Transaction> transactions;
    private Map<String, Budget> budgets;

    public Wallet() {
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        Budget budget = budgets.get(transaction.getCategory());
        if (budget != null) {
            budget.spend(transaction.getAmount());
        } else {
            budgets.put(transaction.getCategory(), new Budget(transaction.getCategory(), 0));
        }
    }

    public double calculateTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.ДОХОД)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.РАСХОД)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getBalance() {
        return calculateTotalIncome() - calculateTotalExpense();
    }

    public boolean transferBetweenCategories(String fromCategory, String toCategory, double amount) {
        Budget fromBudget = budgets.get(fromCategory);
        Budget toBudget = budgets.get(toCategory);

        if (fromBudget == null || toBudget == null || fromBudget.getRemainingBudget() < amount) {
            return false;
        }

        fromBudget.spend(-amount);
        toBudget.spend(amount);
        return true;
    }

    public void setBudget(String category, double limit) {
        budgets.put(category, new Budget(category, limit));
    }

    public void displaySummary() {
        System.out.println("Баланс: " + getBalance());
        System.out.println("Доход: " + calculateTotalIncome());
        System.out.println("Расходы: " + calculateTotalExpense());


    }

    public Map<String, Budget> getBudgets() {
        return budgets;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void saveToFile(String filename) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        }
    }

    public static Wallet loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Wallet) in.readObject();
        }
    }
}
