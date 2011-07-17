package org.netmelody.cieye.server.observation;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.System.currentTimeMillis;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyHandler;

import com.google.common.collect.MapMaker;

public final class PollingSpy implements CiSpyHandler {

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
        final StatusResult result = statuses.get(feature);
        if (null != result) {
            return result.status;
        }
        
        final TargetGroup digest = new TargetGroup(delegate.targetsConstituting(feature));
        statuses.putIfAbsent(feature, new StatusResult(digest, currentTimeMillis));
        
        return digest;
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        final StatusResult statusResult = statuses.get(feature);
        if (null != statusResult) {
            return Math.max(0L, (POLLING_PERIOD_SECONDS * 1000L) - (currentTimeMillis() - statusResult.timestamp));
        }
        return 0L;
    }

    @Override
    public boolean takeNoteOf(String targetId, String note) {
        return delegate.takeNoteOf(targetId, note);
    }
    
    private void update() {
        for (Feature feature : trackedFeatures.keySet()) {
            statuses.put(feature, new StatusResult(copyOf(delegate.statusOf(feature).targets()), currentTimeMillis()));
        }
    }
    
    private static final class StatusResult {
        public final TargetGroup status;
        public final long timestamp;
        public StatusResult(Iterable<Target> targets, long timestamp) {
            this.status = new TargetGroup(targets);
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