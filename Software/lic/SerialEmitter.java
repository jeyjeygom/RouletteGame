package isel.lic;

public class SerialEmitter {
    public enum Destination {RDisplay, LCD}
    private static final int SS_MASK_OUT_PORT = 0x01; // SOC Select.
    private static final int SCLK_MASK_OUT_PORT = 0x02; // SOC Clock.
    private static final int SDX_MASK_OUT_PORT = 0x04; // Send.
    private static final int DATA_SIZE_BITS = 5;
    public static void main(String[] args) {
        HAL.init();
        init();
    }
    public static void init() {
        HAL.clrBits(SS_MASK_OUT_PORT);
        HAL.clrBits(SCLK_MASK_OUT_PORT);
    }
    public static void send(Destination addr, int data) {
        HAL.setBits(SS_MASK_OUT_PORT);
        int bit = Destination.LCD == addr ? 1 : 0;
        int parity = bit ^ 1; // 1 for odd parity, 0 for even.
        sendBit(bit);
        for (int i = 0; i < DATA_SIZE_BITS; i++) {
            bit = data & 1;
            data >>= 1;
            parity ^= bit;
            sendBit(bit);
        }
        sendBit(parity);
        HAL.clrBits(SS_MASK_OUT_PORT);
    }
    private static void sendBit(int bit) {
        if (1 == bit) {
            HAL.setBits(SDX_MASK_OUT_PORT);
        } else {
            HAL.clrBits(SDX_MASK_OUT_PORT);
        }
        HAL.setBits(SCLK_MASK_OUT_PORT);
        HAL.clrBits(SCLK_MASK_OUT_PORT);
    }
}
