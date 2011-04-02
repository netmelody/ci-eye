package org.netmelody.cieye.witness.protocol;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.observation.Contact;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.gson.Gson;

public final class JsonRestRequester implements Contact {

    private static final Log LOG = LogFactory.getLog(JsonRestRequester.class);
    
    private final Gson json;
    private final RestRequester restRequester = new RestRequester();
    private final Function<String, String> contentMunger;

    public JsonRestRequester(Gson jsonTranslator) {
        this(jsonTranslator, Functions.<String>identity());
    }
    
    public JsonRestRequester(Gson jsonTranslator, Function<String, String> contentMunger) {
        this.json = jsonTranslator;
        this.contentMunger = contentMunger;
    }
    
    /* (non-Javadoc)
     * @see org.netmelody.cieye.witness.protocol.Contact#makeJsonRestCall(java.lang.String, java.lang.Class)
     */
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

    /* (non-Javadoc)
     * @see org.netmelody.cieye.witness.protocol.Contact#performBasicLogin(java.lang.String)
     */
    @Override
    public void performBasicLogin(String loginUrl) {
        restRequester.makeRequest(loginUrl);
    }

    /* (non-Javadoc)
     * @see org.netmelody.cieye.witness.protocol.Contact#performBasicAuthentication(java.lang.String, java.lang.String)
     */
    @Override
    public void performBasicAuthentication(String username, String password) {
        restRequester.performBasicAuthentication(username, password);
    }
    
    /* (non-Javadoc)
     * @see org.netmelody.cieye.witness.protocol.Contact#doPost(java.lang.String)
     */
    @Override
    public void doPost(String url) {
        restRequester.doPost(url);
    }

    /* (non-Javadoc)
     * @see org.netmelody.cieye.witness.protocol.Contact#doPut(java.lang.String, java.lang.String)
     */
    @Override
    public void doPut(String url, String content) {
        restRequester.doPut(url, content);
    }
}
