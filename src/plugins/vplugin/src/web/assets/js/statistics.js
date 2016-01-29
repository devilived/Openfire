jQuery(function($) {
	$.get("api/web/sys/verinfo.json").success(function(json) {
		var html = $("#ver-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#ver-tpl").after(tpl(json.d));
	});
});