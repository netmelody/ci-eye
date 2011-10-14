package org.netmelody.cieye.core.logging;

import org.apache.commons.logging.LogFactory;

public final class LogKeeper {

    private LogKeeper() { }
    
    public static final Logbook logbookFor(Class<?> target) {
        return new Logbook(LogFactory.getLog(target));
    }
}
