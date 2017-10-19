package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniFeedback;
import com.nethsoft.web.service.alumni.AlumniFeedbackService;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 系统反馈
 */
@Controller
@RequestMapping(value = "/alumni/feedback")
public class AlumniFeedbackController extends BaseController<AlumniFeedback> {

    private final String TPL = "/alumni/feedback/";
    @Autowired
    private AlumniFeedbackService alumniFeedbackService;

    /**
     * 首页
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String date,String content) {
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(content)){
            pageBean.addCriterion(Restrictions.like("content", content.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniFeedback> list = alumniFeedbackService.find(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }

}
