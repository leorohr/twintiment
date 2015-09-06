$(document).ready(function() {

/* Leaflet Map */
mapWidget = (function() {	
	var map = L.map('map_div').setView([51.505, -0.09], 5);
	
	//Add tile layer
	L.tileLayer('http://{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg', {
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
	    var layer = e.layer;
	    drawnItems.addLayer(layer);
	});

	//Configure and add the layer for the heatmap
	var heatLayer = L.heatLayer([], 
		{
			minOpacity: .5
		}).addTo(map);

	//Add MarkerClusterLayer
	var markerLayer = L.markerClusterGroup({
 			animateAddingMarkers: true,
 			iconCreateFunction: function (cluster) {
 				var markers = cluster.getAllChildMarkers();
 				var sentiment = 0;
 				for (var i = 0; i < markers.length; i++) {
 					sentiment += markers[i].options.sentiment;
 				}
 				sentiment /= markers.length;
 				var c = sentiment > 0 ? 'marker-cluster-small' :
 						sentiment < 0 ? 'marker-cluster-large' : 
 										'marker-cluster-medium';

 				var icon = new L.DivIcon({
 					html: "<div><span>"	+ markers.length + "</span></div>",
 					className: 'marker-cluster ' + c,
 					iconSize: new L.Point(40, 40)
 				});
				
 				return icon;
 			}
			});

	map.addLayer(markerLayer);

	//Create different markers
	var redIcon = L.icon({
		iconUrl: '/Twintiment/resources/img/marker-icon-red.png'
	});
	var greenIcon = new L.icon({
		iconUrl: '/Twintiment/resources/img/marker-icon-green.png'
	});
	
	//Public functions
	return { 
		addHeatPoint : function(latLng) {
			heatLayer.addLatLng(latLng);
		},
		addMarker : function(coords, sentiment, message) {
			var marker = L.marker(coords,
 				{
 				icon :
 					sentiment > 0 ? greenIcon :
 					sentiment < 0 ? redIcon :
 					new L.Icon.Default(),
 				sentiment: sentiment	
 				});
			marker.bindPopup(message+"<br>Sentiment: "+sentiment);
			markerLayer.addLayer(marker); 
		},
		getDrawnSquares : function() {
			if($.isEmptyObject(drawnItems._layers))
				return null;
			var square_coords = [];
			for(var i in drawnItems._layers) {
				square_coords.push(drawnItems._layers[i]._latlngs);
			}
			return square_coords;
		},
		map	: map
	};

}());

}); //document ready