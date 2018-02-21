package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import javax.swing.*;

class PriorityComboBox extends JComboBox<Priority> {

    private static final Logger log = LogManager.getLogger(PriorityComboBox.class);

    private static final Priority[] ALL_PRIORITIES = {
            Level.ALL,
            Level.TRACE,
            Level.DEBUG,
            Level.INFO,
            Level.WARN,
            Level.ERROR,
            Level.FATAL,
            Level.OFF,
    };

    PriorityComboBox() {
        addItem(null);
        for (Priority priority: ALL_PRIORITIES) {
            addItem(priority);
        }
    }

}
