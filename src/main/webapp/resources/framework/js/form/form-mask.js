ns.readyEvents.push(function(){
	ns.form.formMasks = function() {
		return {
			init : function() {
				//示例：999-9999999、+999 999999、9999-99-99、a*-999-9999
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/jquery.maskedinput.min.js\"></script>");
				$.mask.definitions["~"] = "[+-]";
				$("*[data-mask]").each(function(i,n){
					$(this).mask($(this).attr("data-mask"));
				});
			}
		}
	}();
	ns.form.formMasks.init()
});