jQuery(function($) {
	function fmtDate (date) {
	    return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	}
	var now=new Date();
	var aday=24 * 60 * 60 *1000;
	Handlebars.registerHelper("showday", function(idx) {
		return fmtDate(new Date(now.valueOf()-idx*aday));
	});
	
	$.get("api/web/sys/verinfo.json").success(function(json) {
		var html = $("#ver-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#ver-tpl").after(tpl(json.d));
	});
	$.get("api/web/sys/moneyinfo.json").success(function(json) {
		var data=json.d;
		if(data&&data.KEY_WEEK){
			var weekaliavg=0;
			var weekwxavg=0;
			for(var i=0;i<data.KEY_WEEK.length;i++){
				var item=data.KEY_WEEK[i];
				weekaliavg+=item.alimoney;
				weekwxavg+=item.wxmoney;
			}
			data.KEY_WEEK_ALI_AVG=Math.floor(weekaliavg/data.KEY_WEEK.length);
			data.KEY_WEEK_WX_AVG=Math.floor(weekwxavg/data.KEY_WEEK.length);
//			data.KEY_DAY_CNT=data.KEY_WEEK.length;
			var html = $("#money-tpl").html();
			var tpl = Handlebars.compile(html);
			$("#money-tpl").after(tpl(data));
		}
	});
});