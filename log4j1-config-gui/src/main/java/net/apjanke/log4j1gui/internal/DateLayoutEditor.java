package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.DateLayout;

import javax.swing.*;

class DateLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(DateLayoutEditor.class);

    private final DateLayout layout;

    private final JTextField dateFormatField = new JTextField();
    private final JTextField timeZoneField = new JTextField();

    DateLayoutEditor(DateLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        dateFormatField.setPreferredSize(textFieldPreferredSize);
        timeZoneField.setPreferredSize(textFieldPreferredSize);

        Object[] arrangement = {
                "Date Format",      dateFormatField,
                "Time Zone",        timeZoneField,
        };
        addControlsFromArrangement(p, gbc, arrangement);

        refreshGuiThisLevel();
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        dateFormatField.setText(layout.getDateFormat());
        timeZoneField.setText(layout.getTimeZone());
    }

    @Override
    void applyChanges() {
        super.applyChanges();
        layout.setDateFormat(dateFormatField.getText());
        layout.setTimeZone(timeZoneField.getText());
    }
}
