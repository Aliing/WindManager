/**
 *@filename		VlanGroup.java
 *@version
 *@author		Wenping
 *@createtime	2012-8-30 PM 07:16:52
 *Copyright (c) 2006-2012 Aerohive Co., Ltd.
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

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.VlanGroup;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

/**
 * @author Wenping
 * @version V1.0.0.0
 */
public class VlanGroupAction extends BaseAction implements QueryBo{

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
            if ("new".equals(operation)) {
				
				if (!setTitleAndCheckAccess(getText("config.title.vlanGroup"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new VlanGroup());

				return getReturnPathWithJsonMode(INPUT, "vlanGroupDlg");
			} else if ("create".equals(operation) 
			        || ("create" + getLstForward()).equals(operation)) {
				
				if (checkNameExists("vlanGroupName", getDataSource()
						.getVlanGroupName())) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource()
								.getVlanGroupName()));
					}
					
					return getReturnPathWithJsonMode(INPUT, "vlanGroupDlg");
				}
	
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
				
			} else if ("edit".equals(operation)) {
				
				String strForward = editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.title.vlanGroup")
							+ " '" + getChangedName() + "'");
				}

				return isJsonMode()? "vlanGroupDlg" : strForward;

			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);

				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
				
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				VlanGroup profile = (VlanGroup) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setVlanGroupName("");
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {			
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
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
		setSelectedL2Feature(L2_FEATURE_VLAN_GROUP);
		setDataSource(VlanGroup.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_VLAN_GROUP;
	}

	public VlanGroup getDataSource()
	{
		return (VlanGroup) dataSource;
	}

	public int getVlanGroupNameLength() {
		return getAttributeLength("vlanGroupName");
	}
	
	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedName() {
		return getDataSource().getVlanGroupName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		VlanGroup source = QueryUtil.findBoById(VlanGroup.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<VlanGroup> list = QueryUtil.executeQuery(VlanGroup.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (VlanGroup profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			VlanGroup up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setVlanGroupName(profile.getVlanGroupName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			hmBos.add(up);
		}
		return hmBos;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	public static final int COLUMN_VLANS_VALUE = 3;
	
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
			code = "config.vlanGroup.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipPolicy.description";
			break;
		case COLUMN_VLANS_VALUE:
			code = "config.vlanGroup.vlans";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_VLANS_VALUE));
		return columns;
	}
	

	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		return null;
	}
    
}