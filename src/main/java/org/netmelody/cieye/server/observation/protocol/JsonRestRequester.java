package org.netmelody.cieye.server.observation.protocol;

import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.core.observation.Contact;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

public final class JsonRestRequester implements Contact {

    private static final Logbook LOG = LogKeeper.logbookFor(JsonRestRequester.class);
    
    private final Gson json;
    private final RestRequester restRequester;
    private final Function<String, String> contentMunger;

    public JsonRestRequester(Gson jsonTranslator) {
        this(jsonTranslator, new Function<String, String>() {
            @Override public String apply(String input) {  return input.replace("\"@", "\""); }
        });
    }
    
    private JsonRestRequester(Gson jsonTranslator, Function<String, String> contentMunger) {
        this.restRequester = new RestRequester();
        this.json = jsonTranslator;
        this.contentMunger = contentMunger;
    }
    
    @Override
    public <T> T makeJsonRestCall(String url, Class<T> type) {
        T result = null;
        String content = "";
        try {
            content = contentMunger.apply(restRequester.makeRequest(url));
            result = json.fromJson(content, type);
        }
        catch (Exception e) {
            LOG.error(String.format("Failed to parse json from (%s) of:\n %s", url, content), e);
        }
        
        if (null == result) {
            LOG.warn("null result for json request: " + url);
            try {
                result = type.newInstance();
            }
            catch (Exception e) {
                LOG.error("Failed to instantiate " + type.getName(), e);
            }
        }
        
        return result;
    }

    @Override
    public JsonElement makeJsonRestCall(String url) {
        String content = null;
        JsonElement result = null;
        try {
            content = contentMunger.apply(restRequester.makeRequest(url));
            result = new JsonParser().parse(content);
        }
        catch (Exception e) {
            LOG.error(String.format("Failed to parse json from (%s) of:\n %s", url, content), e);
        }
        return (result == null) ? JsonNull.INSTANCE : result; 
    }
    
    @Override
    public void performBasicLogin(String loginUrl) {
        restRequester.makeRequest(loginUrl);
    }

    @Override
    public void performBasicAuthentication(String username, String password) {
        restRequester.performBasicAuthentication(username, password);
    }
    
    @Override
    public void doPost(String url) {
        restRequester.doPost(url);
    }

    @Override
    public void doPut(String url, String content) {
        restRequester.doPut(url, content);
    }

    public void shutdown() {
        restRequester.shutdown();
    }
}
