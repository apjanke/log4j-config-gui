package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;

import javax.swing.table.AbstractTableModel;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

class AppenderListTableModel extends AbstractTableModel {
    private List<Appender> appenders;
    private static final String[] columnNames = new String[] {
            "Class", "Name", "Layout", "Filters", "ErrorHandler", "Object ID"
    };

    AppenderListTableModel(java.util.List<Appender> appenders) {
        this.appenders = requireNonNull(appenders);
    }

    void setAppenders(java.util.List<Appender> appenders) {
        this.appenders = requireNonNull(appenders);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return appenders.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Appender appender = appenders.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return appender.getClass();
            case 1:
                return appender.getName();
            case 2:
                return appender.getLayout();
            case 3:
                return appender.getFilter();
            case 4:
                return appender.getErrorHandler();
            case 5:
                return ""+appender.toString();
            default:
                throw new ArrayIndexOutOfBoundsException(columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Class.class;
            case 1:
                return String.class;
            case 2:
                return Layout.class;
            case 3:
                return Filter.class;
            case 4:
                return ErrorHandler.class;
            case 5:
                return String.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Appender appender = appenders.get(rowIndex);
        switch (columnIndex) {
            case 1:
                appender.setName((String)aValue);
                break;
            default:
                throw new IllegalArgumentException(sprintf("Value setting not allowed for this field (column %d)", columnIndex));
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 1:
                return true;
            default:
                return false;
        }
    }
}
