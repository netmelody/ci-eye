"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newMugshotListWidget = function() {
    var mugshotList = $("<ul></ul>");
    
    function displayListItem(mugshotJson) {
        var item = $("<li></li>"),
            anchor = $("<a></a>");
        
        anchor.attr("href", "/landscapes/" + mugshotJson.name + "/");
        anchor.text(mugshotJson.name);
        
        item.append(anchor);
        mugshotList.append(item);
    }
    
    function displayList(mugshotListItemsJson) {
        mugshotList.empty();
        $.each(mugshotListItemsJson, function(index, mugshotJson) {
            displayListItem(mugshotJson);
        });
    }
    
    function refresh() {
        $.getJSON("mugshotlist.json", function(mugshotListJson) {
            displayList(mugshotListJson.mugshots);
        });
    }
    
    refresh();
    return {
        "getContent": function() { return mugshotList; }
    };
};

$(document).ready(function() {
    var mugshotListWidget = ORG.NETMELODY.CIEYE.newMugshotListWidget();
    
    $("#mugshotlist").append(mugshotListWidget.getContent());
});