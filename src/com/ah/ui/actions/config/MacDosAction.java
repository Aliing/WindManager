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
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosParams.FrameType;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;

public class MacDosAction extends DosPreventionAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DOSTYPE = 2;
	
	public static final int COLUMN_DESCRIPTION = 3;

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
			code = "config.security.macdos.macDosName";
			break;
		case COLUMN_DOSTYPE:
			code = "config.security.macdos.dosTypeName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.security.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(3);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DOSTYPE));
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
				if (!setTitleAndCheckAccess(getText("config.title.macDos"))) {
					return getLstForward();
				}
				setSessionDataSource(new DosPrevention());
				prepareDosParams();
				prepareDosType();
//				getDataSource().setDosType(DosType.MAC);
				initDoSType();
				return returnResultKeyWord(INPUT,"macDosOnly") ;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				if (dataSource != null) {
					updateDosParams();
					prepareDosType();
					saveBanCheckBox();
				}
				String where = "dosPreventionName = :s1 AND dosType = :s2";
				Object[] values = new Object[2];
				values[0] = getDataSource().getDosPreventionName();
				values[1] = getDataSource().getDosType();
				if (checkNameExists(where, values)) {
					prepareDosParams();
					initDoSType();
					initBanCheckBox();
					if(isJsonMode()) {
						jsonObject.put("error", true);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists",
								values[0].toString()));
						return isParentIframeOpenFlg()? "macDosOnly" : "json";
					}else{
						return INPUT;
					}
				}
				if(isJsonMode() && !isParentIframeOpenFlg()){
					try{
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("option", "create");
						jsonObject.put("id", id);
						//jsonObject.put("dosType", getDataSource().getDosType());
						jsonObject.put("name", getDataSource().getDosPreventionName());
						jsonObject.put("parentDomID", getParentDomID());
					}catch(Exception e){
						jsonObject.put("error", true);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
					}
					jsonObject.put("error", false);
					return "json";
				}else{
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						initDoSType();
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
				editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.title.macDos.edit") + " '"
							+ getChangedDosPreventionName() + "'");
					prepareDosParams();
					initDoSType();
					initBanCheckBox();
				}
				//return returnWord;
				return	returnResultKeyWord(INPUT,"macDosOnly") ;
			} else if ("clone".equals(operation)) {
				//setSessionDataSource(new DosPrevention());
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
				initDoSType();
				initBanCheckBox();
				return INPUT;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				if (dataSource != null) {
					updateDosParams();
					prepareDosType();
					saveBanCheckBox();
					
					if(isJsonMode() && !isParentIframeOpenFlg()){
						try{
							updateBo(dataSource);
							jsonObject.put("option", "update");
							jsonObject.put("id", getDataSource().getId());
							jsonObject.put("name", getDataSource().getDosPreventionName());
							jsonObject.put("parentDomID", getParentDomID());
						}catch (Exception e) {
							jsonObject.put("error", true);
							jsonObject.put("errMsg", e.getMessage());
							return "json";
						}
						
						jsonObject.put("error", false);
						return "json";
					}else{
						if ("update".equals(operation)) {
							return updateBo();
						} else {
							updateBo(dataSource);
							setUpdateContext(true);
							initDoSType();
							return getLstForward();
						}
					}
				}else{
					if(isJsonMode()){
						return "macDosOnly";
					}else{
						baseOperation();
						return prepareBoList();
					}
				}
			} else {
				if(isJsonMode()){
					baseOperation();
					prepareBoList();
					return "macDosOnly";
				}else{
					baseOperation();
					return prepareBoList();
				}
				
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAC_DOS);
		setDataSource(DosPrevention.class);
		List<Object> lst = new ArrayList<Object>(2);
		lst.add(DosType.MAC);
		lst.add(DosType.MAC_STATION);
		filterParams = new FilterParams("dosType", lst);
		sortParams = new SortParams("dosType");
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MAC_DOS;
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
//		getDataSource().setDefaultFlag(false);
//		getDataSource().setDescription(profile.getDescription());
//		getDataSource().setId(null);
//		getDataSource().setOwner(null);
//		getDataSource().setVersion(null);
//	}

	protected void prepareDosParams() {
		DosPrevention dosPrevention = getDataSource();
		Map<String, DosParams> dosParamsMap = new LinkedHashMap<String, DosParams>();
		for (FrameType frameType : DosParams.FrameType.values()) {
			DosParams dosParams = dosPrevention.getDosParams(frameType);
			if (dosParams == null) {
				dosParams = new DosParams();
			}
			dosParams.setFrameType(frameType);
			dosParamsMap.put(dosParams.getkey(), dosParams);
		}
		dosPrevention.setDosParamsMap(dosParamsMap);
	}

	protected void initDoSType() {
		if (getDataSource() == null)
			return;
		if (getDataSource().getDosType() == null && getRadioMacDos() == null) {
			getDataSource().setDosType(DosType.MAC);
		}
		if (getDataSource().getDosType().equals(DosType.MAC)) {
			setRadioMacDos("mac");
			setActionTime_div("none");
		}
		if (getDataSource().getDosType().equals(DosType.MAC_STATION)) {
			setRadioMacDos("station");
			setActionTime_div("block");
		}
	}

	public void prepareDosType() {
		if (getDataSource() == null)
			return;
		if (getRadioMacDos() == null)
			return;
		if (getRadioMacDos().equals("mac"))
			getDataSource().setDosType(DosType.MAC);
		if (getRadioMacDos().equals("station"))
			getDataSource().setDosType(DosType.MAC_STATION);
	}

	public void initBanCheckBox() {
		if (getDataSource().getDosType()==DosType.MAC_STATION) {
			if (getDataSource().getDosParams(FrameType.ASSOC_REQ).getDosActionTime()<0){
				banForever2 = false;
				getDataSource().getDosParamsMap().get(FrameType.ASSOC_REQ.name()).setDosActionTime(60);
			} else {
				banForever2 = true;
			}
			if (getDataSource().getDosParams(FrameType.AUTH).getDosActionTime()<0){
				banForever5 = false;
				getDataSource().getDosParamsMap().get(FrameType.AUTH.name()).setDosActionTime(60);
			} else {
				banForever5 = true;
			}
			if (getDataSource().getDosParams(FrameType.EAPOL).getDosActionTime()<0){
				banForever7 = false;
				getDataSource().getDosParamsMap().get(FrameType.EAPOL.name()).setDosActionTime(60);
			} else {
				banForever7 = true;
			}
		}
	}

	public void saveBanCheckBox() {
		if (getDataSource().getDosType()==DosType.MAC_STATION) {
			if (!banForever2) {
				getDataSource().getDosParams(FrameType.ASSOC_REQ).setDosActionTime(-1);
			}
			if (!banForever5) {
				getDataSource().getDosParams(FrameType.AUTH).setDosActionTime(-1);
			}
			if (!banForever7) {
				getDataSource().getDosParams(FrameType.EAPOL).setDosActionTime(-1);
			}
		}
	}

	public DosType[] getDosTypeValues() {
		return DosType.values();
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

	public int getDosNameLength() {
		return getAttributeLength("dosPreventionName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	private boolean banForever2=true;
	private boolean banForever5=true;
	private boolean banForever7=true;
	
	public boolean getBanForever2() {
		return banForever2;
	}

	public void setBanForever2(boolean banForever2) {
		this.banForever2 = banForever2;
	}

	public boolean getBanForever5() {
		return banForever5;
	}

	public void setBanForever5(boolean banForever5) {
		this.banForever5 = banForever5;
	}

	public boolean getBanForever7() {
		return banForever7;
	}

	public void setBanForever7(boolean banForever7) {
		this.banForever7 = banForever7;
	}

	public TextItem[] getMacRadioType() {
		return new TextItem[] {new TextItem("mac", 
				getText("config.security.macdos.radioType.mac"))};
	}
	
	public boolean getDisabledRadioButton(){
		if (getDataSource() != null && getDataSource().getId() != null) {
			return true;
		}
		if (isJsonMode()) {
			return true;
		}
		return false;
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