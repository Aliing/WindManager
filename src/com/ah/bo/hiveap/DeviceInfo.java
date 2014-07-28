package com.ah.bo.hiveap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmStartConfig;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.xml.deviceProperties.DeviceObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrOptionObj;

public class DeviceInfo extends DeviceObj {
	
	public static final String SPT_DEVICE_TYPE					= "spt_device_type";
	public static final String DEFAULT_DEVICE_TYPE				= "default_device_type";
	public static final String SPT_RADIO_COUNTS					= "spt_radio_counts";
	public static final String SPT_ETHERNET_COUNTS				= "spt_ethernet_counts";
	public static final String SPT_SFP_COUNTS					= "spt_sfp_counts";
	public static final String SPT_SFP_INDEX					= "spt_sfp_index";
	public static final String SPT_USB_COUNTS					= "spt_usb_counts";
	public static final String SPT_DYNAMIC_ROUTING				= "spt_dynamic_routing";
	public static final String SPT_RADIUS_SERVER				= "spt_radius_server";
	public static final String SPT_BONJOUR_SERVICE				= "spt_bonjour_service";
	public static final String SPT_PSE							= "spt_pse";
	public static final String SPT_WIFI_CLIENT_MODE				= "spt_wifi_client_mode";
	public static final String SPT_40M_FOR_24G					= "spt_channel40M_for_2.4g";
	public static final String SPT_TEACHER_VIEW					= "spt_teacher_view";
	public static final String SPT_IDM_PROXY					= "spt_idm_proxy";
	public static final String SPT_L7_SERVICE					= "spt_L7_service";
	public static final String SPT_11AC							= "spt_11ac";
	public static final String SPT_IMAGE_LS_NAME				= "spt_image_ls_name";
	public static final String SPT_IMAGE_INTERNAL_NAME 			= "spt_image_internal_name";
	public static final String SPT_VPN_SERVICE_SERVER			= "spt_vpn_service_server";
	public static final String SPT_LATEST_VERSION				= "spt_latest_version";
	public static final String START_VERSION					= "start_version";
	public static final String SPT_HIVE_UI						= "spt_hive_ui";
	public static final String SPT_MULTIPLE_HOST				= "spt_multiple_host";
	public static final String SLA_MAX_11N_MCS_RATE				= "SLA_max_11n_mcs_rate";
	public static final String SPT_SFPSPEED_ONLY_AUTO			= "spt_SFPspeed_only_auto";
	public static final String SPT_DEVICE_IMAGE_COUNTS			= "spt_device_image_counts";

	private Map<String, BooleanAttrParam> booleanMap = new HashMap<String, BooleanAttrParam>();
	private Map<String, Integer> integerMap = new HashMap<String, Integer>();
	private Map<String, String> stringMap = new HashMap<String, String>();
	private Map<String, List<DeviceOption>> attrOptionsMap = new HashMap<String, List<DeviceOption>>();

	private short hiveApModel;
	private short deviceType;
	private short vhmMode;
	private String version = "99.99.99.99";
	
	public DeviceInfo(short hiveApModel){
		this.hiveApModel = hiveApModel;
	}
	
	public DeviceInfo(short hiveApModel, short deviceType, String version, short vhmMode){
		this.hiveApModel = hiveApModel;
		this.deviceType = deviceType;
		this.vhmMode = vhmMode;
		if(version != null && !"".equals(version)){
			this.version = version;
		}
	}
	
	public void init(){
		if(this.getProperty() != null && this.getProperty().getAttribute() != null){
			for(DevicePropertyAttrObj attr : this.getProperty().getAttribute()){
				String key = attr.getKey();
				String value = attr.getValue();
				List<DevicePropertyAttrOptionObj> options = attr.getOption();
				if(options != null && !options.isEmpty()){
					List<DeviceOption> deviceOptions = new ArrayList<DeviceOption>();
					for (DevicePropertyAttrOptionObj devicePropertyAttrOptionObj : options) {
						DeviceOption deviceOption = new DeviceOption();
						deviceOption.setValue(devicePropertyAttrOptionObj.getValue());
						deviceOption.setVersion(devicePropertyAttrOptionObj.getVersion());
						// if DeviceProperties.xsd changed please add/modify/delete some code here
						deviceOptions.add(deviceOption);
					}
					java.util.Collections.sort(deviceOptions);
					attrOptionsMap.put(key, deviceOptions);
				}else if(value == null){
					continue;
				}else if(isBooleanValue(value)){
					if(attr.getVersion() != null){
						booleanMap.put(key, new BooleanAttrParam(Boolean.valueOf(value.trim()), attr.getVersion()));
					}else{
						booleanMap.put(key, new BooleanAttrParam(Boolean.valueOf(value.trim())));
					}
				}else if(isIntValue(value)){
					integerMap.put(key, Integer.valueOf(value.trim()));
				}else{
					stringMap.put(key, value.trim());
				}
			}
		}
	}
	
	public static class BooleanAttrParam{
		private boolean support;
		private String version;
		
		public BooleanAttrParam(boolean support, String... versions){
			this.support = support;
			if(versions.length > 0){
				version = versions[0];
			}
		}
		public boolean isSupport() {
			return support;
		}
		public void setSupport(boolean support) {
			this.support = support;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
	}
	
	public boolean isSupportAttribute(String attribute){
		BooleanAttrParam bParam = booleanMap.get(attribute);
		if(bParam == null){
			return false;
		}
		if(!bParam.isSupport()){
			return false;
		}
		if(bParam.getVersion() != null){
			return NmsUtil.compareSoftwareVersion(this.version, bParam.getVersion()) >= 0;
		}else{
			return true;
		}
	}
	
	public int getIntegerValue(String attribute){
		Integer res = integerMap.get(attribute);
		if(res == null){
			return -1;
		}else{
			return res;
		}
	}
	
	public String getStringValue(String attribute){
		return stringMap.get(attribute);
	}

	public List<DeviceOption> getDeviceOptions(String attribute) {
		return attrOptionsMap.get(attribute);
	}

	public static boolean isBooleanValue(String value){
		return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
	}
	
	public static boolean isIntValue(String value){
		byte[] argValue = value.getBytes();
		byte bt_0 = 48;
		byte bt_9 = 57;
		for(int i=0; i<argValue.length; i++){
			if(argValue[i] < bt_0 || argValue[i] > bt_9){
				return false;
			}
		}
		return true;
	}
	
	public boolean isSupportWifiRadio(){
		return integerMap.get(SPT_RADIO_COUNTS) > 0;
	}
	
	public boolean isTwoRadio(){
		return integerMap.get(SPT_RADIO_COUNTS) == 2;
	}
	
	public boolean isCvgAsL3Vpn(){
		return (this.hiveApModel == HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA 
				|| this.hiveApModel == HiveAp.HIVEAP_MODEL_VPN_GATEWAY) && 
				this.deviceType == HiveAp.Device_TYPE_VPN_GATEWAY;
	}
	
	public boolean isOnlyRouterFunc(){
		return this.deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER;
	}
	
	public boolean isContainsRouterFunc(){
		return this.deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER || 
				this.deviceType == HiveAp.Device_TYPE_VPN_BR;
	}
	
	public boolean isOnlyCVG(){
		return this.deviceType == HiveAp.Device_TYPE_VPN_GATEWAY;
	}

	public boolean isDeviceTypeAp(){
		return this.deviceType == HiveAp.Device_TYPE_HIVEAP;
	}
	
	public boolean isDeviceTypeSwitch(){
		return this.deviceType == HiveAp.Device_TYPE_SWITCH;
	}
	
	public boolean isDeviceModelInitSwitch() {
		return this.isSptEthernetMore_24();
	}
	
	public boolean isVhmFullMode(){
		return this.vhmMode == HmStartConfig.HM_MODE_FULL;
	}
	
	public boolean isSupportL3Roaming(){
		return isVhmFullMode() && isSupportWifiRadio();
	}
	
	public boolean isSptL2Routing(){
		return isVhmFullMode() && isDeviceTypeAp();
	}
	
	public boolean isSptWifiClientMode(){
		return isSupportWifiRadio() && this.isOnlyRouterFunc() && isSupportAttribute(SPT_WIFI_CLIENT_MODE);
	}
	
	public boolean isSptTeacherView(){
		return isSupportAttribute(SPT_TEACHER_VIEW);
	}
	
	public boolean isApEthernetLess_2(){
		return this.deviceType == HiveAp.Device_TYPE_HIVEAP && getIntegerValue(SPT_ETHERNET_COUNTS) <= 2;
	}
	
	public boolean isRouterOrEthernetMore_2(){
		return this.deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER ||
				this.deviceType == HiveAp.Device_TYPE_VPN_BR || 
				getIntegerValue(SPT_ETHERNET_COUNTS) > 2;
	}
	
	public boolean isSptEthernet2_4(){
		return getIntegerValue(SPT_ETHERNET_COUNTS) > 2 && 
				getIntegerValue(SPT_ETHERNET_COUNTS) <= 5;
	}
	
	public boolean isSptEthernetMore_24(){
		return getIntegerValue(SPT_ETHERNET_COUNTS) >= 24;
	}
	
	public boolean isSptEthernetMore_48(){
		return getIntegerValue(SPT_ETHERNET_COUNTS) >= 48;
	}
	
	/** bean function */
	public short getHiveApModel() {
		return hiveApModel;
	}

	public void setHiveApModel(short hiveApModel) {
		this.hiveApModel = hiveApModel;
	}

	public short getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
	}
	
	/** get device type function start */
	public EnumItem[] getDeviceTypeEnum(){
		List<DeviceOption> deviceTypes = attrOptionsMap.get(SPT_DEVICE_TYPE);
		List<DeviceOption> tmpDeviceTypes = new ArrayList<DeviceOption>();
		for(DeviceOption option : deviceTypes){
			if(isVersionMatch(option.getVersion())){
				tmpDeviceTypes.add(option);
			}
		}
		EnumItem[] resItem = new EnumItem[tmpDeviceTypes.size()];
		int i=0;
		for(DeviceOption option : tmpDeviceTypes){
			int key = getDeviceTypeKey(option.getValue());
			String property = "enum.hiveAp.deviceType." + key;
			String value = MgrUtil.getEnumString(property);
			resItem[i++] = new EnumItem(key, value);
		}
		return resItem;
	}
	
	public short getDefaultDeviceType(){
		return (short)(this.getDeviceTypeEnum()[0].getKey());
	}
	
	private boolean isVersionMatch(String ver){
		if(ver == null || "".equals(ver)){
			return true;
		}
		return NmsUtil.compareSoftwareVersion(this.version, ver) >= 0;
	}
	
	private short getDeviceTypeKey(String strType){
		if("AP".equals(strType)){
			return HiveAp.Device_TYPE_HIVEAP;
		}else if("BR".equals(strType)){
			return HiveAp.Device_TYPE_BRANCH_ROUTER;
		}else if("VPN".equals(strType)){
			return HiveAp.Device_TYPE_VPN_GATEWAY;
		}else if("BR_VPN".equals(strType)){
			return HiveAp.Device_TYPE_VPN_BR;
		}else if("Switch".equals(strType)){
			return HiveAp.Device_TYPE_SWITCH;
		}else{
			return -1;
		}
	}
	
	/** get device type function end */

	public class DeviceOption extends DevicePropertyAttrOptionObj implements Comparable<DeviceOption> {
		@Override
		public int compareTo(DeviceOption o) {
			return NmsUtil.compareSoftwareVersion(o.getVersion(), this.version);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DeviceOption))
				return super.equals(obj);

			return new EqualsBuilder().append(this.value, ((DeviceOption)obj).value).append(this.version, ((DeviceOption)obj).version).isEquals();
		}

		@Override
		public int hashCode() {
			HashCodeBuilder hBuilder = new HashCodeBuilder();
			if(value != null){
				hBuilder.append(value.hashCode());
			}
			if(version != null){
				hBuilder.append(version.hashCode());
			}
			return hBuilder.toHashCode();
		}
	}
}
