package org.netmelody.cieye.server.response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class CiEyeResponse {

    public final long date = System.currentTimeMillis();
    public final String contentType;
    public final long expires;
    public final long lastModified;
    
    private final byte[] content;

    private CiEyeResponse(String contentType, byte[] content, long expiryDate, long lastModified) {
        this.contentType = contentType;
        this.content = content;
        this.expires = expiryDate;
        this.lastModified = lastModified;
    }

    public static CiEyeResponse withJson(String json) {
        return new CiEyeResponse("application/json; charset=utf-8", json.getBytes(Charset.forName("UTF-8")), System.currentTimeMillis(), 0L);
    }

    public static CiEyeResponse forResource(byte[] contentBytes, String contentType) {
        final long time = System.currentTimeMillis();
        return new CiEyeResponse(contentType, contentBytes, time, time);
    }

    public CiEyeResponse expiringInMillis(long timeToLiveMillis) {
        return new CiEyeResponse(this.contentType, this.content, System.currentTimeMillis() + timeToLiveMillis, this.lastModified);
    }

    public CiEyeResponse lastModified(long lastModified) {
        return new CiEyeResponse(this.contentType, this.content, this.expires, lastModified);
    }

    public int contentLength() {
        return content.length;
    }

    public InputStream inputStream() {
        return new ByteArrayInputStream(content);
    }
}
