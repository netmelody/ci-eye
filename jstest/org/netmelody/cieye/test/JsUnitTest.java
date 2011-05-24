package org.netmelody.cieye.test;

import org.junit.runner.RunWith;
import org.netmelody.menodora.JasmineJavascriptContext;
import org.netmelody.menodora.JasmineSuite;

@RunWith(JasmineSuite.class)
@JasmineJavascriptContext(source={"jquery-*.min.js", "jquery.announcer.js", "jquery.popupmenu.js", "cieye.js"},
                          jasmineHelpers={"SpecHelper.js"},
                          jasmineSpecs={"*Spec.js"},
                          withSimulatedDom=true)
public final class JsUnitTest {
    
}
