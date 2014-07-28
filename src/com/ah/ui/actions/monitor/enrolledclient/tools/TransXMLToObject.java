package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.List;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.ui.actions.monitor.enrolledclients.entity.AppInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.DeviceSecurityInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientActivityLogList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientCertificateList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientDetail;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientInfoUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientNetworkInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientProfileList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientScanResultList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledDeviceDetailInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.GeneralInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.LocationInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.NetworkInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionsInfo;

public interface TransXMLToObject {
	public List<EnrolledClientInfoUI> getDeviceList(ResponseModel res);
	
	public List<EnrolledClientInfoUI> doRefreshDeviceList(ResponseModel res);
	
	public String doDeleteDevice(ResponseModel res);
	
	public String doUnenrollDevice(ResponseModel res);
	
	public EnrolledDeviceDetailInfo getDeviceDetail(ResponseModel res);
	
	public String doWipeDevice(ResponseModel res);
	
	public RestrictionInfoForUI getRestrictionInfo( ResponseModel res);
	
	public List<AppInfoForUI> getApplicationInfo(ResponseModel res);
	
	public GeneralInfoForUI getGeneralInfo(ResponseModel res);
	
	public NetworkInfoForUI getNetworkInfo(ResponseModel res);
	
	public LocationInfoForUI getLocationInfo(ResponseModel res);
	
	public List<RestrictionsInfo> getRestrictionsInfoList(ResponseModel res);
	
	public DeviceSecurityInfo getSecurityInfo(ResponseModel res);
	
	public EnrolledClientList getActiveClientListEnrolledInfo(ResponseModel res);
	
	public EnrolledClientDetail getActiveClientDetailInfo(ResponseModel res);
	
	public EnrolledClientNetworkInfo getActiveClientNetworkInfo(ResponseModel res);
	
	public EnrolledClientScanResultList getActiveClientScanResultInfo(ResponseModel res);
	
	public EnrolledClientProfileList getActiveClientProfileInfo(ResponseModel res);
	
	public EnrolledClientCertificateList getActiveClientCertificateInfo(ResponseModel res);
	
	public EnrolledClientActivityLogList getActiveClientActivityLogInfo(ResponseModel res);
 }
