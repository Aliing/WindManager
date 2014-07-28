package com.ah.ws.rest.server.auth.perms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;

public final class MethodPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	private transient int mask;

	private final static int NONE = 0x0;
	private final static int GET = 0x1;
	private final static int POST = 0x2;
	private final static int PUT = 0x4;
	private final static int DELETE = 0x8;
	private final static int ALL = GET | POST | PUT | DELETE;

	private String actions;

	private void init(int mask) {

		if ((mask & ALL) != mask)
			throw new IllegalArgumentException("invalid actions mask");

		if (mask == NONE)
			throw new IllegalArgumentException("invalid actions mask");

		if (getName() == null)
			throw new NullPointerException("name can't be null");

		this.mask = mask;
	}

	private static int getMask(String actions) {

		int mask = NONE;

		if (actions == null) {
			return mask;
		}

		// Check against use of constants
		if (HttpMethod.GET.equalsIgnoreCase(actions)) {
			return GET;
		} else if (HttpMethod.POST.equalsIgnoreCase(actions)) {
			return POST;
		} else if (HttpMethod.PUT.equalsIgnoreCase(actions)) {
			return PUT;
		} else if (HttpMethod.DELETE.equalsIgnoreCase(actions)) {
			return DELETE;
		} else if ("ALL".equalsIgnoreCase(actions)) {
			return ALL;
		}

		StringTokenizer st = new StringTokenizer(actions, ",");
		while (st.hasMoreTokens()) {
			String method = st.nextToken();
			if (HttpMethod.GET.equalsIgnoreCase(method)) {
				mask |= GET;
			} else if (HttpMethod.POST.equalsIgnoreCase(method)) {
				mask |= POST;
			} else if (HttpMethod.PUT.equalsIgnoreCase(method)) {
				mask |= PUT;
			} else if (HttpMethod.DELETE.equalsIgnoreCase(method)) {
				mask |= DELETE;
			} else if ("ALL".equalsIgnoreCase(method)) {
				mask |= ALL;
			}
		}
		return mask;
	}

	public MethodPermission(String name) {
		super(name);
		init(getMask("ALL"));
	}

	// note that actions is ignored and not used,
	// but this constructor is still needed
	public MethodPermission(String name, String actions) {
		super(name, actions);
		init(getMask(actions));
	}

	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof MethodPermission))
			return false;

		MethodPermission that = (MethodPermission) p;

		// we get the effective mask. i.e., the "and" of this and that.
		// They must be equal to that.mask for implies to return true.

		return ((this.mask & that.mask) == that.mask) && super.implies(that);
	}

	@Override
	public String getActions() {
		if (actions == null)
			actions = getActions(this.mask);

		return actions;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof MethodPermission))
			return false;

		MethodPermission that = (MethodPermission) obj;

		return (this.mask == that.mask)
				&& (this.getName().equals(that.getName()));
	}

	static String getActions(int mask) {
		StringBuilder sb = new StringBuilder();
		boolean comma = false;

		if ((mask & GET) == GET) {
			comma = true;
			sb.append("GET");
		}

		if ((mask & POST) == POST) {
			if (comma)
				sb.append(',');
			else
				comma = true;
			sb.append("POST");
		}
		if ((mask & PUT) == PUT) {
			if (comma)
				sb.append(',');
			else
				comma = true;
			sb.append("PUT");
		}
		if ((mask & DELETE) == DELETE) {
			if (comma)
				sb.append(',');
			else
				comma = true;
			sb.append("DELETE");
		}
		return sb.toString();
	}

	int getMask() {
		return mask;
	}

	@Override
	public PermissionCollection newPermissionCollection() {
		return new MethodPermissionCollection();
	}
}

final class MethodPermissionCollection extends PermissionCollection implements
		Serializable {

	/**
	 * Key is property name; value is MethodPermission. Not serialized; see
	 * serialization section at end of class.
	 */
	private transient Map<String, Permission> perms;

	/**
	 * Boolean saying if "*" is in the collection.
	 *
	 * @see #serialPersistentFields
	 */
	// No sync access; OK for this to be stale.
	private boolean all_allowed;

	/**
	 * Create an empty MethodPermissions object.
	 *
	 */

	public MethodPermissionCollection() {
		perms = new HashMap<String, Permission>(32); // Capacity for default
														// policy
		all_allowed = false;
	}

	/**
	 * Adds a permission to the MethodPermissions. The key for the hash is the
	 * name.
	 *
	 * @param permission
	 *            the Permission object to add.
	 *
	 * @exception IllegalArgumentException
	 *                - if the permission is not a MethodPermission
	 *
	 * @exception SecurityException
	 *                - if this MethodPermissionCollection object has been
	 *                marked readonly
	 */

	public void add(Permission permission) {
		if (!(permission instanceof MethodPermission))
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		if (isReadOnly())
			throw new SecurityException(
					"attempt to add a Permission to a readonly PermissionCollection");

		MethodPermission pp = (MethodPermission) permission;
		String propName = pp.getName();

		synchronized (this) {
			MethodPermission existing = (MethodPermission) perms.get(propName);

			if (existing != null) {
				int oldMask = existing.getMask();
				int newMask = pp.getMask();
				if (oldMask != newMask) {
					int effective = oldMask | newMask;
					String actions = MethodPermission.getActions(effective);
					perms.put(propName, new MethodPermission(propName, actions));
				}
			} else {
				perms.put(propName, permission);
			}
		}

		if (!all_allowed) {
			if (propName.equals("*"))
				all_allowed = true;
		}
	}

	/**
	 * Check and see if this set of permissions implies the permissions
	 * expressed in "permission".
	 *
	 * @param p
	 *            the Permission object to compare
	 *
	 * @return true if "permission" is a proper subset of a permission in the
	 *         set, false if not.
	 */

	public boolean implies(Permission permission) {
		if (!(permission instanceof MethodPermission))
			return false;

		MethodPermission pp = (MethodPermission) permission;
		MethodPermission x;

		int desired = pp.getMask();
		int effective = 0;

		// short circuit if the "*" Permission was added
		if (all_allowed) {
			synchronized (this) {
				x = (MethodPermission) perms.get("*");
			}
			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return true;
			}
		}

		// strategy:
		// Check for full match first. Then work our way up the
		// name looking for matches on a.b.*

		String name = pp.getName();
		// System.out.println("check "+name);

		synchronized (this) {
			x = (MethodPermission) perms.get(name);

			if (x == null) {
				for (String key : perms.keySet()) {
					if (key.contains("*")) {
						String regEx = key.replace("*", ".*");
						if (Pattern.matches(regEx, name)) {
							x = (MethodPermission) perms.get(key);
							break;
						}
					}
				}
			}
		}

		if (x != null) {
			// we have a direct hit!
			effective |= x.getMask();
			if ((effective & desired) == desired)
				return true;
		}

		// work our way up the tree...
		int last, offset;

		offset = name.length() - 1;

		while ((last = name.lastIndexOf(".", offset)) != -1) {

			name = name.substring(0, last + 1) + "*";
			// System.out.println("check "+name);
			synchronized (this) {
				x = (MethodPermission) perms.get(name);
			}

			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return true;
			}
			offset = last - 1;
		}

		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}

	/**
	 * Returns an enumeration of all the MethodPermission objects in the
	 * container.
	 *
	 * @return an enumeration of all the MethodPermission objects.
	 */

	public Enumeration<Permission> elements() {
		// Convert Iterator of Map values into an Enumeration
		synchronized (this) {
			return Collections.enumeration(perms.values());
		}
	}

	private static final long serialVersionUID = 7015263904581634791L;

	// Need to maintain serialization interoperability with earlier releases,
	// which had the serializable field:
	//
	// Table of permissions.
	//
	// @serial
	//
	// private Hashtable permissions;
	/**
	 * @serialField
	 *                  permissions java.util.Hashtable A table of the
	 *                  MethodPermissions.
	 * @serialField
	 *                  all_allowed boolean boolean saying if "*" is in the
	 *                  collection.
	 */
	private static final ObjectStreamField[] serialPersistentFields = {
			new ObjectStreamField("permissions", Hashtable.class),
			new ObjectStreamField("all_allowed", Boolean.TYPE), };

	/**
	 * @serialData Default fields.
	 */
	/*
	 * Writes the contents of the perms field out as a Hashtable for
	 * serialization compatibility with earlier releases. all_allowed unchanged.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// Don't call out.defaultWriteObject()

		// Copy perms into a Hashtable
		Hashtable<String, Permission> permissions = new Hashtable<String, Permission>(
				perms.size() * 2);
		synchronized (this) {
			permissions.putAll(perms);
		}

		// Write out serializable fields
		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("all_allowed", all_allowed);
		pfields.put("permissions", permissions);
		out.writeFields();
	}

	/*
	 * Reads in a Hashtable of MethodPermissions and saves them in the perms
	 * field. Reads in all_allowed.
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// Don't call defaultReadObject()

		// Read in serialized fields
		ObjectInputStream.GetField gfields = in.readFields();

		// Get all_allowed
		all_allowed = gfields.get("all_allowed", false);

		// Get permissions
		@SuppressWarnings("unchecked")
		Hashtable<String, Permission> permissions = (Hashtable<String, Permission>) gfields
				.get("permissions", null);
		perms = new HashMap<String, Permission>(permissions.size() * 2);
		perms.putAll(permissions);
	}
}
