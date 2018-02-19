package net.apjanke.log4j1gui.internal;

import org.apache.log4j.varia.DenyAllFilter;

import javax.swing.*;
import java.awt.*;

public class DenyAllFilterEditor extends FilterEditor {

    public DenyAllFilterEditor(DenyAllFilter filter) {
        super(filter);
    }
    @Override

    public void initializeGui() {
        setLayout(new BorderLayout());
        setBorder(SwingUtils.createEmptyBorderPx(30));
        JLabel label = new JLabel("Nothing to edit here. DenyAllFilter has no editable settings.");
        add(label, BorderLayout.CENTER);
    }

    @Override
    public void applyChanges() {
        // NOP
    }

}
