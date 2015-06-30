$(document).ready(function() {
	
/* Leaflet Map */
var map = L.map('map_div').setView([51.505, -0.09], 5);
L.tileLayer('http://{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg', { /* TODO Change to MapBox  */
	attribution: "Tiles and Geocoding Courtesy of <a href='http://www.mapquest.com/' target='_blank'>MapQuest</a>"
	+ "<img src='http://developer.mapquest.com/content/osm/mq_logo.png'",
	maxZoom: 18,
	subdomains: ["otile1","otile2","otile3","otile4"]
}).addTo(map);

/* Chart */
$('#chart_div').highcharts({
	chart: {
		zoomType: 'x'
	},
	title: {
		text: 'Tweet Streaming Rate'
	},
	subtitle: {
		text: document.ontouchstart === undefined ?
				'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
	},
	xAxis: {
		type: 'datetime',
		tickInterval: 10000
	},
	yAxis: {
		title: {
			text: '#Tweets/min'
		}
	},
	legend: {
		enabled: false
	},
	plotOptions: {
			series: {
				marker: {
					enabled: false
				}
			},
			lineWidth: 1,
			states: {
				hover: {
					lineWidth: 1
				}
			},
			threshold: null
	},
	series: [{
		type: 'line',
		name: 'Tweets'
	}]
});
var chart = $('#chart_div').highcharts();

/* Form */
$('#startStreaming').click(function() {
	streamer.startStreaming($('#filterTerms').val());
});

$('#stopStreaming').click(function() {
	streamer.stopStreaming();
});
//Do not submit form on enter
$('#filterTerms').keypress(function(event) {
//	$('#startStreaming').click();
	return event.keyCode != 13;
});

/* ---------------------------- */
/*	Websocket Streaming Module  */
var streamer = (function() {
	var stompClient;
	
	function data_callback(message) {
		var js = JSON.parse(message.body);
		
//		console.log(JSON.stringify(js,null,2));
		
		//Add marker to table
		if(js['coords'] != null)
			L.marker(js['coords']).addTo(map);
		
		//Add row to table
		$('#tweetTable').find('tbody')
			.append($('<tr>')
					.append($('<td>')
						.append(js['message'])
					.append($('</td>')))
					.append($('<td>')
							.append(js['sentiment'])
					.append($('</td>')))
			.append($('</tr>')));	
		
	} //data_callback

	function rate_callback(message) {
		var js = JSON.parse(message.body);
		
		console.log(JSON.stringify(js,null,2));

		chart.series[0].addPoint([js['date'], js['tweets']]);	
	} //rate_callback
	
	
	function connectToWs() {
		var socket = new SockJS("/Twintiment/data_stream");
		stompClient = Stomp.over(socket);
		stompClient.debug = null; //deactivate debug messages
		
		stompClient.connect({},
			function(frame) {
				console.log("Connected " + frame);
			
			stompClient.subscribe("/queue/data", data_callback);

			stompClient.subscribe("/queue/tweet_rate", rate_callback);
		}, function(error) {
			console.log("Error while connecting to STOMP server.\n" + error);
			}
		);		
	} //connectToWs

	//public
	return { 
		/*
		 * Starts the server's tweet feed and has the client poll for updates
		 * continuously.
		 */
		startStreaming : function(filterterms) {
			// Start tweet streamer
			var request = $.get("analysis/start_streaming", {
				"filterTerms" : filterterms
			}).fail(function(jqhxr, status, message) {
				console.log(status + ": " + message);
			}).done(function() {
				console.log("Started server stream.");
				
				connectToWs();
			});
		}, //startStreaming
	
		/*
		 * Stops the server's tweet feed as well as the client's polling
		 */
		stopStreaming : function() {
			var request = $.get("analysis/stop_streaming")
				.done(function(reply) {
					console.log("Stopped server's tweet feed. Status: " + reply);
		
					stompClient.disconnect(function() {
						console.log("Disconnected from websocket.");
					});
				});
		} //stopStreaming
	} //public
}()); //module Streamer


}); //document ready