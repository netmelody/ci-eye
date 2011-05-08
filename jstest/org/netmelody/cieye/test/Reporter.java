package org.netmelody.cieye.test;

import org.mozilla.javascript.NativeObject;

public class Reporter {

    public void reportRunnerResults(NativeObject runner) {
        System.out.println("Runner Finished.");
    }
    
    public void reportRunnerStarting(NativeObject runner) {
        System.out.println("Runner Started.");
    }
    
    public void reportSpecResults(NativeObject spec) {
        NativeObject results = (NativeObject)NativeObject.callMethod(spec, "results", new Object[0]);
        Boolean passed = (Boolean)NativeObject.callMethod(results, "passed", new Object[0]);
        System.out.println(passed ? "Passed." : "Failed.");
    }
    
    public void reportSpecStarting(NativeObject spec) {
        Object specDesc = NativeObject.getProperty(spec, "description");
        NativeObject suite = (NativeObject)NativeObject.getProperty(spec, "suite");
        Object suiteDesc = NativeObject.getProperty(suite, "description");
        System.out.println(suiteDesc + " : " + specDesc + " ... ");
    }
    
    public void reportSuiteResults(NativeObject suite) {
        Object suiteDesc = NativeObject.getProperty(suite, "description");
        NativeObject results = (NativeObject)NativeObject.callMethod(suite, "results", new Object[0]);
        Double passedCount = (Double)NativeObject.getProperty(results, "passedCount");
        Double totalCount = (Double)NativeObject.getProperty(results, "totalCount");
        System.out.println(suiteDesc + ": " + passedCount.intValue() + " of " + totalCount.intValue() + " passed.");
    }
}