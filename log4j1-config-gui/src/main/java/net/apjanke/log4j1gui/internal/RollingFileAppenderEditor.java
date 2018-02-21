package net.apjanke.log4j1gui.internal;

import org.apache.log4j.RollingFileAppender;

import javax.swing.*;

class RollingFileAppenderEditor extends FileAppenderEditor {
    private final RollingFileAppender appender;

    private JTextField maxBackupIndexField = new JTextField();
    private JTextField maximumFileSizeField = new JTextField();

    RollingFileAppenderEditor(RollingFileAppender appender) {
        super(appender);
        this.appender = appender;
    }

    @Override
    void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        maxBackupIndexField.setPreferredSize(smallTextFieldPreferredSize);
        maximumFileSizeField.setPreferredSize(smallTextFieldPreferredSize);
        maxBackupIndexField.setInputVerifier(new SwingUtils.IntegerInputVerifier());
        maximumFileSizeField.setInputVerifier(new SwingUtils.IntegerInputVerifier());

        Object[] arrangement = {
                "Max Backup Index",     maxBackupIndexField,
                "Maximum File Size",    maximumFileSizeField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        maxBackupIndexField.setText(""+appender.getMaxBackupIndex());
        maximumFileSizeField.setText(""+appender.getMaximumFileSize());
    }

    @Override
    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    void applyChanges() {
        super.applyChanges();
        appender.setMaxBackupIndex(Integer.parseInt(maxBackupIndexField.getText()));
        appender.setMaximumFileSize(Integer.parseInt(maximumFileSizeField.getText()));
    }

}
