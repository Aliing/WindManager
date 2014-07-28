package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.common.NmsUtil;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HIVE_AP_UPDATE_SETTINGS")
public class HiveApUpdateSettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	public enum ActivateType {
		activateNextTime, activateAfterTime, activateAtTime
	}

	public enum TransferType {
		tftp, scp
	}

	public enum ImageSelectionType {
		softVer, imgName
	}

	public enum ConfigSelectType {
		full, deltaConfig, deltaRunning, auto
	}

	public static final short CONNECT_TYPE_LOCAL = 0;
	public static final short CONNECT_TYPE_128K = 1;
	public static final short CONNECT_TYPE_256K = 2;
	public static final short CONNECT_TYPE_1500K = 3;
	public static final short CONNECT_TYPE_2000K = 4;

	public static EnumItem[] CONNECT_TYPE = MgrUtil.enumItems(
			"enum.connect.type.", new int[] { CONNECT_TYPE_128K,
					CONNECT_TYPE_256K, CONNECT_TYPE_1500K, CONNECT_TYPE_2000K,
					CONNECT_TYPE_LOCAL });

	/* Image settings */
	@Enumerated(EnumType.STRING)
	private ActivateType imageActivateType = ActivateType.activateNextTime;

	@Enumerated(EnumType.STRING)
	private TransferType imageTransfer = TransferType.scp;

	@Enumerated(EnumType.STRING)
	private ImageSelectionType imageSelectType = ImageSelectionType.imgName;

	private long imageActivateTime;

	public static final long DEFAULT_IMAGE_ACTIVATE_OFFSET = 5;

	private long imageActivateOffset = 5; // seconds

	public static final long DEFAULT_IMAGE_TIME_OUT = 15;

	private long imageTimedout = 15; // minutes

	private short imageConnType = CONNECT_TYPE_LOCAL;
	
	private boolean distributedUpgrades;
	
	/* L7 signature settings */
	@Enumerated(EnumType.STRING)
	private ImageSelectionType signatureSelectType = ImageSelectionType.imgName;
	
	private long signatureTimedout = 15; // minutes
	
	private short signatureConnType = CONNECT_TYPE_LOCAL;

	/* Configuration settings */
	@Enumerated(EnumType.STRING)
	private ActivateType configActivateType = ActivateType.activateNextTime;

	@Enumerated(EnumType.STRING)
	private ConfigSelectType configSelectType = ConfigSelectType.auto;

	private long configActivateTime;

	private long configActivateOffset = 5; // seconds

	private boolean configConfiguration = true;

	private boolean configCwp = true;

	private boolean configCertificate = true;

	private boolean configUserDatabase = true;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public ActivateType getImageActivateType() {
		return imageActivateType;
	}

	public void setImageActivateType(ActivateType imageActivateType) {
		this.imageActivateType = imageActivateType;
	}

	public TransferType getImageTransfer() {
		return imageTransfer;
	}

	public void setImageTransfer(TransferType imageTransfer) {
		this.imageTransfer = imageTransfer;
	}

	public ImageSelectionType getImageSelectType() {
		return imageSelectType;
	}

	public void setImageSelectType(ImageSelectionType imageSelectType) {
		this.imageSelectType = imageSelectType;
	}

	public long getImageActivateTime() {
		return imageActivateTime;
	}

	public void setImageActivateTime(long imageActivateTime) {
		this.imageActivateTime = imageActivateTime;
	}

	public long getImageActivateOffset() {
		return imageActivateOffset;
	}

	public void setImageActivateOffset(long imageActivateOffset) {
		this.imageActivateOffset = imageActivateOffset;
	}

	public long getImageTimedout() {
		return imageTimedout;
	}

	public void setImageTimedout(long imageTimedout) {
		this.imageTimedout = imageTimedout;
	}

	public short getImageConnType() {
		return imageConnType;
	}

	public void setImageConnType(short imageConnType) {
		this.imageConnType = imageConnType;
	}

	public ActivateType getConfigActivateType() {
		return configActivateType;
	}

	public void setConfigActivateType(ActivateType configActivateType) {
		this.configActivateType = configActivateType;
	}

	public ConfigSelectType getConfigSelectType() {
		return configSelectType;
	}

	public void setConfigSelectType(ConfigSelectType configSelectType) {
		this.configSelectType = configSelectType;
	}

	public long getConfigActivateTime() {
		return configActivateTime;
	}

	public void setConfigActivateTime(long configActivateTime) {
		this.configActivateTime = configActivateTime;
	}

	public long getConfigActivateOffset() {
		return configActivateOffset;
	}

	public void setConfigActivateOffset(long configActivateOffset) {
		this.configActivateOffset = configActivateOffset;
	}

	public boolean isConfigConfiguration() {
		return configConfiguration;
	}

	public void setConfigConfiguration(boolean configConfiguration) {
		this.configConfiguration = configConfiguration;
	}

	public boolean isConfigCwp() {
		return configCwp;
	}

	public void setConfigCwp(boolean configCwp) {
		this.configCwp = configCwp;
	}

	public boolean isConfigCertificate() {
		return configCertificate;
	}

	public void setConfigCertificate(boolean configCertificate) {
		this.configCertificate = configCertificate;
	}

	public boolean isConfigUserDatabase() {
		return configUserDatabase;
	}

	public void setConfigUserDatabase(boolean configUserDatabase) {
		this.configUserDatabase = configUserDatabase;
	}
	
	public boolean isDistributedUpgrades(){
		return this.distributedUpgrades;
	}
	
	public void setDistributedUpgrades(boolean distributedUpgrades){
		this.distributedUpgrades = distributedUpgrades;
	}

	public ImageSelectionType getSignatureSelectType() {
		return signatureSelectType;
	}

	public void setSignatureSelectType(ImageSelectionType signatureSelectType) {
		this.signatureSelectType = signatureSelectType;
	}

	public long getSignatureTimedout() {
		return signatureTimedout;
	}

	public void setSignatureTimedout(long signatureTimedout) {
		this.signatureTimedout = signatureTimedout;
	}

	public short getSignatureConnType() {
		return signatureConnType;
	}

	public void setSignatureConnType(short signatureConnType) {
		this.signatureConnType = signatureConnType;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return this.owner.getDomainName() + " update settings";
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
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
	public String getImageActivateTimeString() {
		String value = null;
		if (ActivateType.activateAtTime.equals(imageActivateType)) {
			if (imageActivateTime == 0) {
				imageActivateTime = System.currentTimeMillis();
			}
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
			value = sf.format(new Date(imageActivateTime));
		} else if (ActivateType.activateAfterTime.equals(imageActivateType)) {
			value = NmsUtil.getCLIFormatString((int) imageActivateOffset
					+ BeTopoModuleParameters.DEFAULT_REBOOT_DELAY);
		}
		return value;
	}
	
	@Transient
	//for fix bug 16604, BR100 upload image auto reboot
	public String getBr100ImageActivateTimeString() {
		return NmsUtil.getCLIFormatString((int) 5
				+ BeTopoModuleParameters.DEFAULT_REBOOT_DELAY);
	}

	@Transient
	public String getImageActivateTimeHtmlString() {
		String str = "";
		if (ActivateType.activateNextTime.equals(imageActivateType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.activateNext");
		} else if (ActivateType.activateAtTime.equals(imageActivateType)) {
			boolean exp = imageActivateTime < System.currentTimeMillis();
			String value = getImageActivateTimeString();
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.activateAt")
					+ " (" + value + ")";
			if (exp) {
				str = "<font color='red'>" + str + " (Overdue)" + "</font>";
			}
		} else if (ActivateType.activateAfterTime.equals(imageActivateType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.activateAfter")
					+ " " + imageActivateOffset + " seconds";
		}
		return str;
	}
	
	@Transient
	public String getBr100ImageActivateTimeHtmlString() {
		return MgrUtil
				.getUserMessage("hiveAp.update.configuration.activateAfter")
				+ " " + "5 seconds";
	}

	@Transient
	public String getConfigActivateTimeString() {
		String value = null;
		if (ActivateType.activateAtTime.equals(configActivateType)) {
			if (configActivateTime == 0) {
				configActivateTime = System.currentTimeMillis();
			}
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
			value = sf.format(new Date(configActivateTime));
		} else if (ActivateType.activateAfterTime.equals(configActivateType)) {
			value = NmsUtil.getCLIFormatString((int) configActivateOffset
					+ BeTopoModuleParameters.DEFAULT_REBOOT_DELAY);
		}
		return value;
	}

	@Transient
	public String getConfigActivateTimeHtmlString() {
		String str = "";
		if (ActivateType.activateNextTime.equals(configActivateType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.wizard.activateNext");
		} else if (ActivateType.activateAtTime.equals(configActivateType)) {
			boolean exp = configActivateTime < System.currentTimeMillis();
			String value = getConfigActivateTimeString();
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.wizard.activateAt")
					+ " " + value;
			if (exp) {
				str = "<font color='red'>" + str + " (Overdue)" + "</font>";
			}
		} else if (ActivateType.activateAfterTime.equals(configActivateType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.wizard.activateAfter")
					+ " " + configActivateOffset + " seconds";
		}
		return str;
	}

	@Transient
	public String getUploadTypeHtmlString() {
		String str = "";
		if (ConfigSelectType.full.equals(configSelectType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.uploadType.full");
		} else if (ConfigSelectType.deltaConfig.equals(configSelectType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.uploadType.deltaConfig");
		} else if (ConfigSelectType.deltaRunning.equals(configSelectType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.uploadType.deltaRunning");
		} else if (ConfigSelectType.auto.equals(configSelectType)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.configuration.uploadType.auto");
		}
		return str;
	}

	@Transient
	public String getConfigItemHtmlString() {
		String str = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">"
				+ "<tr>"
				+ "<td><input type=\"checkbox\" name=\"configConfiguration\" id=\"_configConfiguration\" "
				+ (configConfiguration ? "checked=\"checked\" " : "")
				+ "value=\"true\"/></td>"
				+ "<td>"
				+ MgrUtil
						.getUserMessage("hiveAp.update.configuration.item.configuration")
				+ "</td>"
				+ "</tr>"
				+ "<tr><td height=\"5px\"></td></tr>"
				+ "<tr>"
				+ "<td><input type=\"checkbox\" name=\"configCwp\" id=\"_configCwp\" "
				+ (configCwp ? "checked=\"checked\" " : "")
				+ "value=\"true\"/></td>"
				+ "<td>"
				+ MgrUtil
						.getUserMessage("hiveAp.update.configuration.item.cwp")
				+ "</td>"
				+ "</tr>"
				+ "<tr><td height=\"5px\"></td></tr>"
				+ "<tr>"
				+ "<td><input type=\"checkbox\" name=\"configCertificate\" id=\"_configCertificate\" "
				+ (configCertificate ? "checked=\"checked\" " : "")
				+ "value=\"true\"/></td>"
				+ "<td>"
				+ MgrUtil
						.getUserMessage("hiveAp.update.configuration.item.certificate")
				+ "</td>"
				+ "</tr>"
				+ "<tr><td height=\"5px\"></td></tr>"
				+ "<tr>"
				+ "<td><input type=\"checkbox\" name=\"configUserDatabase\" id=\"_configUserDatabase\" "
				+ (configUserDatabase ? "checked=\"checked\" " : "")
				+ "value=\"true\"/></td>"
				+ "<td>"
				+ MgrUtil
						.getUserMessage("hiveAp.update.configuration.item.credential")
				+ "</td>" + "</tr>" + "</table>";
		return str;
	}

	@Transient
	public String getProtocolString() {
		String str = "";
		if (TransferType.scp.equals(imageTransfer)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.images.transfer.type.scp");
		} else if (TransferType.tftp.equals(imageTransfer)) {
			str = MgrUtil
					.getUserMessage("hiveAp.update.images.transfer.type.tftp");
		}
		return str;
	}

	@Transient
	public String getConnectionString() {
		switch (imageConnType) {
		case CONNECT_TYPE_LOCAL:
		case CONNECT_TYPE_128K:
		case CONNECT_TYPE_1500K:
		case CONNECT_TYPE_2000K:
		case CONNECT_TYPE_256K:
			return MgrUtil.getEnumString("enum.connect.type." + imageConnType);
		default:
			return "";
		}
	}

	@Transient
	public int getConnectionLimit() {
		switch (imageConnType) {
		case CONNECT_TYPE_128K:
			return 128;
		case CONNECT_TYPE_1500K:
			return 1500;
		case CONNECT_TYPE_2000K:
			return 2000;
		case CONNECT_TYPE_256K:
			return 256;
		case CONNECT_TYPE_LOCAL:
		default:
			return 0;
		}
	}

	@Transient
	public String getTimedoutString() {
		return imageTimedout + " minutes";
	}

	@Transient
	public boolean isTftpImageTransferType() {
		return TransferType.tftp.equals(this.imageTransfer);
	}

/* signature setting values */
	@Transient
	public String getSignatureConnectionString() {
		switch (signatureConnType) {
		case CONNECT_TYPE_LOCAL:
		case CONNECT_TYPE_128K:
		case CONNECT_TYPE_1500K:
		case CONNECT_TYPE_2000K:
		case CONNECT_TYPE_256K:
			return MgrUtil.getEnumString("enum.connect.type." + signatureConnType);
		default:
			return "";
		}
	}

	@Transient
	public int getSignatureConnectionLimit() {
		switch (signatureConnType) {
		case CONNECT_TYPE_128K:
			return 128;
		case CONNECT_TYPE_1500K:
			return 1500;
		case CONNECT_TYPE_2000K:
			return 2000;
		case CONNECT_TYPE_256K:
			return 256;
		case CONNECT_TYPE_LOCAL:
		default:
			return 0;
		}
	}

	@Transient
	public String getSignatureTimedoutString() {
		return signatureTimedout + " minutes";
	}
}