package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.DateLayout;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class DateLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(DateLayoutEditor.class);

    private final DateLayout layout;

    private JTextField dateFormatField = new JTextField();
    private JTextField timeZoneField = new JTextField();

    DateLayoutEditor(DateLayout layout) {
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
                "Date Format",      dateFormatField,
                "Time Zone",        timeZoneField,
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
        dateFormatField.setText(layout.getDateFormat());
        timeZoneField.setText(layout.getTimeZone());
    }

    @Override
    public void applyChanges() {
        layout.setDateFormat(dateFormatField.getText());
        layout.setTimeZone(timeZoneField.getText());
    }
}
