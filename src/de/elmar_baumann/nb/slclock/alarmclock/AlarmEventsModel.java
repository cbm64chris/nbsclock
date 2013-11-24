package de.elmar_baumann.nb.slclock.alarmclock;

import de.elmar_baumann.nb.slclock.util.NamedThreadFactory;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
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
public final class AlarmEventsModel {

    public static final String PROPERTY_EVENTS = "events";
    public static final String PROPERTY_SHOW_ICON = "showIcon";
    private static final String KEY_SHOW_ICON = "AlarmEventsModel.ShowIcon";
    private final Map<AlarmEvent, Future<?>> futuresOfEvents = new HashMap<>();
    private final AlarmEventsRepository repository = new AlarmEventsRepository();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final ThreadFactory threadFactory = new NamedThreadFactory("StatusLineClock: Alarm Event");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private static AlarmEventsModel instance;
    private boolean showIcon = NbPreferences.forModule(AlarmEventsModel.class).getBoolean(KEY_SHOW_ICON, true);

    public static synchronized AlarmEventsModel getInstance() {
        if (instance == null) {
            instance = new AlarmEventsModel();
        }
        return instance;
    }

    public synchronized Collection<AlarmEvent> getEvents() {
        return futuresOfEvents.keySet();
    }

    public int getRunningEventsCount() {
        int count = 0;
        for (AlarmEvent evt : futuresOfEvents.keySet()) {
            if (evt.isRun()) {
                count++;
            }
        }
        return count;
    }

    public synchronized void addToEvents(AlarmEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        if (!futuresOfEvents.containsKey(event)) {
            scheduleEvent(event);
            saveEvents();
            fireEventsChanged();
        }
    }

    private synchronized void scheduleEvent(AlarmEvent event) {
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
        futuresOfEvents.get(event).cancel(false);
        futuresOfEvents.remove(event);
        saveEvents();
        fireEventsChanged();
    }

    public synchronized void updateEvent(AlarmEvent oldEvent, AlarmEvent newEvent) {
        if (oldEvent == null) {
            throw new NullPointerException("oldEvent == null");
        }
        if (newEvent == null) {
            throw new NullPointerException("newEvent == null");
        }
        if (futuresOfEvents.containsKey(oldEvent)) {
            futuresOfEvents.get(oldEvent).cancel(false);
            futuresOfEvents.remove(oldEvent);
            scheduleEvent(newEvent);
            saveEvents();
            fireEventsChanged();
        }
    }

    public synchronized void setRun(AlarmEvent event, boolean run) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        AlarmEvent e = findEvent(event);
        if (e != null) {
            e.setRun(run);
            saveEvents();
            fireEventsChanged();
        }
    }

    private synchronized AlarmEvent findEvent(AlarmEvent event) {
        for (AlarmEvent e : futuresOfEvents.keySet()) {
            if (e.equals(event)) {
                return e;
            }
        }
        return null;
    }

    private synchronized void saveEvents() {
        try {
            repository.save(new AlarmEvents(futuresOfEvents.keySet()));
        } catch (Throwable t) {
            Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public synchronized boolean isShowIcon() {
        return showIcon;
    }

    public synchronized void setShowIcon(boolean show) {
        boolean old = this.showIcon;
        this.showIcon = show;
        NbPreferences.forModule(AlarmEventsModel.class).putBoolean(KEY_SHOW_ICON, show);
        pcs.firePropertyChange(PROPERTY_SHOW_ICON, old, show);
    }

    private final class AlarmEventRunnable implements Runnable {

        @StaticResource private static final String ICON_PATH_ALARM_RUNS = "de/elmar_baumann/nb/slclock/icons/alarm-runs.png";
        private final AlarmEvent event;

        private AlarmEventRunnable(AlarmEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event.isAlarm(System.currentTimeMillis())) {
                notifyAlarm();
                if (!event.isRepeatable()) {
                    removeFromEvents(event);
                }
            }
        }

        private void notifyAlarm() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    NotificationDisplayer.getDefault().notify(
                            NbBundle.getMessage(AlarmEventRunnable.class, "AlarmEventRunnable.Notification.Title"),
                            ImageUtilities.loadImageIcon(ICON_PATH_ALARM_RUNS, false),
                            NbBundle.getMessage(AlarmEventRunnable.class, "AlarmEventRunnable.Notification.Details", event),
                            null // detailsAction
                    );
                    if (event.isVerbose()) {
                        String message = NbBundle.getMessage(AlarmEventRunnable.class, "AlarmEventRunnable.Notification.Verbose", event);
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
        AlarmEventsPanel alarmEventsPanel = new AlarmEventsPanel();
        alarmEventsPanel.listenToModelChanges(true);
        DialogDescriptor dd = new DialogDescriptor(
                alarmEventsPanel, // innerPane
                NbBundle.getMessage(AlarmClockPanel.class, "AlarmClockPanel.PreferencesDialog.Title"), // title
                true, // modal
                new Object[]{DialogDescriptor.OK_OPTION}, //options
                DialogDescriptor.OK_OPTION, // initialValue
                DialogDescriptor.DEFAULT_ALIGN, // optionsAlign
                null, // helpCtx
                null //bl
        );
        DialogDisplayer.getDefault().notify(dd);
        alarmEventsPanel.listenToModelChanges(false);
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

    private AlarmEventsModel() {
        try {
            for (AlarmEvent event : repository.load().getEvents()) {
                scheduleEvent(event);
            }
        } catch (Throwable t) {
            Logger.getLogger(AlarmEventsModel.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}
