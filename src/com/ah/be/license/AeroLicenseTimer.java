/**
 *@filename		AeroLicenseTimer.java
 *@version		v1.19
 *@author		Fiona
 *@createtime	Mar 13, 2007 1:24:46 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.license;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.activation.AeroActivationTimer;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;

/**
 * @author Fiona
 * @version v1.19
 */
public class AeroLicenseTimer implements SessionKeys
{
	private static final Tracer log = new Tracer(AeroLicenseTimer.class.getSimpleName());
	
	private ScheduledExecutorService evaluationTimer;
	
	private ScheduledExecutorService permanentTimer;
	
	private ScheduledFuture<?> evaluateFuture;
	
	private ScheduledFuture<?> permanentFuture;

	private final BeLicenseModule LicenseModule = AhAppContainer.HmBe.getLicenseModule();
	
	private String[] twoSystemIds;
	
	public static LicenseInfo HIVEMANAGER_LICENSE_INFO;

	public void startAllLicenseTimer() {
		try {
			if (LicenseModule != null) {
				HIVEMANAGER_LICENSE_INFO = null;
				twoSystemIds = LicenseModule.getTwoSystemId();
				
				LicenseInfo lsInfo = LicenseOperationTool.getOrderKeyInfoFromDatabase(HmDomain.HOME_DOMAIN);
				
				// whole hmol system
				if (null == lsInfo) {
					HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_VALID;
					HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
					if (!HAUtil.isSlave()) {
						permanentEntitleKeyTimer();
					}
				} else {
					boolean noLicense = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType());
					
					// no order key, need to get license
					if (noLicense) {
						// deal with HA mode
						if ((null != twoSystemIds && LicenseOperationTool.updateTwoLicenseInfo(twoSystemIds, 0) > -1) || null == twoSystemIds) {
							HIVEMANAGER_LICENSE_INFO = LicenseOperationTool.getLicenseInfoFromDatabase(LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER, BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
						}
					}
							
					// the license info does not exist
					if (HIVEMANAGER_LICENSE_INFO == null) {
						HIVEMANAGER_LICENSE_INFO = lsInfo;
					} else {
						noLicense = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(HIVEMANAGER_LICENSE_INFO.getLicenseType());
					}
					
					// set the flag of GM Light
					HmBeLicenseUtil.GM_LITE_LICENSE_INFO = LicenseOperationTool.getLicenseInfoFromDatabase(LicenseHistoryInfo.LICENSE_TYPE_GM_LITE, HIVEMANAGER_LICENSE_INFO.getSystemId());
					if (null != HmBeLicenseUtil.GM_LITE_LICENSE_INFO) {
						HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
					
					// no order key but has 30 grace days
					} else if (noLicense && HIVEMANAGER_LICENSE_INFO.getLeftHours() > 0) {
						HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
					}
					
					if (HAUtil.isSlave()) {
						HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_VALID;
					} else {
						// the license is evaluation or vmware
						if ((BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(HIVEMANAGER_LICENSE_INFO.getLicenseType())
								&& "".equals(HIVEMANAGER_LICENSE_INFO.getOrderKey()))
								|| BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(HIVEMANAGER_LICENSE_INFO.getLicenseType())
								|| noLicense) {
							// the license is overdue
							if (HIVEMANAGER_LICENSE_INFO.getLeftHours() <= 0) {
								HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = noLicense ? HmBeLicenseUtil.NO_LICENSE_MUST_INPUT : HmBeLicenseUtil.LICENSE_INVALID;
								log.debug("The license is overdue or you must install license!");
							} else {
								HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = noLicense ? HmBeLicenseUtil.NO_LICENSE_HAS_PERIOD : HmBeLicenseUtil.LICENSE_VALID;
								
								// change the license allowed use time based on the server running time
								evaluationOrVmwareTimer();
							}
						} else {
							HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_VALID;
						}
						// permanent license string, not HA system
						if (!"".equals(HIVEMANAGER_LICENSE_INFO.getOrderKey()) && null == twoSystemIds) {
							permanentEntitleKeyTimer();
						}
					}
				}
			}
		} catch (Exception e) {
			myDebug("startAllLicenseTimer(): "+e.getMessage());
		}
	}

	/**
	 * Change the evaluation license allowed use time based on the server running time
	 */
	public void evaluationOrVmwareTimer()
	{
		EvaluationLicenseTask taskEvalue = new EvaluationLicenseTask();
		if(null == evaluationTimer || evaluationTimer.isShutdown()) {
			evaluationTimer = Executors.newSingleThreadScheduledExecutor();
			evaluateFuture = evaluationTimer.scheduleWithFixedDelay(taskEvalue, 60 * 60, 60 * 60, TimeUnit.SECONDS);
			//evaluateFuture = evaluationTimer.scheduleAtFixedRate(taskEvalue, 60 * 3, 60 * 3, TimeUnit.SECONDS);
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_LICENSE, 
				"<BE Thread> Evaluation License timer is running...");
		}	
	}
	
	/**
	 * Change the permanent entitle key can manage HiveAP number
	 */
	public void permanentEntitleKeyTimer()
	{
		PermanentEntitleKeyTask taskEvalue = new PermanentEntitleKeyTask();
		if(null == permanentTimer || permanentTimer.isShutdown()) {
			permanentTimer = Executors.newSingleThreadScheduledExecutor();
			permanentFuture = permanentTimer.scheduleWithFixedDelay(taskEvalue, 5, 6*60, TimeUnit.MINUTES);
			//permanentFuture = permanentTimer.scheduleAtFixedRate(taskEvalue, 5, 5, TimeUnit.MINUTES);
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_LICENSE, 
				"<BE Thread> Permanent Entitle Key timer is running...");
		}	
	}
	
	/**
	 * Do the evaluation license task.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class EvaluationLicenseTask implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			try {
				int hours;
				boolean noLicense = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(HIVEMANAGER_LICENSE_INFO.getLicenseType());
				
				// no order key but has 30 days evaluation
				if (noLicense || !"".equals(HIVEMANAGER_LICENSE_INFO.getOrderKey())) {
					DomainOrderKeyInfo hmDom = LicenseOperationTool.getDomainOrderKeyInfoForHa(HmDomain.HOME_DOMAIN);
					if (null != hmDom) {
						hmDom.setHoursUsed(hmDom.getEncriptString(hmDom.getHoursUsedInt()+1));
						QueryUtil.updateBo(hmDom);
					}
					
					hours = HIVEMANAGER_LICENSE_INFO.getLeftHours()-1;
				} else {
					// compare the used hours of primary and secondary after join to HA
					twoSystemIds = LicenseModule.getTwoSystemId();
					if (null != twoSystemIds) {
						hours = LicenseOperationTool.updateTwoLicenseInfo(twoSystemIds, 1);
					} else {
						// the license information exists
						LicenseHistoryInfo	activeLicense = LicenseOperationTool.getActiveLicenseBySystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID,
								LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER);
						hours = 0;
						if (null != activeLicense) {
							String licenseKey = HM_License.getInstance().decrypt_from_string(BeLicenseModule.HIVEMANAGER_SYSTEM_ID, activeLicense.getLicenseString());
							int totalHours = Integer.parseInt(licenseKey
								.substring(BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX, BeLicenseModule.LICENSE_KEY_LENGTH));
							int usedHours = LicenseOperationTool.getDecryptedHours(activeLicense.getHoursUsed(), BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
							
							if (usedHours < totalHours) {
								// modify evaluation license hours
								activeLicense.setHoursUsed(LicenseOperationTool.getEncryptedHours(usedHours + 1, BeLicenseModule.HIVEMANAGER_SYSTEM_ID));
								QueryUtil.updateBo(activeLicense);
								hours = totalHours-usedHours-1;
							}
						}					
					}
				}
				HIVEMANAGER_LICENSE_INFO.setLeftHours(hours);
				
				// change the flag
				if (hours <= 0) {
					HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = noLicense ? HmBeLicenseUtil.NO_LICENSE_MUST_INPUT : HmBeLicenseUtil.LICENSE_INVALID;
					
					// change the flag in entitle key history
//					if (!noLicense) {
//						QueryUtil.executeNativeUpdate("UPDATE order_history_info SET statusflag = 3, cvgStatusFlag = 3 WHERE orderkey = '" + HIVEMANAGER_LICENSE_INFO.getOrderKey() +"'");
//					}
				}
				AeroActivationTimer.writeSystemLog(hours, false);
			} catch (Exception ex) {
				myDebug("EvaluationLicenseTask(): "+ex.getMessage());
			} catch (Error e) {
				myDebug("EvaluationLicenseTask(): "+e.getMessage());
			}
		}
	}
	
	/**
	 * Do permanent entitle key task.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class PermanentEntitleKeyTask implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			log.debug("PermanentEntitleKeyTask start");
			try {
				// ACM entitlement key information
				checkAcmEntitleKeyStatus();
				
				List<OrderHistoryInfo> historyInfo;
				Set<HmBo> updateBos = new HashSet<HmBo>();
				FilterParams filterPar = null;
				
				// hm online info
				if (NmsUtil.isHostedHMApplication()) {
					List<?> domNames = QueryUtil.executeQuery("select domainName from " + HmDomain.class.getSimpleName(), null,
						new FilterParams("domainName != :s1 AND domainName != :s2 AND runStatus = :s3", 
							new Object[]{HmDomain.HOME_DOMAIN, HmDomain.GLOBAL_DOMAIN, HmDomain.DOMAIN_DEFAULT_STATUS}));
					if (!domNames.isEmpty()) {
						filterPar = new FilterParams("domainName in (:s1) AND (statusFlag = :s2 OR cvgStatusFlag = :s3)", 
							new Object[]{domNames, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL});
					}
				} else {
					if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID != HmBeLicenseUtil.LICENSE_VALID) {
						return;
					} else {
						filterPar = new FilterParams("domainName = :s1 AND statusFlag = :s2", 
								new Object[]{HmDomain.HOME_DOMAIN, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL});
					}
				}
				
				if (null != filterPar) {
					log.debug("PermanentEntitleKeyTask FilterParams "+filterPar.getWhere());
					
					historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, null, filterPar);
					Map<String, DomainOrderKeyInfo> changeDomain = new HashMap<String, DomainOrderKeyInfo>();
					
					// the current date
					long current;
					
					// subscription or support end date compare with the current date
					long support;
					
					long leftDays;
					
					int homeLessAp = 0;
					
					int homeLessVhm = 0;
					
					int homeLessCvg = 0;
					
					// notify email list
					Map<String, List<LicenseInfo>> orderKeyExpiredEmailNotify = new HashMap<String, List<LicenseInfo>>();
					List<LicenseInfo> orderKeyList;
					
					for (OrderHistoryInfo history : historyInfo) {
						log.debug("PermanentEntitleKeyTask OrderHistoryInfo " + history.getDomainName() + history.getOrderKey());
						
						if (HmDomain.HOME_DOMAIN.equals(history.getDomainName())) {
							current = System.currentTimeMillis();
							support = System.currentTimeMillis();
							if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(history.getLicenseType())) {
								current = history.getActiveTime();
							} else if (!NmsUtil.isHMForOEM()){
								if (history.getSupportEndDate() <= 0)
									continue;
								support = history.getSupportEndDate();
							} else {
								continue;
							}
							
							leftDays = (support - current) / (1000l * 60l * 60l * 24l);
							
							long leftHours = (support - current) % (1000l * 60l * 60l * 24l);
							
							if (leftDays >= 0 && leftHours > (1000l * 60l * 60l * 6l)) {
								leftDays += 1;
							}
							
							if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(history.getLicenseType())) {
								leftDays = history.getNumberOfEvalValidDays() - leftDays;
							}
							
							// the support time is less than the 30 days
							if (leftDays < 31) {
								// send email to admin
								if (!NmsUtil.isHMForOEM() && !history.isSendEmail()) {
									String paraStr = BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(history.getLicenseType()) ? 
										"The entitlement key(" : "The support of this entitlement key(";
									String mailContent = paraStr+history.getOrderKey()+") with "+history.getNumberOfAps()+" Devices and "
									+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances and "+history.getNumberOfVhms()+" VHMs is valid for "+(leftDays > 1 ? leftDays+" days." : "1 day.");
									
									if (leftDays < 1) {
										mailContent = paraStr+history.getOrderKey()+") with "+history.getNumberOfAps()
										+" Devices and "+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances and "+history.getNumberOfVhms()+" VHMs has expired.";
									}
									
									LicenseInfo orderKey = new LicenseInfo();
									orderKey.setOrderKey(history.getOrderKey());
									orderKey.setSystemId(mailContent);
									
									orderKeyList = orderKeyExpiredEmailNotify.get(HmDomain.HOME_DOMAIN);
									if (null == orderKeyList) {
										orderKeyList = new ArrayList<LicenseInfo>();
									}
									orderKeyList.add(orderKey);
									orderKeyExpiredEmailNotify.put(HmDomain.HOME_DOMAIN, orderKeyList);
								}
								// support end date overdue
								if (leftDays <= 0) {
									history.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
									history.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
									updateBos.add(history);
									
									// evaluation key
									if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(history.getLicenseType())) {
										homeLessAp += history.getNumberOfAps();
										homeLessVhm += history.getNumberOfVhms();
										homeLessCvg += history.getNumberOfCvgs();
									}
								} else if (!NmsUtil.isHMForOEM() && !history.isSendEmail()) {
									updateBos.add(history);
								}
							}
						} else {
							if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(history.getLicenseType())) {
								dealwithEvaluationDays(history, updateBos, changeDomain, orderKeyExpiredEmailNotify);
							} else {
								dealwithSupportDate(history, updateBos, changeDomain, orderKeyExpiredEmailNotify);
							}
						}		
					}
					
					// send email notify
					for (String domName : orderKeyExpiredEmailNotify.keySet()) {
						StringBuffer emailTxt = new StringBuffer();
						for (LicenseInfo lsInfo : orderKeyExpiredEmailNotify.get(domName)) {
							emailTxt.append(lsInfo.getSystemId()+"<br>");
						}
						// update the email notify flag
						if (send(domName, emailTxt.toString())) {
							overloop:
							for (LicenseInfo lsInfo : orderKeyExpiredEmailNotify.get(domName)) {
								for (HmBo hmbo : updateBos) {
									if (hmbo instanceof OrderHistoryInfo) {
										OrderHistoryInfo orInfo = (OrderHistoryInfo)hmbo;
										if (orInfo.getOrderKey().equals(lsInfo.getOrderKey())) {
											((OrderHistoryInfo)hmbo).setSendEmail(true);
											continue overloop;
										}
									}
								}
							}		
						}
					}
					
					if (!changeDomain.isEmpty()) {
						updateBos.addAll(changeDomain.values());
					}
					
					if (!updateBos.isEmpty()) {
						QueryUtil.bulkUpdateBos(updateBos);
					}
					
					if (homeLessAp > 0 || homeLessVhm > 0 || homeLessCvg > 0) {
						if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID != HmBeLicenseUtil.LICENSE_VALID) {
							return;
						}
						// change the value in memory
						HIVEMANAGER_LICENSE_INFO.setHiveAps(HIVEMANAGER_LICENSE_INFO.getHiveAps()>homeLessAp?HIVEMANAGER_LICENSE_INFO.getHiveAps()-homeLessAp:0);
						HIVEMANAGER_LICENSE_INFO.setVhmNumber(HIVEMANAGER_LICENSE_INFO.getVhmNumber()>homeLessVhm?HIVEMANAGER_LICENSE_INFO.getVhmNumber()-homeLessVhm:0);
						HIVEMANAGER_LICENSE_INFO.setCvgNumber(HIVEMANAGER_LICENSE_INFO.getCvgNumber()>homeLessCvg?HIVEMANAGER_LICENSE_INFO.getCvgNumber()-homeLessCvg:0);
						
						// change the value in database
						DomainOrderKeyInfo currentDom = LicenseOperationTool.getDomainOrderKeyInfoForHa(HmDomain.HOME_DOMAIN);
						OrderHistoryInfo zeroDeviceKey = null;
						// check if has entitlement key with 0 device
						if (HIVEMANAGER_LICENSE_INFO.getHiveAps() == 0 && HIVEMANAGER_LICENSE_INFO.getCvgNumber() == 0) {
							zeroDeviceKey = LicenseOperationTool.getEntitlementKeyWithZeroDevice(HmDomain.HOME_DOMAIN);
							if (null == zeroDeviceKey) {
								HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_INVALID;
							}
						}
						
						if (null != currentDom) {
							if (HIVEMANAGER_LICENSE_INFO.getHiveAps() == 0 && HIVEMANAGER_LICENSE_INFO.getCvgNumber() == 0) {
								if (null != zeroDeviceKey) {
									currentDom.setOrderKey(zeroDeviceKey.getOrderKey());
									currentDom.setEncryptLicense(zeroDeviceKey.getLicenseType(), 0, HIVEMANAGER_LICENSE_INFO.getVhmNumber(), 0, 0);
								}
							}
							if (null == zeroDeviceKey)
								currentDom.setEncryptLicense(HIVEMANAGER_LICENSE_INFO.getLicenseType(), HIVEMANAGER_LICENSE_INFO.getHiveAps(), 
									HIVEMANAGER_LICENSE_INFO.getVhmNumber(), HIVEMANAGER_LICENSE_INFO.getTotalDays(), HIVEMANAGER_LICENSE_INFO.getCvgNumber());
							QueryUtil.updateBo(currentDom);
						}
						
						if (homeLessVhm > 0) {
							int delVhm = CacheMgmt.getInstance().getCacheDomainCount() - HIVEMANAGER_LICENSE_INFO.getVhmNumber();
							if (delVhm > 0) {
							//	String query = "UPDATE hm_domain SET runstatus = "+HmDomain.DOMAIN_DISABLE_STATUS +" WHERE id IN (SELECT id FROM hm_domain WHERE domainname != '"
							//	+HmDomain.HOME_DOMAIN +"' AND domainname != '"+HmDomain.GLOBAL_DOMAIN+"' ORDER BY id limit "+delVhm+")";
							//	QueryUtil.executeNativeUpdate(query);

								Collection<String> excludedDomainNames = new ArrayList<String>(2);
								excludedDomainNames.add(HmDomain.GLOBAL_DOMAIN);
								excludedDomainNames.add(HmDomain.HOME_DOMAIN);
								List<?> hmDomainIds = QueryUtil.executeQuery("select id from " + HmDomain.class.getSimpleName(), new SortParams("id"), new FilterParams("domainName not in (:s1)", new Object[] { excludedDomainNames }), delVhm);

								if (!hmDomainIds.isEmpty()) {
									QueryUtil.updateBos(HmDomain.class, "runStatus = :s1", "id in (:s2)", new Object[] { HmDomain.DOMAIN_DISABLE_STATUS, hmDomainIds });
								}
							}
						}
						
						if (homeLessAp > 0) {
							int mgmtNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 " +
								"AND simulated = false AND deviceType != :s2", new Object[]{HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY }));
							
							// delete managed overdue HiveAPs
							int delNumber = mgmtNumber - HIVEMANAGER_LICENSE_INFO.getHiveAps() - BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT;
							if (delNumber > 0) {
								String query = "UPDATE hive_ap SET managestatus = "+HiveAp.STATUS_NEW +" WHERE id IN (SELECT id FROM hive_ap WHERE simulated = false AND managestatus = "+HiveAp.STATUS_MANAGED + 
									" AND devicetype != "+HiveAp.Device_TYPE_VPN_GATEWAY+" ORDER BY id limit "+delNumber+")";
								QueryUtil.executeNativeUpdate(query);
							}
						}
						
						if (homeLessCvg > 0) {
							int mgmtCvgNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 " +
								"AND simulated = false AND deviceType = :s2", new Object[]{HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY }));
							
							// delete managed overdue CVGs
							int delCvgNumber = mgmtCvgNumber - HIVEMANAGER_LICENSE_INFO.getCvgNumber();
							if (delCvgNumber > 0) {
								String query = "UPDATE hive_ap SET managestatus = "+HiveAp.STATUS_NEW +" WHERE id IN (SELECT id FROM hive_ap WHERE simulated = false AND managestatus = "+HiveAp.STATUS_MANAGED + 
									" AND devicetype = "+HiveAp.Device_TYPE_VPN_GATEWAY+" ORDER BY id limit "+delCvgNumber+")";
								QueryUtil.executeNativeUpdate(query);
							}
						}
					}

					if (!changeDomain.isEmpty()) {
						for (String domName : changeDomain.keySet()) {
							DomainOrderKeyInfo currentDom = changeDomain.get(domName);
							int apNumber = currentDom.getOrderInfo()[1];
							int mgmtNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 " +
								"AND owner.domainName = :s2 AND simulated = false AND deviceType != :s3", new Object[]{HiveAp.STATUS_MANAGED, domName,
									HiveAp.Device_TYPE_VPN_GATEWAY }));
							
							// delete managed overdue HiveAPs
							int delNumber = mgmtNumber - apNumber;
							if (delNumber > 0) {
								String query = "UPDATE hive_ap SET managestatus = "+HiveAp.STATUS_NEW +" WHERE id IN (SELECT id FROM hive_ap WHERE simulated = false AND managestatus = "+HiveAp.STATUS_MANAGED + 
									" AND devicetype != "+HiveAp.Device_TYPE_VPN_GATEWAY+
									" AND owner = (SELECT id FROM hm_domain WHERE domainname ='"+domName+"') ORDER BY id limit "+delNumber+")";
								QueryUtil.executeNativeUpdate(query);
							}
							
							int cvgNumber = currentDom.getOrderInfo()[4];
							int mgmtCvgNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams("manageStatus = :s1 " +
								"AND owner.domainName = :s2 AND simulated = false AND deviceType = :s3", new Object[]{HiveAp.STATUS_MANAGED, domName,
									HiveAp.Device_TYPE_VPN_GATEWAY }));
							
							// delete managed overdue CVGs
							int delCvgNumber = mgmtCvgNumber - cvgNumber;
							if (delCvgNumber > 0) {
								String query = "UPDATE hive_ap SET managestatus = "+HiveAp.STATUS_NEW +" WHERE id IN (SELECT id FROM hive_ap WHERE simulated = false AND managestatus = "+HiveAp.STATUS_MANAGED + 
									" AND devicetype = "+HiveAp.Device_TYPE_VPN_GATEWAY+
									" AND owner = (SELECT id FROM hm_domain WHERE domainname ='"+domName+"') ORDER BY id limit "+delCvgNumber+")";
								QueryUtil.executeNativeUpdate(query);
							}
							
							// send ap number to portal
							if (!HmDomain.HOME_DOMAIN.equals(domName)) {
								RemotePortalOperationRequest.updateVHMInfo(domName, apNumber);
							}
						}
					}
				}
			} catch (Exception ex) {
				myDebug("PermanentEntitleKeyTask(): "+ex.getMessage());
			} catch (Error e) {
				myDebug("PermanentEntitleKeyTask(): "+e.getMessage());
			}
		}
	}
	
	/**
	 * Check ACM entitlement key if expired
	 *
	 */
	private void checkAcmEntitleKeyStatus() {
		List<AcmEntitleKeyHistoryInfo> keyInfos = QueryUtil.executeQuery(AcmEntitleKeyHistoryInfo.class, new SortParams("supportEndDate", false),
				new FilterParams("statusFlag", AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL));
		List<AcmEntitleKeyHistoryInfo> updateBos = new ArrayList<AcmEntitleKeyHistoryInfo>();
		for (AcmEntitleKeyHistoryInfo keyInfo : keyInfos) {
			// the key overdue
			if (System.currentTimeMillis() > keyInfo.getSupportEndDate()) {
				keyInfo.setStatusFlag(AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
				updateBos.add(keyInfo);
			}
		}
		if (!updateBos.isEmpty()) {
			try {
				QueryUtil.bulkUpdateBos(updateBos);
			} catch (Exception ex) {
				myDebug("PermanentEntitleKeyTask() -> checkAcmEntitleKeyStatus(): "+ex.getMessage());
			}
		}
	}
	
	private void dealwithEvaluationDays(OrderHistoryInfo history, Set<HmBo> updateBos, Map<String, DomainOrderKeyInfo> changeDomain, Map<String, List<LicenseInfo>> emailList) {
		long leftDays = 100l;
		
		// the current date
		long current = System.currentTimeMillis();
		
		if (OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL == history.getStatusFlag()) {
			long supportDate = history.getActiveTime()+(history.getNumberOfEvalValidDays()*1000l * 60l * 60l * 24l);
			
			// HiveAP subscription date
			leftDays = (supportDate - current) / (1000l * 60l * 60l * 24l);
			
			long apLeftHours = (supportDate - current) % (1000l * 60l * 60l * 24l);
			
			if (leftDays >= 0 && apLeftHours > (1000l * 60l * 60l * 6l)) {
				leftDays += 1;
			}
		}
		
		// current licensed HiveAP and CVG number
		DomainOrderKeyInfo currentDom = changeDomain.get(history.getDomainName());
		if (null == currentDom) {
			currentDom = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class,
				"domainName", history.getDomainName());
		}
		
		int newApNumber = currentDom.getOrderInfo()[1];
		int newCvgNumber = currentDom.getOrderInfo()[4];
		String mailContent = null;
		
		// evaluation entitlement key expired
		if (leftDays <= 0) {
			history.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
			history.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
			newApNumber = currentDom.getOrderInfo()[1]-history.getNumberOfAps();
			newCvgNumber = currentDom.getOrderInfo()[4]-history.getNumberOfCvgs();
			
			mailContent = "The evaluation of "+history.getNumberOfAps()+" Devices and "+history.getNumberOfCvgs()
			+" VPN Gateway Virtual Appliances licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"' has expired.";
			
			changeApAndCvgNumber(currentDom, newApNumber, newCvgNumber, history.getDomainName(), changeDomain);
			
			updateBos.add(history);
			
		// evaluation left not more than 30 days
		} else if (leftDays < 31) {
			mailContent = "The evaluation of "+history.getNumberOfAps()+" Devices and "
			+history.getNumberOfCvgs()+" HiveOS Virtual Appliances is valid for "+(leftDays > 1 ? leftDays+" days" : "1 day")
			+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"'.";
		}
		
		// send email to admin
		if (null != mailContent) {
			if (!history.isSendEmail()) {
				LicenseInfo keyInfo = new LicenseInfo();
				keyInfo.setOrderKey(history.getOrderKey());
				keyInfo.setSystemId(mailContent);
				List<LicenseInfo> keyList = emailList.get(history.getDomainName());
				if (null == keyList) {
					keyList = new ArrayList<LicenseInfo>();
				}
				keyList.add(keyInfo);
				emailList.put(history.getDomainName(), keyList);
				if (leftDays > 0) {
					updateBos.add(history);
				}
			}
		}
	}
	
	private void dealwithSupportDate(OrderHistoryInfo history, Set<HmBo> updateBos, Map<String, DomainOrderKeyInfo> changeDomain, Map<String, List<LicenseInfo>> emailList) {
		long apLeftDays = 100l;
		long cvgLeftDays = 100l;
		// the current date
		long current = System.currentTimeMillis();
		
		if (OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL == history.getStatusFlag() && history.getNumberOfAps() > 0) {
			long apSupport = System.currentTimeMillis();
			if (history.getSubEndDate() <= 0) {
				if (history.getSupportEndDate() > 0) {
					apSupport = history.getSupportEndDate();
				}
			} else {
				apSupport = history.getSubEndDate();
			}
			
			// HiveAP subscription date
			apLeftDays = (apSupport - current) / (1000l * 60l * 60l * 24l);
			
			long apLeftHours = (apSupport - current) % (1000l * 60l * 60l * 24l);
			
			if (apLeftDays >= 0 && apLeftHours > (1000l * 60l * 60l * 6l)) {
				apLeftDays += 1;
			}
		}
		
		if (OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL == history.getCvgStatusFlag() && history.getNumberOfCvgs() > 0) {
			long cvgSupport = System.currentTimeMillis();;
			if (history.getCvgSubEndDate() <= 0) {
				if (history.getSupportEndDate() > 0) {
					cvgSupport = history.getSupportEndDate();
				}
			} else {
				cvgSupport = history.getCvgSubEndDate();
			}
			
			// CVG subscription date
			cvgLeftDays = (cvgSupport - current) / (1000l * 60l * 60l * 24l);
			
			long cvgLeftHours = (cvgSupport - current) % (1000l * 60l * 60l * 24l);
			
			if (cvgLeftDays >= 0 && cvgLeftHours > (1000l * 60l * 60l * 6l)) {
				cvgLeftDays += 1;
			}
		}
		
		// current licensed HiveAP and CVG number
		DomainOrderKeyInfo currentDom = changeDomain.get(history.getDomainName());
		if (null == currentDom) {
			currentDom = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class,
				"domainName", history.getDomainName());
		}
		
		int newApNumber = currentDom.getOrderInfo()[1];
		int newCvgNumber = currentDom.getOrderInfo()[4];
		String mailContent = null;
		
		// HiveAP subscription date expired
		if (apLeftDays <= 0) {
			history.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
			newApNumber = newApNumber>history.getNumberOfAps()?newApNumber-history.getNumberOfAps():0;
			
			// CVG subscription date expired
			if (cvgLeftDays <= 0) {
				history.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
				newCvgNumber = newCvgNumber>history.getNumberOfCvgs()?newCvgNumber-history.getNumberOfCvgs():0;
				
				mailContent = "The subscription of "+history.getNumberOfAps()+" Devices and "+history.getNumberOfCvgs()
				+" VPN Gateway Virtual Appliances licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"' has expired.";
				
			// CVG subscription date left not more than 30 days
			} else if (cvgLeftDays < 31) {
				mailContent = "The subscription of "+history.getNumberOfAps()+" Devices has expired and the subscription of "
				+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances is valid for "+(cvgLeftDays > 1 ? cvgLeftDays+" days" : "1 day")
				+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"'.";
			} else {
				mailContent = "The subscription of "+history.getNumberOfAps()+" Devices licensed by the entitlement key("
				+history.getOrderKey()+") of VHM '"+history.getDomainName()+"' has expired.";
			}
			changeApAndCvgNumber(currentDom, newApNumber, newCvgNumber, history.getDomainName(), changeDomain);
			
		// HiveAP subscription date left not more than 30 days
		} else if (apLeftDays < 31) {
			// CVG subscription date expired
			if (cvgLeftDays <= 0) {
				history.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);

				newCvgNumber = newCvgNumber>history.getNumberOfCvgs()?newCvgNumber-history.getNumberOfCvgs():0;
				changeApAndCvgNumber(currentDom, newApNumber, newCvgNumber, history.getDomainName(), changeDomain);
				
				mailContent = "The subscription of "+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances has expired and the subscription of "
				+history.getNumberOfAps()+" Devices is valid for "+(apLeftDays > 1 ? apLeftDays+" days" : "1 day")
				+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"'.";
				
			// CVG subscription date left not more than 30 days
			} else if (cvgLeftDays < 31) {
				mailContent = "The subscription of "+history.getNumberOfAps()+" Devices is valid for "+(apLeftDays > 1 ?
				apLeftDays+" days" : "1 day")+" and the subscription of "
				+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances is valid for "+(cvgLeftDays > 1 ? cvgLeftDays+" days" : "1 day")
				+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"'.";
			} else {
				mailContent = "The subscription of "+history.getNumberOfAps()+" Devices is valid for "
				+(apLeftDays > 1 ? apLeftDays+" days" : "1 day")
				+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM '"+history.getDomainName()+"'.";
			}
		} else {
			// CVG subscription date expired
			if (cvgLeftDays <= 0) {
				history.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
				
				newCvgNumber = newCvgNumber>history.getNumberOfCvgs()?newCvgNumber-history.getNumberOfCvgs():0;
				changeApAndCvgNumber(currentDom, newApNumber, newCvgNumber, history.getDomainName(), changeDomain);
				
				mailContent = "The subscription of "+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances licensed by the entitlement key("
				+history.getOrderKey()+") of VHM'"+history.getDomainName()+"' has expired.";
				
			// CVG subscription date left not more than 30 days
			} else if (cvgLeftDays < 31) {
				mailContent = "The subscription of "+history.getNumberOfCvgs()+" VPN Gateway Virtual Appliances is valid for "
				+(cvgLeftDays > 1 ? cvgLeftDays+" days" : "1 day")
				+" licensed by the entitlement key ("+history.getOrderKey()+") of VHM'"+history.getDomainName()+"'.";
			}
		}
		
		// send email to admin
		if (null != mailContent) {
			if (!history.isSendEmail()) {
				LicenseInfo keyInfo = new LicenseInfo();
				keyInfo.setOrderKey(history.getOrderKey());
				keyInfo.setSystemId(mailContent);
				List<LicenseInfo> keyList = emailList.get(history.getDomainName());
				if (null == keyList) {
					keyList = new ArrayList<LicenseInfo>();
				}
				keyList.add(keyInfo);
				emailList.put(history.getDomainName(), keyList);
			}
			updateBos.add(history);
		}
	}

	private void changeApAndCvgNumber(DomainOrderKeyInfo currentDom, int newApNumber, int newCvgNumber, String domName,
		Map<String, DomainOrderKeyInfo> changeDomain) {
		OrderHistoryInfo keyInfo = null;
		// check if has entitlement key with 0 device
		if (newApNumber == 0 && newCvgNumber == 0) {
			keyInfo = LicenseOperationTool.getEntitlementKeyWithZeroDevice(domName);
		}
		if (null != keyInfo) {
			currentDom.setOrderKey(keyInfo.getOrderKey());
			currentDom.setEncryptLicense(keyInfo.getLicenseType(), 0, 0, 0, 0);
		} else {
			currentDom.setEncryptLicense("0"+currentDom.getOrderInfo()[0], newApNumber, 0, 0, newCvgNumber);
		}
		changeDomain.put(domName, currentDom);
		
		LicenseInfo vhmLic = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domName);
		if (null != vhmLic) {
			vhmLic.setHiveAps(newApNumber);
			vhmLic.setCvgNumber(newCvgNumber);
			if (newApNumber == 0) {
				vhmLic.setZeroDeviceKeyValid(null != keyInfo);
			}
			HmBeLicenseUtil.VHM_ORDERKEY_INFO.put(domName, vhmLic);
		}
	}
	
	/**
	 * Stop your defined license timer.
	 *
	 * @param arg_Timer -
	 * @param arg_Future -
	 * @return String : the error message
	 */
	public static String stopLicenseTimer(ScheduledExecutorService arg_Timer, ScheduledFuture<?> arg_Future)
	{
		try {
			if (!arg_Timer.isShutdown()) {
				
				if (null != arg_Future) {
					arg_Future.cancel(false);
				}
						
				// Disable new tasks from being submitted.
				arg_Timer.shutdown();
				try {
					// Wait a while for existing tasks to terminate.
		            if (!arg_Timer.awaitTermination(5, TimeUnit.SECONDS)) {
		            	// Cancel currently executing tasks.
		            	arg_Timer.shutdownNow();

		                // Wait a while for tasks to respond to being canceled.
		                if (!arg_Timer.awaitTermination(5, TimeUnit.SECONDS)) {
		                	return "The license timer does not terminate.";
		                }
		            }
		        } catch (InterruptedException ie) {
		            // (Re-)Cancel if current thread also interrupted.
		            //arg_Timer.shutdownNow();
		        }
			}   	
		} catch (Exception e) {
			return "There is something wrong with timer stop.";
        }
		return null;
	}
	
	public void stopEvaluationTimer() {
		if(null != evaluationTimer) {
			String errorMes = stopLicenseTimer(evaluationTimer, evaluateFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_LICENSE, 
				"<BE Thread> Evaluation License timer is shutdown");
		}	
	}
	
	public void stopPermanentTimer() {
		if(null != permanentTimer) {
			String errorMes = stopLicenseTimer(permanentTimer, permanentFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_LICENSE, 
				"<BE Thread> Permanent Entitle Key timer is shutdown");
		}	
	}
	
	public void stopAllLicenseTimer() {
		stopEvaluationTimer();
		stopPermanentTimer();
	}
	
	/**
	 * Record the debug message.
	 *
	 * @param arg_Msg -
	 */
	public void myDebug(String arg_Msg)
	{
		log.error(arg_Msg);
	}
	
	/**
	 * Send entitle key expiration to admin user
	 *
	 * @param domName -
	 * @param content -
	 * @return boolean
	 */
	private boolean send(String domName, String content) {
		MailNotification mailNotification = QueryUtil.findBoByAttribute(MailNotification.class, "owner.domainName", domName);
		
		if (null != mailNotification) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification);
			mailUtil.setMailContentType("text/html");
			
			List<?> defaultUser = QueryUtil.executeQuery("SELECT emailAddress FROM "+HmUser.class.getSimpleName(), null, new FilterParams("defaultFlag = :s1 AND owner.domainName = :s2", 
				new Object[]{true, domName}));

			if (!defaultUser.isEmpty()) {
				String defEmail = (String)defaultUser.get(0);
				if (HmDomain.HOME_DOMAIN.equals(domName)) {
					if (null != mailNotification.getMailTo() && !defEmail.equalsIgnoreCase(mailNotification.getMailTo())) {
						mailUtil.addMailToAddr(defEmail);
					}
				} else {
					mailUtil.setMailTo(defEmail);
				}
			// get default user info from Portal
			} else if (!HmDomain.HOME_DOMAIN.equals(domName)) {
				try {
					List<?> vhmids = QueryUtil.executeQuery("SELECT vhmID FROM "+HmDomain.class.getSimpleName(), new SortParams("id"), new FilterParams("domainName", domName));
					if (!vhmids.isEmpty() && !StringUtils.isBlank((String)vhmids.get(0))) {
						VHMCustomerInfo vhmCustomer = ClientUtils.getPortalResUtils().getVHMCustomerInfo((String)vhmids.get(0));
						if (vhmCustomer != null && !StringUtils.isBlank(vhmCustomer.getPrimaryEmail())) {
							for (String priMail : vhmCustomer.getPrimaryUsers()) {
								mailUtil.addMailToAddr(priMail);
							}
						} else {
							log.error("send entitlement key expired email, FAILED to fetch customer primary info from Portal, no primary account email, this vhm is "+domName);
							return false;
						}
					} else {
						log.error("send entitlement key expired email, FAILED to fetch vhmid from database, this vhm is "+domName);
						return false;
					}
				} catch (Exception e1) {
					log.error("send entitlement key expired email, FAILED to fetch customer primary info from Portal", e1.getMessage(), e1);
					return false;
				}
			}
			
			mailUtil.setSubject(MgrUtil
					.getUserMessage("info.email.permanent.entitle.key.expiration.title"));
			mailUtil.setText(SendMailUtil.addHeadAndFoot(content));
			mailUtil.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");

			try {
				mailUtil.startSend();
				return true;
			} catch (Exception e) {
				log.error("send entitlement key expired email, FAILED to send email", e.getMessage(), e);
				return false;
			}
		}
		log.error("send entitlement key expired email, there is no email configuration in this domain "+domName);
		return false;
	}

}