package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.ui.actions.monitor.enrolledclients.entity.AppInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.AppInfoList;
import com.ah.ui.actions.monitor.enrolledclients.entity.DeviceList;
import com.ah.ui.actions.monitor.enrolledclients.entity.DeviceSecurityInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientActivityLogItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientActivityLogList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientCertificateItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientCertificateList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientDetail;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientNetworkInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientProfileItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientProfileList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientScanResultItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientScanResultList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledDeviceDetailInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.GeneralInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.LocationInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.NetworkInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionsInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionsListResponse;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientInfoUI;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TransXMLToObjectImpl implements TransXMLToObject {
	
	private static final Tracer log = new Tracer(ResponseModelServiceImpl.class.getSimpleName());
	
	public DeviceList getDeviceListWithTotalNumber(ResponseModel res){
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(DeviceList.class);
			xs.processAnnotations(EnrolledClientInfoUI.class);
			xs.alias("DeviceList", ArrayList.class);
			xs.alias("Device", EnrolledClientInfoUI.class);
			return (DeviceList)xs.fromXML(deviceXML);
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getDeviceListWithTotalNumber()","Failed when transfer xml to object", e);
			return null;
		}
	}
	public List<EnrolledClientInfoUI> getDeviceList(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(DeviceList.class);
			xs.processAnnotations(EnrolledClientInfoUI.class);
			xs.alias("DeviceList", ArrayList.class);
			xs.alias("Device", EnrolledClientInfoUI.class);
			DeviceList deviceList = (DeviceList)xs.fromXML(deviceXML);
			List<EnrolledClientInfoUI> tempList = deviceList.getDeviceList();
			return tempList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getDeviceList()","Failed when transfer xml to object", e);
			return null;
		}
	}

	public List<EnrolledClientInfoUI> doRefreshDeviceList(ResponseModel res) {
		return getDeviceList(res);
	}

	public String doDeleteDevice(ResponseModel res) {
		if(res == null){
			return null;
		}
		return "Succeed";
	}

	public String doUnenrollDevice(ResponseModel res) {
		if(res == null){
			return null;
		}
		return "Succeed";
	}

	public EnrolledDeviceDetailInfo getDeviceDetail(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledDeviceDetailInfo.class);
			return (EnrolledDeviceDetailInfo)xs.fromXML(deviceXML);
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":EnrolledDeviceDetailInfo()","Failed when transfer xml to object", e);
			return new EnrolledDeviceDetailInfo();
		}
	}

	public String doWipeDevice(ResponseModel res) {
		if(res == null){
			return null;
		}
		return "Succeed";
	}
	
	public RestrictionInfoForUI getRestrictionInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
		String deviceXML = res.getResponseText();
		XStream xs = new XStream(new DomDriver());
		xs.processAnnotations(RestrictionInfoForUI.class);
		return (RestrictionInfoForUI)xs.fromXML(deviceXML);
		}catch(Exception e){
			e.printStackTrace();
			return new RestrictionInfoForUI();
		}
	}

	public List<AppInfoForUI> getApplicationInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(AppInfoList.class);
			xs.processAnnotations(AppInfoForUI.class);
			xs.alias("AppList", ArrayList.class);
			xs.alias("App", AppInfoForUI.class);
			AppInfoList appList = (AppInfoList)xs.fromXML(appXML);
			List<AppInfoForUI> appUI = appList.getAppList();
			return appUI;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getApplicationInfo()","Failed when transfer xml to object", e);
			return new ArrayList<AppInfoForUI>();
		}
	}
	
	public EnrolledClientList getActiveClientListEnrolledInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientList.class);
			xs.processAnnotations(DeviceForClient.class);
			xs.alias("DeviceList", ArrayList.class);
			xs.alias("Device", DeviceForClient.class);
			EnrolledClientList retList = (EnrolledClientList)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientListEnrolledInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientDetail getActiveClientDetailInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientDetail.class);
			EnrolledClientDetail retList = (EnrolledClientDetail)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientDetailInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientNetworkInfo getActiveClientNetworkInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientNetworkInfo.class);
			EnrolledClientNetworkInfo retList = (EnrolledClientNetworkInfo)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientNetworkInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientScanResultList getActiveClientScanResultInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientScanResultList.class);
			xs.processAnnotations(EnrolledClientScanResultItem.class);
			xs.alias("ScanList", ArrayList.class);
			xs.alias("ScanInfo", EnrolledClientScanResultItem.class);
			EnrolledClientScanResultList retList = (EnrolledClientScanResultList)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientScanResultInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientProfileList getActiveClientProfileInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientProfileList.class);
			xs.processAnnotations(EnrolledClientProfileItem.class);
			xs.alias("ProfileList", ArrayList.class);
			xs.alias("Profile", EnrolledClientProfileItem.class);
			EnrolledClientProfileList retList = (EnrolledClientProfileList)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientProfileInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientCertificateList getActiveClientCertificateInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientCertificateList.class);
			xs.processAnnotations(EnrolledClientCertificateItem.class);
			xs.alias("CertList", ArrayList.class);
			xs.alias("Cert", EnrolledClientCertificateItem.class);
			EnrolledClientCertificateList retList = (EnrolledClientCertificateList)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientCertificateInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public EnrolledClientActivityLogList getActiveClientActivityLogInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String appXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(EnrolledClientActivityLogList.class);
			xs.processAnnotations(EnrolledClientActivityLogItem.class);
			xs.alias("LogList", ArrayList.class);
			xs.alias("Log", EnrolledClientActivityLogItem.class);
			EnrolledClientActivityLogList retList = (EnrolledClientActivityLogList)xs.fromXML(appXML);
			return retList;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientActivityLogInfo()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
//	public static void main(String [] args){
//
//		try{
//			String appXML = "<content version=\"1.0\"><DeviceUrlSuffix>/monitor/device/view/</DeviceUrlSuffix><DeviceList><Device macAddress=\"123\"  enrollmentStatus =\"true\"  deviceId=\"123\" /><Device macAddress=\"2324\"  enrollmentStatus =\"false\"  deviceId=\"456\" /></DeviceList></content>";			XStream xs = new XStream(new DomDriver());
//			xs.processAnnotations(EnrolledClientList.class);
//			xs.processAnnotations(DeviceForClient.class);
//			xs.alias("DeviceList", ArrayList.class);
//			xs.alias("Device", DeviceForClient.class);
//			EnrolledClientList retList = (EnrolledClientList)xs.fromXML(appXML);
//			System.out.println(retList);
//		}catch(Exception e){
//			e.printStackTrace();
//			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getActiveClientListEnrolledInfo()","Failed when transfer xml to object", e);
//			System.out.println("Error");
//		}
//		
//	}
	

	public GeneralInfoForUI getGeneralInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(GeneralInfoForUI.class);
			return (GeneralInfoForUI)xs.fromXML(deviceXML);
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getGeneralInfo()","Failed when transfer xml to object", e);
			return new GeneralInfoForUI();
		}
	}

	public NetworkInfoForUI getNetworkInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(NetworkInfoForUI.class);
			NetworkInfoForUI net = (NetworkInfoForUI)xs.fromXML(deviceXML);
			return net;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getNetworkInfo()","Failed when transfer xml to object", e);
			return new NetworkInfoForUI();
		}
	}

	public LocationInfoForUI getLocationInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(LocationInfoForUI.class);
			xs.alias("RestrictionsInfo", ArrayList.class);
			return (LocationInfoForUI)xs.fromXML(deviceXML);
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getLocationInfo()","Failed when transfer xml to object", e);
			return new LocationInfoForUI();
		}
	}

	@Override
	public List<RestrictionsInfo> getRestrictionsInfoList(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String deviceXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(RestrictionsListResponse.class);
			xs.processAnnotations(RestrictionsInfo.class);
			xs.alias("RestrictionsInfoList",ArrayList.class);
			xs.alias("RestrictionsInfo", RestrictionsInfo.class);
			return ((RestrictionsListResponse)xs.fromXML(deviceXML)).getRestrictionList();
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getRestrictionsInfoList()","Failed when transfer xml to object", e);
			return new ArrayList<RestrictionsInfo>();
		}
	}
	@Override
	public DeviceSecurityInfo getSecurityInfo(ResponseModel res) {
		if(res == null){
			return null;
		}
		try{
			String securityXML = res.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(DeviceSecurityInfo.class);
			DeviceSecurityInfo net = (DeviceSecurityInfo)xs.fromXML(securityXML);
			return net;
		}catch(Exception e){
			e.printStackTrace();
			log.error(TransXMLToObjectImpl.class.getSimpleName()+":getSecurityInfo()","Failed when transfer xml to object", e);
			return new DeviceSecurityInfo();
		}
	}
}
