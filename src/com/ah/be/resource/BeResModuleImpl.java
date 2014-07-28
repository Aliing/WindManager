package com.ah.be.resource;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ah.be.app.BaseModule;
import com.ah.be.common.ConfigUtil;

/**
 * @filename BeResModuleImpl.java
 * @version V1.0.0.0
 * @author juyizhou
 * @createtime 2007-9-10 08:54:18 Copyright (c) 2006-2008 Aerohive Co., Ltd. All
 *             right reserved.
 */
/**
 * modify history*
 */
public class BeResModuleImpl extends BaseModule implements BeResModule
{

	private final String	defaultRes_zh	= "locale.zh.stringresource";

	private final String	defaultRes_en	= "locale.en.stringresource";

	/**
	 * Construct method
	 */
	public BeResModuleImpl()
	{
		setModuleId(13);
		setModuleName("BeResourceModule");

		// get config from config.ini
		setLocale(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_LOCAL,
			ConfigUtil.KEY_LOCAL, "en"));
	}

	private ResourceBundle				defaultRB	= null;

	private Locale						locale		= null;

	private final String				blankStr	= "";

	/**
	 * missed resource list
	 */
	private final Collection<String>	misses		= new HashSet<String>();

	public void setLocale(String locale)
	{
		if (locale.equalsIgnoreCase("zh"))
		{
			this.locale = Locale.CHINA;
		}
		else
			if (locale.equalsIgnoreCase("en"))
			{
				this.locale = Locale.ENGLISH;
			}
	}

	private String getString(ResourceBundle rb, String keyStr)
	{
		if (rb == null || keyStr == null)
		{
			return blankStr;
		}

		String value;
		try
		{
			value = rb.getString(keyStr);
		}
		catch (MissingResourceException e)
		{
			return blankStr;
		}

		if (value == null)
		{
			return blankStr;
		}

		//mark: java read property string as Unicode coding, so we need native2ascii resource files.
//		try
//		{
//			if (this.locale.equals(Locale.CHINA))
//			{
//				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
//			}
//		}
//		catch (UnsupportedEncodingException e)
//		{
//			return blankStr;
//		}

		return value;
	}

	/**
	 * Finds the given resource bundle by it's name.
	 * 
	 * @param aBundleName
	 *            the name of the bundle
	 * @param locale
	 *            the locale.
	 * @return the bundle, return null if not found.
	 */
	private ResourceBundle findResourceBundle(String aBundleName, Locale locale)
	{
		synchronized (misses)
		{
			try
			{
				if (!misses.contains(aBundleName))
				{
					return ResourceBundle.getBundle(aBundleName, locale, Thread
						.currentThread().getContextClassLoader());
				}
			}
			catch (MissingResourceException ex)
			{
				misses.add(aBundleName);
			}
		}

		return null;
	}

	/**
	 * Finds a localized text message for the given key, filename.
	 * 
	 * @param keyStr
	 *            the key to find the text message for
	 * @param filename
	 *            the file whose name to use as the point for the search
	 * @return -
	 */
	public String getString(String filename, String keyStr)
	{
		ResourceBundle rb = findResourceBundle(filename, locale);

		return getString(rb, keyStr);
	}

	/**
	 * Finds a localized text message for the given key in default resource
	 * 
	 * @param keyStr
	 *            the key to find the text message for
	 * @return -
	 */
	public String getString(String keyStr)
	{
		if (defaultRB == null)
		{
			if (locale.equals(Locale.CHINA))
			{
				defaultRB = findResourceBundle(defaultRes_zh, locale);
			}
			else
				if (locale.equals(Locale.ENGLISH))
				{
					defaultRB = findResourceBundle(defaultRes_en, locale);
				}

		}

		return getString(defaultRB, keyStr);
	}

	/**
	 * Finds a localized text message for the given key, filename and agrs.
	 * 
	 * @param filename
	 *            the file whose name to use as the point for the search
	 * @param keyStr
	 *            the key to find the text message for
	 * @param args
	 *            an array of objects to be substituted into the message text
	 * @return -
	 */
	public String getString(String filename, String keyStr, String[] args)
	{
		String message = getString(filename, keyStr);
		MessageFormat mf = buildMessageFormat(message, locale);
		return mf.format(args);
	}

	/**
	 * Finds a localized text message for the given key and agrs in default
	 * resource
	 * 
	 * @param keyStr
	 *            the key to find the text message for
	 * @param args
	 *            an array of objects to be substituted into the message text
	 * @return -
	 */
	public String getString(String keyStr, String[] args)
	{
		String message = getString(keyStr);
		MessageFormat mf = buildMessageFormat(message, locale);
		return mf.format(args);
	}

	private final Map<MessageFormatKey, MessageFormat>	messageFormats	= new HashMap<MessageFormatKey, MessageFormat>();

	private MessageFormat buildMessageFormat(String pattern, Locale locale)
	{
		MessageFormatKey key = new MessageFormatKey(pattern, locale);
		MessageFormat format = messageFormats.get(key);
		if (format == null)
		{
			format = new MessageFormat(pattern);
			format.setLocale(locale);
			format.applyPattern(pattern);
			messageFormats.put(key, format);
		}

		return format;
	}

	private class MessageFormatKey
	{
		final String	pattern;

		final Locale	locale;

		MessageFormatKey(String pattern, Locale locale)
		{
			this.pattern = pattern;
			this.locale = locale;
		}

		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (!(o instanceof MessageFormatKey))
				return false;

			final MessageFormatKey messageFormatKey = (MessageFormatKey) o;

			if (locale != null ? !locale.equals(messageFormatKey.locale)
				: messageFormatKey.locale != null)
				return false;
			if (pattern != null ? !pattern.equals(messageFormatKey.pattern)
				: messageFormatKey.pattern != null)
				return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result = (pattern != null ? pattern.hashCode() : 0);
			result = 29 * result + (locale != null ? locale.hashCode() : 0);
			return result;
		}
	}

	/**
	 * Clears all the internal lists.
	 */
	public void reset()
	{
		clearDefaultResourceBundles();

		synchronized (misses)
		{
			misses.clear();
		}

		synchronized (messageFormats)
		{
			messageFormats.clear();
		}
	}

	private void clearDefaultResourceBundles()
	{
		defaultRB = null;
	}

}