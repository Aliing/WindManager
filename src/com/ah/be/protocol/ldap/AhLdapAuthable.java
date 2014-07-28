package com.ah.be.protocol.ldap;

//import java.security.NoSuchAlgorithmException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;

public interface AhLdapAuthable {

	enum AuthLevel {
		NONE, SIMPLE, STRONG
	}

	void connect(String principal, String credentials) throws NamingException;

	void connect() throws NamingException;

	Attribute[] searchAttributes(String dn, String filter, String[] returnAttrs) throws NamingException;

	String[] searchUserDN(String dn, String filter) throws NamingException;

	String searchSingleUserDN(String dn, String filter) throws NamingException;

	DirContext authenticate(String userDN, String password) throws NamingException;

	Attribute[] authenticate(String userDN, String password, String[] returnAttrs) throws NamingException;

//	boolean authenticate(String dn, String attrId, String userName, String userPW) throws NamingException, NoSuchAlgorithmException;

	void close() throws NamingException;

}