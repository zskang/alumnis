package com.nethsoft.web.entity.common.notify.action;

import com.nethsoft.web.entity.common.notify.NotifyAction;
import com.nethsoft.web.entity.common.notify.ShowMode;

public class NotifyLinkAction extends NotifyAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3130677042933534027L;
	
	public enum ModalSize{
		Large,Small
	}
	private String url;
	private ShowMode showMode = ShowMode.Page;// page modal
	private ModalSize modalSize = ModalSize.Large;
	
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
	public ModalSize getModalSize() {
		return modalSize;
	}
	public void setModalSize(ModalSize modalSize) {
		this.modalSize = modalSize;
	}
	@Override
	public String html() {
		String html = "";
		if(this.showMode == ShowMode.Page){
			html += "<a href=\""+this.url+"\">";
		}else if(this.showMode == ShowMode.Modal){
			if(this.modalSize == ModalSize.Large){
				html += "<a href=\"javascript:nspms.__showModal('"+this.url+"',1);\">";
			}else{
				html += "<a href=\"javascript:nspms.__showModal('"+this.url+"',2);\">";
			}
			
		}else{
			html += "<a href=\"javascript:;\">";
		}
		html += "<span class=\"label "+this.backgroudClass+" pull-right mr5\"> "+this.name+" </span>";
		html += "</a>";
		
		return html;
	}
}
