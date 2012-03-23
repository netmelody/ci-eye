package org.netmelody.cieye.spies.jenkins;

import java.util.Collection;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.spies.jenkins.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.Server;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.spies.jenkins.jsondomain.ViewDetail;

public final class JenkinsCommunicator {

    private final Contact contact;
    private final String endpoint;

    public JenkinsCommunicator(String endpoint, Contact contact) {
        this.endpoint = endpoint;
        this.contact = contact;
    }
    
    public String endpoint() {
        return endpoint;
    }

    public boolean canSpeakFor(Feature feature) {
        return endpoint.equals(feature.endpoint());
    }
    
    public void doJenkinsPost(String url) {
        contact.doPost(url);
    }

    public BuildDetail buildDetailsFor(String buildUrl) {
        return makeJenkinsRestCall(buildUrl, BuildDetail.class);
    }

    public JobDetail jobDetailFor(String jobEndpoint) {
        return makeJenkinsRestCall(jobEndpoint, JobDetail.class);
    }

    public String lastBadBuildFor(String jobEndpoint) {
        return jobDetailFor(jobEndpoint).lastBadBuildUrl();
    }

    public Collection<Job> jobsFor(View viewDigest) {
        return makeJenkinsRestCall(viewDigest.url, ViewDetail.class).jobs();
    }

    public Collection<View> views() {
        return makeJenkinsRestCallWithSuffix("", Server.class).views();
    }
    
    private <T> T makeJenkinsRestCallWithSuffix(String urlSuffix, Class<T> type) {
        return makeJenkinsRestCall(endpoint + ((urlSuffix.length() == 0) ? "" : ("/" + urlSuffix)), type);
    }
    
    private <T> T makeJenkinsRestCall(String url, Class<T> type) {
        final String reqUrl = url + (url.endsWith("/") ? "" : "/") + "api/json";
        return contact.makeJsonRestCall(reqUrl, type);
    }
}
