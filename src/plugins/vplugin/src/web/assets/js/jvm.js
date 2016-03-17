jQuery(function($) {
	function fmtDate(date) {
		return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
	}
	var M = 1024 * 1024;
	Handlebars.registerHelper("showM", function(v) {
		if (v&&v>0) {
			return Math.floor(v / M);
		} else {
			return v;
		}
	});
	$.get("api/web/sys/cache/list.json").success(function(json) {
		var html = $("#cache-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#cache-tpl").after(tpl(json.d));
	});
	$.get("api/web/sys/jvm.json").success(function(json) {
		var html = $("#mem-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#mem-tpl").after(tpl(json.d));
	});
	
	$("#clearCache").click(function(){
		$.get("api/web/sys/cache/clear.json").success(function(json) {
			if(json.c==0){
				alert("清空缓存成功");
				$.get("api/web/sys/cache/list.json").success(function(json) {
					$(".cache-row").remove();
					var html = $("#cache-tpl").html();
					var tpl = Handlebars.compile(html);
					$("#cache-tpl").after(tpl(json.d));
				});
			}
		});
	});
});