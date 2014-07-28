/**
 *@filename		AlgConfigAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-6 PM 08:09:11
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *remove dns alg 2009-02-05
 *
 *add dns alg 2009-05-21
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class AlgConfigAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.alg.configuration.title"))) {
					return getLstForward();
				}
				setSessionDataSource(new AlgConfiguration());
				prepareAlgInfo();
				return isJsonMode() ? "algConfigurationDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateAlgInfo();
				if (checkNameExists("configName", getDataSource()
						.getConfigName())) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				if (isJsonMode()) {
					try {
						jsonObject = new JSONObject();
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("id", id);
						jsonObject.put("parentDomID",getParentDomID());
						jsonObject.put("name", getDataSource().getConfigName());
						jsonObject.put("resultStatus",true);
					}catch (Exception e) {
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
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareAlgInfo();
				}
				addLstTitle(getText("config.alg.configuration.title.edit") + " '" + getChangedAlgName() + "'");
				return isJsonMode() ? "algConfigurationDlg" : returnWord;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AlgConfiguration profile = (AlgConfiguration) findBoById(
						boClass, cloneId, this);
				profile.setId(null);
				profile.setConfigName("");
				profile.setVersion(null);
				profile.setItems(getCloneAlgItem(profile));
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				setSessionDataSource(profile);
				prepareAlgInfo();
				return INPUT;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateAlgInfo();
				}
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					updateBo(dataSource);
					setUpdateContext(true);
					jsonObject.put("resultStatus",true);
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
		setSelectedL2Feature(L2_FEATURE_ALG_CONFIGURATION);
		setDataSource(AlgConfiguration.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_ALG;
	}

	public AlgConfiguration getDataSource() {
		return (AlgConfiguration) dataSource;
	}
	
	private Map<String, AlgConfigurationInfo> getCloneAlgItem(AlgConfiguration profile) {
		Map<String, AlgConfigurationInfo> newitem = new LinkedHashMap<String, AlgConfigurationInfo>();
		for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType
				.values()) {
			AlgConfigurationInfo oneItem = profile.getAlgInfo(gatewayType);
			if (oneItem == null) {
				oneItem = new AlgConfigurationInfo();
			}
			oneItem.setGatewayType(gatewayType);
			newitem.put(oneItem.getkey(), oneItem);
		}
		return newitem;
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	protected void prepareAlgInfo() {
		AlgConfiguration algConfig = getDataSource();
		Map<String, AlgConfigurationInfo> items = new LinkedHashMap<String, AlgConfigurationInfo>();
		for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType
				.values()) {
			AlgConfigurationInfo oneItem = getDataSource().getAlgInfo(
					gatewayType);
			if (oneItem == null) {
				oneItem = new AlgConfigurationInfo();
				if(GatewayType.SIP.equals(gatewayType)) {
					oneItem.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
					oneItem.setTimeout(60);
					oneItem.setDuration(720);
				}
			}
			oneItem.setGatewayType(gatewayType);
			items.put(oneItem.getkey(), oneItem);
		}
		algConfig.setItems(items);
	}

	public EnumItem[] getEnumClass() {
		return EnumConstUtil.ENUM_QOS_CLASS;
	}

	public String getChangedAlgName() {
		return getDataSource().getConfigName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getAlgNameLength() {
		return getAttributeLength("configName");
	}
	
	public int getCommentLength() {
		return getAttributeLength("description");
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		AlgConfiguration source = QueryUtil.findBoById(AlgConfiguration.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<AlgConfiguration> list = QueryUtil.executeQuery(AlgConfiguration.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (AlgConfiguration profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			AlgConfiguration up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setConfigName(profile.getConfigName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			up.setItems(getCloneAlgItem(source));
			hmBos.add(up);
		}
		return hmBos;
	}

	protected void updateAlgInfo() {
		AlgConfiguration algConfig = getDataSource();
		int i = 0;
		for (AlgConfigurationInfo oneItem : algConfig.getItems().values()) {
			boolean enableRow = false;
			if (ifEnable != null) {
				for (int j = 0; j < ifEnable.length; j++) {
					if (!ifEnable[j].equals("false") && i == Integer.valueOf(ifEnable[j])) {
						enableRow = true;
						if (i != 4) {
							oneItem.setQosClass(qosClass[j]);
							if (i != 3) {
								oneItem.setTimeout(timeout[j]);
								oneItem.setDuration(duration[j]);	
							}
						}
						break;
					}
				}
			}
			if (!enableRow) {
				oneItem.setQosClass(i == 1 ? EnumConstUtil.QOS_CLASS_VOICE : EnumConstUtil.QOS_CLASS_BACKGROUND);
				oneItem.setTimeout(i == 1 ? 60 : 30);
				oneItem.setDuration(i == 1 ? 720 : 60);
			}
			oneItem.setIfEnable(enableRow);	
			i++;
		}
	}

	protected int[] timeout;

	protected int[] duration;

	protected short[] qosClass;
	
	protected String[] ifEnable;

	public String[] getIfEnable()
	{
		return ifEnable;
	}

	public void setIfEnable(String[] ifEnable)
	{
		this.ifEnable = ifEnable;
	}

	public int[] getTimeout() {
		return timeout;
	}

	public void setTimeout(int[] timeout) {
		this.timeout = timeout;
	}

	public int[] getDuration() {
		return duration;
	}

	public void setDuration(int[] duration) {
		this.duration = duration;
	}

	public short[] getQosClass() {
		return qosClass;
	}

	public void setQosClass(short[] qosClass) {
		this.qosClass = qosClass;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.alg.configuration.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.radiusAssign.description";
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
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AlgConfiguration) {
			dataSource = bo;
			if (getDataSource().getItems() != null)
				getDataSource().getItems().size();
		}
		return null;
	}
	
}