package net.apjanke.log4j1gui.internal;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.px;

public abstract class LayoutEditor extends JPanel {

    public abstract void initializeGui();

    protected abstract void applyChanges();

    public static LayoutEditor createEditorFor(Layout layout) {
        requireNonNull(layout);
        if (layout instanceof PatternLayout) {
            return new PatternLayoutEditor((PatternLayout) layout);
        } else if (layout instanceof EnhancedPatternLayout) {
            return new EnhancedPatternLayoutEditor((EnhancedPatternLayout) layout);
        } else {
            throw new UnsupportedOperationException("No layout editor defined for class "+layout.getClass().getName());
        }
    }

    private static class UnsupportedLayoutException extends Exception {
        UnsupportedLayoutException(String message) {
            super(message);
        }
    }

    public static class MyDialog extends JDialog {

        private DialogOption userSelection = DialogOption.NONE;
        private final LayoutEditor editor;

        MyDialog(LayoutEditor layoutEditor) {
            this.editor = requireNonNull(layoutEditor);
        }

        void initializeGui() {
            setLayout(new BorderLayout());
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            getContentPane().add(editor, BorderLayout.CENTER);
            setSize(px(new Dimension(800,400)));

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

            setModal(true);
        }

        public DialogOption getUserSelection() {
            return userSelection;
        }
    }

    public MyDialog showInModalDialog() {
        final MyDialog dialog = new MyDialog(this);
        dialog.initializeGui();
        return dialog;
    }

}
