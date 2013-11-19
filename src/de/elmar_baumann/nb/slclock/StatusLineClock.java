package de.elmar_baumann.nb.slclock;

import java.awt.Component;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * Displays date and time in the status line, has an alarm clock.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public final class StatusLineClock implements StatusLineElementProvider {

    private StatusLineClockPanel panel;

    @Override
    public Component getStatusLineElement() {
        if (panel == null) {
            panel = new StatusLineClockPanel();
        }
        return panel;
    }
}
