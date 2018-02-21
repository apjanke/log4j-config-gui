package net.apjanke.log4j1gui;

import net.apjanke.log4j1gui.internal.*;
import org.apache.log4j.*;
import org.apache.log4j.lf5.LF5Appender;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

/**
 * A configurator GUI for Apache log4j 1.2.
 * <p>
 * This is implemented as a JPanel so you can embed it in other GUI components. But don't
 * call any of the regular JPanel methods on it; this class takes full responsibility for
 * its own internal arrangement. Treat this class as an opaque JComponent.
 * <p>
 * As a convenience, you can call showInFrame() to pop this GUI up in a new JFrame.
 */
@SuppressWarnings("WeakerAccess")
public class Log4jConfiguratorGui extends JPanel {

    private final static Logger log = LogManager.getLogger(Log4jConfiguratorGui.class);

    private static final int LEVEL_COLUMN = 1;
    private static final int ADDITIVITY_COLUMN = 2;

    private MenuBar menuBar;
    private List<? extends Image> iconImages = null;
    private JTable table;
    private LoggerListTableModel tableModel;
    private List<Logger> loggersForRows;
    private JPopupMenu popupMenu;
    private final java.util.List<JComponent> thingsNeedingLoggerSelection = new ArrayList<>();

    public void initializeGui() {
        MyWidgetSizes sizes = new MyWidgetSizes();

        // Set up widgets
        setLayout(new BorderLayout());
        setBorder(SwingUtils.createEmptyBorderPx(10));
        JLabel topLabel = new JLabel("Loggers");
        topLabel.setBorder(SwingUtils.createEmptyBorderPx(0, 5, 10, 5));
        add(topLabel, BorderLayout.NORTH);
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(SwingUtils.createEmptyBorderPx(5));
        add(tableScrollPane, BorderLayout.CENTER);
        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new MyPopupMenuListener());
        JMenuItem editLoggerItem = new JMenuItem("Edit Logger");
        editLoggerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedLogger();
            }
        });
        thingsNeedingLoggerSelection.add(editLoggerItem);
        popupMenu.add(editLoggerItem);
        JMenuItem editAppendersItem = new JMenuItem("Edit Appenders");
        editAppendersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editAppendersForSelectedLogger();
            }
        });
        thingsNeedingLoggerSelection.add(editAppendersItem);
        popupMenu.add(editAppendersItem);
        table.setComponentPopupMenu(popupMenu);

        tableModel = new LoggerListTableModel();
        table.setModel(tableModel);

        // Column sizing and cell renderer setting has to be done after initial model setting
        TableColumnModel colModel = table.getColumnModel();
        sizes.scaleForHiDpiMode();
        colModel.getColumn(1).setCellEditor(new DefaultCellEditor(new LevelComboBox()));
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            if (sizes.colPreferredWidths[i] != -1) {
                colModel.getColumn(i).setPreferredWidth(sizes.colPreferredWidths[i]);
            }
            if (sizes.colMaxWidths[i] != -1) {
                colModel.getColumn(i).setMaxWidth(sizes.colMaxWidths[i]);
            }
        }
        table.getColumnModel().getColumn(3).setCellRenderer(new AppenderListCellRenderer());
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                updateItemEnabling();
            }
        });

        menuBar = new MenuBar();
        refreshGui();
    }

    private void updateItemEnabling() {
        int row = table.getSelectedRow();
        boolean hasSelection = row != -1;
        for (JComponent c : thingsNeedingLoggerSelection) {
            c.setEnabled(hasSelection);
        }
    }

    private void editSelectedLogger() {
        // We can do this because we made right-click always select a row
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        Logger selectedLogger = loggersForRows.get(row);
        LoggerEditor loggerEditor = new LoggerEditor(selectedLogger);
        JDialog dialog = loggerEditor.showInModalDialog();
        dialog.setVisible(true);
        refreshGui();
    }

    private void editAppendersForSelectedLogger() {
        // We can do this because we made right-click always select a row
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        AppendersEditor appendersEditor = new AppendersEditor(loggersForRows.get(row));
        appendersEditor.initializeGui();
        JDialog dialog = appendersEditor.showInModalDialog();
        dialog.setVisible(true);
        refreshGui();
    }

    private void addNewLogger() {
        String newLoggerName = JOptionPane.showInputDialog("New logger name:");
        if (null == newLoggerName) {
            return;
        }
        LogManager.getLogger(newLoggerName);
        refreshGui();
    }

    private class MenuBar extends JMenuBar {
        private MenuBar() {
            JMenu loggerMenu = new JMenu("Logger");
            add(loggerMenu);
            JMenuItem newLoggerItem = new JMenuItem("New...");
            newLoggerItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNewLogger();
                }
            });
            loggerMenu.add(newLoggerItem);
            JMenuItem editLoggerItem = new JMenuItem("Edit");
            editLoggerItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editSelectedLogger();
                }
            });
            thingsNeedingLoggerSelection.add(editLoggerItem);
            loggerMenu.add(editLoggerItem);
            JMenu viewMenu = new JMenu("View");
            add(viewMenu);
            JMenuItem refreshMenuItem = new JMenuItem("Refresh");
            refreshMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshGui();
                }
            });
            viewMenu.add(refreshMenuItem);
            JMenu toolsMenu = new JMenu("Tools");
            add(toolsMenu);
            JMenuItem attachLF5Item = new JMenuItem("Attach LF5 to Root Logger");
            attachLF5Item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    attachLF5MonitorToRootLogger();
                }
            });
            toolsMenu.add(attachLF5Item);
            JMenuItem resetConfigurationItem = new JMenuItem("Reset Log4j Configuration");
            resetConfigurationItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LogManager.resetConfiguration();
                    refreshGui();
                }
            });
            toolsMenu.add(resetConfigurationItem);
            JMenu windowMenu = new JMenu("Window");
            add(windowMenu);
            JMenuItem LF5MenuItem = new JMenuItem("Show LF5 Monitor");
            LF5MenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LF5Appender lf5Appender = new LF5Appender();
                    lf5Appender.getLogBrokerMonitor().show();
                }
            });
            windowMenu.add(LF5MenuItem);

        }
    }

    private JMenuBar getMenuBar() {
        return menuBar;
    }

    public void refreshGui() {
        List<Logger> loggers = new ArrayList<>();
        loggers.add(LogManager.getRootLogger());
        //noinspection unchecked
        List<Logger> currentLoggers = Utils.getCurrentLoggers();
        Collections.sort(currentLoggers, new LoggerComparator());
        loggers.addAll(currentLoggers);
        loggersForRows = loggers;
        tableModel.setLoggers(loggersForRows);
        updateItemEnabling();
    }

    private void attachLF5MonitorToRootLogger() {
        Logger rootLogger = LogManager.getRootLogger();
        List<Appender> allAppenders = Utils.getAllAppenders(rootLogger);
        for (Appender appender: allAppenders) {
            if (appender instanceof LF5Appender) {
                // It's already attached
                return;
            }
        }
        rootLogger.addAppender(new LF5Appender());
    }

    private class PopupMenuItemsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.info(sprintf("Action performed: %s", e));
        }
    }

    private class MyPopupMenuListener implements PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
                    if (rowAtPoint > -1) {
                        table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    }
                }
            });
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // NOP
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            // NOP
        }
    }

    private static class AppenderListCellRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value) {
            if (null == value) {
                setText("");
                return;
            }
            try {
                if (!(value instanceof List)) {
                    log.error(sprintf("Got unexpected type for cell value: %s", value.getClass().getName()));
                    return;
                }
                @SuppressWarnings("unchecked") List<Appender> appenders = (List<Appender>) value;
                if (appenders.isEmpty()) {
                    setText("");
                    return;
                }
                List<String> strs = new ArrayList<>();
                for (Appender appender : appenders) {
                    if (appender instanceof ConsoleAppender) {
                        ConsoleAppender ca = (ConsoleAppender) appender;
                        strs.add("ConsoleAppender: " + layoutString(ca.getLayout()));
                    } else {
                        strs.add(nameWithoutLog4jPackage("" + appender));
                    }
                }
                setText("" + (strs.isEmpty() ? "" : (strs.size() == 1 ? strs.get(0) : strs)));
            } catch (Exception e) {
                log.error(sprintf("Error rendering Appender list: %s", e.getMessage()), e);
            }
        }
    }

    /**
     * A compact, human-readable representation of a Layout. This will include the pattern strings
     * for Pattern-related layouts.
     * @param layout The layout to construct a display string for
     * @return The display string for that Layout, non-null
     */
    public static String layoutString(Layout layout) {
        if (null == layout) {
            return "";
        }
        String str;
        if (layout instanceof PatternLayout) {
            PatternLayout pl = (PatternLayout) layout;
            str = sprintf("\"%s\" (<PL>)",  pl.getConversionPattern());
        } else if (layout instanceof EnhancedPatternLayout) {
            EnhancedPatternLayout epl = (EnhancedPatternLayout) layout;
            str = sprintf("\"%s\" (<EPL>)", epl.getConversionPattern());
        } else {
            str = ""+layout;
        }
        return str;
    }

    /**
     * Sets the icon images to be used for all windows generated by this GUI. If not set,
     * the default platform images will be used.
     *
     * @param iconImages the list of icon images to be displayed, or null to revert to using the default images.
     */
    public void setIconImages(List<? extends Image> iconImages) {
        this.iconImages = iconImages;
    }

    /**
     * Sets the single icon image to be used for all windows generated by this GUI. This is a
     * convenience method you can call instead of setIconImages().
     *
     * @param iconImage The image to be displayed, or null to revert to using the default image
     */
    public void setIconImage(Image iconImage) {
        if (null == iconImage) {
            this.iconImages = null;
        } else {
            List<Image> images = new ArrayList<>();
            images.add(iconImage);
            this.iconImages = images;
        }
    }

    private static class LoggerListTableModel extends AbstractTableModel {

        private static final String[] COLUMN_NAMES = new String[] {
                "Name", "Level", "Additivity", "Appenders"
        };

        private List<Logger> loggers;

        LoggerListTableModel() {
            loggers = new ArrayList<>();
        }

        LoggerListTableModel(List<Logger> loggers) {
            this.loggers = requireNonNull(loggers);
        }

        void setLoggers(List<Logger> loggers) {
            this.loggers = requireNonNull(loggers);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return loggers.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Logger logger = loggers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return logger.getName();
                case 1:
                    return logger.getLevel();
                case 2:
                    return logger.getAdditivity();
                case 3:
                    return Collections.list(logger.getAllAppenders());
                default:
                    throw new ArrayIndexOutOfBoundsException(columnIndex);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: // Name
                    return String.class;
                case 1: // Level
                    return Level.class;
                case 2: // Additivity
                    return Boolean.class;
                default:
                    return super.getColumnClass(columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            switch (column) {
                case 1:
                case 2:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Logger logger = loggers.get(rowIndex);
            switch (columnIndex) {
                case 1:
                    logger.setLevel((Level) aValue);
                    break;
                case 2:
                    logger.setAdditivity((Boolean) aValue);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot set value for column " + columnIndex);

            }
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }
    }

    public static abstract class WidgetSizes {
        private boolean isScaled = false;

        final private List<int[]> pixelFields = new ArrayList<>();
        final private List<Dimension> dimensionFields = new ArrayList<>();

        private List<int[]> getPixelFields() {
            return pixelFields;
        }

        private List<Dimension> getDimensionFields() {
            return dimensionFields;
        }

        protected void addPixelField(int[] pixelField) {
            requireNonNull(pixelField);
            pixelFields.add(pixelField);
        }

        protected void addDimensionField(Dimension dimensionField) {
            requireNonNull(dimensionField);
            dimensionFields.add(dimensionField);
        }

        protected void addDimensionFields(Iterable<Dimension> dimensionFields) {
            for (Dimension d : dimensionFields) {
                addDimensionField(d);
            }
        }

        protected void addPixelFields(Iterable<int[]> pixelFields) {
            for (int[] p : pixelFields) {
                addPixelField(p);
            }
        }

        public void scaleForHiDpiMode() {
            // Avoid redundant scaling
            if (isScaled) {
                return;
            }
            if (Configuration.isUseHiDpiMode()) {
                // Upsize everything on HiDPI displays
                for (Dimension dimension : getDimensionFields()) {
                    dimension.width = dimension.width * 2;
                    dimension.height = dimension.height * 2;
                }
                for (int[] pixelField : getPixelFields()) {
                    for (int i = 0; i < pixelField.length; i++) {
                        if (pixelField[i] != -1) {
                            pixelField[i] = pixelField[i] * 2;
                        }
                    }
                }
                isScaled = true;
            }
        }

    }

    private class MyWidgetSizes extends WidgetSizes {
        final int[] colPreferredWidths = new int[]{300, 100, 100, -1};
        final int[] colMaxWidths = new int[]{-1, 100, 100, -1};
        final Dimension windowSize = new Dimension(800, 600);

        MyWidgetSizes() {
            addPixelField(colPreferredWidths);
            addPixelField(colMaxWidths);
            addDimensionField(windowSize);
        }
    }

    private class MyTableModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            String info = String.format("type %d: [rows %d:%d, column %d]", e.getType(),
                    e.getFirstRow(), e.getLastRow(), e.getColumn());
            log.debug(sprintf("tableChanged() : %s", info));
            int col = e.getColumn();
            for (int row = e.getFirstRow(); row <= e.getLastRow(); row++) {
                Logger logger = loggersForRows.get(row);
                switch (e.getType()) {
                    case TableModelEvent.UPDATE:
                        if (col == LEVEL_COLUMN || col == TableModelEvent.ALL_COLUMNS) {
                            Level newLevel = (Level) tableModel.getValueAt(row, LEVEL_COLUMN);
                            logger.setLevel(newLevel);
                            log.debug(sprintf("Set logger %s to level %s", logger.getName(), newLevel));
                        }
                        if (col == ADDITIVITY_COLUMN || col == TableModelEvent.ALL_COLUMNS) {
                            boolean newAdditivity = (Boolean) tableModel.getValueAt(row, ADDITIVITY_COLUMN);
                            logger.setAdditivity(newAdditivity);
                            log.debug(sprintf("Set logger %s additivity to %s", logger.getName(), newAdditivity));
                        }
                        break;
                    case TableModelEvent.INSERT:
                        // ignore; this shouldn't happen
                        break;
                    case TableModelEvent.DELETE:
                        // ignore; this shouldn't happen
                        break;
                }
            }
        }
    }

    /**
     * Show this GUI panel in a newly constructed JFrame. This builds a frame, adds this
     * panel to it, picks a size, and makes it visible. The frame will be set to dispose
     * itself on close. The returned JFrame will not be set to be visible. This allows you
     * to do customization on the frame before displaying it to the user.
     *
     * @return The newly-constructed JFrame containing this GUI
     */
    public JFrame showInFrame() {
        JFrame frame = new JFrame("Log4jConfiguratorGui");
        if (iconImages != null) {
            frame.setIconImages(iconImages);
        }
        frame.getContentPane().add(this, BorderLayout.CENTER);
        MyWidgetSizes sizes = new MyWidgetSizes();
        sizes.scaleForHiDpiMode();
        frame.setSize(sizes.windowSize);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setJMenuBar(getMenuBar());
        return frame;
    }

    private static class LoggerComparator implements Comparator<Logger> {
        @Override
        public int compare(Logger o1, Logger o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

}