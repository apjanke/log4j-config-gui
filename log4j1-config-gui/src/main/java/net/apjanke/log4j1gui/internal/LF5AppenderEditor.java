package net.apjanke.log4j1gui.internal;

import org.apache.log4j.lf5.LF5Appender;

/**
 * This is just an empty subclass; there's no configuration you can do on an
 * LF5Appender.
 */
class LF5AppenderEditor extends AppenderSkeletonEditor {

    LF5AppenderEditor(LF5Appender appender) {
        super(appender);
    }

}
