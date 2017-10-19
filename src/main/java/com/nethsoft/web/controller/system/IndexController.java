package com.nethsoft.web.controller.system;

import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.util.ImageUtil;
import com.nethsoft.core.util.MailUtil;
import com.nethsoft.core.util.Md5Util;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.entity.system.UserEmail;
import com.nethsoft.web.entity.system.Version;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.UserEmailService;
import com.nethsoft.web.service.system.UserService;
import com.nethsoft.web.service.system.VersionService;

//import detection.Detector;

/**
 * 系统界面
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseController<Object>{
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserEmailService userEmailService;
	@Autowired
	private VersionService versionService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(value="/index", method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model){
		/*int todayCount = logService.count(Restrictions.like("time", getCurrentTime("yyyy-MM-dd"), MatchMode.START));
		int yesterdayCount = logService.count(Restrictions.like("time", DateUtil.format(DateUtil.minDate(new Date(), 1), "yyyy-MM-dd"), MatchMode.START));
		model.addAttribute("todayCount", todayCount);
		model.addAttribute("yesterdayCount", yesterdayCount);*/
		return "index-simple";
	}
	
	@RequestMapping(value="/index/version", method={RequestMethod.GET, RequestMethod.POST})
	public String version(Model model){
		Version version = versionService.getVersion();
		if(ObjectUtil.isNotNull(version)){
			model.addAttribute("version", version.getVersion());
			model.addAttribute("build", version.getBuild());
		}
		return "index/version";
	}
	
	@RequestMapping(value="/index/changetheme", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject changeTheme(String theme){
		try {
			User user = getCurrentUser();
			if(ObjectUtil.isNull(user))
				return WebResult.error("登录超时");
			user.setPageStyle(theme);
			userService.executeHQL("update User set pageStyle='"+theme+"' where id='"+user.getId()+"'");
			
			logService.info("系统首页", String.format("切换皮肤：[theme: %s]", theme), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 更换布局
	 * @param layout
	 * @return
	 */
	@RequestMapping(value="/index/changelayout", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject changeLayout(String layout){
		try {
			User user = getCurrentUser();
			if(ObjectUtil.isNull(user))
				return WebResult.error("登录超时");
			layout = layout.replace("app", "").trim();
			user.setLayout(layout);
			userService.executeHQL("update User set layout='"+layout+"' where id='"+user.getId()+"'");
			
			logService.info("系统首页", String.format("切换布局：[layout: %s]", layout), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
	
	/**
	 * 个人中心
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/uc", method={RequestMethod.GET, RequestMethod.POST})
	public String userCenter(Model model){
		User user = getCurrentUser();
		model.addAttribute("realName", user.getRealName());
		String roleNames = "";
		for(Role role : user.getRoles()){
			roleNames += ","+role.getName();
		}
		if(roleNames.length()>0)
			roleNames = roleNames.substring(1);
		model.addAttribute("roleNames", roleNames);
		model.addAttribute("unitName", user.getUnits().get(0).getName());
		model.addAttribute("user", user);
		PageBean pageBean = new PageBean();
		pageBean.setRows(8);
		pageBean.addCriterion(Restrictions.or(Restrictions.eq("manipulateName", "退出系统"), Restrictions.eq("manipulateName", "登录系统")));
		pageBean.addCriterion(Restrictions.eq("userName", getCurrentUser().getUserName()));
		pageBean.addOrder(Order.desc("time"));
		model.addAttribute("logs", logService.listByPage(pageBean));
		
		//邮件设置
		UserEmail userEmail = userEmailService.getByHql("select mailUser from UserEmail where userId='"+getCurrentUser().getId()+"'");
		if(ObjectUtil.isNotNull(userEmail)){
			model.addAttribute("mailUser", userEmail.getMailUser());
		}
		model.addAttribute("range", new Random().nextInt(99999));
		return "index/uc";
	}
	
	@RequestMapping(value="/uc/pwd", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject pwd(String oldPwd, String newPwd){
		//判断原密码是否正确
		oldPwd = Md5Util.encode(oldPwd);
		User user = userService.getByHql("select password from User where id = '"+getCurrentUser().getId()+"'");
		if(user.getPassword().equals(oldPwd)){
			userService.executeHQL("update User set password='"+Md5Util.encode(newPwd)+"' where id='"+getCurrentUser().getId()+"'");
			
			logService.info("系统首页", "修改用户密码", getCurrentUser().getUserName(), getRequestAddr());
		}else{
			return WebResult.error("原密码输入错误！");
		}
		return WebResult.success();
	}
	
	/**
	 * 用户头像
	 * @return
	 */
	@RequestMapping(value="/uc/himage", method={RequestMethod.GET, RequestMethod.POST})
	public String himage(){
		return "index/himage";
	}
	
	/**
	 * 上传用户头像
	 * @param request
	 * @param img
	 * @return
	 */
	@RequestMapping(value="/uc/himage/do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject doHimage(HttpServletRequest request, String img) {
		try {
			String dirPath = request.getServletContext().getRealPath("/resources");
			String fileName =dirPath + "/faces/" + getCurrentUser().getId() + ".png";
			Base64 decoder = new Base64();
			// Base64解码
			byte[] imgData = decoder.decode(img.substring(22));
			FileUtils.writeByteArrayToFile(new File(fileName), imgData);
			//图片压缩
			ImageUtil.compressImage(fileName);
			// 人脸识别
//			Detector detector = new Detector(dirPath + "/application/opencv/haarcascade_frontalface_default.xml");
//			List<Rectangle> rec = detector.getFaces(fileName, 1.2f, 1.1f, .05f,2, true);
//
//			if (rec.size() > 0) {
//				Rectangle r = rec.get(0);
//				//图片剪切
//				ImageUtil.cutImage(fileName, fileName, r.x, r.y, r.width, r.height);
//
//				getCurrentUser().setHeadImage(getCurrentUser().getId()+".png");
//				userService.merge(getCurrentUser());//更新到数据库中
//				return WebResult.success();
//			}else{
//				return WebResult.error("识别失败！");
//			}
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			return WebResult.error(e);
		}
	}
	
	/**
	 * 设置外部邮箱
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/uc/email", method = { RequestMethod.GET, RequestMethod.POST })
	public String email(Model model){
		UserEmail userEmail = userEmailService.get(Restrictions.eq("userId", getCurrentUser().getId()));
		if(ObjectUtil.isNotNull(userEmail)){
			model.addAttribute("email", userEmail);
		}
		return "index/email";
	}
	
	/**
	 * 保存外部邮箱设置
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/uc/email/do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject doEmail(UserEmail email){
		UserEmail userEmail = userEmailService.get(Restrictions.eq("userId", getCurrentUser().getId()));
		if(ObjectUtil.isNull(userEmail)){
			userEmail = new UserEmail();
			userEmail.setUserId(getCurrentUser().getId());
		}
		userEmail.setMailType(email.getMailType());
		userEmail.setMailHost(email.getMailHost());
		userEmail.setMailPort(email.getMailPort());
		userEmail.setSslEnable(email.getSslEnable());
		userEmail.setMailUser(email.getMailUser());
		userEmail.setMailPass(email.getMailPass());
		
		userEmailService.saveOrUpdate(userEmail);
		return WebResult.success();
	}
	
	/**
	 * 保存外部邮箱设置
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/uc/email/test", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject doTestEmail(UserEmail email){
		try {
			int result = MailUtil.getMessageCount(email.getMailType(), email.getMailHost(), email.getMailPort(), email.getMailUser(), email.getMailPass(), email.getSslEnable());
			if(result<0)
				return WebResult.error("配置不可用！");
		} catch (MessagingException e) {
			e.printStackTrace();
			return WebResult.error("配置不可用，"+e.getMessage());
		}
		return WebResult.success();
	}
}

