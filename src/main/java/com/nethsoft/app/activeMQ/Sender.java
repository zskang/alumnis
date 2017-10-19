package com.nethsoft.app.activeMQ;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.nethsoft.core.util.ObjectUtil;

public class Sender {
	private final String BROKER_URL = "tcp://192.168.0.107:61616";
	private final String DESTINATION = "ns:test";
	
	public void run() throws JMSException{
		Connection conn = null;
		Session session = null;
		try {
			//创建连接工厂
			ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, ActiveMQConnectionFactory.DEFAULT_PASSWORD, BROKER_URL);
			//通过工厂创建一个连接
			conn = factory.createConnection();
			//启动连接
			conn.start();
			//通过连接创建一个session
			session = conn.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			//创建一个消息队列
			Destination destination = session.createQueue(DESTINATION);
			//创建消息制作者
			MessageProducer producer = session.createProducer(destination);
			//设置持久化模式
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			//发送消息
			sendMessage(session, producer);
			//提交
			session.commit();
			
		} catch (JMSException e) {
			e.printStackTrace();
		}finally{
			if(ObjectUtil.isNotNull(conn)) conn.close();
			if(ObjectUtil.isNotNull(session)) session.close();
		}
	}
	
	private void sendMessage(Session session, MessageProducer producer) throws JMSException{
		String message = "我是中文的哦！！";
		TextMessage textMessage = session.createTextMessage(message);
		producer.send(textMessage);
	}
	
	public static void main(String[] args) {
		Sender sender = new Sender();
		try {
			sender.run();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
