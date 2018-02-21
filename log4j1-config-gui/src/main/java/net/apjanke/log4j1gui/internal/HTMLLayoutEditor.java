package net.apjanke.log4j1gui.internal;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

class HTMLLayoutEditor extends LayoutEditor {

    private static final Logger log = LogManager.getLogger(HTMLLayoutEditor.class);

    private final HTMLLayout layout;

    private final JTextField titleField = new JTextField();
    private final JCheckBox locationInfoField = new JCheckBox();

    HTMLLayoutEditor(HTMLLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Title",      titleField,
                "Location Info",    locationInfoField,
        };
        addControlsFromArrangement(p, gbc, arrangement);

        refreshGuiThisLevel();
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        titleField.setText(layout.getTitle());
        locationInfoField.setEnabled(layout.getLocationInfo());
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        layout.setTitle(titleField.getText());
        layout.setLocationInfo(locationInfoField.isSelected());
    }

}
