package net.apjanke.log4j1gui.internal;

import org.apache.log4j.spi.Filter;

interface FilterFactory {

    Iterable<Class<? extends Filter>> getSupportedFilterClasses();

    Filter createFilter(Class<? extends Filter> filterClass);
}
