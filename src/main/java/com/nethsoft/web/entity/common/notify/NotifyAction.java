package com.nethsoft.web.entity.common.notify;

import java.io.Serializable;

public class NotifyAction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7566865863348005291L;
	protected String id;// ID
	protected String name;// 名称，用来显示
	protected String backgroudClass = "label-warning";// 背景样式
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
	public String getBackgroudClass() {
		return backgroudClass;
	}
	public void setBackgroudClass(String backgroudClass) {
		this.backgroudClass = backgroudClass;
	}
	
	public String html(){
		return "<a href=\"javascript:;\"><span class=\"label "+this.backgroudClass+" pull-right mr5\"> "+this.name+" </span></a>";
	}
}
