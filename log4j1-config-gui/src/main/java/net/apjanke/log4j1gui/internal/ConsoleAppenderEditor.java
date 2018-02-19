package net.apjanke.log4j1gui.internal;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class ConsoleAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(ConsoleAppenderEditor.class);

    private final ConsoleAppender appender;

    private final TargetComboBox targetComboBox = new TargetComboBox();
    private final JCheckBox followCheckBox = new JCheckBox();

    ConsoleAppenderEditor(ConsoleAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Target",   targetComboBox,
                "Follow",   followCheckBox,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        targetComboBox.setSelectedItem(appender.getTarget());
        followCheckBox.setSelected(appender.getFollow());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        //noinspection ConstantConditions
        appender.setTarget((String) targetComboBox.getSelectedItem());
        appender.setFollow(followCheckBox.isSelected());
    }

    class TargetComboBox extends JComboBox<String> {
        private final String[] validValues = new String[] {
                "System.out",
                "System.err",
        };

        TargetComboBox() {
            for (String str : validValues) {
                addItem(str);
            }
            String target = (appender == null) ? null : appender.getTarget();
            setSelectedItem(target);
        }
    }

}
