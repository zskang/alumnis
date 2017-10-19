package com.nethsoft.web.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 用户通知
 * @author zengchao
 *
 */
@Entity
@Table(name="sys_notify_user")
@DynamicUpdate(true)
public class UserNotify implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3172186830466646459L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;
	@ManyToOne
	@JoinColumn(name = "notifyId")
	private Notify notify;
	@Column
	private String businessId;//业务ID
	@Column
	private String createTime;//创建时间
	@Column
	private boolean recread = false;//是否已读, 默认已读
	@Column
	private String readTime;// 阅读时间
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Notify getNotify() {
		return notify;
	}
	public void setNotify(Notify notify) {
		this.notify = notify;
	}
	public boolean isRecread() {
		return recread;
	}
	public void setRecread(boolean recread) {
		this.recread = recread;
	}
	public String getReadTime() {
		return readTime;
	}
	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
