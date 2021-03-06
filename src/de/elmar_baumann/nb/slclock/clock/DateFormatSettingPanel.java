package de.elmar_baumann.nb.slclock.clock;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.NbBundle;

/**
 * @author Elmar Baumann
 */
public class DateFormatSettingPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public DateFormatSettingPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textFieldCustomPattern.getDocument().addDocumentListener(customFormatChangedDocumentListener);
    }

    void setDateFormatSelection(DateFormatSelection dateFormatSelection) {
        comboBoxFormat.setSelectedItem(dateFormatSelection);
    }

    DateFormatSelection getDateFormatSelection() {
        return (DateFormatSelection) comboBoxFormat.getSelectedItem();
    }

    void setDelimiter(String delimiter) {
        comboBoxDelimiter.setSelectedItem(delimiter);
    }

    String getDelimiter() {
        return (String) comboBoxDelimiter.getSelectedItem();
    }

    void setCustomPattern(String pattern) {
        textFieldCustomPattern.setText(pattern.trim());
    }

    String getCustomPattern() {
        return textFieldCustomPattern.getText().trim();
    }

    private void checkCustomFormat() {
        Object selectedItem = comboBoxFormat.getSelectedItem();
        boolean isCustomPattern = selectedItem == DateFormatSelection.CUSTOM_PATTERN;
        textFieldCustomPattern.setEditable(isCustomPattern);
        if (isCustomPattern) {
            textFieldCustomPattern.requestFocusInWindow();
        }
    }

    private final DocumentListener customFormatChangedDocumentListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            fireCustomPatternChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireCustomPatternChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fireCustomPatternChanged();
        }

        private void fireCustomPatternChanged() {
            firePropertyChange("customPattern", null, getCustomPattern());
        }
    };

    private static final ListCellRenderer<String> DELIMITER_RENDERER = new ListCellRenderer<String>() {

        private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ("".equals(value)) { // not value.isEmpty(), maybe null, hence no switch over string
                label.setText(NbBundle.getMessage(DateFormatSettingPanel.class, "DelimiterRenderer.Value.Empty"));
            } else if (" ".equals(value)) {
                label.setText(NbBundle.getMessage(DateFormatSettingPanel.class, "DelimiterRenderer.Value.Space"));
            }
            return label;
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelDelimiter = new javax.swing.JLabel();
        labelPattern = new javax.swing.JLabel();
        comboBoxDelimiter = new javax.swing.JComboBox<>();
        comboBoxFormat = new javax.swing.JComboBox<>();
        textFieldCustomPattern = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        labelDelimiter.setLabelFor(comboBoxDelimiter);
        labelDelimiter.setText(org.openide.util.NbBundle.getMessage(DateFormatSettingPanel.class, "DateFormatSettingPanel.labelDelimiter.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelDelimiter, gridBagConstraints);

        labelPattern.setLabelFor(comboBoxFormat);
        labelPattern.setText(org.openide.util.NbBundle.getMessage(DateFormatSettingPanel.class, "DateFormatSettingPanel.labelPattern.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(labelPattern, gridBagConstraints);

        comboBoxDelimiter.setModel(new DefaultComboBoxModel<>(new String[]{"", "-", ":", ".", " ", "/", " | "}));
        comboBoxDelimiter.setRenderer(DELIMITER_RENDERER);
        comboBoxDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDelimiterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(comboBoxDelimiter, gridBagConstraints);

        comboBoxFormat.setModel(new DefaultComboBoxModel<>(DateFormatSelection.values()));
        comboBoxFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(comboBoxFormat, gridBagConstraints);

        textFieldCustomPattern.setEditable(false);
        textFieldCustomPattern.setColumns(5);
        textFieldCustomPattern.setToolTipText(org.openide.util.NbBundle.getMessage(DateFormatSettingPanel.class, "DateFormatSettingPanel.textFieldCustomPattern.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(textFieldCustomPattern, gridBagConstraints);
    }//GEN-END:initComponents

    private void comboBoxFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxFormatActionPerformed
        checkCustomFormat();
        firePropertyChange("dateFormatSelection", null, getDateFormatSelection());
    }//GEN-LAST:event_comboBoxFormatActionPerformed

    private void comboBoxDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDelimiterActionPerformed
        firePropertyChange("delimiter", null, getDelimiter());
    }//GEN-LAST:event_comboBoxDelimiterActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> comboBoxDelimiter;
    private javax.swing.JComboBox<DateFormatSelection> comboBoxFormat;
    private javax.swing.JLabel labelDelimiter;
    private javax.swing.JLabel labelPattern;
    private javax.swing.JTextField textFieldCustomPattern;
    // End of variables declaration//GEN-END:variables
}
