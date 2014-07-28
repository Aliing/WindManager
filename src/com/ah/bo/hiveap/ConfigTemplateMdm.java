package com.ah.bo.hiveap;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "CONFIG_TEMPLATE_MDM")
@org.hibernate.annotations.Table(appliesTo = "CONFIG_TEMPLATE_MDM", indexes = { @Index(name = "CONFIG_TEMPLATE_MDM_OWNER", columnNames = { "OWNER" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConfigTemplateMdm implements HmBo {
    private static final long serialVersionUID = -7004352226491690463L;
    public static final int MDM_ENROLL_TYPE_JSS = 0;
	public static final int MDM_ENROLL_TYPE_AIRWATCH = 1;
	public static final int MDM_ENROLL_TYPE_AEROHIVE = 2;

	public static EnumItem[] ENUM_MDM_ENROLL_TYPE = MgrUtil.enumItems(
			"enum.ssid.mdm.enroll.type.", new int[] { MDM_ENROLL_TYPE_AIRWATCH,
					MDM_ENROLL_TYPE_JSS });

	private static final int MDM_OS_TYPE_APPLE = 0x01; // ipod/ipad/iphone
	private static final int MDM_OS_TYPE_MAC = 0x02;
	private static final int MDM_OS_TYPE_SYMBIAN = 0x04;
	private static final int MDM_OS_TYPE_BLACKBERRY = 0x08;
	private static final int MDM_OS_TYPE_ANDROID = 0x10;
	private static final int MDM_OS_TYPE_WINDOWSPHONE = 0x20;

	@Column(length = 32)
	private String apiKey;

	@Column(length = 256)
	private String apiURL;
	@Column(length = 64)
	private String description;

	private int enableMdmOs = 0;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 32)
	private String mdmPassword;

	private int mdmType = MDM_ENROLL_TYPE_AIRWATCH;

	@Column(length = 32)
	private String mdmUserName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Column(length = 32)
	private String policyname;

	@Column(length = 256)
	private String rootURLPath;
	
	@Embedded
	private ConfigMDMAirWatchNonCompliance awNonCompliance = new ConfigMDMAirWatchNonCompliance();
	
	@Transient
	private boolean selected;
	@Version
	private Timestamp version;

	@Override
	public ConfigTemplateMdm clone() {
		try {
			return (ConfigTemplateMdm) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getApiURL() {
		return apiURL;
	}

	public String getDescription() {
		return description;
	}


	public int getEnableMdmOs() {
		return enableMdmOs;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {

		return policyname;
	}

	public String getMdmPassword() {
		return mdmPassword;
	}

	public int getMdmType() {
		return mdmType;
	}

	public String getMdmUserName() {
		return mdmUserName;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public String getPolicyname() {
		return policyname;
	}

	public String getRootURLPath() {
		return rootURLPath;
	}

	public Timestamp getVersion() {
		return version;
	}

	public boolean isEnableAndroidOs() {
		return (enableMdmOs & MDM_OS_TYPE_ANDROID) > 0;
	}

	public boolean isEnableAppleOs() {
		return (enableMdmOs & MDM_OS_TYPE_APPLE) > 0;
	}

	public boolean isEnableBlackberryOs() {
		return (enableMdmOs & MDM_OS_TYPE_BLACKBERRY) > 0;
	}

	public boolean isEnableMacOs() {
		return (enableMdmOs & MDM_OS_TYPE_MAC) > 0;
	}

	public boolean isEnableSymbianOs() {
		return (enableMdmOs & MDM_OS_TYPE_SYMBIAN) > 0;
	}
	
	public boolean isEnableWindowsphoneOs() {
		return (enableMdmOs & MDM_OS_TYPE_WINDOWSPHONE) > 0;
	}

	public String isOsObject() {
		StringBuffer str = new StringBuffer();
		if (isEnableAppleOs()) {
			str.append("iPod/iPhone/iPad,");
		}
		if (isEnableMacOs()) {
			str.append("MacOS,");
		}
		if (isEnableSymbianOs()) {
			str.append("Symbian,");
		}
		if(isEnableWindowsphoneOs()){
			str.append("Windows Phone,");
		}
		if (isEnableBlackberryOs()) {
			str.append("BlackBerry,");
		}
		if (isEnableAndroidOs()) {
			str.append("Android,");
		}
		String osstr = str.toString();
		if (osstr != "" && osstr != null) {
			return osstr.substring(0, osstr.length() - 1);
		} else {
			return "";
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL==null?"":apiURL.trim();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEnableAndroidOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_ANDROID);
	}

	public void setEnableAppleOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_APPLE);
	}

	public void setEnableBlackberryOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_BLACKBERRY);
	}

	public void setEnableMacOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_MAC);
	}

	private void setEnableMdmOs(boolean enable, int osType) {
		enableMdmOs = enable ? (enableMdmOs | osType) : (enableMdmOs & ~osType);
	}

	public void setEnableMdmOs(int enableMdmOs) {
		this.enableMdmOs = enableMdmOs;
	}

	public void setEnableSymbianOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_SYMBIAN);
	}
	
	public void setEnableWindowsphoneOs(boolean enable) {
		setEnableMdmOs(enable, MDM_OS_TYPE_WINDOWSPHONE);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMdmPassword(String mdmPassword) {
		this.mdmPassword = mdmPassword;
	}

	public void setMdmType(int mdmType) {
		this.mdmType = mdmType;
	}

	public void setMdmUserName(String mdmUserName) {
		this.mdmUserName = mdmUserName;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public void setPolicyname(String policyname) {
		this.policyname = policyname;
	}

	public void setRootURLPath(String rootURLPath) {
		this.rootURLPath = rootURLPath == null?"":rootURLPath.trim();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

    public ConfigMDMAirWatchNonCompliance getAwNonCompliance() {
        return awNonCompliance;
    }

    public void setAwNonCompliance(ConfigMDMAirWatchNonCompliance awNonCompliance) {
        this.awNonCompliance = awNonCompliance;
    }

}
