package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniComment;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.service.alumni.AlumniCommentOpenService;
import com.nethsoft.web.service.alumni.AlumniCommentService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.support.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻评论
 */
@Controller
@RequestMapping(value = "/alumni")
public class AlumniCommentController extends BaseController<AlumniComment> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/comment/";
    @Autowired
    private AlumniCommentService alumniCommentService;
    @Autowired
    private AlumniNewsService alumniNewsService;
    @Autowired
    private AlumniCommentOpenService alumniCommentOpenService;

    /**
     *评论列表
     * @param model
     * @param pageBean
     * @param type 新闻的类型
     * @param newsId  新闻ID
     * @param date  查询的日期
     * @param content 查询的内容
     * @return
     */
    @RequestMapping(value = "/news/{type}/comment/{newsId}")
    public String index(Model model, PageBean pageBean,@PathVariable String type,@PathVariable String newsId,String date,String content) {
        if(StringUtil.isEmpty(type)){
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        if(StringUtil.isEmpty(newsId)){
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(content)){
            pageBean.addCriterion(Restrictions.like("content", content.trim(), MatchMode.ANYWHERE));
        }
        AlumniNews alumniNews = alumniNewsService.getById(newsId);
        pageBean.addCriterion(Restrictions.eq("news",alumniNews));
        JSONArray jsonArray = alumniCommentService.listJSONArray(pageBean);
        model.addAttribute("list", jsonArray);
        model.addAttribute("newsId", newsId);
        model.addAttribute("type", type);
        return TPL + "index";
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/comment/delete/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@PathVariable String id) {
        try {
            //删除相关的所有数据
            alumniCommentService.delete(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 评论公开设置
     * @param value
     * @param type
     * @return
     */
    @RequestMapping(value = "/comment/open")
    @ResponseBody
    public JSONObject open(@RequestParam("value") String value,@RequestParam("type") String type) {
        if(StringUtil.isEmpty(value) || StringUtil.isEmpty(type)){
            return WebResult.error("参数错误");
        }
        try {
            String key;
            switch (type){
                case Constant.NEWS_TYPE_ALMA_MATER:
                    key = Constant.KEY_COMMENT_OPEN_ALMA_MATER;
                    break;
                case Constant.NEWS_TYPE_ALUMNI_DYNAMICS:
                    key = Constant.KEY_COMMENT_OPEN_ALUMNI_DYNAMICS;
                    break;
                case Constant.NEWS_TYPE_ALUMNI_ACTIVITY:
                    key = Constant.KEY_COMMENT_OPEN_ALUMNI_ACTIVITY;
                    break;
                case Constant.NEWS_TYPE_ALUMNI_DONATIONS:
                    key = Constant.KEY_COMMENT_OPEN_ALUMNI_DONATIONS;
                    break;
                default:
                    key = null;
            }
            if(key == null){
                return WebResult.error("参数错误");
            }
            alumniCommentOpenService.save(key,value);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
