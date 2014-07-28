package com.ah.be.admin.auth;

import java.io.Serializable;

public interface AhAuthable extends Serializable {

	/**
	 * Authenticates specified user and returns an appropriate user group attribute corresponds to.
	 *
	 * @param userName user name to be authenticated.
	 * @param password plaintext password going with the user name to be authenticated.
	 * @return an integer stands for a specific user group attribute.
	 * @throws AhAuthException if any error occurs during authentication.
	 */
	int authenticate(String userName, String password) throws AhAuthException;

}