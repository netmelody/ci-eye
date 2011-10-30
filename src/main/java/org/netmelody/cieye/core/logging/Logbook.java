package org.netmelody.cieye.core.logging;

import org.apache.commons.logging.Log;

public final class Logbook {

    private final Log log;

    public Logbook(Log log) {
        this.log = log;
    }

    public void info(String message) {
        log.info(message);
    }
    
    public void info(String message, Throwable exception) {
        log.info(message, exception);
    }
    
    public void warn(String message) {
        log.warn(message);
    }
    
    public void warn(String message, Throwable exception) {
        log.warn(message, exception);
    }
    
    public void error(String message) {
        log.error(message);
    }
    
    public void error(String message, Throwable exception) {
        log.error(message, exception);
    }
    
    public void fatal(String message) {
        log.fatal(message);
    }
    
    public void fatal(String message, Throwable exception) {
        log.fatal(message, exception);
    }
}
