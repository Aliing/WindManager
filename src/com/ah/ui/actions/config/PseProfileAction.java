/**
 *@filename		PseProfileAction.java
 *@version
 *@author		LiangWenPing
 *@createtime	2012-11-12 PM 02:06:01
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

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.PseProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		LiangWenPing
 * @version		V1.0.0.0 
 */
public class PseProfileAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
  			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.pse.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new PseProfile());
				prepareInitData();
				return isJsonMode() ? "pseProfileDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("name", getDataSource().getName())) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					prepareInitData();
					return isJsonMode() ? "json" : INPUT;								
				}
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getName());
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
					addLstTitle(getText("config.pse.title.edit") + " '" + getChangedName() + "'");
				}
				prepareInitData();
				return isJsonMode() ? "pseProfileDlg" : strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
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
				PseProfile profile = (PseProfile) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setName("");
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.pse.title"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return isJsonMode() ? "pseProfileDlg" : INPUT;
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public EnumItem[] getEnumPowerMode() {
		return PseProfile.ENUM_POWER_MODE;
	}
	
	public EnumItem[] getEnumPriority() {
		return PseProfile.ENUM_PRIORITY;
	}
	
	public int getNameLength() {
		return getAttributeLength("name");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private void prepareInitData(){
		if(null != getDataSource()){
			if(getDataSource().getPowerMode() == AhInterface.PSE_PDTYPE_8023AF){
				getDataSource().setThresholdPowerRange(MgrUtil.getResourceString("config.pse.threshold.power.af.range"));

			} else {
				getDataSource().setThresholdPowerRange(MgrUtil.getResourceString("config.pse.threshold.power.at.range"));

			}
		}
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public String getPowerLimitNote(){
		if(getDataSource() != null ){
			if(getDataSource().getPowerMode() == AhInterface.PSE_PDTYPE_8023AF){
				return MgrUtil.getUserMessage("gotham_21.config.pse.threshold.power.range.note",String.valueOf(PseProfile.THRESHOLD_POWER_AF));
			}
		}
		return "";
	}
	
	/************************* Override Method ********************************/
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_PSE_PROFILE);
		setDataSource(PseProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWOR_PSE_PROFILES;
	}

	@Override
	public PseProfile getDataSource() {
		return (PseProfile) dataSource;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_POWER_MODE = 2;
	
	public static final int COLUMN_THRESHOLD_POWER = 3;
	
	public static final int COLUMN_PRIORITY = 4;
	
	public static final int COLUMN_DESCRIPTION = 5;
	
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
			code = "config.pse.name";
			break;
		case COLUMN_POWER_MODE:
			code = "config.pse.powerMode";
			break;
		case COLUMN_THRESHOLD_POWER:
			code = "config.pse.threshold.power";
			break;
		case COLUMN_PRIORITY:
			code = "config.pse.priority";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.pse.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(5);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_POWER_MODE));
		columns.add(new HmTableColumn(COLUMN_THRESHOLD_POWER));
		columns.add(new HmTableColumn(COLUMN_PRIORITY));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		PseProfile source = QueryUtil.findBoById(PseProfile.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<PseProfile> list = QueryUtil.executeQuery(PseProfile.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (PseProfile profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			PseProfile up = source.clone();
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
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		return null;
	}
}