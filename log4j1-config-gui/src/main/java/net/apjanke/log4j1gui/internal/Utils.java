package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Configuration;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class Utils {
    public static final Level[] ALL_LEVELS = new Level[]{
            Level.ALL,
            Level.TRACE,
            Level.DEBUG,
            Level.WARN,
            Level.INFO,
            Level.ERROR,
            Level.FATAL,
            Level.OFF,
    };

    /**
     * Gets all loggers from the LogManager. This method exists to concentrate
     * the Enumeration-to-Collection conversion in one place. (Doing it directly requires
     * unchecked-assignment-warning suppression to get it in a one-liner, and that's
     * gross.)
     * @return List of all the current loggers
     */
    public static java.util.List<Logger> getCurrentLoggers() {
        Enumeration loggers = LogManager.getCurrentLoggers();
        java.util.List<Logger> out = new ArrayList<>();
        while (loggers.hasMoreElements()) {
            Object obj = loggers.nextElement();
            if (obj instanceof Logger) {
                out.add((Logger) obj);
            } else {
                throw new InternalError(sprintf("Got a non-Logger object from LogManager.getCurrentLoggers(): %s",
                        obj.getClass()));
            }
        }
        return out;
    }

    /**
     * Gets all Appenders from a Logger. This method exists to concentrate
     * the Enumeration-to-Collection conversion in one place. (Doing it directly requires
     * unchecked-assignment-warning suppression to get it in a one-liner, and that's
     * gross.)
     * @return List of all the current loggers
     */
    public static java.util.List<Appender> getAllAppenders(Logger logger) {
        Enumeration appenders = logger.getAllAppenders();
        java.util.List<Appender> out = new ArrayList<>();
        while (appenders.hasMoreElements()) {
            Object obj = appenders.nextElement();
            if (obj instanceof Appender) {
                out.add((Appender) obj);
            } else {
                throw new InternalError(sprintf("Got a non-Appender object from Logger.getAllAppenders(): %s",
                        obj.getClass()));
            }
        }
        return out;
    }

    /**
     * Alias for String.format, because I just prefer saying "sprintf".
     * @param fmt Format specification string
     * @param args Arguments to be substituted in for conversions
     * @return Formatted string
     */
    public static String sprintf(String fmt, Object... args) {
        return String.format(fmt, args);
    }

    /**
     * Maybe scale a pixel size for HiDPI mode.
     * @param x non-scaled size in pixels
     * @return scaled size in pixels
     */
    public static int px(int x) {
        return Configuration.isUseHiDpiMode() ? x * 2 : x;
    }

    /**
     * Maybe scale a Dimension for HiDPI mode. Always creates a new Dimension object instead
     * of re-using the input.
     * @param in non-scaled Dimension
     * @return newly-constructed scaled Dimension.
     */
    public static Dimension px(Dimension in) {
        return Configuration.isUseHiDpiMode()
                ? new Dimension(in.width * 2, in.height * 2)
                : new Dimension(in.width, in.height);
    }

    private static final String[] LOG4J_PREFIXES_TO_STRIP = new String[] {
            "org.apache.log4j.extras.",
            "org.apache.log4j.varia.",
            "org.apache.log4j.",
    };

    public static String nameWithoutLog4jPackage(String str) {
        if (str == null) {
            return null;
        } else {
            for (String prefix: LOG4J_PREFIXES_TO_STRIP) {
                if (str.startsWith(prefix)) {
                    return str.substring(prefix.length());
                }
            }
            return str;
        }
    }

}
