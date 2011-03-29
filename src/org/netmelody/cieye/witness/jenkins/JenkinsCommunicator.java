package org.netmelody.cieye.witness.jenkins;

import org.netmelody.cieye.witness.protocol.JsonRestRequester;

import com.google.gson.GsonBuilder;

public final class JenkinsCommunicator {

    private final JsonRestRequester restRequester =
        new JsonRestRequester(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create());
    
    private final String endpoint;

    public JenkinsCommunicator(String endpoint) {
        this.endpoint = endpoint;
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
}
