jQuery(function($) {
	Handlebars.registerHelper("showday", function(all,idx) {
		if(idx==0){
			return "今天";
		}else if(idx==1){
			return "昨天";
		}else if(idx==2){
			return "前天";
		}else if(idx==3){
			return "三天前";
		}else if(idx==4){
			return "四天前";
		}else if(idx==5){
			return "五天前";
		}else if(idx==6){
			return "六天前";
		}
	});
	
	$.get("api/web/sys/verinfo.json").success(function(json) {
		var html = $("#ver-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#ver-tpl").after(tpl(json.d));
	});
	$.get("api/web/sys/moneyinfo.json").success(function(json) {
		var data=json.d;
		if(data&&data.KEY_STACK){
			data.KEY_STACK.reverse();
			var weekaliavg=0;
			var weekwxavg=0;
			for(var i=0;i<data.KEY_STACK.length;i++){
				var item=data.KEY_STACK[i];
				weekaliavg+=item.alimoney;
				weekwxavg+=item.wxmoney;
			}
			data.KEY_WEEK_ALI_AVG=Math.floor(weekaliavg/data.KEY_STACK.length);
			data.KEY_WEEK_WX_AVG=Math.floor(weekwxavg/data.KEY_STACK.length);
			data.KEY_DAY_CNT=data.KEY_STACK.length;
			var html = $("#money-tpl").html();
			var tpl = Handlebars.compile(html);
			$("#money-tpl").after(tpl(data));
		}
	});
});