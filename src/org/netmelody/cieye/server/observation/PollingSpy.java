package org.netmelody.cieye.server.observation;

import static java.lang.System.currentTimeMillis;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;

import com.google.common.collect.MapMaker;

public final class PollingSpy implements CiSpy {

    private static final long POLLING_PERIOD_SECONDS = 5L;
    
    private final CiSpy delegate;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentMap<Feature, Long> trackedFeatures = new MapMaker()
                                                                    .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                    .makeMap();
    
    private final ConcurrentMap<Feature, StatusResult> statuses = new MapMaker().makeMap();
    
    public PollingSpy(CiSpy delegate) {
        this.delegate = delegate;
        executor.scheduleWithFixedDelay(new StatusUpdater(), 0L, POLLING_PERIOD_SECONDS, TimeUnit.SECONDS);
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        final long currentTimeMillis = currentTimeMillis();
        trackedFeatures.put(feature, currentTimeMillis);
        StatusResult result = statuses.get(feature);
        return (null == result) ? new TargetGroup() : result.status;
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        long result = 0L;
        
        final StatusResult statusResult = statuses.get(feature);
        if (null != statusResult) {
            result = Math.max(0L, (POLLING_PERIOD_SECONDS * 1000L) - (currentTimeMillis() - statusResult.timestamp));
        }
        return Math.max(result, delegate.millisecondsUntilNextUpdate(feature));
    }

    @Override
    public boolean takeNoteOf(String targetId, String note) {
        return delegate.takeNoteOf(targetId, note);
    }
    
    private void update() {
        for (Feature feature : trackedFeatures.keySet()) {
            statuses.put(feature, new StatusResult(delegate.statusOf(feature), currentTimeMillis()));
        }
    }
    
    private static final class StatusResult {
        public final TargetGroup status;
        public final long timestamp;
        public StatusResult(TargetGroup status, long timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }
    }
    
    private final class StatusUpdater implements Runnable {
        @Override
        public void run() {
            update();
        }
    }
}