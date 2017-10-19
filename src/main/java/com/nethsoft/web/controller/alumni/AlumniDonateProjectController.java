package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;

import com.nethsoft.orm.query.PageBean;

import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniDonateProject;
import com.nethsoft.web.service.alumni.AlumniDonateProjectService;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 捐赠项目
 */
@Controller
@RequestMapping(value = "/alumni/donate/project")
public class AlumniDonateProjectController extends BaseController<AlumniDonateProject> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/donate/project/";
    @Autowired
    private AlumniDonateProjectService alumniDonateProjectService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String name) {
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.desc("modifyTime"));
        List<AlumniDonateProject> list = alumniDonateProjectService.listByPage(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }

    /**
     * 转向新增页面
     */
    @RequestMapping(value = "/add", method =  {RequestMethod.GET})
    public String add(Model model) {
        return TPL + "add";
    }

    /**
     * 执行新增
     */
    @RequestMapping(value = "/add/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doAdd(AlumniDonateProject alumniDonateProject) {
        try {
            alumniDonateProject.setModifyTime(getCurrentTime());
            alumniDonateProjectService.save(alumniDonateProject);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 转向编辑页面
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/edit/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    public String edit(Model model, @PathVariable String id) {
        AlumniDonateProject alumniDonateProject = alumniDonateProjectService.getById(id);
        if (!ObjectUtil.isNotNull(alumniDonateProject)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniDonateProject", alumniDonateProject);
        return TPL + "edit";
    }

    /**
     * 执行编辑
     * @param alumniDonateProject
     * @return
     */
    @RequestMapping(value = "/edit/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doEdit(AlumniDonateProject alumniDonateProject) {
        try {
            AlumniDonateProject eqAlumniDonateProject = alumniDonateProjectService.getById(alumniDonateProject.getId());
            if (ObjectUtil.isNull(eqAlumniDonateProject)) {
                return WebResult.error("非法操作");
            }
            alumniDonateProject.setModifyTime(getCurrentTime());
            alumniDonateProjectService.merge(alumniDonateProject);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@PathVariable String id) {
        try {
            alumniDonateProjectService.del(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
