package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;

import com.nethsoft.orm.query.PageBean;

import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniAssociation;
import com.nethsoft.web.service.alumni.AlumniAssociationService;

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
 * 校友分会
 */
@Controller
@RequestMapping(value = "/alumni/association")
public class AlumniAssociationController extends BaseController<AlumniAssociation> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/association/";
    @Autowired
    private AlumniAssociationService alumniAssociationService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String name) {
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.desc("modifyTime"));
        List<AlumniAssociation> list = alumniAssociationService.listByPage(pageBean);
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
    public JSONObject doAdd(AlumniAssociation alumniAssociation) {
        try {
            int count = alumniAssociationService.count(Restrictions.eq("name",alumniAssociation.getName()));
            if(count > 0){
                return WebResult.error("已存在此社团");
            }
            alumniAssociation.setModifyTime(getCurrentTime());
            alumniAssociationService.save(alumniAssociation);
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
        AlumniAssociation alumniAssociation = alumniAssociationService.getById(id);
        if (!ObjectUtil.isNotNull(alumniAssociation)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniAssociation", alumniAssociation);
        return TPL + "edit";
    }

    /**
     * 执行编辑
     * @param alumniAssociation
     * @return
     */
    @RequestMapping(value = "/edit/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doEdit(AlumniAssociation alumniAssociation) {
        try {
            AlumniAssociation eqAlumniAssociation = alumniAssociationService.getById(alumniAssociation.getId());
            if (ObjectUtil.isNull(eqAlumniAssociation)) {
                return WebResult.error("非法操作");
            }
            alumniAssociation.setModifyTime(getCurrentTime());
            if(alumniAssociation.getName().equals(eqAlumniAssociation.getName())){//没修改社团名称，更新
                alumniAssociationService.merge(alumniAssociation);
            }else{//修改了社团名称，执行新增操作
                alumniAssociation.setId(null);
                alumniAssociation.setGroupId(null);
                alumniAssociationService.save(alumniAssociation);
            }
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
            //删除相关的所有数据
            alumniAssociationService.del(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 查看详情
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    public String detail(Model model, @PathVariable String id) {
        AlumniAssociation alumniAssociation = alumniAssociationService.getById(id);
        if (!ObjectUtil.isNotNull(alumniAssociation)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniAssociation", alumniAssociation);
        return TPL + "detail";
    }
}
