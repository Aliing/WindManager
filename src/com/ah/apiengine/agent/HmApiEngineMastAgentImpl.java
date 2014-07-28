package com.ah.apiengine.agent;

import java.util.Collection;
import java.util.List;

import com.ah.apiengine.agent.subagent.CommandLineAgent;
import com.ah.apiengine.agent.subagent.CommonAgent;
import com.ah.apiengine.agent.subagent.HhmAgent;
import com.ah.apiengine.agent.subagent.UserAgent;
import com.ah.apiengine.agent.subagent.VhmAgent;
import com.ah.apiengine.agent.subagent.impl.CommandLineAgentImpl;
import com.ah.apiengine.agent.subagent.impl.CommonAgentImpl;
import com.ah.apiengine.agent.subagent.impl.HhmAgentImpl;
import com.ah.apiengine.agent.subagent.impl.UserAgentImpl;
import com.ah.apiengine.agent.subagent.impl.VhmAgentImpl;
import com.ah.apiengine.element.Login;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public final class HmApiEngineMastAgentImpl implements HmApiEngineMastAgent {

	private static HmApiEngineMastAgentImpl	instance;

	/* HHM Sub-Agent */
	private final HhmAgent							hhmAgent		= new HhmAgentImpl();

	/* User Sub-Agent */
	private final UserAgent							userAgent		= new UserAgentImpl();

	/* VHM Sub-Agent */
	private final VhmAgent							vhmAgent		= new VhmAgentImpl();

	private final CommandLineAgent					commandAgent	= new CommandLineAgentImpl();

	private final CommonAgent						commonAgent		= new CommonAgentImpl();

	public synchronized static HmApiEngineMastAgentImpl getInstance() {
		if (instance == null) {
			instance = new HmApiEngineMastAgentImpl();
		}

		return instance;
	}

	@Override
	public HhmAgent getHhmAgent() {
		return hhmAgent;
	}

	@Override
	public UserAgent getUserAgent() {
		return userAgent;
	}

	@Override
	public VhmAgent getVhmAgent() {
		return vhmAgent;
	}

	@Override
	public CommandLineAgent getCommandAgent() {
		return commandAgent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ah.be.apiengine.agent.HmApiEngineMastAgent#login(com.ah.be.apiengine.element.Login)
	 */
	@Override
	public boolean login(Login login) throws HmApiEngineException {
		if (null != login) {
			String userName = login.getEngineUserName();

			// Super user authentication.
			if (NmsUtil.getHMScpUser().equalsIgnoreCase(userName)) {
				return NmsUtil.getHMScpPsd().equals(login.getEnginePassword());
			} else { // Admin user authentication.
				if (HmUser.ADMIN_USER.equals(userName)) {
					List<?> passList = QueryUtil.executeQuery("SELECT password FROM " + HmUser.class.getSimpleName(), null,
							new FilterParams("userName = :s1 AND owner.domainName = :s2",
									new Object[] { userName, HmDomain.HOME_DOMAIN }));
					if (passList.size() != 1) {
						throw new HmApiEngineException(MgrUtil
								.getUserMessage("error.auth.between.hhm.portal.login.exist"));
					} else {
						String password = (String) passList.get(0);
						if (password.equals(login.getEnginePassword())) {
							return true;
						} else {
							throw new HmApiEngineException(MgrUtil.getUserMessage(
									"error.auth.between.hhm.portal.login.match", "password"));
						}
					}
				} else {
					throw new HmApiEngineException(MgrUtil.getUserMessage(
							"error.auth.between.hhm.portal.login.match", "user name"));
				}
			}
		} else {
			throw new HmApiEngineException(MgrUtil
					.getUserMessage("error.auth.between.hhm.portal.login.null"));
		}
	}

	@Override
	public Collection<HMUpdateSoftwareInfo> queryDnsUpdateInfos() throws HmApiEngineException {
		return HHMoperate.getChangeDNSInfo();
	}

	@Override
	public CommonAgent getCommonAgent() {
		return commonAgent;
	}

}