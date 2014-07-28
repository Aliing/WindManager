/**
 *@filename		PPPoEAction.java
 *@version
 *@author		LiangWenPing
 *@createtime	2012-2-2 PM 02:06:01
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
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.PPPoE;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		LiangWenPing
 * @version		V1.0.0.0 
 */
public class PPPoEAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
  			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.pppoe.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new PPPoE());
				return isJsonMode() ? "pppoeDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("pppoeName", getDataSource().getPppoeName())) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;								
				}
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getPppoeName());
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
					addLstTitle(getText("config.pppoe.title.edit") + " '" + getChangedName() + "'");
				}
				return isJsonMode() ? "pppoeDlg" : strForward;
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
				PPPoE profile = (PPPoE) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setPppoeName("");
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.ethernet.access.title"));
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
					return isJsonMode() ? "pppoeDlg" : INPUT;
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public int getNameLength() {
		return getAttributeLength("pppoeName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getPppoeName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public EnumItem[] getEnumEncryptionMethod() {
		return PPPoE.ENUM_ENCRYPTION_METHOD;
	}
	
	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	/************************* Override Method ********************************/
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_PPPOE);
		setDataSource(PPPoE.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_PPPOE;
	}

	@Override
	public PPPoE getDataSource() {
		return (PPPoE) dataSource;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_USERNAME = 2;
	
	public static final int COLUMN_PASSWORD = 3;
	
	public static final int COLUMN_DOMAIN = 4;
	
	public static final int COLUMN_ENCRYPTIONMETHOD = 5;
	
	public static final int COLUMN_DESCRIPTION = 6;
	
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
			code = "config.pppoe.name";
			break;
		case COLUMN_USERNAME:
			code = "config.pppoe.username";
			break;
		case COLUMN_PASSWORD:
			code = "config.pppoe.password";
			break;
		case COLUMN_DOMAIN:
			code = "config.pppoe.domain";
			break;
		case COLUMN_ENCRYPTIONMETHOD:
			code = "config.pppoe.encryption.method";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipFilter.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_PASSWORD));
		columns.add(new HmTableColumn(COLUMN_DOMAIN));
		columns.add(new HmTableColumn(COLUMN_ENCRYPTIONMETHOD));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		PPPoE source = QueryUtil.findBoById(PPPoE.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<PPPoE> list = QueryUtil.executeQuery(PPPoE.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (PPPoE profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			PPPoE up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPppoeName(profile.getPppoeName());
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