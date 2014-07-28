package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_SUMMARY_PAGE")
@org.hibernate.annotations.Table(appliesTo = "HM_SUMMARY_PAGE", indexes = {
		@Index(name = "SUMMARY_PAGE_OWNER", columnNames = { "OWNER" })
		})
public class AhSummaryPage implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Column(length = 128,nullable = false)
	private String userName;
	
	@Column(length = 2048)
	private String attribute;
	
	private boolean ckwidgetAPhealth=true;
	private boolean ckwidgetAPmostClientCount=true;
	private boolean ckwidgetAPmostBandwidth;
	private boolean ckwidgetAPmostInterference;
	private boolean ckwidgetAPmostCrcError;
	private boolean ckwidgetAPmostTxRetry;
	private boolean ckwidgetAPmostRxRetry;
	private boolean ckwidgetAPsecurity;
	private boolean ckwidgetAPcompliance;
	private boolean ckwidgetAPalarm=true;
	private boolean ckwidgetAPbandwidth=true;
	private boolean ckwidgetAPsla;
	
	private boolean ckwidgetAPversion;
	private boolean ckwidgetAuditLog;
	
	private boolean ckwidgetAPuptime;
	private boolean ckwidgetActiveUser;
	
	private boolean ckwidgetCinfo=true;
	private boolean ckwidgetCmostTxAirtime;
	private boolean ckwidgetCmostRxAirtime;
	private boolean ckwidgetCmostFailure;
	private boolean ckwidgetCvendor;
	private boolean ckwidgetCradio=true;
	private boolean ckwidgetCuserprofile;
	private boolean ckwidgetCsla=true;
	
	private boolean ckwidgetSinfo=true;
	private boolean ckwidgetSuser;
	private boolean ckwidgetScpu=true;
	private boolean ckwidgetSperformanceInfo;
	

	public boolean getCkwidgetSinfo() {
		return ckwidgetSinfo;
	}
	public boolean getCkwidgetSuser() {
		return ckwidgetSuser;
	}
	public boolean getCkwidgetScpu() {
		return ckwidgetScpu;
	}
//	public boolean getCkwidgetSmemory() {
//		return ckwidgetSmemory;
//	}
	public boolean getCkwidgetSperformanceInfo() {
		return ckwidgetSperformanceInfo;
	}
	public void setCkwidgetSinfo(boolean ckwidgetSinfo) {
		this.ckwidgetSinfo = ckwidgetSinfo;
	}
	public void setCkwidgetSuser(boolean ckwidgetSuser) {
		this.ckwidgetSuser = ckwidgetSuser;
	}
	public void setCkwidgetScpu(boolean ckwidgetScpu) {
		this.ckwidgetScpu = ckwidgetScpu;
	}
//	public void setCkwidgetSmemory(boolean ckwidgetSmemory) {
//		this.ckwidgetSmemory = ckwidgetSmemory;
//	}
	public void setCkwidgetSperformanceInfo(boolean ckwidgetSperformanceInfo) {
		this.ckwidgetSperformanceInfo = ckwidgetSperformanceInfo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	// for page index
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
		this.id=id;
	}
	@Override
	public void setSelected(boolean selected) {
		
	}
	@Override
	public void setVersion(Timestamp version) {
		
	}
	@Override
	public String getLabel() {
		return null;
	}
	@Override
	public HmDomain getOwner() {
		return owner;
	}
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}
	public boolean getCkwidgetAPhealth() {
		return ckwidgetAPhealth;
	}
	public void setCkwidgetAPhealth(boolean ckwidgetAPhealth) {
		this.ckwidgetAPhealth = ckwidgetAPhealth;
	}
	public boolean getCkwidgetAPsecurity() {
		return ckwidgetAPsecurity;
	}
	public void setCkwidgetAPsecurity(boolean ckwidgetAPsecurity) {
		this.ckwidgetAPsecurity = ckwidgetAPsecurity;
	}
	public boolean getCkwidgetAPcompliance() {
		return ckwidgetAPcompliance;
	}
	public void setCkwidgetAPcompliance(boolean ckwidgetAPcompliance) {
		this.ckwidgetAPcompliance = ckwidgetAPcompliance;
	}
	public boolean getCkwidgetAPalarm() {
		return ckwidgetAPalarm;
	}
	public void setCkwidgetAPalarm(boolean ckwidgetAPalarm) {
		this.ckwidgetAPalarm = ckwidgetAPalarm;
	}
	public boolean getCkwidgetAPbandwidth() {
		return ckwidgetAPbandwidth;
	}
	public void setCkwidgetAPbandwidth(boolean ckwidgetAPbandwidth) {
		this.ckwidgetAPbandwidth = ckwidgetAPbandwidth;
	}
	public boolean getCkwidgetAPsla() {
		return ckwidgetAPsla;
	}
	public void setCkwidgetAPsla(boolean ckwidgetAPsla) {
		this.ckwidgetAPsla = ckwidgetAPsla;
	}
	public boolean getCkwidgetCinfo() {
		return ckwidgetCinfo;
	}
	public void setCkwidgetCinfo(boolean ckwidgetCinfo) {
		this.ckwidgetCinfo = ckwidgetCinfo;
	}
	public boolean getCkwidgetCvendor() {
		return ckwidgetCvendor;
	}
	public void setCkwidgetCvendor(boolean ckwidgetCvendor) {
		this.ckwidgetCvendor = ckwidgetCvendor;
	}
	public boolean getCkwidgetCradio() {
		return ckwidgetCradio;
	}
	public void setCkwidgetCradio(boolean ckwidgetCradio) {
		this.ckwidgetCradio = ckwidgetCradio;
	}
	public boolean getCkwidgetCuserprofile() {
		return ckwidgetCuserprofile;
	}
	public void setCkwidgetCuserprofile(boolean ckwidgetCuserprofile) {
		this.ckwidgetCuserprofile = ckwidgetCuserprofile;
	}
	public boolean getCkwidgetCsla() {
		return ckwidgetCsla;
	}
	public void setCkwidgetCsla(boolean ckwidgetCsla) {
		this.ckwidgetCsla = ckwidgetCsla;
	}
	/**
	 * @return the ckwidgetAPmostClientCount
	 */
	public boolean getCkwidgetAPmostClientCount() {
		return ckwidgetAPmostClientCount;
	}
	/**
	 * @param ckwidgetAPmostClientCount the ckwidgetAPmostClientCount to set
	 */
	public void setCkwidgetAPmostClientCount(boolean ckwidgetAPmostClientCount) {
		this.ckwidgetAPmostClientCount = ckwidgetAPmostClientCount;
	}
	/**
	 * @return the ckwidgetAPmostBandwidth
	 */
	public boolean getCkwidgetAPmostBandwidth() {
		return ckwidgetAPmostBandwidth;
	}
	/**
	 * @param ckwidgetAPmostBandwidth the ckwidgetAPmostBandwidth to set
	 */
	public void setCkwidgetAPmostBandwidth(boolean ckwidgetAPmostBandwidth) {
		this.ckwidgetAPmostBandwidth = ckwidgetAPmostBandwidth;
	}
	/**
	 * @return the ckwidgetAPmostInterference
	 */
	public boolean getCkwidgetAPmostInterference() {
		return ckwidgetAPmostInterference;
	}
	/**
	 * @param ckwidgetAPmostInterference the ckwidgetAPmostInterference to set
	 */
	public void setCkwidgetAPmostInterference(boolean ckwidgetAPmostInterference) {
		this.ckwidgetAPmostInterference = ckwidgetAPmostInterference;
	}
	/**
	 * @return the ckwidgetAPmostCrcError
	 */
	public boolean getCkwidgetAPmostCrcError() {
		return ckwidgetAPmostCrcError;
	}
	/**
	 * @param ckwidgetAPmostCrcError the ckwidgetAPmostCrcError to set
	 */
	public void setCkwidgetAPmostCrcError(boolean ckwidgetAPmostCrcError) {
		this.ckwidgetAPmostCrcError = ckwidgetAPmostCrcError;
	}
	/**
	 * @return the ckwidgetAPmostTxRetry
	 */
	public boolean getCkwidgetAPmostTxRetry() {
		return ckwidgetAPmostTxRetry;
	}
	/**
	 * @param ckwidgetAPmostTxRetry the ckwidgetAPmostTxRetry to set
	 */
	public void setCkwidgetAPmostTxRetry(boolean ckwidgetAPmostTxRetry) {
		this.ckwidgetAPmostTxRetry = ckwidgetAPmostTxRetry;
	}
	/**
	 * @return the ckwidgetAPmostRxRetry
	 */
	public boolean getCkwidgetAPmostRxRetry() {
		return ckwidgetAPmostRxRetry;
	}
	/**
	 * @param ckwidgetAPmostRxRetry the ckwidgetAPmostRxRetry to set
	 */
	public void setCkwidgetAPmostRxRetry(boolean ckwidgetAPmostRxRetry) {
		this.ckwidgetAPmostRxRetry = ckwidgetAPmostRxRetry;
	}
	/**
	 * @return the ckwidgetCmostTxAirtime
	 */
	public boolean getCkwidgetCmostTxAirtime() {
		return ckwidgetCmostTxAirtime;
	}
	/**
	 * @param ckwidgetCmostTxAirtime the ckwidgetCmostTxAirtime to set
	 */
	public void setCkwidgetCmostTxAirtime(boolean ckwidgetCmostTxAirtime) {
		this.ckwidgetCmostTxAirtime = ckwidgetCmostTxAirtime;
	}
	/**
	 * @return the ckwidgetCmostRxAirtime
	 */
	public boolean getCkwidgetCmostRxAirtime() {
		return ckwidgetCmostRxAirtime;
	}
	/**
	 * @param ckwidgetCmostRxAirtime the ckwidgetCmostRxAirtime to set
	 */
	public void setCkwidgetCmostRxAirtime(boolean ckwidgetCmostRxAirtime) {
		this.ckwidgetCmostRxAirtime = ckwidgetCmostRxAirtime;
	}
	/**
	 * @return the ckwidgetCmostFailure
	 */
	public boolean getCkwidgetCmostFailure() {
		return ckwidgetCmostFailure;
	}
	/**
	 * @param ckwidgetCmostFailure the ckwidgetCmostFailure to set
	 */
	public void setCkwidgetCmostFailure(boolean ckwidgetCmostFailure) {
		this.ckwidgetCmostFailure = ckwidgetCmostFailure;
	}
	/**
	 * @return the ckwidgetAPversion
	 */
	public boolean getCkwidgetAPversion() {
		return ckwidgetAPversion;
	}
	/**
	 * @param ckwidgetAPversion the ckwidgetAPversion to set
	 */
	public void setCkwidgetAPversion(boolean ckwidgetAPversion) {
		this.ckwidgetAPversion = ckwidgetAPversion;
	}
	/**
	 * @return the ckwidgetAuditLog
	 */
	public boolean getCkwidgetAuditLog() {
		return ckwidgetAuditLog;
	}
	/**
	 * @param ckwidgetAuditLog the ckwidgetAuditLog to set
	 */
	public void setCkwidgetAuditLog(boolean ckwidgetAuditLog) {
		this.ckwidgetAuditLog = ckwidgetAuditLog;
	}
	/**
	 * @return the ckwidgetAPuptime
	 */
	public boolean getCkwidgetAPuptime() {
		return ckwidgetAPuptime;
	}
	/**
	 * @param ckwidgetAPuptime the ckwidgetAPuptime to set
	 */
	public void setCkwidgetAPuptime(boolean ckwidgetAPuptime) {
		this.ckwidgetAPuptime = ckwidgetAPuptime;
	}
	/**
	 * @return the ckwidgetActiveUser
	 */
	public boolean getCkwidgetActiveUser() {
		return ckwidgetActiveUser;
	}
	/**
	 * @param ckwidgetActiveUser the ckwidgetActiveUser to set
	 */
	public void setCkwidgetActiveUser(boolean ckwidgetActiveUser) {
		this.ckwidgetActiveUser = ckwidgetActiveUser;
	}
	
}