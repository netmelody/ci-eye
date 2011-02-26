"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newLandscapeListWidget = function(landscapeListDiv) {
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
    
    refresh();
    return {
        "getContent": function() { return landscapeList; }
    };
};

ORG.NETMELODY.CIEYE.newSettingsLocationWidget = function(landscapeListDiv) {
    var settingsLocationSpan = $('<span></span>');
    
    function displayLocation(location) {
        settingsLocationSpan.text(location);
    }
    
    function refresh() {
        $.getJSON('settingslocation.json', function(locationJson) {
            displayLocation(locationJson);
        });
    }
    
    refresh();
    return {
        "getContent": function() { return settingsLocationSpan; }
    };
};

ORG.NETMELODY.CIEYE.newPopup = function(trigger, content) {
    var fadeIn, fadeOut;

    fadeIn = function() {
        content.fadeIn();
        trigger.unbind('click');
        trigger.click(fadeOut);
    }
    
    fadeOut = function fadeOut() {
        content.fadeOut();
        trigger.unbind('click');
        trigger.click(fadeIn);
    }

    trigger.click(fadeIn);
    content.click(fadeOut);
    return {
    };
};

$(document).ready(function() {
    var landscapeListWidget = ORG.NETMELODY.CIEYE.newLandscapeListWidget(),
        settingsLocationWidget = ORG.NETMELODY.CIEYE.newSettingsLocationWidget();
    
    $('#landscapelist').append(landscapeListWidget.getContent());
    $('#settingsDir').append(settingsLocationWidget.getContent());
    ORG.NETMELODY.CIEYE.newPopup($('#help'), $('#helptext'));
});