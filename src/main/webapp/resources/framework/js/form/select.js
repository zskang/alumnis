ns.readyEvents.push(function(){
	ns.form.select = {
			init : function(){
				if (!$.isFunction($.fn.chosen)) {
					//引入必要的样式+JS文件
					$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/chosen/chosen.min.css\">");
					$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/chosen/chosen.jquery.min.js\"></script>");
				}
				ns.form.initChosen = function(obj){
					if ($.isFunction($.fn.chosen)) {
		                if (obj)
		                    $(obj).chosen({
		                        no_results_text: "没有结果匹配",
		                        disable_search_threshold: 10
		                    });
		                else
		                    $(".chosen").chosen({
		                        no_results_text: "没有结果匹配",
		                        disable_search_threshold: 10 //少于10个不显示搜索框
		                    }); 
		            }
				}
				ns.form.initChosen();
			}
	}
	try{
		ns.form.select.init();
	}catch(e){
		alert("请引入chosen组件！"+e);
	}
});