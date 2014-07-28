/**
 *@filename		VlanDhcpServerAction.java
 *@version
 *@author		Fiona
 *@createtime	2008-10-9 AM 11:11:07
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
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DhcpServerIpPool;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhEncoder;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class VlanDhcpServerAction extends BaseAction implements QueryBo {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.network.object.dhcp.server.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setTabId(0);
				setSessionDataSource(new VlanDhcpServer());
				return getReturnPathWithJsonMode(INPUT, "vlanDhcpServerJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				setTabId(0);
				setPoolNewOrApplyButton();
				setCustomNewOrApplyButton();
				setDhcpServerType();
				if (isJsonMode()) {
					if (checkNameExists("profileName", getDataSource()
							.getProfileName())
							|| !updateDhcpOptionValue()) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getDhcpNetmask()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getProfileName());
					try {
							id = createBo(dataSource);
							jsonObject.put("newObjId", id);
							setUpdateContext(true);
							getLstForward();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if (checkNameExists("profileName", getDataSource()
							.getProfileName())
							|| !updateDhcpOptionValue()) {
						return INPUT;
					}
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setTabId(0);
				String strForward = editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.network.object.dhcp.server.edit")
							+ " '" + getChangedName() + "'");
					setPoolNewOrApplyButton();
					setCustomNewOrApplyButton();
				}
				return getReturnPathWithJsonMode(strForward, "vlanDhcpServerJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try{
						if (dataSource != null) {
							setPoolNewOrApplyButton();
							setCustomNewOrApplyButton();
							setDhcpServerType();
							if (!updateDhcpOptionValue()) {
								setTabId(0);
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getDhcpNetmask()));
								return "json";
							}
						}
						if ("update".equals(operation)) {
							updateBo();
						} else {
							updateBo(dataSource);
							setUpdateContext(true);
							getLstForward();
						}
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if (dataSource != null) {
						setPoolNewOrApplyButton();
						setCustomNewOrApplyButton();
						setDhcpServerType();
						if (!updateDhcpOptionValue()) {
							setTabId(0);
							return INPUT;
						}
					}
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				setTabId(0);
				long cloneId = getSelectedIds().get(0);
				VlanDhcpServer profile = (VlanDhcpServer) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setProfileName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setCloneValue(profile, profile);
				setSessionDataSource(profile);
				addLstTitle(getText("config.network.object.dhcp.server.title"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					return INPUT;
				}
			} else if ("addIPAddress".equals(operation)) {
				setTabId(0);
				setCustomNewOrApplyButton();
				setDhcpServerType();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					if (!addSingleIpPool()) {
						hidePoolCreateItem = "";
						hidePoolNewButton = "none";
					}
					return getReturnPathWithJsonMode(INPUT, "vlanDhcpServerJson");
				}
			} else if ("removeIPAddress".equals(operation)
					|| "removeIPAddressNone".equals(operation)) {
				setTabId(0);
				hidePoolCreateItem = "removeIPAddressNone".equals(operation) ? ""
						: "none";
				hidePoolNewButton = "removeIPAddressNone".equals(operation) ? "none"
						: "";
				setCustomNewOrApplyButton();
				setDhcpServerType();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					removeSelectedIpPool();
					return getReturnPathWithJsonMode(INPUT, "vlanDhcpServerJson");
				}
			} else if ("addCustom".equals(operation)) {
				setTabId(1);
				setPoolNewOrApplyButton();
				setDhcpServerType();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					if (!addSingleCustom()) {
						hideCustomCreateItem = "";
						hideCustomNewButton = "none";
					}
					return getReturnPathWithJsonMode(INPUT, "vlanDhcpServerJson");
				}
			} else if ("removeCustom".equals(operation)
					|| "removeCustomNone".equals(operation)) {
				hideCustomCreateItem = "removeCustomNone".equals(operation) ? ""
						: "none";
				hideCustomNewButton = "removeCustomNone".equals(operation) ? "none"
						: "";
				setTabId(1);
				setPoolNewOrApplyButton();
				setDhcpServerType();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					removeSelectedCustom();
					return getReturnPathWithJsonMode(INPUT, "vlanDhcpServerJson");
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

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_VLAN_DHCP_SERVER);
		setDataSource(VlanDhcpServer.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_DHCP;
	}

	@Override
	public VlanDhcpServer getDataSource() {
		return (VlanDhcpServer) dataSource;
	}
	
	private void setCloneValue(VlanDhcpServer source, VlanDhcpServer dest) {
		List<DhcpServerIpPool> ipPools = new ArrayList<DhcpServerIpPool>();
		ipPools.addAll(source.getIpPools());
		dest.setIpPools(ipPools);
		List<DhcpServerOptionsCustom> customs = new ArrayList<DhcpServerOptionsCustom>();
		customs.addAll(source.getCustoms());
		dest.setCustoms(customs);
	}

	public int getNameLength() {
		return getAttributeLength("profileName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getProfileName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public EnumItem[] getEnumDhcpInterfaceMgt() {
		return VlanDhcpServer.ENUM_INTERFACE_FOR_OPTION;
	}

	public EnumItem[] getEnumCustomType() {
		return DhcpServerOptionsCustom.ENUM_CUSTOM_TYYPE;
	}

	public boolean advanceShowing = true;

	public boolean isAdvanceShowing() {
		advanceShowing = VlanDhcpServer.ENABLE_DHCP_SERVER == getDataSource()
				.getTypeFlag();
		return advanceShowing;
	}

	public void setAdvanceShowing(boolean advanceShowing) {
		this.advanceShowing = advanceShowing;
	}

	public String getDisplayDhcpOption() {
		return isAdvanceShowing() ? "" : "none";
	}

	public String getDisplayDhcpRelay() {
		return isAdvanceShowing() ? "none" : "";
	}

	public String getDisplayInterfaceIp() {
		return getDataSource().getDhcpMgt() == 0 ? "none" : "";
	}
	
	public String getDisableDhcpNetmask() {
		return getDataSource().getDhcpMgt() == 0 ? "true" : "false";
	}

	private void setPoolNewOrApplyButton() {
		if (null != getDataSource().getIpPools()
				&& getDataSource().getIpPools().size() > 0) {
			hidePoolCreateItem = "none";
			hidePoolNewButton = "";
		} else {
			hidePoolCreateItem = "";
			hidePoolNewButton = "none";
		}
	}

	private void setCustomNewOrApplyButton() {
		if (null != getDataSource().getCustoms()
				&& getDataSource().getCustoms().size() > 0) {
			hideCustomCreateItem = "none";
			hideCustomNewButton = "";
		} else {
			hideCustomCreateItem = "";
			hideCustomNewButton = "none";
		}
	}

	private void setDhcpServerType() throws Exception {
		if (!getDisableDhcpInterface()) {
			getDataSource().setTypeFlag(
					"option".equals(dhcpType) ? VlanDhcpServer.ENABLE_DHCP_SERVER
							: VlanDhcpServer.ENABLE_DHCP_RELAY);
		}
	}

	public List<CheckItem> getAvailableIpAddress() {
		return getIpObjectsByIpAndName();
	}

	public String dhcpType = "option";

	public String getDhcpType() {
		dhcpType = VlanDhcpServer.ENABLE_DHCP_SERVER == getDataSource()
				.getTypeFlag() ? "option" : "relay";
		return dhcpType;
	}

	public void setDhcpType(String dhcpType) {
		this.dhcpType = dhcpType;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		VlanDhcpServer source = QueryUtil.findBoById(VlanDhcpServer.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<VlanDhcpServer> list = QueryUtil.executeQuery(VlanDhcpServer.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (VlanDhcpServer profile : list) {		
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			VlanDhcpServer up = source.clone();
			if (null == up) {
				continue;
			}
			
			// type must be same
			if (profile.getTypeFlag() != source.getTypeFlag()) {
				addActionError(MgrUtil
					.getUserMessage("error.use.paintbrush.objectIsDifferentType", "type"));
				return null;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setProfileName(profile.getProfileName());
			up.setOwner(profile.getOwner());
			setCloneValue(source, up);
			hmBos.add(up);
		}
		return hmBos;
	}

	private boolean updateDhcpOptionValue() throws Exception {
		if (getDataSource().getDhcpMgt() == 0) {
			getDataSource().setInterfaceIp("");
			getDataSource().setInterfaceNet("");
			getDataSource().setInterVlan(1);
			getDataSource().setEnablePing(true);
		}
		if (VlanDhcpServer.ENABLE_DHCP_SERVER == getDataSource().getTypeFlag()) {
			if (getDataSource().getDhcpMgt() > 0) {
				long oneStart;
				long oneEnd;
				long interfaceIpInt = AhEncoder.ip2Long(getDataSource()
						.getInterfaceIp());
				long interfaceIpNet = AhEncoder.ip2Long(getDataSource()
						.getInterfaceNet());
				/*
				 * default gateway and interface ip must be in the same subnet
				 */
				if (!"".equals(getDataSource().getDefaultGateway())) {
					if (!HmBeOsUtil.isInSameSubnet(getDataSource().getDefaultGateway(),
						getDataSource().getInterfaceIp(),
						getDataSource().getInterfaceNet())) {
						addActionError(MgrUtil.getUserMessage("error.config.network.dhcp.ip.pool.network",
							new String[] { getText("admin.interface.defaultGateway"), "the Interface IP" }));
						return false;
					}
				}
				/*
				 * DHCP netmask must be smaller than the interface netmask
				 */
				if (!"".equals(getDataSource().getDhcpNetmask())) {
					if (AhEncoder.ip2Long(getDataSource().getDhcpNetmask()) < interfaceIpNet) {
						addActionError(MgrUtil
								.getUserMessage("error.config.network.dhcp.netmask"));
						return false;
					}
				}
				for (DhcpServerIpPool onePool : getDataSource().getIpPools()) {
					oneStart = AhEncoder.ip2Long(onePool.getStartIp());
					oneEnd = AhEncoder.ip2Long(onePool.getEndIp());
					/*
					 * ip pool and interface ip must be in the same subnet
					 */
					if (!HmBeOsUtil.isInSameSubnet(onePool.getStartIp(),
							 getDataSource()
									.getInterfaceIp(), getDataSource()
									.getInterfaceNet())
							|| !HmBeOsUtil.isInSameSubnet(onePool
									.getEndIp(), getDataSource()
									.getInterfaceIp(), getDataSource()
									.getInterfaceNet())) {
						addActionError(MgrUtil.getUserMessage(
								"error.config.network.dhcp.ip.pool.network",
								new String[] { "The IP pool",
										"the Interface IP" }));
						return false;
					}
					/*
					 * ip pool cannot contain interface ip
					 */
					if (oneStart <= interfaceIpInt && interfaceIpInt <= oneEnd) {
						addActionError(MgrUtil
								.getUserMessage("error.config.network.dhcp.ip.pool.contain.interface"));
						return false;
					}
				}
				for (DhcpServerOptionsCustom oneCus : getDataSource()
						.getCustoms()) {
					/*
					 * HiveManager ip cannot be as the same as the interface ip
					 */
					if (oneCus.getNumber() == 226 && getDataSource().getInterfaceIp().equals(oneCus.getValue())) {
						addActionError(MgrUtil.getUserMessage("error.config.network.dhcp.interface.ip.address",
								getText("config.network.object.dhcp.server.hivemanager.ip")));
						return false;
					}
				}
			}
			getDataSource().setIpHelper1("");
			getDataSource().setIpHelper2("");
		} else {
			getDataSource().setIpPools(null);
			getDataSource().setCustoms(null);
			getDataSource().setAuthoritative(true);
			getDataSource().setEnableArp(true);
			getDataSource().setDefaultGateway("");
			getDataSource().setDnsServer1("");
			getDataSource().setDnsServer2("");
			getDataSource().setDnsServer3("");
			getDataSource().setDomainName("");
			getDataSource().setLeaseTime("86400");
			getDataSource().setDhcpNetmask("");
			getDataSource().setPop3("");
			getDataSource().setSmtp("");
			getDataSource().setWins1("");
			getDataSource().setWins2("");
			getDataSource().setNtpServer1("");
			getDataSource().setNtpServer2("");
			getDataSource().setLogsrv("");
			getDataSource().setMtu("");
			getDataSource().setNatSupport(false);
		}
		return true;
	}

	private boolean addSingleIpPool() throws Exception {
		long startInt = AhEncoder.ip2Long(startIp);
		long endInt = AhEncoder.ip2Long(endIp);
		if (startInt <= endInt) {
			long totalNumber = 0;
			long oneStart;
			long oneEnd;
			//long number;
			if (getDataSource().getDhcpMgt() > 0
					&& !"".equals(getDataSource().getInterfaceIp())) {
				/*
				 * the ip pool must have the same subnet
				 */
				if (!HmBeOsUtil.isInSameSubnet(startIp, getDataSource().getInterfaceIp(),
						getDataSource().getInterfaceNet())
						|| !HmBeOsUtil.isInSameSubnet(endIp,
								getDataSource().getInterfaceIp(),
								getDataSource().getInterfaceNet())) {
					addActionError(MgrUtil.getUserMessage(
							"error.config.network.dhcp.ip.pool.network",
							new String[] { "Add failed, the IP pool",
									"the interface IP" }));
					return false;
				}
				/*
				 * ip pool cannot contain interface ip
				 */
				long interfaceIpInt = AhEncoder.ip2Long(getDataSource()
						.getInterfaceIp());
				if (startInt <= interfaceIpInt && interfaceIpInt <= endInt) {
					addActionError(MgrUtil
							.getUserMessage("error.config.network.dhcp.ip.pool.contain.interface"));
					return false;
				}
			}
			// the ip must has the same subnet as the interface's
			for (DhcpServerIpPool onePool : getDataSource().getIpPools()) {
				oneStart = AhEncoder.ip2Long(onePool.getStartIp());
				oneEnd = AhEncoder.ip2Long(onePool.getEndIp());
				if (endInt < oneStart || startInt > oneEnd) {
					//number = oneEnd - oneStart ;
					totalNumber += (oneEnd - oneStart == 0 ? 1 : oneEnd - oneStart + 1);
				} else {
					addActionError(MgrUtil
							.getUserMessage("error.config.network.dhcp.ip.pool.overlap"));
					return false;
				}
			}
			/*
			 * the ip number in ip pool cannot more than 512
			 */
			totalNumber += (endInt - startInt == 0 ? 1 : endInt - startInt + 1);
			if (totalNumber > 512) {
				addActionError(MgrUtil
						.getUserMessage("error.config.network.dhcp.ip.pool.count"));
				return false;
			} else {
				DhcpServerIpPool oneItem = new DhcpServerIpPool();
				oneItem.setStartIp(startIp);
				oneItem.setEndIp(endIp);
				getDataSource().getIpPools().add(oneItem);
				startIp = "";
				endIp = "";
				return true;
			}
		} else {
			addActionError(MgrUtil.getUserMessage("error.notLargerThan",
					new String[] {
							getText("config.tunnelSetting.startIpAddress"),
							getText("config.tunnelSetting.endIpAddress") }));
			return false;
		}
	}

	private void removeSelectedIpPool() {
		if (poolIndices != null) {
			Collection<DhcpServerIpPool> removeList = new Vector<DhcpServerIpPool>();
			for (String serviceIndex : poolIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getIpPools().size()) {
						removeList.add(getDataSource().getIpPools().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getIpPools().removeAll(removeList);
		}
	}

	private boolean addSingleCustom() throws Exception {
		int totalCount = getDataSource().getCustoms().size();
		for (DhcpServerOptionsCustom custom : getDataSource().getCustoms()) {
			// the custom number cannot repeat
			if (custom.getNumber() == customNumber) {
				addActionError(MgrUtil
						.getUserMessage("error.addObjectExists"));
				return false;
			}
			if (custom.getNumber() == 225 || custom.getNumber() == 226) {
				totalCount--;
			}
		}
		// the max number of custom option is 8 except 225 and 226
		if (customNumber != 225 && customNumber != 226 && totalCount >= 8) {
			addActionError(MgrUtil
					.getUserMessage("error.entryLimit", new String[] {getText("config.network.object.dhcp.server.options.custom"), "8"})
					+ "(except 225 and 226)");
			return false;
		}
		DhcpServerOptionsCustom oneItem = new DhcpServerOptionsCustom();
		oneItem.setNumber(customNumber);
		oneItem.setType(customType);
		switch (customType) {
		case DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER:
			oneItem.setValue(integerValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_IP:
			oneItem.setValue(ipValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING:
			oneItem.setValue(strValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX:
			oneItem.setValue(hexValue);
			break;
		default:
			break;
		}
		getDataSource().getCustoms().add(oneItem);
		customNumber = 0;
		customType = DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
		integerValue = "";
		ipValue = "";
		strValue = "";
		hexValue = "";
		return true;
	}

	private void removeSelectedCustom() {
		if (customIndices != null) {
			Collection<DhcpServerOptionsCustom> removeList = new Vector<DhcpServerOptionsCustom>();
			for (String serviceIndex : customIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getCustoms().size()) {
						removeList.add(getDataSource().getCustoms().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getCustoms().removeAll(removeList);
		}
	}

	/*
	 * For IP Pools
	 */
	public int getPoolGridCount() {
		if (null == getDataSource().getIpPools()) {
			return 3;
		}
		return getDataSource().getIpPools().size() == 0 ? 3 : 0;
	}

	private String hidePoolCreateItem = "";

	private String hidePoolNewButton = "none";

	private String startIp;

	private String endIp;

	private Collection<String> poolIndices;

	public String getStartIp() {
		return startIp;
	}

	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}

	public String getEndIp() {
		return endIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	public void setPoolIndices(Collection<String> poolIndices) {
		this.poolIndices = poolIndices;
	}

	public String getHidePoolCreateItem() {
		return hidePoolCreateItem;
	}

	public void setHidePoolCreateItem(String hidePoolCreateItem) {
		this.hidePoolCreateItem = hidePoolCreateItem;
	}

	public String getHidePoolNewButton() {
		return hidePoolNewButton;
	}

	public void setHidePoolNewButton(String hidePoolNewButton) {
		this.hidePoolNewButton = hidePoolNewButton;
	}

	/*
	 * For Custom Options
	 */

	public int getCustomGridCount() {
		if (null == getDataSource().getCustoms()) {
			return 3;
		}
		return getDataSource().getCustoms().size() == 0 ? 3 : 0;
	}

	public String getDisplayCustomInt() {
		return DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER == customType ? "" : "none";
	}

	public String getDisplayCustomIp() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_IP == customType ? "" : "none";
	}

	public String getDisplayCustomStr() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING == customType ? "" : "none";
	}

	public String getDisplayCustomHex() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX == customType ? "" : "none";
	}

	private String hideCustomCreateItem = "";

	private String hideCustomNewButton = "none";

	private short customNumber;

	private short customType = DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;

	private String integerValue;

	private String ipValue;

	private String strValue;

	private String hexValue;

	private Collection<String> customIndices;

	public String getHideCustomCreateItem() {
		return hideCustomCreateItem;
	}

	public void setHideCustomCreateItem(String hideCustomCreateItem) {
		this.hideCustomCreateItem = hideCustomCreateItem;
	}

	public String getHideCustomNewButton() {
		return hideCustomNewButton;
	}

	public void setHideCustomNewButton(String hideCustomNewButton) {
		this.hideCustomNewButton = hideCustomNewButton;
	}

	public short getCustomNumber() {
		return customNumber;
	}

	public String getHexValue()
	{
		return hexValue;
	}

	public void setHexValue(String hexValue)
	{
		this.hexValue = hexValue;
	}

	public void setCustomNumber(short customNumber) {
		this.customNumber = customNumber;
	}

	public short getCustomType() {
		return customType;
	}

	public void setCustomType(short customType) {
		this.customType = customType;
	}

	public String getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(String integerValue) {
		this.integerValue = integerValue;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public void setCustomIndices(Collection<String> customIndices) {
		this.customIndices = customIndices;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof VlanDhcpServer) {
			VlanDhcpServer dhcp = (VlanDhcpServer) bo;
			if (dhcp.getIpPools() != null)
				dhcp.getIpPools().size();
			if (dhcp.getCustoms() != null)
				dhcp.getCustoms().size();
		}
		return null;
	}

	public boolean getDisableDhcpInterface() {
		if (getDataSource().getId() == null) {
			return false;
		} else {
			List<?> boIds = QueryUtil
					.executeNativeQuery("SELECT hive_ap_id FROM hive_ap_dhcp_server WHERE dhcpservers_id = "
							+ getDataSource().getId());
			return !boIds.isEmpty();
		}
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_INTERFACE = 2;
	
	public static final int COLUMN_IP = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;
	
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
		case COLUMN_INTERFACE:
			code = "config.network.object.dhcp.server.inter.dhcp";
			break;
		case COLUMN_IP:
			code = "config.network.object.dhcp.server.inter.ip.title";
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
		columns.add(new HmTableColumn(COLUMN_INTERFACE));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	private static class QueryLazyBo implements QueryBo {
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof VlanDhcpServer) {
				VlanDhcpServer dhcp = (VlanDhcpServer) bo;
				if (dhcp.getIpPools() != null)
					dhcp.getIpPools().size();
				if (dhcp.getCustoms() != null)
					dhcp.getCustoms().size();
			}
			return null;
		}
	}

	/**
	 * Check the ip and netmask of interface mgt0 if valid for the configuration
	 * in this DHCP server
	 *
	 * @param str_Ip mgt0 ip
	 * @param str_Net mgt0 netmask
	 * @param str_Obj this DHCP server
	 * @return the error message, "" is no error
	 */
	public static String checkInterfaceNetwork(String str_Ip, String str_Net,
			VlanDhcpServer str_Obj) {
		str_Obj = QueryUtil.findBoById(VlanDhcpServer.class,
				str_Obj.getId(), new QueryLazyBo());
		String errorMessage = "";
		if (null != str_Ip && !"".equals(str_Ip) && null != str_Net
				&& !"".equals(str_Net) && null != str_Obj) {
			if (str_Obj.getDhcpMgt() == 0) {
				if (VlanDhcpServer.ENABLE_DHCP_SERVER == str_Obj.getTypeFlag()) {
					if (str_Ip.equals(str_Obj.getDefaultGateway())) {
						errorMessage = MgrUtil
								.getUserMessage("admin.interface.defaultGateway");
					} else if (!"".equals(str_Obj.getDefaultGateway()) && !HmBeOsUtil.isInSameSubnet(str_Obj.getDefaultGateway(),
						str_Ip, str_Net)) {
						return "Check this DHCP Server & Relay ("
						+ str_Obj.getProfileName()
						+ ") failed! ("
						+ MgrUtil
								.getUserMessage(
										"error.config.network.dhcp.ip.pool.network",
										new String[] {
											MgrUtil.getUserMessage("admin.interface.defaultGateway"),
												"the mgt0 Interface IP" })
						+ ")";
					} else if (str_Ip.equals(str_Obj.getDnsServer1())) {
						errorMessage = MgrUtil
								.getUserMessage("config.network.object.dhcp.server.dns1");
					} else if (!"".equals(str_Obj.getDhcpNetmask())
							&& AhEncoder.ip2Long(str_Obj.getDhcpNetmask()) < AhEncoder
									.ip2Long(str_Net)) {
						return "Check this DHCP Server & Relay ("
								+ str_Obj.getProfileName()
								+ ") failed! ("
								+ MgrUtil
										.getUserMessage("error.config.network.dhcp.netmask")
								+ ")";
					} else {
						if (null != str_Obj.getIpPools()) {
							long oneStart;
							long oneEnd;
							long interfaceIpInt = AhEncoder.ip2Long(str_Ip);
							for (DhcpServerIpPool onePool : str_Obj
									.getIpPools()) {
								oneStart = AhEncoder.ip2Long(onePool
										.getStartIp());
								oneEnd = AhEncoder.ip2Long(onePool.getEndIp());
								if (!HmBeOsUtil
										.isInSameSubnet(onePool.getStartIp(),
												str_Ip, str_Net)
										|| !HmBeOsUtil.isInSameSubnet(
												onePool.getEndIp(), 
												str_Ip, str_Net)) {
									return "Check this DHCP Server & Relay ("
											+ str_Obj.getProfileName()
											+ ") failed! ("
											+ MgrUtil
													.getUserMessage(
															"error.config.network.dhcp.ip.pool.network",
															new String[] {
																	"The IP pool",
																	"the mgt0 Interface IP" })
											+ ")";
								}
								if (oneStart <= interfaceIpInt
										&& interfaceIpInt <= oneEnd) {
									return "Check this DHCP Server & Relay ("
											+ str_Obj.getProfileName()
											+ ") failed! ("
											+ MgrUtil
													.getUserMessage("error.config.network.dhcp.ip.pool.contain.interface")
											+ ")";
								}
							}
						}
						if (null != str_Obj.getCustoms()) {
							for (DhcpServerOptionsCustom oneCus : str_Obj
									.getCustoms()) {
								if (oneCus.getNumber() == 226 && str_Ip.equals(oneCus.getValue())) {
									return "Check this DHCP Server & Relay ("
											+ str_Obj.getProfileName()
											+ ") failed! ("
											+ MgrUtil.getUserMessage("error.config.network.dhcp.interface.ip.address",
													MgrUtil.getUserMessage("config.network.object.dhcp.server.hivemanager.ip"))
											+ ")";
								}
							}
						}
						if (str_Ip.equals(str_Obj.getDnsServer2())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.dns2");
						} else if (str_Ip.equals(str_Obj.getDnsServer3())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.dns3");
						} else if (str_Ip.equals(str_Obj.getNtpServer1())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.ntp1");
						} else if (str_Ip.equals(str_Obj.getNtpServer2())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.ntp2");
						} else if (str_Ip.equals(str_Obj.getPop3())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.pop3");
						} else if (str_Ip.equals(str_Obj.getSmtp())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.smtp");
						} else if (str_Ip.equals(str_Obj.getLogsrv())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.logsrv");
						} else if (str_Ip.equals(str_Obj.getWins1())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.wins1");
						} else if (str_Ip.equals(str_Obj.getWins2())) {
							errorMessage = MgrUtil
									.getUserMessage("config.network.object.dhcp.server.wins2");
						}
					}
				} else {
					if (str_Ip.equals(str_Obj.getIpHelper1())) {
						errorMessage = MgrUtil
								.getUserMessage("config.network.object.dhcp.server.ip.helper1");
					} else if (str_Ip.equals(str_Obj.getIpHelper2())) {
						errorMessage = MgrUtil
								.getUserMessage("config.network.object.dhcp.server.ip.helper2");
					}
				}
				if (!"".equals(errorMessage)) {
					errorMessage = "Check this DHCP Server & Relay ("
							+ str_Obj.getProfileName()
							+ ") failed! ("
							+ MgrUtil
									.getUserMessage(
											"error.config.network.dhcp.interface.ip.address",
											errorMessage)
							+ ")";
				}
			}
		} else {
			errorMessage = "The parameter is invalid.";
		}
		return errorMessage;
	}

}