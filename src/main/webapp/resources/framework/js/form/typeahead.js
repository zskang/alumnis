/*
 *  文本框自动补全组件 v1.0.0
 *  依赖 plugins/typeahead目录下所有文件
 */
ns.readyEvents.push(function(){
	ns.form.typeahead = {
		import : function(){
			$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/typeahead/typeahead.js-bootstrap.css\">");
			$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/typeahead/typeahead.bundle.js\"></script>");
		},
		init : function(element){
			var url = element.attr("data-url");
			if(url){
				element.typeahead(null,{
					source: function(query,process){
						url = element.attr("data-url");
						jQuery.post(url,{query:query},function(data){
							process(data);
						});
					}
				});
				var width = element.attr("data-width");//宽度
				if(width)
					element.parent().find(".tt-dropdown-menu").css("minWidth",width)
			}
		}
	};
	ns.form.typeahead.import();
	$.each($(".typeahead"), function(i,element){
		element = $(element);
		ns.form.typeahead.init(element);
	})
});