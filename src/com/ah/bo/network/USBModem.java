package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "USB_MODEM_PARAMETER")
@org.hibernate.annotations.Table(appliesTo = "USB_MODEM_PARAMETER", indexes = {
		@Index(name = "USB_MODEM_PARAMETER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class USBModem implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private Timestamp version;
	
	private boolean selected;
	
	private String modemName;
	
	private String apn;
	
	private String dailupNumber;
	
	private String userId;
	
	private String password;
	
	private String displayName;
	
	private String displayType;
	
	private String usbVendorId;
	
	private String usbProductId;
	
	private String usbModule;
	
	private String hiveOSVersionMin;
	
	private String serialPort;
	
	private String connectType;
	
	private String authType;
	
	private boolean usePeerDns;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "USB_MODEM_SIGNAL_STRENGTH_CHECK", joinColumns = @JoinColumn(name = "USB_MODEM_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<USBSignalStrengthCheck> usbSignalStrengthCheckList = new ArrayList<USBSignalStrengthCheck>();
	
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return this.modemName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getModemName() {
		return modemName;
	}

	public void setModemName(String modemName) {
		this.modemName = modemName;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}

	public String getDailupNumber() {
		return dailupNumber;
	}

	public void setDailupNumber(String dailupNumber) {
		this.dailupNumber = dailupNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getUsbVendorId() {
		return usbVendorId;
	}

	public void setUsbVendorId(String usbVendorId) {
		this.usbVendorId = usbVendorId;
	}

	public String getUsbProductId() {
		return usbProductId;
	}

	public void setUsbProductId(String usbProductId) {
		this.usbProductId = usbProductId;
	}

	public String getUsbModule() {
		return usbModule;
	}

	public void setUsbModule(String usbModule) {
		this.usbModule = usbModule;
	}

	public String getHiveOSVersionMin() {
		return hiveOSVersionMin;
	}

	public void setHiveOSVersionMin(String hiveOSVersionMin) {
		this.hiveOSVersionMin = hiveOSVersionMin;
	}

	public String getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}

	public String getConnectType() {
		return connectType;
	}

	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public boolean isUsePeerDns() {
		return usePeerDns;
	}

	public void setUsePeerDns(boolean usePeerDns) {
		this.usePeerDns = usePeerDns;
	}

	public List<USBSignalStrengthCheck> getUsbSignalStrengthCheckList() {
		return usbSignalStrengthCheckList;
	}

	public void setUsbSignalStrengthCheckList(
			List<USBSignalStrengthCheck> usbSignalStrengthCheckList) {
		this.usbSignalStrengthCheckList = usbSignalStrengthCheckList;
	}

}