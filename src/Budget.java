import java.io.Serializable;

public class Budget implements Serializable {
    private String category;
    private double limit;
    private double spent;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
        this.spent = 0;
    }

    public void spend(double amount) {
        this.spent += amount;
    }

    public boolean isLimitExceeded() {
        return this.spent > this.limit;
    }

    public double getRemainingBudget() {
        return limit - spent;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Бюджет{" +
                "Категория='" + category + '\'' +
                ", Лимит=" + limit +
                ", Потрачено=" + spent +
                ", Остаток бюджета=" + getRemainingBudget() +
                '}';
    }
}