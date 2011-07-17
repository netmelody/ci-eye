package org.netmelody.cieye.core.domain;

public abstract class Target {

    private final String id;
    private final String webUrl;
    private final String name;
    private final Status status;

    public Target(String id, String webUrl, String name, Status status) {
        this.id = id;
        this.webUrl = webUrl;
        this.name = name;
        this.status = status;
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

    public String webUrl() {
        return webUrl;
    }
}