package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class SyslogAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(SyslogAppenderEditor.class);

    private final SyslogAppender appender;

    private final JTextField facilityField = new JTextField();
    private final JCheckBox facilityPrintingField = new JCheckBox();
    private final JCheckBox headerField = new JCheckBox();
    private final JTextField syslogHostField = new JTextField();

    SyslogAppenderEditor(SyslogAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Syslog Host",      syslogHostField,
                "Facility",         facilityField,
                "Facility Printing", facilityPrintingField,
                "Header",           headerField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        facilityField.setText(appender.getFacility());
        facilityPrintingField.setSelected(appender.getFacilityPrinting());
        headerField.setSelected(appender.getHeader());
        syslogHostField.setText(appender.getSyslogHost());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setFacility(facilityField.getText());
        appender.setFacilityPrinting(facilityPrintingField.isSelected());
        appender.setHeader(headerField.isSelected());
        appender.setSyslogHost(syslogHostField.getText());
    }

}
