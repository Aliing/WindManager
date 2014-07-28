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
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosParams.DosAction;
import com.ah.bo.network.DosParams.ScreeningType;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.util.MgrUtil;

public class IpDosAction extends DosPreventionAction implements QueryBo {

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
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.security.ipdos.ipDosName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.security.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);
		
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
				if (!setTitleAndCheckAccess(getText("config.title.ipDos"))) {
					return getLstForward();
				}
				setSessionDataSource(new DosPrevention(DosType.IP));
				prepareDosParams();
				return returnResultKeyWord(INPUT,"ipDosOnly");
			} else if ("create".equals(operation)) {
				updateDosParams();
				String where = "dosPreventionName = :s1 AND dosType = :s2";
				Object[] values = new Object[2];
				values[0] = getDataSource().getDosPreventionName();
				values[1] = DosType.IP;
				if (checkNameExists(where, values)) {
					if(isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("error", true);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.objectExists",
								values[0].toString()));
						return "json";
					} else {
						return INPUT;
					}
				}
				if(isJsonMode()){
					id = createBo(dataSource);
					setUpdateContext(true);
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					jsonObject.put("option", "create");
					jsonObject.put("id", id);
					jsonObject.put("name", getDataSource().getDosPreventionName());
					return "json";
				}else{
					return createBo();
				}
			} else if (("create" + getLstForward()).equals(operation)) {
				updateDosParams();
				String where = "dosPreventionName = :s1 AND dosType = :s2";
				Object[] values = new Object[2];
				values[0] = getDataSource().getDosPreventionName();
				values[1] = DosType.IP;
				if (checkNameExists(where, values)) {
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
				editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.title.ipDos.edit")
								+ " '" + getChangedDosPreventionName() + "'");
					prepareDosParams();
				}
				return returnResultKeyWord(INPUT,"ipDosOnly");
			} else if ("clone".equals(operation)) {
				//setSessionDataSource(new DosPrevention(DosType.IP));
				long cloneId = getSelectedIds().get(0);
				DosPrevention profile = (DosPrevention) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				profile.setDosPreventionName("");
				setSessionDataSource(profile);
				prepareDosParams();
				return INPUT;
			} else if ("update".equals(operation)) {
				String ret="";
				if (dataSource != null) {
					updateDosParams();
					ret =updateBo();
					if(isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("option", "update");
						jsonObject.put("id", getDataSource().getId());
						jsonObject.put("name", getDataSource().getDosPreventionName());
						return "json";
					}
				}
				if (ret.equals("")) {
					baseOperation();
					return prepareBoList();
				}
				return  ret;
			} else if (("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateDosParams();
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else {
				if(isJsonMode()){
					prepareBoList();
					return "ipDosOnly";
				}
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_IP_DOS);
		setDataSource(DosPrevention.class);
		filterParams = new FilterParams("dosType", DosType.IP);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_IP_DOS;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		DosPrevention source = QueryUtil.findBoById(DosPrevention.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<DosPrevention> list = QueryUtil.executeQuery(DosPrevention.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (DosPrevention profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			DosPrevention up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setDosPreventionName(profile.getDosPreventionName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);

			hmBos.add(up);
		}
		return hmBos;
	}


//	public void initDataSource(DosPrevention profile) {
//		getDataSource().setDosPreventionName("");
//		getDataSource().setDosParamsMap(profile.getDosParamsMap());
//		getDataSource().setDosType(profile.getDosType());
//		getDataSource().setEnabledSynCheck(profile.getEnabledSynCheck());
//		getDataSource().setDefaultFlag(false);
//		getDataSource().setDescription(profile.getDescription());
//		getDataSource().setId(null);
//		getDataSource().setOwner(null);
//		getDataSource().setVersion(null);
//	}

	protected void prepareDosParams() {
		DosPrevention dosPrevention = getDataSource();
		Map<String, DosParams> dosParamsMap = new LinkedHashMap<String, DosParams>();
		for (ScreeningType screeningType : DosParams.ScreeningType.values()) {
			DosParams dosParams = dosPrevention.getDosParams(screeningType);
			if (dosParams == null) {
				dosParams = new DosParams();
			}
			dosParams.setScreeningType(screeningType);
			dosParamsMap.put(dosParams.getkey(), dosParams);
		}
		dosPrevention.setDosParamsMap(dosParamsMap);
	}

	public DosAction[] getDosActionValues() {
		return DosAction.values();
	}
	
	public DosAction[] getDosActionValuesReject() {
		DosAction[] dosAction = new DosAction[3];
		dosAction[0] = DosAction.ALARM;
		dosAction[1] = DosAction.BAN;
		dosAction[2] = DosAction.BAN_FOREVER;
		return dosAction;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedDosPreventionName() {
		return getDataSource().getDosPreventionName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public int getIpDosNameLength() {
		return getAttributeLength("dosPreventionName");
	}
	
	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		dataSource = bo;
		if (bo == null) {
			return null;
		}
		if (getDataSource().getDosParamsMap() != null)
			getDataSource().getDosParamsMap().size();
		return null;
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}

}