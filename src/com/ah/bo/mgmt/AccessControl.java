package com.ah.bo.mgmt;

import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.ui.actions.Navigation;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;

public abstract class AccessControl {

	public enum CrudOperation {
		CREATE, READ, UPDATE, DELETE
	}

	public static void checkUserAccess(HmUser user, String feature,
			CrudOperation operation) throws HmException {
		if (user == null
				|| HmUserGroup.ADMINISTRATOR.equals(user.getUserGroup()
						.getGroupName())) {
			return;
		}
		HmPermission permission = user.getUserGroup().getFeaturePermissions()
				.get(feature);
		if (permission == null) {
			throw new HmException("User '" + user.getUserName()
					+ "' does not have access to feature '" + feature + "'.",
					HmMessageCodes.PERMISSION_DENIED_FEATURE, new String[] {
							user.getUserName(),
							Navigation.getFeatureName(feature) });
		} else {
			if (!operation.equals(CrudOperation.READ)
					&& !permission.hasAccess(HmPermission.OPERATION_WRITE)) {
				throw new HmException("User '" + user.getUserName()
						+ "' does not have WRITE access to feature '" + feature
						+ "'.", HmMessageCodes.PERMISSION_DENIED_OPERATION,
						new String[] { user.getUserName(),
								Navigation.getFeatureName(feature) });
			}
		}
	}

	public abstract void init();

	public abstract void destroy();

}