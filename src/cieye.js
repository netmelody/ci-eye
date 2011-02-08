"use strict";

// Perform login: Ask user for name, and send message to socket.
function login() {
    var defaultUsername = (window.localStorage && window.localStorage.username) || 'yourname',
        username = prompt('Choose a username', defaultUsername);
    
    if (username) {
        if (window.localStorage) { // store in browser localStorage, so we remember next next
            window.localStorage.username = username;
        }
//        send({action:'LOGIN', loginUsername:username});
//        document.getElementById('entry').focus();
    } else {
//        ws.close();
    }
}

function addBuild(targetDiv, build) {
     var buildDiv = document.createElement('div'),
         barDiv   = document.createElement('div');
     
     buildDiv.setAttribute('class', 'progress-bar');
     barDiv.setAttribute('style', 'width: ' + build.progress + '%');
     buildDiv.appendChild(barDiv);
     targetDiv.appendChild(buildDiv);
}

function updateTarget(radiatorDiv, target) {
    var targetDivId = 'target_' + target.id,
        targetDiv = document.getElementById(targetDivId);
    
    if (!targetDiv) {
        targetDiv = document.createElement('div');
        targetDiv.setAttribute('id', targetDivId);
        targetDiv.innerHTML = '<span>' + target.name + '</span>';
    }
  
    targetDiv.setAttribute('class', 'target ' + target.status);
    $(targetDiv).children('.progress-bar').remove();
    
    for (i in target.builds) {
        addBuild(targetDiv, target.builds[i]);
    }
    radiatorDiv.appendChild(targetDiv);
}

function refreshTargets(radiatorDiv) {
    $.getJSON('landscapeobservation.json', { landscapeName: 'Ci-eye Demo' }, function(targetList) {
        for (i in targetList.targets) {
            updateTarget(radiatorDiv, targetList.targets[i]);
        }
    });
}

function start() {
    var radiatorDiv = document.getElementById('radiator');
    refreshTargets(radiatorDiv);
    setInterval(function(){ refreshTargets(radiatorDiv); }, 1000);
}

window.onload = start;
