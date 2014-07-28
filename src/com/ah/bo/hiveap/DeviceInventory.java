package com.ah.bo.hiveap;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "DEVICE_INVENTORY")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_INVENTORY", indexes = {
		@Index(name = "DEVICE_INVENTORY_SERIALNUMBER_OWNER", columnNames = { "OWNER", "SERIALNUMBER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DeviceInventory implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;
	
	@Column(length = 14, nullable = false, unique = true)
	private String serialNumber;

	@Column(length = 12)
	private String macAddress;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String hostName;

	private long timestamp= System.currentTimeMillis();

	public static final short STATUS_DISCONNECT_REDIRECTOR = 1;
	public static final short STATUS_CONNECT_REDIRECTOR = 2;
	public static final short STATUS_DISCONNECT_HM = 3;
	public static final short STATUS_CONNECT_HM = 4;
	private short connectStatus = STATUS_DISCONNECT_REDIRECTOR;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	private boolean selected;

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
	public boolean equals(Object other) {
		return other instanceof DeviceInventory
				&& (null == id ? super.equals(other) : id.equals(((DeviceInventory) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Override
	public DeviceInventory clone() {
		try {
			return (DeviceInventory) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String getLabel() {
		return this.getSerialNumber();
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public short getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(short connectStatus) {
		this.connectStatus = connectStatus;
	}

	@Transient
	public String getConnectStatusDesc() {
		return MgrUtil.getEnumString("geneva_08.enum.device.inventory.connection.status." + this.getConnectStatus());
	}
	
	@Transient
	private HiveAp hiveAp;

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
}
