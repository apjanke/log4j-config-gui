package net.apjanke.log4j1gui.internal;

import java.awt.*;

import static net.apjanke.log4j1gui.internal.Utils.px;

class GBC extends GridBagConstraints {
    public GBC() {
        this.ipadx = px(10);
        this.ipady = px(10);
        this.anchor = WEST;
        this.gridx = 0;
        this.gridy = 0;
    }

    public void nextRow() {
        this.gridy++;
        this.gridx = 0;
    }
}
