package com.nethsoft.web.entity.system;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author nethsoft
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name = "sys_user_email")
@DynamicUpdate(true)
public class UserEmail implements Serializable{
	
	private static final long serialVersionUID = 3509941384851901401L;
	
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	private String userId;
	@Column
	private String mailType;
	@Column
	private String mailHost;
	@Column
	private Integer mailPort;
	@Column
	private Boolean sslEnable = false;
	@Column
	private String mailUser;
	@Column
	private String mailPass;
	
	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getUserId(){
		return this.userId;
	}
	public void setUserId(String userId){
		this.userId = userId;
	}
	public String getMailType(){
		return this.mailType;
	}
	public void setMailType(String mailType){
		this.mailType = mailType;
	}
	public String getMailHost(){
		return this.mailHost;
	}
	public void setMailHost(String mailHost){
		this.mailHost = mailHost;
	}
	public Integer getMailPort(){
		return this.mailPort;
	}
	public void setMailPort(Integer mailPort){
		this.mailPort = mailPort;
	}
	public Boolean getSslEnable(){
		return this.sslEnable;
	}
	public void setSslEnable(Boolean sslEnable){
		this.sslEnable = sslEnable;
	}
	public String getMailUser(){
		return this.mailUser;
	}
	public void setMailUser(String mailUser){
		this.mailUser = mailUser;
	}
	public String getMailPass(){
		return this.mailPass;
	}
	public void setMailPass(String mailPass){
		this.mailPass = mailPass;
	}
}
