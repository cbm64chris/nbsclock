package de.elmar_baumann.nb.slclock.stopwatch;

import de.elmar_baumann.nb.slclock.util.PropertyChangeNotifier;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * @author Elmar Baumann
 */
public class StopwatchPanel extends javax.swing.JPanel {

    public static final String PROPERTY_RUN = "run";
    @StaticResource private static final String ICON_PATH_START = "de/elmar_baumann/nb/slclock/icons/start.png";
    @StaticResource private static final String ICON_PATH_PAUSE = "de/elmar_baumann/nb/slclock/icons/pause.png";
    private static final Icon ICON_START = ImageUtilities.loadImageIcon(ICON_PATH_START, false);
    private static final Icon ICON_PAUSE = ImageUtilities.loadImageIcon(ICON_PATH_PAUSE, false);
    private static final long serialVersionUID = 1L;
    private long startTimeInMillis;
    private long totalTimeInMillis;
    private long intermediateTimeInMillis;
    private long prevIntermediateEventInMillis;
    private long pauseAtTimeInMillis;
    private boolean started;

    public StopwatchPanel() {
        initComponents();
        checkBoxShowStopWatchIcon.setSelected(NbPreferences.forModule(StopwatchDisplayPanel.class).getBoolean(StopwatchDisplayPanel.KEY_SHOW_ICON, true));
        tableIntermediateResults.setDefaultRenderer(Object.class, tableCellRenderer);
        tableIntermediateResults.setModel(intermediateResultsTableModel);
    }

    private void toggleStarted() {
        if (started) {
            timer.stop();
            pauseAtTimeInMillis = System.currentTimeMillis();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            startTimeInMillis = currentTimeMillis - totalTimeInMillis;
            prevIntermediateEventInMillis += (currentTimeMillis - pauseAtTimeInMillis);
            timer.start();
        }
        started = !started;
        buttonItermediate.setEnabled(started);
        buttonReset.setEnabled(!started);
        buttonStartPause.setIcon(started ? ICON_PAUSE : ICON_START);
        firePropertyChange(PROPERTY_RUN, !started, started);
    }

    private void reset() {
        startTimeInMillis = 0;
        totalTimeInMillis = 0;
        intermediateTimeInMillis = 0;
        started = false;
        buttonStartPause.setIcon(ICON_START);
        labelHours.setText("H");
        labelMinutes.setText("mm");
        labelSeconds.setText("ss");
        labelHundredthSeconds.setText("hs");
        int rowCount = intermediateResultsTableModel.getRowCount();
        for (int row = rowCount - 1; row >= 0; row--) {
            intermediateResultsTableModel.removeRow(row);
        }
    }

    private void addIntermediateResult() {
        intermediateTimeInMillis = startTimeInMillis + totalTimeInMillis - prevIntermediateEventInMillis;
        prevIntermediateEventInMillis = System.currentTimeMillis();
        intermediateResultsTableModel.addRow(new Object[]{
            String.format("%02d", intermediateResultsTableModel.getRowCount() + 1),
            formatTime(intermediateTimeInMillis),
            formatTime(totalTimeInMillis),
            });
    }

    private final ActionListener elapsedTimeUpdater = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            totalTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
            long hours = totalTimeInMillis / (3600 * 1000);
            long minutes = totalTimeInMillis / (60 * 1000) - hours * 60;
            long seconds = totalTimeInMillis / 1000 - hours * 3600 - minutes * 60;
            long hundredthSeconds = (totalTimeInMillis
                    - seconds * 1000
                    - minutes * 60 * 1000
                    - hours * 3600 * 1000)
                    / 10;
            labelHours.setText(String.format("%d", hours));
            labelMinutes.setText(String.format("%02d", minutes));
            labelSeconds.setText(String.format("%02d", seconds));
            labelHundredthSeconds.setText(String.format("%02d", hundredthSeconds));
        }
    };

    private String formatTime(long milliseconds) {
        long hours = milliseconds / (3600 * 1000);
        long minutes = milliseconds / (60 * 1000) - hours * 60;
        long seconds = milliseconds / 1000 - hours * 3600 - minutes * 60;
        long hundredthSeconds = Math.round((double)
                (milliseconds - seconds * 1000
                        - minutes * 60 * 1000
                        - hours * 3600 * 1000)
                / 10.0d);
        String hoursString = hours > 0 ? String.valueOf(hours) : "";
        String hoursDelim = hours > 0 ? ":" : "";
        String minutesString = minutes > 0 ? String.format("%02d", minutes) : "";
        String minutesDelim = minutes > 0 ? ":" : "";
        String secondsString = String.format("%02d", seconds);
        String msString = String.format("%02d", hundredthSeconds);
        return hoursString + hoursDelim + minutesString + minutesDelim + secondsString + ":" + msString;
    }

    private final Timer timer = new Timer(0, elapsedTimeUpdater);

    private final DefaultTableModel intermediateResultsTableModel = new DefaultTableModel(new Object[][]{},
            new String[]{
                NbBundle.getMessage(StopwatchPanel.class, "StopwatchPanel.IntermediateResult.Column.Number"),
                NbBundle.getMessage(StopwatchPanel.class, "StopwatchPanel.IntermediateResult.Column.SincePrevious"),
                NbBundle.getMessage(StopwatchPanel.class, "StopwatchPanel.IntermediateResult.Column.SinceStart"),}) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final TableCellRenderer tableCellRenderer = new TableCellRenderer() {

        private final DefaultTableCellRenderer delegate = new DefaultTableCellRenderer();
        private final Color alternateBackground = new Color(225, 225, 225);
        private final Color defaultBackground;

        {
            defaultBackground = delegate.getBackground();
            delegate.setHorizontalAlignment(SwingConstants.RIGHT);
            delegate.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBackground(row % 2 == 0 ? alternateBackground : defaultBackground);
            label.setText(label.getText() + "  ");
            return label;
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        buttonStartPause = new javax.swing.JButton();
        buttonItermediate = new javax.swing.JButton();
        panelTime = new javax.swing.JPanel();
        labelHours = new javax.swing.JLabel();
        labelTimeDelim1 = new javax.swing.JLabel();
        labelMinutes = new javax.swing.JLabel();
        labelTimeDelim2 = new javax.swing.JLabel();
        labelSeconds = new javax.swing.JLabel();
        labelTimeDelim3 = new javax.swing.JLabel();
        labelHundredthSeconds = new javax.swing.JLabel();
        buttonReset = new javax.swing.JButton();
        panelIntermediateResults = new javax.swing.JPanel();
        scrollPaneIntermediateResults = new javax.swing.JScrollPane();
        tableIntermediateResults = new javax.swing.JTable();
        checkBoxShowStopWatchIcon = new javax.swing.JCheckBox();

        buttonStartPause.setIcon(ICON_START);
        buttonStartPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartPauseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(buttonItermediate, org.openide.util.NbBundle.getBundle(StopwatchPanel.class).getString("StopwatchPanel.buttonItermediate.text")); // NOI18N
        buttonItermediate.setEnabled(false);
        buttonItermediate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonItermediateActionPerformed(evt);
            }
        });

        labelHours.setFont(labelHours.getFont().deriveFont(labelHours.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelHours.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelHours, "H"); // NOI18N

        labelTimeDelim1.setFont(labelTimeDelim1.getFont().deriveFont(labelTimeDelim1.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelTimeDelim1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelTimeDelim1, ":"); // NOI18N

        labelMinutes.setFont(labelMinutes.getFont().deriveFont(labelMinutes.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelMinutes.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelMinutes, "mm"); // NOI18N

        labelTimeDelim2.setFont(labelTimeDelim2.getFont().deriveFont(labelTimeDelim2.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelTimeDelim2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelTimeDelim2, ":"); // NOI18N

        labelSeconds.setFont(labelSeconds.getFont().deriveFont(labelSeconds.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelSeconds.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelSeconds, "ss"); // NOI18N

        labelTimeDelim3.setFont(labelTimeDelim3.getFont().deriveFont(labelTimeDelim3.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelTimeDelim3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelTimeDelim3, ":"); // NOI18N

        labelHundredthSeconds.setFont(labelHundredthSeconds.getFont().deriveFont(labelHundredthSeconds.getFont().getStyle() | java.awt.Font.BOLD, 18));
        labelHundredthSeconds.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(labelHundredthSeconds, "hs"); // NOI18N

        javax.swing.GroupLayout panelTimeLayout = new javax.swing.GroupLayout(panelTime);
        panelTime.setLayout(panelTimeLayout);
        panelTimeLayout.setHorizontalGroup(
            panelTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTimeLayout.createSequentialGroup()
                .addComponent(labelHours)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTimeDelim1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelMinutes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTimeDelim2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSeconds)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTimeDelim3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelHundredthSeconds)
                .addContainerGap())
        );
        panelTimeLayout.setVerticalGroup(
            panelTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTimeLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(panelTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelHours)
                    .addComponent(labelTimeDelim1)
                    .addComponent(labelMinutes)
                    .addComponent(labelTimeDelim2)
                    .addComponent(labelSeconds)
                    .addComponent(labelTimeDelim3)
                    .addComponent(labelHundredthSeconds)))
        );

        org.openide.awt.Mnemonics.setLocalizedText(buttonReset, org.openide.util.NbBundle.getBundle(StopwatchPanel.class).getString("StopwatchPanel.buttonReset.text")); // NOI18N
        buttonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetActionPerformed(evt);
            }
        });

        panelIntermediateResults.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getBundle(StopwatchPanel.class).getString("StopwatchPanel.panelIntermediateResults.border.title"))); // NOI18N

        tableIntermediateResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableIntermediateResults.setShowHorizontalLines(false);
        tableIntermediateResults.setShowVerticalLines(false);
        scrollPaneIntermediateResults.setViewportView(tableIntermediateResults);

        javax.swing.GroupLayout panelIntermediateResultsLayout = new javax.swing.GroupLayout(panelIntermediateResults);
        panelIntermediateResults.setLayout(panelIntermediateResultsLayout);
        panelIntermediateResultsLayout.setHorizontalGroup(
            panelIntermediateResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneIntermediateResults)
        );
        panelIntermediateResultsLayout.setVerticalGroup(
            panelIntermediateResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneIntermediateResults, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(checkBoxShowStopWatchIcon, org.openide.util.NbBundle.getBundle(StopwatchPanel.class).getString("StopwatchPanel.checkBoxShowStopWatchIcon.text")); // NOI18N
        checkBoxShowStopWatchIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxShowStopWatchIconActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxShowStopWatchIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(273, 273, 273))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonStartPause)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonItermediate)
                        .addGap(18, 18, 18)
                        .addComponent(panelTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(134, 134, 134)
                        .addComponent(buttonReset)
                        .addGap(8, 8, 8))
                    .addComponent(panelIntermediateResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(buttonStartPause)
                    .addComponent(buttonItermediate)
                    .addComponent(panelTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonReset))
                .addGap(6, 6, 6)
                .addComponent(panelIntermediateResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxShowStopWatchIcon)
                .addContainerGap())
        );
    }//GEN-END:initComponents

    private void buttonStartPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartPauseActionPerformed
        toggleStarted();
    }//GEN-LAST:event_buttonStartPauseActionPerformed

    private void buttonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetActionPerformed
        reset();
    }//GEN-LAST:event_buttonResetActionPerformed

    private void buttonItermediateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonItermediateActionPerformed
        addIntermediateResult();
    }//GEN-LAST:event_buttonItermediateActionPerformed

    private void checkBoxShowStopWatchIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxShowStopWatchIconActionPerformed
        boolean show = checkBoxShowStopWatchIcon.isSelected();
        PropertyChangeNotifier.INSTANCE.firePropertyChange(PropertyChangeNotifier.PROPERTY_SHOW_STOPWATCH_ICON, !show, show);
    }//GEN-LAST:event_checkBoxShowStopWatchIconActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonItermediate;
    private javax.swing.JButton buttonReset;
    private javax.swing.JButton buttonStartPause;
    private javax.swing.JCheckBox checkBoxShowStopWatchIcon;
    private javax.swing.JLabel labelHours;
    private javax.swing.JLabel labelHundredthSeconds;
    private javax.swing.JLabel labelMinutes;
    private javax.swing.JLabel labelSeconds;
    private javax.swing.JLabel labelTimeDelim1;
    private javax.swing.JLabel labelTimeDelim2;
    private javax.swing.JLabel labelTimeDelim3;
    private javax.swing.JPanel panelIntermediateResults;
    private javax.swing.JPanel panelTime;
    private javax.swing.JScrollPane scrollPaneIntermediateResults;
    private javax.swing.JTable tableIntermediateResults;
    // End of variables declaration//GEN-END:variables

}
