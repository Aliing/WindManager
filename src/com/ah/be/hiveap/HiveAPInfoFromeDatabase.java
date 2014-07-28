/**
 *@filename		HiveAPInfoFromeDatabase.java
 *@version
 *@author		Fiona
 *@createtime	May 15, 2009 9:11:10 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.hiveap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhClientSession;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HiveAPInfoFromeDatabase {
	
	/**
	 * Get hiveap number base on hiveap type.
	 *
	 * @param arg_Type: mesh point or portal;
	 * @param domName: domain name
	 * @return hiveap number of this type
	 */
	public static int getHiveAPNumber(short arg_Type, String domName) {
		String sqlStr = "SELECT COUNT(hostName) FROM " + HiveAp.class.getSimpleName();
		StringBuffer where = new StringBuffer();
		where.append("manageStatus = :s1 AND hiveApType = :s2 AND deviceType != :s3");
		
		List<Object> values = new ArrayList<Object>();
		values.add(HiveAp.STATUS_MANAGED);
		values.add(arg_Type);
		values.add(HiveAp.Device_TYPE_VPN_GATEWAY);
		
		if (null != domName && !"".equals(domName)) {
			where.append(" AND owner.domainName = :s4");
			values.add(domName);
		}
		List<?> result = QueryUtil.executeQuery(sqlStr, null, new FilterParams(where.toString(), values.toArray()));
		if (!result.isEmpty()) {
			Object objValue = result.get(0);
			return Integer.parseInt(String.valueOf(objValue));
		}
		return 0;
	}
	
	/**
	 * Get hiveap version and hiveap number of this version
	 *
	 * @param domName : domain name
	 * @return List<HiveApVersionInfo>
	 */
	public static List<HiveApVersionInfo> getHiveAPVersion(String domName) {
		List<HiveApVersionInfo> versionInfo = new ArrayList<HiveApVersionInfo>();
		String query = "SELECT DISTINCT bo.softVer, COUNT(bo.hostName) FROM " + HiveAp.class.getSimpleName() + " bo";
		
		StringBuffer where = new StringBuffer();
		where.append("bo.manageStatus = :s1 AND bo.deviceType != :s2");
		
		List<Object> values = new ArrayList<Object>();
		values.add(HiveAp.STATUS_MANAGED);
		values.add(HiveAp.Device_TYPE_VPN_GATEWAY);
		
		if (null != domName && !"".equals(domName)) {
			where.append(" AND bo.owner.domainName = :s3");
			values.add(domName);
		}
		
		// get the hiveap version and count from database
		List<?> allVersion = QueryUtil.executeQuery(query, new SortParams("bo.softVer"), new FilterParams(where.toString(), 
			values.toArray()), new GroupByParams(new String[]{"softVer"}), null);

		for (Object obj : allVersion) {
			Object[] value = (Object[])obj;
			HiveApVersionInfo version = new HiveApVersionInfo();
			String[] innerVersion = ((String)value[0]).split("\\.");
			// the version format is 3.4.2.0
			if (innerVersion.length >= 3) {
				String versionStr = innerVersion[0]+"."+innerVersion[1]+"r"+innerVersion[2];
				if (innerVersion.length > 3 && !"".equals(innerVersion[3]) && !"0".equals(innerVersion[3])) {
					versionStr += innerVersion[3];
				}
				version.setHiveapVersion(versionStr);
				version.setHiveapCount(Integer.parseInt(String.valueOf(value[1])));
				versionInfo.add(version);
			}
		}

		return versionInfo.isEmpty() ? null : versionInfo;
	}
	
	/**
	 * Get active client mac vendor information
	 *
	 * @param domName: domain name
	 * @return List<ClientMacInfo>
	 */
	public static List<ClientMacInfo> getActiveClientsMacInfo(String domName) {
		StringBuffer where = new StringBuffer();
		where.append("connectstate = :s1");
		
		List<Object> values = new ArrayList<Object>();
		values.add(AhClientSession.CONNECT_STATE_UP);
		
		if (null != domName && !"".equals(domName)) {
			where.append(" AND owner.domainName = :s2");
			values.add(domName);
		}
		List<?> clientMacList = QueryUtil.executeQuery("SELECT clientMac FROM " + AhClientSession.class.getSimpleName(),
		     null, new FilterParams(where.toString(), values.toArray()));
		Map<String, Integer> clientVendorCount = new HashMap<String, Integer>();

		for (Object objectClient:clientMacList) {
			String tempMac = objectClient.toString().substring(0, 6);
			if (clientVendorCount.get(tempMac) == null) {
				clientVendorCount.put(tempMac, 1);
			} else {
				clientVendorCount.put(tempMac, clientVendorCount.get(tempMac)+1);
			}
		}

		if (clientVendorCount.size() > 0) {
			List<ClientMacInfo> resultInfo = new ArrayList<ClientMacInfo>();
			for (String mac : clientVendorCount.keySet()) {
				ClientMacInfo oneClient = new ClientMacInfo();
				oneClient.setClientMac(mac);
				oneClient.setClientCount(clientVendorCount.get(mac));
				resultInfo.add(oneClient);
			}
			return resultInfo.isEmpty() ? null : resultInfo;
		}
		// for test
//		List<ClientMacInfo> resultInfo = new ArrayList<ClientMacInfo>();
//		ClientMacInfo oneClient = new ClientMacInfo();
//		oneClient.setClientMac("123456");
//		oneClient.setClientCount(2);
//		resultInfo.add(oneClient);
//		oneClient = new ClientMacInfo();
//		oneClient.setClientMac("567890");
//		oneClient.setClientCount(4);
//		resultInfo.add(oneClient);
		return null;
	}
	
	/**
	 * Get the managed device type name and count
	 *
	 *@param domain name
	 *
	 *@return device type name and count
	 */
	public static Map<String, Integer> getManagedDeviceTypeAndNumber(String domName) {
		if (StringUtils.isBlank(domName))
			return null;
		String parString = "bo.manageStatus = :s1 AND bo.simulated = :s2";
		List<Object> parValue = new ArrayList<>();
		parValue.add(HiveAp.STATUS_MANAGED);
		parValue.add(false);
		if (!HmDomain.HOME_DOMAIN.equals(domName)) {
			parString += " AND bo.owner.domainName = :s3";
			parValue.add(domName);
		}
		try {
			List<?> apNumberList = QueryUtil.executeQuery("SELECT DISTINCT bo.hiveApModel, COUNT(bo.hostName) FROM "+HiveAp.class.getSimpleName() + " bo", 
					new SortParams("bo.hiveApModel"), new FilterParams(parString, parValue.toArray()), new GroupByParams(new String[]{"hiveApModel"}), null);
			if (!apNumberList.isEmpty()) {
				Map<String, Integer> resultMap = new HashMap<>(apNumberList.size());
				for (Object obj : apNumberList) {
					Object[] value = (Object[])obj;
					resultMap.put(MgrUtil.getEnumString("enum.hiveAp.model." + (Short)value[0]),
							Integer.parseInt(String.valueOf(value[1])));
				}
				return resultMap;
			}
		} catch (Exception ex) {
			Log.error("getManagedDeviceTypeAndNumber() : "+ex.getMessage());
		}
		return null;
	}

}