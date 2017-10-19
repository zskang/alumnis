ns.readyEvents.push(function(){
	ns.view.treeTable = {
			init : function(){
				//引入必要的样式+JS文件
				$("head").append("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/treetable/jquery.treetable.css\">");
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/treetable/jquery.treetable.js\"></script>");
			}
	}
	try{
		ns.view.treeTable.init();
	}catch(e){
		alert("请引入TreeTable组件！");
	}
});
