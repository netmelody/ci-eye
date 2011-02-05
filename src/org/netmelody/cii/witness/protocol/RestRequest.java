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
        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = client.execute(httpget, responseHandler);
            
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
            
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

