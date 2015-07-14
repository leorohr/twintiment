$(document).ready(function() {

//set up slider
window.sentimentRangeSlider = $('#sentimentRangeSlider').slider({
	id: "sentimentRangeSlider",
	min: -5,
	max: 5,
	range: true,
	value: [-5,5]
	});

}); //document ready


//Global variables
window.streamer;


//Global functions
function setSettingsDisabled(disabled) { //disabled is boolean parameter
	$('#heatRadioBtn').prop('disabled', disabled);
	$('#markerRadioBtn').prop('disabled', disabled);
	$('#includeAllTweetsCB').prop('disabled', disabled);
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