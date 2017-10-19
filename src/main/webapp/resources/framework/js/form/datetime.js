ns.readyEvents.push(function(){
	ns.form.dateTimePicker = {
			init : function(){
				if (!$.isFunction($.fn.datetimepicker)) {
					//引入必要的样式+JS文件
					$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/datetimepicker/css/bootstrap-datetimepicker.min.css\">");
					$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/datetimepicker/js/bootstrap-datetimepicker.min.js\"></script>");
					$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js\"></script>");
				}
				ns.form.initDateTimePicker = function(obj){//初始化控件
					if ($.isFunction($.fn.datetimepicker)) {
		                if (obj)
		                    $(obj).datetimepicker();
		                else
		                    $(".datetime").datetimepicker({
		                    	format : "yyyy-mm-dd HH:mm:ss",
		                    	autoclose : true,
		                    	todayBtn : true,
		                    	todayHighlight : true,
		                    	language : "zh-CN"
		                    });
		            }
				}
				ns.form.initDateTimePicker();
			}
	}
	try{
		ns.form.dateTimePicker.init();
	}catch(e){
		alert("请引入Bootstrap.DateTimePicker组件！");
	}
});