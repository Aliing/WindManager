package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AmrpProfileInt;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.xml.be.config.AmrpConcreteInterface;
import com.ah.xml.be.config.AmrpHeartbeat;
import com.ah.xml.be.config.AmrpHeartbeatInterval;
import com.ah.xml.be.config.AmrpHeartbeatRetry;
import com.ah.xml.be.config.AmrpInterface;
import com.ah.xml.be.config.AmrpInterfacePriority;
import com.ah.xml.be.config.AmrpNeighborMetric;
import com.ah.xml.be.config.AmrpObj;
import com.ah.xml.be.config.AmrpVpnTunnel;

/**
 * 
 * @author zhang
 *
 */
public class CreateAmrpTree {
	
	private AmrpProfileInt amrpImp;
	private AmrpObj amrpObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> amrpChildList_1 = new ArrayList<Object>();
	private List<Object> amrpChildList_2 = new ArrayList<Object>();
	private List<Object> amrpChildList_3 = new ArrayList<Object>();
	
	private List<Object> neighborChildList_1 = new ArrayList<Object>();
	private List<Object> neighborChildList_2 = new ArrayList<Object>();
	private List<Object> neighborChildList_3 = new ArrayList<Object>();
	private List<Object> neighborChildList_4 = new ArrayList<Object>();

	public CreateAmrpTree(AmrpProfileInt amrpImpl, GenerateXMLDebug oDebug) throws Exception {
		this.amrpImp = amrpImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(this.amrpImp != null ){
			amrpObj = new AmrpObj();
			generateAmrpLevel_1();
		}
	}
	
	public AmrpObj getAmrpObj(){
		return this.amrpObj;
	}
	
	private void generateAmrpLevel_1() throws Exception {
		/**
		 * <amrp>		AmrpObj
		 */
		
		/** attribute: updateTime */
		amrpObj.setUpdateTime(amrpImp.getUpdateTime());
		
		/** element: <amrp>.<metric> */
		AmrpObj.Metric metricObj = new AmrpObj.Metric();
		amrpChildList_1.add(metricObj);
		amrpObj.setMetric(metricObj);
		
		/** element: <amrp>.<neighbor> */
		oDebug.debug("/configuration/amrp", 
				"neighbor", GenerateXMLDebug.CONFIG_ELEMENT,
				amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
		for(int i=0; i<amrpImp.getAmrpNeighborSize(); i++ ){
			amrpObj.getNeighbor().add(this.createAmrpObjNeighbor(i));
		}
		
		/** element: <amrp>.<vpn-tunnel> */
		oDebug.debug("/configuration/amrp", 
				"vpn-tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
				amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
		if(amrpImp.isVpnClient()){
			AmrpVpnTunnel vpnTunnelObj = new AmrpVpnTunnel();
			amrpChildList_1.add(vpnTunnelObj);
			amrpObj.setVpnTunnel(vpnTunnelObj);
		}
		
		/** element: <amrp>.<interface> */
		AmrpInterface interfaceObj = new AmrpInterface();
		amrpChildList_1.add(interfaceObj);
		amrpObj.setInterface(interfaceObj);
		
		generateAmrpLevel_2();
	}
	
	private void generateAmrpLevel_2() throws Exception {
		/**
		 * <amrp>.<metric>			AmrpObj.Metric
		 * <amrp>.<vpn-tunnel>		AmrpVpnTunnel
		 * <amrp>.<interface>		AmrpInterface
		 */
		for(Object childObj : amrpChildList_1){
			
			/** element: <amrp>.<metric> */
			if(childObj instanceof AmrpObj.Metric){
				AmrpObj.Metric metricObj = (AmrpObj.Metric)childObj;
				
				/** element: <amrp>.<metric>.<type> */
				AmrpObj.Metric.Type typeObj = new AmrpObj.Metric.Type();
				amrpChildList_2.add(typeObj);
				metricObj.setType(typeObj);
				
				/** element: <amrp>.<metric>.<poll-interval> */
				oDebug.debug("/configuration/amrp/metric", 
						"poll-interval", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				Object[][] pollIntervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, amrpImp.getAmrpPollInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				metricObj.setPollInterval(
						(AmrpObj.Metric.PollInterval)CLICommonFunc.createObjectWithName(
								AmrpObj.Metric.PollInterval.class, pollIntervalParm)
				);
			}
			
			/** element: <amrp>.<vpn-tunnel> */
			if(childObj instanceof AmrpVpnTunnel){
				AmrpVpnTunnel vpnTunnelObj = (AmrpVpnTunnel)childObj;
				
				/** element: <amrp>.<vpn-tunnel>.<heartbeat> */
				AmrpHeartbeat heartbeatObj = new AmrpHeartbeat();
				amrpChildList_2.add(heartbeatObj);
				vpnTunnelObj.setHeartbeat(heartbeatObj);
			}
			
			/** element: <amrp>.<interface> */
			if(childObj instanceof AmrpInterface){
				AmrpInterface interfaceObj = (AmrpInterface)childObj;
				
				/** element: <amrp>.<interface>.<eth0> */
				interfaceObj.setEth0(this.createAmrpConcreteInterface(amrpImp.getPriority(InterfaceProfileInt.InterType.eth0)));
				
				/** element: <amrp>.<interface>.<eth1> */
				interfaceObj.setEth1(this.createAmrpConcreteInterface(amrpImp.getPriority(InterfaceProfileInt.InterType.eth1)));
				
				/** element: <amrp>.<interface>.<red0> */
				interfaceObj.setRed0(this.createAmrpConcreteInterface(amrpImp.getPriority(InterfaceProfileInt.InterType.red0)));
				
				/** element: <amrp>.<interface>.<agg0> */
				interfaceObj.setAgg0(this.createAmrpConcreteInterface(amrpImp.getPriority(InterfaceProfileInt.InterType.agg0)));
			}
		}
		amrpChildList_1.clear();
		generateAmrpLevel_3();
	}
	
	private void generateAmrpLevel_3() throws Exception {
		/**
		 * <amrp>.<metric>.<type>				AmrpObj.Metric.Type
		 * <amrp>.<vpn-tunnel>.<heartbeat>		AmrpHeartbeat
		 */
		for(Object childObj : amrpChildList_2){
			
			/** element: <amrp>.<metric>.<type> */
			if(childObj instanceof AmrpObj.Metric.Type){
				AmrpObj.Metric.Type typeObj = (AmrpObj.Metric.Type)childObj;
				
				/** attribute: operation */
				typeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/amrp/metric", 
						"type", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				typeObj.setValue(amrpImp.getAmrpMetricType());
			}
			
			/** element: <amrp>.<vpn-tunnel>.<heartbeat> */
			if(childObj instanceof AmrpHeartbeat){
				AmrpHeartbeat heartBeatObj = (AmrpHeartbeat)childObj;
				
				/** attribute: operation */
				heartBeatObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <amrp>.<vpn-tunnel>.<heartbeat>.<interval> */
				AmrpHeartbeatInterval intervalObj = new AmrpHeartbeatInterval();
				amrpChildList_3.add(intervalObj);
				heartBeatObj.setInterval(intervalObj);
			}
		}
		amrpChildList_2.clear();
		generateAmrpLevel_4();
	}
	
	private void generateAmrpLevel_4() throws Exception {
		/**
		 * <amrp>.<vpn-tunnel>.<heartbeat>.<interval>				AmrpHeartbeatInterval
		 */
		for(Object childObj : amrpChildList_3){
			
			/** element: <amrp>.<vpn-tunnel>.<heartbeat>.<interval> */
			if(childObj instanceof AmrpHeartbeatInterval){
				AmrpHeartbeatInterval intervalObj = (AmrpHeartbeatInterval)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/amrp/vpn-tunnel/heartbeat", 
						"interval", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				intervalObj.setValue(amrpImp.getHeartbeatInterval());
				
				/** element: <amrp>.<vpn-tunnel>.<heartbeat>.<interval>.<retry> */
				oDebug.debug("/configuration/amrp/vpn-tunnel/heartbeat/interval", 
						"retry", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				Object[][] hbRetryParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, amrpImp.getHeartbeatRetry()}
				};
				intervalObj.setRetry(
						(AmrpHeartbeatRetry)CLICommonFunc.createObjectWithName(AmrpHeartbeatRetry.class, hbRetryParm)
				);
			}
		}
		amrpChildList_3.clear();
	}
	
	private AmrpObj.Neighbor createAmrpObjNeighbor(int index)throws Exception {
		AmrpObj.Neighbor neighborObj = new AmrpObj.Neighbor();
		neighborChildList_1.add(neighborObj);
		
		generateNeighborLevel_1(index);
		return neighborObj;
	}
	
	private void generateNeighborLevel_1(int index) throws Exception {
		/**
		 * <amrp>.<neighbor>			AmrpObj.Neighbor
		 */
		for(Object childObj : neighborChildList_1){
			
			/** element: <amrp>.<neighbor> */
			if(childObj instanceof AmrpObj.Neighbor){
				AmrpObj.Neighbor neighborObj = (AmrpObj.Neighbor)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/amrp", 
						"neighbor", GenerateXMLDebug.SET_NAME,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				neighborObj.setName(amrpImp.getAmrpNeighborMac(index));
				
				/** attribute: operation */
				neighborObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <amrp>.<neighbor>.<metric> */
				AmrpNeighborMetric metricObj = new AmrpNeighborMetric();
				neighborChildList_2.add(metricObj);
				neighborObj.setMetric(metricObj);
			}
		}
		neighborChildList_1.clear();
		generateNeighborLevel_2(index);
	}
	
	private void generateNeighborLevel_2(int index){
		/**
		 * <amrp>.<neighbor>.<metric>			AmrpNeighborMetric
		 */
		for(Object childObj : neighborChildList_2){
			
			/** element <amrp>.<neighbor>.<metric> */
			if(childObj instanceof AmrpNeighborMetric){
				AmrpNeighborMetric metricObj = (AmrpNeighborMetric)childObj;
				
				/** element: <amrp>.<neighbor>.<metric>.<min> */
				AmrpNeighborMetric.Min minObj = new AmrpNeighborMetric.Min();
				neighborChildList_3.add(minObj);
				metricObj.setMin(minObj);
			}
		}
		neighborChildList_2.clear();
		generateNeighborLevel_3(index);
	}
	
	private void generateNeighborLevel_3(int index) {
		/**
		 * <amrp>.<neighbor>.<metric>.<min>			AmrpNeighborMetric.Min
		 */
		for(Object childObj : neighborChildList_3){
			
			/** element: <amrp>.<neighbor>.<metric>.<min> */
			if(childObj instanceof AmrpNeighborMetric.Min){
				AmrpNeighborMetric.Min minObj = (AmrpNeighborMetric.Min)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/amrp/neighbor[@name='"+amrpImp.getAmrpNeighborMac(index)+"']/metric", 
						"min", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				minObj.setValue(amrpImp.getAmrpNeighborMin(index));
				
				/** element: <amrp>.<neighbor>.<metric>.<min>.<max> */
				AmrpNeighborMetric.Min.Max maxObj = new AmrpNeighborMetric.Min.Max();
				neighborChildList_4.add(maxObj);
				minObj.setMax(maxObj);
			}
		}
		neighborChildList_3.clear();
		generateNeighborLevel_4(index);
	}
	
	private void generateNeighborLevel_4(int index){
		/**
		 * <amrp>.<neighbor>.<metric>.<min>.<max>			AmrpNeighborMetric.Min.Max
		 */
		for(Object childObj : neighborChildList_4){
			
			/** element: <amrp>.<neighbor>.<metric>.<min>.<max> */
			if(childObj instanceof AmrpNeighborMetric.Min.Max){
				AmrpNeighborMetric.Min.Max maxObj = (AmrpNeighborMetric.Min.Max)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/amrp/neighbor[@name='"+amrpImp.getAmrpNeighborMac(index)+"']/metric/min", 
						"max", GenerateXMLDebug.SET_VALUE,
						amrpImp.getHiveApGuiName(), amrpImp.getHiveApName());
				maxObj.setValue(amrpImp.getAmrpNeighborMax(index));
			}
		}
		neighborChildList_4.clear();
	}
	
	private AmrpConcreteInterface createAmrpConcreteInterface(int priority){
		AmrpConcreteInterface interfaceObj = new AmrpConcreteInterface();
		
		AmrpInterfacePriority priorityObj = new AmrpInterfacePriority();
		priorityObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		priorityObj.setValue(priority);
		
		interfaceObj.setPriority(priorityObj);
		
		return interfaceObj;
	}
}
