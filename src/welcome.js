"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newLandscapeList = function(landscapeListDiv) {
    var landscapeList = $('<ul></ul>');
    
    function displayListItem(landscapeListItemJson) {
        var item = $('<li></li>'),
            anchor = $('<a></a>');
        
        anchor.attr('href', '/landscapes/' + landscapeListItemJson.name + '/');
        anchor.text(landscapeListItemJson.name);
        
        item.append(anchor);
        landscapeList.append(item);
    }
    
    function displayList(landscapeListItemsJson) {
        landscapeList.empty();
        $.each(landscapeListItemsJson, function(index, landscapeListItemJson) {
            displayListItem(landscapeListItemJson);
        });
    }
    
    function refresh() {
        $.getJSON('landscapelist.json', function(landscapeListJson) {
            displayList(landscapeListJson.landscapes);
        });
    }
    

    landscapeListDiv.append(landscapeList);
    refresh();
    
    return {
    };
};

$(document).ready(function() {
    ORG.NETMELODY.CIEYE.newLandscapeList($('#landscapelist'));
});
