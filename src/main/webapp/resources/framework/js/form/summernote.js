ns.readyEvents.push(function(){
	ns.form.summernote = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/summernote/summernote.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/summernote/summernote.min.js\"></script>");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/summernote/summernote-zh-CN.js\"></script>");
			}
	}
	try{
		ns.form.summernote.init();
	}catch(e){
		alert("请引入Summernote组件！");
	}
});