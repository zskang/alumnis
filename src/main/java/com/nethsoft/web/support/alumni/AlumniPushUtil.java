package com.nethsoft.web.support.alumni;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import com.nethsoft.web.support.PushUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public class AlumniPushUtil extends PushUtil {
    private static final Logger logger = Logger.getLogger(AlumniPushUtil.class);

	private static final String appKey ="c04bd80ed724ade24e7d1458";
	private static final String masterSecret = "31b2c3b84a38cc5f6cb4abd9";

    private static JPushClient jPushClient =null;

    static {
        jPushClient=new JPushClient(masterSecret, appKey, null, config);
    }

    /**
     * 广播式推送
     * @param title  标题
     * @param content 内容
     * @param extras 额外参数
     * @return
     */
    public static String pushAll(String title,String content,Map<String,String> extras){
        PushPayload payload = buildPushObject_android_and_ios(title,content,extras);
        try {
            PushResult result = jPushClient.sendPush(payload);
            logger.info("Got result - " + result);
            return "true";
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
            return "连接失败";
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
            logger.info("Msg ID: " + e.getMsgId());
            if(e.getErrorCode()==1011){
                return "没有满足条件的推送目标";
            }
            return "false";
        }
    }

    /**
     * 根据标签推送
     * @param title  标题
     * @param content  内容
     * @param tag  标签
     * @return
     */
    public static String pushWithTag(String title,String content,Map<String,String> extras,String...tag){
        PushPayload payload = buildPushObject_android_and_ios_tag(title,content,extras,tag);
        try {
            PushResult result = jPushClient.sendPush(payload);
            logger.info("Got result - " + result);
            return "true";
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
            return "连接失败";
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
            logger.info("Msg ID: " + e.getMsgId());
            if(e.getErrorCode()==1011){
                return "没有满足条件的推送目标";
            }
            return "false";
        }
    }

    /**
     * 根据别名推送
     * @param title  标题
     * @param content  内容
     * @param alias  别名
     * @return
     */
    public static String pushWithAlias(String title,String content,String alias,Map<String,String> extras){
        PushPayload payload = buildPushObject_android_and_ios_alias(title,content,alias,extras);
        try {
            PushResult result = jPushClient.sendPush(payload);
            logger.info("Got result - " + result);
            return "true";
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
            return "连接失败";
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
            logger.info("Msg ID: " + e.getMsgId());
            if(e.getErrorCode()==1011){
                return "没有满足条件的推送目标";
            }
            return "false";
        }
    }

}
