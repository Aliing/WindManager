/* 
 * $RCSfile: BeResModule_CWPImpl.java,v $ 
 * $Revision: 1.2 $ 
 * $Date: 2012/11/19 06:50:30 $ 
 * 
 * Copyright (C) 2012 Aerohive, Inc. All rights reserved. 
 * 
 * This software is the proprietary information of Aerohive, Inc. 
 * Use is subject to license terms. 
 */  
package com.ah.be.resource;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** 
 * <p>Title: BeResModule_CWPImpl</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2012</p>  
 * @author xxu
 * @mail xxu@aerohive.com 
 * @version 1.0 
 */
public class BeResModule_CWPImpl {

	private final String	defaultRes_zh	= "locale.zh.stringresource";

	private final String	defaultRes_en	= "locale.en.stringresource";
	
	private final String    defaultRes_fr   = "locale.fr.stringresource";
	
	private final String    defaultRes_de   = "locale.de.stringresource";
	
	private final String	defaultRes_es	= "locale.es.stringresource";

	private final String	defaultRes_ko	= "locale.ko.stringresource";
	
	private final String    defaultRes_it   = "locale.it.stringresource";
	
	private final String    defaultRes_nl   = "locale.nl.stringresource";
	
	private final String    defaultRes_zh_Hant   = "locale.zh_Hant.stringresource";

	private ResourceBundle				defaultRB;

	private Locale						locale;

	private final String				blankStr	= "";

	/**
	 * missed resource list
	 */
	private final Collection<String>	misses		= new HashSet<>();

	public void setLocale(String locale)
	{
		if (locale.equalsIgnoreCase("zh"))
		{
			this.locale = Locale.CHINA;
		}
		else if (locale.equalsIgnoreCase("en"))
		{
			this.locale = Locale.ENGLISH;
		}
		else if (locale.equalsIgnoreCase("fr"))
		{
			this.locale = Locale.FRANCE;
		}
		else if (locale.equalsIgnoreCase("de"))
		{
			this.locale = Locale.GERMAN;
		}
		else if (locale.equalsIgnoreCase("ko"))
		{
			this.locale = Locale.KOREA;
		}
		else if (locale.equalsIgnoreCase("nl"))
		{
			this.locale = new Locale("nl","DU");
		}
		else if (locale.equalsIgnoreCase("es"))
		{
			this.locale = new Locale("es","SP");
		}
		else if (locale.equalsIgnoreCase("zh"))
		{
			this.locale = Locale.TAIWAN;
		}
		else if (locale.equalsIgnoreCase("it"))
		{
			this.locale = Locale.ITALIAN;
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
	
	public String getString(String keyStr,Locale locale)
	{
		if (locale.equals(Locale.CHINA))
		{
			defaultRB = findResourceBundle(defaultRes_zh, locale);
		}
		else if (locale.equals(Locale.ENGLISH))
		{
			defaultRB = findResourceBundle(defaultRes_en, locale);
		}
		else if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH))
		{
			defaultRB = findResourceBundle(defaultRes_fr, locale);
		}
		else if (locale.equals(Locale.GERMAN))
		{
			defaultRB = findResourceBundle(defaultRes_de, locale);
		}
		else if (locale.equals(Locale.KOREA))
		{
			defaultRB = findResourceBundle(defaultRes_ko, locale);
		}
		else if (locale.getLanguage().equals("nl") )
		{
			defaultRB = findResourceBundle(defaultRes_nl, locale);
		}
		else if (locale.getLanguage().equals("es"))
		{
			defaultRB = findResourceBundle(defaultRes_es, locale);
		}
		else if (locale.equals(Locale.TAIWAN))
		{
			defaultRB = findResourceBundle(defaultRes_zh_Hant, locale);
		}
		else if (locale.equals(Locale.ITALIAN))
		{
			defaultRB = findResourceBundle(defaultRes_it, locale);
		}

		return getString(defaultRB, keyStr);
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
	public String getString(String filename, String keyStr,Locale locale1)
	{
		ResourceBundle rb = findResourceBundle(filename, locale1);

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
			else if (locale.equals(Locale.ENGLISH))
			{
				defaultRB = findResourceBundle(defaultRes_en, locale);
			}
			else if (locale.equals(Locale.FRANCE))
			{
				defaultRB = findResourceBundle(defaultRes_fr, locale);
			}
			else if (locale.equals(Locale.GERMAN))
			{
				defaultRB = findResourceBundle(defaultRes_de, locale);
			}
			else if (locale.equals(Locale.KOREA))
			{
				defaultRB = findResourceBundle(defaultRes_ko, locale);
			}
			else if (locale.getLanguage().equals("nl") )
			{
				defaultRB = findResourceBundle(defaultRes_nl, locale);
			}
			else if (locale.getLanguage().equals("es"))
			{
				defaultRB = findResourceBundle(defaultRes_es, locale);
			}
			else if (locale.equals(Locale.TAIWAN))
			{
				defaultRB = findResourceBundle(defaultRes_zh_Hant, locale);
			}
			else if (locale.equals(Locale.ITALIAN))
			{
				defaultRB = findResourceBundle(defaultRes_it, locale);
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

	private final Map<MessageFormatKey, MessageFormat>	messageFormats	= new HashMap<>();

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

		@Override
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

		@Override
		public int hashCode()
		{
			int result = (pattern != null ? pattern.hashCode() : 0);
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