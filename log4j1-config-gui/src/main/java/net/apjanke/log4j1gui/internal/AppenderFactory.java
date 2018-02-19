package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Appender;

interface AppenderFactory {

    Iterable<Class<? extends Appender>> getSupportedAppenderClasses();

    Appender createAppender(Class<? extends Appender> appenderClass);

}
