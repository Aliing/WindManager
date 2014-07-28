package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name="AH_PORT_AVAILABILITY")
@org.hibernate.annotations.Table(appliesTo = "AH_PORT_AVAILABILITY", indexes = {
		@Index(name = "PORT_AVAILABILITY_OWNER", columnNames = { "OWNER" }),
		@Index(name = "PORT_AVAILABILITY_MAC", columnNames = { "MAC" })
		})
public class AhPortAvailability implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	public 	final static String		INTERFACE_TYPE_WAN_STRING	=	"wan";
	public 	final static String		INTERFACE_TYPE_VPN_STRING 	= 	"vpn";
	public 	final static byte		INTERFACE_TYPE_WAN	=	1;
	public 	final static byte		INTERFACE_TYPE_VPN 	= 	2;
	public 	final static byte		INTERFACE_TYPE_LAN 	= 	2;
	
	public 	final static String		INTERFACE_STATUS_DOWN_STRING	=	"DOWN";
	public 	final static String		INTERFACE_STATUS_UP_STRING 		= 	"UP";

	public 	final static byte		INTERFACE_STATUS_DOWN			=	0;
	public 	final static byte		INTERFACE_STATUS_UP 			= 	1;
	public  final static byte		INTERFACE_STATUS_NOT_CONNECTED	=	2;
	
	private byte		interfType;
	
	private byte		interfMode;
	
	private byte		interfStatus;
	
	private String 		interfName;
	
	private int			wanipaddress;
	
	private int			wannetmask;
	
	public 	final static byte		WAN_INACTIVE	=	0;
	public 	final static byte		WAN_ACTIVE	 	= 	1;
	
	private byte		wanactive;
	
	@ManyToOne(fetch = FetchType.LAZY)
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

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	public byte getInterfType() {
		return interfType;
	}

	public void setInterfType(byte interfType) {
		this.interfType = interfType;
	}

	public byte getInterfStatus() {
		return interfStatus;
	}

	public void setInterfStatus(byte interfStatus) {
		this.interfStatus = interfStatus;
	}

	public String getInterfName() {
		return interfName;
	}

	public void setInterfName(String interfName) {
		this.interfName = interfName;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public byte getInterfMode() {
		return interfMode;
	}

	public void setInterfMode(byte interfMode) {
		this.interfMode = interfMode;
	}

	public int getWanipaddress() {
		return wanipaddress;
	}

	public void setWanipaddress(int wanipaddress) {
		this.wanipaddress = wanipaddress;
	}

	public int getWannetmask() {
		return wannetmask;
	}

	public void setWannetmask(int wannetmask) {
		this.wannetmask = wannetmask;
	}

	public byte getWanactive() {
		return wanactive;
	}

	public void setWanactive(byte wanactive) {
		this.wanactive = wanactive;
	}
}