package de.elmar_baumann.nb.slclock.util;

/**
 * @author Elmar Baumann
 */
public final class Util {

    public static Integer[] createIntRegionArray(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from " + from + " is greater than to " + to);
        }
        Integer[] values = new Integer[to - from + 1];
        for (int i = from; i <= to; i++) {
            values[i - from] = i;
        }
        return values;
    }

    private Util() {
    }
}
