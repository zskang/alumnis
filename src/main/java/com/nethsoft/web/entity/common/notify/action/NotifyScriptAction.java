package com.nethsoft.web.entity.common.notify.action;

import java.util.ArrayList;
import java.util.List;

import com.nethsoft.web.entity.common.notify.NotifyAction;

public class NotifyScriptAction extends NotifyAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -144740491973499637L;
	private String scriptUrl;
	private String method;
	private List<String> params = new ArrayList<String>();
	public String getScriptUrl() {
		return scriptUrl;
	}
	public void setScriptUrl(String scriptUrl) {
		this.scriptUrl = scriptUrl;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	//增加参数
	public void addParam(String param){
		this.params.add(param);
	}
	@Override
	public String html() {
		String html = "";
		html = "<script type='text/javascript' src='"+this.scriptUrl+"'></script>";//引入脚本
		String param = "";
		for(String p : this.params){
			param += ",'"+p+"'";
		}
		param = param.length()>0?param.substring(1):param;
		html += "<a href=\"javascript:"+this.method+"("+param+");\">";
		html += "<span class=\"label "+this.backgroudClass+" pull-right mr5\"> "+this.name+" </span>";
		html += "</a>";
		
		return html;
	}
}
