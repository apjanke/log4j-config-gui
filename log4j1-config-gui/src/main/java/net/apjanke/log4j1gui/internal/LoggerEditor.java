package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

public class LoggerEditor extends ThingEditor {
    private static final Logger log = LogManager.getLogger(LoggerEditor.class);

    private final Logger logger;
    private final AppenderFactory appenderFactory = new StandardAppenderFactory();

    private final JLabel loggerDisplayField = new JLabel();
    private final LevelComboBox levelComboBox = new LevelComboBox();
    private final JCheckBox additivityCheckBox = new JCheckBox();
    private final JLabel nameField = new JLabel();
    private final JLabel loggerRepositoryField = new JLabel();
    private final JLabel resourceBundleField = new JLabel();

    private AppendersEditor appendersEditor;
    private final java.util.List<JComponent> thingsNeedingAppenderSelection = new ArrayList<>();
    private MenuBar menuBar;

    public LoggerEditor (Logger logger) {
        super(logger);
        this.logger = requireNonNull(logger);
    }

    public String getTitle() {
        return nameWithoutLog4jPackage(""+logger.getClass().getName() + " " + logger.getName());
    }

    public void initializeGui() {
        setLayout(new BorderLayout());
        setBorder(SwingUtils.createEmptyBorderPx(20));
        JPanel holder = new JPanel();
        holder.setLayout(new GridBagLayout());
        GBC holderGbc = new GBC();

        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        p.setAlignmentX(-1);
        GBC gbc = new GBC();

        Object[] arrangement = new Object[] {
                "Name",         nameField,
                "Level",        levelComboBox,
                "Additivity",   additivityCheckBox,
                "Logger",       loggerDisplayField,
                "Logger Repository",    loggerRepositoryField,
                "Resource Bundle",      resourceBundleField,
        };
        addControlsFromArrangement(p, gbc, arrangement);
        holder.add(p, holderGbc);
        holderGbc.nextRow();

        appendersEditor = new AppendersEditor(logger);
        appendersEditor.initializeGui();
        appendersEditor.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                updateItemEnabling();
            }
        });
        menuBar = new MenuBar();
        holder.add(appendersEditor, holderGbc);
        add(holder, BorderLayout.CENTER);
        refreshGui();
    }

    private void refreshGuiThisLevel() {
        loggerDisplayField.setText(nameWithoutLog4jPackage(""+logger));
        nameField.setText(logger.getName());
        levelComboBox.setSelectedItem(logger.getLevel());
        additivityCheckBox.setSelected(logger.getAdditivity());
        loggerRepositoryField.setText(""+logger.getLoggerRepository());
        resourceBundleField.setText(""+logger.getResourceBundle());
        updateItemEnabling();
    }

    private void updateItemEnabling() {
        int row = appendersEditor.table.getSelectedRow();
        boolean hasSelection = row != -1;
        for (JComponent c : thingsNeedingAppenderSelection) {
            c.setEnabled(hasSelection);
        }
    }

    @Override
    public void applyChanges() {
        logger.setLevel((Level) levelComboBox.getSelectedItem());
        logger.setAdditivity(additivityCheckBox.isSelected());
    }

    private void refreshGui() {
        refreshGuiThisLevel();
        appendersEditor.refreshGui();
        updateItemEnabling();
    }

    private class MenuBar extends JMenuBar {
        MenuBar() {
            JMenu appenderMenu = new JMenu("Appender");
            add(appenderMenu);
            JMenu addAppenderMenu = new JMenu("Add Appender");
            appenderMenu.add(addAppenderMenu);
            for (Class klass: appenderFactory.getSupportedAppenderClasses()) {
                final Class theClass = klass;
                JMenuItem addAppenderItem = new JMenuItem(nameWithoutLog4jPackage(klass.getName()));
                addAppenderItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addNewAppender(theClass);
                        // TODO: Turn exceptions here into error dialog boxes instead of
                        // just logging and swallowing them.
                    }
                });
                addAppenderMenu.add(addAppenderItem);
            }
            JMenuItem editAppenderItem = new JMenuItem("Edit");
            editAppenderItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    appendersEditor.editSelectedAppender();
                }
            });
            thingsNeedingAppenderSelection.add(editAppenderItem);
            appenderMenu.add(editAppenderItem);
            JMenuItem removeAppenderItem = new JMenuItem("Remove");
            removeAppenderItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    appendersEditor.removeSelectedAppender();
                }
            });
            appenderMenu.add(removeAppenderItem);
            thingsNeedingAppenderSelection.add(removeAppenderItem);
            JMenu viewMenu = new JMenu("View");
            JMenuItem refreshMenuItem = new JMenuItem("Refresh");
            viewMenu.add(refreshMenuItem);
            refreshMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshGui();
                }
            });
            add(viewMenu);
        }
    }

    @Override
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private void addNewAppender(Class appenderClass) {
        try {
            @SuppressWarnings("unchecked") Appender appender = appenderFactory.createAppender(appenderClass);
            logger.addAppender(appender);
            refreshGui();
        } catch (Exception e) {
            log.error(sprintf("Error while adding appender: %s", e.getMessage()), e);
        }
    }
}
