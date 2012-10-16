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
    var image = $("<img></img>"),
        heightFactor = 1.0,
        widthFactor = 1.0,
        currentMaxSize = -1,
        loaded = false;
    
    function resizeImage() {
        if (!loaded) {
            return;
        }
        
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
        
        loaded = true;
        resizeImage();
    }
    
    function initialise() {
        image.attr({ "src": sponsorJson.picture,
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
        sponsorMugshots = {},
        displayedMugshots = {},
        markedOn = 0;
    
    function sortedSponsors(unsortedSponsors) {
        return unsortedSponsors.sort(function(a, b) {
            return (a.name === b.name) ? 0 : (a.name < b.name) ? -1 : 1;
        });
    }
    
    function calculateImageSize() {
        return parseInt(titleSpan.css("font-size"), 10) + 5;
    }
    
    function updateFrom(newTargetJson) {
        var lastTargetJson = currentTargetJson,
            deadMugshots = $.extend({}, displayedMugshots);
        
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

        targetDiv.toggleClass("marked", (new Date() - markedOn) < 12000);
        targetDiv.toggleClass("building", newTargetJson.builds.length !== 0);
        
        if (newTargetJson.builds.length === 0 && newTargetJson.status === "GREEN") {
            sponsorDiv.empty();
            displayedMugshots = {};
            return;
        }
        
        $.each(sortedSponsors(newTargetJson.sponsors), function(index, sponsorJson) {
            var mugshotId = sponsorJson.picture;
            if (!sponsorMugshots[mugshotId]) {
                sponsorMugshots[mugshotId] = ORG.NETMELODY.CIEYE.newMugshotWidget(sponsorJson, calculateImageSize);
            }
            delete deadMugshots[mugshotId];
            if (!displayedMugshots[mugshotId]) {
                displayedMugshots[mugshotId] = true;
                sponsorDiv.append(sponsorMugshots[mugshotId].getContent());
            }
        });
        $.each(deadMugshots, function(mugshotId, value) {
            if (displayedMugshots[mugshotId]) {
                sponsorMugshots[mugshotId].getContent().detach();
                delete displayedMugshots[mugshotId];
            }
        });
    }
    
    function viewDetails() {
        window.open(targetJson.webUrl);
    }
    
    function markAs(note) {
        return function() {
            $.post("addNote", { "id": targetJson.id, "note": note });
            markedOn = new Date() - 1;
            targetDiv.addClass("marked");
        };
    }
    
    function doh() {
        $.post("doh", { "active": true });
    }
        
    function getMenuItems() {
        var result = [];
        
        if (targetDiv.css("cursor") !== "pointer") {
            return result;
        }
        
        result.push({"label": "View Details", "handler": viewDetails});
        if (currentTargetJson.status !== "GREEN") {
            result.push({"label": "Mark as Under Investigation", "handler": markAs("Under Investigation")});
            result.push({"label": "Mark as Fixed", "handler": markAs("Fixed")});
        }
        result.push({"label": "D'OH", "handler": doh});
        return result;
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
        targetDiv.popupMenu(getMenuItems);
        
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
        dohDiv = $("<div></div>").addClass("doh").hide(),
        allGreenImg = $("<img></img>").hide(),
        dohAudio = $("<audio><source src='/doh.ogg' type='audio/ogg'/><source src='/doh.wav' type='audio/wav'/></audio>"),
        woohooAudio = $("<audio><source src='/woohoo.ogg' type='audio/ogg'/><source src='/woohoo.wav' type='audio/wav'/></audio>"),
        targetWidgets = {},
        dohMugshots = {},
        noisy = false,
        statusRanks = ["BROKEN", "UNKNOWN", "UNDER_INVESTIGATION", "GREEN", "DISABLED"];

    function targetComparator(a, b) {
        function compare(obj1, obj2) {
            return (obj1 < obj2) ? -1 : ((obj1 === obj2) ? 0 : 1);
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

    function play(audio) {
        if(noisy) {
            audio.load();
            audio.play();
        }
    }
    
    function doDoh(dohGroup) {
        function dohSizeCalculator() {
            return (radiatorDiv.width() / dohGroup.length) - 50;
        }
        
        if (dohDiv.is(":hidden")) {
            $.each(dohGroup, function(index, sponsorJson) {
                dohMugshots[sponsorJson.picture] = ORG.NETMELODY.CIEYE.newMugshotWidget(sponsorJson, dohSizeCalculator);
                dohDiv.append(dohMugshots[sponsorJson.picture].getContent());
                dohDiv.show();
                dohDiv.popupMenu(function() { return [{"label": "D'OH OVER", "handler": unDoh}]; });
            });
            play(dohAudio[0]);
        }
    }
    
    function doUnDoh() {
        if (!dohDiv.is(":hidden")) {
            dohDiv.hide();
            dohDiv.empty();
            play(woohooAudio[0]);
        }
    }

    function unDoh() {
        $.post("doh", { "active": false });
    }

    function isAllGreen(targetsJson) {
        if (targetsJson.length === 0) {
            return false;
        }
        
        var result = true;
        $.each(targetsJson, function(index, targetJson) {
            if (targetJson.builds.length !== 0 || (targetJson.status !== "GREEN" && targetJson.status !== "DISABLED")) {
                result = false;
                return false;
            }
        });
        return result;
    }
    
    function updateFrom(targetGroupJson) {
        var targets = targetGroupJson.targets.sort(targetComparator),
            deadTargetWidgets = $.extend({}, targetWidgets);

        if (targetGroupJson.dohGroup) {
            doDoh(targetGroupJson.dohGroup);
        } else {
            doUnDoh();
        }

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
            deadTargetWidget.getContent().remove();
            delete targetWidgets[index];
        });
        
        if (isAllGreen(targets) && !targetGroupJson.dohGroup) {
            allGreenImg.width("100%");
            allGreenImg.height("100%");
            allGreenImg.show();
        }
        else {
            allGreenImg.hide();
        }
    }
    
    function refresh() {
        $.each(dohMugshots, function(key, mugshotWidget) {
            mugshotWidget.refresh();
        });
        $.each(targetWidgets, function(index, targetWidget) {
            targetWidget.refresh();
        });
    }
    
    function silentMode(status) {
        noisy = status ? false : true;
    }
    
    radiatorDiv.append(dohDiv);
    radiatorDiv.append(dohAudio);
    radiatorDiv.append(allGreenImg);
    
    allGreenImg.mousemove(function() { allGreenImg.hide(); });
    $.getJSON("/sponsor.json", { "fingerprint": "all-green" }, function(sponsorJson) {
        if (sponsorJson) {
            allGreenImg.attr({ "src": sponsorJson.picture, "title": sponsorJson.name });
        }
    });
    
    return {
        "refresh": refresh,
        "silentMode": silentMode,
        "updateFrom": updateFrom,
        "getContent": function() { return radiatorDiv; }
    };
};

ORG.NETMELODY.CIEYE.newScheduler = function(browser) {
    var protector = undefined,
        alarm = false;
    
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
        $().announcer("announce", "Lost contact with CI-Eye server");
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
            protector = undefined;
        }
    }
    
    return {
        "repeat": repeat,
        "guard": guard,
        "relax": relax,
        "reload": reloadPage
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
    
    function silentMode(status) {
        radiatorWidget.silentMode(status);
    }
    
    return {
        "start": startup,
        "refresh": refresh,
        "silentMode": silentMode
    };
};

ORG.NETMELODY.CIEYE.newVersionChecker = function(scheduler) {
    var currentVersion = undefined;
    
    function assessVersion(versionString) {
        if (!currentVersion) {
            currentVersion = versionString;
            return;
        }
        
        if (currentVersion !== versionString) {
            scheduler.reload();
        }
    }
    
    function checkForNewVersion() {
        $.getJSON("/version.json", function(versionJson) {
            assessVersion(versionJson.currentServerVersion);
        });
    }
    
    function startup() {
        checkForNewVersion();
        scheduler.repeat(checkForNewVersion, 30000);
    }
    
    return {
        "start": startup
    };
};

ORG.NETMELODY.CIEYE.newStore = function(disk) {
    function saveBooleanValue(key, value) {
        if (!disk) {
            return;
        }
        disk.setItem(key, (value === true) ? "true" : "false");
    }
    
    function loadBooleanValue(key, defaultValue) {
        var result = disk ? disk.getItem(key) : null;
        return (result === null) ? (defaultValue === true) : (result === "true");
    }
    
    return {
        "saveBoolean": saveBooleanValue,
        "loadBoolean": loadBooleanValue
    };
};

$(document).ready(function() {
    if (!$("#radiator").length) {
        return;
    }
    
    var scheduler = ORG.NETMELODY.CIEYE.newScheduler(window),
        radiator = ORG.NETMELODY.CIEYE.newRadiator($("#radiator"), scheduler),
        updater = ORG.NETMELODY.CIEYE.newVersionChecker(scheduler),
        store = ORG.NETMELODY.CIEYE.newStore(window.localStorage),
        initialDesktopModeStatus = store.loadBoolean("desktopModeEnabled", $(window).width() <= 750),
        initialGridModeStatus = store.loadBoolean("gridModeEnabled", false),
        initialSilentModeStatus = store.loadBoolean("silentModeEnabled", initialDesktopModeStatus);
    
    function landscapeNameFromUri() {
        var path = $(location).attr("pathname");
        
        if (path.match(/\/$/)) {
            path = path.slice(0, -1);
        }
        
        return decodeURIComponent(path.substr((path.lastIndexOf("/") + 1)));
    }
    
    function desktopMode(desktopModeOn) {
        if (desktopModeOn) {
            $("head").append($("<link rel='stylesheet' href='/desktop.css' type='text/css'/>"));
        }
        else {
            $("head > link[href='/desktop.css']").remove();
        }
        store.saveBoolean("desktopModeEnabled", desktopModeOn);
        radiator.refresh();
        window.setTimeout(radiator.refresh, 200);
    }
    
    function gridMode(gridModeOn) {
        if (gridModeOn) {
            $("head").append($("<link rel='stylesheet' href='/grid.css' type='text/css'/>"));
        }
        else {
            $("head > link[href='/grid.css']").remove();
        }
        store.saveBoolean("gridModeEnabled", gridModeOn);
        radiator.refresh();
        window.setTimeout(radiator.refresh, 200);
    }
    
    function silentMode(silentModeOn) {
        store.saveBoolean("silentModeEnabled", silentModeOn);
        radiator.silentMode(silentModeOn);
    }
    
    document.title = landscapeNameFromUri() + " - " + document.title;
    gridMode(initialGridModeStatus);
    desktopMode(initialDesktopModeStatus);
    radiator.silentMode(initialSilentModeStatus);
    $("body").flyMenu([{"label": "Desktop Mode", "initialState": initialDesktopModeStatus, "changeHandler": desktopMode },
                       {"label": "Grid Mode", "initialState": initialGridModeStatus, "changeHandler": gridMode },
                       {"label": "Silent", "initialState": initialSilentModeStatus, "changeHandler": silentMode }]);
    
    radiator.start();
    updater.start();
});