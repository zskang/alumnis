package com.nethsoft.web.service.system;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nethsoft.orm.query.Condition;
import com.nethsoft.web.entity.system.Role;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.BaseService;

@Service
public class UnitService extends BaseService<Unit>{
	@Autowired
	private ResourceService resService;
	@Autowired
	private RoleService roleService;

	public void deleteAndRelated(String id) {
		//获取当前菜单的父级菜单
		String pid = this.getById(id).getParentId();
		//删除机构相关数据
		this.executeSQL("delete from sys_unit_res where unitId = ?", id);
		this.executeSQL("delete from sys_user_unit where unitId = ?", id);
		//删除此机构下的二级菜单
		List<Unit> unitList = this.listForEntity("select id from Unit where parentId in (select id from Unit where parentId = '"+id+"')");
		for(Unit unit : unitList)
			this.executeHQL("delete from Unit where id = ?", unit.getId());
		this.executeHQL("delete from Unit where parentId = ?", id);
		this.executeHQL("delete from Unit where id = ?", id);
		
		//判断
		int count = this.count(Restrictions.eq("parentId", pid));
		if(count == 0){
			this.executeHQL("update Unit set hasChildren=false where id = ?", pid);
		}
	}
	/**
	 * 获取单位 立即加载下属资源
	 * @param id
	 * @return
	 */
	public Unit getById_noLazyRes(String id) {
		Unit unit = this.getById(id);
		Hibernate.initialize(unit.getResources());
		if(unit.isSystem()){//如果是系统机构，拥有所有菜单资源
			unit.setResources(resService.list(Order.asc("location")));
		}
		return unit;
	}
	
	/**
	 * 获取当前机构下的所有用户
	 * @return
	 */
	public List<User> listUser() {
		List<Unit> units = this.list(Restrictions.in("id", getCurrentUserUnitIdList()));
		List<User> userList = new ArrayList<User>();
		for(Unit unit : units){
			Hibernate.initialize(unit.getUsers());
			List<User> users = unit.getUsers();
			for(User user : users){
				if(!userList.contains(user))
					userList.add(user);
			}
		}
		return userList;
	}
	/**
	 * 获取当前机构下所有角色
	 * @return
	 */
	public List<Role> listRole() {
		List<Role> roleList = roleService.listForEntity("select id,name from Role where unitId in ("+getCurrentUserUnitIds()+")");
		return roleList;
	}
	
	
	/**
	 * 根据类型获取机构
	 * @return
	 */
	public List<Unit> listByType(String type) {
		List<Unit> unitList = this.list(String.format("select u from Unit u where u.type='%s' order by u.code", type));
		return unitList;
	}
	
	public List<Unit> list_noLazyRes(Condition conditon) {
		List<Unit> units = super.list(conditon);
		
		for(Unit unit : units){
			Hibernate.initialize(unit.getResources());
		}
		return units;
	}

}
