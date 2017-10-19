package com.nethsoft.web.service.system;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.web.entity.system.Button;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.RoleResourceButton;
import com.nethsoft.web.service.BaseService;

@Service
public class RoleService extends BaseService<Role>{
	@Autowired
	private RoleResourceButtonService rrbService;
	@Autowired
	private ButtonService btnService;

	public void save(Role role, String resourceIds, String userIds) {
		JSONArray resources = JSONArray.fromObject(resourceIds);
		List<Resource> resList = new ArrayList<Resource>();//资源列表
		for(int i=0;i<resources.size();i++){
			JSONObject jRes = resources.getJSONObject(i);
			Resource res = new Resource();
			res.setId(jRes.getString("id"));
			
			resList.add(res);
		}
		
		role.setResources(resList);
		role.setCreateTime(getCurrentTime());
		
		this.save(role);
		
		//保存资源按钮列表
		for(int i=0;i<resources.size();i++){
			JSONObject jRes = resources.getJSONObject(i);
			JSONArray btnIds = jRes.getJSONArray("btns");
			
			for(int j=0;j<btnIds.size();j++){
				String btnId = btnIds.getString(j);
				RoleResourceButton rrb = new RoleResourceButton();
				rrb.setRoleId(role.getId());
				rrb.setResId(jRes.getString("id"));
				rrb.setBtnId(btnId);
				
				rrbService.save(rrb);
			}
		}
	}

	/**
	 * 删除角色
	 * 删除角色关联信息，菜单，按钮等。
	 * @param id
	 */
	public void deleteAsRelated(String id) {
		rrbService.executeHQL("delete from RoleResourceButton where roleId = ?", id);
		rrbService.executeSQL("delete from sys_role_res where roleId = ?", id);
		rrbService.executeSQL("delete from sys_user_role where roleId = ?", id);
		this.delete(id);
	}

	/**
	 * 修改角色菜单
	 * @param id
	 * @param resourceIds
	 */
	public void executeEditRoleRes(String id, String resourceIds) {
		Role role = getById(id);
		Hibernate.initialize(role.getResources());
		
		role.getResources().clear();//清空原有菜单
		JSONArray resources = JSONArray.fromObject(resourceIds);
		List<Resource> resList = new ArrayList<Resource>();//资源列表
		for(int i=0;i<resources.size();i++){
			JSONObject jRes = resources.getJSONObject(i);
			Resource res = new Resource();
			res.setId(jRes.getString("id"));
			
			resList.add(res);
		}
		
		role.setResources(resList);
		
		this.update(role);
		//删除原有按钮
		rrbService.executeHQL("delete from RoleResourceButton where roleid='"+role.getId()+"'");
		//保存资源按钮列表
		for(int i=0;i<resources.size();i++){
			JSONObject jRes = resources.getJSONObject(i);
			JSONArray btnIds = jRes.getJSONArray("btns");
			
			for(int j=0;j<btnIds.size();j++){
				String btnId = btnIds.getString(j);
				RoleResourceButton rrb = new RoleResourceButton();
				rrb.setRoleId(role.getId());
				rrb.setResId(jRes.getString("id"));
				rrb.setBtnId(btnId);
				
				rrbService.save(rrb);
			}
		}
	}
	/**
	 * 获取角色信息，立即加载用户
	 * @param id
	 * @return
	 */
	public Role getById_noLazyUser(String id) {
		Role role = getById(id);
		Hibernate.initialize(role.getUsers());
		return role;
	}
	/**
	 * 获取角色信息，立即加载用户
	 * @param id
	 * @return
	 */
	public Role getById_noLazyRes(String id) {
		Role role = getById(id);
		Hibernate.initialize(role.getResources());
		List<Button> allButtons = btnService.list();//获取所有按钮
		List<RoleResourceButton> rrbList = rrbService.list(Restrictions.eq("roleId", role.getId()));
		for(RoleResourceButton rrb : rrbList){
			//找到菜单
			Resource res = null;
			for(Resource r : role.getResources()){
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
			if(ObjectUtil.isNull(btn))
				continue;
			res.getButtons().add(btn);
		}
		return role;
	}

	/**
	 * 立即加载延迟加载属性
	 * @return
	 */
	public List<Role> list_noLazy(Criterion...criterions) {
		List<Role> roleList = this.list(criterions);
		for(Role role : roleList){
			Hibernate.initialize(role.getUnit());
		}
		return roleList;
	}

	public Role getById_noLazy(String id) {
		Role role = getById(id);
		if(ObjectUtil.isNotNull(role)){
			Hibernate.initialize(role.getUnit());
		}
		return role;
	}

}
