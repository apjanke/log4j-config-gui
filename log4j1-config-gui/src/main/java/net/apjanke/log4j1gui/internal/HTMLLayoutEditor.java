package net.apjanke.log4j1gui.internal;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class HTMLLayoutEditor extends LayoutEditor {

    private static final Logger log = LogManager.getLogger(HTMLLayoutEditor.class);

    private final HTMLLayout layout;

    private JTextField titleField = new JTextField();
    private JCheckBox locationInfoField = new JCheckBox();

    HTMLLayoutEditor(HTMLLayout layout) {
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
                "Title",      titleField,
                "Location Info",    locationInfoField,
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
        titleField.setText(layout.getTitle());
        locationInfoField.setEnabled(layout.getLocationInfo());
    }

    @Override
    public void applyChanges() {
        layout.setTitle(titleField.getText());
        layout.setLocationInfo(locationInfoField.isSelected());
    }

}
