package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.network.IpAddress;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class MgmtServiceSnmpInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String oldDefaultCommunity = "public";
	
	public static String newDefaultCommunity = "hivecommunity";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MGMT_SERVICE_IP_ADDRESS_ID", nullable = true)
	private IpAddress ipAddress;
	
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String community;
	
	public static final short MGMTSNMP_VERSION_V1 = 0;

	public static final short MGMTSNMP_VERSION_V2C = 1;
	
	public static final short MGMTSNMP_VERSION_V3 = 2;

	public static EnumItem[] ENUM_MGMTSNMP_VERSION = MgrUtil.enumItems(
			"enum.mgmt.snmp.version.", new int[] { 
					MGMTSNMP_VERSION_V1, MGMTSNMP_VERSION_V2C,MGMTSNMP_VERSION_V3});
	private short snmpVersion = MGMTSNMP_VERSION_V2C;
	
	public static final short MGMTSNMP_OPERATION_NONE = 0;

	public static final short MGMTSNMP_OPERATION_GET = 1;
	
	public static final short MGMTSNMP_OPERATION_GETANDTRAP = 2;
	
	public static final short MGMTSNMP_OPERATION_TRAP = 3;

	public static EnumItem[] ENUM_MGMTSNMP_OPERATION = MgrUtil.enumItems(
			"enum.mgmt.snmp.operation.", new int[] { MGMTSNMP_OPERATION_NONE,
				MGMTSNMP_OPERATION_GET, MGMTSNMP_OPERATION_GETANDTRAP, MGMTSNMP_OPERATION_TRAP });
	private short snmpOperation = MGMTSNMP_OPERATION_GETANDTRAP;
	
	public static final short PASS_AUTH_NONE=-1;
	public static final short PASS_AUTH_MD5=0;
	public static final short PASS_AUTH_SHA=1;
	
	public static final short PASS_ENCRYPTION_NONE=-1;
	public static final short PASS_ENCRYPTION_DES=0;
	public static final short PASS_ENCRYPTION_AES=1;
	
	public static EnumItem[] ENUM_PASSWORD_AUTH_METHOD = MgrUtil.enumItems(
			"enum.mgmt.snmp.auth.method.", new int[] { PASS_AUTH_MD5, PASS_AUTH_SHA});
	public static EnumItem[] ENUM_PASSWORD_ENCRY_METHOD = MgrUtil.enumItems(
			"enum.mgmt.snmp.encryption.method.", new int[] {PASS_ENCRYPTION_DES, PASS_ENCRYPTION_AES});
	
	private short authPassMethod=PASS_AUTH_NONE;
	private short encryPassMethod=PASS_ENCRYPTION_NONE;
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String userName;
	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String authPass;
	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String encryPass;
	
	@Transient
    public String[] getFieldValues(){
    	String[] fieldValues={"MGMT_SERVICE_SNMP_ID","snmpOperation","MGMT_SERVICE_IP_ADDRESS_ID","type",
    			"communityString","community","snmpVersion",
    			"serverName","userName","authPassMethod","authPass",
    			"encryPassMethod","encryPass"};
    	return fieldValues;
    }
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	
	public short getSnmpOperation() {
		return snmpOperation;
	}
	public void setSnmpOperation(short snmpOperation) {
		this.snmpOperation = snmpOperation;
	}
	public short getSnmpVersion() {
		return snmpVersion;
	}
	public void setSnmpVersion(short snmpVersion) {
		this.snmpVersion = snmpVersion;
	}
	public IpAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Transient
	public String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}
	
	@Transient
	public String getServerName() {
		if(this.ipAddress != null) {
			return this.ipAddress.getAddressName();
		} else {
			return "";
		}
	}
	public short getAuthPassMethod() {
		return authPassMethod;
	}
	public short getEncryPassMethod() {
		return encryPassMethod;
	}
	public String getUserName() {
		return userName;
	}
	public String getAuthPass() {
		return authPass;
	}
	public String getEncryPass() {
		return encryPass;
	}
	public void setAuthPassMethod(short authPassMethod) {
		this.authPassMethod = authPassMethod;
	}
	public void setEncryPassMethod(short encryPassMethod) {
		this.encryPassMethod = encryPassMethod;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setAuthPass(String authPass) {
		this.authPass = authPass;
	}
	public void setEncryPass(String encryPass) {
		this.encryPass = encryPass;
	}
	
	public boolean getReadOnlyV1V2(){
		if (snmpVersion==MGMTSNMP_VERSION_V3){
			return true;
		}
		return false;
	}
	
	public boolean getReadOnlyV3(){
		if (snmpVersion==MGMTSNMP_VERSION_V3){
			return false;
		}
		return true;
	}

}