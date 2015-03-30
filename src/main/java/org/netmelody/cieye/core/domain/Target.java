package org.netmelody.cieye.core.domain;

import com.google.common.base.Optional;

public abstract class Target {

    private final TargetId id;
    private final String webUrl;
    private final Optional<String> featureName;
    private final String name;
    private final Status status;

    public Target(String id, String webUrl, Optional<String> featureName, String name, Status status) {
        this(new TargetId(id), webUrl, featureName, name, status);
    }

    public Target(TargetId id, String webUrl, Optional<String> featureName, String name, Status status) {
        this.id = id;
        this.webUrl = webUrl;
        this.featureName = featureName;
        this.name = name;
        this.status = status;
    }

    public TargetId id() {
        return id;
    }

    public String webUrl() {
        return webUrl;
    }

    public Optional<String> featureName() {
        return featureName;
    }

    public String name() {
        return name;
    }

    public Status status() {
        return status;
    }
}
