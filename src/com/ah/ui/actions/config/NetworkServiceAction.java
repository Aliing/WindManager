package com.ah.ui.actions.config;

/*
 * @author Frazer
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.NetworkService;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class NetworkServiceAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private static final String NETWORK_SERVICE_OBJECT_LOCATION_FLAG = "NETWORK_SERVICE_OBJECT_LOCATION_FLAG";
	

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if (null == operation || L2_FEATURE_NETWORK_SERVICE.equals(operation) || "new".equals(operation)
				 || "edit".equals(operation)) {
				MgrUtil.setSessionAttribute(NETWORK_SERVICE_OBJECT_LOCATION_FLAG, isJsonMode());
			} else {
				setJsonMode((Boolean)MgrUtil.getSessionAttribute(NETWORK_SERVICE_OBJECT_LOCATION_FLAG));
			}
			
			filterParams = new FilterParams("servicetype = :s1",new Object[]{NetworkService.SERVICE_TYPE_NETWORK});
			
			if ("new".equals(operation)) {
				if(!isOnloadSelectedService()){
					if (!setTitleAndCheckAccess(getText("config.title.networkService"))) {
						setUpdateContext(true);
						return getLstForward();
					}
				}
				setSessionDataSource(new NetworkService());
				if (getLstForward().equalsIgnoreCase("configTemplate") 
						|| getLstForward().equalsIgnoreCase("networkPolicy")){
					getDataSource().setAlgType(NetworkService.ALG_TYPE_HTTP);
				}
				getDataSource().setIdleTimeout(null);
				getDataSource().setPortNumber(null);
				getDataSource().setProtocolNumber(null);
				if(isOnloadSelectedService()){
					return "onLoadSelectedService";
				}else{
					return returnResultKeyWord(INPUT, "networkServiceJson");
				}
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if (checkServiceName(getDataSource().getServiceName())) {
					if (isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (getActionErrors().size()>0) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					}else if(isOnloadSelectedService()){
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (getActionErrors().size()>0) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					}else{
						return returnResultKeyWord(INPUT, "networkServiceJson");
					}
				}
				if(null != getDataSource()){
					getDataSource().setAppId(0);
					getDataSource().setServiceType(NetworkService.SERVICE_TYPE_NETWORK);
				}
				if (isJsonMode() && !isParentIframeOpenFlg()){
					updateServiceParams();
					id = createBo(dataSource);
					jsonObject = new JSONObject();
					if(isOnloadSelectedService()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("nId", id);
						jsonObject.put("nName", getDataSource().getServiceName());
						jsonObject.put("nDesc", getDataSource().getDescription());
						return "json";
					}else if(isTeacherViewEnabled() && NetworkService.ALG_TYPE_HTTP == getDataSource().getAlgType()){
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", id);
						jsonObject.put("nName", getDataSource().getServiceName());
					}else{
						jsonObject.put("t", true);
						jsonObject.put("n", false);
					}
					return "json";
					
				}else if(isOnloadSelectedService()){
					updateServiceParams();
					id = createBo(dataSource);
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					jsonObject.put("n", true);
					jsonObject.put("nId", id);
					jsonObject.put("nName", getDataSource().getServiceName());
					jsonObject.put("nDesc", getDataSource().getDescription());
					return "json";
				} else {
					updateServiceParams();
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				String returnValue = editBo();
				if(isOnloadSelectedService()){
					return "onLoadSelectedService";
				}else{
					if (dataSource != null) {
						addLstTitle(getText("config.title.networkService.edit")
								+ " '" + this.getChangedName() + "'");
					}
					return returnResultKeyWord(returnValue, "networkServiceJson");
				}
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if(null != getDataSource()){
					getDataSource().setAppId(0);
					getDataSource().setServiceType(NetworkService.SERVICE_TYPE_NETWORK);
				}
				if (dataSource != null) {
					updateServiceParams();
					//id = dataSource.getId();
				}
				if ("update".equals(operation)) {
					updateBo(dataSource);
					if(isOnloadSelectedService()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", false);
						jsonObject.put("nDesc", getDataSource().getDescription());
						return "json";
					}else if (isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", false);
						return "json";
					}else {
						return prepareBoList();
					}
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				NetworkService profile = (NetworkService) findBoById(boClass,
						cloneId);
				profile.setId(null);
				profile.setServiceName("");
				profile.setDefaultFlag(false);
				profile.setCliDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			if(isJsonMode()){
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				return returnResultKeyWord(INPUT, "networkServiceJson");
			}
			return prepareActionError(e);
		}
	}

	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_NETWORK_SERVICE);
		setDataSource(NetworkService.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_SERVICE;
	}

	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		NetworkService source = QueryUtil.findBoById(NetworkService.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<NetworkService> list = QueryUtil.executeQuery(
				NetworkService.class, null, new FilterParams("id",
						destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (NetworkService profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			NetworkService bo = source.clone();
			if (null == bo) {
				continue;
			}
			bo.setId(profile.getId());
			bo.setVersion(profile.getVersion());
			bo.setServiceName(profile.getServiceName());
			bo.setOwner(profile.getOwner());
			bo.setDefaultFlag(false);
			hmBos.add(bo);
		}
		return hmBos;
	}

	public NetworkService getDataSource() {
		return (NetworkService) dataSource;
	}

	public EnumItem[] getEnmuProtocolId() {
		return NetworkService.ENUM_PROTOCOL_ID;
	}

	public EnumItem[] getEnmuAlgType() {
		return NetworkService.ENUM_ALG_TYPE;
	}

	public Range getProtocolNumberRange() {
		return getAttributeRange("protocolNumber");
	}

	public Range getPortNumberRange() {
		return getAttributeRange("portNumber");
	}

	public Range getIdleTimeoutRange() {
		return getAttributeRange("idleTimeout");
	}

	/**
	 * Check service name if exists in database ignore case
	 * 
	 * @param name
	 *            service name
	 * @return if the name exists
	 */
	private boolean checkServiceName(String name) {
		if (MgrUtil.getUserMessage("config.ipPolicy.any")
				.equalsIgnoreCase(name)) {
			addActionError(MgrUtil.getUserMessage(
					"error.ipOrMacOrService.nameLimit", MgrUtil
							.getUserMessage("config.ipPolicy.any")));
			return true;
		}
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		name = name.replace("\\", "\\\\");
		List<NetworkService> idList = QueryUtil.findBosByCondition(NetworkService.class,
				"servicename", name, domainId);
		if (!idList.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists", name));
			return true;
		}
		return false;
	}

	public int getServiceNameLength() {
		return getAttributeLength("serviceName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	private void updateServiceParams() {
		if (null != getDataSource()) {
			int pid = getDataSource().getProtocolId();
			switch (pid) {
			case NetworkService.PROTOCOL_ID_TCP:
				getDataSource().setProtocolNumber(6);
				break;
			case NetworkService.PROTOCOL_ID_UDP:
				getDataSource().setProtocolNumber(17);
				break;
			case NetworkService.PROTOCOL_ID_SVP:
				getDataSource().setProtocolNumber(119);
				getDataSource().setPortNumber(0);
				break;
			case NetworkService.PROTOCOL_ID_CUSTOM:
				getDataSource().setPortNumber(0);
				break;
			}
		}
	}

	public String getProtocolNumberTRStatus() {
		if (null != getDataSource()) {
			int pid = getDataSource().getProtocolId();
			if (pid == NetworkService.PROTOCOL_ID_CUSTOM) {
				return "";
			} else {
				return "none";
			}
		}
		return "none";
	}

	public String getPortNumberTRStatus() {
		if (null != getDataSource()) {
			int pid = getDataSource().getProtocolId();
			if (pid == NetworkService.PROTOCOL_ID_CUSTOM
					|| pid == NetworkService.PROTOCOL_ID_SVP) {
				return "none";
			} else {
				return "";
			}
		}
		return "";
	}

	public String getProtocolStr() {
		String str_ptl = "";
		if (null != getDataSource()) {
			int pid = getDataSource().getProtocolId();
			switch (pid) {
			case NetworkService.PROTOCOL_ID_TCP:
				str_ptl = "TCP (6)";
				break;
			case NetworkService.PROTOCOL_ID_UDP:
				str_ptl = "UDP (17)";
				break;
			case NetworkService.PROTOCOL_ID_SVP:
				str_ptl = "SVP (119)";
				break;
			case NetworkService.PROTOCOL_ID_CUSTOM:
				str_ptl = "PROTOCOL (" + getDataSource().getProtocolNumber()
						+ ")";
				break;
			}
		}
		return str_ptl;
	}

	public String getChangedName() {
		return getDataSource().getServiceName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_PROTOCOL = 2;

	public static final int COLUMN_PORT = 3;

	public static final int COLUMN_TIMEOUT = 4;

	public static final int COLUMN_ALG = 5;

	public static final int COLUMN_DESCRIPTION = 6;

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
			code = "config.ns.name";
			break;
		case COLUMN_PROTOCOL:
			code = "config.ns.protocolNumber";
			break;
		case COLUMN_PORT:
			code = "config.ns.portNumber";
			break;
		case COLUMN_TIMEOUT:
			code = "config.ns.idleTimeout";
			break;
		case COLUMN_ALG:
			code = "config.ns.algTypename";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ns.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_PROTOCOL));
		columns.add(new HmTableColumn(COLUMN_PORT));
		columns.add(new HmTableColumn(COLUMN_TIMEOUT));
		columns.add(new HmTableColumn(COLUMN_ALG));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	private boolean onloadSelectedService = false;
	
	public boolean isOnloadSelectedService() {
		return onloadSelectedService;
	}

	public void setOnloadSelectedService(boolean onloadSelectedService) {
		this.onloadSelectedService = onloadSelectedService;
	}
}