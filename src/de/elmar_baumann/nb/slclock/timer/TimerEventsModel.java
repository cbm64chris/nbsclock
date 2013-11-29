package de.elmar_baumann.nb.slclock.timer;

import de.elmar_baumann.nb.slclock.util.NamedThreadFactory;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * @author Elmar Baumann
 */
public final class TimerEventsModel {

    public static final String PROPERTY_EVENTS = "events";
    public static final String PROPERTY_SHOW_ICON = "showIcon";
    public static final String PROPERTY_TIME = "time";
    private static final String KEY_SHOW_ICON = "TimerEventsModel.ShowIcon";
    private final Map<TimerEvent, Future<?>> futuresOfEvents = new HashMap<>();
    private final TimerEventsRepository repository = new TimerEventsRepository();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ThreadFactory threadFactory = new NamedThreadFactory("StatusLineClock: Timer Event");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private static TimerEventsModel instance;
    private boolean showIcon = NbPreferences.forModule(TimerEventsModel.class).getBoolean(KEY_SHOW_ICON, true);

    /**
     * Delivered through {@link #PROPERTY_TIME}
     */
    public static final class TimerScheduleEvent {

        private final TimerEvent event;
        private final long remainingSeconds;

        private TimerScheduleEvent(TimerEvent event, long remainingSeconds) {
            this.event = event;
            this.remainingSeconds = remainingSeconds;
        }

        public TimerEvent getEvent() {
            return event;
        }

        public long getRemainingSeconds() {
            return remainingSeconds;
        }
    }

    public static synchronized TimerEventsModel getInstance() {
        if (instance == null) {
            instance = new TimerEventsModel();
        }
        return instance;
    }

    public synchronized Collection<TimerEvent> getEvents() {
        return futuresOfEvents.keySet();
    }

    public synchronized int getRunningEventsCount() {
        int count = 0;
        for (TimerEvent evt : futuresOfEvents.keySet()) {
            if (evt.isRun()) {
                count++;
            }
        }
        return count;
    }

    public synchronized boolean addToEvents(TimerEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (!futuresOfEvents.containsKey(event)) {
            if (event.isRun()) {
                scheduleEvent(event);
            } else {
                futuresOfEvents.put(event, null);
            }
            saveEvents();
            fireEventsChanged();
            return true;
        }
        return false;
    }

    public synchronized void clear() {
        Set<TimerEvent> events = new HashSet<>(futuresOfEvents.keySet());
        for (TimerEvent event : events) {
            removeFromEvents(event);
        }
    }

    public synchronized int getEventCount() {
        return futuresOfEvents.size();
    }

    private synchronized void scheduleEvent(TimerEvent event) {
        if (event.getRemainingSeconds() == 0) {
            event.setRemainingSeconds(event.getSeconds());
        }
        event.setStartTimeInNanos(System.nanoTime());
        Future<?> future = scheduler.scheduleWithFixedDelay(
                new TimerEventRunnable(event),
                0, // no delay
                1, // every second
                TimeUnit.SECONDS);
        futuresOfEvents.put(event, future);
    }

    private synchronized void unscheduleEvent(TimerEvent event) {
        Future<?> future = futuresOfEvents.get(event);
        if (future != null) {
            future.cancel(false);
            futuresOfEvents.put(event, null);
        }
    }

    public synchronized void removeFromEvents(TimerEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (futuresOfEvents.containsKey(event)) {
            unscheduleEvent(event);
            futuresOfEvents.remove(event);
            saveEvents();
            fireEventsChanged();
        }
    }

    public synchronized void updateEvent(TimerEvent oldEvent, TimerEvent newEvent) {
        if (oldEvent == null) {
            throw new NullPointerException("oldEvent == null");
        }
        if (newEvent == null) {
            throw new NullPointerException("newEvent == null");
        }
        if (futuresOfEvents.containsKey(oldEvent)) {
            unscheduleEvent(oldEvent);
            futuresOfEvents.remove(oldEvent);
            addToEvents(newEvent); // saves events and notifies listeners
        }
    }

    public synchronized void setPause(TimerEvent event, boolean pause) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        setRun(event, pause, false);
    }

    public synchronized void setStop(TimerEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        setRun(event, false, true);
    }

    private synchronized void setRun(TimerEvent event, boolean run, boolean stop) {
        TimerEvent e = findEvent(event);
        if (e != null) {
            e.setRun(run);
            Future<?> future = futuresOfEvents.get(e);
            if (future == null) {
                scheduleEvent(event);
            } else {
                if (stop) {
                    event.setRemainingSeconds(event.getSeconds());
                } else {
                    long scheduledSeconds = (System.nanoTime() - e.getStartTimeInNanos()) / 1_000_000_000;
                    long remainingSeconds = e.getRemainingSeconds();
                    if (remainingSeconds > scheduledSeconds) {
                        e.setRemainingSeconds(remainingSeconds - scheduledSeconds);
                    }
                }
                unscheduleEvent(event);
            }
            fireEventsChanged();
        }
    }

    private synchronized TimerEvent findEvent(TimerEvent event) {
        for (TimerEvent e : futuresOfEvents.keySet()) {
            if (e.equals(event)) {
                return e;
            }
        }
        return null;
    }

    private synchronized void saveEvents() {
        try {
            repository.save(new TimerEvents(futuresOfEvents.keySet()));
        } catch (Throwable t) {
            Logger.getLogger(TimerEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public synchronized boolean isShowIcon() {
        return showIcon;
    }

    public synchronized void setShowIcon(boolean show) {
        boolean old = this.showIcon;
        this.showIcon = show;
        NbPreferences.forModule(TimerEventsModel.class).putBoolean(KEY_SHOW_ICON, show);
        pcs.firePropertyChange(PROPERTY_SHOW_ICON, old, show);
    }

    private final class TimerEventRunnable implements Runnable {

        @StaticResource private static final String ICON_PATH_TIMER_RUNS = "de/elmar_baumann/nb/slclock/icons/timer-runs.png";
        private final TimerEvent event;

        private TimerEventRunnable(TimerEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            long eventRemainingSeconds = event.getRemainingSeconds();
            long secondsSinceStartTime = (System.nanoTime() - event.getStartTimeInNanos()) / 1_000_000_000;
            long timerRemainingSeconds = eventRemainingSeconds - secondsSinceStartTime;
            synchronized (TimerEventsModel.this) {
                pcs.firePropertyChange(PROPERTY_TIME, null, new TimerScheduleEvent(event, timerRemainingSeconds));
            }
            if (timerRemainingSeconds < 1) {
                notifyTimer();
                synchronized (TimerEventsModel.this) {
                    if (event.isPersistent()) {
                        unscheduleEvent(event);
                        event.setRun(false);
                    } else {
                        removeFromEvents(event);
                    }
                }
            }
        }

        private void notifyTimer() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (event.isSound()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    String message = NbBundle.getMessage(TimerEventRunnable.class, "TimerEventRunnable.Notification.Details", event);
                    NotificationDisplayer.getDefault().notify(
                            message, // title
                            ImageUtilities.loadImageIcon(ICON_PATH_TIMER_RUNS, false),
                            message,
                            null // detailsAction
                    );
                    if (event.isVerbose()) {
                        message = NbBundle.getMessage(TimerEventRunnable.class, "TimerEventRunnable.Notification.Verbose", event);
                        NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    }
                }
            });
        }
    }

    public void showSettingsGui() {
        TimerEventsPanel timerEventsPanel = new TimerEventsPanel();
        timerEventsPanel.listenToModelChanges(true);
        DialogDescriptor dd = new DialogDescriptor(
                timerEventsPanel, // innerPane
                NbBundle.getMessage(TimerEventsModel.class, "TimerEventsModel.PreferencesDialog.Title"), // title
                true, // modal
                new Object[]{DialogDescriptor.OK_OPTION}, //options
                DialogDescriptor.OK_OPTION, // initialValue
                DialogDescriptor.DEFAULT_ALIGN, // optionsAlign
                null, // helpCtx
                null //bl
        );
        DialogDisplayer.getDefault().notify(dd);
        timerEventsPanel.listenToModelChanges(false);
    }

    private synchronized void fireEventsChanged() {
        pcs.firePropertyChange(PROPERTY_EVENTS, false, true);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        pcs.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        pcs.removePropertyChangeListener(listener);
    }

    private TimerEventsModel() {
        try {
            for (TimerEvent event : repository.load().getEvents()) {
                futuresOfEvents.put(event, null);
            }
        } catch (Throwable t) {
            Logger.getLogger(TimerEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}
