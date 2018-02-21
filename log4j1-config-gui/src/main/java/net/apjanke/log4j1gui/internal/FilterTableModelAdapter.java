package net.apjanke.log4j1gui.internal;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.apache.log4j.varia.StringMatchFilter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import static net.apjanke.log4j1gui.internal.Utils.nameWithoutLog4jPackage;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

class FilterTableModelAdapter extends AbstractTableModel {

    private static final String[] columnNames = {
            "Filter", "Class", "Attributes"
    };
    private static final Class<?>[] columnClasses = {
            Filter.class, String.class, String.class
    };

    private final java.util.List<Filter> filters = new ArrayList<>();

    FilterTableModelAdapter() {
    }

    void setHeadFilter(Filter headFilter) {
        filters.clear();
        Filter f = headFilter;
        while (f != null) {
            filters.add(f);
            f = f.getNext();
        }
        fireTableDataChanged();
    }

    java.util.List<Filter> getFilters() {
        return filters;
    }

    @Override
    public int getRowCount() {
        return filters.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Filter filter = filters.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return nameWithoutLog4jPackage(""+filter);
            case 1:
                return nameWithoutLog4jPackage(filter.getClass().getName());
            case 2:
                String str = null;
                if (filter instanceof LevelMatchFilter) {
                    LevelMatchFilter f = (LevelMatchFilter) filter;
                    str = f.getLevelToMatch() + ": " + (f.getAcceptOnMatch() ? "ACCEPT" : "DENY");
                } else if (filter instanceof LevelRangeFilter) {
                    LevelRangeFilter f = (LevelRangeFilter) filter;
                    str = sprintf("%s %s - %s", (f.getAcceptOnMatch() ? "ACCEPT" : "DENY"),
                            f.getLevelMin(), f.getLevelMax());
                } else if (filter instanceof StringMatchFilter) {
                    StringMatchFilter f = (StringMatchFilter) filter;
                    str = sprintf("%s: \"%s\"", (f.getAcceptOnMatch() ? "ACCEPT" : "DENY"),
                            f.getStringToMatch());
                }
                return str;
            default:
                throw new IndexOutOfBoundsException(sprintf(
                        "Got columnIndex %d, max is 2", columnIndex));
        }
    }
}
