package com.ah.bo.hiveap;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.StpSettings;

@Entity
@Table(name = "DEVICE_STP_SETTINGS")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_STP_SETTINGS", indexes = { @Index(name = "DEVICE_SETTINGS_OWNER", columnNames = { "OWNER" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeviceStpSettings implements HmBo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -987450929114493559L;

	private short stp_mode = StpSettings.STP_MODE_STP;
	public static final short DEFAULT_FORCE_VERSION = -1;
	public static final short DEFAULT_MAX_AGE = 20;
	public static final short DEFAULT_HELLO_TIME = 2;
	public static final short MAX_HELLO_TIME = 10;
	public static final short DEFAULT_FORWARD_DELAY = 15;
	public static final int DEFAULT_PRIORITY = 32768;
	public static final int BASE_PRIORITY = 4096;

	@Transient
	private short timerItem = 6;
	private short helloTime = DEFAULT_HELLO_TIME;
	private short forwardTime = DEFAULT_FORWARD_DELAY;
	private short maxAge = DEFAULT_MAX_AGE;
	private short forceVersion = DEFAULT_FORCE_VERSION;
	
	@Transient
	private boolean mstpEnable = isEnableStp() && stp_mode == StpSettings.STP_MODE_MSTP;

	@Transient
	private String forceVersionString = "";
	@Transient
	private boolean restrict = false;
	
	private int priority = DEFAULT_PRIORITY;
	private boolean overrideStp = false;
	
	@Transient
	@Range(min = MIN_TIMES, max = MAX_TIMES)
	private short times = MIN_TIMES;
	public static final short MAX_TIMES = 15;
	public static final short MIN_TIMES = 0;
	private boolean enableStp = false;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "INTERFACE_STP_SETTINGS", joinColumns = @JoinColumn(name = "DEVICE_STP_SETTINGS_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<InterfaceStpSettings> interfaceStpSettings = new ArrayList<InterfaceStpSettings>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "INTERFACE_MSTP_SETTINGS", joinColumns = @JoinColumn(name = "DEVICE_STP_SETTINGS_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<InterfaceMstpSettings> interfaceMstpSettings = new ArrayList<InterfaceMstpSettings>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MSTP_INSTANCE_PRIORITY", joinColumns = @JoinColumn(name = "DEVICE_STP_SETTINGS_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<DeviceMstpInstancePriority> instancePriority = new ArrayList<DeviceMstpInstancePriority>();

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Transient
	private boolean selected;

	public boolean isEnableStp() {
		return enableStp;
	}

	public void setEnableStp(boolean enableStp) {
		this.enableStp = enableStp;
	}

	public short getStp_mode() {
		return stp_mode;
	}

	public void setStp_mode(short stp_mode) {
		this.stp_mode = stp_mode;
	}

	public short getHelloTime() {
		return helloTime;
	}

	public void setHelloTime(short helloTime) {
		this.helloTime = helloTime;
	}

	public short getForwardTime() {
		return forwardTime;
	}

	public void setForwardTime(short forwardTime) {
		this.forwardTime = forwardTime;
	}

	public short getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(short maxAge) {
		this.maxAge = maxAge;
	}

	public short getForceVersion() {
		if(!mstpEnable && forceVersion == StpSettings.STP_MODE_MSTP){
			this.setForceVersion(DEFAULT_FORCE_VERSION);
		}
		return forceVersion;
	}

	public void setForceVersion(short forceVersion) {
		this.forceVersion = forceVersion;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isOverrideStp() {
		return overrideStp;
	}

	public void setOverrideStp(boolean overrideStp) {
		this.overrideStp = overrideStp;
	}

	public short getTimes() {
		this.times = (short) (this.priority / BASE_PRIORITY);
		return times;
	}

	public void setTimes(short times) {
		this.times = times;
		this.setPriority(BASE_PRIORITY * times);
	}

	public String getForceVersionString() {
		if (this.forceVersion == DEFAULT_FORCE_VERSION) {
			forceVersionString = "";
		} else {
			forceVersionString = Short.toString(this.forceVersion);
		}
		return forceVersionString;
	}

	public void setForceVersionString(String forceVersionString) {
		if ("".equalsIgnoreCase(forceVersionString)
				|| forceVersionString == null) {
			this.setForceVersion(DEFAULT_FORCE_VERSION);
		} else {
			this.setForceVersion(Short.parseShort(forceVersionString));
		}
	}

	@Override
	public HmDomain getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// TODO Auto-generated method stub
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		this.selected = selected;
	}

	public List<InterfaceStpSettings> getInterfaceStpSettings() {
		return interfaceStpSettings;
	}

	public void setInterfaceStpSettings(
			List<InterfaceStpSettings> interfaceStpSettings) {
		this.interfaceStpSettings = interfaceStpSettings;
	}

	public List<InterfaceMstpSettings> getInterfaceMstpSettings() {
		return interfaceMstpSettings;
	}

	public void setInterfaceMstpSettings(
			List<InterfaceMstpSettings> interfaceMstpSettings) {
		this.interfaceMstpSettings = interfaceMstpSettings;
	}

	public List<DeviceMstpInstancePriority> getInstancePriority() {
		return instancePriority;
	}

	public void setInstancePriority(
			List<DeviceMstpInstancePriority> instancePriority) {
		this.instancePriority = instancePriority;
	}

	public short getTimerItem() {
		switch(this.getForwardTime()){
			case DeviceStpSettings.DEFAULT_FORWARD_DELAY:
				timerItem = 6;
				break;
			case 13:
				timerItem = 5;
				break;
			case 12:
				timerItem = 4;
				break;
			case 10:
				timerItem = 3;
				break;
			case 9:
				timerItem = 2;
				break;
			case 7:
				timerItem = 1;
				break;
		}
		return timerItem;
	}

	public void setTimerItem(short timerItem) {
		this.setHelloTime(DEFAULT_HELLO_TIME);
		switch(timerItem){
			case 1:
				this.setForwardTime(Short.valueOf("7"));
				this.setMaxAge(Short.valueOf("10"));
				break;
			case 2:
				this.setForwardTime(Short.valueOf("9"));
				this.setMaxAge(Short.valueOf("12"));
				break;
			case 3:
				this.setForwardTime(Short.valueOf("10"));
				this.setMaxAge(Short.valueOf("14"));
				break;
			case 4:
				this.setForwardTime(Short.valueOf("12"));
				this.setMaxAge(Short.valueOf("16"));
				break;
			case 5:
				this.setForwardTime(Short.valueOf("13"));
				this.setMaxAge(Short.valueOf("18"));
				break;
			case 6:
				this.setForwardTime(DeviceStpSettings.DEFAULT_FORWARD_DELAY);
				this.setMaxAge(DeviceStpSettings.DEFAULT_MAX_AGE);
				break;
		}
		this.timerItem = timerItem;
	}

	public boolean isRestrict() {
		if(!mstpEnable && this.forceVersion == StpSettings.STP_MODE_MSTP){
			this.setForceVersion(DEFAULT_FORCE_VERSION);
			this.setRestrict(false);
		}
		if(this.forceVersion != DeviceStpSettings.DEFAULT_FORCE_VERSION){
			this.restrict = true;
		}else{
			this.restrict = false;
		}
		return restrict;
	}

	public void setRestrict(boolean restrict) {
		if(!restrict){
			this.setForceVersion(DEFAULT_FORCE_VERSION);
		}
		this.restrict = restrict;
	}

	public boolean isMstpEnable() {
		return mstpEnable;
	}

	public void setMstpEnable(boolean mstpEnable) {
		this.mstpEnable = mstpEnable;
	}
}