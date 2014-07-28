package com.ah.be.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

import javax.servlet.http.HttpSession;

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

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientCountForAP;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsAvailabilityLow;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsLatencyLow;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsThroughputLow;
import com.ah.bo.performance.AhStatsVpnStatusHigh;
import com.ah.bo.performance.AhStatsVpnStatusLow;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.performance.ComplianceResult;
import com.ah.bo.performance.ComplianceSsidListInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.ui.actions.monitor.HeaderFooterPage;
import com.ah.ui.actions.monitor.ReportListAction;
import com.ah.ui.actions.monitor.ReportServiceFilter;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 *
 *@filename		BePerformScheduleImpl.java
 *@version		V1.0.0.0
 *@author		Fisher
 *@createtime	2008-5-6 11:12:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BePerformScheduleImpl implements QueryBo {
	private static final Tracer log = new Tracer(
			BePerformScheduleImpl.class.getSimpleName());
	
	public boolean excutePerformance(String listType, Long reportId) {
		try {
			AhReport profile = QueryUtil.findBoById(AhReport.class, reportId, this);
			return excutePerformance(profile.getReportType(), profile, profile.getOwner().getTimeZone());
		}catch (OutOfMemoryError outError) {
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
			"report scheduler running out of memory~~~!!!");
			log.error(outError);
			return false;
		}
	}

	public boolean excutePerformance(String listType, AhReport profile, TimeZone tz) {
		File tmpFileDir = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
				+ profile.getOwner().getDomainName());
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}

		if (listType.equals("radioTrafficMetrics")) {
			return setRadioTrafficMetrics(profile, tz);
		} else if (listType.equals("channelPowerNoise")) {
			if (profile.getNewOldFlg() == AhReport.REPORT_NEWOLDTYEP_NEW){
				return setChannelPowerNoiseNew(profile, tz);
			} else {
				return setChannelPowerNoise(profile, tz);
			}
		} else if (listType.equals("radioTroubleShooting")) {
			return setRadioTroubleShooting(profile, tz);
		} else if (listType.equals("radioAirTime")) {
			return setRadioAirTime(profile, tz);
		} else if (listType.equals("radioInterference")) {
			return setRadioInterference(profile, tz);
		} else if (listType.equals("ssidAirTime")) {
			return setSsidAirTime(profile, tz);
		} else if (listType.equals("ssidTrafficMetrics")) {
			return setSsidTrafficMetrics(profile, tz);
		} else if (listType.equals("ssidTroubleShooting")) {
			return setSsidTroubleShooting(profile, tz);
		} else if (listType.equals("mostClientsAPs")) {
			return setMostClientsAPs(profile, tz);
		} else if (listType.equals("clientSession")) {
			if (profile.getNewOldFlg() == AhReport.REPORT_NEWOLDTYEP_NEW){
				return setClientSessionNew(profile, tz);
			} else {
				return setClientSession(profile, tz);
			}
		} else if (listType.equals("clientCount")) {
			return setClientCount(profile, tz);
		} else if (listType.equals("clientAirTime")) {
			return setClientAirTime(profile, tz);
		} else if (listType.equals("uniqueClientCount")) {
			return setUniqueClientCount(profile, tz);
		} else if (listType.equals("securityRogueAPs")) {
			return setSecurityRogueAPs(profile, tz);
		} else if (listType.equals("securityRogueClients")) {
			return setSecurityRogueClients(profile, tz);
		} else if (listType.equals("securityDetection")) {
			return setSecurityDetection(profile, tz);
		} else if (listType.equals("compliance")) {
			return setCompliance(profile, tz);
		} else if (listType.equals("pciCompliance")) {
			return setPCICompliance(profile, tz);
		} else if (listType.equals("hiveApNonCompliance")){
			return generalCurrentNonHiveAPCsvFile(profile,tz);
		} else if (listType.equals("clientNonCompliance")){
			return generalCurrentNonClientCsvFile(profile,tz);
		} else if (listType.equals("meshNeighbors")) {
			return setMeshNeighbors(profile, tz);
		} else if (listType.equals("clientVendor")) {
			return setClientVendor(profile, tz);
		} else if (listType.equals("inventory")) {
			return setInventory(profile, tz);
		} else if (listType.equals("clientAuth")) {
			return setClientAuth(profile, tz);
		} else if (listType.equals("hiveApSla")) {
			return setHiveAPSlaCompliance(profile, tz);
		} else if (listType.equals("clientSla")) {
			return setClientSlaCompliance(profile, tz);
		} else if (listType.equals("maxClient")) {
			return setMaxClient(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_HIVEAPCONNECTION)) {
			return setHiveApConnection(profile, tz);
		} else if (listType.equals("configAudits")) {
			return setConfigAudits(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_SUMMARYUSAGE)
				||listType.equals(Navigation.L2_FEATURE_DETAILUSAGE)) {
			return setLoginUserUsage(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY)){
			return setVpnAvailablity(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT)) {
			return setVpnThroughput(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_VPNLATENCY)) {
			return setVpnLatency(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_WANAVAILABILITY)
				|| listType.equals(Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY)) {
			return setWanAvailablity(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)
				|| listType.equals(Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT)) {
			return setWanThroughput(profile, tz);
		} else if (listType.equals(Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			return setGwVpnAvailablity(profile, tz);
		}
		return true;
	}
	
	public boolean setLoginUserUsage(AhReport profile, TimeZone tz){
		long timeInMill = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();
		List<AhAdminLoginSession> reportResult;
		if (profile.getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SUMMARYUSAGE)){
			String sumLoginTimeSQL= "select username,owner,sum(totallogintime),count(id) " +
					"from ah_adminlogin_session where logintime>=" + timeInMill + 
					" group by username, owner";
			List<?> lstSumLoginTime = QueryUtil.executeNativeQuery(sumLoginTimeSQL);
			
			String uniqueAdminSQL= "select DISTINCT ON (username,owner) id " +
					"from ah_adminlogin_session where logintime>=" + timeInMill +
					" order by username,owner, logintime desc";
			List<?> lstUniqueAdminId = QueryUtil.executeNativeQuery(uniqueAdminSQL);
	
			String apCountSQL= "select owner,count(id) from hive_ap " +
					" where managestatus=1 group by owner";
			List<?> lstApCount = QueryUtil.executeNativeQuery(apCountSQL);
			
			FilterParams myFilterParams=null;
			if (!lstUniqueAdminId.isEmpty()){
				List<Long> filterIds = new ArrayList<Long>();
				for (Object object: lstUniqueAdminId){
					filterIds.add(Long.parseLong(object.toString()));
				}
				myFilterParams = new FilterParams("id", filterIds);
			}
			long sysTime = System.currentTimeMillis();
			
			if (myFilterParams != null){
				reportResult = QueryUtil.executeQuery(AhAdminLoginSession.class, null, myFilterParams);
				List<HmUser> lstHmUser = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("defaultFlag",true));
				for(HmUser oneUser:lstHmUser){
					boolean needAdd = true;
					for (AhAdminLoginSession oneObjFinal:reportResult){
						if (oneObjFinal.getOwner().getId().equals(oneUser.getOwner().getId())){
							needAdd=false;
							break;
						}
					}
					if (needAdd){
						AhAdminLoginSession needaddClass = new AhAdminLoginSession();
						needaddClass.setApCount(0);
						needaddClass.setCurrentLoginCount(0);
						needaddClass.setEmailAddress(oneUser.getEmailAddress());
						needaddClass.setLoginCount(0);
						needaddClass.setOwner(oneUser.getDomain());
						needaddClass.setPlannerAdminTimeZone(tz);
						needaddClass.setTimeZone(oneUser.getTimeZone());
						needaddClass.setTotalLoginTime(0);
						needaddClass.setUserFullName(oneUser.getUserFullName());
						needaddClass.setUserName(oneUser.getUserName());
						reportResult.add(needaddClass);
					}
				}
				for (AhAdminLoginSession oneUser : reportResult) {
					oneUser.setPlannerAdminTimeZone(tz);
					for (Object obj:lstSumLoginTime){
						Object[] oneObjectSum = (Object[])obj;
						if (oneObjectSum[0].toString().equalsIgnoreCase(oneUser.getUserName())
								&& oneObjectSum[1].toString().equals(oneUser.getOwner().getId().toString())) {
							oneUser.setTotalLoginTime(Long.parseLong(oneObjectSum[2].toString()));
							oneUser.setLoginCount(Long.parseLong(oneObjectSum[3].toString()));
						}
					}

					for (Object obj:lstApCount){
						Object[] oneObjectApCount = (Object[])obj;
						if (oneObjectApCount[0].toString().equals(oneUser.getOwner().getId().toString())) {
							oneUser.setApCount(Long.parseLong(oneObjectApCount[1].toString()));
						}
					}
					
					for (HttpSession activeUser : CurrentUserCache.getInstance()
							.getActiveSessions()) {
						HmUser sessionUser;
						try {
							sessionUser = (HmUser) activeUser.getAttribute(SessionKeys.USER_CONTEXT);
						} catch (Exception e) {
							continue;
						}
						if (sessionUser != null) {
							if (sessionUser.getUserName().equalsIgnoreCase(oneUser.getUserName())
									&& sessionUser.getDomain().getId().equals(oneUser.getOwner().getId())) {
								if (sysTime - activeUser.getCreationTime() >= 0) {
									oneUser.setTotalLoginTime(oneUser.getTotalLoginTime()
											+ sysTime - activeUser.getCreationTime());
								}
								if (oneUser.getLoginTime()<activeUser.getCreationTime()){
									oneUser.setLoginTime(activeUser.getCreationTime());
								}
								oneUser.setCurrentLoginCount(oneUser.getCurrentLoginCount() + 1);
								oneUser.setLoginCount(oneUser.getLoginCount() + 1);
							}
						}
					}
				} 
			} else {
				Map<String,AhAdminLoginSession> userLoginMap = new HashMap<String,AhAdminLoginSession>();

				for (HttpSession activeUser : CurrentUserCache.getInstance()
						.getActiveSessions()) {
					HmUser sessionUser;
					try {
						sessionUser = (HmUser) activeUser.getAttribute(SessionKeys.USER_CONTEXT);
					} catch (Exception e){
						continue;
					}
					if (sessionUser != null) {
						String keyValue = sessionUser.getOwner().getId().toString();
						if (userLoginMap.get(keyValue)==null) {
							userLoginMap.put(keyValue, new AhAdminLoginSession());
						}
						AhAdminLoginSession oneUser = userLoginMap.get(keyValue);
						
						List<Short> cvgList = new ArrayList<>();
						cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
						cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
						long apCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
								"manageStatus = :s1 and owner.id=:s2 " +
								" and (deviceType=:s3 or deviceType=:s4 or deviceType=:s5) " +
								" and hiveApModel not in :s6", 
								new Object[] { HiveAp.STATUS_MANAGED, 
										sessionUser.getOwner().getId(), HiveAp.Device_TYPE_HIVEAP,
										HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR, cvgList}));
						oneUser.setApCount(apCount);
						
						oneUser.setCurrentLoginCount(oneUser.getCurrentLoginCount()+1);
						oneUser.setEmailAddress(sessionUser.getEmailAddress());
						oneUser.setLoginCount(oneUser.getLoginCount()+1);
						if (oneUser.getLoginTime()<activeUser.getCreationTime()){
							oneUser.setLoginTime(activeUser.getCreationTime());
						}
						oneUser.setOwner(sessionUser.getDomain());
						oneUser.setPlannerAdminTimeZone(tz);
						oneUser.setTimeZone(sessionUser.getTimeZone());
						oneUser.setTotalLoginTime(oneUser.getTotalLoginTime() + sysTime - activeUser.getCreationTime());
						oneUser.setUserFullName(sessionUser.getUserFullName());
						oneUser.setUserName(sessionUser.getUserName());
					}
				}
				reportResult = new ArrayList<AhAdminLoginSession>();
				for(String key:userLoginMap.keySet()){
					reportResult.add(userLoginMap.get(key));
				}
				List<HmUser> lstHmUser = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("defaultFlag",true));
				for(HmUser oneUser:lstHmUser){
					boolean needAdd = true;
					for (AhAdminLoginSession oneObjFinal:reportResult){
						if (oneObjFinal.getOwner().getId().equals(oneUser.getOwner().getId())){
							needAdd=false;
							break;
						}
					}
					if (needAdd){
						AhAdminLoginSession needaddClass = new AhAdminLoginSession();
						needaddClass.setApCount(0);
						needaddClass.setCurrentLoginCount(0);
						needaddClass.setEmailAddress(oneUser.getEmailAddress());
						needaddClass.setLoginCount(0);
						needaddClass.setOwner(oneUser.getDomain());
						needaddClass.setPlannerAdminTimeZone(tz);
						needaddClass.setTimeZone(oneUser.getTimeZone());
						needaddClass.setTotalLoginTime(0);
						needaddClass.setUserFullName(oneUser.getUserFullName());
						needaddClass.setUserName(oneUser.getUserName());
						reportResult.add(needaddClass);
					}
				}
			}
		} else {
			FilterParams myFilterParams=new FilterParams("loginTime>=:s1 and owner.domainName=:s2",
					new Object[]{timeInMill,profile.getDetailDomainName()});
			reportResult = QueryUtil.executeQuery(AhAdminLoginSession.class, null, myFilterParams);
			for (AhAdminLoginSession oneSession:reportResult){
				oneSession.setPlannerAdminTimeZone(tz);
			}
		}
		final int cuPageSort = profile.getCuPageSort();
		final boolean cuPageSortDesc = profile.getCuPageSortDesc();
		if (reportResult != null && !reportResult.isEmpty()){
			Collections.sort(reportResult, new Comparator<AhAdminLoginSession>() {
				@Override
				public int compare(AhAdminLoginSession o1, AhAdminLoginSession o2) {
					double ret = 0;
					if (cuPageSort==1){
						ret = o1.getUserName().compareToIgnoreCase(o2.getUserName());
					} else if (cuPageSort==2){
						ret = o1.getUserFullName().compareToIgnoreCase(o2.getUserFullName());
					} else if (cuPageSort==3){
						ret = o1.getEmailAddress().compareToIgnoreCase(o2.getEmailAddress());
					} else if (cuPageSort==4){
						ret = o1.getApCount()-o2.getApCount();
					} else if (cuPageSort==5){
						ret = o1.getLoginCount()-o2.getLoginCount();
					} else if (cuPageSort==6){
						ret = o1.getCurrentLoginCount()-o2.getCurrentLoginCount();
					} else if (cuPageSort==7){
						ret = o1.getTotalLoginTime()-o2.getTotalLoginTime();
					} else if (cuPageSort==8){
						ret = o1.getLoginTime()-o2.getLoginTime();
					}
					if (!cuPageSortDesc) {
						ret = ret * -1;
					}
					if (ret > 0) {
						return 1;
					} else if (ret < 0) {
						return -1;
					}
					return 0;
				}
			});
		}
		
		try {
			FileWriter out;
			StringBuffer strOutput;

			if (reportResult != null && !reportResult.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
				strOutput = new StringBuffer();
				if (profile.getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SUMMARYUSAGE)){
					strOutput.append(MgrUtil.getUserMessage("admin.user.userName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("admin.user.userFullName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("admin.user.emailAddress")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.apCount")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.loginCount")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.currentLoginCount")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.totalLoginTime")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.lastLoginTime")).append(",");
					strOutput.append(MgrUtil.getUserMessage("config.domain"));
					strOutput.append("\n");
				} else {
					strOutput.append(MgrUtil.getUserMessage("admin.user.userName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("admin.user.userFullName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("admin.user.emailAddress")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.apCount")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.sessionTime")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.plannerInfo.loginTime")).append(",");
					strOutput.append(MgrUtil.getUserMessage("config.domain"));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				if (profile.getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SUMMARYUSAGE)){
					for (int cnt = 0; cnt < reportResult.size(); cnt++) {
						AhAdminLoginSession oneSession = reportResult.get(cnt);
						strOutput.append("\"").append(oneSession.getUserName()).append("\",");
						strOutput.append("\"").append(oneSession.getUserFullName() == null ? "" : oneSession.getUserFullName()).append("\",");
						strOutput.append("\"").append(oneSession.getEmailAddress()).append("\",");
						strOutput.append("\"").append(oneSession.getApCount()).append("\",");
						strOutput.append("\"").append(oneSession.getLoginCount()).append("\",");
						strOutput.append("\"").append(oneSession.getCurrentLoginCount()).append("\",");
						strOutput.append("\"").append(oneSession.getTotalLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getLastLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getOwner().getDomainName()).append("\"");
						strOutput.append("\n");
						if (cnt % 1000 == 0 && cnt != 0) {
							out.write(strOutput.toString());
							strOutput = new StringBuffer();
						}
					}
				} else {
					for (int cnt = 0; cnt < reportResult.size(); cnt++) {
						AhAdminLoginSession oneSession = reportResult.get(cnt);
						strOutput.append("\"").append(oneSession.getUserName()).append("\",");
						strOutput.append("\"").append(oneSession.getUserFullName() == null ? "" : oneSession.getUserFullName()).append("\",");
						strOutput.append("\"").append(oneSession.getEmailAddress()).append("\",");
						strOutput.append("\"").append(oneSession.getApCount()).append("\",");
						strOutput.append("\"").append(oneSession.getTotalLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getLastLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getOwner().getDomainName()).append("\"");
						strOutput.append("\n");
						if (cnt % 1000 == 0 && cnt != 0) {
							out.write(strOutput.toString());
							strOutput = new StringBuffer();
						}
					}
				}
				out.write(strOutput.toString());
			} else {
				return false;
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientAuth():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public Calendar getReportDateTime(int reportPeriod, TimeZone timez) {
		int reportDay = 0;
		int reportMonth = 0;
		switch (reportPeriod) {
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
		Calendar calendar = Calendar.getInstance(timez);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		return calendar;
	}

	public long getReportTimeAggregation(int timeAggregation) {
		int reportHour;
		switch (timeAggregation) {
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
	
	public boolean setChannelPowerNoiseNew(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			
			long reportDateTime = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();
			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL = " (deviceType=" + HiveAp.Device_TYPE_HIVEAP +
			" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
			") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
						
			}
			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + searchAPNameSQL;
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + " AND " + searchLoactionSQL;
				}
				searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
			} else {
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + searchLoactionSQL;
					searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
				} else {
					searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
				}
			}
			searchSQL = searchSQL + " AND owner.id=" + profile.getOwner().getId();

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);

			if (!lstProfiles.isEmpty()) {
				for (Object lstProfile : lstProfiles) {
					List<AhInterfaceStats> lstInterfaceInfo = QueryUtil.executeQuery(AhInterfaceStats.class,
							new SortParams("timeStamp"),
							new FilterParams("timeStamp>=:s1 and apName=:s2",
									new Object[]{reportDateTime,
											lstProfile.toString().trim()}),
							profile.getOwner().getId());

					Map<String, List<CheckItem>> hiveap_wifi0_rec_rate_dis = new HashMap<String, List<CheckItem>>();
					List<TextItem> hiveap_wifi0_rec_rate_succ_dis = new ArrayList<TextItem>();
					Map<String, List<CheckItem>> hiveap_wifi0_trans_rate_dis = new HashMap<String, List<CheckItem>>();
					List<TextItem> hiveap_wifi0_trans_rate_succ_dis = new ArrayList<TextItem>();
					List<CheckItem> hiveap_wifi0_rec_unicast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_unicast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_rec_broadcast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_broadcast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_rec_drops = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_drops = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_rec_totalU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_totalU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_rec_retryRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_retryRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_rec_airTimeU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_trans_airTimeU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_crcErrorRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_totalChannelU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_InterferenceU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_noiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_bandsteering = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_loadbalance = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_weaksnr = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_safetynet = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_proberequest = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi0_authrequest = new ArrayList<CheckItem>();

					Map<String, List<CheckItem>> hiveap_wifi1_rec_rate_dis = new HashMap<String, List<CheckItem>>();
					List<TextItem> hiveap_wifi1_rec_rate_succ_dis = new ArrayList<TextItem>();
					Map<String, List<CheckItem>> hiveap_wifi1_trans_rate_dis = new HashMap<String, List<CheckItem>>();
					List<TextItem> hiveap_wifi1_trans_rate_succ_dis = new ArrayList<TextItem>();
					List<CheckItem> hiveap_wifi1_rec_unicast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_unicast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_rec_broadcast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_broadcast = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_rec_drops = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_drops = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_rec_totalU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_totalU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_rec_retryRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_retryRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_rec_airTimeU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_trans_airTimeU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_crcErrorRateU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_totalChannelU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_InterferenceU = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_noiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_bandsteering = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_loadbalance = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_weaksnr = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_safetynet = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_proberequest = new ArrayList<CheckItem>();
					List<CheckItem> hiveap_wifi1_authrequest = new ArrayList<CheckItem>();

					for (AhInterfaceStats oneBo : lstInterfaceInfo) {
						String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(oneBo.getTimeStamp(), tz);

						if (oneBo.getIfName().equals("wifi0")) {
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

							StringBuilder wifi0TxRateSucValue = new StringBuilder();
							if (oneBo.getTxRateInfo() != null && !oneBo.getTxRateInfo().equals("")) {
								String[] txRate = oneBo.getTxRateInfo().split(";");
								for (String rate : txRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											if (hiveap_wifi0_trans_rate_dis.get(starTime) == null) {
												List<CheckItem> tmpArray = new ArrayList<CheckItem>();
												tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
												hiveap_wifi0_trans_rate_dis.put(starTime, tmpArray);
											} else {
												hiveap_wifi0_trans_rate_dis.get(starTime).add(
														new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
											}
											wifi0TxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
													.append(oneRec[2]).append("%; ");
										}
									}
								}
							}
							hiveap_wifi0_trans_rate_succ_dis.add(new TextItem(starTime, wifi0TxRateSucValue.toString()));

							StringBuilder wifi0RxRateSucValue = new StringBuilder();
							if (oneBo.getRxRateInfo() != null && !oneBo.getRxRateInfo().equals("")) {
								String[] rxRate = oneBo.getRxRateInfo().split(";");

								for (String rate : rxRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											if (hiveap_wifi0_rec_rate_dis.get(starTime) == null) {
												List<CheckItem> tmpArray = new ArrayList<CheckItem>();
												tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
												hiveap_wifi0_rec_rate_dis.put(starTime, tmpArray);
											} else {
												hiveap_wifi0_rec_rate_dis.get(starTime).add(
														new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
											}
											wifi0RxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
													.append(oneRec[2]).append("%; ");
										}
									}
								}
							}
							hiveap_wifi0_rec_rate_succ_dis.add(new TextItem(starTime, wifi0RxRateSucValue.toString()));
						} else if (oneBo.getIfName().equals("wifi1")) {
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

							StringBuilder wifi1TxRateSucValue = new StringBuilder();
							if (oneBo.getTxRateInfo() != null && !oneBo.getTxRateInfo().equals("")) {
								String[] txRate = oneBo.getTxRateInfo().split(";");
								for (String rate : txRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											if (hiveap_wifi1_trans_rate_dis.get(starTime) == null) {
												List<CheckItem> tmpArray = new ArrayList<CheckItem>();
												tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
												hiveap_wifi1_trans_rate_dis.put(starTime, tmpArray);
											} else {
												hiveap_wifi1_trans_rate_dis.get(starTime).add(
														new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
											}
											wifi1TxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
													.append(oneRec[2]).append("%; ");
										}
									}
								}
							}
							hiveap_wifi1_trans_rate_succ_dis.add(new TextItem(starTime, wifi1TxRateSucValue.toString()));

							StringBuilder wifi1RxRateSucValue = new StringBuilder();
							if (oneBo.getRxRateInfo() != null && !oneBo.getRxRateInfo().equals("")) {
								String[] rxRate = oneBo.getRxRateInfo().split(";");
								for (String rate : rxRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											if (hiveap_wifi1_rec_rate_dis.get(starTime) == null) {
												List<CheckItem> tmpArray = new ArrayList<CheckItem>();
												tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
												hiveap_wifi1_rec_rate_dis.put(starTime, tmpArray);
											} else {
												hiveap_wifi1_rec_rate_dis.get(starTime).add(
														new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
											}
											wifi1RxRateSucValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
													.append(oneRec[2]).append("%; ");
										}
									}
								}
							}
							hiveap_wifi1_rec_rate_succ_dis.add(new TextItem(starTime, wifi1RxRateSucValue.toString()));
						}
					}

					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
						sf.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < hiveap_wifi0_rec_unicast.size(); cnt++) {
						strOutput.append(lstProfile.toString().trim()).append(",");
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

						if (hiveap_wifi0_trans_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue()) != null) {
							for (CheckItem items : hiveap_wifi0_trans_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())) {
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
										.append(items.getId()).append("%; ");
							}
						}
						strOutput.append(",");
						if (hiveap_wifi0_rec_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue()) != null) {
							for (CheckItem items : hiveap_wifi0_rec_rate_dis.get(hiveap_wifi0_rec_unicast.get(cnt).getValue())) {
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
						strOutput.append(lstProfile.toString().trim()).append(",");
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
						if (hiveap_wifi1_trans_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue()) != null) {
							for (CheckItem items : hiveap_wifi1_trans_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())) {
								strOutput.append(convertRateToM(Integer.parseInt(items.getValue()))).append("Mbps:")
										.append(items.getId()).append("%; ");
							}
						}
						strOutput.append(",");
						if (hiveap_wifi1_rec_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue()) != null) {
							for (CheckItem items : hiveap_wifi1_rec_rate_dis.get(hiveap_wifi1_rec_unicast.get(cnt).getValue())) {
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
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setChannelPowerNoiseNew():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setChannelPowerNoise(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;

			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();

			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL = " (deviceType=" + HiveAp.Device_TYPE_HIVEAP + 
			" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" or deviceType=" + HiveAp.Device_TYPE_VPN_BR +
			") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
						
			}
			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + searchAPNameSQL;
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + " AND " + searchLoactionSQL;
				}
				searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
			} else {
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + searchLoactionSQL;
					searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
				} else {
					searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
				}
			}
			searchSQL = searchSQL + " AND owner.id=" + profile.getOwner().getId();

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);

			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
					String sqlString = "select distinct ifindex, ifname from hm_xif where apname='" +
							NmsUtil.convertSqlStr(obj.toString().trim()) +
							"' and statTimeStamp >= " + reportDateTime + " and (ifname='wifi0' or ifname='wifi1')";
					List<?> lstIfIndex = QueryUtil.executeNativeQuery(sqlString);
					int wifi0IfIndex = -1;
					int wifi1IfIndex = -1;
					int wifi1_2IfIndex = -1;
					if (!lstIfIndex.isEmpty()) {
						boolean setBefore = false;
						for (Object tmpObj : lstIfIndex) {
							Object[] objIfInfo = (Object[]) tmpObj;
							if (objIfInfo[1].toString().toLowerCase().equals("wifi0")) {
								wifi0IfIndex = Integer.parseInt(objIfInfo[0].toString());
							}
							if (objIfInfo[1].toString().toLowerCase().equals("wifi1")) {
								if (!setBefore) {
									wifi1IfIndex = Integer.parseInt(objIfInfo[0].toString());
									setBefore = true;
								} else {
									wifi1_2IfIndex = Integer.parseInt(objIfInfo[0].toString());
								}
							}
						}

						String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (xifpk.ifIndex =:s3 OR xifpk.ifIndex =:s4 OR xifpk.ifIndex =:s5)";
						Object values[] = new Object[5];
						values[0] = reportDateTime;
						values[1] = obj.toString();
						values[2] = wifi0IfIndex;
						values[3] = wifi1IfIndex;
						values[4] = wifi1_2IfIndex;
						List<AhRadioAttribute> lstAttributeInfo = QueryUtil.executeQuery(AhRadioAttribute.class, new SortParams(
								"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
								.getOwner().getId());

						List<CheckItem> rcpn_wifi0_channel = new ArrayList<CheckItem>();
						List<CheckItem> rcpn_wifi1_channel = new ArrayList<CheckItem>();
						List<CheckItem> rcpn_wifi0_power = new ArrayList<CheckItem>();
						List<CheckItem> rcpn_wifi1_power = new ArrayList<CheckItem>();
						List<CheckItem> rcpn_wifi0_noise = new ArrayList<CheckItem>();
						List<CheckItem> rcpn_wifi1_noise = new ArrayList<CheckItem>();
						AhRadioAttribute tmpWifi0NeedSaveValue = new AhRadioAttribute();
						AhRadioAttribute tmpWifi1NeedSaveValue = new AhRadioAttribute();

						int tmpWifi0Count = 0;
						int tmpWifi1Count = 0;
						Calendar tmpDate = Calendar.getInstance();
						tmpDate.setTimeInMillis(reportDateTime);
						long reportTimeInMillis = reportDateTime;
						long systemTimeInLong = System.currentTimeMillis();
						while (reportTimeInMillis < systemTimeInLong) {
							for (AhRadioAttribute radioAttribute : lstAttributeInfo) {
								if (radioAttribute.getXifpk().getStatTimeValue() <= reportTimeInMillis
										- reportTimeAggregation) {
									continue;
								}
								if (radioAttribute.getXifpk().getStatTimeValue() > reportTimeInMillis) {
									break;
								} else {
									if (radioAttribute.getXifpk().getIfIndex() == wifi0IfIndex) {
										tmpWifi0NeedSaveValue.setRadioChannel(radioAttribute
												.getRadioChannel());
										tmpWifi0NeedSaveValue.setRadioTxPower(tmpWifi0NeedSaveValue
												.getRadioTxPower()
												+ radioAttribute.getRadioTxPower());
										tmpWifi0NeedSaveValue
												.setRadioNoiseFloor(tmpWifi0NeedSaveValue
														.getRadioNoiseFloor()
														+ radioAttribute.getRadioNoiseFloor());
										tmpWifi0Count++;
									} else if (radioAttribute.getXifpk().getIfIndex() == wifi1IfIndex
											|| radioAttribute.getXifpk().getIfIndex() == wifi1_2IfIndex) {
										tmpWifi1NeedSaveValue.setRadioChannel(radioAttribute
												.getRadioChannel());
										tmpWifi1NeedSaveValue.setRadioTxPower(tmpWifi1NeedSaveValue
												.getRadioTxPower()
												+ radioAttribute.getRadioTxPower());
										tmpWifi1NeedSaveValue
												.setRadioNoiseFloor(tmpWifi1NeedSaveValue
														.getRadioNoiseFloor()
														+ radioAttribute.getRadioNoiseFloor());
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

							String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
									tmpDate.getTimeInMillis(), tz);

							rcpn_wifi0_channel.add(new CheckItem(tmpWifi0NeedSaveValue
									.getRadioChannel(), tmpDateStringValue));
							rcpn_wifi1_channel.add(new CheckItem(tmpWifi1NeedSaveValue
									.getRadioChannel(), tmpDateStringValue));
							rcpn_wifi0_power.add(new CheckItem(tmpWifi0NeedSaveValue
									.getRadioTxPower()
									/ tmpWifi0Count, tmpDateStringValue));
							rcpn_wifi1_power.add(new CheckItem(tmpWifi1NeedSaveValue
									.getRadioTxPower()
									/ tmpWifi1Count, tmpDateStringValue));
							rcpn_wifi0_noise.add(new CheckItem(tmpWifi0NeedSaveValue
									.getRadioNoiseFloor()
									/ tmpWifi0Count, tmpDateStringValue));
							rcpn_wifi1_noise.add(new CheckItem(tmpWifi1NeedSaveValue
									.getRadioNoiseFloor()
									/ tmpWifi1Count, tmpDateStringValue));

							reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
							tmpDate.add(Calendar.HOUR_OF_DAY,
									(int) (reportTimeAggregation / 3600000));
							tmpWifi0NeedSaveValue = new AhRadioAttribute();
							tmpWifi1NeedSaveValue = new AhRadioAttribute();
							tmpWifi1Count = 0;
							tmpWifi0Count = 0;
						}

						StringBuffer strOutput;
						if (!createCsvfile) {
							SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
							sf.setTimeZone(tz);
							String mailFileName = profile.getReportTypeShow() + "-"
									+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
							File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
									+ File.separator + profile.getOwner().getDomainName()
									+ File.separator + mailFileName);
							out = new FileWriter(tmpFile);
							createCsvfile = true;
							strOutput = new StringBuffer();
							strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
							strOutput.append("StatTime,");
							strOutput.append("Interface Name,");
							strOutput.append("Radio Channel,");
							strOutput.append("Radio Noise Floor,");
							strOutput.append("Radio TxPower");
							strOutput.append("\n");
							out.write(strOutput.toString());
						}

						strOutput = new StringBuffer();
						for (int cnt = 0; cnt < rcpn_wifi0_channel.size(); cnt++) {
							strOutput.append(obj.toString()).append(",");
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
							strOutput.append(obj.toString()).append(",");
							strOutput.append(rcpn_wifi1_channel.get(cnt).getValue()).append(",");
							strOutput.append("wifi1" + ",");
							strOutput.append(rcpn_wifi1_channel.get(cnt).getId()).append(",");
							strOutput.append(rcpn_wifi1_noise.get(cnt).getId()).append(",");
							strOutput.append(rcpn_wifi1_power.get(cnt).getId());
							strOutput.append("\n");
						}
						out.write(strOutput.toString());
					}
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setChannelPowerNoise():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setRadioAirTime(AhReport profile, TimeZone tz) {
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL =" (deviceType=" + HiveAp.Device_TYPE_HIVEAP + 
			" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
			") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
			}
			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + searchAPNameSQL;
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + " AND " + searchLoactionSQL;
				}
				searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
			} else {
				if (!searchLoactionSQL.equals("")) {
					searchSQL = searchSQL + searchLoactionSQL;
					searchSQL = searchSQL + " AND manageStatus = " + HiveAp.STATUS_MANAGED;
				} else {
					searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
				}
			}
			searchSQL = searchSQL + " AND owner.id=" + profile.getOwner().getId();

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);

			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//					Object values[] = new Object[4];
//					values[0] = reportDateTime;
//					values[1] = obj.toString();
//					values[2] = "wifi0";
//					values[3] = "wifi1";

//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					
					String sql ="select x.ifname,v.statTimeStamp,v.radioTxAirtime,v.radioRxAirtime from hm_xif x, HM_RADIOSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime + " and v.apName ='" + NmsUtil.convertSqlStr(obj.toString()) +
							"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
							"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
					
					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
					AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();

					List<TextItem> receive_airTime = new ArrayList<TextItem>();
					List<TextItem> transmit_airTime = new ArrayList<TextItem>();

					List<TextItem> wifi1_receive_airTime = new ArrayList<TextItem>();
					List<TextItem> wifi1_transmit_airTime = new ArrayList<TextItem>();

					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();

					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//							tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
							tmpWifi0PreviousValue.setRadioTxAirtime(Double.parseDouble(oneItem[2].toString()));
							tmpWifi0PreviousValue.setRadioRxAirtime(Double.parseDouble(oneItem[3].toString()));
							break;
						}
					}
					
					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						if (oneItem[0].toString().equalsIgnoreCase("wifi1")) {
//							tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
							tmpWifi1PreviousValue.setRadioTxAirtime(Double.parseDouble(oneItem[2].toString()));
							tmpWifi1PreviousValue.setRadioRxAirtime(Double.parseDouble(oneItem[3].toString()));
							break;
						}
					}
					while (reportTimeInMillis < systemTimeInLong) {
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

						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);
						receive_airTime.add(new TextItem(df.format(tmpWifi0NeedSaveValue
								.getRadioRxAirtime() * 100 / reportTimeAggregation), tmpDateStringValue));
						transmit_airTime.add(new TextItem(df.format(tmpWifi0NeedSaveValue
								.getRadioTxAirtime() * 100 / reportTimeAggregation), tmpDateStringValue));
						wifi1_receive_airTime.add(new TextItem(df.format(tmpWifi1NeedSaveValue
								.getRadioRxAirtime() * 100 / reportTimeAggregation), tmpDateStringValue));
						wifi1_transmit_airTime.add(new TextItem(df.format(tmpWifi1NeedSaveValue
								.getRadioTxAirtime() * 100 / reportTimeAggregation), tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpWifi0NeedSaveValue = new AhRadioStats();
						tmpWifi1NeedSaveValue = new AhRadioStats();
					}

					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
						sf.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("StatTime,");
						strOutput.append("Interface Name,");
						strOutput.append("Transmitted AirTime(%),");
						strOutput.append("Received AirTime(%)");
						strOutput.append("\n");
						out.write(strOutput.toString());
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
						strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
						strOutput.append("wifi0,");
						strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
						strOutput.append(receive_airTime.get(cnt).getKey());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < wifi1_transmit_airTime.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
						strOutput.append(wifi1_transmit_airTime.get(cnt).getValue()).append(",");
						strOutput.append("wifi1,");
						strOutput.append(wifi1_transmit_airTime.get(cnt).getKey()).append(",");
						strOutput.append(wifi1_receive_airTime.get(cnt).getKey());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setRadioAirTime():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setRadioInterference(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchAPNameSQL = "";
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchSQL = searchSQL + "manageStatus = " + HiveAp.STATUS_MANAGED + " AND owner.id = "
					+ profile.getOwner().getId() + 
					" and (deviceType=" + HiveAp.Device_TYPE_HIVEAP +
					" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
					" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
					") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;

			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + "lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
			}
			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchAPNameSQL;
			}
			if (!searchLoactionSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchLoactionSQL;
			}

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
					String searchSQLSub = "timeStamp.time >= :s1 AND apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
					Object values[] = new Object[4];
					values[0] = reportDateTime;
					values[1] = obj.toString();
					values[2] = "wifi0";
					values[3] = "wifi1";

					List<AhInterferenceStats> lstInterfaceInfo = QueryUtil.executeQuery(AhInterferenceStats.class, new SortParams(
							"timeStamp.time"), new FilterParams(searchSQLSub, values), profile
							.getOwner().getId());

					List<CheckItem> wifi0_averageTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_averageRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_averageInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_averageNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_shortTermTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_shortTermRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_shortTermInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_shortTermNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_snapShotTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_snapShotRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_snapShotInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_snapShotNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi0_crcError = new ArrayList<CheckItem>();

					List<CheckItem> wifi1_averageTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_averageRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_averageInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_averageNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_shortTermTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_shortTermRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_shortTermInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_shortTermNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_snapShotTxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_snapShotRxCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_snapShotInterferenceCu = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_snapShotNoiseFloor = new ArrayList<CheckItem>();
					List<CheckItem> wifi1_crcError = new ArrayList<CheckItem>();

					for (AhInterferenceStats ifStats : lstInterfaceInfo) {
						if (ifStats.getIfName().equalsIgnoreCase("wifi0")) {
							// wifi0_averageTxCu
							String tmpDateStringValue = AhDateTimeUtil
									.getSpecifyDateTimeReport(ifStats
											.getTimeStamp().getTime(), tz);
							wifi0_averageTxCu.add(new CheckItem((long) ifStats
									.getAverageTXCU(), tmpDateStringValue));
							wifi0_averageRxCu.add(new CheckItem((long) ifStats
									.getAverageRXCU(), tmpDateStringValue));
							wifi0_averageInterferenceCu.add(new CheckItem(
									(long) ifStats.getAverageInterferenceCU(),
									tmpDateStringValue));
							wifi0_averageNoiseFloor.add(new CheckItem(
									(long) ifStats.getAverageNoiseFloor(),
									tmpDateStringValue));
							wifi0_shortTermTxCu.add(new CheckItem(
									(long) ifStats.getShortTermTXCU(),
									tmpDateStringValue));
							wifi0_shortTermRxCu.add(new CheckItem(
									(long) ifStats.getShortTermRXCU(),
									tmpDateStringValue));
							wifi0_shortTermInterferenceCu
									.add(new CheckItem((long) ifStats
											.getShortTermInterferenceCU(),
											tmpDateStringValue));
							wifi0_shortTermNoiseFloor.add(new CheckItem(
									(long) ifStats.getShortTermNoiseFloor(),
									tmpDateStringValue));
							wifi0_snapShotTxCu.add(new CheckItem((long) ifStats
									.getSnapShotTXCU(), tmpDateStringValue));
							wifi0_snapShotRxCu.add(new CheckItem((long) ifStats
									.getSnapShotRXCU(), tmpDateStringValue));
							wifi0_snapShotInterferenceCu.add(new CheckItem(
									(long) ifStats.getSnapShotInterferenceCU(),
									tmpDateStringValue));
							wifi0_snapShotNoiseFloor.add(new CheckItem(
									(long) ifStats.getSnapShotNoiseFloor(),
									tmpDateStringValue));
							wifi0_crcError.add(new CheckItem((long) ifStats
									.getCrcError(), tmpDateStringValue));
						} else if (ifStats.getIfName()
								.equalsIgnoreCase("wifi1")) {
							// wifi1_averageTxCu
							String tmpDateStringValue = AhDateTimeUtil
									.getSpecifyDateTimeReport(ifStats
											.getTimeStamp().getTime(), tz);
							wifi1_averageTxCu.add(new CheckItem((long) ifStats
									.getAverageTXCU(), tmpDateStringValue));
							wifi1_averageRxCu.add(new CheckItem((long) ifStats
									.getAverageRXCU(), tmpDateStringValue));
							wifi1_averageInterferenceCu.add(new CheckItem(
									(long) ifStats.getAverageInterferenceCU(),
									tmpDateStringValue));
							wifi1_averageNoiseFloor.add(new CheckItem(
									(long) ifStats.getAverageNoiseFloor(),
									tmpDateStringValue));
							wifi1_shortTermTxCu.add(new CheckItem(
									(long) ifStats.getShortTermTXCU(),
									tmpDateStringValue));
							wifi1_shortTermRxCu.add(new CheckItem(
									(long) ifStats.getShortTermRXCU(),
									tmpDateStringValue));
							wifi1_shortTermInterferenceCu
									.add(new CheckItem((long) ifStats
											.getShortTermInterferenceCU(),
											tmpDateStringValue));
							wifi1_shortTermNoiseFloor.add(new CheckItem(
									(long) ifStats.getShortTermNoiseFloor(),
									tmpDateStringValue));
							wifi1_snapShotTxCu.add(new CheckItem((long) ifStats
									.getSnapShotTXCU(), tmpDateStringValue));
							wifi1_snapShotRxCu.add(new CheckItem((long) ifStats
									.getSnapShotRXCU(), tmpDateStringValue));
							wifi1_snapShotInterferenceCu.add(new CheckItem(
									(long) ifStats.getSnapShotInterferenceCU(),
									tmpDateStringValue));
							wifi1_snapShotNoiseFloor.add(new CheckItem(
									(long) ifStats.getSnapShotNoiseFloor(),
									tmpDateStringValue));
							wifi1_crcError.add(new CheckItem((long) ifStats
									.getCrcError(), tmpDateStringValue));
						}
					}

					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
						sf.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < wifi0_averageTxCu.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
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
						strOutput.append(obj.toString()).append(",");
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
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setRadioInterference():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setRadioTrafficMetrics(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL = " (deviceType=" + HiveAp.Device_TYPE_HIVEAP +
			" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
			") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED
			+ " AND owner.id = " + profile.getOwner().getId();
			
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchAPNameSQL;
			}
			if (!searchLoactionSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchLoactionSQL;
			}

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//					if (profile.getRole() == AhReport.REPORT_ROLE_ACCESS) {
//						searchSQLSub = searchSQLSub + " AND ifMode=" + AhReport.REPORT_ROLE_ACCESS;
//					} else if (profile.getRole() == AhReport.REPORT_ROLE_BACKHAUL) {
//						searchSQLSub = searchSQLSub + " AND ifMode="
//								+ AhReport.REPORT_ROLE_BACKHAUL;
//					}
//
//					Object values[] = new Object[4];
//					values[0] = reportDateTime;
//					values[1] = obj.toString();
//					values[2] = "wifi0";
//					values[3] = "wifi1";
//
//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					
					String sql ="select x.ifname,v.statTimeStamp,v.radioTxDataFrames,v.radioTxBeDataFrames," +
							" v.radioTxBgDataFrames,v.radioTxViDataFrames,v.radioTxVoDataFrames,v.radioTxUnicastDataFrames," +
							" v.radioTxMulticastDataFrames,v.radioTxBroadcastDataFrames,v.radioTxNonBeaconMgtFrames,v.radioTxBeaconFrames," +
							" v.radioRxTotalDataFrames,v.radioRxUnicastDataFrames,v.radioRxMulticastDataFrames,v.radioRxBroadcastDataFrames," +
							" v.radioRxMgtFrames from hm_xif x, HM_RADIOSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime+ " and v.apName ='" + NmsUtil.convertSqlStr(obj.toString()) +
							"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
							"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
					
					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
					AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();

					List<CheckItem> rtm_trans_wifi0_totalData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_beData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_bgData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_viData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_voData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_nonBeaconMgtData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi0_beaconData = new ArrayList<CheckItem>();

					List<CheckItem> rtm_rec_wifi0_totalData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi0_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi0_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi0_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi0_mgtData = new ArrayList<CheckItem>();

					List<CheckItem> rtm_trans_wifi1_totalData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_beData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_bgData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_viData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_voData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_nonBeaconMgtData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_trans_wifi1_beaconData = new ArrayList<CheckItem>();

					List<CheckItem> rtm_rec_wifi1_totalData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi1_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi1_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi1_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> rtm_rec_wifi1_mgtData = new ArrayList<CheckItem>();

					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//							tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
//							tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();
					while (reportTimeInMillis < systemTimeInLong) {
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

						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);
						rtm_trans_wifi0_totalData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi0_beData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioTxBeDataFrames(), tmpDateStringValue));
						rtm_trans_wifi0_bgData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioTxBgDataFrames(), tmpDateStringValue));
						rtm_trans_wifi0_viData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioTxViDataFrames(), tmpDateStringValue));
						rtm_trans_wifi0_voData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioTxVoDataFrames(), tmpDateStringValue));
						rtm_trans_wifi0_unicastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxUnicastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi0_multicastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxMulticastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi0_broadcastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxBroadcastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi0_nonBeaconMgtData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxNonBeaconMgtFrames(),
								tmpDateStringValue));
						rtm_trans_wifi0_beaconData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxBeaconFrames(),
								tmpDateStringValue));
						rtm_rec_wifi0_totalData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioRxTotalDataFrames(), tmpDateStringValue));
						rtm_rec_wifi0_unicastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioRxUnicastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi0_multicastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioRxMulticastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi0_broadcastData.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioRxBroadcastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi0_mgtData.add(new CheckItem(tmpWifi0NeedSaveValue
								.getRadioRxMgtFrames(), tmpDateStringValue));

						rtm_trans_wifi1_totalData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi1_beData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioTxBeDataFrames(), tmpDateStringValue));
						rtm_trans_wifi1_bgData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioTxBgDataFrames(), tmpDateStringValue));
						rtm_trans_wifi1_viData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioTxViDataFrames(), tmpDateStringValue));
						rtm_trans_wifi1_voData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioTxVoDataFrames(), tmpDateStringValue));
						rtm_trans_wifi1_unicastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxUnicastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi1_multicastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxMulticastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi1_broadcastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxBroadcastDataFrames(),
								tmpDateStringValue));
						rtm_trans_wifi1_nonBeaconMgtData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxNonBeaconMgtFrames(),
								tmpDateStringValue));
						rtm_trans_wifi1_beaconData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxBeaconFrames(),
								tmpDateStringValue));
						rtm_rec_wifi1_totalData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioRxTotalDataFrames(), tmpDateStringValue));
						rtm_rec_wifi1_unicastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioRxUnicastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi1_multicastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioRxMulticastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi1_broadcastData.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioRxBroadcastDataFrames(),
								tmpDateStringValue));
						rtm_rec_wifi1_mgtData.add(new CheckItem(tmpWifi1NeedSaveValue
								.getRadioRxMgtFrames(), tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpWifi0NeedSaveValue = new AhRadioStats();
						tmpWifi1NeedSaveValue = new AhRadioStats();
					}
					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
						sf.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < rtm_trans_wifi0_totalData.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
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
						strOutput.append(obj.toString()).append(",");
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
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setRadioTrafficMetrics():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setRadioTroubleShooting(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchAPNameSQL;
			String searchLoactionSQL = "";
			String searchSQL = "select hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchAPNameSQL = " (deviceType=" + HiveAp.Device_TYPE_HIVEAP +
			" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
			") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;
			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchAPNameSQL = searchAPNameSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
			}
			if (profile.getLocation() != null) {
				searchLoactionSQL = searchLoactionSQL + "mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED
			+ " AND owner.id = " + profile.getOwner().getId();
			
			if (!searchAPNameSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchAPNameSQL;
			}
			if (!searchLoactionSQL.equals("")) {
				searchSQL = searchSQL + " AND " + searchLoactionSQL;
			}

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND (lower(ifName) =:s3 OR lower(ifName) =:s4)";
//					if (profile.getRole() == AhReport.REPORT_ROLE_ACCESS) {
//						searchSQLSub = searchSQLSub + " AND ifMode=" + AhReport.REPORT_ROLE_ACCESS;
//					} else if (profile.getRole() == AhReport.REPORT_ROLE_BACKHAUL) {
//						searchSQLSub = searchSQLSub + " AND ifMode="
//								+ AhReport.REPORT_ROLE_BACKHAUL;
//					}
//
//					Object values[] = new Object[4];
//					values[0] = reportDateTime;
//					values[1] = obj.toString();
//					values[2] = "wifi0";
//					values[3] = "wifi1";
//
//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					
					String sql ="select x.ifname,v.statTimeStamp,v.radioTxTotalRetries,v.radioTxTotalFramesDropped," +
							"v.radioTxTotalFrameErrors,v.radioTxFEForExcessiveHWRetries," +
							"v.radioTXRTSFailures,v.radioRxTotalFrameDropped" +
							" from hm_xif x, HM_RADIOSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime + " and v.apName ='" + NmsUtil.convertSqlStr(obj.toString()) +
							"' AND (lower(x.ifName) ='wifi0' OR lower(x.ifName) ='wifi1') " +
							"  and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
					
					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhRadioStats tmpWifi0PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi1PreviousValue = new AhRadioStats();
					AhRadioStats tmpWifi0NeedSaveValue = new AhRadioStats();
					AhRadioStats tmpWifi1NeedSaveValue = new AhRadioStats();
					List<CheckItem> rts_trans_wifi0_totalRetries = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi1_totalRetries = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi0_totalFramesDropped = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi1_totalFramesDropped = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi0_totalFrameErrors = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi1_totalFrameErrors = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi0_feForExcessiveHWRetries = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi1_feForExcessiveHWRetries = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi0_rtsFailures = new ArrayList<CheckItem>();
					List<CheckItem> rts_trans_wifi1_rtsFailures = new ArrayList<CheckItem>();
					List<CheckItem> rts_rec_wifi0_totalFrameDropped = new ArrayList<CheckItem>();
					List<CheckItem> rts_rec_wifi1_totalFrameDropped = new ArrayList<CheckItem>();

					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();
					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						if (oneItem[0].toString().equalsIgnoreCase("wifi0")) {
//							tmpWifi0PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
//							tmpWifi1PreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
							tmpWifi1PreviousValue.setRadioTxTotalRetries(Long.parseLong(oneItem[2].toString()));
							tmpWifi1PreviousValue.setRadioTxTotalFramesDropped(Long.parseLong(oneItem[3].toString()));
							tmpWifi1PreviousValue.setRadioTxTotalFrameErrors(Long.parseLong(oneItem[4].toString()));
							tmpWifi1PreviousValue.setRadioTxFEForExcessiveHWRetries(Long.parseLong(oneItem[5].toString()));
							tmpWifi1PreviousValue.setRadioTXRTSFailures(Long.parseLong(oneItem[6].toString()));
							tmpWifi1PreviousValue.setRadioRxTotalFrameDropped(Long.parseLong(oneItem[7].toString()));
							break;
						}
					}
					while (reportTimeInMillis < systemTimeInLong) {
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
						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);
						rts_trans_wifi0_totalRetries.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxTotalRetries(),
								tmpDateStringValue));
						rts_trans_wifi1_totalRetries.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxTotalRetries(),
								tmpDateStringValue));
						rts_trans_wifi0_totalFramesDropped.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxTotalFramesDropped(),
								tmpDateStringValue));
						rts_trans_wifi1_totalFramesDropped.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxTotalFramesDropped(),
								tmpDateStringValue));
						rts_trans_wifi0_totalFrameErrors.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTxTotalFrameErrors(),
								tmpDateStringValue));
						rts_trans_wifi1_totalFrameErrors.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTxTotalFrameErrors(),
								tmpDateStringValue));
						rts_trans_wifi0_feForExcessiveHWRetries.add(new CheckItem(
								tmpWifi0NeedSaveValue
										.getRadioTxFEForExcessiveHWRetries(),
								tmpDateStringValue));
						rts_trans_wifi1_feForExcessiveHWRetries.add(new CheckItem(
								tmpWifi1NeedSaveValue
										.getRadioTxFEForExcessiveHWRetries(),
								tmpDateStringValue));
						rts_trans_wifi0_rtsFailures.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioTXRTSFailures(),
								tmpDateStringValue));
						rts_trans_wifi1_rtsFailures.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioTXRTSFailures(),
								tmpDateStringValue));
						rts_rec_wifi0_totalFrameDropped.add(new CheckItem(
								tmpWifi0NeedSaveValue.getRadioRxTotalFrameDropped(),
								tmpDateStringValue));
						rts_rec_wifi1_totalFrameDropped.add(new CheckItem(
								tmpWifi1NeedSaveValue.getRadioRxTotalFrameDropped(),
								tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpWifi0NeedSaveValue = new AhRadioStats();
						tmpWifi1NeedSaveValue = new AhRadioStats();
					}
					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
						sf.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sf.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < rts_trans_wifi0_totalRetries.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
						strOutput.append(rts_trans_wifi0_totalRetries.get(cnt).getValue()).append(",");
						strOutput.append("wifi0,");
						strOutput.append(rts_trans_wifi0_totalRetries.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi0_totalFramesDropped.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi0_totalFrameErrors.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi0_feForExcessiveHWRetries.get(cnt)
								.getId()).append(",");
						strOutput.append(rts_trans_wifi0_rtsFailures.get(cnt).getId()).append(",");
						strOutput.append(rts_rec_wifi0_totalFrameDropped.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < rts_trans_wifi1_totalRetries.size(); cnt++) {
						strOutput.append(obj.toString()).append(",");
						strOutput.append(rts_trans_wifi1_totalRetries.get(cnt).getValue()).append(",");
						strOutput.append("wifi1,");
						strOutput.append(rts_trans_wifi1_totalRetries.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi1_totalFramesDropped.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi1_totalFrameErrors.get(cnt).getId()).append(",");
						strOutput.append(rts_trans_wifi1_feForExcessiveHWRetries.get(cnt)
								.getId()).append(",");
						strOutput.append(rts_trans_wifi1_rtsFailures.get(cnt).getId()).append(",");
						strOutput.append(rts_rec_wifi1_totalFrameDropped.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
				if (out != null) {
					out.flush();
					out.close();
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setRadioTroubleShooting():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSsidAirTime(AhReport profile, TimeZone tz) {
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchSQL = "select DISTINCT xifpk.apName, ssidName from " + AhXIf.class.getSimpleName() + " where "
					+ " xifpk.statTimeStamp >=" + reportDateTime
					+ " AND lower(xifpk.apName) like '%" + profile.getApNameForSQL().toLowerCase()
					+ "%'" + " AND lower(ssidName) like '%"
					+ profile.getSsidNameForSQL().toLowerCase() + "%'" + " AND owner.id = "
					+ profile.getOwner().getId();

			List<?> subProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstHiveAPName = new TreeSet<String>();
			Map<String, List<String>> ssidNameMap = new HashMap<String, List<String>>();

			if (!subProfiles.isEmpty()) {
				for (Object obj : subProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[1] != null && !tmp[1].toString().equalsIgnoreCase("N/A") && !tmp[1].toString().equals("")) {
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
			} else {
				return false;
			}

			for (String reportAPName : lstHiveAPName) {
				if (ssidNameMap.get(reportAPName) == null) {
					continue;
				}
				for (int i = 0; i < ssidNameMap.get(reportAPName).size(); i++) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//					Object values[] = new Object[3];
//					values[0] = reportDateTime;
//					values[1] = reportAPName;
//					values[2] = ssidNameMap.get(reportAPName).get(i);
//
//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					
					String sql ="select x.ifname,v.statTimeStamp,v.txVifAirtime,v.rxVifAirtime from hm_xif x, HM_VIFSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(ssidNameMap.get(reportAPName).get(i)) +
							"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";

					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhVIfStats tmpPreviousValue = new AhVIfStats();
					AhVIfStats tmpPreviousValueSec = new AhVIfStats();
					AhVIfStats tmpNeedSaveValue = new AhVIfStats();

					List<TextItem> transmit_airTime = new ArrayList<TextItem>();
					List<TextItem> receive_airTime = new ArrayList<TextItem>();

					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();
					boolean firstSave= false;
					String firstIfName= "";
					String secondIfName= "";
					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						
						if (!firstSave) {
//							tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
							tmpPreviousValue.setTxVifAirtime(Double.parseDouble(oneItem[2].toString()));
							tmpPreviousValue.setRxVifAirtime(Double.parseDouble(oneItem[3].toString()));
							firstSave = true;
							firstIfName = oneItem[0].toString();
							continue;
						}
						if (firstSave && secondIfName.equals("")) {
							if (!oneItem[0].toString().equals(firstIfName)) {
//								tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
								tmpPreviousValueSec.setTxVifAirtime(Double.parseDouble(oneItem[2].toString()));
								tmpPreviousValueSec.setRxVifAirtime(Double.parseDouble(oneItem[3].toString()));
								secondIfName = oneItem[0].toString();
								break;
							}
						}
					}
					while (reportTimeInMillis < systemTimeInLong) {
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

						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);
						transmit_airTime.add(new TextItem(df.format(tmpNeedSaveValue
								.getTxVifAirtime()*100/reportTimeAggregation), tmpDateStringValue));
						receive_airTime.add(new TextItem(df.format(tmpNeedSaveValue
								.getRxVifAirtime()*100/reportTimeAggregation), tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpNeedSaveValue = new AhVIfStats();
					}
					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
						sfname.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("SSID Name,");
						strOutput.append("StatTime,");
						strOutput.append("Transmitted AirTime(%),");
						strOutput.append("Received AirTime(%)");
						strOutput.append("\n");
						out.write(strOutput.toString());
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(ssidNameMap.get(reportAPName).get(i)).append(",");
						strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
						strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
						strOutput.append(receive_airTime.get(cnt).getKey());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setSsidAirTime():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSsidTrafficMetrics(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchSQL = "select DISTINCT xifpk.apName, ssidName from " + AhXIf.class.getSimpleName() + " where "
					+ " xifpk.statTimeStamp >=" + reportDateTime
					+ " AND lower(xifpk.apName) like '%" + profile.getApNameForSQL().toLowerCase()
					+ "%'" + " AND lower(ssidName) like '%"
					+ profile.getSsidNameForSQL().toLowerCase() + "%'" + " AND owner.id = "
					+ profile.getOwner().getId();

			List<?> subProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstHiveAPName = new TreeSet<String>();
			Map<String, List<String>> ssidNameMap = new HashMap<String, List<String>>();

			if (!subProfiles.isEmpty()) {
				for (Object obj : subProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[1] != null && !tmp[1].toString().equalsIgnoreCase("N/A") && !tmp[1].toString().equals("")) {
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
			} else {
				return false;
			}

			for (String reportAPName : lstHiveAPName) {
				if (ssidNameMap.get(reportAPName) == null) {
					continue;
				}
				for (int k = 0; k < ssidNameMap.get(reportAPName).size(); k++) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//					Object values[] = new Object[3];
//					values[0] = reportDateTime;
//					values[1] = reportAPName;
//					values[2] = ssidNameMap.get(reportAPName).get(k);
//
//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					String sql ="select x.ifname,v.statTimeStamp,v.txVIfDataFrames,v.txVIfBeDataFrames,v.txVIfBgDataFrames," +
							"v.txVIfViDataFrames,v.txVIfVoDataFrames,v.txVIfUnicastDataFrames,v.txVIfMulticastDataFrames," +
							"v.txVIfBroadcastDataFrames,v.rxVIfDataFrames,v.rxVIfUnicastDataFrames,v.rxVIfMulticastDataFrames," +
							"v.rxVIfBroadcastDataFrames from hm_xif x, HM_VIFSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(ssidNameMap.get(reportAPName).get(k)) +
							"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";

					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhVIfStats tmpPreviousValue = new AhVIfStats();
					AhVIfStats tmpPreviousValueSec = new AhVIfStats();
					AhVIfStats tmpNeedSaveValue = new AhVIfStats();
					List<CheckItem> stm_trans_totalData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_beData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_bgData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_viData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_voData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> stm_trans_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> stm_rec_totalData = new ArrayList<CheckItem>();
					List<CheckItem> stm_rec_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> stm_rec_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> stm_rec_broadcastData = new ArrayList<CheckItem>();

					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();
					boolean firstSave= false;
					String firstIfName= "";
					String secondIfName= "";
					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						
						if (!firstSave) {
//							tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
//								tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
					while (reportTimeInMillis < systemTimeInLong) {
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
						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);

						stm_trans_totalData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfDataFrames(), tmpDateStringValue));
						stm_trans_beData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfBeDataFrames(), tmpDateStringValue));
						stm_trans_bgData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfBgDataFrames(), tmpDateStringValue));
						stm_trans_viData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfViDataFrames(), tmpDateStringValue));
						stm_trans_voData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfVoDataFrames(), tmpDateStringValue));
						stm_trans_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfUnicastDataFrames(), tmpDateStringValue));
						stm_trans_multicastData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfMulticastDataFrames(), tmpDateStringValue));
						stm_trans_broadcastData.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfBroadcastDataFrames(), tmpDateStringValue));
						stm_rec_totalData.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfDataFrames(), tmpDateStringValue));
						stm_rec_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfUnicastDataFrames(), tmpDateStringValue));
						stm_rec_multicastData.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfMulticastDataFrames(), tmpDateStringValue));
						stm_rec_broadcastData.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfBroadcastDataFrames(), tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpNeedSaveValue = new AhVIfStats();
					}

					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
						sfname.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < stm_trans_totalData.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(ssidNameMap.get(reportAPName).get(k)).append(",");
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
				}
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setSsidTrafficMetrics():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSsidTroubleShooting(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();

			String searchSQL = "select DISTINCT xifpk.apName, ssidName from " + AhXIf.class.getSimpleName() + " where "
					+ " xifpk.statTimeStamp >=" + reportDateTime
					+ " AND lower(xifpk.apName) like '%" + profile.getApNameForSQL().toLowerCase()
					+ "%'" + " AND lower(ssidName) like '%"
					+ profile.getSsidNameForSQL().toLowerCase() + "%'" + " AND owner.id = "
					+ profile.getOwner().getId();

			List<?> subProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstHiveAPName = new TreeSet<String>();
			Map<String, List<String>> ssidNameMap = new HashMap<String, List<String>>();

			if (!subProfiles.isEmpty()) {
				for (Object obj : subProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[1] != null && !tmp[1].toString().equalsIgnoreCase("N/A")  && !tmp[1].toString().equals("")) {
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
			} else {
				return false;
			}

			for (String reportAPName : lstHiveAPName) {
				if (ssidNameMap.get(reportAPName) == null) {
					continue;
				}
				for (int k = 0; k < ssidNameMap.get(reportAPName).size(); k++) {
//					String searchSQLSub = "xifpk.statTimeStamp >= :s1 AND xifpk.apName =:s2 AND ssidName=:s3";
//
//					Object values[] = new Object[3];
//					values[0] = reportDateTime;
//					values[1] = reportAPName;
//					values[2] = ssidNameMap.get(reportAPName).get(k);
//
//					List<AhXIf> lstInterfaceInfo = QueryUtil.executeQuery(AhXIf.class, new SortParams(
//							"xifpk.statTimeStamp"), new FilterParams(searchSQLSub, values), profile
//							.getOwner().getId());
					String sql ="select x.ifname,v.statTimeStamp,v.txVIfDroppedFrames,v.rxVIfDroppedFrames," +
							"v.txVIfErrorFrames,v.rxVIfErrorFrames from hm_xif x, HM_VIFSTATS v where " +
							"x.owner=" + profile.getOwner().getId() + "and v.owner=" + profile.getOwner().getId() +
							" and v.statTimeStamp >= " + reportDateTime + " and v.apName ='" + NmsUtil.convertSqlStr(reportAPName) + "' AND x.ssidName='" +NmsUtil.convertSqlStr(ssidNameMap.get(reportAPName).get(k)) +
							"' and x.apName=v.apName and x.ifindex=v.ifindex and x.statTimeStamp=v.statTimeStamp order by v.statTimeStamp";
					List<?> lstInterfaceInfo = QueryUtil.executeNativeQuery(sql);

					AhVIfStats tmpPreviousValue = new AhVIfStats();
					AhVIfStats tmpPreviousValueSec = new AhVIfStats();
					AhVIfStats tmpNeedSaveValue = new AhVIfStats();
					List<CheckItem> sts_trans_totalFramesDropped = new ArrayList<CheckItem>();
					List<CheckItem> sts_trans_totalFrameErrors = new ArrayList<CheckItem>();
					List<CheckItem> sts_rec_totalFramesDropped = new ArrayList<CheckItem>();
					List<CheckItem> sts_rec_totalFrameErrors = new ArrayList<CheckItem>();

					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();

					boolean firstSave= false;
					String firstIfName= "";
					String secondIfName= "";
					for (Object oneObj : lstInterfaceInfo) {
						Object[] oneItem = (Object[])oneObj;
						
						if (!firstSave) {
//							tmpPreviousValue.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
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
//								tmpPreviousValueSec.getXifpk().setStatTime(Long.parseLong(oneItem[1].toString()));
								tmpPreviousValueSec.setTxVIfDroppedFrames(Long.parseLong(oneItem[2].toString()));
								tmpPreviousValueSec.setRxVIfDroppedFrames(Long.parseLong(oneItem[3].toString()));
								tmpPreviousValueSec.setTxVIfErrorFrames(Long.parseLong(oneItem[4].toString()));
								tmpPreviousValueSec.setRxVIfErrorFrames(Long.parseLong(oneItem[5].toString()));
								secondIfName = oneItem[0].toString();
								break;
							}
						}
					}
					while (reportTimeInMillis < systemTimeInLong) {
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
						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);

						sts_trans_totalFramesDropped.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfDroppedFrames(), tmpDateStringValue));
						sts_trans_totalFrameErrors.add(new CheckItem(tmpNeedSaveValue
								.getTxVIfErrorFrames(), tmpDateStringValue));
						sts_rec_totalFramesDropped.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfDroppedFrames(), tmpDateStringValue));
						sts_rec_totalFrameErrors.add(new CheckItem(tmpNeedSaveValue
								.getRxVIfErrorFrames(), tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpNeedSaveValue = new AhVIfStats();
					}
					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
						sfname.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("SSID Name,");
						strOutput.append("StatTime,");
						strOutput.append("Transmitted Total Frames Dropped by Radio,");
						strOutput.append("Transmitted Total Frames Dropped by SW,");
						strOutput.append("Received Total Frames Dropped by Radio,");
						strOutput.append("Received Total Frames Dropped by SW");
						strOutput.append("\n");
						out.write(strOutput.toString());
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < sts_trans_totalFramesDropped.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(ssidNameMap.get(reportAPName).get(k)).append(",");
						strOutput.append(sts_trans_totalFramesDropped.get(cnt).getValue()).append(",");
						strOutput.append(sts_trans_totalFrameErrors.get(cnt).getId()).append(",");
						strOutput.append(sts_trans_totalFramesDropped.get(cnt).getId()).append(",");
						strOutput.append(sts_rec_totalFrameErrors.get(cnt).getId()).append(",");
						strOutput.append(sts_rec_totalFramesDropped.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setSsidTroubleShooting():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setMostClientsAPs(AhReport profile, TimeZone tz) {
		try {
			FileWriter out;
			Calendar firstReportTime = getReportDateTime(profile.getReportPeriod(), tz);
			int reportTimeAggregation = (int) getReportTimeAggregation(profile.getTimeAggregation()) / 3600000;
			long systemTimeInMillis = System.currentTimeMillis();

			Calendar reportTime = Calendar.getInstance();
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());

			FilterParams filterParams;
			if (profile.getLocation() != null) {
				filterParams = new FilterParams(
						"bo.endTimeStamp>=:s1 and bo.mapId=:s2",
						new Object[] { reportTime.getTimeInMillis(), profile.getLocation().getId() });
			} else {
				filterParams = new FilterParams("bo.endTimeStamp>=:s1", new Object[] { reportTime
						.getTimeInMillis() });
			}
			List<?> subProfiles = QueryUtil.executeQuery(
					"select bo.apName, bo.endTimeStamp, bo.startTimeStamp from "
							+ AhClientSessionHistory.class.getSimpleName() + " bo", new SortParams(
							"endTimeStamp"), filterParams, profile.getOwner().getId());
			Map<String, AhClientCountForAP> clientCount = new HashMap<String, AhClientCountForAP>();

			for (Object obj : subProfiles) {
				reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
				Object[] tmp = (Object[]) obj;
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
//			if (profile.getLocation() != null) {
//				currentFilterParams = new FilterParams("bo.mapId=:s1", new Object[] { profile
//						.getLocation().getId() });
//			} else {
//				currentFilterParams = null;
//			}
//			List<?> currentProfiles = QueryUtil.executeQuery(
//					"select bo.apName, bo.startTimeStamp from "
//							+ AhClientSession.class.getSimpleName() + " bo", new SortParams(
//							"startTimeStamp"), currentFilterParams, profile.getOwner().getId());
			
			if (profile.getLocation() != null) {
				currentFilterParams = new FilterParams("mapId=?", new Object[] { profile
						.getLocation().getId() });
			} else {
				currentFilterParams = null;
			}
			List<?> currentProfiles = DBOperationUtil.executeQuery(
					"select apName, startTimeStamp from ah_clientsesssion", new SortParams(
							"startTimeStamp"), currentFilterParams, profile.getOwner().getId());
			
			for (Object obj : currentProfiles) {
				Object[] tmp = (Object[]) obj;
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
					int diff = new Long((reportTime1 - reportTime2) / 100000).intValue();
					if (diff == 0) {
						diff = o2.getClientCount() - o1.getClientCount();
						if (diff == 0) {
							diff = o1.getApName().compareTo(o2.getApName());
						}
					}
					return diff;
				}
			});

			List<List<AhClientCountForAP>> fiveMaxClientCount = new ArrayList<List<AhClientCountForAP>>();
			long comparatorDate = 0;
			int tmpCount = 0;
			List<AhClientCountForAP> tmpList = new ArrayList<AhClientCountForAP>();
			for (int i = 0; i < lstClientCount.size(); i++) {
				if (i == 0 || comparatorDate == lstClientCount.get(i).getReportTime()) {
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
			if (!tmpList.isEmpty()) {
				fiveMaxClientCount.add(tmpList);
			}

			StringBuffer strOutput;
			if (!fiveMaxClientCount.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);

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
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setMostClientsAPs():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setClientSessionNew(AhReport profile, TimeZone tz) {
		try {
			FileWriter out;
			long reportDateTime = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();

			String searchSQL = "select distinct clientMac from " + AhClientSessionHistory.class.getSimpleName() + " where "
					+ " endTimeStamp >="
					+ reportDateTime
					+ " AND lower(apName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'";

			if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ profile.getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ profile.getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ profile.getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ profile.getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = " + profile.getLocation().getId();
			}

			searchSQL = searchSQL + " AND owner.id = " + profile.getOwner().getId();

			List<?> mapProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			
			if (!mapProfiles.isEmpty()){
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				
				String mailFileName = profile.getReportTypeShow() + "-"
						+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
						+ File.separator + profile.getOwner().getDomainName()
						+ File.separator + mailFileName);
				out = new FileWriter(tmpFile);
				
				for (Object obj:mapProfiles){
					List<AhClientSessionHistory> lstSessionInfo = QueryUtil.executeQuery(AhClientSessionHistory.class,
							new SortParams("startTimeStamp"), 
							new FilterParams("clientMac=:s1 and endTimeStamp>=:s2", 
								new Object[]{obj.toString(),reportDateTime}),
							profile.getOwner().getId());
					
					StringBuffer strOutput;
					if (!lstSessionInfo.isEmpty()) {
						strOutput = new StringBuffer();
						strOutput.append("Client MAC Address,");
						strOutput.append("Client IP Address,");
						strOutput.append("Client HostName,");
						strOutput.append("Client UserName,");
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("Start Time,");
						strOutput.append("End Time,");
						strOutput.append("Client SSID,");
						strOutput.append("Client VLAN,");
						strOutput.append("Client User Profile Attribute,");
						strOutput.append("Client Channel,");
						strOutput.append("Client Auth Method,");
						strOutput.append("Client Encryption Method,");
						strOutput.append("Client Physical Mode,");
						strOutput.append("Client CWP Used,");
						strOutput.append("Client Link Uptime,");
						strOutput.append("Client Associated BSSID");
						strOutput.append("\n");
						out.write(strOutput.toString());
						
						strOutput = new StringBuffer();
						for (AhClientSessionHistory ahHistory : lstSessionInfo) {
							strOutput.append(ahHistory.getClientMac()).append(",");
							strOutput.append(ahHistory.getClientIP()).append(",");
							strOutput.append(ahHistory.getClientHostname()).append(",");
							strOutput.append(ahHistory.getClientUsername()).append(",");
							strOutput.append(ahHistory.getApName()).append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(ahHistory.getStartTimeStamp(), tz)).append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(ahHistory.getEndTimeStamp(), tz)).append(",");
							strOutput.append(ahHistory.getClientSSID()).append(",");
							strOutput.append(ahHistory.getClientVLANString()).append(",");
							strOutput.append(ahHistory.getClientUserProfId4Show()).append(",");
							strOutput.append(ahHistory.getClientChannelString()).append(",");
							strOutput.append(ahHistory.getClientAuthMethodString()).append(",");
							strOutput.append(ahHistory.getClientEncryptionMethodString()).append(",");
							strOutput.append(ahHistory.getClientMacPtlString()).append(",");
							strOutput.append(ahHistory.getClientCWPUsedString()).append(",");
							strOutput.append("\"").append(NmsUtil.transformTime((int) ((ahHistory.getEndTimeStamp() - ahHistory.getStartTimeStamp()) / 1000))).append("\",");
							strOutput.append(ahHistory.getClientBSSID());
							strOutput.append("\n");
						}
						out.write(strOutput.toString());
					}
					
					List<AhClientStats> lstInterfaceInfo = QueryUtil.executeQuery(AhClientStats.class,
							new SortParams("timeStamp"), 
							new FilterParams("timeStamp>=:s1 and clientMac=:s2", 
								new Object[]{reportDateTime,obj.toString()}),
							profile.getOwner().getId());
					if (!lstInterfaceInfo.isEmpty()) {
						strOutput = new StringBuffer();
						strOutput.append("Client MAC Address,");
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("StatTime,");
						strOutput.append("Collection Period (seconds),");
						strOutput.append("SSID Name,");
						strOutput.append("Tx Frames,");
						strOutput.append("Rx Frames,");
						strOutput.append("Tx Drop Frames,");
						strOutput.append("Rx Drop Frames,");
						strOutput.append("Tx Airtime Utilization,");
						strOutput.append("Rx Airtime Utilization,");
						strOutput.append("Client Health,");
						strOutput.append("Client Radio Score,");
						strOutput.append("Client IP Network Score,");
						strOutput.append("Client Application Score,");
						strOutput.append("Bandwidth (Kbps),");
						strOutput.append("SLA Violation Traps,");
						strOutput.append("Tx Rate Distribution,");
						strOutput.append("Rx Rate Distribution,");
						strOutput.append("Tx Rate Success Distribution,");
						strOutput.append("Rx Rate Success Distribution");
						strOutput.append("\n");
						out.write(strOutput.toString());
						
						strOutput = new StringBuffer();
						for (AhClientStats clientStats : lstInterfaceInfo) {
							strOutput.append(clientStats.getClientMac()).append(",");
							strOutput.append(clientStats.getApName()).append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(clientStats.getTimeStamp(), tz)).append(",");
							strOutput.append(clientStats.getCollectPeriod()).append(",");
							strOutput.append(clientStats.getSsidName()).append(",");
							strOutput.append(clientStats.getTxFrameCount()).append(",");
							strOutput.append(clientStats.getRxFrameCount()).append(",");
							strOutput.append(clientStats.getTxFrameDropped()).append(",");
							strOutput.append(clientStats.getRxFrameDropped()).append(",");
							strOutput.append(clientStats.getTxAirTime()).append(",");
							strOutput.append(clientStats.getRxAirTime()).append(",");
							strOutput.append(clientStats.getOverallClientHealthScore()).append(",");
							strOutput.append(clientStats.getSlaConnectScore()).append(",");
							strOutput.append(clientStats.getIpNetworkConnectivityScore()).append(",");
							strOutput.append(clientStats.getApplicationHealthScore()).append(",");
							strOutput.append(clientStats.getBandWidthUsage()).append(",");
							strOutput.append(clientStats.getSlaViolationTraps()).append(",");

							String txDis = "";
							String rxDis = "";
							String txSuccDis = "";
							String rxSuccDis = "";
							if (clientStats.getTxRateInfo() != null) {
								String[] txRate = clientStats.getTxRateInfo().split(";");
								for (String rate : txRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											txDis = txDis + convertRateToM(Integer.parseInt(oneRec[0])) + "Mbps:" + oneRec[1] + "%; ";
											txSuccDis = txSuccDis + convertRateToM(Integer.parseInt(oneRec[0])) + "Mbps:" + oneRec[2] + "%; ";
										}
									}
								}
							}
							if (clientStats.getRxRateInfo() != null) {
								String[] rxRate = clientStats.getRxRateInfo().split(";");
								for (String rate : rxRate) {
									if (!rate.equals("")) {
										String[] oneRec = rate.split(",");
										if (oneRec.length == 3) {
											rxDis = rxDis + convertRateToM(Integer.parseInt(oneRec[0])) + "Mbps:" + oneRec[1] + "%; ";
											rxSuccDis = rxSuccDis + convertRateToM(Integer.parseInt(oneRec[0])) + "Mbps:" + oneRec[2] + "%; ";
										}
									}
								}
							}
							strOutput.append(txDis).append(",");
							strOutput.append(rxDis).append(",");
							strOutput.append(txSuccDis).append(",");
							strOutput.append(rxSuccDis);
							strOutput.append("\n");
						}
						out.write(strOutput.toString());
					}
				}
			} else {
				return false;
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientSessionNew():", e);
			log.error(e);
			return false;
		}
		return true;
	}


	public boolean setClientSession(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportDateTime = getReportDateTime(profile.getReportPeriod(),tz).getTimeInMillis();

			String searchSQL = "select clientMac, startTimeStamp,endTimeStamp,clientIP,clientHostname,clientUsername from " + AhClientSessionHistory.class.getSimpleName() + " where "
					+ " endTimeStamp >="
					+ reportDateTime
					+ " AND lower(apName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'";

			if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ profile.getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ profile.getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ profile.getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ profile.getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = " + profile.getLocation().getId();
			}

			searchSQL = searchSQL + " AND owner.id = " + profile.getOwner().getId();

			List<?> mapProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstReportClientMac = new TreeSet<String>();
			Map<String, String> clientHostNameMap = new HashMap<String, String>();
			Map<String, String> clientUserNameMap = new HashMap<String, String>();

			if (!mapProfiles.isEmpty()) {
				for (Object obj : mapProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[0] != null) {
						lstReportClientMac.add(tmp[0].toString());
						if (clientHostNameMap.get(tmp[0].toString()) == null
								|| clientHostNameMap.get(tmp[0].toString()).equals("")) {
							clientHostNameMap.put(tmp[0].toString(), tmp[4] == null ? "" : tmp[4]
									.toString());
						}
						if (clientUserNameMap.get(tmp[0].toString()) == null
								|| clientUserNameMap.get(tmp[0].toString()).equals("")) {
							clientUserNameMap.put(tmp[0].toString(), tmp[5] == null ? "" : tmp[5]
									.toString());
						}
					}
				}
			} else {
				return false;
			}
			for (String reportClientMac : lstReportClientMac) {
				List<AhAssociation> lstInterfaceInfo = QueryUtil.executeQuery(AhAssociation.class, new SortParams("timeStamp.time"),
						new FilterParams("timeStamp.time >= :s1 AND clientMac=:s2",
								new Object[]{reportDateTime,reportClientMac}), profile
						.getOwner().getId());

				AhAssociation tmpAhAssociationValue = new AhAssociation();

				List<CheckItem> client_trans_totalData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_beData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_bgData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_viData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_voData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_mgtData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_unicastData = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_dataOctets = new ArrayList<CheckItem>();
				List<CheckItem> client_trans_lastrate = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_totalData = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_mgtData = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_unicastData = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_multicastData = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_broadcastData = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_micfailures = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_dataOctets = new ArrayList<CheckItem>();
				List<CheckItem> client_rec_lastrate = new ArrayList<CheckItem>();
				List<CheckItem> client_rssi = new ArrayList<CheckItem>();
				List<CheckItem> clientLinkUpTime = new ArrayList<CheckItem>();

				List<String> clientSSID = new ArrayList<String>();
				List<String> clientVLAN = new ArrayList<String>();
				List<String> clientUserProfile = new ArrayList<String>();
				List<String> clientChannel = new ArrayList<String>();
				List<String> clientAuthMethod = new ArrayList<String>();
				List<String> clientEncryptionMethod = new ArrayList<String>();
				List<String> clientPhysicalMode = new ArrayList<String>();
				List<String> clientCWPUsed = new ArrayList<String>();
				List<String> clientHiveApName = new ArrayList<String>();
				List<String> clientBSSID = new ArrayList<String>();
				List<String> clientIpAddress=new ArrayList<String>();

				for (AhAssociation association : lstInterfaceInfo) {
					String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(association
							.getTimeStamp().getTime(), tz);

					// transmit client_totalData
					if (client_trans_totalData.size() == 0) {
						client_trans_totalData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxDataFrames(association
								.getClientTxDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxDataFrames(), tmpAhAssociationValue
								.getClientTxDataFrames());
						client_trans_totalData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxDataFrames(association
								.getClientTxDataFrames());
					}

					// transmit client_beData
					if (client_trans_beData.size() == 0) {
						client_trans_beData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxBeDataFrames(association
								.getClientTxBeDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxBeDataFrames(), tmpAhAssociationValue
								.getClientTxBeDataFrames());
						client_trans_beData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxBeDataFrames(association
								.getClientTxBeDataFrames());
					}

					// transmit client_bgData
					if (client_trans_bgData.size() == 0) {
						client_trans_bgData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxBgDataFrames(association
								.getClientTxBgDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxBgDataFrames(), tmpAhAssociationValue
								.getClientTxBgDataFrames());
						client_trans_bgData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxBgDataFrames(association
								.getClientTxBgDataFrames());
					}

					// transmit client_viData
					if (client_trans_viData.size() == 0) {
						client_trans_viData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxViDataFrames(association
								.getClientTxViDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxViDataFrames(), tmpAhAssociationValue
								.getClientTxViDataFrames());
						client_trans_viData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxViDataFrames(association
								.getClientTxViDataFrames());
					}

					// transmit client_voData
					if (client_trans_voData.size() == 0) {
						client_trans_voData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxVoDataFrames(association
								.getClientTxVoDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxVoDataFrames(), tmpAhAssociationValue
								.getClientTxVoDataFrames());
						client_trans_voData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxVoDataFrames(association
								.getClientTxVoDataFrames());
					}

					// transmit client_mgtData
					if (client_trans_mgtData.size() == 0) {
						client_trans_mgtData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxMgtFrames(association
								.getClientTxMgtFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxMgtFrames(), tmpAhAssociationValue
								.getClientTxMgtFrames());
						client_trans_mgtData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxMgtFrames(association
								.getClientTxMgtFrames());
					}

					// transmit client_unicastData
					if (client_trans_unicastData.size() == 0) {
						client_trans_unicastData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxUnicastFrames(association
								.getClientTxUnicastFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxUnicastFrames(), tmpAhAssociationValue
								.getClientTxUnicastFrames());
						client_trans_unicastData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxUnicastFrames(association
								.getClientTxUnicastFrames());
					}

					// transmit client_dataOctets
					if (client_trans_dataOctets.size() == 0) {
						client_trans_dataOctets.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientTxDataOctets(association
								.getClientTxDataOctets());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientTxDataOctets(), tmpAhAssociationValue
								.getClientTxDataOctets());
						client_trans_dataOctets.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientTxDataOctets(association
								.getClientTxDataOctets());
					}

					// transmit client_lastrate
					if (client_trans_lastrate.size() == 0) {
						client_trans_lastrate.add(new CheckItem((long) 0, starTime));
					} else {
						client_trans_lastrate.add(new CheckItem((long) association
								.getClientLastTxRate(), starTime));
					}

					// receive client_totalDataFrame
					if (client_rec_totalData.size() == 0) {
						client_rec_totalData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxDataFrames(association
								.getClientRxDataFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxDataFrames(), tmpAhAssociationValue
								.getClientRxDataFrames());
						client_rec_totalData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxDataFrames(association
								.getClientRxDataFrames());
					}

					// receive client_mgtData
					if (client_rec_mgtData.size() == 0) {
						client_rec_mgtData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxMgtFrames(association
								.getClientRxMgtFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxMgtFrames(), tmpAhAssociationValue
								.getClientRxMgtFrames());
						client_rec_mgtData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxMgtFrames(association
								.getClientRxMgtFrames());
					}

					// receive client_unicastData
					if (client_rec_unicastData.size() == 0) {
						client_rec_unicastData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxUnicastFrames(association
								.getClientRxUnicastFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxUnicastFrames(), tmpAhAssociationValue
								.getClientRxUnicastFrames());
						client_rec_unicastData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxUnicastFrames(association
								.getClientRxUnicastFrames());
					}

					// receive client_multicastData
					if (client_rec_multicastData.size() == 0) {
						client_rec_multicastData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxMulticastFrames(association
								.getClientRxMulticastFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxMulticastFrames(), tmpAhAssociationValue
								.getClientRxMulticastFrames());
						client_rec_multicastData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxMulticastFrames(association
								.getClientRxMulticastFrames());
					}

					// receive client_broadcastData
					if (client_rec_broadcastData.size() == 0) {
						client_rec_broadcastData.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxBroadcastFrames(association
								.getClientRxBroadcastFrames());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxBroadcastFrames(), tmpAhAssociationValue
								.getClientRxBroadcastFrames());
						client_rec_broadcastData.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxBroadcastFrames(association
								.getClientRxBroadcastFrames());
					}

					// receive client_micfailures
					if (client_rec_micfailures.size() == 0) {
						client_rec_micfailures.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxMICFailures(association
								.getClientRxMICFailures());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxMICFailures(), tmpAhAssociationValue
								.getClientRxMICFailures());
						client_rec_micfailures.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxMICFailures(association
								.getClientRxMICFailures());
					}

					// receive client_dataOctets
					if (client_rec_dataOctets.size() == 0) {
						client_rec_dataOctets.add(new CheckItem((long) 0, starTime));
						tmpAhAssociationValue.setClientRxDataOctets(association
								.getClientRxDataOctets());
					} else {
						long tmpCount = checkValueLessThanZero(association
								.getClientRxDataOctets(), tmpAhAssociationValue
								.getClientRxDataOctets());
						client_rec_dataOctets.add(new CheckItem(tmpCount, starTime));
						tmpAhAssociationValue.setClientRxDataOctets(association
								.getClientRxDataOctets());
					}

					// receive client_lastrate
					if (client_rec_lastrate.size() == 0) {
						client_rec_lastrate.add(new CheckItem((long) 0, starTime));
					} else {
						client_rec_lastrate.add(new CheckItem((long) association
								.getClientLastRxRate(), starTime));
					}

					// client_rssi
					if (client_rssi.size() == 0) {
						client_rssi.add(new CheckItem((long) 0, starTime));
					} else {
						client_rssi.add(new CheckItem((long) association.getClientRSSI(),
								starTime));
					}

					clientLinkUpTime.add(new CheckItem(association.getClientLinkUptime(),
							starTime));

					clientIpAddress.add(association.getClientIP());
					clientHiveApName.add(association.getApName());
					clientSSID.add(association.getClientSSID());
					clientVLAN.add(association.getClientVLANString());
					clientUserProfile.add(String.valueOf(association
							.getClientUserProfId()));
					clientChannel.add(String.valueOf(association
							.getClientChannelString()));
					clientAuthMethod.add(association.getClientAuthMethodString());
					clientEncryptionMethod.add(association
							.getClientEncryptionMethodString());
					clientPhysicalMode.add(association.getClientMacPtlString());
					clientCWPUsed.add(association.getClientCWPUsedString());
//								clientLinkUpTime = NmsUtil.transformTime((int) ((Long
//										.parseLong(values[1].toString()) - Long.parseLong(values[0]
//										.toString())) / 1000));
					clientBSSID.add(association.getClientBSSID());
				}

				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Client MAC Address,");
					strOutput.append("Client IP Address,");
					strOutput.append("Client HostName,");
					strOutput.append("Client UserName,");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
				}

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < client_trans_totalData.size(); cnt++) {
					strOutput.append(reportClientMac).append(",");
					strOutput.append(clientIpAddress.get(cnt)).append(",");
					strOutput.append(clientHostNameMap.get(reportClientMac) == null ? ","
							: clientHostNameMap.get(reportClientMac) + ",");
					strOutput.append(clientUserNameMap.get(reportClientMac) == null ? ","
							: clientUserNameMap.get(reportClientMac) + ",");
					strOutput.append(clientHiveApName.get(cnt)).append(",");
					strOutput.append(client_trans_totalData.get(cnt).getValue()).append(",");
					strOutput.append(clientSSID.get(cnt)).append(",");
					strOutput.append(clientVLAN.get(cnt)).append(",");
					strOutput.append(clientUserProfile.get(cnt)).append(",");
					strOutput.append(clientChannel.get(cnt)).append(",");
					strOutput.append(clientAuthMethod.get(cnt)).append(",");
					strOutput.append(clientEncryptionMethod.get(cnt)).append(",");
					strOutput.append(clientPhysicalMode.get(cnt)).append(",");
					strOutput.append(clientCWPUsed.get(cnt)).append(",");
					strOutput.append("\"").append(NmsUtil.transformTime(clientLinkUpTime.get(cnt).getId().intValue())).append("\",");
					strOutput.append(clientBSSID.get(cnt)).append(",");
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
					strOutput.append(client_rssi.get(cnt).getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientSession():", e);
			log.error(e);
			return false;
		}

		return true;
	}

	public boolean setClientCount(AhReport profile, TimeZone tz) {
		try {
			FileWriter out;

			Calendar firstReportTime = getReportDateTime(profile.getReportPeriod(),tz);
			int reportTimeAggregation = (int) getReportTimeAggregation(profile.getTimeAggregation()) / 3600000;
			long systemTimeInMillis = System.currentTimeMillis();

			Calendar reportTime = Calendar.getInstance();
			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());

			FilterParams filterParams;
			if (profile.getLocation() != null) {
				filterParams = new FilterParams(
						"bo.endTimeStamp>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3",
						new Object[] { reportTime.getTimeInMillis(), profile.getLocation().getId(),
								profile.getApNameForSQLTwo() });
			} else {
				filterParams = new FilterParams(
						"bo.endTimeStamp>=:s1 and lower(bo.apName) like :s2", new Object[] {
								reportTime.getTimeInMillis(), profile.getApNameForSQLTwo() });
			}

			List<?> subProfiles = QueryUtil.executeQuery(
					"select bo.clientMACProtocol, bo.endTimeStamp,bo.startTimeStamp from "
							+ AhClientSessionHistory.class.getSimpleName() + " bo", new SortParams(
							"endTimeStamp"), filterParams, profile.getOwner().getId());
			Map<String, AhClientCountForAP> clientCount = new HashMap<String, AhClientCountForAP>();

			for (Object obj : subProfiles) {
				reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
				reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
				while (reportTime.getTimeInMillis() <= systemTimeInMillis) {
					Object[] tmp = (Object[]) obj;
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
							tmpAhClientCountForAP.setTz(tz);
							tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
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
						tmpAhClientCountForAP.setTz(tz);
						tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
						clientCount.put(String.valueOf(reportTime.getTimeInMillis()),
								tmpAhClientCountForAP);
					}
					reportTime.add(Calendar.HOUR_OF_DAY, reportTimeAggregation);
				}
			}

			reportTime.setTimeInMillis(firstReportTime.getTimeInMillis());
			FilterParams currentFilterParams;
//			if (profile.getLocation() != null) {
//				currentFilterParams = new FilterParams(
//						"bo.mapId=:s1 and lower(bo.apName) like :s2", new Object[] {
//								profile.getLocation().getId(), profile.getApNameForSQLTwo() });
//			} else {
//				currentFilterParams = new FilterParams("lower(bo.apName) like :s1",
//						new Object[] { profile.getApNameForSQLTwo() });
//			}
//
//			List<?> currentProfiles = QueryUtil.executeQuery(
//					"select bo.clientMACProtocol, bo.startTimeStamp from "
//							+ AhClientSession.class.getSimpleName() + " bo", new SortParams(
//							"startTimeStamp"), currentFilterParams, profile.getOwner().getId());
			if (profile.getLocation() != null) {
				currentFilterParams = new FilterParams(
						"mapId=? and lower(apName) like ?", new Object[] {
								profile.getLocation().getId(), profile.getApNameForSQLTwo() });
			} else {
				currentFilterParams = new FilterParams("lower(apName) like ?",
						new Object[] { profile.getApNameForSQLTwo() });
			}

			List<?> currentProfiles = DBOperationUtil.executeQuery(
					"select clientMACProtocol, startTimeStamp from ah_clientsession",
					new SortParams("startTimeStamp"), currentFilterParams, profile.getOwner().getId());

			for (Object obj : currentProfiles) {
				Object[] tmp = (Object[]) obj;
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
						tmpAhClientCountForAP.setTz(tz);
						tmpAhClientCountForAP.setReportTime(reportTime.getTimeInMillis());
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
					return new Long((reportTime1 - reportTime2) / 100000).intValue();
				}
			});

			StringBuffer strOutput;
			if (!lstClientCount.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);

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
				for (AhClientCountForAP clientCountForAP : lstClientCount) {
					strOutput.append(clientCountForAP.getReportTimeString()).append(",");
					strOutput.append(clientCountForAP.getAModeCount()).append(",");
					strOutput.append(clientCountForAP.getBModeCount()).append(",");
					strOutput.append(clientCountForAP.getGModeCount()).append(",");
					strOutput.append(clientCountForAP.getNaModeCount()).append(",");
					strOutput.append(clientCountForAP.getNgModeCount()).append(",");
					strOutput.append(clientCountForAP.getAcModeCount());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientCount():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setMaxClient(AhReport profile, TimeZone tz){
		try {
			FileWriter out;
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			
			String searchSQL = "select bo.maxClientCount,bo.timeStamp from ";
			FilterParams maxClientFilterParams;
			
			maxClientFilterParams = new FilterParams(
					"bo.timeStamp>=:s1 and globalFlg=:s2", new Object[] {reportDateTime.getTimeInMillis(),false });

			List<?> maxClientProfiles = QueryUtil.executeQuery(searchSQL + AhMaxClientsCount.class.getSimpleName()
					+ " bo", new SortParams("timeStamp"), maxClientFilterParams, profile.getOwner().getId());

			List<CheckItem> maxClients = new ArrayList<CheckItem>();

			for (Object obj : maxClientProfiles) {
				Object[] oneProfile = (Object[]) obj;
				maxClients.add(new CheckItem(Long.parseLong(oneProfile[0].toString()),
						AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(oneProfile[1].toString()), tz)));
			}

			StringBuffer strOutput;
			SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
			sfname.setTimeZone(tz);
			String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
					+ sfname.format(new Date()) + ".csv";
			File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
					+ profile.getOwner().getDomainName() + File.separator + mailFileName);
			out = new FileWriter(tmpFile);

			strOutput = new StringBuffer();
			strOutput.append("StatTime,");
			strOutput.append("Max Concurrent Client Count");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			if (!maxClients.isEmpty()) {
				strOutput = new StringBuffer();
				for (CheckItem clientCount : maxClients) {
					strOutput.append(clientCount.getValue()).append(",");
					strOutput.append(clientCount.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} 

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setMaxClient():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setHiveApConnection(AhReport profile, TimeZone tz){
		try {
			FileWriter out;
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(),tz);
	
			FilterParams filterParams;
			if (profile.getLocation() != null) {
				filterParams = new FilterParams(
						"bo.trapTime>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3", new Object[] {
								reportDateTime.getTimeInMillis(), profile.getLocation().getId(),
								profile.getApNameForSQLTwo() });
			} else {
				filterParams = new FilterParams("bo.trapTime>=:s1 and lower(bo.apName) like :s2",
						new Object[] { reportDateTime.getTimeInMillis(), profile.getApNameForSQLTwo() });
			}
			List<APConnectHistoryInfo> lstHiveApConnection = QueryUtil.executeQuery(APConnectHistoryInfo.class, 
					new SortParams("apName,trapTime", false), filterParams, profile.getOwner().getId());
			
			for(APConnectHistoryInfo info:lstHiveApConnection){
				info.setTz(tz);
			}

			StringBuffer strOutput;
			SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
			sfname.setTimeZone(tz);
			String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
					+ sfname.format(new Date()) + ".csv";
			File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
					+ profile.getOwner().getDomainName() + File.separator + mailFileName);
			out = new FileWriter(tmpFile);

			strOutput = new StringBuffer();
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceMac")).append(",");
			strOutput.append("Occurred,");
			strOutput.append("Connection Status,");
			strOutput.append("Reason");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			if (!lstHiveApConnection.isEmpty()) {
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
			} 

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setHiveApConnection():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	

	public boolean setClientAirTime(AhReport profile, TimeZone tz) {
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchSQL = "select clientMac, clientHostname from " + AhClientSessionHistory.class.getSimpleName() + " where "
					+ " startTimeStamp >="
					+ reportDateTime
					+ " AND lower(apName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'";

			if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ profile.getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ profile.getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ profile.getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ profile.getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = " + profile.getLocation().getId();
			}

			searchSQL = searchSQL + " AND owner.id = " + profile.getOwner().getId();

			List<?> mapProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstReportClientMac = new TreeSet<String>();
//			Map<String, List<String>> clientSessionMap = new HashMap<String, List<String>>();
//			Map<String, String> clientIpAddressMap = new HashMap<String, String>();
//			Map<String, String> clientHostNameMap = new HashMap<String, String>();
//			Map<String, String> clientUserNameMap = new HashMap<String, String>();

			if (!mapProfiles.isEmpty()) {
				for (Object obj : mapProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[0] != null) {
						lstReportClientMac.add(tmp[0].toString());

//						if (clientSessionMap.get(tmp[0].toString()) == null) {
//							List<String> tmpList = new ArrayList<String>();
//							String tmpSession = AhDateTimeUtil.getSpecifyDateTimeReport(Long
//									.parseLong(tmp[1].toString()), tz)
//									+ "|"
//									+ AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(tmp[2]
//											.toString()), tz);
//							tmpList.add(tmpSession);
//							clientSessionMap.put(tmp[0].toString(), tmpList);
//						} else {
//							String tmpSession = AhDateTimeUtil.getSpecifyDateTimeReport(Long
//									.parseLong(tmp[1].toString()), tz)
//									+ "|"
//									+ AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(tmp[2]
//											.toString()), tz);
//							clientSessionMap.get(tmp[0].toString()).add(tmpSession);
//						}
//						if (clientIpAddressMap.get(tmp[0].toString()) == null
//								|| clientIpAddressMap.get(tmp[0].toString()).equals("")) {
//							clientIpAddressMap.put(tmp[0].toString(), tmp[3] == null? "" : tmp[3]
//							        .toString());
//						}
//						if (clientHostNameMap.get(tmp[0].toString()) == null
//								|| clientHostNameMap.get(tmp[0].toString()).equals("")) {
//							clientHostNameMap.put(tmp[0].toString(), tmp[3] == null ? "" : tmp[3]
//									.toString());
//						}
//						if (clientUserNameMap.get(tmp[0].toString()) == null
//								|| clientUserNameMap.get(tmp[0].toString()).equals("")) {
//							clientUserNameMap.put(tmp[0].toString(), tmp[5] == null ? "" : tmp[5]
//									.toString());
//						}
					}
				}
			} else {
				return false;
			}

			for (String reportClientMac : lstReportClientMac) {
				String searchSQLSub = "timeStamp.time >= :s1 AND clientMac=:s2";

				Object values[] = new Object[2];
				values[0] = reportDateTime;
				values[1] = reportClientMac;

				List<AhAssociation> lstInterfaceInfo = QueryUtil.executeQuery(AhAssociation.class, new SortParams(
						"timeStamp.time"), new FilterParams(searchSQLSub, values), profile
						.getOwner().getId());

				AhAssociation tmpPreviousValue = new AhAssociation();
				AhAssociation tmpNeedSaveValue = new AhAssociation();
				List<TextItem> transmit_airTime = new ArrayList<TextItem>();
				List<TextItem> receive_airTime = new ArrayList<TextItem>();

				Calendar tmpDate = Calendar.getInstance();
				tmpDate.setTimeInMillis(reportDateTime);
				long reportTimeInMillis = reportDateTime;
				long systemTimeInLong = System.currentTimeMillis();
				if (!lstInterfaceInfo.isEmpty()) {
					tmpPreviousValue = lstInterfaceInfo.get(0);
				}
				while (reportTimeInMillis < systemTimeInLong) {
					for (AhAssociation association : lstInterfaceInfo) {
						if (association.getTimeStamp().getTime() <= reportTimeInMillis
								- reportTimeAggregation) {
							continue;
						}
						if (association.getTimeStamp().getTime() > reportTimeInMillis) {
							break;
						} else {
							// transmit airTime
							double tmpCount = checkValueLessThanZero(association
									.getClientTxAirtime(), tmpPreviousValue
									.getClientTxAirtime());
							tmpNeedSaveValue.setClientTxAirtime(tmpNeedSaveValue
									.getClientTxAirtime()
									+ tmpCount);
							tmpPreviousValue.setClientTxAirtime(association
									.getClientTxAirtime());
							// receive airTime
							tmpCount = checkValueLessThanZero(association
									.getClientRxAirtime(), tmpPreviousValue
									.getClientRxAirtime());
							tmpNeedSaveValue.setClientRxAirtime(tmpNeedSaveValue
									.getClientRxAirtime()
									+ tmpCount);
							tmpPreviousValue.setClientRxAirtime(association
									.getClientRxAirtime());
						}
					}
					String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(tmpDate
							.getTimeInMillis(), tz);

					transmit_airTime.add(new TextItem(df.format(tmpNeedSaveValue
							.getClientTxAirtime()*100/reportTimeAggregation), tmpDateStringValue));
					receive_airTime.add(new TextItem(df.format(tmpNeedSaveValue
							.getClientRxAirtime()*100/reportTimeAggregation), tmpDateStringValue));

					reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
					tmpDate.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
					tmpNeedSaveValue = new AhAssociation();
				}

				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-" + profile.getName()
							+ "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Client MAC Address,");
//						strOutput.append("Client IP Address,");
//						strOutput.append("Client HostName,");
//						strOutput.append("Client UserName,");
					strOutput.append("StatTime,");
					strOutput.append("Transmitted AirTime(%),");
					strOutput.append("Received AirTime(%)");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < transmit_airTime.size(); cnt++) {
					strOutput.append(reportClientMac).append(",");
//						strOutput.append(clientIpAddressMap.get(reportClientMac) == null ? ","
//								: clientIpAddressMap.get(reportClientMac) + ",");
//						strOutput.append(clientHostNameMap.get(reportClientMac) == null ? ","
//								: clientHostNameMap.get(reportClientMac) + ",");
//						strOutput.append(clientUserNameMap.get(reportClientMac) == null ? ","
//								: clientUserNameMap.get(reportClientMac) + ",");

					strOutput.append(transmit_airTime.get(cnt).getValue()).append(",");
					strOutput.append(transmit_airTime.get(cnt).getKey()).append(",");
					strOutput.append(receive_airTime.get(cnt).getKey());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientAirTime():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setUniqueClientCount(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			long systemTimeInMillis = System.currentTimeMillis();

			String searchSQL = "select DISTINCT apName from " + AhClientSessionHistory.class.getSimpleName() + " where "
					+ " endTimeStamp >=" + reportDateTime.getTimeInMillis()
					+ " AND lower(apName) like '%" + profile.getApNameForSQL().toLowerCase() + "%'"
					+ " AND owner.id=" + profile.getOwner().getId();

			if (profile.getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = " + profile.getLocation().getId().toString();
			}

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstHiveAPName = new TreeSet<String>();

			for (Object obj : lstProfiles) {
				String tmp = obj.toString();
				if (tmp != null && !tmp.equals("")) {
					lstHiveAPName.add(tmp);
				}
			}

			String searchSQLCurrent = "select DISTINCT apName from " + AhClientSession.class.getSimpleName() + " where "
					+ " lower(apName) like '%" + profile.getApNameForSQL().toLowerCase() + "%'"
					+ " AND owner.id=" + profile.getOwner().getId();

			if (profile.getLocation() != null) {
				searchSQLCurrent = searchSQLCurrent + " AND mapId = "
						+ profile.getLocation().getId().toString();
			}

			List<?> profilesCurrnet = QueryUtil.executeQuery(searchSQLCurrent, null, null);

			for (Object obj : profilesCurrnet) {
				String tmp = obj.toString();
				if (tmp != null && !tmp.equals("")) {
					lstHiveAPName.add(tmp);
				}
			}

			if (lstHiveAPName.isEmpty()) {
				return false;
			}

			for (String reportAPName : lstHiveAPName) {
				String searchSQLSub = "select clientMac,startTimeStamp,endTimeStamp from " + AhClientSessionHistory.class.getSimpleName() + " where "
						+ " endTimeStamp >=" + reportDateTime.getTimeInMillis();
				searchSQLSub = searchSQLSub + " AND apName='" + reportAPName + "'";
				if (profile.getLocation() != null) {
					searchSQLSub = searchSQLSub + " AND mapId = "
							+ profile.getLocation().getId().toString();
				}
				searchSQLSub = searchSQLSub + " AND owner.id=" + profile.getOwner().getId();

				List<?> subProfiles = QueryUtil.executeQuery(searchSQLSub, null, null);

				Map<String, Set<String>> clientCountMap = new HashMap<String, Set<String>>();

				if (!subProfiles.isEmpty()) {
					Calendar historyTime = Calendar.getInstance();

					for (Object obj : subProfiles) {
						historyTime.setTimeInMillis(reportDateTime.getTimeInMillis());
						historyTime.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						while (historyTime.getTimeInMillis() <= systemTimeInMillis) {
							Object[] tmp = (Object[]) obj;
							String historyTimeString = AhDateTimeUtil.getSpecifyDateTimeReport(
									historyTime.getTimeInMillis(), tz);
							if (Long.parseLong(tmp[1].toString()) <= historyTime.getTimeInMillis()
									&& Long.parseLong(tmp[2].toString()) > historyTime
									.getTimeInMillis()
									- reportTimeAggregation) {
								if (clientCountMap.get(historyTimeString) == null) {
									Set<String> setClientMac = new HashSet<String>();
									setClientMac.add(tmp[0].toString());
									clientCountMap.put(historyTimeString, setClientMac);
								} else {
									clientCountMap.get(historyTimeString).add(tmp[0].toString());
								}
							}

							if (clientCountMap.get(historyTimeString) == null) {
								Set<String> setClientMac = new HashSet<String>();
								clientCountMap.put(historyTimeString, setClientMac);
							}

							historyTime.add(Calendar.HOUR_OF_DAY,
									(int) (reportTimeAggregation / 3600000));
						}
					}
				}

				String searchSQLSubCurrent = "select clientMac,startTimeStamp from " + AhClientSession.class.getSimpleName() + " where";
				searchSQLSubCurrent = searchSQLSubCurrent + " apName='" + reportAPName + "'";
				if (profile.getLocation() != null) {
					searchSQLSubCurrent = searchSQLSubCurrent + " AND mapId = "
							+ profile.getLocation().getId().toString();
				}
				searchSQLSubCurrent = searchSQLSubCurrent + " AND owner.id="
						+ profile.getOwner().getId();

				List<?> subProfilesCurrent = QueryUtil
						.executeQuery(searchSQLSubCurrent, null, null);
				if (!subProfilesCurrent.isEmpty()) {
					Calendar currentTime = Calendar.getInstance();
					for (Object obj : subProfilesCurrent) {
						currentTime.setTimeInMillis(reportDateTime.getTimeInMillis());
						currentTime.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						while (currentTime.getTimeInMillis() <= systemTimeInMillis) {
							Object[] tmp = (Object[]) obj;
							String currentTimeString = AhDateTimeUtil.getSpecifyDateTimeReport(
									currentTime.getTimeInMillis(), tz);

							if (Long.parseLong(tmp[1].toString()) <= currentTime.getTimeInMillis()) {
								if (clientCountMap.get(currentTimeString) == null) {
									Set<String> setClientMac = new HashSet<String>();
									setClientMac.add(tmp[0].toString());
									clientCountMap.put(currentTimeString, setClientMac);
								} else {
									clientCountMap.get(currentTimeString).add(tmp[0].toString());
								}
							}
							if (clientCountMap.get(currentTimeString) == null) {
								Set<String> setClientMac = new HashSet<String>();
								clientCountMap.put(currentTimeString, setClientMac);
							}
							currentTime.add(Calendar.HOUR_OF_DAY,
									(int) (reportTimeAggregation / 3600000));
						}
					}
				}

				List<CheckItem> uniqueClients = new ArrayList<CheckItem>();
				for (String tmpReportTime : clientCountMap.keySet()) {
					long tmpSize = clientCountMap.get(tmpReportTime).size();
					uniqueClients.add(new CheckItem(tmpSize, tmpReportTime));
				}

				Collections.sort(uniqueClients, new Comparator<CheckItem>() {
					@Override
					public int compare(CheckItem o1, CheckItem o2) {
						try {
							SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date reportTime1 = sf.parse(o1.getValue());
							Date reportTime2 = sf.parse(o2.getValue());
							return new Long(
									(reportTime1.getTime() - reportTime2.getTime()) / 100000)
									.intValue();
						} catch (Exception e) {
							return 0;
						}
					}
				});

				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-" + profile.getName()
							+ "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName() + File.separator
							+ mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
					strOutput.append("StatTime,");
					strOutput.append("Client Count");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}

				strOutput = new StringBuffer();
				for (CheckItem uniqueClient : uniqueClients) {
					strOutput.append(reportAPName).append(",");
					strOutput.append(uniqueClient.getValue()).append(",");
					strOutput.append(uniqueClient.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setUniqueClientCount():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSecurityRogueAPs(AhReport profile, TimeZone tz) {
		try {
			FileWriter out;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			long systemTimeInMillis = System.currentTimeMillis();
			String searchSQL = "select bo.ifMacAddress,bo.reportTime.time from ";
			// String searchSQL = "select bo.lastDetectedTime from ";
			FilterParams rogueApsFilterParams;
			// if (profile.getLocation() != null) {
			// rogueApsFilterParams = new FilterParams(
			// "bo.manageStatus=:s1 and bo.lastDetectedTime>=:s2 and
			// bo.mapContainer.id=:s3",
			// new Object[] { HiveAp.STATUS_ROGUE, reportDateTime.getTime(),
			// profile.getLocation().getId() });
			// } else {
			// rogueApsFilterParams = new FilterParams(
			// "bo.manageStatus=:s1 and bo.lastDetectedTime>=:s2", new Object[]
			// {
			// HiveAp.STATUS_ROGUE, reportDateTime.getTime() });
			// }
			rogueApsFilterParams = new FilterParams(
					"bo.stationType = :s1 and bo.idpType=:s2 and bo.reportTime.time>=:s3",
					new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_AP,
							BeCommunicationConstant.IDP_TYPE_ROGUE,
							reportDateTime.getTimeInMillis() });

			List<?> rogueProfiles = QueryUtil.executeQuery(searchSQL + Idp.class.getSimpleName()
					+ " bo", new SortParams("reportTime.time"), rogueApsFilterParams, profile
					.getOwner().getId());

			List<CheckItem> rogueAPs = new ArrayList<CheckItem>();

			long currentTimeInMillis = reportDateTime.getTimeInMillis() - 1;
			reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			while (reportDateTime.getTimeInMillis() <= systemTimeInMillis) {
				long rogueCount = 0;
				List<String> ifMac = new ArrayList<String>();
				for (Object obj : rogueProfiles) {
					// Date tmp = (Date) rogueProfiles.get(i);
					Object[] oneProfile = (Object[]) obj;
					if (ifMac.contains(oneProfile[0].toString())) {
						continue;
					}
					ifMac.add(oneProfile[0].toString());
					long tmp = Long.parseLong(oneProfile[1].toString());
					if (tmp >= currentTimeInMillis && tmp < reportDateTime.getTimeInMillis()) {
						rogueCount++;
					}
				}
				String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(reportDateTime
						.getTimeInMillis(), tz);

				rogueAPs.add(new CheckItem(rogueCount, tmpDateStringValue));
				currentTimeInMillis = reportDateTime.getTimeInMillis();
				reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			}

			StringBuffer strOutput;
			if (!rogueAPs.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
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
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setSecurityRogueAPs():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSecurityRogueClients(AhReport profile, TimeZone tz) {
		try {
			FileWriter out;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			long systemTimeInMillis = System.currentTimeMillis();
			String searchSQL = "select bo.ifMacAddress,bo.reportTime.time from ";
			FilterParams rogueApsFilterParams;

			rogueApsFilterParams = new FilterParams(
					"bo.stationType = :s1 and bo.idpType=:s2 and bo.reportTime.time>=:s3",
					new Object[] { BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
							BeCommunicationConstant.IDP_TYPE_ROGUE,
							reportDateTime.getTimeInMillis() });

			List<?> rogueProfiles = QueryUtil.executeQuery(searchSQL + Idp.class.getSimpleName()
					+ " bo", new SortParams("reportTime.time"), rogueApsFilterParams, profile
					.getOwner().getId());

			List<CheckItem> rogueClients = new ArrayList<CheckItem>();

			long currentTimeInMillis = reportDateTime.getTimeInMillis() - 1;
			reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			while (reportDateTime.getTimeInMillis() <= systemTimeInMillis) {
				long rogueCount = 0;
				List<String> ifMac = new ArrayList<String>();
				for (Object obj : rogueProfiles) {
					Object[] oneProfile = (Object[]) obj;
					if (ifMac.contains(oneProfile[0].toString())) {
						continue;
					}
					ifMac.add(oneProfile[0].toString());
					long tmp = Long.parseLong(oneProfile[1].toString());
					// Date tmp = (Date) rogueProfiles.get(i);
					if (tmp >= currentTimeInMillis && tmp < reportDateTime.getTimeInMillis()) {
						rogueCount++;
					}
				}
				String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(reportDateTime
						.getTimeInMillis(), tz);

				rogueClients.add(new CheckItem(rogueCount, tmpDateStringValue));
				currentTimeInMillis = reportDateTime.getTimeInMillis();
				reportDateTime.add(Calendar.HOUR_OF_DAY, (int) (reportTimeAggregation / 3600000));
			}
			StringBuffer strOutput;
			if (!rogueClients.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
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
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setSecurityRogueClients():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setSecurityDetection(AhReport profile, TimeZone tz) {
		return true;
	}

	public boolean setMeshNeighbors(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportTimeAggregation = getReportTimeAggregation(profile.getTimeAggregation());
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			List<SimpleHiveAp> allApList = CacheMgmt.getInstance().getAllApList(profile.getOwner().getId());
			StringBuilder filterSwitchSql = new StringBuilder();
			if (allApList!=null && !allApList.isEmpty()) {
				for(SimpleHiveAp simpAp : allApList) {
					if (HiveAp.isSwitchProduct(simpAp.getHiveApModel()) || simpAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200) {
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
					+ " timeStamp.time >=" + reportDateTime + " AND lower(apName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'" + " AND owner.id="
					+ profile.getOwner().getId();
			
			if (filterSwitchSql.length()>0) {
				searchSQL = searchSQL + " AND apMac not in " + filterSwitchSql.toString();
				searchSQL = searchSQL + " AND neighborAPID not in " + filterSwitchSql.toString();
			}

			List<?> lstProfiles = QueryUtil.executeQuery(searchSQL, null, null);
			Set<String> lstHiveAPName = new TreeSet<String>();
			Map<String, List<String>> neighborAPMap = new HashMap<String, List<String>>();

			if (!lstProfiles.isEmpty()) {
				for (Object obj : lstProfiles) {
					Object[] tmp = (Object[]) obj;
					if (tmp[1] != null && !tmp[1].toString().equals("")) {
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
			} else {
				return false;
			}

			for (String reportAPName : lstHiveAPName) {
				if (neighborAPMap.get(reportAPName) == null) {
					continue;
				}
				for (int k = 0; k < neighborAPMap.get(reportAPName).size(); k++) {
					String searchSQLSub = "timeStamp.time >= :s1 AND apName =:s2 AND neighborAPID=:s3 AND linkType=:s4";

					Object values[] = new Object[4];
					values[0] = reportDateTime;
					values[1] = reportAPName;
					values[2] = neighborAPMap.get(reportAPName).get(k);
					values[3] = AhNeighbor.LINKTYPE_WIRELESSLINK;

					List<AhNeighbor> lstInterfaceInfo = QueryUtil.executeQuery(AhNeighbor.class, new SortParams(
							"timeStamp.time"), new FilterParams(searchSQLSub, values), profile
							.getOwner().getId());

					AhNeighbor tmpPreviousValue = new AhNeighbor();
					AhNeighbor tmpNeedSaveValue = new AhNeighbor();
					List<CheckItem> mesh_trans_totalData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_beData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_bgData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_viData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_voData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_mgtData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_trans_unicastData = new ArrayList<CheckItem>();
					// List<CheckItem> mesh_trans_multicastData = new
					// ArrayList<CheckItem>();
					// List<CheckItem> mesh_trans_broadcastData = new
					// ArrayList<CheckItem>();
					List<CheckItem> mesh_rec_totalData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_rec_mgtData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_rec_unicastData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_rec_multicastData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_rec_broadcastData = new ArrayList<CheckItem>();
					List<CheckItem> mesh_rssiData = new ArrayList<CheckItem>();

//						int rssiCount = 0;
					Calendar tmpDate = Calendar.getInstance();
					tmpDate.setTimeInMillis(reportDateTime);
					long reportTimeInMillis = reportDateTime;
					long systemTimeInLong = System.currentTimeMillis();
					if (!lstInterfaceInfo.isEmpty()) {
						tmpPreviousValue = lstInterfaceInfo.get(0);
					}
					while (reportTimeInMillis < systemTimeInLong) {
						for (AhNeighbor neighbor : lstInterfaceInfo) {
							if (neighbor.getTimeStamp().getTime() <= reportTimeInMillis
									- reportTimeAggregation) {
								continue;
							}

							if (neighbor.getTimeStamp().getTime() > reportTimeInMillis) {
								break;
							} else {
								// transmit totalData
								long tmpCount = checkValueLessThanZero(neighbor
										.getTxDataFrames(), tmpPreviousValue.getTxDataFrames());
								tmpNeedSaveValue.setTxDataFrames(tmpNeedSaveValue
										.getTxDataFrames()
										+ tmpCount);
								tmpPreviousValue.setTxDataFrames(neighbor.getTxDataFrames());

								// transmit beData
								tmpCount = checkValueLessThanZero(neighbor
										.getTxBeDataFrames(), tmpPreviousValue
										.getTxBeDataFrames());
								tmpNeedSaveValue.setTxBeDataFrames(tmpNeedSaveValue
										.getTxBeDataFrames()
										+ tmpCount);
								tmpPreviousValue.setTxBeDataFrames(neighbor
										.getTxBeDataFrames());

								// transmit bgData
								tmpCount = checkValueLessThanZero(neighbor
										.getTxBgDataFrames(), tmpPreviousValue
										.getTxBgDataFrames());
								tmpNeedSaveValue.setTxBgDataFrames(tmpNeedSaveValue
										.getTxBgDataFrames()
										+ tmpCount);
								tmpPreviousValue.setTxBgDataFrames(neighbor
										.getTxBgDataFrames());

								// transmit viData
								tmpCount = checkValueLessThanZero(neighbor
										.getTxViDataFrames(), tmpPreviousValue
										.getTxViDataFrames());
								tmpNeedSaveValue.setTxViDataFrames(tmpNeedSaveValue
										.getTxViDataFrames()
										+ tmpCount);
								tmpPreviousValue.setTxViDataFrames(neighbor
										.getTxViDataFrames());

								// transmit voData
								tmpCount = checkValueLessThanZero(neighbor
										.getTxVoDataFrames(), tmpPreviousValue
										.getTxVoDataFrames());
								tmpNeedSaveValue.setTxVoDataFrames(tmpNeedSaveValue
										.getTxVoDataFrames()
										+ tmpCount);
								tmpPreviousValue.setTxVoDataFrames(neighbor
										.getTxVoDataFrames());

								// transmit mgtData
								tmpCount = checkValueLessThanZero(neighbor.getTxMgtFrames(),
										tmpPreviousValue.getTxMgtFrames());
								tmpNeedSaveValue.setTxMgtFrames(tmpNeedSaveValue
										.getTxMgtFrames()
										+ tmpCount);
								tmpPreviousValue.setTxMgtFrames(neighbor.getTxMgtFrames());

								// transmit unicastData
								tmpCount = checkValueLessThanZero(neighbor
										.getTxUnicastFrames(), tmpPreviousValue
										.getTxUnicastFrames());
								tmpNeedSaveValue.setTxUnicastFrames(tmpNeedSaveValue
										.getTxUnicastFrames()
										+ tmpCount);
								tmpPreviousValue.setTxUnicastFrames(neighbor
										.getTxUnicastFrames());

								// receive totalData
								tmpCount = checkValueLessThanZero(neighbor.getRxDataFrames(),
										tmpPreviousValue.getRxDataFrames());
								tmpNeedSaveValue.setRxDataFrames(tmpNeedSaveValue
										.getRxDataFrames()
										+ tmpCount);
								tmpPreviousValue.setRxDataFrames(neighbor.getRxDataFrames());

								// receive mgtData
								tmpCount = checkValueLessThanZero(neighbor.getRxMgtFrames(),
										tmpPreviousValue.getRxMgtFrames());
								tmpNeedSaveValue.setRxMgtFrames(tmpNeedSaveValue
										.getRxMgtFrames()
										+ tmpCount);
								tmpPreviousValue.setRxMgtFrames(neighbor.getRxMgtFrames());

								// receive unicastData
								tmpCount = checkValueLessThanZero(neighbor
										.getRxUnicastFrames(), tmpPreviousValue
										.getRxUnicastFrames());
								tmpNeedSaveValue.setRxUnicastFrames(tmpNeedSaveValue
										.getRxUnicastFrames()
										+ tmpCount);
								tmpPreviousValue.setRxUnicastFrames(neighbor
										.getRxUnicastFrames());

								// receive multicastData
								tmpCount = checkValueLessThanZero(neighbor
										.getRxMulticastFrames(), tmpPreviousValue
										.getRxMulticastFrames());
								tmpNeedSaveValue.setRxMulticastFrames(tmpNeedSaveValue
										.getRxMulticastFrames()
										+ tmpCount);
								tmpPreviousValue.setRxMulticastFrames(neighbor
										.getRxMulticastFrames());

								// receive broadcastData
								tmpCount = checkValueLessThanZero(neighbor
										.getRxBroadcastFrames(), tmpPreviousValue
										.getRxBroadcastFrames());
								tmpNeedSaveValue.setRxBroadcastFrames(tmpNeedSaveValue
										.getRxBroadcastFrames()
										+ tmpCount);
								tmpPreviousValue.setRxBroadcastFrames(neighbor
										.getRxBroadcastFrames());

								// receive rssiData
								tmpNeedSaveValue.setRssi(neighbor.getRssi());
//									rssiCount++;
							}
						}
						String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(
								tmpDate.getTimeInMillis(), tz);

						mesh_trans_totalData.add(new CheckItem(tmpNeedSaveValue
								.getTxDataFrames(), tmpDateStringValue));
						mesh_trans_beData.add(new CheckItem(tmpNeedSaveValue
								.getTxBeDataFrames(), tmpDateStringValue));
						mesh_trans_bgData.add(new CheckItem(tmpNeedSaveValue
								.getTxBgDataFrames(), tmpDateStringValue));
						mesh_trans_viData.add(new CheckItem(tmpNeedSaveValue
								.getTxViDataFrames(), tmpDateStringValue));
						mesh_trans_voData.add(new CheckItem(tmpNeedSaveValue
								.getTxVoDataFrames(), tmpDateStringValue));
						mesh_trans_mgtData.add(new CheckItem(tmpNeedSaveValue
								.getTxMgtFrames(), tmpDateStringValue));
						mesh_trans_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getTxUnicastFrames(), tmpDateStringValue));
						mesh_rec_totalData.add(new CheckItem(tmpNeedSaveValue
								.getRxDataFrames(), tmpDateStringValue));
						mesh_rec_mgtData.add(new CheckItem(tmpNeedSaveValue
								.getRxMgtFrames(), tmpDateStringValue));
						mesh_rec_unicastData.add(new CheckItem(tmpNeedSaveValue
								.getRxUnicastFrames(), tmpDateStringValue));
						mesh_rec_multicastData.add(new CheckItem(tmpNeedSaveValue
								.getRxMulticastFrames(), tmpDateStringValue));
						mesh_rec_broadcastData.add(new CheckItem(tmpNeedSaveValue
								.getRxBroadcastFrames(), tmpDateStringValue));
//							if (rssiCount == 0) {
//								rssiCount = 1;
//							}
						mesh_rssiData.add(new CheckItem((long) tmpNeedSaveValue.getRssi()-95
								, tmpDateStringValue));

						reportTimeInMillis = reportTimeInMillis + reportTimeAggregation;
						tmpDate.add(Calendar.HOUR_OF_DAY,
								(int) (reportTimeAggregation / 3600000));
						tmpNeedSaveValue = new AhNeighbor();
//							rssiCount = 0;
					}

					StringBuffer strOutput;
					if (!createCsvfile) {
						SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
						sfname.setTimeZone(tz);
						String mailFileName = profile.getReportTypeShow() + "-"
								+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
						File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
								+ File.separator + profile.getOwner().getDomainName()
								+ File.separator + mailFileName);
						out = new FileWriter(tmpFile);
						createCsvfile = true;
						strOutput = new StringBuffer();
						strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
						strOutput.append("NeighborAP Node,");
						strOutput.append("StatTime,");
						strOutput.append("Transmitted Total Data Frames,");
						strOutput.append("Transmitted WMM Best Effort Data Frames,");
						strOutput.append("Transmitted WMM Background Data Frames,");
						strOutput.append("Transmitted WMM Video Data Frames,");
						strOutput.append("Transmitted WMM Voice Data Frames,");
						strOutput.append("Transmitted Mgt Frames,");
						strOutput.append("Transmitted Unicast Data Frames,");
						// strOutput.append("TxMulticastDataFrames,");
						// strOutput.append("TxBroadcastDataFrames,");
						strOutput.append("Received Total Data Frames,");
						strOutput.append("Received Mgt Frames,");
						strOutput.append("Received Unicast Data Frames,");
						strOutput.append("Received Multicast Data Frames,");
						strOutput.append("Received Broadcast Data Frames,");
						strOutput.append("RSSI");
						strOutput.append("\n");
						out.write(strOutput.toString());
					}

					strOutput = new StringBuffer();
					for (int cnt = 0; cnt < mesh_trans_totalData.size(); cnt++) {
						strOutput.append(reportAPName).append(",");
						strOutput.append(neighborAPMap.get(reportAPName).get(k)).append(",");
						strOutput.append(mesh_trans_totalData.get(cnt).getValue()).append(",");
						strOutput.append(mesh_trans_totalData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_beData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_bgData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_viData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_voData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_mgtData.get(cnt).getId()).append(",");
						strOutput.append(mesh_trans_unicastData.get(cnt).getId()).append(",");
						// strOutput.append(mesh_trans_multicastData.get(cnt)
						// .getId()
						// + ",");
						// strOutput.append(mesh_trans_broadcastData.get(cnt)
						// .getId()
						// + ",");
						strOutput.append(mesh_rec_totalData.get(cnt).getId()).append(",");
						strOutput.append(mesh_rec_mgtData.get(cnt).getId()).append(",");
						strOutput.append(mesh_rec_unicastData.get(cnt).getId()).append(",");
						strOutput.append(mesh_rec_multicastData.get(cnt).getId()).append(",");
						strOutput.append(mesh_rec_broadcastData.get(cnt).getId()).append(",");
						strOutput.append(mesh_rssiData.get(cnt).getId());
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setMeshNeighbors():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setClientVendor(AhReport profile, TimeZone tz) {
		try {
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			FilterParams filterParams;
			if (profile.getLocation() != null) {
				filterParams = new FilterParams(
						"bo.endTimeStamp>=:s1 and bo.mapId=:s2 and lower(bo.apName) like :s3",
						new Object[] { reportDateTime, profile.getLocation().getId(),
								profile.getApNameForSQLTwo() });
			} else {
				filterParams = new FilterParams(
						"bo.endTimeStamp>=:s1 and lower(bo.apName) like :s2", new Object[] {
								reportDateTime, profile.getApNameForSQLTwo() });
			}
			List<?> profilesHistoryClient = QueryUtil.executeQuery(
					"select DISTINCT bo.clientMac from "
							+ AhClientSessionHistory.class.getSimpleName() + " bo", null,
					filterParams, profile.getOwner().getId());

			FilterParams activeFilterParams;
//			if (profile.getLocation() != null) {
//				activeFilterParams = new FilterParams(
//						"bo.mapId=:s1 and lower(bo.apName) like :s2",
//						new Object[] { profile.getLocation().getId(), profile.getApNameForSQLTwo() });
//			} else {
//				activeFilterParams = new FilterParams("lower(bo.apName) like :s1",
//						new Object[] { profile.getApNameForSQLTwo() });
//			}
//			List<?> profilesActiveClient = QueryUtil.executeQuery(
//					"select DISTINCT bo.clientMac from " + AhClientSession.class.getSimpleName()
//							+ " bo", null, activeFilterParams, profile.getOwner().getId());
			if (profile.getLocation() != null) {
				activeFilterParams = new FilterParams(
						"mapId=? and lower(apName) like ?",
						new Object[] { profile.getLocation().getId(), profile.getApNameForSQLTwo() });
			} else {
				activeFilterParams = new FilterParams("lower(apName) like ?",
						new Object[] { profile.getApNameForSQLTwo() });
			}
			List<?> profilesActiveClient = DBOperationUtil.executeQuery(
					"select DISTINCT clientMac from ah_clientsession",
					null, activeFilterParams, profile.getOwner().getId());

			Map<String, Integer> clientVendorCount = new HashMap<String, Integer>();
			Set<String> clientMacSet = new HashSet<String>();
			for (Object obj : profilesHistoryClient) {
				clientMacSet.add(obj.toString());
			}
			for (Object obj : profilesActiveClient) {
				clientMacSet.add(obj.toString());
			}

			for (String tempMacAll : clientMacSet) {
				String tempMac = tempMacAll.substring(0, 6);
				String macVendor = AhConstantUtil.getMacOuiComName(tempMac) == null ? tempMac : 
					AhConstantUtil.getMacOuiComName(tempMac);
				if (clientVendorCount.get(macVendor) == null) {
					clientVendorCount.put(macVendor, 1);
				} else {
					clientVendorCount.put(macVendor, clientVendorCount.get(macVendor) + 1);
				}
			}

			List<CheckItem> lstClientVendorCount = new ArrayList<CheckItem>();
			for (String clientMac : clientVendorCount.keySet()) {
				CheckItem tmpCheckItem = new CheckItem(
						clientVendorCount.get(clientMac).longValue(), clientMac);
				lstClientVendorCount.add(tmpCheckItem);
			}

			FileWriter out;
			StringBuffer strOutput;
			if (!lstClientVendorCount.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
				strOutput = new StringBuffer();
				strOutput.append("Start Time,");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.vendor")).append(",");
				strOutput.append(MgrUtil.getUserMessage("report.reportList.title.vendorCount"));
				strOutput.append("\n");
				out.write(strOutput.toString());
				strOutput = new StringBuffer();
				String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(System.currentTimeMillis(),
						tz);
				for (CheckItem vendorCount : lstClientVendorCount) {
					strOutput.append(tmpDateStringValue).append(",");
					strOutput.append("\"").append(vendorCount.getValue()).append("\",");
					strOutput.append(vendorCount.getId());
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientVendor():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setCompliance(AhReport profile, TimeZone tz) {
		try {
			List<ComplianceResult> lstCompliance = new ArrayList<ComplianceResult>();
			List<Short> notInList = new ArrayList<>();
			notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
			notInList.add(HiveAp.HIVEAP_MODEL_SR24);
			notInList.add(HiveAp.HIVEAP_MODEL_SR48);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2124P);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2148P);
			notInList.add(HiveAp.HIVEAP_MODEL_SR2024P);
			
			FilterParams filterParams;
			if (profile.getLocation() != null) {
				filterParams = new FilterParams(
						"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and lower(bo.hostName) like :s3 and " +
						"(bo.deviceType=:s4 or bo.deviceType=:s5 or bo.deviceType=:s6) and bo.hiveApModel not in :s7 ",
						new Object[] { profile.getLocation().getId(), HiveAp.STATUS_MANAGED,
								profile.getApNameForSQLTwo(), HiveAp.Device_TYPE_HIVEAP, 
								HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,
								notInList});
			} else {
				filterParams = new FilterParams(
						"bo.manageStatus=:s1 and lower(bo.hostName) like :s2 and " +
						"(bo.deviceType=:s3 or bo.deviceType=:s4 or bo.deviceType=:s5) and bo.hiveApModel!=:s6 ", new Object[] {
								HiveAp.STATUS_MANAGED, profile.getApNameForSQLTwo(), HiveAp.Device_TYPE_HIVEAP, 
								HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR,
								notInList});
			}

			List<HiveAp> profiles = QueryUtil.executeQuery(HiveAp.class, null, filterParams, profile.getOwner().getId());

			if (!profiles.isEmpty()) {
				CompliancePolicy compliancePolicy;
				List<CompliancePolicy> configData = QueryUtil.executeQuery(CompliancePolicy.class, null, null,
						profile.getOwner().getId());
				if (configData.size() == 0) {
					compliancePolicy = new CompliancePolicy();
				} else {
					compliancePolicy = QueryUtil.findBoById(
							CompliancePolicy.class, configData.get(0).getId());
				}

				List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null, null, 
						profile.getOwner().getId());
				HmStartConfig stg=  list.isEmpty() ? null : list.get(0);
				String globalDevicePwd=null;
				if (stg!=null) {
					globalDevicePwd = stg.getHiveApPassword();
				}
				
				for (Object object : profiles) {
					HiveAp hiveap = (HiveAp) object;
					ComplianceResult complianceResult = new ComplianceResult();
					complianceResult.setHiveApName(hiveap.getHostName());
					if (!compliancePolicy.getPasswordHiveap()) {
						complianceResult.setHiveApPass(ComplianceResult.PASSWORD_STRENGTH_NA);
					} else {
						if (hiveap.getCfgPassword() == null || hiveap.getCfgPassword().equals("")) {
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
							if (hiveap.getCfgReadOnlyUser() == null
									|| hiveap.getCfgReadOnlyUser().equals("")) {
								int adminPass = MgrUtil.checkPasswordStrength(hiveap
										.getCfgPassword());
								complianceResult.setHiveApPass(adminPass);
							} else {
								int reanonlyPass = MgrUtil.checkPasswordStrength(hiveap
										.getCfgReadOnlyPassword());
								int adminPass = MgrUtil.checkPasswordStrength(hiveap
										.getCfgPassword());
								complianceResult.setHiveApPass(reanonlyPass > adminPass ? adminPass
										: reanonlyPass);
							}
						}
					}
					if (!compliancePolicy.getPasswordCapwap()) {
						complianceResult.setCapwapPass(ComplianceResult.PASSWORD_STRENGTH_NA);
					} else {
						if (hiveap.getPassPhrase() == null || hiveap.getPassPhrase().equals("")) {
							complianceResult
									.setCapwapPass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
						} else {
							int capwapPass = MgrUtil.checkPasswordStrength(hiveap.getPassPhrase());
							complianceResult.setCapwapPass(capwapPass);
						}
					}

					hiveap = QueryUtil.findBoById(HiveAp.class, hiveap.getId(), this);

					HiveProfile hive = hiveap.getConfigTemplate().getHiveProfile();

					if (!compliancePolicy.getPasswordHive()) {
						complianceResult.setHivePass(ComplianceResult.PASSWORD_STRENGTH_NA);
					} else {
						if (!hive.getEnabledPassword()) {
							complianceResult
									.setHivePass(ComplianceResult.PASSWORD_STRENGTH_VERYWEAK);
						} else {
							int hivePass = MgrUtil.checkPasswordStrength(hive.getHivePassword());
							complianceResult.setHivePass(hivePass);
						}
					}
					ReportServiceFilter rsf = new ReportServiceFilter(hiveap, compliancePolicy);
					complianceResult.getSsidList().addAll(rsf.initServiceFilter());
//					if (!hiveap.getConfigTemplate().isOverrideTF4IndividualAPs() 
//							&& hiveap.getConfigTemplate().getDeviceServiceFilter()!=null) {
//						ServiceFilter defFilter = hiveap.getConfigTemplate().getDeviceServiceFilter();
//						hiveap.getConfigTemplate().setEth0ServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setEth0BackServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setWireServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setEth1ServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setEth1BackServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setRed0ServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setRed0BackServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setAgg0ServiceFilter(defFilter);
//						hiveap.getConfigTemplate().setAgg0BackServiceFilter(defFilter);
//					}
//					//xxxxxxx
//					ServiceFilter serviceFilter =hiveap.getConfigTemplate().getEth0ServiceFilter();
//					ServiceFilter serviceFilterBack =hiveap.getConfigTemplate().getEth0BackServiceFilter();
//					ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
//							serviceFilter,serviceFilterBack,compliancePolicy,"eth0");
//					complianceResult.getSsidList().add(ssidListInfo);
//					
//					if (hiveap.isEth1Available()){
//						serviceFilter =hiveap.getConfigTemplate().getEth1ServiceFilter();
//						serviceFilterBack =hiveap.getConfigTemplate().getEth1BackServiceFilter();
//						ssidListInfo = getComplianceSsidListInfo(
//								serviceFilter,serviceFilterBack,compliancePolicy,"eth1");
//						complianceResult.getSsidList().add(ssidListInfo);
//						
//						serviceFilter =hiveap.getConfigTemplate().getRed0ServiceFilter();
//						serviceFilterBack =hiveap.getConfigTemplate().getRed0BackServiceFilter();
//						ssidListInfo = getComplianceSsidListInfo(
//								serviceFilter,serviceFilterBack,compliancePolicy,"red0");
//						complianceResult.getSsidList().add(ssidListInfo);
//						
//						serviceFilter =hiveap.getConfigTemplate().getAgg0ServiceFilter();
//						serviceFilterBack =hiveap.getConfigTemplate().getAgg0BackServiceFilter();
//						ssidListInfo = getComplianceSsidListInfo(
//								serviceFilter,serviceFilterBack,compliancePolicy,"agg0");
//						complianceResult.getSsidList().add(ssidListInfo);
//					}
//
//					for (ConfigTemplateSsid configTemplateSsid : hiveap.getConfigTemplate()
//							.getSsidInterfaces().values()) {
//						if (configTemplateSsid.getSsidProfile() != null) {
//							SsidProfile sp = configTemplateSsid.getSsidProfile();
//							ssidListInfo = new ComplianceSsidListInfo();
//							ssidListInfo.setSsidName(sp.getSsidName());
//							switch (sp.getAccessMode()) {
//							case SsidProfile.ACCESS_MODE_OPEN:
//								if (sp.getMacAuthEnabled()) {
//									ssidListInfo
//											.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN_AUTH);
//									ssidListInfo.setRating(compliancePolicy.getClientOpenAuth());
//								} else {
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN);
//									ssidListInfo.setRating(compliancePolicy.getClientOpen());
//								}
//								break;
//							case SsidProfile.ACCESS_MODE_WPA:
//								ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PSK);
//								ssidListInfo.setRating(compliancePolicy.getClientPsk());
//								if (!compliancePolicy.getPasswordSSID()) {
//									ssidListInfo.setSsidPass(ComplianceResult.PASSWORD_STRENGTH_NA);
//								} else {
//									int ssidPass = MgrUtil.checkPasswordStrength(sp
//											.getSsidSecurity().getFirstKeyValue());
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
//							}
//							serviceFilter = sp.getServiceFilter();
//							if (serviceFilter.getEnableSSH()) {
//								ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
//							} else {
//								ssidListInfo
//										.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//							}
//							if (serviceFilter.getEnablePing()) {
//								ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
//							} else {
//								ssidListInfo
//										.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//							}
//							if (serviceFilter.getEnableTelnet()) {
//								ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
//							} else {
//								ssidListInfo
//										.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//							}
//							if (serviceFilter.getEnableSNMP()) {
//								ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
//							} else {
//								ssidListInfo
//										.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//							}
//							complianceResult.getSsidList().add(ssidListInfo);
//						}
//					}
					lstCompliance.add(complianceResult);
				}

				if (profile.getComplianceType() != AhReport.REPORT_COMPLIANCEPOLICY_ALL) {
					List<ComplianceResult> removeClass = new ArrayList<ComplianceResult>();
					for (ComplianceResult tmpClass : lstCompliance) {
						if (tmpClass.getSummarySecurity() != profile.getComplianceType()) {
							removeClass.add(tmpClass);
						}
					}
					lstCompliance.removeAll(removeClass);
				}

				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".pdf";
				String allPathName = AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName;
				generalCurrentPdfFile(lstCompliance, allPathName);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setCompliance():", e);
			log.error(e);
			return false;
		}
	}

	public ComplianceSsidListInfo getComplianceSsidListInfo(ServiceFilter serviceFilter,
			ServiceFilter serviceFilterBack,
			CompliancePolicy compliancePolicy, String name) {
		ComplianceSsidListInfo ssidListInfo = new ComplianceSsidListInfo();
		ssidListInfo.setSsidName(name);
		ssidListInfo.setRating(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		if (serviceFilter.getEnableSSH()|| serviceFilterBack.getEnableSSH()) {
			ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
		} else {
			ssidListInfo.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnablePing()|| serviceFilterBack.getEnablePing()) {
			ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
		} else {
			ssidListInfo.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableTelnet()|| serviceFilterBack.getEnableTelnet()) {
			ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
		} else {
			ssidListInfo.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		if (serviceFilter.getEnableSNMP()|| serviceFilterBack.getEnableSNMP()) {
			ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
		} else {
			ssidListInfo.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
		}
		return ssidListInfo;
	}

	public synchronized boolean generalCurrentPdfFile(List<ComplianceResult> lstCompliance,
			String filename) {
		Document document = new Document(PageSize.A4.rotate(),50,50,72,72);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
			writer.setPageEvent(new HeaderFooterPage());
			
			document.open();
			Font fonts = new Font(Font.COURIER, Font.DEFAULTSIZE, Font.BOLD);
			Font cellfonts = new Font(Font.COURIER, 11, Font.BOLD);
			Paragraph graph = new Paragraph();
			graph.setFont(fonts);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add(MgrUtil.getUserMessage("report.reportList.compliance.result"));
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			document.add(graph);

			// graph = new Paragraph();
			// graph.setAlignment(Element.ALIGN_LEFT);
			// graph.add(MgrUtil.getUserMessage("report.reportList.compliance.hmpassStrength"));
			// graph.add(hmpassStrength);
			// graph.setSpacingAfter(15f);
			// document.add(graph);

			PdfPTable table = new PdfPTable(2);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.setWidthPercentage(80);
			PdfPCell cell = new PdfPCell(new Paragraph(MgrUtil
					.getUserMessage("report.reportList.compliance.securityRating"), fonts));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(MgrUtil
					.getUserMessage("report.reportList.compliance.hiveApCount"), fonts));
			table.addCell(cell);

			int weakCount = 0;
			int acceptableCount = 0;
			int strongCount = 0;
			for (ComplianceResult result : lstCompliance) {
				switch (result.getSummarySecurity()) {
				case CompliancePolicy.COMPLIANCE_POLICY_POOR:
					weakCount++;
					break;
				case CompliancePolicy.COMPLIANCE_POLICY_GOOD:
					acceptableCount++;
					break;
				case CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT:
					strongCount++;
					break;
				}
			}
			table.addCell(MgrUtil.getUserMessage("report.reportList.compliance.weak"));
			table.addCell(String.valueOf(weakCount));
			table.addCell(MgrUtil.getUserMessage("report.reportList.compliance.moderate"));
			table.addCell(String.valueOf(acceptableCount));
			table.addCell(MgrUtil.getUserMessage("report.reportList.compliance.strong"));
			table.addCell(String.valueOf(strongCount));
			document.add(table);

			for (ComplianceResult result : lstCompliance) {
				com.lowagie.text.List hiveAps = new com.lowagie.text.List(false, 10);
				hiveAps.setListSymbol(new Chunk("\u2022", FontFactory.getFont(
						FontFactory.HELVETICA, 12, Font.BOLD)));

				ListItem listItem = new ListItem(NmsUtil.getOEMCustomer().getAccessPonitName() + " \"" + result.getHiveApName()
						+ "\" Detail Security", fonts);
				hiveAps.add(listItem);

				com.lowagie.text.List sublist = new com.lowagie.text.List(false, true, 10);
				sublist.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 10)));

				sublist.add(MgrUtil.getUserMessage("report.reportList.compliance.hivePass") + ": "
						+ result.getHivePassString());
				sublist.add(MgrUtil.getUserMessage("report.reportList.compliance.hiveApPass")
						+ ": " + result.getHiveApPassString());
				sublist.add(MgrUtil.getUserMessage("report.reportList.compliance.capwapPass")
						+ ": " + result.getCapwapPassString());
				sublist.add(MgrUtil.getUserMessage("report.reportList.compliance.ssidSecurity")
						+ ": ");

				hiveAps.add(sublist);
				graph = new Paragraph();
				graph.setAlignment(Element.ALIGN_LEFT);
				graph.setSpacingAfter(10f);
				graph.setSpacingBefore(10f);
				graph.add(hiveAps);
				document.add(graph);

				float[] widths = { 0.2f, 0.2f, 0.1f, 0.075f, 0.075f, 0.075f, 0.075f, 0.2f };
				table = new PdfPTable(widths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.ssidName"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.accessSecurity"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.securityRating"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.ssh"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.telnet"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.ping"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.snmp"), cellfonts));
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(MgrUtil
						.getUserMessage("report.reportList.compliance.pskPass"), cellfonts));
				table.addCell(cell);

				for (ComplianceSsidListInfo ssidlistInfo : result.getSsidList()) {
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
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
			log.error(de);
			return false;
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			log.error(ioe);
			return false;
		}
		document.close();
		return true;
	}

	public boolean setInventory(AhReport profile, TimeZone tz) {
		List<Short> cvgList = new ArrayList<>();
		cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		
		FilterParams currentFilterParams;
		if (profile.getLocation() != null) {
			currentFilterParams = new FilterParams(
					"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and bo.deviceType!=:s3 and bo.hiveApModel not in :s4", new Object[] {
							profile.getLocation().getId(), HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY, cvgList });
		} else {
			currentFilterParams = new FilterParams("bo.manageStatus=:s1 and bo.deviceType!=:s2 and bo.hiveApModel not in :s3",
					new Object[] { HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY, cvgList });
		}
		List<HiveAp> lstInventory = QueryUtil.executeQuery(HiveAp.class, new SortParams("hostName"),
				currentFilterParams, profile.getOwner().getId(), new QueryBo() {
	        @Override
	        public Collection<HmBo> load(HmBo bo) {
	            if (bo instanceof HiveAp) {
	            	HiveAp ap = (HiveAp) bo;
	                if (ap.getMapContainer() != null){
	                	ap.getMapContainer();
	                	if (ap.getMapContainer().getParentMap()!=null) {
		                	ap.getMapContainer().getParentMap().getId();
		                }
	                }
	            }
	            return null;
	        }
		});
		try {
			FileWriter out;
			StringBuffer strOutput;

			if (!lstInventory.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
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
				String tmpDateStringValue = AhDateTimeUtil.getSpecifyDateTimeReport(System
						.currentTimeMillis(), tz);
				for (HiveAp hiveAp : lstInventory) {
					strOutput.append(tmpDateStringValue).append(",");
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
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setInventory():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setClientAuth(AhReport profile, TimeZone tz) {
		FilterParams currentFilterParams = setClientAuthFilterParam(profile, tz);

		List<AhEvent> lstClientAuth = QueryUtil.executeQuery(AhEvent.class, new SortParams("remoteId"),
				currentFilterParams, profile.getOwner().getId());
		try {
			FileWriter out;
			StringBuffer strOutput;

			if (!lstClientAuth.isEmpty()) {
				SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
				sfname.setTimeZone(tz);
				String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
						+ sfname.format(new Date()) + ".csv";
				File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
						+ profile.getOwner().getDomainName() + File.separator + mailFileName);
				out = new FileWriter(tmpFile);
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
			} else {
				return false;
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientAuth():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setHiveAPSlaCompliance(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);

			String searchSQL = "select id,hostName from " + HiveAp.class.getSimpleName() + " bo where ";
			searchSQL = searchSQL + " manageStatus = " + HiveAp.STATUS_MANAGED;
			searchSQL = searchSQL + " and (deviceType=" + HiveAp.Device_TYPE_HIVEAP + 
					" or deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
					" or deviceType=" + HiveAp.Device_TYPE_VPN_BR + 
					") and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_VPN_GATEWAY +					
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2124P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2024P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR2148P +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR24 +
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_SR48 + 
			" and hiveApModel!=" + HiveAp.HIVEAP_MODEL_BR200;

			if (profile.getApName() != null && !profile.getApName().equals("")) {
				searchSQL = searchSQL + " and lower(hostName) like '%"
						+ profile.getApNameForSQL().toLowerCase() + "%'";
			}
			if (profile.getLocation() != null) {
				searchSQL = searchSQL + "and mapContainer.id = "
						+ profile.getLocation().getId().toString();
			}
			searchSQL = searchSQL + " AND owner.id=" + profile.getOwner().getId();

			List<?> profilesIds = QueryUtil.executeQuery(searchSQL, null, null);

			for (Object obj : profilesIds) {
				Object[] tmp = (Object[]) obj;

				List<AhBandWidthSentinelHistory> lstHiveApSla = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
						new SortParams("timeStamp.time"), new FilterParams(
								"apName=:s1 and timeStamp.time>=:s2", new Object[]{
										tmp[1].toString(), reportDateTime.getTimeInMillis()}),
						profile.getOwner().getId());
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-" + profile.getName()
							+ "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					strOutput = new StringBuffer();

					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceMac")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.title.currentClientMac")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.status")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.title.time")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.sla.configBandwidth")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.sla.actualBandwidth"));
					strOutput.append("\n");
					out.write(strOutput.toString());
				}

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < lstHiveApSla.size(); cnt++) {
					AhBandWidthSentinelHistory tmpClass = lstHiveApSla
							.get(cnt);
					strOutput.append(tmpClass.getApName()).append(",");
					strOutput.append(tmpClass.getApMac()).append(",");
					strOutput.append(tmpClass.getClientMac()).append(",");
					strOutput.append(tmpClass.getBandWidthSentinelStatusString()).append(",");
					String timeString = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass
							.getTimeStamp().getTime(), tz);
					strOutput.append(timeString).append(",");
					strOutput.append(tmpClass.getGuaranteedBandWidth()).append(",");
					strOutput.append(tmpClass.getActualBandWidth());
					strOutput.append("\n");
					if (cnt % 1000 == 0 && cnt != 0) {
						out.write(strOutput.toString());
						strOutput = new StringBuffer();
					}
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setHiveAPSlaCompliance():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public boolean setClientSlaCompliance(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();

			String searchSQL = "select DISTINCT clientMac from Ah_ClientSession_History where "
					+ " startTimeStamp >=" + reportDateTime + " AND lower(apName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'";

			if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientMac) like '%"
						+ profile.getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientHostname) like '%"
						+ profile.getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientUsername) like '%"
						+ profile.getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
				searchSQL = searchSQL + " AND lower(clientIP) like '%"
						+ profile.getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchSQL = searchSQL + " AND mapId = " + profile.getLocation().getId();
			}

			searchSQL = searchSQL + " AND owner = " + profile.getOwner().getId();

			String searchSQLSec = " select DISTINCT clientMac from Ah_ClientSession where "
					+ " lower(apName) like '%" + profile.getApNameForSQL().toLowerCase() + "%'";

			if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientMac) like '%"
						+ profile.getAuthMacForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientHostname) like '%"
						+ profile.getAuthHostNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientUsername) like '%"
						+ profile.getAuthUserNameForSQLTwo().toLowerCase() + "%'";
			}
			if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
				searchSQLSec = searchSQLSec + " AND lower(clientIP) like '%"
						+ profile.getAuthIpForSQLTwo().toLowerCase() + "%'";
			}

			if (profile.getLocation() != null) {
				searchSQLSec = searchSQLSec + " AND mapId = " + profile.getLocation().getId();
			}

			searchSQLSec = searchSQLSec + " AND owner = " + profile.getOwner().getId();

//			List<?> mapProfiles = QueryUtil.executeNativeQuery(searchSQL + searchSQLSec);
//
//			for (Object obj : mapProfiles) {
			Set<Object> mapProfiles = new HashSet<Object>();
			
			List<?> rsList = QueryUtil.executeNativeQuery(searchSQL);
			mapProfiles.addAll(rsList);
			rsList = DBOperationUtil.executeQuery(searchSQLSec);
			mapProfiles.addAll(rsList);

			for (Object obj : mapProfiles) {
				List<AhBandWidthSentinelHistory> lstHiveApSla = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
						new SortParams("timeStamp.time"), new FilterParams(
								"clientMac=:s1 and timeStamp.time>=:s2", new Object[] {
										obj.toString(), reportDateTime }), profile
								.getOwner().getId());
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-" + profile.getName()
							+ "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();

					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceMac")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.title.currentClientMac")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.sla.status")).append(",");
					strOutput.append(MgrUtil.getUserMessage("report.reportList.title.time")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.sla.configBandwidth")).append(",");
					strOutput.append(MgrUtil
							.getUserMessage("report.reportList.sla.actualBandwidth"));
					strOutput.append("\n");
					out.write(strOutput.toString());
				}

				strOutput = new StringBuffer();
				for (int cnt = 0; cnt < lstHiveApSla.size(); cnt++) {
					AhBandWidthSentinelHistory tmpClass = lstHiveApSla
							.get(cnt);
					strOutput.append(tmpClass.getApName()).append(",");
					strOutput.append(tmpClass.getApMac()).append(",");
					strOutput.append(tmpClass.getClientMac()).append(",");
					strOutput.append(tmpClass.getBandWidthSentinelStatusString()).append(",");
					String timeString = AhDateTimeUtil.getSpecifyDateTimeReport(tmpClass
							.getTimeStamp().getTime(), tz);
					strOutput.append(timeString).append(",");
					strOutput.append(tmpClass.getGuaranteedBandWidth()).append(",");
					strOutput.append(tmpClass.getActualBandWidth());
					strOutput.append("\n");
					if (cnt % 1000 == 0 && cnt != 0) {
						out.write(strOutput.toString());
						strOutput = new StringBuffer();
					}
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setClientSlaCompliance():", e);
			log.error(e);
			return false;
		}
		return true;
	}

	public FilterParams setClientAuthFilterParam(AhReport profile,  TimeZone tz) {
		long reportDateTime = getReportDateTime(profile.getReportPeriod(), tz).getTimeInMillis();
		List<Object> lstCondition = new ArrayList<Object>();
		String searchSQL = "trapTimeStamp.time >=:s1";
		lstCondition.add(reportDateTime);
		int intParam;
		if (profile.getAuthType() == AhReport.REPORT_CLIENTAUTH_AUTH) {
			searchSQL = searchSQL + " and eventType =:s2 and objectType=:s3 and currentState=:s4";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_UP);
			intParam = 5;
		} else if (profile.getAuthType() == AhReport.REPORT_CLIENTAUTH_DEAUTH) {
			searchSQL = searchSQL
					+ " and eventType =:s2 and objectType=:s3 and currentState=:s4 and code!=:s5";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_DOWN);
			// de-auth code
			lstCondition.add(ReportListAction.CLIENT_DE_AUTH_CODE);
			intParam = 6;
		} else if (profile.getAuthType() == AhReport.REPORT_CLIENTAUTH_REJECT) {
			searchSQL = searchSQL
					+ " and eventType =:s2 and objectType=:s3 and currentState=:s4 and code=:s5";
			lstCondition.add(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
			lstCondition.add(AhEvent.AH_OBJECT_TYPE_CLIENT_LINK);
			lstCondition.add(AhEvent.AH_STATE_DOWN);
			// de-auth code
			lstCondition.add(ReportListAction.CLIENT_DE_AUTH_CODE);
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

		if (profile.getLocation() != null) {
			List<?> mapAP = QueryUtil.executeQuery("select upper(bo.macAddress) from "
					+ HiveAp.class.getSimpleName() + " bo", null, new FilterParams(
					"bo.mapContainer.id=:s1 and bo.manageStatus=:s2 and (bo.deviceType=:s3 or bo.deviceType=:s4 or bo.deviceType=:s5)", new Object[] {
							profile.getLocation().getId(), HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_HIVEAP, HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR }));
			if (!mapAP.isEmpty()) {
				searchSQL = searchSQL + " AND upper(apId) in (:s" + intParam + ")";
				lstCondition.add(mapAP);
			} else {
				searchSQL = searchSQL + " AND apId = :s" + intParam;
				lstCondition.add("notexist");
			}
			intParam++;
		}

		if (profile.getAuthMac() != null && !profile.getAuthMac().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(remoteId) like :s" + intParam;
			lstCondition.add("%" + profile.getAuthMacForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		if (profile.getAuthHostName() != null && !profile.getAuthHostName().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientHostName) like :s" + intParam;
			lstCondition.add("%" + profile.getAuthHostNameForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		if (profile.getAuthUserName() != null && !profile.getAuthUserName().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientUserName) like :s" + intParam;
			lstCondition.add("%" + profile.getAuthUserNameForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		if (profile.getAuthIp() != null && !profile.getAuthIp().trim().equals("")) {
			searchSQL = searchSQL + " AND lower(clientIp) like :s" + intParam;
			lstCondition.add("%" + profile.getAuthIpForSQL().trim().toLowerCase() + "%");
			intParam++;
		}

		Object values[] = new Object[lstCondition.size()];
		for (int i = 0; i < lstCondition.size(); i++) {
			values[i] = lstCondition.get(i);
		}

		return new FilterParams(searchSQL, values);
	}
	
	public boolean generalCurrentNonHiveAPCsvFile(AhReport profile, TimeZone tz){
		try {
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			FilterParams filterSLAHistory = new FilterParams(
						"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) and lower(apName) like :s4",
						new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
								,AhBandWidthSentinelHistory.STATUS_ALERT, profile.getApNameForSQLTwo()});
			List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					new SortParams("apName"), filterSLAHistory, profile.getOwner().getId());
			FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and alarmFlag>:s2 and lower(apName) like :s3",
					new Object[] { reportDateTime.getTimeInMillis(),0,profile.getApNameForSQLTwo() });
			List<AhInterfaceStats> interfaceStatsHistory =QueryUtil.executeQuery(AhInterfaceStats.class, new SortParams("apName"),
					filterInterfaceHistory,profile.getOwner().getId());
			SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
			sfname.setTimeZone(tz);
			String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
					+ sfname.format(new Date()) + ".csv";
			File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
					+ profile.getOwner().getDomainName() + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);
			
			StringBuffer strOutput = new StringBuffer();
			
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
			for (AhInterfaceStats oneItem:interfaceStatsHistory){
				strOutput.append(oneItem.getApName()).append(",");
				strOutput.append(oneItem.getIfName()).append(",");
				strOutput.append(oneItem.getCrcErrorRate()).append(",");
				strOutput.append(getPencentageValue(oneItem.getTxDrops(),oneItem.getUniTxFrameCount() + oneItem.getBcastTxFrameCount())).append(",");
				strOutput.append(getPencentageValue(oneItem.getRxDrops(),oneItem.getUniRxFrameCount() + oneItem.getBcastRxFrameCount())).append(",");
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
			log.error(ex);
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
	
	public boolean generalCurrentNonClientCsvFile(AhReport profile, TimeZone tz){
		try {
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			FilterParams filterSLAHistory = new FilterParams(
						"timeStamp.time>:s1 and (bandWidthSentinelStatus=:s2 or bandWidthSentinelStatus=:s3) " +
						"and lower(apName) like :s4 and lower(clientMac) like :s5",
						new Object[] { reportDateTime.getTimeInMillis(), AhBandWidthSentinelHistory.STATUS_BAD
								,AhBandWidthSentinelHistory.STATUS_ALERT, profile.getApNameForSQLTwo(),
								"%" + profile.getAuthMacForSQL().toLowerCase()+ "%"});
			List<AhBandWidthSentinelHistory> slaHistory = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					new SortParams("clientMac"), filterSLAHistory, profile.getOwner().getId());
	
			
			FilterParams filterInterfaceHistory = new FilterParams("timeStamp>:s1 and (alarmFlag>:s2 or overallClientHealthScore<:s3) and " +
					"lower(apName) like :s4 and lower(clientMac) like :s5",
					new Object[] { reportDateTime.getTimeInMillis(),0,AhClientSession.CLIENT_SCORE_RED,profile.getApNameForSQLTwo(),
									"%" + profile.getAuthMacForSQL().toLowerCase()+ "%"});
			List<AhClientStats> interfaceStatsHistory =QueryUtil.executeQuery(AhClientStats.class,
					new SortParams("clientMac"), filterInterfaceHistory,profile.getOwner().getId());
			
			SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
			sfname.setTimeZone(tz);
			String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
					+ sfname.format(new Date()) + ".csv";
			File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath + File.separator
					+ profile.getOwner().getDomainName() + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);
			StringBuffer strOutput = new StringBuffer();
			
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
			strOutput.append("Client MAC Address,");
			strOutput.append("SLA Events,");
			strOutput.append("Guaranteed Throughput (Kbps),");
			strOutput.append("Actual Throughput (Kbps),");
			strOutput.append("Report Time");
			strOutput.append("\n");
			out.write(strOutput.toString());
			
			strOutput = new StringBuffer();
			int printCount=0;
			for (AhBandWidthSentinelHistory oneItem:slaHistory){
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
			strOutput.append(MgrUtil.getUserMessage("report.reportList.deviceName")).append(",");
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
			for (AhClientStats oneItem:interfaceStatsHistory){
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
			log.error(ex);
			return false;
		}
	}
	
	public boolean setVpnAvailablity(AhReport profile, TimeZone tz) {
//		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;

			String searchSQL = "select a.hostname,a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
			" config_template b, " + 
			" VPN_SERVICE c, " +
			"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
			"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
			"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
			"and c.id= d.VPN_GATEWAY_SETTING_ID " +
			"and lower(a.hostName) like '%" + 
			profile.getApNameForSQL().toLowerCase() + "%'" +
			" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
			" and a.simulated=false" + 
			" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" AND a.owner=" + profile.getOwner().getId();
			
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();
//			values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);

			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
						tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
					}
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				
				searchSQL = "lower(mac)=:s1 and interfType=:s2";
				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Tunnel Name,");
					strOutput.append("Server,");
					strOutput.append("Destination Name,");
					strOutput.append("Status,");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				byte recStatus;
				String recServerIp;
				String name;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsLatencyHigh)OneRec).getTime();
						recStatus = ((AhStatsLatencyHigh)OneRec).getTargetStatus();
						recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
						name = ((AhStatsLatencyHigh)OneRec).getName();
					} else {
						recTime = ((AhStatsLatencyLow)OneRec).getTime();
						recStatus = ((AhStatsLatencyLow)OneRec).getTargetStatus();
						recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
						name = ((AhStatsLatencyLow)OneRec).getName();
					}
				
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)==null? recServerIp : tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)).append(",");
					strOutput.append(recServerIp).append(",");
					strOutput.append(name).append(",");
					strOutput.append(recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP? "Up" : "Down").append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setVpnThroughput(AhReport profile, TimeZone tz) {
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;

			String searchSQL = "select a.hostname,a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
			" config_template b, " + 
			" VPN_SERVICE c, " +
			"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
			"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
			"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
			"and c.id= d.VPN_GATEWAY_SETTING_ID " +
			"and lower(a.hostName) like '%" + 
			profile.getApNameForSQL().toLowerCase() + "%'" +
			" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
			" and a.simulated=false" + 
			" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" AND a.owner=" + profile.getOwner().getId();
			
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();
			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
						tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
					}
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				searchSQL = "lower(mac)=:s1 and interfType=:s2";
				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Tunnel Name,");
					strOutput.append("Server,");
					strOutput.append("Data In (kbps),");
					strOutput.append("Data Out (kbps),");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				String dataIn;
				String dataOut;
				String recServerIp;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsThroughputHigh)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputHigh)OneRec).getRxBytes()*8/(float)1024/60);
						dataOut = df.format(((AhStatsThroughputHigh)OneRec).getTxBytes()*8/(float)1024/60);
						recServerIp = ((AhStatsThroughputHigh)OneRec).getInterfServer();
					} else {
						recTime = ((AhStatsThroughputLow)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputLow)OneRec).getRxBytes()*8/(float)1024/3600);
						dataOut = df.format(((AhStatsThroughputLow)OneRec).getTxBytes()*8/(float)1024/3600);
						recServerIp = ((AhStatsThroughputLow)OneRec).getInterfServer();
					}
				
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)==null? recServerIp : tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)).append(",");
					strOutput.append(recServerIp).append(",");
					strOutput.append(dataIn).append(",");
					strOutput.append(dataOut).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	
	public boolean setVpnLatency(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;

			String searchSQL = "select a.hostname,a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
			" config_template b, " + 
			" VPN_SERVICE c, " +
			"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
			"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
			"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
			"and c.id= d.VPN_GATEWAY_SETTING_ID " +
			"and lower(a.hostName) like '%" + 
			profile.getApNameForSQL().toLowerCase() + "%'" +
			" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
			" and a.simulated=false" + 
			" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
			" AND a.owner=" + profile.getOwner().getId();
			
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();
			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
						tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
					}
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				searchSQL = "lower(mac)=:s1 and interfType=:s2";
				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Tunnel Name,");
					strOutput.append("Server,");
					strOutput.append("Destination Name,");
					strOutput.append("Rtt (msec),");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				double rtt;
				String recServerIp;
				String name;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsLatencyHigh)OneRec).getTime();
						if (((AhStatsLatencyHigh)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_DOWN) {
							rtt = -1;
						} else {
							rtt = ((AhStatsLatencyHigh)OneRec).getRtt();
						}
						recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
						name= ((AhStatsLatencyHigh)OneRec).getName();
					} else {
						recTime = ((AhStatsLatencyLow)OneRec).getTime();
						if (((AhStatsLatencyLow)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_DOWN) {
							rtt = -1;
						} else {
							rtt = ((AhStatsLatencyLow)OneRec).getRtt();
						}
						recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
						name= ((AhStatsLatencyHigh)OneRec).getName();
					}
				
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)==null? recServerIp : tunnelNameIPMap.get(oneAP.toString()+ "-_-" + recServerIp)).append(",");
					strOutput.append(recServerIp).append(",");
					strOutput.append(name).append(",");
					strOutput.append(rtt).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setWanAvailablity(AhReport profile, TimeZone tz) {
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			String searchSQL;
			if (profile.getReportType().equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)) {
				searchSQL = "select a.hostname,a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
				" config_template b, " + 
				" VPN_SERVICE c, " +
				"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
				"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
				"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
				"and c.id= d.VPN_GATEWAY_SETTING_ID " +
				"and lower(a.hostName) like '%" + 
				profile.getApNameForSQL().toLowerCase() + "%'" +
				" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
				" and a.simulated=false" + 
				" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
				" AND a.owner=" + profile.getOwner().getId();
			} else {
				searchSQL = "select DISTINCT hostName,macAddress from hive_ap where "
					+ " lower(hostName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'"
					+ " and manageStatus=" + HiveAp.STATUS_MANAGED 
					+ " and simulated=false"  
					+ " and (deviceType=" + HiveAp.Device_TYPE_VPN_GATEWAY
					+ " or deviceType=" + HiveAp.Device_TYPE_VPN_BR
					+ " )";
				searchSQL = searchSQL + " AND owner=" + profile.getOwner().getId();
			}
			
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
//			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();
			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
//					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
//						tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
//					}
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				searchSQL = "lower(mac)=:s1";
				Object values[] = new Object[1];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Interface Name,");
					strOutput.append("Status,");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				byte recStatus;
				String recName;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsAvailabilityHigh)OneRec).getTime();
						recStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfStatus();
						recName = ((AhStatsAvailabilityHigh)OneRec).getInterfName();
					} else {
						recTime = ((AhStatsAvailabilityLow)OneRec).getTime();
						recStatus = ((AhStatsAvailabilityLow)OneRec).getInterfStatus();
						recName = ((AhStatsAvailabilityLow)OneRec).getInterfName();
					}
				
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(recName).append(",");
					strOutput.append(recStatus==AhPortAvailability.INTERFACE_STATUS_UP? "Up" : "Down").append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setWanThroughput(AhReport profile, TimeZone tz) {
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			String searchSQL;
			if (profile.getReportType().equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)) {
				searchSQL = "select a.hostname, a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
				" config_template b, " + 
				" VPN_SERVICE c, " +
				"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
				"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
				"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
				"and c.id= d.VPN_GATEWAY_SETTING_ID " +
				"and lower(a.hostName) like '%" + 
				profile.getApNameForSQL().toLowerCase() + "%'" +
				" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
				" and a.simulated=false" +
				" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
				" AND a.owner=" + profile.getOwner().getId();
			} else {
				searchSQL = "select DISTINCT hostName,macAddress from hive_ap where "
					+ " lower(hostName) like '%"
					+ profile.getApNameForSQL().toLowerCase() + "%'"
					+ " and manageStatus=" + HiveAp.STATUS_MANAGED 
					+ " and simulated=false"  
					+ " and (deviceType=" + HiveAp.Device_TYPE_VPN_GATEWAY
					+ " or deviceType=" + HiveAp.Device_TYPE_VPN_BR
					+ " )";
				searchSQL = searchSQL + " AND owner=" + profile.getOwner().getId();
				
			}
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
//			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();
			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(),tmp[1].toString());
//					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
//						tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
//					}
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				searchSQL = "lower(mac)=:s1 and interfType=:s2";
				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Interface Name,");
					strOutput.append("Data In (kbps),");
					strOutput.append("Data Out (kbps),");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				String dataIn;
				String dataOut;
				String ifName;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsThroughputHigh)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputHigh)OneRec).getRxBytes()*8/(float)1024/60);
						dataOut = df.format(((AhStatsThroughputHigh)OneRec).getTxBytes()*8/(float)1024/60);
						ifName = ((AhStatsThroughputHigh)OneRec).getInterfName();
					} else {
						recTime = ((AhStatsThroughputLow)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputLow)OneRec).getRxBytes()*8/(float)1024/3600);
						dataOut = df.format(((AhStatsThroughputLow)OneRec).getTxBytes()*8/(float)1024/3600);
						ifName = ((AhStatsThroughputLow)OneRec).getInterfName();
					}
				
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(ifName).append(",");
					strOutput.append(dataIn).append(",");
					strOutput.append(dataOut).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	public boolean setGwVpnAvailablity(AhReport profile, TimeZone tz) {

//		DecimalFormat df = new DecimalFormat("0.00");
		try {
			boolean createCsvfile = false;
			FileWriter out = null;
			Map<String, String> hiveApNameTunnelCountMap = new HashMap<String, String>();
			Map<String, String> hiveApIdTunnelCountMap = new HashMap<String, String>();
			String vpnCountSql = 
				"select c.hiveapid, count(c.hiveapid) from vpn_service a, vpn_service_credential b, " +
				"vpn_gateway_setting c where a.id=b.vpn_service_id and a.id=c.vpn_gateway_setting_id " + 
				"and a.ipsecvpntype=4 and b.allocatedStatus="+ VpnServiceCredential.ALLOCATED_STATUS_USED +
				" and a.owner=" + profile.getOwner().getId() + " group by c.hiveapid";
//				"and a.ipsecvpntype=4 and b.allocated=true and a.owner=" + getDomain().getId() + " group by c.hiveapid";
				List<?> profilesIds = QueryUtil.executeNativeQuery(vpnCountSql);
				
			for(Object oneApObj: profilesIds){
				Object[] singleObj = (Object[])oneApObj;
				hiveApIdTunnelCountMap.put(singleObj[0].toString(), singleObj[1].toString());
			}
			
			String searchSQL = "select DISTINCT hostName,macAddress, id from hive_ap where "
				+ " lower(hostName) like '%"
				+ profile.getApNameForSQL().toLowerCase() + "%'"
				+ " and manageStatus=" + HiveAp.STATUS_MANAGED 
				+ " and simulated=false" 
				+ " and (deviceType=" + HiveAp.Device_TYPE_VPN_GATEWAY
				+ " or deviceType=" + HiveAp.Device_TYPE_VPN_BR
				+ ")";
			searchSQL = searchSQL + " AND owner=" + profile.getOwner().getId();
			
			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		
			if (profiles.isEmpty()) {
				return false;
			}
			
			TreeSet<String> lstHiveAPName = new TreeSet<String>();
			Map<String,String> hiveApNameMacMap = new HashMap<String, String>();

			for (Object onePro : profiles) {
				Object[] tmp = (Object[]) onePro;
				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					lstHiveAPName.add(tmp[0].toString());
					hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
					String tunnelCount= hiveApIdTunnelCountMap.get(tmp[2].toString());
					hiveApNameTunnelCountMap.put(tmp[0].toString(), tunnelCount==null?"0":tunnelCount);
				}
			}

			for(Object oneAP: lstHiveAPName){
				//TODO
				if (hiveApNameMacMap.get(oneAP.toString())==null) {
					continue;
				}
				if (hiveApNameTunnelCountMap.get(oneAP.toString())==null 
						|| hiveApNameTunnelCountMap.get(oneAP.toString()).equals("0")) {
					continue;
				}
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(oneAP.toString()), profile.getReportPeriod());
				if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
					continue;
				}
				
				searchSQL = "lower(mac)=:s1";
				Object values[] = new Object[1];
				values[0] = hiveApNameMacMap.get(oneAP.toString())==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(oneAP.toString());
				List<?> lstInterfaceInfo;
				if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusHigh.class, new SortParams("time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusLow.class, new SortParams("time"),
							new FilterParams(searchSQL, values), profile.getOwner().getId());
				}
				if (lstInterfaceInfo.isEmpty()) {
					continue;
				}
				StringBuffer strOutput;
				if (!createCsvfile) {
					SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
					sfname.setTimeZone(tz);
					String mailFileName = profile.getReportTypeShow() + "-"
							+ profile.getName() + "-" + sfname.format(new Date()) + ".csv";
					File tmpFile = new File(AhPerformanceScheduleModule.fileDirPath
							+ File.separator + profile.getOwner().getDomainName()
							+ File.separator + mailFileName);
					out = new FileWriter(tmpFile);
					createCsvfile = true;
					strOutput = new StringBuffer();
					strOutput.append("Device Name").append(",");
					strOutput.append("Mac Address,");
					strOutput.append("Tunnels Count,");
					strOutput.append("UP Tunnels Count,");
					strOutput.append("Report Time");
					strOutput.append("\n");
					out.write(strOutput.toString());
				}
				strOutput = new StringBuffer();
				long recTime;
				int recTunnelCount;
				String recMac;
				for(Object OneRec : lstInterfaceInfo) {
					if (profile.getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsVpnStatusHigh)OneRec).getTime();
						recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();
						recMac = ((AhStatsVpnStatusHigh)OneRec).getMac();
					} else {
						recTime = ((AhStatsVpnStatusLow)OneRec).getTime();
						recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();
						recMac = ((AhStatsVpnStatusLow)OneRec).getMac();
					}
					strOutput.append(oneAP.toString()).append(",");
					strOutput.append(recMac).append(",");
					strOutput.append(hiveApNameTunnelCountMap.get(oneAP.toString())).append(",");
					strOutput.append(recTunnelCount).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}

			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformScheduleImpl.setVpnAvailablity():", e);
			log.error(e);
			return false;
		}
		return true;
	
	}
	
	public boolean setConfigAudits(AhReport profile, TimeZone tz) {
		return true;
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

	public String convertRateToM(int rateValue){
		if (rateValue%1000==0) {
			return String.valueOf(rateValue/1000);
		} else {
			return String.valueOf(((float)rateValue)/1000);
		}
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
	
	
	public boolean setPCICompliance(AhReport profile, TimeZone tz) {

		try {
			String currentDir = AhPerformanceScheduleModule.fileDirPath + File.separator
					+ profile.getOwner().getDomainName();
			SimpleDateFormat sfname = new SimpleDateFormat("yyyyMMddHH");
			sfname.setTimeZone(tz);
			String mailFileName = profile.getReportTypeShow() + "-" + profile.getName() + "-"
					+ sfname.format(new Date()) + ".pdf";
			
			Calendar reportDateTime = getReportDateTime(profile.getReportPeriod(), tz);
			Calendar currentTime = Calendar.getInstance(tz);
			currentTime.clear(Calendar.MILLISECOND);
			currentTime.clear(Calendar.SECOND);
			currentTime.clear(Calendar.MINUTE);
			long endTimeOfSystem= currentTime.getTimeInMillis();
			
			PCIReportGenerateImpl geImp = new PCIReportGenerateImpl(
					profile, tz,currentDir,mailFileName,reportDateTime.getTimeInMillis(),endTimeOfSystem);
			return geImp.generatePciReport();
			
			
        } catch(Exception ioe) {
            ioe.printStackTrace();
            log.error(ioe);
            return false;
        }
	}
	
	
}
