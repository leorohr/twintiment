<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	

<!-- Highcharts -->
<script
	src="<c:url value="/resources/bower_components/highcharts/highcharts.js"/>"></script>


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
			<div id="statsTableWrapper" class="col-md-6">
				<table id="statsTable">
					<tr><td>#Tweets:</td><td id="numTweets"></td></tr>
					<tr><td>#Tweets (inferred):</td><td id="numInferred"></td></tr>
					<tr><td>#Tweets (geotagged):</td><td id="numTagged"></td></tr>
					<tr><td>Avg Sentiment:</td><td id="avgSentiment"></td></tr>
					<tr><td>Max Distance:</td><td id="maxDist"></td></tr>
				</table>
			</div>

			<div id="settingsWrapper" class="col-md-6">
				<label class="radio-inline"><input type="radio" id="markerRadioBtn" name="mapTypeRb" checked="checked">Markers</label>
				<label class="radio-inline"><input type="radio"	id="heatRadioBtn" name="mapTypeRb">Heatmap</label>
				<div class="checkbox">
					<label><input type="checkbox" id="inclAllTweetsCB"/>Include All Tweets</label>
				</div>
			</div>
		</div>
		
		<div id="topTweetsWrapper">
			<div id="topPosTweetsWrapper" class="col-md-6">
				<table id="topPosTweetsTable" class="table table-bordered table-condensed" border="1">
					<caption>5 Most Positive Tweets</caption>
					<thead>
						<tr>
							<th style="width: 85%">Message</th>
							<th style="width: 15%">Score</th>
						</tr>
					</thead>
					<tbody>
						<!-- Inserted by JS -->
					</tbody>
				</table>
			</div>
			<div id="topNegTweetsWrapper" class="col-md-6">
				<table id="topNegTweetsTable" class="table table-bordered table-condensed" border="1">
					<caption>5 Most Negative Tweets</caption>	
					<thead>
						<tr>
							<th style="width: 85%">Message</th>
							<th style="width: 15%">Score</th>
						</tr>
					</thead>
					<tbody>
						<!-- Inserted by JS -->
					</tbody>
				</table>
			</div>
		</div>
	


		

		<div id="tweetTableWrapper">
			<table id="tweetTable" class="table table-bordered table-condensed" border="1">
				<thead>
					<tr>
						<th style="width: 85%">Message</th>
						<th style="width: 15%">Score</th>
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
						<input id="filterTerms" name="filterTerms"
							placeholder="Enter Filterterms..." class="form-control">
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
						<input type="button" value="Start Analysis" class="btn btn-default" id="startFileAnalysis"/>
						<input type="button" value="Stop Analysis" class="btn btn-default" id="stopFileAnalysis"/>
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
						<div class="progress" style="display: none;">
							<div class="progress-bar" id="progress",
								aria-valuemin="0" aria-valuemax="100"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
</body>
</html>
