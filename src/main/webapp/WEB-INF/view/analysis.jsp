<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>

<head>
<title>Twintiment</title>

<!--  jQuery  -->
<script type="text/javascript"
	src="<c:url value="/resources/bower_components/jquery/dist/jquery.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/bower_components/jquery-ui/ui/minified/widget.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/bower_components/blueimp-file-upload/js/jquery.fileupload.js" />"></script>

<script type="text/javascript"
	src="<c:url value="/resources/bower_components/sockjs-client/dist/sockjs.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/bower_components/stomp-websocket/lib/stomp.min.js" />"></script>

<!-- Bootstrap -->
<link rel="stylesheet"	
	href="<c:url value="/resources/bower_components/bootstrap/dist/css/bootstrap.min.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/bower_components/bootstrap/dist/css/bootstrap-theme.min.css"/>" />
<script
	src="<c:url value="/resources/bower_components/bootstrap/dist/js/bootstrap.min.js"/> "></script>

<!-- Leaflet  -->
<link rel="stylesheet"
	href="<c:url value="/resources/bower_components/leaflet/dist/leaflet.css" />">
<script
	src="<c:url value="/resources/bower_components/leaflet/dist/leaflet.js" />"></script>
<!-- Plugins -->
<script
	src="<c:url value="/resources/bower_components/Leaflet.heat/dist/leaflet-heat.js"/>"></script>
<script
	src="<c:url value="/resources/bower_components/leaflet.markercluster/dist/leaflet.markercluster.js"/>"></script>
<link rel="stylesheet"
	href="<c:url value="/resources/bower_components/leaflet.markercluster/dist/MarkerCluster.Default.css" />">
<script
	src="<c:url value="/resources/bower_components/leaflet.draw/dist/leaflet.draw.js"/>"></script>
<link rel="stylesheet"
	href="<c:url value="/resources/bower_components/leaflet.draw/dist/leaflet.draw.css" />">
	
<!-- Highcharts -->
<script
	src="<c:url value="/resources/bower_components/highcharts/highcharts.js"/>"></script>
<script
	src="<c:url value="/resources/bower_components/highcharts/modules/exporting.js"/>"></script>

<!-- Bootstrap-Slider -->
<script
	src="<c:url value="/resources/bower_components/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"/>"></script>
<link rel="stylesheet"
	href="<c:url value="/resources/bower_components/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css" />">

<link rel="stylesheet"
	href="<c:url value="/resources/css/analysis.css" />">
<script type="text/javascript"
	src="<c:url value="/resources/js/application.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/chartwidget.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/mapwidget.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/streamingwidget.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/filewidget.js" />"></script>

</head>

<body>
<div class="container-fluid">
	
	<div id="left_container" class="col-md-7">
		<div id="map_div"></div>

		<div id="statsSettingsWrapper">
			<div id="statsTableWrapper" class="col-md-5">
				<table id="statsTable" title="Analysis Statistics">
					<tr title="Number of received tweets"><td>#Tweets:</td><td id="numTweets"></td></tr>
					<tr title="Number of inferred locations"><td>#Tweets (inferred):</td><td id="numInferred"></td></tr>
					<tr title="Number of tweets with geotag"><td>#Tweets (geotagged):</td><td id="numTagged"></td></tr>
					<tr title="The average sentiment of all processed tweets"><td>Avg Sentiment:</td><td id="avgSentiment"></td></tr>
					<tr title="The maximum distance between any two tweets on the map"><td>Max Distance:</td><td id="maxDist"></td></tr>
					<tr title="The average time from receiving a tweet on the server until it is sent to the client"><td>Avg Analysis Time (ms):</td><td id="avgTime"></td></tr>
				</table>
			</div>

			<div id="settingsWrapper" class="col-md-7">
				<label title="Display tweets as markers on the map" class="radio-inline"><input type="radio" id="markerRadioBtn" name="mapTypeRb" checked="checked">Markers</label>
				<label title="Create a heatmap with the locations of tweets" class="radio-inline"><input type="radio"	id="heatRadioBtn" name="mapTypeRb">Heatmap</label>
				<div class="checkbox" title="Include tweets without any location information in the tables">
					<label><input type="checkbox" id="includeAllTweetsCB"/>Include Tweets w/o Location</label>
				</div>
				<div class="checkbox" title="If no location can be inferred with the default methods, try to resolve the user's home town with a gazetteer service.">
					<label><input type="checkbox" id="fallbackGazetteer"/>Fallback Hometown Lookup</label>
				</div>
				<label>Allowed Sentiment Range</label><input id="sentimentRangeSlider" type="text"/>
			</div>
		</div>
		
		<div id="topTweetsWrapper">
			<div id="topPosTweetsWrapper" class="col-md-6">
				<table id="topPosTweetsTable" class="table table-bordered table-condensed">
					<caption>5 Most Positive Tweets</caption>
					<thead>
						<tr>
							<th style="width: 85%">Message</th>
							<th style="width: 15%">Sentiment</th>
						</tr>
					</thead>
					<tbody>
						<!-- Inserted by JS -->
					</tbody>
				</table>
			</div>
			<div id="topNegTweetsWrapper" class="col-md-6">
				<table id="topNegTweetsTable" class="table table-bordered table-condensed">
					<caption>5 Most Negative Tweets</caption>	
					<thead>
						<tr>
							<th style="width: 85%">Message</th>
							<th style="width: 15%">Sentiment</th>
						</tr>
					</thead>
					<tbody>
						<!-- Inserted by JS -->
					</tbody>
				</table>
			</div>
		</div>

		<div id="tweetTableWrapper">
			<table id="tweetTable" class="table table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 85%">Message</th>
						<th style="width: 15%">Sentiment</th>
					</tr>
				</thead>
				<tbody>
					 <!-- Inserted by JS -->
				</tbody>
			</table>
		</div>
		
	</div>

	<div id="right_container" class="col-md-5">
		<div id="chart_container" class="chart_container">
			<div id="chart_div" class="chart"></div>
			<div id="tpm_chart_div" class="chart"></div>
		</div>

		<div id="content">
			<ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
				<li class="active"><a data-target="#liveTab" data-toggle="tab">Live
						Feed</a></li>
				<li><a data-target="#fileTab" data-toggle="tab">File</a></li>
			</ul>
			<div id="my-tab-content" class="tab-content">
				<div class="tab-pane active" id="liveTab">
					<!-- Live Stream Controls -->
					<form:form>
						<input id="filterTerms" name="filterTerms" title="If only hashtags are entered, the tags will also be used as filterterms."
							placeholder="Enter Filterterms... (e.g. kcl, #london)" class="form-control">
						<input type="button" value="Start" class="btn btn-success" id="startStreaming"/>
						<input type="button" value="Stop" class="btn btn-default" id="stopStreaming"/>
					</form:form>
				</div>
				<div class="tab-pane" id="fileTab">
					<!-- File Controls -->
					<div id="fileTableWrapper">
						<table id="fileTable" class="table table-bordered table-condensed table-hover">
							<thead>
								<tr>
									<th></th>
									<th>Filename</th>
									<th>Size</th>
								</tr>
							</thead>
							<tbody>
								<!-- Inserted by JS -->
							</tbody>
						</table>
					</div>

					<div>
						<input type="button" value="Start" class="btn btn-success" id="startFileAnalysis"/>
						<input type="button" value="Stop" class="btn btn-default" id="stopFileAnalysis"/>
					</div>

					<div id="fileUploadContainer">
						<div class="input-group">
							<span class="input-group-btn">
								<span class="btn btn-default btn-file">
						    		Browse&hellip; <input id="fileupload" type="file" name="files[]" accept="application/json" data-url="/Twintiment/upload">
								</span>
							</span>
							<input id="selectedFile" type="text" class="form-control" readonly>
						</div>
						
						<button class="btn btn-default" id="startUpload">Upload</button>
						<button class="btn btn-default" id="cancelUpload">Cancel</button>
						<div class="progress">
							<div class="progress-bar" id="progress" role="progressbar" aria-valuemin="0" aria-valuemax="100"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
</body>
</html>
