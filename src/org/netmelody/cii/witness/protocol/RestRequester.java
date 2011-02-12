package org.netmelody.cii.witness.protocol;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class RestRequester {

    private final HttpClient client = new DefaultHttpClient();

    public String makeRequest(String url) {
        try {
            final HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");

            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String responseBody = client.execute(httpget, responseHandler);

            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void shutdown() {
        try {
            client.getConnectionManager().shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

