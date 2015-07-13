$(document).ready(function() {

/* Leaflet Map */
mapWidget = (function() {	
	var map = L.map('map_div').setView([51.505, -0.09], 5)
	
	//Add tile layer
	L.tileLayer('http://{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg', { /* TODO Change to MapBox  */
		attribution: "Tiles and Geocoding Courtesy of <a href='http://www.mapquest.com/' target='_blank'>MapQuest</a>"
		+ "<img src='http://developer.mapquest.com/content/osm/mq_logo.png'",
		maxZoom: 18,
		subdomains: ["otile1","otile2","otile3","otile4"]
	}).addTo(map);
	
	//Configure and add layer to draw rectangles
	var drawnItems = new L.FeatureGroup();
	map.addLayer(drawnItems);

	var drawControl = new L.Control.Draw({
		draw: {
			polyline: false,
			polygon: false,
			circle: false,
			marker: false
		},
		edit: {
			featureGroup: drawnItems
		}
	});
	map.addControl(drawControl);
	map.on('draw:created', function (e) {
	    var type = e.layerType,
	        layer = e.layer;
	    drawnItems.addLayer(layer);
	});

	//Configure and add the layer for the heatmap
	var heatLayer = L.heatLayer([], 
		{
// 			maxZoom : 12, //TODO
			minOpacity: .5
		}).addTo(map);

	//Add MarkerClusterLayer
	var markerLayer = new L.MarkerClusterGroup();
	map.addLayer(markerLayer);
	
	//Public functions
	return { 
		addHeatPoint : function(latLng) {
			heatLayer.addLatLng(latLng);
		},
		addMarker : function(marker) {
			markerLayer.addLayer(marker); },
		getDrawnSquares : function() {
			var square_coords = [];
			for(var i in drawnItems._layers) {
				square_coords.push(drawnItems._layers[i]._latlngs);
			}
			return square_coords;
		},
		map	: map
	}

}());

}); //document ready