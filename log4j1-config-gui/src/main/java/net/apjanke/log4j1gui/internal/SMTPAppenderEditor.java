package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SMTPAppender;

import javax.swing.*;

import static java.util.Objects.requireNonNull;

/**
 * An editor for SMTPAppender.
 *
 * Does not fully support editing for the following properties because there's no getter for them
 * on SMTPAppender in Log4j release 1.2.15:
 * <ul>
 *     <li>replyTo</li>
 *     <li>SMTPProtocol</li>
 *     <li>SMTPPort</li>
 *     <li>sendOnClose</li>
 * </ul>
 */
class SMTPAppenderEditor extends AppenderSkeletonEditor {

    private final static Logger log = LogManager.getLogger(ConsoleAppenderEditor.class);

    private final SMTPAppender appender;

    private JTextField bccField;
    private JTextField bufferSizeField;
    private JTextField ccField;
    private JLabel evaluatorField;
    private JTextField evaluatorClassField;
    private JTextField fromField;
    private JCheckBox locationInfoField;
    //private JTextField replyToField;
    //private JCheckBox sendOnCloseField;
    private JCheckBox SMTPDebugField;
    private JTextField SMTPHostField;
    private JPasswordField SMTPPasswordField;
    private JTextField SMTPPortField;
    //private JTextField SMTPProtocolField;
    private JTextField SMTPUsernameField;
    private JTextField subjectField;
    private JTextField toField;

    SMTPAppenderEditor(SMTPAppender appender) {
        super(appender);
        this.appender = requireNonNull(appender);
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        bccField = new JTextField();
        bufferSizeField = new JTextField();
        ccField = new JTextField();
        evaluatorField = new JLabel();
        evaluatorClassField = new JTextField();
        fromField = new JTextField();
        locationInfoField = new JCheckBox();
        //replyToField = new JTextField();
        //sendOnCloseField = new JCheckBox();
        SMTPDebugField = new JCheckBox();
        SMTPHostField = new JTextField();
        SMTPPasswordField = new JPasswordField();
        //SMTPPortField = new JTextField();
        //SMTPProtocolField = new JTextField();
        SMTPUsernameField = new JTextField();
        subjectField = new JTextField();
        toField = new JTextField();
        bufferSizeField.setInputVerifier(new SwingUtils.IntegerInputVerifier());

        JComponent[] bigFields = {
                bccField, bufferSizeField, ccField, evaluatorClassField, fromField, SMTPHostField,
                SMTPPasswordField, SMTPUsernameField, subjectField, toField
        };
        for (JComponent c : bigFields) {
            c.setPreferredSize(textFieldPreferredSize);
        }

        Object[] arrangement = {
                "From", fromField,
                //"Reply To", replyToField,
                "To", toField,
                "Subject", subjectField,
                "CC", ccField,
                "BCC", bccField,
                "Host", SMTPHostField,
                "Username", SMTPUsernameField,
                "Password", SMTPPasswordField,
                //"Port", SMTPPortField,
                //"Protocol", SMTPProtocolField,
                //"Send on Close", sendOnCloseField,
                "Debug",    SMTPDebugField,
                "Buffer Size", bufferSizeField,
                "Evaluator", evaluatorField,
                "Evaluator Class", evaluatorClassField,
                "Location Info", locationInfoField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        fromField.setText(appender.getFrom());
        toField.setText(appender.getTo());
        subjectField.setText(appender.getSubject());
        ccField.setText(appender.getCc());
        bccField.setText(appender.getBcc());
        SMTPHostField.setText(appender.getSMTPHost());
        SMTPUsernameField.setText(appender.getSMTPUsername());
        SMTPPasswordField.setText(appender.getSMTPPassword());
        //SMTPProtocolField.setText(appender.getSMTPProtocol());
        SMTPDebugField.setSelected(appender.getSMTPDebug());
        bufferSizeField.setText(""+appender.getBufferSize());
        evaluatorField.setText(""+appender.getEvaluator());
        evaluatorClassField.setText(""+appender.getEvaluatorClass());
        locationInfoField.setSelected(appender.getLocationInfo());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setFrom(fromField.getText());
        appender.setTo(toField.getText());
        appender.setSubject(subjectField.getText());
        appender.setCc(ccField.getText());
        appender.setBcc(bccField.getText());
        appender.setSMTPHost(SMTPHostField.getText());
        appender.setSMTPUsername(SMTPUsernameField.getText());
        appender.setSMTPPassword(new String(SMTPPasswordField.getPassword()));
        //appender.setSMTPProtocol(SMTPProtocolField.getText());
        appender.setSMTPDebug(SMTPDebugField.isSelected());
        appender.setBufferSize(Integer.parseInt(bufferSizeField.getText()));
        appender.setEvaluatorClass(evaluatorClassField.getText());
        appender.setLocationInfo(locationInfoField.isSelected());
    }}
