package org.netmelody.cii.witness.jenkins;

import org.netmelody.cii.witness.protocol.RestRequester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JenkinsCommunicator {

    private final Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    private final RestRequester restRequester = new RestRequester();
    private final String endpoint;

    public JenkinsCommunicator(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public <T> T makeJenkinsRestCallWithSuffix(String urlSuffix, Class<T> type) {
        return makeJenkinsRestCall(endpoint + ((urlSuffix.length() == 0) ? "" : ("/" + urlSuffix)), type);
    }
    
    public <T> T makeJenkinsRestCall(String url, Class<T> type) {
        return json.fromJson(restRequester.makeRequest(url + "/api/json"), type);
    }
    
    public String endpoint() {
        return endpoint;
    }
}
