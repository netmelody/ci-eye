package org.netmelody.cii.witness.jenkins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cii.witness.protocol.RestRequester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JenkinsCommunicator {

    private static final Log LOG = LogFactory.getLog(JenkinsCommunicator.class);
    
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
        final String reqUrl = url + (url.endsWith("/") ? "" : "/") + "api/json";
        
        T result = null;
        String content = "";
        try {
            content = restRequester.makeRequest(reqUrl);
            result = json.fromJson(content, type);
        }
        catch (Exception e) {
            LOG.error(String.format("Failed to parse json from (%s) of:\n %s", reqUrl, content));
        }
        
        
        if (null == result) {
            LOG.warn("null result for json request: " + reqUrl);
            try {
                result = type.newInstance();
            }
            catch (Exception e) {
                LOG.error("Failed to instantiate " + type.getName());
            }
        }
        
        return result;
    }
    
    public String endpoint() {
        return endpoint;
    }
}
