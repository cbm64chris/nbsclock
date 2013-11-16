package de.elmar_baumann.nb.slclock.alarmclock;

import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.*;

/**
 * @author Elmar Baumann
 */
public class DayOfWeekTest {

    @Test
    public void testGetForCalendar() {
        assertEquals(Calendar.MONDAY, MONDAY.getForCalendar());
        assertEquals(Calendar.TUESDAY, TUESDAY.getForCalendar());
        assertEquals(Calendar.WEDNESDAY, WEDNESDAY.getForCalendar());
        assertEquals(Calendar.THURSDAY, THURSDAY.getForCalendar());
        assertEquals(Calendar.FRIDAY, FRIDAY.getForCalendar());
        assertEquals(Calendar.SATURDAY, SATURDAY.getForCalendar());
        assertEquals(Calendar.SUNDAY, SUNDAY.getForCalendar());
    }

    @Test
    public void testParseDate() {
        Calendar cal = Calendar.getInstance();
        int day = 4;
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            cal.set(2013, 10, day); // 10 == November
            day++;
            Date date = cal.getTime();
            DayOfWeek expResult = dayOfWeek;
            DayOfWeek result = DayOfWeek.parseDate(date);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testParseCalendarDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        int day = 4;
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            cal.set(2013, 10, day); // 10 == November
            day++;
            DayOfWeek expResult = dayOfWeek;
            DayOfWeek result = DayOfWeek.parseCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
            assertEquals(expResult, result);
        }
    }
}
