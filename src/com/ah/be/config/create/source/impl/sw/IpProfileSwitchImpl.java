package com.ah.be.config.create.source.impl.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.source.IpProfileInt;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.IpProfileImpl;
import com.ah.be.config.create.source.impl.baseImpl.IpProfileBaseImpl;
import com.ah.be.config.create.source.impl.branchRouter.IpProfileBRImpl;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.igmp.MulticastGroupInterface;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;

public class IpProfileSwitchImpl extends IpProfileImpl {
	
	private IpProfileInt ipBrImpl;
	
	private SwitchSettings switchSettings;
	
	private List<IgmpPolicy> policyInfoList = new ArrayList<IgmpPolicy>();
	
	private List<Map<String, Object>> groupInfoList = new ArrayList<Map<String, Object>>();
	
	private List<Short> wanPortConstantList = new ArrayList<Short>();
	
	private boolean globalEnableIgmpSnooping = false;
	private boolean globalEnableImmediateLeave = false;
	private boolean globalEnableReportSuppression = false;
	private int globalDelayLeaveQueryInterval = 0;
	private int globalDelayLeaveQueryCount = 0;
	private int globalRouterPortAginTime = 0;
	private int globalRobustnessCount = 0;
	
	public IpProfileSwitchImpl(HiveAp hiveAp, ConfigureProfileFunction function) {
		super(hiveAp);
		this.switchSettings = hiveAp.getConfigTemplate().getSwitchSettings();
	    init();
	    if(hiveAp.isBranchRouter()){
	    	ipBrImpl = new IpProfileBRImpl(hiveAp, function);
	    }
	    ipBrImpl = (ipBrImpl == null) ? new IpProfileBaseImpl(hiveAp) : ipBrImpl;
	}
	
	private List<Short> setWanPortList() {
		PortGroupProfile portProfile = hiveAp.getPortGroup();
		if (portProfile != null) {
			List<Short> ports = portProfile.getPortFinalValuesByPortType(DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
			if (null != ports) {
				wanPortConstantList.addAll(ports);
			}
			ports = portProfile.getPortFinalValuesByPortType(DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);
			if (null != ports) {
				wanPortConstantList.addAll(ports);
			}
			ports = portProfile.getPortFinalValuesByPortType(DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
			if (null != ports) {
				wanPortConstantList.addAll(ports);
			}
		}
//		for (Short s : wanPortConstantList) {
//			resultList.add(DeviceInfType.getInstance(s).getIndex());
//		}
		return wanPortConstantList;
	}
	
	private boolean isWanInterface(MulticastGroupInterface groupInterface) {
		Short port = 0;
		if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_ETH) {
			port = DeviceInfType.Gigabit.getFinalValue(groupInterface.getInterfacePort(), hiveAp.getHiveApModel());
		} else if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_PORTCHANNEL) {
			port = DeviceInfType.PortChannel.getFinalValue(groupInterface.getInterfacePort(), hiveAp.getHiveApModel());
		} else if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_SFP) {
			port = DeviceInfType.SFP.getFinalValue(groupInterface.getInterfacePort(), hiveAp.getHiveApModel());
		}
		if (wanPortConstantList.contains(port)) {
			return true;
		}
		return false;
	}
	
	public void init() {
		setWanPortList();
		if (isConfigIGMPInDevicePage()) {
			globalEnableIgmpSnooping = hiveAp.isOverrideIgmpSnooping();
			globalEnableImmediateLeave = hiveAp.isEnableImmediateLeave();
			globalEnableReportSuppression = hiveAp.isEnableReportSuppression();
			globalDelayLeaveQueryInterval = hiveAp.getGlobalDelayLeaveQueryInterval();
			globalDelayLeaveQueryCount = hiveAp.getGlobalDelayLeaveQueryCount();
			globalRouterPortAginTime = hiveAp.getGlobalRouterPortAginTime();
			globalRobustnessCount = hiveAp.getGlobalRobustnessCount();
			policyInfoList.addAll(hiveAp.getIgmpPolicys());
			for (MulticastGroup group : hiveAp.getMulticastGroups()) {
				for (MulticastGroupInterface groupInterface : group.getInterfaces()) {
					if (isWanInterface(groupInterface)) {
						continue;
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("interface", getInterfaceStr(groupInterface));
					map.put("vlanId", group.getVlanId());
					map.put("ipAddress", group.getIpAddress());
					groupInfoList.add(map);
				}
			}
		} 
		else if (isConfigIGMPInNetWorkPolicy()) {
			globalEnableIgmpSnooping = switchSettings.isEnableIgmpSnooping();
			globalEnableImmediateLeave = switchSettings.isEnableImmediateLeave();
			globalEnableReportSuppression = switchSettings.isEnableReportSuppression();
			globalDelayLeaveQueryInterval = switchSettings.getGlobalDelayLeaveQueryInterval();
			globalDelayLeaveQueryCount = switchSettings.getGlobalDelayLeaveQueryCount();
			globalRouterPortAginTime = switchSettings.getGlobalRouterPortAginTime();
			globalRobustnessCount = switchSettings.getGlobalRobustnessCount();
		}
		
	}
	
	public int getIpNetSize(){
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.getIpNetSize();
		}else{
			return super.getIpNetSize();
		}
	}
	
	public String getIpNetName(int index){
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.getIpNetName(index);
		}else{
			return super.getIpNetName(index);
		}
	}
	
	private String getInterfaceStr(MulticastGroupInterface groupInterface) {
		String interfaceStr = "";
		int portIndex = groupInterface.getInterfacePort();
		if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_ETH) {
			interfaceStr = DeviceInfType.Gigabit.getCLIName(portIndex, hiveAp.getHiveApModel());
		} else if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_PORTCHANNEL) {
			interfaceStr = DeviceInfType.PortChannel.getCLIName(portIndex, hiveAp.getHiveApModel());
		} else if (groupInterface.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_SFP) {
			interfaceStr = DeviceInfType.SFP.getCLIName(portIndex, hiveAp.getHiveApModel());
		}
		return interfaceStr;
	}
	
	public boolean isConfigIGMP() {
		//return (isConfigIGMPInDevicePage() || isConfigIGMPInNetWorkPolicy());
	    return globalEnableIgmpSnooping;
	}
	
	public boolean isConfigIGMPInDevicePage() {
		if (hiveAp.isOverrideIgmpSnooping()) {
			return true;
		}
		return false;
	}
	
	public boolean isConfigIGMPInNetWorkPolicy() {
		if (switchSettings != null && switchSettings.isEnableIgmpSnooping()) {
			return true;
		}
		return false;
	}
	
	public boolean isEnableIgmpSnooping() {
		return true;
	}

	@Override
	public boolean isEnableImmediateLeave() {
		return this.globalEnableImmediateLeave;
	}

	@Override
	public boolean isEnableReportSuppression() {
		return this.globalEnableReportSuppression;
	}

	@Override
	public int getGlobalDelayLeaveQueryInterval() {
		return this.globalDelayLeaveQueryInterval;
	}

	@Override
	public int getGlobalDelayLeaveQueryCount() {
		return this.globalDelayLeaveQueryCount;
	}

	@Override
	public int getGlobalRouterPortAginTime() {
		return this.globalRouterPortAginTime;
	}

	@Override
	public int getGlobalRobustnessCount() {
		return this.globalRobustnessCount;
	}
	
    public int getIgmpPolicySize() {
    	return this.policyInfoList.size();
    }
    
    public boolean getIgmpPolicyEnableSnooping(int index) {
    	return policyInfoList.get(index).isIgmpSnooping();
    }
    
    public boolean getIgmpPolicyEnableImmediateLeave(int index) {
    	return policyInfoList.get(index).isImmediateLeave();
    }
    
    public int getIgmpPolicyDelayLeaveQueryCount(int index) {
    	return policyInfoList.get(index).getDelayLeaveQueryCount();
    }
    
    public int getIgmpPolicyDelayLeaveQueryInterval(int index) {
    	return policyInfoList.get(index).getDelayLeaveQueryInterval();
    }
    
    public int getIgmpPolicyRobustnessCount(int index) {
    	return policyInfoList.get(index).getRobustnessCount();
    }
    
    public int getIgmpPolicyRouterPortAginTime(int index) {
    	return policyInfoList.get(index).getRouterPortAginTime();
    }
    
    public int getIgmpPolicyVlanId(int index) {
    	return policyInfoList.get(index).getVlanId();
    }
    
    public int getIgmpMulticastGroupSize() {
    	return this.groupInfoList.size();
    }
    
    public String getMulticastGroupValue(int index) {
    	return groupInfoList.get(index).get("ipAddress") 
	            + " vlan " + groupInfoList.get(index).get("vlanId")
	            + " interface " + groupInfoList.get(index).get("interface");
    }
    
	public boolean isConfigNatPolicy() {
		if (ipBrImpl == null) {
			return false;
		}
		return ipBrImpl.isConfigNatPolicy();
	}
    
	public int getNatPolicySize() {
		return ipBrImpl.getNatPolicySize();
	}

	public String getNatPolicyName(int index) {
		return ipBrImpl.getNatPolicyName(index);
	}

	public boolean isNatPolicyConfigMatch(int index) {
		return ipBrImpl.isNatPolicyConfigMatch(index);
	}

	public boolean isNatPolicyConfigVirtualHost(int index) {
		return ipBrImpl.isNatPolicyConfigVirtualHost(index);
	}

	public String getNatPolicyMatchInsideValue(int index) {
		return ipBrImpl.getNatPolicyMatchInsideValue(index);
	}

	public String getNatPolicyMatchOutsideValue(int index) {
		return ipBrImpl.getNatPolicyMatchOutsideValue(index);
	}
	
	public String getNatPolicyVhostInsideHostValue(int index) {
		return ipBrImpl.getNatPolicyVhostInsideHostValue(index);
	}

	public String getNatPolicyVhostInsidePortValue(int index) {
		return ipBrImpl.getNatPolicyVhostInsidePortValue(index);
	}

	public String getNatPolicyVhostOutsidePortValue(int index) {
		return ipBrImpl.getNatPolicyVhostOutsidePortValue(index);
	}

	public String getNatPolicyVhostProtocolValue(int index) {
		return ipBrImpl.getNatPolicyVhostProtocolValue(index);
	}
	
	public boolean isConfigPathAndTcpMss() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.isConfigPathAndTcpMss();
		}else{
			return super.isConfigPathAndTcpMss();
		}
	}
	
	public boolean isIpPathMtuDiscoveryEnable() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.isIpPathMtuDiscoveryEnable();
		}else{
			return super.isIpPathMtuDiscoveryEnable();
		}
	}

	public boolean isIpTcpMssThresholdEnable() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.isIpTcpMssThresholdEnable();
		}else{
			return super.isIpTcpMssThresholdEnable();
		}
	}
	
	public boolean isConfigThresholdSize() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.isConfigThresholdSize();
		}else{
			return super.isConfigThresholdSize();
		}
	}

	public int getThresholdSize() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.getThresholdSize();
		}else{
			return super.getThresholdSize();
		}
	}

	public boolean isConfigL3VpnThresholdSize() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.isConfigL3VpnThresholdSize();
		}else{
			return super.isConfigL3VpnThresholdSize();
		}
	}
	
	public int getL3VpnThresholdSize() {
		if(hiveAp.isBranchRouter()){
			return ipBrImpl.getL3VpnThresholdSize();
		}else{
			return super.getL3VpnThresholdSize();
		}
	}

}
