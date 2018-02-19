package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class SocketAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(SocketAppenderEditor.class);

    private final SocketAppender appender;

    private JTextField applicationField;
    private JCheckBox locationInfoField;
    private JTextField portField;
    private JTextField reconnectionDelayField;
    private JTextField remoteHostField;

    SocketAppenderEditor(SocketAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        applicationField = new JTextField();
        locationInfoField = new JCheckBox();
        portField = new JTextField();
        reconnectionDelayField = new JTextField();
        remoteHostField = new JTextField();

        Object[] arrangement = {
                "Remote Host",  remoteHostField,
                "Port",         portField,
                "Application",  applicationField,
                "Location Info",    locationInfoField,
                "Reconnection Delay", reconnectionDelayField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        applicationField.setText(appender.getApplication());
        locationInfoField.setSelected(appender.getLocationInfo());
        portField.setText(""+appender.getPort());
        reconnectionDelayField.setText(""+appender.getReconnectionDelay());
        remoteHostField.setText(appender.getRemoteHost());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setApplication(applicationField.getText());
        appender.setLocationInfo(locationInfoField.isSelected());
        appender.setPort(Integer.parseInt(portField.getText()));
        appender.setReconnectionDelay(Integer.parseInt(reconnectionDelayField.getText()));
        appender.setRemoteHost(remoteHostField.getText());
    }

}
