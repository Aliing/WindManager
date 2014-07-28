package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhReportCompliance;
import com.ah.bo.performance.ComplianceResult;
import com.ah.bo.performance.ComplianceSsidListInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.monitor.ReportServiceFilter;
import com.ah.util.LongItem;
import com.ah.util.MgrUtil;

public class BePerformSummaryPageModule implements Runnable, QueryBo {

	private Map<Long, Boolean> domainData;
	private Map<Long, Boolean> bandWidthDomainData;
	private Map<Long, List<LongItem>> mapApUptime;
	private Map<Long, List<LongItem>> mapBindWidth;
	private Map<Long, Integer> mapPoorAps;
	private Map<Long, Integer> mapGoodAps;
	private Map<Long, Integer> mapExcellentAps;

	private static final Boolean synchronizedFlg = Boolean.TRUE;

	private ScheduledExecutorService scheduler;

	public BePerformSummaryPageModule() {

	}

	public void start() {
		domainData = new HashMap<Long, Boolean>();
		bandWidthDomainData = new HashMap<Long, Boolean>();
		mapApUptime = new HashMap<Long, List<LongItem>>();
		mapBindWidth = new HashMap<Long, List<LongItem>>();
		mapPoorAps = new HashMap<Long, Integer>();
		mapGoodAps = new HashMap<Long, Integer>();
		mapExcellentAps = new HashMap<Long, Integer>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		long remainMinute = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 60000;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, remainMinute + 1, 60L, TimeUnit.MINUTES);
		}
		new Thread() {
			@Override
			public void run() {
				this.setName("PerformSummaryPageModule");
				AhAppContainer.HmBe.getPerformModule().getBePerformSummaryPageModule().run();
//				bePerformSummaryPageModule.run();
			}
		}.start();

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Performance summary page module - scheduler is running...");
	}

	public void getApUptimeValue(){
		try {
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Performance summary page module getApUptimeValue " +
					"- scheduler start running...");
			long connChangeTime=System.currentTimeMillis()-1000*60*60*24*7L;
			List<?> hiveApLst=null;
			
			List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);
			
			for (HmDomain hmDomain : listData) {
				Calendar calendar = Calendar.getInstance(hmDomain.getTimeZone());
				calendar.clear(Calendar.MINUTE);
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				Long domainId;
	
				if ("global".equalsIgnoreCase(hmDomain.getDomainName())) {
					domainId = (long) -1;
				} else {
					domainId = hmDomain.getId();
				}
				
				int diffHour;
				if (domainData.get(domainId) == null) {
					diffHour = -72;
					domainData.put(domainId, true);
				} else {
					diffHour = -1;
				}
				calendar.add(Calendar.HOUR_OF_DAY, diffHour);
				
				List<LongItem> apUptime = mapApUptime.get(domainId);
				if (apUptime == null || apUptime.isEmpty()) {
					apUptime = new ArrayList<LongItem>(72);
				}
				
				if (hiveApLst == null){
					FilterParams myFilterParams = new FilterParams(
							"manageStatus = :s1 and (upTime>:s2 or (upTime=:s3 and connChangedTime>:s4))", new Object[] {
									HiveAp.STATUS_MANAGED, 0L,0L, connChangeTime});
					hiveApLst = QueryUtil.executeQuery("select macAddress,connected,owner.id from "
							+ HiveAp.class.getSimpleName(), null, myFilterParams);
				}
				int upApCount=0;
				int totalApCount=0;
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				for(Object oneObj:hiveApLst){
					Object[] oneItem = (Object[])oneObj;
					Long ownId = (Long) oneItem[2];
					Boolean connected = (Boolean) oneItem[1];
					if (domainId==-1){
						if (connected){
							upApCount++;
						}
						totalApCount++;
					} else {
						if (domainId.longValue()==ownId){
							if (connected){
								upApCount++;
							}
							totalApCount++;
						}
					}
				}
				if (apUptime.size() > 71) {
					apUptime.remove(0);
				}
				if (totalApCount != 0) {
					apUptime.add(new LongItem(upApCount * 100 / totalApCount, calendar.getTimeInMillis()));
				} else {
					apUptime.add(new LongItem(0, calendar.getTimeInMillis()));
				}
				
				for (int i = 1; i < (diffHour * -1) && diffHour < 0; i++) {
					calendar.add(Calendar.HOUR_OF_DAY, 1);
					if (apUptime.size() > 71) {
						apUptime.remove(0);
					}
					if (totalApCount != 0) {
						apUptime.add(new LongItem(upApCount * 100 / totalApCount, calendar.getTimeInMillis()));
					} else {
						apUptime.add(new LongItem(0, calendar.getTimeInMillis()));
					}
				}
				mapApUptime.put(domainId, apUptime);
			}
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Performance summary page module getApUptimeValue" +
					" - scheduler end running...");
		} catch (Exception e) {
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Performance summary page ap uptime scheduler running exception~~~!!!" + e.getMessage());
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		synchronized (synchronizedFlg) {
			getApUptimeValue();
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
			"<BE Thread> Performance summary page module - scheduler start running..." 
					+ Runtime.getRuntime().freeMemory() + "----" +
					Runtime.getRuntime().totalMemory());
			try {
				List<HmDomain> listData = QueryUtil.executeQuery(HmDomain.class, null, null);

				for (HmDomain hmDomain : listData) {
					Calendar calendar = Calendar.getInstance(hmDomain.getTimeZone());
					calendar.clear(Calendar.MINUTE);
					calendar.clear(Calendar.SECOND);
					calendar.clear(Calendar.MILLISECOND);
					Long domainId;

					if ("global".equalsIgnoreCase(hmDomain.getDomainName())) {
						domainId = (long) -1;
					} else {
						domainId = hmDomain.getId();
					}
					int diffHour;
					if (bandWidthDomainData.get(domainId) == null) {
						diffHour = -24;
						bandWidthDomainData.put(domainId, true);
					} else {
						diffHour = -1;
					}
					calendar.add(Calendar.HOUR_OF_DAY, diffHour);

					getValueCompliance(domainId);


					List<LongItem> bindWidthRate = mapBindWidth.get(domainId);
					if (bindWidthRate == null || bindWidthRate.isEmpty()) {
						bindWidthRate = new ArrayList<LongItem>(24);
						for(int i=1;i<=21;i++){
							bindWidthRate.add(new LongItem(0L,calendar.getTimeInMillis()+(i*3600000)));
						}
						mapBindWidth.put(domainId, bindWidthRate);
						diffHour=-3;
						calendar.add(Calendar.HOUR_OF_DAY, 21);
					}

					for (int i = 0; i < (diffHour * -1) && diffHour < 0; i++) {
						long startTimeInMillis = calendar.getTimeInMillis();
						calendar.add(Calendar.HOUR_OF_DAY, 1);
						long currentTimeInMillis = calendar.getTimeInMillis();
						
						getBindWidthCountValue(bindWidthRate, domainId, startTimeInMillis,
								currentTimeInMillis);
					}
				}
			} catch (OutOfMemoryError outError) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"Performance summary page scheduler running out of memory~~~!!!");
			} catch (Exception e) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"Performance summary page scheduler running exception~~~!!!" + e.getMessage());
			}
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
			"<BE Thread> Performance summary page module - scheduler end running..."
				+ Runtime.getRuntime().freeMemory() + "----" +
				Runtime.getRuntime().totalMemory());
		}
	}

	public void getBindWidthCountValue(List<LongItem> bindWidthRate, Long domainId,
			long startTimeInMillis, long currentTimeInMillis) {
		String sqlRate = "select sum(bo.txByteCount + bo.rxByteCount) from " + AhInterfaceStats.class.getSimpleName() + " bo where bo.timeStamp >= "
				+ startTimeInMillis;
		if (domainId != null && domainId != -1) {
			sqlRate = sqlRate + " and bo.owner.id=" + domainId;
		}
		sqlRate = sqlRate + " and bo.timeStamp < " + currentTimeInMillis;
		try {
			List<?> profilesRate = QueryUtil.executeQuery(sqlRate, null, null);

			long countBindWidthData = Long.valueOf(profilesRate.get(0).toString());
			if (bindWidthRate.size() > 23) {
				bindWidthRate.remove(0);
			}
			bindWidthRate.add(new LongItem(countBindWidthData, currentTimeInMillis));
		} catch (Exception e) {
			if (bindWidthRate.size() > 23) {
				bindWidthRate.remove(0);
			}
			bindWidthRate.add(new LongItem(0, currentTimeInMillis));
		}
		mapBindWidth.put(domainId, bindWidthRate);
	}

	public void getValueCompliance(Long domainId) {
		List<ComplianceResult> lstCompliance = new ArrayList<ComplianceResult>();
		try {
			Long myDomain = null;
			if (domainId != null && domainId != -1) {
				myDomain = domainId;
			}
			int poorAp = 0;
			int goodAp = 0;
			int excellentAp = 0;
			
//			List<AhSummaryPage> checkWidgetCount;
//			if (myDomain==null) {
//				checkWidgetCount = QueryUtil.executeQuery(AhSummaryPage.class, null, 
//					new FilterParams("ckwidgetAPcompliance",true), BoMgmt.getDomainMgmt().getHomeDomain().getId());
//			} else {
//				checkWidgetCount = QueryUtil.executeQuery(AhSummaryPage.class, null, 
//						new FilterParams("ckwidgetAPcompliance",true), myDomain);
//			}
//			
//			if (checkWidgetCount!=null && checkWidgetCount.size()>0){
			
				List<HiveAp> profiles = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams("manageStatus=:s1 and (deviceType=:s2 or deviceType=:s3 or deviceType=:s4)",
								new Object[]{HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_HIVEAP, HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR}),
						myDomain);

				if (profiles.size() > 0) {
					CompliancePolicy compliancePolicy = null;
					List<CompliancePolicy> configData = QueryUtil.executeQuery(CompliancePolicy.class, null, null,
							myDomain);
					if (configData.size() == 0) {
						compliancePolicy = new CompliancePolicy();
					} else {
						for (CompliancePolicy compliancePolicyClass : configData) {
							compliancePolicyClass = QueryUtil.findBoById(
									CompliancePolicy.class, compliancePolicyClass.getId(),
									this);
							if (myDomain == null) {
								if (compliancePolicyClass.getOwner().getDomainName().equalsIgnoreCase(
										"home")) {
									compliancePolicy = compliancePolicyClass;
									break;
								}
							} else {
								compliancePolicy = compliancePolicyClass;
								break;
							}
						}
						if (compliancePolicy == null) {
							compliancePolicy = new CompliancePolicy();
						}
					}
	
					List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null, null, 
							myDomain==null?BoMgmt.getDomainMgmt().getHomeDomain().getId():myDomain);
					HmStartConfig stg=  list.isEmpty() ? null : list.get(0);
					String globalDevicePwd=null;
					if (stg!=null) {
						globalDevicePwd = stg.getHiveApPassword();

					}
					for (HiveAp hiveap : profiles) {
						ComplianceResult complianceResult = new ComplianceResult();
						complianceResult.setApMac(hiveap.getMacAddress());
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
	
						hiveap = QueryUtil.findBoById(HiveAp.class, hiveap.getId(),
								this);
	
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
//						if (!hiveap.getConfigTemplate().isOverrideTF4IndividualAPs() 
//								&& hiveap.getConfigTemplate().getDeviceServiceFilter()!=null) {
//							ServiceFilter defFilter = hiveap.getConfigTemplate().getDeviceServiceFilter();
//							hiveap.getConfigTemplate().setEth0ServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setEth0BackServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setWireServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setEth1ServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setEth1BackServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setRed0ServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setRed0BackServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setAgg0ServiceFilter(defFilter);
//							hiveap.getConfigTemplate().setAgg0BackServiceFilter(defFilter);
//						}
//						//xxxxxxxxx
//						ServiceFilter serviceFilter =hiveap.getConfigTemplate().getEth0ServiceFilter();
//						ServiceFilter serviceFilterBack =hiveap.getConfigTemplate().getEth0BackServiceFilter();
//						ComplianceSsidListInfo ssidListInfo = getComplianceSsidListInfo(
//								serviceFilter,serviceFilterBack,compliancePolicy,"eth0");
//						complianceResult.getSsidList().add(ssidListInfo);
//						
//						if (hiveap.isEth1Available()){
//							serviceFilter =hiveap.getConfigTemplate().getEth1ServiceFilter();
//							serviceFilterBack =hiveap.getConfigTemplate().getEth1BackServiceFilter();
//							ssidListInfo = getComplianceSsidListInfo(
//									serviceFilter,serviceFilterBack,compliancePolicy,"eth1");
//							complianceResult.getSsidList().add(ssidListInfo);
//							
//							serviceFilter =hiveap.getConfigTemplate().getRed0ServiceFilter();
//							serviceFilterBack =hiveap.getConfigTemplate().getRed0BackServiceFilter();
//							ssidListInfo = getComplianceSsidListInfo(
//									serviceFilter,serviceFilterBack,compliancePolicy,"red0");
//							complianceResult.getSsidList().add(ssidListInfo);
//							
//							serviceFilter =hiveap.getConfigTemplate().getAgg0ServiceFilter();
//							serviceFilterBack =hiveap.getConfigTemplate().getAgg0BackServiceFilter();
//							ssidListInfo = getComplianceSsidListInfo(
//									serviceFilter,serviceFilterBack,compliancePolicy,"agg0");
//							complianceResult.getSsidList().add(ssidListInfo);
//						}
//	
//						for (ConfigTemplateSsid configTemplateSsid : hiveap.getConfigTemplate()
//								.getSsidInterfaces().values()) {
//							if (configTemplateSsid.getSsidProfile() != null) {
//								SsidProfile sp = configTemplateSsid.getSsidProfile();
//								ssidListInfo = new ComplianceSsidListInfo();
//								ssidListInfo.setSsidName(sp.getSsidName());
//								switch (sp.getAccessMode()) {
//								case SsidProfile.ACCESS_MODE_OPEN:
//									if (sp.getMacAuthEnabled()) {
//										ssidListInfo
//												.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN_AUTH);
//										ssidListInfo.setRating(compliancePolicy.getClientOpenAuth());
//									} else {
//										ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_OPEN);
//										ssidListInfo.setRating(compliancePolicy.getClientOpen());
//									}
//									break;
//								case SsidProfile.ACCESS_MODE_WPA:
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PSK);
//									ssidListInfo.setRating(compliancePolicy.getClientPsk());
//									if (!compliancePolicy.getPasswordSSID()) {
//										ssidListInfo.setSsidPass(ComplianceResult.PASSWORD_STRENGTH_NA);
//									} else {
//										int ssidPass = MgrUtil.checkPasswordStrength(sp
//												.getSsidSecurity().getFirstKeyValue());
//										ssidListInfo.setSsidPass(ssidPass);
//									}
//									break;
//								case SsidProfile.ACCESS_MODE_PSK:
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_PRIVETE_PSK);
//									ssidListInfo.setRating(compliancePolicy.getClientPrivatePsk());
//									break;
//								case SsidProfile.ACCESS_MODE_WEP:
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_WEP);
//									ssidListInfo.setRating(compliancePolicy.getClientWep());
//									break;
//								case SsidProfile.ACCESS_MODE_8021X:
//									ssidListInfo.setSsidMethod(ComplianceSsidListInfo.SSID_8021X);
//									ssidListInfo.setRating(compliancePolicy.getClient8021x());
//									break;
//								}
//								serviceFilter = sp.getServiceFilter();
//								if (serviceFilter.getEnableSSH()) {
//									ssidListInfo.setBlnSsh(compliancePolicy.getHiveApSsh());
//								} else {
//									ssidListInfo
//											.setBlnSsh(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//								}
//								if (serviceFilter.getEnablePing()) {
//									ssidListInfo.setBlnPing(compliancePolicy.getHiveApPing());
//								} else {
//									ssidListInfo
//											.setBlnPing(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//								}
//								if (serviceFilter.getEnableTelnet()) {
//									ssidListInfo.setBlnTelnet(compliancePolicy.getHiveApTelnet());
//								} else {
//									ssidListInfo
//											.setBlnTelnet(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//								}
//								if (serviceFilter.getEnableSNMP()) {
//									ssidListInfo.setBlnSnmp(compliancePolicy.getHiveApSnmp());
//								} else {
//									ssidListInfo
//											.setBlnSnmp(CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT);
//								}
//								complianceResult.getSsidList().add(ssidListInfo);
//							}
//						}
						lstCompliance.add(complianceResult);
					}

					for (ComplianceResult resultValue : lstCompliance) {
						if (resultValue.getSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_POOR) {
							poorAp++;
						}
						if (resultValue.getSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_GOOD) {
							goodAp++;
						}
						if (resultValue.getSummarySecurity() == CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT) {
							excellentAp++;
						}
					}
					insertComplianceResult(lstCompliance, domainId);
				}
//			}
			mapPoorAps.put(domainId, poorAp);
			mapGoodAps.put(domainId, goodAp);
			mapExcellentAps.put(domainId, excellentAp);
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("getValueCompliance():", e);
			mapPoorAps.put(domainId, 0);
			mapGoodAps.put(domainId, 0);
			mapExcellentAps.put(domainId, 0);
		}
	}
	//AhReportCompliance
	private boolean insertComplianceResult(List<ComplianceResult> resultValue , Long domainId){
//		need transaction
		EntityManager em = null;
		EntityTransaction tx = null;
		AhReportCompliance ahReport = null;
		List<AhReportCompliance> insertList = new ArrayList<AhReportCompliance>();
		try{
			HmDomain owner = QueryUtil.findBoById(HmDomain.class, domainId);
			if ( owner == null ) {
				return false;
			}
			//TODO is right?
			if (domainId == null || (domainId == -1)) {
				return false;
			}
			
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			em.createQuery(" delete from AhReportCompliance where owner=" + domainId).executeUpdate();
			
//			@SuppressWarnings("unchecked")
//			List<AhReportCompliance> oldList = (List<AhReportCompliance>)
//					em.createQuery(" from " + AhReportCompliance.class + " where owner=" + domainId);
//			for( AhReportCompliance old : oldList ) {
//				em.remove( old );
//			}
			
			for (ComplianceResult result: resultValue){ 
				String mac = result.getApMac();
				int status = result.getSummarySecurity();
				ahReport = new AhReportCompliance();
				ahReport.setApmac(mac);
				ahReport.setStatus(status);
				ahReport.setOwner(owner);
				insertList.add(ahReport);
				em.persist( ahReport );
//				QueryUtil.createBo(ahReport);
			}
			tx.commit();
//			QueryUtil.bulkRemoveBos(AhReportCompliance.class, null, domainId);
			//QueryUtil.bulkCreateBos(insertList);
			//QueryUtil.b
		}catch(Exception e){
			QueryUtil.rollback(tx);
			DebugUtil.performanceDebugWarn("insertComplianceResult():", e);
		}finally{
			QueryUtil.closeEntityManager(em);
		}
		
		return false;
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

	public void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being cancelled.
		if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			BeLogTools
					.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
							"<BE Thread> Performance summary page module - task is not terminated completely");
		}
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Performance summary page module - scheduler is shutdown");
	}

	public List<LongItem> getMapBindWidth(Long domainId) {
		return mapBindWidth.get(domainId);
	}

	public List<LongItem> getMapApUptime(Long domainId) {
		return mapApUptime.get(domainId);
	}

	public int getMapPoorAps(Long domainId) {
		return mapPoorAps.get(domainId) == null ? 0 : mapPoorAps.get(domainId);
	}

	public int getMapGoodAps(Long domainId) {
		return mapGoodAps.get(domainId) == null ? 0 : mapGoodAps.get(domainId);
	}

	public int getMapExcellentAps(Long domainId) {
		return mapExcellentAps.get(domainId) == null ? 0 : mapExcellentAps.get(domainId);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
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
		if (bo instanceof CompliancePolicy) {
			CompliancePolicy policy = (CompliancePolicy) bo;
			if (policy.getOwner() != null) {
				policy.getOwner().getId();
			}
		}

		return null;
	}

}