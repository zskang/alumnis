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
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 角色
 * @author zengchao
 *
 */
@Entity
@Table(name="sys_role")
@DynamicUpdate(true)
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4396185097159608162L;
	
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unitId")
	private Unit unit;
	@Column
	private boolean enabled;
	@Column
	private String descript;
	@Column
	private String createTime;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_role_res",joinColumns={@JoinColumn(name="roleid")},inverseJoinColumns={@JoinColumn(name="resid")})
	@OrderBy("location asc")
	private List<Resource> resources = new ArrayList<Resource>();
	/**
	 * 该角色下所有用户
	 */
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_user_role",joinColumns={@JoinColumn(name="roleid")},inverseJoinColumns={@JoinColumn(name="userid")})
	private List<User> users = new ArrayList<User>();
	
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
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
