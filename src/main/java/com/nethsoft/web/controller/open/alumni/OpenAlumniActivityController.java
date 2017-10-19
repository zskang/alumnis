package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.alumni.AlumniEnter;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniEnterService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.service.alumni.AlumniVoteConfigureService;
import com.nethsoft.web.service.alumni.AlumniVoteService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 活动
 */
@RestController
@RequestMapping(value = "/open/alumni/activity/")
public class OpenAlumniActivityController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniNewsService alumniNewsService;
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniEnterService alumniEnterService;
	@Autowired
	private AlumniVoteService alumniVoteService;
	@Autowired
	private AlumniVoteConfigureService alumniVoteConfigureService;


	/**
	 * 活动报名
	 * @param token 用户ID
	 * @param newsid  新闻ID
     * @return
     */
	@RequestMapping(value="enter")
	public JSONObject enter(String token,String newsid){
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		try{
			AlumniNews alumniNews = alumniNewsService.getById(newsid);
			if(alumniNews == null){
				return WebResult.error("非法操作");
			}
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("非法操作");
			}
			int count = alumniEnterService.count(Restrictions.eq("news.id",newsid),Restrictions.eq("student.id",token));
			if(count == 0){//没报过名，新增
				AlumniEnter alumniEnter = new AlumniEnter(alumniNews,campusStudent,getCurrentTime());
				alumniEnterService.save(alumniEnter);
			}
			JSONArray jsonArray = alumniEnterService.find(newsid);
			int enterNum  = jsonArray.size();
			return WebResult.success().element("enterNum",enterNum).element("enterUser",jsonArray);
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 取消报名
	 * @param token  用户ID
	 * @param newsid  新闻ID
     * @return
     */
	@RequestMapping(value="cancel")
	public JSONObject cancel(String token,String newsid){
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		try{
			AlumniNews alumniNews = alumniNewsService.getById(newsid);
			if(alumniNews == null){
				return WebResult.error("非法操作");
			}
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("非法操作");
			}
			alumniEnterService.executeSQL("delete from alumni_enter where news_id=? and student_id=?",newsid,token);
			return WebResult.success();
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 活动投票
	 * @param token 用户ID
	 * @param newsid  新闻ID
	 * @param options  投票选项
	 * @return
	 */
	@RequestMapping(value="vote")
	public JSONObject vote(String token, String newsid, @RequestBody @RequestParam("options") String[] options){
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		if(ArrayUtils.isEmpty(options)){
			return WebResult.error("投票选项不能为空");
		}
		try{
			AlumniNews alumniNews = alumniNewsService.getById(newsid);
			if(alumniNews == null){
				return WebResult.error("非法操作");
			}
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("非法操作");
			}
			if("1".equals(alumniNews.getVoteType()) && options.length>alumniNews.getTopLimit()){
				return WebResult.error("只允许给最多"+alumniNews.getTopLimit()+"个选项投票");
			}
			alumniVoteService.save(newsid,token,options,alumniNews,campusStudent);
			JSONArray jsonArray = alumniVoteService.listJSONArray(newsid);
			int voteNum  = jsonArray.size();
			return WebResult.success().element("voteNum",voteNum).element("voteUser",jsonArray);
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 投票项目
	 * @param newsid
	 * @return
     */
	@RequestMapping(value="project")
	public JSONObject project(String newsid) {
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		try{
			return WebResult.success().element("data",alumniVoteConfigureService.find(newsid));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

}
