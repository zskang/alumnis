package com.nethsoft.web.support.alumni;

import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件发送
 */
public class AlumniMailUtil {

    private static final Logger logger = Logger.getLogger(AlumniMailUtil.class);
    private static final String from = "xyh@ustb.edu.cn";
    private static final String fromName = "北科大校友之家";
    private static final String charSet = "utf-8";
    private static final String username = "xyh@ustb.edu.cn";
    private static final String password = "ustbxiaoyouhui";
    private static Map<String, String> hostMap = new HashMap<String, String>();

    static {
        // 126
        hostMap.put("smtp.126", "smtp.126.com");
        // qq
        hostMap.put("smtp.qq", "smtp.qq.com");
        // 163
        hostMap.put("smtp.163", "smtp.163.com");
        // sina
        hostMap.put("smtp.sina", "smtp.sina.com.cn");
        // tom
        hostMap.put("smtp.tom", "smtp.tom.com");
        // 263
        hostMap.put("smtp.263", "smtp.263.net");
        // yahoo
        hostMap.put("smtp.yahoo", "smtp.mail.yahoo.com");
        // hotmail
        hostMap.put("smtp.hotmail", "smtp.live.com");
        // gmail
        hostMap.put("smtp.gmail", "smtp.gmail.com");
        hostMap.put("smtp.port.gmail", "465");
        //合肥睿帮
        hostMap.put("smtp.regpang", "smtp.ym.163.com");
        //北科大校友
        hostMap.put("smtp.ustb", "smtp.ustb.edu.cn");
    }

    public static String getHost(String email) throws Exception {
        Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
        Matcher matcher = pattern.matcher(email);
        String key = "unSupportEmail";
        if (matcher.find()) {
            key = "smtp." + matcher.group(1);
        }
        if (hostMap.containsKey(key)) {
            return hostMap.get(key);
        } else {
            throw new Exception("unSupportEmail");
        }
    }

    public static int getSmtpPort(String email) throws Exception {
        Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
        Matcher matcher = pattern.matcher(email);
        String key = "unSupportEmail";
        if (matcher.find()) {
            key = "smtp.port." + matcher.group(1);
        }
        if (hostMap.containsKey(key)) {
            return Integer.parseInt(hostMap.get(key));
        } else {
            return 25;
        }
    }

    /**
     * 发送普通邮件【单个】
     *
     * @param toMailAddr 收信人地址
     * @param subject    email主题
     * @param message    发送email信息
     */
    public static boolean sendCommonMail(String toMailAddr, String subject, String message) {
        HtmlEmail hemail = new HtmlEmail();
        try {
            hemail.setHostName(getHost(from));
            hemail.setSmtpPort(getSmtpPort(from));
            hemail.setCharset(charSet);
            hemail.addTo(toMailAddr);
            hemail.setFrom(from, fromName);
            hemail.setAuthentication(username, password);
            hemail.setSubject(subject);
            hemail.setMsg(message);
            hemail.send();
            return true;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * 发送普通邮件【批量】
     * @param toMailAddrArr  收信人地址数组
     * @param subject  email主题
     * @param message   发送email信息
     * @return
     */
    public static boolean sendCommonMail(String[] toMailAddrArr, String subject, String message) {
        HtmlEmail hemail = new HtmlEmail();
        try {
            hemail.setHostName(getHost(from));
            hemail.setSmtpPort(getSmtpPort(from));
            hemail.setCharset(charSet);
            hemail.addTo(toMailAddrArr);
            hemail.setFrom(from, fromName);
            hemail.setAuthentication(username, password);
            hemail.setSubject(subject);
            hemail.setMsg(message);
            hemail.send();
            return true;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

}