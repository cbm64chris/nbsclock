package de.elmar_baumann.nb.slclock.alarmclock;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class AlarmEventsRepositoryTest {

    @Test
    public void testSaveAndLoad() {
        AlarmEvents events = new AlarmEvents();
        AlarmEventsRepository repository = new AlarmEventsRepository();
        repository.save(events);
        Collection<AlarmEvent> loadedEvents = repository.load().getEvents();
        Assert.assertTrue(loadedEvents.isEmpty());
        AlarmEvent evt1 = new AlarmEvent(7, 56);
        AlarmEvent evt2 = new AlarmEvent(8, 33, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        evt2.setSound(true);
        evt2.setRun(true);
        events = new AlarmEvents(Arrays.asList(evt1));
        repository.save(events);
        loadedEvents = repository.load().getEvents();
        Assert.assertEquals(1, loadedEvents.size());
        Assert.assertTrue(loadedEvents.contains(evt1));
        events = new AlarmEvents(Arrays.asList(evt1, evt2));
        repository.save(events);
        loadedEvents = repository.load().getEvents();
        Assert.assertEquals(2, loadedEvents.size());
        Assert.assertTrue(loadedEvents.contains(evt1));
        Assert.assertTrue(loadedEvents.contains(evt2));
        AlarmEvent e = find(loadedEvents, evt2);
        Assert.assertEquals(evt2, e);
        Assert.assertTrue(e.isRun());
        Assert.assertTrue(e.isSound());
        e = find(loadedEvents, evt1);
        Assert.assertEquals(evt1, e);
        Assert.assertFalse(e.isRun());
        Assert.assertFalse(e.isSound());
    }

    private AlarmEvent find(Collection<AlarmEvent> events, AlarmEvent event) {
        for (AlarmEvent evt : events) {
            if (event.equals(evt)) {
                return evt;
            }
        }
        return null;
    }
}
