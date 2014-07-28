package com.ah.bo.admin;

import java.io.Serializable;

import javax.persistence.Embeddable;

/*
 * @author Chris Scheers
 * 
 * This is a value type used by HmUserGroup
 */

@Embeddable
@SuppressWarnings("serial")
public class HmPermission implements Serializable {
	private short operations;

	private String label;

	public static final short OPERATION_READ = 1;

	public static final short OPERATION_WRITE = 2;

	public short getOperations() {
		return operations;
	}

	public void setOperations(short operations) {
		this.operations = operations;
	}

	public void addOperation(short operation) {
		this.operations |= operation;
	}

	public boolean hasAccess(int operation) {
		return (this.operations & operation) != 0;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
