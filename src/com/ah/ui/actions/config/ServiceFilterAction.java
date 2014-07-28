package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.ServiceFilter;
import com.ah.ui.actions.BaseAction;
import com.ah.bo.admin.HmTableColumn;
import com.ah.util.MgrUtil;

/*
 * @author Fisher
 */

public class ServiceFilterAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_ENABLE_SSH = 2;
	
	public static final int COLUMN_ENABLE_TELNET = 3;
	
	public static final int COLUMN_ENABLE_PING = 4;
	
	public static final int COLUMN_ENABLE_SNMP = 5;
	
	public static final int COLUMN_ENABLE_INTER = 6;
	
	public static final int COLUMN_DESCRIPTION = 7;

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
			code = "config.serviceFilter.name";
			break;
		case COLUMN_ENABLE_SSH:
			code = "config.serviceFilter.enableSSH";
			break;
		case COLUMN_ENABLE_TELNET:
			code = "config.serviceFilter.enableTelnet";
			break;
		case COLUMN_ENABLE_PING:
			code = "config.serviceFilter.enablePing";
			break;
		case COLUMN_ENABLE_SNMP:
			code = "config.serviceFilter.enableSNMP";
			break;
		case COLUMN_ENABLE_INTER:
			code = "config.serviceFilter.inter.station.traffic";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.serviceFilter.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SSH));
		columns.add(new HmTableColumn(COLUMN_ENABLE_TELNET));
		columns.add(new HmTableColumn(COLUMN_ENABLE_PING));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SNMP));
		columns.add(new HmTableColumn(COLUMN_ENABLE_INTER));	
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.trafficFilters"))) {
					return getLstForward();
				}
				setSessionDataSource(new ServiceFilter());
				return isJsonMode() ? "trafficFilterDlg" : INPUT ;

			} else if ("create".equals(operation)) {
				if (checkNameExists("filterName", getDataSource()
						.getFilterName())) {
					
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("ok",false);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.objectExists", getDataSource()
								.getFilterName().toString()));
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					Long id = createBo(dataSource);
					jsonObject.put("serviceFilterId",id);
					if (id == null) {
						jsonObject.put("ok",false);
						jsonObject.put("msg","id==null");
						return "json";
					} else {
						jsonObject.put("id", id);
						jsonObject.put("name", getDataSource().getFilterName());
						jsonObject.put("ok",true);
						return "json";
					}
				}
				return createBo();
			} else if (("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("filterName", getDataSource()
						.getFilterName())) {
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
				String strForward = editBo();
				if (dataSource != null) {
					addLstTitle(getText("config.title.trafficFilters.edit")
							+ " '" + getChangedName() + "'");
					setSelectedId(getDataSource().getId());
				}
				if (isJsonMode()) {
					return "trafficFilterDlg";
				}
				return strForward;
			} else if ("update".equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					updateBo(dataSource);
					jsonObject.put("id", getDataSource().getId());
					jsonObject.put("name", getDataSource().getFilterName());
					jsonObject.put("ok",true);
					return "json";
					
				}
				return updateBo();
			} else if (("update"+ getLstForward()).equals(operation)) {
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				ServiceFilter profile = (ServiceFilter) findBoById(boClass,
					cloneId);
				profile.setId(null);
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.trafficFilters"));
				return INPUT;
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
		if (isEasyMode()){
			setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
		} else {
			setSelectedL2Feature(L2_FEATURE_SERVICE_FILTERS);
		}

		setDataSource(ServiceFilter.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_SERVICE_FILTER;
	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		ServiceFilter source = QueryUtil.findBoById(ServiceFilter.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<ServiceFilter> list = QueryUtil.executeQuery(ServiceFilter.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (ServiceFilter profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			ServiceFilter up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setFilterName(profile.getFilterName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);

			hmBos.add(up);
		}
		return hmBos;
	}

	public ServiceFilter getDataSource() {
		return (ServiceFilter) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("filterName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedName() {

		return getDataSource().getFilterName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
}