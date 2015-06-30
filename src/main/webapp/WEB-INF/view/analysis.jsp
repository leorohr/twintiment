<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
<title>Twintiment</title>

<link rel="stylesheet"
	href="<c:url value="/resources/css/analysis.css" />">
<script type="text/javascript"
	src="<c:url value="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.0.0-beta.9/sockjs.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js" />"></script>

<!-- Leaflet  -->
<link rel="stylesheet"
	href="<c:url value="http://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.css" />">
<script
	src="http://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.js"></script>

<!-- Highcharts -->
<script src="http://code.highcharts.com/stock/highstock.js"></script>

<script type="text/javascript"
	src="<c:url value="/resources/js/application.js" />"></script>



</head>

<body>
	<div id="map_div" class="map"></div>
	<div id="chart_div" class="chart"></div>
	
	<form:form>
		<input id="filterTerms" name="filterTerms"
			placeholder="Enter Filterterms...">
		<input id="startStreaming" type="button" name="start" value="Start" />
		<input id="stopStreaming" type="button" name="stop" value="Stop" />
	</form:form>

	<div class="tableWrapper">
		<table id="tweetTable" border="1" style="width: 100%">
			<thead>
				<tr>
					<th style="width: 85%">Message</th>
					<th style="width: 15%">Score</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>

</body>
</html>
