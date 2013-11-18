package de.elmar_baumann.nb.slclock.alarmclock;

import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.FRIDAY;
import static de.elmar_baumann.nb.slclock.alarmclock.DayOfWeek.MONDAY;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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
        }
