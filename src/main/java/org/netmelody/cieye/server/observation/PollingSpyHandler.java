package org.netmelody.cieye.server.observation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyHandler;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

public final class PollingSpyHandler implements CiSpyHandler {

    private static final Logbook LOG = LogKeeper.logbookFor(PollingSpyHandler.class);

    private static final long POLLING_PERIOD_SECONDS = 5L;
    private static final long CUTOFF_PERIOD_MINUTES = 15L;

    private final CiSpy trustedSpy;
    private final ScheduledExecutorService executor;

    private final ConcurrentMap<Feature, Long> requests = new MapMaker().makeMap();
    private final ConcurrentMap<Feature, StatusResult> statuses = new MapMaker().makeMap();

    public PollingSpyHandler(CiSpy untrustedSpy, Feature feature) {
        this.trustedSpy = new TrustedSpy(untrustedSpy);
        this.executor = Executors.newSingleThreadScheduledExecutor(threadsNamed(feature, untrustedSpy));
        this.executor.scheduleWithFixedDelay(new StatusUpdater(), 0L, POLLING_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    private ThreadFactory threadsNamed(Feature feature, CiSpy untrustedSpy) {
        String threadPrefix = format("%s-%s-%s", 
                untrustedSpy.getClass().getSimpleName(), 
                feature.type().name(), 
                feature.name());
        return new ThreadFactoryBuilder().setNameFormat(threadPrefix + "-%d").build();
    }

    @Override
    public TargetDetailGroup statusOf(Feature feature) {
        final long currentTimeMillis = currentTimeMillis();
        requests.put(feature, currentTimeMillis);
        
        final StatusResult result = statuses.get(feature);
        if (null != result) {
            return result.status();
        }
        
        final TargetDetailGroup digest = new TargetDetailGroup(trustedSpy.targetsConstituting(feature));
        statuses.putIfAbsent(feature, new StatusResult(digest));
        
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
        return trustedSpy.takeNoteOf(new TargetId(targetId), note);
    }
    
    @Override
    public void endMission() {
        this.executor.shutdown();
    }
    
    private void update() {
        final long cutoffTime = currentTimeMillis() - TimeUnit.MINUTES.toMillis(CUTOFF_PERIOD_MINUTES);
        final Iterable<Feature> features = transform(filter(requests.entrySet(), requestedAfter(cutoffTime)), toFeature());
        
        for (Feature feature : features) {
            final TargetDigestGroup targets = trustedSpy.targetsConstituting(feature);
            
            StatusResult intermediateStatus = statuses.get(feature);
            if (null == intermediateStatus) {
                intermediateStatus = new StatusResult(new TargetDetailGroup(targets));
                statuses.putIfAbsent(feature, intermediateStatus);
            }
            
            final List<TargetDetail> newStatus = newArrayList();
            for (TargetDigest digest : targets) {
                final TargetDetail target = trustedSpy.statusOf(digest.id());
                newStatus.add(target);
                intermediateStatus = intermediateStatus.updatedWith(target);
                statuses.put(feature, intermediateStatus);
            }
            
            statuses.put(feature, new StatusResult(newStatus));
        }
    }
    
    private static Function<Entry<Feature, Long>, Feature> toFeature() {
        return new Function<Entry<Feature,Long>, Feature>() {
            @Override public Feature apply(Entry<Feature, Long> input) { return input.getKey(); }
        };
    }

    private static Predicate<Entry<?, Long>> requestedAfter(final long cutoffTimeMillis) {
        return new Predicate<Entry<?, Long>>() {
            @Override public boolean apply(Entry<?, Long> input) { return input.getValue() > cutoffTimeMillis; }
        };
    }
    
    private static final class StatusResult {
        private final ImmutableMap<TargetId, TargetDetail> status;
        private final long timestamp;

        public StatusResult(Iterable<TargetDetail> status) {
            this(Maps.uniqueIndex(status, toId()), System.currentTimeMillis());
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
        @Override public void run() {
            try {
                update();
            }
            catch (Exception e) {
                LOG.fatal("Status update failed.", e);
            }
        }
    }

}