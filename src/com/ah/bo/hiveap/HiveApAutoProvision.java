package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.IpAddress;
import com.ah.bo.wlan.RadioProfile;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.devices.impl.Device;

@Entity
@Table(name = "HIVE_AP_AUTO_PROVISION", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "NAME" }) })
@org.hibernate.annotations.Table(appliesTo = "HIVE_AP_AUTO_PROVISION", indexes = {
		@Index(name = "HIVE_AP_AUTO_PROVISION_OWNER", columnNames = { "OWNER" })
		})
public class HiveApAutoProvision implements HmBo {

	private static final long serialVersionUID = 1L;

	public static final String HIVEOS_IMAGE_NOT_FOUND = "<b style='color: red; font-style: italic;'>&lt;HiveOS Image not found.&gt;<b></b></b>";

//	public static final String PROVISION_NAME_20 = "ProvisionForAg20";
//	public static final String PROVISION_NAME_28 = "ProvisionForAg28";
//	public static final String PROVISION_NAME_320 = "ProvisionFor320";
//	public static final String PROVISION_NAME_340 = "ProvisionFor340";
//	public static final String PROVISION_NAME_380 = "ProvisionFor380";
//	public static final String PROVISION_NAME_120 = "ProvisionFor120";
//	public static final String PROVISION_NAME_110 = "ProvisionFor110";
//	public static final String PROVISION_NAME_VI = "ProvisionForVI";

	public static Map<Integer, String> hiveApModelMap;

	public static Map<Integer, String> hiveApDeviceTypeMap;

	public static String getProvisionName(short hiveApModel, short deviceType){
		if(hiveApModelMap == null){
			hiveApModelMap = new HashMap<Integer, String>(HiveAp.HIVEAP_MODEL.length);
			for(int i=0; i<HiveAp.HIVEAP_MODEL.length; i++){
				hiveApModelMap.put(HiveAp.HIVEAP_MODEL[i].getKey(), HiveAp.HIVEAP_MODEL[i].getValue());
			}
		}
		if(hiveApDeviceTypeMap == null){
			hiveApDeviceTypeMap = new HashMap<Integer, String>(HiveAp.DEVICE_TYPE.length);
			for(int i=0; i<HiveAp.DEVICE_TYPE.length; i++){
				hiveApDeviceTypeMap.put(HiveAp.DEVICE_TYPE[i].getKey(), HiveAp.DEVICE_TYPE[i].getValue().replaceAll(" ", ""));
			}
		}
		String prefix = "ProvisionFor";
		return prefix + hiveApModelMap.get((int)hiveApModel)+hiveApDeviceTypeMap.get((int)deviceType);
	}

	// initialize some fields
	public HiveApAutoProvision() {
		getEth0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		getEth1().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		getRed0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		getAgg0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		getWifi0().setOperationMode(AhInterface.OPERATION_MODE_ACCESS);
		getWifi0().setRadioMode(AhInterface.RADIO_MODE_BG);
		getWifi0().setPower(AhInterface.POWER_AUTO);
		getWifi1().setOperationMode(AhInterface.OPERATION_MODE_ACCESS);
		getWifi1().setRadioMode(AhInterface.RADIO_MODE_A);
		getWifi1().setPower(AhInterface.POWER_AUTO);
	}

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length=36)
	private String provisioningName;

	private short modelType = HiveAp.HIVEAP_MODEL_20;

	private int countryCode = CountryCode.COUNTRY_CODE_US;

	private boolean autoProvision;

	private boolean uploadImage;

	private boolean uploadConfig;

	public static List<TextItem> getImageVersions(short model) {
		List<TextItem> versions = new ArrayList<TextItem>();
		for (String imageVersion : (String[])AhConstantUtil.getEnumValues(Device.SUPPORTED_HIVEOS_VERSIONS, model)) {
			TextItem item = new TextItem(imageVersion, MgrUtil
					.getHiveOSDisplayVersion(imageVersion));
			versions.add(item);
		}
		return versions;
	}

	public String getNewestVersion() {
		return getImageVersions(modelType).get(0).getKey();
	}

	@Column(length = 15)
	private String imageVersion = getImageVersions(modelType).get(0).getKey();

	@SuppressWarnings("unused")
	private String imageName;

	private boolean rewriteMap;

	private Long configTemplateId;

	private Long mapContainerId;

	private Long wifi0ProfileId;

	private Long wifi1ProfileId;

	// private Long lldpProfileId;

	private Long cfgCapwapIpId;

	private Long capwapBackupIpId;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String passPhrase;

	@Column(length = 20)
	private String cfgAdminUser;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String cfgPassword;

	@Column(length = 20)
	private String cfgReadOnlyUser;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String cfgReadOnlyPassword;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "ETH0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "ETH0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "ETH0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "ETH0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "ETH0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "ETH0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "ETH0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "ETH0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "ETH0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "ETH0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "ETH0_MULTINATIVE_VLAN"))
			})
	private HiveApEth eth0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "ETH1_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "ETH1_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "ETH1_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "ETH1_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "ETH1_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "ETH1_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "ETH1_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "ETH1_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "ETH1_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "ETH1_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "ETH1_MULTINATIVE_VLAN"))
			})
	private HiveApEth eth1 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "RED0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "RED0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "RED0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "RED0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "RED0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "RED0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "RED0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "RED0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "RED0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "RED0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "RED0_MULTINATIVE_VLAN"))
			})
	private HiveApEth red0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "AGG0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "AGG0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "AGG0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "AGG0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "AGG0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "AGG0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "AGG0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "AGG0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "AGG0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "AGG0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "AGG0_MULTINATIVE_VLAN"))
			})
	private HiveApEth agg0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "WIFI0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "WIFI0_OPERATION_MODE")),
			@AttributeOverride(name = "radioMode", column = @Column(name = "WIFI0_RADIO_MODE")),
			@AttributeOverride(name = "channel", column = @Column(name = "WIFI0_RADIO_CHANNEL")),
			@AttributeOverride(name = "power", column = @Column(name = "WIFI0_RADIO_POWER")) })
	private HiveApWifi wifi0 = new HiveApWifi();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "WIFI1_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "WIFI1_OPERATION_MODE")),
			@AttributeOverride(name = "radioMode", column = @Column(name = "WIFI1_RADIO_MODE")),
			@AttributeOverride(name = "channel", column = @Column(name = "WIFI1_RADIO_CHANNEL")),
			@AttributeOverride(name = "power", column = @Column(name = "WIFI1_RADIO_POWER")) })
	private HiveApWifi wifi1 = new HiveApWifi();

	private boolean rebooting;

	private boolean accessControled;

	public static final short ACL_MANUAL_AP = 0;
	public static final short ACL_IMPORT_SN = 1;

	private short aclType = ACL_MANUAL_AP;

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "macAddress", length=14)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_AUTO_PROVISION_MACES", joinColumns = @JoinColumn(name = "HIVE_AP_AUTO_PROVISION_ID", nullable = true))
	private List<String> macAddresses;

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "ipSubNetwork", length=18)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DEVICE_AUTO_PROVISION_IPSUBNETWORKS", joinColumns = @JoinColumn(name = "DEVICE_AUTO_PROVISION_ID", nullable = true))
	private List<String> ipSubNetworks;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DEVICE_AUTO_PROVISION_INTERFACE", joinColumns = @JoinColumn(name = "DEVICE_AUTO_PROVISION_ID", nullable = false))
	private List<AutoProvisionDeviceInterface> deviceInterfaces;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String name;

	@Column(length=255)
	private String description;

	private short deviceType;

	@Column(length=64)
	private String classificationTag1;

	@Column(length=64)
	private String classificationTag2;

	@Column(length=64)
	private String classificationTag3;

	// indicate if include topology info in sysLocation
	private boolean includeTopologyInfo = true;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
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
	public String getLabel() {
		return name;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getProvisioningName() {
		return provisioningName;
	}

	public void setProvisioningName(String provisioningName) {
		this.provisioningName = provisioningName;
		if (this.provisioningName != null
				&& this.provisioningName.length() > 36) {
			this.provisioningName = this.provisioningName.substring(0, 36);
		}
	}

	public boolean isAutoProvision() {
		return autoProvision;
	}

	public void setAutoProvision(boolean autoProvision) {
		this.autoProvision = autoProvision;
	}

	public boolean isUploadImage() {
		return uploadImage;
	}

	public void setUploadImage(boolean uploadImage) {
		this.uploadImage = uploadImage;
	}

	public boolean isUploadConfig() {
		return uploadConfig;
	}

	public void setUploadConfig(boolean uploadConfig) {
		this.uploadConfig = uploadConfig;
	}

	public String getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(String imageVersion) {
//		if (imageVersion != null && imageVersion.lastIndexOf(".") > -1
//				&& NmsUtil.compareSoftwareVersion("5.1.1.0", imageVersion) <= 0) {
//			imageVersion = imageVersion.substring(0, imageVersion.lastIndexOf(".")) + ".1";
//		}
		this.imageVersion = imageVersion;
	}

	public String getImageName() {
		// return imageName;
		// find image name from image table
		String in = null;
		if (this.uploadImage && !MgrUtil.isEnableDownloadServer()) {
			HiveApImageInfo imageInfo = com.ah.be.config.image.ImageManager.getLatestImageName(this.modelType, this.getHiveOsVerString());
			in = imageInfo == null ? null : imageInfo.getImageName();
		}
		return StringUtils.isEmpty(in) ? "" : in;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isRewriteMap() {
		return rewriteMap;
	}

	public void setRewriteMap(boolean rewriteMap) {
		this.rewriteMap = rewriteMap;
	}

	public Long getConfigTemplateId() {
		return configTemplateId;
	}

	public void setConfigTemplateId(Long configTemplateId) {
		this.configTemplateId = configTemplateId;
	}

	public Long getMapContainerId() {
		return mapContainerId;
	}

	public void setMapContainerId(Long mapContainerId) {
		this.mapContainerId = mapContainerId;
	}

	public boolean isRebooting() {
		return rebooting;
	}

	public void setRebooting(boolean rebooting) {
		this.rebooting = rebooting;
	}

	public short getModelType() {
		return modelType;
	}

	public void setModelType(short modelType) {
		this.modelType = modelType;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public Long getWifi0ProfileId() {
		return wifi0ProfileId;
	}

	public void setWifi0ProfileId(Long wifi0ProfileId) {
		this.wifi0ProfileId = wifi0ProfileId;
	}

	public Long getWifi1ProfileId() {
		return wifi1ProfileId;
	}

	public void setWifi1ProfileId(Long wifi1ProfileId) {
		this.wifi1ProfileId = wifi1ProfileId;
	}

	// public Long getLldpProfileId() {
	// return lldpProfileId;
	// }
	//
	// public void setLldpProfileId(Long lldpProfileId) {
	// this.lldpProfileId = lldpProfileId;
	// }

	public Long getCfgCapwapIpId() {
		return cfgCapwapIpId;
	}

	public void setCfgCapwapIpId(Long cfgCapwapIpId) {
		this.cfgCapwapIpId = cfgCapwapIpId;
	}

	public Long getCapwapBackupIpId() {
		return capwapBackupIpId;
	}

	public void setCapwapBackupIpId(Long capwapBackupIpId) {
		this.capwapBackupIpId = capwapBackupIpId;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public String getCfgPassword() {
		return cfgPassword;
	}

	public void setCfgPassword(String cfgPassword) {
		this.cfgPassword = cfgPassword;
	}

	public String getCfgAdminUser() {
		return cfgAdminUser;
	}

	public void setCfgAdminUser(String cfgAdminUser) {
		this.cfgAdminUser = cfgAdminUser;
	}

	public String getCfgReadOnlyUser() {
		return cfgReadOnlyUser;
	}

	public void setCfgReadOnlyUser(String cfgReadOnlyUser) {
		this.cfgReadOnlyUser = cfgReadOnlyUser;
	}

	public String getCfgReadOnlyPassword() {
		return cfgReadOnlyPassword;
	}

	public void setCfgReadOnlyPassword(String cfgReadOnlyPassword) {
		this.cfgReadOnlyPassword = cfgReadOnlyPassword;
	}

	public HiveApEth getEth0() {
		return eth0;
	}

	public void setEth0(HiveApEth eth0) {
		this.eth0 = eth0;
	}

	public HiveApEth getEth1() {
		return eth1;
	}

	public void setEth1(HiveApEth eth1) {
		this.eth1 = eth1;
	}

	public HiveApEth getRed0() {
		return red0;
	}

	public void setRed0(HiveApEth red0) {
		this.red0 = red0;
	}

	public HiveApEth getAgg0() {
		return agg0;
	}

	public void setAgg0(HiveApEth agg0) {
		this.agg0 = agg0;
	}

	public HiveApWifi getWifi0() {
		return wifi0;
	}

	public void setWifi0(HiveApWifi wifi0) {
		this.wifi0 = wifi0;
	}

	public HiveApWifi getWifi1() {
		return wifi1;
	}

	public void setWifi1(HiveApWifi wifi1) {
		this.wifi1 = wifi1;
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

	@Transient
	public ConfigTemplate getConfigTemplate() {
		ConfigTemplate template = null;
		if (null != configTemplateId && configTemplateId > 0) {
			template = QueryUtil.findBoById(ConfigTemplate.class,
					configTemplateId);
		}
		if (null == template) {
			template = HmBeParaUtil.getDefaultProfile(
					ConfigTemplate.class, null);
		}
		return template;
	}

	@Transient
	public RadioProfile getWifi0RadioProfile() {
		short profileType;
		if (HiveAp.is11nHiveAP(modelType)) {
			profileType = RadioProfile.RADIO_PROFILE_MODE_NG;
		} else {
			profileType = RadioProfile.RADIO_PROFILE_MODE_BG;
		}
		return getRadioProfile(wifi0ProfileId, profileType);
	}

	@Transient
	public RadioProfile getWifi1RadioProfile() {
		short profileType;
		if (HiveAp.is11acHiveAP(modelType)) {
			profileType = RadioProfile.RADIO_PROFILE_MODE_AC;
		} else if(HiveAp.is11nHiveAP(modelType)){
			profileType = RadioProfile.RADIO_PROFILE_MODE_NA;
		}else {
			profileType = RadioProfile.RADIO_PROFILE_MODE_A;
		}
		return getRadioProfile(wifi1ProfileId, profileType);
	}

	private RadioProfile getRadioProfile(Long profileId, short profileType) {
		RadioProfile profile = null;
		if (null != profileId && profileId > 0) {
			profile = QueryUtil.findBoById(RadioProfile.class, profileId);
		}
		if (null == profile) {
			Map<String, Object> term = new HashMap<String, Object>(1);
			term.put("radioMode", profileType);
			profile = HmBeParaUtil.getDefaultProfile(
					RadioProfile.class, term);
		}
		return profile;
	}

	@Transient
	public String getRadio1ProfileName() {
		if (HiveAp.isWifi1Available(modelType)) {
			RadioProfile radio1 = getWifi1RadioProfile();
			if (null != radio1) {
				return radio1.getRadioName();
			} else {
				return "";
			}
		}
		return "-";
	}

	@Transient
	public String getRadio0ProfileName() {
		if (HiveAp.isWifi0Available(modelType)) {
			RadioProfile radio0 = getWifi0RadioProfile();
			if (null != radio0) {
				return radio0.getRadioName();
			} else {
				return "";
			}
		}
		return "-";
	}

	@Transient
	public MapContainerNode getMapContainer() {
		MapContainerNode container = null;
		if (null != mapContainerId && mapContainerId > 0) {
			container = QueryUtil.findBoById(MapContainerNode.class,
					mapContainerId);
		}// if the container is not exist, just leave it no topo map.
		return container;
	}

	// @Transient
	// public LLDPCDPProfile getLLDPCDPProfile() {
	// LLDPCDPProfile lldpcdp = null;
	// if (null != lldpProfileId && lldpProfileId > 0) {
	// lldpcdp = QueryUtil.findBoById(
	// LLDPCDPProfile.class, lldpProfileId);
	// }
	// return lldpcdp;
	// }

	@Transient
	public IpAddress getCapwapIpAddress() {
		IpAddress capwapIp = null;
		if (null != cfgCapwapIpId && cfgCapwapIpId > 0) {
			capwapIp = QueryUtil.findBoById(IpAddress.class, cfgCapwapIpId, new QueryBo() {

                @Override
                public Collection<HmBo> load(HmBo bo) {
                    if (bo instanceof IpAddress) {
                        IpAddress ipAddress = (IpAddress) bo;
                        if (null != ipAddress.getItems()) ipAddress.getItems().size();
                    }
                    return null;
                }
            });
		}
		return capwapIp;
	}

	@Transient
	public IpAddress getCapwapBackupIpAddress() {
		IpAddress capwapIp = null;
		if (null != capwapBackupIpId && capwapBackupIpId > 0) {
			capwapIp = QueryUtil.findBoById(IpAddress.class, capwapBackupIpId, new QueryBo() {

                @Override
                public Collection<HmBo> load(HmBo bo) {
                    if (bo instanceof IpAddress) {
                        IpAddress ipAddress = (IpAddress) bo;
                        if (null != ipAddress.getItems()) ipAddress.getItems().size();
                    }
                    return null;
                }
            });
		}
		return capwapIp;
	}

	@Transient
	public String getAutoProvisionString() {
		if (autoProvision) {
			return "Yes";
		} else {
			return "No";
		}
	}

	@Transient
	public String getModelTypeString() {
		return HiveAp.getModelEnumString(modelType);
	}

	@Transient
	public String getDeviceTypeString() {
		return HiveAp.getDeviceEnumString(modelType, deviceType);
	}

	@Transient
	public String getUploadImageString() {
		if (uploadImage) {
			return "Enabled";
		} else {
			return "Disabled";
		}
	}

	@Transient
	public String getUploadConfigString() {
		if (uploadConfig) {
			return "Enabled";
		} else {
			return "Disabled";
		}
	}

	public boolean isAccessControled() {
		return accessControled;
	}

	public void setAccessControled(boolean accessControled) {
		this.accessControled = accessControled;
	}

	public List<String> getMacAddresses() {
		return macAddresses;
	}

	public void setMacAddresses(List<String> macAddresses) {
		this.macAddresses = macAddresses;
	}

	public short getAclType() {
		return aclType;
	}

	public void setAclType(short aclType) {
		this.aclType = aclType;
	}

	@Transient
	public String getHiveOsVerString() {
		if (null == imageVersion || "".equals(imageVersion)) {
			return "";
		}
		return MgrUtil.getHiveOSDisplayVersion(imageVersion);
	}

	@Transient
	private String capwapText;

	@Transient
	private String capwapBackupText;

	public String getCapwapText() {
		return capwapText;
	}

	public void setCapwapText(String capwapText) {
		this.capwapText = capwapText;
	}

	public String getCapwapBackupText() {
		return capwapBackupText;
	}

	public void setCapwapBackupText(String capwapBackupText) {
		this.capwapBackupText = capwapBackupText;
	}

	public List<String> getIpSubNetworks() {
		return ipSubNetworks;
	}

	public void setIpSubNetworks(List<String> ipSubNetworks) {
		this.ipSubNetworks = ipSubNetworks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
	}

	public String getClassificationTag1() {
		return classificationTag1;
	}

	public void setClassificationTag1(String classificationTag1) {
		this.classificationTag1 = classificationTag1;
	}

	public String getClassificationTag2() {
		return classificationTag2;
	}

	public void setClassificationTag2(String classificationTag2) {
		this.classificationTag2 = classificationTag2;
	}

	public String getClassificationTag3() {
		return classificationTag3;
	}

	public void setClassificationTag3(String classificationTag3) {
		this.classificationTag3 = classificationTag3;
	}

	public boolean isIncludeTopologyInfo() {
		return includeTopologyInfo;
	}

	public void setIncludeTopologyInfo(boolean includeTopologyInfo) {
		this.includeTopologyInfo = includeTopologyInfo;
	}

	public List<AutoProvisionDeviceInterface> getDeviceInterfaces() {
		if (deviceInterfaces == null) {
			deviceInterfaces = new ArrayList<AutoProvisionDeviceInterface>();
		}
		return deviceInterfaces;
	}

	public void setDeviceInterfaces(
			List<AutoProvisionDeviceInterface> deviceInterfaces) {
		this.deviceInterfaces = deviceInterfaces;
	}

	@Transient
	private String capwapConfigOptionDisplayStyle = "none";
	@Transient
	private String interfaceSettingOptionDisplayStyle = "none";
	@Transient
	private String advancedSettingOptionDisplayStyle = "none";

	public String getCapwapConfigOptionDisplayStyle() {
		return capwapConfigOptionDisplayStyle;
	}

	public void setCapwapConfigOptionDisplayStyle(
			String capwapConfigOptionDisplayStyle) {
		this.capwapConfigOptionDisplayStyle = capwapConfigOptionDisplayStyle;
	}

	public String getInterfaceSettingOptionDisplayStyle() {
		return interfaceSettingOptionDisplayStyle;
	}

	public void setInterfaceSettingOptionDisplayStyle(
			String interfaceSettingOptionDisplayStyle) {
		this.interfaceSettingOptionDisplayStyle = interfaceSettingOptionDisplayStyle;
	}

	public String getAdvancedSettingOptionDisplayStyle() {
		return advancedSettingOptionDisplayStyle;
	}

	public void setAdvancedSettingOptionDisplayStyle(
			String advancedSettingOptionDisplayStyle) {
		this.advancedSettingOptionDisplayStyle = advancedSettingOptionDisplayStyle;
	}

	@Transient
	public boolean isBranchRouter(){
		return this.deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER ||
				this.deviceType == HiveAp.Device_TYPE_VPN_BR;
	}

	@Transient
	public boolean isSwitch(){
		return this.deviceType == HiveAp.Device_TYPE_SWITCH;
	}
	
	@Transient
	public String getImageNameHtml() {
		String imageName = this.getImageName();
		return "" == imageName ? HIVEOS_IMAGE_NOT_FOUND : imageName;
	}

	@Transient
	private AutoProvisionDeviceInterface br100Eth0;
	@Transient
	private AutoProvisionDeviceInterface br100Eth1;
	@Transient
	private AutoProvisionDeviceInterface br100Eth2;
	@Transient
	private AutoProvisionDeviceInterface br100Eth3;
	@Transient
	private AutoProvisionDeviceInterface br100Eth4;
	@Transient
	private AutoProvisionDeviceInterface br100Usb;

	public AutoProvisionDeviceInterface getBr100Eth0() {
		return br100Eth0;
	}

	public void setBr100Eth0(AutoProvisionDeviceInterface br100Eth0) {
		this.br100Eth0 = br100Eth0;
	}

	public AutoProvisionDeviceInterface getBr100Eth1() {
		return br100Eth1;
	}

	public void setBr100Eth1(AutoProvisionDeviceInterface br100Eth1) {
		this.br100Eth1 = br100Eth1;
	}

	public AutoProvisionDeviceInterface getBr100Eth2() {
		return br100Eth2;
	}

	public void setBr100Eth2(AutoProvisionDeviceInterface br100Eth2) {
		this.br100Eth2 = br100Eth2;
	}

	public AutoProvisionDeviceInterface getBr100Eth3() {
		return br100Eth3;
	}

	public void setBr100Eth3(AutoProvisionDeviceInterface br100Eth3) {
		this.br100Eth3 = br100Eth3;
	}

	public AutoProvisionDeviceInterface getBr100Eth4() {
		return br100Eth4;
	}

	public void setBr100Eth4(AutoProvisionDeviceInterface br100Eth4) {
		this.br100Eth4 = br100Eth4;
	}

	public AutoProvisionDeviceInterface getBr100Usb() {
		return br100Usb;
	}

	public void setBr100Usb(AutoProvisionDeviceInterface br100Usb) {
		this.br100Usb = br100Usb;
	}

	private short usbConnectionModel = HiveAp.USB_CONNECTION_MODEL_NEEDED;

	public short getUsbConnectionModel() {
		return usbConnectionModel;
	}

	public void setUsbConnectionModel(short usbConnectionModel) {
		this.usbConnectionModel = usbConnectionModel;
	}

	private boolean enableOneTimePassword;

	public boolean isEnableOneTimePassword() {
		return enableOneTimePassword;
	}

	public void setEnableOneTimePassword(boolean enableOneTimePassword) {
		this.enableOneTimePassword = enableOneTimePassword;
	}
}