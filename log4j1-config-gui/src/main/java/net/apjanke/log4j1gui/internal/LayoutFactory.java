package net.apjanke.log4j1gui.internal;

import org.apache.log4j.Layout;

interface LayoutFactory {

    Iterable<Class<? extends Layout>> getSupportedLayoutClasses();

    Layout createLayout(Class<? extends Layout> layoutClass);
}
