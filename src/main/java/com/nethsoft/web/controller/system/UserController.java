package com.nethsoft.web.controller.system;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.util.Md5Util;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.RoleService;
import com.nethsoft.web.service.system.UserService;
import com.nethsoft.web.support.Constant;
import com.nethsoft.web.support.ImUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController<User>{
	private final Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private LogService logService;

	
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, PageBean pageBean){
		logService.info("用户管理", "访问用户列表", getCurrentUser().getUserName(), getRequestAddr());
		return "system/user/index";
	}
	
	/**
	 * 获取机构下级用户
	 * @param model
	 * @param unitId
	 * @return
	 */
	@RequestMapping(value="/unituser/{unitId}", method={RequestMethod.GET, RequestMethod.POST})
	public String unit(Model model, @PathVariable String unitId, String userName, String realName){
		String hql = "select u.users From Unit u where u.id='"+unitId+"'";
		List<User> userList = userService.list(hql);
		model.addAttribute("userList", userList);
		return "system/user/unituser";
	}
	/**
	 * 增加用户
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add", method={RequestMethod.GET})
	public String add(Model model){ 
		//获取角色
		//List<Role> roleList = roleService.listForEntity("select id, name from Role");
		//model.addAttribute("roleList", roleList);
		return "system/user/add";
	}
	
	@RequestMapping(value="/unitrole/{unitId}", method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject unitrole(@PathVariable String unitId){
		JSONObject json = new JSONObject();
		List<Role> roleList = roleService.listForEntity("select id, name from Role where unit.id = '"+unitId+"'");
		JSONArray roles = new JSONArray();
		for(Role role : roleList){
			JSONObject r = new JSONObject();
			r.element("id", role.getId());
			r.element("name", role.getName());
			
			roles.add(r);
		}
		json.element("roles", roles);
		return json;
	}
	
	@RequestMapping(value="/add/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(User user, String unitId, String[] roleIds){
		try {
			if(StringUtil.isBlank(user.getUserName())){
				return WebResult.error("用户名不能为空！");
			}
			if(StringUtil.isBlank(user.getPassword())){
				return WebResult.error("密码不能为空！");
			}
			String mobile = user.getMobile();
			if(StringUtil.isEmpty(mobile)){
				return WebResult.error("联系方式不能为空");
			}
			//判断用户名是否重复
			int count = userService.count(Restrictions.eq("userName", user.getUserName()));
			if(count > 0)
				return WebResult.error("用户名 '"+user.getUserName()+"' 已存在！");
			if(!StringUtil.isBlank(user.getEmail())){
				count = userService.count(Restrictions.eq("email", user.getEmail()));
				if(count > 0)
					return WebResult.error("邮箱 '"+user.getEmail()+"' 已被使用！");
			}
			
			//判断此联系方式是否存在
			if(mobileIsExist(mobile.trim())){
				return WebResult.error("联系方式 '"+mobile+"' 已存在！");
			}
			
			//绑定角色
			for(String roleId : roleIds){
				Role role = new Role();
				role.setId(roleId);
				
				user.getRoles().add(role);
			}
			//绑定单位
			Unit unit = new Unit();
			unit.setId(unitId);
			user.getUnits().add(unit);
			//设置创建时间
			user.setCreateTime(getCurrentTime());
			//密码加密
			user.setPassword(Md5Util.encode(user.getPassword()));
			
			userService.save(user);
			logger.debug(String.format("新增用户：[Id:%s,UserName:%s,Unit:%s]", user.getId(), user.getUserName(), unitId));
			logService.info("用户管理", String.format("新增用户：[Id:%s,UserName:%s,Unit:%s]", user.getId(), user.getUserName(), unitId), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 删除单个用户
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/delete/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(@PathVariable String id){
		try {
			userService.deleteAsRelated(id);
			logger.debug(String.format("删除用户：[Id:%s]", id));
			logService.info("用户管理", String.format("删除用户：[Id:%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 删除多个用户
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/delete", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(String[] ids){
		try {
			//批量删除
			for(String id : ids)
				userService.delete(id);
			logger.debug(String.format("删除用户：[Id:%s]", StringUtil.toString(ids)));
			logService.info("用户管理", String.format("删除用户：[Id:%s]", StringUtil.toString(ids)), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 启用
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/enable/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject enable(@PathVariable String id){
		try {
			userService.executeHQL("update User set enabled=true where id='"+id+"'");
			logger.debug("启用用户：[Id:"+id+"]");
			logService.info("用户管理", "启用用户：["+id+"]", getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 禁用
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/disable/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject disable(@PathVariable String id){
		try {
			userService.executeHQL("update User set enabled=false where id='"+id+"'");
			logger.debug(String.format("禁用用户：[Id:%s]", id));
			logService.info("用户管理", String.format("禁用用户：[%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	/**
	 * 密码重置
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/resetPwd/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject resetPwd(@PathVariable String id){
		try {
			String pwd = Md5Util.encode("123456");
			userService.executeHQL("update User set password='"+pwd+"' where id='"+id+"'");
			logService.info("用户管理", String.format("重置密码：[%s]", id), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 编辑用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}", method={RequestMethod.GET})
	public String edit(Model model, @PathVariable String id){ 
		User user = userService.getById_noLazy(id);
		if(ObjectUtil.isNull(user)){
			return ControllerCommon.ERROR;
		}
		//获取用户所在单位
		if(user.getUnits().size()>0){
			Unit unit = user.getUnits().get(0);
			model.addAttribute("unitId", unit.getId());
			model.addAttribute("unitName", unit.getName());
		}else{
			model.addAttribute("unitName", "未分配单位");
		}
		//获取角色列表
		List<String> roleIds = new ArrayList<String>();
		for(Role role : user.getRoles()){
			roleIds.add(role.getId());
		}
		model.addAttribute("roleIds", roleIds);
		//获取角色列表
		List<Role> roleList = roleService.listForEntity("select id, name from Role where unit.id in ("+getCurrentUserUnitIds()+")");
		model.addAttribute("roleList", roleList);
		model.addAttribute("user", user);
		return "system/user/edit";
	}
	
	@RequestMapping(value="/edit/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(User user,String unitId, String[] roleIds){
		try {
			if(StringUtil.isBlank(user.getUserName())){
				return WebResult.error("用户名不能为空！");
			}
			if(StringUtil.isBlank(user.getPassword())){
				return WebResult.error("密码不能为空！");
			}
			String mobile = user.getMobile();
			if(StringUtil.isEmpty(mobile)){
				return WebResult.error("联系方式不能为空");
			}
			//判断用户名是否重复
			int count = userService.count(Restrictions.eq("userName", user.getUserName()), Restrictions.not(Restrictions.eq("id", user.getId())));
			if(count > 0)
				return WebResult.error("用户名 '"+user.getUserName()+"' 已存在！");
			if(!StringUtil.isBlank(user.getEmail())){
				count = userService.count(Restrictions.eq("email", user.getEmail()), Restrictions.not(Restrictions.eq("id", user.getId())));
				if(count > 0)
					return WebResult.error("邮箱 '"+user.getEmail()+"' 已被使用！");
			}
			//判断此联系方式是否存在
			if(mobileIsExist(mobile.trim(),user.getId())){
				return WebResult.error("联系方式 '"+mobile+"' 已存在！");
			}
			User eqUser = userService.getById_noLazy(user.getId());
			
			eqUser.setUserName(user.getUserName());//修改用户名
			eqUser.setRealName(user.getRealName());
			if(!eqUser.getPassword().equals(user.getPassword())){
				eqUser.setPassword(Md5Util.encode(user.getPassword()));
			}
			eqUser.setEmail(user.getEmail());
			eqUser.setMobile(user.getMobile());
			eqUser.setPageStyle(user.getPageStyle());
			eqUser.setQq(user.getQq());
			eqUser.setEnabled(user.isEnabled());
			
			eqUser.getRoles().clear();
			eqUser.getUnits().clear();
			//绑定角色
			for(String roleId : roleIds){
				Role role = new Role();
				role.setId(roleId);
				
				eqUser.getRoles().add(role);
			}
			//绑定单位
			Unit unit = new Unit();
			unit.setId(unitId);
			eqUser.getUnits().add(unit);
			
			userService.merge(eqUser);
			
			logger.debug(String.format("编辑用户：[Id:%s,UserName:%s,Unit:%s]", user.getId(), user.getUserName(), unitId));
			logService.info("用户管理", String.format("编辑用户：[Id:%s,UserName:%s,Unit:%s]", user.getId(), user.getUserName(), unitId), getCurrentUser().getUserName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	/**
	 * 查看用户基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/detail/info/{id}", method={RequestMethod.GET,RequestMethod.POST})
	public String detail_info(Model model, @PathVariable String id){
		User user = userService.getById_noLazy(id);
		if(ObjectUtil.isNull(user))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		model.addAttribute("user", user);
		if(user.getUnits().size()>0){
			model.addAttribute("unitName", user.getUnits().get(0).getName());
		}
		List<String> roles = new ArrayList<String>();
		for(Role role : user.getRoles()){
			roles.add(role.getName());
		}
		model.addAttribute("roles", StringUtil.toString(roles));
		logService.info("用户管理", String.format("查看基本信息：[%s]", id), getCurrentUser().getUserName(), getRequestAddr());
		return "system/user/detail_info";
	}
	
	/**
	 * 查看用户权限
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/detail/right/{id}", method={RequestMethod.GET,RequestMethod.POST})
	public String detail_right(Model model, @PathVariable String id){
		User user = userService.getById_noLazyUnit(id);
		if(ObjectUtil.isNull(user))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		model.addAttribute("user", user);
		if(user.getUnits().size()>0){
			model.addAttribute("unitName", user.getUnits().get(0).getName());
		}
		//获取用户菜单资源
		List<Resource> resList = userService.getUserResList(id);
		List<Resource> firstResList = new ArrayList<Resource>();
		JSONArray jsonResList = new JSONArray();
		for(Resource res : resList){
			res.setRoles(null);//防止延迟加载属性
			if(res.getParentId().equals("-1")){
				firstResList.add(res);
			}
			JSONObject jsonRes = JSONObject.fromObject(res);
			jsonResList.add(jsonRes);
		}
		model.addAttribute("allRes", jsonResList.toString());
		model.addAttribute("resList", firstResList);
		logService.info("用户管理", String.format("查看权限：[%s]", id), getCurrentUser().getUserName(), getRequestAddr());
		return "system/user/detail_right";
	}
	

	@RequestMapping(value = "/edit/realname", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject edit_username(String pk, String name, String value) {
		try {
			userService.executeHQL("update User set realName=? where id=?", value, pk);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/edit/mobile", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject edit_mobile(String pk, String name, String value) {
		try {
			userService.executeHQL("update User set mobile=? where id=?", value, pk);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/edit/email", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject edit_email(String pk, String name, String value) {
		try {
			userService.executeHQL("update User set email=? where id=?", value, pk);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	/**
	 * 验证手机号码是否存在
	 * @param mobile  手机号
	 * @return
	 */
	public boolean mobileIsExist(String mobile){
		SimpleExpression s = Restrictions.eq("mobile", mobile);
		return userService.count(s)>0;
	}
	
	/**
	 * 验证手机号码是否存在
	 * @param mobile  手机号
	 * @param id  主键
	 * @return
	 */
	public boolean mobileIsExist(String mobile,String id){
		SimpleExpression s = Restrictions.eq("mobile", mobile);
		Criterion c = Restrictions.not(Restrictions.eq("id",id));
		return userService.count(s,c)>0;
	}
}
