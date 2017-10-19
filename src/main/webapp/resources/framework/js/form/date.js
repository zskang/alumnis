ns.readyEvents.push(function(){
	ns.form.datePicker = {
			init : function(){
				if (!$.isFunction($.fn.datepicker)) {
					//引入必要的样式+JS文件
					$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/datepicker/datepicker.css\">");
					$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/datepicker/bootstrap-datepicker.js\"></script>");
				}
				ns.form.initDatePicker = function(obj){//初始化控件
					if ($.isFunction($.fn.datepicker)) {
		                if (obj)
		                    $(obj).datepicker();
		                else
		                    $(".datepicker").datepicker();
		            }
				}
				ns.form.initDatePicker();
			}
	}
	try{
		ns.form.datePicker.init();
	}catch(e){
		alert("请引入Bootstrap.DatePicker组件！");
	}
});