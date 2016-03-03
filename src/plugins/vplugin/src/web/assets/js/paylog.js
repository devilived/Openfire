jQuery(function($) {
    Handlebars.registerHelper("ifnull", function(value) {
    	if (value == 0||value) {
			return value;
		}
        return " ";
    });
    $("#search").click(function() {
        var tradeno = $("[name='tradeno']").val();
        var url = 'api/web/paylog/list.json?tradeno=' + tradeno;;
        $.get(url).success(function(json) {
            $(".row").remove();
            var html = $("#user-tpl").html();
            var tpl = Handlebars.compile(html);
            $("#user-tpl").after(tpl(json.d));
        });
    });
});