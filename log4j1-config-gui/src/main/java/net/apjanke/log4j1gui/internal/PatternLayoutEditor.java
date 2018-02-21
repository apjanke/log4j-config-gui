package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import javax.swing.*;
import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

/**
 * TODO: Maybe consider making CR/LF/control characters in the conversionPattern visible?
 */
class PatternLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(PatternLayoutEditor.class);

    private final PatternLayout layout;

    private JTextField patternField = new JTextField();

    PatternLayoutEditor(PatternLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        setLayout(new GridBagLayout());
        setBorder(SwingUtils.createEmptyBorderPx(20));
        setPreferredSize(px(new Dimension(1200,200)));

        GBC gbc = new GBC();

        patternField.setMinimumSize(px(new Dimension(600, SwingUtils.singleRowTextFieldHeight)));
        patternField.setPreferredSize(px(new Dimension(800, SwingUtils.singleRowTextFieldHeight)));

        Object[] arrangement = {
                "Pattern",      patternField,
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
        patternField.setText(layout.getConversionPattern());
    }

    @Override
    public void applyChanges() {
        layout.setConversionPattern(patternField.getText());
    }
}
