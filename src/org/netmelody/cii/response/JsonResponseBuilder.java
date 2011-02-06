package org.netmelody.cii.response;

import org.simpleframework.http.Query;


public interface JsonResponseBuilder {
    
    JsonResponse buildResponse(Query query, String requestContent);
}
