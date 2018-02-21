package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.EnhancedPatternLayout;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

/**
 * TODO: Maybe consider making CR/LF/control characters in the conversionPattern visible?
 */
class EnhancedPatternLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(PatternLayoutEditor.class);

    private final EnhancedPatternLayout layout;

    private final JTextField patternField = new JTextField();

    EnhancedPatternLayoutEditor(EnhancedPatternLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        patternField.setMinimumSize(px(new Dimension(600, SwingUtils.singleRowTextFieldHeight)));
        patternField.setPreferredSize(px(new Dimension(800, SwingUtils.singleRowTextFieldHeight)));

        Object[] arrangement = {
                "Pattern",      patternField,
        };
        addControlsFromArrangement(p, gbc, arrangement);

        refreshGuiThisLevel();
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        patternField.setText(layout.getConversionPattern());
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        layout.setConversionPattern(patternField.getText());
    }
}
