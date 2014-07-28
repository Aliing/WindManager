/**
 *@filename		MgmtServiceIPTrackAction.java
 *@version
 *@author		Fiona
 *@createtime	2008-8-1 PM 04:36:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
public class MgmtServiceIPTrackAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	private boolean enableTrackIP = true;
	
	private boolean enableTrackWAN = true;
	
	public boolean getEnableTrackIP(){
		return enableTrackIP;
	}
	
	public void setEnableTrackIP(boolean enableTrackIP){
		this.enableTrackIP = enableTrackIP;
	}
	
	public boolean getEnableTrackWAN(){
		return enableTrackWAN;
	}
	
	public void setEnableTrackWAN(boolean enableTrackWAN){
		this.enableTrackWAN = enableTrackWAN;
	}
	
	public void setTrackGroup(boolean track){
		enableTrackIP = track;
		enableTrackWAN = track;
	}
	
	public void setTrackWAN(){
		enableTrackIP = false;
		enableTrackWAN = true;
	}
	
	public void setTrackIP(){
		enableTrackIP = true;
		enableTrackWAN = false;
	}

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.ip.track.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				MgmtServiceIPTrack o = new MgmtServiceIPTrack();
				if(getParentDomID().equals("ipTrackWANList_ID") || getParentDomID().equals("primaryIpTrackId") || getParentDomID().equals("backup1TrackId") || getParentDomID().equals("backup2TrackId")){
					setTrackWAN();
					o.setGroupType(1);
					o.setInterval(MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKWAN);
				}else if (getParentDomID().equals("leftOptions_ipTrackIds")){
					setTrackIP();
					o.setGroupType(0);
					o.setInterval(MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKIP);
				}else{
					setTrackGroup(true);
				}
				
				setSessionDataSource(o);
				return returnResultKeyWord(INPUT, "iptrackOnly");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("trackName", getDataSource().getTrackName())) {
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (getActionErrors().size()>0) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					} else {
						return returnResultKeyWord(INPUT, "iptrackOnly");
					}
				}
				if ("create".equals(operation)) {
					id=createBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", id);
						jsonObject.put("nName", getDataSource().getTrackName());
						jsonObject.put("gt", getDataSource().getGroupType());
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
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
				String strForward = editBo();
				if (dataSource != null) {
					addLstTitle(getText("config.ip.track.title.edit")
							+ " '" + getChangedName() + "'");
				}
				return returnResultKeyWord(strForward, "iptrackOnly");
			} else if ("update".equals(operation)) {
				updateBo(dataSource);
				if (isJsonMode()){
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					jsonObject.put("n", false);
					jsonObject.put("pId", getParentDomID());
					jsonObject.put("nId", id);
					jsonObject.put("nName", getDataSource().getTrackName());
					jsonObject.put("gt", getDataSource().getGroupType());
					return "json";
				} else {
					return prepareBoList();
				}
			} else if (("update" + getLstForward()).equals(operation)) {
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MgmtServiceIPTrack profile = (MgmtServiceIPTrack) findBoById(boClass,
						cloneId);
				profile.setId(null);
				profile.setTrackName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.ip.track.title"));
				return INPUT;
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

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MGMT_IP_TRACKING);
		setDataSource(MgmtServiceIPTrack.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_IP_TRACK;
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return expressKey;
		} else {
			return normalkey;
		}
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceIPTrack source = QueryUtil.findBoById(MgmtServiceIPTrack.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<MgmtServiceIPTrack> list = QueryUtil.executeQuery(MgmtServiceIPTrack.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (MgmtServiceIPTrack profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			MgmtServiceIPTrack up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setTrackName(profile.getTrackName());
			up.setOwner(profile.getOwner());
			hmBos.add(up);
		}
		return hmBos;
	}

	public MgmtServiceIPTrack getDataSource() {
		return (MgmtServiceIPTrack) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("trackName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public Range getIntervalRange() {
		return getAttributeRange("interval");
	}

	public Range getRetryRange() {
		return getAttributeRange("retryTime");
	}

	public EnumItem[] getEnumTrackLogic() {
		return MgmtServiceIPTrack.ENUM_IP_TRACK_LOGIC;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return HmDomain.GLOBAL_DOMAIN.equals(getDataSource().getOwner().getDomainName()) ? "disabled" : "";
		}
		return "disabled";
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_ENABLE = 2;
	
	public static final int COLUMN_IP = 3;
	
	public static final int COLUMN_GATEWAY = 4;
	
	public static final int COLUMN_DESCRIPTION = 5;
	
	public static final int COLUMN_TYPE = 6;
	
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
			code = "config.ip.track.name.title";
			break;
		case COLUMN_ENABLE:
			code = "config.alg.configuration.enable";
			break;
		case COLUMN_IP:
			code = "config.ip.track.ipAddress";
			break;
		case COLUMN_GATEWAY:
			code = "config.ip.track.gateway";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ipAddress.description";
			break;
		case COLUMN_TYPE:
			code = "config.ip.track.type";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_ENABLE));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_GATEWAY));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

}