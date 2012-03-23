package org.netmelody.cieye.server.observation.protocol;

import java.text.SimpleDateFormat;

import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;

import com.google.gson.GsonBuilder;

public final class JsonRestRequesterBuilder implements CommunicationNetwork {

    @Override
    public Contact makeContact(CodeBook codeBook) {
        return makeContact(codeBook.dateFormat());
    }
    
    @Override
    public Contact makeContact(SimpleDateFormat dateFormat) {
        return makeContact(dateFormat, null, null);
    }
    
    @Override
    public Contact makeContact(SimpleDateFormat dateFormat, Class<?> type, Object typeAdapter) {
        final GsonBuilder builder = new GsonBuilder().setDateFormat(dateFormat.toPattern());
        
        if (null != typeAdapter) {
            builder.registerTypeAdapter(type, typeAdapter);
        }
        
        return new JsonRestRequester(builder.create());
    }
}
