package net.apjanke.log4j1gui.internal;

import org.apache.log4j.*;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.lf5.LF5Appender;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SyslogAppender;
import org.apache.log4j.nt.NTEventLogAppender;
import org.apache.log4j.varia.*;

import static java.util.Objects.requireNonNull;

/**
 * An editor for an Appender.
 */
public abstract class AppenderEditor extends ThingEditor {
    private static final Logger log = LogManager.getLogger(AppenderEditor.class);

    protected final static Class[] KNOWN_FILTER_TYPES = {
            StringMatchFilter.class,
            LevelMatchFilter.class,
            LevelRangeFilter.class,
            DenyAllFilter.class,
    };

    protected static final Class[] KNOWN_APPENDER_TYPES = {
            NullAppender.class,
            ConsoleAppender.class,
            FileAppender.class,
    };

    abstract void initializeGui();

    abstract void applyChanges();

    AppenderEditor(Appender appender) {
        super(requireNonNull(appender));
    }

    public static AppenderEditor createEditorFor(Appender appender) throws UnrecognizedAppenderException {
        requireNonNull(appender);
        Class<? extends Appender> appClass = appender.getClass();
        if (appClass == NullAppender.class) {
            return new NullAppenderEditor((NullAppender) appender);
        } else if (appClass == ConsoleAppender.class) {
            return new ConsoleAppenderEditor((ConsoleAppender) appender);
        } else if (appClass == FileAppender.class) {
            return new FileAppenderEditor((FileAppender) appender);
        } else if (appClass == WriterAppender.class) {
            return new WriterAppenderEditor((WriterAppender) appender);
        } else if (appClass == RollingFileAppender.class) {
            return new RollingFileAppenderEditor((RollingFileAppender) appender);
        } else if (appClass == DailyRollingFileAppender.class) {
            return new DailyRollingFileAppenderEditor((DailyRollingFileAppender) appender);
        } else if (appClass == ExternallyRolledFileAppender.class) {
            return new ExternallyRolledFileAppenderEditor((ExternallyRolledFileAppender) appender);
        } else if (appClass == JDBCAppender.class) {
            return new JDBCAppenderEditor((JDBCAppender) appender);
        } else if (appClass == LF5Appender.class) {
            return new LF5AppenderEditor((LF5Appender) appender);
        } else if (appClass == NTEventLogAppender.class) {
            return new NTEventLogAppenderEditor((NTEventLogAppender) appender);
        } else if (appClass == SMTPAppender.class) {
            return new SMTPAppenderEditor((SMTPAppender) appender);
        } else if (appClass == SocketAppender.class) {
            return new SocketAppenderEditor((SocketAppender) appender);
        } else if (appClass == SyslogAppender.class) {
            return new SyslogAppenderEditor((SyslogAppender) appender);
        } else {
            throw new UnrecognizedAppenderException("No appender editor defined for class " + appender.getClass().getName());
        }
    }

    public static class UnrecognizedAppenderException extends Exception {
        UnrecognizedAppenderException(String message) {
            super(message);
        }
    }

}
