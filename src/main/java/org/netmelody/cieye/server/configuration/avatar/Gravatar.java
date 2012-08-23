package org.netmelody.cieye.server.configuration.avatar;

import org.apache.commons.codec.digest.DigestUtils;

public final class Gravatar implements PictureUrlProvider {

    private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";

    @Override
    public String imageUrlFor(String email) {
        return GRAVATAR_URL + DigestUtils.md5Hex(email.toLowerCase().trim()) + ".jpg";
    }

    @Override
    public String handlesPrefix() {
        return "gravatar";
    }

}
