ns.readyEvents.push(function(){
	ns.view.tree = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").append("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/jstree/themes/default/style.min.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/jstree/jstree.min.js\"></script>");
			}
	}
	try{
		ns.view.tree.init();
	}catch(e){
		alert("请引入JSTree组件！");
	}
});
