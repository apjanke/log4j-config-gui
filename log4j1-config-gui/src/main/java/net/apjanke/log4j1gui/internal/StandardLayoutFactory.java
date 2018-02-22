package net.apjanke.log4j1gui.internal;

import org.apache.log4j.*;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.spi.LoggingEvent;
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
        // EnhancedPatternLayout is only available if Log4j Extras is loaded
        try {
            Class.forName("org.apache.log4j.EnhancedPatternLayout");
            tmp.add(EnhancedPatternLayout.class);
        } catch (ClassNotFoundException e) {
            log.debug(sprintf("Class %s not found; looks like Log4j Extras is absent. Skipping.",
                    "org.apache.log4j.EnhancedPatternLayout"));
        }
        // DateLayout is abstract; can't instantiate it
        //tmp.add(DateLayout.class);
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
