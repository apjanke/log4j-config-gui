package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

import javax.swing.*;

class TTCCLayoutEditor extends DateLayoutEditor {
    private static final Logger log = LogManager.getLogger(TTCCLayoutEditor.class);

    private final TTCCLayout layout;

    private final JCheckBox categoryPrefixingField = new JCheckBox();
    private final JCheckBox contextPrintingField = new JCheckBox();
    private final JCheckBox threadPrintingField = new JCheckBox();

    TTCCLayoutEditor(TTCCLayout layout) {
        super(layout);
        this.layout = layout;
    }

    @Override
    public void initializeGui() {
        super.initializeGui();

        JComponent p = controlPane;
        GBC gbc = controlPaneGBC;

        Object[] arrangement = {
                "Category Prefixing",       categoryPrefixingField,
                "Context Printing",         contextPrintingField,
                "Thread Printing",          threadPrintingField,
        };
        addControlsFromArrangement(p, gbc, arrangement);

        refreshGuiThisLevel();
    }

    void refreshGui() {
        super.refreshGui();
        refreshGuiThisLevel();
    }

    private void refreshGuiThisLevel() {
        categoryPrefixingField.setSelected(layout.getCategoryPrefixing());
        contextPrintingField.setSelected(layout.getContextPrinting());
        threadPrintingField.setSelected(layout.getThreadPrinting());
    }

    @Override
    public void applyChanges() {
        super.applyChanges();
        layout.setCategoryPrefixing(categoryPrefixingField.isSelected());
        layout.setContextPrinting(contextPrintingField.isSelected());
        layout.setThreadPrinting(threadPrintingField.isSelected());
    }

}
