package com.nethsoft.web.entity.campus;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**   
 * @Title: Entity
 * @Description: 学生表
 * @author cf
 * @date 2016-06-23 11:05:24
 * @version V1.0   
 *
 */
@Entity
@Table(name = "CAMPUS_STUDENT", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class CampusStudent implements java.io.Serializable {
	/**id*/
	private String id;
	/**学号*/
	private String code;
	/**密码*/
	private String password;
	/**身份证号*/
	private String idcard;
	/**手机号*/
	private String mobile;
	/**邮箱*/
	private String email;
	/**环信账号*/
	private String imusername;
	/**环信密码*/
	private String impassword;
	/**最后登录时间*/
	private String lastlogintime;
	/**姓名*/
	private String name;
	/**照片*/
	private String photo;
	/**昵称*/
	private String nickname;
	/**注册时间*/
	private String registertime;

	public CampusStudent() {
	}

	public CampusStudent(String name,String mobile,String email,String password,String imusername,
						 String impassword,String registertime) {
		this.name = name;
		this.mobile = mobile;
		this.email = email;
		this.password = password;
		this.imusername = imusername;
		this.impassword = impassword;
		this.registertime = registertime;
	}

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  id
	 */
	@Id
	@GeneratedValue(generator = "campusStudentGenerator")
	@GenericGenerator(name = "campusStudentGenerator", strategy = "uuid")
	@Column(name ="ID",nullable=false,length=50)
	public String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  id
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  学号
	 */
	@Column(name ="CODE",nullable=true,length=20)
	public String getCode(){
		return this.code;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  学号
	 */
	public void setCode(String code){
		this.code = code;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  密码
	 */
	@Column(name ="PASSWORD",nullable=false,length=50)
	public String getPassword(){
		return this.password;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  密码
	 */
	public void setPassword(String password){
		this.password = password;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  身份证号
	 */
	@Column(name ="IDCARD",nullable=true,length=18)
	public String getIdcard(){
		return this.idcard;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  身份证号
	 */
	public void setIdcard(String idcard){
		this.idcard = idcard;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  手机号
	 */
	@Column(name ="MOBILE",nullable=true,scale=0)
	public String getMobile(){
		return this.mobile;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  手机号
	 */
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  邮箱
	 */
	@Column(name ="EMAIL",nullable=true,length=30)
	public String getEmail(){
		return this.email;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  邮箱
	 */
	public void setEmail(String email){
		this.email = email;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  环信账号
	 */
	@Column(name ="IM_USERNAME",nullable=false,length=50)
	public String getImusername(){
		return this.imusername;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  环信账号
	 */
	public void setImusername(String imusername){
		this.imusername = imusername;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  环信密码
	 */
	@Column(name ="IM_PASSWORD",nullable=false,length=16)
	public String getImpassword(){
		return this.impassword;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  环信密码
	 */
	public void setImpassword(String impassword){
		this.impassword = impassword;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  最后登录时间
	 */
	@Column(name ="LAST_LOGIN_TIME",nullable=true,length=20)
	public String getLastlogintime(){
		return this.lastlogintime;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  最后登录时间
	 */
	public void setLastlogintime(String lastlogintime){
		this.lastlogintime = lastlogintime;
	}

	@Column(name ="NAME",nullable=false,length=30)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name ="PHOTO",nullable=true,length=50)
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Column(name ="NICKNAME",nullable=true,length=30)
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name ="REGISTER_TIME",nullable=true,length=20)
	public String getRegistertime() {
		return registertime;
	}

	public void setRegistertime(String registertime) {
		this.registertime = registertime;
	}
}
