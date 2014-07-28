package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;

/*
 * @author Fisher
 */

public class IpFilterAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;

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
			code = "config.ipFilter.name";
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
				if (!setTitleAndCheckAccess(getText("config.title.ipFilter"))) {
					return getLstForward();
				}
				setSessionDataSource(new IpFilter());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("addressName", BeParaModule.DEFAULT_IP_ADDRESS_NAME);
				IpAddress ipClass = HmBeParaUtil.getDefaultProfile(IpAddress.class, map);
				
//				IpAddress ipClass = (IpAddress) QueryUtil
//				.executeQuery(IpAddress.class, null,
//						new FilterParams("defaultFlag", true)).get(0);
				getDataSource().getIpAddress().add(ipClass);
				prepareAvailableIpAddress();
				return getReturnPath(INPUT, "ipFilterJson");
			} else if ("create".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					if (!setSelectedIpAddress()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.ipFilter.needDefaultIpAddress"));
						return "json";
					}
					if(checkNameExists("filterName", getDataSource()
									.getFilterName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getFilterName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getFilterName());
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
				}else{
					if (!setSelectedIpAddress()
							|| checkNameExists("filterName", getDataSource()
									.getFilterName())) {
						prepareAvailableIpAddress();
						return INPUT;
					}
					
					return createBo();
				}
			} else if (("create" + getLstForward()).equals(operation)) {
				if (!setSelectedIpAddress()
						|| checkNameExists("filterName", getDataSource()
								.getFilterName())) {
					prepareAvailableIpAddress();
					return INPUT;
				}
				id = createBo(dataSource);
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
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					prepareAvailableIpAddress();
					setSelectedId(getDataSource().getId());
				}
				addLstTitle(getText("config.title.ipFilter.edit") + " '"
						+ getChangedName() + "'");
				return getReturnPath(strForward, "ipFilterJson");
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if (dataSource != null) {
							if (!setSelectedIpAddress()) {
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", MgrUtil.getUserMessage("error.ipFilter.needDefaultIpAddress"));
								return "json";
							}
						}
						updateBo(dataSource);
						setUpdateContext(true);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if (dataSource != null) {
						if (!setSelectedIpAddress()) {
							prepareAvailableIpAddress();
							return INPUT;
						}
					}
				}
				return updateBo();
			} else if (("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					if (!setSelectedIpAddress()) {
						prepareAvailableIpAddress();
						return INPUT;
					}
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				IpFilter profile = (IpFilter) findBoById(boClass, cloneId,this);
				profile.setId(null);
				profile.setFilterName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setCloneFields(profile, profile);
				setSessionDataSource(profile);
				prepareAvailableIpAddress();
				addLstTitle(getText("config.title.ipFilter.clone"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					prepareAvailableIpAddress();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return getReturnPath(INPUT, "ipFilterJson");
				}
			} else if ("newIpAddress".equals(operation)||
					   "editIpAddress".equals(operation)) {
				setSelectedIpAddress();
				clearErrorsAndMessages();
				addLstForward("ipFilter");
				return operation;
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
		setSelectedL2Feature(L2_FEATURE_IP_FILTERS);
		setDataSource(IpFilter.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_IP_FILTER;
	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		IpFilter source = QueryUtil.findBoById(IpFilter.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<IpFilter> list = QueryUtil.executeQuery(IpFilter.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (IpFilter profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			IpFilter up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setFilterName(profile.getFilterName());
			up.setOwner(profile.getOwner());
			setCloneFields(source,up);

			hmBos.add(up);
		}
		return hmBos;
	}

	public void setCloneFields(IpFilter source, IpFilter destination){
		Set<IpAddress> cloneIpAddress = new HashSet<IpAddress>();
		for (IpAddress tempClass : source.getIpAddress()) {
			cloneIpAddress.add(tempClass);
		}
		destination.setIpAddress(cloneIpAddress);
	}

	protected OptionsTransfer ipAddressOptions;

	public OptionsTransfer getIpAddressOptions() {
		return ipAddressOptions;
	}

	public void setIpAddressOptions(OptionsTransfer ipAddressOptions) {
		this.ipAddressOptions = ipAddressOptions;
	}

	public void prepareAvailableIpAddress() throws Exception {
		List<CheckItem> availableFilters = getIpObjectsByNetwork();
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (IpAddress savedip : getDataSource().getIpAddress()) {
				if (savedip.getAddressName()
						.equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);

		ipAddressOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ipFilter.avaliableIpAddress"), MgrUtil
				.getUserMessage("config.ipFilter.selectedIpAddress"),
				availableFilters, getDataSource().getIpAddress(), "id",
				"value", "selectIpAddress", 8, "IpAddress", SIMPLE_OBJECT_IP, 
				IP_SUB_OBJECT_NETWORK, "", domainId);
	}

	public boolean setSelectedIpAddress() throws Exception {
		if (selectIpAddress == null)
			return true;
		boolean selectDefaultIp = false;
		Set<IpAddress> set_ip = getDataSource().getIpAddress();
		set_ip.clear();
		for (Long ip_id : selectIpAddress) {
			IpAddress ipClass = findBoById(IpAddress.class, ip_id);
			if (ipClass != null) {
				set_ip.add(ipClass);
				if (ipClass.isDefaultFlag()) {
					selectDefaultIp = true;
				}
			}
		}
		if (set_ip.size() > 0)
			getDataSource().setIpAddress(set_ip);
		if (!selectDefaultIp) {
			addActionError(getText("error.ipFilter.needDefaultIpAddress"));
			return false;
		}
		return true;
	}

	public IpFilter getDataSource() {
		return (IpFilter) dataSource;
	}

	protected List<Long> selectIpAddress;
	
	protected Long ipAddressId;

	public Long getIpAddressId() {
		return ipAddressId;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}

	public void setSelectIpAddress(List<Long> selectIpAddress) {
		this.selectIpAddress = selectIpAddress;
	}

	public int getNameLength() {
		return getAttributeLength("filterName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getFilterName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof IpFilter) {
			dataSource = bo;
			if (getDataSource().getIpAddress()!=null) {
				getDataSource().getIpAddress().size();
				for (IpAddress ipAddress : getDataSource().getIpAddress()) {
                    if (ipAddress.getItems() != null)
                        ipAddress.getItems().size();
                }
			}
		}
		return null;
	}
	
	private String getReturnPath(String normalPath, String jsonModePath) {
		return isJsonMode() ? jsonModePath : normalPath;
	}

}