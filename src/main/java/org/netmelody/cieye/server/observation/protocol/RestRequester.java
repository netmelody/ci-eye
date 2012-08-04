package org.netmelody.cieye.server.observation.protocol;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;

public final class RestRequester implements GrapeVine {

    private static final Logbook LOG = LogKeeper.logbookFor(RestRequester.class);
    
    private final DefaultHttpClient client;
    private final boolean privileged;

    public RestRequester(String username, String password) {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        
        final HttpParams params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        
        privileged = !username.isEmpty();
        
        client = new DefaultHttpClient(connectionManager, params);
        client.getCredentialsProvider().setCredentials(new AuthScope(null, -1), new UsernamePasswordCredentials(username, password));
    }

    @Override
    public boolean privileged() {
        return privileged;
    }

    @Override
    public String doGet(String url) {
        LOG.info(url);
        try {
            final HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");

            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(httpget, responseHandler);
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

    @Override
    public void doPost(String url) {
        LOG.info(url);
        try {
            final BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, new SingleAuthCache(new BasicScheme()));
            
            client.execute(new HttpPost(url), new ConsumingResponseHandler(), localcontext);
        }
        catch (Exception e) {
            LOG.error(url, e);
        }
    }
    
    @Override
    public void doPut(String url, String content) {
        LOG.info(url);
        try {
            final BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, new SingleAuthCache(new BasicScheme()));
            
            final HttpPut put = new HttpPut(url);
            put.setEntity(new StringEntity(content));
            
            client.execute(put, new ConsumingResponseHandler(), localcontext);
        }
        catch (Exception e) {
            LOG.error(url, e);
        }
    }
    
    @Override
    public void shutdown() {
        try {
            client.getConnectionManager().shutdown();
        }
        catch (Exception e) {
            LOG.error("error shutting down", e);
        }
    }
    
    public static final class SingleAuthCache implements AuthCache {
        private AuthScheme authScheme;
        public SingleAuthCache(AuthScheme authScheme) { this.authScheme = authScheme; }
        @Override public void put(HttpHost host, AuthScheme authScheme) { this.authScheme = authScheme; } 
        @Override public AuthScheme get(HttpHost host) { return this.authScheme; } 
        @Override public void remove(HttpHost host) { return; } 
        @Override public void clear() { return; }
    }
    
    public static final class ConsumingResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) {
            final HttpEntity entity = response.getEntity();
            final int statusCode = response.getStatusLine().getStatusCode();
            try {
                if (statusCode >= 300) {
                    LOG.error("Failed to PUT/POST\n" + EntityUtils.toString(entity));
                }
                EntityUtils.consume(entity);
            } catch (IOException e) {
                LOG.error("Failed to consume response entity");
            }
            return "";
        }
    }
}
