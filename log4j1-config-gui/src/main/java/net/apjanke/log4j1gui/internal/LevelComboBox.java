package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Level;

import javax.swing.*;

import static net.apjanke.log4j1gui.internal.Utils.ALL_LEVELS;

public class LevelComboBox extends JComboBox<Level> {
    public LevelComboBox() {
        addItem(null);
        for (Level level : ALL_LEVELS) {
            addItem(level);
        }
    }
}