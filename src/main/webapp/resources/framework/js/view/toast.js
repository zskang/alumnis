var Toast = {
		success : function(msg, title, fn){
			if(!title)
				title = "提示";
			if(fn){
				toastr.options.onclick = fn;
			}
			if(typeof(title) == "function"){
				fn = title;
				title = "提示";
				toastr.options.onclick = fn;
			}
			toastr["success"](msg);
		},
		info : function(msg, title, fn){
			if(!title)
				title = "提示";
			if(fn){
				toastr.options.onclick = fn;
			}
			if(typeof(title) == "function"){
				fn = title;
				title = "提示";
				toastr.options.onclick = fn;
			}
			toastr["info"](msg);
		},
		warning : function(msg, title, fn){
			if(!title)
				title = "警告";
			if(fn){
				toastr.options.onclick = fn;
			}
			if(typeof(title) == "function"){
				fn = title;
				title = "警告";
				toastr.options.onclick = fn;
			}
			toastr["warning"](msg);
		},
		error : function(msg, title, fn){
			if(!title)
				title = "错误";
			if(fn){
				toastr.options.onclick = fn;
			}
			if(typeof(title) == "function"){
				fn = title;
				title = "错误";
				toastr.options.onclick = fn;
			}
			toastr["error"](msg);
		},
		init : function(){
			//引入必要的样式+JS文件
			$("head").prepend("<link rel=\"stylesheet\" href=\""+ns.getBasePath()+"/framework/plugins/toastr/toastr.min.css\">");
			$("body").append("<script src=\""+ns.getBasePath()+"/framework/plugins/toastr/toastr.min.js\"></script>");
			toastr.options = {
				closeButton : false,
				debug : false,
				positionClass : "toast-top-right",
				onclick : null,
				showDuration : 300,
				hideDuration : 300,
				timeOut : 3000,
				extendedTimeOut : 1000,
				showEasing : 'swing',
				hideEasing : 'linear',
				showMethod : 'show',
				hideMethod : 'hide'
			};
		}
}
ns.readyEvents.push(function(){
	try{
		ns.tip.toast = Toast;
		ns.tip.toast.init();
	}catch(e){
		alert("请引入plugins/toastr/toastr.min.js");
	}
});