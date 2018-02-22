package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Log4jConfiguratorGui;
import org.apache.log4j.EnhancedPatternLayout;

public class AppendersCellRendererWithExtras extends AppendersCellRenderer {
    @Override
    public void setValue(Object value) {
        if (value instanceof EnhancedPatternLayout) {
            String str = Log4jConfiguratorGui.layoutString((EnhancedPatternLayout) value);
            setText(str);
        } else {
            super.setValue(value);
        }
    }

}