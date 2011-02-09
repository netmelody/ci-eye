package org.netmelody.cii.witness;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;

public class BufferedWitness implements Witness {

    private static final long BUFFER_TIME = 5000L;

    private final Witness delegate;
    
    private long lastRequestTime = 0L;
    private TargetGroup lastStatus;

    public BufferedWitness(Witness delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        if ((System.currentTimeMillis() - lastRequestTime) > BUFFER_TIME) {
            lastRequestTime = System.currentTimeMillis();
            lastStatus = delegate.statusOf(feature);
        }
        
        return lastStatus;
    }

}
