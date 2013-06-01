var button;

var map;
var mapnik;
var markers;

var idQuete;
var objectifDistance;
var objectifDuree;

var startDate;
var distanceParcourue = 0;
var dernierePosition = null;

var watcher;
var positions = new Array();

// TODO gérer avec Play ?
var server = '/seance';
var serverRedirection = '/quetes';

function initGPS() {
	map = new OpenLayers.Map('mapquest');
	mapnik = new OpenLayers.Layer.OSM();
	markers = new OpenLayers.Layer.Markers("Markers");

	map.addLayer(mapnik);
	map.addLayer(markers);

	map.zoomToMaxExtent();
}

function startGPS() {
	$('#cancelQuest').hide();
	button.attr('data-state', 'start');
	button.html('Arrêter');
	button.addClass('btn-attention');

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
			updateCompteurs();
		},

		function(error) {
			stopGPS();
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

function updateCompteurs() {
	var distKm = Math.round(distanceParcourue);
	$('#distance').html(Math.round(distanceParcourue) + " km");

	var diff = dateDiff(startDate, new Date());
	$('#duree').html(diff.min + " mn");

	if(distKm >= objectifDistance) {
		$('#distance').addClass('ok');
	}
	if(diff.min >= objectifDuree) {
		$('#duree').addClass('ok');
	}
}

function addPosition(pos) {
	if(pos.accuracy < 25) {
		distanceParcourue += calculerDistance(dernierePosition, pos);
		positions.push({
			'lon' : pos.longitude,
			'lat' : pos.latitude,
			'alt' : pos.altitude,
			'acc' : pos.accuracy,
			'time': new Date().toISOString()
		});
		dernierePosition = pos;
	}
}

function stopGPS() {
	button.attr('data-state', 'stop');
	button.html('Commencer');
	button.removeClass('btn-attention');

	window.navigator.geolocation.clearWatch(watcher);

	sendPositionsToServer();

	//document.location.href = serverRedirection;
}

function sendPositionsToServer() {
	var distKm = Math.round(distanceParcourue);
	var diff = dateDiff(startDate, new Date());

	if(distKm >= objectifDistance && diff.min >= objectifDuree) {
		$.ajax({
			type: 'POST',
			url: server,
			data: {
				positions : JSON.stringify(positions),
				idQuete : idQuete
			},
			success: function(data){
				alert('yeah aaah !');
			}
		});
	}
}

$(document).ready(function() {
	objectifDistance = parseInt($('#objDistance').html());
	objectifDuree = parseInt($('#objDuree').html());
	idQuete = parseInt($('#quete').attr('data-idQuete'));
	if(isNaN(idQuete)) {
		idQuete = 0;
	}

	initGPS();

	button = $('#startQuest');

	button.click(function() {
		var state = $(this).attr('data-state');
		if(state == 'stop') {
			startGPS();
		} else {
			stopGPS();
		}

		return false;
	});
});

// http://www.finalclap.com/faq/88-javascript-difference-date
function dateDiff(date1, date2){
	var diff = {}
	var tmp = date2 - date1;

	tmp = Math.floor(tmp/1000);
	diff.sec = tmp % 60;

	tmp = Math.floor((tmp-diff.sec)/60);
	diff.min = tmp % 60;

	tmp = Math.floor((tmp-diff.min)/60);
	diff.hour = tmp % 24;

	tmp = Math.floor((tmp-diff.hour)/24);
	diff.day = tmp;

	return diff;
}

// http://www.clubic.com/forum/programmation/calcul-de-distance-entre-deux-coordonnees-gps-id178494-page1.html
function calculerDistance(pos1, pos2) {
	if(pos1 == null || pos2 == null) {
		return 0;
	}

	a = Math.PI / 180;
	lat1 = pos1.latitude * a;
	lat2 = pos2.latitude * a;
	lon1 = pos1.longitude * a;
	lon2 = pos2.longitude * a;

	t1 = Math.sin(lat1) * Math.sin(lat2);
	t2 = Math.cos(lat1) * Math.cos(lat2);
	t3 = Math.cos(lon1 - lon2);
	t4 = t2 * t3;
	t5 = t1 + t4;
	rad_dist = Math.atan(-t5/Math.sqrt(-t5 * t5 +1)) + 2 * Math.atan(1);

	return (rad_dist * 3437.74677 * 1.1508) * 1.6093470878864446;
}
