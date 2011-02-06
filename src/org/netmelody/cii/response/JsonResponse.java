package org.netmelody.cii.response;

public final class JsonResponse {

    private final Object content;
    
    public JsonResponse(Object content) {
        this.content = content;
    }

    public Object jsonContent() {
        return content;
    }
}
