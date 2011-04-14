package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.List;

public final class Users {
    public List<UserDetail> users;

    public List<UserDetail> users() {
        return (null == users) ? new ArrayList<UserDetail>() : users;
    }
}