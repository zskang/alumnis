package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniEnter;
import com.nethsoft.web.service.alumni.AlumniEnterService;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 报名参加
 */
@Controller
@RequestMapping(value = "/alumni/news")
public class AlumniEnterController extends BaseController<AlumniEnter> {

    private final String TPL = "/alumni/enter/";
    @Autowired
    private AlumniEnterService alumniEnterService;

    /**
     * 首页
     */
    @RequestMapping(value="/{type}/enter/{newsId}",method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean, @PathVariable String type,@PathVariable String newsId,
                        String date,String name) {
        if (StringUtil.isEmpty(newsId) || StringUtil.isEmpty(type)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("enterTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("student.name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addCriterion(Restrictions.eq("news.id",newsId));
        pageBean.addOrder(Order.desc("enterTime"));
        int enterNum = alumniEnterService.count(newsId);
        List<AlumniEnter> list = alumniEnterService.find(pageBean);
        model.addAttribute("list", list);
        model.addAttribute("enterNum", enterNum);
        model.addAttribute("type", type);
        model.addAttribute("newsId", newsId);
        return TPL + "index";
    }

}
