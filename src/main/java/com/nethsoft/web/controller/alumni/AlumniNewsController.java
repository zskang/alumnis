package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.service.alumni.AlumniCommentOpenService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.Constant;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sql.rowset.serial.SerialClob;
import java.util.List;

/**
 * 新闻
 */
@Controller
@RequestMapping(value = "/alumni/news")
public class AlumniNewsController extends BaseController<AlumniNews> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/news/";
    @Autowired
    private AlumniNewsService alumniNewsService;
    @Autowired
    private AlumniCommentOpenService alumniCommentOpenService;

    /**
     * 首页
     */
    @RequestMapping(value="/{type}")
    public String index(Model model, PageBean pageBean,@PathVariable String type,String date,String title) {
        if(type == null){
            return TPL + "400";
        }
        String[] typeArr = new String[]{Constant.NEWS_TYPE_ALMA_MATER,Constant.NEWS_TYPE_ALUMNI_DYNAMICS,Constant.NEWS_TYPE_ALUMNI_ACTIVITY,Constant.NEWS_TYPE_ALUMNI_DONATIONS};
        if (!ArrayUtils.contains(typeArr,type)) {
            return TPL + "400";
        }
        pageBean.addCriterion(Restrictions.eq("type", type));
        if(StringUtil.isNotEmpty(date)){
            pageBean.addCriterion(Restrictions.eq("newsTime",date));
        }
        if(StringUtil.isNotEmpty(title)){
            pageBean.addCriterion(Restrictions.like("title", title.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.asc("top"));
        pageBean.addOrder(Order.desc("newsTime"));
        List<AlumniNews> list = alumniNewsService.listByPage(pageBean);
        model.addAttribute("list", list);
        model.addAttribute("type", type);
        String[] keyArr = {Constant.KEY_COMMENT_OPEN_ALMA_MATER,Constant.KEY_COMMENT_OPEN_ALUMNI_DYNAMICS,Constant.KEY_COMMENT_OPEN_ALUMNI_ACTIVITY,Constant.KEY_COMMENT_OPEN_ALUMNI_DONATIONS};
        int index = ArrayUtils.indexOf(typeArr,type);
        Object comment_open = alumniCommentOpenService.getValueByKey(keyArr[index]);
        model.addAttribute("comment_open", comment_open);
        return TPL + "index";
    }

    /**
     * 转向新增页面
     */
    @RequestMapping(value = "/{type}/add", method = {RequestMethod.GET})
    public String add(Model model,  @PathVariable String type) {
        model.addAttribute("type", type);
        return TPL + "add";
    }

    /**
     * 执行新增
     */
    @RequestMapping(value = "/add/do", method = {RequestMethod.POST})
    @ResponseBody
    public JSONObject doAdd(AlumniNews alumniNews) {
        try {
            if(StringUtil.isNotEmpty(alumniNews.getContentStr())){
                alumniNews.setContent(new SerialClob(alumniNews.getContentStr().toCharArray()));
            }
            alumniNews.setTop("1");//不置顶
            alumniNewsService.save(alumniNews);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 转向编辑页面
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/{type}/edit/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String edit(Model model,@PathVariable String type, @PathVariable String id) {
        AlumniNews alumniNews = alumniNewsService.getById(id);
        if (!ObjectUtil.isNotNull(alumniNews)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        if(alumniNews.getContent() != null){
            alumniNews.setContentStr(AppUtil.converClobToString(alumniNews.getContent()));
        }
        model.addAttribute("alumniNews", alumniNews);
        return TPL + "edit";
    }

    /**
     * 执行编辑
     * @param alumniNews
     * @return
     */
    @RequestMapping(value = "/edit/do", method = {RequestMethod.POST})
    @ResponseBody
    public JSONObject doEdit(AlumniNews alumniNews) {
        try {
            AlumniNews eqAlumniNews = alumniNewsService.getById(alumniNews.getId());
            if (ObjectUtil.isNull(eqAlumniNews)) {
                return WebResult.error("非法操作");
            }
            if(StringUtil.isNotEmpty(alumniNews.getContentStr())){
                alumniNews.setContent(new SerialClob(alumniNews.getContentStr().toCharArray()));
            }
            alumniNewsService.merge(alumniNews);
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
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@RequestParam("id") String id,@RequestParam("type") String type) {
        try {
            //删除相关的所有数据
            alumniNewsService.del(id,type);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 设置置顶状态
     * @param id
     * @param top
     * @return
     */
    @RequestMapping(value = "/setTop", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject setTop(@RequestParam("id") String id,@RequestParam("top") String top) {
        try {
            AlumniNews alumniNews = alumniNewsService.getById(id);
            if (alumniNews == null) {
                return WebResult.error("非法操作");
            }
            alumniNews.setTop(top);
            alumniNewsService.update(alumniNews);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 设置投票上限
     * @param id
     * @param topLimit
     * @return
     */
    @RequestMapping(value = "/setTopLimit", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject setTopLimit(@RequestParam("id") String id,@RequestParam("topLimit") Integer topLimit) {
        try {
            AlumniNews alumniNews = alumniNewsService.getById(id);
            if (alumniNews == null) {
                return WebResult.error("非法操作");
            }
            alumniNews.setTopLimit(topLimit);
            alumniNewsService.update(alumniNews);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 设置投票类别，单选还是多选
     * @param id
     * @param voteType
     * @return
     */
    @RequestMapping(value = "/setVoteType", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject setVoteType(@RequestParam("id") String id,@RequestParam("voteType") String voteType) {
        try {
            AlumniNews alumniNews = alumniNewsService.getById(id);
            if (alumniNews == null) {
                return WebResult.error("非法操作");
            }
            alumniNews.setVoteType(voteType);
            alumniNewsService.update(alumniNews);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
