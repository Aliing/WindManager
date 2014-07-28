package com.ah.be.admin.auth.agent;

import com.ah.be.admin.auth.AhAuthable;
import com.ah.be.admin.auth.AhAuthException;
import com.ah.bo.admin.HmUser;

/**
 * @author Jonathan Yu
 */
public interface AhAuthAgent extends AhAuthable {

	enum AuthMethod {
		LOCAL, RADIUS, DUPLEX
	}

	/**
	 * Returns the authentication method to a specific implementation.
	 *
	 * @return an instance of AuthMethod associated with the specific implementation.
	 */
	AuthMethod getAuthMethod();

	/**
	 * execute authenticate and authorize
	 * 
	 * @param userName user name to be authenticated.
	 * @param userPassword plaintext password going with the user name to be authenticated.
	 * @return HmUser
	 * @throws AhAuthException
	 *             1.User-name or password do not match 2.No any RADIUS server 3.RADIUS exception
	 *             4.The RADIUS server(s) have no response 5.The RADIUS server(s) reject
	 */
	HmUser execute(String userName, String userPassword) throws AhAuthException;

}