package com.ah.be.parameter.constant.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.common.AhDirTools;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.constant.AhConstantConfigParsable;
import com.ah.be.parameter.constant.AhConstantConfigParsable.ConstantConfigType;
import com.ah.be.parameter.constant.AhConstantConfigParsedException;
import com.ah.be.parameter.constant.parser.AhDeviceProductNameParser.DeviceProductType;
import com.ah.be.parameter.constant.parser.AhDeviceStyleParser.DeviceStyle;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.devices.ValueTypes;
import com.ah.util.devices.impl.Device;

public final class AhConstantUtil {

	private static final Tracer log = new Tracer(AhConstantUtil.class.getSimpleName());

	private static final String DEVICES_PROPERTIES = "com.ah.util.devices.conf.devices";

	private static List<?> constantConfigElems;

	private static final Map<ConstantConfigType, AhConstantConfigParsable> constantConfigParsers = new EnumMap<ConstantConfigType, AhConstantConfigParsable>(
			ConstantConfigType.class);

	/** Available HiveAP Product Names */
	private static Map<DeviceProductType, Collection<String>> hiveApProductNames;
	
	private static Map<String, Collection<DeviceStyle>> devicePageStyleMap;
	
	private static Collection<Short> teacherViewSupportDevices = null;

	private static Map<Device, Object> deviceSettings;

	/** MacOui names */
	public static final Map<String, String> macOuiDict = new HashMap<String, String>();

	public static final String[] APPLE_MAC_OUI_LIST = new String[]{"000393", "000502", "000A27", "000A95", "000D93", "0010FA", "001124",
		"001451", "0016CB", "0017F2", "0019E3", "001B63", "001CB3", "001D4F", "001E52", "001EC2", "001F5B", "001FF3", "0021E9", "002241",
		"002312", "002332", "00236C", "0023DF", "002436", "002500", "00254B", "0025BC", "002608", "00264A", "0026B0", "0026BB", "003065",
		"0050E4", "00A03F", "00A040", "041E64", "080007", "34159E", "40D32D", "58B035", "60FB42", "64B9E8", "7C6D62", "90840D", "D49A20",
		"F81EDF"};

	public static final String[] APPLE_COMPANY_NAME_LIST = new String[] {
		"apple computer, inc.",
		"apple computer, inc",
		"apple computer inc.",
		"apple computer",
		"apple, inc.",
		"apple, inc",
		"apple inc.",
		"apple inc",
		"apple",
		"computer society microprocessor & microprocessor standards c"
	};
	
	public static final String[] AEROHIVE_COMPANY_NAME_LIST = new String[] {
		"aerohive networks, inc.",
		"aerohive networks inc."
	};
	
	public static final String[] DELL_MAC_OUI_LIST = new String[]{"00065B", "000874", "000BDB", "000D56", "000F1F",
		"001143", "00123F", "001372", "001422", "0015C5",
		"00188B", "0019B9", "001AA0", "001C23", "001D09", "001E4F", "001EC9", "002170", "00219B", "002219",
		"0023AE", "0024E8", "002564", "0026B9", "00B0D0", "14FEB5", "180373", "24B6FD", "5C260A", "5CF9DD",
		"782BCB", "7845C4", "842B2B", "848F69", "90B11C", "A41F72", "A4BADB", "B8AC6F", "B8CA3A", "BC305B",
		"D067E5", "D4AE52", "D4BED9", "E0DB55", "F04DA2"};

	public static final String[] DELL_COMPANY_NAME_LIST = new String[] {
		"Dell Computer Corp.",
		"DELL INC.",
		"Dell Inc",
		"Dell Inc.",
		"Dell",
		"Dell ESG PCBA Test",
		"Dell PCBA Test",
		"WW PCBA Test"
	};

	public static final String APPLE_COMPANY_NAME = "Apple Inc";
	public static final String DELL_COMPANY_NAME = "Dell Inc";
	public static final String AEROHIVE_COMPANY_NAME = "Aerohive Networks Inc.";

	static {
		String constantControlConfig = AhDirTools.getConstantConfigDir()
				+ "ah_constant_control_config.xml";

		try {
			loadConstantControlConfig(constantControlConfig);

			if (deviceSettings == null) {
				deviceSettings = new HashMap<Device, Object>();

				log.info("init", "Loading Device settings.");
				loadDeviceSettings();
				log.info("init", "Device settings have been loaded.");
			}
		} catch (Throwable e) {
			log.error("initializer", "Loading constant control config failed.", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void loadConstantControlConfig(String constantConfigPath)
			throws AhConstantConfigParsedException {
		if (constantConfigPath == null || constantConfigPath.trim().equals("")) {
			throw new AhConstantConfigParsedException("Invalid argument " + constantConfigPath);
		}

		loadConstantControlConfig(new File(constantConfigPath));
	}

	public static void loadConstantControlConfig(File constantConfig)
			throws AhConstantConfigParsedException {
		String constantConfigName = constantConfig.getName();

		if (!constantConfig.exists()) {
			throw new AhConstantConfigParsedException("Constant control config "
					+ constantConfigName + " doesn't exist.");
		}

		log.info("loadConstantControlConfig", "Loading constant control config "
				+ constantConfigName);
		SAXReader reader = new SAXReader();

		try {
			Document document = reader.read(constantConfig);
			Element root = document.getRootElement();
			constantConfigElems = root.elements();
		} catch (DocumentException de) {
			throw new AhConstantConfigParsedException("Loading constant control config "
					+ constantConfigName + " failed because of wrong file format.");
		}

		log.info("loadConstantControlConfig", "Constant control config " + constantConfigName
				+ " was loaded.");
	}

	public static void loadAllConstantConfigs() throws AhConstantConfigParsedException {
		if (constantConfigElems == null) {
			throw new AhConstantConfigParsedException("Constant control config was not loaded.");
		}

		for (Object constantConfigElem : constantConfigElems) {
			// Load each constant config contained in the constant control file.
			loadConstantConfig((Element) constantConfigElem);
		}
	}

	public static void loadConstantConfig(ConstantConfigType constantConfigType,
			String constantConfigPath) throws AhConstantConfigParsedException {
		if (constantConfigType == null || constantConfigPath == null
				|| constantConfigPath.trim().equals("")) {
			throw new AhConstantConfigParsedException("Argument is invalid");
		}

		loadConstantConfig(constantConfigType, new File(constantConfigPath));
	}

	public static void loadConstantConfig(ConstantConfigType constantConfigType, File constantConfig)
			throws AhConstantConfigParsedException {
		if (constantConfigType == null || constantConfig == null) {
			throw new AhConstantConfigParsedException("Argument is invalid");
		}

		String constantConfigName = constantConfig.getName();

		if (!constantConfig.exists()) {
			throw new AhConstantConfigParsedException("Constant config " + constantConfigName
					+ " doesn't exist.");
		}

		AhConstantConfigParsable constantConfigParser = constantConfigParsers
				.get(constantConfigType);

		if (constantConfigParser == null) {
			throw new AhConstantConfigParsedException(
					"Could not find parser class relative to the constant config: "
							+ constantConfigName);
		}

		Object obj = constantConfigParser.parse(constantConfig);
		loadConstantInfo(constantConfigType, obj);
	}

	private static void loadConstantConfig(Element constantConfigElem)
			throws AhConstantConfigParsedException {
		String constantType = constantConfigElem.getName();
		String constantConfigName = constantConfigElem.attributeValue("filename");

		if (constantConfigName == null) {
			throw new AhConstantConfigParsedException(
					"Lack 'filename' attribute in the element of " + constantType);
		}

		String constantConfigParserClass = constantConfigElem.attributeValue("class");

		if (constantConfigParserClass == null) {
			throw new AhConstantConfigParsedException("Lack 'class' attribute in the element of "
					+ constantType);
		}

		log.info("loadConstantConfig", "Parsing constant config " + constantConfigName
				+ " for constant type " + constantType);

		try {
			ConstantConfigType constantConfigType = ConstantConfigType.valueOf(constantType);
			AhConstantConfigParsable constantConfigParser = (AhConstantConfigParsable) Class
					.forName(constantConfigParserClass).newInstance();
			String constantConfigPath = AhDirTools.getConstantConfigDir() + constantConfigName;
			File constantConfig = new File(constantConfigPath);
			Object parsingResult = constantConfigParser.parse(constantConfig);
			loadConstantInfo(constantConfigType, parsingResult);
			constantConfigParsers.put(constantConfigType, constantConfigParser);
		} catch (IllegalArgumentException iae) {
			throw new AhConstantConfigParsedException("Unknown constant config type "
					+ constantType);
		} catch (ClassNotFoundException cnfe) {
			throw new AhConstantConfigParsedException("Constant config parser class["
					+ constantConfigParserClass + "] is not found.", cnfe);
		} catch (InstantiationException ie) {
			throw new AhConstantConfigParsedException("Constant config parser class["
					+ constantConfigParserClass + "] cannot be constructed.", ie);
		} catch (IllegalAccessException iae) {
			throw new AhConstantConfigParsedException("Constant config parser class["
					+ constantConfigParserClass + "] cannot be constructed or accessed.", iae);
		}

		log.info("loadConstantConfig", "Constant config " + constantConfigName
				+ " for constant type " + constantType + " was parsed.");
	}

	private static void loadConstantInfo(ConstantConfigType constantConfigType, Object parsingResult) {
		switch (constantConfigType) {
		case hiveap_product_name:
			loadHiveApProductNames((Map<DeviceProductType, Collection<String>>) parsingResult);
			break;
		case device_page_style:
			loadDevicePageStyleMap((Map<String, Collection<DeviceStyle>>) parsingResult);
			break;
		default:
			log.warning("loadConstantInfo", "Unknown Constant config: "
					+ constantConfigType.toString());
			break;
		}
	}

	private static void loadHiveApProductNames(Map<DeviceProductType, Collection<String>> productNames) {
		hiveApProductNames = productNames;
	}
	
	private static void loadDevicePageStyleMap(Map<String, Collection<DeviceStyle>> result){
		devicePageStyleMap = result;
	}

	public static Device getDeviceByModel(short model) {
		Device dev;

		switch (model) {
		case HiveAp.HIVEAP_MODEL_20:
			dev = Device.AP20;
			break;
		case HiveAp.HIVEAP_MODEL_28:
			dev = Device.AP28;
			break;
		case HiveAp.HIVEAP_MODEL_110:
			dev = Device.AP110;
			break;
		case HiveAp.HIVEAP_MODEL_120:
			dev = Device.AP120;
			break;
		case HiveAp.HIVEAP_MODEL_170:
			dev = Device.AP170;
			break;
		case HiveAp.HIVEAP_MODEL_320:
			dev = Device.AP320;
			break;
		case HiveAp.HIVEAP_MODEL_330:
			dev = Device.AP330;
			break;
		case HiveAp.HIVEAP_MODEL_340:
			dev = Device.AP340;
			break;
		case HiveAp.HIVEAP_MODEL_350:
			dev = Device.AP350;
			break;
		case HiveAp.HIVEAP_MODEL_370:
			dev = Device.AP370;
			break;	
		case HiveAp.HIVEAP_MODEL_230:
			dev = Device.AP230;
			break;				
		case HiveAp.HIVEAP_MODEL_380:
			dev = Device.AP380;
			break;
		case HiveAp.HIVEAP_MODEL_390:
			dev = Device.AP390;
			break;	
		case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
			dev = Device.CVG;
			break;
		case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
			dev = Device.CVGAPPLIANCE;
			break;	
		case HiveAp.HIVEAP_MODEL_BR100:
			dev = Device.BR100;
			break;
		case HiveAp.HIVEAP_MODEL_BR200:
			dev = Device.BR200;
			break;
		case HiveAp.HIVEAP_MODEL_BR200_WP:
			dev = Device.BR200_WP;
			break;
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
			dev = Device.BR200_LTE_VZ;
			break;
		case HiveAp.HIVEAP_MODEL_121:
			dev = Device.AP121;
			break;
		case HiveAp.HIVEAP_MODEL_141:
			dev = Device.AP141;
			break;
		case HiveAp.HIVEAP_MODEL_SR24:
			dev = Device.SR24;
			break;
		case HiveAp.HIVEAP_MODEL_SR2124P:
			dev = Device.SR2124P;
			break;
		case HiveAp.HIVEAP_MODEL_SR2148P:
			dev = Device.SR2148P;
			break;
		case HiveAp.HIVEAP_MODEL_SR48:
			dev = Device.SR48;
			break;
		case HiveAp.HIVEAP_MODEL_SR2024P:
			dev = Device.SR2024P;
			break;			
		default:
			dev = null;
			break;
		}
		return dev;
	}

	public static short getModelByDevice(Device device) {
		short model = HiveAp.HIVEAP_MODEL_20;
		switch (device) {
		case AP20:
			model = HiveAp.HIVEAP_MODEL_20;
			break;
		case AP28:
			model = HiveAp.HIVEAP_MODEL_28;
			break;
		case AP110:
			model = HiveAp.HIVEAP_MODEL_110;
			break;
		case AP120:
			model = HiveAp.HIVEAP_MODEL_120;
			break;
		case AP170:
			model = HiveAp.HIVEAP_MODEL_170;
			break;
		case AP320:
			model = HiveAp.HIVEAP_MODEL_320;
			break;
		case AP330:
			model = HiveAp.HIVEAP_MODEL_330;
			break;
		case AP340:
			model = HiveAp.HIVEAP_MODEL_340;
			break;
		case AP350:
			model = HiveAp.HIVEAP_MODEL_350;
			break;
		case AP370:
			model = HiveAp.HIVEAP_MODEL_370;
			break;
		case AP230:
			model = HiveAp.HIVEAP_MODEL_230;
			break;			
		case AP380:
			model = HiveAp.HIVEAP_MODEL_380;
			break;
		case AP390:
			model = HiveAp.HIVEAP_MODEL_390;
			break;
		case CVG:
			model = HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA;
			break;
		case CVGAPPLIANCE:
			model = HiveAp.HIVEAP_MODEL_VPN_GATEWAY;
			break;			
		case BR100:
			model = HiveAp.HIVEAP_MODEL_BR100;
			break;
		case BR200:
			model = HiveAp.HIVEAP_MODEL_BR200;
			break;
		case BR200_WP:
			model = HiveAp.HIVEAP_MODEL_BR200_WP;
			break;
		case BR200_LTE_VZ:
			model = HiveAp.HIVEAP_MODEL_BR200_LTE_VZ;
			break;
		case AP121:
			model = HiveAp.HIVEAP_MODEL_121;
			break;
		case AP141:
			model = HiveAp.HIVEAP_MODEL_141;
			break;
		case SR24:
			model = HiveAp.HIVEAP_MODEL_SR24;
			break;
		case SR2124P:
			model = HiveAp.HIVEAP_MODEL_SR2124P;
			break;
		case SR2024P:
			model = HiveAp.HIVEAP_MODEL_SR2024P;
			break;			
		case SR2148P:
			model = HiveAp.HIVEAP_MODEL_SR2148P;
			break;			
		case SR48:
			model = HiveAp.HIVEAP_MODEL_SR48;
			break;
		default:
			model = HiveAp.HIVEAP_MODEL_20;
			break;
		}
		return model;
	}

	public static short getDeviceTypeByDevice(Device device) {
		short deviceType = HiveAp.Device_TYPE_HIVEAP;
		switch (device) {
		case CVG:
		case CVGAPPLIANCE:
			deviceType = HiveAp.Device_TYPE_VPN_GATEWAY;
			break;
		case BR100:
		case BR200:
		case BR200_LTE_VZ:
		case BR200_WP:
			deviceType = HiveAp.Device_TYPE_BRANCH_ROUTER;
			break;
		default:
			deviceType = HiveAp.Device_TYPE_HIVEAP;
			break;
		}
		return deviceType;
	}

	public static String getStrings(Device prop, Device value, Boolean... type) {
		Boolean _type = (type.length <= 0 ? true : type[0]);
		List<Device> deviceObjects = getDeviceObjects(prop, _type);
		if (deviceObjects == null) return null;
		if (deviceObjects.isEmpty()) return "";
		StringBuffer sb = new StringBuffer();
		for (Device device : deviceObjects) {
			Boolean isTrue = isTrueAll(prop, getModelByDevice(device));
			if (isTrue == null) continue;
			if (!_type.equals(isTrue)) continue;
			String valstr = getStringAll(value, getModelByDevice(device));
			if (valstr != null) sb.append(valstr + ",");
		}
		if (sb.length() > 0 ) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	public static String getStringAll(Device prop, short model) {
		String strValueAll = getString(prop);
		String strValue = getString(prop, model);

		return strValue == null ? strValueAll : strValue;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] getEnumValues(Device prop, short... model) {
		if (prop.getTypeTree()[0] != ValueTypes.Enum || prop.getTypeTree().length < 2) return null;
		T[] result = null;

		if (model.length > 0) {
			Device dev = getDeviceByModel(model[0]);
			if (dev == null) return null;
			Object obj = deviceSettings.get(dev);
			if (obj != null){
				Object[] objs = (Object[])obj;
				result = (T[]) ((HashMap<Device, Object>)objs[1]).get(prop);
			} else {
				return null;
			}
		}
		if (result == null) {
			return (T[])deviceSettings.get(prop);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static String getString(Device prop, short... model) {
		if (prop.getTypeTree()[0] instanceof DeviceProductType) {
			return null;
		} else if (prop.getTypeTree()[0] instanceof ValueTypes) {
			if (prop.getTypeTree()[0] != ValueTypes.String) return null;
			if (model.length > 0) {
				Device dev = getDeviceByModel(model[0]);
				if (dev == null) return null;
				Object obj = deviceSettings.get(dev);
				if (obj != null){
					Object[] objs = (Object[])obj;
					return (String)((HashMap<Device, Object>)objs[1]).get(prop);
				} else return null;
			} else {
				return (String)deviceSettings.get(prop);
			}
		} else {
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public static List<Device> getDeviceObjects(Device prop, Boolean... type) {
		Boolean _type = (type.length <= 0 ? true : type[0]);
		List<Device> devices = new ArrayList<Device>();
		if (prop == Device.ALL) {
			Object obj = deviceSettings.get(prop);
			if (obj != null) {
				List<Device> allDev = (List<Device>)obj;
				for (Device device : allDev) {
					Object obj1 = deviceSettings.get(device);
					if (obj1 != null) {
						Object[] objs = (Object[])obj1;
						if (_type.equals(objs[0])) {
							devices.add(device);
						}
					} else continue;
				}
				return devices;
			} else return null;
		} else if (prop.getTypeTree()[0] instanceof DeviceProductType) {
			Object obj = deviceSettings.get(prop);
			if (obj != null && ((Object[])obj)[0].equals(Boolean.TRUE)) {
				Map<Device, Object> props = (HashMap<Device, Object>)((Object[])obj)[1];
				for (Device val : props.keySet()) {
					devices.add(val);
				}
				return devices;
			} else return null;
		} else if (prop.getTypeTree()[0] instanceof ValueTypes) {
			if (prop.getTypeTree()[0] != ValueTypes.Boolean) return null;

			Object obj = deviceSettings.get(Device.ALL);
			if (obj != null) {
				List<Device> allDev = (List<Device>)obj;
				for (Device device : allDev) {
					Boolean isTrue = isTrueAll(prop, getModelByDevice(device));
					if (isTrue == null) continue;
					if (isTrue == _type) devices.add(device);
				}
				return devices;
			} else return null;
		}
		return null;
	}

	/**
	 * return property value(Boolean) with GLOBAL setting override
	 *
	 * @param prop
	 * @param model
	 * @return
	 */
	public static Boolean isTrueAll(Device prop, short model) {
		Boolean isTrueAll = isTrue(prop);
		if (isTrueAll == null) return null;

		Boolean isTrue = isTrue(prop, model);

		return isTrue == null ? isTrueAll : isTrue;
	}

	@SuppressWarnings("unchecked")
	public static Boolean isTrue(Device prop, short... model) {
		if (prop == null) return null;
		if (prop.getTypeTree()[0] instanceof DeviceProductType) {
			Object obj = deviceSettings.get(prop);
			if (obj != null) {
				Object[] objs = (Object[])obj;
				return (Boolean)objs[0];
			} else return null;
		} else if (prop.getTypeTree()[0] instanceof ValueTypes){
			if (prop.getTypeTree()[0] != ValueTypes.Boolean) return null;
			if (model.length > 0) {
				Device dev = getDeviceByModel(model[0]);
				if (dev == null) return null;
				Object obj = deviceSettings.get(dev);
				if (obj != null){
					Object[] objs = (Object[])obj;
					return (Boolean)((HashMap<Device, Object>)objs[1]).get(prop);
				} else return null;
			} else {
				return (Boolean)deviceSettings.get(prop);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadDeviceSettings() {
		try {
			ResourceBundle rb = MgrUtil.getBundle(DEVICES_PROPERTIES);

			Set<String> keys = rb.keySet();
			List<Device[]> props = new ArrayList<Device[]>();
			Map<Object, String> vals = new HashMap<Object, String>();
			for (String key : keys) {
				int idx = key.indexOf(".");
				if (idx < 0) {
					try {
						Device dev = Device.valueOf(key);
						deviceSettings.put(dev, parseValue(dev.getTypeTree(), rb.getString(key)));
						if (dev.getTypeTree()[0] instanceof DeviceProductType) {
							List<Device> allDevice = null;
							Object aDev = deviceSettings.get(Device.ALL);
							if (aDev == null) {
								allDevice = new ArrayList<Device>();
								deviceSettings.put(Device.ALL, allDevice);
							} else {
								allDevice = (List<Device>)aDev;
							}
							allDevice.add(dev);
						}
					} catch (IllegalArgumentException ex) {
						log.error("loadDeviceSettings", "Unknown type key " + key, ex);
					} catch (Exception e) {
						log.error("loadDeviceSettings", e);
					}
				} else {
					String deviceKey = key.substring(0, idx);
					String propKey = key.substring(idx + 1);

					try {
						Device device = Device.valueOf(deviceKey);
						Device prop = Device.valueOf(propKey);

						Device[] dvp = new Device[]{device, prop};
						props.add(dvp);
						vals.put(dvp, rb.getString(key));

					} catch (IllegalArgumentException ex) {
						log.error("loadDeviceSettings", "Unknown device/type key " + deviceKey + "/" + propKey, ex);
					} catch (Exception e) {
						log.error("loadDeviceSettings", e);
					}
				}
			}
			for (Device[] objs : props) {
				if (!(objs[0].getTypeTree()[0] instanceof DeviceProductType)) continue;

				if (!(objs[1].getTypeTree()[0] instanceof ValueTypes)) continue;

				Object data = deviceSettings.get(objs[0]);
				if (data == null) {
					data = new Object[]{true, new HashMap<Device, Object>()};
					deviceSettings.put((Device)objs[0], data);
				}

				Object[] deValues = (Object[])data;
				@SuppressWarnings("unchecked")
				HashMap<Device, Object> devMap = (HashMap<Device, Object>)deValues[1];

				devMap.put(objs[1], parseValue(objs[1].getTypeTree(), vals.get(objs)));
			}
		} catch (Exception e) {
			log.error("loadDeviceSettings", e);
		}
	}

	private static Object parseValue(Object[] typeTree, String value) {
		Object type = typeTree[0];
		Object subType = typeTree.length > 1 ? typeTree[1] : ValueTypes.String;

		if (type == ValueTypes.Boolean) {
			return Boolean.valueOf(value);
		} else if (type == ValueTypes.Integer) {
			return Integer.valueOf(value);
		} else if (type == ValueTypes.Short) {
			return Short.valueOf(value);
		} else if (type == ValueTypes.Double) {
			return Double.valueOf(value);
		} else if (type == ValueTypes.Enum) {
			String[] strs = value.split(",");
			if (subType == ValueTypes.Integer) {
				Integer[] ints = new Integer[strs.length];
				for (int i = 0; i < strs.length; i++) {
					ints[i] = Integer.valueOf(strs[i]);
				}

				return ints;
			} else if (subType == ValueTypes.Short) {
				Short[] shts = new Short[strs.length];
				for (int i = 0; i < strs.length; i++) {
					shts[i] = Short.valueOf(strs[i]);
				}

				return shts;
			} else if(subType == ValueTypes.Double) {
				Double[] dubs = new Double[strs.length];
				for (int i = 0; i < strs.length; i++) {
					dubs[i] = Double.valueOf(strs[i]);
				}

				return dubs;
			} else {
				return strs;
			}
		} else if(type instanceof DeviceProductType){
			Object[] objs = new Object[]{value.equalsIgnoreCase("off") ? false:true, new HashMap<Device, Object>()};
			return objs;
		} else {
			return value;
		}
	}

	/**
	 * Put the mac oui dictionary info to a map.
	 */
	public static void loadMacOuiDictionary() {
		FileManager fileMg = FileManager.getInstance();

		try {
			// set mac oui for apple company
			for (String oui : APPLE_MAC_OUI_LIST) {
				macOuiDict.put(oui, APPLE_COMPANY_NAME);
			}
			
			for (String oui : DELL_MAC_OUI_LIST) {
				macOuiDict.put(oui, DELL_COMPANY_NAME);
			}
			
			// get all the message from the mac oui dictionary
			String[] allLines = fileMg.readFile(BeAdminCentOSTools.AH_NMS_MACOUI_DICT_FILE);

			for (String line : allLines) {
				line = line.trim();
				// the mac oui line contains this string
				if (line.contains("(hex)")) {
					// get the mac oui
					String key = (line.substring(0, 8)).replace("-", "");

					if (null != macOuiDict.get(key)) {
						continue;
					}

					// get the company name
					String value = line.substring(line.indexOf("(hex)") + 5).trim();
					if (value.toLowerCase().startsWith("apple")
							|| value.toLowerCase().startsWith("computer society microprocessor")) {
						boolean addFlg = false;
						for(String apName: APPLE_COMPANY_NAME_LIST){
							if (value.equalsIgnoreCase(apName)) {
								macOuiDict.put(key, APPLE_COMPANY_NAME);
								addFlg=true;
								break;
							}
						}
						if (!addFlg) {
							macOuiDict.put(key, value);
						}
					} else if (value.toLowerCase().startsWith("dell ")) {
						boolean addFlg = false;
						for(String apName: DELL_COMPANY_NAME_LIST){
							if (value.equalsIgnoreCase(apName)) {
								macOuiDict.put(key, DELL_COMPANY_NAME);
								addFlg=true;
								break;
							}
						}
						if (!addFlg) {
							macOuiDict.put(key, value);
						}
					} else if (value.toLowerCase().startsWith("aerohive ")) {
						boolean addFlg = false;
						for(String apName: AEROHIVE_COMPANY_NAME_LIST){
							if (value.equalsIgnoreCase(apName)) {
								macOuiDict.put(key, AEROHIVE_COMPANY_NAME);
								addFlg=true;
								break;
							}
						}
						if (!addFlg) {
							macOuiDict.put(key, value);
						}
					
					}else {
						macOuiDict.put(key, value);
					}
				}
			}
		} catch (Exception ex) {
			log.error("loadMacOuiDictionary", ex.getMessage());
		}
	}

	/**
	 * Get the company name by this mac oui.
	 *
	 * @param arg_Key
	 *            : mac oui
	 * @return String : company name
	 */
	public static String getMacOuiComName(String arg_Key) {
		if (null == arg_Key) {
			return null;
		}

		return macOuiDict.get(arg_Key.toUpperCase());
	}

	public static short getHiveApModelByProductName(String hiveApProductName) {
		short hiveApModel = HiveAp.HIVEAP_MODEL_20;

		for (DeviceProductType productType : hiveApProductNames.keySet()) {
			for (String name : hiveApProductNames.get(productType)) {
				if (name.equalsIgnoreCase(hiveApProductName)) {
					switch (productType) {
						case hiveap110:
							hiveApModel = HiveAp.HIVEAP_MODEL_110;
							break;
						case hiveap120:
							hiveApModel = HiveAp.HIVEAP_MODEL_120;
							break;
						case hiveap170:
							hiveApModel = HiveAp.HIVEAP_MODEL_170;
							break;
						case hiveap320:
							hiveApModel = HiveAp.HIVEAP_MODEL_320;
							break;
						case hiveap330:
							hiveApModel = HiveAp.HIVEAP_MODEL_330;
							break;
						case hiveap340:
						case hiveap5020:
							hiveApModel = HiveAp.HIVEAP_MODEL_340;
							break;
						case hiveap350:
							hiveApModel = HiveAp.HIVEAP_MODEL_350;
							break;
						case hiveap370:
							hiveApModel = HiveAp.HIVEAP_MODEL_370;
							break;
						case hiveap230:
							hiveApModel = HiveAp.HIVEAP_MODEL_230;
							break;							
						case hiveap380:
							hiveApModel = HiveAp.HIVEAP_MODEL_380;
							break;
						case hiveap390:
							hiveApModel = HiveAp.HIVEAP_MODEL_390;
							break;
						case hiveap28:
							hiveApModel = HiveAp.HIVEAP_MODEL_28;
							break;
						case cvg:
							hiveApModel = HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA;
							break;
						case cvgappliance:
							hiveApModel = HiveAp.HIVEAP_MODEL_VPN_GATEWAY;
							break;	
						case br100:
							hiveApModel = HiveAp.HIVEAP_MODEL_BR100;
							break;
						case br200:
							hiveApModel = HiveAp.HIVEAP_MODEL_BR200;
							break;
						case br200_wp:
							hiveApModel = HiveAp.HIVEAP_MODEL_BR200_WP;
							break;
						case br200_lte_vz:
							hiveApModel = HiveAp.HIVEAP_MODEL_BR200_LTE_VZ;
							break;
						case hiveap121:
							hiveApModel = HiveAp.HIVEAP_MODEL_121;
							break;
						case hiveap141:
							hiveApModel = HiveAp.HIVEAP_MODEL_141;
							break;
						case sr24:
							hiveApModel = HiveAp.HIVEAP_MODEL_SR24;
							break;
						case sr2124p:
							hiveApModel = HiveAp.HIVEAP_MODEL_SR2124P;
							break;
						case sr2024p:
							hiveApModel = HiveAp.HIVEAP_MODEL_SR2024P;
							break;							
						case sr2148p:
							hiveApModel = HiveAp.HIVEAP_MODEL_SR2148P;
							break;
						case sr48:
							hiveApModel = HiveAp.HIVEAP_MODEL_SR48;
							break;
						case hiveap20:
							hiveApModel = HiveAp.HIVEAP_MODEL_20;
							break;
						default:
							log.error("Set Device Type Produect Name", "The ProductName get from the Device does not belong to any type of HM prodects,ProductName:::" + hiveApProductName);
							hiveApModel = HiveAp.HIVEAP_MODEL_20;
							break;
					}

					return hiveApModel;
				}
			}
		}

		return hiveApModel;
	}
	
	public static boolean getHiveApModelSupportType(short hiveApModel, short deviceType) {
		switch (hiveApModel) {
		case HiveAp.HIVEAP_MODEL_28:
		case HiveAp.HIVEAP_MODEL_20:
		case HiveAp.HIVEAP_MODEL_320:
		case HiveAp.HIVEAP_MODEL_340:
		case HiveAp.HIVEAP_MODEL_380:
		case HiveAp.HIVEAP_MODEL_120:
		case HiveAp.HIVEAP_MODEL_110:
		case HiveAp.HIVEAP_MODEL_370:
		case HiveAp.HIVEAP_MODEL_390:
		case HiveAp.HIVEAP_MODEL_170:
		case HiveAp.HIVEAP_MODEL_121:
		case HiveAp.HIVEAP_MODEL_141:
			if (deviceType==HiveAp.Device_TYPE_HIVEAP) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_330:
		case HiveAp.HIVEAP_MODEL_350:
			if (deviceType==HiveAp.Device_TYPE_HIVEAP || deviceType==HiveAp.Device_TYPE_BRANCH_ROUTER) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_BR100:
			if (deviceType==HiveAp.Device_TYPE_HIVEAP || deviceType==HiveAp.Device_TYPE_BRANCH_ROUTER) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_BR200:
		case HiveAp.HIVEAP_MODEL_BR200_WP:
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
			if (deviceType==HiveAp.Device_TYPE_BRANCH_ROUTER) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
		case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
			if (deviceType==HiveAp.Device_TYPE_VPN_GATEWAY || deviceType==HiveAp.Device_TYPE_HIVEAP) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_SR24:
		case HiveAp.HIVEAP_MODEL_SR48:
		case HiveAp.HIVEAP_MODEL_SR2124P:
		case HiveAp.HIVEAP_MODEL_SR2024P:
			if (deviceType==HiveAp.Device_TYPE_SWITCH || deviceType==HiveAp.Device_TYPE_BRANCH_ROUTER) return true;
			else return false;
		case HiveAp.HIVEAP_MODEL_SR2148P:
			if (deviceType==HiveAp.Device_TYPE_SWITCH) return true;
			else return false;
		}
		return false;
	}

	public static String getHiveApProductNameByModel(short hiveApModel) {
		DeviceProductType productType;

		switch (hiveApModel) {
			case HiveAp.HIVEAP_MODEL_110:
				productType = DeviceProductType.hiveap110;
				break;
			case HiveAp.HIVEAP_MODEL_120:
				productType = DeviceProductType.hiveap120;
				break;
			case HiveAp.HIVEAP_MODEL_170:
				productType = DeviceProductType.hiveap170;
				break;
			case HiveAp.HIVEAP_MODEL_320:
				productType = DeviceProductType.hiveap320;
				break;
			case HiveAp.HIVEAP_MODEL_330:
				productType = DeviceProductType.hiveap330;
				break;
			case HiveAp.HIVEAP_MODEL_340:
				productType = DeviceProductType.hiveap340;
				break;
			case HiveAp.HIVEAP_MODEL_350:
				productType = DeviceProductType.hiveap350;
				break;
			case HiveAp.HIVEAP_MODEL_370:
				productType = DeviceProductType.hiveap370;
				break;
			case HiveAp.HIVEAP_MODEL_380:
				productType = DeviceProductType.hiveap380;
				break;
			case HiveAp.HIVEAP_MODEL_390:
				productType = DeviceProductType.hiveap390;
				break;
			case HiveAp.HIVEAP_MODEL_230:
				productType = DeviceProductType.hiveap230;
				break;				
			case HiveAp.HIVEAP_MODEL_28:
				productType = DeviceProductType.hiveap28;
				break;
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
				productType = DeviceProductType.cvg;
				break;
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
				productType = DeviceProductType.cvgappliance;
				break;				
			case HiveAp.HIVEAP_MODEL_BR100:
				productType = DeviceProductType.br100;
				break;
			case HiveAp.HIVEAP_MODEL_BR200:
				productType = DeviceProductType.br200;
				break;
			case HiveAp.HIVEAP_MODEL_BR200_WP:
				productType = DeviceProductType.br200_wp;
				break;
			case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
				productType = DeviceProductType.br200_lte_vz;
				break;
			case HiveAp.HIVEAP_MODEL_121:
				productType = DeviceProductType.hiveap121;
				break;
			case HiveAp.HIVEAP_MODEL_141:
				productType = DeviceProductType.hiveap141;
				break;
			case HiveAp.HIVEAP_MODEL_SR24:
				productType = DeviceProductType.sr24;
				break;
			case HiveAp.HIVEAP_MODEL_SR2124P:
				productType = DeviceProductType.sr2124p;
				break;
			case HiveAp.HIVEAP_MODEL_SR2024P:
				productType = DeviceProductType.sr2024p;
				break;				
			case HiveAp.HIVEAP_MODEL_SR2148P:
				productType = DeviceProductType.sr2148p;
				break;	
			case HiveAp.HIVEAP_MODEL_SR48:
				productType = DeviceProductType.sr48;
				break;
			case HiveAp.HIVEAP_MODEL_20:
			default:
				productType = DeviceProductType.hiveap20;
				break;
		}

		Collection<String> productNames = hiveApProductNames.get(productType);

		return productNames != null && !productNames.isEmpty() ? productNames.iterator().next() : "HiveAP020_ag";
	}

	public static short getDeviceTypeByProductName(String pName) {
		short deviceType = HiveAp.Device_TYPE_HIVEAP;

		for (DeviceProductType type : hiveApProductNames.keySet()) {
			for (String name : hiveApProductNames.get(type)) {
				if (name.equalsIgnoreCase(pName)) {
					switch (type) {
					case hiveap110:
					case hiveap120:
					case hiveap170:
					case hiveap320:
					case hiveap330:
					case hiveap340:
					case hiveap5020:
					case hiveap350:
					case hiveap370:
					case hiveap380:
					case hiveap390:
					case hiveap230:
					case hiveap28:
					case hiveap20:
					case hiveap121:
					case hiveap141:
						deviceType = HiveAp.Device_TYPE_HIVEAP;
						break;

					case cvg:
					case cvgappliance:
						deviceType = HiveAp.Device_TYPE_VPN_GATEWAY;
						break;

					case br100:
					case br200:
					case br200_wp:
					case br200_lte_vz:
						deviceType = HiveAp.Device_TYPE_BRANCH_ROUTER;
						break;
					case sr24:
					case sr2124p:
					case sr48:
						deviceType = HiveAp.Device_TYPE_SWITCH;
						break;
					default:
						deviceType = HiveAp.Device_TYPE_HIVEAP;
						break;
					}

					return deviceType;
				}
			}
		}
		return deviceType;
	}
	
	public static short getDeviceTypeByDeviceModel(short hiveApModel){
		short deviceType = HiveAp.Device_TYPE_HIVEAP;
		String productNames = getHiveApProductNameByModel(hiveApModel);
		deviceType = getDeviceTypeByProductName(productNames);
		return deviceType;
	}
	
	public static Map<String, Collection<DeviceStyle>> getDevicePageStyleMap(){
		return devicePageStyleMap;
	}
	
	public static Collection<Short> getTeacherViewSupportDevices(){
		if( teacherViewSupportDevices == null ){
			teacherViewSupportDevices = new ArrayList<Short>();
			List<Device> devices = AhConstantUtil.getDeviceObjects(Device.SUPPORTED_STUDENTMANAGER);
			if (null != devices) {
				for (Device device : devices) {
					if (!AhConstantUtil.isTrue(device)) continue;
					teacherViewSupportDevices.add(AhConstantUtil.getModelByDevice(device));
				}
			}
		}

		return teacherViewSupportDevices;
	}
}