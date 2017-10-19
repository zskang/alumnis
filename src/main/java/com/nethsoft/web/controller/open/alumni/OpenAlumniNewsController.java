package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.alumni.AlumniNewsAgree;
import com.nethsoft.web.service.alumni.AlumniNewsAgreeService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.BaseObject;
import com.nethsoft.web.support.Constant;
import com.nethsoft.web.support.alumni.ExtractUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 新闻
 */
@Controller
@RequestMapping(value = "/open/alumni/news/")
public class OpenAlumniNewsController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniNewsService alumniNewsService;
	@Autowired
	private AlumniNewsAgreeService alumniNewsAgreeService;

	/**
	 * 点赞
	 * @param newsid  新闻ID
	 * @param token  用户ID
	 * @return
	 */
	@RequestMapping(value="agree")
	public @ResponseBody JSONObject agree(String newsid,String token){
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		try{
			int count = alumniNewsAgreeService.count(Restrictions.eq("newsId",newsid),Restrictions.eq("studentId",token));
			if(count > 0){
				return WebResult.error("已点过赞了");
			}
			AlumniNewsAgree alumniNewsAgree = new AlumniNewsAgree(newsid,token,getCurrentTime());
			alumniNewsAgreeService.save(alumniNewsAgree);
			int agreeNum = alumniNewsAgreeService.count(Restrictions.eq("newsId",newsid));
			return WebResult.success().element("agreeNum",agreeNum);
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 母校新闻
	 * @param pageBean
	 * @param title  标题
	 * @return
	 */
	@RequestMapping(value="almamater")
	public @ResponseBody JSONObject almaMater(PageBean pageBean,String title){
		try{
			return WebResult.success().element("data",alumniNewsService.find(pageBean,title,Constant.NEWS_TYPE_ALMA_MATER));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 校友动态
	 * @param pageBean
	 * @param title  标题
	 * @return
	 */
	@RequestMapping(value="dynamics")
	public @ResponseBody JSONObject dynamics(PageBean pageBean,String title){
		try{
			return WebResult.success().element("data",alumniNewsService.find(pageBean,title,Constant.NEWS_TYPE_ALUMNI_DYNAMICS));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 校友活动
	 * @param pageBean
	 * @param title
	 * @return
	 */
	@RequestMapping(value="activity")
	public @ResponseBody JSONObject activity(PageBean pageBean,String title){
		try{
			return WebResult.success().element("data",alumniNewsService.find(pageBean,title,Constant.NEWS_TYPE_ALUMNI_ACTIVITY));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 往期回顾
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value="activity/past")
	public @ResponseBody JSONObject pastActivity(PageBean pageBean){
		pageBean.addCriterion(Restrictions.lt("newsTime",getCurrentTime("yyyy-MM-dd")));
		return activity(pageBean,"");
	}

	/**
	 * 报名参加
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value="activity/future")
	public @ResponseBody JSONObject futureActivity(PageBean pageBean){
		pageBean.addCriterion(Restrictions.ge("newsTime",getCurrentTime("yyyy-MM-dd")));
		return activity(pageBean,"");
	}

	/**
	 * 新闻详情
	 * @param request
	 * @param pageBean
	 * @param newsid 新闻ID
	 * @param token 用户ID
	 * @return
	 */
	@RequestMapping(value="info")
	public @ResponseBody JSONObject info(HttpServletRequest request,PageBean pageBean,String newsid,String token){
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		try{
			String path = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			path += "/open/alumni/news/detail/"+newsid;
			AlumniNews alumniNews = alumniNewsService.getById(newsid);
			if(alumniNews == null){
				return WebResult.error("找不到此新闻");
			}
			if(StringUtil.isEmpty(token)){//没登录的情况
				return WebResult.success().element("path",path).element("enterNum",0).element("voteNum",0);
			}
			JSONObject result =  alumniNewsService.recordInfo(alumniNews,pageBean,token);
			result.element("path",path);
			return result;
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}


	/**
	 * 查看详情
	 *
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "detail/{id}")
	public String detail(Model model, @PathVariable String id) throws Exception{
		AlumniNews alumniNews = alumniNewsService.getById(id);
		if (!ObjectUtil.isNotNull(alumniNews)) {
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
		if(alumniNews.getContent() != null){
			alumniNews.setContentStr(AppUtil.converClobToString(alumniNews.getContent()));
		}
		model.addAttribute("alumniNews", alumniNews);
		return "/alumni/news/detail";
	}

}
