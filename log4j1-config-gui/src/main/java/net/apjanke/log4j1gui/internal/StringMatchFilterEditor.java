package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.StringMatchFilter;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class StringMatchFilterEditor extends FilterEditor {

    private static final Logger log = LogManager.getLogger(StringMatchFilterEditor.class);

    private final StringMatchFilter filter;

    private final JTextField stringField = new JTextField();
    private final AcceptOnMatchComboBox acceptComboBox = new AcceptOnMatchComboBox();

    public StringMatchFilterEditor(StringMatchFilter filter) {
        super(filter);
        this.filter = filter;
    }

    @Override
    public void initializeGui() {
        setLayout(new GridBagLayout());
        setBorder(SwingUtils.createEmptyBorderPx(20));

        GBC gbc = new GBC();
        add(new JLabel("Action on match:"), gbc);
        gbc.gridx = 1;
        acceptComboBox.setSelectedValue(filter.getAcceptOnMatch());
        add(acceptComboBox, gbc);
        gbc.nextRow();
        add(new JLabel("String:"), gbc);
        gbc.gridx = 1;
        stringField.setPreferredSize(px(new Dimension(400, SwingUtils.singleRowTextFieldHeight)));
        stringField.setText(filter.getStringToMatch());
        add(stringField, gbc);
        gbc.nextRow();
    }

    @Override
    public void applyChanges() {
        filter.setStringToMatch(stringField.getText());
        filter.setAcceptOnMatch(acceptComboBox.getSelectedValue());
    }
}
