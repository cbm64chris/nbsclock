package de.elmar_baumann.nb.slclock;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.openide.awt.StatusLineElementProvider;

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

    private static final DateFormat DATE_FORMAT =
            DateFormat.getDateInstance(DateFormat.SHORT);
    private static final DateFormat TIME_FORMAT =
            DateFormat.getTimeInstance(DateFormat.SHORT);
    private static final int REFRESH_INTERVAL_MILLI_SEC = 1000;
    private static final String PADDING = "  ";
    private static final String DATE_TIME_DELIM = " - ";
    private final JLabel label = new JLabel(currentDateTimeText());
    private final JPanel panel = new JPanel(new BorderLayout());

    public StatusLineClock() {
        init();
    }

    public Component getStatusLineElement() {
        return panel;
    }

    private void init() {
        Timer timer = new Timer(REFRESH_INTERVAL_MILLI_SEC, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                label.setText(currentDateTimeText());
            }
        });
        timer.start();
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(label, BorderLayout.CENTER);
    }

    private static String currentDateTimeText() {
        Date now = new Date();
        return PADDING +
                DATE_FORMAT.format(now) +
                DATE_TIME_DELIM +
                TIME_FORMAT.format(now) +
                PADDING;
    }
}
