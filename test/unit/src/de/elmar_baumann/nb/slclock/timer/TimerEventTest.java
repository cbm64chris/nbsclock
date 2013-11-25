package de.elmar_baumann.nb.slclock.timer;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class TimerEventTest {

    public TimerEventTest() {
    }

    @Test
    public void testSetGetSeconds() {
        TimerEvent evt = new TimerEvent();
        assertEquals(0, evt.getSeconds());
        evt.setSeconds(5);
        assertEquals(5, evt.getSeconds());
    }

    @Test
    public void testSetGetDisplayName() {
        TimerEvent evt = new TimerEvent();
        assertNull(evt.getDisplayName());
        String displayName = "Hello, World";
        evt.setDisplayName(displayName);
        assertEquals(displayName, evt.getDisplayName());
        evt.setDisplayName(null);
        assertNull(evt.getDisplayName());
    }

    @Test
    public void testSetGetVerbose() {
        TimerEvent evt = new TimerEvent();
        assertFalse(evt.isVerbose());
        evt.setVerbose(true);
        assertTrue(evt.isVerbose());
    }

    @Test
    public void testSetGetSound() {
        TimerEvent evt = new TimerEvent();
        assertFalse(evt.isSound());
        evt.setSound(true);
        assertTrue(evt.isSound());
    }

    @Test
    public void testSetGetRun() {
        TimerEvent evt = new TimerEvent();
        assertFalse(evt.isRun());
        evt.setRun(true);
        assertTrue(evt.isRun());
    }

    @Test
    public void testSetGetPersistent() {
        TimerEvent evt = new TimerEvent();
        assertFalse(evt.isPersistent());
        evt.setPersistent(true);
        assertTrue(evt.isPersistent());
    }

    @Test
    public void testEquals() {
        TimerEvent evt1 = new TimerEvent();
        TimerEvent evt2 = new TimerEvent();
        assertTrue(evt1.equals(evt2));
        evt1.setSeconds(3);
        assertFalse(evt1.equals(evt2));
        evt2 = new TimerEvent(evt1);
        assertTrue(evt1.equals(evt2));
        String displayName = "Hello, World";
        evt1.setDisplayName(displayName);
        assertFalse(evt1.equals(evt2));
        evt2.setDisplayName(displayName);
        assertTrue(evt1.equals(evt2));
        evt2.setDisplayName(displayName.toLowerCase());
        assertTrue(evt1.equals(evt2));
    }

    @Test
    public void testGetHours() {
        TimerEvent evt = new TimerEvent();
        assertEquals(0, evt.getHours());
        int secondsPerHour = 3600;
        evt.setSeconds(secondsPerHour - 1);
        assertEquals(0, evt.getHours());
        evt.setSeconds(secondsPerHour);
        assertEquals(1, evt.getHours());
        evt.setSeconds(secondsPerHour * 3 + 1);
        assertEquals(3, evt.getHours());
    }

    @Test
    public void testGetMinutesPerHour() {
        TimerEvent evt = new TimerEvent();
        assertEquals(0, evt.getMinutesPerHour());
        int secondsPerMinute = 60;
        evt.setSeconds(secondsPerMinute - 1);
        assertEquals(0, evt.getMinutesPerHour());
        evt.setSeconds(secondsPerMinute);
        assertEquals(1, evt.getMinutesPerHour());
        evt.setSeconds(7200 + 3 * secondsPerMinute + 2); // 2h:3m:2s
        assertEquals(3, evt.getMinutesPerHour());
    }

    @Test
    public void testGetSecondsPerMinute() {
        TimerEvent evt = new TimerEvent();
        assertEquals(0, evt.getSecondsPerMinute());
        evt.setSeconds(59);
        assertEquals(59, evt.getSecondsPerMinute());
        evt.setSeconds(61);
        assertEquals(1, evt.getSecondsPerMinute());
        evt.setSeconds(7200 + 2); // 2h:0m:2s
        assertEquals(2, evt.getSecondsPerMinute());
    }
}
