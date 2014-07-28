package com.ah.test.ldap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.codec.binary.Base64;

public class AhLdapTest {

	/* init.ldif
		dn: dc=aerohive,dc=com
		objectclass: top
		objectclass: dcObject
		objectclass: organization
		o: Guessant
		dc: aerohive

		dn: cn=admin,dc=aerohive,dc=com
		objectclass: organizationalRole
		cn: admin
	 */

	/* aerohive_china.ldif
		# The origination of Aerohive China
		#
		version: 1

		dn: ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: organizationalUnit
		description: Contains entries which describe overall employees (seamen)
		ou: employees

		dn: ou=groups,dc=aerohive,dc=com
		objectclass: top
		objectclass: organizationalUnit
		description: Contains entries which describe overall groups (Engineers & Administrators)
		ou: groups

		dn: ou=engineers,ou=groups,dc=aerohive,dc=com
		objectclass: top
		objectclass: organizationalUnit
		description: Contains entries which describe overall engineer groups (Device, NMS, QA)
		ou: engineers

		#    Device
		# ------------

		dn: cn=Peter Wu,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: PeterWu
		Description: Device Manager
		sn: Wu
		givenname: Peter
		mail: pwu@aerohive.com
		uid: No.6(US)
		cn: Peter Wu

		dn: cn=Hai Lin,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: HaiLin
		Description: Lin Zong
		sn: Lin
		givenname: Hai
		mail: hlin@aerohive.com
		manager: cn=Peter Wu,ou=employees,dc=aerohive,dc=com
		uid: No.2
		cn: Hai Lin

		dn: cn=Yongqiang Zuo,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: YongqiangZuo
		Description: Jerry
		sn: Zuo
		givenname: Yongqiang
		mail: yzuo@aerohive.com
		manager: cn=Peter Wu,ou=employees,dc=aerohive,dc=com
		uid: No.4
		cn: Yongqiang Zuo

		dn: cn=Lei Zhang,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: LeiZhang
		Description: WebUI Leader
		sn: Zhang
		givenname: Lei
		mail: lzhang@aerohive.com
		manager: cn=Peter Wu,ou=employees,dc=aerohive,dc=com
		uid: No.7
		cn: Lei Zhang

		dn: cn=Device,ou=engineers,ou=groups,dc=aerohive,dc=com
		objectclass: top
		objectclass: groupOfUniqueNames
		uniquemember: cn=Peter Wu,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Hai Li,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Yongqiang Zuo,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Lei Zhang,ou=employees,dc=aerohive,dc=com
		cn: Device

		#     NMS
		# ------------

		dn: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: XiaohaiKong
		Description: NMS Manager
		sn: Kong
		givenname: Xiaohai
		mail: skong@aerohive.com
		uid: No.10
		cn: Xiaohai Kong

		dn: cn=Yang Chen,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: YangChen
		Description: From Liaoning province
		sn: Chen
		givenname: Yang
		mail: ychen@aerohive.com
		manager: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		uid: No.3
		cn: Yang Chen

		dn: cn=Minfei Jin,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: MinfeiJin
		Description: GUI Leader
		sn: Jin
		givenname: Minfei
		mail: mjin@aerohive.com
		manager: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		uid: No.6
		cn: Minfei Jin

		dn: cn=Xiaorong Feng,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: XiaorongFeng
		Description: GUI Desinger
		sn: Feng
		givenname: Xiaorong
		mail: xfeng@aerohive.com
		manager: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		uid: No.13
		cn: Xiaorong Feng

		dn: cn=NMS,ou=engineers,ou=groups,dc=aerohive,dc=com
		objectclass: top
		objectclass: groupOfUniqueNames
		uniquemember: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Yang Chen,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Minfei Jin,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Xiaorong Feng,ou=employees,dc=aerohive,dc=com
		cn: NMS

		#     QA
		# ------------

		dn: cn=Jingbo Ni,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: JingboNi
		Description: QA Manager
		sn: Ni
		givenname: Jingbo
		mail: jni@aerohive.com
		uid: No.5(US)
		cn: Jingbo Ni

		dn: cn=Jing Li,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: JingLi
		Description: QA Leader
		sn: Li
		givenname: Jing
		mail: jli@aerohive.com
		manager: cn=Jingbo Ni,ou=employees,dc=aerohive,dc=com
		uid: No.7
		cn: Jing Li

		dn: cn=Yun Feng,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: YunFeng
		Description: QA Leader
		sn: Feng
		givenname: Yun
		mail: yfeng@aerohive.com
		manager: cn=Jingbo Ni,ou=employees,dc=aerohive,dc=com
		uid: No.8
		cn: Yun Feng

		dn: cn=Tiesong Wang,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: TiesongWang
		Description: Staff Engineer
		sn: Wang
		givenname: Tiesong
		mail: twang@aerohive.com
		manager: cn=Jingbo Ni,ou=employees,dc=aerohive,dc=com
		uid: No.9
		cn: Tiesong Wang

		dn: cn=QA,ou=engineers,ou=groups,dc=aerohive,dc=com
		objectclass: top
		objectclass: groupOfUniqueNames
		uniquemember: cn=Jingbo Ni,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Jing Li,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Yun Feng,ou=employees,dc=aerohive,dc=com
		uniquemember: cn=Tiesong Wang,ou=employees,dc=aerohive,dc=com
		cn: QA
	 */

	static byte[] salt = new byte[0];

	/**
	 * Encrypt user password for LDAP.
	 *
	 * @param method the name of algorithm to encrypt.
	 * @param password plaintext of user password.
	 * @param salt the initial random plaintext participated in the encryption.
	 * @return A cryptography string formatted as {algorithm} + Base64.encrypt(digest + salt) 
	 * @throws NoSuchAlgorithmException if no Provider supports the implementation for the specified algorithm.
	 */
	public static String encrypt(String method, String password, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest md;
		method = method.toUpperCase();

		// Initialize MessageDigest instance by method name.
		if (method.equals("{SSHA}")) {
			md = MessageDigest.getInstance("SHA-1");
		} else if (method.equals("{SHA}")) {
			md = MessageDigest.getInstance("SHA-1");
		} else if (method.equals("MD5")) {
			md = MessageDigest.getInstance("MD5");
		} else if (method.equals("{SMD5}")) {
			md = MessageDigest.getInstance("MD5");
		} else {
			throw new NoSuchAlgorithmException("Unknown or unsupported algorithem given - " + method);
		}

		// The beginning 20 bytes is the encryption component and the left bytes is the salt.
		if (salt == null) {
			salt = new byte[0];
		}

		// Append the input password into the MessageDigest instance.
		md.update(password.getBytes());

		// Append the salt into the MessageDigest instace.
		md.update(salt);

		byte[] digest = md.digest();
		byte[] encryptedComponent = digest;

		if (salt.length > 0) {
			encryptedComponent = new byte[digest.length + salt.length];
			System.arraycopy(digest, 0, encryptedComponent, 0, digest.length);
			System.arraycopy(salt, 0, encryptedComponent, digest.length, salt.length);
		}

		// Return formatted LDAP password.
		return method + new String(Base64.encodeBase64(encryptedComponent));
	//	return method + Base64.encode(encryptedComponent);
	}

	public static boolean verify(String ldapPwd, String inputPwd) throws NoSuchAlgorithmException {
		MessageDigest md;
		int len;

		// Initialize MessageDigest instance by method name.
		if (ldapPwd.startsWith("{SSHA}")) {
			ldapPwd = ldapPwd.substring(6);
			md = MessageDigest.getInstance("SHA-1");
			len = 20;
		} else if (ldapPwd.startsWith("{SHA}")) {
			ldapPwd = ldapPwd.substring(5);
			md = MessageDigest.getInstance("SHA-1");
			len = 20;
		} else if (ldapPwd.startsWith("MD5")) {
			ldapPwd = ldapPwd.substring(5);
			md = MessageDigest.getInstance("MD5");
			len = 20;
		} else if (ldapPwd.startsWith("{SMD5}")) {
			ldapPwd = ldapPwd.substring(6);
			md = MessageDigest.getInstance("MD5");
			len = 20;
		} else {
			throw new NoSuchAlgorithmException("Invalid LDAP password format - " + ldapPwd);
		}

		// Decode {digest + salt} with Base64.
//		byte[] digestAndSalt = Base64.decode(ldapPwd);
		byte[] digestAndSalt = Base64.decodeBase64(ldapPwd.getBytes());
		byte[] shacode;
		byte[] salt;

		// The beginning 20 bytes is the encryption component and the left bytes is the salt.
		if (digestAndSalt.length <= len) {
			shacode = digestAndSalt;
			salt = new byte[0];
		} else {
			shacode = new byte[len];
			salt = new byte[digestAndSalt.length - len];
			System.arraycopy(digestAndSalt, 0, shacode, 0, len);
			System.arraycopy(digestAndSalt, len, salt, 0, salt.length);
		}

		// Append the input password into the MessageDigest instance.
		md.update(inputPwd.getBytes());

		// Append the salt into the MessageDigest instace.
		md.update(salt);

		byte[] digest = md.digest();

		// Return verfication result.
		return MessageDigest.isEqual(shacode, digest);
	}

	/*
		dn: cn=Lanbao Xiao,ou=employees,dc=aerohive,dc=com
		objectclass: top
		objectclass: person
		objectclass: organizationalPerson
		objectclass: inetOrgPerson
		userpassword: LanbaoXiao
		Description: NMS native feature leader
		sn: Xiao
		givenname: Lanbao
		mail: lxiao@aerohive.com
		manager: cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com
		uid: No.16
		cn: Lanbao Xiao
	 */
	public static void addUser(DirContext context, String dn) throws NamingException, NoSuchAlgorithmException {
		Attributes attrs = new BasicAttributes();
	//	attrs.put("dn", "cn=Lanbao Xiao,ou=employees,dc=aerohive,dc=com");

		// The following attribute has multiple values.
		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("person");
		objectClass.add("organizationalPerson");
		objectClass.add("inetOrgPerson");
		attrs.put(objectClass);

		// The following attributes have single value.
		String password = encrypt("{SHA}", "LanbaoXiao", salt);
		System.out.println("Password after encryption: " + password);
		attrs.put("userpassword", password);
		attrs.put("Description", "NMS native feature leader");
		attrs.put("sn", "Xiao");
		attrs.put("givenname", "Lanbao");
		attrs.put("mail", "lxiao@aerohive.com");
		attrs.put("manager", "cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com");
		attrs.put("uid", "No.16");
		attrs.put("cn", "Lanbao Xiao");

		// Both the bind and createSubcontext ways are the same. 
	//	context.bind(dn, null, attrs);
		context.createSubcontext(dn, attrs);
		System.out.println("New user {Lanbao Xiao} was successfully added.");
	}

	public static boolean checkUser(DirContext context, String filter, String password, String dn) throws NamingException, NoSuchAlgorithmException {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		boolean isExist = false;

		for (NamingEnumeration<SearchResult> results = context.search(dn, filter, constraints); results.hasMoreElements();) {
			SearchResult result = results.nextElement();
			System.out.println("dn: " + result.getNameInNamespace());

			Attributes attrs = result.getAttributes();
			Attribute attr = attrs.get("userPassword");
			Object obj = attr.get();
			byte[] pwdBytes = (byte[]) obj;
			String ldapPwd = new String(pwdBytes);
			isExist = verify(ldapPwd, password);

			if (isExist) {
				break;
			}
		}

		return isExist;
	}

	public static void showUsers(DirContext context, String filter, String dn) throws NamingException, NoSuchAlgorithmException {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

		for (NamingEnumeration<SearchResult> results = context.search(dn, filter, constraints); results.hasMoreElements();) {
			SearchResult result = results.nextElement();
			System.out.println("dn: " + result.getNameInNamespace());

			Attributes attrs = result.getAttributes();

			for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
				Attribute attr = attrEnum.next();
//				int size = attr.size();
//
//				if (size == 1) {
//					Object obj = attr.get();
//
//					if (obj != null) {
//						if (obj instanceof byte[]) {
//							System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//						} else {
//							System.out.println(attr.getID() + " : " + obj);
//						}
//					}
//				} else {
//				   for (int i = 0; i < size; i++) {
//					   Object obj = attr.get(i);
//
//					   if (obj != null) {
//							if (obj instanceof byte[]) {
//								System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//							} else {
//								System.out.println(attr.getID() + " : " + obj);
//							}
//					   }
//				   }
//				}

				for (NamingEnumeration<?> objEnum = attr.getAll(); objEnum.hasMore();) {
					Object obj = objEnum.next();

					if (obj != null) {
						if (obj instanceof byte[]) {
						//	System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
							System.out.println(attr.getID() + " :: " + new String (Base64.encodeBase64((byte[])obj)));
						} else {
							System.out.println(attr.getID() + " : " + obj);
						}
					}
				}
			}
		}
	}

	public static void showProperties(DirContext context, String dn) throws NamingException, NoSuchAlgorithmException {
		Attributes attrs = context.getAttributes(dn);

		System.out.println("dn: " + dn);

		for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
			Attribute attr = attrEnum.next();
//			int size = attr.size();
//
//			if (size == 1) {
//				Object obj = attr.get();
//
//				if (obj != null) {
//					if (obj instanceof byte[]) {
//						System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//					} else {
//						System.out.println(attr.getID() + " : " + obj);
//					}
//				}
//			} else {
//			   for (int i = 0; i < size; i++) {
//				   Object obj = attr.get(i);
//
//				   if (obj != null) {
//						if (obj instanceof byte[]) {
//							System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//						} else {
//							System.out.println(attr.getID() + " : " + obj);
//						}
//				   }
//			   }
//			}

			for (NamingEnumeration<?> objEnum = attr.getAll(); objEnum.hasMore();) {
				Object obj = objEnum.next();

				if (obj != null) {
					if (obj instanceof byte[]) {
					//	System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
						System.out.println(attr.getID() + " :: " + new String(Base64.encodeBase64((byte[])obj)));
					} else {
						System.out.println(attr.getID() + " : " + obj);
					}
				}
			}
		}
	}

	public static void showProperties(DirContext context, String dn, String[] properties) throws NamingException, NoSuchAlgorithmException {
		Attributes attrs = context.getAttributes(dn, properties);

		System.out.println("dn: " + dn);

		for (NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll(); attrEnum.hasMore();) {
			Attribute attr = attrEnum.next();
//			int size = attr.size();
//
//			if (size == 1) {
//				Object obj = attr.get();
//
//				if (obj != null) {
//					if (obj instanceof byte[]) {
//						System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//					} else {
//						System.out.println(attr.getID() + " : " + obj);
//					}
//				}
//			} else {
//			   for (int i = 0; i < size; i++) {
//				   Object obj = attr.get(i);
//
//				   if (obj != null) {
//						if (obj instanceof byte[]) {
//							System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
//						} else {
//							System.out.println(attr.getID() + " : " + obj);
//						}
//				   }
//			   }
//			}

			for (NamingEnumeration<?> objEnum = attr.getAll(); objEnum.hasMore();) {
				Object obj = objEnum.next();

				if (obj != null) {
					if (obj instanceof byte[]) {
					//	System.out.println(attr.getID() + " :: " + Base64.encode((byte[])obj));
						System.out.println(attr.getID() + " :: " + new String(Base64.encodeBase64((byte[])obj)));
					} else {
						System.out.println(attr.getID() + " : " + obj);
					}
				}
			}
		}
	}

	public static void modifyUserPassword(DirContext context, String dn, String newPassword) throws NamingException, NoSuchAlgorithmException {
		ModificationItem[] modificationItems = new ModificationItem[1];
		String encryptedPassword = encrypt("{SHA}", newPassword, salt);
		System.out.println("Password after encryption: " + encryptedPassword);
		modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", encryptedPassword));
		context.modifyAttributes(dn, modificationItems);
		System.out.println("Password was successfully changed to " + encryptedPassword);
	}

	public static void deleteUser(DirContext context, String dn) throws NamingException {
		context.destroySubcontext(dn);
		System.out.println("User {" + dn + "} was successfully deleted.");
	}

	public static void main(String[] args) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost/");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=aerohive,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "secret");

		DirContext ctx = null;

		try {
			ctx = new InitialDirContext(env);
			System.out.println("Authentication Success");

			// Add new user
		//	addUser(ctx, "cn=Lanbao Xiao,ou=employees,dc=aerohive,dc=com");

			// Check user
		//	boolean isExist = checkUser(ctx, "cn=Lanbao Xiao", "LanbaoXiao", "ou=employees,dc=aerohive,dc=com");
		//	System.out.println("Lanbao Xiao " + (isExist ? "has existed." : "has not existed."));

			// Modify password
		//	modifyUserPassword(ctx, "cn=Lanbao Xiao,ou=employees,dc=aerohive,dc=com", "LanbaoXiao");

			// Delete user
		//	deleteUser(ctx, "cn=Lanbao Xiao,ou=employees,dc=aerohive,dc=com");

			// Show users
		//	showUsers(ctx, "cn=*", "ou=employees,dc=aerohive,dc=com");

			// Show properties
		//	showProperties(ctx, "ou=groups,dc=aerohive,dc=com");
		//	showProperties(ctx, "cn=Yang Chen,ou=employees,dc=aerohive,dc=com", new String[] {"sn", "givenName"});
		} catch (AuthenticationException ae) {
			System.err.println("Authentication Failure");
			ae.printStackTrace();
		} catch (NamingException ne) {
			System.err.println("Authentication Error");
			ne.printStackTrace();
	//	} catch (NoSuchAlgorithmException nsae) {
	//		System.err.println("Algorithm is not supported");
	//		nsae.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ne) {
					System.err.println("DirContext Close Error");
					ne.printStackTrace();
				}
			}
		}
	}

}