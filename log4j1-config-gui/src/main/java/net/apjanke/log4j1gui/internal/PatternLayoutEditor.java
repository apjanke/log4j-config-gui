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
public class PatternLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(PatternLayoutEditor.class);

    private final PatternLayout layout;
    private JTextField patternField;

    PatternLayoutEditor(PatternLayout layout) {
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(px(30), px(30), px(30), px(30)));
        setMinimumSize(px(new Dimension(1200,200)));

        GridBagConstraints c = new GridBagConstraints();

        JLabel patternLabel = new JLabel("Pattern:");
        patternField = new JTextField();
        patternField.setMinimumSize(px(new Dimension(400, 20)));

        int row = 0;
        c.gridx = 0;
        c.gridy = row;
        add(patternLabel, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        add(patternField, c);

        patternField.setText(layout.getConversionPattern());
    }

    @Override
    public void applyChanges() {
        layout.setConversionPattern(patternField.getText());
    }
}
