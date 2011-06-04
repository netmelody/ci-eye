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

    private final CiSpy delegate;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentMap<Feature, Long> trackedFeatures = new MapMaker()
                                                                    .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                    .makeMap();
    
    private final ConcurrentMap<Feature, StatusResult> statuses = new MapMaker().makeMap();
    
    public PollingSpy(CiSpy delegate) {
        this.delegate = delegate;
        executor.scheduleWithFixedDelay(new StatusUpdater(), 0L, 10L, TimeUnit.SECONDS);
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        final long currentTimeMillis = currentTimeMillis();
        trackedFeatures.put(feature, currentTimeMillis);
        return statuses.putIfAbsent(feature, new StatusResult(new TargetGroup(), currentTimeMillis())).status;
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        long result = 0L;
        
        final StatusResult statusResult = statuses.get(feature);
        if (null != statusResult) {
            result = currentTimeMillis() - statusResult.timestamp;
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
    
    private final class StatusResult {
        private final TargetGroup status;
        private final long timestamp;
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