package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.XMLLayout;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class XMLLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(XMLLayoutEditor.class);

    private final XMLLayout layout;

    private JCheckBox locationInfoField = new JCheckBox();
    private JCheckBox propertiesField = new JCheckBox();

    XMLLayoutEditor(XMLLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        setLayout(new GridBagLayout());
        setBorder(SwingUtils.createEmptyBorderPx(20));
        setPreferredSize(px(new Dimension(1200,200)));

        GBC gbc = new GBC();

        Object[] arrangement = {
                "Location Info",        locationInfoField,
                "Properties",           propertiesField,
        };
        JComponent p = this;
        for (int i = 0; i < arrangement.length; i+=2) {
            p.add(new JLabel(arrangement[i] +":"), gbc);
            gbc.gridx = 1;  gbc.weightx = 1;
            p.add(((JComponent)arrangement[i+1]), gbc);
            gbc.nextRow(); gbc.weightx = 0;
        }

        refreshGuiThisLevel();
    }

    private void refreshGui() {
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        locationInfoField.setSelected(layout.getLocationInfo());
        propertiesField.setSelected(layout.getProperties());
    }

    @Override
    public void applyChanges() {
        layout.setLocationInfo(locationInfoField.isSelected());
        layout.setProperties(propertiesField.isSelected());
    }
}
