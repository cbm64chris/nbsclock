package de.elmar_baumann.nb.slclock.alarmclock;

import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.FRIDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.MONDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.SATURDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.SUNDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.THURSDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.TUESDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.WEDNESDAY;
import java.util.Arrays;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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
    public void testIsDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        int day = 4; // 2013-11-04 == Monday
        for (DayOfWeek dayOfWeek : Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)) {
            cal.set(2013, 10, day); // 10 == November
            assertTrue(dayOfWeek.isDayOfWeek(cal.getTimeInMillis()));
            day++;
        }
    }
        }
