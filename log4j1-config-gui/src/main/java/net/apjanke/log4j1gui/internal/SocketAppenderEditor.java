package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class SocketAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(SocketAppenderEditor.class);

    private final SocketAppender appender;

    private JTextField applicationField = new JTextField();
    private JCheckBox locationInfoField = new JCheckBox();
    private JTextField portField = new JTextField();
    private JTextField reconnectionDelayField = new JTextField();
    private JTextField remoteHostField = new JTextField();

    SocketAppenderEditor(SocketAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        portField.setInputVerifier(new SwingUtils.IntegerInputVerifier());
        reconnectionDelayField.setInputVerifier(new SwingUtils.IntegerInputVerifier());

        for (JComponent c : new JComponent[] {
                applicationField, portField, reconnectionDelayField, remoteHostField
        }) {
            c.setPreferredSize(smallTextFieldPreferredSize);
        }

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
