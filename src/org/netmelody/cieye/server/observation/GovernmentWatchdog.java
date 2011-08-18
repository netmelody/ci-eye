package org.netmelody.cieye.server.observation;

import java.text.SimpleDateFormat;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;

public final class GovernmentWatchdog implements CiEyeNewVersionChecker {

    private final Contact contact;

    public static final class Tags {
        
    }
    
    public GovernmentWatchdog(CommunicationNetwork network) {
        contact = network.makeContact(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }

    @Override
    public String getLatestVersion() {
        contact.makeJsonRestCall("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", Tags.class);
        throw new java.lang.UnsupportedOperationException();
    }
}
