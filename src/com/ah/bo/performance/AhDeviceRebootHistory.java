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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name="hm_device_reboot_history")
@org.hibernate.annotations.Table(appliesTo = "hm_device_reboot_history", indexes = {
		@Index(name = "idx_device_reboot_history_owner", columnNames = { "OWNER" }),
		@Index(name = "idx_device_reboot_history_mac", columnNames = { "MAC" })
		})
public class AhDeviceRebootHistory implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	private short		deviceType;
	
	/** reboot type
	 * 	0: power cycle 
		1: unknown
		2: hardware watchdog
		3: software watchdog
		4: reboot by user
		5: kernel panic
		6: debug
		7: hardware watchdog confirmed
		8: radio stuck
		9: reset button
		10: memory PM
		11: out of memory 
		12: lockup issue 
		13: PCI bus error 
		14: kernel exception
	 */
	public static final int		REBOOT_TYPE_POWER_CYCLE 					= 0;
	public static final int		REBOOT_TYPE_UNKNOWN							= 1;
	public static final int		REBOOT_TYPE_HARDWARE_WATCHDOG				= 2;
	public static final int		REBOOT_TYPE_SOFTWARE_WATCHDOG				= 3;
	public static final int		REBOOT_TYPE_REBOOT_BY_USER					= 4;
	public static final int		REBOOT_TYPE_KERNEL_PANIC					= 5;
	public static final int		REBOOT_TYPE_DEBUG							= 6;
	public static final int		REBOOT_TYPE_HARDWARD_WATCHDOG_CONFIRMED		= 7;
	public static final int		REBOOT_TYPE_RADIO_STUCK						= 8;
	public static final int		REBOOT_TYPE_RESET_BUTTON					= 9;
	public static final int		REBOOT_TYPE_MEMORY_PM						= 10;
	public static final int		REBOOT_TYPE_OUTOF_MEMORY					= 11;
	public static final int		REBOOT_TYPE_LOCKUP_ISSUE					= 12;
	public static final int		REBOOT_TYPE_PCI_BUS_ERROR					= 13;
	public static final int		REBOOT_TYPE_KERNEL_EXCEPTION				= 14;
	
	
	private byte		rebootType;
	
	private long		rebootTimestamp;
	
	private long 		receivedTimestamp;
	
	@Transient
	private TimeZone logTimeZone = TimeZone.getDefault();

	
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

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public short getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
	}

	public byte getRebootType() {
		return rebootType;
	}

	public void setRebootType(byte rebootType) {
		this.rebootType = rebootType;
	}

	public long getRebootTimestamp() {
		return rebootTimestamp;
	}

	public void setRebootTimestamp(long rebootTimestamp) {
		this.rebootTimestamp = rebootTimestamp;
	}

	public  long getReceivedTimestamp() {
		return receivedTimestamp;
	}

	public void setReceivedTimestamp(long receivedTimestamp) {
		this.receivedTimestamp = receivedTimestamp;
	}
	
	
   public TimeZone getLogTimeZone() {
		return logTimeZone;
	}

	public void setLogTimeZone(TimeZone logTimeZone) {
		this.logTimeZone = logTimeZone;
	}
	
  @Transient
   public String getTimestampStr(long logTimeStamp){
	   return AhDateTimeUtil.getSpecifyDateTimeReport(logTimeStamp, logTimeZone);
   }
  @Transient
	public String getRebootTypeStr() {
		return MgrUtil.getEnumString("enum.hiveAp.reboot.type."
				+ rebootType);
	}

}