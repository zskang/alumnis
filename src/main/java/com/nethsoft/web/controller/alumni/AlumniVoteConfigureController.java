package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.alumni.AlumniVoteConfigure;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.service.alumni.AlumniVoteConfigureService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投票项目
 */
@Controller
@RequestMapping(value = "/alumni")
public class AlumniVoteConfigureController extends BaseController<AlumniVoteConfigure> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/vote/configure/";
    @Autowired
    private AlumniVoteConfigureService alumniVoteConfigureService;
    @Autowired
    private AlumniNewsService alumniNewsService;

    /**
     * 首页
     */
    @RequestMapping(value="/news/{type}/vote/configure/{newsId}",method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,@PathVariable String type,@PathVariable String newsId) {
        if (StringUtil.isEmpty(newsId) || StringUtil.isEmpty(type)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        pageBean.addCriterion(Restrictions.eq("news.id",newsId));
        AlumniNews alumniNews = alumniNewsService.getById(newsId);
        List<AlumniVoteConfigure> list = alumniVoteConfigureService.listByPage(pageBean);
        model.addAttribute("list", list);
        model.addAttribute("type", type);
        model.addAttribute("newsId", newsId);
        model.addAttribute("topLimit", alumniNews.getTopLimit());
        model.addAttribute("voteType", alumniNews.getVoteType());
        return TPL + "index";
    }

    /**
     * 保存
     * @param id
     * @param newsId  新闻ID
     * @param name
     * @return
     */
    @RequestMapping(value = "/vote/configure/save", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject save(String id,@RequestParam("newsId") String newsId, @RequestParam("name") String name) {
        try {
            AlumniNews alumniNews = alumniNewsService.getById(newsId);
            if(alumniNews == null){
                return WebResult.error("非法操作");
            }
            AlumniVoteConfigure alumniVoteConfigure = null;
            if(StringUtil.isEmpty(id)){//添加
                alumniVoteConfigure = new AlumniVoteConfigure();
                alumniVoteConfigure.setName(name);
                alumniVoteConfigure.setNews(alumniNews);
                alumniVoteConfigureService.save(alumniVoteConfigure);
            }else{
                alumniVoteConfigure = alumniVoteConfigureService.getById(id);
                alumniVoteConfigure.setName(name);
                alumniVoteConfigureService.merge(alumniVoteConfigure);
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
    @RequestMapping(value = "/vote/configure/delete/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@PathVariable String id) {
        try {
            alumniVoteConfigureService.delete(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
