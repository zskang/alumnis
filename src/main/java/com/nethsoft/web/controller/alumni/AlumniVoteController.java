package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniVote;
import com.nethsoft.web.service.alumni.AlumniVoteService;
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

import java.util.List;

/**
 * 投票
 */
@Controller
@RequestMapping(value = "/alumni/news")
public class AlumniVoteController extends BaseController<AlumniVote> {

    private final String TPL = "/alumni/vote/";
    @Autowired
    private AlumniVoteService alumniVoteService;

    /**O
     * 列表
     * @param model
     * @param pageBean
     * @param type  新闻类别
     * @param newsId  新闻ID
     * @param date  搜索日期
     * @param name  搜索姓名
     * @return
     */
    @RequestMapping(value="/{type}/vote/{newsId}",method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,@PathVariable String type,@PathVariable String newsId,
                        String date,String name) {
        if (StringUtil.isEmpty(newsId) || StringUtil.isEmpty(type)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("student.name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addCriterion(Restrictions.eq("news.id",newsId));
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniVote> list = alumniVoteService.find(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }

}
