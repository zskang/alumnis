package com.nethsoft.web.entity.campus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**   
 * @Title: Entity
 * @Description: 验证码表
 * @author cf
 * @date 2016-06-23 11:05:24
 * @version V1.0   
 *
 */
@Entity
@Table(name = "CAMPUS_VER_CODE", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class CampusVerCode implements java.io.Serializable{
	/**id*/
	private String id;
	
	private String mobile;
	
	
	private String code;
	
	
	private String createTime;

	public CampusVerCode(){}
	
	public CampusVerCode(String mobile,String code,String createTime) {
		this.mobile=mobile;
		this.code=code;
		this.createTime=createTime;
	}
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=50)
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	@Column(name ="MOBILE")
	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name ="CODE")
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	@Column(name ="CREATE_TIME")
	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
}
