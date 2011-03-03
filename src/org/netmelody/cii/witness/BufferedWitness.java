package org.netmelody.cii.witness;

import static java.lang.Math.max;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class BufferedWitness implements Witness {

    private static final long DEFAULT_BUFFER_TIME = 10000L;

    private final Map<Feature, Long> requestTimeCache = new HashMap<Feature, Long>();
    private final Map<Feature, TargetGroup> resultCache;
    
    private final long bufferTime;

    public BufferedWitness(Witness delegate) {
        this(delegate, DEFAULT_BUFFER_TIME);
    }
    
    public BufferedWitness(final Witness delegate, final long bufferTime) {
        this.bufferTime = bufferTime;
        
        this.resultCache =
            new MapMaker()
                .expireAfterWrite(bufferTime, TimeUnit.MILLISECONDS)
                .makeComputingMap(new Function<Feature, TargetGroup>() {
                     @Override
                     public TargetGroup apply(Feature feature) {
                         requestTimeCache.put(feature, System.currentTimeMillis());
                         return delegate.statusOf(feature);
                     }
                });
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        return resultCache.get(feature);
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return max(requestTimeCache.get(feature) + bufferTime - System.currentTimeMillis(), 0L);
    }
}
