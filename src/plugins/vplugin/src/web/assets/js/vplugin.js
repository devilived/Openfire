$(function(){
	$.getJSON("api/test/verinfo.json", function(json){
		 $("#VerInfo").html(JSON.stringify(json));
	});
	
	$("#UrlTestTb button").click(function(){
		$(".return").remove();
		var $this=$(this);
		var $tr=$this.closest("tr");
		var $td=$tr.find("td:first-child");
		var url=$td.data("url");
		if(url){
			$td.find("input[type=text]").each(function(){
				$this=$(this);
				var name= $this.attr('name');
				url=url.replace('{'+name+'}',$this.val());
			});
		}else{
			url=$td.html().trim();
		}
		$.getJSON(url, function(json){
			 $tr.after("<tr class='return'><td colspan='2'>"+JSON.stringify(json)+"</td><tr>");
		});
	});
});