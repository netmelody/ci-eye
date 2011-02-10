"use strict";

var ORG = (ORG) ? ORG : {};
ORG.NETMELODY = {};
ORG.NETMELODY.CIEYE = {};

ORG.NETMELODY.CIEYE.newBuildWidget = function(buildJson) {
    var buildDiv = $('<div></div>').addClass('progress-bar'),
        barDiv   = $('<div></div>');
        
    function updateProgress(percent) {
        barDiv.attr('style', 'width: ' + percent + '%');
    }
    
    function refresh(newBuildJson) {
        updateProgress(newBuildJson.progress);
    }
    
    function initialise() {
        buildDiv.append(barDiv);
        refresh(buildJson);
    }
    
    initialise();
    
    return {
        updateFrom: refresh,
        getContent: function() { return buildDiv; }
    };
};

ORG.NETMELODY.CIEYE.newTargetWidget = function(targetJson) {
    var currentTargetJson = { builds:[] },
        targetDiv = $('<div></div>'),
        buildsDiv = $('<div></div>');
    
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
    }
    
    function initialise() {
        var titleSpan = $('<span></span>');
        
        titleSpan.text(targetJson.name);
        targetDiv.append(titleSpan);
        targetDiv.append(buildsDiv);
        targetDiv.addClass('target');
        refresh(targetJson);
    }
    
    initialise();
    
    return {
        updateFrom: refresh,
        getContent: function() { return targetDiv; }
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
            return (a.status === 'BROKEN') ? -1 : 1;
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
        updateFrom: refresh,
        getContent: function() { return radiatorDiv; }
    };
};

ORG.NETMELODY.CIEYE.newRadiator = function(radiatorDiv, repeatingTaskProvider) {
    var radiatorWidget = ORG.NETMELODY.CIEYE.newRadiatorWidget();
    
    function refresh() {
        $.getJSON('landscapeobservation.json', { landscapeName: 'HIP' }, function(targetList) {
            radiatorWidget.updateFrom(targetList);
        });
    }
    
    function startup() {
        $(radiatorDiv).append(radiatorWidget.getContent());
    
        refresh();
        repeatingTaskProvider.setInterval(refresh, 1000);
    }
    
    return {
        start: startup
    };
};

window.onload = function() {
    ORG.NETMELODY.CIEYE.newRadiator(document.getElementById('radiator'), window).start();
};
