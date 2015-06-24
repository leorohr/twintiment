$(document).ready(function() {

/* Leaflet Map */
var map = L.map('map_div').setView([51.505, -0.09], 5);
L.tileLayer('http://{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg', { /* TODO Change to MapBox  */
	attribution: "Tiles and Geocoding Courtesy of <a href='http://www.mapquest.com/' target='_blank'>MapQuest</a>"
	+ "<img src='http://developer.mapquest.com/content/osm/mq_logo.png'",
	maxZoom: 18,
	subdomains: ["otile1","otile2","otile3","otile4"]
}).addTo(map);


/* Form */
$('#startStreaming').click(function() {
	startStreaming($('#filterTerms').val());
});

$('#stopStreaming').click(function() {
	stopStreaming();
});

/* --------------------------- */
/*	Websocket	   Streaming   */

function wscallback(message) {
	var js = JSON.parse(message.body);
	
	console.log(JSON.stringify(js,null,2));
	if(js['coords'] != null)
		L.marker(js['coords']).addTo(map);
	
	$('#tweetTable').find('tbody')
		.append($('<tr>')
				.append($('<td>')
					.append(js['message'])
				.append($('</td>')))
				.append($('<td>')
						.append(js['sentiment'])
				.append($('</td>')))
		.append($('</tr>')));
		
	
//	var table = $('#tweetTable');
//	var row = table.insertRow(0);
//	var msgCell = row.insertCell(0);
//	msgCell.innerHTML = js['message'];
//	var sentiCell = row.insertCell(1);
//	sentiCell.innerHTML = js['sentiment'];
	
}

var stompClient;
function connectToWs() {
	var socket = new SockJS("/Twintiment/data_stream");
	stompClient = Stomp.over(socket);

	stompClient.connect({},
		function(frame) {
			console.log("Connected " + frame);
		
		stompClient.subscribe("/queue/data", wscallback);
	}, function(error) {
		console.log("Error while connecting to STOMP server.\n" + error);
		}
	);		
}

/*
 * Starts the server's tweet feed and has the client poll for updates
 * continuously.
 */
function startStreaming(filterterms) {
	
	// Start tweet streamer
	var request = $.get("analysis/start_streaming", {
		"filterTerms" : filterterms
	}).fail(function(jqhxr, status, message) {
		console.log(status + ": " + message);
	}).done(function() {
		console.log("Started server stream.");
		
		connectToWs();
	});
	
}
	
/*
 * Stops the server's tweet feed as well as the client's polling
 */
function stopStreaming() {

	// Stop tweet streamer
	var request = $.get("analysis/stop_streaming")
		.done(function(reply) {
			console.log("Stopped server's tweet feed. Status: " + reply);

			stompClient.disconnect(function() {
				console.log("Disconnected from websocket.");
			});
		});
	
}

}); //document ready