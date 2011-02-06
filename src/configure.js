
function initialise() {
    var addLandscapeButton = document.getElementById('addLandscapeButton');
    addLandscapeButton.setAttribute('onclick', 'createLandscape();');
    refreshLandscapes();
}

function refreshLandscapes() {
    var landscapeSelector = document.getElementById('landscapeSelector');
    $(landscapeSelector).empty();
    fetchLandscapes(landscapeSelector);
}

function fetchLandscapes(landscapeSelector) {
    $.getJSON('landscapelist.json', function(landscapeList) {
        for (i in landscapeList.landscapes) {
            addLandscapeOption(landscapeSelector, landscapeList.landscapes[i].name);
        }
    });
}

function addLandscapeOption(landscapeSelector, landscapeName) {
    var landscapeOption = document.createElement('option');
    
    landscapeOption.innerHTML = landscapeName;
    landscapeSelector.appendChild(landscapeOption);
}

function createLandscape() {
    var landscapeName = prompt('Enter a name for the new Landscape', "My Project");

    if (!landscapeName) {
        return;
    }
    
    $.post('createLandscape.json', landscapeName, function() {
        refreshLandscapes();
    });
}

// Connect on load.
window.onload = initialise;
