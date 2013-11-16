package de.elmar_baumann.nb.slclock.alarmclock;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Elmar Baumann
 */
public enum DayOfWeek {

    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY),
    SUNDAY(Calendar.SUNDAY);

    private final int forCalendar;

    private DayOfWeek(int forCalendar) {
        this.forCalendar = forCalendar;
    }

    public int getForCalendar() {
        return forCalendar;
    }

    public static DayOfWeek parseDate(Date date) {
        if (date == null) {
            throw new NullPointerException("date == null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return parseCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * @param calendarDayOfWeek day of week returned by {@link Calendar} for {@link Calendar#DAY_OF_WEEK}
     * @return
     * @throws IllegalArgumentException if not parsable
     */
    public static DayOfWeek parseCalendarDayOfWeek(int calendarDayOfWeek) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek.forCalendar == calendarDayOfWeek) {
                return dayOfWeek;
            }
        }
        throw new IllegalArgumentException("Not parseble date day of week: " + calendarDayOfWeek);
    }

    public String getGuiString() {
        DateFormatSymbols s = DateFormatSymbols.getInstance();
        return s.getWeekdays()[forCalendar];
    }

    @Override
    public String toString() {
        return getGuiString();
    }

    public static final class CalendarDayOfWeekCmpAsc implements Comparator<DayOfWeek> {

        @Override
        public int compare(DayOfWeek o1, DayOfWeek o2) {
            return o1.forCalendar == o2.forCalendar
                    ? 0
                    : o1.forCalendar > o2.forCalendar
                    ? 1
                    : -1;
        }
    }
}
