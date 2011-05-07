package org.netmelody.cieye.test;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;

public final class JsUnitTest {
    
    @Test public void
    executeJUnitTests() throws IOException {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1);
        cx.setLanguageVersion(Context.VERSION_1_5);
        Global global = Main.getGlobal();
        global.init(cx);
        
        eval(cx, global, "Packages.org.mozilla.javascript.Context.getCurrentContext().setOptimizationLevel(-1);");
        
        loadJavaScript(cx, global, "/env.js-1.2/env.rhino.1.2.js");
        eval(cx, global, "Envjs.scriptTypes['text/javascript'] = true;");
        
        loadJavaScript(cx, global, "/jasmine-1.0.2/jasmine.js");
        loadJavaScript(cx, global, "/jasmine-1.0.2/jasmine-html.js");
        loadJavaScript(cx, global, "/jasmine-reporters/jasmine.console_reporter.js");
        loadJavaScript(cx, global, "/jasmine-reporters/jasmine.junit_reporter.js");

        loadJavaScript(cx, global, "/Player.js");
        loadJavaScript(cx, global, "/Song.js");

        loadJavaScript(cx, global, "/SpecHelper.js");
        loadJavaScript(cx, global, "/PlayerSpec.js");

        //eval(cx, global, "jasmine.getEnv().addReporter(new jasmine.TrivialReporter());");
        eval(cx, global, "jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter('C:\\\\temp\\\\'));");
        //eval(cx, global, "jasmine.getEnv().addReporter(new jasmine.ConsoleReporter());");
        eval(cx, global, "jasmine.getEnv().execute();");
        
        System.out.println("");
    }

    private void loadJavaScript(Context context, Global scope, String resource) throws IOException {
        URL url = getClass().getResource(resource);
        String scriptSource = IOUtils.toString(url.openStream());
        String path = url.toExternalForm();
        Main.evaluateScript(Main.loadScriptFromSource(context, scriptSource, path, 1, null), context, scope);
    }
    
    private void eval(Context cx, Global global, String script) {
        Main.evaluateScript(Main.loadScriptFromSource(cx, script, "local.js", 1, null), cx, global);
    }
    
}
