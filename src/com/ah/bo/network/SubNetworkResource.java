package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.hiveap.HiveApAction;

/**
 * @author		zhang
 * @version		V1.0.0.0 
 */

@Entity
@Table(name = "SUB_NETWORK_RESOURCE")
@org.hibernate.annotations.Table(appliesTo = "SUB_NETWORK_RESOURCE", indexes = {
		@Index(name = "SUB_NETWORK_RESOURCE_OWNER", columnNames = { "OWNER" }),
		@Index(name = "SUB_NETWORK_RESOURCE_MAC", columnNames = { "HIVEAPMAC" }),
		@Index(name = "SUB_NETWORK_RESOURCE_STATUS", columnNames = { "STATUS" }),
		@Index(name = "SUB_NETWORK_RESOURCE_NETWORKID", columnNames = { "NETWORKID" })
		})
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class SubNetworkResource implements HmBo {

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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "networkId")
	private VpnNetwork vpnNetwork;

	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String network;

	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String parentNetwork;
	
	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String localNetwork;
	
	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String parentLocalNetwork;
	
	private boolean enableNat = false;
	
	/**
	 * firstIp actually is default gateway
	 * it can be first IP and last IP
	 */
	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String firstIp;
	
	private long ipStartLong;

	private long ipEndLong;

	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String ipPoolStart;
	
	@Column(length = HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String ipPoolEnd;
	
	@Column(length = 12)
	private String hiveApMac;

	private short hiveApMgtx = -1;
	
	private boolean vipIpAddress;

	public static final short IP_SUBBLOCKS_STATUS_FREE = 0;
	public static final short IP_SUBBLOCKS_STATUS_PRE_USE = 1;
	public static final short IP_SUBBLOCKS_STATUS_USED = 2;
	public static final short IP_SUBBLOCKS_STATUS_PRE_REMOVE = 3;

	private short status = IP_SUBBLOCKS_STATUS_FREE;
	
	@Transient
	public String getDhcpPool() {
		return ipPoolStart + "~" + ipPoolEnd;
	}
	@Transient
	private HiveAp relativeAP;
	public HiveAp getRelativeAP() {
		if(null == this.relativeAP) {
			try {
				this.relativeAP = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", hiveApMac, this.owner.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == this.relativeAP) {
			this.relativeAP = new HiveAp();
			this.relativeAP.setHostName("Unknown");
			this.relativeAP.setClassificationTag1("");
			this.relativeAP.setClassificationTag2("");
			this.relativeAP.setClassificationTag3("");
		}
		return this.relativeAP;
	}
	@Transient
	public String getPrentLocalNetworkStr() {
		if(StringUtils.isBlank(this.localNetwork)) {
			if(null != vpnNetwork) {
				return vpnNetwork.getNetworkName();
			}
		}
		return this.parentLocalNetwork;
	}
	
	
	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getParentNetwork() {
		return parentNetwork;
	}

	public void setParentNetwork(String parentNetwork) {
		this.parentNetwork = parentNetwork;
	}

	public String getIpPoolStart() {
		return ipPoolStart;
	}

	public void setIpPoolStart(String ipPoolStart) {
		this.ipPoolStart = ipPoolStart;
	}

	public String getIpPoolEnd() {
		return ipPoolEnd;
	}

	public void setIpPoolEnd(String ipPoolEnd) {
		this.ipPoolEnd = ipPoolEnd;
	}

	public String getHiveApMac() {
		return hiveApMac;
	}

	public void setHiveApMac(String hiveApMac) {
		this.hiveApMac = hiveApMac;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}
	
	public String getFirstIp() {
		return firstIp;
	}

	public void setFirstIp(String firstIp) {
		this.firstIp = firstIp;
	}

	public short getHiveApMgtx() {
		return hiveApMgtx;
	}

	public void setHiveApMgtx(short hiveApMgtx) {
		this.hiveApMgtx = hiveApMgtx;
	}
	
	public boolean isVipIpAddress() {
		return vipIpAddress;
	}

	public void setVipIpAddress(boolean vipIpAddress) {
		this.vipIpAddress = vipIpAddress;
	}
	
	public long getIpStartLong() {
		return ipStartLong;
	}

	public void setIpStartLong(long ipStartLong) {
		this.ipStartLong = ipStartLong;
	}

	public long getIpEndLong() {
		return ipEndLong;
	}

	public void setIpEndLong(long ipEndLong) {
		this.ipEndLong = ipEndLong;
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
		return this.getNetwork();
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
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public VpnNetwork getVpnNetwork() {
		return vpnNetwork;
	}

	public void setVpnNetwork(VpnNetwork vpnNetwork) {
		this.vpnNetwork = vpnNetwork;
	}
	
	@Transient
	public String getNatPolicyName() {
		return StringUtils.isNotBlank(network) ? network.replace(".", "-").replace("/", "-") + "_policy" : "";
	}
	
	public String getLocalNetwork() {
		return localNetwork;
	}
	
	public void setLocalNetwork(String localNetwork) {
		this.localNetwork = localNetwork;
	}
	
	public String getParentLocalNetwork() {
		return parentLocalNetwork;
	}
	
	public void setParentLocalNetwork(String parentLocalNetwork) {
		this.parentLocalNetwork = parentLocalNetwork;
	}
	
	public boolean isEnableNat() {
		return enableNat;
	}

	public void setEnableNat(boolean enableNat) {
		this.enableNat = enableNat;
	}
	
	@Transient
	public Set<Short> getPortMapping(){
		Set<Short> ports = new HashSet<>();
		if(vpnNetwork == null){
			return ports;
		}
		HiveAp hiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", this.hiveApMac,new HiveApAction());
		
		PortGroupProfile portGroup = hiveAP.getPortGroup();
		if(portGroup == null || portGroup.getBasicProfiles() == null){
			return ports;
		}
		List<PortBasicProfile> basicProfiles = portGroup.getBasicProfiles();
		Map<Short,Set<Vlan>> portVlanMap = new HashMap<>();
		Map<Short,Set<Integer>> allowVlanMap = new HashMap<>();
		for(PortBasicProfile basic : basicProfiles){
			Set<Vlan> vlanSet = new HashSet<>();
			Set<Integer> allowVlanSet = new HashSet<>();
			PortAccessProfile accessProfile = basic.getAccessProfile();
			if(accessProfile != null){
				Vlan nativeVlan = accessProfile.getNativeVlan();
				if(nativeVlan != null){
					vlanSet.add(nativeVlan);
				}
				String allowVlan = accessProfile.getAllowedVlan();
				if(allowVlan != null){
					allowVlanSet = mergeRange(allowVlan);
				}
				if(accessProfile.getVoiceVlan() != null){
				    vlanSet.add(accessProfile.getVoiceVlan());
				}
				if(accessProfile.getDataVlan() != null){
				    vlanSet.add(accessProfile.getDataVlan());
				}
				if(accessProfile.getDefUserProfile() != null 
					&& accessProfile.getDefUserProfile().getVlan() != null){
					vlanSet.add(accessProfile.getDefUserProfile().getVlan());
				}
				if(accessProfile.getSelfRegUserProfile() != null 
					&& accessProfile.getSelfRegUserProfile().getVlan() != null){
					vlanSet.add(accessProfile.getSelfRegUserProfile().getVlan());
				}
				if(accessProfile.getAuthOkUserProfile() != null){
					for(UserProfile userProfile : accessProfile.getAuthOkUserProfile()){
						if(userProfile.getVlan() != null){
							vlanSet.add(userProfile.getVlan());
						}
					}
				}
				if(accessProfile.getAuthOkDataUserProfile() != null){
					for(UserProfile userProfile : accessProfile.getAuthOkDataUserProfile()){
						if(userProfile.getVlan() != null){
							vlanSet.add(userProfile.getVlan());
						}
					}
				}
				if(accessProfile.getAuthFailUserProfile() != null){
					for(UserProfile userProfile : accessProfile.getAuthFailUserProfile()){
						if(userProfile.getVlan() != null){
							vlanSet.add(userProfile.getVlan());
						}
					}
				}
			}
			
			String[] eths = basic.getETHs();
			if(eths != null){
				for(String eth : eths){
					short sEth = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eth), hiveAP.getHiveApModel());
					if(portVlanMap.containsKey(sEth)){
						portVlanMap.get(sEth).addAll(vlanSet);
					} else {
						portVlanMap.put(sEth, vlanSet);
					}
					
					if(allowVlanMap.containsKey(sEth)){
						allowVlanMap.get(sEth).addAll(allowVlanSet);
					} else {
						allowVlanMap.put(sEth, allowVlanSet);
					}
					
				}
			}
			String[] sfps = basic.getSFPs();
			if(sfps != null){
				for(String sfp : sfps){
					short sSfp = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfp), hiveAP.getHiveApModel());
					if(portVlanMap.containsKey(sSfp)){
						portVlanMap.get(sSfp).addAll(vlanSet);
					} else {
						portVlanMap.put(sSfp, vlanSet);
					}
					
					if(allowVlanMap.containsKey(sSfp)){
						allowVlanMap.get(sSfp).addAll(allowVlanSet);
					} else {
						allowVlanMap.put(sSfp, allowVlanSet);
					}
				}
			}
			String[] usbs = basic.getUSBs();
			if(usbs != null){
				for(String usb : usbs){
					//usb model no need hiveApModel.
					short sUsb = DeviceInfType.USB.getFinalValue(Integer.valueOf(usb), hiveAP.getHiveApModel());
					if(portVlanMap.containsKey(sUsb)){
						portVlanMap.get(sUsb).addAll(vlanSet);
					} else {
						portVlanMap.put(sUsb, vlanSet);
					}
					
					if(allowVlanMap.containsKey(sUsb)){
						allowVlanMap.get(sUsb).addAll(allowVlanSet);
					} else {
						allowVlanMap.put(sUsb, allowVlanSet);
					}
				}
			}
		}
		
		if(hiveAP.getConfigTemplate() != null){
			List<ConfigTemplateVlanNetwork> vlanNetworkList = hiveAP.getConfigTemplate().getVlanNetwork();
			for(ConfigTemplateVlanNetwork vlanNetwork : vlanNetworkList){
				if(vpnNetwork.equals(vlanNetwork.getNetworkObj())){
					Vlan vlan = vlanNetwork.getVlan();
					if(vlan != null){
						for(Short key :portVlanMap.keySet()){
							Set<Vlan> vlans = portVlanMap.get(key);
							if(vlans.contains(vlan)){
								ports.add(key);
							}
						}
						for(Short key :allowVlanMap.keySet()){
							Set<Integer> allowVlans = allowVlanMap.get(key);
							if(allowVlans != null && !allowVlans.isEmpty()){
								String sql = "from "+Vlan.class.getSimpleName() + " vl";
								String where =" exists(select 1 from vl.items it where it.vlanId  in (:s1))";
								FilterParams filter = null;
								filter = new FilterParams(where, new Object[]{allowVlans});
								List<?> vlanList = (List<Vlan>) QueryUtil.executeQuery(sql, null, filter);
								if(vlanList.contains(vlan)){
									ports.add(key);
								}
							}
						}
					}
				}
			}
		}
		
		return ports;
	}

	@Transient
	private Set<Integer> mergeRange(String strSource){
		Set<Integer> vlanSet = new HashSet<>();
		if(strSource == null || "".equals(strSource)){
			return vlanSet;
		}
		
		if("all".equalsIgnoreCase(strSource)){
			for(int index=1; index<4095; index++){
				vlanSet.add(index);
			}
		} else {
			String[] attrArr = strSource.split(",");
			for(int index=0; index<attrArr.length; index++) {
				try{
					String vlanStr = attrArr[index];
					if(vlanStr.indexOf("-") > 0){
						int frome, to;
						frome = Integer.valueOf((vlanStr.substring(0, vlanStr.indexOf("-"))).trim());
						to = Integer.valueOf((vlanStr.substring(vlanStr.indexOf("-") +1)).trim());
						for(int i=frome; i<=to; i++){
							vlanSet.add(i);
						}
					}else{
						vlanSet.add(Integer.valueOf(vlanStr.trim()));
					}
				}catch(Exception ex){
				}
			}
		}
		
		return vlanSet;
	}
	
	@Override
	public boolean equals(Object obj){
		SubNetworkResource resource;
		if(obj instanceof SubNetworkResource){
			resource = (SubNetworkResource)obj;
		}else{
			return false;
		}
		
		if(this.localNetwork != null && this.localNetwork.equals(resource.getLocalNetwork())){
			
		}else if(this.localNetwork == null && resource.getLocalNetwork() == null){
			
		}else{
			return false;
		}
		
		if(this.network != null && this.network.equals(resource.getNetwork())){
			
		}else if(this.network == null && resource.getNetwork() == null){
			
		}else{
			return false;
		}
		
		if(this.ipPoolStart != null && this.ipPoolStart.equals(resource.getIpPoolStart())){
			
		}else if(this.ipPoolStart == null && resource.getIpPoolStart() == null){
			
		}else{
			return false;
		}
		
		if(this.firstIp != null && this.firstIp.equals(resource.getFirstIp())){
			
		}else if(this.firstIp == null && resource.getFirstIp() == null){
			
		}else{
			return false;
		}
		
		if(this.ipPoolEnd != null && this.ipPoolEnd.equals(resource.getIpPoolEnd())){
			
		}else if(this.ipPoolEnd == null && resource.getIpPoolEnd() == null){
			
		}else{
			return false;
		}
		
		return true;
	}

}