package org.netmelody.cii.response;

import org.netmelody.cii.witness.jenkins.JenkinsWitness;

public final class TargetListResponder extends BaseJsonResponder {

    @Override
    protected Object jsonResponseObject() {
        JenkinsWitness witness = new JenkinsWitness("http://ccmain:8080");
        return witness.targetList();
    }

}
