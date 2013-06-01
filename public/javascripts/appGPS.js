var button;

var map;
var mapnik;
var markers;

var startDate;

var watcher;
var positions = new Array();

var server = './validerParcours';

function initGPS() {
	map = new OpenLayers.Map('mapquest');
	mapnik = new OpenLayers.Layer.OSM();
	markers = new OpenLayers.Layer.Markers("Markers");

	map.addLayer(mapnik);
	map.addLayer(markers);

	map.zoomToMaxExtent();
}

function startGPS() {
	button.attr('data-state', 'start');
	button.html('Arrêter');

	var acc = 100000;
	var lat;
	var lon;
	startDate = new Date().getTime();

	watcher = navigator.geolocation.watchPosition(
		function(location) {
			updateMap(
				location.coords.longitude,
				location.coords.latitude,
				location.coords.accuracy
			);

			addPosition(location.coords);
			//updateSpeed(location.coords.speed);
		},

		function(error) {
			stopGPS();
			/*
			switch(error.code) {
			case error.PERMISSION_DENIED:
				$('#err').html('Vous avez interdit l\'accès à votre localisation. Veuillez l\'autoriser pour bénéficier du service.');
				break;
			case error.POSITION_UNAVAILABLE:
				$('#err').html('Votre emplacement n\'a pu être déterminé.');
				break;
			case error.TIMEOUT:
				$('#err').html('Le service ne répond pas.');
				break;
			}
			*/	
		},

		{timeout:30000, enableHighAccuracy:true, maximumAge:0}
	);
}

function updateMap(lon, lat, acc) {
	var lonLat = new OpenLayers.LonLat(lon, lat).transform(
		new OpenLayers.Projection("EPSG:4326"),
		map.getProjectionObject()
	);
	markers.addMarker(new OpenLayers.Marker(lonLat));
	map.setCenter(lonLat, 18);
}

function addPosition(pos) {
	positions.push({
		'lon' : pos.longitude,
		'lat' : pos.latitude,
		'alt' : pos.altitude,
		'acc' : pos.accuracy,
		'time': new Date().toISOString()
	});
}

function stopGPS() {
	button.attr('data-state', 'stop');
	button.html('Commencer');

	window.navigator.geolocation.clearWatch(watcher);

	//sendPositionsToServer();
}
/*
function sendPositionsToServer() {
	$.ajax({
		type: 'POST',
		url: server,
		data: { positions : JSON.stringify(positions) },
		success: function(data){
			
		}
	});
}

function updateSpeed(speed) {
	if(!isNaN(speed)) {
		speed = speed * 3.6;
		$('#speed').html(speed + ' km/h');
	}
}

function stopGPS() {
	button.attr('data-state', 'stop');
	button.val('Start');

	window.navigator.geolocation.clearWatch(watcher);

	sendPositionsToServer();
}
*/

$(document).ready(function() {
	initGPS();

	button = $('#startQuest');

	button.click(function() {
		var state = $(this).attr('data-state');
		if(state == 'stop') {
			startGPS();
		} else {
			stopGPS();
		}
	});
});
