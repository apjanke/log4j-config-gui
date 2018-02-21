package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.XMLLayout;

import javax.swing.*;

class XMLLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(XMLLayoutEditor.class);

    private final XMLLayout layout;

    private final JCheckBox locationInfoField = new JCheckBox();
    private final JCheckBox propertiesField = new JCheckBox();

    XMLLayoutEditor(XMLLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Location Info",        locationInfoField,
                "Properties",           propertiesField,
        };
        addControlsFromArrangement(p, gbc, arrangement);

        refreshGuiThisLevel();
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        locationInfoField.setSelected(layout.getLocationInfo());
        propertiesField.setSelected(layout.getProperties());
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        layout.setLocationInfo(locationInfoField.isSelected());
        layout.setProperties(propertiesField.isSelected());
    }
}
