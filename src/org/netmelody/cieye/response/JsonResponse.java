package org.netmelody.cieye.response;

public final class JsonResponse {

    private final Object content;
    private final long timeToLive;
    
    public JsonResponse(Object content) {
        this(content, 10000L);
    }

    public JsonResponse(Object content, long millisecondsUntilExpiry) {
        this.content = content;
        this.timeToLive = millisecondsUntilExpiry;
    }
    
    public Object jsonContent() {
        return content;
    }

    public long millisecondsUntilExpiry() {
        return this.timeToLive;
    }
}
