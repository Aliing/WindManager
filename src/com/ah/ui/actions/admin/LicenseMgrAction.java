package com.ah.ui.actions.admin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.ah.be.activation.ActivationKeyOperation;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.hiveap.HiveAPInfoFromeDatabase;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.LicenseOperationTool;
import com.ah.be.license.OrderKeyManagement;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;

import edu.emory.mathcs.backport.java.util.Arrays;

public class LicenseMgrAction extends BaseAction {

	private static final long serialVersionUID = 1L;

    private static final Tracer		log	= new Tracer(LicenseMgrAction.class.getSimpleName());

	// for license
	public LicenseInfo				licenseInfo;

	public ActivationKeyInfo		activationInfo;

	public List<ActivationKeyInfo>	allActiveInfo;
	
	public static final String ORDERKEYINFO_EXPORT_FILE_NAME = "entitlementKeyExport.xls";
	
	public static final String ORDERKEYINFO_EXPORT_FILE_PATH = AhDirTools.getTempFileDir()+"entitlementKeyExport.xls";
	
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null && !"license".equals(forward)) {
			return forward;
		}
		try {
		    initLicenseAndActKeyInfo();
		    
			if ("import".equals(operation)) {
				// windows system can not import license
				if (os.toLowerCase().contains("windows")) {
					addActionError(MgrUtil.getUserMessage("error.licenseFailed.system.error"));
				} else {
					importLicenseFile();
				}
			} else if ("activate".equals(operation)) {

				// ActivationKeyOperation actOper = ActivationKeyOperation.getInstance();

				// actOper.sendQueryAndReceive(activationInfo, false, activationKey);

				// actOper.sendCollectionInfo();

				// actOper.sendClientMacInfo();

				// actOper.getNewVersionFlag();

				// activate activation key
				activateActivationKey();

			} else if ("activate_orderkey".equals(operation)) {
				// windows system can not import license
				if (os.toLowerCase().contains("windows")) {
					addActionError(MgrUtil.getUserMessage("error.licenseFailed.system.error"));
				} else {
					primaryOrderKey = primaryOrderKey.trim();
					String entitleKey = primaryOrderKey;
					if (installOrderKey()) {
						// check if it is acm entitlement key
						if (!QueryUtil.executeQuery("select id from "+AcmEntitleKeyHistoryInfo.class.getSimpleName(), 
								null, new FilterParams("entitleKey", entitleKey)).isEmpty()) {
							// get the enable client manager flag
							List<?> acmEnables = QueryUtil.executeQuery("select enableClientManagement from "+HMServicesSettings.class.getSimpleName(), 
									null, new FilterParams("owner.domainName", getDomain().getDomainName()));
							if (null == acmEnables || acmEnables.isEmpty()) {
								warnToEnableAcm = true;
							} else {
								warnToEnableAcm = !(Boolean)acmEnables.get(0);
							}
						}
					}
					//update LogSettings AlarmMaxRecords
					if(!isHMOnline()){
						updateAlarmMaxRecords();
					}
				}
			} else if ("update_umlicense".equals(operation)) {
				try {
					OrderKeyManagement.updateUserManagerLicenseFromLs();

					addActionMessage(MgrUtil.getUserMessage("message.user.manager.license.updated.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.manager.license"));
				} catch (Exception e) {
					addActionError(MgrUtil.getUserMessage("action.error.user.manager.license.update") + e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.manager.license"));
				}
			} else if ("enterKeyInTop".equals(operation)) {
				installEntitleKeyInTop();
				return "json";
			} else if ("submitUserRegInfo".equals(operation)) {
				if (null != userRegInfo) {
					userRegInfo.setOwner(getDomain());
					try {
						if (ClientSenderCenter.sendUserRegisterInfo(userRegInfo)) {
							userRegInfo.setActiveBySelf(true);
							
							UserRegInfoForLs oldInfo = QueryUtil.findBoByAttribute(UserRegInfoForLs.class, "owner.domainName", getDomain().getDomainName());			
							if (null == oldInfo) {
								QueryUtil.createBo(userRegInfo);
							} else {
								oldInfo.setActiveBySelf(true);
								oldInfo.setCompany(userRegInfo.getCompany());
								oldInfo.setCountry(userRegInfo.getCountry());
								oldInfo.setAddressLine1(userRegInfo.getAddressLine1());
								oldInfo.setAddressLine2(userRegInfo.getAddressLine2());
								oldInfo.setPostalCode(userRegInfo.getPostalCode());
								oldInfo.setName(userRegInfo.getName());
								oldInfo.setTelephone(userRegInfo.getTelephone());
								oldInfo.setEmail(userRegInfo.getEmail());
								QueryUtil.updateBo(oldInfo);
							}
							addActionMessage(MgrUtil.getUserMessage("info.config.guide.tellFriend.success"));
						} else {
							addActionError(MgrUtil.getUserMessage("info.config.guide.support.failed"));
						}
					} catch (Exception ex) {
						addActionError(ex.getMessage());
					}
				}
				userRegInfoStyle = "";
			} else if ("export_orderkey".equals(operation)) {
				exportEntitleKeyInfo();
				return "export";
			}
			if (getShowUserRegInfoWarning()) {
				userRegInfoStyle = "";
			}
			baseOperation();
			return prepareBoList();
			// return SUCCESS;
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LICENSEMGR);
		setDataSource(OrderHistoryInfo.class);
	}

	@Override
	public OrderHistoryInfo getDataSource() {
		return (OrderHistoryInfo) dataSource;
	}
	
	@Override
	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		FilterParams params = new FilterParams("licenseType != :s1 AND domainName = :s2", new Object[]{BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY, getDomain().getDomainName()});
		page = paging.executeQuery(sortParams, params);
	}
	
	@Override
	protected void enableSorting() {
		String sessionKey = boClass.getSimpleName() + "Sorting";
		sortParams = (SortParams) MgrUtil.getSessionAttribute(sessionKey);
		if (sortParams == null) {
			sortParams = new SortParams();
			sortParams.setPrimaryOrderBy("statusFlag");
			if (NmsUtil.isHMForOEM()) {
				sortParams.setOrderBy("activeTime");
				sortParams.setAscending(false);
			} else if (!NmsUtil.isHostedHMApplication()){
				sortParams.setOrderBy("supportEndDate");
				sortParams.setAscending(false);
			} else {
				sortParams.setOrderBy("cvgStatusFlag");
			}
			MgrUtil.setSessionAttribute(sessionKey, sortParams);
		}
		// So every sort tag doesn't need to specify a session key
		ActionContext.getContext().put(PAGE_SORTING, sortParams);
	}

	public void initLicenseAndActKeyInfo() {
		// for license and activation key
		// in home domain
		if (null == userContext || getIsInHomeDomain()) {
			licenseInfo = HmBeLicenseUtil.getLicenseInfo();

		// for vhm in hm online
		} else {
			licenseInfo = LicenseOperationTool.getOrderKeyInfoFromDatabase(getDomain().getDomainName());
			
			if (NmsUtil.isHostedHMApplication()) {
				HmBeLicenseUtil.VHM_ORDERKEY_INFO.put(getDomain().getDomainName(), licenseInfo);
			}
		}
		
		// get all the activation key information
		if (null != licenseInfo && licenseInfo.isUseActiveCheck()) {
			allActiveInfo = HmBeActivationUtil.getAllActivationInfoFromDb();
			for (ActivationKeyInfo actInfo : allActiveInfo) {
				// avoid of NullPointException
				if (null != BeLicenseModule.HIVEMANAGER_SYSTEM_ID && null != actInfo) {
					if (BeLicenseModule.HIVEMANAGER_SYSTEM_ID.equals(actInfo.getSystemId())) {
						activationInfo = actInfo;
						primaryActKey = actInfo.getActivationKey();
					} else {
						secondaryActKey = actInfo.getActivationKey();
					}
				}
			}
		}
		
		// init entitlement key
		List<OrderHistoryInfo> failKey = QueryUtil.executeQuery(OrderHistoryInfo.class, null, new FilterParams("licenseType = :s1 AND domainName = :s2", 
			new Object[]{BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY, getDomain().getDomainName()}));
		if (!failKey.isEmpty()) {
			primaryOrderKey = failKey.get(0).getOrderKey();
		}
	}

	public boolean activateActivationKey() {
		String activationKey;
		boolean ifNeedTimer = false;
		// activate one or two activation key
		int invalidTime = 0;
		for (ActivationKeyInfo actInfo : allActiveInfo) {
			if (null != actInfo) {
				if (BeLicenseModule.HIVEMANAGER_SYSTEM_ID.equals(actInfo.getSystemId())) {
					activationKey = primaryActKey;
				} else {
					activationKey = secondaryActKey;
				}
				// the current activation key is invalid
				if (null != activationKey && !"".equals(activationKey)) {
					// must input different activation key
					if ((actInfo.isActivateSuccess() || actInfo.isStartRetryTimer())
							&& activationKey.equals(actInfo.getActivationKey())) {
						addActionError(MgrUtil.getUserMessage("error.equal",
								new String[] { "The new Activation Key (" + activationKey + ")",
										"the current one" }));
						invalidTime++;
					} else {
						// send query with this activation key
						String resultMessage = ActivationKeyOperation.sendQueryAndReceive(actInfo, false,
								activationKey);

						// send successfully
						if ("".equals(resultMessage)) {
							addActionMessage(MgrUtil.getUserMessage(
									"info.licenseActivationKeyActivated", activationKey));
							generateAuditLog(HmAuditLog.STATUS_SUCCESS, getText(
									"activate.license.activation.key", new String[] { "Activate" })
									+ " (" + activationKey + ")");
							// update activation key info in database
							try {
								QueryUtil.updateBo(actInfo);
							} catch (Exception e) {
								addActionMessage(MgrUtil.getUserMessage("message.update.activation.key.failure")
										+ e.getMessage());
							}
							ifNeedTimer = true;

							// send unsuccessfully
						} else {
							addActionError(MgrUtil.getUserMessage(
									"error.licenseActivationKeyFailed", activationKey)
									+ " " + resultMessage);
							generateAuditLog(HmAuditLog.STATUS_FAILURE, getText(
									"activate.license.activation.key", new String[] { "Activate" })
									+ " (" + activationKey + ")");
							invalidTime = allActiveInfo.size();
						}
					}
				}
			}
		}
		if (ifNeedTimer) {
			// update the active information
			HmBeActivationUtil.ACTIVATION_KEY_TIMER.stopActiveTimer();

			HmBeActivationUtil.ACTIVATION_KEY_TIMER.activationKeyTimer();
		}

		if (HmBeActivationUtil.ifActivationKeyValid()) {
			HmBeActivationUtil.ACTIVATION_KEY_VALID = true;
		}

		if (invalidTime >= allActiveInfo.size()) {
			return false;
		}
		primaryActKey = "";
		secondaryActKey = "";
		return true;
	}

	public boolean importLicenseFile() {
		// check the license key
		List<LicenseHistoryInfo> licenseInfoBo = new ArrayList<LicenseHistoryInfo>();
		LicenseInfo newLicenseInfo = new LicenseInfo();
		String result = checkInputLicense(licenseInfoBo, newLicenseInfo);
		if (!result.equals("")) {
			addActionError(result);
		} else {
			// the hivemanager type or user management type
			short appType = BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM.equals(newLicenseInfo
					.getLicenseType()) ? LicenseHistoryInfo.LICENSE_TYPE_GM_LITE
					: LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER;
			String type = LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER == appType ? NmsUtil.getOEMCustomer().getNmsName()
					: "User Manager";
			String logInfo = getText("admin.license.import") + " (" + type + ")";
			try {
				if (HmBeLicenseUtil.importLicenseKey(licenseInfoBo, newLicenseInfo)) {
					addActionMessage(MgrUtil.getUserMessage("info.licenseImported", type));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, logInfo);

					if (LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER == appType) {
						doAfterInstallNewLicense();
					}
					primaryLicense = "";
					secondaryLicense = "";
					
					// remove the expired info in session
					MgrUtil.removeSessionAttribute(LICENSE_INFO_IN_TITLE_AREA);
					return true;
				} else {
					addActionError(MgrUtil.getUserMessage("error.licenseFailed", type));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, logInfo);
				}
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("error.licenseFailed", type));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, logInfo);
			}
		}
		return false;
	}
	
	public boolean installOrderKey() {
		// check the order key
		String checkName = checkKeyExistsIgnoreDomain(primaryOrderKey);
		
		if (null != checkName) {
			addActionError(checkName);
			return false;
		}
		String hmId = licenseInfo.getSystemId();
		HmDomain domain = getDomain();
		
		try {
		    if(!Jsoup.isValid(primaryOrderKey, Whitelist.none())) {
		        addActionError(MgrUtil.getUserMessage("error.license.orderkey.activate.Failed",
	                    new String[] { StringEscapeUtils.escapeHtml4(primaryOrderKey) }));
		        return false;
		    }
			OrderKeyManagement.activateOrderKey(primaryOrderKey, domain.getDomainName(), hmId);
			
			doAfterInstallNewLicense();

			addActionMessage(MgrUtil.getUserMessage("info.license.orderKeyActivated", primaryOrderKey));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.entitlement.key.enter",primaryOrderKey));
			
			primaryOrderKey= "";
			
			// remove the expired info in session
			MgrUtil.removeSessionAttribute(LICENSE_INFO_IN_TITLE_AREA);
			return true;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("error.license.orderkey.activate.Failed",
					new String[] { primaryOrderKey }) + "<br>" + e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.entitlement.key.enter",primaryOrderKey));
		}
		return false;
	}
	
	private void updateAlarmMaxRecords(){
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if(list.isEmpty()){
			return;
		}
		LogSettings bo=list.get(0);
		int maxRecords=LogSettings.getConvertMaxRecords(10*HmBeLicenseUtil.getLicenseInfo().getHiveAps());
		bo.setAlarmMaxRecords(maxRecords);
		try {
			QueryUtil.updateBo(bo);
		} catch (Exception e) {
			log.error("updateAlarmMaxRecords", "Update log settings catch exception!", e);
		}
	}
	public interface LicenseReturnedInfoEncap {
		void encap(LicenseInfo licenseInfo, JSONObject jsonObject) throws JSONException;
	}
	public void installEntitleKeyInTop() throws JSONException {
		installEntitleKeyInTop(null);
	}
	public void installEntitleKeyInTop(LicenseReturnedInfoEncap returnedInfoEncap) throws JSONException {
		jsonObject = new JSONObject();
		
		// entitlement key
		if (null != primaryOrderKey) {
			primaryOrderKey = primaryOrderKey.trim();
			// check the order key
			String checkName = checkKeyExistsIgnoreDomain(primaryOrderKey);

			if (null != checkName) {
				jsonObject.put("result", false);
				jsonObject.put("message", checkName);
				return;
			}
			String hmId = licenseInfo.getSystemId();
			HmDomain domain = getDomain();
			
			try {
				OrderKeyManagement.activateOrderKey(primaryOrderKey, domain.getDomainName(), hmId);
				
				doAfterInstallNewLicense();
				
				jsonObject.put("result", true);
				
				String daysStr = licenseInfo.getTotalTime();
				
				// permanent entitle key
				if ("".equals(daysStr)) {
					daysStr = "It is "+(hmId.length() > 10 ? "a permanent ":"a subscription ")+" key.";
				} else {
					daysStr = "It is valid for "+daysStr+".";
				}
				jsonObject.put("message", MgrUtil.getUserMessage("enter.entitlement.key.message")+daysStr);
				
				if (returnedInfoEncap != null) {
					returnedInfoEncap.encap(licenseInfo, jsonObject);
				}
				
				generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.entitlement.key.enter",primaryOrderKey));
			} catch (Exception e) {
				jsonObject.put("result", false);
				jsonObject.put("message", MgrUtil.getUserMessage("error.license.orderkey.activate.Failed",
					new String[] { primaryOrderKey }) + " " + e.getMessage());
				generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.entitlement.key.enter",primaryOrderKey));
			}
		// license
		} else {
			// check the license key
			List<LicenseHistoryInfo> licenseInfoBo = new ArrayList<LicenseHistoryInfo>();
			LicenseInfo newLicenseInfo = new LicenseInfo();
			String result = checkInputLicense(licenseInfoBo, newLicenseInfo);
			if (!result.equals("")) {
				jsonObject.put("result", false);
				jsonObject.put("message", result);
			} else {
				// the hivemanager type
				String logInfo = getText("admin.license.import") + " (" + NmsUtil.getOEMCustomer().getNmsName() + ")";
				try {
					if (HmBeLicenseUtil.importLicenseKey(licenseInfoBo, newLicenseInfo)) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, logInfo);

						doAfterInstallNewLicense();
						
						jsonObject.put("result", true);
						
						String daysStr = licenseInfo.getTotalTime();
						
						// permanent license
						if ("".equals(daysStr)) {
							daysStr = "permanent";
						}
						jsonObject.put("message", MgrUtil.getUserMessage("enter.license.key.message")+daysStr+".");
						
						if (returnedInfoEncap != null) {
							returnedInfoEncap.encap(newLicenseInfo, jsonObject);
						}
					} else {
						jsonObject.put("result", false);
						jsonObject.put("message", MgrUtil.getUserMessage("error.licenseFailed.inTopArea"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE, logInfo);
					}
				} catch (Exception e) {
					jsonObject.put("result", false);
					jsonObject.put("message", MgrUtil.getUserMessage("error.licenseFailed.inTopArea"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, logInfo);
				}
			}
		}
	}
	
	public String getDayLeftStr(int hours) {
		int days = hours / 24;
		return days > 1 ? (days + " days") : "1 day";
	}
	
	public String getSupportEvalSubsInfo() {
		if (null != licenseInfo && !"".equals(licenseInfo.getOrderKey()) 
			&& !DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(licenseInfo.getOrderKey())) {
		
			List<OrderHistoryInfo> historyInfo;
			FilterParams filterPar = null;
			String sqlStr = "domainName = :s1 AND (statusFlag = :s2 OR cvgStatusFlag = :s3)";
			
			// whole hm
			SortParams sortPara = new SortParams();
			sortPara.setPrimaryOrderBy("licenseType");
			
			if (getIsInHomeDomain()) {
				if (!NmsUtil.isHostedHMApplication()) {
					filterPar = new FilterParams(sqlStr, new Object[]{HmDomain.HOME_DOMAIN, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL, 
						OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL});
					sortPara.setOrderBy("supportEndDate");
					sortPara.setAscending(false);
				}
				
			// for vhm in hm online
			} else {
				if (NmsUtil.isHostedHMApplication()) {
					filterPar = new FilterParams(sqlStr, new Object[]{getDomain().getDomainName(),
						OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL});
					sortPara.setOrderBy("subEndDate");
					sortPara.setAscending(false);
				}
			}
			
			if (null != filterPar) {
				historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, sortPara, filterPar);
				
				if (historyInfo.isEmpty()) {
					return "";
				}
				
				// hmonline
				long bigDate = 0l;
				
				// the current date
				long current = System.currentTimeMillis();
				
				// subscription or support end date compare with the current date
				long support = System.currentTimeMillis();
				
				String evalKey = null;
				
				String permKey = null;

				for (OrderHistoryInfo hisInfo : historyInfo) {
					if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(hisInfo.getLicenseType())) {
						if ((support - hisInfo.getActiveTime()) / (1000l * 60l * 60l * 24l) < 31) {
							support = hisInfo.getActiveTime()+(hisInfo.getNumberOfEvalValidDays()*1000l * 60l * 60l * 24l);
							evalKey = hisInfo.getOrderKey();
							break;
						}
					} else if (!NmsUtil.isHMForOEM()){
						if (getIsInHomeDomain()) {
							if (hisInfo.getSupportEndDate() > bigDate) {
								bigDate = hisInfo.getSupportEndDate();
							}
						} else {
							if (hisInfo.getSubEndDate() > bigDate) {
								bigDate = hisInfo.getSubEndDate();
								permKey = hisInfo.getOrderKey();
							}
							if (hisInfo.getCvgSubEndDate() > bigDate) {
								bigDate = hisInfo.getCvgSubEndDate();
								permKey = hisInfo.getOrderKey();
							}
						}
					}
				}
				if (bigDate > 0) {
					support = bigDate;
				}
				long leftDays = (support - current) / (1000l * 60l * 60l * 24l);
				
				long leftHours = (support - current) % (1000l * 60l * 60l * 24l);
				
				if (leftHours > (1000l * 60l * 60l * 6l)) {
					leftDays += 1;
				}
				if (leftDays <= 0 && (null != evalKey || null != permKey)) {
					leftDays = 1;
				}
				
				// the subscription date is less than the 30 days
				if (leftDays < 31) {
					if (null != evalKey) {
						return MgrUtil.getUserMessage("info.license.permanent.entitlement.key.will.expired", new String[]{"evaluation key ("
							+evalKey+")", leftDays > 1 ? (leftDays + " days") : "1 day"});
					} else if (null != permKey) {
						return MgrUtil.getUserMessage("info.license.entitlement.key.evaluation.or.support", new String[]{
							leftDays > 1 ? (leftDays + " days remain") : "1 day remains", "subscription key ("+permKey+")"});
					} else {
						if (leftDays > 0) {
							return MgrUtil.getUserMessage("info.license.entitlement.key.evaluation.or.support", new String[]{
								leftDays > 1 ? (leftDays + " days remain") : "1 day remains", NmsUtil.getOEMCustomer().getNmsName()+" Technical Support contract"});
						} else {
							return MgrUtil.getUserMessage("info.license.entitlement.key.expired", NmsUtil.getOEMCustomer().getNmsName()
								+" Technical Support contract");
						}
					}
				}
			}
		}
		return "";
	}
	
	public static String checkKeyExistsIgnoreDomain(String entitleKey) {
		if (StringUtils.isBlank(entitleKey)) {
			return MgrUtil.getUserMessage("error.license.activation.key.input", "entitlement key");
		}
		List<?> boIds = QueryUtil.executeQuery("select id from " + OrderHistoryInfo.class.getSimpleName(), null,
			new FilterParams("licenseType != :s1 AND orderKey = :s2", 
					new Object[]{BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY, entitleKey}));
		if (!boIds.isEmpty()) {
			return MgrUtil.getUserMessage("error.objectExists", entitleKey);
		}
		boIds = QueryUtil.executeQuery("select id from " + AcmEntitleKeyHistoryInfo.class.getSimpleName(), null,
				new FilterParams("entitleKey", entitleKey));
		if (!boIds.isEmpty()) {
			return MgrUtil.getUserMessage("error.objectExists", entitleKey);
		}
		return null;
	}
	
	private void doAfterInstallNewLicense() {
		// the license and activation key maybe change
		initLicenseAndActKeyInfo();
		
		if (getIsInHomeDomain()) {
			// send new application information if hmol
			RemotePortalOperationRequest.updateApplicationInfo(licenseInfo);
		} else {
			RemotePortalOperationRequest.updateVHMInfo(getDomain().getDomainName(), licenseInfo.getHiveAps());
		}
	}

	public String getSystemId() {
		if (os.toLowerCase().contains("linux")) {
			return BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
		}
		return "";
	}

	public boolean ifActKeyInvalid(String arg_System) {
		if (null != allActiveInfo) {
			for (ActivationKeyInfo actInfo : allActiveInfo) {
				if (arg_System.equals(actInfo.getSystemId())) {
					return !actInfo.isStartRetryTimer() && !actInfo.isActivateSuccess();
				}
			}
		}
		return false;
	}

	public String getActivationDisplay() {
		return (!ifVmware && licenseInfo.isUseActiveCheck()) ? "" : "none";
	}
	
	public String getActivationMessage() {
		if (null != activationInfo) {
			// there is no activation key information
			if ("".equals(activationInfo.getActivationKey())) {
				int period = activationInfo.getQueryPeriodLeft();
				if (period > 0) {
					int days = period / 24;
					return MgrUtil.getUserMessage("info.license.activation.key.input",
							new String[] { "activation key", (days > 0 ? " "+days + (days > 1 ?" days":" day") : "")
									+ (period % 24 > 0 ? " " + period % 24 + (period % 24 > 1 ?" hours":" hour") : "") });
				} else {
					return MgrUtil.getUserMessage("error.license.activation.key.input");
				}
				// show the warning message
			} else {
				StringBuilder strBuf = new StringBuilder();
				// there are two activation keys
				if (null != twoSystemId) {
					if (ifActKeyInvalid(twoSystemId[0])) {
						strBuf.append("The activation key ").append(primaryActKey).append(" is invalid.");
					}
					if (ifActKeyInvalid(twoSystemId[1])) {
						strBuf.append("<br>" + "The activation key ").append(secondaryActKey).append(" is invalid.");
					}
					// there is only one activation key
				} else if (ifActKeyInvalid(BeLicenseModule.HIVEMANAGER_SYSTEM_ID)) {
					strBuf.append("The activation key ").append(primaryActKey).append(" is invalid.");
				}
				if (strBuf.length() == 0) {
					strBuf.append(MgrUtil.getUserMessage("info.license.activation.key.query"));
				}
				return strBuf.toString();
			}
		}
		return "";
	}

	public String	primaryLicense;

	public String	secondaryLicense;

	public String	primaryActKey;

	public String	secondaryActKey;
	
	public String   primaryOrderKey;

	public void setPrimaryLicense(String primaryLicense) {
		this.primaryLicense = primaryLicense;
	}

	public void setSecondaryLicense(String secondaryLicense) {
		this.secondaryLicense = secondaryLicense;
	}

	/**
	 * Check the input license data.
	 * 
	 * @param licenseInfoBo -
	 * @param newLicenseInfo -
	 * @return the error message
	 */
	public String checkInputLicense(List<LicenseHistoryInfo> licenseInfoBo, LicenseInfo newLicenseInfo) {
		// get primary system id and secondary system id
		if (null == primaryLicense || primaryLicense.equals("")) {
			return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The primary");
		}
		primaryLicense = primaryLicense.trim().replaceAll(" ", "-");
		if (null != twoSystemId) {
			if (null == secondaryLicense || secondaryLicense.equals("")) {
				return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The secondary");
			}
			secondaryLicense = secondaryLicense.trim().replaceAll(" ", "-");
		}
		HM_License hm_l = HM_License.getInstance();
		String systemId = hm_l.get_system_id();
		String priLicense;
		String licenseKey = primaryLicense;

		// get the license info from this file
		if (null != twoSystemId) {
			String seconLicense;
			if (systemId.equals(twoSystemId[0])) {
				priLicense = hm_l.decrypt_from_string(systemId, primaryLicense);
				seconLicense = hm_l.decrypt_from_string(twoSystemId[1], secondaryLicense);
			} else {
				priLicense = hm_l.decrypt_from_string(systemId, primaryLicense);
				licenseKey = secondaryLicense;
				seconLicense = hm_l.decrypt_from_string(twoSystemId[0], secondaryLicense);
			}
			// the license key is invalid
			if (!checkLicenseLength(priLicense)) {
				return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The primary");
			}
			// the license key is invalid
			if (!checkLicenseLength(seconLicense)) {
				return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The secondary");
			}
			if (!priLicense.equals(seconLicense)) {
				return MgrUtil.getUserMessage("error.licenseFailed.ha.license.info.same");
			}
		} else {
			priLicense = hm_l.decrypt_from_string(systemId, primaryLicense);
			// the license key is invalid
			if (!checkLicenseLength(priLicense)) {
				return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The");
			}
		}

		String type = priLicense.substring(0, BeLicenseModule.LICENSE_TYPE_INDEX);
		int hiveAPs = Integer.parseInt(priLicense.substring(BeLicenseModule.LICENSE_TYPE_INDEX,
				BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX));
		int hours = Integer.parseInt(priLicense.substring(BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX,
				BeLicenseModule.LICENSE_KEY_LENGTH));
		int vhmNum = 1;
		if (priLicense.length() >= BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH) {
			vhmNum = Integer.parseInt(priLicense.substring(BeLicenseModule.LICENSE_KEY_LENGTH,
					BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH));
		}
		// the license can be HiveManager or GM Light
		short appType;
		if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(type)
				|| BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(type)
				|| BeLicenseModule.LICENSE_TYPE_DEVELOP_NUM.equals(type)
				|| BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(type)) {
			appType = LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER;
		} else if (BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM.equals(type)) {
			appType = LicenseHistoryInfo.LICENSE_TYPE_GM_LITE;
		} else {
			return MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "The");
		}

		// select all the license history info
		String where = "systemId = :s1 AND licenseString = :s2 AND type = :s3";
		Object[] values = new Object[3];
		values[0] = systemId;
		values[1] = licenseKey;
		values[2] = appType;
		List<?> boIds = QueryUtil.executeQuery("select id from " + LicenseHistoryInfo.class.getSimpleName(), null,
				new FilterParams(where, values));
		// cannot import the same license as before
		if (!boIds.isEmpty()) {
			return MgrUtil.getUserMessage("error.licenseFailed.new.license");
		}

		// check hivemanager license
		if (appType == LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER) {
			// activation key flag
			boolean ifActive = false;
			if (priLicense.length() >= BeLicenseModule.LICENSE_KEY_ADD_ACTIVATION_LENGTH) {
				ifActive = priLicense.substring(BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH,
						BeLicenseModule.LICENSE_KEY_ADD_ACTIVATION_LENGTH).equals(
						BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM);
			}

			/*
			 * Get the active license information is using now.
			 */
			int activeVhmNumber = 1;
			int activeApNumber = 1;
			if (null != licenseInfo && !BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(licenseInfo.getLicenseType())) {
				activeVhmNumber = licenseInfo.getVhmNumber();
				activeApNumber = licenseInfo.getHiveAps();
			}

			// vmware license
			// ap number cannot more than the limit
			// vhm number must equal the limit
			if (BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(type)
					&& (BeLicenseModule.VMWARE_LICENSE_SUPPORT_AP_COUNT < hiveAPs || BeLicenseModule.VMWARE_LICENSE_SUPPORT_VHM_COUNT != vhmNum)) {
				return MgrUtil.getUserMessage("error.licenseFailed.vmware.type.error", "VMware");
			}

			// in vmware platform
			if (hm_l.isVirtualMachineSystem()) {
				if (priLicense.length() == BeLicenseModule.LICENSE_KEY_LENGTH) {
					vhmNum = BeLicenseModule.VMWARE_LICENSE_SUPPORT_VHM_COUNT;
				}
				if (!ifActive) {
					return MgrUtil.getUserMessage("error.licenseFailed.vmware.activation.key");
				}
			} else {
				// hardware hm does not need activation key
				if (ifActive) {
					return MgrUtil.getUserMessage("error.licenseFailed.hardware.activation.key");
				}

				// must not import vmware license in non-vmware platform
				if (BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(type)) {
					return MgrUtil.getUserMessage("error.licenseFailed.vmware.type.error",
							"non-VMware");
				}

				if (null != licenseInfo) {

					// the new license type must be permanent
					if (BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseInfo
							.getLicenseType())
							&& !BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(type)) {
						return MgrUtil.getUserMessage("error.licenseFailed.type.error");
					}
				}
			}

			if (null != licenseInfo) {

				// the new license cannot support less hiveAPs
				if (activeApNumber > hiveAPs) {
					return MgrUtil.getUserMessage("error.licenseFailed.more.hiveaps", NmsUtil.getOEMCustomer().getAccessPonitName()+"s");
				}

				// the new license cannot support less vhm
				if (activeVhmNumber > vhmNum) {
					return MgrUtil.getUserMessage("error.licenseFailed.more.hiveaps", "V"+NmsUtil.getOEMCustomer().getNmsNameAbbreviation()+"s");
				}
			}
			// set the license information to memory
			newLicenseInfo.setLicenseType(type);
			newLicenseInfo.setHiveAps(hiveAPs);
			newLicenseInfo.setVhmNumber(vhmNum);
			newLicenseInfo.setTotalDays(hours / 24);
			newLicenseInfo.setLeftHours(hours);
			newLicenseInfo.setUseActiveCheck(ifActive);
			newLicenseInfo.setSystemId(systemId);
		} else {
			// set the license information to memory
			newLicenseInfo.setLicenseType(type);
			newLicenseInfo.setHiveAps(hiveAPs);
			newLicenseInfo.setTotalDays(hours / 24);
			newLicenseInfo.setLeftHours(hours);
		}

		// add the new license to database
		licenseInfoBo.clear();
		if (null != twoSystemId) {
			licenseInfoBo.add(0, getLicenseHistoryBo(appType, twoSystemId[0], primaryLicense));
			licenseInfoBo.add(getLicenseHistoryBo(appType, twoSystemId[1], secondaryLicense));
		} else {
			licenseInfoBo.add(getLicenseHistoryBo(appType, systemId, primaryLicense));
		}
		return "";
	}

	/**
	 * check the length of license string
	 * 
	 * @param arg_Lic
	 *            license string
	 * @return -
	 */
	private boolean checkLicenseLength(String arg_Lic) {
		return null != arg_Lic
				&& (arg_Lic.length() == BeLicenseModule.LICENSE_KEY_LENGTH
				|| arg_Lic.length() == BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH
				|| arg_Lic.length() >= BeLicenseModule.LICENSE_KEY_ADD_ACTIVATION_LENGTH);
	}

	/**
	 * Make up new license bo
	 * 
	 * @param arg_Type HiveManager License or GM Lite License
	 * @param arg_System -
	 * @param arg_License -
	 * @return LicenseHistoryInfo
	 */
	private LicenseHistoryInfo getLicenseHistoryBo(short arg_Type, String arg_System,
			String arg_License) {
		LicenseHistoryInfo historyInfo = new LicenseHistoryInfo();
		historyInfo.setActive(true);
		historyInfo.setSystemId(arg_System);
		historyInfo.setLicenseString(arg_License);
		AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(arg_System);
		historyInfo.setHoursUsed(encryptTool.encrypt("0" + arg_System));
		historyInfo.setType(arg_Type);
		return historyInfo;
	}
	
	public String getOrderKeyInfoByStr() {
		StringBuilder result = new StringBuilder();
		String nmsName = NmsUtil.getOEMCustomer().getNmsNameAbbreviation();
		String apName = "devices";
		if (!NmsUtil.isHMForOEM()) {
			apName = "Aerohive devices";
		}
		
		if (null != licenseInfo) {
			if (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)) {
				int period = licenseInfo.getLeftHours();
				// HiveManager domain user
				if (getIsInHomeDomain()) {
					result.append(NmsUtil.getOEMCustomer().getNmsName()).append(" System ID: ").append(getSystemId());
					result.append("<br><br>");
				// HiveManager Online user
				} else if (NmsUtil.isHostedHMApplication()) {
					result.append("V").append(nmsName).append(" ID: ").append(getDomain().getVhmID());
					result.append("<br><br>");
				}
				result.append("<font color=\"red\">");
				if (period > 0) {
					int days = period / 24;
					result.append(MgrUtil.getUserMessage("info.license.activation.key.input", new String[] { getTitleParam(), (days > 0 ? " "+days + (days > 1 ?" days":" day") : "")
							+ (period % 24 > 0 ? " " + period % 24 + (period % 24 > 1 ?" hours":" hour") : "") }));
				} else {
					result.append(MgrUtil.getUserMessage("error.license.activation.key.input", getTitleParam()));	
				}
				result.append("</font>");
			// simple vhm user
			} else if (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_SIMPLE_VHM)) {
				result.append("Number of ").append(apName).append(" licensed: ").append(licenseInfo.getHiveAps());
				result.append("<br><br>");
			} else {
				// HiveManager domain user
				if (getIsInHomeDomain()) {
					result.append(NmsUtil.getOEMCustomer().getNmsName()).append(" System ID: ").append(getSystemId());
					result.append("<br><br>");
//					result.append("License Type : ").append(licenseInfo.getLicenseTypeStr());
//					result.append("<br><br>");
					result.append("Number of ").append(apName).append(" licensed: ").append(licenseInfo.getHiveAps());
					result.append("<br><br>");
					result.append("Number of VPN Gateway Virtual Appliances licensed: ").append(licenseInfo.getCvgNumber());
					result.append("<br><br>");
					result.append("Number of V").append(nmsName).append("s licensed: ").append(licenseInfo.getVhmNumber());
					result.append("<br><br>");
				// HiveManager Online user
				} else if (NmsUtil.isHostedHMApplication()) {
					result.append("V").append(nmsName).append(" ID: ").append(getDomain().getVhmID());
					result.append("<br><br>");
//					if (NmsUtil.isProduction()) {
//						result.append("License Type : ").append(licenseInfo.getLicenseTypeStr());
//						result.append("<br><br>");
//					}
					result.append("Number of ").append(apName).append(" licensed: ").append(licenseInfo.getHiveAps());
					result.append("<br><br>");
					result.append("Number of VPN Gateway Virtual Appliances licensed: ").append(licenseInfo.getCvgNumber());
					result.append("<br><br>");
				}
				// evaluation entitle key
				if ("".equals(licenseInfo.getOrderKey()) && licenseInfo.getTotalDays() > 0) {
					result.append("Time Supported: ").append(licenseInfo.getTotalTime());
					result.append("<br><br>");
					result.append("Effective Time: ").append(licenseInfo.getExpireDate());
					result.append("<br><br>");
				}
				if (ifVmware && licenseInfo.isUseActiveCheck()) {
					if (null != activationInfo) {
						result.append("<font color=\"red\">");
						
						// there is no activation key information
						if ("".equals(activationInfo.getActivationKey())) {
							int period = activationInfo.getQueryPeriodLeft();
							if (period > 0) {
								int days = period / 24;
								result.append(MgrUtil.getUserMessage("info.license.activation.key.input", new String[] { "entitlement key", (days > 0 ? " "+days + (days > 0 ?" days":" day") : "")
										+ (period % 24 > 0 ? " " + period % 24 + (period % 24 > 1 ?" hours":" hour") : "") }));
							} else {
								result.append(MgrUtil.getUserMessage("error.license.activation.key.input", "entitlement key"));	
							}
							
							// show the warning message
						} else if(!HmBeActivationUtil.ACTIVATION_KEY_VALID) {
							result.append(MgrUtil.getUserMessage("error.license.activation.key.input", "entitlement key"));	
						}
						result.append("</font>");
					}
				}
				
			}
			// user manager license
			if (getIsInHomeDomain()) {
				if (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)) {
					if (!NmsUtil.isHostedHMApplication()) {
						result.append("<br><br>");
						result.append(NmsUtil.getOEMCustomer().getNmsName()).append(" Activated: <font color=\"red\">No</font>");
					}
				} else {
					if (!NmsUtil.isHostedHMApplication()) {
						result.append(NmsUtil.getOEMCustomer().getNmsName()).append(" Activated: Yes<br><br>");
					}
					if (null == getGmLicense()) {
						result.append("User Manager License: <font color=\"red\">Not Installed</font>");
					} else {
						result.append("User Manager License: Installed");
					}
				}
			}
			
			// aerohive client manager license
			if ((NmsUtil.isHostedHMApplication() || getIsInHomeDomain())
					&& (!licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)
					|| QueryUtil.findRowCount(AcmEntitleKeyHistoryInfo.class, new FilterParams("domainName", getDomain().getDomainName())) > 0)
					&& isFullMode()) {
				// VHM no need this
				if (getIsInHomeDomain() || licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)) {
					result.append("<br><br>");
				}
				result.append(LicenseOperationTool.getAcmEntitleKeyInfoStr(getDomain().getDomainName()));
			} 
		}
		
		return result.toString();
	}
	
	public String getDeviceInfoByStr() {
		StringBuilder result = new StringBuilder();
		Map<String, Integer> apcount = HiveAPInfoFromeDatabase.getManagedDeviceTypeAndNumber(getDomain().getDomainName());
		result.append("<b>"+(isOEMSystem() ? "" : "Aerohive ") + "Devices in "+NmsUtil.getOEMCustomer().getNmsName()+"</b><br><br>");
		int apCount = 0;
		int vpnCount = 0;
		StringBuilder resultDevice = new StringBuilder();
		if (null != apcount) {
			Object[] apType = apcount.keySet().toArray();
			Arrays.sort(apType);
			for (Object typeName : apType) {
				
				// VPN Gateway VA does not belong to device
				if (MgrUtil.getEnumString("enum.hiveAp.model.10").equals(typeName)) {
					vpnCount = apcount.get(typeName);
				} else {
					resultDevice.append("&nbsp;&nbsp;&nbsp;"+typeName+"&nbsp;&nbsp;"+apcount.get(typeName)+"<br><br>");
					apCount += apcount.get(typeName);
				}
			}
		}
		result.append("&nbsp;&nbsp;&nbsp;" + (isOEMSystem() ? "" : "Aerohive ") + "Devices&nbsp;&nbsp;");
		if (apCount > 0) {
			deviceInventoryDetail = resultDevice.toString();
			result.append("<a href=\"javascript:getDeviceInventoryDetail();\">"+apCount+"</a><br><br>");
		} else {
			result.append(apCount+"<br><br>");
		}
		result.append("&nbsp;&nbsp;&nbsp;VPN Gateway Virtual Appliances&nbsp;&nbsp;"+vpnCount);
		return result.toString();
	}

	public boolean getCanImportLicense() {
		if ((userContext.isSuperUser() && userContext.getSwitchDomain() == null) || (NmsUtil.isProduction() && userContext.getDefaultFlag())) {
			// fix bug 32340
			HmUserGroup userGroup = userContext.getUserGroup();
			if (null != userGroup) {
				return HmUserGroup.ADMINISTRATOR.equals(userGroup.getGroupName()) || HmUserGroup.CONFIG.equals(userGroup.getGroupName());
			}
		}
		return false;
	}
	
	public String getTitleParam() {
		return ifVmware ? "entitlement key" : "license key or entitlement key";
	}
	
	public boolean getShowUpUserRegInfoInLsPage() {
		if (null != licenseInfo && !isOEMSystem()) {
			if ((NmsUtil.isHostedHMApplication() && !getIsInHomeDomain()) || (!NmsUtil.isHostedHMApplication() && getIsInHomeDomain())) {
				if (!licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)) {
					return null != licenseInfo.getOrderKey() && !"".equals(licenseInfo.getOrderKey());
				}
			}
		}
		return false;
	}
	
	private String licenseStyle = "none"; // default
	
	private String userRegInfoStyle = "none";

	public String getUserRegInfoStyle()
	{
		return userRegInfoStyle;
	}

	public void setUserRegInfoStyle(String userRegInfoStyle)
	{
		this.userRegInfoStyle = userRegInfoStyle;
	}

	public String getPrimaryLicense() {
		return primaryLicense;
	}

	public String getSecondaryLicense() {
		return secondaryLicense;
	}

	public String getPrimaryActKey() {
		return primaryActKey;
	}

	public void setPrimaryActKey(String primaryActKey) {
		this.primaryActKey = primaryActKey;
	}

	public String getSecondaryActKey() {
		return secondaryActKey;
	}

	public void setSecondaryActKey(String secondaryActKey) {
		this.secondaryActKey = secondaryActKey;
	}

	public LicenseInfo getGmLicense() {
		return HmBeLicenseUtil.GM_LITE_LICENSE_INFO;
	}

	public LicenseInfo getHmLicense() {
		return licenseInfo;
	}

	public String getPrimaryOrderKey()
	{
		return primaryOrderKey;
	}

	public void setPrimaryOrderKey(String primaryOrderKey)
	{
		this.primaryOrderKey = primaryOrderKey;
	}

	public String getLicenseStyle()
	{
		return licenseStyle;
	}

	public void setLicenseStyle(String licenseStyle)
	{
		this.licenseStyle = licenseStyle;
	}
	
	private boolean warnToEnableAcm;

	public boolean getWarnToEnableAcm() {
		return warnToEnableAcm;
	}

	public void setWarnToEnableAcm(boolean warnToEnableAcm) {
		this.warnToEnableAcm = warnToEnableAcm;
	}
	
	private void exportEntitleKeyInfo() {
		try {
			// create a new file
			FileOutputStream out = new FileOutputStream(ORDERKEYINFO_EXPORT_FILE_PATH);
			// create a new workbook
			HSSFWorkbook wb = new HSSFWorkbook();
			// create a new sheet
			HSSFSheet s = wb.createSheet("Sheet1");
			// declare a row object reference
			HSSFRow r = null;
			// declare a cell object reference
			HSSFCell c = null;
			// row index
			int rowNum = 0;
			// cell count
			int cellcount = 0;
			if (NmsUtil.isHMForOEM()) {
				cellcount = 7;
			} else if (getIsInHomeDomain()) {
				cellcount = 8;
			} else if (NmsUtil.isHostedHMApplication()) {
				cellcount = 9;
			}
			if (cellcount == 0) {
				return;
			} else {
				for (int i = 0; i < cellcount; i++) {
					s.setColumnWidth(i, getColumnWidthByIndex(i)*256);
				}
			}
			// create cell style
			HSSFCellStyle cs = wb.createCellStyle();
			// create font object
			HSSFFont f = wb.createFont();

			//set font to 12 point type
			f.setFontHeightInPoints((short) 12);
			f.setFontName("Calibri");

			//set cell stlye
			cs.setFont(f);
			cs.setAlignment(CellStyle.ALIGN_CENTER);
			cs.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
			HSSFCellStyle cs1 = wb.createCellStyle();
			// create font object
			HSSFFont f1 = wb.createFont();
	
			//set font to 12 point type
			f1.setFontHeightInPoints((short) 12);
			f1.setFontName("Calibri");
			f1.setBoldweight(Font.BOLDWEIGHT_BOLD);
	
			//set cell stlye
			cs1.setFont(f1);
			cs1.setAlignment(CellStyle.ALIGN_RIGHT);
			cs1.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
		 	HSSFCellStyle cs2 = wb.createCellStyle();
		 	cs2.setFont(f1);
			cs2.setAlignment(CellStyle.ALIGN_CENTER);
			cs2.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
		 	HSSFCellStyle cs3 = wb.createCellStyle();
		 	cs3.setFont(f);
			cs3.setAlignment(CellStyle.ALIGN_RIGHT);
			cs3.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
		 	HSSFCellStyle cs4 = wb.createCellStyle();
		 	cs4.setFont(f1);
			cs4.setAlignment(CellStyle.ALIGN_LEFT);
			cs4.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
		 	HSSFCellStyle cs5 = wb.createCellStyle();
		 	// create font object
			HSSFFont f2 = wb.createFont();
	
			//set font to 12 point type
			f2.setFontHeightInPoints((short) 12);
			f2.setFontName("Calibri");
			f2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 	f2.setColor(Font.COLOR_RED);
		 	cs5.setFont(f2);
			cs5.setAlignment(CellStyle.ALIGN_LEFT);
			cs5.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			
			// create cell style
		 	HSSFCellStyle cs6 = wb.createCellStyle();
		 	cs6.setFont(f);
			cs6.setAlignment(CellStyle.ALIGN_LEFT);
			cs6.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);

			// create a row
			// row 1
			s.addMergedRegion(new CellRangeAddress(0, 0, 0, cellcount-1));
		    r = s.createRow(rowNum++);
		    c = r.createCell(0);
		    c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.title"));
		    c.setCellStyle(cs);
		    
		    // row 2
		    String sysInfo = "";
		    if (getIsInHomeDomain()) {
		    	sysInfo = "System ID: "+getSystemId();
			// HiveManager Online user
			} else if (NmsUtil.isHostedHMApplication()) {
				sysInfo = "VHM ID: "+getDomain().getVhmID();
			}
		    s.addMergedRegion(new CellRangeAddress(1, 1, 0, cellcount-1));
		    r = s.createRow(rowNum++);
		    c = r.createCell(0);
		    c.setCellValue(sysInfo);
		    c.setCellStyle(cs);
		    
		    // row 3
		    s.addMergedRegion(new CellRangeAddress(2, 2, 0, cellcount-1));
		    r = s.createRow(rowNum++);
		    c = r.createCell(0);
		    List<?> userInfo = QueryUtil.executeQuery("SELECT email, company FROM "+UserRegInfoForLs.class.getSimpleName(), null, new FilterParams("owner.domainName", getDomain().getDomainName()));
		    if (!userInfo.isEmpty()) {
		    	Object[] userInfos = (Object[])userInfo.get(0);
		    	c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.email.company", new String[]{(String)userInfos[0], (String)userInfos[1]}));
		    }
		    c.setCellStyle(cs);
		    
		    // row 4
		    r = s.createRow(rowNum++);
		    
		    // row 5
		    r = s.createRow(rowNum++);
		    
		    // row 6 cell 1
		    c = r.createCell(0);
		    c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.device.licensed"));
			c.setCellStyle(cs1);
			
			// row 6 cell 2
			c = r.createCell(1);
		    c.setCellValue(licenseInfo.getHiveAps());
		    c.setCellStyle(cs2);
		    
		    // device management info
		    Map<String, Integer> apcount = HiveAPInfoFromeDatabase.getManagedDeviceTypeAndNumber(getDomain().getDomainName());
		    int vpnCount = 0;
		    int totalCount = 0;
		    if (null != apcount) {
				Object[] typeNames = apcount.keySet().toArray();
				Arrays.sort(typeNames);
				for (Object typeName : typeNames) {
					
					// VPN Gateway VA does not belong to device
					if (MgrUtil.getEnumString("enum.hiveAp.model.10").equals(typeName)) {
						vpnCount = apcount.get(typeName);
					} else {
						r = s.createRow(rowNum++);
						c = r.createCell(0);
					    c.setCellValue((String)typeName);
					    c.setCellStyle(cs3);
					    
					    c = r.createCell(1);
					    c.setCellValue(apcount.get(typeName));
					    c.setCellStyle(cs);
					    totalCount += apcount.get(typeName);
					}
				}
				
				   
			}
		    
		    // managed device total number cell1
			r = s.createRow(rowNum++);
			c = r.createCell(0);
		    c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.device.total"));
		    c.setCellStyle(cs1);
		    
		    // managed device total number cell2
		    c = r.createCell(1);
		    c.setCellValue(totalCount);
		    c.setCellStyle(cs2);
			
			// blank row
		    r = s.createRow(rowNum++);
		    
		    // licensed VPN Gateway VA cell1
		    r = s.createRow(rowNum++);
		    c = r.createCell(0);
		    c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.cvg.licensed"));
			c.setCellStyle(cs1);
			
			// licensed VPN Gateway VA cell2
			c = r.createCell(1);
		    c.setCellValue(licenseInfo.getCvgNumber());
		    c.setCellStyle(cs2);
			
		    // managed VPN Gateway VA cell1
		    r = s.createRow(rowNum++);
		    c = r.createCell(0);
		    c.setCellValue(MgrUtil.getUserMessage("admin.license.orderkey.export.cvg.total"));
			c.setCellStyle(cs1);
			
			// managed VPN Gateway VA cell 2
			c = r.createCell(1);
		    c.setCellValue(vpnCount);
		    c.setCellStyle(cs2);
			
			// entitlement key information
			preparePage();
			if (null != page && !page.isEmpty()) {
				r = s.createRow(rowNum++);
				List<OrderHistoryInfo> normalKey = new ArrayList<>();
				List<OrderHistoryInfo> invalidKey = new ArrayList<>();
				List<OrderHistoryInfo> expiredKey = new ArrayList<>();
				
				for (Object obj : page) {
					OrderHistoryInfo orderInfo = (OrderHistoryInfo)obj;
					if (orderInfo.getStatusFlag() == OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL && 
							orderInfo.getCvgStatusFlag() == OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL) {
						normalKey.add(orderInfo);
					} else if (orderInfo.getStatusFlag() == OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE || 
							orderInfo.getCvgStatusFlag() == OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE) {
						invalidKey.add(orderInfo);
					} else {
						expiredKey.add(orderInfo);
					}
				}
				
				if (!normalKey.isEmpty()) {
					
					// normal entitle key title
					r = s.createRow(rowNum++);
					setEntitlementKeyCellValue(cellcount, cs4, null, r, null, MgrUtil.getUserMessage("order.key"));
					
					// normal entitle key info
					for (OrderHistoryInfo keyInfo : normalKey) {
						r = s.createRow(rowNum++);
						setEntitlementKeyCellValue(cellcount, cs6, cs3, r, keyInfo, null);
					}
				}
				
				
				
				if (!invalidKey.isEmpty()) {
					
					if (!normalKey.isEmpty()) {
						r = s.createRow(rowNum++);
						r = s.createRow(rowNum++);
					}
					
					// invalid entitle key title
					r = s.createRow(rowNum++);
					setEntitlementKeyCellValue(cellcount, cs5, null, r, null, MgrUtil.getUserMessage("admin.license.orderkey.export.invalidkey.title"));
					
					// invalid entitle key info
					for (OrderHistoryInfo keyInfo : invalidKey) {
						r = s.createRow(rowNum++);
						setEntitlementKeyCellValue(cellcount, cs6, cs3, r, keyInfo, null);
					}
				}
				
				if (!expiredKey.isEmpty()) {
					
					if (!normalKey.isEmpty() || !invalidKey.isEmpty()) {
						r = s.createRow(rowNum++);
						r = s.createRow(rowNum++);
					}
					
					// expired entitle key title
					r = s.createRow(rowNum++);
					setEntitlementKeyCellValue(cellcount, cs5, null, r, null, MgrUtil.getUserMessage("admin.license.orderkey.export.expiredkey.title"));
					
					// expired entitle key info
					for (OrderHistoryInfo keyInfo : expiredKey) {
						r = s.createRow(rowNum++);
						setEntitlementKeyCellValue(cellcount, cs6, cs3, r, keyInfo, null);
					}
				}
			}

			// write the workbook to the output stream
			// close our file (don't blow out our file handles)
			wb.write(out);
			out.close();
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.export.entitlement.key"));
		} catch (Exception ex) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.export.entitlement.key"));
			log.error("exportEntitleKeyInfo()", ex.getMessage());
		}
	}
	
	private void setEntitlementKeyCellValue(int cellcount, HSSFCellStyle cs, HSSFCellStyle cs1, HSSFRow r, OrderHistoryInfo lsInfo, String keyTitle) {
		for (int i = 0; i < cellcount; i++) {
			HSSFCell c = r.createCell(i);
			c.setCellStyle(cs);
		    switch (i) {
		    	case 0:
		    		c.setCellValue(null == lsInfo ? keyTitle : lsInfo.getOrderKey());
		    		break;
		    	case 1:
		    		c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.type") : lsInfo.getLicenseTypeStr());
		    		break;
		    	case 2:
		    		c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.ap") : String.valueOf(lsInfo.getNumberOfAps()));
		    		if (null != lsInfo) {
		    			cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		    			c.setCellStyle(cs1);
		    		}
		    		break;
		    	case 3:
		    		if (NmsUtil.isHostedHMApplication()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.subscription.end") : lsInfo.getSubEndTimeStr());
		    		} else {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.cvg") : String.valueOf(lsInfo.getNumberOfCvgs()));
		    		}
		    		if (null != lsInfo) {
		    			if (!NmsUtil.isHostedHMApplication()) {
		    				cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		    			}
		    			if (!"N/A".equals(c.getStringCellValue())) {
		    				c.setCellStyle(cs1);
		    			}
		    		}
		    		break;
		    	case 4:
		    		if (NmsUtil.isHostedHMApplication()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.cvg") : String.valueOf(lsInfo.getNumberOfCvgs()));
		    		} else {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.vhm") : String.valueOf(lsInfo.getNumberOfVhms()));
		    		}
		    		if (null != lsInfo) {
		    			cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		    			c.setCellStyle(cs1);
		    		}
		    		break;
		    	case 5:
		    		if (NmsUtil.isHMForOEM()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.time") :
		    				(lsInfo.getIsPermanentLicense() ? "N/A" : String.valueOf(lsInfo.getNumberOfEvalValidDays())));
		    			if (null != lsInfo) {
		    				cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			    			c.setCellStyle(lsInfo.getIsPermanentLicense() ? cs : cs1);
			    		}
		    		} else if (NmsUtil.isHostedHMApplication()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.subscription.end") : lsInfo.getCvgSubEndTimeStr());
		    		} else {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.end") : lsInfo.getSupportEndTimeStr());
		    		}
		    		if (null != lsInfo && !"N/A".equals(c.getStringCellValue())) {
	    				c.setCellStyle(cs1);
	    			}
		    		break;
		    	case 6:
		    		if (NmsUtil.isHMForOEM()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.active") : lsInfo.getActiveTimeStr());
		    		} else if (NmsUtil.isHostedHMApplication()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.end") : lsInfo.getSupportEndTimeStr());
		    		} else {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.time") : 
		    				(lsInfo.getIsPermanentLicense() ? "N/A" : String.valueOf(lsInfo.getNumberOfEvalValidDays())));
		    			if (null != lsInfo) {
		    				cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			    			c.setCellStyle(lsInfo.getIsPermanentLicense() ? cs : cs1);
			    		}
		    		}
		    		if (null != lsInfo && !"N/A".equals(c.getStringCellValue())) {
	    				c.setCellStyle(cs1);
	    			}
		    		break;
		    	case 7:
		    		if (NmsUtil.isHostedHMApplication()) {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.time") : 
		    				(lsInfo.getIsPermanentLicense() ? "N/A" : String.valueOf(lsInfo.getNumberOfEvalValidDays())));
		    			if (null != lsInfo) {
		    				cs1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			    			c.setCellStyle(lsInfo.getIsPermanentLicense() ? cs : cs1);
			    		}
		    		} else {
		    			c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.active") : lsInfo.getActiveTimeStr());
		    		}
		    		if (null != lsInfo && !"N/A".equals(c.getStringCellValue())) {
	    				c.setCellStyle(cs1);
	    			}
		    		break;
		    	case 8:
		    		c.setCellValue(null == lsInfo ? MgrUtil.getUserMessage("admin.license.orderKey.support.active") : lsInfo.getActiveTimeStr());
		    		if (null != lsInfo) {
		    			c.setCellStyle(cs1);
		    		}
		    		break;
		    	default:
		    		break;
		    }
		}
	}
	
	private int getColumnWidthByIndex(int index) {
		int width = 20;
		switch (index) {
			// order key
			case 0:
				width = 45;
				break;
			// key type
			case 1:
				width = 16;
				break;
			// device number
			case 2:
				width = 16;
				break;
			// cvg number (OP-HM), device subscription (HMOL)
			case 3:
				width = NmsUtil.isHostedHMApplication() ? 24 : 20;
				break;
			// vhm number (OP-HM), cvg number (HMOL)
			case 4:
				width = NmsUtil.isHostedHMApplication() ? 20 : 16;
				break;
			// support date (OP-HM), cvg subscription (HMOL)
			case 5:
				width = NmsUtil.isHostedHMApplication() ? 24 : 21;
				break;
			// evaluation period (OP-HM), support date (HMOL)
			case 6:
				width = NmsUtil.isHMForOEM() ? 25 : 21;
				break;
			// active date (OP-HM), evaluation period (HMOL)
			case 7:
				width = NmsUtil.isHostedHMApplication() ? 21 : 25;
				break;
			// active date (HMOL)
			case 8:
				width = 25;
				break;
		}
		return width;
	}
	
	public String getExportFileName() {
		return ORDERKEYINFO_EXPORT_FILE_NAME;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(ORDERKEYINFO_EXPORT_FILE_PATH);
	}
	
	private String deviceInventoryDetail;

	public String getDeviceInventoryDetail() {
		return deviceInventoryDetail;
	}

	public void setDeviceInventoryDetail(String deviceInventoryDetail) {
		this.deviceInventoryDetail = deviceInventoryDetail;
	}
	
}