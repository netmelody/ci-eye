package org.netmelody.cieye.server.configuration;

import org.apache.commons.codec.digest.DigestUtils;

public final class Gravatar {

    private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";

    public String getUrl(String email) {
        return GRAVATAR_URL + DigestUtils.md5Hex(email.toLowerCase().trim()) + ".jpg";
    }

}
