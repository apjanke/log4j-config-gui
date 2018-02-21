package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Configuration;
import net.apjanke.log4j1gui.Log4jConfiguratorGui;
import org.apache.log4j.*;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.px;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

/**
 * Edits the appenders attached to a Logger.
 */
public class AppendersEditor extends JPanel {
    private static final Logger log = LogManager.getLogger(AppendersEditor.class);

    private static final int LAYOUT_COLUMN = 2;
    private static final java.util.List<Integer> editableColsList = new ArrayList<>();
    static {
        editableColsList.add(1);
    }

    /**
     * The logger that this is editing.
     */
    private final Logger logger;
    /**
     * The appenders on the logger.
     */
    private java.util.List<Appender> appenders;

    JTable table;
    private AppenderListTableModel tableModel;
    private JPopupMenu popupMenu;

    public AppendersEditor(Logger logger) {
        requireNonNull(logger);
        this.logger = logger;
    }

    private class MyWidgetSizes extends Log4jConfiguratorGui.WidgetSizes {
        final int[] colPreferredWidths = new int[]{150, 100, 300, -1, -1, -1};
        final int[] colMaxWidths = new int[]{-1, -1, -1, -1, -1, -1};
        final Dimension windowPreferredSize = new Dimension(800, 400);
        final Dimension windowMinimumSize = new Dimension(400, 200);
        final Dimension buttonPanelMinimumSize = new Dimension(400, 150);

        MyWidgetSizes() {
            addPixelFields(Arrays.asList(
                    colPreferredWidths,
                    colMaxWidths));
            addDimensionFields(Arrays.asList(
                    windowPreferredSize,
                    windowMinimumSize,
                    buttonPanelMinimumSize));
        }
    }

    public void editSelectedAppender() {
        Appender appender;
        // We can do this because we made right-click select a row
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        appender = appenders.get(row);
        try {
            AppenderEditor editor = AppenderEditor.createEditorFor(appender);
            editor.showInModalDialog().setVisible(true);
            tableModel.fireTableRowsUpdated(row, row);
        } catch (AppenderEditor.UnrecognizedAppenderException err) {
            log.error(sprintf("Unrecognized Appender: %s", appender), err);
            JOptionPane.showMessageDialog(null,
                    sprintf("Editing is not supported for Appender type %s\n\nSorry. :(",
                            appender.getClass().getName()));
        } catch (Exception err) {
            log.error(sprintf("Error during attempt to Edit Appender for %s", appender), err);
            JOptionPane.showMessageDialog(null,
                    sprintf("Error during attempt to Edit Appender for %s", appender),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editLayoutForSelectedAppender() {
        try {
            // We can do this because we made right-click select a row
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            Layout layout = (Layout) table.getValueAt(row, LAYOUT_COLUMN);
            if (null == layout) {
                JOptionPane.showMessageDialog(null, "No Layout for this Appender; can't edit. Sorry.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LayoutEditor editor = LayoutEditor.createEditorFor(layout);
            editor.showInModalDialog().setVisible(true);
            tableModel.fireTableCellUpdated(row, LAYOUT_COLUMN);
        } catch (Exception err) {
            log.error(sprintf("Error during attempt to Edit Layout"), err);
        }
    }

    public void removeSelectedAppender() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        Appender appenderToRemove = appenders.get(row);
        logger.removeAppender(appenderToRemove);
        java.util.List<Appender> updatedAppenders = Utils.getAllAppenders(logger);
        if (updatedAppenders.contains(appenderToRemove)) {
            JOptionPane.showMessageDialog(null, "Removal failed: Logger ignored request to remove " + appenderToRemove,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        for (Appender appender : updatedAppenders) {
            if (appender == appenderToRemove) {
                log.error(sprintf("Removal failed: Got a match by identity: %s", appender));
            }
        }
        refreshTableModel();
    }

    private class PopupMenu extends JPopupMenu {
        PopupMenu() {
            addPopupMenuListener(new MyPopupMenuListener());
            JMenuItem editAppenderItem = new JMenuItem("Edit Appender");
            editAppenderItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editSelectedAppender();
                }
            });
            add(editAppenderItem);
            JMenuItem editLayoutItem = new JMenuItem("Edit Layout");
            editLayoutItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    editLayoutForSelectedAppender();
                }
            });
            add(editLayoutItem);
            JMenu setLayoutMenu = new JMenu("Set New Layout");
            add(setLayoutMenu);
            LayoutFactory layoutFactory = new StandardLayoutFactory();
            for (final Class<? extends Layout> klass : layoutFactory.getSupportedLayoutClasses()) {
                JMenuItem setLayoutItem = new SetLayoutMenuItem(klass.getSimpleName(), klass);
                setLayoutMenu.add(setLayoutItem);
            }
            JMenuItem setErrorHandlerItem = new JMenuItem("Set Error Handler...");
            setErrorHandlerItem.setEnabled(false);
            add(setErrorHandlerItem);
        }
    }

    public void initializeGui() {
        table = new JTable();
        //noinspection unchecked
        final AppenderListTableModel tableModel = new AppenderListTableModel(Collections.list(logger.getAllAppenders()));
        table.setModel(tableModel);
        MyWidgetSizes sizes = new MyWidgetSizes();
        sizes.scaleForHiDpiMode();
        setPreferredSize(sizes.windowPreferredSize);
        setMinimumSize(sizes.windowPreferredSize);

        popupMenu = new PopupMenu();
        table.setComponentPopupMenu(popupMenu);

        MyCellRenderer cellRenderer = new MyCellRenderer();
        table.setDefaultRenderer(Class.class, cellRenderer);
        table.setDefaultRenderer(Layout.class, cellRenderer);
        table.setDefaultRenderer(Filter.class, cellRenderer);
        table.setDefaultRenderer(ErrorHandler.class, cellRenderer);

        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMinimumSize(sizes.buttonPanelMinimumSize);
        FlowLayout buttonPanelLayout = new FlowLayout();
        buttonPanelLayout.setHgap(20);
        buttonPanel.setLayout(buttonPanelLayout);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedAppender();
            }
        });
        JButton removeAllButton = new JButton("Remove All");
        removeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.removeAllAppenders();
                refreshTableModel();
            }
        });
        buttonPanel.add(removeButton);
        buttonPanel.add(removeAllButton);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                int col = table.columnAtPoint(point);
                if (e.getClickCount() == 2) {
                    log.info(sprintf("Double-click on table: point=%s, row=%s", point, row));
                    if (editableColsList.contains(col)) {
                        // Let the table cell editor take care of it
                        return;
                    }
                    editSelectedAppender();
                }
            }
        });

        // Have to do column sizing after the tableModel has been set
        TableColumnModel colModel = table.getColumnModel();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            if (sizes.colPreferredWidths[i] != -1) {
                colModel.getColumn(i).setPreferredWidth(sizes.colPreferredWidths[i]);
            }
            if (sizes.colMaxWidths[i] != -1) {
                colModel.getColumn(i).setMaxWidth(sizes.colMaxWidths[i]);
            }
        }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshGui();
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

    public void refreshGui() {
        //noinspection unchecked
        appenders = Collections.list(logger.getAllAppenders());
        tableModel = new AppenderListTableModel(appenders);
        table.setModel(tableModel);
    }

    /**
     * Refresh the data underlying the tableModel, without creating a new tableModel.
     */
    private void refreshTableModel() {
        //noinspection unchecked
        tableModel.setAppenders(Collections.list(logger.getAllAppenders()));
    }

    public JDialog showInModalDialog() {
        JDialog dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.setSize(px(new Dimension(800, 300)));

        // TODO: Deactivate the "Set Layout" menu if there's no selection
        // TODO: Deactivate the "Set Layout" menu if the selected appender does not take a Layout
        JMenuBar menuBar = new JMenuBar();
        JMenu loggerMenu = new JMenu("Logger");
        JMenu addAppenderMenu = new JMenu("Add Appender");
        JMenuItem addNullAppenderMenuItem = new JMenuItem("Null Appender");
        addAppenderMenu.add(addNullAppenderMenuItem);
        final AppenderFactory appenderFactory = new StandardAppenderFactory();
        for (final Class<? extends Appender> appenderClass : appenderFactory.getSupportedAppenderClasses()) {
            JMenuItem addAppenderItem = new JMenuItem(nameWithoutLog4jPackage(appenderClass.getName()));
            addAppenderItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.addAppender(appenderFactory.createAppender(appenderClass));
                    refreshGui();
                }
            });
            addAppenderMenu.add(addAppenderItem);
        }
        loggerMenu.add(addAppenderMenu);
        JMenu loggerTestMenu = new JMenu("Test");
        JMenu testHelloWorldMenu = new JMenu("Hello World");
        for (Level level : Utils.ALL_LEVELS) {
            JMenuItem blah = new JMenuItem("" + level);
            final Level thisLevel = level;
            blah.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Use System.out to bypass all logging configuration here
                    String msg = "Hello, World!";
                    System.out.format("TEST: Sending message \"%s\" at level %s (level id=%d) to Logger '%s' (%s)\n",
                            msg, thisLevel, thisLevel.toInt(), logger.getName(), "" + logger);
                    String logMsg = "TEST: \"" + msg + "\" (level " + thisLevel + " on logger " + logger.getName() + ")";
                    logger.log(thisLevel, msg);
                }
            });
            testHelloWorldMenu.add(blah);
        }
        loggerTestMenu.add(testHelloWorldMenu);
        loggerMenu.add(loggerTestMenu);
        menuBar.add(loggerMenu);

        JMenu appenderMenu = new JMenu("Appender");
        JMenu setLayoutMenu = new JMenu("Set Layout");
        JMenuItem setPatternLayoutItem = new SetLayoutMenuItem("Pattern Layout", PatternLayout.class);
        JMenuItem setEnhancedPatternLayoutItem = null;
        if (Configuration.hasLog4jExtras()) {
            setEnhancedPatternLayoutItem = new SetLayoutMenuItem("Enhanced Pattern Layout", EnhancedPatternLayout.class);
        }
        setLayoutMenu.add(setPatternLayoutItem);
        if (setEnhancedPatternLayoutItem != null)
            setLayoutMenu.add(setEnhancedPatternLayoutItem);
        appenderMenu.add(setLayoutMenu);
        // TODO: Implement these
        JMenuItem setErrorHandlerItem = new JMenuItem("Set Error Handler...");
        setErrorHandlerItem.setEnabled(false);
        appenderMenu.add(setErrorHandlerItem);
        menuBar.add(appenderMenu);
        dialog.setJMenuBar(menuBar);

        dialog.setModal(true);
        return dialog;
    }

    private class SetLayoutMenuItem extends JMenuItem {
        private final Class<? extends Layout> layoutClass;

        SetLayoutMenuItem(String text, final Class<? extends Layout> layoutClass) {
            super(text);
            this.layoutClass = requireNonNull(layoutClass);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        return;
                    }
                    Appender appender = appenders.get(row);
                    log.info(sprintf("SetLayoutMenuItem: selected: %s: %s", getText(), layoutClass.getName()));
                    doSetNewLayout(appender, row);
                }
            });
        }

        private void doSetNewLayout(Appender appender, int row) {
            try {
                LayoutFactory layoutFactory = new StandardLayoutFactory();
                Layout newLayout = layoutFactory.createLayout(layoutClass);
                LayoutEditor editor = LayoutEditor.createEditorFor(newLayout);
                LayoutEditor.MyDialog dialog = editor.showInModalDialog();
                dialog.setVisible(true);
                if (dialog.getUserSelection() == DialogOption.OK) {
                    appender.setLayout(newLayout);
                    refreshGui();
                }
            } catch (Exception e) {
                log.error(sprintf("Error while trying to set new Layout: %s", e.getMessage()), e);
            }
        }
    }

    private static class MyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value) {
            String str;
            if (null == value) {
                str = "";
            } else if (value instanceof Class) {
                str = nameWithoutLog4jPackage(((Class) value).getName());
            } else if (value instanceof PatternLayout) {
                str = Log4jConfiguratorGui.layoutString((PatternLayout) value);
            } else if (value instanceof EnhancedPatternLayout) {
                str = Log4jConfiguratorGui.layoutString((EnhancedPatternLayout) value);
            } else if (value instanceof Layout) {
                str = nameWithoutLog4jPackage(value.toString());
            } else if (value instanceof Filter) {
                str = nameWithoutLog4jPackage(value.toString());
            } else if (value instanceof ErrorHandler) {
                str = nameWithoutLog4jPackage(value.toString());
            } else {
                str = "" + value;
            }
            setText(str);
        }

    }

}
