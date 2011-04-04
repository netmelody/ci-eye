package org.netmelody.cieye.spies.jenkins;

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.spies.jenkins.jsondomain.ViewDetail;

import com.google.common.base.Function;

public final class ViewAnalyser {

    private final JenkinsCommunicator communicator;
    private final Map<String, JobAnalyser> analyserMap = new HashMap<String, JobAnalyser>();
    private final KnownOffendersDirectory detective;
    
    public ViewAnalyser(JenkinsCommunicator communicator, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.detective = detective;
    }
    
    public Collection<Target> analyse(View viewDigest) {
        return transform(jobsFor(viewDigest), new Function<Job, Target>() {
            @Override public Target apply(Job job) {
                return targetFrom(job);
            }
        });
    }
    
    public String lastBadBuildUrlFor(String jobId) {
        if (analyserMap.containsKey(jobId)) {
            return analyserMap.get(jobId).lastBadBuildUrl();
        }
        return null;
    }
    
    private Collection<Job> jobsFor(View view) {
        return communicator.makeJenkinsRestCall(view.url, ViewDetail.class).jobs();
    }
    
    private Target targetFrom(Job jobDigest) {
        if (!analyserMap.containsKey(jobDigest.url)) {
            analyserMap.put(jobDigest.url, new JobAnalyser(communicator, jobDigest.url, detective));
        }
        return analyserMap.get(jobDigest.url).analyse(jobDigest);
    }
}
