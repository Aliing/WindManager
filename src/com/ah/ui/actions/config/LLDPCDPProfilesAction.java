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
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class LLDPCDPProfilesAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
//	private static final Tracer	log	= new Tracer(LLDPCDPProfilesAction.class.getSimpleName());	

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.lldpcdpprofile.title"))) {
					return getLstForward();
				}
				setSessionDataSource(new LLDPCDPProfile());
//				return INPUT;
				return getReturnPath(INPUT, "lldpCdpJson");
			} else if ("create".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					if (checkNameExists("profileName", getDataSource().getProfileName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getProfileName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getProfileName());
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
					if (checkNameExists("profileName", getDataSource().getProfileName())) {
						return INPUT;
					}
					return createBo();
				}
			} else if (("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("profileName", getDataSource().getProfileName())) {
					return INPUT;
				}

				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription() + " > Edit '"
						+ getDisplayName() + "'");
//				return strForward;
				return getReturnPath(strForward, "lldpCdpJson");				
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					return updateBo();
				}
			} else if (("update" + getLstForward()).equals(operation)) {
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				LLDPCDPProfile profile = (LLDPCDPProfile) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setProfileName(null);
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				// addLstTitle(getText("config.ethernet.access.title"));
				return INPUT;
			} else {
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
		setSelectedL2Feature(L2_FEATURE_LLDPCDP_PROFILE);
		setDataSource(LLDPCDPProfile.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_LLDPCDPPROFILE;
	}

	@Override
	public LLDPCDPProfile getDataSource() {
		return (LLDPCDPProfile) dataSource;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_NAME			= 1;

	public static final int	cOLUMN_DESCRIPTION	= 3;
	
	public static final int COLUMN_TYPE			= 2;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	@Override
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.lldpcdpprofile.name";
			break;
		case cOLUMN_DESCRIPTION:
			code = "config.ipFilter.description";
			break;
		case COLUMN_TYPE:
			code = "config.switchSettings.lldp.device.type";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(cOLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_TYPE));

		return columns;
	}

	public String getDisplayName() {
		return getDataSource().getProfileName().replace("\\", "\\\\").replace("'", "\\'");
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof LLDPCDPProfile) {

		}

		return null;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		LLDPCDPProfile source = QueryUtil.findBoById(LLDPCDPProfile.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<LLDPCDPProfile> list = QueryUtil.executeQuery(LLDPCDPProfile.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (LLDPCDPProfile profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			LLDPCDPProfile up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setProfileName(profile.getProfileName());
			up.setOwner(profile.getOwner());
			hmBos.add(up);
		}
		return hmBos;
	}

	private String getReturnPath(String normalPath, String jsonModePath) {
		return isJsonMode() ? jsonModePath : normalPath;
	}
	
	public boolean getCdpMaxEntriesStatus(){
		if(getDataSource().isEnableCDP() 
				|| getDataSource().isEnableCDPHostPorts() 
				|| getDataSource().isEnableCDPNonHostPorts()){
			return false;
		} 
		return true;
	}
	
	public boolean getLldpCommonFilesStatus(){
		if(getDataSource().isEnableLLDP()
				|| getDataSource().isEnableLLDPHostPorts()
				|| getDataSource().isEnableLLDPNonHostPorts()){
			return false;
		}
		return true;
	}
	
	public boolean getLldpApBrOnlyStatue(){
		return getDataSource().isEnableLLDP() ? false : true;
	}
	
	public boolean getLldpSwitchOnlyStatue(){
		if(getDataSource().isEnableLLDPHostPorts() || getDataSource().isEnableLLDPNonHostPorts()){
			return false;
		}
		return true;
	}
}