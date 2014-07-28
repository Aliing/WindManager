/**
 *@filename		RadiusLibrarySipAction.java
 *@version
 *@author		Fiona
 *@createtime	2010-10-13 PM 03:14:10
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class RadiusLibrarySipAction extends BaseAction implements QueryBo
{

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(RadiusLibrarySipAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() &&
					("continue".equals(operation)
						|| "continueDef".equals(operation))) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.radius.library.sip.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new RadiusLibrarySip());
				hideCreateItem = "";
				hideNewButton = "none";
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "radiusLibSipJsonDlg");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateRules();
				if (checkNameExists("policyName", getDataSource().getPolicyName())) {
					return getReturnPathWithJsonMode(INPUT, "radiusLibSipJsonDlg");
				}
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (null != dataSource) {
					addLstTitle(getText("config.title.radius.library.sip.edit")
							+ " '" + getChangedPolicyName() + "'");
					if (getDataSource().getRules().size() == 0) {
						hideCreateItem = "";
						hideNewButton = "none";
					}
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(strForward, "radiusLibSipJsonDlg");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateRules();
				}
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadiusLibrarySip profile = (RadiusLibrarySip) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setPolicyName("");
				List<RadiusLibrarySipRule> newrule = new ArrayList<RadiusLibrarySipRule>();
				newrule.addAll(profile.getRules());
				profile.setRules(newrule);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.radius.library.sip.new"));
				return INPUT;
			} else if ("newUserGroup".equals(operation) || "editUserGroup".equals(operation)
					|| "newDefUserGroup".equals(operation) || "editDefUserGroup".equals(operation)) {
				updateRules();
				if ("newDefUserGroup".equals(operation) || "editDefUserGroup".equals(operation)) {
					addLstForward("librarySipDef");
				} else {
					addLstForward("librarySip");
				}
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation) || "continueDef".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if ("continue".equals(operation) || getDataSource().getRules().isEmpty()) {
					hideCreateItem = "";
					hideNewButton = "none";
				}
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					return getReturnPathWithJsonMode(INPUT, "radiusLibSipJsonDlg");
				}
			} else if ("addPolicyRules".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRules();
					addSelectedRules();
					return getReturnPathWithJsonMode(INPUT, "radiusLibSipJsonDlg");
				}
			} else if ("removePolicyRules".equals(operation)
					|| "removePolicyRulesNone".equals(operation)) {
				hideCreateItem = "removePolicyRulesNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removePolicyRulesNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					// Find rules to remove before reordering/updating
					Collection<RadiusLibrarySipRule> removeList = findRulesToRemove();
					updateRules();
					getDataSource().getRules().removeAll(removeList);
					return getReturnPathWithJsonMode(INPUT, "radiusLibSipJsonDlg");
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
		setSelectedL2Feature(L2_FEATURE_RADIUS_LIBRARY_SIP);
		setDataSource(RadiusLibrarySip.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_RADIUS_LIBRARY_SIP;
	}

	public RadiusLibrarySip getDataSource() {
		return (RadiusLibrarySip) dataSource;
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

	public List<CheckItem> getAvailableSipFields() {
		List<CheckItem> result = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i ++) {
			if (i == 0) {
				result.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
			} else {
				result.add(new CheckItem((long)i, MgrUtil.getEnumString("enum.config.radius.library.sip.field."+i)));
			}
		}
		return result;
	}
	
	public EnumItem[] getEnumOperation() {
		return RadiusLibrarySipRule.ENUM_SIP_OPERATOR;
	}
	
	public EnumItem[] getEnumAction() {
		return RadiusLibrarySipRule.ENUM_SIP_RULE_ACTION;
	}
	
	public EnumItem[] getRuleFieldValue() {
		int ruleLength = RadiusLibrarySipRule.BL_FIELD_VALID_PATRON.length;
		EnumItem[] enumItems = new EnumItem[ruleLength];
		for (int i = 0; i < ruleLength; i++) {
			enumItems[i] = new EnumItem(i, RadiusLibrarySipRule.BL_FIELD_VALID_PATRON[i]);
		}
		return enumItems;
	}
	
	public List<CheckItem> getLocalUserGroup() {
		return getBoCheckItems("groupName", LocalUserGroup.class,
			new FilterParams("userType", LocalUserGroup.USERGROUP_USERTYPE_RADIUS));
	}
	
	public Map<String, String> getSipFields() {
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("AA", MgrUtil.getEnumString("enum.config.radius.library.sip.field.aa"));
		fields.put("AE", MgrUtil.getEnumString("enum.config.radius.library.sip.field.ae"));
		fields.put("AF", MgrUtil.getEnumString("enum.config.radius.library.sip.field.af"));
		fields.put("AG", MgrUtil.getEnumString("enum.config.radius.library.sip.field.ag"));
		fields.put("AO", MgrUtil.getEnumString("enum.config.radius.library.sip.field.ao"));
		fields.put("AS", MgrUtil.getEnumString("enum.config.radius.library.sip.field.as"));
		fields.put("AT", MgrUtil.getEnumString("enum.config.radius.library.sip.field.at"));
		fields.put("AU", MgrUtil.getEnumString("enum.config.radius.library.sip.field.au"));
		fields.put("AV", MgrUtil.getEnumString("enum.config.radius.library.sip.field.av"));
		fields.put("BD", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bd"));
		fields.put("BE", MgrUtil.getEnumString("enum.config.radius.library.sip.field.be"));
		fields.put("BF", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bf"));
		fields.put("BH", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bh"));
		fields.put("BL", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bl"));
		fields.put("BU", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bu"));
		fields.put("BV", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bv"));
		fields.put("BZ", MgrUtil.getEnumString("enum.config.radius.library.sip.field.bz"));
		fields.put("CA", MgrUtil.getEnumString("enum.config.radius.library.sip.field.ca"));
		fields.put("CB", MgrUtil.getEnumString("enum.config.radius.library.sip.field.cb"));
		fields.put("CC", MgrUtil.getEnumString("enum.config.radius.library.sip.field.cc"));
		fields.put("CD", MgrUtil.getEnumString("enum.config.radius.library.sip.field.cd"));
		fields.put("CQ", MgrUtil.getEnumString("enum.config.radius.library.sip.field.cq"));
		return fields;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadiusLibrarySip source = QueryUtil.findBoById(RadiusLibrarySip.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadiusLibrarySip> list = QueryUtil.executeQuery(RadiusLibrarySip.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (RadiusLibrarySip profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			RadiusLibrarySip up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPolicyName(profile.getPolicyName());
			up.setOwner(profile.getOwner());
			List<RadiusLibrarySipRule> newrule = new ArrayList<RadiusLibrarySipRule>();
			newrule.addAll(source.getRules());
			up.setRules(newrule);
			hmBos.add(up);
		}
		return hmBos;
	}
	
	protected boolean addSelectedRules() {
		RadiusLibrarySipRule sipRule = new RadiusLibrarySipRule();
		short i = 1;
		// the field from list
		if (null != fieldId && fieldId > 0) {
			field = MgrUtil.getEnumString("enum.config.radius.library.sip.field."+fieldId).substring(0, 2);
		}

		// check field and set rule id
		if (getDataSource().getRules() != null) {
//			for (RadiusLibrarySipRule rule : getDataSource().getRules()) {
//				if (field.equalsIgnoreCase(rule.getField())) {
//					hideCreateItem = "";
//					hideNewButton = "none";
//					sipOperation = RadiusLibrarySipRule.SIP_OPERATOR_MATCH;
//					addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
//					return false;
//				}
//			}			
			for (int l = 1; l < 65; l++) {
				int k = 0;
				for (RadiusLibrarySipRule rule : getDataSource().getRules()) {
					if (l != rule.getRuleId())
						k++;
				}
				if (k == getDataSource().getRules().size()) {
					sipRule.setRuleId((short) l);
					break;
				}
			}
		} else {
			sipRule.setRuleId(i++);
		}
		// set user group
		if (null != groupId && groupId > -1) {
			sipRule.setUserGroup(QueryUtil.findBoById(LocalUserGroup.class, groupId));
		}
		sipRule.setField(field.toUpperCase());
		sipRule.setOperator(sipOperation);
		
		switch (sipOperation) {
			case RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_AFTER:
			case RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_BEFORE:
				sipRule.setValueStr(fieldTimeStr);
				break;
			case RadiusLibrarySipRule.SIP_OPERATOR_EQUAL:
			case RadiusLibrarySipRule.SIP_OPERATOR_GREATER_THAN:
			case RadiusLibrarySipRule.SIP_OPERATOR_LESS_THAN:
				sipRule.setValueStr(valueInt);
				break;
			default:
				sipRule.setValueStr(valueStr);
				if (null != fieldId && fieldId > 0) {
					if (fieldId == 13 || fieldId == 14 || fieldId == 22) {
						sipRule.setValueStr(valueSelect);
					}
				}
				break;
		}
		sipRule.setAction(accessAction);
		sipRule.setMessage(accessMessage);
		
		getDataSource().getRules().add(sipRule);
		
		fieldId = 0l;
		field = "";
		valueStr = "";
		groupId = null;
		sipOperation = RadiusLibrarySipRule.SIP_OPERATOR_MATCH;
		accessAction = RadiusLibrarySipRule.SIP_RULE_ACTION_PERMIT;
		accessMessage = "";
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
		log.info("reoderRules", "Needs re-ordering");
		Collections.sort(getDataSource().getRules(),
				new Comparator<RadiusLibrarySipRule>() {
					public int compare(RadiusLibrarySipRule rule1, RadiusLibrarySipRule rule2) {
						Integer id1 = rule1.getReorder();
						Integer id2 = rule2.getReorder();
						return id1.compareTo(id2);
					}
				});
	}

	protected Collection<RadiusLibrarySipRule> findRulesToRemove() {
		Collection<RadiusLibrarySipRule> removeList = new Vector<RadiusLibrarySipRule>();
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
		// set default user group
		if (null != defGroupId && defGroupId > -1) {
			getDataSource().setDefUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, defGroupId));
		}
		// Reorder before updating any of the attributes
		reorderRules();
	}
	
	private Long fieldId;
	
	private String field;
	
	private String valueSelect;
	
	private String valueStr;
	
	private String valueInt;
	
	private String fieldTimeStr = "2010-10-10";
	
	private short accessAction;
	
	private String accessMessage;
	
	private Long groupId;
	
	private Long defGroupId;

	private int[] ordering;

	private Collection<String> ruleIndices;

	private short sipOperation = RadiusLibrarySipRule.SIP_OPERATOR_MATCH;

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
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

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusLibrarySip) {
			RadiusLibrarySip policy = (RadiusLibrarySip) bo;
			if (policy.getRules() != null)
				policy.getRules().size();
		}
		return null;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DEF_USER_GROUP = 2;
	
	public static final int COLUMN_DESCRIPTION = 3;
	
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
		case COLUMN_DEF_USER_GROUP:
			code = "config.radius.library.sip.def.userGroup";
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
		columns.add(new HmTableColumn(COLUMN_DEF_USER_GROUP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public String getField()
	{
		if (null != fieldId) {
			for (CheckItem item : getAvailableSipFields()) {
				if (item.getId().longValue() == fieldId.longValue()) {
					field = item.getValue();
					break;
				}
			}
		}
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public String getValueStr()
	{
		return valueStr;
	}

	public void setValueStr(String valueStr)
	{
		this.valueStr = valueStr;
	}

	public Long getGroupId()
	{
		return groupId;
	}

	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}

	public short getSipOperation()
	{
		return sipOperation;
	}

	public void setSipOperation(short sipOperation)
	{
		this.sipOperation = sipOperation;
	}

	public Long getFieldId()
	{
		return fieldId;
	}

	public void setFieldId(Long fieldId)
	{
		this.fieldId = fieldId;
	}

	public Long getDefGroupId()
	{
		if (null == defGroupId && null != getDataSource().getDefUserGroup()) {
			defGroupId = getDataSource().getDefUserGroup().getId();
		}
		return defGroupId;
	}

	public void setDefGroupId(Long defGroupId)
	{
		this.defGroupId = defGroupId;
	}

	public String getValueSelect()
	{
		return valueSelect;
	}

	public void setValueSelect(String valueSelect)
	{
		this.valueSelect = valueSelect;
	}

	public String getFieldTimeStr()
	{
		return fieldTimeStr;
	}

	public void setFieldTimeStr(String fieldTimeStr)
	{
		this.fieldTimeStr = fieldTimeStr;
	}

	public String getValueInt()
	{
		return valueInt;
	}

	public void setValueInt(String valueInt)
	{
		this.valueInt = valueInt;
	}

	public short getAccessAction()
	{
		return accessAction;
	}

	public void setAccessAction(short accessAction)
	{
		this.accessAction = accessAction;
	}
	
	public String getAccessMessage()
	{
		return accessMessage;
	}

	public void setAccessMessage(String accessMessage)
	{
		this.accessMessage = accessMessage;
	}

	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
		getDataSource().setContentShowType(getContentShowType());
	}
	
	private void restoreJsonContext() {
		setParentDomID(getDataSource().getParentDomID());
		setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
		setContentShowType(getDataSource().getContentShowType());
	}

}