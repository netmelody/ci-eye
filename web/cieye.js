"use strict";
var ORG = window.ORG ? window.ORG : {};
ORG.NETMELODY = ORG.NETMELODY ? ORG.NETMELODY : {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newBuildWidget = function(buildJson) {
    var buildDiv = $("<div></div>").addClass("progress-bar"),
        barDiv   = $("<div></div>");
    
    function updateProgress(percent) {
        barDiv.attr("style", "width: " + percent + "%");
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

ORG.NETMELODY.CIEYE.newMugshotWidget = function(sponsorJson, sizeCalculator) {
    var image,
        heightFactor = 1.0,
        widthFactor = 1.0,
        currentMaxSize = -1;
    
    function resizeImage() {
        var maxSize = sizeCalculator();
        
        if (maxSize === currentMaxSize) {
            return;
        }
        
        currentMaxSize = maxSize;
        image.height(maxSize * heightFactor);
        image.width(maxSize * widthFactor);
    }
    
    function initialiseImage() {
        var width = image.width(),
            height = image.height();
        
        if (width > height) {
            heightFactor = height / width;
        }
        else {
            widthFactor = width / height;
        }
        
        resizeImage();
    }
    
    function initialise() {
        image = $("<img></img>")
                    .attr({ "src": sponsorJson.picture,
                            "title": sponsorJson.name })
                    .load(initialiseImage);
    }
    
    initialise();
    
    return {
        "getContent": function() { return image; },
        "refresh": resizeImage
    };
};

ORG.NETMELODY.CIEYE.newTargetWidget = function(targetJson) {
    var currentTargetJson = { builds:[] },
        targetDiv = $("<div></div>"),
        titleSpan = $("<span></span>"),
        sponsorDiv = $("<div></div>").addClass("sponsors"),
        buildsDiv = $("<div></div>"),
        sponsorMugshots = {};
    
    function sortedSponsors(unsortedSponsors) {
        return unsortedSponsors.sort(function(a, b) {
            return (a.name === b.name) ? 0 : (a.name < b.name) ? -1 : 1;
        });
    }
    
    function calculateImageSize() {
        return parseInt(titleSpan.css("font-size"), 10) + 5;
    }
    
    function updateFrom(newTargetJson) {
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

        if (newTargetJson.builds.length === 0) {
            targetDiv.removeClass("building");
        }
        else {
            targetDiv.addClass("building");
        }
        
        if (newTargetJson.builds.length === 0 && newTargetJson.status === "GREEN") {
            sponsorDiv.empty();
            return;
        }
        
        $.each(sortedSponsors(newTargetJson.sponsors), function(index, sponsorJson) {
            if (!sponsorMugshots[sponsorJson.picture]) {
                sponsorMugshots[sponsorJson.picture] = ORG.NETMELODY.CIEYE.newMugshotWidget(sponsorJson, calculateImageSize);
            }
            sponsorDiv.append(sponsorMugshots[sponsorJson.picture].getContent());
        });
    }
    
    function clickable() {
        return (targetDiv.css("cursor") === "pointer");
    }
    
    function viewDetails() {
        window.open(targetJson.webUrl);
    }
    
    function markAsUnderInvestigation() {
        $.post("addNote", { "id": targetJson.id, "note": "Under Investigation" } );
    }
    
    function markAsFixed() {
        $.post("addNote", { "id": targetJson.id, "note": "Fixed" } );
    }
    
    function refreshImages() {
        $.each(sponsorMugshots, function(key, mugshotWidget) {
            mugshotWidget.refresh();
        });
    }
    
    function initialise() {
        titleSpan.text(targetJson.name);
        targetDiv.append(titleSpan);
        targetDiv.append(sponsorDiv);
        targetDiv.append(buildsDiv);
        targetDiv.addClass("target");
        targetDiv.popupMenu([{"label": "View Details", "handler": viewDetails},
                             {"label": "Mark as Under Investigation", "handler": markAsUnderInvestigation},
                             {"label": "Mark as Fixed", "handler": markAsFixed}],
                            clickable);
        
        updateFrom(targetJson);
    }
    
    initialise();
    
    return {
        "refresh": refreshImages,
        "updateFrom": updateFrom,
        "getContent": function() { return targetDiv; }
    };
};

ORG.NETMELODY.CIEYE.newRadiatorWidget = function() {
    var radiatorDiv = $("<div></div>"),
        targetWidgets = {},
        statusRanks = ["BROKEN", "UNKNOWN", "UNDER_INVESTIGATION", "GREEN", "DISABLED"];

    function targetComparator(a, b) {
        function compare(a, b) {
            return (a < b) ? -1 : ((a === b) ? 0 : 1);
        }
        
        if (a.status !== b.status) {
            return compare(statusRanks.indexOf(a.status), statusRanks.indexOf(b.status));
        }
        
        if (a.builds.length !== b.builds.length) {
            return compare(b.builds.length, a.builds.length);
        }
        
        if (a.lastStartTime !== b.lastStartTime) {
            return compare(b.lastStartTime, a.lastStartTime);
        }
        
        return compare(a.name, b.name);
    }
    
    function updateFrom(targetGroupJson) {
        var targets = targetGroupJson.targets.sort(targetComparator),
            deadTargetWidgets = $.extend({}, targetWidgets);
        
        $.each(targets, function(index, targetJson) {
            if (targetWidgets[targetJson.id]) {
                targetWidgets[targetJson.id].updateFrom(targetJson);
                delete deadTargetWidgets[targetJson.id];
            }
            else {
                targetWidgets[targetJson.id] = ORG.NETMELODY.CIEYE.newTargetWidget(targetJson);
            }
            radiatorDiv.append(targetWidgets[targetJson.id].getContent());
        });
        $.each(deadTargetWidgets, function(index, deadTargetWidget) {
            radiatorDiv.remove(deadTargetWidget.getContent());
        });
    }
    
    function refresh() {
        $.each(targetWidgets, function(index, targetWidget) {
            targetWidget.refresh();
        });
    }
    
    return {
        "refresh": refresh,
        "updateFrom": updateFrom,
        "getContent": function() { return radiatorDiv; }
    };
};

ORG.NETMELODY.CIEYE.scheduler = function(browser) {
    var protector,
        alarm;
    
    function reloadPage() {
        browser.location.reload();
    }
    
    function safeCallableFor(callback) {
        return function() {
            try {
                callback();
            }
            catch (ex) {
                // ignore
            }
        };
    }
    
    function raiseAlarm() {
        alarm = true;
        $.announcer.announce("Lost contact with Ci-Eye server");
    }
    
    function repeat(callback, interval) {
        browser.setInterval(safeCallableFor(callback), interval);
    }
    
    function guard(timeout) {
        if (!protector) {
            protector = browser.setTimeout(raiseAlarm, timeout);
        }
    }
    
    function relax() {
        if (alarm) {
            reloadPage();
        }
        if (protector) {
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
    
    function update() {
        scheduler.guard(60000);
        $.getJSON("landscapeobservation.json", function(targetList) {
            scheduler.relax();
            radiatorWidget.updateFrom(targetList);
        });
    }
    
    function refresh() {
        radiatorWidget.refresh();
    }
    
    function startup() {
        radiatorDiv.append(radiatorWidget.getContent());
        update();
        scheduler.repeat(update, 2000);
    }
    
    return {
        "start": startup,
        "refresh": refresh
    };
};

$(document).ready(function() {
    var scheduler = ORG.NETMELODY.CIEYE.scheduler(window),
        radiator = ORG.NETMELODY.CIEYE.newRadiator($("#radiator"), scheduler);
    
    $(window).bind("resize", function() {
        radiator.refresh();
    });
    
    radiator.start();
});
