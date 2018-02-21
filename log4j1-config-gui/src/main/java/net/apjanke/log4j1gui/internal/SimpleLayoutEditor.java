package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;


/**
 * This is an empty implementation, because there's nothing to edit on a
 * SimpleLayoutEditor.
 */
class SimpleLayoutEditor extends LayoutEditor {
    private static final Logger log = LogManager.getLogger(PatternLayoutEditor.class);

    SimpleLayoutEditor(SimpleLayout layout) {
        super(layout);
    }

}
