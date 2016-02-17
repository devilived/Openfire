jQuery(function($) {
    Handlebars.registerHelper("ifnull", function(value) {
    	if (value == 0||value) {
			return value;
		}
        return " ";
    });

    $.get("api/web/setting/list.json").success(function(json){
    	 var html = $("#prop-tpl").html();
         var tpl = Handlebars.compile(html);
         $("#prop-tpl").after(tpl(json.d));
         
         
         $(".am-btn-toolbar .am-text-secondary").click(function() {
             $tr = $(this).closest("tr");
             var key = $tr.find("td:eq(0)").html();
             var value = $tr.find("td:eq(1)").html();
             $("#propEditLayer .am-modal-hd").html("属性:" + key);
             $("#propEditLayer .am-modal-bd .am-modal-prompt-input").val(value);
             $("#propEditLayer").modal({
                 relatedTarget: this,
                 onConfirm: function(e) {
                     $.getJSON("api/web/setting/update.json?key=" + key + "&value=" + e.data,function(json) {
                         if (json.c == 0) {
                         	$tr.find("td:eq(1)").html(e.data);
                             alert("保存成功 !");
                         } else {
                             alert("保存失败,code:" + json.c + ",msg:" + json.m);
                         }
                     });
                 }
             });
         });
         
         $(".am-btn-toolbar .am-text-danger").click(function() {
             $tr = $(this).closest("tr");
             var key = $tr.find("td:eq(0)").html();
             var value = $tr.find("td:eq(1)").html();
             $.getJSON("api/web/setting/delete.json?key=" + key,function(json) {
                 if (json.c == 0) {
                 	$tr.remove();
                     alert("删除成功 !");
                 } else {
                     alert("删除失败,code:" + json.c);
                 }
             });
         });
    });
});