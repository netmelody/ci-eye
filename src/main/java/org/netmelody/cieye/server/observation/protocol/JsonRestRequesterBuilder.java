package org.netmelody.cieye.server.observation.protocol;

import java.util.Map.Entry;

import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

public final class JsonRestRequesterBuilder implements CommunicationNetwork {

    @Override
    public Contact makeContact(CodeBook codeBook) {
        final GsonBuilder builder = new GsonBuilder().setDateFormat(codeBook.dateFormat().toPattern());
        
        for (Entry<Class<?>, JsonDeserializer<?>> entry : codeBook.deserialisers().entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        
        return new JsonRestRequester(builder.create(), codeBook.contentMunger(), codeBook.username(), codeBook.password());
    }
}