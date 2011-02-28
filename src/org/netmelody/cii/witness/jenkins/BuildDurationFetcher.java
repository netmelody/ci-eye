package org.netmelody.cii.witness.jenkins;

import java.util.Map;

import org.netmelody.cii.witness.jenkins.jsondomain.Build;
import org.netmelody.cii.witness.jenkins.jsondomain.Job;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class BuildDurationFetcher {

    private final Map<String, Long> durations;
    
    public BuildDurationFetcher(final JenkinsCommunicator communicator) {
        durations = new MapMaker()
            .maximumSize(100)
            .makeComputingMap(
                new Function<String, Long>() {
                    public Long apply(String buildUrl) {
                        return communicator.makeJenkinsRestCall(buildUrl, Build.class).duration;
                    }
                });
    }
    
    public long lastGoodDurationOf(final Job job) {
        if (!(job.lastStableBuild == null)) {
            return durations.get(job.lastStableBuild.url);
        }
        
        if (!(job.lastSuccessfulBuild == null)) {
            return durations.get(job.lastSuccessfulBuild.url);
        }
        return 300000L;
    }
}
