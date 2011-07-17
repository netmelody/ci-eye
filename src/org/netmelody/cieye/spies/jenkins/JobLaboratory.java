package org.netmelody.cieye.spies.jenkins;

import java.util.Map;

import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class JobLaboratory {

    private final JenkinsCommunicator communicator;
    private final KnownOffendersDirectory detective;
    private final Map<String, JobAnalyser> analyserMap = new MapMaker().makeComputingMap(toAnalysers());
    
    public JobLaboratory(JenkinsCommunicator communicator, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.detective = detective;
    }
       
    public TargetDetail analyseJob(Job jobDigest) {
        return analyserMap.get(jobDigest.url).analyse(jobDigest);
    }

    public String lastBadBuildUrlFor(Job jobDigest) {
        if (analyserMap.containsKey(jobDigest.url)) {
            return analyserMap.get(jobDigest.url).lastBadBuildUrl();
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
