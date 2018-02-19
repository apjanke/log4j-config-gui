package net.apjanke.log4j1gui.internal;

import org.apache.log4j.DailyRollingFileAppender;

import javax.swing.*;

class DailyRollingFileAppenderEditor extends FileAppenderEditor {

    private final DailyRollingFileAppender appender;

    private JTextField datePatternField;

    DailyRollingFileAppenderEditor(DailyRollingFileAppender appender) {
        super(appender);
        this.appender = appender;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        datePatternField = new JTextField();

        Object[] arrangement = {
                "Date Pattern", datePatternField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        datePatternField.setText(appender.getDatePattern());
    }

    @Override
    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setDatePattern(datePatternField.getText());
    }
}