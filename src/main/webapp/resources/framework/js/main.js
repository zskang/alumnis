(function (a) {
    (jQuery.browser = jQuery.browser || {}).mobile =
        /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
        .test(a) ||
        /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i
        .test(a.substr(0, 4))
})(navigator.userAgent || navigator.vendor || window.opera);
Modernizr.addTest("retina", window.devicePixelRatio > 1);
Modernizr.addTest("webkit", navigator.userAgent.match(/AppleWebKit/i));
Modernizr.addTest("ipad", navigator.userAgent.match(/iPad/i));
Modernizr.addTest("iphone", navigator.userAgent.match(/iPhone/i));
Modernizr.addTest("ipod", navigator.userAgent.match(/iPod/i));
Modernizr.addTest("ios", Modernizr.ipad || Modernizr.ipod || Modernizr.iphone);
Modernizr.addTest("android", navigator.userAgent.match(/Android/i));
var ns = function () {
    var searchOpen = false,
        app = $(".app"),
        maxHeight = 0;
 
    function events() {
        $(".offscreen-left, .main-navigation").ontouchstart = function () {};
        FastClick.attach(document.body);
        $(".accordion > dd").hide();
        $(window).on("load", function () {
            $(".pageload").fadeOut("slow");
        });
        $(window).resize(function () {
            equalHeightWidgets();
            if (!$.browser.mobile && !checkBreakout()) {
                $(".no-touch .main-navigation").slimScroll({
                    height: "auto"
                });
                $(".no-touch .slimscroll").slimScroll({
                    height: "auto"
                });
                initFooterFix();
            }
            ns.view.initContentWrap();
            ns.view.initDropdownMenuDirection();
        });
        $(document).mouseup(function () {
            if (searchOpen === true) {
                $(".toggle-search").click()
            }
        });
        $(".toggle-search").mouseup(function () {
            return false
        });
        $(".header-search").mouseup(function () {
            return false
        })
    }
 
    function checkBreakout() {//界面样式检测
        var state = false;
        if (app.hasClass("small-menu") || app.hasClass("fixed-scroll")) {
            state = true;
        }
        return state;
    }
 
    function initAnimationAPI() {//UI动画
        if (!$.browser.mobile && $.fn.appear) {
            $("[data-animation]").appear({interval:100});
            $("[data-animation]").on("appear", function () {
                var elm = $(this),
                    animation = elm.data("animation") || "fadeIn",
                    delay = elm.data("delay") || 0;
                if (!elm.hasClass("done")) {
                    setTimeout(function () {
                        elm.addClass("animated " + animation + " done")
                    }, delay)
                }
            })
        } else {
            $("[data-animation]").each(function () {
                var elm = $(this),
                    animation = elm.data("animation") || "fadeIn";
                if (!elm.hasClass("done")) {
                    elm.addClass("animated " + animation + " done");
                }
            })
        }
    }
 
    function initAnimateProgressBars() {//进度条动画
        if (!$.browser.mobile && $.fn.appear) {
            $(".progress-bar").appear();
            $(".progress-bar").on("appear", function () {
                var elm = $(this),
                    percent = elm.data("percent");
                if (!elm.hasClass("done")) {
                    elm.addClass("done").css("width", Math.ceil(percent) + "%")
                }
            })
        } else {
            $(".progress-bar").each(function () {
                var elm = $(this),
                    percent = elm.data("percent");
                if (!elm.hasClass("done")) {
                    elm.addClass("done").css("width", Math.ceil(percent) + "%")
                }
            })
        }
    }
    function initBrowserFix() {//修复火狐样式
        if (navigator.userAgent.search("Firefox") >= 0) {
            $(".layout > aside, .layout > section").wrapInner(
                '<div class="fffix"/>')
        }
    }
    
    function initFuelUX() {//初始化
        if ($.isFunction($.fn.wizard)) {
            $(".wizard").wizard()
        }
        if ($.isFunction($.fn.pillbox)) {
            $(".pillbox").pillbox()
        }
        if ($.isFunction($.fn.spinner)) {
            $(".spinner").spinner()
        }
    }
 
    function equalHeightWidgets() {//计算高度
        $(".equal-height-widgets").each(function () {
            maxHeight = 0;
            $(this).find(".widget").each(function () {
                if ($(this).innerHeight() > maxHeight) {
                    maxHeight = $(this).innerHeight();
                }
            });
            $(this).find(".widget").each(function () {
                $(this).height(maxHeight);
            });
        });
    }
 
    function initPlacehoderFallback() {//初始化placeholder
        $("input, textarea").placeholder()
    }
 
    function initHeaderSearch() {//初始化头部搜索
        $(document).on("click", ".toggle-search", function () {
            if (!searchOpen) {
                $(".header-search").addClass("open");
                $(".search-container .search").focus();
                searchOpen = true
            } else {
                $(".header-search").removeClass("open");
                $(".search-container .search").focusout();
                searchOpen = false
            }
        });
    }
 
    function initMenuCollapse() {//初始化系统菜单
        $(document).on("click", ".main-navigation a", function (e) {
            var links = $(this).parents('li'),
                parentLink = $(this).closest("li"),
                otherLinks = $('.main-navigation li').not(links),
                subMenu = $(this).next();
            if (!subMenu.hasClass("sub-menu")) {
                offscreen.hide();
                return;
            }
            if (app.hasClass("small-menu") && parentLink.parent().hasClass("nav") && $(window).width() > 767) {
                return;
            }
            otherLinks.removeClass('open');
            otherLinks.find('.sub-menu').slideUp();
            if (subMenu.is("ul") && (!subMenu.is(":visible")) && (!app.hasClass("small-sidebar"))) {
                subMenu.slideDown();
                parentLink.addClass("open");
            } else if (subMenu.is("ul") && (subMenu.is(":visible")) && (!app.hasClass("small-sidebar"))) {
                parentLink.removeClass("open");
                subMenu.slideUp();
            }
            if ($(this).hasClass('active') === false) {
                $(this).parents("ul.dropdown-menu").find('a').removeClass('active');
                $(this).addClass('active');
            }
            if ($(this).attr('href') === '#') {
                e.preventDefault();
            }
            if (subMenu.is("ul")) {
                return false;
            }
            e.stopPropagation();
            return true;
        });
        $(".main-navigation > .nav > li.open").each(function () {
            $(".sub-menu").hide();
            $(this).children(".sub-menu").show();
            $(this).find("li.open").children(".sub-menu").show();
        });
        $(".no-touch .main-navigation, .no-touch .slimscroll").each(function () {
            if (checkBreakout() || app.hasClass("fixed-scroll") || $.browser.mobile) {
                return;
            }
            var data = $(this).data();
            $(this).slimScroll(data);
        });
        //自动添加滚动条，除非有no-scroll样式
        $(".notifications .dropdown-menu .panel .content,.content-wrap .wrapper").each(function () {
            if (!$(this).hasClass("no-scroll")) {
                $(this).slimScroll({
                    alwaysVisible: false
                });
            }
        });
    }
 
    function initToggleActiveState() {//切换active样式
        $(document).on("click touchstart", ".toggle-active", function (e) {
            $(this).toggleClass("active");
            e.preventDefault();
            e.stopPropagation()
        })
    }
 
    function initToggleSidebar() {//展开/收缩菜单
        $(document).on("click touchstart", ".toggle-sidebar", function (e) {
            e.preventDefault();
            e.stopPropagation();
            if (app.hasClass("small-menu")) {
                console.log("yes");
                app.removeClass("small-menu");
                if (!$.browser.mobile && !checkBreakout() && $.fn.slimScroll) {
                    $(".no-touch .main-navigation").each(function () {
                        var data = $(this).data();
                        $(this).slimScroll(data)
                    })
                }
            } else {
                if (!app.hasClass("small-menu")) {
                    console.log("no");
                    app.addClass("small-menu");
                    $(".main-navigation").each(function () {
                        $(this).slimScroll({
                            destroy: true
                        }).removeAttr("style")
                    })
                }
            }
            ns.post(ns.getBasePath()+"/index/changelayout", {layout:$(".app").attr("class")});
        })
    }
 
    function initToggleVertical() {// 切换菜单显示模式
        $(document).on("click touchstart", ".toggle-vertical", function (e) {
            app.toggleClass("layout-v");
            e.preventDefault();
            e.stopPropagation();
            ns.post(ns.getBasePath()+"/index/changelayout", {layout:$(".app").attr("class")},function(){window.location.reload()});
        });
        //初始化二级菜单显示
        $("ul.vertical li>ul>li a").hover(function(e){
        	$(this).parent().parent().find(".open:first").removeClass("open");
        	if($(this).attr("data-toggle")){
    			var submenu = $(this).next();
    			submenu.css("left", submenu.width());
    			submenu.css("top", $(this).offset().top - 52);
    			$(this).parent().addClass("open");
        	}
        },function(){});
    }
 
    function initContentWrap() {//自适应高度
        $("aside, .main-content").each(function () {
        	var height = $(document).height();
        	height -= $(".app>header").height();
            //自适应高度
            if ($(this).find("header").length > 0) {
                height -= $(this).find("header").height();
            }
            if ($(this).find("footer").length > 0) {
                height -= $(this).find("footer").height();
            }
            $(this).find(".content-wrap .wrapper").css("minHeight", height);
            $(this).find(".content-wrap .wrapper").css("top", $(this).find("header").height());
        });
        //全屏
        $(".content-wrap .wrapper-full").css("minHeight", $(document).height());
    }
 
    function initFooterFix() {//footer底部固定
        $("footer").each(function () {
            var footerHeight = $(this).outerHeight();
            if ($(this).prev().hasClass("content-wrap")) {
                $(this).prev().find(".wrapper").css("bottom",
                    footerHeight)
            } else {
                if ($(this).prev().hasClass("slimScrollDiv")) {
                    $(this).prev().find(".main-navigation").css(
                        "padding-bottom", footerHeight)
                } else {
                    if ($(this).prev().hasClass("main-navigation")) {
                        $(this).prev().css("bottom", footerHeight)
                    }
                }
            }
        })
    };
 
    function initSlider() {
        if ($.isFunction($.fn.slider)) {
            $(".sliders input").slider()
        }
    }
 
    function initSortableLists() {
        if ($.isFunction($.fn.sortable)) {
            $(".sortable").sortable();
            $(".handles").sortable({
                handle: "span"
            })
        }
    }
    function initExtPrototype() {
        Array.prototype.contains = function (item) {
            return RegExp(item).test(this);
        };
    }
    function initDropdownMenuDirection(){//初始化下拉菜单方向，上下左右
    	$(".btn-group>.dropdown-menu").each(function(i,n){
    		var w = $(this).width();
    		var h = $(this).height();
    		var l = $(this).prev().offset().left;
    		var t = $(this).prev().offset().top;
    		var sw = $(document).width();
    		var sh = $(document).height()
    		if(w + l > sw){//控制横向
    			if(!$(this).hasClass("pull-right"))
    				$(this).addClass("pull-right");
    		}else{
    			$(this).removeClass("pull-right");
    		}
    		if(h + t > sh){//控制纵向
    			if(!$(this).parent().hasClass("dropup"))
    				$(this).parent().addClass("dropup");
    		}else{
    			$(this).parent().removeClass("dropup");
    		}
    	});
    }
    return {
        getBasePath: function () { //项目路径
            var path = $("#__basePath").val();
            if (path == undefined)
                path = "/nspms"
            return path;
        },
        getUploadPath: function () { //文件访问地址前缀
            return  $("#__uploadPath").val();
        },
        readyEvents: new Array(),
        checkBreakout: checkBreakout,
        initContentWrap: initContentWrap,
        initDropdownMenuDirection : initDropdownMenuDirection,//下拉菜单方向
        init: function () {
            events();
            initAnimationAPI();
            initAnimateProgressBars();
            initHeaderSearch();
            initBrowserFix();
            initMenuCollapse();
            initToggleActiveState();
            initToggleSidebar();
            initToggleVertical();
            initContentWrap();
            initFooterFix();
            equalHeightWidgets();
            initFuelUX();
            initPlacehoderFallback();
            initSlider();
            initSortableLists();
            initExtPrototype();
            initDropdownMenuDirection();//下拉菜单方向
        },
        showProgress: function (obj) { //显示等待
            obj = obj || $("body");
            obj.prepend(
                "<div class=\"gallery-loader\" style=\"background-color:transparent;\"><div class=\"loader\"></div></div>");
        },
        closeProgress: function (obj) { //显示关闭
            obj = obj || $("body");
            obj.find(".gallery-loader").fadeOut(function () {
                obj.find(".gallery-loader").remove();
            });
        },
        showProgressbar : function(title){
        	var id = "__divAlert" + new Date().getTime();
        	var content = "<div class=\"progress progress-striped active\"> <div class=\"progress-bar progress-bar-info done\" style=\"width: 0%\"><span class=\"sr-only\">20% Complete</span> </div> </div>";
            var oDiv = "<div id=\"" + id +
                "\" class=\"modal fade bs-modal-sm\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog\"><div class=\"modal-content\">" +
            "<div class=\"modal-header\"><button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">×</button><h4 class=\"modal-title\">"+title+"</h4></div>" +
            "<div class=\"modal-body\"><div class=\"row p10\"><div class=\"col-xs-12\">" + content +
                "</div></div></div></div></div></div>";
            $("body").append(oDiv);
            $obj = $("#" + id);
            $obj.modal("show");
            //防止遮罩层重叠
            $obj.next().css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 2);
        	$obj.css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 4);
            //关闭窗口后
            $obj.on("hidden.bs.modal", function () {
            	$(this).remove();
            });
           
            var width = (5 + Math.random() * 30);
            var interval = setInterval(function () {
                width = width + 10;
                if (width < 100) {
                    $(".progress-bar").width(width + "%");
                } else {
                    $(".progress-bar").width("101%");
                    clearInterval(interval);
                }
            }, 1000);
            $(".progress-bar").attr("interval", interval);
            $obj.close = function(){
            	$(this).modal("hide");
                clearInterval(interval);
            }
            return $obj;
        },
        showLoadingbar: function (obj) { //显示顶部进度条
            var clz = "waiting";
            if (!obj) {
                clz += " full";
            }
            if (typeof (obj) == "string")
                obj = $(obj);
            obj = obj || $("body");
            obj.prepend("<div id=\"loadingbar\"></div>");
            $("#loadingbar").addClass(clz).append($("<dt/><dd/>"));
            $("#loadingbar").width((30 + Math.random() * 30) + "%");
            var width = (30 + Math.random() * 30);
            var interval = setInterval(function () {
                width = width + 10;
                if (width < 100) {
                    $("#loadingbar").width(width + "%");
                } else {
                    $("#loadingbar").width("101%");
                    clearInterval(interval);
                }
            }, 1000);
            $("#loadingbar").attr("interval", interval);
        },
        closeLoadingbar: function (obj) { //关闭顶部进度条
            if (typeof (obj) == "string")
                obj = $(obj);
            obj = obj || $("body");
            obj.find("#loadingbar").width("101%").delay(200).fadeOut(400, function () {
                var interval = $(this).attr("interval");
                clearInterval(interval);
                $("#loadingbar").remove();
            });
        },
        alert: function (content, ok) { //提示
            var id = "__divAlert" + new Date().getTime();
            var oDiv = "<div id=\"" + id +
                "\" class=\"modal fade bs-modal-sm\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog modal-sm\"><div class=\"modal-content\">" +
            //"<div class=\"modal-header no-b\"><button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">×</button><h4 class=\"modal-title\">"+title+"</h4></div>" +
            "<div class=\"modal-body\"><div class=\"row p10\"><div class=\"col-xs-12\">" + content +
                "</div></div></div>" +
                "<div class=\"modal-footer p10\"><button id=\"ok\" type=\"button\" class=\"btn btn-primary\" autofocus>确 定</button></div></div></div></div>";
            $("body").append(oDiv);
            $obj = $("#" + id);
            $obj.modal("show");
            setTimeout(function(){//延迟获取焦点
            	$obj.find("#ok").focus();
            }, 500);
            $(".app").addClass("blur");
            $obj.find("#ok").click(function () {
            	$obj.modal("hide");
            	if (typeof (ok) == "function") {
            		ok();
            	}
            });
            //防止遮罩层重叠
            $obj.next().css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 2);
        	$obj.css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 4);
            //关闭窗口后
            $obj.on("hidden.bs.modal", function () {
            	$(this).remove();
            	$(".app").removeClass("blur");
            });
            
            return $obj;
        },
        confirm: function (content, ok, cancel) { //确认框
            var id = "__divConfirm" + new Date().getTime();
            var oDiv = "<div id=\"" + id + "\" class=\"modal fade\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog\"><div class=\"modal-content\">";
            if(content.length <= 18)
            	oDiv = "<div id=\"" + id + "\" class=\"modal fade bs-modal-sm\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog modal-sm\"><div class=\"modal-content\">";
            //"<div class=\"modal-header no-b\"><button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">×</button><h4 class=\"modal-title\">"+title+"</h4></div>" +
            oDiv += "<div class=\"modal-body\"><div class=\"row p10\"><div class=\"col-xs-12\">" + content +
                "</div></div></div>" +
                "<div class=\"modal-footer p10\"><button id=\"ok\" type=\"button\" class=\"btn btn-primary\" autofocus>确 定</button><button id=\"cancel\" type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">取 消</button></div></div></div></div>";
            $("body").append(oDiv);
            $obj = $("#" + id);
            $obj.modal("show");
            setTimeout(function(){//延迟获取焦点
            	$obj.find("#ok").focus();
            }, 500);
            $(".app").addClass("blur");
            if (typeof (ok) == "function") {
                $obj.find("#ok").click(function () {
                	$obj.modal("hide");
                    ok();
                });
            }
            if (typeof (cancel) == "function") {
                $obj.find("#cancel").click(cancel);
            }
            //防止遮罩层重叠
            $obj.next().css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 2);
        	$obj.css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 4);
            //关闭窗口后
            $obj.on("hidden.bs.modal", function () {
            	$(this).remove();
            	$(".app").removeClass("blur");
            });
            
            return $obj;
        },
        prompt: function (title, ok, cancel) {
            var id = "__divPrompt" + new Date().getTime();
            var oDiv = "<div id=\"" + id +
                "\" class=\"modal fade bs-modal-sm\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog\"><div class=\"modal-content\">" +
                "<div class=\"modal-header no-b\"><button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">×</button><h4 class=\"modal-title\">" +
                title + "</h4></div>" +
                "<div class=\"modal-body\"><div class=\"row\"><div class=\"col-xs-12\"><input type='text' class='form-control' autofocus/></div></div></div>" +
                "<div class=\"modal-footer p10\"><button id=\"ok\" type=\"button\" class=\"btn btn-primary\">确 定</button><button id=\"cancel\" type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">取 消</button></div></div></div></div>";
            $("body").append(oDiv);
            $obj = $("#" + id);
            $obj.modal("show");
            var $input = $obj.find("input");
            setTimeout(function(){//延迟获取焦点
            	$input.focus();
            }, 500);
            $(".app").addClass("blur");
            if (typeof (ok) == "function") {
            	$input.bind("keydown", function(e){
                	if(e.keyCode == 13){
                		$obj.find("#ok").click();
                	}
                });
                $obj.find("#ok").click(function () {
                    ok($obj.find("input").val());
                    $obj.modal("hide");
                });
            }
            if (typeof (cancel) == "function") {
                $obj.find("#cancel").click(cancel);
            }
            //防止遮罩层重叠
            $obj.next().css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 2);
        	$obj.css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 4);
            //关闭窗口后
            $obj.on("hidden.bs.modal", function () {
            	$(this).remove();
            	$(".app").removeClass("blur");
            });
            return $obj;
        },
        __showModal: function (url, options) { //显示弹窗
            var id = "__divModal" + new Date().getTime();
            var modalSize = "", modalBorder = "";
            if(options){
            	if(options.size) modalSize = options.size;
            	if(options.border == false) modalBorder = " no-b";
            }
            var oDiv = "<div id=\"" + id +
            "\" class=\"modal fade bs-modal-sm\" role=\"dialog\" aria-hidden=\"true\"><div class=\"modal-dialog " +
            modalSize + "\"><div class=\"modal-content"+modalBorder+"\"><div class=\"modal-body\"><div class=\"row\"><div class=\"col-xs-12\"><center><img src='"+ns.getBasePath()+"/framework/img/refresh.gif'/>正在加载中...</center></div></div></div></div></div></div>";
            $("body").append(oDiv);
            $obj = $("#" + id);
            $obj.modal({
                show: true,
                remote: url
            });
            $(".app").addClass("blur");
            //加载完成后，自动加载JS
            $obj.on("shown.bs.modal", function () {
            	var len = ns.readyEvents.length;
                $(this).find("script[src]").each(function(i, n){
            		ns.importJs($(this).attr("src"));
                });
                for (var i = len; i < ns.readyEvents.length; i++) {
                    ns.readyEvents[i]();
                }
                //设置按钮权限
                ns.rules.button();
                //设置焦点
                $(this).find("[autofocus]").focus();
                //防止遮罩层重叠
                $obj.next().css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 2);
            	$obj.css("zIndex", parseInt($obj.css("zIndex")) + $(".modal").length * 4);
            });
            //关闭窗口后
            $obj.on("hidden.bs.modal", function () {
                $(this).remove();
                $(".app").removeClass("blur");
            });
            $obj.close = function(){
            	$(this).modal("hide");
            }
            return $obj;
        },
        __notify: function () {
            $.get(ns.getBasePath() + "/system/notify/header", {}, function (data) {
                var oNotify = $(".notifications ul");
                oNotify.html(data);
            });
            //移除点击方法
            //$(".notifications a:first").removeAttr("onclick");
        },
        __readNotify: function (id, obj) {
            $.post(ns.getBasePath() + "/system/notify/read", {
                id: id
            }, function (data) {
                $(obj).find("a").css("color", "#ccc");
                $(obj).removeAttr("onclick");
 
                // 提示数字减1
                var number = $(".notifications .badge span").text();
                number = parseInt(number);
                number = number - 1;
                if (number <= 0) {
                    $(".notifications .badge").hide();
                } else {
                    $(".notifications .badge span").text(number);
                }
            });
        },
        checkAll: function (obj) { //全选	
        	alert(obj);
            if (obj) {
                if (typeof (obj) == "string") {
                    $(obj).find("input[type='checkbox']").each(function () {
                        this.checked = obj.checked;
                    });
                } else {
                	alert($(obj).hasClass("icheck"));
                	if($(obj).hasClass("icheck")){
                		$(obj).parents("table").find("td input[type='checkbox']").each(function () {
                            if(obj.checked){
                            	$(this).iCheck("check");
                            }else{
                            	$(this).iCheck("check");
                            }
                        });
                	}else{
                		$(obj).parents("table").find("td input[type='checkbox']").each(function () {
                			this.checked = obj.checked;
                        });
                	}
                }
            }
        },
        getChecks: function (obj) { //获取选中的复选框值
            obj = typeof (obj) == "string" ? $(obj) : obj;
            var array = new Array();
            $(obj).find("input[type='checkbox']:checked").each(function () {
                array.push(this.value);
            });
            return array.toString();
        },
        /**
         * 异步提交
         * @param 地址
         * @param 参数列表
         * @param 回调函数，成功时第一个参数为true，失败是false， 第二个参数为服务器返回的数据
         * @returns
         */
        post: function (url, param, callback) { //异步Post提交
            jQuery.post(url, param, function (data) {
            	if(typeof(callback) != "function") return;
                if (data.success == true) {
                    callback(true, data);
                } else {
                    callback(false, data);
                }
            });
        },
        /**
         * 异步获取内容，并加载到页面上
         * @param 地址
         * @param 参数列表
         * @param 目标对象ID
         * @param 加载完成后的回调函数
         * @returns
         */
        asyncRequest: function (url, param, elementId, fn) {
            ns.showLoadingbar(".main-content");
            var timer = setTimeout(function(){$(elementId).html(ns.tip.progress.circle());},100);//延迟显示，提高网速顺畅时的浏览体验
            jQuery.post(url, param, function (data) {
            	clearTimeout(timer);
            	var idx = data.indexOf("<HTML".toLowerCase());
            	if(idx >= 0)
            		data = data.substring(data.indexOf("<HTML".toLowerCase()));
        		if (elementId) {
        			var content = idx>=0?$(elementId, data).html():data;
        			$(elementId).html(content);
        			if (typeof (fn) == "function")
        				fn(true);
        		} else {
        			if (typeof (fn) == "function")
        				fn(false);
        		}
                ns.init2();
                ns.closeLoadingbar(".main-content");
            });
        },
        /**
         * 获取form参数列表
         * @param form
         * @returns
         */
        formAttrs: function (form) {
            var attrs = {};
            if(typeof(form) == "string")
            	form = $(form);
            var elements = form.find("*[name]");
            $.each(elements, function (i, e) {
                e = $(e);
                if(e.attr("type") == "radio"){
                    if(e.is(":checked")){
                        attrs[e.attr("name")] = e.val();
                    }
                }else{
                    attrs[e.attr("name")] = e.val();
                }
            });
            return attrs;
        },
        back: function (url, delay) { //延迟返回指定路径
            setTimeout(function () {
                window.location.href = url;
            }, delay);
        },
        importJs : function(url){//导入js
        	var len = ns.readyEvents.length;
        	$("body").append("<script src=\""+url+"\"></script>");
            for (var i = len; i < ns.readyEvents.length; i++) {
                ns.readyEvents[i]();
            }
        }
    }
}();
//添加到事件池中
ns.readyEvents.push(function(){
    $.ajaxSetup({
        cache: true
    });
    ns.init();//页面加载完初始化
    ns.init2 = function(){//页面局部初始化
    	if(ns.view.initDropdownMenuDirection)ns.view.initDropdownMenuDirection();
        if(ns.form.initChonse)ns.form.initChosen();
        if(ns.form.initDatePicker)ns.form.initDatePicker();
        if(ns.form.initCheckbox)ns.form.initCheckbox();
    };
    ns.tip = { //提示组件
        alert: ns.alert,
        confirm: ns.confirm,
        prompt: ns.prompt,
        tooltip: {},
        toast: {},
        progress:{//进度提示
        	wave : function(){
        		var html = "<div class='sk-wave'>"
        		for(var i=0;i<5;i++) html += "<div class='sk-rect sk-rect"+(i+1)+"'></div>";
        		return html+"</div>";
        	},
        	circle : function(){
        		var html = "<div class='sk-circle'>";
        		for(var i=0;i<12;i++) html += "<div class='sk-circle"+(i+1)+" sk-child'></div>";
        		return html+"</div>";
        	}
        }
    };
    ns.view = { //视图组件
        checkBreakout: ns.checkBreakout,
        initContentWrap: ns.initContentWrap, //自适应高度
        initDropdownMenuDirection : ns.initDropdownMenuDirection,//自适应下拉菜单方向
        showProgress: ns.showProgress, //显示进度提示
        closeProgress: ns.closeProgress, //显示进度提示
        showProgressbar : ns.showProgressbar,//显示进度条
        showLoadingbar: ns.showLoadingbar, //显示顶部进度提示
        closeLoadingbar: ns.closeLoadingbar, //关闭顶部进度提示
        toggleFilter: function (filter) { //显示/隐藏筛选面板
            var filter = $(filter);
            filter.addClass('is-visible');
            filter.unbind("click");
            filter.bind("click", function (event) {
                if ($(event.target).is('.cd-panel') || $(event.target).is('.cd-panel-close')) {
                    filter.removeClass('is-visible');
                    event.preventDefault();
                }
            });
            filter.find("[autofocus]").focus();
        }, //显示/关闭侧边搜索栏
        showModal: ns.__showModal,
        tooltip : function(element, msg){
        	element.tooltip({title:msg});
        	element.tooltip("show");
        }
    };
    ns.form = { //form组件
        post: ns.post,
        ajaxRequest: ns.ajaxRequest,
        formAttrs: ns.formAttrs, //获取form表单中的参数，返回json
        getChecks: ns.getChecks, //获取表格中所有选中的复选框
        checkAll: ns.checkAll //全选
    };
    ns.data = { //数据存储
		localStorage : {//本地存储，永久有效
			set : function(key, value){
				if(window.localStorage){
					key = "ns$ls$"+key;
					window.localStorage.removeItem(key);
					window.localStorage.setItem(key, value);
				}
			},
			get : function(key){
				if(window.localStorage){
					key = "ns$ls$"+key;
					return window.localStorage.getItem(key);
				}
			},
			remove : function(key){
				if(window.localStorage){
					key = "ns$ls$"+key;
					window.localStorage.removeItem(key);
				}
			},
			clear : function(){
				if(window.localStorage){
					window.localStorage.clear();
				}
			}
		},
		sessionStorage : {//本地存储，会话有效
			set : function(key, value){
				if(window.sessionStorage){
					key = "ns$ss$"+key;
					window.sessionStorage.removeItem(key);
					window.sessionStorage.setItem(key, value);
				}
			},
			get : function(key){
				if(window.sessionStorage){
					key = "ns$ss$"+key;
					return window.sessionStorage.getItem(key);
				}
			},
			remove : function(key){
				if(window.sessionStorage){
					key = "ns$ss$"+key;
					window.sessionStorage.removeItem(key);
				}
			},
			clear : function(){
				if(window.sessionStorage){
					window.sessionStorage.clear();
				}
			}
		},
		cookie : {
			_init : function(){
				if(typeof(setCookie) != "function"){
					ns.importJs(ns.getBasePath()+"/framework/js/plugins/zframe.cookie.js")
				}
			},
			set : function(name, value, time){
				ns.data.cookie._init();
				name = "ns$cookie$"+name;
				delCookie(name);
				setCookie(name, value, time)
				return true;
			},
			get : function(name){
				ns.data.cookie._init();
				name = "ns$cookie$"+name;
				return getCookie(name);
			},
			remove : function(name){
				ns.data.cookie._init();
				name = "ns$cookie$"+name;
				delCookie(name);
				return true;
			}
		}
    };
    ns.event = {// events
		move : function(target){
			var header = target.find(".modal-header");
			header.css("cursor", "move");
			var move=false;//移动标记 
			var _x,_y;//鼠标离控件左上角的相对位置 
			header.bind("mousedown", function(e){
				move = true;
				_x=e.pageX - parseInt(target.css("left")); 
				_y=e.pageY - parseInt(target.css("top")); 
			});
			header.bind("mouseup", function(e){
				move = false;
			});
			header.bind("mousemove", function(e){
				if(move){
					var x= e.pageX - _x;//控件左上角到屏幕左上角的相对位置 
					var y= e.pageY - _y; 
					target.css({"top":y,"left":x}); 
				}
			});
		}
    };
    ns.debug = { //debug
    		each : function(obj){
    			$.each(obj, function(i, n){
    				alert(i+":"+n);
    			});
    		}
    };
    /**************业务相关**************/
    ns.rules = {//权限
    		button : function(){
    			/*按钮权限控制开始*/
    			var buttons = $("#__buttons").val();
    			if(buttons == "$ALL$") $("[ns-button]").removeClass("hide").show();
    			if(buttons != "$ALL$"){
    				$("[ns-button]").hide();
    				if(buttons == undefined) return;
    				var array = buttons.split(",");
    				$.each(array, function(i, id){
    					$("[ns-button][id='"+id+"']").removeClass("hide").show();
    				});
    			}
    			/*按钮权限控制结束*/
    		}
    };
    ns.userCenter = function () { //个人中心
        ns.__showModal(ns.getBasePath() + "/uc", {border:false});
    };
    ns.setting = function () { //设置
    	ns.tip.alert("此功能暂未开放！");
    };
    ns.checkUpdate = function () {
    	ns.tip.alert("当前无可用更新！");
    };
    ns.exit = function(){
    	var exitObj = ns.tip.confirm("您确定要退出系统吗？", function(){
    		window.location.href = ns.getBasePath() + "/signout";
    	});
    	var dialog = exitObj.find(".modal-dialog");
    	dialog.addClass("modal-sm");
    	dialog.css("padding-top", "200px");
    };
    ns.rules.button();
    var offset = $(".sub-menu li.active").offset();
	if(offset){
		var top = offset.top;
		if(top > $(".sidebar").height() * 0.8){
			$(".sidebar .main-navigation").animate({scrollTop: (top*0.4)+'px'}, 800, function(){
				if($(document).width() > 1200)
					$(this).slimScroll({ scrollTo: (top * 0.4)+"px"});
			});
		}
	}
	ns.init2();
});
//按顺序逐步加载
$(function () {
    for (var i = 0; i < ns.readyEvents.length; i++) {
        ns.readyEvents[i]();
    }
    ns.readyEvents = new Array();
});
