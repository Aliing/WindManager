package com.ah.be.parameter.constant.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.AhConstantConfigParsable;
import com.ah.be.parameter.constant.AhConstantConfigParsedException;
import com.ah.be.parameter.constant.parser.AhDeviceProductNameParser.DeviceProductType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public class AhDeviceStyleParser implements AhConstantConfigParsable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(AhDeviceProductNameParser.class.getSimpleName());
	
	public static enum DeviceType{
		ap, br, cvg, vpn_br, sr
	}
	
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_DEVICE_TYPE = "deviceType";
	public static final String ATTRIBUTE_HIDE = "hide";
	
	public Map<String, Collection<DeviceStyle>> parse(File configFile)
			throws AhConstantConfigParsedException {
		if (configFile == null) {
			throw new AhConstantConfigParsedException("Invalid argument: " + configFile);
		}

		if (!configFile.exists()) {
			throw new AhConstantConfigParsedException(NmsUtil.getOEMCustomer().getAccessPonitName()+" file["
					+ configFile.getName() + "] doesn't exist.");
		}

		SAXReader reader = new SAXReader();

		try {
			Document document = reader.read(configFile);
			Element root = document.getRootElement();
			List<?> devices = root.elements();
			Map<String, Collection<DeviceStyle>> deviceStyleMap = new HashMap<String, Collection<DeviceStyle>>();

			for (Object obj : devices) {
				Element deviceTypeElem = (Element) obj;
				String deviceName = deviceTypeElem.attributeValue(ATTRIBUTE_NAME);
				String deviceType = deviceTypeElem.attributeValue(ATTRIBUTE_DEVICE_TYPE);

				try {
					DeviceType deviceTypeObj = DeviceType.valueOf(deviceType);
					Collection<DeviceStyle> deviceStyleList = new ArrayList<DeviceStyle>();
					treeWalk(deviceTypeElem, deviceStyleList);

					log.debug("parse", "Parsing Device Mode type: " + deviceName);
					log.debug("parse", "Parsing Device Type type: " + deviceType);
					
					
					if(deviceName.contains(",")){
						String[] argDName = deviceName.split(",");
						for(int i=0; i<argDName.length; i++){
							deviceStyleMap.put(new DeviceKey(
									DeviceProductType.valueOf(argDName[i].trim()), deviceTypeObj).getKey(), deviceStyleList);
						}
					}else{
						deviceStyleMap.put(new DeviceKey(
								DeviceProductType.valueOf(deviceName), deviceTypeObj).getKey(), deviceStyleList);
					}
				} catch (IllegalArgumentException iae) {
					log.error("parse", "Unknown Device[" + deviceName
							+ ", "+deviceType+"], ignore parsing.", iae);
				}
			}

			return deviceStyleMap;
		} catch (DocumentException de) {
			throw new AhConstantConfigParsedException("Failed to parse "+NmsUtil.getOEMCustomer().getAccessPonitName()+" product type ["
					+ configFile.getName() + "] because of wrong file format.");
		}
	}
	
	private void treeWalk(Element element, Collection<DeviceStyle> deviceColl){
		String deviceName = element.attributeValue(ATTRIBUTE_NAME);
		if(!element.isRootElement() && deviceName == null){
			String elementName = element.getName();
			String hideStr = element.attributeValue(ATTRIBUTE_HIDE);
			boolean blnHide;
			if(hideStr == null){
				blnHide = false;
			}else{
				blnHide = Boolean.valueOf(hideStr);
			}
			deviceColl.add(new DeviceStyle(elementName, blnHide));
		}
		
		Iterator<?> eleIte = element.elementIterator();
		while (eleIte.hasNext()) {
			Element childElement = (Element) eleIte.next();
			treeWalk(childElement, deviceColl);
		}
	}
	
	public static class DeviceKey{
		
		private AhDeviceProductNameParser.DeviceProductType deviceMode;

		private DeviceType deviceType;
		
		public DeviceKey(AhDeviceProductNameParser.DeviceProductType deviceMode, DeviceType deviceType){
			this.deviceMode = deviceMode;
			this.deviceType = deviceType;
		}
		
		public DeviceKey(short deviceMode, short deviceType){
			switch(deviceMode){
				case HiveAp.HIVEAP_MODEL_20:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap20;
					break;
				case HiveAp.HIVEAP_MODEL_28:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap28;
					break;
				case HiveAp.HIVEAP_MODEL_320:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap320;
					break;
				case HiveAp.HIVEAP_MODEL_340:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap340;
					break;
				case HiveAp.HIVEAP_MODEL_120:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap120;
					break;
				case HiveAp.HIVEAP_MODEL_110:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap110;
					break;
				case HiveAp.HIVEAP_MODEL_121:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap121;
					break;
				case HiveAp.HIVEAP_MODEL_141:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap141;
					break;
				case HiveAp.HIVEAP_MODEL_170:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap170;
					break;
				case HiveAp.HIVEAP_MODEL_330:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap330;
					break;
				case HiveAp.HIVEAP_MODEL_350:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap350;
					break;
				case HiveAp.HIVEAP_MODEL_370:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap370;
					break;	
				case HiveAp.HIVEAP_MODEL_390:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap390;
					break;	
				case HiveAp.HIVEAP_MODEL_230:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.hiveap230;
					break;		
				case HiveAp.HIVEAP_MODEL_BR100:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.br100;
					break;
				case HiveAp.HIVEAP_MODEL_BR200:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.br200;
					break;
				case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.br200_lte_vz;
					break;					
				case HiveAp.HIVEAP_MODEL_BR200_WP:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.br200_wp;
					break;
				case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.cvg;
					break;
				case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.cvgappliance;
					break;
				case HiveAp.HIVEAP_MODEL_SR24:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.sr24;
					break;
				case HiveAp.HIVEAP_MODEL_SR2124P:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.sr2124p;
					break;
				case HiveAp.HIVEAP_MODEL_SR2148P:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.sr2148p;
					break;
				case HiveAp.HIVEAP_MODEL_SR2024P:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.sr2024p;
					break;					
				case HiveAp.HIVEAP_MODEL_SR48:
					this.deviceMode = AhDeviceProductNameParser.DeviceProductType.sr48;
					break;
			}
			
			switch(deviceType){
				case HiveAp.Device_TYPE_HIVEAP:
					this.deviceType = DeviceType.ap;
					break;
				case HiveAp.Device_TYPE_BRANCH_ROUTER:
					this.deviceType = DeviceType.br;
					break;
				case HiveAp.Device_TYPE_VPN_GATEWAY:
					this.deviceType = DeviceType.cvg;
					break;
				case HiveAp.Device_TYPE_VPN_BR:
					this.deviceType = DeviceType.vpn_br;
					break;
				case HiveAp.Device_TYPE_SWITCH:
					this.deviceType = DeviceType.sr;
					break;
			}
		}
		
		public AhDeviceProductNameParser.DeviceProductType getDeviceMode() {
			return deviceMode;
		}

		public DeviceType getDeviceType() {
			return deviceType;
		}
		
		public String getKey(){
			return deviceMode.name() + ", "+ deviceType.name();
		}
		
		public boolean equals(Object obj){
			if(obj == null || !(obj instanceof DeviceKey)){
				return false;
			}
			DeviceKey device = (DeviceKey)obj;
			return this.deviceMode.equals(device.getDeviceMode()) && 
					this.deviceType.equals(device.getDeviceType());
		}
	}
	
	public static class DeviceStyle{
		
		private String id;

		private boolean hide;
		
		public DeviceStyle(String id, boolean hide){
			this.id = id;
			this.hide = hide;
		}
		
		public String getId() {
			return id;
		}

		public boolean isHide() {
			return hide;
		}
	}

}
