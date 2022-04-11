package isel.lic;

import isel.leic.utils.Time;

public class RouletteDisplay {
    private static final int CLEAR_DISPLAY = 0x17;
    private static final char ANIMATION[] = {0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
    private static final int ANIMATION_MAX_INDEX = ANIMATION.length - 1;
    private static final long ANIMATION_TIMEOUT_MS = 300;
    private static final boolean SERIAL_INTERFACE = true;
    private static final int DATA_MASK_OUT_PORT = 0x1F;
    private static final int WR_MASK_OUT_PORT = 0x40; // Write.
    private static int animationIndex;
    private static long timeout;
    public static void main(String[] args) {
        HAL.init();
        SerialEmitter.init();
        init();
    }
    public static void init() {
        clear();
    }
    public static void clear() {
        showNumber(CLEAR_DISPLAY);
    }
    private static void showNumberParallel(int number) {
        HAL.writeBits(DATA_MASK_OUT_PORT, number);
        HAL.setBits(WR_MASK_OUT_PORT);
        HAL.clrBits(WR_MASK_OUT_PORT);
    }
    private static void showNumberSerial(int number) {
        SerialEmitter.send(SerialEmitter.Destination.RDisplay, number);
    }
    public static void showNumber(int number) {
        if (SERIAL_INTERFACE) {
            showNumberSerial(number);
        } else {
            showNumberParallel(number);
        }
    }
    public static void startAnimation() {
        animationIndex = 0;
        showNumber(ANIMATION[animationIndex]);
        timeout = Time.getTimeInMillis() + ANIMATION_TIMEOUT_MS;
    }
    public static void animation() {
        if (timeout < Time.getTimeInMillis()) {
            animationIndex = animationIndex < ANIMATION_MAX_INDEX ? animationIndex + 1 : 0;
            showNumber(ANIMATION[animationIndex]);
            timeout += ANIMATION_TIMEOUT_MS;
        }
    }
}
