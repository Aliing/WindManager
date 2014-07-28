package com.ah.be.protocol.ldap;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.ah.util.Tracer;

public class AhLdapAuthImpl implements AhLdapAuthable {

	private static final Tracer log = new Tracer(AhLdapAuthImpl.class.getSimpleName());

	/* LDAP server to connect */
	private final String host;

	/* Service port number, 389 and 636 for SSL by default */
	private int port = 389;

	/* Indicates if using SSL */
	private boolean enablingSSL = false;

	/* Principal for authentication */
	private String bindDN;

	/* Credentials associated with 'bindDN' for authentication */
	private String bindPW;

	/*
	 * Security Authentication Level
	 *
	 * In toString().toLowerCase() format evaluated to Context.SECURITY_AUTHENTICATION attribute during the environment initialization for DirContext.
	 */
	private AuthLevel authLevel = AuthLevel.SIMPLE;

	/* An instance of DirContext for performing various of LDAP operations. */
	private DirContext context;

	public AhLdapAuthImpl(String host) {
		this.host = host;
	}

	public AhLdapAuthImpl(String host, int port) {
		this(host);
		this.port = port;
	}

	public AhLdapAuthImpl(String host, int port, boolean enablingSSL) {
		this(host, port);
		this.enablingSSL = enablingSSL;
	}

	public AhLdapAuthImpl(String host, int port, boolean enablingSSL, AuthLevel authLevel) {
		this(host, port, enablingSSL);
		this.authLevel = authLevel;
	}

	public AhLdapAuthImpl(String host, int port, boolean enablingSSL, AuthLevel authLevel, String bindDN, String bindPW) {
		this(host, port, enablingSSL, authLevel);
		this.bindDN = bindDN;
		this.bindPW = bindPW;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEnablingSSL() {
		return enablingSSL;
	}

	public void setEnablingSSL(boolean enablingSSL) {
		this.enablingSSL = enablingSSL;
	}

	public AuthLevel getAuthLevel() {
		return authLevel;
	}

	public void setAuthLevel(AuthLevel authLevel) {
		this.authLevel = authLevel;
	}

	public String getBindDN() {
		return bindDN;
	}

	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	public String getBindPW() {
		return bindPW;
	}

	public void setBindPW(String bindPW) {
		this.bindPW = bindPW;
	}

	/**
	 * Encrypt password with Base64.
	 *
	 * @param method the name of specified algorithm to encrypt.
	 * @param password plaintext password.
	 * @param salt the initial random participated in the encryption.
	 * @return A composite string in the format of {algorithm} + Base64.encrypt(digest + salt)
	 * @throws NoSuchAlgorithmException if no Provider supports the implementation for the specified algorithm.
	 */
	/*-
	public static String encrypt(String method, String password, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest md;
		method = method.toUpperCase();

		// Initialize a MessageDigest instance with the algorithm specified.
		if (method.equals("{SSHA}")) {
			md = MessageDigest.getInstance("SHA-1");
		} else if (method.equals("{SHA}")) {
			md = MessageDigest.getInstance("SHA-1");
		} else if (method.equals("{MD5}")) {
			md = MessageDigest.getInstance("MD5");
		} else if (method.equals("{SMD5}")) {
			md = MessageDigest.getInstance("MD5");
		} else {
			throw new NoSuchAlgorithmException("The algorithm " + method + " input is incorrect format or not supported.");
		}

		// The beginning 20 bytes is the encrypted component and the left bytes is the salt.
		if (salt == null) {
			salt = new byte[0];
		}

		// Append the plaintext password into the MessageDigest instance.
		md.update(password.getBytes());

		// Append the salt into the MessageDigest instance.
		md.update(salt);

		byte[] digest = md.digest();
		byte[] ldapPW = digest;

		if (salt.length > 0) {
			ldapPW = new byte[digest.length + salt.length];
			System.arraycopy(digest, 0, ldapPW, 0, digest.length);
			System.arraycopy(salt, 0, ldapPW, digest.length, salt.length);
		}

		// Return the formatted password.
		return method + Base64.encode(ldapPW);
	}*/

	/**
	 * Verify password.
	 *
	 * @param ldapPW A composite string in the format of {algorithm} + Base64.encrypt(digest + salt)
	 * @param inputPW plaintext password.
	 * @return true if the two passwords with different formats are the same through comparison, false otherwise.
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
	 */
	/*-
	public static boolean verify(String ldapPW, String inputPW) throws NoSuchAlgorithmException {
		MessageDigest md;
		int len;

		// Initialize a MessageDigest instance with the algorithm specified.
		if (ldapPW.startsWith("{SSHA}")) {
			ldapPW = ldapPW.substring(6);
			md = MessageDigest.getInstance("SHA-1");
			len = 20;
		} else if (ldapPW.startsWith("{SHA}")) {
			ldapPW = ldapPW.substring(5);
			md = MessageDigest.getInstance("SHA-1");
			len = 20;
		} else if (ldapPW.startsWith("{MD5}")) {
			ldapPW = ldapPW.substring(5);
			md = MessageDigest.getInstance("MD5");
			len = 16;
		} else if (ldapPW.startsWith("{SMD5}")) {
			ldapPW = ldapPW.substring(6);
			md = MessageDigest.getInstance("MD5");
			len = 16;
		} else {
			throw new NoSuchAlgorithmException("Invalid LDAP password format - " + ldapPW + " input.");
		}

		// Decode with Base64.
		byte[] digestAndSalt = Base64.decode(ldapPW);
		byte[] shacode;
		byte[] salt;

		// The beginning 20 bytes is the encrypted component and the left bytes is the salt.
		if (digestAndSalt.length <= len) {
			shacode = digestAndSalt;
			salt = new byte[0];
		} else {
			shacode = new byte[len];
			salt = new byte[digestAndSalt.length - len];
			System.arraycopy(digestAndSalt, 0, shacode, 0, len);
			System.arraycopy(digestAndSalt, len, salt, 0, salt.length);
		}

		// Append the plaintext password into the MessageDigest instance.
		md.update(inputPW.getBytes());

		// Append the salt into the MessageDigest instance.
		md.update(salt);

		byte[] digest = md.digest();

		// Return the verification result.
		return MessageDigest.isEqual(shacode, digest);
	}*/

	/**
	 * Constructs an environment to be used to create a <tt>DirContext</tt> instance.
	 *
	 * @param principal DN specified for authentication.
	 * @param credentials going with principal for authentication.
	 * @return a <tt>Hashtable</tt> of environment to create a <tt>DirContext</tt> instance.
	 */
	private Hashtable<String, String> initLdapEnv(String principal, String credentials) {
		String url = "ldap://" + host + ":" + port;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, credentials);
		env.put(Context.SECURITY_AUTHENTICATION, authLevel.toString().toLowerCase());

		if (enablingSSL) {
			/**
			 * To enable SSL should perform the following steps during the initialization of enviorment.
			 *
			 * 1. Register CA certificate with the keytool utility as shown below:
			 *
			 * 	  keytool# -importcert -alias <cacert name> -file <cacert file> -keystore <keystore file>
			 *    Note: For the reason of java policy, it is better not import CA certificate into the default keystore given by SUN located at <JRE_HOME>\lib\security\cacerts
			 *
			 * 2. String keystore = "C:\\.keystore";
			 *    System.setProperty("javax.net.ssl.trustStore", keystore);
			 */

			// The following two statements have the same effect under JDK 1.5 or later.
		//	env.put(Context.PROVIDER_URL, "ldaps://" + host + ":" + port);
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		return env;
	}

	/**
	 * Searches in the named context for the attributes that satisfy the given search filter. Perform the search as specified by search control. This method is called internally.
	 *
	 * @param context used to perform the search operation.
	 * @param dn where the searching engine starts.
	 * @param filter the filter expression to use for the search.
	 * @param searchScope scope with which to apply the search. One of <tt>ONELEVEL_SCOPE</tt>, <tt>OBJECT_SCOPE</tt>, or <tt>SUBTREE_SCOPE</tt>.
	 * @param returnAttrs specifies the attributes that will be returned as part of the search.
	 * @return attributes searched and never be <code>null</code>.
	 * @throws NamingException if a naming exception is encountered.
	 */
	private Attribute[] search(DirContext context, String dn, String filter, int searchScope, String[] returnAttrs) throws NamingException {
		// Hold the searched attributes.
		List<Attribute> searchedAttrs = new ArrayList<Attribute>();
		String attrIds = "";

		if (returnAttrs == null) {
			attrIds = "all";
		} else if (returnAttrs.length == 0) {
			attrIds = "none";
		} else {
			for (int i = 0; i < returnAttrs.length; i++) {
				attrIds += returnAttrs[i];

				if (i != returnAttrs.length - 1) {
					attrIds += ", ";
				}
			}
		}

		// Search for attributes with filter.
		if (filter != null && !filter.trim().equals("")) {
			log.info("search", "Searching in dn {" + dn + "} for attributes {" + attrIds + "} with filter {" + filter + "}.");
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(searchScope);
			constraints.setReturningAttributes(returnAttrs);

			for (NamingEnumeration<SearchResult> results = context.search(dn, filter, constraints); results.hasMore();) {
				SearchResult result = results.next();
				String searchedDN = result.getNameInNamespace();
				log.info("search", "Searched DN: " + searchedDN);
				Attributes attrs = result.getAttributes();

				for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
					Attribute attr = attrEnum.next();

					if (attr != null) {
						log.info("search", "Searched attribute: " + searchedDN);
						searchedAttrs.add(attr);
					}
				}
			}
		} else {// Search for attributes without any filter.
			log.info("search", "Searching in dn {" + dn + "} for attributes {" + attrIds + "} without any filter.");
			Attributes attrs = context.getAttributes(dn);

			// All of attributes should be included if null is evaluated to the returning attributes object.
			if (returnAttrs == null) {
				for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
					Attribute attr = attrEnum.next();

					if (attr != null) {
						searchedAttrs.add(attr);
					}
				}
			} else {
			// Just for the attribute matching any of search controls will be included.
				for (String returnAttr : returnAttrs) {
					Attribute attr = attrs.get(returnAttr);

					if (attr != null) {
						searchedAttrs.add(attr);
					}
				}
			}
		}

		return searchedAttrs.toArray(new Attribute[searchedAttrs.size()]);
	}

	/**
	 * Search in specified DN for user DNs with filter.
	 *
	 * @param dn where the searching engine starts.
	 * @param filter the filter expression to use for the search; may not be <code>null</code>.
	 * @param maxReturnNum specifies the maximum number of results to be returned. The value no more than 0 denotes total results found will be returned.
	 * @return userDNs searched and never be <code>null</code>.
	 * @throws NamingException if a naming exception is encountered.
	 */
	private String[] searchUserDN(String dn, String filter, int maxReturnNum) throws NamingException {
		// Initialize the directory context object if it has not been done yet.
		if (context == null) {
			connect();
		}

		int currNum = 0;
		List<String> userDNs = new ArrayList<String>();
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		log.info("searchUserDN", "Searching userDN with filter " + filter);

		for (NamingEnumeration<SearchResult> results = context.search(dn, filter, constraints); results.hasMore();) {
			SearchResult result = results.next();
			String userDN = result.getNameInNamespace();
			log.info("searchUserDN", "Found userDN: " + userDN);

			/*-
			Attributes attrs = result.getAttributes();
			log.info("searchUserDN", attrs.size() + " are contained in " + userDN);

			for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
				Attribute attr = attrEnum.next();
				log.info("searchUserDN", attr.size() + " values are contained in the found attribute " + attr.getID());

				for (NamingEnumeration<?> objects = attr.getAll(); objects.hasMore();) {
					Object obj = objects.next();

					if (obj != null) {
						if (obj instanceof byte[]) {
							log.info("searchUserDN", attr.getID() + " :: " + Base64.encode((byte[])obj));
						} else {
							log.info("searchUserDN", attr.getID() + " : " + obj);
						}
					}
				}
			}*/

			userDNs.add(userDN);

			if (++currNum == maxReturnNum) {
				break;
			}
		}

		return userDNs.toArray(new String[userDNs.size()]);
	}

	/**
	 * Connect LDAP server and authenticate with specified arguments.
	 *
	 * @param principal DN specified for authentication.
	 * @param credentials going with principal for authentication.
	 * @throws NamingException if a naming exception is encountered.
	 */
	@Override
	public void connect(String principal, String credentials) throws NamingException {
		bindDN = principal;
		bindPW = credentials;
		connect();
	}

	/**
	 * Connect LDAP server and authenticate with existing settings.
	 *
	 * @throws NamingException if a naming exception is encountered.
	 */
	@Override
	public synchronized void connect() throws NamingException {
		if (bindDN == null || bindDN.trim().isEmpty()) {
			throw new NamingException("Invalid principal {" + bindDN + "}.");
		}

		if (bindPW == null || bindPW.trim().isEmpty()) {
			throw new NamingException("Invalid credentials {" + bindPW + "}.");
		}

		Hashtable<String, String> env = initLdapEnv(bindDN, bindPW);
		long startTime = System.currentTimeMillis();
		log.info("connect", "Connecting " + env.get(Context.PROVIDER_URL));
		context = new InitialDirContext(env);
		long endTime = System.currentTimeMillis();
		log.info("connect", "It took " + (endTime - startTime) + "ms to connect " + host + " and authenticate with " + bindDN);
	}

	/**
	 * Search in specified DN for attributes with filter and search controls given.
	 *
	 * @param dn where the searching engine starts.
	 * @param filter the filter expression to use for the search; may not be <code>null</code>.
	 * @param returnAttrs specifies the attributes that will be returned as part of the search.
	 * @return attributes searched and never be <code>null</code>.
	 * @throws NamingException if a naming exception is encountered.
	 */
	@Override
	public Attribute[] searchAttributes(String dn, String filter, String[] returnAttrs) throws NamingException {
		// Initialize the directory context object if it has not been done yet.
		if (context == null) {
			connect();
		}

		return search(context, dn, filter, SearchControls.SUBTREE_SCOPE, returnAttrs);
	}

	/**
	 * Search in specified DN for user DNs with filter.
	 *
	 * @param dn where the searching engine starts.
	 * @param filter the filter expression to use for the search; may not be <code>null</code>.
	 * @return userDNs searched and never be <code>null</code>.
	 * @throws NamingException if a naming exception is encountered.
	 */
	@Override
	public String[] searchUserDN(String dn, String filter) throws NamingException {
		return searchUserDN(dn, filter, 0);
	}

	/**
	 * Search in specified DN for a single userDN with filter.
	 *
	 * @param dn where the searching engine starts.
	 * @param filter the filter expression to use for the search; may not be <code>null</code>.
	 * @return userDN searched; return <code>null</code> if nothing to be found.
	 * @throws NamingException if a naming exception is encountered.
	 */
	@Override
	public String searchSingleUserDN(String dn, String filter) throws NamingException {
		String[] userDNs = searchUserDN(dn, filter, 1);

		return userDNs.length > 0 ? userDNs[0] : null;
	}

	/**
	 * Authenticate over LDAP server and return an instance of <tt>DirContext</tt> has performed a successful authentication with the arguments given.
	 *
	 * @param userDN userDN to be authenticated.
	 * @param password the plaintext password associated with the userDN to be authenticated.
	 * @throws NamingException if a naming exception is encountered.
	 * @return An instance of <tt>DirContext</tt> has performed a successful authentication with the arguments given.
	 */
	@Override
	public DirContext authenticate(String userDN, String password) throws NamingException {
		Hashtable<String, String> env = initLdapEnv(userDN, password);
		log.info("authenticate", "Authenticating userDN {" + userDN + " }.");
		long startTime = System.currentTimeMillis();
		DirContext dirContext = new InitialDirContext(env);
		long endTime = System.currentTimeMillis();
		log.info("authenticate", "It took " + (endTime - startTime) + "ms to authenticate userDN { " + userDN + "}.");
		return dirContext;
	}

	/**
	 * Authenticate over LDAP server with the arguments given and return required attributes.
	 *
	 * @param userDN a specified user DN to be authenticated.
	 * @param password the plaintext password associated with the userDN to be authenticated.
	 * @param returnAttrs specifies the attributes that will be returned as part of the search.
	 * @throws NamingException if a naming exception is encountered.
	 * @return attributes found as <tt>returnAttrs</tt> requires.
	 */
	@Override
	public Attribute[] authenticate(String userDN, String password, String[] returnAttrs) throws NamingException {
		// Authenticate.
		DirContext dirContext = authenticate(userDN, password);

		// Search and return required attributes.
		return search(dirContext, userDN, null, SearchControls.SUBTREE_SCOPE, returnAttrs);
	}

	/**
	 * Use LDAP search to find out an unique user DN with the user name specified and then authenticate it at the local side.
	 *
	 * @param dn where the searching engine starts.
	 * @param attrId identifier for the attribute holding <tt>userName</tt> as its unique attribute value.
	 * @param userName user name.
	 * @param userPW plaintext user password.
	 * @throws NamingException if a naming exception is encountered.
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
	 * @return <code>true</code> indicates a successful authentication; <code>false</code> otherwise.
	 */
	/*-
	@Override
	public boolean authenticate(String dn, String attrId, String userName, String userPW) throws NamingException, NoSuchAlgorithmException {
		// Initialize the directory context object if it has not been done yet.
		if (context == null) {
			connect();
		}

		boolean isExist = false;
		String filter = attrId + "=" + userName;
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		log.info("authenticate", "Searching userDNs from " + dn + " with filter " + filter);
		Attribute[] attrs = search(context, dn, filter, SearchControls.SUBTREE_SCOPE, new String[] {"userPassword"});

//		for (NamingEnumeration<SearchResult> results = context.search(dn, filter, constraints); results.hasMore();) {
//			SearchResult result = results.next();
//			String userDN = result.getNameInNamespace();
//			log.info("authenticate", "Found userDN: " + userDN);
//			Attributes attrs = result.getAttributes();
//			Attribute userPassword = attrs.get("userPassword");
//			Object obj = userPassword.get();
//
//			if (obj != null) {
//				String ldapPW;
//
//				if (obj instanceof byte[]) {
//					ldapPW = new String((byte[]) obj);
//				} else if (obj instanceof String) {
//					ldapPW = (String) obj;
//				} else {
//					ldapPW = obj.toString();
//				}
//
//				isExist = verify(ldapPW, userPW);
//
//				if (isExist) {
//					break;
//				}
//			}
//		}

		for (Attribute attr : attrs) {
			Object obj = attr.get();

			if (obj != null) {
				String ldapPW;

				if (obj instanceof byte[]) {
					ldapPW = new String((byte[]) obj);
				} else if (obj instanceof String) {
					ldapPW = (String) obj;
				} else {
					ldapPW = obj.toString();
				}

				isExist = verify(ldapPW, userPW);

				if (isExist) {
					break;
				}
			}
		}

		return isExist;
	}*/

	/**
     * Closes the directory context.
     * This method releases this context's resources immediately, instead of
     * waiting for them to be released automatically by the garbage collector.
	 *
	 * @throws NamingException if a naming exception is encountered
	 */
	@Override
	public synchronized void close() throws NamingException {
		if (context != null) {
			context.close();
		}
	}

	/*-
	public static void main(String[] args) {
		String keystore = "C:\\OpenLDAP\\cacerts\\.keystore";
		System.setProperty("javax.net.ssl.trustStore", keystore);
		String userName = "Yang Chen";
		String password = "";
		String bindDN = "cn=admin,dc=aerohive,dc=com";
		String bindPW = "aerohive";
		int port = 636;
		AhLdapAuthable ldapAuth = new AhLdapAuthImpl("hq-dc-1.aerohive.com");

		try {
			String filter = "Description=GUI Leader";
			String[] returnAttrs = new String[] {"sn", "cn"};
			Attribute[] attrs = ldapAuth.searchAttributes("ou=employees,dc=aerohive,dc=com", filter, returnAttrs);

			for (Attribute attr : attrs) {
				System.out.println(attr.get());
			}

			userName = "Lanbao Xiao";
			password = "LanbaoXiao";
			String userDN = ldapAuth.searchSingleUserDN("ou=employees,dc=aerohive,dc=com", "cn=" + userName);

			if (userDN != null) {
				returnAttrs = new String[] {"mail", "manager"};
				attrs = ldapAuth.authenticate(userDN, password, returnAttrs);
				System.out.println("Got " + attrs.length + " attributes from the authentication for {" + userName + "}.");

				for (Attribute attr : attrs) {
					System.out.println(attr.get());
				}
			} else {
				System.err.println("User {" + userName + "} doesn't exist");
			}

			userDN = "CN=" + userName + ",OU=Users,OU=Corporate,DC=aerohive,DC=com";
			returnAttrs = new String[] {"memberOf", "mail"};
			attrs = ldapAuth.authenticate(userDN, password, returnAttrs);
			System.out.println("Got " + attrs.length + " attributes from the authentication for {" + userName + "}.");

			for (Attribute attr : attrs) {
				for (NamingEnumeration<?> attrValues = attr.getAll(); attrValues.hasMore();) {
					Object value = attrValues.next();

					if (value instanceof byte[]) {
						System.out.println(attr.getID() + " :: " + Base64.encode((byte[])value));
					} else {
						System.out.println(attr.getID() + " : " + value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ldapAuth.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}*/

}