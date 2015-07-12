$(document).ready(function() {


}); //document ready


//Global variables
window.streamer;


//Global functions
function setRadioButtonsDisabled(disabled) {
	$('#heatRadioBtn').attr('disabled', disabled);
	$('#markerRadioBtn').attr('disabled', disabled);
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