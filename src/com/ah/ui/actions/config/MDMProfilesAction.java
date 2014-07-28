package com.ah.ui.actions.config;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.util.encoders.Base64;
import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.mdm.core.profile.entity.ApnProfileInfo;
import com.ah.mdm.core.profile.entity.CalDavProfileInfo;
import com.ah.mdm.core.profile.entity.CalendarSubscriptionProfileInfo;
import com.ah.mdm.core.profile.entity.CardDavProfileInfo;
import com.ah.mdm.core.profile.entity.ConfigurationProfileInfo;
import com.ah.mdm.core.profile.entity.CredentialsProfileInfo;
import com.ah.mdm.core.profile.entity.EmailProfileInfo;
import com.ah.mdm.core.profile.entity.ExchangeProfileInfo;
import com.ah.mdm.core.profile.entity.LdapProfileInfo;
import com.ah.mdm.core.profile.entity.MdmObject;
import com.ah.mdm.core.profile.entity.PasscodeProfileInfo;
import com.ah.mdm.core.profile.entity.RemovalPasscodeProfileInfo;
import com.ah.mdm.core.profile.entity.RestrictionsProfileInfo;
import com.ah.mdm.core.profile.entity.ScepProfileInfo;
import com.ah.mdm.core.profile.entity.ValidTimeInfo;
import com.ah.mdm.core.profile.entity.VpnProfileInfo;
import com.ah.mdm.core.profile.entity.WebClipProfileInfo;
import com.ah.mdm.core.profile.entity.WifiProfileInfo;
import com.ah.mdm.core.profile.impl.ProfileMgrServiceImpl;
import com.ah.mdm.core.profile.utils.CertificateParser;
import com.ah.mdm.core.profile.utils.DictItem;
import com.ah.mdm.core.profile.utils.ImageUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class MDMProfilesAction extends BaseAction implements QueryBo{
	private Tracer logger = new Tracer(MDMProfilesAction.class.getSimpleName());
	private static final long serialVersionUID = 1L;
	
	private static final String MDMPROFILES_DIALOG_MODE = "mdmProfilesDlg";
    private static final String MDMPROFILES_JSON_MODE = "mdmProfilesJson";
    private ProfileMgrServiceImpl impl =new ProfileMgrServiceImpl();
    private static final String MDMPROFILE_SECURITY_VALUE_NEVER = "Never";
    private static final String MDMPROFILE_SECURITY_VALUE_AUTHENTIFICATION = "WithAuthentication";
    private static final String MDMPROFILE_SECURITY_VALUE_ALWAYS = "Always";
        
	public String execute() throws Exception {
		String fw = globalForward();
		setContentRatingForRes();
		if (fw != null) {
			return fw;
		}
		
		try {
            if ("new".equals(operation)) {
            	if(!setTitleAndCheckAccess(getText("config.title.mdmProfiles.new"))){
            		return getLstForward();
            	}            	      	
            	setSessionDataSource(new MdmProfiles());
            	validTimeInfo = new ValidTimeInfo();
            	this.setValidTimeDataCron();
            	setProfileToUI(new ConfigurationProfileInfo());            	
            	return getReturnPathWithJsonMode(INPUT,MDMPROFILES_JSON_MODE,MDMPROFILES_DIALOG_MODE);            	
			} else if ("create".equals(operation) 
			        || ("create" + getLstForward()).equals(operation)) {	
				
				if(checkNameExists("mdmProfilesName", displayName)) {
				    if(isJsonMode() && isContentShownInSubDrawer()) {
				        return MDMPROFILES_JSON_MODE;
				    } else if(isJsonMode() && !isParentIframeOpenFlg()) {
				    	jsonObject = new JSONObject();
				        jsonObject.put("resultStatus", false);
				        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", displayName));
				        return "json";
				    } else {
				        return getReturnPathWithJsonMode(INPUT, MDMPROFILES_DIALOG_MODE);
				    }
				}
				
				ConfigurationProfileInfo profile = new ConfigurationProfileInfo();
				profile = this.getProfileFromUI();
				if(this.isEmptyProfile(profile)){
					addActionError(MgrUtil.getUserMessage("error.operation.empty"));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.operation.empty"));
						return "json";
					}
					return isJsonMode() ? MDMPROFILES_JSON_MODE : INPUT ;
				}
				
				this.getValidTimeDataCron();
								
				//use rest api:add mdmProfiles
				if(!impl.setMdmProfile(validTimeInfo,profile, this.getUserContext().getOwner().getInstanceId())){
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.operation.fail"));
						return "json";
					}
					return isJsonMode() ? MDMPROFILES_JSON_MODE : INPUT ;
				}
				
				getDataSource().setMdmProfilesName(displayName);
				getDataSource().setUserProfileAttributeValue(Short.parseShort(userAttributeNum));
				getDataSource().setCreateTime(System.currentTimeMillis());
				getDataSource().setUpdateTime(0);
				
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					Long id = createBo(dataSource);
					jsonObject.put("mdmProfilesId",id);
					if (id == null) {
						jsonObject.put("ok",false);
						jsonObject.put("msg","id==null");
						return "json";
					} else {
						jsonObject.put("parentDomID", getParentDomID());
						jsonObject.put("id", id);
						jsonObject.put("name", getDataSource().getMdmProfilesName());
						jsonObject.put("ok",true);
						return "json";
					}
				}				

				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if(null == this.getDataSource()){
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, MDMPROFILES_JSON_MODE, MDMPROFILES_DIALOG_MODE);
				}
				//use rest api:get mdmProfiles	
				MdmObject object = new MdmObject();
				object = impl.getMdmProfile(this.getDataSource().getMdmProfilesName(),this.getUserContext().getOwner().getInstanceId());
				if(object != null){
					this.validTimeInfo = object.getValidTimeInfo();
					this.setValidTimeDataCron();
					prepareRegionAttributeForUI(object.getConfigurationProfileInfo());
					this.setProfileToUI(object.getConfigurationProfileInfo());
					addLstTitle(getText("config.title.mdmProfiles.edit")+ "'" + getChangedName() + "'");
					String strForward = editBo(this);
					return getReturnPathWithJsonMode(strForward, MDMPROFILES_JSON_MODE);
				}else{
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, MDMPROFILES_JSON_MODE, MDMPROFILES_DIALOG_MODE);
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
								
				ConfigurationProfileInfo profile = new ConfigurationProfileInfo();
				profile = this.getProfileFromUI();
				this.getValidTimeDataCron();
				if(this.isEmptyProfile(profile)){
					displayName = this.displayNameHid;
					addActionError(MgrUtil.getUserMessage("error.operation.empty"));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.operation.empty"));
						return "json";
					}
					return isJsonMode() ? "mdmProfileJson" : INPUT ;
				}
				profile.setDisplayName(getDataSource().getMdmProfilesName());
				//profile.setUserProfileAttributeValue(getDataSource().getUserProfileAttributeValue());
				this.getValidTimeDataCron();

				//use rest api:update mdmProfiles
				if(!impl.setMdmProfile(this.validTimeInfo,profile, this.getUserContext().getOwner().getInstanceId())){
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.operation.fail"));
						return "json";
					}
					return isJsonMode() ? "mdmProfileJson" : INPUT ;
				}		
				
				getDataSource().setUpdateTime(System.currentTimeMillis());
				getDataSource().setUserProfileAttributeValue(Short.parseShort(userAttributeNum));
				updateBo(dataSource);
				
				if ("update".equals(operation)) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("id", getDataSource().getId());
						jsonObject.put("name", getDataSource().getMdmProfilesName());
						jsonObject.put("ok",true);
						return "json";
						
					}else {
						return prepareBoList();
					}
				} else {
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {

				long cloneId = getSelectedIds().get(0);
				MdmProfiles mdmProfiles = (MdmProfiles) findBoById(boClass, cloneId, this);
				if(null == mdmProfiles){
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, MDMPROFILES_JSON_MODE, MDMPROFILES_DIALOG_MODE);
				}
				mdmProfiles.setId(null);
				mdmProfiles.setOwner(null);
				mdmProfiles.setVersion(null);
				setSessionDataSource(mdmProfiles);	
				 
				//use rest api:get mdmProfiles
				MdmObject object = new MdmObject();
				object = impl.getMdmProfile(mdmProfiles.getMdmProfilesName(), this.getUserContext().getOwner().getInstanceId());
				if(null == object){
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, MDMPROFILES_JSON_MODE, MDMPROFILES_DIALOG_MODE);
				}
				this.validTimeInfo = object.getValidTimeInfo();
				this.setValidTimeDataCron();
				ConfigurationProfileInfo profileInfo = object.getConfigurationProfileInfo();
				profileInfo.setDisplayName("");
//				profileInfo.setUserProfileAttributeValue((short) 1);
				this.setProfileToUI(profileInfo); prepareRegionAttributeForUI(profileInfo);
				addLstTitle(getText("config.title.mdmprofiles.new"));				
				return getReturnPathWithJsonMode(INPUT, MDMPROFILES_JSON_MODE);
			} else if (("cancel" + getLstForward()).equals(operation)) {
				System.out.println("cancel");
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("continue".equals(operation)) {
				return getReturnPathWithJsonMode(INPUT, MDMPROFILES_JSON_MODE);
				
			}else if("showCredentialDetail".equals(operation)){
				CredentialsProfileInfo credentials = new CredentialsProfileInfo();
				int idx = Integer.valueOf(index);
				for(int i = 0;i<Integer.valueOf(index);i++){
					if("0".equals(this.haveUploadCertificate[i])){
						idx--;
					}
				}		
				getCredentialsInfoFromFile(uploadCertificate[idx],credentials);
				jsonObject = new JSONObject();
				if(credentials.getIssuer() == null){
					jsonObject.put("havePwd", "0");
				}else{
					jsonObject.put("havePwd", "1");
					jsonObject.put("issuer",credentials.getIssuer());
					jsonObject.put("notbefore", credentials.getNotBefore());
					jsonObject.put("notafter", credentials.getNotAfter());
				}
				return "json";
			}else if("getIssuer12".equals(operation)){
				jsonObject = new JSONObject();
				try{
					int idx = Integer.valueOf(index);
					for(int i = 0;i<Integer.valueOf(index);i++){
						if("0".equals(this.haveUploadCertificate[i])){
							idx--;
						}
					}
				FileInputStream input=new FileInputStream(uploadCertificate[idx]);
				X509Certificate x509certificate = CertificateParser.ananysisP12(input,this.pwd.toCharArray());
				
				jsonObject = new JSONObject();
 				jsonObject.put("issuer",CertificateParser.getIssuer(x509certificate));
				jsonObject.put("notbefore", x509certificate.getNotBefore());
				jsonObject.put("notafter", x509certificate.getNotAfter());
				}catch(Exception e1){
					e1.printStackTrace(); 
				}
				return "json";
			} else if(("remove"+getLstForward()).equals(operation)){
				Set<String> profileNames = new HashSet<String>();
				if (this.getAllSelectedIds() != null && !this.getAllSelectedIds().isEmpty()) {
					for(Long selectedId:getAllSelectedIds()){
						MdmProfiles mdmProfiles = (MdmProfiles) findBoById(boClass, selectedId, this);
						profileNames.add(mdmProfiles.getMdmProfilesName());
					}
				}					
				if(impl.delMdmProfile(profileNames, this.getUserContext().getOwner().getInstanceId())){
					baseOperation();
					return prepareBoList();
				}else{
					addActionError(MgrUtil.getUserMessage("error.operation.fail"));
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, MDMPROFILES_JSON_MODE, MDMPROFILES_DIALOG_MODE);
				}
				
			}
			else {
				baseOperation();
				if(this.getActionErrors() == null){
					if(("remove"+getLstForward()).equals(operation)){
						Set<String> profileNames = new HashSet<String>();
						if (this.getAllSelectedIds() != null && !this.getAllSelectedIds().isEmpty()) {
							for(Long selectedId:getAllSelectedIds()){
								MdmProfiles mdmProfiles = (MdmProfiles) findBoById(boClass, selectedId, this);
								profileNames.add(mdmProfiles.getMdmProfilesName());
							}
						}					
						impl.delMdmProfile(profileNames, this.getUserContext().getOwner().getInstanceId());
					}
				}
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	//=======================================
	public String getPasscodeDisplayStyle(){
		if (passcodeProfileInfos == null || passcodeProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getRestrictDisplayStyle(){
		if (restrictionsProfileInfos == null || restrictionsProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getWifiDisplayStyle(){
		if (wifiProfileInfos == null || wifiProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getVpnDisplayStyle(){
		if (vpnProfileInfos == null || vpnProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getEmailDisplayStyle(){
		if (emailProfileInfos == null || emailProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getExchangeDisplayStyle(){
		if (exchangeProfileInfos == null || exchangeProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getLdapDisplayStyle(){
		if (ldapProfileInfos == null || ldapProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getCaldavDisplayStyle(){
		if (calDavProfileInfos == null || calDavProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	
	public String getCalsubDisplayStyle(){
		if (calendarSubscriptionProfileInfos == null || calendarSubscriptionProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	
	public String getCarddavDisplayStyle(){
		if (cardDavProfileInfos == null || cardDavProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getWebclipsDisplayStyle(){
		if (webClipProfileInfos == null || webClipProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getCredentialsDisplayStyle(){
		if (credentialsProfileInfos == null || credentialsProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	public String getScepDisplayStyle(){
		if (scepProfileInfos == null || scepProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	
	public String getApnDisplayStyle(){
		if (apnProfileInfos == null || apnProfileInfos.size() == 0) {
			return "";
		}
		return "none";
	}
	
	private void getValidTimeDataCron(){
		if(2 == validTimeInfo.getValidType()){
			String startCronTime = this.startTime + " "
				+ ((-1==this.validDay)?"*":"?") + " "
				+ "* "
				+ ((-1 == this.validDay)?"?":((0 == this.validDay)?"1-5":this.validDay)) + " ";
			String endCronTime = this.endTime + " "
				+ ((-1==this.validDay)?"*":"?") + " "
				+ "* "
				+ ((-1==this.validDay)?"?":((0 ==this.validDay)?"1-5":this.validDay)) + " ";
			validTimeInfo.setEffectiveStartTime(startCronTime);
			validTimeInfo.setEffectiveEndTime(endCronTime);
		}
	}
	
	private void setValidTimeDataCron(){
		if(2 == validTimeInfo.getValidType()){			
			String[] cronArr = validTimeInfo.getEffectiveStartTime().split(" ");
			if("*".equals(cronArr[5])||"?".equals(cronArr[5])){
				this.validDay = -1;
			}else if("1-5".equals(cronArr[5])){
				this.validDay = 0;
			}else{
				this.validDay = Short.valueOf(cronArr[5]);
			}
			this.startTime = cronArr[0] + " " + cronArr[1]+ " " + cronArr[2];
			cronArr = validTimeInfo.getEffectiveEndTime().split(" ");
			this.endTime = cronArr[0] + " " + cronArr[1]+ " " + cronArr[2];
		}		
	}
	private ConfigurationProfileInfo getProfileFromUI(){
		ConfigurationProfileInfo profile = new ConfigurationProfileInfo();
		// General
		profile.setDisplayName(displayName);
		profile.setUserProfileAttributeValue(Short.parseShort(userAttributeNum));
		profile.setOrganization(organization);
		if(MDMPROFILE_SECURITY_VALUE_NEVER.equals(security)){
			profile.setHasRemovalPasscode(false);
			profile.setRemovalDisallowed(true);
		}else if(MDMPROFILE_SECURITY_VALUE_AUTHENTIFICATION.equals(security)){
			profile.setHasRemovalPasscode(true);
			profile.setRemovalDisallowed(false);
			profile.setRemovalPasscodeProfileInfo(removalPasscodeProfileInfo);
		}else{
			profile.setHasRemovalPasscode(false);
			profile.setRemovalDisallowed(false);
		}
		profile.setDescription(description);
		// Passcode
		if(passcodeProfileInfos != null){
			for (PasscodeProfileInfo info : passcodeProfileInfos){
				profile.getPasscodeProfileInfos().add(info);
			}
		}		
		// Restrictions
		if(restrictionsProfileInfos != null){
			for (RestrictionsProfileInfo info : restrictionsProfileInfos){
				if(info.isSafariAllowPopups()){
					info.setSafariAllowPopups(false);
				}else{
					info.setSafariAllowPopups(true);
				}
				profile.getRestrictionsProfileInfos().add(info);
			}		
		}
		// Wi-Fi
		if(wifiProfileInfos != null){
			for (WifiProfileInfo info : wifiProfileInfos){
				profile.getWifiProfileInfos().add(info);
			}
		}
		// VPN
		if(vpnProfileInfos != null){
			for (VpnProfileInfo info : vpnProfileInfos){
				/*String[] arrSharedSecret = info.getSharedSecret().split(",");
				if("IPSec".equals(info.getVpnType())){
					info.setSharedSecret(arrSharedSecret[0].trim());
				}else{
					info.setSharedSecret(arrSharedSecret[1].trim());
				}*/
				profile.getVpnProfileInfos().add(info);
			}
		}
		// Email
		if(emailProfileInfos != null){
			for (EmailProfileInfo info : emailProfileInfos){
				profile.getEmailProfileInfos().add(info);
			}		
		}
		//Exchange ActiveSync
		if(exchangeProfileInfos != null){
			for (ExchangeProfileInfo info : exchangeProfileInfos){
				profile.getExchangeProfileInfos().add(info);
			}
		}
		// LDAP
		if(ldapProfileInfos != null){
			for (LdapProfileInfo info : ldapProfileInfos){
				profile.getLdapProfileInfos().add(info);
			}	
		}
		// CalDAV
		if(calDavProfileInfos != null){
			for (CalDavProfileInfo info : calDavProfileInfos){
				profile.getCalDavProfileInfos().add(info);
			}
		}	
		// Subscribed Calendars
		if(calendarSubscriptionProfileInfos != null){
			for (CalendarSubscriptionProfileInfo info : calendarSubscriptionProfileInfos){
				profile.getCalendarSubscriptionProfileInfos().add(info);
			}	
		}
		// CardDAV
		if(cardDavProfileInfos != null){
			for (CardDavProfileInfo info : cardDavProfileInfos){
				profile.getCardDavProfileInfos().add(info);
			}
		}
		// Web Clips
		if(webClipProfileInfos != null){
			for(int i=0;i<webClipProfileInfos.size();i++){
				WebClipProfileInfo info = new WebClipProfileInfo();
				info = webClipProfileInfos.get(i);

				if(!("0").equals(this.haveIcons[i])){
					int idx = i;
					for(int j = 0;j<i;j++){
						if("0".equals(this.haveIcons[j])){
							idx--;
						}
					}
					info.setIcon(getBytesFromFile(icons[idx]));
					info.setIconFileName(this.iconsFileName[idx]);
				}
				// Resize the web clip icon
				if(info.getIcon() != null){
					try {
						info.setIcon(ImageUtil.resizeImage(info.getIcon(), 60, 60));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				profile.getWebClipProfileInfos().add(info);
			}
		}
		// Credentials
		if(credentialsProfileInfos != null){
			for(int i=0;i<credentialsProfileInfos.size();i++){
				CredentialsProfileInfo info = new CredentialsProfileInfo();
				info = credentialsProfileInfos.get(i);
				if(!("0").equals(this.haveUploadCertificate[i])){
					int idx = i;
					for(int j = 0;j<i;j++){
						if("0".equals(this.haveUploadCertificate[j])){
							idx--;
						}
					}
					getCredentialsInfoFromFile(uploadCertificate[idx],info);
				}
				
				profile.getCredentialsProfileInfos().add(info);
			}
		}	
		// SCEP
		if(scepProfileInfos !=null){
			for (ScepProfileInfo info : scepProfileInfos){
				profile.getScepProfileInfos().add(info);
			}	
		}
		// APN;
		if(apnProfileInfos != null){
			for (ApnProfileInfo info : apnProfileInfos){
				profile.getApnProfileInfos().add(info);
			}
		}
		return profile;
	}
	private void updateRestrictionPopupsValue(ConfigurationProfileInfo profile){
		if(profile != null && profile.getRestrictionsProfileInfos() != null){
			for(RestrictionsProfileInfo info : profile.getRestrictionsProfileInfos()){
				if(info.isSafariAllowPopups()){
					info.setSafariAllowPopups(false);
				}else{
					info.setSafariAllowPopups(true);
				}
			}
		}
	}
	private void setProfileToUI(ConfigurationProfileInfo profile){
		// General
		setIconBase64WebClips(profile.getWebClipProfileInfos());
		updateRestrictionPopupsValue(profile);
		displayName = profile.getDisplayName();
		userAttributeNum =Short.toString(profile.getUserProfileAttributeValue());
		organization = profile.getOrganization();
	/*	if(!profile.isHasRemovalPasscode() && profile.isRemovalDisallowed()){
			security = MDMPROFILE_SECURITY_VALUE_NEVER;
		}else if(profile.isHasRemovalPasscode() && !profile.isRemovalDisallowed()){
			security = MDMPROFILE_SECURITY_VALUE_AUTHENTIFICATION;
			removalPasscodeProfileInfo = profile.getRemovalPasscodeProfileInfo();
		}else{
			security = MDMPROFILE_SECURITY_VALUE_ALWAYS;
			security = MDMPROFILE_SECURITY_VALUE_NEVER;
		}*/
		security = MDMPROFILE_SECURITY_VALUE_NEVER;
		
		description = profile.getDescription();
		// Passcode
		passcodeProfileInfos = profile.getPasscodeProfileInfos();		
		// Restrictions	
		restrictionsProfileInfos = profile.getRestrictionsProfileInfos();
		// Wi-Fi
		wifiProfileInfos = profile.getWifiProfileInfos();
		// VPN
		vpnProfileInfos = profile.getVpnProfileInfos();
		
		// Email
		emailProfileInfos = profile.getEmailProfileInfos();
		//Exchange ActiveSync
		exchangeProfileInfos = profile.getExchangeProfileInfos();
		// LDAP
		ldapProfileInfos =profile.getLdapProfileInfos();
		// CalDAV
		calDavProfileInfos = profile.getCalDavProfileInfos();
		// Subscribed Calendars
		calendarSubscriptionProfileInfos = profile.getCalendarSubscriptionProfileInfos();
		// CardDAV
		cardDavProfileInfos = profile.getCardDavProfileInfos();
		// Web Clips
		webClipProfileInfos = profile.getWebClipProfileInfos();
		// Credentials
		credentialsProfileInfos = profile.getCredentialsProfileInfos();
		for(CredentialsProfileInfo info:credentialsProfileInfos){
			if(null != info.getPassword() && !"".equals(info.getPassword())){
				info.setPasswordDisplay("");
			}else{
				info.setPasswordDisplay("none");
			}
		}
		// SCEP
		scepProfileInfos = profile.getScepProfileInfos();
		// APN
		apnProfileInfos = profile.getApnProfileInfos();
	} 

	@Override
	public MdmProfiles getDataSource() {
		return (MdmProfiles) dataSource;
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MDM_PROFILES);
		setDataSource(MdmProfiles.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MDM_PROFILES;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
 
		MdmProfiles source = QueryUtil.findBoById(MdmProfiles.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		//=======================================
		MdmObject objectS = new MdmObject();
		objectS = impl.getMdmProfile(source.getMdmProfilesName(), this.getUserContext().getOwner().getInstanceId());
		//========================================

		List<MdmProfiles> list = QueryUtil.executeQuery(MdmProfiles.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (MdmProfiles profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MdmProfiles rp = source.clone();
			if (null == rp) {
				continue;
			}
			//===================================
			objectS.getConfigurationProfileInfo().setDisplayName(profile.getMdmProfilesName());
			objectS.getConfigurationProfileInfo().setUserProfileAttributeValue(profile.getUserProfileAttributeValue());
			if(!impl.setMdmProfile(objectS.getValidTimeInfo(),objectS.getConfigurationProfileInfo(), this.getUserContext().getOwner().getInstanceId())){
				return null;
			}
			//===================================
//			
//			if(!impl.setMdmProfile(objectS.getValidTimeInfo(),objectS.getConfigurationProfileInfo(), this.getUserContext().getOwner().getInstanceId())){
//				return null;
//			}
			//====================================
			rp.setId(profile.getId());
			rp.setMdmProfilesName(profile.getMdmProfilesName());
			rp.setVersion(profile.getVersion());
			rp.setOwner(profile.getOwner());
			hmBos.add(rp);
		}
		return hmBos;
	}
	
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_CREATETIME = 2;
    public static final int COLUMN_UPDATETIME = 3;
    public static final int COLUMN_USERATTRIBUTE = 4;
	
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.security.mdmProfiles.name";
			break;
		case COLUMN_CREATETIME:
			code = "config.security.mdmProfiles.createtime";
			break;
		case COLUMN_UPDATETIME:
			code = "config.security.mdmProfiles.updatetime";
			break;
		case COLUMN_USERATTRIBUTE:
			code = "config.security.mdmProfiles.userProfileAttribute";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	    
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_CREATETIME));
		columns.add(new HmTableColumn(COLUMN_UPDATETIME));
		columns.add(new HmTableColumn(COLUMN_USERATTRIBUTE));
		return columns;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	//=========================================================================== 
	public static final int MDM_VALID_TYPE_ALWAYS = 0;
	public static final int MDM_VALID_TYPE_DELETEAFTER = 1;
	public static final int MDM_VALID_TYPE_VALIDPERIOD = 2;
	public static final int MDM_VALID_TYPE_DELETE_TYPE_NOT_DELTE = 0;
	public static final int MDM_VALID_TYPE_DELTE_TYPE_DELETE = 1;
	public EnumItem[] getValidTimeOption1() {
		return new EnumItem[] { new EnumItem(MDM_VALID_TYPE_ALWAYS,
				getText("config.secutity.mdmProfiles.validType.always")) };
	}
	
	public EnumItem[] getValidTimeOption2() {
		return new EnumItem[] { new EnumItem(MDM_VALID_TYPE_DELETEAFTER,
				getText("config.secutity.mdmProfiles.validType.deleteAfter")) };
	}
	
	public EnumItem[] getValidTimeOption3() {
		return new EnumItem[] { new EnumItem(MDM_VALID_TYPE_VALIDPERIOD,
				getText("config.secutity.mdmProfiles.validType.validPeriod")) };
	}
	
	public EnumItem[] getDeleteOption1(){
		return new EnumItem[] { new EnumItem(MDM_VALID_TYPE_DELETE_TYPE_NOT_DELTE,
				getText("config.secutity.mdmProfiles.validType.deleteAfter.opt1"))};
	}
	public EnumItem[] getDeleteOption2(){
		return new EnumItem[] { new EnumItem(MDM_VALID_TYPE_DELTE_TYPE_DELETE,
				getText("config.secutity.mdmProfiles.validType.deleteAfter.opt2"))};
	}
	public static final int MDM_VALIDTYPE_EVERYDAY = -1;
	public static final int MDM_VALIDTYPE_EVERYWORKINGDAY = 0;
	public static final int MDM_VALIDTYPE_EVERYMONDAY = 1;
	public static final int MDM_VALIDTYPE_EVERYTUESDAY = 2;
	public static final int MDM_VALIDTYPE_EVERYWEDNESDAY = 3;
	public static final int MDM_VALIDTYPE_EVERYTHURSDAY = 4;
	public static final int MDM_VALIDTYPE_EVERYFRIDAY = 5;
	public static final int MDM_VALIDTYPE_EVERYSATURDAY = 6;
	public static final int MDM_VALIDTYPE_EVERYSUNDAY = 7;
	
	public EnumItem[] getValidPeriodTypeList() {
		return MgrUtil.enumItems(
				"enum.mdm.validPeriodType.", new int[] { MDM_VALIDTYPE_EVERYDAY,
						MDM_VALIDTYPE_EVERYWORKINGDAY,MDM_VALIDTYPE_EVERYMONDAY,
						MDM_VALIDTYPE_EVERYTUESDAY,MDM_VALIDTYPE_EVERYWEDNESDAY,
						MDM_VALIDTYPE_EVERYTHURSDAY,MDM_VALIDTYPE_EVERYFRIDAY,
						MDM_VALIDTYPE_EVERYSATURDAY,MDM_VALIDTYPE_EVERYSUNDAY});
	}
	
	//============================================================================
	private ValidTimeInfo validTimeInfo;
	private String startTime="0 0 9";
	private String endTime="0 0 17";
	private short validDay=-1;
	private String mdmProfilesName;
	private String displayName;
	private String displayNameHid;
	private String userAttributeNum;
	private String organization;
	private String security;
	private RemovalPasscodeProfileInfo removalPasscodeProfileInfo;
	private String description;
	private List<PasscodeProfileInfo> passcodeProfileInfos;
	private List<RestrictionsProfileInfo> restrictionsProfileInfos;
	private List<WifiProfileInfo> wifiProfileInfos;
	private List<VpnProfileInfo> vpnProfileInfos;
	private List<EmailProfileInfo> emailProfileInfos;
	private List<ExchangeProfileInfo> exchangeProfileInfos;
	private List<LdapProfileInfo> ldapProfileInfos;
	private List<CalDavProfileInfo>	calDavProfileInfos;
	private List<CalendarSubscriptionProfileInfo> calendarSubscriptionProfileInfos;
	private List<CardDavProfileInfo> cardDavProfileInfos;
	private List<WebClipProfileInfo> webClipProfileInfos;	
	private File[] icons;
	private String[] iconsFileName;
	private String[] haveIcons;
	private List<CredentialsProfileInfo> credentialsProfileInfos;
	private File[] uploadCertificate;
	private String index;
	private String pwd;
	private String[] haveUploadCertificate;
	private List<ScepProfileInfo> scepProfileInfos;
	private List<ApnProfileInfo> apnProfileInfos;
	
	public String getDisplayNameHid() {
		return displayNameHid;
	}
	public void setDisplayNameHid(String displayNameHid) {
		this.displayNameHid = displayNameHid;
	}
	public String[] getHaveIcons() {
		return haveIcons;
	}
	public void setHaveIcons(String[] haveIcons) {
		this.haveIcons = haveIcons;
	}
	public String[] getHaveUploadCertificate() {
		return haveUploadCertificate;
	}
	public void setHaveUploadCertificate(String[] haveUploadCertificate) {
		this.haveUploadCertificate = haveUploadCertificate;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public File[] getUploadCertificate() {
		return uploadCertificate;
	}
	public void setUploadCertificate(File[] uploadCertificate) {
		this.uploadCertificate = uploadCertificate;
	}
	public File[] getIcons() {
		return icons;
	}
	public void setIcons(File[] icons) {
		this.icons = icons;
	}
	public String[] getIconsFileName() {
		return iconsFileName;
	}
	public void setIconsFileName(String[] iconsFileName) {
		this.iconsFileName = iconsFileName;
	}
	public ProfileMgrServiceImpl getImpl() {
		return impl;
	}
	public void setImpl(ProfileMgrServiceImpl impl) {
		this.impl = impl;
	}
	public ValidTimeInfo getValidTimeInfo() {
		return validTimeInfo;
	}
	public void setValidTimeInfo(ValidTimeInfo validTimeInfo) {
		this.validTimeInfo = validTimeInfo;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public short getValidDay() {
		return validDay;
	}
	public void setValidDay(short validDay) {
		this.validDay = validDay;
	}
	public String getMdmProfilesName() {
		return mdmProfilesName;
	}
	public void setMdmProfilesName(String mdmProfilesName) {
		this.mdmProfilesName = mdmProfilesName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getUserAttributeNum() {
		return userAttributeNum;
	}
	public void setUserAttributeNum(String userAttributeNum) {
		this.userAttributeNum = userAttributeNum;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getSecurity() {
		return security;
	}
	public void setSecurity(String security) {
		this.security = security;
	}
	public RemovalPasscodeProfileInfo getRemovalPasscodeProfileInfo() {
		return removalPasscodeProfileInfo;
	}
	public void setRemovalPasscodeProfileInfo(
			RemovalPasscodeProfileInfo removalPasscodeProfileInfo) {
		this.removalPasscodeProfileInfo = removalPasscodeProfileInfo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<PasscodeProfileInfo> getPasscodeProfileInfos() {
		return passcodeProfileInfos;
	}
	public void setPasscodeProfileInfos(
			List<PasscodeProfileInfo> passcodeProfileInfos) {
		this.passcodeProfileInfos = passcodeProfileInfos;
	}
	public List<RestrictionsProfileInfo> getRestrictionsProfileInfos() {
		return restrictionsProfileInfos;
	}
	public void setRestrictionsProfileInfos(
			List<RestrictionsProfileInfo> restrictionsProfileInfos) {
		this.restrictionsProfileInfos = restrictionsProfileInfos;
	}
	public List<WifiProfileInfo> getWifiProfileInfos() {
		return wifiProfileInfos;
	}
	public void setWifiProfileInfos(List<WifiProfileInfo> wifiProfileInfos) {
		this.wifiProfileInfos = wifiProfileInfos;
	}
	public List<VpnProfileInfo> getVpnProfileInfos() {
		return vpnProfileInfos;
	}
	public void setVpnProfileInfos(List<VpnProfileInfo> vpnProfileInfos) {
		this.vpnProfileInfos = vpnProfileInfos;
	}
	public List<EmailProfileInfo> getEmailProfileInfos() {
		return emailProfileInfos;
	}
	public void setEmailProfileInfos(List<EmailProfileInfo> emailProfileInfos) {
		this.emailProfileInfos = emailProfileInfos;
	}
	public List<ExchangeProfileInfo> getExchangeProfileInfos() {
		return exchangeProfileInfos;
	}
	public void setExchangeProfileInfos(
			List<ExchangeProfileInfo> exchangeProfileInfos) {
		this.exchangeProfileInfos = exchangeProfileInfos;
	}
	public List<LdapProfileInfo> getLdapProfileInfos() {
		return ldapProfileInfos;
	}
	public void setLdapProfileInfos(List<LdapProfileInfo> ldapProfileInfos) {
		this.ldapProfileInfos = ldapProfileInfos;
	}
	public List<CalDavProfileInfo> getCalDavProfileInfos() {
		return calDavProfileInfos;
	}
	public void setCalDavProfileInfos(List<CalDavProfileInfo> calDavProfileInfos) {
		this.calDavProfileInfos = calDavProfileInfos;
	}
	public List<CalendarSubscriptionProfileInfo> getCalendarSubscriptionProfileInfos() {
		return calendarSubscriptionProfileInfos;
	}
	public void setCalendarSubscriptionProfileInfos(
			List<CalendarSubscriptionProfileInfo> calendarSubscriptionProfileInfos) {
		this.calendarSubscriptionProfileInfos = calendarSubscriptionProfileInfos;
	}
	public List<CardDavProfileInfo> getCardDavProfileInfos() {
		return cardDavProfileInfos;
	}
	public void setCardDavProfileInfos(List<CardDavProfileInfo> cardDavProfileInfos) {
		this.cardDavProfileInfos = cardDavProfileInfos;
	}
	public List<WebClipProfileInfo> getWebClipProfileInfos() {
		return webClipProfileInfos;
	}
	public void setWebClipProfileInfos(List<WebClipProfileInfo> webClipProfileInfos) {
		this.webClipProfileInfos = webClipProfileInfos;
	}
	public List<CredentialsProfileInfo> getCredentialsProfileInfos() {
		return credentialsProfileInfos;
	}
	public void setCredentialsProfileInfos(
			List<CredentialsProfileInfo> credentialsProfileInfos) {
		this.credentialsProfileInfos = credentialsProfileInfos;
	}
	public List<ScepProfileInfo> getScepProfileInfos() {
		return scepProfileInfos;
	}
	public void setScepProfileInfos(List<ScepProfileInfo> scepProfileInfos) {
		this.scepProfileInfos = scepProfileInfos;
	}
	public List<ApnProfileInfo> getApnProfileInfos() {
		return apnProfileInfos;
	}
	public void setApnProfileInfos(List<ApnProfileInfo> apnProfileInfos) {
		this.apnProfileInfos = apnProfileInfos;
	}
	
	public Range getNumberRange() {
		return super.getAttributeRange("userProfileAttributeValue");
	}
	
	//=============================================
	private boolean isEmptyProfile(ConfigurationProfileInfo profile){
		boolean ret = false;
		if(profile.getApnProfileInfos().isEmpty()
				&& profile.getPasscodeProfileInfos().isEmpty()
				&& profile.getRestrictionsProfileInfos().isEmpty()
				&& profile.getWifiProfileInfos().isEmpty()
				&& profile.getVpnProfileInfos().isEmpty()
				&& profile.getEmailProfileInfos().isEmpty()
				&& profile.getExchangeProfileInfos().isEmpty()
				&& profile.getLdapProfileInfos().isEmpty()
				&& profile.getCalDavProfileInfos().isEmpty()
				&& profile.getCalendarSubscriptionProfileInfos().isEmpty()
				&& profile.getCardDavProfileInfos().isEmpty()				
				&& profile.getWebClipProfileInfos().isEmpty()
				&& profile.getCredentialsProfileInfos().isEmpty()
				&& profile.getScepProfileInfos().isEmpty()
				&& (profile.getRemovalPasscodeProfileInfo() ==null)){
			ret = true;
		}
		return ret;
	}
	public static void getCredentialsInfoFromFile(File f,CredentialsProfileInfo credentialsProfileInfo){
        if (f == null){  
            return;  
        }  
        Boolean isP12 = true;
        try{
        	CertificateFactory certificate_factory=CertificateFactory.getInstance("X.509");
        	FileInputStream file_inputstream=new FileInputStream(f);
        	X509Certificate x509certificate=(X509Certificate)certificate_factory.generateCertificate(file_inputstream);
        	credentialsProfileInfo.setIssuer(CertificateParser.getIssuer(x509certificate));
        	credentialsProfileInfo.setNotBefore(x509certificate.getNotBefore());
        	credentialsProfileInfo.setNotAfter(x509certificate.getNotAfter());
        	isP12 = false;
        } catch (Exception e){  
            e.printStackTrace();  
        }         
        try{
            FileInputStream stream = new FileInputStream(f);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = stream.read(b)) != -1)  
                out.write(b, 0, n);  
                stream.close();  
                out.close(); 
        	if(isP12){
        		credentialsProfileInfo.setCertificateContent(Base64.encode(out.toByteArray()));
        	}else{
        		credentialsProfileInfo.setCertificateContent(out.toByteArray());
        	}
        	
        } catch (Exception e){  
            e.printStackTrace();  
        } 
        return;  
    }  
	
	public static byte[] getBytesFromFile(File f){  
        if (f == null){  
            return null;  
        }  
        try{  
            FileInputStream stream = new FileInputStream(f);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = stream.read(b)) != -1)  
                out.write(b, 0, n);  
                stream.close();  
                out.close();  
            return out.toByteArray();  
        } catch (IOException e){  
            e.printStackTrace();  
        }  
        return null;  
    }
	/**
	 * Author : she 
	 * Date: 2013/5/17
	 * @param sourceByte
	 * @return
	 */
	  
	

	private List<DictItem> usMovieParams = new ArrayList<DictItem>();
	private List<DictItem> usTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> auMovieParams = new ArrayList<DictItem>();
	private List<DictItem> auTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> caMovieParams = new ArrayList<DictItem>();
	private List<DictItem> caTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> deMovieParams = new ArrayList<DictItem>();
	private List<DictItem> deTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> ieMovieParams = new ArrayList<DictItem>();
	private List<DictItem> ieTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> frMovieParams = new ArrayList<DictItem>();
	private List<DictItem> frTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> gbMovieParams = new ArrayList<DictItem>();
	private List<DictItem> gbTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> jpMovieParams = new ArrayList<DictItem>();
	private List<DictItem> jpTvParams = new ArrayList<DictItem>();
	
	private List<DictItem> nzMovieParams = new ArrayList<DictItem>();
	private List<DictItem> nzTvParams = new ArrayList<DictItem>();
	
	private String choiceRatingShow;
	
	public String getChoiceRatingShow() {
		return choiceRatingShow;
	}
	public void setChoiceRatingShow(String choiceRatingShow) {
		this.choiceRatingShow = choiceRatingShow;
	}
	public List<DictItem> getUsMovieParams() {
		return usMovieParams;
	}
	public void setUsMovieParams(List<DictItem> usMovieParams) {
		this.usMovieParams = usMovieParams;
	}
	public List<DictItem> getUsTvParams() {
		return usTvParams;
	}
	public void setUsTvParams(List<DictItem> usTvParams) {
		this.usTvParams = usTvParams;
	}
	public List<DictItem> getAuMovieParams() {
		return auMovieParams;
	}
	public void setAuMovieParams(List<DictItem> auMovieParams) {
		this.auMovieParams = auMovieParams;
	}
	public List<DictItem> getAuTvParams() {
		return auTvParams;
	}
	public void setAuTvParams(List<DictItem> auTvParams) {
		this.auTvParams = auTvParams;
	}
	public List<DictItem> getCaMovieParams() {
		return caMovieParams;
	}
	public void setCaMovieParams(List<DictItem> caMovieParams) {
		this.caMovieParams = caMovieParams;
	}
	public List<DictItem> getCaTvParams() {
		return caTvParams;
	}
	public void setCaTvParams(List<DictItem> caTvParams) {
		this.caTvParams = caTvParams;
	}
	public List<DictItem> getDeMovieParams() {
		return deMovieParams;
	}
	public void setDeMovieParams(List<DictItem> deMovieParams) {
		this.deMovieParams = deMovieParams;
	}
	public List<DictItem> getDeTvParams() {
		return deTvParams;
	}
	public void setDeTvParams(List<DictItem> deTvParams) {
		this.deTvParams = deTvParams;
	}
	public List<DictItem> getIeMovieParams() {
		return ieMovieParams;
	}
	public void setIeMovieParams(List<DictItem> ieMovieParams) {
		this.ieMovieParams = ieMovieParams;
	}
	public List<DictItem> getIeTvParams() {
		return ieTvParams;
	}
	public void setIeTvParams(List<DictItem> ieTvParams) {
		this.ieTvParams = ieTvParams;
	}
	public List<DictItem> getFrMovieParams() {
		return frMovieParams;
	}
	public void setFrMovieParams(List<DictItem> frMovieParams) {
		this.frMovieParams = frMovieParams;
	}
	public List<DictItem> getFrTvParams() {
		return frTvParams;
	}
	public void setFrTvParams(List<DictItem> frTvParams) {
		this.frTvParams = frTvParams;
	}
	public List<DictItem> getGbMovieParams() {
		return gbMovieParams;
	}
	public void setGbMovieParams(List<DictItem> gbMovieParams) {
		this.gbMovieParams = gbMovieParams;
	}
	public List<DictItem> getGbTvParams() {
		return gbTvParams;
	}
	public void setGbTvParams(List<DictItem> gbTvParams) {
		this.gbTvParams = gbTvParams;
	}
	public List<DictItem> getJpMovieParams() {
		return jpMovieParams;
	}
	public void setJpMovieParams(List<DictItem> jpMovieParams) {
		this.jpMovieParams = jpMovieParams;
	}
	public List<DictItem> getJpTvParams() {
		return jpTvParams;
	}
	public void setJpTvParams(List<DictItem> jpTvParams) {
		this.jpTvParams = jpTvParams;
	}
	public List<DictItem> getNzMovieParams() {
		return nzMovieParams;
	}
	public void setNzMovieParams(List<DictItem> nzMovieParams) {
		this.nzMovieParams = nzMovieParams;
	}
	public List<DictItem> getNzTvParams() {
		return nzTvParams;
	}
	public void setNzTvParams(List<DictItem> nzTvParams) {
		this.nzTvParams = nzTvParams;
	}
	public List getUsMoviesValues() {
		return usMoviesValues;
	}
	public void setUsMoviesValues(List usMoviesValues) {
		this.usMoviesValues = usMoviesValues;
	}
	public List getUsMovieskey() {
		return usMovieskey;
	}
	public void setUsMovieskey(List usMovieskey) {
		this.usMovieskey = usMovieskey;
	}
	public List getUsTvShowsValues() {
		return usTvShowsValues;
	}
	public void setUsTvShowsValues(List usTvShowsValues) {
		this.usTvShowsValues = usTvShowsValues;
	}
	public List getUsTvkey() {
		return usTvkey;
	}
	public void setUsTvkey(List usTvkey) {
		this.usTvkey = usTvkey;
	}
	public List getAuMoviesValues() {
		return auMoviesValues;
	}
	public void setAuMoviesValues(List auMoviesValues) {
		this.auMoviesValues = auMoviesValues;
	}
	public List getAuMovieskey() {
		return auMovieskey;
	}
	public void setAuMovieskey(List auMovieskey) {
		this.auMovieskey = auMovieskey;
	}
	public List getAuTvShowsValues() {
		return auTvShowsValues;
	}
	public void setAuTvShowsValues(List auTvShowsValues) {
		this.auTvShowsValues = auTvShowsValues;
	}
	public List getAuTvShowskey() {
		return auTvShowskey;
	}
	public void setAuTvShowskey(List auTvShowskey) {
		this.auTvShowskey = auTvShowskey;
	}
	public List getCaMoviesValues() {
		return caMoviesValues;
	}
	public void setCaMoviesValues(List caMoviesValues) {
		this.caMoviesValues = caMoviesValues;
	}
	public List getCaMovieskey() {
		return caMovieskey;
	}
	public void setCaMovieskey(List caMovieskey) {
		this.caMovieskey = caMovieskey;
	}
	public List getCaTVShowsValues() {
		return caTVShowsValues;
	}
	public void setCaTVShowsValues(List caTVShowsValues) {
		this.caTVShowsValues = caTVShowsValues;
	}
	public List getCaTVShowskey() {
		return caTVShowskey;
	}
	public void setCaTVShowskey(List caTVShowskey) {
		this.caTVShowskey = caTVShowskey;
	}
	public List getDeMoviesValues() {
		return deMoviesValues;
	}
	public void setDeMoviesValues(List deMoviesValues) {
		this.deMoviesValues = deMoviesValues;
	}
	public List getDeMovieskey() {
		return deMovieskey;
	}
	public void setDeMovieskey(List deMovieskey) {
		this.deMovieskey = deMovieskey;
	}
	public List getDeTVShowsValues() {
		return deTVShowsValues;
	}
	public void setDeTVShowsValues(List deTVShowsValues) {
		this.deTVShowsValues = deTVShowsValues;
	}
	public List getDeTVShowskey() {
		return deTVShowskey;
	}
	public void setDeTVShowskey(List deTVShowskey) {
		this.deTVShowskey = deTVShowskey;
	}
	public List getAppsValues() {
		return appsValues;
	}
	public void setAppsValues(List appsValues) {
		this.appsValues = appsValues;
	}
	public List getAppskeys() {
		return appskeys;
	}
	public void setAppskeys(List appskeys) {
		this.appskeys = appskeys;
	}
	public List getFrMoviesValues() {
		return frMoviesValues;
	}
	public void setFrMoviesValues(List frMoviesValues) {
		this.frMoviesValues = frMoviesValues;
	}
	public List getFrMovieskey() {
		return frMovieskey;
	}
	public void setFrMovieskey(List frMovieskey) {
		this.frMovieskey = frMovieskey;
	}
	public List getFrTVShowsValues() {
		return frTVShowsValues;
	}
	public void setFrTVShowsValues(List frTVShowsValues) {
		this.frTVShowsValues = frTVShowsValues;
	}
	public List getFrTVShowskey() {
		return frTVShowskey;
	}
	public void setFrTVShowskey(List frTVShowskey) {
		this.frTVShowskey = frTVShowskey;
	}
	public List getIeMoviesValues() {
		return ieMoviesValues;
	}
	public void setIeMoviesValues(List ieMoviesValues) {
		this.ieMoviesValues = ieMoviesValues;
	}
	public List getIeMovieskey() {
		return ieMovieskey;
	}
	public void setIeMovieskey(List ieMovieskey) {
		this.ieMovieskey = ieMovieskey;
	}
	public List getIeTvShowsValues() {
		return ieTvShowsValues;
	}
	public void setIeTvShowsValues(List ieTvShowsValues) {
		this.ieTvShowsValues = ieTvShowsValues;
	}
	public List getIeTvShowskey() {
		return ieTvShowskey;
	}
	public void setIeTvShowskey(List ieTvShowskey) {
		this.ieTvShowskey = ieTvShowskey;
	}
	public List getJpMoviesValues() {
		return jpMoviesValues;
	}
	public void setJpMoviesValues(List jpMoviesValues) {
		this.jpMoviesValues = jpMoviesValues;
	}
	public List getJpMovieskey() {
		return jpMovieskey;
	}
	public void setJpMovieskey(List jpMovieskey) {
		this.jpMovieskey = jpMovieskey;
	}
	public List getJpTVShowsValues() {
		return jpTVShowsValues;
	}
	public void setJpTVShowsValues(List jpTVShowsValues) {
		this.jpTVShowsValues = jpTVShowsValues;
	}
	public List getJpTVShowskey() {
		return jpTVShowskey;
	}
	public void setJpTVShowskey(List jpTVShowskey) {
		this.jpTVShowskey = jpTVShowskey;
	}
	public List getNzMoivesValues() {
		return nzMoivesValues;
	}
	public void setNzMoivesValues(List nzMoivesValues) {
		this.nzMoivesValues = nzMoivesValues;
	}
	public List getNzMoiveskey() {
		return nzMoiveskey;
	}
	public void setNzMoiveskey(List nzMoiveskey) {
		this.nzMoiveskey = nzMoiveskey;
	}
	public List getNzTVShowsValues() {
		return nzTVShowsValues;
	}
	public void setNzTVShowsValues(List nzTVShowsValues) {
		this.nzTVShowsValues = nzTVShowsValues;
	}
	public List getNzTVShowskey() {
		return nzTVShowskey;
	}
	public void setNzTVShowskey(List nzTVShowskey) {
		this.nzTVShowskey = nzTVShowskey;
	}
	public List getGbMoviesValues() {
		return gbMoviesValues;
	}
	public void setGbMoviesValues(List gbMoviesValues) {
		this.gbMoviesValues = gbMoviesValues;
	}
	public List getGbMovieskey() {
		return gbMovieskey;
	}
	public void setGbMovieskey(List gbMovieskey) {
		this.gbMovieskey = gbMovieskey;
	}
	public List getGbTVShowsValues() {
		return gbTVShowsValues;
	}
	public void setGbTVShowsValues(List gbTVShowsValues) {
		this.gbTVShowsValues = gbTVShowsValues;
	}
	public List getGbTVShowskey() {
		return gbTVShowskey;
	}
	public void setGbTVShowskey(List gbTVShowskey) {
		this.gbTVShowskey = gbTVShowskey;
	}

	private List usMoviesValues = new ArrayList<>();
	private List usMovieskey = new ArrayList<>();
	private List usTvShowsValues = new ArrayList<>();
	private List usTvkey = new ArrayList<>();

	private List auMoviesValues = new ArrayList<>();
	private List auMovieskey = new ArrayList<>();
	private List auTvShowsValues = new ArrayList<>();
	private List auTvShowskey = new ArrayList<>();

	private List caMoviesValues = new ArrayList<>();
	private List caMovieskey = new ArrayList<>();
	private List caTVShowsValues = new ArrayList<>();
	private List caTVShowskey = new ArrayList<>();

	private List deMoviesValues = new ArrayList<>();
	private List deMovieskey = new ArrayList<>();
	private List deTVShowsValues = new ArrayList<>();
	private List deTVShowskey = new ArrayList<>();

	private List appsValues = new ArrayList<>();
	private List appskeys = new ArrayList<>();

	private List frMoviesValues = new ArrayList<>();
	private List frMovieskey = new ArrayList<>();
	private List frTVShowsValues = new ArrayList<>();
	private List frTVShowskey = new ArrayList<>();

	private List ieMoviesValues = new ArrayList<>();
	private List ieMovieskey = new ArrayList<>();
	private List ieTvShowsValues = new ArrayList<>();
	private List ieTvShowskey = new ArrayList<>();

	private List jpMoviesValues = new ArrayList<>();
	private List jpMovieskey = new ArrayList<>();
	private List jpTVShowsValues = new ArrayList<>();
	private List jpTVShowskey = new ArrayList<>();

	private List nzMoivesValues = new ArrayList<>();
	private List nzMoiveskey = new ArrayList<>();
	private List nzTVShowsValues = new ArrayList<>();
	private List nzTVShowskey = new ArrayList<>();

	private List gbMoviesValues = new ArrayList<>();
	private List gbMovieskey = new ArrayList<>();
	private List gbTVShowsValues = new ArrayList<>();
	private List gbTVShowskey = new ArrayList<>();

	public void setContentRatingForRes() {
		usMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.us.movies"));
		usMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.us.movies.key"));
		usTvShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.us.tv"));
		usTvkey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.us.tv.key"));

		auMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.au.movies"));
		auMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.au.movies.key"));
		auTvShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.au.tv"));
		auTvShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.au.tv.key"));

		caMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ca.movies"));
		caMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ca.movies.key"));
		caTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ca.tv"));
		caTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ca.tv.key"));

		deMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.movies"));
		deMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.movies.key"));
		deTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.tv"));
		deTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.tv.key"));

		appsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.app"));
		appskeys = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.de.app.key"));
		frMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.fr.movies"));
		frMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.fr.movies.key"));
		frTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.fr.tv"));
		frTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.fr.tv.key"));

		ieMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ie.movies"));
		ieMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ie.movies.key"));
		ieTvShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ie.tv"));
		ieTvShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.ie.tv.key"));

		jpMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.jp.movies"));
		jpMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.jp.movies.key"));
		jpTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.jp.tv"));
		jpTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.jp.tv.key"));

		nzMoivesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.nz.movies"));
		nzMoiveskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.nz.movies.key"));
		nzTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.nz.tv"));
		nzTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.nz.tv.key"));

		gbMoviesValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.gb.movies"));
		gbMovieskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.gb.movies.key"));
		gbTVShowsValues = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.gb.tv"));
		gbTVShowskey = getValueList(MgrUtil
				.getUserMessage("client.profile.restriction.ratings.gb.tv.key"));
	}

	private void setContentRatingValue(String conChar) {
		choiceRatingShow = conChar;
		List<DictItem> item = new ArrayList<DictItem>();
		if (conChar.equals("us")) {
			usMovieParams = contructDictItem(usMovieskey, usMoviesValues);
			usTvParams = contructDictItem(usTvkey, usTvShowsValues);
		} else if (conChar.equals("au")) {
			auMovieParams = contructDictItem(auMovieskey, auMoviesValues);
			auTvParams = contructDictItem(auTvShowskey, auTvShowsValues);
		} else if (conChar.equals("ca")) {
			caMovieParams = contructDictItem(caMovieskey, caMoviesValues);
			caTvParams = contructDictItem(caTVShowskey, caTVShowsValues);
		} else if (conChar.equals("de")) {
			deMovieParams = contructDictItem(deMovieskey, deMoviesValues);
			deTvParams = contructDictItem(deTVShowskey, deTVShowsValues);
		} else if (conChar.equals("fr")) {
			frMovieParams = contructDictItem(frMovieskey, frMoviesValues);
			frTvParams = contructDictItem(frTVShowskey, frTVShowsValues);
		} else if (conChar.equals("ie")) {
			ieMovieParams = contructDictItem(ieMovieskey, ieMoviesValues);
			ieTvParams = contructDictItem(ieTvShowskey, ieTvShowsValues);
		} else if (conChar.equals("jp")) {
			jpMovieParams = contructDictItem(jpMovieskey, jpMoviesValues);
			jpTvParams = contructDictItem(jpTVShowskey, jpTVShowsValues);
		} else if (conChar.equals("nz")) {
			nzMovieParams = contructDictItem(nzMoiveskey, nzMoivesValues);
			nzTvParams = contructDictItem(nzTVShowskey, nzTVShowsValues);
		} else {
			gbMovieParams = contructDictItem(gbMovieskey, gbMoviesValues);
			gbTvParams = contructDictItem(gbTVShowskey, gbTVShowsValues);
		}
	}

	private List<String> getValueList(String valueSource) {
		List<String> desList = new ArrayList<String>();
		for (String str : valueSource.split(",")) {
			desList.add(str);
		}
		return desList;
	}

	private void prepareRegionAttributeForUI(ConfigurationProfileInfo profile) {
		restrictionsProfileInfos = profile.getRestrictionsProfileInfos();
		if (restrictionsProfileInfos.size() > 0) {
			setContentRatingValue(restrictionsProfileInfos.get(0)
					.getRatingRegion());
		}
	}
	
	public void setIconBase64WebClips(
			List<WebClipProfileInfo> webClipProfileInfos) {
		for (WebClipProfileInfo profile : webClipProfileInfos) {
			if (profile.getIcon() != null) {
				profile.setIconStr(transByteToBase64(profile.getIcon()));
			}
		}
	}

	public String transByteToBase64(byte[] sourceByte) {
		return new BASE64Encoder().encode(sourceByte);
	}

	private List<DictItem> contructDictItem(List<String> key, List<String> value){
		List<DictItem> item = new ArrayList<DictItem>();
		for(int i = 0 ; i<key.size() ; i++){
			DictItem dict = new DictItem();
			dict.setKeyCode(key.get(i));
			dict.setValueCode(value.get(i));
			item.add(dict);
		}
		return item;
	}
	// End of editing by She
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		return null;
	}
}