package com.nethsoft.web.controller.open.alumni;

import com.easemob.server.example.comm.Constants;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.CodeZyb;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.entity.campus.CampusVerCode;
import com.nethsoft.web.service.alumni.AlumniUserService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.service.campus.CampusVerCodeService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 用户
 */
@RestController
@RequestMapping(value = "/open/alumni/user")
public class OpenAlumniUserController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniUserService alumniUserService;
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private CampusVerCodeService campusVerCodeService;
	@Autowired
	private CommonCodeService commonCodeService;

	/**
	 * 注册
	 * @param name  姓名
	 * @param mobile  手机号
	 * @param vercode  验证码
	 * @param rxnf  入学年份
	 * @param zyh  专业号
	 * @param email  邮箱
	 * @param password  密码
     * @return
     * @throws Exception
     */
	@RequestMapping(value="/register")
	public JSONObject register(String name,String mobile,String vercode,String rxnf,String zyh,String email,String password){
		if(StringUtil.isEmpty(name)){
			return WebResult.error("姓名不能为空");
		}
		if(StringUtil.isEmpty(mobile)||mobile.length()!=11||!StringUtil.isNumeric(mobile)){
			return WebResult.error("手机号必须满足11位数字");
		}

		if(StringUtil.isEmpty(password)||password.length()<6||password.length()>16) {
			return WebResult.error("密码必须为6-16位");
		}

		if(!StringUtil.isEmail(email)) {
			return WebResult.error("邮箱格式不符");
		}

		if(StringUtil.isEmpty(rxnf)) {
			return WebResult.error("入学年份不能为空");
		}
		try{
			CodeZyb codeZyb = commonCodeService.getMajor(zyh);
			if(codeZyb == null){
				return WebResult.error("专业不能为空");
			}
			List<CampusVerCode> verCodelist = campusVerCodeService.list(new Criterion[]{Restrictions.eq("mobile", mobile),Restrictions.eq("code", vercode)}, Order.desc("createTime"));
			if(ObjectUtil.isEmpty(verCodelist)) {
				return WebResult.error("验证码错误");
			}else{
				Date sendTime = DateUtil.parseDateTime(verCodelist.get(0).getCreateTime());
				if((new Date().getTime()-sendTime.getTime())>180000) {
					return WebResult.error("验证码已过期");
				}
			}
			return alumniUserService.executeRegister(name, mobile, rxnf, email, password, zyh,codeZyb.getZyjc());
		}catch(Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 登录
	 * @param username  用户名 【手机号】
	 * @param password  密码
     * @return
     */
	@RequestMapping(value="/login")
	public JSONObject login(String username,String password) {
		if(StringUtil.isEmpty(username)) {
			return WebResult.error("用户名不能为空");
		}
		if(StringUtil.isEmpty(password)) {
			return WebResult.error("密码不能为空");
		}
		try{
			CampusStudent	campusStudent = campusStudentService.get(Restrictions.eq("password",password),Restrictions.eq("mobile",username));
//			campusStudent = campusStudentService.get(Restrictions.eq("password", Md5Util.encode(password)),Restrictions.eq("mobile",username));
			if(campusStudent == null){
				return WebResult.error("用户名或者密码错误");
			}
			campusStudent.setLastlogintime(getCurrentTime());
			return alumniUserService.executeLogin(campusStudent);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 修改密码
	 * @param token  用户ID
	 * @param password  初始密码
	 * @param newpassword  新密码
     * @return
     */
	@RequestMapping(value="/password/edit")
	public JSONObject editPassword(String token,String password,String newpassword) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(password)) {
			return WebResult.error("原始密码不能为空");
		}
		if(StringUtil.isEmpty(newpassword)) {
			return WebResult.error("新密码不能为空");
		}
		CampusStudent campusStudent = campusStudentService.getById(token);
		if(campusStudent == null){
			return WebResult.error("用户不存在");
		}
		if(!campusStudent.getPassword().equals(password)){
			return WebResult.error("原始密码不正确");
		}
//		if(!campusStudent.getPassword().equals(Md5Util.encode(password))){
//			return WebResult.error("原始密码不正确");
//		}
		if(password.equals(newpassword)){
			return WebResult.error("新密码不得与原始密码相同");
		}
		campusStudent.setPassword(newpassword);
//		campusStudent.setPassword(Md5Util.encode(newpassword));
		try{
			campusStudentService.update(campusStudent);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 忘记密码
	 * @param username  用户名【手机】
	 * @param password  密码
	 * @param vercode  验证码
     * @return
     */
	@RequestMapping(value="/password/forget")
	public JSONObject forgetPassword(String username,String password,String vercode) {
		if(StringUtil.isEmpty(username)) {
			return WebResult.error("用户名不能为空");
		}
		if(StringUtil.isEmpty(password)) {
			return WebResult.error("密码不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.get(Restrictions.eq("mobile",username));
			if(campusStudent == null){
				return WebResult.error("用户不存在");
			}
			List<CampusVerCode> verCodelist = campusVerCodeService.list(new Criterion[]{Restrictions.eq("mobile", username),Restrictions.eq("code", vercode)}, Order.desc("createTime"));
			if(ObjectUtil.isEmpty(verCodelist)) {
				return WebResult.error("验证码错误");
			}else{
				Date sendTime =  DateUtil.parseDateTime(verCodelist.get(0).getCreateTime());
				if((new Date().getTime()-sendTime.getTime())>180000) {
					return WebResult.error("验证码已过期");
				}
			}
			campusStudent.setPassword(password);
//		campusStudent.setPassword(Md5Util.encode(password));
			campusStudentService.update(campusStudent);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 用户列表
	 * @param pageBean
	 * @param name
     * @return
     */
	@RequestMapping(value="list")
	public JSONObject list(PageBean pageBean,String name) {
		pageBean.addCriterion(Restrictions.ne("campusStudent.imusername", Constants.DEFAULT_IMUSERNAME));//剔除掉通过Excel导入的
		if(StringUtil.isNotEmpty(name)) {
			pageBean.addCriterion(Restrictions.like("campusStudent.name", name.trim(), MatchMode.ANYWHERE));
		}
		try{
			return WebResult.success().element("data",alumniUserService.find(pageBean));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 用户信息
	 * @param usernames  环信账号数组
	 * @return
     */
	@RequestMapping(value="info")
	public JSONObject info(String[] usernames) {
		if(ArrayUtils.isEmpty(usernames)){
			return WebResult.error("环信账号不能为空");
		}
		try{
			List<CampusStudent> list = campusStudentService.list(Restrictions.in("imusername", usernames));
			JSONArray jsonArray = new JSONArray();
			if(list != null && list.size()>0){
				JSONObject jsonObject = null;
				for(CampusStudent student : list){
					jsonObject = new JSONObject();
					jsonObject.element("id",student.getId()).element("name",student.getName()).element("imusername",student.getImusername())
							.element("nickName",StringUtil.null2String(student.getNickname()))
							.element("photo",StringUtil.null2String(student.getPhoto()));
					jsonArray.add(jsonObject);
				}
			}
			return WebResult.success().element("data",jsonArray);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
}
