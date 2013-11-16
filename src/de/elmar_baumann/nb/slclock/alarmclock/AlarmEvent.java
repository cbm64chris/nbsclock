package de.elmar_baumann.nb.slclock.alarmclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.netbeans.api.annotations.common.NullAllowed;

/**
 * @author Elmar Baumann
 */
public final class AlarmEvent {

    private final int hour;
    private final int minute;
    private final Collection<DayOfWeek> daysOfWeek;
    private String displayName;
    private boolean run;

    public AlarmEvent(int hour, int minute, DayOfWeek... daysOfWeek) {
        if (hour < 0 || hour > 24) {
            throw new IllegalArgumentException("Invalid hour (allowed range 0 .. 24): " + hour);
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute (allowed range 0 .. 59): " + minute);
        }
        this.hour = hour;
        this.minute = minute;
        this.daysOfWeek = daysOfWeek == null || daysOfWeek.length == 0
                ? EnumSet.noneOf(DayOfWeek.class)
                : EnumSet.copyOf(Arrays.asList(daysOfWeek));
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Collection<DayOfWeek> getDaysOfWeek() {
        return Collections.unmodifiableCollection(daysOfWeek);
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void setDisplayName(@NullAllowed String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        StringBuilder sb = new StringBuilder(df.format(cal.getTime()));
        List<DayOfWeek> sortedDaysOfWeek = new LinkedList<>(daysOfWeek);
        Collections.sort(sortedDaysOfWeek, new DayOfWeek.CalendarDayOfWeekCmpAsc());
        boolean first = true;
        for (DayOfWeek dayOfWeek : sortedDaysOfWeek) {
            sb.append(first ? " " : ", ")
              .append(dayOfWeek.getGuiString());
            first = false;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.hour;
        hash = 83 * hash + this.minute;
        hash = 83 * hash + Objects.hashCode(this.daysOfWeek);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
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
     * May throw Exceptions!
     * @param date
     * @return
     */
    public Date createAlarmDateNextTo(Date date) {
        if (date == null) {
            throw new NullPointerException("date == null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dateHour = cal.get(Calendar.HOUR_OF_DAY);
        int dateMinute = cal.get(Calendar.MINUTE);
        DayOfWeek dateDayOfWeek = DayOfWeek.parseDate(date);
        boolean hmEqLater = hour >= dateHour || hour == dateHour && minute >= dateMinute;
        boolean isAtSameDay = daysOfWeek.isEmpty() && hmEqLater ||
                daysOfWeek.contains(dateDayOfWeek) && hmEqLater;
        if (isAtSameDay) {
            setTime(cal);
            return cal.getTime();
        }
        boolean isNextDay = daysOfWeek.isEmpty()  && !hmEqLater; // next day with content within daysOfWeek is treated below
        if (isNextDay) {
            setTime(cal);
            cal.add(Calendar.DAY_OF_WEEK, 1);
            return cal.getTime();
        }
        boolean isNextWeek = daysOfWeek.size() == 1 && daysOfWeek.contains(dateDayOfWeek);
        if (isNextWeek) {
            setTime(cal);
            cal.add(Calendar.DAY_OF_WEEK, 7);
            return cal.getTime();
        }
        int calDateDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        List<DayOfWeek> sortedDaysOfWeek = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
        Collections.sort(sortedDaysOfWeek, new DayOfWeek.CalendarDayOfWeekCmpAsc());
        for (DayOfWeek dayOfWeek : sortedDaysOfWeek) {
            if (daysOfWeek.contains(dayOfWeek) && dayOfWeek.getForCalendar() > calDateDayOfWeek) {
                setTime(cal);
                cal.add(Calendar.DAY_OF_WEEK, dayOfWeek.getForCalendar() - calDateDayOfWeek);
                return cal.getTime();
            }
        }
        throw new IllegalStateException("Date not calculated for: " + date);
    }

    private void setTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * May throw exceptions!
     * @param s
     * @return
     */
    public static Collection<AlarmEvent> fromPersistentString(@NullAllowed String s) {
        if (s == null || s.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] eventTokens = s.split(";");
        Collection<AlarmEvent> events = new ArrayList<>(eventTokens.length);
        for (String eventToken : eventTokens) {
            String[] timeRunDaysOfWeekTokens = eventToken.split(" ");
            String[] timeTokens = timeRunDaysOfWeekTokens[0].split(":");
            int hours = Integer.valueOf(timeTokens[0]);
            int minutes = Integer.valueOf(timeTokens[1]);
            String runToken = timeRunDaysOfWeekTokens[1];
            boolean run = "true".equals(runToken);
            Collection<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
            if (timeRunDaysOfWeekTokens.length == 3) {
                for (String dowToken : timeRunDaysOfWeekTokens[2].split(",")) {
                    daysOfWeek.add(DayOfWeek.valueOf(dowToken));
                }
            }
            AlarmEvent alarmEvent = new AlarmEvent(hours, minutes, daysOfWeek.toArray(new DayOfWeek[daysOfWeek.size()]));
            alarmEvent.setRun(run);
            events.add(alarmEvent);
        }
        return events;
    }

    /**
     * @param events
     * @return "hour:minute run dayOfWeek1,dayOfWeek2,...;hour:minute run dayOfWeek1,dayOfWeek2,...;..."
     */
    public static String toPersistentString(Collection<AlarmEvent> events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (AlarmEvent event : events) {
            sb.append(first ? "" : ";")
              .append(event.toPersistentString());
            first = false;
        }
        return sb.toString();
    }

    // "hour:minute run dayOfWeek1,dayOfWeek2,..."
    private String toPersistentString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(hour))
          .append(":")
          .append(String.valueOf(minute))
          .append(" ")
          .append(run ? "true" : "false");
        boolean first = true;
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            sb.append(first ? " " : ",")
              .append(dayOfWeek.name());
            first = false;
        }
        return sb.toString();
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
