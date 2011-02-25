"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newBuildWidget = function(buildJson) {
    var buildDiv = $('<div></div>').addClass('progress-bar'),
        barDiv   = $('<div></div>');
    
    function updateProgress(percent) {
        barDiv.attr('style', 'width: ' + percent + '%');
    }
    
    function updateStatus(status) {
        barDiv.removeClass();
        barDiv.addClass(status);
    }
    
    function refresh(newBuildJson) {
        updateProgress(newBuildJson.progress);
        updateStatus(newBuildJson.status);
    }
    
    function initialise() {
        buildDiv.append(barDiv);
        refresh(buildJson);
    }
    
    initialise();
    
    return {
        "updateFrom": refresh,
        "getContent": function() { return buildDiv; }
    };
};

ORG.NETMELODY.CIEYE.newTargetWidget = function(targetJson) {
    var MAX_IMAGE_SIZE = 90,
        currentTargetJson = { builds:[] },
        targetDiv = $('<div></div>'),
        sponsorDiv = $('<div></div>').addClass('sponsors'),
        buildsDiv = $('<div></div>');
    
    function sortedSponsors(unsortedSponsors) {
        return unsortedSponsors.sort(function(a, b) {
            return (a.name === b.name) ? 0 : (a.name < b.name) ? -1 : 1;
        });
    }
    
    function resizeImage() {
        var image = $(this),
            width = image.width(),
            height = image.height();
        
        if (width > height) {
            image.height(height * MAX_IMAGE_SIZE / width);
            image.width(MAX_IMAGE_SIZE);
        }
        else {
            image.width(width * MAX_IMAGE_SIZE / height);
            image.height(MAX_IMAGE_SIZE);
        }
    }
    
    function refresh(newTargetJson) {
        var lastTargetJson = currentTargetJson;
        
        currentTargetJson = newTargetJson;
        if (lastTargetJson.status !== newTargetJson.status) {
            if (lastTargetJson.status) {
                targetDiv.removeClass(lastTargetJson.status);
            }
            targetDiv.addClass(newTargetJson.status);
        }
        
        buildsDiv.empty();
        $.each(newTargetJson.builds, function(index, buildJson) {
            buildsDiv.append(ORG.NETMELODY.CIEYE.newBuildWidget(buildJson).getContent());
        });
        
        sponsorDiv.empty();
        if (newTargetJson.builds.length === 0 && newTargetJson.status === "GREEN") {
            return;
        }
        $.each(sortedSponsors(newTargetJson.sponsors), function(index, sponsorJson) {
            var img = $('<img></img>').attr('src', sponsorJson.picture).load(resizeImage);
            sponsorDiv.append(img);
        });
    }
    
    function initialise() {
        var titleSpan = $('<span></span>');
        
        titleSpan.text(targetJson.name);
        targetDiv.append(titleSpan);
        targetDiv.append(sponsorDiv);
        targetDiv.append(buildsDiv);
        targetDiv.addClass('target');
        refresh(targetJson);
    }
    
    initialise();
    
    return {
        "updateFrom": refresh,
        "getContent": function() { return targetDiv; }
    };
};

ORG.NETMELODY.CIEYE.newRadiatorWidget = function() {
    var radiatorDiv = $('<div></div>'),
        targetWidgets = {};
    
    function refresh(targetGroupJson) {
        var targets = targetGroupJson.targets.sort(function(a, b) {
            if (a.status === b.status) {
                if (a.builds.length === b.builds.length) {
                    return (a.name < b.name) ? -1 : 1;
                }
                return (a.builds.length > b.builds.length) ? -1 : 1;
            }
            return (a.status === 'BROKEN' || b.status === 'GREEN') ? -1 : 1;
        });
        
        radiatorDiv.empty();
        $.each(targets, function(index, targetJson) {
            if (targetWidgets[targetJson.id]) {
                targetWidgets[targetJson.id].updateFrom(targetJson);
            }
            else {
                targetWidgets[targetJson.id] = ORG.NETMELODY.CIEYE.newTargetWidget(targetJson);
            }
            radiatorDiv.append(targetWidgets[targetJson.id].getContent());
        });
    }
    
    return {
        "updateFrom": refresh,
        "getContent": function() { return radiatorDiv; }
    };
};

ORG.NETMELODY.CIEYE.scheduler = function(browser) {
    var protector;
    
    function repeat(callback, interval) {
        browser.setInterval(callback, interval);
    }
    
    function guard(timeout) {
        if (!protector) {
            protector = browser.setTimeout(browser.location.reload, timeout);
        }
    }
    
    function relax() {
        if (!protector) {
            browser.clearTimeout(protector);
            protector = null;
        }
    }
    
    return {
        "repeat": repeat,
        "guard": guard,
        "relax": relax
    };
};

ORG.NETMELODY.CIEYE.newRadiator = function(radiatorDiv, scheduler) {
    var radiatorWidget = ORG.NETMELODY.CIEYE.newRadiatorWidget();
    
    function refresh() {
        scheduler.guard(30000);
        $.getJSON('landscapeobservation.json', function(targetList) {
            scheduler.relax();
            radiatorWidget.updateFrom(targetList);
        });
    }
    
    function startup() {
        radiatorDiv.append(radiatorWidget.getContent());
        refresh();
        scheduler.repeat(refresh, 2000);
    }
    
    return {
        "start": startup
    };
};

$(document).ready(function() {
    var scheduler = ORG.NETMELODY.CIEYE.scheduler(window);
    ORG.NETMELODY.CIEYE.newRadiator($('#radiator'), scheduler).start();
});
