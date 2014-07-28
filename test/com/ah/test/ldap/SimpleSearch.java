package com.ah.test.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class SimpleSearch {

	public static void main(String[] args) throws NamingException {
		// JNDI connection data, move them to jndi.properties
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost:389/");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=aerohive,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "secret");

		try {
			DirContext ctx = new InitialDirContext(env);

			String base = "dc=aerohive,dc=com";
			String filter = "(&(objectClass=person)(manager=cn=Xiaohai Kong,ou=employees,dc=aerohive,dc=com))";

			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(new String[] { "cn", "mail" });

			NamingEnumeration<SearchResult> resultEnum = ctx.search(base, filter, ctls);

			while (resultEnum.hasMore()) {
				SearchResult result = resultEnum.next();

				// print DN of entry
				System.out.println(result.getNameInNamespace());
				System.out.println("getClassName: " + result.getClassName());
				System.out.println("getName: " + result.getName());
				System.out.println("getObject: " + result.getObject());

				// print attributes returned by search
				Attributes attrs = result.getAttributes();
				NamingEnumeration<? extends Attribute> e = attrs.getAll();

				while (e.hasMore()) {
					Attribute attr = e.next();
					System.out.println(attr);
				}

				System.out.println();
			}

//			Attributes attrs = ctx.getAttributes("dc=aerohive,dc=com");
//
//			NamingEnumeration e = attrs.getAll();
//
//			while (e.hasMore()) {
//				Attribute attr = (Attribute) e.next();
//				System.err.println(attr);
//			}

			ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

}