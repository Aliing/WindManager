package com.ah.be.admin.auth;

import java.util.List;

import net.jradius.packet.attribute.AttributeFactory;

import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.be.admin.auth.agent.AhAuthAgent.AuthMethod;
import com.ah.be.admin.auth.agent.impl.AhDefaultAuthAgentImpl;
import com.ah.be.admin.auth.agent.impl.duplex.AhDuplexAuthAgentImpl;
import com.ah.be.admin.auth.agent.impl.radius.AhRadiusAuthAgentImpl;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.EnumConstUtil;

public class AhAuthFactory {

	private static AhAuthFactory instance;
	
	private AhAuthFactory() {
		super();
	}
	
	/**
	 * get an instance, implement singleton mode
	 *
	 * @return -
	 */
	public synchronized static AhAuthFactory getInstance() {
		if (instance == null) {
			instance = new AhAuthFactory();
		}

		return instance;
	}
	
	/**
	 * judge if exist VHM domain
	 *
	 * @return -
	 */
	public boolean isExistVhmDomain(){
		return QueryUtil.findRowCount(HmDomain.class, null) != 2;
	}
	
	/**
	 * get AhAuthAgent implement
	 * 
	 * @return -
	 */
	public AhAuthAgent getAuthAgent() {
		int authType = EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL; //default is local
		
//		//if exist VHM, don't need to get radius configuration
//		if (!isExistVhmDomain()) {
			// get the HmLoginAuthentication information from database
			List<?> bos = QueryUtil.executeQuery("select hmAdminAuth from "+HmLoginAuthentication.class.getSimpleName(),
					null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));

			if (!bos.isEmpty()) {
				authType = (Short) bos.get(0);
			}
			
			//if auth-type is RADIUS or both, should be load attribute dictionary.
			if (authType != EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL) {
				AttributeFactory.loadAttributeDictionary("net.jradius.dictionary.AttributeDictionaryImpl");
			}
//		}
		
		AhAuthAgent agent;

		switch (authType) {
			case EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS:
				agent = new AhRadiusAuthAgentImpl();
				break;
			case EnumConstUtil.ADMIN_USER_AUTHENTICATION_BOTH:
				agent = new AhDuplexAuthAgentImpl();
				break;
			case EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL:
			default:
				agent = new AhDefaultAuthAgentImpl();	//default is local
				break;
		}
		
		return agent;
	}

	public AhAuthAgent getAuthAgent(AuthMethod method) {
		AhAuthAgent candidate;

		switch (method) {
			case RADIUS:
				candidate = new AhRadiusAuthAgentImpl();
				break;
			case LOCAL:
			default:
				candidate = new AhDefaultAuthAgentImpl();
				break;
		}

		return candidate;
	}

}