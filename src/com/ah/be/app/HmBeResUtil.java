/**
 *@filename		HmBeResUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5  04:21:06
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeResUtil
{
	/**
	 * get a string for the given key from file named by filename
	 * 
	 * @param filename
	 * @param keyStr
	 * @return a string if success find corresponding value, otherwise return ""
	 */
	public static String getString(String filename, String keyStr)
	{
		return AhAppContainer.HmBe.getResModule().getString(filename, keyStr);
	}

	/**
	 * get a string for the given key from default resource file
	 * 
	 * @param keyStr
	 * @return a string if success find corresponding value, otherwise return ""
	 */
	public static String getString(String keyStr)
	{
		return AhAppContainer.HmBe.getResModule().getString(keyStr);
	}

	/**
	 * Finds a localized text message for the given key, filename and agrs.
	 * 
	 * @param filename
	 * @param keyStr
	 *            the key to find the text message for
	 * @param args
	 *            an array of objects to be substituted into the message text
	 * @return
	 */
	public static String getString(String filename, String keyStr, String[] args)
	{
		return AhAppContainer.HmBe.getResModule().getString(filename, keyStr,
			args);
	}

	/**
	 * Finds a localized text message for the given key and agrs in default
	 * resource
	 * 
	 * @param keyStr
	 *            the key to find the text message for
	 * @param args
	 *            an array of objects to be substituted into the message text
	 * @return
	 */
	public static String getString(String keyStr, String[] args)
	{
		return AhAppContainer.HmBe.getResModule().getString(keyStr, args);
	}
}
