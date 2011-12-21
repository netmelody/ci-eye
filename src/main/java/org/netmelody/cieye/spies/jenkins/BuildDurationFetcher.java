package org.netmelody.cieye.spies.jenkins;

import static com.google.common.cache.CacheLoader.from;

import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class BuildDurationFetcher {

    private final LoadingCache<String, Long> durations;
    
    public BuildDurationFetcher(final BuildDetailFetcher detailFetcher) {
        durations = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(from(new Function<String, Long>() {
                            public Long apply(String buildUrl) {
                                return detailFetcher.detailsOf(buildUrl).duration;
                            }
                        }));
    }
    
    public long lastGoodDurationOf(final JobDetail job) {
        if (!(job.lastStableBuild == null)) {
            return durations.getUnchecked(job.lastStableBuild.url);
        }
        
        if (!(job.lastSuccessfulBuild == null)) {
            return durations.getUnchecked(job.lastSuccessfulBuild.url);
        }
        return 300000L;
    }
}
