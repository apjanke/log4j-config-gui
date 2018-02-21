package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.px;

public abstract class ThingEditor extends JPanel {
    private static final Logger log = LogManager.getLogger(ThingEditor.class);

    /** The thing that this is editing. */
    private final Object thing;

    /**
     * The preferred size that most text fields in the control pane should use. Treat this
     * as read-only.
     *
     * This default size is pretty wide, to accommodate fields that contain file paths.
     */
    final Dimension textFieldPreferredSize = px(new Dimension(500, SwingUtils.singleRowTextFieldHeight));

    abstract void initializeGui();

    abstract void applyChanges();

    ThingEditor(Object thing) {
        this.thing = thing;
    }

    String getTitle() {
        return nameWithoutLog4jPackage(""+thing);
    }

    void addControlsFromArrangement(JComponent component, GBC gbc, Object[] arrangement) {
        for (int i = 0; i < arrangement.length; i+=2) {
            component.add(new JLabel(arrangement[i] +":"), gbc);
            gbc.gridx = 1;  gbc.weightx = 1;
            component.add(((JComponent)arrangement[i+1]), gbc);
            gbc.nextRow(); gbc.weightx = 0;
        }
    }

    /**
     * Get the menu bar to use when this editor is displayed in a window. Subclasses
     * should override this to provide a menu bar. The default implementation returns
     * null.
     * @return The menu bar to use, or null to indicate no menu bar
     */
    JMenuBar getMenuBar() {
        return null;
    }

    public static class MyDialog extends JDialog {

        private DialogOption userSelection = DialogOption.NONE;
        private final ThingEditor editor;

        MyDialog(ThingEditor editor) {
            this.editor = requireNonNull(editor);
        }

        void initializeGui() {
            setLayout(new BorderLayout());
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            getContentPane().add(editor, BorderLayout.CENTER);
            setSize(px(new Dimension(800, 400)));

            JPanel buttonPanel = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(50);
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setMinimumSize(px(new Dimension(300, 50)));
            buttonPanel.setMaximumSize(px(new Dimension(600, 50)));
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userSelection = DialogOption.OK;
                    editor.applyChanges();
                    dispose();
                }
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userSelection = DialogOption.CANCEL;
                    dispose();
                }
            });
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            JMenuBar menuBar = editor.getMenuBar();
            if (null != menuBar) {
                setJMenuBar(menuBar);
            }
            pack();
            setModal(true);
        }

        DialogOption getUserSelection() {
            return userSelection;
        }

    }

    public MyDialog showInModalDialog() {
        initializeGui();
        final MyDialog dialog = new MyDialog(this);
        dialog.setTitle(getTitle());
        dialog.initializeGui();
        return dialog;
    }
}

