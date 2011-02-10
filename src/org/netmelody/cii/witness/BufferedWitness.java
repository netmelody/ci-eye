package org.netmelody.cii.witness;

import static java.lang.Math.max;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;

public class BufferedWitness implements Witness {

    private static final long DEFAULT_BUFFER_TIME = 10000L;

    private final Witness delegate;
    private final long bufferTime;
    
    private long lastRequestTime = 0L;
    private TargetGroup lastStatus;

    public BufferedWitness(Witness delegate) {
        this(delegate, DEFAULT_BUFFER_TIME);
    }
    
    public BufferedWitness(Witness delegate, long bufferTime) {
        this.delegate = delegate;
        this.bufferTime = bufferTime;
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        if ((System.currentTimeMillis() - lastRequestTime) > bufferTime) {
            lastRequestTime = System.currentTimeMillis();
            lastStatus = delegate.statusOf(feature);
        }
        return lastStatus;
    }

    @Override
    public long millisecondsUntilNextUpdate() {
        return max(lastRequestTime + bufferTime - System.currentTimeMillis(), 0L);
    }
}
