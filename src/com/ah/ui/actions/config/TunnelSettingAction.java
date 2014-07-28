package com.ah.ui.actions.config;

/*
 * @author Fisher
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Range;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.IpAddress;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.MgrUtil;

public class TunnelSettingAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TUNNLESETTING_TYPE = 2;
	
	public static final int COLUMN_DESCRIPTION = 3;

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.tunnelSetting.name";
			break;
		case COLUMN_TUNNLESETTING_TYPE:
			code = "config.tunnelSetting.enableType";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.tunnelSetting.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TUNNLESETTING_TYPE));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && "continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.tunnelSetting"))) {
					return getLstForward();
				}
				setSessionDataSource(new TunnelSetting());
				prepareDependentObjects();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "tunnelSettingJsonDlg");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				prepareSetSaveObjects();
				if (checkNameExists("tunnelName", getDataSource()
						.getTunnelName())) {
					prepareDependentObjects();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.objectExists",
								getDataSource().getTunnelName()));
						return "json";
					} else {
						return getReturnPathWithJsonMode(INPUT,"tunnelSettingJsonDlg");
					}
				}
				setInputDirectlyIP();
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getTunnelName());
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
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
					addLstTitle(getText("config.title.tunnelSetting.edit") + " '"
							+ getChangedTunnelName() + "'");
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(returnWord, "tunnelSettingJsonDlg");
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				TunnelSetting profile = (TunnelSetting) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setTunnelName("");
				profile.setVersion(null);
				Set<IpAddress> setIp = new HashSet<IpAddress>();
				setIp.addAll(profile.getIpAddressList());
				profile.setIpAddressList(setIp);
				profile.setOwner(null);
				setSessionDataSource(profile);
				prepareDependentObjects();
				addLstTitle(getText("config.title.tunnelSetting"));
				return INPUT;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSetSaveObjects();
					setInputDirectlyIP();
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
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
					prepareDependentObjects();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return this.getReturnPathWithJsonMode(INPUT, "tunnelSettingJsonDlg");
				}
			} else if ("newIpAddress".equals(operation)
					|| "editIpAddress".equals(operation)
					|| "newipAddress".equals(operation)
					|| "editipAddress".equals(operation)) {
				prepareSetSaveObjects();
				clearErrorsAndMessages();
				if ("newIpAddress".equals(operation)
					|| "editIpAddress".equals(operation)) {
					addLstForward("tunnel");
				} else {
					addLstForward("tunnelTwo");
				}
				return operation;
			} else if ("genenatePassword".equals(operation)){
				jsonObject = new JSONObject();
				jsonObject.put("v", MgrUtil.getRandomString(64,7));
				return "json";
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
			setSelectedL2Feature(L2_FEATURE_IDENTITY_BASED_TUNNELS);
		}
		setDataSource(TunnelSetting.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_TUNNEL_POLICY;
	}

	public void prepareDependentObjects() throws Exception {
		// Tunnel destination
		if (null != getIpId()) {
			for (CheckItem item : getAvailableIpAddress()) {
				if (item.getId().longValue() == getIpId().longValue()) {
					getDataSource().setIpInputValue(item.getValue());
					break;
				}
			}
		}
		prepareAvailableIpAddress();
	}

	public void prepareSetSaveObjects() throws Exception {
		if (getDataSource().getEnableType() == TunnelSetting.TUNNELSETTING_STATIC_TUNNELING) {
			getDataSource().setUnroamingAgeout(0);
			getDataSource().setUnroamingInterval(60);
			if (getDataSource().getTunnelToType() == TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS) {
				getDataSource().setIpRangeStart("");
				getDataSource().setIpRangeEnd("");
				if (ipId != null && ipId > -1) {
					IpAddress ipClass = findBoById(IpAddress.class,
							ipId);
					getDataSource().setIpAddress(ipClass);
				} 
			} else {
				getDataSource().setIpAddress(null);
			}
			setSelectedIpAddress();
		} else {
			getDataSource().setPassword("");
			getDataSource().setIpRangeStart("");
			getDataSource().setIpAddress(null);
			getDataSource().setIpRangeEnd("");
			getDataSource().getIpAddressList().clear();
		}
	}
	
	private void setInputDirectlyIP() {
		if (getDataSource().getEnableType() == TunnelSetting.TUNNELSETTING_STATIC_TUNNELING && 
				getDataSource().getTunnelToType() == TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS) {
			if (null == ipId || ipId == -1) {
				getDataSource().setIpAddress(CreateObjectAuto.createNewIP(getDataSource().getIpInputValue(), IpAddress.TYPE_IP_ADDRESS, 
					getDomain(), "For Tunnel Policy : " + getDataSource().getTunnelName()));
			}
		}
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		TunnelSetting source = QueryUtil.findBoById(TunnelSetting.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<TunnelSetting> list = QueryUtil.executeQuery(TunnelSetting.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (TunnelSetting profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			TunnelSetting up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setTunnelName(profile.getTunnelName());
			up.setOwner(profile.getOwner());
			Set<IpAddress> setIp = new HashSet<IpAddress>();
			setIp.addAll(source.getIpAddressList());
			up.setIpAddressList(setIp);
			hmBos.add(up);
		}
		return hmBos;
	}
	
//	private JSONObject jsonObject = null;
//	
//	public String getJSONString() {
//		return jsonObject.toString();
//	}
//	
	private Long ipId;
	
	private Long singleIpId;
	
	protected OptionsTransfer ipAddressOptions;
	
	protected List<Long> selectedIps;

	public OptionsTransfer getIpAddressOptions()
	{
		return ipAddressOptions;
	}

	public void setIpAddressOptions(OptionsTransfer ipAddressOptions)
	{
		this.ipAddressOptions = ipAddressOptions;
	}

	public List<Long> getSelectedIps()
	{
		return selectedIps;
	}

	public void setSelectedIps(List<Long> selectedIps)
	{
		this.selectedIps = selectedIps;
	}

	public Long getIpId() {
		if (null == ipId && getDataSource().getIpAddress() != null) {
			ipId = getDataSource().getIpAddress().getId();
		}
		return ipId;
	}

	public void setIpId(Long ipId) {
		this.ipId = ipId;
	}
	
	public String getIpSelectHigh() {
		String userAgent = request.getHeader("user-agent");
		if (null != userAgent) {
			// e.g Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN;
			// rv:1.8.1.17) Gecko/20080829 Firefox/2.0.0.17
			if (userAgent.contains("Mozilla")
					&& userAgent.contains("Gecko")
					&& userAgent.contains("rv:1.8")) {
				return 15+"px";
			}
		}
		return "13px";
	}

	public void prepareAvailableIpAddress() throws Exception {
		List<CheckItem> availableIps = getIpObjectsByNetwork();
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		if (null != getDataSource().getIpAddressList()) {
			for (CheckItem oneItem : availableIps) {
				for (IpAddress savedIpAddress : getDataSource().getIpAddressList()) {
					if (savedIpAddress.getAddressName().equals(oneItem.getValue())) {
						removeList.add(oneItem);
					}
				}
			}	
		}
		availableIps.removeAll(removeList);
		ipAddressOptions = new OptionsTransfer(MgrUtil.getUserMessage("config.ipFilter.avaliableIpAddress"), 
			MgrUtil.getUserMessage("config.ipFilter.selectedIpAddress"),
			availableIps, getDataSource().getIpAddressList(), "id", "value", "selectedIps", 32, "ipAddress", SIMPLE_OBJECT_IP, 
			IP_SUB_OBJECT_NETWORK, "multiIpCallBack", domainId);
	}

	public void setSelectedIpAddress() throws Exception {
		Set<IpAddress> setIp = new HashSet<IpAddress>();
		if (null != selectedIps) {
			for (Long ip_id : selectedIps) {
				IpAddress ip = findBoById(IpAddress.class, ip_id);
				if (ip != null) {
					setIp.add(ip);
				}
			}
		}
		getDataSource().setIpAddressList(setIp.size() > 0 ? setIp : null);
	}
	
	public List<CheckItem> getAvailableIpAddress() {
		return getIpObjectsBySingleIp(CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public TunnelSetting getDataSource() {
		return (TunnelSetting) dataSource;
	}

	public int getTunnelNameLength() {
		return getAttributeLength("tunnelName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	
	public Range getUnroamingIntervalRange() {
		return getAttributeRange("unroamingInterval");
	}

	public Range getUnroamingAgeoutRange() {
		return getAttributeRange("unroamingAgeout");
	}

	public String getHideFirst() {
		if (getDataSource().getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING
				|| getDataSource().getEnableType() == 0) {
			return "";
		}
		return "none";
	}

	public String getHideSecond() {
		if (getDataSource().getEnableType() == TunnelSetting.TUNNELSETTING_STATIC_TUNNELING) {
			return "";
		}
		return "none";
	}

	public boolean getDisabledIpAddress() {
		return getDataSource().getTunnelToType() != TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS;
	}

	public boolean getDisabledIpRange() {
		return getDataSource().getTunnelToType() != TunnelSetting.TUNNELSETTING_TUNNELTYPE_RANGEIP;
	}
	
	public String getDisabledButton() {
		return "".equals(getEnableButton()) ? "none" : "";
	}
	
	public String getEnableButton() {
		if (!getWriteDisabled().equalsIgnoreCase("disabled")) {
			if (getDataSource().getTunnelToType() == TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS) {
				return "";
			}
		}
		return "none";
	}

	public String getChangedTunnelName() {
		return getDataSource().getTunnelName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof TunnelSetting) {
			TunnelSetting tunnel = (TunnelSetting) bo;
			if (tunnel.getIpAddressList() != null)
				tunnel.getIpAddressList().size();
	}
		return null;
	}

	public Long getSingleIpId()
	{
		return singleIpId;
	}

	public void setSingleIpId(Long singleIpId)
	{
		this.singleIpId = singleIpId;
	}
	
	public String getgenenatePassword() {
		return "genenatePassword";	
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