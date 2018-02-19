package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.nt.NTEventLogAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class NTEventLogAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(ConsoleAppenderEditor.class);

    private final NTEventLogAppender appender;

    private JTextField sourceField;

    NTEventLogAppenderEditor(NTEventLogAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        sourceField = new JTextField();

        Object[] arrangement = {
                "Source",   sourceField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        sourceField.setText(appender.getSource());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setSource(sourceField.getText());
    }}
