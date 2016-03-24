jQuery(function($) {
    Handlebars.registerHelper("ifnull", function(value) {
    	if (value == 0||value) {
			return value;
		}
        return " ";
    });
    $("#search").click(function() {
    	  var querytype = $("[name='querytype']").val();
          var queryvalue = $("[name='queryvalue']").val();
          var url = '';
          if (querytype == 'tno') {
              url = 'api/web/paylog/list.json?tno=' + queryvalue;
          } else if (querytype == 'phone') {
              url = 'api/web/paylog/list.json?phone=' + queryvalue;
          } else {
              url = 'api/web/paylog/list.json?phone=' + queryvalue;
          }
          
        $.get(url).success(function(json) {
            $(".row").remove();
            var html = $("#user-tpl").html();
            var tpl = Handlebars.compile(html);
            $("#user-tpl").after(tpl(json.d));
        });
    });
});