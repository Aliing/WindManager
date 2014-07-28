/**
 *@filename		MacPolicyAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 AM 10:34:34
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
import java.util.Vector;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MacPolicyRule;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class MacPolicyAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private final short unSupportType = MacOrOui.TYPE_MAC_RANGE;

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
				if (!setTitleAndCheckAccess(getText("config.title.macPolicy.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new MacPolicy());
				hideCreateItem = "";
				hideNewButton = "none";
				storeJsonContext();
				return isJsonMode() ? "macPolicyDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateRules();
				if (checkNameExists("policyName", getDataSource()
						.getPolicyName())) {
					
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource()
								.getPolicyName().toString()));
						return "json";
					} else {
						return isJsonMode() ? "macPolicyDlg" : INPUT;
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
				
					try {
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("id", id);
						jsonObject.put("parentDomID",getParentDomID());
						jsonObject.put("name", getDataSource().getPolicyName());
						jsonObject.put("resultStatus",true);
					} catch (Exception e) {
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
				String strForward = editBo(this);
				if (null != dataSource) {
					addLstTitle(getText("config.title.macPolicy.edit")
							+ " '" + getChangedPolicyName() + "'");
					if (getDataSource().getRules().size() == 0) {
						hideCreateItem = "";
						hideNewButton = "none";
					}	
				}
				storeJsonContext();
				return isJsonMode() ? "macPolicyDlg" : strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateRules();
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
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MacPolicy profile = (MacPolicy) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setPolicyName("");
				profile.setVersion(null);
				List<MacPolicyRule> newrule = new ArrayList<MacPolicyRule>();
				newrule.addAll(profile.getRules());
				profile.setRules(newrule);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.macPolicy.new"));
				return INPUT;
			} else if ("newMac".equals(operation) || "editSourceMac".equals(operation) || "editDestMac".equals(operation)) {
				addLstForward("macPolicy");
				clearErrorsAndMessages();
				if (!"newMac".equals(operation)) {
					operation = "editMac";
				}
				return operation;
			} else if ("continue".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				hideCreateItem = "";
				hideNewButton = "none";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					return isJsonMode() ? "macPolicyDlg" : INPUT;
				}
			} else if ("addPolicyRules".equals(operation)) {
				if (dataSource == null) {
					String str = prepareBoList();
					return isJsonMode() ? "macPolicyDlg" : str;
				} else {
					updateRules();
					addSelectedRules();
					return isJsonMode() ? "macPolicyDlg" : INPUT;
				}
			} else if ("removePolicyRules".equals(operation)
					|| "removePolicyRulesNone".equals(operation)) {
				hideCreateItem = "removePolicyRulesNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removePolicyRulesNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					String str = prepareBoList();
					return isJsonMode() ? "macPolicyDlg" : str;
				} else {
					Collection<MacPolicyRule> removeList = findRulesToRemove();
					updateRules();
					getDataSource().getRules().removeAll(removeList);
					return isJsonMode() ? "macPolicyDlg" : INPUT;
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
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
			setSelectedL2Feature(L2_FEATURE_MAC_POLICY);
		}
//		setSelectedL2Feature(L2_FEATURE_MAC_POLICY);
		setDataSource(MacPolicy.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MAC_POLICY;
	}

	public MacPolicy getDataSource() {
		return (MacPolicy) dataSource;
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

	public EnumItem[] getEnumDenyLog() {
		return IpPolicyRule.ENUM_POLICY_LOGGING_DENY;
	}

	public EnumItem[] getEnumPermitLog() {
		return IpPolicyRule.ENUM_POLICY_LOGGING_PERMIT;
	}

	public List<CheckItem> getAllMacAddress() {
		return getBoCheckItems("macOrOuiName", MacOrOui.class, new FilterParams("typeFlag != :s1", new Object[]{MacOrOui.TYPE_MAC_RANGE}));
	}

	public List<CheckItem> getAvailableMacAddress() {
		List<CheckItem> availableMacAddress = getAllMacAddress();
		availableMacAddress.remove(new CheckItem((long) -1, MgrUtil
				.getUserMessage("config.optionsTransfer.none")));
		availableMacAddress.add(0, new CheckItem((long) -1, MgrUtil
				.getUserMessage("config.ipPolicy.any")));
		return availableMacAddress;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MacPolicy source = QueryUtil.findBoById(MacPolicy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MacPolicy> list = QueryUtil.executeQuery(MacPolicy.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (MacPolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			MacPolicy up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPolicyName(profile.getPolicyName());
			up.setOwner(profile.getOwner());
			List<MacPolicyRule> newrule = new ArrayList<MacPolicyRule>();
			newrule.addAll(source.getRules());
			up.setRules(newrule);
			hmBos.add(up);
		}
		return hmBos;
	}

	protected boolean addSelectedRules() {
		int count = sourceMacIds.size() * destMacIds.size();
		int oldCount = getDataSource().getRules().size();
		if (count + oldCount > 32) {
			addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
			hideCreateItem = "";
			hideNewButton = "none";
			return false;
		}

		MacPolicyRule macPolicyRule;

		short i = 1;

		for (Long sourceMacId : sourceMacIds) {
			MacOrOui sourceMac = null;
			if (sourceMacId > -1) {
				sourceMac = QueryUtil.findBoById(MacOrOui.class,
						sourceMacId);
			}
			for (Long destMacId : destMacIds) {
				MacOrOui desctinationMac = null;
				if (destMacId > -1) {
					desctinationMac = QueryUtil.findBoById(
							MacOrOui.class, destMacId);
				}
				macPolicyRule = new MacPolicyRule();
				if (getDataSource().getRules() != null) {
					boolean bool = false;
					for (MacPolicyRule rule : getDataSource().getRules()) {
						if ((sourceMac == rule.getSourceMac() || (null != sourceMac
								&& null != rule.getSourceMac() && sourceMac
								.getMacOrOuiName().equals(
										rule.getSourceMac().getMacOrOuiName())))
								&& (desctinationMac == rule.getDestinationMac() || (null != desctinationMac
										&& null != rule.getDestinationMac() && desctinationMac
										.getMacOrOuiName().equals(
												rule.getDestinationMac()
														.getMacOrOuiName())))) {
							bool = true;
							break;
						}
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
					for (int l = 1; l < 33; l++) {
						int k = 0;
						for (MacPolicyRule rule : getDataSource().getRules()) {
							if (l != rule.getRuleId())
								k++;
						}
						if (k == getDataSource().getRules().size()) {
							macPolicyRule.setRuleId((short) l);
							break;
						}
					}
				} else {
					macPolicyRule.setRuleId(i++);
				}
				macPolicyRule.setSourceMac(sourceMac);
				macPolicyRule.setDestinationMac(desctinationMac);
				macPolicyRule.setFilterAction(filterAction);
				macPolicyRule.setActionLog(actionLog);

				getDataSource().getRules().add(macPolicyRule);
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
		sourceMacIds = null;
		destMacIds = null;
		filterAction = IpPolicyRule.POLICY_ACTION_DENY;
		actionLog = IpPolicyRule.POLICY_LOGGING_OFF;
		return true;
	}

	protected void reorderRules() {
		if (ordering == null) {
			return;
		}

		boolean needsReordering = false;
		for (int i = 0; i < ordering.length; i++) {
			if (ordering[i] != i) {
				needsReordering = true;
			}
			if (ordering[i] < getDataSource().getRules().size()) {
				getDataSource().getRules().get(ordering[i]).setReorder(i);
			}
		}
		if (!needsReordering) {
			return;
		}

		Collections.sort(getDataSource().getRules(),
				new Comparator<MacPolicyRule>() {
					public int compare(MacPolicyRule rule1, MacPolicyRule rule2) {
						Integer id1 = rule1.getReorder();
						Integer id2 = rule2.getReorder();
						return id1.compareTo(id2);
					}
				});
	}

	protected Collection<MacPolicyRule> findRulesToRemove() {
		Collection<MacPolicyRule> removeList = new Vector<MacPolicyRule>();
		if (ruleIndices != null) {
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getRules().size()) {
						removeList.add(getDataSource().getRules().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
				}
			}
		}
		return removeList;
	}

	protected void updateRules() {

		reorderRules();
		if (filterActions != null) {
			for (int i = 0; i < filterActions.length
					&& i < getDataSource().getRules().size(); i++) {
				getDataSource().getRules().get(i).setFilterAction(
						filterActions[i]);
				getDataSource().getRules().get(i).setActionLog(actionLogs[i]);
			}
		}
	}

	private List<Long> sourceMacIds;

	private List<Long> destMacIds;
	
	private Long macAddressId;

	private Collection<String> ruleIndices;

	private int[] ordering;

	private short[] filterActions;

	private short filterAction = IpPolicyRule.POLICY_ACTION_DENY;

	private short[] actionLogs;

	private short actionLog;

	private String policyType;

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public void setFilterActions(short[] filterActions) {
		this.filterActions = filterActions;
	}

	public void setSourceMacIds(List<Long> sourceMacIds) {
		this.sourceMacIds = sourceMacIds;
	}

	public void setDestMacIds(List<Long> destMacIds) {
		this.destMacIds = destMacIds;
	}

	public void setOrdering(int[] ordering) {
		this.ordering = ordering;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	public void setActionLogs(short[] actionLogs) {
		this.actionLogs = actionLogs;
	}

	public void setActionLog(short actionLog) {
		this.actionLog = actionLog;
	}

	public List<Long> getSourceMacIds() {
		return sourceMacIds;
	}

	public List<Long> getDestMacIds() {
		return destMacIds;
	}

	public short getFilterAction() {
		return filterAction;
	}

	public short getActionLog() {
		return actionLog;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof MacPolicy) {
			MacPolicy policy = (MacPolicy) bo;
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
	 *
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

	public Long getMacAddressId() {
		return macAddressId;
	}

	public void setMacAddressId(Long macAddressId) {
		this.macAddressId = macAddressId;
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
	}

	public short getUnSupportType() {
		return unSupportType;
	}

}