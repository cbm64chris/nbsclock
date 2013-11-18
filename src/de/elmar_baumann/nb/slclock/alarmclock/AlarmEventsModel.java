package de.elmar_baumann.nb.slclock.alarmclock;

import de.elmar_baumann.nb.slclock.util.NamedThreadFactory;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * @author Elmar Baumann
 */
public final class AlarmEventsModel {

    public static final String PROPERTY_EVENTS = "events";
    private final Map<AlarmEvent, Future<?>> futuresOfEvents = new HashMap<>();
    private final AlarmEventsRepository repository = new AlarmEventsRepository();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ThreadFactory threadFactory = new NamedThreadFactory("StatusLineClock: Events");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private boolean init;

    public synchronized void init() {
        if (init) {
            Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.WARNING, "init() should be called once!");
            return;
        }
        try {
            for (AlarmEvent event : repository.load().getEvents()) {
                add(event);
            }
            init = true;
        } catch (Throwable t) {
            Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public synchronized Collection<AlarmEvent> getEvents() {
        return futuresOfEvents.keySet();
    }

    public int getEventCount() {
        return futuresOfEvents.size();
    }

    public synchronized void addToEvents(AlarmEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (!futuresOfEvents.containsKey(event)) {
            add(event);
            try {
                repository.save(new AlarmEvents(futuresOfEvents.keySet()));
                fireEventsChanged();
            } catch (Throwable t) {
                Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private synchronized void add(AlarmEvent event) {
        Future<?> future = scheduler.scheduleWithFixedDelay(
                new AlarmEventRunnable(event),
                getInitialDelayInSeconds(),
                60, // check every 60 seconds
                TimeUnit.SECONDS);
        futuresOfEvents.put(event, future);
    }

    private long getInitialDelayInSeconds() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return 60 - cal.get(Calendar.SECOND);
    }

    public synchronized void removeFromEvents(AlarmEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (!futuresOfEvents.containsKey(event)) {
            return;
        }
        Future<?> future = futuresOfEvents.get(event);
        if (future != null) {
            future.cancel(false);
        }
        futuresOfEvents.remove(event);
        try {
            repository.save(new AlarmEvents(futuresOfEvents.keySet()));
            fireEventsChanged();
        } catch (Throwable t) {
            Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public synchronized void changeEvent(AlarmEvent oldEvent, AlarmEvent newEvent) {
        if (futuresOfEvents.containsKey(oldEvent)) {
            Future<?> future = futuresOfEvents.get(oldEvent);
            future.cancel(false);
            futuresOfEvents.remove(oldEvent);
            add(newEvent);
        }
    }

    private final class AlarmEventRunnable implements Runnable {

        private final AlarmEvent event;

        private AlarmEventRunnable(AlarmEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event.isAlarm(System.currentTimeMillis())) {
                notifyAlarm();
                if (!event.isRepeatable()) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            removeFromEvents(event);
                        }
                    });
                }
            }
        }

        private void notifyAlarm() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    NotificationDisplayer.getDefault().notify(
                            NbBundle.getMessage(AlarmEventRunnable.class, "AlarmEventRunnable.Notification.Title"),
                            ImageUtilities.loadImageIcon("de/elmar_baumann/nb/slclock/icons/alarm-runs.png", false),
                            NbBundle.getMessage(AlarmEventRunnable.class, "AlarmEventRunnable.Notification.Details", event),
                            null // detailsAction
                    );
                }
            });
            if (event.isSound()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public synchronized void runEvent(AlarmEvent event, boolean run) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        for (AlarmEvent e : futuresOfEvents.keySet()) {
            if (e.equals(event)) {
                e.setRun(run);
                break;
            }
        }
    }

    private void fireEventsChanged() {
        pcs.firePropertyChange(PROPERTY_EVENTS, false, true);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        pcs.removePropertyChangeListener(listener);
    }
}
