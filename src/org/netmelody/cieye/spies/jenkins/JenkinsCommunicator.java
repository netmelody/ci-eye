package org.netmelody.cieye.spies.jenkins;

import java.text.SimpleDateFormat;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;

public final class JenkinsCommunicator {

    private final Contact contact;
    private final String endpoint;
    private final String username;
    private final String password;

    public JenkinsCommunicator(String endpoint, CommunicationNetwork network, String username, String password) {
        this.endpoint = endpoint;
        this.contact = network.makeContact(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        this.username = username;
        this.password = password;
    }
    
    public <T> T makeJenkinsRestCallWithSuffix(String urlSuffix, Class<T> type) {
        return makeJenkinsRestCall(endpoint + ((urlSuffix.length() == 0) ? "" : ("/" + urlSuffix)), type);
    }
    
    public <T> T makeJenkinsRestCall(String url, Class<T> type) {
        final String reqUrl = url + (url.endsWith("/") ? "" : "/") + "api/json";
        return contact.makeJsonRestCall(reqUrl, type);
    }
    
    public String endpoint() {
        return endpoint;
    }

    public void doJenkinsPost(String url) {
        contact.performBasicAuthentication(username, password);
        contact.doPost(url);
    }
}
