package com.nethsoft.web.entity.common.notify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.system.User;

public class NotifyMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9201813289329541578L;
	protected String id;
	protected String businessId;//业务ID
	protected String logo = "ti-bell";// Logo图标
	protected String logoBackgroundClass = "bg-warning";//logo 背景样式
	protected String title;//标题
	protected String content;//内容
	protected String createTime;//创建时间
	protected String createUser;//创建人
	protected String readTime;// 阅读时间
	protected List<User> recipients = new ArrayList<User>();//接收人
	protected List<NotifyAction> actions = new ArrayList<NotifyAction>();// 动作列表
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getLogoBackgroundClass() {
		return logoBackgroundClass;
	}
	public void setLogoBackgroundClass(String logoBackgroundClass) {
		this.logoBackgroundClass = logoBackgroundClass;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public List<User> getRecipients() {
		return recipients;
	}
	public void setRecipients(List<User> recipients) {
		this.recipients = recipients;
	}
	public String getReadTime() {
		return readTime;
	}
	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}
	/**
	 * 增加接受者ID
	 * @param 用户ID
	 */
	public void addRecipient(User user){
		this.recipients.add(user);
	}
	/**
	 * 增加接受者ID
	 * @param 用户ID
	 */
	public void addRecipient(String userId){
		User user = new User();
		user.setId(userId);
		this.recipients.add(user);
	}
	public List<NotifyAction> getActions() {
		return actions;
	}
	public void setActions(List<NotifyAction> actions) {
		this.actions = actions;
	}
	/**
	 * 增加动作
	 * @param action
	 */
	public void addAction(NotifyAction action){
		this.actions.add(action);
	}
	/**
	 * 生成HTML
	 * @return
	 */
	public String html(){
		String html = "<a href=\"javascript:;\">";
		html += "<div class=\"pull-left mt5 mr15\">";
		html += "<div class=\"circle-icon "+this.logoBackgroundClass+"\">";
		html += "<i class=\""+this.logo+"\"></i>";//logo
		html += "</div>";
		html += "</div>";
		html += "<div class=\"m-body\">";
		html += "<div class=\"\">";
		html += "<small><b>"+this.title+"</b></small>";//标题
		for(NotifyAction action : this.actions)
			html += action.html();
		html += "</div>";
		html += "<div>";//内容
		html += "<span>"+this.content+"</span>";
		html += "</div>";
		html += "<span class=\"time small\">"+getTimeSpan()+"</span>";
		html += "</div>";
		html += " </a>";
		
		return html;
	}
	/**
	 * 计算时间间隔
	 * @return
	 */
	protected String getTimeSpan() {
		String desc = "0分钟前";
		if(!StringUtil.isEmpty(this.createTime)){
			//计算时间间隔
			Date date = DateUtil.parseDate(this.createTime, "yyyy-MM-dd HH:mm:ss");//创建时间
			
			Long timespan = System.currentTimeMillis() - date.getTime();
			
			Long sec = timespan/1000;
			if(sec < 60)//计算秒
				desc = sec.intValue()+"秒前";
			else{
				Long min = sec/60;
				if(min < 60)
					desc = min.intValue() + "分钟前";
				else{//计算小时
					Long hour = min/60;
					if(hour < 24)
						desc = hour.intValue()+"小时前";
					else{
						Long day = hour / 24;
						desc = day.intValue() + "天前";
					}
				}
			}
		}
		return desc;
	}
}
