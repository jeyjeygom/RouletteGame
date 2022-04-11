package isel.lic;

import isel.leic.utils.Time;

// Keyboard.
public class KBD {
    public static final char NONE = 0;
    private static final char[] KEYBOARD = {
            '1', '4', '7',
            '*', '2', '5',
            '8', '0', '3',
            '6', '9', '#'
    };
    private static final int OUT_REGISTER_MASK_IN_PORT = 0x0F;
    private static final int DVAL_MASK_IN_PORT = 0x10; // Data Valid.
    private static final int ACK_MASK_OUT_PORT = 0x80; // Acknowledge.
    public static void main(String[] args) {
        HAL.init();
        init();
    }
    public static void init() {
        HAL.clrBits(ACK_MASK_OUT_PORT);
    }
    // Gets the key being pressed. Returns NONE if no key is being pressed.
    public static char getKey() {
        char key = HAL.isBit(DVAL_MASK_IN_PORT) ? KEYBOARD[HAL.readBits(OUT_REGISTER_MASK_IN_PORT)] : NONE;
        if (NONE == key) {
            return NONE;
        }
        HAL.setBits(ACK_MASK_OUT_PORT);
        while (HAL.isBit(DVAL_MASK_IN_PORT));
        HAL.clrBits(ACK_MASK_OUT_PORT);
        return key;
    }
    // Waits for timeout milliseconds for a key press. Returns the pressed key or NONE if no key was pressed during the specified
    // time span.
    public static char waitKey(long timeout) {
        timeout += Time.getTimeInMillis();
        char key;
        while (Time.getTimeInMillis() <= timeout) {
            if (NONE != (key = getKey())) {
                return key;
            }
        }
        return NONE;
    }
}
