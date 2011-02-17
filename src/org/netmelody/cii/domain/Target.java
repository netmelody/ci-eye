package org.netmelody.cii.domain;

import static com.google.common.collect.Iterators.find;
import static com.google.common.collect.Iterators.transform;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class Target {

    private final String id;
    private final String name;
    private final Status status;
    private final List<Sponsor> sponsors = new ArrayList<Sponsor>();
    private final List<Build> builds = new ArrayList<Build>();
    
    public Target(String id, String name, Status status, Build... builds) {
        this(id, name, status, new ArrayList<Sponsor>(), builds);
    }
    
    public Target(String id, String name, Status status, List<Sponsor> sponsors, Build... builds) {
        this(id, name, status, sponsors, (null == builds) ? new ArrayList<Build>() : asList(builds));
    }
    
    public Target(String id, String name, Status status, List<Sponsor> sponsors, List<Build> builds) {
        this.id = id;
        this.name = name;
        this.sponsors.addAll(new HashSet<Sponsor>(sponsors));
        this.builds.addAll(builds);
        
        this.status = find(transform(builds.iterator(), new Function<Build, Status>() {
                                                            @Override public Status apply(Build build) {
                                                                return build.status();
                                                            }
                                                        }),
                           new Predicate<Status>() {
                               @Override public boolean apply(Status status) {
                                   return Status.BROKEN.equals(status);
                               }
                           },
                           status);
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
    
    public List<Sponsor> sponsors() {
        return new ArrayList<Sponsor>(sponsors);
    }
}
