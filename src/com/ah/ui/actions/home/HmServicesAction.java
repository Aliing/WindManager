package com.ah.ui.actions.home;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.SendFailedException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeLogServerInfo;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeFaultUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.fault.BeFaultConst;
import com.ah.be.ga.GAConfigHepler;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseOperationTool;
import com.ah.be.performance.BePresenceProcessor;
import com.ah.be.rest.client.OpenDNSService;
import com.ah.be.rest.client.exception.OpenDNSException;
import com.ah.be.rest.client.models.opendns.OpenDNSModel;
import com.ah.bo.HmBo;
import com.ah.bo.admin.AirtightSettings;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.MailNotification4VHM;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.admin.RemoteProcessCallSettings;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.integration.airtight.SgeIntegrator;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.CasTool;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgPool;
import com.ah.util.values.PairValue;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.client.utils.GuestAnalyticsResUtils;
import com.ah.ws.rest.client.utils.PortalResUtils;
import com.ah.ws.rest.models.ga.CheckGAServiceResponse;
import com.ah.ws.rest.models.ga.GuestAnalyticsRequestResponse;

public class HmServicesAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log							= new Tracer(HmServicesAction.class
																	.getSimpleName());

	// set visibility of radius config section
	// visible:"" invisible:"none"
	private String				hideRadius					= "none";

	private String				hideSNMP					= "none";

	private int					tftpState;

	private boolean				tftpStateUpdate;

	private boolean				adminSessionUpdate;

	private boolean				disableTftpState;

	private boolean 			disableSnpState;
	private int					snpState;
	private boolean				snpStateUpdate;

	private boolean             presenceUpdate;
	private boolean             presenceEnable;
	
	private boolean             updateL7Setting;
	private boolean             enableSystemL7Switch;
	
	private boolean				updateAuth;

	private short				adminAuth;

	private int					authType;

	private Long				radiusServiceId;

	private boolean				updateSNMP;

	private String				snmpCommunity;

	// set visibility of log server update section
	// visible:"" invisible:"none"
	private String				hideLogServer				= "none";

	private boolean				disabledLogServer;

	private boolean				updateLogServer;

	// log status definition
	private final String		LOGSTATUS_DISABLE			= "disableLog";

	private final String		LOGSTATUS_ENABLE			= "enableLog";

	// enum value: "disableLog","enableLog"
	private String				logStatus					= LOGSTATUS_DISABLE;

	// entry status definition
	private final String		ENTRYSTATUS_ANY				= "anyEntry";

	private final String		ENTRYSTATUS_SPECIAL			= "specialEntry";

	// enum value: "anyEntry","specialEntry"
	private String				entryStatus					= ENTRYSTATUS_ANY;

	private List<HmSubnet>		subnetList					= new ArrayList<HmSubnet>();

	private static long			nextId						= 1;

	private String				disabledAnyEntry;

	private String				subnetMask;

	private String				subnetIP;

	private boolean				updateCAPWAP;

	private int					capwapUdpPort;

	private short				capwapTimeOut;

	private short				deadInterval;

	private String				newPassPhrase;

	private String				confirmPassPhrase;

	private boolean				defaultPassPhrase;
	
	private boolean              disablePassPhrase;

	private short				trapFilterInterval;

	private String				primaryCapwapIP;

	private String				backupCapwapIP;

	private boolean				updateEmail;

	private String				smtpServer;

	private String				fromEmail;

	private String				toEmail1;

	private String				toEmail2;

	private String				toEmail3;

	private String				toEmail4;

	private String				toEmail5;

	private boolean				enableNotify				= true;

	// enabled_stateChange return a array such like {"0","3"},corresponding
	// is eventTypeArray[0] & eventTypeArray[3] is enabled in stateChange
	// column,otherwise in other event type

	private String[]			enabled_event;

	private String[]			enabledCritical;

	private String[]			enabledMajor;

	private String[]			enabledMinor;

	private String[]			enabledInfo;

	private String[]			enabledClear;

	private int					smtpPort;

	private boolean				supportAuth;

	private boolean				smtpEncryption;

	public static final String	SMTP_ENCRY_SSL				= "ssl";

	public static final String	SMTP_ENCRY_TLS				= "tls";

	private String				smtpEncryProtocol			= SMTP_ENCRY_SSL;

	private String				emailUserName;

	private String				emailPassword;

	private boolean				updateNotifyInfo;

	private boolean				showNotifyInfo;

	private String				hmNotifyInfo;

	private String				hmNotifyTitle;

	private boolean				enableRollback;

	private boolean				updateTeacher;

	private boolean				enableTeacher;

	private String				casServer;

	private boolean 			enableTVProxy;

	private String 				tvProxyIP;

	private int 				tvProxyPort;

	private String 				tvAutoProxyFile;
	
	private boolean			    enableCaseSensitive;

	private boolean				updateAirtight;

	private boolean				enableAirtight;

	private String 				airTightURL;
	private String 				airTightUserName;
	private String 				airTightInterval = String.valueOf(AirtightSettings.DEFAULT_SYNC_INTERVAL);
	private String 				airTightPassword;

	private boolean				updateRPC;
	private boolean				enableRPCServer;
	private String				rpcUserName;
	private String				rpcPasswd;
	private String				rpcRePasswd;
	private String				rpcInterval = String.valueOf(RemoteProcessCallSettings.DEFAULT_OVERTIME);

	//barracuda
	private boolean updateBarracudaServer;
	private String	hideBarracudaServer = "none";
	private String authorizationKey;
	private String serviceHost;
	private int servicePort;
	private String windowsDomain;
	private String barracudaDefaultUserName;
	private boolean chkAuthorizationKey;
	private String authorizationKeyText;
	private boolean enableBarracuda;

	//websense
	private boolean updateWebSenseServer;
	private String	hideWebSenseServer = "none";
	private String accountID;
	private String webSenseServiceHost;
	private String securityKey;
	private short wensenseMode;
	private String defaultDomain;
	private String webSenseDefaultUserName;
	private boolean chkSecurityKey;
	private String securityKeyText;
	private boolean enableWebsense;

	private List<CheckItem> websenseWhitelists;
	private Long websenseWhitelist;
	private List<CheckItem> barracudaWhitelists;
	private Long barracudaWhitelist;
	private Long whiteListId;
	

	
	//******************* For Client Management System Settings  ********************************//
	private boolean onboardUpdate;
	private boolean enabledClientProfile;
	private boolean enabledCidPolicyEnforcement;
	/*private boolean enableCustomizeCA;
	private File[]    uploads;
	private String[]  uploadFileNames;*/

	public boolean isUpdateL7Setting() {
		return updateL7Setting;
	}

	public void setUpdateL7Setting(boolean updateL7Setting) {
		this.updateL7Setting = updateL7Setting;
	}

	public boolean isEnableSystemL7Switch() {
		return enableSystemL7Switch;
	}

	public void setEnableSystemL7Switch(boolean enableSystemL7Switch) {
		this.enableSystemL7Switch = enableSystemL7Switch;
	}

	public boolean isEnabledClientProfile(){
		return enabledClientProfile;
	}
	
	public void setEnabledClientProfile(boolean enabledClientProfile){
		this.enabledClientProfile = enabledClientProfile;
	}
	
	public void setOnboardUpdate(boolean onboardUpdate){
		this.onboardUpdate = onboardUpdate;
	}
	
	public boolean isOnboardUpdate(){
		return this.onboardUpdate;
	}
	
	public boolean isEnabledCidPolicyEnforcement(){
		return enabledCidPolicyEnforcement;
	}
	
	public void setEnabledCidPolicyEnforcement(boolean enabledCidPolicyEnforcement){
		this.enabledCidPolicyEnforcement = enabledCidPolicyEnforcement;
	}
	
	public String getHide4EasyMode(){
		return isEasyMode()? "none" : "";
	}
	
	
	
/*	public boolean isEnableCustomizeCA(){
		return this.enableCustomizeCA;
	}
	
	public void setEnableCustomizeCA(boolean enableCustomizeCA){
		this.enableCustomizeCA = enableCustomizeCA;
	}
	
	public File[] getUpload(){
		return this.uploads;
	}
	
	public void setUpload(File[] upload){
		this.uploads = upload;
	}
	
	public void setUploadFileName(String[] uploadFileName){
		this.uploadFileNames = uploadFileName;
	}
	
	public String[] getUploadFileName(){
		return this.uploadFileNames;
	}*/

	public final static String WEBSENSEQUICKSTART = "QS-Websense-Whitelist";
	public final static String BARRACUDAQUICKSTART = "QS-Barracuda-Whitelist";

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("update".equals(operation)) {

				if (!(updateOpenDNSServer || updateWebSenseServer || updateBarracudaServer || updateRPC || updateAirtight || updateTeacher
						|| updateNotifyInfo || updateEmail || updateCAPWAP || updateLogServer || updateGuestAnalytics || updateIDM
						|| updateSNMP || updateAuth || snpStateUpdate || tftpStateUpdate || presenceUpdate || onboardUpdate || updateL7Setting)) {
					addActionError(HmBeResUtil.getString("hmservices.select.msg"));
				}

				// tftp service
				if (tftpStateUpdate) {
					boolean isSuccess = updateTFTPService();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("mgmtSettings.tftp.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.tftp.service.state"));
					} else {
						addActionError(HmBeResUtil.getString("mgmtSettings.tftp.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.tftp.service.state"));
					}
				}

				if (snpStateUpdate) {
					boolean isSuccess = updateSnpService();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("mgmtSettings.snp.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.spectrum.analysis.max"));
					} else {
						addActionError(HmBeResUtil.getString("mgmtSettings.snp.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.spectrum.analysis.max"));
					}
				}

				if (updateAuth) {
					boolean isSuccess = updateLoginAuth();
					if (isSuccess) {
						addActionMessage(HmBeResUtil
								.getString("mgmtSettings.loginAuth.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.login.auth"));
					} else {
						addActionError(HmBeResUtil.getString("mgmtSettings.loginAuth.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.update.login.auth"));
					}
				}

				// update SNMP
				if (updateSNMP) {
					boolean isSuccess = updateSNMPServerSettings();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("mgmtSettings.snmp.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.snmp.server.setting"));
					} else {
						addActionMessage(HmBeResUtil.getString("mgmtSettings.snmp.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.snmp.server.setting"));
					}
				}

				// update log server settings
				if (updateLogServer) {
					try {
                        boolean isSuccess = updateLogServer();
                        if(isSuccess){
                            addActionMessage(HmBeResUtil.getString("logConfig.server.update.success"));
                            generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("hm.audit.log.update.system.log.config"));
                        }else{
                            addActionError(HmBeResUtil.getString("logConfig.server.update.error"));
                            generateAuditLog(HmAuditLog.STATUS_FAILURE,MgrUtil.getUserMessage("hm.audit.log.update.system.log.config"));
                        }
					} catch (BeOperateException e) {
						addActionError(HmBeResUtil.getString("logConfig.server.update.error") + " "
								+ e.getMessage());
						log.error("execute", "update system log configuration catch exception", e);
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.system.log.config"));
					} catch (Exception e) {
						addActionError(HmBeResUtil.getString("logConfig.server.update.error") + " "
								+ e.getMessage());
						log.error("execute", "update system log configuration catch exception", e);
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.system.log.config"));
					}
				}

				if (updateCAPWAP) {
					boolean isSuccess = updateCAPWAPConfig();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("capwapConfig.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.capwap.setting"));
						
						// for bug 20171
						if (!isHMOnline()) {
							try {
								int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
								BoMgmt.getHiveApMgmt().updateConfigurationIndication(
										true, pendingIndex, "CAPWAP Server Settings",
										ConfigurationType.Configuration);
							} catch (Exception e) {
								log.error(e);
							}
						}
					
					} else {
						addActionMessage(HmBeResUtil.getString("capwapConfig.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.update.capwap.setting"));
					}
				}

				if (updateEmail) {
					boolean isSuccess = updateEmailNotifyConfig();

					if (isSuccess) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.email.notify.setting"));
						addActionMessage(HmBeResUtil.getString("emailConfig.update.success"));
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.email.notify.setting"));
						addActionError(HmBeResUtil.getString("emailConfig.update.error"));
					}
				}

				if (updateNotifyInfo) {
					boolean isSuccess = updateNotifyInfoSettings();

					if (isSuccess) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.update.notify.info.setting"));
						addActionMessage(HmBeResUtil.getString("notifyInfo.update.success"));
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.notify.info.setting"));
						addActionError(HmBeResUtil.getString("notifyInfo.update.error"));
					}
				}

				if (updateTeacher) {
					boolean isSuccess = updateTeacherViewSettings();

					if (isSuccess) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.update.teacher.view.setting"));
						addActionMessage(HmBeResUtil.getString("teacherView.update.success"));
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.teacher.view.setting"));
						addActionError(HmBeResUtil.getString("teacherView.update.error"));
					}
				}

				if (updateAirtight) {
					updateAirTight();
				}

				if (updateRPC) {
					boolean isSuccess = updateRPC();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("rpcConfig.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.rpc.setting"));
					} else {
						addActionError(HmBeResUtil.getString("rpcConfig.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.update.rpc.setting"));
					}
				}

				//barracuda
				if (updateBarracudaServer) {
					boolean isSuccess = updateBarracudaServerSetting();
					if (isSuccess) {
						addActionMessage(HmBeResUtil
								.getString("barracudaServer.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.update.barracudaserver.setting"));
					} else {
						addActionError(HmBeResUtil
								.getString("barracudaServer.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.barracudaserver.setting"));
					}
				}

				//websense
				if (updateWebSenseServer) {
					boolean isSuccess = updateWebSenseServerSetting();
					if (isSuccess) {
						addActionMessage(HmBeResUtil
								.getString("webSense.update.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.update.websense.setting"));
					} else {
						addActionError(HmBeResUtil
								.getString("webSense.update.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.websense.setting"));
					}
				}
				
				//OpenDNS
				if(updateOpenDNSServer){
					boolean isSuccess = false;
					try{
						isSuccess = updateOpenDNSServerSettings();
						if (isSuccess) {
							addActionMessage(HmBeResUtil.getString("openDNS.update.success"));
							generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("hm.audit.log.update.openDNS.setting"));
						} else {
							addActionError(HmBeResUtil.getString("openDNS.update.error"));
							generateAuditLog(HmAuditLog.STATUS_FAILURE,MgrUtil.getUserMessage("hm.audit.log.update.openDNS.setting"));
						}
					}catch(Exception ex){
						if(ex instanceof OpenDNSException){
							addActionError(((OpenDNSException)ex).getMessage());				
						}else{
							addActionError(HmBeResUtil.getString("openDNS.update.error"));
						}
						
						generateAuditLog(HmAuditLog.STATUS_FAILURE,MgrUtil.getUserMessage("hm.audit.log.update.openDNS.setting"));
					}
				}
				
				// presence
				if (presenceUpdate) {
                   boolean isSuccess=updatePresencesetting();
                   if (isSuccess) {
						addActionMessage(HmBeResUtil
								.getString("presence.update.success"));
						generateAuditLog(
								HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.update.presence.setting"));
					} else {
						addActionError(HmBeResUtil
								.getString("presence.update.error"));
						generateAuditLog(
								HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.presence.setting"));
					}
				}
				
				if(onboardUpdate){
					try{
						HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
			                    "owner", getDomain());
					    if(bo != null && bo.isEnableClientManagement()){
					    	this.enabledClientProfile = bo.isEnableClientManagement();
					    }
					}catch(Exception e){
						log.error("error happens when query HMServicesSettings from database");
					}
					String isSuccess = updateOnboardSystemSetting();
		            if ("".equals(isSuccess)) {
						addActionMessage(MgrUtil.getUserMessage("home.clientManagement.enable.success"));
						
						// remove the client manager warning message
						if (enabledClientProfile) {
							AhNotificationMsgPool msgPool = getSessionNotificationMessagePool();
					        if (null != msgPool) {
					        	TreeSet<AhNotificationMessage> msgs = msgPool.getCurrentMessages();
					        	if (null != msgs) {
					        		AhNotificationMessage removableMsg = null;
						        	for (AhNotificationMessage msg : msgs) {
						                if (msg.getPriority() == AhNotificationMessage.CLIENT_MANAGER_MSG_PRIORITY) {
						                    removableMsg = msg;
						                    break;
						                }
						            }
							        if (null != removableMsg)
							        	msgPool.disableMsg(removableMsg, getUserContext());
					        	}
					        }
						}
					} else {
						addActionError(isSuccess);
					}
				}
				
				if (updateL7Setting) {
					boolean isSuccess = updateL7Setting();
					if (isSuccess) {
						addActionMessage(HmBeResUtil.getString("enableSystemL7Setting.update.success"));
						ReportCacheMgmt.getInstance().updateEnableSystemL7Switch(enableSystemL7Switch);
						setNotifyFlag(enableSystemL7Switch);
						//generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.spectrum.analysis.max"));
					} else {
						addActionError(HmBeResUtil.getString("enableSystemL7Setting.update.error"));
					}
				}
				
				if(updateGuestAnalytics) {
				    boolean isSuccess = toggleGuestAnalytics();
		            if (isSuccess) {
                        generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("home.services.guestanalytics.update"));
                    } else {
                        generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("home.services.guestanalytics.update"));
                    }
				}
				
				if(updateIDM) {
				    boolean isSuccess = updateIDMServices();
				    if (isSuccess) {
				        generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("home.services.idm.retrieve.update"));
				    } else {
				        generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("home.services.idm.retrieve.update"));
				    }
				}
				
				initValue();

				return SUCCESS;
			} else if("testOpenDNSLogin".equals(operation)){
				OpenDNSModel openDNSModel =  OpenDNSService.login(openDNSUserName, chkOpenDNSPassword ? openDNSPassword : openDNSPasswordText);
				jsonObject = new JSONObject();
				if(openDNSModel.isSuccessFlag()){
					jsonObject.put("isScuess", true);
					jsonObject.put("msg", MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.loginTest.success"));
				}else{
					jsonObject.put("isScuess", false);
					jsonObject.put("msg", MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.loginTest.failed", openDNSModel.getError_message()));
				}
				return "json";
			} else if("openDNSDeviceSettings".equals(operation)){	
				return "openDNSDevice";
			} else if("createOpenDNSDeviceId".equals(operation)){
				jsonObject = createOpenDNSDevice();
				return "json";
			} else if("checkActiveUserName".equals(operation)){
				checkActiveUserName();
				return "json";
			} else if("getDeviceId".equals(operation)){
				jsonObject = getDeviceId();
				return "json";
			} else if("refreshOpenDNSDevice".equals(operation)){
				refreshOpenDNSDevice();
				return "json";
			} else if("troubleshooting".equals(operation)){
				jsonObject = new JSONObject();
                try{
                    String customerId = LicenseOperationTool.getCustomerIdFromRemote(getDomain().getInstanceId());
	                PairValue<Boolean, PairValue<String, String>> rtnvalue = CertificateGenSV.troubleshooting(customerId, getDomain());
	                String message = rtnvalue.getDesc().getValue();
	                String warningMessage = rtnvalue.getDesc().getDesc();
	                if(rtnvalue.getValue()){
	                    jsonObject.put("succ", true);
	                    if(StringUtils.isBlank(message)) {
	                        jsonObject.put("msg", MgrUtil.getUserMessage("home.hmSettings.clientManagement.troubleshooting.success"));
	                    }
	                }else{
	                    jsonObject.put("succ", false);
                        if(Integer.parseInt(message) > 0){
	                        message = MgrUtil.getEnumString(CertificateGenSV.TROUBLESHOOTING_EXCEPTION + message);
	                    }
	                    jsonObject.put("msg", MgrUtil.getUserMessage("home.hmSettings.clientManagement.troubleshooting.failure.reason",message));
	                }
	                if(StringUtils.isNotBlank(warningMessage)) {
	                    jsonObject.put("warn", warningMessage);
	                }
	            }catch(Exception e){
	                jsonObject.put("msg", MgrUtil.getUserMessage("home.clientManagement.enable.error.prefix") + e.getMessage());
	                log.error("troubleshooting", e);
	            }
				return "json";
			}else if ("reset".equals(operation)) {
				cancelOperation();
				return SUCCESS;
			} else if ("newRadius".equals(operation) || "editRadius".equals(operation)) {
				clearErrorsAndMessages();
				setRemoveAllLstTitle(true);
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				MgrUtil.setSessionAttribute("hiveManagerLoginRadius", adminAuth);
				MgrUtil.setSessionAttribute("hiveManagerLoginRadiusType", authType);
				MgrUtil.setSessionAttribute("hiveManagerLoginRadiusServer", radiusServiceId);
				return operation;
			} else if ("continueRadius".equals(operation)) {
				initRadiusServerContinueValue();
				removeLstForward();
				removeLstTitle();

				return SUCCESS;
			} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)) {
				MgrUtil.setSessionAttribute("snmpCommunity", snmpCommunity);
				MgrUtil.setSessionAttribute("snmpIPAddress", ipAddressId);
				setRemoveAllLstTitle(true);
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				clearErrorsAndMessages();
				return operation;
			} else if ("continueSNMP".equals(operation)) {
				initValue();
				updateSNMP = true;
				hideSNMP = "";
				snmpCommunity = (String) MgrUtil.getSessionAttribute("snmpCommunity");
				if (ipAddressId == null) {
					ipAddressId = (Long) MgrUtil.getSessionAttribute("snmpIPAddress");
				}

				MgrUtil.removeSessionAttribute("snmpCommunity");
				MgrUtil.removeSessionAttribute("snmpIPAddress");
				removeLstForward();
				removeLstTitle();

				return SUCCESS;
			} else if ("newWebsenseWhitelists".equals(operation) || "editWebsenseWhitelists".equals(operation)) {
				MgrUtil.setSessionAttribute("whitelists","websense" );
				MgrUtil.setSessionAttribute("accountID", accountID);
				MgrUtil.setSessionAttribute("webSenseServiceHost", webSenseServiceHost);
				MgrUtil.setSessionAttribute("webSenseDefaultUserName", webSenseDefaultUserName);
				if(chkSecurityKey) {
					MgrUtil.setSessionAttribute("securityKey", securityKey);
				}else{
					MgrUtil.setSessionAttribute("securityKey", securityKeyText);
				}
				MgrUtil.setSessionAttribute("wensenseMode", wensenseMode);
				MgrUtil.setSessionAttribute("defaultDomain", defaultDomain);
				MgrUtil.setSessionAttribute("websenseWhitelist", websenseWhitelist);
				MgrUtil.setSessionAttribute("websenseWhiteListId", whiteListId);
				MgrUtil.setSessionAttribute("enableWebsense", enableWebsense);
				setRemoveAllLstTitle(true);
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				clearErrorsAndMessages();
				return operation;
			} else if ("newBarracudaWhitelists".equals(operation) || "editBarracudaWhitelists".equals(operation)) {
				MgrUtil.setSessionAttribute("whitelists","barracuda" );
				if(chkAuthorizationKey){
					MgrUtil.setSessionAttribute("authorizationKey", authorizationKey);
				}else{
					MgrUtil.setSessionAttribute("authorizationKey", authorizationKeyText);
				}
				MgrUtil.setSessionAttribute("serviceHost", serviceHost);
				MgrUtil.setSessionAttribute("servicePort", servicePort);
				MgrUtil.setSessionAttribute("windowsDomain", windowsDomain);
				MgrUtil.setSessionAttribute("barracudaDefaultUserName", barracudaDefaultUserName);
				MgrUtil.setSessionAttribute("barracudaWhitelist", barracudaWhitelist);
				MgrUtil.setSessionAttribute("barracudaWhiteListId", whiteListId);
				MgrUtil.setSessionAttribute("enableBarracuda", enableBarracuda);

				setRemoveAllLstTitle(true);
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				clearErrorsAndMessages();
				return operation;
			} else if ("continueWhitelists".equals(operation)) {
				initValue();
				if (MgrUtil.getSessionAttribute("whitelists").equals("websense")) {
					updateWebSenseServer = true;
					if (whiteListId == null) {
						whiteListId = (Long) MgrUtil.getSessionAttribute("websenseWhiteListId");
					}
					hideWebSenseServer = "";
					accountID = (String) MgrUtil.getSessionAttribute("accountID");
					webSenseDefaultUserName = (String) MgrUtil.getSessionAttribute("webSenseDefaultUserName");
					webSenseServiceHost = (String) MgrUtil.getSessionAttribute("webSenseServiceHost");
					securityKey = (String) MgrUtil.getSessionAttribute("securityKey");
					wensenseMode = (Short) MgrUtil.getSessionAttribute("wensenseMode");
					defaultDomain = (String) MgrUtil.getSessionAttribute("defaultDomain");
					websenseWhitelist = (Long) MgrUtil.getSessionAttribute("websenseWhitelist");
					enableWebsense = (Boolean) MgrUtil.getSessionAttribute("enableWebsense");
					MgrUtil.removeSessionAttribute("whitelists");
					MgrUtil.removeSessionAttribute("accountID");
					MgrUtil.removeSessionAttribute("webSenseDefaultUserName");
					MgrUtil.removeSessionAttribute("webSenseServiceHost");
					MgrUtil.removeSessionAttribute("securityKey");
					MgrUtil.removeSessionAttribute("wensenseMode");
					MgrUtil.removeSessionAttribute("defaultDomain");
					MgrUtil.removeSessionAttribute("websenseWhitelist");
					MgrUtil.removeSessionAttribute("websenseWhiteListId");
					MgrUtil.removeSessionAttribute("enableWebsense");
				} else if (MgrUtil.getSessionAttribute("whitelists").equals("barracuda")) {
					updateBarracudaServer = true;
					if (whiteListId == null) {
						whiteListId = (Long) MgrUtil.getSessionAttribute("barracudaWhiteListId");
					}
					hideBarracudaServer = "";
					authorizationKey = (String) MgrUtil.getSessionAttribute("authorizationKey");
					serviceHost = (String) MgrUtil.getSessionAttribute("serviceHost");
					servicePort = (Integer) MgrUtil.getSessionAttribute("servicePort");
					windowsDomain = (String) MgrUtil.getSessionAttribute("windowsDomain");
					barracudaDefaultUserName = (String) MgrUtil.getSessionAttribute("barracudaDefaultUserName");
					barracudaWhitelist = (Long) MgrUtil.getSessionAttribute("barracudaWhitelist");
					enableBarracuda = (Boolean) MgrUtil.getSessionAttribute("enableBarracuda");
					MgrUtil.removeSessionAttribute("whitelists");
					MgrUtil.removeSessionAttribute("authorizationKey");
					MgrUtil.removeSessionAttribute("serviceHost");
					MgrUtil.removeSessionAttribute("httpServicePort");
					MgrUtil.removeSessionAttribute("httpsServicePort");
					MgrUtil.removeSessionAttribute("windowsDomain");
					MgrUtil.removeSessionAttribute("barracudaDefaultUserName");
					MgrUtil.removeSessionAttribute("barracudaWhitelist");
					MgrUtil.removeSessionAttribute("barracudaWhiteListId");
					MgrUtil.removeSessionAttribute("enableBarracuda");
				}
				removeLstForward();
				removeLstTitle();
				return SUCCESS;
			} else if ("addSubnet".equals(operation)) {
				initValue2();

				HmSubnet subnet = new HmSubnet();
				subnet.setIp(subnetIP);
				subnet.setMask(subnetMask);

				addSubnet(subnet);

				hideLogServer = "";
				disabledAnyEntry = "";

				return SUCCESS;
			} else if ("removeSubnet".equals(operation)) {
				initValue2();

				removeSelectedSubnets();

				hideLogServer = "";
				disabledAnyEntry = "";

				return SUCCESS;
			} else if ("testEmail".equals(operation)) {
				SendMailUtil mailUtil = new SendMailUtil();
				mailUtil.setSmtpServer(smtpServer);
				mailUtil.setFromEmail(fromEmail);
				mailUtil.addMailToAddr(getCombinedToEmail());
				mailUtil.setSubject("testing");
				mailUtil.setText("This is the test message body.");

				mailUtil.setSupportSSL(smtpEncryption && smtpEncryProtocol.equals(SMTP_ENCRY_SSL));
				mailUtil.setSupportTLS(smtpEncryption && smtpEncryProtocol.equals(SMTP_ENCRY_TLS));
				mailUtil.setPort(smtpPort);
				mailUtil.setSupportPwdAuth(supportAuth);
				if (supportAuth) {
					mailUtil.setEmailUserName(emailUserName);
					mailUtil.setEmailPassword(emailPassword);
				}

				String rspMessage = "";
				String errorMessage = null;
				try {
					mailUtil.startSend();
					rspMessage = HmBeResUtil.getString("emailConfig.sendTestMail.success");
				} catch (AuthenticationFailedException e) {
					log.error("execute", "catch AuthenticationFailedException", e);
					errorMessage = "Authentication failed. " + e.getMessage();
				} catch (SendFailedException e) {
					log.error("execute", "catch SendFailedException", e);
					if (e.getMessage().contains("Invalid Addresses: ")) {
						errorMessage = e.getMessage();
						for (Address ip : e.getInvalidAddresses()) {
							errorMessage += ip + " ";
						}
					} else {
						errorMessage = e.getMessage();
					}
				} catch (Exception e) {
					log.error("execute", "send test mail error", e);
					errorMessage = e.getMessage();
				}

				if (errorMessage != null) {
					rspMessage = HmBeResUtil.getString("emailConfig.sendTestMail.error")
							+ "<br>Error message: " + errorMessage;
				}

				jsonObject = new JSONObject();
				jsonObject.put("result", errorMessage == null);
				jsonObject.put("rspMessage", rspMessage);

				return "json";
			} else if ("checkGA".equals(operation)) {
			    return checkGAService();
			} else {
				initValue();

				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private boolean updateBarracudaServerSetting() {
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(
				HMServicesSettings.class, null, new FilterParams("owner.id",
						getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		try {
			if (enableBarracuda) {
				if(chkAuthorizationKey) {
					bo.setAuthorizationKey(authorizationKey);
				} else {
					bo.setAuthorizationKey(authorizationKeyText);
				}

				bo.setServiceHost(serviceHost);
				bo.setServicePort(servicePort);
				bo.setWindowsDomain(windowsDomain);
				bo.setBarracudaDefaultUserName(barracudaDefaultUserName);

				DomainObject barracudaDomainObject = QueryUtil.findBoById(DomainObject.class,barracudaWhitelist);
				bo.setBarracudaWhitelist(barracudaDomainObject);
			}
			bo.setEnableBarracuda(enableBarracuda);

			if (isUpdate) {
				Date oldVer = bo.getVersion();
				bo = QueryUtil.updateBo(bo);
				if (enableBarracuda) {
					bo.setEnableWebsense(false);
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(bo, ConfigurationChangedEvent.Operation.UPDATE, oldVer));
				}
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateBarracudaServerSetting",
					"barracuda server settings catch exception!", e);
			return false;
		}
	}

	private boolean updateWebSenseServerSetting() {
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(
				HMServicesSettings.class, null, new FilterParams("owner.id",
						getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		try {
			if (enableWebsense) {
				bo.setAccountID(accountID);
				bo.setWebSenseDefaultUserName(webSenseDefaultUserName);
				if(chkSecurityKey) {
					bo.setSecurityKey(securityKey);
				} else {
					bo.setSecurityKey(securityKeyText);
				}
				bo.setWensenseMode(wensenseMode);
				bo.setPort(8081);
				bo.setDefaultDomain(defaultDomain);
				if (wensenseMode == 0) {
					bo.setWebSenseServiceHost(getText("admin.management.webSecurity.websense.serviceHost.hosted"));
				} else {
					bo.setWebSenseServiceHost(getText("admin.management.webSecurity.websense.serviceHost.hybrid"));
				}

				//Service host field will be made editable temporarily,This should be changed back to read-only before FCS.
				bo.setWebSenseServiceHost(webSenseServiceHost);

				DomainObject websenseDomainObject = QueryUtil.findBoById(DomainObject.class,websenseWhitelist);
				bo.setWebsenseWhitelist(websenseDomainObject);
			}
			bo.setEnableWebsense(enableWebsense);
			if (isUpdate) {
				Date oldVer = bo.getVersion();
				bo = QueryUtil.updateBo(bo);
				if (enableWebsense) {
					bo.setEnableBarracuda(false);
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(bo, ConfigurationChangedEvent.Operation.UPDATE, oldVer));
				}
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateWebSenseServerSetting",
					"websense server settings catch exception!", e);
			return false;
		}
	}
	

	
	private boolean updatePresencesetting() {
		try{
			boolean isUpdate=true;
			HMServicesSettings bo;
			List<HMServicesSettings>list=QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams("owner.domainName",
					HmDomain.HOME_DOMAIN));
			if(list.isEmpty()){
				bo = new HMServicesSettings();
				isUpdate=false;
			}else{
				bo = list.get(0);
			}
			bo.setPresenceEnable(presenceEnable);
			if(isUpdate){
				 QueryUtil.updateBo(bo);
			}else{
				QueryUtil.createBo(bo);
			}
			refreshNavigationTree();
			// update presence flag in memory
			PresenceUtil.setPresenceStatus(presenceEnable);
			//check Euclid server connection status immediately
			BePresenceProcessor processor=new BePresenceProcessor();
			processor.startScheduledThread();
		}catch(Exception e){
			log.error("updatePresencesetting",
					"update presence settings catch exception!", e);
			return false;
		}
		return true;
	}
	
	private String updateOnboardSystemSetting() throws Exception{
		HMServicesSettings bo = null;
		String err = "";
		String customerId = "";
		try{
			bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                    "owner", getDomain());
		    if(bo == null){
			    bo = new HMServicesSettings();
                bo.setOwner(getDomain());
                QueryUtil.createBo(bo);
		    }
		    try{
		    	customerId = LicenseOperationTool.getCustomerIdFromRemote(getDomain().getInstanceId());
		    }catch(Exception e){
		    	e.printStackTrace();
		    	return MgrUtil.getUserMessage("home.clientManagement.enable.error.prefix") + e.getMessage();
		    }
		    try{
		    	ClientUtils.getPortalResUtils().createACMProductByCustomerId(customerId, getDomain().isHomeDomain() ? BeLicenseModule.HIVEMANAGER_SYSTEM_ID : getDomain().getVhmID());
		    }catch(Exception e){
		    	e.printStackTrace();
		    	return MgrUtil.getUserMessage("home.clientManagement.enable.error.prefix") + e.getMessage();
		    }
		    err = CertificateGenSV.certificateGenereate(enabledClientProfile,customerId,getDomain().getInstanceId(),
	                    getDomain().getDomainName(), "OnboardCA", "AuthServer", createData(),bo);
			if(err.equals("")){
				bo.setEnableClientManagement(enabledClientProfile);
		    	QueryUtil.updateBo(bo);
		    	CertificateGenSV.synCert4VHM(enabledClientProfile,bo);
			}
    		return err;
		}catch(IOException e){
			String msg = MgrUtil.getUserMessage("home.clientManagement.update.error.unreachable");
			log.error(msg);
			return msg;
		}catch(Exception e){
			log.error("updateOnboardSystemSetting()","Error when generate the certificate" + 
		               "Info: customerId " + customerId + "\r Error:" + err,e);
    		return MgrUtil.getUserMessage("home.clientManagement.enable.other",new String[]{getDomain().getInstanceId(),customerId});
		}
	}
	
	private void initL7Setting() {
    	//onboardUpdate = false;
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner.domainName", HmDomain.HOME_DOMAIN);
                if (null == bo) {
                	enableSystemL7Switch = new HMServicesSettings().isEnableSystemL7Switch();
                }else{
                	enableSystemL7Switch = bo.isEnableSystemL7Switch();
                }
            } catch (Exception e) {
                log.error("initL7Setting", "Error when init the L7 settings.", e);
            }
    }
	
    private void initOnboardSettings() {
    	//onboardUpdate = false;
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null == bo) {
                	bo = new HMServicesSettings();
                    bo.setOwner(getDomain());
                    enabledClientProfile = bo.isEnableClientManagement();
                    enabledCidPolicyEnforcement = bo.isEnableCidPolicyEnforcement();
//                    enableCustomizeCA = bo.isEnableCustomerCa();
                    QueryUtil.createBo(bo);
                }else{
                	enabledClientProfile = bo.isEnableClientManagement();
                    enabledCidPolicyEnforcement = bo.isEnableCidPolicyEnforcement();
//                    enableCustomizeCA = bo.isEnableCustomerCa();
                }
            } catch (Exception e) {
                log.error("initClientManagementService", "Error when init the client management settings.", e);
            }
    }
    
    private BeRootCADTO createData(){
    	// create CSR file
    	BeRootCADTO dto = new BeRootCADTO();
    	dto.setCommName(CertificateGenSV.COMMON_NAME);
    	dto.setCountryCode(CertificateGenSV.COUNTRY);
    	dto.setKeySize("1024");
    	dto.setLocalityName(CertificateGenSV.LOCALITY_NAME);
    	dto.setOrgName(CertificateGenSV.ORGANIZATION);
    	dto.setOrgUnit(CertificateGenSV.ORGANIZATION_UNIT);
    	dto.setStateName(CertificateGenSV.STATE);
    	dto.setFileName("ClientMgmt-Radius-Server");
    	dto.setPassword("");
    	dto.setDomainName(getDomain().getDomainName());
        return dto;
    }

	private boolean updateRPC() {
		List<RemoteProcessCallSettings> rpcSettingsList = QueryUtil.executeQuery(
				RemoteProcessCallSettings.class, null, null);
		RemoteProcessCallSettings rpcSettings;
		boolean isUpdate = false;
		if (rpcSettingsList.isEmpty()) {
			rpcSettings = new RemoteProcessCallSettings();
			rpcSettings.setOwner(getDomain());
		} else {
			isUpdate = true;
			rpcSettings = rpcSettingsList.get(0);
		}
		if(enableRPCServer){
			rpcSettings.setUserName(rpcUserName);
			rpcSettings.setPassword(rpcPasswd);
			rpcSettings.setTimeout(Integer.parseInt(rpcInterval));
		}
		rpcSettings.setEnabled(enableRPCServer);

		try {
			if(isUpdate){
				QueryUtil.updateBo(rpcSettings);
			}else{
				QueryUtil.createBo(rpcSettings);
			}
		} catch (Exception e) {
			log.error("updateRPC", "Update RPC settings catch exception!", e);
			resetRPCValues();
			return false;
		}
		return true;
	}

	private void resetRPCValues() {
		rpcUserName = null;
		rpcPasswd = null;
		rpcRePasswd = null;
		rpcInterval = String.valueOf(RemoteProcessCallSettings.DEFAULT_OVERTIME);
		enableRPCServer = false;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HM_SERVICES);
	}

	private void initRadiusServerContinueValue() throws Exception {
		initValue();

		updateAuth = true;
		hideRadius = "";
		adminAuth = Integer.valueOf(
				MgrUtil.getSessionAttribute("hiveManagerLoginRadius").toString()).shortValue();
		authType = Integer.valueOf(
				MgrUtil.getSessionAttribute("hiveManagerLoginRadiusType").toString()).shortValue();
		if (null == radiusServiceId) {
			radiusServiceId = Long.valueOf(MgrUtil.getSessionAttribute(
					"hiveManagerLoginRadiusServer").toString());
		}
	}

	/**
	 * init interval value
	 *
	 * @throws Exception
	 *             -
	 */
	private void initValue() throws Exception {
		initValue2();

		// log server settings
		initLogServerSettings();
	}

	/**
	 * exclude log server settings
	 */
	private void initValue2() throws Exception{
		// tftp service
		tftpStateUpdate = false;
		initTFTPState();
		
		snpStateUpdate = false;
		initSnpState();

		// authentication
		initLoginAuthSettings();

		initCapwapSetting();

		initEmailNotifySetting();

		//--Get HMServiceSettings bo
		HMServicesSettings settingsBo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
		 //presence
		presenceUpdate=false;
		if(null!=settingsBo){
			presenceEnable=settingsBo.isPresenceEnable();
		}
		// snmp
		initSNMPServerSettings(settingsBo);

		initNotifyInfoSetting(settingsBo);

		initTeacherSetting(settingsBo);

		// barracuda service setting
		initBarracudaServerSettings(settingsBo);

		//websense service setting
		initWebSenseServerSettings(settingsBo);
		
		//OpenDNS service setting
		initOpenDNServerSettings(settingsBo);

		initAirTightValues();

		initRPCValues();
		
		initIDMServices();
		
		initOnboardSettings();
		
		initL7Setting();
		
		initGuestAnalytics();
	}

    private void initRPCValues() {
		List<RemoteProcessCallSettings> list = QueryUtil.executeQuery(
				RemoteProcessCallSettings.class, null, null);

		if (list.isEmpty()) {
			return;
		}

		RemoteProcessCallSettings rpcSettings = list.get(0);

		rpcUserName = rpcSettings.getUserName();
		rpcPasswd = rpcSettings.getPassword();
		rpcRePasswd = rpcSettings.getPassword();
		rpcInterval = String.valueOf(rpcSettings.getTimeout());

		enableRPCServer = rpcSettings.isEnabled();
	}

	private void initTeacherSetting(HMServicesSettings bo)
	{
		updateTeacher = false;

		if (bo != null) {
			enableTeacher = bo.isEnableTeacher();
			String casServerConfig = CasTool.getCASServerIP();

			if(isHMOnline()) {
				this.casServer = casServerConfig;
			} else {
				/*
				 * the default setting of HM (not HMOL) is localhost
				 * it would be better to set it as local IP
				 */
				if("localhost".equals(casServerConfig)
						|| casServerConfig == null
						|| casServerConfig.trim().length() == 0) {
					this.casServer = HmBeOsUtil.getHiveManagerIPAddr();
				} else {
					this.casServer = casServerConfig;
				}
			}

			enableTVProxy = bo.isEnableTVProxy();
			tvProxyIP = bo.getTvProxyIP();
			tvProxyPort = bo.getTvProxyPort();
			tvAutoProxyFile = bo.getTvAutoProxyFile();
			enableCaseSensitive = bo.getEnableCaseSensitive() == 1 ? false : true;			
		}
	}

	private void initAirTightValues() {
		List<AirtightSettings> list = QueryUtil.executeQuery(AirtightSettings.class, null, null);

		if(list.isEmpty()) {
			return ;
		}

		AirtightSettings airtight = list.get(0);

		enableAirtight = airtight.isEnabled();
		airTightURL = airtight.getServerURL();
		airTightUserName = airtight.getUserName();
		airTightPassword = airtight.getPassword();

		if(!isDebugMode() &&
				airtight.getSyncInterval() < AirtightSettings.MIN_INTERVAL_RELEASE) {
			addActionMessage(MgrUtil.getUserMessage("hm.airtight.timeout.range.change"));
		}

		airTightInterval = String.valueOf(airtight.getSyncInterval());
	}

	private void initNotifyInfoSetting(HMServicesSettings bo) {
		updateNotifyInfo = false;

		if (bo == null) {
			showNotifyInfo = false;
			hmNotifyInfo = "";
			hmNotifyTitle = "";
			return;
		}

		showNotifyInfo = bo.isShowNotifyInfo();
		hmNotifyInfo = bo.getNotifyInformation();
		hmNotifyTitle = bo.getNotifyInformationTitle();
	}

	private void initEmailNotifySetting() {
		updateEmail = false;

		MailNotification mailNotification;

		List<MailNotification> configData = QueryUtil.executeQuery(MailNotification.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (configData.isEmpty()) {
			mailNotification = new MailNotification();
			mailNotification.setSendMailFlag(false);
			mailNotification.setServerName("");
			mailNotification.setMailFrom("");
			mailNotification.setMailTo("");
			mailNotification.setHdRadio((byte) 16);
			mailNotification.setCapWap((byte) 17);
			mailNotification.setSecurity((byte) 8);
			mailNotification.setConfig((byte) 8);
			mailNotification.setTimeBomb((byte) 28);
			mailNotification.setAd((byte)9);
			mailNotification.setSystem((byte)9);
			mailNotification.setSupportPwdAuth(false);
			mailNotification.setSupportSSL(false);
			mailNotification.setSupportTLS(false);
			mailNotification.setPort(25);
			//mailNotification.setClient((byte) 5);//hidden connection alarming feature
		} else {
			mailNotification = configData.get(0);
		}

		enableNotify = mailNotification.getSendMailFlag();
		boolean supportSSL = mailNotification.isSupportSSL();
		boolean supportTLS = mailNotification.isSupportTLS();
		smtpEncryption = supportSSL || supportTLS;
		smtpEncryProtocol = supportTLS ? SMTP_ENCRY_TLS : SMTP_ENCRY_SSL;
		smtpPort = mailNotification.getPort();
		supportAuth = mailNotification.isSupportPwdAuth();
		emailUserName = mailNotification.getEmailUserName();
		emailPassword = mailNotification.getEmailPassword();

		smtpServer = mailNotification.getServerName();
		fromEmail = mailNotification.getMailFrom();
		String toEmail = mailNotification.getMailTo() == null ? "" : mailNotification.getMailTo();

		if (toEmail.length() > 0 && toEmail.charAt(0) == ';') {
			toEmail = toEmail.substring(1);
		}
		String[] mailArray = toEmail.split(";");
		toEmail1 = mailArray.length > 0 ? mailArray[0] : "";
		toEmail2 = mailArray.length > 1 ? mailArray[1] : "";
		toEmail3 = mailArray.length > 2 ? mailArray[2] : "";
		toEmail4 = mailArray.length > 3 ? mailArray[3] : "";
		toEmail5 = mailArray.length > 4 ? mailArray[4] : "";

		boolean hdCPU = mailNotification.isHdCpu();
		boolean hdMemory = mailNotification.isHdMemory();
		boolean auth = mailNotification.isAuth();
		boolean interfaceValue = mailNotification.isInterfaceValue();
		boolean l2Dos = mailNotification.isL2Dos();
		boolean screen = mailNotification.isScreen();
		boolean vpn = mailNotification.isVpn();
		boolean idp = mailNotification.isInNetIdp();
		boolean clientValue = mailNotification.isClientMonitor();
		boolean clientRegister = mailNotification.isClientRegister();

		List<String> eventEnableList = new ArrayList<String>();
		if (hdCPU) {
			eventEnableList.add(String.valueOf(EVENTINDEX_HDCPU));
		}
		if (hdMemory) {
			eventEnableList.add(String.valueOf(EVENTINDEX_HDMEMORY));
		}
		if (auth) {
			eventEnableList.add(String.valueOf(EVENTINDEX_AUTH));
		}
		if (interfaceValue) {
			eventEnableList.add(String.valueOf(EVENTINDEX_INTERFACE));
		}
		if (l2Dos) {
			eventEnableList.add(String.valueOf(EVENTINDEX_L2DOS));
		}
		if (screen) {
			eventEnableList.add(String.valueOf(EVENTINDEX_SCREEN));
		}
		if (vpn) {
			eventEnableList.add(String.valueOf(EVENTINDEX_VPN));
		}
		if (idp) {
			eventEnableList.add(String.valueOf(EVENTINDEX_IDP));
		}
		if (clientValue) {
			eventEnableList.add(String.valueOf(EVENTINDEX_CLIENTMONITOR));
		}
		if (clientRegister){
			eventEnableList.add(String.valueOf(EVENTINDEX_CLIENTREGISTER));
		}

		enabled_event = eventEnableList.size() > 0 ? eventEnableList
				.toArray(new String[eventEnableList.size()]) : null;

		byte hdRadioStr = mailNotification.getHdRadio();
		byte capwapStr = mailNotification.getCapWap();
		byte configStr = mailNotification.getConfig();
		byte timeBombStr = mailNotification.getTimeBomb();
		byte securityStr = mailNotification.getSecurity();
		byte adStr = mailNotification.getAd();
		byte tcaStr = mailNotification.getTca();
		byte systemStr = mailNotification.getSystem();
		//byte clientStr = mailNotification.getClient();//hidden connection alarming feature

		// init alert notification table
		byte[] alertTypeArray = new byte[] { hdRadioStr, capwapStr, configStr, timeBombStr, securityStr, adStr, tcaStr, systemStr};//, clientStr};//hidden connection alarming feature

		List<String> criticalList = new ArrayList<String>();
		List<String> majorList = new ArrayList<String>();
		List<String> minorList = new ArrayList<String>();
		List<String> infoList = new ArrayList<String>();
		List<String> clearList = new ArrayList<String>();
		for (int i = 0; i < alertTypeArray.length; i++) {
			String tmpStr = String.valueOf(i);

			if (alertTypeArray[i] % 2 == 1) {
				clearList.add(tmpStr);
			}

			if ((alertTypeArray[i] >> 1) % 2 == 1) {
				infoList.add(tmpStr);
			}

			if ((alertTypeArray[i] >> 2) % 2 == 1) {
				minorList.add(tmpStr);
			}

			if ((alertTypeArray[i] >> 3) % 2 == 1) {
				majorList.add(tmpStr);
			}

			if ((alertTypeArray[i] >> 4) % 2 == 1) {
				criticalList.add(tmpStr);
			}
		}

		enabledCritical = criticalList.size() > 0 ? criticalList.toArray(new String[criticalList
				.size()]) : null;

		enabledMajor = majorList.size() > 0 ? majorList.toArray(new String[majorList.size()])
				: null;
		enabledMinor = minorList.size() > 0 ? minorList.toArray(new String[minorList.size()])
				: null;
		enabledInfo = infoList.size() > 0 ? infoList.toArray(new String[infoList.size()]) : null;
		enabledClear = clearList.size() > 0 ? clearList.toArray(new String[clearList.size()])
				: null;
	}

	private void initCapwapSetting() {
		updateCAPWAP = false;

		List<CapwapSettings> capwapSettings = QueryUtil.executeQuery(CapwapSettings.class, null,
				null);
		if (capwapSettings.isEmpty()) {
			// set default value
			capwapUdpPort = 12222;
			capwapTimeOut = 30;
			deadInterval = 105;
			primaryCapwapIP = backupCapwapIP = newPassPhrase = confirmPassPhrase = "";
			defaultPassPhrase = true;
			trapFilterInterval = 3;
			enableRollback = true;
			disablePassPhrase = true;
			return;
		}

		CapwapSettings currentSetting = capwapSettings.get(0);
		primaryCapwapIP = currentSetting.getPrimaryCapwapIP();
		backupCapwapIP = currentSetting.getBackupCapwapIP();
		capwapUdpPort = currentSetting.getUdpPort();
		capwapTimeOut = currentSetting.getTimeOut();
		deadInterval = currentSetting.getNeighborDeadInterval();
		newPassPhrase = currentSetting.getBootStrap();
		confirmPassPhrase = newPassPhrase;
		defaultPassPhrase = (newPassPhrase == null || newPassPhrase.length() == 0);
		disablePassPhrase = defaultPassPhrase;
		trapFilterInterval = currentSetting.getTrapFilterInterval();
		enableRollback = currentSetting.isEnableRollback();
	}

	private void initLogServerSettings() {
		hideLogServer = "none";
		updateLogServer = false;
		BeLogServerInfo logServerInfo = HmBeAdminUtil.getLogServerInfo();
		boolean isLogServer = logServerInfo.getIsLogServer();
		boolean isFullEntry = logServerInfo.getIsFullNet();
		logStatus = isLogServer ? LOGSTATUS_ENABLE : LOGSTATUS_DISABLE;
		entryStatus = isFullEntry ? ENTRYSTATUS_ANY : ENTRYSTATUS_SPECIAL;
		disabledLogServer = !isLogServer;
		disabledAnyEntry = isFullEntry ? "disabled" : "";

		// init subnet list
		List<String> infoList = logServerInfo.getSubNet();
		if (infoList == null || infoList.size() == 0) {
			subnetList = new ArrayList<HmSubnet>();
	        MgrUtil.setSessionAttribute(getSelectedL2FeatureKey() + "_subnetlist", subnetList);
			return;
		}

		subnetList = new ArrayList<HmSubnet>();
		for (String info : infoList) {
			HmSubnet subnet = new HmSubnet(getNextId(), info.substring(0, info.indexOf("/")), info
					.substring(info.indexOf("/") + 1));
			subnetList.add(subnet);
		}

		MgrUtil.setSessionAttribute(getSelectedL2FeatureKey() + "_subnetlist", subnetList);
	}

	private void initTFTPState() {
		disableTftpState = true;

		tftpState = HmBeAdminUtil.isTftpEnable() ? TFTPSTATE_ON : TFTPSTATE_OFF;
	}

	private void initSnpState() {
		disableSnpState = true;
		List<?> list = QueryUtil.executeQuery("select snpMaximum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN), 1);
		if (list.isEmpty()) {
			snpState = 20;
		} else {
			snpState = Integer.parseInt(list.get(0).toString());
		}
	}

	private void initLoginAuthSettings() {
		updateAuth = false;
		hideRadius = "none";

		List<HmLoginAuthentication> list = QueryUtil.executeQuery(HmLoginAuthentication.class,
				null, null);
		if (list.isEmpty()) {
			log.debug("initValue", "Error find no login authentication settings in db!");
			radiusServiceId = null;
			adminAuth = EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;
			authType = Cwp.AUTH_METHOD_PAP;
		} else {
			HmLoginAuthentication loginAuthentication = list.get(0);
			adminAuth = loginAuthentication.getHmAdminAuth();
			authType = loginAuthentication.getAuthType();
			radiusServiceId = loginAuthentication.getRadiusAssignment() == null ? null
					: loginAuthentication.getRadiusAssignment().getId();
		}

		if (adminAuth == EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS
				|| adminAuth == EnumConstUtil.ADMIN_USER_AUTHENTICATION_BOTH) {
			hideRadius = "";
		}
	}

	private void initSNMPServerSettings(HMServicesSettings bo) {
		updateSNMP = false;
		hideSNMP = "none";

		if (bo == null) {
			log.debug("initSNMPServerSettings", "No settings bo in db!");
			snmpCommunity = "";
			ipAddressId = null;

			return;
		}

		snmpCommunity = bo.getSnmpCommunity();
		ipAddressId = bo.getSnmpReceiverIP() != null ? bo.getSnmpReceiverIP().getId() : null;
	}

	private void initBarracudaServerSettings(HMServicesSettings bo) {
		updateBarracudaServer = false;
		hideBarracudaServer = "none";
		if (bo == null) {
			log.debug("initBarracudaServerSettings", "No settings bo in db!");
			// set default value
			authorizationKey = "";
			serviceHost = "";
			servicePort = HMServicesSettings.SERVICEPROT_DEFAULT_VALUE;
			windowsDomain = "";
			barracudaDefaultUserName = "";
			DomainObject domainObject = QueryUtil.findBoByAttribute(DomainObject.class, "objName",
					BARRACUDAQUICKSTART);
			if (domainObject != null) {
				barracudaWhitelist = domainObject.getId();
			}
			enableBarracuda = false;
			return;
		}

		authorizationKey = bo.getAuthorizationKey();
		serviceHost = bo.getServiceHost();
		servicePort =bo.getServicePort();
		windowsDomain = bo.getWindowsDomain();
		barracudaDefaultUserName = bo.getBarracudaDefaultUserName();
		if (bo.getAuthorizationKey()==null || "".equals(bo.getAuthorizationKey())){
			DomainObject domainObject = QueryUtil.findBoByAttribute(DomainObject.class, "objName",
					BARRACUDAQUICKSTART);
			if (domainObject != null) {
				barracudaWhitelist = domainObject.getId();
			}
		} else if (bo.getBarracudaWhitelist() != null) {
			barracudaWhitelist = bo.getBarracudaWhitelist().getId();
		}
		enableBarracuda = bo.isEnableBarracuda();
	}

	private void initWebSenseServerSettings(HMServicesSettings bo) {
		updateWebSenseServer = false;
		hideWebSenseServer = "none";
		if (bo == null) {
			log.debug("initWebSenseServerSettings", "No settings bo in db!");
			// set default value
			accountID = "";
			webSenseDefaultUserName = "";
			webSenseServiceHost = getText("admin.management.webSecurity.websense.serviceHost.hosted");
			securityKey = "";
			wensenseMode = HMServicesSettings.WEBSENSEMODE_HOSTED;
			defaultDomain = "";
			DomainObject domainObject = QueryUtil.findBoByAttribute(DomainObject.class, "objName",
					WEBSENSEQUICKSTART);
			if (domainObject != null) {
				websenseWhitelist = domainObject.getId();
			}
			enableWebsense = false;
			return;
		}

		accountID = bo.getAccountID();
		webSenseDefaultUserName = bo.getWebSenseDefaultUserName();
		webSenseServiceHost = bo.getWebSenseServiceHost();
		securityKey = bo.getSecurityKey();
		wensenseMode = bo.getWensenseMode();
		defaultDomain = bo.getDefaultDomain();
		if (bo.getAccountID()==null || "".equals(bo.getAccountID())){
			DomainObject domainObject = QueryUtil.findBoByAttribute(DomainObject.class, "objName",
					WEBSENSEQUICKSTART);
			if (domainObject != null) {
				websenseWhitelist = domainObject.getId();
			}
		}else if (bo.getWebsenseWhitelist() != null) {
			websenseWhitelist = bo.getWebsenseWhitelist().getId();
		}
		enableWebsense = bo.isEnableWebsense();
	}

	private boolean updateTFTPService() {
		return HmBeAdminUtil.setTftpEnable(tftpState == TFTPSTATE_ON);
	}
	
	
	private void setNotifyFlag(boolean enableSystemL7Switch) {
		if (enableSystemL7Switch == true) {
			return;
		}
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, null);
		if (list != null && list.size() > 0) {
			for (HMServicesSettings setting : list) {
				setting.setNotifyDisableL7(false);
			}
		}
		try {
			QueryUtil.bulkUpdateBos(list); 
		} catch (Exception e) {
			log.error("HmServicesAction", "Set Disable L7 Notify catch exception!", e);
		}
	}
	
	private boolean updateL7Setting() {
		HMServicesSettings bo;
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN));
		boolean isUpdate = true;
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}
		bo.setEnableSystemL7Switch(enableSystemL7Switch);

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}
		} catch (Exception e) {
			log.error("updateSnpService", "Update L7 setting catch exception!", e);
			return false;
		}
		return true;
	}

	private boolean updateSnpService() {
		HMServicesSettings bo;
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN));
		boolean isUpdate = true;
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}
		bo.setSnpMaximum(snpState);

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}
		} catch (Exception e) {
			log.error("updateSnpService", "Update snp maximum catch exception!", e);
			return false;
		}
		return true;
	}

	private boolean updateCAPWAPConfig() {
		// 1. update db
		CapwapSettings bo;
		boolean isUpdate = true;

		List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null, null);
		if (list.isEmpty()) {
			bo = new CapwapSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		if (defaultPassPhrase) {
			bo.setBootStrap("");
		} else {
			bo.setBootStrap(newPassPhrase);
		}

		bo.setDtlsCapability(CapwapSettings.DTLS_DTLSONLY);
		bo.setTimeOut(capwapTimeOut);
		bo.setNeighborDeadInterval(deadInterval);
		bo.setUdpPort(capwapUdpPort);
		bo.setTrapFilterInterval(trapFilterInterval);
		bo.setPrimaryCapwapIP(primaryCapwapIP);
		bo.setBackupCapwapIP(backupCapwapIP);
		bo.setEnableRollback(enableRollback);

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}
		} catch (Exception e) {
			log.error("updateCAPWAPConfig", "Update capwap settings catch exception!", e);
			return false;
		}

		// 2. update capwap settings
		HmBeFaultUtil.setCapwapTrapFilterInterval(trapFilterInterval);

		return HmBeAdminUtil.updateCapwapSettings(bo);
	}

	/**
	 * update email notify configuration
	 *
	 * @return -
	 */
	private boolean updateEmailNotifyConfig() {
		// update bo
		MailNotification bo;
		boolean isUpdate = true;

		List<MailNotification> list = QueryUtil.executeQuery(MailNotification.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new MailNotification();
			bo.setOwner(getDomain());
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		bo.setSendMailFlag(enableNotify);
		bo.setSupportSSL(smtpEncryption && smtpEncryProtocol.equals(SMTP_ENCRY_SSL));
		bo.setSupportTLS(smtpEncryption && smtpEncryProtocol.equals(SMTP_ENCRY_TLS));
		bo.setPort(smtpPort);
		bo.setSupportPwdAuth(supportAuth);
		if (supportAuth) {
			// if auth checked
			bo.setEmailUserName(emailUserName);
			bo.setEmailPassword(emailPassword);
		} else {
			// if auth un-checked, not save username and password
			bo.setEmailUserName(null);
			bo.setEmailPassword(null);
		}

		bo.setServerName(smtpServer);
		bo.setMailFrom(fromEmail);
		String toEmail = getCombinedToEmail();

		bo.setMailTo(toEmail);

		if (enableNotify) {
			// set notification
			boolean hdCPUMgr = false;
			boolean hdMem = false;
			boolean auth = false;
			boolean interf = false;
			boolean l2Dos = false;
			boolean screen = false;
			boolean vpn = false;
			boolean airscreen = false;
			boolean idp = false;
			boolean client = false;
			boolean clientRegister = false;
			
			if(null != enabled_event){
				if (enabled_event != null && !(enabled_event.length == 1 && enabled_event[0].equals("false"))) {
					for (String eventIndex : enabled_event) {
						int index = Integer.valueOf(eventIndex);
						switch (index) {
						case EVENTINDEX_HDCPU:
							hdCPUMgr = true;
							break;
	
						case EVENTINDEX_HDMEMORY:
							hdMem = true;
							break;
	
						case EVENTINDEX_AUTH:
							auth = true;
							break;
	
						case EVENTINDEX_INTERFACE:
							interf = true;
							break;
	
						case EVENTINDEX_L2DOS:
							l2Dos = true;
							break;
	
						case EVENTINDEX_SCREEN:
							screen = true;
							break;
	
						case EVENTINDEX_VPN:
							vpn = true;
							break;
	
						case EVENTINDEX_IDP:
							idp = true;
							break;
	
						case EVENTINDEX_CLIENTMONITOR:
							client = true;
							break;
						
						case EVENTINDEX_CLIENTREGISTER:
							clientRegister = true;
							break;
						default:
							break;
						}
					}
				}
			}

			bo.setHdCpu(hdCPUMgr);
			bo.setHdMemory(hdMem);
			bo.setAuth(auth);
			bo.setInterfaceValue(interf);
			bo.setL2Dos(l2Dos);
			bo.setScreen(screen);
			bo.setVpn(vpn);
			bo.setAirScreen(airscreen);
			bo.setInNetIdp(idp);
			bo.setClientMonitor(client);
			bo.setClientRegister(clientRegister);

			// set alarm
			byte hdRadio = 0;
			byte capwap = 0;
			byte config = 0;
			byte timebomb = 0;
			byte security = 0;
			byte ad = 0;
			byte tca = 0;
			byte system = 0;
			//byte clientAlarm = 0;//hidden connection alarming feature
			byte[] alertArray = new byte[] { hdRadio, capwap, config, timebomb, security,ad,tca,system};//, clientAlarm };//hidden connection alarming feature

			if(null != enabledCritical){
				if (!(enabledCritical.length == 1 && enabledCritical[0].equals("false"))) {
					for (String critical : enabledCritical) {
						alertArray[Integer.valueOf(critical)] += 16;
					}
				}
			}
			
			if(null != enabledMajor){
				if (!(enabledMajor.length == 1 && enabledMajor[0].equals("false"))) {
					for (String major : enabledMajor) {
						alertArray[Integer.valueOf(major)] += 8;
					}
				}
			}
			
			if(null != enabledMinor){
				if (!(enabledMinor.length == 1 && enabledMinor[0].equals("false"))) {
					for (String minor : enabledMinor) {
						alertArray[Integer.valueOf(minor)] += 4;
					}
				}
			}

			if (enabledInfo != null){
				if (!(enabledInfo.length == 1 && enabledInfo[0].equals("false"))) {
					for (String info : enabledInfo) {
						alertArray[Integer.valueOf(info)] += 2;
					}
				}
			}
			
			if (enabledClear != null) {
				if (!(enabledClear.length == 1 && enabledClear[0]
						.equals("false"))) {
					for (String clear : enabledClear) {
						alertArray[Integer.valueOf(clear)] += 1;
					}
				}
			}

			bo.setHdRadio(alertArray[0]);
			bo.setCapWap(alertArray[1]);
			bo.setConfig(alertArray[2]);
			bo.setTimeBomb(alertArray[3]);
			bo.setSecurity(alertArray[4]);
			bo.setAd(alertArray[5]);
			bo.setTca(alertArray[6]);
			bo.setSystem(alertArray[7]);
			//bo.setClient(alertArray[7]);
		}

		// call be
		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			HmBeAdminUtil.updateMailNotification(bo);
		} catch (Exception e) {
			log.error("updateEmailNotifyConfig",
					"Update email notify configuration catch exception!", e);
			return false;
		}

		return getIsInHomeDomain() || updateMailConfig4VHM();
	}

	/**
	 * update bo for VHM
	 *
	 * @return -
	 */
	private boolean updateMailConfig4VHM() {
		MailNotification4VHM bo;
		boolean isUpdate = true;

		List<MailNotification4VHM> list = QueryUtil.executeQuery(MailNotification4VHM.class, null,
				new FilterParams("owner.id", getDomain().getId()));
		if (list.isEmpty()) {
			bo = new MailNotification4VHM();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		String toEmail = getCombinedToEmail();
		bo.setMailTo(toEmail);

		// call be
		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateMailConfig4VHM", "Update email notify configuration catch exception!",
					e);

			return false;
		}
	}

	private String getCombinedToEmail() {
		return toEmail1 + ((toEmail2.length() > 0) ? (";" + toEmail2) : "")
				+ ((toEmail3.length() > 0) ? (";" + toEmail3) : "")
				+ ((toEmail4.length() > 0) ? (";" + toEmail4) : "")
				+ ((toEmail5.length() > 0) ? (";" + toEmail5) : "");
	}

	/**
	 * update snmp settings
	 *
	 * @return -
	 */
	private boolean updateSNMPServerSettings() {
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		try {
			if (null != ipAddressId && ipAddressId > -1) {
				IpAddress newIP = findBoById(IpAddress.class, ipAddressId);
				bo.setSnmpReceiverIP(newIP);
			} else {
				short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME
						: IpAddress.TYPE_IP_ADDRESS;
				bo.setSnmpReceiverIP(CreateObjectAuto.createNewIP(inputIpValue, ipType,
						getDomain(), "For SNMP Trap Receiver"));
			}

			bo.setSnmpCommunity(snmpCommunity);

			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateSNMPServerSettings",
					"Update SNMP server settings catch exception!", e);
			return false;
		}
	}

	private boolean updateNotifyInfoSettings() {
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		try {
			bo.setShowNotifyInfo(showNotifyInfo);
			bo.setNotifyInformation(hmNotifyInfo);
			bo.setNotifyInformationTitle(hmNotifyTitle);

			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateNotifyInfoSettings", "catch exception", e);
			return false;
		}
	}

	private boolean updateTeacherViewSettings() {
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		try {
			bo.setEnableTeacher(enableTeacher);
			bo.setEnableTVProxy(enableTVProxy);
			bo.setEnableCaseSensitive(enableCaseSensitive ? (short)0: (short)1);
			if(enableTVProxy){
				bo.setTvProxyIP(tvProxyIP);
				bo.setTvProxyPort(tvProxyPort);
				bo.setTvAutoProxyFile(tvAutoProxyFile);				
			}

			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			if (getIsInHomeDomain()) {
				NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED = enableTeacher;

				if (enableTeacher && checkNeedUpdateCASSettings()) {
					addActionMessage(MgrUtil.getUserMessage("message.enable.teacherview",NmsUtil.getOEMCustomer().getNmsName()));
				}
			} else {
				if (enableTeacher) {
					HmUserGroup teacherGroup = QueryUtil.findBoByAttribute(HmUserGroup.class,
							"groupName", HmUserGroup.TEACHER, getDomainId());
					if (teacherGroup == null) {
						teacherGroup = new HmUserGroup();
						teacherGroup.setGroupName(HmUserGroup.TEACHER);
						teacherGroup.setDefaultFlag(true);
						teacherGroup.setOwner(getDomain());
						teacherGroup.setGroupAttribute(HmUserGroup.TEACHER_ATTRIBUTE);
						teacherGroup.setFeaturePermissions(BoMgmt.getDomainMgmt()
								.getTeacherPermission());
						createBo(teacherGroup);
					}
				}
			}

			refreshNavigationTree();

			return true;
		} catch (Exception e) {
			log.error("updateTeacherViewSettings", "catch exception", e);
			return false;
		}
	}

	private void updateAirTight() {
		// operate WIFI scanner
		SgeIntegrator sgeIntegrator = AhAppContainer.getBeMiscModule().getAirTightSgeIntegrator();
	//	String clientId = scanner.getClientIdentifierPrefix();

		if (this.isEnableAirtight()) {
				if(sgeIntegrator.isStarted()) {
					sgeIntegrator.stop();
				}

			// start WIFI scanner
			try {
				AirtightSettings config = new AirtightSettings();
				config.setServerURL(getAirTightURL());
				config.setUserName(getAirTightUserName());
				config.setPassword(getAirTightPassword());
				config.setSyncInterval(Integer.parseInt(getAirTightInterval()));
			//	config.setClientID(clientId);
				sgeIntegrator.start(config, false);
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				addActionError(errorMessage);

				/*
				 * failed to start wifi scanner, update database
				 */
				List<AirtightSettings> list = QueryUtil.executeQuery(AirtightSettings.class, null, null);

				if(!list.isEmpty()) {
					AirtightSettings setting = list.get(0);
					setting.setEnabled(false);

					try {
						QueryUtil.updateBo(setting);
					} catch (Exception e1) {
						log.error("Failed to update AirTight settings", e);
					}
				}

				this.updateAirtight = true;
				return;
			}
		} else {
			// stop WIFI scanner
			try {
				if(sgeIntegrator.isStarted()) {
					sgeIntegrator.stop();
				}
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.hmSettings.airtight.stop.failed"), e);
				addActionError(MgrUtil.getUserMessage("error.hmSettings.airtight.stop.failed"));
				this.updateAirtight = true;
				return;
			}
		}

		// save configuration into database
		try {
			AirtightSettings setting;
			List<AirtightSettings> list = QueryUtil.executeQuery(AirtightSettings.class, null, null);

			if (list.isEmpty()) {
				setting = new AirtightSettings();
				setting.setOwner(getDomain());
			} else {
				setting = list.get(0);
			}

			setting.setEnabled(this.isEnableAirtight());
			setting.setServerURL(this.getAirTightURL());
			setting.setUserName(this.getAirTightUserName());
			setting.setPassword(this.getAirTightPassword());
			setting.setSyncInterval(Integer.parseInt(this.getAirTightInterval()));
		//	setting.setClientID(clientId);

			if (setting.getId() == null || setting.getId() == 0) {
				// create a new bo
				QueryUtil.createBo(setting);
			} else {
				QueryUtil.updateBo(setting);
			}

			addActionMessage(MgrUtil.getUserMessage("error.hmSettings.airtight.update.ok"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("info.hmSettings.airtight.update"));
		} catch (Exception e) {
			log.error("Failed to update AirTight settings", e);
			addActionError(MgrUtil.getUserMessage("error.hmSettings.airtight.update.failed"));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("info.hmSettings.airtight.update"));
			this.updateAirtight = true;
		}

		this.updateAirtight = false;
	}

	private boolean checkNeedUpdateCASSettings()
	{
		if (NmsUtil.isHostedHMApplication()) {
			return false;
		}

		if(this.casServer == null
				|| this.casServer.trim().length() == 0) {
			return false;
		}

		String server = CasTool.getCASServerIP();

		if (server == null) {
			return false;
		}

		if (!server.equals(this.casServer)) {

			CasTool.setCASServerIP(this.casServer);
			CasTool.setCASClientIP(this.casServer);

			return true;
		}

		return false;
	}

	/**
	 * update syslog configuration
	 *
	 * @throws BeOperateException
	 *             -
	 * @return -
	 */
	@SuppressWarnings("unchecked")
    private boolean updateLogServer() throws BeOperateException {
		subnetList = (List<HmSubnet>) MgrUtil.getSessionAttribute(getSelectedL2FeatureKey()
				+ "_subnetlist");
		if (subnetList == null) {
			subnetList = new ArrayList<HmSubnet>();
		}

		BeLogServerInfo logServerInfo = new BeLogServerInfo();
		logServerInfo.setIsLogServer(logStatus.equals(LOGSTATUS_ENABLE));
		logServerInfo.setIsFullNet(entryStatus.equals(ENTRYSTATUS_ANY));
		List<String> infoList = new ArrayList<String>(subnetList.size());
		for (HmSubnet subnet : subnetList) {
			infoList.add(subnet.getIp() + "/" + subnet.getMask());
		}
		logServerInfo.setSubNet(infoList);

		return HmBeAdminUtil.setLogServer(logServerInfo);
	}

	private boolean updateLoginAuth() {
		// 1. update bo
		HmLoginAuthentication bo;
		boolean isUpdate = true;

		List<HmLoginAuthentication> list = QueryUtil.executeQuery(HmLoginAuthentication.class,
				null, null);
		if (list.isEmpty()) {
			bo = new HmLoginAuthentication();
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		bo.setHmAdminAuth(adminAuth);
		if (EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL == adminAuth) {
			bo.setAuthType(Cwp.AUTH_METHOD_PAP);
			bo.setRadiusAssignment(null);
		} else {
			bo.setAuthType(authType);
			RadiusAssignment radius = QueryUtil.findBoById(RadiusAssignment.class, radiusServiceId,
					this);
			if (radius == null || radius.getServices().size() == 0) {
				addActionError(MgrUtil.getUserMessage("error.authfail.radius.noserver"));
				return false;
			} else {
				bo.setRadiusAssignment(radius);
			}
		}

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log
					.error("updateLoginAuth",
							"Update login authentication settings catch exception!", e);
			return false;
		}
	}
    
	private boolean updateIDM;
    private String idmUserEmail;
    private boolean enabledProxy;
    private boolean enableProxyIdm;

    private void initIDMServices() {
        try {
            if(!NmsUtil.isHostedHMApplication() && getIsInHomeDomain()) {
                // only initial the value for one-premise
                CloudAuthCustomer customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class,
                        "owner.domainName", HmDomain.HOME_DOMAIN);
                if(null != customerObj) {
                    idmUserEmail = customerObj.getUserName();
                    enableProxyIdm =  customerObj.isUsingProxy();
                }
                HMServicesSettings service = QueryUtil.findBoByAttribute(HMServicesSettings.class, 
                        "owner.id", getDomain().getId());
                if(null != service) {
                    enabledProxy = service.isEnableProxy();
                }
            }
        } catch (Exception e) {
            log.error("initCloudAuthServer", "Error when init the ID Manager Service settings.", e);
        }
    }
    
    private boolean updateIDMServices() {
        if(!NmsUtil.isHostedHMApplication() && getIsInHomeDomain()) {
            try {
                CloudAuthCustomer customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class,
                        "owner.domainName", HmDomain.HOME_DOMAIN);
                if(null != customerObj && customerObj.isUsingProxy() == !enableProxyIdm) {
                    customerObj.setUsingProxy(enableProxyIdm);
                    
                    QueryUtil.updateBo(customerObj);
                    
                    if(enabledProxy) {
                        addActionMessage(MgrUtil
                                .getUserMessage(enableProxyIdm ? "home.services.idm.retrieveCustomerId.enableproxy.succ"
                                        : "home.services.idm.retrieveCustomerId.disableproxy.succ"));
                    }
                }
                return true;
            } catch (Exception e) {
                log.error("updateIDMServices", "Error when update the ID Manager Service settings.", e);
                if(enabledProxy) {
                    addActionError(MgrUtil.getUserMessage("home.services.idm.retrieveCustomerId.proxy.fail"));
                }
            }
        }
        return false;
    }
    
    public boolean isInHomeDomain() {
        if (null == getUserContext()) {
            return false;
        }
        HmDomain hmDomain = getUserContext().getSwitchDomain() != null ? getUserContext()
                .getSwitchDomain() : getUserContext().getDomain();
        return HmDomain.HOME_DOMAIN.equals(hmDomain.getDomainName());
    }
    
	/*
	 * radius service is lazy to get
	 *
	 * @see com.ah.bo.mgmt.QueryBo#load(com.ah.bo.HmBo)
	 */
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusAssignment) {
			RadiusAssignment radius = (RadiusAssignment) bo;
			if (radius.getServices() != null)
				radius.getServices().size();
		}
		
		if(bo instanceof OpenDNSMapping){
			OpenDNSMapping mapping = (OpenDNSMapping)bo;
			if(mapping.getOpenDNSAccount() != null){
				mapping.getOpenDNSAccount().getId();
			}
			
			if(mapping.getOpenDNSDevice() != null){
				mapping.getOpenDNSDevice().getId();
			}
		}
		
		if(bo instanceof HMServicesSettings){
			HMServicesSettings settings = (HMServicesSettings)bo;
			if(settings != null){
				if(settings.getOpenDNSAccount() != null){
					settings.getOpenDNSAccount().getId();
				}
			}
		}
		return null;
	}

	/**
	 * reset
	 *
	 * @throws Exception
	 *             -
	 */
	private void cancelOperation() throws Exception {
		initValue();
	}

	public EnumItem[] getAdminAuthValues() {
		return EnumConstUtil.ENUM_ADMIN_USER_AUTH_TYPE;
	}

	public EnumItem[] getAuthTypeValues() {
		return Cwp.ENUM_AUTH_METHOD;
	}

	public static final int	TFTPSTATE_ON	= 1;

	public static final int	TFTPSTATE_OFF	= 2;

	public EnumItem[] getTftpStateValues() {
		EnumItem[] values = new EnumItem[2];
		values[0] = new EnumItem(TFTPSTATE_ON, "On");
		values[1] = new EnumItem(TFTPSTATE_OFF, "Off");

		return values;
	}

	public List<CheckItem> getRadiusAssignment() {
		List<?> profiles = QueryUtil.executeQuery(
				"select bo.id, bo.radiusName from " + RadiusAssignment.class.getSimpleName() + " bo", new SortParams("id"), null, domainId);
		List<CheckItem> listService = new ArrayList<CheckItem>();
		for (Object obj : profiles) {
			Object[] profile = (Object[]) obj;
			CheckItem checkItem = new CheckItem((Long) profile[0], (String) profile[1]);
			listService.add(checkItem);
		}
		if (profiles.isEmpty()) {
			listService.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return listService;
	}

	public List<CheckItem> getClientRefreshFilterList() {
		List<?> filters = QueryUtil.executeQuery(
				"select bo.id, bo.filterName from " + ActiveClientFilter.class.getSimpleName() + " bo", new SortParams("id"),
				null, getDomainId());
		List<CheckItem> filterList = new ArrayList<CheckItem>();
		for (Object obj : filters) {
			Object[] filter = (Object[]) obj;
			CheckItem checkItem = new CheckItem((Long) filter[0], (String) filter[1]);
			filterList.add(checkItem);
		}

		return filterList;
	}

	private Long	ipAddressId;

	private String	inputIpValue	= "";

	public String getInputIpValue() {
		if (null != getIpAddressId()) {
			for (CheckItem item : getAvailableIpAddress()) {
				if (item.getId().longValue() == getIpAddressId().longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue) {
		this.inputIpValue = inputIpValue;
	}

	public Long getIpAddressId() {
		// if (null == ipAddressId) {
		// if (null != getDataSource().getServerIP()) {
		// ipAddressId = getDataSource().getServerIP().getId();
		// }
		// }
		return ipAddressId;
	}

	@SuppressWarnings("unchecked")
    private List<HmSubnet> addSubnet(HmSubnet hmSubnet) {
		subnetList = (List<HmSubnet>) MgrUtil.getSessionAttribute(getSelectedL2FeatureKey()
				+ "_subnetlist");
		if (subnetList == null) {
			subnetList = new ArrayList<HmSubnet>();
		}

		if (subnetList.contains(hmSubnet)) {
			addActionError(MgrUtil.getUserMessage("action.error.duplicate.subnet.exist"));

			return subnetList;
		}

		hmSubnet.setId(getNextId());
		subnetList.add(hmSubnet);

		MgrUtil.setSessionAttribute(getSelectedL2FeatureKey() + "_subnetlist", subnetList);
		return subnetList;
	}

	private int removeSubnet(Collection<Long> ids) {
		for (Iterator<HmSubnet> iter = subnetList.iterator(); iter.hasNext();) {
			HmSubnet subnet = iter.next();
			if (ids.contains(subnet.getId())) {
				iter.remove();
			}
		}

		return ids.size();
	}

	@SuppressWarnings("unchecked")
	private List<HmSubnet> removeSelectedSubnets() {
		subnetList = (List<HmSubnet>) MgrUtil.getSessionAttribute(getSelectedL2FeatureKey()
				+ "_subnetlist");
		if (subnetList == null) {
			subnetList = new ArrayList<HmSubnet>();
		}

		if (getAllSelectedIds() == null) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
			return subnetList;
		}

		int selectedCount = getAllSelectedIds().size();
		if (selectedCount == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
			return subnetList;
		}

		int removeCount = removeSubnet(getAllSelectedIds());
		if (removeCount > 0) {
			addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, removeCount + ""));
		}

		MgrUtil.setSessionAttribute(getSelectedL2FeatureKey() + "_subnetlist", subnetList);
		return subnetList;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}

	public List<CheckItem> getAvailableIpAddress() {
		return getIpObjectsByIpAndName();
	}

	public boolean isAdminSessionUpdate() {
		return adminSessionUpdate;
	}

	public void setAdminSessionUpdate(boolean adminSessionUpdate) {
		this.adminSessionUpdate = adminSessionUpdate;
	}

	public int getIpAddressLength() {
		return 15;
	}

	public boolean isUpdateAuth() {
		return updateAuth;
	}

	public void setUpdateAuth(boolean updateAuth) {
		this.updateAuth = updateAuth;
	}

	public short getAdminAuth() {
		return adminAuth;
	}

	public void setAdminAuth(short adminAuth) {
		this.adminAuth = adminAuth;
	}

	public boolean getEnableCaseSensitive() {
		return enableCaseSensitive;
	}

	public void setEnableCaseSensitive(boolean enableCaseSensitive) {
		this.enableCaseSensitive = enableCaseSensitive;
	}

	public String getHideRadius() {
		return hideRadius;
	}

	@Override
	public HmLoginAuthentication getDataSource() {
		return (HmLoginAuthentication) dataSource;
	}

	public Long getRadiusServiceId() {
		return radiusServiceId;
	}

	public void setRadiusServiceId(Long radiusServiceId) {
		this.radiusServiceId = radiusServiceId;
	}

	public boolean isDisableAuthSelect() {
		if (NmsUtil.isHostedHMApplication()) {
			int domainCount = CacheMgmt.getInstance().getCacheDomainCount();
			return domainCount > 1;
		} else {
			return false;
		}
	}

	public boolean isDisableAdminAuth() {
		if (NmsUtil.isHostedHMApplication()) {
			int domainCount = CacheMgmt.getInstance().getCacheDomainCount();
			return domainCount > 1 || !updateAuth;
		} else {
			return false;
		}
	}

	public boolean isDisableTftpState() {
		return disableTftpState;
	}

	public void setDisableTftpState(boolean disableTftpState) {
		this.disableTftpState = disableTftpState;
	}

	public int getTftpState() {
		return tftpState;
	}

	public void setTftpState(int tftpState) {
		this.tftpState = tftpState;
	}

	public boolean isTftpStateUpdate() {
		return tftpStateUpdate;
	}

	public void setTftpStateUpdate(boolean tftpStateUpdate) {
		this.tftpStateUpdate = tftpStateUpdate;
	}

	public String getSnmpCommunity() {
		return snmpCommunity;
	}

	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	public boolean isUpdateSNMP() {
		return updateSNMP;
	}

	public void setUpdateSNMP(boolean updateSNMP) {
		this.updateSNMP = updateSNMP;
	}

	public String getHideSNMP() {
		return hideSNMP;
	}

	public void setHideSNMP(String hideSNMP) {
		this.hideSNMP = hideSNMP;
	}

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

	/**
	 * support some setting to vhm
	 *
	 * @return -
	 */
	public String getHide4VHM() {
		if (getIsInHomeDomain()) {
			return "";
		}

		return "none";
	}

	public String getHide4HHM() {
		if (NmsUtil.isHostedHMApplication()) {
			return "none";
		}

		return "";
	}

	public String getHide4HHMHome() {
		if (NmsUtil.isHostedHMApplication()) {
			if (getIsInHomeDomain()) {
				if (getUserContext().isSuperUser()){
					return "";
				}
			}
		}
		return "none";
	}

	public String getDisplayTeacher() {
		if (isEasyMode()) {
			return "none";
		}

		if (getIsInHomeDomain()) {
			return "";
		}

		if (NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED) {
			return "";
		}

		return "none";
	}
	
	public String getDisplayPresence() {
		if (getIsInHomeDomain()) {
			return "";
		}
		return "none";
	}
	
	public String getHide4ClientManagement(){
		String result = "none";  //hide
		if(isEasyMode()){
			return result;
		}
		if(NmsUtil.isHostedHMApplication()){
			if(!getIsInHomeDomain()){
				result = "block";
			}
		}else{
			if(getIsInHomeDomain()){
				result = "block";
			}
		}
		return result;
	}
	
	public String getDisableClientManagement(){
		HMServicesSettings bo = null;
		String disable = "true";
		try{
			bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                    "owner", getDomain());
			if(null != bo){
				if(!bo.isEnableClientManagement()){
					disable = "false";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return disable;
	}

	/**
	 * get id for hmRoute
	 *
	 * @return -
	 */
	private long getNextId() {
		if (++nextId == 2147483647) {
			nextId = 1;
		}

		return nextId;
	}

	public List<HmSubnet> getSubnetList() {
		return subnetList;
	}

	public void setSubnetList(List<HmSubnet> subnetList) {
		this.subnetList = subnetList;
	}

	public String getDisabledAnyEntry() {
		return disabledAnyEntry;
	}

	public void setDisabledAnyEntry(String disabledAnyEntry) {
		this.disabledAnyEntry = disabledAnyEntry;
	}

	public boolean isDisabledLogServer() {
		return disabledLogServer;
	}

	public void setDisabledLogServer(boolean disabledLogServer) {
		this.disabledLogServer = disabledLogServer;
	}

	public String getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}

	public String getHideLogServer() {
		return hideLogServer;
	}

	public void setHideLogServer(String hideLogServer) {
		this.hideLogServer = hideLogServer;
	}

	public String getLogStatus() {
		return logStatus;
	}

	public void setLogStatus(String logStatus) {
		this.logStatus = logStatus;
	}

	public void setHideRadius(String hideRadius) {
		this.hideRadius = hideRadius;
	}

	public boolean isUpdateLogServer() {
		return updateLogServer;
	}

	public void setUpdateLogServer(boolean updateLogServer) {
		this.updateLogServer = updateLogServer;
	}

	public String getSubnetIP() {
		return subnetIP;
	}

	public void setSubnetIP(String subnetIP) {
		this.subnetIP = subnetIP;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public String getBackupCapwapIP() {
		return backupCapwapIP;
	}

	public void setBackupCapwapIP(String backupCapwapIP) {
		this.backupCapwapIP = backupCapwapIP;
	}

	public short getCapwapTimeOut() {
		return capwapTimeOut;
	}

	public void setCapwapTimeOut(short capwapTimeOut) {
		this.capwapTimeOut = capwapTimeOut;
	}

	public int getCapwapUdpPort() {
		return capwapUdpPort;
	}

	public void setCapwapUdpPort(int capwapUdpPort) {
		this.capwapUdpPort = capwapUdpPort;
	}

	public String getConfirmPassPhrase() {
		return confirmPassPhrase;
	}

	public void setConfirmPassPhrase(String confirmPassPhrase) {
		this.confirmPassPhrase = confirmPassPhrase;
	}

	public short getDeadInterval() {
		return deadInterval;
	}

	public void setDeadInterval(short deadInterval) {
		this.deadInterval = deadInterval;
	}

	public boolean isDefaultPassPhrase() {
		return defaultPassPhrase;
	}

	public void setDefaultPassPhrase(boolean defaultPassPhrase) {
		this.defaultPassPhrase = defaultPassPhrase;
	}

	public String getNewPassPhrase() {
		return newPassPhrase;
	}

	public void setNewPassPhrase(String newPassPhrase) {
		this.newPassPhrase = newPassPhrase;
	}

	public boolean isDisablePassPhrase() {
		return disablePassPhrase;
	}

	public void setDisablePassPhrase(boolean disablePassPhrase) {
		this.disablePassPhrase = disablePassPhrase;
	}

	public String getPrimaryCapwapIP() {
		return primaryCapwapIP;
	}

	public void setPrimaryCapwapIP(String primaryCapwapIP) {
		this.primaryCapwapIP = primaryCapwapIP;
	}

	public short getTrapFilterInterval() {
		return trapFilterInterval;
	}

	public void setTrapFilterInterval(short trapFilterInterval) {
		this.trapFilterInterval = trapFilterInterval;
	}

	public boolean isUpdateCAPWAP() {
		return updateCAPWAP;
	}

	public void setUpdateCAPWAP(boolean updateCAPWAP) {
		this.updateCAPWAP = updateCAPWAP;
	}

	public String getHmNotifyInfo() {
		return hmNotifyInfo;
	}

	public void setHmNotifyInfo(String hmNotifyInfo) {
		this.hmNotifyInfo = hmNotifyInfo;
	}

	public boolean isShowNotifyInfo() {
		return showNotifyInfo;
	}

	public void setShowNotifyInfo(boolean showNotifyInfo) {
		this.showNotifyInfo = showNotifyInfo;
	}

	public boolean isUpdateNotifyInfo() {
		return updateNotifyInfo;
	}

	public void setUpdateNotifyInfo(boolean updateNotifyInfo) {
		this.updateNotifyInfo = updateNotifyInfo;
	}

	public boolean isUpdateEmail() {
		return updateEmail;
	}

	public void setUpdateEmail(boolean updateEmail) {
		this.updateEmail = updateEmail;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public String[] getEnabled_event() {
		return enabled_event;
	}

	public void setEnabled_event(String[] enabled_event) {
		this.enabled_event = enabled_event;
	}

	public String[] getEnabledClear() {
		return enabledClear;
	}

	public void setEnabledClear(String[] enabledClear) {
		this.enabledClear = enabledClear;
	}

	public String[] getEnabledCritical() {
		return enabledCritical;
	}

	public void setEnabledCritical(String[] enabledCritical) {
		this.enabledCritical = enabledCritical;
	}

	public String[] getEnabledInfo() {
		return enabledInfo;
	}

	public void setEnabledInfo(String[] enabledInfo) {
		this.enabledInfo = enabledInfo;
	}

	public String[] getEnabledMajor() {
		return enabledMajor;
	}

	public void setEnabledMajor(String[] enabledMajor) {
		this.enabledMajor = enabledMajor;
	}

	public String[] getEnabledMinor() {
		return enabledMinor;
	}

	public void setEnabledMinor(String[] enabledMinor) {
		this.enabledMinor = enabledMinor;
	}

	public boolean isEnableNotify() {
		return enableNotify;
	}

	public void setEnableNotify(boolean enableNotify) {
		this.enableNotify = enableNotify;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public boolean isSupportAuth() {
		return supportAuth;
	}

	public void setSupportAuth(boolean supportAuth) {
		this.supportAuth = supportAuth;
	}

	public String getToEmail1() {
		return toEmail1;
	}

	public void setToEmail1(String toEmail1) {
		this.toEmail1 = toEmail1;
	}

	public String getToEmail2() {
		return toEmail2;
	}

	public void setToEmail2(String toEmail2) {
		this.toEmail2 = toEmail2;
	}

	public String getToEmail3() {
		return toEmail3;
	}

	public void setToEmail3(String toEmail3) {
		this.toEmail3 = toEmail3;
	}

	public String getToEmail4() {
		return toEmail4;
	}

	public void setToEmail4(String toEmail4) {
		this.toEmail4 = toEmail4;
	}

	public String getToEmail5() {
		return toEmail5;
	}

	public void setToEmail5(String toEmail5) {
		this.toEmail5 = toEmail5;
	}

	final String[][]		eventTypeArray				= new String[][] {
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[3], "Hardware CPU" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[4], "Hardware Memory" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[5], "Authentication" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[6], "Interface" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[7], "L2 DoS" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[11], "Screen" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[15], "VPN Service" },
			{ "In-net Rogue AP", "In-net Rogue AP" }, { "Client Monitor", "Client Monitor" }
			//,{ "Client Register", "Client Register" }
			};

	public static final int	EVENTINDEX_HDCPU			= 0;
	public static final int	EVENTINDEX_HDMEMORY			= 1;
	public static final int	EVENTINDEX_AUTH				= 2;
	public static final int	EVENTINDEX_INTERFACE		= 3;
	public static final int	EVENTINDEX_L2DOS			= 4;
	public static final int	EVENTINDEX_SCREEN			= 5;
	public static final int	EVENTINDEX_VPN				= 6;
	public static final int	EVENTINDEX_IDP				= 7;
	public static final int	EVENTINDEX_CLIENTMONITOR	= 8;
	public static final int	EVENTINDEX_CLIENTREGISTER	= 9;

	public List<EventItem> getEventTypeList() {
		List<EventItem> eventTypeList = new ArrayList<EventItem>();

		for (int i = 0; i < eventTypeArray.length; i++) {
			if(isFullMode()){
				EventItem item = new EventItem(eventTypeArray[i][0], eventTypeArray[i][1]);
				if (ArrayUtils.contains(enabled_event, String.valueOf(i))) {
					item.setEnabled_event(true);
				}
				eventTypeList.add(item);
			}else{
				 if(!eventTypeArray[i][0].equals("Client Register")){
					EventItem item = new EventItem(eventTypeArray[i][0], eventTypeArray[i][1]);
					if (ArrayUtils.contains(enabled_event, String.valueOf(i))) {
						item.setEnabled_event(true);
					}
					eventTypeList.add(item);
				 }
			}
		}
		return eventTypeList;
	}

	class EventItem {
		String	value;

		String	displayName;	// joseph chen , 08-12-31

		boolean	enabled_event;

		public EventItem(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * getter of displayName
		 *
		 * @return the displayName
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * setter of displayName
		 *
		 * @param displayName
		 *            the displayName to set
		 */
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public boolean isEnabled_event() {
			return enabled_event;
		}

		public void setEnabled_event(boolean enabled) {
			this.enabled_event = enabled;
		}
	}

	final String[][]	alertTypeArray	= new String[][] {
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[8], "Hardware Radio" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[9], "CAPWAP" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[10], "Configuration" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[12], "License Expiration" },
			{ "Spoofed BSSIDs", "Spoofed BSSIDs" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[16], "User Database" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[17], "HiveManager Alarms" },
			{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[20], "System" }//,
			//{ BeFaultConst.TRAP_SEND_MAIL_TYPEX[18], "Client"}//hidden connection alarming feature, Jan.31 2013
			};

	public List<AlertItem> getAlertTypeList() {
		List<AlertItem> alertTypeList = new ArrayList<AlertItem>();

		for (int i = 0; i < alertTypeArray.length; i++) {
			AlertItem item = new AlertItem(alertTypeArray[i][0], alertTypeArray[i][1]);
			if (ArrayUtils.contains(enabledCritical, String.valueOf(i))) {
				item.setEnabledCritical(true);
			}
			if (ArrayUtils.contains(enabledMajor, String.valueOf(i))) {
				item.setEnabledMajor(true);
			}
			if (ArrayUtils.contains(enabledMinor, String.valueOf(i))) {
				item.setEnabledMinor(true);
			}
			if (ArrayUtils.contains(enabledInfo, String.valueOf(i))) {
				item.setEnabledInfo(true);
			}
			if (ArrayUtils.contains(enabledClear, String.valueOf(i))) {
				item.setEnabledClear(true);
			}
			alertTypeList.add(item);
		}

		return alertTypeList;
	}

	class AlertItem {
		String	value;

		String	displayName;		// joseph chen, 08-12-31

		boolean	enabledCritical;

		boolean	enabledMajor;

		boolean	enabledMinor;

		boolean	enabledInfo;

		boolean	enabledClear;

		public AlertItem(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public boolean isEnabledClear() {
			return enabledClear;
		}

		public void setEnabledClear(boolean enabledClear) {
			this.enabledClear = enabledClear;
		}

		public boolean isEnabledCritical() {
			return enabledCritical;
		}

		public void setEnabledCritical(boolean enabledCritical) {
			this.enabledCritical = enabledCritical;
		}

		public boolean isEnabledInfo() {
			return enabledInfo;
		}

		public void setEnabledInfo(boolean enabledInfo) {
			this.enabledInfo = enabledInfo;
		}

		public boolean isEnabledMajor() {
			return enabledMajor;
		}

		public void setEnabledMajor(boolean enabledMajor) {
			this.enabledMajor = enabledMajor;
		}

		public boolean isEnabledMinor() {
			return enabledMinor;
		}

		public void setEnabledMinor(boolean enabledMinor) {
			this.enabledMinor = enabledMinor;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * getter of displayName
		 *
		 * @return the displayName
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * setter of displayName
		 *
		 * @param displayName
		 *            the displayName to set
		 */
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}

	public String getHmNotifyTitle() {
		return hmNotifyTitle;
	}

	public void setHmNotifyTitle(String hmNotifyTitle) {
		this.hmNotifyTitle = hmNotifyTitle;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public boolean isSmtpEncryption() {
		return smtpEncryption;
	}

	public void setSmtpEncryption(boolean smtpEncryption) {
		this.smtpEncryption = smtpEncryption;
	}

	public String getSmtpEncryProtocol() {
		return smtpEncryProtocol;
	}

	public void setSmtpEncryProtocol(String smtpEncryProtocol) {
		this.smtpEncryProtocol = smtpEncryProtocol;
	}

	public boolean isEnableRollback() {
		return enableRollback;
	}

	public void setEnableRollback(boolean enableRollback) {
		this.enableRollback = enableRollback;
	}

	public boolean isUpdateTeacher() {
		return updateTeacher;
	}

	public void setUpdateTeacher(boolean updateTeacher) {
		this.updateTeacher = updateTeacher;
	}

	public boolean isEnableTeacher() {
		return enableTeacher;
	}

	public void setEnableTeacher(boolean enableTeacher) {
		this.enableTeacher = enableTeacher;
	}

	public String getCasServer() {
		return this.casServer;
	}

	public void setCasServer(String casServer) {
		this.casServer = casServer;
	}
	/**
	 * getter of updateAirtight
	 * @return the updateAirtight
	 */
	public boolean isUpdateAirtight() {
		return updateAirtight;
	}

	/**
	 * setter of updateAirtight
	 * @param updateAirtight the updateAirtight to set
	 */
	public void setUpdateAirtight(boolean updateAirtight) {
		this.updateAirtight = updateAirtight;
	}

	/**
	 * getter of enableAirtight
	 * @return the enableAirtight
	 */
	public boolean isEnableAirtight() {
		return enableAirtight;
	}

	/**
	 * setter of enableAirtight
	 * @param enableAirtight the enableAirtight to set
	 */
	public void setEnableAirtight(boolean enableAirtight) {
		this.enableAirtight = enableAirtight;
	}

	/**
	 * getter of airTightURL
	 * @return the airTightURL
	 */
	public String getAirTightURL() {
		return airTightURL;
	}

	/**
	 * setter of airTightURL
	 * @param airTightURL the airTightURL to set
	 */
	public void setAirTightURL(String airTightURL) {
		this.airTightURL = airTightURL;
	}

	/**
	 * getter of airTightUserName
	 * @return the airTightUserName
	 */
	public String getAirTightUserName() {
		return airTightUserName;
	}

	/**
	 * setter of airTightUserName
	 * @param airTightUserName the airTightUserName to set
	 */
	public void setAirTightUserName(String airTightUserName) {
		this.airTightUserName = airTightUserName;
	}

	/**
	 * getter of airTightInterval
	 * @return the airTightInterval
	 */
	public String getAirTightInterval() {
		return airTightInterval;
	}

	/**
	 * setter of airTightInterval
	 * @param airTightInterval the airTightInterval to set
	 */
	public void setAirTightInterval(String airTightInterval) {
		this.airTightInterval = airTightInterval;
	}

	/**
	 * getter of airTightPassword
	 * @return the airTightPassword
	 */
	public String getAirTightPassword() {
		return airTightPassword;
	}

	/**
	 * setter of airTightPassword
	 * @param airTightPassword the airTightPassword to set
	 */
	public void setAirTightPassword(String airTightPassword) {
		this.airTightPassword = airTightPassword;
	}

	public String getRpcInterval() {
		return rpcInterval;
	}

	public void setRpcInterval(String rpcInterval) {
		this.rpcInterval = rpcInterval;
	}

	public String getRpcUserName() {
		return rpcUserName;
	}

	public void setRpcUserName(String rpcUserName) {
		this.rpcUserName = rpcUserName;
	}

	public String getRpcPasswd() {
		return rpcPasswd;
	}

	public void setRpcPasswd(String rpcPasswd) {
		this.rpcPasswd = rpcPasswd;
	}

	/**
	 * @return the rpcRePasswd
	 */
	public String getRpcRePasswd() {
		return rpcRePasswd;
	}

	/**
	 * @param rpcRePasswd
	 *		the rpcRePasswd to set
	 */
	public void setRpcRePasswd(String rpcRePasswd) {
		this.rpcRePasswd = rpcRePasswd;
	}

	public boolean isUpdateRPC() {
		return updateRPC;
	}

	public void setUpdateRPC(boolean updateRPC) {
		this.updateRPC = updateRPC;
	}

	/**
	 * @return the enableRPCServer
	 */
	public boolean isEnableRPCServer() {
		return enableRPCServer;
	}

	/**
	 * @param enableRPCServer
	 *		the enableRPCServer to set
	 */
	public void setEnableRPCServer(boolean enableRPCServer) {
		this.enableRPCServer = enableRPCServer;
	}

	public int getServerUrlLength() {
		setDataSource(AirtightSettings.class);
		return getAttributeLength("serverURL");
	}

	public int getUserNameLength() {
		setDataSource(AirtightSettings.class);
		return getAttributeLength("userName");
	}

	public int getPasswordLength() {
		setDataSource(AirtightSettings.class);
		return getAttributeLength("password");
	}

	public int getMinInterval() {
		return isDebugMode() ?
				AirtightSettings.MIN_INTERVAL_DEBUG
				: AirtightSettings.MIN_INTERVAL_RELEASE;
	}

	public int getMaxInterval() {
		return AirtightSettings.MAX_INTERVAL;
	}

	public String getAirTightClientID() {
		return AhAppContainer.getBeMiscModule().getAirTightSgeIntegrator().getClientIdentifierPrefix();
	}

	public int getClientIDLength() {
		setDataSource(AirtightSettings.class);
		return getAttributeLength("clientID");
	}

	public String getShowAirtightSection() {
		return this.updateAirtight ? "" : "none";
	}

	public String getShowRPCSection() {
		return this.updateRPC ? "" : "none";
	}

	public TextItem[] getDisableLogServer() {
		return new TextItem[] {new TextItem("disableLog",
				MgrUtil.getUserMessage("admin.management.updateLogServer.disable"))};
	}

	public TextItem[] getEnableLogServer() {
		return new TextItem[] {new TextItem("enableLog",
				MgrUtil.getUserMessage("admin.management.updateLogServer.enable"))};
	}

	public boolean isDisableSnpState() {
		return disableSnpState;
	}

	public void setDisableSnpState(boolean disableSnpState) {
		this.disableSnpState = disableSnpState;
	}

	public int getSnpState() {
		return snpState;
	}

	public void setSnpState(int snpState) {
		this.snpState = snpState;
	}

	public boolean isSnpStateUpdate() {
		return snpStateUpdate;
	}

	public void setSnpStateUpdate(boolean snpStateUpdate) {
		this.snpStateUpdate = snpStateUpdate;
	}

	public boolean isPresenceUpdate() {
		return presenceUpdate;
	}

	public void setPresenceUpdate(boolean presenceUpdate) {
		this.presenceUpdate = presenceUpdate;
	}

	public boolean isPresenceEnable() {
		return presenceEnable;
	}

	public void setPresenceEnable(boolean presenceEnable) {
		this.presenceEnable = presenceEnable;
	}

	public boolean isUpdateBarracudaServer() {
		return updateBarracudaServer;
	}

	public void setUpdateBarracudaServer(boolean updateBarracudaServer) {
		this.updateBarracudaServer = updateBarracudaServer;
	}

	public String getHideBarracudaServer() {
		return hideBarracudaServer;
	}

	public void setHideBarracudaServer(String hideBarracudaServer) {
		this.hideBarracudaServer = hideBarracudaServer;
	}

	public String getAuthorizationKey() {
		return authorizationKey;
	}

	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

	public String getServiceHost() {
		return serviceHost;
	}

	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public String getWindowsDomain() {
		return windowsDomain;
	}

	public void setWindowsDomain(String windowsDomain) {
		this.windowsDomain = windowsDomain;
	}

	public boolean isUpdateWebSenseServer() {
		return updateWebSenseServer;
	}

	public void setUpdateWebSenseServer(boolean updateWebSenseServer) {
		this.updateWebSenseServer = updateWebSenseServer;
	}

	public String getHideWebSenseServer() {
		return hideWebSenseServer;
	}

	public void setHideWebSenseServer(String hideWebSenseServer) {
		this.hideWebSenseServer = hideWebSenseServer;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getBarracudaDefaultUserName() {
		return barracudaDefaultUserName;
	}

	public void setBarracudaDefaultUserName(String barracudaDefaultUserName) {
		this.barracudaDefaultUserName = barracudaDefaultUserName;
	}

	public String getAuthorizationKeyText() {
		return authorizationKeyText;
	}

	public void setAuthorizationKeyText(String authorizationKeyText) {
		this.authorizationKeyText = authorizationKeyText;
	}

	public boolean isChkAuthorizationKey() {
		return chkAuthorizationKey;
	}

	public void setChkAuthorizationKey(boolean chkAuthorizationKey) {
		this.chkAuthorizationKey = chkAuthorizationKey;
	}

	public String getWebSenseServiceHost() {
		return webSenseServiceHost;
	}

	public void setWebSenseServiceHost(String webSenseServiceHost) {
		this.webSenseServiceHost = webSenseServiceHost;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public short getWensenseMode() {
		return wensenseMode;
	}

	public void setWensenseMode(short wensenseMode) {
		this.wensenseMode = wensenseMode;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

    public String getWebSenseDefaultUserName() {
		return webSenseDefaultUserName;
	}

	public void setWebSenseDefaultUserName(String webSenseDefaultUserName) {
		this.webSenseDefaultUserName = webSenseDefaultUserName;
	}
	
	public String getSecurityKeyText() {
		return securityKeyText;
	}

	public void setSecurityKeyText(String securityKeyText) {
		this.securityKeyText = securityKeyText;
	}

	public boolean isChkSecurityKey() {
		return chkSecurityKey;
	}

	public void setChkSecurityKey(boolean chkSecurityKey) {
		this.chkSecurityKey = chkSecurityKey;
	}

	public boolean isEnableWebsense() {
		return enableWebsense;
	}

	public void setEnableWebsense(boolean enableWebsense) {
		this.enableWebsense = enableWebsense;
	}

	public boolean isEnableBarracuda() {
		return enableBarracuda;
	}

	public void setEnableBarracuda(boolean enableBarracuda) {
		this.enableBarracuda = enableBarracuda;
	}

	public List<CheckItem> getWebsenseWhitelists() {
		List<CheckItem> websenseWhitelists = getBoCheckItems("objName", DomainObject.class,
				new FilterParams("autoGenerateFlag = :s1 and objType = :s2", new Object[]{false,DomainObject.WEB_SECURITY} ) , CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);

		websenseWhitelists.add(0,new CheckItem((long) -1, ""));

		return websenseWhitelists;
	}

	public void setWebsenseWhitelists(List<CheckItem> websenseWhitelists) {
		this.websenseWhitelists = websenseWhitelists;
	}

	public Long getWebsenseWhitelist() {
		return websenseWhitelist;
	}

	public void setWebsenseWhitelist(Long websenseWhitelist) {
		this.websenseWhitelist = websenseWhitelist;
	}

	public List<CheckItem> getBarracudaWhitelists() {
		List<CheckItem> barracudaWhitelists = getBoCheckItems("objName", DomainObject.class,
				new FilterParams("autoGenerateFlag = :s1 and objType = :s2", new Object[]{false,DomainObject.WEB_SECURITY} ), CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);

		barracudaWhitelists.add(0,new CheckItem((long) -1, ""));
		return barracudaWhitelists;
	}

	public void setBarracudaWhitelists(List<CheckItem> barracudaWhitelists) {
		this.barracudaWhitelists = barracudaWhitelists;
	}

	public Long getBarracudaWhitelist() {
		return barracudaWhitelist;
	}

	public void setBarracudaWhitelist(Long barracudaWhitelist) {
		this.barracudaWhitelist = barracudaWhitelist;
	}

	public Long getWhiteListId() {
		return whiteListId;
	}

	public void setWhiteListId(Long whiteListId) {
		this.whiteListId = whiteListId;
	}

	public EnumItem[] getHostedMode() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.WEBSENSEMODE_HOSTED,
				getText("admin.management.webSecurity.websense.hosted")) };
	}

	public EnumItem[] getHybridMode() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.WEBSENSEMODE_HYBRID,
				getText("admin.management.webSecurity.websense.hybrid")) };
	}

	public String getWensenseNoteStyle(){
		if (getWensenseMode() == 0) {
			return "";
		} else {
			return "none";
		}
	}

	public void setEnableTVProxy(boolean enableTVProxy) {
		this.enableTVProxy = enableTVProxy;
	}

	public boolean isEnableTVProxy() {
		return enableTVProxy;
	}

	public void setTvProxyIP(String tvProxyIP) {
		this.tvProxyIP = tvProxyIP;
	}

	public String getTvProxyIP() {
		return tvProxyIP;
	}

	public void setTvProxyPort(int tvProxyPort) {
		this.tvProxyPort = tvProxyPort;
	}

	public int getTvProxyPort() {
		return tvProxyPort;
	}

	public void setTvAutoProxyFile(String tvAutoProxyFile) {
		this.tvAutoProxyFile = tvAutoProxyFile;
	}

	public String getTvAutoProxyFile() {
		return tvAutoProxyFile;
	}
    
    public String getIdmUserEmail() {
        return idmUserEmail;
    }

    public void setIdmUserEmail(String idmUserEmail) {
        this.idmUserEmail = idmUserEmail;
    }
	
	public boolean isEnabledProxy() {
        return enabledProxy;
    }

    public void setEnabledProxy(boolean enabledProxy) {
        this.enabledProxy = enabledProxy;
    }

    public boolean isEnableProxyIdm() {
        return enableProxyIdm;
    }

    public void setEnableProxyIdm(boolean enableProxyIdm) {
        this.enableProxyIdm = enableProxyIdm;
    }

    public boolean isUpdateIDM() {
        return updateIDM;
    }

    public void setUpdateIDM(boolean updateIDM) {
        this.updateIDM = updateIDM;
    }



    //--------------------------OpenDNS Feature Logic : Glasgow_16 Start--------------------------//
	private boolean updateOpenDNSServer;
	private String	hideOpenDNSServer = "none";
	private boolean enableOpenDNS;
	private String openDNSUserName;
	private String openDNSCustomerName;
	private String openDNSPassword;
	private String openDNSPasswordText;
	private String openDNSServer1IP = OpenDNSAccount.OPENDNS_SERVER_1;
	private String openDNSServer2IP = OpenDNSAccount.OPENDNS_SERVER_2;
	private boolean chkOpenDNSPassword;
	private String openDNSDeviceLabel;
	private String[] openDNSDevices;
	private String[] openDNSMappingIds;
	private Long selDeviceId;
	private Long selUserProfileId;
	private boolean isUserSwitched = false;
	private OpenDNSAccount activeAccount = null;
	private OpenDNSDevice defaultDevice = null;
	private String activeUserName;

	private void initOpenDNServerSettings(HMServicesSettings bo)
			throws Exception {
		this.updateOpenDNSServer = false;
		this.hideOpenDNSServer = "none";
		if (bo == null) {
			log.debug("initOpenDNServerSettings", "No settings bo in db!");
			this.enableOpenDNS = false;
			return;
		}

		activeAccount = getHMSettings().getOpenDNSAccount();
		if (activeAccount != null) {
			this.openDNSUserName = activeAccount.getUserName();
			this.openDNSPassword = activeAccount.getPassword();
			this.openDNSServer1IP = activeAccount.getDnsServer1();
			this.openDNSServer2IP = activeAccount.getDnsServer2();

			defaultDevice = getDefaultDevice();
			initOpenDNSMappings(defaultDevice, activeAccount);
		} else {
			this.openDNSPassword = "";
			this.openDNSUserName = "";
			this.openDNSCustomerName = "";
			this.openDNSServer1IP = OpenDNSAccount.OPENDNS_SERVER_1;
			this.openDNSServer2IP = OpenDNSAccount.OPENDNS_SERVER_2;
		}

		this.enableOpenDNS = bo.isEnableOpenDNS();

	}
	
	private boolean updateOpenDNSServerSettings() throws Exception {
		HMServicesSettings bo = getHMSettings();
		boolean isUpdate = true;

		if (this.enableOpenDNS) {
			OpenDNSModel loginResult = OpenDNSService.login(openDNSUserName,
					chkOpenDNSPassword ? openDNSPassword : openDNSPasswordText);
			if (loginResult.isSuccessFlag()) {
				try {
					// Check whether the OpenDNS Account is switched or not.
					OpenDNSAccount currentAccount = bo.getOpenDNSAccount();
					if (currentAccount != null) {
						isUserSwitched = currentAccount.getUserName().equalsIgnoreCase(openDNSUserName.trim()) ? false : true;
					}

					// If OpenDNS Account Create / Update failed
					Long activeAccountId = upsertOpenDNSAccountSettings(loginResult.getToken());
					if (activeAccountId == -1L)
						return false;

					// Update the HmServiceSettings with the active account.
					activeAccount = QueryUtil.findBoById(OpenDNSAccount.class, activeAccountId);

					bo.setOpenDNSAccount(activeAccount);

					// Create Default OpenDNS Device Settings
					updateOpenDNSDefDevSttings();
					updateOpenDNSMapping();
				} catch (Exception ex) {
					if (ex instanceof OpenDNSException) {
						throw (OpenDNSException) ex;
					} else {
						return false;
					}
				}
			} else {
				throw new OpenDNSException(
						MgrUtil.getUserMessage(
								"glasgow_16.home.hmSettings.update.openDNS.login.failed",
								loginResult.getError_message()));
			}
		}

		bo.setEnableOpenDNS(this.enableOpenDNS);

		try {
			if (isUpdate) {
				bo = QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}
			return true;
		} catch (Exception e) {
			log.error("updateOpenDNSServerSettings",
					"OpenDNS server settings catch exception!", e);
			return false;
		}
	}
	
	private Long upsertOpenDNSAccountSettings(String token){		
		OpenDNSAccount account = QueryUtil.findBoByAttribute(OpenDNSAccount.class, "userName", openDNSUserName.trim(), getDomainId());
		if(account != null){
			account.setPassword(chkOpenDNSPassword ? openDNSPassword : openDNSPasswordText);
			account.setDnsServer1(openDNSServer1IP);
			account.setDnsServer2(openDNSServer2IP);
			account.setToken(token);
			try{
				return QueryUtil.updateBo(account).getId();
			}catch(Exception ex){
				log.error("upsertOpenDNSAccountSettings", "Update OpenDNS Account catch exception!", ex);
				return -1L;
			}
		}else{
			OpenDNSAccount newAccount = new OpenDNSAccount();
			newAccount.setPassword(chkOpenDNSPassword ? openDNSPassword : openDNSPasswordText);
			newAccount.setUserName(openDNSUserName);
			newAccount.setDnsServer1(openDNSServer1IP);
			newAccount.setDnsServer2(openDNSServer2IP);
			newAccount.setToken(token);
			newAccount.setOwner(getDomain());
			try{
				return QueryUtil.createBo(newAccount);
			}catch(Exception ex){
				log.error("upsertOpenDNSAccountSettings", "Create OpenDNS Account catch exception!", ex);
				return -1L;
			}
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @description Create the deault OpenDNS Device if not exists, update it if exists.
	 * @param activeAccount
	 * @param token
	 * @throws OpenDNSException
	 */
	private void updateOpenDNSDefDevSttings() throws OpenDNSException{		
		List<OpenDNSDevice> defaultDevices = QueryUtil.executeQuery(OpenDNSDevice.class, null, new FilterParams("openDNSAccount.id=:s1 and defaultDevice=:s2", new Object[]{activeAccount.getId(), true}), domainId);
		String deviceLabel = getDefaultDeviceLabel();
		String token = activeAccount.getToken();
		if(defaultDevices == null || defaultDevices.size() == 0){
			String devicekey = deviceLabel + "-" + java.util.UUID.randomUUID();
			try{
				OpenDNSModel createResult = OpenDNSService.createDevice(token, devicekey, deviceLabel);
				if(createResult.isSuccessFlag()){
					OpenDNSDevice newDevice = new OpenDNSDevice();
					String deviceId = createResult.getDeviceId();
					newDevice.setDefaultDevice(true);
					newDevice.setDeviceId(deviceId);
					newDevice.setDeviceKey(devicekey);
					newDevice.setDeviceLabel(deviceLabel);
					newDevice.setOpenDNSAccount(activeAccount);
					newDevice.setOwner(getDomain());
					
					QueryUtil.createBo(newDevice);
				}else{
					throw new OpenDNSException(createResult.getError_message());
				}
			}catch(Exception ex){
				log.error("openDNSDefDevCreate", "OpenDNS Create Device catch exception!", ex);
				if(ex instanceof OpenDNSException){
					throw (OpenDNSException)ex;
				}
			}
		}else{
			OpenDNSDevice defaultDevice = defaultDevices.get(0);
			try{
				if(isUserSwitched){	
					defaultDevice.setOpenDNSAccount(activeAccount);
					QueryUtil.updateBo(defaultDevice);
				}						
			}catch(Exception ex){
				log.error("openDNSDefDevCreate", "OpenDNS Update Device catch exception!", ex);
			}
							
		}
	}
	
	private void checkActiveUserName() throws Exception {
		boolean activeFlag = false;
		jsonObject = new JSONObject();
		HMServicesSettings settings = getHMSettings();
		OpenDNSAccount activeAccount = settings.getOpenDNSAccount();

		if (activeAccount != null
				&& !activeAccount.getUserName().equals(activeUserName)) {
			activeFlag = true;
		}
		jsonObject.put("activeFlag", activeFlag);
	}
	
	private void refreshOpenDNSDevice() throws Exception{
		jsonObject = new JSONObject();
		HMServicesSettings settings = getHMSettings();
		OpenDNSAccount activeAccount = settings.getOpenDNSAccount();
		boolean isUpdated = false;
		String msg = "";
		if(settings.isEnableOpenDNS() && activeAccount != null){
			List<OpenDNSDevice> devices = QueryUtil.executeQuery(OpenDNSDevice.class, null, new FilterParams("openDNSAccount.id", activeAccount.getId()), getDomainId());		
			List<OpenDNSDevice> removedDevices = new ArrayList<OpenDNSDevice>();
			OpenDNSDevice defaultDevice = null;
			boolean isDefaultRemoved = false;
			for(OpenDNSDevice device : devices){
				try{
					OpenDNSModel model = OpenDNSService.fetchDevice(activeAccount.getToken(), device.getDeviceKey());
					if(device.isDefaultDevice()){
						defaultDevice = device;
					}
					if(OpenDNSModel.ERROR_CODE_DEVICE_NOT_EXISTS == model.getError_code()){	
						if(device.isDefaultDevice()){
							isDefaultRemoved = true;
						}
						removedDevices.add(device);		
					}
				}catch(Exception ex){
					log.error("OpenDNSCleaner", "Got error when remove the OpenDNSDeviceMapping and OpenDNSDevice", ex);
				}	
			}		
			
			if(removedDevices.size() != 0){
				isUpdated = true;
				List<OpenDNSMapping> updatedMappings = new ArrayList<OpenDNSMapping>();
				JSONArray mappingObjs = new JSONArray();
				JSONArray deviceObjs = new JSONArray();
				List<Long> removeIds = new ArrayList<Long>();
				for(OpenDNSDevice removedDevice : removedDevices){
					removeIds.add(removedDevice.getId());
					List<OpenDNSMapping> mappings = QueryUtil.executeQuery(OpenDNSMapping.class, null, new FilterParams("openDNSDevice.id", removedDevice.getId()));
					for(OpenDNSMapping mapping : mappings){
						JSONObject obj = new JSONObject();
						if(isDefaultRemoved){
							mapping.setOpenDNSDevice(null);
							obj.put("deviceid", -1L);	
							obj.put("did", "");
						}else{
							mapping.setOpenDNSDevice(defaultDevice);
							obj.put("deviceid", defaultDevice.getId());		
							obj.put("did", defaultDevice.getDeviceId());
						}	
						updatedMappings.add(mapping);
						obj.put("profileid", mapping.getId());
						mappingObjs.put(obj);
					}
					JSONObject deviceObj = new JSONObject();
					deviceObj.put("deviceid", removedDevice.getId());
					deviceObjs.put(deviceObj);
				}
				
				if(updatedMappings.size() != 0){
					QueryUtil.bulkUpdateBos(updatedMappings);
				}
				
				removeBos(OpenDNSDevice.class, removeIds);
			
				msg = MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.sync.info", new String[]{String.valueOf(removedDevices.size()), String.valueOf(updatedMappings.size())});
				jsonObject.put("userprofiles", mappingObjs);
				jsonObject.put("devices", deviceObjs);
			}else{
				isUpdated = false;
				msg = MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.sync.noupdated");
			}
		}
		
		jsonObject.put("noupdate", isUpdated);
		jsonObject.put("msg", msg);
	}
	
	private String getDefaultDeviceLabel(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("AH").append("-");
		buffer.append(getDomain().getDomainName()).append("-");
		buffer.append("Default-DeviceSettings");
		
		return buffer.toString();
	}
	
	//For the first time of OpenDNS account init, create all mappings by the User Profile with the default device settings.
	private void initOpenDNSMappings(OpenDNSDevice defaultDevice,
			OpenDNSAccount activeAccount) throws Exception {
		List<UserProfile> profiles = QueryUtil.executeQuery(UserProfile.class,
				null, null, getDomainId());
		for (UserProfile profile : profiles) {
			List<OpenDNSMapping> mappings = QueryUtil.executeQuery(
					OpenDNSMapping.class,
					null,
					new FilterParams(
							"userProfileId=:s1 and openDNSAccount.id=:s2",
							new Object[] { profile.getId(),
									activeAccount.getId() }));
			if (mappings.isEmpty()) {
				OpenDNSMapping mapping = new OpenDNSMapping();
				mapping.setOpenDNSAccount(activeAccount);
				mapping.setOwner(getDomain());
				mapping.setOpenDNSDevice(defaultDevice);
				mapping.setUserProfile(profile);
				QueryUtil.createBo(mapping);
			}
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @descirption Update the opendns mappings from the UI.
	 * @throws Exception
	 */
	private void updateOpenDNSMapping() throws Exception {	
		if(openDNSMappingIds == null || openDNSMappingIds.length == 0 || isUserSwitched){
			return;
		}
		
		List<OpenDNSMapping> updateMappings = new ArrayList<OpenDNSMapping>();
		OpenDNSMapping mapping = null;
		OpenDNSDevice device = null;
		for(int i = 0; i < openDNSMappingIds.length; i++){
			Long mappingId = Long.valueOf(openDNSMappingIds[i]);
			Long deviceId = Long.valueOf(openDNSDevices[i]);
			mapping = QueryUtil.findBoById(OpenDNSMapping.class, mappingId);
			if(deviceId != -1){
				device = QueryUtil.findBoById(OpenDNSDevice.class, deviceId);	
			}else{
				device = null;
			}
			
			if(mapping == null){
				continue;
			}
			mapping.setOpenDNSDevice(device);
			updateMappings.add(mapping);			
		}
		if(updateMappings.size() > 0){
			QueryUtil.bulkUpdateBos(updateMappings);
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @description create the OpenDNS device. For the UI.
	 * @return
	 * @throws Exception
	 */
	private JSONObject createOpenDNSDevice() throws Exception{
		OpenDNSAccount activeAccount = getHMSettings().getOpenDNSAccount();
		JSONObject result = new JSONObject();
		String msg = "";		
		if(activeAccount == null){
			msg = MgrUtil.getUserMessage("glasgow_16.error.home.hmservice.opendns.account.notsave");
			result.put("isSuccess", false);
			result.put("msg", msg);
			return result;
		}
		
		List<OpenDNSDevice> devices = QueryUtil.executeQuery(OpenDNSDevice.class, null, new FilterParams("openDNSAccount.id=:s1 and lower(deviceLabel)=:s2", new Object[]{activeAccount.getId(), openDNSDeviceLabel.toLowerCase()}));
		if(devices != null &&  !devices.isEmpty()){
			OpenDNSDevice device = devices.get(0);
			result.put("isSuccess", true);
			result.put("msg", MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.device.get.success"));
			result.put("deviceKey", device.getDeviceKey());
			result.put("deviceLabel", openDNSDeviceLabel);
			result.put("deviceId", device.getDeviceId());
			result.put("isCreated", false);
			return result;
		}
		
		String deviceKey = openDNSDeviceLabel + "-" + java.util.UUID.randomUUID();
		OpenDNSModel deviceResult = OpenDNSService.createDevice(activeAccount.getToken(), deviceKey, openDNSDeviceLabel);
		if(deviceResult.isSuccessFlag()){		
			OpenDNSDevice device = new OpenDNSDevice();
			device.setDeviceId(deviceResult.getDeviceId());
			device.setDeviceKey(deviceKey);
			device.setDeviceLabel(openDNSDeviceLabel);
			device.setOpenDNSAccount(activeAccount);
			device.setOwner(getDomain());
			Long id = QueryUtil.createBo(device);
			result.put("isSuccess", true);
			result.put("msg", MgrUtil.getUserMessage("glasgow_16.home.hmSettings.openDNS.device.create.success"));
			result.put("id", id);
			result.put("deviceKey", deviceKey);
			result.put("deviceLabel", openDNSDeviceLabel);
			result.put("deviceId", deviceResult.getDeviceId());
			result.put("isCreated", true);
		}else{
			result.put("isSuccess", false);
			result.put("msg", MgrUtil.getUserMessage("glasgow_16.errir.home.hmSettings.openDNS.device.create.fail", deviceResult.getError_message()));
		}
		return result;
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @description Get the device id, used by UI when need to fresh the device id.
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDeviceId() throws Exception{
		JSONObject obj = new JSONObject();
		if(selDeviceId == -1){
			obj.put("isSuccess", true);
			obj.put("deviceId", "");
			obj.put("upId", selUserProfileId);
			return obj;
		}
		
		OpenDNSDevice device = QueryUtil.findBoById(OpenDNSDevice.class, selDeviceId);
		if(device == null){
			obj.put("isSuccess", false);
		}else{
			obj.put("isSuccess", true);
			obj.put("deviceId", device.getDeviceId());
			obj.put("upId", selUserProfileId);
		}
		
		return obj;
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @description return all the Active Account related Devices.
	 */
	public List<CheckItem> getAllOpenDNSDevices(){
		HMServicesSettings settings = getHMSettings();
		List<CheckItem> openDNSDevices = this.getBoCheckItems("deviceLabel", OpenDNSDevice.class, new FilterParams("openDNSAccount.id", settings.getOpenDNSAccount().getId()));
		CheckItem disableItem = new CheckItem(-1L, "[-Disabled-]");
		List<CheckItem> allItems =  openDNSDevices.isEmpty() ? new ArrayList<CheckItem>() : openDNSDevices;
		allItems.add(0, disableItem);
		return allItems;
	}	
	
	private HMServicesSettings getHMSettings(){
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, null, getDomainId(), this);
		if(list == null || list.size() ==0){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	private OpenDNSDevice getDefaultDevice(){
		if(defaultDevice == null){
			List<OpenDNSDevice> defaultDevices = QueryUtil.executeQuery(
					OpenDNSDevice.class, null, new FilterParams(
							"openDNSAccount.id=:s1 and defaultDevice=:s2",
							new Object[] { activeAccount.getId(), true }));
			if(defaultDevices.isEmpty()){
				return null;
			}
			
			return defaultDevices.get(0);
		}
		
		return defaultDevice;
	}

	public void setDefaultDevice(OpenDNSDevice defaultDevice) {
		this.defaultDevice = defaultDevice;
	}

	public List<OpenDNSMapping> getOpenDNSMappings() {
		OpenDNSAccount activeAccount = getHMSettings().getOpenDNSAccount();
		if(activeAccount == null)
			return null;
		
		return QueryUtil.executeQuery(
				OpenDNSMapping.class, new SortParams("id") , new FilterParams(
						"openDNSAccount.id", activeAccount.getId()), getDomainId(), this);
	}

	public String[] getOpenDNSDevices() {
		return openDNSDevices;
	}

	public void setOpenDNSDevices(String[] openDNSDevices) {
		this.openDNSDevices = openDNSDevices;
	}
	
	public boolean isUpdateOpenDNSServer() {
		return updateOpenDNSServer;
	}

	public String getOpenDNSDeviceLabel() {
		return openDNSDeviceLabel;
	}

	public void setOpenDNSDeviceLabel(String openDNSDeviceLabel) {
		this.openDNSDeviceLabel = openDNSDeviceLabel;
	}

	public boolean isChkOpenDNSPassword() {
		return chkOpenDNSPassword;
	}

	public void setChkOpenDNSPassword(boolean chkOpenDNSPassword) {
		this.chkOpenDNSPassword = chkOpenDNSPassword;
	}

	public boolean isEnableOpenDNS() {
		return enableOpenDNS;
	}

	public void setEnableOpenDNS(boolean enableOpenDNS) {
		this.enableOpenDNS = enableOpenDNS;
	}

	public void setUpdateOpenDNSServer(boolean updateOpenDNSServer) {
		this.updateOpenDNSServer = updateOpenDNSServer;
	}

	public String getHideOpenDNSServer() {
		return hideOpenDNSServer;
	}

	public void setHideOpenDNSServer(String hideOpenDNSServer) {
		this.hideOpenDNSServer = hideOpenDNSServer;
	}

	public String getOpenDNSUserName() {
		return openDNSUserName;
	}

	public void setOpenDNSUserName(String openDNSUserName) {
		this.openDNSUserName = openDNSUserName;
	}

	public String getOpenDNSCustomerName() {
		return openDNSCustomerName;
	}

	public void setOpenDNSCustomerName(String openDNSCustomerName) {
		this.openDNSCustomerName = openDNSCustomerName;
	}

	public String getOpenDNSPassword() {
		return openDNSPassword;
	}

	public void setOpenDNSPassword(String openDNSPassword) {
		this.openDNSPassword = openDNSPassword;
	}

	public String getOpenDNSPasswordText() {
		return openDNSPasswordText;
	}

	public void setOpenDNSPasswordText(String openDNSPasswordText) {
		this.openDNSPasswordText = openDNSPasswordText;
	}

	public String getOpenDNSServer1IP() {
		return openDNSServer1IP;
	}

	public void setOpenDNSServer1IP(String openDNSServer1IP) {
		this.openDNSServer1IP = openDNSServer1IP;
	}

	public String getOpenDNSServer2IP() {
		return openDNSServer2IP;
	}

	public void setOpenDNSServer2IP(String openDNSServer2IP) {
		this.openDNSServer2IP = openDNSServer2IP;
	}

	public String[] getOpenDNSMappingIds() {
		return openDNSMappingIds;
	}

	public void setOpenDNSMappingIds(String[] openDNSMappingIds) {
		this.openDNSMappingIds = openDNSMappingIds;
	}

	public Long getSelDeviceId() {
		return selDeviceId;
	}

	public void setSelDeviceId(Long selDeviceId) {
		this.selDeviceId = selDeviceId;
	}

	public Long getSelUserProfileId() {
		return selUserProfileId;
	}

	public void setSelUserProfileId(Long selUserProfileId) {
		this.selUserProfileId = selUserProfileId;
	}

	public boolean isUserSwitched() {
		return isUserSwitched;
	}

	public void setUserSwitched(boolean isUserSwitched) {
		this.isUserSwitched = isUserSwitched;
	}

	public String getActiveUserName() {
		return activeUserName;
	}

	public void setActiveUserName(String activeUserName) {
		this.activeUserName = activeUserName;
	}

	//--------------------------OpenDNS Feature Logic  End--------------------------//
	
	//-----------------------Social Analytics ::start:: ------------------------//
	private boolean updateGuestAnalytics;
	private boolean enabledGuestAnanlytics;
	private boolean resetSSIDs;
	
    private void initGuestAnalytics() {
        GuestAnalyticsInfo info = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.id", getDomain().getId());
        if(null != info) {
            setEnabledGuestAnanlytics(info.isEnabled());
        }
    }
    
    private boolean toggleGuestAnalytics() {
        try{
            clearActionErrors();
            
            final String instanceId = getDomain().getInstanceId();
            String customerId = LicenseOperationTool.getCustomerIdFromRemote(instanceId);
            GuestAnalyticsRequestResponse responseEntity = PortalResUtils.getInstance().toggleGuestAnalytics(instanceId, customerId, enabledGuestAnanlytics);
            
            if(responseEntity.getStatus() == 200) {
                GuestAnalyticsInfo info = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.id", getDomain().getId());
                if(null == info) {
                    info = new GuestAnalyticsInfo(enabledGuestAnanlytics, responseEntity.getApiKey(), responseEntity.getApiNonce(), getDomain());
                    QueryUtil.createBo(info);
                } else {
                    info.setEnabled(enabledGuestAnanlytics);
                    info.setApiKey(responseEntity.getApiKey());
                    info.setApiNonce(responseEntity.getApiNonce());
                    QueryUtil.updateBo(info);
                }
                addActionMessage(MgrUtil
                        .getUserMessage(enabledGuestAnanlytics ? "home.services.guestanalytics.enable.succ"
                                : "home.services.guestanalytics.disable.succ"));
                
                if(!enabledGuestAnanlytics && resetSSIDs) {
                    if (NmsUtil.isHostedHMApplication()) {
                        String setClause = "enabledSocialLogin = :s1";
                        String whereClause = "accessMode = :s2 and enabledSocialLogin = :s3";
                        Object[] bindings = new Object[]{false, SsidProfile.ACCESS_MODE_OPEN, true};
                        QueryUtil.updateBos(SsidProfile.class, setClause , whereClause , bindings , getDomain().getId());
                    } else {
                        String setClause = "enabledSocialLogin = :s1";
                        String whereClause = "accessMode = :s2 and enabledSocialLogin = :s3";
                        Object[] bindings = new Object[]{false, SsidProfile.ACCESS_MODE_OPEN, true};
                        QueryUtil.updateBos(SsidProfile.class, setClause , whereClause , bindings);
                    }
                }
                
                setSelectedL2Feature(L2_FEATURE_HM_SERVICES);
                return true;
            } else {
                log.error("toggleGuestAnalytics", "Fail to enable/disable GA, " + responseEntity.getMessage());
                addActionError(responseEntity.getMessage());
            }
        }catch(Exception e){
            log.error("toggleGuestAnalytics", e);
            addActionError(MgrUtil.getUserMessage(enabledGuestAnanlytics ? "home.services.guestanalytics.enable.fail"
                            : "home.services.guestanalytics.disable.fail") + (e.getMessage().contains("MIME media type application/octet-stream was not found") ? "" : " "+e.getMessage()));
        }
        setSelectedL2Feature(L2_FEATURE_HM_SERVICES);
        return false;
    }
    
    private String checkGAService() throws JSONException {
        jsonObject = new JSONObject();
        try {
            final String instanceId = getDomain().getInstanceId();
            String customerId = LicenseOperationTool.getCustomerIdFromRemote(instanceId);
            final String healthCheckURL = new GAConfigHepler(getDomain().getId()).getHealthCheckURL();
            CheckGAServiceResponse resonpse = GuestAnalyticsResUtils.getInstance()
                    .checkGAService(healthCheckURL, customerId, instanceId);
            if(resonpse.getServiceStuts() == 0
                    && StringUtils.isNotBlank(resonpse.getApiKey())
                    && StringUtils.isNotBlank(resonpse.getApiNonce())) {
                GuestAnalyticsInfo info = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.id", getDomain().getId());
                if(null != info) {
                    info.setApiKey(resonpse.getApiKey());
                    info.setApiNonce(resonpse.getApiNonce());
                    QueryUtil.updateBo(info);
                }
                jsonObject.put("succ", true);
                jsonObject.put("msg", MgrUtil.getUserMessage("home.services.guestanalytics.check.service.succ"));
            } else {
                jsonObject.put("msg", MgrUtil.getUserMessage("home.services.guestanalytics.check.service.fail.code", 
                        MgrUtil.getEnumString("enum.ga.check.service.response."+resonpse.getServiceStuts())));
            }
        } catch (Exception e) {
            log.error("checkGAService", e);
            jsonObject.put("msg", MgrUtil.getUserMessage("home.services.guestanalytics.check.service.fail"));
        }
        
        return "json";
    }
    
    public boolean isUpdateGuestAnalytics() {
        return updateGuestAnalytics;
    }

    public boolean isEnabledGuestAnanlytics() {
        return enabledGuestAnanlytics;
    }

    public void setUpdateGuestAnalytics(boolean updateGuestAnalytics) {
        this.updateGuestAnalytics = updateGuestAnalytics;
    }

    public void setEnabledGuestAnanlytics(boolean enabledGuestAnanlytics) {
        this.enabledGuestAnanlytics = enabledGuestAnanlytics;
    }

    public boolean isResetSSIDs() {
        return resetSSIDs;
    }

    public void setResetSSIDs(boolean resetSSIDs) {
        this.resetSSIDs = resetSSIDs;
    }
    
    //-----------------------Social Analytics ::end:: ------------------------//
}

class HmSubnet implements HmBo {

	private static final long	serialVersionUID	= 1L;

	private Long	id;

	private String	ip;

	private String	mask;

	private boolean	selected;

	public HmSubnet() {

	}

	public HmSubnet(Long id, String ip, String mask) {
		this.id = id;
		this.ip = ip;
		this.mask = mask;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return ip + "-" + mask;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		//
	}

	public void setOwner(String owner) {
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HmSubnet))
			return false;

		final HmSubnet subnet = (HmSubnet) o;

		if (ip != null ? !ip.equals(subnet.ip) : subnet.ip != null)
			return false;
		if (mask != null ? !mask.equals(subnet.mask) : subnet.mask != null)
			return false;

		return true;
	}

	
}
