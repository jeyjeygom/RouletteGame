package isel.lic;

// Text User Interface.
public class TUI {
    public enum Alignment {
        CENTER,
        LEFT,
        RIGHT
    }
    public static final char NONE = KBD.NONE;
    public static void main(String[] args) {
        HAL.init();
        KBD.init();
        LCD.init();
        init();
    }
    public static void init() { clearLCD(); }
    public static void clearLCD() { LCD.clear(); }
    // Lines start at 1.
    public static void printAlignedStringLCD(int line, Alignment alignment, String txt) {
        if (Alignment.CENTER == alignment) {
            LCD.cursor(line, (LCD.COLS - txt.length()) / 2 + 1);
        } else if (Alignment.LEFT == alignment) {
            LCD.cursor(line, 1);
        } else if (Alignment.RIGHT == alignment) {
            LCD.cursor(line, LCD.COLS - txt.length() + 1);
        }
        LCD.write(txt);
    }
    // Lines and columns start at 1.
    public static void printCharLCD(int line, int col, char c) {
        LCD.cursor(line, col);
        LCD.write(c);
    }
    // Cells: 0-7.
    public static void setCustomCharLCD(int cell, int c[]) {
        LCD.setCustomChar(cell, c);
    }
    public static char waitKeyKBD(long timeout) {
        return KBD.waitKey(timeout);
    }
    public static char getKeyKBD() {
        return KBD.getKey();
    }
}
