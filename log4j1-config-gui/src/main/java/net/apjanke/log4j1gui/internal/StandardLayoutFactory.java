package net.apjanke.log4j1gui.internal;

import org.apache.log4j.*;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.xml.XMLLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.apjanke.log4j1gui.internal.Utils.sprintf;

public class StandardLayoutFactory implements LayoutFactory {
    private static final Logger log = LogManager.getLogger(StandardLayoutFactory.class);

    private static final List<Class<? extends Layout>> myLayoutClasses;
    static {
        List<Class<? extends Layout>> tmp = new ArrayList<>();
        tmp.add(PatternLayout.class);
        tmp.add(EnhancedPatternLayout.class);
        tmp.add(DateLayout.class);
        tmp.add(HTMLLayout.class);
        tmp.add(SimpleLayout.class);
        tmp.add(XMLLayout.class);
        tmp.add(TTCCLayout.class);
        myLayoutClasses = Collections.unmodifiableList(tmp);
    }

    @Override
    public Iterable<Class<? extends Layout>> getSupportedLayoutClasses() {
        return myLayoutClasses;
    }

    @Override
    public Layout createLayout(Class<? extends Layout> layoutClass) {
        try {
            return layoutClass.getConstructor().newInstance();
        } catch (Exception e) {
            log.error(sprintf("Error while instantiating %s: %s",
                    layoutClass.getName(), e.getMessage()), e);
            throw new InternalError(sprintf("Error while instantiating %s: %s",
                    layoutClass.getName(), e.getMessage()));
        }
    }
}
