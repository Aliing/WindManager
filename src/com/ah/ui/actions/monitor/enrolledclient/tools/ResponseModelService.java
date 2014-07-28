package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.List;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.bo.mgmt.SortParams;

public interface ResponseModelService {
	
	public ResponseModel getDeviceListAll(String customId,String pageNum, String pageSize,
			String status,String ownerType, String osType, String active,SortParams sortParam);
	
	public ResponseModel getDeviceDetail(String customId, String deviceId);
	
	public ResponseModel getRestrictionInfo( String customId, String deviceId);
	
	public ResponseModel getApplicationInfo(String customId, String deviceId);
	
	public ResponseModel getNetworkInfo(String customId, String deviceId);
	
	public ResponseModel getActiveClientEnrolledStatus(String customId,String macAddress);
	
	public ResponseModel getSecurityinfo(String customId,String deviceId);
	
	public ResponseModel operationOnClient(String customId, String deviceIdList, String actionName);
	
	public ResponseModel getActiveClientListEnrolledInfo(String customId, String vhmId, List<String> clientMacList);

	public ResponseModel getActiveClientDetailInfo(String customId, String vhmId, String macAddress);

	public ResponseModel getActiveClientNetworkInfo(String customId, String vhmId, String macAddress);
	
	public ResponseModel getActiveClientScanResultInfo(String customId, String vhmId, String macAddress);
	
	public ResponseModel getActiveClientScanResultInfo(String customId, String vhmId, String macAddress, int limit);
	
	public ResponseModel getActiveClientProfileInfo(String customId, String vhmId, String macAddress);

	public ResponseModel getActiveClientProfileInfo(String customId, String vhmId, String macAddress, int limit);

	public ResponseModel getActiveClientCertificateInfo(String customId, String vhmId, String macAddress);
	
	public ResponseModel getActiveClientCertificateInfo(String customId, String vhmId, String macAddress, int limit);

	public ResponseModel getActiveClientActivityLogInfo(String customId, String vhmId, String macAddress);
	
	public ResponseModel getActiveClientActivityLogInfo(String customId, String vhmId, String macAddress, int limit);
}
