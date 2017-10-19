package com.nethsoft.web.entity.common.notify.message;

import com.nethsoft.web.entity.common.notify.NotifyAction;
import com.nethsoft.web.entity.common.notify.NotifyMessage;
import com.nethsoft.web.entity.common.notify.ShowMode;

public class NotifyLinkMessage extends NotifyMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216461630559090103L;
	
	private String url;
	private ShowMode showMode = ShowMode.Page;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ShowMode getShowMode() {
		return showMode;
	}
	public void setShowMode(ShowMode showMode) {
		this.showMode = showMode;
	}
	
	@Override
	public String html() {
		String html ="";
		if(this.showMode == ShowMode.Page)
			html += "<a href=\""+this.url+"\">";
		else if(this.showMode == ShowMode.NewPage){
			html += "<a href=\""+this.url+"\" target=\"_blank\">";
		}else if(this.showMode == ShowMode.Modal){
			html += "<a href=\"javascript:nspms.__showModal('"+this.url+"',1);\">";
		}else{
			html += "<a href=\"javascript:;\">";
		}
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
}
