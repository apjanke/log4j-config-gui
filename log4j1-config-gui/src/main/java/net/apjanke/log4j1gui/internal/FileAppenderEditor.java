package net.apjanke.log4j1gui.internal;

import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

class FileAppenderEditor extends WriterAppenderEditor {
    private final static Logger log = LogManager.getLogger(ConsoleAppenderEditor.class);

    private final FileAppender appender;

    private final JCheckBox appendCheckBox = new JCheckBox();
    private final JCheckBox bufferedIoCheckBox = new JCheckBox();
    private final JTextField bufferSizeTextField = new JTextField();
    private final JTextField fileTextField = new JTextField();


    FileAppenderEditor(FileAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        bufferSizeTextField.setPreferredSize(smallTextFieldPreferredSize);
        fileTextField.setPreferredSize(textFieldPreferredSize);
        bufferSizeTextField.setInputVerifier(new SwingUtils.IntegerInputVerifier());

        Object[] arrangement = {
                "File",     fileTextField,
                "Buffered", bufferedIoCheckBox,
                "Buffer Size", bufferSizeTextField,
                "Append",   appendCheckBox,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        fileTextField.setText(appender.getFile());
        appendCheckBox.setSelected(appender.getAppend());
        bufferedIoCheckBox.setSelected(appender.getBufferedIO());
        bufferSizeTextField.setText(""+appender.getBufferSize());
    }

    @Override
    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    void applyChanges() {
        super.applyChanges();
        int bufferSize = Integer.parseInt(bufferSizeTextField.getText());
        appender.setFile(fileTextField.getText());
        appender.setAppend(appendCheckBox.isSelected());
        appender.setBufferedIO(bufferedIoCheckBox.isSelected());
        appender.setBufferSize(bufferSize);
    }

}
