package de.elmar_baumann.nb.slclock.timer;

import java.util.Collection;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class TimerEventsModelTest {

    @Test
    public void testGetEvents() {
        TimerEventsModel model = TimerEventsModel.getInstance();
        try {
            Collection<TimerEvent> events = model.getEvents();
            assertTrue(events.isEmpty());
            TimerEvent evt = new TimerEvent();
            evt.setSeconds(5);
            model.addToEvents(evt);
            events = model.getEvents();
            Assert.assertEquals(1, events.size());
            Assert.assertEquals(evt, events.iterator().next());
        } finally {
            model.clear();
        }
    }

    @Test
    public void testGetRunningEventsCount() {
        TimerEventsModel model = TimerEventsModel.getInstance();
        try {
            TimerEvent evt1 = new TimerEvent();
            evt1.setSeconds(60);
            model.addToEvents(evt1);
            Assert.assertEquals(0, model.getRunningEventsCount());
            model.setRun(evt1, true);
            Assert.assertEquals(1, model.getRunningEventsCount());
            TimerEvent evt2 = new TimerEvent();
            evt2.setSeconds(70);
            evt2.setRun(true);
            model.addToEvents(evt2);
            Assert.assertEquals(2, model.getRunningEventsCount());
            model.setRun(evt2, false);
            Assert.assertEquals(1, model.getRunningEventsCount());
        } finally {
            model.clear();
        }
    }

    @Test
    public void testAddRemoveCountEvents() {
        TimerEventsModel model = TimerEventsModel.getInstance();
        try {
            TimerEvent evt1 = new TimerEvent();
            Assert.assertEquals(0, model.getEventCount());
            evt1.setSeconds(60);
            model.addToEvents(evt1);
            TimerEvent evt2 = new TimerEvent();
            evt2.setSeconds(100);
            model.addToEvents(evt2);
            Assert.assertEquals(2, model.getEventCount());
            model.removeFromEvents(evt1);
            Assert.assertEquals(1, model.getEventCount());
            model.removeFromEvents(evt2);
            Assert.assertEquals(0, model.getEventCount());
            model.addToEvents(evt1);
            model.addToEvents(evt2);
            Assert.assertEquals(2, model.getEventCount());
            model.clear();
            Assert.assertEquals(0, model.getEventCount());
        } finally {
            model.clear();
        }
    }

    @Test
    public void testUpdateEvent() {
        TimerEventsModel model = TimerEventsModel.getInstance();
        try {
            TimerEvent evt1 = new TimerEvent();
            evt1.setSeconds(60);
            model.addToEvents(evt1);
            Assert.assertEquals(evt1, model.getEvents().iterator().next());
            TimerEvent evt2 = new TimerEvent();
            evt2.setSeconds(75);
            model.updateEvent(evt1, evt2);
            Assert.assertEquals(evt2, model.getEvents().iterator().next());
        } finally {
            model.clear();
        }
    }

    @Test
    public void testSetRun() {
        TimerEventsModel model = TimerEventsModel.getInstance();
        try {
            TimerEvent evt1 = new TimerEvent();
            evt1.setSeconds(600);
            model.addToEvents(evt1);
            Assert.assertEquals(0, model.getRunningEventsCount());
            model.setRun(evt1, true);
            Assert.assertEquals(1, model.getRunningEventsCount());
            Assert.assertTrue(model.getEvents().iterator().next().isRun());
            model.setRun(evt1, false);
            Assert.assertEquals(0, model.getRunningEventsCount());
            Assert.assertFalse(model.getEvents().iterator().next().isRun());
        } finally {
            model.clear();
        }
    }
}
