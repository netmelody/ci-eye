package org.netmelody.cieye.spies.jenkins;

import static com.google.common.cache.CacheLoader.from;

import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class JobLaboratory {

    private final JenkinsCommunicator communicator;
    private final KnownOffendersDirectory detective;
    private final LoadingCache<String, JobAnalyser> analyserMap = CacheBuilder.newBuilder().build(from(toAnalysers()));
    
    public JobLaboratory(JenkinsCommunicator communicator, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.detective = detective;
    }
       
    public TargetDetail analyseJob(Job jobDigest) {
        return analyserMap.getUnchecked(jobDigest.url).analyse(jobDigest);
    }

    public String lastBadBuildUrlFor(Job jobDigest) {
        if (analyserMap.asMap().containsKey(jobDigest.url)) {
            return analyserMap.getUnchecked(jobDigest.url).lastBadBuildUrl();
        }
        return "";
    }
    
    private Function<String, JobAnalyser> toAnalysers() {
        return new Function<String, JobAnalyser>() {
            @Override public JobAnalyser apply(String jobDigestUrl) {
                return new JobAnalyser(communicator, jobDigestUrl, detective);
            }
        };
    }
}
