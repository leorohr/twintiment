$(document).ready(function() {

//set up slider
window.sentimentRangeSlider = $('#sentimentRangeSlider').slider({
	id: "sentimentRangeSlider",
	min: -2,
	max: 2,
	range: true,
	value: [-2,2]
	});

//UID used for websocket communication
window.clientID = generateUid();

//Warn before leaving/reloading the page
$(window).bind('beforeunload', function() {
	return "This will stop the current analysis.";
}); 
//Stop streaming if page is refreshed or left
$(window).unload(function() {
	streamer.stopStreaming();
});

}); //document ready


//Global variables
window.streamer;


//Global functions
function setSettingsDisabled(disabled) { //disabled is boolean parameter
	$('#heatRadioBtn').prop('disabled', disabled);
	$('#markerRadioBtn').prop('disabled', disabled);
	$('#includeAllTweetsCB').prop('disabled', disabled);
	$('#fallbackGazetteer').prop('disabled', disabled);
	window.sentimentRangeSlider.slider(disabled ? 'disable' : 'enable');
}

function appendToTweetTable(tableid, message, sentiment) {
	$(tableid).find('tbody')
	.append($('<tr>')
			.append($('<td>')
				.append(message)
			.append($('</td>')))
			.append($('<td>')
					.append(sentiment)
			.append($('</td>')))
	.append($('</tr>')));	
}

//Generates a unique ID to identify the client's own websocket channel.
function generateUid() {

    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    return (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
};


// Wrapper function for $.ajax that posts the JSON contained in 'data' to the 'url'
$.postJSON = function(url, data, callback) {
    return $.ajax({
    headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
    },
    'type': 'POST',
    'url': url,
    'data': JSON.stringify(data),
    'success': callback
    });
};