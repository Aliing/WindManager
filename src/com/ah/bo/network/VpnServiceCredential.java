package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

@Embeddable
public class VpnServiceCredential implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final short SERVER_ROLE_NONE = 0;
	public static final short SERVER_ROLE_SERVER1 = 1;
	public static final short SERVER_ROLE_SERVER2 = 2;
	
	public static final short ALLOCATED_STATUS_FREE = 0;
	public static final short ALLOCATED_STATUS_USED = 1;
	public static final short ALLOCATED_STATUS_PRE_USE = 2;
	public static final short ALLOCATED_STATUS_PRE_REMOVE = 3;

	@Column(nullable = false)
	private String credential;

	@Column(nullable = false)
	private String clientName;
	
	private short allocatedStatus = ALLOCATED_STATUS_FREE;

	@Index(name="assignedClient")
	private String assignedClient;

	private short primaryRole;

	private short backupRole;

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public short getAllocatedStatus() {
		return allocatedStatus;
	}

	public void setAllocatedStatus(short allocatedStatus) {
		this.allocatedStatus = allocatedStatus;
	}
	
	@Transient
	public boolean isFree(){
		return allocatedStatus == ALLOCATED_STATUS_FREE;
	}
	
	@Transient
	public boolean isAllocated(){
		return allocatedStatus == ALLOCATED_STATUS_USED;
	}
	
	@Transient
	public void setAllocated(boolean allocated){
		if(allocated){
			allocatedStatus = ALLOCATED_STATUS_USED;
		}else{
			allocatedStatus = ALLOCATED_STATUS_FREE;
		}
	}

	public String getAssignedClient() {
		return assignedClient;
	}

	public void setAssignedClient(String assignedClient) {
		this.assignedClient = assignedClient;
	}

	public short getPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(short primaryRole) {
		this.primaryRole = primaryRole;
	}

	public short getBackupRole() {
		return backupRole;
	}

	public void setBackupRole(short backupRole) {
		this.backupRole = backupRole;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof VpnServiceCredential) {
			VpnServiceCredential o = (VpnServiceCredential) other;
			if (clientName.equals(o.getClientName())
					&& credential.equals(o.getCredential())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (clientName + credential).hashCode();
	}

	@Transient
	private String hostname = "&nbsp;";// avoid blank in ie;

	public String getHostname() {
		if (null != assignedClient && !"".equals(assignedClient)) {
			HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", assignedClient);
			if (null != hiveAp) {
				hostname = hiveAp.getHostName();
			}
		}
		return hostname;
	}

	@Transient
	public String getPrimaryRoleString() {
		switch (primaryRole) {
		case SERVER_ROLE_SERVER1:
			return "Server 1";
		case SERVER_ROLE_SERVER2:
			return "Server 2";
		default:
			return "&nbsp";
		}
	}

	@Transient
	public String getBackupRoleString() {
		switch (backupRole) {
		case SERVER_ROLE_SERVER1:
			return "Server 1";
		case SERVER_ROLE_SERVER2:
			return "Server 2";
		default:
			return "&nbsp";
		}
	}

}