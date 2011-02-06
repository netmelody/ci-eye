package org.netmelody.cii.response.json;

import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.witness.Witness;

public final class TargetListResponseBuilder implements JsonResponseBuilder {

    private final Witness witness;

    public TargetListResponseBuilder(Witness witness) {
        this.witness = witness;
    }
    
    @Override
    public JsonResponse buildResponse(String requestContent) {
        return new JsonResponse(witness.targetList());
    }
}
