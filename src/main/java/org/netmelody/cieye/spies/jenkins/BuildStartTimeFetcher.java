package org.netmelody.cieye.spies.jenkins;

import static com.google.common.cache.CacheLoader.from;

import java.util.concurrent.ExecutionException;

import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class BuildStartTimeFetcher {

    private final LoadingCache<String, Long> timestamps;
    
    public BuildStartTimeFetcher(final BuildDetailFetcher detailFetcher) {
        timestamps = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(from(new Function<String, Long>() {
                            public Long apply(String buildUrl) {
                                return detailFetcher.detailsOf(buildUrl).timestamp;
                            }
                        }));
    }
    
    public long lastStartTimeOf(final JobDetail job) {
        if (job.lastBuild == null) {
            return 0L;
        }
        
        try {
            return timestamps.get(job.lastBuild.url);
        } catch (ExecutionException e) {
            return 0L;
        }
    }
}