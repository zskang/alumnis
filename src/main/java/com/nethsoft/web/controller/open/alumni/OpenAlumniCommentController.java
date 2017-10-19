package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniComment;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniCommentOpenService;
import com.nethsoft.web.service.alumni.AlumniCommentService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import com.nethsoft.web.support.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 新闻评论
 */
@RestController
@RequestMapping(value = "/open/alumni/comment/")
public class OpenAlumniCommentController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniCommentService alumniCommentService;
	@Autowired
	private AlumniNewsService alumniNewsService;
	@Autowired
	private AlumniCommentOpenService alumniCommentOpenService;
	@Autowired
	private CampusStudentService campusStudentService;

	/**
	 * 评论列表
	 * @param pageBean
	 * @param newsid
     * @return
     */
	@RequestMapping(value="list")
	public JSONObject list(PageBean pageBean,String newsid){
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		AlumniNews alumniNews = alumniNewsService.getById(newsid);
		if(alumniNews == null){
			return WebResult.error("找不到此新闻");
		}
		String type = alumniNews.getType();
		String key = "";
		if(type.equals(Constant.NEWS_TYPE_ALMA_MATER)){
			key = Constant.KEY_COMMENT_OPEN_ALMA_MATER;
		}else if(type.equals(Constant.NEWS_TYPE_ALUMNI_DYNAMICS)){
			key = Constant.KEY_COMMENT_OPEN_ALUMNI_DYNAMICS;
		}else if(type.equals(Constant.NEWS_TYPE_ALUMNI_ACTIVITY)){
			key = Constant.KEY_COMMENT_OPEN_ALUMNI_ACTIVITY;
		}
		Object comment_open = alumniCommentOpenService.getValueByKey(key);
		JSONArray jsonArray = null;
		if(comment_open == null){
			jsonArray = new JSONArray();
			return WebResult.success().element("comment",jsonArray);
		}else if(comment_open.toString().equals("1")){//不公开
			jsonArray = new JSONArray();
			return WebResult.success().element("comment",jsonArray);
		}else{
			pageBean.addCriterion(Restrictions.eq("news",alumniNews));
			jsonArray = alumniCommentService.listJSONArray(pageBean);
			return WebResult.success().element("comment",jsonArray);
		}
	}

	/**
	 * 保存评论
	 * @param token 用户ID
	 * @param newsid  新闻ID
	 * @param content  内容
     * @return
     */
	@RequestMapping(value="save")
	public JSONObject save(String token,String newsid,String content){
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(newsid)){
			return WebResult.error("新闻ID不能为空");
		}
		if(StringUtil.isEmpty(content)){
			return WebResult.error("内容不能为空");
		}
		if(newsid.startsWith("articleId")){
			return WebResult.error("此新闻不能评论");
		}
		AlumniNews alumniNews = alumniNewsService.getById(newsid);
		if(alumniNews == null){
			return WebResult.error("非法操作");
		}
		CampusStudent campusStudent = campusStudentService.getById(token);
		if(campusStudent == null){
			return WebResult.error("非法操作");
		}
		AlumniComment alumniComment = new AlumniComment(alumniNews,campusStudent,content,getCurrentTime());
		try{
			alumniCommentService.save(alumniComment);
			return WebResult.success();
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

}
