package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hibernate.validator.constraints.Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.SubjectAltname_st;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.UserProfileForTrafficL2;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

import edu.emory.mathcs.backport.java.util.Collections;

public class VpnServiceAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private  boolean wirelessRoutingEnabled;
	private  String  wirelessRoutingEnabledStyle = "none";

	private static final Tracer log = new Tracer(VpnServiceAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		if(!isJsonMode()){
			wirelessRoutingEnabled = isFullMode();
		}
		
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("new".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (!setTitleAndCheckAccess(getText("config.title.vpn.service"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new VpnService());
				prepareDependentObjects();
				hideCreateItem = "";
				hideNewButton = "none";
				hideCreateTunnelException = "";
				hideNewTunnelException = "none";
				return returnResultKeyWord(INPUT,"vpnOnly");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				prepareSaveObjects();
				if (checkNameExists("profileName", getDataSource()
						.getProfileName())
						|| !checkCertificateValid()
						|| !checkCredentialCounts()) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"vpnOnly");
				}
				if (hasBoInEasyMode()) {
					return prepareBoList();
				}
				// create capwapIp object if needed;
				IpAddress dnsIp = autoCreateIpAddress();
				if (null != dnsIp) {
					getDataSource().setDnsIp(dnsIp);
				}
				//prepare the user profile to be saved
				updateDependentObjects();
				String result;
				Long newId;
				if ("create".equals(operation)) {
					newId = createBo(dataSource);
					result = prepareBoList();
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("id", newId);
						jsonObject.put("n", true);
						result = "json";
					}
				} else {
					newId = id = createBo(dataSource);
					setUpdateContext(true);
					result = getLstForward();
				}
				if (isEasyMode()) {
					VpnService vpn = QueryUtil.findBoById(VpnService.class,
							newId);
					ConfigTemplate defaultTemplate = HmBeParaUtil
							.getEasyModeDefaultTemplate(domainId);
					defaultTemplate.setVpnService(vpn);
					QueryUtil.updateBo(defaultTemplate);
				}
				return result;
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
					reorderGateways();
				}
				addLstTitle(getText("config.title.vpn.service.edit") + " '"
						+ getChangedName() + "'");
				hideCreateItem = "";
				hideNewButton = "none";
				hideCreateTunnelException = "";
				hideNewTunnelException = "none";
				return returnResultKeyWord(returnWord,"vpnOnly");
			} else if ("update".equals(operation)) {
				if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT,
							new String[] { "Update" });
				}
				if (dataSource == null || !checkCertificateValid() || !checkCredentialCounts()) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"vpnOnly");
				}
				
				String errorMsg = validateIpPoolCapability(getDataSource());
				if (null != errorMsg && !"".equals(errorMsg)) {
					addActionError(errorMsg);
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"vpnOnly");
				}
				prepareSaveObjects();
				// create capwapIp object if needed;
				IpAddress dnsIp = autoCreateIpAddress();
				if (null != dnsIp) {
					getDataSource().setDnsIp(dnsIp);
				}
				updateDependentObjects();
				
				/*if (isJsonMode()){
					setId(dataSource.getId());
				}*/
				String returnWordString = updateBo();
				if (isJsonMode()){
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					jsonObject.put("n", true);
					jsonObject.put("id", dataSource.getId());
					return "json";
				} else {
					return returnWordString;
				}
			} else if (("update" + getLstForward()).equals(operation)) {
				if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT,
							new String[] { "Update" });
				}
				String errorMsg = validateIpPoolCapability(getDataSource());
				if (null != errorMsg && !"".equals(errorMsg)) {
					addActionError(errorMsg);
					prepareDependentObjects();
					return INPUT;
				}
				if (dataSource != null) {
					prepareSaveObjects();
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				VpnService profile = (VpnService) findBoById(boClass, cloneId,
						this);
				if (null != profile) {
					profile.setOwner(null);
					profile.setId(null);
					profile.setVersion(null);
					profile.setProfileName("");
					setCloneFields(profile, profile);
					setSessionDataSource(profile);
					prepareDependentObjects();
					hideCreateItem = "";
					hideNewButton = "none";
					hideCreateTunnelException = "";
					hideNewTunnelException = "none";
					addLstTitle(getText("config.title.vpn.service"));
					return INPUT;
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("newDnsIp".equals(operation)
					|| "editDnsIp".equals(operation)
					|| "importCa".equals(operation)
					|| "importCert".equals(operation)
					|| "importKey".equals(operation)
					|| "vpnGateWayEdit".equals(operation)
					|| "newIpAddress".equals(operation)
					|| "editIpAddress".equals(operation)) {
				if ("importCa".equals(operation)) {
					getDataSource().setImportFileType("ca");
				} else if ("importCert".equals(operation)) {
					getDataSource().setImportFileType("cert");
				} else if ("importKey".equals(operation)) {
					getDataSource().setImportFileType("key");
				}
				prepareSaveObjects();
				clearErrorsAndMessages();
				addLstForward("vpnService");
				return operation;
			} else if ("continue".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (dataSource == null) {
					return prepareBoList();
				} else {
					if (null != importFileName && !"".equals(importFileName)) {
						if ("ca".equals(getDataSource().getImportFileType())) {
							getDataSource().setRootCa(importFileName);
						} else if ("cert".equals(getDataSource()
								.getImportFileType())) {
							getDataSource().setCertificate(importFileName);
						} else if ("key".equals(getDataSource()
								.getImportFileType())) {
							getDataSource().setPrivateKey(importFileName);
						}
					}
					prepareSaveObjects();
					prepareDependentObjects();
					setId(dataSource.getId());
					hideCreateItem = "";
					hideNewButton = "none";
					return INPUT;
				}
			} else if ("generateCredential".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", credentialCount:" + credentialCount);
				jsonArray = generateCredentials(credentialCount);
				return "json";
			} else if ("removeCredential".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", credentialIds:" + credentialIds);
				jsonArray = removeCredentials(credentialIds);
				return "json";
			} else if ("updateCredential".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", credentialIds:" + credentialIds);
				jsonArray = updateCredentials(credentialIds);
				return "json";
			} else if ("initVpnTopologyPanel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id
						+ ", pageId:" + pageId);
				VpnTopologyCache vpnTopologyCache = getVpnTopologyCache();
				vpnTopologyCache.setServiceId(id, pageId);
				initVpnTopology(id);
				return "vpnTopology";
			} else if ("vpnTopologys".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id
						+ ", pageId:" + pageId + ", mapId:" + mapId);
				VpnTopologyCache vpnTopologyCache = getVpnTopologyCache();
				jsonObject = prepareTopologys(id, pageId, mapId,
						vpnTopologyCache);
				if(null != id){
					VpnService vpn = QueryUtil.findBoById(VpnService.class,
							id,this);
					if(null != vpn){
						jsonObject.put("primaryTunnel", vpn.getPrimaryTunnelString());
						jsonObject.put("backupTunnel", vpn.getBackupTunnelString());
					}
				}
				
				return "json";
			} else if ("pollVpnTopology".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id
						+ ", pageId:" + pageId + ", mapId:" + mapId);
				VpnTopologyCache vpnTopologyCache = getVpnTopologyCache();
				jsonObject = prepareTopologys(id, pageId, mapId,
						vpnTopologyCache);
				
				if(null != id){
					VpnService vpn = QueryUtil.findBoById(VpnService.class,
							id,this);
					if(null != vpn){
						jsonObject.put("primaryTunnel", vpn.getPrimaryTunnelString());
						jsonObject.put("backupTunnel", vpn.getBackupTunnelString());
					}
				}
				return "json";
			} else if ("refreshVpnTopologys".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id
						+ ", pageId:" + pageId + ", mapId:" + mapId);
				VpnTopologyCache vpnTopologyCache = getVpnTopologyCache();
				jsonObject = refreshVpnTopologys(id, pageId, vpnTopologyCache);
				
				if(null != id){
					VpnService vpn = QueryUtil.findBoById(VpnService.class,
							id,this);
					if(null != vpn){
						jsonObject.put("primaryTunnel", vpn.getPrimaryTunnelString());
						jsonObject.put("backupTunnel", vpn.getBackupTunnelString());
					}
				}
				return "json";
			}else if("viewHiveApInfo".equals(operation)){
				//Get Hive_Ap information when change selected vpn server
				log.info("execute", "operation:" + operation + ", id:" + id);
				jsonObject =  getAvailableHiveApInfo(id);
				return "json";
			}else if("addVpnGateWay".equals(operation)){
				//valid if the VpnGateway has been applied
				if(!addSingleVpnGateway()){
					addActionError(MgrUtil
							.getUserMessage("error.addObjectVpnGateWayExists"));
					prepareDependentObjects();
				}else if(!addSameTypeVpnGateway()){
					addActionError(MgrUtil
							.getUserMessage("gotham_06.error.addObject.vpnGateWay.typeDifferent"));
					prepareDependentObjects();
				}else{
					prepareDependentObjects();
					addVpnGateWaySetting();
				}
				
				hideCreateTunnelException = "";
				hideNewTunnelException = "none";
				return returnResultKeyWord(INPUT,"vpnOnly");
			}else if("removeVpnGateWay".equals(operation) || "removeVpnGateWayNone".equals(operation)){
				hideCreateItem = "removeVpnGateWayNone".equals(operation) ? "": "none";
				hideNewButton = "removeVpnGateWayNone".equals(operation) ? "none": "";
				prepareDependentObjects();
				removeVpnGateWaySetting();
				hideCreateTunnelException = "";
				hideNewTunnelException = "none";
				return returnResultKeyWord(INPUT,"vpnOnly");
			}else if("fetchVPNNodes".equals(operation)){
				fetchVPNNodes();
				return "json";
			}else {
				baseOperation();
				if(isJsonMode()){
					return "vpnOnly";			
				}
				return prepareBoList();
			}
		} catch (Exception e) {
			if (isJsonMode()) {
				log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				prepareDependentObjects();
				return "vpnOnly";
			}
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_VPN_SERVICE);
		setDataSource(VpnService.class);
		keyColumnId = COLUMN_PROFILE_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_VPN_SERVICE;
		
		// avoid cancel back error when open two edit view 
		if(L2_FEATURE_VPN_SERVICE.equals(request.getParameter("operation"))
		        || "create".equals(request.getParameter("operation")) 
		        || ("create" + getLstForward()).equals(request.getParameter("operation"))
		        || "update".equals(request.getParameter("operation")) 
		        || ("update"+ getLstForward()).equals(request.getParameter("operation"))) {
		    if(null == getDataSource()) {
		        setSessionDataSource(new VpnService());
		    }
		}
	}

	@Override
	public VpnService getDataSource() {
		return (VpnService) dataSource;
	}

	@Override
	protected void updateConfigTemplate() throws Exception {
		ConfigTemplate defaultTemplate = HmBeParaUtil
				.getEasyModeDefaultTemplate(domainId);
		defaultTemplate.setVpnService(null);
		QueryUtil.updateBo(defaultTemplate);
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		VpnService source = QueryUtil.findBoById(VpnService.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<VpnService> list = QueryUtil.executeQuery(VpnService.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (VpnService profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			VpnService vs = source.clone();
			if (null == vs) {
				continue;
			}
			setCloneFields(source, vs);
			vs.setId(profile.getId());
			vs.setVersion(profile.getVersion());
			vs.setProfileName(profile.getProfileName());
			vs.setOwner(profile.getOwner());

			String errors = validateIpPoolCapability(vs);
			if (null != errors) {
				addActionError(errors);
				return null;
			}
			hmBos.add(vs);
		}
		return hmBos;
	}

	private void setCloneFields(VpnService source, VpnService destination) {
		// set credential
		List<VpnServiceCredential> list = new ArrayList<>();
		for (VpnServiceCredential sc : source.getVpnCredentials()) {
			sc.setAllocated(false);
			sc.setAssignedClient(null);
			sc.setPrimaryRole(VpnServiceCredential.SERVER_ROLE_NONE);
			sc.setBackupRole(VpnServiceCredential.SERVER_ROLE_NONE);
			list.add(sc);
		}
		destination.setVpnCredentials(list);
		//remove CVG when cloning a VPN service
		List<VpnGatewaySetting> vpnGatewayList = new ArrayList<>();
		destination.setVpnGateWaysSetting(vpnGatewayList);
		
		List<UserProfileForTrafficL3> userProfileForTrafficL3s = new ArrayList<>();
		for(UserProfileForTrafficL3 userProfileForTrafficL3:source.getUserProfileTrafficL3()){
			userProfileForTrafficL3s.add(userProfileForTrafficL3);
		}
		destination.setUserProfileTrafficL3(userProfileForTrafficL3s);
		
		List<UserProfileForTrafficL2> userProfileForTrafficL2s = new ArrayList<>();
		for(UserProfileForTrafficL2 userProfileForTrafficL2:source.getUserProfileTrafficL2()){
			userProfileForTrafficL2s.add(userProfileForTrafficL2);
		}
		destination.setUserProfileTrafficL2(userProfileForTrafficL2s);
	}

	public String getChangedName() {
		return getDataSource().getProfileName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getProfileNameLength() {
		return getAttributeLength("profileName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public int getServerPrivateIp1Length() {
		return getAttributeLength("serverPrivateIp1");
	}
	
	public int getServerDefaultGateway1Length() {
		return getAttributeLength("serverDeaultGateway1");
	}
	
	public int getServerDefaultGateway2Length() {
		return getAttributeLength("serverDeaultGateway2");
	}
	
	public int getServerPublicIp1Length() {
		return getAttributeLength("serverPublicIp1");
	}

	public int getClientIpPoolStart1Length() {
		return getAttributeLength("clientIpPoolStart1");
	}

	public int getClientIpPoolEnd1Length() {
		return getAttributeLength("clientIpPoolEnd1");
	}

	public int getClientIpPoolNetmask1Length() {
		return getAttributeLength("clientIpPoolNetmask1");
	}

	public int getServerPrivateIp2Length() {
		return getAttributeLength("serverPrivateIp2");
	}

	public int getServerPublicIp2Length() {
		return getAttributeLength("serverPublicIp2");
	}

	public int getClientIpPoolStart2Length() {
		return getAttributeLength("clientIpPoolStart2");
	}

	public int getClientIpPoolEnd2Length() {
		return getAttributeLength("clientIpPoolEnd2");
	}

	public int getClientIpPoolNetmask2Length() {
		return getAttributeLength("clientIpPoolNetmask2");
	}

	public EnumItem[] getPhase1AuthMethods() {
		return VpnService.PHASE1_AUTH_METHOD;
	}

	public EnumItem[] getPhase1EncrypAlgs() {
		return VpnService.PHASE1_ENCRYP_ALG;
	}

	public EnumItem[] getPhase1Hashs() {
		return VpnService.PHASE1_HASH;
	}

	public EnumItem[] getPhase1DhGroups() {
		return VpnService.PHASE1_DH_GROUP;
	}

	public EnumItem[] getPhase2EncrypAlgs() {
		return VpnService.PHASE2_ENCRYP_ALG;
	}

	public EnumItem[] getPhase2Hashs() {
		return VpnService.PHASE2_HASH;
	}

	public EnumItem[] getPhase2PfsGroups() {
		return VpnService.PHASE2_PFS_GROUP;
	}

	public EnumItem[] getIkeIds() {
		return VpnService.IKE_ID;
	}

	public Range getPhase1LifeTimeRange() {
		return getAttributeRange("phase1LifeTime");
	}

	public Range getPhase2LifeTimeRange() {
		return getAttributeRange("phase2LifeTime");
	}

	public Range getDpdIdelIntervalRange() {
		return getAttributeRange("dpdIdelInterval");
	}

	public Range getDpdRetryRange() {
		return getAttributeRange("dpdRetry");
	}

	public Range getDpdRetryIntervalRange() {
		return getAttributeRange("dpdRetryInterval");
	}

	public Range getAmrpIntervalRange() {
		return getAttributeRange("amrpInterval");
	}

	public Range getAmrpRetryRange() {
		return getAttributeRange("amrpRetry");
	}

	public String getVpnServer2SettingStyle() {
		if (getDataSource().getVpnServerType() == VpnService.VPN_SERVER_TYPE_REDUNDANT) {
			return "";
		}
		return "none";
	}
	
	public String getIpsecVpnLayer2Type(){
		if (getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2) {
			return "";
		}
		return "none";
	}
	
	public String getIpsecVpnLayer3Type(){
		if (getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3) {
			return "";
		}
		return "none";
	}
	
	public String getRouteTrafficTypeShow(){
		if(getDataSource().getRouteTrafficType() == VpnService.ROUTE_VPNTUNNEL_TRAFFIC_ALL){
			return "";
		}
		return "none";
	}
	
	public String getRouteOnlyInternalShow(){
		if(getDataSource().getRouteTrafficType() == VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL){
			for(UserProfileForTrafficL3 userProfileForTrafficL3:getDataSource().getUserProfileTrafficL3()){
				if(userProfileForTrafficL3.getVpnTunnelBehavior()== UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS
						|| userProfileForTrafficL3.getVpnTunnelBehavior()== UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL){
					return "";
				}
			}
		}
		return "none";
	}
	
	private boolean checkCertificateValid() {
		// check if the certificate file valid
		try {
			if (!HmBeAdminUtil
					.verifyCertificate(getDataSource().getRootCa(),
							getDataSource().getCertificate(), getDataSource()
									.getPrivateKey(), null, getDomain()
									.getDomainName(),true)) {
				addActionError(MgrUtil
						.getUserMessage("error.radius.checkCertificateFile"));
				return false;
			}

			// check certificate has configured ike id
			if (getDataSource().isIkeValidation()) {
				SubjectAltname_st result = HmBeAdminUtil.getSubjetAltName(
						getDataSource().getCertificate(), getDomain()
								.getDomainName());
				if (null == result) {
					addActionError(MgrUtil
							.getUserMessage("error.vpn.certificate.parse"));
					return false;
				}
				if (!result.is_ok()) {
					String msg = result.getErrorMsg();
					if (null == msg || "".equals(msg)) {
						msg = MgrUtil
								.getUserMessage("error.vpn.certificate.parse");
					}
					addActionError(msg);
					return false;
				}
				int type = getDataSource().getServerIkeId();
				switch (type) {
				case VpnService.IKE_ID_ADDRESS:
					List<String> ip = result.getIpRslt();
					if (null == ip || ip.isEmpty()) {
						addActionError(MgrUtil
								.getUserMessage("error.vpn.certificate.no.ip"));
						return false;
					}
					break;
				case VpnService.IKE_ID_ASN1DN:
					String asn1dn = result.getAsn1dn();
					if (null == asn1dn || "".equals(asn1dn)) {
						addActionError(MgrUtil
								.getUserMessage("error.vpn.certificate.no.asn1dn"));
						return false;
					}
					break;
				case VpnService.IKE_ID_FQDN:
					List<String> dns = result.getDnsRslt();
					if (null == dns || dns.isEmpty()) {
						addActionError(MgrUtil
								.getUserMessage("error.vpn.certificate.no.FQDN"));
						return false;
					}
					break;
				case VpnService.IKE_ID_UFQDN:
					List<String> email = result.getEmailRslt();
					if (null == email || email.isEmpty()) {
						addActionError(MgrUtil
								.getUserMessage("error.vpn.certificate.no.UserFQDN"));
						return false;
					}
					break;
				}
			}
		} catch (BeOperateException boe) {
			addActionError(boe.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean checkCredentialCounts(){
		int counts = getCredentialCount();
		if(isExistsCvgDeviceAsVpnServer()){
			if(counts > VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE){
				addActionError(MgrUtil.getUserMessage("gotham_06.error.vpn.credentials.tooManyUsers.cvgDevice", 
						new String[]{String.valueOf(VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE)}));
				return false;
			}
		}else if(isExistsCvgAsVpnServer()){
			if(counts > VpnService.MAX_IP_POOL_SIZE){
				addActionError(MgrUtil.getUserMessage("error.vpn.credentials.tooManyUsers.cvg", 
						new String[]{String.valueOf(VpnService.MAX_IP_POOL_SIZE)}));
				return false;
			}
		}else{
			if(counts > VpnService.MAX_IP_POOL_SIZE_VPN2){
				addActionError(MgrUtil.getUserMessage("error.vpn.credentials.tooManyUsers.ap", 
						new String[]{String.valueOf(VpnService.MAX_IP_POOL_SIZE_VPN2)}));
				return false;
			}
		}
		return true;
	}
	
	private boolean isExistsCvgDeviceAsVpnServer(){
		if(getDataSource() == null){
			return false;
		}
		
		List<Long> vpnServerList = new ArrayList<Long>();

		if(getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
			if(getDataSource().getHiveApVpnServer1() != null){
				vpnServerList.add(getDataSource().getHiveApVpnServer1());
			}
			if(getDataSource().getHiveApVpnServer2() != null){
				vpnServerList.add(getDataSource().getHiveApVpnServer2());
			}
		}else{
			if(getDataSource().getVpnGateWaysSetting() != null){
				for(VpnGatewaySetting setting : getDataSource().getVpnGateWaysSetting()){
					vpnServerList.add(setting.getApId());
				}
			}
		}
		
		if(vpnServerList.isEmpty()){
			return false;
		}
		
		List<?> resultList = QueryUtil.executeQuery("select count(id) from "+HiveAp.class.getSimpleName(), null, 
				new FilterParams("id in (:s1) and hiveApModel = :s2", new Object[]{vpnServerList, HiveAp.HIVEAP_MODEL_VPN_GATEWAY}));
		
		return Integer.valueOf(resultList.get(0).toString()) > 0;
	}
	
	
	private boolean isExistsCvgAsVpnServer(){
		if(getDataSource() == null){
			return false;
		}
		
		List<Long> vpnServerList = new ArrayList<Long>();
		if(getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
			if(getDataSource().getHiveApVpnServer1() != null){
				vpnServerList.add(getDataSource().getHiveApVpnServer1());
			}
			if(getDataSource().getHiveApVpnServer2() != null){
				vpnServerList.add(getDataSource().getHiveApVpnServer2());
			}
		}else{
			if(getDataSource().getVpnGateWaysSetting() != null){
				for(VpnGatewaySetting setting : getDataSource().getVpnGateWaysSetting()){
					vpnServerList.add(setting.getApId());
				}
			}
		}
		
		if(vpnServerList.isEmpty()){
			return false;
		}
		List<?> resultList = QueryUtil.executeQuery("select count(id) from "+HiveAp.class.getSimpleName(), null, 
				new FilterParams("id in (:s1) and hiveApModel = :s2", new Object[]{vpnServerList, HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA}));
		
		return Integer.valueOf(resultList.get(0).toString()) > 0;
	}

	/*
	 * not used, currently, if server is bind on HiveAP, the server field will
	 * be disabled.
	 */
	@SuppressWarnings("unused")
	private boolean checkServerIpValid() {
		Set<Long> set = ConfigurationUtils.getRelevantVpnServers(
				getDataSource());
		String server1 = getDataSource().getServerPrivateIp1();
		String server2 = getDataSource().getServerPrivateIp2();
		if (!set.isEmpty()) {
			String query = "select hostName, cfgIpAddress from "
					+ HiveAp.class.getSimpleName();
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams("id", set), domainId);
			for (Object object : list) {
				Object[] attr = (Object[]) object;
				String hn = (String) attr[0];
				String ip = (String) attr[1];
				if (null == ip || (!ip.equals(server1) && !ip.equals(server2))) {
					addActionError(MgrUtil.getUserMessage(
							"error.update.vpn.server", new String[] { hn, ip }));
					return false;
				}
			}
		}
		return true;
	}

	private Long dnsIp;
	private List<CheckItem> ipAddresses;
	private List<String> certificates = new ArrayList<>();// empty list
	private String importFileName;
	private int credentialCount;
	private String credentialIds;
	private String vpnProfileName;
	private Long pageId;
	private Long mapId;
	
	
	//added for Congo HM FS
	private List<CheckItem> availableHiveApsList;
	private List<CheckItem> availableDomainObjects;
	//private List<CheckItem> userProfilesWithInternalNetworks;
	private List<CheckItem> vpnGateWaysList;
	
	private String hideNewButton = "";
	private String hideCreateItem = "none";
	
	//used for vpnGateWay
	private String[] externalIpAddresses;
	private String externalIpAddress = "";
	private String vpnGateWay;
	private Collection<String> vpnGatewayIndices;
	private Collection<String> tunnelExceptionIndices;
	private Long vpnGateWayId;
	private int[] ordering;
	
	
	
	//used for transfer value when save user profile traffic setting
    private String userProfilesTrafficForL2Str;
    private String userProfilesTrafficForL3Str;
    
    private short[] vpnTunnelModesL2;
    private short[] vpnTunnelModesL3;
    
    private String hideNewTunnelException = "";
    private String hideCreateTunnelException = "none";
    
    private String vpnTunnelUserProfilesSelect;
    private String vpnTunnelBehaviorMode;
    private String vpnTunnelBehaviorModeAll;
    private String vpnTunnelBehaviorModeSplit;
    private Long domObjId;

	public String getVpnGateWay() {
		return vpnGateWay;
	}

	public void setVpnGateWay(String vpnGateWay) {
		this.vpnGateWay = vpnGateWay;
	}

	public String getHideCreateItem()
	{
		return hideCreateItem;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public String getVpnProfileName() {
		return vpnProfileName;
	}

	public void setCredentialIds(String credentialIds) {
		this.credentialIds = credentialIds;
	}

	public void setCredentialCount(int credentialCount) {
		this.credentialCount = credentialCount;
	}

	public int getCredentialCount() {
		if (null != getDataSource()) {
			credentialCount = getDataSource().getVpnCredentials().size();
		}
		return credentialCount;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

	public Long getDnsIp() {
		return dnsIp;
	}

	public void setDnsIp(Long dnsIp) {
		this.dnsIp = dnsIp;
	}

	public List<CheckItem> getIpAddresses() {
		return ipAddresses;
	}

	public List<String> getCertificates() {
		return certificates;
	}

	private void prepareSaveObjects() throws Exception {
		if (null != dnsIp) {
			IpAddress ip = findBoById(IpAddress.class, dnsIp);
			if (null == ip && dnsIp != -1) {
				String tempStr[] = { getText("config.vpn.service.client.dns.server") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setDnsIp(ip);
		}
		//when select the layer 2 ipsec vpn
		if(getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
			if (getDataSource().getVpnServerType() == VpnService.VPN_SERVER_TYPE_SINGLE) {
				getDataSource().setServerPrivateIp2("");
				getDataSource().setServerPublicIp2("");
				getDataSource().setClientIpPoolEnd2("");
				getDataSource().setClientIpPoolNetmask2("");
				getDataSource().setClientIpPoolStart2("");
				getDataSource().setServerDeaultGateway2("");
			}
		}else{
			//when select the layer 3 ipsec vpn
			getDataSource().setServerPrivateIp1("");
			getDataSource().setServerPublicIp1("");
			getDataSource().setClientIpPoolEnd1("");
			getDataSource().setClientIpPoolNetmask1("");
			getDataSource().setClientIpPoolStart1("");
			getDataSource().setServerPrivateIp2("");
			getDataSource().setServerPublicIp2("");
			getDataSource().setClientIpPoolEnd2("");
			getDataSource().setClientIpPoolNetmask2("");
			getDataSource().setClientIpPoolStart2("");
			//save white list 
			if (null != domObjId && domObjId > -1) {
				DomainObject domobj = findBoById(DomainObject.class, domObjId);
				if (null == domobj && domObjId != -1) {
					String tempStr[] = { getText("config.vpn.service.userProfiles.whitelist.tunnel.exepmtion") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setDomObj(domobj);
			} else{
				getDataSource().setDomObj(null);
			}
			reorderGateways();
		}
	}

	private void prepareDependentObjects() {
		if (null != getDataSource().getDnsIp()) {
			dnsIp = getDataSource().getDnsIp().getId();
			getDataSource().setInputText(
					getDataSource().getDnsIp().getAddressName());
		}
		prepareIpAddresses();
		prepareAvailableHiveAps();
		prepareVpnGateWays();
		//prepareUserProfilesWithInternalNetworks();
		prepareUserProfileForTraffic();
		prepareAvailableDomainObjects();
		prepareCertificates();
	}
	
	private void prepareUserProfileForTraffic(){
		prepareUserProfileForTrafficL2();
		//prepareUserProfileForTrafficL3();
		//set vpnType
		if(getDataSource().getIpsecVpnType() == 0){
			if(wirelessRoutingEnabled){
				getDataSource().setIpsecVpnType(VpnService.IPSEC_VPN_LAYER_3);
				getDataSource().setRouteTrafficType(VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL);
			}else{
				getDataSource().setIpsecVpnType(VpnService.IPSEC_VPN_LAYER_2);
			}	
		}
	}
	
	private void updateDependentObjects(){
		if(getDataSource().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
			String strsTemp[];
			if (userProfilesTrafficForL2Str != null && !"".equals(userProfilesTrafficForL2Str)) {
				strsTemp = userProfilesTrafficForL2Str.split(",");
				if(strsTemp != null){
			        for(int i=0;i< getDataSource().getUserProfileTrafficL2().size();i++){
						String[] temp = strsTemp[i].split("_");
						if("2".equals(temp[0])){
							getDataSource().getUserProfileTrafficL2().get(i).setVpnTunnelModeL2(UserProfileForTrafficL2.VPNTUNNEL_MODE_ENABLED);
							if(temp.length >1){
								if("tunnelAll".equals(temp[1]) || "splitTunnel".equals(temp[1])){
									getDataSource().getUserProfileTrafficL2().get(i).setTunnelSelected(temp[1]);
								}
							}else{
								getDataSource().getUserProfileTrafficL2().get(i).setTunnelSelected("");
							}
						}else{
							getDataSource().getUserProfileTrafficL2().get(i).setVpnTunnelModeL2(UserProfileForTrafficL2.VPNTUNNEL_MODE_DISABLED);
							getDataSource().getUserProfileTrafficL2().get(i).setTunnelSelected("");
						}
			        }
				}
			}
		}else{
			updateGateWaySetting();
		}
	}

	public EnumItem[] getVpnServerTypeSingle() {
		return new EnumItem[] { new EnumItem(VpnService.VPN_SERVER_TYPE_SINGLE,
				getText("config.vpn.service.option.single")) };
	}

	public EnumItem[] getVpnServerTypeRedundant() {
		return new EnumItem[] { new EnumItem(
				VpnService.VPN_SERVER_TYPE_REDUNDANT,
				getText("config.vpn.service.option.redundant")) };
	}
	
	public EnumItem[] getIPsecVpnLayer2() {
		return new EnumItem[] { new EnumItem(VpnService.IPSEC_VPN_LAYER_2,
				getText("config.vpn.service.option.ipsec.vpn.layer2")) };
	}

	public EnumItem[] getIPsecVpnLayer3() {
		return new EnumItem[] { new EnumItem(
				VpnService.IPSEC_VPN_LAYER_3,
				getText("config.vpn.service.option.ipsec.vpn.layer3")) };
	}
	
	public EnumItem[] getRouteTrafficAll() {
		return new EnumItem[] { new EnumItem(VpnService.ROUTE_VPNTUNNEL_TRAFFIC_ALL,
				getText("config.vpn.service.route.vpntunnel.traffic.all")) };
	}

	public EnumItem[] getRouteTrafficInternal() {
		return new EnumItem[] { new EnumItem(
				VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL,
				getText("config.vpn.service.route.vpntunnel.traffic.internal")) };
	}
	
	public EnumItem[] getProfileTunnelAllL2() {
		return new EnumItem[] { new EnumItem(
				VpnService.USER_PROFILES_TUNNEL_ALL,
				"") };
	}

	public EnumItem[] getProfileSplitTunnelL2() {
		return new EnumItem[] { new EnumItem(
				VpnService.USER_PROFILES_SPLIT_TUNNEL,
				"") };
	}

	public EnumItem[] getProfileTunnelAllL3() {
		return new EnumItem[] { new EnumItem(
				VpnService.USER_PROFILES_TUNNEL_ALLL3,
				"") };
	}

	public EnumItem[] getProfileSplitTunnelL3() {
		return new EnumItem[] { new EnumItem(
				VpnService.USER_PROFILES_SPLIT_TUNNELL3,
				"") };
	}
	
	private void prepareIpAddresses() {
		ipAddresses = getIpObjectsBySingleIp(CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
	}

	public boolean getDisablePrivateServer1() {
		if (null != getDataSource() && null != getDataSource().getId()) {
			String where = "bo.configTemplate.vpnService.id = :s1 and bo.vpnMark = :s2";
			Object[] values = new Object[] { getDataSource().getId(),
					HiveAp.VPN_MARK_SERVER };
			List<?> list = QueryUtil.executeQuery(
					"select bo.cfgIpAddress from "
							+ HiveAp.class.getSimpleName() + " bo", null,
					new FilterParams(where, values));

			for (Object object : list) {
				String cfgIpAddress = (String) object;
				if (null != cfgIpAddress
						&& !"".equals(cfgIpAddress)
						&& cfgIpAddress.equals(getDataSource()
								.getServerPrivateIp1())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean getDisablePrivateServer2() {
		if (null != getDataSource() && null != getDataSource().getId()) {
			String where = "bo.configTemplate.vpnService.id = :s1 and bo.vpnMark = :s2";
			Object[] values = new Object[] { getDataSource().getId(),
					HiveAp.VPN_MARK_SERVER };
			List<?> list = QueryUtil.executeQuery(
					"select bo.cfgIpAddress from "
							+ HiveAp.class.getSimpleName() + " bo", null,
					new FilterParams(where, values));

			for (Object object : list) {
				String cfgIpAddress = (String) object;
				if (null != cfgIpAddress
						&& !"".equals(cfgIpAddress)
						&& cfgIpAddress.equals(getDataSource()
								.getServerPrivateIp2())) {
					return true;
				}
			}
		}
		return false;
	}

	private void prepareCertificates() {
		/*String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			return;
		}*/
		List<String> listFile = HmBeAdminUtil.getCAFileList(CacheMgmt
				.getInstance().getCacheDomainById(domainId).getDomainName());
		if (null == listFile || listFile.isEmpty()) {
			listFile = new ArrayList<>();
			listFile.add("test1");
			listFile.add("test2");
			listFile.add("test3");
		}
		certificates = listFile;
	}
	
	private void prepareAvailableHiveAps(){
		List<Short> vpnSupportModel = DevicePropertyManage.getInstance().getSupportDeviceList(DeviceInfo.SPT_VPN_SERVICE_SERVER);
		
		availableHiveApsList = getBoCheckItems("hostName", HiveAp.class, 
				new FilterParams("managestatus=:s1 and devicetype != :s2 and devicetype != :s3 and hiveapmodel in :s4",
						new Object[]{HiveAp.STATUS_MANAGED,HiveAp.Device_TYPE_BRANCH_ROUTER,HiveAp.Device_TYPE_SWITCH, vpnSupportModel}),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}
	
	private void prepareVpnGateWays(){
		vpnGateWaysList = getBoCheckItems("hostName", HiveAp.class, 
				new FilterParams("(devicetype=:s1 or devicetype=:s2)", new Object[]{HiveAp.Device_TYPE_VPN_GATEWAY, HiveAp.Device_TYPE_VPN_BR}),
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
		
		//remove all the vpngateways that the managestatus is new
		/*List<CheckItem> manageStatusNewList = new ArrayList<CheckItem>();
		manageStatusNewList = getBoCheckItems("hostName", HiveAp.class, 
				new FilterParams("managestatus",HiveAp.STATUS_NEW),
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
		if(!manageStatusNewList.isEmpty()){
			vpnGateWaysList.removeAll(manageStatusNewList);
		}*/
		
		//remove the vpnGateWays that used in other vpnservices
		List<CheckItem> removeVpnGateWaysList = new ArrayList<>();
		List<VpnService> vpnServiceList =QueryUtil.executeQuery(VpnService.class,null,null,domainId,this);
		
		for(VpnService vpnService:vpnServiceList){
			if(getDataSource() != null && getDataSource().getId()!= null 
					&& !getDataSource().getId().equals(vpnService.getId())){
				List<VpnGatewaySetting> vpnGatewaySettingsList = vpnService.getVpnGateWaysSetting();
				for(VpnGatewaySetting vpnGatewaySetting:vpnGatewaySettingsList){
					CheckItem checkItem = new CheckItem(vpnGatewaySetting.getApId(), null);
					if(!removeVpnGateWaysList.contains(checkItem)){
						removeVpnGateWaysList.add(checkItem);
					}
				}
			}
			if(getDataSource() != null && getDataSource().getId() == null){
				List<VpnGatewaySetting> vpnGatewaySettingsList = vpnService.getVpnGateWaysSetting();
				for(VpnGatewaySetting vpnGatewaySetting:vpnGatewaySettingsList){
					CheckItem checkItem = new CheckItem(vpnGatewaySetting.getApId(), null);
					if(!removeVpnGateWaysList.contains(checkItem)){
						removeVpnGateWaysList.add(checkItem);
					}
				}
			}
		}
		vpnGateWaysList.removeAll(removeVpnGateWaysList);
		
		if(vpnGateWaysList.isEmpty()){
			vpnGateWaysList.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
						.getUserMessage("config.optionsTransfer.none")));
		}
		 //prepare vpn gateway setting
		int count = getDataSource().getVpnGateWaysSetting().size() - 1;
	    for(int index =count;index >-1; index --){
	    	HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, getDataSource().getVpnGateWaysSetting().get(index).getApId(),this);
	    	if(null != hiveAp){
	    		getDataSource().getVpnGateWaysSetting().get(index).setHiveAP(hiveAp);
	    	}else{
	    		getDataSource().getVpnGateWaysSetting().remove(index);
	    	}
	    	
	    }
	    //update external ipaddress
	    updateGateWaySetting();
	}
	
	/*private void prepareUserProfilesWithInternalNetworks(){
		userProfilesWithInternalNetworks = getBoCheckItems("userProfileName", UserProfile.class, 
				new FilterParams("networkObj.networkType",VpnNetwork.VPN_NETWORK_TYPE_INTERNAL),
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
	}*/
	
	private void prepareAvailableDomainObjects(){
		availableDomainObjects = getBoCheckItems("objName", DomainObject.class, 
				new FilterParams("objType", DomainObject.VPN_TUNNEL),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}
	
	private void prepareUserProfileForTrafficL2(){
		List<UserProfile> availableUserprofiles = QueryUtil.executeQuery(UserProfile.class,null,new FilterParams("defaultFlag", false) ,domainId,this);
	    List<UserProfile> userProfileTempAdd = new ArrayList<>();
	    
	    for(UserProfile userProfile:availableUserprofiles){
	    	 int index = 0; 
	    	 for(UserProfileForTrafficL2 userProfileForTrafficL2:getDataSource().getUserProfileTrafficL2()){
	 	    	if(null != userProfileForTrafficL2.getUserProfile() && userProfile.getId().equals(userProfileForTrafficL2.getUserProfile().getId())){
	 	    		userProfileForTrafficL2.setUserProfile(userProfile);
	 	    		break;
	 	    	}else{
	 	    		index ++;
	 	    	}
	 	     }
	    	 if(index == getDataSource().getUserProfileTrafficL2().size()){
	    		 userProfileTempAdd.add(userProfile);
	    	 }
		}

	    for(UserProfile userProfile:userProfileTempAdd){
	    	UserProfileForTrafficL2 userProfileForTrafficL2 = new UserProfileForTrafficL2();
			userProfileForTrafficL2.setUserProfile(userProfile);
			getDataSource().getUserProfileTrafficL2().add(userProfileForTrafficL2);
	    }
	}

	private IpAddress autoCreateIpAddress() {
		if ((null == dnsIp || dnsIp == -1)
				&& null != getDataSource().getInputText()
				&& !"".equals(getDataSource().getInputText())) {
			short ipType = ImportCsvFileAction
					.getIpAddressWrongFlag(getDataSource().getInputText()) ? IpAddress.TYPE_HOST_NAME
					: IpAddress.TYPE_IP_ADDRESS;
			return CreateObjectAuto
					.createNewIP(
							getDataSource().getInputText(),
							ipType,
							getDomain(),
							MgrUtil
									.getUserMessage("config.vpn.service.client.dns.server")
									+ " for VPN Service:"
									+ getDataSource().getProfileName());
		}
		return null;
	}
	
	private JSONArray generateCredentials(int count) throws JSONException {
		JSONArray array = new JSONArray();
		if (count > VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE) {// protect code!
			log.error("generateCredentials",
					"cannot generate so large count of credentials!!! count:"
							+ count);
			return array;
		}
		List<VpnServiceCredential> list = getDataSource().getVpnCredentials();
		Set<VpnServiceCredential> set = generate(count);
		for (VpnServiceCredential sc : set) {
			list.add(sc);
			JSONObject object = new JSONObject();
			object.put("pwd", getHtmlString(sc.getCredential()));
			array.put(object);
		}
		return array;
	}

	private JSONArray removeCredentials(String indexStr) throws JSONException {
		JSONArray array = new JSONArray();
		if (null != indexStr) {
			List<VpnServiceCredential> list = getDataSource()
					.getVpnCredentials();
			String[] indexes = indexStr.split(",");
			for (String strIndex : indexes) {
				int index = Integer.parseInt(strIndex);
				if (list.size() > index) {
					list.remove(index);
				}
				JSONObject object = new JSONObject();
				object.put("index", index);
				array.put(object);
			}
		}
		return array;
	}

	private JSONArray updateCredentials(String indexStr) throws JSONException {
		JSONArray array = new JSONArray();
		if (null != indexStr) {
			List<VpnServiceCredential> list = getDataSource()
					.getVpnCredentials();
			String[] indexes = indexStr.split(",");
			Set<VpnServiceCredential> set = generate(indexes.length);
			int i = 0;
			for (Iterator<VpnServiceCredential> it = set.iterator(); it
					.hasNext(); i++) {
				VpnServiceCredential next = it.next();
				int index = Integer.parseInt(indexes[i]);
				if (list.size() > index) {
					VpnServiceCredential sc = list.get(index);
					sc.setClientName(next.getClientName());
					sc.setCredential(next.getCredential());
					sc.setAllocated(false);
					JSONObject object = new JSONObject();
					object.put("index", index);
					object.put("pwd", getHtmlString(next.getCredential()));
					array.put(object);
				}
			}
		}
		return array;
	}

	private String getHtmlString(String str) {
		return str.replace("<", "&lt;").replace(">", "&gt;");
	}

	public static Set<VpnServiceCredential> generate(int count) {
		log.info("generate", "count:" + count);
		count = count > VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE ? VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE
				: count;// make sure count doesn't beyond MAX_IP_POOL_SIZE_VPN_CVG_DEVICE
		Set<VpnServiceCredential> set = new HashSet<>(count);
		while (set.size() < count) {// make sure credentials are unique
			String username = MgrUtil.getRandomString(32, 3);
			String password = MgrUtil.getRandomString(32, 7);
			VpnServiceCredential sc = new VpnServiceCredential();
			sc.setClientName(username);
			sc.setCredential(password);
			sc.setAllocated(false);
			set.add(sc);
		}
		return set;
	}

	public static String validateIpPoolCapability(Long wlanId, Long vpnServiceId) {
		if (null != wlanId && null != vpnServiceId) {
			ConfigTemplate template = new ConfigTemplate(ConfigTemplateType.WIRELESS);
			template.setId(wlanId);
			Set<Long> newClients = ConfigurationUtils.getRelevantVpnClients(template);

			VpnService vpn = QueryUtil.findBoById(VpnService.class,
					vpnServiceId, new VpnServiceAction());
			if (vpn.getIpsecVpnType()==VpnService.IPSEC_VPN_LAYER_3) {
				return "";
			}
			
			Set<Long> currentClients = ConfigurationUtils.getRelevantVpnClients(vpn);
			newClients.addAll(currentClients);
			int clientSize = newClients.size();
			int credentialSize = vpn.getVpnCredentials().size();

			if (clientSize > credentialSize) {
				return MgrUtil.getUserMessage(
						"error.update.vpn.client.outofIpPool", new String[] {
								vpn.getProfileName(),
								String.valueOf(credentialSize),
								String.valueOf(clientSize) });
			}
		}
		return "";
	}

	public static String verifyVpnClient(Long wlanId, Set<Long> updateAps) {
		if (null != wlanId) {
			ConfigTemplate wlan = QueryUtil.findBoById(ConfigTemplate.class,
					wlanId, new VpnServiceAction());
			VpnService clientService = wlan.getVpnService();
			if (clientService != null && clientService.getIpsecVpnType()==VpnService.IPSEC_VPN_LAYER_3) {
				return "";
			}
			if (null != clientService && null != updateAps) {
				int credentialsSize = clientService.getVpnCredentials().size();
				Set<Long> currentClient = ConfigurationUtils
						.getRelevantVpnClients(clientService);
				updateAps.addAll(currentClient);
				int clientSize = updateAps.size();
				if (clientSize > credentialsSize) {
					return MgrUtil.getUserMessage(
							"error.update.vpn.client.outofIpPool",
							new String[] { clientService.getProfileName(),
									String.valueOf(credentialsSize),
									String.valueOf(clientSize) });
				}
			}
		}
		return "";
	}

	private String validateIpPoolCapability(VpnService vpnService) {
		if (null != vpnService) {
			if (vpnService.getIpsecVpnType()==VpnService.IPSEC_VPN_LAYER_3) {
				return "";
			}
			int credentialSize = vpnService.getVpnCredentials().size();
			int clientSize = ConfigurationUtils.getRelevantVpnClients(vpnService).size();
			if (clientSize > credentialSize) {
				return MgrUtil.getUserMessage(
						"error.update.vpn.client.outofIpPool", new String[] {
								vpnService.getProfileName(),
								String.valueOf(credentialSize),
								String.valueOf(clientSize) });
			}
		}
		return "";
	}

	private void initVpnTopology(Long id) {
		if (null != id) {
			VpnService vpn = QueryUtil.findBoById(VpnService.class, id);
			if (null != vpn) {
				vpnProfileName = vpn.getProfileName();
			}
		}
	}

	private JSONObject refreshVpnTopologys(Long id, Long pageId,
			VpnTopologyCache vpnTopologyCache) throws JSONException {
		return vpnTopologyCache.refreshVpnTopologys(id, pageId);
	}

	private JSONObject prepareTopologys(Long id, Long pageId, Long mapId,
			VpnTopologyCache vpnTopologyCache) throws JSONException {
		return vpnTopologyCache.getVpnTopologys(id, pageId, mapId);
		// return prepareTestTopologys();
	}

	/*-
	private final int clientCount = 36;

	private JSONObject prepareTestTopologys() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("updated", true);
		JSONArray servers = new JSONArray();
		JSONObject server1 = new JSONObject();
		server1.put("apName", "HiveAP_VPN_SERVER1");
		server1.put("nodeId", "001977007777");
		server1.put("s", (int) Math.floor(Math.random() * 6));
		servers.put(server1);

		JSONObject server2 = new JSONObject();
		server2.put("apName", "HiveAP_VPN_SERVER2");
		server2.put("nodeId", "001977008888");
		server2.put("s", (int) Math.floor(Math.random() * 6));
		servers.put(server2);

		jsonObject.put("servers", servers);

		JSONArray clients = new JSONArray();
		for (int i = 0; i < clientCount; i++) {
			JSONObject client = new JSONObject();
			client.put("apName", "HiveAP_000" + i);
			client.put("nodeId", "00197700000" + i);
			client.put("s", (int) Math.floor(Math.random() * 6));
			clients.put(client);
		}
		jsonObject.put("clients", clients);

		JSONArray links = new JSONArray();
		for (int i = 0; i < clientCount; i++) {
			JSONObject link = new JSONObject();
			link.put("from", "001977007777");
			link.put("to", "00197700000" + i);
			link.put("connected", (Math.random() * 2 > 1));

			if ((Boolean) link.get("connected")) {
				links.put(link);
			}

			JSONObject link2 = new JSONObject();
			link2.put("from", "001977008888");
			link2.put("to", "00197700000" + i);
			link2.put("connected", (Math.random() * 2 > 1));

			if ((Boolean) link2.get("connected")) {
				links.put(link2);
			}
		}

		jsonObject.put("links", links);
		return jsonObject;
	}*/

	/*
	 * Only create 1 VPN topology cache per session.
	 */
	protected VpnTopologyCache getVpnTopologyCache() throws Exception {
		VpnTopologyCache vpnTopologyCache = (VpnTopologyCache) MgrUtil
				.getSessionAttribute(SessionKeys.VPN_TOPOLOGY_CACHE);
		if (vpnTopologyCache == null) {
			vpnTopologyCache = new VpnTopologyCache();
			MgrUtil.setSessionAttribute(SessionKeys.VPN_TOPOLOGY_CACHE,
					vpnTopologyCache);
		}
		return vpnTopologyCache;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_PROFILE_NAME = 1;
	public static final int COLUMN_CERT = 2;
	public static final int COLUMN_PRIVATE_KEY = 3;
	public static final int COLUMN_SERVER_PRIVATE_IP_1 = 4;
	public static final int COLUMN_SERVER_PUBLIC_IP_1 = 5;
	public static final int COLUMN_SERVER_PRIVATE_IP_2 = 6;
	public static final int COLUMN_SERVER_PUBLIC_IP_2 = 7;
	public static final int COLUMN_DESCRIPTION = 8;
	public static final int COLUMN_SERVER_POOL_START_1 = 9;
	public static final int COLUMN_SERVER_POOL_END_1 = 10;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return String -
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_PROFILE_NAME:
			code = "config.vpn.service.name";
			break;
		case COLUMN_CERT:
			code = "config.vpn.service.list.certificate";
			break;
		case COLUMN_PRIVATE_KEY:
			code = "config.vpn.service.list.privateKey";
			break;
		case COLUMN_SERVER_PRIVATE_IP_1:
			code = "config.vpn.service.server1.privateIp";
			break;
		case COLUMN_SERVER_PUBLIC_IP_1:
			code = "config.vpn.service.server1.publicIp";
			break;
		case COLUMN_SERVER_PRIVATE_IP_2:
			code = "config.vpn.service.server2.privateIp";
			break;
		case COLUMN_SERVER_PUBLIC_IP_2:
			code = "config.vpn.service.server2.publicIp";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.vpn.service.description";
			break;
		case COLUMN_SERVER_POOL_START_1:
			code = "config.vpn.service.list.server.ippool.start";
			break;
		case COLUMN_SERVER_POOL_END_1:
			code = "config.vpn.service.list.server.ippool.end";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(6);
		columns.add(new HmTableColumn(COLUMN_PROFILE_NAME));
		columns.add(new HmTableColumn(COLUMN_CERT));
		columns.add(new HmTableColumn(COLUMN_PRIVATE_KEY));
		columns.add(new HmTableColumn(COLUMN_SERVER_POOL_START_1));
		columns.add(new HmTableColumn(COLUMN_SERVER_POOL_END_1));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null != bo) {
			if (bo instanceof VpnService) {
				VpnService vpn = (VpnService) bo;
				
				if(vpn.getVpnCredentials() != null){
					vpn.getVpnCredentials().size();
				}
				
				if(vpn.getVpnGateWaysSetting() != null){
					vpn.getVpnGateWaysSetting().size();
					for(VpnGatewaySetting vpnGatewaySetting:vpn.getVpnGateWaysSetting()){
						if(vpnGatewaySetting.getHiveAP() != null){
							vpnGatewaySetting.getHiveAP().getId();
							if(vpnGatewaySetting.getHiveAP().getRoutingProfile() != null){
								vpnGatewaySetting.getHiveAP().getRoutingProfile().getId();
							}
						}
					}
				}
				if (vpn.getUserProfileTrafficL3() != null) {
					vpn.getUserProfileTrafficL3().size();
					for(UserProfileForTrafficL3 userProfileForTraffic:vpn.getUserProfileTrafficL3()){
						if(userProfileForTraffic.getUserProfile() != null){
							userProfileForTraffic.getUserProfile().getId();
						}
					}
				}
				if (vpn.getUserProfileTrafficL2() != null) {
					vpn.getUserProfileTrafficL2().size();
					for(UserProfileForTrafficL2 userProfileForTrafficL2:vpn.getUserProfileTrafficL2()){
						if(userProfileForTrafficL2.getUserProfile() != null){
							userProfileForTrafficL2.getUserProfile().getId();
						}
					}
				}
				if(vpn.getDomObj() != null){
					vpn.getDomObj().getId();
				}
				if(vpn.getOwner() != null){
					vpn.getOwner().getId();
				}
			} else if (bo instanceof ConfigTemplate) {
				ConfigTemplate wlan = (ConfigTemplate) bo;
				if (wlan.getVpnService() != null) {
					wlan.getVpnService().getVpnCredentials().size();
				}
			}else if(bo instanceof UserProfile){
				UserProfile userProfile = (UserProfile)bo;
				userProfile.getId();
			}else if(bo instanceof HiveAp){
				HiveAp hiveAp = (HiveAp)bo;
				if(hiveAp.getRoutingProfile() != null){
					hiveAp.getRoutingProfile().getId();
				}
				if (hiveAp.getDeviceInterfaces() != null){
					hiveAp.getDeviceInterfaces().values();
				}
			}
		}
		return null;
	}

	public List<CheckItem> getAvailableHiveApsList() {
		return availableHiveApsList;
	}
	
	public JSONObject getAvailableHiveApInfo(Long id){
		JSONObject object = new JSONObject();
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("id", id), domainId, this);
		try {
			if(list.isEmpty()){
				object.put("id", id);
			}
			
			for (HiveAp hiveAp : list){
				if(!hiveAp.isDhcp()){
						object.put("cfgGateway", hiveAp.getCfgGateway());
						object.put("serverPrivateIp", hiveAp.getIpAddress());
						object.put("dhcp", "false");
				
				}else{
					object.put("dhcp", "true");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public List<CheckItem> getVpnGateWaysList() {
		return vpnGateWaysList;
	}
	
	public String getHideNewButton()
	{
		return hideNewButton;
	}
	
	public int getGridCount() {
		if(getDataSource().getVpnGateWaysSetting() != null){
			return getDataSource().getVpnGateWaysSetting().isEmpty() ? 3 : 0;
		}else{
			return 3;
		}
	}
	
	public int getTunnelExceptionGridCount(){
		if(getDataSource().getUserProfileTrafficL3() != null){
			return getDataSource().getUserProfileTrafficL3().isEmpty() ? 3 : 0;
		}else{
			return 3;
		}
	}
	
	/**
	 * Add Vpn Gateway setting when select Ipsec VPN Layer 3
	 */
	public void addVpnGateWaySetting(){
		if(null != vpnGateWay && !"".equals(vpnGateWay)){
			VpnGatewaySetting vpnGatewaySetting = new VpnGatewaySetting();
			vpnGatewaySetting.setExternalIpAddress(externalIpAddress);
			vpnGatewaySetting.setApId(Long.parseLong(vpnGateWay));
			
			HiveAp ap = QueryUtil.findBoById(HiveAp.class, Long.parseLong(vpnGateWay), this);
			if(null != ap){
				vpnGatewaySetting.setHiveAP(ap);
			}
			getDataSource().getVpnGateWaysSetting().add(vpnGatewaySetting);
		}
	}
	
	//update the input of external ip address
	public void updateGateWaySetting(){
		if(externalIpAddresses != null){
			for(int i=0;i<externalIpAddresses.length && i<getDataSource().getVpnGateWaysSetting().size();i++){
				getDataSource().getVpnGateWaysSetting().get(i).setExternalIpAddress(externalIpAddresses[i]);
			}
		}
	}
	
	public void removeVpnGateWaySetting(){
		if (vpnGatewayIndices != null) {
			Collection<VpnGatewaySetting> removeList = new Vector<>();
			for (String serviceIndex : vpnGatewayIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getVpnGateWaysSetting().size()) {
						removeList
								.add(getDataSource().getVpnGateWaysSetting().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			reorderGateways();
			getDataSource().getVpnGateWaysSetting().removeAll(removeList);
		}
	}

	public boolean addSingleVpnGateway(){
		if(getDataSource().getVpnGateWaysSetting() != null){
			for(VpnGatewaySetting vpnGatewaySetting:getDataSource().getVpnGateWaysSetting()){
				if(vpnGateWay != null && !"".equals(vpnGateWay)){
					if(vpnGatewaySetting.getApId() == Long.parseLong(vpnGateWay)){
						hideCreateItem = "";
						hideNewButton = "none";
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean addSameTypeVpnGateway(){
		if(getDataSource().getVpnGateWaysSetting() != null){
			for(VpnGatewaySetting vpnGatewaySetting:getDataSource().getVpnGateWaysSetting()){
				if(vpnGateWay != null && !"".equals(vpnGateWay)){
					HiveAp ap = QueryUtil.findBoById(HiveAp.class, Long.parseLong(vpnGateWay));
					if(vpnGatewaySetting.getHiveAP().getHiveApModel() !=  ap.getHiveApModel()){
						hideCreateItem = "";
						hideNewButton = "none";
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	public EnumItem[] getEnumVpnTunnelModeL2() {
		return UserProfileForTrafficL2.ENUM_VPN_TUNNEL_MODE_L2;
	}
	
	public EnumItem[] getEnumVpnTunnelModeSplit() {
		return UserProfileForTrafficL3.ENUM_VPN_TUNNEL_MODE_SPLIT;
	}
	
	public EnumItem[] getEnumVpnTunnelModeAll() {
		return UserProfileForTrafficL3.ENUM_VPN_TUNNEL_MODE_ALL;
	}
	
	public Long getVpnGateWayId() {
		return vpnGateWayId;
	}

	public void setVpnGateWayId(Long vpnGateWayId) {
		this.vpnGateWayId = vpnGateWayId;
	}

	public boolean isWirelessRoutingEnabled() {
		return wirelessRoutingEnabled;
	}

	public void setWirelessRoutingEnabled(boolean wirelessRoutingEnabled) {
		this.wirelessRoutingEnabled = wirelessRoutingEnabled;
	}

	public String getWirelessRoutingEnabledStyle() {
		if(wirelessRoutingEnabled){
			return "";
		}
		return "none";
	}

	public void setWirelessRoutingEnabledStyle(String wirelessRoutingEnabledStyle) {
		this.wirelessRoutingEnabledStyle = wirelessRoutingEnabledStyle;
	}

	public String getUserProfilesTrafficForL2Str() {
		return userProfilesTrafficForL2Str;
	}

	public void setUserProfilesTrafficForL2Str(String userProfilesTrafficForL2Str) {
		this.userProfilesTrafficForL2Str = userProfilesTrafficForL2Str;
	}

	public String getUserProfilesTrafficForL3Str() {
		return userProfilesTrafficForL3Str;
	}

	public void setUserProfilesTrafficForL3Str(String userProfilesTrafficForL3Str) {
		this.userProfilesTrafficForL3Str = userProfilesTrafficForL3Str;
	}

	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}

	public short[] getVpnTunnelModesL3() {
		return vpnTunnelModesL3;
	}

	public void setVpnTunnelModesL3(short[] vpnTunnelModesL3) {
		this.vpnTunnelModesL3 = vpnTunnelModesL3;
	}

	public short[] getVpnTunnelModesL2() {
		return vpnTunnelModesL2;
	}

	public void setVpnTunnelModesL2(short[] vpnTunnelModesL2) {
		this.vpnTunnelModesL2 = vpnTunnelModesL2;
	}

	private void fetchVPNNodes() throws JSONException {
		List<VpnService> vpns = QueryUtil.executeQuery(VpnService.class, 
				new SortParams("profileName"), 
				null, QueryUtil.getDomainFilter(userContext));
		
		if(vpns.isEmpty()) {
			return ;
		}
		
		jsonArray = new JSONArray();
		
		for(VpnService vpn : vpns) {
			if(vpn == null) {
				continue;
			}
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", vpn.getId());
			jsonObj.put("name", vpn.getProfileName());
			jsonArray.put(jsonObj);
		}
	}

	/*public List<CheckItem> getUserProfilesWithInternalNetworks() {
		return userProfilesWithInternalNetworks;
	}

	public void setUserProfilesWithInternalNetworks(
			List<CheckItem> userProfilesWithInternalNetworks) {
		this.userProfilesWithInternalNetworks = userProfilesWithInternalNetworks;
	}*/

	public String getVpnTunnelUserProfilesSelect() {
		return vpnTunnelUserProfilesSelect;
	}

	public void setVpnTunnelUserProfilesSelect(String vpnTunnelUserProfilesSelect) {
		this.vpnTunnelUserProfilesSelect = vpnTunnelUserProfilesSelect;
	}

	public String getVpnTunnelBehaviorMode() {
		return vpnTunnelBehaviorMode;
	}

	public void setVpnTunnelBehaviorMode(String vpnTunnelBehaviorMode) {
		this.vpnTunnelBehaviorMode = vpnTunnelBehaviorMode;
	}

	public String getHideNewTunnelException() {
		return hideNewTunnelException;
	}

	public void setHideNewTunnelException(String hideNewTunnelException) {
		this.hideNewTunnelException = hideNewTunnelException;
	}

	public String getHideCreateTunnelException() {
		return hideCreateTunnelException;
	}

	public void setHideCreateTunnelException(String hideCreateTunnelException) {
		this.hideCreateTunnelException = hideCreateTunnelException;
	}
	
	private boolean vpnServiceFlag;

	public boolean isVpnServiceFlag() {
		return vpnServiceFlag;
	}

	public void setVpnServiceFlag(boolean vpnServiceFlag) {
		this.vpnServiceFlag = vpnServiceFlag;
	}

	public int[] getOrdering() {
		return ordering;
	}

	public void setOrdering(int[] ordering) {
		this.ordering = ordering;
	}
	
	/**
	 * reorder vpngateways
	 */
	protected void reorderGateways() {
		if (ordering == null) {
			return;
		}

		boolean needsReordering = false;
		for (int i = 0; i < ordering.length; i++) {
			if (ordering[i] != i) {
				needsReordering = true;
			}
			if (ordering[i] < getDataSource().getVpnGateWaysSetting().size()) {
				getDataSource().getVpnGateWaysSetting().get(ordering[i]).setReorder(i);
			}
		}
		if (!needsReordering) {
			return;
		}
		log.info("reorderGateways", "Needs re-ordering");
		Collections.sort(getDataSource().getVpnGateWaysSetting(),
				new Comparator<VpnGatewaySetting>() {
					@Override
					public int compare(VpnGatewaySetting rule1, VpnGatewaySetting rule2) {
						Integer id1 = rule1.getReorder();
						Integer id2 = rule2.getReorder();
						return id1.compareTo(id2);
					}
				});
	}

	protected Collection<VpnGatewaySetting> findGatewaysToRemove() {
		Collection<VpnGatewaySetting> removeList = new Vector<>();
		if (vpnGatewayIndices != null) {
			for (String serviceIndex : vpnGatewayIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getVpnGateWaysSetting().size()) {
						removeList.add(getDataSource().getVpnGateWaysSetting().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
				}
			}
		}
		return removeList;
	}

	public String getVpnTunnelBehaviorModeAll() {
		return vpnTunnelBehaviorModeAll;
	}

	public void setVpnTunnelBehaviorModeAll(String vpnTunnelBehaviorModeAll) {
		this.vpnTunnelBehaviorModeAll = vpnTunnelBehaviorModeAll;
	}

	public String getVpnTunnelBehaviorModeSplit() {
		return vpnTunnelBehaviorModeSplit;
	}

	public void setVpnTunnelBehaviorModeSplit(String vpnTunnelBehaviorModeSplit) {
		this.vpnTunnelBehaviorModeSplit = vpnTunnelBehaviorModeSplit;
	}

	public String getBehaviorModeAllShow() {
		if(getDataSource()!= null){
			if(getDataSource().getRouteTrafficType() == VpnService.ROUTE_VPNTUNNEL_TRAFFIC_ALL){
				return "";
			}else{
				return "none";
			}
		}
		return "none";
	}

	public String getBehaviorModeSplitShow() {
		if(getDataSource() != null){
			if(getDataSource().getRouteTrafficType() == VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL){
				return "";
			}else{
				return "none";
			}
		}
		return "";
	}

	public void setVpnGatewayIndices(Collection<String> vpnGatewayIndices) {
		this.vpnGatewayIndices = vpnGatewayIndices;
	}

	public void setTunnelExceptionIndices(Collection<String> tunnelExceptionIndices) {
		this.tunnelExceptionIndices = tunnelExceptionIndices;
	}

	public List<CheckItem> getAvailableDomainObjects() {
		return availableDomainObjects;
	}

	public void setAvailableDomainObjects(List<CheckItem> availableDomainObjects) {
		this.availableDomainObjects = availableDomainObjects;
	}

	public Long getDomObjId() {
		if(null == domObjId){
			if(null != getDataSource()){
				if(getDataSource().getDomObj()!= null){
					domObjId = getDataSource().getDomObj().getId();
				}
			}
		}
		return domObjId;
	}

	public void setDomObjId(Long domObjId) {
		this.domObjId = domObjId;
	}

	public String[] getExternalIpAddresses() {
		return externalIpAddresses;
	}

	public void setExternalIpAddresses(String[] externalIpAddresses) {
		this.externalIpAddresses = externalIpAddresses;
	}

	public String getExternalIpAddress() {
		return externalIpAddress;
	}

	public void setExternalIpAddress(String externalIpAddress) {
		this.externalIpAddress = externalIpAddress;
	}
	
	private List<Short> cvgList;
	
	public List<Short> getCVGList(){
		if(cvgList == null){
			cvgList = new ArrayList<Short>();
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
			return cvgList;
		}
		
		return cvgList;
	}

}