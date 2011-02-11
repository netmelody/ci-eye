package org.netmelody.cii.witness.protocol;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class RestRequest {

    public static void main(String[] args) throws ClientProtocolException, IOException {
        makeRequest("http://www.apache.org/");
    }

    public static String makeRequest(String url) {
        final HttpClient client = new DefaultHttpClient();
        try {
            final HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");
            
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String responseBody = client.execute(httpget, responseHandler);
            
            return responseBody;
        } catch (Exception e) {
            return "";
        } finally {
            try {
               client.getConnectionManager().shutdown();
            }
            catch (Exception e) {
                return "";
            }
        }
    }
}

