/**
 *@filename		AhRestoreConstParameters.java
 *@version
 *@author		Frazer
 *@createtime	2007-5-12 11:22:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.restoredb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;

/**
 * @author Frazer
 * @version V1.0.0.0
 */
public class AhRestoreCommons {

	/**
	 * Check whether the column is exist in the table taken as parameter
	 * 
	 * @param a_xmlParser
	 *            - Used for parsing resouce table datas from xml created from
	 *            database backup.
	 * @param a_talbeName
	 *            - Name of table
	 * @param a_colName
	 *            - Name of column
	 * @throws AhRestoreException
	 *             - if error in checking the existence of the column.
	 * @return true column is in the table, false otherwise.
	 */
	public static boolean isColumnPresent(AhRestoreGetXML a_xmlParser,
			String a_talbeName, String a_colName) throws AhRestoreException {
		boolean isColPresent;

		try {
			isColPresent = a_xmlParser.checkColExist(a_colName);
		} catch (Exception ex) {
			String errorMsg = "Check column[" + a_colName + "] for table of "
					+ a_talbeName + " failed";
			// printDebugMsg("Francis", "AhRestoreCommons", "isColumnPresent",
			// errorMsg);

			AhRestoreDBTools.logRestoreMsg(errorMsg);

			throw new AhRestoreException(errorMsg);
		}

		return isColPresent;
	}

	/**
	 * Convert the string value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return String : the valid value
	 */
	public static String convertString(String a_colValue) {
		return a_colValue == null || a_colValue.trim().equalsIgnoreCase("NULL")
				|| a_colValue.trim().equals("") ? "" : a_colValue.trim();
	}
	
	/**
	 * Convert the string value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return String : the valid value (allow start or end with blank)
	 */
	public static String convertStringNoTrim(String a_colValue) {
		return a_colValue == null || a_colValue.equalsIgnoreCase("NULL")
				|| a_colValue.equals("") ? "" : a_colValue;
	}

	/**
	 * Convert the string value parsed from xml to boolean value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return boolean : the boolean value
	 */
	public static boolean convertStringToBoolean(String a_colValue) {
		boolean result = false;
		if (!"".equals(convertString(a_colValue))) {
			if (a_colValue.toLowerCase().startsWith("t"))
				result = true;
		}
		return result;
	}

	/**
	 * Convert the string value parsed from xml to DosType
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return DosType : "0" :DosPrevention.DosType.MAC "1"
	 *         :DosPrevention.DosType.MAC_STATION "2" :DosPrevention.DosType.IP
	 */
	public static DosPrevention.DosType convertStringToDosType(String a_colValue) {
		if ("0".equals(a_colValue)) {
			return DosPrevention.DosType.MAC;
		} else if ("1".equals(a_colValue)) {
			return DosPrevention.DosType.MAC_STATION;
		} else {
			return DosPrevention.DosType.IP;
		}
	}

	/**
	 * Convert the string value parsed from xml to DosAction
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return DosAction : "0" :DosParams.DosAction.ALARM; "1"
	 *         :DosParams.DosAction.DROP; "2" :DosParams.DosAction.DISCONNECT;
	 *         "3" :DosParams.DosAction.BAN; "4"
	 *         :DosParams.DosAction.BAN_FOREVER;
	 */
	public static DosParams.DosAction convertStringToDosAction(String a_colValue) {
		if ("0".equals(a_colValue)) {
			return DosParams.DosAction.ALARM;
		} else if ("1".equals(a_colValue)) {
			return DosParams.DosAction.DROP;
		} else if ("2".equals(a_colValue)) {
			return DosParams.DosAction.DISCONNECT;
		} else if ("3".equals(a_colValue)) {
			return DosParams.DosAction.BAN;
		} else {
			return DosParams.DosAction.BAN_FOREVER;
		}
	}

	/**
	 * Convert the int value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return int : the valid value
	 */
	public static int convertInt(String a_colValue) {
		int intValue;

		if (a_colValue != null) {
			try {
				intValue = Integer.parseInt(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				intValue = 0;
			}
		} else {
			intValue = 0;
		}

		return intValue;
	}

	/**
	 * Convert the double value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @param defaultValue
	 *            :
	 * @return double : the valid value
	 */
	public static double convertDouble(String a_colValue, double defaultValue) {
		double doubleValue;

		if (a_colValue != null) {
			try {
				doubleValue = Double.parseDouble(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				doubleValue = defaultValue;
			}
		} else {
			doubleValue = defaultValue;
		}

		return doubleValue;
	}

	/**
	 * Convert the double value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return double : the valid value
	 */
	public static double convertDouble(String a_colValue) {
		double doubleValue;

		if (a_colValue != null) {
			try {
				doubleValue = Double.parseDouble(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				doubleValue = 0;
			}
		} else {
			doubleValue = 0;
		}

		return doubleValue;
	}

	/**
	 * Convert the long value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return long : the valid value
	 */
	public static long convertLong(String a_colValue) {
		long longValue;

		if (a_colValue != null) {
			try {
				longValue = Long.parseLong(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				longValue = 0;
			}
		} else {
			longValue = 0;
		}

		return longValue;
	}

	/**
	 * Convert the float value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return float : the valid value
	 */
	public static float convertFloat(String a_colValue) {
		float floatValue;

		if (a_colValue != null) {
			try {
				floatValue = Float.parseFloat(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				floatValue = 0;
			}
		} else {
			floatValue = 0;
		}

		return floatValue;
	}

	/**
	 * Convert the string value parsed from xml to valid value,when parsed
	 * abnormal value, then set the default in;
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @param a_default
	 *            :
	 * @return String : the valid value
	 */
	public static String convertString(String a_colValue, String a_default) {
		return a_colValue == null || a_colValue.trim().equalsIgnoreCase("NULL")
				|| a_colValue.trim().equals("") ? a_default : a_colValue.trim();
	}

	/**
	 * Convert the int value parsed from xml to valid value, when parsed
	 * abnormal value, then set the default in;
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @param a_default
	 *            :
	 * @return int : the valid value
	 */
	public static int convertInt(String a_colValue, int a_default) {
		int intValue;

		if (a_colValue != null) {
			try {
				intValue = Integer.parseInt(a_colValue.trim());
			} catch (NumberFormatException nfe) {
				intValue = a_default;
			}
		} else {
			intValue = a_default;
		}

		return intValue;
	}

	public static <T extends Enum<T>> T convertStringToEnum(Class<T> enumType,
			String enumName, T defaultValue) {
		try {
			return Enum.valueOf(enumType, enumName);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private static final SimpleDateFormat DATEFORMAT_WITHMILLSECOND = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	private static final SimpleDateFormat DATEFORMAT_NOMILLSECOND = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * Convert the Date value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @return Date : the valid value
	 */
	public static Date convertDate(String a_colValue) {
		return convertDate(a_colValue, null);
	}

	/**
	 * Convert the Date value parsed from xml to valid value
	 * 
	 * @param a_colValue
	 *            : the value parsed from xml
	 * @param default_value
	 *            :
	 * @return Date : the valid value
	 */
	public static Date convertDate(String a_colValue, Date default_value) {
		if (a_colValue == null || "".equals(a_colValue.trim())
				|| a_colValue.equalsIgnoreCase("NULL")) {
			return default_value;
		}

		try {
			return DATEFORMAT_WITHMILLSECOND.parse(a_colValue);
		} catch (ParseException e) {
			try {
				return DATEFORMAT_NOMILLSECOND.parse(a_colValue);
			} catch (Exception ex) {
				//
				AhRestoreDBTools
						.logRestoreMsg("convert date object error.", ex);
			}
		}

		return default_value;
	}

	/**
	 * This function is used to convert string to long, when failed, it also
	 * will try to convert to date then get the time in ms.
	 * 
	 * @return
	 */
	public static long convertString2Long(String col) {
		long longValue = 0;
		if (col != null) {
			try {
				longValue = Long.parseLong(col.trim());
			} catch (NumberFormatException nfe) {
				Date value = convertDate(col);
				if (null != value) {
					longValue = value.getTime();
				}
			}
		}
		return longValue;
	}

	// feeling 2007-11-09 add
	public static int checkColExist(String col, String[] columns) {
		if (col == null || col.trim().equals("") || columns == null
				|| columns.length <= 0)
			return -1;
		for (int i = 0; i < columns.length; i++)
			if (col.equalsIgnoreCase(columns[i]))
				return i;
		return -1;
	}
}