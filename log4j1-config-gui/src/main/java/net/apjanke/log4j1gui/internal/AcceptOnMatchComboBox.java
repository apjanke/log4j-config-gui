package net.apjanke.log4j1gui.internal;

import javax.swing.*;

class AcceptOnMatchComboBox extends JComboBox<AcceptOnMatchComboBox.AcceptValue> {

    static class AcceptValue {
        static final AcceptValue ACCEPT =  new AcceptValue(true, "ACCEPT");
        static final AcceptValue DENY =  new AcceptValue(false, "DENY");

        final boolean val;
        final String label;

        AcceptValue(boolean val, String label) {
            this.val = val;
            this.label = label;
        }

        public String toString() {
            return this.label;
        }
    }

    public AcceptOnMatchComboBox() {
        addItem(AcceptValue.ACCEPT);
        addItem(AcceptValue.DENY);
    }

    public void setSelectedValue(boolean val) {
        if (val) {
            setSelectedItem(AcceptValue.ACCEPT);
        } else {
            setSelectedItem(AcceptValue.DENY);
        }
    }

    public boolean getSelectedValue() {
        return getItemAt(getSelectedIndex()).val;
    }
}
