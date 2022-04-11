package isel.lic;

import isel.leic.UsbPort;

// Hardware Abstraction Layer.
public class HAL {
    private static int outputPort; // The value on the output port.
    public static void main(String[] args) {
        init();
    }
    public static void init() {
        outputPort = 0;
        out(outputPort);
    }
    // Checks, on the input port, if a specific bit is set.
    public static boolean isBit(int mask) {
        return 0 != readBits(mask);
    }
    // Reads, from the input port, the bits specified by a mask.
    public static int readBits(int mask) {
        return in() & mask;
    }
    // Writes, to the output port, on the bits specified by a mask (while keeping the others), a value.
    public static void writeBits(int mask, int value) {
        outputPort &= ~mask;
        outputPort |= value & mask;
        setBits(outputPort);
        clrBits(~outputPort);
    }
    // Sets, on the output port, the bits specified by a mask (and keeps the others).
    public static void setBits(int mask) {
        outputPort |= mask;
        out(outputPort);
    }
    // Resets, on the output port, the bits specified by a mask (and keeps the others).
    public static void clrBits(int mask) {
        outputPort &= ~mask;
        out(outputPort);
    }
    private static int in() {
        return ~UsbPort.in(); // UsbPort.in() reads from the input port.
    }
    private static void out(int value) {
        UsbPort.out(~value); // UsbPort.out() writes to the output port.
    }
}
