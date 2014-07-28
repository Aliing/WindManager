package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.admin.restoredb.AhRestoreCommons;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.HmSystemInfoUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhSLAStats;
import com.ah.bo.performance.AhSummaryPage;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.LinuxSystemInfoCollector;
import com.ah.util.LongItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class ReportsAction extends BaseAction {
	
	private static final Tracer log = new Tracer(ReportsAction.class.getSimpleName());
	
	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		tz = getUserTimeZone();
		domainId = QueryUtil.getDomainFilter(userContext);

		try {
			if ("saveCookie".equals(operation)) {
				if (!getInPassiveNode()){
					if (getUserContext().getSwitchDomain()==null || getIsInHomeDomain()) {
						AhSummaryPage sumBo=QueryUtil.findBoByAttribute(AhSummaryPage.class, 
								"userName", getUserContext().getUserName(),
								getDomain().getId());
						if (sumBo==null) {
							AhSummaryPage summaryPage = new AhSummaryPage();
							summaryPage.setOwner(getDomain());
							summaryPage.setAttribute(cookieString);
							setClosePanelWidget(summaryPage);
							summaryPage.setUserName(getUserContext().getUserName());
							QueryUtil.createBo(summaryPage);
						} else {
							sumBo.setAttribute(cookieString);
							setClosePanelWidget(sumBo);
							QueryUtil.updateBo(sumBo);
						}
					}
				}
				return "json";
			} else if ("saveConfig".equals(operation)){
				if (getUserContext().getSwitchDomain()==null || getIsInHomeDomain()) {
					AhSummaryPage sumBo=QueryUtil.findBoByAttribute(AhSummaryPage.class, 
							"userName", getUserContext().getUserName(),
							getDomain().getId());
					if (sumBo==null) {
						AhSummaryPage summaryPage = new AhSummaryPage();
						summaryPage.setOwner(getDomain());
						summaryPage.setUserName(getUserContext().getUserName());
						
						summaryPage.setCkwidgetAPhealth(ckwidgetAPhealth);
						summaryPage.setCkwidgetAPmostClientCount(ckwidgetAPmostClientCount);
						summaryPage.setCkwidgetAPmostBandwidth(ckwidgetAPmostBandwidth);
						summaryPage.setCkwidgetAPmostInterference(ckwidgetAPmostInterference);
						summaryPage.setCkwidgetAPmostCrcError(ckwidgetAPmostCrcError);
						summaryPage.setCkwidgetAPmostRxRetry(ckwidgetAPmostTxRetry);
						summaryPage.setCkwidgetAPmostRxRetry(ckwidgetAPmostRxRetry);
						
						summaryPage.setCkwidgetAPsecurity(ckwidgetAPsecurity);
						summaryPage.setCkwidgetAPcompliance(ckwidgetAPcompliance);
						summaryPage.setCkwidgetAPalarm(ckwidgetAPalarm);
						summaryPage.setCkwidgetAPbandwidth(ckwidgetAPbandwidth);
						summaryPage.setCkwidgetAPsla(ckwidgetAPsla);
						
						summaryPage.setCkwidgetAPversion(ckwidgetAPversion);
						summaryPage.setCkwidgetAuditLog(ckwidgetAuditLog);
						summaryPage.setCkwidgetAPuptime(ckwidgetAPuptime);
						summaryPage.setCkwidgetActiveUser(ckwidgetActiveUser);
						
						summaryPage.setCkwidgetCinfo(ckwidgetCinfo);
						summaryPage.setCkwidgetCmostFailure(ckwidgetCmostFailure);
						summaryPage.setCkwidgetCmostTxAirtime(ckwidgetCmostTxAirtime);
						summaryPage.setCkwidgetCmostRxAirtime(ckwidgetCmostRxAirtime);
						
						summaryPage.setCkwidgetCvendor(ckwidgetCvendor);
						summaryPage.setCkwidgetCradio(ckwidgetCradio);
						summaryPage.setCkwidgetCuserprofile(ckwidgetCuserprofile);
						summaryPage.setCkwidgetCsla(ckwidgetCsla);
	
						summaryPage.setCkwidgetScpu(ckwidgetScpu);
						summaryPage.setCkwidgetSinfo(ckwidgetSinfo);
						summaryPage.setCkwidgetSperformanceInfo(ckwidgetSperformanceInfo);
						summaryPage.setCkwidgetSuser(ckwidgetSuser);
						
						QueryUtil.createBo(summaryPage);
					} else {
						sumBo.setCkwidgetAPhealth(ckwidgetAPhealth);
						
						sumBo.setCkwidgetAPmostClientCount(ckwidgetAPmostClientCount);
						sumBo.setCkwidgetAPmostBandwidth(ckwidgetAPmostBandwidth);
						sumBo.setCkwidgetAPmostInterference(ckwidgetAPmostInterference);
						sumBo.setCkwidgetAPmostCrcError(ckwidgetAPmostCrcError);
						sumBo.setCkwidgetAPmostRxRetry(ckwidgetAPmostTxRetry);
						sumBo.setCkwidgetAPmostRxRetry(ckwidgetAPmostRxRetry);
						
						sumBo.setCkwidgetAPsecurity(ckwidgetAPsecurity);
						sumBo.setCkwidgetAPcompliance(ckwidgetAPcompliance);
						sumBo.setCkwidgetAPalarm(ckwidgetAPalarm);
						sumBo.setCkwidgetAPbandwidth(ckwidgetAPbandwidth);
						sumBo.setCkwidgetAPsla(ckwidgetAPsla);
						
						sumBo.setCkwidgetAPversion(ckwidgetAPversion);
						sumBo.setCkwidgetAuditLog(ckwidgetAuditLog);
						sumBo.setCkwidgetAPuptime(ckwidgetAPuptime);
						sumBo.setCkwidgetActiveUser(ckwidgetActiveUser);
						
						sumBo.setCkwidgetCinfo(ckwidgetCinfo);
						sumBo.setCkwidgetCmostFailure(ckwidgetCmostFailure);
						sumBo.setCkwidgetCmostTxAirtime(ckwidgetCmostTxAirtime);
						sumBo.setCkwidgetCmostRxAirtime(ckwidgetCmostRxAirtime);
						sumBo.setCkwidgetCvendor(ckwidgetCvendor);
						sumBo.setCkwidgetCradio(ckwidgetCradio);
						sumBo.setCkwidgetCuserprofile(ckwidgetCuserprofile);
						sumBo.setCkwidgetCsla(ckwidgetCsla);
						
						sumBo.setCkwidgetScpu(ckwidgetScpu);
						sumBo.setCkwidgetSinfo(ckwidgetSinfo);
						sumBo.setCkwidgetSperformanceInfo(ckwidgetSperformanceInfo);
						sumBo.setCkwidgetSuser(ckwidgetSuser);
						QueryUtil.updateBo(sumBo);
						initCookieString=sumBo.getAttribute();
					}
				}
				if (ckwidgetAPcompliance){
					poorAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapPoorAps(domainId==null?-1:domainId);
					goodAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapGoodAps(domainId==null?-1:domainId);
					excellentAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapExcellentAps(domainId==null?-1:domainId);
					if (poorAp==0 && goodAp==0 && excellentAp==0){
						new Thread() {
							@Override
							public void run() {
								AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getValueCompliance(domainId==null?-1:domainId);
							}
						}.start();
					}
				}
				prepareWidgetInfo();
				return "summary";
			} else if ("summaryWidgetRadioData".equals(operation)){
				prepareClientRadioMode();
				return "summaryWidgetRadioData";
			} else if ("summaryWidgetAlarmData".equals(operation)){
				prepareHiveApAlarm();
				return "summaryWidgetAlarmData";
			} else if ("summaryWidgetComplianceData".equals(operation)){
				prepareHiveApCompliance();
				return "summaryWidgetComplianceData";
			} else if ("summaryWidgetBandwidthData".equals(operation)){
				prepareHiveApNewWorkBandwidth();
				return "summaryWidgetBandwidthData";
			} else if ("summaryWidgetUserprofileData".equals(operation)){
				prepareClientUserProfile();
				return "summaryWidgetUserprofileData";
			} else if ("summaryWidgetVendorData".equals(operation)){
				prepareClientVendor();
				return "summaryWidgetVendorData";
			} else if ("summaryWidgetApSlaData".equals(operation)){
				List<HMServicesSettings> hmSetting = QueryUtil.executeQuery(HMServicesSettings.class,null,null, getDomain().getId());
				if (hmSetting!=null && !hmSetting.isEmpty()) {
					apSlaType = hmSetting.get(0).getApSlaType();
				}
				prepareHiveApSLA();
				return "summaryWidgetApSlaData";
			} else if ("summaryWidgetClientSlaData".equals(operation)){
				List<HMServicesSettings> hmSetting = QueryUtil.executeQuery(HMServicesSettings.class,null,null, getDomain().getId());
				if (hmSetting!=null && !hmSetting.isEmpty()) {
					clientSlaType = hmSetting.get(0).getClientSlaType();
				}
				prepareClientSLA();
				return "summaryWidgetClientSlaData";
			} else if ("summaryWidgetSystemCpuData".equals(operation)){
				prepareSystemCpu();
				return "summaryWidgetSystemCpuData";
			} else if ("summaryWidgetSystemUserData".equals(operation)){
				prepareSystemUser();
				return "summaryWidgetSystemUserData";
			} else if ("summaryWidgetSystemPerformanceData".equals(operation)){
				prepareSystemPerformance();
				return "summaryWidgetSystemPerformanceData";
			} else if ("summaryWidgetApVersionData".equals(operation)){
				prepareHiveAPVersion();
				return "summaryWidgetApVersionData";
			} else if ("summaryWidgetApUptimeData".equals(operation)){
				prepareHiveAPUpTime();
				return "summaryWidgetApUptimeData";
			} else if ("summaryWidgetActiveUserData".equals(operation)){
				prepareActiveUser();
				return "summaryWidgetActiveUserData";
			}  else if ("viewUpgradeLog".equals(operation)) {
				ignoreUpgradeLog();
				return "json";
			}  else if ("remindUpgradeLog".equals(operation)) {
				return "json";
			}  else if ("ignoreUpgradeLog".equals(operation)) {
				ignoreUpgradeLog();
				return "json";
			} else if ("removeSession".equals(operation)) {
				removeActiveSession();
				prepareSystemUser();
				
				if (this.isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("rs", true);
					try {
						BaseAction.getSessionUserContext();
					} catch (Exception e) {
						jsonObject.put("needLogin", true);
						return "json";
					}
					return "json";
				} else {
					return "summaryWidgetSystemUserData";
				}
			} else if ("saveApSlaFilter".equals(operation)) {
				if (getUserContext().getSwitchDomain()==null || getIsInHomeDomain()) {
					try {
						List<HMServicesSettings> hmSetting = QueryUtil.executeQuery(HMServicesSettings.class,null,null, getDomain().getId());
						if (hmSetting!=null && !hmSetting.isEmpty()) {
							HMServicesSettings oneBo = hmSetting.get(0);
							oneBo.setApSlaType(apSlaType);
							QueryUtil.updateBo(oneBo);
							prepareHiveApSLA();
						}
					} catch (Exception e) {
						log.error("saveApSlaFilter",e);
						prepareHiveApSLA();
					}
				}
				return "summaryWidgetApSlaData";
			} else if ("saveClientSlaFilter".equals(operation)) {
				if (getUserContext().getSwitchDomain()==null || getIsInHomeDomain()) {
					try {
						List<HMServicesSettings> hmSetting = QueryUtil.executeQuery(HMServicesSettings.class,null,null, getDomain().getId());
						if (hmSetting!=null && !hmSetting.isEmpty()) {
							HMServicesSettings oneBo = hmSetting.get(0);
							oneBo.setClientSlaType(clientSlaType);
							QueryUtil.updateBo(oneBo);
							prepareClientSLA();
							
						}
					} catch (Exception e) {
						log.error("saveClientSlaFilter",e);
						prepareClientSLA();
					}
				}
				return "summaryWidgetClientSlaData";
			} else {
				if (getInPassiveNode()){
					ckwidgetSinfo=true;
					ckwidgetAPhealth = false;
					ckwidgetAPmostClientCount=false;
					ckwidgetAPmostBandwidth=false;
					ckwidgetAPmostInterference=false;
					ckwidgetAPmostCrcError=false;
					ckwidgetAPmostTxRetry=false;
					ckwidgetAPmostRxRetry=false;
					ckwidgetAPsecurity =false;
					ckwidgetAPcompliance =false;
					ckwidgetAPalarm =false;
					ckwidgetAPbandwidth =false;
					ckwidgetAPsla =false;
					ckwidgetAPuptime = false;
					ckwidgetActiveUser = false;
					ckwidgetAPversion=false;
					ckwidgetAuditLog= false;
					ckwidgetCinfo =false;
					ckwidgetCmostFailure=false;
					ckwidgetCmostTxAirtime=false;
					ckwidgetCmostRxAirtime=false;
					ckwidgetCvendor =false;
					ckwidgetCradio =false;
					ckwidgetCuserprofile =false;
					ckwidgetCsla =false;
					ckwidgetScpu = false;
					ckwidgetSperformanceInfo =false;
					ckwidgetSuser =false;
					
				} else {
					if (getUserContext().getSwitchDomain()==null || getIsInHomeDomain()) {
						AhSummaryPage sumBo=QueryUtil.findBoByAttribute(AhSummaryPage.class, 
								"userName", getUserContext().getUserName(),
								getDomain().getId());
						if (sumBo!=null) {
							initCookieString = sumBo.getAttribute();
		
							ckwidgetAPhealth = sumBo.getCkwidgetAPhealth();
	//						ckwidgetAPtop10 = sumBo.getCkwidgetAPtop10();
							
							ckwidgetAPmostClientCount=sumBo.getCkwidgetAPmostClientCount();
							ckwidgetAPmostBandwidth=sumBo.getCkwidgetAPmostBandwidth();
							ckwidgetAPmostInterference=sumBo.getCkwidgetAPmostInterference();
							ckwidgetAPmostCrcError=sumBo.getCkwidgetAPmostCrcError();
							ckwidgetAPmostTxRetry=sumBo.getCkwidgetAPmostRxRetry();
							ckwidgetAPmostRxRetry=sumBo.getCkwidgetAPmostRxRetry();
							
							ckwidgetAPsecurity =sumBo.getCkwidgetAPsecurity();
							ckwidgetAPcompliance =sumBo.getCkwidgetAPcompliance();
							ckwidgetAPalarm =sumBo.getCkwidgetAPalarm();
							ckwidgetAPbandwidth =sumBo.getCkwidgetAPbandwidth();
							ckwidgetAPsla =sumBo.getCkwidgetAPsla();
							
							ckwidgetAPuptime = sumBo.getCkwidgetAPuptime();
							ckwidgetActiveUser = sumBo.getCkwidgetActiveUser();
							
							ckwidgetAPversion=sumBo.getCkwidgetAPversion();
							ckwidgetAuditLog= sumBo.getCkwidgetAuditLog();
							ckwidgetCinfo =sumBo.getCkwidgetCinfo();
							
							ckwidgetCmostFailure=sumBo.getCkwidgetCmostFailure();
							ckwidgetCmostTxAirtime=sumBo.getCkwidgetCmostTxAirtime();
							ckwidgetCmostRxAirtime=sumBo.getCkwidgetCmostRxAirtime();
							
							ckwidgetCvendor =sumBo.getCkwidgetCvendor();
							ckwidgetCradio =sumBo.getCkwidgetCradio();
							ckwidgetCuserprofile =sumBo.getCkwidgetCuserprofile();
							ckwidgetCsla =sumBo.getCkwidgetCsla();
							
							ckwidgetScpu = sumBo.getCkwidgetScpu();
							ckwidgetSinfo =sumBo.getCkwidgetSinfo();
							ckwidgetSperformanceInfo =sumBo.getCkwidgetSperformanceInfo();
							ckwidgetSuser =sumBo.getCkwidgetSuser();
						}
					}
				}
				prepareWidgetInfo();
				return "summary";
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.info("excute",e);
			return "summary";
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SUMMARY);
	}
	
	public boolean getShowUpgradeLog() {
		return NmsUtil.isShowUpdateLog(getDomain());
	}
	
	public String getDefaultLogAction() {
		return "view";
	}
		
	private void ignoreUpgradeLog() {
		NmsUtil.clearShowUpdateLogFlag(getDomain());
		generateAuditLog(HmAuditLog.STATUS_SUCCESS, 
				getText("report.summary.upgradeLog.confirm"));
	}
	
	public boolean getBooleanSuperUser(){
		return userContext.isSuperUser() && getIsInHomeDomain();
	}
	
	public boolean getBooleanOnline(){
		return isHMOnline();
	}
	
	private final String  width = "100%";
	private final String  bgcolor="ffffff";
	
	// for radio swf
	private String wf_radio, wf_radio_h;
	// for alarm swf
	private String wf_alarm, wf_alarm_h;
	// for compliance swf
	private String wf_compliance, wf_compliance_h;
	// for bandwidth swf
	private String wf_bandwidth, wf_bandwidth_h;
	// for client vendor swf
	private String wf_vendor, wf_vendor_h;
	// for client user profile swf
	private String wf_userprofile, wf_userprofile_h;
	
	private String wf_apsla, wf_apsla_h;
	
	private String wf_clientsla, wf_clientsla_h;
	
	private String wf_systemuser, wf_systemuser_h;
	private String wf_systemcpu, wf_systemcpu_h;
//	private String wf_systemmemory, wf_systemmemory_h;
	private String wf_systemperfomance, wf_systemperfomance_h;
	
	private String wf_apversion, wf_apversion_h;
	private String wf_apuptime, wf_apuptime_h;
	private String wf_activeuser, wf_activeuser_h;

	public void prepareWidgetInfo(){
		if (ckwidgetAPhealth) {
			prepareHiveApHealth();
		}
		
		if (ckwidgetAPmostClientCount||
			ckwidgetAPmostBandwidth||
			ckwidgetAPmostInterference||
			ckwidgetAPmostCrcError||
			ckwidgetAPmostTxRetry||
			ckwidgetAPmostRxRetry){
			prepareHiveApTop10();
		}
		if (ckwidgetAPsecurity){
			prepareHiveApNewWorkSecurity();
		}
		if (ckwidgetAPcompliance){
			wf_compliance = "summaryWidgetCompliance";
			wf_compliance_h = "150";
		}
		if(ckwidgetAPalarm){
			wf_alarm = "summaryWidgetAlarm";
			wf_alarm_h = "150";
		}
		
		if(ckwidgetAPversion){
			wf_apversion = "summaryWidgetAPversion";
			wf_apversion_h = "150";
		}
		if(ckwidgetAPbandwidth){
			wf_bandwidth = "summaryWidgetBandwidth";
			wf_bandwidth_h = "180";
		}
		if (ckwidgetAPsla){
			wf_apsla = "summaryWidgetApSla";
			wf_apsla_h = "200";
		}
		if (ckwidgetCinfo){
			prepareClientInfo();
		}
		if (ckwidgetCmostFailure || ckwidgetCmostTxAirtime || ckwidgetCmostRxAirtime){
			prepareClientTop10();
		}
		if (ckwidgetCradio){
			wf_radio = "summaryWidgetRadio";
			wf_radio_h = "150";
		}
		if (ckwidgetCvendor){
			wf_vendor = "summaryWidgetVendor";
			wf_vendor_h = "300";
		}
		
		if (ckwidgetCuserprofile){
			wf_userprofile = "summaryWidgetUserprofile";
			wf_userprofile_h = "200";
		}
		if (ckwidgetCsla){
			wf_clientsla = "summaryWidgetClientSla";
			wf_clientsla_h = "200";
		}
		
		if (ckwidgetSinfo){
			prepareSystemInfo();
		}
		if (ckwidgetScpu){
			wf_systemcpu = "summaryWidgetSystemCpu";
			wf_systemcpu_h = "280";
		}
//		if (ckwidgetSmemory){
//			wf_systemcpu = "summaryWidgetSystemMemory";
//			wf_systemcpu_h = "150";
//		}
		if (ckwidgetSuser){
			wf_systemuser = "summaryWidgetSystemUser";
			wf_systemuser_h = "250";
		}
		if (ckwidgetSperformanceInfo){
			wf_systemperfomance = "summaryWidgetSystemPerformance";
			wf_systemperfomance_h = "240";
		}
		
		if (ckwidgetAuditLog){
			prepareAuditLogInfo();
		}
		
		if (ckwidgetAPuptime){
			wf_apuptime = "summaryWidgetApUptime";
			wf_apuptime_h = "150";
		}
		if (ckwidgetActiveUser){
			wf_activeuser = "summaryWidgetActiveUser";
			wf_activeuser_h = "150";
		}

	}
	private void setClosePanelWidget(AhSummaryPage summaryPage){
		if (removeWdigetId!=null && !removeWdigetId.equals("")){
			if (removeWdigetId.equals("ckwidgetAPhealth")){
				summaryPage.setCkwidgetAPhealth(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostClientCount")){
				summaryPage.setCkwidgetAPmostClientCount(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostBandwidth")){
				summaryPage.setCkwidgetAPmostBandwidth(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostInterference")){
				summaryPage.setCkwidgetAPmostInterference(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostCrcError")){
				summaryPage.setCkwidgetAPmostCrcError(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostTxRetry")){
				summaryPage.setCkwidgetAPmostTxRetry(false);
			} else if (removeWdigetId.equals("ckwidgetAPmostRxRetry")){
				summaryPage.setCkwidgetAPmostRxRetry(false);
			} else if (removeWdigetId.equals("ckwidgetAPsecurity")){
				summaryPage.setCkwidgetAPsecurity(false);
			} else if (removeWdigetId.equals("ckwidgetAPcompliance")){
				summaryPage.setCkwidgetAPcompliance(false);
			} else if (removeWdigetId.equals("ckwidgetAPalarm")){
				summaryPage.setCkwidgetAPalarm(false);
			} else if (removeWdigetId.equals("ckwidgetAPbandwidth")){
				summaryPage.setCkwidgetAPbandwidth(false);
			} else if (removeWdigetId.equals("ckwidgetAPsla")){
				summaryPage.setCkwidgetAPsla(false);
			} else if (removeWdigetId.equals("ckwidgetCinfo")){
				summaryPage.setCkwidgetCinfo(false);
			} else if (removeWdigetId.equals("ckwidgetCmostFailure")){
				summaryPage.setCkwidgetCmostFailure(false);
			} else if (removeWdigetId.equals("ckwidgetCmostTxAirtime")){
				summaryPage.setCkwidgetCmostTxAirtime(false);
			} else if (removeWdigetId.equals("ckwidgetCmostRxAirtime")){	
				summaryPage.setCkwidgetCmostRxAirtime(false);
			} else if (removeWdigetId.equals("ckwidgetCvendor")){
				summaryPage.setCkwidgetCvendor(false);
			} else if (removeWdigetId.equals("ckwidgetCradio")){
				summaryPage.setCkwidgetCradio(false);
			} else if (removeWdigetId.equals("ckwidgetCuserprofile")){
				summaryPage.setCkwidgetCuserprofile(false);
			} else if (removeWdigetId.equals("ckwidgetCsla")){
				summaryPage.setCkwidgetCsla(false);
			} else if (removeWdigetId.equals("ckwidgetScpu")){
				summaryPage.setCkwidgetScpu(false);
			} else if (removeWdigetId.equals("ckwidgetSinfo")){
				summaryPage.setCkwidgetSinfo(false);
			} else if (removeWdigetId.equals("ckwidgetSperformanceInfo")){
				summaryPage.setCkwidgetSperformanceInfo(false);
			} else if (removeWdigetId.equals("ckwidgetSuser")){
				summaryPage.setCkwidgetSuser(false);
			} else if (removeWdigetId.equals("ckwidgetAPversion")){
				summaryPage.setCkwidgetAPversion(false);
			} else if (removeWdigetId.equals("ckwidgetAuditLog")){
				summaryPage.setCkwidgetAuditLog(false);
			} else if (removeWdigetId.equals("ckwidgetAPuptime")){
				summaryPage.setCkwidgetAPuptime(false);
			} else if (removeWdigetId.equals("ckwidgetActiveUser")){
				summaryPage.setCkwidgetActiveUser(false);
			}
		}
	}
	
	public static final Vector<String> cpuUse = new Vector<String>();
	public static final Vector<String> memoryUse = new Vector<String>();
	private String totalMemo = "0";
	private String freeMemo = "0";
	private String usageMemo = "0";
	public void prepareSystemCpu(){
		if (cpuUse.size() < 20) {
			for (int i = 0; i < 20; i++) {
				cpuUse.add("0");
			}
		}
		for (int i = 0; i < 20; i++) {
			if (i != 19) {
				cpuUse.set(i, cpuUse.get(i + 1));
			} else {

				String count = "0";
				try {
					count = String.valueOf((int) (LinuxSystemInfoCollector.getInstance().getCpuInfo() * 100));
				} catch (Exception e) {
					// e.printStackTrace();
				}
				cpuUse.set(i, count);
			}
		}
		
		if (memoryUse.size() < 20) {
			for (int i = 0; i < 20; i++) {
				memoryUse.add("0");
			}
		}
		for (int i = 0; i < 20; i++) {
			if (i != 19) {
				memoryUse.set(i, memoryUse.get(i + 1));
			} else {

				String percent = "0";
				try {
					long count[] = LinuxSystemInfoCollector.getInstance().getMemInfo();
					totalMemo = String.valueOf(count[0]);
					freeMemo = String.valueOf(count[1] + count[2] + count[3]);
					usageMemo = String.valueOf(count[0] - count[1] - count[2] - count[3]);
					percent = String.valueOf((count[0] - count[1] - count[2] - count[3]) * 100
							/ count[0]);

				} catch (Exception e) {
					// e.printStackTrace();
				}
				memoryUse.set(i, percent);
			}
		}
	}
	
	private List<ActiveUserInfo> loginUsers;
	private String numberOfLogin;
	public void prepareSystemUser(){
		loginUsers = new ArrayList<ActiveUserInfo>();
		for (HttpSession activeUser : CurrentUserCache.getInstance().getActiveSessions()) {
			try {
				ActiveUserInfo tmpUser = new ActiveUserInfo();
				HmUser sessionUser = (HmUser) activeUser.getAttribute(USER_CONTEXT);
				if (sessionUser != null) {
					tmpUser.setUserIpAddress(sessionUser.getUserIpAddress());
					tmpUser.setUserName(sessionUser.getUserName());
				}
				tmpUser.setUserSessionTotalTime(NmsUtil.transformTime(
						(int) (System.currentTimeMillis() - activeUser.getCreationTime()) / 1000)
						.replace(" 0 Secs", ""));
				tmpUser.setSessionId(activeUser.getId());
				loginUsers.add(tmpUser);
			} catch (Exception e) {
				log.error(e);
			}
		}

		numberOfLogin = String.valueOf(loginUsers.size());

	}
	
	private String removeSessionId = "";
	public void removeActiveSession() {
		try {
			if (removeSessionId.equalsIgnoreCase("AllClient")){
				CurrentUserCache.getInstance().invalidateAllSessions();
			} else {
				CurrentUserCache.getInstance().invalidateSession(removeSessionId);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	private long numPackage, numEvent, numAlarm, numActiveClient, numDelat, numAuditRequest,
			numBackup, numRestore, numUpgrade;
	public void prepareSystemPerformance(){
		if (isHMOnline()) {
			numPackage = CurrentLoadCache.getInstance().getResultOfCAPWAP();
			numEvent = CurrentLoadCache.getInstance().getResultOfEvent();
			numAlarm = CurrentLoadCache.getInstance().getResultOfAlarm();
//			numActiveClient = QueryUtil.findRowCount(AhClientSession.class, 
//					new FilterParams("connectstate",AhClientSession.CONNECT_STATE_UP));
			numActiveClient = DBOperationUtil.findRowCount(AhClientSession.class, 
					new FilterParams("connectstate",AhClientSession.CONNECT_STATE_UP));
			numBackup = CurrentLoadCache.getInstance().getMaxNumberOfBackupRunning();
			numRestore= CurrentLoadCache.getInstance().getMaxNumberOfRestoreRunning();
			numDelat = CurrentLoadCache.getInstance().getMaxNumberOfConfigRunning();// Running thread number.
			numAuditRequest = CurrentLoadCache.getInstance().getMaxNumberOfConfigRequest();
			numUpgrade = CurrentLoadCache.getInstance().getMaxNumberOfUpgradeRunning();
		}
	}
	
	public final List<EnumItem> apUptime = new ArrayList<EnumItem>();
	private void prepareHiveAPUpTime(){
		if (domainId == null) {
			domainId = (long) -1;
		}
		try {
			List<LongItem> tmpApUptime = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapApUptime(domainId);
			changeTimeToString(tmpApUptime,apUptime);
		} catch (Exception e) {
			log.error("prepareHiveAPUpTime", e);
		}
	}
	
	public final List<EnumItem> users = new ArrayList<EnumItem>();
	private void prepareActiveUser(){
		try {
			FilterParams myFilterParams;
			if (domainId != null) {
				myFilterParams = new FilterParams(
							"timeStamp >= :s1 and owner.id = :s2 and globalFlg=:s3",
							new Object[] {
								System.currentTimeMillis()-1000*60*60*24,
								domainId,false });
			} else {
				myFilterParams = new FilterParams(
						"timeStamp >= :s1 and globalFlg=:s2 ",
						new Object[] {
							System.currentTimeMillis()-1000*60*60*24,
							true });
			}

			List<AhMaxClientsCount> activeClientLst = QueryUtil.executeQuery(AhMaxClientsCount.class, null,myFilterParams);
			
			for(AhMaxClientsCount oneBo:activeClientLst){
				String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(oneBo.getTimeStamp(), tz);
				users.add(new EnumItem(oneBo.getCurrentClientCount(),strTime));
			}
			
		} catch (Exception e) {
			log.error("prepareActiveUser", e);
		}
	}
	
	private final Map<String,Integer> hiveApVersionMap = new HashMap<String, Integer>();
	private void prepareHiveAPVersion(){
		
		try {
			FilterParams myFilterParams;
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("manageStatus = :s1 and owner.id = :s2",
						new Object[] { HiveAp.STATUS_MANAGED, domainId});
			} else {
				myFilterParams = new FilterParams("manageStatus = :s1",
						new Object[] { HiveAp.STATUS_MANAGED});
			}
			
			List<?> allApVersion= QueryUtil.executeQuery("select softVer from " + HiveAp.class.getSimpleName(),
					null, myFilterParams);
			for(Object oneObj:allApVersion){
				String ver = MgrUtil.getHiveOSDisplayVersion(oneObj.toString());
				if (hiveApVersionMap.get(ver)==null){
					hiveApVersionMap.put(ver, 1);
				} else {
					hiveApVersionMap.put(ver,(hiveApVersionMap.get(ver) + 1));
				}
			}
		} catch (Exception e) {
			log.error("prepareHiveAPVersion", e);
		}
	}
	
	
	private String hmHostname, hmVersion, buildTime, systemUpTime, hmModel, hmSN,
	lanState, mgtState,haStatus,replicateStatus;
	private boolean showReplicateStatus = false;
	private boolean showSerialNumber = false;
	public void prepareSystemInfo(){
		setValueHmHostname();
		setValueHmVersionAndBuildTime();
		setValueSystemUpTime();
		setValueHmModel();
		setValueHmSN();
		setInterfacesValue();
		initHAStatus();
		
	}
	
	public void setValueHmHostname() {
		try {
			hmHostname = HmBeOsUtil.getHostName();
		} catch (Exception e) {
			hmHostname = "";
		}
	}
	public void setValueHmModel() {
		try {
			hmModel = HmBeAdminUtil.getHmKernelModel();
			if ("AH-HM-1U".equals(hmModel)) {
				showSerialNumber = true;
			}
		} catch (Exception e) {
			log.error("setValueHmModel", "catch exception", e);
			hmModel = "";
		}
	}
	public void setValueHmSN() {
		try {
			hmSN = HmBeAdminUtil.getHmSerialNumber();
		} catch (Exception e) {
			log.error("setValueHmSN", "catch exception", e);
			hmSN = "";
		}
	}
	public void setValueHmVersionAndBuildTime() {
		try {
			BeVersionInfo versionInfo = getSessionVersionInfo();
			hmVersion = versionInfo.getMainVersion() + "r" 
			+ versionInfo.getSubVersion() + MgrUtil.getUserMessage("hm.version.subversion") + "." + versionInfo.getImageUid()+"";
			buildTime = getDateFormatString(versionInfo.getBuildTime(),getDomain());
		} catch (Exception e) {
			hmVersion = "";
			buildTime = "";
		}
	}
	public void setValueSystemUpTime() {
		try {
			long linkTime = HmSystemInfoUtil.getJvmUptime()/1000;
			systemUpTime = NmsUtil.transformTime((int) linkTime).replace(" 0 Secs", "");
		} catch (Exception e) {
			systemUpTime = "";
		}
	}
	
	public void setInterfacesValue() {
		try {
			List<String> mgtInfos = HmBeAdminUtil.getEthInfo("eth0");

			mgtState = mgtInfos.get(1).replace("Mb/s", " Mbps") + "   " + mgtInfos.get(2);

			List<String> lanInfos = HmBeAdminUtil.getEthInfo("eth1");
			if (lanInfos.get(0).equalsIgnoreCase("off")) {
				lanState = "Down";
			} else {
				lanState = lanInfos.get(1).replace("Mb/s", " Mbps") + "   " + lanInfos.get(2);
			}

			if (mgtState.trim().length() == 0) {
				mgtState = "unknown";
			}

			if (lanState.trim().length() == 0) {
				lanState = "unknown";
			}

		} catch (Exception e) {
			log.error("setInterfacesValue", "catch exception", e);
			mgtState = "unknown";
			lanState = "unknown";
		}
	}
	private void initHAStatus() {
		try {
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
			if (list.isEmpty()) {
				haStatus = "unknown";
				return;
			}

			HASettings haSettings = list.get(0);
			if (haSettings.getHaStatus() != HASettings.HASTATUS_ENABLE) {
				haStatus = "Standalone";
			} else {
				haStatus = "HA is running";
			}

//			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
//					+ "check_heartbeat_running.sh");
//			if (exitValue != 0) {
//				haStatus = "HA is abnormal running";
//				return;
//			}
//
//			haStatus = "HA is running";
//
//			String masterIP = HmBeOsUtil.getIP_eth0();
//			String slaveIP = masterIP.equals(haSettings.getPrimaryMGTIP()) ? haSettings
//					.getSecondaryMGTIP() : haSettings.getPrimaryMGTIP();
//
//			exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "check_master_online.sh");
//			haStatus += "  Active node (" + masterIP + ") "
//					+ (exitValue == 0 ? "online" : "offline");
//			exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "check_slave_online.sh");
//			haStatus += " | Passive node (" + slaveIP + ") "
//					+ (exitValue == 0 ? "online" : "offline");
//			if (exitValue != 0) {
//				return;
//			}
//
//			showReplicateStatus = true;
//			replicateStatus = getSlaveReplicateStatus();

		} catch (Exception e) {
			haStatus = "unknown";
			showReplicateStatus = false;
		}
	}
	
//	private int execCommand(String cmd) {
//
//		try {
//			String string_Path_Array[] = new String[3];
//			string_Path_Array[0] = "bash";
//			string_Path_Array[1] = "-c";
//			string_Path_Array[2] = cmd;
//
//			Process p = Runtime.getRuntime().exec(string_Path_Array);
//
//			p.waitFor();
//
//			return p.exitValue();
//		} catch (Exception e) {
//			log.error("execCommand", "catch exception", e);
//			return 255;
//		}
//	}
	
	/**
	 * query slave node sync status
	 * 
	 * @return -
	 */
//	private String getSlaveReplicateStatus() {
//		try {
////			List<String> results = execCommandOutResult("cat /HiveManager/ha/opt/ha_node_num");
////			if (results.size() == 0) {
////				return "Error getting node number of passive node.";
////			}
////
////			String nodeNum = results.get(0);
////			if (nodeNum.equals("2")) {
////				nodeNum = "1";
////			} else if (nodeNum.equals("1")) {
////				nodeNum = "2";
////			}
////			String slaveVPNIP = "172.16.0." + nodeNum;
////
////			results = execCommandOutResult("/HiveManager/ha/ha-d/ha-d-slave -c 13 -t " + slaveVPNIP);
//			List<String> results = execCommandOutResult("/HiveManager/ha/scripts/get_replication_status.sh");
//			String replicateStatus = "";
//			for (String out : results) {
//				if (out.toLowerCase().contains("replication status")) {
//					replicateStatus = out.substring(out.indexOf("=") + 1);
//					break;
//				}
//			}
//
//			if (!replicateStatus.trim().equalsIgnoreCase("running")) {
//				return "Replication not running.";
//			}
//
//			results = execCommandOutResult("/HiveManager/ha/scripts/get_replication_event.sh ");
//			String num_replicated = "0";
//			String num_left = "0";
//			for (String out : results) {
//				if (out.toLowerCase().contains("sync status")) {
//					if (!out.contains("#")) {
//						return "Replication running but " + out.substring(out.indexOf("=") + 1);
//					}
//
//					num_replicated = out.substring(out.indexOf("#") + 1);
//				}
//
//				if (out.toLowerCase().contains("replication event")) {
//					num_left = out.substring(out.indexOf("=") + 1);
//				}
//			}
//
//			return "Replicating data: " + num_replicated
//					+ ((Integer.valueOf(num_replicated) > 1) ? " events" : " event")
//					+ " replicated; " + num_left
//					+ ((Integer.valueOf(num_left) > 1) ? " events" : " event") + " remaining.";
//		} catch (Exception e) {
//			log.error("getSlaveReplicateStatus", "catch exception", e);
//			return "Error getting sync status.";
//		}
//	}
	
//	private List<String> execCommandOutResult(String cmd) {
//		List<String> resultList = new ArrayList<String>();
//
//		try {
//			String cmds[] = new String[3];
//			cmds[0] = "bash";
//			cmds[1] = "-c";
//			cmds[2] = cmd;
//
//			Process proc = Runtime.getRuntime().exec(cmds);
//			InputStream inputStream = proc.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream), 2048);
//
//			String line;
//			while ((line = br.readLine()) != null) {
//				resultList.add(line);
//			}
//
//			inputStream.close();
//			br.close();
//
//			return resultList;
//		} catch (Exception ex) {
//			log.error("execCommandOutResult", "catch exception", ex);
//
//			return resultList;
//		}
//	}
	
	private int apSecNew, apHthUp,apHthDown,apHthAlarm,apHthOutdate;
	public void prepareHiveApHealth(){
		try {
			FilterParams myFilterParams;
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("manageStatus = :s1 and owner.id = :s2)",
						new Object[] { HiveAp.STATUS_NEW, domainId});
			} else {
				myFilterParams = new FilterParams("manageStatus = :s1",
						new Object[] { HiveAp.STATUS_NEW});
			}
			
			// new HiveAP
			apSecNew = (int)QueryUtil.findRowCount(HiveAp.class, myFilterParams);
			
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("manageStatus = :s1 and owner.id = :s2",
						new Object[] { HiveAp.STATUS_MANAGED, domainId});
			} else {
				myFilterParams = new FilterParams("manageStatus = :s1",
						new Object[] { HiveAp.STATUS_MANAGED});
			}
			
			// connected,manageStatus,,severity,pending =:s1 or pending_user
			List<?> findHiveAPList=QueryUtil.executeQuery(
					"select connected,severity,pending,pending_user from "
					+ HiveAp.class.getSimpleName() + " bo", null, myFilterParams);

			for(Object oneobj:findHiveAPList){
				Object[] tObj=(Object[])oneobj;
				// HiveAP down and up check
				if (AhRestoreCommons.convertStringToBoolean(tObj[0].toString())){
					apHthUp++;
				} else {
					apHthDown++;
				}
				// HiveAPs with alarm condition
				if (Integer.parseInt(String.valueOf(tObj[1]))>AhAlarm.AH_SEVERITY_UNDETERMINED){
					apHthAlarm++;
				}
				// HiveAPs with outdate configuration
				if (AhRestoreCommons.convertStringToBoolean(tObj[2].toString())
					|| AhRestoreCommons.convertStringToBoolean(tObj[2].toString())){
					apHthOutdate++;
				}
			}
		} catch (Exception e) {
			log.error("prepareHiveApHealth", e);
		}
	}
	List<HmAuditLog> lstAuditLog = new ArrayList<HmAuditLog>();
	@SuppressWarnings("unchecked")
	public void prepareAuditLogInfo(){
		Long myDomain = null;
		if (domainId != null && domainId != -1) {
			myDomain =domainId;
		}
		
		lstAuditLog = (List<HmAuditLog>)QueryUtil.executeQuery("from " + HmAuditLog.class.getSimpleName(), new SortParams("logTimeStamp",false),
				null,myDomain,5,new ImplQueryBo());
		
	}
	
	class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}
			
			if (bo instanceof HmAuditLog){
				HmAuditLog auLog = (HmAuditLog)bo;

				if(auLog.getOwner() != null) {
					auLog.getOwner().getId();
				}
			}
			
			return null;
		}
	}
	
	public void prepareHiveApTop10(){
		try {
			if (ckwidgetAPmostBandwidth){
				getValueMostBandwidthAps();
			}
			if (ckwidgetAPmostInterference){
				getValueHighInterferenceAps();
			}
			if (ckwidgetAPmostClientCount){
				getMostClientAp();
			}
			if (ckwidgetAPmostCrcError || ckwidgetAPmostTxRetry || ckwidgetAPmostRxRetry){
				getAPHighRate();
			}
		} catch (Exception e) {
			log.error("prepareHiveApTop10", e);
		}
	}
	final List<AhInterfaceStats> crcErrorTop = new ArrayList<AhInterfaceStats> ();
	final List<AhInterfaceStats> txRetryRateTop = new ArrayList<AhInterfaceStats> ();
	final List<AhInterfaceStats> rxRetryRateTop = new ArrayList<AhInterfaceStats> ();
	public void getAPHighRate(){
		Calendar myCalendar = Calendar.getInstance(tz);
		myCalendar.add(Calendar.HOUR_OF_DAY, -1);

		if (ckwidgetAPmostCrcError){
			String crcSql = "select apName ,timeStamp,crcErrorRate,ifName,collectPeriod " +
				"from hm_interface_stats where timeStamp>=" +myCalendar.getTimeInMillis();
			if (domainId != null && domainId != -1) {
				crcSql = crcSql + " and owner=" + domainId;
			}
			crcSql = crcSql + " order by crcErrorRate desc, apName";
			List<?> lstcrcErrorInfo = QueryUtil.executeNativeQuery(crcSql,1000);
			Set<String> apSet = new HashSet<String>();
			for(Object oneObj:lstcrcErrorInfo){
				Object[] oneItem = (Object[])oneObj;
				if (apSet.contains(oneItem[0].toString())){
					continue;
				}
				apSet.add(oneItem[0].toString());
				AhInterfaceStats tmpBo = new AhInterfaceStats();
				tmpBo.setApName(oneItem[0].toString());
				tmpBo.setTimeStamp(Long.valueOf(oneItem[1].toString()));
				tmpBo.setCrcErrorRate(Byte.valueOf(oneItem[2].toString()));
				tmpBo.setIfName(oneItem[3].toString());
				tmpBo.setCollectPeriod(Short.valueOf(oneItem[4].toString()));
				tmpBo.setTz(tz);
				tmpBo.setOwner(getDomain());
				crcErrorTop.add(tmpBo);
				if (crcErrorTop.size()>9){
					break;
				}
			}
		}
		
		if (ckwidgetAPmostTxRetry){
			String txRetrySql = "select apName ,timeStamp,txRetryRate,ifName,collectPeriod " +
			"from hm_interface_stats where timeStamp>=" +myCalendar.getTimeInMillis();
			if (domainId != null && domainId != -1) {
				txRetrySql = txRetrySql + " and owner=" + domainId;
			}
			txRetrySql = txRetrySql + " order by txRetryRate desc,apName";
			List<?> lsttxRetrySqlInfo = QueryUtil.executeNativeQuery(txRetrySql,1000);
			Set<String> apSet = new HashSet<String>();
			for(Object oneObj:lsttxRetrySqlInfo){
				Object[] oneItem = (Object[])oneObj;
				if (apSet.contains(oneItem[0].toString())){
					continue;
				}
				apSet.add(oneItem[0].toString());
				AhInterfaceStats tmpBo = new AhInterfaceStats();
				tmpBo.setApName(oneItem[0].toString());
				tmpBo.setTimeStamp(Long.valueOf(oneItem[1].toString()));
				tmpBo.setTxRetryRate(Byte.valueOf(oneItem[2].toString()));
				tmpBo.setIfName(oneItem[3].toString());
				tmpBo.setCollectPeriod(Short.valueOf(oneItem[4].toString()));
				tmpBo.setTz(tz);
				tmpBo.setOwner(getDomain());
				txRetryRateTop.add(tmpBo);
				if (txRetryRateTop.size()>9){
					break;
				}
			}
		}
		
		if (ckwidgetAPmostRxRetry){
			String rxRetrySql = "select apName ,timeStamp,rxRetryRate,ifName,collectPeriod " +
			"from hm_interface_stats where timeStamp>=" +myCalendar.getTimeInMillis();
			if (domainId != null && domainId != -1) {
				rxRetrySql = rxRetrySql + " and owner=" + domainId;
			}
			rxRetrySql = rxRetrySql + " order by rxRetryRate desc,apName";
			List<?> lstrxRetrySqlInfo = QueryUtil.executeNativeQuery(rxRetrySql,10);
			Set<String> apSet = new HashSet<String>();
			for(Object oneObj:lstrxRetrySqlInfo){
				Object[] oneItem = (Object[])oneObj;
				if (apSet.contains(oneItem[0].toString())){
					continue;
				}
				apSet.add(oneItem[0].toString());
				AhInterfaceStats tmpBo = new AhInterfaceStats();
				tmpBo.setApName(oneItem[0].toString());
				tmpBo.setTimeStamp(Long.valueOf(oneItem[1].toString()));
				tmpBo.setRxRetryRate(Byte.valueOf(oneItem[2].toString()));
				tmpBo.setIfName(oneItem[3].toString());
				tmpBo.setCollectPeriod(Short.valueOf(oneItem[4].toString()));
				tmpBo.setTz(tz);
				tmpBo.setOwner(getDomain());
				rxRetryRateTop.add(tmpBo);
				if (rxRetryRateTop.size()>9){
					break;
				}
			}
		}
	}
	
	final List<TrafficData> apmaxClientList = new ArrayList<TrafficData>();
	public void getMostClientAp(){
		List<TrafficData> tmpApmaxClientList = new ArrayList<TrafficData>();
		List<SimpleHiveAp> lstAp;
		if (domainId!=null && domainId!=-1) {
			lstAp= CacheMgmt.getInstance().getManagedApList(domainId);
		} else {
			lstAp= CacheMgmt.getInstance().getManagedApList();
		}
		for(SimpleHiveAp simpAp:lstAp){
			TrafficData tmpData= new TrafficData();
			tmpData.setName(simpAp.getHostname());
			tmpData.setSlaCount(simpAp.getActiveClientCount());
			if (simpAp.getMapContainerId()!=null) {
				tmpData.setScore(simpAp.getMapContainerId());
			}
			tmpApmaxClientList.add(tmpData);
		}
		Collections.sort(tmpApmaxClientList, new Comparator<TrafficData>() {
			@Override
			public int compare(TrafficData o1, TrafficData o2) {
				try {
					long data1 = o1.getSlaCount();
					long data2 = o2.getSlaCount();
					long diff = data2 - data1;
					if (diff>0) {
						return 1;
					} else if (diff<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		

		Set<Long> lstMapId = new HashSet<Long>();
		for(int k=0; k<tmpApmaxClientList.size()&& apmaxClientList.size()<11; k++){
			if(tmpApmaxClientList.get(k)!=null && tmpApmaxClientList.get(k).getSlaCount()>0){
				lstMapId.add(tmpApmaxClientList.get(k).getScore());
				apmaxClientList.add(tmpApmaxClientList.get(k));
			}
		}
		if (lstMapId.size()>0){
			List<?> idMapNameLst = QueryUtil.executeQuery(
				"select id, mapName from " + 
				MapContainerNode.class.getSimpleName(), null, new FilterParams("id",lstMapId));
			
			for (TrafficData tmpData:apmaxClientList){
				if (tmpData.getScore()>0){
					for(Object oneObj:idMapNameLst){
						Object[] oneItem = (Object[]) oneObj;
						if (tmpData.getScore()==Long.parseLong(oneItem[0].toString())){
							tmpData.setLocation(oneItem[1].toString());
							break;
						}
					}
				}
			}
		}
	}
	
	private final List<TextItem> apMostBandwidthData = new ArrayList<TextItem>();
	public void getValueMostBandwidthAps(){
		String sqlRate = "select apName, sum(txByteCount + rxByteCount) as totalByte from hm_interface_stats where timeStamp >= "
			+ (System.currentTimeMillis()-1000*60*90);
		if (domainId != null && domainId != -1) {
			sqlRate = sqlRate + " and owner=" + domainId;
		}
		sqlRate = sqlRate + " and timeStamp < " + System.currentTimeMillis();
		sqlRate = sqlRate + " group by apName order by totalByte desc";

		try {
			List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sqlRate, 10);

			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneRecord = (Object[])oneObj;
				if (oneRecord[1]!=null && Long.parseLong(oneRecord[1].toString())>0) {
					long byteValue = Long.parseLong(oneRecord[1].toString());
					apMostBandwidthData.add(new TextItem(oneRecord[0].toString(),convertByteToKb(byteValue)));
				}
			}
		} catch (Exception e) {
			log.error("getValueMostBandwidthAps", e);
		}
	}
	
	private String convertByteToKb(long byteValue){
		double doubleValue = (double)byteValue;
		DecimalFormat df = new DecimalFormat("0.00");
  		if (doubleValue > 500000000) {
  			return (df.format(doubleValue/1000000000)) + " GB";
  		} else if (doubleValue > 500000) {
  			return (df.format(doubleValue/1000000)) + " MB";
		} else if (doubleValue > 500) {
  			return (df.format(doubleValue/1000)) + " KB";
  		} else {
  			return byteValue + " Bytes";
  		}
	}
	
	private final List<TrafficData> lstInterferenceCrcError = new ArrayList<TrafficData>();
	public void getValueHighInterferenceAps(){
		Calendar myCalendar = Calendar.getInstance(tz);

		String searchSQL = "timeStamp.time < :s1 AND timeStamp.time >= :s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";

		Object values[] = new Object[4];
		values[0] = myCalendar.getTimeInMillis();
		myCalendar.add(Calendar.HOUR_OF_DAY, -1);
		values[1] = myCalendar.getTimeInMillis();
		values[2] = "wifi0";
		values[3] = "wifi1";
		Long myDomain = null;
		if (domainId != null && domainId != -1) {
			myDomain =domainId;
		}
		List<?> lstInterferenceInfo = QueryUtil.executeQuery("select apName,ifName,crcError from " + AhInterferenceStats.class.getSimpleName()
				+ " bo", null, new FilterParams(searchSQL, values), myDomain);

		Map<String,TrafficData> apDataFrame = new HashMap<String,TrafficData>();
		
		if (!lstInterferenceInfo.isEmpty()) {
			for(Object object: lstInterferenceInfo){
				Object[] tmpClass = (Object[])object;
				if (apDataFrame.get(tmpClass[0].toString())==null) {
					TrafficData trafficData = new TrafficData();
					trafficData.setName(tmpClass[0].toString());
					apDataFrame.put(tmpClass[0].toString(), trafficData);
				}
				if (tmpClass[1].toString().equalsIgnoreCase("wifi0")){
					if ((Byte)tmpClass[2] > apDataFrame.get(tmpClass[0].toString()).getTxdata()){
						apDataFrame.get(tmpClass[0].toString()).setTxdata((Byte)tmpClass[2]);
					}
				}
				if (tmpClass[1].toString().equalsIgnoreCase("wifi1")){
					if ((Byte)tmpClass[2] > apDataFrame.get(tmpClass[0].toString()).getRxdata()){
						apDataFrame.get(tmpClass[0].toString()).setRxdata((Byte)tmpClass[2]);
					}
				}
			}
			
			List<TrafficData> tmpApMostData = new ArrayList<TrafficData>();
			for(String key:apDataFrame.keySet()){
				tmpApMostData.add(apDataFrame.get(key));
			}

			Collections.sort(tmpApMostData, new Comparator<TrafficData>() {
				@Override
				public int compare(TrafficData o1, TrafficData o2) {
					try {
						long data1 = o1.getTotalData();
						long data2 = o2.getTotalData();
						long diff = data2 - data1;
						if (diff>0) {
							return 1;
						} else if (diff<0) {
							return -1;
						} else {
							return 0;
						}
					} catch (Exception e) {
						return 0;
					}
				}
			});

			for(int k=0; k<tmpApMostData.size()&& k<10; k++){
				lstInterferenceCrcError.add(tmpApMostData.get(k));
			}
		}
	}
	
	
	private int apSecFriend, apSecRogue;
	private long rogueClientCountInNet,rogueClientCountOnMap;
	public void prepareHiveApNewWorkSecurity(){
		try {
			FilterParams myFilterParams;
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("stationType = :s1 and owner.id = :s2",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP, domainId });
			} else {
				myFilterParams = new FilterParams("stationType = :s1",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP });
			}
			
			// ifMacAddress,idpType
			List<?> findIdpAPList=QueryUtil.executeQuery(
					"select distinct ifMacAddress,idpType from "
					+ Idp.class.getSimpleName() + " bo", null, myFilterParams);

			for(Object oneobj:findIdpAPList){
				Object[] tObj=(Object[])oneobj;
				// Network Security
				if (Integer.parseInt(String.valueOf(tObj[1]))==BeCommunicationConstant.IDP_TYPE_ROGUE){
					apSecRogue++;
				} else {
					apSecFriend++;
				}
			}
			
			// find rogue client count
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams(
						"stationType = :s1 and idpType=:s2 and inNetworkFlag=:s3 and owner.id = :s4",
						new Object[] {
								BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
								BeCommunicationConstant.IDP_TYPE_ROGUE,
								BeCommunicationConstant.IDP_CONNECTION_IN_NET,domainId });
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and inNetworkFlag=:s3",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE,
									   BeCommunicationConstant.IDP_CONNECTION_IN_NET});
			}

			List<?> rogueInNetCount = QueryUtil.executeQuery("select count(DISTINCT ifMacAddress) from "
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			rogueClientCountInNet = AhRestoreCommons.convertLong(rogueInNetCount.get(0).toString());
			
			// find rogue client count
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams(
						"stationType = :s1 and idpType=:s2 and mapId is not null and owner.id = :s3",
						new Object[] {
								BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
								BeCommunicationConstant.IDP_TYPE_ROGUE,domainId });
			} else {
				myFilterParams = new FilterParams("stationType = :s1 and idpType=:s2 and mapId is not null",
						new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
									   BeCommunicationConstant.IDP_TYPE_ROGUE});
			}

			List<?> rogueOnMapCount = QueryUtil.executeQuery("select count(DISTINCT ifMacAddress) from "
					+  Idp.class.getSimpleName() + " bo", null,myFilterParams);
			rogueClientCountOnMap = AhRestoreCommons.convertLong(rogueOnMapCount.get(0).toString());
			
		} catch (Exception e) {
			log.error("prepareHiveApNewWorkSecurity", e);
		}
	}
	
	private int poorAp,goodAp,excellentAp;
	public void prepareHiveApCompliance(){
		if (domainId == null) {
			domainId = (long) -1;
		}
		poorAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapPoorAps(domainId);
		goodAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapGoodAps(domainId);
		excellentAp = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapExcellentAps(domainId);
	}
	public void prepareHiveApAlarm(){
		
	}
	
	public final List<CheckItem> bindWidthRate = new ArrayList<CheckItem>(24);
	public void prepareHiveApNewWorkBandwidth(){
		if (domainId == null) {
			domainId = (long) -1;
		}
		try {
			List<LongItem> tmpBindWidthRate = AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().getMapBindWidth(domainId);
			changeTimeBandToString(tmpBindWidthRate,bindWidthRate);
		} catch (Exception e) {
			log.error("prepareHiveApNewWorkBandwidth", e);
		}
	}
	
	private long activeClientCount,maxClientCount;
	public void prepareClientInfo(){
		
		try {
			//find active Client Count
			FilterParams myFilterParams;
//			if (domainId != null && domainId !=-1) {
//				myFilterParams = new FilterParams("connectstate = :s1 AND owner.id = :s2",
//						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
//			} else {
//				myFilterParams = new FilterParams("connectstate = :s1",
//						new Object[] { AhClientSession.CONNECT_STATE_UP });
//			}
//			
//			List<?> activeCount = QueryUtil.executeQuery("select count(DISTINCT clientMac) from " 
//					+  AhClientSession.class.getSimpleName() + " bo",
//					null, myFilterParams);
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("connectstate = ? AND owner = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
			} else {
				myFilterParams = new FilterParams("connectstate = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP });
			}
			
			List<?> activeCount = DBOperationUtil.executeQuery("select count(DISTINCT clientMac) from  ah_clientsession",
					null, myFilterParams);
			
			activeClientCount = AhRestoreCommons.convertLong(activeCount.get(0).toString());
			
			if (domainId != null) {
				myFilterParams = new FilterParams(
							"timeStamp >= :s1 and owner.id = :s2 and globalFlg=:s3",
							new Object[] {
								System.currentTimeMillis()-1000*60*60*24,
								domainId,false });
			} else {
				myFilterParams = new FilterParams(
						"timeStamp >= :s1 and globalFlg=:s2",
						new Object[] {
							System.currentTimeMillis()-1000*60*60*24,
							true });
			}

			List<?> maxCount = QueryUtil.executeQuery("select max(maxClientCount) from "
					+  AhMaxClientsCount.class.getSimpleName(), null,myFilterParams);
			if (maxCount.get(0)!=null) {
				maxClientCount = AhRestoreCommons.convertLong(maxCount.get(0).toString());
			} else {
				maxClientCount=0;
			}
		} catch (Exception e) {
			log.error("prepareClientInfo", e);
		}	
	}
	public void prepareClientTop10(){
		if (ckwidgetCmostFailure){
			getValueClientMostFailures();
		}
		if (ckwidgetCmostTxAirtime || ckwidgetCmostRxAirtime){
			getClientAirtimeHighRate();
		}
	}
	private final List<EnumItem> lstClietnFailures = new ArrayList<EnumItem>();
	public void getValueClientMostFailures(){
		Calendar calendar = Calendar.getInstance(tz);

		Map<String,Integer> clientFailures = new HashMap<String,Integer>();
		try {
			Object lstCondition[] = new Object[6];
			String searchSQL = "trapTimeStamp.time <:s1 and trapTimeStamp.time >=:s2";
			searchSQL = searchSQL
			+ " and eventType =:s3 and objectType=:s4 and currentState=:s5 and code=:s6";
			
			lstCondition[0]=calendar.getTimeInMillis();
			calendar.add(Calendar.HOUR_OF_DAY, -1);
			lstCondition[1]=calendar.getTimeInMillis();
			lstCondition[2]=AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE;
			lstCondition[3]=AhEvent.AH_OBJECT_TYPE_CLIENT_LINK;
			lstCondition[4]=AhEvent.AH_STATE_DOWN;
			lstCondition[5]=ReportListAction.CLIENT_DE_AUTH_CODE;
			Long myDomain = null;
			if (domainId != null && domainId != -1) {
				myDomain =domainId;
			}
			FilterParams myFilterParams = new FilterParams(searchSQL, lstCondition);
			List<?> eventList = QueryUtil.executeQuery("select remoteId from " + AhEvent.class.getSimpleName(), null, myFilterParams, myDomain);
			
			for(Object object:eventList){
				String eventRemoteId = object.toString();
				if (clientFailures.get(eventRemoteId) !=null){
					clientFailures.put(eventRemoteId,clientFailures.get(eventRemoteId) + 1);
				} else {
					clientFailures.put(eventRemoteId, 1);
				}
			}
			List<EnumItem> clientFailuresItem = new ArrayList<EnumItem>();
			for(String clientmac:clientFailures.keySet()){
				clientFailuresItem.add(new EnumItem(clientFailures.get(clientmac),clientmac));
			}
			Collections.sort(clientFailuresItem, new Comparator<EnumItem>() {
				@Override
				public int compare(EnumItem o1, EnumItem o2) {
					try {
						return  o2.getKey() -o1.getKey();
					} catch (Exception e) {
						return 0;
					}
				}
			});
			for(EnumItem enumItem: clientFailuresItem){
				if (lstClietnFailures.size()>=10){
					break;
				}
				lstClietnFailures.add(enumItem);
			}
		} catch (Exception e) {
			log.error("getValueClientMostFailures", e);
		}
	}

	final List<AhClientStats> txAirtimeTop = new ArrayList<AhClientStats> ();
	final List<AhClientStats> rxAirtimeTop = new ArrayList<AhClientStats> ();
	public void getClientAirtimeHighRate(){
		Calendar myCalendar = Calendar.getInstance(tz);
		myCalendar.add(Calendar.HOUR_OF_DAY, -1);

		if (ckwidgetCmostTxAirtime){
			String crcSql = "select clientMac ,timeStamp,collectPeriod,txAirTime " +
				"from hm_client_stats where timeStamp>=" +myCalendar.getTimeInMillis();
			if (domainId != null && domainId != -1) {
				crcSql = crcSql + " and owner=" + domainId;
			}
			crcSql = crcSql + " order by txAirTime desc,clientMac";
			List<?> lsttxAirtimeInfo = QueryUtil.executeNativeQuery(crcSql,1000);
			Set<String> clientSet = new HashSet<String>();
			for(Object oneObj:lsttxAirtimeInfo){
				Object[] oneItem = (Object[])oneObj;
				if (clientSet.contains(oneItem[0].toString())){
					continue;
				}
				clientSet.add(oneItem[0].toString());
				AhClientStats tmpBo = new AhClientStats();
				tmpBo.setClientMac(oneItem[0].toString());
				tmpBo.setTimeStamp(Long.valueOf(oneItem[1].toString()));
				tmpBo.setCollectPeriod(Short.valueOf(oneItem[2].toString()));
				tmpBo.setTxAirTime(Byte.valueOf(oneItem[3].toString()));
				tmpBo.setTz(tz);
				tmpBo.setOwner(getDomain());
				txAirtimeTop.add(tmpBo);
				if (txAirtimeTop.size()>9){
					break;
				}
			}
		}
		if (ckwidgetCmostRxAirtime) {
			String txRetrySql = "select clientMac ,timeStamp,collectPeriod,rxAirTime " +
			"from hm_client_stats where timeStamp>=" +myCalendar.getTimeInMillis();
			if (domainId != null && domainId != -1) {
				txRetrySql = txRetrySql + " and owner=" + domainId;
			}
			txRetrySql = txRetrySql + " order by rxAirTime desc,clientMac";
			List<?> lstrxAirtimeInfo = QueryUtil.executeNativeQuery(txRetrySql,1000);
			Set<String> clientSet = new HashSet<String>();
			for(Object oneObj:lstrxAirtimeInfo){
				Object[] oneItem = (Object[])oneObj;
				if (clientSet.contains(oneItem[0].toString())){
					continue;
				}
				clientSet.add(oneItem[0].toString());
				AhClientStats tmpBo = new AhClientStats();
				tmpBo.setClientMac(oneItem[0].toString());
				tmpBo.setTimeStamp(Long.valueOf(oneItem[1].toString()));
				tmpBo.setCollectPeriod(Short.valueOf(oneItem[2].toString()));
				tmpBo.setRxAirTime(Byte.valueOf(oneItem[3].toString()));
				tmpBo.setTz(tz);
				tmpBo.setOwner(getDomain());
				rxAirtimeTop.add(tmpBo);
				if (rxAirtimeTop.size()>9){
					break;
				}
			}
		}
	}
	
	
	int count11a, count11b, count11g, count11na, count11ng, countwired;
	public void prepareClientRadioMode(){
		try {
			FilterParams myFilterParams;
//			if (domainId != null && domainId !=-1) {
//				myFilterParams = new FilterParams("connectstate = :s1 AND owner.id = :s2",
//						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
//			} else {
//				myFilterParams = new FilterParams("connectstate = :s1",
//						new Object[] { AhClientSession.CONNECT_STATE_UP });
//			}
//			
//			List<?> clientProtocolList = QueryUtil.executeQuery("select clientMACProtocol, count(clientMACProtocol) from " 
//					+  AhClientSession.class.getSimpleName() + " bo",
//					null, myFilterParams, new GroupByParams(new String[]{"clientMACProtocol"}), null);
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("connectstate = ? AND owner = ? AND clientChannel > ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId, 0});
			} else {
				myFilterParams = new FilterParams("connectstate = ? AND clientChannel > ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, 0 });
			}
			
			List<?> clientProtocolList = DBOperationUtil.executeQuery("select clientMACProtocol, count(clientMACProtocol) from  ah_clientsession",
					null, myFilterParams, new GroupByParams(new String[]{"clientMACProtocol"}), null);

			for(Object objectClient:clientProtocolList){
				Object[] profile = (Object[]) objectClient;
				if (Byte.parseByte(profile[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_AMODE){
					count11a = AhRestoreCommons.convertInt(profile[1].toString());
				} else if (Byte.parseByte(profile[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_BMODE){
					count11b = AhRestoreCommons.convertInt(profile[1].toString());
				} else if (Byte.parseByte(profile[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_GMODE){
					count11g = AhRestoreCommons.convertInt(profile[1].toString());
				} else if (Byte.parseByte(profile[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NAMODE){
					count11na = AhRestoreCommons.convertInt(profile[1].toString());
				} else if (Byte.parseByte(profile[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NGMODE){
					count11ng = AhRestoreCommons.convertInt(profile[1].toString());
				}
			}
			
			if (domainId != null && domainId !=-1) {
				myFilterParams = new FilterParams("connectstate = ? AND owner = ? AND clientChannel <= ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId, 0});
			} else {
				myFilterParams = new FilterParams("connectstate = ? AND clientChannel <= ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, 0 });
			}
			
			List<?> clientWiredList = DBOperationUtil.executeQuery("select count(id) from  ah_clientsession",
					null, myFilterParams);
			if (clientWiredList.isEmpty()) {
				countwired = AhRestoreCommons.convertInt(clientWiredList.get(0).toString());
			}
		} catch (Exception e) {
			log.error("prepareClientRadioMode", e);
		}
	}
	
	private final List<CheckItem> lstClientVendorCount = new ArrayList<CheckItem>();
	public void prepareClientVendor(){
		try {
			FilterParams myFilterParams;
//			if (domainId != null && domainId != -1) {
//				myFilterParams = new FilterParams("connectstate = :s1 AND owner.id = :s2",
//						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
//			} else {
//				myFilterParams = new FilterParams("connectstate = :s1",
//						new Object[] { AhClientSession.CONNECT_STATE_UP });
//			}
//			
//			List<?> clientMacList = QueryUtil.executeQuery("select clientMac,clientUserProfId from " 
//					+  AhClientSession.class.getSimpleName() + " bo",
//					null, myFilterParams, null);
			if (domainId != null && domainId != -1) {
				myFilterParams = new FilterParams("connectstate = ? AND owner = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
			} else {
				myFilterParams = new FilterParams("connectstate = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP });
			}
			
			List<?> clientMacList = DBOperationUtil.executeQuery("select clientMac,clientUserProfId from  ah_clientsession",
					null, myFilterParams, null);
			
			if (!clientMacList.isEmpty()){
				Map<String, Integer> clientVendorCount = new HashMap<String, Integer>();
				for(Object objectClient:clientMacList){
					Object[] profile = (Object[]) objectClient;
					String tempMac = profile[0].toString().substring(0, 6).toUpperCase();
					String macVendor = AhConstantUtil.getMacOuiComName(tempMac) == null ? tempMac : 
						AhConstantUtil.getMacOuiComName(tempMac);
					if (clientVendorCount.get(macVendor) == null) {
						clientVendorCount.put(macVendor, 1);
					} else {
						clientVendorCount.put(macVendor, clientVendorCount.get(macVendor) + 1);
					}
				}
				List<CheckItem> clientVendorInformation = new ArrayList<CheckItem>();
				for (String clientMac : clientVendorCount.keySet()) {
					CheckItem tmpCheckItem = new CheckItem(clientVendorCount.get(clientMac).longValue(),
							clientMac);
					clientVendorInformation.add(tmpCheckItem);
				}
				Collections.sort(clientVendorInformation, new Comparator<CheckItem>() {
					@Override
					public int compare(CheckItem o1, CheckItem o2) {
						try {
							long ret = o2.getId() - o1.getId();
							if (ret>0) {
								return 1;
							} else if (ret==0){
								return 0;
							} else {
								return -1;
							}
						} catch (Exception e) {
							return 0;
						}
					}
				});
				long useClientCount = 0;
				for(int k=0; k<10 && k<clientVendorInformation.size(); k++){
					lstClientVendorCount.add(clientVendorInformation.get(k));
					useClientCount = useClientCount + clientVendorInformation.get(k).getId();
				}
				if (clientMacList.size()>useClientCount){
					lstClientVendorCount.add(new CheckItem(clientMacList.size()-useClientCount,"Others"));
				}
			}
		} catch (Exception e) {
			log.error("prepareClientVendor", e);
		}
	
	}
	
	private final List<CheckItem> lstClientUserProfile = new ArrayList<CheckItem>();
	public void prepareClientUserProfile(){
		try {
			FilterParams myFilterParams;
//			if (domainId != null && domainId != -1) {
//				myFilterParams = new FilterParams("connectstate = :s1 AND owner.id = :s2",
//						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
//			} else {
//				myFilterParams = new FilterParams("connectstate = :s1",
//						new Object[] { AhClientSession.CONNECT_STATE_UP });
//			}
//			
//			List<?> clientMacList = QueryUtil.executeQuery("select clientMac,clientUserProfId from " 
//					+  AhClientSession.class.getSimpleName() + " bo",
//					null, myFilterParams, null);
			if (domainId != null && domainId != -1) {
				myFilterParams = new FilterParams("connectstate = ? AND owner = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP, domainId });
			} else {
				myFilterParams = new FilterParams("connectstate = ?",
						new Object[] { AhClientSession.CONNECT_STATE_UP });
			}
			
			List<?> clientMacList = DBOperationUtil.executeQuery("select clientMac,clientUserProfId from ah_clientsession",
					null, myFilterParams, null);
			
			if (!clientMacList.isEmpty()){
				Map<String, Long> mapClientUserProfile = new HashMap<String, Long>();
				for(Object objectClient:clientMacList){
					Object[] profile = (Object[]) objectClient;
					if (mapClientUserProfile.get(profile[1].toString())!=null){
						mapClientUserProfile.put(profile[1].toString(), mapClientUserProfile.get(profile[1].toString()) + 1);
					} else {
						mapClientUserProfile.put(profile[1].toString(), (long)1);
					}
				}
				List<CheckItem> clientUserProfileInformation = new ArrayList<CheckItem>();
				for (String userId : mapClientUserProfile.keySet()) {
					clientUserProfileInformation.add(new CheckItem(mapClientUserProfile.get(userId),userId));
				}
				Collections.sort(clientUserProfileInformation, new Comparator<CheckItem>() {
					@Override
					public int compare(CheckItem o1, CheckItem o2) {
						try {
							long ret = o2.getId() - o1.getId();
							if (ret>0) {
								return 1;
							} else if (ret==0){
								return 0;
							} else {
								return -1;
							}
						} catch (Exception e) {
							return 0;
						}
					}
				});
				long useClientCount = 0;
				for(int k=0; k<10 && k<clientUserProfileInformation.size(); k++){
					lstClientUserProfile.add(clientUserProfileInformation.get(k));
					useClientCount = useClientCount + clientUserProfileInformation.get(k).getId();
				}
				if (clientMacList.size()>useClientCount){
					lstClientUserProfile.add(new CheckItem(clientMacList.size()-useClientCount,"Others"));
				}
			}
		} catch (Exception e) {
			log.error("prepareClientVendorUserProfile", e);
		}
	}
	
	private String slaApTitle;
	public final List<EnumItem> lstSlaApBad = new ArrayList<EnumItem>();
	public final List<EnumItem> lstSlaApYellow = new ArrayList<EnumItem>();
	public final List<EnumItem> lstSlaApClear = new ArrayList<EnumItem>();
	public void prepareHiveApSLA(){
		try{
			if (domainId == null) {
				domainId = (long) -1;
			}
			int showHour = Integer.parseInt(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_GUI,ConfigUtil.KEY_SLA_TIME_SPAN,"8"));
			slaApTitle="Device SLA Compliance Percentage (Last "+ showHour + " Hours)";
			FilterParams myFilterParams;
			if (domainId != -1) {
				myFilterParams = new FilterParams(
							"timeStamp >= :s1 and owner.id = :s2 and globalflag=:s3",
							new Object[] {
								System.currentTimeMillis()-1000L*60*60*showHour,
								domainId,false });
			} else {
				myFilterParams = new FilterParams(
						"timeStamp >= :s1 and globalflag=:s2 ",
						new Object[] {
							System.currentTimeMillis()-1000L*60*60*showHour,
							true });
			}
	
			List<AhSLAStats> hiveApSlaLst = QueryUtil.executeQuery(AhSLAStats.class, null,myFilterParams);
			
			if (hiveApSlaLst!= null) {
				for(AhSLAStats tmpClass:hiveApSlaLst){
					String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass.getTimeStamp(), tz);
					if (apSlaType==HMServicesSettings.AP_SLA_STATS_ALL){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApTotal_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(cGV(tmpClass.getApTotal_Yellow()),strTime));
						int tmpClear=100-tmpClass.getApTotal_Red()-tmpClass.getApTotal_Yellow();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_AIRTIME){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApAirTime_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getApAirTime_Red();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_CRC){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApCrcError_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getApCrcError_Red();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_SLA){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApSla_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(cGV(tmpClass.getApSla_Yellow()),strTime));
						int tmpClear=100-tmpClass.getApSla_Red()-tmpClass.getApSla_Yellow();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_TXRETRY){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApRetry_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getApRetry_Red();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_TXDROP){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApTxDrop_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getApTxDrop_Red();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (apSlaType==HMServicesSettings.AP_SLA_STATS_RXDROP){
						lstSlaApBad.add(new EnumItem(cGV(tmpClass.getApRxDrop_Red()),strTime));
						lstSlaApYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getApRxDrop_Red();
						lstSlaApClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					}
				}
			}
	
			if (lstSlaApBad.size()==0){
				Long timeStampMil = System.currentTimeMillis();
				for(int i=0; i<24; i++){
					String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(timeStampMil-i*1000*60*3, tz);
					lstSlaApBad.add(new EnumItem(0,strTime));
					lstSlaApYellow.add(new EnumItem(0,strTime));
					lstSlaApClear.add(new EnumItem(0,strTime));
				}
			}
		} catch (Exception e) {
			log.error("prepareHiveApSLA", e);
		}
	}
	
	private int cGV(int value){
		if (value>0) return value;
		return 0;
	}
	private String slaClientTitle;
	public final List<EnumItem> lstSlaClientBad = new ArrayList<EnumItem>();
	public final List<EnumItem> lstSlaClientYellow = new ArrayList<EnumItem>();
	public final List<EnumItem> lstSlaClientClear = new ArrayList<EnumItem>();
	public void prepareClientSLA(){

		try{
			if (domainId == null) {
				domainId = (long) -1;
			}
			int showHour = Integer.parseInt(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_GUI,ConfigUtil.KEY_SLA_TIME_SPAN,"8"));
			slaClientTitle="Client SLA Compliance Percentage (Last "+ showHour + " Hours)";
			FilterParams myFilterParams;
			if (domainId != -1) {
				myFilterParams = new FilterParams(
							"timeStamp >= :s1 and owner.id = :s2 and globalflag=:s3",
							new Object[] {
								System.currentTimeMillis()-1000L*60*60*showHour,
								domainId,false });
			} else {
				myFilterParams = new FilterParams(
						"timeStamp >= :s1 and globalflag=:s2 ",
						new Object[] {
							System.currentTimeMillis()-1000L*60*60*showHour,
							true });
			}
	
			List<AhSLAStats> clientSlaLst = QueryUtil.executeQuery(AhSLAStats.class, null,myFilterParams);
			
			if (clientSlaLst!= null) {
				for(AhSLAStats tmpClass:clientSlaLst){
					String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass.getTimeStamp(), tz);
					if (clientSlaType==HMServicesSettings.CLIENT_SLA_STATS_ALL){
						lstSlaClientBad.add(new EnumItem(cGV(tmpClass.getClientTotal_Red()),strTime));
						lstSlaClientYellow.add(new EnumItem(cGV(tmpClass.getClientTotal_Yellow()),strTime));
						int tmpClear=100-tmpClass.getClientTotal_Red()-tmpClass.getClientTotal_Yellow();
						lstSlaClientClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (clientSlaType==HMServicesSettings.CLIENT_SLA_STATS_SLA){
						lstSlaClientBad.add(new EnumItem(cGV(tmpClass.getClientSla_Red()),strTime));
						lstSlaClientYellow.add(new EnumItem(cGV(tmpClass.getClientSla_Yellow()),strTime));
						int tmpClear=100-tmpClass.getClientSla_Red()-tmpClass.getClientSla_Yellow();
						lstSlaClientClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (clientSlaType==HMServicesSettings.CLIENT_SLA_STATS_AIRTIME){
						lstSlaClientBad.add(new EnumItem(cGV(tmpClass.getClientAirTime_Red()),strTime));
						lstSlaClientYellow.add(new EnumItem(0,strTime));
						int tmpClear=100-tmpClass.getClientAirTime_Red();
						lstSlaClientClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					} else if (clientSlaType==HMServicesSettings.CLIENT_SLA_STATS_SCORE){
						lstSlaClientBad.add(new EnumItem(cGV(tmpClass.getClientScore_Red()),strTime));
						lstSlaClientYellow.add(new EnumItem(cGV(tmpClass.getClientScore_Yellow()),strTime));
						int tmpClear=100-tmpClass.getClientScore_Red()-tmpClass.getClientScore_Yellow();
						lstSlaClientClear.add(new EnumItem(tmpClear>100?0:tmpClear,strTime));
					}
				}
			}
	
			if (lstSlaClientBad.size()==0){
				Long timeStampMil = System.currentTimeMillis();
				for(int i=0; i<24; i++){
					String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(timeStampMil-i*1000*60*3, tz);
					lstSlaClientBad.add(new EnumItem(0,strTime));
					lstSlaClientYellow.add(new EnumItem(0,strTime));
					lstSlaClientClear.add(new EnumItem(0,strTime));
				}
			}
		} catch (Exception e) {
			log.error("prepareClientSLA", e);
		}
	}

	public List<EnumItem> changeTimeToString(List<LongItem> needConvertList,List<EnumItem> retResult){
		if (needConvertList!= null) {
			for(LongItem tmpClass:needConvertList){
				String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass.getValue(), tz);
				retResult.add(new EnumItem((int)tmpClass.getKey(),strTime));
			}
		}
		return retResult; 
	}
	
	public List<CheckItem> changeTimeBandToString(List<LongItem> needConvertList,List<CheckItem> retResult){
		if (needConvertList!= null) {
			for(LongItem tmpClass:needConvertList){
				String strTime = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass.getValue(), tz);
				retResult.add(new CheckItem(tmpClass.getKey(),strTime));
			}
		}
		return retResult; 
	}
	
	@Override
	public boolean isAutoPlayMsg() {
	    return true;
	}
	
	public TimeZone tz;

	private String cookieString="";
	
	private String initCookieString="";

	private boolean ckwidgetAPhealth=true;
	private boolean ckwidgetAPmostClientCount=true;
	private boolean ckwidgetAPmostBandwidth=false;
	private boolean ckwidgetAPmostInterference=false;
	private boolean ckwidgetAPmostCrcError=false;
	private boolean ckwidgetAPmostTxRetry=false;
	private boolean ckwidgetAPmostRxRetry=false;
	private boolean ckwidgetAPsecurity=false;
	private boolean ckwidgetAPcompliance=false;
	private boolean ckwidgetAPalarm=true;
	private boolean ckwidgetAPbandwidth=true;
	private boolean ckwidgetAPsla=false;
	
	private boolean ckwidgetAPversion=false;
	private boolean ckwidgetAuditLog=false;
	private boolean ckwidgetAPuptime=false;
	private boolean ckwidgetActiveUser=false; 
	
	private boolean ckwidgetCinfo=true;
	private boolean ckwidgetCmostTxAirtime=false;
	private boolean ckwidgetCmostRxAirtime=false;
	private boolean ckwidgetCmostFailure=false;
	private boolean ckwidgetCvendor=false;
	private boolean ckwidgetCradio=true;
	private boolean ckwidgetCuserprofile=false;
	private boolean ckwidgetCsla=true;
	
	private boolean ckwidgetSinfo=true;
	private boolean ckwidgetSuser=false;
	private boolean ckwidgetScpu=true;
	private boolean ckwidgetSperformanceInfo=false;

	private String removeWdigetId="";
	
	private int apSlaType=HMServicesSettings.AP_SLA_STATS_ALL;
	private int clientSlaType=HMServicesSettings.CLIENT_SLA_STATS_ALL;
	
	public boolean getCkwidgetSinfo() {
		return ckwidgetSinfo;
	}

	public boolean getCkwidgetSuser() {
		return ckwidgetSuser;
	}

	public boolean getCkwidgetScpu() {
		return ckwidgetScpu;
	}

	public boolean getCkwidgetSperformanceInfo() {
		return ckwidgetSperformanceInfo;
	}

	public void setCkwidgetSinfo(boolean ckwidgetSinfo) {
		this.ckwidgetSinfo = ckwidgetSinfo;
	}

	public void setCkwidgetSuser(boolean ckwidgetSuser) {
		this.ckwidgetSuser = ckwidgetSuser;
	}

	public void setCkwidgetScpu(boolean ckwidgetScpu) {
		this.ckwidgetScpu = ckwidgetScpu;
	}

//	public void setCkwidgetSmemory(boolean ckwidgetSmemory) {
//		this.ckwidgetSmemory = ckwidgetSmemory;
//	}

	public void setCkwidgetSperformanceInfo(boolean ckwidgetSperformanceInfo) {
		this.ckwidgetSperformanceInfo = ckwidgetSperformanceInfo;
	}

	public String getWidth() {
		return width;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public String getCookieString() {
		return cookieString;
	}

	public void setCookieString(String cookieString) {
		this.cookieString = cookieString;
	}

	public String getInitCookieString() {
		if (initCookieString==null){
			return "";
		}
		return initCookieString;
	}

	public void setInitCookieString(String initCookieString) {
		this.initCookieString = initCookieString;
	}

	public boolean getCkwidgetAPhealth() {
		return ckwidgetAPhealth;
	}

	public void setCkwidgetAPhealth(boolean ckwidgetAPhealth) {
		this.ckwidgetAPhealth = ckwidgetAPhealth;
	}

	public boolean getCkwidgetAPsecurity() {
		return ckwidgetAPsecurity;
	}

	public void setCkwidgetAPsecurity(boolean ckwidgetAPsecurity) {
		this.ckwidgetAPsecurity = ckwidgetAPsecurity;
	}

	public boolean getCkwidgetAPcompliance() {
		return ckwidgetAPcompliance;
	}

	public void setCkwidgetAPcompliance(boolean ckwidgetAPcompliance) {
		this.ckwidgetAPcompliance = ckwidgetAPcompliance;
	}

	public boolean getCkwidgetAPalarm() {
		return ckwidgetAPalarm;
	}

	public void setCkwidgetAPalarm(boolean ckwidgetAPalarm) {
		this.ckwidgetAPalarm = ckwidgetAPalarm;
	}

	public boolean getCkwidgetAPbandwidth() {
		return ckwidgetAPbandwidth;
	}

	public void setCkwidgetAPbandwidth(boolean ckwidgetAPbandwidth) {
		this.ckwidgetAPbandwidth = ckwidgetAPbandwidth;
	}

	public boolean getCkwidgetAPsla() {
		return ckwidgetAPsla;
	}

	public void setCkwidgetAPsla(boolean ckwidgetAPsla) {
		this.ckwidgetAPsla = ckwidgetAPsla;
	}

	public boolean getCkwidgetCinfo() {
		return ckwidgetCinfo;
	}

	public void setCkwidgetCinfo(boolean ckwidgetCinfo) {
		this.ckwidgetCinfo = ckwidgetCinfo;
	}

	public boolean getCkwidgetCvendor() {
		return ckwidgetCvendor;
	}

	public void setCkwidgetCvendor(boolean ckwidgetCvendor) {
		this.ckwidgetCvendor = ckwidgetCvendor;
	}

	public boolean getCkwidgetCradio() {
		return ckwidgetCradio;
	}

	public void setCkwidgetCradio(boolean ckwidgetCradio) {
		this.ckwidgetCradio = ckwidgetCradio;
	}

	public boolean getCkwidgetCuserprofile() {
		return ckwidgetCuserprofile;
	}

	public void setCkwidgetCuserprofile(boolean ckwidgetCuserprofile) {
		this.ckwidgetCuserprofile = ckwidgetCuserprofile;
	}

	public boolean getCkwidgetCsla() {
		return ckwidgetCsla;
	}

	public void setCkwidgetCsla(boolean ckwidgetCsla) {
		this.ckwidgetCsla = ckwidgetCsla;
	}

	public int getApHthUp() {
		return apHthUp;
	}

	public int getApHthDown() {
		return apHthDown;
	}

	public int getApHthAlarm() {
		return apHthAlarm;
	}

	public int getApHthOutdate() {
		return apHthOutdate;
	}

	public int getApSecFriend() {
		return apSecFriend;
	}

	public int getApSecRogue() {
		return apSecRogue;
	}

	public int getPoorAp() {
		return poorAp;
	}

	public int getGoodAp() {
		return goodAp;
	}

	public int getExcellentAp() {
		return excellentAp;
	}

	public List<CheckItem> getBindWidthRate() {
		return bindWidthRate;
	}

	public long getActiveClientCount() {
		return activeClientCount;
	}

	public int getCount11a() {
		return count11a;
	}

	public int getCount11b() {
		return count11b;
	}

	public int getCount11g() {
		return count11g;
	}

	public int getCount11na() {
		return count11na;
	}

	public int getCount11ng() {
		return count11ng;
	}

	public List<CheckItem> getLstClientVendorCount() {
		return lstClientVendorCount;
	}

	public List<CheckItem> getLstClientUserProfile() {
		return lstClientUserProfile;
	}

	public long getMaxClientCount() {
		return maxClientCount;
	}

	public String getWf_radio() {
		return wf_radio;
	}

	public String getWf_radio_h() {
		return wf_radio_h;
	}

	public String getWf_alarm() {
		return wf_alarm;
	}

	public String getWf_alarm_h() {
		return wf_alarm_h;
	}

	public String getWf_compliance() {
		return wf_compliance;
	}

	public String getWf_compliance_h() {
		return wf_compliance_h;
	}

	public String getWf_bandwidth() {
		return wf_bandwidth;
	}

	public String getWf_bandwidth_h() {
		return wf_bandwidth_h;
	}

	public String getWf_vendor() {
		return wf_vendor;
	}

	public String getWf_vendor_h() {
		return wf_vendor_h;
	}

	public String getWf_userprofile() {
		return wf_userprofile;
	}

	public String getWf_userprofile_h() {
		return wf_userprofile_h;
	}

	public String getWf_apsla() {
		return wf_apsla;
	}

	public String getWf_apsla_h() {
		return wf_apsla_h;
	}

	public String getWf_clientsla() {
		return wf_clientsla;
	}

	public String getWf_clientsla_h() {
		return wf_clientsla_h;
	}

	public String getSlaApTitle() {
		return slaApTitle;
	}

	public List<EnumItem> getLstSlaApBad() {
		return lstSlaApBad;
	}

	public String getSlaClientTitle() {
		return slaClientTitle;
	}
	
	public List<EnumItem> getLstSlaClientBad() {
		return lstSlaClientBad;
	}

	public int getApSecNew() {
		return apSecNew;
	}

	public String getWf_systemuser() {
		return wf_systemuser;
	}

	public String getWf_systemuser_h() {
		return wf_systemuser_h;
	}

	public String getWf_systemcpu() {
		return wf_systemcpu;
	}

	public String getWf_systemcpu_h() {
		return wf_systemcpu_h;
	}

	public String getWf_systemperfomance() {
		return wf_systemperfomance;
	}

	public String getWf_systemperfomance_h() {
		return wf_systemperfomance_h;
	}

	public static Vector<String> getCpuUse() {
		return cpuUse;
	}

	public static Vector<String> getMemoryUse() {
		return memoryUse;
	}

	public String getTotalMemo() {
		return totalMemo;
	}

	public String getFreeMemo() {
		return freeMemo;
	}

	public String getUsageMemo() {
		return usageMemo;
	}

	public List<ActiveUserInfo> getLoginUsers() {
		return loginUsers;
	}

	public String getNumberOfLogin() {
		return numberOfLogin;
	}

	public long getNumPackage() {
		return numPackage;
	}

	public long getNumEvent() {
		return numEvent;
	}

	public long getNumAlarm() {
		return numAlarm;
	}

	public long getNumActiveClient() {
		return numActiveClient;
	}

	public long getNumDelat() {
		return numDelat;
	}

	public long getNumAuditRequest() {
		return numAuditRequest;
	}

	public long getNumBackup() {
		return numBackup;
	}

	public long getNumRestore() {
		return numRestore;
	}

	public long getNumUpgrade() {
		return numUpgrade;
	}

	public String getHmHostname() {
		return hmHostname;
	}

	public String getHmVersion() {
		return hmVersion;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public String getSystemUpTime() {
		return systemUpTime;
	}

	public String getHmModel() {
		return hmModel;
	}

	public String getHmSN() {
		return hmSN;
	}

	public String getLanState() {
		return lanState;
	}

	public String getMgtState() {
		return mgtState;
	}

	public String getHaStatus() {
		return haStatus;
	}

	public String getReplicateStatus() {
		return replicateStatus;
	}

	public boolean getShowReplicateStatus() {
		return showReplicateStatus;
	}

	public boolean getShowSerialNumber() {
		return showSerialNumber;
	}

	public String getRemoveWdigetId() {
		return removeWdigetId;
	}

	public void setRemoveWdigetId(String removeWdigetId) {
		this.removeWdigetId = removeWdigetId;
	}

	public String getRemoveSessionId() {
		return removeSessionId;
	}

	public void setRemoveSessionId(String removeSessionId) {
		this.removeSessionId = removeSessionId;
	}

	public List<TrafficData> getLstInterferenceCrcError() {
		return lstInterferenceCrcError;
	}

	public List<TextItem> getApMostBandwidthData() {
		return apMostBandwidthData;
	}

	public List<TrafficData> getApmaxClientList() {
		return apmaxClientList;
	}

	public List<EnumItem> getLstClietnFailures() {
		return lstClietnFailures;
	}

	public List<AhInterfaceStats> getCrcErrorTop() {
		return crcErrorTop;
	}

	public List<AhInterfaceStats> getTxRetryRateTop() {
		return txRetryRateTop;
	}

	public List<AhInterfaceStats> getRxRetryRateTop() {
		return rxRetryRateTop;
	}

	/**
	 * @return the rogueClientCountInNet
	 */
	public long getRogueClientCountInNet() {
		return rogueClientCountInNet;
	}

	/**
	 * @return the rogueClientCountOnMap
	 */
	public long getRogueClientCountOnMap() {
		return rogueClientCountOnMap;
	}

	/**
	 * @return the ckwidgetAPmostClientCount
	 */
	public boolean getCkwidgetAPmostClientCount() {
		return ckwidgetAPmostClientCount;
	}

	/**
	 * @param ckwidgetAPmostClientCount the ckwidgetAPmostClientCount to set
	 */
	public void setCkwidgetAPmostClientCount(boolean ckwidgetAPmostClientCount) {
		this.ckwidgetAPmostClientCount = ckwidgetAPmostClientCount;
	}

	/**
	 * @return the ckwidgetAPmostBandwidth
	 */
	public boolean getCkwidgetAPmostBandwidth() {
		return ckwidgetAPmostBandwidth;
	}

	/**
	 * @param ckwidgetAPmostBandwidth the ckwidgetAPmostBandwidth to set
	 */
	public void setCkwidgetAPmostBandwidth(boolean ckwidgetAPmostBandwidth) {
		this.ckwidgetAPmostBandwidth = ckwidgetAPmostBandwidth;
	}

	/**
	 * @return the ckwidgetAPmostInterference
	 */
	public boolean getCkwidgetAPmostInterference() {
		return ckwidgetAPmostInterference;
	}

	/**
	 * @param ckwidgetAPmostInterference the ckwidgetAPmostInterference to set
	 */
	public void setCkwidgetAPmostInterference(boolean ckwidgetAPmostInterference) {
		this.ckwidgetAPmostInterference = ckwidgetAPmostInterference;
	}

	/**
	 * @return the ckwidgetAPmostCrcError
	 */
	public boolean getCkwidgetAPmostCrcError() {
		return ckwidgetAPmostCrcError;
	}

	/**
	 * @param ckwidgetAPmostCrcError the ckwidgetAPmostCrcError to set
	 */
	public void setCkwidgetAPmostCrcError(boolean ckwidgetAPmostCrcError) {
		this.ckwidgetAPmostCrcError = ckwidgetAPmostCrcError;
	}

	/**
	 * @return the ckwidgetAPmostTxRetry
	 */
	public boolean getCkwidgetAPmostTxRetry() {
		return ckwidgetAPmostTxRetry;
	}

	/**
	 * @param ckwidgetAPmostTxRetry the ckwidgetAPmostTxRetry to set
	 */
	public void setCkwidgetAPmostTxRetry(boolean ckwidgetAPmostTxRetry) {
		this.ckwidgetAPmostTxRetry = ckwidgetAPmostTxRetry;
	}

	/**
	 * @return the ckwidgetAPmostRxRetry
	 */
	public boolean getCkwidgetAPmostRxRetry() {
		return ckwidgetAPmostRxRetry;
	}

	/**
	 * @param ckwidgetAPmostRxRetry the ckwidgetAPmostRxRetry to set
	 */
	public void setCkwidgetAPmostRxRetry(boolean ckwidgetAPmostRxRetry) {
		this.ckwidgetAPmostRxRetry = ckwidgetAPmostRxRetry;
	}

	/**
	 * @return the ckwidgetCmostTxAirtime
	 */
	public boolean getCkwidgetCmostTxAirtime() {
		return ckwidgetCmostTxAirtime;
	}

	/**
	 * @param ckwidgetCmostTxAirtime the ckwidgetCmostTxAirtime to set
	 */
	public void setCkwidgetCmostTxAirtime(boolean ckwidgetCmostTxAirtime) {
		this.ckwidgetCmostTxAirtime = ckwidgetCmostTxAirtime;
	}

	/**
	 * @return the ckwidgetCmostRxAirtime
	 */
	public boolean getCkwidgetCmostRxAirtime() {
		return ckwidgetCmostRxAirtime;
	}

	/**
	 * @param ckwidgetCmostRxAirtime the ckwidgetCmostRxAirtime to set
	 */
	public void setCkwidgetCmostRxAirtime(boolean ckwidgetCmostRxAirtime) {
		this.ckwidgetCmostRxAirtime = ckwidgetCmostRxAirtime;
	}

	/**
	 * @return the ckwidgetCmostFailure
	 */
	public boolean getCkwidgetCmostFailure() {
		return ckwidgetCmostFailure;
	}

	/**
	 * @param ckwidgetCmostFailure the ckwidgetCmostFailure to set
	 */
	public void setCkwidgetCmostFailure(boolean ckwidgetCmostFailure) {
		this.ckwidgetCmostFailure = ckwidgetCmostFailure;
	}

	/**
	 * @return the ckwidgetAPversion
	 */
	public boolean getCkwidgetAPversion() {
		return ckwidgetAPversion;
	}

	/**
	 * @param ckwidgetAPversion the ckwidgetAPversion to set
	 */
	public void setCkwidgetAPversion(boolean ckwidgetAPversion) {
		this.ckwidgetAPversion = ckwidgetAPversion;
	}

	/**
	 * @return the wf_apversion
	 */
	public String getWf_apversion() {
		return wf_apversion;
	}

	/**
	 * @return the wf_apversion_h
	 */
	public String getWf_apversion_h() {
		return wf_apversion_h;
	}

	/**
	 * @return the hiveApVersionMap
	 */
	public Map<String, Integer> getHiveApVersionMap() {
		return hiveApVersionMap;
	}

	/**
	 * @return the ckwidgetAuditLog
	 */
	public boolean getCkwidgetAuditLog() {
		return ckwidgetAuditLog;
	}

	/**
	 * @param ckwidgetAuditLog the ckwidgetAuditLog to set
	 */
	public void setCkwidgetAuditLog(boolean ckwidgetAuditLog) {
		this.ckwidgetAuditLog = ckwidgetAuditLog;
	}

	/**
	 * @return the lstAuditLog
	 */
	public List<HmAuditLog> getLstAuditLog() {
		return lstAuditLog;
	}

	/**
	 * @return the log
	 */
	public static Tracer getLog() {
		return log;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * @return the txAirtimeTop
	 */
	public List<AhClientStats> getTxAirtimeTop() {
		return txAirtimeTop;
	}

	/**
	 * @return the rxAirtimeTop
	 */
	public List<AhClientStats> getRxAirtimeTop() {
		return rxAirtimeTop;
	}

	/**
	 * @return the ckwidgetAPuptime
	 */
	public boolean getCkwidgetAPuptime() {
		return ckwidgetAPuptime;
	}

	/**
	 * @param ckwidgetAPuptime the ckwidgetAPuptime to set
	 */
	public void setCkwidgetAPuptime(boolean ckwidgetAPuptime) {
		this.ckwidgetAPuptime = ckwidgetAPuptime;
	}

	/**
	 * @return the ckwidgetActiveUser
	 */
	public boolean getCkwidgetActiveUser() {
		return ckwidgetActiveUser;
	}

	/**
	 * @param ckwidgetActiveUser the ckwidgetActiveUser to set
	 */
	public void setCkwidgetActiveUser(boolean ckwidgetActiveUser) {
		this.ckwidgetActiveUser = ckwidgetActiveUser;
	}
	
	/**
	 * @return the wf_apuptime
	 */
	public String getWf_apuptime() {
		return wf_apuptime;
	}

	/**
	 * @return the wf_apuptime_h
	 */
	public String getWf_apuptime_h() {
		return wf_apuptime_h;
	}

	/**
	 * @return the wf_activeuser
	 */
	public String getWf_activeuser() {
		return wf_activeuser;
	}

	/**
	 * @return the wf_activeuser_h
	 */
	public String getWf_activeuser_h() {
		return wf_activeuser_h;
	}

//	/**
//	 * @return the apFilterSla
//	 */
//	public boolean isApFilterSla() {
//		return apFilterSla;
//	}
//
//	/**
//	 * @param apFilterSla the apFilterSla to set
//	 */
//	public void setApFilterSla(boolean apFilterSla) {
//		this.apFilterSla = apFilterSla;
//	}
//
//	/**
//	 * @return the apFilterCrcError
//	 */
//	public boolean isApFilterCrcError() {
//		return apFilterCrcError;
//	}
//
//	/**
//	 * @param apFilterCrcError the apFilterCrcError to set
//	 */
//	public void setApFilterCrcError(boolean apFilterCrcError) {
//		this.apFilterCrcError = apFilterCrcError;
//	}
//
//	/**
//	 * @return the apFilterAirtime
//	 */
//	public boolean isApFilterAirtime() {
//		return apFilterAirtime;
//	}
//
//	/**
//	 * @param apFilterAirtime the apFilterAirtime to set
//	 */
//	public void setApFilterAirtime(boolean apFilterAirtime) {
//		this.apFilterAirtime = apFilterAirtime;
//	}
//
//	/**
//	 * @return the apFilterTxDrop
//	 */
//	public boolean isApFilterTxDrop() {
//		return apFilterTxDrop;
//	}
//
//	/**
//	 * @param apFilterTxDrop the apFilterTxDrop to set
//	 */
//	public void setApFilterTxDrop(boolean apFilterTxDrop) {
//		this.apFilterTxDrop = apFilterTxDrop;
//	}
//
//	/**
//	 * @return the apFilterRxDrop
//	 */
//	public boolean isApFilterRxDrop() {
//		return apFilterRxDrop;
//	}
//
//	/**
//	 * @param apFilterRxDrop the apFilterRxDrop to set
//	 */
//	public void setApFilterRxDrop(boolean apFilterRxDrop) {
//		this.apFilterRxDrop = apFilterRxDrop;
//	}
//
//	/**
//	 * @return the apFilterTxRetry
//	 */
//	public boolean isApFilterTxRetry() {
//		return apFilterTxRetry;
//	}
//
//	/**
//	 * @param apFilterTxRetry the apFilterTxRetry to set
//	 */
//	public void setApFilterTxRetry(boolean apFilterTxRetry) {
//		this.apFilterTxRetry = apFilterTxRetry;
//	}

	/**
	 * @return the apUptime
	 */
	public List<EnumItem> getApUptime() {
		return apUptime;
	}

	/**
	 * @return the users
	 */
	public List<EnumItem> getUsers() {
		return users;
	}

	/**
	 * @return the tz
	 */
	public TimeZone getTz() {
		return tz;
	}

//	/**
//	 * @return the clientFilterScore
//	 */
//	public boolean isClientFilterScore() {
//		return clientFilterScore;
//	}
//
//	/**
//	 * @param clientFilterScore the clientFilterScore to set
//	 */
//	public void setClientFilterScore(boolean clientFilterScore) {
//		this.clientFilterScore = clientFilterScore;
//	}

	/**
	 * @return the apSlaType
	 */
	public int getApSlaType() {
		return apSlaType;
	}

	/**
	 * @param apSlaType the apSlaType to set
	 */
	public void setApSlaType(int apSlaType) {
		this.apSlaType = apSlaType;
	}

	/**
	 * @return the clientSlaType
	 */
	public int getClientSlaType() {
		return clientSlaType;
	}

	/**
	 * @param clientSlaType the clientSlaType to set
	 */
	public void setClientSlaType(int clientSlaType) {
		this.clientSlaType = clientSlaType;
	}

	/**
	 * @return the lstSlaClientYellow
	 */
	public List<EnumItem> getLstSlaClientYellow() {
		return lstSlaClientYellow;
	}

	/**
	 * @return the lstSlaClientClear
	 */
	public List<EnumItem> getLstSlaClientClear() {
		return lstSlaClientClear;
	}

	/**
	 * @return the lstSlaApYellow
	 */
	public List<EnumItem> getLstSlaApYellow() {
		return lstSlaApYellow;
	}

	/**
	 * @return the lstSlaApClear
	 */
	public List<EnumItem> getLstSlaApClear() {
		return lstSlaApClear;
	}

	public int getCountwired() {
		return countwired;
	}
	
	private String getDateFormatString(String timeString,HmDomain owner){
		String year = "", mouth = "", day = "", minute = "", second = "";
		int hour = 0;
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", owner);
		StringBuilder dateTimeString = new StringBuilder();
		if(timeString.length() > 0){
			String[] tmp = timeString.split(" ");
			if(tmp.length > 1){
				String[] date = tmp[0].split("-");
				if(date.length > 2){
					year = date[0];
					mouth = date[1];
					day = date[2];
				}
				String[] time = tmp[1].split(":");
				if(time.length > 2){
					hour = Integer.parseInt(time[0]);
					minute = time[1];
					second = time[2];
				}
			}
		} else {
			return timeString;
		}

		if (bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
			dateTimeString.append(mouth);
			dateTimeString.append(bo.getDateSeparator());
			dateTimeString.append(day);
			dateTimeString.append(bo.getDateSeparator());
			dateTimeString.append(year);
		} else {
			dateTimeString.append(day);
			dateTimeString.append(bo.getDateSeparator());
			dateTimeString.append(mouth);
			dateTimeString.append(bo.getDateSeparator());
			dateTimeString.append(year);
		}
		
		if (hour > 12){
			dateTimeString.append(" ");
			if(hour - 12 < 10){
				dateTimeString.append("0");
			}
			dateTimeString.append(hour - 12);
			dateTimeString.append(":");
			dateTimeString.append(minute);
			dateTimeString.append(":");
			dateTimeString.append(second);
			dateTimeString.append(" PM");
		} else {
			dateTimeString.append(" ");
			if(hour < 10){
				dateTimeString.append("0");
			}
			dateTimeString.append(hour);
			dateTimeString.append(":");
			dateTimeString.append(minute);
			dateTimeString.append(":");
			dateTimeString.append(second);
			dateTimeString.append(" AM");
		}
		
		return dateTimeString.toString();
	}
}