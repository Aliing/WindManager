package com.ah.be.parameter.device;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.common.AhDirTools;
import com.ah.util.Tracer;
import com.ah.xml.deviceProperties.DeviceObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrOptionObj;
import com.ah.xml.deviceProperties.DevicePropertyObj;
import com.ah.xml.deviceProperties.Devices;

import edu.emory.mathcs.backport.java.util.Collections;

public class DevicePropertyManage {

	private static final Tracer log = new Tracer(DevicePropertyManage.class.getSimpleName());

	private Devices devices;
	private JSONObject devicesJSON;
	private String devicesJSONStr;
	
	private static DevicePropertyManage instance;
	
	private DevicePropertyManage() {}
	
	public static DevicePropertyManage getInstance(){
		synchronized(DevicePropertyManage.class){
			if(instance == null){
				instance = new DevicePropertyManage();
				instance.init();
			}
			return instance;
		}
	}

	private void init() {
		try {
			String cfgPath = AhDirTools.getConstantConfigDir() + "device_property.xml";
			JAXBContext jc = JAXBContext
					.newInstance("com.ah.xml.deviceProperties");
			Unmarshaller m = jc.createUnmarshaller();

			devices = (Devices) m.unmarshal(new File(cfgPath));
			
			//handle extends relationship
			initExtendsRelation();
			
			//convert to JSON type.
			initDevicesJSON();
		} catch (Exception e) {
			log.error("DevicePropertyManage.init()", e);
		}
	}
	
	private void initExtendsRelation() throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		LinkedList<DeviceObj> queue = new LinkedList<DeviceObj>();
		Map<String, DeviceObj> map = new HashMap<>();
		int protectNum = 0;
		queue.addAll(devices.getDevice());
		while(!queue.isEmpty()){
			if(protectNum ++ > 999){
				break;
			}
			
			DeviceObj firstObj = queue.pollFirst();
			String extendsStr = firstObj.getExtends();
			
			//extends field is null, nothing handle.
			if(StringUtils.isEmpty(extendsStr)){
				map.put(firstObj.getDeviceName(), firstObj);
				continue;
			}
			
			//if parent not ready, put to queue end.
			DeviceObj parentObj = map.get(extendsStr);
			if(parentObj == null){
				queue.addLast(firstObj);
				continue;
			}
			
			//handle extends relationship
			DeviceObj parentCloneObj = DeepCloneUtil.cloneObject(DeviceObj.class, parentObj);
			if(parentCloneObj.getProperty() != null){
				for(DevicePropertyAttrObj attrObj : parentCloneObj.getProperty().getAttribute()){
					if(!isContainsKey(attrObj.getKey(), firstObj)){
						if(firstObj.getProperty() == null){
							firstObj.setProperty(new DevicePropertyObj());
						}
						firstObj.getProperty().getAttribute().add(attrObj);
					}
				}
			}
			map.put(firstObj.getDeviceName(), firstObj);
		}
	}
	
	private void initDevicesJSON(){
		try{
			this.devicesJSON = new JSONObject();
			for(DeviceObj dObj : devices.getDevice()){
				if(dObj.getProperty() == null || dObj.getKey() == null){
					continue;
				}
				
				JSONObject cldObj = new JSONObject();
				devicesJSON.put(dObj.getKey().toString(), cldObj);
				for(DevicePropertyAttrObj attrObj : dObj.getProperty().getAttribute()){
					if(!attrObj.getOption().isEmpty()){
						JSONArray jArray = new JSONArray();
						cldObj.put(attrObj.getKey(), jArray);
						for(DevicePropertyAttrOptionObj optObj : attrObj.getOption()){
							jArray.put(optObj.getValue());
						}
						continue;
					}else{
						cldObj.put(attrObj.getKey(), attrObj.getValue());
					}
				}
			}
			devicesJSONStr = devicesJSON.toString();
		}catch(Exception e){
			log.error("initDevicesJSON() failed.", e);
		}
	}

	public DeviceObj getDeviceProperty(short hiveApModel) {
		if (devices == null || devices.getDevice() == null || devices.getDevice().isEmpty()) {
			init();
		}
		int deviceModelInt = Integer.valueOf(hiveApModel);
		for (DeviceObj property : devices.getDevice()) {
			if(property.getKey() == null){
				continue;
			}
			if(deviceModelInt == property.getKey()){
				return property;
			}
		}
		return null;
	}
	
	public List<Short> getSupportDeviceList(String attrKey, String attrValue) {
		if (devices == null || devices.getDevice() == null || devices.getDevice().isEmpty()) {
			init();
		}
		List<Short> resList = new ArrayList<Short>();
		if(StringUtils.isEmpty(attrKey) || StringUtils.isEmpty(attrValue)) {
			return resList;
		}
		
		for (DeviceObj property : devices.getDevice()) {
			if(property.getProperty() == null || property.getProperty().getAttribute() == null || 
					property.getKey() == null){
				continue;
			}
			int deviceKey = property.getKey();
			if(deviceKey < 0) {
				continue;
			}
			for(DevicePropertyAttrObj attrObj : property.getProperty().getAttribute()){
				if(attrKey.equals(attrObj.getKey()) && attrValue.equals(attrObj.getValue())){
					resList.add((short)deviceKey);
					break;
				}
			}
		}
		Collections.sort(resList);
		return resList;
	}
	
	public String getSupportDeviceRegex(String attrKey, String attrValue) {
		List<Short> resList = getSupportDeviceList(attrKey, attrValue);
		
		StringBuilder sBuilder = new StringBuilder();
		if(resList != null){
			for(Short hiveApModel : resList){
				if(sBuilder.length() > 0){
					sBuilder.append("|");
				}
				sBuilder.append(hiveApModel.toString());
			}
		}
		return sBuilder.toString();
	}
	
	public String getSupportDeviceRegex(String propertyExpression) {
		String attrKey = null, attrValue = null;
		int signIndex = propertyExpression.indexOf("=");
		if(signIndex > 0 && signIndex < propertyExpression.length() - 1) {
			attrKey = propertyExpression.substring(0, signIndex).trim();
			attrValue = propertyExpression.substring(signIndex + 1).trim();
		}
		
		return getSupportDeviceRegex(attrKey, attrValue);
	}
	
	public List<Short> getSupportDeviceList(String attributeKey){
		return getSupportDeviceList(attributeKey, "true");
	}
	
	public Map<Short, String> getDeviceModelValueMapping(String attributeKey){
		if (devices == null) {
			return null;
		}
		Map<Short, String> resMap = new HashMap<>();
		for (DeviceObj property : devices.getDevice()) {
			if(property.getProperty() == null || property.getProperty().getAttribute() == null || 
					property.getKey() == null){
				continue;
			}
			int deviceKey = property.getKey();
			for(DevicePropertyAttrObj attrObj : property.getProperty().getAttribute()){
				if(attributeKey.equals(attrObj.getKey())){
//					if(attrObj.getValue() != null){
						resMap.put((short)deviceKey, attrObj.getValue());
						return resMap;
//					}
				}
			}
			resMap.put((short)deviceKey, null);
		}
		return resMap;
	}
	
	public Map<Short, List<DevicePropertyAttrOptionObj>> getDeviceModelOptionsMapping(String attributeKey){
		if (devices == null) {
			return null;
		}
		Map<Short, List<DevicePropertyAttrOptionObj>> resMap = new HashMap<>();
		for (DeviceObj property : devices.getDevice()) {
			if(property.getProperty() == null || property.getProperty().getAttribute() == null || 
					property.getKey() == null){
				continue;
			}
			int deviceKey = property.getKey();
			if(deviceKey <= 0){
				continue;
			}
			for(DevicePropertyAttrObj attrObj : property.getProperty().getAttribute()){
				if(attributeKey.equals(attrObj.getKey())){
					if(attrObj.getOption() != null && !attrObj.getOption().isEmpty()){
						resMap.put((short)deviceKey, attrObj.getOption());
						break;
					}
				}
			}
		}
		return resMap;
	}
	
	public JSONObject getDevicesJSON(){
		return devicesJSON;
	}
	
	public String getDevicesJSONStr(){
		return devicesJSONStr;
	}
	
	public List<Short> getAllDeviceKey(){
		List<Short> resList = new ArrayList<>();
		for (DeviceObj property : devices.getDevice()) {
			if(property.getKey() == null){
				continue;
			}
			resList.add(Short.parseShort(property.getKey().toString()));
		}
		return resList;
	}

	public void clone(Object source, Object dest) {
		Field[] fields = source.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String propertyName = fields[i].getName();
			Object propertyValue = getProperty(source, propertyName);
			setProperty(dest, propertyName, propertyValue);
		}
	}

	private static Object setProperty(Object bean, String propertyName,
			Object value) {
		Class<?> clazz = bean.getClass();
		try {
			String setMethodName = getSetterName(propertyName);
			Method[] methods = clazz.getMethods();
			Method method = null;
			for(int i=0; i<methods.length; i++) {
				if(setMethodName.equals(methods[i].getName())){
					method = methods[i];
					break;
				}
			}
			if(method != null){
				return method.invoke(bean, new Object[] { value });
			}
		} catch (Exception e) {
		}
		return null;
	}

	private static Object getProperty(Object bean, String propertyName) {
		Class<?> clazz = bean.getClass();
		try {
			Field field = clazz.getDeclaredField(propertyName);
			Method method = clazz.getDeclaredMethod(
					getGetterName(field.getName()), new Class[] {});
			return method.invoke(bean, new Object[] {});
		} catch (Exception e) {
		}
		return null;
	}

	private static String getGetterName(String propertyName) {
		String method = "get" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		return method;
	}

	private static String getSetterName(String propertyName) {
		String method = "set" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		return method;
	}
	
	public static void main(String[] args){
		String key = "spt_radio_counts";
		Map<Short, String> map = DevicePropertyManage.getInstance().getDeviceModelValueMapping(key);
		Object[] arrayObj = map.entrySet().toArray();
		System.out.println(arrayObj);
	}

	public Devices getDevices() {
		if (devices == null || devices.getDevice() == null || devices.getDevice().isEmpty()) {
			init();
		}
		return devices;
	}
	
	private boolean isContainsKey(String key, DeviceObj deviceObj){
		if(deviceObj == null || deviceObj.getProperty() == null || 
				deviceObj.getProperty().getAttribute().isEmpty()){
			return false;
		}
		
		for(DevicePropertyAttrObj attrObj : deviceObj.getProperty().getAttribute()){
			if(attrObj.getKey().equals(key)){
				return true;
			}
		}
		return false;
	}
}
