package org.netmelody.cieye.server.observation;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
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
    public TargetDetailGroup statusOf(Feature feature) {
        final long currentTimeMillis = currentTimeMillis();
        trackedFeatures.put(feature, currentTimeMillis);
        final StatusResult result = statuses.get(feature);
        if (null != result) {
            return result.status();
        }
        
        final TargetDetailGroup digest = new TargetDetailGroup(delegate.targetsConstituting(feature));
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
            StatusResult intermediateStatus = statuses.putIfAbsent(feature, new StatusResult());
            if (null == intermediateStatus) {
                intermediateStatus = new StatusResult();
            }
            
            final TargetDetailGroup snapshot = delegate.statusOf(feature);
            final List<TargetDetail> newStatus = newArrayList();
            
            for (TargetDetail target : snapshot) {
                newStatus.add(target);
                intermediateStatus = intermediateStatus.updatedWith(target);
                statuses.put(feature, intermediateStatus);
            }
            
            statuses.put(feature, new StatusResult(newStatus, currentTimeMillis()));
        }
    }
    
    private static final class StatusResult {
        private final Iterable<TargetDetail> targets;
        public final long timestamp;

        public StatusResult() {
            this(new ArrayList<TargetDetail>(), currentTimeMillis());
        }
        public StatusResult(Iterable<TargetDetail> targets, long timestamp) {
            this.targets = targets;
            this.timestamp = timestamp;
        }
        public TargetDetailGroup status() {
            return new TargetDetailGroup(targets);
        }
        public StatusResult updatedWith(TargetDetail target) {
            final List<TargetDetail> newStatus = newArrayList(targets);
            Iterator<TargetDetail> iterator = newStatus.iterator();
            while (iterator.hasNext()) {
                if(iterator.next().id().equals(target.id())) {
                    iterator.remove();
                    break;
                }
            }
            newStatus.add(target);
            return new StatusResult(newStatus, timestamp);
        }
    }
    
    private final class StatusUpdater implements Runnable {
        @Override
        public void run() {
            update();
        }
    }
}