package com.nethsoft.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {
	/**
	 * 精度精确到小数点后1位，如果超出1位，则保留小数点后所有位
	 * @param value
	 * @return
	 */
	public static BigDecimal setScale1(BigDecimal value){
		return value.scale()<1?value.setScale(1, RoundingMode.CEILING):value;
	}
	/**
	 * 精度精确到小数点后2位，如果超出2位，则保留小数点后所有位
	 * @param value
	 * @return
	 */
	public static BigDecimal setScale2(BigDecimal value){
		return value.scale()<2?value.setScale(2, RoundingMode.DOWN):value;
	}
	/**
	 * 精度精确到小数点后3位，如果超出3位，则保留小数点后所有位
	 * @param value
	 * @return
	 */
	public static BigDecimal setScale3(BigDecimal value){
		return value.scale()<3?value.setScale(3, RoundingMode.CEILING):value;
	}
	/**
	 * 精度精确到小数点后4位，如果超出4位，则保留小数点后所有位
	 * @param value
	 * @return
	 */
	public static BigDecimal setScale4(BigDecimal value){
		return value.scale()<4?value.setScale(4, RoundingMode.CEILING):value;
	}
}
