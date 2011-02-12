package org.netmelody.cii.witness.jenkins;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.Collection;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.jenkins.jsondomain.JenkinsDetails;
import org.netmelody.cii.witness.jenkins.jsondomain.JenkinsUserDetails;
import org.netmelody.cii.witness.jenkins.jsondomain.Job;
import org.netmelody.cii.witness.jenkins.jsondomain.UserDetail;
import org.netmelody.cii.witness.jenkins.jsondomain.View;
import org.netmelody.cii.witness.protocol.RestRequester;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JenkinsWitness implements Witness {
    
    private final Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    private final RestRequester restRequester = new RestRequester();
    private final String endpoint;
    private final JobAnalyser jobAnalyser;

    public JenkinsWitness(String endpoint) {
        this.endpoint = endpoint;
        this.jobAnalyser = new JobAnalyser(restRequester, endpoint);
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        if (!endpoint.equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        final View view = filter(views(), new Predicate<View>() {
            @Override public boolean apply(View view) {
                return view.name.startsWith(feature.name());
            }
        }).iterator().next();
        
        final Collection<Target> targets = transform(jobsFor(view), new Function<Job, Target>() {
            @Override public Target apply(Job job) {
                return targetFrom(job);
            }
        });
        
        return new TargetGroup(targets);
    }
    
    @Override
    public long millisecondsUntilNextUpdate() {
        return 0L;
    }
    
    public Collection<String> users() {
        final JenkinsUserDetails detail = makeJenkinsRestCall(endpoint + "/people", JenkinsUserDetails.class);
        return transform(detail.users, new Function<UserDetail, String>() {
            @Override public String apply(UserDetail userDetail) { return userDetail.user.fullName; }
        });
    }
    
    private Collection<View> views() {
        return makeJenkinsRestCall(endpoint, JenkinsDetails.class).views;
    }

    private Collection<Job> jobsFor(View viewDigest) {
        return makeJenkinsRestCall(viewDigest.url, View.class).jobs;
    }
    
    private Target targetFrom(Job jobDigest) {
        if (!jobDigest.building() && Status.BROKEN != jobDigest.status()) {
            return new Target(jobDigest.name, jobDigest.status());
        }
        
        final Job job = makeJenkinsRestCall(jobDigest.url, Job.class);
        if (job.lastBuild == null || job.lastSuccessfulBuild == null) {
            return new Target(jobDigest.name, jobDigest.status(), buildAt(percentageOf(0)));
        }
        
        return new Target(jobDigest.name, jobDigest.name, jobDigest.status(),
                          jobAnalyser.sponsorsOf(job), buildAt(jobAnalyser.progressOf(job)));
    }

//    private Computer agentDetails(String agentName) {
//        return makeJenkinsRestCall(endpoint + "/computer/" + agentName, Computer.class);
//    }
    
//    private void changeDescription(String jobName, String buildNumber, String newDescription) {
//        "/submitDescription?Submit=Submit&description=" + encodeURI(change.desc) + "&json={\"description\":\"" + change.desc + "\"}";
//    }
    
    private <T> T makeJenkinsRestCall(String url, Class<T> type) {
        return json.fromJson(restRequester.makeRequest(url + "/api/json"), type);
    }
}
