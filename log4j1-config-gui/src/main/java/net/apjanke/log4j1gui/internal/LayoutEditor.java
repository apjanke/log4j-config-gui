package net.apjanke.log4j1gui.internal;

import org.apache.log4j.*;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.xml.XMLLayout;

import javax.swing.*;

import java.awt.*;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.px;

public abstract class LayoutEditor extends ThingEditor {
    private static final Logger log = LogManager.getLogger(LayoutEditor.class);

    private final Layout layout;

    private final JLabel contentTypeField = new JLabel();
    private final JTextArea headerField = new JTextArea();
    private final JLabel footerField = new JLabel();

    /**
     * The subpane containing control widgets that are label/value pairs. This has a
     * GridBagLayout, and is 2 columns wide. Subclasses may add additional components
     * to it.
     */
    JComponent controlPane;

    /**
     * The GBC for controlPane. This will be left in the state it was in after adding
     * all of AppenderSkeleton's control widgets, so subclasses can pick it up and use it.
     */
    GBC controlPaneGBC;

    LayoutEditor(Layout layout) {
        super(layout);
        this.layout = requireNonNull(layout);
    }

    @Override
    void initializeGui() {
        setLayout(new GridBagLayout());
        setBorder(SwingUtils.createEmptyBorderPx(20));

        GBC gbc = new GBC();

        headerField.setEditable(false);
        headerField.setBackground(getBackground());

        Object[] arrangement = {
                "Content Type",     contentTypeField,
                "Header",           headerField,
                "Footer",           footerField,
        };
        JComponent p = this;
        addControlsFromArrangement(p, gbc, arrangement);

        controlPane = p;
        controlPaneGBC = gbc;

        refreshGuiThisLevel();
    }

    void refreshGui() {
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        contentTypeField.setText(layout.getContentType());
        headerField.setText(layout.getHeader());
        footerField.setText(layout.getFooter());
    }

    @Override
    void applyChanges() {
        // NOP at this level
    }

    public static LayoutEditor createEditorFor(Layout layout) {
        requireNonNull(layout);
        if (layout instanceof PatternLayout) {
            return new PatternLayoutEditor((PatternLayout) layout);
        } else if (layout.getClass().getName().equals("org.apache.log4j.EnhancedPatternLayout")) {
            return new EnhancedPatternLayoutEditor((EnhancedPatternLayout) layout);
        } else if (layout instanceof TTCCLayout) {
            return new TTCCLayoutEditor((TTCCLayout) layout);
        } else if (layout instanceof DateLayout) {
            return new DateLayoutEditor((DateLayout) layout);
        } else if (layout instanceof HTMLLayout) {
            return new HTMLLayoutEditor((HTMLLayout) layout);
        } else if (layout instanceof SimpleLayout) {
            return new SimpleLayoutEditor((SimpleLayout) layout);
        } else if (layout instanceof XMLLayout) {
            return new XMLLayoutEditor((XMLLayout) layout);
        } else {
            throw new UnsupportedOperationException("No layout editor defined for class "+layout.getClass().getName());
        }
    }

    private static class UnsupportedLayoutException extends Exception {
        UnsupportedLayoutException(String message) {
            super(message);
        }
    }

}
