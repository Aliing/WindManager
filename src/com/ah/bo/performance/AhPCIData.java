package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_PCIDATA")
@org.hibernate.annotations.Table(appliesTo = "HM_PCIDATA", indexes = {
		@Index(name = "PCI_DATA_OWNER", columnNames = { "OWNER" }),
		@Index(name = "PCI_DATA_REPORTTIME", columnNames = { "reportTime" })
		})
public class AhPCIData implements HmBo {

	private static final long serialVersionUID = 1L;

	/**
	 * alert code definition.
	 */
	public static final int	ALERT_CODE_PROBE_REQUEST			= 0;
	public static final int	ALERT_CODE_PROBE_RESPONSE			= 1;
	public static final int	ALERT_CODE_ASSOC_REQUEST			= 2;
	public static final int	ALERT_CODE_ASSOC_RESPONSE			= 3;
	public static final int	ALERT_CODE_AUTH						= 4;
	public static final int	ALERT_CODE_DEAUTH					= 5;
	public static final int	ALERT_CODE_DEASSOC					= 6;
	public static final int	ALERT_CODE_EAPOL					= 7;
	public static final int	ALERT_CODE_ICMP_FLOOD				= 8;
	public static final int	ALERT_CODE_UDP_FLOOD				= 9;
	public static final int	ALERT_CODE_SYN_FLOOD				= 10;
	public static final int	ALERT_CODE_ARP_FLOOD				= 11;
	public static final int	ALERT_CODE_ADDRESS_SWEEP			= 12;
	public static final int	ALERT_CODE_PORT_SCAN				= 13;
	public static final int	ALERT_CODE_IP_SPOOF					= 14;
	public static final int	ALERT_CODE_RADIUS_ATTACK			= 15;
	public static final int	ALERT_CODE_TCP_SYN_CHECK			= 16;
	public static final int	ALERT_CODE_IP_FIREWALL_VIOLATION	= 17;
	public static final int	ALERT_CODE_MAC_FIREWALL_VIOLATION	= 18;
	public static final int	ALERT_CODE_MAC_FILTER_VIOLATION		= 19;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	private String			nodeID;

	private short			alertCode;

	private long			violationCounter;

	private String			srcObject;

	private String			destObject;

	private String			reportSystem;

	private Long			mapID;

	private long 			reportTime;
	
	@Transient
	private long 			endReportTime;

	public Long getMapID() {
		return mapID;
	}

	public void setMapID(Long mapID) {
		this.mapID = mapID;
	}

	public short getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(short alertCode) {
		this.alertCode = alertCode;
	}

	public String getDestObject() {
		return destObject;
	}

	public void setDestObject(String destObject) {
		this.destObject = destObject;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getReportSystem() {
		return reportSystem;
	}
	
	public String getReportSystemString() {
		if (reportSystem==null || reportSystem.equals("")){
			return "Unknown";
		}
		return reportSystem;
	}

	public void setReportSystem(String reportSystem) {
		this.reportSystem = reportSystem;
	}

	public String getSrcObject() {
		return srcObject;
	}
	
	public String getSrcObjectString() {
		if (srcObject==null || srcObject.equals("")){
			return "Unknown";
		}
		return srcObject;
	}

	public void setSrcObject(String srcObject) {
		this.srcObject = srcObject;
	}

	public long getViolationCounter() {
		return violationCounter;
	}

	public void setViolationCounter(long violationCounter) {
		this.violationCounter = violationCounter;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public String getLabel() {
		return "pci data";
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public long getReportTime() {
		return reportTime;
	}
	public String getReportTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(reportTime), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTimeReport(reportTime, owner.getTimeZone());
		}
	}
	
	public void setReportTime(long reportTime) {
		this.reportTime = reportTime;
	}

	public long getEndReportTime() {
		return endReportTime;
	}
	
	public String getEndReportTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(reportTime), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTimeReport(endReportTime, owner.getTimeZone());
		}
	}

	public void setEndReportTime(long endReportTime) {
		this.endReportTime = endReportTime;
	}
	
	public void addViolationCounter(long addCounter){
		this.violationCounter = this.violationCounter + addCounter;
	}
	
	public String getApName(){
		if (CacheMgmt.getInstance().getSimpleHiveAp(this.nodeID)==null) {
			return "Unknown";
		}
		return CacheMgmt.getInstance().getSimpleHiveAp(this.nodeID).getHostname();
	}
	
	public String getAlertCodeString(){
		switch (this.alertCode) {
		case	ALERT_CODE_PROBE_REQUEST: return "Probe Request";
		case	ALERT_CODE_PROBE_RESPONSE: return "Probe Response";
		case	ALERT_CODE_ASSOC_REQUEST: return "Association Request";
		case	ALERT_CODE_ASSOC_RESPONSE: return "Association Response";
		case	ALERT_CODE_DEASSOC: return "Disassociation";
		case	ALERT_CODE_AUTH: return "Authentication";
		case	ALERT_CODE_DEAUTH: return "De-authentication";
		case	ALERT_CODE_EAPOL: return "EAP over LAN";
		case	ALERT_CODE_ICMP_FLOOD: return "ICMP Flood";
		case	ALERT_CODE_UDP_FLOOD: return "UDP Flood";
		case	ALERT_CODE_SYN_FLOOD: return "SYN Flood";
		case	ALERT_CODE_ARP_FLOOD: return "ARP Flood";
		case	ALERT_CODE_ADDRESS_SWEEP: return "Address Sweep";
		case	ALERT_CODE_PORT_SCAN: return "Port Scan";
		case	ALERT_CODE_IP_SPOOF: return "IP Spoof";
		case	ALERT_CODE_RADIUS_ATTACK: return "RADIUS Attack";
		case	ALERT_CODE_TCP_SYN_CHECK: return "TCP syn check";
		case	ALERT_CODE_IP_FIREWALL_VIOLATION: return "IP Firewall Violation";
		case	ALERT_CODE_MAC_FIREWALL_VIOLATION: return "MAC Firewall Violation";
		case	ALERT_CODE_MAC_FILTER_VIOLATION: return "MAC Filter Violation";
		default:
			return "Unknown";
		}
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}