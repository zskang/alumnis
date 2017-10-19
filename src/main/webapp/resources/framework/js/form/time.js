ns.readyEvents.push(function(){
	ns.form.timePicker = {
			init : function(){
				if (!$.isFunction($.fn.datetimepicker)) {
					//引入必要的样式+JS文件
					$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/timepicker/jquery.timepicker.css\">");
					$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/timepicker/jquery.timepicker.min.js\"></script>");
				}
				ns.form.initTimePicker = function(obj){//初始化控件
					if ($.isFunction($.fn.timepicker)) {
		                if (obj)
		                    $(obj).timepicker();
		                else{
		                	$(".time").timepicker({
		                    	lang:{am:"上午", pm:"下午"},
		                    	show2400:true,
		                    	timeFormat:"H:i"
		                    });
		                }
		                    
		            }
				}
				ns.form.initTimePicker();
			}
	}
	try{
		ns.form.timePicker.init();
	}catch(e){
		alert("请引入Bootstrap.DateTimePicker组件！");
	}
});