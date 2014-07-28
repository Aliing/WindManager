package com.ah.util.notificationmsg;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.devices.impl.Device;

public class AhNotificationMsgUtil {
    
    private static Map<String[], String> maxVersionMap   = new HashMap<String[], String>();
    private static Map<EnumItem, String> deviceMaxVerMap = new TreeMap<EnumItem, String>(new DeviceVerTypeComparator());
    private static Map<EnumItem, String> deviceMaxVerDescMap = new TreeMap<EnumItem, String>(new DeviceVerAlphComparator());
    
    /**
     * Get the latest supported version of device map. The supported versions are defined in <u>devices.properties</u>.<br/>
     * e.g., the map key({@link EnumItem}) is {key: 1,value: "HiveAP20"}, and the map value is the latest version string.
     * @author Yunzhi Lin
     * - Time: Nov 23, 2011 5:59:24 PM
     * @return HashMap 
     */
    public static Map<EnumItem, String> getLatestDeviceSupportVersionMap() {
        if(deviceMaxVerMap.isEmpty()) {
            for(int index=0;index<HiveAp.HIVEAP_MODEL.length;index++){
                EnumItem deviceModel = HiveAp.HIVEAP_MODEL[index];
                String maxVersion = getDeviceOSMaxVersionByModel(deviceModel);
                //DebugUtil.debug("get device: "+deviceModel.getValue()+"'s max version is: "+maxVersion);
                deviceMaxVerMap.put(deviceModel, maxVersion);
            }
        }
        return deviceMaxVerMap;
    }
    
    public static Map<EnumItem, String> getLatestDeviceSupportVersionDescMap() {
        if (deviceMaxVerDescMap.isEmpty()) {
            String version = null;
            for (Entry<EnumItem, String> entry : deviceMaxVerMap.entrySet()) {
                version = entry.getValue();
                deviceMaxVerDescMap.put(entry.getKey(),
                        StringUtils.isNotBlank(version) ? MgrUtil.getHiveOSDisplayVersion(version)
                                : "N/A");
            }
        }
        return deviceMaxVerDescMap;
    }

    private static String getDeviceOSMaxVersionByModel(EnumItem deviceModel) {
        String[] versions = AhConstantUtil.getEnumValues(Device.SUPPORTED_HIVEOS_VERSIONS, (short)deviceModel.getKey());
        String maxVersionStr = maxVersionMap.get(versions);
        
        if(null == maxVersionStr) {
            //debug("get device: "+deviceModel.getValue()+"'s versions"+Arrays.toString(versions));
            int maxVersionValue = 0;
            for (String version : versions) {
                String parsedVersion= StringUtils.remove(version, ".");
                int osVersionIntValue = 0;
                try {
                    osVersionIntValue = StringUtils.isNumeric(parsedVersion) ? Integer.parseInt(parsedVersion):0;
                    if(maxVersionValue < osVersionIntValue) {
                        maxVersionValue = osVersionIntValue;
                        maxVersionStr = version;
                    }
                } catch (NumberFormatException e) {
                    // Dump
                }
            }
            maxVersionMap.put(versions, maxVersionStr);
        }
        return maxVersionStr;
    }

    public static void refreshNotificationMsgs(Object action) {
        AhNotificationMsgPool msgPool = (AhNotificationMsgPool) MgrUtil
                .getSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_POOL);
        if (null != msgPool) {
            msgPool.refreshMsgs(action);
        }
    }
    
    public static HmDomain getCurrentDomain(HmUser userContext) {
        return userContext.getSwitchDomain() != null ? userContext.getSwitchDomain() : userContext
                .getDomain();
    }
    
    private static final class DeviceVerTypeComparator implements Comparator<EnumItem> {
        @Override
        public int compare(EnumItem item1, EnumItem item2) {
            if(null != item1 && null != item2) {
                return item1.getKey() - item2.getKey();
            } else {
                return 0;
            }
        }
    }
    
    private static final class DeviceVerAlphComparator implements Comparator<EnumItem> {
        @Override
        public int compare(EnumItem item1, EnumItem item2) {
            if(null != item1 && null != item2) {
                return item1.getValue().compareToIgnoreCase(item2.getValue());
            } else {
                return 0;
            }
        }
    }
}
