package de.elmar_baumann.nb.slclock.alarmclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class AlarmEvent {

    private int hour;
    private int minute;
    private Collection<DayOfWeek> daysOfWeek = new ArrayList<>();
    private String displayName;
    private boolean run;
    private boolean sound;

    public AlarmEvent() {
        this(0, 0);
    }

    public AlarmEvent(int hour, int minute, DayOfWeek... daysOfWeek) {
        setHour(hour);
        setMinute(minute);
        setDaysOfWeek(Arrays.asList(daysOfWeek));
    }

    public AlarmEvent(AlarmEvent other) {
        this.hour = other.hour;
        this.minute = other.minute;
        this.daysOfWeek = new ArrayList<>(other.daysOfWeek);
        this.displayName = other.displayName;
        this.run = other.run;
        this.sound = other.sound;
    }

    @XmlElement(name = "hour")
    public synchronized int getHour() {
        return hour;
    }

    public synchronized void setHour(int hour) {
        if (hour < 0 || hour > 24) {
            throw new IllegalArgumentException("Invalid hour (allowed range 0 .. 24): " + hour);
        }
        this.hour = hour;
    }

    @XmlElement(name = "minute")
    public synchronized int getMinute() {
        return minute;
    }

    public synchronized void setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute (allowed range 0 .. 59): " + minute);
        }
        this.minute = minute;
    }

    @XmlElement(name = "dayofweek")
    public synchronized Collection<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek; // JAXB: modifiable
    }

    public synchronized void setDaysOfWeek(Collection<DayOfWeek> daysOfWeek) {
        if (daysOfWeek == null) {
            throw new NullPointerException("daysOfWeek == null");
    }
        this.daysOfWeek = new ArrayList<>(daysOfWeek);
    }

    public synchronized boolean isRepeatable() {
        return !daysOfWeek.isEmpty();
    }

    public synchronized boolean isAlarm(long timeInMillis) {
        if (!run) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        int calHour = cal.get(Calendar.HOUR_OF_DAY);
        int calMinute = cal.get(Calendar.MINUTE);
        final boolean hourAnMinuteEquals = hour == calHour && minute == calMinute;
        return isRepeatable()
                ? hourAnMinuteEquals && containsDayOfWeek(timeInMillis)
                : hourAnMinuteEquals;
    }

    private synchronized boolean containsDayOfWeek(long timeInMillis) {
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            if (dayOfWeek.isDayOfWeek(timeInMillis)) {
                return true;
            }
        }
        return false;
    }

    @XmlElement(name = "run")
    public synchronized boolean isRun() {
        return run;
    }

    public synchronized void setRun(boolean run) {
        this.run = run;
    }

    @XmlElement(name = "sound")
    public synchronized boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @XmlElement(name = "displayname")
    public synchronized String getDisplayName() {
            return displayName;
        }

    public synchronized String getTimeForGui() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        StringBuilder sb = new StringBuilder(df.format(cal.getTime()));
        List<DayOfWeek> sortedDaysOfWeek = new LinkedList<>(daysOfWeek);
        Collections.sort(sortedDaysOfWeek, new DayOfWeek.CalendarDayOfWeekCmpAsc());
        boolean first = true;
        for (DayOfWeek dayOfWeek : sortedDaysOfWeek) {
            sb.append(first ? " " : " ")
              .append(dayOfWeek.getShortGuiString());
            first = false;
        }
        return sb.toString();
    }

    @Override
    public synchronized String toString() {
        String dn = getDisplayName();
        return getTimeForGui() + dn == null
                ? ""
                : dn;
    }

    @Override
    public synchronized int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.hour;
        hash = 83 * hash + this.minute;
        hash = 83 * hash + daysOfWeekHashCode();
        return hash;
    }

    private synchronized int daysOfWeekHashCode() {
        int hash = 3;
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            hash += dayOfWeek.hashCode();
        }
        return hash;
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AlarmEvent)) {
            return false;
        }
        AlarmEvent other = (AlarmEvent) obj;
        return hour == other.hour
                && minute == other.minute
                && daysOfWeek.size() == other.daysOfWeek.size()
                && daysOfWeek.containsAll(other.daysOfWeek);
    }

    /**
     * Ignores days of week.
     */
    public static final class AlarmEventCmpAsc implements Comparator<AlarmEvent> {

        @Override
        public int compare(AlarmEvent o1, AlarmEvent o2) {
            return o1.hour == o2.hour && o1.minute == o2.minute
                    ? 0
                    : o1.hour == o2.hour && o1.minute > o2.minute
                    ? 1
                    : o1.hour > o2.hour
                    ? 1
                    : -1;
        }
    }
}
