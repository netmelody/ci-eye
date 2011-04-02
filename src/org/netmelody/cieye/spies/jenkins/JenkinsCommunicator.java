package org.netmelody.cieye.spies.jenkins;


import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.witness.protocol.JsonRestRequester;

import com.google.gson.GsonBuilder;

public final class JenkinsCommunicator {

    private final Contact restRequester =
        new JsonRestRequester(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create());
    
    private final String endpoint;
    private final String username;
    private final String password;

    public JenkinsCommunicator(String endpoint, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }
    
    public <T> T makeJenkinsRestCallWithSuffix(String urlSuffix, Class<T> type) {
        return makeJenkinsRestCall(endpoint + ((urlSuffix.length() == 0) ? "" : ("/" + urlSuffix)), type);
    }
    
    public <T> T makeJenkinsRestCall(String url, Class<T> type) {
        final String reqUrl = url + (url.endsWith("/") ? "" : "/") + "api/json";
        return restRequester.makeJsonRestCall(reqUrl, type);
    }
    
    public String endpoint() {
        return endpoint;
    }

    public void doJenkinsPost(String url) {
        restRequester.performBasicAuthentication(username, password);
        restRequester.doPost(url);
    }
}
