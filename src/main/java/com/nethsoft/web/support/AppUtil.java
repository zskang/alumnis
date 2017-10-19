package com.nethsoft.web.support;

import com.nethsoft.core.util.ObjectUtil;

import java.io.Reader;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AppUtil {
	public static String createVerificationCode() {
		Random random = new Random();
		String code="";
		for(int i=0;i<6;i++){
			code+=random.nextInt(10);
		}
		return  code;
	}

	public static String converClobToString(Clob clob) {
    	if(ObjectUtil.isNotNull(clob)){
    		String str="";
    		try
    		{
    			Reader inStreamDoc = clob.getCharacterStream();    
    			char[] tempDoc = new char[(int) clob.length()];   
    			inStreamDoc.read(tempDoc);   
    			inStreamDoc.close();
    			str=new String(tempDoc);
    		}catch(Exception e) {
    			str="";
    		}
    		return str;
    	}
    	return null;
	}

	/**
	 * 锁对象，可以为任意对象
	 */
	private static Object lockObj = "lockerOrder";
	/**
	 * 订单号生成计数器
	 */
	private static long orderNumCount = 0L;
	/**
	 * 每毫秒生成订单号数量最大值
	 */
	private static int maxPerMSECSize=1000;
	/**
	 * 生成非重复订单号，理论上限1毫秒1000个，可扩展
	 *
	 */
	public static String makeOrderNum() {
		try {
			// 最终生成的订单号
			String finOrderNum = "";
			synchronized (lockObj) {
				// 取系统当前时间作为订单号变量前半部分，精确到毫秒
				long nowLong = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
				// 计数器到最大值归零，可扩展更大，目前1毫秒处理峰值1000个，1秒100万
				if (orderNumCount > maxPerMSECSize) {
					orderNumCount = 0L;
				}
				//组装订单号
				String countStr=maxPerMSECSize +orderNumCount+"";
				finOrderNum=nowLong+countStr.substring(1);
				orderNumCount++;
				return finOrderNum;
				// Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
