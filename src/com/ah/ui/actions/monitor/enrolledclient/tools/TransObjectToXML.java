package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.List;

public interface TransObjectToXML {
	
	public String getIdListPostStr(String customId,String idList, String actionName);
	
	public String getDeviceListPostStr(String customId,String pageNum,String pageSize,String status,String ownerType,String osType,String active,List<SortParamForClient> sort);
	
	public String getAppListPostStr(String customId,String pageNum,String pageSize);
	
	public String getCertListPostStr(String customId,String pageNum,String pageSize);
	
	public String getProfileListPostStr(String customId,String pageNum,String pageSize);
	
	public String getDeviceInfoPostStr(String customId);
	
	public String getNetworkInfoPostStr(String customId);
	
	public String getRestrictionInfoPostStr(String customId);
	
	public String getActiveClientEnrolledPostStr(String customId, String macAddress);
	
	public String getOperationPostStr(String customerId, String deviceIdList, String actionName);
	
	public String getActiveClientListEnrolledPostStr(String customId, String vhmId, List<String> clientMacList);

	public String getActiveClientDetailPostStr(String customId, String vhmId, String macAddress);
	
	public String getActiveClientNetworkPostStr(String customId, String vhmId, String macAddress);
	
	public String getActiveClientScanResultPostStr(String customId, String vhmId, String macAddress);

	public String getActiveClientScanResultPostStr(String customId, String vhmId, String macAddress, int limit);
	
	public String getActiveClientActivityLogPostStr(String customId, String vhmId, String macAddress);

	public String getActiveClientActivityLogPostStr(String customId, String vhmId, String macAddress, int limit);
	
	public String getActiveClientCertificatePostStr(String customId, String vhmId, String macAddress);

	public String getActiveClientCertificatePostStr(String customId, String vhmId, String macAddress, int limit);
	
	public String getActiveClientProfilePostStr(String customId, String vhmId, String macAddress);

	public String getActiveClientProfilePostStr(String customId, String vhmId, String macAddress, int limit);
}
