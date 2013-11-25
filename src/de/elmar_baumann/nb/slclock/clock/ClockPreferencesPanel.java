package de.elmar_baumann.nb.slclock.clock;

import de.elmar_baumann.nb.slclock.alarmclock.AlarmEventsModel;
import de.elmar_baumann.nb.slclock.timer.TimerEventsModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Elmar Baumann
 */
public class ClockPreferencesPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final List<DateFormatSettingPanel> settingsPanels = new ArrayList<>();
    private boolean listen;

    public ClockPreferencesPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setInfoCustomPattern();
        restoreFormat();
        showExample();
        setShowAlarmClockSettingsButton(false);
        listen = true;
    }

    private void restoreFormat() {
        for (int i = 0; i < settingsPanels.size(); i++) {
            ClockPreferences.restoreDateFormatSettingsPanel(settingsPanels.get(i), i);
        }
    }

    private void persistFormat() {
        if (checkErrorInCustomFormat()) {
            ClockPreferences.persistDateFormatArray(settingsPanels);
        }
    }

    private void setInfoCustomPattern() {
        labelInfoCustomPattern.setText(
                NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.InfoCustomPattern",
                DateFormatSelection.CUSTOM_PATTERN));
    }

    private void reset() {
        panelSettings1.setDelimiter(" ");
        panelSettings1.setDateFormatSelection(DateFormatSelection.DAY_OF_WEEK_SHORT);
        panelSettings1.setCustomPattern("");
        panelSettings2.setDelimiter(" ");
        panelSettings2.setDateFormatSelection(DateFormatSelection.DATE_SHORT);
        panelSettings2.setCustomPattern("");
        panelSettings3.setDelimiter("-");
        panelSettings3.setDateFormatSelection(DateFormatSelection.TIME_SHORT);
        panelSettings3.setCustomPattern("");
        persistFormat();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!listen) {
            return;
        }
        String propertyName = evt.getPropertyName();
        boolean changed = "dateFormatSelection".equals(propertyName)
                || "delimiter".equals(propertyName)
                || "customPattern".equals(propertyName);
        if (changed) {
            showExample();
        }
    }

    private void showExample() {
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        for (DateFormatSettingPanel panel : settingsPanels) {
            if (panel.getDateFormatSelection() != DateFormatSelection.NONE) {
                sb.append(panel.getDelimiter());
                sb.append(format(now, panel));
            }
        }
        labelExample.setText(sb.toString());
    }

    private String format(Date date, DateFormatSettingPanel panel) {
        DateFormatSelection dateFormatSelection = panel.getDateFormatSelection();
        if (dateFormatSelection == DateFormatSelection.NONE) {
            return "";
        } else if (dateFormatSelection == DateFormatSelection.CUSTOM_PATTERN) {
            try {
                return new SimpleDateFormat(panel.getCustomPattern()).format(date);
            } catch (Throwable t) {
                return NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.Error.Format");
            }
        } else {
            return dateFormatSelection.getDateFormat().format(date);
        }
    }

    private boolean checkErrorInCustomFormat() {
        for (DateFormatSettingPanel panel : settingsPanels) {
            if (panel.getDateFormatSelection() == DateFormatSelection.CUSTOM_PATTERN) {
                String pattern = panel.getCustomPattern();
                if (!isValidCustomPattern(pattern)) {
                    showCustomPatternErrorMessage(pattern);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidCustomPattern(String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private void showCustomPatternErrorMessage(String pattern) {
        String message = NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.Error.CustomPattern", pattern);
        NotifyDescriptor d = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
    }

    /**
     * @param show Default: false
     */
    public void setShowAlarmClockSettingsButton(boolean show) {
        buttonShowAlarmSettingsGui.setVisible(show);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupPatterns = new javax.swing.ButtonGroup();
        panelContent = new javax.swing.JPanel();
        panelSettings = new javax.swing.JPanel();
        panelSettings1 = new de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel();
        panelSettings1.setDelimiter(" ");
        panelSettings1.setDateFormatSelection(DateFormatSelection.DAY_OF_WEEK_SHORT);
        panelSettings1.addPropertyChangeListener(this);
        settingsPanels.add(panelSettings1);
        panelSettings2 = new de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel();
        panelSettings2.setDelimiter(" ");
        panelSettings2.setDateFormatSelection(DateFormatSelection.DATE_SHORT);
        panelSettings2.addPropertyChangeListener(this);
        settingsPanels.add(panelSettings2);
        panelSettings3 = new de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel();
        panelSettings3.setDelimiter("-");
        panelSettings3.setDateFormatSelection(DateFormatSelection.TIME_SHORT);
        panelSettings3.addPropertyChangeListener(this);
        settingsPanels.add(panelSettings3);
        labelInfoCustomPattern = new javax.swing.JLabel();
        panelExample = new javax.swing.JPanel();
        labelExample = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        buttonShowAlarmSettingsGui = new javax.swing.JButton();
        buttonShowTimerSettingsGui = new javax.swing.JButton();
        buttonReset = new javax.swing.JButton();
        buttonApply = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        panelSettings.setLayout(new java.awt.GridBagLayout());

        panelSettings1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelSettings.add(panelSettings1, gridBagConstraints);

        panelSettings2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        panelSettings.add(panelSettings2, gridBagConstraints);

        panelSettings3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        panelSettings.add(panelSettings3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelSettings, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 15);
        panelContent.add(labelInfoCustomPattern, gridBagConstraints);

        panelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.panelExample.border.title"))); // NOI18N
        panelExample.setLayout(new java.awt.GridBagLayout());

        labelExample.setForeground(new java.awt.Color(0, 0, 255));
        org.openide.awt.Mnemonics.setLocalizedText(labelExample, " "); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        panelExample.add(labelExample, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelExample, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(buttonShowAlarmSettingsGui, org.openide.util.NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.buttonShowAlarmSettingsGui.text")); // NOI18N
        buttonShowAlarmSettingsGui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowAlarmSettingsGuiActionPerformed(evt);
            }
        });
        panelButtons.add(buttonShowAlarmSettingsGui, new java.awt.GridBagConstraints());

        org.openide.awt.Mnemonics.setLocalizedText(buttonShowTimerSettingsGui, org.openide.util.NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.buttonShowTimerSettingsGui.text")); // NOI18N
        buttonShowTimerSettingsGui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowTimerSettingsGuiActionPerformed(evt);
            }
        });
        panelButtons.add(buttonShowTimerSettingsGui, new java.awt.GridBagConstraints());

        org.openide.awt.Mnemonics.setLocalizedText(buttonReset, org.openide.util.NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.buttonReset.text")); // NOI18N
        buttonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelButtons.add(buttonReset, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(buttonApply, org.openide.util.NbBundle.getMessage(ClockPreferencesPanel.class, "ClockPreferencesPanel.buttonApply.text")); // NOI18N
        buttonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonApplyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelButtons.add(buttonApply, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        add(panelContent, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonApplyActionPerformed
        persistFormat();
    }//GEN-LAST:event_buttonApplyActionPerformed

    private void buttonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetActionPerformed
        reset();
    }//GEN-LAST:event_buttonResetActionPerformed

    private void buttonShowAlarmSettingsGuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowAlarmSettingsGuiActionPerformed
        AlarmEventsModel.getInstance().showSettingsGui();
    }//GEN-LAST:event_buttonShowAlarmSettingsGuiActionPerformed

    private void buttonShowTimerSettingsGuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowTimerSettingsGuiActionPerformed
        TimerEventsModel.getInstance().showSettingsGui();
    }//GEN-LAST:event_buttonShowTimerSettingsGuiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonApply;
    private javax.swing.ButtonGroup buttonGroupPatterns;
    private javax.swing.JButton buttonReset;
    private javax.swing.JButton buttonShowAlarmSettingsGui;
    private javax.swing.JButton buttonShowTimerSettingsGui;
    private javax.swing.JLabel labelExample;
    private javax.swing.JLabel labelInfoCustomPattern;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelExample;
    private javax.swing.JPanel panelSettings;
    private de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel panelSettings1;
    private de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel panelSettings2;
    private de.elmar_baumann.nb.slclock.clock.DateFormatSettingPanel panelSettings3;
    // End of variables declaration//GEN-END:variables
}
