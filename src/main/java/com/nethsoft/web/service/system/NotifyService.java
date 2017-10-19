package com.nethsoft.web.service.system;

import java.io.Serializable;
import java.sql.SQLException;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nethsoft.web.entity.common.notify.NotifyMessage;
import com.nethsoft.web.entity.system.Notify;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.entity.system.UserNotify;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.core.util.ObjectUtil;

@Service
public class NotifyService extends BaseService<Notify>{
	
	@Autowired
	private UserNotifyService userNotifyService;
	/**
	 * 保存通知到数据库
	 * @param notifyMessage
	 */
	public void save(NotifyMessage notifyMessage){
		//设置默认值
		if(ObjectUtil.isNull(notifyMessage.getCreateTime()))
			notifyMessage.setCreateTime(getCurrentTime());
		if(ObjectUtil.isNull(notifyMessage.getCreateUser()))
			notifyMessage.setCreateUser(getCurrentUser().getUserName());
		
		Notify notify = new Notify();
		notify.setContent(getBlob(notifyMessage));
		
		//保存通知到数据库
		this.save(notify);
		
		//向消息接受者发送通知
		if(notifyMessage.getRecipients().size()>0){
			for(User user : notifyMessage.getRecipients()){
				UserNotify userNotify = new UserNotify();
				userNotify.setNotify(notify);
				userNotify.setUser(user);
				userNotify.setRecread(false);
				userNotify.setCreateTime(getCurrentTime());
				userNotify.setBusinessId(notifyMessage.getBusinessId());
				
				userNotifyService.save(userNotify);
			}
		}
	}
	
	public NotifyMessage getNotifyById(Serializable id){
		NotifyMessage notifyMessage = null;
		Notify notify = this.getById(id);
		if(ObjectUtil.isNotNull(notify)){
			try {
				notifyMessage = (NotifyMessage) SerializationUtils.deserialize(notify.getContent().getBinaryStream());
				notifyMessage.setId(notify.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return notifyMessage;
	}
	
	/**
	 * 通过业务字段，将通知设为已读！
	 * @param businessId
	 */
	@Transactional
	public void readNotifyMessage(String businessId){
		this.executeHQL("update UserNotify set recread=true where businessId='"+businessId+"' and user.id='"+getCurrentUser().getId()+"'");
	}
}
