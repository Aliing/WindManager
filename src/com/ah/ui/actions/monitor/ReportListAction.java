package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.be.performance.BePerformScheduleImpl;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientCountForAP;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.performance.ClientSessionButton;
import com.ah.bo.performance.ComplianceResult;
import com.ah.bo.performance.ComplianceSsidListInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.datetime.AhDateTimeUtil;

public class ReportListAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final String REPORT_LIST_TYPE = "reportListType";
	public static final String REPORT_HIVEAP_NAME = "reportHiveApName";
	public static final String REPORT_HIVEAP_LST = "reportHiveApList";
	public static final String REPORT_SSID_NAME = "reportSsidName";
	public static final String REPORT_SSIDNAME_MAP = "reportSsidNameMap";
	public static final String REPORT_NEIGHBORAP_ID = "reportNeighborAPId";
	public static final String REPORT_NEIGHBORAP_MAP = "reportNeighborAPMap";
	public static final String REPORT_CLIENTMAC_LIST = "reportClientMacList";
	public static final String REPORT_CLIENT_MAC = "reportClientMac";
	public static final String REPORT_CLIENT_SESSION = "reportClientSession";
	public static final String REPORT_CLIENT_LISTCLIENTSESSION = "reportClientSessionList";
	public static final String REPORT_CLIENT_LISTCLIENTSESSIONPAGE = "reportClientSessionListPage";
	public static final String REPORT_CLIENT_CLIENTSESSIONPAGE = "reportClientSessionPage";
	public static final String REPORT_SLA_LST = "reportSlaSearchResult";
	
	public static final int CLIENT_DE_AUTH_CODE = 117440512;

	private String listType;
	
	private String buttonType;

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_REPORT = 2;
	
	public static final int COLUMN_TYPE = 3;

	public static final int COLUMN_EMAILADDRESS = 4;

	public static final int COLUMN_NEXTSCHEDULE = 5;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "report.reportList.name";
			break;
		case COLUMN_REPORT:
			code = "report.reportList.title.type";
			break;
		case COLUMN_TYPE:
			code = "report.reportList.excuteType";
			break;
		case COLUMN_EMAILADDRESS:
			code = "report.reportList.emailAddress";
			break;
		case COLUMN_NEXTSCHEDULE:
			code = "report.reportList.startTime";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_REPORT));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_EMAILADDRESS));
		columns.add(new HmTableColumn(COLUMN_NEXTSCHEDULE));
		return columns;
	}

	public String execute() throws Exception {
		if (listType == null || listType.equals("")) {
			if (MgrUtil.getSessionAttribute(REPORT_LIST_TYPE)!=null) {
				listType = MgrUtil.getSessionAttribute(REPORT_LIST_TYPE).toString();
			} else {
				listType =Navigation.L2_FEATURE_HIVEAPREPORT;
			}
		} else {
			MgrUtil.setSessionAttribute(REPORT_LIST_TYPE, listType);
		}
		if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_HIVEAPREPORT)){
			List<String> filter = new ArrayList<String>();
			filter.add(L2_FEATURE_CHANNELPOWERNOISE);
			filter.add(L2_FEATURE_RADIOAIRTIME);
			filter.add(L2_FEATURE_RADIOTRAFFICMETRICS);
			filter.add(L2_FEATURE_RADIOTROUBLESHOOTING);
			filter.add(L2_FEATURE_RADIOINTERFERENCE);
			filter.add(L2_FEATURE_HIVEAPSLA);
			filter.add(L2_FEATURE_UNIQUECLIENTCOUNT);
			
			filterParams = new FilterParams("reportType", filter);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_SSIDREPORT)){
				List<String> filter = new ArrayList<String>();
				filter.add(L2_FEATURE_SSIDAIRTIME);
				filter.add(L2_FEATURE_SSIDTRAFFICMETRICS);
				filter.add(L2_FEATURE_SSIDTROUBLESHOOTING);
				filterParams = new FilterParams("reportType", filter);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_CLIENTREPORT)){
			List<String> filter = new ArrayList<String>();
			filter.add(L2_FEATURE_CLIENTSESSION);
			filter.add(L2_FEATURE_CLIENTAIRTIME);
			filter.add(L2_FEATURE_CLIENTSLA);
			filterParams = new FilterParams("reportType", filter);
		} else {
			filterParams = new FilterParams("reportType", listType);
		}

		setSelectedL1Feature(Navigation.L1_FEATURE_MONITOR);
		if (listType.equalsIgnoreCase(L2_FEATURE_CHANNELPOWERNOISE)||
				listType.equalsIgnoreCase(L2_FEATURE_RADIOAIRTIME) ||
				listType.equalsIgnoreCase(L2_FEATURE_RADIOTRAFFICMETRICS) ||
				listType.equalsIgnoreCase(L2_FEATURE_RADIOTROUBLESHOOTING) ||
				listType.equalsIgnoreCase(L2_FEATURE_RADIOINTERFERENCE)||
				listType.equalsIgnoreCase(L2_FEATURE_HIVEAPSLA)||
				listType.equalsIgnoreCase(L2_FEATURE_UNIQUECLIENTCOUNT)){
			setSelectedL2Feature(Navigation.L2_FEATURE_HIVEAPREPORT);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_SSIDAIRTIME)||
					listType.equalsIgnoreCase(L2_FEATURE_SSIDTRAFFICMETRICS) ||
					listType.equalsIgnoreCase(L2_FEATURE_SSIDTROUBLESHOOTING)){
			setSelectedL2Feature(Navigation.L2_FEATURE_SSIDREPORT);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_CLIENTSESSION)||
				listType.equalsIgnoreCase(L2_FEATURE_CLIENTAIRTIME) ||
				listType.equalsIgnoreCase(L2_FEATURE_CLIENTSLA)){
			setSelectedL2Feature(Navigation.L2_FEATURE_CLIENTREPORT);
		} else {
			setSelectedL2Feature(listType);
		}
		
		resetPermission();
		
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		tz = getUserTimeZone();
		
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(listType + " > New")) {
					return getLstForward();
				}
				setSessionDataSource(new AhReport());
				if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_HIVEAPREPORT)){
					getDataSource().setReportType(L2_FEATURE_CHANNELPOWERNOISE);
				} else if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_SSIDREPORT)){
					getDataSource().setReportType(L2_FEATURE_SSIDAIRTIME);
				} else if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_CLIENTREPORT)){
					getDataSource().setReportType(L2_FEATURE_CLIENTSESSION);
				} else {
					getDataSource().setReportType(listType);
				}
				getDataSource().setOwner(getDomain());
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("create".equals(operation)) {
				if (checkNameExists("name=:s1 and reportType=:s2", new Object[] {
						getDataSource().getName(), getDataSource().getReportType() })) {
					return getDataSource().getReportType();
				}
				saveGuiValue();
				clearOldSession();
				String retString =  createBo();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retString;
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					if (getDataSource().getId() != null) {
						findBoById(AhReport.class, getDataSource().getId(), this);
					}
					setSessionDataSource(dataSource);
				}
				prepareGuiSetting();
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhReport ahReport = (AhReport) findBoById(boClass, cloneId);
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				setSessionDataSource(ahReport);
				prepareGuiSetting();
				addLstTitle(listType + " > New");
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("viewSsid".equals(operation)) {
				jsonObject = new JSONObject();
				if (listType.equals("meshNeighbors")) {
					jsonObject.put("id", "reportNeighborAP");
					jsonObject.put("v", ((Map<String, List<String>>) MgrUtil
							.getSessionAttribute(REPORT_NEIGHBORAP_MAP)).get(selectHiveAPName));
				} else {
					jsonObject.put("id", "reportSsidName");
					jsonObject.put("v", ((Map<String, List<String>>) MgrUtil
							.getSessionAttribute(REPORT_SSIDNAME_MAP)).get(selectHiveAPName));
				}
				return "json";
			} else if ("run".equals(operation)) {
				if (getDataSource() == null) {
					long cloneId = getSelectedIds().get(0);
					AhReport ahReport = (AhReport) findBoById(boClass, cloneId, this);
					setId(cloneId);
					setSessionDataSource(ahReport);
				} else {
					saveGuiValue();
				}
				prepareGuiSetting();
				getDataSource().setReportStartTime(System.currentTimeMillis());
				
				if (buttonType!=null && !buttonType.equals("")){
					getDataSource().setReportType(buttonType);
				}
				showReportTab = true;
				boolean generateAuditLogFlg = false;
				if (getDataSource().getReportType().equals("mostClientsAPs")) {
					prepareSetMostClientsAPs();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("clientVendor")) {
					prepareSetClientVendorCount();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("inventory")) {
					prepareSetInventory();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("clientAuth")) {
					prepareSetClientAuth();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("compliance")) {
					prepareSetCompliance();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("hiveApNonCompliance")) {
					prepareSetHiveApNonCompliance();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("clientNonCompliance")) {
					prepareSetClientNonCompliance();
					generateAuditLogFlg= true;
				} else if (getDataSource().getReportType().equals("hiveApConnection")){
					prepareSetHiveApConnect();
					generateAuditLogFlg= true;
				} else {
					prepareFlash();
					if (getDataSource().getReportType().equals("securityRogueAPs")
							|| getDataSource().getReportType().equals("securityRogueClients")
							|| getDataSource().getReportType().equals("clientCount")
							|| getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)) {
					} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
						if (!prepareSetClientSession()){
							addActionError(MgrUtil.getUserMessage("action.error.create.temp.table.fail"));
							return prepareBoList(); 
						}
					} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME)
							|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
						prepareSetClientAirTime();
					} else if (getDataSource().getReportType().equals("hiveApSla")){
						prepareSetHiveApSLA();
					} else {
						prepareSelectHiveAP();
					}
				}
				generalCSVAndEmail();
				if (generateAuditLogFlg) {
					generateAuditReportLog();
				}
				return getDataSource().getReportType();
			} else if ("runLink".equals(operation)) {
				removeLstTitle();
				if (!setTitleAndCheckAccess(listType + " > New")) {
					if (getLstForward()!=null && !getLstForward().isEmpty()) {
						return getLstForward();
					} else {
						String errorMsg = (String) MgrUtil.getSessionAttribute("errorMessage");
						if (errorMsg!=null && !errorMsg.isEmpty()) {
							addActionError(errorMsg);
						}
						return prepareBoList(); 
					}
				}
				setSessionDataSource(new AhReport());
				if (buttonType.equalsIgnoreCase("compliance")){
					getDataSource().setName("linkedSummaryPage");
					getDataSource().setComplianceType(linkComplianceType);
				}else if (buttonType.equalsIgnoreCase("hiveApSla")){
					getDataSource().setName("linkedSummaryPage");
					getDataSource().setComplianceType(linkComplianceType);
					getDataSource().setApName(reportAPName);
					getDataSource().setNewOldFlg(AhReport.REPORT_NEWOLDTYEP_OLD);
					reportAPName = null;
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
				}else if (buttonType.equalsIgnoreCase("hiveApSlaFromClientSla")){
					getDataSource().setName("linkedClientPage");
					buttonType ="hiveApSla";
					getDataSource().setReportPeriod(reportPeriodFromClient);
					//getDataSource().setComplianceType(linkComplianceType);
					getDataSource().setApName(reportAPName);
					getDataSource().setNewOldFlg(AhReport.REPORT_NEWOLDTYEP_OLD);
					reportAPName = null;
					//getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
				}else if (buttonType.equalsIgnoreCase(L2_FEATURE_CLIENTSLA)){
					getDataSource().setName("linkedSummaryPage");
					getDataSource().setComplianceType(linkComplianceType);
					getDataSource().setAuthMac(reportLinkClientMac);
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
					getDataSource().setNewOldFlg(AhReport.REPORT_NEWOLDTYEP_OLD);
				}else if (buttonType.equalsIgnoreCase(L2_FEATURE_MAXCLIENTREPORT)){
					getDataSource().setName("linkedSummaryPage");
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
				} else if (buttonType.equalsIgnoreCase(L2_FEATURE_SECURITY_NONHIVEAP)){
					getDataSource().setName("linkedSummaryPage"); 
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
				} else if (buttonType.equalsIgnoreCase(L2_FEATURE_SECURITY_NONCLIENT)){
					getDataSource().setName("linkedSummaryPage"); 
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
				} else {
					getDataSource().setName("linkedActiveClient");
					getDataSource().setAuthMac(reportLinkClientMac);
					getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEWEEK);
				}

				getDataSource().setReportType(buttonType);
				getDataSource().setOwner(getDomain());
				clearOldSession();
				prepareGuiSetting();
				
				getDataSource().setReportStartTime(System.currentTimeMillis());
				
				showReportTab = true;
				if (getDataSource().getReportType().equalsIgnoreCase("compliance")){
					prepareSetCompliance();
					generateAuditReportLog();
				} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SECURITY_NONHIVEAP)){
					prepareSetHiveApNonCompliance();
					generateAuditReportLog();
				} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SECURITY_NONCLIENT)){
					prepareSetClientNonCompliance();
					generateAuditReportLog();
				} else {
					prepareFlash();
					if (getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)){
						
					} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
						prepareSetClientAirTime();
					} else if (getDataSource().getReportType().equals("hiveApSla")){
						prepareSetHiveApSLA();
					}else {
						if (!prepareSetClientSession()){
							addActionError(MgrUtil.getUserMessage("action.error.create.temp.table.fail"));
							return prepareBoList(); 
						}
					}
				}
				return getDataSource().getReportType();
			} else if ("getFlashData".equals(operation)) {
				if (getDataSource().getReportStartTime()==0) {
					getDataSource().setReportStartTime(System.currentTimeMillis());
				}
				if (getDataSource().getReportType().equals("securityRogueAPs") 
						|| getDataSource().getReportType().equals("securityRogueClients")
						|| listType.equals("clientCount")
						|| listType.equals(L2_FEATURE_MAXCLIENTREPORT)) {

				} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
					getClientSessionInitData();
				} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME) 
						|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
					reportClientMacWhole = MgrUtil.getSessionAttribute(REPORT_CLIENT_MAC)
							.toString();
					reportClientMac = reportClientMacWhole.split("\\(")[0];
					if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)){
						lstHiveApSla = (List<AhBandWidthSentinelHistory>)MgrUtil.getSessionAttribute(REPORT_SLA_LST);
					}
				} else {
					reportAPName = MgrUtil.getSessionAttribute(REPORT_HIVEAP_NAME).toString();
					if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS)
							|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
							|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
						reportSsidName = MgrUtil.getSessionAttribute(REPORT_SSID_NAME).toString();
					}
					if (getDataSource().getReportType().equals("meshNeighbors")) {
						reportNeighborAP = MgrUtil.getSessionAttribute(REPORT_NEIGHBORAP_ID)
								.toString();
					}
					if (getDataSource().getReportType().equals("hiveApSla")) {
						lstHiveApSla = (List<AhBandWidthSentinelHistory>)MgrUtil.getSessionAttribute(REPORT_SLA_LST);
					}
				}
				initFlashData();
				generateAuditReportLog();
				return "reportListData";
			} else if ("getClientSessionData".equals(operation)) {
				getClientSessionInitData();
				setClientSessionInfo();
				if (getDataSource().getNewOldFlg()== AhReport.REPORT_NEWOLDTYEP_OLD) {
					client_trans_totalData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_TOTALDATA");
					client_trans_beData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_BEDATA");
					client_trans_bgData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_BGDATA");
					client_trans_viData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_VIDATA");
					client_trans_voData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_VODATA");
					client_trans_mgtData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_MGTDATA");
					client_trans_unicastData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_UNICASTDATA");
					client_trans_dataOctets = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_DATAOCTETS");
					client_trans_lastrate = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_TRANS_LASTRATE");
					client_rec_totalData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_TOTALDATA");
					client_rec_mgtData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_MGTDATA");
					client_rec_unicastData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_UNICASTDATA");
					client_rec_multicastData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_MULTICASTDATA");
					client_rec_broadcastData = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_BROADCASTDATA");
					client_rec_micfailures = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_MICFAILURES");
					client_rec_dataOctets = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_DATAOCTETS");
					client_rec_lastrate = (List<CheckItem>) MgrUtil
							.getSessionAttribute("CLIENT_REC_LASTRATE");
					client_rssi = (List<CheckItem>) MgrUtil.getSessionAttribute("CLIENT_RSSI");
					client_signal_to_noise = (List<CheckItem>) MgrUtil.getSessionAttribute("CLIENT_SIGNAL_TO_NOISE");
				} else {
					client_trans_totalData = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_TRANS_TOTALDATA");
					client_rec_totalData = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_REC_TOTALDATA");
					client_rec_airTime =(List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_REC_AIRTIME");
					client_trans_airTime = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_TRANS_AIRTIME");
					client_score=(List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_SCORE");
					
					client_radio_score = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_RADIO_SCORE");
					client_ipnetwork_score = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_IPNETWORK_SCORE");
					client_application_score = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_APPLICATION_SCORE");
					
					client_rec_drop = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_REC_DROP");
					client_trans_drop = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_TRANS_DROP");
					client_bandwidth = (List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_BANDWIDTH");
					client_slacount =(List<CheckItem>)MgrUtil.getSessionAttribute("CLIENT_SLACOUNT");
					client_rec_rate_dis= (Map<String, List<CheckItem>>)MgrUtil.getSessionAttribute("CLIENT_REC_RATE_DIS");
					client_rec_rate_succ_dis =(List<TextItem>)MgrUtil.getSessionAttribute("CLIENT_REC_RATE_SUCC_DIS");
					client_trans_rate_dis =(Map<String, List<CheckItem>>)MgrUtil.getSessionAttribute("CLIENT_TRANS_RATE_DIS");
					client_trans_rate_succ_dis =(List<TextItem>)MgrUtil.getSessionAttribute("CLIENT_TRANS_RATE_SUCC_DIS");
					
//					client_trans_total_rate_succ_dis =(List<TextItem>)MgrUtil.getSessionAttribute("CLIENT_TRANS_TOTAL_RATE_SUCC_DIS");
//					client_rec_total_rate_succ_dis =(List<TextItem>)MgrUtil.getSessionAttribute("CLIENT_REC_TOTAL_RATE_SUCC_DIS");
//					
					client_rec_dateTimeList =(List<String>)MgrUtil.getSessionAttribute("CLIENT_REC_DATETIMELIST");
					client_trans_dateTimeList =(List<String>)MgrUtil.getSessionAttribute("CLIENT_TRANS_DATETIMELIST");
					client_rec_rateTypeList =(List<String>)MgrUtil.getSessionAttribute("CLIENT_REC_RATETYPELIST");
					client_trans_rateTypeList =(List<String>)MgrUtil.getSessionAttribute("CLIENT_TRANS_RATETYPELIST");

				}
				return "reportListData";
			} else if ("createDownloadData".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					jsonObject = new JSONObject();
					jsonObject.put("success", false);
					
					jsonObject.put("eword","User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return "json";
				}
				getDataSource().setReportStartTime(System.currentTimeMillis());
				if (getDataSource().getReportType().equals("mostClientsAPs")) {
					prepareSetMostClientsAPs();
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("inventory")) {
					prepareSetInventory();
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("clientAuth")) {
					prepareSetClientAuth();
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("clientVendor")) {
					prepareSetClientVendorCount();
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals(L2_FEATURE_HIVEAPCONNECTION)) {
					prepareSetHiveApConnect();
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("compliance")){
					prepareSetCompliance();
					boolean isSucc = generalCurrentPdfFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals(L2_FEATURE_SECURITY_NONHIVEAP)){
					//prepareSetCompliance();
					boolean isSucc = generalCurrentNonHiveAPCsvFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals(L2_FEATURE_SECURITY_NONCLIENT)){
					//prepareSetCompliance();
					boolean isSucc = generalCurrentNonClientCsvFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("hiveApSla")) {
					lstHiveApSla = (List<AhBandWidthSentinelHistory>)MgrUtil.getSessionAttribute(REPORT_SLA_LST);
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
					lstHiveApSla = (List<AhBandWidthSentinelHistory>)MgrUtil.getSessionAttribute(REPORT_SLA_LST);
					boolean isSucc = generalCurrentCvsFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
					boolean isSucc = generalClientSessionCSVFile();
					jsonObject = new JSONObject();
					jsonObject.put("success", isSucc);
					generateAuditReportLog();
					return "json";
				} else if (getDataSource().getReportType().equals("securityRogueAPs")
						|| getDataSource().getReportType().equals("securityRogueClients")
						|| getDataSource().getReportType().equals("clientCount")) {

				} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME)) {
					reportClientMacWhole = MgrUtil.getSessionAttribute(REPORT_CLIENT_MAC)
							.toString();
					reportClientMac = reportClientMacWhole.split("\\(")[0];
				} else if (getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)){
					
				} else {
					reportAPName = MgrUtil.getSessionAttribute(REPORT_HIVEAP_NAME).toString();
					if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS)
							|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
							|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
						reportSsidName = MgrUtil.getSessionAttribute(REPORT_SSID_NAME).toString();
					}
					if (getDataSource().getReportType().equals("meshNeighbors")) {
						reportNeighborAP = MgrUtil.getSessionAttribute(REPORT_NEIGHBORAP_ID)
								.toString();
					}
				}
				initFlashData();
				boolean isSucc = generalCurrentCvsFile();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				generateAuditReportLog();
				return "json";
			} else if ("download".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					addActionError("User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return getDataSource().getReportType();
				}
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return getDataSource().getReportType();
				} 
				return "download";
			} else if ("update".equals(operation)) {
				List<?> boIds = QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null,
						new FilterParams("name=:s1 and reportType=:s2", 
						new Object[] {getDataSource().getName(), getDataSource().getReportType()})
						, domainId);
				if (!boIds.isEmpty() && !String.valueOf(boIds.get(0)).equals(getDataSource().getId().toString())) {
					addActionError(MgrUtil.getUserMessage("error.objectExists",
							getDataSource().getName()));
					return getDataSource().getReportType();
				}
				saveGuiValue();
				clearOldSession();
				if (id == null)
					setId(getDataSource().getId());
				String retString = updateBo();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retString;
			} else if ("showApSlaPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApClientSlaPanelValue(1);
				return "json";
			} else if ("showApCrcPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApInterfacePanelValue(1);
				return "json";
			} else if ("showApTxDropPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApInterfacePanelValue(2);
				return "json";
			} else if ("showApRxDropPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApInterfacePanelValue(3);
				return "json";
			} else if ("showApTxRetryPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApInterfacePanelValue(4);
				return "json";
			} else if ("showApAirtimePanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApInterfacePanelValue(5);
				return "json";
			} else if ("showCSlaPanel".equals(operation)) {
				jsonArray = new JSONArray();
				setApClientSlaPanelValue(2);
				return "json";
			} else if ("showCScorePanel".equals(operation)) {
				jsonArray = new JSONArray();
				setClientPanelValue(6);
				return "json";
//			} else if ("showCRxDropPanel".equals(operation)) {
//				jsonArray = new JSONArray();
//				setClientPanelValue(3);
//				return "json";
			} else if ("showCAirtimePanel".equals(operation)) {
				jsonArray = new JSONArray();
				setClientPanelValue(5);
				return "json";
			} else {
				if (listType.equals("clientAuth") && operation != null
						&& !operation.equals("cancel") && !operation.equals("remove")
						&& getDataSource() != null) {
					boClass = AhEvent.class;
					baseOperation();
					enablePaging();
					enableSorting();
					setClientAuthFilterParam();
					page = findBos(getDomain().getId());
					for(Object myEvent:page){
						AhEvent tmpEvent = (AhEvent)myEvent;
						tmpEvent.getTrapTimeStamp().setTimeZone(userContext.getTimeZone());
					}
					prepareGuiSetting();
					showReportTab = true;
					boClass = AhReport.class;
					return listType;
				}
				baseOperation();
				clearOldSession();
				String retStr =  prepareBoList();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retStr;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void generateAuditReportLog() {
		long diffTimer = System.currentTimeMillis() - getDataSource().getReportStartTime();
		String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
		generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.report") 
				+ MgrUtil.getUserMessage("report.reportList.name") + " (" + getDataSource().getName() + "),"
				+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + getDataSource().getReportTypeShowInGUI() + "),"
				+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + getDataSource().getReportPeriodString() + "),"
				+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + getDataSource().getExcuteTypeString() + ")"
				+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr);
	}
	
	public static TimeZone tz;

	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhReport.class);
		enableSorting();
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setPrimaryOrderBy("defaultFlag");
			sortParams.setPrimaryAscending(false);
		}
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_REPORT;
	}
	
	public boolean getHasPredefinedReport() {
		boolean has = false;
		if (null != getPage()) {
			for (Object object : getPage()) {
				AhReport item = (AhReport) object;
				if (item.getDefaultFlag()) {
					has = true;
					break;
				}
			}
		}
		return has;
	}

	public static void clearOldSession() {
		MgrUtil.removeSessionAttribute(REPORT_HIVEAP_NAME);
		MgrUtil.removeSessionAttribute(REPORT_HIVEAP_LST);
		MgrUtil.removeSessionAttribute(REPORT_SSID_NAME);
		MgrUtil.removeSessionAttribute(REPORT_SSIDNAME_MAP);
		MgrUtil.removeSessionAttribute(REPORT_NEIGHBORAP_ID);
		MgrUtil.removeSessionAttribute(REPORT_NEIGHBORAP_MAP);
		MgrUtil.removeSessionAttribute(REPORT_CLIENTMAC_LIST);
		MgrUtil.removeSessionAttribute(REPORT_CLIENT_MAC);
		MgrUtil.removeSessionAttribute(REPORT_CLIENT_SESSION);
		MgrUtil.removeSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSION);
		MgrUtil.removeSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSIONPAGE);
		MgrUtil.removeSessionAttribute(REPORT_CLIENT_CLIENTSESSIONPAGE);
		MgrUtil.removeSessionAttribute(REPORT_SLA_LST);
		
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_TOTALDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_TOTALDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_AIRTIME");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_AIRTIME");
		MgrUtil.removeSessionAttribute("CLIENT_SCORE");
		MgrUtil.removeSessionAttribute("CLIENT_RADIO_SCORE");
		MgrUtil.removeSessionAttribute("CLIENT_IPNETWORK_SCORE");
		MgrUtil.removeSessionAttribute("CLIENT_APPLICATION_SCORE");
		MgrUtil.removeSessionAttribute("CLIENT_REC_DROP");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_DROP");
		MgrUtil.removeSessionAttribute("CLIENT_BANDWIDTH");
		MgrUtil.removeSessionAttribute("CLIENT_SLACOUNT");
		MgrUtil.removeSessionAttribute("CLIENT_REC_RATE_DIS");
		MgrUtil.removeSessionAttribute("CLIENT_REC_RATE_SUCC_DIS");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_RATE_DIS");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_RATE_SUCC_DIS");
		
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_TOTAL_RATE_SUCC_DIS");
		MgrUtil.removeSessionAttribute("CLIENT_REC_TOTAL_RATE_SUCC_DIS");
		
		MgrUtil.removeSessionAttribute("CLIENT_REC_DATETIMELIST");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_DATETIMELIST");
		MgrUtil.removeSessionAttribute("CLIENT_REC_RATETYPELIST");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_RATETYPELIST");
		
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_BEDATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_BGDATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_VIDATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_VODATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_MGTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_UNICASTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_DATAOCTETS");
		MgrUtil.removeSessionAttribute("CLIENT_TRANS_LASTRATE");
		MgrUtil.removeSessionAttribute("CLIENT_REC_MGTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_UNICASTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_MULTICASTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_BROADCASTDATA");
		MgrUtil.removeSessionAttribute("CLIENT_REC_MICFAILURES");
		MgrUtil.removeSessionAttribute("CLIENT_REC_DATAOCTETS");
		MgrUtil.removeSessionAttribute("CLIENT_REC_LASTRATE");
		MgrUtil.removeSessionAttribute("CLIENT_RSSI");
		MgrUtil.removeSessionAttribute("CLIENT_SIGNAL_TO_NOISE");
	}

	public void saveGuiValue() throws Exception {
		if (startTime != null && !startTime.equals("")) {
			String datetime[] = startTime.split("-");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
			calendar.set(Calendar.HOUR_OF_DAY, startHour);
			// calendar.set(Calendar.MINUTE, startMin);
			getDataSource().setStartTime(calendar.getTimeInMillis());
		} else {
			getDataSource().setStartTime(0);
		}
		if (locationId != null && locationId > -1) {
			MapContainerNode location = findBoById(MapContainerNode.class,
					locationId);
			getDataSource().setLocation(location);
		} else {
			getDataSource().setLocation(null);
		}

		if (!getDataSource().getExcuteType().equals("2")) {
			getDataSource().setEnabledRecurrence(false);
		}

		if (!getDataSource().getEnabledEmail()) {
			getDataSource().setEmailAddress("");
		}
	}

	public void prepareGuiSetting() {
		long tmpStartTime = getDataSource().getStartTime();
		if (tmpStartTime != 0) {
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTimeInMillis(tmpStartTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(tz);
			startTime = formatter.format(calendar.getTime());
			startHour = calendar.get(Calendar.HOUR_OF_DAY);
		}
		if (getDataSource().getLocation() != null) {
			locationId = getDataSource().getLocation().getId();
		}
	}

	protected void prepareFlash() {
		swf = getDataSource().getReportType();
		application = getDataSource().getReportType();
		if (getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_NEW){
			if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
				swf="clientSessionNew";
				application="clientSessionNew";
			} else if (getDataSource().getReportType().equals(L2_FEATURE_CHANNELPOWERNOISE)){
				swf="channelPowerNoiseNew";
				application="channelPowerNoiseNew";
			}
		}
		
		width = "100%";
		bgcolor = "ffffff";
		if (getDataSource().getReportType().equals("hiveApSla") 
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)){
			height = "220";
		} else {
			height = "530";
		}
	}

	protected void generalCSVAndEmail() {
		if (!tabIndex.equals("1")) {
			if (getDataSource().getExcuteType().equals("1")
					&& !getDataSource().getEmailAddress().equals("")) {
				BePerformScheduleImpl bePerformScheduleImpl = new BePerformScheduleImpl();
				File tmpFileDir = new File(AhPerformanceScheduleModule.fileDirPath);
				if (!tmpFileDir.exists()) {
					tmpFileDir.mkdirs();
				}
				boolean ret = bePerformScheduleImpl.excutePerformance(getDataSource().getReportType(), getDataSource(),tz);
				if (ret) {
//					List<MailNotification> mailNotification = QueryUtil.executeQuery(MailNotification.class, null,
//							null,getDomain().getId());
//					String serverName = "";
//					String mailFrom = "";
//					if (!mailNotification.isEmpty()) {
//						serverName = mailNotification.get(0).getServerName();
//						mailFrom = mailNotification.get(0).getMailFrom();
//					}
//					if (serverName != null && !serverName.equals("") && mailFrom != null
//							&& !mailFrom.equals("")) {
//						AhPerformanceScheduleModule.mailCsvFile(getDataSource(), getDomain()
//								.getDomainName(), serverName, mailFrom,tz);
//					}
					
					AhPerformanceScheduleModule.mailCsvFile(getDataSource(), tz);
				}
			}
		}
	}
	
	protected boolean generalClientSessionCSVFile() {
		BePerformScheduleImpl bePerformScheduleImpl = new BePerformScheduleImpl();
		File tmpFileDir = new File(AhPerformanceScheduleModule.fileDirPath);
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
		sf.setTimeZone(tz);
		clientExportFileName = getDataSource().getReportTypeShow() + "-"  + getDataSource().getName() + "-"+   sf.format(new Date()) + ".csv";
		HmDomain tmpDomain = getDataSource().getOwner();
		if (tmpDomain.getDomainName().equalsIgnoreCase("global")){
			getDataSource().setOwner(getDomain());
		}
		boolean ret =bePerformScheduleImpl.excutePerformance(getDataSource().getReportType(), getDataSource(),tz);
		getDataSource().setOwner(tmpDomain);
		return ret;
	}

	protected void getClientSessionInitData() {
		reportClientMacWhole = MgrUtil.getSessionAttribute(REPORT_CLIENT_MAC).toString();
		reportClientMac = reportClientMacWhole.split("\\(")[0];
		if (reportClientSession != null) {
			reportClientSession = reportClientSession.replaceAll("S", ":");
		}
		pageSessionList = (Map<String, String>) MgrUtil
				.getSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSIONPAGE);
		mapLstReportClientSession = (Map<String, List<ClientSessionButton>>) MgrUtil
				.getSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSION);
		if (currentPage == 0) {
			currentPage = (Integer) MgrUtil.getSessionAttribute(REPORT_CLIENT_CLIENTSESSIONPAGE);
		} else {
			MgrUtil.setSessionAttribute(REPORT_CLIENT_CLIENTSESSIONPAGE, currentPage);
		}
		currentPageSession = pageSessionList.get(String.valueOf(currentPage));
		currentLstReportClientSession = mapLstReportClientSession.get(String.valueOf(currentPage));
		if (reportClientSession != null && !reportClientSession.equals("")) {
			reportClientSession = reportClientSession.replaceAll("S", ":");
			for (ClientSessionButton cbnt : currentLstReportClientSession) {
				if (cbnt.getSessionTime().equals(reportClientSession)) {
					cbnt.setButtonHeigth(17);
				} else {
					cbnt.setButtonHeigth(12);
				}
			}
		} else {
			if (operation.equals("createDownloadData")) {
				reportClientSession = MgrUtil.getSessionAttribute(REPORT_CLIENT_SESSION).toString();
			} else {
				for (ClientSessionButton cbnt : currentLstReportClientSession) {
					cbnt.setButtonHeigth(12);
				}
				
				for (ClientSessionButton cbnt: currentLstReportClientSession){
					if (!cbnt.getButtonColor().equals("#FFFFFF,#FFFFFF")){
						reportClientSession = cbnt.getSessionTime();
						cbnt.setButtonHeigth(17);
						break;
					}
				}
				if (reportClientSession == null || reportClientSession.equals("")){
					reportClientSession = currentLstReportClientSession.get(0).getSessionTime();
					currentLstReportClientSession.get(0).setButtonHeigth(17);
				}
			}
		}
		MgrUtil.setSessionAttribute(REPORT_CLIENT_SESSION, reportClientSession);
	}

	protected void prepareSetClientAirTime() {
		long reportStartTime = getReportDateTime().getTimeInMillis();
		if (reportClientMacWhole != null
				&& !reportClientMacWhole.equals(MgrUtil
						.getUserMessage("config.optionsTransfer.none")) && tabIndex.equals("1")) {
			reportClientMac = reportClientMacWhole.split("\\(")[0];
			lstReportClientMac = (Set<String>) MgrUtil.getSessionAttribute(REPORT_CLIENTMAC_LIST);
			MgrUtil.setSessionAttribute(REPORT_CLIENT_MAC, reportClientMacWhole);
		} else {
			String searchSQL = "select clientMac, clientIP,clientHostname,clientUsername from Ah_ClientSession_History where "
					+ " startTimeStamp >="
					+ reportStartTime
					+ " AND lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'";

			if (getDataSource().getAuthMac() != null
					&& !getDataSource().getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ getDataSource().getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthHostName() != null
					&& !getDataSource().getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ getDataSource().getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthUserName() != null
					&& !getDataSource().getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ getDataSource().getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthIp() != null
					&& !getDataSource().getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ getDataSource().getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (getDataSource().getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = "
						+ getDataSource().getLocation().getId().toString();
			}
			searchSQL = searchSQL + " AND owner=" + getDomain().getId();
			
			String searchSQLSec = "select clientMac, clientIP,clientHostname,clientUsername from Ah_ClientSession where "
					+ " lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'";
	
			if (getDataSource().getAuthMac() != null
					&& !getDataSource().getAuthMac().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientMac) like '%"
						+ getDataSource().getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthHostName() != null
					&& !getDataSource().getAuthHostName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientHostname) like '%"
						+ getDataSource().getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthUserName() != null
					&& !getDataSource().getAuthUserName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientUsername) like '%"
						+ getDataSource().getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthIp() != null
					&& !getDataSource().getAuthIp().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientIP) like '%"
						+ getDataSource().getAuthIpForSQLTwo().toLowerCase() + "%'";
			}
	
			if (getDataSource().getLocation() != null) {
				searchSQLSec = searchSQLSec + " AND mapId = "
						+ getDataSource().getLocation().getId().toString();
			}
			searchSQLSec = searchSQLSec + " AND owner=" + getDomain().getId();
			
//			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL + searchSQLSec);
			
			Map<String,Object> mapClient = new HashMap<String,Object>();
			List<?> lstPro = QueryUtil.executeNativeQuery(searchSQL);
			for(Object obj: lstPro) {
				Object[] objs = (Object[])obj;
				String key = objs[0].toString();
				mapClient.put(key, obj);
			}
			lstPro = DBOperationUtil.executeQuery(searchSQLSec);
			for(Object obj: lstPro) {
				Object[] objs = (Object[])obj;
				String key = objs[0].toString();
				mapClient.put(key, obj);
			}
			Collection<Object> profiles = mapClient.values();
			
			TreeSet<String> lstSlaClientMac = new TreeSet<String>();
			if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)){
				if (getDataSource().getComplianceType()!=AhReport.REPORT_COMPLIANCEPOLICY_ALL){
					String slaSQL = "select distinct clientMac from hm_bandwidthsentinel_history where" +
					" time>=" + reportStartTime;
					slaSQL = slaSQL + " and owner=" + getDomain().getId();
					if (getDataSource().getComplianceType()==AhReport.REPORT_COMPLIANCEPOLICY_POOR){
						slaSQL = slaSQL + " and (bandwidthsentinelstatus=0 or bandwidthsentinelstatus=2)";
					} else if (getDataSource().getComplianceType()==AhReport.REPORT_COMPLIANCEPOLICY_GOOD){
						slaSQL = slaSQL + " and bandwidthsentinelstatus=1 and (action=3 or action=2)";
					}
					List<?> lstSelectSlaClient = QueryUtil.executeNativeQuery(slaSQL);
					
					if (lstSelectSlaClient!=null && lstSelectSlaClient.size()>0){
						for (Object obj : lstSelectSlaClient) {
							lstSlaClientMac.add(obj.toString());
						}
					} 
				}	
			}
			
			lstReportClientMac = new TreeSet<String>();

			Map<String, String> clientHostNameMap = new HashMap<String, String>();
			TreeSet<String> allClientMac = new TreeSet<String>();

			if (!profiles.isEmpty()) {
				for (Object profile : profiles) {
					Object[] tmp = (Object[]) profile;
					if (tmp[0] != null) {
						allClientMac.add(tmp[0].toString().trim());

						if (clientHostNameMap.get(tmp[0].toString()) == null
								|| clientHostNameMap.get(tmp[0].toString()).equals("")) {
							clientHostNameMap.put(tmp[0].toString(), tmp[2] == null ? "" : tmp[2]
									.toString());
						}
					}
				}
				for (String clientMac : allClientMac) {
					if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)){
						if (getDataSource().getComplianceType()!=AhReport.REPORT_COMPLIANCEPOLICY_ALL){
							if (!lstSlaClientMac.contains(clientMac)){
								continue;
							}
						}
					}
					String strMacOui = clientMac.substring(0, 6).toUpperCase();
					if (lstReportClientMac.size() < 1) {
						reportClientMac = clientMac;
						reportClientMacWhole = clientMac
								+ "("
								+ ((clientHostNameMap.get(clientMac) == null || clientHostNameMap
										.get(clientMac).equals("")) ? "" : (clientHostNameMap
										.get(clientMac)+ "/"))
								+ (AhConstantUtil.getMacOuiComName(strMacOui) == null ? "unknown"
										: AhConstantUtil.getMacOuiComName(strMacOui)) + ")";
					}
					String strMacOuiFull = clientMac
							+ "("
							+ ((clientHostNameMap.get(clientMac) == null || clientHostNameMap.get(
									clientMac).equals("")) ? "" : (clientHostNameMap
									.get(clientMac)+ "/"))
							+ (AhConstantUtil.getMacOuiComName(strMacOui) == null ? "unknown"
									: AhConstantUtil.getMacOuiComName(strMacOui)) + ")";
					lstReportClientMac.add(strMacOuiFull);
				}
			}
			if (lstReportClientMac.size() < 1) {
				lstReportClientMac.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportClientMac = MgrUtil.getUserMessage("config.optionsTransfer.none");
				reportClientMacWhole = MgrUtil.getUserMessage("config.optionsTransfer.none");
			}

			MgrUtil.setSessionAttribute(REPORT_CLIENTMAC_LIST, lstReportClientMac);
			MgrUtil.setSessionAttribute(REPORT_CLIENT_MAC, reportClientMacWhole);
		}
		if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)){
			lstHiveApSla = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					new SortParams("timeStamp.time",false),
					new FilterParams("clientMac=:s1 and timeStamp.time>=:s2",
							new Object[]{reportClientMac,reportStartTime})
					,getDomain().getId());

			for(AhBandWidthSentinelHistory tmpClass:lstHiveApSla){
				tmpClass.getTimeStamp().setTimeZone(getUserContext().getTimeZone());
			}
			
			MgrUtil.setSessionAttribute(REPORT_SLA_LST, lstHiveApSla);
		}
	}

	protected boolean prepareSetClientSession() {
		long reportStartTime = getReportDateTime().getTimeInMillis();
		long endStartTime = System.currentTimeMillis();

		if (reportClientMacWhole != null
				&& !reportClientMacWhole.equals(MgrUtil
						.getUserMessage("config.optionsTransfer.none")) && tabIndex.equals("1")) {
			reportClientMac = reportClientMacWhole.split("\\(")[0];
			lstReportClientMac = (Set<String>) MgrUtil.getSessionAttribute(REPORT_CLIENTMAC_LIST);
			MgrUtil.setSessionAttribute(REPORT_CLIENT_MAC, reportClientMacWhole);
		} else {
			String searchSQL = "select clientMac,clientHostname from Ah_ClientSession_History where "
					+ " endTimeStamp >=" + reportStartTime
					+ " AND lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'";

			if (getDataSource().getAuthMac() != null
					&& !getDataSource().getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ getDataSource().getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthHostName() != null
					&& !getDataSource().getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ getDataSource().getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthUserName() != null
					&& !getDataSource().getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ getDataSource().getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthIp() != null
					&& !getDataSource().getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ getDataSource().getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (getDataSource().getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = "
						+ getDataSource().getLocation().getId().toString();
			}
			searchSQL = searchSQL + " AND owner=" + getDomain().getId();
			
			String searchSQLSec = "select clientMac,clientHostname from Ah_ClientSession where "
				+ " lower(apName) like '%"
				+ getDataSource().getApNameForSQL().toLowerCase() + "%'";

			if (getDataSource().getAuthMac() != null
					&& !getDataSource().getAuthMac().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientMac) like '%"
						+ getDataSource().getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthHostName() != null
					&& !getDataSource().getAuthHostName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientHostname) like '%"
						+ getDataSource().getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthUserName() != null
					&& !getDataSource().getAuthUserName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientUsername) like '%"
						+ getDataSource().getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (getDataSource().getAuthIp() != null
					&& !getDataSource().getAuthIp().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientIP) like '%"
						+ getDataSource().getAuthIpForSQLTwo().toLowerCase() + "%'";
			}
	
			if (getDataSource().getLocation() != null) {
				searchSQLSec = searchSQLSec + " AND mapId = "
						+ getDataSource().getLocation().getId().toString();
			}
			searchSQLSec = searchSQLSec + " AND owner=" + getDomain().getId();
		
			Map<String,Object> mapClient = new HashMap<String,Object>();
			List<?> lstPro = QueryUtil.executeNativeQuery(searchSQL);
			for(Object obj: lstPro) {
				Object[] objs = (Object[])obj;
				String key = objs[0].toString();
				mapClient.put(key, obj);
			}
			lstPro = DBOperationUtil.executeQuery(searchSQLSec);
			for(Object obj: lstPro) {
				Object[] objs = (Object[])obj;
				String key = objs[0].toString();
				mapClient.put(key, obj);
			}
			Collection<Object> profiles = mapClient.values();
			
			TreeSet<String> allClientMac = new TreeSet<String>();
			Map<String, String> clientHostNameMap = new HashMap<String, String>();
			lstReportClientMac = new TreeSet<String>();
			if (profiles == null || profiles.size() < 1) {
				lstReportClientMac.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportClientMac = MgrUtil.getUserMessage("config.optionsTransfer.none");
				reportClientMacWhole = MgrUtil.getUserMessage("config.optionsTransfer.none");
				clientHostNameMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"),
						MgrUtil.getUserMessage("config.optionsTransfer.none"));
			} else {
				for (Object profile : profiles) {
					Object[] tmp = (Object[]) profile;
					allClientMac.add(tmp[0].toString());
					if (clientHostNameMap.get(tmp[0].toString()) == null
							|| clientHostNameMap.get(tmp[0].toString()).equals("")) {
						clientHostNameMap.put(tmp[0].toString(), tmp[1] == null ? "" : tmp[1]
								.toString());
					}
				}
				for (String clientMac : allClientMac) {
					String strMacOui = clientMac.substring(0, 6).toUpperCase();
					if (lstReportClientMac.size() < 1) {
						reportClientMac = clientMac;
						reportClientMacWhole = clientMac
								+ "("
								+ ((clientHostNameMap.get(clientMac) == null || clientHostNameMap
										.get(clientMac).equals("")) ? "" : (clientHostNameMap
										.get(clientMac)+ "/"))
								+ (AhConstantUtil.getMacOuiComName(strMacOui) == null ? "unknown"
										: AhConstantUtil.getMacOuiComName(strMacOui)) + ")";
					}
					String strMacOuiFull = clientMac
							+ "("
							+ ((clientHostNameMap.get(clientMac) == null || clientHostNameMap.get(
									clientMac).equals("")) ? "" : (clientHostNameMap.get(clientMac)+ "/"))
							+ (AhConstantUtil.getMacOuiComName(strMacOui) == null ? "unknown"
									: AhConstantUtil.getMacOuiComName(strMacOui)) + ")";
					lstReportClientMac.add(strMacOuiFull);
				}
			}
		}
		try {
			String sqlClientSession = "lock TABLE ah_clientsession_history in EXCLUSIVE mode;"
					+ " drop table if exists ah_clientsession_history_"
					+ getTempTableSuffix() + ";"
					+ " create table ah_clientsession_history_" + getTempTableSuffix()
					+ " as" + " select * from ah_clientsession_history" + " where clientmac='"
					+ reportClientMac + "'" + " and endtimestamp>=" + reportStartTime
					+ " and owner=" + getDomain().getId();
			QueryUtil.executeNativeUpdate(sqlClientSession);

			String sqlAssociation = "lock TABLE hm_association in EXCLUSIVE mode;"
					+ " drop table if exists hm_association_" + getTempTableSuffix()
					+ ";" + " create table hm_association_" + getTempTableSuffix()
					+ " as" + " select * from hm_association" + " where clientmac='"
					+ reportClientMac + "'" + " and time>=" + reportStartTime
					+ " and owner=" + getDomain().getId();
			QueryUtil.executeNativeUpdate(sqlAssociation);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String searchSQL2 = "select starttimestamp,endtimestamp from ah_clientsession_history_"
				+ getTempTableSuffix() + " where endtimestamp >="
				+ reportStartTime + " AND clientmac= '" + reportClientMac + "'"
				+ " AND owner=" + getDomain().getId() + " order by starttimestamp";

		List<?> profiles2 = QueryUtil.executeNativeQuery(searchSQL2);

		int pageCount = 1;
		pageSessionList = new HashMap<String, String>();
		mapLstReportClientSession = new HashMap<String, List<ClientSessionButton>>();

		if (profiles2 == null || profiles2.size() < 1) {

			pageSessionList.put(String.valueOf(pageCount), 
					AhDateTimeUtil.getSpecifyDateTimeReport(reportStartTime,tz)
					+ "|" + 
					AhDateTimeUtil.getSpecifyDateTimeReport(endStartTime,tz));
			ClientSessionButton clientSessionButton = new ClientSessionButton();
			clientSessionButton.setButtonColor("#FFFFFF,#FFFFFF");
			clientSessionButton.setButtonHeigth(12);
			clientSessionButton.setButtonWidth(500);
			clientSessionButton.setSessionTime(MgrUtil
					.getUserMessage("config.optionsTransfer.none"));
			if (mapLstReportClientSession.get(String.valueOf(pageCount)) == null) {
				List<ClientSessionButton> tmpList = new ArrayList<ClientSessionButton>();
				tmpList.add(clientSessionButton);
				mapLstReportClientSession.put(String.valueOf(pageCount), tmpList);
			} else {
				mapLstReportClientSession.get(String.valueOf(pageCount)).add(clientSessionButton);
			}
		} else {
			long previousEndTime = reportStartTime;
			long previousStartTime = reportStartTime;
			boolean blnBlank = false;
			float totalWidth = 0;
			float width;
			for (int i = 0; i < profiles2.size(); i++) {
				Object[] tmp = (Object[]) profiles2.get(i);

				if (Long.parseLong(tmp[0].toString()) - previousEndTime > 2 * 1000) {
					String tmpSession = AhDateTimeUtil.getSpecifyDateTimeReport(previousEndTime,tz)
					+ "|"	
					+ AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(tmp[0].toString()),tz);
					ClientSessionButton clientSessionButton = new ClientSessionButton();
					clientSessionButton.setButtonColor("#FFFFFF,#FFFFFF");
					width = ((float) ((Long.parseLong(tmp[0].toString()) - previousEndTime) * 500))
							/ (8 * 60 * 60 * 1000);
					if (totalWidth + width > 500) {
						clientSessionButton.setButtonWidth(500 - totalWidth);
					} else {
						totalWidth = totalWidth + width;
						clientSessionButton.setButtonWidth(width);
					}
					clientSessionButton.setButtonHeigth(12);
					clientSessionButton.setSessionTime(tmpSession);
					if (mapLstReportClientSession.get(String.valueOf(pageCount)) == null) {
						List<ClientSessionButton> tmpList = new ArrayList<ClientSessionButton>();
						tmpList.add(clientSessionButton);
						mapLstReportClientSession.put(String.valueOf(pageCount), tmpList);
					} else {
						mapLstReportClientSession.get(String.valueOf(pageCount)).add(
								clientSessionButton);
					}
					previousEndTime = Long.parseLong(tmp[0].toString());
					blnBlank = false;
				}

				if (previousEndTime - previousStartTime >= 8 * 60 * 60 * 1000) {
					String tmpSession = AhDateTimeUtil.getSpecifyDateTimeReport(previousStartTime,tz)
						+ "|"	
						+ AhDateTimeUtil.getSpecifyDateTimeReport(previousEndTime,tz);
					pageSessionList.put(String.valueOf(pageCount), tmpSession);
					totalWidth = 0;
					pageCount++;
					previousStartTime = previousEndTime;
				}

				ClientSessionButton clientSessionButton = new ClientSessionButton();
				if (!blnBlank) {
					blnBlank = true;
					clientSessionButton.setButtonColor("#00FF00, #00FF00");
				} else {
					blnBlank = false;
					clientSessionButton.setButtonColor("#00AAFF, #00AAFF");
				}
				long tmpStartTimeInMills = Long.parseLong(tmp[0].toString())>reportStartTime ?Long.parseLong(tmp[0].toString()):reportStartTime;
				width = ((float) ((Long.parseLong(tmp[1].toString()) - tmpStartTimeInMills) * 500))
						/ (8 * 60 * 60 * 1000);
				if (totalWidth + width > 500) {
					clientSessionButton.setButtonWidth(500 - totalWidth);
				} else {
					totalWidth = totalWidth + width;
					clientSessionButton.setButtonWidth(width);
				}
				clientSessionButton.setButtonHeigth(12);
				clientSessionButton.setSessionTime(
						AhDateTimeUtil.getSpecifyDateTimeReport(tmpStartTimeInMills,tz)
						+ "|"	
						+ AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(tmp[1].toString()),tz));
				if (mapLstReportClientSession.get(String.valueOf(pageCount)) == null) {
					List<ClientSessionButton> tmpList = new ArrayList<ClientSessionButton>();
					tmpList.add(clientSessionButton);
					mapLstReportClientSession.put(String.valueOf(pageCount), tmpList);
				} else {
					mapLstReportClientSession.get(String.valueOf(pageCount)).add(
							clientSessionButton);
				}

				previousEndTime = Long.parseLong(tmp[1].toString());

				if (previousEndTime - previousStartTime >= 8 * 60 * 60 * 1000) {
					String tmpSession = 
						AhDateTimeUtil.getSpecifyDateTimeReport(previousStartTime,tz)
						+ "|"	
						+ AhDateTimeUtil.getSpecifyDateTimeReport(previousEndTime,tz);
					pageSessionList.put(String.valueOf(pageCount), tmpSession);
					totalWidth = 0;
					pageCount++;
					previousStartTime = previousEndTime;

				}
				if (i == profiles2.size() - 1) {
					if (endStartTime - previousEndTime > 2 * 1000) {
						String tmpSession = 
							AhDateTimeUtil.getSpecifyDateTimeReport(previousEndTime,tz)
							+ "|"	
							+ AhDateTimeUtil.getSpecifyDateTimeReport(endStartTime,tz);

						ClientSessionButton clientSessionButtonEnd = new ClientSessionButton();
						clientSessionButtonEnd.setButtonColor("#FFFFFF,#FFFFFF");
						width = ((float) ((endStartTime - previousEndTime) * 500))
								/ (8 * 60 * 60 * 1000);
						if (totalWidth + width > 500) {
							clientSessionButtonEnd.setButtonWidth(500 - totalWidth);
						} else {
							totalWidth = totalWidth + width;
							clientSessionButtonEnd.setButtonWidth(width);
						}
						clientSessionButtonEnd.setButtonHeigth(12);
						clientSessionButtonEnd.setSessionTime(tmpSession);
						if (mapLstReportClientSession.get(String.valueOf(pageCount)) == null) {
							List<ClientSessionButton> tmpList = new ArrayList<ClientSessionButton>();
							tmpList.add(clientSessionButtonEnd);
							mapLstReportClientSession.put(String.valueOf(pageCount), tmpList);
						} else {
							mapLstReportClientSession.get(String.valueOf(pageCount)).add(
									clientSessionButtonEnd);
						}
						previousEndTime = endStartTime;
						blnBlank = false;
					}

					if (previousEndTime != previousStartTime) {
						String tmpSession = 
							AhDateTimeUtil.getSpecifyDateTimeReport(previousStartTime,tz)
							+ "|"	
							+ AhDateTimeUtil.getSpecifyDateTimeReport(previousEndTime,tz);
						pageSessionList.put(String.valueOf(pageCount), tmpSession);
					}
				}
			}
		}
		currentPage = 1;

		MgrUtil.setSessionAttribute(REPORT_CLIENTMAC_LIST, lstReportClientMac);
		MgrUtil.setSessionAttribute(REPORT_CLIENT_MAC, reportClientMacWhole);
		MgrUtil.setSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSION, mapLstReportClientSession);
		MgrUtil.setSessionAttribute(REPORT_CLIENT_LISTCLIENTSESSIONPAGE, pageSessionList);
		MgrUtil.setSessionAttribute(REPORT_CLIENT_CLIENTSESSIONPAGE, currentPage);
		
		return true;
	}

	protected void prepareSetMostClientsAPs() {
		Calendar firstReportTime = getReportDateTime();
		Calendar reportTime = Calendar.getInstance(tz);
		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
		long systemTimeInMillis = System.currentTimeMillis();

		int reportTimeAggregation = (int) getReportTimeAggregation() / 3600000;
		FilterParams filterParams;
		if (getDataSource().getLocation() != null) {
			filterParams = new FilterParams("bo.endTimeStamp>=:s1 and bo.mapId=:s2", new Object[] {
					reportTime.getTimeInMillis(), getDataSource().getLocation().getId() });
		} else {
			filterParams = new FilterParams("bo.endTimeStamp>=:s1",
					new Object[] { reportTime.getTimeInMillis() });
		}
		List<?> profiles = QueryUtil.executeQuery(
				"select bo.apName, bo.endTimeStamp, bo.startTimeStamp from "
						+ AhClientSessionHistory.class.getSimpleName() + " bo", new SortParams(
						"endTimeStamp"), filterParams, getDomain().getId());
		Map<String, AhClientCountForAP> clientCount = new HashMap<String, AhClientCountForAP>();

		for (Object profile : profiles) {
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
			reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			Object[] tmp = (Object[]) profile;
			if (tmp[0] == null || tmp[0].toString().equals("")) {
				continue;
			}
			while (reportTime.getTimeInMillis() <= systemTimeInMillis) {
				if (Long.parseLong(tmp[1].toString()) > reportTime.getTimeInMillis()
						&& Long.parseLong(tmp[2].toString()) <= reportTime.getTimeInMillis()) {
					if (clientCount.get(tmp[0].toString() + reportTime.getTimeInMillis()) == null) {
						AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
						tmpAhClientCountForAP.setApName(tmp[0].toString());
						tmpAhClientCountForAP.setClientCount(1);
						tmpAhClientCountForAP.setTz(tz);
						tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
						clientCount.put(tmp[0].toString() + reportTime.getTimeInMillis(),
								tmpAhClientCountForAP);
					} else {
						clientCount.get(tmp[0].toString() + reportTime.getTimeInMillis())
								.addClientCount();
					}
				}

				if (clientCount.get(tmp[0].toString() + reportTime.getTimeInMillis()) == null) {
					AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
					tmpAhClientCountForAP.setApName(tmp[0].toString());
					tmpAhClientCountForAP.setClientCount(0);
					tmpAhClientCountForAP.setTz(tz);
					tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
					clientCount.put(tmp[0].toString() + reportTime.getTimeInMillis(),
							tmpAhClientCountForAP);
				}
				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			}
		}

		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
		FilterParams currentFilterParams;
//		if (getDataSource().getLocation() != null) {
//			currentFilterParams = new FilterParams("bo.mapId=:s1", new Object[] { getDataSource()
//					.getLocation().getId() });
//		} else {
//			currentFilterParams = null;
//		}
//		List<?> currentProfiles = QueryUtil.executeQuery("select bo.apName, bo.startTimeStamp from "
//				+ AhClientSession.class.getSimpleName() + " bo", new SortParams("startTimeStamp"),
//				currentFilterParams, getDomain().getId());
		if (getDataSource().getLocation() != null) {
			currentFilterParams = new FilterParams(".mapId=?", new Object[] { getDataSource()
					.getLocation().getId() });
		} else {
			currentFilterParams = null;
		}
		List<?> currentProfiles = DBOperationUtil.executeQuery("select apName, startTimeStamp from ah_clientsession",
				new SortParams("startTimeStamp"),
				currentFilterParams, getDomain().getId());

		for (Object currentProfile : currentProfiles) {
			Object[] tmp = (Object[]) currentProfile;
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
			reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			if (tmp[0] == null || tmp[0].toString().equals("")) {
				continue;
			}
			while (reportTime.getTimeInMillis() <= systemTimeInMillis) {
				if (Long.parseLong(tmp[1].toString()) > reportTime.getTimeInMillis()) {
					reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
					continue;
				}

				if (clientCount.get(tmp[0].toString() + reportTime.getTimeInMillis()) == null) {
					AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
					tmpAhClientCountForAP.setApName(tmp[0].toString());
					tmpAhClientCountForAP.setClientCount(1);
					tmpAhClientCountForAP.setTz(tz);
					tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
					clientCount.put(tmp[0].toString() + reportTime.getTimeInMillis(),
							tmpAhClientCountForAP);
				} else {
					clientCount.get(tmp[0].toString() + reportTime.getTimeInMillis())
							.addClientCount();
				}
				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			}
		}

		List<AhClientCountForAP> lstClientCount = new ArrayList<AhClientCountForAP>();
		for (AhClientCountForAP tmpClientCount : clientCount.values()) {
			lstClientCount.add(tmpClientCount);
		}

		Collections.sort(lstClientCount, new Comparator<AhClientCountForAP>() {
			@Override
			public int compare(AhClientCountForAP o1, AhClientCountForAP o2) {
				long reportTime1 = o1.getReportTime();
				long reportTime2 = o2.getReportTime();
				int diff = new Long((reportTime1 - reportTime2) / 100000)
						.intValue();
				if (diff == 0) {
					diff = o2.getClientCount() - o1.getClientCount();
					if (diff == 0) {
						diff = o1.getApName().compareTo(o2.getApName());
					}
				}
				return diff;
			}
		});

		fiveMaxClientCount = new ArrayList<List<AhClientCountForAP>>();
		long comparatorDate = 0;
		int tmpCount = 0;
		List<AhClientCountForAP> tmpList = new ArrayList<AhClientCountForAP>();
		for (int i = 0; i < lstClientCount.size(); i++) {
			if (i == 0
					|| comparatorDate == lstClientCount.get(i).getReportTime()) {
				if (tmpCount == 5) {
					continue;
				}
				tmpList.add(lstClientCount.get(i));
				comparatorDate = lstClientCount.get(i).getReportTime();
				tmpCount++;
			} else {
				fiveMaxClientCount.add(tmpList);
				tmpList = new ArrayList<AhClientCountForAP>();
				tmpList.add(lstClientCount.get(i));
				comparatorDate = lstClientCount.get(i).getReportTime();
				tmpCount = 1;
			}
		}
		if (tmpList.size() > 0) {
			fiveMaxClientCount.add(tmpList);
		}
	}

	protected void prepareSetInventory() {
		FilterParams currentFilterParams;
		List<Short> cvgList = new ArrayList<>();
		cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		if (getDataSource().getLocation() != null) {
			currentFilterParams = new FilterParams(
					"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and bo.deviceType!=:s3 and bo.hiveApModel not in :s4", new Object[] {
							getDataSource().getLocation().getId(), HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY, cvgList});
		} else {
			currentFilterParams = new FilterParams("bo.manageStatus=:s1 and bo.deviceType!=:s2 and bo.hiveApModel not in :s3",
					new Object[] { HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY, cvgList});
		}
		lstInventory = QueryUtil.executeQuery(HiveAp.class, new SortParams("hostName"), 
				currentFilterParams, getDomain().getId(), 
				new QueryBo() {
			        @Override
			        public Collection<HmBo> load(HmBo bo) {
			            if (bo instanceof HiveAp) {
			            	HiveAp ap = (HiveAp) bo;
			                if (ap.getMapContainer() != null) {
			                	ap.getMapContainer();
				                if (ap.getMapContainer().getParentMap()!=null) {
				                	ap.getMapContainer().getParentMap().getId();
				                }
			                }
			            }
			            return null;
			        }
				});
	}

	protected void prepareSetClientVendorCount() {
		Calendar firstReportTime = getReportDateTime();
		Calendar reportTime = Calendar.getInstance(tz);
		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());

		FilterParams filterParams;
		if (getDataSource().getLocation() != null) {
			filterParams = new FilterParams(
					"bo.endTimeStamp>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3", new Object[] {
							reportTime.getTimeInMillis(), getDataSource().getLocation().getId(),
							getDataSource().getApNameForSQLTwo() });
		} else {
			filterParams = new FilterParams("bo.endTimeStamp>=:s1 and lower(bo.apName) like :s2",
					new Object[] { reportTime.getTimeInMillis(), getDataSource().getApNameForSQLTwo() });
		}
		List<?> profiles = QueryUtil.executeQuery("select DISTINCT bo.clientMac from "
				+ AhClientSessionHistory.class.getSimpleName() + " bo", null, filterParams,
				getDomain().getId());

		FilterParams activeFilterParams;
//		if (getDataSource().getLocation() != null) {
//			activeFilterParams = new FilterParams("bo.mapId=:s1 and lower(bo.apName) like :s2",
//					new Object[] { getDataSource().getLocation().getId(),
//							getDataSource().getApNameForSQLTwo() });
//		} else {
//			activeFilterParams = new FilterParams("lower(bo.apName) like :s1",
//					new Object[] { getDataSource().getApNameForSQLTwo() });
//		}
//		List<?> profilesActiveClient = QueryUtil.executeQuery("select DISTINCT bo.clientMac from "
//				+ AhClientSession.class.getSimpleName() + " bo", null, activeFilterParams,
//				getDomain().getId());
		if (getDataSource().getLocation() != null) {
			activeFilterParams = new FilterParams("mapId=? and lower(apName) like ?",
					new Object[] { getDataSource().getLocation().getId(),
							getDataSource().getApNameForSQLTwo() });
		} else {
			activeFilterParams = new FilterParams("lower(apName) like ?",
					new Object[] { getDataSource().getApNameForSQLTwo() });
		}
		List<?> profilesActiveClient = DBOperationUtil.executeQuery("select DISTINCT clientMac from ah_clientsession",
				null, activeFilterParams,
				getDomain().getId());

		Map<String, Integer> clientVendorCount = new HashMap<String, Integer>();

		Set<String> clientMacSet = new HashSet<String>();
		for (Object profile : profiles) {
			clientMacSet.add(profile.toString());
		}
		for (Object obj : profilesActiveClient) {
			clientMacSet.add(obj.toString());
		}

		for (String tempMacAll: clientMacSet) {
			String tempMac = tempMacAll.substring(0, 6).toUpperCase();
			String macVendor = AhConstantUtil.getMacOuiComName(tempMac) == null ? tempMac : 
				AhConstantUtil.getMacOuiComName(tempMac);
			if (clientVendorCount.get(macVendor) == null) {
				clientVendorCount.put(macVendor, 1);
			} else {
				clientVendorCount.put(macVendor, clientVendorCount.get(macVendor) + 1);
			}
		}

		lstClientVendorCount = new ArrayList<CheckItem>();
		for (String clientMac : clientVendorCount.keySet()) {
			CheckItem tmpCheckItem = new CheckItem(clientVendorCount.get(clientMac).longValue(),
					clientMac);
			lstClientVendorCount.add(tmpCheckItem);
		}
	}
	
	protected void prepareSetHiveApConnect() {
		Calendar firstReportTime = getReportDateTime();
		Calendar reportTime = Calendar.getInstance(tz);
		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());

		FilterParams filterParams;
		if (getDataSource().getLocation() != null) {
			filterParams = new FilterParams(
					"bo.trapTime>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3", new Object[] {
							reportTime.getTimeInMillis(), getDataSource().getLocation().getId(),
							getDataSource().getApNameForSQLTwo() });
		} else {
			filterParams = new FilterParams("bo.trapTime>=:s1 and lower(bo.apName) like :s2",
					new Object[] { reportTime.getTimeInMillis(), getDataSource().getApNameForSQLTwo() });
		}
		lstHiveApConnection = QueryUtil.executeQuery(APConnectHistoryInfo.class, 
				new SortParams("apName,trapTime", false), filterParams, getDomain().getId());
		
		for(APConnectHistoryInfo info:lstHiveApConnection){
			info.setTz(tz);
		}
	}
	

	protected void prepareSetClientAuth() throws Exception {
		boClass = AhEvent.class;
		baseOperation();
		enablePaging();
		paging.setPageIndex(1);
		enableSorting();
		setClientAuthFilterParam();
		page = findBos(getDomain().getId());
		for(Object myEvent:page){
			AhEvent tmpEvent = (AhEvent)myEvent;
			tmpEvent.getTrapTimeStamp().setTimeZone(userContext.getTimeZone());
		}
		boClass = AhReport.class;
	}
	
	/**
	 * @throws Exception -
	 */
	protected void prepareSetCompliance() throws Exception {
		FilterParams filterParams;
		
		List<Short> notInList = new ArrayList<>();
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		notInList.add(HiveAp.HIVEAP_MODEL_SR24);
		notInList.add(HiveAp.HIVEAP_MODEL_SR48);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2124P);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2024P);
		notInList.add(HiveAp.HIVEAP_MODEL_SR2148P);
		if (getDataSource().getLocation() != null) {
			filterParams = new FilterParams(
					"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and lower(bo.hostName) like :s3 and " +
					"(bo.deviceType=:s4 or bo.deviceType=:s5 or bo.deviceType=:s6) and bo.hiveApModel not in :s7 ", new Object[] {
							getDataSource().getLocation().getId(),HiveAp.STATUS_MANAGED,
							getDataSource().getApNameForSQLTwo(), HiveAp.Device_TYPE_HIVEAP, 
							HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,
							notInList});
		} else {
			filterParams = new FilterParams(
					"bo.manageStatus=:s1 and lower(bo.hostName) like :s2 and " +
					"(bo.deviceType=:s3 or bo.deviceType=:s4 or bo.deviceType=:s5) and bo.hiveApModel not in :s6 ", new Object[] {
							HiveAp.STATUS_MANAGED,
							getDataSource().getApNameForSQLTwo(),HiveAp.Device_TYPE_HIVEAP, 
							HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,
							notInList});
		}
		
		List<HiveAp> profiles = QueryUtil.executeQuery(HiveAp.class
				,null, filterParams, getDomain().getId());
		
		if (!profiles.isEmpty()){
			CompliancePolicy compliancePolicy;
			List<CompliancePolicy> configData = QueryUtil.executeQuery(CompliancePolicy.class, null, null,getDomain().getId());
			if (configData.size()==0) {
				compliancePolicy = new CompliancePolicy();
			}  else {
				compliancePolicy = findBoById(CompliancePolicy.class, configData.get(0).getId());
			}

//			if (!compliancePolicy.getPasswordHm()){
//				hmpassStrength = getText("report.reportList.compliance.strong");
//			} else {
//				String hmpassword = getSessionUserContext().getPassword();
//				if (hmpassword.equalsIgnoreCase("aerohive")){
//					hmpassStrength = getText("report.reportList.compliance.weak");
//				} else {
//					int hmPass = MgrUtil.checkPasswordStrength(hmpassword);
//					hmpassStrength = ComplianceResult.getPasswordStrengthString(hmPass);
//				}
//			}
			List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null, null, 
					getDomain().getId());
			HmStartConfig stg=  list.isEmpty() ? null : list.get(0);
			String globalDevicePwd=null;
			if (stg!=null) {
				globalDevicePwd = stg.getHiveApPassword();

			}
			for(HiveAp hiveap:profiles){
				ComplianceResult complianceResult = new ComplianceResult();
	
				complianceResult.setHiveApName(hiveap.getHostName());
				if (!compliancePolicy.getPasswordHiveap()){
					complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_NA);
				} else {
					if (hiveap.getCfgPassword()==null || hiveap.getCfgPassword().equals("")){
						if (globalDevicePwd == null
								|| "".equals(globalDevicePwd)
								|| globalDevicePwd.equals(NmsUtil.getOEMCustomer().getDefaultAPPassword())) {
							complianceResult
							.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
						} else {
							complianceResult
							.setHiveApPass(MgrUtil.checkPasswordStrength(globalDevicePwd));
						}
					} else {
						if (hiveap.getCfgReadOnlyUser()==null || hiveap.getCfgReadOnlyUser().equals("")){
							int adminPass = MgrUtil.checkPasswordStrength(hiveap.getCfgPassword());
							complianceResult.setHiveApPass(adminPass);
						}else {
							int reanonlyPass = MgrUtil.checkPasswordStrength(hiveap.getCfgReadOnlyPassword());
							int adminPass = MgrUtil.checkPasswordStrength(hiveap.getCfgPassword());
							complianceResult.setHiveApPass(reanonlyPass>adminPass?adminPass:reanonlyPass);
						}
					}
				}
				if (!compliancePolicy.getPasswordCapwap()){
					complianceResult.setCapwapPass(ComplianceResult.PASSWORD_STRENGTH_NA);
				}else {	
					if (hiveap.getPassPhrase()==null || hiveap.getPassPhrase().equals("")){
						complianceResult.setCapwapPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
					} else {
						int capwapPass = MgrUtil.checkPasswordStrength(hiveap.getPassPhrase());
						complianceResult.setCapwapPass(capwapPass);
					}
				}
				hiveap = findBoById(HiveAp.class, hiveap.getId(), this);
				
				HiveProfile hive = hiveap.getConfigTemplate().getHiveProfile();
				
				if (!compliancePolicy.getPasswordHive()){
					complianceResult.setHivePass(ComplianceResult.PASSWORD_STRENGTH_NA);
				} else {
					if (!hive.getEnabledPassword()){
						complianceResult.setHivePass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
					} else {
						int hivePass = MgrUtil.checkPasswordStrength(hive.getHivePassword());
						complianceResult.setHivePass(hivePass);
					}
				}
				ReportServiceFilter rsf = new ReportServiceFilter(hiveap, compliancePolicy);
				complianceResult.getSsidList().addAll(rsf.initServiceFilter());
//				if (!hiveap.getConfigTemplate().isOverrideTF4IndividualAPs() 
//						&& hiveap.getConfigTemplate().getDeviceServiceFilter()!=null) {
//					ServiceFilter defFilter = hiveap.getConfigTemplate().getDeviceServiceFilter();
//					hiveap.getConfigTemplate().setEth0ServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setEth0BackServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setWireServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setEth1ServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setEth1BackServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setRed0ServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setRed0BackServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setAgg0ServiceFilter(defFilter);
//					hiveap.getConfigTemplate().setAgg0BackServiceFilter(defFilter);
//				}
//				
//				ServiceFilter serviceFilter =hiveap.getConfigTemplate().getEth0ServiceFilter();
//				ServiceFilter serviceFilterBack =hiveap.getConfigTemplate().getEth0BackServiceFilter();
//				if ("BR".equalsIgnoreCase(hiveap.getDeviceInfo().getStringValue(DeviceInfo.DEFAULT_DEVICE_TYPE))) {
//					serviceFilter =hiveap.getConfigTemplate().getWireServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getWireServiceFilter();
//				}
//				ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
//						serviceFilter,serviceFilterBack,compliancePolicy,"eth0");
//				complianceResult.getSsidList().add(ssidListInfo);
//				
//				if (hiveap.isEth1Available() && "AP".equalsIgnoreCase(hiveap.getDeviceInfo().getStringValue(DeviceInfo.DEFAULT_DEVICE_TYPE))){
//					serviceFilter =hiveap.getConfigTemplate().getEth1ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getEth1BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"eth1");
//					complianceResult.getSsidList().add(ssidListInfo);
//					
//					serviceFilter =hiveap.getConfigTemplate().getRed0ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getRed0BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"red0");
//					complianceResult.getSsidList().add(ssidListInfo);
//					
//					serviceFilter =hiveap.getConfigTemplate().getAgg0ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getAgg0BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"agg0");
//					complianceResult.getSsidList().add(ssidListInfo);
//				} else if ("BR".equalsIgnoreCase(hiveap.getDeviceInfo().getStringValue(DeviceInfo.DEFAULT_DEVICE_TYPE))) {
//					dddd
//					
//					serviceFilter =hiveap.getConfigTemplate().getEth1ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getEth1BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"eth1");
//					complianceResult.getSsidList().add(ssidListInfo);
//					
//					serviceFilter =hiveap.getConfigTemplate().getRed0ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getRed0BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"red0");
//					complianceResult.getSsidList().add(ssidListInfo);
//					
//					serviceFilter =hiveap.getConfigTemplate().getAgg0ServiceFilter();
//					serviceFilterBack =hiveap.getConfigTemplate().getAgg0BackServiceFilter();
//					ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"agg0");
//					complianceResult.getSsidList().add(ssidListInfo);
//
//				}
//				
//				for(ConfigTemplateSsid configTemplateSsid :hiveap.getConfigTemplate().getSsidInterfaces().values()){
//					if (configTemplateSsid.getSsidProfile()!=null){
//						SsidProfile sp=configTemplateSsid.getSsidProfile();
//						ssidListInfo = new ComplianceSsidListInfo();
//						ssidListInfo.setSsidName(sp.getSsidName());
//						switch (sp.getAccessMode()){
//							case SsidProfile.ACCESS_MODE_OPEN:
//								if (sp.getMacAuthEnabled()){
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN_AUTH);
//									ssidListInfo.setRating(compliancePolicy.getClientOpenAuth());
//								} else {
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN);
//									ssidListInfo.setRating(compliancePolicy.getClientOpen());
//								}
//								break;
//							case SsidProfile.ACCESS_MODE_WPA:
//								ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PSK);
//								ssidListInfo.setRating(compliancePolicy.getClientPsk());
//								if (!compliancePolicy.getPasswordSSID()){
//									ssidListInfo.setSsidPass(ComplianceResult.PASSWORD_STRENGTH_NA);
//								} else {
//									int ssidPass = MgrUtil.checkPasswordStrength(sp.getSsidSecurity().getFirstKeyValue());
//									ssidListInfo.setSsidPass(ssidPass);
//								}
//								break;
//							case SsidProfile.ACCESS_MODE_PSK:
//								ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PRIVETE_PSK);
//								ssidListInfo.setRating(compliancePolicy.getClientPrivatePsk());
//								break;
//							case SsidProfile.ACCESS_MODE_WEP:
//								ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_WEP);
//								ssidListInfo.setRating(compliancePolicy.getClientWep());
//								break;
//							case SsidProfile.ACCESS_MODE_8021X:
//								ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_8021X);
//								ssidListInfo.setRating(compliancePolicy.getClient8021x());
//								break;
//						}
//						serviceFilter = sp.getServiceFilter();
//						if (serviceFilter.getEnableSSH()){
//							ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
//						} else {
//							ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//						}
//						if (serviceFilter.getEnablePing()){
//							ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
//						} else {
//							ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//						}
//						if (serviceFilter.getEnableTelnet()){
//							ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
//						} else {
//							ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//						}
//						if (serviceFilter.getEnableSNMP()){
//							ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
//						} else {
//							ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//						}
//						complianceResult.getSsidList().add(ssidListInfo);
//					}
//				}
				lstCompliance.add(complianceResult);
			}
		}
		if (getDataSource().getComplianceType()!=AhReport.REPORT_COMPLIANCEPOLICY_ALL){
			List<ComplianceResult> removeClass = new ArrayList<ComplianceResult>();
			for(ComplianceResult tmpClass: lstCompliance){
				if (tmpClass.getSummarySecurity()!= getDataSource().getComplianceType()){
					removeClass.add(tmpClass);
				}
			}
			lstCompliance.removeAll(removeClass);
		}
	}

	public ComplianceSsidListInfo getComplianceSsidListInfo(
			ServiceFilter serviceFilter, ServiceFilter serviceFilterBack,
			CompliancePolicy compliancePolicy,String name){
		ComplianceSsidListInfo ssidListInfo = new ComplianceSsidListInfo();
		ssidListInfo.setSsidName(name);
		ssidListInfo.setRating(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		if (serviceFilter.getEnableSSH()|| serviceFilterBack.getEnableSSH()){
			ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
		} else {
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnablePing()|| serviceFilterBack.getEnablePing()){
			ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
		} else {
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableTelnet()|| serviceFilterBack.getEnableTelnet()){
			ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
		} else {
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableSNMP() || serviceFilterBack.getEnableSNMP()){
			ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
		} else {
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		return ssidListInfo;
	}

	public boolean setClientAuthFilterParam() {
		Calendar reportDateTime = getReportDateTime();
		List<Object> lstCondition = new ArrayList<Object>();
		String searchSQL = "trapTimeStamp.time >=:s1";
		lstCondition.add(reportDateTime.getTimeInMillis());
		int intParam;
		if (getDataSource().getAuthType() == AhReport.REPORT_CLIENTAUTH_AUTH) {
			searchSQL = searchSQL + " and eventType =:s2 and objectType=:s3 and currentState=:s4";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_UP);
			intParam = 5;
		} else if (getDataSource().getAuthType() == AhReport.REPORT_CLIENTAUTH_DEAUTH) {
			searchSQL = searchSQL
					+ " and eventType =:s2 and objectType=:s3 and currentState=:s4 and code!=:s5";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_DOWN);
			// de-auth code
			lstCondition.add(CLIENT_DE_AUTH_CODE);
			intParam = 6;
		} else if (getDataSource().getAuthType() == AhReport.REPORT_CLIENTAUTH_REJECT) {
			searchSQL = searchSQL
					+ " and eventType =:s2 and objectType=:s3 and currentState=:s4 and code=:s5";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_DOWN);
			lstCondition.add(CLIENT_DE_AUTH_CODE);
			intParam = 6;
		} else {
			searchSQL = searchSQL
					+ " and eventType =:s2 and objectType=:s3 and (currentState=:s4 or currentState=:s5)";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_UP);
			lstCondition.add(AhEvent.AH_STATE_DOWN);
			intParam = 6;
		}

		if (getDataSource().getLocation() != null) {
			List<?> mapAP = QueryUtil.executeQuery("select upper(bo.macAddress) from "
					+ HiveAp.class.getSimpleName() + " bo", null, new FilterParams(
					"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and (bo.deviceType=:s3 or bo.deviceType=:s4 or bo.deviceType=:s5)", new Object[] {
							getDataSource().getLocation().getId(), HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_HIVEAP, HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR }));
			if (mapAP.size() > 0) {
				searchSQL = searchSQL + " AND upper(apId) in (:s" + intParam + ")";
				lstCondition.add(mapAP);
			} else {
				searchSQL = searchSQL + " AND apId = :s" + intParam;
				lstCondition.add("notexist");
			}
			intParam++;
		}

		if (getDataSource().getAuthMac() != null && !getDataSource().getAuthMac().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(remoteId) like :s" + intParam;
			lstCondition.add("%" + getDataSource().getAuthMacForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		if (getDataSource().getAuthHostName() != null
				&& !getDataSource().getAuthHostName().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientHostName) like :s" + intParam;
			lstCondition.add("%" + getDataSource().getAuthHostNameForSQL().trim().toLowerCase()
					+ "%");
			intParam++;
		}

		if (getDataSource().getAuthUserName() != null
				&& !getDataSource().getAuthUserName().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientUserName) like :s" + intParam;
			lstCondition.add("%" + getDataSource().getAuthUserNameForSQL().trim().toLowerCase()
					+ "%");
			intParam++;
		}

		if (getDataSource().getAuthIp() != null && !getDataSource().getAuthIp().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientIp) like :s" + intParam;
			lstCondition.add("%" + getDataSource().getAuthIpForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		Object values[] = new Object[lstCondition.size()];
		for (int i = 0; i < lstCondition.size(); i++) {
			values[i] = lstCondition.get(i);
		}

		filterParams = new FilterParams(searchSQL, values);

		return true;
	}
	
	private String currentAPName;
	
	private final List<TrafficData> hiveAPNonComplianceList=new ArrayList<TrafficData>();
	private final List<TrafficData> clientNonComplianceList=new ArrayList<TrafficData>();
	protected void prepareSetHiveApNonCompliance(){
		Map<String,TrafficData> apMap = new HashMap<String, TrafficData>();
		
		Calendar reportDateTime = getReportDateTime();
		FilterParams filterSLAHistory = new FilterParams(
					"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) and lower(apName) like :s4",
					new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
							,AhBandWidthSentinelHistory.STATUS_ALERT, getDataSource().getApNameForSQLTwo()});
		List<?> slaHistory = QueryUtil.executeQuery(
				"select apName,count(apName) from " + AhBandWidthSentinelHistory.class.getSimpleName(),
				null, filterSLAHistory, new GroupByParams(new String[]{"apName"}), getDomain().getId());
		
		for(Object oneObj:slaHistory){
			Object[] oneItem = (Object[]) oneObj;
			if (apMap.get(oneItem[0].toString())==null) {
				TrafficData tmpData = new TrafficData();
				tmpData.setName(oneItem[0].toString());
				tmpData.setSlaCount(Long.parseLong(oneItem[1].toString()));
				apMap.put(oneItem[0].toString(), tmpData);
			} else {
				apMap.get(oneItem[0].toString()).addSlaCount(Long.parseLong(oneItem[1].toString()));
			}
		}
		
		FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2 and lower(apName) like :s3",
				new Object[] { reportDateTime.getTimeInMillis(),0,getDataSource().getApNameForSQLTwo() });
		List<?> interfaceStatsHistory =QueryUtil.executeQuery(
				"select apName,alarmFlag from "
				+ AhInterfaceStats.class.getSimpleName(), null,
				filterInterfaceHistory,getDomain().getId());
		
		for(Object oneObj: interfaceStatsHistory){
			Object[] oneItem = (Object[]) oneObj;
			int argFlg = Integer.parseInt(oneItem[1].toString());
			if (apMap.get(oneItem[0].toString())==null) {
				TrafficData tmpData = new TrafficData();
				tmpData.setName(oneItem[0].toString());
				if ((argFlg & 0x01) ==1) {
					tmpData.setCrcError(1);
				}
				if ((argFlg >>>1 & 0x01) ==1) {
					tmpData.setTxdata(1);
				}
				if ((argFlg >>>2 & 0x01) ==1) {
					tmpData.setRxdata(1);
				}
				if ((argFlg >>>3 & 0x01) ==1) {
					tmpData.setTxRetry(1);
				}
				if ((argFlg >>>4 & 0x01) ==1) {
					tmpData.setAirtime(1);
				}
				apMap.put(oneItem[0].toString(), tmpData);
			} else {
				if ((argFlg & 0x01) ==1) {
					apMap.get(oneItem[0].toString()).addCrcError(1);
				}
				if ((argFlg >>>1 & 0x01) ==1) {
					apMap.get(oneItem[0].toString()).addTxdata(1);
				}
				if ((argFlg >>>2 & 0x01) ==1) {
					apMap.get(oneItem[0].toString()).addRxdata(1);
				}
				if ((argFlg >>>3 & 0x01) ==1) {
					apMap.get(oneItem[0].toString()).addTxRetry(1);
				}
				if ((argFlg >>>4 & 0x01) ==1) {
					apMap.get(oneItem[0].toString()).addAirtime(1);
				}
			}
		}
		
		for(String key:apMap.keySet()){
			hiveAPNonComplianceList.add(apMap.get(key));
		}
		
//		TrafficData t111 = new TrafficData();
//		t111.setName("name1");
//		t111.setSlaCount(1);
//		t111.setCrcError(2);
//		t111.setTxdata(3);
//		t111.setRxdata(4);
//		t111.setTxRetry(5);
//		t111.setAirtime(6);
//		hiveAPNonComplianceList.add(t111);
//		
//		TrafficData t112 = new TrafficData();
//		t112.setName("name2");
//		t112.setSlaCount(11);
//		t112.setCrcError(12);
//		t112.setTxdata(13);
//		t112.setRxdata(14);
//		t112.setTxRetry(15);
//		t112.setAirtime(16);
//		
//		hiveAPNonComplianceList.add(t112);
		
		Collections.sort(hiveAPNonComplianceList, new Comparator<TrafficData>() {
			@Override
			public int compare(TrafficData o1, TrafficData o2) {
				try {
					long a1=o1.getAirtime() + o1.getCrcError() + o1.getRxdata() + o1.getSlaCount() + o1.getTxdata() + o1.getTxRetry();
					long a2=o2.getAirtime() + o2.getCrcError() + o2.getRxdata() + o2.getSlaCount() + o2.getTxdata() + o2.getTxRetry();
					if (a2-a1>0) {
						return 1;
					} else if (a2-a1<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}

	protected void prepareSetClientNonCompliance() {
		Map<String, TrafficData> clientMap = new HashMap<String, TrafficData>();
		Calendar reportDateTime = getReportDateTime();
		FilterParams filterSLAHistory = new FilterParams(
					"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) " +
					"and lower(apName) like :s4 and lower(clientMac) like :s5",
					new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
							,AhBandWidthSentinelHistory.STATUS_ALERT, getDataSource().getApNameForSQLTwo(),
							"%" + getDataSource().getAuthMacForSQL().toLowerCase()+ "%"});
		List<?> slaHistory = QueryUtil.executeQuery(
				"select clientMac,count(clientMac) from " + AhBandWidthSentinelHistory.class.getSimpleName(),
				null, filterSLAHistory, new GroupByParams(new String[]{"clientMac"}), getDomain().getId());
		
		for(Object oneObj:slaHistory){
			Object[] oneItem = (Object[]) oneObj;
			if (clientMap.get(oneItem[0].toString())==null) {
				TrafficData tmpData = new TrafficData();
				tmpData.setName(oneItem[0].toString());
				tmpData.setSlaCount(Long.parseLong(oneItem[1].toString()));
				clientMap.put(oneItem[0].toString(), tmpData);
			} else {
				clientMap.get(oneItem[0].toString()).addSlaCount(Long.parseLong(oneItem[1].toString()));
			}
		}
		
		
		FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and (alarmFlag>:s2 or overallClientHealthScore<:s3) and " +
				"lower(apName) like :s4 and lower(clientMac) like :s5",
				new Object[] { reportDateTime.getTimeInMillis(),0,AhClientSession.CLIENT_SCORE_RED,getDataSource().getApNameForSQLTwo(),
								"%" + getDataSource().getAuthMacForSQL().toLowerCase()+ "%"});
		List<?> clientStatsHistory =QueryUtil.executeQuery(
				"select clientMac,alarmFlag,overallClientHealthScore from "
				+ AhClientStats.class.getSimpleName(), null,
				filterInterfaceHistory,getDomain().getId());
		
		for(Object oneObj: clientStatsHistory){
			Object[] oneItem = (Object[]) oneObj;
			int argFlg = Integer.parseInt(oneItem[1].toString());
			if (clientMap.get(oneItem[0].toString())==null) {
				TrafficData tmpData = new TrafficData();
				tmpData.setName(oneItem[0].toString());
				if ((argFlg & 0x01) ==1) {
					tmpData.setCrcError(1);
				}
				if ((argFlg >>>1 & 0x01) ==1) {
					tmpData.setTxdata(1);
				}
				if ((argFlg >>>2 & 0x01) ==1) {
					tmpData.setRxdata(1);
				}
				if ((argFlg >>>3 & 0x01) ==1) {
					tmpData.setTxRetry(1);
				}
				if ((argFlg >>>4 & 0x01) ==1) {
					tmpData.setAirtime(1);
				}
				if (Integer.parseInt(oneItem[2].toString())<AhClientSession.CLIENT_SCORE_RED){
					tmpData.setScore(1);
				}
				if (tmpData.getAirtime()>0 || tmpData.getScore()>0) {
					clientMap.put(oneItem[0].toString(), tmpData);
				}
			} else {
				if ((argFlg & 0x01) ==1) {
					clientMap.get(oneItem[0].toString()).addCrcError(1);
				}
				if ((argFlg >>>1 & 0x01) ==1) {
					clientMap.get(oneItem[0].toString()).addTxdata(1);
				}
				if ((argFlg >>>2 & 0x01) ==1) {
					clientMap.get(oneItem[0].toString()).addRxdata(1);
				}
				if ((argFlg >>>3 & 0x01) ==1) {
					clientMap.get(oneItem[0].toString()).addTxRetry(1);
				}
				if ((argFlg >>>4 & 0x01) ==1) {
					clientMap.get(oneItem[0].toString()).addAirtime(1);
				}
				if (Integer.parseInt(oneItem[2].toString())<AhClientSession.CLIENT_SCORE_RED){
					clientMap.get(oneItem[0].toString()).addScore(1);
				}
			}
		}
		
		for(String key:clientMap.keySet()){
			clientNonComplianceList.add(clientMap.get(key));
		}
		
//		for(int i=0;i<200; i++) {
//			TrafficData t2 = new TrafficData();
//			t2.setName("name" + i);
//			t2.setSlaCount(i);
//			t2.setCrcError(i);
//			t2.setTxdata(i);
//			t2.setRxdata(i);
//			t2.setTxRetry(i);
//			t2.setAirtime(i);
//			clientNonComplianceList.add(t2);
//		}
		
		Collections.sort(clientNonComplianceList, new Comparator<TrafficData>() {
			@Override
			public int compare(TrafficData o1, TrafficData o2) {
				try {
					long a1=o1.getAirtime() + o1.getScore() + o1.getSlaCount();
					long a2=o2.getAirtime() + o2.getScore() + o2.getSlaCount();
					if (a2-a1>0) {
						return 1;
					} else if (a2-a1<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		
	}
	protected void setClientRadioMode(Calendar firstReportTime,
			long firstReportTimeAggregation) {
		Calendar reportTime = Calendar.getInstance(tz);
		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
		long systemTimeInMillis = System.currentTimeMillis();

		int reportTimeAggregation = (int) firstReportTimeAggregation / 3600000;
		FilterParams filterParams;
		if (getDataSource().getLocation() != null) {
			filterParams = new FilterParams(
					"bo.endTimeStamp>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3", new Object[] {
							reportTime.getTimeInMillis(), getDataSource().getLocation().getId(),
							getDataSource().getApNameForSQLTwo() });
		} else {
			filterParams = new FilterParams("bo.endTimeStamp>=:s1 and lower(bo.apName) like :s2",
					new Object[] { reportTime.getTimeInMillis(), getDataSource().getApNameForSQLTwo() });
		}
		List<?> profiles = QueryUtil.executeQuery(
				"select bo.clientMACProtocol, bo.endTimeStamp,bo.startTimeStamp from "
						+ AhClientSessionHistory.class.getSimpleName() + " bo", new SortParams(
						"endTimeStamp"), filterParams, getDomain().getId());
		Map<String, AhClientCountForAP> clientCount = new HashMap<String, AhClientCountForAP>();

		for (Object profile : profiles) {
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
			reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			while (reportTime.getTimeInMillis() <= systemTimeInMillis) {
				Object[] tmp = (Object[]) profile;
				if (Long.parseLong(tmp[1].toString()) > reportTime.getTimeInMillis()
						&& Long.parseLong(tmp[2].toString()) <= reportTime.getTimeInMillis()) {
					if (clientCount.get(String.valueOf(reportTime.getTimeInMillis())) == null) {
						AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
						if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_AMODE) {
							tmpAhClientCountForAP.setAModeCount(1);
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_BMODE) {
							tmpAhClientCountForAP.setBModeCount(1);
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_GMODE) {
							tmpAhClientCountForAP.setGModeCount(1);
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NAMODE) {
							tmpAhClientCountForAP.setNaModeCount(1);
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NGMODE) {
							tmpAhClientCountForAP.setNgModeCount(1);
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_ACMODE) {
							tmpAhClientCountForAP.setAcModeCount(1);
						}
						tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
						tmpAhClientCountForAP.setTz(tz);
						clientCount.put(String.valueOf(reportTime.getTimeInMillis()),
								tmpAhClientCountForAP);
					} else {
						if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_AMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addAModeCount();
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_BMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addBModeCount();
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_GMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addGModeCount();
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NAMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addNAModeCount();
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NGMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addNGModeCount();
						} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_ACMODE) {
							clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
									.addACModeCount();
						}
					}
				}

				if (clientCount.get(String.valueOf(reportTime.getTimeInMillis())) == null) {
					AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
					tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
					tmpAhClientCountForAP.setTz(tz);
					clientCount.put(String.valueOf(reportTime.getTimeInMillis()),
							tmpAhClientCountForAP);
				}
				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			}
		}

		reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
		FilterParams currentFilterParams;
//		if (getDataSource().getLocation() != null) {
//			currentFilterParams = new FilterParams("bo.mapId=:s1 and lower(bo.apName) like :s2",
//					new Object[] { getDataSource().getLocation().getId(),
//							getDataSource().getApNameForSQLTwo() });
//		} else {
//			currentFilterParams = new FilterParams("lower(bo.apName) like :s1",
//					new Object[] { getDataSource().getApNameForSQLTwo() });
//		}
//		List<?> currentProfiles = QueryUtil.executeQuery(
//				"select bo.clientMACProtocol, bo.startTimeStamp from "
//						+ AhClientSession.class.getSimpleName() + " bo",
//				new SortParams("startTimeStamp"), currentFilterParams, getDomain().getId());
		if (getDataSource().getLocation() != null) {
			currentFilterParams = new FilterParams("mapId=? and lower(apName) like ?",
					new Object[] { getDataSource().getLocation().getId(),
							getDataSource().getApNameForSQLTwo() });
		} else {
			currentFilterParams = new FilterParams("lower(apName) like ?",
					new Object[] { getDataSource().getApNameForSQLTwo() });
		}
		List<?> currentProfiles = DBOperationUtil.executeQuery(
				"select clientMACProtocol, startTimeStamp from ah_clientsession",
				new SortParams("startTimeStamp"), currentFilterParams, getDomain().getId());

		for (Object currentProfile : currentProfiles) {
			Object[] tmp = (Object[]) currentProfile;
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
			reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			while (reportTime.getTimeInMillis() <= systemTimeInMillis) {
				if (Long.parseLong(tmp[1].toString()) > reportTime.getTimeInMillis()) {
					reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
					continue;
				}

				if (clientCount.get(String.valueOf(reportTime.getTimeInMillis())) == null) {
					AhClientCountForAP tmpAhClientCountForAP = new AhClientCountForAP();
					if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_AMODE) {
						tmpAhClientCountForAP.setAModeCount(1);
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_BMODE) {
						tmpAhClientCountForAP.setBModeCount(1);
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_GMODE) {
						tmpAhClientCountForAP.setGModeCount(1);
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NAMODE) {
						tmpAhClientCountForAP.setNaModeCount(1);
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NGMODE) {
						tmpAhClientCountForAP.setNgModeCount(1);
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_ACMODE) {
						tmpAhClientCountForAP.setAcModeCount(1);
					}
					tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
					tmpAhClientCountForAP.setTz(tz);
					clientCount.put(String.valueOf(reportTime.getTimeInMillis()),
							tmpAhClientCountForAP);
				} else {
					if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_AMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addAModeCount();
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_BMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addBModeCount();
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_GMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addGModeCount();
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NAMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addNAModeCount();
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_NGMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addNGModeCount();
					} else if (Integer.parseInt(tmp[0].toString()) == AhAssociation.CLIENTMACPROTOCOL_ACMODE) {
						clientCount.get(String.valueOf(reportTime.getTimeInMillis()))
								.addACModeCount();
					}
				}

				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
			}
		}

		List<AhClientCountForAP> lstClientCount = new ArrayList<AhClientCountForAP>();
		for (AhClientCountForAP tmpClientCount : clientCount.values()) {
			lstClientCount.add(tmpClientCount);
		}

		Collections.sort(lstClientCount, new Comparator<AhClientCountForAP>() {
			@Override
			public int compare(AhClientCountForAP o1, AhClientCountForAP o2) {
				long reportTime1 = o1.getReportTime();
				long reportTime2 = o2.getReportTime();
				return new Long((reportTime1 - reportTime2) / 100000)
						.intValue();
			}
		});

		radioClientCount = lstClientCount;
	}
	
	public void prepareSetHiveApSLA() {
		if (reportAPName != null
				&& !reportAPName.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))
				&& tabIndex.equals("1")) {
			lstHiveAPName = (Set<String>) MgrUtil.getSessionAttribute(REPORT_HIVEAP_LST);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);
		} else if (getDataSource().getReportType().equals("hiveApSla")){
			if (getDataSource().getComplianceType()==AhReport.REPORT_COMPLIANCEPOLICY_ALL){
				String searchSQL = "select id,hostName from " + HiveAp.class.getSimpleName() + " bo where ";
				searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
				searchSQL = searchSQL + " and (deviceType= " + HiveAp.Device_TYPE_HIVEAP + 
						" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
						" or deviceType=" + HiveAp.Device_TYPE_VPN_BR +
						" ) and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA +
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY +
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +						
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P + 
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P + 
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 + 
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 +
						" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
				
				if (getDataSource().getApName() != null && !getDataSource().getApName().equals("")) {
					searchSQL = searchSQL + " and lower(hostName) like '%"
							+ getDataSource().getApNameForSQL().toLowerCase() + "%'";
				}
				if (getDataSource().getLocation() != null) {
					searchSQL = searchSQL + "and mapContainer.id = "
							+ getDataSource().getLocation().getId().toString();
				}
				searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();
				
				List<?> profilesIds = QueryUtil.executeQuery(searchSQL, null, null);
				lstHiveAPName = new TreeSet<String>();
				if (profilesIds == null || profilesIds.size() < 1) {
					lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
					reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");
				} else {
					for (int i = 0; i < profilesIds.size(); i++) {
						Object[] tmp = (Object[]) profilesIds.get(i);
						lstHiveAPName.add(tmp[1].toString());
						if (i == 0) {
							reportAPName = tmp[1].toString();
						}
					}
				}
			} else {
				String slaSQL = "select distinct apname from hm_bandwidthsentinel_history where" +
					" time>=" + getReportDateTime().getTimeInMillis();
				slaSQL = slaSQL + " and owner=" + getDomain().getId();
				
				if (getDataSource().getComplianceType()==AhReport.REPORT_COMPLIANCEPOLICY_POOR){
					slaSQL = slaSQL + " and (bandwidthsentinelstatus=0 or bandwidthsentinelstatus=2)";
				} else if (getDataSource().getComplianceType()==AhReport.REPORT_COMPLIANCEPOLICY_GOOD){
					slaSQL = slaSQL + " and bandwidthsentinelstatus=1 and (action=3 or action=2)";
				}
				List<?> lstSelectSlaAp = QueryUtil.executeNativeQuery(slaSQL);
				lstHiveAPName = new TreeSet<String>();
				if (lstSelectSlaAp!=null && lstSelectSlaAp.size()>0){
					for (int i = 0; i < lstSelectSlaAp.size(); i++) {
						lstHiveAPName.add(lstSelectSlaAp.get(i).toString());
						if (i == 0) {
							reportAPName = lstSelectSlaAp.get(i).toString();
						}
					}
				} else {
					lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
					reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");
				}
			}
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);
		}
		
		lstHiveApSla = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
				new SortParams("timeStamp.time",false),
				new FilterParams("apName=:s1 and timeStamp.time>=:s2",
						new Object[]{reportAPName,getReportDateTime().getTimeInMillis()})
				,getDomain().getId());

		for(AhBandWidthSentinelHistory tmpClass:lstHiveApSla){
			tmpClass.getTimeStamp().setTimeZone(getUserContext().getTimeZone());
		}
		
		MgrUtil.setSessionAttribute(REPORT_SLA_LST, lstHiveApSla);
	}

	public void prepareSelectHiveAP() {
		if (reportAPName != null
				&& !reportAPName.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))
				&& tabIndex.equals("1")) {
			lstHiveAPName = (Set<String>) MgrUtil.getSessionAttribute(REPORT_HIVEAP_LST);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);

			if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS) 
					|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
					|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
				lstReportSsidName = ((Map<String, List<String>>) MgrUtil
						.getSessionAttribute(REPORT_SSIDNAME_MAP)).get(reportAPName);
				MgrUtil.setSessionAttribute(REPORT_SSID_NAME, reportSsidName);
			}
			if (getDataSource().getReportType().equals("meshNeighbors")) {
				lstReportNeighborAP = ((Map<String, List<String>>) MgrUtil
						.getSessionAttribute(REPORT_NEIGHBORAP_MAP)).get(reportAPName);
				MgrUtil.setSessionAttribute(REPORT_NEIGHBORAP_ID, reportNeighborAP);
			}
		} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {

			String searchSQL = "select DISTINCT xifpk.apName, ssidName from " + AhXIf.class.getSimpleName() + " where "
					+ " xifpk.statTimeStamp >=" + getReportDateTime().getTimeInMillis()
					+ " AND lower(xifpk.apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'"
					+ " AND lower(ssidName) like '%"
					+ getDataSource().getSsidNameForSQL().toLowerCase() + "%'";
			searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();

			List<?> profiles = QueryUtil.executeQuery(searchSQL, null, null);
			lstHiveAPName = new TreeSet<String>();
			lstReportSsidName = new ArrayList<String>();
			Map<String, List<String>> ssidNameMap = new HashMap<String, List<String>>();

			if (profiles == null || profiles.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				ssidNameMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			} else {
				for (Object profile : profiles) {
					Object[] tmp = (Object[]) profile;
					if (tmp[1] != null && !tmp[1].toString().equalsIgnoreCase("N/A") && !tmp[1].toString().equals("")) {
						if (lstHiveAPName.size() < 1) {
							reportAPName = tmp[0].toString();
						}
						lstHiveAPName.add(tmp[0].toString());
						
						if (ssidNameMap.get(tmp[0].toString()) == null) {
							List<String> tmpList = new ArrayList<String>();
							tmpList.add(tmp[1].toString());
							ssidNameMap.put(tmp[0].toString(), tmpList);
						} else {
							ssidNameMap.get(tmp[0].toString()).add(tmp[1].toString());
						}
					}
				}
			}

			if (ssidNameMap.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				ssidNameMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			}

			lstReportSsidName = ssidNameMap.get(reportAPName);
			reportSsidName = lstReportSsidName.get(0);

			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);

			MgrUtil.setSessionAttribute(REPORT_SSIDNAME_MAP, ssidNameMap);
			MgrUtil.setSessionAttribute(REPORT_SSID_NAME, reportSsidName);
		} else if (getDataSource().getReportType().equals("meshNeighbors")) {
			List<SimpleHiveAp> allApList = CacheMgmt.getInstance().getAllApList(getDomain().getId());
			StringBuilder filterSwitchSql = new StringBuilder();
			if (allApList!=null && !allApList.isEmpty()) {
				for(SimpleHiveAp simpAp : allApList) {
					if (HiveAp.isSwitchProduct(simpAp.getHiveApModel()) || simpAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_BR200) {
						if (filterSwitchSql.length()==0) {
							filterSwitchSql.append("(");
							filterSwitchSql.append("'").append(simpAp.getMacAddress()).append("'");
						} else {
							filterSwitchSql.append(",").append("'").append(simpAp.getMacAddress()).append("'");
						}
					}
				}
			}
			if (filterSwitchSql.length()>0) {
				filterSwitchSql.append(")");
			}
			
			String searchSQL = "select DISTINCT apName, neighborAPID from " + AhNeighbor.class.getSimpleName() + " where "
					+ " timeStamp.time >=" + getReportDateTime().getTimeInMillis()
					+ " AND lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'";
			searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();
			if (filterSwitchSql.length()>0) {
				searchSQL = searchSQL + " AND apMac not in " + filterSwitchSql.toString();
				searchSQL = searchSQL + " AND neighborAPID not in " + filterSwitchSql.toString();
			}

			List<?> profiles = QueryUtil.executeQuery(searchSQL, null, null);
			lstHiveAPName = new TreeSet<String>();
			lstReportNeighborAP = new ArrayList<String>();
			Map<String, List<String>> neighborAPMap = new HashMap<String, List<String>>();

			if (profiles == null || profiles.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				neighborAPMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			} else {
				for (Object profile : profiles) {
					Object[] tmp = (Object[]) profile;
					if (tmp[1] != null && !tmp[1].toString().equals("")) {
						if (lstHiveAPName.size() < 1) {
							reportAPName = tmp[0].toString();
						}
						lstHiveAPName.add(tmp[0].toString());
						if (neighborAPMap.get(tmp[0].toString()) == null) {
							List<String> tmpList = new ArrayList<String>();
							tmpList.add(tmp[1].toString());
							neighborAPMap.put(tmp[0].toString(), tmpList);
						} else {
							neighborAPMap.get(tmp[0].toString()).add(tmp[1].toString());
						}
					}
				}
			}

			if (neighborAPMap.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				neighborAPMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			}

			lstReportNeighborAP = neighborAPMap.get(reportAPName);
			reportNeighborAP = lstReportNeighborAP.get(0);

			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);

			MgrUtil.setSessionAttribute(REPORT_NEIGHBORAP_MAP, neighborAPMap);
			MgrUtil.setSessionAttribute(REPORT_NEIGHBORAP_ID, reportNeighborAP);
		} else {		
			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select id,hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL = " (deviceType=" + HiveAp.Device_TYPE_HIVEAP +
							" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER +
							" or deviceType=" + HiveAp.Device_TYPE_VPN_BR +") " +
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + 
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P + 
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P + 					
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P + 
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 + 
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 +
					" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (getDataSource().getApName() != null && !getDataSource().getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ getDataSource().getApNameForSQL().toLowerCase() + "%'";
			}

			if (getDataSource().getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ getDataSource().getLocation().getId().toString();
			}

			searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
			
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchAPNameSQL;
			}
			if (!searchLoactionSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchLoactionSQL;
			}
			searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();

			List<?> profilesIds = QueryUtil.executeQuery(searchSQL, null, null);
			lstHiveAPName = new TreeSet<String>();
			if (profilesIds == null || profilesIds.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");
			} else {
				for (int i = 0; i < profilesIds.size(); i++) {
					Object[] tmp = (Object[]) profilesIds.get(i);
					lstHiveAPName.add(tmp[1].toString());
					if (i == 0) {
						reportAPName = tmp[1].toString();
					}
				}
			}
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);
		}
	}

	// RadioTrafficMetrics data
	private List<CheckItem> rtm_trans_wifi0_totalData = null;
	private List<CheckItem> rtm_trans_wifi0_beData = null;
	private List<CheckItem> rtm_trans_wifi0_bgData = null;
	private List<CheckItem> rtm_trans_wifi0_viData = null;
	private List<CheckItem> rtm_trans_wifi0_voData = null;
	private List<CheckItem> rtm_trans_wifi0_unicastData = null;
	private List<CheckItem> rtm_trans_wifi0_multicastData = null;
	private List<CheckItem> rtm_trans_wifi0_broadcastData = null;
	private List<CheckItem> rtm_trans_wifi0_nonBeaconMgtData = null;
	private List<CheckItem> rtm_trans_wifi0_beaconData = null;
	private List<CheckItem> rtm_rec_wifi0_totalData = null;
	private List<CheckItem> rtm_rec_wifi0_unicastData = null;
	private List<CheckItem> rtm_rec_wifi0_multicastData = null;
	private List<CheckItem> rtm_rec_wifi0_broadcastData = null;
	private List<CheckItem> rtm_rec_wifi0_mgtData = null;
	private List<CheckItem> rtm_trans_wifi1_totalData = null;
	private List<CheckItem> rtm_trans_wifi1_beData = null;
	private List<CheckItem> rtm_trans_wifi1_bgData = null;
	private List<CheckItem> rtm_trans_wifi1_viData = null;
	private List<CheckItem> rtm_trans_wifi1_voData = null;
	private List<CheckItem> rtm_trans_wifi1_unicastData = null;
	private List<CheckItem> rtm_trans_wifi1_multicastData = null;
	private List<CheckItem> rtm_trans_wifi1_broadcastData = null;
	private List<CheckItem> rtm_trans_wifi1_nonBeaconMgtData = null;
	private List<CheckItem> rtm_trans_wifi1_beaconData = null;
	private List<CheckItem> rtm_rec_wifi1_totalData = null;
	private List<CheckItem> rtm_rec_wifi1_unicastData = null;
	private List<CheckItem> rtm_rec_wifi1_multicastData = null;
	private List<CheckItem> rtm_rec_wifi1_broadcastData = null;
	private List<CheckItem> rtm_rec_wifi1_mgtData = null;
	// radio interference
	private List<CheckItem> wifi0_averageTxCu=null;
	private List<CheckItem> wifi0_averageRxCu=null;
	private List<CheckItem> wifi0_averageInterferenceCu=null;
	private List<CheckItem> wifi0_averageNoiseFloor=null;
	private List<CheckItem> wifi0_shortTermTxCu=null;
	private List<CheckItem> wifi0_shortTermRxCu=null;
	private List<CheckItem> wifi0_shortTermInterferenceCu=null;
	private List<CheckItem> wifi0_shortTermNoiseFloor=null;
	private List<CheckItem> wifi0_snapShotTxCu=null;
	private List<CheckItem> wifi0_snapShotRxCu=null;
	private List<CheckItem> wifi0_snapShotInterferenceCu=null;
	private List<CheckItem> wifi0_snapShotNoiseFloor=null;
	private List<CheckItem> wifi0_crcError=null;

	private List<CheckItem> wifi1_averageTxCu=null;
	private List<CheckItem> wifi1_averageRxCu=null;
	private List<CheckItem> wifi1_averageInterferenceCu=null;
	private List<CheckItem> wifi1_averageNoiseFloor=null;
	private List<CheckItem> wifi1_shortTermTxCu=null;
	private List<CheckItem> wifi1_shortTermRxCu=null;
	private List<CheckItem> wifi1_shortTermInterferenceCu=null;
	private List<CheckItem> wifi1_shortTermNoiseFloor=null;
	private List<CheckItem> wifi1_snapShotTxCu=null;
	private List<CheckItem> wifi1_snapShotRxCu=null;
	private List<CheckItem> wifi1_snapShotInterferenceCu=null;
	private List<CheckItem> wifi1_snapShotNoiseFloor=null;
	private List<CheckItem> wifi1_crcError=null;

	// ChannelPowerNoise data
	private List<CheckItem> rcpn_wifi0_channel = null;
	private List<CheckItem> rcpn_wifi1_channel = null;
	private List<CheckItem> rcpn_wifi0_power = null;
	private List<CheckItem> rcpn_wifi1_power = null;
	private List<CheckItem> rcpn_wifi0_noise = null;
	private List<CheckItem> rcpn_wifi1_noise = null;
	
	// new ChannelPowerNoise date, for new Hive AP reoprt
	private List<String> hiveap_wifi0_rec_rateTypeList=null;
	private List<String> hiveap_wifi0_rec_dateTimeList=null;
	private List<String> hiveap_wifi0_trans_rateTypeList=null;
	private List<String> hiveap_wifi0_trans_dateTimeList=null;
	private Map<String, List<CheckItem>> hiveap_wifi0_rec_rate_dis=null;
	private List<TextItem> hiveap_wifi0_rec_rate_succ_dis=null;
	private Map<String, List<CheckItem>> hiveap_wifi0_trans_rate_dis=null;
	private List<TextItem> hiveap_wifi0_trans_rate_succ_dis=null;
	
//	private List<TextItem> hiveap_wifi0_trans_rate_total_succ_dis=null;
//	private List<TextItem> hiveap_wifi0_rec_rate_total_succ_dis=null;
	
	private List<CheckItem> hiveap_wifi0_rec_unicast=null;
	private List<CheckItem> hiveap_wifi0_trans_unicast=null;
	private List<CheckItem> hiveap_wifi0_rec_broadcast=null;
	private List<CheckItem> hiveap_wifi0_trans_broadcast=null;
	private List<CheckItem> hiveap_wifi0_rec_drops=null;
	private List<CheckItem> hiveap_wifi0_trans_drops=null;
	private List<CheckItem> hiveap_wifi0_rec_totalU=null;
	private List<CheckItem> hiveap_wifi0_trans_totalU=null;
	private List<CheckItem> hiveap_wifi0_rec_retryRateU=null;
	private List<CheckItem> hiveap_wifi0_trans_retryRateU=null;
	private List<CheckItem> hiveap_wifi0_rec_airTimeU=null;
	private List<CheckItem> hiveap_wifi0_trans_airTimeU=null;
	private List<CheckItem> hiveap_wifi0_crcErrorRateU=null;
	private List<CheckItem> hiveap_wifi0_totalChannelU=null;
	private List<CheckItem> hiveap_wifi0_InterferenceU=null;
	private List<CheckItem> hiveap_wifi0_noiseFloor=null;
	private List<CheckItem> hiveap_wifi0_bandsteering=null;
	private List<CheckItem> hiveap_wifi0_loadbalance=null;
	private List<CheckItem> hiveap_wifi0_weaksnr=null;
	private List<CheckItem> hiveap_wifi0_safetynet=null;
	private List<CheckItem> hiveap_wifi0_proberequest=null;
	private List<CheckItem> hiveap_wifi0_authrequest=null;
	
	private List<String> hiveap_wifi1_rec_rateTypeList=null;
	private List<String> hiveap_wifi1_rec_dateTimeList=null;
	private List<String> hiveap_wifi1_trans_rateTypeList=null;
	private List<String> hiveap_wifi1_trans_dateTimeList=null;
	private Map<String, List<CheckItem>> hiveap_wifi1_rec_rate_dis=null;
	private List<TextItem> hiveap_wifi1_rec_rate_succ_dis=null;
	private Map<String, List<CheckItem>> hiveap_wifi1_trans_rate_dis=null;
	private List<TextItem> hiveap_wifi1_trans_rate_succ_dis=null;
	
//	private List<TextItem> hiveap_wifi1_trans_rate_total_succ_dis=null;
//	private List<TextItem> hiveap_wifi1_rec_rate_total_succ_dis=null;
	
	private List<CheckItem> hiveap_wifi1_rec_unicast=null;
	private List<CheckItem> hiveap_wifi1_trans_unicast=null;
	private List<CheckItem> hiveap_wifi1_rec_broadcast=null;
	private List<CheckItem> hiveap_wifi1_trans_broadcast=null;
	private List<CheckItem> hiveap_wifi1_rec_drops=null;
	private List<CheckItem> hiveap_wifi1_trans_drops=null;
	private List<CheckItem> hiveap_wifi1_rec_totalU=null;
	private List<CheckItem> hiveap_wifi1_trans_totalU=null;
	private List<CheckItem> hiveap_wifi1_rec_retryRateU=null;
	private List<CheckItem> hiveap_wifi1_trans_retryRateU=null;
	private List<CheckItem> hiveap_wifi1_rec_airTimeU=null;
	private List<CheckItem> hiveap_wifi1_trans_airTimeU=null;
	private List<CheckItem> hiveap_wifi1_crcErrorRateU=null;
	private List<CheckItem> hiveap_wifi1_totalChannelU=null;
	private List<CheckItem> hiveap_wifi1_InterferenceU=null;
	private List<CheckItem> hiveap_wifi1_noiseFloor=null;
	private List<CheckItem> hiveap_wifi1_bandsteering=null;
	private List<CheckItem> hiveap_wifi1_loadbalance=null;
	private List<CheckItem> hiveap_wifi1_weaksnr=null;
	private List<CheckItem> hiveap_wifi1_safetynet=null;
	private List<CheckItem> hiveap_wifi1_proberequest=null;
	private List<CheckItem> hiveap_wifi1_authrequest=null;
	

	// RadioTroubleShooting data
	private List<CheckItem> rts_trans_wifi0_totalRetries = null;
	private List<CheckItem> rts_trans_wifi1_totalRetries = null;
	private List<CheckItem> rts_trans_wifi0_totalFramesDropped = null;
	private List<CheckItem> rts_trans_wifi1_totalFramesDropped = null;
	private List<CheckItem> rts_trans_wifi0_totalFrameErrors = null;
	private List<CheckItem> rts_trans_wifi1_totalFrameErrors = null;
	private List<CheckItem> rts_trans_wifi0_feForExcessiveHWRetries = null;
	private List<CheckItem> rts_trans_wifi1_feForExcessiveHWRetries = null;
	private List<CheckItem> rts_trans_wifi0_rtsFailures = null;
	private List<CheckItem> rts_trans_wifi1_rtsFailures = null;
	private List<CheckItem> rts_rec_wifi0_totalFrameDropped = null;
	private List<CheckItem> rts_rec_wifi1_totalFrameDropped = null;

	// SsidTrafficMetrics data
	private List<CheckItem> stm_trans_totalData = null;
	private List<CheckItem> stm_trans_beData = null;
	private List<CheckItem> stm_trans_bgData = null;
	private List<CheckItem> stm_trans_viData = null;
	private List<CheckItem> stm_trans_voData = null;
	private List<CheckItem> stm_trans_unicastData = null;
	private List<CheckItem> stm_trans_multicastData = null;
	private List<CheckItem> stm_trans_broadcastData = null;
	private List<CheckItem> stm_rec_totalData = null;
	private List<CheckItem> stm_rec_unicastData = null;
	private List<CheckItem> stm_rec_multicastData = null;
	private List<CheckItem> stm_rec_broadcastData = null;

	// SsidTroubleShooting data
	private List<CheckItem> sts_trans_totalFramesDropped = null;
	private List<CheckItem> sts_trans_totalFrameErrors = null;
	private List<CheckItem> sts_rec_totalFramesDropped = null;
	private List<CheckItem> sts_rec_totalFrameErrors = null;

	// mostClientsAPs
	private List<List<AhClientCountForAP>> fiveMaxClientCount = null;

	// radioClientCount
	private List<AhClientCountForAP> radioClientCount = null;

	// rogueClients
	private List<CheckItem> uniqueClients = null;

	// rogueAPs
	private List<CheckItem> rogueAPs = null;

	// rogueClients
	private List<CheckItem> rogueClients = null;
	
	// maxClients
	private List<CheckItem> maxClients = null;

	// mesh neighbors
	private List<CheckItem> mesh_trans_totalData = null;
	private List<CheckItem> mesh_trans_beData = null;
	private List<CheckItem> mesh_trans_bgData = null;
	private List<CheckItem> mesh_trans_viData = null;
	private List<CheckItem> mesh_trans_voData = null;
	private List<CheckItem> mesh_trans_mgtData = null;
	private List<CheckItem> mesh_trans_unicastData = null;
	private List<CheckItem> mesh_rec_totalData = null;
	private List<CheckItem> mesh_rec_mgtData = null;
	private List<CheckItem> mesh_rec_unicastData = null;
	private List<CheckItem> mesh_rec_multicastData = null;
	private List<CheckItem> mesh_rec_broadcastData = null;
	private List<CheckItem> mesh_rssiData = null;

	// clientSession
	private List<CheckItem> client_trans_totalData = null;
	private List<CheckItem> client_trans_beData = null;
	private List<CheckItem> client_trans_bgData = null;
	private List<CheckItem> client_trans_viData = null;
	private List<CheckItem> client_trans_voData = null;
	private List<CheckItem> client_trans_mgtData = null;
	private List<CheckItem> client_trans_unicastData = null;
	private List<CheckItem> client_trans_dataOctets = null;
	private List<CheckItem> client_trans_lastrate = null;
	private List<CheckItem> client_rec_totalData = null;
	private List<CheckItem> client_rec_mgtData = null;
	private List<CheckItem> client_rec_unicastData = null;
	private List<CheckItem> client_rec_multicastData = null;
	private List<CheckItem> client_rec_broadcastData = null;
	private List<CheckItem> client_rec_micfailures = null;
	private List<CheckItem> client_rec_dataOctets = null;
	private List<CheckItem> client_rec_lastrate = null;
	private List<CheckItem> client_rssi = null;
	private List<CheckItem> client_signal_to_noise = null;
	
	private List<CheckItem> client_rec_drop=null;
	private List<CheckItem> client_trans_drop=null;
	private List<CheckItem> client_bandwidth=null;
	private List<CheckItem> client_slacount=null;
	private List<CheckItem> client_rec_airTime=null;
	private List<CheckItem> client_trans_airTime=null;
	
	private List<CheckItem> client_score=null;
	private List<CheckItem> client_radio_score=null;
	private List<CheckItem> client_ipnetwork_score=null;
	private List<CheckItem> client_application_score=null;

	private List<String> client_rec_rateTypeList=null;
	private List<String> client_rec_dateTimeList=null;
	private List<String> client_trans_rateTypeList=null;
	private List<String> client_trans_dateTimeList=null;
 	
	private Map<String, List<CheckItem>> client_rec_rate_dis=null;
	private List<TextItem> client_rec_rate_succ_dis=null;
	private Map<String, List<CheckItem>> client_trans_rate_dis=null;
	
	private List<TextItem> client_trans_rate_succ_dis=null;
//	private List<TextItem> client_trans_total_rate_succ_dis=null;
//	private List<TextItem> client_rec_total_rate_succ_dis=null;

	private String clientIp = "";
	private String clientUserName = "";
	private String clientBSSID="";
	private String clientHostName = "";
	private String clientApMac = "";
	private String clientApName = "";
	private String clientSSID = "";
	private String clientVLAN = "";
	private String clientUserProfile = "";
	private String clientChannel = "";
	private String clientAuthMethod = "";
	private String clientEncryptionMethod = "";
	private String clientPhysicalMode = "";
	private String clientCWPUsed = "";
	private String clientLinkUpTime = "";

	// inventory
	private List<HiveAp> lstInventory = null;

	// client Vendor
	private List<CheckItem> lstClientVendorCount = null;
	
	private List<APConnectHistoryInfo> lstHiveApConnection= null;
	
	private final List<ComplianceResult> lstCompliance = new ArrayList<ComplianceResult>();

	// inventory
	private List<AhEvent> lstClientAuth = null;
	
	// HiveApSla
	private List<CheckItem> lstHiveApSlaBad = new ArrayList<CheckItem>();
	private List<CheckItem> lstHiveApSlaAlert = new ArrayList<CheckItem>();
	
	// ClientSla
	private List<CheckItem> lstClientSlaBad = new ArrayList<CheckItem>();
	private List<CheckItem> lstClientSlaAlert = new ArrayList<CheckItem>();

	private List<AhBandWidthSentinelHistory> lstHiveApSla;
	
//	public static String hmpassStrength;
	
	// AirTime
	private List<TextItem> receive_airTime;
	private List<TextItem> transmit_airTime;

	private List<TextItem> wifi1_receive_airTime;
	private List<TextItem> wifi1_transmit_airTime;

	public Calendar getReportDateTime() {
		int reportDay = 0;
		int reportMonth = 0;
		switch (getDataSource().getReportPeriod()) {
		case AhReport.REPORT_PERIOD_LASTONEDAY:
			reportDay = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWODAYS:
			reportDay = 2;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEDAYS:
			reportDay = 3;
			break;
		case AhReport.REPORT_PERIOD_LASTONEWEEK:
			reportDay = 7;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOWEEKS:
			reportDay = 14;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEWEEKS:
			reportDay = 21;
			break;
		case AhReport.REPORT_PERIOD_LASTONEMONTH:
			reportMonth = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOMONTHs:
			reportMonth = 2;
			break;
		}
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		return calendar;
	}

	public long getReportTimeAggregation() {
		int reportHour;
		switch (getDataSource().getTimeAggregation()) {
		case AhReport.TIME_AGGREGATION_FOURHOURS:
			reportHour = 4;
			break;
		case AhReport.TIME_AGGREGATION_EIGHTHOURS:
			reportHour = 8;
			break;
		case AhReport.TIME_AGGREGATION_ONEDAY:
			reportHour = 24;
			break;
		case AhReport.TIME_AGGREGATION_TWODAYS:
			reportHour = 48;
			break;
		case AhReport.TIME_AGGREGATION_ONEWEEK:
			reportHour = 168;
			break;
		case AhReport.TIME_AGGREGATION_TWOWEEKS:
			reportHour = 336;
			break;
		default:
			reportHour = 1;

		}
		return reportHour * 3600000;
	}

	public void initFlashData() throws Exception {
		Calendar reportDateTime = getReportDateTime();
		long reportTimeAggregation = getReportTimeAggregation();

		if (getDataSource().getReportType().equals(L2_FEATURE_RADIOTRAFFICMETRICS)) {
			setRadioTrafficMetrics(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_CHANNELPOWERNOISE)) {
			if (getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_NEW){
				setChannelPowerNoiseNew(reportDateTime, reportTimeAggregation);
			} else {
				setChannelPowerNoise(reportDateTime, reportTimeAggregation);
			}
		} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOTROUBLESHOOTING)) {
			setRadioTroubleShooting(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOINTERFERENCE)) {
			setRadioInterference(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS)) {
			setSsidTrafficMetrics(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)) {
			setSsidTroubleShooting(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals("securityRogueAPs")) {
			setSecurityRogueAPs(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals("securityRogueClients")) {
			setSecurityRogueClients(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals("meshNeighbors")) {
			setMeshNeighbors(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_UNIQUECLIENTCOUNT)) {
			setUniqueClientCount(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
			setClientSessionInfo();
			if(getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_NEW){
				setClientSessionNew();
			} else {
				setClientSession();
			}
		} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME)) {
			setClientAirTime(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOAIRTIME)) {
			setRadioAirTime(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
			setSsidAirTime(reportDateTime, reportTimeAggregation);
		} else if (getDataSource().getReportType().equals("clientCount")) {
			setClientRadioMode(reportDateTime, reportTimeAggregation);
		}else if (getDataSource().getReportType().equals("hiveApSla")) {
			setHiveAPSlaInfo(reportDateTime,reportTimeAggregation);
		}else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
			setClientSlaInfo(reportDateTime,reportTimeAggregation);
		}else if (getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)) {
			setMaxClientReport(reportDateTime,reportTimeAggregation);
		}
	}
	
	public void setClientSlaInfo(Calendar reportDateTime,long reportTimeAggregation){
		lstClientSlaBad = new ArrayList<CheckItem>();
		lstClientSlaAlert = new ArrayList<CheckItem>();
		if (lstHiveApSla!=null){
			Calendar tmpDate = Calendar.getInstance(tz);
			tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
			long reportTimeInMillis = reportDateTime.getTimeInMillis();
			long systemTimeInLong = System.currentTimeMillis()+ 1000;
			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			do {
				int badCount = 0;
				int alertCount=0;

				for(AhBandWidthSentinelHistory historyClass:lstHiveApSla){
					if (historyClass.getTimeStamp().getTime()>=reportTimeInMillis - reportTimeAggregation && 
							historyClass.getTimeStamp().getTime()<reportTimeInMillis ){
						if (historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_BAD ||
								historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_ALERT){
							badCount ++;
						}
						if (historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_CLEAR &&
								(historyClass.getAction() == AhBandWidthSentinelHistory.ACTION_STATUS_BOOST_YES)){
							alertCount ++;
						}
					}
				}
				lstClientSlaBad.add(new CheckItem((long) badCount,
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeInMillis,tz)));
				lstClientSlaAlert.add(new CheckItem((long) alertCount,
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeInMillis,tz)));
				reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			}while (reportTimeInMillis <= systemTimeInLong);
		}
	}	
	
	public void setHiveAPSlaInfo(Calendar reportDateTime,long reportTimeAggregation){
		lstHiveApSlaBad = new ArrayList<CheckItem>();
		lstHiveApSlaAlert = new ArrayList<CheckItem>();
		if (lstHiveApSla!=null){
			Calendar tmpDate = Calendar.getInstance(tz);
			tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
			long reportTimeInMillis = reportDateTime.getTimeInMillis();
			long systemTimeInLong = System.currentTimeMillis() + 1000;
			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			do {
				Set<String> badCount=new HashSet<String>();
				Set<String> alertCount=new HashSet<String>();

				for(AhBandWidthSentinelHistory historyClass:lstHiveApSla){
					if (historyClass.getTimeStamp().getTime()>=reportTimeInMillis - reportTimeAggregation && 
							historyClass.getTimeStamp().getTime()<reportTimeInMillis ){
						if (historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_BAD ||
								historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_ALERT){
							badCount.add(historyClass.getClientMac());
						}
						if (historyClass.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_CLEAR && 
								(historyClass.getAction() == AhBandWidthSentinelHistory.ACTION_STATUS_BOOST_YES)){
							alertCount.add(historyClass.getClientMac());
						}
					}
				}
				lstHiveApSlaBad.add(new CheckItem((long) badCount.size(),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeInMillis,tz)));
				lstHiveApSlaAlert.add(new CheckItem((long) alertCount.size(),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeInMillis,tz)));
				reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			} while (reportTimeInMillis <= systemTimeInLong);
		}
	}

	public void setClientAirTime(Calendar reportDateTime, long reportTimeAggregation) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "timeStamp.time >= :s1 AND clientMac=:s2";

		Object values[] = new Object[2];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = reportClientMac;

		List<AhAssociation> lstInterfaceInfo = QueryUtil.executeQuery(AhAssociation.class, new SortParams("timeStamp.time"),
				new FilterParams(searchSQL, values), getDomain().getId());
		AhAssociation tmpPreviousValue = new AhAssociation();
		AhAssociation tmpNeedSaveValue = new AhAssociation();
		transmit_airTime = new ArrayList<TextItem>();
		receive_airTime = new ArrayList<TextItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		if (lstInterfaceInfo.size() > 0) {
			tmpPreviousValue = lstInterfaceInfo.get(0);
		}
		while (reportTimeInMillis <= systemTimeInLong) {
			for (AhAssociation ahAssociation : lstInterfaceInfo) {
				if (ahAssociation.getTimeStamp().getTime() <= reportTimeInMillis
						- reportTimeAggregation) {
					continue;
				}
				if (ahAssociation.getTimeStamp().getTime() > reportTimeInMillis) {
					break;
				} else {
					// transmit airTime
					double tmpCount = checkValueLessThanZero(
							ahAssociation.getClientTxAirtime(), tmpPreviousValue
									.getClientTxAirtime());
					tmpNeedSaveValue.setClientTxAirtime(tmpNeedSaveValue.getClientTxAirtime()
							+ tmpCount);
					tmpPreviousValue.setClientTxAirtime(ahAssociation.getClientTxAirtime());
					// receive airTime
					tmpCount = checkValueLessThanZero(ahAssociation.getClientRxAirtime(),
							tmpPreviousValue.getClientRxAirtime());
					tmpNeedSaveValue.setClientRxAirtime(tmpNeedSaveValue.getClientRxAirtime()
							+ tmpCount);
					tmpPreviousValue.setClientRxAirtime(ahAssociation.getClientRxAirtime());
				}
			}

			transmit_airTime.add(new TextItem(df.format(tmpNeedSaveValue
					.getClientTxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));
			receive_airTime.add(new TextItem(df.format(tmpNeedSaveValue
					.getClientRxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpNeedSaveValue = new AhAssociation();
		}
	}

	public void setSsidAirTime(Calendar reportDateTime, long reportTimeAggregation) {
		DecimalFormat df = new DecimalFormat("0.00");
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//		Object values[] = new Object[3];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = reportSsidName;
//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		
		String sql ="select x.ifname,v.statTimeStamp,v.txVifAirtime,v.rxVifAirtime from hm_xif x, HM_VIFSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(reportSsidName) +
				"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";

		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
		AhVIfStats tmpPreviousValue = new AhVIfStats();
		AhVIfStats tmpPreviousValueSec = new AhVIfStats();
		AhVIfStats tmpNeedSaveValue = new AhVIfStats();
		transmit_airTime = new ArrayList<TextItem>();
		receive_airTime = new ArrayList<TextItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		boolean firstSave= false;
		String firstIfName= "";
		String secondIfName= "";
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			
			if (!firstSave) {
//				tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpPreviousValue.setTxVifAirtime(Double.parseDouble(oneItem[2].toString()));
				tmpPreviousValue.setRxVifAirtime(Double.parseDouble(oneItem[3].toString()));
				firstSave = true;
				firstIfName = oneItem[0].toString();
				continue;
			}
			if (firstSave && secondIfName.equals("")) {
				if (!oneItem[0].toString().equals(firstIfName)) {
//					tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
					tmpPreviousValueSec.setTxVifAirtime(Double.parseDouble(oneItem[2].toString()));
					tmpPreviousValueSec.setRxVifAirtime(Double.parseDouble(oneItem[3].toString()));
					secondIfName = oneItem[0].toString();
					break;
				}
			}
		}
		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				double tx= Double.parseDouble(oneItem[2].toString());
				double rx= Double.parseDouble(oneItem[3].toString());
				if (cuTime <= reportTimeInMillis - reportTimeAggregation) {
					continue;
				}
				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					double tmpCount;
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(tx, tmpPreviousValue.getTxVifAirtime());
						tmpPreviousValue.setTxVifAirtime(tx);
					} else {
						tmpCount = checkValueLessThanZero(tx, tmpPreviousValueSec.getTxVifAirtime());
						tmpPreviousValueSec.setTxVifAirtime(tx);
					}
					tmpNeedSaveValue.setTxVifAirtime(tmpNeedSaveValue.getTxVifAirtime()
							+ tmpCount);

					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rx, tmpPreviousValue.getRxVifAirtime());
						tmpPreviousValue.setRxVifAirtime(rx);
					} else {
						tmpCount = checkValueLessThanZero(rx, tmpPreviousValue.getRxVifAirtime());
						tmpPreviousValueSec.setRxVifAirtime(rx);
					}
					tmpNeedSaveValue.setRxVifAirtime(tmpNeedSaveValue.getRxVifAirtime()
							+ tmpCount);
				}
			}

			transmit_airTime.add(new TextItem(df.format(tmpNeedSaveValue
					.getTxVifAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));
			receive_airTime.add(new TextItem(df.format(tmpNeedSaveValue
					.getRxVifAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpNeedSaveValue = new AhVIfStats();
		}
	}

	public void setRadioAirTime(Calendar reportDateTime, long reportTimeAggregation) {
		DecimalFormat df = new DecimalFormat("0.00");
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//		Object values[] = new Object[4];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = "wifi0";
//		values[3] = "wifi1";

//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		
		String sql ="select x.ifname,v.statTimeStamp,v.radioTxAirtime,v.radioRxAirtime from hm_xif x, HM_RADIOSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) +
				"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
				"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
		
		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
		
		AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
		AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();

		receive_airTime = new ArrayList<TextItem>();
		transmit_airTime = new ArrayList<TextItem>();

		wifi1_receive_airTime = new ArrayList<TextItem>();
		wifi1_transmit_airTime = new ArrayList<TextItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();

		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//				tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi0PreviousValue.setRadioTxAirtime(Double.parseDouble(oneItem[2].toString()));
				tmpWifi0PreviousValue.setRadioRxAirtime(Double.parseDouble(oneItem[3].toString()));
				break;
			}
		}
		
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi1")) {
//				tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi1PreviousValue.setRadioTxAirtime(Double.parseDouble(oneItem[2].toString()));
				tmpWifi1PreviousValue.setRadioRxAirtime(Double.parseDouble(oneItem[3].toString()));
				break;
			}
		}

		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				double tx= Double.parseDouble(oneItem[2].toString());
				double rx= Double.parseDouble(oneItem[3].toString());
				
				if (cuTime <= reportTimeInMillis - reportTimeAggregation) {
					continue;
				}
				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					if (cuIfname.equalsIgnoreCase("wifi0")) {
						// transmit wifi0_airTime
						double tmpCount = checkValueLessThanZero(tx, tmpWifi0PreviousValue
								.getRadioTxAirtime());
						tmpWifi0NeedSaveValue.setRadioTxAirtime(tmpWifi0NeedSaveValue
								.getRadioTxAirtime()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxAirtime(tx);
						// receive wifi0_airTime
						tmpCount = checkValueLessThanZero(rx, tmpWifi0PreviousValue
								.getRadioRxAirtime());
						tmpWifi0NeedSaveValue.setRadioRxAirtime(tmpWifi0NeedSaveValue
								.getRadioRxAirtime()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxAirtime(rx);
					} else if (cuIfname.equals("wifi1")) {
						// transmit wifi1_airTime
						double tmpCount = checkValueLessThanZero(tx, tmpWifi1PreviousValue
								.getRadioTxAirtime());
						tmpWifi1NeedSaveValue.setRadioTxAirtime(tmpWifi1NeedSaveValue
								.getRadioTxAirtime()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxAirtime(tx);
						// receive wifi1_airTime
						tmpCount = checkValueLessThanZero(rx, tmpWifi1PreviousValue
								.getRadioRxAirtime());
						tmpWifi1NeedSaveValue.setRadioRxAirtime(tmpWifi1NeedSaveValue
								.getRadioRxAirtime()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxAirtime(rx);
					}
				}

			}
			receive_airTime.add(new TextItem(df.format(tmpWifi0NeedSaveValue.
					getRadioRxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));
			transmit_airTime.add(new TextItem(df.format(tmpWifi0NeedSaveValue.
					getRadioTxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));
			wifi1_receive_airTime.add(new TextItem(df.format(tmpWifi1NeedSaveValue
					.getRadioRxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));
			wifi1_transmit_airTime.add(new TextItem(df.format(tmpWifi1NeedSaveValue
					.getRadioTxAirtime()*100/reportTimeAggregation),
					AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz)));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpWifi0NeedSaveValue = new AhRadioStats();
			tmpWifi1NeedSaveValue = new AhRadioStats();
		}
	}

	public void setClientSessionInfo() throws Exception {
		if (reportClientSession.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
			return;
		}
		String[] sessionTime = reportClientSession.split("\\|");
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		l_sdf.setTimeZone(tz);
		Object values[] = new Object[3];
		values[0] = l_sdf.parse(sessionTime[0]).getTime();
		values[1] = l_sdf.parse(sessionTime[1]).getTime();
		values[2] = reportClientMac;

		String strField = "clientip,clientusername,clienthostname,apmac,apname,clientssid,clientvlan,clientuserprofid,clientchannel,clientauthmethod,clientencryptionmethod,clientmacprotocol,clientcwpused,clientbssid";

		String searchSQLSession = "select " + strField + " from ah_clientsession_history_"
				+ getTempTableSuffix() + " where endtimestamp > "
				+ l_sdf.parse(sessionTime[0]).getTime() + " AND starttimestamp <"
				+ l_sdf.parse(sessionTime[1]).getTime() + " AND clientmac='" + reportClientMac + "'"
				+ " AND owner=" + getDomain().getId();
		List<?> lstInterfaceSessionInfo = QueryUtil.executeNativeQuery(searchSQLSession);

		if (lstInterfaceSessionInfo != null && lstInterfaceSessionInfo.size() > 0) {
			AhClientSessionHistory tmpSessionHistory = new AhClientSessionHistory();
			Object[] searchResult = (Object[]) lstInterfaceSessionInfo.get(0);
			tmpSessionHistory.setClientIP(searchResult[0].toString());
			tmpSessionHistory.setClientUsername(searchResult[1].toString());
			tmpSessionHistory.setClientHostname(searchResult[2].toString());
			tmpSessionHistory.setApMac(searchResult[3].toString());
			tmpSessionHistory.setApName(searchResult[4].toString());
			tmpSessionHistory.setClientSSID(searchResult[5].toString());
			tmpSessionHistory.setClientVLAN(Integer.parseInt(searchResult[6].toString()));
			tmpSessionHistory.setClientUserProfId(Integer.parseInt(searchResult[7].toString()));
			tmpSessionHistory.setClientChannel(Integer.parseInt(searchResult[8].toString()));
			tmpSessionHistory.setClientAuthMethod(Byte.parseByte(searchResult[9].toString()));
			tmpSessionHistory
					.setClientEncryptionMethod(Byte.parseByte(searchResult[10].toString()));
			tmpSessionHistory.setClientMACProtocol(Byte.parseByte(searchResult[11].toString()));
			tmpSessionHistory.setClientCWPUsed(Byte.parseByte(searchResult[12].toString()));
			tmpSessionHistory.setClientBSSID(searchResult[13].toString());
			// AhClientSessionHistoryTemp tmpSessionHistory =
			// (AhClientSessionHistoryTemp)lstInterfaceSessionInfo.get(0);
			clientIp = tmpSessionHistory.getClientIP();
			clientUserName = tmpSessionHistory.getClientUsername();
			clientHostName = tmpSessionHistory.getClientHostname();
			clientApMac = tmpSessionHistory.getApMac();
			clientApName = tmpSessionHistory.getApName();
			clientSSID = tmpSessionHistory.getClientSSID();
			clientVLAN = tmpSessionHistory.getClientVLANString();
			clientUserProfile = String.valueOf(tmpSessionHistory.getClientUserProfId());
			clientChannel = String.valueOf(tmpSessionHistory.getClientChannelString());
			clientAuthMethod = tmpSessionHistory.getClientAuthMethodString();
			clientEncryptionMethod = tmpSessionHistory.getClientEncryptionMethodString();
			clientPhysicalMode = tmpSessionHistory.getClientMacPtlString();
			clientCWPUsed = tmpSessionHistory.getClientCWPUsedString();
			clientLinkUpTime = NmsUtil
					.transformTime((int) ((Long.parseLong(values[1].toString()) - Long.parseLong(values[0].toString())) / 1000));
			clientBSSID=tmpSessionHistory.getClientBSSID();
		}
	}

	public void setClientSessionNew() throws Exception {
		if (currentPageSession.equals(
				MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
			return;
		}
		
		String[] sessionTime = currentPageSession.split("\\|");
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		l_sdf.setTimeZone(tz);
		
		List<AhClientStats> lstInterfaceInfo = QueryUtil.executeQuery(AhClientStats.class,
				new SortParams("timeStamp"), 
				new FilterParams("timeStamp>=:s1 and timeStamp<:s2 and clientMac=:s3",
						new Object[]{l_sdf.parse(sessionTime[0]).getTime(),
									l_sdf.parse(sessionTime[1]).getTime(),
									reportClientMac}),
				getDomain().getId());
		client_trans_totalData = new ArrayList<CheckItem>();
		client_rec_totalData = new ArrayList<CheckItem>();
		client_rec_airTime= new ArrayList<CheckItem>();
		client_trans_airTime= new ArrayList<CheckItem>();
		client_score = new ArrayList<CheckItem>();
		client_radio_score = new ArrayList<CheckItem>();
		client_ipnetwork_score = new ArrayList<CheckItem>();
		client_application_score = new ArrayList<CheckItem>();
		
		client_rec_drop = new ArrayList<CheckItem>();
		client_trans_drop = new ArrayList<CheckItem>();
		client_bandwidth = new ArrayList<CheckItem>();
		client_slacount = new ArrayList<CheckItem>();
		
		client_rec_rate_dis = new HashMap<String, List<CheckItem>>();
		client_rec_rate_succ_dis = new ArrayList<TextItem>();
		client_trans_rate_dis = new HashMap<String, List<CheckItem>>();
		client_trans_rate_succ_dis = new ArrayList<TextItem>();
//		client_trans_total_rate_succ_dis = new ArrayList<TextItem>();
//		client_rec_total_rate_succ_dis = new ArrayList<TextItem>();
		
		client_rec_dateTimeList= new ArrayList<String>();
		client_trans_dateTimeList= new ArrayList<String>();
		client_rec_rateTypeList= new ArrayList<String>();
		client_trans_rateTypeList= new ArrayList<String>();
		
//		List<CheckItem> tmpArray1;
//		for(int i=1;i<22;i++) {
//			tmpArray1= new ArrayList<CheckItem>();
//			String starTime = "2009-10-22 " + i + ":00:00";
//			tmpArray1.add(new CheckItem((long)i,"100K"));
//			tmpArray1.add(new CheckItem((long)i,"200K"));
//			tmpArray1.add(new CheckItem((long)i,"300K"));
//			tmpArray1.add(new CheckItem((long)i,"400K"));
//			
//			client_trans_rate_dis.put(starTime, tmpArray1);
//			
//			client_trans_dateTimeList.add(starTime);
//		}
//		
//		client_trans_rateTypeList.add("100K");
//		client_trans_rateTypeList.add("200K");
//		client_trans_rateTypeList.add("300K");
//		client_trans_rateTypeList.add("400K");
//		
//		
//		for(int i=1;i<22;i++) {
//			tmpArray1= new ArrayList<CheckItem>();
//			String starTime = "2009-10-22 " + i + ":00:00";
//			tmpArray1.add(new CheckItem((long)i,"100K"));
//			if (i%2==0) {
//				tmpArray1.add(new CheckItem((long)i,"200K"));
//			}
//			if (i%3==0) {
//				tmpArray1.add(new CheckItem((long)i,"300K"));
//			}
//			if (i%5==0) {
//				tmpArray1.add(new CheckItem((long)i,"400K"));
//			}
//			client_rec_rate_succ_dis.put(starTime, tmpArray1);
//			
//			client_rec_dateTimeList.add(starTime);
//		}
//		
//		client_rec_rateTypeList.add("100K");
//		client_rec_rateTypeList.add("200K");
//		client_rec_rateTypeList.add("300K");
//		client_rec_rateTypeList.add("400K");
		
		for(AhClientStats oneBo:lstInterfaceInfo){
			String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(oneBo.getTimeStamp(),tz);
			
			client_trans_totalData.add(new CheckItem((long) oneBo.getTxFrameCount(), starTime));
			client_rec_totalData.add(new CheckItem((long) oneBo.getRxFrameCount(), starTime));
			
			client_trans_airTime.add(new CheckItem((long)oneBo.getTxAirTime(), starTime));
			client_rec_airTime.add(new CheckItem((long)oneBo.getRxAirTime(), starTime));
			client_score.add(new CheckItem((long)oneBo.getOverallClientHealthScore(),starTime));
			client_radio_score.add(new CheckItem((long)oneBo.getSlaConnectScore(),starTime));
			client_ipnetwork_score.add(new CheckItem((long)oneBo.getIpNetworkConnectivityScore(),starTime));
			client_application_score.add(new CheckItem((long)oneBo.getApplicationHealthScore(),starTime));
			
			client_trans_drop.add(new CheckItem((long) oneBo.getTxFrameDropped(), starTime));
			client_rec_drop.add(new CheckItem((long) oneBo.getRxFrameDropped(), starTime));
			
			client_bandwidth.add(new CheckItem((long) oneBo.getBandWidthUsage(), starTime));
			client_slacount.add(new CheckItem((long) oneBo.getSlaViolationTraps(), starTime));
			
			StringBuilder rateSucDisTxValue=new StringBuilder();
			int totalPencent=0;
			int rateCount=0;
			if (oneBo.getTxRateInfo()!=null && !oneBo.getTxRateInfo().equals("")){
				String[] txRate=oneBo.getTxRateInfo().split(";");
				client_trans_dateTimeList.add(starTime);
				for (String rate : txRate) {
					if (!rate.equals("")) {
						String[] oneRec = rate.split(",");
						if (oneRec.length == 3) {
							rateCount++;
							if (!client_trans_rateTypeList.contains(oneRec[0])) {
								client_trans_rateTypeList.add(oneRec[0]);
							}
							if (client_trans_rate_dis.get(starTime) == null) {
								List<CheckItem> tmpArray = new ArrayList<CheckItem>();
								tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								client_trans_rate_dis.put(starTime, tmpArray);
							} else {
								client_trans_rate_dis.get(starTime).add(
										new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
							}
							totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
							rateSucDisTxValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
								.append(oneRec[2]).append("%; ");
						}
					}
				}
			}
			
			
			if (oneBo.getTotalTxBitSuccessRate()==0) {
				if (rateCount!=0) {
					client_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),rateSucDisTxValue.toString()));
//					client_trans_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
				} else {
					client_trans_rate_succ_dis.add(new TextItem(starTime,"0",rateSucDisTxValue.toString()));
//					client_trans_total_rate_succ_dis.add(new TextItem(starTime,"0%"));
				}
			} else {
				client_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate()),rateSucDisTxValue.toString()));
//				client_trans_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate())+ "%"));
			}
			totalPencent=0;
			rateCount=0;
			StringBuilder rateSucDisRxValue=new StringBuilder();
			if (oneBo.getRxRateInfo()!=null && !oneBo.getRxRateInfo().equals("")){
				String[] rxRate=oneBo.getRxRateInfo().split(";");
				client_rec_dateTimeList.add(starTime);
				
				for (String rate : rxRate) {
					if (!rate.equals("")) {
						String[] oneRec = rate.split(",");
						if (oneRec.length == 3) {
							rateCount++;
							if (!client_rec_rateTypeList.contains(oneRec[0])) {
								client_rec_rateTypeList.add(oneRec[0]);
							}
							if (client_rec_rate_dis.get(starTime) == null) {
								List<CheckItem> tmpArray = new ArrayList<CheckItem>();
								tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								client_rec_rate_dis.put(starTime, tmpArray);
							} else {
								client_rec_rate_dis.get(starTime).add(
										new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
							}
							totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
							rateSucDisRxValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
								.append(oneRec[2]).append("%; ");
						}
					}
				}
			}

			if (oneBo.getTotalRxBitSuccessRate()==0) {
				if (rateCount!=0) {
					client_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),rateSucDisRxValue.toString()));
//					client_rec_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
				} else {
					client_rec_rate_succ_dis.add(new TextItem(starTime,"0",rateSucDisRxValue.toString()));
//					client_rec_total_rate_succ_dis.add(new TextItem(starTime,"0%"));
				}
			} else {
				client_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate()),rateSucDisRxValue.toString()));
//				client_rec_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate())+ "%"));
			}
		}
		Collections.sort(client_rec_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		Collections.sort(client_trans_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		
		MgrUtil.setSessionAttribute("CLIENT_TRANS_TOTALDATA", client_trans_totalData);
		MgrUtil.setSessionAttribute("CLIENT_REC_TOTALDATA", client_rec_totalData);
		MgrUtil.setSessionAttribute("CLIENT_REC_AIRTIME", client_rec_airTime);
		MgrUtil.setSessionAttribute("CLIENT_SCORE", client_score);
		MgrUtil.setSessionAttribute("CLIENT_RADIO_SCORE", client_radio_score);
		MgrUtil.setSessionAttribute("CLIENT_IPNETWORK_SCORE", client_ipnetwork_score);
		MgrUtil.setSessionAttribute("CLIENT_APPLICATION_SCORE", client_application_score);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_AIRTIME", client_trans_airTime);
		MgrUtil.setSessionAttribute("CLIENT_REC_DROP", client_rec_drop);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_DROP", client_trans_drop);
		MgrUtil.setSessionAttribute("CLIENT_BANDWIDTH", client_bandwidth);
		MgrUtil.setSessionAttribute("CLIENT_SLACOUNT", client_slacount);
		MgrUtil.setSessionAttribute("CLIENT_REC_RATE_DIS", client_rec_rate_dis);
		MgrUtil.setSessionAttribute("CLIENT_REC_RATE_SUCC_DIS", client_rec_rate_succ_dis);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_RATE_DIS", client_trans_rate_dis);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_RATE_SUCC_DIS", client_trans_rate_succ_dis);
		
//		MgrUtil.setSessionAttribute("CLIENT_TRANS_TOTAL_RATE_SUCC_DIS", client_trans_total_rate_succ_dis);
//		MgrUtil.setSessionAttribute("CLIENT_REC_TOTAL_RATE_SUCC_DIS", client_rec_total_rate_succ_dis);
		
		MgrUtil.setSessionAttribute("CLIENT_REC_DATETIMELIST", client_rec_dateTimeList);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_DATETIMELIST", client_trans_dateTimeList);
		MgrUtil.setSessionAttribute("CLIENT_REC_RATETYPELIST", client_rec_rateTypeList);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_RATETYPELIST", client_trans_rateTypeList);
	}
	
	public void setClientSession() throws Exception {
		if (currentPageSession.equals(
				MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
			return;
		}

		String[] sessionTime = currentPageSession.split("\\|");
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		l_sdf.setTimeZone(tz);

		String strField = "time,clienttxdataframes,clienttxbedataframes,clienttxbgdataframes,clienttxvidataframes,clienttxvodataframes,clienttxmgtframes,clienttxunicastframes,clienttxdataoctets,clientlasttxrate,clientrxdataframes,clientrxmgtframes,clientrxunicastframes,clientrxmulticastframes,clientrxbroadcastframes,clientrxmicfailures,clientrxdataoctets,clientlastrxrate,clientrssi,clientChannel,apMac,SNR";
		String searchSQL = "select " + strField + " from hm_association_"
				+ getTempTableSuffix() + " where time >= "
				+ l_sdf.parse(sessionTime[0]).getTime() + " AND time < "
				+ l_sdf.parse(sessionTime[1]).getTime() + " AND clientmac='" + reportClientMac + "'"
				+ " AND owner=" + getDomain().getId();
		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(searchSQL);

		if (lstInterfaceInfo != null) {
			AhAssociation tmpAhAssociationValue = new AhAssociation();
			AhAssociation tmpNeedSaveValue = new AhAssociation();

			client_trans_totalData = new ArrayList<CheckItem>();
			client_trans_beData = new ArrayList<CheckItem>();
			client_trans_bgData = new ArrayList<CheckItem>();
			client_trans_viData = new ArrayList<CheckItem>();
			client_trans_voData = new ArrayList<CheckItem>();
			client_trans_mgtData = new ArrayList<CheckItem>();
			client_trans_unicastData = new ArrayList<CheckItem>();
			client_trans_dataOctets = new ArrayList<CheckItem>();
			client_trans_lastrate = new ArrayList<CheckItem>();
			client_rec_totalData = new ArrayList<CheckItem>();
			client_rec_mgtData = new ArrayList<CheckItem>();
			client_rec_unicastData = new ArrayList<CheckItem>();
			client_rec_multicastData = new ArrayList<CheckItem>();
			client_rec_broadcastData = new ArrayList<CheckItem>();
			client_rec_micfailures = new ArrayList<CheckItem>();
			client_rec_dataOctets = new ArrayList<CheckItem>();
			client_rec_lastrate = new ArrayList<CheckItem>();
			client_rssi = new ArrayList<CheckItem>();
			client_signal_to_noise = new ArrayList<CheckItem>();

			Calendar tmpDate = Calendar.getInstance(tz);
			tmpDate.setTimeInMillis(l_sdf.parse(sessionTime[0]).getTime());
			long reportTimeInMillis = l_sdf.parse(sessionTime[0]).getTime();
			long systemTimeInLong = l_sdf.parse(sessionTime[1]).getTime();

			while (reportTimeInMillis < systemTimeInLong + 3600000 && reportTimeInMillis<System.currentTimeMillis()) {
				boolean saveBefore = false;
				for (Object obj : lstInterfaceInfo) {
					Object[] searchResult = (Object[]) obj;
					AhAssociation ahAssociation = new AhAssociation();
					ahAssociation.setTimeStamp(new HmTimeStamp());
					ahAssociation.getTimeStamp().setTime(Long.parseLong(searchResult[0].toString()));
					ahAssociation.setClientTxDataFrames(Long.parseLong(searchResult[1].toString()));
					ahAssociation.setClientTxBeDataFrames(Long
							.parseLong(searchResult[2].toString()));
					ahAssociation.setClientTxBgDataFrames(Long
							.parseLong(searchResult[3].toString()));
					ahAssociation.setClientTxViDataFrames(Long
							.parseLong(searchResult[4].toString()));
					ahAssociation.setClientTxVoDataFrames(Long
							.parseLong(searchResult[5].toString()));
					ahAssociation.setClientTxMgtFrames(Long.parseLong(searchResult[6].toString()));
					ahAssociation.setClientTxUnicastFrames(Long.parseLong(searchResult[7]
							.toString()));
					ahAssociation.setClientTxDataOctets(Long.parseLong(searchResult[8].toString()));
					ahAssociation.setClientLastTxRate(Integer.parseInt(searchResult[9].toString()));
					ahAssociation
							.setClientRxDataFrames(Long.parseLong(searchResult[10].toString()));
					ahAssociation.setClientRxMgtFrames(Long.parseLong(searchResult[11].toString()));
					ahAssociation.setClientRxUnicastFrames(Long.parseLong(searchResult[12]
							.toString()));
					ahAssociation.setClientRxMulticastFrames(Long.parseLong(searchResult[13]
							.toString()));
					ahAssociation.setClientRxBroadcastFrames(Long.parseLong(searchResult[14]
							.toString()));
					ahAssociation.setClientRxMICFailures(Long
							.parseLong(searchResult[15].toString()));
					ahAssociation
							.setClientRxDataOctets(Long.parseLong(searchResult[16].toString()));
					ahAssociation
							.setClientLastRxRate(Integer.parseInt(searchResult[17].toString()));
					ahAssociation.setClientRSSI(Integer.parseInt(searchResult[18].toString()));
					ahAssociation.setClientChannel(Integer.parseInt((searchResult[19] == null ? "-1" : searchResult[19]).toString()));
					ahAssociation.setApMac(searchResult[20] == null ? null : searchResult[20].toString());
					ahAssociation.setSNR(Short.parseShort(searchResult[21].toString()));

					String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(ahAssociation.getTimeStamp().getTime(), tz);

					if (ahAssociation.getTimeStamp().getTime() <= reportTimeInMillis - 3600000) {
						continue;
					}

					if (ahAssociation.getTimeStamp().getTime() > reportTimeInMillis) {
						break;
					} else {
						// transmit totalData
						long tmpCount = checkValueLessThanZero(ahAssociation
								.getClientTxDataFrames(), tmpAhAssociationValue
								.getClientTxDataFrames());
						tmpNeedSaveValue.setClientTxDataFrames(tmpCount);
						tmpAhAssociationValue.setClientTxDataFrames(ahAssociation
								.getClientTxDataFrames());
						// transmit client_beData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxBeDataFrames(),
								tmpAhAssociationValue.getClientTxBeDataFrames());
						tmpNeedSaveValue.setClientTxBeDataFrames(tmpCount);
						tmpAhAssociationValue.setClientTxBeDataFrames(ahAssociation
								.getClientTxBeDataFrames());
						// transmit client_bgData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxBgDataFrames(),
								tmpAhAssociationValue.getClientTxBgDataFrames());
						tmpNeedSaveValue.setClientTxBgDataFrames(tmpCount);
						tmpAhAssociationValue.setClientTxBgDataFrames(ahAssociation
								.getClientTxBgDataFrames());
						// transmit client_viData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxViDataFrames(),
								tmpAhAssociationValue.getClientTxViDataFrames());
						tmpNeedSaveValue.setClientTxViDataFrames(tmpCount);
						tmpAhAssociationValue.setClientTxViDataFrames(ahAssociation
								.getClientTxViDataFrames());
						// transmit client_voData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxVoDataFrames(),
								tmpAhAssociationValue.getClientTxVoDataFrames());
						tmpNeedSaveValue.setClientTxVoDataFrames(tmpCount);
						tmpAhAssociationValue.setClientTxVoDataFrames(ahAssociation
								.getClientTxVoDataFrames());
						// transmit client_mgtData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxMgtFrames(),
								tmpAhAssociationValue.getClientTxMgtFrames());
						tmpNeedSaveValue.setClientTxMgtFrames(tmpCount);
						tmpAhAssociationValue.setClientTxMgtFrames(ahAssociation
								.getClientTxMgtFrames());
						// transmit client_unicastData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxUnicastFrames(),
								tmpAhAssociationValue.getClientTxUnicastFrames());
						tmpNeedSaveValue.setClientTxUnicastFrames(tmpCount);
						tmpAhAssociationValue.setClientTxUnicastFrames(ahAssociation
								.getClientTxUnicastFrames());
						// transmit client_dataOctets
						tmpCount = checkValueLessThanZero(ahAssociation.getClientTxDataOctets(),
								tmpAhAssociationValue.getClientTxDataOctets());
						tmpNeedSaveValue.setClientTxDataOctets(tmpCount);
						tmpAhAssociationValue.setClientTxDataOctets(ahAssociation
								.getClientTxDataOctets());
						// transmit client_lastrate
						tmpNeedSaveValue.setClientLastTxRate(ahAssociation.getClientLastTxRate());
						tmpAhAssociationValue.setClientLastTxRate(ahAssociation
								.getClientLastTxRate());
						// receive client_totalDataFrame
						tmpCount = checkValueLessThanZero(ahAssociation.getClientRxDataFrames(),
								tmpAhAssociationValue.getClientRxDataFrames());
						tmpNeedSaveValue.setClientRxDataFrames(tmpCount);
						tmpAhAssociationValue.setClientRxDataFrames(ahAssociation
								.getClientRxDataFrames());
						// receive client_mgtData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientRxMgtFrames(),
								tmpAhAssociationValue.getClientRxMgtFrames());
						tmpNeedSaveValue.setClientRxMgtFrames(tmpCount);
						tmpAhAssociationValue.setClientRxMgtFrames(ahAssociation
								.getClientRxMgtFrames());
						// receive client_unicastData
						tmpCount = checkValueLessThanZero(ahAssociation.getClientRxUnicastFrames(),
								tmpAhAssociationValue.getClientRxUnicastFrames());
						tmpNeedSaveValue.setClientRxUnicastFrames(tmpCount);
						tmpAhAssociationValue.setClientRxUnicastFrames(ahAssociation
								.getClientRxUnicastFrames());
						// receive client_multicastData
						tmpCount = checkValueLessThanZero(ahAssociation
								.getClientRxMulticastFrames(), tmpAhAssociationValue
								.getClientRxMulticastFrames());
						tmpNeedSaveValue.setClientRxMulticastFrames(tmpCount);
						tmpAhAssociationValue.setClientRxMulticastFrames(ahAssociation
								.getClientRxMulticastFrames());
						// receive client_broadcastData
						tmpCount = checkValueLessThanZero(ahAssociation
								.getClientRxBroadcastFrames(), tmpAhAssociationValue
								.getClientRxBroadcastFrames());
						tmpNeedSaveValue.setClientRxBroadcastFrames(tmpCount);
						tmpAhAssociationValue.setClientRxBroadcastFrames(ahAssociation
								.getClientRxBroadcastFrames());
						// receive client_micfailures
						tmpCount = checkValueLessThanZero(ahAssociation.getClientRxMICFailures(),
								tmpAhAssociationValue.getClientRxMICFailures());
						tmpNeedSaveValue.setClientRxMICFailures(tmpCount);
						tmpAhAssociationValue.setClientRxMICFailures(ahAssociation
								.getClientRxMICFailures());
						// receive client_dataOctets
						tmpCount = checkValueLessThanZero(ahAssociation.getClientRxDataOctets(),
								tmpAhAssociationValue.getClientRxDataOctets());
						tmpNeedSaveValue.setClientRxDataOctets(tmpCount);
						tmpAhAssociationValue.setClientRxDataOctets(ahAssociation
								.getClientRxDataOctets());
						// receive client_lastrate
						tmpNeedSaveValue.setClientLastRxRate(ahAssociation.getClientLastRxRate());
						tmpAhAssociationValue.setClientLastRxRate(ahAssociation
								.getClientLastRxRate());
						// client_rssi
						tmpNeedSaveValue.setClientRSSI(ahAssociation.getClientRSSI());
						tmpAhAssociationValue.setClientRSSI(ahAssociation.getClientRSSI());
						
						tmpNeedSaveValue.setClientChannel(ahAssociation.getClientChannel());
						tmpAhAssociationValue.setClientChannel(tmpNeedSaveValue.getClientChannel());
						
						tmpNeedSaveValue.setApMac(ahAssociation.getApMac());
						tmpAhAssociationValue.setApMac(tmpNeedSaveValue.getApMac());

						client_trans_totalData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxDataFrames(), starTime));
						client_trans_beData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxBeDataFrames(), starTime));
						client_trans_bgData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxBgDataFrames(), starTime));
						client_trans_viData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxViDataFrames(), starTime));
						client_trans_voData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxVoDataFrames(), starTime));
						client_trans_mgtData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxMgtFrames(), starTime));
						client_trans_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getClientTxUnicastFrames(), starTime));
						client_trans_dataOctets.add(new CheckItem(tmpNeedSaveValue
								.getClientTxDataOctets(), starTime));
						client_trans_lastrate.add(new CheckItem((long) tmpNeedSaveValue
								.getClientLastTxRate(), starTime));
						client_rec_totalData.add(new CheckItem(tmpNeedSaveValue
								.getClientRxDataFrames(), starTime));
						client_rec_mgtData.add(new CheckItem(tmpNeedSaveValue
								.getClientRxMgtFrames(), starTime));
						client_rec_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getClientRxUnicastFrames(), starTime));
						client_rec_multicastData.add(new CheckItem(tmpNeedSaveValue
								.getClientRxMulticastFrames(), starTime));
						client_rec_broadcastData.add(new CheckItem(tmpNeedSaveValue
								.getClientRxBroadcastFrames(), starTime));
						client_rec_micfailures.add(new CheckItem(tmpNeedSaveValue
								.getClientRxMICFailures(), starTime));
						client_rec_dataOctets.add(new CheckItem(tmpNeedSaveValue
								.getClientRxDataOctets(), starTime));
						client_rec_lastrate.add(new CheckItem((long) tmpNeedSaveValue
								.getClientLastRxRate(), starTime));
						if (tmpNeedSaveValue.getClientChannel()>0) {
							client_rssi.add(new CheckItem((long) tmpNeedSaveValue.getClientRSSI(),
								starTime));
						}
						client_signal_to_noise.add(new CheckItem((long)tmpNeedSaveValue.getSNR(),
								starTime));
						tmpNeedSaveValue = new AhAssociation();
						saveBefore = true;
					}
				}

				if (!saveBefore && tmpDate.getTimeInMillis()<=systemTimeInLong && reportTimeInMillis<System.currentTimeMillis()) {
					client_trans_totalData.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_trans_beData
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_trans_bgData
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_trans_viData
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_trans_voData
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_trans_mgtData.add(new CheckItem((long) 0, l_sdf
							.format(tmpDate.getTime())));
					client_trans_unicastData.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_trans_dataOctets.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_trans_lastrate.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_totalData.add(new CheckItem((long) 0, l_sdf
							.format(tmpDate.getTime())));
					client_rec_mgtData
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_rec_unicastData.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_multicastData.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_broadcastData.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_micfailures.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_dataOctets.add(new CheckItem((long) 0, l_sdf.format(tmpDate
							.getTime())));
					client_rec_lastrate
							.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_rssi.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
					client_signal_to_noise.add(new CheckItem((long) 0, l_sdf.format(tmpDate.getTime())));
				}
				reportTimeInMillis = reportTimeInMillis + 3600000;
				tmpDate.add(Calendar.HOUR_OF_DAY, 1);
				tmpNeedSaveValue = new AhAssociation();
			}
		}

		MgrUtil.setSessionAttribute("CLIENT_TRANS_TOTALDATA", client_trans_totalData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_BEDATA", client_trans_beData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_BGDATA", client_trans_bgData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_VIDATA", client_trans_viData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_VODATA", client_trans_voData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_MGTDATA", client_trans_mgtData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_UNICASTDATA", client_trans_unicastData);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_DATAOCTETS", client_trans_dataOctets);
		MgrUtil.setSessionAttribute("CLIENT_TRANS_LASTRATE", client_trans_lastrate);
		MgrUtil.setSessionAttribute("CLIENT_REC_TOTALDATA", client_rec_totalData);
		MgrUtil.setSessionAttribute("CLIENT_REC_MGTDATA", client_rec_mgtData);
		MgrUtil.setSessionAttribute("CLIENT_REC_UNICASTDATA", client_rec_unicastData);
		MgrUtil.setSessionAttribute("CLIENT_REC_MULTICASTDATA", client_rec_multicastData);
		MgrUtil.setSessionAttribute("CLIENT_REC_BROADCASTDATA", client_rec_broadcastData);
		MgrUtil.setSessionAttribute("CLIENT_REC_MICFAILURES", client_rec_micfailures);
		MgrUtil.setSessionAttribute("CLIENT_REC_DATAOCTETS", client_rec_dataOctets);
		MgrUtil.setSessionAttribute("CLIENT_REC_LASTRATE", client_rec_lastrate);
		MgrUtil.setSessionAttribute("CLIENT_RSSI", client_rssi);
		MgrUtil.setSessionAttribute("CLIENT_SIGNAL_TO_NOISE", client_signal_to_noise);
	}

	public void setUniqueClientCount(Calendar reportDateTime, long reportTimeAggregation) {
		long systemTimeInMillis = System.currentTimeMillis();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sf.setTimeZone(tz);
		String searchSQL = "select clientMac,startTimeStamp,endTimeStamp from " + AhClientSessionHistory.class.getSimpleName() + " where "
				+ " endTimeStamp >=" + reportDateTime.getTimeInMillis();
		if (reportAPName.equals("ALL")) {
			searchSQL = searchSQL + " AND lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%'";
		} else {
			searchSQL = searchSQL + " AND apName='" + reportAPName + "'";
		}
		if (getDataSource().getLocation() != null) {
			searchSQL = searchSQL + " AND mapId = "
					+ getDataSource().getLocation().getId().toString();
		}
		searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();

		List<?> profiles = QueryUtil.executeQuery(searchSQL, null, null);

		Map<String, Set<String>> cleintCountMap = new HashMap<String, Set<String>>();

		if (profiles != null && profiles.size() > 0) {
			Calendar historyTime = Calendar.getInstance(tz);

			for (Object profile : profiles) {
				historyTime.setTimeInMillis(reportDateTime.getTimeInMillis());
				historyTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
				while (historyTime.getTimeInMillis() <= systemTimeInMillis) {
					Object[] tmp = (Object[]) profile;
					if (Long.parseLong(tmp[1].toString()) <= historyTime.getTimeInMillis()
							&& Long.parseLong(tmp[2].toString()) > historyTime.getTimeInMillis() - reportTimeAggregation) {
						if (cleintCountMap.get(sf.format(historyTime.getTime())) == null) {
							Set<String> setClientMac = new HashSet<String>();
							setClientMac.add(tmp[0].toString());
							cleintCountMap.put(sf.format(historyTime.getTime()), setClientMac);
						} else {
							cleintCountMap.get(sf.format(historyTime.getTime())).add(
									tmp[0].toString());
						}
					}

					if (cleintCountMap.get(sf.format(historyTime.getTime())) == null) {
						Set<String> setClientMac = new HashSet<String>();
						cleintCountMap.put(sf.format(historyTime.getTime()), setClientMac);
					}

					historyTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
				}
			}
		}

		String searchSQLCurrent = "select clientMac,startTimeStamp from " + AhClientSession.class.getSimpleName() + " where ";
		if (reportAPName.equals("ALL")) {
			searchSQLCurrent = searchSQLCurrent + "lower(apName) like '%"
					+ getDataSource().getApNameForSQL().toLowerCase() + "%' AND ";
		} else {
			searchSQLCurrent = searchSQLCurrent + "apName='" + reportAPName + "' AND ";
		}
		if (getDataSource().getLocation() != null) {
			searchSQLCurrent = searchSQLCurrent + "mapId = "
					+ getDataSource().getLocation().getId().toString() + " AND ";
		}
		searchSQLCurrent = searchSQLCurrent + "owner.id=" + getDomain().getId();

		List<?> profilesCurrent = QueryUtil.executeQuery(searchSQLCurrent, null, null);
		if (profilesCurrent != null && profilesCurrent.size() > 0) {
			Calendar currentTime = Calendar.getInstance(tz);

			for (Object obj : profilesCurrent) {
				currentTime.setTimeInMillis(reportDateTime.getTimeInMillis());
				currentTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
				while (currentTime.getTimeInMillis() <= systemTimeInMillis) {
					Object[] tmp = (Object[]) obj;
					if (Long.parseLong(tmp[1].toString()) <= currentTime.getTimeInMillis()) {
						if (cleintCountMap.get(sf.format(currentTime.getTime())) == null) {
							Set<String> setClientMac = new HashSet<String>();
							setClientMac.add(tmp[0].toString());
							cleintCountMap.put(sf.format(currentTime.getTime()), setClientMac);
						} else {
							cleintCountMap.get(sf.format(currentTime.getTime())).add(
									tmp[0].toString());
						}
					}
					if (cleintCountMap.get(sf.format(currentTime.getTime())) == null) {
						Set<String> setClientMac = new HashSet<String>();
						cleintCountMap.put(sf.format(currentTime.getTime()), setClientMac);
					}
					currentTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
				}
			}
		}

		uniqueClients = new ArrayList<CheckItem>();
		for (String tmpReportTime : cleintCountMap.keySet()) {
			long tmpSize = cleintCountMap.get(tmpReportTime).size();
			uniqueClients.add(new CheckItem(tmpSize, tmpReportTime));

		}

		Collections.sort(uniqueClients, new Comparator<CheckItem>() {
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				try {
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sf1.setTimeZone(tz);
					Date reportTime1 = sf1.parse(o1.getValue());
					Date reportTime2 = sf1.parse(o2.getValue());
					return new Long((reportTime1.getTime() - reportTime2.getTime()) / 100000)
							.intValue();
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}

	public void setMeshNeighbors(Calendar reportDateTime, long reportTimeAggregation) {
		String searchSQL = "timeStamp.time >= :s1 AND apName =:s2 AND neighborAPID=:s3 AND linkType=:s4";

		Object values[] = new Object[4];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = reportAPName;
		values[2] = reportNeighborAP;
		values[3] = AhNeighbor.LINKTYPE_WIRELESSLINK;

		List<AhNeighbor> lstInterfaceInfo = QueryUtil.executeQuery(AhNeighbor.class, new SortParams("timeStamp.time"),
				new FilterParams(searchSQL, values), getDomain().getId());
		AhNeighbor tmpPreviousValue = new AhNeighbor();
		AhNeighbor tmpNeedSaveValue = new AhNeighbor();

		mesh_trans_totalData = new ArrayList<CheckItem>();
		mesh_trans_beData = new ArrayList<CheckItem>();
		mesh_trans_bgData = new ArrayList<CheckItem>();
		mesh_trans_viData = new ArrayList<CheckItem>();
		mesh_trans_voData = new ArrayList<CheckItem>();
		mesh_trans_mgtData = new ArrayList<CheckItem>();
		mesh_trans_unicastData = new ArrayList<CheckItem>();
		mesh_rec_totalData = new ArrayList<CheckItem>();
		mesh_rec_mgtData = new ArrayList<CheckItem>();
		mesh_rec_unicastData = new ArrayList<CheckItem>();
		mesh_rec_multicastData = new ArrayList<CheckItem>();
		mesh_rec_broadcastData = new ArrayList<CheckItem>();
		mesh_rssiData = new ArrayList<CheckItem>();

//			int rssiCount = 0;
		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		if (lstInterfaceInfo.size() > 0) {
			tmpPreviousValue = lstInterfaceInfo.get(0);
		}
		while (reportTimeInMillis < systemTimeInLong) {
			for (AhNeighbor ahNeighbor : lstInterfaceInfo) {
				if (ahNeighbor.getTimeStamp().getTime() <= reportTimeInMillis
						- reportTimeAggregation) {
					continue;
				}

				if (ahNeighbor.getTimeStamp().getTime() > reportTimeInMillis) {
					break;
				} else {
					// transmit totalData
					long tmpCount = checkValueLessThanZero(ahNeighbor.getTxDataFrames(),
							tmpPreviousValue.getTxDataFrames());
					tmpNeedSaveValue.setTxDataFrames(tmpNeedSaveValue.getTxDataFrames()
							+ tmpCount);
					tmpPreviousValue.setTxDataFrames(ahNeighbor.getTxDataFrames());

					// transmit beData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxBeDataFrames(),
							tmpPreviousValue.getTxBeDataFrames());
					tmpNeedSaveValue.setTxBeDataFrames(tmpNeedSaveValue.getTxBeDataFrames()
							+ tmpCount);
					tmpPreviousValue.setTxBeDataFrames(ahNeighbor.getTxBeDataFrames());

					// transmit bgData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxBgDataFrames(),
							tmpPreviousValue.getTxBgDataFrames());
					tmpNeedSaveValue.setTxBgDataFrames(tmpNeedSaveValue.getTxBgDataFrames()
							+ tmpCount);
					tmpPreviousValue.setTxBgDataFrames(ahNeighbor.getTxBgDataFrames());

					// transmit viData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxViDataFrames(),
							tmpPreviousValue.getTxViDataFrames());
					tmpNeedSaveValue.setTxViDataFrames(tmpNeedSaveValue.getTxViDataFrames()
							+ tmpCount);
					tmpPreviousValue.setTxViDataFrames(ahNeighbor.getTxViDataFrames());

					// transmit voData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxVoDataFrames(),
							tmpPreviousValue.getTxVoDataFrames());
					tmpNeedSaveValue.setTxVoDataFrames(tmpNeedSaveValue.getTxVoDataFrames()
							+ tmpCount);
					tmpPreviousValue.setTxVoDataFrames(ahNeighbor.getTxVoDataFrames());

					// transmit mgtData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxMgtFrames(),
							tmpPreviousValue.getTxMgtFrames());
					tmpNeedSaveValue.setTxMgtFrames(tmpNeedSaveValue.getTxMgtFrames()
							+ tmpCount);
					tmpPreviousValue.setTxMgtFrames(ahNeighbor.getTxMgtFrames());

					// transmit unicastData
					tmpCount = checkValueLessThanZero(ahNeighbor.getTxUnicastFrames(),
							tmpPreviousValue.getTxUnicastFrames());
					tmpNeedSaveValue.setTxUnicastFrames(tmpNeedSaveValue.getTxUnicastFrames()
							+ tmpCount);
					tmpPreviousValue.setTxUnicastFrames(ahNeighbor.getTxUnicastFrames());

					// receive totalData
					tmpCount = checkValueLessThanZero(ahNeighbor.getRxDataFrames(),
							tmpPreviousValue.getRxDataFrames());
					tmpNeedSaveValue.setRxDataFrames(tmpNeedSaveValue.getRxDataFrames()
							+ tmpCount);
					tmpPreviousValue.setRxDataFrames(ahNeighbor.getRxDataFrames());

					// receive mgtData
					tmpCount = checkValueLessThanZero(ahNeighbor.getRxMgtFrames(),
							tmpPreviousValue.getRxMgtFrames());
					tmpNeedSaveValue.setRxMgtFrames(tmpNeedSaveValue.getRxMgtFrames()
							+ tmpCount);
					tmpPreviousValue.setRxMgtFrames(ahNeighbor.getRxMgtFrames());

					// receive unicastData
					tmpCount = checkValueLessThanZero(ahNeighbor.getRxUnicastFrames(),
							tmpPreviousValue.getRxUnicastFrames());
					tmpNeedSaveValue.setRxUnicastFrames(tmpNeedSaveValue.getRxUnicastFrames()
							+ tmpCount);
					tmpPreviousValue.setRxUnicastFrames(ahNeighbor.getRxUnicastFrames());

					// receive multicastData
					tmpCount = checkValueLessThanZero(ahNeighbor.getRxMulticastFrames(),
							tmpPreviousValue.getRxMulticastFrames());
					tmpNeedSaveValue.setRxMulticastFrames(tmpNeedSaveValue
							.getRxMulticastFrames()
							+ tmpCount);
					tmpPreviousValue.setRxMulticastFrames(ahNeighbor.getRxMulticastFrames());

					// receive broadcastData
					tmpCount = checkValueLessThanZero(ahNeighbor.getRxBroadcastFrames(),
							tmpPreviousValue.getRxBroadcastFrames());
					tmpNeedSaveValue.setRxBroadcastFrames(tmpNeedSaveValue
							.getRxBroadcastFrames()
							+ tmpCount);
					tmpPreviousValue.setRxBroadcastFrames(ahNeighbor.getRxBroadcastFrames());

					// receive rssiData
					tmpNeedSaveValue.setRssi(ahNeighbor.getRssi());
//						rssiCount++;
				}
			}
			String tmpDateStringValue=AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			mesh_trans_totalData.add(new CheckItem(tmpNeedSaveValue.getTxDataFrames(),
					tmpDateStringValue));
			mesh_trans_beData.add(new CheckItem(tmpNeedSaveValue.getTxBeDataFrames(),
					tmpDateStringValue));
			mesh_trans_bgData.add(new CheckItem(tmpNeedSaveValue.getTxBgDataFrames(),
					tmpDateStringValue));
			mesh_trans_viData.add(new CheckItem(tmpNeedSaveValue.getTxViDataFrames(),
					tmpDateStringValue));
			mesh_trans_voData.add(new CheckItem(tmpNeedSaveValue.getTxVoDataFrames(),
					tmpDateStringValue));
			mesh_trans_mgtData.add(new CheckItem(tmpNeedSaveValue.getTxMgtFrames(),
					tmpDateStringValue));
			mesh_trans_unicastData.add(new CheckItem(tmpNeedSaveValue
					.getTxUnicastFrames(), tmpDateStringValue));
			mesh_rec_totalData.add(new CheckItem(tmpNeedSaveValue.getRxDataFrames(),
					tmpDateStringValue));
			mesh_rec_mgtData.add(new CheckItem(tmpNeedSaveValue.getRxMgtFrames(),
					tmpDateStringValue));
			mesh_rec_unicastData.add(new CheckItem(tmpNeedSaveValue.getRxUnicastFrames(),
					tmpDateStringValue));
			mesh_rec_multicastData.add(new CheckItem(tmpNeedSaveValue
					.getRxMulticastFrames(), tmpDateStringValue));
			mesh_rec_broadcastData.add(new CheckItem(tmpNeedSaveValue
					.getRxBroadcastFrames(), tmpDateStringValue));
//				if (rssiCount == 0) {
//					rssiCount = 1;
//				}
			mesh_rssiData.add(new CheckItem((long) tmpNeedSaveValue.getRssi()-95,
					tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpNeedSaveValue = new AhNeighbor();
//				rssiCount = 0;
		}
	}

	public void setSecurityRogueAPs(Calendar reportDateTime, long reportTimeAggregation) {
		long systemTimeInMillis = System.currentTimeMillis();
		String searchSQL = "select bo.ifMacAddress,bo.reportTime.time from ";
		FilterParams rogueApsFilterParams;
		rogueApsFilterParams = new FilterParams(
				"bo.stationType = :s1 and bo.idpType=:s2 and bo.reportTime.time>=:s3", new Object[] {
						BeCommunicationConstant.IDP_STATION_TYPE_AP,
						BeCommunicationConstant.IDP_TYPE_ROGUE, reportDateTime.getTimeInMillis() });

		List<?> rogueProfiles = QueryUtil.executeQuery(searchSQL + Idp.class.getSimpleName()
				+ " bo", new SortParams("reportTime.time"), rogueApsFilterParams, getDomain().getId());

		rogueAPs = new ArrayList<CheckItem>();

		long currentTimeInMillis = reportDateTime.getTimeInMillis() - 1;
		reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
		while (reportDateTime.getTimeInMillis() <= systemTimeInMillis) {
			long rogueCount = 0;
			List<String> ifMac = new ArrayList<String>();
			for (Object rogueProfile : rogueProfiles) {
				Object[] oneProfile = (Object[]) rogueProfile;
				if (ifMac.contains(oneProfile[0].toString())) {
					continue;
				}
				ifMac.add(oneProfile[0].toString());
				long tmp = Long.parseLong(oneProfile[1].toString());
				if (tmp >= currentTimeInMillis
						&& tmp < reportDateTime.getTimeInMillis()) {
					rogueCount++;
				}
			}
			rogueAPs.add(new CheckItem(rogueCount, AhDateTimeUtil.getSpecifyDateTimeReport(reportDateTime.getTimeInMillis(),tz)));
			currentTimeInMillis = reportDateTime.getTimeInMillis();
			reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
		}
	}
	
	public void setSecurityRogueClients(Calendar reportDateTime, long reportTimeAggregation) {
		long systemTimeInMillis = System.currentTimeMillis();
		String searchSQL = "select bo.ifMacAddress,bo.reportTime.time from ";
		FilterParams rogueApsFilterParams;

		rogueApsFilterParams = new FilterParams(
				"bo.stationType = :s1 and bo.idpType=:s2 and bo.reportTime.time>=:s3", new Object[] {
						BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
						BeCommunicationConstant.IDP_TYPE_ROGUE, reportDateTime.getTimeInMillis() });

		List<?> rogueProfiles = QueryUtil.executeQuery(searchSQL + Idp.class.getSimpleName()
				+ " bo", new SortParams("reportTime.time"), rogueApsFilterParams, getDomain().getId());

		rogueClients = new ArrayList<CheckItem>();

		long currentTimeInMillis = reportDateTime.getTimeInMillis() - 1;
		reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
		while (reportDateTime.getTimeInMillis() <= systemTimeInMillis) {
			long rogueCount = 0;
			List<String> ifMac = new ArrayList<String>();
			for (Object rogueProfile : rogueProfiles) {
				Object[] oneProfile = (Object[]) rogueProfile;
				if (ifMac.contains(oneProfile[0].toString())) {
					continue;
				}
				ifMac.add(oneProfile[0].toString());
				long tmp = Long.parseLong(oneProfile[1].toString());
				if (tmp >= currentTimeInMillis
						&& tmp < reportDateTime.getTimeInMillis()) {
					rogueCount++;
				}
			}
			rogueClients.add(new CheckItem(rogueCount, AhDateTimeUtil.getSpecifyDateTimeReport(reportDateTime.getTimeInMillis(),tz)));
			currentTimeInMillis = reportDateTime.getTimeInMillis();
			reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
		}
	}
	
	public void setMaxClientReport(Calendar reportDateTime, long reportTimeAggregation) {
		String searchSQL = "select bo.maxClientCount,bo.timeStamp from ";
		FilterParams maxClientFilterParams;
		
		maxClientFilterParams = new FilterParams(
				"bo.timeStamp>=:s1 and globalFlg=:s2", new Object[] {reportDateTime.getTimeInMillis(),false });

		List<?> maxClientProfiles = QueryUtil.executeQuery(searchSQL + AhMaxClientsCount.class.getSimpleName()
				+ " bo", new SortParams("timeStamp"), maxClientFilterParams, getDomain().getId());

		maxClients = new ArrayList<CheckItem>();

		for (Object maxClientProfile : maxClientProfiles) {
			Object[] oneProfile = (Object[]) maxClientProfile;
			maxClients.add(new CheckItem(Long.parseLong(oneProfile[0].toString()),
					AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(oneProfile[1].toString()), tz)));
		}
	}

	public void setSsidTroubleShooting(Calendar reportDateTime, long reportTimeAggregation) {
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//		Object values[] = new Object[3];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = reportSsidName;
//
//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		
		String sql ="select x.ifname,v.statTimeStamp,v.txVIfDroppedFrames,v.rxVIfDroppedFrames," +
				"v.txVIfErrorFrames,v.rxVIfErrorFrames from hm_xif x, HM_VIFSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(reportSsidName) +
				"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
				
		AhVIfStats tmpPreviousValue = new AhVIfStats();
		AhVIfStats tmpPreviousValueSec = new AhVIfStats();
		AhVIfStats tmpNeedSaveValue = new AhVIfStats();

		sts_trans_totalFramesDropped = new ArrayList<CheckItem>();
		sts_trans_totalFrameErrors = new ArrayList<CheckItem>();
		sts_rec_totalFramesDropped = new ArrayList<CheckItem>();
		sts_rec_totalFrameErrors = new ArrayList<CheckItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		boolean firstSave= false;
		String firstIfName= "";
		String secondIfName= "";
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			
			if (!firstSave) {
//				tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpPreviousValue.setTxVIfDroppedFrames(Long.parseLong(oneItem[2].toString()));
				tmpPreviousValue.setRxVIfDroppedFrames(Long.parseLong(oneItem[3].toString()));
				tmpPreviousValue.setTxVIfErrorFrames(Long.parseLong(oneItem[4].toString()));
				tmpPreviousValue.setRxVIfErrorFrames(Long.parseLong(oneItem[5].toString()));
				firstSave = true;
				firstIfName = oneItem[0].toString();
				continue;
			}
			if (firstSave && secondIfName.equals("")) {
				if (!oneItem[0].toString().equals(firstIfName)) {
//					tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
					tmpPreviousValueSec.setTxVIfDroppedFrames(Long.parseLong(oneItem[2].toString()));
					tmpPreviousValueSec.setRxVIfDroppedFrames(Long.parseLong(oneItem[3].toString()));
					tmpPreviousValueSec.setTxVIfErrorFrames(Long.parseLong(oneItem[4].toString()));
					tmpPreviousValueSec.setRxVIfErrorFrames(Long.parseLong(oneItem[5].toString()));
					secondIfName = oneItem[0].toString();
					break;
				}
			}
		}
		
		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				long txDrop= Long.parseLong(oneItem[2].toString());
				long rxDrop= Long.parseLong(oneItem[3].toString());
				long txError= Long.parseLong(oneItem[4].toString());
				long rxError= Long.parseLong(oneItem[5].toString());
				
				if (cuTime <= reportTimeInMillis
						- reportTimeAggregation) {
					continue;
				}

				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					long tmpCount;
					// transmit totalFramesDropped
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txDrop, tmpPreviousValue
								.getTxVIfDroppedFrames());
						tmpPreviousValue.setTxVIfDroppedFrames(txDrop);
					} else {
						tmpCount = checkValueLessThanZero(txDrop, tmpPreviousValueSec
								.getTxVIfDroppedFrames());
						tmpPreviousValueSec.setTxVIfDroppedFrames(txDrop);
					}
					tmpNeedSaveValue.setTxVIfDroppedFrames(tmpNeedSaveValue
							.getTxVIfDroppedFrames()
							+ tmpCount);

					// transmit totalFrameErrors
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txError, tmpPreviousValue.getTxVIfErrorFrames());
						tmpPreviousValue.setTxVIfErrorFrames(txError);
					} else {
						tmpCount = checkValueLessThanZero(txError, tmpPreviousValueSec.getTxVIfErrorFrames());
						tmpPreviousValueSec.setTxVIfErrorFrames(txError);
					}
					tmpNeedSaveValue.setTxVIfErrorFrames(tmpNeedSaveValue
							.getTxVIfErrorFrames()
							+ tmpCount);

					// receive totalFramesDropped
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxDrop, tmpPreviousValue
								.getRxVIfDroppedFrames());
						tmpPreviousValue.setRxVIfDroppedFrames(rxDrop);
					} else {
						tmpCount = checkValueLessThanZero(rxDrop, tmpPreviousValueSec
								.getRxVIfDroppedFrames());
						tmpPreviousValueSec.setRxVIfDroppedFrames(rxDrop);
					}
					tmpNeedSaveValue.setRxVIfDroppedFrames(tmpNeedSaveValue
							.getRxVIfDroppedFrames()
							+ tmpCount);

					// receive totalFrameErrors
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxError, tmpPreviousValue.getRxVIfErrorFrames());
						tmpPreviousValue.setRxVIfErrorFrames(rxError);
					} else {
						tmpCount = checkValueLessThanZero(rxError, tmpPreviousValueSec.getRxVIfErrorFrames());
						tmpPreviousValueSec.setRxVIfErrorFrames(rxError);
					}
					tmpNeedSaveValue.setRxVIfErrorFrames(tmpNeedSaveValue
							.getRxVIfErrorFrames()
							+ tmpCount);
				}

			}
			String tmpDateStringValue=AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			sts_trans_totalFramesDropped.add(new CheckItem(tmpNeedSaveValue
					.getTxVIfDroppedFrames(), tmpDateStringValue));
			sts_trans_totalFrameErrors.add(new CheckItem(tmpNeedSaveValue
					.getTxVIfErrorFrames(), tmpDateStringValue));
			sts_rec_totalFramesDropped.add(new CheckItem(tmpNeedSaveValue
					.getRxVIfDroppedFrames(), tmpDateStringValue));
			sts_rec_totalFrameErrors.add(new CheckItem(tmpNeedSaveValue
					.getRxVIfErrorFrames(), tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpNeedSaveValue = new AhVIfStats();
		}
	}

	public void setSsidTrafficMetrics(Calendar reportDateTime, long reportTimeAggregation) {
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//		Object values[] = new Object[3];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = reportSsidName;
//
//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		String sql ="select x.ifname,v.statTimeStamp,v.txVIfDataFrames,v.txVIfBeDataFrames,v.txVIfBgDataFrames," +
				"v.txVIfViDataFrames,v.txVIfVoDataFrames,v.txVIfUnicastDataFrames,v.txVIfMulticastDataFrames," +
				"v.txVIfBroadcastDataFrames,v.rxVIfDataFrames,v.rxVIfUnicastDataFrames,v.rxVIfMulticastDataFrames," +
				"v.rxVIfBroadcastDataFrames from hm_xif x, HM_VIFSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(reportSsidName) +
				"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";

		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
		
		AhVIfStats tmpPreviousValue = new AhVIfStats();
		AhVIfStats tmpPreviousValueSec = new AhVIfStats();
		AhVIfStats tmpNeedSaveValue = new AhVIfStats();

		stm_trans_totalData = new ArrayList<CheckItem>();
		stm_trans_beData = new ArrayList<CheckItem>();
		stm_trans_bgData = new ArrayList<CheckItem>();
		stm_trans_viData = new ArrayList<CheckItem>();
		stm_trans_voData = new ArrayList<CheckItem>();
		stm_trans_unicastData = new ArrayList<CheckItem>();
		stm_trans_multicastData = new ArrayList<CheckItem>();
		stm_trans_broadcastData = new ArrayList<CheckItem>();
		stm_rec_totalData = new ArrayList<CheckItem>();
		stm_rec_unicastData = new ArrayList<CheckItem>();
		stm_rec_multicastData = new ArrayList<CheckItem>();
		stm_rec_broadcastData = new ArrayList<CheckItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();

		boolean firstSave= false;
		String firstIfName= "";
		String secondIfName= "";
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			
			if (!firstSave) {
//				tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpPreviousValue.setTxVIfDataFrames(Long.parseLong(oneItem[2].toString()));
				tmpPreviousValue.setTxVIfBeDataFrames(Long.parseLong(oneItem[3].toString()));
				tmpPreviousValue.setTxVIfBgDataFrames(Long.parseLong(oneItem[4].toString()));
				tmpPreviousValue.setTxVIfViDataFrames(Long.parseLong(oneItem[5].toString()));
				tmpPreviousValue.setTxVIfVoDataFrames(Long.parseLong(oneItem[6].toString()));
				tmpPreviousValue.setTxVIfUnicastDataFrames(Long.parseLong(oneItem[7].toString()));
				tmpPreviousValue.setTxVIfMulticastDataFrames(Long.parseLong(oneItem[8].toString()));
				tmpPreviousValue.setTxVIfBroadcastDataFrames(Long.parseLong(oneItem[9].toString()));
				tmpPreviousValue.setRxVIfDataFrames(Long.parseLong(oneItem[10].toString()));
				tmpPreviousValue.setRxVIfUnicastDataFrames(Long.parseLong(oneItem[11].toString()));
				tmpPreviousValue.setRxVIfMulticastDataFrames(Long.parseLong(oneItem[12].toString()));
				tmpPreviousValue.setRxVIfBroadcastDataFrames(Long.parseLong(oneItem[13].toString()));
				firstSave = true;
				firstIfName = oneItem[0].toString();
				continue;
			}
			if (firstSave && secondIfName.equals("")) {
				if (!oneItem[0].toString().equals(firstIfName)) {
//					tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
					tmpPreviousValueSec.setTxVIfDataFrames(Long.parseLong(oneItem[2].toString()));
					tmpPreviousValueSec.setTxVIfBeDataFrames(Long.parseLong(oneItem[3].toString()));
					tmpPreviousValueSec.setTxVIfBgDataFrames(Long.parseLong(oneItem[4].toString()));
					tmpPreviousValueSec.setTxVIfViDataFrames(Long.parseLong(oneItem[5].toString()));
					tmpPreviousValueSec.setTxVIfVoDataFrames(Long.parseLong(oneItem[6].toString()));
					tmpPreviousValueSec.setTxVIfUnicastDataFrames(Long.parseLong(oneItem[7].toString()));
					tmpPreviousValueSec.setTxVIfMulticastDataFrames(Long.parseLong(oneItem[8].toString()));
					tmpPreviousValueSec.setTxVIfBroadcastDataFrames(Long.parseLong(oneItem[9].toString()));
					tmpPreviousValueSec.setRxVIfDataFrames(Long.parseLong(oneItem[10].toString()));
					tmpPreviousValueSec.setRxVIfUnicastDataFrames(Long.parseLong(oneItem[11].toString()));
					tmpPreviousValueSec.setRxVIfMulticastDataFrames(Long.parseLong(oneItem[12].toString()));
					tmpPreviousValueSec.setRxVIfBroadcastDataFrames(Long.parseLong(oneItem[13].toString()));
					secondIfName = oneItem[0].toString();
					break;
				}
			}
		}

		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				long txVIfDataFrames = Long.parseLong(oneItem[2].toString());
				long txVIfBeDataFrames=Long.parseLong(oneItem[3].toString());
				long txVIfBgDataFrames=Long.parseLong(oneItem[4].toString());
				long txVIfViDataFrames=Long.parseLong(oneItem[5].toString());
				long txVIfVoDataFrames=Long.parseLong(oneItem[6].toString());
				long txVIfUnicastDataFrames=Long.parseLong(oneItem[7].toString());
				long txVIfMulticastDataFrames=Long.parseLong(oneItem[8].toString());
				long txVIfBroadcastDataFrames=Long.parseLong(oneItem[9].toString());
				long rxVIfDataFrames=Long.parseLong(oneItem[10].toString());
				long rxVIfUnicastDataFrames=Long.parseLong(oneItem[11].toString());
				long rxVIfMulticastDataFrames=Long.parseLong(oneItem[12].toString());
				long rxVIfBroadcastDataFrames=Long.parseLong(oneItem[13].toString());
				
				if (cuTime <= reportTimeInMillis - reportTimeAggregation) {
					continue;
				}

				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					// transmit totalData
					long tmpCount;
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfDataFrames
								, tmpPreviousValue.getTxVIfDataFrames());
						tmpPreviousValue.setTxVIfDataFrames(txVIfDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfDataFrames
								, tmpPreviousValueSec.getTxVIfDataFrames());
						tmpPreviousValueSec.setTxVIfDataFrames(txVIfDataFrames);
					}
					tmpNeedSaveValue.setTxVIfDataFrames(tmpNeedSaveValue
							.getTxVIfDataFrames()
							+ tmpCount);

					// transmit beData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfBeDataFrames
								, tmpPreviousValue.getTxVIfBeDataFrames());
						tmpPreviousValue.setTxVIfBeDataFrames(txVIfBeDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfBeDataFrames
								, tmpPreviousValueSec.getTxVIfBeDataFrames());
						tmpPreviousValueSec.setTxVIfBeDataFrames(txVIfBeDataFrames);
					}
					tmpNeedSaveValue.setTxVIfBeDataFrames(tmpNeedSaveValue
							.getTxVIfBeDataFrames()
							+ tmpCount);

					// transmit bgData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfBgDataFrames
								, tmpPreviousValue.getTxVIfBgDataFrames());
						tmpPreviousValue.setTxVIfBgDataFrames(txVIfBgDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfBgDataFrames
								, tmpPreviousValueSec.getTxVIfBgDataFrames());
						tmpPreviousValueSec.setTxVIfBgDataFrames(txVIfBgDataFrames);
					}
					tmpNeedSaveValue.setTxVIfBgDataFrames(tmpNeedSaveValue
							.getTxVIfBgDataFrames()
							+ tmpCount);

					// transmit viData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfViDataFrames
								, tmpPreviousValue.getTxVIfViDataFrames());
						tmpPreviousValue.setTxVIfViDataFrames(txVIfViDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfViDataFrames
								, tmpPreviousValueSec.getTxVIfViDataFrames());
						tmpPreviousValueSec.setTxVIfViDataFrames(txVIfViDataFrames);
					}
					tmpNeedSaveValue.setTxVIfViDataFrames(tmpNeedSaveValue
							.getTxVIfViDataFrames()
							+ tmpCount);

					// transmit voData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfVoDataFrames
								, tmpPreviousValue.getTxVIfVoDataFrames());
						tmpPreviousValue.setTxVIfVoDataFrames(txVIfVoDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfVoDataFrames
								, tmpPreviousValueSec.getTxVIfVoDataFrames());
						tmpPreviousValueSec.setTxVIfVoDataFrames(txVIfVoDataFrames);
					}
					tmpNeedSaveValue.setTxVIfVoDataFrames(tmpNeedSaveValue
							.getTxVIfVoDataFrames()
							+ tmpCount);

					// transmit unicastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfUnicastDataFrames
								, tmpPreviousValue.getTxVIfUnicastDataFrames());
						tmpPreviousValue.setTxVIfUnicastDataFrames(txVIfUnicastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfUnicastDataFrames
								, tmpPreviousValueSec.getTxVIfUnicastDataFrames());
						tmpPreviousValueSec.setTxVIfUnicastDataFrames(txVIfUnicastDataFrames);
					}
					tmpNeedSaveValue.setTxVIfUnicastDataFrames(tmpNeedSaveValue
							.getTxVIfUnicastDataFrames()
							+ tmpCount);

					// transmit multicastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfMulticastDataFrames
								, tmpPreviousValue.getTxVIfMulticastDataFrames());
						tmpPreviousValue.setTxVIfMulticastDataFrames(txVIfMulticastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfMulticastDataFrames
								, tmpPreviousValueSec.getTxVIfMulticastDataFrames());
						tmpPreviousValueSec.setTxVIfMulticastDataFrames(txVIfMulticastDataFrames);
					}
					tmpNeedSaveValue.setTxVIfMulticastDataFrames(tmpNeedSaveValue
							.getTxVIfMulticastDataFrames()
							+ tmpCount);

					// transmit broadcastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(txVIfBroadcastDataFrames
								, tmpPreviousValue.getTxVIfBroadcastDataFrames());
						tmpPreviousValue.setTxVIfBroadcastDataFrames(txVIfBroadcastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(txVIfBroadcastDataFrames
								, tmpPreviousValueSec.getTxVIfBroadcastDataFrames());
						tmpPreviousValueSec.setTxVIfBroadcastDataFrames(txVIfBroadcastDataFrames);
					}
					tmpNeedSaveValue.setTxVIfBroadcastDataFrames(tmpNeedSaveValue
							.getTxVIfBroadcastDataFrames()
							+ tmpCount);

					// receive totalData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxVIfDataFrames
								, tmpPreviousValue.getRxVIfDataFrames());
						tmpPreviousValue.setRxVIfDataFrames(rxVIfDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(rxVIfDataFrames
								, tmpPreviousValueSec.getRxVIfDataFrames());
						tmpPreviousValueSec.setRxVIfDataFrames(rxVIfDataFrames);
					}
					tmpNeedSaveValue.setRxVIfDataFrames(tmpNeedSaveValue
							.getRxVIfDataFrames()
							+ tmpCount);

					// receive unicastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxVIfUnicastDataFrames
								, tmpPreviousValue.getRxVIfUnicastDataFrames());
						tmpPreviousValue.setRxVIfUnicastDataFrames(rxVIfUnicastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(rxVIfUnicastDataFrames
								, tmpPreviousValueSec.getRxVIfUnicastDataFrames());
						tmpPreviousValueSec.setRxVIfUnicastDataFrames(rxVIfUnicastDataFrames);
					}
					tmpNeedSaveValue.setRxVIfUnicastDataFrames(tmpNeedSaveValue
							.getRxVIfUnicastDataFrames()
							+ tmpCount);

					// receive multicastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxVIfMulticastDataFrames
								, tmpPreviousValue.getRxVIfMulticastDataFrames());
						tmpPreviousValue.setRxVIfMulticastDataFrames(rxVIfMulticastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(rxVIfMulticastDataFrames
								, tmpPreviousValueSec.getRxVIfMulticastDataFrames());
						tmpPreviousValueSec.setRxVIfMulticastDataFrames(rxVIfMulticastDataFrames);
					}
					tmpNeedSaveValue.setRxVIfMulticastDataFrames(tmpNeedSaveValue
							.getRxVIfMulticastDataFrames()
							+ tmpCount);

					// receive broadcastData
					if (cuIfname.equals(firstIfName)) {
						tmpCount = checkValueLessThanZero(rxVIfBroadcastDataFrames
								, tmpPreviousValue.getRxVIfBroadcastDataFrames());
						tmpPreviousValue.setRxVIfBroadcastDataFrames(rxVIfBroadcastDataFrames);
					} else {
						tmpCount = checkValueLessThanZero(rxVIfBroadcastDataFrames
								, tmpPreviousValueSec.getRxVIfBroadcastDataFrames());
						tmpPreviousValueSec.setRxVIfBroadcastDataFrames(rxVIfBroadcastDataFrames);
					}
					tmpNeedSaveValue.setRxVIfBroadcastDataFrames(tmpNeedSaveValue
							.getRxVIfBroadcastDataFrames()
							+ tmpCount);
				}
				
			}
			String tmpDateStringValue=AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			stm_trans_totalData.add(new CheckItem(tmpNeedSaveValue.getTxVIfDataFrames(),
					tmpDateStringValue));
			stm_trans_beData.add(new CheckItem(tmpNeedSaveValue.getTxVIfBeDataFrames(),
					tmpDateStringValue));
			stm_trans_bgData.add(new CheckItem(tmpNeedSaveValue.getTxVIfBgDataFrames(),
					tmpDateStringValue));
			stm_trans_viData.add(new CheckItem(tmpNeedSaveValue.getTxVIfViDataFrames(),
					tmpDateStringValue));
			stm_trans_voData.add(new CheckItem(tmpNeedSaveValue.getTxVIfVoDataFrames(),
					tmpDateStringValue));
			stm_trans_unicastData.add(new CheckItem(tmpNeedSaveValue
					.getTxVIfUnicastDataFrames(), tmpDateStringValue));
			stm_trans_multicastData.add(new CheckItem(tmpNeedSaveValue
					.getTxVIfMulticastDataFrames(), tmpDateStringValue));
			stm_trans_broadcastData.add(new CheckItem(tmpNeedSaveValue
					.getTxVIfBroadcastDataFrames(), tmpDateStringValue));
			stm_rec_totalData.add(new CheckItem(tmpNeedSaveValue.getRxVIfDataFrames(),
					tmpDateStringValue));
			stm_rec_unicastData.add(new CheckItem(tmpNeedSaveValue
					.getRxVIfUnicastDataFrames(), tmpDateStringValue));
			stm_rec_multicastData.add(new CheckItem(tmpNeedSaveValue
					.getRxVIfMulticastDataFrames(), tmpDateStringValue));
			stm_rec_broadcastData.add(new CheckItem(tmpNeedSaveValue
					.getRxVIfBroadcastDataFrames(), tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpNeedSaveValue = new AhVIfStats();
		}
	}

	public void setRadioTroubleShooting(Calendar reportDateTime, long reportTimeAggregation) {
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//		if (getDataSource().getRole() == AhReport.REPORT_ROLE_ACCESS) {
//			searchSQL = searchSQL + " AND ifMode=" + AhReport.REPORT_ROLE_ACCESS;
//		} else if (getDataSource().getRole() == AhReport.REPORT_ROLE_BACKHAUL) {
//			searchSQL = searchSQL + " AND ifMode=" + AhReport.REPORT_ROLE_BACKHAUL;
//		}
//
//		Object values[] = new Object[4];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = "wifi0";
//		values[3] = "wifi1";
//
//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		String sql ="select x.ifname,v.statTimeStamp,v.radioTxTotalRetries,v.radioTxTotalFramesDropped," +
				"v.radioTxTotalFrameErrors,v.radioTxFEForExcessiveHWRetries," +
				"v.radioTXRTSFailures,v.radioRxTotalFrameDropped" +
				" from hm_xif x, HM_RADIOSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) +
				"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
				"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
		
		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
		
		
		AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
		AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();

		rts_trans_wifi0_totalRetries = new ArrayList<CheckItem>();
		rts_trans_wifi1_totalRetries = new ArrayList<CheckItem>();
		rts_trans_wifi0_totalFramesDropped = new ArrayList<CheckItem>();
		rts_trans_wifi1_totalFramesDropped = new ArrayList<CheckItem>();
		rts_trans_wifi0_totalFrameErrors = new ArrayList<CheckItem>();
		rts_trans_wifi1_totalFrameErrors = new ArrayList<CheckItem>();
		rts_trans_wifi0_feForExcessiveHWRetries = new ArrayList<CheckItem>();
		rts_trans_wifi1_feForExcessiveHWRetries = new ArrayList<CheckItem>();
		rts_trans_wifi0_rtsFailures = new ArrayList<CheckItem>();
		rts_trans_wifi1_rtsFailures = new ArrayList<CheckItem>();
		rts_rec_wifi0_totalFrameDropped = new ArrayList<CheckItem>();
		rts_rec_wifi1_totalFrameDropped = new ArrayList<CheckItem>();

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//				tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi0PreviousValue.setRadioTxTotalRetries(Long.parseLong(oneItem[2].toString()));
				tmpWifi0PreviousValue.setRadioTxTotalFramesDropped(Long.parseLong(oneItem[3].toString()));
				tmpWifi0PreviousValue.setRadioTxTotalFrameErrors(Long.parseLong(oneItem[4].toString()));
				tmpWifi0PreviousValue.setRadioTxFEForExcessiveHWRetries(Long.parseLong(oneItem[5].toString()));
				tmpWifi0PreviousValue.setRadioTXRTSFailures(Long.parseLong(oneItem[6].toString()));
				tmpWifi0PreviousValue.setRadioRxTotalFrameDropped(Long.parseLong(oneItem[7].toString()));
				break;
			}
		}
		
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi1")) {
//				tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi1PreviousValue.setRadioTxTotalRetries(Long.parseLong(oneItem[2].toString()));
				tmpWifi1PreviousValue.setRadioTxTotalFramesDropped(Long.parseLong(oneItem[3].toString()));
				tmpWifi1PreviousValue.setRadioTxTotalFrameErrors(Long.parseLong(oneItem[4].toString()));
				tmpWifi1PreviousValue.setRadioTxFEForExcessiveHWRetries(Long.parseLong(oneItem[5].toString()));
				tmpWifi1PreviousValue.setRadioTXRTSFailures(Long.parseLong(oneItem[6].toString()));
				tmpWifi1PreviousValue.setRadioRxTotalFrameDropped(Long.parseLong(oneItem[7].toString()));
				break;
			}
		}
		
		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				long radioTxTotalRetries= Long.parseLong(oneItem[2].toString());
				long radioTxTotalFramesDropped= Long.parseLong(oneItem[3].toString());
				long radioTxTotalFrameErrors= Long.parseLong(oneItem[4].toString());
				long radioTxFEForExcessiveHWRetries= Long.parseLong(oneItem[5].toString());
				long radioTXRTSFailures= Long.parseLong(oneItem[6].toString());
				long radioRxTotalFrameDropped= Long.parseLong(oneItem[7].toString());
				
				if (cuTime <= reportTimeInMillis - reportTimeAggregation) {
					continue;
				}
				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					if (cuIfname.equalsIgnoreCase("wifi0")) {
						// transmit wifi0_totalRetries
						long tmpCount = checkValueLessThanZero(radioTxTotalRetries, tmpWifi0PreviousValue
								.getRadioTxTotalRetries());
						tmpWifi0NeedSaveValue.setRadioTxTotalRetries(tmpWifi0NeedSaveValue
								.getRadioTxTotalRetries()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxTotalRetries(radioTxTotalRetries);
						// transmit wifi0_totalFramesDropped
						tmpCount = checkValueLessThanZero(radioTxTotalFramesDropped, tmpWifi0PreviousValue
								.getRadioTxTotalFramesDropped());
						tmpWifi0NeedSaveValue
								.setRadioTxTotalFramesDropped(tmpWifi0NeedSaveValue
										.getRadioTxTotalFramesDropped()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxTotalFramesDropped(radioTxTotalFramesDropped);
						// transmit wifi0_totalFrameErrors
						tmpCount = checkValueLessThanZero(radioTxTotalFrameErrors, tmpWifi0PreviousValue
								.getRadioTxTotalFrameErrors());
						tmpWifi0NeedSaveValue
								.setRadioTxTotalFrameErrors(tmpWifi0NeedSaveValue
										.getRadioTxTotalFrameErrors()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxTotalFrameErrors(radioTxTotalFrameErrors);
						// transmit wifi0_feForExcessiveHWRetries
						tmpCount = checkValueLessThanZero(radioTxFEForExcessiveHWRetries, tmpWifi0PreviousValue
								.getRadioTxFEForExcessiveHWRetries());
						tmpWifi0NeedSaveValue
								.setRadioTxFEForExcessiveHWRetries(tmpWifi0NeedSaveValue
										.getRadioTxFEForExcessiveHWRetries()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxFEForExcessiveHWRetries(radioTxFEForExcessiveHWRetries);
						// transmit wifi0_rtsFailures
						tmpCount = checkValueLessThanZero(radioTXRTSFailures, tmpWifi0PreviousValue
								.getRadioTXRTSFailures());
						tmpWifi0NeedSaveValue.setRadioTXRTSFailures(tmpWifi0NeedSaveValue
								.getRadioTXRTSFailures()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTXRTSFailures(radioTXRTSFailures);
						// receive wifi0_totalFrameDropped
						tmpCount = checkValueLessThanZero(radioRxTotalFrameDropped, tmpWifi0PreviousValue
								.getRadioRxTotalFrameDropped());
						tmpWifi0NeedSaveValue
								.setRadioRxTotalFrameDropped(tmpWifi0NeedSaveValue
										.getRadioRxTotalFrameDropped()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxTotalFrameDropped(radioRxTotalFrameDropped);
					} else if (cuIfname.equalsIgnoreCase("wifi1")) {
						// transmit wifi1_totalRetries
						long tmpCount = checkValueLessThanZero(radioTxTotalRetries, tmpWifi1PreviousValue
								.getRadioTxTotalRetries());
						tmpWifi1NeedSaveValue.setRadioTxTotalRetries(tmpWifi1NeedSaveValue
								.getRadioTxTotalRetries()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxTotalRetries(radioTxTotalRetries);
						// transmit wifi1_totalFramesDropped
						tmpCount = checkValueLessThanZero(radioTxTotalFramesDropped, tmpWifi1PreviousValue
								.getRadioTxTotalFramesDropped());
						tmpWifi1NeedSaveValue
								.setRadioTxTotalFramesDropped(tmpWifi1NeedSaveValue
										.getRadioTxTotalFramesDropped()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxTotalFramesDropped(radioTxTotalFramesDropped);
						// transmit wifi1_totalFrameErrors
						tmpCount = checkValueLessThanZero(radioTxTotalFrameErrors, tmpWifi1PreviousValue
								.getRadioTxTotalFrameErrors());
						tmpWifi1NeedSaveValue
								.setRadioTxTotalFrameErrors(tmpWifi1NeedSaveValue
										.getRadioTxTotalFrameErrors()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxTotalFrameErrors(radioTxTotalFrameErrors);
						// transmit wifi1_feForExcessiveHWRetries
						tmpCount = checkValueLessThanZero(radioTxFEForExcessiveHWRetries, tmpWifi1PreviousValue
								.getRadioTxFEForExcessiveHWRetries());
						tmpWifi1NeedSaveValue
								.setRadioTxFEForExcessiveHWRetries(tmpWifi1NeedSaveValue
										.getRadioTxFEForExcessiveHWRetries()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxFEForExcessiveHWRetries(radioTxFEForExcessiveHWRetries);
						// transmit wifi1_rtsFailures
						tmpCount = checkValueLessThanZero(radioTXRTSFailures, tmpWifi1PreviousValue
								.getRadioTXRTSFailures());
						tmpWifi1NeedSaveValue.setRadioTXRTSFailures(tmpWifi1NeedSaveValue
								.getRadioTXRTSFailures()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTXRTSFailures(radioTXRTSFailures);
						// receive wifi1_totalFrameDropped
						tmpCount = checkValueLessThanZero(radioRxTotalFrameDropped, tmpWifi1PreviousValue
								.getRadioRxTotalFrameDropped());
						tmpWifi1NeedSaveValue
								.setRadioRxTotalFrameDropped(tmpWifi1NeedSaveValue
										.getRadioRxTotalFrameDropped()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxTotalFrameDropped(radioRxTotalFrameDropped);
					}
				}
				
			}
			String tmpDateStringValue=AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			rts_trans_wifi0_totalRetries.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxTotalRetries(), tmpDateStringValue));
			rts_trans_wifi1_totalRetries.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxTotalRetries(), tmpDateStringValue));
			rts_trans_wifi0_totalFramesDropped.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxTotalFramesDropped(), tmpDateStringValue));
			rts_trans_wifi1_totalFramesDropped.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxTotalFramesDropped(), tmpDateStringValue));
			rts_trans_wifi0_totalFrameErrors.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxTotalFrameErrors(), tmpDateStringValue));
			rts_trans_wifi1_totalFrameErrors.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxTotalFrameErrors(), tmpDateStringValue));
			rts_trans_wifi0_feForExcessiveHWRetries.add(new CheckItem(
					tmpWifi0NeedSaveValue.getRadioTxFEForExcessiveHWRetries(),
					tmpDateStringValue));
			rts_trans_wifi1_feForExcessiveHWRetries.add(new CheckItem(
					tmpWifi1NeedSaveValue.getRadioTxFEForExcessiveHWRetries(),
					tmpDateStringValue));
			rts_trans_wifi0_rtsFailures.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTXRTSFailures(), tmpDateStringValue));
			rts_trans_wifi1_rtsFailures.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTXRTSFailures(), tmpDateStringValue));
			rts_rec_wifi0_totalFrameDropped.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxTotalFrameDropped(), tmpDateStringValue));
			rts_rec_wifi1_totalFrameDropped.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxTotalFrameDropped(), tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpWifi0NeedSaveValue = new AhRadioStats();
			tmpWifi1NeedSaveValue = new AhRadioStats();
		}
	}
	
	public void setChannelPowerNoiseNew(Calendar reportDateTime, long reportTimeAggregation) throws Exception {
		List<AhInterfaceStats> lstInterfaceInfo = QueryUtil.executeQuery(AhInterfaceStats.class,
				new SortParams("timeStamp"), 
				new FilterParams("timeStamp>=:s1 and apName=:s2",
						new Object[]{reportDateTime.getTimeInMillis(),
									reportAPName}),
				getDomain().getId());		
		hiveap_wifi0_rec_rateTypeList = new ArrayList<String>();
		hiveap_wifi0_rec_dateTimeList = new ArrayList<String>();
		hiveap_wifi0_trans_rateTypeList = new ArrayList<String>();
		hiveap_wifi0_trans_dateTimeList = new ArrayList<String>();
		hiveap_wifi0_rec_rate_dis = new HashMap<String, List<CheckItem>>();
		hiveap_wifi0_rec_rate_succ_dis = new ArrayList<TextItem>();
		hiveap_wifi0_trans_rate_dis = new HashMap<String, List<CheckItem>>();
		hiveap_wifi0_trans_rate_succ_dis = new ArrayList<TextItem>();
		
//		hiveap_wifi0_trans_rate_total_succ_dis = new ArrayList<TextItem>();
//		hiveap_wifi0_rec_rate_total_succ_dis = new ArrayList<TextItem>();
		
		hiveap_wifi0_rec_unicast = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_unicast = new ArrayList<CheckItem>();
		hiveap_wifi0_rec_broadcast = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_broadcast = new ArrayList<CheckItem>();
		hiveap_wifi0_rec_drops = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_drops = new ArrayList<CheckItem>();
		hiveap_wifi0_rec_totalU = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_totalU = new ArrayList<CheckItem>();
		hiveap_wifi0_rec_retryRateU = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_retryRateU = new ArrayList<CheckItem>();
		hiveap_wifi0_rec_airTimeU = new ArrayList<CheckItem>();
		hiveap_wifi0_trans_airTimeU = new ArrayList<CheckItem>();
		hiveap_wifi0_crcErrorRateU = new ArrayList<CheckItem>();
		hiveap_wifi0_totalChannelU = new ArrayList<CheckItem>();
		hiveap_wifi0_InterferenceU = new ArrayList<CheckItem>();
		hiveap_wifi0_noiseFloor = new ArrayList<CheckItem>();
		hiveap_wifi0_bandsteering=new ArrayList<CheckItem>();
		hiveap_wifi0_loadbalance=new ArrayList<CheckItem>();
		hiveap_wifi0_weaksnr=new ArrayList<CheckItem>();
		hiveap_wifi0_safetynet=new ArrayList<CheckItem>();
		hiveap_wifi0_proberequest=new ArrayList<CheckItem>();
		hiveap_wifi0_authrequest=new ArrayList<CheckItem>();
		
		hiveap_wifi1_rec_rateTypeList = new ArrayList<String>();
		hiveap_wifi1_rec_dateTimeList = new ArrayList<String>();
		hiveap_wifi1_trans_rateTypeList = new ArrayList<String>();
		hiveap_wifi1_trans_dateTimeList = new ArrayList<String>();
		hiveap_wifi1_rec_rate_dis = new HashMap<String, List<CheckItem>>();
		hiveap_wifi1_rec_rate_succ_dis = new ArrayList<TextItem>();
		hiveap_wifi1_trans_rate_dis = new HashMap<String, List<CheckItem>>();
		hiveap_wifi1_trans_rate_succ_dis = new ArrayList<TextItem>();
		
//		hiveap_wifi1_trans_rate_total_succ_dis = new ArrayList<TextItem>();
//		hiveap_wifi1_rec_rate_total_succ_dis = new ArrayList<TextItem>();
		
		hiveap_wifi1_rec_unicast = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_unicast = new ArrayList<CheckItem>();
		hiveap_wifi1_rec_broadcast = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_broadcast = new ArrayList<CheckItem>();
		hiveap_wifi1_rec_drops = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_drops = new ArrayList<CheckItem>();
		hiveap_wifi1_rec_totalU = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_totalU = new ArrayList<CheckItem>();
		hiveap_wifi1_rec_retryRateU = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_retryRateU = new ArrayList<CheckItem>();
		hiveap_wifi1_rec_airTimeU = new ArrayList<CheckItem>();
		hiveap_wifi1_trans_airTimeU = new ArrayList<CheckItem>();
		hiveap_wifi1_crcErrorRateU = new ArrayList<CheckItem>();
		hiveap_wifi1_totalChannelU = new ArrayList<CheckItem>();
		hiveap_wifi1_InterferenceU = new ArrayList<CheckItem>();
		hiveap_wifi1_noiseFloor = new ArrayList<CheckItem>();
		hiveap_wifi1_bandsteering=new ArrayList<CheckItem>();
		hiveap_wifi1_loadbalance=new ArrayList<CheckItem>();
		hiveap_wifi1_weaksnr=new ArrayList<CheckItem>();
		hiveap_wifi1_safetynet=new ArrayList<CheckItem>();
		hiveap_wifi1_proberequest=new ArrayList<CheckItem>();
		hiveap_wifi1_authrequest=new ArrayList<CheckItem>();
		
		for(AhInterfaceStats oneBo:lstInterfaceInfo){
			String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(oneBo.getTimeStamp(),tz);
			
			if (oneBo.getIfName().equals("wifi0")){
				hiveap_wifi0_rec_unicast.add(new CheckItem(oneBo.getUniRxFrameCount(), starTime));
				hiveap_wifi0_trans_unicast.add(new CheckItem(oneBo.getUniTxFrameCount(), starTime));
				hiveap_wifi0_rec_broadcast.add(new CheckItem(oneBo.getBcastRxFrameCount(), starTime));
				hiveap_wifi0_trans_broadcast.add(new CheckItem(oneBo.getBcastTxFrameCount(), starTime));
				hiveap_wifi0_rec_drops.add(new CheckItem(oneBo.getRxDrops(), starTime));
				hiveap_wifi0_trans_drops.add(new CheckItem(oneBo.getTxDrops(), starTime));
				hiveap_wifi0_rec_totalU.add(new CheckItem((long) oneBo.getRxUtilization(), starTime));
				hiveap_wifi0_trans_totalU.add(new CheckItem((long) oneBo.getTxUtilization(), starTime));
				hiveap_wifi0_rec_retryRateU.add(new CheckItem((long) oneBo.getRxRetryRate(), starTime));
				hiveap_wifi0_trans_retryRateU.add(new CheckItem((long) oneBo.getTxRetryRate(), starTime));
				hiveap_wifi0_rec_airTimeU.add(new CheckItem((long) oneBo.getRxAirTime(), starTime));
				hiveap_wifi0_trans_airTimeU.add(new CheckItem((long) oneBo.getTxAirTime(), starTime));
				hiveap_wifi0_crcErrorRateU.add(new CheckItem((long) oneBo.getCrcErrorRate(), starTime));
				hiveap_wifi0_totalChannelU.add(new CheckItem((long) oneBo.getTotalChannelUtilization(), starTime));
				hiveap_wifi0_InterferenceU.add(new CheckItem((long) oneBo.getInterferenceUtilization(), starTime));
				hiveap_wifi0_noiseFloor.add(new CheckItem((long) oneBo.getNoiseFloor(), starTime));
				hiveap_wifi0_bandsteering.add(new CheckItem((long) oneBo.getBandSteerSuppressCount(), starTime));
				hiveap_wifi0_loadbalance.add(new CheckItem((long) oneBo.getLoadBalanceSuppressCount(), starTime));
				hiveap_wifi0_weaksnr.add(new CheckItem((long) oneBo.getWeakSnrSuppressCount(), starTime));
				hiveap_wifi0_safetynet.add(new CheckItem((long) oneBo.getSafetyNetAnswerCount(), starTime));
				hiveap_wifi0_proberequest.add(new CheckItem((long) oneBo.getProbeRequestSuppressCount(), starTime));
				hiveap_wifi0_authrequest.add(new CheckItem((long) oneBo.getAuthRequestSuppressCount(), starTime));
				
//				if (hiveap_wifi0_trans_dateTimeList.size()<=200 || operation.equals("createDownloadData")){
				int totalPencent=0;
				int rateCount=0;
				StringBuilder wifi0TxRateSucValue = new StringBuilder();
				if (oneBo.getTxRateInfo()!=null && !oneBo.getTxRateInfo().equals("")){
					String[] txRate=oneBo.getTxRateInfo().split(";");
				
					hiveap_wifi0_trans_dateTimeList.add(starTime);
					for (String rate : txRate) {
						if (!rate.equals("")) {
							String[] oneRec = rate.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!hiveap_wifi0_trans_rateTypeList.contains(oneRec[0])) {
									hiveap_wifi0_trans_rateTypeList.add(oneRec[0]);
								}
								if (hiveap_wifi0_trans_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									hiveap_wifi0_trans_rate_dis.put(starTime, tmpArray);
								} else {
									hiveap_wifi0_trans_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2]) * Integer.parseInt(oneRec[1]);
								wifi0TxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
									.append(oneRec[2]).append("%; ");
							}
						}
					}
				}
//				hiveap_wifi0_trans_rate_succ_dis.add(new TextItem(starTime,wifi0TxRateSucValue.toString()));
				
				if (oneBo.getTotalTxBitSuccessRate()==0) {
					if (rateCount!=0) {
						hiveap_wifi0_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),wifi0TxRateSucValue.toString()));
//						hiveap_wifi0_trans_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						hiveap_wifi0_trans_rate_succ_dis.add(new TextItem(starTime,"0",wifi0TxRateSucValue.toString()));
//						hiveap_wifi0_trans_rate_total_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					hiveap_wifi0_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate()),wifi0TxRateSucValue.toString()));
//					hiveap_wifi0_trans_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate())+ "%"));
				}
				
//				}
				
//				if (hiveap_wifi0_rec_dateTimeList.size()<=200  || operation.equals("createDownloadData")){
				totalPencent=0;
				rateCount=0;
				StringBuilder wifi0RxRateSucValue = new StringBuilder();
				if (oneBo.getRxRateInfo()!=null && !oneBo.getRxRateInfo().equals("")){
					String[] rxRate=oneBo.getRxRateInfo().split(";");
					hiveap_wifi0_rec_dateTimeList.add(starTime);
					for (String rate : rxRate) {
						if (!rate.equals("")) {
							String[] oneRec = rate.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!hiveap_wifi0_rec_rateTypeList.contains(oneRec[0])) {
									hiveap_wifi0_rec_rateTypeList.add(oneRec[0]);
								}
								if (hiveap_wifi0_rec_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									hiveap_wifi0_rec_rate_dis.put(starTime, tmpArray);
								} else {
									hiveap_wifi0_rec_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
								wifi0RxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
								.append(oneRec[2]).append("%; ");
							}
						}
					}
				}
				
				
				if (oneBo.getTotalRxBitSuccessRate()==0) {
					if (rateCount!=0) {
						hiveap_wifi0_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),wifi0RxRateSucValue.toString()));
//						hiveap_wifi0_rec_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						hiveap_wifi0_rec_rate_succ_dis.add(new TextItem(starTime,"0",wifi0RxRateSucValue.toString()));
//						hiveap_wifi0_rec_rate_total_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					hiveap_wifi0_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate()),wifi0RxRateSucValue.toString()));
//					hiveap_wifi0_rec_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate())+ "%"));
				}
				
//				}
			} else if (oneBo.getIfName().equals("wifi1")){
				hiveap_wifi1_rec_unicast.add(new CheckItem(oneBo.getUniRxFrameCount(), starTime));
				hiveap_wifi1_trans_unicast.add(new CheckItem(oneBo.getUniTxFrameCount(), starTime));
				hiveap_wifi1_rec_broadcast.add(new CheckItem(oneBo.getBcastRxFrameCount(), starTime));
				hiveap_wifi1_trans_broadcast.add(new CheckItem(oneBo.getBcastTxFrameCount(), starTime));
				hiveap_wifi1_rec_drops.add(new CheckItem(oneBo.getRxDrops(), starTime));
				hiveap_wifi1_trans_drops.add(new CheckItem(oneBo.getTxDrops(), starTime));
				hiveap_wifi1_rec_totalU.add(new CheckItem((long) oneBo.getRxUtilization(), starTime));
				hiveap_wifi1_trans_totalU.add(new CheckItem((long) oneBo.getTxUtilization(), starTime));
				hiveap_wifi1_rec_retryRateU.add(new CheckItem((long) oneBo.getRxRetryRate(), starTime));
				hiveap_wifi1_trans_retryRateU.add(new CheckItem((long) oneBo.getTxRetryRate(), starTime));
				hiveap_wifi1_rec_airTimeU.add(new CheckItem((long) oneBo.getRxAirTime(), starTime));
				hiveap_wifi1_trans_airTimeU.add(new CheckItem((long) oneBo.getTxAirTime(), starTime));
				hiveap_wifi1_crcErrorRateU.add(new CheckItem((long) oneBo.getCrcErrorRate(), starTime));
				hiveap_wifi1_totalChannelU.add(new CheckItem((long) oneBo.getTotalChannelUtilization(), starTime));
				hiveap_wifi1_InterferenceU.add(new CheckItem((long) oneBo.getInterferenceUtilization(), starTime));
				hiveap_wifi1_noiseFloor.add(new CheckItem((long) oneBo.getNoiseFloor(), starTime));
				hiveap_wifi1_bandsteering.add(new CheckItem((long) oneBo.getBandSteerSuppressCount(), starTime));
				hiveap_wifi1_loadbalance.add(new CheckItem((long) oneBo.getLoadBalanceSuppressCount(), starTime));
				hiveap_wifi1_weaksnr.add(new CheckItem((long) oneBo.getWeakSnrSuppressCount(), starTime));
				hiveap_wifi1_safetynet.add(new CheckItem((long) oneBo.getSafetyNetAnswerCount(), starTime));
				hiveap_wifi1_proberequest.add(new CheckItem((long) oneBo.getProbeRequestSuppressCount(), starTime));
				hiveap_wifi1_authrequest.add(new CheckItem((long) oneBo.getAuthRequestSuppressCount(), starTime));
				
//				if (hiveap_wifi1_trans_dateTimeList.size()<=200 || operation.equals("createDownloadData")){
				int totalPencent=0;
				int rateCount=0;
				StringBuilder wifi1TxRateSucValue = new StringBuilder();
				if (oneBo.getTxRateInfo()!=null && !oneBo.getTxRateInfo().equals("")){
					String[] txRate=oneBo.getTxRateInfo().split(";");
				
					hiveap_wifi1_trans_dateTimeList.add(starTime);
					
					for (String rate : txRate) {
						if (!rate.equals("")) {
							String[] oneRec = rate.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!hiveap_wifi1_trans_rateTypeList.contains(oneRec[0])) {
									hiveap_wifi1_trans_rateTypeList.add(oneRec[0]);
								}
								if (hiveap_wifi1_trans_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									hiveap_wifi1_trans_rate_dis.put(starTime, tmpArray);
								} else {
									hiveap_wifi1_trans_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
								wifi1TxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
								.append(oneRec[2]).append("%; ");
								
							}
						}
					}
				}
				
				
				if (oneBo.getTotalTxBitSuccessRate()==0) {
					if (rateCount!=0) {
						hiveap_wifi1_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),wifi1TxRateSucValue.toString()));
//						hiveap_wifi1_trans_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						hiveap_wifi1_trans_rate_succ_dis.add(new TextItem(starTime,"0",wifi1TxRateSucValue.toString()));
//						hiveap_wifi1_trans_rate_total_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					hiveap_wifi1_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate()),wifi1TxRateSucValue.toString()));
//					hiveap_wifi1_trans_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate())+ "%"));
				}
				
//				}
//				if (hiveap_wifi1_rec_dateTimeList.size()<=200 || operation.equals("createDownloadData")){
				
				totalPencent=0;
				rateCount=0;
				StringBuilder wifi1RxRateSucValue = new StringBuilder();
				if (oneBo.getRxRateInfo()!=null && !oneBo.getRxRateInfo().equals("")){
					String[] rxRate=oneBo.getRxRateInfo().split(";");
					hiveap_wifi1_rec_dateTimeList.add(starTime);
					for (String rate : rxRate) {
						if (!rate.equals("")) {
							String[] oneRec = rate.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!hiveap_wifi1_rec_rateTypeList.contains(oneRec[0])) {
									hiveap_wifi1_rec_rateTypeList.add(oneRec[0]);
								}
								if (hiveap_wifi1_rec_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									hiveap_wifi1_rec_rate_dis.put(starTime, tmpArray);
								} else {
									hiveap_wifi1_rec_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
								wifi1RxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
								.append(oneRec[2]).append("%; ");
							}
						}
					}
				}
				
				if (oneBo.getTotalRxBitSuccessRate()==0) {
					if (rateCount!=0) {
						hiveap_wifi1_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),wifi1RxRateSucValue.toString()));
//						hiveap_wifi1_rec_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						hiveap_wifi1_rec_rate_succ_dis.add(new TextItem(starTime,"0",wifi1RxRateSucValue.toString()));
//						hiveap_wifi1_rec_rate_total_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					hiveap_wifi1_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate()),wifi1RxRateSucValue.toString()));
//					hiveap_wifi1_rec_rate_total_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate())+ "%"));
				}
				
//				}
			}
		}
		Collections.sort(hiveap_wifi0_rec_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		Collections.sort(hiveap_wifi0_trans_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		Collections.sort(hiveap_wifi1_rec_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
		Collections.sort(hiveap_wifi1_trans_rateTypeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					
					long rate1 = Long.parseLong(o1);
					long rate2 = Long.parseLong(o2);
					if (rate1-rate2>0) {
						return 1;
					} else if (rate1-rate2<0) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}

	public void setChannelPowerNoise(Calendar reportDateTime, long reportTimeAggregation) {
		String sqlString = "select distinct ifindex, ifname from hm_xif where apname='"+ NmsUtil.convertSqlStr(reportAPName) + 
			"' and statTimeStamp >= " + reportDateTime.getTimeInMillis() +
			" and (ifname='wifi0' or ifname='wifi1')";
		List<?> lstIfIndex = QueryUtil.executeNativeQuery(sqlString);
		int wifi0IfIndex = -1;
		int wifi1IfIndex = -1;
		int wifi1_2IfIndex = -1;
		if (lstIfIndex!=null && lstIfIndex.size()>0){
			boolean setBefore = false;
			for(Object tmpObj:lstIfIndex){
				Object[] objIfInfo = (Object[]) tmpObj;
				if (objIfInfo[1].toString().toLowerCase().equals("wifi0")){
					wifi0IfIndex = Integer.parseInt(objIfInfo[0].toString());
				}
				if (objIfInfo[1].toString().toLowerCase().equals("wifi1")){
					if (!setBefore){
						wifi1IfIndex = Integer.parseInt(objIfInfo[0].toString());
						setBefore = true;
					} else {
						wifi1_2IfIndex = Integer.parseInt(objIfInfo[0].toString());
					}
				}
			}
		}

		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (xifpk.ifIndex =:s3 OR xifpk.ifIndex =:s4 OR xifpk.ifIndex =:s5)";

		Object values[] = new Object[5];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = reportAPName;
		values[2] = wifi0IfIndex;
		values[3] = wifi1IfIndex;
		values[4] = wifi1_2IfIndex;
		List<AhRadioAttribute> lstAttributeInfo = QueryUtil.executeQuery(AhRadioAttribute.class, new SortParams("xifpk.statTimeStamp"),
				new FilterParams(searchSQL, values), getDomain().getId());
		rcpn_wifi0_channel = new ArrayList<CheckItem>();
		rcpn_wifi1_channel = new ArrayList<CheckItem>();
		rcpn_wifi0_power = new ArrayList<CheckItem>();
		rcpn_wifi1_power = new ArrayList<CheckItem>();
		rcpn_wifi0_noise = new ArrayList<CheckItem>();
		rcpn_wifi1_noise = new ArrayList<CheckItem>();

		AhRadioAttribute tmpWifi0NeedSaveValue = new AhRadioAttribute();
		AhRadioAttribute tmpWifi1NeedSaveValue = new AhRadioAttribute();

		int tmpWifi0Count = 0;
		int tmpWifi1Count = 0;
		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		while (reportTimeInMillis <= systemTimeInLong) {
			for (AhRadioAttribute ahRadioAttribute : lstAttributeInfo) {
				if (ahRadioAttribute.getXifpk().getStatTimeValue() <= reportTimeInMillis
						- reportTimeAggregation) {
					continue;
				}
				if (ahRadioAttribute.getXifpk().getStatTimeValue() > reportTimeInMillis) {
					break;
				} else {
					if (ahRadioAttribute.getXifpk().getIfIndex() == wifi0IfIndex) {
						tmpWifi0NeedSaveValue.setRadioChannel(ahRadioAttribute.getRadioChannel());
						tmpWifi0NeedSaveValue.setRadioTxPower(tmpWifi0NeedSaveValue
								.getRadioTxPower()
								+ ahRadioAttribute.getRadioTxPower());
						tmpWifi0NeedSaveValue.setRadioNoiseFloor(tmpWifi0NeedSaveValue
								.getRadioNoiseFloor()
								+ ahRadioAttribute.getRadioNoiseFloor());
						tmpWifi0Count++;
					} else if (ahRadioAttribute.getXifpk().getIfIndex() == wifi1IfIndex
							|| ahRadioAttribute.getXifpk().getIfIndex() == wifi1_2IfIndex) {
						tmpWifi1NeedSaveValue.setRadioChannel(ahRadioAttribute.getRadioChannel());
						tmpWifi1NeedSaveValue.setRadioTxPower(tmpWifi1NeedSaveValue
								.getRadioTxPower()
								+ ahRadioAttribute.getRadioTxPower());
						tmpWifi1NeedSaveValue.setRadioNoiseFloor(tmpWifi1NeedSaveValue
								.getRadioNoiseFloor()
								+ ahRadioAttribute.getRadioNoiseFloor());
						tmpWifi1Count++;
					}
				}
			}
			if (tmpWifi0Count == 0) {
				tmpWifi0NeedSaveValue.setRadioNoiseFloor(-95);
				tmpWifi0Count = 1;
			}
			if (tmpWifi1Count == 0) {
				tmpWifi1NeedSaveValue.setRadioNoiseFloor(-95);
				tmpWifi1Count = 1;
			}
			String tmpDateStringValue=AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			rcpn_wifi0_channel.add(new CheckItem(tmpWifi0NeedSaveValue.getRadioChannel()
					, tmpDateStringValue));
			rcpn_wifi1_channel.add(new CheckItem(tmpWifi1NeedSaveValue.getRadioChannel()
					, tmpDateStringValue));
			rcpn_wifi0_power.add(new CheckItem(tmpWifi0NeedSaveValue.getRadioTxPower()
					/ tmpWifi0Count, tmpDateStringValue));
			rcpn_wifi1_power.add(new CheckItem(tmpWifi1NeedSaveValue.getRadioTxPower()
					/ tmpWifi1Count, tmpDateStringValue));
			rcpn_wifi0_noise.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioNoiseFloor()
					/ tmpWifi0Count, tmpDateStringValue));
			rcpn_wifi1_noise.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioNoiseFloor()
					/ tmpWifi1Count, tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpWifi0NeedSaveValue = new AhRadioAttribute();
			tmpWifi1NeedSaveValue = new AhRadioAttribute();
			tmpWifi1Count = 0;
			tmpWifi0Count = 0;
		}
	}
	
	public void setRadioInterference(Calendar reportDateTime, long reportTimeAggregation) {
		String searchSQL = "timeStamp.time >= :s1 AND apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
		Object values[] = new Object[4];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = reportAPName;
		values[2] = "wifi0";
		values[3] = "wifi1";

		List<AhInterferenceStats> lstInterfaceInfo = QueryUtil.executeQuery(AhInterferenceStats.class, new SortParams("timeStamp.time"), new FilterParams(searchSQL, values),
				getDomain().getId());
		wifi0_averageTxCu=new ArrayList<CheckItem>();
		wifi0_averageRxCu=new ArrayList<CheckItem>();
		wifi0_averageInterferenceCu=new ArrayList<CheckItem>();
		wifi0_averageNoiseFloor=new ArrayList<CheckItem>();
		wifi0_shortTermTxCu=new ArrayList<CheckItem>();
		wifi0_shortTermRxCu=new ArrayList<CheckItem>();
		wifi0_shortTermInterferenceCu=new ArrayList<CheckItem>();
		wifi0_shortTermNoiseFloor=new ArrayList<CheckItem>();
		wifi0_snapShotTxCu=new ArrayList<CheckItem>();
		wifi0_snapShotRxCu=new ArrayList<CheckItem>();
		wifi0_snapShotInterferenceCu=new ArrayList<CheckItem>();
		wifi0_snapShotNoiseFloor=new ArrayList<CheckItem>();
		wifi0_crcError=new ArrayList<CheckItem>();

		wifi1_averageTxCu=new ArrayList<CheckItem>();
		wifi1_averageRxCu=new ArrayList<CheckItem>();
		wifi1_averageInterferenceCu=new ArrayList<CheckItem>();
		wifi1_averageNoiseFloor=new ArrayList<CheckItem>();
		wifi1_shortTermTxCu=new ArrayList<CheckItem>();
		wifi1_shortTermRxCu=new ArrayList<CheckItem>();
		wifi1_shortTermInterferenceCu=new ArrayList<CheckItem>();
		wifi1_shortTermNoiseFloor=new ArrayList<CheckItem>();
		wifi1_snapShotTxCu=new ArrayList<CheckItem>();
		wifi1_snapShotRxCu=new ArrayList<CheckItem>();
		wifi1_snapShotInterferenceCu=new ArrayList<CheckItem>();
		wifi1_snapShotNoiseFloor=new ArrayList<CheckItem>();
		wifi1_crcError=new ArrayList<CheckItem>();

		for (AhInterferenceStats ahInterferenceStats : lstInterfaceInfo) {
			if (ahInterferenceStats != null) {
				if (ahInterferenceStats.getIfName().equalsIgnoreCase("wifi0")) {
					// wifi0_averageTxCu
					String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(ahInterferenceStats.getTimeStamp().getTime(), tz);

					wifi0_averageTxCu.add(new CheckItem((long) ahInterferenceStats.getAverageTXCU(),
							tmpDateStringValue));
					wifi0_averageRxCu.add(new CheckItem((long) ahInterferenceStats
							.getAverageRXCU(), tmpDateStringValue));
					wifi0_averageInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getAverageInterferenceCU(), tmpDateStringValue));
					wifi0_averageNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getAverageNoiseFloor(), tmpDateStringValue));
					wifi0_shortTermTxCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermTXCU(), tmpDateStringValue));
					wifi0_shortTermRxCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermRXCU(), tmpDateStringValue));
					wifi0_shortTermInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermInterferenceCU(), tmpDateStringValue));
					wifi0_shortTermNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getShortTermNoiseFloor(), tmpDateStringValue));
					wifi0_snapShotTxCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotTXCU(), tmpDateStringValue));
					wifi0_snapShotRxCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotRXCU(), tmpDateStringValue));
					wifi0_snapShotInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotInterferenceCU(), tmpDateStringValue));
					wifi0_snapShotNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotNoiseFloor(), tmpDateStringValue));
					wifi0_crcError.add(new CheckItem((long) ahInterferenceStats
							.getCrcError(), tmpDateStringValue));
				} else if (ahInterferenceStats.getIfName().equalsIgnoreCase("wifi1")) {
					// wifi1_averageTxCu
					String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(ahInterferenceStats.getTimeStamp().getTime(), tz);
					wifi1_averageTxCu.add(new CheckItem((long) ahInterferenceStats
							.getAverageTXCU(), tmpDateStringValue));
					wifi1_averageRxCu.add(new CheckItem((long) ahInterferenceStats
							.getAverageRXCU(), tmpDateStringValue));
					wifi1_averageInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getAverageInterferenceCU(), tmpDateStringValue));
					wifi1_averageNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getAverageNoiseFloor(), tmpDateStringValue));
					wifi1_shortTermTxCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermTXCU(), tmpDateStringValue));
					wifi1_shortTermRxCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermRXCU(), tmpDateStringValue));
					wifi1_shortTermInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getShortTermInterferenceCU(), tmpDateStringValue));
					wifi1_shortTermNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getShortTermNoiseFloor(), tmpDateStringValue));
					wifi1_snapShotTxCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotTXCU(), tmpDateStringValue));
					wifi1_snapShotRxCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotRXCU(), tmpDateStringValue));
					wifi1_snapShotInterferenceCu.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotInterferenceCU(), tmpDateStringValue));
					wifi1_snapShotNoiseFloor.add(new CheckItem((long) ahInterferenceStats
							.getSnapShotNoiseFloor(), tmpDateStringValue));
					wifi1_crcError.add(new CheckItem((long) ahInterferenceStats
							.getCrcError(), tmpDateStringValue));
				}
			}
		}
	}
	
	public void setRadioTrafficMetrics(Calendar reportDateTime, long reportTimeAggregation) {
//		String searchSQL = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//		if (getDataSource().getRole() == AhReport.REPORT_ROLE_ACCESS) {
//			searchSQL = searchSQL + " AND ifMode=" + AhReport.REPORT_ROLE_ACCESS;
//		} else if (getDataSource().getRole() == AhReport.REPORT_ROLE_BACKHAUL) {
//			searchSQL = searchSQL + " AND ifMode=" + AhReport.REPORT_ROLE_BACKHAUL;
//		}
//
//		Object values[] = new Object[4];
//		values[0] = reportDateTime.getTimeInMillis();
//		values[1] = reportAPName;
//		values[2] = "wifi0";
//		values[3] = "wifi1";
//
//		List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams("xifpk.statTimeStamp"), new FilterParams(searchSQL, values),
//				getDomain().getId());
		
		String sql ="select x.ifname,v.statTimeStamp,v.radioTxDataFrames,v.radioTxBeDataFrames," +
				" v.radioTxBgDataFrames,v.radioTxViDataFrames,v.radioTxVoDataFrames,v.radioTxUnicastDataFrames," +
				" v.radioTxMulticastDataFrames,v.radioTxBroadcastDataFrames,v.radioTxNonBeaconMgtFrames,v.radioTxBeaconFrames," +
				" v.radioRxTotalDataFrames,v.radioRxUnicastDataFrames,v.radioRxMulticastDataFrames,v.radioRxBroadcastDataFrames," +
				" v.radioRxMgtFrames from hm_xif x, HM_RADIOSTATS v where " +
				"x.owner=" + getDomain().getId() + "and v.owner=" + getDomain().getId() +
				" and v.statTimeStamp >= " + reportDateTime.getTimeInMillis() + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) +
				"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
				"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
		
		List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);
		
		AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
		AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
		AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();

		rtm_trans_wifi0_totalData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_beData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_bgData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_viData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_voData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_unicastData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_multicastData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_broadcastData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_nonBeaconMgtData = new ArrayList<CheckItem>();
		rtm_trans_wifi0_beaconData = new ArrayList<CheckItem>();

		rtm_rec_wifi0_totalData = new ArrayList<CheckItem>();
		rtm_rec_wifi0_unicastData = new ArrayList<CheckItem>();
		rtm_rec_wifi0_multicastData = new ArrayList<CheckItem>();
		rtm_rec_wifi0_broadcastData = new ArrayList<CheckItem>();
		rtm_rec_wifi0_mgtData = new ArrayList<CheckItem>();

		rtm_trans_wifi1_totalData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_beData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_bgData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_viData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_voData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_unicastData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_multicastData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_broadcastData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_nonBeaconMgtData = new ArrayList<CheckItem>();
		rtm_trans_wifi1_beaconData = new ArrayList<CheckItem>();

		rtm_rec_wifi1_totalData = new ArrayList<CheckItem>();
		rtm_rec_wifi1_unicastData = new ArrayList<CheckItem>();
		rtm_rec_wifi1_multicastData = new ArrayList<CheckItem>();
		rtm_rec_wifi1_broadcastData = new ArrayList<CheckItem>();
		rtm_rec_wifi1_mgtData = new ArrayList<CheckItem>();

		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//				tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi0PreviousValue.setRadioTxDataFrames(Long.parseLong(oneItem[2].toString()));
				tmpWifi0PreviousValue.setRadioTxBeDataFrames(Long.parseLong(oneItem[3].toString()));
				tmpWifi0PreviousValue.setRadioTxBgDataFrames(Long.parseLong(oneItem[4].toString()));
				tmpWifi0PreviousValue.setRadioTxViDataFrames(Long.parseLong(oneItem[5].toString()));
				tmpWifi0PreviousValue.setRadioTxVoDataFrames(Long.parseLong(oneItem[6].toString()));
				tmpWifi0PreviousValue.setRadioTxUnicastDataFrames(Long.parseLong(oneItem[7].toString()));
				tmpWifi0PreviousValue.setRadioTxMulticastDataFrames(Long.parseLong(oneItem[8].toString()));
				tmpWifi0PreviousValue.setRadioTxBroadcastDataFrames(Long.parseLong(oneItem[9].toString()));
				tmpWifi0PreviousValue.setRadioTxNonBeaconMgtFrames(Long.parseLong(oneItem[10].toString()));
				tmpWifi0PreviousValue.setRadioTxBeaconFrames(Long.parseLong(oneItem[11].toString()));
				tmpWifi0PreviousValue.setRadioRxTotalDataFrames(Long.parseLong(oneItem[12].toString()));
				tmpWifi0PreviousValue.setRadioRxUnicastDataFrames(Long.parseLong(oneItem[13].toString()));
				tmpWifi0PreviousValue.setRadioRxMulticastDataFrames(Long.parseLong(oneItem[14].toString()));
				tmpWifi0PreviousValue.setRadioRxBroadcastDataFrames(Long.parseLong(oneItem[15].toString()));
				tmpWifi0PreviousValue.setRadioRxMgtFrames(Long.parseLong(oneItem[16].toString()));
				break;
			}
		}
		
		for (Object oneObj : lstInterfaceInfo) {
			Object[] oneItem = (Object[])oneObj;
			if (oneItem[0].toString().equalsIgnoreCase("wifi1")) {
//				tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
				tmpWifi1PreviousValue.setRadioTxDataFrames(Long.parseLong(oneItem[2].toString()));
				tmpWifi1PreviousValue.setRadioTxBeDataFrames(Long.parseLong(oneItem[3].toString()));
				tmpWifi1PreviousValue.setRadioTxBgDataFrames(Long.parseLong(oneItem[4].toString()));
				tmpWifi1PreviousValue.setRadioTxViDataFrames(Long.parseLong(oneItem[5].toString()));
				tmpWifi1PreviousValue.setRadioTxVoDataFrames(Long.parseLong(oneItem[6].toString()));
				tmpWifi1PreviousValue.setRadioTxUnicastDataFrames(Long.parseLong(oneItem[7].toString()));
				tmpWifi1PreviousValue.setRadioTxMulticastDataFrames(Long.parseLong(oneItem[8].toString()));
				tmpWifi1PreviousValue.setRadioTxBroadcastDataFrames(Long.parseLong(oneItem[9].toString()));
				tmpWifi1PreviousValue.setRadioTxNonBeaconMgtFrames(Long.parseLong(oneItem[10].toString()));
				tmpWifi1PreviousValue.setRadioTxBeaconFrames(Long.parseLong(oneItem[11].toString()));
				tmpWifi1PreviousValue.setRadioRxTotalDataFrames(Long.parseLong(oneItem[12].toString()));
				tmpWifi1PreviousValue.setRadioRxUnicastDataFrames(Long.parseLong(oneItem[13].toString()));
				tmpWifi1PreviousValue.setRadioRxMulticastDataFrames(Long.parseLong(oneItem[14].toString()));
				tmpWifi1PreviousValue.setRadioRxBroadcastDataFrames(Long.parseLong(oneItem[15].toString()));
				tmpWifi1PreviousValue.setRadioRxMgtFrames(Long.parseLong(oneItem[16].toString()));
				break;
			}
		}

		Calendar tmpDate = Calendar.getInstance(tz);
		tmpDate.setTimeInMillis(reportDateTime.getTimeInMillis());
		long reportTimeInMillis = reportDateTime.getTimeInMillis();
		long systemTimeInLong = System.currentTimeMillis();
		while (reportTimeInMillis <= systemTimeInLong) {
			for (Object oneObj : lstInterfaceInfo) {
				Object[] oneItem = (Object[])oneObj;
				String cuIfname = oneItem[0].toString();
				long cuTime = Long.parseLong(oneItem[1].toString());
				long radioTxDataFrames = Long.parseLong(oneItem[2].toString());
				long radioTxBeDataFrames = Long.parseLong(oneItem[3].toString());
				long radioTxBgDataFrames = Long.parseLong(oneItem[4].toString());
				long radioTxViDataFrames = Long.parseLong(oneItem[5].toString());
				long radioTxVoDataFrames = Long.parseLong(oneItem[6].toString());
				long radioTxUnicastDataFrames = Long.parseLong(oneItem[7].toString());
				long radioTxMulticastDataFrames = Long.parseLong(oneItem[8].toString());
				long radioTxBroadcastDataFrames = Long.parseLong(oneItem[9].toString());
				long radioTxNonBeaconMgtFrames = Long.parseLong(oneItem[10].toString());
				long radioTxBeaconFrames = Long.parseLong(oneItem[11].toString());
				long radioRxTotalDataFrames = Long.parseLong(oneItem[12].toString());
				long radioRxUnicastDataFrames = Long.parseLong(oneItem[13].toString());
				long radioRxMulticastDataFrames = Long.parseLong(oneItem[14].toString());
				long radioRxBroadcastDataFrames = Long.parseLong(oneItem[15].toString());
				long radioRxMgtFrames = Long.parseLong(oneItem[16].toString());
				
				if (cuTime <= reportTimeInMillis - reportTimeAggregation) {
					continue;
				}
				if (cuTime > reportTimeInMillis) {
					break;
				} else {
					if (cuIfname.equalsIgnoreCase("wifi0")) {
						// transmit wifi0_totalData
						long tmpCount = checkValueLessThanZero(radioTxDataFrames, tmpWifi0PreviousValue
								.getRadioTxDataFrames());
						tmpWifi0NeedSaveValue.setRadioTxDataFrames(tmpWifi0NeedSaveValue
								.getRadioTxDataFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxDataFrames(radioTxDataFrames);
						// transmit wifi0_beData
						tmpCount = checkValueLessThanZero(radioTxBeDataFrames, tmpWifi0PreviousValue
								.getRadioTxBeDataFrames());
						tmpWifi0NeedSaveValue.setRadioTxBeDataFrames(tmpWifi0NeedSaveValue
								.getRadioTxBeDataFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxBeDataFrames(radioTxBeDataFrames);
						// transmit wifi0_bgData
						tmpCount = checkValueLessThanZero(radioTxBgDataFrames, tmpWifi0PreviousValue
								.getRadioTxBgDataFrames());
						tmpWifi0NeedSaveValue.setRadioTxBgDataFrames(tmpWifi0NeedSaveValue
								.getRadioTxBgDataFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxBgDataFrames(radioTxBgDataFrames);
						// transmit wifi0_viData
						tmpCount = checkValueLessThanZero(radioTxViDataFrames, tmpWifi0PreviousValue
								.getRadioTxViDataFrames());
						tmpWifi0NeedSaveValue.setRadioTxViDataFrames(tmpWifi0NeedSaveValue
								.getRadioTxViDataFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxViDataFrames(radioTxViDataFrames);
						// transmit wifi0_voData
						tmpCount = checkValueLessThanZero(radioTxVoDataFrames, tmpWifi0PreviousValue
								.getRadioTxVoDataFrames());
						tmpWifi0NeedSaveValue.setRadioTxVoDataFrames(tmpWifi0NeedSaveValue
								.getRadioTxVoDataFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxVoDataFrames(radioTxVoDataFrames);
						// transmit wifi0_unicastData
						tmpCount = checkValueLessThanZero(radioTxUnicastDataFrames, tmpWifi0PreviousValue
								.getRadioTxUnicastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioTxUnicastDataFrames(tmpWifi0NeedSaveValue
										.getRadioTxUnicastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxUnicastDataFrames(radioTxUnicastDataFrames);
						// transmit wifi0_multicastData
						tmpCount = checkValueLessThanZero(radioTxMulticastDataFrames, tmpWifi0PreviousValue
								.getRadioTxMulticastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioTxMulticastDataFrames(tmpWifi0NeedSaveValue
										.getRadioTxMulticastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxMulticastDataFrames(radioTxMulticastDataFrames);
						// transmit wifi0_broadcastData
						tmpCount = checkValueLessThanZero(radioTxBroadcastDataFrames, tmpWifi0PreviousValue
								.getRadioTxBroadcastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioTxBroadcastDataFrames(tmpWifi0NeedSaveValue
										.getRadioTxBroadcastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxBroadcastDataFrames(radioTxBroadcastDataFrames);
						// transmit wifi0_nonBeaconData
						tmpCount = checkValueLessThanZero(radioTxNonBeaconMgtFrames, tmpWifi0PreviousValue
								.getRadioTxNonBeaconMgtFrames());
						tmpWifi0NeedSaveValue
								.setRadioTxNonBeaconMgtFrames(tmpWifi0NeedSaveValue
										.getRadioTxNonBeaconMgtFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxNonBeaconMgtFrames(radioTxNonBeaconMgtFrames);
						// transmit wifi0_BeaconData
						tmpCount = checkValueLessThanZero(radioTxBeaconFrames, tmpWifi0PreviousValue
								.getRadioTxBeaconFrames());
						tmpWifi0NeedSaveValue.setRadioTxBeaconFrames(tmpWifi0NeedSaveValue
								.getRadioTxBeaconFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioTxBeaconFrames(radioTxBeaconFrames);
						// receive wifi0_totalData
						tmpCount = checkValueLessThanZero(radioRxTotalDataFrames, tmpWifi0PreviousValue
								.getRadioRxTotalDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioRxTotalDataFrames(tmpWifi0NeedSaveValue
										.getRadioRxTotalDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxTotalDataFrames(radioRxTotalDataFrames);
						// receive wifi0_unicastData
						tmpCount = checkValueLessThanZero(radioRxUnicastDataFrames, tmpWifi0PreviousValue
								.getRadioRxUnicastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioRxUnicastDataFrames(tmpWifi0NeedSaveValue
										.getRadioRxUnicastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxUnicastDataFrames(radioRxUnicastDataFrames);
						// receive wifi0_multicastData
						tmpCount = checkValueLessThanZero(radioRxMulticastDataFrames, tmpWifi0PreviousValue
								.getRadioRxMulticastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioRxMulticastDataFrames(tmpWifi0NeedSaveValue
										.getRadioRxMulticastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxMulticastDataFrames(radioRxMulticastDataFrames);
						// receive wifi0_broadcastData
						tmpCount = checkValueLessThanZero(radioRxBroadcastDataFrames, tmpWifi0PreviousValue
								.getRadioRxBroadcastDataFrames());
						tmpWifi0NeedSaveValue
								.setRadioRxBroadcastDataFrames(tmpWifi0NeedSaveValue
										.getRadioRxBroadcastDataFrames()
										+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxBroadcastDataFrames(radioRxBroadcastDataFrames);
						// receive wifi0_mgtData
						tmpCount = checkValueLessThanZero(radioRxMgtFrames, tmpWifi0PreviousValue
								.getRadioRxMgtFrames());
						tmpWifi0NeedSaveValue.setRadioRxMgtFrames(tmpWifi0NeedSaveValue
								.getRadioRxMgtFrames()
								+ tmpCount);
						tmpWifi0PreviousValue.setRadioRxMgtFrames(radioRxMgtFrames);
					} else if (cuIfname.equalsIgnoreCase("wifi1")) {
						// transmit wifi1_totalData
						long tmpCount = checkValueLessThanZero(radioTxDataFrames, tmpWifi1PreviousValue
								.getRadioTxDataFrames());
						tmpWifi1NeedSaveValue.setRadioTxDataFrames(tmpWifi1NeedSaveValue
								.getRadioTxDataFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxDataFrames(radioTxDataFrames);
						// transmit wifi1_beData
						tmpCount = checkValueLessThanZero(radioTxBeDataFrames, tmpWifi1PreviousValue
								.getRadioTxBeDataFrames());
						tmpWifi1NeedSaveValue.setRadioTxBeDataFrames(tmpWifi1NeedSaveValue
								.getRadioTxBeDataFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxBeDataFrames(radioTxBeDataFrames);
						// transmit wifi1_bgData
						tmpCount = checkValueLessThanZero(radioTxBgDataFrames, tmpWifi1PreviousValue
								.getRadioTxBgDataFrames());
						tmpWifi1NeedSaveValue.setRadioTxBgDataFrames(tmpWifi1NeedSaveValue
								.getRadioTxBgDataFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxBgDataFrames(radioTxBgDataFrames);
						// transmit wifi1_viData
						tmpCount = checkValueLessThanZero(radioTxViDataFrames, tmpWifi1PreviousValue
								.getRadioTxViDataFrames());
						tmpWifi1NeedSaveValue.setRadioTxViDataFrames(tmpWifi1NeedSaveValue
								.getRadioTxViDataFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxViDataFrames(radioTxViDataFrames);
						// transmit wifi1_voData
						tmpCount = checkValueLessThanZero(radioTxVoDataFrames, tmpWifi1PreviousValue
								.getRadioTxVoDataFrames());
						tmpWifi1NeedSaveValue.setRadioTxVoDataFrames(tmpWifi1NeedSaveValue
								.getRadioTxVoDataFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxVoDataFrames(radioTxVoDataFrames);
						// transmit wifi1_unicastData
						tmpCount = checkValueLessThanZero(radioTxUnicastDataFrames, tmpWifi1PreviousValue
								.getRadioTxUnicastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioTxUnicastDataFrames(tmpWifi1NeedSaveValue
										.getRadioTxUnicastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxUnicastDataFrames(radioTxUnicastDataFrames);
						// transmit wifi1_multicastData
						tmpCount = checkValueLessThanZero(radioTxMulticastDataFrames, tmpWifi1PreviousValue
								.getRadioTxMulticastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioTxMulticastDataFrames(tmpWifi1NeedSaveValue
										.getRadioTxMulticastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxMulticastDataFrames(radioTxMulticastDataFrames);
						// transmit wifi1_broadcastData
						tmpCount = checkValueLessThanZero(radioTxBroadcastDataFrames, tmpWifi1PreviousValue
								.getRadioTxBroadcastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioTxBroadcastDataFrames(tmpWifi1NeedSaveValue
										.getRadioTxBroadcastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxBroadcastDataFrames(radioTxBroadcastDataFrames);
						// transmit wifi1_nonBeaconData
						tmpCount = checkValueLessThanZero(radioTxNonBeaconMgtFrames, tmpWifi1PreviousValue
								.getRadioTxNonBeaconMgtFrames());
						tmpWifi1NeedSaveValue
								.setRadioTxNonBeaconMgtFrames(tmpWifi1NeedSaveValue
										.getRadioTxNonBeaconMgtFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxNonBeaconMgtFrames(radioTxNonBeaconMgtFrames);
						// transmit wifi1_BeaconData
						tmpCount = checkValueLessThanZero(radioTxBeaconFrames, tmpWifi1PreviousValue
								.getRadioTxBeaconFrames());
						tmpWifi1NeedSaveValue.setRadioTxBeaconFrames(tmpWifi1NeedSaveValue
								.getRadioTxBeaconFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioTxBeaconFrames(radioTxBeaconFrames);
						// receive wifi1_totalData
						tmpCount = checkValueLessThanZero(radioRxTotalDataFrames, tmpWifi1PreviousValue
								.getRadioRxTotalDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioRxTotalDataFrames(tmpWifi1NeedSaveValue
										.getRadioRxTotalDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxTotalDataFrames(radioRxTotalDataFrames);
						// receive wifi1_unicastData
						tmpCount = checkValueLessThanZero(radioRxUnicastDataFrames, tmpWifi1PreviousValue
								.getRadioRxUnicastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioRxUnicastDataFrames(tmpWifi1NeedSaveValue
										.getRadioRxUnicastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxUnicastDataFrames(radioRxUnicastDataFrames);
						// receive wifi1_multicastData
						tmpCount = checkValueLessThanZero(radioRxMulticastDataFrames, tmpWifi1PreviousValue
								.getRadioRxMulticastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioRxMulticastDataFrames(tmpWifi1NeedSaveValue
										.getRadioRxMulticastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxMulticastDataFrames(radioRxMulticastDataFrames);
						// receive wifi1_broadcastData
						tmpCount = checkValueLessThanZero(radioRxBroadcastDataFrames, tmpWifi1PreviousValue
								.getRadioRxBroadcastDataFrames());
						tmpWifi1NeedSaveValue
								.setRadioRxBroadcastDataFrames(tmpWifi1NeedSaveValue
										.getRadioRxBroadcastDataFrames()
										+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxBroadcastDataFrames(radioRxBroadcastDataFrames);
						// receive wifi1_mgtData
						tmpCount = checkValueLessThanZero(radioRxMgtFrames, tmpWifi1PreviousValue
								.getRadioRxMgtFrames());
						tmpWifi1NeedSaveValue.setRadioRxMgtFrames(tmpWifi1NeedSaveValue
								.getRadioRxMgtFrames()
								+ tmpCount);
						tmpWifi1PreviousValue.setRadioRxMgtFrames(radioRxMgtFrames);
					}
				}
				
			}
			String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate.getTimeInMillis(),tz);
			rtm_trans_wifi0_totalData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_beData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxBeDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_bgData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxBgDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_viData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxViDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_voData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxVoDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_unicastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxUnicastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_multicastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxMulticastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_broadcastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxBroadcastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi0_nonBeaconMgtData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxNonBeaconMgtFrames(), tmpDateStringValue));
			rtm_trans_wifi0_beaconData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioTxBeaconFrames(), tmpDateStringValue));
			rtm_rec_wifi0_totalData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxTotalDataFrames(), tmpDateStringValue));
			rtm_rec_wifi0_unicastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxUnicastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi0_multicastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxMulticastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi0_broadcastData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxBroadcastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi0_mgtData.add(new CheckItem(tmpWifi0NeedSaveValue
					.getRadioRxMgtFrames(), tmpDateStringValue));

			rtm_trans_wifi1_totalData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_beData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxBeDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_bgData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxBgDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_viData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxViDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_voData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxVoDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_unicastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxUnicastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_multicastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxMulticastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_broadcastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxBroadcastDataFrames(), tmpDateStringValue));
			rtm_trans_wifi1_nonBeaconMgtData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxNonBeaconMgtFrames(), tmpDateStringValue));
			rtm_trans_wifi1_beaconData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioTxBeaconFrames(), tmpDateStringValue));
			rtm_rec_wifi1_totalData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxTotalDataFrames(), tmpDateStringValue));
			rtm_rec_wifi1_unicastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxUnicastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi1_multicastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxMulticastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi1_broadcastData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxBroadcastDataFrames(), tmpDateStringValue));
			rtm_rec_wifi1_mgtData.add(new CheckItem(tmpWifi1NeedSaveValue
					.getRadioRxMgtFrames(), tmpDateStringValue));

			reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
			tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			tmpWifi0NeedSaveValue = new AhRadioStats();
			tmpWifi1NeedSaveValue = new AhRadioStats();
		}
	}

	public long checkValueLessThanZero(long value) {
		if (value < 0) {
			return 0;
		}
		return value;
	}

	public long checkValueLessThanZero(long value1, long value2) {
		if (value1 - value2 < 0) {
			return value1;
		}
		return value1 - value2;
	}

	public double checkValueLessThanZero(double value1, double value2) {
		if (value1 - value2 < 0) {
			return value1;
		}
		return value1 - value2;
	}

	public String getShowRole() {
//		if (listType.equals("radioTrafficMetrics") || listType.equals("radioTroubleShooting")) {
//			return "";
//		}
		return "none";
	}
	
	public String getShowComplianceType() {
		if (getDataSource().getReportType().equals(L2_FEATURE_SECURITYCOMPLIANCE)
				|| getDataSource().getReportType().equals(L2_FEATURE_HIVEAPSLA)
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
			return "";
		}
		return "none";
	}

	public String getShowLocation() {
		if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME) 
				|| getDataSource().getReportType().equals(L2_FEATURE_MESHNEIGHBORS)
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITYROGUEAPS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITYROGUECLIENTS)
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)
				|| getDataSource().getReportType().equals(L2_FEATURE_SUMMARYUSAGE)
				|| getDataSource().getReportType().equals(L2_FEATURE_DETAILUSAGE)
				|| getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITY_NONHIVEAP)
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITY_NONCLIENT)) {
			return "none";
		}
		return "";
	}
	
	public String getShowSSID() {
		if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)
				|| getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
			return "";
		}
		return "none";
	}

	public String getShowApName() {
		if (getDataSource().getReportType().equals(L2_FEATURE_MOSTCLIENTSAPS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITYROGUEAPS)
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITYROGUECLIENTS) 
				|| getDataSource().getReportType().equals(L2_FEATURE_INVENTORY)
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTAUTH)
				|| getDataSource().getReportType().equals(L2_FEATURE_SUMMARYUSAGE)
				|| getDataSource().getReportType().equals(L2_FEATURE_DETAILUSAGE)
				|| getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)) {
			return "none";
		}
		return "";
	}

	public String getShowClientMacAddress() {
		if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAUTH) 
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME) 
				|| getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
			return "";
		}
		return "none";
	}
	
	public String getShowClientMacAddressOnly() {
		if (getDataSource().getReportType().equals(L2_FEATURE_SECURITY_NONCLIENT)){
			return "";
		} else {
			return getShowClientMacAddress();
		}
	}

	public String getShowClientAuth() {
		if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAUTH)) {
			return "";
		}
		return "none";
	}

	public String getShowReportPeriod() {
		if (getDataSource().getReportType().equals(L2_FEATURE_INVENTORY) 
//				|| getDataSource().getReportType().equals("clientAuth")
//				|| getDataSource().getReportType().equals("clientVendor") 
				|| getDataSource().getReportType().equals(L2_FEATURE_SECURITYCOMPLIANCE)) {
			return "none";
		}
		return "";
	}
	
	public String getHideTimeAggregation(){
		if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTAUTH)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_CLIENTSESSION)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_RADIOINTERFERENCE)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_CLIENTVENDOR)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SUMMARYUSAGE)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_DETAILUSAGE)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_MAXCLIENTREPORT)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_HIVEAPCONNECTION)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SECURITY_NONHIVEAP)
				||getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SECURITY_NONCLIENT)){
			return "none";
		}
		if (getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_NEW) {
			if (getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_CHANNELPOWERNOISE)){
				return "none";
			}
		}
		return "";
	}

	private String startTime;

	private int startHour;

	private Long locationId;

	private boolean showReportTab;

	private String swf, width, height, application, bgcolor;

	private Set<String> lstHiveAPName;

	private String reportAPName;

	private String reportSsidName;

	private List<String> lstReportSsidName;

	private String reportNeighborAP;

	private List<String> lstReportNeighborAP;

	private Set<String> lstReportClientMac;
	private String reportClientMac;
	private String reportClientMacWhole;
	private String reportLinkClientMac;
	private Map<String, String> pageSessionList = new HashMap<String, String>();
	private String currentPageSession;
	private int currentPage;

	private Map<String, List<ClientSessionButton>> mapLstReportClientSession;
	private List<ClientSessionButton> currentLstReportClientSession;
	private String reportClientSession;

	private String tabIndex = "";

	private String selectHiveAPName;
	
	private int linkComplianceType;
	
	private int reportPeriodFromClient=1;

//	private JSONObject jsonObject = null;

//	public String getJSONString() {
//		return jsonObject.toString();
//	}

	public AhReport getDataSource() {
		return (AhReport) dataSource;
	}

	public boolean getShowReportTab() {
		return showReportTab;
	}

	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getRunReportTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(tz);
		return formatter.format(new Date());
	}

	public int getNameLength() {
		return getAttributeLength("name");
	}

	public EnumItem[] getEnumRole() {
		return AhReport.REPORT_ROLE_TYPE;
	}
	
	public Map<String,String> getEnumReportType(){
		Map<String,String> typeList = new HashMap<String,String>();
		if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CHANNELPOWERNOISE)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTROUBLESHOOTING)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTRAFFICMETRICS)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_HIVEAPSLA)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOAIRTIME)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOINTERFERENCE)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UNIQUECLIENTCOUNT)){
			typeList.put(L2_FEATURE_CHANNELPOWERNOISE,"Channel/Power/Noise");
			typeList.put(L2_FEATURE_RADIOTROUBLESHOOTING,"Troubleshooting");
			typeList.put(L2_FEATURE_RADIOTRAFFICMETRICS,"Traffic Metrics");
			typeList.put(L2_FEATURE_HIVEAPSLA,NmsUtil.getOEMCustomer().getAccessPonitName() + " SLA");
			typeList.put(L2_FEATURE_RADIOAIRTIME,"Device Airtime Usage");
			typeList.put(L2_FEATURE_RADIOINTERFERENCE,"Interference");
			typeList.put(L2_FEATURE_UNIQUECLIENTCOUNT,"Unique Client Count");
			
		} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTRAFFICMETRICS)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTROUBLESHOOTING)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDAIRTIME)){
			typeList.put(L2_FEATURE_SSIDTRAFFICMETRICS,"Traffic Metrics");
			typeList.put(L2_FEATURE_SSIDTROUBLESHOOTING,"Troubleshooting");
			typeList.put(L2_FEATURE_SSIDAIRTIME,"Airtime Usage");
		} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSESSION)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSLA)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTAIRTIME)){
			typeList.put(L2_FEATURE_CLIENTSESSION,"Client Sessions");
			typeList.put(L2_FEATURE_CLIENTSLA,"Client SLA");
			typeList.put(L2_FEATURE_CLIENTAIRTIME,"Airtime Usage");
		}
		return typeList;
	}
	
	public String getShowReportType(){
		if (getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_OLD){
			if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CHANNELPOWERNOISE)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTROUBLESHOOTING)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTRAFFICMETRICS)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_HIVEAPSLA)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOAIRTIME)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOINTERFERENCE)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UNIQUECLIENTCOUNT)
					
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTRAFFICMETRICS)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTROUBLESHOOTING)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDAIRTIME)
					
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSESSION)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTAIRTIME)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSLA) ){
				return "";
			}
		} else {
			if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTRAFFICMETRICS)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDTROUBLESHOOTING)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_SSIDAIRTIME)){
				return "";
			}
		}
		return "none";
	}
	public String getShowNewOldReportType(){
		if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CHANNELPOWERNOISE)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTROUBLESHOOTING)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOTRAFFICMETRICS)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_HIVEAPSLA)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOAIRTIME)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_RADIOINTERFERENCE)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UNIQUECLIENTCOUNT)
				
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSESSION)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTAIRTIME)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_CLIENTSLA) ){
			return "";
		}
		return "none";
	}


	public EnumItem[] getEnumComplianceType() {
		if (listType.equalsIgnoreCase("compliance")){
			return AhReport.REPORT_COMPLIANCEPOLICY_RESULT;
		} else {
			return AhReport.REPORT_SLA_TYPE_RESULT;
		}
	}

	public EnumItem[] getEnumReportPeriod() {
		return AhReport.REPORT_PERIOD_TYPE;
	}

	public EnumItem[] getEnumReportAuthType() {
		return AhReport.REPORT_AUTH_TYPE;
	}

	public EnumItem[] getEnumTimeAggregation() {
		return AhReport.TIME_AGGREGATION_TYPE;
	}

	public EnumItem[] getEnumWeekDay() {
		return AhReport.REPORT_WEEKDAY_TYPE;
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		listLocation.add(0, new CheckItem((long) -1, ""));
		return listLocation;
	}

	public List<CheckItem> getLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i++) {
			lstHour.add(new CheckItem((long) i, String.valueOf(i + " hour")));
		}
		return lstHour;
	}

	public String getHideSchedule() {
		if (getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowRecurrence() {
		if (getDataSource().getEnabledRecurrence() && getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public String getSwf() {
		return swf;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getApplication() {
		return application;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public Set<String> getLstHiveAPName() {
		if (lstHiveAPName == null) {
			lstHiveAPName = new HashSet<String>();
			lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstHiveAPName;
	}

	public String getReportAPName() {
		if (reportAPName == null || reportAPName.equals("")) {
			return MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return reportAPName;
	}

	public List<TextItem> getWifi1_receive_airTime() {
		return wifi1_receive_airTime;
	}

	public List<TextItem> getWifi1_transmit_airTime() {
		return wifi1_transmit_airTime;
	}

	public String getReportClientMac() {
		return reportClientMac;
	}

	public void setReportClientMac(String reportClientMac) {
		this.reportClientMac = reportClientMac;
	}

	public String getReportClientSession() {
		return reportClientSession;
	}

	public void setReportClientSession(String reportClientSession) {
		this.reportClientSession = reportClientSession;
	}

	public String getReportClientSessionStart() {
		if (reportClientSession.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
			return "";
		}
		String[] strSessionTime = reportClientSession.split("\\|");
		return strSessionTime[0];
	}

	public String getReportClientSessionEnd() {
		if (reportClientSession.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
			return "";
		}
		String[] strSessionTime = reportClientSession.split("\\|");
		return strSessionTime[1];
	}

	public Set<String> getLstReportClientMac() {
		if (lstReportClientMac == null) {
			lstReportClientMac = new HashSet<String>();
			lstReportClientMac.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstReportClientMac;
	}

	public String getViewSsid() {
		return "viewSsid";
	}

	public String getSelectHiveAPName() {
		return selectHiveAPName;
	}

	public void setSelectHiveAPName(String selectHiveAPName) {
		this.selectHiveAPName = selectHiveAPName;
	}

	public List<CheckItem> getRtm_trans_wifi0_totalData() {
		return rtm_trans_wifi0_totalData;
	}

	public List<CheckItem> getRtm_trans_wifi0_beData() {
		return rtm_trans_wifi0_beData;
	}

	public List<CheckItem> getRtm_trans_wifi0_bgData() {
		return rtm_trans_wifi0_bgData;
	}

	public List<CheckItem> getRtm_trans_wifi0_viData() {
		return rtm_trans_wifi0_viData;
	}

	public List<CheckItem> getRtm_trans_wifi0_voData() {
		return rtm_trans_wifi0_voData;
	}

	public List<CheckItem> getRtm_trans_wifi0_unicastData() {
		return rtm_trans_wifi0_unicastData;
	}

	public List<CheckItem> getRtm_trans_wifi0_multicastData() {
		return rtm_trans_wifi0_multicastData;
	}

	public List<CheckItem> getRtm_trans_wifi0_broadcastData() {
		return rtm_trans_wifi0_broadcastData;
	}

	public List<CheckItem> getRtm_trans_wifi0_nonBeaconMgtData() {
		return rtm_trans_wifi0_nonBeaconMgtData;
	}

	public List<CheckItem> getRtm_trans_wifi0_beaconData() {
		return rtm_trans_wifi0_beaconData;
	}

	public List<CheckItem> getRtm_trans_wifi1_totalData() {
		return rtm_trans_wifi1_totalData;
	}

	public List<CheckItem> getRtm_trans_wifi1_beData() {
		return rtm_trans_wifi1_beData;
	}

	public List<CheckItem> getRtm_trans_wifi1_bgData() {
		return rtm_trans_wifi1_bgData;
	}

	public List<CheckItem> getRtm_trans_wifi1_viData() {
		return rtm_trans_wifi1_viData;
	}

	public List<CheckItem> getRtm_trans_wifi1_voData() {
		return rtm_trans_wifi1_voData;
	}

	public List<CheckItem> getRtm_trans_wifi1_unicastData() {
		return rtm_trans_wifi1_unicastData;
	}

	public List<CheckItem> getRtm_trans_wifi1_multicastData() {
		return rtm_trans_wifi1_multicastData;
	}

	public List<CheckItem> getRtm_trans_wifi1_broadcastData() {
		return rtm_trans_wifi1_broadcastData;
	}

	public List<CheckItem> getRtm_trans_wifi1_nonBeaconMgtData() {
		return rtm_trans_wifi1_nonBeaconMgtData;
	}

	public List<CheckItem> getRtm_trans_wifi1_beaconData() {
		return rtm_trans_wifi1_beaconData;
	}

	public List<CheckItem> getRtm_rec_wifi0_totalData() {
		return rtm_rec_wifi0_totalData;
	}

	public List<CheckItem> getRtm_rec_wifi0_unicastData() {
		return rtm_rec_wifi0_unicastData;
	}

	public List<CheckItem> getRtm_rec_wifi0_multicastData() {
		return rtm_rec_wifi0_multicastData;
	}

	public List<CheckItem> getRtm_rec_wifi0_broadcastData() {
		return rtm_rec_wifi0_broadcastData;
	}

	public List<CheckItem> getRtm_rec_wifi0_mgtData() {
		return rtm_rec_wifi0_mgtData;
	}

	public List<CheckItem> getRtm_rec_wifi1_totalData() {
		return rtm_rec_wifi1_totalData;
	}

	public List<CheckItem> getRtm_rec_wifi1_unicastData() {
		return rtm_rec_wifi1_unicastData;
	}

	public List<CheckItem> getRtm_rec_wifi1_multicastData() {
		return rtm_rec_wifi1_multicastData;
	}

	public List<CheckItem> getRtm_rec_wifi1_broadcastData() {
		return rtm_rec_wifi1_broadcastData;
	}

	public List<CheckItem> getRtm_rec_wifi1_mgtData() {
		return rtm_rec_wifi1_mgtData;
	}

	public List<CheckItem> getRcpn_wifi0_channel() {
		return rcpn_wifi0_channel;
	}

	public List<CheckItem> getRcpn_wifi1_channel() {
		return rcpn_wifi1_channel;
	}

	public List<CheckItem> getRcpn_wifi0_power() {
		return rcpn_wifi0_power;
	}

	public List<CheckItem> getRcpn_wifi1_power() {
		return rcpn_wifi1_power;
	}

	public List<CheckItem> getRcpn_wifi0_noise() {
		return rcpn_wifi0_noise;
	}

	public List<CheckItem> getRcpn_wifi1_noise() {
		return rcpn_wifi1_noise;
	}

	public List<CheckItem> getRts_trans_wifi0_totalRetries() {
		return rts_trans_wifi0_totalRetries;
	}

	public List<CheckItem> getRts_trans_wifi1_totalRetries() {
		return rts_trans_wifi1_totalRetries;
	}

	public List<CheckItem> getRts_trans_wifi0_totalFramesDropped() {
		return rts_trans_wifi0_totalFramesDropped;
	}

	public List<CheckItem> getRts_trans_wifi1_totalFramesDropped() {
		return rts_trans_wifi1_totalFramesDropped;
	}

	public List<CheckItem> getRts_trans_wifi0_totalFrameErrors() {
		return rts_trans_wifi0_totalFrameErrors;
	}

	public List<CheckItem> getRts_trans_wifi1_totalFrameErrors() {
		return rts_trans_wifi1_totalFrameErrors;
	}

	public List<CheckItem> getRts_trans_wifi0_feForExcessiveHWRetries() {
		return rts_trans_wifi0_feForExcessiveHWRetries;
	}

	public List<CheckItem> getRts_trans_wifi1_feForExcessiveHWRetries() {
		return rts_trans_wifi1_feForExcessiveHWRetries;
	}

	public List<CheckItem> getRts_trans_wifi0_rtsFailures() {
		return rts_trans_wifi0_rtsFailures;
	}

	public List<CheckItem> getRts_trans_wifi1_rtsFailures() {
		return rts_trans_wifi1_rtsFailures;
	}

	public List<CheckItem> getRts_rec_wifi0_totalFrameDropped() {
		return rts_rec_wifi0_totalFrameDropped;
	}

	public List<CheckItem> getRts_rec_wifi1_totalFrameDropped() {
		return rts_rec_wifi1_totalFrameDropped;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getReportSsidName() {
		if (reportSsidName == null || reportSsidName.equals("")) {
			return MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return reportSsidName;
	}

	public List<String> getLstReportSsidName() {
		if (lstReportSsidName == null) {
			lstReportSsidName = new ArrayList<String>();
			lstReportSsidName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstReportSsidName;
	}

	public void setReportAPName(String reportAPName) {
		this.reportAPName = reportAPName;
	}

	public void setReportSsidName(String reportSsidName) {
		this.reportSsidName = reportSsidName;
	}

	public List<CheckItem> getStm_trans_totalData() {
		return stm_trans_totalData;
	}

	public List<CheckItem> getStm_trans_beData() {
		return stm_trans_beData;
	}

	public List<CheckItem> getStm_trans_bgData() {
		return stm_trans_bgData;
	}

	public List<CheckItem> getStm_trans_viData() {
		return stm_trans_viData;
	}

	public List<CheckItem> getStm_trans_voData() {
		return stm_trans_voData;
	}

	public List<CheckItem> getStm_trans_unicastData() {
		return stm_trans_unicastData;
	}

	public List<CheckItem> getStm_trans_multicastData() {
		return stm_trans_multicastData;
	}

	public List<CheckItem> getStm_trans_broadcastData() {
		return stm_trans_broadcastData;
	}

	public List<CheckItem> getStm_rec_totalData() {
		return stm_rec_totalData;
	}

	public List<CheckItem> getStm_rec_unicastData() {
		return stm_rec_unicastData;
	}

	public List<CheckItem> getStm_rec_multicastData() {
		return stm_rec_multicastData;
	}

	public List<CheckItem> getStm_rec_broadcastData() {
		return stm_rec_broadcastData;
	}

	public List<CheckItem> getSts_trans_totalFramesDropped() {
		return sts_trans_totalFramesDropped;
	}

	public List<CheckItem> getSts_trans_totalFrameErrors() {
		return sts_trans_totalFrameErrors;
	}

	public List<CheckItem> getSts_rec_totalFramesDropped() {
		return sts_rec_totalFramesDropped;
	}

	public List<CheckItem> getSts_rec_totalFrameErrors() {
		return sts_rec_totalFrameErrors;
	}

	public List<List<AhClientCountForAP>> getFiveMaxClientCount() {
		return fiveMaxClientCount;
	}

	public List<CheckItem> getRogueAPs() {
		return rogueAPs;
	}

	public List<CheckItem> getRogueClients() {
		return rogueClients;
	}

	public String getReportNeighborAP() {
		if (reportNeighborAP == null || reportNeighborAP.equals("")) {
			return MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return reportNeighborAP;
	}

	public List<String> getLstReportNeighborAP() {
		if (lstReportNeighborAP == null) {
			lstReportNeighborAP = new ArrayList<String>();
			lstReportNeighborAP.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstReportNeighborAP;
	}

	public List<CheckItem> getMesh_trans_totalData() {
		return mesh_trans_totalData;
	}

	public List<CheckItem> getMesh_trans_beData() {
		return mesh_trans_beData;
	}

	public List<CheckItem> getMesh_trans_bgData() {
		return mesh_trans_bgData;
	}

	public List<CheckItem> getMesh_trans_viData() {
		return mesh_trans_viData;
	}

	public List<CheckItem> getMesh_trans_voData() {
		return mesh_trans_voData;
	}

	public List<CheckItem> getMesh_trans_mgtData() {
		return mesh_trans_mgtData;
	}

	public List<CheckItem> getMesh_trans_unicastData() {
		return mesh_trans_unicastData;
	}

	// public List<CheckItem> getMesh_trans_multicastData() {
	// return mesh_trans_multicastData;
	// }
	//
	// public List<CheckItem> getMesh_trans_broadcastData() {
	// return mesh_trans_broadcastData;
	// }

	public List<CheckItem> getMesh_rec_totalData() {
		return mesh_rec_totalData;
	}

	public List<CheckItem> getMesh_rec_mgtData() {
		return mesh_rec_mgtData;
	}

	public List<CheckItem> getMesh_rec_unicastData() {
		return mesh_rec_unicastData;
	}

	public List<CheckItem> getMesh_rec_multicastData() {
		return mesh_rec_multicastData;
	}

	public List<CheckItem> getMesh_rec_broadcastData() {
		return mesh_rec_broadcastData;
	}

	public List<CheckItem> getMesh_rssiData() {
		return mesh_rssiData;
	}

	public void setReportNeighborAP(String reportNeighborAP) {
		this.reportNeighborAP = reportNeighborAP;
	}

	public List<CheckItem> getUniqueClients() {
		return uniqueClients;
	}

	public List<AhClientCountForAP> getRadioClientCount() {
		return radioClientCount;
	}

//	public String getReportClientIpAddress() {
//		return reportClientIpAddress;
//	}
//
//	public String getReportClientHostName() {
//		return reportClientHostName;
//	}
//
//	public String getReportClientUserName() {
//		return reportClientUserName;
//	}

	public String getClientSSID() {
		return clientSSID;
	}

	public String getClientVLAN() {
		return clientVLAN;
	}

	public String getClientUserProfile() {
		return clientUserProfile;
	}

	public String getClientChannel() {
		return clientChannel;
	}

	public String getClientAuthMethod() {
		return clientAuthMethod;
	}

	public String getClientEncryptionMethod() {
		return clientEncryptionMethod;
	}

	public String getClientPhysicalMode() {
		return clientPhysicalMode;
	}

	public String getClientCWPUsed() {
		return clientCWPUsed;
	}

	public String getClientLinkUpTime() {
		return clientLinkUpTime;
	}

	public List<CheckItem> getClient_trans_totalData() {
		if (client_trans_totalData.size() == 0) {
			client_trans_totalData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_totalData;
	}

	public List<CheckItem> getClient_trans_beData() {
		if (client_trans_beData.size() == 0) {
			client_trans_beData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_beData;
	}

	public List<CheckItem> getClient_trans_bgData() {
		if (client_trans_bgData.size() == 0) {
			client_trans_bgData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_bgData;
	}

	public List<CheckItem> getClient_trans_viData() {
		if (client_trans_viData.size() == 0) {
			client_trans_viData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_viData;
	}

	public List<CheckItem> getClient_trans_voData() {
		if (client_trans_voData.size() == 0) {
			client_trans_voData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_voData;
	}

	public List<CheckItem> getClient_trans_mgtData() {
		if (client_trans_mgtData.size() == 0) {
			client_trans_mgtData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_mgtData;
	}

	public List<CheckItem> getClient_trans_unicastData() {
		if (client_trans_unicastData.size() == 0) {
			client_trans_unicastData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_unicastData;
	}

	// public List<CheckItem> getClient_trans_multicastData() {
	// return client_trans_multicastData;
	// }
	//
	// public List<CheckItem> getClient_trans_broadcastData() {
	// return client_trans_broadcastData;
	// }

	public List<CheckItem> getClient_trans_dataOctets() {
		if (client_trans_dataOctets.size() == 0) {
			client_trans_dataOctets.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_dataOctets;
	}

	public List<CheckItem> getClient_trans_lastrate() {
		if (client_trans_lastrate.size() == 0) {
			client_trans_lastrate.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_trans_lastrate;
	}

	public List<CheckItem> getClient_rec_totalData() {
		if (client_rec_totalData.size() == 0) {
			client_rec_totalData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_totalData;
	}

	public List<CheckItem> getClient_rec_mgtData() {
		if (client_rec_mgtData.size() == 0) {
			client_rec_mgtData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_mgtData;
	}

	public List<CheckItem> getClient_rec_unicastData() {
		if (client_rec_unicastData.size() == 0) {
			client_rec_unicastData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_unicastData;
	}

	public List<CheckItem> getClient_rec_multicastData() {
		if (client_rec_multicastData.size() == 0) {
			client_rec_multicastData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_multicastData;
	}

	public List<CheckItem> getClient_rec_broadcastData() {
		if (client_rec_broadcastData.size() == 0) {
			client_rec_broadcastData.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_broadcastData;
	}

	public List<CheckItem> getClient_rec_micfailures() {
		if (client_rec_micfailures.size() == 0) {
			client_rec_micfailures.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_micfailures;
	}

	public List<CheckItem> getClient_rec_dataOctets() {
		if (client_rec_dataOctets.size() == 0) {
			client_rec_dataOctets.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_dataOctets;
	}

	public List<CheckItem> getClient_rec_lastrate() {
		if (client_rec_lastrate.size() == 0) {
			client_rec_lastrate.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rec_lastrate;
	}

	public List<CheckItem> getClient_rssi() {
		if (client_rssi.size() == 0) {
			client_rssi.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_rssi;
	}
	
	public List<CheckItem> getClient_signal_to_noise() {
		if (client_signal_to_noise.size() == 0) {
			client_signal_to_noise.add(new CheckItem((long) 0, new Date().toString()));
		}
		return client_signal_to_noise;
	}

	public List<HiveAp> getLstInventory() {
		return lstInventory;
	}

	public List<TextItem> getReceive_airTime() {
		return receive_airTime;
	}

	public List<TextItem> getTransmit_airTime() {
		return transmit_airTime;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AhReport) {
			dataSource = bo;
			if (getDataSource().getOwner() != null) {
				getDataSource().getOwner().getId();
				getDataSource().getOwner().getDomainName();
			}
		}
		if (bo instanceof HiveAp) {
			HiveAp hiveap = (HiveAp) bo;
			if (hiveap.getConfigTemplate() != null) {
				hiveap.getConfigTemplate().getId();
				hiveap.getConfigTemplate().getHiveProfile().getId();
				hiveap.getConfigTemplate().getSsidInterfaces().values();
				hiveap.getConfigTemplate().getDeviceServiceFilter().getId();
				hiveap.getConfigTemplate().getEth0ServiceFilter().getId();
				hiveap.getConfigTemplate().getEth1ServiceFilter().getId();
				hiveap.getConfigTemplate().getRed0ServiceFilter().getId();
				hiveap.getConfigTemplate().getAgg0ServiceFilter().getId();
				hiveap.getConfigTemplate().getEth0BackServiceFilter().getId();
				hiveap.getConfigTemplate().getEth1BackServiceFilter().getId();
				hiveap.getConfigTemplate().getRed0BackServiceFilter().getId();
				hiveap.getConfigTemplate().getAgg0BackServiceFilter().getId();
				
				
				Collection<PortGroupProfile> ports = hiveap.getConfigTemplate().getPortProfiles();
				if(ports != null) {
					for(PortGroupProfile pgProfile : ports){
						if(pgProfile.getBasicProfiles() == null){
							continue;
						}
						for (PortBasicProfile base : pgProfile.getBasicProfiles()){
							if(base.getAccessProfile() != null) {
								if (base.getAccessProfile().getServiceFilter()!=null) {
									base.getAccessProfile().getServiceFilter().getId();
								}	
							}
						}
					}
				}
			}
		}
		return null;
	}

	public String getClientApMac() {
		return clientApMac;
	}

	public String getClientApName() {
		return clientApName;
	}

	// struts download support
	private final String mailFileName = "currentReportData.csv";
	private final String mailFileNamePdf = "currentReportData.pdf";
	private static String clientExportFileName = "currentReportData.csv";
	
	public String getInputPath() {
		if (getDataSource().getReportType().equals("compliance")){
			return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
			+ getDomain().getDomainName() + File.separator + mailFileNamePdf;
		} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)){
			return AhPerformanceScheduleModule.fileDirPath + File.separator
			+ getDomain().getDomainName() + File.separator + clientExportFileName;
		}
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}

	public String getLocalFileName() {
		if (getDataSource().getReportType().equals("compliance")){
			return mailFileNamePdf;
		}else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)){
			return clientExportFileName;
		}
		return mailFileName;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}

	public synchronized boolean generalCurrentPdfFile() {
		Document document = new Document(PageSize.A4.rotate(),50,50,72,72);
		try {
			String currentDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
			+ getDomain().getDomainName();
			File tmpFileDir = new File(currentDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
					AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileNamePdf));
			
			writer.setPageEvent(new HeaderFooterPage());
			
			document.open();
			Font fonts = new Font(Font.COURIER, Font.DEFAULTSIZE, Font.BOLD);
			Font cellfonts = new Font(Font.COURIER, 11, Font.BOLD);
			Paragraph graph = new Paragraph();
			graph.setFont(fonts);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add(getText("report.reportList.compliance.result"));
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			document.add(graph);
			
//			graph = new Paragraph();
//			graph.setAlignment(Element.ALIGN_LEFT);
//			graph.add(getText("report.reportList.compliance.hmpassStrength"));
//			graph.add(getHmpassStrength());
//			graph.setSpacingAfter(15f);
//			document.add(graph);
			
			PdfPTable table = new PdfPTable(2);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.setWidthPercentage(80);
			PdfPCell cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.securityRating"),fonts));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.hiveApCount"),fonts));
			table.addCell(cell);
			
			int weakCount=0;
			int acceptableCount=0 ;
			int strongCount=0;
			for(ComplianceResult result: lstCompliance){
				switch (result.getSummarySecurity()) {
				case CompliancePolicy.COMPLIANCE_POLICY_POOR:weakCount++;break;
				case CompliancePolicy.COMPLIANCE_POLICY_GOOD:acceptableCount++;break;
				case CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT:strongCount++;break;
				}
			}
			table.addCell(getText("report.reportList.compliance.weak"));
			table.addCell(String.valueOf(weakCount));
			table.addCell(getText("report.reportList.compliance.moderate"));
			table.addCell(String.valueOf(acceptableCount));
			table.addCell(getText("report.reportList.compliance.strong"));
			table.addCell(String.valueOf(strongCount));
			document.add(table);

			for(ComplianceResult result: lstCompliance){
				com.lowagie.text.List hiveAps = new com.lowagie.text.List(false, 10);
				hiveAps.setListSymbol(new Chunk("\u2022", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));

				ListItem listItem = new ListItem(NmsUtil.getOEMCustomer().getAccessPonitName() + " \"" + result.getHiveApName() + "\" Detail Security",fonts);
				hiveAps.add(listItem);
				
				com.lowagie.text.List sublist = new com.lowagie.text.List(false, true, 10);
				sublist.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 10)));
				
				sublist.add(getText("report.reportList.compliance.hivePass") + ": "+ result.getHivePassString());
				sublist.add(getText("report.reportList.compliance.hiveApPass") + ": " + result.getHiveApPassString());
				sublist.add(getText("report.reportList.compliance.capwapPass") + ": " + result.getCapwapPassString());
				sublist.add(getText("report.reportList.compliance.ssidSecurity") + ": ");
				
				hiveAps.add(sublist);
				graph = new Paragraph();
				graph.setAlignment(Element.ALIGN_LEFT);
				graph.setSpacingAfter(10f);
				graph.setSpacingBefore(10f);
				graph.add(hiveAps);
				document.add(graph);
				
				float[] widths = {0.2f, 0.2f, 0.1f, 0.075f, 0.075f,0.075f,0.075f,0.2f};
				table = new PdfPTable(widths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.ssidName"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.accessSecurity"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.securityRating"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.ssh"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.telnet"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.ping"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.snmp"),cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(getText("report.reportList.compliance.pskPass"),cellfonts));
				table.addCell(cell);
				
				for(ComplianceSsidListInfo ssidlistInfo:result.getSsidList()){
					table.addCell(ssidlistInfo.getSsidName());
					table.addCell(ssidlistInfo.getSsidMethodString());
					table.addCell(ssidlistInfo.getRatingString());
					table.addCell(ssidlistInfo.getBlnSshString());
					table.addCell(ssidlistInfo.getBlnTelnetString());
					table.addCell(ssidlistInfo.getBlnPingString());
					table.addCell(ssidlistInfo.getBlnSnmpString());
					table.addCell(ssidlistInfo.getSsidPassString());
				}
				document.add(table);
			}
        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
            return false;
        }
        catch(IOException ioe) {
            System.err.println(ioe.getMessage());
            return false;
        }
        document.close();
        return true;
	}
	
	
	public synchronized boolean generalCurrentNonHiveAPCsvFile(){
		try {
			Calendar reportDateTime = getReportDateTime();
			FilterParams filterSLAHistory = new FilterParams(
						"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) and lower(apName) like :s4",
						new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
								,AhBandWidthSentinelHistory.STATUS_ALERT, getDataSource().getApNameForSQLTwo()});
			List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					new SortParams("apName"), filterSLAHistory, getDomain().getId());
	
			
			FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2 and lower(apName) like :s3",
					new Object[] { reportDateTime.getTimeInMillis(),0,getDataSource().getApNameForSQLTwo() });
			List<AhInterfaceStats> interfaceStatsHistory =QueryUtil.executeQuery(AhInterfaceStats.class, new SortParams("apName"),
					filterInterfaceHistory,getDomain().getId());
			
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			StringBuffer strOutput;
			File tmpFile = new File(currentFileDir + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);
			strOutput = new StringBuffer();
			
			strOutput.append(getText("report.reportList.deviceName")).append(",");
			strOutput.append("Client MAC Address,");
			strOutput.append("SLA Events,");
			strOutput.append("Guaranteed Throughput (Kbps),");
			strOutput.append("Actual Throughput (Kbps),");
			strOutput.append("Channel CU (%),");
			strOutput.append("Interference CU (%),");
			strOutput.append("Tx CU (%),");
			strOutput.append("Rx CU (%),");
			strOutput.append("Report Time");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			strOutput = new StringBuffer();
			int printCount=0;
			for(AhBandWidthSentinelHistory oneItem:slaHistory){
				strOutput.append(oneItem.getApName()).append(",");
				strOutput.append(oneItem.getClientMac()).append(",");
				strOutput.append(oneItem.getBandWidthSentinelStatusString()).append(",");
				strOutput.append(oneItem.getGuaranteedBandWidth()).append(",");
				strOutput.append(oneItem.getActualBandWidth()).append(",");
				strOutput.append(oneItem.getChannelUltil()>=0? oneItem.getChannelUltil(): "Unknown").append(",");
				strOutput.append(oneItem.getInterferenceUltil()>=0? oneItem.getInterferenceUltil(): "Unknown").append(",");
				strOutput.append(oneItem.getTxUltil()>=0? oneItem.getTxUltil(): "Unknown").append(",");
				strOutput.append(oneItem.getRxUltil()>=0? oneItem.getRxUltil(): "Unknown").append(",");
				strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp().getTime(),tz));
				strOutput.append("\n");
	
				printCount++;
				if (printCount % 1000 == 0 && printCount != 0) {
					out.write(strOutput.toString());
					strOutput = new StringBuffer();
				}
			}
			strOutput.append("\n");
			strOutput.append("\n");
			strOutput.append("\n");
			out.write(strOutput.toString());

			strOutput = new StringBuffer();
			strOutput.append(getText("report.reportList.deviceName")).append(",");
			strOutput.append("Interface Name,");
			strOutput.append("CRC Error Rate (%),");
			strOutput.append("Tx Drop Rate (%),");
			strOutput.append("Rx Drop Rate (%),");
			strOutput.append("Tx Retry Rate (%),");
			strOutput.append("Tx Airtime Usage (%),");
			strOutput.append("Rx Airtime Usage (%),");
			strOutput.append("Channel CU (%),");
			strOutput.append("Interference CU (%),");
			strOutput.append("Tx CU (%),");
			strOutput.append("Rx CU (%),");
			strOutput.append("Collection Period (seconds),");
			strOutput.append("Report Time");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			strOutput = new StringBuffer();
			printCount=0;
			for(AhInterfaceStats oneItem:interfaceStatsHistory){
				strOutput.append(oneItem.getApName()).append(",");
				strOutput.append(oneItem.getIfName()).append(",");
				strOutput.append(oneItem.getCrcErrorRate()).append(",");
				strOutput.append(getPencentageValue(oneItem.getTxDrops(),oneItem.getUniTxFrameCount() + oneItem.getBcastTxFrameCount())).append(",");
				strOutput.append(getPencentageValue(oneItem.getRxDrops(),oneItem.getUniRxFrameCount()+ oneItem.getBcastRxFrameCount())).append(",");
				strOutput.append(oneItem.getTxRetryRate()).append(",");
				strOutput.append(oneItem.getTxAirTime()).append(",");
				strOutput.append(oneItem.getRxAirTime()).append(",");
				strOutput.append(oneItem.getTotalChannelUtilization()).append(",");
				strOutput.append(oneItem.getInterferenceUtilization()).append(",");
				strOutput.append(oneItem.getTxUtilization()).append(",");
				strOutput.append(oneItem.getRxUtilization()).append(",");
				strOutput.append(oneItem.getCollectPeriod()).append(",");
				strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
				strOutput.append("\n");
	
				printCount++;
				if (printCount % 1000 == 0 && printCount != 0) {
					out.write(strOutput.toString());
					strOutput = new StringBuffer();
				}
			}
			out.write(strOutput.toString());
			
			out.flush();
			out.close();
			return true;
		} catch(Exception ex) {
			DebugUtil.performanceDebugWarn("exportCurrentNonComplianceData in report:", ex);
			return false;
		}
	
	}
	
	public String getPencentageValue(long value1, long value2){
		if (value2==0) {
			return "0";
		} else if (value1>=value2){
			return "100";
		} else {
			return String.valueOf(value1 * 100 / value2);
		}
	}
	
	public synchronized boolean generalCurrentNonClientCsvFile(){

		try {
			Calendar reportDateTime = getReportDateTime();
			FilterParams filterSLAHistory = new FilterParams(
						"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) " +
						"and lower(apName) like :s4 and lower(clientMac) like :s5",
						new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
								,AhBandWidthSentinelHistory.STATUS_ALERT, getDataSource().getApNameForSQLTwo(),
								"%" + getDataSource().getAuthMacForSQL().toLowerCase()+ "%"});
			List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					new SortParams("clientMac"), filterSLAHistory, getDomain().getId());
			
			FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and (alarmFlag>:s2 or overallClientHealthScore<:s3) and " +
					"lower(apName) like :s4 and lower(clientMac) like :s5",
					new Object[] { reportDateTime.getTimeInMillis(),0,AhClientSession.CLIENT_SCORE_RED,getDataSource().getApNameForSQLTwo(),
									"%" + getDataSource().getAuthMacForSQL().toLowerCase()+ "%"});
			List<AhClientStats> interfaceStatsHistory =QueryUtil.executeQuery(AhClientStats.class, new SortParams("clientMac"),
					filterInterfaceHistory,getDomain().getId());
			
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			StringBuffer strOutput;
			File tmpFile = new File(currentFileDir + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);
			strOutput = new StringBuffer();
			
			strOutput.append(getText("report.reportList.deviceName")).append(",");
			strOutput.append("Client MAC Address,");
			strOutput.append("SLA Events,");
			strOutput.append("Guaranteed Throughput (Kbps),");
			strOutput.append("Actual Throughput (Kbps),");
			strOutput.append("Report Time");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			strOutput = new StringBuffer();
			int printCount=0;
			for(AhBandWidthSentinelHistory oneItem:slaHistory){
				strOutput.append(oneItem.getApName()).append(",");
				strOutput.append(oneItem.getClientMac()).append(",");
				strOutput.append(oneItem.getBandWidthSentinelStatusString()).append(",");
				strOutput.append(oneItem.getGuaranteedBandWidth()).append(",");
				strOutput.append(oneItem.getActualBandWidth()).append(",");
				strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp().getTime(),tz));
				strOutput.append("\n");
	
				printCount++;
				if (printCount % 1000 == 0 && printCount != 0) {
					out.write(strOutput.toString());
					strOutput = new StringBuffer();
				}
			}
			strOutput.append("\n");
			strOutput.append("\n");
			strOutput.append("\n");
			out.write(strOutput.toString());

			strOutput = new StringBuffer();
			strOutput.append(getText("report.reportList.deviceName")).append(",");
			strOutput.append("Client MAC Address,");
			strOutput.append("SSID Name,");
			strOutput.append("Tx Drop Rate (%),");
			strOutput.append("Rx Drop Rate (%),");
			strOutput.append("Client Health,");
			strOutput.append("Client Radio Score,");
			strOutput.append("Client IP Network Score,");
			strOutput.append("Client Application Score,");
			strOutput.append("Tx Airtime Usage (%),");
			strOutput.append("Rx Airtime Usage (%),");
			strOutput.append("Collection Period (seconds),");
			strOutput.append("Report Time");
			strOutput.append("\n");
			out.write(strOutput.toString());

			strOutput = new StringBuffer();
			printCount=0;
			for(AhClientStats oneItem:interfaceStatsHistory){				
				strOutput.append(oneItem.getApName()).append(",");
				strOutput.append(oneItem.getClientMac()).append(",");
				strOutput.append(oneItem.getSsidName()).append(",");
				strOutput.append(getPencentageValue(oneItem.getTxFrameDropped(),oneItem.getTxFrameCount())).append(",");
				strOutput.append(getPencentageValue(oneItem.getRxFrameDropped(),oneItem.getRxFrameCount())).append(",");
				strOutput.append(oneItem.getOverallClientHealthScore()).append(",");
				strOutput.append(oneItem.getSlaConnectScore()).append(",");
				strOutput.append(oneItem.getIpNetworkConnectivityScore()).append(",");
				strOutput.append(oneItem.getApplicationHealthScore()).append(",");
				strOutput.append(oneItem.getTxAirTime()).append(",");
				strOutput.append(oneItem.getRxAirTime()).append(",");
				strOutput.append(oneItem.getCollectPeriod()).append(",");
				strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
				strOutput.append("\n");
	
				printCount++;
				if (printCount % 1000 == 0 && printCount != 0) {
					out.write(strOutput.toString());
					strOutput = new StringBuffer();
				}
			}
			out.write(strOutput.toString());
			
			out.flush();
			out.close();
			return true;
		} catch(Exception ex) {
			DebugUtil.performanceDebugWarn("exportCurrentNonComplianceData in report:", ex);
			return false;
		}

	}
	
	public synchronized boolean generalCurrentCvsFile() {
		try {
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			StringBuffer strOutput;
			File tmpFile = new File(currentFileDir + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);

			if (getDataSource().getReportType().equals(L2_FEATURE_RADIOTRAFFICMETRICS)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Interface Name,");
				strOutput.append("Transmitted Total Data Frames,");
				strOutput.append("Transmitted WMM Best Effort Data Frames,");
				strOutput.append("Transmitted WMM Background Data Frames,");
				strOutput.append("Transmitted WMM Video Data Frames,");
				strOutput.append("Transmitted WMM Voice Data Frames,");
				strOutput.append("Transmitted Unicast Data Frames,");
				strOutput.append("Transmitted Multicast Data Frames,");
				strOutput.append("Transmitted Broadcast Data Frames,");
				strOutput.append("Transmitted Mgt Frames (not Beacons),");
				strOutput.append("Transmitted Beacon Frames,");
				strOutput.append("Received Total Data Frames,");
				strOutput.append("Received Unicast Data Frames,");
				strOutput.append("Received Multicast Data Frames,");
				strOutput.append("Received Broadcast Data Frames,");
				strOutput.append("Received Mgt Frames");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < rtm_trans_wifi0_totalData.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(rtm_trans_wifi0_totalData.get(cnt).getValue()).append(",");
					strOutput.append("wifi0,");
					strOutput.append(rtm_trans_wifi0_totalData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_beData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_bgData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_viData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_voData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_unicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_multicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_nonBeaconMgtData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi0_beaconData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi0_totalData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi0_unicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi0_multicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi0_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi0_mgtData.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < rtm_trans_wifi1_totalData.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(rtm_trans_wifi1_totalData.get(cnt).getValue()).append(",");
					strOutput.append("wifi1,");
					strOutput.append(rtm_trans_wifi1_totalData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_beData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_bgData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_viData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_voData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_unicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_multicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_nonBeaconMgtData.get(cnt).getId()).append(",");
					strOutput.append(rtm_trans_wifi1_beaconData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi1_totalData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi1_unicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi1_multicastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi1_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(rtm_rec_wifi1_mgtData.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_CHANNELPOWERNOISE)) {
				if (getDataSource().getNewOldFlg()==AhReport.REPORT_NEWOLDTYEP_OLD) {
					strOutput = new StringBuffer();
					strOutput.append(getText("report.reportList.deviceName")).append(",");
					strOutput.append("StatTime,");
					strOutput.append("Interface Name,");
					strOutput.append("Radio Channel,");
					strOutput.append("Radio Noise Floor,");
					strOutput.append("Radio TxPower");
					strOutput.append("\n");
					out.write(strOutput.toString());
	
					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < rcpn_wifi0_channel.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(rcpn_wifi0_channel.get(cnt).getValue()).append(",");
						strOutput.append("wifi0" + ",");
						strOutput.append(rcpn_wifi0_channel.get(cnt).getId()).append(",");
						strOutput.append(rcpn_wifi0_noise.get(cnt).getId()).append(",");
						strOutput.append(rcpn_wifi0_power.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
	
					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < rcpn_wifi1_channel.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(rcpn_wifi1_channel.get(cnt).getValue()).append(",");
						strOutput.append("wifi1" + ",");
						strOutput.append(rcpn_wifi1_channel.get(cnt).getId()).append(",");
						strOutput.append(rcpn_wifi1_noise.get(cnt).getId()).append(",");
						strOutput.append(rcpn_wifi1_power.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				} else {
					strOutput = new StringBuffer();
					strOutput.append(getText("report.reportList.deviceName")).append(",");
					strOutput.append("StatTime,");
					strOutput.append("Interface Name,");
					strOutput.append("Tx Unicast Frames,");
					strOutput.append("Rx Unicast Frames,");
					strOutput.append("Tx Broadcast Frames,");
					strOutput.append("Rx Broadcast Frames,");
					strOutput.append("Tx Drop Frames,");
					strOutput.append("Rx Drop Frames,");
					strOutput.append("Tx Utilization,");
					strOutput.append("Rx Utilization,");
					strOutput.append("Tx Retry Rate,");
					strOutput.append("Rx Retry Rate,");
					strOutput.append("Tx Airtime Utilization,");
					strOutput.append("Rx Airtime Utilization,");
					strOutput.append("CRC Error Rate,");
					strOutput.append("Total Channel Utilization,");
					strOutput.append("Interference Utilization,");
					strOutput.append("Noise Floor,");
					strOutput.append("Band Steering Suppress Count,");
					strOutput.append("Load Balance Suppress Count,");
					strOutput.append("Weak SNR Suppress Count,");
					strOutput.append("Safety Net Answer Count,");
					strOutput.append("Probe Request Suppress Count,");
					strOutput.append("Auth Request Suppress Count,");
					strOutput.append("Tx Rate Distribution,");
					strOutput.append("Rx Rate Distribution,");
					strOutput.append("Tx Rate Success Distribution,");
					strOutput.append("Rx Rate Success Distribution");
					strOutput.append("\n");
					out.write(strOutput.toString());
	
					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < hiveap_wifi0_rec_unicast.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(hiveap_wifi0_rec_unicast.get(cnt).getValue()).append(",");
						strOutput.append("wifi0" + ",");
						strOutput.append(hiveap_wifi0_trans_unicast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_unicast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_trans_broadcast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_broadcast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_trans_drops.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_drops.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_trans_totalU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_totalU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_trans_retryRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_retryRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_trans_airTimeU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_rec_airTimeU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_crcErrorRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_totalChannelU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_InterferenceU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_noiseFloor.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_bandsteering.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_loadbalance.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_weaksnr.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_safetynet.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_proberequest.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi0_authrequest.get(cnt).getId()).append(",");
						
						if (hiveap_wifi0_trans_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())!=null){
							for (CheckItem items:hiveap_wifi0_trans_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())){
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
								.append(items.getId()).append("%; ");
							}				
						}
						strOutput.append(",");
						if (hiveap_wifi0_rec_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())!=null){
							for (CheckItem items:hiveap_wifi0_rec_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())){
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
								.append(items.getId()).append("%; ");
							}
						}
						strOutput.append(",");
						
						strOutput.append(hiveap_wifi0_trans_rate_succ_dis.get(cnt).getToopTip()).append(",");
						strOutput.append(hiveap_wifi0_rec_rate_succ_dis.get(cnt).getToopTip());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
	
					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < hiveap_wifi1_rec_unicast.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(hiveap_wifi1_rec_unicast.get(cnt).getValue()).append(",");
						strOutput.append("wifi1" + ",");
						strOutput.append(hiveap_wifi1_trans_unicast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_unicast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_trans_broadcast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_broadcast.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_trans_drops.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_drops.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_trans_totalU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_totalU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_trans_retryRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_retryRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_trans_airTimeU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_rec_airTimeU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_crcErrorRateU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_totalChannelU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_InterferenceU.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_noiseFloor.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_bandsteering.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_loadbalance.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_weaksnr.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_safetynet.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_proberequest.get(cnt).getId()).append(",");
						strOutput.append(hiveap_wifi1_authrequest.get(cnt).getId()).append(",");
						if (hiveap_wifi1_trans_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())!=null){
							for (CheckItem items:hiveap_wifi1_trans_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())){
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
								.append(items.getId()).append("%; ");
							}
						}
						strOutput.append(",");
						if (hiveap_wifi1_rec_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())!=null){
							for (CheckItem items:hiveap_wifi1_rec_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())){
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
								.append(items.getId()).append("%; ");
							}
						}
						strOutput.append(",");
						strOutput.append(hiveap_wifi1_trans_rate_succ_dis.get(cnt).getToopTip()).append(",");
						strOutput.append(hiveap_wifi1_rec_rate_succ_dis.get(cnt).getToopTip());
						strOutput.append("\n");
					}
					
					out.write(strOutput.toString());
				}
			} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOTROUBLESHOOTING)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Interface Name,");
				strOutput.append("Transmitted Total Retransmitted Frames,");
				strOutput.append("Transmitted Total Frames Dropped by SW,");
				strOutput.append("Transmitted Total Frames Dropped by Radio,");
				strOutput.append("Transmitted Retry Threshold Crossing Events,");
				strOutput.append("Transmitted RTS Failures,");
				strOutput.append("Received Total Frames Dropped by SW");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < rts_trans_wifi0_totalRetries.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(rts_trans_wifi0_totalRetries.get(cnt).getValue()).append(",");
					strOutput.append("wifi0,");
					strOutput.append(rts_trans_wifi0_totalRetries.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi0_totalFramesDropped.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi0_totalFrameErrors.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi0_feForExcessiveHWRetries.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi0_rtsFailures.get(cnt).getId()).append(",");
					strOutput.append(rts_rec_wifi0_totalFrameDropped.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < rts_trans_wifi1_totalRetries.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(rts_trans_wifi1_totalRetries.get(cnt).getValue()).append(",");
					strOutput.append("wifi1,");
					strOutput.append(rts_trans_wifi1_totalRetries.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi1_totalFramesDropped.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi1_totalFrameErrors.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi1_feForExcessiveHWRetries.get(cnt).getId()).append(",");
					strOutput.append(rts_trans_wifi1_rtsFailures.get(cnt).getId()).append(",");
					strOutput.append(rts_rec_wifi1_totalFrameDropped.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOINTERFERENCE)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Interface Name,");
				strOutput.append("Average TX CU,");
				strOutput.append("Average RX CU,");
				strOutput.append("Average Interference CU,");
				strOutput.append("Average Noise Floor,");
				strOutput.append("Short Term Means Average TX CU,");
				strOutput.append("Short Term Means Average RX CU,");
				strOutput.append("Short Term Means Average Interference CU,");
				strOutput.append("Short Term Means Average Noise Floor,");
				strOutput.append("Snapshot TX CU ,");
				strOutput.append("Snapshot RX CU,");
				strOutput.append("Snapshot Interference CU,");
				strOutput.append("Snapshot Noise Floor,");
				strOutput.append("CRC Error Rate");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < wifi0_averageTxCu.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(wifi0_averageTxCu.get(cnt).getValue()).append(",");
					strOutput.append("wifi0,");
					strOutput.append(wifi0_averageTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_averageRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_averageInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_averageNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi0_shortTermTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_shortTermRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_shortTermInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_shortTermNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi0_snapShotTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_snapShotRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_snapShotInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi0_snapShotNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi0_crcError.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < wifi1_averageTxCu.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(wifi1_averageTxCu.get(cnt).getValue()).append(",");
					strOutput.append("wifi1,");
					strOutput.append(wifi1_averageTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_averageRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_averageInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_averageNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi1_shortTermTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_shortTermRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_shortTermInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_shortTermNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi1_snapShotTxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_snapShotRxCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_snapShotInterferenceCu.get(cnt).getId()).append(",");
					strOutput.append(wifi1_snapShotNoiseFloor.get(cnt).getId()).append(",");
					strOutput.append(wifi1_crcError.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_RADIOAIRTIME)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Interface Name,");
				strOutput.append("Transmitted AirTime(%),");
				strOutput.append("Received AirTime(%)");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
					strOutput.append("wifi0,");
					strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
					strOutput.append(receive_airTime.get(cnt).getKey());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < wifi1_transmit_airTime.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(wifi1_transmit_airTime.get(cnt).getValue()).append(",");
					strOutput.append("wifi1,");
					strOutput.append(wifi1_transmit_airTime.get(cnt).getKey()).append(",");
					strOutput.append(wifi1_receive_airTime.get(cnt).getKey());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDAIRTIME)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("SSID Name,");
				strOutput.append("StatTime,");
				strOutput.append("Transmitted AirTime(%),");
				strOutput.append("Received AirTime(%)");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(reportSsidName).append(",");
					strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
					strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
					strOutput.append(receive_airTime.get(cnt).getKey());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTRAFFICMETRICS)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("SSID Name,");
				strOutput.append("StatTime,");
				strOutput.append("Transmitted Total Data Frames,");
				strOutput.append("Transmitted WMM Best Effort Data Frames,");
				strOutput.append("Transmitted WMM Background Data Frames,");
				strOutput.append("Transmitted WMM Video Data Frames,");
				strOutput.append("Transmitted WMM Voice Data Frames,");
				strOutput.append("Transmitted Unicast Data Frames,");
				strOutput.append("Transmitted Multicast Data Frames,");
				strOutput.append("Transmitted Broadcast Data Frames,");
				strOutput.append("Received Total Data Frames,");
				strOutput.append("Received Unicast Data Frames,");
				strOutput.append("Received Multicast Data Frames,");
				strOutput.append("Received Broadcast Data Frames");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < stm_trans_totalData.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(reportSsidName).append(",");
					strOutput.append(stm_trans_totalData.get(cnt).getValue()).append(",");
					strOutput.append(stm_trans_totalData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_beData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_bgData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_viData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_voData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_unicastData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_multicastData.get(cnt).getId()).append(",");
					strOutput.append(stm_trans_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(stm_rec_totalData.get(cnt).getId()).append(",");
					strOutput.append(stm_rec_unicastData.get(cnt).getId()).append(",");
					strOutput.append(stm_rec_multicastData.get(cnt).getId()).append(",");
					strOutput.append(stm_rec_broadcastData.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_SSIDTROUBLESHOOTING)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("SSID Name,");
				strOutput.append("StatTime,");
				strOutput.append("Transmitted Total Frames Dropped by Radio,");
				strOutput.append("Transmitted Total Frames Dropped by SW,");
				strOutput.append("Received Total Frames Dropped by Radio,");
				strOutput.append("Received Total Frames Dropped by SW");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < sts_trans_totalFramesDropped.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(reportSsidName).append(",");
					strOutput.append(sts_trans_totalFramesDropped.get(cnt).getValue()).append(",");
					strOutput.append(sts_trans_totalFrameErrors.get(cnt).getId()).append(",");
					strOutput.append(sts_trans_totalFramesDropped.get(cnt).getId()).append(",");
					strOutput.append(sts_rec_totalFrameErrors.get(cnt).getId()).append(",");
					strOutput.append(sts_rec_totalFramesDropped.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("mostClientsAPs")) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(" with the Most Clients,");
				strOutput.append("Total Clients,");
				strOutput.append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(" with the Second Most Clients,");
				strOutput.append("Total Clients,");
				strOutput.append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(" with the Third Most Clients,");
				strOutput.append("Total Clients,");
				strOutput.append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(" with the Fourth Most Clients,");
				strOutput.append("Total Clients,");
				strOutput.append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(" with the Fifth Most Clients,");
				strOutput.append("Total Clients");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (List<AhClientCountForAP> oneTimeList : fiveMaxClientCount) {
					for (int secCount = 0; secCount < oneTimeList.size(); secCount++) {
						AhClientCountForAP ahClientCountForAP = oneTimeList.get(secCount);
						if (secCount == 0) {
							strOutput.append(ahClientCountForAP.getReportTimeString()).append(",");
						}
						strOutput.append(ahClientCountForAP.getApName()).append(",");
						if (secCount == oneTimeList.size() - 1) {
							strOutput.append(ahClientCountForAP.getClientCount());
						} else {
							strOutput.append(ahClientCountForAP.getClientCount()).append(",");
						}
					}
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSESSION)) {
				strOutput = new StringBuffer();
				strOutput.append("Client MAC Address,");
				strOutput.append("Client IP Address,");
				strOutput.append("Client HostName,");
				strOutput.append("Client UserName,");
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Client SSID,");
				strOutput.append("Client VLAN,");
				strOutput.append("Client User Profile Attribute,");
				strOutput.append("Client Channel,");
				strOutput.append("Client Auth Method,");
				strOutput.append("Client Encryption Method,");
				strOutput.append("Client Physical Mode,");
				strOutput.append("Client CWP Used,");
				strOutput.append("Client Link Uptime,");
				strOutput.append("Client Associated BSSID,");
				strOutput.append("Transmitted Total Data Frames,");
				strOutput.append("Transmitted WMM Best Effort Data Frames,");
				strOutput.append("Transmitted WMM Background Data Frames,");
				strOutput.append("Transmitted WMM Video Data Frames,");
				strOutput.append("Transmitted WMM Voice Data Frames,");
				strOutput.append("Transmitted Mgt Frames,");
				strOutput.append("Transmitted Unicast Data Frames,");
				strOutput.append("Transmitted DataOctets,");
				strOutput.append("Transmitted Last Rate,");
				strOutput.append("Received Total Data Frames,");
				strOutput.append("Received Mgt Frames,");
				strOutput.append("Received Unicast Data Frames,");
				strOutput.append("Received Multicast Data Frames,");
				strOutput.append("Received Broadcast Data Frames,");
				strOutput.append("Received MIC Failures,");
				strOutput.append("Received DataOctets,");
				strOutput.append("Received Last Rate,");
				strOutput.append("RSSI");
				strOutput.append("\n");
				out.write(strOutput.toString());

				SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				l_sdf.setTimeZone(tz);
				long startTimeLong = 0;
				long endTimeLong = 0;
				if (client_trans_totalData.size() > 0) {
					startTimeLong = l_sdf.parse(getReportClientSessionStart()).getTime();
					endTimeLong = l_sdf.parse(getReportClientSessionEnd()).getTime();
				}
				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < client_trans_totalData.size(); cnt++) {
					long inTimeLong = l_sdf.parse(client_trans_totalData.get(cnt).getValue())
							.getTime();
					if (inTimeLong >= startTimeLong && inTimeLong <= endTimeLong) {
						strOutput.append(reportClientMac).append(",");
						strOutput.append(clientIp).append(",");
						strOutput.append(clientHostName).append(",");
						strOutput.append(clientUserName).append(",");
						strOutput.append(clientApName).append(",");
						strOutput.append(client_trans_totalData.get(cnt).getValue()).append(",");
						strOutput.append(clientSSID).append(",");
						strOutput.append(clientVLAN).append(",");
						strOutput.append(clientUserProfile).append(",");
						strOutput.append(clientChannel).append(",");
						strOutput.append(clientAuthMethod).append(",");
						strOutput.append(clientEncryptionMethod).append(",");
						strOutput.append(clientPhysicalMode).append(",");
						strOutput.append(clientCWPUsed).append(",");
						strOutput.append("\"").append(clientLinkUpTime).append("\",");
						strOutput.append(clientBSSID).append(",");
						strOutput.append(client_trans_totalData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_beData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_bgData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_viData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_voData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_mgtData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_unicastData.get(cnt).getId()).append(",");
						strOutput.append(client_trans_dataOctets.get(cnt).getId()).append(",");
						strOutput.append(client_trans_lastrate.get(cnt).getId()).append(",");
						strOutput.append(client_rec_totalData.get(cnt).getId()).append(",");
						strOutput.append(client_rec_mgtData.get(cnt).getId()).append(",");
						strOutput.append(client_rec_unicastData.get(cnt).getId()).append(",");
						strOutput.append(client_rec_multicastData.get(cnt).getId()).append(",");
						strOutput.append(client_rec_broadcastData.get(cnt).getId()).append(",");
						strOutput.append(client_rec_micfailures.get(cnt).getId()).append(",");
						strOutput.append(client_rec_dataOctets.get(cnt).getId()).append(",");
						strOutput.append(client_rec_lastrate.get(cnt).getId()).append(",");
						strOutput.append(client_rssi.get(cnt).getId()).append(",");
						strOutput.append(client_signal_to_noise.get(cnt).getId());
						strOutput.append("\n");
					}
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("clientCount")) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append("11a Mode Client Count,");
				strOutput.append("11b Mode Client Count,");
				strOutput.append("11g Mode Client Count,");
				strOutput.append("11na Mode Client Count,");
				strOutput.append("11ng Mode Client Count,");
				strOutput.append("11ac Mode Client Count");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (AhClientCountForAP ahClientCountForAP : radioClientCount) {
					strOutput.append(ahClientCountForAP.getReportTimeString()).append(",");
					strOutput.append(ahClientCountForAP.getAModeCount()).append(",");
					strOutput.append(ahClientCountForAP.getBModeCount()).append(",");
					strOutput.append(ahClientCountForAP.getGModeCount()).append(",");
					strOutput.append(ahClientCountForAP.getNaModeCount()).append(",");
					strOutput.append(ahClientCountForAP.getNgModeCount()).append(",");
					strOutput.append(ahClientCountForAP.getAcModeCount());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTAIRTIME)) {
				strOutput = new StringBuffer();
				strOutput.append("Client MAC Address,");
//				strOutput.append("Client IP Address,");
//				strOutput.append("Client HostName,");
//				strOutput.append("Client UserName,");
				strOutput.append("StatTime,");
				strOutput.append("Transmitted AirTime(%),");
				strOutput.append("Received AirTime(%)");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
					strOutput.append(reportClientMac).append(",");
//					strOutput.append(reportClientIpAddress == null ? "," : reportClientIpAddress
//							+ ",");
//					strOutput
//							.append(reportClientHostName == null ? "," : reportClientHostName + ",");
//					strOutput
//							.append(reportClientUserName == null ? "," : reportClientUserName + ",");
					strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
					strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
					strOutput.append(receive_airTime.get(cnt).getKey());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_UNIQUECLIENTCOUNT)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("StatTime,");
				strOutput.append("Client Count");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (CheckItem uniqueClient : uniqueClients) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(uniqueClient.getValue()).append(",");
					strOutput.append(uniqueClient.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("clientVendor")) {
				strOutput = new StringBuffer();
				strOutput.append("Start Time,");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.vendor")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.vendorCount"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sf.setTimeZone(tz);
				String reportTimeStr = sf.format(new Date());
				for (CheckItem item : lstClientVendorCount) {
					strOutput.append(reportTimeStr).append(",");
					strOutput.append("\"").append(item.getValue()).append("\",");
					strOutput.append(item.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_MAXCLIENTREPORT)) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append("Max Concurrent Client Count");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (CheckItem maxClient : maxClients) {
					strOutput.append(maxClient.getValue()).append(",");
					strOutput.append(maxClient.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("securityRogueAPs")) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append("Rogue APs Count");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (CheckItem rogueAP : rogueAPs) {
					strOutput.append(rogueAP.getValue()).append(",");
					strOutput.append(rogueAP.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("hiveApSla")) {
				strOutput = new StringBuffer();
				strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceMac")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.currentClientMac")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.status")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.time")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.configBandwidth")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.actualBandwidth"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (AhBandWidthSentinelHistory tmpClass : lstHiveApSla) {
					strOutput.append(tmpClass.getApName()).append(",");
					strOutput.append(tmpClass.getApMac()).append(",");
					strOutput.append(tmpClass.getClientMac()).append(",");
					strOutput.append(tmpClass.getBandWidthSentinelStatusString()).append(",");
					String timeString = AhDateTimeUtil.getSpecifyDateTimeReport(
							tmpClass.getTimeStamp().getTime(), tz);
					strOutput.append(timeString).append(",");
					strOutput.append(tmpClass.getGuaranteedBandWidth()).append(",");
					strOutput.append(tmpClass.getActualBandWidth());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_CLIENTSLA)) {
				strOutput = new StringBuffer();
				strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceMac")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.currentClientMac")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.status")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.time")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.configBandwidth")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.actualBandwidth"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (AhBandWidthSentinelHistory tmpClass : lstHiveApSla) {
					strOutput.append(tmpClass.getApName()).append(",");
					strOutput.append(tmpClass.getApMac()).append(",");
					strOutput.append(tmpClass.getClientMac()).append(",");
					strOutput.append(tmpClass.getBandWidthSentinelStatus()).append(",");
					String timeString = AhDateTimeUtil.getSpecifyDateTimeReport(
							tmpClass.getTimeStamp().getTime(), tz);
					strOutput.append(timeString).append(",");
					strOutput.append(tmpClass.getGuaranteedBandWidth()).append(",");
					strOutput.append(tmpClass.getActualBandWidth());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
				
			} else if (getDataSource().getReportType().equals("securityRogueClients")) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append("Rogue Clients Count");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (CheckItem rogueClient : rogueClients) {
					strOutput.append(rogueClient.getValue()).append(",");
					strOutput.append(rogueClient.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("securityDetection")) {
				return false;
			} else if (getDataSource().getReportType().equals("meshNeighbors")) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append("NeighborAP Node,");
				strOutput.append("StatTime,");
				strOutput.append("Transmitted Total Data Frames,");
				strOutput.append("Transmitted WMM Best Effort Data Frames,");
				strOutput.append("Transmitted WMM Background Data Frames,");
				strOutput.append("Transmitted WMM Video Data Frames,");
				strOutput.append("Transmitted WMM Voice Data Frames,");
				strOutput.append("Transmitted Mgt Frames,");
				strOutput.append("Transmitted Unicast Data Frames,");
				strOutput.append("Received Total Data Frames,");
				strOutput.append("Received Mgt Frames,");
				strOutput.append("Received Unicast Data Frames,");
				strOutput.append("Received Multicast Data Frames,");
				strOutput.append("Received Broadcast Data Frames,");
				strOutput.append("RSSI");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < mesh_trans_totalData.size(); cnt++) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(reportNeighborAP).append(",");
					strOutput.append(mesh_trans_totalData.get(cnt).getValue()).append(",");
					strOutput.append(mesh_trans_totalData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_beData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_bgData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_viData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_voData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_mgtData.get(cnt).getId()).append(",");
					strOutput.append(mesh_trans_unicastData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rec_totalData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rec_mgtData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rec_unicastData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rec_multicastData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rec_broadcastData.get(cnt).getId()).append(",");
					strOutput.append(mesh_rssiData.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("inventory")) {
				strOutput = new StringBuffer();
				strOutput.append("StatTime,");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.hostName")).append(",");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.ipAddress")).append(",");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.macaddress")).append(",");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.serialNumber")).append(",");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.apType")).append(",");
				strOutput.append(MgrUtil.getUserMessage("hiveAp.topology")).append(",");
				strOutput.append(MgrUtil.getUserMessage("monitor.hiveAp.connectionTime")).append(",");
				strOutput.append(MgrUtil.getUserMessage("monitor.hiveAp.model")).append(",");
				strOutput.append(MgrUtil.getUserMessage("monitor.hiveAp.sw"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sf.setTimeZone(tz);
				String reportDate = sf.format(new Date());
				for (HiveAp hiveAp : lstInventory) {
					strOutput.append(reportDate).append(",");
					strOutput.append(hiveAp.getHostName()).append(",");
					strOutput.append(hiveAp.getIpAddress()).append(",");
					strOutput.append(hiveAp.getMacAddress()).append(",");
					strOutput.append(hiveAp.getSerialNumber()).append(",");
					strOutput.append(hiveAp.getHiveApTypeString()).append(",");
					strOutput.append(hiveAp.getTopologyName()).append(",");
					strOutput.append("\"").append(hiveAp.getUpTimeString()).append("\",");
					strOutput.append(hiveAp.getProductName()).append(",");
					strOutput.append(hiveAp.getSoftVer());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_HIVEAPCONNECTION)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("report.reportList.deviceName")).append(",");
				strOutput.append(getText("report.reportList.deviceMac")).append(",");
				strOutput.append("Occurred,");
				strOutput.append("Connection Status,");
				strOutput.append("Reason");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				for (APConnectHistoryInfo connectionHistory : lstHiveApConnection) {
					strOutput.append(connectionHistory.getApName()).append(",");
					strOutput.append(connectionHistory.getApId()).append(",");
					strOutput.append(connectionHistory.getTrapTimeString()).append(",");
					strOutput.append(connectionHistory.getTrapTypeString()).append(",");
					strOutput.append(connectionHistory.getTrapMessage());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("clientAuth")) {
				strOutput = new StringBuffer();
				strOutput.append(MgrUtil.getUserMessage("report.reportList.clientAuth.macAddress")).append(",");
				strOutput.append(MgrUtil
						.getUserMessage("report.reportList.title.currentClientHostName")).append(",");
				strOutput.append(MgrUtil
						.getUserMessage("report.reportList.title.currentClientUserName")).append(",");
				strOutput.append(MgrUtil
						.getUserMessage("report.reportList.title.currentClientIpAddress")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.clientAuth.eventTime")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.clientAuth.authType")).append(",");
				strOutput
						.append(MgrUtil.getUserMessage("report.reportList.clientAuth.description"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				setClientAuthFilterParam();
				List<AhEvent> lstClientAuth = QueryUtil.executeQuery(AhEvent.class, sortParams, filterParams,
						getDomain().getId());

				for (int cnt = 0; cnt < lstClientAuth.size(); cnt++) {
					strOutput.append(lstClientAuth.get(cnt).getRemoteId()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getClientHostName()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getClientUserName()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getClientIp()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getTrapTimeExcel()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getCodeString()).append(",");
					strOutput.append(lstClientAuth.get(cnt).getTrapDesc());
					strOutput.append("\n");
					if (cnt % 1000 == 0 && cnt != 0) {
						out.write(strOutput.toString());
						strOutput = new StringBuffer();
					}
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals("configAudits")) {
				return false;
			}
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("exportCurrentData in report:", e);
			return false;
		}
	}

	public List<AhEvent> getLstClientAuth() {
		return lstClientAuth;
	}

	public String getReportClientMacWhole() {
		return reportClientMacWhole;
	}

	public void setReportClientMacWhole(String reportClientMacWhole) {
		this.reportClientMacWhole = reportClientMacWhole;
	}

	public String getReportLinkClientMac() {
		return reportLinkClientMac;
	}

	public void setReportLinkClientMac(String reportLinkClientMac) {
		this.reportLinkClientMac = reportLinkClientMac;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public boolean getEmailUsabled() {
		return !getUpdateDisabled().equals("");
	}

	public String getClientIp() {
		return clientIp;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public String getClientHostName() {
		return clientHostName;
	}

	public String getCurrentPageSession() {
		return currentPageSession;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public List<ClientSessionButton> getCurrentLstReportClientSession() {
		return currentLstReportClientSession;
	}

	public Map<String, List<ClientSessionButton>> getMapLstReportClientSession() {
		return mapLstReportClientSession;
	}

	public Map<String, String> getPageSessionList() {
		return pageSessionList;
	}

	public String getPreviousPage() {
		if (getPreviousShowButton()) {
			return String.valueOf(currentPage - 1);
		}
		return String.valueOf(currentPage);
	}

	public boolean getPreviousShowButton() {
		return pageSessionList.size() > 1 && currentPage != 1;
	}

	public String getNextPage() {
		if (getNextShowButton()) {
			return String.valueOf(currentPage + 1);
		}
		return String.valueOf(currentPage);
	}

	public boolean getNextShowButton() {
		return pageSessionList.size() > 1 && currentPage != pageSessionList.size();
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<ComplianceResult> getLstCompliance() {
		return lstCompliance;
	}

	public String getClientBSSID() {
		return clientBSSID;
	}

	public List<CheckItem> getLstClientVendorCount() {
		return lstClientVendorCount;
	}

//	public static String getHmpassStrength() {
//		if (hmpassStrength == null){
//			return MgrUtil.getUserMessage("report.reportList.compliance.weak");
//		}
//		return hmpassStrength;
//	}
	
	public String convertRateToM(int rateValue){
		if (rateValue%1000==0) {
			return String.valueOf(rateValue/1000);
		} else {
			return String.valueOf(((float)rateValue)/1000);
		}
	}

	public List<CheckItem> getWifi0_averageTxCu() {
		return wifi0_averageTxCu;
	}

	public List<CheckItem> getWifi0_averageRxCu() {
		return wifi0_averageRxCu;
	}

	public List<CheckItem> getWifi0_averageInterferenceCu() {
		return wifi0_averageInterferenceCu;
	}

	public List<CheckItem> getWifi0_averageNoiseFloor() {
		return wifi0_averageNoiseFloor;
	}

	public List<CheckItem> getWifi0_shortTermTxCu() {
		return wifi0_shortTermTxCu;
	}

	public List<CheckItem> getWifi0_shortTermRxCu() {
		return wifi0_shortTermRxCu;
	}

	public List<CheckItem> getWifi0_shortTermInterferenceCu() {
		return wifi0_shortTermInterferenceCu;
	}

	public List<CheckItem> getWifi0_shortTermNoiseFloor() {
		return wifi0_shortTermNoiseFloor;
	}

	public List<CheckItem> getWifi0_snapShotTxCu() {
		return wifi0_snapShotTxCu;
	}

	public List<CheckItem> getWifi0_snapShotRxCu() {
		return wifi0_snapShotRxCu;
	}

	public List<CheckItem> getWifi0_snapShotInterferenceCu() {
		return wifi0_snapShotInterferenceCu;
	}

	public List<CheckItem> getWifi0_snapShotNoiseFloor() {
		return wifi0_snapShotNoiseFloor;
	}

	public List<CheckItem> getWifi0_crcError() {
		return wifi0_crcError;
	}

	public List<CheckItem> getWifi1_averageTxCu() {
		return wifi1_averageTxCu;
	}

	public List<CheckItem> getWifi1_averageRxCu() {
		return wifi1_averageRxCu;
	}

	public List<CheckItem> getWifi1_averageInterferenceCu() {
		return wifi1_averageInterferenceCu;
	}

	public List<CheckItem> getWifi1_averageNoiseFloor() {
		return wifi1_averageNoiseFloor;
	}

	public List<CheckItem> getWifi1_shortTermTxCu() {
		return wifi1_shortTermTxCu;
	}

	public List<CheckItem> getWifi1_shortTermRxCu() {
		return wifi1_shortTermRxCu;
	}

	public List<CheckItem> getWifi1_shortTermInterferenceCu() {
		return wifi1_shortTermInterferenceCu;
	}

	public List<CheckItem> getWifi1_shortTermNoiseFloor() {
		return wifi1_shortTermNoiseFloor;
	}

	public List<CheckItem> getWifi1_snapShotTxCu() {
		return wifi1_snapShotTxCu;
	}

	public List<CheckItem> getWifi1_snapShotRxCu() {
		return wifi1_snapShotRxCu;
	}

	public List<CheckItem> getWifi1_snapShotInterferenceCu() {
		return wifi1_snapShotInterferenceCu;
	}

	public List<CheckItem> getWifi1_snapShotNoiseFloor() {
		return wifi1_snapShotNoiseFloor;
	}

	public List<CheckItem> getWifi1_crcError() {
		return wifi1_crcError;
	}

	public int getLinkComplianceType() {
		return linkComplianceType;
	}

	public void setLinkComplianceType(int linkComplianceType) {
		this.linkComplianceType = linkComplianceType;
	}

	public List<AhBandWidthSentinelHistory> getLstHiveApSla() {
		return lstHiveApSla;
	}

	public List<CheckItem> getLstClientSlaAlert() {
		return lstClientSlaAlert;
	}

	public List<CheckItem> getLstClientSlaBad() {
		return lstClientSlaBad;
	}

	public List<CheckItem> getLstHiveApSlaAlert() {
		return lstHiveApSlaAlert;
	}

	public List<CheckItem> getLstHiveApSlaBad() {
		return lstHiveApSlaBad;
	}

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public int getReportPeriodFromClient() {
		return reportPeriodFromClient;
	}

	public void setReportPeriodFromClient(int reportPeriodFromClient) {
		this.reportPeriodFromClient = reportPeriodFromClient;
	}

	public List<CheckItem> getMaxClients() {
		return maxClients;
	}

	public List<APConnectHistoryInfo> getLstHiveApConnection() {
		return lstHiveApConnection;
	}

	public List<CheckItem> getClient_rec_drop() {
		return client_rec_drop;
	}

	public List<CheckItem> getClient_bandwidth() {
		return client_bandwidth;
	}

	public List<CheckItem> getClient_rec_airTime() {
		return client_rec_airTime;
	}

	public List<CheckItem> getClient_trans_airTime() {
		return client_trans_airTime;
	}

	public Map<String, List<CheckItem>> getClient_rec_rate_dis() {
		return client_rec_rate_dis;
	}

	public List<TextItem> getClient_rec_rate_succ_dis() {
		return client_rec_rate_succ_dis;
	}

	public Map<String, List<CheckItem>> getClient_trans_rate_dis() {
		return client_trans_rate_dis;
	}

	public List<TextItem> getClient_trans_rate_succ_dis() {
		return client_trans_rate_succ_dis;
	}

	public List<CheckItem> getClient_trans_drop() {
		return client_trans_drop;
	}

	public List<CheckItem> getClient_slacount() {
		return client_slacount;
	}

	public void setClient_rec_rate_dis(Map<String, List<CheckItem>> client_rec_rate_dis) {
		this.client_rec_rate_dis = client_rec_rate_dis;
	}

	public List<String> getClient_trans_rateTypeList() {
		return client_trans_rateTypeList;
	}

	public List<String> getClient_trans_dateTimeList() {
		return client_trans_dateTimeList;
	}

	public List<String> getClient_rec_dateTimeList() {
		return client_rec_dateTimeList;
	}

	public List<String> getClient_rec_rateTypeList() {
		return client_rec_rateTypeList;
	}

	public List<String> getHiveap_wifi0_trans_rateTypeList() {
		return hiveap_wifi0_trans_rateTypeList;
	}

	public void setHiveap_wifi0_trans_rateTypeList(List<String> hiveap_wifi0_trans_rateTypeList) {
		this.hiveap_wifi0_trans_rateTypeList = hiveap_wifi0_trans_rateTypeList;
	}

	public List<String> getHiveap_wifi0_rec_rateTypeList() {
		return hiveap_wifi0_rec_rateTypeList;
	}

	public List<String> getHiveap_wifi0_rec_dateTimeList() {
		if (hiveap_wifi0_rec_dateTimeList.size()>200){
			return hiveap_wifi0_rec_dateTimeList.subList(hiveap_wifi0_rec_dateTimeList.size()-200, hiveap_wifi0_rec_dateTimeList.size()-1);
		}
		return hiveap_wifi0_rec_dateTimeList;
	}

	public List<String> getHiveap_wifi0_trans_dateTimeList() {
		if (hiveap_wifi0_trans_dateTimeList.size()>200){
			return hiveap_wifi0_trans_dateTimeList.subList(hiveap_wifi0_trans_dateTimeList.size()-200, hiveap_wifi0_trans_dateTimeList.size()-1);
		}
		return hiveap_wifi0_trans_dateTimeList;
	}

	public Map<String, List<CheckItem>> getHiveap_wifi0_rec_rate_dis() {
		return hiveap_wifi0_rec_rate_dis;
	}

	public List<TextItem> getHiveap_wifi0_rec_rate_succ_dis() {
		return hiveap_wifi0_rec_rate_succ_dis;
	}

	public Map<String, List<CheckItem>> getHiveap_wifi0_trans_rate_dis() {
		return hiveap_wifi0_trans_rate_dis;
	}

	public List<TextItem> getHiveap_wifi0_trans_rate_succ_dis() {
		return hiveap_wifi0_trans_rate_succ_dis;
	}

	public List<CheckItem> getHiveap_wifi0_rec_unicast() {
		return hiveap_wifi0_rec_unicast;
	}

	public List<CheckItem> getHiveap_wifi0_trans_unicast() {
		return hiveap_wifi0_trans_unicast;
	}

	public List<CheckItem> getHiveap_wifi0_rec_broadcast() {
		return hiveap_wifi0_rec_broadcast;
	}

	public List<CheckItem> getHiveap_wifi0_trans_broadcast() {
		return hiveap_wifi0_trans_broadcast;
	}

	public List<CheckItem> getHiveap_wifi0_rec_drops() {
		return hiveap_wifi0_rec_drops;
	}

	public List<CheckItem> getHiveap_wifi0_trans_drops() {
		return hiveap_wifi0_trans_drops;
	}

	public List<CheckItem> getHiveap_wifi0_rec_totalU() {
		return hiveap_wifi0_rec_totalU;
	}

	public List<CheckItem> getHiveap_wifi0_trans_totalU() {
		return hiveap_wifi0_trans_totalU;
	}

	public List<CheckItem> getHiveap_wifi0_rec_retryRateU() {
		return hiveap_wifi0_rec_retryRateU;
	}

	public List<CheckItem> getHiveap_wifi0_trans_retryRateU() {
		return hiveap_wifi0_trans_retryRateU;
	}

	public List<CheckItem> getHiveap_wifi0_rec_airTimeU() {
		return hiveap_wifi0_rec_airTimeU;
	}

	public List<CheckItem> getHiveap_wifi0_trans_airTimeU() {
		return hiveap_wifi0_trans_airTimeU;
	}

	public List<CheckItem> getHiveap_wifi0_crcErrorRateU() {
		return hiveap_wifi0_crcErrorRateU;
	}

	public List<CheckItem> getHiveap_wifi0_totalChannelU() {
		return hiveap_wifi0_totalChannelU;
	}

	public List<CheckItem> getHiveap_wifi0_InterferenceU() {
		return hiveap_wifi0_InterferenceU;
	}

	public List<CheckItem> getHiveap_wifi0_noiseFloor() {
		return hiveap_wifi0_noiseFloor;
	}

	public List<String> getHiveap_wifi1_rec_rateTypeList() {
		return hiveap_wifi1_rec_rateTypeList;
	}

	public List<String> getHiveap_wifi1_rec_dateTimeList() {
		if (hiveap_wifi1_rec_dateTimeList.size()>200){
			return hiveap_wifi1_rec_dateTimeList.subList(hiveap_wifi1_rec_dateTimeList.size()-200, hiveap_wifi1_rec_dateTimeList.size()-1);
		}
		return hiveap_wifi1_rec_dateTimeList;
	}

	public List<String> getHiveap_wifi1_trans_rateTypeList() {
		return hiveap_wifi1_trans_rateTypeList;
	}

	public List<String> getHiveap_wifi1_trans_dateTimeList() {
		if (hiveap_wifi1_trans_dateTimeList.size()>200){
			return hiveap_wifi1_trans_dateTimeList.subList(hiveap_wifi1_trans_dateTimeList.size()-200, hiveap_wifi1_trans_dateTimeList.size()-1);
		}
		return hiveap_wifi1_trans_dateTimeList;
	}

	public Map<String, List<CheckItem>> getHiveap_wifi1_rec_rate_dis() {
		return hiveap_wifi1_rec_rate_dis;
	}

	public List<TextItem> getHiveap_wifi1_rec_rate_succ_dis() {
		return hiveap_wifi1_rec_rate_succ_dis;
	}

	public Map<String, List<CheckItem>> getHiveap_wifi1_trans_rate_dis() {
		return hiveap_wifi1_trans_rate_dis;
	}

	public List<TextItem> getHiveap_wifi1_trans_rate_succ_dis() {
		return hiveap_wifi1_trans_rate_succ_dis;
	}

	public List<CheckItem> getHiveap_wifi1_rec_unicast() {
		return hiveap_wifi1_rec_unicast;
	}

	public List<CheckItem> getHiveap_wifi1_trans_unicast() {
		return hiveap_wifi1_trans_unicast;
	}

	public List<CheckItem> getHiveap_wifi1_rec_broadcast() {
		return hiveap_wifi1_rec_broadcast;
	}

	public List<CheckItem> getHiveap_wifi1_trans_broadcast() {
		return hiveap_wifi1_trans_broadcast;
	}

	public List<CheckItem> getHiveap_wifi1_rec_drops() {
		return hiveap_wifi1_rec_drops;
	}

	public List<CheckItem> getHiveap_wifi1_trans_drops() {
		return hiveap_wifi1_trans_drops;
	}

	public List<CheckItem> getHiveap_wifi1_rec_totalU() {
		return hiveap_wifi1_rec_totalU;
	}

	public List<CheckItem> getHiveap_wifi1_trans_totalU() {
		return hiveap_wifi1_trans_totalU;
	}

	public List<CheckItem> getHiveap_wifi1_rec_retryRateU() {
		return hiveap_wifi1_rec_retryRateU;
	}

	public List<CheckItem> getHiveap_wifi1_trans_retryRateU() {
		return hiveap_wifi1_trans_retryRateU;
	}

	public List<CheckItem> getHiveap_wifi1_rec_airTimeU() {
		return hiveap_wifi1_rec_airTimeU;
	}

	public List<CheckItem> getHiveap_wifi1_trans_airTimeU() {
		return hiveap_wifi1_trans_airTimeU;
	}

	public List<CheckItem> getHiveap_wifi1_crcErrorRateU() {
		return hiveap_wifi1_crcErrorRateU;
	}

	public List<CheckItem> getHiveap_wifi1_totalChannelU() {
		return hiveap_wifi1_totalChannelU;
	}

	public List<CheckItem> getHiveap_wifi1_InterferenceU() {
		return hiveap_wifi1_InterferenceU;
	}

	public List<CheckItem> getHiveap_wifi1_noiseFloor() {
		return hiveap_wifi1_noiseFloor;
	}

	public List<TrafficData> getHiveAPNonComplianceList() {
		return hiveAPNonComplianceList;
	}

	public List<TrafficData> getClientNonComplianceList() {
		return clientNonComplianceList;
	}

	public String getCurrentAPName() {
		return currentAPName;
	}

	public void setCurrentAPName(String currentAPName) {
		this.currentAPName = currentAPName;
	}

	
	public void setApClientSlaPanelValue(int apClientFlg) throws JSONException{
		Calendar reportDateTime = getReportDateTime();
		FilterParams filterSLAHistory;
		if (apClientFlg==1) {
			filterSLAHistory = new FilterParams(
					"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) and lower(apName)=:s4",
					new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
							,AhBandWidthSentinelHistory.STATUS_ALERT, getCurrentAPName().toLowerCase()});
		} else {
			filterSLAHistory = new FilterParams(
					"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) " +
					"and lower(clientMac)=:s4",
					new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
							,AhBandWidthSentinelHistory.STATUS_ALERT, getCurrentAPName().toLowerCase()});
		}
		List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
				new SortParams("timeStamp.time",false), filterSLAHistory, getDomain().getId());

		for(AhBandWidthSentinelHistory oneItem : slaHistory){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("name", oneItem.getApName());
			jsonObj.put("clientMac", oneItem.getClientMac());
			jsonObj.put("status", oneItem.getBandWidthSentinelStatusString());
			jsonObj.put("guaBandwidth", oneItem.getGuaranteedBandWidth());
			jsonObj.put("actBandwidth", oneItem.getActualBandWidth());
			if (apClientFlg==1) {
				jsonObj.put("channelCu", oneItem.getChannelUltil()>=0?oneItem.getChannelUltil():"Unknown");
				jsonObj.put("interferenceCu", oneItem.getInterferenceUltil()>=0?oneItem.getInterferenceUltil():"Unknown");
				jsonObj.put("txCu", oneItem.getTxUltil()>=0?oneItem.getTxUltil():"Unknown");
				jsonObj.put("rxCu", oneItem.getRxUltil()>=0?oneItem.getRxUltil():"Unknown");
			}
			jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp().getTime(),tz));
			jsonArray.put(jsonObj);
		}
		
	}

	public void setApInterfacePanelValue(int errFlg) throws JSONException{
		Calendar reportDateTime = getReportDateTime();
		FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2 and lower(apName)=:s3",
				new Object[] { reportDateTime.getTimeInMillis(),0,getCurrentAPName().toLowerCase() });
		List<AhInterfaceStats> interfaceStatsHistory =QueryUtil.executeQuery(AhInterfaceStats.class, new SortParams("timeStamp",false),
				filterInterfaceHistory,getDomain().getId());
		
		for(AhInterfaceStats oneItem : interfaceStatsHistory){
			int argFlg = oneItem.getAlarmFlag();
			// CRC error
			if (errFlg==1) {
				if ((argFlg & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("ifName", oneItem.getIfName());
					jsonObj.put("crcErrorRate", oneItem.getCrcErrorRate());
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("channelCu", oneItem.getTotalChannelUtilization());
					jsonObj.put("interferenceCu", oneItem.getInterferenceUtilization());
					jsonObj.put("txCu", oneItem.getTxUtilization());
					jsonObj.put("rxCu", oneItem.getRxUtilization());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Tx Drop
			} else if (errFlg==2){
				if ((argFlg >>>1 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("ifName", oneItem.getIfName());
					jsonObj.put("txDrop", getPencentageValue(oneItem.getTxDrops(), oneItem.getUniTxFrameCount() + oneItem.getBcastTxFrameCount()));
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("channelCu", oneItem.getTotalChannelUtilization());
					jsonObj.put("interferenceCu", oneItem.getInterferenceUtilization());
					jsonObj.put("txCu", oneItem.getTxUtilization());
					jsonObj.put("rxCu", oneItem.getRxUtilization());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Rx Drop
			} else if (errFlg==3){
				if ((argFlg >>>2 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("ifName", oneItem.getIfName());
					jsonObj.put("rxDrop", getPencentageValue(oneItem.getRxDrops(), oneItem.getUniRxFrameCount()+ oneItem.getBcastRxFrameCount()));
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("channelCu", oneItem.getTotalChannelUtilization());
					jsonObj.put("interferenceCu", oneItem.getInterferenceUtilization());
					jsonObj.put("txCu", oneItem.getTxUtilization());
					jsonObj.put("rxCu", oneItem.getRxUtilization());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Tx Retry
			} else if (errFlg==4){
				if ((argFlg >>>3 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("ifName", oneItem.getIfName());
					jsonObj.put("txRetry", oneItem.getTxRetryRate());
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("channelCu", oneItem.getTotalChannelUtilization());
					jsonObj.put("interferenceCu", oneItem.getInterferenceUtilization());
					jsonObj.put("txCu", oneItem.getTxUtilization());
					jsonObj.put("rxCu", oneItem.getRxUtilization());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Airtime
			} else if (errFlg==5){
				if ((argFlg >>>4 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("ifName", oneItem.getIfName());
					jsonObj.put("txAirtime", oneItem.getTxAirTime());
					jsonObj.put("rxAirtime", oneItem.getRxAirTime());
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("channelCu", oneItem.getTotalChannelUtilization());
					jsonObj.put("interferenceCu", oneItem.getInterferenceUtilization());
					jsonObj.put("txCu", oneItem.getTxUtilization());
					jsonObj.put("rxCu", oneItem.getRxUtilization());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			}
		}
		
	}

	public void setClientPanelValue(int errFlg) throws JSONException{
		Calendar reportDateTime = getReportDateTime();
		FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and (alarmFlag>:s2 or overallClientHealthScore<:s3) and lower(clientMac)=:s4",
				new Object[] { reportDateTime.getTimeInMillis(),0, AhClientSession.CLIENT_SCORE_RED,getCurrentAPName().toLowerCase() });
		List<AhClientStats> clientStatsHistory =QueryUtil.executeQuery(AhClientStats.class, new SortParams("timeStamp",false),
				filterInterfaceHistory,getDomain().getId());

		for(AhClientStats oneItem : clientStatsHistory){
			int argFlg = oneItem.getAlarmFlag();
			// Tx Drop
			if (errFlg==2) {
				if ((argFlg>>>1 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("clientMac", oneItem.getClientMac());
					jsonObj.put("ssidName", oneItem.getSsidName());
					jsonObj.put("txDrop", getPencentageValue(oneItem.getTxFrameDropped(), oneItem.getTxFrameCount()));
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Rx Drop
			} else if (errFlg==3){
				if ((argFlg >>>2 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("clientMac", oneItem.getClientMac());
					jsonObj.put("ssidName", oneItem.getSsidName());
					jsonObj.put("rxDrop", getPencentageValue(oneItem.getRxFrameDropped(), oneItem.getRxFrameCount()));
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// Airtime
			} else if (errFlg==5){
				if ((argFlg >>>4 & 0x01) ==1) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("clientMac", oneItem.getClientMac());
					jsonObj.put("ssidName", oneItem.getSsidName());
					jsonObj.put("txAirtime", oneItem.getTxAirTime());
					jsonObj.put("rxAirtime", oneItem.getRxAirTime());
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			// client score
			} else if (errFlg==6){
				if (oneItem.getOverallClientHealthScore()<AhClientSession.CLIENT_SCORE_RED) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("name", oneItem.getApName());
					jsonObj.put("clientMac", oneItem.getClientMac());
					jsonObj.put("ssidName", oneItem.getSsidName());
					jsonObj.put("score", oneItem.getOverallClientHealthScore());
					jsonObj.put("radioscore", oneItem.getSlaConnectScore());
					jsonObj.put("ipnetworkscore", oneItem.getIpNetworkConnectivityScore());
					jsonObj.put("applicationscore", oneItem.getApplicationHealthScore());
					jsonObj.put("collectPeriod", oneItem.getCollectPeriod());
					jsonObj.put("reportTime", AhDateTimeUtil.getSpecifyDateTimeReport(oneItem.getTimeStamp(),tz));
					jsonArray.put(jsonObj);
				}
			}
		}
	}
	
	public String getTempTableSuffix(){
		char[] tmpField = "TempRadiusUser".toCharArray();
		if (getUserContext().getEmailAddress()!=null && !getUserContext().getEmailAddress().equals("")){
			tmpField = getUserContext().getEmailAddress().toCharArray();
		}
		StringBuilder bufField = new StringBuilder();
		for (char field : tmpField) {
			if ((field >= 'a' && field <= 'z')
					|| (field >= 'A' && field <= 'Z')
					|| (field >= '0' && field <= '9')) {
				bufField.append(field);
			} else {
				bufField.append("_");
			}
		}
		
		if (bufField.toString().length()>36){
			return bufField.toString().substring(0, 36);
		}
		return bufField.toString();
	}

	/**
	 * @return the hiveap_wifi0_bandsteering
	 */
	public List<CheckItem> getHiveap_wifi0_bandsteering() {
		return hiveap_wifi0_bandsteering;
	}

	/**
	 * @return the hiveap_wifi0_loadbalance
	 */
	public List<CheckItem> getHiveap_wifi0_loadbalance() {
		return hiveap_wifi0_loadbalance;
	}

	/**
	 * @return the hiveap_wifi0_weaksnr
	 */
	public List<CheckItem> getHiveap_wifi0_weaksnr() {
		return hiveap_wifi0_weaksnr;
	}

	/**
	 * @return the hiveap_wifi0_safetynet
	 */
	public List<CheckItem> getHiveap_wifi0_safetynet() {
		return hiveap_wifi0_safetynet;
	}

	/**
	 * @return the hiveap_wifi0_proberequest
	 */
	public List<CheckItem> getHiveap_wifi0_proberequest() {
		return hiveap_wifi0_proberequest;
	}

	/**
	 * @return the hiveap_wifi0_authrequest
	 */
	public List<CheckItem> getHiveap_wifi0_authrequest() {
		return hiveap_wifi0_authrequest;
	}

	/**
	 * @return the hiveap_wifi1_bandsteering
	 */
	public List<CheckItem> getHiveap_wifi1_bandsteering() {
		return hiveap_wifi1_bandsteering;
	}

	/**
	 * @return the hiveap_wifi1_loadbalance
	 */
	public List<CheckItem> getHiveap_wifi1_loadbalance() {
		return hiveap_wifi1_loadbalance;
	}

	/**
	 * @return the hiveap_wifi1_weaksnr
	 */
	public List<CheckItem> getHiveap_wifi1_weaksnr() {
		return hiveap_wifi1_weaksnr;
	}

	/**
	 * @return the hiveap_wifi1_safetynet
	 */
	public List<CheckItem> getHiveap_wifi1_safetynet() {
		return hiveap_wifi1_safetynet;
	}

	/**
	 * @return the hiveap_wifi1_proberequest
	 */
	public List<CheckItem> getHiveap_wifi1_proberequest() {
		return hiveap_wifi1_proberequest;
	}

	/**
	 * @return the hiveap_wifi1_authrequest
	 */
	public List<CheckItem> getHiveap_wifi1_authrequest() {
		return hiveap_wifi1_authrequest;
	}

	/**
	 * @return the client_score
	 */
	public List<CheckItem> getClient_score() {
		return client_score;
	}

	/**
	 * @return the client_radio_score
	 */
	public List<CheckItem> getClient_radio_score() {
		return client_radio_score;
	}

	/**
	 * @return the client_ipnetwork_score
	 */
	public List<CheckItem> getClient_ipnetwork_score() {
		return client_ipnetwork_score;
	}

	/**
	 * @return the client_application_score
	 */
	public List<CheckItem> getClient_application_score() {
		return client_application_score;
	}

//	/**
//	 * @return the client_trans_total_rate_succ_dis
//	 */
//	public List<TextItem> getClient_trans_total_rate_succ_dis() {
//		return client_trans_total_rate_succ_dis;
//	}
//
//	/**
//	 * @return the client_rec_total_rate_succ_dis
//	 */
//	public List<TextItem> getClient_rec_total_rate_succ_dis() {
//		return client_rec_total_rate_succ_dis;
//	}
//
//	/**
//	 * @return the hiveap_wifi1_trans_rate_total_succ_dis
//	 */
//	public List<TextItem> getHiveap_wifi1_trans_rate_total_succ_dis() {
//		return hiveap_wifi1_trans_rate_total_succ_dis;
//	}
//
//	/**
//	 * @return the hiveap_wifi0_trans_rate_total_succ_dis
//	 */
//	public List<TextItem> getHiveap_wifi0_trans_rate_total_succ_dis() {
//		return hiveap_wifi0_trans_rate_total_succ_dis;
//	}
//
//	/**
//	 * @return the hiveap_wifi0_rec_rate_total_succ_dis
//	 */
//	public List<TextItem> getHiveap_wifi0_rec_rate_total_succ_dis() {
//		return hiveap_wifi0_rec_rate_total_succ_dis;
//	}
//
//	/**
//	 * @return the hiveap_wifi1_rec_rate_total_succ_dis
//	 */
//	public List<TextItem> getHiveap_wifi1_rec_rate_total_succ_dis() {
//		return hiveap_wifi1_rec_rate_total_succ_dis;
//	}

}