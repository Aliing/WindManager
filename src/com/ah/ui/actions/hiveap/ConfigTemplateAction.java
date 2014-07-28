package com.ah.ui.actions.hiveap;

/*
 * @author Fisher
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateQos;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnService;
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
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.VpnServiceAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CloneUtil;
import com.ah.util.CreateObjectAuto;
import com.ah.util.MgrUtil;
import com.ah.util.bo.BoGenerationUtil;

public class ConfigTemplateAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
//	private static final Tracer log = new Tracer(ConfigTemplateAction.class
//			.getSimpleName());
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_HIVE = 2;
	public static final int COLUMN_VLAN = 3;
	public static final int COLUMN_VLANNATIVE = 4;
	public static final int COLUMN_SSID = 5;
	public static final int COLUMN_DESCRIPTION = 6;
	public static final int COLUMN_DNS=7;
	public static final int COLUMN_NTP=8;
	public static final int COLUMN_SNMP=9;
	
	public static final int COLUMN_LOCATION=10;
	public static final int COLUMN_LOG=11;
	public static final int COLUMN_ALG=12;
	public static final int COLUMN_MANAGEMENT=13;
	public static final int COLUMN_IDS=14;
	public static final int COLUMN_CONSOLE=15;
	public static final int COLUMN_IP=16;
	public static final int COLUMN_SUPPLEMENTAL_CLI=17;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.configTemplate.configName";
			break;
		case COLUMN_HIVE:
			code = "config.configTemplate.hive";
			break;
		case COLUMN_VLAN:
			code = "config.configTemplate.vlan";
			break;
		case COLUMN_VLANNATIVE:
			code = "config.configTemplate.vlanNative";
			break;
		case COLUMN_SSID:
			code = "config.configTemplate.overview";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.configTemplate.description";
			break;
		case COLUMN_DNS:
			code = "config.configTemplate.mgtDns";
			break;
		case COLUMN_NTP:
			code = "config.configTemplate.mgtTime";
			break;	
		case COLUMN_SNMP:
			code = "config.configTemplate.mgtSnmp";
			break;
		case COLUMN_LOCATION:
			code = "config.configTemplate.locationServer";
			break;
		case COLUMN_LOG:
			code = "config.configTemplate.mgtSyslog";
			break;
		case COLUMN_ALG:
			code = "config.configTemplate.algConfig";
			break;
		case COLUMN_MANAGEMENT:
			code = "config.configTemplate.mgtOption";
			break;
		case COLUMN_IDS:
			code = "config.configTemplate.idsPolicy";
			break;
		case COLUMN_CONSOLE:
			code = "config.configTemplate.accessConsole";
			break;
		case COLUMN_IP:
			code = "config.configTemplate.ipFilter";
			break;
		case COLUMN_SUPPLEMENTAL_CLI:
			if (isFullMode() && getEnableSupplementalCLI()){
				code = "hollywood_02.supp_cli.configTemplateList.title";
			}else{
				code = null;
			}
			
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(16);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_HIVE));
		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_VLANNATIVE));
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_DNS));
		columns.add(new HmTableColumn(COLUMN_NTP));
		columns.add(new HmTableColumn(COLUMN_SNMP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_LOCATION));
		columns.add(new HmTableColumn(COLUMN_LOG));
		columns.add(new HmTableColumn(COLUMN_ALG));
		columns.add(new HmTableColumn(COLUMN_MANAGEMENT));
		columns.add(new HmTableColumn(COLUMN_IDS));
		columns.add(new HmTableColumn(COLUMN_CONSOLE));
		columns.add(new HmTableColumn(COLUMN_IP));
		
		if (isFullMode() && getEnableSupplementalCLI()){
			columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
		}
		
		return columns;
	}
	/**
	 * get default selected columns
	 */
	@Override
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(9);
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_HIVE));
		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_VLANNATIVE));
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_DNS));
		columns.add(new HmTableColumn(COLUMN_NTP));
		columns.add(new HmTableColumn(COLUMN_SNMP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.configTemplate"))) {
					return getLstForward();
				}
				setSessionDataSource(new ConfigTemplate(ConfigTemplateType.WIRELESS));
				/*
				ConfigTemplateSsid eth0 = new ConfigTemplateSsid();
				eth0.setInterfaceName(getText("config.configTemplate.eth0"));
				getDataSource().getSsidInterfaces().put((long) -1, eth0);
				ConfigTemplateSsid eth1 = new ConfigTemplateSsid();
				eth1.setInterfaceName(getText("config.configTemplate.eth1"));
				getDataSource().getSsidInterfaces().put((long) -2, eth1);
				ConfigTemplateSsid red0 = new ConfigTemplateSsid();
				red0.setInterfaceName(getText("config.configTemplate.red0"));
				getDataSource().getSsidInterfaces().put((long) -3, red0);
				ConfigTemplateSsid agg0 = new ConfigTemplateSsid();
				agg0.setInterfaceName(getText("config.configTemplate.agg0"));
				getDataSource().getSsidInterfaces().put((long) -4, agg0);
				
				ConfigTemplateSsid eth2 = new ConfigTemplateSsid();
				eth2.setInterfaceName(getText("config.configTemplate.eth2"));
				getDataSource().getSsidInterfaces().put((long) -5, eth2);
				ConfigTemplateSsid eth3 = new ConfigTemplateSsid();
				eth3.setInterfaceName(getText("config.configTemplate.eth3"));
				getDataSource().getSsidInterfaces().put((long) -6, eth3);
				ConfigTemplateSsid eth4 = new ConfigTemplateSsid();
				eth4.setInterfaceName(getText("config.configTemplate.eth4"));
				getDataSource().getSsidInterfaces().put((long) -7, eth4);
				*/
				getDataSource().setSsidInterfaces(BoGenerationUtil.genDefaultSsidInterfaces());

				prepareDependentSelectObjects();
				prepareInitSelectObjects();
				setTabId(0);
				return getReturnPathWithFullMode(INPUT, "configGuide2");
			} else if ("edit".equals(operation)){
//				|| "editFromSSID".equals(operation)) {
				String returnWord = editBo();
				if (dataSource != null) {
					return getReturnPathWithFullMode(returnWord, "configGuide2");
				} else {
					return returnWord;
				}
				/*if (dataSource != null) {
					if (getDataSource().getId() != null) {
						findBoById(ConfigTemplate.class, getDataSource().getId(), this);
					}
					setSessionDataSource(dataSource);
					prepareDependentSelectObjects();
					prepareInitSelectObjects();
					addLstTitle(getText("config.title.configTemplate.edit") + " '"
							+ getChangedConfigName() + "'");
//					if (resetConfigTemplateQos()) {
//						addActionMessage(getText("error.template.changeSSid"));
//					}
				}
//				if ("edit".equals(operation)){
					setTabId(0);
//				} else {
//					setTabId(4);
//				}
				return returnWord;*/
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				ConfigTemplate profile = findBoById(ConfigTemplate.class, cloneId,
						this);
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setId(null);
				profile.setDefaultFlag(false);
				profile.setConfigName("");
				
				//setCloneFields(profile, profile);
				CloneUtil.setConfigTemplateCloneFields(profile, profile);
				
//				Map<String, ConfigTemplateQos> cloneQosPolicies = new HashMap<String, ConfigTemplateQos>();
//				for (ConfigTemplateQos tempClass : profile.getQosPolicies().values()) {
//					cloneQosPolicies.put(tempClass.getKey(), tempClass);
//
//				}
//				profile.setQosPolicies(cloneQosPolicies);

				setSessionDataSource(profile);
				prepareDependentSelectObjects();
				prepareInitSelectObjects();
				addLstTitle(getText("config.title.configTemplate"));
//				resetConfigTemplateQos();
				setTabId(0);
				return INPUT;
			} else if ("update".equals(operation)) {
				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
				if (!checkVpnSize()){
					return INPUT;
				}
				if (!checkAllWhenUpdateOrCreate()) {
					return INPUT;
				}
				updateBo(dataSource);
				String retString =  prepareBoList();
				loadPageLazyData();
				return retString;
			} else if (("update"+ getLstForward()).equals(operation)) {
				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
				if (!checkVpnSize()){
					return INPUT;
				}
				if (!checkAllWhenUpdateOrCreate()) {
					return INPUT;
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("create".equals(operation)) {
				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
				if (dataSource.getId() == null) {
					if (checkNameExists("configName", getDataSource().getConfigName())) {
						prepareDependentSelectObjects();
						prepareInitSelectObjects();
						setTabId(0);
						return INPUT;
					}
				}
				if (!checkAllWhenUpdateOrCreate()) {
					return INPUT;
				}
				
				createBo(dataSource);
				String retString =  prepareBoList();
				loadPageLazyData();
				return retString;
			} else if (("create" + getLstForward()).equals(operation)) {
				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
				if (dataSource.getId() == null) {
					if (checkNameExists("configName", getDataSource().getConfigName())) {
						prepareDependentSelectObjects();
						prepareInitSelectObjects();
						setTabId(0);
						return INPUT;
					}
				}
				if (!checkAllWhenUpdateOrCreate()) {
					return INPUT;
				}
				
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					String retString =  prepareBoList();
					loadPageLazyData();
					return retString;
				}
			} else if ("applySsid".equals(operation)) {
				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
//				if (!saveRadioModel()) {
//					prepareDependentSelectObjects();
//					prepareInitSelectObjects();
//					setTabId(4);
//					return INPUT;
//				}
				if (!checkSameSsidAndSsidSize()) {
					prepareDependentSelectObjects();
					prepareInitSelectObjects();
					setTabId(0);
					return INPUT;
				}
				if (!checkExistSsid()){
					prepareDependentSelectObjects();
					prepareInitSelectObjects();
					setTabId(0);
					return INPUT;
				}

				setSelectedSsidProfiles();
				prepareDependentSelectObjects();
				prepareInitSelectObjects();
				return INPUT;
			} else if ("newHive".equals(operation) || "newMgtDns".equals(operation)
					|| "newMgtSnmp".equals(operation) || "newMgtSyslog".equals(operation)
					|| "newMgtTime".equals(operation) || "newMgtOption".equals(operation)
					|| "newVlan".equals(operation) || "newClientWatch".equals(operation)
					|| "newVlanNative".equals(operation) || "newIpFilter".equals(operation)
					|| "newAccessConsole".equals(operation) || "newIdsPolicy".equals(operation)
					|| "newSsidSelect".equals(operation)
					|| "newAlgConfig".equals(operation) || "newEthernetAccess".equals(operation)
					|| "newLocationServer".equals(operation) || "newEthernetBridge".equals(operation)
					|| "newClassification".equals(operation) || "editSsid".equals(operation)
					|| "newClassifierMap".equals(operation) || "newMarkerMap".equals(operation)
					|| "editSsidSelect".equals(operation) || "editHive".equals(operation)
					|| "editVlan".equals(operation) || "editVlanNative".equals(operation)
					|| "editMgtDns".equals(operation) || "editMgtSnmp".equals(operation)
					|| "editMgtSyslog".equals(operation) || "editMgtTime".equals(operation)
					|| "editMgtOption".equals(operation)  || "editClientWatch".equals(operation)
					|| "editAlgConfig".equals(operation) || "editLocationServer".equals(operation)
					|| "editIdsPolicy".equals(operation) || "editIpFilter".equals(operation)
					|| "editAccessConsole".equals(operation)
					|| "editEthernetAccess".equals(operation) || "editEthernetBridge".equals(operation)
					|| "editClassifierMap".equals(operation) || "editMarkerMap".equals(operation)
					|| "newVpnService".equals(operation) || "editVpnService".equals(operation)
					|| "newLldpCdp".equals(operation) || "editLldpCdp".equals(operation)
					|| "newIpTrack".equals(operation) || "editIpTrack".equals(operation)
					|| "newDeviceServiceFilter".equals(operation) || "editDeviceServiceFilter".equals(operation)
					|| "newEth0ServiceFilter".equals(operation) || "editEth0ServiceFilter".equals(operation)
					|| "newEth1ServiceFilter".equals(operation) || "editEth1ServiceFilter".equals(operation)
					|| "newRed0ServiceFilter".equals(operation) || "editRed0ServiceFilter".equals(operation)
					|| "newAgg0ServiceFilter".equals(operation) || "editAgg0ServiceFilter".equals(operation)
					|| "newBackEth0ServiceFilter".equals(operation) || "editBackEth0ServiceFilter".equals(operation)
					|| "newBackEth1ServiceFilter".equals(operation) || "editBackEth1ServiceFilter".equals(operation)
					|| "newBackRed0ServiceFilter".equals(operation) || "editBackRed0ServiceFilter".equals(operation)
					|| "newBackAgg0ServiceFilter".equals(operation) || "editBackAgg0ServiceFilter".equals(operation)
					|| "newBackWireServiceFilter".equals(operation) || "editBackWireServiceFilter".equals(operation)
					|| "newTvNetwork".equals(operation) || "editTvNetwork".equals(operation)) {

				prepareSaveSelectObjects();
				setSelectedIpTracks();
				setSelectedTVNetworkServices();
				updateQos();
//				saveRadioModel();
				if ("newVlanNative".equals(operation) || "editVlanNative".equals(operation)) {
					addLstForward("configTemplate2");
				} else {
					addLstForward("configTemplate");
				}
				if ("newClassification".equals(operation)) {
					MgrUtil.setSessionAttribute("interfaceKey", interfaceKey);
				}
				if ("newSsidSelect".equals(operation) || "editSsidSelect".equals(operation)){
					MgrUtil.setSessionAttribute("SHOWSSIDSECTION", true);
				} else {
					MgrUtil.removeSessionAttribute("SHOWSSIDSECTION");
				}
				if ("editSsid".equals(operation)){
					MgrUtil.setSessionAttribute("EDITSSIDPROFILEYET", true);
				} else {
					MgrUtil.removeSessionAttribute("EDITSSIDPROFILEYET");
				}
				setReturnTabId();
				addLstTabId(tabId);
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					String retString =  prepareBoList();
					loadPageLazyData();
					return retString;
				} else {
					if (MgrUtil.getSessionAttribute("EDITSSIDPROFILEYET") != null) {
						SsidProfile changeSsidProfile = findBoById(
								SsidProfile.class, ssidId, this);
						getDataSource().getSsidInterfaces().get(
								changeSsidProfile.getId()).setSsidProfile(
								changeSsidProfile);
						if (getDataSource().getId()!=null){
							ConfigTemplate tmp = (ConfigTemplate)findBoById(boClass, getDataSource().getId());
							getDataSource().setVersion(tmp.getVersion());
						}
//						if (resetConfigTemplateQos()) {
//							addActionMessage(getText("error.template.changeSSid"));
//						}
					}
					prepareSaveSelectObjects();
//					setQosSelectItem();
					prepareDependentSelectObjects();
					prepareInitSelectObjects();
					setId(dataSource.getId());
					setTabId(getLstTabId());
					if (MgrUtil.getSessionAttribute("SHOWSSIDSECTION")!=null) {
						hideCreateButton = "";
						hideNewButton = "none";
					}
					if (getUpdateContext()) {
						removeLstTitle();
						MgrUtil.setSessionAttribute("CURRENT_TABID", getTabId());
						removeLstForward();
						removeLstTabId();
						setUpdateContext(false);
					} else {
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute("CURRENT_TABID")
								.toString()));
					}
					return INPUT;
				}
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
			} else {
				baseOperation();
				//======================================
				if("paintbrush".equals(operation)){
					
					List<Long> configTemplateIds = new ArrayList<Long>();
					for(Long configTemplateId:selectedIds){
						configTemplateIds.add(configTemplateId);
					}
					
					ConfigTemplate config = findBoById(ConfigTemplate.class, selectedIds.get(0), this);
					List<Long> portTemplateIds = new ArrayList<Long>();
					for (PortGroupProfile template: config.getPortProfiles()){
						portTemplateIds.add(template.getId());
					}
					NetworkPolicyAction.removeItems(configTemplateIds,portTemplateIds,domainId,this);
					
				}
				if("remove".equals(operation)){
					NetworkPolicyAction.removeItems(selectedIds,domainId,this);
				}
				//======================================
				if(isSearchFlg() && null != id) {
				    filterParams = new FilterParams("id", id);
				}
				String retString =  prepareBoList();
				loadPageLazyData();
				return retString;
			}
		} catch (Exception e) {
			String retString =  prepareActionError(e);
			loadPageLazyData();
			return retString;
		}
	}
	public List<Long> configTemplateIds = new ArrayList<Long>();
	public List<Long> portTemplateIds = new ArrayList<Long>();
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CONFIGURATION_TEMPLATE);
		setDataSource(ConfigTemplate.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_WLAN_POLICY;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		
		//======================================
		for(Long configTemplateId:destinationIds){
			configTemplateIds.add(configTemplateId);
		}		
		//======================================
		ConfigTemplate source = QueryUtil.findBoById(ConfigTemplate.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<ConfigTemplate> list = QueryUtil.executeQuery(ConfigTemplate.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (ConfigTemplate profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			ConfigTemplate up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setConfigName(profile.getConfigName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			//======================================
			for (PortGroupProfile tempClass : up.getPortProfiles()) {
				List<SingleTableItem> lst=tempClass.getItems();
				List<SingleTableItem> newlst = new ArrayList<SingleTableItem>();
				for (SingleTableItem item : lst) {
					if(item.getConfigTemplateId() != profile.getId()){
						newlst.add(item);
					}
					if(paintbrushSource == item.getConfigTemplateId() ){
						SingleTableItem newitem =  item.clone();
						newitem.setConfigTemplateId(profile.getId());
						newlst.add(newitem);
					}
				}
				tempClass.setItems(newlst);
				portTemplateIds.add(tempClass.getId());
			}
			//======================================
			//setCloneFields(source, up);
			try {
				CloneUtil.setConfigTemplateCloneFields(source, up);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			hmBos.add(up);
		}
		return hmBos;
	}

	/*public void setCloneFields(ConfigTemplate source, ConfigTemplate destination){
		Map<Long, ConfigTemplateSsid> cloneSsidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
		for (ConfigTemplateSsid tempClass : source.getSsidInterfaces().values()) {
			if (tempClass.getSsidProfile() != null) {
				cloneSsidInterfaces.put(tempClass.getSsidProfile().getId(), tempClass);
			} else if (getText("config.configTemplate.eth0").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -1, tempClass);
			} else if (getText("config.configTemplate.eth1").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -2, tempClass);
			} else if (getText("config.configTemplate.red0").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -3, tempClass);
			} else {
				cloneSsidInterfaces.put((long) -4, tempClass);
			}
		}
		 destination.setSsidInterfaces(cloneSsidInterfaces);

		Set<MgmtServiceIPTrack> cloneIpTracks = new HashSet<MgmtServiceIPTrack>();
		for (MgmtServiceIPTrack tempClass : source.getIpTracks()) {
			cloneIpTracks.add(tempClass);
		}
		destination.setIpTracks(cloneIpTracks);
		 
		Set<NetworkService> cloneNetworkServices = new HashSet<NetworkService>();
		for (NetworkService tempClass : source.getTvNetworkService()) {
			cloneNetworkServices.add(tempClass);
		}
		destination.setTvNetworkService(cloneNetworkServices);
		
		Set<LanProfile> cloneLanProfiles = new HashSet<LanProfile>();
		for (LanProfile tempClass : source.getLanProfiles()) {
			cloneLanProfiles.add(tempClass);
		}
		destination.setLanProfiles(cloneLanProfiles);
	}*/

	public void loadPageLazyData(){
		if (page.isEmpty()){
			return;
		}
		Map<Long, ConfigTemplate> configTemplateMap = new HashMap<Long, ConfigTemplate>();
		String whereCon = "";
		int i=0;
		// Replace the 'LAZY' template with an empty one
		for (Object objectItem: page){
			ConfigTemplate oneConfig =(ConfigTemplate)objectItem;
			configTemplateMap.put(oneConfig.getId(), oneConfig);
			oneConfig.setHiveProfile(new HiveProfile());
			oneConfig.setVlan(new Vlan());
			oneConfig.setVlanNative(new Vlan());
			
			oneConfig.setMgmtServiceDns(new MgmtServiceDns());
			oneConfig.getMgmtServiceDns().setMgmtName("");
			oneConfig.setMgmtServiceTime(new MgmtServiceTime());
			oneConfig.getMgmtServiceTime().setMgmtName("");
			oneConfig.setMgmtServiceSnmp(new MgmtServiceSnmp());
			oneConfig.getMgmtServiceSnmp().setMgmtName("");
			oneConfig.setLocationServer(new LocationServer());
			oneConfig.getLocationServer().setName("");
			oneConfig.setMgmtServiceSyslog(new MgmtServiceSyslog());
			oneConfig.getMgmtServiceSyslog().setMgmtName("");
			oneConfig.setAlgConfiguration(new AlgConfiguration());
			oneConfig.getAlgConfiguration().setConfigName("");
			oneConfig.setAccessConsole(new AccessConsole());
			oneConfig.getAccessConsole().setConsoleName("");
			oneConfig.setMgmtServiceOption(new MgmtServiceOption());
			oneConfig.getMgmtServiceOption().setMgmtName("");
			oneConfig.setIdsPolicy(new IdsPolicy());
			oneConfig.getIdsPolicy().setPolicyName("");
			oneConfig.setIpFilter(new IpFilter());
			oneConfig.getIpFilter().setFilterName("");
			oneConfig.setSupplementalCLI(new CLIBlob());
			oneConfig.getSupplementalCLI().setSupplementalName("");
			
			whereCon = whereCon + oneConfig.getId();
			i++;
			if (page.size()!=i){
				whereCon = whereCon + ",";
			}
			
			oneConfig.setSsidInterfaces(new HashMap<Long,ConfigTemplateSsid>());
		}
		// Query for the template names only
		String strSql="select a.id,b.ssid_profile_id,b.interfacename," +
				" c.hivename,d.vlanname as vlan,e.vlanname," +
				" f.mgmtname as dns,g.mgmtname as ntp,h.mgmtname as snmp, i.name as location," +
				" j.mgmtname as log,k.configname as alg, l.mgmtname as mgnt,m.policyname as ids," +
//				" n.consolename as console, o.filtername as ipfilter, p.nm" +
				" n.consolename as console, o.filtername as ipfilter," +
				" p.supplementalName as supplementalCLI" + 
				" from config_template a " +
				" inner join config_template_ssid b on  b.config_template_id = a.id  " +
				" inner join hive_profile c on a.hive_profile_id = c.id  " +
				" inner join vlan d on a.vlan_id = d.id  " +
				" inner join vlan e on a.native_vlan_id = e.id " +
				" left join mgmt_service_dns f on a.mgmt_service_dns_id=f.id " +
				" left join mgmt_service_time g on a.mgmt_service_time_id=g.id " +
				" left join mgmt_service_snmp h on a.mgmt_service_snmp_id=h.id " +
				" left join location_server i on a.location_server_id=i.id " +
				" left join mgmt_service_syslog j on a.mgmt_service_syslog_id=j.id " +
				" left join alg_configuration k on a.alg_configuration_id=k.id " +
				" left join mgmt_service_option l on a.mgmt_service_option_id=l.id " +
				" left join ids_policy m on a.ids_policy_id=m.id " +
				" left join access_console n on a.access_console_id=n.id " +
				" left join ip_filter o on a.ip_filter_id=o.id " + 
				" left join cli_blob p on a.supplemental_cli_id=p.id" +
//				" left join (select distinct vv.vlanname as nm, nn.id as nnid from vlan vv,VPN_NETWORK nn " +
//				" where nn.VLAN_ID=vv.id) p on a.mgt_network_id=p.nnid " + 
				" where a.id in(" + whereCon + ")";
		
		List<?> templates = QueryUtil.executeNativeQuery(strSql);
		// Fill in the template names
		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());
			String interfaceName = (String) template[2];
			String hiveName = (String) template[3];
			String vlanName = (String) template[4];
			String nativevlanName = (String) template[5];
			String dns = (String) template[6];
			String ntp = (String) template[7];
			String snmp = (String) template[8];
			String location = (String) template[9];
			String log = (String) template[10];
			String alg = (String) template[11];
			String mgnt = (String) template[12];
			String ids = (String) template[13];
			String console = (String) template[14];
			String ipfilter  = (String) template[15];
//			String networkVlanName  = (String) template[16];
			String supplementalCLI = (String) template[16];

			ConfigTemplate templateSave = configTemplateMap.get(id);
			if (templateSave != null) {
				templateSave.getHiveProfile().setHiveName(hiveName);
//				if (templateSave.isBlnWirelessRouter()) {
//					templateSave.getVlan().setVlanName(networkVlanName);
//				} else {
					templateSave.getVlan().setVlanName(vlanName);
//				} 
				templateSave.getVlanNative().setVlanName(nativevlanName);
				templateSave.getMgmtServiceDns().setMgmtName(dns);
				templateSave.getMgmtServiceTime().setMgmtName(ntp);
				templateSave.getMgmtServiceSnmp().setMgmtName(snmp);
				templateSave.getLocationServer().setName(location);
				templateSave.getMgmtServiceSyslog().setMgmtName(log);
				templateSave.getAlgConfiguration().setConfigName(alg);
				templateSave.getAccessConsole().setConsoleName(console);
				templateSave.getMgmtServiceOption().setMgmtName(mgnt);
				templateSave.getIdsPolicy().setPolicyName(ids);
				templateSave.getIpFilter().setFilterName(ipfilter);
				templateSave.getSupplementalCLI().setSupplementalName(supplementalCLI);
				
				if (template[1]!=null && !template[1].toString().trim().equals("")){
					Long ssidId = Long.valueOf(template[1].toString());
					ConfigTemplateSsid tempClass = new ConfigTemplateSsid();
					tempClass.setSsidProfile(new SsidProfile());
					tempClass.setInterfaceName(interfaceName);
					tempClass.getSsidProfile().setId(ssidId);
					templateSave.getSsidInterfaces().put(ssidId, tempClass);
				}
			}
		}
	}

	@Override
	public ConfigTemplate getDataSource() {
		return (ConfigTemplate) dataSource;
	}

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
				for (ConfigTemplateSsid tmpTemplate : getDataSource()
						.getSsidInterfaces().values()) {
					if (tmpTemplate.getSsidProfile() != null) {
						if (tmpTemplate.getSsidProfile().getRadiusUserProfile() != null) {
							tmpTemplate.getSsidProfile().getRadiusUserProfile()
									.size();
						}
						if (null != tmpTemplate.getSsidProfile()
								.getLocalUserGroups()) {
							tmpTemplate.getSsidProfile().getLocalUserGroups()
									.size();
						}
						if (tmpTemplate.getSsidProfile().getUserProfileDefault() != null) {
							tmpTemplate.getSsidProfile().getUserProfileDefault().getId();
						}
						if (tmpTemplate.getSsidProfile().getUserProfileSelfReg() != null) {
							tmpTemplate.getSsidProfile().getUserProfileSelfReg().getId();
						}
					}
				}
			}

			getDataSource().getIpTracks().size();
			getDataSource().getTvNetworkService().size();
			// load Port Template profiles
			if (!getDataSource().getPortProfiles().isEmpty()) {
				getDataSource().getPortProfiles().size();
				
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
			
			if(null != this.getDataSource().getSupplementalCLI()){
				this.getDataSource().getSupplementalCLI().getId();
			}
		}

		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
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
			if (ssid.getRadiusUserProfile() != null)
				ssid.getRadiusUserProfile().size();
			if (null != ssid.getLocalUserGroups()) {
				ssid.getLocalUserGroups().size();
			}
		}
		if (bo instanceof EthernetAccess) {
			EthernetAccess ethe = (EthernetAccess) bo;
			if (ethe.getUserProfile() != null)
				ethe.getUserProfile().getId();
		}
		
		if(bo instanceof QosRateControl) {
			QosRateControl qosRate = (QosRateControl)bo;
			
			if(qosRate.getQosRateLimit() != null) {
				qosRate.getQosRateLimit().size();
			}
		}
		
		return null;
	}

	public void prepareDependentSelectObjects() throws Exception {
		// general page
		list_hive = getBoCheckItems("hiveName", HiveProfile.class, null);
		list_vlan = getBoCheckItems("vlanName", Vlan.class, null,CHECK_ITEM_BEGIN_BLANK,CHECK_ITEM_END_NO);
		list_mgtDns = getBoCheckItems("mgmtName", MgmtServiceDns.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtTime = getBoCheckItems("mgmtName", MgmtServiceTime.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		// option page left side
		list_locationServer = getBoCheckItems("name", LocationServer.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtSyslog = getBoCheckItems("mgmtName", MgmtServiceSyslog.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_mgtSnmp = getBoCheckItems("mgmtName", MgmtServiceSnmp.class, null);
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
		// option page right side
		list_algConfig = getBoCheckItems("configName", AlgConfiguration.class, null);
		list_mgtOption = getBoCheckItems("mgmtName", MgmtServiceOption.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
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
		// QOS page
		list_qosClassification = getBoCheckItems("qosName", QosClassfierAndMarker.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NEW);
		list_classifierMap = getBoCheckItems("classificationName", QosClassification.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		list_markerMap = getBoCheckItems("qosName", QosMarking.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);

		list_vpnService = getBoCheckItems("profileName", VpnService.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		
		list_ppskServer = new ArrayList<CheckItem>();
//		list_ppskServer = getBoCheckItems("hostName", HiveAp.class, 
//							new FilterParams("configTemplate",this.getDataSource()),
//							CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);

		prepareSsidProfiles();

		prepareVpnUserProfile();
		
		prepareIpTracks();
		
		prepareTVNetworkServices();
	}

	public void prepareVpnUserProfile() throws Exception {
		list_vpnUserProfile = new ArrayList<UserProfile>();
		Set<Long> addBeforeList = new HashSet<Long>();

		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()){
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					if (!addBeforeList.contains(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId())){
						list_vpnUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileDefault());
						addBeforeList.add(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					if (!addBeforeList.contains(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId())){
						list_vpnUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg());
						addBeforeList.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						if (!addBeforeList.contains(tempUser.getId())){
							list_vpnUserProfile.add(tempUser);
							addBeforeList.add(tempUser.getId());
						}
					}
				}
			}
		}
		
//		for (ConfigTemplateQos configTemplateQos : getDataSource().getQosPolicies().values()) {
//			if (addBeforeList.contains(configTemplateQos.getUserProfile().getId())){
//				continue;
//			}
//			list_vpnUserProfile.add(configTemplateQos.getUserProfile());
//			addBeforeList.add(configTemplateQos.getUserProfile().getId());
//		}
	}

	public void prepareSsidProfiles() throws Exception {
		List<CheckItem> availableSsidProfiles = getBoCheckItems("ssidName", SsidProfile.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableSsidProfiles) {
			for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
					.values()) {
				if (!configTemplateSsid.getInterfaceName().equals(
						getText("config.configTemplate.eth0"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.eth1"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.red0"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.agg0"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.eth2"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.eth3"))
						&& !configTemplateSsid.getInterfaceName().equals(
								getText("config.configTemplate.eth4"))) {
					if (configTemplateSsid.getSsidProfile().getSsidName()
							.equals(oneItem.getValue())) {
						removeList.add(oneItem);
					}
				}
			}
		}

		availableSsidProfiles.removeAll(removeList);

		// For the OptionsTransfer component
		ssidOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.configTemplate.availableSsidProfiles"), MgrUtil
				.getUserMessage("config.configTemplate.selectedSsidProfiles"),
				availableSsidProfiles, removeList, "id", "value", "ssidProfileIds",32,"250px",null,false, "SsidSelect");
	}

	public void prepareInitSelectObjects() {
		// general page
		if (getDataSource().getHiveProfile() != null) {
			hiveId = getDataSource().getHiveProfile().getId();
		}
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
		
		if (getDataSource().getMgmtServiceSnmp() != null) {
			mgtSnmpId = getDataSource().getMgmtServiceSnmp().getId();
		}
		if (getDataSource().getMgmtServiceSyslog() != null) {
			mgtSyslogId = getDataSource().getMgmtServiceSyslog().getId();
		}
		if (getDataSource().getClientWatch() != null) {
			clientWatchId = getDataSource().getClientWatch().getId();
		}

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
		// Ethernet Access
//		if (getDataSource().getEthernetAccess() != null) {
//			ethernetAccessId = getDataSource().getEthernetAccess().getId();
//		}
//		if (getDataSource().getEthernetAccessEth1() != null) {
//			ethernetAccessEth1Id = getDataSource().getEthernetAccessEth1().getId();
//		}
//		if (getDataSource().getEthernetAccessRed() != null) {
//			ethernetAccessRedId = getDataSource().getEthernetAccessRed().getId();
//		}
//		if (getDataSource().getEthernetAccessAgg() != null) {
//			ethernetAccessAggId = getDataSource().getEthernetAccessAgg().getId();
//		}
		// Ethernet Bridge
//		if (getDataSource().getEthernetBridge() != null) {
//			ethernetBridgeId = getDataSource().getEthernetBridge().getId();
//		}
//		if (getDataSource().getEthernetBridgeEth1() != null) {
//			ethernetBridgeEth1Id = getDataSource().getEthernetBridgeEth1().getId();
//		}
//		if (getDataSource().getEthernetBridgeRed() != null) {
//			ethernetBridgeRedId = getDataSource().getEthernetBridgeRed().getId();
//		}
//		if (getDataSource().getEthernetBridgeAgg() != null) {
//			ethernetBridgeAggId = getDataSource().getEthernetBridgeAgg().getId();
//		}
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
		if (getDataSource().getVpnService()!=null) {
			vpnServiceId = getDataSource().getVpnService().getId();
		}
		
		initQosRateLimit();
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

	public void setReturnTabId() {
		if ("newHive".equals(operation) || "editHive".equals(operation)
				|| "newVlan".equals(operation) || "editVlan".equals(operation)
				|| "newVlanNative".equals(operation) || "editVlanNative".equals(operation)
				|| "newSsidSelect".equals(operation) || "editSsid".equals(operation)
				|| "editSsidSelect".equals(operation)){
			setTabId(0);
		}
		if ("newDeviceServiceFilter".equals(operation) || "editDeviceServiceFilter".equals(operation)
				|| "newEthernetAccess".equals(operation) || "editEthernetAccess".equals(operation)
				|| "newEthernetBridge".equals(operation) || "editEthernetBridge".equals(operation)
				|| "newEth0ServiceFilter".equals(operation) || "editEth0ServiceFilter".equals(operation)
				|| "newEth1ServiceFilter".equals(operation) || "editEth1ServiceFilter".equals(operation)
				|| "newRed0ServiceFilter".equals(operation) || "editRed0ServiceFilter".equals(operation)
				|| "newAgg0ServiceFilter".equals(operation) || "editAgg0ServiceFilter".equals(operation)
				|| "newBackEth0ServiceFilter".equals(operation) || "editBackEth0ServiceFilter".equals(operation)
				|| "newBackEth1ServiceFilter".equals(operation) || "editBackEth1ServiceFilter".equals(operation)
				|| "newBackRed0ServiceFilter".equals(operation) || "editBackRed0ServiceFilter".equals(operation)
				|| "newBackAgg0ServiceFilter".equals(operation) || "editBackAgg0ServiceFilter".equals(operation)
				|| "newBackWireServiceFilter".equals(operation) || "editBackWireServiceFilter".equals(operation)){
			setTabId(1);
		}

		if ("newAlgConfig".equals(operation) || "editAlgConfig".equals(operation)
				|| "newAccessConsole".equals(operation) || "editAccessConsole".equals(operation)
				|| "newIdsPolicy".equals(operation) || "editIdsPolicy".equals(operation)
				|| "newIpFilter".equals(operation) || "editIpFilter".equals(operation)
				|| "newMgtOption".equals(operation) || "editMgtOption".equals(operation)
				|| "newLldpCdp".equals(operation) || "editLldpCdp".equals(operation)
				|| "newIpTrack".equals(operation) || "editIpTrack".equals(operation)){
			setTabId(2);
		}
		if ("newMgtDns".equals(operation) || "editMgtDns".equals(operation)
				|| "newMgtSnmp".equals(operation) || "editMgtSnmp".equals(operation)
				|| "newMgtTime".equals(operation) || "editMgtTime".equals(operation)
				|| "newMgtSyslog".equals(operation) || "editMgtSyslog".equals(operation)
				|| "newLocationServer".equals(operation) || "editLocationServer".equals(operation)
				|| "newClientWatch".equals(operation) || "editClientWatch".equals(operation)
				){
			setTabId(3);
		}
		if ("newClassification".equals(operation)
				|| "newClassifierMap".equals(operation) || "editClassifierMap".equals(operation)
				|| "newMarkerMap".equals(operation) || "editMarkerMap".equals(operation)){
			setTabId(4);
		}

		if ("newVpnService".equals(operation) || "editVpnService".equals(operation)){
			setTabId(5);
		}
		
		if ("newTvNetwork".equals(operation) || "editTvNetwork".equals(operation)){
			setTabId(7);
		}
	}

//	public void setQosSelectItem() throws Exception {
//		if (MgrUtil.getSessionAttribute("interfaceKey") != null) {
//			setInterfaceKey(Long.valueOf(MgrUtil.getSessionAttribute("interfaceKey").toString()));
//
//			if (qosClassId != null) {
//				QosClassfierAndMarker tmpClass = (QosClassfierAndMarker) findBoById(
//						QosClassfierAndMarker.class, qosClassId);
//				if (getDataSource().getSsidInterfaces().get(interfaceKey) != null) {
//					getDataSource().getSsidInterfaces().get(interfaceKey).setClassfierAndMarker(
//							tmpClass);
//				}
//			}
//			MgrUtil.removeSessionAttribute("interfaceKey");
//		}
//	}

	public void prepareSaveSelectObjects() throws Exception {
		// general page
		if (hiveId != null) {
			HiveProfile tmpClass = findBoById(HiveProfile.class, hiveId);
			getDataSource().setHiveProfile(tmpClass);
		}
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
		if (clientWatchId != null) {
			LocationClientWatch tmpClass = findBoById(LocationClientWatch.class,
					clientWatchId);
			getDataSource().setClientWatch(tmpClass);
		}

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

		// optional page access service
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
		// Ethernet asscee
//		if (ethernetAccessId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetAccessId);
//			getDataSource().setEthernetAccess(tmpClass);
//		}
//		if (ethernetAccessEth1Id != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetAccessEth1Id);
//			getDataSource().setEthernetAccessEth1(tmpClass);
//		}
//		if (ethernetAccessRedId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetAccessRedId);
//			getDataSource().setEthernetAccessRed(tmpClass);
//		}
//		if (ethernetAccessAggId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetAccessAggId);
//			getDataSource().setEthernetAccessAgg(tmpClass);
//		}

		// Ethernet Bridge
//		if (ethernetBridgeId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetBridgeId);
//			getDataSource().setEthernetBridge(tmpClass);
//		}
//		if (ethernetBridgeEth1Id != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetBridgeEth1Id);
//			getDataSource().setEthernetBridgeEth1(tmpClass);
//		}
//		if (ethernetBridgeRedId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetBridgeRedId);
//			getDataSource().setEthernetBridgeRed(tmpClass);
//		}
//		if (ethernetBridgeAggId != null) {
//			EthernetAccess tmpClass = (EthernetAccess) findBoById(EthernetAccess.class,
//					ethernetBridgeAggId);
//			getDataSource().setEthernetBridgeAgg(tmpClass);
//		}
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

		// VPN Service settings
		if (vpnServiceId != null) {
			VpnService tmpClass = findBoById(VpnService.class,vpnServiceId);
			getDataSource().setVpnService(tmpClass);
		}
	}

//	protected boolean checkSsidSize() {
//		if (getDataSource().getSsidInterfaces().size() < 5) {
//			addActionError(getText("error.template.addssid"));
//			return false;
//		}
//
//		return true;
//	}
	public void prepareIpTracks() {
		List<CheckItem> availableFilters = getBoCheckItems("trackName", MgmtServiceIPTrack.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (MgmtServiceIPTrack ServiceIPTrack : getDataSource().getIpTracks()) {
				if (ServiceIPTrack.getTrackName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);

		ipTrackOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("hiveAp.ipTrack.availableIpTrack"), MgrUtil
				.getUserMessage("hiveAp.ipTrack.selectedIpTrack"), availableFilters,
				getDataSource().getIpTracks(), "id", "value", "ipTrackIds",  0,
				"250px", "6", true, "IpTrack");
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

	private boolean checkTotalPmkUserSize() {
		long count = getTotalPmkUserSize(getDataSource());
		if (count > LocalUser.MAX_COUNT_AP30_USERPERAP) {
			addActionError(getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERPERAP)}));
			return false;
		}
		
		long totalCount = getTotalPSKUserSize(getDataSource());
		if (totalCount > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
			addActionError(getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP)}));
			return false;
		}
		
		return true;
	}

	private boolean checkTotalPskGroupSize() {
		long totalUserCount = getTotalPskGroupId(getDataSource()).size();
		if (totalUserCount > 512) {
			addActionError(getText("error.template.morePskGroupPerTemplate"));
			return false;
		}
		return true;
	}

	public static Set<Long> getTotalPskGroupId(ConfigTemplate template) {
		Set<Long> setPskGroupId = new HashSet<Long>();
		if (null != template && template.getSsidInterfaces().size() > 0) {
			for (ConfigTemplateSsid configSsid : template.getSsidInterfaces()
					.values()) {
				if (configSsid.getSsidProfile()!=null) {
					for(LocalUserGroup userGroup:configSsid.getSsidProfile().getLocalUserGroups()){
						if (null != userGroup) {
							setPskGroupId.add(userGroup.getId());
						}
					}
				}
			}
		}
		return setPskGroupId;
	}

	public static Set<Long> getTotalPskGroupId(Long templateId){
		ConfigTemplate template = QueryUtil.findBoById(ConfigTemplate.class, templateId, new ConfigTemplateAction());
		return getTotalPskGroupId(template);
	}

	public static long getTotalPmkUserSize(ConfigTemplate template) {
		long totalUserCount = 0;
		if (null != template && template.getSsidInterfaces().size() > 0) {
			for (ConfigTemplateSsid configSsid : template.getSsidInterfaces()
					.values()) {
				Set<Long> setPskGroupId = new HashSet<Long>();
				if (configSsid.getSsidProfile()!=null) {
					for(LocalUserGroup userGroup:configSsid.getSsidProfile().getLocalUserGroups()){
						if (null != userGroup) {
							setPskGroupId.add(userGroup.getId());
						}
					}
				}
				if (!setPskGroupId.isEmpty()){
					totalUserCount = totalUserCount + QueryUtil.findRowCount(LocalUser.class,
							new FilterParams("localUserGroup.id in(:s1) and revoked=:s2",
									new Object[]{setPskGroupId,false}));
				}
			}
		}
		return totalUserCount;
	}
	
	public static long getTotalPSKUserSize(ConfigTemplate template) {
		long totalUserCount = 0;
		if (null != template && template.getSsidInterfaces().size() > 0) {
			Set<Long> setPskGroupId = new HashSet<Long>();
			for (ConfigTemplateSsid configSsid : template.getSsidInterfaces()
					.values()) {
				if (configSsid.getSsidProfile()!=null) {
					for(LocalUserGroup userGroup:configSsid.getSsidProfile().getLocalUserGroups()){
						if (null != userGroup) {
							setPskGroupId.add(userGroup.getId());
						}
					}
				}
			}
			if (!setPskGroupId.isEmpty()){
				totalUserCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams("localUserGroup.id in(:s1) and revoked=:s2",
								new Object[]{setPskGroupId,false}));
			}
		}
		return totalUserCount;
	}
	
	private static int getMaxUserCountPerGroup(ConfigTemplate template){
		long maxUserCount = 0;
		if (null != template && template.getSsidInterfaces().size() > 0) {
			Set<Long> setPskGroupId = new HashSet<Long>();
			for (ConfigTemplateSsid configSsid : template.getSsidInterfaces()
					.values()) {
				if (configSsid.getSsidProfile()!=null) {
					for(LocalUserGroup userGroup:configSsid.getSsidProfile().getLocalUserGroups()){
						if (null != userGroup) {
							setPskGroupId.add(userGroup.getId());
						}
					}
				}
			}
			if (!setPskGroupId.isEmpty()) {
				List<?> listRet = QueryUtil
						.executeQuery(
								"select count(*) from "
										+ LocalUser.class.getSimpleName(),
								null,
								new FilterParams(
										"localUserGroup.id in(:s1) and revoked=:s2",
										new Object[] { setPskGroupId, false }),
								new GroupByParams(
										new String[] { "localUserGroup.id" }),
								null);
				if (listRet != null && listRet.size() > 0) {
					for (Object count : listRet) {
						if (null != count) {
							if ((Long) count > maxUserCount) {
								maxUserCount = (Long) count;
							}
						}
					}
				}
			}
		}
		return (int) maxUserCount;
	}
	
	public static int[] getNumberOfGroupAndUser(Long templateId){
		ConfigTemplate template = QueryUtil.findBoById(ConfigTemplate.class, templateId, new ConfigTemplateAction());
		int[] result = new int[3];
		result[0] = (int)getTotalPSKUserSize(template);
		result[1] = (int)getTotalPmkUserSize(template);
		result[2] = getMaxUserCountPerGroup(template);
		return result;
	}

	public boolean checkCacAirTime() {
		int cacPercent = 0;
		if (getDataSource().getMgmtServiceOption() != null) {
			if (!getDataSource().getMgmtServiceOption().getDisableCallAdmissionControl()){
				cacPercent = cacPercent
					+ getDataSource().getMgmtServiceOption().getRoamingGuaranteedAirtime();
			}
		} else {
			cacPercent = cacPercent + 20;
		}
		Set<Long> setUserProfile = new HashSet<Long>();
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId())) {
						cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileDefault().getGuarantedAirTime();
						setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId())) {
						cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getGuarantedAirTime();
						setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						if (!setUserProfile.contains(tempUser.getId())) {
							cacPercent = cacPercent + tempUser.getGuarantedAirTime();
							setUserProfile.add(tempUser.getId());
						}
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						setUserProfile.add(rule.getUserProfile().getId());
//						UserProfile tempUser = rule.getUserProfile();
//						if (!setUserProfile.contains(tempUser.getId())) {
//							cacPercent = cacPercent + tempUser.getGuarantedAirTime();
//							setUserProfile.add(tempUser.getId());
//						}
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!setUserProfile.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : setUserProfile) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!setUserProfile.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							upObj = QueryUtil.findBoById(UserProfile.class, rule.getUserProfileId());
							cacPercent = cacPercent + upObj.getGuarantedAirTime();
							newSetUserProfile.add(rule.getUserProfileId());
						}
						
					}
				}
			}
		}
		
//		for (ConfigTemplateQos qosPolicies : getDataSource().getQosPolicies().values()) {
//			if (setUserProfile.contains(qosPolicies.getUserProfile().getId())) {
//				continue;
//			}
//			cacPercent = cacPercent + qosPolicies.getUserProfile().getGuarantedAirTime();
//			setUserProfile.add(qosPolicies.getUserProfile().getId());
//		}

//		if (getDataSource().getEthernetAccess() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccess().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetAccessAgg() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessAgg().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetAccessEth1() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessEth1().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetAccessRed() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessRed().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetBridge() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridge().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetBridgeAgg() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeAgg().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetBridgeEth1() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeEth1().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}
//		if (getDataSource().getEthernetBridgeRed() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeRed().getId(), this);
//			if (tmpEthernet.getUserProfile() != null
//					&& !setUserProfile.contains(tmpEthernet.getUserProfile().getId())) {
//				cacPercent = cacPercent + tmpEthernet.getUserProfile().getGuarantedAirTime();
//				setUserProfile.add(tmpEthernet.getUserProfile().getId());
//			}
//		}

		if (cacPercent > 100) {
			addActionError(getText("error.template.guaranteedAirTime"));
			return false;
		}
		return true;
	}

//	public boolean checkSameUserProfileRateValue() {
//		for (ConfigTemplateQos temp : getDataSource().getQosPolicies().values()) {
//			for (ConfigTemplateQos temp2 : getDataSource().getQosPolicies().values()) {
//				long userproid = temp.getUserProfile().getId();
//				long userproid2 = temp2.getUserProfile().getId();
//				UserProfile usertemp = QueryUtil.findBoById(UserProfile.class,
//						userproid, this);
//				UserProfile usertemp2 = QueryUtil.findBoById(UserProfile.class,
//						userproid2, this);
//
//				if (userproid == userproid2 && temp.getRadioMode() != temp2.getRadioMode()) {
//					if (temp.getPolicingRate() != temp2.getPolicingRate()
//							|| temp.getPolicingRate11n() != temp2.getPolicingRate11n()
//							|| temp.getSchedulingWeight() != temp2.getSchedulingWeight()) {
//						String tempStr[] = { usertemp.getUserProfileName() };
//						addActionError(getText("error.template.sameUserProfile.checkPolicingRate",
//								tempStr));
//						return false;
//					}
//				}
//				if (usertemp.getQosRateControl().getId().toString().equals(
//						usertemp2.getQosRateControl().getId().toString())) {
//					if (temp.getPolicingRate() != temp2.getPolicingRate()
//							|| temp.getPolicingRate11n() != temp2.getPolicingRate11n()
//							|| temp.getSchedulingWeight() != temp2.getSchedulingWeight()) {
//						String tempStr[] = { usertemp.getUserProfileName(),
//								usertemp2.getUserProfileName() };
//						addActionError(getText(
//								"error.template.sameQosRateControl.checkPolicingRate", tempStr));
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}

	public boolean checkAllWhenUpdateOrCreate() throws Exception {
//		if (!checkSsidSize()) {
//			prepareDependentSelectObjects();
//			prepareInitSelectObjects();
//			setTabId(0);
//			return false;
//		}
//		if (!saveRadioModel()) {
//			prepareDependentSelectObjects();
//			prepareInitSelectObjects();
//			setTabId(4);
//			return false;
//		}
//		if (!checkSameUserProfileRateValue()) {
//			prepareDependentSelectObjects();
//			prepareInitSelectObjects();
//			setTabId(4);
//			return false;
//		}
		if (!checkExistSsid()){
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}
		
		if (!checkRadioModeSize()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}

		if (!checkIpPolicyAndMacPolicySize()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}

		if (!checkUserProfileAttribute()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}
		if (!checkCacAirTime()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}

		if (!checkTotalPskGroupSize()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}

		if (!checkTotalPmkUserSize()) {
			prepareDependentSelectObjects();
			prepareInitSelectObjects();
			setTabId(0);
			return false;
		}
		return true;
	}

	protected boolean checkUserProfileAttribute() {
		Set<String> setUsedUserProfile = new HashSet<String>();
		Set<String> setUsedAttrValue = new HashSet<String>();
		Set<String> userProfileCount = new HashSet<String>();
		Set<Long> userProIds = new HashSet<Long>();
		Set<Long> qosIds = new HashSet<Long>();
		
		// check bind userProfile attribute value in wlan mapping
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileDefault()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						userProIds.add(tempUser.getId());
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						userProIds.add(rule.getUserProfile().getId());
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!userProIds.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : userProIds) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!userProIds.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							newSetUserProfile.add(rule.getUserProfileId());
						}
					}
				}
			}
			if (!newSetUserProfile.isEmpty()) {
				userProIds.addAll(newSetUserProfile);
			}
		}
		
		for (Long ids : userProIds) {
			UserProfile forAttrUserProfile = QueryUtil.findBoById(
					UserProfile.class, ids, this);
			if (forAttrUserProfile.getQosRateControl()!=null) {
				qosIds.add(forAttrUserProfile.getQosRateControl().getId());
			}
			
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& setUsedAttrValue.contains(String.valueOf(forAttrUserProfile
							.getAttributeValue()))) {
				addActionError(getText("error.template.sameAttribute"));
				return false;
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
									addActionError(getText("error.template.sameAttribute"));
									return false;
								}
							}
						} else {
							if (setUsedAttrValue.contains(attrRange[0])) {
								addActionError(getText("error.template.sameAttribute"));
								return false;
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
							}
						} else {
							setUsedAttrValue.add(attrRange[0]);
						}
					}
				}
			}
			setUsedUserProfile.add(forAttrUserProfile.getId().toString());
			setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
			if (userProfileAttr != null) {
				userProfileCount.add(userProfileAttr.getId().toString());
			}
		}

		// check Ethernet access userProfile attribute
//		if (getDataSource().getEthernetAccess() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccess().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.eth0") + " "
//						+ getText("config.configTemplate.ethernetAccess"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetBridge() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridge().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.eth0") + " "
//						+ getText("config.configTemplate.ethernetBridge"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetAccessEth1() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessEth1().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.eth1") + " "
//						+ getText("config.configTemplate.ethernetAccess"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetBridgeEth1() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeEth1().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.eth1") + " "
//						+ getText("config.configTemplate.ethernetBridge"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetAccessRed() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessRed().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.red0") + " "
//						+ getText("config.configTemplate.ethernetAccess"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetBridgeRed() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeRed().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.red0") + " "
//						+ getText("config.configTemplate.ethernetBridge"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetAccessAgg() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetAccessAgg().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.agg0") + " "
//						+ getText("config.configTemplate.ethernetAccess"), userProfileCount)) {
//					return false;
//				}
//			}
//		}
//		if (getDataSource().getEthernetBridgeAgg() != null) {
//			EthernetAccess tmpEthernet = QueryUtil.findBoById(
//					EthernetAccess.class, getDataSource().getEthernetBridgeAgg().getId(), this);
//			if (tmpEthernet.getUserProfile() != null) {
//				if (!ethernetAttrCheck(setUsedUserProfile, setUsedAttrValue, tmpEthernet
//						.getUserProfile().getId(), getText("config.configTemplate.agg0") + " "
//						+ getText("config.configTemplate.ethernetBridge"), userProfileCount)) {
//					return false;
//				}
//			}
//		}

		if (userProfileCount.size() > 64) {
			addActionError(getText("error.template.moreUserProfileAttributeGroup"));
			return false;
		}
		
		if (qosIds.size()>16) {
			addActionError(getText("error.template.moreUserProfileQos"));
			return false;
		}
		return true;
	}

	protected boolean ethernetAttrCheck(Set<String> setUsedUserProfile,
			Set<String> setUsedAttrValue, Long userProfileId, String msg,
			Set<String> userProfileCount) {
		UserProfile forAttrUserProfile = QueryUtil.findBoById(UserProfile.class,
				userProfileId, this);

		if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
				&& setUsedAttrValue
						.contains(String.valueOf(forAttrUserProfile.getAttributeValue()))) {
			String tempStr[] = { msg };
			addActionError(getText("error.template.sameAttributeValue", tempStr));
			return false;
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
								String tempStr[] = {msg};
								addActionError(getText("error.template.sameAttributeValue", tempStr));
								return false;
							}
						}
					} else {
						if (setUsedAttrValue.contains(attrRange[0])) {
							String tempStr[] = {msg};
							addActionError(getText("error.template.sameAttributeValue", tempStr));
							return false;
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
						}
					} else {
						setUsedAttrValue.add(attrRange[0]);
					}
				}
			}
		}
		setUsedUserProfile.add(forAttrUserProfile.getId().toString());
		setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
		if (userProfileAttr != null) {
			userProfileCount.add(userProfileAttr.getId().toString());
		}
		return true;
	}

	public boolean checkIpPolicyAndMacPolicySize() {
		Set<String> ipPolicyName = new HashSet<String>();
		Set<String> macPolicyName = new HashSet<String>();
		Set<Long> userProIds = new HashSet<Long>();
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileDefault()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						userProIds.add(tempUser.getId());
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						userProIds.add(rule.getUserProfile().getId());
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!userProIds.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : userProIds) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!userProIds.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							newSetUserProfile.add(rule.getUserProfileId());
						}
					}
				}
			}
			if (!newSetUserProfile.isEmpty()) {
				userProIds.addAll(newSetUserProfile);
			}
		}
		
		for (Long ids : userProIds) {
			UserProfile tempUserProfile = QueryUtil.findBoById(
					UserProfile.class, ids, this);

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
			addActionError(getText("error.template.moreIPPolicy"));
			return false;
		}

		if (macPolicyName.size() > 32) {
			// macPolicyName size must less than 32
			addActionError(getText("error.template.moreMACPolicy"));
			return false;
		}
		return true;
	}

	public boolean checkRadioModeSize() {
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
			addActionError(getText("error.assignSsid.range", tempStr));
			return false;
		}

		if (bmodelConnt > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeBG") };
			addActionError(getText("error.assignSsid.range", tempStr));
			return false;
		}
		return true;
	}

	public boolean checkSameSsidAndSsidSize() {
		if (ssidProfileIds == null || ssidProfileIds.size() < 1) {
			//addActionError(getText("error.template.addssid"));
			return true;
		} else if (ssidProfileIds.size() > 32) {
			String tempStr[] = { "32", "one "+NmsUtil.getOEMCustomer().getAccessPonitName() };
			addActionError(getText("error.assignSsid.range", tempStr));
			return false;
		}
		return true;
	}

	public boolean checkVpnSize()throws Exception{
		if (getDataSource().getVpnService()!=null){
			String msg = VpnServiceAction.validateIpPoolCapability(
					getDataSource().getId(),getDataSource().getVpnService().getId());
			if (!msg.equals("")){
				prepareDependentSelectObjects();
				prepareInitSelectObjects();
				setTabId(5);
				addActionError(msg);
				return false;
			}
		}
		return true;
	}
	
	public boolean checkExistSsid() {
		Set<String> ssidSets = new HashSet<String>();
		if (ssidProfileIds == null) {
			return true;
		}
		for (Long filterId : ssidProfileIds) {
			SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class,
					filterId);
			if (ssidSets.contains(ssidProfile.getSsid())) {
				addActionError(getText("error.template.existSsid"));
				return false;
			} else {
				ssidSets.add(ssidProfile.getSsid());
			}
		}
		return true;
	}
	
//	protected void updateQos() throws Exception {
//		if (dataSource == null) {
//			return;
//		}
//
//		if (qosClassifications != null
//				&& qosClassifications.length == getDataSource().getSsidInterfaces().size()) {
//
//			int qosIndex = 0;
//			boolean addCheckEth0 = false;
//			boolean addCheckEth1 = false;
//			boolean addCheckRed0 = false;
//			boolean addCheckAgg0 = false;
//
//			for (ConfigTemplateSsid configTemplateSSID : getDataSource().getSsidInterfaces()
//					.values()) {
//				if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.eth0"))) {
//					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
//							.findBoById(QosClassfierAndMarker.class, qosClassifications[0]);
//					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					if (!addCheckEth0) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.eth1"))) {
//					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
//							.findBoById(QosClassfierAndMarker.class, qosClassifications[1]);
//					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					if (!addCheckEth1) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.red0"))) {
//					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
//							.findBoById(QosClassfierAndMarker.class, qosClassifications[2]);
//					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					if (!addCheckRed0) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.agg0"))) {
//					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
//							.findBoById(QosClassfierAndMarker.class, qosClassifications[3]);
//					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					if (!addCheckAgg0) {
//						qosIndex++;
//					}
//				} else if (qosClassifications[qosIndex] != -2) {
//					if (qosIndex == 0) {
//						qosIndex++;
//						addCheckEth0 = true;
//					}
//					if (qosIndex == 1) {
//						qosIndex++;
//						addCheckEth1 = true;
//					}
//					if (qosIndex == 2) {
//						qosIndex++;
//						addCheckRed0 = true;
//					}
//					if (qosIndex == 3) {
//						qosIndex++;
//						addCheckAgg0 = true;
//					}
//					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
//							.findBoById(QosClassfierAndMarker.class, qosClassifications[qosIndex]);
//					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					qosIndex++;
//				} else {
//					qosIndex++;
//				}
//			}
//		}
//	}

	protected void updateQos() throws Exception {
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
		
		

//		if (arrayNetwork != null
//				&& arrayNetwork.length == getDataSource().getSsidInterfaces().size()) {
//
//			int qosIndex = 0;
//			ei
//			
//			boolean addCheckEth0 = false;
//			boolean addCheckEth1 = false;
//			boolean addCheckRed0 = false;
//			boolean addCheckAgg0 = false;


			
//				boolean arrayNetworkBln = false;
//				if (arrayNetwork != null && ei < enabled.length) {
//					try {
//						int enabledIndex = Integer.parseInt(enabled[ei]);
//						if (i == enabledIndex) {
//							enableRow = true;
//							ei++;
//						}
//					} catch (NumberFormatException e) {
//						// Bug in struts, it should not set false in this array if
//						// no row is enabled
//						enabled = null;
//					}
//				}
//				
//				
//				if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.eth0"))) {
////					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
////							.findBoById(QosClassfierAndMarker.class, qosClassifications[0]);
////					configTemplateSSID.setClassfierAndMarker(qosClassification);
//					
//					configTemplateSSID.setNetworkServicesEnabled(arrayNetwork[0]);
//					configTemplateSSID.setMacOuisEnabled(arrayMacOui[0]);
//					configTemplateSSID.setSsidEnabled(arraySsid[0]);
//					configTemplateSSID.setCheckE(arrayCheckE[0]);
//					configTemplateSSID.setCheckD(arrayCheckD[0]);
//					configTemplateSSID.setCheckP(arrayCheckP[0]);
//					configTemplateSSID.setCheckET(arrayCheckET[0]);
//					configTemplateSSID.setCheckDT(arrayCheckDT[0]);
//					configTemplateSSID.setCheckPT(arrayCheckPT[0]);
//					if (!addCheckEth0) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.eth1"))) {
////					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
////							.findBoById(QosClassfierAndMarker.class, qosClassifications[1]);
////					configTemplateSSID.setClassfierAndMarker(qosClassification);
////					configTemplateSSID.setNetworkServicesEnabled(arrayNetwork[1]);
////					configTemplateSSID.setMacOuisEnabled(arrayMacOui[1]);
////					configTemplateSSID.setSsidEnabled(arraySsid[1]);
////					configTemplateSSID.setCheckE(arrayCheckE[1]);
////					configTemplateSSID.setCheckD(arrayCheckD[1]);
////					configTemplateSSID.setCheckP(arrayCheckP[1]);
////					configTemplateSSID.setCheckET(arrayCheckET[1]);
////					configTemplateSSID.setCheckDT(arrayCheckDT[1]);
////					configTemplateSSID.setCheckPT(arrayCheckPT[1]);
//					if (!addCheckEth1) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.red0"))) {
////					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
////							.findBoById(QosClassfierAndMarker.class, qosClassifications[2]);
////					configTemplateSSID.setClassfierAndMarker(qosClassification);
////					configTemplateSSID.setNetworkServicesEnabled(arrayNetwork[2]);
////					configTemplateSSID.setMacOuisEnabled(arrayMacOui[2]);
////					configTemplateSSID.setSsidEnabled(arraySsid[2]);
////					configTemplateSSID.setCheckE(arrayCheckE[2]);
////					configTemplateSSID.setCheckD(arrayCheckD[2]);
////					configTemplateSSID.setCheckP(arrayCheckP[2]);
////					configTemplateSSID.setCheckET(arrayCheckET[2]);
////					configTemplateSSID.setCheckDT(arrayCheckDT[2]);
////					configTemplateSSID.setCheckPT(arrayCheckPT[2]);
//					if (!addCheckRed0) {
//						qosIndex++;
//					}
//				} else if (configTemplateSSID.getInterfaceName().equals(
//						getText("config.configTemplate.agg0"))) {
////					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
////							.findBoById(QosClassfierAndMarker.class, qosClassifications[3]);
////					configTemplateSSID.setClassfierAndMarker(qosClassification);
////					configTemplateSSID.setNetworkServicesEnabled(arrayNetwork[3]);
////					configTemplateSSID.setMacOuisEnabled(arrayMacOui[3]);
////					configTemplateSSID.setSsidEnabled(arraySsid[3]);
////					configTemplateSSID.setCheckE(arrayCheckE[3]);
////					configTemplateSSID.setCheckD(arrayCheckD[3]);
////					configTemplateSSID.setCheckP(arrayCheckP[3]);
////					configTemplateSSID.setCheckET(arrayCheckET[3]);
////					configTemplateSSID.setCheckDT(arrayCheckDT[3]);
////					configTemplateSSID.setCheckPT(arrayCheckPT[3]);
//					if (!addCheckAgg0) {
//						qosIndex++;
//					}
//				} else if (qosClassifications[qosIndex] != -2) {
//					if (qosIndex == 0) {
//						qosIndex++;
//						addCheckEth0 = true;
//					}
//					if (qosIndex == 1) {
//						qosIndex++;
//						addCheckEth1 = true;
//					}
//					if (qosIndex == 2) {
//						qosIndex++;
//						addCheckRed0 = true;
//					}
//					if (qosIndex == 3) {
//						qosIndex++;
//						addCheckAgg0 = true;
//					}
////					QosClassfierAndMarker qosClassification = (QosClassfierAndMarker) QueryUtil
////							.findBoById(QosClassfierAndMarker.class, qosClassifications[qosIndex]);
////					configTemplateSSID.setClassfierAndMarker(qosClassification);
////					configTemplateSSID.setNetworkServicesEnabled(arrayNetwork[qosIndex]);
////					configTemplateSSID.setMacOuisEnabled(arrayMacOui[qosIndex]);
////					configTemplateSSID.setSsidEnabled(arraySsid[qosIndex]);
////					configTemplateSSID.setCheckE(arrayCheckE[qosIndex]);
////					configTemplateSSID.setCheckD(arrayCheckD[qosIndex]);
////					configTemplateSSID.setCheckP(arrayCheckP[qosIndex]);
////					configTemplateSSID.setCheckET(arrayCheckET[qosIndex]);
////					configTemplateSSID.setCheckDT(arrayCheckDT[qosIndex]);
////					configTemplateSSID.setCheckPT(arrayCheckPT[qosIndex]);
//					qosIndex++;
//				} else {
//					qosIndex++;
//				}
//			}
//		}
//	}

//	public boolean saveRadioModel() {
//		int amodeCount = 0;
//		int bgmodeCount = 0;
//		int sizeCount = 0;
//
//		if (amodelRate != null) {
//			sizeCount = sizeCount + amodelRate.length;
//		}
//
//		if (bgmodelRate != null) {
//			sizeCount = sizeCount + bgmodelRate.length;
//		}
//
//		if (sizeCount != getDataSource().getQosPolicies().size()) {
//			return false;
//		}
//		for (ConfigTemplateQos configTemplateQos : getDataSource().getQosPolicies().values()) {
//			long userproid = configTemplateQos.getUserProfile().getId();
//			UserProfile usertemp = QueryUtil.findBoById(UserProfile.class, userproid,
//					this);
//
//			if (configTemplateQos.getRadioMode() == SsidProfile.RADIOMODE_A) {
//				int qosRate;
//				int qosRate11n;
//				if (usertemp.getQosRateControl() != null) {
//					qosRate = usertemp.getQosRateControl().getRateLimit();
//					qosRate11n = usertemp.getQosRateControl().getRateLimit11n();
//				} else {
//					qosRate = 1;
//					qosRate11n = 1;
//				}
//				if (amodelRate[amodeCount] < qosRate) {
//					String tempStr[] = { String.valueOf(amodelRate[amodeCount]),
//							"802.11a", 
//							usertemp.getQosRateControl().getQosName(),
//							usertemp.getUserProfileName()};
//					addActionError(getText("error.template.policingRate", tempStr));
//				}
//				if (amodelRate11n[amodeCount] < qosRate11n) {
//					String tempStr[] = { String.valueOf(amodelRate11n[amodeCount]),
//							"802.11na", 
//							usertemp.getQosRateControl().getQosName(),
//							usertemp.getUserProfileName()};
//					addActionError(getText("error.template.policingRate", tempStr));
//				}
//				configTemplateQos.setPolicingRate(amodelRate[amodeCount]);
//				configTemplateQos.setPolicingRate11n(amodelRate11n[amodeCount]);
//				configTemplateQos.setSchedulingWeight(amodelWeight[amodeCount]);
//				configTemplateQos.setWeightPercent(amodelWeightPercent[amodeCount]);
//				amodeCount++;
//			}
//			if (configTemplateQos.getRadioMode() == SsidProfile.RADIOMODE_BG) {
//				int qosRate;
//				int qosRate11n;
//				if (usertemp.getQosRateControl() != null) {
//					qosRate = usertemp.getQosRateControl().getRateLimit();
//					qosRate11n = usertemp.getQosRateControl().getRateLimit11n();
//				} else {
//					qosRate = 1;
//					qosRate11n = 1;
//				}
//				if (bgmodelRate[bgmodeCount] < qosRate) {
//					String tempStr[] = { String.valueOf(bgmodelRate[bgmodeCount]),
//							"802.11b/g", 
//							usertemp.getQosRateControl().getQosName(),
//							usertemp.getUserProfileName()};
//					addActionError(getText("error.template.policingRate", tempStr));
//				}
//				if (bgmodelRate11n[bgmodeCount] < qosRate11n) {
//					String tempStr[] = { String.valueOf(bgmodelRate11n[bgmodeCount]),
//							"802.11ng", 
//							usertemp.getQosRateControl().getQosName(),
//							usertemp.getUserProfileName()};
//					addActionError(getText("error.template.policingRate", tempStr));
//				}
//				configTemplateQos.setPolicingRate(bgmodelRate[bgmodeCount]);
//				configTemplateQos.setPolicingRate11n(bgmodelRate11n[bgmodeCount]);
//				configTemplateQos.setSchedulingWeight(bgmodelWeight[bgmodeCount]);
//				configTemplateQos.setWeightPercent(bgmodelWeightPercent[bgmodeCount]);
//				bgmodeCount++;
//			}
//		}
//
//		return !hasErrors();
//	}

	protected void setSelectedSsidProfiles() throws Exception {
		Set<Long> removeItems = new HashSet<Long>();
		Set<Long> oldSsidProfile = new HashSet<Long>();
		for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces().values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				oldSsidProfile.add(configTemplateSsid.getSsidProfile().getId());
			}
		}

		if (ssidProfileIds != null) {
			for (ConfigTemplateSsid configTemplateSsid : getDataSource().getSsidInterfaces()
					.values()) {
				if (configTemplateSsid.getSsidProfile() != null) {
					for (Long filterId : ssidProfileIds) {
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
				// update QOS userProfile
			}
		}
//		removeConfigTemplateQosWhenModifySSID(oldSsidProfile);

		if (ssidProfileIds!=null){
			for (Long filterId : ssidProfileIds) {
				if (removeItems.contains(filterId)) {
					continue;
				}
				SsidProfile ssidProfile = findBoById(SsidProfile.class, filterId, this);
				if (ssidProfile != null) {
					ConfigTemplateSsid templateSsid = new ConfigTemplateSsid();
					templateSsid.setSsidProfile(ssidProfile);
					templateSsid.setInterfaceName(ssidProfile.getSsidName());
					getDataSource().getSsidInterfaces().put(ssidProfile.getId(), templateSsid);
					// update QOS userProfile
//					setupConfigTemplateQosWhenModifySSID(ssidProfile);
				}
			}
		}
	}

//	protected void removeConfigTemplateQosWhenModifySSID(Set<Long> removeSsidIds) throws Exception {
//		Set<String> userProifleInSsid = new HashSet<String>();
//		Set<String> removeProifleInSsid = new HashSet<String>();
//		for (ConfigTemplateSsid templateSsid : getDataSource().getSsidInterfaces().values()) {
//			if (templateSsid.getSsidProfile() == null) {
//				continue;
//			}
//			if (templateSsid.getSsidProfile().getUserProfileDefault() != null) {
//				if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileDefault()
//							.getId()
//							+ "|" + SsidProfile.RADIOMODE_A);
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileDefault()
//							.getId()
//							+ "|" + SsidProfile.RADIOMODE_BG);
//				} else {
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileDefault()
//							.getId()
//							+ "|" + templateSsid.getSsidProfile().getRadioMode());
//				}
//			}
//			if (templateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
//				if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//							.getId()
//							+ "|" + SsidProfile.RADIOMODE_A);
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//							.getId()
//							+ "|" + SsidProfile.RADIOMODE_BG);
//				} else {
//					userProifleInSsid.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//							.getId()
//							+ "|" + templateSsid.getSsidProfile().getRadioMode());
//				}
//			}
//			if (templateSsid.getSsidProfile().getRadiusUserProfile() != null) {
//				for (UserProfile uspr : templateSsid.getSsidProfile().getRadiusUserProfile()) {
//					if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//						userProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_A);
//						userProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_BG);
//					} else {
//						userProifleInSsid.add(uspr.getId() + "|"
//								+ templateSsid.getSsidProfile().getRadioMode());
//					}
//				}
//			}
//		}
//
//		for (Long filterId : removeSsidIds) {
//			SsidProfile ssidProfile = (SsidProfile) findBoById(SsidProfile.class, filterId, this);
//			if (ssidProfile.getUserProfileDefault() != null) {
//				if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//					removeProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//							+ SsidProfile.RADIOMODE_A);
//					removeProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//							+ SsidProfile.RADIOMODE_BG);
//				} else {
//					removeProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//							+ ssidProfile.getRadioMode());
//				}
//			}
//			if (ssidProfile.getUserProfileSelfReg() != null) {
//				if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//					removeProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//							+ SsidProfile.RADIOMODE_A);
//					removeProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//							+ SsidProfile.RADIOMODE_BG);
//				} else {
//					removeProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//							+ ssidProfile.getRadioMode());
//				}
//			}
//			if (ssidProfile.getRadiusUserProfile() != null) {
//				for (UserProfile uspr : ssidProfile.getRadiusUserProfile()) {
//					if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//						removeProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_A);
//						removeProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_BG);
//					} else {
//						removeProifleInSsid.add(uspr.getId() + "|" + ssidProfile.getRadioMode());
//					}
//				}
//			}
//		}
//
//		removeProifleInSsid.removeAll(userProifleInSsid);
//		for (String unusedId : removeProifleInSsid) {
//			getDataSource().getQosPolicies().remove(unusedId);
//		}
//	}

//	protected boolean resetConfigTemplateQos() throws Exception {
//		boolean ret = false;
//		Set<String> newUserProfile = new HashSet<String>();
//		for (ConfigTemplateSsid templateSsid : getDataSource().getSsidInterfaces().values()) {
//			if (templateSsid.getSsidProfile() != null) {
//				if (templateSsid.getSsidProfile().getUserProfileDefault() != null) {
//					if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//								.getId()
//								+ "|" + SsidProfile.RADIOMODE_A);
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//								.getId()
//								+ "|" + SsidProfile.RADIOMODE_BG);
//					} else {
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//								.getId()
//								+ "|" + templateSsid.getSsidProfile().getRadioMode());
//					}
//				}
//
//				if (templateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
//					if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//								.getId()
//								+ "|" + SsidProfile.RADIOMODE_A);
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//								.getId()
//								+ "|" + SsidProfile.RADIOMODE_BG);
//					} else {
//						newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//								.getId()
//								+ "|" + templateSsid.getSsidProfile().getRadioMode());
//					}
//				}
//				if (templateSsid.getSsidProfile().getRadiusUserProfile() != null) {
//					for (UserProfile uspr : templateSsid.getSsidProfile().getRadiusUserProfile()) {
//						if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//							newUserProfile.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_A);
//							newUserProfile.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_BG);
//						} else {
//							newUserProfile.add(uspr.getId() + "|"
//									+ templateSsid.getSsidProfile().getRadioMode());
//						}
//					}
//				}
//			}
//		}
//		Set<String> removeKey = new HashSet<String>();
//		for (ConfigTemplateQos templateQos : getDataSource().getQosPolicies().values()) {
//			if (newUserProfile.contains(templateQos.getKey())) {
//				newUserProfile.remove(templateQos.getKey());
//			} else {
//				removeKey.add(templateQos.getKey());
//			}
//		}
//		if (removeKey.size() > 0) {
//			ret = true;
//			for (String key : removeKey) {
//				getDataSource().getQosPolicies().remove(key);
//			}
//		}
//
//		if (newUserProfile.size() > 0) {
//			ret = true;
//			for (String unusedId : newUserProfile) {
//				String[] IdMode = unusedId.split("\\|");
//				UserProfile userProfile = (UserProfile) findBoById(UserProfile.class, Long
//						.valueOf(IdMode[0]));
//				if (userProfile != null) {
//					ConfigTemplateQos templateQos = new ConfigTemplateQos();
//					templateQos.setUserProfile(userProfile);
//					templateQos.setRadioMode(Integer.valueOf(IdMode[1]));
//					getDataSource().getQosPolicies().put(templateQos.getKey(), templateQos);
//				}
//			}
//		}
//
//		return ret;
//
//	}

//	protected void setupConfigTemplateQosWhenModifySSID(SsidProfile ssidProfile) throws Exception {
//		Set<String> userProifleInSsid = new HashSet<String>();
//		if (ssidProfile.getUserProfileDefault() != null) {
//			if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//				userProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//						+ SsidProfile.RADIOMODE_A);
//				userProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//						+ SsidProfile.RADIOMODE_BG);
//			} else {
//				userProifleInSsid.add(ssidProfile.getUserProfileDefault().getId() + "|"
//						+ ssidProfile.getRadioMode());
//			}
//		}
//		if (ssidProfile.getUserProfileSelfReg() != null) {
//			if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//				userProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//						+ SsidProfile.RADIOMODE_A);
//				userProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//						+ SsidProfile.RADIOMODE_BG);
//			} else {
//				userProifleInSsid.add(ssidProfile.getUserProfileSelfReg().getId() + "|"
//						+ ssidProfile.getRadioMode());
//			}
//		}
//		if (ssidProfile.getRadiusUserProfile() != null) {
//			for (UserProfile uspr : ssidProfile.getRadiusUserProfile()) {
//				if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//					userProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_A);
//					userProifleInSsid.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_BG);
//				} else {
//					userProifleInSsid.add(uspr.getId() + "|" + ssidProfile.getRadioMode());
//				}
//			}
//		}
//		for (ConfigTemplateQos templateQos : getDataSource().getQosPolicies().values()) {
//			if (userProifleInSsid.contains(templateQos.getKey())) {
//				userProifleInSsid.remove(templateQos.getKey());
//			}
//		}
//		for (String unusedId : userProifleInSsid) {
//			String[] IdMode = unusedId.split("\\|");
//			UserProfile userProfile = (UserProfile) findBoById(UserProfile.class, Long
//					.valueOf(IdMode[0]));
//			if (userProfile != null) {
//				ConfigTemplateQos templateQos = new ConfigTemplateQos();
//				templateQos.setUserProfile(userProfile);
//				templateQos.setRadioMode(Integer.valueOf(IdMode[1]));
//				getDataSource().getQosPolicies().put(templateQos.getKey(), templateQos);
//			}
//		}
//	}

	public int getGridCount() {
		return getDataSource().getSsidInterfaces().size() < 8 ? 3 : 0;
	}

	public int getConfigNameLength() {
		return getAttributeLength("configName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
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
	
	public Range getSlaIntervalRange() {
		return super.getAttributeRange("slaInterval");
	}

	// general page
	private Long ssidId;
	private Long hiveId;
	private Long vlanId;
	private String inputVlanIdValue;
	private String inputVlanNativeIdValue;
	private Long vlanNativeId;
	private Long mgtDnsId;
	private Long mgtTimeId;

	// optional page left fieldset
	private Long locationServerId;
	private Long lldpCdpId;
	private Long mgtSnmpId;
	private Long mgtSyslogId;
	private Long clientWatchId;

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

	private Long vpnServiceId;

	// list
	private List<CheckItem> list_hive;
	private List<CheckItem> list_vlan;
	private List<CheckItem> list_mgtDns;
	private List<CheckItem> list_mgtTime;
	private List<CheckItem> list_mgtSyslog;
	private List<CheckItem> list_locationServer;
	private List<CheckItem> list_lldpCdp;
	private List<CheckItem> list_mgtSnmp;
	private List<CheckItem> list_algConfig;
	private List<CheckItem> list_mgtOption;
	private List<CheckItem> list_clientWatch;
	private List<CheckItem> list_idsPolicy;
	private List<CheckItem> list_accessConsole;
	private List<CheckItem> list_ipFilter;
	private List<CheckItem> list_radius;
	private List<CheckItem> list_service;
	private List<CheckItem> list_qosClassification;
	private List<CheckItem> list_classifierMap;
	private List<CheckItem> list_markerMap;
	private List<CheckItem> list_ethernetAccess;
	private List<CheckItem> list_vpnService;
	private List<UserProfile> list_vpnUserProfile;
	private List<CheckItem> list_ppskServer;
	
	private final List<ConfigTemplateQos> listQosRateLimit = new ArrayList<ConfigTemplateQos>();

	// QOS rate setting
	private Long qosClassId;
	private Long classifierMapId;
	private Long markerMapId;
//	protected int[] amodelRate;
//	protected int[] amodelRate11n;
//	protected int[] bgmodelRate;
//	protected int[] bgmodelRate11n;
//	protected int[] amodelWeight;
//	protected int[] bgmodelWeight;
//	protected float[] amodelWeightPercent;
//	protected float[] bgmodelWeightPercent;
	private Long[] qosClassifications;
	
	// Remove Qos and mark , add check box
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
	
	// for qos new back confirm which interface should set
	private Long interfaceKey;

	OptionsTransfer ssidOptions;
	private List<Long> ssidProfileIds;
	
	OptionsTransfer ipTrackOptions;
	private List<Long> ipTrackIds;
	private Long ipTrackId;
	
	OptionsTransfer tvNetworkOptions;
	private List<Long> tvNetworkIds;
	private Long tvNetworkId;

	private String hideCreateButton = "none";
	private String hideNewButton = "";

	public String getHideCreateButton() {
		return hideCreateButton;
	}

	public String getHideNewButton() {
		return hideNewButton;
	}

	public String getShowNetworkSettingsDiv() {
		if (getTabId() == 1) {
			return "none";
		}
		return "";
	}

	public String getHideNetworkSettingsDiv() {
		if (getTabId() == 1) {
			return "";
		}
		return "none";
	}

	public String getShowServiceSettingsDiv() {
		if (getTabId() == 2) {
			return "none";
		}
		return "";
	}

	public String getHideServiceSettingsDiv() {
		if (getTabId() == 2) {
			return "";
		}
		return "none";
	}

	public String getShowServerSettingsDiv() {
		if (getTabId() == 3) {
			return "none";
		}
		return "";
	}

	public String getHideServerSettingsDiv() {
		if (getTabId() == 3) {
			return "";
		}
		return "none";
	}

	public String getShowQosSettingsDiv() {
		if (getTabId() == 4) {
			return "none";
		}
		return "";
	}

	public String getHideQosSettingsDiv() {
		if (getTabId() == 4) {
			return "";
		}
		return "none";
	}
	
	public String getHideOverrideMapPanel(){
		if (getDataSource().getEnabledMapOverride()){
			return "";
		}
		return "none";
	}

	public String getShowVpnSettingsDiv() {
		if (getTabId() == 5) {
			return "none";
		}
		return "";
	}

	public String getHideVpnSettingsDiv() {
		if (getTabId() == 5) {
			return "";
		}
		return "none";
	}
	
	public String getShowReportSettingsDiv() {
		if (getTabId() == 6) {
			return "none";
		}
		return "";
	}
	
	public String getHideReportSettingsDiv() {
		if (getTabId() == 6) {
			return "";
		}
		return "none";
	}
	
	
	public String getShowTVSettingsDiv() {
		if (!isTeacherViewEnabled()) {
			return "none";
		}
		if (getTabId() == 7) {
			return "none";
		}
		return "";
	}
	
	public String getHideTVSettingsDiv() {
		if (!isTeacherViewEnabled()) {
			return "none";
		}
		if (getTabId() == 7) {
			return "";
		}
		return "none";
	}
	
	public String getShowPPSKRegDiv() {
		if (getTabId() == 8) {
			return "none";
		}
		return "";
	}
	
	public String getHidePPSKRegDiv() {
		if (getTabId() == 8) {
			return "";
		}
		return "none";
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
//		return "none";
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

	public Long getHiveId() {
		return hiveId;
	}

	public void setHiveId(Long hiveId) {
		this.hiveId = hiveId;
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

	public Long getEth0ServiceId() {
		return eth0ServiceId;
	}

	public void setEth0ServiceId(Long eth0ServiceId) {
		this.eth0ServiceId = eth0ServiceId;
	}

	public Long getDeviceServiceId() {
		return deviceServiceId;
	}

	public void setDeviceServiceId(Long deviceServiceId) {
		this.deviceServiceId = deviceServiceId;
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

	public Long getQosClassId() {
		return qosClassId;
	}

	public void setQosClassId(Long qosClassId) {
		this.qosClassId = qosClassId;
	}

	public Long getClassifierMapId() {
		return classifierMapId;
	}

	public void setClassifierMapId(Long classifierMapId) {
		this.classifierMapId = classifierMapId;
	}

	public Long getMarkerMapId() {
		return markerMapId;
	}

	public void setMarkerMapId(Long markerMapId) {
		this.markerMapId = markerMapId;
	}

//	public int[] getAmodelRate() {
//		return amodelRate;
//	}
//
//	public void setAmodelRate(int[] amodelRate) {
//		this.amodelRate = amodelRate;
//	}
//
//	public int[] getAmodelRate11n() {
//		return amodelRate11n;
//	}
//
//	public void setAmodelRate11n(int[] amodelRate11n) {
//		this.amodelRate11n = amodelRate11n;
//	}
//
//	public int[] getBgmodelRate() {
//		return bgmodelRate;
//	}
//
//	public void setBgmodelRate(int[] bgmodelRate) {
//		this.bgmodelRate = bgmodelRate;
//	}
//
//	public int[] getBgmodelRate11n() {
//		return bgmodelRate11n;
//	}
//
//	public void setBgmodelRate11n(int[] bgmodelRate11n) {
//		this.bgmodelRate11n = bgmodelRate11n;
//	}
//
//	public int[] getAmodelWeight() {
//		return amodelWeight;
//	}
//
//	public void setAmodelWeight(int[] amodelWeight) {
//		this.amodelWeight = amodelWeight;
//	}
//
//	public int[] getBgmodelWeight() {
//		return bgmodelWeight;
//	}
//
//	public void setBgmodelWeight(int[] bgmodelWeight) {
//		this.bgmodelWeight = bgmodelWeight;
//	}
//
//	public float[] getAmodelWeightPercent() {
//		return amodelWeightPercent;
//	}
//
//	public void setAmodelWeightPercent(float[] amodelWeightPercent) {
//		this.amodelWeightPercent = amodelWeightPercent;
//	}
//
//	public float[] getBgmodelWeightPercent() {
//		return bgmodelWeightPercent;
//	}
//
//	public void setBgmodelWeightPercent(float[] bgmodelWeightPercent) {
//		this.bgmodelWeightPercent = bgmodelWeightPercent;
//	}

	public Long[] getQosClassifications() {
		return qosClassifications;
	}

	public void setQosClassifications(Long[] qosClassifications) {
		this.qosClassifications = qosClassifications;
	}

	public Long getInterfaceKey() {
		return interfaceKey;
	}

	public void setInterfaceKey(Long interfaceKey) {
		this.interfaceKey = interfaceKey;
	}

	public List<CheckItem> getList_hive() {
		return list_hive;
	}

	public List<CheckItem> getList_vlan() {
		return list_vlan;
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

	public List<CheckItem> getList_mgtSnmp() {
		return list_mgtSnmp;
	}

	public List<CheckItem> getList_algConfig() {
		return list_algConfig;
	}

	public List<CheckItem> getList_mgtOption() {
		return list_mgtOption;
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

	public List<CheckItem> getList_qosClassification() {
		return list_qosClassification;
	}

	public List<CheckItem> getList_classifierMap() {
		return list_classifierMap;
	}

	public List<CheckItem> getList_markerMap() {
		return list_markerMap;
	}

	public List<CheckItem> getList_ethernetAccess() {
		return list_ethernetAccess;
	}
	
	public List<CheckItem> getList_ppskServer() {
		return list_ppskServer;
	}

	public OptionsTransfer getSsidOptions() {
		return ssidOptions;
	}

	public void setSsidOptions(OptionsTransfer ssidOptions) {
		this.ssidOptions = ssidOptions;
	}

	public List<Long> getSsidProfileIds() {
		return ssidProfileIds;
	}

	public void setSsidProfileIds(List<Long> ssidProfileIds) {
		this.ssidProfileIds = ssidProfileIds;
	}

	public Long getSsidId() {
		return ssidId;
	}

	public void setSsidId(Long ssidId) {
		this.ssidId = ssidId;
	}

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

	public List<CheckItem> getList_vpnService() {
		return list_vpnService;
	}

	public List<UserProfile> getList_vpnUserProfile() {
		return list_vpnUserProfile;
	}

	public Long getVpnServiceId() {
		return vpnServiceId;
	}

	public void setVpnServiceId(Long vpnServiceId) {
		this.vpnServiceId = vpnServiceId;
	}

	public Long getClientWatchId() {
		return clientWatchId;
	}

	public void setClientWatchId(Long clientWatchId) {
		this.clientWatchId = clientWatchId;
	}

	public List<CheckItem> getList_clientWatch() {
		return list_clientWatch;
	}
	
//	private JSONObject jsonObject = null;
//
//	public String getJSONString() {
//		return jsonObject.toString();
//	}
	public String getChangeLocationServerOperation(){
		return "changeLocationServer";
	}

	public List<ConfigTemplateQos> getListQosRateLimit() {
		return listQosRateLimit;
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

	public List<CheckItem> getList_lldpCdp() {
		return list_lldpCdp;
	}

	public Long getLldpCdpId() {
		return lldpCdpId;
	}

	public void setLldpCdpId(Long lldpCdpId) {
		this.lldpCdpId = lldpCdpId;
	}

	public OptionsTransfer getIpTrackOptions() {
		return ipTrackOptions;
	}

	public void setIpTrackOptions(OptionsTransfer ipTrackOptions) {
		this.ipTrackOptions = ipTrackOptions;
	}

	public List<Long> getIpTrackIds() {
		return ipTrackIds;
	}

	public void setIpTrackIds(List<Long> ipTrackIds) {
		this.ipTrackIds = ipTrackIds;
	}

	public Long getIpTrackId() {
		return ipTrackId;
	}

	public void setIpTrackId(Long ipTrackId) {
		this.ipTrackId = ipTrackId;
	}

	public String[] getArraySsidOnly() {
		return arraySsidOnly;
	}

	public void setArraySsidOnly(String[] arraySsidOnly) {
		this.arraySsidOnly = arraySsidOnly;
	}

	/**
	 * @return the tvNetworkOptions
	 */
	public OptionsTransfer getTvNetworkOptions() {
		return tvNetworkOptions;
	}

	/**
	 * @param tvNetworkOptions the tvNetworkOptions to set
	 */
	public void setTvNetworkOptions(OptionsTransfer tvNetworkOptions) {
		this.tvNetworkOptions = tvNetworkOptions;
	}

	/**
	 * @return the tvNetworkIds
	 */
	public List<Long> getTvNetworkIds() {
		return tvNetworkIds;
	}

	/**
	 * @param tvNetworkIds the tvNetworkIds to set
	 */
	public void setTvNetworkIds(List<Long> tvNetworkIds) {
		this.tvNetworkIds = tvNetworkIds;
	}

	/**
	 * @return the tvNetworkId
	 */
	public Long getTvNetworkId() {
		return tvNetworkId;
	}

	/**
	 * @param tvNetworkId the tvNetworkId to set
	 */
	public void setTvNetworkId(Long tvNetworkId) {
		this.tvNetworkId = tvNetworkId;
	}

	public String getSSIDNotes() {
		String param;
		
		if(this.isOEMSystem()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("The product set (");
			Map<String, String> oemMap = NmsUtil.getOEMCustomer().getApSeries();
			
			for(String key : oemMap.keySet()) {
				buffer.append(oemMap.get(key)).append(", ");
			}
			
			param = buffer.substring(0, buffer.length() - 2) + ")";
		} else {
			param = "The HiveAP 100 and 300 series";
		}
		
		return MgrUtil.getUserMessage("config.configTemplate.apAssignSsid.note", 
				new String[] {param});
	}
	
	private String getReturnPathWithFullMode(String normalPath, String fullModePath) {
		if (isFullMode()) {
			return fullModePath;
		}
		return normalPath;
	}
	
	public boolean getEnableSupplementalCLI(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",getDomain());
		return null != bo && bo.isEnableSupplementalCLI()? true : false;
	}
	
}