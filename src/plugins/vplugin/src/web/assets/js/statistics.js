jQuery(function($) {
	function fmtDate (date) {
	    return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	}
	
	Handlebars.registerHelper("ifnullZero", function(v) {
		if (!v) {
			return 0;
		}
		return v;
	});
	
	var now=new Date();
	var aday=24 * 60 * 60 *1000;
	Handlebars.registerHelper("showday", function(idx) {
		return fmtDate(new Date(now.valueOf()-idx*aday));
	});
	Handlebars.registerHelper("calint", function(v1, operator, v2) {
		switch (operator) {
		case '+':
			return v1 + v2;
		case '-':
			return v1 - v2;
		case '*':
			return v1 * v2;
		case '/':
			return Math.floor(v1 / v2);
		}
	});
	
	$.get("api/web/sys/verinfo.json").success(function(json) {
		var html = $("#ver-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#ver-tpl").after(tpl(json.d));
	});
	
	$.get("api/web/sys/last10info.json").success(function(json) {
		if(json.c==0){
			json.d=json.d.reverse();
			var html = $("#last10-tpl").html();
			var tpl = Handlebars.compile(html);
			$("#last10-tpl").after(tpl(json.d));
		}
		
	});
	$.get("api/web/sys/hourinfo.json").success(function(json) {
		var html = $("#hourstat-tpl").html();
		var tpl = Handlebars.compile(html);
		$("#hourstat-tpl").after(tpl(json.d));
	});
	$.get("api/web/sys/moneyinfo.json").success(function(json) {
		var data=json.d;
		if(data&&data.KEY_WEEK){
			var weekalisum=0;
			var weekalicnt=0;
			var weekwxsum=0;
			var weekwxcnt=0;
			
			var yearcntsum=0;
			
			for(var i=0;i<data.KEY_WEEK.length;i++){
				var item=data.KEY_WEEK[i];
				if(i>0){
					weekalisum+=item.alimoney;
					weekwxsum+=item.wxmoney;
					
					weekalicnt+=item.alicnt;
					weekwxcnt+=item.wxcnt;
				}
				
				item.money=item.alimoney+item.wxmoney;
				item.cnt=item.alicnt+item.wxcnt;
				
				yearcntsum+=item.yearcnt;
			}
			data.KEY_WEEK_ALI_AVG=Math.floor(weekalisum/7);
			data.KEY_WEEK_ALI_CNT=weekalicnt;
			data.KEY_WEEK_WX_AVG=Math.floor(weekwxsum/7);
			data.KEY_WEEK_WX_CNT=weekwxcnt;
			data.KEY_WEEK_AVG=data.KEY_WEEK_ALI_AVG+data.KEY_WEEK_WX_AVG;
			
			data.KEY_WEEK_CNT=data.KEY_WEEK_ALI_CNT+data.KEY_WEEK_WX_CNT;
			
			data.KEY_WEEK_YEAR_CNT=yearcntsum;
			
//			data.KEY_DAY_CNT=data.KEY_WEEK.length;
			var html = $("#money-tpl").html();
			var tpl = Handlebars.compile(html);
			$("#money-tpl").after(tpl(data));
		}
	});
});