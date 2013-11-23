package de.elmar_baumann.nb.slclock.timer;

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

    private int seconds;
    private String displayName;
    private boolean verbose;
    private boolean sound;
    private boolean run;
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
    }

    /**
     * @return zero if not set, else set seconds
     */
    @XmlElement(name = "seconds")
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
    @XmlElement(name = "displayName")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NullAllowed String displayName) {
        this.displayName = displayName;
    }

    @XmlElement(name = "verbose")
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isSound() {
        return sound;
    }

    @XmlElement(name = "sound")
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
        boolean oneDisplayNameIsNullOtherNot =
                   (displayName == null && other.displayName != null)
                || (displayName != null && other.displayName == null);
        return oneDisplayNameIsNullOtherNot
                ? false
                : displayName != null && other.displayName != null
                ? displayName.compareToIgnoreCase(other.displayName) == 0
                : this.seconds == other.seconds;
    }

    public int getHours() {
        return seconds / 3600;
    }

    public int getMinutesPerHour() {
        return (seconds - getHours() * 3600) / 60;
    }

    public int getSecondsPerMinute() {
        int spHspM = 3600 * getHours() + 60 * getMinutesPerHour();
        return spHspM > 0
                ? seconds % spHspM
                : seconds;
    }

    private String getDisplayNameGui() {
        String name = displayName == null ? "" : displayName;
        int hours = getHours();
        String hoursString = hours > 0 ? String.format("%02d", hours) : "";
        int minutesPerHour = getMinutesPerHour();
        String minutesString = minutesPerHour > 0 && hours > 0
                ? String.format(":02d:", minutesPerHour)
                : minutesPerHour > 0
                ? String.format("%02d:", minutesPerHour)
                : "0:";
        int secondsPerMinute = getSecondsPerMinute();
        return String.format("%s%s%s%02d", name, hoursString, minutesString, secondsPerMinute);
    }

    @Override
    public String toString() {
        return getDisplayNameGui();
    }
}
