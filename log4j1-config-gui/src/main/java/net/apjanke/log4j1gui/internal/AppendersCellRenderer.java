package net.apjanke.log4j1gui.internal;

import net.apjanke.log4j1gui.Log4jConfiguratorGui;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;

import javax.swing.table.DefaultTableCellRenderer;

import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;

public class AppendersCellRenderer extends DefaultTableCellRenderer {
    @Override
    public void setValue(Object value) {
        String str;
        if (null == value) {
            str = "";
        } else {
            if (value instanceof Class) {
                str = nameWithoutLog4jPackage(((Class) value).getName());
            } else if (value instanceof PatternLayout) {
                str = Log4jConfiguratorGui.layoutString((PatternLayout) value);
            } else if (value instanceof Layout) {
                str = nameWithoutLog4jPackage(value.toString());
            } else if (value instanceof Filter) {
                str = nameWithoutLog4jPackage(value.toString());
            } else if (value instanceof ErrorHandler) {
                str = nameWithoutLog4jPackage(value.toString());
            } else {
                str = "" + value;
            }
        }
        setText(str);
    }

}