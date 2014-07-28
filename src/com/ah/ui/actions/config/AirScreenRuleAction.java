package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.AirScreenAction;
import com.ah.bo.network.AirScreenBehavior;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenSource;
import com.ah.bo.network.MacOrOui;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AirScreenRuleAction extends BaseAction implements QueryBo {

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
				if (!setTitleAndCheckAccess(getText("config.title.airscreen.rule"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new AirScreenRule());
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
				if (!validateElementsLimit(getDataSource())) {
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
				addLstTitle(getText("config.title.airscreen.rule.edit") + " '"
						+ getChangedName() + "'");
				return returnWord;
			} else if ("update".equals(operation)) {
				if (dataSource == null) {
					prepareDependentObjects();
					return INPUT;
				}
				prepareSaveObjects();
				if (!validateElementsLimit(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				return updateBo();
			} else if (("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSaveObjects();
				}
				if (!validateElementsLimit(getDataSource())) {
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
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AirScreenRule profile = (AirScreenRule) findBoById(boClass,
						cloneId, this);
				if (null != profile) {
					profile.setOwner(null);
					profile.setId(null);
					profile.setVersion(null);
					profile.setProfileName("");

					setCloneFields(profile, profile);

					setSessionDataSource(profile);
					prepareDependentObjects();
					addLstTitle(getText("config.title.airscreen.rule"));
					return INPUT;
				} else {
					return prepareBoList();
				}
			} else if ("newOui".equals(operation)
					|| "editOui".equals(operation)) {
				prepareSaveObjects();
				clearErrorsAndMessages();
				addLstForward("airScreenRule");
				return operation;
			} else if ("viewSources".equals(operation)
					|| "viewBehaviors".equals(operation)
					|| "viewActions".equals(operation)) {
				prepareSaveObjects();
				clearErrorsAndMessages();
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
					prepareOui();
					prepareDependentObjects();
					setId(dataSource.getId());
					return INPUT;
				}
			} else if ("newSource".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				getDataSource().setTempSource(new AirScreenSource());
				sourceOuiId = null;
				prepareDependentObjects();
				return INPUT;
			} else if ("createSource".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				createSource();
				prepareDependentObjects();
				return INPUT;
			} else if ("editSource".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				editSource();
				prepareDependentObjects();
				return INPUT;
			} else if ("updateSource".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				updateSource();
				prepareDependentObjects();
				return INPUT;
			} else if ("clearSource".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				clearSource();
				prepareDependentObjects();
				return INPUT;
			} else if ("newBehavior".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				getDataSource().setTempBehavior(new AirScreenBehavior());
				prepareDependentObjects();
				return INPUT;
			} else if ("createBehavior".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				createBehavior();
				prepareDependentObjects();
				return INPUT;
			} else if ("editBehavior".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				editBehavior();
				prepareDependentObjects();
				return INPUT;
			} else if ("updateBehavior".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				updateBehavior();
				prepareDependentObjects();
				return INPUT;
			} else if ("clearBehavior".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				clearBehavior();
				prepareDependentObjects();
				return INPUT;
			} else if ("newAction".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				getDataSource().setTempAction(new AirScreenAction());
				prepareDependentObjects();
				return INPUT;
			} else if ("createAction".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				createAction();
				prepareDependentObjects();
				return INPUT;
			} else if ("editAction".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				editAction();
				prepareDependentObjects();
				return INPUT;
			} else if ("updateAction".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				updateAction();
				prepareDependentObjects();
				return INPUT;
			} else if ("clearAction".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareSaveObjects();
				clearAction();
				prepareDependentObjects();
				return INPUT;
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
		setSelectedL2Feature(L2_FEATURE_AIR_SCREEN_RULE);
		setDataSource(AirScreenRule.class);
		keyColumnId = COLUMN_PROFILE_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AIR_SCREEN_RULE;
	}

	@Override
	public AirScreenRule getDataSource() {
		return (AirScreenRule) dataSource;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		AirScreenRule source = QueryUtil.findBoById(AirScreenRule.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<AirScreenRule> list = QueryUtil.executeQuery(AirScreenRule.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (AirScreenRule profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			AirScreenRule ar = source.clone();
			if (null == ar) {
				continue;
			}
			setCloneFields(source, ar);
			ar.setId(profile.getId());
			ar.setVersion(profile.getVersion());
			ar.setProfileName(profile.getProfileName());
			ar.setOwner(profile.getOwner());

			if (!validateElementsLimit(ar)) {
				return null;
			}
			hmBos.add(ar);
		}
		return hmBos;
	}

	private void setCloneFields(AirScreenRule source, AirScreenRule destination) {
		Set<AirScreenBehavior> set = new HashSet<AirScreenBehavior>();
		for (AirScreenBehavior sc : source.getBehaviors()) {
			set.add(sc);
		}
		Set<AirScreenAction> set1 = new HashSet<AirScreenAction>();
		for (AirScreenAction sc : source.getActions()) {
			set1.add(sc);
		}
		destination.setBehaviors(set);
		destination.setActions(set1);
	}

	public String getChangedName() {
		return getDataSource().getProfileName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public boolean checkNameExists(Class<? extends HmBo> boClass, String name,
			Object value) {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ boClass.getSimpleName(), null, new FilterParams(name, value),
				domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists", value
					.toString()));
			return true;
		} else {
			return false;
		}
	}

	private boolean validateElementsLimit(AirScreenRule as) {
		if (null != as) {
			// behaviors count <=4
			if (null != as.getBehaviors() && as.getBehaviors().size() > 4) {
				addActionError(getText("error.airscreen.overflow.behaviors"));
				return false;
			}
			// actions count <=8
			if (null != as.getActions() && as.getActions().size() > 8) {
				addActionError(getText("error.airscreen.overflow.actions"));
				return false;
			}
			// behaviors type cannot repeat
			if (null != as.getBehaviors()) {
				Set<AirScreenBehavior> set = as.getBehaviors();
				Map<Short, Integer> map = new HashMap<Short, Integer>();
				for (AirScreenBehavior bv : set) {
					short type = bv.getType();
					if (map.get(type) == null) {
						map.put(type, 0);
					}
					map.put(type, map.get(type) + 1);
				}
				for (Short type : map.keySet()) {
					int count = map.get(type);
					if (count > 1) {
						addActionError(getText("error.airscreen.behavior.type.repeated"));
						return false;
					}
				}
			}
			// actions type cannot repeat
			if (null != as.getActions()) {
				Set<AirScreenAction> set = as.getActions();
				Map<Short, Integer> map = new HashMap<Short, Integer>();
				for (AirScreenAction ac : set) {
					short type = ac.getType();
					if (map.get(type) == null) {
						map.put(type, 0);
					}
					map.put(type, map.get(type) + 1);
				}
				for (Short type : map.keySet()) {
					int count = map.get(type);
					if (count > 1) {
						addActionError(getText("error.airscreen.action.type.repeated"));
						return false;
					}
				}
			}
		}
		return true;
	}

	private void updateAction() throws Exception {
		Long actionId = getDataSource().getTempAction().getId();
		if (null != actionId) {
			BoMgmt.updateBo(getDataSource().getTempAction(), getUserContext(),
					getSelectedL2FeatureKey());
			// getDataSource().getActions().add(getDataSource().getTempAction());
			getDataSource().setTempAction(new AirScreenAction());
			getDataSource().setActionCreationDisplayStyle("none");
		}
	}

	private void editAction() throws Exception {
		if (null != actionId) {
			AirScreenAction action = findBoById(
					AirScreenAction.class, actionId);
			if (null != action) {
				getDataSource().setTempAction(action);
				getDataSource().setActionCreationDisplayStyle("");
			}
		}
	}

	private void createAction() throws Exception {
		BoMgmt.createBo(getDataSource().getTempAction(), getUserContext(),
				getSelectedL2FeatureKey());
		// getDataSource().getActions().add(getDataSource().getTempAction());
		getDataSource().setTempAction(new AirScreenAction());
		getDataSource().setActionCreationDisplayStyle("none");
	}

	private void clearAction() throws Exception {
		getDataSource().setTempAction(new AirScreenAction());
		getDataSource().setActionCreationDisplayStyle("none");
	}

	private void updateBehavior() throws Exception {
		Long behaviorId = getDataSource().getTempBehavior().getId();
		if (null != behaviorId) {
			BoMgmt.updateBo(getDataSource().getTempBehavior(),
					getUserContext(), getSelectedL2FeatureKey());
			// getDataSource().getBehaviors().add(getDataSource().getTempBehavior());
			getDataSource().setTempBehavior(new AirScreenBehavior());
			getDataSource().setBehaviorCreationDisplayStyle("none");
		}
	}

	private void editBehavior() throws Exception {
		if (null != behaviorId) {
			AirScreenBehavior behavior = findBoById(
					AirScreenBehavior.class, behaviorId);
			if (null != behavior) {
				getDataSource().setTempBehavior(behavior);
				getDataSource().setBehaviorCreationDisplayStyle("");
			}
		}
	}

	private void createBehavior() throws Exception {
		BoMgmt.createBo(getDataSource().getTempBehavior(), getUserContext(),
				getSelectedL2FeatureKey());
		// getDataSource().getBehaviors().add(getDataSource().getTempBehavior());
		getDataSource().setTempBehavior(new AirScreenBehavior());
		getDataSource().setBehaviorCreationDisplayStyle("none");
	}

	private void clearBehavior() throws Exception {
		getDataSource().setTempBehavior(new AirScreenBehavior());
		getDataSource().setBehaviorCreationDisplayStyle("none");
	}

	private void updateSource() throws Exception {
		Long sourceId = getDataSource().getTempSource().getId();
		if (null != sourceId) {
			if (null != sourceOuiId) {
				MacOrOui oui = findBoById(MacOrOui.class,
						sourceOuiId);
				if (oui == null && sourceOuiId != -1) {
					String tempStr[] = { getText("config.air.screen.source.oui") };
					addActionError(getText("info.ssid.warning", tempStr));
					return;
				}
				getDataSource().getTempSource().setOui(oui);
				BoMgmt.updateBo(getDataSource().getTempSource(),
						getUserContext(), getSelectedL2FeatureKey());
				getDataSource().setSource(getDataSource().getTempSource());
				getDataSource().setTempSource(new AirScreenSource());
				getDataSource().setSourceCreationDisplayStyle("none");
			}
		}
	}

	private void editSource() throws Exception {
		if (null != sourceId) {
			AirScreenSource source = findBoById(
					AirScreenSource.class, sourceId);
			if (null != source) {
				getDataSource().setTempSource(source);
				getDataSource().setSourceCreationDisplayStyle("");
				if (null != source.getOui()) {
					sourceOuiId = source.getOui().getId();
					getDataSource().setInputOuiText(
							source.getOui().getMacOrOuiName());
				}
			}
		}
	}

	private void createSource() throws Exception {
		MacOrOui oui;
		// select the exist mac oui object
		if (sourceOuiId != null && sourceOuiId != -1) {
			oui = findBoById(MacOrOui.class, sourceOuiId);
			// create new mac oui object by input value
		} else {
			oui = CreateObjectAuto.createNewMAC(getDataSource()
					.getInputOuiText(), MacOrOui.TYPE_MAC_OUI, getDomain(),
					"For Air Screen Rule");
		}
		if (oui == null) {
			String tempStr[] = { getText("config.air.screen.source.oui") };
			addActionError(getText("info.ssid.warning", tempStr));
			return;
		}
		getDataSource().getTempSource().setOui(oui);
		BoMgmt.createBo(getDataSource().getTempSource(), getUserContext(),
				getSelectedL2FeatureKey());
		getDataSource().setSource(getDataSource().getTempSource());
		getDataSource().setTempSource(new AirScreenSource());
		getDataSource().setSourceCreationDisplayStyle("none");
	}

	private void clearSource() throws Exception {
		getDataSource().setTempSource(new AirScreenSource());
		getDataSource().setSourceCreationDisplayStyle("none");
	}

	private void prepareOui() throws Exception {
		if (null != sourceOuiId) {
			MacOrOui oui = findBoById(MacOrOui.class, sourceOuiId);
			if (null != oui) {
				getDataSource().getTempSource().setOui(oui);
			}
		}
	}

	private boolean prepareSaveObjects() throws Exception {
		boolean r1 = setSelectedSource();
		boolean r2 = setSelectedBehaviors();
		boolean r3 = setSelectedActions();
		boolean r4 = setSelectedMacOui();
		return r1 && r2 && r3 && r4;
	}

	private boolean setSelectedMacOui() throws Exception {
		if (null != sourceOuiId) {
			MacOrOui oui = findBoById(MacOrOui.class, sourceOuiId);
			if (null == oui && sourceOuiId != -1) {
				String tempStr[] = { getText("config.air.screen.source.oui") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().getTempSource().setOui(oui);
		}
		return true;
	}

	private boolean setSelectedSource() throws Exception {
		if (null != sourceId) {
			AirScreenSource source = findBoById(
					AirScreenSource.class, sourceId);
			if (null == source && sourceId != -1) {
				String tempStr[] = { getText("config.air.screen.rule.source") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setSource(source);
		}
		return true;
	}

	private boolean setSelectedBehaviors() throws Exception {
		Set<AirScreenBehavior> ruleBehaviors = getDataSource().getBehaviors();
		ruleBehaviors.clear();
		if (behaviors != null) {
			for (Long behaviorId : behaviors) {
				AirScreenBehavior behavior = findBoById(
						AirScreenBehavior.class, behaviorId);
				if (behavior != null) {
					ruleBehaviors.add(behavior);
				}
			}
			if (ruleBehaviors.size() != behaviors.size()) {
				String tempStr[] = { getText("config.air.screen.rule.selectedBehaviors") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
				return false;
			}
		}
		getDataSource().setBehaviors(ruleBehaviors);
		log.info("setSelectedBehaviors", "Air Screen Rule "
				+ getDataSource().getProfileName() + " has "
				+ ruleBehaviors.size() + " Behaviors.");
		return true;
	}

	private boolean setSelectedActions() throws Exception {
		Set<AirScreenAction> ruleActions = getDataSource().getActions();
		ruleActions.clear();
		if (actions != null) {
			for (Long actionId : actions) {
				AirScreenAction action = findBoById(
						AirScreenAction.class, actionId);
				if (action != null) {
					ruleActions.add(action);
				}
			}
			if (ruleActions.size() != actions.size()) {
				String tempStr[] = { getText("config.air.screen.rule.selectedActions") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
				return false;
			}
		}
		getDataSource().setActions(ruleActions);
		log.info("setSelectedActions", "Air Screen Rule "
				+ getDataSource().getProfileName() + " has "
				+ ruleActions.size() + " Actions.");
		return true;
	}

	private void prepareDependentObjects() throws Exception {
		if (null != getDataSource().getSource()) {
			sourceId = getDataSource().getSource().getId();
		}
		if (null != getDataSource().getTempSource().getOui()) {
			sourceOuiId = getDataSource().getTempSource().getOui().getId();
			getDataSource().setInputOuiText(
					getDataSource().getTempSource().getOui().getMacOrOuiName());
		}
		prepareSource();
		prepareMacOui();
		prepareAvailableBehaviors();
		prepareAvailableActions();
		prepareOthers();
	}

	private void prepareOthers() {

	}

	private void prepareAvailableActions() throws Exception {
		List<CheckItem> availableActions = getBoCheckItems("profileName",
				AirScreenAction.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableActions) {
			for (AirScreenAction savedAction : getDataSource().getActions()) {
				if (savedAction.getProfileName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableActions.removeAll(removeList);
		// For the OptionsTransfer component
		actionOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("config.air.screen.rule.availableActions"),
				MgrUtil
						.getUserMessage("config.air.screen.rule.selectedActions"),
				availableActions, getDataSource().getActions(), "id", "value",
				"actions", "ActionContent");
	}

	private void prepareAvailableBehaviors() throws Exception {
		List<CheckItem> availableBehaviors = getBoCheckItems("profileName",
				AirScreenBehavior.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableBehaviors) {
			for (AirScreenBehavior savedBehavor : getDataSource()
					.getBehaviors()) {
				if (savedBehavor.getProfileName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableBehaviors.removeAll(removeList);
		// For the OptionsTransfer component
		behaviorOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("config.air.screen.rule.availableBehaviors"),
				MgrUtil
						.getUserMessage("config.air.screen.rule.selectedBehaviors"),
				availableBehaviors, getDataSource().getBehaviors(), "id",
				"value", "behaviors", "BehaviorContent");
	}

	private void prepareSource() {
		sources = getBoCheckItems("profileName", AirScreenSource.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	private void prepareMacOui() {
		ouis = getBoCheckItems("macOrOuiName", MacOrOui.class,
				new FilterParams("typeFlag", MacOrOui.TYPE_MAC_OUI),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public int getProfileNameLength() {
		return getAttributeLength("profileName");
	}

	public int getCommentLength() {
		return getAttributeLength("comment");
	}

	public int getSourceNameLength() {
		return HmBoBase.DEFAULT_STRING_LENGTH;
	}

	public int getSourceCommentLength() {
		return HmBoBase.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getBehaviorNameLength() {
		return HmBoBase.DEFAULT_STRING_LENGTH;
	}

	public int getBehaviorCommentLength() {
		return HmBoBase.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getActionNameLength() {
		return HmBoBase.DEFAULT_STRING_LENGTH;
	}

	public int getActionCommentLength() {
		return HmBoBase.DEFAULT_DESCRIPTION_LENGTH;
	}

	public EnumItem[] getSourceTypes() {
		return AirScreenSource.TYPE;
	}

	public EnumItem[] getSourceEncryptionModes() {
		return AirScreenSource.ENCRYPTION_MODE;
	}

	public EnumItem[] getSourceAuthModes() {
		return AirScreenSource.AUTH_MODE;
	}

	public EnumItem[] getBehaviorTypes() {
		return AirScreenBehavior.TYPE;
	}

	public EnumItem[] getBehaviorCases() {
		return AirScreenBehavior.CONNECTION_CASE;
	}

	public EnumItem[] getActionTypes() {
		return AirScreenAction.TYPE;
	}

	// indicate the source section is under edit, not create
	public boolean getSourceNameDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempSource().getId() != null;
	}

	// indicate the behavior section is under edit, not create
	public boolean getBehaviorNameDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempBehavior().getId() != null;
	}

	// indicate the action section is under edit, not create
	public boolean getActionNameDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempAction().getId() != null;
	}

	public String getEncryptionTrStyle() {
		if (null != getDataSource()) {
			short auth = getDataSource().getTempSource().getAuthMode();
			if (auth == AirScreenSource.AUTH_MODE_WPA
					|| auth == AirScreenSource.AUTH_MODE_WPA2_8021X
					|| auth == AirScreenSource.AUTH_MODE_WPA2_PSK
					|| auth == AirScreenSource.AUTH_MODE_WPA_8021X
					|| auth == AirScreenSource.AUTH_MODE_WPA_PSK) {
				return "";
			}
		}
		return "none";
	}

	public String getActionIntervalTrStyle() {
		if (null != getDataSource()) {
			short type = getDataSource().getTempAction().getType();
			if (type == AirScreenAction.TYPE_LOCAL_BAN) {
				return "";
			}
		}
		return "none";
	}

	private Long sourceId;
	private List<CheckItem> sources;
	private OptionsTransfer behaviorOptions;
	protected List<Long> behaviors;
	private OptionsTransfer actionOptions;
	protected List<Long> actions;

	public OptionsTransfer getActionOptions() {
		return actionOptions;
	}

	public void setActions(List<Long> actions) {
		this.actions = actions;
	}

	public OptionsTransfer getBehaviorOptions() {
		return behaviorOptions;
	}

	public void setBehaviors(List<Long> behaviors) {
		this.behaviors = behaviors;
	}

	public List<CheckItem> getSources() {
		return sources;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/*
	 * Source Fields
	 */

	private Long sourceOuiId;
	private List<CheckItem> ouis;

	/*
	 * Behavior Fields
	 */
	private Long behaviorId;

	/*
	 * Action Fields
	 */
	private Long actionId;

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public void setBehaviorId(Long behaviorId) {
		this.behaviorId = behaviorId;
	}

	public Long getBehaviorId() {
		return behaviorId;
	}

	public List<CheckItem> getOuis() {
		return ouis;
	}

	public Long getSourceOuiId() {
		return sourceOuiId;
	}

	public void setSourceOuiId(Long sourceOuiId) {
		this.sourceOuiId = sourceOuiId;
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
			code = "config.air.screen.rule.name";
			break;
		case COLUMN_COMMENT:
			code = "config.air.screen.rule.comment";
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
			if (bo instanceof AirScreenRule) {
				AirScreenRule rule = (AirScreenRule) bo;
				rule.getBehaviors().size();
				rule.getActions().size();
				if (null != rule.getSource()) {
					rule.getSource().getId();
				}
			}
		}
		return null;
	}

}