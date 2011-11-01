package de.elmar_baumann.nb.slclock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.openide.awt.StatusLineElementProvider;

/**
 * Displays date and time in the status line.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-04-02
 */
public final class StatusLineClock implements StatusLineElementProvider {

    private static final String FORMAT_PATTERN = "  {0} {1} - {2}";
    private static final DateFormat WEEKDAY_FORMAT = new SimpleDateFormat("E");
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final int REFRESH_INTERVAL_MILLISECONDS = 1000;
    private final JLabel clockLabel = new JLabel(getCurrentDateTimeString());
    private final JPanel statusLinePanel = new JPanel(new BorderLayout());
    private final ScheduledExecutorService scheduler;

    public StatusLineClock() {
        scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
        initComponents();
        scheduler.scheduleWithFixedDelay(clockLabelUpdater, 0, REFRESH_INTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    private void initComponents() {
        statusLinePanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        statusLinePanel.add(clockLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getStatusLineElement() {
        return statusLinePanel;
    }

    private static String getCurrentDateTimeString() {
        Date now = new Date();

        return MessageFormat.format(FORMAT_PATTERN,
                WEEKDAY_FORMAT.format(now),
                DATE_FORMAT.format(now),
                TIME_FORMAT.format(now));
    }

    private final Runnable clockLabelUpdater = new Runnable() {

        public void run() {
            clockLabel.setText(getCurrentDateTimeString());
        }
    };

    private final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);

            thread.setName("StatusLineClock: Displaying Date and Time in the Status Line");

            return thread;
        }
    };
}
