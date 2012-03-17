package de.elmar_baumann.nb.slclock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 * Displays date and time in the status line.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public final class StatusLineClock implements StatusLineElementProvider {

    private static final int REFRESH_INTERVAL_MILLISECONDS = 1000;
    private final JLabel clockLabel;
    private final JPanel statusLinePanel = new JPanel(new BorderLayout());
    private final ScheduledExecutorService scheduler;

    public StatusLineClock() {
        scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
        DateFormatArray dateFormatArray = StatusLinePreferences.restoreDateFormatArray();
        clockLabel = new JLabel(dateFormatArray.format(new Date()));
        initComponents();
        scheduler.scheduleWithFixedDelay(new ClockLabelUpdater(dateFormatArray), 0, REFRESH_INTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS);
        clockLabel.addMouseListener(settingsDialogDisplayer);
    }

    private void initComponents() {
        statusLinePanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        statusLinePanel.add(clockLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return statusLinePanel;
    }

    private final MouseListener settingsDialogDisplayer = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Frame mainWindow = WindowManager.getDefault().getMainWindow();
                StatusLinePreferencesDialog dialog = new StatusLinePreferencesDialog(mainWindow, true);
                dialog.setLocationRelativeTo(mainWindow);
                dialog.setVisible(true);
            }
        }
    };

    private class ClockLabelUpdater implements Runnable, StatusLinePreferencesListener {

        private DateFormatArray dateFormatArray;

        private ClockLabelUpdater(DateFormatArray dateFormatArray) {
            this.dateFormatArray = dateFormatArray;
            listen();
    }

        private void listen() {
            StatusLinePreferences.addListener(this);
        }

        public void run() {
            Date now = new Date();
            clockLabel.setText(dateFormatArray.format(now));
        }

        public void dateFormatChanged(DateFormatArray newFormat) {
            dateFormatArray = newFormat;
        }
    }
    private final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("StatusLineClock: Displaying Date and Time in the Status Line");
            return thread;
        }
    };
}
