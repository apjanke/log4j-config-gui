package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Configuration;
import org.apache.log4j.*;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.lf5.LF5Appender;
import org.apache.log4j.net.*;
import org.apache.log4j.nt.NTEventLogAppender;
import org.apache.log4j.receivers.rewrite.RewriteAppender;
import org.apache.log4j.varia.ExternallyRolledFileAppender;
import org.apache.log4j.varia.NullAppender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

public class StandardAppenderFactory implements AppenderFactory {
    private static final Logger log = LogManager.getLogger(StandardAppenderFactory.class);

    private static final String DEFAULT_PATTERN = "%-6r [%15.15t] %-5p %30.30c %x - %m%n";

    private static final List<Class<? extends Appender>> myAppenderClasses;
    static {
        List<Class<? extends Appender>> tmp = new ArrayList<>();
        tmp.add(ConsoleAppender.class);
        tmp.add(FileAppender.class);
        tmp.add(DailyRollingFileAppender.class);
        tmp.add(RollingFileAppender.class);
        tmp.add(ExternallyRolledFileAppender.class);
        tmp.add(JDBCAppender.class);
        // JMS is only available if JMS library is loaded (part of Java EE). Detect that.
        try {
            Class.forName("javax.jms.Connection");
            tmp.add(JMSAppender.class);
        } catch (ClassNotFoundException e) {
            log.debug("JMS not detected; not adding JMSAppender.");
        }
        tmp.add(LF5Appender.class);
        // RewriteAppender is only available if Log4j Extras is loaded
        try {
            Class.forName("org.apache.log4j.receivers.rewrite.RewriteAppender");
            tmp.add(RewriteAppender.class);
        } catch (ClassNotFoundException e) {
            log.debug(sprintf("Class %s not found; looks like Log4j Extras is absent. Skipping.",
                    "org.apache.log4j.receivers.rewrite.RewriteAppender"));
        }
        tmp.add(SMTPAppender.class);
        tmp.add(SocketAppender.class);
        tmp.add(SocketHubAppender.class);
        tmp.add(SyslogAppender.class);
        tmp.add(TelnetAppender.class);
        if (Configuration.isRunningOnWindows()) {
            tmp.add(NTEventLogAppender.class);
        }
        tmp.add(NullAppender.class);
        myAppenderClasses = Collections.unmodifiableList(tmp);
    }

    @Override
    public Iterable<Class<? extends Appender>> getSupportedAppenderClasses() {
        return myAppenderClasses;
    }

    @Override
    public Appender createAppender(Class<? extends Appender> appenderClass) {
        requireNonNull(appenderClass);
        if (appenderClass == NullAppender.class) {
            return new NullAppender();
        } else if (appenderClass == ConsoleAppender.class) {
            ConsoleAppender appender = new ConsoleAppender();
            appender.setLayout(new PatternLayout(DEFAULT_PATTERN));
            return appender;
        } else if (myAppenderClasses.contains(appenderClass)) {
            try {
                return appenderClass.getConstructor().newInstance();
            } catch (Exception e) {
                log.error(sprintf("Error while instantiating %s: %s",
                        appenderClass.getName(), e.getMessage()), e);
                throw new InternalError(sprintf("Error while instantiating %s: %s",
                        appenderClass.getName(), e.getMessage()));
            }
        } else {
            throw new IllegalArgumentException("StandardAppenderFactory does not support Appender type: "
                    + appenderClass.getName());
        }
    }
}
