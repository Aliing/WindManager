package com.ah.ui.actions.config;

/*
 * @author Chris Scheers
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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.InterRoaming;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class HiveProfilesAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveProfilesAction.class
			.getSimpleName());
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_L3TRAFFICPORT = 2;
	
	public static final int COLUMN_ENABLEPASSWORD = 3;
	
	public static final int COLUMN_RTSTHRESHOLD = 4;
	
	public static final int COLUMN_FRAGTHRESHOLD = 5;
	
	public static final int COLUMN_DESCRIPTION = 6;

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
			code = "config.hp.name";
			break;
		case COLUMN_L3TRAFFICPORT:
			code = "config.hp.l3TrafficPort";
			break;
		case COLUMN_ENABLEPASSWORD:
			code = "config.hp.enabledPassword";
			break;
		case COLUMN_RTSTHRESHOLD:
			code = "config.hp.rtsThreshold";
			break;
		case COLUMN_FRAGTHRESHOLD:
			code = "config.hp.fragThreshold";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.hp.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_L3TRAFFICPORT));
		columns.add(new HmTableColumn(COLUMN_ENABLEPASSWORD));
		columns.add(new HmTableColumn(COLUMN_RTSTHRESHOLD));
		columns.add(new HmTableColumn(COLUMN_FRAGTHRESHOLD));
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
				if (!setTitleAndCheckAccess(getText("config.title.hive"))) {
					return getLstForward();
				}
				setTabId(0);
				setSessionDataSource(new HiveProfile());
				prepareDependentObjects();
				return getProperReturnPath(INPUT, "hiveProfileJson");
			} else if ("create".equals(operation)) {
				setSelectedMacFilters();
				prepareSetSaveObjects();
				if (checkNameExists("hiveName", getDataSource().getHiveName())) {
					prepareDependentObjects();
					if(isJsonMode()) {
						jsonObject  = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getHiveName()));
						return "json";
					} else {
						return INPUT;
					}
				}
				if (checkSsidNameExists("ssidName", getDataSource()
						.getHiveName())) {
					prepareDependentObjects();
					if(isJsonMode()) {
						jsonObject  = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.existsInHiveSsid", getDataSource().getHiveName()));
						return "json";
					} else {
						return INPUT;
					}
				}
				errorMsgTmp = "";
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					if(isJsonMode()) {
						jsonObject  = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errorMsgTmp);
						return "json";
					} else {
						return INPUT;
					}
				}
				if (isJsonMode()) {
					jsonObject  = new JSONObject();
					try {
						Long addedIdTmp = createBo(dataSource);
						jsonObject.put("resultStatus", true);
						jsonObject.put("parentDomID", this.getParentDomID());
						jsonObject.put("addedId", addedIdTmp);
						jsonObject.put("addedName", this.getDataSource().getHiveName());
						return "json";
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
				} else {
					return createBo();
				}
			} else if (("create" + getLstForward()).equals(operation)) {
				setSelectedMacFilters();
				prepareSetSaveObjects();
				if (checkNameExists("hiveName", getDataSource().getHiveName())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (checkSsidNameExists("ssidName", getDataSource()
						.getHiveName())) {
					prepareDependentObjects();
					return INPUT;
				}
				
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					return INPUT;
				}
				
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("edit".equals(operation)) {
				setTabId(0);
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
				}
				addLstTitle(getText("config.title.hive.edit") + " '"
						+ getChangedHiveName() + "'");
				return getProperReturnPath(returnWord, "hiveProfileJson");
			} else if ("clone".equals(operation)) {
				setTabId(0);
				setSessionDataSource(new HiveProfile());
				long cloneId = getSelectedIds().get(0);
				HiveProfile profile = (HiveProfile) findBoById(boClass, cloneId,this);
				profile.setOwner(null);
				profile.setId(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				profile.setHiveName("");
				
				setCloneFields(profile, profile);

				setSessionDataSource(profile);

				// initDataSource(profile);
				prepareDependentObjects();
				addLstTitle(getText("config.title.hive"));
				return INPUT;
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
				} else {
					if (dosId != null) {
						if (selectDosType != null) {
							if (selectDosType.equals("mac")) {
								hiveDos = dosId;
							}
							if (selectDosType.equals("station")) {
								stationDos = dosId;
							}
						}
					}
					prepareSetSaveObjects();
					prepareDependentObjects();
					setId(dataSource.getId());
					setTabId(getLstTabId());
					if (getUpdateContext()) {
						removeLstTitle();
						MgrUtil
								.setSessionAttribute("CURRENT_TABID",
										getTabId());
						removeLstForward();
						removeLstTabId();
						setUpdateContext(false);
					} else {
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute(
								"CURRENT_TABID").toString()));
					}
					return getProperReturnPath(INPUT, "hiveProfileJson");
				}
			} else if ("update".equals(operation)) {
				if (dataSource != null) {
					setSelectedMacFilters();
					prepareSetSaveObjects();
				}
				errorMsgTmp = "";
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					if(isJsonMode()) {
						jsonObject  = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errorMsgTmp);
						return "json";
					} else {
						return INPUT;
					}
				}
				if (isJsonMode()) {
					jsonObject  = new JSONObject();
					try {
						updateBo(dataSource);
						jsonObject.put("resultStatus", true);
						return "json";
					} catch (Exception e) {
						//e.printStackTrace();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
				} else {
					return updateBo();
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					setSelectedMacFilters();
					prepareSetSaveObjects();
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("newHiveDos".equals(operation)
					|| "newStationDos".equals(operation)
					|| "newRoaming".equals(operation)
					|| "newMacFilter".equals(operation)
					|| "editHiveDos".equals(operation)
					|| "editStationDos".equals(operation)
					|| "editMacFilter".equals(operation)) {
				prepareSetSaveObjects();
				setSelectedMacFilters();
				clearErrorsAndMessages();
				addLstForward("hive");
				addLstTabId(tabId);
				return operation;
			} else if ("genenatePassword".equals(operation)){
				jsonObject = new JSONObject();
				jsonObject.put("v", MgrUtil.getRandomString(63,7));
				return "json";
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
		setSelectedL2Feature(L2_FEATURE_HIVE_PROFILES);
		setDataSource(HiveProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_HIVE_PROFILE;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		HiveProfile source = QueryUtil.findBoById(HiveProfile.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<HiveProfile> list = QueryUtil.executeQuery(HiveProfile.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (HiveProfile profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			HiveProfile up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setHiveName(profile.getHiveName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			setCloneFields(source, up);

			hmBos.add(up);
		}
		return hmBos;
	}
	
	public void setCloneFields(HiveProfile source, HiveProfile destination){
		Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
		for (MacFilter tempClass : source.getMacFilters()) {
			cloneMacFilters.add(tempClass);
		}
		destination.setMacFilters(cloneMacFilters);
	}

	public void prepareDependentObjects() throws Exception {
		if (getDataSource().getHiveDos() != null) {
			hiveDos = getDataSource().getHiveDos().getId();
		}
		if (getDataSource().getStationDos() != null) {
			stationDos = getDataSource().getStationDos().getId();
		}
		prepareAvailableFilters();
		prepareDosParameterProfiles();
		prepareRoamingProfiles();
	}
	
	public boolean checkMacFilterAction() throws Exception {
		Map<String, String> tmpMacFilter = new HashMap<String, String>();
		Set<String> totalMacOUI = new HashSet<String>();
		for (MacFilter lazyMacfilter : getDataSource().getMacFilters()) {
			MacFilter filter = findBoById(MacFilter.class, lazyMacfilter.getId(), this);
			for (MacFilterInfo filterInfo : filter.getFilterInfo()) {
				totalMacOUI.add(filterInfo.getMacOrOui().getMacOrOuiName());
				if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_PERMIT) {
					if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
							+ MacFilter.FILTER_ACTION_DENY) != null) {
						errorMsgTmp = MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName());
						addActionError(errorMsgTmp);
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_PERMIT, "true");
					}
				} else if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_DENY) {
					if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
							+ MacFilter.FILTER_ACTION_PERMIT) != null) {
						errorMsgTmp = MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName());
						addActionError(errorMsgTmp);
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_DENY, "true");
					}
				}
			}
		}
		if (totalMacOUI.size()>MacFiltersAction.MAX_MACFILTER_ENTER) {
			errorMsgTmp = getText("error.config.macFilter.maxNumber.reference",
					new String[]{String.valueOf(MacFiltersAction.MAX_MACFILTER_ENTER)} );
			addActionError(errorMsgTmp);
			return false;
		}
		return true;
	}

	public void prepareSetSaveObjects() throws Exception {
		if (hiveDos != null) {
			DosPrevention hiveDosClass = findBoById(
					DosPrevention.class, hiveDos);
			if (hiveDosClass == null && hiveDos != -1) {
				String tempStr[] = { getText("config.hiveProfile.hiveDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setHiveDos(hiveDosClass);
		}

		if (stationDos != null) {
			DosPrevention stationDosClass = findBoById(
					DosPrevention.class, stationDos);
			if (stationDosClass == null && stationDos != -1) {
				String tempStr[] = { getText("config.hiveProfile.stationDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setStationDos(stationDosClass);
		}
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkSsidNameExists(String name, Object value) {
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}

		List<?> boIds = QueryUtil.executeQuery(
				"select bo.id from " + SsidProfile.class.getSimpleName() + " bo", null, new FilterParams(
						name, value),domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.existsInHiveSsid",
					value.toString()));
			return true;
		} else {
			return false;
		}
	}
//	private JSONObject jsonObject = null;
//	
//	public String getJSONString() {
//		return jsonObject.toString();
//	}
	
	private Long dosId;

	private Long hiveDos;

	private Long stationDos;

	private Long macFilter;

	public Long getDosId() {
		return dosId;
	}

	public void setDosId(Long dosId) {
		this.dosId = dosId;
	}

	public Long getStationDos() {
		return stationDos;
	}

	public void setStationDos(Long stationDos) {
		this.stationDos = stationDos;
	}

	public void setMacFilter(Long macFilter) {
		this.macFilter = macFilter;
	}

	public Long getMacFilter() {
		return macFilter;
	}

	public Long getHiveDos() {
		return hiveDos;
	}

	public void setHiveDos(Long hiveDos) {
		this.hiveDos = hiveDos;
	}

	public void prepareAvailableFilters() throws Exception {
		List<CheckItem> availableFilters = getBoCheckItems("filterName", MacFilter.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (MacFilter savedMac : getDataSource().getMacFilters()) {
				if (savedMac.getFilterName()
						.equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);
		// For the OptionsTransfer component
		macFilterOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.hp.availableMacFilters"), MgrUtil
				.getUserMessage("config.hp.selectedMacFilters"),
				availableFilters, getDataSource().getMacFilters(), "id",
				"value", "macFilters", "MacFilter");
	}

	protected void setSelectedMacFilters() throws Exception {
		Set<MacFilter> hiveMacFilters = getDataSource().getMacFilters();
		hiveMacFilters.clear();
		if (macFilters != null) {

			for (Long filterId : macFilters) {
				MacFilter macFilter = findBoById(MacFilter.class,
						filterId);
				if (macFilter != null) {
					hiveMacFilters.add(macFilter);
				}
			}
			if (hiveMacFilters.size() != macFilters.size()) {
				String tempStr[] = { getText("config.hp.selectedMacFilters") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
			}
		}
		getDataSource().setMacFilters(hiveMacFilters);
		log.info("setSelectedMacFilters", "Hive Profiles "
				+ getDataSource().getHiveName() + " has "
				+ hiveMacFilters.size() + " MAC filters.");
	}

	@Override
	public HiveProfile getDataSource() {
		return (HiveProfile) dataSource;
	}

	public Range getRtsThresholdRange() {
		return getAttributeRange("rtsThreshold");
	}

	public Range getFragThresholdRange() {
		return getAttributeRange("fragThreshold");
	}

	public Range getL3TrafficPortRange() {
		return getAttributeRange("l3TrafficPort");
	}

	public Range getPollingIntervalRange() {
		return getAttributeRange("pollingInterval");
	}

	public Range getKeepAliveIntervalRange() {
		return getAttributeRange("keepAliveInterval");
	}

	public Range getKeepAliveAgeoutRange() {
		return getAttributeRange("keepAliveAgeout");
	}

	public Range getUpdateIntervalRange() {
		return getAttributeRange("updateInterval");
	}

	public Range getUpdateAgeoutRange() {
		return getAttributeRange("updateAgeout");
	}

	public int getHiveNameLength() {
		return getAttributeLength("hiveName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public int getPasswordLength() {
		return getAttributeLength("hivePassword");
	}

	public String getHidePassword() {
		if (!getDataSource().getEnabledPassword()) {
			return "none";
		} else {
			return "";
		}
	}
	
	public String getHidePasswordTextBox(){
		if (getDataSource().getGeneratePasswordType() == HiveProfile.GENERAtE_PASSWORK_MANUL&&
				getDataSource().getEnabledPassword()){
			return "";
		}
		return "none";
	}

	public String getHideThreshold() {
		if (!getDataSource().getEnabledThreshold()) {
			return "none";
		} else {
			return "";
		}
	}

	public String getHideL3Setting() {
		if (getDataSource().getEnabledL3Setting()) {
			return "";
		} else {
			return "none";
		}
	}

	protected void prepareDosParameterProfiles() {
		hiveDosParameterProfiles = getDosParameterProfiles(DosType.MAC);
		stationDosParameterProfiles = getDosParameterProfiles(DosType.MAC_STATION);
	}

	protected List<CheckItem> getDosParameterProfiles(DosType dosType) {
		return getBoCheckItems("dosPreventionName", DosPrevention.class,
				new FilterParams("dosType", dosType));
	}

	protected void prepareRoamingProfiles() {
		interRoaming = getBoCheckItems("roamingName", InterRoaming.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	protected List<CheckItem> hiveDosParameterProfiles;

	protected List<CheckItem> stationDosParameterProfiles;

	protected List<CheckItem> interRoaming;

	public List<CheckItem> getHiveDosParameterProfiles() {
		return hiveDosParameterProfiles;
	}

	public List<CheckItem> getStationDosParameterProfiles() {
		return stationDosParameterProfiles;
	}

	public List<CheckItem> getInterRoaming() {
		return interRoaming;
	}

	public void setInterRoaming(List<CheckItem> interRoaming) {
		this.interRoaming = interRoaming;
	}

	private String selectDosType;

	public String getSelectDosType() {
		return selectDosType;
	}

	public void setSelectDosType(String selectDosType) {
		this.selectDosType = selectDosType;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedHiveName() {
		return getDataSource().getHiveName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}

	public EnumItem[] getEnumConnectionThreshold() {
		EnumItem[] tmpItem = new EnumItem[36];
		short startKey=-90;
		for(short i=0; i<=35; i++){
			if (startKey==-85){
				tmpItem[i] = new EnumItem(startKey, startKey + " dBm (Low)");
			} else if (startKey==-80){
				tmpItem[i] = new EnumItem(startKey, startKey + " dBm (Medium)");
			} else if (startKey==-75){
				tmpItem[i] = new EnumItem(startKey, startKey + " dBm (High)");
			} else {
				tmpItem[i] = new EnumItem(startKey, startKey + " dBm");
			}
			startKey++;
		}
		
		return tmpItem;
	}
	
	public EnumItem[] getEnumPriority() {
		EnumItem[] tmpItem = new EnumItem[256];
		for(short i=0; i<=255; i++){
			tmpItem[i] = new EnumItem(i, String.valueOf(i));
		}
		return tmpItem;
	}

	OptionsTransfer macFilterOptions;

	public OptionsTransfer getMacFilterOptions() {
		return macFilterOptions;
	}

	public void setMacFilterOptions(OptionsTransfer macFilterOptions) {
		this.macFilterOptions = macFilterOptions;
	}

	protected List<Long> macFilters;

	public void setMacFilters(List<Long> macFilters) {
		this.macFilters = macFilters;
	}
	
	public String getGenenatePassword() {
		return "genenatePassword";
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveProfile) {
			dataSource = bo;
			if (getDataSource().getMacFilters()!=null) {
				getDataSource().getMacFilters().size();
			}
		}		
		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
		return null;
	}
	
	private String getProperReturnPath(String normalPath, String jsonModePath) {
		if (this.isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}
	
	public boolean isSavePermit() {
		if ("disabled".equals(this.getWriteDisabled())) {
			return false;
		}
		if (getDataSource() != null && getDataSource().getDefaultFlag()) {
			return false;
		}
		return true;
	}
	
	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private String errorMsgTmp;

}