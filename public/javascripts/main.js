$(document).ready(function(){
	$("a[rel^='prettyPhoto']").prettyPhoto({
		deeplinking: false,
		social_tools: ''
	});

	$(".delete").click(function() {
		if(!confirm('Etes-vous sûr certain ?')) {
			return false;
		}
	});
});
