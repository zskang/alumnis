package com.nethsoft.web.service.system;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.nethsoft.orm.query.PageBean;
import com.nethsoft.orm.query.QueryParams;
import com.nethsoft.web.entity.common.notify.NotifyMessage;
import com.nethsoft.web.entity.system.UserNotify;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.core.util.ObjectUtil;

@Service
public class UserNotifyService extends BaseService<UserNotify>{
	/**
	 * 获取已读消息
	 * @return
	 */
	public List<NotifyMessage> getReadNotifyMessage(){
		List<NotifyMessage> list = new ArrayList<NotifyMessage>();
		QueryParams param = new QueryParams();
		param.addCriterion(Restrictions.eq("user", getCurrentUser()));
		param.addCriterion(Restrictions.eq("recread", true));
		param.addOrders(Order.desc("createTime"));
		List<UserNotify> userNotifyList = this.list(param);
		for(UserNotify notify : userNotifyList){
			if(ObjectUtil.isNotNull(notify.getNotify().getContent())){
				try {
					NotifyMessage notifyMessage = (NotifyMessage) SerializationUtils.deserialize(notify.getNotify().getContent().getBinaryStream());
					notifyMessage.setId(notify.getId());
					notifyMessage.setReadTime(notify.getReadTime());
					list.add(notifyMessage);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 获取未读消息
	 * @return
	 */
	public List<NotifyMessage> getUnReadNotifyMessage(){
		List<NotifyMessage> list = new ArrayList<NotifyMessage>();
		QueryParams param = new QueryParams();
		param.addCriterion(Restrictions.eq("user", getCurrentUser()));
		param.addCriterion(Restrictions.eq("recread", false));
		param.addOrders(Order.desc("createTime"));
		List<UserNotify> userNotifyList = this.list(param);
		for(UserNotify notify : userNotifyList){
			if(ObjectUtil.isNotNull(notify.getNotify().getContent())){
				try {
					NotifyMessage notifyMessage = (NotifyMessage) SerializationUtils.deserialize(notify.getNotify().getContent().getBinaryStream());
					notifyMessage.setId(notify.getId());
					list.add(notifyMessage);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}

	
	/**
	 * 分页获取已读通知
	 * @param pageBean
	 * @return
	 */
	public List<NotifyMessage> getReadNotifyMessage(PageBean pageBean) {
		List<NotifyMessage> list = new ArrayList<NotifyMessage>();
		pageBean.addCriterion(Restrictions.eq("user", getCurrentUser()));
		pageBean.addCriterion(Restrictions.eq("recread", true));
		pageBean.addOrder(Order.desc("createTime"));
		List<UserNotify> userNotifyList = this.listByPage(pageBean);
		for(UserNotify notify : userNotifyList){
			if(ObjectUtil.isNotNull(notify.getNotify().getContent())){
				try {
					NotifyMessage notifyMessage = (NotifyMessage) SerializationUtils.deserialize(notify.getNotify().getContent().getBinaryStream());
					notifyMessage.setId(notify.getId());
					notifyMessage.setReadTime(notify.getReadTime());
					list.add(notifyMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}

	/**
	 * 分页获取未读通知
	 * @param pageBean
	 * @return
	 */
	public List<NotifyMessage> getUnReadNotifyMessage(PageBean pageBean) {
		List<NotifyMessage> list = new ArrayList<NotifyMessage>();
		pageBean.addCriterion(Restrictions.eq("user", getCurrentUser()));
		pageBean.addCriterion(Restrictions.eq("recread", false));
		pageBean.addOrder(Order.desc("createTime"));
		List<UserNotify> userNotifyList = this.listByPage(pageBean);
		for(UserNotify notify : userNotifyList){
			if(ObjectUtil.isNotNull(notify.getNotify().getContent())){
				try {
					NotifyMessage notifyMessage = (NotifyMessage) SerializationUtils.deserialize(notify.getNotify().getContent().getBinaryStream());
					notifyMessage.setId(notify.getId());
					list.add(notifyMessage);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
}
