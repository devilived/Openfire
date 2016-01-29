jQuery(function($) {
    Handlebars.registerHelper("ifnull", function(value) {
        if (value) {
            return value;
        }
        return " ";
    });
    var PAGE_SIZE = 10;
    
    function getCurrent(){
    	var txt=$(".am-pagination li.am-active").text();
    	return parseInt(txt);
    }

    function refreshData(data) {
        $(".row").remove();
        var html = $("#user-tpl").html();
        var tpl = Handlebars.compile(html);
        $("#user-tpl").after(tpl(data));

        $(".am-btn-toolbar .am-text-secondary").click(function() {
            $tr = $(this).closest("tr");
            var uid = $tr.data("uid");
            var phone = $tr.find("td:eq(2)").html().trim();
            var nick = $tr.find("td:eq(3)").html().trim();
            var lvl = $tr.find("td:eq(5)").html().trim();
            $("#userEditLayer .am-modal-hd").html("用户:" + uid + "/" + phone + "/" + nick);
            $("#userEditLayer .am-modal-bd .am-modal-prompt-input").val(lvl);
            $("#userEditLayer").modal({
                relatedTarget: this,
                onConfirm: function(e) {
                    $.getJSON("api/web/user/update.json?id=" + uid + "&lvl=" + e.data.trim(),function(json) {
                        if (json.c == 0) {
                        	pagination.current=getCurrent();
                            pagination.init();
                            pagination.get(pagination.current);
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
            var uid = $tr.data("uid");
            $.getJSON("api/web/user/delete.json?uid=" + uid,function(json) {
                if (json.c == 0) {
                    pagination.current=getCurrent();
                    pagination.init();
                    pagination.get(pagination.current);
                    alert("删除成功 !");
                } else {
                    alert("删除失败,code:" + json.c);
                }
            });
        });
    }
    // refreshData();
    var pagination = new Pagination({
        wrap: $('.am-pagination'),// 存放分页内容的容器
        count: Math.ceil(USR_CNT / PAGE_SIZE),// 总页数
        // current: page, // 当前的页数（默认为1）
        prevText: '上一页', // prev 按钮的文本内容
        nextText: '下一页', // next 按钮的文本内容
        callback: function(page) { // 每一个页数按钮的回调事件
            // page 为当前点击的页数
        },
        // 会发送 get 请求到 /api/xxx/page/page_number
        // 或者你可以写成 /api/xxx?page= 插件发送的 url 为 /api/xxx?page=page_number
        // page_number 为当前的页数
        ajax: {
            url: 'api/web/user/get.json?page=',// 你可以传入你需要的 queryString
            data: {
                pageSize: PAGE_SIZE,
            },
            success: function(json) {
                // result 成功返回的结果
                refreshData(json.d);
            },
            error: function(error) {
                // error 失败返回的 message
                alert(error);
            }
        }
    });
    if(pagination.ajax){
    	pagination.get(1);
    }
    $("#search").click(function() {
        var acctype = $("[name='acctype']").val();
        var accvalue = $("[name='accvalue']").val();
        var url = '';
        if (acctype == 'uid') {
            url = 'api/web/user/get.json?uid=' + accvalue;
        } else if (acctype == 'phone') {
            url = 'api/web/user/get.json?phone=' + accvalue;
        } else {
            url = 'api/web/user/get.json?page=1&pageSize=20';
        }
        $.getJSON(url, function(json) {
        	pagination.current=1;
            pagination.init();
            pagination.get(pagination.current);
        });
    });
});