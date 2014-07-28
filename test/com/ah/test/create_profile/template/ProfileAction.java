package com.ah.test.create_profile.template;

import com.ah.ui.actions.BaseAction;

public class ProfileAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText(""))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new ProfileBo());
				return INPUT;
			} else if ("create".equals(operation)) {
				if (checkNameExists("profileName", getDataSource().getProfileName())) {
					return INPUT;
				}
				return createBo();
			} else if ("edit".equals(operation)) {
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getChangedName() + "'");
				return editBo();
			} else if ("update".equals(operation)) {
				if (dataSource != null) {
				}
				return updateBo();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				ProfileBo profile = (ProfileBo) findBoById(boClass,
						cloneId);
				profile.setId(null);
				profile.setProfileName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
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
		setSelectedL2Feature(L2_FEATURE_ACCESS_CONSOLE);
		setDataSource(ProfileBo.class);
	}

	public ProfileBo getDataSource() {
		return (ProfileBo) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("profileName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getProfileName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
}
