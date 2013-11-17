package de.elmar_baumann.nb.slclock;

import java.awt.Component;

import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * Displays date and time in the status line.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public final class StatusLineClock implements StatusLineElementProvider {

    private StatusLinePanel panelStatusLine;

    @Override
    public Component getStatusLineElement() {
        if (panelStatusLine == null) {
            panelStatusLine = new StatusLinePanel();
        }
        return panelStatusLine;
    }
}
