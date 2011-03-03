package org.netmelody.cii.witness.protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class RestRequester {

    private static final Log LOG = LogFactory.getLog(RestRequester.class);
    
    private final HttpClient client = new DefaultHttpClient();

    public String makeRequest(String url) {
        LOG.info(url);
        try {
            final HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");

            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            
            final String responseBody;
            synchronized(client) {
                responseBody = client.execute(httpget, responseHandler);
            }

            return responseBody;
        }
        catch (HttpResponseException e) {
            if (e.getStatusCode() == 404) {
                LOG.info(url + " - 404 Not Found", e);
                return "";
            }
            LOG.error(url, e);
        }
        catch (Exception e) {
            LOG.error(url, e);
        }
        return "";
    }

    public void shutdown() {
        try {
            client.getConnectionManager().shutdown();
        }
        catch (Exception e) {
            LOG.error("error shutting down", e);
        }
    }
}
