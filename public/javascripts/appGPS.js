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
var nbPoints = 0;

var watcher;
var positions = new Array();

var timerCompteurs;

// TODO gérer avec Play ?
var server = '/seance';
var serverRedirection = '/profil';

/*
var posTest = [
	{
	coords: {
		longitude: 5.7673325915,
		latitude: 45.1933331749,
		altitude: 267.695526123,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76732475552,
		latitude: 45.1933219616,
		altitude: 267.683227539,
		accuracy: 17
		}
	},
	{
	coords: {
		longitude: 5.76732315617,
		latitude: 45.1933328877,
		altitude: 267.683227539,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76731690048,
		latitude: 45.1933308574,
		altitude: 267.683227539,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76730792179,
		latitude: 45.1933306173,
		altitude: 267.683227539,
		accuracy: 14
		}
	},
	{
	coords: {
		longitude: 5.76730107696,
		latitude: 45.1933313125,
		altitude: 267.683227539,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76729232122,
		latitude: 45.1933306391,
		altitude: 267.683227539,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76728812959,
		latitude: 45.1933370844,
		altitude: 267.683227539,
		accuracy: 15
		}
	},
	{
	coords: {
		longitude: 5.76728082687,
		latitude: 45.1933431642,
		altitude: 267.683227539,
		accuracy: 14
		}
	},
	{
	coords: {
		longitude: 5.72788468107,
		latitude: 45.1891268139,
		altitude: 267.683227539,
		accuracy: 29
		}
	},
	{
	coords: {
		longitude: 5.72797408593,
		latitude: 45.1890230576,
		altitude: 267.683227539,
		accuracy: 23
		}
	},
	{
	coords: {
		longitude: 5.7279589937,
		latitude: 45.1890282945,
		altitude: 244.710586548,
		accuracy: 22
		}
	},
	{
	coords: {
		longitude: 5.72793678817,
		latitude: 45.1890407695,
		altitude: 242.422225952,
		accuracy: 23
		}
	}
]
posTest.forEach(
	function(location) {
		updateMap(
			location.coords.longitude,
			location.coords.latitude,
			location.coords.accuracy
		);

		addPosition(location.coords);
		updateCompteurs();
	}
)
stopGPS();
*/

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

	timerCompteurs = setInterval(updateCompteurs, 1000);

	watcher = navigator.geolocation.watchPosition(
		function(location) {
			nbPoints++;

			updateMap(
				location.coords.longitude,
				location.coords.latitude,
				location.coords.accuracy
			);

			if(nbPoints % 2 == 0) {
				addPosition(location.coords);
			}
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
	$('#distance').html(distKm + " km");

	var diff = dateDiff(startDate, new Date());
	$('#duree').html(diff.min + ":" + diff.sec);

	if(distKm >= objectifDistance && distKm > 0) {
		$('#distance').addClass('ok');
	}
	if(diff.min >= objectifDuree && diff.min > 0) {
		$('#duree').addClass('ok');
	}
}

function addPosition(pos) {
	if(pos.accuracy < 50) {
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
	clearInterval(timerCompteurs);

	button.attr('data-state', 'stop');
	button.html('Commencer');
	button.removeClass('btn-attention');

	window.navigator.geolocation.clearWatch(watcher);

	sendPositionsToServer();
}

function sendPositionsToServer() {
	var distKm = Math.round(distanceParcourue);
	var diff = dateDiff(startDate, new Date());

	$.ajax({
		type: 'POST',
		url: server,
		data: {
			positions : JSON.stringify(positions),
			idQuete : idQuete
		},
		success: function(data){
			if(data.status == "ok") {
				alert("Vous avez gagné " + data.gainEndurance + " XP en endurance et " + data.gainVitesse + " XP en vitesse !");
			} else {
				alert("Vous n'avez pas assez couru !");
			}

			document.location.href = serverRedirection;
		}
	});
}

$(document).ready(function() {
	objectifDistance = parseInt($('#objDistance').html());
	if(isNaN(objectifDistance)) {
		objectifDistance = 0;
	}
	objectifDuree = parseInt($('#objDuree').html());
	if(isNaN(objectifDuree)) {
		objectifDuree = 0;
	}
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
