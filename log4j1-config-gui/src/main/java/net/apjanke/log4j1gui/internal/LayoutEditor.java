package net.apjanke.log4j1gui.internal;

import org.apache.log4j.*;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.xml.XMLLayout;

import static java.util.Objects.requireNonNull;

public abstract class LayoutEditor extends ThingEditor {

    LayoutEditor(Layout layout) {
        super(layout);
        Layout layout1 = requireNonNull(layout);
    }

    public static LayoutEditor createEditorFor(Layout layout) {
        requireNonNull(layout);
        if (layout instanceof PatternLayout) {
            return new PatternLayoutEditor((PatternLayout) layout);
        } else if (layout instanceof EnhancedPatternLayout) {
            return new EnhancedPatternLayoutEditor((EnhancedPatternLayout) layout);
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
