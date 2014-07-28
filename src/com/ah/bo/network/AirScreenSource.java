package com.ah.bo.network;

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
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "AIR_SCREEN_SOURCE")
@org.hibernate.annotations.Table(appliesTo = "AIR_SCREEN_SOURCE", indexes = {
		@Index(name = "AIR_SCREEN_SOURCE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AirScreenSource implements HmBo {

	private static final long serialVersionUID = 1L;

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String profileName;

	public static final short TYPE_CLIENT = 1;
	public static EnumItem[] TYPE = MgrUtil.enumItems("enum.as.source.type.",
			new int[] { TYPE_CLIENT });
	private short type = TYPE_CLIENT;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OUI_ID")
	private MacOrOui oui;

	public static final short ENCRYPTION_MODE_ANY = 1;
	public static final short ENCRYPTION_MODE_TKIP = 2;
	public static final short ENCRYPTION_MODE_AES = 3;
	public static EnumItem[] ENCRYPTION_MODE = MgrUtil.enumItems(
			"enum.as.source.encryption.mode.", new int[] { ENCRYPTION_MODE_ANY,
					ENCRYPTION_MODE_TKIP, ENCRYPTION_MODE_AES });
	private short encryptionMode = ENCRYPTION_MODE_ANY;

	public static final short AUTH_MODE_OPEN = 1;
	public static final short AUTH_MODE_WEP = 2;
	public static final short AUTH_MODE_WEP_OPEN = 3;
	public static final short AUTH_MODE_WEP_SHARED = 4;
	public static final short AUTH_MODE_DYNAMIC_WEP = 5;
	public static final short AUTH_MODE_WPA = 6;
	public static final short AUTH_MODE_WPA_PSK = 7;
	public static final short AUTH_MODE_WPA_8021X = 8;
	public static final short AUTH_MODE_WPA2_PSK = 9;
	public static final short AUTH_MODE_WPA2_8021X = 10;
	public static EnumItem[] AUTH_MODE = MgrUtil.enumItems(
			"enum.as.source.auth.mode.", new int[] { AUTH_MODE_OPEN,
					AUTH_MODE_WEP, AUTH_MODE_WEP_OPEN, AUTH_MODE_WEP_SHARED,
					AUTH_MODE_DYNAMIC_WEP, AUTH_MODE_WPA, AUTH_MODE_WPA_PSK,
					AUTH_MODE_WPA_8021X, AUTH_MODE_WPA2_PSK,
					AUTH_MODE_WPA2_8021X });
	private short authMode = AUTH_MODE_OPEN;

	@Range(min = 3, max = 65)
	private int minRssi = 3;

	@Range(min = 3, max = 65)
	private int maxRssi = 65;

	@Column(length = 64)
	private String comment;

	@Override
	public Long getId() {
		return id;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return profileName;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public MacOrOui getOui() {
		return oui;
	}

	public void setOui(MacOrOui oui) {
		this.oui = oui;
	}

	public short getEncryptionMode() {
		return encryptionMode;
	}

	public void setEncryptionMode(short encryptionMode) {
		this.encryptionMode = encryptionMode;
	}

	public short getAuthMode() {
		return authMode;
	}

	public void setAuthMode(short authMode) {
		this.authMode = authMode;
	}

	public int getMinRssi() {
		return minRssi;
	}

	public void setMinRssi(int minRssi) {
		this.minRssi = minRssi;
	}

	public int getMaxRssi() {
		return maxRssi;
	}

	public void setMaxRssi(int maxRssi) {
		this.maxRssi = maxRssi;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public String getTypeString() {
		switch (type) {
		case TYPE_CLIENT:
			return MgrUtil.getEnumString("enum.as.source.type." + type);
		default:
			return "";
		}
	}

	@Transient
	public String getMacOrOuiString() {
		if (null != oui) {
			return oui.getMacOrOuiName();
		}
		return "";
	}

	@Transient
	public String getEncryptionModeString() {
		switch (encryptionMode) {
		case ENCRYPTION_MODE_ANY:
		case ENCRYPTION_MODE_TKIP:
		case ENCRYPTION_MODE_AES:
			return MgrUtil.getEnumString("enum.as.source.encryption.mode."
					+ encryptionMode);
		default:
			return "";
		}
	}

	@Transient
	public String getAuthModeString() {
		switch (authMode) {
		case AUTH_MODE_OPEN:
		case AUTH_MODE_WEP:
		case AUTH_MODE_WEP_OPEN:
		case AUTH_MODE_WEP_SHARED:
		case AUTH_MODE_DYNAMIC_WEP:
		case AUTH_MODE_WPA:
		case AUTH_MODE_WPA_PSK:
		case AUTH_MODE_WPA_8021X:
		case AUTH_MODE_WPA2_PSK:
		case AUTH_MODE_WPA2_8021X:
			return MgrUtil
					.getEnumString("enum.as.source.auth.mode." + authMode);
		default:
			return "";
		}
	}

	@Transient
	public String getRssiString() {
		return minRssi + " - " + maxRssi + " dB";
	}

}