package org.netmelody.cii.witness.jenkins;

import java.util.Map;

import org.netmelody.cii.witness.jenkins.jsondomain.JobDetail;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class BuildDurationFetcher {

    private final Map<String, Long> durations;
    
    public BuildDurationFetcher(final BuildDetailFetcher detailFetcher) {
        durations = new MapMaker()
            .maximumSize(100)
            .makeComputingMap(
                new Function<String, Long>() {
                    public Long apply(String buildUrl) {
                        return detailFetcher.detailsOf(buildUrl).duration;
                    }
                });
    }
    
    public long lastGoodDurationOf(final JobDetail job) {
        if (!(job.lastStableBuild == null)) {
            return durations.get(job.lastStableBuild.url);
        }
        
        if (!(job.lastSuccessfulBuild == null)) {
            return durations.get(job.lastSuccessfulBuild.url);
        }
        return 300000L;
    }
}
