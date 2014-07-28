/**
 *@filename		AccessConsole.java
 *@version
 *@author		Fiona
 *@createtime	2008-9-12 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

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
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "ACCESS_CONSOLE")
@org.hibernate.annotations.Table(appliesTo = "ACCESS_CONSOLE", indexes = {
		@Index(name = "ACCESS_CONSOLE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AccessConsole implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

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
	private String consoleName;
	
	public static final short ACCESS_CONSOLE_MODE_AUTO = 0;
	
	public static final short ACCESS_CONSOLE_MODE_DISABLE = 1;
	
	public static final short ACCESS_CONSOLE_MODE_ENABLE = 2;
	
	public static EnumItem[] ENUM_ACCESS_CONSOLE_MODE = MgrUtil.enumItems("enum.access.console.mode.",
		new int[] { ACCESS_CONSOLE_MODE_AUTO, ACCESS_CONSOLE_MODE_DISABLE, 
		ACCESS_CONSOLE_MODE_ENABLE });
	
	private short consoleMode = ACCESS_CONSOLE_MODE_AUTO;
	
	@Range(min = 1, max = 64)
	private short maxClient = 2;
	
	private int mgmtKey = SsidProfile.KEY_MGMT_WPA2_PSK;
	
	private int encryption = SsidProfile.KEY_ENC_CCMP;
	
	@Column(length = 63)
	private String asciiKey = "";

	private boolean hideSsid;

	private boolean enableTelnet = true;

	private short defaultAction = MacFilter.FILTER_ACTION_PERMIT;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ACCESS_CONSOLE_MAC_FILTER", joinColumns = { @JoinColumn(name = "ACCESS_CONSOLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAC_FILTER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacFilter> macFilters = new HashSet<MacFilter>();

	public Set<MacFilter> getMacFilters() {
		return macFilters;
	}

	public void setMacFilters(Set<MacFilter> macFilters) {
		this.macFilters = macFilters;
	}

	public int getEncryption() {
		return encryption;
	}

	@Transient
	public String getStrEncryption() {
		switch (encryption) {
			case SsidProfile.KEY_ENC_NONE:
				return "NONE";
			case SsidProfile.KEY_ENC_CCMP:
				return "CCMP(AES)";
			case SsidProfile.KEY_ENC_TKIP:
				return "TKIP";
			case SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP:
				return "Auto-TKIP or CCMP (AES)";
			default:
				return "Unknown";
		}
	}

	public void setEncryption(int encryption) {
		this.encryption = encryption;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return consoleName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Transient
	private String optionDisplayStyle = "none";
	
	public String getOptionDisplayStyle()
	{
		return optionDisplayStyle;
	}

	public void setOptionDisplayStyle(String optionDisplayStyle)
	{
		this.optionDisplayStyle = optionDisplayStyle;
	}

	@Transient
	public String getStrMgmtKey() {
		return MgrUtil.getEnumString("enum.keyMgmt." + mgmtKey);
	}
	
	@Transient
	public String getStrConsoleMode() {
		return MgrUtil.getEnumString("enum.access.console.mode." + consoleMode);
	}

	public void setMgmtKey(int mgmtKey) {
		this.mgmtKey = mgmtKey;
	}

	public short getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(short defaultAction) {
		this.defaultAction = defaultAction;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getConsoleName()
	{
		return consoleName;
	}

	public void setConsoleName(String consoleName)
	{
		this.consoleName = consoleName;
	}

	public short getConsoleMode()
	{
		return consoleMode;
	}

	public void setConsoleMode(short consoleMode)
	{
		this.consoleMode = consoleMode;
	}

	public short getMaxClient()
	{
		return maxClient;
	}

	public void setMaxClient(short maxClient)
	{
		this.maxClient = maxClient;
	}

	public boolean isHideSsid()
	{
		return hideSsid;
	}

	public void setHideSsid(boolean hideSsid)
	{
		this.hideSsid = hideSsid;
	}

	public boolean isEnableTelnet()
	{
		return enableTelnet;
	}

	public void setEnableTelnet(boolean enableTelnet)
	{
		this.enableTelnet = enableTelnet;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getMgmtKey()
	{
		return mgmtKey;
	}

	public String getAsciiKey()
	{
		return asciiKey;
	}

	public void setAsciiKey(String asciiKey)
	{
		this.asciiKey = asciiKey;
	}
	
	@Override
	public AccessConsole clone() {
		try {
			return (AccessConsole) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}