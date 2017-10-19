package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.service.alumni.AlumniDonateService;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * 捐赠
 */
@Controller
@RequestMapping(value = "/alumni/donate")
public class AlumniDonateController extends BaseController<AlumniDonate> {

    private final String TPL = "/alumni/donate/";
    @Autowired
    private AlumniDonateService alumniDonateService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String date,String name) {
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("student.name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniDonate> list = alumniDonateService.find(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }


    /**
     * 捐赠统计，按姓名
     * @param model
     * @param pageBean
     * @param startDate 开始日期
     * @param endDate  结束日期
     * @return
     */
    @RequestMapping("/statistic")
    public String statistic(Model model,PageBean pageBean,String startDate, String endDate) {
        Map<String,Object> map = alumniDonateService.statistic(pageBean,startDate,endDate);
        model.addAllAttributes(map);
        return TPL + "statistic";
    }

    /**
     * 捐赠指南
     * @return
     */
    @RequestMapping("/guide")
    public String guide() {
        return TPL + "guide";
    }
}
