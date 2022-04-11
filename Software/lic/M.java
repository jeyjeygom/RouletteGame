package isel.lic;

// Maintenance.
public class M {
    private static final int M_MASK_IN_PORT = 0x20; // Maintenance.
    public static boolean check() {
        return HAL.isBit(M_MASK_IN_PORT);
    }
}
