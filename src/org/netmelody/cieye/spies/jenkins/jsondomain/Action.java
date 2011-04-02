package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.List;


public final class Action {
    public List<Cause> causes;
    public int failCount;
    public int skipCount;
    public int totalCount;
    public String urlName;
    
    public List<String> upstreamBuildUrls() {
        final List<String> result = new ArrayList<String>();
        
        if (null != causes) {
            for (Cause cause : causes) {
                if (null != cause) {
                    String upstreamBuildUrl = cause.upstreamBuildUrl();
                    if (null != upstreamBuildUrl) {
                        result.add(upstreamBuildUrl);
                    }
                }
            }
        }
        return result;
    }
}