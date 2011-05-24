package org.netmelody.cieye.test;

import org.junit.runner.RunWith;
import org.netmelody.menodora.JasmineJavascriptContext;
import org.netmelody.menodora.JasmineSuite;

@RunWith(JasmineSuite.class)
@JasmineJavascriptContext(source={"/Player.js", "/Song.js"},
                          jasmineHelpers={"/SpecHelper.js"},
                          jasmineSpecs={"/PlayerSpec.js"},
                          withSimulatedDom=true)
public final class JsUnitTest {
    
}
