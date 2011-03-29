package org.netmelody.cieye.witness.jenkins.jsondomain;

public final class Cause {
    public String shortDescription;
    public long upstreamBuild;
    public String upstreamProject;
    public String upstreamUrl;
    
    public String upstreamBuildUrl() {
        return (null == upstreamUrl) ? "" : upstreamUrl + upstreamBuild;
    }
}