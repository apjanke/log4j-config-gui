package net.apjanke.log4j1gui.internal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.apache.log4j.varia.StringMatchFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.apjanke.log4j1gui.internal.Utils.sprintf;

public class StandardFilterFactory implements FilterFactory {
    private static final Logger log = LogManager.getLogger(StandardFilterFactory.class);

    private static final List<Class<? extends Filter>> myFilterClasses;
    static {
        List<Class<? extends Filter>> tmp = new ArrayList<>();
        tmp.add(DenyAllFilter.class);
        tmp.add(LevelMatchFilter.class);
        tmp.add(LevelRangeFilter.class);
        tmp.add(StringMatchFilter.class);
        myFilterClasses = Collections.unmodifiableList(tmp);
    }


    @Override
    public Iterable<Class<? extends Filter>> getSupportedFilterClasses() {
        return myFilterClasses;
    }

    @Override
    public Filter createFilter(Class<? extends Filter> filterClass) {
        requireNonNull(filterClass);
        if (!myFilterClasses.contains(filterClass)) {
            throw new IllegalArgumentException("StandardFilterFactory does not support Filter type: "
                    + filterClass.getName());
        }
        try {
            return filterClass.newInstance();
        } catch (Exception e) {
            log.error(sprintf("Error while instantiating %s: %s",
                    filterClass.getName(), e.getMessage()), e);
            throw new InternalError(sprintf("Error while instantiating %s: %s",
                    filterClass.getName(), e.getMessage()));
        }
    }
}
