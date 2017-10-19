package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.*;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniDonateFeedbackService;
import com.nethsoft.web.service.alumni.AlumniNoticeService;
import com.nethsoft.web.service.alumni.AlumniUserService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * 通知
 */
@Controller
@RequestMapping(value = "/open/alumni/notice")
public class OpenAlumniNoticeCotroller extends BaseObject{
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private AlumniNoticeService alumniNoticeService;
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniUserService alumniUserService;
	@Autowired
	private CommonCodeService commonCodeService;
	@Autowired
	private AlumniDonateFeedbackService alumniDonateFeedbackService;

    /**
	 * 通知列表
	 * @param token  用户ID
	 * @return
     */
    @RequestMapping(value="/list")
	public @ResponseBody JSONObject list(HttpServletRequest request, PageBean pageBean, String token) {
		CampusStudent campusStudent = campusStudentService.getById(token);
		if(campusStudent == null){
			return WebResult.error("用户不存在");
		}
		try {
			//查询用户的标签
			AlumniUser alumniUser = alumniUserService.get(Restrictions.eq("campusStudent",campusStudent));
			List<String> tagList = new LinkedList<String>();
			if(StringUtil.isNotEmpty(alumniUser.getRxnf())){
				tagList.add("rxnf_"+alumniUser.getRxnf());//入学年份标签
			}
			if(StringUtil.isNotEmpty(alumniUser.getZyh())){
				tagList.add("major_"+alumniUser.getZyh());//专业标签
			}
			if(StringUtil.isNotEmpty(alumniUser.getIndustry())){
				CodeIndustry industry = commonCodeService.findIndustryByMc(alumniUser.getIndustry());
				if(industry != null){
					tagList.add("industry_"+industry.getId());//行业标签
				}
			}
			if(StringUtil.isNotEmpty(alumniUser.getRegion())){
				CodeDm codeDm = commonCodeService.findRegionByQc(alumniUser.getRegion());
				if(codeDm != null){
					tagList.add("region_"+codeDm.getId());//辖区标签
				}
			}
			pageBean.addCriterion(Restrictions.or(Restrictions.in("tags",tagList),Restrictions.isNull("tags")));
			pageBean.addOrder(Order.desc("createTime"));
			//系统通知
			List<AlumniNotice> noticeList = alumniNoticeService.listByPage(pageBean);
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			if(noticeList != null && noticeList.size()>0){
				jsonObject = new JSONObject();
				String templateNoticePath = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+request.getContextPath()
						+"/open/alumni/notice/detail/";
				String path = "";
				for(AlumniNotice alumniNotice : noticeList){
					path = templateNoticePath + alumniNotice.getId();
					jsonObject.element("title",alumniNotice.getTitle()).element("date",alumniNotice.getCreateTime())
							.element("path",path);
					jsonArray.add(jsonObject);
				}
			}
			//捐赠反馈
			String templateDonatePath = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+request.getContextPath()
					+"/open/alumni/notice/donate/detail/";
			pageBean.clearCriterion();
			pageBean.addCriterion(Restrictions.eq("donate.student",campusStudent));
			List<AlumniDonateFeedback> feedbackList = alumniDonateFeedbackService.listByPage(pageBean);
			if(feedbackList != null && feedbackList.size()>0){
				jsonObject = new JSONObject();
				String path = "";
				for(AlumniDonateFeedback feedback : feedbackList){
					path = templateDonatePath + feedback.getId();
					jsonObject.element("title",feedback.getTitle()).element("date",feedback.getCreateTime())
							.element("path",path);
					jsonArray.add(jsonObject);
				}
			}
			return WebResult.success().element("data",jsonArray);
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 查看详情
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
	public String detail(Model model, @PathVariable String id) {
		AlumniNotice alumniNotice = alumniNoticeService.getById(id);
		if (alumniNotice == null) {
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
		model.addAttribute("alumniNotice", alumniNotice);
		return "/alumni/communication/notice/detail";
	}

	/**
	 * 查看捐赠反馈详情
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/donate/detail/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
	public String donateDetail(Model model, @PathVariable String id) {
		AlumniDonateFeedback alumniDonateFeedback = alumniDonateFeedbackService.getById(id);
		if (!ObjectUtil.isNotNull(alumniDonateFeedback)) {
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
		model.addAttribute("feedback", alumniDonateFeedback);
		return "/alumni/donate/feedback/detail";
	}

}
