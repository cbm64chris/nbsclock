package de.elmar_baumann.nb.slclock.timer;

import de.elmar_baumann.nb.slclock.util.NamedThreadFactory;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
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
    private static final String KEY_SHOW_ICON = "TimerEventsModel.ShowIcon";
    private final Map<TimerEvent, Future<?>> futuresOfEvents = new HashMap<>();
    private final TimerEventsRepository repository = new TimerEventsRepository();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ThreadFactory threadFactory = new NamedThreadFactory("StatusLineClock: Timer Event");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private static TimerEventsModel instance;
    private boolean showIcon = NbPreferences.forModule(TimerEventsModel.class).getBoolean(KEY_SHOW_ICON, true);

    public static synchronized TimerEventsModel getInstance() {
        if (instance == null) {
            instance = new TimerEventsModel();
        }
        return instance;
    }

    public synchronized Collection<TimerEvent> getEvents() {
        return futuresOfEvents.keySet();
    }

    public int getRunningEventsCount() {
        int count = 0;
        for (TimerEvent evt : futuresOfEvents.keySet()) {
            if (evt.isRun()) {
                count++;
            }
        }
        return count;
    }

    public synchronized void addToEvents(TimerEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (!futuresOfEvents.containsKey(event)) {
            futuresOfEvents.put(event, null);
            saveEvents();
            fireEventsChanged();
        }
    }

    public synchronized void scheduleEvent(TimerEvent event) {
        Future<?> future = scheduler.schedule(
                new TimerEventRunnable(event),
                event.getSeconds(),
                TimeUnit.SECONDS);
        futuresOfEvents.put(event, future);
    }

    public synchronized void removeFromEvents(TimerEvent event) {
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
        saveEvents();
        fireEventsChanged();
    }

    public synchronized void updateEvent(TimerEvent oldEvent, TimerEvent newEvent) {
        if (oldEvent == null) {
            throw new NullPointerException("oldEvent == null");
        }
        if (newEvent == null) {
            throw new NullPointerException("newEvent == null");
        }
        if (futuresOfEvents.containsKey(oldEvent)) {
            Future<?> future = futuresOfEvents.get(oldEvent);
            if (future != null) {
                future.cancel(false);
            }
            futuresOfEvents.remove(oldEvent);
            saveEvents();
            fireEventsChanged();
        }
    }

    public synchronized void setRun(TimerEvent event, boolean run) {
        // TODO pause
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        TimerEvent e = findEvent(event);
        if (e != null) {
            e.setRun(run);
            saveEvents();
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

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean show) {
        boolean old = this.showIcon;
        this.showIcon = show;
        NbPreferences.forModule(TimerEventsModel.class).putBoolean(KEY_SHOW_ICON, show);
        pcs.firePropertyChange(PROPERTY_SHOW_ICON, old, show);
    }

    private final class TimerEventRunnable implements Runnable {

        private final TimerEvent event;

        private TimerEventRunnable(TimerEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            notifyTimer();
            if (event.isPersistent()) {
                synchronized(TimerEventsModel.this) {
                    futuresOfEvents.put(event, null);
                }
            } else {
                removeFromEvents(event);
            }
        }

        private void notifyTimer() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    NotificationDisplayer.getDefault().notify(
                            NbBundle.getMessage(TimerEventRunnable.class, "TimerEventRunnable.Notification.Title"),
                            ImageUtilities.loadImageIcon("de/elmar_baumann/nb/slclock/icons/alarm-runs.png", false),
                            NbBundle.getMessage(TimerEventRunnable.class, "TimerEventRunnable.Notification.Details", event),
                            null // detailsAction
                    );
                    if (event.isVerbose()) {
                        String message = NbBundle.getMessage(TimerEventRunnable.class, "TimerEventRunnable.Notification.Verbose", event);
                        NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    }
                }
            });
            if (event.isSound()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public void showSettingsGui() {
//        TimerEventsPanel alarmEventsPanel = new TimerEventsPanel();
//        alarmEventsPanel.listenToModelChanges(true);
//        DialogDescriptor dd = new DialogDescriptor(
//                alarmEventsPanel, // innerPane
//                NbBundle.getMessage(TimerClockPanel.class, "TimerClockPanel.PreferencesDialog.Title"), // title
//                true, // modal
//                new Object[]{DialogDescriptor.OK_OPTION}, //options
//                DialogDescriptor.OK_OPTION, // initialValue
//                DialogDescriptor.DEFAULT_ALIGN, // optionsAlign
//                null, // helpCtx
//                null //bl
//        );
//        DialogDisplayer.getDefault().notify(dd);
//        alarmEventsPanel.listenToModelChanges(false);
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
                scheduleEvent(event);
            }
        } catch (Throwable t) {
            Logger.getLogger(TimerEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}
