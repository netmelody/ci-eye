package org.netmelody.cii.response;

import org.netmelody.cii.witness.Witness;

public final class TargetListResponder extends BaseJsonResponder {

    private final Witness witness;

    public TargetListResponder(Witness witness) {
        this.witness = witness;
    }
    
    @Override
    protected Object jsonResponseObject() {
        return witness.targetList();    
    }

}
