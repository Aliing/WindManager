package com.ah.ui.actions.config;

/*
 * @author Fisher
 */

import org.hibernate.validator.constraints.Range;

import com.ah.bo.mobility.InterRoaming;
import com.ah.ui.actions.BaseAction;

public class InterRoamingAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.layer3Roaming"))) {
					return getLstForward();
				}
				setSessionDataSource(new InterRoaming());
				return INPUT;
			} else if ("create".equals(operation)) {
				if (checkNameExists("roamingName", getDataSource().getRoamingName())) {
					return INPUT;
				}
				return createBo();
			} else if ("edit".equals(operation)) {
				return editBo();
			} else if ("update".equals(operation)) {
				return updateBo();
			} else if ("clone".equals(operation)) {				
				long cloneId = getSelectedIds().get(0);
				InterRoaming profile = (InterRoaming) findBoById(boClass,
						cloneId);
				profile.setRoamingName("");
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("roamingName", getDataSource().getRoamingName())) {
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
		setSelectedL2Feature(L2_FEATURE_INTER_SUBNET_ROAMING);
		setDataSource(InterRoaming.class);
	}

	public InterRoaming getDataSource() {
		return (InterRoaming) dataSource;
	}

	public Range getKeepAliveIntervalRange() {
		return getAttributeRange("keepAliveInterval");
	}

	public Range getKeepAliveAgeoutRange() {
		return getAttributeRange("keepAliveAgeout");
	}

	public Range getUpdateIntervalRange() {
		return getAttributeRange("updateInterval");
	}

	public Range getUpdateAgeoutRange() {
		return getAttributeRange("updateAgeout");
	}
	
	public int getRoamingNameLength() {
		return getAttributeLength("roamingName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getHideL3Setting() {
		if (getDataSource().getEnabledL3Setting()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getChangedRoamingName() {

		return getDataSource().getRoamingName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

}