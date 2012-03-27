package org.netmelody.cieye.server.observation.test;

import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;

public final class StubCommunicationNetwork implements CommunicationNetwork {

    private final StubGrapeVine channel = new StubGrapeVine();
    
    @Override
    public Contact makeContact(CodeBook codeBook) {
        return new JsonRestRequester(codeBook.decoder(), codeBook.contentMunger(), channel);
    }
    
    public StubCommunicationNetwork respondingWith(String url, String response) {
        channel.respondingWith(url, response);
        return this;
    }
}