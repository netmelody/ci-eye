package org.netmelody.cii.witness.jenkins;

import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.Date;
import java.util.List;

import org.netmelody.cii.domain.Percentage;
import org.netmelody.cii.domain.Sponsor;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.jenkins.jsondomain.Build;
import org.netmelody.cii.witness.jenkins.jsondomain.ChangeSetItem;
import org.netmelody.cii.witness.jenkins.jsondomain.Job;
import org.netmelody.cii.witness.jenkins.jsondomain.User;
import org.netmelody.cii.witness.protocol.RestRequester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JobAnalyser {
    
    private final Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    private final RestRequester restRequester;
    private final String endpoint;

    public JobAnalyser(RestRequester restRequester, String endpoint) {
        this.restRequester = restRequester;
        this.endpoint = endpoint;
    }
    
    public Percentage progressOf(Job job) {
        if (!job.building()) {
            return percentageOf(100);
        }
        
        final Build lastBuild = fetchBuildData(job.lastBuild.url);
        final Build lastSuccessfulBuild = fetchBuildData(job.lastSuccessfulBuild.url);
        
        return percentageOf(new Date().getTime() - lastBuild.timestamp,
                            lastSuccessfulBuild.duration);
    }
    
    public List<Sponsor> sponsorsOf(Job job) {
        return sponsorsOf(fetchBuildData(job.lastBuild.url));
    }
    
    private List<Sponsor> sponsorsOf(Build build) {
        final Detective detective = new Detective();
        
        final List<Sponsor> sponsors = detective.sponsorsOf(analyseChanges(build));
        
        if (!sponsors.isEmpty()) {
            return sponsors;
        }
        
        for (String buildUrl :  build.upstreamBuildUrls()) {
            final Build upstreamBuild = fetchBuildData(endpoint + "/" + buildUrl);
            if (null == upstreamBuild) {
                continue;
            }
            sponsors.addAll(sponsorsOf(upstreamBuild));
        }
        return sponsors;
    }

    private String analyseChanges(Build build) {
        if (null == build.changeSet || null == build.changeSet.items) {
            return "";
        }
        
        final StringBuilder result = new StringBuilder();
        for (ChangeSetItem changeSetItem : build.changeSet.items) {
            result.append(changeSetItem.user);
            result.append(changeSetItem.msg);
        }
        
        for (User user : build.culprits) {
            result.append(user.fullName);
        }
        
        return result.toString();
    }
    
    private Build fetchBuildData(String buildUrl) {
        return makeJenkinsRestCall(buildUrl, Build.class);
    }
    
    private <T> T makeJenkinsRestCall(String url, Class<T> type) {
        return json.fromJson(restRequester.makeRequest(url + "/api/json"), type);
    }
}