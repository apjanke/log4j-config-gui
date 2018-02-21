package net.apjanke.log4j1gui;

import net.apjanke.log4j1gui.internal.Utils;
import org.apache.log4j.*;
import org.apache.log4j.varia.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static net.apjanke.log4j1gui.internal.Utils.sprintf;

@SuppressWarnings("WeakerAccess")
public class Log4jConfiguratorGuiDemo {
    private static final Logger log = LogManager.getLogger(Log4jConfiguratorGuiDemo.class);

    private Log4jConfiguratorGui gui;

    public static void main(String[] args) {
        setUpExampleLoggers();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Log4jConfiguratorGuiDemo().initializeGui();
            }
        });
    }

    private Log4jConfiguratorGuiDemo() {

    }

    private void initializeGui() {
        gui = new Log4jConfiguratorGui();
        gui.initializeGui();
        JFrame frame = gui.showInFrame();
        JMenuBar menuBar = frame.getJMenuBar();
        JMenu demoMenu = new JMenu("Demo");
        menuBar.add(demoMenu);
        final int[] counts = { 10, 100, 10000 };
        JMenu generateMessagesMenu = new JMenu("Generate Messages");
        for (final int n : counts) {
            JMenuItem generateMessagesItem = new JMenuItem("" + n);
            generateMessagesItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateRandomMessages(n);
                }
            });
            generateMessagesMenu.add(generateMessagesItem);
        }
        demoMenu.add(generateMessagesMenu);
        JMenu generateLoggersMenu = new JMenu("Generate Loggers");
        for (final int n : counts) {
            JMenuItem generateLoggersItem = new JMenuItem("" + n);
            generateLoggersItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateRandomLoggers(n);
                }
            });
            generateLoggersMenu.add(generateLoggersItem);
        }
        demoMenu.add(generateLoggersMenu);

        String logoResourcePath = "Apache-Log4j-transparent.png";
        try {
            Image image = ImageIO.read(Log4jConfiguratorGuiDemo.class.getResource(logoResourcePath));
            frame.setIconImage(image);
            if (isRunningOnMac()) {
                setMacDockIcon(image);
            }
        } catch (Exception err) {
            log.error(String.format("Failed loading logo as resource '%s': %s", logoResourcePath, err.getMessage()));
        }
        frame.setVisible(true);
    }

    private static int randomPositiveInt(int maxVal) {
        return (int) Math.round(Math.random() * maxVal);
    }

    @SuppressWarnings("SameParameterValue")
    private static long randomPositiveLong(long maxVal) {
        return Math.round(Math.random() * maxVal);
    }

    private void generateRandomMessages(int nMessages) {
        @SuppressWarnings("unchecked") java.util.List<Logger> allLoggers = Collections.list(LogManager.getCurrentLoggers());
        java.util.List<Logger> fooLoggers = new ArrayList<>();
        for (Logger logger: allLoggers) {
            if (logger.getName().startsWith("foo.")) {
                fooLoggers.add(logger);
            }
        }
        for (int i = 0; i < nMessages; i++) {
            Logger logger = fooLoggers.get(randomPositiveInt(fooLoggers.size()-1));
            Level level = Utils.ALL_LEVELS[randomPositiveInt(Utils.ALL_LEVELS.length-1)];
            int randomVal = (int) Math.round(Math.random() * Integer.MAX_VALUE);
            logger.log(level, sprintf("Hello, world! Value=%d", randomVal));
        }
    }

    private void generateRandomLoggers(int nLoggers) {
        for (int i = 0; i < nLoggers; i++) {
            long val = randomPositiveLong(Long.MAX_VALUE);
            String loggerName = sprintf("foo.bar.random.Logger%d", val);
            getLog(loggerName);
        }
        gui.refreshGui();
    }

    /**
     * Hack to set Dock icon image on Mac.
     * @param image image to display in Dock.
     */
    private static void setMacDockIcon(Image image) {
        try {
            Class<?> appClass = Class.forName("com.apple.eawt.Application");
            Object application = appClass.getConstructor().newInstance().getClass().getMethod("getApplication")
                    .invoke(null);
            application.getClass().getMethod("setDockIconImage", java.awt.Image.class)
                    .invoke(application, image);
        } catch (Exception err) {
            log.error(String.format("Error while attempting to set macOS DockImage to %s: %s", image, err.getMessage()));
        }
    }

    private static Logger getLog(String name) {
        return LogManager.getLogger(name);
    }

    private static void setUpExampleLoggers() {
        org.apache.log4j.BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.INFO);
        // Flesh out the logger hierarchy with some dummy loggers
        String[] loggerNames = new String[] {
                "foo.bar",
                "foo.bar.baz.qux",
                "foo.bar.db.Connection",
                "foo.bar.db.DatabaseManager",
                "foo.bar.db.Statement",
                "foo.gui.SomeGui",
                "foo.gui.AnotherGui"
        };
        for (String loggerName: loggerNames) {
            LogManager.getLogger(loggerName);
        }
        getLog("foo.bar.db.Connection").setLevel(Level.DEBUG);
        // Add example loggers using specific features
        EnhancedPatternLayout myLayout = new EnhancedPatternLayout("%-6r [%15.15t] %-5p %30.30c %x - %m%n");
        Logger lg = getLog("foo.bar.enhancedpattern.SomeClass");
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(myLayout);
        lg.addAppender(consoleAppender);
        lg = getLog("foo.bar.withfilters.Filter1");
        Appender appender = new ConsoleAppender();
        appender.setLayout(new PatternLayout("Hello: %-6r [%15.15t] %-5p %30.30c %x - %m%n"));
        StringMatchFilter stringMatchFilter = new StringMatchFilter();
        stringMatchFilter.setStringToMatch("Hello, world!");
        appender.addFilter(stringMatchFilter);
        LevelMatchFilter levelMatchFilter = new LevelMatchFilter();
        levelMatchFilter.setLevelToMatch("DEBUG");
        appender.addFilter(levelMatchFilter);
        LevelRangeFilter levelRangeFilter = new LevelRangeFilter();
        levelRangeFilter.setLevelMin(Level.TRACE);
        levelRangeFilter.setLevelMax(Level.INFO);
        appender.addFilter(levelRangeFilter);
        appender.addFilter(new DenyAllFilter());
        lg.addAppender(appender);
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFile = tempDir + "/log4j-config-gui.tmp";
        String tempFile2 = tempDir + "/log4j-config-gui-daily-roll.tmp";
        lg = getLog("foo.bar.tofile.SomeClass");
        try {
            FileAppender fa = new FileAppender(myLayout, tempFile, true, true, 2048);
            lg.addAppender(fa);
        } catch (IOException e) {
            // Log it and ignore
            log.debug(sprintf("IO Error when setting up FileAppender temp file logger at '%s': %s",
                    tempFile, e.getMessage()), e);
        }
        lg = getLog("foo.bar.tofile.SomeClassWithDailyRoll");
        try {
            DailyRollingFileAppender drfa = new DailyRollingFileAppender(myLayout,
                    tempFile2, "'.'yyyy-MM-dd");
            lg.addAppender(drfa);
        } catch (IOException e) {
            // Log it and ignore
            log.debug(sprintf("IO Error when setting up DailyRollingFileAppender temp file logger at '%s': %s",
                    tempFile2, e.getMessage()), e);
        }
        lg = getLog("foo.bar.tofile.SomeClassWithExternalRoll");
        String tempFile3 = tempDir + "/log4j-config-gui-external-roll.tmp";
        ExternallyRolledFileAppender erfa = new ExternallyRolledFileAppender();
        erfa.setFile(tempFile3);
        lg.addAppender(erfa);
        lg = getLog("foo.bar.with.ttcc.SomeClass");
        appender = new ConsoleAppender();
        appender.setLayout(new TTCCLayout());
        lg.addAppender(appender);
    }

    private static boolean isRunningOnMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }
}
