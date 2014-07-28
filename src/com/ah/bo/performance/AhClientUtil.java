package com.ah.bo.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.SubNetworkResource;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class AhClientUtil {
	public static void setClientNatIp(List<?> page) {
		if (null == page || page.isEmpty()) {
			return;
		}
		List<String> hiveApMacList = new ArrayList<String>();
		Map<String, String> apMacMap = new HashMap<String, String>();
		for (Object obj : page) {
			AhClientSession asection = (AhClientSession) obj;
			if (!deviceIsRouter(asection.getApMac())
					|| apMacMap.containsKey(asection.getApMac())) {
				continue;
			}
			apMacMap.put(asection.getApMac(), asection.getApMac());
			hiveApMacList.add(asection.getApMac());
		}
		FilterParams filterParam = new FilterParams("hiveApMac", hiveApMacList);
		List<SubNetworkResource> subResourceList = querySubNetworkResources(filterParam);
		if (null == subResourceList || subResourceList.isEmpty()) {
			return;
		}
		Map<String, List<SubNetworkResource>> resourceMap = new HashMap<String, List<SubNetworkResource>>();
		for (SubNetworkResource resource : subResourceList) {
			if (!resourceMap.containsKey(resource.getHiveApMac())) {
				resourceMap.put(resource.getHiveApMac(),
						new ArrayList<SubNetworkResource>());
			}
			resourceMap.get(resource.getHiveApMac()).add(resource);
		}
		List<SubNetworkResource> resourceList = null;
		String natIp = "";
		for (Object obj : page) {
			AhClientSession asection = (AhClientSession) obj;
			resourceList = resourceMap.get(asection.getApMac());
			if (null == resourceList || resourceList.isEmpty()) {
				continue;
			}
			for (SubNetworkResource resource : resourceList) {
				natIp = getClientNatIP(resource, asection.getClientIP());
				if (!"".equals(natIp) && null != natIp) {
					break;
				}
			}
			asection.setClientNatIP(natIp);
		}
	}

	public static String getClientNatIP(SubNetworkResource subResource,
			String clientIP) {
		if (null == subResource || null == clientIP
				|| null == subResource.getNetwork()
				|| null == subResource.getLocalNetwork() || "".equals(clientIP)
				|| "".equals(subResource.getNetwork())
				|| "".equals(subResource.getLocalNetwork())) {
			return "";
		}
		if (!subResource.isEnableNat()) {
			return "";
		}
		String clientNatIP = "";
		// localIp
		int flagIndex = subResource.getLocalNetwork().indexOf("/");
		int mask = Integer.valueOf(subResource.getLocalNetwork().substring(
				flagIndex + 1));
		long allNum = (long) Math.pow(2, 32 - mask);
		String ipAddr = subResource.getLocalNetwork().substring(0, flagIndex);
		long ipNum = AhEncoder.ip2Long(ipAddr);
		// natIp
		flagIndex = subResource.getNetwork().indexOf("/");
		String natIpAddr = subResource.getNetwork().substring(0, flagIndex);
		long natIpNum = AhEncoder.ip2Long(natIpAddr);
		for (long i = 0; i < allNum; i++) {
			if (clientIP.equals(AhDecoder.long2Ip(ipNum + i))) {
				clientNatIP = AhDecoder.long2Ip(natIpNum + i);
				break;
			}
		}
		return clientNatIP;
	}

	public static List<SubNetworkResource> querySubNetworkResources(
			FilterParams filterParam) {
		if (("".equals(filterParam.getValue()) || null == filterParam
				.getValue())
				&& (null == filterParam.getValues() || filterParam.getValues()
						.isEmpty())) {
			return null;
		}
		List<?> list = executeSubNetworkResurceSQL(filterParam);
		if (null == list || list.isEmpty()) {
			return null;
		}
		List<SubNetworkResource> subNetworkResourceList = new ArrayList<SubNetworkResource>();
		SubNetworkResource resource = null;
		for (Object object : list) {
			resource = new SubNetworkResource();
			Object[] objects = (Object[]) object;
			String hiveApMac = (String) objects[0];
			String localnetwork = (String) objects[1];
			String network = (String) objects[2];
			boolean enableNat = (Boolean) objects[3];
			resource.setHiveApMac(hiveApMac);
			resource.setLocalNetwork(localnetwork);
			resource.setNetwork(network);
			resource.setEnableNat(enableNat);
			subNetworkResourceList.add(resource);
		}
		return subNetworkResourceList;
	}

	private static List<?> executeSubNetworkResurceSQL(FilterParams filterParam) {
		List<?> list = QueryUtil.executeQuery(
				"select hiveApMac,localNetwork,network,enableNat from "
						+ SubNetworkResource.class.getSimpleName(), null,
				filterParam);
		return list;
	}

	public static boolean deviceIsRouter(String mac) {
		if ("".equals(mac) || null == mac) {
			return false;
		}
		SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(mac);
		if (null == hiveAp
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != hiveAp.getDeviceType()) {
			return false;
		}
		return true;
	}
}
