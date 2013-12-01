package de.elmar_baumann.nb.slclock.alarmclock;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Elmar Baumann
 */
public final class AlarmEventSnoozeAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final AlarmEvent event;

    public AlarmEventSnoozeAction(AlarmEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }
        String name = NbBundle.getMessage(AlarmEventSnoozeAction.class, "AlarmEventSnoozeAction.Name", event.getDisplayName());
        putValue(Action.NAME, name);
        this.event = event;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AlarmEventSnoozeTimePanel panel = new AlarmEventSnoozeTimePanel(NbBundle.getMessage(AlarmEventSnoozeAction.class, "AlarmEventSnoozeAction.EventDisplayName", event));
        NotifyDescriptor nd = new NotifyDescriptor.Message(panel, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        AlarmEventsModel.getInstance().snoozeEvent(event, panel.getSnoozeTimeInMinutes());
    }
}
