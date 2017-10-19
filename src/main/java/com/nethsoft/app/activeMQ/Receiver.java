package com.nethsoft.app.activeMQ;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.nethsoft.core.util.ObjectUtil;

public class Receiver {
	private final String BROKER_URL = "tcp://192.168.0.107:61616";
	private final String DESTINATION = "ns:test";
	
	private void run() throws JMSException{
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
			//创建消息消费者
			MessageConsumer consumer = session.createConsumer(destination);
			//接受消息
			while(true){
				Message message = consumer.receive(1000 * 100);
				
				TextMessage text = (TextMessage) message;
				if(ObjectUtil.isNotNull(text)){
					text.acknowledge();
				}else
					break;
			}
			//提交会话
			session.commit();
		}catch (JMSException e) {
			e.printStackTrace();
		}finally{
			if(ObjectUtil.isNotNull(conn)) conn.close();
			if(ObjectUtil.isNotNull(session)) session.close();
		}
	}
	
	public static void main(String[] args) {
		Receiver receiver = new Receiver();
		try {
			receiver.run();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
