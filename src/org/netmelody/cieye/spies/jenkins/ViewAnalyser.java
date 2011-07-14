package org.netmelody.cieye.spies.jenkins;

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.Map;

import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.spies.jenkins.jsondomain.ViewDetail;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class ViewAnalyser {

    private final JenkinsCommunicator communicator;
    private final KnownOffendersDirectory detective;
    private final Map<String, JobAnalyser> analyserMap = new MapMaker().makeComputingMap(toAnalysers());
    
    public ViewAnalyser(JenkinsCommunicator communicator, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.detective = detective;
    }
    
    public Collection<Target> analyse(View viewDigest) {
        return transform(jobsFor(viewDigest), toTargets());
    }

    public String lastBadBuildUrlFor(String jobId) {
        if (analyserMap.containsKey(jobId)) {
            return analyserMap.get(jobId).lastBadBuildUrl();
        }
        return "";
    }
    
    private Collection<Job> jobsFor(View viewDigest) {
        return communicator.makeJenkinsRestCall(viewDigest.url, ViewDetail.class).jobs();
    }
    
    private Function<Job, Target> toTargets() {
        return new Function<Job, Target>() {
            @Override public Target apply(Job jobDigest) {
                return analyserMap.get(jobDigest.url).analyse(jobDigest);
            }
        };
    }
    
    private Function<String, JobAnalyser> toAnalysers() {
        return new Function<String, JobAnalyser>() {
            @Override public JobAnalyser apply(String jobDigestUrl) {
                return new JobAnalyser(communicator, jobDigestUrl, detective);
            }
        };
    }
}
