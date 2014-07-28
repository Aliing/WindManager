/**
 *@filename		LocationServerAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-12 PM 03:16:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.LocationServer;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class LocationServerAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private String aerohiveDisplay = "block";
	private String aeroscoutDisplay = "none";
	private String aeroscoutDisplay2 = "none";
	private String ekahauDisplay = "none";
	
//	private String ekahauIpDisplay= "block";
//	private String ekahauDomainDisplay= "none";

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					"continue".equals(operation)) { // user profile
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.location.server.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new LocationServer());
				initGUIValue();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "locationServerJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					setServerNameOrIP();
					if (checkNameExists("name", getDataSource().getName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getName()));
						return "json";
					}
					setInputDirectlyIP();
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getName());
					jsonObject.put("serviceType", getDataSource().getServiceType());
					try {
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					setServerNameOrIP();
					if (checkNameExists("name", getDataSource().getName())) {
						return INPUT;
					}
					setInputDirectlyIP();
					if ("create".equals(operation)) {
						return createBo();	
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo();
				if (dataSource != null) {	
					addLstTitle(getText("config.location.server.title.edit")
							+ " '" + getChangedName() + "'");
				}
				initGUIValue();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(strForward, "locationServerJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					try {
						if (dataSource != null) {
							setServerNameOrIP();
							setInputDirectlyIP();
						}
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("serviceType", getDataSource().getServiceType());
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if (dataSource != null) {
						setServerNameOrIP();
						setInputDirectlyIP();
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
				long cloneId = getSelectedIds().get(0);
				LocationServer profile = (LocationServer) findBoById(boClass,
						cloneId);
				profile.setId(null);
				profile.setName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.location.server.title"));
				initGUIValue();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)) {
				setServerNameOrIP();
				addLstForward("locationServer");
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
					setId(dataSource.getId());
					initGUIValue();
					return getReturnPathWithJsonMode(INPUT, "locationServerJson");
				}
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
		setSelectedL2Feature(L2_FEATURE_LOCATION_SERVER);
		setDataSource(LocationServer.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_LOCATION;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		LocationServer source = QueryUtil.findBoById(LocationServer.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<LocationServer> list = QueryUtil.executeQuery(LocationServer.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (LocationServer profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			LocationServer up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setName(profile.getName());
			up.setOwner(profile.getOwner());
			hmBos.add(up);
		}
		return hmBos;
	}

	private void setServerNameOrIP() throws Exception {
		if (null != ipAddressId && ipAddressId > -1) {
			IpAddress newIP = findBoById(IpAddress.class,
					ipAddressId);
			getDataSource().setServerIP(newIP);
		} else {
			getDataSource().setServerIP(null);
		}
		
		if (!getDataSource().isEnableTag()) {
			getDataSource().setTagThreshold(1000);
		}
		if (!getDataSource().isEnableStation()) {
			getDataSource().setStationThreshold(200);
		}
		if (!getDataSource().isEnableRogue()) {
			getDataSource().setRogueThreshold(50);
		}
		
		// aerohive
		if (getDataSource().getServiceType() == LocationServer.SERVICETYPE_AEROHIVE) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("addressName", BeParaModule.DEFAULT_IP_ADDRESS_NAME);
			IpAddress newIp = HmBeParaUtil.getDefaultProfile(IpAddress.class, map);
			getDataSource().setServerIP(newIp);
		} 
	}
	
	private void setInputDirectlyIP() {
		if ((null == ipAddressId || ipAddressId == -1) 
				&& (getDataSource().getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT
						|| getDataSource().getServiceType() == LocationServer.SERVICETYPE_EKAHAU)) {
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(getDataSource().getIpInputValue()) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			getDataSource().setServerIP(CreateObjectAuto.createNewIP(getDataSource().getIpInputValue(), ipType, getDomain(), "For Location Server : " + getDataSource().getName()));
		}
	}
	
	private void initGUIValue()
	{
		if (getDataSource() != null && getDataSource().getServiceType() == LocationServer.SERVICETYPE_AEROHIVE) {
			aerohiveDisplay = "block";
			aeroscoutDisplay = "none";
			aeroscoutDisplay2 = "none";
			ekahauDisplay = "none";
		} else if (getDataSource() != null && getDataSource().getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT)  {
			if (null != getIpAddressId()) {
				for (CheckItem item : getAvailableIpAddress()) {
					if (item.getId().longValue() == getIpAddressId().longValue()) {
						getDataSource().setIpInputValue(item.getValue());
						break;
					}
				}
			}
			aerohiveDisplay = "none";
			aeroscoutDisplay = "block";
			aeroscoutDisplay2 = "block";
			ekahauDisplay = "none";
		} else {
			if (null != getIpAddressId()) {
				for (CheckItem item : getAvailableIpAddress()) {
					if (item.getId().longValue() == getIpAddressId().longValue()) {
						getDataSource().setIpInputValue(item.getValue());
						break;
					}
				}
			}
			aerohiveDisplay = "none";
			aeroscoutDisplay = "block";
			aeroscoutDisplay2 = "none";
			ekahauDisplay = "block";
			
//			aerohiveDisplay = "none";
//			aeroscoutDisplay = "none";
//			ekahauDisplay = "block";
//			if (getDataSource().getEkahauServerType() == LocationServer.EKAHAU_SERVERTYPE_IP) {
//				ekahauIpDisplay= "block";
//				ekahauDomainDisplay= "none";
//			} else {
//				ekahauIpDisplay= "none";
//				ekahauDomainDisplay= "block";
//			}
		}
	}

	public LocationServer getDataSource() {
		return (LocationServer) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("name");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}
	
	public Range getThresholdRange() {
		return getAttributeRange("tagThreshold");
	}
	
	public Range getEkahauPortRange() {
		return getAttributeRange("ekahauPort");
	}

	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	private Long ipAddressId;

	public Long getIpAddressId() {
		if (null == ipAddressId) {
			if (null != getDataSource().getServerIP()) {
				ipAddressId = getDataSource().getServerIP().getId();
			}
		}
		return ipAddressId;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}

	public List<CheckItem> getAvailableIpAddress() {
		return getIpObjectsByIpAndName();
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_ENABLED = 2;
	
	public static final int COLUMN_SERVICETYPE = 3;
	
	public static final int COLUMN_SERVER = 4;
	
	public static final int COLUMN_DESCRIPTION = 5;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String -
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.ipFilter.name";
			break;
		case COLUMN_ENABLED:
			code = "config.location.server.enabled";
			break;
		case COLUMN_SERVICETYPE:
			code = "config.location.server.serviceType";
			break;
		case COLUMN_SERVER:
			code = "config.radiusAssign.serverName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipFilter.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ENABLED));
		columns.add(new HmTableColumn(COLUMN_SERVICETYPE));
		columns.add(new HmTableColumn(COLUMN_SERVER));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public String getAerohiveDisplay() {
		return aerohiveDisplay;
	}

	public void setAerohiveDisplay(String aerohiveDisplay) {
		this.aerohiveDisplay = aerohiveDisplay;
	}

	public String getAeroscoutDisplay() {
		return aeroscoutDisplay;
	}

	public void setAeroscoutDisplay(String aeroscoutDisplay) {
		this.aeroscoutDisplay = aeroscoutDisplay;
	}

	public String getEkahauDisplay() {
		return ekahauDisplay;
	}

	public String getAeroscoutDisplay2() {
		return aeroscoutDisplay2;
	}

	public void setAeroscoutDisplay2(String aeroscoutDisplay2) {
		this.aeroscoutDisplay2 = aeroscoutDisplay2;
	}

//	public String getEkahauIpDisplay() {
//		return ekahauIpDisplay;
//	}
//
//	public String getEkahauDomainDisplay() {
//		return ekahauDomainDisplay;
//	}
	
	public EnumItem[] getLocationType1() {
		return new EnumItem[] { new EnumItem(LocationServer.SERVICETYPE_AEROHIVE,
				MgrUtil.getEnumString("enum.config.location.server.type."
						+ LocationServer.SERVICETYPE_AEROHIVE)) };
	}

	public EnumItem[] getLocationType2() {
		return new EnumItem[] { new EnumItem(LocationServer.SERVICETYPE_AEROSCOUT,
				MgrUtil.getEnumString("enum.config.location.server.type."
						+ LocationServer.SERVICETYPE_AEROSCOUT)) };
	}
	
	public EnumItem[] getLocationType3() {
		return new EnumItem[] { new EnumItem(LocationServer.SERVICETYPE_EKAHAU,
				MgrUtil.getEnumString("enum.config.location.server.type."
						+ LocationServer.SERVICETYPE_EKAHAU)) };
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