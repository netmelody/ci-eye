"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newLandscapeListWidget = function() {
    var landscapeList = $("<ul></ul>");
    
    function displayListItem(landscapeListItemJson) {
        var item = $("<li></li>"),
            anchor = $("<a></a>");
        
        anchor.attr("href", "/landscapes/" + landscapeListItemJson.name + "/");
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
        $.getJSON("landscapelist.json", function(landscapeListJson) {
            displayList(landscapeListJson.landscapes);
        });
    }
    
    refresh();
    return {
        "getContent": function() { return landscapeList; }
    };
};

ORG.NETMELODY.CIEYE.newSettingsLocationWidget = function() {
    var settingsLocationSpan = $("<span></span>");
    
    function displayLocation(location) {
        settingsLocationSpan.text(location);
    }
    
    function refresh() {
        $.getJSON("settingslocation.json", function(locationJson) {
            displayLocation(locationJson);
        });
    }
    
    refresh();
    return {
        "getContent": function() { return settingsLocationSpan; }
    };
};

ORG.NETMELODY.CIEYE.newVersionInformationWidget = function() {
    var versionSpan = $("<span></span>");
    
    function displayVersion(versionJson) {
        var versionText = "Version " + versionJson.currentServerVersion;
        if (versionJson.latestServerVersion !== "" && versionJson.currentServerVersion !== versionJson.latestServerVersion) {
            versionText = versionText + " -- NEW VERSION AVAILABLE (" + versionJson.latestServerVersion + ")";
        }
        versionSpan.text(versionText);
    }
    
    function refresh() {
        $.getJSON("version.json", function(versionJson) {
            displayVersion(versionJson);
        });
    }
    
    refresh();
    return {
        "getContent": function() { return versionSpan; }
    };
};

ORG.NETMELODY.CIEYE.newPopup = function(trigger, content) {
    var displayed = false;

    function fadeIn() {
        content.fadeIn();
        displayed = true;
    };
    
    function fadeOut() {
        content.fadeOut();
        displayed = false;
    };

    function toggle() {
        if (displayed) {
            fadeOut();
            return;
        }
        fadeIn();
    }
    
    trigger.click(toggle);
    content.click(fadeOut);
    return {
    };
};

$(document).ready(function() {
    var landscapeListWidget = ORG.NETMELODY.CIEYE.newLandscapeListWidget(),
        settingsLocationWidget = ORG.NETMELODY.CIEYE.newSettingsLocationWidget(),
        versionInformationWidget = ORG.NETMELODY.CIEYE.newVersionInformationWidget();
    
    $("#landscapelist").append(landscapeListWidget.getContent());
    $("#settingsDir").append(settingsLocationWidget.getContent());
    $("#versionInformation").append(versionInformationWidget.getContent());
    ORG.NETMELODY.CIEYE.newPopup($("#help"), $("#helptext"));
});