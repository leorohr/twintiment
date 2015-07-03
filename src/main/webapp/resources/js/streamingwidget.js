$(document).ready(function() {

/*	Websocket Streaming Module  */
streamer = (function() {
	var stompClient;
	
	/* UI Bindings */
	$('#startStreaming').click(function() {
		streamer.startStreaming($('#filterTerms').val());
	});

	$('#stopStreaming').click(function() {
		streamer.stopStreaming();
	});

	$('#startFileAnalysis').click(function(e) {
		streamer.startFileAnalysis();
	});

	$('#stopFileAnalysis').click(function(e) {
		streamer.stopStreaming();
	});
	
	//Do not submit form on enter
	$('#filterTerms').keypress(function(event) {
		return event.keyCode != 13; 
	});
	
	function data_callback(message) {
		var js = JSON.parse(message.body);
				
		//Add marker to map
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
		
		//Update TweetsPerMin		
		var minuteDate = new Date(js['date']).setSeconds(0); //date variable with secs dropped
		
		if(tweetPerMin[tweetPerMin.length-1] != null && tweetPerMin[tweetPerMin.length-1]['x'] == minuteDate)
			tweetPerMin[tweetPerMin.length-1]['y'] += 1;
		else tweetPerMin.push({x: minuteDate, y: 1});
		
		//update chart
		tpm_chart.series[0].setData(tweetPerMin);

	} //data_callback

	function rate_callback(message) {
		var js = JSON.parse(message.body);
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
		selectedFile : "",

		/*
		 * Starts the server's live tweet feed and connects to the websocket.
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
		 * Starts the analysis of 'filename' on the server and connects to the websocket.
		 */
		startFileAnalysis : function() {
			if(streamer.selectedFile == "") {
				alert("Please select a file from the list or upload a new one.");
				return;
			}

			// Start tweet streamer
			var request = $.get("analysis/start", {
				"filename" : this.selectedFile
			}).fail(function(jqhxr, status, message) {
				console.log(status + ": " + message);
			}).done(function() {
				console.log("Started server stream.");
				
				connectToWs();
			});
		}, //startFileAnalysis
	
		/*
		 * Stops the server's tweet feed and disconnects from the websocket.
		 */
		stopStreaming : function() {
			var request = $.get("analysis/stop")
				.done(function(reply) {
					console.log("Stopped server's tweet feed. Status: " + reply);
		
					stompClient.disconnect(function() {
						console.log("Disconnected from websocket.");
					});
				});
		} //stopStreaming
	} //public
}()); //module Streamer

});