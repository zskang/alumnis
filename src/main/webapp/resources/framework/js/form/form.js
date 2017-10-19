ns.readyEvents.push(function(){
	var form = function() {
		return {
			init : function() {
				//引入jquery.form
				$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/jquery.form.js\"></script>");
				try{
					//开关
					var elems = Array.prototype.slice.call(document.querySelectorAll(".js-switch"));
					var blues = $(".js-switch-blue");
					$.each(blues, function(i,blue){
						var switchery = new Switchery(blue, {
							color : $(".app>.header").css("backgroundColor")//"#1582dc"
						});
					});
					var pinks = $(".js-switch-pink");
					$.each(pinks, function(i,pink){
						var switchery = new Switchery(pink, {
							color : "#ff7791",
							disabled : true
						});
					});
					
					var greens = $(".js-switch-green");
					$.each(greens, function(i,green){
						var switchery = new Switchery(green, {
							color : "#15db81"
						});
					});
					
					var reds = $(".js-switch-red");
					$.each(reds, function(i, red){
						var switchery = new Switchery(red, {
							color : "#FF604F"
						});
					});
					var secondarys = $(".js-switch-secondary");
					$.each(secondarys, function(i,secondary){
						var switchery = new Switchery(secondary, {
							color : "#FFB244",
							secondaryColor : "#ff8787"
						});
					});
					if($('input[maxlength]').length > 0){
						ns.importJs(ns.getBasePath()+"/framework/plugins/boostrap.maxlength.js");
						$('input[maxlength]').maxlength();
					}
				}catch(e){
					
				}
			}
		}
	}();
	ns.form.init = form.init;
	form.init();
});