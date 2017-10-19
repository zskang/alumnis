package com.nethsoft.web.service.system;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.system.Button;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.RoleResourceButton;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.BaseService;

@Service
public class UserService extends BaseService<User>{
	@Autowired
	private ButtonService btnService;
	@Autowired
	private RoleResourceButtonService rrbService;
	@Autowired
	private ResourceService resService;
	@Autowired
	private LogService logService;
	@Autowired
	private UnitService unitService;
	
	public User getById_noLazy(String id) {
		User user = this.getById(id);
		//初始化单位
		Hibernate.initialize(user.getUnits());
		//初始化角色
		Hibernate.initialize(user.getRoles());
		return user;
	}
	public User getById_noLazyUnit(String id) {
		User user = this.getById(id);
		//初始化单位
		Hibernate.initialize(user.getUnits());
		return user;
	}
	
	public List<User> listByPage_noLazyUnit(PageBean pageBean) {
		List<User> list = this.listByPage(pageBean);
		for(User user : list){
			Hibernate.initialize(user.getUnits());
		}
		return list;
	}
	/**
	 * 获取用户的所有菜单
	 * @param id
	 * @return
	 */
	public List<Resource> getUserResList(String id) {
		List<Resource> resList = new ArrayList<Resource>();
		User user = this.getById(id);
		//初始化角色
		Hibernate.initialize(user.getRoles());
		List<String> roleIds = new ArrayList<String>();
		for(Role role : user.getRoles()){
			Hibernate.initialize(role.getResources());
			for(Resource res : role.getResources()){
				if(!resList.contains(res))//判断重复
					resList.add(res);
			}
			roleIds.add(role.getId());
		}
		if(roleIds.size() == 0)
			return resList;
		List<Button> allButtons = btnService.list();//获取所有按钮
		List<RoleResourceButton> rrbList = rrbService.list(Restrictions.in("roleId", roleIds));
		for(RoleResourceButton rrb : rrbList){
			//找到菜单
			Resource res = null;
			for(Resource r : resList){
				if(r.getId().equals(rrb.getResId())){
					res = r;
					break;
				}
			}
			if(ObjectUtil.isNull(res))
				continue;
			//找到按钮
			Button btn = null;
			for(Button b : allButtons){
				if(b.getId().equals(rrb.getBtnId())){
					btn = b;
					break;
				}
			}
			if(ObjectUtil.isNull(btn) || res.getButtons().contains(btn))//判断重复
				continue;
			res.getButtons().add(btn);
		}
		return resList;
	}
	/**
	 * 用户登录操作
	 * @param username
	 * @param password
	 * @param request 
	 * @param session
	 */
	public JSONObject executeLogin(String username, String password, String verifycode, HttpSession session) {
		User user = this.get(Restrictions.or(Restrictions.eq("userName", username),Restrictions.eq("email", username),Restrictions.eq("mobile", username)), Restrictions.eq("password", password));
		if(!user.isEnabled()){
			return WebResult.error("此用户被禁用！");
		}
		//获取上次登录IP
		if(!getRequestAddr().equals(user.getLastLoginIp())){//判断是否与上次登录的IP地址一致
			if(StringUtil.isEmpty(verifycode))
				return WebResult.error("vc");
			else if(!VerificationCode(verifycode))
				return WebResult.error("vc_error");
		}
		//初始化角色
		Hibernate.initialize(user.getRoles());
		//初始化单位
		Hibernate.initialize(user.getUnits());
		
		List<RoleResourceButton> rrbList = new ArrayList<RoleResourceButton>();//菜单按钮
		
		boolean isSystemUnit = false;
		for(Unit unit : user.getUnits())
			if(unit.isSystem())
				isSystemUnit = true;
		//获取上级机构 + 判断机构状态，机构状态为禁用时，下属所有用户无法登陆
		if(user.getUnits().size()>0){
			if(!user.getUnits().get(0).isEnabled())return WebResult.error("账号异常，请与系统管理员联系。"); 
			if(!user.getUnits().get(0).getParentId().equals("-1")){
				Unit parentUnit = unitService.getById(user.getUnits().get(0).getParentId());
				if(!parentUnit.isEnabled()) return WebResult.error("账号异常，请与系统管理员联系。"); 
			}
		}
		if(isSystemUnit && ApplicationCommon.SYSTEM_ADMIN.equals(username)){//系统机构并且为系统机构的人员拥有全部菜单权限
			user.setResources(resService.list("from Resource where enabled=true and display=true order by location asc"));
			rrbList = rrbService.list();
			user.setSystemAdmin(true);//标识为系统管理员
		}else{//非系统管理员按配置菜单权限
			List<String> roleIds = new ArrayList<String>();
			for(Role role : user.getRoles()){
				Hibernate.initialize(role.getResources());
				for(Resource r : role.getResources()){
					if(r.isEnabled() && r.isDisplay() && !user.getResources().contains(r)){//筛选出未启用的和重复的
						user.getResources().add(r);
					}
				}
				roleIds.add(role.getId());
			}
			if(roleIds.size() == 0)
				return WebResult.success();
			
			rrbList = rrbService.list(Restrictions.in("roleId", roleIds));
		}
		
		List<Button> allButtons = btnService.list();//获取所有按钮
		for(RoleResourceButton rrb : rrbList){
			//找到菜单
			Resource res = null;
			for(Resource r : user.getResources()){
				if(r.getId().equals(rrb.getResId()) && r.isEnabled()){
					res = r;
					break;
				}
			}
			if(ObjectUtil.isNull(res))
				continue;
			//找到按钮
			Button btn = null;
			for(Button b : allButtons){
				if(b.getId().equals(rrb.getBtnId())){
					btn = b;
					break;
				}
			}
			if(ObjectUtil.isNull(btn) || res.getButtons().contains(btn))
				continue;
			res.getButtons().add(btn);
		}
		//更新为在线状态
		user.setOnline(true);
		user.setLastLoginType(1);
		user.setLastLoginIp(getRequestAddr());
		user.setLastLoginTime(getCurrentTime());
		
		merge(user);
		//存入Session中
		session.setAttribute(ApplicationCommon.SESSION_ADMIN, user);
		//用户名存入内存中
		ApplicationCommon.LOGIN_USERS.add(user.getUserName());
		ApplicationCommon.LOGIN_SESSIONS.put(user.getUserName(), session);
		logService.info("系统登录", String.format("用户：[%s] 成功登录系统", username), username, getRequestAddr());
		return WebResult.success();
	}
	/**
	 * 根据ID查询用户，立即加载角色列表
	 * @param userId
	 * @return
	 */
	public User getById_noLazyRole(String userId) {
		User user = super.getById(userId);
		if(ObjectUtil.isNotNull(user))
			Hibernate.initialize(user.getRoles());
		return user;
	}
	
	/**
	 * 删除用户相关所有数据
	 * @param id
	 */
	public void deleteAsRelated(String id) {
		super.executeHQL("delete from UserConfig where user.id = ?", id);
		super.executeHQL("delete from UserEmail where userId = ?", id);
		super.executeHQL("delete from UserNotify where user.id = ?", id);
		
		super.executeSQL("delete from sys_user_role where userId = ?", id);
		super.executeSQL("delete from sys_user_unit where userId = ?", id);
		
		super.delete(id);
	}

}
