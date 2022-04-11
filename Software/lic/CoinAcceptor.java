package isel.lic;

public class CoinAcceptor {
    private static final int C_MASK_IN_PORT = 0x40; // Coin.
    private static final int A_MASK_OUT_PORT = 0x40; // Accept.
    public static boolean check() {
        return HAL.isBit(C_MASK_IN_PORT);
    }
    public static void accept() {
        HAL.setBits(A_MASK_OUT_PORT);
        while (HAL.isBit(C_MASK_IN_PORT));
        HAL.clrBits(A_MASK_OUT_PORT);
    }
}
