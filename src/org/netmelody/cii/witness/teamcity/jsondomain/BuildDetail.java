package org.netmelody.cii.witness.teamcity.jsondomain;

import org.netmelody.cii.domain.Status;

public final class BuildDetail {
    public long id;
    public String number;
    public String status;
    public String href;
    public String webUrl;
    public boolean personal;
    public boolean history;
    public boolean pinned;
    public String statusText;
    //buildType
    //startDate
    //finishDate
    //agent
    //tags
    //properties
    //revisions
    public ChangesHref changes;
    //relatedIssues
    
    public Status status() {
        if (status == null || "SUCCESS".equals(status)) {
            return Status.GREEN;
        }
        return Status.BROKEN;
    }
}