package com.ah.ui.actions.hiveap;

/*
 * @author Fisher
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateQos;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryCertainBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.config.MacFiltersAction;
import com.ah.ui.actions.config.SsidProfilesAction;
import com.ah.ui.actions.config.VpnServiceAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CloneUtil;
import com.ah.util.ColorUtil;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.BoAssistant;
import com.ah.util.bo.BoGenerationUtil;
import com.ah.util.bo.np.NpUserProfileVlanMappingUtil;
import com.ah.util.bo.np.NpUserProfileVlanMappingUtil.UserProfileVlanRelation;
import com.ah.util.userprofile.attr.UserProfileAttrFactory;
import com.ah.util.userprofile.attr.UserProfileType.UserProfileOnPort;
import com.ah.util.userprofile.attr.UserProfileType.UserProfileOnSSID;
import com.ah.util.userprofile.attr.validation.IUserProfileAttrValidator;
import com.ah.util.userprofile.attr.validation.UserProfileAttrValidateImpl;
import com.ah.util.values.PairValue;

public class NetworkPolicyAction extends BaseAction implements QueryBo {
	private static final Tracer log = new Tracer(NetworkPolicyAction.class
			.getSimpleName());
	private static final long serialVersionUID = 1L;
	
	@Override
	public String execute() throws Exception {
		try {
			if ("edit".equals(operation)){
				if (id!=null){
					editBo(this);
					loadLazyVpnService();
					loadLazyPortItemService();
					setSessionDataSource(dataSource);
				}
				if (dataSource==null) {
					jsonObject  = new JSONObject();
					jsonObject.put("e", "An error happen when init profile data.");
					return "json";
				}
				if (refreshFlg){
					refreshSessionSsid();
				}
				
				prepareColorSet();
				
				return INPUT;
			} else if ("chooseMgtVlan".equals(operation)){
				prapareVlanAndNetworkSelectObjects();
				prepareInitVlanAndNetworkSelectObjects();
				return "mgtVlan";
			} else if ("saveMgtVlan".equals(operation)) {
				jsonObject = new JSONObject();
				try {
//					if (getDataSource().isBlnWirelessRouter()) {
//						VpnNetwork tmpVpnNetwork =null;
//						if (mgtNetworkId != null) {
//							tmpVpnNetwork = findBoById(VpnNetwork.class,
//									mgtNetworkId, this);
//						}
//						if (tmpVpnNetwork==null) {
//							jsonObject.put("r", false);
//							jsonObject.put("e", "Management Network does not exist.");
//							return "json";
//						}
//						if (!checkManagementNetwork(tmpVpnNetwork).equals("")) {
//							jsonObject.put("r", false);
//							String tmpStr[] = {"Management Network"};
//							jsonObject.put("e", MgrUtil.getUserMessage("warn.config.vpnnetwork.not.properForParam1", tmpStr));
//							return "json";
//						}
//						getDataSource().setMgtNetwork(tmpVpnNetwork);
//					} else {
//						getDataSource().setMgtNetwork(null);
//					}
					prepareSaveVlanSelectObjects();
					resetVlanNetworkMapping();
					jsonObject.put("r", true);
//					if (getDataSource().isBlnWirelessRouter()) {
//						jsonObject.put("r1", getDataSource().getMgtNetwork().getNetworkName());
//					} else {
						jsonObject.put("r1", getDataSource().getVlan().getVlanName());
//					}
					jsonObject.put("r2", getDataSource().getVlanNative().getVlanName());
				} catch (Exception e) {
					jsonObject.put("r", false);
					jsonObject.put("e", e.getMessage());
				}
				return "json";
			} else if ("fetchAddRemoveSsidPage".equals(operation)){
				selectSsidIds.clear();
				for(Long key: getDataSource().getSsidInterfaces().keySet()){
					if (key>0){
						selectSsidIds.add(key);
					}
				}
				if (createSsidId!=null) {
					selectSsidIds.add(createSsidId);
				}
				return "ssidSelectPage";
			} else if ("fetchAddRemoveNetworkObjPage".equals(operation)) {
				selectNetworkId=getDataSource().getPreNetworkId();
				return "configTemplateNetworkSelectPage";
			} else if ("refreshNetworkObjPage".equals(operation)) {
				return "templateVlanNetworkMapping";
			} else if ("finishSelectNetworkObject".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					if (getDataSource()!=null && getDataSource().getPreVlanId()!=null) {
						if (selectNetworkId!=null) {
							boolean hasVlanFlg = false;
							for (ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
								if (cvn.getVlan().getId().equals(getDataSource().getPreVlanId())) {
									hasVlanFlg=true;
									cvn.setNetworkObj(QueryUtil.findBoById(VpnNetwork.class, selectNetworkId));
									break;
								}
							}
							if (!hasVlanFlg) {
								ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
								cvn.setVlan(QueryUtil.findBoById(Vlan.class, getDataSource().getPreVlanId()));
								cvn.setNetworkObj(QueryUtil.findBoById(VpnNetwork.class, selectNetworkId));
								getDataSource().getVlanNetwork().add(cvn);
							}
						} else {
							for (ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
								if (getDataSource().getPreVlanId().equals(cvn.getVlan().getId())) {
									cvn.setNetworkObj(null);
								}
							}
						}
					}
				} catch (Exception e) {
					jsonObject.put("e", e.getMessage());
					return "json";
				}
				return "templateVlanNetworkMapping";
			} else if ("checkSsidsUsed".equals(operation)) {
				return checkSelectedSsids();
			} else if ("finishSelectSsid".equals(operation)){
				jsonObject = new JSONObject();
				String err = checkSameSsidAndSsidSize();
				if (!err.equals("")) {
					jsonObject.put("e", err);
					return "json";
				}
				err= checkExistSsid();
				if (!err.equals("")){
					jsonObject.put("e", err);
					return "json";
				}
				err = setSelectedSsidProfiles();
				
				if (!err.equals("")){
					jsonObject.put("e", err);
					return "json";
				}
				resetVlanNetworkMapping();
				return INPUT;
			} else if ("fetchSelectFwPolicyPage".equals(operation)){
				if (null == selectFwPolicyId) {
					//selectFwPolicyId = getAvailableFwPolicy().get(0).getId();
					if(null != getDataSource().getFwPolicy()){
						selectFwPolicyId = getDataSource().getFwPolicy().getId();
					}
				}
				return "fwPolicyListNew";
			} else if ("finishSelectFwPolicy".equals(operation)){
				if (null != selectFwPolicyId) {
					FirewallPolicy fwPol = QueryUtil.findBoById(FirewallPolicy.class, selectFwPolicyId, this);
					getDataSource().setFwPolicy(fwPol);
					jsonObject = new JSONObject();
					jsonObject.put("result", true);
					jsonObject.put("fwName", fwPol.getPolicyName());
					jsonObject.put("fwId", selectFwPolicyId);
					return "json";
				} else {
					getDataSource().setFwPolicy(null);
					jsonObject = new JSONObject();
					jsonObject.put("result", false);
					return "json";
				}
			} else if ("fetchSelectRSPage".equals(operation)){
                //initRadiusList4CloudAuth();
				return "radiusServerListNew";
			} else if ("finishSelectRadiusServer".equals(operation)){
				if (null != selectRadiusServerId) {
					RadiusAssignment radiusSer = QueryUtil.findBoById(RadiusAssignment.class, selectRadiusServerId);
					// lan profile
					if (3 == radiusTypeFlag) {
						PortAccessProfile accObj = QueryUtil.findBoById(PortAccessProfile.class, ssidForRs);
						//lanObj.setMacAuthEnabled(true);
						accObj.setRadiusAssignment(radiusSer);
						updateBoWithEvent(accObj);
					} else {
						for (Long confssid : getDataSource().getSsidInterfaces().keySet()) {
							if (ssidForRs.equals(confssid)) {
								SsidProfile ssidObj = QueryUtil.findBoById(SsidProfile.class, ssidForRs);
								if (1 == radiusTypeFlag) {
									ssidObj.setRadiusAssignment(radiusSer);
									getDataSource().getSsidInterfaces().get(confssid).getSsidProfile().setRadiusAssignment(radiusSer);
									
								} else {
									ssidObj.setRadiusAssignmentPpsk(radiusSer);
									getDataSource().getSsidInterfaces().get(confssid).getSsidProfile().setRadiusAssignmentPpsk(radiusSer);
									
								}
								updateBoWithEvent(ssidObj);
								
								break;
							}
						}
					}
					jsonObject = new JSONObject();
					jsonObject.put("ssid", ssidForRs);
					jsonObject.put("rsid", selectRadiusServerId);
					jsonObject.put("flag", radiusTypeFlag);
					jsonObject.put("rsName", radiusSer.getRadiusName());
					jsonObject.put("rsNameSub", radiusSer.getRadiusNameSubstr());
					return "json";
				}
				return INPUT;
			} else if ("networkPolicySelect".equals(operation)) {
				prepareForNetworkPolicySelection();
				return "networkPolicySelect";
			}  else if ("networkPolicySelectDlg".equals(operation)) {
				this.prepareNpTypeProxy(null);
				return "networkPolicyNew";
			} else if ("newNetworkPolicy".equals(operation)) {
				//create a new network policy in database, clone from default network policy
				jsonObject = new JSONObject();
				Long addedId;
				try {
					// check session token for CSRF attack
					if (!isCSRFTokenValida() && ("newNetworkPolicy".equals(operation))) {
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("security.csrfattack") + getLastTitle());

						throw new HmException(MgrUtil.getUserMessage("error.security.invalidRequest"),						
								HmMessageCodes.SECURITY_REQUEST_INVALID, new String[] { "newNetworkPolicy" });
					}
                   if(!Jsoup.isValid( this.getConfigName(), Whitelist.none())) {
                        jsonObject.put("e",MgrUtil.getUserMessage("error.common.invalid", "The policy name"));
                        jsonObject.put("resultStatus", false);
                        return "json";
                    }
					List<?> boIds = QueryUtil.executeQuery("select id from "
							+ boClass.getSimpleName(), null, new FilterParams("configName", this.getConfigName()),
							domainId);
					if (!boIds.isEmpty()) {
						jsonObject.put("e",MgrUtil.getUserMessage("error.objectExists", this.getConfigName()));
						jsonObject.put("resultStatus", false);
						return "json";
					}
					
					ConfigTemplate configTemplateTmp = QueryUtil.findBoByAttribute(ConfigTemplate.class,
							"defaultFlag",true, this);
					
					configTemplateTmp.getSsidInterfaces().clear();
					configTemplateTmp.setSsidInterfaces(BoGenerationUtil.genDefaultSsidInterfaces());
					
					//set fields with user inputs
					configTemplateTmp.setVersion(null);
					configTemplateTmp.setId(null);
					configTemplateTmp.setDefaultFlag(false);
					
					configTemplateTmp.setConfigName(this.getConfigName());
					configTemplateTmp.setDescription(this.getDescription());
					
					configTemplateTmp.setConfigType(this.getNpTypeCtl().getConfigType());
					configTemplateTmp.setOwner(getDomain());
					
					//setCloneFields(configTemplateTmp, configTemplateTmp);
					List<?> networkNameLst= QueryUtil.executeQuery("select networkName from " + HmStartConfig.class.getSimpleName(),
							null, null, domainId);
					configTemplateTmp.setHiveProfile(this.getDefaultVHMHiveProfile(networkNameLst));
					
					if (configTemplateTmp.getConfigType().isRouterContained()) {
						String strFetchNetworkName=BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT;
						VpnNetwork vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
						if(null == vpnNetWork){
							if (!networkNameLst.isEmpty()) {
								strFetchNetworkName = networkNameLst.get(0).toString();
							}
							vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
							
							if (vpnNetWork==null) {
								strFetchNetworkName=getDomain().getDomainName();
								vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
							}
						}
						if (configTemplateTmp.getVlanNetwork()==null) {
							List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<ConfigTemplateVlanNetwork>();
							configTemplateTmp.setVlanNetwork(vlanNetwork);
						} else {
							configTemplateTmp.getVlanNetwork().clear();
						}
						ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
						cvn.setVlan(configTemplateTmp.getVlan());
						cvn.setNetworkObj(vpnNetWork);
						configTemplateTmp.getVlanNetwork().add(cvn);
						
					}
					
//					if (configTemplateTmp.getConfigType().isRouterContained() 
//							&& configTemplateTmp.getRouterIpTrack()==null) {
//						MgmtServiceIPTrack routerIpTrackClass = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
//								BeParaModule.DEFAULT_IP_TRACKING_BR_NAME_NEW, domainId);
//						if (routerIpTrackClass!=null) {
//							configTemplateTmp.setRouterIpTrack(routerIpTrackClass);
//						}
//					}
					
					if (configTemplateTmp.getConfigType().isBonjourContained()) {
						if (configTemplateTmp.getBonjourGw()==null) {
							BonjourGatewaySettings bgsAerohive = QueryUtil.findBoByAttribute(BonjourGatewaySettings.class, "bonjourGwName", BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_AEROHIVE,
									getDomain().getId());
							configTemplateTmp.setBonjourGw(bgsAerohive);
						}
					} 
					
					if(configTemplateTmp.getMgmtServiceOption() == null){
						String strFetchNetworkName=null;
						if (!networkNameLst.isEmpty()) {
							strFetchNetworkName = networkNameLst.get(0).toString();
						}
						MgmtServiceOption msOption = QueryUtil.findBoByAttribute(MgmtServiceOption.class, "mgmtName", strFetchNetworkName, domainId, this);
						if(msOption == null){
							strFetchNetworkName = getDomain().getDomainName();
							msOption = QueryUtil.findBoByAttribute(MgmtServiceOption.class, "mgmtName", strFetchNetworkName, domainId, this);
						}
						configTemplateTmp.setMgmtServiceOption(msOption);
					}
					
					if(configTemplateTmp.getMgmtServiceDns() == null){
						String strFetchNetworkName=null;
						if (!networkNameLst.isEmpty()) {
							strFetchNetworkName = networkNameLst.get(0).toString();
						}
						MgmtServiceDns msd =  QueryUtil.findBoByAttribute(MgmtServiceDns.class, "mgmtName", strFetchNetworkName, domainId, this);
						if(msd == null){
							strFetchNetworkName = getDomain().getDomainName();
							 msd =  QueryUtil.findBoByAttribute(MgmtServiceDns.class, "mgmtName", strFetchNetworkName, domainId, this);
						}
						configTemplateTmp.setMgmtServiceDns(msd);
					}
					
					if(configTemplateTmp.getMgmtServiceTime() == null){
						String strFetchNetworkName=null;
						if (!networkNameLst.isEmpty()) {
							strFetchNetworkName = networkNameLst.get(0).toString();
						}
						MgmtServiceTime mgmtServiceTime =  QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", strFetchNetworkName, domainId, this);
						if(mgmtServiceTime == null){
							strFetchNetworkName = getDomain().getDomainName();
							mgmtServiceTime =  QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", strFetchNetworkName, domainId, this);
						}
						configTemplateTmp.setMgmtServiceTime(mgmtServiceTime);
					}
					
					if(configTemplateTmp.getLldpCdp() == null){
						String strFetchNetworkName=null;
						if (!networkNameLst.isEmpty()) {
							strFetchNetworkName = networkNameLst.get(0).toString();
						}
						LLDPCDPProfile lldpcdpProfile =  QueryUtil.findBoByAttribute(LLDPCDPProfile.class, "profileName", strFetchNetworkName, domainId, this);
						if(lldpcdpProfile == null){
							strFetchNetworkName = getDomain().getDomainName();
							lldpcdpProfile =  QueryUtil.findBoByAttribute(LLDPCDPProfile.class, "profileName", strFetchNetworkName, domainId, this);
						}
						configTemplateTmp.setLldpCdp(lldpcdpProfile);
					}
					
					if (checkSwitchSettingsEnabled(configTemplateTmp)) {
						configTemplateTmp.getStormControlList().clear();
						configTemplateTmp.setStormControlList(BoGenerationUtil.getDefaultStormControl(getDomain(),configTemplateTmp));
					}
					
					if (checkSwitchSettingsEnabled(configTemplateTmp) && configTemplateTmp.getSwitchSettings() == null){

						SwitchSettings switchSettings = new SwitchSettings();
						switchSettings.setOwner(getDomain());
						switchSettings.setStpSettings(switchSettings.initStpSettings(getDomain()));
						configTemplateTmp.setSwitchSettings(switchSettings);
					}
					
					addedId = QueryUtil.createBo(configTemplateTmp);
				} catch (Exception e) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("e",e.getMessage());
					return "json";
				}
				//return something to indicate whether errors occured.
				jsonObject.put("resultStatus", true);
				jsonObject.put("addedId", addedId);
				return "json";
			} else if ("networkPolicyModify".equals(operation)) {
				//just get information to shown on page, not save
				ConfigTemplate configTemplateTmp;
				if (this.isSave2Db()) {
					//may be there is no session
					configTemplateTmp = QueryUtil.findBoById(ConfigTemplate.class, this.getNetworkPolicyId(), this);
				} else {
					//fetch info from session
					configTemplateTmp = getDataSource();
				}
				if (configTemplateTmp != null) {
					this.setConfigName(configTemplateTmp.getConfigName());
					this.setDescription(configTemplateTmp.getDescription());
					this.setNetworkPolicyId(configTemplateTmp.getId());
				}
				this.prepareNpTypeProxy(configTemplateTmp);
				return "networkPolicyModify";
			} else if ("networkPolicyUpdate".equals(operation)) {
				jsonObject = new JSONObject();
				try {
	                if(StringUtils.isNotBlank(this.getConfigName())
	                        && !Jsoup.isValid(this.getConfigName(), Whitelist.none())) {
                        jsonObject.put("e",MgrUtil.getUserMessage("error.common.invalid", "The policy name"));
                        jsonObject.put("resultStatus", false);
                        return "json";
                    }
	                
					ConfigTemplate configTemplateTmp1 = QueryUtil.findBoById(ConfigTemplate.class, this.getNetworkPolicyId(), this);
					if (!configTemplateTmp1.getConfigName().equals(this.getConfigName())) {
						List<?> boIds = QueryUtil.executeQuery("select id from "
								+ boClass.getSimpleName(), null, new FilterParams("configName", this.getConfigName()),
								domainId);
						if (!boIds.isEmpty()) {
							jsonObject.put("e",MgrUtil.getUserMessage("error.objectExists", this.getConfigName()));
							jsonObject.put("resultStatus", false);
							return "json";
						}
					}
					jsonObject.put("routingChg", false);
					if (this.isSave2Db()) {
						//save those info to database
						ConfigTemplate configTemplateTmp = QueryUtil.findBoById(ConfigTemplate.class, this.getNetworkPolicyId(), this);
						//HiveProfile hiveProfileTmp = QueryUtil.findBoById(HiveProfile.class, this.getHiveId(), this);
						/*if (configTemplateTmp.isBlnWirelessRouter() != this.isFormSettingWirelessRouter()
								|| configTemplateTmp.isBlnBonjourOnly() != this.isFormSettingBonjourOnly()) {*/
						if (!configTemplateTmp.getConfigType().equals(this.getNpTypeCtl().getConfigType())) {
							jsonObject.put("routingChg", true);
							changeNetworkPolicyType(configTemplateTmp, this.getNpTypeCtl().getConfigType());
						}
						//configTemplateTmp.setHiveProfile(hiveProfileTmp);
						configTemplateTmp.setDescription(this.getDescription());
						//configTemplateTmp.setConfigName(this.getConfigName());
						/*configTemplateTmp.setBlnWirelessRouter(this.isFormSettingWirelessRouter());
						configTemplateTmp.setBlnBonjourOnly(this.isFormSettingBonjourOnly());*/
						configTemplateTmp.setConfigType(this.getNpTypeCtl().getConfigType());
						//====================================================================	
						Set<PortGroupProfile> portGroupProfileSet = new HashSet<PortGroupProfile>();
						for (PortGroupProfile tempClass : configTemplateTmp.getPortProfiles()) {
							if(!configTemplateTmp.getConfigType().isWirelessEnabled()&& tempClass.getDeviceType() == 0 ){
							}else if(!configTemplateTmp.getConfigType().isSwitchEnabled()&& tempClass.getDeviceType() == 4 ){
							}else if(!configTemplateTmp.getConfigType().isRouterEnabled()&& tempClass.getDeviceType() == 1 ){
							}else{
								portGroupProfileSet.add(tempClass);
							}
						}
						configTemplateTmp.setPortProfiles(portGroupProfileSet);

						
						//====================================================================
						updateBoWithEvent(configTemplateTmp);
					} else {
						//save those info to session
						getSessionDataSource();
						ConfigTemplate configTemplateTmp = this.getDataSource();
						//HiveProfile hiveProfileTmp = QueryUtil.findBoById(HiveProfile.class, this.getHiveId(), this);
						/*if (configTemplateTmp.isBlnWirelessRouter() != this.isFormSettingWirelessRouter()
								|| configTemplateTmp.isBlnBonjourOnly() != this.isFormSettingBonjourOnly()) {*/
						if (!configTemplateTmp.getConfigType().equals(this.getNpTypeCtl().getConfigType())) {
							jsonObject.put("routingChg", true);
							changeNetworkPolicyType(configTemplateTmp, this.getNpTypeCtl().getConfigType());
						}
						//configTemplateTmp.setHiveProfile(hiveProfileTmp);
						configTemplateTmp.setDescription(this.getDescription());
						//configTemplateTmp.setConfigName(this.getConfigName());
						/*configTemplateTmp.setBlnWirelessRouter(this.isFormSettingWirelessRouter());
						configTemplateTmp.setBlnBonjourOnly(this.isFormSettingBonjourOnly());*/
						configTemplateTmp.setConfigType(this.getNpTypeCtl().getConfigType());
						setSessionDataSource(configTemplateTmp);
					}
					jsonObject.put("resultStatus", true);
				} catch (Exception e) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("e", "errors occured while saving data.");
				}
				return "json";
			} else if ("networkPolicyCloneDlg".equals(operation)){
				ConfigTemplate profile = findBoById(ConfigTemplate.class, cloneSrcId,
						this);
				profile.setVersion(null);
				profile.setId(null);
				
				if(profile.isDefaultFlag()){
					//setCloneFields(configTemplateTmp, configTemplateTmp);
					List<?> networkNameLst= QueryUtil.executeQuery("select networkName from " + HmStartConfig.class.getSimpleName(),
							null, null, domainId);
					if(profile.getMgmtServiceOption() == null){
						String strFetchNetworkName=null;
						if (!networkNameLst.isEmpty()) {
							strFetchNetworkName = networkNameLst.get(0).toString();
						}
						MgmtServiceOption msOption = QueryUtil.findBoByAttribute(MgmtServiceOption.class, "mgmtName", strFetchNetworkName, domainId, this);
						if(msOption == null){
							strFetchNetworkName = getDomain().getDomainName();
							msOption = QueryUtil.findBoByAttribute(MgmtServiceOption.class, "mgmtName", strFetchNetworkName, domainId, this);
						}
						profile.setMgmtServiceOption(msOption);
					}
				}
				
				profile.setDefaultFlag(false);
				profile.setConfigName("");
				
				CloneUtil.setConfigTemplateCloneFields(profile, profile);
				
				profile.setOwner(null);
				setSessionDataSource(profile);
				
				setConfigName(profile.getConfigName());
				setDescription(profile.getDescription());
				//setHiveId(profile.getHiveProfile().getId());
				setNetworkPolicyId(profile.getId());
				return "networkPolicyModify";
			}  else if ("networkPolicyCloneDone".equals(operation)){
				//save network policy cloned into database and move to drawer two
				jsonObject = new JSONObject();
				if (checkNameExists("configName", getConfigName())) {
					jsonObject.put("e",MgrUtil.getUserMessage("error.objectExists", getConfigName()));
					jsonObject.put("resultStatus", false);
					return "json";
				}
				//HiveProfile hiveProfileTmp = QueryUtil.findBoById(HiveProfile.class, getHiveId(), this);
				//getDataSource().setHiveProfile(hiveProfileTmp);
				getDataSource().setDescription(getDescription());
				getDataSource().setConfigName(getConfigName());
				try {
					Long clonedId = createBo(dataSource);
					
					//============================================
					for (PortGroupProfile tempClass : this.getDataSource().getPortProfiles()) {
						List<SingleTableItem> lst=tempClass.getItems();
						List<SingleTableItem> list = new ArrayList<SingleTableItem>();
						if(null != lst && !lst.isEmpty()){
							for (SingleTableItem item : lst) {
								if(null != item){
									list.add(item);
									if(cloneSrcId == item.getConfigTemplateId() ){
										SingleTableItem newitem =  item.clone();
										newitem.setConfigTemplateId(clonedId.longValue());
										list.add(newitem);
									}
								}
							}
						}
						tempClass.setItems(list);
					}
					setId(clonedId.longValue());
					dataSource = updateBo(dataSource);
					//=============================================
					
					jsonObject.put("cloneId", clonedId);
				} catch (Exception e) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("e", "errors occured while saving data.");
					return "json";
				}
				jsonObject.put("resultStatus", true);
				return "json";
			} else if ("saveNetworkPolicyGui".equals(operation)){
				jsonObject = new JSONObject();
				try {
					if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
						throw new HmException(
								"Update object failed, session must have been shared by another browser window.",
								HmMessageCodes.STALE_SESSION_OBJECT,
								new String[] { "Update" });
					}
					
					if (isNeedSwitchSettings()) {
						prepareSaveSwitchSettings();
					}
					
					String msg="";
					if (!getDataSource().getConfigType().isBonjourOnly()) {
					    msg=checkAllConfigTemplate();
					}
					if (!msg.equals("")){
						jsonObject.put("t", false);
						jsonObject.put("m", msg);
						return "json";
					}
					NpUserProfileVlanMappingUtil.removeNotExistUserProfileVlanMapping(this.getDataSource());
					if (!isSaveAlways() && compareSameWithDB()){
						jsonObject.put("t", true);
					} else {
						//setId(dataSource.getId());
						
						// need to load the classifier tag items from device template due to the poor data structure design
						loadPortItem((ConfigTemplate)dataSource);
						
						dataSource = updateBo(dataSource);
						//added a switch for CAPWAP delay alarm from Guadalupe
						updateSimpeleHiveApCache(dataSource.getId(),getDataSource().isEnableDelayAlarm(),domainId);
						//======================================
						List<Long> configTemplateIds = new ArrayList<Long>();
						configTemplateIds.add(dataSource.getId());
						
						List<Long> portTemplateIds = new ArrayList<Long>();
						for (PortGroupProfile template: this.getDataSource().getPortProfiles()){
							portTemplateIds.add(template.getId());
						}
						removeItems(configTemplateIds,portTemplateIds,domainId,this);
						//======================================
						setSessionDataSource(findBoById(ConfigTemplate.class, dataSource.getId(), this));
						loadLazyVpnService();
						loadLazyPortItemService();
						jsonObject.put("t", true);
					}
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage(e));
					reportActionError(e);
				}
				return "json";
			} else if ("fetchAddRemoveVlanMappingSelect".equals(operation)) {
				fetchVlanMappingSelectPage();
				return "vlanMappingSelectPage";
			} else if ("finishSelectVlanMappingOK".equals(operation)) {
				jsonObject = new JSONObject();
				try {
					String ret = setVlanMappingSelectPageValue();
					if (!ret.equals("")) {
						jsonObject.put("t", false);
						jsonObject.put("m", ret);
						return "json";
					}
					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", "Save vlans error. please try again.");
				}
				return "json";
			} else if ("removeSelectVlanMapping".equals(operation)){
				jsonObject = new JSONObject();
				if (vlanMappingId!=null) {
					if (getDataSource().getVlanNetwork()!=null) {
						// remove not select vlan
						for (Iterator<ConfigTemplateVlanNetwork> iter = getDataSource().getVlanNetwork().iterator(); iter.hasNext();) {
							ConfigTemplateVlanNetwork cvn  = iter.next();
							if(vlanMappingId.equals(cvn.getVlan().getId())){
							if (cvn.isBlnUserAdd()) {
									 iter.remove();
								} else {
									cvn.setBlnRemoved(true);
									cvn.setNetworkObj(null);
								 }
							}
						}
					}
				}
				jsonObject.put("t", true);
				return "json";
			}else if("selectVpnProfile".equals(operation)){
				if(null == vpnSelectedId){
					//vpnSelectedId = getVpnSelectList().get(0).getId();
					if( null != getDataSource().getVpnService()){
						vpnSelectedId = getDataSource().getVpnService().getId();
					}
				}
				if(createVpnId != null){
					vpnSelectedId = createVpnId;
				}
				return "selectVpnProfile";
			}else if("finishSelectVpn".equals(operation)){
				if (null != vpnSelectedId) {
					VpnService vpn = QueryUtil.findBoById(VpnService.class, vpnSelectedId, this);
					getDataSource().setVpnService(vpn);
					loadLazyVpnService();
				}else{
					if(null != getDataSource().getVpnService()){
						getDataSource().setVpnService(null);
					}
				}
				return INPUT;
			} else if ("editMgtAdvancedSetting".equals(operation)){
				prepareMgtDependentSelectObjects();
				prepareInitMgtSelectObjects();
				prepareHiveList();
				if (this.getDataSource().getHiveProfile() != null) {
					this.hiveId = this.getDataSource().getHiveProfile().getId();
				}
				
				if (isNeedSwitchSettings()){
					prepareSwitchSettingsObjects();
				}
				return "jsonMgtAdvancedSetting";
			} else if ("saveMgtAdvancedSetting".equals(operation)){
				prepareSaveMgtSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
				if (isNeedSwitchSettings()){
					prepareSaveSwitchSettings();
					updateStormContorl();
				}
				
				this.getDataSource().setHiveProfile(QueryUtil.findBoById(HiveProfile.class, this.hiveId));
//				String ret = checkManagementNetwork();
//				if (!ret.equals("")){
//					addActionError(ret);
//					return "jsonMgtAdvancedSetting";
//				}
				jsonObject = new JSONObject();
				jsonObject.put("t", true);
				return "json";
			} else if ("changeLocationServer".equals(operation)){
				jsonObject = new JSONObject();
				if (locationServerId != null && locationServerId>0) {
					LocationServer locationServer =	findBoById(LocationServer.class, locationServerId);
					if (locationServer != null){
						if (locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROHIVE){
							jsonObject.put("v", 1);
						} else {
							jsonObject.put("v", 0);
						}
					} else {
						jsonObject.put("v", 0);
					}
				} else {
					jsonObject.put("v", 0);
				}
				return "json";
			} else if ("fetchSelectBonjourGw".equals(operation)){
                if (null == selectBonjourGwId) {
                    if(null != getDataSource().getBonjourGw()){
                        selectBonjourGwId = getDataSource().getBonjourGw().getId();
                    }
                }
                return "selectBonjourGw";
            } else if ("finishSelectBonjourGw".equals(operation)){
                if (null != selectBonjourGwId) {
                    BonjourGatewaySettings bonjourGw = QueryUtil.findBoById(BonjourGatewaySettings.class, selectBonjourGwId, this);
                    getDataSource().setBonjourGw(bonjourGw);
                    jsonObject = new JSONObject();
                    jsonObject.put("result", true);
                    jsonObject.put("bonjourGwName", bonjourGw.getBonjourGwName());
                    jsonObject.put("bgId", selectBonjourGwId);
                    return "json";
                } else {
                    getDataSource().setBonjourGw(null);
                    jsonObject = new JSONObject();
                    jsonObject.put("result", false);
                    return "json";
                }
            } else if ("newIpTrack".equals(operation) || "editIpTrack".equals(operation) ||
					"newMgtDns".equals(operation) || "editMgtDns".equals(operation) ||
					"newRouterIpTrack".equals(operation) || "editRouterIpTrack".equals(operation)||
					"newMarkerMap".equals(operation) || "editMarkerMap".equals(operation)||
					"newClassifierMap".equals(operation) || "editClassifierMap".equals(operation)||
					"newMarkerMap4Switch".equals(operation) || "editMarkerMap4Switch".equals(operation)||
					"newClassifierMap4Switch".equals(operation) || "editClassifierMap4Switch".equals(operation)||
					"newVlanNative".equals(operation) || "editVlanNative".equals(operation)||
					"newVlan".equals(operation) || "editVlan".equals(operation)||
					"newVlanMapping".equals(operation) || "editVlanMapping".equals(operation)||
					"newMgtSnmp".equals(operation) || "editMgtSnmp".equals(operation)||
					"newMgtSyslog".equals(operation) || "editMgtSyslog".equals(operation)||
					"newMgtTime".equals(operation) || "editMgtTime".equals(operation)|| 
					"newMgtOption".equals(operation) || "editMgtOption".equals(operation)||
					"newIpFilter".equals(operation) || "editIpFilter".equals(operation)||
					"newIdsPolicy".equals(operation) || "editIdsPolicy".equals(operation)||
					"newMgtNetwork".equals(operation) || "editMgtNetwork".equals(operation)||
					"newLldpCdp".equals(operation) || "editLldpCdp".equals(operation) ||
					"newRadiusProxy".equals(operation) || "editRadiusProxy".equals(operation) ||
					"newRadiusServer".equals(operation) || "editRadiusServer".equals(operation) ||
					"newRoutingPbrPolicy".equals(operation) || "editRoutingPbrPolicy".equals(operation) ||
					"newLocationServer".equals(operation) || "editLocationServer".equals(operation) ||
					"newClientWatch".equals(operation) || "editClientWatch".equals(operation) ||
					"newTvNetwork".equals(operation) || "editTvNetwork".equals(operation) ||
					"newRadiusOptName".equals(operation) || "editRadiusOptName".equals(operation) ||
					"newMstpRegion".equals(operation) || "editMstpRegion".equals(operation) ||
					"newSuppCLIBlob".equals(operation) ||"editSuppCLIBlob".equals(operation)) {
				addLstForward("networkPolicy");
				addLstTabId(tabId);
				clearErrorsAndMessages();
				return operation;
			} else if ("removeNetworkPolicy".equals(operation)) {
				messagesToShown = removeOperationJson(ConfigTemplate.class, selectedIds);
				//======================================
				removeItems(selectedIds,domainId,this);
				//======================================
				prepareForNetworkPolicySelection();
				return "networkPolicySelect";
			} else if ("upVlanMapping".equals(operation)) {
				reloadUpVlanMappingForNetworkPolicy();
				this.prepareUserProfileVlanMappingRelation(this.getDataSource(), this.upVlanMappingType);
				return "upVlanMapping";
			} else if ("newVlanForUpMapping".equals(operation)) {
				jsonObject = new JSONObject();
				Vlan vlan = CreateObjectAuto.createNewVlan(this.inputVlanIdValue, getDomain(), "");
				jsonObject.put("id", vlan.getId());
				jsonObject.put("text", vlan.getVlanName());
				return "json";
			} else if ("newVlanIdForUpMapping".equals(operation)
							|| "editVlanIdForUpMapping".equals(operation)) {
				NpUserProfileVlanMappingUtil.setUpVlanMapping(getDataSource(), 
						this.mappingUpId, 
						this.mappingVlanId);
				MgrUtil.setSessionAttribute(SESSION_UP_VLAN_MAPPING_UP_ID_KEY, this.userProfileId);
				MgrUtil.setSessionAttribute(SESSION_UP_VLAN_MAPPING_TYPE_KEY, this.upVlanMappingType);
				MgrUtil.setSessionAttribute(SESSION_UP_VLAN_MAPPING_RELATIVE_ID_KEY, this.upVlanRelativeId);
				addLstForward("npUpVlanMappingForward");
				return operation;
			} else if ("npUpVlanMappingContinue".equals(operation)) {
				if (this.vlanId != null) {
					NpUserProfileVlanMappingUtil.addOrEditUpVlanMapping(getDataSource(), 
							(Long)MgrUtil.getSessionAttribute(SESSION_UP_VLAN_MAPPING_UP_ID_KEY), this.vlanId);
				}
				this.upVlanMappingType = (String)MgrUtil.getSessionAttribute(SESSION_UP_VLAN_MAPPING_TYPE_KEY);
				this.upVlanRelativeId = (Long)MgrUtil.getSessionAttribute(SESSION_UP_VLAN_MAPPING_RELATIVE_ID_KEY);
				this.prepareUserProfileVlanMappingRelation(this.getDataSource(), this.upVlanMappingType);
				return "upVlanMapping";
			} else if ("saveVlanForUpMapping".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("rs", true);
				NpUserProfileVlanMappingUtil.setUpVlanMapping(getDataSource(), 
						this.mappingUpId, 
						this.mappingVlanId);
				this.resetVlanNetworkMapping();
				return "json";
			} else if ("descVlanItems".equals(operation)) {
				this.vlan = QueryUtil.findBoById(Vlan.class, this.vlanId, new QueryCertainBo<Vlan>() {
					@Override
					public Collection<HmBo> loadBo(Vlan bo) {
						if (bo.getItems() != null) {
							bo.getItems().size();
						}
						return null;
					}
				});
				return "vlanItems";
            } else if ("listPortTemplates".equals(operation)) {
                selectedPortTempalteIds.clear();
                List<CheckItem> portProfilesList = getPortTemplateList();
                
//                if (!(null == portProfilesList || portProfilesList.isEmpty())) {
//                	
//                    for (PortGroupProfile portProfile : getDataSource()
//                            .getPortProfiles()) {
//                        selectedPortTempalteIds.add(portProfile.getId());
//                    }
//                    if (null != createPortTempalteIds) {
//                        selectedPortTempalteIds.add(createPortTempalteIds);
//                    }
//                }
                return "listPortTemplates";
            } else if("checkNonDefaultTemplates".equals(operation)){
            	String err = "";
                //=====================================   	
    			PortGroupProfile pro = QueryUtil.findBoById(PortGroupProfile.class, selectedId, this); 
    			//=====================================
            	 for (PortGroupProfile portProfile : getDataSource().getPortProfiles()){
            		 if(null !=selectedId && selectedId.equals(portProfile.getId()) && !portProfile.getItems().isEmpty()){
            			 String wt = "";
            			 int conflict = 0;
            			 List<String> oldName = new ArrayList<String>();
               			 for(SingleTableItem item : portProfile.getItems()){
               				 if(item.getConfigTemplateId() == getDataSource().getId().longValue()){
               					conflict++;
               					Long id = item.getNonGlobalId();
                    			PortGroupProfile devTem = null;
                    			if (id != null){
                    				try{
                    					devTem = QueryUtil.findBoById(PortGroupProfile.class, id);
                    				}catch(Exception e){
                    					devTem = null;
                    				}
                    			}
                    			oldName.add(devTem.getName());
                    		   /* wt = wt.concat(" " + devTem.getName() + ",");*/
               				 }
               			 }
               			 wt = cleanDuplicateTemplateName(oldName);
               			 if(conflict > 0){
               				wt = wt.substring(0,wt.length()-1);
                    		err =  MgrUtil.getUserMessage("error.config.networkPolicy.deviceTemplates.classification.modify",new String[]{pro.getName(),wt});
               			 }
               			
               			 if (StringUtils.isNotEmpty(err)) {
                                jsonObject = new JSONObject();
                                jsonObject.put("err", err);
                                return "json";
                            }
               		 }
            		 //=============================================
            		 if(pro.getDeviceType() == portProfile.getDeviceType() 
            				 && pro.getDeviceModels().equals(portProfile.getDeviceModels()) && portProfile.getItems().size()>0){
            			 jsonObject = new JSONObject();
            			 jsonObject.put("defaultSelectedId", portProfile.getId());
            			 return "json";
            		 }
            		 //=============================================            		 
               	 }
                   return INPUT;
            } else if ("checkPortTemplates".equals(operation)) {
                // Port Template Profiles
                jsonObject = new JSONObject();
            	//=====================================
            	Set<PortGroupProfile> portGroupProfileSet = new HashSet<PortGroupProfile>();
            	for (Long selectedPortTemplateId:selectedPortTempalteIds){
            		PortGroupProfile profile = QueryUtil.findBoById(PortGroupProfile.class, selectedPortTemplateId, this);
            		List<SingleTableItem> items = new ArrayList<SingleTableItem>();            		
            		for (PortGroupProfile portGroupProfile :this.getDataSource().getPortProfiles()){		
                		if(profile.getDeviceModels().equals(portGroupProfile.getDeviceModels())
                				&& profile.getDeviceType() == portGroupProfile.getDeviceType()){
                			for(SingleTableItem item:portGroupProfile.getItems()){
                				SingleTableItem oneItem = item.clone();
                				items.add(oneItem);
                			}
                		}
                	}
            		profile.setItems(items);
            		portGroupProfileSet.add(profile);
            	}
            	this.getDataSource().setPortProfiles(portGroupProfileSet);
            	//=====================================
                return "json";
            } else if ("selectedPortTemplates".equals(operation)) {
                String err = setSelectedPortTemplateProfiles();
                if (StringUtils.isNotEmpty(err)) {
                    jsonObject = new JSONObject();
                    jsonObject.put("err", err);
                    return "json";
                }
                return INPUT;
                //================================================================	
            } else if ("addRemovePortItem".equals(operation)){
				PortGroupProfile pro = QueryUtil.findBoById(PortGroupProfile.class, portTemplateId, this);
				String deviceModels = pro.getDeviceModels();
				short devieceType = pro.getDeviceType();
				
				templist = QueryUtil.executeNativeQuery("select id,name from port_template_profile where devicemodels = '" +deviceModels 
				+ "' and devicetype = " + devieceType 
				+ " and id <> " + portTemplateId 
				+ " and owner = "+this.domainId);
				//=========================================
				for(int i=0;i<templist.size();i++){
					Object[] obj = new Object[2];
					obj = (Object[])templist.get(i);
					obj[1] = ((String)obj[1]).replace("\\", "\\\\").replace("'","\\'");
				}
				if( templist != null){
					if(templist.size() == 0){
						jsonObject = new JSONObject();
						jsonObject.put("err", getText("configure.network.policy.device.template.no.warning.message"));
						return "json";
					}
				}
				if(templist == null){
					jsonObject = new JSONObject();
					jsonObject.put("err", getText("configure.network.policy.device.template.no.warning.message"));
					return "json";
				}
				//=========================================				
				for(PortGroupProfile object : this.getDataSource().getPortProfiles()) {
					if(object != null){
						if (object.getId().longValue()== portTemplateId){
							MgrUtil.setSessionAttribute(PortGroupProfile.class.getSimpleName() + "Source",object);
							defaultTemplateName=object.getName();
	 
							deviceTemplateInfo = object.getTemplDescInfo();
						 
							itemlist = object.getItems();
							
						}
					}
				}
				MgrUtil.setSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER, DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_VALUE);
				MgrUtil.setSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE, devieceType);
				MgrUtil.setSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE, deviceModels);
 			 	configTemplateId = this.getDataSource().getId();
				return "portItemDlg";
			}else if("savePortItem".equals(operation)){
				//For fixing bug 24935
				MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER);
				MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE);
				MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE);
				//End of fixing bug 24935
				//done the error
				String err="";
					
				for(PortGroupProfile object : this.getDataSource().getPortProfiles()) {
					if (object.getId().longValue()== portTemplateId){
						SingleTableItem oneItem = new SingleTableItem();
						if (descriptions != null) {
							for (int i = 0; i < descriptions.length
								&& i < object.getItems().size(); i++) {
								oneItem = object.getItems().get(i);
								oneItem.setDescription(descriptions[i]);
								
								if( oneItem.getConfigTemplateId() == 0 || oneItem.getConfigTemplateId() == -1){
									oneItem.setConfigTemplateId(getDataSource().getId());
								} 
								if((oneItem.getNonGlobalId() != -1 || oneItem.getNonGlobalId() != 0) 
										&& oneItem.getConfigTemplateId() == getDataSource().getId().longValue()){
									PortGroupProfile profile = QueryUtil.findBoById(PortGroupProfile.class, oneItem.getNonGlobalId(), this);
									if(profile != null){
	    								oneItem.setNonDefault(profile);
	    							}
								}else{
									oneItem.setNonDefault(null);
								}
								
							}
						}
					
					}
				}
				if (StringUtils.isNotEmpty(err)) {
                    jsonObject = new JSONObject();
                    jsonObject.put("err", err);
                    return "json";
                }
				
				prepareColorSet();
				
                return INPUT;
			//================================================================           
            } else {
                baseOperation();
                return prepareBoList();
            }
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private void loadPortItem(ConfigTemplate template) {
	    if(!template.getPortProfiles().isEmpty()) {
	        for (PortGroupProfile profile : template.getPortProfiles()) {
	            PortGroupProfile newProfile = (PortGroupProfile) QueryUtil.findBoById(PortGroupProfile.class, profile.getId(), new QueryBo() {
                    @Override
                    public Collection<HmBo> load(HmBo bo) {
                        if (bo instanceof PortGroupProfile) {
                            PortGroupProfile port = (PortGroupProfile) bo;
                            port.getItems().size();
                        }
                        return null;
                    }
                });
	            for (SingleTableItem item : newProfile.getItems()) {
	                if(item.getConfigTemplateId()!=template.getId().longValue()
	                        && !profile.getItems().contains(item)) {
	                    profile.getItems().add(item);
	                }
                }
            }
	    }
    }

    //====================================
    public static String getPortTemplateName(long portTemplateId,boolean flag){
    	String ret = "";
    	PortGroupProfile profile = (PortGroupProfile) QueryUtil.findBoById(PortGroupProfile.class, portTemplateId);
    	if(profile != null){
    		if(flag){
    			ret = profile.getName().replace("\\", "\\\\").replace("'","\\'");
    		}else{
    			ret = profile.getName();
    		}
    	}
    	return ret;
    }
    private String defaultTemplateName;
    private String deviceTemplateInfo;
    private long portTemplateId;
    private String[] descriptions;
    private List<?> templist;
    List<?> itemlist;
    private Long configTemplateId;

    public Long getConfigTemplateId() {
		return configTemplateId;
	}

	public void setConfigTemplateId(Long configTemplateId) {
		this.configTemplateId = configTemplateId;
	}

	public List<?> getTemplist() {
		return templist;
	}

	public void setTemplist(List<?> templist) {
		this.templist = templist;
	}

	public List<?> getItemlist() {
		return itemlist;
	}

	public void setItemlist(List<?> itemlist) {
		this.itemlist = itemlist;
	}

	public String getDefaultTemplateName() {
		return defaultTemplateName;
	}

	public void setDefaultTemplateName(String defaultTemplateName) {
		this.defaultTemplateName = defaultTemplateName;
	}

	public String getDeviceTemplateInfo() {
		return deviceTemplateInfo;
	}

	public void setDeviceTemplateInfo(String deviceTemplateInfo) {
		this.deviceTemplateInfo = deviceTemplateInfo;
	}

	public String[] getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public long getPortTemplateId() {
		return portTemplateId;
	}

	public void setPortTemplateId(long portTemplateId) {
		this.portTemplateId = portTemplateId;
	}
	//==========================================

    private void prepareForNetworkPolicySelection() {
		//if there is no session dataSource, set default config template selected. if IDM, set default IDM network policy
		//also set the session dataSource to the default one.
		if (this.getSelectedNetworkPolicyId() != null && this.getSelectedNetworkPolicyId() > 0) {
			this.setDefaultConfigTemplateId(this.getSelectedNetworkPolicyId());
		} else {
			if (dataSource==null) {
				// remove quick start policies
				this.setDefaultConfigTemplateId(-1L);
			} else { //if there is session dataSource, set the selected one as the session indicate
				this.setDefaultConfigTemplateId(dataSource.getId());
			}
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CONFIGURATION_TEMPLATE);
		setDataSource(ConfigTemplate.class);
	}
	
	public void loadLazyVpnService(){
		if (getDataSource()!=null && getDataSource().getVpnService()!=null &&
				getDataSource().getVpnService().getIpsecVpnType()==VpnService.IPSEC_VPN_LAYER_3){
			if (getDataSource().getVpnService().getVpnGateWaysSetting()!=null) {
				for (VpnGatewaySetting vps: getDataSource().getVpnService().getVpnGateWaysSetting()){
					if (vps.getHiveAP()==null && vps.getApId()!=null) {
						try {
							vps.setHiveAP(findBoById(HiveAp.class, vps.getApId(), this));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	public void loadLazyPortItemService(){
		if (getDataSource()!=null && getDataSource().getPortProfiles()!=null ){
			 
			for(PortGroupProfile profile:getDataSource().getPortProfiles()){
				List<SingleTableItem> items = profile.getItems();
				
                // refresh cache
                for (SingleTableItem item : items) {
	            		if(null != item && -1 != item.getNonGlobalId() 
	            				&& item.getConfigTemplateId() == getDataSource().getId().longValue()){
	                		item.setNonDefault(QueryUtil.findBoById(PortGroupProfile.class, item.getNonGlobalId(), this));
	                	}
                }
			}
		}		
	}

	public List<ConfigTemplateVlanNetwork> getFetchVlanNetworkMapping(){
		List<ConfigTemplateVlanNetwork> lst = new ArrayList<ConfigTemplateVlanNetwork>();
		Set<Vlan> vlanSet = new HashSet<Vlan>();
		// fetch all vlan can be mapping
		Map<Long, UserProfileVlanMapping> mappings = NpUserProfileVlanMappingUtil.getFullUpVlanMappings(getDataSource(), true, true,-1);
		if (mappings != null) {
			for (UserProfileVlanMapping mapping : mappings.values()) {
				//upSet.remove(mapping.getUserProfile());
				vlanSet.add(mapping.getVlan());
			}
		}

		// fetch all native Vlan in port assess profile when routing mode
		List<String> allowVlans = new ArrayList<String>();
		Set<Vlan> nativeVlan = NpUserProfileVlanMappingUtil.getAllRoutingNativeVlan(getDataSource(), -1, allowVlans);
		if (!nativeVlan.isEmpty()) {
			vlanSet.addAll(nativeVlan);
		}

		if (getDataSource().getVlan()!=null) {
			vlanSet.add(getDataSource().getVlan());
		}
		
		Set<String> allVlanList = MgrUtil.convertRangeToVlaue(allowVlans);
		
		if (getDataSource().getVlanNetwork()!=null) {
			for (Iterator<ConfigTemplateVlanNetwork> iter = getDataSource().getVlanNetwork().iterator(); iter.hasNext();) {
				ConfigTemplateVlanNetwork cvn  = iter.next();
				if (!vlanSet.contains(cvn.getVlan()) && !cvn.isBlnUserAdd()) {
					boolean hasVlan = false;
					Vlan loadLazyVlan = QueryUtil.findBoById(Vlan.class, cvn.getVlan().getId(), this);
					for(SingleTableItem st1: loadLazyVlan.getItems()){
						if (allVlanList.contains("all") || allVlanList.contains(String.valueOf(st1.getVlanId()))) {
							hasVlan=true;
							break;
						}
					}
					if (hasVlan) {
						if (cvn.isBlnRemoved()) {
							iter.remove();
						} else {
						cvn.setBlnUserAdd(true);
						}
					} else {
					iter.remove();
					}
				} else if (vlanSet.contains(cvn.getVlan()) && cvn.isBlnUserAdd()){
					cvn.setBlnUserAdd(false);
				}
			}
		}
		
		if (getDataSource().getVlanNetwork()!=null) {
			for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (vlanSet.contains(cvn.getVlan())) {
					vlanSet.remove(cvn.getVlan());
				}
				if (cvn.getNetworkObj()!=null) {
				VpnNetwork vpn = QueryUtil.findBoById(VpnNetwork.class, cvn.getNetworkObj().getId(), this);
				cvn.setNetworkObj(vpn);
			}
		}
		}
		for(Vlan v: vlanSet){
			if (getDataSource().getVlanNetwork()==null) {
				getDataSource().setVlanNetwork(new ArrayList<ConfigTemplateVlanNetwork>());
			}
			ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
			cvn.setVlan(v);
			getDataSource().getVlanNetwork().add(cvn);
		}
		
		if (getDataSource().getVlanNetwork()!=null) {
			for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (getDataSource().getVlan()!=null && cvn.getVlan().equals(getDataSource().getVlan())) {
					cvn.setBlnRemoved(false);
					cvn.setBlnMgtVlan(true);
				} else {
					cvn.setBlnMgtVlan(false);
				}
				if (!cvn.isBlnRemoved()) {
					lst.add(cvn);
				}
			}
		}
		Collections.sort(lst, new Comparator<ConfigTemplateVlanNetwork>() {
            public int compare(ConfigTemplateVlanNetwork obj1, ConfigTemplateVlanNetwork obj2) {
            	if (NumberUtils.isDigits(obj1.getVlan().getVlanName()) && NumberUtils.isDigits(obj2.getVlan().getVlanName())) {
                    return Integer.parseInt(obj1.getVlan().getVlanName())- Integer.parseInt(obj2.getVlan().getVlanName());
                  } else if (NumberUtils.isDigits(obj1.getVlan().getVlanName())) {
                   return -1;
                  } else if (NumberUtils.isDigits(obj2.getVlan().getVlanName())) {
                   return 1;
                  } else {
                    return obj1.getVlan().getVlanName().compareToIgnoreCase(obj2.getVlan().getVlanName());
                  }
            }
        });
		
		return lst;
	}
	
	public void resetVlanNetworkMapping(){
		Set<Vlan> vlanSet = new HashSet<Vlan>();
		// fetch all vlan can be mapping
		usMapping = NpUserProfileVlanMappingUtil.getFullUpVlanMappings(getDataSource());
		Map<Long, UserProfileVlanMapping> mappings = NpUserProfileVlanMappingUtil.getFullUpVlanMappings(getDataSource(), true, true,-1);
		if (mappings != null) {
			for (UserProfileVlanMapping mapping : mappings.values()) {
				//upSet.remove(mapping.getUserProfile());
				vlanSet.add(mapping.getVlan());
			}
		}
		// fetch all native Vlan in port assess profile when routing mode
		List<String> allowVlans = new ArrayList<String>();
		Set<Vlan> nativeVlan = NpUserProfileVlanMappingUtil.getAllRoutingNativeVlan(getDataSource(), -1, allowVlans);
		if (!nativeVlan.isEmpty()) {
			vlanSet.addAll(nativeVlan);
		}

		if (getDataSource().getVlan()!=null) {
			vlanSet.add(getDataSource().getVlan());
		}
		Set<String> allVlanList = MgrUtil.convertRangeToVlaue(allowVlans);
		
		if (getDataSource().getVlanNetwork()!=null) {
			for (Iterator<ConfigTemplateVlanNetwork> iter = getDataSource().getVlanNetwork().iterator(); iter.hasNext();) {
				ConfigTemplateVlanNetwork cvn  = iter.next();
				if (!vlanSet.contains(cvn.getVlan()) && !cvn.isBlnUserAdd()) {
					boolean hasVlan = false;
					Vlan loadLazyVlan = QueryUtil.findBoById(Vlan.class, cvn.getVlan().getId(), this);
					for(SingleTableItem st1: loadLazyVlan.getItems()){
						if (allVlanList.contains("all") || allVlanList.contains(String.valueOf(st1.getVlanId()))) {
							hasVlan=true;
							break;
						}
					}
					if (hasVlan) {
						if (cvn.isBlnRemoved()) {
							iter.remove();
						} else {
						cvn.setBlnUserAdd(true);
						}
					} else {
					iter.remove();
					}
					
				} else if (vlanSet.contains(cvn.getVlan()) && cvn.isBlnUserAdd()){
					cvn.setBlnUserAdd(false);
				}
			}
		}
	}
	
	public String setVlanMappingSelectPageValue() throws Exception{
		Vlan newAddVlan =null;
		if (vlanMappingId != null) {
			if (vlanMappingId==-1){
				Vlan myVlan = CreateObjectAuto.createNewVlan(inputVlanMappingIdValue,getDomain(),"");
				if (myVlan!=null){
					newAddVlan = myVlan;
							 }
			} else {
				Vlan tmpClass = findBoById(Vlan.class, vlanMappingId);
				newAddVlan=tmpClass;
						}
					}
		if (newAddVlan!=null) {
					for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (cvn.getVlan().equals(newAddVlan)){
					if (!cvn.isBlnRemoved()) {
					return MgrUtil.getUserMessage("error.addObjectExists");
					} else {
						cvn.setBlnRemoved(false);
						return "";
					}
				}
					}
			
						ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
						cvn.setBlnUserAdd(true);
			cvn.setVlan(newAddVlan);
						getDataSource().getVlanNetwork().add(cvn);
					}
		
		return "";
				}
	
	
//	public void setVlanMappingSelectPageValue(){
//		boolean removeAllUserAdd = false;
//		if (selectVlanNetworkVlanIds!=null) {
//			selectVlanNetworkVlanIds.remove(-1L);
//			if (!selectVlanNetworkVlanIds.isEmpty()) {
//				List<Long> fi= new ArrayList<Long>();
//				fi.addAll(selectVlanNetworkVlanIds);
//				List<Vlan> vlans = QueryUtil.executeQuery(Vlan.class, null, new FilterParams("id",fi));
//				Set<Vlan> vlanSet = new HashSet<Vlan>();
//				
//				if (getDataSource().getVlanNetwork()!=null) {
//					// remove not select vlan
//					for (Iterator<ConfigTemplateVlanNetwork> iter = getDataSource().getVlanNetwork().iterator(); iter.hasNext();) {
//						ConfigTemplateVlanNetwork cvn  = iter.next();
//						if (cvn.isBlnUserAdd()) {
//							 if(!vlans.contains(cvn.getVlan())){
//								 iter.remove();
//							 }
//						}
//					}
//					// add new vlan
//					for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
//						vlanSet.add(cvn.getVlan());
//					}
//					vlans.removeAll(vlanSet);
//					for(Vlan v: vlans){
//						ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
//						cvn.setBlnUserAdd(true);
//						cvn.setVlan(v);
//						getDataSource().getVlanNetwork().add(cvn);
//					}
//				}
//			} else {
//				removeAllUserAdd=true;
//			}
//		} else {
//			removeAllUserAdd=true;
//		}
//		if (removeAllUserAdd){
//			if (getDataSource().getVlanNetwork()!=null) {
//				for (Iterator<ConfigTemplateVlanNetwork> iter = getDataSource().getVlanNetwork().iterator(); iter.hasNext();) {
//					ConfigTemplateVlanNetwork cvn  = iter.next();
//					if (cvn.isBlnUserAdd()) {
//						iter.remove();
//					}
//				}
//			}
//		}
//	}
	
	public void fetchVlanMappingSelectPage(){
		String sql = "SELECT bo.id, bo.vlanName FROM "
				+ Vlan.class.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("bo.vlanName"), null,
				getDomain().getId());
		Set<Long> vlanSet = new HashSet<Long>();
		//selectVlanNetworkVlanIds.clear();
		if (getDataSource().getVlanNetwork()!=null) {
			for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (!cvn.isBlnRemoved()) {
					vlanSet.add(cvn.getVlan().getId());
			}
		}
		}
		vlanUserAddList=new ArrayList<CheckItem>();
		for(Object obj: bos){
			Object[] oneItem = (Object[])obj;
			Long iid= (Long)oneItem[0];
			String iName = oneItem[1].toString();
			if (!vlanSet.contains(iid)){
				vlanUserAddList.add(new CheckItem(iid,iName));
			}
		}
		vlanMappingId=-1L;
		inputVlanMappingIdValue="";
		vlanUserAddList.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK,
				"Create new VLAN"));
		}
	
	private Map<Long, UserProfileVlanMapping> usMapping;
	
	@Override
	public ConfigTemplate getDataSource() {
		return (ConfigTemplate) dataSource;
	}

	public String checkSameSsidAndSsidSize() {
		if (selectSsidIds!=null && selectSsidIds.size() > 32) {
			String tempStr[] = { "32", "one "+NmsUtil.getOEMCustomer().getAccessPonitName() };
			return getText("error.assignSsid.range", tempStr);
		}
		return "";
	}
	
	private String checkVpnSize() {
		if (getDataSource().getVpnService()!=null){
			if (getDataSource().getConfigType().isRouterContained()) {
				if (getDataSource().getVpnService().getVpnServerType()==VpnService.IPSEC_VPN_LAYER_2){
					return getText("error.config.networkPolicy.check.connotConfig", 
							new String[]{"Layer 2 IPsec VPN", getDataSource().getConfigName()});
				}
				if (getDataSource().getVpnService().getVpnGateWaysSetting().size()<1) {
					return getText("error.config.networkPolicy.check.mustConfig", 
							new String[]{"VPN Gateway", "Layer 3 IPsec VPN", getDataSource().getConfigName()});
				}
			} else {
				if (getDataSource().getVpnService().getVpnServerType()==VpnService.IPSEC_VPN_LAYER_3){
					return getText("error.config.networkPolicy.check.connotConfig", 
							new String[]{"Layer 3 IPsec VPN", getDataSource().getConfigName()});

				}
			}

			if (!getDataSource().getConfigType().isRouterContained() && !getDataSource().getConfigType().isBonjourOnly()) {
				String msg = VpnServiceAction.validateIpPoolCapability(
						getDataSource().getId(),getDataSource().getVpnService().getId());
				if (!msg.equals("")) {
					return msg;
				}
			}
		}
		
		return "";
	}
	
	public String checkExistSsid() {
		Set<String> ssidSets = new HashSet<String>();
		if (selectSsidIds == null) {
			return "";
		}
		for (Long filterId : selectSsidIds) {
			SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class,
					filterId);
			if (ssidSets.contains(ssidProfile.getSsid())) {
				return getText("error.template.existSsid");
			} else {
				ssidSets.add(ssidProfile.getSsid());
			}
		}
		return "";
	}
	
    /**
     * TA2525: Check SSID whether is used by other NetworkPolicy
     * @author Nianrong
     * - Time: Jan 11, 2012 4:46:45 PM
     * @return JSON Object
     * @throws JSONException
     */
    private String checkSelectedSsids() throws JSONException {
        jsonObject = new JSONObject();
        if (null != selectSsidIds && !selectSsidIds.isEmpty()) {
            try {
                StringBuilder queryStr = new StringBuilder(
                        "select distinct on (c.ssid_profile_id) c.ssid_profile_id, p.ssidName " +
                        "from config_template_ssid as c, ssid_profile as p where ");
                if (null != getDataSource().getId()) {
                    queryStr.append(" c.config_template_id !=" + getDataSource().getId()
                            + " and ");
                }
                queryStr.append(" c.ssid_profile_id = p.id and ");
                queryStr.append(" c.ssid_profile_id in (");
                for (Long selectedId : selectSsidIds) {
                    queryStr.append(selectedId);
                    queryStr.append(",");
                }
                queryStr.deleteCharAt(queryStr.length() - 1);
                queryStr.append(")");
                List<?> resultList = QueryUtil.executeNativeQuery(queryStr.toString());
                if (!resultList.isEmpty()) {
                    String detailMsg = "";
                    for (Object result : resultList) {
                        detailMsg += "<p>" + ((Object[])result)[1].toString() + "</p>";
                    }
                    jsonObject.put("err", detailMsg);
                }
            } catch (Exception e) {
                String msg = "Unknown error.";
                jsonObject.put("err", msg);
                log.error("checkSelectedSsids", msg, e);
            }
        }
        return "json";
    }
	
	protected String setSelectedSsidProfiles() {
		try {
			Set<Long> removeItems = new HashSet<Long>();
			Set<Long> oldSsidProfile = new HashSet<Long>();
			for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()) {
				if (configTemplateSsid.getSsidProfile() != null) {
					oldSsidProfile.add(configTemplateSsid.getSsidProfile().getId());
				}
			}
	
			if (selectSsidIds != null) {
				for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
						.values()) {
					if (configTemplateSsid.getSsidProfile() != null) {
						for (Long filterId : selectSsidIds) {
							if (configTemplateSsid.getSsidProfile().getId().equals(filterId)) {
								removeItems.add(filterId);
								break;
							}
						}
					}
				}
			}
	
			oldSsidProfile.removeAll(removeItems);
			// remove old ssid
			for (Long filterId : oldSsidProfile) {
				SsidProfile ssidProfile = findBoById(SsidProfile.class, filterId);
				if (ssidProfile != null) {
					getDataSource().getSsidInterfaces().remove(ssidProfile.getId());
				}
			}
	
			if (selectSsidIds!=null){
				for (Long filterId : selectSsidIds) {
					if (removeItems.contains(filterId)) {
						continue;
					}
					SsidProfile ssidProfile = findBoById(SsidProfile.class, filterId, this);
					if (ssidProfile != null) {
						ConfigTemplateSsid templateSsid = new ConfigTemplateSsid();
						templateSsid.setSsidProfile(ssidProfile);
						templateSsid.setInterfaceName(ssidProfile.getSsidName());
						getDataSource().getSsidInterfaces().put(ssidProfile.getId(), templateSsid);
					}
				}
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "";
	}
	
	public String prepareSaveSelectObjects(){
		return "";
	}
	
	private void refreshSessionSsid() {
		try {
			for (ConfigTemplateSsid tmpSsid: getDataSource().getSsidInterfaces().values()) {
				if (tmpSsid.getSsidProfile()!=null) {
					tmpSsid.setSsidProfile(findBoById(SsidProfile.class, tmpSsid.getSsidProfile().getId(), this));
				}
			}
			
			refreshWiredPorts();
			
			if (getDataSource().getVpnService()!=null) {
				VpnService vpn = QueryUtil.findBoById(VpnService.class, getDataSource().getVpnService().getId(), this);
				getDataSource().setVpnService(vpn);
			}
			loadLazyVpnService();
			loadLazyPortItemService();
			setSessionDataSource(dataSource);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void refreshWiredPorts() {
        Set<PortGroupProfile> list = new HashSet<>();
        for (PortGroupProfile portProfile : getDataSource().getPortProfiles()) {
            PortGroupProfile template = QueryUtil.findBoById(PortGroupProfile.class, portProfile.getId(), this);
            if(null == template) {
                // for remove item from list dialog
                continue;
            }
            //set non default portType
            if( null != portProfile.getItems() && !portProfile.getItems().isEmpty()){
            	for(SingleTableItem nonItem : portProfile.getItems()){
            		//reset
            		if(null !=nonItem && nonItem.getConfigTemplateId() == getDataSource().getId().longValue()){
            			nonItem.setNonDefault(QueryUtil.findBoById(PortGroupProfile.class, nonItem.getNonGlobalId(), this));
            			
            			for(PortBasicProfile nonBasic : nonItem.getNonDefault().getBasicProfiles()){
                			nonBasic.setAccessProfile(QueryUtil.findBoById(
                                    PortAccessProfile.class, nonBasic.getAccessProfile().getId(), this));
                		}
            		}
            	}
            	template.setItems(portProfile.getItems());
            }else{
            	template.getItems().clear();
            }
           //set default  portType
            if (null != template.getBasicProfiles()) {
                for (PortBasicProfile basic : template.getBasicProfiles()) {
                    basic.setAccessProfile(QueryUtil.findBoById(
                            PortAccessProfile.class, basic.getAccessProfile().getId(), this));
                }
            }
            
            list.add(template);
        }
        getDataSource().setPortProfiles(list);
    }
	
	public boolean getSavePermit(){
		if ("".equals(getWriteDisabled())){
			if (!getDataSource().isDefaultFlag()){
				return true;
			}
		}
		return false;
	}
	
	private String checkAllConfigTemplate() throws Exception{
		for(ConfigTemplateSsid cs: getDataSource().getSsidInterfaces().values()){
			if (cs.getSsidProfile()!=null) {
				if (cs.getSsidProfile().isEnablePpskSelfReg()) {
					if (cs.getSsidProfile().getLocalUserGroups()!=null &&
							cs.getSsidProfile().getLocalUserGroups().size()>1) {
						return getText("error.config.networkPolicy.check.moreLocalusergroup", 
								new String[]{cs.getSsidProfile().getSsidName(),
								getDataSource().getConfigName()});
					}
				}
				String ret=checkUserProfileSize(cs.getSsidProfile());
				if (!ret.equals("")){
					return ret;
				}
				ret=checkMacFilterAction(cs.getSsidProfile());
				if (!ret.equals("")){
					return ret;
				}
				ret=checkPskUserSize(cs.getSsidProfile());
				if (!ret.equals("")){
					return ret;
				}
			}
		}

		//check Lan profile
		// each network object should be has his owner VLAN, vlan cannot same
		// how many VLAN (16) and how many network object(16) for lan policy and ssid profile
		// check vpn when enabled layer2 or layer3
		// check management network, cannot be in same subnet for all policy
		// user profile is for both ssid and lan
		
		String ret=checkVpnSize();
		if (!ret.equals("")){
			return ret;
		}
		
		ret=checkManagementNetwork();
		if (!ret.equals("")){
			return ret;
		}
		
		ret=checkRadioModeSize();
		if (!ret.equals("")){
			return ret;
		}
		
		Map<Long, UserProfile> mapUserProfile = new HashMap<Long,UserProfile>();
		Map<Long, UserProfile> mapUserProfileAssign = new HashMap<Long,UserProfile>();
		
		ret=checkCacAirTime(mapUserProfile,mapUserProfileAssign);
		if (!ret.equals("")){
			return ret;
		}
		
		Map<Long, UserProfile> allUserProfileMaps = new HashMap<Long, UserProfile>();
		allUserProfileMaps.putAll(mapUserProfile);
		allUserProfileMaps.putAll(mapUserProfileAssign);
		if (allUserProfileMaps.values().size() > 64) {
			return getText("error.template.moreUserProfileForNetworkPolicy", new String []{getDataSource().getConfigName()});
		}
		
		ret=checkIpPolicyAndMacPolicySize(mapUserProfile,mapUserProfileAssign);
		if (!ret.equals("")){
			return ret;
		}
		
		ret=checkUserProfileAttribute(mapUserProfile,mapUserProfileAssign);
		if (!ret.equals("")){
			return ret;
		}
		
        ret=checkDeviceTemplateUserProfileAttrs();
        if (!ret.equals("")){
            return ret;
        }
		
		ret=checkNetworkObjectVlan();
			if (!ret.equals("")){
				return ret;
			}
		
		ret=checkTotalPskGroupSize();
		if (!ret.equals("")){
			return ret;
		}
		ret=checkTotalPmkUserSize();
		if (!ret.equals("")){
			return ret;
		}
		
		return "";
	}

    private String checkNetworkObjectVlan() {
    	try {
	    	if (getDataSource().getConfigType().isRouterContained()) {

				List<Long> vlanIds = new ArrayList<Long>();
                for(ConfigTemplateVlanNetwork cvn:getDataSource().getVlanNetwork()){
                	if (!cvn.isBlnRemoved()) {
                    vlanIds.add(cvn.getVlan().getId());
				}
                }
		
				List<Vlan> vlanList = QueryUtil.executeQuery(Vlan.class, null, new FilterParams("id",vlanIds), null, this);
				for (int i=0; i<vlanList.size(); i++) {
					for (int j=i+1; j<vlanList.size(); j++) {
						for(SingleTableItem st1: vlanList.get(i).getItems()){
							for(SingleTableItem stn: vlanList.get(j).getItems()){
								if (st1.getVlanId()==stn.getVlanId()) {
									return getText("error.config.networkPolicy.check.vlansame", 
											new String[]{vlanList.get(i).getVlanName(),
											vlanList.get(j).getVlanName(),
											getDataSource().getConfigName()});
				}
			}
			}
		}
	}
		    	
                // following item check move to AP upload
//                Set<VpnNetwork> setNetWork = new HashSet<VpnNetwork>();
//                for(ConfigTemplateVlanNetwork cvn:getDataSource().getVlanNetwork()){
//                    if(!cvn.getVlan().equals(getDataSource().getVlan())) {
//                        if (cvn.getNetworkObj()!=null) {
//                        	setNetWork.add(cvn.getNetworkObj());
//                        }
//                    }
//                }
//                if (setNetWork.size()>16) {
//                    return getText("error.config.networkPolicy.check.moreVlan", 
//                            new String[]{"16", getDataSource().getConfigName()});
//                }
                
	    	}
    	} catch (Exception e) {
    		return e.getMessage();
    	}
		
		return "";
	}
	
	private String checkUserProfileSize(SsidProfile sp) {
		int usSize = 0;
		if (sp.getUserProfileDefault() != null) {
			usSize++;
		}
		if (sp.getUserProfileSelfReg() != null) {
			usSize++;
		}
		if (sp.getUserProfileGuest() != null) {
		    usSize++;
		}
		if (sp.getRadiusUserProfile() != null) {
			usSize = usSize + sp.getRadiusUserProfile().size();
		}
		if (usSize > 64) {
			return getText("error.template.moreUserProfile");
		}
		return "";
	}
	
	private String checkMacFilterAction(SsidProfile sp) {
		try {
			Map<String, String> tmpMacFilter = new HashMap<String, String>();
			Set<String> totalMacOUI = new HashSet<String>();
			for (MacFilter lazyMacfilter : sp.getMacFilters()) {
				MacFilter filter = findBoById(MacFilter.class, lazyMacfilter.getId(), this);
				for (MacFilterInfo filterInfo : filter.getFilterInfo()) {
					totalMacOUI.add(filterInfo.getMacOrOui().getMacOrOuiName());
					if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_PERMIT) {
						if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_DENY) != null) {
							return MgrUtil.getUserMessage("error.differentMacOuiAction",
									filterInfo.getMacOrOui().getMacOrOuiName());
						} else {
							tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
									+ MacFilter.FILTER_ACTION_PERMIT, "true");
						}
					} else if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_DENY) {
						if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_PERMIT) != null) {
							return MgrUtil.getUserMessage("error.differentMacOuiAction",
									filterInfo.getMacOrOui().getMacOrOuiName());
						} else {
							tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
									+ MacFilter.FILTER_ACTION_DENY, "true");
						}
					}
				}
			}
			if (totalMacOUI.size()>MacFiltersAction.MAX_MACFILTER_ENTER) {
				return getText("error.config.macFilter.maxNumber.reference",
						new String[]{String.valueOf(MacFiltersAction.MAX_MACFILTER_ENTER)});
				
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "";
	}
	
	private String checkPskUserSize(SsidProfile sp) {
		long count = SsidProfilesAction.getPskUserCount(sp);
		if (count > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
			return getText("error.template.morePskUsers");
		}
		return "";
	}
	
	private String checkManagementNetwork() {
		try {	
		if (getDataSource().getConfigType().isRouterContained()) {
				VpnNetwork mgtNetwork = getDataSource().getNetworkByVlan(getDataSource().getVlan());
				
			if (mgtNetwork==null) {
				return getText("error.config.networkPolicy.check.mnk.empty",
							new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
			} else {
					mgtNetwork = findBoById(VpnNetwork.class,mgtNetwork.getId(), this);
				if (mgtNetwork.getNetworkType()!=VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
					return getText("error.config.networkPolicy.check.mnk.guest",
								new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
				} else if (mgtNetwork.getVpnDnsService()==null){
					return getText("error.config.networkPolicy.check.mnk.noDns",
								new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
				} else if (mgtNetwork.getSubItems()==null || mgtNetwork.getSubItems().isEmpty()){
					return getText("error.config.networkPolicy.check.mnk.nosubnet",
								new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
				} else {
					for (VpnNetworkSub vpnSub: mgtNetwork.getSubItems()) {
						if (!vpnSub.isEnableDhcp()) {
							return getText("error.config.networkPolicy.check.mnk.noDhcp",
										new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
						}
						if (vpnSub.getIpBranches()==1) {
							return getText("error.config.networkPolicy.check.mnk.subnetBranchError",
										new String[]{getDataSource().getVlan().getVlanName(),getDataSource().getConfigName()});
							}
						if(!vpnSub.isUniqueSubnetworkForEachBranches()) {
							return getText("error.config.networkPolicy.check.mnk.subnetSameMode");
							}
						if(vpnSub.isEnableNat()) {
							return getText("error.config.networkPolicy.check.mnk.subnetEnableNat");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		
		return "";
	}
//	
//	private boolean checkIpInSameSubNet(String ip1, String ip2) {
//		String[] ipArray1 = ip1.split("/");
//		String[] ipArray2 = ip2.split("/");
//		int minMask=Math.min(Integer.parseInt(ipArray1[1]), Integer.parseInt(ipArray2[1]));
//		return MgrUtil.checkIpInSameSubnet(ipArray1[0], ipArray2[0],
//				AhDecoder.int2Netmask(minMask));
//	}
	
	private String checkRadioModeSize() {
		int amodelCount = 0;
		int bmodelConnt = 0;

		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_A) {
					amodelCount++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && 
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						amodelCount++;
					}
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BG) {
					bmodelConnt++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && 
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						bmodelConnt++;
					}
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
					amodelCount++;
					bmodelConnt++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && 
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						amodelCount++;
						bmodelConnt++;
					}
				}
			}
		}
		if (amodelCount > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeA") };
			return getText("error.assignSsid.range", tempStr);
		}

		if (bmodelConnt > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeBG") };
			return getText("error.assignSsid.range", tempStr);
		}
		return "";
	}
	
	private String checkIpPolicyAndMacPolicySize(Map<Long, UserProfile> mapUserProfile,Map<Long, UserProfile> mapUserProfileAssign) {
		Set<String> ipPolicyName = new HashSet<String>();
		Set<String> macPolicyName = new HashSet<String>();
		Map<Long, UserProfile> allUserProfileMap = new HashMap<Long, UserProfile>();
		allUserProfileMap.putAll(mapUserProfile);
		allUserProfileMap.putAll(mapUserProfileAssign);
		for (UserProfile tempUserProfile: allUserProfileMap.values()) {
			if (tempUserProfile.getIpPolicyTo() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyTo() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getIpPolicyFrom() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyFrom().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyFrom() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyFrom().getPolicyName());
			}
		}

		if (ipPolicyName.size() > 32) {
			// ipPolicyName size must less than 32
			return getText("error.template.moreIPPolicy");
		}

		if (macPolicyName.size() > 32) {
			// macPolicyName size must less than 32
			return getText("error.template.moreMACPolicy");
		}
		return "";
	}
	
	private String checkUserProfileAttribute(Map<Long, UserProfile> mapUserProfile,Map<Long, UserProfile> mapUserProfileAssign, String... prefixes) {
		Set<String> setUsedUserProfile = new HashSet<String>();
		Set<String> setUsedAttrValue = new HashSet<String>();
		Set<String> userProfileCount = new HashSet<String>();
		Set<Long> qosIds = new HashSet<Long>();
		Map<Long, UserProfile> allUserProfileMap = new HashMap<Long, UserProfile>();
		allUserProfileMap.putAll(mapUserProfile);
		allUserProfileMap.putAll(mapUserProfileAssign);
		
		String prefix = "<br>";
		if(ArrayUtils.isNotEmpty(prefixes)) {
		    prefix += prefixes[0] + ", ";
		}
		
		for (UserProfile forAttrUserProfile : allUserProfileMap.values()) {
			if (forAttrUserProfile.getQosRateControl()!=null) {
				qosIds.add(forAttrUserProfile.getQosRateControl().getId());
			}
			
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& setUsedAttrValue.contains(String.valueOf(forAttrUserProfile
							.getAttributeValue()))) {
			    
				return getText("error.template.sameAttribute") 
				        + prefix
				        + validator.validate(forAttrUserProfile, String.valueOf(forAttrUserProfile
                        .getAttributeValue()));
			}
			UserProfileAttribute userProfileAttr = forAttrUserProfile.getUserProfileAttribute();
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& userProfileAttr != null) {
				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								if (setUsedAttrValue.contains(String.valueOf(addCount))) {
									return getText("error.template.sameAttribute")
									        + prefix
									        + validator.validate(forAttrUserProfile, String.valueOf(addCount));
								}
							}
						} else {
							if (setUsedAttrValue.contains(attrRange[0])) {
								return getText("error.template.sameAttribute")
								        + prefix
								        + validator.validate(forAttrUserProfile, attrRange[0]);
							}
						}
					}
				}

				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								setUsedAttrValue.add(String.valueOf(addCount));
								
								validator.updateAttr(forAttrUserProfile.getId(), String.valueOf(addCount));
							}
						} else {
							setUsedAttrValue.add(attrRange[0]);
							
							validator.updateAttr(forAttrUserProfile.getId(), attrRange[0]);
						}
					}
				}
			}
			setUsedUserProfile.add(forAttrUserProfile.getId().toString());
			setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
			
			validator.updateAttr(forAttrUserProfile.getId(), String.valueOf(forAttrUserProfile.getAttributeValue()));
			
			if (userProfileAttr != null) {
				userProfileCount.add(userProfileAttr.getId().toString());
			}
		}

		if (userProfileCount.size() > 64) {
			return getText("error.template.moreUserProfileAttributeGroup");
		}
		
		if (qosIds.size()>16) {
			return getText("error.template.moreUserProfileQos");
		}
		return "";
	}

	public String checkCacAirTime(Map<Long, UserProfile> mapUserProfile,Map<Long, UserProfile> mapUserProfileAssign) {
		int cacPercent = 0;
		if (getDataSource().getMgmtServiceOption() != null) {
			try {
				MgmtServiceOption mgtService = findBoById(MgmtServiceOption.class, getDataSource().getMgmtServiceOption().getId());
				if (!mgtService.getDisableCallAdmissionControl()){
					cacPercent = cacPercent
							+ mgtService.getRoamingGuaranteedAirtime();
				}
			} catch (Exception e) {
				//DO nothing
			}
		} else {
			cacPercent = cacPercent + 20;
		}
		Set<Long> setUserProfile = new HashSet<Long>();
		
		validator = new UserProfileAttrValidateImpl();
		
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()) {
			final SsidProfile ssidProfile = configTemplateSsid.getSsidProfile();
            if (ssidProfile != null) {
				if (ssidProfile.getUserProfileDefault() != null) {
					if (!setUserProfile.contains(ssidProfile.getUserProfileDefault().getId())) {
						cacPercent = cacPercent + ssidProfile.getUserProfileDefault().getGuarantedAirTime();
						setUserProfile.add(ssidProfile.getUserProfileDefault().getId());
						
                        validator.add(UserProfileAttrFactory.getPlainObjFromSSID(ssidProfile.getUserProfileDefault(),
                                                ssidProfile,
                                                UserProfileOnSSID.DEFAULT));
					}
				}
				if (ssidProfile.getUserProfileSelfReg() != null) {
					if (!setUserProfile.contains(ssidProfile.getUserProfileSelfReg().getId())) {
						cacPercent = cacPercent + ssidProfile.getUserProfileSelfReg().getGuarantedAirTime();
						setUserProfile.add(ssidProfile.getUserProfileSelfReg().getId());
						
                        validator.add(UserProfileAttrFactory.getPlainObjFromSSID(ssidProfile.getUserProfileSelfReg(),
                                ssidProfile,
                                UserProfileOnSSID.SELFREG));
					}
				}
				if (ssidProfile.getUserProfileGuest() != null) {
				    if (!setUserProfile.contains(ssidProfile.getUserProfileGuest().getId())) {
				        cacPercent = cacPercent + ssidProfile.getUserProfileGuest().getGuarantedAirTime();
				        setUserProfile.add(ssidProfile.getUserProfileGuest().getId());
				        
                        validator.add(UserProfileAttrFactory.getPlainObjFromSSID(ssidProfile.getUserProfileGuest(),
                                ssidProfile,
                                UserProfileOnSSID.GUEST));
				    }
				}
				if (ssidProfile.getRadiusUserProfile() != null) {
					for (UserProfile tempUser : ssidProfile
							.getRadiusUserProfile()) {
						if (!setUserProfile.contains(tempUser.getId())) {
							cacPercent = cacPercent + tempUser.getGuarantedAirTime();
							setUserProfile.add(tempUser.getId());
							
	                        validator.add(UserProfileAttrFactory.getPlainObjFromSSID(tempUser,
                                    ssidProfile,
                                    UserProfileOnSSID.AUTHENTICATION));
						}
					}
				}
			}
		}

		if (!getDataSource().getConfigType().isBonjourOnly()
				&& getDataSource().getPortProfiles()!=null) {
		    //TODO port templates
		}
		
		Set<Long> radiusServerUspId = new HashSet<Long>();
		/** UserProfile from Network Firewall */
		if(getDataSource().getFwPolicy() != null && getDataSource().getFwPolicy().getRules() != null){
			for(FirewallPolicyRule rule : getDataSource().getFwPolicy().getRules()){
				if(rule != null && !rule.isDisableRule() && rule.getSourceUp() != null){
					radiusServerUspId.add(rule.getSourceUp().getId());
				}
			}
		}
		
		// check reassign user profile
		if (!setUserProfile.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : setUserProfile) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (mapUserProfile.get(upObj.getId())==null) {
					mapUserProfile.put(upObj.getId(), upObj);
				}
				
				cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
						newSetUserProfile, setUserProfile, cacPercent);
			}
			for (Long upId : radiusServerUspId) {
				if (!setUserProfile.contains(upId) && !newSetUserProfile.contains(upId)) {
					UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);	
					newSetUserProfile.add(upId);
					if (mapUserProfileAssign.get(upId)==null) {
						mapUserProfileAssign.put(upId, upObj);
					}
					cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
							newSetUserProfile, setUserProfile, cacPercent);
				}
			}
		}
		
		if (cacPercent > 100) {
			return getText("error.template.guaranteedAirTime");
		}
		return "";
	}
	
	private int loadUserProfileFromDPRule(UserProfile upObj, 
			Map<Long, UserProfile> mapUserProfileAssign,
			Set<Long> newSetUserProfile,
			Set<Long> setUserProfile, int cacPercent){
		if (upObj.isEnableAssign()) {
			for (DevicePolicyRule rule : upObj.getAssignRules()) {
				if (!setUserProfile.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
					UserProfile upObj2 = QueryUtil.findBoById(UserProfile.class, rule.getUserProfileId(), this);
					cacPercent = cacPercent + upObj2.getGuarantedAirTime();
					newSetUserProfile.add(rule.getUserProfileId());
					if (mapUserProfileAssign.get(upObj2.getId())==null) {
						mapUserProfileAssign.put(upObj2.getId(), upObj2);
						
						validator.add(UserProfileAttrFactory.getReassginedObj(upObj2, upObj));
					}
					cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
							newSetUserProfile, setUserProfile, cacPercent);
				}
			}
		}
		return cacPercent;
	}
	
	private String checkTotalPskGroupSize() {
		long totalUserCount = ConfigTemplateAction.getTotalPskGroupId(getDataSource()).size();
		if (totalUserCount > LocalUserGroup.MAX_COUNT_AP_USERGROUPPERAP) {
			return getText("error.template.morePskGroupPerTemplate");
		}
		return "";
	}
	
	private String checkTotalPmkUserSize() {
		long count = ConfigTemplateAction.getTotalPmkUserSize(getDataSource());
		if (count > LocalUser.MAX_COUNT_AP30_USERPERAP) {
			return getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERPERAP)});
		}
		
		long totalCount = ConfigTemplateAction.getTotalPSKUserSize(getDataSource());
		if (totalCount > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
			return getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP)});
		}
		
		return "";
	}
	
	private boolean compareSameWithDB() {
		try {
			ConfigTemplate dbConfig = QueryUtil.findBoById(ConfigTemplate.class, getDataSource().getId(), new NetworkPolicyAction());
			if (dbConfig.getEnableAirTime()!=getDataSource().getEnableAirTime() 
					|| dbConfig.getEnabledMapOverride()!=getDataSource().getEnabledMapOverride()
					|| dbConfig.getEnableReportCollection()!=getDataSource().getEnableReportCollection()
					|| dbConfig.getSlaInterval()!=getDataSource().getSlaInterval()
					|| dbConfig.getClientExpireTime8021X() != getDataSource().getClientExpireTime8021X()
					|| dbConfig.getClientSuppressInterval8021X() != getDataSource().getClientSuppressInterval8021X()) {
//					|| dbConfig.isEnabledRouterPpskServer()!=getDataSource().isEnabledRouterPpskServer()) {
				return false;
			}
			
			if (dbConfig.getCollectionInterval()!=getDataSource().getCollectionInterval() 
					|| dbConfig.getCollectionIfCrc()!=getDataSource().getCollectionIfCrc() 
					|| dbConfig.getCollectionInterval()!=getDataSource().getCollectionInterval()
					|| dbConfig.getCollectionIfTxDrop()!=getDataSource().getCollectionIfTxDrop()
					|| dbConfig.getCollectionIfRxDrop()!=getDataSource().getCollectionIfRxDrop()
					|| dbConfig.getCollectionIfTxRetry()!=getDataSource().getCollectionIfTxRetry()
					|| dbConfig.getCollectionIfAirtime()!=getDataSource().getCollectionIfAirtime()
					|| dbConfig.getCollectionClientTxDrop()!=getDataSource().getCollectionClientTxDrop()
					|| dbConfig.getCollectionClientRxDrop()!=getDataSource().getCollectionClientRxDrop()
					|| dbConfig.getCollectionClientTxRetry()!=getDataSource().getCollectionClientTxRetry()
					|| dbConfig.getCollectionClientAirtime()!=getDataSource().getCollectionClientAirtime()){
				return false;
			}
			
			if (dbConfig.getTxRetryThreshold()!=getDataSource().getTxRetryThreshold()
					|| dbConfig.getTxFrameErrorThreshold()!=getDataSource().getTxFrameErrorThreshold()
					|| dbConfig.getProbRequestThreshold()!=getDataSource().getProbRequestThreshold()
					|| dbConfig.getEgressMulticastThreshold()!=getDataSource().getEgressMulticastThreshold()
					|| dbConfig.getIngressMulticastThreshold()!=getDataSource().getIngressMulticastThreshold()
					|| dbConfig.getChannelUtilizationThreshold()!=getDataSource().getChannelUtilizationThreshold()){
				return false;
			}
			
			if (!compareObj(dbConfig.getMgmtServiceDns(),getDataSource().getMgmtServiceDns())
					|| !compareObj(dbConfig.getMgmtServiceSyslog(),getDataSource().getMgmtServiceSyslog())
					|| !compareObj(dbConfig.getMgmtServiceSnmp(),getDataSource().getMgmtServiceSnmp())
					|| !compareObj(dbConfig.getMgmtServiceTime(),getDataSource().getMgmtServiceTime())
					|| !compareObj(dbConfig.getMgmtServiceOption(),getDataSource().getMgmtServiceOption())
					|| !compareObj(dbConfig.getClientWatch(),getDataSource().getClientWatch())
					|| !compareObj(dbConfig.getIdsPolicy(),getDataSource().getIdsPolicy())
					|| !compareObj(dbConfig.getVlan(),getDataSource().getVlan())
					|| !compareObj(dbConfig.getVlanNative(),getDataSource().getVlanNative())
					|| !compareObj(dbConfig.getIpFilter(),getDataSource().getIpFilter())
					|| !compareObj(dbConfig.getAccessConsole(),getDataSource().getAccessConsole())
					|| !compareObj(dbConfig.getDeviceServiceFilter(),getDataSource().getDeviceServiceFilter())
					|| !compareObj(dbConfig.getEth0ServiceFilter(),getDataSource().getEth0ServiceFilter())
					|| !compareObj(dbConfig.getWireServiceFilter(),getDataSource().getWireServiceFilter())
					|| !compareObj(dbConfig.getEth0BackServiceFilter(),getDataSource().getEth0BackServiceFilter())
					|| !compareObj(dbConfig.getEth1BackServiceFilter(),getDataSource().getEth1BackServiceFilter())
					|| !compareObj(dbConfig.getRed0BackServiceFilter(),getDataSource().getRed0BackServiceFilter())
					|| !compareObj(dbConfig.getAgg0BackServiceFilter(),getDataSource().getAgg0BackServiceFilter())
					|| !compareObj(dbConfig.getLocationServer(),getDataSource().getLocationServer())
					|| !compareObj(dbConfig.getAlgConfiguration(),getDataSource().getAlgConfiguration())
					|| !compareObj(dbConfig.getClassifierMap(),getDataSource().getClassifierMap())
					|| !compareObj(dbConfig.getMarkerMap(),getDataSource().getMarkerMap())
//					|| !compareObj(dbConfig.getRouterIpTrack(),getDataSource().getRouterIpTrack())
					|| !compareObj(dbConfig.getEth1ServiceFilter(),getDataSource().getEth1ServiceFilter())
					|| !compareObj(dbConfig.getRed0ServiceFilter(),getDataSource().getRed0ServiceFilter())
					|| !compareObj(dbConfig.getAgg0ServiceFilter(),getDataSource().getAgg0ServiceFilter())
					|| !compareObj(dbConfig.getVpnService(),getDataSource().getVpnService())
					|| !compareObj(dbConfig.getLldpCdp(),getDataSource().getLldpCdp())
					|| !compareObj(dbConfig.getRadiusProxyProfile(),getDataSource().getRadiusProxyProfile())
					|| !compareObj(dbConfig.getRadiusServerProfile(),getDataSource().getRadiusServerProfile())
					|| !compareObj(dbConfig.getRoutingProfilePolicy(),getDataSource().getRoutingProfilePolicy())
					|| !compareObj(dbConfig.getFwPolicy(),getDataSource().getFwPolicy()) 
					|| !compareObj(dbConfig.getAppProfile(),getDataSource().getAppProfile())
					|| !compareObj(dbConfig.getRadiusAttrs(),getDataSource().getRadiusAttrs())
					|| !compareObj(dbConfig.getBonjourGw(), getDataSource().getBonjourGw())) {
				return false;
			}
			
			if (dbConfig.isEnableProbe()!=getDataSource().isEnableProbe()
					|| dbConfig.getProbeInterval()!=getDataSource().getProbeInterval()
					|| dbConfig.getProbeRetryCount()!=getDataSource().getProbeRetryCount()	
					|| dbConfig.getProbeRetryInterval()!=getDataSource().getProbeRetryInterval()
					|| !dbConfig.getProbeUsername().equals(getDataSource().getProbeUsername())
					|| !dbConfig.getProbePassword().equals(getDataSource().getProbePassword())
					|| dbConfig.isEnableOSDURL()!=getDataSource().isEnableOSDURL()
					|| dbConfig.isEnableTVService()!=getDataSource().isEnableTVService()
					|| dbConfig.isEnableHttpServer()!=getDataSource().isEnableHttpServer()
					/*|| dbConfig.isBlnWirelessRouter()!=getDataSource().isBlnWirelessRouter()
					|| dbConfig.isBlnBonjourOnly()!=getDataSource().isBlnBonjourOnly()) {*/
					|| !dbConfig.getConfigType().equals(getDataSource().getConfigType())) {
				return false;
			}
			
			//for VoIP
			if(dbConfig.isEnableEth0LimitDownloadBandwidth()!=getDataSource().isEnableEth0LimitDownloadBandwidth()
					|| dbConfig.isEnableEth0LimitUploadBandwidth()!=getDataSource().isEnableEth0LimitUploadBandwidth()
					|| dbConfig.getEth0LimitDownloadRate()!=getDataSource().getEth0LimitDownloadRate()
					|| dbConfig.getEth0LimitUploadRate()!=getDataSource().getEth0LimitUploadRate()
					|| dbConfig.isEnableUSBLimitDownloadBandwidth()!=getDataSource().isEnableUSBLimitDownloadBandwidth()
					|| dbConfig.isEnableUSBLimitUploadBandwidth()!=getDataSource().isEnableUSBLimitUploadBandwidth()
					|| dbConfig.getUsbLimitDownloadRate()!=getDataSource().getUsbLimitDownloadRate()
					|| dbConfig.getUsbLimitUploadRate()!=getDataSource().getUsbLimitUploadRate()){
				return false;
			}
									
			if (dbConfig.getPortProfiles().size()!= getDataSource().getPortProfiles().size()
					|| !dbConfig.getPortProfiles().containsAll(getDataSource().getPortProfiles())) {
				return false;
			}else{
				// for non-default
				if(compareNonDefault(dbConfig.getPortProfiles(),getDataSource().getPortProfiles())){
					return false;
				}
			}
			
			if (dbConfig.getTvNetworkService().size()!= getDataSource().getTvNetworkService().size()
					|| !dbConfig.getTvNetworkService().containsAll(getDataSource().getTvNetworkService())) {
				return false;
			}
			
			if (dbConfig.getIpTracks().size()!= getDataSource().getIpTracks().size()
					|| !dbConfig.getIpTracks().containsAll(getDataSource().getIpTracks())) {
				return false;
			}
			if (dbConfig.getSsidInterfaces().size()!= getDataSource().getSsidInterfaces().size()
					|| !dbConfig.getSsidInterfaces().keySet().containsAll(getDataSource().getSsidInterfaces().keySet())) {
				return false;
			}
			
			for (Long keyValue: dbConfig.getSsidInterfaces().keySet()){
				if (!compareConfigTemplateSsid(dbConfig.getSsidInterfaces().get(keyValue),getDataSource().getSsidInterfaces().get(keyValue))) {
					return false;
				}
			}
			if (dbConfig.getVlanNetwork().size()!= getDataSource().getVlanNetwork().size()
					|| !dbConfig.getVlanNetwork().containsAll(getDataSource().getVlanNetwork())) {
				return false;
			}
			if (dbConfig.getUpVlanMapping().size()!= getDataSource().getUpVlanMapping().size()
					|| !dbConfig.getUpVlanMapping().containsAll(getDataSource().getUpVlanMapping())) {
				return false;
			}
			
			//storm Control
			if(dbConfig.getStormControlList().size() != getDataSource().getStormControlList().size()
					|| !dbConfig.getStormControlList().containsAll(getDataSource().getStormControlList())){
				return false;
			}
			
			// stp settings
			if (checkSwitchSettingsEnabled(dbConfig)) {
				if (dbConfig.getSwitchSettings() != null) {
					if ((!compareObj(dbConfig.getSwitchSettings(),
							getDataSource().getSwitchSettings()))) {
						return false;
					}

					if (dbConfig.getSwitchSettings().getStpSettings() != null) {
						if (!compareObj(dbConfig.getSwitchSettings()
								.getStpSettings(), getDataSource()
								.getSwitchSettings().getStpSettings())) {
							return false;
						}
						if (dbConfig.getSwitchSettings().getStpSettings()
								.getMstpRegion() != null) {
							if (!compareObj(dbConfig.getSwitchSettings()
									.getStpSettings().getMstpRegion(),
									getDataSource().getSwitchSettings()
											.getStpSettings().getMstpRegion())) {
								return false;
							}

							if (dbConfig.getSwitchSettings().getStpSettings()
									.getMstpRegion()
									.getMstpRegionPriorityList().size() != getDataSource()
									.getSwitchSettings().getStpSettings()
									.getMstpRegion()
									.getMstpRegionPriorityList().size()
									|| !dbConfig
											.getSwitchSettings()
											.getStpSettings()
											.getMstpRegion()
											.getMstpRegionPriorityList()
											.containsAll(
													getDataSource()
															.getSwitchSettings()
															.getStpSettings()
															.getMstpRegion()
															.getMstpRegionPriorityList())) {
								return false;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("compareSameWithDB", e);
			return false;
		}
		return true;
	}
	private boolean compareNonDefault(Set<PortGroupProfile> db_portProfiles,
			Set<PortGroupProfile> session_portProfiles) {
		int itHave = 0;
		for( PortGroupProfile portProfile: db_portProfiles){
			if(session_portProfiles.contains(portProfile)){
				for(PortGroupProfile session_portProfile: session_portProfiles){
					if(portProfile.getId().equals(session_portProfile.getId())){
						List<SingleTableItem> db_items = portProfile.getCurrentPolicyItems(getDataSource().getId());
						List<SingleTableItem> session_items = session_portProfile.getCurrentPolicyItems(getDataSource().getId());
						if(null != db_items && null !=session_items){
							if(db_items.size()!= session_items.size()){
								++itHave;
								break;
							}else{
								if(!db_items.containsAll(session_items)){
									++itHave;
									break;
								}
							}
						}
					}
				}
			}
		}
		return itHave == 0 ? false: true;
	}

	private boolean compareObj(HmBo one, HmBo two) {
		if (one==null && two==null) {
			return true;
		} else if (one!=null && two!=null && one.getId().equals(two.getId())) {
			return true;
		}
		return false;
	}
	
	private boolean compareConfigTemplateSsid(ConfigTemplateSsid one, ConfigTemplateSsid two) {
		if (one==null && two==null) {
			return true;
		} else if (one!=null && two!=null) {
			if(one.getCheckD()!= two.getCheckD()
					|| one.getCheckDT()!=two.getCheckDT()
					|| one.getCheckE()!=two.getCheckE()
					|| one.getCheckET()!=two.getCheckET()
					|| one.getCheckP()!=two.getCheckP()
					|| one.getCheckPT()!=two.getCheckPT()
					|| one.getMacOuisEnabled()!=two.getMacOuisEnabled()
					|| one.getNetworkServicesEnabled()!=two.getNetworkServicesEnabled()
					|| one.getSsidEnabled()!=two.getSsidEnabled()
					|| one.getSsidOnlyEnabled() !=two.getSsidOnlyEnabled()) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private Set<Long> selectSsidIds = new HashSet<Long>();
	
//	private Set<Long> selectVlanNetworkVlanIds = new HashSet<Long>();
	
	private List<CheckItem> vlanUserAddList = new ArrayList<CheckItem>();
	
	private Long selectNetworkId;
	
	private Long selectFwPolicyId;
	
	private Long selectBonjourGwId;
	
	private Long selectRadiusServerId;
	
	private Long ssidForRs;
	
	private short radiusTypeFlag;
	
	private String radiusServerName;
	
	public short getRadiusTypeFlag()
	{
		return radiusTypeFlag;
	}

	public void setRadiusTypeFlag(short radiusTypeFlag)
	{
		this.radiusTypeFlag = radiusTypeFlag;
	}

	public Long getSsidForRs()
	{
		return ssidForRs;
	}

	public void setSsidForRs(Long ssidForRs)
	{
		this.ssidForRs = ssidForRs;
	}

	public Long getSelectRadiusServerId()
	{
		return selectRadiusServerId;
	}

	public void setSelectRadiusServerId(Long selectRadiusServerId)
	{
		this.selectRadiusServerId = selectRadiusServerId;
	}

	public Long getSelectFwPolicyId()
	{
		return selectFwPolicyId;
	}

	public void setSelectFwPolicyId(Long selectFwPolicyId)
	{
		this.selectFwPolicyId = selectFwPolicyId;
	}
	
	public String getRadiusServerName() {
        return radiusServerName;
    }

    public void setRadiusServerName(String radiusServerName) {
        this.radiusServerName = radiusServerName;
    }

    public List<CheckItem> getAvailableFwPolicy() {
		return getBoCheckItems("policyName", FirewallPolicy.class, null);
	}
	
	public List<CheckItem> getAvailableRadiusServer() {
		return getBoCheckItems("radiusName", RadiusAssignment.class, null);
	}

	public List<CheckItem> getSsidProfilesList() throws Exception {
		return getBoCheckItems("ssidName", SsidProfile.class, new FilterParams("defaultFlag", false));
	}
	
	public List<CheckItem> getNetworkObjectList() throws Exception {
		List<Long> filter = new ArrayList<Long>();
		if (getDataSource()!=null && getDataSource().getVlanNetwork()!=null) {
			for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (getDataSource().getPreVlanId()==null || !cvn.getVlan().getId().equals(getDataSource().getPreVlanId())) {
					if (cvn.getNetworkObj()!=null){
					filter.add(cvn.getNetworkObj().getId());
				}
			}
		}
		}
		if (filter.isEmpty()) {
			return getBoCheckItems("networkName", VpnNetwork.class, null, new SortParams("networkName"));
		} else {
			return getBoCheckItems("networkName", VpnNetwork.class, new FilterParams("id not ", filter),new SortParams("networkName"));
		}
	}
	
   public List<CheckItem> getAvailableBonjourGw() {
        return getBoCheckItems("bonjourGwName", BonjourGatewaySettings.class, null);
    }
	
	public String getUpdateDisabled() {
	if ("".equals(getWriteDisabled())) {
		return getDataSource().isDefaultFlag() ? "disabled" : "";
	}
	return "disabled";
	}
	
	public String getChangedConfigName() {
		return getDataSource().getConfigName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	private Long createSsidId;
	private boolean refreshFlg=false;
	
	private boolean saveAlways= false;

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof ConfigTemplate) {
			dataSource = bo;
			// Just calling the get method will fetch the LAZY attributes
			// bo.getUserProfileAttribute();
			// Call additional LAZY methods
			if (getDataSource().getOwner() != null)
				getDataSource().getOwner().getId();
			if (getDataSource().getHiveProfile() != null)
				getDataSource().getHiveProfile().getId();
			if (getDataSource().getMgmtServiceDns() != null)
				getDataSource().getMgmtServiceDns().getId();
			if (getDataSource().getMgmtServiceSyslog() != null)
				getDataSource().getMgmtServiceSyslog().getId();
			if (getDataSource().getMgmtServiceSnmp() != null)
				getDataSource().getMgmtServiceSnmp().getId();
			if (getDataSource().getMgmtServiceTime() != null)
				getDataSource().getMgmtServiceTime().getId();
			if (getDataSource().getMgmtServiceOption() != null)
				getDataSource().getMgmtServiceOption().getId();
			if (getDataSource().getIdsPolicy() != null)
				getDataSource().getIdsPolicy().getId();
			// if (getDataSource().getRadiusAssignment() != null)
			// getDataSource().getRadiusAssignment().getId();
			if (getDataSource().getVlan() != null)
				getDataSource().getVlan().getId();
			if (getDataSource().getVlanNative() != null)
				getDataSource().getVlanNative().getId();
			if (getDataSource().getIpFilter() != null)
				getDataSource().getIpFilter().getId();
			if (getDataSource().getAccessConsole() != null)
				getDataSource().getAccessConsole().getId();
			if (getDataSource().getDeviceServiceFilter() != null)
				getDataSource().getDeviceServiceFilter().getId();
			if (getDataSource().getEth0ServiceFilter() != null)
				getDataSource().getEth0ServiceFilter().getId();
			if (getDataSource().getWireServiceFilter() != null)
				getDataSource().getWireServiceFilter().getId();
			if (getDataSource().getEth0BackServiceFilter() != null)
				getDataSource().getEth0BackServiceFilter().getId();
			if (getDataSource().getEth1BackServiceFilter() != null)
				getDataSource().getEth1BackServiceFilter().getId();
			if (getDataSource().getRed0BackServiceFilter() != null)
				getDataSource().getRed0BackServiceFilter().getId();
			if (getDataSource().getAgg0BackServiceFilter() != null)
				getDataSource().getAgg0BackServiceFilter().getId();
			if (getDataSource().getAlgConfiguration() != null)
				getDataSource().getAlgConfiguration().getId();
			if (getDataSource().getLocationServer() != null)
				getDataSource().getLocationServer().getId();
			if (getDataSource().getLldpCdp() != null)
				getDataSource().getLldpCdp().getId();
			 if (getDataSource().getRadiusProxyProfile() != null)
				 getDataSource().getRadiusProxyProfile().getId();
			 if (getDataSource().getRadiusServerProfile() != null)
				 getDataSource().getRadiusServerProfile().getId();
			 if (getDataSource().getRoutingProfilePolicy() != null)
				 getDataSource().getRoutingProfilePolicy().getId();
			if (getDataSource().getClientWatch() != null)
				getDataSource().getClientWatch().getId();
			if (getDataSource().getClassifierMap() != null)
				getDataSource().getClassifierMap().getId();
			if (getDataSource().getMarkerMap() != null)
				getDataSource().getMarkerMap().getId();
			if (getDataSource().getEth1ServiceFilter() != null)
				getDataSource().getEth1ServiceFilter().getId();
			if (getDataSource().getAgg0ServiceFilter() != null)
				getDataSource().getAgg0ServiceFilter().getId();
			if (getDataSource().getRed0ServiceFilter() != null)
				getDataSource().getRed0ServiceFilter().getId();
//			if(getDataSource().getRouterIpTrack()!=null) {
//				getDataSource().getRouterIpTrack().getId();
//			}
			
			if (getDataSource().getVpnService() != null){
				getDataSource().getVpnService().getId();
				if (getDataSource().getVpnService().getVpnGateWaysSetting()!=null) {
					getDataSource().getVpnService().getVpnGateWaysSetting().size();
				}
			}
			
			if (getDataSource().getFwPolicy() != null){
				getDataSource().getFwPolicy().getId();
				if (getDataSource().getFwPolicy().getRules()!=null) {
					getDataSource().getFwPolicy().getRules().size();
				}
			}
			getDataSource().getVlanNetwork().size();
			for(ConfigTemplateVlanNetwork cvn: getDataSource().getVlanNetwork()){
				if (cvn.getNetworkObj()!=null) {
					cvn.getNetworkObj().getId();
				}
				if (cvn.getVlan()!=null) {
					cvn.getVlan().getId();
				}
			}
			
			if (getDataSource().getSsidInterfaces() != null) {
				getDataSource().getSsidInterfaces().values();
				if (getDataSource().getSsidInterfaces().values() != null) {
					for (ConfigTemplateSsid tmpTemplate : getDataSource()
							.getSsidInterfaces().values()) {
                        loadLazySsidProfile(tmpTemplate.getSsidProfile());
					}
				}
			}
			getDataSource().getIpTracks().size();
			getDataSource().getTvNetworkService().size();
			// load Port Template profiles
			if (!getDataSource().getPortProfiles().isEmpty()) {
				getDataSource().getPortProfiles().size();
				for (PortGroupProfile portProfile : getDataSource().getPortProfiles()) {
					loadLazyPortProfile(portProfile);
				}
			}
			if (getDataSource().getRadiusAttrs() != null)
				getDataSource().getRadiusAttrs().getId();
			if(null != getDataSource().getBonjourGw()) {
			    getDataSource().getBonjourGw().getId();
			}
			if (getDataSource().getAppProfile() != null) {
				getDataSource().getAppProfile().getId();
			}		
			if(getDataSource().getStormControlList() != null){
				getDataSource().getStormControlList().size();
//				for(ConfigTemplateStormControl stormControl :getDataSource().getConfigTemplateStormControl()){
//					stormControl.getConfigTemplate().getId();
//				}
				
			}
			
			if (this.getDataSource().getUpVlanMapping() != null) {
				this.getDataSource().getUpVlanMapping().size();
				for (UserProfileVlanMapping mapping : this.getDataSource().getUpVlanMapping()) {
					if (mapping.getUserProfile() != null) {
						mapping.getUserProfile().getId();
						if (mapping.getUserProfile().getAssignRules() != null) {
							mapping.getUserProfile().getAssignRules().size();
						}
					}
					if (mapping.getVlan() != null) {
						mapping.getVlan().getId();
					}
				}
			}
			
			if (this.getDataSource().getSwitchSettings() != null) {
				this.getDataSource().getSwitchSettings().getId();
				if (this.getDataSource().getSwitchSettings().getStpSettings() != null) {
					if (getDataSource().getSwitchSettings().getStpSettings()
							.getMstpRegion() != null) {
						getDataSource().getSwitchSettings().getStpSettings()
								.getMstpRegion().getId();
						getDataSource().getSwitchSettings().getStpSettings()
								.getMstpRegion().getMstpRegionPriorityList()
								.size();
					}
				}
			}
			
			if(null != getDataSource().getSupplementalCLI()){
				getDataSource().getSupplementalCLI().getId();
			}
		}
		
		if (bo instanceof SwitchSettings) {
			SwitchSettings switchSettings = (SwitchSettings) bo;
			if (switchSettings != null) {
				switchSettings.getId();
				if (switchSettings.getStpSettings() != null) {
					if (switchSettings.getStpSettings().getMstpRegion() != null) {
						switchSettings.getStpSettings().getMstpRegion().getId();
						switchSettings.getStpSettings().getMstpRegion().getMstpRegionPriorityList().size();
					}
				}
			}
		}
		
		if (bo instanceof StpSettings) {
			StpSettings stpSettings = (StpSettings) bo;
			if (stpSettings != null) {
				if (stpSettings.getMstpRegion() != null) {
					stpSettings.getMstpRegion().getId();
					stpSettings.getMstpRegion().getMstpRegionPriorityList().size();
				}
			}
		}
		
		if (bo instanceof MstpRegion) {
			MstpRegion mstpRegion = (MstpRegion) bo;
			if (mstpRegion != null) {
				mstpRegion.getId();
				mstpRegion.getMstpRegionPriorityList().size();
			}
		}
		
	

		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
			if (userp.getVlan()!=null) {
				userp.getVlan().getId();
			}
			if (userp.getQosRateControl() != null)
				userp.getQosRateControl().getId();
			if (userp.getIpPolicyFrom() != null)
				userp.getIpPolicyFrom().getId();
			if (userp.getIpPolicyTo() != null)
				userp.getIpPolicyTo().getId();
			if (userp.getMacPolicyFrom() != null)
				userp.getMacPolicyFrom().getId();
			if (userp.getMacPolicyTo() != null)
				userp.getMacPolicyTo().getId();
			if (userp.getUserProfileAttribute() != null){
				userp.getUserProfileAttribute().getId();
				if (userp.getUserProfileAttribute().getItems()!=null) {
					userp.getUserProfileAttribute().getItems().size();
				}
			}
			if (null != userp.getAssignRules()) {
				userp.getAssignRules().size();
			}
		}

		if (bo instanceof SsidProfile) {
			SsidProfile ssid = (SsidProfile) bo;
			if (ssid.getRadiusUserProfile() != null){
				ssid.getRadiusUserProfile().size();
				for(UserProfile us: ssid.getRadiusUserProfile()){
					if (us.getVlan()!=null) {
						us.getVlan().getId();
					}
					if (us.getAssignRules()!=null) {
						us.getAssignRules().size();
					}
				}
			}
			if (null != ssid.getLocalUserGroups()) {
				ssid.getLocalUserGroups().size();
			}
			if (null != ssid.getRadiusUserGroups()) {
				ssid.getRadiusUserGroups().size();
			}
			if(null !=ssid.getSchedulers()){
				ssid.getSchedulers().size();
			}
			if(null !=ssid.getMacFilters()){
				ssid.getMacFilters().size();
			}
			if (null != ssid.getUserProfileDefault()){
				if (ssid.getUserProfileDefault().getVlan()!=null) {
					ssid.getUserProfileDefault().getVlan().getId();
				}
				if (ssid.getUserProfileDefault().getAssignRules()!=null) {
					ssid.getUserProfileDefault().getAssignRules().size();
				}
			}
			
			if (null != ssid.getUserProfileSelfReg()){
				if (ssid.getUserProfileSelfReg().getVlan()!=null) {
					ssid.getUserProfileSelfReg().getVlan().getId();
				}
				if (ssid.getUserProfileSelfReg().getAssignRules()!=null) {
					ssid.getUserProfileSelfReg().getAssignRules().size();
				}
			}
			if (null != ssid.getUserProfileGuest()){
			    if (ssid.getUserProfileGuest().getVlan()!=null) {
			        ssid.getUserProfileGuest().getVlan().getId();
			    }
			    if (ssid.getUserProfileGuest().getAssignRules()!=null) {
			        ssid.getUserProfileGuest().getAssignRules().size();
			    }
			}
		}
		if (bo instanceof EthernetAccess) {
			EthernetAccess ethe = (EthernetAccess) bo;
			if (ethe.getUserProfile() != null)
				ethe.getUserProfile().getId();
		}
		
		if (bo instanceof FirewallPolicy) {
			FirewallPolicy fw = (FirewallPolicy) bo;
			if (fw.getRules()!=null) {
				fw.getRules().size();
			}
		}
		
		if(bo instanceof QosRateControl) {
			QosRateControl qosRate = (QosRateControl)bo;
			
			if(qosRate.getQosRateLimit() != null) {
				qosRate.getQosRateLimit().size();
			}
		}
		
		if (bo instanceof HiveProfile) {
			HiveProfile hiveProfileTmp = (HiveProfile)bo;
			if (hiveProfileTmp.getMacFilters()!=null) {
				hiveProfileTmp.getMacFilters().size();
			}
		}
		
		if(bo instanceof PortGroupProfile) {
			loadLazyPortProfile((PortGroupProfile) bo);
		}
		
		if(bo instanceof PortAccessProfile) {
		    loadLazyPortAccessProfile((PortAccessProfile) bo);
		}
		
		if (bo instanceof HiveAp) {
			HiveAp ap = (HiveAp) bo;
			if (ap.getRoutingProfile() != null){
				ap.getRoutingProfile().getId();
			}
			if (ap.getDeviceInterfaces() != null){
				ap.getDeviceInterfaces().values();
			}
		}
		
		if (bo instanceof VpnService) {
			VpnService pf = (VpnService) bo;
			if (pf.getVpnGateWaysSetting()!=null) {
				pf.getVpnGateWaysSetting().size();
			}
		}
		
		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
		
		if (bo instanceof VpnNetwork) {
			VpnNetwork vpnNetwork = (VpnNetwork) bo;
			if (vpnNetwork.getVpnDnsService()!=null) {
				vpnNetwork.getVpnDnsService().getId();
			}
			if (vpnNetwork.getSubItems()!=null) {
				vpnNetwork.getSubItems().size();
			}
		}
		
		if (bo instanceof Vlan) {
			Vlan profile = (Vlan) bo;
			if (profile.getItems()!=null) {
				profile.getItems().size();
			}
		}
		
		if (bo instanceof RadiusAssignment) {
		    RadiusAssignment aaaClient = (RadiusAssignment) bo;
		    if(aaaClient.getServices() != null) {
		        aaaClient.getServices().size();
		        for (RadiusServer server : aaaClient.getServices()) {
                    server.getIpAddress().getId();
                    if(null != server.getIpAddress().getItems()) {
                        server.getIpAddress().getItems().size();
                    }
		        }
		    }
		}
			 
		return null;
	}

    private void loadLazySsidProfile(final SsidProfile ssidProfile) {
        if (ssidProfile != null) {
            if (ssidProfile.getRadiusUserProfile() != null) {
                ssidProfile.getRadiusUserProfile().size();
                for (UserProfile us : ssidProfile.getRadiusUserProfile()) {
                    if (us.getVlan() != null) {
                        us.getVlan().getId();
                    }

                    if (null != us.getAssignRules()) {
                        us.getAssignRules().size();
                    }
                }
            }
            if (null != ssidProfile.getLocalUserGroups()) {
                ssidProfile.getLocalUserGroups().size();
            }
            if (null != ssidProfile.getRadiusUserGroups()) {
                ssidProfile.getRadiusUserGroups().size();
            }
            if (null != ssidProfile.getMacFilters()) {
                ssidProfile.getMacFilters().size();
            }
        	if (ssidProfile.getUserProfileDefault() != null) {
        		ssidProfile.getUserProfileDefault().getId();
        		if (ssidProfile.getUserProfileDefault().getVlan()!=null) {
        			ssidProfile.getUserProfileDefault().getVlan().getId();
        		}

        		if (null != ssidProfile.getUserProfileDefault().getAssignRules()) {
        			ssidProfile.getUserProfileDefault().getAssignRules().size();
        		}
        	}
        	if (ssidProfile.getUserProfileSelfReg() != null) {
        		ssidProfile.getUserProfileSelfReg().getId();
        		if (ssidProfile.getUserProfileSelfReg().getVlan()!=null) {
        			ssidProfile.getUserProfileSelfReg().getVlan().getId();
        		}
 
        		if (null != ssidProfile.getUserProfileSelfReg().getAssignRules()) {
        			ssidProfile.getUserProfileSelfReg().getAssignRules().size();
        		}
        	}
        	if (ssidProfile.getUserProfileGuest() != null) {
        	    ssidProfile.getUserProfileGuest().getId();
        	    if (ssidProfile.getUserProfileGuest().getVlan()!=null) {
        	        ssidProfile.getUserProfileGuest().getVlan().getId();
        	    }
        	    
        	    if (null != ssidProfile.getUserProfileGuest().getAssignRules()) {
        	        ssidProfile.getUserProfileGuest().getAssignRules().size();
        	    }
        	}
        	if (ssidProfile.getSchedulers() != null) {
        		ssidProfile.getSchedulers().size();
        	}
        }
    }

    private void loadLazyPortProfile(PortGroupProfile portProfile) {
        // Port Template Profiles
        if(null != portProfile) {
            if(!portProfile.getBasicProfiles().isEmpty()) {
                portProfile.getBasicProfiles().size();
                for(PortBasicProfile basicProfile : portProfile.getBasicProfiles()) {
                    PortAccessProfile accessProfile = basicProfile.getAccessProfile();
                    loadLazyPortAccessProfile(accessProfile);
                }
            }
            if(!portProfile.getItems().isEmpty()){
            	portProfile.getItems().size();
                List<SingleTableItem> items = portProfile.getItems();
                if(null == items || items.isEmpty()) {
                } else {
                    // refresh cache
                    for (SingleTableItem item : items) {
                		if(null != item && -1 != item.getNonGlobalId() 
                				&& item.getConfigTemplateId() == getDataSource().getId().longValue()){
                    		//item.setNonDefault(QueryUtil.findBoById(PortGroupProfile.class, item.getNonGlobalId(), this));
                    	}
                }
                }
            }
            if(!portProfile.getPortPseProfiles().isEmpty()) {
                portProfile.getPortPseProfiles().size();
            }
            if(!portProfile.getMonitorProfiles().isEmpty()) {
                portProfile.getMonitorProfiles().size();
            }
        }
    }

	private void loadLazyPortAccessProfile(PortAccessProfile accessProfile) {
        if (null != accessProfile) {
            accessProfile.getId();
            if (null != accessProfile.getRadiusAssignment()) {
                accessProfile.getRadiusAssignment().getId();
            }
            if (null != accessProfile.getCwp()) {
                accessProfile.getCwp().getId();
            }
            if (null != accessProfile.getNativeVlan()) {
                accessProfile.getNativeVlan().getId();
            }
            if (null != accessProfile.getDefUserProfile()) {
                accessProfile.getDefUserProfile().getId();
                if(null != accessProfile.getDefUserProfile().getVlan()) {
                    accessProfile.getDefUserProfile().getVlan().getId();
                }
                if(!accessProfile.getDefUserProfile().getAssignRules().isEmpty()) {
                    accessProfile.getDefUserProfile().getAssignRules().size();
                }
            }
            if (null != accessProfile.getSelfRegUserProfile()) {
                accessProfile.getSelfRegUserProfile().getId();
                if(null != accessProfile.getSelfRegUserProfile().getVlan()) {
                    accessProfile.getSelfRegUserProfile().getVlan().getId();
                }
                if(!accessProfile.getSelfRegUserProfile().getAssignRules().isEmpty()) {
                    accessProfile.getSelfRegUserProfile().getAssignRules().size();
                }
            }
            if (null != accessProfile.getGuestUserProfile()) {
                accessProfile.getGuestUserProfile().getId();
                if(null != accessProfile.getGuestUserProfile().getVlan()) {
                    accessProfile.getGuestUserProfile().getVlan().getId();
                }
                if(!accessProfile.getGuestUserProfile().getAssignRules().isEmpty()) {
                    accessProfile.getGuestUserProfile().getAssignRules().size();
                }
            }
            if (!accessProfile.getAuthOkUserProfile().isEmpty()) {
                accessProfile.getAuthOkUserProfile().size();
                for (UserProfile us : accessProfile.getAuthOkUserProfile()) {
                    if(null != us.getVlan()) {
                        us.getVlan().getId();
                    }
                    if(!us.getAssignRules().isEmpty()) {
                        us.getAssignRules().size();
                    }
                 }
            }
            if (!accessProfile.getAuthFailUserProfile().isEmpty()) {
                accessProfile.getAuthFailUserProfile().size();
                for (UserProfile us : accessProfile.getAuthFailUserProfile()) {
                    if(null != us.getVlan()) {
                        us.getVlan().getId();
                    }
                    if(!us.getAssignRules().isEmpty()) {
                        us.getAssignRules().size();
                    }
                }
            }
            if (null != accessProfile.getVoiceVlan()) {
                accessProfile.getVoiceVlan().getId();
            }
            if (null != accessProfile.getDataVlan()) {
                accessProfile.getDataVlan().getId();
            }
            if (!accessProfile.getAuthOkDataUserProfile().isEmpty()) {
                accessProfile.getAuthOkDataUserProfile().size();
                for (UserProfile us : accessProfile.getAuthOkDataUserProfile()) {
                    if(null != us.getVlan()) {
                        us.getVlan().getId();
                    }
                    if(!us.getAssignRules().isEmpty()) {
                        us.getAssignRules().size();
                    }
                }
            }
            if (!accessProfile.getRadiusUserGroups().isEmpty()) {
                accessProfile.getRadiusUserGroups().size();
            }
        }
    }
    
	public Set<Long> getSelectSsidIds() {
		return selectSsidIds;
	}
	
	public void setSelectSsidIds(Set<Long> selectSsidIds) {
		this.selectSsidIds = selectSsidIds;
	}
	
	public List<CheckItem> getNetworkPolicyList() throws Exception {
		//if the current dataSource is not a newly added session dataSource, fetch all from database
		//return getBoCheckItems("configName", ConfigTemplate.class, null);
		return getBoCheckItems("configName", ConfigTemplate.class, new FilterParams("defaultFlag", false));
	}
	
	private Long defaultConfigTemplateId;

	public Long getDefaultConfigTemplateId() {
		return defaultConfigTemplateId;
	}

	public void setDefaultConfigTemplateId(Long defaultConfigTemplateId) {
		this.defaultConfigTemplateId = defaultConfigTemplateId;
	}
	
	private Long selectedNetworkPolicyId = null;

	public Long getSelectedNetworkPolicyId() {
		return selectedNetworkPolicyId;
	}

	public void setSelectedNetworkPolicyId(Long selectedNetworkPolicyId) {
		this.selectedNetworkPolicyId = selectedNetworkPolicyId;
	}

	public Long getCreateSsidId() {
		return createSsidId;
	}

	public void setCreateSsidId(Long createSsidId) {
		this.createSsidId = createSsidId;
	}

	public boolean isRefreshFlg() {
		return refreshFlg;
	}

	public void setRefreshFlg(boolean refreshFlg) {
		this.refreshFlg = refreshFlg;
	}
	
	private List<CheckItem> list_hive;

	public List<CheckItem> getList_hive() {
		return list_hive;
	}

	public void setList_hive(List<CheckItem> list_hive) {
		this.list_hive = list_hive;
	}
	
	private void prepareHiveList() {
		list_hive = getBoCheckItems("hiveName", HiveProfile.class, null);
		// add not secure to hive0
		if (list_hive != null && !list_hive.isEmpty()) {
			for (int i = 0; i < list_hive.size(); i++) {
				CheckItem item = list_hive.get(i);
				if ("hive0".equals(item.getValue())) {
					CheckItem ckItem = new CheckItem(item.getId(), item.getValue()+" (not secure)");
					list_hive.remove(i);
					list_hive.add(i, ckItem);
					break;
				}
			}
		}
	}
	
	private String configName;
	private String description;
	private Long hiveId;
	private Long networkPolicyId;
	private boolean save2Db;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getHiveId() {
		return hiveId;
	}

	public void setHiveId(Long hiveId) {
		this.hiveId = hiveId;
	}
	
	public Long getNetworkPolicyId() {
		return networkPolicyId;
	}

	public void setNetworkPolicyId(Long networkPolicyId) {
		this.networkPolicyId = networkPolicyId;
	}
	
	public boolean isSave2Db() {
		return save2Db;
	}

	public void setSave2Db(boolean save2Db) {
		this.save2Db = save2Db;
	}

	/*
	 * QoS Setting begin
	 */
	private Long classifierMapId;
	private Long markerMapId;
	
	public Long getClassifierMapId()
	{
		return classifierMapId;
	}

	public void setClassifierMapId(Long classifierMapId)
	{
		this.classifierMapId = classifierMapId;
	}

	public Long getMarkerMapId()
	{
		return markerMapId;
	}

	public void setMarkerMapId(Long markerMapId)
	{
		this.markerMapId = markerMapId;
	}

	
	public String getHideOverrideMapPanel(){
		if (getDataSource().getEnabledMapOverride()){
			return "";
		}
		return "none";
	}
	
	public String getShowOverrideMapUnCheckedNote(){
		if (!getDataSource().getEnabledMapOverride() && !getDataSource().getConfigType().isSwitchOnly()){
			return "";
		}
		return "none";
	}
	
	public String getHideQosScheduleAndAirtimePanel(){
		if (getDataSource().getConfigType().isWirelessOrBonjourContained()){
			return "";
		}
		return "none";
	}
	
	public boolean getEnableUSBLimitDownloadBandwidth() {
		if (getDataSource()!= null) {
			return !getDataSource().isEnableUSBLimitDownloadBandwidth();
		} else {
			return true;
		}
	}
	
	public boolean getEnableUSBLimitUploadBandwidth() {
		if (getDataSource()!= null) {
			return !getDataSource().isEnableUSBLimitUploadBandwidth();
		} else {
			return true;
		}
	}
	
	public boolean getEnableEth0LimitDownloadBandwidth() {
		if (getDataSource()!= null) {
			return !getDataSource().isEnableEth0LimitDownloadBandwidth();
		} else {
			return true;
		}
	}
	
	public boolean getEnableEth0LimitUploadBandwidth() {
		if (getDataSource()!= null) {
			return !getDataSource().isEnableEth0LimitUploadBandwidth();
		} else {
			return true;
		}
	}
	
	public String getShowVoIPSetting() {
		if (getDataSource()!= null) {
			return (getDataSource().getConfigType().isRouterContained() || getDataSource().getConfigType().isBonjourContained()) ? "" : "none";
		} else {
			return "none";
		}
	}
	
	/*
	 * QoS Setting end
	 */
	
	private Long vpnSelectedId;
	
	private Long createVpnId;

	public Long getVpnSelectedId() {
//		if(vpnSelectedId == null){
//			return getVpnSelectList().get(0).getId();
//		}
		return vpnSelectedId;
	}

	public void setVpnSelectedId(Long vpnSelectedId) {
		this.vpnSelectedId = vpnSelectedId;
	}

	public List<CheckItem> getVpnSelectList() {
		if(getDataSource() != null){
			//wireless routing mode
			/*if(getDataSource().isBlnWirelessRouter()){*/
			if(getDataSource().getConfigType().isRouterContained()){
				return getBoCheckItems("profileName", VpnService.class, new FilterParams("ipsecVpnType",VpnService.IPSEC_VPN_LAYER_3));
			}else{
				return getBoCheckItems("profileName", VpnService.class, new FilterParams("ipsecVpnType",VpnService.IPSEC_VPN_LAYER_2));
			}
		}
		return getBoCheckItems("profileName", VpnService.class, null);
	}

	public Long getCreateVpnId() {
		return createVpnId;
	}

	public void setCreateVpnId(Long createVpnId) {
		this.createVpnId = createVpnId;
	}
	
	private Long vlanId;
	private String inputVlanIdValue;
	private String inputVlanNativeIdValue;
	private Long vlanNativeId;

	private Long vlanMappingId;
	private String inputVlanMappingIdValue;

	public String getInputVlanIdValue() {
		return inputVlanIdValue;
	}

	public void setInputVlanIdValue(String inputVlanIdValue) {
		this.inputVlanIdValue = inputVlanIdValue;
	}

	public String getInputVlanNativeIdValue() {
		return inputVlanNativeIdValue;
	}

	public void setInputVlanNativeIdValue(String inputVlanNativeIdValue) {
		this.inputVlanNativeIdValue = inputVlanNativeIdValue;
	}
	
	private String[] arraySsidOnly;
	private String[] arrayNetwork;
	private String[] arrayMacOui;
	private String[] arraySsid;
	private String[] arrayCheckE;
	private String[] arrayCheckP;
	private String[] arrayCheckD;
	private String[] arrayCheckET;
	private String[] arrayCheckPT;
	private String[] arrayCheckDT;
	
	private final List<ConfigTemplateQos> listQosRateLimit = new ArrayList<ConfigTemplateQos>();

	public boolean getRadioModeASize() {
		for (ConfigTemplateQos qosPolicies : listQosRateLimit) {
			if (qosPolicies.getRadioMode() == SsidProfile.RADIOMODE_A) {
				return true;
			}
		}
		return false;
	}

	public boolean getRadioModeBGSize() {
		for (ConfigTemplateQos qosPolicies : listQosRateLimit) {
			if (qosPolicies.getRadioMode() == SsidProfile.RADIOMODE_BG) {
				return true;
			}
		}
		return false;
	}
	
	protected void updateQos() {
		if (dataSource == null) {
			return;
		}
		
		for (ConfigTemplateSsid configTemplateSSID : getDataSource().getSsidInterfaces()
				.values()) {
			boolean blnQosSsidOnly = false;
			boolean blnQosNetwork = false;
			boolean blnQosMacOui = false;
			boolean blnQosSsid = false;
			boolean blnQosCheckE = false;
			boolean blnQosCheckD = false;
			boolean blnQosCheckP = false;
			boolean blnQosCheckET = false;
			boolean blnQosCheckDT = false;
			boolean blnQosCheckPT = false;
			if (arraySsidOnly!=null){
				for(String strSsidOnly:arraySsidOnly){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strSsidOnly)){
						blnQosSsidOnly = true;
						break;
					}
				}
			}
			if (blnQosSsidOnly) {
				configTemplateSSID.setSsidOnlyEnabled(blnQosSsidOnly);
				configTemplateSSID.setNetworkServicesEnabled(blnQosNetwork);
				configTemplateSSID.setMacOuisEnabled(blnQosMacOui);
				configTemplateSSID.setSsidEnabled(blnQosSsid);
				configTemplateSSID.setCheckE(blnQosCheckE);
				configTemplateSSID.setCheckD(blnQosCheckD);
				configTemplateSSID.setCheckP(blnQosCheckP);
				configTemplateSSID.setCheckET(blnQosCheckET);
				configTemplateSSID.setCheckDT(blnQosCheckDT);
				configTemplateSSID.setCheckPT(blnQosCheckPT);
				continue;
			}
			if (arrayNetwork!=null){
				for(String strNetWork:arrayNetwork){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strNetWork)){
						blnQosNetwork = true;
						break;
					}
				}
			}
			if (arrayMacOui!=null){
				for(String strMacOui:arrayMacOui){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strMacOui)){
						blnQosMacOui = true;
						break;
					}
				}
			}
			if (arraySsid!=null){
				for(String strSsid:arraySsid){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strSsid)){
						blnQosSsid = true;
						break;
					}
				}
			}
			if (arrayCheckE!=null){
				for(String strCheckE:arrayCheckE){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckE)){
						blnQosCheckE = true;
						break;
					}
				}
			}
			if (arrayCheckD!=null){
				for(String strCheckD:arrayCheckD){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckD)){
						blnQosCheckD = true;
						break;
					}
				}
			}
			if (arrayCheckP!=null){
				for(String strCheckP:arrayCheckP){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckP)){
						blnQosCheckP = true;
						break;
					}
				}
			}
			if (arrayCheckET!=null){
				for(String strCheckET:arrayCheckET){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckET)){
						blnQosCheckET = true;
						break;
					}
				}
			}
			if (arrayCheckDT!=null){
				for(String strCheckDT:arrayCheckDT){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckDT)){
						blnQosCheckDT = true;
						break;
					}
				}
			}
			if (arrayCheckPT!=null){
				for(String strCheckPT:arrayCheckPT){
					if (configTemplateSSID.getInterfaceName().equalsIgnoreCase(strCheckPT)){
						blnQosCheckPT = true;
						break;
					}
				}
			}
			configTemplateSSID.setSsidOnlyEnabled(blnQosSsidOnly);
			configTemplateSSID.setNetworkServicesEnabled(blnQosNetwork);
			configTemplateSSID.setMacOuisEnabled(blnQosMacOui);
			configTemplateSSID.setSsidEnabled(blnQosSsid);
			configTemplateSSID.setCheckE(blnQosCheckE);
			configTemplateSSID.setCheckD(blnQosCheckD);
			configTemplateSSID.setCheckP(blnQosCheckP);
			configTemplateSSID.setCheckET(blnQosCheckET);
			configTemplateSSID.setCheckDT(blnQosCheckDT);
			configTemplateSSID.setCheckPT(blnQosCheckPT);
		}
	}
	
	public void initQosRateLimit(){
		Set<String> qosKey = new HashSet<String>();
		int totalWeightAmode=0;
		int totalWeightBgmode=0;
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					UserProfile myUserProfile = configTemplateSsid.getSsidProfile().getUserProfileDefault();
					int intRadio = configTemplateSsid.getSsidProfile().getRadioMode();
					if (intRadio!=SsidProfile.RADIOMODE_BOTH){
						String key =  myUserProfile.getId() + "|" + intRadio;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,intRadio);
							qosKey.add(key);
							if (intRadio == SsidProfile.RADIOMODE_A){
								totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
							}
							if (intRadio == SsidProfile.RADIOMODE_BG){
								totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
							}
						}
					} else {
						String key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_A;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,SsidProfile.RADIOMODE_A);
							qosKey.add(key);
							totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
						}
						key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_BG;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,SsidProfile.RADIOMODE_BG);
							qosKey.add(key);
							totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
						}
					}
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					UserProfile myUserProfile = configTemplateSsid.getSsidProfile().getUserProfileSelfReg();
					int intRadio = configTemplateSsid.getSsidProfile().getRadioMode();
					if (intRadio!=SsidProfile.RADIOMODE_BOTH){
						String key =  myUserProfile.getId() + "|" + intRadio;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,intRadio);
							qosKey.add(key);
							if (intRadio == SsidProfile.RADIOMODE_A){
								totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
							}
							if (intRadio == SsidProfile.RADIOMODE_BG){
								totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
							}
						}
					} else {
						String key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_A;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,SsidProfile.RADIOMODE_A);
							qosKey.add(key);
							totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
						}
						key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_BG;
						if (!qosKey.contains(key)) {
							addQosList(myUserProfile,SsidProfile.RADIOMODE_BG);
							qosKey.add(key);
							totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
						}
					}
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileGuest() != null) {
				    UserProfile myUserProfile = configTemplateSsid.getSsidProfile().getUserProfileGuest();
				    int intRadio = configTemplateSsid.getSsidProfile().getRadioMode();
				    if (intRadio!=SsidProfile.RADIOMODE_BOTH){
				        String key =  myUserProfile.getId() + "|" + intRadio;
				        if (!qosKey.contains(key)) {
				            addQosList(myUserProfile,intRadio);
				            qosKey.add(key);
				            if (intRadio == SsidProfile.RADIOMODE_A){
				                totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
				            }
				            if (intRadio == SsidProfile.RADIOMODE_BG){
				                totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
				            }
				        }
				    } else {
				        String key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_A;
				        if (!qosKey.contains(key)) {
				            addQosList(myUserProfile,SsidProfile.RADIOMODE_A);
				            qosKey.add(key);
				            totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
				        }
				        key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_BG;
				        if (!qosKey.contains(key)) {
				            addQosList(myUserProfile,SsidProfile.RADIOMODE_BG);
				            qosKey.add(key);
				            totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
				        }
				    }
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile myUserProfile : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						int intRadio = configTemplateSsid.getSsidProfile().getRadioMode();
						if (intRadio!=SsidProfile.RADIOMODE_BOTH){
							String key =  myUserProfile.getId() + "|" + intRadio;
							if (!qosKey.contains(key)) {
								addQosList(myUserProfile,intRadio);
								qosKey.add(key);
								if (intRadio == SsidProfile.RADIOMODE_A){
									totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
								}
								if (intRadio == SsidProfile.RADIOMODE_BG){
									totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
								}
							}
						} else {
							String key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_A;
							if (!qosKey.contains(key)) {
								addQosList(myUserProfile,SsidProfile.RADIOMODE_A);
								qosKey.add(key);
								totalWeightAmode = totalWeightAmode + myUserProfile.getSchedulingWeight();
							}
							key =  myUserProfile.getId() + "|" + SsidProfile.RADIOMODE_BG;
							if (!qosKey.contains(key)) {
								addQosList(myUserProfile,SsidProfile.RADIOMODE_BG);
								qosKey.add(key);
								totalWeightBgmode = totalWeightBgmode + myUserProfile.getSchedulingWeight();
							}
						}
					}
				}
			}
		}
		for(ConfigTemplateQos tmpQos:listQosRateLimit){
			if (tmpQos.getRadioMode()==SsidProfile.RADIOMODE_A){
				if (totalWeightAmode == 0) {
					tmpQos.setWeightPercent(100.0f);
				} else {
					tmpQos.setWeightPercent(tmpQos.getSchedulingWeight()* 10000/totalWeightAmode/100f);
				}
			}
			if (tmpQos.getRadioMode()==SsidProfile.RADIOMODE_BG){
				if (totalWeightBgmode == 0) {
					tmpQos.setWeightPercent(100.0f);
				} else {
					tmpQos.setWeightPercent(tmpQos.getSchedulingWeight()* 10000/totalWeightBgmode/100f);
				}
			}
		}
	}
		
	public void addQosList(UserProfile up, int intRadio){
		ConfigTemplateQos tmpQos = new ConfigTemplateQos();
		tmpQos.setUserProfile(up);
		tmpQos.setPolicingRate(up.getPolicingRate());
		tmpQos.setPolicingRate11n(up.getPolicingRate11n());
		tmpQos.setPolicingRate11ac(up.getPolicingRate11ac());
		tmpQos.setSchedulingWeight(up.getSchedulingWeight());
		tmpQos.setRadioMode(intRadio);
		listQosRateLimit.add(tmpQos);
	}
	
//	public String saveQoSFunc() throws JSONException{
//		jsonObject = new JSONObject();
//		try {
//			updateQos();
//			if (classifierMapId != null) {
//				QosClassification tmpClass = findBoById(QosClassification.class,
//						classifierMapId);
//				getDataSource().setClassifierMap(tmpClass);
//			}
//			if (markerMapId != null) {
//				QosMarking tmpClass = findBoById(QosMarking.class, markerMapId);
//				getDataSource().setMarkerMap(tmpClass);
//			}
//		} catch (Exception e) {
//			jsonObject.put("t", false);
//			return "json";
//		}
//		jsonObject.put("t", true);
//		return "json";
//	}
	public void prapareVlanAndNetworkSelectObjects() throws Exception {
		list_vlan = getBoCheckItems("vlanName", Vlan.class, null,CHECK_ITEM_BEGIN_BLANK,CHECK_ITEM_END_NO);
		
        list_mgtNetwork = getBoCheckItems("networkName", VpnNetwork.class, new FilterParams(
                "networkType", VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT), CHECK_ITEM_BEGIN_NO,
                CHECK_ITEM_END_NO);
		
//		List<VpnNetwork> lstNetwork = QueryUtil.executeQuery(VpnNetwork.class, new SortParams("id"),
//				new FilterParams("networkType", VpnNetwork.VPN_NETWORK_TYPE_INTERNAL), getDomainId(), this);
//
//		list_mgtNetwork = new ArrayList<CheckItem>();
//		for(VpnNetwork obj: lstNetwork){
//			if (obj.getVpnDnsService()==null) {
//				continue;
//			}
//			if (obj.getSubItems() == null || obj.getSubItems().isEmpty()){
//				continue;
//			}
//			boolean dhcpEnable = true;
//			for (VpnNetworkSub vpnSub: obj.getSubItems()) {
//				if (!vpnSub.isEnableDhcp()) {
//					dhcpEnable = false;
//					break;
//				}
//				if (vpnSub.getIpBranches()==1) {
//					dhcpEnable = false;
//					break;
//				}
//			}
//			if (!dhcpEnable) {
//				continue;
//			}
//			
//			list_mgtNetwork.add(new CheckItem(obj.getId(), obj.getNetworkName()));
//		}
//		if (list_mgtNetwork.isEmpty()) {
//			list_mgtNetwork.add(new CheckItem((long) CHECK_ITEM_ID_BLANK, MgrUtil
//					.getUserMessage("config.optionsTransfer.none")));
//		}
	}
	
	public void prepareInitVlanAndNetworkSelectObjects(){
		if (getDataSource().getVlan() != null) {
			vlanId = getDataSource().getVlan().getId();
			inputVlanIdValue = getDataSource().getVlan().getVlanName();
		} else {
			Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
			vlanId = vlanClass.getId();
			inputVlanIdValue=vlanClass.getVlanName();
		}
		if (getDataSource().getVlanNative() != null) {
			vlanNativeId = getDataSource().getVlanNative().getId();
			inputVlanNativeIdValue = getDataSource().getVlanNative().getVlanName();
		} else {
			Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
			vlanNativeId = vlanClass.getId();
			inputVlanNativeIdValue=vlanClass.getVlanName();
		}
		
//		if (getDataSource().getMgtNetwork()!=null) {
//			mgtNetworkId=getDataSource().getMgtNetwork().getId();
//		}
	}
	
	public void prepareMgtDependentSelectObjects() throws Exception {
		list_mgtDns = getBoCheckItems("mgmtName", MgmtServiceDns.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtTime = getBoCheckItems("mgmtName", MgmtServiceTime.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		// option page left side
		list_locationServer = getBoCheckItems("name", LocationServer.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtSyslog = getBoCheckItems("mgmtName", MgmtServiceSyslog.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtSnmp = getBoCheckItems("mgmtName", MgmtServiceSnmp.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		if (isHMOnline()){
			list_clientWatch = getBoCheckItems("name", LocationClientWatch.class, 
					new FilterParams("defaultFlag",false),
					CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		} else {
			list_clientWatch = getBoCheckItems("name", LocationClientWatch.class, null,
					CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		}
		list_lldpCdp = getBoCheckItems("profileName", LLDPCDPProfile.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_radiusServerOnAp = getBoCheckItems("radiusName", RadiusOnHiveap.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_radiusProxy = getBoCheckItems("proxyName", RadiusProxy.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		list_routingPolicy = getBoCheckItemsSort("profileName", RoutingProfilePolicy.class, null, 
				new SortParams("profileName"),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		// option page right side
		list_algConfig = getBoCheckItems("configName", AlgConfiguration.class, null);
		list_mgtOption = getBoCheckItems("mgmtName", MgmtServiceOption.class, null);
		list_idsPolicy = getBoCheckItems("policyName", IdsPolicy.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_accessConsole = getBoCheckItems("consoleName", AccessConsole.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_ipFilter = getBoCheckItems("filterName", IpFilter.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
		// access backhaul setting
		list_service = getBoCheckItems("filterName", ServiceFilter.class, null);
		list_ethernetAccess = getBoCheckItems("ethernetName", EthernetAccess.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_classifierMap = getBoCheckItems("classificationName", QosClassification.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_markerMap = getBoCheckItems("qosName", QosMarking.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
//		list_vpnService = getBoCheckItems("profileName", VpnService.class, null,
//				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		list_ppskServer = new ArrayList<CheckItem>();
		
		list_ipTrack = getBoCheckItems("trackName", MgmtServiceIPTrack.class, null, CHECK_ITEM_BEGIN_NO,
				CHECK_ITEM_END_NO);

		this.list_radiusServers = getBoCheckItems("radiusName",
				RadiusOnHiveap.class, null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);

		list_radiusOptNames = getBoCheckItems("objectName", RadiusAttrs.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		list_cliBlob = getBoCheckItems("supplementalName", CLIBlob.class, null,
				CHECK_ITEM_BEGIN_BLANK,CHECK_ITEM_END_NO);
		
//		prepareVpnUserProfile();
		
		appProfileList = QueryUtil.executeQuery(ApplicationProfile.class, null, null);
		
		prepareIpTracks();
		
		prepareTVNetworkServices();
		
		prepareStormControl();

	}
	
	public void prepareSaveVlanSelectObjects() throws Exception {
		if (vlanId != null) {
			if (vlanId==-1){
				Vlan myVlan = CreateObjectAuto.createNewVlan(inputVlanIdValue,getDomain(),"");
				if (myVlan!=null){
					getDataSource().setVlan(myVlan);
				}
			} else {
				Vlan tmpClass = findBoById(Vlan.class, vlanId);
				getDataSource().setVlan(tmpClass);
			}
		}
		if (vlanNativeId != null) {
			if (vlanNativeId==-1){
				Vlan myVlan = CreateObjectAuto.createNewVlan(inputVlanNativeIdValue,getDomain(),"");
				if (myVlan!=null){
					getDataSource().setVlanNative(myVlan);
				}
			} else {
				Vlan tmpClass = findBoById(Vlan.class, vlanNativeId);
				getDataSource().setVlanNative(tmpClass);
			}
		}
	}
	
	public void prepareInitMgtSelectObjects() {
		// general page
		if (getDataSource().getMgmtServiceDns() != null) {
			mgtDnsId = getDataSource().getMgmtServiceDns().getId();
		}
		if (getDataSource().getMgmtServiceTime() != null) {
			mgtTimeId = getDataSource().getMgmtServiceTime().getId();
		}

		// optional left fieldset
		if (getDataSource().getLocationServer() != null) {
			locationServerId = getDataSource().getLocationServer().getId();
		}
		if (getDataSource().getLldpCdp() != null) {
			lldpCdpId = getDataSource().getLldpCdp().getId();
		}
		
		if (getDataSource().getRadiusProxyProfile() != null) {
			radiusProxyId = getDataSource().getRadiusProxyProfile().getId();
		}
		if (getDataSource().getRadiusServerProfile() != null) {
			radiusServerId = getDataSource().getRadiusServerProfile().getId();
		}
		
		if (getDataSource().getRoutingProfilePolicy() != null) {
			routingPolicyId = getDataSource().getRoutingProfilePolicy().getId();
		}
		
		if (getDataSource().getMgmtServiceSnmp() != null) {
			mgtSnmpId = getDataSource().getMgmtServiceSnmp().getId();
		}
		if (getDataSource().getMgmtServiceSyslog() != null) {
			mgtSyslogId = getDataSource().getMgmtServiceSyslog().getId();
		}
		if (getDataSource().getClientWatch() != null) {
			clientWatchId = getDataSource().getClientWatch().getId();
		}
		
//		if (getDataSource().getRouterIpTrack() != null) {
//			routerIpTrackId = getDataSource().getRouterIpTrack().getId();
//		}

		// optional right fieldset
		if (getDataSource().getAlgConfiguration() != null) {
			algConfigId = getDataSource().getAlgConfiguration().getId();
		}
		if (getDataSource().getMgmtServiceOption() != null) {
			mgtOptionId = getDataSource().getMgmtServiceOption().getId();
		}
		if (getDataSource().getIdsPolicy() != null) {
			idsPolicyId = getDataSource().getIdsPolicy().getId();
		}
		if (getDataSource().getAccessConsole() != null) {
			accessConsoleId = getDataSource().getAccessConsole().getId();
		}
		if (getDataSource().getIpFilter() != null) {
			ipFilterId = getDataSource().getIpFilter().getId();
		}
		
		// QOS
		// private Long qosClassId;
		if (getDataSource().getClassifierMap() != null) {
			classifierMapId = getDataSource().getClassifierMap().getId();
		}
		if (getDataSource().getMarkerMap() != null) {
			markerMapId = getDataSource().getMarkerMap().getId();
		}

		// service access
		if(getDataSource().getDeviceServiceFilter() != null){
			deviceServiceId = getDataSource().getDeviceServiceFilter().getId();
		}
		if (getDataSource().getEth0ServiceFilter() != null) {
			eth0ServiceId = getDataSource().getEth0ServiceFilter().getId();
		}
		if (getDataSource().getEth1ServiceFilter() != null) {
			eth1ServiceId = getDataSource().getEth1ServiceFilter().getId();
		}
		if (getDataSource().getRed0ServiceFilter() != null) {
			redServiceId = getDataSource().getRed0ServiceFilter().getId();
		}
		if (getDataSource().getAgg0ServiceFilter() != null) {
			aggServiceId = getDataSource().getAgg0ServiceFilter().getId();
		}
		
		// service backhaul
		if (getDataSource().getEth0BackServiceFilter() != null) {
			eth0BackServiceId = getDataSource().getEth0BackServiceFilter().getId();
		}
		if (getDataSource().getEth1BackServiceFilter() != null) {
			eth1BackServiceId = getDataSource().getEth1BackServiceFilter().getId();
		}
		if (getDataSource().getRed0BackServiceFilter() != null) {
			red0BackServiceId = getDataSource().getRed0BackServiceFilter().getId();
		}
		if (getDataSource().getAgg0BackServiceFilter() != null) {
			agg0BackServiceId = getDataSource().getAgg0BackServiceFilter().getId();
		}
		// service backhaul wireless
		if (getDataSource().getWireServiceFilter() != null) {
			wireServiceId = getDataSource().getWireServiceFilter().getId();
		}

		// VPN Service settings
//		if (getDataSource().getVpnService()!=null) {
//			vpnServiceId = getDataSource().getVpnService().getId();
//		}
		// RADIUS Operator-Name attribute
		if (getDataSource().getRadiusAttrs() != null) {
			radiusOptNameId = getDataSource().getRadiusAttrs().getId();
		}
		if (getDataSource().getPrimaryIpTrack() != null) {
			primaryIpTrackId = getDataSource().getPrimaryIpTrack().getId();
		}
		if (getDataSource().getBackup1IpTrack() != null) {
			backup1TrackId = getDataSource().getBackup1IpTrack().getId();
		}
		if (getDataSource().getBackup2IpTrack() != null) {
			backup2TrackId = getDataSource().getBackup2IpTrack().getId();
		}
		if(getDataSource().getThirdPort() != null){
			thirdPortId = getDataSource().getThirdPort();
		}
		if(null != getDataSource().getSupplementalCLI()){
			supplementalCLIId = getDataSource().getSupplementalCLI().getId();
		}
		initQosRateLimit();
	}
	
	public void prepareSaveMgtSelectObjects() throws Exception {
		if (mgtDnsId != null) {
			MgmtServiceDns tmpClass = findBoById(MgmtServiceDns.class, mgtDnsId);
			getDataSource().setMgmtServiceDns(tmpClass);
		}
		if (mgtTimeId != null) {
			MgmtServiceTime tmpClass = findBoById(MgmtServiceTime.class,
					mgtTimeId);
			getDataSource().setMgmtServiceTime(tmpClass);
		}

		// optional page left side
		if (mgtSnmpId != null) {
			MgmtServiceSnmp tmpClass = findBoById(MgmtServiceSnmp.class,
					mgtSnmpId);
			getDataSource().setMgmtServiceSnmp(tmpClass);
		}
		if (mgtSyslogId != null) {
			MgmtServiceSyslog tmpClass = findBoById(MgmtServiceSyslog.class,
					mgtSyslogId);
			getDataSource().setMgmtServiceSyslog(tmpClass);
		}
		if (locationServerId != null) {
			LocationServer tmpClass = findBoById(LocationServer.class,
					locationServerId);
			getDataSource().setLocationServer(tmpClass);
		}
		if (lldpCdpId != null) {
			LLDPCDPProfile tmpClass = findBoById(LLDPCDPProfile.class,
					lldpCdpId);
			getDataSource().setLldpCdp(tmpClass);
		}
		
		if (radiusProxyId != null) {
			RadiusProxy tmpClass = findBoById(RadiusProxy.class,
					radiusProxyId);
			getDataSource().setRadiusProxyProfile(tmpClass);
		}
		
		if (radiusServerId != null) {
			RadiusOnHiveap tmpClass = findBoById(RadiusOnHiveap.class,
					radiusServerId);
			getDataSource().setRadiusServerProfile(tmpClass);
		}
		
		if (routingPolicyId != null) {
			RoutingProfilePolicy tmpClass = findBoById(RoutingProfilePolicy.class,
					routingPolicyId);
			getDataSource().setRoutingProfilePolicy(tmpClass);
		}
		
		if (clientWatchId != null) {
			LocationClientWatch tmpClass = findBoById(LocationClientWatch.class,
					clientWatchId);
			getDataSource().setClientWatch(tmpClass);
		}
		
//		if (routerIpTrackId != null) {
//			MgmtServiceIPTrack tmpClass = findBoById(MgmtServiceIPTrack.class,
//					routerIpTrackId);
//			getDataSource().setRouterIpTrack(tmpClass);
//		}
		
		// optional page right side
		if (algConfigId != null) {
			AlgConfiguration tmpClass = findBoById(AlgConfiguration.class,
					algConfigId);
			getDataSource().setAlgConfiguration(tmpClass);
		}
		if (mgtOptionId != null) {
			MgmtServiceOption tmpClass = findBoById(MgmtServiceOption.class,
					mgtOptionId);
			getDataSource().setMgmtServiceOption(tmpClass);
		}
		if (idsPolicyId != null) {
			IdsPolicy tmpClass = findBoById(IdsPolicy.class, idsPolicyId);
			getDataSource().setIdsPolicy(tmpClass);
		}
		if (accessConsoleId != null) {
			AccessConsole tmpClass = findBoById(AccessConsole.class,
					accessConsoleId);
			getDataSource().setAccessConsole(tmpClass);
		}
		if (ipFilterId != null) {
			IpFilter tmpClass = findBoById(IpFilter.class, ipFilterId);
			getDataSource().setIpFilter(tmpClass);
		}
		
		// Qos
		if (classifierMapId != null) {
			QosClassification tmpClass = findBoById(QosClassification.class,
					classifierMapId);
			getDataSource().setClassifierMap(tmpClass);
		}
		if (markerMapId != null) {
			QosMarking tmpClass = findBoById(QosMarking.class, markerMapId);
			getDataSource().setMarkerMap(tmpClass);
		}

		// optional page access service
		if(deviceServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, deviceServiceId);
			getDataSource().setDeviceServiceFilter(tmpClass);
		}
		if (eth0ServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, eth0ServiceId);
			getDataSource().setEth0ServiceFilter(tmpClass);
		}
		if (eth1ServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, eth1ServiceId);
			getDataSource().setEth1ServiceFilter(tmpClass);
		}
		if (redServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, redServiceId);
			getDataSource().setRed0ServiceFilter(tmpClass);
		}
		if (aggServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, aggServiceId);
			getDataSource().setAgg0ServiceFilter(tmpClass);
		}

		// optional page backhaul service
		if (eth0BackServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class,
					eth0BackServiceId);
			getDataSource().setEth0BackServiceFilter(tmpClass);
		}
		if (eth1BackServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class,
					eth1BackServiceId);
			getDataSource().setEth1BackServiceFilter(tmpClass);
		}
		if (red0BackServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class,
					red0BackServiceId);
			getDataSource().setRed0BackServiceFilter(tmpClass);
		}
		if (agg0BackServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class,
					agg0BackServiceId);
			getDataSource().setAgg0BackServiceFilter(tmpClass);
		}

		// backhaul wireless service
		if (wireServiceId != null) {
			ServiceFilter tmpClass = findBoById(ServiceFilter.class, wireServiceId);
			getDataSource().setWireServiceFilter(tmpClass);
		}

		// VPN Service settings
//		if (vpnServiceId != null) {
//			VpnService tmpClass = findBoById(VpnService.class,vpnServiceId);
//			getDataSource().setVpnService(tmpClass);
//		}
		
		// RADIUS Operator-Name attribute
		if (radiusOptNameId != null) {
			RadiusAttrs tmpClass = findBoById(RadiusAttrs.class, radiusOptNameId);
			getDataSource().setRadiusAttrs(tmpClass);
		}
		
		if (appProfileId != null) {
			ApplicationProfile profile = findBoById(ApplicationProfile.class, appProfileId);
			getDataSource().setAppProfile(profile);
		}
		
		if (primaryIpTrackId != null) {
			MgmtServiceIPTrack tmpClass = findBoById(MgmtServiceIPTrack.class,
					primaryIpTrackId);
			getDataSource().setPrimaryIpTrack(tmpClass);
		}
		if (backup1TrackId != null) {
			MgmtServiceIPTrack tmpClass = findBoById(MgmtServiceIPTrack.class,
					backup1TrackId);
			getDataSource().setBackup1IpTrack(tmpClass);
		}
		if (backup2TrackId != null) {
			MgmtServiceIPTrack tmpClass = findBoById(MgmtServiceIPTrack.class,
					backup2TrackId);
			getDataSource().setBackup2IpTrack(tmpClass);
		}
		if(thirdPortId != null){
			getDataSource().setThirdPort(thirdPortId);
		}
		
		if(null != supplementalCLIId){
			CLIBlob cliBlob = findBoById(CLIBlob.class, supplementalCLIId);
			getDataSource().setSupplementalCLI(cliBlob);
		}
	}
	
	public void prepareIpTracks() {
		List<CheckItem> remendialList = new ArrayList<CheckItem>();
		List<CheckItem> wanlList = new ArrayList<CheckItem>();
		
		remendialList = getBoCheckItems("trackName", MgmtServiceIPTrack.class, new FilterParams("groupType", 0));
		wanlList = getBoCheckItems("trackName", MgmtServiceIPTrack.class, new FilterParams("groupType", 1));
		//List<CheckItem> availableFilters = getBoCheckItems("trackName", MgmtServiceIPTrack.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : remendialList) {
			for (MgmtServiceIPTrack ServiceIPTrack : getDataSource().getIpTracks()) {
				if (ServiceIPTrack.getTrackName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		remendialList.removeAll(removeList);
		
		removeList.clear();
		for (CheckItem oneItem : wanlList) {
			for (MgmtServiceIPTrack ServiceIPTrack : getDataSource().getIpTracks()) {
				if (ServiceIPTrack.getTrackName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		wanlList.removeAll(removeList);

		List<MgmtServiceIPTrack> wanTrackList = new ArrayList<MgmtServiceIPTrack>();
		List<MgmtServiceIPTrack> redemtionActionList = new ArrayList<MgmtServiceIPTrack>();
		for(MgmtServiceIPTrack item:getDataSource().getIpTracks()){
			if(item.getGroupType()==0) redemtionActionList.add(item);
			else
				wanTrackList.add(item);
		}
		ipTrackOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("hiveAp.ipTrack.availableIpTrack"), MgrUtil
				.getUserMessage("hiveAp.ipTrack.selectedIpTrack"), remendialList,
				redemtionActionList, "id", "value", "ipTrackIds",  0,
				"250px", "6", true, "IpTrack");
		
		ipTrackWANList = wanlList;
		ipTrackWANListWithBlank = new ArrayList<CheckItem>();
		ipTrackWANListWithBlank.add(new CheckItem(Long.valueOf(0),""));
		ipTrackWANListWithBlank.addAll(wanlList);
	}
	
	protected void setSelectedIpTracks() throws Exception {
		Set<MgmtServiceIPTrack> ssidIpTracks = getDataSource().getIpTracks();
		ssidIpTracks.clear();

		if (ipTrackIds != null) {
			for (Long filterId : ipTrackIds) {
				MgmtServiceIPTrack ipTrack = findBoById(
						MgmtServiceIPTrack.class, filterId);
				if (ipTrack != null) {
					ssidIpTracks.add(ipTrack);
				}
			}
		}
		getDataSource().setIpTracks(ssidIpTracks);
	}
	
	public void prepareTVNetworkServices() {
		List<CheckItem> availableFilters = getBoCheckItems("serviceName", 
				NetworkService.class, 
				new FilterParams("algType=:s1 and serviceName!=:s2"
						,new Object[]{NetworkService.ALG_TYPE_HTTP,"TeacherView-HTTP"}));
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (NetworkService services : getDataSource().getTvNetworkService()) {
				if (services.getServiceName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);

		tvNetworkOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.configTemplate.tvsetting.availableNetwork"), MgrUtil
				.getUserMessage("config.configTemplate.tvsetting.selectedNetwork"), availableFilters,
				getDataSource().getTvNetworkService(), "id", "value", "tvNetworkIds",  0,
				"250px", "6", true, "TvNetwork");
	}

	protected void setSelectedTVNetworkServices() throws Exception {
		Set<NetworkService> tvNewworks = getDataSource().getTvNetworkService();
		tvNewworks.clear();
		if (getDataSource().isEnableTVService()){
			if (tvNetworkIds != null) {
				for (Long filterId : tvNetworkIds) {
					NetworkService tvNetwork = findBoById(
							NetworkService.class, filterId);
					if (tvNetwork != null) {
						tvNewworks.add(tvNetwork);
					}
				}
			}
		}
		getDataSource().setTvNetworkService(tvNewworks);
	}
	
	OptionsTransfer ipTrackOptions;
	private List<Long> ipTrackIds;
	private List<CheckItem> ipTrackWANList;
	private List<CheckItem> ipTrackWANListWithBlank;
	
	OptionsTransfer ipTrackWANOptions;
	private List<Long> ipTrackWANOptionsIds;
	
	OptionsTransfer tvNetworkOptions;
	private List<Long> tvNetworkIds;
	
	// list
	private List<CheckItem> list_vlan;
	private List<CheckItem> list_mgtDns;
	private List<CheckItem> list_mgtTime;
	private List<CheckItem> list_mgtSyslog;
	private List<CheckItem> list_locationServer;
	private List<CheckItem> list_lldpCdp;
	private List<CheckItem> list_radiusServerOnAp;
	private List<CheckItem> list_radiusProxy;
	
	private List<CheckItem> list_routingPolicy;

	private List<CheckItem> list_mgtSnmp;
	private List<CheckItem> list_algConfig;
	private List<CheckItem> list_mgtOption;
	private List<CheckItem> list_clientWatch;
	private List<CheckItem> list_idsPolicy;
	private List<CheckItem> list_accessConsole;
	private List<CheckItem> list_ipFilter;
	private List<CheckItem> list_radius;
	private List<CheckItem> list_service;
	private List<CheckItem> list_ethernetAccess;
	private List<CheckItem> list_classifierMap;
	private List<CheckItem> list_markerMap;
//	private List<CheckItem> list_vpnService;
//	private List<UserProfile> list_vpnUserProfile;
	private List<CheckItem> list_ppskServer;
	
	private List<CheckItem> list_mstpRegion;
	private List<CheckItem> list_ipTrack;
	
	private List<CheckItem>	list_mgtNetwork;
	private List<CheckItem> list_radiusServers;
	private List<CheckItem> list_radiusOptNames;
	private List<CheckItem> list_cliBlob;
	
	private Long mgtDnsId;
	private Long mgtTimeId;

	// optional page left fieldset
	private Long locationServerId;
	private Long lldpCdpId;
	private Long radiusProxyId;
	private Long radiusServerId;
	private Long mgtSnmpId;
	private Long mgtSyslogId;
	private Long clientWatchId;
	
	private Long routingPolicyId;

	private Long routerIpTrackId;
	private Long ipTrackId;
//	private Long mgtNetworkId;
	private Long tvNetworkId;
	
	private Long mstpRegionId;

	// optional page right fieldset
	private Long algConfigId;
	private Long mgtOptionId;
	private Long idsPolicyId;
	private Long accessConsoleId;
	private Long ipFilterId;

	// service access
	private Long deviceServiceId;
	private Long eth0ServiceId;
	private Long eth1ServiceId;
	private Long redServiceId;
	private Long aggServiceId;
	// Ethernet Access
	private Long ethernetAccessId;
	private Long ethernetAccessEth1Id;
	private Long ethernetAccessRedId;
	private Long ethernetAccessAggId;
	// Ethernet Bridge
	private Long ethernetBridgeId;
	private Long ethernetBridgeEth1Id;
	private Long ethernetBridgeRedId;
	private Long ethernetBridgeAggId;
	// service backhaul
	private Long eth0BackServiceId;
	private Long eth1BackServiceId;
	private Long red0BackServiceId;
	private Long agg0BackServiceId;
	// service backhaul wireless
	private Long wireServiceId;
//	private Long vpnServiceId;
	private Long radiusOptNameId;
	private Long appProfileId;
	private Long supplementalCLIId;
	
	private List<ApplicationProfile> appProfileList;
	
	private Long primaryIpTrackId;
	private Long backup1TrackId;
	private Long backup2TrackId;
	private Long thirdPortId  = Long.valueOf(5);

	public Long getAppProfileId() {
		return appProfileId;
	}

	public void setAppProfileId(Long appProfileId) {
		this.appProfileId = appProfileId;
	}

	public List<ApplicationProfile> getAppProfileList() {
		return appProfileList;
	}

	public void setAppProfileList(List<ApplicationProfile> appProfileList) {
		this.appProfileList = appProfileList;
	}

	public Long getRadiusOptNameId() {
		return radiusOptNameId;
	}

	public void setRadiusOptNameId(Long radiusOptNameId) {
		this.radiusOptNameId = radiusOptNameId;
	}

	//========== Storm Control =================
	private void prepareStormControl(){
		if (dataSource == null) {
			return;
		}
		if(getDataSource().getStormControlList().isEmpty()){
			getDataSource().setStormControlList(BoGenerationUtil.getDefaultStormControl(getDomain(), getDataSource()));
		}
		
		short stormControlMode = getDataSource().getSwitchStormControlMode();
		for(ConfigTemplateStormControl stormControl : getDataSource().getStormControlList()){
			if(stormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
				if(stormControl.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
					stormControl.setRateLimitRange(" " + MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"));
					stormControl.setRateLimitValueLength(7);
					if(!isPortStromEnable(stormControl)){
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
						stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
						
					}
				} else {
					stormControl.setRateLimitRange(" " + MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"));
					stormControl.setRateLimitValueLength(3);
					if(!isPortStromEnable(stormControl)){
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID);
						stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE);
					}
				}
			} else {
				stormControl.setRateLimitRange(" " + MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"));
				stormControl.setRateLimitValueLength(9);
				if(!isPortStromEnable(stormControl)){
					stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
					stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
				}
			}
		}
	}
	
	private void updateStormContorl(){
		if (dataSource == null) {
			return;
		}
		short stormControlMode = getDataSource().getSwitchStormControlMode();
		Set<String> interfaceNumSet = getEanbleStormIfName();
		for(ConfigTemplateStormControl stormControl : getDataSource().getStormControlList()){
			boolean blnAllTrafficType = false;
			boolean blnBroadcast = false;
			boolean blnUnknownUnicast = false;
			boolean blnMulticast = false;
			boolean blnTcpsyn = false;
			
			if(!interfaceNumSet.contains(stormControl.getInterfaceType())){
				if(stormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
					if(stormControl.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
						stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
					} else {
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID);
						stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE);
					}
					
				} else {
					stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
					stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
				}
			}

			if (arrayInterfaceType!=null){
				for(int i =0;i<arrayInterfaceType.length;i++){
					if (stormControl.getInterfaceType().equalsIgnoreCase(arrayInterfaceType[i])){
						if(stormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
							if(arrayRateLimitType != null && i < arrayRateLimitType.length){
								stormControl.setRateLimitType(arrayRateLimitType[i]);
							}
						} else {
							stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
						}
						
						if(arrayRateLimitValue != null && i < arrayRateLimitValue.length){
							stormControl.setRateLimitValue(arrayRateLimitValue[i]);
						}
						
						break;
					}
				}
			}

			if (arrayAllTrafficType!=null){
				for(String strAllTrafficType:arrayAllTrafficType){
					if (stormControl.getInterfaceType().equalsIgnoreCase(strAllTrafficType)){
						blnAllTrafficType = true;
						break;
					}
				}
			}
			if (blnAllTrafficType) {
				stormControl.setAllTrafficType(blnAllTrafficType);
				stormControl.setBroadcast(blnAllTrafficType);
				stormControl.setUnknownUnicast(blnAllTrafficType);
				stormControl.setMulticast(blnAllTrafficType);
				stormControl.setTcpsyn(blnAllTrafficType);
				continue;
			}
			if (arrayBroadcast!=null){
				for(String strBroadcast:arrayBroadcast){
					if (stormControl.getInterfaceType().equalsIgnoreCase(strBroadcast)){
						blnBroadcast = true;
						break;
					}
				}
			}
			if (arrayUnknownUnicast!=null){
				for(String strUnknownUnicast:arrayUnknownUnicast){
					if (stormControl.getInterfaceType().equalsIgnoreCase(strUnknownUnicast)){
						blnUnknownUnicast = true;
						break;
					}
				}
			}
			if (arrayMulticast!=null){
				for(String strMulticast:arrayMulticast){
					if (stormControl.getInterfaceType().equalsIgnoreCase(strMulticast)){
						blnMulticast = true;
						break;
					}
				}
			}
			
			if (arrayTcpsyn!=null){
				for(String strTcpsyn:arrayTcpsyn){
					if (stormControl.getInterfaceType().equalsIgnoreCase(strTcpsyn)){
						blnTcpsyn = true;
						break;
					}
				}
			}
			
			stormControl.setAllTrafficType(blnAllTrafficType);
			stormControl.setBroadcast(blnBroadcast);
			stormControl.setUnknownUnicast(blnUnknownUnicast);
			stormControl.setMulticast(blnMulticast);
			stormControl.setTcpsyn(blnTcpsyn);
		}
	}
	
	private boolean isPortStromEnable(ConfigTemplateStormControl stormControl){
		if(stormControl == null){
			return false;
		}
		
		if(stormControl.isBroadcast() 
				|| stormControl.isMulticast()
				|| stormControl.isTcpsyn()
				|| stormControl.isUnknownUnicast()){
			return true;
		}
		
		return false;
	}
	
	private Set<String> getEanbleStormIfName(){
		Set<String> result = new HashSet<>();
		if(arrayBroadcast != null){
			result.addAll(Arrays.asList(arrayBroadcast));
		}
		if(arrayUnknownUnicast != null){
			result.addAll(Arrays.asList(arrayUnknownUnicast));
		}
		if(arrayMulticast != null){
			result.addAll(Arrays.asList(arrayMulticast));
		}
		if(arrayTcpsyn != null){
			result.addAll(Arrays.asList(arrayTcpsyn));
		}
		return result;
	}
	
	public List<CheckItem> getList_stormLimitType() {
		List<CheckItem> items = new ArrayList<CheckItem>();
		CheckItem item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID,
				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS);
		items.add(item);
//		item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID,
//				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS);
//		items.add(item);
		item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID,
				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE);
		items.add(item);
		return items;
	}
	
    public EnumItem[] getStormRateLimitByte() {
			return new EnumItem[] { new EnumItem(0,
					getText("config.configTemplate.switchSettings.stormControl.byteBased")) };
	}
	
    public EnumItem[] getStormRateLimitPacket() {
			return new EnumItem[] { new EnumItem(1,
					getText("config.configTemplate.switchSettings.stormControl.packetBased")) };
	}
	private String[] arrayAllTrafficType;
	private String[] arrayBroadcast;
	private String[] arrayUnknownUnicast;
	private String[] arrayMulticast;
	private String[] arrayTcpsyn;
	private Long[] arrayRateLimitValue;
	private Long[] arrayRateLimitType;
	private String[] arrayInterfaceType;

	public Long[] getArrayRateLimitType() {
		return arrayRateLimitType;
	}

	public void setArrayRateLimitType(Long[] arrayRateLimitType) {
		this.arrayRateLimitType = arrayRateLimitType;
	}

	public String[] getArrayInterfaceType() {
		return arrayInterfaceType;
	}

	public void setArrayInterfaceType(String[] arrayInterfaceType) {
		this.arrayInterfaceType = arrayInterfaceType;
	}

	public String[] getArrayMulticast() {
		return arrayMulticast;
	}

	public void setArrayMulticast(String[] arrayMulticast) {
		this.arrayMulticast = arrayMulticast;
	}
	
	public String[] getArrayAllTrafficType() {
		return arrayAllTrafficType;
	}

	public String[] getArrayBroadcast() {
		return arrayBroadcast;
	}

	public String[] getArrayUnknownUnicast() {
		return arrayUnknownUnicast;
	}


	public String[] getArrayTcpsyn() {
		return arrayTcpsyn;
	}

	public Long[] getArrayRateLimitValue() {
		return arrayRateLimitValue;
	}

	public void setArrayAllTrafficType(String[] arrayAllTrafficType) {
		this.arrayAllTrafficType = arrayAllTrafficType;
	}

	public void setArrayBroadcast(String[] arrayBroadcast) {
		this.arrayBroadcast = arrayBroadcast;
	}

	public void setArrayUnknownUnicast(String[] arrayUnknownUnicast) {
		this.arrayUnknownUnicast = arrayUnknownUnicast;
	}

	public void setArrayTcpsyn(String[] arrayTcpsyn) {
		this.arrayTcpsyn = arrayTcpsyn;
	}

	public void setArrayRateLimitValue(Long[] arrayRateLimitValue) {
		this.arrayRateLimitValue = arrayRateLimitValue;
	}

	public List<CheckItem> getList_radiusOptNames() {
		return list_radiusOptNames;
	}

	public List<CheckItem> getList_mgtDns() {
		return list_mgtDns;
	}

	public List<CheckItem> getList_mgtTime() {
		return list_mgtTime;
	}

	public List<CheckItem> getList_mgtSyslog() {
		return list_mgtSyslog;
	}

	public List<CheckItem> getList_locationServer() {
		return list_locationServer;
	}

	public List<CheckItem> getList_lldpCdp() {
		return list_lldpCdp;
	}

	public List<CheckItem> getList_mgtSnmp() {
		return list_mgtSnmp;
	}

	public List<CheckItem> getList_algConfig() {
		return list_algConfig;
	}

	public List<CheckItem> getList_mgtOption() {
		return list_mgtOption;
	}

	public List<CheckItem> getList_clientWatch() {
		return list_clientWatch;
	}

	public List<CheckItem> getList_idsPolicy() {
		return list_idsPolicy;
	}

	public List<CheckItem> getList_accessConsole() {
		return list_accessConsole;
	}

	public List<CheckItem> getList_ipFilter() {
		return list_ipFilter;
	}

	public List<CheckItem> getList_radius() {
		return list_radius;
	}

	public List<CheckItem> getList_service() {
		return list_service;
	}

	public List<CheckItem> getList_ethernetAccess() {
		return list_ethernetAccess;
	}

//	public List<CheckItem> getList_vpnService() {
//		return list_vpnService;
//	}
//
//	public List<UserProfile> getList_vpnUserProfile() {
//		return list_vpnUserProfile;
//	}

	public List<CheckItem> getList_ppskServer() {
		return list_ppskServer;
	}

	public String getShowVlanDiv() {
		return "";
	}

	public String getHideVlanDiv() {
		return "none";
	}
	
	public String getShowRadiusSettingsDiv() {
		return "";
	}

	public String getHideRadiusSettingsDiv() {
		return "none";
	}
	
	public String getShowNetworkSettingsDiv() {
//		if (getDataSource().getConfigType().isWirelessOrBonjourContained()) {
//			return "";
//		}
		return "";
	}

	public String getHideNetworkSettingsDiv() {
		return "none";
	}
	public String getShowStormControlDiv() {
		return "";
	}

	public String getHideStormControlDiv() {
		return "none";
	}
	
	public String getShowLLDPCDPSettingDiv() {
		return "";
	}

	public String getHideLLDPCDPSettingDiv() {
		return "none";
	}

	public String getHideIGMPSettingDiv() {
		return "none";
	}
	
	public String getShowIGMPSettingDiv() {
		return "";
	}

	public String getShowSTPSettingsDiv() {
		return "";
	}

	public String getHideSTPSettingsDiv() {
		return "none";
	}
	
	public boolean checkSwitchSettingsEnabled(ConfigTemplate config) {
	    if(null == config) {
	        return false;
	    }
        if(config.getConfigType().isSwitchContained()) {
            return true;
        } else {
            Set<PortGroupProfile> portGroups = config.getPortProfiles();
            if(null != portGroups && !portGroups.isEmpty()) {
                for (PortGroupProfile profile : portGroups) {
                    if(profile.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
                            && (profile.getPortNum() == 24 || profile.getPortNum() == 48)) {
                        return true;
                    }
                }
            }
        }
        return false;
    
	}
	
	public boolean isNeedSwitchSettings() {
	    return checkSwitchSettingsEnabled(getDataSource());
	}

    public String getShowSwitchSettingsDiv() {
		if (!isNeedSwitchSettings()) {
			return "none";
		}
		return "";
	}

	public String getHideSwitchSettingsDiv() {
		return "none";
	}
	
	public String getShowRouterRadiusSettingsDiv() {
		if (!getDataSource().getConfigType().isRouterContained()) {
			return "none";
		}
		return "";
	}

	public String getHideRouterRadiusSettingsDiv() {
		return "none";
	}
	
	public String getHideDivSwitchModeOnly() {
		if (getDataSource().getConfigType().isSwitchOnly()) {
			return "none";
		}
		return "";
	}
	
	public String getHideDivSwitchOrRouterMode(){
		if (getDataSource().getConfigType().isWirelessOrBonjourContained()){
			return "";
		}
		return "none";
	}
	
	public String getHideLocationAndClientWatchDiv(){
		if (getDataSource().getConfigType().isWirelessOrBonjourContained()){
			return "";
		}
		return "none";
	}
	
	public String getMstpEditButton(){
		if(getDataSource().getSwitchSettings() != null){
			StpSettings stpSettings = getDataSource().getSwitchSettings().getStpSettings();
			if (stpSettings != null) {
				if (stpSettings.getStp_mode() == StpSettings.STP_MODE_MSTP){
					return "";
				}
			}
		}
		
		return "none";
	} 

	public String getShowServiceSettingsDiv() {
		return "";
	}

	public String getHideServiceSettingsDiv() {
		return "none";
	}

	public String getShowServerSettingsDiv() {
		return "";
	}

	public String getHideServerSettingsDiv() {
		return "none";
	}

	public String getShowQosSettingsDiv() {
//		if (getDataSource().getConfigType().isSwitchOnly()) {
//			return "none";
//		}
		return "";
	}

	public String getHideQosSettingsDiv() {
		return "none";
	}

//	public String getShowVpnSettingsDiv() {
//		if (getTabId() == 5) {
//			return "none";
//		}
//		return "";
//	}
//
//	public String getHideVpnSettingsDiv() {
//		if (getTabId() == 5) {
//			return "";
//		}
//		return "none";
//	}
	
	public String getShowReportSettingsDiv() {
		if (getDataSource().getConfigType().isSwitchOnly()) {
			return "none";
		}
		return "";
	}
	
	public String getHideReportSettingsDiv() {
		return "none";
	}
	
	
	public String getShowTVSettingsDiv() {
		if (!isTeacherViewEnabled()) {
			return "none";
		}
		if (!getDataSource().getConfigType().isWirelessContained() && !getDataSource().getConfigType().isBonjourContained()){
			return "none";
		}
		return "";
	}
	
	public String getHideTVSettingsDiv() {
		if (!isTeacherViewEnabled()) {
			return "none";
		}
		return "none";
	}
	
	public String getShowPPSKRegDiv() {
		return "";
	}
	
	public String getHidePPSKRegDiv() {
		return "none";
	}
	
	public String getHideNativeVlan(){
		if (getDataSource().getConfigType().isSwitchOnly()) {
			return "none";
		}
		return "";
	}
	
	public String getHideTVCheckBoxOption(){
		if (getDataSource().isEnableOSDURL()) {
			return "";
		}
		return "none";
	}
	
	public String getHideTVSelectOption(){
		if (getDataSource().isEnableTVService() && getDataSource().isEnableOSDURL()) {
			return "";
		}
		return "none";
	}
	
	public String getHideReportSettingsDetailDiv(){
		if (getDataSource().getEnableReportCollection()) {
			return "";
		}
		return "none";
	}
	
	public String getHideClientWatch() throws Exception {
		if (locationServerId!=null && locationServerId>0) {
			LocationServer locationServer =	findBoById(LocationServer.class, locationServerId);
			if (locationServer != null){
				if (locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROHIVE){
					return "";
				} else {
					return "none";
				}
			} else {
				return "none";
			}
		} else {
			return "none";
		}
	}
	
	public String getHideProbeDetail(){
		if (getDataSource().isEnableProbe()) {
			return "";
		}
		return "none";
	}
	
	public String getChangeLocationServerOperation(){
		return "changeLocationServer";
	}
	
	public Range getSlaIntervalRange() {
		return super.getAttributeRange("slaInterval");
	}

	public Long getMgtDnsId() {
		return mgtDnsId;
	}

	public void setMgtDnsId(Long mgtDnsId) {
		this.mgtDnsId = mgtDnsId;
	}

	public Long getMgtTimeId() {
		return mgtTimeId;
	}

	public void setMgtTimeId(Long mgtTimeId) {
		this.mgtTimeId = mgtTimeId;
	}

	public Long getLocationServerId() {
		return locationServerId;
	}

	public void setLocationServerId(Long locationServerId) {
		this.locationServerId = locationServerId;
	}

	public Long getLldpCdpId() {
		return lldpCdpId;
	}

	public void setLldpCdpId(Long lldpCdpId) {
		this.lldpCdpId = lldpCdpId;
	}

	public Long getMgtSnmpId() {
		return mgtSnmpId;
	}

	public void setMgtSnmpId(Long mgtSnmpId) {
		this.mgtSnmpId = mgtSnmpId;
	}

	public Long getMgtSyslogId() {
		return mgtSyslogId;
	}

	public void setMgtSyslogId(Long mgtSyslogId) {
		this.mgtSyslogId = mgtSyslogId;
	}

	public Long getClientWatchId() {
		return clientWatchId;
	}

	public void setClientWatchId(Long clientWatchId) {
		this.clientWatchId = clientWatchId;
	}

	public Long getAlgConfigId() {
		return algConfigId;
	}

	public void setAlgConfigId(Long algConfigId) {
		this.algConfigId = algConfigId;
	}

	public Long getMgtOptionId() {
		return mgtOptionId;
	}

	public void setMgtOptionId(Long mgtOptionId) {
		this.mgtOptionId = mgtOptionId;
	}

	public Long getIdsPolicyId() {
		return idsPolicyId;
	}

	public void setIdsPolicyId(Long idsPolicyId) {
		this.idsPolicyId = idsPolicyId;
	}

	public Long getAccessConsoleId() {
		return accessConsoleId;
	}

	public void setAccessConsoleId(Long accessConsoleId) {
		this.accessConsoleId = accessConsoleId;
	}

	public Long getIpFilterId() {
		return ipFilterId;
	}

	public void setIpFilterId(Long ipFilterId) {
		this.ipFilterId = ipFilterId;
	}

	public Long getDeviceServiceId() {
		return deviceServiceId;
	}

	public void setDeviceServiceId(Long deviceServiceId) {
		this.deviceServiceId = deviceServiceId;
	}

	public Long getEth0ServiceId() {
		return eth0ServiceId;
	}

	public void setEth0ServiceId(Long eth0ServiceId) {
		this.eth0ServiceId = eth0ServiceId;
	}

	public Long getEth1ServiceId() {
		return eth1ServiceId;
	}

	public void setEth1ServiceId(Long eth1ServiceId) {
		this.eth1ServiceId = eth1ServiceId;
	}

	public Long getRedServiceId() {
		return redServiceId;
	}

	public void setRedServiceId(Long redServiceId) {
		this.redServiceId = redServiceId;
	}

	public Long getAggServiceId() {
		return aggServiceId;
	}

	public void setAggServiceId(Long aggServiceId) {
		this.aggServiceId = aggServiceId;
	}

	public Long getEthernetAccessId() {
		return ethernetAccessId;
	}

	public void setEthernetAccessId(Long ethernetAccessId) {
		this.ethernetAccessId = ethernetAccessId;
	}

	public Long getEthernetAccessEth1Id() {
		return ethernetAccessEth1Id;
	}

	public void setEthernetAccessEth1Id(Long ethernetAccessEth1Id) {
		this.ethernetAccessEth1Id = ethernetAccessEth1Id;
	}

	public Long getEthernetAccessRedId() {
		return ethernetAccessRedId;
	}

	public void setEthernetAccessRedId(Long ethernetAccessRedId) {
		this.ethernetAccessRedId = ethernetAccessRedId;
	}

	public Long getEthernetAccessAggId() {
		return ethernetAccessAggId;
	}

	public void setEthernetAccessAggId(Long ethernetAccessAggId) {
		this.ethernetAccessAggId = ethernetAccessAggId;
	}

	public Long getEthernetBridgeId() {
		return ethernetBridgeId;
	}

	public void setEthernetBridgeId(Long ethernetBridgeId) {
		this.ethernetBridgeId = ethernetBridgeId;
	}

	public Long getEthernetBridgeEth1Id() {
		return ethernetBridgeEth1Id;
	}

	public void setEthernetBridgeEth1Id(Long ethernetBridgeEth1Id) {
		this.ethernetBridgeEth1Id = ethernetBridgeEth1Id;
	}

	public Long getEthernetBridgeRedId() {
		return ethernetBridgeRedId;
	}

	public void setEthernetBridgeRedId(Long ethernetBridgeRedId) {
		this.ethernetBridgeRedId = ethernetBridgeRedId;
	}

	public Long getEthernetBridgeAggId() {
		return ethernetBridgeAggId;
	}

	public void setEthernetBridgeAggId(Long ethernetBridgeAggId) {
		this.ethernetBridgeAggId = ethernetBridgeAggId;
	}

	public Long getEth0BackServiceId() {
		return eth0BackServiceId;
	}

	public void setEth0BackServiceId(Long eth0BackServiceId) {
		this.eth0BackServiceId = eth0BackServiceId;
	}

	public Long getEth1BackServiceId() {
		return eth1BackServiceId;
	}

	public void setEth1BackServiceId(Long eth1BackServiceId) {
		this.eth1BackServiceId = eth1BackServiceId;
	}

	public Long getRed0BackServiceId() {
		return red0BackServiceId;
	}

	public void setRed0BackServiceId(Long red0BackServiceId) {
		this.red0BackServiceId = red0BackServiceId;
	}

	public Long getAgg0BackServiceId() {
		return agg0BackServiceId;
	}

	public void setAgg0BackServiceId(Long agg0BackServiceId) {
		this.agg0BackServiceId = agg0BackServiceId;
	}

	public Long getWireServiceId() {
		return wireServiceId;
	}

	public void setWireServiceId(Long wireServiceId) {
		this.wireServiceId = wireServiceId;
	}

//	public Long getVpnServiceId() {
//		return vpnServiceId;
//	}
//
//	public void setVpnServiceId(Long vpnServiceId) {
//		this.vpnServiceId = vpnServiceId;
//	}

	public String[] getArraySsidOnly() {
		return arraySsidOnly;
	}

	public void setArraySsidOnly(String[] arraySsidOnly) {
		this.arraySsidOnly = arraySsidOnly;
	}

	public String[] getArrayNetwork() {
		return arrayNetwork;
	}

	public void setArrayNetwork(String[] arrayNetwork) {
		this.arrayNetwork = arrayNetwork;
	}

	public String[] getArrayMacOui() {
		return arrayMacOui;
	}

	public void setArrayMacOui(String[] arrayMacOui) {
		this.arrayMacOui = arrayMacOui;
	}

	public String[] getArraySsid() {
		return arraySsid;
	}

	public void setArraySsid(String[] arraySsid) {
		this.arraySsid = arraySsid;
	}

	public String[] getArrayCheckE() {
		return arrayCheckE;
	}

	public void setArrayCheckE(String[] arrayCheckE) {
		this.arrayCheckE = arrayCheckE;
	}

	public String[] getArrayCheckP() {
		return arrayCheckP;
	}

	public void setArrayCheckP(String[] arrayCheckP) {
		this.arrayCheckP = arrayCheckP;
	}

	public String[] getArrayCheckD() {
		return arrayCheckD;
	}

	public void setArrayCheckD(String[] arrayCheckD) {
		this.arrayCheckD = arrayCheckD;
	}

	public String[] getArrayCheckET() {
		return arrayCheckET;
	}

	public void setArrayCheckET(String[] arrayCheckET) {
		this.arrayCheckET = arrayCheckET;
	}

	public String[] getArrayCheckPT() {
		return arrayCheckPT;
	}

	public void setArrayCheckPT(String[] arrayCheckPT) {
		this.arrayCheckPT = arrayCheckPT;
	}

	public String[] getArrayCheckDT() {
		return arrayCheckDT;
	}

	public void setArrayCheckDT(String[] arrayCheckDT) {
		this.arrayCheckDT = arrayCheckDT;
	}

	public List<ConfigTemplateQos> getListQosRateLimit() {
		return listQosRateLimit;
	}

    public List<Long> getIpTrackIds() {
		return ipTrackIds;
	}

	public void setIpTrackIds(List<Long> ipTrackIds) {
		this.ipTrackIds = ipTrackIds;
	}

	public List<Long> getTvNetworkIds() {
		return tvNetworkIds;
	}

	public void setTvNetworkIds(List<Long> tvNetworkIds) {
		this.tvNetworkIds = tvNetworkIds;
	}

	public OptionsTransfer getIpTrackOptions() {
		return ipTrackOptions;
	}

	public OptionsTransfer getTvNetworkOptions() {
		return tvNetworkOptions;
	}
	// for user profile selection of LAN profile
	// which LAN profile is now dealing
	private Long selectedLanId;
	private Long addedUserProfileId;
	
	//used for the id newly added
	private Long addedHiveId;

	public Long getSelectedLanId() {
		return selectedLanId;
	}

	public void setSelectedLanId(Long selectedLanId) {
		this.selectedLanId = selectedLanId;
	}

	public Long getAddedUserProfileId() {
		return addedUserProfileId;
	}

	public void setAddedUserProfileId(Long addedUserProfileId) {
		this.addedUserProfileId = addedUserProfileId;
	}
	
	public Long getAddedHiveId() {
		return addedHiveId;
	}

	public void setAddedHiveId(Long addedHiveId) {
		this.addedHiveId = addedHiveId;
	}

	public Long getVlanId() {
		return vlanId;
	}

	public void setVlanId(Long vlanId) {
		this.vlanId = vlanId;
	}

	public Long getVlanNativeId() {
		return vlanNativeId;
	}

	public void setVlanNativeId(Long vlanNativeId) {
		this.vlanNativeId = vlanNativeId;
	}

	public List<CheckItem> getList_vlan() {
		return list_vlan;
	}

	public List<CheckItem> getList_classifierMap() {
		return list_classifierMap;
	}

	public List<CheckItem> getList_markerMap() {
		return list_markerMap;
	}

	public Long getRouterIpTrackId() {
		return routerIpTrackId;
	}

	public void setRouterIpTrackId(Long routerIpTrackId) {
		this.routerIpTrackId = routerIpTrackId;
	}

	public List<CheckItem> getList_ipTrack() {
		return list_ipTrack;
	}
	
	/*
	 * deal profile configurations change here when type of network policy changed, e.g. remove or clear some profiles that are not support in some type(s) 
	 */
	private boolean changeNetworkPolicyType(ConfigTemplate configTemplateTmp, ConfigTemplateType curConfigType) {
		if (configTemplateTmp == null) {
			return false;
		}
		// to branch routing
		if (curConfigType.isRouterContained()
				&& !configTemplateTmp.getConfigType().isRouterContained()) {
			// remove layer 2 vpn service setting
			if (configTemplateTmp.getVpnService() != null 
					&& VpnService.IPSEC_VPN_LAYER_2 == configTemplateTmp.getVpnService().getIpsecVpnType()) {
				configTemplateTmp.setVpnService(null);
			}
			
			List<?> networkNameLst= QueryUtil.executeQuery("select networkName from " + HmStartConfig.class.getSimpleName(),
					null, null, domainId);
			String strFetchNetworkName = BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT;
			VpnNetwork vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
			if(null == vpnNetWork){
				if (!networkNameLst.isEmpty()) {
					strFetchNetworkName = networkNameLst.get(0).toString();
				}
				vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
				
				if (vpnNetWork==null) {
					strFetchNetworkName=getDomain().getDomainName();
					vpnNetWork = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", strFetchNetworkName, domainId, this);
				}
			}
			configTemplateTmp.setVlanNetwork(new ArrayList<ConfigTemplateVlanNetwork>());
			ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
			cvn.setVlan(configTemplateTmp.getVlan());
			cvn.setNetworkObj(vpnNetWork);
			configTemplateTmp.getVlanNetwork().add(cvn);

//			if (configTemplateTmp.getRouterIpTrack()==null) {
//				MgmtServiceIPTrack routerIpTrackClass = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
//						BeParaModule.DEFAULT_IP_TRACKING_BR_NAME_NEW, domainId);
//				if (routerIpTrackClass!=null) {
//					configTemplateTmp.setRouterIpTrack(routerIpTrackClass);
//				}
//			}
		} 
		// when no branch routing is supported
		if (!curConfigType.isRouterContained()){
			// remove layer 3 vpn service setting
			if (configTemplateTmp.getVpnService() != null 
					&& VpnService.IPSEC_VPN_LAYER_3 == configTemplateTmp.getVpnService().getIpsecVpnType()) {
				configTemplateTmp.setVpnService(null);
			}
			if (configTemplateTmp.getVlanNetwork()!=null) {
				configTemplateTmp.getVlanNetwork().clear();
			}
//			configTemplateTmp.setRouterIpTrack(null);
			configTemplateTmp.setFwPolicy(null);
			getDataSource().setRadiusProxyProfile(null);
			getDataSource().setRadiusServerProfile(null);
			getDataSource().setRoutingProfilePolicy(null);
		}
		
		if (curConfigType.isBonjourContained()) {
			if (getDataSource().getBonjourGw()==null) {
				BonjourGatewaySettings bgsAerohive = QueryUtil.findBoByAttribute(BonjourGatewaySettings.class, "bonjourGwName", BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_AEROHIVE,
						getDomain().getId());
				getDataSource().setBonjourGw(bgsAerohive);
			}
		}
		
		if (!curConfigType.isWirelessContained()) {
			getDataSource().setSsidInterfaces(BoGenerationUtil.genDefaultSsidInterfaces());
		}
		
		if (!checkSwitchSettingsEnabled(configTemplateTmp)) {
			if (getDataSource().getSwitchSettings() != null){
				if (getDataSource().getSwitchSettings().getStpSettings() != null) {
					getDataSource().getSwitchSettings().setStpSettings(null);
				} else {
					getDataSource().setSwitchSettings(null);
				}
			}
		}
		
		if (curConfigType.isBonjourOnly()) {
			configTemplateTmp.getPortProfiles().clear();
			getDataSource().setVpnService(null);
		}
		// switch only do nothing, because above is clean all no used settings
//		if (curConfigType.isSwitchOnly()) {
//		}
		changePortTemplate(curConfigType);
		
		return true;
	}

    private void changePortTemplate(ConfigTemplateType curConfigType) {
        Set<PortGroupProfile> portProfiles = getDataSource().getPortProfiles();
		if(curConfigType.isBonjourOnly()) {
		    portProfiles.clear();
		}
		Set<PortGroupProfile> removabledPorts = new HashSet<>();
		for (PortGroupProfile portGroupProfile : portProfiles) {
		    if(null != portGroupProfile) {
		        if(!curConfigType.isWirelessContained()) {
		            //remove the BR100 as AP of port template
		            if(portGroupProfile.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
		                removabledPorts.add(portGroupProfile);
		            } 
		        }
		        if(!curConfigType.isRouterContained()) {
		            //remove AP3xx|BR1xx|BR2xx and SR2024 as Router of port template
		            if(portGroupProfile.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
		                removabledPorts.add(portGroupProfile);
		            } 
		        }
		        if(!curConfigType.isSwitchContained()) {
		            //remove SR2024 as Switch of port template
		            if(portGroupProfile.getDeviceType() == HiveAp.Device_TYPE_SWITCH) {
		                removabledPorts.add(portGroupProfile);
		            } 
		        }
		    }
        }
		portProfiles.removeAll(removabledPorts);
    }

	public Long getIpTrackId() {
		return ipTrackId;
	}

	public void setIpTrackId(Long ipTrackId) {
		this.ipTrackId = ipTrackId;
	}

	public List<CheckItem> getList_mgtNetwork() {
		return list_mgtNetwork;
	}

//	public Long getMgtNetworkId() {
//		return mgtNetworkId;
//	}
//
//	public void setMgtNetworkId(Long mgtNetworkId) {
//		this.mgtNetworkId = mgtNetworkId;
//	}
	
	/**
	 * This function is to verify if the VPN service in this network policy
	 * has VPN topology or not
	 * @return true or false
	 * jchen, 2011-09-15
	 */
	public boolean getHasVpnTopology() {
		VpnService vpn = getDataSource().getVpnService();
		
		if(vpn == null) {
			return false;
		}
		
		/*
		 * get the VPN servers and clients which use this VPN service
		 * here, they are Long object id
		 */
		Set<Long> vpnServers = ConfigurationUtils.getRelevantVpnServers(vpn);
		
		if(vpnServers == null || vpnServers.isEmpty()) {
			return false;
		}
		
		/*
		 * get MAC address of servers
		 */
		List<?> servers = QueryUtil.executeQuery("select bo.macAddress from "
						+ HiveAp.class.getSimpleName() + " bo",
				null, new FilterParams("id", vpnServers));
		
		if(servers == null || servers.isEmpty()) {
			return false;
		}
		
		/*
		 * get count of VPN status
		 */
		String where = "serverID in (:s1)";
		long count = QueryUtil.findRowCount(AhVPNStatus.class,new FilterParams(where, new Object[] {
						servers}));
		return count > 0;
	}
	
	public List<CheckItem> getList_radiusServers() {
		return list_radiusServers;
	}

	public Long getTvNetworkId() {
		return tvNetworkId;
	}

	public void setTvNetworkId(Long tvNetworkId) {
		this.tvNetworkId = tvNetworkId;
	}
	
	private Long cloneSrcId;

	public Long getCloneSrcId() {
		return cloneSrcId;
	}

	public void setCloneSrcId(Long cloneSrcId) {
		this.cloneSrcId = cloneSrcId;
	}
	
	public boolean isBlnCloneNetworkPolicy() {
		return cloneSrcId != null && cloneSrcId.compareTo(0L) > 0;
	}

	public boolean isSaveAlways() {
		return saveAlways;
	}

	public void setSaveAlways(boolean saveAlways) {
		this.saveAlways = saveAlways;
	}

	public Long getRadiusProxyId() {
		return radiusProxyId;
	}

	public void setRadiusProxyId(Long radiusProxyId) {
		this.radiusProxyId = radiusProxyId;
	}

	public Long getRadiusServerId() {
		return radiusServerId;
	}

	public void setRadiusServerId(Long radiusServerId) {
		this.radiusServerId = radiusServerId;
	}

	public List<CheckItem> getList_radiusServerOnAp() {
		return list_radiusServerOnAp;
	}

	public List<CheckItem> getList_radiusProxy() {
		return list_radiusProxy;
	}
	
	public Long getSelectBonjourGwId() {
        return selectBonjourGwId;
    }

    public void setSelectBonjourGwId(Long selectBonjourGwId) {
        this.selectBonjourGwId = selectBonjourGwId;
    }


	public boolean isBonjourOnly() {
		if (this.getDataSource() != null
				&& this.getDataSource().getConfigType().isBonjourOnly()) {
			return true;
		}
		return false;
	}
	
	public String getHideTrafficFilter4IndividualAPs(){
		if(getDataSource() != null && getDataSource().getConfigType().isSwitchOnly()){
			return "none";
		}
		return "";
	}
	
	public String getTrafficFilter4IndividualAPsStyle(){
		if(getDataSource() == null){
			return "none";
		}
		return getDataSource().isOverrideTF4IndividualAPs() ? "":"none" ;
	}
	
	public List<CheckItem> getList_routingPolicy() {
		return list_routingPolicy;
	}
	
	public Long getRoutingPolicyId() {
		return routingPolicyId;
	}

	public void setRoutingPolicyId(Long routingPolicyId) {
		this.routingPolicyId = routingPolicyId;
	}
	
	public class ConfigTemplateTypeProxy {
		private ConfigTemplateType configType;
		private String wirelessSelectedValue;
		private String switchSelectedValue;
		private String routerSelectedValue;
		private String bonjourSelectedValue;
		
		public ConfigTemplateTypeProxy() {
			configType = new ConfigTemplateType().setDefaultTemplateType();
		}
		
		public ConfigTemplateTypeProxy(ConfigTemplate config) {
			if (config == null) {
				this.configType = new ConfigTemplateType().setDefaultTemplateType();
			} else {
				this.configType = config.getConfigType();
			}
			wirelessSelectedValue = getStringValueOfTypeSelection(this.configType.isTypeSupport(ConfigTemplateType.WIRELESS));
			switchSelectedValue = getStringValueOfTypeSelection(this.configType.isTypeSupport(ConfigTemplateType.SWITCH));
			routerSelectedValue = getStringValueOfTypeSelection(this.configType.isTypeSupport(ConfigTemplateType.ROUTER));
			bonjourSelectedValue = getStringValueOfTypeSelection(this.configType.isTypeSupport(ConfigTemplateType.BONJOUR));
		}
		
		private String getStringValueOfTypeSelection(boolean blnSelected) {
			if (blnSelected) {
				return "1";
			}
			return "-1";
		}
		
		private boolean getBooleanValueOfTypeSelection(String value) {
			if ("1".equals(value)) {
				return true;
			}
			return false;
		}
		
		public ConfigTemplateType getConfigType() {
			if (!this.configType.isTypeSet()) {
				this.configType.setWirelessEnabled(true);
			}
			return this.configType;
		}

		public String getWirelessSelectedValue() {
			return wirelessSelectedValue;
		}

		public void setWirelessSelectedValue(String wirelessSelectedValue) {
			this.wirelessSelectedValue = wirelessSelectedValue;
			this.configType.setWirelessEnabled(getBooleanValueOfTypeSelection(wirelessSelectedValue));
		}

		public String getSwitchSelectedValue() {
			return switchSelectedValue;
		}

		public void setSwitchSelectedValue(String switchSelectedValue) {
			this.switchSelectedValue = switchSelectedValue;
			this.configType.setSwitchEnabled(getBooleanValueOfTypeSelection(switchSelectedValue));
		}

		public String getRouterSelectedValue() {
			return routerSelectedValue;
		}

		public void setRouterSelectedValue(String routerSelectedValue) {
			this.routerSelectedValue = routerSelectedValue;
			this.configType.setRouterEnabled(getBooleanValueOfTypeSelection(routerSelectedValue));
		}

		public String getBonjourSelectedValue() {
			return bonjourSelectedValue;
		}

		public void setBonjourSelectedValue(String bonjourSelectedValue) {
			this.bonjourSelectedValue = bonjourSelectedValue;
			this.configType.setBonjourEnabled(getBooleanValueOfTypeSelection(bonjourSelectedValue));
		}
	}
	
	private ConfigTemplateTypeProxy npTypeCtl = null;

	public void prepareNpTypeProxy(ConfigTemplate configArg) {
		npTypeCtl = new ConfigTemplateTypeProxy(configArg);
	}
	
	public ConfigTemplateTypeProxy getNpTypeCtl() {
		if (this.npTypeCtl == null) {
			this.npTypeCtl = new ConfigTemplateTypeProxy();
		}
		return this.npTypeCtl;
	}
	
	public void setNpTypeCtl(ConfigTemplateTypeProxy npTypeCtl) {
		this.npTypeCtl = npTypeCtl;
	}
	
	private List<String> messagesToShown;

	public List<String> getMessagesToShown() {
		return messagesToShown;
	}

	public void setMessagesToShown(List<String> messagesToShown) {
		this.messagesToShown = messagesToShown;
	}
	
    public static String getAAASuffix(boolean isPPSK) {
        return isPPSK ? "_AAAClientPSK" : "_AAAClient";
    }
	
    public boolean isJumpFromIDM() {
        return false;
    }
    
    public OptionsTransfer getIpTrackWANOptions() {
		return ipTrackWANOptions;
	}

	public void setIpTrackWANOptions(OptionsTransfer ipTrackWANOptions) {
		this.ipTrackWANOptions = ipTrackWANOptions;
	}

	public void setIpTrackOptions(OptionsTransfer ipTrackOptions) {
		this.ipTrackOptions = ipTrackOptions;
	}

	public List<Long> getIpTrackWANOptionsIds() {
		return ipTrackWANOptionsIds;
	}

	public void setIpTrackWANOptionsIds(List<Long> ipTrackWANOptionsIds) {
		this.ipTrackWANOptionsIds = ipTrackWANOptionsIds;
	}

	public List<CheckItem> getIpTrackWANList() {
		return ipTrackWANList;
	}

	public void setIpTrackWANList(List<CheckItem> ipTrackWANList) {
		this.ipTrackWANList = ipTrackWANList;
	}

	public Long getPrimaryIpTrackId() {
		return primaryIpTrackId;
	}

	public void setPrimaryIpTrackId(Long primaryIpTrackId) {
		this.primaryIpTrackId = primaryIpTrackId;
	}

	public Long getBackup1TrackId() {
		return backup1TrackId;
	}

	public void setBackup1TrackId(Long backup1TrackId) {
		this.backup1TrackId = backup1TrackId;
	}

	public Long getBackup2TrackId() {
		return backup2TrackId;
	}

	public void setBackup2TrackId(Long backup2TrackId) {
		this.backup2TrackId = backup2TrackId;
	}

	public Long getThirdPortId() {
		return thirdPortId;
	}

	public void setThirdPortId(Long thirdPortId) {
		this.thirdPortId = thirdPortId;
	}

	public List<CheckItem> getIpTrackWANListWithBlank() {
		return ipTrackWANListWithBlank;
	}

	public void setIpTrackWANListWithBlank(List<CheckItem> ipTrackWANListWithBlank) {
		this.ipTrackWANListWithBlank = ipTrackWANListWithBlank;
	}
	
    private HiveProfile getDefaultVHMHiveProfile(List<?> networkNameLst) {
    	HiveProfile hive = null;
    	if (networkNameLst == null) {
    		networkNameLst= QueryUtil.executeQuery("select networkName from " + HmStartConfig.class.getSimpleName(),
				null, null, domainId);
    	}
		if (networkNameLst != null
				&& !networkNameLst.isEmpty()) {
			hive = QueryUtil.findBoByAttribute(HiveProfile.class, "hiveName", networkNameLst.get(0).toString(), domainId);
		}
		if (hive == null) {
			hive = QueryUtil.findBoByAttribute(HiveProfile.class, "hiveName", getDomain().getDomainName(), domainId);
		}
		if (hive == null) {
			hive = QueryUtil.findBoByAttribute(HiveProfile.class, "defaultFlag", false, domainId);
		}
		
		return hive;
    }
    
    private List<UserProfileVlanRelation> upVlanMapping;

	public List<UserProfileVlanRelation> getUpVlanMapping() {
		return upVlanMapping;
	}

	public void setUpVlanMapping(List<UserProfileVlanRelation> upVlanMapping) {
		this.upVlanMapping = upVlanMapping;
	}

	private Long[] mappingUpId;
	private Long[] mappingVlanId;
	private Long userProfileId;
	private static final String SESSION_UP_VLAN_MAPPING_UP_ID_KEY = "SESSION_UP_VLAN_MAPPING_UP_ID_KEY";
	private static final String SESSION_UP_VLAN_MAPPING_TYPE_KEY = "SESSION_UP_VLAN_MAPPING_TYPE_KEY";
	private static final String SESSION_UP_VLAN_MAPPING_RELATIVE_ID_KEY = "SESSION_UP_VLAN_MAPPING_RELATIVE_ID_KEY";

    
	public List<CheckItem> getList_mstpRegion() {
		return list_mstpRegion;
	}
	
	public Long getMstpRegionId() {
		return mstpRegionId;
	}

	public void setMstpRegionId(Long mstpRegionId) {
		this.mstpRegionId = mstpRegionId;
	}
	
	public EnumItem[] getStpModeStp(){
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_STP,
				getText("config.switchSettings.STPmode.stp")) };
	}
	
	public EnumItem[] getStpModeRstp(){
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_RSTP,
				getText("config.switchSettings.STPmode.rstp")) };
	}
	
	public EnumItem[] getStpModeMstp(){
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_MSTP,
				getText("config.switchSettings.STPmode.mstp")) };
	}
	
	public String getEnabledStpMode(){
		if(getDataSource().getSwitchSettings().getStpSettings().isEnableStp()){
			return "";
		}else{
			return "none";
		}
	}
	
	public String getEnabledIgmpMode(){
		if(getDataSource().getSwitchSettings().isEnableIgmpSnooping()){
			return "";
		}else{
			return "none";
		}
	}
	
	public boolean getEnabledMstpMode(){
		if(getDataSource().getSwitchSettings().getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP){
			return false;
		}else{
			return true;
		}
	}

	public Long[] getMappingUpId() {
		return mappingUpId;
	}

	public void setMappingUpId(Long[] mappingUpId) {
		this.mappingUpId = mappingUpId;
	}

	public Long[] getMappingVlanId() {
		return mappingVlanId;
	}

	public void setMappingVlanId(Long[] mappingVlanId) {
		this.mappingVlanId = mappingVlanId;
	}
	
	/**
	 * type is used to indicate what kind of user profiles to be fetched out, e.g. ssid/lan
	 * @param ct
	 * @param type
	 */
	private void prepareUserProfileVlanMappingRelation(ConfigTemplate ct, String type) {
		upVlanMapping = NpUserProfileVlanMappingUtil.prepareUserProfileVlanMappingRelation(ct, type, 
				UserProfileVlanMapping.getRelativeProfile(type, this.upVlanRelativeId),
				type.equals(UserProfileVlanMapping.MAPPING_TYPE_SSID) ? new QueryCertainBo<SsidProfile>() {
							@Override
							public Collection<HmBo> loadBo(SsidProfile bo) {
								loadLazySsidProfile(bo);
								return null;
							}
						} : new QueryCertainBo<PortAccessProfile>() {
                            @Override
                            public Collection<HmBo> loadBo(PortAccessProfile bo) {
                                loadLazyPortAccessProfile(bo);
                                return null;
                            }
                        });
		
		this.vlanIdList = getBoCheckItems("vlanName", Vlan.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);
		CheckItem item0 = this.vlanIdList.get(0);
		this.vlanIdList.remove(0);
		Collections.sort(this.vlanIdList, new Comparator<CheckItem>(){
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		this.vlanIdList.add(0, item0);
	}
	
	private List<CheckItem> vlanIdList;

	public List<CheckItem> getVlanIdList() {
		return vlanIdList;
	}

	public void setVlanIdList(List<CheckItem> vlanIdList) {
		this.vlanIdList = vlanIdList;
	}
	
	public Long getSelectNetworkId() {
		return selectNetworkId;
	}

	public void setSelectNetworkId(Long selectNetworkId) {
		this.selectNetworkId = selectNetworkId;
		if (getDataSource()!=null) {
			getDataSource().setPreNetworkId(selectNetworkId);
		}
	}
	public void setPreNetworkId(Long preNetworkId) {
		if (getDataSource()!=null) {
			if (preNetworkId.longValue()<0) {
				getDataSource().setPreNetworkId(null);
			} else {
				getDataSource().setPreNetworkId(preNetworkId);
			}
		}
	}

	public void setPreVlanId(Long preVlanId) {
		if (getDataSource()!=null) {
			getDataSource().setPreVlanId(preVlanId);
		}
	}


	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}
	
	private Vlan vlan;

	public Vlan getVlan() {
		return vlan;
	}
	
	private String upVlanMappingType;
	private Long upVlanRelativeId;
	public String getUpVlanMappingType() {
		return upVlanMappingType;
	}

	public void setUpVlanMappingType(String upVlanMappingType) {
		this.upVlanMappingType = upVlanMappingType;
	}
	
	public Long getUpVlanRelativeId() {
		return upVlanRelativeId;
	}

	
	public boolean getEnableClientManagement(){
		return QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement();
	}
	
	public void setUpVlanRelativeId(Long upVlanRelativeId) {
		this.upVlanRelativeId = upVlanRelativeId;
	}

	private void reloadUpVlanMappingForNetworkPolicy() {
		if (this.getDataSource() != null
				&& this.getDataSource().getId() != null) {
			Set<UserProfileVlanMapping> dbMappings = new HashSet<>(QueryUtil.executeQuery(UserProfileVlanMapping.class, 
					null, 
					new FilterParams("networkPolicy.id", this.getDataSource().getId()), 
					this.getDataSource().getOwner().getId(),
					UserProfileVlanMapping.FULL_BO_QUERY));
			Map<Long, UserProfileVlanMapping> mappingMap = BoAssistant.getIdObjectMap(dbMappings, null);
			Map<Long, UserProfileVlanMapping> mappingSessionMap = BoAssistant.getIdObjectMap(this.getDataSource().getUpVlanMapping(), null);
			Set<UserProfileVlanMapping> mappings = new HashSet<>();
			Map<Long, UserProfile> curAllUps = NpUserProfileVlanMappingUtil.getAllUserProfiles(
					this.getDataSource(), UserProfileVlanMapping.MAPPING_TYPE_ALL, null, true, null, true, false, -1);
			for (UserProfileVlanMapping mapping : this.getDataSource().getUpVlanMapping()) {
				if (curAllUps.containsKey(mapping.getUserProfile().getId())) {
					if (mapping.getId() == null) {
						mapping.setUserProfile(curAllUps.get(mapping.getUserProfile().getId()));
						mappings.add(mapping);
					} else if (mappingMap.containsKey(mapping.getId())) {
						if (mappingSessionMap.containsKey(mapping.getId())) {
							mappings.add(mappingSessionMap.get(mapping.getId()));
						} else {
							mappings.add(mappingMap.get(mapping.getId()));
						}
					}
				}
			}
			this.getDataSource().setUpVlanMapping(mappings);
		}
	}
	
	// ==== Wired Port Template === start
	private Long expandPortTemplateId;
	private int tmpIndex = -1;
    private Set<Long> selectedPortTempalteIds = new HashSet<Long>();
    private Long createPortTempalteIds;
    private Long updatePortTempalteIds;
    private Long networkPolicyID;
    private String selectTmpIdStr;
    public String getSelectTmpIdStr() {
		return selectTmpIdStr;
	}

	public void setSelectTmpIdStr(String selectTmpIdStr) {
		this.selectTmpIdStr = selectTmpIdStr;
	}
	private boolean showAllPortTemplates;
    public enum NetworkPolicyType {
        SUPPORT_SWITCH(0b01), // switch [bonjour] = 1
        SUPPORT_ROUTER(0b10), // router [bonjour] = 2
        SUPPORT_AP(0b11), // wireless [bonjour] =3 
        SUPPORT_NO_AP(0b100), // switch&router, no wireless = 4
        SUPPORT_NO_ROUTER(0b101), // switch&wireless, no router = 5
        SUPPORT_NO_SWITCH(0b110), // router&wireless, no switch = 6
        NIL(-1);
        
        private int value;
        private NetworkPolicyType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        public static NetworkPolicyType get(int value) {
            for (NetworkPolicyType item : values()) {
                if(item.getValue() == value) {
                    return item;
                }
            }
            return NIL;
        }
    }
    
    private String setSelectedPortTemplateProfiles() {
        // Port Template Profiles
        try {
            if (null != selectedPortTempalteIds){
                // DeviceModels, DeviceFunction: NameofPortTemplate
                Map<PairValue<String, Short>, String> portSettingsMap = new HashMap<>();
                
                Set<PortGroupProfile> tmpPortProfiles = new TreeSet<PortGroupProfile>();
                for (Long lanId : selectedPortTempalteIds) {
                	PortGroupProfile portProfile = QueryUtil.findBoById(PortGroupProfile.class, lanId, this);
                	if(!getDataSource().getPortProfiles().isEmpty() && getDataSource().getPortProfiles().contains(portProfile)){
                		for(PortGroupProfile selectProtProfile : getDataSource().getPortProfiles()){
                			if(selectProtProfile.getId().equals(lanId)){
                				portProfile = selectProtProfile;
                			}
                		}
                	}
                    if (portProfile != null) {
                        
                        // check if the same port settings already exists
                        final String[] deviceModels = portProfile.getDeviceModelStrs();
                        final short deviceType = portProfile.getDeviceType();
                        final String profileName = portProfile.getName();
                        if(null != deviceModels) {
                            for (String deviceModel : deviceModels) {
                                if(StringUtils.isNotBlank(deviceModel)) {
                                    final PairValue<String, Short> mapKey = new PairValue<String, Short>(deviceModel, deviceType);
                                    final String mapValue = portSettingsMap.get(mapKey);
                                    if (null == mapValue) {
                                        portSettingsMap.put(mapKey, profileName);
                                    } else {
                                        return MgrUtil.getUserMessage("warn.port.template.duplicate4Config", 
                                                new String[]{MgrUtil.getEnumString("enum.hiveAp.model." + mapKey.getValue()), 
                                                MgrUtil.getEnumString("enum.hiveAp.deviceType." + mapKey.getDesc())});
                                    }
                                }
                            }
                        }
                        
                        List<PortBasicProfile> basicProfiles = portProfile.getBasicProfiles();
                        if(null == basicProfiles || basicProfiles.isEmpty()) {
                            //expandPortTemplateId = lanId;
                        } else {
                            // refresh cache
                            for (PortBasicProfile basic : basicProfiles) {
                            	if(basic.getAccessProfile() != null){
                            		 basic.setAccessProfile(QueryUtil.findBoById(
                                             PortAccessProfile.class, basic.getAccessProfile().getId(), this));
                            	}
                            }
                        }
                        //get non-default templates
//                        List<SingleTableItem> items = portProfile.getItems();
//                        if(null == items || items.isEmpty()) {
//                        } else {
//                            // refresh cache
//                            for (SingleTableItem item : items) {
//                            	item.setNonDefault(QueryUtil.findBoById(PortGroupProfile.class, item.getNonGlobalId(), this));
//                            }
//                        }
                        tmpPortProfiles.add(portProfile);
                    }
                }
                Set<PortGroupProfile> portProfiles = getDataSource().getPortProfiles();
                portProfiles.clear();
                getDataSource().setPortProfiles(tmpPortProfiles);
                
                prepareExpandPortGroup();
                
                prepareColorSet();
            }
        } catch (Exception e) {
            reportActionError(e);
            return "Error when selected the Port Template profile.";
        }
        return "";
    }
    
    IUserProfileAttrValidator validator = null;
    
    private String checkDeviceTemplateUserProfileAttrs() {
        // check user profiles on Port Template Profiles
        final Set<PortGroupProfile> portProfiles = getDataSource().getPortProfiles();
        Set<PortGroupProfile> tempDeviceTemplates = new HashSet<>();
        tempDeviceTemplates.addAll(portProfiles); // get the default device templates
        for (PortGroupProfile profile : portProfiles) {
            for (SingleTableItem items : profile.getItems()) {
                if(null != items.getNonDefault()) {
                    tempDeviceTemplates.add(items.getNonDefault()); // add the tag device templates
                }
            }
        }
        // now loop all the device templates
        Set<Long> userProfileSet = null;
        Map<Long, UserProfile> mapUserProfile = null;
        Map<Long, UserProfile> mapUserProfileAssign = null;
        for (PortGroupProfile portGroupProfile : tempDeviceTemplates) {
            
            userProfileSet = new HashSet<>();
            mapUserProfile = new HashMap<>();
            mapUserProfileAssign = new HashMap<>();
            int cacPercent = 0;
            
            validator = new UserProfileAttrValidateImpl();
            
            List<PortAccessProfile> accesses = portGroupProfile.getAllAccessProfiles();
            
            for (PortAccessProfile access : accesses) {

                if (access.getDefUserProfile() != null) {
                    if (!userProfileSet.contains(access.getDefUserProfile().getId())) {
                        cacPercent = cacPercent + access.getDefUserProfile().getGuarantedAirTime();
                        userProfileSet.add(access.getDefUserProfile().getId());
                        
                        validator.add(UserProfileAttrFactory.getPlainObjFromPort(access.getDefUserProfile(), access, UserProfileOnPort.DEFAULT));
                    }
                }
                if (access.getSelfRegUserProfile() != null) {
                    if (!userProfileSet.contains(access.getSelfRegUserProfile().getId())) {
                        cacPercent = cacPercent + access.getSelfRegUserProfile().getGuarantedAirTime();
                        userProfileSet.add(access.getSelfRegUserProfile().getId());
                        
                        validator.add(UserProfileAttrFactory.getPlainObjFromPort(access.getSelfRegUserProfile(), access, UserProfileOnPort.SELFREG));
                    }
                }
                if (access.getGuestUserProfile() != null) {
                    if (!userProfileSet.contains(access.getGuestUserProfile().getId())) {
                        cacPercent = cacPercent + access.getGuestUserProfile().getGuarantedAirTime();
                        userProfileSet.add(access.getGuestUserProfile().getId());
                        
                        validator.add(UserProfileAttrFactory.getPlainObjFromPort(access.getGuestUserProfile(), access, UserProfileOnPort.GUEST));
                    }
                }
                if (access.getAuthOkUserProfile() != null) {
                    for (UserProfile tempUser : access.getAuthOkUserProfile()) {
                        if (!userProfileSet.contains(tempUser.getId())) {
                            cacPercent = cacPercent + tempUser.getGuarantedAirTime();
                            userProfileSet.add(tempUser.getId());
                            
                            validator.add(UserProfileAttrFactory.getPlainObjFromPort(tempUser, access, UserProfileOnPort.AUTHOK));
                        }
                    }
                }
                if (access.getAuthFailUserProfile() != null) {
                    for (UserProfile tempUser : access.getAuthFailUserProfile()) {
                        if (!userProfileSet.contains(tempUser.getId())) {
                            cacPercent = cacPercent + tempUser.getGuarantedAirTime();
                            userProfileSet.add(tempUser.getId());
                            
                            validator.add(UserProfileAttrFactory.getPlainObjFromPort(tempUser, access, UserProfileOnPort.AUTHFAIL));
                        }
                    }
                }
                if (access.getAuthOkDataUserProfile() != null) {
                    for (UserProfile tempUser : access.getAuthOkDataUserProfile()) {
                        if (!userProfileSet.contains(tempUser.getId())) {
                            cacPercent = cacPercent + tempUser.getGuarantedAirTime();
                            userProfileSet.add(tempUser.getId());
                            
                            validator.add(UserProfileAttrFactory.getPlainObjFromPort(tempUser, access, UserProfileOnPort.AUTHOK_DATA));
                        }
                    }
                }
            
            }
            
            Set<Long> radiusServerUspId = new HashSet<Long>();
            /** UserProfile from Network Firewall */
            if(getDataSource().getFwPolicy() != null && getDataSource().getFwPolicy().getRules() != null){
                for(FirewallPolicyRule rule : getDataSource().getFwPolicy().getRules()){
                    if(rule != null && !rule.isDisableRule() && rule.getSourceUp() != null){
                        radiusServerUspId.add(rule.getSourceUp().getId());
                    }
                }
            }
            
            // check reassign user profile
            if (!userProfileSet.isEmpty()) {
                Set<Long> newSetUserProfile = new HashSet<Long>();
                for (Long upId : userProfileSet) {
                    UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
                    if (mapUserProfile.get(upObj.getId())==null) {
                        mapUserProfile.put(upObj.getId(), upObj);
                    }
                    
                    cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
                            newSetUserProfile, userProfileSet, cacPercent);
                }
                for (Long upId : radiusServerUspId) {
                    if (!userProfileSet.contains(upId) && !newSetUserProfile.contains(upId)) {
                        UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);    
                        newSetUserProfile.add(upId);
                        if (mapUserProfileAssign.get(upId)==null) {
                            mapUserProfileAssign.put(upId, upObj);
                        }
                        cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
                                newSetUserProfile, userProfileSet, cacPercent);
                    }
                }
            }
            
            if (cacPercent > 100) {
                return getText("error.template.guaranteedAirTime");
            }
            
            Map<Long, UserProfile> allUserProfileMaps = new HashMap<Long, UserProfile>();
            allUserProfileMaps.putAll(mapUserProfile);
            allUserProfileMaps.putAll(mapUserProfileAssign);
            if (allUserProfileMaps.values().size() > 64) {
                return getText("error.template.moreUserProfileForNetworkPolicy", 
                        new String []{getDataSource().getConfigName()});
            }
            
            final String returnMsg = checkUserProfileAttribute(mapUserProfile,mapUserProfileAssign, 
                    "In Device Template \"" + portGroupProfile.getName() + "\"");
            if (StringUtils.isBlank(returnMsg)){
                continue;
            }
            return returnMsg;
        }
        
        return "";
    }
    
    public List<CheckItem> getPortTemplateList() throws Exception {
/*        if(showAllPortTemplates) {
        } else {
            return getBoCheckItems("name", PortGroupProfile.class,
                    new FilterParams("parentNPId", getDataSource().getId()));
        }*/
        FilterParams filter = null;
        int limitType = getConfigType4Port();
        List<Short> filterList = new ArrayList<>();
        
        NetworkPolicyType type = NetworkPolicyType.get(limitType);
        switch (type) {
        case SUPPORT_SWITCH:
            filter = new FilterParams("deviceType", HiveAp.Device_TYPE_SWITCH);
            break;
        case SUPPORT_ROUTER:
            filter = new FilterParams("deviceType",HiveAp.Device_TYPE_BRANCH_ROUTER);
            break;
        case SUPPORT_AP:
            filter = new FilterParams("deviceType", HiveAp.Device_TYPE_HIVEAP);
            break;
        case SUPPORT_NO_AP:
            filterList.add(HiveAp.Device_TYPE_SWITCH);
            filterList.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
            filter = new FilterParams("deviceType", filterList);
            break;
        case SUPPORT_NO_ROUTER:
            filterList.add(HiveAp.Device_TYPE_SWITCH);
            filterList.add(HiveAp.Device_TYPE_HIVEAP);
            filter = new FilterParams("deviceType", filterList);
            break;
        case SUPPORT_NO_SWITCH:
            filterList.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
            filterList.add(HiveAp.Device_TYPE_HIVEAP);
            filter = new FilterParams("deviceType", filterList);
            break;

        default:
            break;
        }
//      List<CheckItem> portProfilesList = getBoCheckItems("name", PortGroupProfile.class, filter); --old
        List<CheckItem> portProfilesList = new ArrayList<CheckItem>();
        Set<PortGroupProfile> cacheSelectItems = new HashSet<PortGroupProfile>();
        List<PortGroupProfile> defaultTemplatesList = QueryUtil.executeQuery(PortGroupProfile.class, new SortParams("id"), filter,domainId);
        networkPolicyID = getDataSource().getId();
        
        refreshWiredPorts();
        
        if (defaultTemplatesList != null && defaultTemplatesList.size() != 0) {
            for(PortGroupProfile defaultTemplate : defaultTemplatesList){
            	
            	int flag = 0;
            	
            	//get selected default templates
            	for(PortGroupProfile selectedDefaultTemplate : getDataSource().getPortProfiles()){
            		
            		 selectedPortTempalteIds.add(selectedDefaultTemplate.getId());
            		 cacheSelectItems.add(selectedDefaultTemplate);
              
           		 if(!selectedDefaultTemplate.getItems().isEmpty()){
           			 //get non default templates
           			 for(SingleTableItem nonDefault : selectedDefaultTemplate.getItems()){
           				 if(null != nonDefault && nonDefault.getNonGlobalId() != -1 && 
           						 nonDefault.getConfigTemplateId() == getDataSource().getId().longValue()){
           					  //remove some default templates used as non-default templates in the list at the same policy.
           					 if(nonDefault.getNonGlobalId() == defaultTemplate.getId()){
           						 ++flag;
           						 break;
           					 }
           				 }
           			 }
           		 }
            	}
            	
            	//select
            	StringBuilder builder = new StringBuilder();
            	boolean isnotFirst = false;
            	for(Long tmpID : selectedPortTempalteIds){
            		if(isnotFirst) {
                        builder.append("-");
                    } else {
                        isnotFirst = true;
                    }
            		builder.append(tmpID);
            	}
            	selectTmpIdStr = builder.toString();
            	
            	
            	if(flag == 0){
            		StringBuilder titleStr = new StringBuilder(defaultTemplate.getName());
                	titleStr.append(" (" + MgrUtil.getEnumString("enum.hiveAp.deviceType."+ defaultTemplate.getDeviceType()) + ") "); 
                	String[] deviceModels = defaultTemplate.getDeviceModelStrs();
                	   if(null != deviceModels) {
                		   titleStr.append(" - ");
                           boolean notFirst = false;
                           for (String device : deviceModels) {
                               if(notFirst) {
                            	   titleStr.append(", ");
                               } else {
                                   notFirst = true;
                               }
                               titleStr.append(MgrUtil.getEnumString("enum.hiveAp.model."+device));
                           }
                       }
                       //group template name,deviceModle,deviceType as a title
                	CheckItem item = new CheckItem(defaultTemplate.getId(), titleStr.toString(),defaultTemplate.getDeviceType(),
                			defaultTemplate.getDeviceModelStrs(),defaultTemplate.getDeviceModels());
            	 	portProfilesList.add(item);
            	}
            }
           	//hold on the yellow color for the edit item, if it has no conflicts with other templates!
        	if(null != updatePortTempalteIds){
        			 selectedPortTempalteIds.add(updatePortTempalteIds);
        	}
            if (null != createPortTempalteIds) {
            	  yellowNewPortTemplates(cacheSelectItems);
            }
           
        }
       
        return portProfilesList;
    }
    
    
    private void yellowNewPortTemplates(Set<PortGroupProfile> cacheSelectItems){
         	//if the new Port template is same as the one of the selected items, change the new port template is selected while the
         	//one of selected items has none of non-default templates
         	PortGroupProfile newPortTemplate = QueryUtil.findBoById(PortGroupProfile.class, createPortTempalteIds);
         	int isInSelectItem = 0;
         	if(null != cacheSelectItems && cacheSelectItems.size() !=0 ){
         		for(PortGroupProfile selectItemInCache : cacheSelectItems){
         			int haveIt = 0;
         			if(selectItemInCache.getDeviceType() == newPortTemplate.getDeviceType()){
         				for(String deviceModel : newPortTemplate.getDeviceModelStrs()){
         					int returnValue = Arrays.binarySearch(selectItemInCache.getDeviceModelStrs(), deviceModel);
         					if(returnValue >= 0){
         						++haveIt;
         					}
         				}
         				if(haveIt > 0){
         					if(selectItemInCache.getItems().isEmpty()){
     							for(Iterator<Long> it = selectedPortTempalteIds.iterator();it.hasNext();){
     								Long id = it.next();
     								if(id.equals(selectItemInCache.getId())){
     									it.remove();
     								}
     							}
     							//CheckItem oldSelectItem = new CheckItem(selectItemInCache.getId(), selectItemInCache.toString(),selectItemInCache.getDeviceType(),selectItemInCache.getDeviceModelStrs());
     							//portProfilesList.add(oldSelectItem);
     							selectedPortTempalteIds.add(createPortTempalteIds);
     							//put the new port template into selected item
     							break;
     						}
         				}else if(haveIt == 0){
         					isInSelectItem++;
             				if(isInSelectItem == cacheSelectItems.size()){
             					selectedPortTempalteIds.add(createPortTempalteIds);
             				}
         				}
         			}else{
         				isInSelectItem++;
         				if(isInSelectItem == cacheSelectItems.size()){
         					selectedPortTempalteIds.add(createPortTempalteIds);
         				}
         			}
         		}
         	}else{
         		selectedPortTempalteIds.add(createPortTempalteIds);
         	}
    }
    public List<CheckItem> getPortAccessProfileList() throws Exception {
        List<CheckItem> items = getBoCheckItems("name",
                PortAccessProfile.class, null);
        if (items.size() == 1) {
            if (items.get(0).getId() == CHECK_ITEM_ID_BLANK) {
                items.remove(0);
            }
        }
        items.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK,
                "No Access Profile"));
        return items;
    }
    
    public int getConfigType4Port() {
        int type = 0;
        if(null != getDataSource()) {
            ConfigTemplateType configType = getDataSource().getConfigType();
            if(null != configType) {
                if (configType.isSwitchContained()
                        && configType.isTypeSupportOrStrict(ConfigTemplateType.SWITCH|ConfigTemplateType.BONJOUR)) {
                    // switch [bonjour]
                    type = NetworkPolicyType.SUPPORT_SWITCH.getValue();
                } else if (configType.isRouterContained()
                        && configType.isTypeSupportOrStrict(ConfigTemplateType.ROUTER|ConfigTemplateType.BONJOUR)) {
                    // router [bonjour]
                    type = NetworkPolicyType.SUPPORT_ROUTER.getValue();
                } else if (configType.isWirelessContained()
                        && configType.isTypeSupportOrStrict(ConfigTemplateType.WIRELESS|ConfigTemplateType.BONJOUR)) {
                    // wireless [bonjour]
                    type = NetworkPolicyType.SUPPORT_AP.getValue();
                } else if (configType.isTypeSupport(ConfigTemplateType.SWITCH|ConfigTemplateType.ROUTER)
                        && !configType.isWirelessContained()) {
                    // switch&router, no wireless
                    type =NetworkPolicyType.SUPPORT_NO_AP.getValue();
                } else if (configType.isTypeSupport(ConfigTemplateType.SWITCH|ConfigTemplateType.WIRELESS)
                        && !configType.isRouterContained()) {
                    // switch&wireless, no router
                    type =NetworkPolicyType.SUPPORT_NO_ROUTER.getValue();
                } else if (configType.isTypeSupport(ConfigTemplateType.ROUTER|ConfigTemplateType.WIRELESS)
                        && !configType.isSwitchContained()) {
                    // router&wireless, no switch
                    type = NetworkPolicyType.SUPPORT_NO_SWITCH.getValue();
                }
            }
        }
        return type;
    }

    public Set<Long> getSelectedPortTempalteIds() {
        return selectedPortTempalteIds;
    }
    public Long getCreatePortTempalteIds() {
        return createPortTempalteIds;
    }
    public void setSelectedPortTempalteIds(Set<Long> selectedPortTempalteIds) {
        this.selectedPortTempalteIds = selectedPortTempalteIds;
    }
    public void setCreatePortTempalteIds(Long createPortTempalteIds) {
        this.createPortTempalteIds = createPortTempalteIds;
    }
    public boolean isShowAllPortTemplates() {
        return showAllPortTemplates;
    }
    public void setShowAllPortTemplates(boolean showAllPortTemplates) {
        this.showAllPortTemplates = showAllPortTemplates;
    }
    public Long getExpandPortTemplateId() {
        return expandPortTemplateId;
    }
    public void setExpandPortTemplateId(Long expandPortTemplateId) {
        this.expandPortTemplateId = expandPortTemplateId;
    }
    // ==== Wired Port Template === end

	public Map<Long, UserProfileVlanMapping> getUsMapping() {
		if (usMapping==null) {
			usMapping = NpUserProfileVlanMappingUtil.getFullUpVlanMappings(getDataSource());
		}
		return usMapping;
	}

	public void setUsMapping(Map<Long, UserProfileVlanMapping> usMapping) {
		this.usMapping = usMapping;
	}
	
	//STP Settings
	public void prepareSwitchSettingsObjects() throws Exception {
		list_mstpRegion = getBoCheckItems("regionName", MstpRegion.class, null);
		
		if(getDataSource().getSwitchSettings() != null && getDataSource().getSwitchSettings().getStpSettings() != null){
			if(getDataSource().getSwitchSettings().getStpSettings().isEnableStp() 
					&& getDataSource().getSwitchSettings().getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP){
				if(getDataSource().getSwitchSettings().getStpSettings().getMstpRegion() != null){
				mstpRegionId = getDataSource().getSwitchSettings().getStpSettings().getMstpRegion().getId();
				}
			}
		}else{
			prepareSaveSwitchSettings();
		}
	}
	
	public void prepareSaveSwitchSettings() throws Exception{
		SwitchSettings switchSettings = getDataSource().getSwitchSettings();
		if (switchSettings == null){
			switchSettings = new SwitchSettings();
			switchSettings.setOwner(getDomain());
			switchSettings.setStpSettings(switchSettings.initStpSettings(getDomain()));
			switchSettings.setId(QueryUtil.createBo(switchSettings));
			getDataSource().setSwitchSettings(switchSettings);
		}
		prepareSaveStpSettings();
	}
	
	public void prepareSaveStpSettings() throws Exception{
		StpSettings stpSettings = getDataSource().getSwitchSettings().getStpSettings();
		if (stpSettings != null){
			if (stpSettings.isEnableStp() && stpSettings.getStp_mode() == StpSettings.STP_MODE_MSTP && mstpRegionId != null) {
				MstpRegion tmpClass = QueryUtil.findBoById(MstpRegion.class, mstpRegionId, this);
				stpSettings.setMstpRegion(tmpClass);
				stpSettings.setOwner(getDomain());
			} else if (stpSettings.getStp_mode() != StpSettings.STP_MODE_MSTP && stpSettings.getMstpRegion() != null){
				stpSettings.getMstpRegion().getMstpRegionPriorityList().clear();
				stpSettings.setMstpRegion(null);
				stpSettings.setOwner(getDomain());
			}
		} else {
			stpSettings = new StpSettings();
			stpSettings.setOwner(getDomain());
		}
		getDataSource().getSwitchSettings().setStpSettings(stpSettings);
	}

//	public Set<Long> getSelectVlanNetworkVlanIds() {
//		return selectVlanNetworkVlanIds;
//	}
//
//	public void setSelectVlanNetworkVlanIds(Set<Long> selectVlanNetworkVlanIds) {
//		this.selectVlanNetworkVlanIds = selectVlanNetworkVlanIds;
//	}

	public List<CheckItem> getVlanUserAddList() {
		return vlanUserAddList;
	}

	public Long getVlanMappingId() {
		return vlanMappingId;
	}

	public void setVlanMappingId(Long vlanMappingId) {
		this.vlanMappingId = vlanMappingId;
	}

	public String getInputVlanMappingIdValue() {
		return inputVlanMappingIdValue;
	}

	public void setInputVlanMappingIdValue(String inputVlanMappingIdValue) {
		this.inputVlanMappingIdValue = inputVlanMappingIdValue;
	}

    private void prepareExpandPortGroup() {
        if(!getDataSource().getSortedPortProfiles().isEmpty()) {
            PortGroupProfile firstPort = ((TreeSet<PortGroupProfile>) getDataSource().getSortedPortProfiles()).first();
            if(null != firstPort) {
                expandPortTemplateId = firstPort.getId();
            }
        }
    }
    // ================= Color for Ports =========== :: start
    private String colortSets = "[]";
    private static final String SESSION_COLOR_INDICATOR_KEY = "SESSION_COLOR_INDICATOR_KEY";
    @SuppressWarnings("unchecked")
    private Map<Long, String> getColorsSession() {
        Object attr = MgrUtil.getSessionAttribute(SESSION_COLOR_INDICATOR_KEY);
        if(null != attr) {
            if(attr instanceof Map<?, ?>) {
                return (Map<Long, String>)attr;
            }
        }
        return new HashMap<>();
    }
    private void setColorsSession(Map<Long, String> colorMap) {
        MgrUtil.setSessionAttribute(SESSION_COLOR_INDICATOR_KEY, colorMap);
    }
    private void prepareColorSet() {
        // FIXME now only support 52 colors
        final Set<PortAccessProfile> accessProfiles = getDataSource().getAccessProfiles();
        if (null != accessProfiles) {
        	if(accessProfiles.isEmpty()) {
                setColortSets("[]");
                return;
            }
            Map<Long, String> colorMap = getColorsSession();
            
            int size = accessProfiles.size();
            String[] colors =  ColorUtil.CSS_STANDARD_COLOR;
            if(16 < size && size <= 24) {
                colors = ColorUtil.CSS_COLOR_24;
            } else {
                colors = ColorUtil.CSS_COLOR_52;
            }
            for (PortAccessProfile access : accessProfiles) {
                updateColorMap(colorMap, access.getId(), colors);
            }
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            boolean notFirstFlag = false;
            for (Long accessId : colorMap.keySet()) {
                if(notFirstFlag) {
                    builder.append(",");
                } else {
                    notFirstFlag = true;
                }
                builder.append("{'accessProfileId':"+accessId+", 'color': '"+colorMap.get(accessId)+"'}");
            }
            builder.append("]");
            setColortSets(builder.toString());
            
            setColorsSession(colorMap);
        }
        
    }
    private void updateColorMap(Map<Long, String> colorMap, Long key, String[] colors) {
        if(null == colorMap) {
            colorMap = new HashMap<>();
        }
        Collection<String> allColors = colorMap.values();
        int leftColorNum = colors.length - allColors.size();
        boolean noRepeat = true;
        if(null == colorMap.get(key)) {
            if(leftColorNum <= 0) {
                log.warn("updateColorMap", "No enough colors to indicate the port group, repeat color.");
                noRepeat = false;
            }
            for (int index = 0; index <= colors.length; index++) {
                if(index == colors.length) {
                    log.warn("updateColorMap", "No enough colors to indicate the port group, reset index.");
                    noRepeat = false;
                    index = 0;
                }
                if(noRepeat && allColors.contains(colors[index])) {
                    continue;
                }
                
                colorMap.put(key, colors[index]);
                return;
            }
        }
    }
    public String getColortSets() {
        return colortSets;
    }
    public void setColortSets(String colortSets) {
        this.colortSets = colortSets;
    }
    // ================= Color for Ports =========== :: end
    
	public int getTmpIndex() {
		return tmpIndex;
	}

	public void setTmpIndex(int tmpIndex) {
		this.tmpIndex = tmpIndex;
	}

	public Long getUpdatePortTempalteIds() {
		return updatePortTempalteIds;
	}

	public void setUpdatePortTempalteIds(Long updatePortTempalteIds) {
		this.updatePortTempalteIds = updatePortTempalteIds;
	}

	public Long getNetworkPolicyID() {
		return networkPolicyID;
	}

	public void setNetworkPolicyID(Long networkPolicyID) {
		this.networkPolicyID = networkPolicyID;
	}

	public static void removeItems(
			List<Long> configTemplateIds,Long domainId, QueryBo queryBo) throws Exception {

		List<PortGroupProfile> updatePortTemplateList = new ArrayList<PortGroupProfile>();
		List<PortGroupProfile> allPortTemplateList = QueryUtil.executeQuery(PortGroupProfile.class, new SortParams("id"), null,domainId,queryBo);
		for (PortGroupProfile template: allPortTemplateList){
			PortGroupProfile updateTemplate = template.clone();
			List<SingleTableItem> updatePortitemList = new ArrayList<SingleTableItem>();
			for (SingleTableItem item: template.getItems()){
				if(configTemplateIds.contains(item.getConfigTemplateId())){
				}else{
					updatePortitemList.add(item);
				}
			}
			updateTemplate.setItems(updatePortitemList);
			updatePortTemplateList.add(updateTemplate);
		}
		QueryUtil.bulkUpdateBos(updatePortTemplateList);
 
	}
	
	public static  void removeItems(
			List<Long> configTemplateIds,List<Long> portTemplateIds,Long domainId, QueryBo queryBo) throws Exception {

		List<PortGroupProfile> updatePortTemplateList = new ArrayList<PortGroupProfile>();
		List<PortGroupProfile> allPortTemplateList = QueryUtil.executeQuery(PortGroupProfile.class, new SortParams("id"), null,domainId,queryBo);
		for (PortGroupProfile template: allPortTemplateList){
				if(portTemplateIds.contains(template.getId())){
					
				}else{
					PortGroupProfile updateTemplate = template.clone();
					List<SingleTableItem> updatePortitemList = new ArrayList<SingleTableItem>();
					for (SingleTableItem item: updateTemplate.getItems()){
						if(configTemplateIds.contains(item.getConfigTemplateId())){
						}else{
							updatePortitemList.add(item);
						}
					}
					updateTemplate.setItems(updatePortitemList);

					updatePortTemplateList.add(updateTemplate);
				}
		}
		QueryUtil.bulkUpdateBos(updatePortTemplateList);

	}
	
	public List<EnumItem> getReportCollectionIntervalList() {
			List<EnumItem> lst = new ArrayList<EnumItem>(5);
			lst.add(new EnumItem(1, "1 minute"));
			lst.add(new EnumItem(5, "5 minutes"));
			lst.add(new EnumItem(10, "10 minutes"));
			lst.add(new EnumItem(30, "30 minutes"));
			lst.add(new EnumItem(60, "60 minutes"));
			return lst;
	}
//add to avoid duplicate name when showing waring message(bug )
	public static String cleanDuplicateTemplateName(List<String> nameList){
		String str = "";
		List<String> newName = new ArrayList<String>();
		if(nameList != null && nameList.size() > 0){
			for(String name : nameList){
				if(newName.contains(name)){
					continue ;
				}else{
					newName.add(name);
				}
			}
			for(String name : newName){
				str = str.concat(" " + name + ",");
			}
			return str ;
		}
		return str ;
	}

	public void updateSimpeleHiveApCache(Long configTemplateId,boolean enableDelayAlarm,Long domainId){
		try {
			List<HiveAp> apList = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("template_id", configTemplateId), domainId);
			for(HiveAp ap: apList){
				if(isFullMode() && !ap.isOverrideEnableDelayAlarm() && ap.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
					ap.setEnableDelayAlarm(enableDelayAlarm);
					
					QueryUtil.updateBo(ap);
					// propagate the updated HiveAP to Cache.
					CacheMgmt.getInstance().updateSimpleHiveAp(ap);
				}
			}
		} catch (Exception e) {
			log.error("updateSimpeleHiveApCache in network policy failed:"+e.getMessage());
			e.printStackTrace();
		}
	}

	public List<CheckItem> getList_cliBlob() {
		return list_cliBlob;
	}

	public void setList_cliBlob(List<CheckItem> list_cliBlob) {
		this.list_cliBlob = list_cliBlob;
	}

	public Long getSupplementalCLIId() {
		return supplementalCLIId;
	}

	public void setSupplementalCLIId(Long supplementalCLIId) {
		this.supplementalCLIId = supplementalCLIId;
	}
	
	public String getSupplementalCLIStyle(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",getDomain());
		if(null != bo && bo.isEnableSupplementalCLI()){
			return "";
		}
		return "none";
	}
}
