package org.netmelody.cieye.server.observation;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyHandler;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

public final class PollingSpy implements CiSpyHandler {

    private static final long POLLING_PERIOD_SECONDS = 5L;
    private static final long CLEANUP_PERIOD_MINUTES = 15L;
    
    private final CiSpy delegate;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentMap<Feature, Long> trackedFeatures = new MapMaker().makeMap();
    private final ConcurrentMap<Feature, StatusResult> statuses = new MapMaker().makeMap();
    
    public PollingSpy(CiSpy delegate) {
        this.delegate = delegate;
        executor.scheduleWithFixedDelay(new StatusUpdater(), 0L, POLLING_PERIOD_SECONDS, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(new StaleEntryCleaner(), 15L, CLEANUP_PERIOD_MINUTES, TimeUnit.MINUTES);
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
            return Math.max(0L, TimeUnit.SECONDS.toMillis(POLLING_PERIOD_SECONDS) - statusResult.ageInMillis());
        }
        return 0L;
    }

    @Override
    public boolean takeNoteOf(String targetId, String note) {
        return delegate.takeNoteOf(new TargetId(targetId), note);
    }
    
    private void update() {
        for (Feature feature : trackedFeatures.keySet()) {
            StatusResult intermediateStatus = statuses.putIfAbsent(feature, new StatusResult());
            if (null == intermediateStatus) {
                intermediateStatus = new StatusResult();
            }
            
            final List<TargetDetail> newStatus = newArrayList();

            final TargetDigestGroup targets = delegate.targetsConstituting(feature);
            for (TargetDigest digest : targets) {
                final TargetDetail target = delegate.statusOf(digest.id());
                newStatus.add(target);
                intermediateStatus = intermediateStatus.updatedWith(target);
                statuses.put(feature, intermediateStatus);
            }
            
            statuses.put(feature, new StatusResult(newStatus, currentTimeMillis()));
        }
    }
    
    private void removeStaleEntries() {
        final long currentTime = currentTimeMillis();
        final Iterator<Entry<Feature, Long>> entries = trackedFeatures.entrySet().iterator();
        while (entries.hasNext()) {
            final Entry<Feature, Long> entry = entries.next();
            if (currentTime - entry.getValue() > TimeUnit.MINUTES.toMillis(CLEANUP_PERIOD_MINUTES)) {
                entries.remove();
            }
        }
    }
    
    private static final class StatusResult {
        private final ImmutableMap<TargetId, TargetDetail> status;
        private final long timestamp;

        public StatusResult() {
            this(new ArrayList<TargetDetail>(), currentTimeMillis());
        }
        public StatusResult(Iterable<TargetDetail> status, long timestamp) {
            this(Maps.uniqueIndex(status, toId()), timestamp);
        }
        private StatusResult(ImmutableMap<TargetId, TargetDetail> status, long timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }
        private static Function<TargetDetail, TargetId> toId() {
            return new Function<TargetDetail, TargetId>() {
                @Override public TargetId apply(TargetDetail input) { return input.id(); }
            };
        }
        public TargetDetailGroup status() {
            return TargetDetailGroup.of(status.values());
        }
        public long ageInMillis() {
            return currentTimeMillis() - timestamp;
        }
        public StatusResult updatedWith(TargetDetail target) {
            Map<TargetId, TargetDetail> newTargets = Maps.newHashMap(status);
            newTargets.put(target.id(), target);
            return new StatusResult(ImmutableMap.copyOf(newTargets), timestamp); 
        }
    }
    
    private final class StatusUpdater implements Runnable {
        @Override public void run() { update(); }
    }
    
    private final class StaleEntryCleaner implements Runnable {
        @Override public void run() { removeStaleEntries(); }
    }
}