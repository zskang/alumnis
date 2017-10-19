package com.nethsoft.web.support;

public class Constant {

	//新闻类别
	public static final String NEWS_TYPE_ALMA_MATER = "0";//母校新闻
	public static final String NEWS_TYPE_ALUMNI_DYNAMICS = "1";//校友动态
	public static final String NEWS_TYPE_ALUMNI_ACTIVITY = "2";//校友活动
	public static final String NEWS_TYPE_ALUMNI_DONATIONS = "3";//校友捐赠

	//表alumni_comment_open中的key值
	public static final String KEY_COMMENT_OPEN_ALMA_MATER = "comment_open_alma_mater";//是否公开母校新闻的key值
	public static final String KEY_COMMENT_OPEN_ALUMNI_DYNAMICS = "comment_open_alumni_dynamics";//是否公开校友动态的key值
	public static final String KEY_COMMENT_OPEN_ALUMNI_ACTIVITY = "comment_open_alumni_activity";//是否公开校友活动的key值
	public static final String KEY_COMMENT_OPEN_ALUMNI_DONATIONS = "comment_open_alumni_donations";//是否公开校友捐赠的key值

	//捐赠类别
	public static final String TYPE_ALUMNI_DONATE_PERSONAL = "0";//个人捐款
	public static final String TYPE_ALUMNI_DONATE_TEAM = "1";//团体捐款

	//捐赠方式
	public static final String WAY_ALUMNI_DONATE_ALIPAY = "0";//支付宝
	public static final String WAY_ALUMNI_DONATE_WECHAT = "1";//微信
	public static final String WAY_ALUMNI_DONATE_CASH = "2";//现金

	//捐赠状态
	public static final String STATE_ALUMNI_DONATE_NOT = "0";//未支付
	public static final String STATE_ALUMNI_DONATE_DONE = "1";//已支付

	//新闻内容图片尺寸，宽，高
	public static final int WIDTH_NEWS_CONTENT = 600;
	public static final int HEIGHT_NEWS_CONTENT = 400;
	//新闻预览图片尺寸，宽，高
	public static final int WIDTH_NEW_VIEW = 800;
	public static final int HEIGHT_NEWS_VIEW = 600;

}
