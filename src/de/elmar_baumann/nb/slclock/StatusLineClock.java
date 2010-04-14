package de.elmar_baumann.nb.slclock;

import org.openide.awt.StatusLineElementProvider;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Displays date and time in the IDE's status line.
 *
 * The refresh interval is 1 second, the date's and time's display format is
 * DateFormat.SHORT.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-04-02
 */
public final class StatusLineClock implements StatusLineElementProvider {
    public static final String      FORMAT_PATTERN;
    private static final DateFormat WEEKDAY;
    private static final DateFormat DATE_FORMAT;
    private static final DateFormat TIME_FORMAT;
    private static final int        REFRESH_INTERVAL_MILLI_SEC;
    private final JLabel            label;
    private final JPanel            panel;

    static {
        FORMAT_PATTERN             = "  {0} {1} - {2}";
        REFRESH_INTERVAL_MILLI_SEC = 1000;
        WEEKDAY                    = new SimpleDateFormat("E");
        DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);
        TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    public StatusLineClock() {
        label = new JLabel(currentDateTimeText());
        panel = new JPanel(new BorderLayout());
        init();
    }

    public Component getStatusLineElement() {
        return panel;
    }

    private void init() {
        Timer timer = new Timer(REFRESH_INTERVAL_MILLI_SEC, new DatePainter());

        timer.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(label, BorderLayout.CENTER);
    }

    private static String currentDateTimeText() {
        Date now = new Date();

        return MessageFormat.format(FORMAT_PATTERN, WEEKDAY.format(now),
                                    DATE_FORMAT.format(now),
                                    TIME_FORMAT.format(now));
    }

    private class DatePainter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            label.setText(currentDateTimeText());
        }
    }
}
