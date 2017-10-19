ns.readyEvents.push(function(){
	ns.form.editable = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").append("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/x-editable/bootstrap-editable.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/x-editable/bootstrap-editable.js\"></script>");
			}
	}
	try{
		ns.form.editable.init();
	}catch(e){
		alert("请引入Bootstrap.x-editable组件！");
	}
});