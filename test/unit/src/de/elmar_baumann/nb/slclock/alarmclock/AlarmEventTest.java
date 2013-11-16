package de.elmar_baumann.nb.slclock.alarmclock;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Elmar Baumann
 */
public class AlarmEventTest {

    @Test
    public void testGetHours() {
        AlarmEvent instance = new AlarmEvent(5, 59);
        int expResult = 5;
        int result = instance.getHour();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetMinutes() {
        AlarmEvent instance = new AlarmEvent(22, 45);
        int expResult = 45;
        int result = instance.getMinute();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetDaysOfWeek() {
        AlarmEvent instance = new AlarmEvent(5, 55);
        Collection<DayOfWeek> result = instance.getDaysOfWeek();
        assertTrue(result.isEmpty());
        instance = new AlarmEvent(5, 55, MONDAY);
        Set<DayOfWeek> expResult = EnumSet.copyOf(Arrays.asList(MONDAY));
        result = instance.getDaysOfWeek();
        assertTrue(expResult.containsAll(result) && expResult.size() == result.size());
        instance = new AlarmEvent(5, 55, MONDAY, FRIDAY);
        expResult = EnumSet.copyOf(Arrays.asList(MONDAY, FRIDAY));
        result = instance.getDaysOfWeek();
        assertTrue(expResult.containsAll(result) && expResult.size() == result.size());
        instance = new AlarmEvent(5, 55, DayOfWeek.values());
        result = instance.getDaysOfWeek();
        expResult = EnumSet.allOf(DayOfWeek.class);
        assertTrue(expResult.containsAll(result) && expResult.size() == result.size());
    }

    @Test
    public void testPersistence() {
        AlarmEvent event1 = new AlarmEvent(5, 55);
        AlarmEvent event2 = new AlarmEvent(22, 5, DayOfWeek.MONDAY);
        event2.setRun(true);
        AlarmEvent event3 = new AlarmEvent(22, 5, DayOfWeek.values());
        String persistenceString = AlarmEvent.toPersistentString(Collections.<AlarmEvent>emptyList());
        List<AlarmEvent> persistedEvents = new ArrayList<>(AlarmEvent.fromPersistentString(persistenceString));
        assertTrue(persistedEvents.isEmpty());
        persistenceString = AlarmEvent.toPersistentString(Arrays.asList(event1));
        persistedEvents = new ArrayList<>(AlarmEvent.fromPersistentString(persistenceString));
        assertEquals(1, persistedEvents.size());
        assertEquals(event1, persistedEvents.get(0));
        persistenceString = AlarmEvent.toPersistentString(Arrays.asList(event1, event2, event3));
        persistedEvents = new ArrayList<>(AlarmEvent.fromPersistentString(persistenceString));
        assertEquals(3, persistedEvents.size());
        assertTrue(persistedEvents.contains(event1));
        assertTrue(persistedEvents.contains(event2));
        assertTrue(persistedEvents.contains(event3));
        for (AlarmEvent evt : Arrays.asList(event1, event2, event3)) {
            assertEquals(evt.isRun(), persistedEvents.get(persistedEvents.indexOf(evt)).isRun());
        }
    }

    @Test
    public void testCreateAlarmDateNextTo() {
        AlarmEvent event = new AlarmEvent(5, 55);
        Date result = event.createAlarmDateNextTo(createDate(2013, 11, 24, 5, 55)); // 2013-12-24 == Tuesday
        assertTrue(equals(createDate(2013, 11, 24, 5, 55), result));
        result = event.createAlarmDateNextTo(createDate(2013, 11, 24, 6, 55));
        assertTrue(equals(createDate(2013, 11, 25, 5, 55), result));

        event = new AlarmEvent(5, 55, TUESDAY);
        result = event.createAlarmDateNextTo(createDate(2013, 11, 31, 4, 55)); // 2013-12-31 == Tuesday
        assertTrue(equals(createDate(2013, 11, 31, 5, 55), result));
        result = event.createAlarmDateNextTo(createDate(2013, 11, 31, 6, 55));
        assertTrue(equals(createDate(2014, 0, 7, 5, 55), result)); // 2014-01-07 == Tuesday

        event = new AlarmEvent(5, 55, DayOfWeek.values());
        result = event.createAlarmDateNextTo(createDate(2013, 11, 24, 5, 55));
        assertTrue(equals(createDate(2013, 11, 24, 5, 55), result));
        result = event.createAlarmDateNextTo(createDate(2013, 11, 24, 6, 55));
        assertTrue(equals(createDate(2013, 11, 25, 5, 55), result));

        event = new AlarmEvent(5, 55, TUESDAY, WEDNESDAY, SATURDAY);
        result = event.createAlarmDateNextTo(createDate(2013, 11, 31, 4, 55));
        assertTrue(equals(createDate(2013, 11, 31, 5, 55), result));
        result = event.createAlarmDateNextTo(createDate(2013, 11, 31, 6, 55));
        assertTrue(equals(createDate(2014, 0, 1, 5, 55), result)); // 2014-01-01 == Wednesday
        result = event.createAlarmDateNextTo(createDate(2014, 0, 1, 6, 55));
        assertTrue(equals(createDate(2014, 0, 4, 5, 55), result)); // 2014-04-07 == Saturday
    }

    private boolean equals(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int day1 = cal1.get(Calendar.DAY_OF_MONTH);
        int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
        int minute1 = cal1.get(Calendar.MINUTE);
        int second1 = cal1.get(Calendar.SECOND);
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int day2 = cal2.get(Calendar.DAY_OF_MONTH);
        int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
        int minute2 = cal2.get(Calendar.MINUTE);
        int second2 = cal2.get(Calendar.SECOND);
        return year1 == year2 && month1 == month2 && day1 == day2
                && hour1 == hour2 && minute1 == minute2 && second1 == second2;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
}
