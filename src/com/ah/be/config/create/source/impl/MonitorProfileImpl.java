package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.MonitorProfileInt;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.AhInterface.DeviceInfUnionType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.port.PortMonitorProfile;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MonitorProfileImpl implements MonitorProfileInt {
	
	public static final String ETH_NAME = "eth1/";
	
	private HiveAp hiveAp;
	
	private List<MonitorSessionCli> monitorList = new ArrayList<MonitorSessionCli>();
	
	public MonitorProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		init();
	}
	
	private void init(){
		if(hiveAp == null || hiveAp.getPortGroup()==null || 
				hiveAp.getPortGroup().getMonitorProfiles()==null ||
				hiveAp.getPortGroup().getMonitorProfiles().isEmpty()){
			return;
		}
		
		for(PortMonitorProfile monitor : hiveAp.getPortGroup().getMonitorProfiles()){
			if(!monitor.isEnableMonitorSession()){
				continue;
			}
			
			if(monitor.isEnablePorts() 
					&& !monitor.isEnableVlans()
					&& monitor.getIngressPort().isEmpty() 
					&& monitor.getBothPort().isEmpty()
					&& monitor.getEgressPort().isEmpty()){
				continue;
			}
			
			if(!monitor.isEnablePorts() && !monitor.isEnableVlans()){
				continue;
			}
			
			if(!monitor.isEnablePorts() && monitor.isEnableVlans()){
				if(monitor.getIngressVlan().trim().length() == 0){
					continue;
				}
			}
			
			MonitorSessionCli monitorCli = new MonitorSessionCli();
			monitorCli.setMonitor(monitor);
			monitorList.add(monitorCli);
			if(monitor.getIngressVlan() != null && !"".equals(monitor.getIngressVlan()) && monitor.isEnableVlans()){
				monitorCli.getVlanList().addAll(stringToList(monitor.getIngressVlan()));
			}
			
			if(monitor.isEnablePorts()){
				if(monitor.getIngressPort() != null && !"".equals(monitor.getIngressPort())){
					List<String> ingree = stringToList(monitor.getIngressPort());
					for(String inf : ingree){
						InfType type = new InfType();
						type.setInfName(getInterfaceName(inf));
						type.setType(InfType.Type_Ingress);
						monitorCli.getInfList().add(type);
					}
				}
				if(monitor.getEgressPort() != null && !"".equals(monitor.getEgressPort())){
					List<String> ingree = stringToList(monitor.getEgressPort());
					for(String inf : ingree){
						InfType type = new InfType();
						type.setInfName(getInterfaceName(inf));
						type.setType(InfType.Type_Egress);
						monitorCli.getInfList().add(type);
					}
				}
				if(monitor.getBothPort() != null && !"".equals(monitor.getBothPort())){
					List<String> ingree = stringToList(monitor.getBothPort());
					for(String inf : ingree){
						InfType type = new InfType();
						type.setInfName(getInterfaceName(inf));
						type.setType(InfType.Type_Both);
						monitorCli.getInfList().add(type);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<String> stringToList(String source){
		if(source == null || "".equals(source)){
			return new ArrayList<String>();
		}
		String[] args = source.split(",");
		for(int index=0; index<args.length; index++){
			String str = args[index];
			if(!str.contains(" - ")){
				str = str.replace("-", " - ");
			}
			args[index] = str;
		}
		return ((List<String>)(Arrays.asList(args)));
	}
	
	public static class MonitorSessionCli{
		private PortMonitorProfile monitor;
		private List<String> vlanList = new ArrayList<String>();
		private List<InfType> infList = new ArrayList<InfType>();
		public PortMonitorProfile getMonitor() {
			return monitor;
		}
		public void setMonitor(PortMonitorProfile monitor) {
			this.monitor = monitor;
		}
		public List<String> getVlanList() {
			return vlanList;
		}
		public void setVlanList(List<String> vlanList) {
			this.vlanList = vlanList;
		}
		public List<InfType> getInfList() {
			return infList;
		}
		public void setInfList(List<InfType> infList) {
			this.infList = infList;
		}
		
	}
	
	public static class InfType{
		public static final short Type_Ingress = 1;
		public static final short Type_Egress = 2;
		public static final short Type_Both = 3;
		private String infName;
		private short type;
		public String getInfName() {
			return infName;
		}
		public void setInfName(String infName) {
			this.infName = infName;
		}
		public short getType() {
			return type;
		}
		public void setType(short type) {
			this.type = type;
		}
	}

	public int getSessionSize() {
		return monitorList.size();
	}

	public String getSessionName(int i) {
		return monitorList.get(i).getMonitor().getMonitorSessionName(hiveAp.getHiveApModel());
	}

	public boolean isSessionEnable(int i) {
		return monitorList.get(i).getMonitor().isEnableMonitorSession();
	}

	private String getInterfaceName(String name){
		int num = name.indexOf("-");
		if(num > 0){
			DeviceInfUnionType unionTypeStart = DeviceInfType.getInstance(Short.valueOf(name.substring(0, num).trim()), hiveAp.getHiveApModel());
			DeviceInfUnionType unionTypeEnd = DeviceInfType.getInstance(Short.valueOf(name.substring(num+1).trim()), hiveAp.getHiveApModel());
			return unionTypeStart.getCLIName(hiveAp.getHiveApModel()) + " - " + unionTypeEnd.getCLIName(hiveAp.getHiveApModel());
		}else{
			DeviceInfUnionType unionType = DeviceInfType.getInstance(Short.valueOf(name), hiveAp.getHiveApModel());
			return unionType.getCLIName(hiveAp.getHiveApModel());
		}
	}
	
	public String getInfDestinationName(int i) {
		return getInterfaceName(Short.toString(monitorList.get(i).getMonitor().getDestinationPort()));
	}

	public int getSourceVlanSize(int i) {
		return monitorList.get(i).getVlanList().size();
	}

	public String getSourceVlanName(int i, int j) {
		return monitorList.get(i).getVlanList().get(j);
	}

	public boolean isSourceVlanIngress(int i, int j) {
		return true;
	}

	public int getSourceInterfaceSize(int i) {
		return monitorList.get(i).getInfList().size();
	}

	public String getSourceInterfaceName(int i, int j) {
		return monitorList.get(i).getInfList().get(j).getInfName();
	}

	public boolean isConfigSourceInterfaceIngress(int i, int j) {
		return monitorList.get(i).getInfList().get(j).getType() == InfType.Type_Ingress;
	}

	public boolean isConfigSourceInterfaceBoth(int i, int j) {
		return monitorList.get(i).getInfList().get(j).getType() == InfType.Type_Both;
	}

	public boolean isConfigSourceInterfaceEgress(int i, int j) {
		return monitorList.get(i).getInfList().get(j).getType() == InfType.Type_Egress;
	}
}
