package org.netmelody.cieye.witness.jenkins;

import java.util.Map;

import org.netmelody.cieye.witness.jenkins.jsondomain.JobDetail;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class BuildStartTimeFetcher {

    private final Map<String, Long> timestamps;
    
    public BuildStartTimeFetcher(final BuildDetailFetcher detailFetcher) {
        timestamps = new MapMaker()
            .maximumSize(10)
            .makeComputingMap(
                new Function<String, Long>() {
                    public Long apply(String buildUrl) {
                        return detailFetcher.detailsOf(buildUrl).timestamp;
                    }
                });
    }
    
    public long lastStartTimeOf(final JobDetail job) {
        if (!(job.lastBuild == null)) {
            return timestamps.get(job.lastBuild.url);
        }
        return 0L;
    }
}
