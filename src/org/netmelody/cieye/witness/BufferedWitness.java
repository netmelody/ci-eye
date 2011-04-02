package org.netmelody.cieye.witness;

import static java.lang.Math.max;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class BufferedWitness implements CiSpy {

    private static final long DEFAULT_BUFFER_TIME = 10000L;

    private final CiSpy delegate;
    private final Map<Feature, Long> requestTimeCache = new HashMap<Feature, Long>();
    private final Map<Feature, TargetGroup> resultCache;
    
    private final long bufferTime;

    public BufferedWitness(CiSpy delegate) {
        this(delegate, DEFAULT_BUFFER_TIME);
    }
    
    public BufferedWitness(final CiSpy delegate, final long bufferTime) {
        this.delegate = delegate;
        this.bufferTime = bufferTime;
        
        this.resultCache =
            new MapMaker()
                .expireAfterWrite(bufferTime, TimeUnit.MILLISECONDS)
                .makeComputingMap(new StatusComputer(requestTimeCache, delegate));
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        return resultCache.get(feature);
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return max(requestTimeCache.get(feature) + bufferTime - System.currentTimeMillis(), 0L);
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        return this.delegate.takeNoteOf(targetId, note);
    } 
    
    public static final class StatusComputer implements Function<Feature, TargetGroup> {
        private static final Log LOG = LogFactory.getLog(StatusComputer.class);
        
        private final Map<Feature, Long> requestTimeCache;
        private final CiSpy witness;

        public StatusComputer(Map<Feature, Long> requestTimeCache, CiSpy witness) {
            this.requestTimeCache = requestTimeCache;
            this.witness = witness;
        }
        
        @Override
        public TargetGroup apply(Feature feature) {
            requestTimeCache.put(feature, System.currentTimeMillis());
            try {
                return witness.statusOf(feature);
            }
            catch (Exception e) {
                LOG.error(String.format("Failed to get status of feature (%s)", feature.name()), e);
            }
            return new TargetGroup();
        }
    }
}
