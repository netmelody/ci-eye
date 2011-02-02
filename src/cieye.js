// Log text to main window.
function logText(msg) {
    var radiatorDiv = document.getElementById('radiator');
    radiatorDiv.value = radiatorDiv.value + msg + '\n';
    radiatorDiv.scrollTop = radiatorDiv.scrollHeight; // scroll into view
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

function onMessage(incoming) {
    switch (incoming.action) {
        case 'JOIN':
            logText("* User '" + incoming.username +"' joined.");
            break;
        case 'LEAVE':
            logText("* User '" + incoming.username +"' left.");
            break;
        case 'SAY':
            logText("[" + incoming.username +"] " + incoming.message);
            break;
    }
}

function connect() {
  $.getJSON('joblist.json', function(data) { logText("Success") });
}

// Connect on load.
window.onload = connect;
