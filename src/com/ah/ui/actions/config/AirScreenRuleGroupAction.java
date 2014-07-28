package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AirScreenRuleGroupAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(AirScreenRuleAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("new".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (!setTitleAndCheckAccess(getText("config.title.airscreen.rule.group"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new AirScreenRuleGroup());
				prepareDependentObjects();
				return INPUT;
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				prepareSaveObjects();
				if (checkNameExists("profileName", getDataSource()
						.getProfileName())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (!validateRuleCount(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
				}
				addLstTitle(getText("config.title.airscreen.rule.group.edit")
						+ " '" + getChangedName() + "'");
				return returnWord;
			} else if ("update".equals(operation)) {
				if (dataSource == null) {
					prepareDependentObjects();
					return INPUT;
				}
				prepareSaveObjects();
				if (!validateRuleCount(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				return updateBo();
			} else if (("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSaveObjects();
				}
				if (!validateRuleCount(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
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
			} else if ("newRule".equals(operation)
					|| "editRule".equals(operation)) {
				prepareSaveObjects();
				clearErrorsAndMessages();
				addLstForward("airScreenRuleGroup");
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
					prepareDependentObjects();
					setId(dataSource.getId());
					return INPUT;
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AirScreenRuleGroup profile = (AirScreenRuleGroup) findBoById(
						boClass, cloneId, this);
				if (null != profile) {
					profile.setOwner(null);
					profile.setId(null);
					profile.setVersion(null);
					profile.setProfileName("");

					Set<AirScreenRule> set = new HashSet<AirScreenRule>();
					for (AirScreenRule sc : profile.getRules()) {
						set.add(sc);
					}
					profile.setRules(set);
					setSessionDataSource(profile);
					prepareDependentObjects();
					addLstTitle(getText("config.title.airscreen.rule.group"));
					return INPUT;
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_AIR_SCREEN_RULE_GROUP);
		setDataSource(AirScreenRuleGroup.class);
		keyColumnId = COLUMN_PROFILE_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AIR_SCREEN_RULE_GROUP;
	}

	@Override
	public AirScreenRuleGroup getDataSource() {
		return (AirScreenRuleGroup) dataSource;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		AirScreenRuleGroup source = QueryUtil.findBoById(
				AirScreenRuleGroup.class, paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<AirScreenRuleGroup> list = QueryUtil.executeQuery(
				AirScreenRuleGroup.class, null, new FilterParams("id",
						destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (AirScreenRuleGroup profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			AirScreenRuleGroup arg = source.clone();
			if (null == arg) {
				continue;
			}
			setCloneFields(source, arg);
			arg.setId(profile.getId());
			arg.setVersion(profile.getVersion());
			arg.setProfileName(profile.getProfileName());
			arg.setOwner(profile.getOwner());

			if (!validateRuleCount(arg)) {
				return null;
			}
			hmBos.add(arg);
		}
		return hmBos;
	}

	private void setCloneFields(AirScreenRuleGroup source,
			AirScreenRuleGroup destination) {
		Set<AirScreenRule> set = new HashSet<AirScreenRule>();
		for (AirScreenRule sc : source.getRules()) {
			set.add(sc);
		}
		destination.setRules(set);
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

	private void prepareDependentObjects() throws Exception {
		prepareAvailableRules();
	}

	public void prepareAvailableRules() throws Exception {
		List<CheckItem> availableRules = getBoCheckItems("profileName",
				AirScreenRule.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableRules) {
			for (AirScreenRule savedRule : getDataSource().getRules()) {
				if (savedRule.getProfileName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableRules.removeAll(removeList);
		// For the OptionsTransfer component
		ruleOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("config.air.screen.rule.group.availableRules"),
				MgrUtil
						.getUserMessage("config.air.screen.rule.group.selectedRules"),
				availableRules, getDataSource().getRules(), "id", "value",
				"rules", "Rule");
	}

	private boolean prepareSaveObjects() throws Exception {
		return setSelectedRules();
	}

	protected boolean setSelectedRules() throws Exception {
		Set<AirScreenRule> groupRules = getDataSource().getRules();
		groupRules.clear();
		if (rules != null) {
			for (Long ruleId : rules) {
				AirScreenRule rule = findBoById(
						AirScreenRule.class, ruleId);
				if (rule != null) {
					groupRules.add(rule);
				}
			}
			if (groupRules.size() != rules.size()) {
				String tempStr[] = { getText("config.air.screen.rule.group.selectedRules") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
				return false;
			}
		}
		getDataSource().setRules(groupRules);
		log.info("setSelectedRules", "Air Screen Rule Group "
				+ getDataSource().getProfileName() + " has "
				+ groupRules.size() + " Rules.");
		return true;
	}

	private boolean validateRuleCount(AirScreenRuleGroup arg) {
		if (null != arg) {
			if (null == arg.getRules() || arg.getRules().isEmpty()) {
				addActionError(MgrUtil.getUserMessage("error.requiredField",
						getText("config.air.screen.rule.fullName")));
				return false;
			} else if (arg.getRules().size() > 8) {
				addActionError(getText("error.airscreen.overflow.rules"));
				return false;
			}
		}
		return true;
	}

	private OptionsTransfer ruleOptions;
	protected List<Long> rules;
	private Long rule;

	public OptionsTransfer getRuleOptions() {
		return ruleOptions;
	}

	public void setRules(List<Long> rules) {
		this.rules = rules;
	}

	public Long getRule() {
		return rule;
	}

	public void setRule(Long rule) {
		this.rule = rule;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_PROFILE_NAME = 1;
	public static final int COLUMN_COMMENT = 2;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_PROFILE_NAME:
			code = "config.air.screen.rule.group.name";
			break;
		case COLUMN_COMMENT:
			code = "config.air.screen.rule.group.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_PROFILE_NAME));
		columns.add(new HmTableColumn(COLUMN_COMMENT));
		return columns;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null != bo) {
			if (bo instanceof AirScreenRuleGroup) {
				AirScreenRuleGroup group = (AirScreenRuleGroup) bo;
				group.getRules().size();
			}
		}
		return null;
	}

}