package isel.lic;

import isel.leic.utils.Time;

// Liquid Cristal Display.
public class LCD {
    public static final int LINES = 2, COLS = 16;
    private static final boolean SERIAL_INTERFACE = true;
    private static final int DATA_MASK_OUT_PORT = 0x0F;
    private static final int RS_MASK_OUT_PORT = 0x10; // Register Select.
    private static final int E_MASK_OUT_PORT = 0x20; // Enable.
    public static void main(String[] args) {
        HAL.init();
        SerialEmitter.init();
        init();
    }
    private static void writeNibbleParallel(boolean rs, int data) {
        if (rs) {
            HAL.setBits(RS_MASK_OUT_PORT);
        } else {
            HAL.clrBits(RS_MASK_OUT_PORT);
        }
        HAL.setBits(E_MASK_OUT_PORT);
        HAL.writeBits(DATA_MASK_OUT_PORT, data);
        HAL.clrBits(E_MASK_OUT_PORT);
    }
    private static void writeNibbleSerial(boolean rs, int data) {
        SerialEmitter.send(SerialEmitter.Destination.LCD, data << 1 | (rs ? 1 : 0));
    }
    private static void writeNibble(boolean rs, int data) {
        if (SERIAL_INTERFACE) {
            writeNibbleSerial(rs, data);
        } else {
            writeNibbleParallel(rs, data);
        }
    }
    private static void writeByte(boolean rs, int data) {
        writeNibble(rs, data >> 4);
        writeNibble(rs, data);
    }
    private static void writeCMD(int data) {
        writeByte(false, data);
    }
    private static void writeDATA(int data) {
        writeByte(true, data);
    }
    public static void init() {
        writeNibble(false, 3);
        Time.sleep(5); // // 4,1 ms until next write.
        writeNibble(false, 3);
        Time.sleep(1); // 100 us until next write.
        writeNibble(false, 3);
        writeNibble(false, 2);
        writeCMD(0x2C);
        writeCMD(0x08);
        writeCMD(0x01);
        writeCMD(0x06);
        writeCMD(0x0F);
        writeCMD(0x0C); // Hide the cursor.
    }
    public static void write(char c) {
        writeDATA(c);
    }
    public static void write(String txt) {
        for (int i = 0; i < txt.length(); i++) {
            writeDATA(txt.charAt(i));
        }
    }
    // Cells: 0-7.
    public static void setCustomChar(int cell, int c[]) {
        writeCMD(0x40 + (cell * 8)); // Set the CGRAM address.
        for (int i : c) {
            writeDATA(i);
        }
    }
    // Lines and columns start at 1.
    public static void cursor(int line, int col) {
        writeCMD(0x80 | (1 == line ? col - 1 : col - 1 + 0x40));
    }
    public static void clear() {
        writeCMD(0x01);
    }
}
