package de.elmar_baumann.nb.slclock.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Elmar Baumann
 */
public final class PropertyChangeNotifier {

    public static final String PROPERTY_SHOW_STOPWATCH_ICON = "showStopwatchIcon";
    public static final PropertyChangeNotifier INSTANCE = new PropertyChangeNotifier();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
