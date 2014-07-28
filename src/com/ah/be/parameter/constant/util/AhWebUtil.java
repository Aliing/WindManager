package com.ah.be.parameter.constant.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.util.devices.ValueTypes;
import com.ah.util.devices.impl.Device;

public class AhWebUtil {
	private static String VAR = "var ";
	private static String EQ = " = ";
	private static String END = ";\n";

	private static String NEW_ARRAY = "new Array()";

	static {
		setAllDevicesJs();
	}

	private static String allDevicesJs;
	private static Map<String, String> boolValuesJs = new HashMap<String, String>();

	private AhWebUtil() {
	}

	private static void setAllDevicesJs() {
		allDevicesJs = "";
		List<Device> allDevices = AhConstantUtil.getDeviceObjects(Device.ALL);
		if (allDevices != null && allDevices.size() > 0) {
			for (Device device : allDevices) {
				allDevicesJs += VAR
						+ device.name()
						+ EQ
						+ AhConstantUtil.getString(Device.MODEL,
								AhConstantUtil.getModelByDevice(device)) + END;
			}
		}
	}

	private static void setBoolProp(Device bProp, Device... props) {
		if (bProp != Device.ALL && bProp.getTypeTree()[0] != ValueTypes.Boolean) return;
		String key = bProp.name() + getStringKey(props);
		if (boolValuesJs.containsKey(key)) return;

		String newArrayStr = VAR + key + EQ + NEW_ARRAY + END;

		List<Device> devices = AhConstantUtil.getDeviceObjects(bProp);
		if (devices != null && devices.size() > 0) {
			for (Device device : devices) {
				if (!AhConstantUtil.isTrue(device)) continue;

				short model = AhConstantUtil.getModelByDevice(device);
				if (props.length <= 0) {
					newArrayStr += key + ".push("
							+ model + ")" + END;
				} else {
					String others = "";
					for (Device prop : props) {
						if (prop.getTypeTree()[0] == ValueTypes.String) {
							String val = AhConstantUtil.getStringAll(prop, model);
							if (val == null) val = "";
							others += ",'" + val + "'";
						} else if (prop.getTypeTree()[0] == ValueTypes.Enum) {
							if (prop.getTypeTree()[1] == ValueTypes.String) {
								String[] enumVals = AhConstantUtil.getEnumValues(prop, model);
								if (enumVals != null && enumVals.length > 0) {
									String strs = "[";
									for (int i = 0; i < enumVals.length; i++) {
										if (i != (enumVals.length - 1)) {
											strs += "'" + enumVals[i] + "',";
										} else {
											strs += "'" + enumVals[i] + "']";
										}
									}
									others += "," + strs;
								}
							}
						}
					}

					newArrayStr += key + ".push([" + model + others + "])"
							+ END;
				}
			}
		}

		boolValuesJs.put(key, newArrayStr);
	}

	public static String getAllDevicesJs() {
		return allDevicesJs;
	}

	public static String getBoolArray(Device bProp, Device... props) {
		if (bProp != Device.ALL && bProp.getTypeTree()[0] != ValueTypes.Boolean) return "";
		String key = bProp.name() + getStringKey(props);

		if (boolValuesJs.containsKey(key)) {
			return boolValuesJs.get(key);
		} else {
			setBoolProp(bProp, props);

			if (boolValuesJs.containsKey(key)) {
				return boolValuesJs.get(key);
			} else return "";
		}
	}

	private static String getStringKey(Device... props) {
		String key = "";
		for (Device prop : props) {
			key += "_" + prop.name();
		}
		return key;
	}
}
