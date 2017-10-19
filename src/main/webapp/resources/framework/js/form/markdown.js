ns.readyEvents.push(function(){
	ns.form.markdown = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").append("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/bootstrap-markdown/css/bootstrap-markdown.min.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/bootstrap-markdown/js/bootstrap-markdown.js\"></script>");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/bootstrap-markdown/js/bootstrap-markdown.zh.js\"></script>");
			}
	}
	try{
		ns.form.markdown.init();
	}catch(e){
		alert("请引入markdown组件！");
	}
});