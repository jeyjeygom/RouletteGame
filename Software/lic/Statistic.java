package isel.lic;

public class Statistic {
    private int number; // The number, 0-9.
    private int count; // How many times the number was drawn.
    private int amount; // The amount won by betting on the number.
    public Statistic(int number) {
        this.number = number;
    }
    public Statistic(Statistic statistic) {
        number = statistic.getNumber();
        count = statistic.getCount();
        amount = statistic.getAmount();
    }
    public int getNumber() {
        return number;
    }
    public int getCount() {
        return count;
    }
    public int getAmount() {
        return amount;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void incrementCount(int inc) {
        count += inc;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void incrementAmount(int inc) {
        amount += inc;
    }
}
