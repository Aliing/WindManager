/**
 *@filename		AcmEntitleKeyHistoryInfo.java
 *@version
 *@author		Fiona
 *@createtime	Sep 17, 2013 2:24:31 PM
 *Copyright (c) 2006-2013 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "ACM_ENTITLE_KEY_HISTORY_INFO")
@org.hibernate.annotations.Table(appliesTo = "ACM_ENTITLE_KEY_HISTORY_INFO", indexes = {
		@Index(name = "ACM_ENTITLE_KEY_HISTORY_INFO_DOMAINNAME", columnNames = { "DOMAINNAME" })
	})
public class AcmEntitleKeyHistoryInfo implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH, nullable = false, unique = true)
	private String entitleKey;

	private long activeTime;

	private int orderType;

	private int numberOfClients;
	
	private long supportStartDate;

	private long supportEndDate;

	private String domainName;
	
	public static final short ENTITLE_KEY_STATUS_NORMAL = 1; // active
	
	public static final short ENTITLE_KEY_STATUS_DISABLE = 2; // disabled
	
	public static final short ENTITLE_KEY_STATUS_OVERDUE = 3; // overdue
	
	private short statusFlag = ENTITLE_KEY_STATUS_NORMAL;
	
	private boolean sendEmail;
	
	// sales order from Netsuite
	private String orderNumber;

	public boolean isSendEmail()
	{
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail)
	{
		this.sendEmail = sendEmail;
	}

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}

	public long getSupportEndDate() {
		return supportEndDate;
	}

	public void setSupportEndDate(long supportEndDate) {
		this.supportEndDate = supportEndDate;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Version
	private Timestamp version;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "Entitlement Key:" + entitleKey;
	}

	@Transient
	public String getActiveTimeStr() {
		return AhDateTimeUtil.getDateStrFromLong(activeTime, AhDateTimeUtil.DEFAULT_FORMATTER,
				HmDomain.HOME_DOMAIN.equals(domainName) ? HmBeOsUtil.getServerTimeZone()
						: getTimeZoneOfVhm());
	}

	@Transient
	public String getSupportEndTimeStr() {
		if (supportEndDate > 0) {
			return AhDateTimeUtil.getDateStrFromLongNoTimeZone(supportEndDate);
		} else {
			return "N/A";
		}
	}

	@Transient
	private String getTimeZoneOfVhm() {
		HmDomain hmDom = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", domainName);
		if (null != hmDom) {
			return hmDom.getTimeZoneString();
		}
		return HmBeOsUtil.getServerTimeZone();
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public short getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(short statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getEntitleKey() {
		return entitleKey;
	}

	public void setEntitleKey(String entitleKey) {
		this.entitleKey = entitleKey;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public int getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}

	public long getSupportStartDate() {
		return supportStartDate;
	}

	public void setSupportStartDate(long supportStartDate) {
		this.supportStartDate = supportStartDate;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

}
