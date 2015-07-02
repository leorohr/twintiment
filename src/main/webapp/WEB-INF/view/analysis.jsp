<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
<title>Twintiment</title>

<script type="text/javascript"
	src="<c:url value="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.0.0-beta.9/sockjs.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js" />"></script>

<!-- jQuery UI -->
<link rel="stylesheet"
	href="<c:url value="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css" />">
<script type="text/javascript"
	src="<c:url value="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js" />"></script>


<!--  jQuery Fileupload -->
<script type="text/javascript"
	src="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/blueimp-file-upload/9.5.7/jquery.fileupload.min.js" />"></script>

<!-- Bootstrap -->
<link rel="stylesheet"
	href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"/>" />
<link rel="stylesheet"
	href="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css"/>" />
<script
	src="<c:url value="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"/> "></script>

<!-- Leaflet  -->
<link rel="stylesheet"
	href="<c:url value="http://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.css" />">
<script
	src="http://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.js"></script>

<!-- Highcharts -->
<script src="http://code.highcharts.com/stock/highstock.js"></script>



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
	
	<div id="right_container" class="right_container">	
		<div id="chart_container" class="chart_container">
			<div id="chart_div" class="chart"></div>
			<div id="tpm_chart_div" class="chart"></div>
		</div>
		<div id="stream_tab_container" class="stream_tab_container">
	</div>
		<div id="content">
			<ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
				<li class="active"><a href="#liveTab" data-toggle="tab">Live
						Feed</a></li>
				<li><a href="#fileTab" data-toggle="tab">File</a></li>
			</ul>
			<div id="my-tab-content" class="tab-content">
				<div class="tab-pane active" id="liveTab">
					<!-- Live Stream Controls -->
					<form:form>
						<input id="filterTerms" name="filterTerms"
							placeholder="Enter Filterterms...">
						<input id="startStreaming" type="button" value="Start" />
						<input id="stopStreaming" type="button" value="Stop" />
					</form:form>
				</div>
				<div class="tab-pane" id="fileTab">
					<!-- File Controls -->
					<div class="fileTableWrapper">
						<table id="fileTable" class="fileTable">
							<thead>
								<tr>
									<th>Filename</th>
									<th>Size</th>
								</tr>
							</thead>
							<tbody>
								<!-- Insterted by JS -->
							</tbody>
						</table>
					</div>

					<div>
						<input id="startFileAnalysis" type="button" value="Start Analysis" />
						<input id="stopFileAnalysis" type="button" value="Stop Analysis" />
					</div>

					<div id="fileUploadContainer">
						<input id="fileupload" type="file" data-url="/Twintiment/upload"
							accept="application/json" /> <input id="startUpload"
							type="button" value="Upload" /> <input id="cancelUpload"
							type="button" value="Cancel" />
						<div id="progressBar"></div>
						<label id="bitrateLbl"></label>
					</div>
				</div>
			</div>
		</div>
		<!-- content div -->

	</div>
	<!-- container -->



	<div id="left_container" class="left_container">
		<div id="map_div" class="map"></div>
		<div class="tweetTableWrapper">
			<table id="tweetTable" class="tweetTable" border="1">
				<thead>
					<tr>
						<th style="width: 85%">Message</th>
						<th style="width: 15%">Score</th>
					</tr>
				</thead>
				<tbody>
					<!-- Insterted by JS -->
				</tbody>
			</table>
		</div>
	</div>

</body>
</html>
