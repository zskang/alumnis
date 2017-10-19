package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniAssociationMember;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.entity.alumni.AlumniFeedback;
import com.nethsoft.web.entity.alumni.AlumniUser;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.*;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 个人信息
 */
@RestController
@RequestMapping(value = "/open/alumni/personal/")
public class OpenAlumniPersonalController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniUserService alumniUserService;
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniFeedbackService alumniFeedbackService;
	@Autowired
	private AlumniDonateService alumniDonateService;
	@Autowired
	private AlumniEnterService alumniEnterService;
	@Autowired
	private AlumniAssociationMemberService associationMemberService;

	/**
	 * 个人信息
	 * @param token  用户ID
	 * @return
     */
	@RequestMapping(value="info/get")
	public JSONObject getInfo(String token) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			AlumniUser alumniUser = alumniUserService.getByStudentId(token);
			if(alumniUser == null){
				return WebResult.error("找不到此用户");
			}
			CampusStudent student = alumniUser.getCampusStudent();
			JSONObject info = new JSONObject();
			AlumniAssociationMember member = associationMemberService.findOneByStudentId(token);
			info.element("name",StringUtil.isEmpty(student.getName())?"":student.getName())
					.element("photo",StringUtil.isEmpty(student.getPhoto())?"":student.getPhoto())
					.element("signature",StringUtil.isEmpty(alumniUser.getSignature())?"":alumniUser.getSignature())
					.element("sex",StringUtil.isEmpty(alumniUser.getXb())?"":alumniUser.getXb())
					.element("mobile",StringUtil.isEmpty(student.getMobile())?"":student.getMobile())
					.element("email",StringUtil.isEmpty(student.getEmail())?"":student.getEmail())
					.element("birthdate",StringUtil.isEmpty(alumniUser.getCsrq())?"":alumniUser.getCsrq())
					.element("associationName",member==null?"":member.getAssociation().getName())
					.element("industry",StringUtil.isEmpty(alumniUser.getIndustry())?"":alumniUser.getIndustry())
					.element("companyName",StringUtil.isEmpty(alumniUser.getCompanyName())?"":alumniUser.getCompanyName())
					.element("region",StringUtil.isEmpty(alumniUser.getRegion())?"":alumniUser.getRegion())
					.element("address",StringUtil.isEmpty(alumniUser.getAddress())?"":alumniUser.getAddress())
					.element("nickname",StringUtil.isEmpty(student.getNickname())?"":student.getNickname())
					.element("backgroundimage",StringUtil.isEmpty(alumniUser.getBackgroundImage())?"":alumniUser.getBackgroundImage());
			return WebResult.success().element("info",info);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 修改个人信息
	 * @param token  用户ID
	 * @param sex  性别
	 * @param birthdate  出生日期
	 * @param industry  行业
	 * @param region  辖区
	 * @param address  地址
     * @return
     */
	@RequestMapping(value="info/edit")
	public JSONObject editInfo(String token,String sex,String birthdate,String industry,String region,String address,
							   String signature,String mobile,String email,String companyname) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			AlumniUser alumniUser = alumniUserService.getByStudentId(token);
			CampusStudent student = alumniUser.getCampusStudent();
			if(alumniUser == null || student == null){
				return WebResult.error("找不到此用户");
			}
			if(StringUtil.isNotEmpty(sex)){
				alumniUser.setXb(sex);
			}
			if(StringUtil.isNotEmpty(birthdate)){
				birthdate = birthdate.replace("-","");
				alumniUser.setCsrq(birthdate);
			}
			if(StringUtil.isNotEmpty(industry)){
				alumniUser.setIndustry(industry);
			}
			if(StringUtil.isNotEmpty(region)){
				alumniUser.setRegion(region);
			}
			if(StringUtil.isNotEmpty(address)){
				alumniUser.setAddress(address);
			}
			if(StringUtil.isNotEmpty(signature)){
				alumniUser.setSignature(signature);
			}
			if(StringUtil.isNotEmpty(mobile)){
				if(!StringUtil.isMobile(mobile)){
					return WebResult.error("手机号格式不符");
				}
				if(!mobile.equals(student.getMobile()) && campusStudentService.isRegistered(mobile)){
					return WebResult.error("该手机号已绑定其他账号");
				}
				student.setMobile(mobile);
			}
			if(StringUtil.isNotEmpty(email)){
				if(!StringUtil.isEmail(email)){
					return WebResult.error("邮箱格式不符");
				}
				student.setEmail(email);
			}
			if(StringUtil.isNotEmpty(companyname)){
				alumniUser.setCompanyName(companyname);
			}
			campusStudentService.merge(student);
			alumniUserService.merge(alumniUser);
			JSONObject userInfo = new JSONObject();
			userInfo.element("token",student.getId()).element("name",student.getName())
					.element("photo",StringUtil.isEmpty(student.getPhoto())?"":student.getPhoto())
					.element("backgroundimage",StringUtil.isEmpty(alumniUser.getBackgroundImage())?"":alumniUser.getBackgroundImage())
					.element("signature",StringUtil.isEmpty(alumniUser.getSignature())?"":alumniUser.getSignature());
			return WebResult.success().element("userInfo",userInfo);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 提交反馈
	 * @param token  用户ID
	 * @param content  反馈内容
     * @return
     */
	@RequestMapping(value="feedback")
	public JSONObject feedback(String token,String content) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(content)) {
			return WebResult.error("反馈内容不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			AlumniFeedback alumniFeedback = new AlumniFeedback(campusStudent,content,getCurrentTime());
			alumniFeedbackService.save(alumniFeedback);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 我的捐款
	 * @param token  用户ID
	 * @param pageBean
	 * @return
     */
	@RequestMapping(value="donate")
	public JSONObject donate(String token,PageBean pageBean) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			pageBean.addCriterion(Restrictions.eq("student.id",token));
			List<AlumniDonate> list = alumniDonateService.list(pageBean);
			JSONArray jsonArray = new JSONArray();
			if(list != null && list.size()>0){
				JSONObject jsonObject = null;
				for(AlumniDonate alumniDonate : list){
					jsonObject = new JSONObject();
					jsonObject.element("date",alumniDonate.getCreateTime()).element("money",alumniDonate.getMoney());
					jsonArray.add(jsonObject);
				}
			}
			return WebResult.success().element("data",jsonArray);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 我的活动
	 * @param token  用户ID
	 * @param pageBean
	 * @return
     */
	@RequestMapping(value="activity")
	public JSONObject activity(String token,PageBean pageBean) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			pageBean.addCriterion(Restrictions.eq("student.id",token));
			return WebResult.success().element("data",alumniEnterService.findByStudent(pageBean));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 绑定头像
	 * @param token  用户ID
	 * @param path  图片路径
     * @return
     */
	@RequestMapping(value="bind/photo")
	public JSONObject bindPhoto(String token,String path) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(path)) {
			return WebResult.error("图片路径不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			campusStudent.setPhoto(path);
			campusStudentService.update(campusStudent);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 绑定背景图片
	 * @param token  用户ID
	 * @param path  图片路径
	 * @return
	 */
	@RequestMapping(value="bind/backgroundimage")
	public JSONObject bindBackGroundImage(String token,String path) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(path)) {
			return WebResult.error("图片路径不能为空");
		}
		try{
			AlumniUser alumniUser = alumniUserService.get(Restrictions.eq("campusStudent.id",token));
			if(alumniUser == null){
				return WebResult.error("找不到此用户");
			}
			alumniUser.setBackgroundImage(path);
			alumniUser.setModifyTime(getCurrentTime());
			alumniUserService.update(alumniUser);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 修改昵称
	 * @param token  用户ID
	 * @param nickname  昵称
     * @return
     */
	@RequestMapping(value="edit/nickname")
	public JSONObject editNickName(String token,String nickname) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(nickname)) {
			return WebResult.error("昵称不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			campusStudent.setNickname(nickname);
			campusStudentService.update(campusStudent);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

}
