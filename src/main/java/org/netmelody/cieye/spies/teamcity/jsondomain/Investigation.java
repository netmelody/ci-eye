package org.netmelody.cieye.spies.teamcity.jsondomain;

public final class Investigation {
    public String id;
    public String state;
    public User responsible;
    public Assignment assignment;
    // scope;
    
    public long startDateTime() {
        if (null == assignment) {
            return 0L;
        }
        return (null == assignment.timestamp) ? 0L : assignment.timestamp.getTime();
    }

    public boolean underInvestigation() {
        return "TAKEN".equals(state) || "FIXED".equals(state);
    }
}