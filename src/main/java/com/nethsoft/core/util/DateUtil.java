package com.nethsoft.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Date parseDate(String dateStr, String format) {
		Date date = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			String dt = dateStr.replaceAll("-", "-");
			if ((!dt.equals("")) && (dt.length() < format.length())) {
				dt = dt
						+ format.substring(dt.length()).replaceAll(
								"[YyMmDdHhSs]", "0");
			}
			date = dateFormat.parse(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, "yyyy-MM-dd");
	}

	public static Date parseDateTime(String dateStr) {
		return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	public static String format(Date date, String format) {
		String result = "";
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat(format);
				result = dateFormat.format(date);
			}
		} catch (Exception localException) {
		}
		return result;
	}

	public static String format(Date date) {
		return format(date, "yyyy-MM-dd");
	}

	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(1);
	}

	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(2) + 1;
	}

	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(5);
	}

	public static int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(11);
	}

	public static int getMinute(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(12);
	}

	public static int getSecond(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(13);
	}

	public static long getMillis(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	public static String getDate(Date date) {
		return format(date, "yyyy-MM-dd");
	}

	public static String getTime(Date date) {
		return format(date, "HH:mm:ss");
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String now(){
		return getDateTime(new Date());
	}
	/**
	 * 获取当前时间
	 * @param 格式
	 * @return
	 */
	public static String now(String format){
		return format(new Date(), format);
	}
	/**
	 * 格式 yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String getDateTime(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date addDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		long millis = getMillis(date) + day * 24L * 3600L * 1000L;
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	public static Date minDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		long millis = getMillis(date) - day * 24L * 3600L * 1000L;
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	public static int diffDate(Date date, Date date1) {
		return (int) ((getMillis(date) - getMillis(date1)) / 86400000L);
	}

	public static String getMonthBegin(String strdate) {
		Date date = parseDate(strdate);
		return format(date, "yyyy-MM") + "-01";
	}

	public static String getMonthEnd(String strdate) {
		Date date = parseDate(getMonthBegin(strdate));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(2, 1);
		calendar.add(6, -1);
		return formatDate(calendar.getTime());
	}

	public static String formatDate(Date date) {
		return formatDateByFormat(date, "yyyy-MM-dd");
	}

	public static String formatDateByFormat(Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
/**
 * 获取星期
 * @return
 */
	public static String getWeek() {
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		String wk = "";
		switch(week){
		case 1:
			wk = "星期天";
			break;
		case 2:
			wk = "星期一";
			break;
		case 3:
			wk = "星期二";
			break;
		case 4:
			wk = "星期三";
			break;
		case 5:
			wk = "星期四";
			break;
		case 6:
			wk = "星期五";
			break;
		case 7:
			wk = "星期六";
			break;
		}
		return wk;
	}

	/**
	 * 根据当前日期获取多少天之后的日期
	 * @param num
	 * @return
     */
	public static Date dateAdd(int num){
		Calendar calendar =  Calendar.getInstance();
		calendar.add(5,num);
		Date date = calendar.getTime();
		return date;
	}

	/**
	 * 根据日期获取多少天之后的日期
	 * @param num
	 * @return
	 */
	public static Date dateAdd(Date date,int num){
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5,num);
		return calendar.getTime();
	}
}