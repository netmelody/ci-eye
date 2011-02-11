package org.netmelody.cii.domain;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public final class Target {

    private final String id;
    private final String name;
    private final Status status;
    private final List<Sponsor> guilty = new ArrayList<Sponsor>();
    private final List<Build> builds = new ArrayList<Build>();
    
    public Target(String name) {
        this(name, Status.GREEN);
    }

    public Target(String name, Status status, Build... builds) {
        this(name, name, status, builds);
    }
    
    public Target(String id, String name, Status status, Build... builds) {
        this(id, name, status, new ArrayList<Sponsor>(), builds);
    }
    
    public Target(String id, String name, Status status, List<Sponsor> guilty, Build... builds) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.guilty.addAll(guilty);
        
        if (null != builds) {
            this.builds.addAll(asList(builds));
        }
    }

    public String id() {
        return id;
    }
    
    public String name() {
        return name;
    }
    
    public Status status() {
        return status;
    }
    
    public List<Sponsor> guilty() {
        return new ArrayList<Sponsor>(guilty);
    }
}
