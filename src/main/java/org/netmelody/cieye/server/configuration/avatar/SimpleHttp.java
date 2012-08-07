package org.netmelody.cieye.server.configuration.avatar;

import java.text.MessageFormat;

final class SimpleHttp implements PictureUrlProvider {

    @Override
    public String imageUrlFor(String picture) {
        return MessageFormat.format("http:{0}", picture);
    }

    @Override
    public String handlesPrefix() {
        return "http";
    }

}
