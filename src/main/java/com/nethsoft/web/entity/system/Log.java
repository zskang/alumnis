package com.nethsoft.web.entity.system;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_log")
@DynamicUpdate(true)
public class Log implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3509941384851901401L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	private int type;
	@Column
	private String content;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=2,max=50)
	private String userName;
	@Column(name="logIp")
	@Length(min=0,max=50)
	private String iP;
	@Column(name="logTime")
	@Length(min=0,max=50)
	private String time;
	@Column(name="manipulateName")
	@Length(min=0,max=100)
	private String manipulateName;
	@Column(name="className")
	@Length(min=0,max=50)
	private String className;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getManipulateName() {
		return manipulateName;
	}
	public void setManipulateName(String manipulateName) {
		this.manipulateName = manipulateName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIP() {
		return iP;
	}
	public void setIP(String iP) {
		this.iP = iP;
	}
	public String getTime() {
		return time;
	}
	public void setTime(java.util.Date time) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");
		this.time = formater.format(time);
	}
}
