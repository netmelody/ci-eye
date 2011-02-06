
function initialise() {
    var addLandscapeButton = document.getElementById('addLandscapeButton');
    addLandscapeButton.setAttribute('onclick', 'createLandscape();');
    
    var landscapeSelector = document.getElementById('landscapeSelector');
    landscapeSelector.setAttribute('onchange', 'displayFeatures();');
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
            addLandscapeOption(landscapeSelector, landscapeList.landscapes[i]);
        }
        displayFeatures();
    });
}

function addLandscapeOption(landscapeSelector, landscape) {
    var landscapeOption = document.createElement('option');
    
    landscapeOption.innerHTML = landscape.name;
    landscapeOption.setAttribute('value', landscape.name);
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

function displayFeatures() {
    var landscapeName = document.getElementById('landscapeSelector').value;
    
    $.getJSON('landscape.json', { name: landscapeName }, function(landscape) {
        refreshFeatures(landscape);
    });
}

function refreshFeatures(landscape) {
    var featuresDiv = document.getElementById('features');
    
    $(featuresDiv).empty();
    for (i in landscape.features) {
        addFeature(featuresDiv, landscape.features[i]);
    }
}

function addFeature(featuresDiv, feature) {
    var featureItem = document.createElement('span');
    
    featureItem.innerHTML = feature.endpoint + ' - ' + feature.name;
    featuresDiv.appendChild(featureItem);
}

window.onload = initialise;
