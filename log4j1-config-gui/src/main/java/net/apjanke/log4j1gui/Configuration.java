package net.apjanke.log4j1gui;

import java.awt.*;

/**
 * Global configuration for log4j1-config-gui.
 *
 * This contains settings that affect the behavior of all log4j1-config-gui components.
 * Settings are picked up at object creation time, so do your configuration on
 * Configuration before you create a Log4jConfiguratorGui or other objects.
 */
@SuppressWarnings("WeakerAccess")
public class Configuration {

    private static final int HIDPI_SCREEN_WIDTH_THRESHOLD = 2048;

    private static boolean useHiDpiMode = guessHiDpiMode();

    /**
     * Heuristic to detect HiDPI mode on Windows and Linux
     *
     * @return boolean Whether it looks like this machine is running on a HiDPI display
     */
    public static boolean guessHiDpiMode() {
        if (isRunningOnMac()) {
            // Java on Mac seems to handle auto-upscaling okay
            return false;
        }
        // Just assume big screens are HiDPI
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.width >= HIDPI_SCREEN_WIDTH_THRESHOLD;
    }


    /**
     * Gets whether log4j1-config-gui is using HiDPI mode. HiDPI mode scales components
     * sizes so they look right on Windows or Linux systems running high-resolution
     * displays.
     * @return Whether this configuration is using HiDPI mode
     */
    public static boolean isUseHiDpiMode() {
        return useHiDpiMode;
    }

    /**
     * Sets useHiDpiMode. This allows you to override this class's detection heuristics and
     * force it to use a particular mode.
     *
     * @param newUseHiDpiMode Whether to use HiDPI mode
     */
    public static void setUseHiDpiMode(boolean newUseHiDpiMode) {
        useHiDpiMode = newUseHiDpiMode;
    }

    public static boolean isRunningOnMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }

    public static boolean isRunningOnWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    private static Boolean log4jExtrasDetected = null;

    /**
     * Whether this Java program is running with Apache Log4j Extras on the classpath.
     *
     * (This is determined by whether an example class from the Extras package can be instantiated.)
     *
     * @return whether it looks like Log4j Extras is present
     */
    public static boolean hasLog4jExtras() {
        if (null == log4jExtrasDetected) {
            try {
                // Class known to exist in Log4j extras
                String probeClassName = "org.apache.log4j.EnhancedPatternLayout";
                Class.forName(probeClassName);
                log4jExtrasDetected = true;
            } catch (Exception e) {
                log4jExtrasDetected = false;
            }
        }
        return log4jExtrasDetected;
    }



}
