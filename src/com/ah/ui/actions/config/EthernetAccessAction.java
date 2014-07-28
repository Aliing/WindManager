/**
 *@filename		EthernetAccessAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-10 PM 03:08:09
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class EthernetAccessAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.ethernet.access.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new EthernetAccess());
				prepareAvailableMacAddress();
				return INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				setSelectedMacAddress();
				setSelectedUserProfile();
				if (checkNameExists("ethernetName", getDataSource()
						.getEthernetName())) {
					prepareAvailableMacAddress();
					return INPUT;
				}
				if ("create".equals(operation)) {
					String returnValue = createBo();
					getLazyUserProfileInfo();
					return returnValue;
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					prepareAvailableMacAddress();
					userProfileId = getDataSource().getUserProfile().getId();
					addLstTitle(getText("config.ethernet.access.title.edit")
							+ " '" + getChangedName() + "'");
				}
				return strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					setSelectedMacAddress();
					setSelectedUserProfile();
				}
				if ("update".equals(operation)) {
					String returnValue = updateBo();
					getLazyUserProfileInfo();
					return returnValue;
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				EthernetAccess profile = (EthernetAccess) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setEthernetName("");
				profile.setVersion(null);
				Set<MacOrOui> setMac = new HashSet<MacOrOui>();
				for (MacOrOui newMac : profile.getMacAddress()) {
					setMac.add(newMac);
				}
				profile.setMacAddress(setMac);
				profile.setOwner(null);
				setSessionDataSource(profile);
				prepareAvailableMacAddress();
				userProfileId = getDataSource().getUserProfile().getId();
				addLstTitle(getText("config.ethernet.access.title"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					prepareAvailableMacAddress();
					setId(dataSource.getId());
					if (null == userProfileId) {
						userProfileId = getDataSource().getUserProfile().getId();
					}
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return INPUT;
				}
			} else if ("newMacAddress".equals(operation) || "newUserProfile".equals(operation)
				|| "editMacAddress".equals(operation) || "editUserProfile".equals(operation)) {
				setSelectedMacAddress();
				setSelectedUserProfile();
				clearErrorsAndMessages();
				addLstForward("ethernetAccess");
				return operation;
			} else {
				baseOperation();
				String returnValue = prepareBoList();
				getLazyUserProfileInfo();
				return returnValue;
			}
		} catch (Exception e) {
			String returnValue = prepareActionError(e);
			getLazyUserProfileInfo();
			return returnValue;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ETHERNET_ACCESS);
		setDataSource(EthernetAccess.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_ETHERNET;
	}

	protected OptionsTransfer macAddressOptions;

	public OptionsTransfer getMacAddressOptions() {
		return macAddressOptions;
	}

	public void setMacAddressOptions(OptionsTransfer macAddressOptions) {
		this.macAddressOptions = macAddressOptions;
	}
	
	public void getLazyUserProfileInfo() throws Exception {
		List<?> lst = getPage();
		List<EthernetAccess> lstValue = new ArrayList<EthernetAccess>();
		if (lst != null && lst.size() > 0) {
			String query = "select bo.userProfile.id from " + EthernetAccess.class.getSimpleName() + " bo";
			for (Object obj : lst) {
				EthernetAccess profile = (EthernetAccess) obj;
				List<?> lst_obj = QueryUtil.executeQuery(query, null,
						new FilterParams("id", profile.getId()));
				if (!lst_obj.isEmpty()) {
					Long id;
					if (lst_obj.get(0) != null) {
						id = Long.parseLong(lst_obj.get(0).toString());
						UserProfile up = QueryUtil
								.findBoById(UserProfile.class, id);
						if (up != null)
							profile.setUserProfile(up);
					}
				}
				lstValue.add(profile);
			}
			super.page = lstValue;
		}
	}

	public void prepareAvailableMacAddress() throws Exception {
		List<CheckItem> availableMac = getBoCheckItems("macOrOuiName", MacOrOui.class, new FilterParams("typeFlag",
			MacOrOui.TYPE_MAC_ADDRESS));
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		for (CheckItem oneItem : availableMac) {
			for (MacOrOui savedMacAddress : getDataSource().getMacAddress()) {
				if (savedMacAddress.getMacOrOuiName()
						.equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableMac.removeAll(removeList);
		macAddressOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ethernet.access.availabel.mac"),
				MgrUtil.getUserMessage("config.ethernet.access.selected.mac"),
				availableMac, getDataSource().getMacAddress(), "id", "value",
				"selectMacAddress", 128, "MacAddress", SIMPLE_OBJECT_MAC, 
				MAC_SUB_OBJECT_MAC, "", domainId);
	}

	public void setSelectedMacAddress() throws Exception {
		Set<MacOrOui> setMac = getDataSource().getMacAddress();
		setMac.clear();
		if (null != selectMacAddress) {
			for (Long mac_id : selectMacAddress) {
				MacOrOui mac = findBoById(MacOrOui.class, mac_id);
				if (mac != null) {
					setMac.add(mac);
				}
			}
		}
		if (!setMac.isEmpty())
			getDataSource().setMacAddress(setMac);
	}
	
	public void setSelectedUserProfile() throws Exception {
		if (null != userProfileId && userProfileId > -1) {
			UserProfile up = findBoById(
				UserProfile.class, userProfileId);
			getDataSource().setUserProfile(up);
		}
	}

	@Override
	public EthernetAccess getDataSource() {
		return (EthernetAccess) dataSource;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof EthernetAccess) {
			EthernetAccess etherAcc = (EthernetAccess) bo;
			if (etherAcc.getUserProfile() != null)
				etherAcc.getUserProfile().getId();
			if (etherAcc.getMacAddress() != null)
				etherAcc.getMacAddress().size();
		}
		return null;
	}
	
	public boolean getDisableUserProfile() {
		if (null != getDataSource()) {
			String where = "ethernetAccess.id = :s1 OR ethernetBridge.id = :s2 OR ethernetAccessEth1.id = :s3 OR " +
					"ethernetBridgeEth1.id = :s4 OR ethernetAccessRed.id = :s5 OR ethernetBridgeRed.id = :s6 OR " +
					"ethernetAccessAgg.id = :s7 OR ethernetBridgeAgg.id = :s8";
			Object[] values = new Object[8];
			for (int i = 0; i < 8; i ++) {
				values[i] = getDataSource().getId();
			}
			List<?> boIds = QueryUtil.executeQuery(
					"select bo.id from " + ConfigTemplate.class.getSimpleName() + " bo", null, new FilterParams(
						where, values));
			return !boIds.isEmpty();
		}
		return false;
	}

	public String getNewButtonDisabled() {
		return (!"".equals(getWriteDisabled()) || getDisableUserProfile()) ? "disabled" : "";
	}

	protected List<Long> selectMacAddress;
	
	private Long macAddress;

	public int getNameLength() {
		return getAttributeLength("ethernetName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getEthernetName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public String getHideIdle() {
		return getDataSource().isMacLearning() ? "" : "none";
	}

	public List<Long> getSelectMacAddress() {
		return selectMacAddress;
	}

	public void setSelectMacAddress(List<Long> selectMacAddress) {
		this.selectMacAddress = selectMacAddress;
	}
	
	public List<CheckItem> getUserProfileList() {
		return getBoCheckItems("userProfileName", UserProfile.class, null);
	}
	
	private Long userProfileId;

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_USER_PROFILE = 2;
	
	public static final int COLUMN_MAC_LEARN = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;
	
	/**
	 * get the description of column by id
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
		case COLUMN_USER_PROFILE:
			code = "config.ethernet.access.user.profile";
			break;
		case COLUMN_MAC_LEARN:
			code = "config.ethernet.access.macLearn";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipFilter.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(4);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_USER_PROFILE));
		columns.add(new HmTableColumn(COLUMN_MAC_LEARN));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public Long getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress(Long macAddress)
	{
		this.macAddress = macAddress;
	}

}