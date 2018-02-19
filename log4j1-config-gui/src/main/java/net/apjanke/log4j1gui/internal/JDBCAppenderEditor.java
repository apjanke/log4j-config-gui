package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.jdbc.JDBCAppender;

import javax.swing.*;

class JDBCAppenderEditor extends AppenderSkeletonEditor {
    private final static Logger log = LogManager.getLogger(ConsoleAppenderEditor.class);

    private final JDBCAppender appender;

    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField sqlField;
    private JTextField bufferSizeField;

    JDBCAppenderEditor(JDBCAppender appender) {
        super(appender);
        this.appender = appender;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        urlField = new JTextField();
        userField = new JTextField();
        passwordField = new JPasswordField();
        sqlField = new JTextField();
        bufferSizeField = new JTextField();

        Object[] arrangement = {
                "URL",      urlField,
                "User",     userField,
                "Password", passwordField,
                "SQL",      sqlField,
                "Buffer Size",  bufferSizeField,
        };
        addControlsFromArrangement(arrangement);

        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        urlField.setText(appender.getURL());
        userField.setText(appender.getUser());
        passwordField.setText(appender.getPassword());
        sqlField.setText(appender.getSql());
        bufferSizeField.setText(""+appender.getBufferSize());
    }

    protected void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        appender.setURL(urlField.getText());
        appender.setUser(userField.getText());
        appender.setPassword(new String(passwordField.getPassword()));
        appender.setSql(sqlField.getText());
        appender.setBufferSize(Integer.parseInt(bufferSizeField.getText()));
    }

}
