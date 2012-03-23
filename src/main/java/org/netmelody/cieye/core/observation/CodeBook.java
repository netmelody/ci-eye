package org.netmelody.cieye.core.observation;

import java.text.SimpleDateFormat;

public final class CodeBook {

    private final SimpleDateFormat dateFormat;
    
    public CodeBook(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public SimpleDateFormat dateFormat() {
        return dateFormat;
    }

}
