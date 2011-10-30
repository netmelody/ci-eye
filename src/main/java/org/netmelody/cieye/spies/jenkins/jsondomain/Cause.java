package org.netmelody.cieye.spies.jenkins.jsondomain;

public final class Cause {
    public String shortDescription;
    public long upstreamBuild;
    public String upstreamProject;
    public String upstreamUrl;
    
    public String upstreamBuildUrl() {
        return (null == upstreamUrl) ? "" : upstreamUrl + upstreamBuild;
    }
}