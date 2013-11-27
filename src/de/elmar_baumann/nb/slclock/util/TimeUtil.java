package de.elmar_baumann.nb.slclock.util;

/**
 * @author Elmar Baumann
 */
public final class TimeUtil {

    public static int getHoursOfSeconds(int seconds) {
        return seconds / 3600;
    }

    public static int getMinutesPerHourOfSeconds(int seconds) {
        return (seconds - getHoursOfSeconds(seconds) * 3600) / 60;
    }

    public static int getSecondsPerMinuteOfSeconds(int seconds) {
        int spHspM = 3600 * getHoursOfSeconds(seconds) + 60 * getMinutesPerHourOfSeconds(seconds);
        return spHspM > 0 ? seconds % spHspM : seconds;
    }

    private TimeUtil() {
    }
}
