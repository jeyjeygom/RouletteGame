package isel.lic;

import isel.leic.utils.Time;

import java.util.ArrayList;
import java.util.Random;

public class APP {
    private static final int SQUARE_1_CELL_NUMBER = 0;
    private static final int[] SQUARE_1 = {
            0b00000,
            0b11111,
            0b10001,
            0b10101,
            0b10001,
            0b11111,
            0b00000,
            0b00000
    };
    private static final int SQUARE_2_CELL_NUMBER = 1;
    private static final int[] SQUARE_2 = {
            0b00000,
            0b11111,
            0b10101,
            0b10001,
            0b10101,
            0b11111,
            0b00000,
            0b00000
    };
    private static final int SQUARE_3_CELL_NUMBER = 2;
    private static final int[] SQUARE_3 = {
            0b00000,
            0b11111,
            0b10011,
            0b10101,
            0b11001,
            0b11111,
            0b00000,
            0b00000
    };
    private static final String STATISTICS_FILE_NAME = "Statistics.txt";
    private static final String COUNT_FILE_NAME = "Count.txt";
    private static final int MAX_BALANCE = 99;
    private static final int MAX_BETS_PER_NUMBER = 9;
    private static final String MAINTENANCE_OPTIONS[] = {"0-Stats #-Count", "*-Play 8-ShutD"};
    private static final int MAINTENANCE_OPTIONS_MAX_INDEX = MAINTENANCE_OPTIONS.length - 1;
    private static final int RD_ANIMATION_1_TIMEOUT_MS = 5500; // Go through the roulette display's external segments.
    private static final int RD_ANIMATION_2_TIMEOUT_MS = 750; // Show random numbers on the roulette display.
    private static final int RD_ANIMATION_3_TIMEOUT_MS = 500; // Blink the drawn number on the roulette display.
    private static final int MAINTENANCE_MODE_OPTIONS_TIMEOUT_MS = 2500;
    private static final int GENERIC_TIMEOUT_MS = 5000;
    private static int balance;
    private static int maintenanceOptionsIndex;
    private static boolean betMade;
    private static int betNumbers[];
    private static ArrayList<Statistic> statistics;
    private static Count count;
    public static void main(String[] args) {
        init();
        for (;;) {
            printMainMenu();
            while (!('*' == TUI.getKeyKBD() && balance > 0)) {
                if (CoinAcceptor.check()) {
                    CoinAcceptor.accept();
                    incrementBalance(1);
                    count.incrementCoins(1);
                    printBalance();
                }
                if (M.check()) {
                    maintenanceMode();
                    printMainMenu();
                }
            }
            gameMode();
            save();
        }
    }
    private static void init() {
        HAL.init();
        KBD.init();
        SerialEmitter.init();
        LCD.init();
        RouletteDisplay.init();
        TUI.init();
        TUI.setCustomCharLCD(SQUARE_1_CELL_NUMBER, SQUARE_1);
        TUI.setCustomCharLCD(SQUARE_2_CELL_NUMBER, SQUARE_2);
        TUI.setCustomCharLCD(SQUARE_3_CELL_NUMBER, SQUARE_3);
        ArrayList<String> fileContent;
        statistics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            statistics.add(new Statistic(i));
        }
        fileContent = FileAccess.read(STATISTICS_FILE_NAME);
        if (null != fileContent) {
            loadStatistics(fileContent);
        }
        count = new Count();
        fileContent = FileAccess.read(COUNT_FILE_NAME);
        if (null != fileContent) {
            loadCount(fileContent);
        }
    }
    private static void printMainMenu() {
        TUI.clearLCD();
        TUI.printAlignedStringLCD(1, TUI.Alignment.CENTER, "Roulette Game");
        TUI.printAlignedStringLCD(2, TUI.Alignment.LEFT,
                " 1 " + (char) SQUARE_1_CELL_NUMBER
                + " 2 " + (char) SQUARE_2_CELL_NUMBER
                + " 3 " + (char) SQUARE_3_CELL_NUMBER);
        printBalance();
    }
    private static void incrementBalance(int inc) {
        if (balance + inc <= MAX_BALANCE) {
            balance += inc;
        } else {
            balance = MAX_BALANCE;
        }
    }
    private static void printBalance() {
        TUI.printAlignedStringLCD(2, TUI.Alignment.RIGHT, " $" + balance);
    }
    private static void maintenanceMode() {
        char key;
        while (M.check()) {
            TUI.clearLCD();
            TUI.printAlignedStringLCD(1, TUI.Alignment.CENTER, "On Maintenance");
            TUI.printAlignedStringLCD(2, TUI.Alignment.CENTER, MAINTENANCE_OPTIONS[maintenanceOptionsIndex]);
            nextMaintenanceOption();
            if (TUI.NONE != (key = TUI.waitKeyKBD(MAINTENANCE_MODE_OPTIONS_TIMEOUT_MS))) {
                if ('0' == key) { // Statistics.
                    TUI.clearLCD();
                    Statistic s1 = statistics.get(0), s2 = statistics.get(1);
                    printStatistic(1, s1);
                    printStatistic(2, s2);
                    while (TUI.NONE != (key = TUI.waitKeyKBD(GENERIC_TIMEOUT_MS))) {
                        if ('2' == key) {
                            if (0 != s1.getNumber()) {
                                s2 = s1;
                                s1 = statistics.get(s2.getNumber() - 1);
                                printStatistic(1, s1);
                                printStatistic(2, s2);
                            }
                        } else if ('8' == key) {
                            if (9 != s2.getNumber()) {
                                s1 = s2;
                                s2 = statistics.get(s1.getNumber() + 1);
                                printStatistic(1, s1);
                                printStatistic(2, s2);
                            }
                        }
                    }
                } else if ('#' == key) { // Count.
                    TUI.clearLCD();
                    TUI.printAlignedStringLCD(1, TUI.Alignment.LEFT, "Games:" + count.getGames());
                    TUI.printAlignedStringLCD(2, TUI.Alignment.LEFT, "Coins:" + count.getCoins());
                    TUI.waitKeyKBD(GENERIC_TIMEOUT_MS);
                } else if ('*' == key) { // Play.
                    int balanceBak = balance;
                    ArrayList<Statistic> statisticsBak = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        statisticsBak.add(new Statistic(statistics.get(i)));
                    }
                    Count countBak = new Count(count);
                    balance = MAX_BALANCE;
                    gameMode();
                    balance = balanceBak;
                    statistics = statisticsBak;
                    count = countBak;
                } else if ('8' == key) { // Shut Down.
                    TUI.clearLCD();
                    TUI.printAlignedStringLCD(1, TUI.Alignment.CENTER, "Shut Down");
                    TUI.printAlignedStringLCD(2, TUI.Alignment.CENTER, "5-Yes Other-No");
                    if ('5' == TUI.waitKeyKBD(GENERIC_TIMEOUT_MS)) {
                        save();
                        System.exit(0);
                    }
                }
            }
        }
    }
    private static void nextMaintenanceOption() {
        if (MAINTENANCE_OPTIONS_MAX_INDEX == maintenanceOptionsIndex) {
            maintenanceOptionsIndex = 0;
        } else {
            maintenanceOptionsIndex++;
        }
    }
    private static void printStatistic(int line, Statistic s) {
        TUI.printAlignedStringLCD(line, TUI.Alignment.LEFT, s.getNumber() + ": -> " + s.getCount() + " $:" + s.getAmount());
    }
    private static void gameMode() {
        TUI.clearLCD();
        TUI.printAlignedStringLCD(2, TUI.Alignment.LEFT, "0123456789");
        printBalance();
        betMade = false;
        betNumbers = new int[10];
        long timeout = 0;
        char key;
        while (0 == timeout || Time.getTimeInMillis() < timeout) {
            key = TUI.getKeyKBD();
            if (timeout > 0) {
                RouletteDisplay.animation();
            } else if ('#' == key && betMade) {
                RouletteDisplay.startAnimation();
                timeout = Time.getTimeInMillis() + RD_ANIMATION_1_TIMEOUT_MS;
            }
            if (key >= '0' && key <= '9'
                    && betNumbers[key - '0'] < MAX_BETS_PER_NUMBER
                    && balance > 0) {
                bet(key - '0');
            }
        }
        Random random = new Random();
        randomNumbersAnimation(random, 4, RD_ANIMATION_2_TIMEOUT_MS);
        int randomNumber = random.nextInt(10);
        RouletteDisplay.showNumber(randomNumber);
        if (betNumbers[randomNumber] > 0) { // Win.
            int winVal = betNumbers[randomNumber] << 1; // Times 2.
            incrementBalance(winVal);
            statistics.get(randomNumber).incrementAmount(winVal);
            TUI.printAlignedStringLCD(1, TUI.Alignment.RIGHT, "W$" + winVal);
        } else { // Lose.
            int loseVal = 0;
            for (int i = 0; i < 10; i++) {
                loseVal += betNumbers[i];
            }
            TUI.printAlignedStringLCD(1, TUI.Alignment.RIGHT, "L$" + loseVal);
        }
        blink(10, RD_ANIMATION_3_TIMEOUT_MS, randomNumber);
        statistics.get(randomNumber).incrementCount(1);
        count.incrementGames(1);
        RouletteDisplay.clear();
    }
    private static void bet(int i) {
        betNumbers[i]++;
        betMade = true;
        balance--;
        TUI.printCharLCD(1, i + 1, (char) (betNumbers[i] + '0'));
        printBalance();
    }
    private static void randomNumbersAnimation(Random random, int count, long initialTimeout) {
        int randomNumber;
        for (int i = 0; i < count; i++) {
            randomNumber = random.nextInt(10);
            RouletteDisplay.showNumber(randomNumber);
            Time.sleep(initialTimeout);
            initialTimeout *= 1.5f;
        }
    }
    private static void blink(int count, long timeout, int number) {
        for (int i = 0; i < count; i++) {
            RouletteDisplay.clear();
            Time.sleep(timeout);
            RouletteDisplay.showNumber(number);
            Time.sleep(timeout);
        }
    }
    private static void loadStatistics(ArrayList<String> fileContent) {
        String values[];
        Statistic statistic;
        for (int i = 0; i < 10; i++) {
            values = fileContent.get(i).split(";");
            statistic = statistics.get(i);
            statistic.setCount(Integer.valueOf(values[1]));
            statistic.setAmount(Integer.valueOf(values[2]));
        }
    }
    private static void loadCount(ArrayList<String> fileContent) {
        count.setGames(Integer.valueOf(fileContent.get(0)));
        count.setCoins(Integer.valueOf(fileContent.get(1)));
    }
    private static void save() {
        ArrayList<String> fileContent = new ArrayList<>();
        for (Statistic s : statistics) {
            fileContent.add(s.getNumber() + ";" + s.getCount() + ";" + s.getAmount());
        }
        FileAccess.write(STATISTICS_FILE_NAME, fileContent);
        fileContent = new ArrayList<>();
        fileContent.add(String.valueOf(count.getGames()));
        fileContent.add(String.valueOf(count.getCoins()));
        FileAccess.write(COUNT_FILE_NAME, fileContent);
    }
}
