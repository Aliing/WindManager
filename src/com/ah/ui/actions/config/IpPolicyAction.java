/**
 *@filename		IpPolicyAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-19 AM 01:58:12
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.service.ApplicationService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.NetworkService;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class IpPolicyAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final short MAX_RULE_SIZE = 64;
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && "continue".equals(operation)) {
				setParentDomID(getDataSource().getParentDomID());
				setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.ipPolicy.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new IpPolicy());
				storeJsonContext();
				prepareAhIpPolicyDataTableColumnDefs();
				return isJsonMode() ? "ipPolicyDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				prepareAhIpPolicyDataTableColumnDefs();
				if (checkNameExists("policyName", getDataSource()
						.getPolicyName()) || checkTheSameNetExist()) {
					prepareTransientIpPolicyTableData();
					prepareAhIpPolicyDataTableDataAfterEdit();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
						return "json";
					} else {
						return isJsonMode() ? "ipPolicyDlg" : INPUT;
					}
				}
				if(!checkRules()){
					prepareTransientIpPolicyTableData();
					prepareAhIpPolicyDataTableDataAfterEdit();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
						return "json";
					} else {
						return isJsonMode() ? "ipPolicyDlg" : INPUT;
					}	
				}
				updateRules();
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					
					try {
						jsonObject = new JSONObject();
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("id", id);
						jsonObject.put("parentDomID",getParentDomID());
						jsonObject.put("name", getDataSource().getPolicyName());
						jsonObject.put("resultStatus",true);
					}catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";

				} else {
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				prepareAhIpPolicyDataTableColumnDefs();
				String strForward = editBo(this);
				prepareAhIpPolicyDataTableData(getDataSource());
				if (null != dataSource) {
					addLstTitle(getText("config.title.ipPolicy.edit")
							+ " '" + getChangedPolicyName() + "'");
					if (getDataSource().getRules().size() == 0) {
						hideCreateItem = "";
						hideNewButton = "none";
					}
				}
				storeJsonContext();
				return isJsonMode() ? "ipPolicyDlg" : strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if(!isFullMode() && 1 == refreshPageFlag ){
					prepareAhIpPolicyDataTableColumnDefs();
					prepareTransientIpPolicyTableData();
					prepareContinueAhIpPolicyDataTableDataAfterEdit();
					storeJsonContext();
					refreshPageFlag = 2;
					if(null != id){
						addLstTitle(getText("config.title.ipPolicy.edit")
								+ " '" + getChangedPolicyName() + "'");
					}else{
						addLstTitle(getText("config.title.ipPolicy.new"));
						getDataSource().setPolicyName(getDataSource().getPolicyName());
						getDataSource().setDescription(getDataSource().getDescription());
					}
					updateRules();
					return isJsonMode() ? "ipPolicyDlg" : INPUT;
				}else{
					prepareAhIpPolicyDataTableColumnDefs();
					if(!checkRules()){
						prepareTransientIpPolicyTableData();
						prepareAhIpPolicyDataTableDataAfterEdit();
						if (isJsonMode() && !isParentIframeOpenFlg()) {
							jsonObject = new JSONObject();
							jsonObject.put("resultStatus",false);
							jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
							return "json";
						} else {
							return isJsonMode() ? "ipPolicyDlg" : INPUT;
						}	
					}
					if (dataSource != null) {
						updateRules();
						if (checkTheSameNetExist()) {
							prepareTransientIpPolicyTableData();
							prepareAhIpPolicyDataTableDataAfterEdit();
							if (isJsonMode() && !isParentIframeOpenFlg()) {
								jsonObject = new JSONObject();
								jsonObject.put("resultStatus",false);
								jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
								return "json";
							} else {
								return isJsonMode() ? "ipPolicyDlg" : INPUT;
							}
						}
					}
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						if (isJsonMode() && !isParentIframeOpenFlg()) {
							jsonObject = new JSONObject();
							updateBo(dataSource);
							setUpdateContext(true);
							jsonObject.put("resultStatus",true);
							return "json";
						}
						
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				IpPolicy profile = (IpPolicy) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setPolicyName("");
				profile.setDefaultFlag(false);
				List<IpPolicyRule> newrule = new ArrayList<IpPolicyRule>();
				newrule.addAll(profile.getRules());
				profile.setRules(newrule);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.ipPolicy.new"));
				prepareAhIpPolicyDataTableColumnDefs();
				prepareAhIpPolicyDataTableData(profile);
				return INPUT;
			} else if ("newSourceIpAddress".equals(operation) || "newDestIpAddress".equals(operation) 
					|| "editSourceIpAddress".equals(operation) || "editDestIpAddress".equals(operation)
					|| "newService".equals(operation) || "editService".equals(operation)) {
				addLstForward("ipPolicy");
				setParentDomID(operation);
				clearErrorsAndMessages();
				if ("editSourceIpAddress".equals(operation) || "editDestIpAddress".equals(operation)) {
					operation = "editIpAddress";
				}
				if("newSourceIpAddress".equals(operation) || "newDestIpAddress".equals(operation)){
					operation = "newIpAddress";
				}
				prepareTransientIpPolicyTableData();
				
				return operation;
			} else if ("continue".equals(operation)) {
				prepareAhIpPolicyDataTableColumnDefs();
				prepareContinueAhIpPolicyDataTableDataAfterEdit();
				
				editIpPolicyInfo = getDataSource().getEditIpPolicyInfo();
				
				if("newSourceIpAddress".equals(getParentDomID()) || "newDestIpAddress".equals(getParentDomID())){
					if(ipAddressId != null){
						IpAddress ipAddress = QueryUtil.findBoById(IpAddress.class, ipAddressId);
						if(ipAddress != null){
							String elName = "newSourceIpAddress".equals(getParentDomID()) ? "ipPolicySourceIp_edit" : "ipPolicyDestinationIp_edit";
							editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,elName,ipAddress.getId(),ipAddress.getAddressName());
						}
					}
				} 
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (dataSource == null) {
					String str = prepareBoList();
					return isJsonMode() ? "ipPolicyDlg" : str;
				} else {
					setId(dataSource.getId());
					return isJsonMode() ? "ipPolicyDlg" : INPUT;
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if("selectIpPolicyService".equals(operation)){
				initNetworkService();
				return "selectIpPolicyService";
			} else if("selectIpPolicyAppService".equals(operation)){
				initAppService();
				return "selectIpPolicyAppService";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		if (isEasyMode()) {
			setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
		} else {
			setSelectedL2Feature(L2_FEATURE_IP_POLICY);
		}
		setDataSource(IpPolicy.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_IP_POLICY;
	}

	public IpPolicy getDataSource() {
		return (IpPolicy) dataSource;
	}

	public int getPolicyNameLength() {
		return getAttributeLength("policyName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedPolicyName() {
		return getDataSource().getPolicyName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getGridCount() {
		return getDataSource().getRules().size() == 0 ? 3 : 0;
	}

	public EnumItem[] getEnumAction() {
		return IpPolicyRule.ENUM_IP_POLICY_ACTION;
	}
	
	public EnumItem[] getL7EnumAction() {
		return IpPolicyRule.L7_ENUM_IP_POLICY_ACTION;
	}

	public EnumItem[] getEnumDenyLog() {
		return IpPolicyRule.ENUM_POLICY_LOGGING_DENY;
	}

	public EnumItem[] getEnumPermitLog() {
		return IpPolicyRule.ENUM_POLICY_LOGGING_PERMIT;
	}
	
	public EnumItem[] getEnumDropLog() {
		return IpPolicyRule.ENUM_POLICY_TRAFFIC_LOGGING_DROP;
	}
	
	private String checkRemoveInitDefaultValue(String defaultVal){
		List<CheckItem> list = getAvailableIpAddress();
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
	
	public List<CheckItem> getAvailableIpAddress() {
		List<CheckItem> availableIpAddress = getBoCheckItems("addressName", IpAddress.class, new FilterParams("typeFlag != :s1 and typeFlag != :s2", new Object[]{IpAddress.TYPE_IP_RANGE,IpAddress.TYPE_WEB_PAGE}));
		availableIpAddress.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableIpAddress.add(0, new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.ipPolicy.any")));
		return availableIpAddress;
	}

	public List<CheckItem> getAvailableNetworkServices() {
		List<CheckItem> availableNetworkServices = new ArrayList<CheckItem>();
		List<CheckItem> networkServices = getBoCheckItems("serviceName", NetworkService.class, null);
		List<NetworkService> list = QueryUtil.executeQuery(NetworkService.class, null, null,domainId);
		for (CheckItem service : networkServices) {
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
		availableNetworkServices.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableNetworkServices.add(0, new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.ipPolicy.any")));
		return availableNetworkServices;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		IpPolicy source = QueryUtil.findBoById(IpPolicy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<IpPolicy> list = QueryUtil.executeQuery(IpPolicy.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (IpPolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			IpPolicy up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPolicyName(profile.getPolicyName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<IpPolicyRule> newrule = new ArrayList<IpPolicyRule>();
			newrule.addAll(source.getRules());
			up.setRules(newrule);
			hmBos.add(up);
		}
		return hmBos;
	}

	/**
	 * Check the same Protocol Number and Port Number network service exist in list.
	 *
	 *@return boolean
	 */
	private boolean checkTheSameNetExist() {
		if (null != getDataSource().getRules()) {
			List<IpPolicyRule> allRules = getDataSource().getRules();
			List<IpPolicyRule> removeRules = new ArrayList<IpPolicyRule>();
			for (IpPolicyRule rule : allRules) {
				NetworkService netService = rule.getNetworkService();
				removeRules.add(rule);
				if (null != netService) {
						for (IpPolicyRule compareRule : allRules) {
							if (removeRules.contains(compareRule))
								continue;
							NetworkService nService = compareRule.getNetworkService();
							if(null != netService && null != nService && netService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK && 
									nService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
								if (null != nService && !netService.getServiceName().equals(nService.getServiceName())) {
									if (netService.getProtocolNumber() == nService.getProtocolNumber()
											&& netService.getPortNumber() == nService.getPortNumber()) {
										addActionError(MgrUtil.getUserMessage("error.config.classifier.network.service", "Policy Rule cannot contain "));
										return true;
									}
								}
							}
					}
				}
			}
		}
		return false;
	}
	
	protected boolean addSelectedRules() {
		List<Long> temp = new ArrayList<Long>(serviceIds.size());
		List<String> l7services = new ArrayList<String>(serviceIds.size());
		for(Long serviceId : serviceIds){
			NetworkService networkService = QueryUtil.findBoById(NetworkService.class, serviceId);
			if (networkService == null) {
				continue;
			}
			if(networkService.getServiceType() != NetworkService.SERVICE_TYPE_L7){
				continue;
			}else{
				if(filterAction == IpPolicyRule.POLICY_ACTION_NAT || filterAction == IpPolicyRule.POLICY_ACTION_TRAFFIC_DROP){
					temp.add(serviceId);
					l7services.add(networkService.getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length()));
				}
			}
		}
		if(null != l7services && l7services.size() >0){
			String action = MgrUtil.getEnumString("enum.ipPolicyAction."+filterAction);
			if(l7services.size()>1){
				StringBuffer sb = new StringBuffer();
				sb.append("");
				for(int i=0; i<l7services.size(); i++){
					sb.append(l7services.get(i));
					sb.append(", ");
				}
				addActionMessage(MgrUtil.getUserMessage("l7.firewall.service.action.deny.message.more",
						new String[]{sb.toString().substring(0, sb.toString().length()-2), action}));
			}else{
				addActionMessage(MgrUtil.getUserMessage("l7.firewall.service.action.deny.message.one",
						new String[]{l7services.get(0), action}));
			}
		}
		if(null != temp && temp.size() > 0){
			serviceIds.removeAll(temp);
		}
		int count = sourceIpIds.size() * destIpIds.size() * serviceIds.size();
		int oldCount = getDataSource().getRules().size();
		if (oldCount + count > 64) {
			addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
			hideCreateItem = "";
			hideNewButton = "none";
			return false;
		}

		IpPolicyRule ipPolicyRule;
		short i = 1;

		for (Long sourceIpId : sourceIpIds) {
			IpAddress sourceIp = null;
			if (sourceIpId > -1) {
				sourceIp = QueryUtil.findBoById(IpAddress.class,
					sourceIpId);
			}
			for (Long destIpId : destIpIds) {
				IpAddress desctinationIp = null;
				if (destIpId > -1) {
					desctinationIp = QueryUtil.findBoById(
						IpAddress.class, destIpId);
				}
				for (Long serviceId : serviceIds) {
					NetworkService networkService = null;
					ipPolicyRule = new IpPolicyRule();
					if (serviceId > -1) {
						networkService = QueryUtil.findBoById(
							NetworkService.class, serviceId);
					}
					if (getDataSource().getRules() != null) {
						boolean bool = false;
						boolean boolNet = false;
						for (IpPolicyRule rule : getDataSource().getRules()) {
							
							NetworkService netService = rule.getNetworkService();
							if(null != networkService && null != netService && 
									networkService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK 
									&& netService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK){
								if (null != networkService && null != netService &&  
										!netService.getServiceName().equals(networkService.getServiceName())) {
									if (networkService.getProtocolNumber() == netService.getProtocolNumber()
											&& networkService.getPortNumber() == netService.getPortNumber()) {
										boolNet = true;
										break;
									}
								}
							}
							
							IpAddress sourceRulIp = rule.getSourceIp();
							IpAddress destRuleIp = rule.getDesctinationIp();
							if ((sourceIp == sourceRulIp || (null != sourceIp
									&& null != sourceRulIp && sourceIp.getAddressName()
									.equals(sourceRulIp.getAddressName())))
									&& (desctinationIp == destRuleIp || (null != desctinationIp
											&& null != destRuleIp && desctinationIp
											.getAddressName().equals(destRuleIp.getAddressName())))
									&& (networkService == netService || (null != networkService
											&& null != netService && networkService
											.getServiceName().equals(netService
													.getServiceName())))) {
								bool = true;
								break;
							}
						}
						if (boolNet) {
							hideCreateItem = "";
							hideNewButton = "none";
							addActionError(MgrUtil.getUserMessage(
										"error.config.classifier.network.service", "You cannot add "));
							return false;
						}
						if (bool) {
							if (count == 1) {
								hideCreateItem = "";
								hideNewButton = "none";
								addActionError(MgrUtil
									.getUserMessage("error.addObjectExists"));
								return false;
							} else
								continue;
						}						
						for (int l = 1; l < 81; l++) {
							int k = 0;
							for (IpPolicyRule rule : getDataSource().getRules()) {
								if (l != rule.getRuleId())
									k++;
							}
							if (k == getDataSource().getRules().size()) {
								ipPolicyRule.setRuleId((short) l);
								break;
							}
						}
					} else {
						ipPolicyRule.setRuleId(i++);
					}
					ipPolicyRule.setSourceIp(sourceIp);
					ipPolicyRule.setDesctinationIp(desctinationIp);
					ipPolicyRule.setNetworkService(networkService);
					ipPolicyRule.setFilterAction(filterAction);
					if (IpPolicyRule.POLICY_ACTION_NAT != filterAction) {
						ipPolicyRule.setActionLog(actionLog);
					}
					getDataSource().getRules().add(ipPolicyRule);
				}
			}
		}
		if (oldCount + count > getDataSource().getRules().size()) {
			if (oldCount == getDataSource().getRules().size()) {
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
			} else {
				addActionError(MgrUtil.getUserMessage("error.addSomeObjectExists"));
			}		
			hideCreateItem = "";
			hideNewButton = "none";
			return false;
		}
		sourceIpIds = null;
		destIpIds = null;
		serviceIds = null;
		filterAction = IpPolicyRule.POLICY_ACTION_DENY;
		actionLog = IpPolicyRule.POLICY_LOGGING_OFF;
		return true;
	}

	protected void updateRules() {
		if(!getDataSource().getRules().isEmpty()){
			getDataSource().getRules().removeAll(getDataSource().getRules());
		}
		
		if(ruleIds == null){
			getDataSource().setRules(new ArrayList<IpPolicyRule>());
		}else {
			List<Short> usedRuleIds = new ArrayList<Short>();
			for (int i = 0; i < ruleIds.length; i++) {
				if(ruleIds[i] != null && !"".equals(ruleIds[i])){
					usedRuleIds.add(Short.valueOf(ruleIds[i]));
				}
			}
			for(int i=0; i<ruleIds.length; i++){
				if("".equals(ipPolicySourceIps[i]) || null == ipPolicySourceIps[i]){
					continue;
				}
				if("".equals(serviceNames[i]) || null == serviceNames[i] || serviceNames[i].indexOf("value") != -1){
					continue;
				}
				IpPolicyRule ipPolicyRule = new IpPolicyRule();
				IpAddress sourceIp = QueryUtil.findBoById(IpAddress.class,Long.parseLong(ipPolicySourceIps[i]));
				IpAddress desctinationIp = QueryUtil.findBoById(IpAddress.class,Long.parseLong(ipPolicyDestinationIps[i]));
				String appType = serviceNames[i].substring(serviceNames[i].length()-1);
				String serviceId =  serviceNames[i].substring(0, serviceNames[i].length()-1);
				NetworkService networkService = null;
				CustomApplication customApp = null;
				if(null != appType && !"".equals(appType)){
					if("0".equals(appType)){
						networkService = QueryUtil.findBoById(NetworkService.class,Long.parseLong(serviceId));
						ipPolicyRule.setServiceType(IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
					}else{
						customApp = QueryUtil.findBoById(CustomApplication.class,Long.parseLong(serviceId));
						ipPolicyRule.setServiceType(IpPolicyRule.RULE_CUSTOMSERVICE_TYPE);
					}
				}
				ipPolicyRule.setNetworkService(networkService);
				ipPolicyRule.setCustomApp(customApp);
				ipPolicyRule.setSourceIp(sourceIp);
				ipPolicyRule.setDesctinationIp(desctinationIp);
				ipPolicyRule.setFilterAction(Short.parseShort(ipPolicyFilterActions[i]));
				ipPolicyRule.setActionLog(Short.parseShort(ipPolicyLoggings[i]));
				// set rule id
				if(ruleIds[i] == null || "".equals(ruleIds[i])){
					for (short l = 1; l < 101; l++) {
						if(usedRuleIds.contains(l)){
							continue;
						}
						ipPolicyRule.setRuleId(l);
						usedRuleIds.add(l);
						break;
					}
				} else {
					ipPolicyRule.setRuleId(Short.valueOf(ruleIds[i]));
				}
				getDataSource().getRules().add(ipPolicyRule);
			}
		}
	}

	private List<Long> sourceIpIds;

	private List<Long> destIpIds;

	private List<Long> serviceIds;
	
	private Long ipAddressId;
	
	private Long netServiceId;

	private short filterAction = IpPolicyRule.POLICY_ACTION_DENY;

	private short actionLog;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public void setSourceIpIds(List<Long> sourceIpIds) {
		this.sourceIpIds = sourceIpIds;
	}

	public void setDestIpIds(List<Long> destIpIds) {
		this.destIpIds = destIpIds;
	}

	public void setServiceIds(List<Long> serviceIds) {
		this.serviceIds = serviceIds;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	public void setActionLog(short actionLog) {
		this.actionLog = actionLog;
	}

	public List<Long> getSourceIpIds() {
		return sourceIpIds;
	}

	public List<Long> getDestIpIds() {
		return destIpIds;
	}

	public List<Long> getServiceIds() {
		return serviceIds;
	}

	public short getFilterAction() {
		return filterAction;
	}

	public short getActionLog() {
		return actionLog;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof IpPolicy) {
			IpPolicy policy = (IpPolicy) bo;
			if (policy.getRules() != null)
				policy.getRules().size();
		}
		return null;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	/**
	 * get the description of column by id
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.ipPolicy.policyName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipPolicy.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public Long getIpAddressId() {
		return ipAddressId;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}

	public Long getNetServiceId() {
		return netServiceId;
	}

	public void setNetServiceId(Long netServiceId) {
		this.netServiceId = netServiceId;
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
	}
	
	//for policy rules table
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
		List<NetworkService> queryServiceList  = QueryUtil.executeQuery(NetworkService.class, null, new FilterParams("servicetype = :s1 and (owner.id = :s2 or owner.id = :s3)",
				new Object[]{NetworkService.SERVICE_TYPE_NETWORK,getDomain().getId(),globalDomainId}));
		List<NetworkService> allServiceList  =  new ArrayList<NetworkService>();
		NetworkService nse = new NetworkService();
		nse.setServiceName(MgrUtil.getUserMessage("config.ipPolicy.any"));
		nse.setId((long)-1);
		allServiceList.add(nse);
		if(!queryServiceList.isEmpty()){
			allServiceList.addAll(queryServiceList);
		}
		selectedService = new ArrayList<NetworkService>();
		unSelectedService = new ArrayList<NetworkService>();
		selectedServiceNum = 0;
		
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
		selectedApp = new ArrayList<Application>();
		unSelectedApp = new ArrayList<Application>();
		selectedServiceNum = 0;
		
		List<NetworkService> appServiceList = QueryUtil.executeQuery(NetworkService.class, null, new FilterParams("servicetype = :s1 and owner.id = :s2",
				new Object[]{NetworkService.SERVICE_TYPE_L7, getDomain().getId()}));
		
		List<Integer> appCodes = new ArrayList<Integer>();
		for(NetworkService ns : appServiceList){
			appCodes.add(ns.getAppId());
		}
		
		for(Application app : fixedAppList){
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
			if(null != allGroupNames && !allGroupNames.isEmpty()){
				if(!allGroupNames.contains(app.getAppGroupName())){
					allGroupNames.add(app.getAppGroupName());
				}
			}else{
				allGroupNames.add(app.getAppGroupName());
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
//			if(null != allGroupNames && !allGroupNames.isEmpty()){
//				if(!allGroupNames.contains(app.getAppGroupName())){
//					allGroupNames.add(app.getAppGroupName());
//				}
//			}else{
//				allGroupNames.add(app.getAppGroupName());
//			}
//		}
		unSelectedApp = fixedAppList;
		
		unSelectedCustomAppList = appService.getCustomApplicationWithBytes(getDomain());
		searchUnSelectedCustomAppList = new ArrayList<>();
		for(CustomApplication ca : unSelectedCustomAppList){
			ca.setAppType(IpPolicyRule.RULE_CUSTOMSERVICE_TYPE);
			String name = ca.getCustomAppName().replace("\\", "\\\\");
			searchUnSelectedCustomAppList.add(name);
		}
	}
	
	private void prepareAhIpPolicyDataTableColumnDefs() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		
		column = new AhDataTableColumn();
		column.setMark("ipPolicySourceIps");
		column.setOptions(getAvailableIpAddress());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ipPolicyDestinationIps");
		column.setOptions(getAvailableIpAddress());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("serviceNames");
		column.setOptions(getSelectServiceOptions());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ipPolicyFilterActions");
		column.setOptions(getAllFilterAction());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("ipPolicyLoggings");
		column.setOptions(getAllLogging());
		ahDataTableColumns.add(column);
		
		ahIpPolicyDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	} 
	
	private void prepareTransientIpPolicyTableData(){
		if(1 != refreshPageFlag){
			boolean checkBol = checkAhDataTableEditInfoServiceName(editIpPolicyInfo,"serviceName_edit");
			if(checkBol){
				getDataSource().setEditIpPolicyInfo(changeAhDataTableEditInfo(editIpPolicyInfo,"serviceName_edit",null,"undefined"));
			}else{
				getDataSource().setEditIpPolicyInfo(editIpPolicyInfo);
			}
			for(int i=0; i < serviceNames.length; i++){
				if(serviceNames[i].indexOf("value") != -1){
					serviceNames[i] = "";
				}
			}
			getDataSource().setServiceNames(serviceNames);
		}else{
			String elName = "ipPolicySourceIp_edit";
			String editIpId = splitAhDataTableEditInfo(editIpPolicyInfo,elName);
			boolean exist = false;
			for(int i=0; i<ipPolicySourceIps.length; i++){
				if(editIpId.equals(ipPolicySourceIps[i])){
					exist = true;
					break;
				}
			}
			if(exist){
				for(int i=0; i<ipPolicySourceIps.length; i++){
					String ipId = checkRemoveInitDefaultValue(ipPolicySourceIps[i]);
					if(!ipPolicySourceIps[i].equals(ipId)){
						IpAddress ipAddress = QueryUtil.findBoById(IpAddress.class, Long.parseLong(ipId));
						if(null != ipAddress){
							editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,elName,ipAddress.getId(),ipAddress.getAddressName());
						
						}else{
							editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,elName,(long)-1,MgrUtil.getUserMessage("config.ipPolicy.any"));
						}
						break;
					}
				}
			}else{
				List<CheckItem> list = getAvailableIpAddress();
				editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,elName,list.get(0).getId(),list.get(0).getValue());
			}
			
			String editDesIpId = splitAhDataTableEditInfo(editIpPolicyInfo,"ipPolicyDestinationIp_edit");
			boolean existDesIp = false;
			for(int i=0; i<ipPolicyDestinationIps.length; i++){
				if(editDesIpId.equals(ipPolicyDestinationIps[i])){
					existDesIp = true;
					break;
				}
			}
			if(existDesIp){
				for(int i=0; i<ipPolicyDestinationIps.length; i++){
					String ipId = checkRemoveInitDefaultValue(ipPolicyDestinationIps[i]);
					if(!ipPolicyDestinationIps[i].equals(ipId)){
						IpAddress ipAddress = QueryUtil.findBoById(IpAddress.class, Long.parseLong(ipId));
						if(null != ipAddress){
							editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,"ipPolicyDestinationIp_edit",ipAddress.getId(),ipAddress.getAddressName());
						
						}else{
							editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,"ipPolicyDestinationIp_edit",(long)-1,MgrUtil.getUserMessage("config.ipPolicy.any"));
						}
						break;
					}
				}
			}else{
				List<CheckItem> list = getAvailableIpAddress();
				editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,"ipPolicyDestinationIp_edit",list.get(0).getId(),list.get(0).getValue());
			}
			editIpPolicyInfo = changeAhDataTableEditInfo(editIpPolicyInfo,"serviceName_edit",null,"undefined");
			
			getDataSource().setEditIpPolicyInfo(editIpPolicyInfo);
			for(int i=0; i < serviceNames.length; i++){
				if(serviceNames[i].indexOf("value") != -1){
					serviceNames[i] = "";
				}
			}
			getDataSource().setServiceNames(serviceNames);
		}
		getDataSource().setRuleIds(ruleIds);
		getDataSource().setIpPolicySourceIps(ipPolicySourceIps);
		getDataSource().setIpPolicyDestinationIps(ipPolicyDestinationIps);
		getDataSource().setIpPolicyFilterActions(ipPolicyFilterActions);
		getDataSource().setIpPolicyLoggings(ipPolicyLoggings);
	}
	
	private void prepareAhIpPolicyDataTableData(IpPolicy ipPolicy) throws JSONException{ 
		if(ipPolicy == null || ipPolicy.getRules().isEmpty()){
			ahIpPolicyDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		List<IpPolicyRule> orderedRules = ipPolicy.getRules();
		for(IpPolicyRule policyRule: orderedRules){
			if(null != policyRule){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ruleIds", policyRule.getRuleId());
				jsonObject.put("ipPolicySourceIps", policyRule.getSourceIp()==null ? -1l : policyRule.getSourceIp().getId());
				jsonObject.put("ipPolicyDestinationIps", policyRule.getDesctinationIp()==null ? -1l : policyRule.getDesctinationIp().getId());		
				JSONObject snObj = new JSONObject();
				if(policyRule.getServiceType() == IpPolicyRule.RULE_NETWORKSERVICE_TYPE){
					if (policyRule.getNetworkService() == null) {
						snObj.put("value", "-1"+IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
						snObj.put("text", NetworkService.NETWORK_SERVICE + MgrUtil.getUserMessage("config.ipPolicy.any"));
					} else {
						snObj.put("value", policyRule.getNetworkService().getId().toString()+IpPolicyRule.RULE_NETWORKSERVICE_TYPE);
						String name = policyRule.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_NETWORK ? NetworkService.NETWORK_SERVICE + policyRule.getNetworkService().getServiceName() :
							NetworkService.APPLICATION_SERVICE + policyRule.getNetworkService().getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length());
						if(name.lastIndexOf("\\") != -1){
							name = name.substring(0, name.length() -1);
						}
						snObj.put("text", name);
					}
				}else{
					snObj.put("value", policyRule.getCustomApp().getId().toString()+IpPolicyRule.RULE_CUSTOMSERVICE_TYPE);
					String name = NetworkService.APPLICATION_SERVICE + policyRule.getCustomApp().getCustomAppName();
					if(name.lastIndexOf("\\") != -1){
						name = name.substring(0, name.length() -1);
					}
					snObj.put("text", name);
				}
				jsonObject.put("serviceNames", snObj);
				jsonObject.put("ipPolicyFilterActions", policyRule.getFilterAction());
				jsonObject.put("ipPolicyLoggings", policyRule.getActionLog());
				jsonArray.put(jsonObject);
			}
		}
		ahIpPolicyDtDatas = jsonArray.toString();
	}
	
	private void prepareAhIpPolicyDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getIpPolicySourceIps() == null){
			ahIpPolicyDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<getDataSource().getIpPolicySourceIps().length; i++){
			if(!"".equals(getDataSource().getServiceNames()[i]) && getDataSource().getServiceNames()[i].indexOf("value") == -1){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ruleIds", getDataSource().getRuleIds()[i]);
				jsonObject.put("ipPolicySourceIps", getDataSource().getIpPolicySourceIps()[i]);
				jsonObject.put("ipPolicyDestinationIps", getDataSource().getIpPolicyDestinationIps()[i]);
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
									snObj.put("text", MgrUtil.getUserMessage("config.ipPolicy.any"));
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
				jsonObject.put("ipPolicyFilterActions", getDataSource().getIpPolicyFilterActions()[i]);
				jsonObject.put("ipPolicyLoggings", getDataSource().getIpPolicyLoggings()[i]);
				jsonArray.put(jsonObject);
			}
		}
	
		ahIpPolicyDtDatas = jsonArray.toString();
	}
	
	private void prepareContinueAhIpPolicyDataTableDataAfterEdit() throws JSONException{ 
		if(getDataSource() == null || getDataSource().getIpPolicySourceIps() == null){
			ahIpPolicyDtDatas = "";
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<getDataSource().getIpPolicySourceIps().length; i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ruleIds", getDataSource().getRuleIds()[i]);
			jsonObject.put("ipPolicySourceIps", isFullMode() ? getDataSource().getIpPolicySourceIps()[i] : checkRemoveInitDefaultValue(getDataSource().getIpPolicySourceIps()[i]));
			jsonObject.put("ipPolicyDestinationIps", isFullMode() ? getDataSource().getIpPolicyDestinationIps()[i] : checkRemoveInitDefaultValue(getDataSource().getIpPolicyDestinationIps()[i]));
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
								snObj.put("text", MgrUtil.getUserMessage("config.ipPolicy.any"));
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
			jsonObject.put("ipPolicyFilterActions", getDataSource().getIpPolicyFilterActions()[i]);
			jsonObject.put("ipPolicyLoggings", getDataSource().getIpPolicyLoggings()[i]);
			jsonArray.put(jsonObject);
		}
		ahIpPolicyDtDatas = jsonArray.toString();
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
	
	private boolean checkAhDataTableEditInfoServiceName(String editInfo, String elName){
		if(editInfo == null || "".equals(editInfo)) return true;
		if(elName == null || "".equals(elName)) return true;
		int index = editInfo.lastIndexOf(elName);
		if(index == -1){
			return true;
		}
		String afterElStr = editInfo.substring(index);
		String serviceName = afterElStr.substring(0,afterElStr.indexOf("],")+2);
		if(serviceName.indexOf("undefined") == -1){
			return false;
		}
	
		return true;
		
	}
	
	private String changeAhDataTableEditInfo(String editInfo, String elName,Long key,String value){
		if(editInfo == null || "".equals(editInfo)) return editInfo;
		if(elName == null || "".equals(elName)) return editInfo;
		String newContent = "";
		if(key == null){
			newContent = ":[\"\",\""+value+"\"]";
		}else{
			newContent = ":[\""+key+"\",\""+value+"\"]";
		}
		int index = editInfo.lastIndexOf(elName);
		if(index == -1){
			return editInfo;
		}
		String frontContent = editInfo.substring(0,index+elName.length());
		String afterElStr = editInfo.substring(index);
		String lastContent = afterElStr.substring(afterElStr.indexOf("\"]")+2);
	
		return frontContent+newContent+lastContent;
		
	}
	
	private boolean checkRules(){
		if(ruleIds != null){
			if (ruleIds.length > MAX_RULE_SIZE) {
				String[] params = new String[]{String.valueOf(ruleIds.length), String.valueOf(MAX_RULE_SIZE)};
				addActionError(MgrUtil.getUserMessage("error.be.config.create.ippolicy.rule.maxAllow", params));
				return false;
			}
			List<String> nameList = new ArrayList<String>();
			List<String> sourceIpList = new ArrayList<String>();
			List<String> destIpList = new ArrayList<String>();
			List<String> filterActionList = new ArrayList<String>();
			for(int i=0; i<serviceNames.length; i++){
				if( !("".equals(serviceNames[i]) || null == serviceNames[i]
						|| serviceNames[i].indexOf("value") != -1)){
					nameList.add(serviceNames[i]);
					sourceIpList.add(ipPolicySourceIps[i]);
					destIpList.add(ipPolicyDestinationIps[i]);
					filterActionList.add(ipPolicyFilterActions[i]);
				}
			}
			List<Integer> list = new ArrayList<>();
			List<Integer> networklist = new ArrayList<>();
			List<Integer> applist = new ArrayList<>();
			if(null != nameList && !nameList.isEmpty()){
				for(int i=0 ; i<nameList.size(); i++){
					
					String ipPolicySourceIp = sourceIpList.get(i);
					String ipPolicyDestinationIp = destIpList.get(i);
					String ipPolicyServiceName = nameList.get(i);
					if("".equals(ipPolicySourceIp) || null == ipPolicySourceIp){
						continue;
					}
					String appType = ipPolicyServiceName.substring(ipPolicyServiceName.length()-1);
					String serviceId =  ipPolicyServiceName.substring(0, ipPolicyServiceName.length()-1);
					NetworkService networkService = null;
					CustomApplication customApp = null;
					if("0".equals(appType)){
						networkService = QueryUtil.findBoById(NetworkService.class, Long.parseLong(serviceId));
						for(int j=i+1;j<nameList.size();j++){
							String serviceNameT = nameList.get(j);
							String serviceIdT =  serviceNameT.substring(0, serviceNameT.length()-1);
							String appTypeT = serviceNameT.substring(serviceNameT.length()-1);
							if("0".equals(appTypeT)){
								NetworkService netService = QueryUtil.findBoById(NetworkService.class, Long.parseLong(serviceIdT));
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
								if(ipPolicySourceIp.equals(sourceIpList.get(j)) && ipPolicyDestinationIp.equals(destIpList.get(j))
										&& ipPolicyServiceName.equals(serviceNameT)){
									list.add(j+1);
									break;
								}
							}
						}
						if(networkService != null && networkService.getServiceType() == NetworkService.SERVICE_TYPE_L7 && 
								(Short.parseShort(filterActionList.get(i)) != IpPolicyRule.POLICY_ACTION_PERMIT &&
								Short.parseShort(filterActionList.get(i)) != IpPolicyRule.POLICY_ACTION_DENY)){
							applist.add(i+1);
						}
					}else{
						for(int j=i+1;j<nameList.size();j++){
							if(ipPolicySourceIp.equals(sourceIpList.get(j)) && ipPolicyDestinationIp.equals(destIpList.get(j))
									&& ipPolicyServiceName.equals(nameList.get(j))){
								list.add(j+1);
								break;
							}
						}
						customApp = QueryUtil.findBoById(CustomApplication.class, Long.parseLong(serviceId));
						if(customApp != null &&
								(Short.parseShort(filterActionList.get(i)) != IpPolicyRule.POLICY_ACTION_PERMIT &&
								Short.parseShort(filterActionList.get(i)) != IpPolicyRule.POLICY_ACTION_DENY)){
							applist.add(i+1);
						}
					}
				}
			}
			if (networklist.size() >0) {
				addActionError(MgrUtil.getUserMessage(
							"error.config.classifier.network.service", "You cannot add "));
				return false;
			}
			if(null != list && !list.isEmpty()){
				sortList(list);
			}
			if(list.size() > 1){
				addActionError("Some rules in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The rule in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}
			if(null != applist && !applist.isEmpty()){
				sortList(applist);
			}
			if (applist.size() >1) {
				addActionError("Some application services in ("+applist.toString()+") rows cannot be added because their actions can be only permit or deny.");
				return false;
			}else if(applist.size() == 1){
				addActionError("The application service in ("+applist.toString()+") row cannot be added because its action can be only permit or deny.");
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
	
	private List<CheckItem> getAllFilterAction(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : IpPolicyRule.ENUM_IP_POLICY_ACTION){
			CheckItem ci = new CheckItem((long)ei.getKey(),ei.getValue());
			list.add(ci);
		}
		return list;
	}
	
	private List<CheckItem> getAllLogging(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		for(EnumItem ei : IpPolicyRule.ENUM_POLICY_TRAFFIC_LOGGING_ALL){
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
	
		private String ahIpPolicyDtClumnDefs;
		private String ahIpPolicyDtDatas;
		private String editIpPolicyInfo;
		
		private String[] ipPolicySourceIps;
		private String[] ipPolicyDestinationIps;
		private String[] serviceNames;
		private String[] ipPolicyLoggings;
		private String[] ipPolicyFilterActions;
		private String[] ruleIds;
		//for express mode refresh page flag
		private int refreshPageFlag;
		
		
		public int getRefreshPageFlag() {
			return refreshPageFlag;
		}

		public void setRefreshPageFlag(int refreshPageFlag) {
			this.refreshPageFlag = refreshPageFlag;
		}

		public String[] getRuleIds() {
			return ruleIds;
		}

		public void setRuleIds(String[] ruleIds) {
			this.ruleIds = ruleIds;
		}

		public String getAhIpPolicyDtClumnDefs() {
			return ahIpPolicyDtClumnDefs == null ? "" : ahIpPolicyDtClumnDefs.replace("'", "\\'");
		}

		public void setAhIpPolicyDtClumnDefs(String ahIpPolicyDtClumnDefs) {
			this.ahIpPolicyDtClumnDefs = ahIpPolicyDtClumnDefs;
		}

		public String getAhIpPolicyDtDatas() {
			return ahIpPolicyDtDatas == null ? "" : ahIpPolicyDtDatas.replace("'", "\\'");
		}

		public void setAhIpPolicyDtDatas(String ahIpPolicyDtDatas) {
			this.ahIpPolicyDtDatas = ahIpPolicyDtDatas;
		}

		public String getEditIpPolicyInfo() {
			return editIpPolicyInfo == null ? "" : editIpPolicyInfo.replace("'", "\\'");
		}

		public void setEditIpPolicyInfo(String editIpPolicyInfo) {
			this.editIpPolicyInfo = editIpPolicyInfo;
		}

		public String[] getIpPolicySourceIps() {
			return ipPolicySourceIps;
		}

		public void setIpPolicySourceIps(String[] ipPolicySourceIps) {
			this.ipPolicySourceIps = ipPolicySourceIps;
		}

		public String[] getIpPolicyDestinationIps() {
			return ipPolicyDestinationIps;
		}

		public void setIpPolicyDestinationIps(String[] ipPolicyDestinationIps) {
			this.ipPolicyDestinationIps = ipPolicyDestinationIps;
		}

		public String[] getServiceNames() {
			return serviceNames;
		}

		public void setServiceNames(String[] serviceNames) {
			this.serviceNames = serviceNames;
		}

		public String[] getIpPolicyLoggings() {
			return ipPolicyLoggings;
		}

		public void setIpPolicyLoggings(String[] ipPolicyLoggings) {
			this.ipPolicyLoggings = ipPolicyLoggings;
		}

		public String[] getIpPolicyFilterActions() {
			return ipPolicyFilterActions;
		}

		public void setIpPolicyFilterActions(String[] ipPolicyFilterActions) {
			this.ipPolicyFilterActions = ipPolicyFilterActions;
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