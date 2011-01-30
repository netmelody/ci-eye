package org.netmelody.cii.witness.jenkins;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Collections2.filter;

import java.util.Collection;
import java.util.List;

import org.netmelody.cii.RestRequest;
import org.netmelody.cii.witness.Witness;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.gson.Gson;

public final class JenkinsWitness implements Witness {
    
    private final Gson json = new Gson();
    private final String endpoint;

    public JenkinsWitness(String endpoint) {
        this.endpoint = endpoint;
    }

    public static void main(String[] args) {
        JenkinsWitness witness = new JenkinsWitness("http://deadlock.netbeans.org/hudson");
        View view = filter(witness.views(), new Predicate<View>() {
            @Override public boolean apply(View view) {
                return view.name.startsWith("Push");
            }}).iterator().next();
        
        final Collection<Job> failedJobs = witness.failedJobsFor(view);
        System.out.println(witness.jobDetails(failedJobs.iterator().next()));
    }
    
    public Collection<String> users() {
        JenkinsUserDetails detail = json.fromJson(makeJenkinsRestCall(endpoint + "/people"), JenkinsUserDetails.class);
        return transform(detail.users, new Function<UserDetail, String>() {
            @Override public String apply(UserDetail userDetail) { return userDetail.user.fullName; }
        });
    }
    
    private Collection<View> views() {
        JenkinsDetails detail = json.fromJson(makeJenkinsRestCall(endpoint), JenkinsDetails.class);
        return detail.views;
    }
    
    private Collection<Job> failedJobsFor(View viewDigest) {
        View view = json.fromJson(makeJenkinsRestCall(viewDigest.url), View.class);
        return filter(view.jobs, new Predicate<Job>() {
            @Override public boolean apply(Job job) {
                return !"blue".equals(job.color);
            }
        });
    }
    
    private Collection<String> jobDetails(Job jobDigest) {
        Job job = json.fromJson(makeJenkinsRestCall(jobDigest.url), Job.class);
        
        if (job.lastBuild != null) {
            Build build = json.fromJson(makeJenkinsRestCall(job.lastBuild.url), Build.class);
            if (build.building && build.buildOn != null) {
                agentDetails(build.buildOn);
            }
        }
        
        return null;
    }
    
    private Collection<String> agentDetails(String agentName) {
        Computer computer = json.fromJson(makeJenkinsRestCall("/computer/" + agentName), Computer.class);
        return null;
    }
    
    private void changeDescription(String jobName, String buildNumber, String newDescription) {
        //"/submitDescription?Submit=Submit&description=" + encodeURI(change.desc) + "&json={\"description\":\"" + change.desc + "\"}";
    }
    
    private String makeJenkinsRestCall(String url) {
        return RestRequest.makeRequest(url + "/api/json");
    }
    
    static class JenkinsDetails {
        List<Label> assignedLabels;
        String mode;
        String nodeName;
        String nodeDescription;
        int numExecutors;
        String description;
        List<Job> jobs;
        Load overallLoad;
        View primaryView;
        int slaveAgentPort;
        boolean useCrumbs;
        boolean useSecurity;
        List<View> views;
    }
    
    static class JenkinsUserDetails {
        List<UserDetail> users;
    }

    static class UserDetail {
        long lastChange;
        Project project;
        User user;
    }
    
    static class Project {
        String name;
        String url;
    }
    
    static class User {
        String absoluteUrl;
        String fullName;
    }
    
    static class Job {
        String name;
        String url;
        String displayName;
        String description;
        String color;
        boolean buildable;
        boolean inQueue;
        boolean keepDependencies;
        boolean concurrentBuild;
        List<Build> builds;
        List<Action> actions;
        Build firstBuild;
        Build lastBuild;
        Build lastFailedBuild;
        Build lastStableBuild;
        Build lasSuccessfulBuild;
        Build lastUnstableBuild;
        Build lastUnsuccessfulBuild;
        int nextBuildNumber;
        List<Project> downstreamProjects;
        List<Project> upstreamProjects;
        List<HealthReport> healthReport;
        //scm
        //property
        //queueItem
    }
    
    static class Build {
        int number;
        String url;
        String description;
        boolean building;
        long duration;
        String fullDisplayName;
        String id;
        boolean keepLog;
        String result;
        long timestamp;
        String buildOn;
        List<User> culprits;
        List<Action> actions;
        //changeset
        //artifacts
    }
    
    static class View {
        String name;
        String url;
        String description;
        List<Job> jobs;
    }
    
    static class HealthReport {
        String description;
        String iconUrl;
        int score;
    }
    
    static class Computer {
        String displayName;
        String icon;
        boolean idle;
        boolean jnlpAgent;
        boolean launchSupported;
        boolean manualLaunchAllowed;
        List<Action> actions;
        int numExecutors;
        boolean offline;
        boolean temporarilyOffline;
        //oneOffExecutors
        //offlineCause
        //loadStatistics
        //executors
        //monitorData
    }
    
    
    static class Label { }
    static class Load { }
    static class Action { }
}
