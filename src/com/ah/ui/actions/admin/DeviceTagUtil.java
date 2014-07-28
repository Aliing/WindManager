package com.ah.ui.actions.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmClassifierTag;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class DeviceTagUtil {
	public static final String CUSTOM_TAG3 = "customTag3";
	public static final String CUSTOM_TAG2 = "customTag2";
	public static final String CUSTOM_TAG1 = "customTag1";
	
	public static final String SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER = "availableDeviceTemplateFilter";
	public static final String SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE= "deviceTemplateFilterType";
	public static final String SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE = "enableModelFilter";
	public static final String SESSION_MARK_DEVICE_TEMPLATE_FILTER_VALUE = "enableFilter";
	
	public static final String TAG1_TYPE = "1";
	public static final String TAG2_TYPE = "2";
	public static final String TAG3_TYPE = "3";
	private static DeviceTagUtil instance = null;

	private HashMap<String, Map<String, String>> customTagMap=new HashMap<String,Map<String, String>>();
	private HashMap<String, Map<String,Set<String>>> cacheTagMap=new HashMap<String, Map<String,Set<String>>>();
	

	private DeviceTagUtil() {

	}
	
	
	
	private static Map<String,HmDomain> domainMap=new HashMap<String,HmDomain>();
	public static void init() {
		//get domain
		
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from ATTRIBUTE_ITEM  a , USER_PROFILE_ATTRIBUTE  b where a.attribute_id=b.id ");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from VLAN_ITEM  a , VLAN  b where a.vlan_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from IP_ADDRESS_ITEM a,IP_ADDRESS b where a.ip_address_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from LOCATIONCLIENT_ITEM a,LOCATIONCLIENTWATCH b where a.locationclientwatch_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from MAC_OR_OUI_ITEM a ,MAC_OR_OUI b where a.mac_or_oui_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from RADIUS_ATTRIBUTE_ITEM a,RADIUS_OPERATOR_ATTRIBUTE b where a.radius_attribute_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from VPN_NETWORK_SUBNETCLASS a  ,VPN_NETWORK b where a.vpn_network_subnetclass_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from VPN_NETWORK_IP_RESERVE_ITEM a  ,VPN_NETWORK b where a.vpn_network_reserveclass_id=b.id");
		syncDatabaseAndCache("select DISTINCT tag1,tag2,tag3,owner from PORT_TEMPLATE_PROFILE_ITEM a  ,PORT_TEMPLATE_PROFILE b where a.portprofiles_id=b.id");
	
		getInstance().updateClassifierTagInternal();
	}
	

	private static void syncDatabaseAndCache(String sql){	
		List<?> tagList = QueryUtil.executeNativeQuery(sql);
		if(!tagList.isEmpty()){			
			for (Object onePro : tagList) {
				Object[] tmp = (Object[]) onePro;
				if(tmp[3]==null)continue;
				String owner=tmp[3].toString(); 
				if (domainMap.get(owner) == null) {
					List<HmDomain> list = QueryUtil.executeQuery(
							HmDomain.class, null,
							new FilterParams("id", Long.parseLong(owner)));
					HmDomain domain = list.get(0);
					if (domain != null)
						domainMap.put(owner, domain);
				}				
				if (tmp[0] != null&&tmp[0].toString().length()>0)
					domainMap.put(TAG1_TYPE+"_"+tmp[0].toString(), domainMap.get(owner));	
				if (tmp[1] != null&&tmp[1].toString().length()>0)
					domainMap.put(TAG2_TYPE+"_"+tmp[1].toString(), domainMap.get(owner));
				if (tmp[2] != null&&tmp[2].toString().length()>0)
					domainMap.put(TAG3_TYPE+"_"+tmp[2].toString(), domainMap.get(owner));							
			}
		}
	}
	
	public static DeviceTagUtil getInstance() {
		if (instance == null) {
			instance = new DeviceTagUtil();
		}
		return instance;

	}

	/**
	 * 
	 * @param domainId
	 * @return
	 */
	private String getClassifierTagSetting(Long domainId) {
		HMServicesSettings bo;
		List<HMServicesSettings> list = QueryUtil.executeQuery(
				HMServicesSettings.class, null, new FilterParams("owner.id",domainId));
		if (list.isEmpty()) {
			return "";
		} else {
			bo = list.get(0);
		}
		return bo.getClassifierTag();
	}
	
	/**
	 * 
	 * @param domainId
	 * @return
	 */
	public Map<String, String> getClassifierCustomTag(Long domainId) {
		Map<String, String> map = customTagMap.get("" + domainId);
		if (map == null) {
			map = new HashMap<String, String>();
			String source = getClassifierTagSetting(domainId);
			if (source == null || source.equals("")) {
				map.put(CUSTOM_TAG1,
						MgrUtil.getResourceString("hiveAp.classification.tag1"));
				map.put(CUSTOM_TAG2,
						MgrUtil.getResourceString("hiveAp.classification.tag2"));
				map.put(CUSTOM_TAG3,
						MgrUtil.getResourceString("hiveAp.classification.tag3"));

			} else {
				map.put(CUSTOM_TAG1,
						DeviceTagUtil.getInstance().getValue(source, "Tag1"));
				map.put(CUSTOM_TAG2,
						DeviceTagUtil.getInstance().getValue(source, "Tag2"));
				map.put(CUSTOM_TAG3,
						DeviceTagUtil.getInstance().getValue(source, "Tag3"));
			}
			customTagMap.put("" + domainId, map);
		}
		return map;
	}
	
	
	public void updateCustomDeviceTag(Long domainId, String classifierTag) {
		Map<String, String> map = customTagMap.get("" + domainId);
		if (map != null) {
			map.put(CUSTOM_TAG1,
					DeviceTagUtil.getInstance().getValue(classifierTag, "Tag1"));
			map.put(CUSTOM_TAG2,
					DeviceTagUtil.getInstance().getValue(classifierTag, "Tag2"));
			map.put(CUSTOM_TAG3,
					DeviceTagUtil.getInstance().getValue(classifierTag, "Tag3"));
		}
	}
	/**
	 * 
	 * @param src
	 * @param tag
	 * @return
	 */

	private String getValue(String src, String tag) {
		int index = src.indexOf(tag);
		if (index == -1)
			return "";
		else {
			src = src.substring(index);
			int fromIndex = src.indexOf("=");
			int toIndex = src.indexOf("*");
			if (toIndex == -1)
				toIndex = src.length();
			return src.substring(fromIndex + 1, toIndex);
		}
	}
	/**
	 * 
	 * @param domainId
	 * @return
	 */
	public Map<String,Set<String>> getExistedClassifierTag(Long domainId) {		
		Map<String, Set<String>> domainMap = cacheTagMap.get(""+domainId);		
		if (domainMap==null||domainMap.isEmpty()) {	
			domainMap=new HashMap<String,Set<String>>();
			domainMap.put(TAG1_TYPE, new HashSet<String>());
			domainMap.put(TAG2_TYPE, new HashSet<String>());
			domainMap.put(TAG3_TYPE, new HashSet<String>());
			
			List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
			for (SimpleHiveAp sDevice : simpleDevice) {
				if (StringUtils.isNotBlank(sDevice.getTag1())) {
					domainMap.get(TAG1_TYPE).add(sDevice.getTag1());
				}
				if (StringUtils.isNotBlank(sDevice.getTag2())) {
					domainMap.get(TAG2_TYPE).add(sDevice.getTag2());
				}
				if (StringUtils.isNotBlank(sDevice.getTag3())) {
					domainMap.get(TAG3_TYPE).add(sDevice.getTag3());
				}
			}			
			cacheTagMap.put("" + domainId, domainMap);
			List<HmClassifierTag> list = QueryUtil.executeQuery(HmClassifierTag.class, null, new FilterParams("owner.id",domainId));
			
			if (list.isEmpty()) {
				return domainMap;
			} else {
				for (HmClassifierTag item : list) {
					String type = "" + item.getTagType();
					if (item.getTagValue() == null)
						continue;
					domainMap.get(type).add(item.getTagValue());
				}			
			}	
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from ATTRIBUTE_ITEM  a , USER_PROFILE_ATTRIBUTE  b where a.attribute_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from VLAN_ITEM  a , VLAN  b where a.vlan_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from IP_ADDRESS_ITEM a,IP_ADDRESS b where a.ip_address_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from LOCATIONCLIENT_ITEM a,LOCATIONCLIENTWATCH b where a.locationclientwatch_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from MAC_OR_OUI_ITEM a ,MAC_OR_OUI b where a.mac_or_oui_id=b.id and  b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from RADIUS_ATTRIBUTE_ITEM a,RADIUS_OPERATOR_ATTRIBUTE b where a.radius_attribute_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from VPN_NETWORK_SUBNETCLASS a  ,VPN_NETWORK b where a.vpn_network_subnetclass_id=b.id and b.owner="+domainId,domainMap);
//			initializeTagFromSeperateTable("select DISTINCT tag1,tag2,tag3 from VPN_NETWORK_IP_RESERVE_ITEM a  ,VPN_NETWORK b where a.vpn_network_reserveclass_id=b.id and b.owner="+domainId,domainMap);			
		
		}
		return domainMap;
	}
	
	
//	private void initializeTagFromSeperateTable(String sql,Map<String, Set<String>> domainMap){	
//		List<?> tagList = QueryUtil.executeNativeQuery(sql);
//		if(!tagList.isEmpty()){
//			for (Object onePro : tagList) {
//				Object[] tmp = (Object[]) onePro;
//				if (tmp[0] != null)
//					domainMap.get(TAG1_TYPE).add(tmp[0].toString());
//				if (tmp[1] != null)
//					domainMap.get(TAG2_TYPE).add(tmp[1].toString());
//				if(tmp[2] !=null)
//					domainMap.get(TAG3_TYPE).add(tmp[2].toString());					
//			}
//		}
//	}
	/**
	 * 
	 * @param classifierTag
	 * @param type
	 * @param domain
	 */
	public void updateClassifierTag(String classifierTag,int type,HmDomain domain){
		if (type == 1 || type == 2 || type == 3) {
			if (classifierTag == null)
				return;
			Map<String, Set<String>> map = cacheTagMap.get("" + domain.getId());
			if (map == null)
				return;
			Set<String> set = map.get("" + type);
			if (set == null)
				return;
			saveClassifierTag(classifierTag, type, domain);
			set.add(classifierTag);
		}
	}
	
	/**
	 * 
	 * @param classifierTag
	 * @param type
	 * @param domain
	 */
	private void updateClassifierTagInternal(){
	    Set<String> set=new HashSet<String>();
		List<HmClassifierTag> list = QueryUtil.executeQuery(HmClassifierTag.class, null,null);
		
		List<?> tagList = QueryUtil.executeNativeQuery("select tagtype,tagvalue,owner from hmclassifiertag");
		if (!list.isEmpty()) {
			for (Object onePro : tagList) {
				Object[] tmp = (Object[]) onePro;
				if(tmp[0]==null||tmp[1]==null||tmp[2]==null)continue;
				set.add(tmp[1].toString()+"-"+tmp[0].toString()+"-"+tmp[2].toString());				
			}
		} 
		
		for(String dataKey : domainMap.keySet()){
			if(dataKey.indexOf("_")!=-1){
				HmDomain tempDomain=domainMap.get(dataKey);
				int index=dataKey.indexOf("_");
				String tagType=dataKey.substring(0, index);
				String tagValue=dataKey.substring(index+1);
				HmClassifierTag target=new HmClassifierTag();
				target.setOwner(tempDomain);
				target.setTagType(Integer.parseInt(tagType));
				target.setTagValue(tagValue);
				if(!isContainItem(set,target)){
					try {
						QueryUtil.createBo(target);
					} catch (Exception e) {			
						e.printStackTrace();		
					}
				}
			}
		}
	}
	
	private boolean isContainItem(Set<String> set, HmClassifierTag target) {
		String temp = target.getTagValue() + "-" + target.getTagType() + "-"
				+ target.getOwner().getId();
		if (set.contains(temp))
			return true;
		return false;
	}	
	
	/**
	 * 
	 * @param classifierTag
	 * @param type
	 * @param domain
	 * @return
	 */
	public boolean saveClassifierTag(String classifierTag,int type,HmDomain domain) {
		if(isExistClassifierTag(classifierTag,type,domain))return false;
		HmClassifierTag bo=new HmClassifierTag();
		bo.setOwner(domain);
		bo.setTagType(type);
		bo.setTagValue(classifierTag);
		try {
			QueryUtil.createBo(bo);
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param classifierTag
	 * @param type
	 * @param domain
	 * @return
	 */
	public boolean isExistClassifierTag(String classifierTag, int type,
			HmDomain domain) {		
		Map<String, Set<String>> map = cacheTagMap.get(""+domain.getId());
		if(map==null)return false;
		Set<String> set = map.get(""+type);
		if(set==null)return false;
		return set.contains(classifierTag);
	}
	
	public String getUrlValue(Map<String, Object> map,String key) {
		String[] value = (String[]) map.get(key);
		String trueValue=value[0];
		return trueValue;
	}
	
	public boolean updateClassifierTagSetting(String classifierTag,long domainId) {
		HMServicesSettings bo;
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams("owner.id",domainId));
		if (list.isEmpty()) {
			return false;
		} else {
			bo = list.get(0);
		}
		bo.setClassifierTag(classifierTag);
		DeviceTagUtil.getInstance().updateCustomDeviceTag(domainId,classifierTag);
		try {			
			bo = QueryUtil.updateBo(bo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}


}
