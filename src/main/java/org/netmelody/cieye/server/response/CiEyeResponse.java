package org.netmelody.cieye.server.response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.simpleframework.http.Status;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class CiEyeResponse {

    public final long date = System.currentTimeMillis();
    public final String contentType;
    public final long expires;
    public final long lastModified;
    public final Status status;
    public final ImmutableMap<String, String> additionalStringHeaders;

    private final byte[] content;

    private CiEyeResponse(String contentType, byte[] content, long expiryDate, long lastModified) {
        this(contentType, content, expiryDate, lastModified, Status.OK);
    }

    private CiEyeResponse(String contentType, byte[] content, long expiryDate, long lastModified, Status status) {
        this(contentType, content, expiryDate, lastModified, status, Maps.<String, String>newHashMap());
    }

    private CiEyeResponse(String contentType, byte[] content, long expiryDate, long lastModified, Status status, Map<String, String> additionalStringHeaders) {
        this.contentType = contentType;
        this.content = content;
        this.expires = expiryDate;
        this.lastModified = lastModified;
        this.status = status;
        this.additionalStringHeaders = ImmutableMap.copyOf(additionalStringHeaders);
    }

    public static CiEyeResponse withJson(String json) {
        return new CiEyeResponse("application/json; charset=utf-8", json.getBytes(Charset.forName("UTF-8")), System.currentTimeMillis(), 0L);
    }

    public static CiEyeResponse withHtml(String html) {
        return new CiEyeResponse("text/html; charset=utf-8", html.getBytes(Charset.forName("UTF-8")), System.currentTimeMillis(), 0L);
    }

    public static CiEyeResponse movedPermanentlyTo(String newLocation) {
        return withHtml("").withStatus(Status.MOVED_PERMANENTLY).withHeader("Location", newLocation);
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

    public CiEyeResponse withStatus(Status status) {
        return new CiEyeResponse(this.contentType, this.content, this.expires, this.lastModified, status);
    }

    private CiEyeResponse withHeader(String header, String value) {
        final Map<String, String> newHeaders = Maps.newHashMap(this.additionalStringHeaders);
        newHeaders.put(header, value);
        return new CiEyeResponse(this.contentType, this.content, this.expires, this.lastModified, this.status, newHeaders);
    }

    public int contentLength() {
        return content.length;
    }

    public InputStream inputStream() {
        return new ByteArrayInputStream(content);
    }
}
