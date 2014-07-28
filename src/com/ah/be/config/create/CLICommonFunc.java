package com.ah.be.config.create;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.xml.be.config.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.util.EnumItem;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;

/**
 * 
 * @author zhang
 * 
 */
public class CLICommonFunc {

	private static final Tracer log = new Tracer(CLICommonFunc.class
			.getSimpleName());

	private static final AhEnumAct YES = AhEnumAct.YES;
	private static final AhEnumAct NO = AhEnumAct.NO;

	private static final AhEnumActValue YES_WITH_VALUE = AhEnumActValue.YES_WITH_VALUE;
	private static final AhEnumActValue NO_WITH_VALUE = AhEnumActValue.NO_WITH_VALUE;

	private static final AhEnumShow YES_WITH_SHOW = AhEnumShow.YES_WITH_SHOW;
	private static final AhEnumShow NO_WITH_HIDDEN = AhEnumShow.NO_WITH_HIDDEN;

	private static final AhOnlyAct YES_ONLY = new AhOnlyAct();
	private static final AhOnlyAct NO_ONLY = new AhOnlyAct();

	public static final short TYPE_GLOBAL = SingleTableItem.TYPE_GLOBAL;
	public static final short TYPE_MAP = SingleTableItem.TYPE_MAP;
	public static final short TYPE_HIVE_AP = SingleTableItem.TYPE_HIVEAPNAME;
	public static final short TYPE_CLASSIFIER = SingleTableItem.TYPE_CLASSIFIER;

	public static final String ATTRIBUTE_VALUE = GenerateXML.ATTRIBUTE_NAME_VALUE;
	public static final String ATTRIBUTE_NAME = GenerateXML.ATTRIBUTE_NAME_NAME;
	public static final String ATTRIBUTE_OPERATION = GenerateXML.ATTRIBUTE_NAME_OPERATION;
	public static final String ATTRIBUTE_QUOTEPROHIBITED = GenerateXML.ATTRIBUTE_NAME_QUOTEPROHIBITED;
	
	public static final int MAX_MERGE_RANGE = 5000;
	
	private static AerohiveEncryptTool dbPasswordKeyIns = new AerohiveEncryptTool("!f%li4*>@7B9cQ#3");

	// public static enum HiveApVer {
	// HiveOS_LOW("2.1.4.0/2.1.3.0"), HiveOS_HIGH("3.0.1.0");
	//
	// private final String value;
	// private final String LOW_REGEX = "^2.1.(3|4|5).0$";
	// private final String HIGH_REGEX = "^3.0.(0|1|2|3).0$";
	// private final Pattern PATTERN_LOW = Pattern.compile(LOW_REGEX);
	// private final Pattern PATTERN_HIGH = Pattern.compile(HIGH_REGEX);
	//
	// private HiveApVer(String value) {
	// this.value = value;
	// }
	//
	// public String value() {
	// return this.value;
	// }
	//
	// public boolean isEquals(String apVer) {
	// if(apVer == null){
	// return false;
	// }
	// Matcher matcher;
	// if (this == HiveOS_LOW) {
	// matcher = PATTERN_LOW.matcher(apVer);
	// } else {
	// matcher = PATTERN_HIGH.matcher(apVer);
	// }
	// return matcher.find();
	// }
	// }

	static {
		YES_ONLY.setOperation(YES);
		NO_ONLY.setOperation(NO);
	}

	public static boolean getYesDefault() {
		return true;
	}

	public static AhEnumAct getAhEnumAct(boolean isYes) {
		if (isYes) {
			return YES;
		} else {
			return NO;
		}
	}

	public static AhEnumActValue getAhEnumActValue(boolean isYes) {
		if (isYes) {
			return YES_WITH_VALUE;
		} else {
			return NO_WITH_VALUE;
		}
	}

	public static AhEnumShow getAhEnumShow(boolean isYes) {
		if (isYes) {
			return YES_WITH_SHOW;
		} else {
			return NO_WITH_HIDDEN;
		}
	}

	public static AhOnlyAct getAhOnlyAct(boolean isYes) {
		if (isYes) {
			return YES_ONLY;
		} else {
			return NO_ONLY;
		}
	}

	public static AhEnumActValueShow getAhEnumActValueShow(boolean isYes,
			boolean isShow) {
		if (isYes) {
			if (isShow) {
				return AhEnumActValueShow.YES_WITH_SHOW;
			} else {
				return AhEnumActValueShow.YES_WITH_VALUE;
			}
		} else {
			if (isShow) {
				return AhEnumActValueShow.NO_WITH_HIDDEN;
			} else {
				return AhEnumActValueShow.NO_WITH_VALUE;
			}
		}
	}

	public static AhString getAhString(String value) {
		AhString strObj = new AhString();
		strObj.setValue(value);
		return strObj;
	}
	
	public static AhNameAct createAhNameActObj(String name, boolean isTrue) {
		AhNameAct nameActObj = new AhNameAct();
		nameActObj.setName(name);
		if (isTrue) {
			nameActObj.setOperation(YES);
		} else {
			nameActObj.setOperation(NO);
		}
		return nameActObj;
	}

	public static AhNameActValue createAhNameActValue(String name,
			boolean isTrue) {
		AhNameActValue nameActObj = new AhNameActValue();
		nameActObj.setName(name);
		if (isTrue) {
			nameActObj.setOperation(YES_WITH_VALUE);
		} else {
			nameActObj.setOperation(NO_WITH_VALUE);
		}
		return nameActObj;
	}

	public static AhNameActValueQuoteProhibited createAhNameActValueQuoteProhibited(
			String name, boolean isTrue, boolean isQuote) {
		AhNameActValueQuoteProhibited quoteObj = new AhNameActValueQuoteProhibited();
		quoteObj.setName(name);

		if (isTrue) {
			quoteObj.setOperation(YES_WITH_VALUE);
		} else {
			quoteObj.setOperation(NO_WITH_VALUE);
		}

		if (isQuote) {
			quoteObj.setQuoteProhibited(YES);
		} else {
			quoteObj.setQuoteProhibited(NO);
		}

		return quoteObj;
	}

	public static AhString createAhStringObj(String value) {
		AhString ahStringObj = new AhString();
		ahStringObj.setValue(value);
		return ahStringObj;
	}

	public static AhName createAhName(String name) {
		AhName ahNameObj = new AhName();
		ahNameObj.setName(name);
		return ahNameObj;
	}

	public static AhActShow createAhActShow(boolean isYes) {
		AhActShow ahOutPutObj = new AhActShow();
		ahOutPutObj.setOperation(CLICommonFunc.getAhEnumShow(isYes));
		return ahOutPutObj;
	}

	public static AhStringAct createAhStringActObj(String value, boolean isTrue) {
		AhStringAct ahStringActObj = new AhStringAct();
		ahStringActObj.setValue(value);
		if (isTrue) {
			ahStringActObj.setOperation(YES);
		} else {
			ahStringActObj.setOperation(NO);
		}
		return ahStringActObj;
	}
	
	public static AhIntAct createAhIntActObj(int value, boolean isTrue) {
		AhIntAct ahIntAct = new AhIntAct();
		ahIntAct.setValue(value);
		if (isTrue) {
			ahIntAct.setOperation(YES);
		} else {
			ahIntAct.setOperation(NO);
		}
		return ahIntAct;
	}

    public static AhIntNameAct createAhIntNameActObj(int name, boolean isTrue) {
        AhIntNameAct ahIntNameAct = new AhIntNameAct();
        ahIntNameAct.setName(name);
        if (isTrue) {
            ahIntNameAct.setOperation(YES);
        } else {
            ahIntNameAct.setOperation(NO);
        }
        return ahIntNameAct;
    }

	public static AhInt getAhInt(int value){
		AhInt ahInt = new AhInt();
		ahInt.setValue(value);
		return ahInt;
	}

	public static AhStringQuoteProhibited createAhStringQuoteProhibited(
			String value, boolean isQuote) {
		AhStringQuoteProhibited quoteObj = new AhStringQuoteProhibited();

		quoteObj.setValue(value);

		if (isQuote) {
			quoteObj.setQuoteProhibited(YES);
		} else {
			quoteObj.setQuoteProhibited(NO);
		}

		return quoteObj;
	}

	public static AhStringActQuoteProhibited createAhStringActQuoteProhibited(
			String value, boolean isTrue, boolean isQuote) {
		AhStringActQuoteProhibited quoteObj = new AhStringActQuoteProhibited();
		quoteObj.setValue(value);

		if (isTrue) {
			quoteObj.setOperation(YES);
		} else {
			quoteObj.setOperation(NO);
		}

		if (isQuote) {
			quoteObj.setQuoteProhibited(YES);
		} else {
			quoteObj.setQuoteProhibited(NO);
		}

		return quoteObj;
	}

	public static AhEncryptedStringAct createAhEncryptedStringAct(String value,
			boolean isTrue) throws IOException {
		AhEncryptedStringAct ahEncryptedObj = new AhEncryptedStringAct();
		ahEncryptedObj.setValue(AhConfigUtil.hiveApCommonEncrypt(value));
		if (isTrue) {
			ahEncryptedObj.setOperation(YES);
		} else {
			ahEncryptedObj.setOperation(NO);
		}
		ahEncryptedObj.setEncrypted(ahEncryptedObj.getEncrypted());
		return ahEncryptedObj;
	}
	
	public static AhEncryptedStringActQuoteProhibited createAhEncryptedStringActQuoteProhibited(String value,
			boolean isTrue, boolean isQuote) throws IOException{
		AhEncryptedStringActQuoteProhibited ahEncryptedObj = new AhEncryptedStringActQuoteProhibited();
		ahEncryptedObj.setValue(AhConfigUtil.hiveApCommonEncrypt(value));
		if (isTrue) {
			ahEncryptedObj.setOperation(YES);
		} else {
			ahEncryptedObj.setOperation(NO);
		}
		if (isQuote){
			ahEncryptedObj.setQuoteProhibited(YES);
		}else{
			ahEncryptedObj.setQuoteProhibited(NO);
		}
		ahEncryptedObj.setEncrypted(ahEncryptedObj.getEncrypted());
		
		return ahEncryptedObj;
	}

	public static AhEncryptedString createAhEncryptedString(String value)
			throws IOException {
		AhEncryptedString encryptedObj = new AhEncryptedString();
		encryptedObj.setValue(AhConfigUtil.hiveApCommonEncrypt(value));
		encryptedObj.setEncrypted(encryptedObj.getEncrypted());
		return encryptedObj;
	}

	public static AhStringActValue createAhStringActValueObj(String value,
			boolean isTrue) {
		AhStringActValue ahStringActValueObj = new AhStringActValue();
		ahStringActValueObj.setValue(value);
		if (isTrue) {
			ahStringActValueObj.setOperation(YES_WITH_VALUE);
		} else {
			ahStringActValueObj.setOperation(NO_WITH_VALUE);
		}
		return ahStringActValueObj;
	}

	public static AhEncryptedStringActValue createAhEncryptedStringActValue(
			String value, boolean isTrue) throws IOException {
		AhEncryptedStringActValue encryptedObj = new AhEncryptedStringActValue();
		encryptedObj.setValue(AhConfigUtil.hiveApCommonEncrypt(value));
		if (isTrue) {
			encryptedObj.setOperation(YES_WITH_VALUE);
		} else {
			encryptedObj.setOperation(NO_WITH_VALUE);
		}
		encryptedObj.setEncrypted(encryptedObj.getEncrypted());
		return encryptedObj;
	}
	
	public static AhUserPassword createAhUserPassword(String userName,String password) throws IOException{
		AhUserPassword ahUserObj = new AhUserPassword();
		ahUserObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		ahUserObj.setValue(userName);
		ahUserObj.setPassword(CLICommonFunc.createAhEncryptedString(password));
		return ahUserObj;
	}

	public static Object createObjectWithName(Class<?> objClass,
			Object[][] parameters) throws Exception {
		if(parameters == null){
			return null;
		}
		for (Object[] parameter : parameters){
			if(parameter[0] == null || parameter[1] == null){
				return null;
			}
		}
		String methodPrefix = "set";
		Field[] fields = objClass.getDeclaredFields();
		Object obj = objClass.newInstance();
		String parameterName, methodName;
		Method method = null;
		Object parameterValue;
		boolean isEqual;
		for (Object[] parameter : parameters) {
			parameterName = (String) parameter[0];

			// validate parameter name
			isEqual = false;
			for (Field f : fields) {
				if (f.getName().equals(parameterName)) {
					isEqual = true;
					break;
				}
			}
			if (!isEqual) {
				String[] errParams = { objClass.getSimpleName(), parameterName };
				String errMsg = NmsUtil.getUserMessage(
						"error.be.config.create.schemaAttrNotFound", errParams);
				log.error("createObjectWithName", errMsg);
				throw new CreateXMLException(errMsg);
			}

			methodName = methodPrefix
					+ parameterName.substring(0, 1).toUpperCase()
					+ parameterName.substring(1);
			parameterValue = parameter[1];
			if (parameterName.equals(ATTRIBUTE_OPERATION)
					|| ATTRIBUTE_QUOTEPROHIBITED.equals(parameterName)) {
				Field field = objClass.getDeclaredField(ATTRIBUTE_OPERATION);
				if (field.getType() == AhEnumAct.class) {
					if ((Boolean) parameterValue) {
						parameterValue = YES;
					} else {
						parameterValue = NO;
					}
				} else if (field.getType() == AhEnumActValue.class) {
					if ((Boolean) parameterValue) {
						parameterValue = YES_WITH_VALUE;
					} else {
						parameterValue = NO_WITH_VALUE;
					}
				} else if (field.getType() == AhEnumShow.class) {
					if ((Boolean) parameterValue) {
						parameterValue = YES_WITH_SHOW;
					} else {
						parameterValue = NO_WITH_HIDDEN;
					}
				}
			}

			try {
				method = objClass.getMethod(methodName, parameterValue
						.getClass());
			} catch (NoSuchMethodException e) {
				if (parameterValue.getClass() == Integer.class) {
					method = objClass.getMethod(methodName, int.class);
				}
			}
			if (method != null) {
				method.invoke(obj, parameterValue);
			}
		}

		return obj;
	}

	public static int turnNetMaskToNum(String netMask) {
		if(netMask == null || "".equals(netMask)){
			return 32;
		}
		return AhEncoder.netmask2int(netMask);
	}
	
	
	public static boolean isRuleMatch(SingleTableItem rule, HiveAp hiveAp){
		if(rule == null || hiveAp == null){
			return false;
		}
		
		if(rule.getType() == SingleTableItem.TYPE_HIVEAPNAME){
			//host name match
			String hostNameAp = hiveAp.getHostName();
			
			String hostNameRule = rule.getTypeName() == null ? "" : rule.getTypeName();
			hostNameRule = hostNameRule.replace("*", ".*");
			hostNameRule = "^" + hostNameRule + "$";
			
			Pattern pattern = Pattern.compile(hostNameRule);
			Matcher matcher = pattern.matcher(hostNameAp);
			
			return matcher.matches();
		}else if(rule.getType() == SingleTableItem.TYPE_MAP){
			// map name match
			String mapNameAp = hiveAp.getMapContainer() != null ? hiveAp.getMapContainer().getMapName() : null;
			String mapNameRule = rule.getLocation() != null ? rule.getLocation().getMapName() : null;
			return (mapNameAp != null && mapNameAp.equals(mapNameRule)) || 
					(mapNameAp == null && mapNameRule == null);
		}else if(rule.getType() == SingleTableItem.TYPE_GLOBAL){
			// global rule is match
			return true;
		}else if(rule.getType() == SingleTableItem.TYPE_CLASSIFIER){
			// Classifier exact match
			boolean tag1Match = isTagMatch(hiveAp.getClassificationTag1(), rule.getTag1(), rule.isTag1Checked());
			boolean tag2Match = isTagMatch(hiveAp.getClassificationTag2(), rule.getTag2(), rule.isTag2Checked());
			boolean tag3Match = isTagMatch(hiveAp.getClassificationTag3(), rule.getTag3(), rule.isTag3Checked());
			return tag1Match && tag2Match && tag3Match;
		}
		
		return false;
	}
	
	private static boolean isTagMatch(String apTag, String ruleTag, boolean tagChecked){
		String apTagStr = apTag == null ? "" : apTag;
		
		String ruleTagStr = tagChecked ? ruleTag : "*";
		ruleTagStr = ruleTagStr == null ? "" : ruleTagStr;
		ruleTagStr = ruleTagStr.replace("*", ".*");
		ruleTagStr = "^" + ruleTagStr + "$";
		
		Pattern pattern = Pattern.compile(ruleTagStr);
		Matcher matcher = pattern.matcher(apTagStr);
		
		return matcher.matches();
	}
	
	public static SingleTableItem getSingleTableItem(Object itemObj, HiveAp hiveAp){
		List<SingleTableItem> itemList = null;
		if(itemObj instanceof IpAddress){
			itemList = ((IpAddress)itemObj).getItems();
		}else if(itemObj instanceof MacOrOui){
			itemList = ((MacOrOui)itemObj).getItems();
		}else if(itemObj instanceof Vlan){
			itemList = ((Vlan)itemObj).getItems();
		}else if(itemObj instanceof UserProfileAttribute){
			itemList = ((UserProfileAttribute)itemObj).getItems();
		}else if(itemObj instanceof RadiusAttrs){
			itemList = ((RadiusAttrs)itemObj).getItems();
		}else if(itemObj instanceof LocationClientWatch){
			itemList = ((LocationClientWatch)itemObj).getItems();
		}
		
		for(SingleTableItem rule : itemList){
			if(isRuleMatch(rule, hiveAp)){
				return rule;
			}
		}
		
		return null;
	}
	
	public static SingleTableItem getMacAddressOrOui(MacOrOui macOrOui,
			HiveAp hiveAp) throws CreateXMLException {
//		if(macOrOui.getId() != null){
//			macOrOui = QueryUtil.findBoById(MacOrOui.class, macOrOui.getId(), new ConfigLazyQueryBo());
//		}
		if(macOrOui == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(macOrOui, hiveAp);
		if(sItem == null){
			String[] errParams = { "MacOrOui", macOrOui.getMacOrOuiName(),
					hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getMacAddressOrOui", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}

	public static SingleTableItem getIpAddress(IpAddress ipAddress,
			HiveAp hiveAp) throws CreateXMLException {
//		if(ipAddress.getId() != null){
//			ipAddress = QueryUtil.findBoById(IpAddress.class, ipAddress.getId(), new ConfigLazyQueryBo());
//		}
		if(ipAddress == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(ipAddress, hiveAp);
		if(sItem == null){
			String[] errParams = { "IPAddress", ipAddress.getAddressName(),
					hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getIpAddress", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}
	
	public static SingleTableItem getRadiusAttrs(RadiusAttrs radiusAttrs,
			HiveAp hiveAp) throws CreateXMLException {
		
		if(radiusAttrs == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(radiusAttrs, hiveAp);
		if(sItem == null){
			String[] errParams = { "RadiusAttrs", radiusAttrs.getObjectName(),
					hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getRadiusAttrs", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}
	
	public static SingleTableItem getPortGroupProfile(PortGroupProfile portGroupObj,HiveAp hiveAp){
		if(portGroupObj == null){
			return null;
		}
		SingleTableItem sItem = getPortGroupSingleTableItem(portGroupObj, hiveAp);
		return sItem;
//		if(sItem == null){
//			String[] errParams = { "PortGroupProfile", portGroupObj.getName(),
//					hiveAp.getHostName() };
//			String errMsg = NmsUtil.getUserMessage(
//					"error.be.config.create.cannotFoundObj", errParams);
//			sendSnmpEvent(errMsg);
//			log.error("getPortGroupProfile", errMsg);
////			throw new CreateXMLException(errMsg);
//			return null;
//		}else{
//			return sItem;
//		}
	}
	
	public static SingleTableItem getPortGroupSingleTableItem(Object itemObj,
			HiveAp hiveAp) {
		List<SingleTableItem> itemList = null;
		if (itemObj instanceof PortGroupProfile) {
			itemList = ((PortGroupProfile) itemObj).getItems();
		}

		long configId = hiveAp.getConfigTemplateId();
		for (SingleTableItem rule : itemList) {
			if (rule.getConfigTemplateId() == configId) {
				if (isRuleMatch(rule, hiveAp)) {
					return rule;
				}
			}
		}

		return null;
	}


	public static SingleTableItem getVlan(Vlan vlanObj, HiveAp hiveAp)
			throws CreateXMLException {
//		if(vlanObj.getId() != null){
//			vlanObj = QueryUtil.findBoById(Vlan.class, vlanObj.getId(), new ConfigLazyQueryBo());
//		}
		if(vlanObj == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(vlanObj, hiveAp);
		if(sItem == null){
			String[] errParams = { "Vlan", vlanObj.getVlanName(),
					hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getVlan", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}

	public static SingleTableItem getUserProfileAttr(
			UserProfileAttribute userAttribute, HiveAp hiveAp)
			throws CreateXMLException {
		if(userAttribute == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(userAttribute, hiveAp);
		if(sItem == null){
			String[] errParams = { "UserProfileAttribute",
					userAttribute.getAttributeName(), hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getUserProfileAttr", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}
	
	public static SingleTableItem getLocationWatch(
			LocationClientWatch localWatch, HiveAp hiveAp)
			throws CreateXMLException {
		if(localWatch == null){
			return null;
		}
		SingleTableItem sItem = getSingleTableItem(localWatch, hiveAp);
		if(sItem == null){
			String[] errParams = { "LocationClientWatch",
					localWatch.getName(), hiveAp.getHostName() };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundObj", errParams);
			sendSnmpEvent(errMsg);
			log.error("getLocationWatch", errMsg);
			throw new CreateXMLException(errMsg);
		}else{
			return sItem;
		}
	}

	public static String getEnumItemValue(EnumItem[] enumItem, int key) {
		for (EnumItem item : enumItem) {
			if (item.getKey() == key) {
				return item.getValue();
			}
		}
		return "";
	}

	public static String transFormMacAddrOrOui(String macAddrOrOui) {
		StringBuffer mac = new StringBuffer();
		for (int i = 0; i < macAddrOrOui.length(); i++) {
			if (i != 0 && i % 2 == 0) {
				mac.append("-");
			}
			mac.append(macAddrOrOui.substring(i, i + 1));
		}
		return mac.toString().toLowerCase();
	}

//	public static boolean isDefaultProfile(Object profileObj) {
//		Class<?> profileClass = profileObj.getClass();
//		Field field;
//		String defFieldName = "defaultFlag";
//
//		try {
//			field = profileClass.getDeclaredField(defFieldName);
//		} catch (Exception e) {
//			field = null;
//		}
//		if (field == null) {
//			return false;
//		} else {
//			String methodName1 = "is"
//					+ defFieldName.substring(0, 1).toUpperCase()
//					+ defFieldName.substring(1);
//			String methodName2 = "get"
//					+ defFieldName.substring(0, 1).toUpperCase()
//					+ defFieldName.substring(1);
//			Method method;
//			try {
//				method = profileClass.getMethod(methodName1);
//			} catch (Exception e) {
//				try {
//					method = profileClass.getMethod(methodName2);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					return false;
//				}
//			}
//			Object resultObj;
//			try {
//				resultObj = method.invoke(profileObj);
//			} catch (Exception e) {
//				return false;
//			}
//			return resultObj instanceof Boolean && (Boolean) resultObj;
//		}
//	}

	public static void sendSnmpEvent(String arg_msg) {
	}

	/**
	 * 
	 * @param objList
	 *            -
	 * @return last modify database time
	 */
	public static String getLastUpdateTime(List<?> objList) {
		long lastTime = 0;
		long currenTime;
		if (objList != null) {
			for (Object obj : objList) {
				if (obj != null && (obj instanceof HmBo)
						&& ((HmBo) obj).getVersion() != null) {
					currenTime = ((HmBo) obj).getVersion().getTime();
					lastTime += currenTime;
				}
			}
		}
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
		}
		return String.valueOf(lastTime);
	}

	/**
	 * compute ip address with net mask.
	 * 
	 * @param ipAddr
	 *            -
	 * @param netMask
	 *            -
	 * @return -
	 */
	public static String countIpAndMask(String ipAddr, String netMask) {
		String[] ipAry = ipAddr.split("\\.");
		String[] maskAry = netMask.split("\\.");
		String result = "";
		for (int i = 0; i < ipAry.length; i++) {
			String ipBinary = fillStringHead(Integer.toBinaryString(Integer
					.valueOf(ipAry[i])), "0", 8);
			String maskBinary = fillStringHead(Integer.toBinaryString(Integer
					.valueOf(maskAry[i])), "0", 8);
			if ("".equals(result)) {
				result += Integer.parseInt(
						twoStringAndOpt(ipBinary, maskBinary), 2);
			} else {
				result += "."
						+ Integer.parseInt(
								twoStringAndOpt(ipBinary, maskBinary), 2);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param oldStr
	 *            : The String want deal
	 * @param fillStr
	 *            : filled String
	 * @param allLength
	 *            : The String length of return
	 * @return -
	 */
	private static String fillStringHead(String oldStr, String fillStr,
			int allLength) {
		if (allLength > oldStr.length()) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < allLength - oldStr.length(); i++) {
				strBuf.append(fillStr);
			}
			return strBuf.toString() + oldStr;
		} else {
			return oldStr;
		}
	}

	/**
	 * Two String & operation, return result string.
	 * 
	 * @param str1
	 *            -
	 * @param str2
	 *            -
	 * @return -
	 */
	private static String twoStringAndOpt(String str1, String str2) {
		if (str1.length() == str2.length()) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < str1.length(); i++) {
				strBuf.append(Integer.valueOf(str1.substring(i, i + 1))
						& Integer.valueOf(str2.substring(i, i + 1)));
			}
			return strBuf.toString();
		} else {
			return null;
		}
	}

	// public static String addQutoTwoSide(String valueStr) {
	// if (valueStr != null && !"".equals(valueStr)
	// && valueStr.indexOf(" ") > -1 && !valueStr.startsWith("\"")
	// && !valueStr.endsWith("\"")) {
	// return "\"" + valueStr + "\"";
	// } else {
	// return valueStr;
	// }
	// }

	public static AhEncryptedString createAhEncryptedString(String pass,
			int type) {
		AhEncryptedString passObj = new AhEncryptedString();
		passObj.setValue(pass);
		passObj.setEncrypted(type);
		return passObj;
	}
	
	public static long getIpIndex(String ipAddr){
		String[] ipArgs = ipAddr.split("\\.");
		long res = 0;
		for(int i=0; i<ipArgs.length; i++){
			if(i == 0){
				long ipInt = Integer.valueOf(ipArgs[i]);
				res += ipInt * 255 * 255 * 255;
			}else if(i == 1){
				long ipInt = Integer.valueOf(ipArgs[i]);
				res += ipInt * 255 * 255;
			}else if(i == 2){
				long ipInt = Integer.valueOf(ipArgs[i]);
				res += ipInt * 255;
			}else if(i == 3){
				long ipInt = Integer.valueOf(ipArgs[i]);
				res += ipInt;
			}
		}
		return res;
	}
	
	public static String getStartIpAddress(String ipAddress, String netMaster){
		if(ipAddress == null || "".equals(ipAddress)){
			return "";
		}
		if(netMaster == null || "".equals(netMaster)){
			return "";
		}
		
		boolean[] ipBinaryArg = getIpBinary(ipAddress);
		boolean[] masterBinaryArg = getIpBinary(netMaster);
		boolean[] mergeRes = new boolean[ipBinaryArg.length];
		char[] resArg = new char[mergeRes.length];
		int ipIndex_1, ipIndex_2, ipIndex_3, ipIndex_4;
		for(int i=0; i<mergeRes.length; i++){
			mergeRes[i] = ipBinaryArg[i] && masterBinaryArg[i];
		}
		for(int i=0; i<mergeRes.length; i++){
			if(mergeRes[i]){
				resArg[i] = '1';
			}else{
				resArg[i] = '0';
			}
		}
		String tempStr = String.valueOf(resArg);
		ipIndex_1 = Integer.parseInt(tempStr.substring(0, 8), 2);
		ipIndex_2 = Integer.parseInt(tempStr.substring(8, 16), 2);
		ipIndex_3 = Integer.parseInt(tempStr.substring(16, 24), 2);
		ipIndex_4 = Integer.parseInt(tempStr.substring(24, 32), 2);
		return ipIndex_1 + "." + ipIndex_2 + "."  + ipIndex_3 + "." + ipIndex_4;
	}
	
	private static boolean[] getIpBinary(String ipAddress){
		boolean[] res = new boolean[32];
		
		String[] ipArgs = ipAddress.split("\\.");
		for(int i=0; i<ipArgs.length; i++){
			String binaryStr = Integer.toBinaryString(Integer.valueOf(ipArgs[i]));
			while(binaryStr.length() < 8){
				binaryStr = "0" + binaryStr;
			}
			char[] binArray = binaryStr.toCharArray();
			for(int j=0; j<binArray.length; j++){
				res[i * 8 + j] = binArray[j] == '1';
			}
		}
		return res;
	}
	
	public static IpAddress getGlobalIpAddress(String ipAddress, String netMask){
		IpAddress ipAddr = new IpAddress();
		ipAddr.setTypeFlag(IpAddress.TYPE_IP_NETWORK);
		
		SingleTableItem ipItem = new SingleTableItem();
		ipItem.setType(SingleTableItem.TYPE_GLOBAL);
		ipItem.setIpAddress(ipAddress);
		ipItem.setNetmask(netMask);
		
		ipAddr.getItems().add(ipItem);
		
		return ipAddr;
	}
	
	public static IpAddress getGlobalHost(String hostName){
		IpAddress ipAddr = new IpAddress();
		ipAddr.setTypeFlag(IpAddress.TYPE_HOST_NAME);
		
		SingleTableItem ipItem = new SingleTableItem();
		ipItem.setType(SingleTableItem.TYPE_GLOBAL);
		ipItem.setIpAddress(hostName);
		
		ipAddr.getItems().add(ipItem);
		
		return ipAddr;
	}
	
	public static String escapeSpecialChar(String str){
		String resStr = str;
		if ((resStr.contains(" ") || resStr.contains("?") || resStr.contains("\"") || resStr.contains("\\"))) {
			resStr = resStr.replace("\\", "\\\\");
			resStr = resStr.replace("\"", "\\\"");
			resStr = "\"" + resStr + "\"";
		}
		return resStr;
	}
	
	public static boolean isIpAddress(String ipAddr){
		String regex = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(ipAddr);
		return matcher.matches();
	}
	
	public static AhEnable createAhEnable(boolean isEnable){
		AhEnable enableObj = new AhEnable();
		
		enableObj.setEnable(CLICommonFunc.getAhOnlyAct(isEnable));
		
		return enableObj;
	}
	
	public static String filterNetmaskFromIpStr(String ipStr){
		String regex = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}(/\\d{1,2})+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(ipStr);
		if(matcher.matches()){
			return ipStr.substring(0, ipStr.indexOf("/"));
		}else{
			return ipStr;
		}
	}
	
	public static List<String> mergeRange(List<String> sourceList) {
		List<String> resList = new ArrayList<String>();
		if(sourceList == null || sourceList.isEmpty()){
			return resList;
		}
		
		boolean[] argMerge = new boolean[MAX_MERGE_RANGE];
		for(String strSource : sourceList) {
			if(strSource.indexOf("-") > 0){
				int frome, to;
				frome = Integer.valueOf((strSource.substring(0, strSource.indexOf("-"))).trim());
				to = Integer.valueOf((strSource.substring(strSource.indexOf("-") +1)).trim());
				for(int i=frome; i<=to; i++){
					argMerge[i] = true;
				}
			}else{
				try{
					argMerge[Integer.valueOf(strSource.trim())] = true;
				}catch(Exception ex){
					
				}
			}
		}
		
		int fromAttr = -1, toAttr = -1;
		for(int i=0; i<argMerge.length; i++){
			if(argMerge[i]) {
				toAttr = i;
				if(fromAttr == -1){
					fromAttr = i;
				}
			}else{
				if(fromAttr == toAttr && toAttr != -1) {
					resList.add(String.valueOf(fromAttr));
					fromAttr = -1;
					toAttr = -1;
				}else if(fromAttr >=0 && toAttr >=0 && fromAttr != toAttr) {
					resList.add(String.valueOf(fromAttr) + " - " + String.valueOf(toAttr));
					fromAttr = -1;
					toAttr = -1;
				}
			}
		}
		return resList;
	}
	
	public static List<String> mergeRangeList(String strSource) {
		if(strSource == null || "".equals(strSource)){
			return new ArrayList<String>();
		}
		String[] attrArr = strSource.split(",");
		List<String> sourceList = new ArrayList<String>();
		for(int i=0; i<attrArr.length; i++) {
			sourceList.add(attrArr[i]);
		}
		List<String> resList = mergeRange(sourceList);
		return resList;
	}
	
	public static String mergeRange(String strSource){
		if(strSource == null || "".equals(strSource)){
			return "";
		}
		String[] attrArr = strSource.split(",");
		List<String> sourceList = new ArrayList<String>();
		for(int i=0; i<attrArr.length; i++){
			sourceList.add(attrArr[i]);
		}
		List<String> resList = mergeRange(sourceList);
		String resStr = "";
		for(String strTemp : resList){
			if("".equals(resStr)){
				resStr += strTemp;
			}else{
				resStr += "," + strTemp;
			}
		}
		return resStr;
	}
	
	public static void mergeRange(boolean[] vlanArgs, String strSource){
		if(strSource == null || "".equals(strSource) || "all".equalsIgnoreCase(strSource)){
			return;
		}
		
		String[] attrArr = strSource.split(",");
		for(int index=0; index<attrArr.length; index++) {
			String vlanStr = attrArr[index];
			if(vlanStr.indexOf("-") > 0){
				int frome, to;
				frome = Integer.valueOf((vlanStr.substring(0, vlanStr.indexOf("-"))).trim());
				to = Integer.valueOf((vlanStr.substring(vlanStr.indexOf("-") +1)).trim());
				for(int i=frome; i<=to; i++){
					vlanArgs[i] = true;
				}
			}else{
				try{
					vlanArgs[Integer.valueOf(vlanStr.trim())] = true;
				}catch(Exception ex){
					
				}
			}
		}
	}
	
	public static Vlan getMappingVlan(UserProfile userProfile, ConfigTemplate policy){
		if(userProfile == null){
			return null;
		}else if(policy == null){
			return userProfile.getVlan();
		}else if(policy.getUpVlanMapping() != null){
			for(UserProfileVlanMapping mapping : policy.getUpVlanMapping()){
				if(mapping.getUserProfile() != null && mapping.getUserProfile().getId().equals(userProfile.getId())){
					return mapping.getVlan();
				}
			}
		}else {
			return userProfile.getVlan();
		}
		return userProfile.getVlan();
	}
	
	public static boolean isContainsPort(short portNum, String portStr){
		if(!portStr.startsWith(PortBasicProfile.PORTS_SPERATOR)){
			portStr = PortBasicProfile.PORTS_SPERATOR + portStr;
		}
		if(!portStr.endsWith(PortBasicProfile.PORTS_SPERATOR)){
			portStr += PortBasicProfile.PORTS_SPERATOR;
		}
		String pNum = PortBasicProfile.PORTS_SPERATOR + portNum + PortBasicProfile.PORTS_SPERATOR;
		return portStr.contains(pNum);
	}
	
	public static void writeXml(Document doc, String path, boolean isPretty) throws IOException{
		OutputFormat format = null;
		String os = System.getProperties().getProperty("os.name").toLowerCase();
		if(isPretty){
			format = new OutputFormat();
//			format = OutputFormat.createCompactFormat();
//			format = OutputFormat.createPrettyPrint();
		}else{
			format = OutputFormat.createCompactFormat();
		}
//		format.setPadText(true);
		format.setIndent("\t");
//		format.setIndentSize(1);
//		format.setIndent(true);
		format.setNewlines(true);
//		format.setXHTML(true);
		if(os.contains("win")){
			format.setLineSeparator("\r\n");
		}else{
			format.setLineSeparator("\n");
		}
		format.setTrimText(true);
		
		XMLWriter writer = new XMLWriter(new FileWriter(path), format);
		writer.write(doc);
		writer.close();
	}
	
	public static Document readXml(String path) throws DocumentException{
		SAXReader reader = new SAXReader();
		Document destDoc = reader.read(new File(path));
		reader = null;
		return destDoc;
	}
	
	public static String getDBPasswordKey(String deviceId){
		return dbPasswordKeyIns.encrypt(deviceId);
	}

	public static void main(String[] args) throws CreateXMLException {
//		System.out.println(mergeRange("1 - 2, 5, 8 ,9"));
		System.out.println(isTagMatch("", "", false));
	}

}