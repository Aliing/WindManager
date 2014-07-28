package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
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

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "AH_ADMINLOGIN_SESSION")
@org.hibernate.annotations.Table(appliesTo = "AH_ADMINLOGIN_SESSION", indexes = {
		@Index(name = "ADMIN_LOGIN_SESSION_OWNER", columnNames = { "OWNER" }),
		@Index(name = "ADMIN_LOGIN_SESSION_TIME", columnNames = { "loginTime" })
		})
public class AhAdminLoginSession implements HmBo {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 128)
	private String userName;

	@Column(length = 128)
	private String userFullName;

	@Column(length = 128)
	private String emailAddress;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private long loginTime;
	
	private long logoutTime;
	
	private long totalLoginTime;
	
	private long apCount;

	private String timeZone = TimeZone.getDefault().getID();

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "currentClientSession";
	}

	// For multi page selection
	@Transient
	private boolean	selected;

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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public long getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(long logoutTime) {
		this.logoutTime = logoutTime;
	}

	public long getTotalLoginTime() {
		return totalLoginTime;
	}

	public void setTotalLoginTime(long totalLoginTime) {
		this.totalLoginTime = totalLoginTime;
	}

	public long getApCount() {
		return apCount;
	}

	public void setApCount(long apCount) {
		this.apCount = apCount;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	@Transient
	private TimeZone plannerAdminTimeZone;
	@Transient
	private long loginCount;
	@Transient
	private long currentLoginCount;


	public long getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(long loginCount) {
		this.loginCount = loginCount;
	}

	public long getCurrentLoginCount() {
		return currentLoginCount;
	}

	public void setCurrentLoginCount(long currentLoginCount) {
		this.currentLoginCount = currentLoginCount;
	}

	public TimeZone getPlannerAdminTimeZone() {
		return plannerAdminTimeZone;
	}

	public void setPlannerAdminTimeZone(TimeZone plannerAdminTimeZone) {
		this.plannerAdminTimeZone = plannerAdminTimeZone;
	}
	
	public String getTotalLoginTimeString() {
		if (totalLoginTime==0){
			return " ";
		}
		return NmsUtil.transformTime((int) (totalLoginTime / 1000)).replace(" 0 Secs", "");
	}
	
	public String getLastLoginTimeString() {
		if (loginTime==0) {
			return " ";
		}
		if (plannerAdminTimeZone==null) {
			return AhDateTimeUtil.getSpecifyDateTime(loginTime, owner);
		}
		return AhDateTimeUtil.getSpecifyDateTime(loginTime, plannerAdminTimeZone, owner);
	}

}