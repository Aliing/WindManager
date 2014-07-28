package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.VpnService;

@Entity
@Table(name = "HM_VPNSTATUS")
@org.hibernate.annotations.Table(appliesTo = "HM_VPNSTATUS", indexes = {
		@Index(name = "VPN_STATUS_OWNER", columnNames = { "OWNER" })
		})
public class AhVPNStatus implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String serverID;

	private String clientID;

	/**
	 * second milli-second.
	 */
	private long connectTimeStamp;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	@Override
	public void setVersion(Timestamp version) {
	}

	@Override
	public String getLabel() {
		return "vpn status of " + serverID;
	}

	@ManyToOne(fetch = FetchType.EAGER)
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

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}

	public long getConnectTimeStamp() {
		return connectTimeStamp;
	}

	public void setConnectTimeStamp(long connectTimeStamp) {
		this.connectTimeStamp = connectTimeStamp;
	}

	public enum VpnStatus {
		Up, Down, Half
	}

	/* get the server status by given VPN status list */
	public static VpnStatus isVpnServerUp(List<AhVPNStatus> vpnStatuses,
			String nodeId) {
		VpnStatus isUp = VpnStatus.Down;
		if (null != vpnStatuses && null != nodeId) {
			for (AhVPNStatus status : vpnStatuses) {
				String serverNodeId = status.getServerID();
				String clientNodeId = status.getClientID();
				if (nodeId.equalsIgnoreCase(serverNodeId)
						&& null == clientNodeId) {
					isUp = VpnStatus.Up;
				}
			}
		}
		return isUp;
	}

	/* get the server status by given VPN status list */
	public static VpnStatus getVpnClientStatus(List<AhVPNStatus> vpnStatuses,
			String nodeId, boolean dualServer) {
		int clientStatusCount = 0;
		if (null != vpnStatuses && null != nodeId) {
			for (AhVPNStatus status : vpnStatuses) {
				String clientNodeId = status.getClientID();
				if (nodeId.equalsIgnoreCase(clientNodeId)) {
					clientStatusCount++;
				}
			}
		}
		if (clientStatusCount > 1) {
			return VpnStatus.Up;
		} else if (clientStatusCount == 1) {
			return dualServer ? VpnStatus.Half : VpnStatus.Up;
		} else {
			return VpnStatus.Down;
		}
	}

	public static VpnStatus isVpnServerUp(String nodeId) {
		String where = "serverID = :s1 and clientID is null";
		long count = QueryUtil.findRowCount(AhVPNStatus.class,
				new FilterParams(where, new Object[] { nodeId }));
		return count > 0 ? VpnStatus.Up : VpnStatus.Down;
	}
	
	public static VpnStatus isVpnClientUp(String nodeId, Long vpnServiceId) {
		boolean dualServer = false;
		if(null != vpnServiceId){
			VpnService vpnService = QueryUtil.findBoById(VpnService.class, vpnServiceId);
			if(null != vpnService){
				if (null != vpnService.getServerPublicIp2()
						&& !"".equals(vpnService.getServerPublicIp2())) {
					dualServer = true;
				}
			}
		}
		
		long count = QueryUtil.findRowCount(AhVPNStatus.class,
				new FilterParams("clientID", nodeId));
		
		if (count > 1) {
			return VpnStatus.Up;
		} else if (count == 1) {
			return dualServer ? VpnStatus.Half : VpnStatus.Up;
		} else {
			return VpnStatus.Down;
		}
	}

}