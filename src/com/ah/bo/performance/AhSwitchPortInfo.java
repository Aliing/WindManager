package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name="HM_SWITCH_PORT_INFO")
@org.hibernate.annotations.Table(appliesTo = "HM_SWITCH_PORT_INFO", indexes = {
		@Index(name = "idx_switch_port_info_owner", columnNames = { "OWNER" }),
		@Index(name = "idx_switch_port_info_mac", columnNames = { "MAC" })
		})
public class AhSwitchPortInfo implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	@Column(length = 128, nullable = false)
	private String 		portName;
	
	// 0:Access, 1:Trunk, 2:WAN
	private byte		portType;
	
	// unit is millisecond (time of port up point, msec from 1/1/1970)
	private long		enableTimestamp;
	
	// 0:down, 1:up
	private byte		state;

	private byte		lineProtocol;
	
	@Column(length = 4096)
	private String		voiceVLANs;
	
	@Column(length = 4096)
	private String		dataVLANs;
	
	private byte		authenticationState;
	
	private byte		STPMode;
	
	private byte		STPRole;
	
	private byte		STPState;
	
	//phyaical ports if this port is port-channel, the values such as "eth1/1,eth1/2"
	@Column(length = 4096)
	private String		physicalPorts;
	
	// 0: not dest mirror port, 1: dest mirror port
	private byte		mirrorPort;
	
	// if portType is (1:Trunk): native vlan, is (0:Access): access vlan, is (2:WAN):wan pvid,will not used
	private short		pvid;
	
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

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public byte getPortType() {
		return portType;
	}

	public void setPortType(byte portType) {
		this.portType = portType;
	}

	public long getEnableTimestamp() {
		return enableTimestamp;
	}

	public void setEnableTimestamp(long enableTimestamp) {
		this.enableTimestamp = enableTimestamp;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public byte getLineProtocol() {
		return lineProtocol;
	}

	public void setLineProtocol(byte lineProtocol) {
		this.lineProtocol = lineProtocol;
	}

	public String getVoiceVLANs() {
		return voiceVLANs;
	}

	public void setVoiceVLANs(String voiceVLANs) {
		this.voiceVLANs = voiceVLANs;
	}

	public String getDataVLANs() {
		return dataVLANs;
	}

	public void setDataVLANs(String dataVLANs) {
		this.dataVLANs = dataVLANs;
	}

	public byte getAuthenticationState() {
		return authenticationState;
	}

	public void setAuthenticationState(byte authenticationState) {
		this.authenticationState = authenticationState;
	}

	public byte getSTPMode() {
		return STPMode;
	}

	public void setSTPMode(byte sTPMode) {
		STPMode = sTPMode;
	}

	public byte getSTPRole() {
		return STPRole;
	}

	public void setSTPRole(byte sTPRole) {
		STPRole = sTPRole;
	}

	public byte getSTPState() {
		return STPState;
	}

	public void setSTPState(byte sTPState) {
		STPState = sTPState;
	}
	
	public String getPhysicalPorts() {
		return physicalPorts;
	}

	public void setPhysicalPorts(String physicalPorts) {
		this.physicalPorts = physicalPorts;
	}
	
	public byte getMirrorPort() {
		return mirrorPort;
	}

	public void setMirrorPort(byte mirrorPort) {
		this.mirrorPort = mirrorPort;
	}



	public static final byte SWITCH_STP_MODE_STP = 0;
	public static final byte SWITCH_STP_MODE_MSTP = 1;
	public static final byte SWITCH_STP_MODE_RSTP = 2;
	
	public static final byte SWITCH_STP_ROLE_MASTERPORT = 0;
	public static final byte SWITCH_STP_ROLE_ALTERNATE = 1;
	public static final byte SWITCH_STP_ROLE_ROOTPORT = 2;
	public static final byte SWITCH_STP_ROLE_DESIGNATED = 3;
	public static final byte SWITCH_STP_ROLE_DISABLE = 4;
	public static final byte SWITCH_STP_ROLE_BACKUP = 5;
	
	public static final byte SWITCH_STP_STATE_DETIAL_DISCARDING = 0;
	public static final byte SWITCH_STP_STATE_DETIAL_LISTENING = 1;
	public static final byte SWITCH_STP_STATE_DETIAL_LEARNING = 2;
	public static final byte SWITCH_STP_STATE_DETIAL_FORWARDING = 3;
	public static final byte SWITCH_STP_STATE_DETIAL_BLOCKING = 4;
	
	public static final byte SWITCH_LINK_STATE_DOWN = 0; // admin enable & down
	public static final byte SWITCH_LINK_STATE_UP = 1; // admin enable & up
	public static final byte SWITCH_LINK_STATE_DOWN_STPD = 2; // admin enable & stp down
	public static final byte SWITCH_LINK_STATE_ADMIN_DISABLE = 3; // admin disable, must be down
	// 99 just use for return value to UI, original status code send up from SW for 'USB not connected' is 2, avoid duplicated with 'STATE_DOWN_STPD:2', value return 99 to UI
	public static final byte SWITCH_LINK_STATE_USB_DISCONNECTED = 99;
	
	public static final byte SWITCH_AUTH_STATE_NO_DATA_NO_VOICE = 0;
	public static final byte SWITCH_AUTH_STATE_DATA_VLAN = 1;
	public static final byte SWITCH_AUTH_STATE_VOICE_VLAN = 2;
	public static final byte SWITCH_AUTH_STATE_DATA_VOICE_VLAN = 3;

	@Transient
	public String getPortUpTimeString() {
		if (SWITCH_LINK_STATE_UP == state) {
			// state = 1(port is up), calculate up time
			long delta = System.currentTimeMillis() - enableTimestamp;
			if (delta > 0) {
				return NmsUtil.transformTime((int) (delta / 1000));
			} else {
				return "";
			}
		} else {
			// state = 0(port is down) or state = 2(port is down by stpd)
			return "";
		}
	}

	@Transient
	public String getLinkStateString () {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.link.state."+this.getState());
		/*switch (state) {
		case SWITCH_LINK_STATE_DOWN:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.link.state.0");
		case SWITCH_LINK_STATE_UP:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.link.state.1");
		}
		return "";*/
	}

	@Transient
	public String getAuthStateString () {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.auth.state."+this.getAuthenticationState());
		/*switch (authenticationState) {
		case SWITCH_AUTH_STATE_NO_DATA_NO_VOICE:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.auth.state.0");
		case SWITCH_AUTH_STATE_DATA_VLAN:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.auth.state.1");
		case SWITCH_AUTH_STATE_VOICE_VLAN:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.auth.state.2");
		case SWITCH_AUTH_STATE_DATA_VOICE_VLAN:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.auth.state.3");
		}
		return "";*/
	}
	
	@Transient
	public String getStpModeString () {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.stp.mode."+this.getSTPMode());
		/*switch (STPMode) {
		case SWITCH_STP_MODE_STP:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.mode.0");
		case SWITCH_STP_MODE_MSTP:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.mode.1");
		case SWITCH_STP_MODE_RSTP:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.mode.2");
		}
		return "";*/
	}
	
	@Transient
	public String getStpRoleString () {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.stp.role."+this.getSTPRole());
		/*switch (STPRole) {
		case SWITCH_STP_ROLE_MASTERPORT:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.0");
		case SWITCH_STP_ROLE_ALTERNATE:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.1");
		case SWITCH_STP_ROLE_ROOTPORT:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.2");
		case SWITCH_STP_ROLE_DESIGNATED:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.3");
		case SWITCH_STP_ROLE_DISABLE:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.4");
		case SWITCH_STP_ROLE_BACKUP:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.role.5");
		}
		return "";*/
	}
	
	@Transient
	public String getStpStateDetailString () {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.stp.state.detail."+this.getSTPState());
		/*switch (STPState) {
		case SWITCH_STP_STATE_DETIAL_DISCARDING:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.state.detail.0");
		case SWITCH_STP_STATE_DETIAL_LISTENING:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.state.detail.1");
		case SWITCH_STP_STATE_DETIAL_LEARNING:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.state.detail.2");
		case SWITCH_STP_STATE_DETIAL_FORWARDING:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.state.detail.3");
		case SWITCH_STP_STATE_DETIAL_BLOCKING:
			return MgrUtil.getUserMessage("monitor.hiveAp.switch.stp.state.detail.4");
		}
		return "";*/
	}

	public short getPvid() {
		return pvid;
	}

	public void setPvid(short pvid) {
		this.pvid = pvid;
	}
	
}