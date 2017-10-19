ns.readyEvents.push(function(){
	ns.iCheck = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/icheck/skins/all.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/icheck/icheck.js\"></script>");
				ns.form.initCheckbox = function(obj){
					if ($.isFunction($.fn.iCheck)) {
		                $(".icheck").each(function(){
		                	//配置skin属性设置按钮皮肤
		                	var skin = $(this).attr("skin");
		                	if(skin && skin.length>0){
		                		$(this).iCheck({
		                			checkboxClass: 'icheckbox_'+skin,
		                			radioClass: 'iradio_'+skin
		                		});
		                	}else{
		                		$(this).iCheck();
		                	}
		                });
		            }
				}
				ns.form.initCheckbox();
				//绑定全选事件，全选按钮上配置icheck-all属性，属性值为特定的属性名，所有拥有此属性名的icheck将与此icheck同步状态
				$(".icheck[check-all]").on('ifChanged', function(event){
					var item = $(this).attr("check-all");
					if(item){
						if($(this).is(":checked"))
							$("input["+item+"]").iCheck("check");
						else
							$("input["+item+"]").iCheck("uncheck");
					}
		        });
			}
	}
	try{
		ns.iCheck.init();
	}catch(e){
		alert("请引入icheck组件！");
	}
});