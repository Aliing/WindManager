package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.SubjectAltname_st;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "VPN_SERVICE",uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "PROFILENAME" }) })
@org.hibernate.annotations.Table(appliesTo = "VPN_SERVICE", indexes = {
		@Index(name = "VPN_SERVICE_OWNER", columnNames = { "OWNER" })
		})
//@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class VpnService implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final int MAX_IP_POOL_SIZE = 1024;
	
	public static final int MAX_IP_POOL_SIZE_VPN2 = 128;
	
	public static final int MAX_IP_POOL_SIZE_VPN2_CVG = 1500;
	
	public static final int MAX_IP_POOL_SIZE_VPN_CVG_DEVICE = 4096;

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

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private String rootCa = BeAdminCentOSTools.AH_NMS_DEFAULT_CA;
	private String certificate = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT;
	private String privateKey = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY;
	

	/* VPN Server1 */
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverPrivateIp1;
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverPublicIp1;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolStart1;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolEnd1;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolNetmask1;
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverDeaultGateway1;
	

	/* VPN Server2 */
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverPrivateIp2;
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverPublicIp2;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolStart2;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolEnd2;
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIpPoolNetmask2;
	@Column(length = IP_ADDRESS_LENGTH)
	private String serverDeaultGateway2;

	public static final int IKE_ID_ADDRESS = 1;
	public static final int IKE_ID_ASN1DN = 2;
	public static final int IKE_ID_FQDN = 3;
	public static final int IKE_ID_UFQDN = 4;
	public static EnumItem[] IKE_ID = MgrUtil.enumItems(
			"enum.vpn.ike.id.type.", new int[] { IKE_ID_ADDRESS, IKE_ID_ASN1DN,
					IKE_ID_FQDN, IKE_ID_UFQDN });
	private int serverIkeId = IKE_ID_ASN1DN;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_SERVICE_CREDENTIAL", joinColumns = @JoinColumn(name = "VPN_SERVICE_ID", nullable = false))
//	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<VpnServiceCredential> vpnCredentials = new ArrayList<VpnServiceCredential>();

	/* for VPN server advanced */
	public static final short PHASE1_AUTH_METHOD_HYBRID = 1;
	public static final short PHASE1_AUTH_METHOD_RSA_SIG = 2;
	public static EnumItem[] PHASE1_AUTH_METHOD = MgrUtil.enumItems(
			"enum.vpn.phase1.auth.method.",
			new int[] { PHASE1_AUTH_METHOD_HYBRID /*
												 * , PHASE1_AUTH_METHOD_RSA_SIG
												 */});
	private short phase1AuthMethod = PHASE1_AUTH_METHOD_HYBRID;

	public static final short PHASE1_ENCRYP_ALG_3DES = 1;
	public static final short PHASE1_ENCRYP_ALG_AES128 = 2;
	public static final short PHASE1_ENCRYP_ALG_AES192 = 3;
	public static final short PHASE1_ENCRYP_ALG_AES256 = 4;
	public static EnumItem[] PHASE1_ENCRYP_ALG = MgrUtil.enumItems(
			"enum.vpn.phase1.encryption.alg.", new int[] {
					PHASE1_ENCRYP_ALG_3DES, PHASE1_ENCRYP_ALG_AES128,
					PHASE1_ENCRYP_ALG_AES192, PHASE1_ENCRYP_ALG_AES256 });
	private short phase1EncrypAlg = PHASE1_ENCRYP_ALG_AES128;

	public static final short PHASE1_HASH_MD5 = 1;
	public static final short PHASE1_HASH_SHA1 = 2;
	public static EnumItem[] PHASE1_HASH = MgrUtil.enumItems(
			"enum.vpn.phase1.hash.", new int[] { PHASE1_HASH_MD5,
					PHASE1_HASH_SHA1 });
	private short phase1Hash = PHASE1_HASH_SHA1;

	public static final short PHASE1_DH_GROUP_1 = 1;
	public static final short PHASE1_DH_GROUP_2 = 2;
	public static final short PHASE1_DH_GROUP_5 = 5;
	public static EnumItem[] PHASE1_DH_GROUP = MgrUtil.enumItems(
			"enum.vpn.phase1.dh.group.", new int[] { PHASE1_DH_GROUP_1,
					PHASE1_DH_GROUP_2, PHASE1_DH_GROUP_5 });
	private short phase1DhGroup = PHASE1_DH_GROUP_2;

	public static final int DEFAULT_PHASE1_LIFE_TIME = 86400;
	@Range(min = 180, max = 10000000)
	private int phase1LifeTime = DEFAULT_PHASE1_LIFE_TIME;

	public static final short PHASE2_ENCRYP_ALG_3DES = 1;
	public static final short PHASE2_ENCRYP_ALG_AES128 = 2;
	public static final short PHASE2_ENCRYP_ALG_AES192 = 3;
	public static final short PHASE2_ENCRYP_ALG_AES256 = 4;
	public static EnumItem[] PHASE2_ENCRYP_ALG = MgrUtil.enumItems(
			"enum.vpn.phase2.encryption.alg.", new int[] {
					PHASE2_ENCRYP_ALG_3DES, PHASE2_ENCRYP_ALG_AES128,
					PHASE2_ENCRYP_ALG_AES192, PHASE2_ENCRYP_ALG_AES256 });
	private short phase2EncrypAlg = PHASE2_ENCRYP_ALG_AES128;

	public static final short PHASE2_HASH_MD5 = 1;
	public static final short PHASE2_HASH_SHA1 = 2;
	public static EnumItem[] PHASE2_HASH = MgrUtil.enumItems(
			"enum.vpn.phase2.hash.", new int[] { PHASE2_HASH_MD5,
					PHASE2_HASH_SHA1 });
	private short phase2Hash = PHASE2_HASH_SHA1;

	public static final short PHASE2_PFS_GROUP_0 = 0;
	public static final short PHASE2_PFS_GROUP_1 = 1;
	public static final short PHASE2_PFS_GROUP_2 = 2;
	public static final short PHASE2_PFS_GROUP_5 = 5;
	public static EnumItem[] PHASE2_PFS_GROUP = MgrUtil.enumItems(
			"enum.vpn.phase2.pfs.group.",
			new int[] { PHASE2_PFS_GROUP_0, PHASE2_PFS_GROUP_1,
					PHASE2_PFS_GROUP_2, PHASE2_PFS_GROUP_5 });
	private short phase2PfsGroup = PHASE2_PFS_GROUP_2;

	public static final int DEFAULT_PHASE2_LEFE_TIME = 3600;
	@Range(min = 180, max = 10000000)
	private int phase2LifeTime = DEFAULT_PHASE2_LEFE_TIME;

	private boolean ikeValidation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DNS_IP", nullable = true)
	private IpAddress dnsIp;

	/* for VPN client advanced */
	private boolean capwapThroughTunnel;
	private boolean snmpThroughTunnel;
	private boolean ntpThroughTunnel;
	private boolean logThroughTunnel;
	private boolean radiusThroughTunnel;
	private boolean dbTypeAdThroughTunnel;
	private boolean dbTypeLdapThroughTunnel;

	public static final int DEFAULT_DPD_IDEL_INTERVAL = 10;
	@Range(min = 0, max = 65535)
	private int dpdIdelInterval = DEFAULT_DPD_IDEL_INTERVAL;

	public static final int DEFAULT_DPD_RETRY = 5;
	@Range(min = 1, max = 65535)
	private int dpdRetry = DEFAULT_DPD_RETRY;

	public static final int DEFAULT_DPD_RETRY_INTERVAL = 3;
	@Range(min = 1, max = 60)
	private int dpdRetryInterval = DEFAULT_DPD_RETRY_INTERVAL;

	public static final int DEFAULT_AMRP_INTERVAL = 10;
	@Range(min = 0, max = 65535)
	private int amrpInterval = DEFAULT_AMRP_INTERVAL;

	public static final int DEFAULT_AMRP_RETRY = 10;
	@Range(min = 1, max = 255)
	private int amrpRetry = DEFAULT_AMRP_RETRY;

	private boolean natTraversal = true;

	private boolean keepAlive = true;

	private boolean loadBalance = true;

	public static final short VPN_SERVER_TYPE_SINGLE = 1;

	public static final short VPN_SERVER_TYPE_REDUNDANT = 2;
	
	//added for Congo
	public static final short IPSEC_VPN_LAYER_2= 3;

	public static final short IPSEC_VPN_LAYER_3 = 4;
	
	public static final short USER_PROFILES_TUNNEL_ALL = 5;
	
	public static final short USER_PROFILES_SPLIT_TUNNEL = 6;
	
    public static final short USER_PROFILES_TUNNEL_ALLL3 = 7;
	
	public static final short USER_PROFILES_SPLIT_TUNNELL3 = 8;
	
	public static final short ROUTE_VPNTUNNEL_TRAFFIC_ALL = 11;
	
	public static final short ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL = 12;
	
	// have no default value
	private Long hiveApVpnServer1;
	private Long hiveApVpnServer2;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_GATEWAY_SETTING", joinColumns = @JoinColumn(name = "VPN_GATEWAY_SETTING_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<VpnGatewaySetting> vpnGateWaysSetting = new ArrayList<VpnGatewaySetting>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_USERPROFILE_TRAFFICL3", joinColumns = @JoinColumn(name = "VPN_USERPROFILE_TRAFFICL3_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<UserProfileForTrafficL3> userProfileTrafficL3 = new ArrayList<UserProfileForTrafficL3>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_USERPROFILE_TRAFFICL2", joinColumns = @JoinColumn(name = "VPN_USERPROFILE_TRAFFICL2_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<UserProfileForTrafficL2> userProfileTrafficL2 = new ArrayList<UserProfileForTrafficL2>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOMAINOBJECT_ID")
	private DomainObject domObj;

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

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return profileName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRootCa() {
		return rootCa;
	}

	public void setRootCa(String rootCa) {
		this.rootCa = rootCa;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getServerPrivateIp1() {
		return serverPrivateIp1;
	}

	public void setServerPrivateIp1(String serverPrivateIp1) {
		this.serverPrivateIp1 = serverPrivateIp1;
	}

	public String getServerPublicIp1() {
		return serverPublicIp1;
	}

	public void setServerPublicIp1(String serverPublicIp1) {
		this.serverPublicIp1 = serverPublicIp1;
	}

	public String getClientIpPoolStart1() {
		return clientIpPoolStart1;
	}

	public void setClientIpPoolStart1(String clientIpPoolStart1) {
		this.clientIpPoolStart1 = clientIpPoolStart1;
	}

	public String getClientIpPoolEnd1() {
		return clientIpPoolEnd1;
	}

	public void setClientIpPoolEnd1(String clientIpPoolEnd1) {
		this.clientIpPoolEnd1 = clientIpPoolEnd1;
	}

	public String getClientIpPoolNetmask1() {
		return clientIpPoolNetmask1;
	}

	public void setClientIpPoolNetmask1(String clientIpPoolNetmask1) {
		this.clientIpPoolNetmask1 = clientIpPoolNetmask1;
	}

	public String getServerPrivateIp2() {
		return serverPrivateIp2;
	}

	public void setServerPrivateIp2(String serverPrivateIp2) {
		this.serverPrivateIp2 = serverPrivateIp2;
	}

	public String getServerPublicIp2() {
		return serverPublicIp2;
	}

	public void setServerPublicIp2(String serverPublicIp2) {
		this.serverPublicIp2 = serverPublicIp2;
	}

	public String getClientIpPoolStart2() {
		return clientIpPoolStart2;
	}

	public void setClientIpPoolStart2(String clientIpPoolStart2) {
		this.clientIpPoolStart2 = clientIpPoolStart2;
	}

	public String getClientIpPoolEnd2() {
		return clientIpPoolEnd2;
	}

	public void setClientIpPoolEnd2(String clientIpPoolEnd2) {
		this.clientIpPoolEnd2 = clientIpPoolEnd2;
	}

	public String getClientIpPoolNetmask2() {
		return clientIpPoolNetmask2;
	}

	public void setClientIpPoolNetmask2(String clientIpPoolNetmask2) {
		this.clientIpPoolNetmask2 = clientIpPoolNetmask2;
	}

	public short getPhase1AuthMethod() {
		return phase1AuthMethod;
	}

	public void setPhase1AuthMethod(short phase1AuthMethod) {
		this.phase1AuthMethod = phase1AuthMethod;
	}

	public int getServerIkeId() {
		return serverIkeId;
	}

	public void setServerIkeId(int serverIkeId) {
		this.serverIkeId = serverIkeId;
	}

	public short getPhase1EncrypAlg() {
		return phase1EncrypAlg;
	}

	public void setPhase1EncrypAlg(short phase1EncrypAlg) {
		this.phase1EncrypAlg = phase1EncrypAlg;
	}

	public short getPhase1Hash() {
		return phase1Hash;
	}

	public void setPhase1Hash(short phase1Hash) {
		this.phase1Hash = phase1Hash;
	}

	public short getPhase1DhGroup() {
		return phase1DhGroup;
	}

	public void setPhase1DhGroup(short phase1DhGroup) {
		this.phase1DhGroup = phase1DhGroup;
	}

	public int getPhase1LifeTime() {
		return phase1LifeTime;
	}

	public void setPhase1LifeTime(int phase1LifeTime) {
		this.phase1LifeTime = phase1LifeTime;
	}

	public short getPhase2EncrypAlg() {
		return phase2EncrypAlg;
	}

	public void setPhase2EncrypAlg(short phase2EncrypAlg) {
		this.phase2EncrypAlg = phase2EncrypAlg;
	}

	public short getPhase2Hash() {
		return phase2Hash;
	}

	public void setPhase2Hash(short phase2Hash) {
		this.phase2Hash = phase2Hash;
	}

	public short getPhase2PfsGroup() {
		return phase2PfsGroup;
	}

	public void setPhase2PfsGroup(short phase2PfsGroup) {
		this.phase2PfsGroup = phase2PfsGroup;
	}

	public int getPhase2LifeTime() {
		return phase2LifeTime;
	}

	public void setPhase2LifeTime(int phase2LifeTime) {
		this.phase2LifeTime = phase2LifeTime;
	}

	public IpAddress getDnsIp() {
		return dnsIp;
	}

	public void setDnsIp(IpAddress dnsIp) {
		this.dnsIp = dnsIp;
	}

	public boolean isCapwapThroughTunnel() {
		return capwapThroughTunnel;
	}

	public void setCapwapThroughTunnel(boolean capwapThroughTunnel) {
		this.capwapThroughTunnel = capwapThroughTunnel;
	}

	public boolean isSnmpThroughTunnel() {
		return snmpThroughTunnel;
	}

	public void setSnmpThroughTunnel(boolean snmpThroughTunnel) {
		this.snmpThroughTunnel = snmpThroughTunnel;
	}

	public boolean isNtpThroughTunnel() {
		return ntpThroughTunnel;
	}

	public void setNtpThroughTunnel(boolean ntpThroughTunnel) {
		this.ntpThroughTunnel = ntpThroughTunnel;
	}

	public boolean isLogThroughTunnel() {
		return logThroughTunnel;
	}

	public void setLogThroughTunnel(boolean logThroughTunnel) {
		this.logThroughTunnel = logThroughTunnel;
	}

	public boolean isRadiusThroughTunnel() {
		return radiusThroughTunnel;
	}

	public void setRadiusThroughTunnel(boolean radiusThroughTunnel) {
		this.radiusThroughTunnel = radiusThroughTunnel;
	}

	public boolean isDbTypeAdThroughTunnel() {
		return dbTypeAdThroughTunnel;
	}

	public void setDbTypeAdThroughTunnel(boolean dbTypeAdThroughTunnel) {
		this.dbTypeAdThroughTunnel = dbTypeAdThroughTunnel;
	}

	public boolean isDbTypeLdapThroughTunnel() {
		return dbTypeLdapThroughTunnel;
	}

	public void setDbTypeLdapThroughTunnel(boolean dbTypeLdapThroughTunnel) {
		this.dbTypeLdapThroughTunnel = dbTypeLdapThroughTunnel;
	}

	public boolean isNatTraversal() {
		return natTraversal;
	}

	public void setNatTraversal(boolean natTraversal) {
		this.natTraversal = natTraversal;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isIkeValidation() {
		return ikeValidation;
	}

	public void setIkeValidation(boolean ikeValidation) {
		this.ikeValidation = ikeValidation;
	}

	public List<VpnServiceCredential> getVpnCredentials() {
		return vpnCredentials;
	}

	public void setVpnCredentials(List<VpnServiceCredential> vpnCredentials) {
		this.vpnCredentials = vpnCredentials;
	}

	public int getDpdIdelInterval() {
		return dpdIdelInterval;
	}

	public void setDpdIdelInterval(int dpdIdelInterval) {
		this.dpdIdelInterval = dpdIdelInterval;
	}

	public int getDpdRetry() {
		return dpdRetry;
	}

	public void setDpdRetry(int dpdRetry) {
		this.dpdRetry = dpdRetry;
	}

	public int getDpdRetryInterval() {
		return dpdRetryInterval;
	}

	public void setDpdRetryInterval(int dpdRetryInterval) {
		this.dpdRetryInterval = dpdRetryInterval;
	}

	public int getAmrpInterval() {
		return amrpInterval;
	}

	public void setAmrpInterval(int amrpInterval) {
		this.amrpInterval = amrpInterval;
	}

	public int getAmrpRetry() {
		return amrpRetry;
	}

	public void setAmrpRetry(int amrpRetry) {
		this.amrpRetry = amrpRetry;
	}

	public boolean isLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(boolean loadBalance) {
		this.loadBalance = loadBalance;
	}

	@Transient
	public boolean isDefaultPhase1LifeTime() {
		return phase1LifeTime == DEFAULT_PHASE1_LIFE_TIME;
	}

	@Transient
	private short vpnServerType;
	
	private short ipsecVpnType;
	
	private short routeTrafficType;
	
	@Transient
	private short profileTunnelType;

	public void setVpnServerType(short vpnServerType) {
		this.vpnServerType = vpnServerType;
	}
	
	public short getVpnServerType() {
		if (this.vpnServerType > 0) {
			return this.vpnServerType;
		}
		if (null != serverPublicIp2 && !"".equals(serverPublicIp2)) {
			return VPN_SERVER_TYPE_REDUNDANT;
		} else {
			return VPN_SERVER_TYPE_SINGLE;
		}
	}
	
	public void setIpsecVpnType(short ipsecVpnType) {
		this.ipsecVpnType = ipsecVpnType;
	}

	public short getIpsecVpnType() {
		return this.ipsecVpnType;
	}
	
	public String getVpnGatewaysString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			StringBuilder retStr = new StringBuilder();
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getHiveAP()!=null) {
						if (retStr.length()!=0){
							retStr.append("<br/>");
						}
						retStr.append(gv.getHiveAP().getHostName());
					}
				}
			}
			return retStr.toString();
			
		} else if (ipsecVpnType==IPSEC_VPN_LAYER_2){
			if (vpnServerType==VPN_SERVER_TYPE_SINGLE){
				return this.getVpnServerHostName(this.hiveApVpnServer1, this.serverDeaultGateway1);
			} else {
				return this.getVpnServerHostName(this.hiveApVpnServer1, serverDeaultGateway1) 
						+ "<br/>" + this.getVpnServerHostName(this.hiveApVpnServer2, serverDeaultGateway2);
			}
		} else {
			return "";
		}
	}
	
	public String getPrimaryTunnelString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getApId()!=null) {
						HiveAp ap = QueryUtil.findBoById(HiveAp.class,gv.getApId());
						if(null != ap){
							return ap.getHostName();
						}
						
					}
				}
			}
		} else if (ipsecVpnType==IPSEC_VPN_LAYER_2){
			// bug fix: 27434, also show host name for L2 VPN service
			return this.getVpnServerHostName(this.hiveApVpnServer1, this.serverDeaultGateway1);
		} 
		return "";
	}
	
	public String getBackupTunnelString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			int i = -1;
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getApId()!=null) {
						if (i != -1){
							HiveAp ap = QueryUtil.findBoById(HiveAp.class,gv.getApId());
							if(null != ap){
								return ap.getHostName();
							}
						}
						i++;
					}
				}
			}
		} else if (ipsecVpnType==IPSEC_VPN_LAYER_2){
			if (vpnServerType==VPN_SERVER_TYPE_SINGLE){
				return "";
			} else {
				// bug fix: 27434, also show host name for L2 VPN service
				return this.getVpnServerHostName(this.hiveApVpnServer2, this.serverDeaultGateway2);
			}
		} 
		return "";
	}

	private String getVpnServerHostName(Long apId, String defValue) {
		if (apId != null
				&& apId.compareTo(0L) > 0) {
			HiveAp ap = QueryUtil.findBoById(HiveAp.class, apId);
			if (ap != null) {
				return ap.getHostName();
			}
		}
		return defValue;
	}
	
	public String getVpnIpAddressString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			StringBuilder retStr = new StringBuilder();
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getHiveAP()!=null && gv.getHiveAP().getEth0Interface()!=null && gv.getHiveAP().getEth0Interface().getIpAddress() != null) {
						if (retStr.length()!=0){
							retStr.append("<br/>");
						}
						retStr.append(gv.getHiveAP().getEth0Interface().getIpAddress());
					}
				}
			}
			return retStr.toString();
		} else if (ipsecVpnType==IPSEC_VPN_LAYER_2){
			if (vpnServerType==VPN_SERVER_TYPE_SINGLE){
				return serverPrivateIp1;
			} else {
				return serverPrivateIp1 + "<br/>" + serverPrivateIp2;
			}
		} else {
			return "";
		}
	}
	
	public String getVpnExternalIpAddressString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			StringBuilder retStr = new StringBuilder();
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getHiveAP() != null && gv.getExternalIpAddress()!=null && !gv.getExternalIpAddress().equals("")) {
						if (retStr.length()!=0){
							retStr.append("<br/>");
						}
						retStr.append(gv.getExternalIpAddress());
					}
				}
			}
			return retStr.toString();
		} else if (ipsecVpnType==IPSEC_VPN_LAYER_2){
			if (vpnServerType==VPN_SERVER_TYPE_SINGLE){
				return serverPublicIp1;
			} else {
				return serverPublicIp1 + "<br/>" + serverPublicIp2;
			}
		} else {
			return "";
		}
	}
	
	public String getVpnPrototcolString(){
		if (ipsecVpnType==IPSEC_VPN_LAYER_3){
			StringBuilder retStr = new StringBuilder();
			if (vpnGateWaysSetting!=null) {
				for(VpnGatewaySetting gv : vpnGateWaysSetting){
					if (gv.getHiveAP()!=null) {
						if(gv.getHiveAP().getRoutingProfile()!=null) {
							if (retStr.length()!=0){
								retStr.append("<br/>");
							}
							retStr.append(gv.getHiveAP().getRoutingProfile().getStringType());
						} else {
							if (retStr.length()!=0){
								retStr.append("<br/>");
							}
							retStr.append("&nbsp;");
						}
					}
				}
			}
			return retStr.toString();
		} else {
			return "";
		}
	}

	@Transient
	private String inputText;

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	@Transient
	private SubjectAltname_st subjects;

	@Transient
	private SubjectAltname_st getSubjects() {
		return HmBeAdminUtil.getSubjetAltName(this.getCertificate(), this
				.getOwner().getDomainName());
	}

	@Transient
	public String getIkeIdValue(int ikeType) {
		if (null == subjects) {
			subjects = getSubjects();
		}
		if (null != subjects) {
			if (!subjects.is_ok()) {
				return null;
			}
			switch (ikeType) {
			case IKE_ID_ADDRESS:
				List<String> ip = subjects.getIpRslt();
				if (null != ip && !ip.isEmpty()) {
					return ip.get(0);
				}
				break;
			case IKE_ID_ASN1DN:
				String asn1dn = subjects.getAsn1dn();
				if (null != asn1dn && !"".equals(asn1dn)) {
					return asn1dn;
				}
				break;
			case IKE_ID_FQDN:
				List<String> dns = subjects.getDnsRslt();
				if (null != dns && !dns.isEmpty()) {
					return dns.get(0);
				}
				break;
			case IKE_ID_UFQDN:
				List<String> email = subjects.getEmailRslt();
				if (null != email && !email.isEmpty()) {
					return email.get(0);
				}
				break;
			}
		}
		return null;
	}

	@Transient
	public String certificateDisplayStyle = "none";
	@Transient
	public String credentialDisplayStyle = "none";// by default
	@Transient
	public String serverAdvDisplayStyle = "none";
	@Transient
	public String clientAdvDisplayStyle = "none";
	@Transient
	public String importFileType;// type options: 'ca', 'cert' or 'key'

	public String getCertificateDisplayStyle() {
		return certificateDisplayStyle;
	}

	public void setCertificateDisplayStyle(String certificateDisplayStyle) {
		this.certificateDisplayStyle = certificateDisplayStyle;
	}

	public String getCredentialDisplayStyle() {
		return credentialDisplayStyle;
	}

	public void setCredentialDisplayStyle(String credentialDisplayStyle) {
		this.credentialDisplayStyle = credentialDisplayStyle;
	}

	public String getServerAdvDisplayStyle() {
		return serverAdvDisplayStyle;
	}

	public void setServerAdvDisplayStyle(String serverAdvDisplayStyle) {
		this.serverAdvDisplayStyle = serverAdvDisplayStyle;
	}

	public String getClientAdvDisplayStyle() {
		return clientAdvDisplayStyle;
	}

	public void setClientAdvDisplayStyle(String clientAdvDisplayStyle) {
		this.clientAdvDisplayStyle = clientAdvDisplayStyle;
	}

	public String getImportFileType() {
		return importFileType;
	}

	public void setImportFileType(String importFileType) {
		this.importFileType = importFileType;
	}

	@Override
	public VpnService clone() {
		try {
			return (VpnService) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Long getHiveApVpnServer1() {
		return hiveApVpnServer1;
	}

	public void setHiveApVpnServer1(Long hiveApVpnServer1) {
		this.hiveApVpnServer1 = hiveApVpnServer1;
	}

	public Long getHiveApVpnServer2() {
		return hiveApVpnServer2;
	}

	public void setHiveApVpnServer2(Long hiveApVpnServer2) {
		this.hiveApVpnServer2 = hiveApVpnServer2;
	}

	public String getServerDeaultGateway1() {
		return serverDeaultGateway1;
	}

	public void setServerDeaultGateway1(String serverDeaultGateway1) {
		this.serverDeaultGateway1 = serverDeaultGateway1;
	}

	public String getServerDeaultGateway2() {
		return serverDeaultGateway2;
	}

	public void setServerDeaultGateway2(String serverDeaultGateway2) {
		this.serverDeaultGateway2 = serverDeaultGateway2;
	}

	public short getProfileTunnelType() {
		return profileTunnelType;
	}

	public void setProfileTunnelType(short profileTunnelType) {
		this.profileTunnelType = profileTunnelType;
	}

	public List<VpnGatewaySetting> getVpnGateWaysSetting() {
		return vpnGateWaysSetting;
	}

	public void setVpnGateWaysSetting(List<VpnGatewaySetting> vpnGateWaysSetting) {
		this.vpnGateWaysSetting = vpnGateWaysSetting;
	}

	public List<UserProfileForTrafficL2> getUserProfileTrafficL2() {
		return userProfileTrafficL2;
	}

	public void setUserProfileTrafficL2(
			List<UserProfileForTrafficL2> userProfileTrafficL2) {
		this.userProfileTrafficL2 = userProfileTrafficL2;
	}

	public List<UserProfileForTrafficL3> getUserProfileTrafficL3() {
		return userProfileTrafficL3;
	}

	public void setUserProfileTrafficL3(
			List<UserProfileForTrafficL3> userProfileTrafficL3) {
		this.userProfileTrafficL3 = userProfileTrafficL3;
	}

	public short getRouteTrafficType() {
		return routeTrafficType;
	}

	public void setRouteTrafficType(short routeTrafficType) {
		this.routeTrafficType = routeTrafficType;
	}

	public DomainObject getDomObj() {
		return domObj;
	}

	public void setDomObj(DomainObject domObj) {
		this.domObj = domObj;
	}
	
	//label upgrade from low version to Dakar
	private boolean upgradeFlag;

	public boolean isUpgradeFlag() {
		return upgradeFlag;
	}

	public void setUpgradeFlag(boolean upgradeFlag) {
		this.upgradeFlag = upgradeFlag;
	}
}