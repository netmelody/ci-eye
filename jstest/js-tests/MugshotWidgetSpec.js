"use strict";
describe("MugshotWidget", function() {
    var maxSize,
        widget;

    beforeEach(function() {
        maxSize = 100;
        widget = ORG.NETMELODY.CIEYE.newMugshotWidget({"picture": "../welcome.jpg", "name": "Bob"},
                                                      function() {return maxSize;});
    });

    it("displays an image with the correct title", function() {
        var imageElement = widget.getContent();
        
        expect(imageElement.attr("title")).toEqual("Bob");
    });
    
    it("displays an image with the correct src", function() {
        var imageElement = widget.getContent();
        
        expect(imageElement.attr("src")).toEqual("../welcome.jpg");
    });
});