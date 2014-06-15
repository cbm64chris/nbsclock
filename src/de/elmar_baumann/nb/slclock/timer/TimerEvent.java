package de.elmar_baumann.nb.slclock.timer;

import de.elmar_baumann.nb.slclock.util.TimeUtil;
import java.text.Collator;
import java.util.Comparator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.netbeans.api.annotations.common.NullAllowed;

/**
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class TimerEvent {

    @XmlElement(name = "seconds")
    private int seconds;

    @XmlElement(name = "remainingseconds")
    private long remainingSeconds;

    private long startTimeInNanos;

    @XmlElement(name = "displayname")
    private String displayName;

    @XmlElement(name = "verbose")
    private boolean verbose;

    @XmlElement(name = "sound")
    private boolean sound;

    private boolean run;

    @XmlElement(name = "persistent")
    private boolean persistent;

    public TimerEvent() {
    }

    public TimerEvent(TimerEvent other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        this.seconds = other.seconds;
        this.displayName = other.displayName;
        this.sound = other.sound;
        this.verbose = other.verbose;
        this.run = other.run;
        this.persistent = other.persistent;
        this.remainingSeconds = other.remainingSeconds;
        this.startTimeInNanos = other.startTimeInNanos;
    }

    /**
     * @return zero if not set, else set seconds
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * @param seconds seconds greater than zero
     */
    public void setSeconds(int seconds) {
        if (seconds < 1) {
            throw new IllegalArgumentException("Seconds has to be greater than zero: " + seconds);
        }
        this.seconds = seconds;
    }

    /**
     * @return Default: null
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NullAllowed String displayName) {
        this.displayName = displayName;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public long getStartTimeInNanos() {
        return startTimeInNanos;
    }

    public void setStartTimeInNanos(long startTimeInNanos) {
        this.startTimeInNanos = startTimeInNanos;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return displayName == null
                ? 47 * hash + this.seconds
                : displayName.toLowerCase().hashCode();
    }

    /**
     * @param obj
     * @return true if the display name of the other timer event is equals
     *         to the display name of this timer event ignoring the case. If
     *         the display name of both events is null, the seconds will be
     *         compared.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimerEvent)) {
            return false;
        }
        TimerEvent other = (TimerEvent) obj;
        boolean oneHasDisplayNameOtherNot =
                   (hasDisplayName(this) && !hasDisplayName(other))
                || (!hasDisplayName(this) && hasDisplayName(other));
        return oneHasDisplayNameOtherNot
                ? false
                : hasDisplayName(this) && hasDisplayName(other)
                ? displayName.compareToIgnoreCase(other.displayName) == 0
                : this.seconds == other.seconds;
    }

    private boolean hasDisplayName(TimerEvent event) {
        if (event == null) {
            return false;
        }
        String dn = event.getDisplayName();
        return dn != null && !dn.trim().isEmpty();
    }

    public int getHours() {
        return TimeUtil.getHoursOfSeconds(seconds);
    }

    public int getMinutesPerHour() {
        return TimeUtil.getMinutesPerHourOfSeconds(seconds);
    }

    public int getSecondsPerMinute() {
        return TimeUtil.getSecondsPerMinuteOfSeconds(seconds);
    }

    public static String formatTimeForGui(int seconds) {
        int hours = TimeUtil.getHoursOfSeconds(seconds);
        String hoursString = hours > 0 ? String.format("%02d", hours) : "";
        int minutesPerHour = TimeUtil.getMinutesPerHourOfSeconds(seconds);
        String minutesString = minutesPerHour > 0 && hours > 0
                ? String.format(":02d:", minutesPerHour)
                : minutesPerHour > 0
                ? String.format("%02d:", minutesPerHour)
                : "0:";
        int secondsPerMinute = TimeUtil.getSecondsPerMinuteOfSeconds(seconds);
        return String.format("%s%s%02d", hoursString, minutesString, secondsPerMinute);
    }

    public String getTimeForGui() {
        return formatTimeForGui(seconds);
    }

    private String getDisplayNameGui() {
        String name = displayName == null ? "" : displayName;
        return name + " " + getTimeForGui();
    }

    @Override
    public String toString() {
        return getDisplayNameGui();
    }

    static class TimerEventCmpAsc implements Comparator<TimerEvent> {

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(TimerEvent o1, TimerEvent o2) {
            String displayName1 = o1.getDisplayName();
            String displayName2 = o2.getDisplayName();
            return displayName1 != null && displayName2 != null
                    ? collator.compare(displayName1, displayName2)
                    : displayName1 != null && displayName2 == null
                    ? 1
                    : displayName1 == null && displayName2 != null
                    ? -1
                    : o1.seconds == o2.seconds
                    ? 0
                    : o1.seconds > o2.seconds
                    ? 1
                    : -1;
}
    }
}
