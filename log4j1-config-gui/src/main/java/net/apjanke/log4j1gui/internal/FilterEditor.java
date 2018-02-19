package net.apjanke.log4j1gui.internal;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.apache.log4j.varia.StringMatchFilter;

import static java.util.Objects.requireNonNull;

public abstract class FilterEditor extends ThingEditor {
    private static final Logger log = LogManager.getLogger(AppenderEditor.class);

    public abstract void initializeGui();

    public abstract void applyChanges();

    FilterEditor(Filter filter) {
        super(requireNonNull(filter));
    }

    public static boolean isEditable(Class<? extends Filter> klass) {
        return !(klass == DenyAllFilter.class);
    }

    public static FilterEditor createEditorFor(Filter filter) throws FilterEditor.UnrecognizedFilterException {
        requireNonNull(filter);
        if (filter instanceof DenyAllFilter) {
            return new DenyAllFilterEditor((DenyAllFilter) filter);
        } else if (filter instanceof StringMatchFilter) {
            return new StringMatchFilterEditor((StringMatchFilter) filter);
        } else if (filter instanceof LevelMatchFilter) {
            return new LevelMatchFilterEditor((LevelMatchFilter) filter);
        } else if (filter instanceof LevelRangeFilter) {
            return new LevelRangeFilterEditor((LevelRangeFilter) filter);
        } else {
            throw new UnrecognizedFilterException("No appender editor defined for class " + filter.getClass().getName());
        }
    }

    static class UnrecognizedFilterException extends Exception {
        UnrecognizedFilterException(String message) {
            super(message);
        }
    }

}
