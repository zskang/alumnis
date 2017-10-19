package com.nethsoft.plugins;

import java.lang.reflect.Method;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.nethsoft.core.plugin.IPlugin;
import com.nethsoft.core.plugin.annotation.Plugin;
import com.nethsoft.core.plugin.annotation.PluginAfterAdvice;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.web.entity.common.notify.NotifyMessage;
import com.nethsoft.web.entity.system.Email;
import com.nethsoft.web.entity.system.User;
import com.nethsoft.web.service.system.EmailService;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.NotifyService;

@Plugin(value="邮件发送插件", autoloading=false)
@PluginAfterAdvice(expression="execution(* com.nethsoft.web.service.system.EmailService.save(..)) ")
public class MailSenderPlugin extends TimerTask implements IPlugin{
	private Logger logger = Logger.getLogger(this.getClass());
	private Properties props;//邮件参数
	private long period = 0;//检查邮件的周期
	private Timer timer = null;
	@Autowired
	private EmailService emailService;
	@Autowired
	private LogService logService;
	@Autowired
	private NotifyService notifyService;

	public boolean init(String[] args) {
		//初始化参数
		props = ApplicationCoreConfigHelper.getPropertyGroup("mail.");
		if(props.size() > 0){
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
			
			timer = new Timer("MailSender");
			period = Long.parseLong(props.getProperty("mail.period"));
			timer.schedule(this, period, period);
			
			logger.info("启动邮件发送程序。。。");
			return true;
		}
		return false;
	}

	public void destory(String[] args) {
		super.cancel();//清除任务
	}

	public void before(Method method, Object[] params, Object obj) {
		
	}

	public void afterReturning(Object returnValue, Method method,
			Object[] params, Object obj) {
		//重置邮件发送休眠时间
		period = Long.parseLong(props.getProperty("mail.period"));
		synchronized (this) {
			notify();//激活线程
		}
	}

	@Override
	public void run() {
		try {
			Criterion[] criterions = {Restrictions.eq("state", 1)};
			//查询待发送邮件
			List<Email> list = emailService.list(criterions ,Order.desc("weight"), Order.asc("createTime"));
			//如果没有待发送邮件，那么处理发送失败的邮件
			if(list.size() == 0){
				Criterion[] criterions2 = {Restrictions.eq("state", 3)};
				emailService.list(criterions2, Order.desc("weight"), Order.asc("createTime"));
			}
			//如果没有邮件，则延长刷新周期
			if(list.size() == 0){
				synchronized (this) {
					wait(period+=period);
				}
				return;
			}
			final String username = props.getProperty("mail.smtp.username");
			final String password = props.getProperty("mail.smtp.password");  
			  
			  Session session = Session.getDefaultInstance(props, new Authenticator(){  
			      protected PasswordAuthentication getPasswordAuthentication() {  
			          return new PasswordAuthentication(username, password);  
			      }
		      }); 
			List<User> senders = new ArrayList<User>();//获取所有邮件发送人
			for(Email email : list){
				Message msg = new MimeMessage(session);
				msg.addFrom(InternetAddress.parse(username,false));//发件人
				for(String recipient : email.getRecipients()){//增加收件人
					msg.addRecipients(RecipientType.TO, InternetAddress.parse(recipient,false));
				}
				msg.setSubject(email.getTitle());
				msg.setText(email.getContent());
				msg.setSentDate(DateUtil.parseDate(email.getCreateTime()));
				try {
					//执行发送
					Transport.send(msg);
					
					email.setState(2);
					
					if(!senders.contains(email.getCreateUser())){
						senders.add(email.getCreateUser());
					}
				} catch (Exception e) {
					email.setState(3);//标记为发送失败
				}
				email.setSendTime(DateUtil.now());
				emailService.merge(email);
			}
			
			//发送到通知中心
			NotifyMessage notify = new NotifyMessage();
			for(User user : senders){//增加通知接收人
				notify.addRecipient(user);
			}
			notify.setTitle("邮件发送成功!");
			notify.setContent("您的邮件已经全部发送！");
			notify.setLogo("ti-email");
			notify.setLogoBackgroundClass("bg-primary");
			notify.setCreateUser("system");
			notify.setCreateTime(DateUtil.now());
			
			notifyService.save(notify);
			
		} catch (Exception e) {
			e.printStackTrace();
			logService.error("邮件发送插件", "邮件发送失败！", "system", "system");
		}
	}

}
