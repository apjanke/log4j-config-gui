package net.apjanke.log4j1gui.internal;

import org.apache.log4j.varia.NullAppender;

import static java.util.Objects.requireNonNull;

/**
 * This is just an empty subclass; there's no configuration you can do on an
 * NullAppender.
 */
class NullAppenderEditor extends AppenderSkeletonEditor {

    public NullAppenderEditor(NullAppender appender) {
        super(requireNonNull(appender));
    }

}
