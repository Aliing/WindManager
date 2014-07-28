package com.ah.be.performance;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.performance.AhPCIData;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.ComplianceResult;
import com.ah.bo.performance.ComplianceSsidListInfo;
import com.ah.bo.performance.PciWeakSsidInfo;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.monitor.HeaderFooterPage;
import com.ah.ui.actions.monitor.ReportListAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import java.awt.Color;
import java.awt.Graphics2D;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PCIReportGenerateImpl implements QueryBo{
	private static final Tracer log = new Tracer(
			BePerformScheduleImpl.class.getSimpleName());
	private AhReport profile;
	private TimeZone tz;
	private String currentDir;
	private String pdfFileName;
	private long starttimeLong;
	private long endtimeLong;
	
	private List<HiveAp> hiveApList;
	private String globalDevicePwd = "";
	private Map<String, Set<String>> internalLogServerList = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> externalLogServerList = new HashMap<String, Set<String>>();
	private Set<String> broadcastSsid = new HashSet<String>();
	private Set<String> weakEncryptionSsid = new HashSet<String>();
	
	private List<PciWeakSsidInfo> ssidWeakInfoList = new ArrayList<PciWeakSsidInfo>();
	private List<ComplianceResult> hiveApCompliance = new ArrayList<ComplianceResult>();
	
	private Set<String> wpaSsid = new HashSet<String>();
	private Set<String> pskSsid = new HashSet<String>();
	private Set<String> authSsid = new HashSet<String>();
	private Set<String> wepSsid = new HashSet<String>();
	private Set<String> openSsid = new HashSet<String>();
	
	private long clientFaitureCount=0;
	private long macLayerAlertCount=0;
	private long ipLayerAlertCount=0;
	
	public PCIReportGenerateImpl(AhReport profile, 
			TimeZone tz, String currentDir, String pdfFileName, long starttimeLong
			, long endtimeLong){
		this.profile=profile;
		this.tz=tz;
		this.currentDir = currentDir;
		this.pdfFileName=pdfFileName;
		this.endtimeLong=endtimeLong;
		this.starttimeLong=starttimeLong;
		
	}
	
	private List<HiveAp> getAllHiveApList(){
		List<Short> notInList = new ArrayList<>();
		notInList.add(HiveAp.HIVEAP_MODEL_SR24);
		notInList.add(HiveAp.HIVEAP_MODEL_SR48);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2124P);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2148P);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2024P);				
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		FilterParams myFilterParams;
		if (profile.getLocation()!=null && profile.getLocation().getId()>0){		
			myFilterParams = new FilterParams("manageStatus = :s1 and owner.id = :s2 and mapContainer.id=:s3 and " +
					" (deviceType=:s4 or deviceType=:s5 or deviceType=:s6) " +
					" and hiveApModel not in :s7 ",
					new Object[] { HiveAp.STATUS_MANAGED, profile.getOwner().getId(), profile.getLocation().getId(), 
					HiveAp.Device_TYPE_HIVEAP, HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,
					notInList});
		} else {		
			myFilterParams = new FilterParams("manageStatus = :s1 and owner.id = :s2 and " +
					" (deviceType=:s3 or deviceType=:s4 or deviceType=:s5) " +
					" and hiveApModel not in :s6 ",
					new Object[] { HiveAp.STATUS_MANAGED, profile.getOwner().getId(), HiveAp.Device_TYPE_HIVEAP, 
					HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,notInList});
		}
		return QueryUtil.executeQuery(HiveAp.class, null, myFilterParams);
	}
	
	private long setAuthFailureCount(){
		long clientFaitureCount = 0;
		Set<String> apMacSet = new HashSet<String>();
		if (hiveApList!=null && hiveApList.size()>0){
			for (HiveAp hiveAp : hiveApList) {
				apMacSet.add(hiveAp.getMacAddress());
			}
		}
		if (apMacSet.isEmpty()){
			return clientFaitureCount;
		}
		try {
			Object lstCondition[] = new Object[7];
			String searchSQL = "trapTimeStamp.time >=:s1 and trapTimeStamp.time <:s2 and eventType =:s3 " +
					"and objectType=:s4 and currentState=:s5 and code=:s6 and owner.id=:s7";

			lstCondition[0]=starttimeLong;
			lstCondition[1]=endtimeLong;
			lstCondition[2]=AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE;
			lstCondition[3]=AhEvent.AH_OBJECT_TYPE_CLIENT_LINK;
			lstCondition[4]=AhEvent.AH_STATE_DOWN;
			lstCondition[5]=ReportListAction.CLIENT_DE_AUTH_CODE;
			lstCondition[6]=profile.getOwner().getId();
			FilterParams myFilterParams = new FilterParams(searchSQL, lstCondition);
			
			List<?> eventList = QueryUtil.executeQuery("select apId,id from " + AhEvent.class.getSimpleName(), null, myFilterParams);
			
			if (eventList!=null && !eventList.isEmpty()){
				for(Object object:eventList){
					Object[] obj = (Object[])object;
					String eventRemoteId = obj[0].toString();
					if (apMacSet.contains(eventRemoteId)){
						clientFaitureCount++;
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return clientFaitureCount;
	}
	
	private void getInformationOfHiveAp() throws Exception{
		if (hiveApList!=null && hiveApList.size()>0) {
			for (HiveAp tmpHiveAp : hiveApList) {
				tmpHiveAp = QueryUtil.findBoById(HiveAp.class, tmpHiveAp.getId(), this);
				//Log Servers
				MgmtServiceSyslog sysLog = tmpHiveAp.getConfigTemplate().getMgmtServiceSyslog();
				if (sysLog != null) {
					if (sysLog.getSyslogInfo() != null && sysLog.getSyslogInfo().size() > 0) {
						for (MgmtServiceSyslogInfo info : sysLog.getSyslogInfo()) {
							if (sysLog.getInternalServer()) {
								if (internalLogServerList.get(info.getServerName()) == null) {
									internalLogServerList.put(info.getServerName(), new HashSet<String>());
								}
								internalLogServerList.get(info.getServerName()).add(tmpHiveAp.getConfigTemplateName());
							} else {
								if (externalLogServerList.get(info.getServerName()) == null) {
									externalLogServerList.put(info.getServerName(), new HashSet<String>());
								}
								externalLogServerList.get(info.getServerName()).add(tmpHiveAp.getConfigTemplateName());
							}
						}
					}
				}

				// Device Configuration Compliance
				Map<Long, ConfigTemplateSsid> templateSsid = tmpHiveAp.getConfigTemplate().getSsidInterfaces();
				for (Long keyset : templateSsid.keySet()) {
					ConfigTemplateSsid tempssid = templateSsid.get(keyset);
					if (tempssid.getSsidProfile() != null) {
						if (!tempssid.getSsidProfile().getHide()) {
							broadcastSsid.add(tempssid.getSsidProfile().getSsid());
						}

						if (tempssid.getSsidProfile().getEncryption() == SsidProfile.KEY_ENC_NONE ||
								tempssid.getSsidProfile().getEncryption() == SsidProfile.KEY_ENC_WEP104 ||
								tempssid.getSsidProfile().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
							weakEncryptionSsid.add(tempssid.getSsidProfile().getSsid());
							PciWeakSsidInfo tmpWeakInfo = new PciWeakSsidInfo();
							tmpWeakInfo.setApName(tmpHiveAp.getHostName());
							tmpWeakInfo.setSsidName(tempssid.getSsidProfile().getSsid());
							tmpWeakInfo.setAccessSecurity(tempssid.getSsidProfile().getAccessMode());
							tmpWeakInfo.setEncryption(tempssid.getSsidProfile().getEncryption());
							ssidWeakInfoList.add(tmpWeakInfo);
						}

						if (tempssid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
							wpaSsid.add(tempssid.getSsidProfile().getSsid());
						} else if (tempssid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_WEP) {
							wepSsid.add(tempssid.getSsidProfile().getSsid());
						} else if (tempssid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
							pskSsid.add(tempssid.getSsidProfile().getSsid());
						} else if (tempssid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_8021X) {
							authSsid.add(tempssid.getSsidProfile().getSsid());
						} else if (tempssid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
							openSsid.add(tempssid.getSsidProfile().getSsid());
						}
					}
				}
				hiveApCompliance.add(getHiveApComplianceResult(tmpHiveAp,globalDevicePwd));
			}
		}
	}
	
	private boolean checkHiveAPPasswordStrong(String pass) {
		if (pass==null || pass.isEmpty()) {
			return false;
		}
		//Require a minimum length of at least seven characters. 
		if (pass.length()<7) {
			return false;
		}
		//Contain both numeric and alphabetic characters. 
		Pattern pa = Pattern.compile(".*[a-zA-Z].*[0-9].*|.*[0-9].*[a-zA-Z].*");
		Matcher m = pa.matcher(pass);
		return m.matches();

	}
	
	private ComplianceResult getHiveApComplianceResult(HiveAp hiveap, String globalDevicePwd) {
		ComplianceResult complianceResult = new ComplianceResult();

		complianceResult.setHiveApName(hiveap.getHostName());
		complianceResult.setApMac(hiveap.getMacAddress());
		complianceResult.setApType(hiveap.getHiveApModelString());
		complianceResult.setApSn(hiveap.getSerialNumber());

		if (hiveap.getAdminPassword()==null || hiveap.getAdminPassword().equals("")){			
			if (globalDevicePwd == null
					|| "".equals(globalDevicePwd)
					|| globalDevicePwd.equals(NmsUtil.getOEMCustomer().getDefaultAPPassword())) {
				complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
				complianceResult.setBlnDefaultWeakDevicePwd(true);
			} else {
				complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_STRONG);
			}
			complianceResult.setHiveApPassStrong(checkHiveAPPasswordStrong(globalDevicePwd));
			
		} else {
			if (hiveap.getReadOnlyPassword()==null || hiveap.getReadOnlyPassword().equals("")){
				if (hiveap.getAdminPassword().equals(NmsUtil.getOEMCustomer().getDefaultAPPassword())){
					complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
				} else {
					complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_STRONG);
				}
				complianceResult.setHiveApPassStrong(checkHiveAPPasswordStrong(hiveap.getAdminPassword()));
			}else {
				if (hiveap.getAdminPassword().equals(NmsUtil.getOEMCustomer().getDefaultAPPassword()) 
						|| hiveap.getReadOnlyPassword().equals(NmsUtil.getOEMCustomer().getDefaultAPPassword())){
					complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
				} else {
					complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_STRONG);
				}
				complianceResult.setHiveApPassStrong(checkHiveAPPasswordStrong(hiveap.getAdminPassword()) && 
						checkHiveAPPasswordStrong(hiveap.getReadOnlyPassword()));
			}
		}
		
		HiveProfile hive = hiveap.getConfigTemplate().getHiveProfile();
		if (!hive.getEnabledPassword()){
			complianceResult.setHivePass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
			complianceResult.setHivePassStrong(false);
		} else {
			complianceResult.setHivePass(ComplianceResult.PASSWORD_STRENGTH_STRONG);
			complianceResult.setHivePassStrong(checkHiveAPPasswordStrong(hive.getHivePassword()));
		}
		
		if (!hiveap.getConfigTemplate().isOverrideTF4IndividualAPs() 
				&& hiveap.getConfigTemplate().getDeviceServiceFilter()!=null) {
			ServiceFilter defFilter = hiveap.getConfigTemplate().getDeviceServiceFilter();
			hiveap.getConfigTemplate().setEth0ServiceFilter(defFilter);
			hiveap.getConfigTemplate().setEth0BackServiceFilter(defFilter);
			hiveap.getConfigTemplate().setWireServiceFilter(defFilter);
			hiveap.getConfigTemplate().setEth1ServiceFilter(defFilter);
			hiveap.getConfigTemplate().setEth1BackServiceFilter(defFilter);
			hiveap.getConfigTemplate().setRed0ServiceFilter(defFilter);
			hiveap.getConfigTemplate().setRed0BackServiceFilter(defFilter);
			hiveap.getConfigTemplate().setAgg0ServiceFilter(defFilter);
			hiveap.getConfigTemplate().setAgg0BackServiceFilter(defFilter);
		}
		
		ServiceFilter serviceFilter =hiveap.getConfigTemplate().getEth0ServiceFilter();
		ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
				serviceFilter,"eth0");
		if (hiveap.getConfigTemplate().getMgmtServiceSnmp().getEnableSnmp()){
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_POOR);
		}
		complianceResult.getSsidList().add(ssidListInfo);
		
		serviceFilter =hiveap.getConfigTemplate().getEth0BackServiceFilter();
		ssidListInfo = getComplianceSsidListInfo(
				serviceFilter,"backeth0");
		complianceResult.getSsidList().add(ssidListInfo);
		
		serviceFilter =hiveap.getConfigTemplate().getWireServiceFilter();
		ssidListInfo = getComplianceSsidListInfo(
				serviceFilter,"backwire");
		complianceResult.getSsidList().add(ssidListInfo);
		
		if (hiveap.isEth1Available()){
			serviceFilter =hiveap.getConfigTemplate().getEth1ServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"eth1");
			complianceResult.getSsidList().add(ssidListInfo);
			
			serviceFilter =hiveap.getConfigTemplate().getEth1BackServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"backeth1");
			complianceResult.getSsidList().add(ssidListInfo);
			
			serviceFilter =hiveap.getConfigTemplate().getRed0ServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"red0");
			complianceResult.getSsidList().add(ssidListInfo);
			
			serviceFilter =hiveap.getConfigTemplate().getRed0BackServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"backred0");
			complianceResult.getSsidList().add(ssidListInfo);
			
			serviceFilter =hiveap.getConfigTemplate().getAgg0ServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"agg0");
			complianceResult.getSsidList().add(ssidListInfo);
			
			serviceFilter =hiveap.getConfigTemplate().getAgg0BackServiceFilter();
			ssidListInfo = getComplianceSsidListInfo(
					serviceFilter,"backagg0");
			complianceResult.getSsidList().add(ssidListInfo);
		}
		
		return complianceResult;
	}
	
	private ComplianceSsidListInfo getComplianceSsidListInfo(
			ServiceFilter serviceFilter,String name){
		ComplianceSsidListInfo ssidListInfo = new ComplianceSsidListInfo();
		ssidListInfo.setSsidName(name);
		ssidListInfo.setRating(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		if (serviceFilter.getEnableSSH()){
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_GOOD);
		} else {
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnablePing()){
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_GOOD);
		} else {
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableTelnet()){
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_POOR);
		} else {
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableSNMP()){
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_POOR);
		} else {
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		return ssidListInfo;
	}
	
	private boolean checkPerformed() {
		try {
			if (hiveApList!=null && hiveApList.size()>0) {
				for (HiveAp tmpHiveAp : hiveApList) {
					tmpHiveAp = QueryUtil.findBoById(HiveAp.class, tmpHiveAp.getId(), this);
					HiveProfile hive = tmpHiveAp.getConfigTemplate().getHiveProfile();
					for (String keySet : hive.getHiveDos().getDosParamsMap().keySet()) {
						if (hive.getHiveDos().getDosParamsMap().get(keySet).isEnabled()) {
							return true;
						}
					}
					for (String keySet : hive.getStationDos().getDosParamsMap().keySet()) {
						if (hive.getStationDos().getDosParamsMap().get(keySet).isEnabled()) {
							return true;
						}
					}
					if (hive.getMacFilters() != null && hive.getMacFilters().size() > 0) {
						return true;
					}

					Map<Long, ConfigTemplateSsid> templateSsid = tmpHiveAp.getConfigTemplate().getSsidInterfaces();
					for (Long keyset : templateSsid.keySet()) {
						ConfigTemplateSsid tempssid = templateSsid.get(keyset);
						if (tempssid.getSsidProfile() != null) {
							SsidProfile lazySsid = QueryUtil.findBoById(SsidProfile.class, tempssid.getSsidProfile().getId(), this);
							for (String ssidDosKey : lazySsid.getSsidDos().getDosParamsMap().keySet()) {
								if (lazySsid.getSsidDos().getDosParamsMap().get(ssidDosKey).isEnabled()) {
									return true;
								}
							}
							for (String ssidDosKey : lazySsid.getStationDos().getDosParamsMap().keySet()) {
								if (lazySsid.getStationDos().getDosParamsMap().get(ssidDosKey).isEnabled()) {
									return true;
								}
							}
							for (String ssidDosKey : lazySsid.getIpDos().getDosParamsMap().keySet()) {
								if (lazySsid.getIpDos().getDosParamsMap().get(ssidDosKey).isEnabled()) {
									return true;
								}
							}
							if (tempssid.getSsidProfile().getIpDos().getEnabledSynCheck()) {
								return true;
							}

							if (lazySsid.getMacFilters() != null && lazySsid.getMacFilters().size() > 0) {
								return true;
							}
						}
					}
				}
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			return true;
		}
	}

	private List<AhPCIData> getAlertData(){
		FilterParams myFilterParams;
		if (profile.getLocation()!=null && profile.getLocation().getId()>0){
			myFilterParams = new FilterParams("reportTime >=:s1 and reportTime <:s2 and owner.id=:s3 and mapID=:s4",
					new Object[] { starttimeLong,endtimeLong,
					profile.getOwner().getId(),profile.getLocation().getId() });
		} else {
			myFilterParams = new FilterParams("reportTime >=:s1 and reportTime <:s2 and owner.id=:s3",
					new Object[] { starttimeLong,endtimeLong,
					profile.getOwner().getId()});
		}
				
		List<AhPCIData> pciReportData = QueryUtil.executeQuery(AhPCIData.class,new SortParams("nodeID,alertCode,reportTime"),myFilterParams);
		
		if (pciReportData!=null && pciReportData.size()>0){
			for(AhPCIData tmpClass:pciReportData){
				if (tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_PROBE_REQUEST || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_PROBE_RESPONSE || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_ASSOC_REQUEST || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_ASSOC_RESPONSE || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_DEASSOC || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_AUTH || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_DEAUTH || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_EAPOL || 
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_MAC_FIREWALL_VIOLATION ||
						tmpClass.getAlertCode()==AhPCIData.ALERT_CODE_MAC_FILTER_VIOLATION){
					macLayerAlertCount = macLayerAlertCount + tmpClass.getViolationCounter();
				} else {
					ipLayerAlertCount = ipLayerAlertCount + tmpClass.getViolationCounter();
				}
			}
		}

		return pciReportData;
	}
	// find Rogue APs
		private String getRogueApCount(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and mapId=:s4 and reportTime.time>=:s5 and reportTime.time<:s6",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   profile.getLocation().getId(),
									   starttimeLong,
									   endtimeLong});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and reportTime.time>=:s4 and reportTime.time<:s5",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   starttimeLong,
									   endtimeLong});
			}
			
			List<?> count = QueryUtil.executeQuery( "select count(DISTINCT ifMacAddress) from " 
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			return count.get(0).toString();
		}
		
	
	// find Rogue APs
		private int getRogueApCountInNet(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and mapId=:s4 and reportTime.time>=:s5 and reportTime.time<:s6 and inNetworkFlag=:s7",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   profile.getLocation().getId(),
									   starttimeLong,
									   endtimeLong,
									   Idp.IDP_CONNECTION_IN_NET});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and reportTime.time>=:s4 and reportTime.time<:s5 and inNetworkFlag=:s6",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   starttimeLong,
									   endtimeLong,
									   Idp.IDP_CONNECTION_IN_NET});
			}
			
			List<?> count = QueryUtil.executeQuery( "select count(DISTINCT ifMacAddress) from " 
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			return Integer.parseInt(count.get(0).toString());
		}
	
	// find Rogue Client count
		private int getRogueClientCounts(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and mapId=:s4 and reportTime.time>=:s5 and reportTime.time<:s6",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   profile.getLocation().getId(),
									   starttimeLong,
									   endtimeLong});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and reportTime.time>=:s4 and reportTime.time<:s5",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   starttimeLong,
									   endtimeLong});
			}
			
			List<?> count = QueryUtil.executeQuery( "select count(DISTINCT ifMacAddress) from " 
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			return Integer.parseInt(count.get(0).toString());
		}
		
		// find Friendly Aps count
		private String getFriendlyApCount(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and (idpType=:s2 or idpType=:s3) and owner.id = :s4 and mapId=:s5",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_EXTERNAL,
									   BeCommunicationConstant.IDP_TYPE_VALID,
									   profile.getOwner().getId(),
									   profile.getLocation().getId()});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and (idpType=:s2 or idpType=:s3) and owner.id = :s4",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
						   				BeCommunicationConstant.IDP_TYPE_EXTERNAL,
						   				BeCommunicationConstant.IDP_TYPE_VALID,
						   				profile.getOwner().getId()});
			}
			List<?> count = QueryUtil.executeQuery( "select count(DISTINCT ifMacAddress) from " 
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			
			return count.get(0).toString();
		}
		
		// find Authorized APs
		private String getAuthApCount(){
			List<Short> notInList = new ArrayList<>();
			notInList.add(HiveAp.HIVEAP_MODEL_SR24);
			notInList.add(HiveAp.HIVEAP_MODEL_SR48);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2124P);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2148P);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2024P);				
			notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
			long intAuthApCount;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){				
				intAuthApCount = QueryUtil.findRowCount(HiveAp.class, 
						new FilterParams("mapContainer.id=:s1 and owner.id=:s2 and " +
								" (deviceType=:s3 or deviceType=:s4 or deviceType=:s5) " +
								" and hiveApModel not in :s6 ",
								new  Object[]{profile.getLocation().getId(),profile.getOwner().getId(),
								HiveAp.Device_TYPE_HIVEAP,
								HiveAp.Device_TYPE_BRANCH_ROUTER, 
								HiveAp.Device_TYPE_VPN_BR,
								notInList}));
			} else {
				intAuthApCount = QueryUtil.findRowCount(HiveAp.class, 
						new FilterParams("owner.id=:s1 and (deviceType=:s2 or deviceType=:s3 or deviceType=:s4) " +
								" and hiveApModel not in :s5 ",
								new Object[]{profile.getOwner().getId(), HiveAp.Device_TYPE_HIVEAP, 
								HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR, notInList}));
			}
			return String.valueOf(intAuthApCount);
		}
		
		private List<Idp> getRogueApInfo(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and mapId=:s4 and reportTime.time>=:s5 and reportTime.time<:s6",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   profile.getLocation().getId(),
									   starttimeLong,
									   endtimeLong});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and reportTime.time>=:s4 and reportTime.time<:s5",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   starttimeLong,
									   endtimeLong});
			}
			List<Idp> retIdpList = new ArrayList<Idp>();
			List<Idp> rogueList = QueryUtil.executeQuery(Idp.class, new SortParams("ssid,ifMacAddress,reportTime desc"),myFilterParams);
			if (rogueList!= null && rogueList.size()>0){
				String ssidname=null;
				String ifMacAddress = null;
				for(int i=0;i<rogueList.size();i++){
					Idp tempIdp = rogueList.get(i);
					if(i==0) {
						retIdpList.add(tempIdp);
						ssidname = tempIdp.getSsid();
						ifMacAddress = tempIdp.getIfMacAddress();
						continue;
					}
					if (tempIdp.getSsid().equals(ssidname) && tempIdp.getIfMacAddress().equals(ifMacAddress)){
					} else {
						retIdpList.add(tempIdp);
						ssidname = tempIdp.getSsid();
						ifMacAddress = tempIdp.getIfMacAddress();
					}
				}
			}
			return retIdpList;
		}
		
		private List<Idp> getRogueClientInfo(){
			FilterParams myFilterParams;
			if (profile.getLocation()!=null && profile.getLocation().getId()>-1){
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and mapId=:s4 and reportTime.time>=:s5 and reportTime.time<:s6",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   profile.getLocation().getId(),
									   starttimeLong,
									   endtimeLong});
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and owner.id = :s3 " +
						"and reportTime.time>=:s4 and reportTime.time<:s5",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   profile.getOwner().getId(),
									   starttimeLong,
									   endtimeLong});
			}
			List<Idp> retIdpList = new ArrayList<Idp>();
			List<Idp> rogueList = QueryUtil.executeQuery(Idp.class, new SortParams("ssid,ifMacAddress,reportTime desc"),myFilterParams);
			if (rogueList!= null && rogueList.size()>0){
				String ssidname=null;
				String ifMacAddress = null;
				for(int i=0;i<rogueList.size();i++){
					Idp tempIdp = rogueList.get(i);
					if(i==0) {
						retIdpList.add(tempIdp);
						ssidname = tempIdp.getSsid();
						ifMacAddress = tempIdp.getIfMacAddress();
						continue;
					}
					if (tempIdp.getSsid().equals(ssidname) && tempIdp.getIfMacAddress().equals(ifMacAddress)){
					} else {
						retIdpList.add(tempIdp);
						ssidname = tempIdp.getSsid();
						ifMacAddress = tempIdp.getIfMacAddress();
					}
				}
			}
			return retIdpList;
		}
	private JFreeChart getPieChartImage() {
    		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
           	DefaultPieDataset dataset = new DefaultPieDataset();
        	dataset.setValue("WPA/WPA2 (Personal)", wpaSsid.size());
        	dataset.setValue("Private PSK", pskSsid.size());
        	dataset.setValue("WPA/WPA2 802.1X (Enterprise)", authSsid.size());
        	dataset.setValue("WEP", wepSsid.size());
        	dataset.setValue("Open", openSsid.size());
        	return  ChartFactory.createPieChart(
        			"Station Access Security", dataset, true,
    				true, false);
    }
	
	
	public boolean generatePciReport() {
		Document document = new Document(PageSize.A4,40,40,72,72);
		try {
			
			File tmpFileDir = new File(currentDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
					currentDir + File.separator + pdfFileName));
			writer.setPageEvent(new HeaderFooterPage());
			
			HmStartConfig startConfig = profile.getOwner().getStartConfig();
			if (startConfig != null) {
				globalDevicePwd = startConfig.getHiveApPassword();
			}
			
			hiveApList = getAllHiveApList();
			setAuthFailureCount();
			getInformationOfHiveAp();
			boolean performed = checkPerformed();

			List<AhPCIData> pciAlertData = getAlertData();

			int rogueClientCount = getRogueClientCounts();
			int rogueApInNetCount = getRogueApCountInNet();
			
			int weakApCount=0;
			int weakApPasswordCount=0;
			int weakApServiceCount=0;
			int weakApPassStrenthCount=0;
			for(ComplianceResult result :hiveApCompliance){
				if (result.getPciSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_POOR){
					weakApCount++;
				}
				if (!result.getHiveAndApPassStrong()) {
					weakApPassStrenthCount++;
				}
				if (result.getPciWeakApPassSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_POOR){
					weakApPasswordCount++;
				}
				if (result.getPciWeakApServiceSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_POOR){
					weakApServiceCount++;
				}
			}
			
			document.open();

			String fontPath = FontFactory.TIMES_ROMAN;
			Font fontHeadTitle = FontFactory.getFont(fontPath, 18, Font.BOLD, new Color(9,35,95));
			Font fontTitle = FontFactory.getFont(fontPath, 16, Font.BOLD,new Color(9,35,95));
			Font fontSubTitle = FontFactory.getFont(fontPath, 14, Font.BOLD,new Color(40,70,140));
			Font fontSubBlackTitle = FontFactory.getFont(fontPath , 12, Font.BOLD);
			Font textFonts = FontFactory.getFont(fontPath, 11);
			Font textRedFonts = FontFactory.getFont(fontPath, 11,new Color(255,0,0));
			Font textGreenFonts = FontFactory.getFont(fontPath, 11,new Color(0,255,0));
			Font textItalicFonts = FontFactory.getFont(fontPath, 11, Font.ITALIC);
			Font commonFonts = FontFactory.getFont(fontPath, 9);
			Color tableTitleColor = new Color(190,200,150);
			
			float tablePadding=5;

			Paragraph graph = new Paragraph();
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			graph.setFont(fontHeadTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("PCI DSS 3.0 Wireless LAN Compliance Report \n\n");
			graph.setFont(fontSubTitle);
			graph.add("System Name:                  " + HmBeOsUtil.getHostName()+"\n");

			
			SimpleDateFormat sfReportTime = new SimpleDateFormat("yyyy/MM/dd HH:00:00");
			sfReportTime.setTimeZone(tz);
			String reportTimeString = sfReportTime.format(new Date(starttimeLong)) + " to " + sfReportTime.format(new Date(endtimeLong));
			graph.add("Reporting Time Period:  "+ reportTimeString + "\n");
			BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
			String hmVersion = versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion();
			graph.add("Version:                            " + NmsUtil.getOEMCustomer().getNmsName()+" " + hmVersion + "\n");
			document.add(graph);
			
			PdfContentByte cb = writer.getDirectContent();
			cb.setColorStroke(Color.BLACK);
			cb.setLineWidth(3.3f);
			cb.moveTo(document.left(), document.top()-100);
			cb.lineTo(document.right(), document.top()-100);
			cb.stroke();
			
			//Summary
			graph = new Paragraph();
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			graph.setFont(fontTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Summary\n\n");
			graph.setFont(textFonts);
			graph.add("Payment Card Industry Data Security Standard PCI DSS compliance is required for all merchants and service providers that store, process, or transmit payment cardholder data. To achieve compliance, merchants and service providers must adhere to the Payment Card Industry Data Security Standard. This standard offers a single approach to safeguarding sensitive data for all card brands.  This report provides summary information about the wireless network.  This report should not be taken as a representation of complete PCI compliance because PCI compliance applies to an entire system, not just the wireless component.\n");
			document.add(graph);
			
			//Relevant PCI DSS Requirements Compliance at a Glance
			graph = new Paragraph();
			graph.setSpacingAfter(10f);
			graph.setFont(fontTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Relevant PCI DSS Requirements Compliance at a Glance\n\n");
			document.add(graph);
			float[] widths = {0.10f, 0.3f, 0.6f};
			PdfPTable table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			PdfPCell cell = new PdfPCell(new Phrase("Section",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Compliance",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("PCI DSS Requirements",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("2.1.1",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (weakApPasswordCount>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Failed because one or more managed devices are still using the default hive password or the default admin password. Please change the default passwords on all managed devices and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: None of the managed " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s uses the default " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " password or the default admin password.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("For wireless environments connected to the cardholder data environment or transmitting cardholder data, change ALL wireless vendor defaults at installation, including but not limited to default wireless encryption keys, passwords, and SNMP community strings.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("2.2.2",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (weakApServiceCount>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Failed because Telnet or SNMP is enabled on one or more managed devices. Disable Telnet and SNMP on all managed devices and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: None of the managed " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s has Telnet or SNMP enabled.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Enable only necessary services, protocols, daemons, etc., as required for the function of the system.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("4.1.1",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (wepSsid.size()>0 || openSsid.size()>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Failed because one or more SSIDs are using WEP or OPEN authentication. Change the authentication method for those SSIDs and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: No SSIDs use WEP or OPEN authentication.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell();
			cell.addElement(new Phrase("Ensure wireless networks transmitting cardholder data or connected to the cardholder data environment, use industry best practices (for example, IEEE 802.11i) to implement strong encryption for authentication and transmission. (The use of WEP as a security control is prohibited.)",textFonts));
//			cell.addElement(new Phrase("For new wireless implementations, use of WEP is prohibited after March 31, 2009.\n",textItalicFonts));
//			cell.addElement(new Phrase("For current wireless implementations, use of WEP is prohibited after June 30, 2010.\n",textItalicFonts));
			cell.setPadding(3);
			table.addCell(cell);
			
			// new add for pci 3.0
			cell = new PdfPCell(new Phrase("6.5.10",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (weakApServiceCount>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Failed because Telnet or SNMP is enabled on one or more devices. Disable Telnet and SNMP on all devices and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: Devices support secure authentication access using https and ssh.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell();
			cell.addElement(new Phrase("Broken authentication and session management.\n",textFonts));
			cell.addElement(new Phrase("Secure authentication and session management prevents unauthorized individuals from compromising legitimate account credentials, keys, or session tokens that would otherwise enable the intruder to assume the identity of an authorized user.\n",textFonts));
			cell.addElement(new Phrase("Note: Requirement 6.5.10 is a best practice until June 30, 2015, after which it becomes a requirement.\n",textItalicFonts));
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("8.2.3",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (weakApPassStrenthCount>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Some " + NmsUtil.getOEMCustomer().getAccessPonitName().toLowerCase() + "s do not meet this password requirement. Please see Compliance details section for more details.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: All the " + NmsUtil.getOEMCustomer().getAccessPonitName().toLowerCase() + "s meet the password requirement.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell();
			cell.addElement(new Phrase("Passwords/phrases must meet the following:\n",textFonts));
			cell.addElement(new Phrase("\u2022 Require a minimum length of at least seven characters.\n",textFonts));
			cell.addElement(new Phrase("\u2022 Contain both numeric and alphabetic characters.\n",textFonts));
			cell.addElement(new Phrase("Alternatively, the passwords/phrases must have complexity and strength at least equivalent to the parameters specified above.",textFonts));
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("10.2.6",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell.addElement(new Phrase("Pass",textGreenFonts));
			cell.addElement(new Phrase("Reason: Logging is enabled on " + NmsUtil.getOEMCustomer().getCompanyName() + " " + NmsUtil.getOEMCustomer().getAccessPonitName().toLowerCase() + "s.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Initialization, stopping, or pausing of the audit logs.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("10.5.4",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (externalLogServerList.size()>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: At least one log server is on an external LAN.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: All log servers are on the internal LAN.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Write logs for external-facing technologies onto a log server on the internal LAN.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("11.1.1",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (rogueApInNetCount>0) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Fail",textRedFonts));
				cell.addElement(new Phrase("Reason: Failed because one or more rogue APs were detected on the same Ethernet backhaul network as the detecting device. Remove any rogue APs and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Pass",textGreenFonts));
				cell.addElement(new Phrase("Reason: No rogue APs were detected on the same Ethernet backhaul network as the detecting " + NmsUtil.getOEMCustomer().getAccessPonitName() + ".",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Maintain an inventory of authorized wireless access points including a documented business justification.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("12.10.5",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (!performed) {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Not Performed",textRedFonts));
				cell.addElement(new Phrase("Reason: The test was not performed because MAC DoS, IP DoS, or MAC filter intrusion detection is not configured. Configure one of these intrusion detection methods and try again.",textFonts));
			} else {
				cell = new PdfPCell();
				cell.addElement(new Phrase("Performed",textGreenFonts));
				cell.addElement(new Phrase("Reason: At least one of the following intrusion detection mechanisms is configured: MAC DoS, IP DoS, or MAC filter.",textFonts));
			}
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Include alerts from security monitoring systems, including but not limited to intrusion-detection, intrusion- prevention, firewalls, and file-integrity monitoring systems.",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);

			//Rogue Device Compliance
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Rogue Device Compliance\n");
			document.add(graph);
			widths = new float[]{0.4f, 0.1f, 0.4f,0.1f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Authorized APs",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(getAuthApCount(),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Rogue APs",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(getRogueApCount(),textRedFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Friendly APs*",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(getFriendlyApCount(),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Rogue Stations",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(rogueClientCount),textRedFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			document.add(new Phrase("* Friendly APs are access points that comply with IDS (intrusion detection system) policy but are not under " + NmsUtil.getOEMCustomer().getNmsName() + " management.",commonFonts));
			
			//Device Configuration Compliance
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Device Configuration Compliance\n");
			document.add(graph);
			widths = new float[]{0.4f, 0.1f, 0.4f,0.1f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Broadcasting SSIDs",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(broadcastSsid.size()),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("SSIDs with Weak Encryption",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(weakEncryptionSsid.size()),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getAccessPonitName() + "s with Weak Access Security",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(weakApCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(" ",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(" ",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			document.add(new Phrase("* The use of WEP authentication in existing wireless LANs will be considered noncompliant with PCI DSS by June 2010.",commonFonts));
			
			//Log Servers
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Log Servers\n");
			document.add(graph);
			widths = new float[]{0.4f, 0.1f, 0.4f,0.1f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Internal Log Servers",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(internalLogServerList.size()),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("External Log Servers",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(externalLogServerList.size()),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			
			//Intrusion Detection Compliance
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Intrusion Detection Compliance\n");
			document.add(graph);
			widths = new float[]{0.4f, 0.1f, 0.4f,0.1f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Authentication Failures",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(clientFaitureCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			table.addCell("");
			table.addCell("");
			cell = new PdfPCell(new Phrase("MAC Layer Denial of Service Attacks",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(macLayerAlertCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("IP Layer Denial of Service Attacks",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(ipLayerAlertCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			PdfTemplate tp = PdfTemplate.createTemplate(writer, document.right()-40, 20);
			tp.setColorStroke(Color.BLACK);
			tp.setLineWidth(3.3f);
			tp.moveTo(0,10);
			tp.lineTo(document.right(),10);
			tp.stroke();
			tp.sanityCheck();
			Image bbbb = Image.getInstance(tp);
    		document.add(bbbb);
    		
    		//Compliance Details
    		//Rogue Device Details
			graph = new Paragraph();
			graph.setSpacingBefore(15f);
			graph.setSpacingAfter(15f);
			graph.setFont(fontTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Compliance Details\n\n");
			graph.setFont(fontSubTitle);
			graph.add("Rogue Device Details\n");
			graph.setFont(textFonts);
			graph.add("This section details the specific threats associated with the rogue devices that have been detected in your wireless LAN. Rogue devices are defined as devices that are not authorized to operate in your wireless LAN. These devices can either be introduced by employees or could be placed by visitors or vendors with benign or malicious intent. This compliance is mandated by PCI DSS requirement 11.1 in reference [1].\n\n");
			graph.add("Rogue AP detection works as follows. A " + NmsUtil.getOEMCustomer().getAccessPonitName() + " radio in access mode periodically scans each channel, checking for beacon frames and probe responses from neighboring access points. Depending on the type of information it uncovers about detected access points, it can classify them as either compliant or noncompliant in regards to one of its wireless IDS (intrusion detection system) policy rules. Any access point that does not comply with all the rules in the policy is automatically classified as rogue. (Note that when both radios are in access mode, the " + NmsUtil.getOEMCustomer().getAccessPonitName() + " performs rogue detection in both the 2.4 GHz and 5 GHz radio bands.)\n\n");
			graph.add("To check if detected rogue APs are in the same backhaul network as the detecting " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s, the " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s send broadcast traffic from their backhaul interfaces (the wireless backhaul interface for mesh points, and the wireless backhaul interface and Ethernet ports for portals) and monitor the responses they receive on their wifi interfaces in access mode. They then compare the MAC address in the TA (transmitter address) field in the 802.11 data frame header with the MAC addresses of noncompliant APs. If they match, then the rogue AP is known to be in the same network.\n\n");
			graph.add("For the " + NmsUtil.getOEMCustomer().getAccessPonitName() + " to determine that a rogue AP is in the same subnet, the rogue AP must meet two criteria:");
			document.add(graph);
			com.lowagie.text.List firstList = new com.lowagie.text.List(false, 10);
			com.lowagie.text.List rogueList = new com.lowagie.text.List(true, 20);
			rogueList.add(new ListItem("The rogue AP must have at least one active client. Some access points-including " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s-only forward broadcast packets to interfaces that have clients connected to them; therefore, for in-network detection to be successful, the rogue AP must have one or more active clients.",textFonts));
			rogueList.add(new ListItem("The rogue AP must be using the same access radio channel as the detecting " + NmsUtil.getOEMCustomer().getAccessPonitName() + " so that the " + NmsUtil.getOEMCustomer().getAccessPonitName() + " can receive the broadcast detection packet on its wifi interface. With background scanning enabled, a " + NmsUtil.getOEMCustomer().getAccessPonitName() + " scans all channels, but it does not listen for broadcasts on those channels; it only listens for broadcast traffic on the access channels it is using.",textFonts));
			firstList.add(rogueList);
			document.add(firstList);
			widths = new float[]{0.4f, 0.1f, 0.4f,0.1f};
			table = new PdfPTable(widths);
			table.setSpacingBefore(10f);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Rogue APs Connected to Your Network",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(rogueApInNetCount),textRedFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Rogue Stations",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(rogueClientCount),textRedFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			
			//Rogue Access Point List
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Rogue Access Point List\n");
			document.add(graph);
			widths = new float[]{0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.25f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("SSID Name",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("BSSID",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Channel",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("RSSI",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Last Seen",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Reporting Devices",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			
			List<Idp> rogueApList = getRogueApInfo();
			if (rogueApList!=null && rogueApList.size()>0) {
				Font currentFont;
				for (Idp idp : rogueApList) {
					if (idp.getInNetworkFlag() == Idp.IDP_CONNECTION_IN_NET) {
						currentFont = textRedFonts;
					} else {
						currentFont = textFonts;
					}
					cell = new PdfPCell(new Phrase(idp.getSsid(), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(idp.getIfMacAddress(), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(idp.getChannelString(), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf(idp.getRssi()), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf(idp.getReportTimeString()), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					SimpleHiveAp tmpAp = CacheMgmt.getInstance().getSimpleHiveAp(idp.getReportNodeId());
					if (tmpAp == null) {
						cell = new PdfPCell(new Phrase(idp.getReportNodeId(), currentFont));
					} else {
						cell = new PdfPCell(new Phrase(tmpAp.getHostname(), currentFont));
					}
					cell = new PdfPCell(new Phrase(idp.getReportNodeId(), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			
			//Rogue Station List
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Rogue Station List\n");
			document.add(graph);
			widths = new float[]{0.2f, 0.15f, 0.15f, 0.25f, 0.25f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Station MAC",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Channel",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("RSSI",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Last Seen",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Reporting Devices",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			
			List<Idp> rogueClientList = getRogueClientInfo();
			if (rogueClientList!=null && rogueClientList.size()>0) {
				for (Idp idp : rogueClientList) {
					cell = new PdfPCell(new Phrase(idp.getIfMacAddress(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(idp.getChannelString(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf(idp.getRssi()), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf(idp.getReportTimeString()), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					SimpleHiveAp tmpAp = CacheMgmt.getInstance().getSimpleHiveAp(idp.getReportNodeId());
					if (tmpAp == null) {
						cell = new PdfPCell(new Phrase(idp.getReportNodeId(), textFonts));
					} else {
						cell = new PdfPCell(new Phrase(tmpAp.getHostname(), textFonts));
					}
					cell.setPadding(tablePadding);
					table.addCell(cell);
				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			
			//Device Configuration Compliance Details
			graph = new Paragraph();
			graph.setSpacingBefore(15f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Device Configuration Compliance Details\n\n");
			graph.setFont(textFonts);
			graph.add("This section details the specific threats associated with the misconfiguration of devices in your wireless LAN. Policies and procedures must be in place to ensure that the access points are configured properly, including mechanisms for authentication and encryption. Configuration management is a key part in managing your infrastructure. Misconfigurations are common due to admin error, forgotten temporary or debugging changes, flaws in firmware updates that might reset configurations. Also, for some non-commercial grade access points, the reset button on the device can reset the configuration to factory default settings. Any such changes can create vulnerabilities in your WLAN. Detecting device misconfigurations is mandated by PCI DSS requirements 2.1.1 and 4.1.1 in reference [1].\n");
			graph.add("This section also details the checks for services and protocols that are unnecessary and insecure in a wireless environment. Detecting these services and protocols is mandated by PCI DSS requirement 2.2.2 in reference [1]. \n");
			document.add(graph);
			JFreeChart chart = getPieChartImage();
			tp = PdfTemplate.createTemplate(writer, document.right()-40, 200);
    	//	Graphics2D g2d = tp.createGraphics(document.right()-40, 200);
			//PdfGraphics2D g2d = new PdfGraphics2D(tp, document.right()-40, 200, true);
			Graphics2D g2d = tp.createGraphicsShapes(document.right()-40, 200);
    		Rectangle2D r2d = new Rectangle2D.Double(0, 0, document.right()-40, 200);
    		PiePlot pieplot = (PiePlot)chart.getPlot();
    		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} : {2}"));
    		pieplot.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
    		pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} : {1}"));
    		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.BOTTOM);
    		chart.getLegend().setBorder(0,0,0,0);
    		chart.getLegend().setVerticalAlignment(org.jfree.ui.VerticalAlignment.TOP);
    		chart.draw(g2d, r2d);
    		g2d.dispose();
    		tp.sanityCheck();
    		bbbb = Image.getInstance(tp);
    		table = new PdfPTable(1);
    		table.setWidthPercentage(100);
    		cell = new PdfPCell();
    		cell.setImage(bbbb);
    		table.addCell(cell);
    		document.add(table);
    		
    		//Authorized Access Points List with Weak Station Access Security
    		graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Authorized Access Points List with Weak Station Access Security\n");
			document.add(graph);
			widths = new float[]{0.25f, 0.25f, 0.25f, 0.25f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase(MgrUtil.getUserMessage("report.reportList.apName"),textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("SSID",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Access Security",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Encryption Method",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (ssidWeakInfoList.size()>0) {
				for (PciWeakSsidInfo ssidWeakInfo : ssidWeakInfoList) {
					cell = new PdfPCell(new Phrase(ssidWeakInfo.getApName(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(ssidWeakInfo.getSsidName(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(ssidWeakInfo.getAccessSecurityStr(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(ssidWeakInfo.getEncryptionStr(), textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("* The use of WEP authentication in existing wireless LANs will be considered noncompliant with PCI DSS by June 2010.",commonFonts));

			//Authorized Access Points List with Weak AP Access Security
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Authorized Access Points List with Weak AP Access Security\n");
			document.add(graph);
			widths = new float[]{0.2f, 0.2f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase(MgrUtil.getUserMessage("report.reportList.apName"),textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			cell.setRowspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getWirelessUnitName() + " Security Password",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			cell.setRowspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getAccessPonitName() + " Login Password",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			cell.setRowspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getAccessPonitName()+ " Access",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setColspan(4);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("SSH",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Telnet",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Ping",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("SNMP",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			boolean addTableFlg = false;
			if (hiveApCompliance.size()>0){
				for (ComplianceResult compliance : hiveApCompliance) {
					if (compliance.getPciSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_POOR) {
						Font fontTmp = compliance.getPciWeakHiveApPassLevel()==CompliancePolicy.COMPLIANCE_POLICY_POOR ? textRedFonts : textFonts;
						Font fontTmpHive = compliance.getPciWeakHivePassLevel()==CompliancePolicy.COMPLIANCE_POLICY_POOR ? textRedFonts : textFonts;
						cell = new PdfPCell(new Phrase(compliance.getHiveApName(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getHivePassDefault(), fontTmpHive));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getHiveApPassDefault(), fontTmp));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getPciSsh(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getPciTelnet(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getPciPing(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getPciSnmp(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						addTableFlg = true;
					}
				}
			} 
			if (!addTableFlg) {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("* The use of default passwords is a violation of PCI DSS 2.1.1.",commonFonts));
		
			if (weakApPassStrenthCount>0) {
				widths = new float[]{0.2f, 0.15f, 0.15f, 0.14f, 0.18f, 0.18f};
				table = new PdfPTable(widths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell = new PdfPCell(new Phrase(MgrUtil.getUserMessage("report.reportList.apName"),textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getWirelessUnitName() + " Security Password",textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getAccessPonitName() + " Login Password",textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(NmsUtil.getOEMCustomer().getAccessPonitName()+ " Type",textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase("MAC Address",textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase("Serial Number",textFonts));
				cell.setBackgroundColor(tableTitleColor);
				cell.setPadding(tablePadding);
				table.addCell(cell);
				
				for (ComplianceResult compliance : hiveApCompliance) {
					if (!compliance.getHiveAndApPassStrong()) {
						Font fontTmp = compliance.isHiveApPassStrong() ? textFonts : textRedFonts;
						Font fontTmpHive = compliance.isHivePassStrong() ? textFonts : textRedFonts;
						cell = new PdfPCell(new Phrase(compliance.getHiveApName(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getHivePassStrongString(), fontTmpHive));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getHiveApPassStrongString(), fontTmp));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getApType(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getApMac(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(compliance.getApSn(), textFonts));
						cell.setPadding(tablePadding);
						table.addCell(cell);
					}
				}
				document.add(table);
				document.add(new Phrase("* The use of passwords is a violation of PCI DSS 8.2.3.",commonFonts));
			}
			
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setFont(textFonts);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Beyond checking passwords for PCI DSS compliance, " + NmsUtil.getOEMCustomer().getCompanyName() + " rates password strength by a scoring them against a number of parameters. Passwords are examined for length, a mix of uppercase and lowercase letters, and a combination of letters, numbers, and special characters. Those that are longer and are more varied in character type are rated as stronger than those that are shorter and consist solely or primarily of a single type of character.");
			document.add(graph);
			
			//Log Server Compliance Details
			graph = new Paragraph();
			graph.setSpacingBefore(15f);
			graph.setSpacingAfter(15f);
			graph.setFont(fontSubTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Log Server Compliance Details\n\n");
			graph.setFont(textFonts);
			graph.add("This section details the specific compliance associated with " + NmsUtil.getOEMCustomer().getAccessPonitName() + " log entries written to an internal log server. Currently, a log server is considered internal if it is within the same firewall as the " + NmsUtil.getOEMCustomer().getAccessPonitName() + " that sends it log data. The log servers that are outside the firewall have to be specifically configured in the security compliance policy to be recognized as secure. The distinction between an internal and external log server is made in the logging configuration on the " + NmsUtil.getOEMCustomer().getAccessPonitName() + ". Internal log server detection is mandated by PCI DSS requirement 10.5.4 in reference [1].");
			document.add(graph);
			
			//List of Internal Log Servers
			graph = new Paragraph();
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("List of Internal Log Servers\n");
			document.add(graph);
			widths = new float[]{0.5f, 0.5f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Log Server Name/Server IP",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Network Policy Used",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (internalLogServerList.size()>0){
//				for(int i=0; i<internalLogServerList.size(); i++){
					for(String keyset:internalLogServerList.keySet()){
						Set<String> server = internalLogServerList.get(keyset);
						for(String wlan :server) {
							cell = new PdfPCell(new Phrase(keyset,textFonts));
							cell.setPadding(tablePadding);
							table.addCell(cell);
							cell = new PdfPCell(new Phrase(wlan,textFonts));
							cell.setPadding(tablePadding);
							table.addCell(cell);
						}
					}
//				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			
			//List of External Log Servers
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("List of External Log Servers\n");
			document.add(graph);
			widths = new float[]{0.5f, 0.5f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Log Server Name/Server IP",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Network Policy Used",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (externalLogServerList.size()>0){
//				for(int i=0; i<externalLogServerList.size(); i++){
					for(String keyset:externalLogServerList.keySet()){
						Set<String> server = externalLogServerList.get(keyset);
						for(String wlan :server) {
							cell = new PdfPCell(new Phrase(keyset,textFonts));
							cell.setPadding(tablePadding);
							table.addCell(cell);
							cell = new PdfPCell(new Phrase(wlan,textFonts));
							cell.setPadding(tablePadding);
							table.addCell(cell);
						}
					}
//				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			
			//Intrusion Alert Compliance Details
			graph = new Paragraph();
			graph.setSpacingBefore(15f);
			graph.setSpacingAfter(15f);
			graph.setFont(fontSubTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Intrusion Alert Compliance Details\n\n");
			graph.setFont(textFonts);
			graph.add("This section details the specific threats associated with intrusions into your wireless LAN. These include active penetration attempts such as failed authentications, associations, or EAP handshakes; various types of protocol frame floods such as probe requests, probe responses, and authentication requests, which can potentially use up channel bandwidth as well as overload the access points, causing them to reset or lock up; and denial of service attacks through deauthentication and disassociation attacks. Wireless LAN intrusion detection is mandated by PCI DSS requirement 12.9.5 in reference [1].");
			document.add(graph);
			widths = new float[]{0.3f, 0.2f, 0.3f, 0.2f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Authentication Failures",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(clientFaitureCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("MAC Layer Denial of Service Attacks",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(macLayerAlertCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("IP Layer Denial of Service Attacks",textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(String.valueOf(ipLayerAlertCount),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			document.add(table);
			
			//Intrusion Alert Details
			graph = new Paragraph();
			graph.setSpacingBefore(10f);
			graph.setSpacingAfter(10f);
			graph.setFont(fontSubBlackTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Intrusion Alert Details\n\n");
			graph.setFont(textFonts);
			graph.add(NmsUtil.getOEMCustomer().getAccessPonitName() + "s detected the following threats to wireless LAN security through the enforcement of IP and MAC firewall policies.");
			document.add(graph);
			widths = new float[]{0.15f, 0.15f, 0.15f, 0.15f, 0.1f, 0.15f,0.15f};
			table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell(new Phrase("Reporting " + NmsUtil.getOEMCustomer().getAccessPonitName(),textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Alert",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("First Occurrence",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Last Occurrence",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Count",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Source",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Location",textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			if (pciAlertData!=null && pciAlertData.size()>0){
				Map<String,String> mapKeyList = new HashMap<String,String>();
				List<?> mapNameSet = QueryUtil.executeQuery("select mapName, id from " +
						MapContainerNode.class.getSimpleName(), null, null, profile.getOwner().getId());
				if (mapNameSet!=null && !mapNameSet.isEmpty()){
					for(Object obj:mapNameSet){
						Object[] mapObj = (Object[])obj;
						mapKeyList.put(mapObj[1].toString(), mapObj[0].toString());
					}
				}
				
				AhPCIData beginTmpPckData = null;
				for(int i=0; i<pciAlertData.size(); i++){
					AhPCIData tmpClass = pciAlertData.get(i);
					if (beginTmpPckData==null) {
						beginTmpPckData=tmpClass;
						beginTmpPckData.setEndReportTime(tmpClass.getReportTime());
						continue;
					}
					
					if (beginTmpPckData.getNodeID().equals(tmpClass.getNodeID()) && 
							beginTmpPckData.getAlertCode()==tmpClass.getAlertCode()){
						beginTmpPckData.addViolationCounter(tmpClass.getViolationCounter());
						beginTmpPckData.setEndReportTime(tmpClass.getReportTime());
						beginTmpPckData.setSrcObject(tmpClass.getSrcObject());
						beginTmpPckData.setReportSystem(tmpClass.getReportSystem());
						if (i<pciAlertData.size()-1){
							continue;
						}
					}
					
					cell = new PdfPCell(new Phrase(beginTmpPckData.getApName(),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(beginTmpPckData.getAlertCodeString(),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(beginTmpPckData.getReportTimeString(),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(beginTmpPckData.getEndReportTimeString(),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(String.valueOf(beginTmpPckData.getViolationCounter()),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(beginTmpPckData.getSrcObject(),textFonts));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					if (mapKeyList.get(beginTmpPckData.getMapID().toString())!=null){
						cell = new PdfPCell(new Phrase(mapKeyList.get(beginTmpPckData.getMapID().toString()),textFonts));
					} else {
						cell = new PdfPCell(new Phrase("Unknown",textFonts));
					}
					cell.setPadding(tablePadding);
					table.addCell(cell);
					
					beginTmpPckData = tmpClass;
					beginTmpPckData.setEndReportTime(tmpClass.getReportTime());
				}
			} else {
				cell = new PdfPCell(new Phrase(" ",textFonts));
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
				table.addCell(cell);
			}
			document.add(table);
			
			//References
			graph = new Paragraph();
			graph.setSpacingBefore(15f);
			graph.setFont(fontTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("References\n\n");
			graph.setFont(textFonts);
			graph.add("[1] PCI DSS, Requirements and Security Assessment Procedures, Version 3.0, January 2014");
			document.add(graph);
			
		} catch(Exception ioe) {
            ioe.printStackTrace();
            log.error(ioe);
            return false;
        } finally {
        	try {
        		if (document != null && document.isOpen()) {
        			document.close();
        		}
        	} catch (Exception e) {
				log.error(e);
				return false;
			}
        }
		return true;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AhReport) {
			AhReport ahReport = (AhReport) bo;
			if (ahReport.getOwner() != null) {
				ahReport.getOwner().getId();
				ahReport.getOwner().getDomainName();
			}
		} else if (bo instanceof SsidProfile) {
			SsidProfile lazySsid = (SsidProfile) bo;
			if (lazySsid.getMacFilters() != null) {
				lazySsid.getMacFilters().size();
			}
			if (lazySsid.getStationDos()!=null) {
				if (lazySsid.getStationDos().getDosParamsMap()!=null) {
					lazySsid.getStationDos().getDosParamsMap().size();
				}
			}
			if (lazySsid.getSsidDos()!=null) {
				if (lazySsid.getSsidDos().getDosParamsMap()!=null) {
					lazySsid.getSsidDos().getDosParamsMap().size();
				}
			}
			if (lazySsid.getIpDos()!=null) {
				if (lazySsid.getIpDos().getDosParamsMap()!=null) {
					lazySsid.getIpDos().getDosParamsMap().size();
				}
			}
		} else if (bo instanceof HiveAp) {
			HiveAp hiveap = (HiveAp) bo;
			if (hiveap.getConfigTemplate() != null) {
				hiveap.getConfigTemplate().getId();
				if (hiveap.getConfigTemplate().getMgmtServiceSyslog()!=null) {
					hiveap.getConfigTemplate().getMgmtServiceSyslog().getId();
					if (hiveap.getConfigTemplate().getMgmtServiceSyslog().getSyslogInfo()!=null){
						hiveap.getConfigTemplate().getMgmtServiceSyslog().getSyslogInfo().size();
					}
				}
				
				if (hiveap.getConfigTemplate().getMgmtServiceSnmp()!=null) {
					hiveap.getConfigTemplate().getMgmtServiceSnmp().getId();
				}
				if (hiveap.getConfigTemplate().getSsidInterfaces()!=null) {
					hiveap.getConfigTemplate().getSsidInterfaces().size();
				}
				hiveap.getConfigTemplate().getHiveProfile().getId();
				
				if (hiveap.getConfigTemplate().getHiveProfile().getMacFilters()!=null){
					hiveap.getConfigTemplate().getHiveProfile().getMacFilters().size();
				}
				if (hiveap.getConfigTemplate().getHiveProfile().getHiveDos()!=null){
					if (hiveap.getConfigTemplate().getHiveProfile().getHiveDos().getDosParamsMap()!=null) {
						hiveap.getConfigTemplate().getHiveProfile().getHiveDos().getDosParamsMap().size();
					}
				}
				if (hiveap.getConfigTemplate().getHiveProfile().getStationDos()!=null){
					if (hiveap.getConfigTemplate().getHiveProfile().getStationDos().getDosParamsMap()!=null) {
						hiveap.getConfigTemplate().getHiveProfile().getStationDos().getDosParamsMap().size();
					}
				}
				
				if (hiveap.getConfigTemplate().getDeviceServiceFilter()!=null) {
					hiveap.getConfigTemplate().getDeviceServiceFilter().getId();
				}
				
				if (hiveap.getConfigTemplate().getEth0ServiceFilter()!=null) {
					hiveap.getConfigTemplate().getEth0ServiceFilter().getId();
				}
				if (hiveap.getConfigTemplate().getEth1ServiceFilter()!=null) {
					hiveap.getConfigTemplate().getEth1ServiceFilter().getId();
				}
				
				if (hiveap.getConfigTemplate().getRed0ServiceFilter()!=null) {
					hiveap.getConfigTemplate().getRed0ServiceFilter().getId();
				}
				if (hiveap.getConfigTemplate().getAgg0ServiceFilter()!=null) {
					hiveap.getConfigTemplate().getAgg0ServiceFilter().getId();
				}
				
				if (hiveap.getConfigTemplate().getEth0BackServiceFilter()!=null) {
					hiveap.getConfigTemplate().getEth0BackServiceFilter().getId();
				}
				if (hiveap.getConfigTemplate().getEth1BackServiceFilter()!=null) {
					hiveap.getConfigTemplate().getEth1BackServiceFilter().getId();
				}
				
				if (hiveap.getConfigTemplate().getRed0BackServiceFilter()!=null) {
					hiveap.getConfigTemplate().getRed0BackServiceFilter().getId();
				}
				if (hiveap.getConfigTemplate().getAgg0BackServiceFilter()!=null) {
					hiveap.getConfigTemplate().getAgg0BackServiceFilter().getId();
				}
				if (hiveap.getConfigTemplate().getWireServiceFilter()!=null) {
					hiveap.getConfigTemplate().getWireServiceFilter().getId();
				}
			}
		}
		return null;
	}
}
