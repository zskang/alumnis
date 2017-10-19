package com.nethsoft.web.controller.system;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.common.notify.NotifyMessage;
import com.nethsoft.web.service.system.UserNotifyService;

@Controller
@RequestMapping(value="/system/notify")
public class NotifyController extends BaseController<Object>{
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "system/notify/";
	@Autowired
	private UserNotifyService userNotifyService;
	
	/**
	 * 获取通知（未读/已读）
	 * @param model
	 * @param pageBean
	 * @param history
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET,RequestMethod.POST})
	public String index(Model model, PageBean pageBean){
		List<NotifyMessage> list = userNotifyService.getUnReadNotifyMessage(pageBean);
		model.addAttribute("list", list);
		model.addAttribute("unreadCount", userNotifyService.count(Restrictions.eq("user", getCurrentUser()), Restrictions.eq("recread", false)));
		return TPL + "index";
	}
	
	/**
	 * 新通知
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/new", method = { RequestMethod.GET, RequestMethod.POST })
	public String newNotify(Model model, PageBean pageBean) {
		List<NotifyMessage> list = userNotifyService.getUnReadNotifyMessage(pageBean);
		model.addAttribute("list", list);
		return TPL + "new";
	}
	
	/**
	 * 历史通知
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/history", method = { RequestMethod.GET, RequestMethod.POST })
	public String history(Model model, PageBean pageBean) {
		pageBean.setRows(2);
		List<NotifyMessage> list = userNotifyService.getReadNotifyMessage(pageBean);
		model.addAttribute("list", list);
		return TPL + "history";
	}
	
	/**
	 * 获取未读通知数量
	 */
	@RequestMapping(value="/count", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String notifyCount(){
		String html = "";
		int count = userNotifyService.count(Restrictions.eq("user", getCurrentUser()), Restrictions.eq("recread", false));
		if(count > 0){
			html += "document.write(\"<div class='badge badge-top bg-danger animated flash'><span>"+count+"</span></div>\");";
		}
		return html;
	}
	
	/**
	 * 标题用未读通知内容
	 * @return
	 */
	@RequestMapping(value="/header", method={RequestMethod.GET,RequestMethod.POST})
	public String header(Model model){
		List<NotifyMessage> list = userNotifyService.getUnReadNotifyMessage();
		model.addAttribute("list", list);
		logger.debug(getCurrentUser().getUserName()+"获取通知!");
		return TPL + "header";
	}
	
	/**
	 * 阅读通知
	 */
	@RequestMapping(value="/read", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject read(String id){
		try {
			userNotifyService.executeHQL("update UserNotify set recread=true, readTime='"+getCurrentTime()+"' where id='"+id+"'");
			logger.debug(getCurrentUser().getUserName()+"阅读了通知:"+id);
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error(e);
		}
	}
}
