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

	$('#filterTerms').keypress(function(event) {
		if(event.keyCode==13)
			$('#startStreaming').click(); //start
		return event.keyCode != 13; //no form submit (to prevent refresh)
	});
	
	$('#startFileAnalysis').click(function(e) {
		streamer.startFileAnalysis();
	});

	$('#stopFileAnalysis').click(function(e) {
		streamer.stopStreaming();
	});
	
	function data_callback(message) {
		var js = JSON.parse(message.body);
				
		//Add heatmap point or marker
		if(js['coords'] != null) {
			if($('#heatRadioBtn').prop('checked')) {
				mapWidget.addHeatPoint(js['coords']);
			}
			else {
				var marker = L.marker(js['coords']);//.addTo(mapWidget.map);
				marker.bindPopup(js['message']+"<br>Sentiment: "+js['sentiment']);
				mapWidget.addMarker(marker);;
			}
 		}

		//Add row to table
		appendToTweetTable('#tweetTable', js['message'], js['sentiment']);
		
		//update sentiment chart
		//TODO js['date'] or current timestamp?
		sentiment_chart.series[0].addPoint([new Date().getTime(), js['sentiment']]);  
		

	} //data_callback	
	
	function connectToWs() {
		var socket = new SockJS("/Twintiment/data_stream");
		stompClient = Stomp.over(socket);
		stompClient.debug = null; //deactivate debug messages
		
		stompClient.connect({},
			function(frame) {
				console.log("Connected " + frame);
			
			stompClient.subscribe("/queue/data", data_callback);
			
			//Poll for stats every 10 seconds
			window.intervalVar = setInterval(function() {
				
				$.get('/Twintiment/analysis/stats', function(response) {
				
					//Update statistics table
					$('#statsTable #numTweets').html(response.numTweets);
					$('#statsTable #numInferred').html(response.numInferred);
					$('#statsTable #numTagged').html(response.numTagged);
					$('#statsTable #avgSentiment').html(response.avgSentiment);
					$('#statsTable #maxDist').html(response.maxDist);
					$('#statsTable #avgTime').html(response.avgTime);
					
					
					//Update top tweets
					$('#topPosTweetsTable tbody').children().remove(); //clear table
					for(i in response.topPosTweets) {
						if(response.topPosTweets[i] != null)
							appendToTweetTable('#topPosTweetsTable', response.topPosTweets[i].message, response.topPosTweets[i].sentiment);
					}
					$('#topNegTweetsTable tbody').children().remove(); //clear table
					for(i in response.topNegTweets) {
						if(response.topNegTweets[i] != null)
							appendToTweetTable('#topNegTweetsTable', response.topNegTweets[i].message, response.topNegTweets[i].sentiment);
					}
					
					var data = tpm_chart.series[0].data; 
					var prev = 0;
					for(var i=0; i<data.length; ++i) {
						prev += data[i]['y'];
					}
					tpm_chart.series[0].addPoint([new Date().getTime(), response.numTweets-prev]);
					sentiment_chart.series[1].addPoint([new Date().getTime(), response.avgSentiment]);
				
				});
			
			}, 5000);
			
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
		startStreaming : function(filterTerms) {

			// Start tweet streamer
			$.postJSON("/Twintiment/analysis/start_streaming", {
				filterTerms : filterTerms,
				includeAllTweets: $('#includeAllTweetsCB').prop('checked')
			}, function() {
				console.log("Started server stream.");
				
				//Disable radio buttons
				setRadioButtonsDisabled(true);

				connectToWs();
			}).fail(function(jqhxr, status, message) {
				console.log(status + ": " + message);
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
			$.postJSON("/Twintiment/analysis/start", {
				fileName : this.selectedFile,
				includeAllTweets: $('#includeAllTweetsCB').prop('checked')
			}, function() {
				console.log("Started server stream.");
				
				//Disable radio buttons
				setRadioButtonsDisabled(true);

				connectToWs();
			}).fail(function(jqhxr, status, message) {
				console.log(status + ": " + message);
			});

		}, //startFileAnalysis
	
		/*
		 * Stops the server's tweet feed and disconnects from the websocket.
		 */
		stopStreaming : function() {
			var request = $.get("/Twintiment/analysis/stop")
				.done(function(reply) {
					console.log("Stopped server's tweet feed. Status: " + reply);
		
					stompClient.disconnect(function() {
						console.log("Disconnected from websocket.");
					});
					
					//Stop polling for top tweets
					clearInterval(window.intervalVar);

					//reactivate radiobuttons
					setRadioButtonsDisabled(false);
				});
		} //stopStreaming
	} //public
}()); //module Streamer

});