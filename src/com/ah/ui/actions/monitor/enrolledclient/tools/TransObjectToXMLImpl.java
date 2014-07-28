package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.ConfigUtil;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TransObjectToXMLImpl implements TransObjectToXML{
	
	private static final Tracer log = new Tracer(ResponseModelServiceImpl.class.getSimpleName());

	public String getIdListPostStr(String customId, String idList, String actionName) {
		DeviceIdList list = new DeviceIdList();
		List<String> de = new ArrayList<String>();
		XStream xs = new XStream(new DomDriver());
		list.setCustomId(customId);
		if(actionName != null && actionName != ""){
			list.setActionName(actionName);
		}
		list.setVersion(ConfigUtil.getVersion());
		try {
			if (idList.indexOf(",") == -1) {
				de.add(idList);
				list.setIdList(de);
				xs.processAnnotations(DeviceIdList.class);
				xs.alias("DeviceId", String.class);
				return xs.toXML(list).toString();
			} else {
				String[] deviceIdList = idList.split(",");
				for (String s : deviceIdList) {
					de.add(s);
				}
				list.setIdList(de);
				xs.processAnnotations(DeviceIdList.class);
				xs.alias("DeviceId", String.class);
				return xs.toXML(list).toString();
			}
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getIdListPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(DeviceIdList.class);
			xs.alias("DeviceId", String.class);
			return xs.toXML(new DeviceIdList());
		}

	}
	
	@Override
	public String getDeviceListPostStr(String customId, String pageNum,
			String pageSize, String status, String ownerType, String osType,
			String active,List<SortParamForClient> sort) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setVersion(ConfigUtil.getVersion());
		try {
			if (pageNum != "" && pageNum != null) {
				pos.setPageNumber(pageNum);
			}
			if (pageSize != "" && pageSize != null) {
				pos.setPageSize(pageSize);
			}
			if (osType != "" && osType != null) {
				pos.setOstype(osType);
			}
			if (ownerType != "" && ownerType != null) {
				pos.setOwnerType(ownerType);
			}
			if (status != "" && status != null) {
				pos.setStatus(status);
			}
			if (active != "" && active != null) {
				pos.setActiveStatus(active);
			}
			if(sort != null){
				pos.setSort(sort);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			xs.processAnnotations(SortParamForClient.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getDeviceListPostStr()",
					"failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			xs.processAnnotations(SortParamForClient.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}

	@Override
	public String getAppListPostStr(String customId, String pageNum,
			String pageSize) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setVersion(ConfigUtil.getVersion());
		try {
			if (pageNum != "" && pageNum != null) {
				pos.setPageNumber(pageNum);
			}
			if (pageSize != "" && pageSize != null) {
				pos.setPageSize(pageSize);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getAppListPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}
	
	@Override
	public String getActiveClientListEnrolledPostStr(String customId, String vhmId, 
			List<String> clientMacList) { 
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());

		try {
			if (clientMacList != null && !clientMacList.isEmpty()) {
				
				for(String mac : clientMacList) {
					DeviceForClient dfc = new DeviceForClient();
					dfc.setMacAddress(mac);
					if (pos.getDeviceList()==null) {
						List<DeviceForClient> dfcList = new ArrayList<DeviceForClient>();
						pos.setDeviceList(dfcList);
					}
					pos.getDeviceList().add(dfc);
				}
			}

			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientListEnrolledPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientDetailPostStr(String customId, String vhmId, 
			String macAddress) { 
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientDetailPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientNetworkPostStr(String customId, String vhmId, 
			String macAddress) { 
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientNetworkPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientScanResultPostStr(String customId, String vhmId, 
			String macAddress) { 
		return getActiveClientScanResultPostStr(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public String getActiveClientScanResultPostStr(String customId, String vhmId, 
			String macAddress, int limit) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());
		pos.setLimit(limit);

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientScanResultPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientActivityLogPostStr(String customId, String vhmId, 
			String macAddress) { 
		return getActiveClientActivityLogPostStr(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public String getActiveClientActivityLogPostStr(String customId, String vhmId, 
			String macAddress, int limit) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());
		pos.setLimit(limit);

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientActivityLogPostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientCertificatePostStr(String customId, String vhmId, 
			String macAddress) { 
		return getActiveClientCertificatePostStr(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public String getActiveClientCertificatePostStr(String customId, String vhmId, 
			String macAddress, int limit) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());
		pos.setLimit(limit);

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientCertificatePostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
	@Override
	public String getActiveClientProfilePostStr(String customId, String vhmId, 
			String macAddress) { 
		return getActiveClientProfilePostStr(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public String getActiveClientProfilePostStr(String customId, String vhmId, 
			String macAddress, int limit) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setCustomId(customId);
		pos.setHmId(vhmId);
		pos.setVersion(ConfigUtil.getVersion());
		pos.setLimit(limit);

		try {
			if (macAddress != null && !macAddress.isEmpty()) {
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientProfilePostStr()", "failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}
	}
	
//	public static void main(String [] args){
//
//		List<String> l = new ArrayList<String>();
//		l.add("123");
//		l.add("2324");
//		
//		XStream xs = new XStream(new DomDriver());
//		GenerateXMLPostStr pos = new GenerateXMLPostStr();
//		pos.setCustomId("woshihe");
//		pos.setHmId("woshihe-VHM");
//		//pos.setVersion(ConfigUtil.getVersion());
//		pos.setVersion("1.0");
//		try {
//			if (l != null && !l.isEmpty()) {
//				
//				for(String mac : l) {
//					DeviceForClient dfc = new DeviceForClient();
//					dfc.setMacAddress(mac);
//					if (pos.getDeviceList()==null) {
//						List<DeviceForClient> dfcList = new ArrayList<DeviceForClient>();
//						pos.setDeviceList(dfcList);
//					}
//					pos.getDeviceList().add(dfc);
//				}
//			}
//
//			xs.processAnnotations(GenerateXMLPostStr.class);
//			System.out.println(xs.toXML(pos).toString());
//
//		} catch (Exception e) {
//			log.error(TransObjectToXMLImpl.class.getSimpleName()
//					+ ":getActiveClientListEnrolledPostStr()", "failed to get XML from Oject", e);
//			xs.processAnnotations(GenerateXMLPostStr.class);
//			System.out.println(xs.toXML(pos).toString());
//		}
//		
//	}

	@Override
	public String getCertListPostStr(String customId, String pageNum,
			String pageSize) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setVersion(ConfigUtil.getVersion());
		pos.setCustomId(customId);
		try {
			if (pageNum != "" && pageNum != null) {
				pos.setPageNumber(pageNum);
			}
			if (pageSize != "" && pageSize != null) {
				pos.setPageSize(pageSize);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getCertListPostStr()", "failed to get XML from Oject",
					e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}

	@Override
	public String getProfileListPostStr(String customId, String pageNum,
			String pageSize) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setVersion(ConfigUtil.getVersion());
		pos.setCustomId(customId);
		try {
			if (pageNum != "" && pageNum != null) {
				pos.setPageNumber(pageNum);
			}
			if (pageSize != "" && pageSize != null) {
				pos.setPageSize(pageSize);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getProfileListPostStr()",
					"failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}

	@Override
	public String getDeviceInfoPostStr(String customId) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setVersion(ConfigUtil.getVersion());
		pos.setCustomId(customId);
		try {
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getDeviceInfoPostStr()",
					"failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}

	@Override
	public String getNetworkInfoPostStr(String customId) {
		return getDeviceInfoPostStr(customId);
	}

	@Override
	public String getRestrictionInfoPostStr(String customId) {
		return getDeviceInfoPostStr(customId);
	}
	
	public String getOperationPostStr(String customerId, String deviceIdList, String actionName){
		return getIdListPostStr(customerId, deviceIdList,actionName);
	}

	@Override
	public String getActiveClientEnrolledPostStr(String customId,
			String macAddress) {
		XStream xs = new XStream(new DomDriver());
		GenerateXMLPostStr pos = new GenerateXMLPostStr();
		pos.setVersion(ConfigUtil.getVersion());
		pos.setCustomId(customId);
		try {
			if(macAddress != null && macAddress != ""){
				pos.setMacAddress(macAddress);
			}
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(pos);
		} catch (Exception e) {
			log.error(TransObjectToXMLImpl.class.getSimpleName()
					+ ":getActiveClientEnrolledPostStr()",
					"failed to get XML from Oject", e);
			xs.processAnnotations(GenerateXMLPostStr.class);
			return xs.toXML(new GenerateXMLPostStr());
		}

	}
}
