package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * 
 * @author fhu
 * 
 */
@Entity
@Table(name = "LSEVEN_SIGNATURE_INFO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LSevenSignatures implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_SPNSOR_LENGTH, nullable = false, unique = true)
	private String fileName;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String ahVersion;

	@Column(length = 8)
	private String dateReleased;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String vendorVersion;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String vendorId;

	public static final short PACKAGE_TYPE_FULL = 1;
	public static final short PACKAGE_TYPE_PATCH = 2;
	public static EnumItem[] PACKAGE_TYPE = MgrUtil.enumItems(
			"enum.l7.signature.package.type.", new int[] { PACKAGE_TYPE_FULL,
					PACKAGE_TYPE_PATCH });
	private short packageType;

	public static final short PLATFORM_1 = 1;
	public static final short PLATFORM_2 = 2;
	public static final short PLATFORM_3 = 3;
	public static final short PLATFORM_4 = 4;
	public static final short PLATFORM_5 = 5;
	public static EnumItem[] PLATFORM_TYPE = MgrUtil.enumItems(
			"enum.l7.signature.platform.", new int[] { PLATFORM_1, PLATFORM_2,
					PLATFORM_3, PLATFORM_4, PLATFORM_5 });
	private short platformId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAhVersion() {
		return ahVersion;
	}

	public void setAhVersion(String ahVersion) {
		this.ahVersion = ahVersion;
	}

	public String getDateReleased() {
		return dateReleased;
	}

	public void setDateReleased(String dateReleased) {
		this.dateReleased = dateReleased;
	}

	public String getVendorVersion() {
		return vendorVersion;
	}

	public void setVendorVersion(String vendorVersion) {
		this.vendorVersion = vendorVersion;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public short getPackageType() {
		return packageType;
	}

	public void setPackageType(short packageType) {
		this.packageType = packageType;
	}

	public short getPlatformId() {
		return platformId;
	}

	public void setPlatformId(short platformId) {
		this.platformId = platformId;
	}

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
		return this.fileName;
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
	public boolean isPatchPackage(){
		return this.packageType == LSevenSignatures.PACKAGE_TYPE_PATCH;
	}
	
	@Transient
	public boolean isSupportVersionUpdate(String version) {
		if (!isPatchPackage()) {
			return true;
		}
		if (StringUtils.isEmpty(version)) {
			return false;
		}
		String[] currentValues = version.split("\\.");
		String[] configValues = ahVersion.split("\\.");
		int currentMajor = Integer.parseInt(currentValues[0]);
		int currentMinor = Integer.parseInt(currentValues[1]);
		int currentMinor2 = Integer.parseInt(currentValues[2]);
		int configMajor = Integer.parseInt(configValues[0]);
		int configMinor = Integer.parseInt(configValues[1]);
		int configMinor2 = Integer.parseInt(configValues[2]);
		if (currentMajor != configMajor) {
			return false;
		}
		if (currentMinor > configMinor) {
			return false;
		}
		if (currentMinor2 > configMinor2) {
			return false;
		}
		return true;
	}

	@Transient
	public String getPlatformIdString() {
		switch (this.platformId) {
		case PLATFORM_1:
		case PLATFORM_2:
		case PLATFORM_3:
		case PLATFORM_4:
		case PLATFORM_5:
			return MgrUtil.getEnumString("enum.l7.signature.platform."
					+ platformId);
		default:
			return "";
		}
	}

	@Transient
	public String getPackageTypeString() {
		switch (this.packageType) {
		case PACKAGE_TYPE_FULL:
		case PACKAGE_TYPE_PATCH:
			return MgrUtil.getEnumString("enum.l7.signature.package.type."
					+ packageType);
		default:
			return "";
		}
	}

	@Transient
	public String getDPIEngineVersion() {
		String[] configValues = ahVersion.split("\\.");
		int configMajor = Integer.parseInt(configValues[0]);
		return "v" + configMajor;
	}
	
	@Transient
	public boolean isPreFujiSignatureVersion() {
		String[] configValues = ahVersion.split("\\.");
		int configMajor = Integer.parseInt(configValues[0]);
		return configMajor < 3;
	}
	

	/**
	 * Check whether current L7 Signature information suitable for specified
	 * signature version.
	 * 
	 * @param currentVer
	 *            current signature version run on HiveAp object
	 * @return
	 */
	@Transient
	public boolean isMatchDeviceSignatureVersion(String currentVer) {
		if (StringUtils.isEmpty(currentVer)) {
			return false;
		}
		String[] currentValues = currentVer.split("\\.");
		String[] configValues = ahVersion.split("\\.");
		int currentMajor = Integer.parseInt(currentValues[0]);
		int configMajor = Integer.parseInt(configValues[0]);
		if (currentMajor < 3) {
			// HOS run version lower than 6.1r1, only support major version < 3
			return configMajor < 3;
		} else {
			// major version must match, which indicates compatible
			return currentMajor == configMajor;
		}
	}

	/**
	 * Check whether current L7 Signature information suitable for specified device platform.
	 * @param deviceModel device platform defined in HiveAp object
	 * @return
	 */
	@Transient
	public boolean isMatchDevicePlatform(short deviceModel) {
		boolean matched = false;
		switch (platformId) {
		case PLATFORM_1:
			matched = deviceModel == HiveAp.HIVEAP_MODEL_121
					|| deviceModel == HiveAp.HIVEAP_MODEL_141;
			break;
		case PLATFORM_2:
			matched = deviceModel == HiveAp.HIVEAP_MODEL_330
					|| deviceModel == HiveAp.HIVEAP_MODEL_350
					|| deviceModel == HiveAp.HIVEAP_MODEL_370
					|| deviceModel == HiveAp.HIVEAP_MODEL_390			
					|| deviceModel == HiveAp.HIVEAP_MODEL_BR200
					|| deviceModel == HiveAp.HIVEAP_MODEL_BR200_WP
					|| deviceModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ;
			break;
		case PLATFORM_3:
			matched = deviceModel == HiveAp.HIVEAP_MODEL_320
					|| deviceModel == HiveAp.HIVEAP_MODEL_340;
			break;
		case PLATFORM_4:
			matched = deviceModel == HiveAp.HIVEAP_MODEL_110
					|| deviceModel == HiveAp.HIVEAP_MODEL_120
					|| deviceModel == HiveAp.HIVEAP_MODEL_170;
			break;
		case PLATFORM_5:
			matched = deviceModel == HiveAp.HIVEAP_MODEL_230;
		}
		return matched;
	}

	public static String getDateReleasedString(String dateReleased) {
		String dateString = dateReleased;
		try {
			DateFormat format = new SimpleDateFormat("MMddyyyy");
			Date date = format.parse(dateReleased);
			format = new SimpleDateFormat("MM/dd/yyyy");
			dateString = format.format(date);
		} catch (Exception e) {
			//
		}
		return dateString;
	}

	public static String getAllSupportedPlatformsString() {
		return MgrUtil
				.getEnumString("enum.l7.signature.platform." + PLATFORM_1)
				+ ", "
				+ MgrUtil.getEnumString("enum.l7.signature.platform."
						+ PLATFORM_2)
				+ ", "
				/*+ MgrUtil.getEnumString("enum.l7.signature.platform."
						+ PLATFORM_3)
				+ ", "*/
				+ MgrUtil.getEnumString("enum.l7.signature.platform."
						+ PLATFORM_4)
				+ ", "
				+ MgrUtil.getEnumString("enum.l7.signature.platform."
						+ PLATFORM_5);
	}

	public static short[] getAllSuppportedPlatform() {
		return new short[] { HiveAp.HIVEAP_MODEL_121, HiveAp.HIVEAP_MODEL_141,
				HiveAp.HIVEAP_MODEL_330, HiveAp.HIVEAP_MODEL_350,
				HiveAp.HIVEAP_MODEL_BR200, HiveAp.HIVEAP_MODEL_BR200_WP,HiveAp.HIVEAP_MODEL_BR200_LTE_VZ,
				HiveAp.HIVEAP_MODEL_320, HiveAp.HIVEAP_MODEL_340,
				HiveAp.HIVEAP_MODEL_110, HiveAp.HIVEAP_MODEL_120,
				HiveAp.HIVEAP_MODEL_170, HiveAp.HIVEAP_MODEL_370, 
				HiveAp.HIVEAP_MODEL_390, HiveAp.HIVEAP_MODEL_230 };
	}
}
