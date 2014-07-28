package com.ah.bo.mobility;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.MacFilter;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "HIVE_PROFILE", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "HIVENAME" }) })
@org.hibernate.annotations.Table(appliesTo = "HIVE_PROFILE", indexes = {
		@Index(name = "HIVE_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class HiveProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String hiveName;

	@Range(min = 1, max = 2346)
	private int rtsThreshold = 2346;

	@Range(min = 256, max = 2346)
	private int fragThreshold = 2346;

	//@Range(min = 1024, max = 65535)
	@Range(min = 1500, max = 65000)
	private int l3TrafficPort = 3000;

	private boolean enabledPassword = true;

	private boolean defaultFlag;

	private boolean enabledThreshold;

//	public static final short CONNECTION_THRESHOLD_LOW = 1;
//
//	public static final short CONNECTION_THRESHOLD_MDIUM = 2;
//
//	public static final short CONNECTION_THRESHOLD_HIGH = 3;
//
//	public static EnumItem[] ENUM_CONNECTION_THRESHOLD = MgrUtil.enumItems(
//			"enum.hive.connection.threshold.", new int[] {
//					CONNECTION_THRESHOLD_LOW, CONNECTION_THRESHOLD_MDIUM,
//					CONNECTION_THRESHOLD_HIGH });

	private short connectionThreshold=-80;

	@Range(min = 1, max = 60)
	private int pollingInterval = 1;

	@Column(length = 63)
	private String hivePassword;
	
	public static final int GENERATE_PASSWORD_AUTO=1;
	public static final int GENERAtE_PASSWORK_MANUL=2;
	private int generatePasswordType=GENERAtE_PASSWORK_MANUL;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private int defaultAction;

	private boolean enabledL3Setting;

	@Range(min = 5, max = 360000)
	private int keepAliveInterval = 10;

	@Range(min = 2, max = 1000)
	private int keepAliveAgeout = 5;

	@Range(min = 10, max = 36000)
	private int updateInterval = 60;

	@Range(min = 1, max = 1000)
	private int updateAgeout = 60;
	
	private boolean neighborTypeBack = true;
	private boolean neighborTypeAccess = true;
	
	private int eth0Priority=0;
	private int eth1Priority=0;
	private int agg0Priority=0;
	private int red0Priority=0;

	@Version
	private Timestamp version;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "HIVE_PROFILE_MAC_FILTER", joinColumns = { @JoinColumn(name = "HIVE_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAC_FILTER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacFilter> macFilters = new HashSet<MacFilter>();

	public Set<MacFilter> getMacFilters() {
		return macFilters;
	}

	public void setMacFilters(Set<MacFilter> macFilters) {
		this.macFilters = macFilters;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "HIVE_DOS_ID")
	private DosPrevention hiveDos;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_DOS_ID")
	private DosPrevention stationDos;
	
    @Transient
    public String advancePanelStyle = "none"; // by default
    
    @Transient
	private String displayEnabledPassword = "Modified";
    
	public String getDisplayEnabledPassword() {
		if (getEnabledPassword()) {
			displayEnabledPassword = "Modified";
		} else {
			displayEnabledPassword = "Unmodified";
		}
		return displayEnabledPassword;
	}

	public void setDisplayEnabledPassword(String displayEnabledPassword) {
		this.displayEnabledPassword = displayEnabledPassword;
	}

	public int getL3TrafficPort() {
		return l3TrafficPort;
	}

	public void setL3TrafficPort(int l3TrafficPort) {
		this.l3TrafficPort = l3TrafficPort;
	}

	public boolean isEnabledPassword() {
		return enabledPassword;
	}

	public boolean getEnabledPassword() {
		return enabledPassword;
	}

	public void setEnabledPassword(boolean enabledPassword) {
		this.enabledPassword = enabledPassword;
	}

	public DosPrevention getHiveDos() {
		return hiveDos;
	}

	public void setHiveDos(DosPrevention hiveDos) {
		this.hiveDos = hiveDos;
	}

	public DosPrevention getStationDos() {
		return stationDos;
	}

	public void setStationDos(DosPrevention stationDos) {
		this.stationDos = stationDos;
	}

	public int getFragThreshold() {
		return fragThreshold;
	}

	public void setFragThreshold(int fragThreshold) {
		this.fragThreshold = fragThreshold;
	}

	public String getHiveName() {
		return hiveName;
	}

	public void setHiveName(String hiveName) {
		this.hiveName = hiveName.trim();
	}

	public String getHivePassword() {
		return hivePassword;
	}

	public void setHivePassword(String hivePassword) {
		this.hivePassword = hivePassword;
	}

	@Override
	public Long getId() {
		return id;
	}

	public int getRtsThreshold() {
		return rtsThreshold;
	}

	public void setRtsThreshold(int rtsThreshold) {
		this.rtsThreshold = rtsThreshold;
	}

	public int getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(int defaultAction) {
		this.defaultAction = defaultAction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public String getLabel() {
		return hiveName;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean getEnabledThreshold() {
		return enabledThreshold;
	}

	public void setEnabledThreshold(boolean enabledThreshold) {
		this.enabledThreshold = enabledThreshold;
	}

	public short getConnectionThreshold() {
		return connectionThreshold;
	}

	public void setConnectionThreshold(short connectionThreshold) {
		this.connectionThreshold = connectionThreshold;
	}

	public int getPollingInterval() {
		return pollingInterval;
	}

	public void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	public boolean getEnabledL3Setting() {
		return enabledL3Setting;
	}

	public void setEnabledL3Setting(boolean enabledL3Setting) {
		this.enabledL3Setting = enabledL3Setting;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public int getKeepAliveAgeout() {
		return keepAliveAgeout;
	}

	public void setKeepAliveAgeout(int keepAliveAgeout) {
		this.keepAliveAgeout = keepAliveAgeout;
	}

	public int getGeneratePasswordType() {
		return generatePasswordType;
	}

	public void setGeneratePasswordType(int generatePasswordType) {
		this.generatePasswordType = generatePasswordType;
	}

	public String getAdvancePanelStyle() {
		return advancePanelStyle;
	}

	public void setAdvancePanelStyle(String advancePanelStyle) {
		this.advancePanelStyle = advancePanelStyle;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public int getUpdateAgeout() {
		return updateAgeout;
	}

	public void setUpdateAgeout(int updateAgeout) {
		this.updateAgeout = updateAgeout;
	}

    @Override
    public HiveProfile clone() {
       try {
           return (HiveProfile) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

	public boolean getNeighborTypeBack() {
		return neighborTypeBack;
	}

	public boolean getNeighborTypeAccess() {
		return neighborTypeAccess;
	}

	public void setNeighborTypeBack(boolean neighborTypeBack) {
		this.neighborTypeBack = neighborTypeBack;
	}

	public void setNeighborTypeAccess(boolean neighborTypeAccess) {
		this.neighborTypeAccess = neighborTypeAccess;
	}

	public int getEth0Priority() {
		return eth0Priority;
	}

	public int getEth1Priority() {
		return eth1Priority;
	}

	public int getAgg0Priority() {
		return agg0Priority;
	}

	public int getRed0Priority() {
		return red0Priority;
	}

	public void setEth0Priority(int eth0Priority) {
		this.eth0Priority = eth0Priority;
	}

	public void setEth1Priority(int eth1Priority) {
		this.eth1Priority = eth1Priority;
	}

	public void setAgg0Priority(int agg0Priority) {
		this.agg0Priority = agg0Priority;
	}

	public void setRed0Priority(int red0Priority) {
		this.red0Priority = red0Priority;
	}

}