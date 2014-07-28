package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.service.ApplicationService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.mobility.QosMacOui;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.mobility.QosSsid;
import com.ah.bo.network.Application;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.NetworkService;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;


/*
 * Modification Hist
 * 
 * support VHM
 *     set owner to null when cloning
 *     modify function prepareMacOuis, prepareNetworkServices, prepareSsids
 * joseph chenj 05/07/2008
 */
/**
 * @author Chris Scheers
 */

public class QosClassificationAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_ENABLE_NETWORK_SERVICE = 2;
	
	public static final int COLUMN_ENABLE_OUI = 3;
	
	public static final int COLUMN_ENABLE_SSID = 4;
	
	public static final int COLUMN_PROTOCOL_P = 5;
	
	public static final int COLUMN_PROTOCOL_D = 6;
	
	public static final int COLUMN_PROTOCOL_E = 7;
	
	public static final int COLUMN_DESCRIPTION = 8;
	
	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if (isJsonMode() && "continue".equals(operation)) {
				setParentDomID(getDataSource().getParentDomID());
			}
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.classification"))) {
					return getLstForward();
				}
				setSessionDataSource(new QosClassification());
				storeJsonContext();
				prepareInformation();
				return returnResultKeyWord(INPUT, "qosClassificationJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if(hasBoInEasyMode()) {
					return prepareBoList();
				}
				
				prepareInformation();
				if (checkNameExists("classificationName", getDataSource()
						.getClassificationName()) || checkTheSameNetExist()) {
					prepareTransientSSIDTableData();
					prepareAhSSIDDataTableDataAfterEdit();
					prepareTransientOuiTableData();
					prepareAhOuiDataTableDataAfterEdit();
					prepareTransientServiceTableData();
					prepareAhServiceDataTableDataAfterEdit();
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (getActionErrors().size()>0) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					} else {
						return INPUT;
					}
				}
				
				if( !checkServices() || !checkMacOuis() || !checkSSIDs()){
					prepareTransientSSIDTableData();
					prepareAhSSIDDataTableDataAfterEdit();
					prepareTransientOuiTableData();
					prepareAhOuiDataTableDataAfterEdit();
					prepareTransientServiceTableData();
					prepareAhServiceDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				
				updateInformation();
				String result;
				Long newId;
				if ("create".equals(operation)) {
					newId=createBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", newId);
						jsonObject.put("nName", getDataSource().getClassificationName());
						return "json";
					} else {
						result = prepareBoList();
					}
				} else {
					newId = id = createBo(dataSource);
					setUpdateContext(true);
					result = getLstForward();
				}
				
				if (isEasyMode()) {
					QosClassification qosClass = QueryUtil
							.findBoById(QosClassification.class, newId);
					ConfigTemplate defaultTemplate = HmBeParaUtil
							.getEasyModeDefaultTemplate(domainId);
					defaultTemplate.setClassifierMap(qosClass);
					QueryUtil.updateBo(defaultTemplate);
				}
				
				return result;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				QosClassification clone = (QosClassification) findBoById(
						boClass, cloneId,this);
				clone.setId(null);
				clone.setClassificationName("");
				clone.setNetworkServices(getServiceInfo(clone.getNetworkServices()));
				clone.setQosMacOuis(getMacInfo(clone.getQosMacOuis()));
				clone.setQosSsids(getSsidInfo(clone.getQosSsids()));
				clone.setOwner(null);
				clone.setVersion(null);    // joseph chen 06/17/2008
				setSessionDataSource(clone);
				prepareInformation();
				prepareAhSSIDDataTableData();
				prepareAhOuiDataTableData();
				prepareAhServiceDataTableData();
				initQosClassValues();
				addLstTitle(getText("config.title.classification"));
				return INPUT;
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					prepareInformation();
					prepareAhSSIDDataTableData();
					prepareAhOuiDataTableData();
					prepareAhServiceDataTableData();
					
					initQosClassValues();
					addLstTitle(getText("config.title.classification.edit")
							+ " '" + getDisplayName() + "'");
					return returnResultKeyWord(INPUT, "qosClassificationJson");
				}
			} else if ("newService".equals(operation)
					|| "editService".equals(operation)
					|| "newMac".equals(operation)
					|| "editMac".equals(operation)
					|| "newSsid".equals(operation)
					|| "editSsid".equals(operation)) {
				prepareTransientSSIDTableData();
				prepareTransientOuiTableData();
				prepareTransientServiceTableData();
				updateInformation();
				setParentDomID(operation);
				addLstForward("qosClassificationForward");
				addLstTabId(tabId);
				getDataSource().setOperationFlag(operation);
				setUpdateContext(true);
				return operation;
			} else if ("update".equals(operation)) {
				if(!isFullMode() && 1 == refreshPageFlag ){
						prepareInformation();
						prepareTransientSSIDTableData();
						prepareAhSSIDDataTableDataAfterEdit();
						prepareTransientOuiTableData();
						prepareAhOuiDataTableDataAfterEdit();
						prepareTransientServiceTableData();
						prepareAhServiceDataTableDataAfterEdit();
						if(null != id){
							addLstTitle(getText("config.title.classification.edit")
									+ " '" + getDisplayName() + "'");
						}else{
							addLstTitle(getText("config.title.classification"));
							getDataSource().setClassificationName(getDataSource().getClassificationName());
							getDataSource().setDescription(getDataSource().getDescription());
						}
						updateInformation();
						initQosClassValues();
						refreshPageFlag = 2;
						return returnResultKeyWord(INPUT, "qosClassificationJson");
				}else{
					if (dataSource != null) {
						prepareInformation();
						if (checkTheSameNetExist()) {
							prepareTransientSSIDTableData();
							prepareAhSSIDDataTableDataAfterEdit();
							prepareTransientOuiTableData();
							prepareAhOuiDataTableDataAfterEdit();
							prepareTransientServiceTableData();
							prepareAhServiceDataTableDataAfterEdit();
							if (isJsonMode()) {
								jsonObject = new JSONObject();
								jsonObject.put("t", false);
								if (getActionErrors().size()>0) {
									Object[] errs = getActionErrors().toArray();
									jsonObject.put("m", errs[0].toString());
								}
								return "json";
							} else {
								return INPUT;
							}
						}
					}
					if( !checkServices() || !checkMacOuis() || !checkSSIDs()){
						prepareTransientSSIDTableData();
						prepareAhSSIDDataTableDataAfterEdit();
						prepareTransientOuiTableData();
						prepareAhOuiDataTableDataAfterEdit();
						prepareTransientServiceTableData();
						prepareAhServiceDataTableDataAfterEdit();
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						jsonObject.put("m",getActionErrors().toArray()[0].toString());
						return isJsonMode() ? "json" : INPUT;		
					}
					updateInformation();
					updateBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", false);
						return "json";
					} else {
						return prepareBoList();
					}
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareInformation();
					updateInformation();
					if (checkTheSameNetExist()) {
						return INPUT;
					}
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
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareInformation();
				setId(dataSource.getId());
				initQosClassValues();
				setTabId(getLstTabId());
				prepareAhSSIDDataTableDataAfterEdit();
				prepareAhOuiDataTableDataAfterEdit();
				if("newService".equals(getParentDomID()) || "editService".equals(getParentDomID())
						|| "networkPolicyMgtAdvancedSetting_classifierMapId".equals(getParentDomID())){
					prepareAhServiceDataTableDataAfterEdit();
				}else{
					prepareContinueAhServiceDataTableDataAfterEdit();
				}
				editOuiInfo = getDataSource().getEditOuiInfo();
				
				if("newMac".equals(getDataSource().getOperationFlag())){
					if(selectedOuiId != null){
						MacOrOui macOrOui = QueryUtil.findBoById(MacOrOui.class, selectedOuiId);
						if(macOrOui != null){
							String elName = "ouiName_edit";
							editOuiInfo = changeAhDataTableEditInfo(editOuiInfo,elName,macOrOui.getId(),macOrOui.getMacOrOuiName());
						}
					}
				}
				
				if (getUpdateContext()) {
					removeLstTitle();
					MgrUtil.setSessionAttribute("CURRENT_TABID", getTabId());
					removeLstTabId();
					removeLstForward();
					setUpdateContext(false);
				} else {
					if(MgrUtil.getSessionAttribute("CURRENT_TABID") != null)
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute(
							"CURRENT_TABID").toString()));
				}
				return returnResultKeyWord(INPUT, "qosClassificationJson");
			} else if("selectService".equals(operation)){
				initNetworkService();
				return "selectService";
			} else if("selectAppService".equals(operation)){
				initAppService();
				return "selectAppService";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof QosClassification) {
			dataSource = bo;
			getDataSource().getNetworkServices().values();
			getDataSource().getCustomServices().values();
			getDataSource().getQosMacOuis().values();
			getDataSource().getQosSsids().values();
		}else if (bo instanceof MacOrOui) {
			MacOrOui macOrOui = (MacOrOui) bo;
			if (null != macOrOui.getItems())
				macOrOui.getItems().size();
		}
		return null;
	}
	
	private Map<Long,QosMacOui> getMacInfo(Map<Long,QosMacOui> map){
		return new HashMap<Long,QosMacOui>(map);
	}
	private Map<Long,QosSsid> getSsidInfo(Map<Long,QosSsid> map){
		return new HashMap<Long,QosSsid>(map);
	}
	private Map<Long,QosNetworkService> getServiceInfo(Map<Long,QosNetworkService> map){
		return new HashMap<Long,QosNetworkService>(map);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_QOS_CLASSIFICATION);
		setDataSource(QosClassification.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_QOS_CLISSIFIER_MAP;
		defaultAction = MacFilter.FILTER_ACTION_PERMIT;
	}
	
	@Override
	protected void updateConfigTemplate() throws Exception {
		ConfigTemplate defaultTemplate = HmBeParaUtil
			.getEasyModeDefaultTemplate(domainId);
		defaultTemplate.setClassifierMap(null);
		QueryUtil.updateBo(defaultTemplate);
	}
	
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
			code = "config.qos.classification.name";
			break;
		case COLUMN_ENABLE_NETWORK_SERVICE:
			code = "config.qos.classification.tab.networkServices";
			break;
		case COLUMN_ENABLE_OUI:
			code = "config.qos.classification.macOuis";
			break;
		case COLUMN_ENABLE_SSID:
			code = "config.qos.classification.tab.ssid";
			break;
		case COLUMN_PROTOCOL_P:
			code = "config.qos.protocolP";
			break;
		case COLUMN_PROTOCOL_D:
			code = "config.qos.protocolD";
			break;
		case COLUMN_PROTOCOL_E:
			code = "config.qos.protocolE";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.qos.classification.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(8);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ENABLE_NETWORK_SERVICE));
		columns.add(new HmTableColumn(COLUMN_ENABLE_OUI));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SSID));
		columns.add(new HmTableColumn(COLUMN_PROTOCOL_P));
		columns.add(new HmTableColumn(COLUMN_PROTOCOL_D));
		columns.add(new HmTableColumn(COLUMN_PROTOCOL_E));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	public void prepareInformation() throws JSONException {
		prepareAhServiceDataTableColumnDefs();
		prepareAhMacOuiDataTableColumnDefs();
		prepareAhSSIDDataTableColumnDefs();
	}

	private void updateInformation() {
		updateServices();
		updateMacOuis();
		updateSsids();
		prepareQosClassValues();
	}

	public void prepareQosClassValues() {
		if (getDataSource() == null)
			return;
		
		if(!getDataSource().getGeneralEnabled()) {
			getDataSource().setPrtclD(null);
			getDataSource().setPrtclE(null);
			getDataSource().setPrtclP(null);
			
			return ;	
		}
		
		if (chboxD) {
			String protocolDiffValue = "";
			if (this.getProtocolDName() != null)
				for (int i = 0; i < getProtocolDName().length; i++)
					protocolDiffValue = protocolDiffValue
							+ getProtocolDName()[i];
			getDataSource().setPrtclD(protocolDiffValue.trim());
		} else {
			getDataSource().setPrtclD(null);
		}
		if (chboxE) {
			String protocolEValue = "";
			if (this.getProtocolEName() != null)
				for (int i = 0; i < getProtocolEName().length; i++)
					protocolEValue = protocolEValue + getProtocolEName()[i];
			getDataSource().setPrtclE(protocolEValue.trim());
		} else {
			getDataSource().setPrtclE(null);
		}
		if (chboxP) {
			String protocolPValue = "";
			if (this.getProtocolPName() != null)
				for (int i = 0; i < getProtocolPName().length; i++)
					protocolPValue = protocolPValue + getProtocolPName()[i];
			getDataSource().setPrtclP(protocolPValue.trim());
		} else {
			getDataSource().setPrtclP(null);
		}
	}

	public void initQosClassValues() {
		if (getDataSource() == null)
			return;
		String value;
		if(getDataSource().getPrtclD()!=null){
			value = getDataSource().getPrtclD().trim();
		} else {
			value = null;
		}
		if (value != null && !value.equals("")) {
			this.chboxD = true;
			String[] tmp = new String[value.length()];
			for (int i = 0; i < value.length(); i++)
				tmp[i] = value.substring(i, i + 1);
			this.setProtocolDName(tmp);
		}
		if(getDataSource().getPrtclE()!=null){
			value = getDataSource().getPrtclE().trim();
		} else {
			value = null;
		}
		if (value != null && !value.equals("")) {
			this.chboxE = true;
			String[] tmp = new String[value.length()];
			for (int i = 0; i < value.length(); i++)
				tmp[i] = value.substring(i, i + 1);
			this.setProtocolEName(tmp);
		}
		if(getDataSource().getPrtclP()!=null) {
			value = getDataSource().getPrtclP().trim();
		} else {
			value = null;
		}
		if (value != null && !value.equals("")) {
			this.chboxP = true;
			String[] tmp = new String[value.length()];
			for (int i = 0; i < value.length(); i++)
				tmp[i] = value.substring(i, i + 1);
			this.setProtocolPName(tmp);
		}
	}

	@Override
	public QosClassification getDataSource() {
		return (QosClassification) dataSource;
	}

	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisplayName() {
		return getDataSource().getClassificationName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public EnumItem[] getEnumQosClass() {
		return EnumConstUtil.ENUM_QOS_CLASS;
	}

	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}

	public EnumItem[] getEnumLogging() {
		return EnumConstUtil.enumLogging;
	}

	public int getNameLength() {
		return super.getAttributeLength("classificationName");
	}

	public int getDescriptionLength() {
		return super.getAttributeLength("description");
	}

	private List<CheckItem> availableNetworkServices;

	private List<CheckItem> availableMacOuis;
	
	private List<CheckItem> availableSsids;

	private boolean serviceShowing = false;
	private boolean macShowing = false;
	private boolean ssidShowing = false;

	public short getDefaultLogging(){
		return EnumConstUtil.DISABLE;
	}
	public boolean getServiceShowing() {
		return serviceShowing;
	}

	public void setServiceShowing(boolean serviceShowing) {
		this.serviceShowing = serviceShowing;
	}

	public boolean getMacShowing() {
		return macShowing;
	}

	public void setMacShowing(boolean macShowing) {
		this.macShowing = macShowing;
	}

	public boolean getSsidShowing() {
		return ssidShowing;
	}

	public void setSsidShowing(boolean ssidShowing) {
		this.ssidShowing = ssidShowing;
	}

	public List<CheckItem> getAvailableMacOuis() {
		return availableMacOuis;
	}

	public List<CheckItem> getAvailableSsids() {
		return availableSsids;
	}

	public List<CheckItem> getAvailableNetworkServices() {
		return availableNetworkServices;
	}

	public void prepareMacOuis() {
		availableMacOuis = new ArrayList<CheckItem>();
		List<CheckItem> allMacOuis = this.getBoCheckItems("macOrOuiName", MacOrOui.class, 
			new FilterParams("typeFlag",MacOrOui.TYPE_MAC_OUI));
		
		for (CheckItem macOui : allMacOuis) {
			if (!hasQosMacOui(macOui.getValue())) {
				availableMacOuis.add(macOui);
			}
		}
		if (availableMacOuis.size() == 0) {
			availableMacOuis.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}
	
	public void prepareSsids() {
		availableSsids = new ArrayList<CheckItem>();
		List<CheckItem> allSsids = this.getBoCheckItems("ssidName", SsidProfile.class, null);
		
		for (CheckItem ssid : allSsids) {
			if (!hasQosSsid(ssid.getValue())) {
				availableSsids.add(ssid);
			}
		}
		if (availableSsids.size() == 0) {
			availableSsids.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	protected void prepareNetworkServices() {
		availableNetworkServices = new ArrayList<CheckItem>();
		List<CheckItem> allNetworkServices = this.getBoCheckItems("serviceName", NetworkService.class, null);
		List<NetworkService> list = QueryUtil.executeQuery(NetworkService.class, null, null,domainId);
		for (CheckItem service : allNetworkServices) {
			if (!hasNetworkService(service.getValue())) {
				for(NetworkService ns: list){
					if(ns.getId().equals(service.getId())){
						if(ns.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
							CheckItem ci = new CheckItem(service.getId(),service.getValue()+MgrUtil
									.getUserMessage("config.servicetype.network"));
							availableNetworkServices.add(ci);
							break;
						}else{
							CheckItem ci = new CheckItem(service.getId(),service.getValue()+MgrUtil
									.getUserMessage("config.servicetype.l7"));
							availableNetworkServices.add(ci);
							break;
						}
					}
				}
			}
		}
		if (availableNetworkServices.size() == 0) {
			availableNetworkServices.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	protected boolean hasQosMacOui(String macOuiName) {
		if (getDataSource().getMacOuisEnabled()) {
			for (QosMacOui macOui : getDataSource().getQosMacOuis().values()) {
				if (macOuiName.equals(macOui.getMacOui().getMacOrOuiName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean hasQosSsid(String ssidName) {
		if (getDataSource().getSsidEnabled()) {
			for (QosSsid qosSsid : getDataSource().getQosSsids().values()) {
				if (ssidName.equals(qosSsid.getSsid().getSsidName())) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean hasNetworkService(String serviceName) {
		if (getDataSource().getNetworkServicesEnabled()) {
			for (QosNetworkService networkService : getDataSource()
					.getNetworkServices().values()) {
				if (serviceName.equals(networkService.getNetworkService()
						.getServiceName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check the same Protocol Number and Port Number network service exist in list.
	 *
	 *@return boolean
	 */
	private boolean checkTheSameNetExist() {
		if (getDataSource().getNetworkServicesEnabled()) {
			boolean saveServiceFlg = false;
			if(serviceNames != null){
				if (serviceNames.length > 100) {
					addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
					return true;
				}
				List<Integer> networklist = new ArrayList<>();
				List<Integer> applist = new ArrayList<>();
				for(int i=0; i<serviceNames.length; i++){
					if("".equals(serviceNames[i]) || null == serviceNames[i] || serviceNames[i].contains(":")){
						continue;
					}
					NetworkService networkService = QueryUtil.findBoById(NetworkService.class,
							Long.parseLong(serviceNames[i]));
					for(int j=i+1; j<serviceNames.length; j++){
						if("".equals(serviceNames[j]) || null == serviceNames[j] || serviceNames[j].contains(":")){
							continue;
						}
						NetworkService netService = QueryUtil.findBoById(NetworkService.class,
								Long.parseLong(serviceNames[j]));
						if(null != networkService && null != netService && 
								networkService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK 
								&& netService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
							if (null != networkService && null != netService &&  
									!netService.getServiceName().equals(networkService.getServiceName())) {
								if (networkService.getProtocolNumber() == netService.getProtocolNumber()
										&& networkService.getPortNumber() == netService.getPortNumber()) {
									networklist.add(i+1);
									break;
								}
							}
						}
					}
					if(networkService != null && networkService.getServiceType() == NetworkService.SERVICE_TYPE_L7 && 
							Short.parseShort(serviceFilterActions[i]) == MacFilter.FILTER_ACTION_DENY){
						applist.add(i+1);
					}
					saveServiceFlg = true;
				}
				
				if (networklist.size() >0) {
					addActionError(MgrUtil.getUserMessage("error.config.classifier.network.service", "Network Services cannot contain "));
					return true;
				}
				
				if (applist.size() >1) {
					addActionError("Some application services in ("+applist.toString()+") rows cannot be added because their actions can be only permit.");
					return true;
				}else if(applist.size() == 1){
					addActionError("The application service in ("+applist.toString()+") row cannot be added because its action can be only permit.");
					return true;
				}
				if(!saveServiceFlg){
					addActionError("Please add at least one service.");
					return true;
				}
			}
			
		}
		return false;
	}

	protected void updateMacOuis() {
		if(!getDataSource().getMacOuisEnabled()){
			getDataSource().setQosMacOuis(new HashMap<Long, QosMacOui>());
			return;
		}
		if(getDataSource() != null){
			if(getDataSource().getQosMacOuis() != null){
				List<QosMacOui> orderedQosMacOuis = getDataSource().getOrderedQosMacOuis();
				if(orderedQosMacOuis != null){
					for(QosMacOui qmo : orderedQosMacOuis){
						if(qmo.getMacOui() != null){
							getDataSource().getQosMacOuis().remove(qmo.getMacOui().getId());
						}
					}
				}
			}
		}
		if(ouiNames != null){
			for(int i=0; i<ouiNames.length; i++){
				if("".equals(ouiNames[i]) || null == ouiNames[i]){
					continue;
				}
				QosMacOui qosMacOui = new QosMacOui();
				MacOrOui macOui = QueryUtil.findBoById(MacOrOui.class,Long.parseLong(ouiNames[i]));
				if (macOui == null)
					continue;
				qosMacOui.setMacOui(macOui);
				qosMacOui.setFilterActionMacOuis(Short.parseShort(ouiFilterActions[i]));
				qosMacOui.setLoggingMacOuis(Short.parseShort(ouiLoggings[i]));
				qosMacOui.setQosClassMacOuis(Short.parseShort(ouiQosClasses[i]));
				qosMacOui.setComment(ouiComments[i]);
				qosMacOui.setMacEntry(macEntry);
				if(getDataSource().getQosMacOuis().size()>=20)
					return;
				getDataSource().getQosMacOuis().put(qosMacOui.getMacOui().getId(), qosMacOui);
			}
		}
	}
	
	protected void updateSsids() {
		if(!getDataSource().getSsidEnabled()){
			getDataSource().setQosSsids(new HashMap<Long, QosSsid>());
			return;
		}
		if(getDataSource() != null){
			if(getDataSource().getQosSsids() != null){
				List<QosSsid> orderedQosSsids = getDataSource().getOrderedQosSsids();
				if(orderedQosSsids != null){
					for(QosSsid qs : orderedQosSsids){
						if(qs.getSsid() != null){
							getDataSource().getQosSsids().remove(qs.getSsid().getId());
						}
					}
				}
			}
		}
		if(ssidNames != null){
			for(int i=0; i<ssidNames.length; i++){
				if("".equals(ssidNames[i]) || null == ssidNames[i]){
					continue;
				}
				QosSsid qssid = new QosSsid();
				SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class,Long.parseLong(ssidNames[i]));
				if (ssid == null)
					continue;
				qssid.setSsid(ssid);
				qssid.setQosClassSsids(Short.parseShort(ssidQosClasses[i]));
				getDataSource().getQosSsids().put(qssid.getSsid().getId(),
						qssid);
			}
		}
	}

	protected void updateServices() {
		if(!getDataSource().getNetworkServicesEnabled()){
			getDataSource().setNetworkServices(new HashMap<Long, QosNetworkService>());
			getDataSource().setCustomServices(new HashMap<Long, QosCustomService>());
			return;
		}
		
		if(getDataSource() != null){
			if(getDataSource().getNetworkServices() != null){
				List<QosNetworkService> orderedQosServices = getDataSource().getOrderedNetworkServices();
				if(orderedQosServices != null){
					for(QosNetworkService qns : orderedQosServices){
						if(qns.getNetworkService() != null){
							getDataSource().getNetworkServices().remove(qns.getNetworkService().getId());
						}
					}
				}
			}
			if(getDataSource().getCustomServices() != null){
				List<QosCustomService> orderedQosServices = getDataSource().getOrderedCustomServices();
				if(orderedQosServices != null){
					for(QosCustomService qcs : orderedQosServices){
						if(qcs.getCustomAppService() != null){
							getDataSource().getCustomServices().remove(qcs.getCustomAppService().getId());
						}
					}
				}
			}
		}
		
		if(serviceNames != null){
			for(int i=0; i<serviceNames.length; i++){
				if("".equals(serviceNames[i]) || null == serviceNames[i] || serviceNames[i].contains(":")){
					continue;
				}
				String serviceName = serviceNames[i];
				String appType = serviceName.substring(serviceName.length()-1);
				String serviceId =  serviceName.substring(0, serviceName.length()-1);
				NetworkService networkService = null;
				CustomApplication customApp = null;
				QosNetworkService qosNetworkService = new QosNetworkService();
				QosCustomService qosCustomService = new QosCustomService();
				if(null != appType && !"".equals(appType)){
					if("0".equals(appType)){
						networkService = QueryUtil.findBoById(NetworkService.class,
								Long.parseLong(serviceId));
						if (networkService == null) {
							continue;
						}
					}else{
						customApp = QueryUtil.findBoById(CustomApplication.class,
								Long.parseLong(serviceId));
						if (customApp == null) {
							continue;
						}
					}
				}
				
				boolean bool = false;
				for (QosNetworkService service : getDataSource().getNetworkServices().values()) {
					NetworkService netService = service.getNetworkService();
					if(null != networkService && null != netService && 
							networkService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK 
							&& netService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
						if (netService.getProtocolNumber() == networkService.getProtocolNumber()
								&& netService.getPortNumber() == networkService.getPortNumber()) {
								bool = true;
								break;
							}
					}
				}
				if (bool)
					continue;
				
				if("0".equals(appType)){
					qosNetworkService.setNetworkService(networkService);
					qosNetworkService.setQosClass(Short.parseShort(serviceQosClasses[i]));
					qosNetworkService.setFilterAction(Short.parseShort(serviceFilterActions[i]));
					qosNetworkService.setLogging(Short.parseShort(serviceLoggings[i]));
				}else{
					qosCustomService.setCustomAppService(customApp);
					qosCustomService.setQosClass(Short.parseShort(serviceQosClasses[i]));
					qosCustomService.setFilterAction(Short.parseShort(serviceFilterActions[i]));
					qosCustomService.setLogging(Short.parseShort(serviceLoggings[i]));
				}
				
				if((getDataSource().getNetworkServices().size() + getDataSource().getCustomServices().size()) >= 100)
					return;
				if(null != qosNetworkService.getNetworkService()){
					getDataSource().getNetworkServices().put(
							qosNetworkService.getNetworkService().getId(),
							qosNetworkService);
				}
				if(null != qosCustomService.getCustomAppService()){
					getDataSource().getCustomServices().put(
							qosCustomService.getCustomAppService().getId(),
							qosCustomService);
				}
			}
		}
		
	}

	private short defaultAction;
	
	private Long selectedServiceId;

	private Long selectedOuiId;
	
	private Long selectedSsidId;

	private String macEntry;

	public Short getQosClassValue(){
		return EnumConstUtil.QOS_CLASS_BEST_EFFORT_1;
	}

	public void setMacEntry(String macEntry) {
		this.macEntry = macEntry;
	}
	
	
	public short getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(short defaultAction) {
		this.defaultAction = defaultAction;
	}

	public  static final String[] defaultKey={ "7", "6", "5", "4", "3",
			"0", "2", "1" };
	public  static final String[] defaultDiffKey={ "7", "6", "5", "4", "3", "2", "1", "0" };
	
	private String[] protocolDName = defaultDiffKey;
	
	private  String[] protocolEName = protocolDName;
	
	private  String[] protocolPName = protocolDName;
	
	public static final String[] defaultDiffValue = { "56-63", "48-55",
			"40-47", "32-39", "24-31", "00-07", "16-23", "08-15" };

	public Map<String, DefaultValue> getProtocolPE() {
		Map<String, DefaultValue> map = new LinkedHashMap<String, DefaultValue>();
		int count = 0;
		for (String value : defaultKey) {
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.key = value;
			defaultValue.value = protocolEName[count++];
			map.put(defaultValue.key, defaultValue);
		}
		return map;
	}
	
	public Map<String, DefaultValue> getProtocolPP() {
		Map<String, DefaultValue> map = new LinkedHashMap<String, DefaultValue>();
		int count = 0;
		for (String value : defaultKey) {
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.key = value;
			defaultValue.value = protocolPName[count++];
			map.put(defaultValue.key, defaultValue);
		}
		return map;
	}

	public Map<String, DefaultValue> getProtocolD() {
		Map<String, DefaultValue> map = new LinkedHashMap<String, DefaultValue>();
		int count = 7;
		for (String value : defaultDiffValue) {
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.key = value;
			defaultValue.value = String.valueOf(count--);
			map.put(defaultValue.key, defaultValue);
		}
		return map;
	}

	class DefaultValue {
		String key;

		String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private boolean chboxP = false;
	private boolean chboxE = false;
	private boolean chboxD = false;


	public String[] getProtocolDName() {
		return protocolDName;
	}

	public void setProtocolDName(String[] protocolDName) {
		this.protocolDName = protocolDName;
	}

	public String[] getProtocolEName() {
		return protocolEName;
	}

	public void setProtocolEName(String[] protocolEName) {
		this.protocolEName = protocolEName;
	}

	public String[] getProtocolPName() {
		return protocolPName;
	}

	public void setProtocolPName(String[] protocolPName) {
		this.protocolPName = protocolPName;
	}

	public boolean getChboxP() {
		return chboxP;
	}

	public boolean getChboxE() {
		return chboxE;
	}

	public void setChboxE(boolean chboxE) {
		this.chboxE = chboxE;
	}

	public boolean getChboxD() {
		return chboxD;
	}

	public void setChboxD(boolean chboxD) {
		this.chboxD = chboxD;
	}

	public void setChboxP(boolean chboxP) {
		this.chboxP = chboxP;
	}
	/**
	 * getter of selectedServiceId
	 * @return the selectedServiceId
	 */
	public Long getSelectedServiceId() {
		return selectedServiceId;
	}
	/**
	 * setter of selectedServiceId
	 * @param selectedServiceId the selectedServiceId to set
	 */
	public void setSelectedServiceId(Long selectedServiceId) {
		this.selectedServiceId = selectedServiceId;
	}
	/**
	 * getter of selectedOuiId
	 * @return the selectedOuiId
	 */
	public Long getSelectedOuiId() {
		return selectedOuiId;
	}
	/**
	 * setter of selectedOuiId
	 * @param selectedOuiId the selectedOuiId to set
	 */
	public void setSelectedOuiId(Long selectedOuiId) {
		this.selectedOuiId = selectedOuiId;
	}
	/**
	 * getter of selectedSsidId
	 * @return the selectedSsidId
	 */
	public Long getSelectedSsidId() {
		return selectedSsidId;
	}
	/**
	 * setter of selectedSsidId
	 * @param selectedSsidId the selectedSsidId to set
	 */
	public void setSelectedSsidId(Long selectedSsidId) {
		this.selectedSsidId = selectedSsidId;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		QosClassification source = QueryUtil.findBoById(QosClassification.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<QosClassification> list = QueryUtil.executeQuery(QosClassification.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (QosClassification profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			QosClassification qos = source.clone();
			
			if (null == qos) {
				continue;
			}
			
			qos.setId(profile.getId());
			qos.setVersion(profile.getVersion());
			qos.setClassificationName(profile.getClassificationName());
			qos.setOwner(profile.getOwner());
			hmBos.add(qos);
		}
	
		return hmBos;
	}
	
	private String selectedAppIds;
	private int selectedServiceNum;
	private String selectedServiceIds;
	private List<Application> selectedApp;
	private List<Application> unSelectedApp;
	private List<NetworkService> selectedService;
	private List<NetworkService> unSelectedService;
	private List<String> searchUnSelectedService;
	private List<String> searchUnSelectedCustomAppList;
	
	public List<String> getSearchUnSelectedCustomAppList() {
		return searchUnSelectedCustomAppList;
	}

	public void setSearchUnSelectedCustomAppList(
			List<String> searchUnSelectedCustomAppList) {
		this.searchUnSelectedCustomAppList = searchUnSelectedCustomAppList;
	}

	public List<String> getSearchUnSelectedService() {
		return searchUnSelectedService;
	}

	public void setSearchUnSelectedService(
			List<String> searchUnSelectedService) {
		this.searchUnSelectedService = searchUnSelectedService;
	}
	public String getSelectedAppIds() {
		return selectedAppIds;
	}

	public void setSelectedAppIds(String selectedAppIds) {
		this.selectedAppIds = selectedAppIds;
	}

	public int getSelectedServiceNum() {
		return selectedServiceNum;
	}

	public void setSelectedServiceNum(int selectedServiceNum) {
		this.selectedServiceNum = selectedServiceNum;
	}

	public String getSelectedServiceIds() {
		return selectedServiceIds;
	}

	public void setSelectedServiceIds(String selectedServiceIds) {
		this.selectedServiceIds = selectedServiceIds;
	}

	public List<Application> getSelectedApp() {
		return selectedApp;
	}

	public void setSelectedApp(List<Application> selectedApp) {
		this.selectedApp = selectedApp;
	}

	public List<Application> getUnSelectedApp() {
		return unSelectedApp;
	}

	public void setUnSelectedApp(List<Application> unSelectedApp) {
		this.unSelectedApp = unSelectedApp;
	}
	
	public List<NetworkService> getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(List<NetworkService> selectedService) {
		this.selectedService = selectedService;
	}

	public List<NetworkService> getUnSelectedService() {
		return unSelectedService;
	}

	public void setUnSelectedService(List<NetworkService> unSelectedService) {
		this.unSelectedService = unSelectedService;
	}

	private void initNetworkService() {
		FilterParams filterParams = new FilterParams("domainName", HmDomain.GLOBAL_DOMAIN);
		List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, filterParams);
		Long globalDomainId = 0l;
		if(null != list && !list.isEmpty()){
			globalDomainId = list.get(0).getId();
		}
		List<NetworkService> allServiceList  = QueryUtil.executeQuery(NetworkService.class, null, new FilterParams("servicetype = :s1 and (owner.id = :s2 or owner.id = :s3)",
				new Object[]{NetworkService.SERVICE_TYPE_NETWORK,getDomain().getId(),globalDomainId}));
		
		QosClassification qosClassification = null;
		if(null != id && !"".equals(id)){
			qosClassification = QueryUtil.findBoById(QosClassification.class, id, this);
		}
		unSelectedService = new ArrayList<NetworkService>();
		if (qosClassification != null && qosClassification.getNetworkServices() != null &&
				!qosClassification.getNetworkServices().isEmpty()) {
			for(QosNetworkService qns : qosClassification.getNetworkServices().values()){
				if(qns.getNetworkService() != null && qns.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
					allServiceList.remove(qns.getNetworkService());
				}
			}
		}
		unSelectedService = allServiceList;
		searchUnSelectedService = new ArrayList<>();
		for(NetworkService ns: unSelectedService){
			ns.setAppType(IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
			String name = ns.getServiceName().replace("\\", "\\\\");
			searchUnSelectedService.add(name);
		}
	}
	
	private void initAppService() throws Exception{
		ApplicationService appService = new ApplicationService();
		List<Application> fixedAppList = appService.getApplicationWithBytes(getDomain());
//		for(Application app: fixedAppList){
//			if(null != allGroupNames && !allGroupNames.isEmpty()){
//				if(!allGroupNames.contains(app.getAppGroupName())){
//					allGroupNames.add(app.getAppGroupName());
//				}
//			}else{
//				allGroupNames.add(app.getAppGroupName());
//			}
//		}
		QosClassification qosClassification = null;
		if(null != id && !"".equals(id)){
			qosClassification = QueryUtil.findBoById(QosClassification.class, id, this);
		}
		unSelectedApp = new ArrayList<Application>();
		
		List<Integer> removeAppCodes = new ArrayList<Integer>(); 
		if (qosClassification != null && qosClassification.getNetworkServices() != null &&
				!qosClassification.getNetworkServices().isEmpty()) {
			for(QosNetworkService qns : qosClassification.getNetworkServices().values()){
				if(qns.getNetworkService() != null && qns.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
//					Application appl = QueryUtil.findBoByAttribute(Application.class, "appCode", qns.getNetworkService().getAppId());
//					fixedAppList.remove(appl);
					removeAppCodes.add(qns.getNetworkService().getAppId());
				}
			}
		}
		
		List<NetworkService> appServiceList = QueryUtil.executeQuery(NetworkService.class, null, new FilterParams("servicetype = :s1 and owner.id = :s2",
				new Object[]{NetworkService.SERVICE_TYPE_L7, getDomain().getId()}));
		List<Integer> appCodes = new ArrayList<Integer>();
		for(NetworkService ns : appServiceList){
			appCodes.add(ns.getAppId());
		}
		
		List<Application> destApps = new ArrayList<Application>();
		for(Application app: fixedAppList){
			if(null != allGroupNames && !allGroupNames.isEmpty()){
				if(!allGroupNames.contains(app.getAppGroupName())){
					allGroupNames.add(app.getAppGroupName());
				}
			}else{
				allGroupNames.add(app.getAppGroupName());
			}
			boolean result = false;
			if(null != removeAppCodes && !removeAppCodes.isEmpty()){
				for(Integer in : removeAppCodes){
					if(app.getAppCode().equals(in)){
						result = true;
						break;
					}
				}
			}
			if(!result){
				destApps.add(app);
			}else{
				appCodes.remove(app.getAppCode());
			}
		}
		
		
		for(Application app : destApps){
			Integer appcode = app.getAppCode();
			app.setAppType(IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
			if(null != appCodes && !appCodes.isEmpty() && appCodes.contains(appcode)){
				for(NetworkService ns : appServiceList){
					if(appcode == ns.getAppId()){
						app.setId(ns.getId());
						break;
					}
				}
			}else{
				NetworkService serviceDto = new NetworkService();
				String appName = NetworkService.L7_SERVICE_NAME_PREFIX+app.getShortName();
				if(appName.length() > 32){
					appName = appName.substring(0, 32);
				}
				serviceDto.setServiceName(appName);
				serviceDto.setProtocolNumber(0);
				serviceDto.setPortNumber(0);				
				serviceDto.setIdleTimeout(300);
				serviceDto.setDescription(app.getAppName());
				serviceDto.setAlgType((short)0);
				serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
				serviceDto.setAppId(app.getAppCode());
				serviceDto.setDefaultFlag(false);
				serviceDto.setOwner(getDomain());
				serviceDto.setCliDefaultFlag(false);
				Long id = QueryUtil.createBo(serviceDto);
				app.setId(id);
			}
		}
//		for (Application app : fixedAppList) {
//			NetworkService service = QueryUtil.findBoByAttribute(NetworkService.class, "appId", app.getAppCode(), getDomain().getId());
//			if(null != service){
//				app.setId(service.getId());
//			}else{
//				NetworkService serviceDto = new NetworkService();
//				serviceDto.setServiceName(NetworkService.L7_SERVICE_NAME_PREFIX+app.getAppName());
//				serviceDto.setProtocolNumber(0);
//				serviceDto.setPortNumber(0);				
//				serviceDto.setIdleTimeout(300);
//				serviceDto.setDescription(app.getAppName());
//				serviceDto.setAlgType((short)0);
//				serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
//				serviceDto.setAppId(app.getAppCode());
//				serviceDto.setDefaultFlag(false);
//				serviceDto.setOwner(getDomain());
//				serviceDto.setCliDefaultFlag(false);
//				Long id = QueryUtil.createBo(serviceDto);
//				app.setId(id);
//			}
//			app.setAppType(IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
//		}
		unSelectedApp = destApps;
		
		List<CustomApplication> customList = appService.getCustomApplicationWithBytes(getDomain());
		List<Long> existCustomIds = new ArrayList<Long>();
		if (qosClassification != null && qosClassification.getCustomServices() != null &&
				!qosClassification.getCustomServices().isEmpty()) {
			for(QosCustomService qcs : qosClassification.getCustomServices().values()){
				if(qcs.getCustomAppService() != null){
					existCustomIds.add(qcs.getCustomAppService().getId());
//					CustomApplication appl = QueryUtil.findBoById(CustomApplication.class, qcs.getCustomAppService().getId());
//					customList.remove(appl);
				}
			}
		}
		unSelectedCustomAppList = new ArrayList<CustomApplication>();
		for(CustomApplication appl : customList){
			if(null != existCustomIds && !existCustomIds.isEmpty()){
				if(!existCustomIds.contains(appl.getId())){
					unSelectedCustomAppList.add(appl);
				}
			}else{
				unSelectedCustomAppList.add(appl);
			}
		}
//		unSelectedCustomAppList = customList;
		searchUnSelectedCustomAppList = new ArrayList<>();
		for(CustomApplication ca : unSelectedCustomAppList){
			ca.setAppType(IpPolicyRule.RULE_CUSTOMSERVICE_TYPE);
			String name = ca.getCustomAppName().replace("\\", "\\\\");
			searchUnSelectedCustomAppList.add(name);
		}
	}
	
	private void prepareAhServiceDataTableColumnDefs() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		
		column = new AhDataTableColumn();
		column.setMark("serviceNames");
		column.setOptions(getSelectServiceOptions());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("serviceQosClasses");
		column.setOptions(getAllQosClass());
		column.setDefaultValue(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("serviceFilterActions");
		column.setOptions(getAllFilterAction());
		column.setDefaultValue(MacFilter.FILTER_ACTION_PERMIT);
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("serviceLoggings");
		column.setOptions(getAllLogging());
		column.setDefaultValue(EnumConstUtil.DISABLE);
		ahDataTableColumns.add(column);
		
		ahServiceDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	} 
	
	private void prepareTransientServiceTableData(){
		getDataSource().setEditServiceInfo(editServiceInfo);
		getDataSource().setServiceNames(serviceNames);
		getDataSource().setServiceQosClasses(serviceQosClasses);
		getDataSource().setServiceFilterActions(serviceFilterActions);
		getDataSource().setServiceLoggings(serviceLoggings);
	}
	
	private void prepareAhServiceDataTableData() throws JSONException{ 
		if((getDataSource() == null || getDataSource().getNetworkServices() == null 
				|| getDataSource().getNetworkServices().values().isEmpty()) 
				&& (getDataSource() == null || getDataSource().getCustomServices() == null 
				|| getDataSource().getCustomServices().values().isEmpty()) ){
			ahServiceDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		List<QosNetworkService> orderedQosNetworkServices = getDataSource().getOrderedNetworkServices();
		if(null != orderedQosNetworkServices && !orderedQosNetworkServices.isEmpty()){
			for(QosNetworkService qns: orderedQosNetworkServices){
				if(null != qns){
					JSONObject jsonObject = new JSONObject();
					JSONObject snObj = new JSONObject();
					if (qns.getNetworkService() != null) {
						snObj.put("value", qns.getNetworkService().getId().toString()+IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
						String name = qns.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_NETWORK ? NetworkService.NETWORK_SERVICE + qns.getNetworkService().getServiceName() :
							NetworkService.APPLICATION_SERVICE + qns.getNetworkService().getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length());;
						if(name.lastIndexOf("\\") != -1){
							name = name.substring(0, name.length() -1);
						}
						snObj.put("text", name);
					}
					jsonObject.put("serviceNames", snObj);
					jsonObject.put("serviceQosClasses", qns.getQosClass());
					jsonObject.put("serviceFilterActions", qns.getFilterAction());
					jsonObject.put("serviceLoggings", qns.getLogging());
					jsonArray.put(jsonObject);
				}
			}
		}
		List<QosCustomService> orderedQosCustomServices = getDataSource().getOrderedCustomServices();
		if(null != orderedQosCustomServices && !orderedQosCustomServices.isEmpty()){
			for(QosCustomService qcs: orderedQosCustomServices){
				if(null != qcs){
					JSONObject jsonObject = new JSONObject();
					JSONObject snObj = new JSONObject();
					snObj.put("value", qcs.getCustomAppService().getId().toString()+IpPolicyRule.RULE_CUSTOMSERVICE_TYPE);
					String name = NetworkService.APPLICATION_SERVICE + qcs.getCustomAppService().getCustomAppName();
					if(name.lastIndexOf("\\") != -1){
						name = name.substring(0, name.length() -1);
					}
					snObj.put("text", name);
					jsonObject.put("serviceNames", snObj);
					jsonObject.put("serviceQosClasses", qcs.getQosClass());
					jsonObject.put("serviceFilterActions", qcs.getFilterAction());
					jsonObject.put("serviceLoggings", qcs.getLogging());
					jsonArray.put(jsonObject);
				}
			}
		}
		ahServiceDtDatas = jsonArray.toString();
	}
	
	private void prepareAhServiceDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getServiceNames() == null){
			ahServiceDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<getDataSource().getServiceNames().length; i++){
			if(!"".equals(getDataSource().getServiceNames()[i]) && getDataSource().getServiceNames()[i].indexOf("value") == -1){
				JSONObject jsonObject = new JSONObject();
				JSONObject snObj = new JSONObject();
				if(getDataSource().getServiceNames()[i].indexOf("value") != -1){
					String service = getDataSource().getServiceNames()[i];
					String value = service.substring(service.indexOf(":")+1,service.indexOf(","));
					String name = service.substring(service.indexOf("\"")+1,service.lastIndexOf("\""));
					if(name.lastIndexOf("\\") != -1){
						name = name.substring(0, name.length() -1);
					}
					snObj.put("value", value);
					snObj.put("text", name);
					jsonObject.put("serviceNames", snObj);
				}else{
					if(!"".equals(getDataSource().getServiceNames()[i])){
						String serviceName = getDataSource().getServiceNames()[i];
						snObj.put("value", serviceName);
						String appType = serviceName.substring(serviceName.length()-1);
						String serviceId =  serviceName.substring(0, serviceName.length()-1);
						NetworkService ns = null;
						CustomApplication customApp = null;
						if(null != appType && !"".equals(appType)){
							if("0".equals(appType)){
								ns = QueryUtil.findBoById(NetworkService.class, Long.parseLong(serviceId));
								if(null != ns){
									String name = ns.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK ? NetworkService.NETWORK_SERVICE + ns.getServiceName() :
										NetworkService.APPLICATION_SERVICE + ns.getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length());
									if(name.lastIndexOf("\\") != -1){
										name = name.substring(0, name.length() -1);
									}
									snObj.put("text", name);
								}else{
									snObj.put("text", "");
								}
							}else{
								customApp = QueryUtil.findBoById(CustomApplication.class,Long.parseLong(serviceId));
								String name = NetworkService.APPLICATION_SERVICE + customApp.getCustomAppName();
								if(name.lastIndexOf("\\") != -1){
									name = name.substring(0, name.length() -1);
								}
								snObj.put("text", name);
							}
						}
						
						jsonObject.put("serviceNames", snObj);
					}else{
						jsonObject.put("serviceNames", "");
					}
				}
				jsonObject.put("serviceQosClasses", getDataSource().getServiceQosClasses()[i]);
				jsonObject.put("serviceFilterActions", getDataSource().getServiceFilterActions()[i]);
				jsonObject.put("serviceLoggings", getDataSource().getServiceLoggings()[i]);
				jsonArray.put(jsonObject);
			}
		}
		ahServiceDtDatas = jsonArray.toString();
	}
	
	private void prepareContinueAhServiceDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getServiceNames() == null){
			ahServiceDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<getDataSource().getServiceNames().length; i++){
			if(!"".equals(getDataSource().getServiceNames()[i]) && getDataSource().getServiceNames()[i].indexOf("value") == -1){
				JSONObject jsonObject = new JSONObject();
				JSONObject snObj = new JSONObject();
				if(getDataSource().getServiceNames()[i].indexOf("value") != -1){
					String service = getDataSource().getServiceNames()[i];
					String value = service.substring(service.indexOf(":")+1,service.indexOf(","));
					String name = service.substring(service.indexOf("\"")+1,service.lastIndexOf("\""));
					if(name.lastIndexOf("\\") != -1){
						name = name.substring(0, name.length() -1);
					}
					snObj.put("value", value);
					snObj.put("text", name);
					jsonObject.put("serviceNames", snObj);
				}else{
					if(!"".equals(getDataSource().getServiceNames()[i])){
						String serviceName = getDataSource().getServiceNames()[i];
						snObj.put("value", serviceName);
						String appType = serviceName.substring(serviceName.length()-1);
						String serviceId =  serviceName.substring(0, serviceName.length()-1);
						NetworkService ns = null;
						CustomApplication customApp = null;
						if(null != appType && !"".equals(appType)){
							if("0".equals(appType)){
								ns = QueryUtil.findBoById(NetworkService.class, Long.parseLong(serviceId));
								if(null != ns){
									String name = ns.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK ? NetworkService.NETWORK_SERVICE + ns.getServiceName() :
										NetworkService.APPLICATION_SERVICE + ns.getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length());
									if(name.lastIndexOf("\\") != -1){
										name = name.substring(0, name.length() -1);
									}
									snObj.put("text", name);
								}else{
									snObj.put("text", "");
								}
							}else{
								customApp = QueryUtil.findBoById(CustomApplication.class,Long.parseLong(serviceId));
								String name = NetworkService.APPLICATION_SERVICE + customApp.getCustomAppName();
								if(name.lastIndexOf("\\") != -1){
									name = name.substring(0, name.length() -1);
								}
								snObj.put("text", name);
							}
						}
						
						jsonObject.put("serviceNames", snObj);
					}else{
						jsonObject.put("serviceNames", "");
					}
				}
				jsonObject.put("serviceQosClasses", getDataSource().getServiceQosClasses()[i]);
				jsonObject.put("serviceFilterActions", getDataSource().getServiceFilterActions()[i]);
				jsonObject.put("serviceLoggings", getDataSource().getServiceLoggings()[i]);
				jsonArray.put(jsonObject);
			}
			
		}
		ahServiceDtDatas = jsonArray.toString();
	}
	
	private boolean checkServices(){
		if(getDataSource() != null && getDataSource().getNetworkServicesEnabled()){
			if(serviceNames == null || (serviceNames != null && serviceNames.length==1 
					&& (serviceNames[0].equals("") || serviceNames[0].indexOf("value") != -1))){
				addActionError("Please add at least one item in Services.");
				return false;
			}
		}
		if(serviceNames != null){
			List<Integer> list = new ArrayList<>();
			List<String> nameList = new ArrayList<String>();
			for(int i=0; i<serviceNames.length; i++){
				if( !("".equals(serviceNames[i]) || null == serviceNames[i]
						|| serviceNames[i].indexOf("value") != -1)){
					nameList.add(serviceNames[i]);
				}
			}
			if(null != nameList && !nameList.isEmpty()){
				for(int i=0 ; i<nameList.size(); i++){
					String serviceName = nameList.get(i);
					String appType = serviceName.substring(serviceName.length()-1);
					if("0".equals(appType)){
						for(int j=i+1;j<nameList.size();j++){
							if(serviceName.equals(nameList.get(j))){
								list.add(j+1);
								break;
							}
						}
					}else{
						for(int j=i+1;j<nameList.size();j++){
							if(serviceName.equals(nameList.get(j))){
								list.add(j+1);
								break;
							}
						}
					}
				}
			}
			
			if(null != list && !list.isEmpty()){
				sortList(list);
			}
			if(list.size() > 1){
				addActionError("Some Services in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The Service in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}
		}
		return true;
	}
	
	private void sortList(List<Integer> list) {
		Collections.sort(list, new Comparator<Integer>() {
			public int compare(Integer one, Integer two) {
				return (int) (one - two);
			}
		});
	}
	
	private void prepareAhMacOuiDataTableColumnDefs() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		
		column = new AhDataTableColumn();
		column.setMark("ouiNames");
		column.setOptions(getAllMacOui());
		column.setDefaultValue(getAllMacOui().isEmpty() ? "" : getAllMacOui().get(0).getId());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ouiQosClasses");
		column.setOptions(getAllQosClass());
		column.setDefaultValue(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ouiFilterActions");
		column.setOptions(getAllFilterAction());
		column.setDefaultValue(MacFilter.FILTER_ACTION_PERMIT);
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ouiLoggings");
		column.setOptions(getAllLogging());
		column.setDefaultValue(EnumConstUtil.DISABLE);
		ahDataTableColumns.add(column);
		
		ahOuiDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	} 
	
	private void prepareTransientOuiTableData(){
		if(1 != refreshPageFlag){
			getDataSource().setEditOuiInfo(editOuiInfo);
		}else{
			String elName = "ouiName_edit";
			String editOuiId = splitAhDataTableEditInfo(editOuiInfo,elName);
			boolean exist = false;
			for(int i=0; i<ouiNames.length; i++){
				if(editOuiId.equals(ouiNames[i])){
					exist = true;
					break;
				}
			}
			if(exist){
				for(int i=0; i<ouiNames.length; i++){
					String ouiId = checkRemoveInitDefaultValue(ouiNames[i]);
					if(!ouiNames[i].equals(ouiId)){
						MacOrOui macOrOui = QueryUtil.findBoById(MacOrOui.class, Long.parseLong(ouiId));
						editOuiInfo = changeAhDataTableEditInfo(editOuiInfo,elName,macOrOui.getId(),macOrOui.getMacOrOuiName());
						break;
					}
				}
			}else{
				List<CheckItem> list = getAllMacOui();
				editOuiInfo = changeAhDataTableEditInfo(editOuiInfo,elName,list.get(0).getId(),list.get(0).getValue());
			}
			
			getDataSource().setEditOuiInfo(editOuiInfo);
			
		}
		getDataSource().setOuiNames(ouiNames);
		getDataSource().setOuiQosClasses(ouiQosClasses);
		getDataSource().setOuiFilterActions(ouiFilterActions);
		getDataSource().setOuiLoggings(ouiLoggings);
		getDataSource().setOuiComments(ouiComments);
	}
	
	private void prepareAhOuiDataTableData() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getQosMacOuis() == null 
				|| getDataSource().getQosMacOuis().values().isEmpty()){
			ahOuiDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		List<QosMacOui> orderedQosMacOuis = getDataSource().getOrderedQosMacOuis();
		for(QosMacOui qosMacOui: orderedQosMacOuis){
			JSONObject jsonObject = new JSONObject();
			if(qosMacOui.getMacOui() != null){
				jsonObject.put("ouiNames", qosMacOui.getMacOui().getId());
			}else{
				jsonObject.put("ouiNames", "");
			}
			jsonObject.put("ouiQosClasses", qosMacOui.getQosClassMacOuis());		
			jsonObject.put("ouiFilterActions", qosMacOui.getFilterActionMacOuis());
			jsonObject.put("ouiLoggings", qosMacOui.getLoggingMacOuis());
			jsonObject.put("ouiComments", qosMacOui.getComment());
			jsonArray.put(jsonObject);
		}
		ahOuiDtDatas = jsonArray.toString();
	}
	
	private void prepareAhOuiDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getOuiNames() == null){
			ahOuiDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		if(isFullMode()){
			for(int i=0; i<getDataSource().getOuiNames().length; i++){
				if(!"".equals(getDataSource().getOuiNames()[i])){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("ouiNames", isFullMode() ? getDataSource().getOuiNames()[i] : checkRemoveInitDefaultValue(getDataSource().getOuiNames()[i]));
					jsonObject.put("ouiQosClasses", getDataSource().getOuiQosClasses()[i]);		
					jsonObject.put("ouiFilterActions", getDataSource().getOuiFilterActions()[i]);
					jsonObject.put("ouiLoggings", getDataSource().getOuiLoggings()[i]);
					jsonObject.put("ouiComments", getDataSource().getOuiComments()[i]);
					jsonArray.put(jsonObject);
				}
				}
		}else{
			for(int i=0; i<getDataSource().getOuiNames().length; i++){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ouiNames", isFullMode() ? getDataSource().getOuiNames()[i] : checkRemoveInitDefaultValue(getDataSource().getOuiNames()[i]));
				jsonObject.put("ouiQosClasses", getDataSource().getOuiQosClasses()[i]);		
				jsonObject.put("ouiFilterActions", getDataSource().getOuiFilterActions()[i]);
				jsonObject.put("ouiLoggings", getDataSource().getOuiLoggings()[i]);
				jsonObject.put("ouiComments", getDataSource().getOuiComments()[i]);
				jsonArray.put(jsonObject);
			}
		}
		
		ahOuiDtDatas = jsonArray.toString();
	}
	
	private String splitAhDataTableEditInfo(String editInfo, String elName){
		if(editInfo == null || "".equals(editInfo)) return editInfo;
		if(elName == null || "".equals(elName)) return editInfo;
		
		int index = editInfo.lastIndexOf(elName);
		if(index == -1){
			return editInfo;
		}
		String afterElStr = editInfo.substring(index);
		String editContent = afterElStr.substring(elName.length()+3, afterElStr.indexOf("\","));
		return editContent;
		
	}
	
	private String changeAhDataTableEditInfo(String editInfo, String elName,Long key,String value){
		if(editInfo == null || "".equals(editInfo)) return editInfo;
		if(elName == null || "".equals(elName)) return editInfo;
		
		String newContent = ":[\""+key+"\",\""+value+"\"]";
		int index = editInfo.lastIndexOf(elName);
		if(index == -1){
			return editInfo;
		}
		String frontContent = editInfo.substring(0,index+elName.length());
		String afterElStr = editInfo.substring(index);
		String lastContent = afterElStr.substring(afterElStr.indexOf("\"]")+2);
	
		return frontContent+newContent+lastContent;
		
	}
	
	
	private boolean checkMacOuis(){
		if(getDataSource() != null && getDataSource().getMacOuisEnabled()){
			if(ouiNames == null || (ouiNames != null && ouiNames.length==1 && ouiNames[0].equals(""))){
				addActionError("Please add at least one item in MAC OUIs.");
				return false;
			}
		}
		if(ouiNames != null){
			List<Integer> list = new ArrayList<>();
			List<String> nameList = new ArrayList<String>();;
			for(int i=0; i<ouiNames.length; i++){
				if( !("".equals(ouiNames[i]) || null == ouiNames[i])){
					nameList.add(ouiNames[i]);
				}
			}
			if(null != nameList && !nameList.isEmpty()){
				for(int i=0 ; i<nameList.size(); i++){
					String ouiName = nameList.get(i);
					for(int j=i+1;j<nameList.size();j++){
						if(ouiName.equals(nameList.get(j))){
							list.add(j+1);
							break;
						}
					}
				}
			}
			if(list.size() > 1){
				addActionError("Some MAC OUIs in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The MAC OUI in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}
		}
		return true;
	}
	
	private void prepareAhSSIDDataTableColumnDefs() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		
		column = new AhDataTableColumn();
		column.setMark("ssidNames");
		column.setOptions(getAllSSIDS());
		column.setDefaultValue(getAllSSIDS().isEmpty() ? "" : getAllSSIDS().get(0).getId());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ssidQosClasses");
		column.setOptions(getAllQosClass());
		column.setDefaultValue(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		ahDataTableColumns.add(column);
		
		ahSSIDDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	} 
	
	private void prepareTransientSSIDTableData(){
		getDataSource().setEditSSIDInfo(editSSIDInfo);
		getDataSource().setSsidNames(ssidNames);
		getDataSource().setSsidQosClasses(ssidQosClasses);
	}
	
	private void prepareAhSSIDDataTableData() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getQosSsids() == null 
				|| getDataSource().getQosSsids().values().isEmpty()){
			ahSSIDDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		List<QosSsid> orderedQosSsids = getDataSource().getOrderedQosSsids();
		for(QosSsid qsid: orderedQosSsids){
			JSONObject jsonObject = new JSONObject();
			if(qsid.getSsid() != null){
				jsonObject.put("ssidNames", qsid.getSsid().getId());
			}else{
				jsonObject.put("ssidNames", "");
			}
			jsonObject.put("ssidQosClasses", qsid.getQosClassSsids());
			jsonArray.put(jsonObject);
		}
		ahSSIDDtDatas = jsonArray.toString();
	}
	
	private void prepareAhSSIDDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getSsidNames() == null){
			ahSSIDDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<getDataSource().getSsidNames().length; i++){
			if(!"".equals(getDataSource().getSsidNames()[i])){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ssidNames", getDataSource().getSsidNames()[i]);
				jsonObject.put("ssidQosClasses", getDataSource().getSsidQosClasses()[i]);
				jsonArray.put(jsonObject);
			}
		}
		ahSSIDDtDatas = jsonArray.toString();
	}
	
	private boolean checkSSIDs(){
		if(getDataSource() != null && getDataSource().getSsidEnabled()){
			if(ssidNames == null || (ssidNames != null && ssidNames.length==1 && ssidNames[0].equals(""))){
				addActionError("Please add at least one item in SSID.");
				return false;
			}
		}
		if(ssidNames != null){
			List<Integer> list = new ArrayList<>();
			List<String> nameList = new ArrayList<String>();;
			for(int i=0; i<ssidNames.length; i++){
				if( !("".equals(ssidNames[i]) || null == ssidNames[i])){
					nameList.add(ssidNames[i]);
				}
			}
			for(String name : nameList){
				if("".equals(name) || null == name){
					nameList.remove(name);
				}
			}
			if(null != nameList && !nameList.isEmpty()){
				for(int i=0 ; i<nameList.size(); i++){
					String ssidName = nameList.get(i);
					for(int j=i+1;j<nameList.size();j++){
						if(ssidName.equals(nameList.get(j))){
							list.add(j+1);
							break;
						}
					}
				}
			}
			if(list.size() > 1){
				addActionError("Some SSIDs in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The SSID in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}
		}
		return true;
	}
	
	private String checkRemoveInitDefaultValue(String defaultVal){
		List<CheckItem> list = getAllMacOui();
		String initVal = "";
		if(!"".equals(defaultVal) && null != list && !list.isEmpty()){
			List<Long> ids = new ArrayList<Long>();
			for(CheckItem ci : list){
				ids.add(ci.getId());
			}
			if(ids.contains(Long.parseLong(defaultVal))){
				initVal = defaultVal;
			}else{
				initVal = list.get(0).getId().toString();
			}
		}else{
			initVal = defaultVal;
		}
		return initVal;
	}
	
	private List<CheckItem> getAllMacOui(){
		List<CheckItem> allMacOuis = this.getBoCheckItems("macOrOuiName", MacOrOui.class, 
				new FilterParams("typeFlag",MacOrOui.TYPE_MAC_OUI));
		return allMacOuis.isEmpty() ? new ArrayList<CheckItem>() : allMacOuis;
	}
	
	private List<CheckItem> getAllSSIDS(){
		List<CheckItem> allSsids = this.getBoCheckItems("ssidName", SsidProfile.class, null);
		return allSsids.isEmpty() ? new ArrayList<CheckItem>() : allSsids;
	}
	
	private List<CheckItem> getAllQosClass(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : EnumConstUtil.ENUM_QOS_CLASS){
			CheckItem ci = new CheckItem((long)ei.getKey(),ei.getValue());
			list.add(ci);
		}
		return list;
	}
	
	private List<CheckItem> getAllFilterAction(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : MacFilter.ENUM_FILTER_ACTION){
			CheckItem ci = new CheckItem((long)ei.getKey(),ei.getValue());
			list.add(ci);
		}
		return list;
	}
	
	private List<CheckItem> getAllLogging(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : EnumConstUtil.enumLogging){
			CheckItem ci = new CheckItem((long)ei.getKey(),ei.getValue());
			list.add(ci);
		}
		return list;
	}
	
	private List<CheckItem> getSelectServiceOptions(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : EnumConstUtil.SERVICE_SELECT_OPTION){
			CheckItem ci = new CheckItem((long)ei.getKey(),ei.getValue());
			list.add(ci);
		}
		return list;
	}
	//for services table
	private String ahServiceDtClumnDefs;
	private String ahServiceDtDatas;
	private String editServiceInfo;
	
	private String[] serviceNames;
	private String[] serviceQosClasses;
	private String[] serviceLoggings;
	private String[] serviceFilterActions;

	//for mac oui table
	private String ahOuiDtClumnDefs;
	private String ahOuiDtDatas;
	private String editOuiInfo;
	
	private String[] ouiNames;
	private String[] ouiQosClasses;
	private String[] ouiLoggings;
	private String[] ouiFilterActions;
	private String[] ouiComments;
	
	//for ssid table
	private String ahSSIDDtClumnDefs;
	private String ahSSIDDtDatas;
	private String editSSIDInfo;
	
	private String[] ssidNames;
	private String[] ssidQosClasses;
	
	//for express mode refresh page flag
	private int refreshPageFlag;
	
	
	public int getRefreshPageFlag() {
		return refreshPageFlag;
	}

	public void setRefreshPageFlag(int refreshPageFlag) {
		this.refreshPageFlag = refreshPageFlag;
	}

	public String[] getSsidQosClasses() {
		return ssidQosClasses;
	}

	public void setSsidQosClasses(String[] ssidQosClasses) {
		this.ssidQosClasses = ssidQosClasses;
	}

	public String getAhOuiDtClumnDefs() {
		return ahOuiDtClumnDefs == null ? "" : ahOuiDtClumnDefs.replace("'", "\\'");
	}

	public void setAhOuiDtClumnDefs(String ahOuiDtClumnDefs) {
		this.ahOuiDtClumnDefs = ahOuiDtClumnDefs;
	}

	public String getAhOuiDtDatas() {
		return ahOuiDtDatas == null ? "" : ahOuiDtDatas.replace("'", "\\'").replace("\\\"","\\\\\"");
	}

	public void setAhOuiDtDatas(String ahOuiDtDatas) {
		this.ahOuiDtDatas = ahOuiDtDatas;
	}

	public String getEditOuiInfo() {
		return editOuiInfo == null ? "" : editOuiInfo.replace("'", "\\'");
	}

	public void setEditOuiInfo(String editOuiInfo) {
		this.editOuiInfo = editOuiInfo;
	}

	public String[] getOuiNames() {
		return ouiNames;
	}

	public void setOuiNames(String[] ouiNames) {
		this.ouiNames = ouiNames;
	}

	public String[] getOuiQosClasses() {
		return ouiQosClasses;
	}

	public void setOuiQosClasses(String[] ouiQosClasses) {
		this.ouiQosClasses = ouiQosClasses;
	}

	public String[] getOuiLoggings() {
		return ouiLoggings;
	}

	public void setOuiLoggings(String[] ouiLoggings) {
		this.ouiLoggings = ouiLoggings;
	}

	public String[] getOuiFilterActions() {
		return ouiFilterActions;
	}

	public void setOuiFilterActions(String[] ouiFilterActions) {
		this.ouiFilterActions = ouiFilterActions;
	}

	public String[] getOuiComments() {
		return ouiComments;
	}

	public void setOuiComments(String[] ouiComments) {
		this.ouiComments = ouiComments;
	}

	public String getAhSSIDDtClumnDefs() {
		return ahSSIDDtClumnDefs == null ? "" : ahSSIDDtClumnDefs.replace("'", "\\'");
	}

	public void setAhSSIDDtClumnDefs(String ahSSIDDtClumnDefs) {
		this.ahSSIDDtClumnDefs = ahSSIDDtClumnDefs;
	}

	public String getAhSSIDDtDatas() {
		return ahSSIDDtDatas == null ? "" : ahSSIDDtDatas.replace("'", "\\'");
	}

	public void setAhSSIDDtDatas(String ahSSIDDtDatas) {
		this.ahSSIDDtDatas = ahSSIDDtDatas;
	}

	public String getEditSSIDInfo() {
		return editSSIDInfo == null ? "" : editSSIDInfo.replace("'", "\\'");
	}

	public void setEditSSIDInfo(String editSSIDInfo) {
		this.editSSIDInfo = editSSIDInfo;
	}

	public String[] getSsidNames() {
		return ssidNames;
	}

	public void setSsidNames(String[] ssidNames) {
		this.ssidNames = ssidNames;
	}

	public String getEditServiceInfo() {
		return editServiceInfo == null ? "" : editServiceInfo.replace("'", "\\'");
	}

	public void setEditServiceInfo(String editServiceInfo) {
		this.editServiceInfo = editServiceInfo;
	}

	public String getAhServiceDtClumnDefs() {
		return ahServiceDtClumnDefs == null ? "" : ahServiceDtClumnDefs.replace("'", "\\'");
	}

	public void setAhServiceDtClumnDefs(String ahServiceDtClumnDefs) {
		this.ahServiceDtClumnDefs = ahServiceDtClumnDefs;
	}

	public String getAhServiceDtDatas() {
		return ahServiceDtDatas == null ? "" : ahServiceDtDatas.replace("'", "\\'");
	}

	public void setAhServiceDtDatas(String ahServiceDtDatas) {
		this.ahServiceDtDatas = ahServiceDtDatas;
	}

	public String[] getServiceNames() {
		return serviceNames;
	}

	public void setServiceNames(String[] serviceNames) {
		this.serviceNames = serviceNames;
	}

	public String[] getServiceQosClasses() {
		return serviceQosClasses;
	}

	public void setServiceQosClasses(String[] serviceQosClasses) {
		this.serviceQosClasses = serviceQosClasses;
	}

	public String[] getServiceLoggings() {
		return serviceLoggings;
	}

	public void setServiceLoggings(String[] serviceLoggings) {
		this.serviceLoggings = serviceLoggings;
	}

	public String[] getServiceFilterActions() {
		return serviceFilterActions;
	}

	public void setServiceFilterActions(String[] serviceFilterActions) {
		this.serviceFilterActions = serviceFilterActions;
	}
	
	private boolean onloadSelectedService;
	
	public boolean isOnloadSelectedService() {
		return onloadSelectedService;
	}

	public void setOnloadSelectedService(boolean onloadSelectedService) {
		this.onloadSelectedService = onloadSelectedService;
	}
	
	private List<CustomApplication> unSelectedCustomAppList;

	public List<CustomApplication> getUnSelectedCustomAppList() {
		return unSelectedCustomAppList;
	}

	public void setUnSelectedCustomAppList(
			List<CustomApplication> unSelectedCustomAppList) {
		this.unSelectedCustomAppList = unSelectedCustomAppList;
	}
	
	private List<String> allGroupNames = new ArrayList<>();
	
	public List<String> getAllGroupNames() {
		return allGroupNames;
	}

	public void setAllGroupNames(List<String> allGroupNames) {
		this.allGroupNames = allGroupNames;
	}
}