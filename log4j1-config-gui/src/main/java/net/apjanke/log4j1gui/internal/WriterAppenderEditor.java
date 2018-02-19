package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class WriterAppenderEditor extends AppenderSkeletonEditor {
    private final static Logger log = LogManager.getLogger(WriterAppenderEditor.class);

    private final WriterAppender appender;

    private final JTextField encodingTextField = new JTextField();
    private final JCheckBox immediateFlushCheckBox = new JCheckBox();

    WriterAppenderEditor(WriterAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }


    @Override
    void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        encodingTextField.setPreferredSize(textFieldPreferredSize);

        Object[] arrangement = new Object[] {
                "Encoding",     encodingTextField,
                "Immediate Flush",  immediateFlushCheckBox,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        encodingTextField.setText(appender.getEncoding());
        immediateFlushCheckBox.setSelected(appender.getImmediateFlush());
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    void applyChanges() {
        super.applyChanges();
        appender.setEncoding(encodingTextField.getText());
        appender.setImmediateFlush(immediateFlushCheckBox.isSelected());
    }

}