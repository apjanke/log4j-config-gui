package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Log4jConfiguratorGui;
import org.apache.log4j.*;
import org.apache.log4j.spi.Filter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

/**
 * A generic, extendable editor for AppenderSkeletons.
 */
class AppenderSkeletonEditor extends AppenderEditor {
    private final static Logger log = LogManager.getLogger(AppenderSkeletonEditor.class);

    private final AppenderSkeleton appender;

    private final JLabel layoutField = new JLabel();
    private final JTextField nameField = new JTextField();
    private final PriorityComboBox priorityComboBox = new PriorityComboBox();
    private final JLabel errorHandlerField = new JLabel();
    private final JTable filterTable = new JTable();
    private JPopupMenu popupMenu;
    private final FilterTableModelAdapter filterTableModel = new FilterTableModelAdapter();

    private final java.util.List<JComponent> thingsNeedingFilterSelection = new ArrayList<>();

    /**
     * The subpane containing control widgets that are label/value pairs. This has a
     * GridBagLayout, and is 2 columns wide. Subclasses may add additional components
     * to it.
     */
    JComponent controlPane;

    /**
     * The GBC for controlPane. This will be left in the state it was in after adding
     * all of AppenderSkeleton's control widgets, so subclasses can pick it up and use it.
     */
    GBC controlPaneGBC;

    /**
     * The JMenuBar that will be used when this editor is displayed in a Window. Subclasses
     * may add additional items to it in their initializeGui().
     */
    private JMenuBar menuBar;

    AppenderSkeletonEditor(AppenderSkeleton appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    /**
     * Adds components to the controlPane from an "arrangement" structure. An "arrangement"
     * structure is an Object[] array that's 2n long, holding n controls, where
     * arrangement[i] is a String to use for the label, and arrangement[i+1] is
     * the JComponent to put in the second column.
     * Adds components to controlPane, and advanceds controlPaneGBC.
     * @param arrangement arrangement Object[] structure
     */
    void addControlsFromArrangement(Object[] arrangement) {
        addControlsFromArrangement(controlPane, controlPaneGBC, arrangement);
    }

    /**
     * Initialize the GUI. Subclasses must call `super.initializeGui()` as the first
     * thing in their overridden initializeGui() implementation.
     */
    @Override
    void initializeGui() {
        setLayout(new BorderLayout());
        setBorder(SwingUtils.createEmptyBorderPx(10));
        JComponent pane = Box.createVerticalBox();
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        p.setAlignmentX(-1);
        GBC gbc = new GBC();
        JLabel l;

        nameField.setPreferredSize(textFieldPreferredSize);

        controlPane = p;
        controlPaneGBC = gbc;

        Object[] arrangement = {
                "Name",     nameField,
                "Layout",   layoutField,
                "Threshold",    priorityComboBox,
                "Error Handler",    errorHandlerField,
        };
        addControlsFromArrangement(arrangement);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filterPanel.add(new JScrollPane(filterTable), BorderLayout.CENTER);
        filterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                updateItemEnabling();
            }
        });
        filterTable.setModel(filterTableModel);
        filterTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = filterTable.rowAtPoint(point);
                if (e.getClickCount() == 2) {
                    editSelectedFilter();
                }
            }
        });

        popupMenu = new FilterPopupMenu();
        filterTable.setComponentPopupMenu(popupMenu);

        pane.add(p);
        pane.add(filterPanel);
        this.add(pane, BorderLayout.CENTER);

        menuBar = new MenuBar();
        refreshGuiThisLevel();
    }

    private void updateItemEnabling() {
        int row = filterTable.getSelectedRow();
        boolean hasSelection = row != -1;
        for (JComponent c : thingsNeedingFilterSelection) {
            c.setEnabled(hasSelection);
        }
    }

    private void addAddFilterMenuItems(JMenu menu) {
        FilterFactory filterFactory = new StandardFilterFactory();
        for (final Class<? extends Filter> filterClass : filterFactory.getSupportedFilterClasses()) {
            JMenuItem addFilterItem = new JMenuItem(nameWithoutLog4jPackage(filterClass.getName()));
            addFilterItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addOrInsertFilter(filterClass);
                }
            });
            menu.add(addFilterItem);
        }
    }

    private void editLayout() {
        Layout layout = appender.getLayout();
        LayoutEditor editor = LayoutEditor.createEditorFor(layout);
        editor.showInModalDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);
        refreshGui();
    }

    private class MenuBar extends JMenuBar {
        private MenuBar() {
            JMenu appenderMenu = new JMenu("Appender");
            add(appenderMenu);
            JMenuItem editLayoutItem = new JMenuItem("Edit Layout");
            editLayoutItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editLayout();
                }
            });
            appenderMenu.add(editLayoutItem);
            LayoutFactory layoutFactory = new StandardLayoutFactory();
            JMenu setNewLayoutMenu = new JMenu("New Layout");
            appenderMenu.add(setNewLayoutMenu);
            for (final Class<? extends Layout> layoutClass : layoutFactory.getSupportedLayoutClasses()) {
                JMenuItem newLayoutItem = new JMenuItem(nameWithoutLog4jPackage(layoutClass.getName()));
                newLayoutItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setNewLayout(layoutClass);
                    }
                });
                setNewLayoutMenu.add(newLayoutItem);
            }
            JMenu filterMenu = new JMenu("Filter");
            add(filterMenu);
            JMenu addFilterMenu = new JMenu("Add Filter");
            filterMenu.add(addFilterMenu);
            addAddFilterMenuItems(addFilterMenu);
            JMenuItem editFilterItem = new JMenuItem("Edit Filter");
            editFilterItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editSelectedFilter();
                }
            });
            thingsNeedingFilterSelection.add(editFilterItem);
            filterMenu.add(editFilterItem);
            JMenuItem removeFilterItem = new JMenuItem("Remove Filter");
            removeFilterItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeSelectedFilter();
                }
            });
            thingsNeedingFilterSelection.add(removeFilterItem);
            filterMenu.add(removeFilterItem);
            JMenu viewMenu = new JMenu("View");
            add(viewMenu);
            JMenuItem refreshMenuItem = new JMenuItem("Refresh");
            viewMenu.add(refreshMenuItem);
            refreshMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshGui();
                }
            });
            viewMenu.add(refreshMenuItem);
        }
    }

    @Override
    JMenuBar getMenuBar() {
        return menuBar;
    }

    private void refreshGuiThisLevel() {
        nameField.setText(appender.getName());
        layoutField.setText(Log4jConfiguratorGui.layoutString(appender.getLayout()));
        priorityComboBox.setSelectedItem(appender.getThreshold());
        errorHandlerField.setText(nameWithoutLog4jPackage("" + appender.getErrorHandler()));
        filterTableModel.setHeadFilter(appender.getFirstFilter());
        updateItemEnabling();
    }

    /**
     * Fully refresh the GUI contents from the edited Appender's current state.
     */
    void refreshGui() {
        refreshGuiThisLevel();
    }

    /**
     * Apply changes from the GUI to the edited Appender. Subclasses must call
     * `super.applyChanges()` as the first
     * thing in their overridden applyChanges() implementation.
     */
    @Override
    void applyChanges() {
        appender.setName(nameField.getText());
        appender.setThreshold((Priority) priorityComboBox.getSelectedItem());
    }

    private void removeSelectedFilter() {
        int row = filterTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        Filter headFilter = appender.getFirstFilter();
        if (row == 0) {
            Filter newHeadFilter = headFilter.getNext();
            appender.clearFilters();
            appender.addFilter(newHeadFilter);
        } else {
            Filter f = headFilter;
            for (int i = 0; i < row - 1; i++) {
                f = f.getNext();
            }
            Filter newNext = f.getNext().getNext();
            f.setNext(newNext);
        }
        refreshGui();
    }

    private void setNewLayout(Class<? extends Layout> layoutClass) {
        try {
            LayoutFactory layoutFactory = new StandardLayoutFactory();
            Layout newLayout = layoutFactory.createLayout(layoutClass);
            LayoutEditor editor = LayoutEditor.createEditorFor(newLayout);
            LayoutEditor.MyDialog dialog = editor.showInModalDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.getUserSelection() == DialogOption.OK) {
                appender.setLayout(newLayout);
                refreshGui();
            }
        } catch (Exception ex) {
            log.error(sprintf("Error while setting new Layout (%s): %s", layoutClass.getName(), ex.getMessage()), ex);
            JOptionPane.showMessageDialog(this,
                    sprintf("Error while setting new Layout (%s): %s", layoutClass.getName(), ex.getMessage()),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedFilter() {
        int row = filterTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        java.util.List<Filter> filters = filterTableModel.getFilters();
        FilterEditor editor;
        Filter filter = filters.get(row);
        try {
            editor = FilterEditor.createEditorFor(filter);
            FilterEditor.MyDialog dialog = editor.showInModalDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            DialogOption choice = dialog.getUserSelection();
        } catch (Exception e) {
            log.error(sprintf("Error while trying to edit filter %s: %s",
                    filter, e.getMessage()), e);
            JOptionPane.showMessageDialog(this,
                    sprintf("Error while trying to edit filter %s: %s",
                            filter, e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrInsertFilter(Class<? extends Filter> filterClass) {
        try {
            Filter newFilter = filterClass.getConstructor().newInstance();
            if (FilterEditor.isEditable(filterClass)) {
                FilterEditor editor = FilterEditor.createEditorFor(newFilter);
                FilterEditor.MyDialog dialog = editor.showInModalDialog(SwingUtilities.getWindowAncestor(this));
                dialog.setVisible(true);
                DialogOption choice = dialog.getUserSelection();
                if (DialogOption.OK != choice) {
                    return;
                }
            }

            int row = filterTable.getSelectedRow();
            switch (row) {
                case -1:
                    // No selection; add to end
                    appender.addFilter(newFilter);
                    break;
                case 0:
                    newFilter.setNext(appender.getFirstFilter());
                    appender.clearFilters();
                    appender.addFilter(newFilter);
                    break;
                default:
                    Filter f = appender.getFirstFilter();
                    for (int i = 0; i < row - 1; i++) {
                        f = f.getNext();
                    }
                    newFilter.setNext(f.getNext());
                    f.setNext(newFilter);
                    break;
            }
            refreshGui();
        } catch (Exception e) {
            log.error("Error during filter creation: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Error during filter creation: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class MyPopupMenuListener implements PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int rowAtPoint = filterTable.rowAtPoint(SwingUtilities.convertPoint(
                            popupMenu, new Point(0, 0), filterTable));
                    if (rowAtPoint > -1) {
                        filterTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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

    private class FilterPopupMenu extends JPopupMenu {
        FilterPopupMenu() {
            JMenu addFilterMenu = new JMenu("Add Filter");
            add(addFilterMenu);
            addAddFilterMenuItems(addFilterMenu);
            JMenuItem editFilterItem = new JMenuItem("Edit Filter");
            editFilterItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editSelectedFilter();
                }
            });
            thingsNeedingFilterSelection.add(editFilterItem);
            add(editFilterItem);
            JMenuItem removeFilterItem = new JMenuItem("Remove Filter");
            removeFilterItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeSelectedFilter();
                }
            });
            thingsNeedingFilterSelection.add(removeFilterItem);
            add(removeFilterItem);
        }
    }

}
