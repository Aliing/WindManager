package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.IdsPolicy;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "IDP", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"reportNodeId", "ifMacAddress" }) })
@org.hibernate.annotations.Table(appliesTo = "IDP", indexes = {
		@Index(name = "IDP_OWNER", columnNames = { "OWNER" }),
		@Index(name = "IDP_BSSID_REPORTNODEID", columnNames = { "IFMACADDRESS", "REPORTNODEID" }),
		@Index(name = "IDP_OWNER_STATIONTYPE_SIMULATED", columnNames = { "OWNER", "STATIONTYPE", "SIMULATED" }),
		@Index(name = "IDP_OWNER_STATIONTYPE_SIMULATED_BSSID", columnNames = { "OWNER", "STATIONTYPE", "SIMULATED", "IFMACADDRESS" })
		})
public class Idp implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final int IDP_MATRIX_OPEN = 1;
	public static final int IDP_MATRIX_WEP = 1 << 1;
	public static final int IDP_MATRIX_WPA = 1 << 2;
	public static final int IDP_MATRIX_WMM = 1 << 3;
	public static final int IDP_MATRIX_OUI = 1 << 4;
	public static final int IDP_MATRIX_SSID = 1 << 5;
	public static final int IDP_MATRIX_PREAMBLE = 1 << 6;
	public static final int IDP_MATRIX_BEACON = 1 << 7;
	public static final int IDP_MATRIX_AD_HOC = 1 << 8;
	public static EnumItem[] IDP_MATRIX_TYPE = MgrUtil.enumItems(
			"enum.idp.matrix.", new int[] { IDP_MATRIX_OPEN, IDP_MATRIX_WEP,
					IDP_MATRIX_WPA, IDP_MATRIX_WMM, IDP_MATRIX_OUI,
					IDP_MATRIX_SSID, IDP_MATRIX_PREAMBLE, IDP_MATRIX_BEACON,
					IDP_MATRIX_AD_HOC });

	public static final short IDP_CONNECTION_NOT_SURE = 0;
	public static final short IDP_CONNECTION_IN_NET = 1;
	public static EnumItem[] IDP_INNETWORK_FLAG_TYPE = MgrUtil.enumItems(
			"enum.idp.innetworkflag.", new int[] { IDP_CONNECTION_NOT_SURE,
					IDP_CONNECTION_IN_NET });

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "IDP_AP", joinColumns = @JoinColumn(name = "IDP_ID", nullable = false))
	private List<IdpAp> mitiAps = new ArrayList<IdpAp>();
	
	@Column(length = 12, nullable = false)
	private String reportNodeId;

	@Column(length = 12, nullable = false)
	private String ifMacAddress;

	private byte ifIndex;
	private String ssid;
	private short idpType;
	private short channel;
	private short rssi;
	private short inNetworkFlag;
	private short stationData;
	private short compliance;
	private short stationType;
	private HmTimeStamp reportTime;
	private double x, y;
	private Long mapId;
	private boolean mitigated;
	@Transient
	private short mode = IdsPolicy.MITIGATION_MODE_SEMIAUTO;
	private String parentBssid;// indicate it's fetched from BSSID (mitigation)
	private boolean simulated;
	private boolean isManaged;// indicate it's a BSSID of managed HiveAP
	@Transient
	private byte removedFlag;
	@Transient
	private boolean selected;
	@Transient
	private String mapName;
	@Transient
	private int rssiCount;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getReportNodeId() {
		return reportNodeId;
	}

	public void setReportNodeId(String reportNodeId) {
		this.reportNodeId = reportNodeId;
	}

	public String getIfMacAddress() {
		return ifMacAddress;
	}

	public void setIfMacAddress(String ifMacAddress) {
		this.ifMacAddress = ifMacAddress;
	}

	public byte getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(byte ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		//fixed the problem that ssid contains 0x00 and cannot insert into DB.
		this.ssid = AhDecoder.bytes2String(ssid.getBytes());
	}

	public short getIdpType() {
		return idpType;
	}

	public void setIdpType(short idpType) {
		this.idpType = idpType;
	}

	public short getChannel() {
		return channel;
	}

	public void setChannel(short channel) {
		this.channel = channel;
	}

	public short getRssi() {
		return rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

	public short getInNetworkFlag() {
		return inNetworkFlag;
	}

	public void setInNetworkFlag(short inNetworkFlag) {
		this.inNetworkFlag = inNetworkFlag;
	}

	public short getStationData() {
		return stationData;
	}

	public void setStationData(short stationData) {
		this.stationData = stationData;
	}

	public short getCompliance() {
		return compliance;
	}

	public void setCompliance(short compliance) {
		this.compliance = compliance;
	}

	public short getStationType() {
		return stationType;
	}

	public void setStationType(short stationType) {
		this.stationType = stationType;
	}

	public HmTimeStamp getReportTime() {
		return reportTime;
	}

	public void setReportTime(HmTimeStamp reportTime) {
		this.reportTime = reportTime;
	}

	public byte getRemovedFlag() {
		return removedFlag;
	}

	public void setRemovedFlag(byte removedFlag) {
		this.removedFlag = removedFlag;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public String getLabel() {
		return ifMacAddress;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Transient
	private String userTimeZone;

	public void setUserTimeZone(String userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	@Transient
	public String getReportHostName() {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(reportNodeId);
		if (null != ap) {
			return ap.getHostname();
		}
		return null;
	}

	@Transient
	public Long getReportHiveAPId() {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(reportNodeId);
		if (null != ap) {
			return ap.getId();
		}
		return null;
	}

	/*
	 * Atheros specific translation
	 */
	@Transient
	public String getRssiDbm() {
		/*-for those mitigated rogue client, will also report rssi since Firenze, see bug 24428, so comment this condition
		if (null == parentBssid || "".equals(parentBssid.trim())) {
			return (rssi - 95) + " dBm";
		} else {
			return "-";
		}*/
		return rssi == 0 ? "-" : ((rssi - 95) + " dBm");
	}

	@Transient
	public String getChannelString() {
		/*-for those mitigated rogue client, will also report channel since Firenze, see bug 24428, so comment this condition
		if (null == parentBssid || "".equals(parentBssid.trim())) {
			return String.valueOf(channel);
		} else {
			return "-";
		}*/
		return channel == 0 ? "-" : String.valueOf(channel);
	}

	@Transient
	public String getReportTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (null != reportTime && reportTime.getTime() > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
							TimeZone.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
							getOwner().getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (null != reportTime && reportTime.getTime() > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
							TimeZone.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
							getOwner().getTimeZone());
				}
			}
		}
		return "";
	}

	@Transient
	public String getNetworkString() {
		switch (inNetworkFlag) {
		case IDP_CONNECTION_IN_NET:
		case IDP_CONNECTION_NOT_SURE:
			return MgrUtil.getEnumString("enum.idp.innetworkflag."
					+ inNetworkFlag);
		default:
			return "";
		}
	}

	@Transient
	public String getVendor() {
		if (ifMacAddress != null) {
			String strOui = ifMacAddress.substring(0, 6).toUpperCase();
			if (AhConstantUtil.getMacOuiComName(strOui) != null) {
				return AhConstantUtil.getMacOuiComName(strOui);
			}
		}
		return "";
	}

	@Transient
	public String getSupportString() {
		return getCompliantValueRendererByString(stationData);
	}

	@Transient
	public String getComplianceString() {
		return getCompliantValueRendererByString(compliance);
	}

	public static String getCompliantValueRendererByString(int int_value) {
		StringBuilder sb = new StringBuilder("");
		if (int_value > 0 && int_value < Integer.MAX_VALUE) {
			try {
				String bInt = Integer.toBinaryString(int_value);
				char ii = '1';
				for (int i = bInt.length(); i > 0; i--) {
					char chrVal = bInt.charAt(i - 1);
					if (chrVal == ii) {
						int x = (int) Math.pow(2, bInt.length() - i);
						sb.append(getStrValue(x));
						if (i != 1)
							sb.append("; ");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private static String getStrValue(int int_val) {
		switch (int_val) {
		case IDP_MATRIX_OPEN:
		case IDP_MATRIX_WEP:
		case IDP_MATRIX_WPA:
		case IDP_MATRIX_WMM:
		case IDP_MATRIX_OUI:
		case IDP_MATRIX_SSID:
		case IDP_MATRIX_PREAMBLE:
		case IDP_MATRIX_BEACON:
		case IDP_MATRIX_AD_HOC:
			return MgrUtil.getEnumString("enum.idp.matrix." + int_val);
		default:
			return "";
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Long getMapId() {
		return mapId;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getRssiCount() {
		return rssiCount;
	}

	public void setRssiCount(int rssiCount) {
		this.rssiCount = rssiCount;
	}

	public boolean isMitigated() {
		return mitigated;
	}

	public void setMitigated(boolean mitigated) {
		this.mitigated = mitigated;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

	public boolean isManaged() {
		return isManaged;
	}

	public void setManaged(boolean isManaged) {
		this.isManaged = isManaged;
	}

	@Transient
	public String getMitigatedString() {
		if (isMitigated()) {
			return "On";
		} else {
			return "Off";
		}
	}
	
	@Transient
	public String getModeString() {
		if (this.mode == IdsPolicy.MITIGATION_MODE_MANUAL) { 
			return "Manual";
		} else if (this.mode == IdsPolicy.MITIGATION_MODE_SEMIAUTO) {
			return "Semi";
		} else {
			return "Auto";
		}
	}
	
	@Transient
	private long clientCount;

	public long getClientCount() {
		return clientCount;
	}

	public void setClientCount(long clientCount) {
		this.clientCount = clientCount;
	}

	public String getParentBssid() {
		return parentBssid;
	}

	public void setParentBssid(String parentBssid) {
		this.parentBssid = parentBssid;
	}

	@Transient
	private Map<String, Short> hightestRSSIs;

	@Transient
	private Object[] lastReportedTime;

	public String getHighestRSSIReportString() {
		String info = "";
		if (null != hightestRSSIs) {
			for (String apName : hightestRSSIs.keySet()) {
				if (info.length() > 0) {
					info += ", ";
				}
				short rssi = hightestRSSIs.get(apName);
				if (rssi == 0) {
					continue;
				}
				String rssiString = (rssi - 95) + " dBm";
				info += rssiString + " by " + apName;
			}
		}
		return "".equals(info) ? "-" : info;
	}

	public String getLastestReportedString() {
		String info = "";
		if (null != lastReportedTime) {
			HmTimeStamp reportTime = (HmTimeStamp) lastReportedTime[0];
			String apname = (String) lastReportedTime[1];
			if (null != userTimeZone && !"".equals(userTimeZone)) {
				info = AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
						TimeZone.getTimeZone(userTimeZone))
						+ " by " + apname;
			} else {
				info = AhDateTimeUtil.getSpecifyDateTime(reportTime.getTime(),
						getOwner().getTimeZone())
						+ " by " + apname;
			}
		}
		return info;
	}

	public void addHightestRSSIs(short rssi, String apName) {
		if (null == hightestRSSIs) {
			hightestRSSIs = new HashMap<String, Short>();
		}
		if (hightestRSSIs.size() < 3) {
			hightestRSSIs.put(apName, rssi);
		} else {
			String ap = "";
			Short smallest = 1000;
			for (String key : hightestRSSIs.keySet()) {
				if (hightestRSSIs.get(key) < smallest) {
					smallest = hightestRSSIs.get(key);
					ap = key;
				}
			}
			if (smallest < rssi) {
				hightestRSSIs.remove(ap);
				hightestRSSIs.put(apName, rssi);
			}
		}
	}

	public void addLastReportedTime(HmTimeStamp time, String apName) {
		if (null == time || apName == null) {
			return;
		}
		if (null == lastReportedTime
				|| ((HmTimeStamp) lastReportedTime[0]).getTime() < time
						.getTime()) {
			lastReportedTime = new Object[] { time, apName };
		}
	}
	
	@Transient
	private String reportedBssid;

	public String getReportedBssid() {
		return reportedBssid;
	}

	public void setReportedBssid(String reportedBssid) {
		this.reportedBssid = reportedBssid;
	}

	@Override
	public String toString() {
		return "Reporter Node ID: " + reportNodeId + "; BSSID: " + ifMacAddress
				+ "; SSID: " + ssid + "; Channel: " + channel + "; RSSI: "
				+ rssi;
	}

	public short getMode() {
		return mode;
	}

	public void setMode(short mode) {
		this.mode = mode;
	}

	public List<IdpAp> getMitiAps() {
		return mitiAps;
	}

	public void setMitiAps(List<IdpAp> mitiAps) {
		this.mitiAps = mitiAps;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}