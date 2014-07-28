package com.ah.be.admin.auth.agent.impl.radius;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jradius.client.RadiusClient;
import net.jradius.client.auth.RadiusAuthenticator;
import net.jradius.packet.AccessRequest;
import net.jradius.packet.RadiusRequest;
import net.jradius.packet.RadiusResponse;
import net.jradius.packet.attribute.AttributeFactory;
import net.jradius.packet.attribute.AttributeList;
import net.jradius.packet.attribute.RadiusAttribute;
import net.jradius.packet.attribute.value.AttributeValue;

import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.be.admin.auth.agent.impl.AhDefaultAuthAgentImpl;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.wlan.Cwp;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class AhRadiusAuthAgentImpl implements AhAuthAgent, QueryBo {

	private static final long		serialVersionUID	= 1L;

	private static final Tracer log = new Tracer(AhDefaultAuthAgentImpl.class.getSimpleName());

	private HmLoginAuthentication	authConfig;

	public AhRadiusAuthAgentImpl() {
		// get the HmLoginAuthentication information from database
		List<HmLoginAuthentication> bos = QueryUtil.executeQuery(HmLoginAuthentication.class, null, null);
		if (!bos.isEmpty()) {
			authConfig = bos.get(0);
		}
	}

	@Override
	public AuthMethod getAuthMethod() {
		return AuthMethod.RADIUS;
	}

	/**
	 * @see com.ah.be.admin.auth.agent.AhAuthAgent#execute(java.lang.String, java.lang.String)
	 */
	@Override
	public HmUser execute(String userName, String userPassword) throws AhAuthException {
		assert (authConfig != null);
		int groupId = authenticate(userName, userPassword);
		List<HmUserGroup> bos = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams(
				"groupAttribute = :s1", new Object[] { groupId }), null, this);

		if (bos.isEmpty()) {
			// if no the group-id, then get default group(group-id=0)
			bos = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams(
					"groupAttribute = :s1 AND owner.domainName = :s2",
					new Object[] { HmUserGroup.MONITOR_ATTRIBUTE, HmDomain.HOME_DOMAIN }), null, this);
		}

		HmUserGroup userGroup = bos.get(0); // only one result.

		HmDomain hmDomain = userGroup.getOwner();

		// Check for the current status of VHM.
		switch (hmDomain.getRunStatus()) {
			case HmDomain.DOMAIN_RESTORE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to restoration for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.restoring");
			case HmDomain.DOMAIN_BACKUP_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to backup for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.backuping");
			case HmDomain.DOMAIN_UPDATE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to upgrade for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.updating");
			case HmDomain.DOMAIN_DISABLE_STATUS:
				log.warn("execute", "The user {" + userName + "} login request is refused due to disablement for VHM " + hmDomain.getDomainName());
				throw new AhAuthException("error.auth.vhm.disabled");
			default:
				break;
		}
		
		HmUser user = new HmUser();
		user.setId((long) 0); // radius user no id, then set to zero
		user.setUserGroup(userGroup);
		user.setUserName(userName); // value of userName can be either emailaddress or username
		user.setEmailAddress(userName); // set user name to column 'emailaddress' for user settings reference (add in Geneva by xtong)
		user.setOwner(userGroup.getOwner());
		return user;
	}

	/**
	 * authenticate from RADIUS server
	 * 
	 * @param userName
	 *            user name to be authenticated.
	 * @param password
	 *            plaintext password going with the user name to be authenticated.
	 * @return HM-admin-group-id
	 * @throws AhAuthException
	 *             2.No any RADIUS server 3.RADIUS exception 4.The RADIUS server(s) have no response
	 *             5.The RADIUS server(s) reject
	 */
	@Override
	public int authenticate(String userName, String password) throws AhAuthException {
		assert (authConfig != null);
		int groupId = -1;
		String[] attributes = new String[4];
		attributes[0] = "User-Name=" + userName;
		attributes[1] = "User-Password=" + password;
		
		// set HM IP as one attribute
		attributes[2] = "NAS-IP-Address=" + HmBeOsUtil.getHiveManagerIPAddr();
		attributes[3] = "NAS-Identifier=" + HmBeOsUtil.getHostName();

		// authenticate method, maybe PAP or CHAP
		String protocol;
		switch (authConfig.getAuthType()) {
		case Cwp.AUTH_METHOD_PAP:
			protocol = RadiusConstant.METHOD_PAP;
			break;
		case Cwp.AUTH_METHOD_CHAP:
			protocol = RadiusConstant.METHOD_CHAP;
			break;
		case Cwp.AUTH_METHOD_MSCHAPV2:
			protocol = RadiusConstant.METHOD_MSCHAPV2;
			break;
		default:
			protocol = RadiusConstant.METHOD_PAP;
			break;
		}

		// 1.build attribute list by attributes
		AttributeList sendAttributes = new AttributeList();
		try {
			for (String attribute : attributes) {
				RadiusAttribute a = AttributeFactory.attributeFromString(attribute);
				if (a != null) {
					sendAttributes.add(a, false);
				}
			}
		} catch (Exception e) {
			DebugUtil.adminDebugError("AttributeFactory.attributeFromString failed, e="
					+ e.getMessage());
			throw new AhAuthException("error.authfail.radiusfail");
		}

		// 2. get RADIUS servers and sort
		RadiusAssignment ra = authConfig.getRadiusAssignment();
		if (ra == null) {
			DebugUtil.adminDebugError("get radius group failed!");
			throw new AhAuthException("error.authfail.radius.noserver");
		}
		RadiusAssignment radius = QueryUtil.findBoById(RadiusAssignment.class,
				ra.getId(), this);
		List<RadiusServer> radiusServers = radius.getServices();
		if (radiusServers.size() == 0) {
			DebugUtil.adminDebugError("no any radius server in the group!");
			throw new AhAuthException("error.authfail.radius.noserver");
		}
		Map<Integer, RadiusServer> servers = new HashMap<Integer, RadiusServer>();
		for (RadiusServer radserv : radiusServers) {
			servers.put((int) radserv.getServerPriority(), radserv);
		}

		// 3.authenticate through primary RADIUS server, and if it fails then, traversing all
		// RADIUS servers by priority from bakup1 to backup3
		boolean isResponse = false;
		boolean isNotBeRejcted = false;
		RadiusAuthenticator auth;
		
		for (int i = RadiusServer.RADIUS_PRIORITY_PRIMARY;
			i <= RadiusServer.RADIUS_PRIORITY_BACKUP3; i++) {
			
			auth = RadiusClient.getAuthProtocol(protocol);
			RadiusServer rs = servers.get(i);
			if (rs == null) {
				continue;
			}
			isResponse = true;
			isNotBeRejcted = true;

			// 3.1 get RADIUS server argument
			// 2009.1.13 Comment by Jonathan. don't get(0) but get global IP-address
			// Delete: String ipAddress = rs.getIpAddress().getItems().get(0).getIpAddress();
			// Add: new code as below
			String ipAddress = null;
			List<SingleTableItem> lst = rs.getIpAddress().getItems();
			for (SingleTableItem a : lst) {
				if (a.getType() == SingleTableItem.TYPE_GLOBAL) {
					ipAddress = a.getIpAddress();
					break;
				}
			}

			InetAddress inet;
			try {
				inet = InetAddress.getByName(ipAddress);
			} catch (UnknownHostException e) {
				DebugUtil.adminDebugWarn("get ip address failed, e=" + e.getMessage());
				continue;
			}
			String secret = rs.getSharedSecret();
			int authPort = rs.getAuthPort();
			int acctPort = rs.getAcctPort();

			// 3.2 initial radius client and create Access-Request packet
			RadiusClient client;
			try {
				client = new RadiusClient(inet, secret, authPort, acctPort, RadiusConstant.TIMEOUT);
			} catch (IOException e) {
				DebugUtil.adminDebugWarn("radius connect failed, e=" + e.getMessage());
				continue;
			}
			RadiusRequest request = new AccessRequest(client, sendAttributes);
			RadiusResponse reply;

			// 3.4 send RADIUS Access-Request packet and receive response may be RADIUS
			// Access-Accept packet or RADIUS Access-Reject packet
			try {
				reply = client.authenticate((AccessRequest) request, auth, RadiusConstant.RETRIES);
			} catch (Exception e) {
				DebugUtil.adminDebugWarn("no response in RADIUS server->" + ipAddress + ":"
						+ rs.getAuthPort());
				isResponse = false;
				continue;
			}

			if (reply.getCode() == RadiusConstant.RADIUS_CODE_ACCESS_REJECT) {
				isNotBeRejcted = false;
				continue;
			}

			DebugUtil.adminDebugInfo("request=" + request.toString());
			DebugUtil.adminDebugInfo("reply=" + reply.toString());

			// 3.5 get attribute by analyze Access-Accept package
			RadiusAttribute attr = reply
					.findAttribute(getAerohiveTypeId(RadiusConstant.ATTRID_GROUP));
			if (attr == null) {
				groupId = 0;
			} else {
				AttributeValue av = attr.getValue();
				groupId = AhDecoder.bytes2int(av.getBytes());
			}
			DebugUtil.adminDebugInfo("return groupId=" + groupId);
			break;
		}

		if (!isResponse) {
			DebugUtil.adminDebugError("The RADIUS server(s) have no response.");
			throw new AhAuthException("error.authfail.radius.noresponse");
		}

		if (!isNotBeRejcted) {
			DebugUtil.adminDebugInfo(userName + " be reject");
			throw new AhAuthException("error.authfail.radius.reject");
		}

		// 4.return
		return groupId;
	}

	/**
	 * radius service is lazy to get
	 * 
	 * @param bo -
	 * @return -
	 */
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusAssignment) {
			RadiusAssignment radius = (RadiusAssignment) bo;
			if (radius.getServices() != null){
				radius.getServices().size();
				for (RadiusServer radiusServer : radius.getServices()) {
				    IpAddress ipAddress = radiusServer.getIpAddress();
                    if (null != ipAddress && null != ipAddress.getItems())
                        radiusServer.getIpAddress().getItems().size();
                }
			}
		}
		if (bo instanceof HmUserGroup) {
			HmUserGroup group = (HmUserGroup) bo;
			if (group.getInstancePermissions() != null)
				group.getInstancePermissions().size();
			if (group.getFeaturePermissions() != null)
				group.getFeaturePermissions().size();
		}
		return null;
	}

	/**
	 * get Aerohive attribute type id
	 * 
	 * @param attributeId -
	 * @return type-id note: vendor-Specific attribute values calculated as follows: type ID = 65536
	 *         x vendor ID + vendor internal attribute ID like as: 1764753409 = 65536 * 26928 + 1
	 */
	private int getAerohiveTypeId(int attributeId) {
		return 65536 * RadiusConstant.AEROHIVE_ID + attributeId;
	}

}