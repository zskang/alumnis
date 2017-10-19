package com.nethsoft.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name="sys_unit")
@DynamicUpdate(true)
public class Unit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2858286280946376201L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	@NotNull
	private String name;
	@Column
	@NotNull
	private String code;
	@Column
	private String contact;
	@Column
	private String mobile;
	@Column
	private String address;
	@Column
	private String email;
	@Column
	private String web;
	@Column
	private boolean system = false;
	@Column
	private boolean enabled = false;
	@Column
	private String parentId = "-1";
	@Column
	private boolean hasChildren;
	@Column
	private String createTime;
	@Column
	private String type;//SGO定制：给予机构内置几种类型：学校，年级，班级，部门
	// 机构下用户
	@ManyToMany(mappedBy="units",cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@OrderBy("userName asc")
	private List<User> users = new ArrayList<User>();
	
	// 机构拥有的菜单
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_unit_res",joinColumns={@JoinColumn(name="unitId")},inverseJoinColumns={@JoinColumn(name="resId")})
	@OrderBy("location asc")
	private List<Resource> resources = new ArrayList<Resource>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public boolean isSystem() {
		return system;
	}
	public void setSystem(boolean system) {
		this.system = system;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
