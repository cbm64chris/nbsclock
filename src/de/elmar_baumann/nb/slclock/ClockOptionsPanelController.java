/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elmar_baumann.nb.slclock;

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
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_Clock=Clock", "AdvancedOption_Keywords_Clock=Statusline Clock"})
public final class ClockOptionsPanelController extends OptionsPanelController {

    private ClockOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getPanel().load();
        changed = false;
    }

    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

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
