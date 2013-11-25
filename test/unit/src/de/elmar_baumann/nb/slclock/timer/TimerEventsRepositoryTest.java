package de.elmar_baumann.nb.slclock.timer;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class TimerEventsRepositoryTest {

    public TimerEventsRepositoryTest() {
    }

    @Test
    public void testSaveAndLoad() {
        TimerEventsRepository repo = new TimerEventsRepository();
        TimerEvents events = new TimerEvents();
        repo.save(events);
        TimerEvents loadedEvents = repo.load();
        assertTrue(loadedEvents.getEvents().isEmpty());
        TimerEvent evt1 = new TimerEvent();
        evt1.setSeconds(25);
        evt1.setPersistent(true);
        events.setEvents(Arrays.asList(evt1));
        repo.save(events);
        loadedEvents = repo.load();
        assertEquals(1, loadedEvents.getEvents().size());
        assertTrue(loadedEvents.getEvents().contains(evt1));
        TimerEvent evt2 = new TimerEvent();
        evt2.setSeconds(37);
        evt2.setPersistent(true);
        events.setEvents(Arrays.asList(evt1, evt2));
        repo.save(events);
        loadedEvents = repo.load();
        assertEquals(2, loadedEvents.getEvents().size());
        assertTrue(loadedEvents.getEvents().containsAll(Arrays.asList(evt1, evt2)));
        evt2.setPersistent(false);
        repo.save(events);
        loadedEvents = repo.load();
        assertEquals(1, loadedEvents.getEvents().size());
        assertTrue(loadedEvents.getEvents().contains(evt1));
    }
}
