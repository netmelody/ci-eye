"use strict";
describe("VersionChecker", function() {
    var widget = undefined,
        scheduler = undefined,
        reloaded = undefined,
        responseJson = {};

    beforeEach(function() {
        reloaded = false;
        $.getJSON = function(a, f) { f(responseJson); };
        scheduler = { "reload": function() { reloaded = true; },
                      "repeat": function() { } };
        
        widget = ORG.NETMELODY.CIEYE.newVersionChecker(scheduler);
    });

    it("does not reload the page after the first version check", function() {
        responseJson = { "currentServerVersion": "1.0.1" };
        widget.start();
        expect(reloaded).toEqual(false);
    });

    it("reloads the page if a new version is available", function() {
        responseJson = { "currentServerVersion": "1.0.1" };
        widget.start();
        
        responseJson = { "currentServerVersion": "1.0.2" };
        widget.start();
        expect(reloaded).toEqual(true);
    });
    
    it("does not reload the page when a new version is not available", function() {
        responseJson = { "currentServerVersion": "1.0.1" };
        widget.start();
        
        responseJson = { "currentServerVersion": "1.0.1" };
        widget.start();
        expect(reloaded).toEqual(false);
    });
});