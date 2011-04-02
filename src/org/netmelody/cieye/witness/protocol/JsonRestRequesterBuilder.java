package org.netmelody.cieye.witness.protocol;

import java.text.SimpleDateFormat;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;

import com.google.gson.GsonBuilder;

public final class JsonRestRequesterBuilder implements CommunicationNetwork {

    @Override
    public Contact makeContact(SimpleDateFormat dateFormat) {
        return new JsonRestRequester(new GsonBuilder().setDateFormat(dateFormat.toPattern()).create());
    }
}
