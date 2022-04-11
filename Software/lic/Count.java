package isel.lic;

public class Count {
    private int games, coins;
    public Count() {
    }
    public Count(Count count) {
        games = count.getGames();
        coins = count.getCoins();
    }
    public int getGames() {
        return games;
    }
    public int getCoins() {
        return coins;
    }
    public void setGames(int games) {
        this.games = games;
    }
    public void incrementGames(int inc) {
        games += inc;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
    public void incrementCoins(int inc) {
        coins += inc;
    }
}
