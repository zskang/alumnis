ns.readyEvents.push(function(){
	ns.form.validator = {
			init : function(){
				//引入必要的样式+JS文件
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/parsley/parsley.min.js\"></script>");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/parsley/parsley.zh_cn.js\"></script>");
			}
	}
	try{
		ns.form.validator.init();
	}catch(e){
		alert("请引入parsley组件！");
	}
});