package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelMatchFilter;

import javax.swing.*;
import java.awt.*;

public class LevelMatchFilterEditor extends FilterEditor {

    private final LevelMatchFilter filter;

    private final LevelComboBox levelComboBox = new LevelComboBox();
    private final AcceptOnMatchComboBox acceptComboBox = new AcceptOnMatchComboBox();

    public LevelMatchFilterEditor(LevelMatchFilter filter) {
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
        add(new JLabel("Level:"), gbc);
        gbc.gridx = 1;
        Level level = Level.toLevel(filter.getLevelToMatch());
        levelComboBox.setSelectedItem(level);
        add(levelComboBox, gbc);
    }

    @Override
    public void applyChanges() {
        filter.setAcceptOnMatch(acceptComboBox.getSelectedValue());
        Level level = (Level) levelComboBox.getSelectedItem();
        String levelName = level == null ? null : level.toString();
        filter.setLevelToMatch(levelName);
    }
}
