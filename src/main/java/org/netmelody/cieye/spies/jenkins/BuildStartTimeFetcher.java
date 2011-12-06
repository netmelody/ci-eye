package org.netmelody.cieye.spies.jenkins;

import java.util.concurrent.ExecutionException;

import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static com.google.common.cache.CacheLoader.from;

public final class BuildStartTimeFetcher {

    private final Cache<String, Long> timestamps;
    
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