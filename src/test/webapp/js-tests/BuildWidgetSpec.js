"use strict";
describe("BuildWidget", function() {
    var widget = undefined;

    beforeEach(function() {
        widget = ORG.NETMELODY.CIEYE.newBuildWidget({"status": "GREEN", "progress": 85});
    });

    it("displays the build status", function() {
        var progressBar = widget.getContent(),
            progressBarFilling = progressBar.children().first();
        
        expect(progressBarFilling.attr("class")).toEqual("GREEN");
    });
    
    it("displays the build progress", function() {
        var progressBar = widget.getContent(),
            progressBarFilling = progressBar.children().first();
    
        expect(progressBarFilling.attr("style")).toEqual("width: 85%;");
    });

    describe("when the status has been updated", function() {
        beforeEach(function() {
            widget.updateFrom({"status": "BROKEN", "progress": 11});
        });

        it("displays the new build status", function() {
            var progressBar = widget.getContent(),
                progressBarFilling = progressBar.children().first();
        
            expect(progressBarFilling.attr("class")).toEqual("BROKEN");
        });

        it("displays the new build progress", function() {
            var progressBar = widget.getContent(),
                progressBarFilling = progressBar.children().first();
        
            expect(progressBarFilling.attr("style")).toEqual("width: 11%;");
        });
    });
});