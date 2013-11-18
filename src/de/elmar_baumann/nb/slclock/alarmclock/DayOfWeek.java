package de.elmar_baumann.nb.slclock.alarmclock;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Comparator;

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

    public boolean isDayOfWeek(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        return forCalendar == cal.get(Calendar.DAY_OF_WEEK);
    }

    public String getGuiString() {
        DateFormatSymbols s = DateFormatSymbols.getInstance();
        return s.getWeekdays()[forCalendar];
    }

    public String getShortGuiString() {
        String guiString = getGuiString();
        return guiString.length() > 2
                ? guiString.substring(0, 2)
                : guiString;
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
