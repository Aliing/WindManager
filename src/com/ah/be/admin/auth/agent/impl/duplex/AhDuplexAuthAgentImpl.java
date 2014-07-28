package com.ah.be.admin.auth.agent.impl.duplex;

import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.be.admin.auth.agent.impl.AhDefaultAuthAgentImpl;
import com.ah.be.admin.auth.agent.impl.radius.AhRadiusAuthAgentImpl;
import com.ah.bo.admin.HmUser;

public class AhDuplexAuthAgentImpl implements AhAuthAgent {

	private static final long serialVersionUID = 1L;

	@Override
	public AuthMethod getAuthMethod() {
		return AuthMethod.DUPLEX;
	}
	
	/**
	 * @see com.ah.be.admin.auth.agent.AhAuthAgent#execute(String, String)
	 */
	@Override
	public HmUser execute(String userName, String userPassword) throws AhAuthException {
		HmUser user = null;
		boolean isRadiusAuthFailed = false;
		AhAuthAgent authAgent;

		// first do RADIUS authenticate.
		try {
			authAgent = new AhRadiusAuthAgentImpl();
			user = authAgent.execute(userName, userPassword);
		} catch (Exception e) {
			isRadiusAuthFailed = true;
		}
		
		// failed then do default authenticate.
		if (isRadiusAuthFailed) {
			authAgent = new AhDefaultAuthAgentImpl();
			user = authAgent.execute(userName, userPassword);
		}
	
		return user;
	}

	@Override
	public int authenticate(String userName, String userPassword) throws AhAuthException {
		HmUser hmUser = execute(userName, userPassword);

		return hmUser.getUserGroup().getGroupAttribute();
	}

}