$(document).ready(function() {

/* Leaflet Map */
mapWidget = (function() {	
	var map = L.map('map_div').setView([51.505, -0.09], 5)
	var heatLayer = L.heatLayer([], 
		{
// 			maxZoom : 12, //TODO
			minOpacity: .5
		}).addTo(map);

	L.tileLayer('http://{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg', { /* TODO Change to MapBox  */
		attribution: "Tiles and Geocoding Courtesy of <a href='http://www.mapquest.com/' target='_blank'>MapQuest</a>"
		+ "<img src='http://developer.mapquest.com/content/osm/mq_logo.png'",
		maxZoom: 18,
		subdomains: ["otile1","otile2","otile3","otile4"]
	}).addTo(map);
	
	var markerLayer = new L.MarkerClusterGroup();
	map.addLayer(markerLayer);
	
	return { //public
		addHeatPoint : function(latLng) {
			heatLayer.addLatLng(latLng);
		},
		addMarker : function(marker) {
			markerLayer.addLayer(marker); },
		map	: map
	}

}());



});