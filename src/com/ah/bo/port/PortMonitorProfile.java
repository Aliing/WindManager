package com.ah.bo.port;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.util.MgrUtil;

@Embeddable
public class PortMonitorProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1131934981998735994L;

	public static String MONITOR_SESSION_PREFIX = "monitor_session_";
	
	private boolean enableMonitorSession = false;
	
	private short destinationPort;
	private String ingressPort;
	private String egressPort;
	private String bothPort;
	private String ingressVlan;
	private boolean enableVlans = false;
	private boolean enablePorts = false;

	@Transient
	private String monitorSessionName;

	@Transient
	private String portName;
	

	public PortMonitorProfile(short destinationPort, short hiveApModel) {
		this.monitorSessionName = MONITOR_SESSION_PREFIX +  DeviceInfType.getInstance(destinationPort, hiveApModel).getIndex();
		this.enableMonitorSession = false;
		this.destinationPort = destinationPort;

	}
	
	public PortMonitorProfile(){
		
	}

	public boolean isEnableMonitorSession() {
		return enableMonitorSession;
	}

	public void setEnableMonitorSession(boolean enableMonitorSession) {
		this.enableMonitorSession = enableMonitorSession;
	}

	public short getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(short destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getIngressPort() {
		return ingressPort;
	}

	public void setIngressPort(String ingressPort) {
		this.ingressPort = ingressPort;
	}

	public String getEgressPort() {
		return egressPort;
	}

	public void setEgressPort(String egressPort) {
		this.egressPort = egressPort;
	}

	public String getBothPort() {
		return bothPort;
	}

	public void setBothPort(String bothPort) {
		this.bothPort = bothPort;
	}

	public String getIngressVlan() {
		return ingressVlan;
	}

	public void setIngressVlan(String ingressVlan) {
		this.ingressVlan = ingressVlan;
	}

	public String getMonitorSessionName(short hiveApModel) {
		monitorSessionName = MONITOR_SESSION_PREFIX +  DeviceInfType.getInstance(destinationPort, hiveApModel).getIndex(); ;
		return monitorSessionName;
	}

	public void setMonitorSessionName(String monitorSessionName) {
		this.monitorSessionName = monitorSessionName;
	}

	@Transient
	private String ingressPorts;
	
	@Transient
	private String egressPorts;
	
	@Transient
	private String bothPorts;
	
	public String getIngressPorts() {
		return this.addPrefix(ingressPort);
	}

	public void setIngressPorts(String ingressPorts) {
		this.ingressPorts = ingressPorts;
	}

	public String getEgressPorts() {
		return this.addPrefix(egressPort);
	}

	public void setEgressPorts(String egressPorts) {
		this.egressPorts = egressPorts;
	}

	public String getBothPorts() {
		return this.addPrefix(bothPort);
	}

	public void setBothPorts(String bothPorts) {
		this.bothPorts = bothPorts;
	}

	public String getPortName() {
		portName = MgrUtil.getEnumString("enum.switch.interface." + this.getDestinationPort());
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String addPrefix(String ports){
		if(ports == null || "".equalsIgnoreCase(ports)){
			return "";
		}
		String[] portRange = CLICommonFunc.mergeRange(ports).split(",");
		if(portRange == null || "".equals(portRange) || portRange.length == 0){
			return "";
		}
		for (int i = 0; i < portRange.length; i++){
			if(portRange[i] != null && !"".equalsIgnoreCase(portRange[i])){
				if(portRange[i].indexOf("-") > 0){
					String[] tmpObj = portRange[i].split("-");
					if(tmpObj != null && tmpObj.length > 1){
						portRange[i] = MgrUtil.getEnumString("enum.switch.interface." + tmpObj[0].trim()) + "-" + MgrUtil.getEnumString("enum.switch.interface." + tmpObj[1].trim());
					}
				}else{
					portRange[i] = MgrUtil.getEnumString("enum.switch.interface." + portRange[i]);
				}
			}
		}
		
		String resStr = "";
		for(String strTemp : portRange){
			if("".equals(resStr)){
				resStr += strTemp;
			}else{
				resStr += "," + strTemp;
			}
		}
		return resStr;
	}

	public boolean isEnableVlans() {
		return enableVlans;
	}

	public void setEnableVlans(boolean enableVlans) {
		this.enableVlans = enableVlans;
	}
	
	public boolean isEnablePorts() {
		return enablePorts;
	}

	public void setEnablePorts(boolean enablePorts) {
		this.enablePorts = enablePorts;
	}
}
