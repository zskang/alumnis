package com.nethsoft.web.support.filter;

import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.BigDecimalUtil;
import com.nethsoft.core.util.HttpServletUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.system.Button;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.User;

/**
 * 添加每次请求中一些公用的参数
 *
 * @author ZENGCHAO
 */
public class ParamInitInterceptor implements HandlerInterceptor {
    /**
     * 在业务处理器处理之前调用
     * 如果返回false
     * 则从当前的处理器往回执行afterCompletion(),再退出拦截连
     * 如果返回true
     * 执行下一个拦截器，知道所有拦截器你执行完毕
     * 在执行业务处理器Controller
     * 然后进入拦截器链
     * 从最后一个拦截器往回执行所有的postHandle()
     * 接着再从最后一个拦截器往回执行所有的afterCompletion()
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object object) throws Exception {
        String requestUrl = request.getRequestURI().replaceFirst(request.getRequestURI().substring(0, request.getRequestURI().indexOf(request.getContextPath()) + request.getContextPath().length()), "");

        //项目基本路径
        if (StringUtil.isEmpty(ApplicationCommon.BASEPATH)) {
            String basePath = request.getContextPath().replace("/", "");
            if (basePath.trim().length() > 0) {
                basePath = "/" + basePath;
            }
            ApplicationCommon.BASEPATH = basePath;
        }
        request.setAttribute("basePath", ApplicationCommon.BASEPATH);
        if (StringUtil.isEmpty(ApplicationCommon.RESPATH)) {//默认使用项目路径
            ApplicationCommon.RESPATH = ApplicationCommon.BASEPATH;
        }
        request.setAttribute("resPath", ApplicationCommon.RESPATH);
        ApplicationCommon.WEBAPPS_PATH = request.getServletContext().getRealPath("/");
        //获取上传文件根路径
        Properties props_remote = ApplicationCoreConfigHelper.getPropertyGroup("upload.remote");
        if(Boolean.parseBoolean(props_remote.getProperty("upload.remote.enabled", "default"))){
            request.setAttribute("uploadPath", props_remote.getProperty("upload.remote.view"));//上传文件保存路径
        }else{
            request.setAttribute("uploadPath", ApplicationCommon.BASEPATH);
        }
        //BigDecimalUtil 数字精度管理
        request.setAttribute("BigDecimalUtil", BigDecimalUtil.class);


        String theme = "palette";//默认样式
        if (requestUrl.startsWith("/") && !requestUrl.startsWith("/open") && !requestUrl.startsWith("/error")) {
            if (!requestUrl.startsWith("/signin")) {
                User currentUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
                //存储用户的皮肤和布局信息
                theme = StringUtil.isEmpty(currentUser.getPageStyle().trim()) ? "palette" : currentUser.getPageStyle().trim();
                request.setAttribute("theme", theme);
                request.setAttribute("layout", currentUser.getLayout() == null ? "" : currentUser.getLayout().trim());

                request.setAttribute("RealName", currentUser.getRealName());
                // TODO 测试时使用，重新登录功能
                request.setAttribute("username", currentUser.getUserName());
                request.setAttribute("password", currentUser.getPassword());
                //初始化后台管理系统资源按钮权限
                List<Resource> firstRes = new ArrayList<Resource>();
                Map<String, List<Resource>> secondRes = new HashMap<String, List<Resource>>();
                Map<String, List<Resource>> thirdRes = new HashMap<String, List<Resource>>();
                Resource currentResource = null;//当前机构，用于按钮权限控制
                int currentLevenshtein = 1000;//相似度，值越小相似度越高

                /*
                 * add by cf 2017-01-05
                 * 为了提高准确率将请求是以uuid结束的则去掉uuid再进行判断
                 */
                int pos = requestUrl.lastIndexOf("/");
                String endStr  = requestUrl.substring(pos+1);
                //系统使用UUID生成的主键为32位,
                if(32==endStr.length()){
                    requestUrl = requestUrl.substring(0,pos);
                }

                for (Resource res : currentUser.getResources()) {//一级菜单
                    //if(requestUrl.equals(res.getUrl()) && ObjectUtil.isNull(currentResource)) currentResource = res;//设置当前资源
                    int levenshtein = StringUtils.getLevenshteinDistance(requestUrl, res.getUrl());
                    if (levenshtein < currentLevenshtein) {
                        currentLevenshtein = levenshtein;
                        currentResource = res;//设置当前资源
                    }
                    if (res.getParentId().equals("-1")) {
                        firstRes.add(res);
                        List<Resource> childrens = new ArrayList<Resource>();
                        //获取子菜单
                        for (Resource childRes : currentUser.getResources()) {
                            //二级菜单
                            if (childRes.getParentId().equals(res.getId())) {
                                childrens.add(childRes);

                                //三级菜单
                                List<Resource> thirdChildrens = new ArrayList<Resource>();
                                for (Resource third : currentUser.getResources()) {
                                    if (third.getParentId().equals(childRes.getId())) {
                                        thirdChildrens.add(third);
                                    }
                                }
                                if (thirdChildrens.size() > 0)
                                    thirdRes.put(childRes.getId(), thirdChildrens);
                            }
                        }
                        if (childrens.size() > 0)
                            secondRes.put(res.getId(), childrens);
                    }
                }
                request.setAttribute("_firstRes", firstRes);
                request.setAttribute("_secondRes", secondRes);
                request.setAttribute("_thirdRes", thirdRes);
                //按钮权限
                if (ObjectUtil.isNotNull(currentResource)) {
                    List<String> sidebarPath = new ArrayList<String>();//存储路径
                    getResPath(currentResource, currentUser.getResources(), sidebarPath);
                    Collections.reverse(sidebarPath);
                    request.setAttribute("_sidebar", sidebarPath);
                    if (currentUser.isSystemAdmin()) {//系统管理员不做按钮权限控制
                        request.setAttribute("buttons", "$ALL$");
                    } else {
                        List<String> buttonIdList = new ArrayList<String>();
                        for (Button button : currentResource.getButtons()) {
                            buttonIdList.add(button.getCode());
                        }
                        request.setAttribute("buttons", StringUtil.toString(buttonIdList));
                    }
                }
            }
        }
        request.setAttribute("theme", theme);
        return true;
    }

    /**
     * 获取资源的位置
     * 一级菜单》二级菜单》三级菜单》...
     *
     * @param sidebarPath
     */
    private List<String> getResPath(Resource res, List<Resource> allRes, List<String> sidebarPath) {
        sidebarPath.add(res.getName());
        if (!res.getParentId().equals("-1")) {
            Resource parentRes = null;
            for (Resource r : allRes) {
                if (r.getId().equals(res.getParentId()))
                    parentRes = r;
            }
            if (ObjectUtil.isNotNull(parentRes))
                getResPath(parentRes, allRes, sidebarPath);
        }
        return sidebarPath;
    }

    /**
     * 在业务处理器处理完成之后执行
     * 在生成视图操作之前执行
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView mav) throws Exception {
        if (ObjectUtil.isNotNull(mav)) {
            String basePath = request.getContextPath().replace("/", "");
            if (basePath.trim().length() > 0) {
                basePath = "/" + basePath;
            }
            if (ObjectUtil.isNotNull(mav.getViewName())) {
                if (mav.getViewName().equals(ControllerCommon.UNAUTHORIZED_ACCESS)) {
                    if (HttpServletUtil.isAjaxWithRequest(request)) {
                        response.setContentType("text/json");
                        PrintWriter out = response.getWriter();
                        out.println(WebResult.error("非法访问!"));
                        out.close();
                    } else {
                        response.sendRedirect(basePath + "/error/" + ControllerCommon.UNAUTHORIZED_ACCESS);
                    }
                    mav.clear();
                } else if (mav.getViewName().equals(ControllerCommon.NO_PERMISSION)) {//无权访问
                    if (HttpServletUtil.isAjaxWithRequest(request)) {
                        response.setContentType("text/json");
                        PrintWriter out = response.getWriter();
                        out.println(WebResult.error("无权访问!"));
                        out.close();
                    } else {
                        response.sendRedirect(basePath + "/error/" + ControllerCommon.NO_PERMISSION);
                    }
                    mav.clear();
                } else if (mav.getViewName().equals(ControllerCommon.ERROR)) {
                    if (HttpServletUtil.isAjaxWithRequest(request)) {
                        response.setContentType("text/json");
                        PrintWriter out = response.getWriter();
                        out.println(WebResult.error("系统繁忙!"));
                        out.close();
                    } else {
                        response.sendRedirect(basePath + "/error/" + ControllerCommon.ERROR);
                    }
                    mav.clear();
                } else if (mav.getViewName().equals(ControllerCommon.CustomError.getViewName())) {
                    if (HttpServletUtil.isAjaxWithRequest(request)) {
                        response.setContentType("text/json");
                        PrintWriter out = response.getWriter();
                        out.println(WebResult.error(ControllerCommon.CustomError.getScript()));
                        out.close();
                    } else {
                        response.sendRedirect(basePath + "/error/" + ControllerCommon.ERROR);
                    }
                    mav.clear();
                }
            }
        }
    }

    /**
     * 在DispatcherServlet完全处理完请求后调用
     * 如果发生异常，则会从当前的拦截器往回执行所有的afterCompletion
     */
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object object, Exception exception)
            throws Exception {

    }
}
