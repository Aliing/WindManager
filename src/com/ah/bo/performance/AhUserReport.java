package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_USER_REPORT")
@org.hibernate.annotations.Table(appliesTo = "HM_USER_REPORT", indexes = {
		@Index(name = "USER_REPORT_OWNER", columnNames = { "OWNER" })
		})
public class AhUserReport implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	private String reportType;

	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String name;

	private String apName;

	public static final int REPORT_PERIOD_LASTONEDAY = 1;
	public static final int REPORT_PERIOD_LASTTWODAYS = 2;
	public static final int REPORT_PERIOD_LASTTHREEDAYS = 3;
	public static final int REPORT_PERIOD_LASTONEWEEK = 4;
	public static final int REPORT_PERIOD_LASTTWOWEEKS = 5;
	public static final int REPORT_PERIOD_LASTTHREEWEEKS = 6;
	public static final int REPORT_PERIOD_LASTONEMONTH = 7;
	public static final int REPORT_PERIOD_LASTTWOMONTHs = 8;
	public static EnumItem[] REPORT_PERIOD_TYPE = MgrUtil.enumItems(
			"enum.report.reportPeriod.", new int[] {
					REPORT_PERIOD_LASTONEDAY, REPORT_PERIOD_LASTTWODAYS,
					REPORT_PERIOD_LASTTHREEDAYS, REPORT_PERIOD_LASTONEWEEK,
					REPORT_PERIOD_LASTTWOWEEKS, REPORT_PERIOD_LASTTHREEWEEKS,
					REPORT_PERIOD_LASTONEMONTH, REPORT_PERIOD_LASTTWOMONTHs});
	private int reportPeriod=1;

//	private boolean enabledEmail;
//
//	private String emailAddress;
	
	private String description;
	
	private String authMac,authHostName,authUserName,authIp;

	private boolean defaultFlag;
	
	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getAuthMac() {
		return authMac;
	}

	public void setAuthMac(String authMac) {
		this.authMac = authMac;
	}

	public String getAuthHostName() {
		return authHostName;
	}

	public void setAuthHostName(String authHostName) {
		this.authHostName = authHostName;
	}

	public String getAuthUserName() {
		return authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getAuthIp() {
		return authIp;
	}

	public void setAuthIp(String authIp) {
		this.authIp = authIp;
	}

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return name;
	}

	@Transient
	private boolean selected;
	
	@Transient
	private TimeZone tz;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getReportPeriod() {
		return reportPeriod;
	}

	public void setReportPeriod(int reportPeriod) {
		this.reportPeriod = reportPeriod;
	}

//	public boolean getEnabledEmail() {
//		return enabledEmail;
//	}
//
//	public void setEnabledEmail(boolean enabledEmail) {
//		this.enabledEmail = enabledEmail;
//	}
//
//	public String getEmailAddress() {
//		return emailAddress;
//	}
//
//	public void setEmailAddress(String emailAddress) {
//		this.emailAddress = emailAddress;
//	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getApNameForSQL(){
		if (apName==null) {
			return "";
		}
		return apName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public String getAuthMacForSQL(){
		if (authMac==null) {
			return "";
		}
		return authMac.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public String getAuthHostNameForSQL(){
		if (authHostName==null) {
			return "";
		}
		return authHostName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	public String getAuthUserNameForSQL(){
		if (authUserName==null) {
			return "";
		}
		return authUserName.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}
	public String getAuthIpForSQL(){
		if (authIp==null) {
			return "";
		}
		return authIp.toLowerCase().replace("\\", "\\\\\\\\").replace("'", "''");
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}