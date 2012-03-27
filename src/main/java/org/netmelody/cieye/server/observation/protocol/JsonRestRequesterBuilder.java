package org.netmelody.cieye.server.observation.protocol;

import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;

public final class JsonRestRequesterBuilder implements CommunicationNetwork {

    @Override
    public Contact makeContact(CodeBook codeBook) {
        return new JsonRestRequester(codeBook.decoder(), codeBook.contentMunger(),
                                     new RestRequester(codeBook.username(), codeBook.password()));
    }
}