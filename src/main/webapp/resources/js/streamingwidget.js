$(document).ready(function() {

/*	Websocket Streaming Module  */
streamer = (function() {
	var stompClient;
	
	/* UI Bindings */
	$('#startStreaming').click(function() {
		var str = $('#filterTerms').val().toLowerCase();

		var words = str.split(/\s|,/g);
		var hashTags = [];
		var filterTerms = [];
		words.forEach(function(val, i, arr) {
			if(val == "")
				return;
			if(val.search('#') != -1)
				hashTags.push(val);
			else filterTerms.push(val);
		});
		if(filterTerms.length == 0 && hashTags.length > 0)
			filterTerms = hashTags;

		streamer.startStreaming(filterTerms, hashTags);
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
				mapWidget.addMarker(js['coords'], js['sentiment'], js['message']);
			}
 		}

		//Add row to table
		appendToTweetTable('#tweetTable', js['message'], js['sentiment']);
		
		//update sentiment chart
		sentiment_chart.series[0].addPoint([js['date'], js['sentiment']]);  
		

	} //data_callback	
	
	function connectToWs() {
		var socket = new SockJS("/Twintiment/data_stream");
		stompClient = Stomp.over(socket);
		stompClient.debug = null; //deactivate debug messages
		
		stompClient.connect({},
			function(frame) {
				console.log("Connected " + frame);
			
				stompClient.subscribe("/queue/data-" + window.clientID, data_callback);

				//Poll for stats every 5 seconds
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
						sentiment_chart.series[1].addPoint([sentiment_chart.series[0].xData[sentiment_chart.series[0].xData.length-1], response.avgSentiment]);

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
		startStreaming : function(filterTerms, hashTags) {
			if(filterTerms.length == 0 && hashTags.length == 0) {
				alert("Please enter filterterms and/or hashtags!");
				return;
			}

			// Start tweet streamer
			$.postJSON("/Twintiment/analysis/start_streaming", {
				clientID : window.clientID,
				fileName : null,
				filterTerms : filterTerms,
				hashTags :  hashTags,
				includeAllTweets: $('#includeAllTweetsCB').prop('checked'),
				fallbackGazetteer: $('#fallbackGazetteer').prop('checked'),
				sentimentRange: window.sentimentRangeSlider.slider('getValue'),
				areas: mapWidget.getDrawnSquares()
			}, function() {
				console.log("Started server stream.");
				
				//Disable settings
				setSettingsDisabled(true);

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
			$.postJSON("/Twintiment/analysis/start_streaming", {
				clientID : window.clientID,
				fileName : this.selectedFile,
				includeAllTweets: $('#includeAllTweetsCB').prop('checked'),
				sentimentRange: window.sentimentRangeSlider.slider('getValue'),
				areas: mapWidget.getDrawnSquares(),
				hashTags : [],
				filterTerms: []
			}, function() {
				console.log("Started server stream.");
				
				//Disable radio buttons
				setSettingsDisabled(true);

				connectToWs();
			}).fail(function(jqhxr, status, message) {
				console.log(status + ": " + message);
			});

		}, //startFileAnalysis
	
		/*
		 * Stops the server's tweet feed and disconnects from the websocket.
		 */
		stopStreaming : function() {
			$.get("/Twintiment/analysis/stop")
				.done(function(reply) {
					console.log("Stopped server's tweet feed. Status: " + reply);
		
					stompClient.disconnect(function() {
						console.log("Disconnected from websocket.");
					});
					
					//Stop polling for top tweets
					clearInterval(window.intervalVar);

					//reactivate settings
					setSettingsDisabled(false);
				});
		} //stopStreaming
	}; //public
}()); //module Streamer

});