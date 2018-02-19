package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;

import javax.swing.*;
import java.awt.*;

public class LevelRangeFilterEditor extends FilterEditor {

    private final LevelRangeFilter filter;

    private final LevelComboBox minLevelComboBox = new LevelComboBox();
    private final LevelComboBox maxLevelComboBox = new LevelComboBox();
    private final AcceptOnMatchComboBox acceptComboBox = new AcceptOnMatchComboBox();

    public LevelRangeFilterEditor(LevelRangeFilter filter) {
        super(filter);
        this.filter = filter;
    }

    @Override
    public void initializeGui() {
        setLayout(new GridBagLayout());

        GBC gbc = new GBC();
        add(new JLabel("Action on match:"), gbc);
        gbc.gridx = 1;
        acceptComboBox.setSelectedValue(filter.getAcceptOnMatch());
        add(acceptComboBox, gbc);
        gbc.nextRow();
        add(new JLabel("Minimum Level:"), gbc);
        gbc.gridx = 1;
        minLevelComboBox.setSelectedItem(filter.getLevelMin());
        add(minLevelComboBox, gbc);
        gbc.nextRow();
        add(new JLabel("Maximum Level:"), gbc);
        gbc.gridx = 1;
        maxLevelComboBox.setSelectedItem(filter.getLevelMax());
        add(maxLevelComboBox, gbc);
    }

    @Override
    public void applyChanges() {
        filter.setAcceptOnMatch(acceptComboBox.getSelectedValue());
        filter.setLevelMin((Level) minLevelComboBox.getSelectedItem());
        filter.setLevelMax((Level) maxLevelComboBox.getSelectedItem());
    }
}