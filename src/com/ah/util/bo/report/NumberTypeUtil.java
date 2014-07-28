package com.ah.util.bo.report;

import java.math.BigDecimal;
import java.math.MathContext;

public class NumberTypeUtil {
	
	public static final String NUMBER_TYPE_BIGDECIMAL = "BigDecimal";
	public static final String NUMBER_TYPE_DOUBLE = "Double";
	public static final String NUMBER_TYPE_FLOAT = "Float";
	public static final String NUMBER_TYPE_LONG = "Long";
	public static final String NUMBER_TYPE_INTEGER = "Integer";
	public static final String NUMBER_TYPE_SHORT = "Short";
	public static final String NUMBER_TYPE_BYTE = "Byte";
	public static final String NUMBER_TYPE_NOT_NUMBER = "Not Number";
	
	public static final MathContext DEFAULT_MC = new MathContext(2);
	
	public static String getNumberType(Object numObj) {
		if (numObj instanceof BigDecimal) {
			return NUMBER_TYPE_BIGDECIMAL;
		} else if (numObj instanceof Double) {
			return NUMBER_TYPE_DOUBLE;
		} else if (numObj instanceof Float) {
			return NUMBER_TYPE_FLOAT;
		} else if (numObj instanceof Long) {
			return NUMBER_TYPE_LONG;
		} else if (numObj instanceof Integer) {
			return NUMBER_TYPE_INTEGER;
		} else if (numObj instanceof Short) {
			return NUMBER_TYPE_SHORT;
		} else if (numObj instanceof Byte) {
			return NUMBER_TYPE_BYTE;
		}
		
		return NUMBER_TYPE_NOT_NUMBER;
	}
	
	
	public static BigDecimal convertToBigDecimal(Object numObj) {
		return convertToBigDecimal(numObj, getNumberType(numObj), DEFAULT_MC);
	}
	
	public static boolean canConvertedToBigDecimal(Object numObj) {
		if (numObj != null) {
			String type = getNumberType(numObj);
			if (!NUMBER_TYPE_NOT_NUMBER.equals(type)) {
				return true;
			}
		}
		return false;
	}
	private static final int DEFAULT_FLOAT_SCALE = 2;
	public static BigDecimal prepareBigDecimalScale(BigDecimal numObj, int scale) {
		return numObj.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
	public static BigDecimal convertToBigDecimal(Object numObj, String objType) {
		return convertToBigDecimal(numObj, objType, DEFAULT_FLOAT_SCALE, DEFAULT_MC);
	}
	public static BigDecimal convertToBigDecimal(Object numObj, String objType, int scale) {
		return convertToBigDecimal(numObj, objType, scale, DEFAULT_MC);
	}
	public static BigDecimal convertToBigDecimal(Object numObj, String objType, MathContext mc) {
		return convertToBigDecimal(numObj, objType, DEFAULT_FLOAT_SCALE, mc);
	}
	public static BigDecimal convertToBigDecimal(Object numObj, String objType, int scale, MathContext mc) {
		if (numObj == null
				|| NUMBER_TYPE_NOT_NUMBER.equals(objType)) {
			return new BigDecimal(0);
		}
		if (NUMBER_TYPE_BIGDECIMAL.equals(objType)) {
			return (BigDecimal)numObj;
		} else if (NUMBER_TYPE_LONG.equals(objType)) {
			return new BigDecimal((Long)numObj);
		} else if (NUMBER_TYPE_INTEGER.equals(objType)) {
			return new BigDecimal((Integer)numObj);
		} else if (NUMBER_TYPE_SHORT.equals(objType)) {
			return new BigDecimal((Short)numObj);
		} else if (NUMBER_TYPE_BYTE.equals(objType)) {
			return new BigDecimal((Byte)numObj);
		} else if (NUMBER_TYPE_DOUBLE.equals(objType)) {
			return prepareBigDecimalScale(new BigDecimal(Double.valueOf(numObj.toString())), scale);
		} else if (NUMBER_TYPE_FLOAT.equals(objType)) {
			return prepareBigDecimalScale(new BigDecimal(Float.valueOf(numObj.toString())), scale);
		}
		
		return new BigDecimal(0);
	}
	
	
	public static boolean canConvertedToDouble(Object numObj) {
		if (numObj != null) {
			String type = getNumberType(numObj);
			if (NUMBER_TYPE_DOUBLE.equals(type)) {
				return true;
			}
		}
		return false;
	}
	public static Double convertToDouble(Object numObj, String objType) {
		if (numObj == null
				|| !NUMBER_TYPE_DOUBLE.equals(objType)) {
			return 0.0;
		}
		
		return (Double)numObj;
	}
	
	
	public static boolean canConvertedToFloat(Object numObj) {
		if (numObj != null) {
			String type = getNumberType(numObj);
			if (NUMBER_TYPE_FLOAT.equals(type)) {
				return true;
			}
		}
		return false;
	}
	public static Float convertToFloat(Object numObj, String objType) {
		if (numObj == null
				|| !NUMBER_TYPE_FLOAT.equals(objType)) {
			return 0.0f;
		}
		
		return (Float)numObj;
	}
}
