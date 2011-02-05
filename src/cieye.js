
function addTarget(radiatorDiv, target) {
    var targetDiv = document.createElement('div');
  
    targetDiv.setAttribute('class', 'target ' + target.status);
    targetDiv.innerHTML = '<span>' + target.name + '</span>';
    
    for (i in target.builds) {
        addBuild(targetDiv, target.builds[i]);
    }
    radiatorDiv.appendChild(targetDiv);
}

function addBuild(targetDiv, build) {
     var buildDiv = document.createElement('div'),
         barDiv   = document.createElement('div');
     
     buildDiv.setAttribute('class', 'progress-bar');
     barDiv.setAttribute('style', 'width: ' + build.progress + '%');
     buildDiv.appendChild(barDiv);
     targetDiv.appendChild(buildDiv);
}

// Perform login: Ask user for name, and send message to socket.
function login() {
    var defaultUsername = (window.localStorage && window.localStorage.username) || 'yourname';
    var username = prompt('Choose a username', defaultUsername);
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

function connect() {
    var radiatorDiv = document.getElementById('radiator');
    $.getJSON('joblist.json', function(targetList) {
        for (i in targetList.targets) {
            addTarget(radiatorDiv, targetList.targets[i]);
        }
    });
}

// Connect on load.
window.onload = connect;
