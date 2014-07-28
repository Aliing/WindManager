/**
 *@filename		AccessConsoleAction.java
 *@version
 *@author		Fiona
 *@createtime	2008-9-12 PM 02:06:01
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
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class AccessConsoleAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
  			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.access.console.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new AccessConsole());
				prepareAvailableMacFilter();
				return isJsonMode() ? "accessConsoleDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				setSelectedMacFilter();
				if (checkNameExists("consoleName", getDataSource()
						.getConsoleName())) {
					prepareAvailableMacFilter();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;								
				}
				if (SsidProfile.KEY_MGMT_OPEN == getDataSource().getMgmtKey()) {
					getDataSource().setAsciiKey("");
				}
				
				if (!checkMacFilterAction()) {
					prepareAvailableMacFilter();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getConsoleName());
					try {
						id = createBo(dataSource);
						jsonObject.put("addedId", id);
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
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					prepareAvailableMacFilter();
					addLstTitle(getText("config.access.console.title.edit") + " '" + getChangedName() + "'");
				}
				return isJsonMode() ? "accessConsoleDlg" : strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					setSelectedMacFilter();
					if (SsidProfile.KEY_MGMT_OPEN == getDataSource().getMgmtKey()) {
						getDataSource().setAsciiKey("");
					}
				}
				
				if (!checkMacFilterAction()) {
					prepareAvailableMacFilter();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;
				}
				
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", true);
						return "json";
					} else {
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AccessConsole profile = (AccessConsole) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setConsoleName("");
				profile.setVersion(null);
				profile.setMacFilters(getCloneMacFilter(profile));
				profile.setOwner(null);
				setSessionDataSource(profile);
				prepareAvailableMacFilter();
				addLstTitle(getText("config.ethernet.access.title"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					prepareAvailableMacFilter();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return isJsonMode() ? "accessConsoleDlg" : INPUT;
				}
			} else if ("newMacFilter".equals(operation) || "editMacFilter".equals(operation)) {
				setSelectedMacFilter();
				clearErrorsAndMessages();
				addLstForward("accessConsole");
				return operation;
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
		setSelectedL2Feature(L2_FEATURE_ACCESS_CONSOLE);
		setDataSource(AccessConsole.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_ACCESS;
	}
	
	private Set<MacFilter> getCloneMacFilter(AccessConsole profile) {
		Set<MacFilter> setMac = new HashSet<MacFilter>();
		setMac.addAll(profile.getMacFilters());
		return setMac;
	}

	protected OptionsTransfer macFilterOptions;

	public void prepareAvailableMacFilter() throws Exception {
		List<CheckItem> availableMacFilter = getBoCheckItems("filterName", MacFilter.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		for (CheckItem oneItem : availableMacFilter) {
			for (MacFilter savedMacFilter : getDataSource().getMacFilters()) {
				if (savedMacFilter.getFilterName()
						.equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableMacFilter.removeAll(removeList);
		macFilterOptions = new OptionsTransfer(MgrUtil
			.getUserMessage("config.ssid.availableMacFilters"), MgrUtil
			.getUserMessage("config.ssid.selectedMacFilters"),
			availableMacFilter, getDataSource().getMacFilters(), "id",
			"value", "selectMacFilter", "MacFilter");
	}

	public void setSelectedMacFilter() throws Exception {
		Set<MacFilter> setMac = getDataSource().getMacFilters();
		setMac.clear();
		if (null != selectMacFilter) {
			for (Long mac_id : selectMacFilter) {
				MacFilter mac = findBoById(MacFilter.class, mac_id);
				if (mac != null) {
					setMac.add(mac);
				}
			}
		}
		if (!setMac.isEmpty()) {
			getDataSource().setMacFilters(setMac);
		}
	}

	@Override
	public AccessConsole getDataSource() {
		return (AccessConsole) dataSource;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AccessConsole) {
			AccessConsole accessCon = (AccessConsole) bo;
			if (accessCon.getMacFilters() != null)
				accessCon.getMacFilters().size();
		}
		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
		return null;
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
						addActionError(MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName()));
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_PERMIT, "true");
					}
				} else if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_DENY) {
					if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
							+ MacFilter.FILTER_ACTION_PERMIT) != null) {
						addActionError(MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName()));
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_DENY, "true");
					}
				}
			}
		}
		if (totalMacOUI.size()>MacFiltersAction.MAX_MACFILTER_ENTER) {
			addActionError(getText("error.config.macFilter.maxNumber.reference",
					new String[]{String.valueOf(MacFiltersAction.MAX_MACFILTER_ENTER)} ));
			return false;
		}
		return true;
	}
	
	protected List<Long> selectMacFilter;
	
	private Long macFilter;

	public Long getMacFilter()
	{
		return macFilter;
	}

	public void setMacFilter(Long macFilter)
	{
		this.macFilter = macFilter;
	}

	public List<Long> getSelectMacFilter()
	{
		return selectMacFilter;
	}

	public void setSelectMacFilter(List<Long> selectMacFilter)
	{
		this.selectMacFilter = selectMacFilter;
	}

	public int getNameLength() {
		return getAttributeLength("consoleName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getConsoleName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public OptionsTransfer getMacFilterOptions()
	{
		return macFilterOptions;
	}

	public void setMacFilterOptions(OptionsTransfer macFilterOptions)
	{
		this.macFilterOptions = macFilterOptions;
	}
	
	public EnumItem[] getEnumConsoleMode() {
		return AccessConsole.ENUM_ACCESS_CONSOLE_MODE;
	}
	
	public EnumItem[] getEnumKeyMgmt1() {
		return new EnumItem[] { new EnumItem(SsidProfile.KEY_MGMT_WPA_PSK,
			MgrUtil.getEnumString("enum.keyMgmt."
					+ SsidProfile.KEY_MGMT_WPA_PSK)) };
	}
	
	public EnumItem[] getEnumKeyMgmt2() {
		return new EnumItem[] { new EnumItem(SsidProfile.KEY_MGMT_WPA2_PSK,
			MgrUtil.getEnumString("enum.keyMgmt."
					+ SsidProfile.KEY_MGMT_WPA2_PSK)) };
	}
	
	public EnumItem[] getEnumKeyMgmt3() {
		return new EnumItem[] { new EnumItem(SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK,
			MgrUtil.getEnumString("enum.keyMgmt."
					+ SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK)) };
	}
	
	public EnumItem[] getEnumKeyMgmt4() {
		return new EnumItem[] { new EnumItem(SsidProfile.KEY_MGMT_OPEN,
			MgrUtil.getEnumString("enum.keyMgmt."
					+ SsidProfile.KEY_MGMT_OPEN)) };
	}
	
	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}
	
	public EnumItem[] getEnumEncryption() {
		EnumItem[] resultEnum = new EnumItem[] {new EnumItem(SsidProfile.KEY_ENC_NONE, "NONE")};
		switch (getDataSource().getMgmtKey()) {
			case SsidProfile.KEY_MGMT_OPEN:
				return resultEnum;
			case SsidProfile.KEY_MGMT_WPA2_PSK:
			case SsidProfile.KEY_MGMT_WPA_PSK:
				return new EnumItem[] {new EnumItem(SsidProfile.KEY_ENC_CCMP, "CCMP(AES)"),
					new EnumItem(SsidProfile.KEY_ENC_TKIP, "TKIP")};
			case SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK:
				return new EnumItem[] {new EnumItem(SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP, "Auto-TKIP or CCMP (AES)")};
			default:
				return resultEnum;
		}
	}
	
	public String getHideKeyManagementNote() {
		if (getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WPA_PSK) {
			return "";
		}
		return "none";
	}
	
	public String getHideAsciiKey() {
		return SsidProfile.KEY_MGMT_OPEN != getDataSource().getMgmtKey() ? "" : "none";
	}
	
	public String getKeyValue() {
		return getDataSource().getAsciiKey();
	}
	
	public Range getMaxClientRange() {
		return getAttributeRange("maxClient");
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		AccessConsole source = QueryUtil.findBoById(AccessConsole.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<AccessConsole> list = QueryUtil.executeQuery(AccessConsole.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (AccessConsole profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			AccessConsole up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setConsoleName(profile.getConsoleName());
			up.setOwner(profile.getOwner());
			up.setMacFilters(getCloneMacFilter(source));
			hmBos.add(up);
		}
		return hmBos;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_MODE = 2;
	
	public static final int COLUMN_CLIENT = 3;
	
	public static final int COLUMN_KEY = 4;
	
	public static final int COLUMN_ENCRYPTION = 5;
	
	public static final int COLUMN_DESCRIPTION = 6;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.ipFilter.name";
			break;
		case COLUMN_MODE:
			code = "config.access.console.mode";
			break;
		case COLUMN_CLIENT:
			code = "config.ssid.maxClient";
			break;
		case COLUMN_KEY:
			code = "config.ssid.keyManagement";
			break;
		case COLUMN_ENCRYPTION:
			code = "config.ssid.encriptionMethord";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipFilter.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_MODE));
		columns.add(new HmTableColumn(COLUMN_CLIENT));
		columns.add(new HmTableColumn(COLUMN_KEY));
		columns.add(new HmTableColumn(COLUMN_ENCRYPTION));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

}