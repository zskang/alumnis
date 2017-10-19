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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="sys_user")
@DynamicUpdate(true)
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2308233050003124998L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	@NotNull
	private String userName;
	@Column
	@NotNull
	private String password;
	@Column
	private String realName;
	@Column
	private String mobile;
	@Column
	private String email;
	@Column
	private String qq;
	@Column
	private String pageStyle;
	@Column
	private String layout;
	@Column(name="en_abled")
	private boolean enabled;
	@Column(name="on_line")
	private boolean online;
	@Column
	private String createTime;
	@Column
	private String lastLoginTime;
	@Column
	private String lastLoginIp;
	@Column
	private int lastLoginType;
	@Column
	private String headImage;//头像图片，用于人脸识别
	@Transient
	private boolean isSystemAdmin = false;//是否是系统管理员
	
	@Column
	private String pictureURL;//系统头像  用于app显示头像图片功能
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_user_role",joinColumns={@JoinColumn(name="userid")},inverseJoinColumns={@JoinColumn(name="roleid")})
	private List<Role> roles = new ArrayList<Role>();
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	@JoinTable(name="sys_user_unit",joinColumns={@JoinColumn(name="userid")},inverseJoinColumns={@JoinColumn(name="unitid")})
	private List<Unit> units = new ArrayList<Unit>();
	@Transient
	private List<Resource> resources = new ArrayList<Resource>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getPageStyle() {
		return pageStyle;
	}
	public void setPageStyle(String pageStyle) {
		this.pageStyle = pageStyle;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public int getLastLoginType() {
		return lastLoginType;
	}
	public void setLastLoginType(int lastLoginType) {
		this.lastLoginType = lastLoginType;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Unit> getUnits() {
		return units;
	}
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public String getHeadImage() {
		return headImage;
	}
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
	public boolean isSystemAdmin() {
		return isSystemAdmin;
	}
	public void setSystemAdmin(boolean isSystemAdmin) {
		this.isSystemAdmin = isSystemAdmin;
	}
	
	public String toString() {
		return this.userName + ":" + this.realName;
	}
	public String getPictureURL() {
		return pictureURL;
	}
	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}
	
	
	
}
