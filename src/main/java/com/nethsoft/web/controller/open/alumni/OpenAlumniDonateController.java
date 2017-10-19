package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.entity.alumni.AlumniDonateProject;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniDonateProjectService;
import com.nethsoft.web.service.alumni.AlumniDonateService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.BaseObject;
import com.nethsoft.web.support.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

/**
 * 捐赠
 */
@Controller
@RequestMapping(value = "/open/alumni/donate/")
public class OpenAlumniDonateController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniNewsService alumniNewsService;
	@Autowired
	private AlumniDonateService alumniDonateService;
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniDonateProjectService alumniDonateProjectService;

	/**
	 * 捐赠
	 * @param token 用户ID
	 * @param type  类别
	 * @param projectid  项目ID
	 * @param money  金额
	 * @param message  留言
	 * @param teamName  集体名称
	 * @param way  支付方式
     * @return
     */
	@RequestMapping(value="do")
	public @ResponseBody JSONObject donate(String token, String type, String projectid, BigDecimal money,
										   String message,String teamName,String way){
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(type)){
			return WebResult.error("捐赠类别不能为空");
		}
		String[] typeArr = {Constant.TYPE_ALUMNI_DONATE_PERSONAL,Constant.TYPE_ALUMNI_DONATE_TEAM};
		if(!ArrayUtils.contains(typeArr,type)){
			return WebResult.error("捐赠类别错误");
		}
		if(StringUtil.isEmpty(projectid)){
			return WebResult.error("捐赠项目不能为空");
		}
		if(money == null){
			return WebResult.error("捐赠金额不能为空");
		}
		if(StringUtil.isEmpty(way)){
			return WebResult.error("支付方式不能为空");
		}
		String[] wayArr = {Constant.WAY_ALUMNI_DONATE_ALIPAY,Constant.WAY_ALUMNI_DONATE_WECHAT};
		if(!ArrayUtils.contains(wayArr,way)){
			return WebResult.error("支付方式错误");
		}
		try{
			CampusStudent student = campusStudentService.getById(token);
			if(student == null){
				return WebResult.error("没找到此用户");
			}
			AlumniDonateProject donateProject = alumniDonateProjectService.getById(projectid);
			if(donateProject == null){
				return WebResult.error("没找到此捐赠项目");
			}
			String orderNum= AppUtil.makeOrderNum();
			if(StringUtil.isEmpty(orderNum)){
				return WebResult.error("捐赠系统繁忙");
			}
			AlumniDonate alumniDonate = new AlumniDonate(student,getCurrentTime(),money,type,donateProject,
					message,Constant.STATE_ALUMNI_DONATE_NOT,teamName,way,orderNum);
			alumniDonateService.save(alumniDonate);
			return WebResult.success().element("orderNum",orderNum);
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠新闻
	 * @param pageBean
	 * @param title
     * @return
     */
	@RequestMapping(value="news")
	public @ResponseBody JSONObject news(PageBean pageBean,String title){
		try{
			return WebResult.success().element("data",alumniNewsService.find(pageBean,title,Constant.NEWS_TYPE_ALUMNI_DONATIONS));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠相册
	 * @param pageBean
	 * @param title
	 * @return
	 */
	@RequestMapping(value="album")
	public @ResponseBody JSONObject album(PageBean pageBean,String title){
		try{
			return WebResult.success().element("data",alumniNewsService.find(pageBean,title,Constant.NEWS_TYPE_ALUMNI_DONATIONS));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠动态
	 * @param pageBean
	 * @param year 年份
	 * @param field  查询的属性名称
	 * @param value  查询的名称对应的值
     * @return
     */
	@RequestMapping(value="dynamics")
	public @ResponseBody JSONObject dynamics(PageBean pageBean,String year,String field,String value){
		try{
			JSONArray jsonArray = alumniDonateService.extractDynamics(pageBean,year,field,value);
			return WebResult.success().element("data",jsonArray);
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠详情
	 * @param id
	 * @return
     */
	@RequestMapping(value="info")
	public @ResponseBody JSONObject info(String id){
		if(StringUtil.isEmpty(id)){
			return WebResult.error("捐赠ID不能为空");
		}
		try{
			return WebResult.success().element("info",alumniDonateService.getDonateInfo(id));
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠项目
	 * @return
     */
	@RequestMapping(value="project")
	public @ResponseBody JSONObject project(){
		try{
			return WebResult.success().element("data",alumniDonateProjectService.find());
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 捐赠指南
	 * @return
	 */
	@RequestMapping("guide")
	public String guide() {
		return "/alumni/donate/guide-open";
	}

}
