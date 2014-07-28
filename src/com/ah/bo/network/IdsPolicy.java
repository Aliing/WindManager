package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
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

/*
 * @author frazer
 */

@Entity
@Table(name = "IDS_POLICY", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "POLICYNAME" }) })
@org.hibernate.annotations.Table(appliesTo = "IDS_POLICY", indexes = {
		@Index(name = "IDS_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class IdsPolicy implements HmBo {

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

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String policyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	// add from 4.0r1 fcs
	private boolean defaultFlag;

	private boolean rogueDetectionEnable = true;

	private boolean shortPreambleEnable = true;

	private boolean shortBeanchIntervalEnable = true;

	private boolean wmmEnable;

	private boolean ouiEnable = true;

	private boolean ssidEnable;

	private boolean inNetworkEnable = false;

	private boolean networkDetectionEnable;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "IDS_POLICY_SSID_PROFILE", joinColumns = @JoinColumn(name = "IDS_POLICY_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<IdsPolicySsidProfile> idsSsids = new ArrayList<IdsPolicySsidProfile>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "IDS_POLICY_MAC_OR_OUI", joinColumns = { @JoinColumn(name = "IDS_POLICY_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAC_OR_OUI_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacOrOui> macOrOuis = new HashSet<MacOrOui>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "IDS_POLICY_VLAN", joinColumns = { @JoinColumn(name = "IDS_POLICY_ID") }, inverseJoinColumns = { @JoinColumn(name = "VLAN_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Vlan> vlans = new HashSet<Vlan>();

	/*
	 * add mitigation parameters for 3.2r2
	 */
	@Range(min = 1, max = 600)
	private int mitigatePeriod = DEFAULT_MITIGATE_PERIOD;

	private int mitigateDuration = 14400;

	private int mitigateQuiet = 3600;

	public static final int DEFAULT_STA_REPORT_DURATION = 300;
	public static final int DEFAULT_STA_REPORT_INTERVAL = 0;
	public static final int DEFAULT_STA_REPORT_AGEOUT = 25;
	public static final int DEFAULT_STA_REPORT_PERIOD = 1;
	
	private boolean staReportEnabled;

	private int staReportDuration = DEFAULT_STA_REPORT_DURATION;
	
	private int staReportPeriod = DEFAULT_STA_REPORT_PERIOD;

	private int staReportInterval = DEFAULT_STA_REPORT_INTERVAL;

	private int staReportAgeout = DEFAULT_STA_REPORT_AGEOUT;
	
	/**
	 * add age time for 6.1r3
	 */
	public static final int DEFAULT_MITIGATE_PERIOD = 1;
	
	public static final int DEFAULT_STA_REPORT_AGETIME = 3600;
	
	private int staReportAgeTime = DEFAULT_STA_REPORT_AGETIME;
	/*
	 * add mitigation more parameters from 4.0r1 begin
	 */
	public static final short MITIGATION_MODE_MANUAL = 1;
	
	public static final short MITIGATION_MODE_SEMIAUTO = 2;
	
	public static final short MITIGATION_MODE_AUTO = 3;
	
	private short mitigationMode = MITIGATION_MODE_SEMIAUTO;
	
	private boolean inSameNetwork = true;
	
	/*
	 * add mitigation more parameters from 4.0r1 end
	 */
	
	@Range(min = 0, max = 1024)
	private int detectorAps = 1;
	
	private int deAuthTime = 60;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return policyName;
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

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRogueDetectionEnable() {
		return rogueDetectionEnable;
	}

	public void setRogueDetectionEnable(boolean rogueDetectionEnable) {
		this.rogueDetectionEnable = rogueDetectionEnable;
	}

	public boolean isShortPreambleEnable() {
		return shortPreambleEnable;
	}

	public void setShortPreambleEnable(boolean shortPreambleEnable) {
		this.shortPreambleEnable = shortPreambleEnable;
	}

	public boolean isShortBeanchIntervalEnable() {
		return shortBeanchIntervalEnable;
	}

	public void setShortBeanchIntervalEnable(boolean shortBeanchIntervalEnable) {
		this.shortBeanchIntervalEnable = shortBeanchIntervalEnable;
	}

	public boolean isWmmEnable() {
		return wmmEnable;
	}

	public void setWmmEnable(boolean wmmEnable) {
		this.wmmEnable = wmmEnable;
	}

	public boolean isOuiEnable() {
		return ouiEnable;
	}

	public void setOuiEnable(boolean ouiEnable) {
		this.ouiEnable = ouiEnable;
	}

	public boolean isSsidEnable() {
		return ssidEnable;
	}

	public void setSsidEnable(boolean ssidEnable) {
		this.ssidEnable = ssidEnable;
	}

	public boolean isInNetworkEnable() {
		return inNetworkEnable;
	}

	public void setInNetworkEnable(boolean inNetworkEnable) {
		this.inNetworkEnable = inNetworkEnable;
	}

	public boolean isNetworkDetectionEnable() {
		return networkDetectionEnable;
	}

	public int getMitigatePeriod() {
		return mitigatePeriod;
	}

	public void setMitigatePeriod(int mitigatePeriod) {
		this.mitigatePeriod = mitigatePeriod;
	}

	public int getMitigateDuration() {
		return mitigateDuration;
	}

	public void setMitigateDuration(int mitigateDuration) {
		this.mitigateDuration = mitigateDuration;
	}

	public int getMitigateQuiet() {
		return mitigateQuiet;
	}

	public void setMitigateQuiet(int mitigateQuiet) {
		this.mitigateQuiet = mitigateQuiet;
	}

	public boolean isStaReportEnabled() {
		return staReportEnabled;
	}

	public void setStaReportEnabled(boolean staReportEnabled) {
		this.staReportEnabled = staReportEnabled;
	}

	public int getStaReportDuration() {
		return staReportDuration;
	}

	public void setStaReportDuration(int staReportDuration) {
		this.staReportDuration = staReportDuration;
	}

	public int getStaReportPeriod() {
		return staReportPeriod;
	}

	public void setStaReportPeriod(int staReportPeriod) {
		this.staReportPeriod = staReportPeriod;
	}

	public int getStaReportInterval() {
		return staReportInterval;
	}

	public void setStaReportInterval(int staReportInterval) {
		this.staReportInterval = staReportInterval;
	}

	public int getStaReportAgeout() {
		return staReportAgeout;
	}

	public void setStaReportAgeout(int staReportAgeout) {
		this.staReportAgeout = staReportAgeout;
	}

	public void setNetworkDetectionEnable(boolean networkDetectionEnable) {
		this.networkDetectionEnable = networkDetectionEnable;
	}

	public Set<MacOrOui> getMacOrOuis() {
		return macOrOuis;
	}

	public void setMacOrOuis(Set<MacOrOui> macOrOuis) {
		this.macOrOuis = macOrOuis;
	}

	public List<IdsPolicySsidProfile> getIdsSsids() {
		return idsSsids;
	}

	public void setIdsSsids(List<IdsPolicySsidProfile> idsSsids) {
		this.idsSsids = idsSsids;
	}

	public Set<Vlan> getVlans() {
		return vlans;
	}

	public void setVlans(Set<Vlan> vlans) {
		this.vlans = vlans;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private String optionDisplayStyle = "none"; // by default

	public String getOptionDisplayStyle() {
		return optionDisplayStyle;
	}

	public void setOptionDisplayStyle(String optionDisplayStyle) {
		this.optionDisplayStyle = optionDisplayStyle;
	}

	@Override
	public IdsPolicy clone() {
		try {
			return (IdsPolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public short getMitigationMode()
	{
		return mitigationMode;
	}

	public void setMitigationMode(short mitigationMode)
	{
		this.mitigationMode = mitigationMode;
	}

	public boolean isInSameNetwork()
	{
		return inSameNetwork;
	}

	public void setInSameNetwork(boolean inSameNetwork)
	{
		this.inSameNetwork = inSameNetwork;
	}

	public int getDetectorAps()
	{
		return detectorAps;
	}

	public void setDetectorAps(int detectorAps)
	{
		this.detectorAps = detectorAps;
	}

	public int getDeAuthTime()
	{
		return deAuthTime;
	}

	public void setDeAuthTime(int deAuthTime)
	{
		this.deAuthTime = deAuthTime;
	}

	public boolean isDefaultFlag()
	{
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag)
	{
		this.defaultFlag = defaultFlag;
	}

	public int getStaReportAgeTime() {
		return staReportAgeTime;
	}

	public void setStaReportAgeTime(int staReportAgeTime) {
		this.staReportAgeTime = staReportAgeTime;
	}

}