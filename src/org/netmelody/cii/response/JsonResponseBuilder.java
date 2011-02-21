package org.netmelody.cii.response;

import org.simpleframework.http.Path;

public interface JsonResponseBuilder {
    JsonResponse buildResponse(Path path, String requestContent);
}
