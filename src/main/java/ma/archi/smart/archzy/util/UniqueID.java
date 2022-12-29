package ma.archi.smart.archzy.util;

public class UniqueID {
    private static long current = System.currentTimeMillis();

    private UniqueID() {
    }

    static public synchronized long get() {
        return current++;
    }
}