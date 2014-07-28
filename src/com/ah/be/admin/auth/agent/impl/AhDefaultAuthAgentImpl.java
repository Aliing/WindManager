package com.ah.be.admin.auth.agent.impl;

import java.util.Collection;
import java.util.List;

import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AhDefaultAuthAgentImpl implements AhAuthAgent, QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhDefaultAuthAgentImpl.class.getSimpleName());

	@Override
	public AuthMethod getAuthMethod() {
		return AuthMethod.LOCAL;
	}

	/**
	 * @see com.ah.be.admin.auth.agent.AhAuthAgent#execute(java.lang.String, java.lang.String)
	 * @throws AhAuthException
	 *             1.User-name or password do not match
	 */
	@Override
	public HmUser execute(String userName, String userPassword) throws AhAuthException {
		HmUser user = null;
		if (null != userName && !"".equals(userName)) {
			//try {
				List<HmUser> allUsers;
				// super user deal with differently
				if (HmUser.ADMIN_USER.equals(userName)) {
					allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("userName = :s1 AND owner.domainName = :s2",
							new Object[]{HmUser.ADMIN_USER, HmDomain.HOME_DOMAIN}), null, this);
				} else {
					allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("lower(userName)", userName.toLowerCase()), null, this);
					// the user name is not unique
					if (allUsers.size() != 1) {
					// the user name is an email address
						String[] uAndD = userName.split("@");
						if (uAndD.length == 2) {
							// select by email address
							allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("lower(emailAddress)", userName.toLowerCase()), null, this);
						}
					}
				}
				if (allUsers.size() == 1) {
					user = allUsers.get(0);
				} else if (allUsers.size() > 1){
					log.warn("execute", "User {" + userName + "} exist more than one.");
					throw new AhAuthException("error.authFailed.more.than.one.user.exist");
				}
			// fix bug 17885
//			} catch (Exception e) {
//				log.error("execute", "Finding user failed.", e);
//				throw new AhAuthException("error.authFailed", e);
//			}
		}

		if (user == null) {
			log.warn("execute", "User {" + userName + "} doesn't exist.");
			throw new AhAuthException("error.authFailed");
		}

		HmDomain hmDomain = user.getDomain();

		// Check for the current status of VHM.
		switch (hmDomain.getRunStatus()) {
			case HmDomain.DOMAIN_RESTORE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to restoration for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.restoring");
			case HmDomain.DOMAIN_BACKUP_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to backup for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.backuping");
			case HmDomain.DOMAIN_UPDATE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to upgrade for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.updating");
			case HmDomain.DOMAIN_DISABLE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to disablement for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.disabled");
			default:
				break;
		}

		// check gml capability, check this in login page
//		if (!hmDomain.isSupportGM() && user.getUserGroup().isGMUserGroup()) {
//			log.warn("execute", "GML user but gml is disabled in this vhm.");
//			throw new AhAuthException("error.authFailed");
//		}

		String password;

		try {
			password = MgrUtil.digest(userPassword);
		} catch (Exception e) {
			log.error("execute", "Digesting user password failed.", e);
			throw new AhAuthException("error.authFailed", e);
		}

		if (password.equals(user.getPassword())) {
			return user;
		} else {
			log.warn("execute", "Username/Password Mismatch!");
			throw new AhAuthException("error.authFailed");
		}
	}

	@Override
	public int authenticate(String userName, String userPassword) throws AhAuthException {
		HmUser hmUser = execute(userName, userPassword);

		return hmUser.getUserGroup().getGroupAttribute();
	}

	@Override
	public Collection<HmBo> load(HmBo bo)
	{
		if (bo instanceof HmUser) {
			HmUser user = (HmUser) bo;
// cchen DONE
//			if (user.getTableColumns() != null)
//				user.getTableColumns().size();
//			if (user.getTableSizes() != null)
//				user.getTableSizes().size();
//			if (user.getAutoRefreshs() != null)
//				user.getAutoRefreshs().size();
			if (user.getUserGroup().getInstancePermissions() != null)
				user.getUserGroup().getInstancePermissions().size();
			if (user.getUserGroup().getFeaturePermissions() != null)
				user.getUserGroup().getFeaturePermissions().size();
		}
		return null;
	}

}