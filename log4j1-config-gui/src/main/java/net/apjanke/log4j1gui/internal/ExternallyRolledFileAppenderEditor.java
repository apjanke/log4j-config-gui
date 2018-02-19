package net.apjanke.log4j1gui.internal;

import org.apache.log4j.varia.ExternallyRolledFileAppender;

import javax.swing.*;

class ExternallyRolledFileAppenderEditor extends RollingFileAppenderEditor {
    private final ExternallyRolledFileAppender appender;

    private JTextField portField;

    ExternallyRolledFileAppenderEditor(ExternallyRolledFileAppender appender) {
        super(appender);
        this.appender = appender;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        portField = new JTextField();

        p.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;  gbc.weightx = 1;
        p.add(portField, gbc);
        gbc.nextRow();  gbc.weightx = 0;
    }

    private void refreshGuiThisLevel() {
        portField.setText(""+appender.getPort());
    }

    @Override
    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setPort(Integer.parseInt(portField.getText()));
    }

}
