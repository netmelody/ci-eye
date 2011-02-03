
function addEntry(target) {
    var radiatorDiv = document.getElementById('radiator'),
        newdiv = document.createElement('div');
  
    newdiv.setAttribute('class','entry ' + target.status);
    newdiv.innerHTML = '<span>' + target.name + ' - ' + target.status + '</span>';
    radiatorDiv.appendChild(newdiv);
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
    $.getJSON('joblist.json', function(targetList) {
        for (i in targetList.targets) {
            addEntry(targetList.targets[i]);
        }
    });
}

// Connect on load.
window.onload = connect;
