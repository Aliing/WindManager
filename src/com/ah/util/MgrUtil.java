package com.ah.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.image.imageconfig.ImageConfig;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.sync.SyncTaskTimer;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.RoutingProfilePolicyRule;
import com.ah.bo.performance.ComplianceResult;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;
import com.ah.util.devices.impl.Device;
import com.ah.xml.hiveprofile.XmlHiveProfile;
import com.ah.xml.hiveprofile.XmlHiveProfiles;
import com.ah.xml.navigation.XmlNavigationNode;
import com.ah.xml.navigation.XmlNavigationTree;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

/*
 * @author Chris Scheers
 */

public class MgrUtil {

	private static final Tracer								log					= new Tracer(
																						MgrUtil.class
																								.getSimpleName());

	private static final ActionSupport						actionSupport		= new ActionSupport();

	private static final SimpleDateFormat					excelDateTimeFormat	= new SimpleDateFormat(
																						"MM-dd-yyyy HH:mm:ss");

	private static final Random								rand				= new Random();

	// private static final String source =
	// "!#$%()*+,-./0123456789:;=@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	/*
	 * Cached set of resource bundles
	 */
	private static ConcurrentMap<String, ResourceBundle>	resourceBundles;

	public static ResourceBundle getBundle(String bundle) {
		if (resourceBundles == null) {
			resourceBundles = new ConcurrentHashMap<>();
		}
		ResourceBundle b = resourceBundles.get(bundle);
		if (b != null) {
			return b;
		}
		b = ResourceBundle.getBundle(bundle);
		// Thread-safe addition
		resourceBundles.putIfAbsent(bundle, b);
		return b;
	}

	public static Object getObject(String bundle, String property) {
		try {
			ResourceBundle b = getBundle(bundle);
			return b.getObject(property);
		} catch (MissingResourceException e) {
			log.error("getObject", "Resource " + (bundle) + " missing.", e);
			return null;
		}
	}

	public static String getString(String bundle, String property) {
		Object obj = getObject(bundle, property);
		if (!(obj instanceof String)) {
			log.error("getString", "Wrong type or missing resource for " + bundle + "/" + property);
			return null;
		}
		return (String) obj;
	}

	public static String getExcelDateTimeString(Date time) {
		return excelDateTimeFormat.format(time);
	}

	/**
	 * get time representation in specific time zone for label in Excel document
	 *
	 * @param time
	 *            time value to be converted
	 * @param timeZone
	 *            specified time zone
	 * @return string of time
	 *
	 * @author Joseph Chen
	 */
	public static String getExcelDateTimeString(Date time, TimeZone timeZone) {
		TimeZone oldTimeZone = excelDateTimeFormat.getTimeZone();
		excelDateTimeFormat.setTimeZone(timeZone);
		String timeString = excelDateTimeFormat.format(time);
		excelDateTimeFormat.setTimeZone(oldTimeZone);

		return timeString;
	}

	public static String getEnumString(String property) {
		return getString("resources.hmEnums", property);
	}

	public static String getResourceString(String property) {
		return getString("resources.hmResources", property);
	}
	
	public static String getMessageString(String property) {
		return getString("resources.hmMessages", property);
	}

	public static EnumItem[] enumItems(String prefix, int[] enumKeys, int start) {
		EnumItem[] enumItems = new EnumItem[enumKeys.length - start];
		for (int i = 0; i < enumKeys.length - start; i++) {
			enumItems[i] = new EnumItem(enumKeys[i + start], getEnumString(prefix
					+ enumKeys[i + start]));
		}
		return enumItems;
	}

	public static EnumItem[] enumItems(String prefix, int[] enumKeys) {
		return enumItems(prefix, enumKeys, 0);
	}

	public static EnumItem[] enumItems(Device value, short... models) {
		List<Device> allDevices = AhConstantUtil.getDeviceObjects(Device.ALL);
		List<EnumItem> enumItems = new ArrayList<>();
		Arrays.sort(models);
		for (Device allDevice : allDevices) {
			short model = AhConstantUtil.getModelByDevice(allDevice);
			if (models.length > 0 && Arrays.binarySearch(models, model) < 0) continue;
			enumItems.add(new EnumItem(model, AhConstantUtil.getString(value, model)));
		}
		Collections.sort(enumItems);
		return enumItems.toArray(new EnumItem[enumItems.size()]);
	}

	public static String getUserMessage(String code) {
		return getUserMessage(code, (String[]) null);
	}

	public static String getUserMessage(String code, String param) {
		return getUserMessage(code, new String[] { param });
	}

	/**
	 * Convert '3.3.1.0' style to '3.3r1' to display.
	 *
	 * @param softVer
	 *            -
	 * @return -
	 */
	public static String getHiveOSDisplayVersion(String softVer, boolean... showBID) {
		String s;
		if (null != softVer) {
//			s = MgrUtil.getEnumString("enum.hiveAp.version." + softVer);
			String[] vers = StringUtils.split(softVer, '.');
			if (vers.length >= 3) {
				return vers[0] + "." + vers[1] + "r" + vers[2] + (vers.length >= 4 && showBID.length > 0 && showBID[0] ? ("." + vers[3]): "");
			}
		}
		s = softVer;
		return s;
	}

	/*
	 * Get string resource from resource files mentioned in struts.properties
	 * struts.custom.i18n.resources
	 */
	public static String getUserMessage(String code, String[] params) {
		String userMessage;
		Locale locale = actionSupport.getLocale();

		if (locale != null) {
			userMessage = actionSupport.getText(code, params);
		} else {
			locale = getLocale();
			userMessage = LocalizedTextUtil.findDefaultText(code, locale, params);
		}

		return userMessage;
	}

	/*
	 * Gets the Locale. If no locale was ever specified the platform's
	 * {@link java.util.Locale#getDefault() default locale} is used.
	 */
	public static Locale getLocale() {
		return Locale.getDefault();
	}

	/*
	 * If HmException, use error code to translate exception into a user message, else use
	 * getMessage of innermost wrapped exception.
	 */
	public static String getUserMessage(Exception e) {
		if (e instanceof HmException) {
			HmException he = (HmException) e;
			return getUserMessage(he.errorCode, he.params);
		}
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		String message = cause.getMessage();
		if (message == null || message.isEmpty()) {
			message = cause.toString();
		}
		if (e instanceof NullPointerException){
			return getUserMessage(HmMessageCodes.OPERATION_INVALID, message);
		}
		return getUserMessage(HmMessageCodes.UNKNOWN_ERROR, message);
	}

	public static void logExceptionCause(Exception e) {
		Throwable cause = e;
		int level = 1;
		while (cause != null) {
			log.error("logExceptionCause", "Level: " + level++ + ": " + cause.getClass().getName());
			if(cause instanceof SQLException){
				int nlevel = 1;
				SQLException nextCause = ((SQLException) cause).getNextException();
				while (nextCause != null){
					log.error("logSQLExceptionCause", "Level: "+(level-1)+ ": " + cause.getClass().getName() +
							 " NextExceptionCause: NextLevel: " + nlevel++ +": " + nextCause.getClass().getName()+": " + nextCause.getMessage());
					nextCause = nextCause.getNextException();
				}
			}
			cause = cause.getCause();
		}
	}

	/*
	 * Session attribute get
	 */
	public static Object getSessionAttribute(String key) {
		Object result = null;
		ActionContext ac = ActionContext.getContext();
		if(ac != null){
			Map<String,Object> session = ac.getSession();
			if(session != null){
				result = session.get(key);
			}
		}
		
		return result;
	}

	/*
	 * Session attribute set
	 */
	public static void setSessionAttribute(String key, Object value) {
		ActionContext.getContext().getSession().put(key, value);
	}

	/*
	 * Session attribute remove
	 */
	public static void removeSessionAttribute(String key) {
		ActionContext.getContext().getSession().remove(key);
	}

	/*
	 * XML Marshalling
	 */
	public static void marshal(XmlHiveProfile xmlHiveProfile) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("com.ah.xml.hiveprofile");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		XmlHiveProfiles xmlHiveProfiles = new XmlHiveProfiles();
		xmlHiveProfiles.getProfile().add(xmlHiveProfile);

		m.marshal(xmlHiveProfiles, System.out);
	}

	public static void marshal(XmlNavigationNode xmlNavigationNode) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("com.ah.xml.navigation");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		XmlNavigationTree xmlNavigationTree = new XmlNavigationTree();
		xmlNavigationTree.setTree(xmlNavigationNode);

		m.marshal(xmlNavigationTree, System.out);
	}

	public static XmlNavigationTree unmarshal(String filename) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("com.ah.xml.navigation");
		Unmarshaller m = jc.createUnmarshaller();

		return (XmlNavigationTree) m.unmarshal(new File(filename));
	}
	
	/** image config file marshal/unmarshal start */
	
	public static void marshal(ImageConfig image) throws Exception {
		JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.image.imageconfig");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		String filePath = AhDirTools.getImageConfigFilePath();
		FileOutputStream output = new FileOutputStream(filePath);
		try{
			m.marshal(image, output);
		}catch(Exception e){
			log.error("Write ImageConfig.xml error", e);
		}finally{
			if(output != null){
				try{
					output.close();
				}catch(Exception ex){
					log.error("IO close error", ex);
				}
			}
		}
	}
	
	public static String marshalString(ImageConfig image) {
		StringWriter stringWriter = new StringWriter();
		try{
			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.image.imageconfig");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(image, stringWriter);
			return stringWriter.toString();
		}catch(Exception e){
			log.error("marshal(ImageConfig image)", "Write ImageConfig.xml error", e);
		}finally{
			if(stringWriter != null){
				try{
					stringWriter.close();
				}catch(Exception ex){
					log.error("marshal(ImageConfig image)", "IO close error", ex);
				}
			}
		}
		return null;
	}
	
	public static ImageConfig unmarshalImageConfig(File file) {
		try{
			if(!file.exists()){
				return null;
			}
			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.image.imageconfig");
			Unmarshaller m = jc.createUnmarshaller();

			
			return (ImageConfig) m.unmarshal(file);
		}catch(Exception e){
			log.error("load "+file.getPath()+" error", e);
		}
		return null;
	}
	
	public static ImageConfig unmarshalImageConfig(String content) {
		try{
			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.image.imageconfig");
			Unmarshaller m = jc.createUnmarshaller();
			ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
			return (ImageConfig) m.unmarshal(stream);
		}catch(Exception e){
			log.error(e);
		}
		return null;
	}
	
	/** end */

	/*
	 * Find object in array
	 */
	public static boolean contains(Object[] names, Object name) {
		for (Object obj : names) {
			if (obj.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Log array of objects
	 */
	public static void log(Tracer log, String label, Object[] strings) {
		if (strings != null) {
			for (Object string : strings) {
				log.info("log", label + string);
			}
		}
	}

	/**
	 * Turn a byte array into a char array containing a printable hex representation of the bytes.
	 * Each byte in the source array contributes a pair of hex digits to the output array.
	 *
	 * @param src
	 *            -
	 * @return -
	 */
	private static char[] hexDump(byte[] src) {
		char buf[] = new char[src.length * 2];
		for (int b = 0; b < src.length; b++) {
			String byt = Integer.toHexString(src[b] & 0xFF);
			if (byt.length() < 2) {
				buf[b * 2] = '0';
				buf[b * 2 + 1] = byt.charAt(0);
			} else {
				buf[b * 2] = byt.charAt(0);
				buf[b * 2 + 1] = byt.charAt(1);
			}
		}
		return buf;
	}

	public static String digest(String clearText) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte clearBytes[] = new byte[clearText.length()];
		char[] clearChars = clearText.toCharArray();
		for (int i = 0; i < clearChars.length; i++) {
			clearBytes[i] = (byte) clearChars[i];
		}
		char digestChars[] = hexDump(md.digest(clearBytes));
		return new String(digestChars);
	}

	public static Object getAttributeValue(Object target, String attributeName) {
		String methodName = "get" + attributeName.substring(0, 1).toUpperCase()
				+ attributeName.substring(1);
		try {
			Method method = target.getClass().getDeclaredMethod(methodName);
			return method.invoke(target);
		} catch (Exception e) {
			log.error("getAttributeValue", "Get attribute value failed.", e);
		}
		return null;
	}

	public static Long getLongRequestParameter(String parameterName) {
		String[] parameter = (String[]) ActionContext.getContext().getParameters().get(
				parameterName);
		if (parameter == null || parameter.length == 0) {
			return null;
		}
		return Long.valueOf(parameter[0]);
	}

	/*-
	public static Timer getMemoryMonitorTask(int seconds) {
		TimerTask memoryMonitorTask = new TimerTask() {
			@Override
			public void run() {
				MgrUtil.setTimerName(getClass().getSimpleName());
				Runtime.getRuntime().gc();
				log.info("memoryMonitorTask", "\n\t---- Total memory: "
						+ Runtime.getRuntime().totalMemory() + "\n\t----- Free memory: "
						+ Runtime.getRuntime().freeMemory() + "\n\t------ Max memory: "
						+ Runtime.getRuntime().maxMemory());
			}
		};
		Timer timer = new Timer();
		timer.schedule(memoryMonitorTask, 1000 * seconds, 1000 * seconds);
		return timer;
	}*/

	public static ScheduledExecutorService getMemoryMonitorScheduler(long initialDelay,
			long period, TimeUnit timeUnit) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				MgrUtil.setTimerName(getClass().getSimpleName());
				try {
					Runtime runtime = Runtime.getRuntime();
					runtime.gc();
					List<MemoryPoolMXBean> mps = ManagementFactory.getMemoryPoolMXBeans();
					StringBuilder info = new StringBuilder(1024);
					for (MemoryPoolMXBean mp : mps) {
						info.append("\n").append(mp.getName()).append(" Usage: Init-").append(
							covertToMemoryString(mp.getUsage().getInit())).append(", Used-").append(
							covertToMemoryString(mp.getUsage().getUsed())).append(", Max-").append(
							covertToMemoryString(mp.getUsage().getMax()));

					}
					info.append("\n");
					log.info("MemoryMonitorTask", info.toString());

					MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
					MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
					MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
					StringBuilder infoBuf = new StringBuilder().append("\nGeneral Memory Usage:")
							.append("\n\t------ Total Memory: ").append(covertToMemoryString(runtime.totalMemory()))
							.append("\n\t------- Free Memory: ").append(covertToMemoryString(runtime.freeMemory()))
							.append("\n\t-------- Max Memory: ").append(covertToMemoryString(runtime.maxMemory()))
							.append("\n\nHeap Memory Usage:").append("\n\t-- Committed Memory: ")
							.append(covertToMemoryString(heapMemoryUsage.getCommitted())).append(
									"\n\t---- Initial Memory: ").append(covertToMemoryString(heapMemoryUsage.getInit()))
							.append("\n\t------- Used Memory: ").append(covertToMemoryString(heapMemoryUsage.getUsed()))
							.append("\n\t-------- Max Memory: ").append(covertToMemoryString(heapMemoryUsage.getMax()))
							.append("\n\nNon-Heap Memory Usage:").append(
									"\n\t-- Committed Memory: ").append(
											covertToMemoryString(nonHeapMemoryUsage.getCommitted())).append(
									"\n\t---- Initial Memory: ").append(
											covertToMemoryString(nonHeapMemoryUsage.getInit())).append(
									"\n\t------- Used Memory: ").append(
											covertToMemoryString(nonHeapMemoryUsage.getUsed())).append(
									"\n\t-------- Max Memory: ")
							.append(covertToMemoryString(nonHeapMemoryUsage.getMax()));
					log.info("MemoryMonitorTask", infoBuf.toString());

					EntityManagerFactoryImpl emf;
					emf = (EntityManagerFactoryImpl) HibernateUtil
							.getEntityManagerFactory();
					Statistics stats = emf.getSessionFactory().getStatistics();
					StringBuilder statsInfo = new StringBuilder(256);
					String[] names = stats.getSecondLevelCacheRegionNames();
					long elementCount = 0, hitCount = 0, missCount = 0, memoryUsed = 0;
					for(String name: names) {
						SecondLevelCacheStatistics secondStats =  stats.getSecondLevelCacheStatistics(name);
						if(secondStats != null) {
							elementCount += secondStats.getElementCountInMemory();
							hitCount += secondStats.getHitCount();
							missCount += secondStats.getMissCount();
							memoryUsed += secondStats.getSizeInMemory();
						}
					}
					statsInfo.append("\n,Hibernate second level cache:")
						.append("\t Element Count:").append(elementCount)
						.append("\t Hit Count:").append(hitCount)
						.append("\t Miss count:").append(missCount)
						.append("\t Use memory:").append((int)(memoryUsed/1024)).append("K");
					log.info("MemoryMonitorTask", statsInfo.toString());

					printAllThreads();
					printAllThreads2();
				} catch (Exception e) {
					log.error("MemoryMonitorTask", "MemoryMonitor Exception", e);
				} catch (Error e) {
					log.error("MemoryMonitorTask", "MemoryMonitor Error", e);
				}
			}
		}, initialDelay, period, timeUnit);
		return scheduler;
	}
	

	private static String covertToMemoryString(long memValue){
		return covertToMemoryString(memValue, (byte)1);
	}

	/**
	 * convert memory value to more readable string
	 * @param memValue -
	 * @param unitType 0-B, 1-KB, 2-MB, 3-GB
	 * @return -
	 */
	public static String covertToMemoryString(long memValue, byte unitType) {
		String unitName;

		switch(unitType){
		case 0:
			unitName = " B";
			break;
		case 1:
			unitName = " KB";
			break;
		case 2:
			unitName = " MB";
			break;
		case 3:
			unitName = " GB";
			break;
		default:
			unitType = 1;
			unitName = " KB";
			break;
		}

		for(int i = 0; i < unitType; i++){
			memValue /= 1000;
		}

		long mod;
		String result = null;

		String e3s;
		while(memValue > 0){
			mod = memValue % 1000;
			memValue /= 1000;

			if(memValue>0){
				e3s = String.valueOf(1000 + mod).substring(1);
			}else{
				e3s = String.valueOf(mod);
			}
			if (result == null) {
				result = e3s + unitName;
			} else {
				result = e3s + "," + result;
			}
		}

		return result;
	}

	public static String getRandomString(int len, int charLimit) {
		String str = null;
		switch (charLimit) {
		case 1:
			str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			break;
		case 2:
			str = "1234567890";
			break;
		case 3:
			str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			break;
		case 4:
			// str = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			str = "!#$%&'()*+,-./:;<=>@[\\]^_`{|}~";
			break;
		case 5:
			// str =
			// "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&'()*+,-./:;<=>@[\\]^_`{|}~";
			break;
		case 6:
			// str = "1234567890!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			str = "1234567890!#$%&'()*+,-./:;<=>@[\\]^_`{|}~";
			break;
		case 7:
			// str =
			// "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!#$%&'()*+,-./:;<=>@[\\]^_`{|}~";
			break;
		case 8:
			str = "1234567890ABCDEF";
			break;
		}

		char[] sur = str.toCharArray();
		char[] buf = new char[len];
		for (int i = 0; i < len; i++) {
			buf[i] = sur[rand.nextInt(str.length())];
		}
		return new String(buf);
	}

	public static int checkPasswordStrength(String passwd) {
		int upper = 0, lower = 0, numbers = 0, special = 0, length;
		int intScore = 0;
		int strVerdict = 0;
		Pattern p;
		Matcher m;
		if (passwd == null) {
			return strVerdict;
		}
		// PASSWORD LENGTH
		length = passwd.length();
		// length 16 or more
		if (length > 15) {
			intScore = (intScore + 18);
			// length between 8 and 15
		} else if (length > 7) {
			intScore = (intScore + 12);
			// length between 5 and 7
		} else if (length > 4) {
			intScore = (intScore + 6);
			// length 4 or less
		} else if (length > 0) {
			intScore = (intScore + 3);
		}

		// LETTERS
		p = Pattern.compile(".??[a-z]");
		m = p.matcher(passwd);
		// [verified] at least one lower case letter
		while (m.find()) {
			lower += 1;
		}
		if (lower > 0) {
			intScore = (intScore + 1);
		}
		p = Pattern.compile(".??[A-Z]");
		m = p.matcher(passwd);
		// [verified] at least one upper case letter
		while (m.find()) {
			upper += 1;
		}
		if (upper > 0) {
			intScore = (intScore + 5);
		}
		// NUMBERS
		p = Pattern.compile(".??[0-9]");
		m = p.matcher(passwd);
		// [verified] at least one number
		while (m.find()) {
			numbers += 1;
		}
		if (numbers > 0) {
			intScore = (intScore + 5);
			if (numbers > 1) {
				intScore = (intScore + 2);
				if (numbers > 2) {
					intScore = (intScore + 3);
				}
			}
		}
		// SPECIAL CHAR
		p = Pattern.compile(".??[!#$%&'()*+,-./:;<= >@\\[\\]\\\\^_`{|}~]");
		m = p.matcher(passwd);
		// [verified] at least one special character
		while (m.find()) {
			special += 1;
		}
		if (special > 0) {
			intScore = (intScore + 5);
			if (special > 1) {
				intScore = (intScore + 5);
			}
		}
		// COMBOS
		// [verified] both upper and lower case
		if (upper > 0 && lower > 0) {
			intScore = (intScore + 2);
		}
		// [verified] both letters and numbers
		if ((upper > 0 || lower > 0) && numbers > 0) {
			intScore = (intScore + 2);
		}
		// [verified] letters, numbers, and special characters
		if ((upper > 0 || lower > 0) && numbers > 0 && special > 0) {
			intScore = (intScore + 2);
		}
		// [verified] upper, lower, numbers, and special characters
		if (upper > 0 && lower > 0 && numbers > 0 && special > 0) {
			intScore = (intScore + 2);
		}
		if (intScore < 16) {
			strVerdict = ComplianceResult.PASSWORD_STRENGTH_VERYWEAK;
		} else if (intScore < 25) {
			strVerdict = ComplianceResult.PASSWORD_STRENGTH_WEAK;
		} else if (intScore < 35) {
			strVerdict = ComplianceResult.PASSWORD_STRENGTH_ACCEPTABLE;
		} else if (intScore < 45) {
			strVerdict = ComplianceResult.PASSWORD_STRENGTH_STRONG;
		} else {
			strVerdict = ComplianceResult.PASSWORD_STRENGTH_VERYSTRONG;
		}
		return strVerdict;
	}

	private static final ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();

	/**
	 * Get the thread list with CPU consumption and the ThreadInfo for each thread sorted by the CPU
	 * time.
	 *
	 * @return -
	 */
	private static List<Map.Entry<Long, ThreadInfo>> getThreadList() {
		// Get all threads and their ThreadInfo objects
		// with no stack trace
		long[] tids = tmbean.getAllThreadIds();
		ThreadInfo[] tinfos = tmbean.getThreadInfo(tids);

		// build a map with key = CPU time and value = ThreadInfo
		SortedMap<Long, ThreadInfo> map = new TreeMap<>();
		for (int i = 0; i < tids.length; i++) {
			long cpuTime = tmbean.getThreadCpuTime(tids[i]);
			// filter out threads that have been terminated
			if (cpuTime != -1 && tinfos[i] != null) {
				map.put(cpuTime, tinfos[i]);
			}
		}

		// build the thread list and sort it with CPU time
		// in decreasing order
		Set<Map.Entry<Long, ThreadInfo>> set = map.entrySet();
		List<Map.Entry<Long, ThreadInfo>> list = new ArrayList<>(set);
		Collections.reverse(list);
		return list;
	}

	public static void printAllThreads2() {
		if (!log.getLogger().isDebugEnabled()) {
			return;
		}

		List<Map.Entry<Long, ThreadInfo>> threadList = getThreadList();
		log.debug("printAllThreads2", "Thread Count for CPU usage:" + threadList.size());

		for (Map.Entry<Long, ThreadInfo> mapt : threadList) {
			log.debug("printAllThreads2", "Tid:" + mapt.getValue().getThreadId()
					+ "\tCpu-time:" + (mapt.getKey() / 1000000) + "ms\tState:"
					+ mapt.getValue().getThreadState() + "\tName:"
					+ mapt.getValue().getThreadName());
		}
	}

	public static void printAllThreads() {
		if (!log.getLogger().isDebugEnabled()) {
			return;
		}

		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;

		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}

		log.debug("printAllThreads", "Thread Active Count:" + Thread.activeCount());
		log.debug("printAllThreads", "ThreadGroup Active Count:" + topGroup.activeCount());
		int estimatedSize = topGroup.activeCount() * 3;
		Thread[] slackList = new Thread[estimatedSize];
		int actualSize = topGroup.enumerate(slackList);
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);

		for (Thread thread : list) {
			log.debug("printAllThreads", "Thread-id:" + thread.getId() + "\tType:"
					+ (thread.isDaemon() ? "sys" : "usr") + "\tThread-name:" + thread.getName());
		}
	}

	public static void setTimerName(String className) {
		String currentThreadName = java.lang.Thread.currentThread().getName();
		if (!currentThreadName.contains("\\\\\\")) {
			java.lang.Thread.currentThread().setName(currentThreadName + "\\\\\\" + className);
		}
	}

	public static void setThreadName(Thread thread, String className) {
		String currentThreadName = thread.getName();
		if (!currentThreadName.contains("///")) {
			thread.setName(currentThreadName + "///" + className);
		}
	}

	public static ScheduledExecutorService getSyncScheduler(long initialDelay, long period,
			TimeUnit timeUnit) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(new SyncTaskTimer(), initialDelay, period, timeUnit);

		return scheduler;
	}

	public static String convertCsvField(String field) {
		if (field == null) {
			return "";
		} else if (field.contains(",") || field.contains("\"")) {
			// if (field.contains("\"")){
			return "\"" + field.replaceAll("\"", "\"\"") + "\"";
			// } else {
			// return "\"" + field +"\"";
			// }
		} else {
			return field;
		}
	}
	
	public static String convertCsvFieldForImport(String field) {
		if (field == null) {
			return "";
		} else if (field.contains(",")) {
			// if (field.contains("\"")){
			return "\"" + field.replaceAll("\\\\","\\\\\\\\").replaceAll("\"", "\\\\\"") + "\"";
			// } else {
			// return "\"" + field +"\"";
			// }
		} else {
			return field;
		}
	}

	public static boolean checkIpAddress(String ip) {
		Pattern p;
		Matcher m;
		p = Pattern
				.compile(".??([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])([.]{1}([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
		m = p.matcher(ip);
		return m.matches();
	}

	public static long getStartIpAddressValue(long intAddress, String netmask){
		long maskValue = AhEncoder.ip2Long(netmask);
		long s = intAddress & maskValue;
		return s < 0 ? (long)Math.pow(2,32)+s : s;
	}

	public static long getStartIpAddressValue(long intAddress, int netmask){
		long maskValue = (long)(Math.pow(2,32)-Math.pow(2,(32-netmask)));
		long s = intAddress & maskValue;
		return s < 0 ? (long)Math.pow(2,32)+s : s;
	}

	public static String getStartIpAddressValue(String ipAndNetmask){
		if(ipAndNetmask == null || "".equals(ipAndNetmask)){
			return ipAndNetmask;
		}
		int indexFlag = ipAndNetmask.indexOf("/");
		String ipStr = ipAndNetmask.substring(0, indexFlag);
		int mask = Integer.valueOf(ipAndNetmask.substring(indexFlag+1));

		long iplong = AhEncoder.ip2Long(ipStr);
		long maskLong = (long)(Math.pow(2,32)-Math.pow(2,(32-mask)));
		long s = iplong & maskLong;
		iplong =  s < 0 ? (long)Math.pow(2,32)+s : s;
		return AhDecoder.long2Ip(iplong) + "/" + String.valueOf(mask);
	}

	public static String getStartIpAddressValue(String ipAddress, String mask){
		long iplong = AhEncoder.ip2Long(ipAddress);
		long maskLong = AhEncoder.ip2Long(mask);
		long s = iplong & maskLong;
		iplong =  s < 0 ? (long)Math.pow(2,32)+s : s;
		return AhDecoder.long2Ip(iplong);
	}

	public static long getValidIpAddressCount(String netmask){
		long maskValue = AhEncoder.ip2Long(netmask);
		return (long)Math.pow(2,32) - maskValue;
	}

	public static boolean checkIpInSameSubnet(String startIpAddress, String endIpAddress, String netmask){
		long ipValue1 = AhEncoder.ip2Long(startIpAddress);
		long ipValue2 = AhEncoder.ip2Long(endIpAddress);
		long maxValue = Math.max(ipValue1, ipValue2);
		long minValue = Math.min(ipValue1, ipValue2);
		long startIpValue = getStartIpAddressValue(minValue, netmask);
		long ipCount = getValidIpAddressCount(netmask);
		return maxValue < (startIpValue + ipCount);
	}

	public static boolean checkIpInSameNetwork(String firstIpNetmask, String secondIpNetmask){
		int index = firstIpNetmask.indexOf("/");
		String firstIp= firstIpNetmask.substring(0, index);
		int firstNetmask=Integer.valueOf(firstIpNetmask.substring(index+1));

		index = secondIpNetmask.indexOf("/");
		String secondIp= secondIpNetmask.substring(0, index);
		int secondNetmask=Integer.valueOf(secondIpNetmask.substring(index+1));

		long ipLong1 = AhEncoder.ip2Long(firstIp);
		long ipLong2 = AhEncoder.ip2Long(secondIp);
		long maskLong1 = (long)(Math.pow(2,32)-Math.pow(2,(32-firstNetmask)));
		long maskLong2 = (long)(Math.pow(2,32)-Math.pow(2,(32-secondNetmask)));

		long s1 = ipLong1 & maskLong1;
		long s2 = ipLong2 & maskLong2;
		ipLong1 =  s1 < 0 ? (long)Math.pow(2,32)+s1 : s1;
		ipLong2 =  s2 < 0 ? (long)Math.pow(2,32)+s2 : s2;

		if (s1==s2) return true;
		long minIpLong = ipLong1 > ipLong2 ? ipLong2: ipLong1;
		
		if(firstNetmask != secondNetmask) {
		    // compare the subnetwork is contained by the other one.
		    long s3 = (firstNetmask < secondNetmask ? ipLong2 : ipLong1)
		    & (firstNetmask < secondNetmask ? maskLong1 : maskLong2);
		    long ipLong3 = s3 < 0 ? (long)Math.pow(2, 32) + s3 : s3;
		    boolean s5 = (ipLong1 & (firstNetmask < secondNetmask ? maskLong1 : maskLong2)) ==
		    		(ipLong2 & (firstNetmask < secondNetmask ? maskLong1 : maskLong2));
		    if(s5 && ipLong3 == minIpLong) return true;
		}

		long s4 = ipLong1 & ipLong2;
		long ipLong4 =  s4 < 0 ? (long)Math.pow(2,32)+s4 : s4;

		return ipLong4 > minIpLong;
	}

	public static String checkIpInSameBranch(String startIpAddress, String netmask, int branches, List<String> ipAddressArray){
		long ipValue1 = AhEncoder.ip2Long(startIpAddress);
		long startIpValue = getStartIpAddressValue(ipValue1, netmask);
		long ipCount = getValidIpAddressCount(netmask);
		long perBranchCount = ipCount/branches;
		Map<String, String> mapBranch = new HashMap<>();
		for(String ip:ipAddressArray){
			long ipValue2 = AhEncoder.ip2Long(ip);
			long key;
			if ((ipValue2-startIpValue)%perBranchCount==0) {
				key=(ipValue2-startIpValue)/perBranchCount;
			} else {
				key=(ipValue2-startIpValue)/perBranchCount +1;
			}
			if (key>branches){
				key=branches;
			}
			if (mapBranch.get(String.valueOf(key))!=null) {
				return "The items " + ip + " and " + mapBranch.get(String.valueOf(key)) + " cannot be in the same branch.";
			} else {
				mapBranch.put(String.valueOf(key), ip);
			}
		}
		return "";
	}

	public static List<String> splitNetworkToSubblocks(String ipAndMask, long splitNum){
		int index = ipAndMask.indexOf("/");
		return splitNetworkToSubblocks(ipAndMask.substring(0, index),
				Integer.valueOf(ipAndMask.substring(index+1)),
				splitNum);
	}

	public static List<String> splitNetworkToSubblocks(String ipAddress, int netmask, long splitNum){
		List<String> resList = new ArrayList<>();
		if(ipAddress == null || "".equals(ipAddress) || netmask < 0 || splitNum <0){
			return resList;
		}

		long ipLong = AhEncoder.ip2Long(ipAddress);
		long startIpLong = MgrUtil.getStartIpAddressValue(ipLong, netmask);

		int n1 = 32 - netmask;
		double n2 = Math.log10(splitNum)/Math.log10(2);
		double perGroup = n1 - n2;
		double perGroupIp = Math.pow(2, perGroup);
		int subMask = (int)(32 - perGroup);

		for(int i=1; i<=splitNum; i++){
			resList.add(AhDecoder.long2Ip(startIpLong) + "/" + String.valueOf(subMask));
			startIpLong += perGroupIp;
		}

		return resList;
	}

	/*-
	 * get the flag of this vhm enable wireless + routing
	 *
	 *@param ownerId id
	 *@return if enable
	 */
	/*public static boolean isWirelessRoutingEnable(Long ownerId) {
		// check the flag has configured
		//List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class,null, null, ownerId);
		List<?> list = QueryUtil.executeQuery("select modeType from " + HmStartConfig.class.getSimpleName(), null, new FilterParams("owner.id", ownerId), 1);
		return !list.isEmpty() && (Short)list.get(0) == HmStartConfig.HM_MODE_FULL;
	}

	public static boolean isNetworkPolicyWirelessRoutingEnable(Long networkPolicyId) {
		//TODO: check it for network policy type re-design
		List<?> list = QueryUtil.executeNativeQuery("select blnwirelessrouter from config_template where id=" + networkPolicyId);
		List<?> list = QueryUtil.executeNativeQuery("select enable_wireless, enable_router from config_template where id=" + networkPolicyId);
		return !list.isEmpty() && (Boolean) list.get(0) && (Boolean) list.get(1);
	}*/
	
	public static boolean isEnableDownloadServer(){
		String resStr = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_DS, ConfigUtil.KEY_DS_ENABLE, "false");
		return Boolean.valueOf(resStr);
	}
	
	public static String getDownloadServerHost(){
		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_DS, ConfigUtil.KEY_DS_SERVER, "");
	}
	
	public static boolean isEnableDSSimulator(){
		String resStr = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_DS, ConfigUtil.KEY_DS_SIMULATOR, "false");
		return Boolean.valueOf(resStr);
	}
	
	public static String getHiveManagerIp(){
		try{
			String osName = System.getProperties().getProperty("os.name").toLowerCase();
			if(osName.contains("windows")){
				InetAddress inet = InetAddress.getLocalHost();
				return inet.getHostAddress();
			}else{
				return HmBeOsUtil.getHiveManagerIPAddr();
			}
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * for access mode(replace current user's group with switch domain's user group, group name according to access mode)
	 *
	 * @param hmDomain -
	 * @return -
	 */
	public static String getUserGroupName(HmDomain hmDomain) {
		if (NmsUtil.hasRwAccessPermission(hmDomain.getAccessMode(), hmDomain.getAuthorizationEndDate())) {
			// read & write
			return HmUserGroup.CONFIG;
		} else {
			// read only
			return HmUserGroup.MONITOR;
		}
	}
	
	/**
	 * for access mode(is access mode is temp access, need show left authorized time)
	 *
	 * @param hmDomain -
	 * @param userObj -
	 */
	public static void setShowLeftTimeFlag(HmDomain hmDomain, HmUser userObj) {
		if (hmDomain == null) {
			userObj.setShowLeftAccessTimeForSwitchDomain(false);
			return;
		}
		
		if (NmsUtil.isTempReadAccessPermission(hmDomain.getAccessMode(), hmDomain.getAuthorizationEndDate())) {
			// after switch domain, show left authorized time once if access mode is temp authorization.
			userObj.setShowLeftAccessTimeForSwitchDomain(true);
		} else {
			userObj.setShowLeftAccessTimeForSwitchDomain(false);
		}
	}

	/**
	 * for access mode(is access mode is temp access, need show left authorized time)
	 * @param List<String> objList, String can be like 1,2-10,77
	 * @retrun Set<String>
	 */
	public static Set<String> convertRangeToVlaue(List<String> objList){
		Set<String> allVlanList = new HashSet<String>();
		for(String str: objList){
			if (str.equalsIgnoreCase("all")){
				allVlanList.clear();
				allVlanList.add("all");
				return allVlanList;
			}
			String[] strAttrValue = str.split(",");
			for (String attrValue : strAttrValue) {
				String[] attrRange = attrValue.split("-");
				if (attrRange.length > 1) {
					for (int addCount = Integer.parseInt(attrRange[0].trim()); addCount < Integer
							.parseInt(attrRange[1].trim()) + 1; addCount++) {
						allVlanList.add(String.valueOf(addCount));
					}
				} else {
					allVlanList.add(attrRange[0].trim());
				}
			}
		}
		return allVlanList;
	}
	
	public static void sleepTime(int seconds) {
		try {
			// waiting for ? seconds
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e1) {
			log.error("Thread sleep error: " + e1.getMessage());
		}
	}
	
	private static QueryUtil queryUtilIns;
	public static QueryUtil getQueryEntity(){
		if(queryUtilIns == null){
			queryUtilIns = new QueryUtil();
		}
		return queryUtilIns;
	}

	
	public static LogSettings fetchHomeDomainLogSettings(){
		LogSettings logSetting = QueryUtil.findBoByAttribute(LogSettings.class, 
				"owner.domainName", HmDomain.HOME_DOMAIN);
		if (logSetting == null) {
			logSetting = new LogSettings();
		}
		return logSetting;
	}

    public static boolean isWanUsedInPBR(HiveAp hiveAp, DeviceInterface dInf) {
        Set<String> activeWans = null;
        //first ,get pbr from hiveap.If exist, contract
        if (hiveAp.getRoutingProfilePolicy() != null) {
            activeWans = hiveAp.getRoutingProfilePolicy().getActiveWans();
        } else if (hiveAp.getConfigTemplate().getRoutingProfilePolicy() != null) {
            activeWans = hiveAp.getConfigTemplate().getRoutingProfilePolicy().getActiveWans();
        }

        if(activeWans != null){
            if(activeWans.contains(String.valueOf(dInf.getDeviceIfType()))) {
                return true;
            }

            DeviceInterface wanIntf = null;
            for(String wanIntfType : activeWans) {
                if (wanIntfType == null)
                    continue;
                if (wanIntfType.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_PRIMARY_WAN_VALUE))) {
                    wanIntf = hiveAp.getOrderWanInterface(0);
                } else if (wanIntfType.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_1_VALUE))) {
                    wanIntf = hiveAp.getOrderWanInterface(1);
                } else if (wanIntfType.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_2_VALUE))) {
                    wanIntf = hiveAp.getOrderWanInterface(2);
                }

                return (wanIntf != null && wanIntf.getDeviceIfType() == dInf.getDeviceIfType());
            }
        }
        return false;
    }
	
    public static final Set<String> unSupportLanguageSet = new HashSet<>();
    
    public static void initNotSupportLanguages() {
 	   if (unSupportLanguageSet.isEmpty()) {
 	        String notSupportLanguages = NmsUtil.getConfigProperty("do.not.support.language");
 	        if (!StringUtils.isEmpty(notSupportLanguages)) {
 	            String[] notSupportLanguageArray = notSupportLanguages.split(",");
 				Collections.addAll(unSupportLanguageSet, notSupportLanguageArray);
 	        }
 	   }
    }

    /**
     * languages are alphabetical
     * @return -
     */
 	public static EnumItem[] getEnumLanguage() {
 		initNotSupportLanguages();
 		
 		// languages are alphabetical in this array
 		int[] languages = HmUser.LANGUAGECODES;
 		String lanCodeStr;
 		List<String> supportLanguages = new ArrayList<>();
 		for (int lanCode : languages) {
 			lanCodeStr = String.valueOf(lanCode);
 			if (!unSupportLanguageSet.contains(lanCodeStr)) {
 				// not include in un-support language set
 				supportLanguages.add(lanCodeStr);
 			}
 		}
 		
 		if (!supportLanguages.isEmpty()) {
 			int[] intSupportLangs = new int[supportLanguages.size()];
 			for (int i = 0; i < supportLanguages.size(); i++) {
 				try {
 					intSupportLangs[i] = Integer.parseInt(supportLanguages.get(i));
 				} catch (Exception e) {
 					log.error("getEnumLanguage", "convert language code from String to Integer error. code= " + supportLanguages.get(i));
 				}
 			}
 			return MgrUtil.enumItems("enum.language.", intSupportLangs);
 		} else {
 			return MgrUtil.enumItems("enum.language.", new int[]{HmUser.I18N_DEFAULT});
 		}
 	}
 	
 	/**
 	 * @author huihe@aerohive.com
 	 * @description check the string has '<' or '>' like char to prevent the xss attack.
 	 * @param name
 	 * @return
 	 */
 	public static boolean xssStringCheck(String name){
 		if((name != null && name.length() > 0) && (name.indexOf("<") >=0  || name.indexOf(">") >= 0)){
 			return false;
 		}
 		
 		return true;
 	}

}