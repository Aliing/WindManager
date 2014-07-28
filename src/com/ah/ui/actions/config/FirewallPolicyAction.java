/**
 *@filename		FirewallPolicyAction.java
 *@version
 *@author		Fiona
 *@createtime	2011-6-16 PM 10:03:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class FirewallPolicyAction extends BaseAction implements QueryBo
{
	private static final long serialVersionUID = 1L;
	
	private static final String FIREWALL_POLICY_LOCATION_FLAG = "FIREWALL_POLICY_LOCATION_FLAG";

	//private static final Tracer log = new Tracer(FirewallPolicyAction.class.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if (null == operation || L2_FEATURE_L3_FIREWALL_POLICY.equals(operation) || "newFw".equals(operation) || "editFw".equals(operation)) {
				if ("newFw".equals(operation) || "editFw".equals(operation)) {
					showUpInNavTree = false;
				}
				MgrUtil.setSessionAttribute(FIREWALL_POLICY_LOCATION_FLAG, showUpInNavTree);
			} else if (null != MgrUtil.getSessionAttribute(FIREWALL_POLICY_LOCATION_FLAG)) {
				showUpInNavTree = (Boolean)MgrUtil.getSessionAttribute(FIREWALL_POLICY_LOCATION_FLAG);
			}
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.firewallPolicy.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new FirewallPolicy());
				hideCreateItem = "";
				hideNewButton = "none";
				return INPUT;
			} else if ("newFw".equals(operation)) {
				setSessionDataSource(new FirewallPolicy());
				hideCreateItem = "";
				hideNewButton = "none";
				return operation;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateRules();
				if (checkNameExists("policyName", getDataSource()
						.getPolicyName()) || checkTheSameItemExist()
						|| checkTheSameNetExist()) {
					return showUpInNavTree ? INPUT : "newFw";
				}
				if ("create".equals(operation) && showUpInNavTree) {
					return createBo();
				} else {
					id = createBo(dataSource);
					if (showUpInNavTree) {
						setUpdateContext(true);
						return getLstForward();
					} else {
						return "newFw";
					}
				}
			} else if ("edit".equals(operation) || "editFw".equals(operation)) {
				String strForward = editBo(this);
				if (null != dataSource) {
					addLstTitle(getText("config.title.firewallPolicy.edit")
							+ " '" + getChangedPolicyName() + "'");
					if (getDataSource().getRules().size() == 0) {
						hideCreateItem = "";
						hideNewButton = "none";
					}
				}
				return showUpInNavTree ? strForward : "newFw";
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateRules();
					if (checkTheSameItemExist()) {
						operation = null;
						return showUpInNavTree ? INPUT : "newFw";
					}
					if (checkTheSameNetExist()) {
						operation = null;
						return showUpInNavTree ? INPUT : "newFw";
					}
					//id = dataSource.getId();
				}
				if ("update".equals(operation) && showUpInNavTree) {
					return updateBo();
				} else {
					updateBo(dataSource);
					if (showUpInNavTree) {
						setUpdateContext(true);
						return getLstForward();
					} else {
						return "newFw";
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				FirewallPolicy profile = (FirewallPolicy) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setPolicyName("");
				List<FirewallPolicyRule> newrule = new ArrayList<FirewallPolicyRule>();
				newrule.addAll(profile.getRules());
				profile.setRules(newrule);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.firewallPolicy.new"));
				return INPUT;
			} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)
					|| "newDestIpAddress".equals(operation) || "editDestIpAddress".equals(operation)
					|| "newService".equals(operation) || "editService".equals(operation)
					|| "newNetworkObj".equals(operation) || "editNetworkObj".equals(operation)
					|| "newDestNetworkObj".equals(operation) || "editDestNetworkObj".equals(operation)
					|| "newUserProfile".equals(operation) || "editUserProfile".equals(operation)) {
				updateRules();
				getDataSource().setSingleRule(getSingleRuleValue(false));
				if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)) {
					switch (sourceType) {
						case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
							radioIpOrName = "network";
							firewallIPType = "firewallNetwork";
							break;
						case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
							radioIpOrName = "range";
							firewallIPType = "firewallRange";
							break;
						case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
							radioIpOrName = "wildcard";
							firewallIPType = "firewallWildcard";
							break;
						default:
							break;
					}
				}
				if ("newDestIpAddress".equals(operation) || "editDestIpAddress".equals(operation)) {
					switch (destType) {
						case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
							radioIpOrName = "network";
							firewallIPType = "firewallNetwork";
							break;
						case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
							radioIpOrName = "range";
							firewallIPType = "firewallRange";
							break;
						case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
							radioIpOrName = "wildcard";
							firewallIPType = "firewallWildcard";
							break;
						case FirewallPolicy.FIREWALL_POLICY_TYPE_HOST:
							radioIpOrName = "name";
							firewallIPType = "firewallName";
							break;
						default:
							break;
					}
				}
				if (operation.contains("Dest")) {
					addLstForward("firewallPolicyDest");
				} else {
					addLstForward("firewallPolicy");
				}
				clearErrorsAndMessages();
				
				// new parameters
				if (!showUpInNavTree) {
					setContentShowType("dlg");
					setJsonMode(true);
					setParentIframeOpenFlg(true);
				}
				return operation;
			} else if ("continue".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				FirewallPolicyRule fwPolicyRule = getDataSource().getSingleRule();
				if (null != fwPolicyRule) {
					filterAction = fwPolicyRule.getFilterAction();
					actionLog = fwPolicyRule.getActionLog();
					disableRule = fwPolicyRule.isDisableRule();
					sourceType = fwPolicyRule.getSourceType();
					srIpAddressStr = "";
					destType = fwPolicyRule.getDestType();
					destIpAddressStr = "";
					if (null == sourceIpId && null != fwPolicyRule.getSourceIp()) {
						sourceIpId = fwPolicyRule.getSourceIp().getId();
					}
					if (null != sourceIpId) {
						IpAddress sourceIpObj = QueryUtil.findBoById(IpAddress.class, sourceIpId);
						if ((sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET && sourceIpObj.getTypeFlag() == 
							IpAddress.TYPE_IP_NETWORK) || (sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE && sourceIpObj.getTypeFlag() == 
								IpAddress.TYPE_IP_RANGE) || (sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD && sourceIpObj.getTypeFlag() == 
							IpAddress.TYPE_IP_WILDCARD)) {
							srIpAddressStr = sourceIpObj.getAddressName();
						}
					}
					if (null == sourceNetObjId && null != fwPolicyRule.getSourceNtObj()) {
						sourceNetObjId = fwPolicyRule.getSourceNtObj().getId();
					}
					if (null == sourceUpId && null != fwPolicyRule.getSourceUp()) {
						sourceUpId = fwPolicyRule.getSourceUp().getId();
					}
					if (null == destIpId && null != fwPolicyRule.getDestinationIp()) {
						destIpId = fwPolicyRule.getDestinationIp().getId();
					}
					if (null != destIpId) {
						IpAddress destIpObj = QueryUtil.findBoById(IpAddress.class, destIpId);
						if ((destType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET && destIpObj.getTypeFlag() == 
							IpAddress.TYPE_IP_NETWORK) || (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE && destIpObj.getTypeFlag() == 
								IpAddress.TYPE_IP_RANGE) || (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD && destIpObj.getTypeFlag() == 
							IpAddress.TYPE_IP_WILDCARD) || (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_HOST && destIpObj.getTypeFlag() == 
								IpAddress.TYPE_HOST_NAME)) {
							destIpAddressStr = destIpObj.getAddressName();
						}
					}
					if (null == destNetObjId && null != fwPolicyRule.getDestinationNtObj()) {
						destNetObjId = fwPolicyRule.getDestinationNtObj().getId();
					}
					if (null == netServiceId && null != fwPolicyRule.getNetworkService()) {
						netServiceId = fwPolicyRule.getNetworkService().getId();
					}
				}
				hideCreateItem = "";
				hideNewButton = "none";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					return showUpInNavTree ? INPUT : "newFw";
				}
			} else if ("addPolicyRules".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRules();
					addSelectedRules();
					return showUpInNavTree ? INPUT : "newFw";
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("changeSourceType".equals(operation) || "changeDestType".equals(operation)) {
				short objType = IpAddress.TYPE_IP_ADDRESS;
				switch ("changeSourceType".equals(operation)?sourceType:destType) {
					case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
						objType = IpAddress.TYPE_IP_NETWORK;
						break;
					case FirewallPolicy.FIREWALL_POLICY_TYPE_HOST:
						objType = IpAddress.TYPE_HOST_NAME;
						break;
					case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
						objType = IpAddress.TYPE_IP_RANGE;
						break;
					case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
						objType = IpAddress.TYPE_IP_WILDCARD;
						break;
				}
				List<CheckItem> objItem = getBoCheckItems("addressName", IpAddress.class, new FilterParams("typeFlag", objType),
					CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
				//objItem.remove(0);
				jsonObject = new JSONObject();
				jsonObject.put("tdId", "changeSourceType".equals(operation)?"sourceIpSelect":"destIpSelect");
				if (objItem.size() > 0) {
					JSONArray jsArray = new JSONArray();
					for (CheckItem item : objItem) {
						JSONObject jsObj = new JSONObject();
						jsObj.put("id", item.getId());
						jsObj.put("valueStr", item.getValue());
						jsArray.put(jsObj);
					}
					jsonObject.put("v", jsArray);
				}
				return "json";
			} else if ("fetchPolicyLs".equals(operation)) {
				if (dataSource != null) {
					if (null != getDataSource().getRules() && !getDataSource().getRules().isEmpty()) {
						jsonArray = new JSONArray();
						for (FirewallPolicyRule ruleInfo : getDataSource().getRules()) {
							JSONObject jsonObj = new JSONObject();
							// rule id
							jsonObj.put("ruleId", ruleInfo.getRuleId());
							// source
							jsonObj.put("srcStr", ruleInfo.getSourceStr());
							// destination
							jsonObj.put("destStr", ruleInfo.getDestStr());
							// network service
							if (null == ruleInfo.getNetworkService()) {
								jsonObj.put("service", MgrUtil.getUserMessage("config.ipPolicy.any"));
							} else {
								jsonObj.put("service", ruleInfo.getNetworkService().getServiceName());
							}
							jsonObj.put("action", ruleInfo.getFilterAction());
							jsonObj.put("log", ruleInfo.getActionLog());
							jsonObj.put("disable", ruleInfo.isDisableRule());
							jsonArray.put(jsonObj);
						}
					}
				}
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			if(!showUpInNavTree){
				addActionError(MgrUtil.getUserMessage(e));
				return "newFw";
			}
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		if (isEasyMode()) {
			setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
		} else {
			setSelectedL2Feature(L2_FEATURE_L3_FIREWALL_POLICY);
		}
		setDataSource(FirewallPolicy.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_FIREWALL_POLICY;
	}

	public FirewallPolicy getDataSource() {
		return (FirewallPolicy) dataSource;
	}

	public int getPolicyNameLength() {
		return getAttributeLength("policyName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public String getChangedPolicyName() {
		return getDataSource().getPolicyName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getGridCount() {
		return getDataSource().getRules().size() == 0 ? 3 : 0;
	}

	public EnumItem[] getEnumAction() {
		return IpPolicyRule.ENUM_MAC_POLICY_ACTION;
	}

	public EnumItem[] getEnumActionLog() {
		return FirewallPolicyRule.ENUM_FIREWALL_POLICY_LOGGING;
	}
	
	public EnumItem[] getEnumSourceType() {
		return FirewallPolicy.ENUM_FIREWALL_POLICY_TYPE1;
	}
	
	public EnumItem[] getEnumDestType() {
		return FirewallPolicy.ENUM_FIREWALL_POLICY_TYPE2;
	}
	
	public List<CheckItem> getAvailableIpAddress() {
		if (FirewallPolicy.FIREWALL_POLICY_TYPE_ANY != sourceType) {
			short objType = IpAddress.TYPE_IP_ADDRESS;
			switch (sourceType) {
				case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
					objType = IpAddress.TYPE_IP_NETWORK;
					break;
				case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
					objType = IpAddress.TYPE_IP_RANGE;
					break;
				case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
					objType = IpAddress.TYPE_IP_WILDCARD;
					break;
				default:
					break;
			}
			return getBoCheckItems("addressName", IpAddress.class, new FilterParams("typeFlag", objType),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		}
		return getBoCheckItems("addressName", IpAddress.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}
	
	public List<CheckItem> getAvailableDestIpAddress() {
		if (FirewallPolicy.FIREWALL_POLICY_TYPE_ANY != destType) {
			short objType = IpAddress.TYPE_IP_ADDRESS;
			switch (destType) {
				case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
					objType = IpAddress.TYPE_IP_NETWORK;
					break;
				case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
					objType = IpAddress.TYPE_IP_RANGE;
					break;
				case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
					objType = IpAddress.TYPE_IP_WILDCARD;
					break;
				case FirewallPolicy.FIREWALL_POLICY_TYPE_HOST:
					objType = IpAddress.TYPE_HOST_NAME;
					break;
				default:
					break;
			}
			return getBoCheckItems("addressName", IpAddress.class, new FilterParams("typeFlag", objType),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		}
		return getBoCheckItems("addressName", IpAddress.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getAvailableNetworkServices() {
		List<CheckItem> availableNetworkServices = getBoCheckItems("serviceName", NetworkService.class, new FilterParams("servicetype",NetworkService.SERVICE_TYPE_NETWORK));
		availableNetworkServices.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableNetworkServices.add(0, new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.ipPolicy.any")));
		return availableNetworkServices;
	}
	
	public List<CheckItem> getAvailableFwPolicy() {
		return getBoCheckItems("policyName", FirewallPolicy.class, null);
	}
	
	public List<CheckItem> getAvailableUserProfile() {
		return getBoCheckItems("userProfileName", UserProfile.class, null);
	}
	
	public List<CheckItem> getAvailableNetworkObj() {
		return getBoCheckItems("networkName", VpnNetwork.class, null);
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		FirewallPolicy source = QueryUtil.findBoById(FirewallPolicy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<FirewallPolicy> list = QueryUtil.executeQuery(FirewallPolicy.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (FirewallPolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			FirewallPolicy up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPolicyName(profile.getPolicyName());
			up.setOwner(profile.getOwner());
			List<FirewallPolicyRule> newrule = new ArrayList<FirewallPolicyRule>();
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
			List<FirewallPolicyRule> allRules = getDataSource().getRules();
			List<FirewallPolicyRule> removeRules = new ArrayList<FirewallPolicyRule>();
			for (FirewallPolicyRule rule : allRules) {
				NetworkService netService = rule.getNetworkService();
				UserProfile up1 = rule.getSourceUp();
				removeRules.add(rule);
				for (FirewallPolicyRule compareRule : allRules) {
					if (removeRules.contains(compareRule))
						continue;
					NetworkService nService = compareRule.getNetworkService();
					UserProfile up2 = compareRule.getSourceUp();
					if (null != netService && null != nService && !netService.getServiceName().equals(nService.getServiceName())) {
						if (netService.getProtocolNumber() == nService.getProtocolNumber() && netService.getPortNumber() == nService.getPortNumber()) {
							addActionError(MgrUtil.getUserMessage("error.config.classifier.network.service", "Policy Rule cannot contain "));
							return true;
						}
					}
					if (null != up1 && null != up2) {
						if (up1.getAttributeValue() == up2.getAttributeValue()) {
							if (!up1.getUserProfileName().equals(up2.getUserProfileName())) {
								addActionError(MgrUtil.getUserMessage("error.config.security.firewall.policy.same.user.profile.attribute",
									new String[]{String.valueOf(up1.getAttributeValue()), up1.getUserProfileName()+" ; "+up2.getUserProfileName()}));
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean checkTheSameItemExist() {
		if (null != getDataSource().getRules()) {
			List<FirewallPolicyRule> allRules = getDataSource().getRules();
			List<FirewallPolicyRule> removeRules = new ArrayList<FirewallPolicyRule>();
			for (FirewallPolicyRule rule : allRules) {
				removeRules.add(rule);
				for (FirewallPolicyRule compareRule : allRules) {
					if (removeRules.contains(compareRule))
						continue;
					
					if(rule.getSourceStr().equals(compareRule.getSourceStr()) && rule.getDestStr().equals(compareRule.getDestStr() )
							&& rule.getFilterAction() == compareRule.getFilterAction()
							&& rule.getActionLog() == compareRule.getActionLog()
							&& rule.isDisableRule() == compareRule.isDisableRule()){
						
						if(null == rule.getNetworkService() && null == compareRule.getNetworkService()){
							addActionError(MgrUtil.getUserMessage("geneva_26.error.custom.application.rule.add.failure"));
							return true;
						}
						
						if(null != rule.getNetworkService() && null != compareRule.getNetworkService()){
							if(rule.getNetworkService().getServiceName().equals(compareRule.getNetworkService().getServiceName())){
								addActionError(MgrUtil.getUserMessage("geneva_26.error.custom.application.rule.add.failure"));
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	protected boolean addSelectedRules() {
		if (getDataSource().getRules().size() + 1 > 1023) {
			addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
			hideCreateItem = "";
			hideNewButton = "none";
			return false;
		}

		FirewallPolicyRule fwPolicyRule = getSingleRuleValue(true);
		
		// set rule id
		fwPolicyRule.setRuleId((short)1);
		if (getDataSource().getRules() != null) {
			overloop:
			for (int l = 1; l < 1025; l++) {
				for (FirewallPolicyRule rule : getDataSource().getRules()) {
					if (l == rule.getRuleId()) {
						continue overloop;
					}
				}
				fwPolicyRule.setRuleId((short)l);
				break;
			}
		}
		
		if (getDataSource().isAddRuleInTop()) {
			List<FirewallPolicyRule> rules = new ArrayList<FirewallPolicyRule>();
			rules.add(fwPolicyRule);
			rules.addAll(getDataSource().getRules());
			getDataSource().setRules(rules);
		} else {
			getDataSource().getRules().add(fwPolicyRule);
		}
		
		filterAction = IpPolicyRule.POLICY_ACTION_DENY;
		actionLog = FirewallPolicyRule.POLICY_LOGGING_OFF;
		disableRule = false;
		sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
		srIpAddressStr = "";
		destType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
		destIpAddressStr = "";
		sourceIpId = null;
		sourceNetObjId = null;
		sourceUpId = null;
		destIpId = null;
		destNetObjId = null;
		netServiceId = null;
		return true;
	}
	
	private FirewallPolicyRule getSingleRuleValue(boolean createBo) {
		FirewallPolicyRule fwPolicyRule = new FirewallPolicyRule();
		
		// source info
		fwPolicyRule.setSourceType(sourceType);
		switch (sourceType) {
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
				if (null != sourceIpId && sourceIpId.longValue() > -1) {
					fwPolicyRule.setSourceIp(QueryUtil.findBoById(IpAddress.class, sourceIpId));
				} else if (createBo && null != srIpAddressStr && !"".equals(srIpAddressStr)) {
					short ipType = IpAddress.TYPE_IP_NETWORK;
					if (sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE) {
						ipType = IpAddress.TYPE_IP_RANGE;
					} else if (sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD) {
						ipType = IpAddress.TYPE_IP_WILDCARD;
					}
					String[] twoIps = srIpAddressStr.split(sourceType==FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE?"-":"/");
					if (twoIps.length == 2) {
						String netmask = twoIps[1];
						if (sourceType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET) {
							netmask = AhDecoder.int2Netmask(Integer.parseInt(twoIps[1]));
						}
						fwPolicyRule.setSourceIp(CreateObjectAuto.createNewIP(twoIps[0], ipType, getDomain(), "For Network Firewall Policy", netmask));
					}
				}
				if (null != fwPolicyRule.getSourceIp()) {
					fwPolicyRule.setSourceCliNetwork(fwPolicyRule.getSourceIp().getAddressName());
				} else {
					fwPolicyRule.setSourceCliNetwork(srIpAddressStr);
				}
				break;
			case FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ:
				fwPolicyRule.setSourceNtObj(QueryUtil.findBoById(VpnNetwork.class, sourceNetObjId));
				break;
			case FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ:
				fwPolicyRule.setSourceUp(QueryUtil.findBoById(UserProfile.class, sourceUpId));
				break;
			default:
				break;
		}
		
		// destination info
		fwPolicyRule.setDestType(destType);
		switch (destType) {
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_HOST:
				if (null != destIpId && destIpId.longValue() > -1) {
					fwPolicyRule.setDestinationIp(QueryUtil.findBoById(IpAddress.class, destIpId));
				} else if (createBo && null != destIpAddressStr && !"".equals(destIpAddressStr)) {
					short ipType = IpAddress.TYPE_IP_NETWORK;
					if (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE) {
						ipType = IpAddress.TYPE_IP_RANGE;
					} else if (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD) {
						ipType = IpAddress.TYPE_IP_WILDCARD;
					} else if (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_HOST) {
						ipType = IpAddress.TYPE_HOST_NAME;
					}
					if (ipType == IpAddress.TYPE_HOST_NAME) {
						fwPolicyRule.setDestinationIp(CreateObjectAuto.createNewIP(destIpAddressStr, ipType, getDomain(), "For Network Firewall Policy"));
					} else {
						String[] twoIps = destIpAddressStr.split(destType==FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE?"-":"/");
						if (twoIps.length == 2) {
							String netmask = twoIps[1];
							if (destType == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET) {
								netmask = AhDecoder.int2Netmask(Integer.parseInt(twoIps[1]));
							}
							fwPolicyRule.setDestinationIp(CreateObjectAuto.createNewIP(twoIps[0], ipType, getDomain(), "For Network Firewall Policy", netmask));
						}
					}
				}
				if (null != fwPolicyRule.getDestinationIp()) {
					fwPolicyRule.setDestCliNetwork(fwPolicyRule.getDestinationIp().getAddressName());
				} else {
					fwPolicyRule.setDestCliNetwork(destIpAddressStr);
				}
				break;
			case FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ:
				fwPolicyRule.setDestinationNtObj(QueryUtil.findBoById(VpnNetwork.class, destNetObjId));
				break;
			default:
				break;
		}
		fwPolicyRule.setNetworkService(QueryUtil.findBoById(NetworkService.class, netServiceId));
		fwPolicyRule.setFilterAction(filterAction);
		fwPolicyRule.setActionLog(actionLog);
		fwPolicyRule.setDisableRule(disableRule);
		return fwPolicyRule;
	}

	protected void updateRules() {
		if (null == ruleIds) {
			getDataSource().setRules(new ArrayList<FirewallPolicyRule>());
		} else {
			List<FirewallPolicyRule> rules = new ArrayList<FirewallPolicyRule>();
			for (int i = 0; i < ruleIds.length; i++) {
				for (FirewallPolicyRule rule : getDataSource().getRules()) {
					if (rule.getRuleId() == ruleIds[i]) {
						rule.setFilterAction(filterActions[i]);
						rule.setActionLog(actionLogs[i]);
						rule.setDisableRule(disableRules[i]);
						rules.add(rule);
						break;
					}
				}
			}
			getDataSource().setRules(rules);
		}
		
		if (null != defRuleRadio) {
			if ("permit".equals(defRuleRadio)) {
				getDataSource().setDefRuleAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			} else {
				getDataSource().setDefRuleAction(IpPolicyRule.POLICY_ACTION_DENY);
			}
		}
		
	}

	private Long sourceIpId = null;
	
	private Long sourceNetObjId = null;
	
	private Long sourceUpId = null;

	private Long destIpId = null;
	
	private Long destNetObjId = null;
	
	private Long netServiceId = null;
	
	private short[] ruleIds;
	
	private String ruleIdStr;

	private short[] filterActions;
	
	private String actionStr;

	private short filterAction = IpPolicyRule.POLICY_ACTION_DENY;

	private short[] actionLogs;
	
	private String logStr;

	private short actionLog = FirewallPolicyRule.POLICY_LOGGING_OFF;
	
	private boolean disableRule = false;
	
	private String disableStr;
	
	private boolean[] disableRules;
	
	private short sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
	
	private short destType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
	
	private String srIpAddressStr;
	
	private String destIpAddressStr;
	
	private String defRuleRadio = "permit";
	
	private String radioIpOrName;
	
	private String firewallIPType;
	
	private boolean showUpInNavTree = true;

	public String getRadioIpOrName()
	{
		return radioIpOrName;
	}

	public void setRadioIpOrName(String radioIpOrName)
	{
		this.radioIpOrName = radioIpOrName;
	}

	public String getDefRuleRadio()
	{
		switch(getDataSource().getDefRuleAction()) {
    		case IpPolicyRule.POLICY_ACTION_PERMIT:
    			defRuleRadio = "permit";
    			break;
    		case IpPolicyRule.POLICY_ACTION_DENY:
    			defRuleRadio = "deny";
    			break;
    		default:
    			break;
    	}
		return defRuleRadio;
	}

	public void setDefRuleRadio(String defRuleRadio)
	{
		this.defRuleRadio = defRuleRadio;
	}

	public String getSrIpAddressStr()
	{
		return srIpAddressStr;
	}

	public void setSrIpAddressStr(String srIpAddressStr)
	{
		this.srIpAddressStr = srIpAddressStr;
	}

	public short getSourceType()
	{
//		if (null != sourceIpId && sourceIpId.longValue() > 0) {
//			if (null != radioIpOrName) {
//				if ("wildcard".equals(radioIpOrName)) {
//					sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD;
//				} else if ("network".equals(radioIpOrName)) {
//					sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET;
//				} else if ("range".equals(radioIpOrName)) {
//					sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE;
//				}
//			}
//		} else if (null != sourceNetObjId && sourceNetObjId.longValue() > 0) {
//			sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ;
//		} else if (null != sourceUpId && sourceUpId.longValue() > 0) {
//			sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ;
//		}
		return sourceType;
	}

	public void setSourceType(short sourceType)
	{
		this.sourceType = sourceType;
	}

	public short getDestType()
	{
		return destType;
	}

	public void setDestType(short destType)
	{
		this.destType = destType;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
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

	public short getFilterAction() {
		return filterAction;
	}

	public short getActionLog() {
		return actionLog;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof FirewallPolicy) {
			FirewallPolicy policy = (FirewallPolicy) bo;
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

	public Long getNetServiceId() {
		return netServiceId;
	}

	public void setNetServiceId(Long netServiceId) {
		this.netServiceId = netServiceId;
	}

	public Long getSourceIpId()
	{
		return sourceIpId;
	}

	public void setSourceIpId(Long sourceIpId)
	{
		this.sourceIpId = sourceIpId;
	}

	public Long getDestIpId()
	{
		return destIpId;
	}

	public void setDestIpId(Long destIpId)
	{
		this.destIpId = destIpId;
	}

	public void setDisableRule(boolean disableRule)
	{
		this.disableRule = disableRule;
	}

	public void setDisableRules(boolean[] disableRules)
	{
		this.disableRules = disableRules;
	}

	public Long getSourceNetObjId()
	{
		return sourceNetObjId;
	}

	public void setSourceNetObjId(Long sourceNetObjId)
	{
		this.sourceNetObjId = sourceNetObjId;
	}

	public Long getSourceUpId()
	{
		return sourceUpId;
	}

	public void setSourceUpId(Long sourceUpId)
	{
		this.sourceUpId = sourceUpId;
	}

	public Long getDestNetObjId()
	{
		return destNetObjId;
	}

	public void setDestNetObjId(Long destNetObjId)
	{
		this.destNetObjId = destNetObjId;
	}

	public String getDestIpAddressStr()
	{
		return destIpAddressStr;
	}

	public void setDestIpAddressStr(String destIpAddressStr)
	{
		this.destIpAddressStr = destIpAddressStr;
	}
	
	private Long fwPolicyNew;

	public Long getFwPolicyNew()
	{
		if (null == fwPolicyNew) {
			fwPolicyNew = getAvailableFwPolicy().get(0).getId();
		}
		return fwPolicyNew;
	}

	public void setFwPolicyNew(Long fwPolicyNew)
	{
		this.fwPolicyNew = fwPolicyNew;
	}

	public boolean isShowUpInNavTree()
	{
		return showUpInNavTree;
	}

	public void setShowUpInNavTree(boolean showUpInNavTree)
	{
		this.showUpInNavTree = showUpInNavTree;
	}

	public void setRuleIdStr(String ruleIdStr)
	{
		this.ruleIdStr = ruleIdStr;
		
		if (null != ruleIdStr && StringUtils.isNotBlank(ruleIdStr)) {
			String[] array = ruleIdStr.split(",");
			ruleIds = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				ruleIds[i] = Short.parseShort(array[i]);
			}
		}
	}


	public void setActionStr(String actionStr)
	{
		this.actionStr = actionStr;
		
		if (null != actionStr && StringUtils.isNotBlank(actionStr)) {
			String[] array = actionStr.split(",");
			filterActions = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				filterActions[i] = Short.parseShort(array[i]);
			}
		}
	}

	public void setLogStr(String logStr)
	{
		this.logStr = logStr;
		
		if (null != logStr && StringUtils.isNotBlank(logStr)) {
			String[] array = logStr.split(",");
			actionLogs = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				actionLogs[i] = Short.parseShort(array[i]);
			}
		}
	}

	public void setDisableStr(String disableStr)
	{
		this.disableStr = disableStr;
		
		if (null != disableStr && StringUtils.isNotBlank(disableStr)) {
			String[] array = disableStr.split(",");
			disableRules = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				disableRules[i] = "true".equals(array[i]);
			}
		}
	}

	public boolean isDisableRule()
	{
		return disableRule;
	}

	public String getFirewallIPType() {
		return firewallIPType;
	}

	public void setFirewallIPType(String firewallIPType) {
		this.firewallIPType = firewallIPType;
	}


}
