package com.ah.ws.rest.server.auth.modules;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.ws.rest.server.auth.RestPrincipal;
import com.ah.ws.rest.server.auth.exception.RestException.RestEx;

public class DBLoginModule implements LoginModule {
	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// username and secretkey
	private String username;
	private String secretkey;

	// configurable option
	private String auth_table;
	private String username_col;
	private String username_default;
	private boolean alternate = false;
	private String alternate_key;
	private String alternate_principal;
	private String alternate_file;
	private String secretkey_col;
	private String principal_col;

	private static Properties keys = new Properties();

	// user's principal
	private String principal_role;
	private RestPrincipal restPrincipal;

	// database connection
	private Connection connection;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;

		alternate = "true".equalsIgnoreCase((String) options.get("alternate"));
		auth_table = (String) options.get("auth_table");
		alternate_key = (String) options.get("alternate_key");
		alternate_principal = (String) options.get("alternate_principal");
		alternate_file = (String) options.get("alternate_file");
		username_col = (String) options.get("username_col");
		username_default = (String) options.get("username_default");
		secretkey_col = (String) options.get("secretkey_col");
		principal_col = (String) options.get("principal_col");

		if (alternate && keys.isEmpty()) {

			InputStream fis = null;
			try {
				fis = new FileInputStream(alternate_file);

				keys.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
					}
			}
		}
	}

	@Override
	public boolean login() throws LoginException {
		// get username and secretkey
		if (callbackHandler == null)
			throw new LoginException(MgrUtil.getUserMessage(RestEx.JAAS_CONFIG.toString()));

		try {
			Callback[] callbacks = new Callback[2];
			callbacks[0] = new NameCallback(":");
			callbacks[1] = new PasswordCallback(":", false);
			callbackHandler.handle(callbacks);

			username = ((NameCallback) callbacks[0]).getName();

			char[] tmpsecretkey = ((PasswordCallback) callbacks[1])
					.getPassword();
			if (tmpsecretkey == null)
				tmpsecretkey = new char[0];
			secretkey = new String(tmpsecretkey);

			((PasswordCallback) callbacks[1]).clearPassword();

			if (alternate) {
				String uname = "",un_key="";
				if (username != null) uname = username;
				if (StringUtils.isEmpty(uname)) uname = "default";

				un_key = alternate_key + "." + uname;
				for (Enumeration<Object> enums = keys.keys(); enums.hasMoreElements();) {
					String key = (String)enums.nextElement();
					if (key.equals(un_key)) {
						if (keys.get(key).equals(secretkey)) {
							principal_role = keys.getProperty(alternate_principal + "." + uname);
							succeeded = true;
							break;
						}
					}
				}
			}
			if(!succeeded) {
				if (connection == null || connection.isClosed())
					connection = QueryUtil.getConnection();
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					ps = connection.prepareStatement("SELECT enableApiAccess," + secretkey_col
							+ " FROM hmservicessettings " 
							+ "WHERE " + username_col + " = ?");
					if (StringUtils.isEmpty(username))
						username = username_default;
					ps.setString(1, username);
					rs = ps.executeQuery();

					if (!rs.next())
						throw new LoginException(MgrUtil.getUserMessage(RestEx.AUTH_BAD_SECRETKEY.toString(), new String[]{secretkey, username}));
					boolean enableApiAccess = rs.getBoolean(1);
	   			    if(!enableApiAccess){
	   				    throw new LoginException(MgrUtil.getUserMessage("mgmtSettings.APIsetting.APIAccess.disable"));
	   				 }
					String secretkey_db = rs.getString(2);
   				   
					principal_role = "HM API User";
					succeeded = MgrUtil.digest(secretkey).equals(secretkey_db);
				} catch (SQLException e) {
					throw new LoginException(MgrUtil.getUserMessage(RestEx.JAAS_CONFIG_DB.toString()));
				} finally {
					try {
						if (rs != null)
							rs.close();
						if (ps != null)
							ps.close();
						if (connection != null)
							connection.close();
					} catch (Exception e) {
					}
				}
			}
			if (!succeeded)
				throw new LoginException(MgrUtil.getUserMessage(RestEx.AUTH_BAD_SECRETKEY.toString(), new String[]{secretkey, username}));

			return succeeded;
		} catch (SQLException e) {
			throw new LoginException(MgrUtil.getUserMessage(RestEx.JAAS_CONFIG_DB.toString()));
		} catch (LoginException e) {
			throw e;
		} catch (Exception e) {
			throw new LoginException(MgrUtil.getUserMessage(RestEx.JAAS_CONFIG.toString()));
		}
	}

	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			if (StringUtils.isBlank(principal_role))
				throw new LoginException(MgrUtil.getUserMessage(RestEx.AUTH_PRINCIPAL_NULL.toString(), username));

			// add a Principal (authenticated identity)
			restPrincipal = new RestPrincipal(principal_role);

			if (!subject.getPrincipals().contains(restPrincipal))
				subject.getPrincipals().add(restPrincipal);

			// clean out state
			username = null;
			secretkey = null;
			commitSucceeded = true;
			return true;
		}
	}

	@Override
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			secretkey = null;
			restPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(restPrincipal);

		succeeded = false;
		commitSucceeded = false;
		username = null;
		secretkey = null;
		restPrincipal = null;
		return true;
	}
}
