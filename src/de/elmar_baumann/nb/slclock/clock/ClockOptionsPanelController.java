package de.elmar_baumann.nb.slclock.clock;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;

import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import org.netbeans.spi.options.OptionsPanelController;

@OptionsPanelController.SubRegistration(location = "Advanced",
    displayName = "#AdvancedOption_DisplayName_Clock",
    keywords = "#AdvancedOption_Keywords_Clock",
    keywordsCategory = "Advanced/Clock")
@org.openide.util.NbBundle.Messages({
    "AdvancedOption_DisplayName_Clock=Clock",
    "AdvancedOption_Keywords_Clock=Statusline Clock"
})
public final class ClockOptionsPanelController extends OptionsPanelController {

    private ClockOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private ClockOptionsPanel getPanel() {
        if (panel == null) {
            panel = new ClockOptionsPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
