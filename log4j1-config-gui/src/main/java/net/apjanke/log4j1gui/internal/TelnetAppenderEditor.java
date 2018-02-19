package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.TelnetAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class TelnetAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(TelnetAppenderEditor.class);

    private final TelnetAppender appender;

    private final JTextField portField = new JTextField();

    private TelnetAppenderEditor(TelnetAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Port",     portField
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        portField.setText(""+appender.getPort());
    }

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
