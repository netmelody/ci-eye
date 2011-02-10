"use strict";

var ORG = (ORG) ? ORG : {};
ORG.NETMELODY = {};

ORG.NETMELODY.newBuildWidget = function(buildJson) {
    var buildDiv = $('<div></div>').addClass('progress-bar'),
        barDiv   = $('<div></div>');
        
    function updateProgress(percent) {
        barDiv.setAttribute('style', 'width: ' + percent + '%');
    }
    
    function refresh(newBuildJson) {
        updateProgress(newBuildJson.progress);
    }
    
    function initialise() {
        buildDiv.appendChild(barDiv);
        refresh(buildJson);
    }
    
    initialise();
    
    return {
        updateFrom: refresh,
        getContent: function() { return buildDiv; },
        getId: function() { return "buildId"; }
    };
};

ORG.NETMELODY.newTargetWidget = function(targetJson) {
    var currentTargetJson = {},
        targetDiv = $('<div></div>').addClass('target'),
        buildsDiv = $('<div></div>');
    
    function refresh(newTargetJson) {
        var lastTargetJson = currentTargetJson;
        
        currentTargetJson = newTargetJson;
        if (lastTargetJson.status !== newTargetJson.status) {
            targetDiv.removeClass(lastTargetJson.status);
            targetDiv.addClass(newTargetJson.status);
        }
        
        $(buildsDiv).empty();
        $.each(newTargetJson.builds, function(buildJson) {
            buildsDiv.appendChild(ORG.NETMELODY.newBuildWidget(buildJson).getContent());
        });
    }
    
    function initialise() {
        targetDiv.innerHTML = '<span>' + targetJson.name + '</span>';
        targetDiv.appendChild(buildsDiv);
        refresh(targetJson);
    }
    
    initialise();
    
    return {
        updateFrom: refresh,
        getContent: function() { return targetDiv; },
        getId: function() { return targetJson.id; }
    };
};

ORG.NETMELODY.newRadiatorWidget = function() {
    var radiatorDiv = $('<div></div>');
    
    function refresh(targetGroupJson) {
        var targets = targetGroupJson.targets.sort(function(a, b) {
            if (a.status === b.status) {
                return (a.builds.length > b.builds.length) ? -1 : (a.builds.length < b.builds.length) ? 1 : 0;
            }
            if (a.status === 'BROKEN') {
                return -1;
            }
            return 1;
        });
        
        $(radiatorDiv).empty();
        $.each(targets, function(targetJson) {
            radiatorDiv.appendChild(ORG.NETMELODY.newTargetWidget(targetJson).getContent());
        });
    }
    
    return {
        updateFrom: refresh,
        getContent: function() { return radiatorDiv; }
    };
};

ORG.NETMELODY.newRadiator = function(radiatorDiv, repeatingTaskProvider) {
    var radiatorWidget = ORG.NETMELODY.newRadiatorWidget();
    
    function refresh() {
        $.getJSON('landscapeobservation.json', { landscapeName: 'HIP' }, function(targetList) {
            radiatorWidget.updateFrom(targetList);
        });
    }
    
    function startup() {
        radiatorDiv.appendChild(radiatorWidget.getContent());
    
        refresh();
        repeatingTaskProvider.setInterval(refresh, 1000);
    }
    
    return {
        start: startup
    };
};

window.onload = ORG.NETMELODY.newRadiator(document.getElementById('radiator'), window).start();
