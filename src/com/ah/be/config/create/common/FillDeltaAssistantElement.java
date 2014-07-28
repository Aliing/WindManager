package com.ah.be.config.create.common;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.xml.be.config.*;

/**
 * @author zhang
 * @version 2008-1-17 19:51:27
 */

public class FillDeltaAssistantElement {
	
	private Configuration configureObj;
	
	public FillDeltaAssistantElement(Configuration configureObj){
		this.configureObj = configureObj;
		
		fillElement();
		
	}
	
	public Configuration getConfiguration(){
		return this.configureObj;
	}
	
	private void fillElement(){
		
		fillResetButtonTree();
		fillLoggingTree();
		fillSnmpTree();
		fillCapwapTree();
		fillRoamingTree();
		fillAmrpTree();
		fillClockTree();
		fillDnsTree();
		fillNtpTree();
		fillIpTree();
		fillLocationTree();
		fillAlgTree();
		fillForwardingEngineTree();
		fillMobilityThresholdTree();
		fillAdminTree();
		fillAAATree();
		fillSecurityTree();
		fillRadioTree();
		fillQosTree();
		fillInterfaceTree();
		fillUserProfileTree();
		fillSsidTree();
		fillMobilityPolicyTree();
		fillHiveTree();
		fillConsoleTree();
		fillSystemTree();
		fillCacTree();
		fillAccessConsoleTree();
		fillLldpTree();
		fillCdpTree();
		fillUserGroupTree();
		fillAutoPskUserGroupTree();
		fillVpnTree();
		fillAirscreenTree();
		fillPerformanceSentinelTree();
		fillSecurityObjectTree();
		fillReportTree();
		fillUserAttributePolicyTree();
		fillDataCollectionTree();
		fillConfigTree();
		fillNetworkFirewallTree();
		fillWebSecurityProxyTree();
		fillUsbmodemTree();
		fillRoutingTree();
		fillLanTree();
		fillTrackTree();
		fillPseTree();
		fill8021XMacTableTree();
		fillOsDetectionTree();
		fillBonjourGatewayTree();
		fillDesignatedServerTree();
		fillMacAddressTree();
		fillMonitorTree();
		fillStormControlTree();
		fillSpanningTree();
	}
	
	private void fillMacAddressTree(){
		
		/** 1 element: <configuration>.<mac-address-table> */
		if(configureObj.getMacAddressTable() == null){
			configureObj.setMacAddressTable(new MacAddressTableObj());
		}
		
		/** 2 element: <configuration>.<mac-address-table>.<learning> */
		if (configureObj.getMacAddressTable().getLearning() == null) {
			configureObj.getMacAddressTable().setLearning(new MacAddressTableLearning());
		}
		
		/** 2 element: <configuration>.<mac-address-table>.<notification> */
		if (configureObj.getMacAddressTable().getNotification() == null) {
			configureObj.getMacAddressTable().setNotification(new MacAddressTableNotification());
		}
		
		/** 3 element: <configuration>.<mac-address-table>.<learning>.<AH-DELTA-ASSISTANT> */
		if (configureObj.getMacAddressTable().getLearning().getAHDELTAASSISTANT() == null) {
			configureObj.getMacAddressTable().getLearning().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 3 element: <configuration>.<mac-address-table>.<notification>.<AH-DELTA-ASSISTANT> */
		if (configureObj.getMacAddressTable().getNotification().getAHDELTAASSISTANT() == null) {
			configureObj.getMacAddressTable().getNotification().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}

	}
	
	private void fillResetButtonTree(){
		
		/** element: <configuration>.<reset-button> */
		if(configureObj.getResetButton() == null){
			ResetButtonObj resetButtonObj = new ResetButtonObj();
			
			/** attribute: updateTime */
			resetButtonObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setResetButton(resetButtonObj);
		}
		
		/** element: <configuration>.<reset-button>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getResetButton().getAHDELTAASSISTANT() == null){
			
			/** element: <configuration>.<reset-button>.<AH-DELTA-ASSISTANT> */
			configureObj.getResetButton().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillLoggingTree(){
		
		/** element: <configuration>.<logging> */
		if(configureObj.getLogging() == null){
			LoggingObj loggingObj = new LoggingObj();
			
			/** attribute: updateTime */
			loggingObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setLogging(loggingObj);
		}
		
		/** element: <configuration>.<logging>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLogging().getAHDELTAASSISTANT() == null){
			
			/** element: <configuration>.<logging>.<AH-DELTA-ASSISTANT> */
			configureObj.getLogging().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillOsDetectionTree(){
		
		/** 1 element: <configuration>.<os-detection> */
		if(configureObj.getOsDetection() == null){
			OsDetectionObj osDetectionObj = new OsDetectionObj();
			configureObj.setOsDetection(osDetectionObj);
		}
		
		/** 2 element: <configuration>.<os-detection><method> */
		if(configureObj.getOsDetection().getMethod() == null){
			configureObj.getOsDetection().setMethod(new OsDetectionMethod());
		}
		
		/** 3 element: <configuration>.<os-detection><method>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getOsDetection().getMethod().getAHDELTAASSISTANT() == null){
			configureObj.getOsDetection().getMethod().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillBonjourGatewayTree(){
		
		/** 1 element: <configuration>.<bonjour-gateway> */
		if(configureObj.getBonjourGateway() == null){
			BonjourGatewayObj bonjourGatewayObj = new BonjourGatewayObj();
			configureObj.setBonjourGateway(bonjourGatewayObj);
		}
		
		/** 2 element: <configuration>.<bonjour-gateway><filter> */
		if(configureObj.getBonjourGateway().getFilter() == null){
			configureObj.getBonjourGateway().setFilter(new BonjourGatewayFilter());
		}
		
		/** 3 element: <configuration>.<bonjour-gateway><filter>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getBonjourGateway().getFilter().getAHDELTAASSISTANT() == null){
			configureObj.getBonjourGateway().getFilter().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillDesignatedServerTree(){
		
		/** 1 element: <configuration>.<designated-server> */
		if(configureObj.getDesignatedServer() == null){
			configureObj.setDesignatedServer(new DesignatedServerObj());
		}
		
		/** 2 element: <configuration>.<designated-server>.<idm-proxy> */
		if(configureObj.getDesignatedServer().getIdmProxy() == null){
			configureObj.getDesignatedServer().setIdmProxy(new DesignatedServerIdmProxy());
		}
		
		/** 3 element: <configuration>.<designated-server>.<idm-proxy>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getDesignatedServer().getIdmProxy().getAHDELTAASSISTANT() == null){
			configureObj.getDesignatedServer().getIdmProxy().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillSnmpTree(){
		
		/** 1  element: <configuration>.<snmp> */
		if(configureObj.getSnmp() == null){
			SnmpObj snmpObj = new SnmpObj();
			
			/** attribute: updateTime */
			snmpObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setSnmp(snmpObj);
		}
		
		/** 2 element: <configuration>.<snmp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 2 element: <configuration>.<snmp>.<reader> */
		if(configureObj.getSnmp().getReader() == null){		
			configureObj.getSnmp().setReader(new SnmpObj.Reader());
		}
		
		/** 2 element: <configuration>.<snmp>.<trap-host> */
		if(configureObj.getSnmp().getTrapHost() == null){
			configureObj.getSnmp().setTrapHost(new SnmpObj.TrapHost());
		}
		
		/** 2 element: <configuration>.<snmp>.<trap-info> */
		if(configureObj.getSnmp().getTrapInfo() == null){
			configureObj.getSnmp().setTrapInfo(new SnmpObj.TrapInfo());
		}
		
		/** 3 element: <configuration>.<snmp>.<trap-host>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getTrapHost().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getTrapHost().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<snmp>.<trap-host>.<v3> */
		if(configureObj.getSnmp().getTrapHost().getV3() == null){
			configureObj.getSnmp().getTrapHost().setV3(new SnmpTrapHostV3());
		}
		
		/** 3 element: <configuration>.<snmp>.<trap-info>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getTrapInfo().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getTrapInfo().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<snmp>.<reader>.<version> */
		if(configureObj.getSnmp().getReader().getVersion() == null){		
			configureObj.getSnmp().getReader().setVersion(new SnmpObj.Reader.Version());
		}
		
		/** 4 element: <configuration>.<snmp>.<reader>.<version>.<v1> */
		if(configureObj.getSnmp().getReader().getVersion().getV1() == null){
			configureObj.getSnmp().getReader().getVersion().setV1(new SnmpReaderCommunity());
		}
		
		/** 4 element: <configuration>.<snmp>.<reader>.<version>.<v2> */
		if(configureObj.getSnmp().getReader().getVersion().getV2C() == null){
			configureObj.getSnmp().getReader().getVersion().setV2C(new SnmpReaderCommunity());
		}
		
		/** 4 element: <configuration>.<snmp>.<reader>.<version>.<v3> */
		if(configureObj.getSnmp().getReader().getVersion().getV3() == null){
			configureObj.getSnmp().getReader().getVersion().setV3(new SnmpReaderV3());
		}
		
		/** 4 element: <configuration>.<snmp>.<reader>.<version>.<any> */
		if(configureObj.getSnmp().getReader().getVersion().getAny() == null){
			configureObj.getSnmp().getReader().getVersion().setAny(new SnmpReaderCommunity());
		}
		
		/** 4 element: <configuration>.<snmp>.<trap-host>.<v3>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getTrapHost().getV3().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getTrapHost().getV3().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<snmp>.<reader>.<version>.<v2>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getReader().getVersion().getV2C().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getReader().getVersion().getV2C().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<snmp>.<reader>.<version>.<v1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getReader().getVersion().getV1().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getReader().getVersion().getV1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<snmp>.<reader>.<version>.<v3>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getReader().getVersion().getV3().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getReader().getVersion().getV3().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<snmp>.<reader>.<version>.<any>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSnmp().getReader().getVersion().getAny().getAHDELTAASSISTANT() == null){
			configureObj.getSnmp().getReader().getVersion().getAny().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
	}
	
	private void fillCapwapTree(){
		
		/** element: <configuration>.<capwap> */
		if(configureObj.getCapwap() == null){
			CapwapObj capwapObj = new CapwapObj();
			
			/** attribute: updateTime */
			capwapObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setCapwap(capwapObj);
		}
		
		/** 3 element: <configuration>.<capwap>.<client> */
		if(configureObj.getCapwap().getClient() == null){
			configureObj.getCapwap().setClient(new CapwapObj.Client());
		}
		
		/** 4 element: <configuration>.<capwap>.<client>.<server> */
		if(configureObj.getCapwap().getClient().getServer() == null){
			configureObj.getCapwap().getClient().setServer(new CapwapObj.Client.Server());
		}
		
		/** 4 element: <configuration>.<capwap>.<client>.<dtls> */
		if(configureObj.getCapwap().getClient().getDtls() == null){
			configureObj.getCapwap().getClient().setDtls(new CapwapObj.Client.Dtls());
		}
		
		/** 4 element: <configuration>.<capwap>.<client>.<neighbor> */
		if(configureObj.getCapwap().getClient().getNeighbor() == null){
			configureObj.getCapwap().getClient().setNeighbor(new CapwapObj.Client.Neighbor());
		}
		
		/** 4 element: <configuration>.<capwap>.<client>.<pci-alert> */
		if(configureObj.getCapwap().getClient().getPciAlert() == null){
			configureObj.getCapwap().getClient().setPciAlert(new AhEnable());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getServer().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<server>.<primary> */
		if(configureObj.getCapwap().getClient().getServer().getPrimary() == null){
			configureObj.getCapwap().getClient().getServer().setPrimary(new CapwapServer());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<server>.<backup> */
		if(configureObj.getCapwap().getClient().getServer().getBackup() == null){
			configureObj.getCapwap().getClient().getServer().setBackup(new CapwapServer());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<server>.<cr-primary> */
		if(configureObj.getCapwap().getClient().getServer().getCrPrimary() == null){
			configureObj.getCapwap().getClient().getServer().setCrPrimary(new CapwapServer());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<server>.<cr-backup> */
		if(configureObj.getCapwap().getClient().getServer().getCrBackup() == null){
			configureObj.getCapwap().getClient().getServer().setCrBackup(new CapwapServer());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<dtls>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getDtls().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getDtls().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<neighbor>.<dead> */
		if(configureObj.getCapwap().getClient().getNeighbor().getDead() == null){
			configureObj.getCapwap().getClient().getNeighbor().setDead(new CapwapObj.Client.Neighbor.Dead());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<neighbor>.<heartbeat> */
		if(configureObj.getCapwap().getClient().getNeighbor().getHeartbeat() == null){
			configureObj.getCapwap().getClient().getNeighbor().setHeartbeat(new CapwapObj.Client.Neighbor.Heartbeat());
		}
		
		/** 5 element: <configuration>.<capwap>.<client>.<pci-alert>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getPciAlert().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getPciAlert().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<neighbor>.<dead>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getNeighbor().getDead().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getNeighbor().getDead().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<neighbor>.<heartbeat>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getNeighbor().getHeartbeat().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getNeighbor().getHeartbeat().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<server>.<primary>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getServer().getPrimary().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getServer().getPrimary().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<server>.<backup>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getServer().getBackup().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getServer().getBackup().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<server>.<cr-primary>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getServer().getCrPrimary().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getServer().getCrPrimary().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<capwap>.<client>.<server>.<cr-backup>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCapwap().getClient().getServer().getCrBackup().getAHDELTAASSISTANT() == null){
			configureObj.getCapwap().getClient().getServer().getCrBackup().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
	}
	
	private void fillRoamingTree(){
		
		/** 2 element: <configuration>.<roaming> */
		if(configureObj.getRoaming() == null){
			RoamingObj roamingObj = new RoamingObj();
			
			/** attribute: updateTime */
			roamingObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setRoaming(roamingObj);
		}
		
		/** 3 element: <configuration>.<roaming>.<cache> */
		if(configureObj.getRoaming().getCache() == null){
			configureObj.getRoaming().setCache(new RoamingObj.Cache());
		}
		
		/** 3 element: <configuration>.<roaming>.<neighbor> */
		if(configureObj.getRoaming().getNeighbor() == null ){
			configureObj.getRoaming().setNeighbor(new RoamingObj.Neighbor());
		}
		
		/** 3 element: <configuration>.<roaming>.<cache-broadcast> */
		if(configureObj.getRoaming().getCacheBroadcast() == null){
			configureObj.getRoaming().setCacheBroadcast(new RoamingObj.CacheBroadcast());
		}
		
		/** 4 element: <configuration>.<roaming>.<cache>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRoaming().getCache().getAHDELTAASSISTANT() == null){
			configureObj.getRoaming().getCache().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<roaming>.<neighbor>.<include> */
		if(configureObj.getRoaming().getNeighbor().getInclude() == null){
			configureObj.getRoaming().getNeighbor().setInclude(new RoamingObj.Neighbor.Include());
		}
		
		/** 4 element: <configuration>.<roaming>.<neighbor>.<exclude> */
		if(configureObj.getRoaming().getNeighbor().getExclude() == null){
			configureObj.getRoaming().getNeighbor().setExclude(new RoamingObj.Neighbor.Exclude());
		}
		
		/** 4 element: <configuration>.<roaming>.<cache-broadcast>.<neighbor-type> */
		if(configureObj.getRoaming().getCacheBroadcast().getNeighborType() == null){
			configureObj.getRoaming().getCacheBroadcast().setNeighborType(new CacheBroadcastNeighborType());
		}
		
		/** 5 element: <configuration>.<roaming>.<neighbor>.<include>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRoaming().getNeighbor().getInclude().getAHDELTAASSISTANT() == null){
			configureObj.getRoaming().getNeighbor().getInclude().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<roaming>.<neighbor>.<exclude>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRoaming().getNeighbor().getExclude().getAHDELTAASSISTANT() == null){
			configureObj.getRoaming().getNeighbor().getExclude().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<roaming>.<cache-broadcast>.<neighbor-type>.<access> */
		if(configureObj.getRoaming().getCacheBroadcast().getNeighborType().getAccess() == null){
			configureObj.getRoaming().getCacheBroadcast().getNeighborType().setAccess(new AhEnable());
		}
		
		/** 5 element: <configuration>.<roaming>.<cache-broadcast>.<neighbor-type>.<backhaul> */
		if(configureObj.getRoaming().getCacheBroadcast().getNeighborType().getBackhaul() == null){
			configureObj.getRoaming().getCacheBroadcast().getNeighborType().setBackhaul(new AhEnable());
		}
		
		/** 6 element: <configuration>.<roaming>.<cache-broadcast>.<neighbor-type>.<access>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRoaming().getCacheBroadcast().getNeighborType().getAccess().getAHDELTAASSISTANT() == null){
			configureObj.getRoaming().getCacheBroadcast().getNeighborType().getAccess().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<roaming>.<cache-broadcast>.<neighbor-type>.<backhaul>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRoaming().getCacheBroadcast().getNeighborType().getBackhaul().getAHDELTAASSISTANT() == null){
			configureObj.getRoaming().getCacheBroadcast().getNeighborType().getBackhaul().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillAmrpTree(){
		
		/** 2 element: <configuration>.<amrp> */
		if(configureObj.getAmrp() == null){
			AmrpObj amrpObj = new AmrpObj();
			
			/** attribute: updateTime */
			amrpObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setAmrp(amrpObj);
		}
		
		/** 3 element: <configuration>.<amrp>.<metric> */
		if(configureObj.getAmrp().getMetric() == null){
			configureObj.getAmrp().setMetric(new AmrpObj.Metric());
		}
		
		/** 3 element: <configuration>.<amrp>.<vpn-tunnel> */
		if(configureObj.getAmrp().getVpnTunnel() == null){
			configureObj.getAmrp().setVpnTunnel(new AmrpVpnTunnel());
		}
		
		/** 4 element: <configuration>.<amrp>.<metric>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAmrp().getMetric().getAHDELTAASSISTANT() == null){
			configureObj.getAmrp().getMetric().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<amrp>.<vpn-tunnel>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAmrp().getVpnTunnel().getAHDELTAASSISTANT() == null){
			configureObj.getAmrp().getVpnTunnel().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillClockTree(){
		
		/** 2 element: <configuration>.<clock> */
		if(configureObj.getClock() == null){
			ClockObj clockObj = new ClockObj();
			
			/** attribute: updateTime */
			clockObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setClock(clockObj);
		}
		
		/** 3 element: <configuration>.<clock>.<time-zone> */
		if(configureObj.getClock().getTimeZone() == null){
			configureObj.getClock().setTimeZone(new ClockObj.TimeZone());
		}
		
		/** 4 element: <configuration>.<clock>.<time-zone>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getClock().getTimeZone().getAHDELTAASSISTANT() == null){
			configureObj.getClock().getTimeZone().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillDnsTree(){
		
		/** 2 element: <configuration>.<dns> */
		if(configureObj.getDns() == null){
			DnsObj dnsObj = new DnsObj();
			
			/** attribute: updateTime */
			dnsObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setDns(dnsObj);
		}
		
		/** 3 element: <configuration>.<dns>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getDns().getAHDELTAASSISTANT() == null){
			configureObj.getDns().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillNtpTree(){
		
		/** 2 element: <configuration>.<ntp> */
		if(configureObj.getNtp() == null){
			NtpObj ntpObj = new NtpObj();
			
			/** attribute: updateTime */
			ntpObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setNtp(ntpObj);
		}
		
		/** 3 element: <configuration>.<ntp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getNtp().getAHDELTAASSISTANT() == null){
			configureObj.getNtp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillIpTree(){
		
		{/** Level_2	####################################################*/
			
			/** 2 element: <configuration>.<ip> */
			if(configureObj.getIp() == null){
				IpObj ipObj = new IpObj();
				
				/** attribute: updateTime */
				ipObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
				
				configureObj.setIp(ipObj);
			}
		}
		
		{/** Level_3	####################################################*/
			
			/** 3 element: <configuration>.<ip>.<route> */
			if(configureObj.getIp().getRoute() == null){
				configureObj.getIp().setRoute(new IpObj.Route());
			}
			
			/** 3 element: <configuration>.<ip>.<path-mtu-discovery> */
			if(configureObj.getIp().getPathMtuDiscovery() == null){
				configureObj.getIp().setPathMtuDiscovery(new IpPathMtuDiscovery());
			}
			
			/** 3 element: <configuration>.<ip>.<tcp-mss-threshold> */
			if(configureObj.getIp().getTcpMssThreshold() == null){
				configureObj.getIp().setTcpMssThreshold(new IpTcpMssThreshold());
			}
			
			/** 3 element: <configuration>.<ip>.<igmp> */
			if(configureObj.getIp().getIgmp() == null){
				configureObj.getIp().setIgmp(new IpIgmp());
			}
		}
		
		{/** Level_4	#################################################### */
			
			/** 4 element: <configuration>.<ip>.<route>.<AH-DELTA-ASSISTANT> */
			if(configureObj.getIp().getRoute().getAHDELTAASSISTANT() == null){
				configureObj.getIp().getRoute().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
			
			/** 4 element: <configuration>.<ip>.<path-mtu-discovery>.<AH-DELTA-ASSISTANT> */
			if(configureObj.getIp().getPathMtuDiscovery().getAHDELTAASSISTANT() == null){
				configureObj.getIp().getPathMtuDiscovery().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
			
			/** 4 element: <configuration>.<ip>.<tcp-mss-threshold>.<AH-DELTA-ASSISTANT> */
			if(configureObj.getIp().getTcpMssThreshold().getAHDELTAASSISTANT() == null){
				configureObj.getIp().getTcpMssThreshold().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
			
			/** 4 element: <configuration>.<ip>.<igmp>.<snooping> */
			if(configureObj.getIp().getIgmp().getSnooping() == null){
				configureObj.getIp().getIgmp().setSnooping(new IpIgmpSnooping());
			}
		}
		
		{/** Level_5	####################################################*/
			
			/** 5 element: <configuration>.<ip>.<igmp>.<snooping> */
			if(configureObj.getIp().getIgmp().getSnooping().getAHDELTAASSISTANT() == null){
				configureObj.getIp().getIgmp().getSnooping().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
				);
			}
		}
	}
	
	private void fillLocationTree(){
		
		/** 2 element: <configuration>.<location> */
		if(configureObj.getLocation() == null){
			LocationObj locationObj = new LocationObj();
			
			/** attribute: updateTime */
			locationObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setLocation(locationObj);
		}
		
		/** 3 element: <configuration>.<location>.<aeroscout> */
		if(configureObj.getLocation().getAeroscout() == null){
			configureObj.getLocation().setAeroscout(new LocationObj.Aeroscout());
		}
		
		/** 3 element: <configuration>.<location>.<rate-threshold> */
		if(configureObj.getLocation().getRateThreshold() == null){
			configureObj.getLocation().setRateThreshold(new LocationObj.RateThreshold());
		}
		
		/** 3 element: <configuration>.<location>.<aerohive> */
		if(configureObj.getLocation().getAerohive() == null){
			configureObj.getLocation().setAerohive(
					new LocationAerohive()
			);
		}
		
		/** 3 element: <configuration>.<location>.<ekahau> */
		if(configureObj.getLocation().getEkahau() == null){
			configureObj.getLocation().setEkahau(new LocationEkahau());
		}
		
		/** 3 element: <configuration>.<location>.<tzsp> */
		if(configureObj.getLocation().getTzsp() == null){
			configureObj.getLocation().setTzsp(new LocationEkahau());
		}
		
		/** 4 element: <configuration>.<location>.<aeroscout>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLocation().getAeroscout().getAHDELTAASSISTANT() == null){
			configureObj.getLocation().getAeroscout().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<location>.<rate-threshold>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLocation().getRateThreshold().getAHDELTAASSISTANT() == null){
			configureObj.getLocation().getRateThreshold().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<location>.<aerohive>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLocation().getAerohive().getAHDELTAASSISTANT() == null){
			configureObj.getLocation().getAerohive().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<location>.<ekahau>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLocation().getEkahau().getAHDELTAASSISTANT() == null){
			configureObj.getLocation().getEkahau().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<location>.<tzsp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLocation().getTzsp().getAHDELTAASSISTANT() == null){
			configureObj.getLocation().getTzsp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillAlgTree(){
		
		/** 2 element: <configuration>.<alg> */
		if(configureObj.getAlg() == null){
			AlgObj algObj = new AlgObj();
			
			algObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setAlg(algObj);
		}
		
		/** 3 element: <configuration>.<alg>.<ftp> */
		if(configureObj.getAlg().getFtp() == null){
			configureObj.getAlg().setFtp(new AlgFtp());
		}
		
		/** 3 element: <configuration>.<alg>.<tftp> */
		if(configureObj.getAlg().getTftp() == null){
			configureObj.getAlg().setTftp(new AlgTftp());
		}
		
		/** 3 element: <configuration>.<alg>.<sip> */
		if(configureObj.getAlg().getSip() == null){
			configureObj.getAlg().setSip(new AlgSip());
		}
		

		/** 3 element: <configuration>.<alg>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAlg().getAHDELTAASSISTANT() == null){
			configureObj.getAlg().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 3 element: <configuration>.<alg>.<dns> */
//		if(configureObj.getAlg().getDns() == null){
//			configureObj.getAlg().setDns(new AlgDns());
//		}
		
		/** 4 element: <configuration>.<alg>.<ftp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAlg().getFtp().getAHDELTAASSISTANT() == null){
			configureObj.getAlg().getFtp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<alg>.<tftp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAlg().getTftp().getAHDELTAASSISTANT() == null){
			configureObj.getAlg().getTftp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<alg>.<sip>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAlg().getSip().getAHDELTAASSISTANT() == null){
			configureObj.getAlg().getSip().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 4 element: <configuration>.<alg>.<dns>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getAlg().getDns().getAHDELTAASSISTANT() == null){
//			configureObj.getAlg().getDns().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
	}
	
	private void fillPseTree(){
		
		/** 2 element: <configuration>.<pse> */
		if(configureObj.getPse() == null){
			PseObj pseObj = new PseObj();			
			configureObj.setPse(pseObj);
		}
		
		/** 3 element: <configuration>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getPse().getAHDELTAASSISTANT() == null){
			configureObj.getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
	}
	
	private void fill8021XMacTableTree(){
		
		/** 2 element: <configuration>.<_802.1x> */
		if(configureObj.get8021XMacTable() == null){
			Ethx802Dot1XMacTableObj macTableObj = new Ethx802Dot1XMacTableObj();			
			configureObj.set8021XMacTable(macTableObj);
		}
		
		/** 3 element: <configuration>.<_802.1x>.<AH-DELTA-ASSISTANT> */
		if(configureObj.get8021XMacTable().getAHDELTAASSISTANT() == null){
			configureObj.get8021XMacTable().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillForwardingEngineTree(){
		
		/** 2 element: <configuration>.<forwarding-engine> */
		if(configureObj.getForwardingEngine() == null){
			ForwardingEngineObj forwardingObj = new ForwardingEngineObj();
			
			/** attribute: updateTime */
			forwardingObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setForwardingEngine(forwardingObj);
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getAHDELTAASSISTANT()== null){
			configureObj.getForwardingEngine().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<inter-ssid-flood> */
		if(configureObj.getForwardingEngine().getInterSsidFlood() == null){
			configureObj.getForwardingEngine().setInterSsidFlood(new AhEnable());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<proxy-arp> */
		if(configureObj.getForwardingEngine().getProxyArp() == null){
			configureObj.getForwardingEngine().setProxyArp(new AhEnable());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<log> */
		if(configureObj.getForwardingEngine().getLog() == null){
			configureObj.getForwardingEngine().setLog(new ForwardingEngineObj.Log());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<drop> */
		if(configureObj.getForwardingEngine().getDrop() == null){
			configureObj.getForwardingEngine().setDrop(new ForwardingEngineObj.Drop());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<tunnel> */
		if(configureObj.getForwardingEngine().getTunnel() == null){
			configureObj.getForwardingEngine().setTunnel(new ForwardingEngineTunnel());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<l2-default-route> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute() == null){
			configureObj.getForwardingEngine().setL2DefaultRoute(new FeL2DefaultRoute());
		}
		
		/** 3 element: <configuration>.<forwarding-engine>.<mac-sessions> */
		if(configureObj.getForwardingEngine().getMacSessions() == null){
			configureObj.getForwardingEngine().setMacSessions(new FeMacSessions());
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<inter-ssid-flood>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getInterSsidFlood().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getInterSsidFlood().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<proxy-arp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getProxyArp().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getProxyArp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<log>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getLog().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getLog().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<drop>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getDrop().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getDrop().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<tunnel>.<tcp-mss-threshold> */
		if(configureObj.getForwardingEngine().getTunnel().getTcpMssThreshold() == null){
			configureObj.getForwardingEngine().getTunnel().setTcpMssThreshold(new ForwardingEngineTunnel.TcpMssThreshold());
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<tunnel>.<selective-multicast-forward> */
		if(configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward() == null){
			configureObj.getForwardingEngine().getTunnel().setSelectiveMulticastForward(new FeTunnelSelectiveMulticastForward());
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<l2-default-route>.<interface> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute().getInterface() == null){
			configureObj.getForwardingEngine().getL2DefaultRoute().setInterface(new FeL2DefaultRouteInterface());
		}
		
		/** 4 element: <configuration>.<forwarding-engine>.<mac-sessions>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getMacSessions().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getMacSessions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<forwarding-engine>.<tunnel>.<tcp-mss-threshold>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getTunnel().getTcpMssThreshold().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getTunnel().getTcpMssThreshold().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<forwarding-engine>.<l2-default-route>.<interface>.<eth0> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth0() == null){
			configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().setEth0(new FeL2DefaultRouteEthx());
		}
		
		/** 5 element: <configuration>.<forwarding-engine>.<l2-default-route>.<interface>.<eth1> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth1() == null){
			configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().setEth1(new FeL2DefaultRouteEthx());
		}
		
		/** 5 element: <configuration>.<forwarding-engine>.<tunnel>.<selective-multicast-forward>.<allow-all> */
		if(configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getAllowAll() == null){
			configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().setAllowAll(new FeTunnelMulticastForwardAllow());
		}
		
		/** 5 element: <configuration>.<forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all> */
		if(configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getBlockAll() == null){
			configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().setBlockAll(new FeTunnelMulticastForwardBlock());
		}
		
		/** 6 element: <configuration>.<forwarding-engine>.<l2-default-route>.<interface>.<eth0>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth0().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth0().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<forwarding-engine>.<l2-default-route>.<interface>.<eth1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth1().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getL2DefaultRoute().getInterface().getEth1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<forwarding-engine>.<tunnel>.<selective-multicast-forward>.<allow-all>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getAllowAll().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getAllowAll().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all> */
		if(configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getBlockAll().getAHDELTAASSISTANT() == null){
			configureObj.getForwardingEngine().getTunnel().getSelectiveMulticastForward().getBlockAll().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillMobilityThresholdTree(){
		
		/** 2 element: <configuration>.<mobility-threshold> */
		if(configureObj.getMobilityThreshold() == null){
			MobilityThresholdObj mobilityObj = new MobilityThresholdObj();
			
			/** attribute: updateTime */
			mobilityObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setMobilityThreshold(mobilityObj);
		}
		
		/** 3 element: <configuration>.<mobility-threshold>.<gre-tunnel> */
		if(configureObj.getMobilityThreshold().getGreTunnel() == null){
			configureObj.getMobilityThreshold().setGreTunnel(new MobilityThresholdObj.GreTunnel());
		}
		
		/** 4 element: <configuration>.<mobility-threshold>.<gre-tunnel>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getMobilityThreshold().getGreTunnel().getAHDELTAASSISTANT() == null){
			configureObj.getMobilityThreshold().getGreTunnel().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillAdminTree(){
		
		/** 2 element: <configuration>.<admin> */
		if(configureObj.getAdmin() == null){
			AdminObj adminObj = new AdminObj();
			
			/** attribute: updatTime */
			adminObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setAdmin(adminObj);
		}
		
		/** 3 element: <configuration>.<admin>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAdmin().getAHDELTAASSISTANT() == null){
			configureObj.getAdmin().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<admin>.<auth> */
		if(configureObj.getAdmin().getAuth() == null){
			configureObj.getAdmin().setAuth(new AdminObj.Auth());
		}
		
		/** 4 element: <configuration>.<admin>.<auth>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAdmin().getAuth().getAHDELTAASSISTANT() == null){
			configureObj.getAdmin().getAuth().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillAAATree(){
		
		/** 2 element: <configuration>.<aaa> */
		if(configureObj.getAaa() == null){
			AaaObj aaaObj = new AaaObj();
			
			/** attribute: updateTime */
			aaaObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setAaa(aaaObj);
		}
		
		/** 3 element: <configuration>.<aaa>.<radius-server> */
		if(configureObj.getAaa().getRadiusServer() == null){
			configureObj.getAaa().setRadiusServer(new AaaObj.RadiusServer());
		}
		
		/** 3 element: <configuration>.<aaa>.<mac-format> */
		if(configureObj.getAaa().getMacFormat() == null){
			configureObj.getAaa().setMacFormat(new AaaObj.MacFormat());
		}
		
		/** 3 element: <configuration>.<aaa>.<ppsk-server> */
		if(configureObj.getAaa().getPpskServer() == null){
			configureObj.getAaa().setPpskServer(new AaaPpskServer());
		}
		
		/** 3 element: <configuration>.<aaa>.<attribute> */
		if(configureObj.getAaa().getAttribute() == null){
			configureObj.getAaa().setAttribute(new AaaAttribute());
		}
		
		/** 4 element: <configuration>.<aaa>.<radius-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 4 element: <configuration>.<aaa>.<radius-server>.<local> */
		if(configureObj.getAaa().getRadiusServer().getLocal() == null){
			configureObj.getAaa().getRadiusServer().setLocal(new AaaObj.RadiusServer.Local());
		}
		
		/** 4 element: <configuration>.<aaa>.<mac-format>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getMacFormat().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getMacFormat().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<aaa>.<radius-server>.<accounting> */
		if(configureObj.getAaa().getRadiusServer().getAccounting() == null){
			configureObj.getAaa().getRadiusServer().setAccounting(new RadiusAccountingAll());
		}
		
		/** 4 element: <configuration>.<aaa>.<radius-server>.<proxy> */
		if(configureObj.getAaa().getRadiusServer().getProxy() == null){
			configureObj.getAaa().getRadiusServer().setProxy(new RadiusProxy());
		}
		
		/** 4 element: <configuration>.<aaa>.<radius-server>.<keepalive> */
		if(configureObj.getAaa().getRadiusServer().getKeepalive() == null){
			configureObj.getAaa().getRadiusServer().setKeepalive(new RadiusKeepalive());
		}
		
		/** 4 element: <configuration>.<aaa>.<ppsk-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getPpskServer().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getPpskServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<aaa>.<ppsk-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getPpskServer().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getPpskServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<aaa>.<ppsk-server>.<radius-server> */
		if(configureObj.getAaa().getPpskServer().getRadiusServer() == null){
			configureObj.getAaa().getPpskServer().setRadiusServer(new PpskRadiusServer());
		}
		
		/** 4 element: <configuration>.<aaa>.<attribute>.<operator-name> */
		if(configureObj.getAaa().getAttribute().getOperatorName() == null){
			configureObj.getAaa().getAttribute().setOperatorName(new AaaAttrOperatorName());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<attr-map> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getAttrMap() == null){
			configureObj.getAaa().getRadiusServer().getLocal().setAttrMap(new AaaObj.RadiusServer.Local.AttrMap());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType() == null){
			configureObj.getAaa().getRadiusServer().getLocal().setDbType(new AaaObj.RadiusServer.Local.DbType());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<LDAP-auth> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getLDAPAuth() == null){
			configureObj.getAaa().getRadiusServer().getLocal().setLDAPAuth(new AaaObj.RadiusServer.Local.LDAPAuth());
		}
		
//		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth() == null){
//			AaaObj.RadiusServer.Local.STAAuth staObj = new AaaObj.RadiusServer.Local.STAAuth();
//			staObj.setOperation(CLICommonFunc.getAhEnumAct(true));
//			configureObj.getAaa().getRadiusServer().getLocal().setSTAAuth(staObj);
//		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<accounting>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getAccounting().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getAccounting().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<proxy>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getProxy().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<proxy>.<realm> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getRealm() == null){
			configureObj.getAaa().getRadiusServer().getProxy().setRealm(new RadiusProxyRealmAll());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<proxy>.<inject> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getInject() == null){
			configureObj.getAaa().getRadiusServer().getProxy().setInject(new RadiusProxyInject());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<proxy>.<radsec> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getRadsec() == null){
			configureObj.getAaa().getRadiusServer().getProxy().setRadsec(new RadiusProxyRadsec());
		}
		
		/** 5 element: <configuration>.<aaa>.<radius-server>.<keepalive>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getKeepalive().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getKeepalive().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<aaa>.<ppsk-server>.<radius-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getPpskServer().getRadiusServer().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getPpskServer().getRadiusServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<aaa>.<attribute>.<operator-name>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getAttribute().getOperatorName().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getAttribute().getOperatorName().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		
//		/** 5 element: <configuration>.<aaa>.<radius-server>.<local>.<user-group> */
//		for(AaaObj.RadiusServer.Local.UserGroup group : configureObj.getAaa().getRadiusServer().getLocal().getUserGroup()){
//			
//			/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<user-group>.<AH-DELTA-ASSISTANT> */
//			if(group.getAHDELTAASSISTANT() == null){
//				group.setAHDELTAASSISTANT(
//						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//				);
//			}
//		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<attr-map>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getAttrMap().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getAttrMap().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<active-directory> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getActiveDirectory() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().setActiveDirectory(
					new AaaObj.RadiusServer.Local.DbType.ActiveDirectory()
			);
		}
		
//		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<open-ldap> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenLdap() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getDbType().setOpenLdap(
//					new AaaObj.RadiusServer.Local.DbType.OpenLdap()
//			);
//		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<ldap-server> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLdapServer() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().setLdapServer(
					new AaaObj.RadiusServer.Local.DbType.LdapServer()
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<open-directory> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenDirectory() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().setOpenDirectory(
					new AaaObj.RadiusServer.Local.DbType.OpenDirectory()
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<library-sip-server> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLibrarySipServer() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().setLibrarySipServer(
					new AaaObj.RadiusServer.Local.DbType.LibrarySipServer()
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<LDAP-auth>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getLDAPAuth().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getLDAPAuth().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 6 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType() == null){
//			AaaObj.RadiusServer.Local.STAAuth.Type typeObj = new AaaObj.RadiusServer.Local.STAAuth.Type();
//			typeObj.setOperation(CLICommonFunc.getAhEnumAct(true));
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().setType(typeObj);
//		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<proxy>.<realm>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getRealm().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getProxy().getRealm().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<proxy>.<inject>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getInject().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getProxy().getInject().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<aaa>.<radius-server>.<proxy>.<radsec>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getProxy().getRadsec().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getProxy().getRadsec().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getActiveDirectory().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getActiveDirectory().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenLdap().getAHDELTAASSISTANT() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenLdap().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLdapServer().getSubType() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLdapServer().setSubType(
					new AaaObj.RadiusServer.Local.DbType.LdapServer.SubType()
			);
		}
		
		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenDirectory().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getOpenDirectory().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLibrarySipServer().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLibrarySipServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().setCr(new STAAuthPositiveType());
//		}
//		
//		/** 7 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getAHDELTAASSISTANT() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 8 element: <configuration>.<aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLdapServer().getSubType().getAHDELTAASSISTANT() == null){
			configureObj.getAaa().getRadiusServer().getLocal().getDbType().getLdapServer().getSubType().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
//		/** 8 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<peap> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getPeap() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().setPeap(new STAAuthChecks());
//		}
//		
//		/** 8 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<ttls> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getTtls() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().setTtls(new STAAuthChecks());
//		}
//		
//		/** 9 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<peap>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getPeap().getAHDELTAASSISTANT() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getPeap().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
//		
//		/** 9 element: <configuration>.<aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<ttls>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getTtls().getAHDELTAASSISTANT() == null){
//			configureObj.getAaa().getRadiusServer().getLocal().getSTAAuth().getType().getCr().getTtls().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
	}
	
	private void fillSecurityTree(){
		
		/** 2 element: <configuration>.<security> */
		if(configureObj.getSecurity() == null){
			SecurityObj securityObj = new SecurityObj();
			
			/** attribute: updateTime */
			securityObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setSecurity(securityObj);
		}
		
		/** 3 element: <configuration>.<security>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSecurity().getAHDELTAASSISTANT() == null){
			configureObj.getSecurity().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<security>.<wlan-idp> */
		if(configureObj.getSecurity().getWlanIdp() == null){
			configureObj.getSecurity().setWlanIdp(new SecurityObj.WlanIdp());
		}
		
		/** 4 element: <configuration>.<security>.<wlan-idp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSecurity().getWlanIdp().getAHDELTAASSISTANT() == null){
			configureObj.getSecurity().getWlanIdp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<security>.<wlan-idp>.<profile> */
		if(configureObj.getSecurity().getWlanIdp().getProfile() != null){
			for(SecurityObj.WlanIdp.Profile profile : configureObj.getSecurity().getWlanIdp().getProfile()){
				
				/** 5 element: <configuration>.<security>.<wlan-idp>.<profile>.<ap-detection> */
				if(profile.getApDetection() == null){
					profile.setApDetection(new SecurityObj.WlanIdp.Profile.ApDetection());
				}
				
				/** 5 element: <configuration>.<security>.<wlan-idp>.<profile>.<mitigate> */
				if(profile.getMitigate() == null){
					profile.setMitigate(new WlanIdpMitigate());
				}
				
				/** 5 element: <configuration>.<security>.<wlan-idp>.<profile>.<sta-report> */
				if(profile.getStaReport() == null){
					profile.setStaReport(new WlanIdpStaReport());
				}
				
				/** 6 element: <configuration>.<security>.<wlan-idp>.<profile>.<ap-detection>.<connected> */
				if(profile.getApDetection().getConnected() == null){
					profile.getApDetection().setConnected(new SecurityObj.WlanIdp.Profile.ApDetection.Connected());
				}
				
				/** 6 element: <configuration>.<security>.<wlan-idp>.<profile>.<mitigate>.<AH-DELTA-ASSISTANT> */
				if(profile.getMitigate().getAHDELTAASSISTANT() == null){
					profile.getMitigate().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<security>.<wlan-idp>.<profile>.<sta-report>.<AH-DELTA-ASSISTANT> */
				if(profile.getStaReport().getAHDELTAASSISTANT() == null){
					profile.getStaReport().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<security>.<wlan-idp>.<profile>.<ap-detection>.<connected>.<AH-DELTA-ASSISTANT> */
				if(profile.getApDetection().getConnected().getAHDELTAASSISTANT() == null){
					profile.getApDetection().getConnected().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
			}
		}
	}
	
	private void fillRadioTree(){
		
		/** 2 element: <configuration>.<radio> */
		if(configureObj.getRadio() == null){
			RadioObj radioObj = new RadioObj();
			
			/** attribute: updateTime */
			radioObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setRadio(radioObj);
		}
		
		/** 3 element: <configuration>.<radio>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRadio().getAHDELTAASSISTANT() == null){
			configureObj.getRadio().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<radio>.<profile> */
		if(configureObj.getRadio().getProfile() != null){
			
			for(RadioObj.Profile profileObj : configureObj.getRadio().getProfile()){
				
				/** 4 element: <configuration>.<radio>.<profile>.<acsp> */
				if(profileObj.getAcsp() == null){
					profileObj.setAcsp(new RadioObj.Profile.Acsp());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<backhaul> */
				if(profileObj.getBackhaul() == null){
					profileObj.setBackhaul(new RadioObj.Profile.Backhaul());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<load-balance> */
				if(profileObj.getLoadBalance() == null){
					profileObj.setLoadBalance(new RadioObj.Profile.LoadBalance());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<scan> */
				if(profileObj.getScan() == null){
					profileObj.setScan(new RadioObj.Profile.Scan());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<wmm> */
				if(profileObj.getWmm() == null){
					profileObj.setWmm(new RadioObj.Profile.Wmm());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<interference-map> */
				if(profileObj.getInterferenceMap() == null){
					profileObj.setInterferenceMap(new RadioObj.Profile.InterferenceMap());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<benchmark> */
				if(profileObj.getBenchmark() == null){
					profileObj.setBenchmark(new RadioObj.Profile.Benchmark());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<old-high-density> */
				if(profileObj.getOldHighDensity() == null){
					profileObj.setOldHighDensity(new RadioObj.Profile.OldHighDensity());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<high-density> */
				if(profileObj.getHighDensity() == null){
					profileObj.setHighDensity(new RadioHighDensity());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<band-steering> */
				if(profileObj.getBandSteering() == null){
					profileObj.setBandSteering(new RadioBandSteering());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<weak-snr-suppress> */
				if(profileObj.getWeakSnrSuppress() == null){
					profileObj.setWeakSnrSuppress(new RadioWeakSnrSuppress());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<client-load-balance> */
				if(profileObj.getClientLoadBalance() == null){
					profileObj.setClientLoadBalance(new RadioClientLoadBalance());
				}
				
				/** 4 element: <configuration>.<radio>.<profile>.<safety-net> */
				if(profileObj.getSafetyNet() == null){
					profileObj.setSafetyNet(new RadioSafetyNet());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<acsp>.<access> */
				if(profileObj.getAcsp().getAccess() == null){
					profileObj.getAcsp().setAccess(new AcspAccess());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<acsp>.<all-channels-model> */
				if(profileObj.getAcsp().getAllChannelsModel() == null){
					profileObj.getAcsp().setAllChannelsModel(new AhEnable());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<acsp>.<interference-switch> */
				if(profileObj.getAcsp().getInterferenceSwitch() == null){
					profileObj.getAcsp().setInterferenceSwitch(new AcspInterferenceSwitch());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<backhaul>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBackhaul().getAHDELTAASSISTANT() == null){
					profileObj.getBackhaul().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<load-balance> */
				if(profileObj.getLoadBalance().getAHDELTAASSISTANT() == null){
					profileObj.getLoadBalance().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<scan>.<access> */
				if(profileObj.getScan().getAccess() == null){
					profileObj.getScan().setAccess(new RadioObj.Profile.Scan.Access());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<wmm>.<ac> */
				if(profileObj.getWmm().getAc() == null){
					profileObj.getWmm().setAc(new RadioObj.Profile.Wmm.Ac());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<interference-map>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getInterferenceMap().getAHDELTAASSISTANT() == null){
					profileObj.getInterferenceMap().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode> */
				if(profileObj.getBenchmark().getPhymode() == null){
					profileObj.getBenchmark().setPhymode(new BenchmarkPhymode());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<band-steering> */
				if(profileObj.getOldHighDensity().getBandSteering() == null){
					profileObj.getOldHighDensity().setBandSteering(new AhEnable());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<continuous-probe-suppress> */
				if(profileObj.getOldHighDensity().getContinuousProbeSuppress() == null){
					profileObj.getOldHighDensity().setContinuousProbeSuppress(new AhEnable());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<broadcast-probe-suppress> */
				if(profileObj.getOldHighDensity().getBroadcastProbeSuppress() == null){
					profileObj.getOldHighDensity().setBroadcastProbeSuppress(new AhEnable());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<weak-snr-suppress> */
				if(profileObj.getOldHighDensity().getWeakSnrSuppress() == null){
					profileObj.getOldHighDensity().setWeakSnrSuppress(new RadioObj.Profile.OldHighDensity.WeakSnrSuppress());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<client-load-balance> */
				if(profileObj.getOldHighDensity().getClientLoadBalance() == null){
					profileObj.getOldHighDensity().setClientLoadBalance(new RadioObj.Profile.OldHighDensity.ClientLoadBalance());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<old-high-density>.<safety-net> */
				if(profileObj.getOldHighDensity().getSafetyNet() == null){
					profileObj.getOldHighDensity().setSafetyNet(new RadioObj.Profile.OldHighDensity.SafetyNet());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<high-density>.<continuous-probe-suppress> */
				if(profileObj.getHighDensity().getContinuousProbeSuppress() == null){
					profileObj.getHighDensity().setContinuousProbeSuppress(new AhEnable());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<high-density>.<broadcast-probe-suppress> */
				if(profileObj.getHighDensity().getBroadcastProbeSuppress() == null){
					//profileObj.getHighDensity().setBroadcastProbeSuppress(new AhEnable());
				}
				
				/**5 element: <configuration>.<radio>.<profile>.<band-steering>.<prefer-5g> */
				if(profileObj.getBandSteering().getPrefer5G() == null){
					profileObj.getBandSteering().setPrefer5G(new BandSteeringModePrefer5G());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<band-steering>.<balance-band> */
				if(profileObj.getBandSteering().getBalanceBand() == null){
					profileObj.getBandSteering().setBalanceBand(new BandSteeringModeBalanceBand());
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<weak-snr-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getWeakSnrSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getWeakSnrSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<client-load-balance>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getClientLoadBalance().getAHDELTAASSISTANT() == null){
					profileObj.getClientLoadBalance().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<radio>.<profile>.<safety-net>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getSafetyNet().getAHDELTAASSISTANT() == null){
					profileObj.getSafetyNet().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				
				/** 6 element: <configuration>.<radio>.<profile>.<acsp>.<access>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getAcsp().getAccess().getAHDELTAASSISTANT() == null){
					profileObj.getAcsp().getAccess().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}

				/** 6 element: <configuration>.<radio>.<profile>.<acsp>.<interference-switch>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getAcsp().getInterferenceSwitch().getAHDELTAASSISTANT() == null){
					profileObj.getAcsp().getInterferenceSwitch().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<acsp>.<all-channels-model>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getAcsp().getAllChannelsModel().getAHDELTAASSISTANT() == null){
					profileObj.getAcsp().getAllChannelsModel().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<scan>.<access>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getScan().getAccess().getAHDELTAASSISTANT() == null){
					profileObj.getScan().getAccess().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<scan>.<access>.<client> */
				if(profileObj.getScan().getAccess().getClient() == null){
					profileObj.getScan().getAccess().setClient(new ScanAccessClient());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<background> */
				if(profileObj.getWmm().getAc().getBackground() == null){
					profileObj.getWmm().getAc().setBackground(new WmmAcType());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<best-effort> */
				if(profileObj.getWmm().getAc().getBestEffort() == null){
					profileObj.getWmm().getAc().setBestEffort(new WmmAcType());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<video> */
				if(profileObj.getWmm().getAc().getVideo() == null){
					profileObj.getWmm().getAc().setVideo(new WmmAcType());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<voice> */
				if(profileObj.getWmm().getAc().getVoice() == null){
					profileObj.getWmm().getAc().setVoice(new WmmAcType());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11a> */
				if(profileObj.getBenchmark().getPhymode().get11A() == null){
					profileObj.getBenchmark().getPhymode().set11A(new PhymodeRate());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11b> */
				if(profileObj.getBenchmark().getPhymode().get11B() == null){
					profileObj.getBenchmark().getPhymode().set11B(new PhymodeRate());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11g> */
				if(profileObj.getBenchmark().getPhymode().get11G() == null){
					profileObj.getBenchmark().getPhymode().set11G(new PhymodeRate());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11n> */
				if(profileObj.getBenchmark().getPhymode().get11N() == null){
					profileObj.getBenchmark().getPhymode().set11N(new PhymodeRate());
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<band-steering>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getBandSteering().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getBandSteering().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<continuous-probe-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getContinuousProbeSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getContinuousProbeSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<broadcast-probe-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getBroadcastProbeSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getBroadcastProbeSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<weak-snr-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getWeakSnrSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getWeakSnrSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<client-load-balance>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getClientLoadBalance().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getClientLoadBalance().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<old-high-density>.<safety-net>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getOldHighDensity().getSafetyNet().getAHDELTAASSISTANT() == null){
					profileObj.getOldHighDensity().getSafetyNet().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<high-density>.<continuous-probe-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getHighDensity().getContinuousProbeSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getHighDensity().getContinuousProbeSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<high-density>.<broadcast-probe-suppress>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getHighDensity().getBroadcastProbeSuppress().getAHDELTAASSISTANT() == null){
					profileObj.getHighDensity().getBroadcastProbeSuppress().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<band-steering>.<prefer-5g>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBandSteering().getPrefer5G().getAHDELTAASSISTANT() == null){
					profileObj.getBandSteering().getPrefer5G().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<radio>.<profile>.<band-steering>.<balance-band>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBandSteering().getBalanceBand().getAHDELTAASSISTANT() == null){
					profileObj.getBandSteering().getBalanceBand().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}

				/** 7 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<background>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getWmm().getAc().getBackground().getAHDELTAASSISTANT() == null){
					profileObj.getWmm().getAc().getBackground().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<best-effort>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getWmm().getAc().getBestEffort().getAHDELTAASSISTANT() == null){
					profileObj.getWmm().getAc().getBestEffort().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<video>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getWmm().getAc().getVideo().getAHDELTAASSISTANT() == null){
					profileObj.getWmm().getAc().getVideo().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<wmm>.<ac>.<voice>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getWmm().getAc().getVoice().getAHDELTAASSISTANT() == null){
					profileObj.getWmm().getAc().getVoice().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11a>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBenchmark().getPhymode().get11A().getAHDELTAASSISTANT() == null){
					profileObj.getBenchmark().getPhymode().get11A().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11b>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBenchmark().getPhymode().get11B().getAHDELTAASSISTANT() == null){
					profileObj.getBenchmark().getPhymode().get11B().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11g>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBenchmark().getPhymode().get11G().getAHDELTAASSISTANT() == null){
					profileObj.getBenchmark().getPhymode().get11G().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<benchmark>.<phymode>.<_11n>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getBenchmark().getPhymode().get11N().getAHDELTAASSISTANT() == null){
					profileObj.getBenchmark().getPhymode().get11N().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<radio>.<profile>.<scan>.<access>.<client>.<AH-DELTA-ASSISTANT> */
				if(profileObj.getScan().getAccess().getClient().getAHDELTAASSISTANT() == null){
					profileObj.getScan().getAccess().getClient().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
			}
		}
	}
	
	private void fillQosTree(){
		
		/** 2 element: <configuration>.<qos> */
		if(configureObj.getQos() == null){
			QosObj qosObj = new QosObj();
			
			/** attribute: updateTime */
			qosObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setQos(qosObj);
		}
		
		/** 3 element: <configuration>.<qos>.<classifier-map> */
		if(configureObj.getQos().getClassifierMap() == null){
			configureObj.getQos().setClassifierMap(new QosObj.ClassifierMap());
		}
		
		/** 3 element: <configuration>.<qos>.<marker-map> */
		if(configureObj.getQos().getMarkerMap() == null){
			configureObj.getQos().setMarkerMap(new QosObj.MarkerMap());
		}
		
		/** 3 element: <configuration>.<qos>.<airtime> */
		if(configureObj.getQos().getAirtime() == null){
			configureObj.getQos().setAirtime(new QosObj.Airtime());
		}
		
		/** 3 element: <configuration>.<qos>.<l3-police> */
		if(configureObj.getQos().getL3Police() == null){
			configureObj.getQos().setL3Police(new QosL3Police());
		}
		
//		/** 4 element: <configuration>.<qos>.<classifier-map>.<interface> */
//		if(configureObj.getQos().getClassifierMap().getInterface() == null){
//			configureObj.getQos().getClassifierMap().setInterface(new QosObj.ClassifierMap.Interface());
//		}
		
		/** 4 element: <configuration>.<qos>.<classifier-map>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getClassifierMap().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getClassifierMap().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<qos>.<classifier-map>.<service> */
		if(configureObj.getQos().getClassifierMap().getService() != null){
			for(QosMapService qosService : configureObj.getQos().getClassifierMap().getService()){
				
				/** 5 element: <configuration>.<qos>.<classifier-map>.<service>.<AH-DELTA-ASSISTANT> */
				qosService.setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
		
		/** 4 element: <configuration>.<qos>.<classifier-map>.<ssid> */
		if(configureObj.getQos().getClassifierMap().getSsid() != null){
			for(QosMapSsid qosMapSsidObj : configureObj.getQos().getClassifierMap().getSsid()){
				
				/** 5 element: <configuration>.<qos>.<classifier-map>.<ssid>.<AH-DELTA-ASSISTANT> */
				qosMapSsidObj.setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
		
		/** 4 element: <configuration>.<qos>.<marker-map>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getMarkerMap().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getMarkerMap().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<qos>.<airtime>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getAirtime().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getAirtime().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<qos>.<l3-police>.<interface> */
		if(configureObj.getQos().getL3Police().getInterface() == null){
			configureObj.getQos().getL3Police().setInterface(new QosL3PoliceInterface());		
		}
		
//		/** 5 element: <configuration>.<qos>.<classifier-map>.<interface>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getQos().getClassifierMap().getInterface().getAHDELTAASSISTANT() == null){
//			configureObj.getQos().getClassifierMap().getInterface().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 5 element: <configuration>.<qos>.<l3-police>.<interface><eth0> */
		if(configureObj.getQos().getL3Police().getInterface().getEth0() == null){
			configureObj.getQos().getL3Police().getInterface().setEth0(new QosL3PoliceSpecificInterface());		
		}
		
		/** 5 element: <configuration>.<qos>.<l3-police>.<interface><tunnel0> */
		if(configureObj.getQos().getL3Police().getInterface().getTunnel0() == null){
			configureObj.getQos().getL3Police().getInterface().setTunnel0(new QosL3PoliceSpecificInterface());		
		}
		
		/** 5 element: <configuration>.<qos>.<l3-police>.<interface><tunnel1> */
		if(configureObj.getQos().getL3Police().getInterface().getTunnel1() == null){
			configureObj.getQos().getL3Police().getInterface().setTunnel1(new QosL3PoliceSpecificInterface());		
		}
		
		/** 5 element: <configuration>.<qos>.<l3-police>.<interface><ppp0> */
		if(configureObj.getQos().getL3Police().getInterface().getPpp0() == null){
			configureObj.getQos().getL3Police().getInterface().setPpp0(new QosL3PoliceSpecificInterface());		
		}
		
		/** 5 element: <configuration>.<qos>.<l3-police>.<interface><ppp1> */
		if(configureObj.getQos().getL3Police().getInterface().getPpp1() == null){
			configureObj.getQos().getL3Police().getInterface().setPpp1(new QosL3PoliceSpecificInterface());		
		}
		
		/** 6 element: <configuration>.<qos>.<l3-police>.<interface><eth0>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getL3Police().getInterface().getEth0().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getL3Police().getInterface().getEth0().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);		
		}
		
		/** 6 element: <configuration>.<qos>.<l3-police>.<interface><tunnel0>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getL3Police().getInterface().getTunnel0().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getL3Police().getInterface().getTunnel0().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<qos>.<l3-police>.<interface><tunnel1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getL3Police().getInterface().getTunnel1().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getL3Police().getInterface().getTunnel1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);		
		}
		
		/** 6 element: <configuration>.<qos>.<l3-police>.<interface><ppp0>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getL3Police().getInterface().getPpp0().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getL3Police().getInterface().getPpp0().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);		
		}
		
		/** 6 element: <configuration>.<qos>.<l3-police>.<interface><ppp1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getQos().getL3Police().getInterface().getPpp1().getAHDELTAASSISTANT() == null){
			configureObj.getQos().getL3Police().getInterface().getPpp1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);		
		}
	}
	
	private void fillInterfaceTree(){
		
		/** 2 element: <configuration>.<interface> */
		if(configureObj.getInterface() == null){
			InterfaceObj interfaceObj = new InterfaceObj();
			
			/** attribute: updateTime */
			interfaceObj.setUpdateTime(CLICommonFunc.getLastUpdateTime(null));
			
			configureObj.setInterface(interfaceObj);
		}
		
		/** 3 element: <configuration>.<interface>.<eth0> */
		if(configureObj.getInterface().getEth0() == null){
			configureObj.getInterface().setEth0(new Eth());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0> */
		if(configureObj.getInterface().getMgt0() == null){
			configureObj.getInterface().setMgt0(new Mgt());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.1> */
		if(configureObj.getInterface().getMgt01() == null){
			configureObj.getInterface().setMgt01(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.2> */
		if(configureObj.getInterface().getMgt02() == null){
			configureObj.getInterface().setMgt02(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.3> */
		if(configureObj.getInterface().getMgt03() == null){
			configureObj.getInterface().setMgt03(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.4> */
		if(configureObj.getInterface().getMgt04() == null){
			configureObj.getInterface().setMgt04(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.5> */
		if(configureObj.getInterface().getMgt05() == null){
			configureObj.getInterface().setMgt05(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.6> */
		if(configureObj.getInterface().getMgt06() == null){
			configureObj.getInterface().setMgt06(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.7> */
		if(configureObj.getInterface().getMgt07() == null){
			configureObj.getInterface().setMgt07(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.8> */
		if(configureObj.getInterface().getMgt08() == null){
			configureObj.getInterface().setMgt08(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.9> */
		if(configureObj.getInterface().getMgt09() == null){
			configureObj.getInterface().setMgt09(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.10> */
		if(configureObj.getInterface().getMgt010() == null){
			configureObj.getInterface().setMgt010(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.11> */
		if(configureObj.getInterface().getMgt011() == null){
			configureObj.getInterface().setMgt011(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.12> */
		if(configureObj.getInterface().getMgt012() == null){
			configureObj.getInterface().setMgt012(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.13> */
		if(configureObj.getInterface().getMgt013() == null){
			configureObj.getInterface().setMgt013(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.14> */
		if(configureObj.getInterface().getMgt014() == null){
			configureObj.getInterface().setMgt014(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.15> */
		if(configureObj.getInterface().getMgt015() == null){
			configureObj.getInterface().setMgt015(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<mgt0.16> */
		if(configureObj.getInterface().getMgt016() == null){
			configureObj.getInterface().setMgt016(new Mgtxy());
		}
		
		/** 3 element: <configuration>.<interface>.<wifi0> */
		if(configureObj.getInterface().getWifi0() == null){
			configureObj.getInterface().setWifi0(new Wifi());
		}
		
		/** 3 element: <configuration>.<interface>.<wifi1> */
		if(configureObj.getInterface().getWifi1() == null){
			configureObj.getInterface().setWifi1(new Wifi());
		}
		
		/** 3 element: <configuration>.<interface>.<eth1> */
		if(configureObj.getInterface().getEth1() == null){
			configureObj.getInterface().setEth1(new Eth());
		}
		
		/** 3 element: <configuration>.<interface>.<eth2> */
		if(configureObj.getInterface().getEth2() == null){
			configureObj.getInterface().setEth2(new Eth());
		}
		
		/** 3 element: <configuration>.<interface>.<eth3> */
		if(configureObj.getInterface().getEth3() == null){
			configureObj.getInterface().setEth3(new Eth());
		}
		
		/** 3 element: <configuration>.<interface>.<eth4> */
		if(configureObj.getInterface().getEth4() == null){
			configureObj.getInterface().setEth4(new Eth());
		}
		
		/** 3 element: <configuration>.<interface>.<red0> */
		if(configureObj.getInterface().getRed0() == null){
			configureObj.getInterface().setRed0(new RedxAggx());
		}
		
		/** 3 element: <configuration>.<interface>.<agg0> */
		if(configureObj.getInterface().getAgg0() == null){
			configureObj.getInterface().setAgg0(new RedxAggx());
		}
		
		/** 3 element: <configuration>.<interface>.<usbnet0> */
		if(configureObj.getInterface().getUsbnet0() == null){
			configureObj.getInterface().setUsbnet0(new Usbnet());
		}
		
		/** 4 element: <configuration>.<interface>.<eth0>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth0().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth0().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<interface>.<eth0>.<pse> */
		if(configureObj.getInterface().getEth0().getPse() == null){
			configureObj.getInterface().getEth0().setPse(new EthxPse());
		}
		
		/** 4 element: <configuration>.<interface>.<eth0>.<manager> */
		if(configureObj.getInterface().getEth0().getManage() == null){
			configureObj.getInterface().getEth0().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<eth0>.<mac-learning> */
		if(configureObj.getInterface().getEth0().getMacLearning() == null){
			configureObj.getInterface().getEth0().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<eth0>.<link-discovery> */
		if(configureObj.getInterface().getEth0().getLinkDiscovery() == null){
			configureObj.getInterface().getEth0().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0>.<dhcp> */
		if(configureObj.getInterface().getMgt0().getDhcp() == null){
			configureObj.getInterface().getMgt0().setDhcp(new Mgt.Dhcp());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0>.<ip-helper> */
		if(configureObj.getInterface().getMgt0().getIpHelper() == null){
			configureObj.getInterface().getMgt0().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0>.<dhcp-server> */
		if(configureObj.getInterface().getMgt0().getDhcpServer() == null){
			configureObj.getInterface().getMgt0().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0>.<dns-server> */
		if(configureObj.getInterface().getMgt0().getDnsServer() == null){
			configureObj.getInterface().getMgt0().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.1>.<ip-helper> */
		if(configureObj.getInterface().getMgt01().getIpHelper() == null){
			configureObj.getInterface().getMgt01().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.1>.<manage> */
		if(configureObj.getInterface().getMgt01().getManage() == null){
			configureObj.getInterface().getMgt01().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.1>.<dhcp-server> */
		if(configureObj.getInterface().getMgt01().getDhcpServer() == null){
			configureObj.getInterface().getMgt01().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.1>.<dns-server> */
		if(configureObj.getInterface().getMgt01().getDnsServer() == null){
			configureObj.getInterface().getMgt01().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.2>.<ip-helper> */
		if(configureObj.getInterface().getMgt02().getIpHelper() == null){
			configureObj.getInterface().getMgt02().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.2>.<manage> */
		if(configureObj.getInterface().getMgt02().getManage() == null){
			configureObj.getInterface().getMgt02().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.2>.<dhcp-server> */
		if(configureObj.getInterface().getMgt02().getDhcpServer() == null){
			configureObj.getInterface().getMgt02().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.2>.<dns-server> */
		if(configureObj.getInterface().getMgt02().getDnsServer() == null){
			configureObj.getInterface().getMgt02().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.3>.<ip-helper> */
		if(configureObj.getInterface().getMgt03().getIpHelper() == null){
			configureObj.getInterface().getMgt03().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.3>.<manage> */
		if(configureObj.getInterface().getMgt03().getManage() == null){
			configureObj.getInterface().getMgt03().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.3>.<dhcp-server> */
		if(configureObj.getInterface().getMgt03().getDhcpServer() == null){
			configureObj.getInterface().getMgt03().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.3>.<dns-server> */
		if(configureObj.getInterface().getMgt03().getDnsServer() == null){
			configureObj.getInterface().getMgt03().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.4>.<ip-helper> */
		if(configureObj.getInterface().getMgt04().getIpHelper() == null){
			configureObj.getInterface().getMgt04().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.4>.<manage> */
		if(configureObj.getInterface().getMgt04().getManage() == null){
			configureObj.getInterface().getMgt04().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.4>.<dhcp-server> */
		if(configureObj.getInterface().getMgt04().getDhcpServer() == null){
			configureObj.getInterface().getMgt04().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.4>.<dns-server> */
		if(configureObj.getInterface().getMgt04().getDnsServer() == null){
			configureObj.getInterface().getMgt04().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.5>.<ip-helper> */
		if(configureObj.getInterface().getMgt05().getIpHelper() == null){
			configureObj.getInterface().getMgt05().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.5>.<manage> */
		if(configureObj.getInterface().getMgt05().getManage() == null){
			configureObj.getInterface().getMgt05().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.5>.<dhcp-server> */
		if(configureObj.getInterface().getMgt05().getDhcpServer() == null){
			configureObj.getInterface().getMgt05().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.5>.<dns-server> */
		if(configureObj.getInterface().getMgt05().getDnsServer() == null){
			configureObj.getInterface().getMgt05().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.6>.<ip-helper> */
		if(configureObj.getInterface().getMgt06().getIpHelper() == null){
			configureObj.getInterface().getMgt06().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.6>.<manage> */
		if(configureObj.getInterface().getMgt06().getManage() == null){
			configureObj.getInterface().getMgt06().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.6>.<dhcp-server> */
		if(configureObj.getInterface().getMgt06().getDhcpServer() == null){
			configureObj.getInterface().getMgt06().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.6>.<dns-server> */
		if(configureObj.getInterface().getMgt06().getDnsServer() == null){
			configureObj.getInterface().getMgt06().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.7>.<ip-helper> */
		if(configureObj.getInterface().getMgt07().getIpHelper() == null){
			configureObj.getInterface().getMgt07().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.7>.<manage> */
		if(configureObj.getInterface().getMgt07().getManage() == null){
			configureObj.getInterface().getMgt07().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.7>.<dhcp-server> */
		if(configureObj.getInterface().getMgt07().getDhcpServer() == null){
			configureObj.getInterface().getMgt07().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.7>.<dns-server> */
		if(configureObj.getInterface().getMgt07().getDnsServer() == null){
			configureObj.getInterface().getMgt07().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.8>.<ip-helper> */
		if(configureObj.getInterface().getMgt08().getIpHelper() == null){
			configureObj.getInterface().getMgt08().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.8>.<manage> */
		if(configureObj.getInterface().getMgt08().getManage() == null){
			configureObj.getInterface().getMgt08().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.8>.<dhcp-server> */
		if(configureObj.getInterface().getMgt08().getDhcpServer() == null){
			configureObj.getInterface().getMgt08().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.8>.<dns-server> */
		if(configureObj.getInterface().getMgt08().getDnsServer() == null){
			configureObj.getInterface().getMgt08().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.9>.<ip-helper> */
		if(configureObj.getInterface().getMgt09().getIpHelper() == null){
			configureObj.getInterface().getMgt09().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.9>.<manage> */
		if(configureObj.getInterface().getMgt09().getManage() == null){
			configureObj.getInterface().getMgt09().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.9>.<dhcp-server> */
		if(configureObj.getInterface().getMgt09().getDhcpServer() == null){
			configureObj.getInterface().getMgt09().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.9>.<dns-server> */
		if(configureObj.getInterface().getMgt09().getDnsServer() == null){
			configureObj.getInterface().getMgt09().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.10>.<ip-helper> */
		if(configureObj.getInterface().getMgt010().getIpHelper() == null){
			configureObj.getInterface().getMgt010().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.10>.<manage> */
		if(configureObj.getInterface().getMgt010().getManage() == null){
			configureObj.getInterface().getMgt010().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.10>.<dhcp-server> */
		if(configureObj.getInterface().getMgt010().getDhcpServer() == null){
			configureObj.getInterface().getMgt010().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.10>.<dns-server> */
		if(configureObj.getInterface().getMgt010().getDnsServer() == null){
			configureObj.getInterface().getMgt010().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.11>.<ip-helper> */
		if(configureObj.getInterface().getMgt011().getIpHelper() == null){
			configureObj.getInterface().getMgt011().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.11>.<manage> */
		if(configureObj.getInterface().getMgt011().getManage() == null){
			configureObj.getInterface().getMgt011().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.11>.<dhcp-server> */
		if(configureObj.getInterface().getMgt011().getDhcpServer() == null){
			configureObj.getInterface().getMgt011().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.11>.<dns-server> */
		if(configureObj.getInterface().getMgt011().getDnsServer() == null){
			configureObj.getInterface().getMgt011().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.12>.<ip-helper> */
		if(configureObj.getInterface().getMgt012().getIpHelper() == null){
			configureObj.getInterface().getMgt012().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.12>.<manage> */
		if(configureObj.getInterface().getMgt012().getManage() == null){
			configureObj.getInterface().getMgt012().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.12>.<dhcp-server> */
		if(configureObj.getInterface().getMgt012().getDhcpServer() == null){
			configureObj.getInterface().getMgt012().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.12>.<dns-server> */
		if(configureObj.getInterface().getMgt012().getDnsServer() == null){
			configureObj.getInterface().getMgt012().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.13>.<ip-helper> */
		if(configureObj.getInterface().getMgt013().getIpHelper() == null){
			configureObj.getInterface().getMgt013().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.13>.<manage> */
		if(configureObj.getInterface().getMgt013().getManage() == null){
			configureObj.getInterface().getMgt013().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.13>.<dhcp-server> */
		if(configureObj.getInterface().getMgt013().getDhcpServer() == null){
			configureObj.getInterface().getMgt013().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.13>.<dns-server> */
		if(configureObj.getInterface().getMgt013().getDnsServer() == null){
			configureObj.getInterface().getMgt013().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.14>.<ip-helper> */
		if(configureObj.getInterface().getMgt014().getIpHelper() == null){
			configureObj.getInterface().getMgt014().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.14>.<manage> */
		if(configureObj.getInterface().getMgt014().getManage() == null){
			configureObj.getInterface().getMgt014().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.14>.<dhcp-server> */
		if(configureObj.getInterface().getMgt014().getDhcpServer() == null){
			configureObj.getInterface().getMgt014().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.14>.<dns-server> */
		if(configureObj.getInterface().getMgt014().getDnsServer() == null){
			configureObj.getInterface().getMgt014().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.15>.<ip-helper> */
		if(configureObj.getInterface().getMgt015().getIpHelper() == null){
			configureObj.getInterface().getMgt015().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.15>.<manage> */
		if(configureObj.getInterface().getMgt015().getManage() == null){
			configureObj.getInterface().getMgt015().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.15>.<dhcp-server> */
		if(configureObj.getInterface().getMgt015().getDhcpServer() == null){
			configureObj.getInterface().getMgt015().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.15>.<dns-server> */
		if(configureObj.getInterface().getMgt015().getDnsServer() == null){
			configureObj.getInterface().getMgt015().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.16>.<ip-helper> */
		if(configureObj.getInterface().getMgt016().getIpHelper() == null){
			configureObj.getInterface().getMgt016().setIpHelper(new InterfaceIpHelper());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.16>.<manage> */
		if(configureObj.getInterface().getMgt016().getManage() == null){
			configureObj.getInterface().getMgt016().setManage(new Mgtxy.Manage());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.16>.<dhcp-server> */
		if(configureObj.getInterface().getMgt016().getDhcpServer() == null){
			configureObj.getInterface().getMgt016().setDhcpServer(new MgtDhcpServer());
		}
		
		/** 4 element: <configuration>.<interface>.<mgt0.16>.<dns-server> */
		if(configureObj.getInterface().getMgt016().getDnsServer() == null){
			configureObj.getInterface().getMgt016().setDnsServer(new MgtDnsServer());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi0>.<wlan-idp> */
		if(configureObj.getInterface().getWifi0().getWlanIdp() == null){
			configureObj.getInterface().getWifi0().setWlanIdp(new Wifi.WlanIdp());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi0>.<link-discovery> */
		if(configureObj.getInterface().getWifi0().getLinkDiscovery() == null){
			configureObj.getInterface().getWifi0().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi0>.<radio> */
		if(configureObj.getInterface().getWifi0().getRadio() == null){
			configureObj.getInterface().getWifi0().setRadio(new Wifi.Radio());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi0>.<client-mode> */
//		if(configureObj.getInterface().getWifi0().getClientMode() == null){
//			configureObj.getInterface().getWifi0().setClientMode(new WifixClientMode());
//		}
		
		/** 4 element: <configuration>.<interface>.<wifi1>.<wlan-idp> */
		if(configureObj.getInterface().getWifi1().getWlanIdp() == null){
			configureObj.getInterface().getWifi1().setWlanIdp(new Wifi.WlanIdp());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi1>.<link-discovery> */
		if(configureObj.getInterface().getWifi1().getLinkDiscovery() == null){
			configureObj.getInterface().getWifi1().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi1>.<radio> */
		if(configureObj.getInterface().getWifi1().getRadio() == null){
			configureObj.getInterface().getWifi1().setRadio(new Wifi.Radio());
		}
		
		/** 4 element: <configuration>.<interface>.<wifi1>.<client-mode> */
//		if(configureObj.getInterface().getWifi1().getClientMode() == null){
//			configureObj.getInterface().getWifi1().setClientMode(new WifixClientMode());
//		}
		
		/** 4 element: <configuration>.<interface>.<eth1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth1().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<interface>.<eth1>.<pse> */
		if(configureObj.getInterface().getEth1().getPse() == null){
			configureObj.getInterface().getEth1().setPse(new EthxPse());
		}
		
		/** 4 element: <configuration>.<interface>.<eth1>.<manager> */
		if(configureObj.getInterface().getEth1().getManage() == null){
			configureObj.getInterface().getEth1().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<eth1>.<link-discovery> */
		if(configureObj.getInterface().getEth1().getLinkDiscovery() == null){
			configureObj.getInterface().getEth1().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<eth1>.<mac-learning> */
		if(configureObj.getInterface().getEth1().getMacLearning() == null){
			configureObj.getInterface().getEth1().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<eth2>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth2().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth2().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<interface>.<eth2>.<pse> */
		if(configureObj.getInterface().getEth2().getPse() == null){
			configureObj.getInterface().getEth2().setPse(new EthxPse());
		}
		
		/** 4 element: <configuration>.<interface>.<eth2>.<manager> */
		if(configureObj.getInterface().getEth2().getManage() == null){
			configureObj.getInterface().getEth2().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<eth2>.<link-discovery> */
		if(configureObj.getInterface().getEth2().getLinkDiscovery() == null){
			configureObj.getInterface().getEth2().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<eth2>.<mac-learning> */
		if(configureObj.getInterface().getEth2().getMacLearning() == null){
			configureObj.getInterface().getEth2().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<eth3>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth3().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth3().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<interface>.<eth3>.<pse> */
		if(configureObj.getInterface().getEth3().getPse() == null){
			configureObj.getInterface().getEth3().setPse(new EthxPse());
		}
		
		/** 4 element: <configuration>.<interface>.<eth3>.<manager> */
		if(configureObj.getInterface().getEth3().getManage() == null){
			configureObj.getInterface().getEth3().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<eth3>.<link-discovery> */
		if(configureObj.getInterface().getEth3().getLinkDiscovery() == null){
			configureObj.getInterface().getEth3().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<eth3>.<mac-learning> */
		if(configureObj.getInterface().getEth3().getMacLearning() == null){
			configureObj.getInterface().getEth3().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<eth4>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth4().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth4().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 4 element: <configuration>.<interface>.<eth4>.<pse> */
		if(configureObj.getInterface().getEth4().getPse() == null){
			configureObj.getInterface().getEth4().setPse(new EthxPse());
		}
		
		/** 4 element: <configuration>.<interface>.<eth4>.<manager> */
		if(configureObj.getInterface().getEth4().getManage() == null){
			configureObj.getInterface().getEth4().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<eth4>.<link-discovery> */
		if(configureObj.getInterface().getEth4().getLinkDiscovery() == null){
			configureObj.getInterface().getEth4().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<eth4>.<mac-learning> */
		if(configureObj.getInterface().getEth4().getMacLearning() == null){
			configureObj.getInterface().getEth4().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<red0>.<manager> */
		if(configureObj.getInterface().getRed0().getManage() == null){
			configureObj.getInterface().getRed0().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<red0>.<link-discovery> */
		if(configureObj.getInterface().getRed0().getLinkDiscovery() == null){
			configureObj.getInterface().getRed0().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<red0>.<mac-learning> */
		if(configureObj.getInterface().getRed0().getMacLearning() == null){
			configureObj.getInterface().getRed0().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<agg0>.<manager> */
		if(configureObj.getInterface().getAgg0().getManage() == null){
			configureObj.getInterface().getAgg0().setManage(new AhManage());
		}
		
		/** 4 element: <configuration>.<interface>.<agg0>.<link-discovery> */
		if(configureObj.getInterface().getAgg0().getLinkDiscovery() == null){
			configureObj.getInterface().getAgg0().setLinkDiscovery(new InterfaceLinkDiscovery());
		}
		
		/** 4 element: <configuration>.<interface>.<agg0>.<mac-learning> */
		if(configureObj.getInterface().getAgg0().getMacLearning() == null){
			configureObj.getInterface().getAgg0().setMacLearning(new InterfaceMacLearning());
		}
		
		/** 4 element: <configuration>.<interface>.<usbnet0>.<mode> */
		if(configureObj.getInterface().getUsbnet0().getMode() == null){
			configureObj.getInterface().getUsbnet0().setMode(new UsbnetMode());
		}
		
		/** 5 element: <configuration>.<interface>.<eth0>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth0().getPse().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth0().getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth0>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth0().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth0().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth0>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth0().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth0().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth0>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth0().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth0().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0>.<dhcp>.<client> */
		if(configureObj.getInterface().getMgt0().getDhcp().getClient() == null){
			configureObj.getInterface().getMgt0().getDhcp().setClient(new Mgt.Dhcp.Client());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0>.<dhcp>.<keepalive> */
		if(configureObj.getInterface().getMgt0().getDhcp().getKeepalive() == null){
			configureObj.getInterface().getMgt0().getDhcp().setKeepalive(new MgtDhcpKeepalive());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt0().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt0().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt0().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt0().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt0().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt0().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.1>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt01().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt01().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.1>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt01().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt01().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.1>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt01().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt01().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.1>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt01().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt01().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.2>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt02().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt02().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.2>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt02().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt02().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.2>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt02().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt02().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.2>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt02().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt02().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.3>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt03().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt03().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.3>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt03().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt03().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.3>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt03().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt03().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.3>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt03().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt03().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.4>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt04().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt04().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.4>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt04().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt04().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.4>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt04().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt04().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.4>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt04().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt04().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.5>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt05().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt05().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.5>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt05().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt05().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.5>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt05().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt05().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.5>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt05().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt05().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.6>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt06().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt06().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.6>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt06().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt06().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.6>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt06().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt06().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.6>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt06().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt06().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.7>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt07().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt07().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.7>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt07().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt07().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.7>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt07().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt07().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.7>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt07().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt07().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.8>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt08().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt08().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.8>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt08().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt08().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.8>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt08().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt08().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.8>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt08().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt08().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.9>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt09().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt09().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.9>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt09().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt09().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.9>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt09().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt09().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.9>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt09().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt09().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.10>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt010().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt010().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.10>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt010().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt010().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.10>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt010().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt010().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.10>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt010().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt010().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.11>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt011().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt011().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.11>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt011().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt011().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.11>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt011().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt011().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.11>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt011().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt011().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.12>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt012().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt012().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.12>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt012().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt012().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.12>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt012().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt012().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.12>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt012().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt012().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.13>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt013().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt013().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.13>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt013().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt013().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.13>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt013().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt013().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.13>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt013().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt013().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.14>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt014().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt014().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.14>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt014().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt014().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.14>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt014().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt014().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.14>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt014().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt014().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.15>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt015().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt015().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.15>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt015().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt015().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.15>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt015().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt015().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.15>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt015().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt015().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.16>.<ip-helper>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt016().getIpHelper().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt016().getIpHelper().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.16>.<manage>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt016().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt016().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.16>.<dhcp-server>.<options> */
		if(configureObj.getInterface().getMgt016().getDhcpServer().getOptions() == null){
			configureObj.getInterface().getMgt016().getDhcpServer().setOptions(new MgtDhcpServer.Options());
		}
		
		/** 5 element: <configuration>.<interface>.<mgt0.16>.<dns-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt016().getDnsServer().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt016().getDnsServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi0>.<wlan-idp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi0().getWlanIdp().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi0().getWlanIdp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi0>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi0().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi0().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi0>.<radio>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi0().getRadio().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi0().getRadio().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi0>.<radio>.<adaptive-cca> */
		if(configureObj.getInterface().getWifi0().getRadio().getAdaptiveCca() == null){
			configureObj.getInterface().getWifi0().getRadio().setAdaptiveCca(new Wifi.Radio.AdaptiveCca());
		}
		
//		/** 5 element: <configuration>.<interface>.<wifi0>.<hive>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getInterface().getWifi0().getHive() != null 
//				&& configureObj.getInterface().getWifi0().getHive().getAHDELTAASSISTANT() == null){
//			configureObj.getInterface().getWifi0().getHive().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 5 element: <configuration>.<interface>.<wifi0>.<client-mode>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getInterface().getWifi0().getClientMode().getAHDELTAASSISTANT() == null){
//			configureObj.getInterface().getWifi0().getClientMode().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 5 element: <configuration>.<interface>.<wifi1>.<wlan-idp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi1().getWlanIdp().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi1().getWlanIdp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi1>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi1().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi1().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi1>.<radio>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi1().getRadio().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi1().getRadio().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<wifi1>.<radio>.<adaptive-cca> */
		if(configureObj.getInterface().getWifi1().getRadio().getAdaptiveCca() == null){
			configureObj.getInterface().getWifi1().getRadio().setAdaptiveCca(new Wifi.Radio.AdaptiveCca());
		}
		
//		/** 5 element: <configuration>.<interface>.<wifi1>.<hive>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getInterface().getWifi1().getHive() != null 
//				&& configureObj.getInterface().getWifi1().getHive().getAHDELTAASSISTANT() == null){
//			configureObj.getInterface().getWifi1().getHive().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 5 element: <configuration>.<interface>.<wifi1>.<client-mode>.<AH-DELTA-ASSISTANT> */
//		if(configureObj.getInterface().getWifi1().getClientMode().getAHDELTAASSISTANT() == null){
//			configureObj.getInterface().getWifi1().getClientMode().setAHDELTAASSISTANT(
//					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//			);
//		}
		
		/** 5 element: <configuration>.<interface>.<eth1>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth1().getPse().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth1().getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth1>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth1().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth1().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth1>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth1().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth1().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth1>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth1().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth1().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth2>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth2().getPse().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth2().getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth2>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth2().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth2().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth2>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth2().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth2().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth2>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth2().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth2().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth3>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth3().getPse().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth3().getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth3>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth3().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth3().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth3>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth3().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth3().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth3>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth3().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth3().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth4>.<pse>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth4().getPse().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth4().getPse().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth4>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth4().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth4().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth4>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth4().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth4().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<eth4>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getEth4().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getEth4().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<red0>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getRed0().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getRed0().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<red0>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getRed0().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getRed0().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<red0>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getRed0().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getRed0().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<agg0>.<manager>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getAgg0().getManage().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getAgg0().getManage().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<agg0>.<mac-learning>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getAgg0().getMacLearning().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getAgg0().getMacLearning().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<agg0>.<link-discovery>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getAgg0().getLinkDiscovery().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getAgg0().getLinkDiscovery().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<interface>.<usbnet0>.<mode>.<wan> */
		if(configureObj.getInterface().getUsbnet0().getMode().getWan() == null){
			configureObj.getInterface().getUsbnet0().getMode().setWan(new UsbnetWan());
		}
		
		/** 6 element: <configuration>.<interface>.<wifi0>.<radio>.<adaptive-cca>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi0().getRadio().getAdaptiveCca().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi0().getRadio().getAdaptiveCca().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<wifi1>.<radio>.<adaptive-cca>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getWifi1().getRadio().getAdaptiveCca().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getWifi1().getRadio().getAdaptiveCca().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0>.<dhcp>.<client>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt0().getDhcp().getClient().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt0().getDhcp().getClient().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0>.<dhcp>.<keepalive>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt0().getDhcp().getKeepalive().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt0().getDhcp().getKeepalive().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt0().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt0().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.1>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt01().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt01().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.2>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt02().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt02().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.3>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt03().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt03().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.4>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt04().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt04().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.5>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt05().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt05().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.6>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt06().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt06().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.7>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt07().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt07().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.8>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt08().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt08().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.9>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt09().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt09().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.10>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt010().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt010().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.11>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt011().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt011().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.12>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt012().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt012().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.13>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt013().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt013().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.14>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt014().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt014().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.15>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt015().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt015().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<mgt0.16>.<dhcp-server>.<options>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getMgt016().getDhcpServer().getOptions().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getMgt016().getDhcpServer().getOptions().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 6 element: <configuration>.<interface>.<usbnet0>.<mode>.<wan>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getInterface().getUsbnet0().getMode().getWan().getAHDELTAASSISTANT() == null){
			configureObj.getInterface().getUsbnet0().getMode().getWan().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillUserProfileTree(){
		
		if(configureObj.getUserProfile() != null){
			
			for(UserProfileObj userProfileObj : configureObj.getUserProfile()){
				
				/** 3 element: <configuration>.<user-profile>.<security> */
				if(userProfileObj.getSecurity() == null){
					userProfileObj.setSecurity(new UserProfileObj.Security());
				}
				
				/** 3 element: <configuration>.<user-profile>.<cac> */
				if(userProfileObj.getCac() == null){
					userProfileObj.setCac(new UserProfileObj.Cac());
				}
				
				/** 3 element: <configuration>.<user-profile>.<airscreen> */
				if(userProfileObj.getAirscreen() == null){
					userProfileObj.setAirscreen(new UserProfileObj.Airscreen());
				}
				
				/** 3 element: <configuration>.<user-profile>.<performance-sentinel> */
				if(userProfileObj.getPerformanceSentinel() == null){
					userProfileObj.setPerformanceSentinel(new UserProfileObj.PerformanceSentinel());
				}
				
				/** 4 element: <configuration>.<user-profile>.<security>.<AH-DELTA-ASSISTANT> */
				if(userProfileObj.getSecurity().getAHDELTAASSISTANT() == null){
					userProfileObj.getSecurity().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<user-profile>.<cac>.<AH-DELTA-ASSISTANT> */
				if(userProfileObj.getCac().getAHDELTAASSISTANT() == null){
					userProfileObj.getCac().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<user-profile>.<airscreen>.<AH-DELTA-ASSISTANT> */
				if(userProfileObj.getAirscreen().getAHDELTAASSISTANT() == null){
					userProfileObj.getAirscreen().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
					);
				}
				
				/** 4 element: <configuration>.<user-profile>.<performance-sentinel>.<AH-DELTA-ASSISTANT> */
				if(userProfileObj.getPerformanceSentinel().getAHDELTAASSISTANT() == null){
					userProfileObj.getPerformanceSentinel().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
			}
		}
	}
	
	private void fillSsidTree(){
		
		if(configureObj.getSsid() != null){
			
			for(SsidObj ssidObj : configureObj.getSsid()){
				
				/** 3 element: <configuration>.<ssid>.<manage> */
				if(ssidObj.getManage() == null){
					ssidObj.setManage(new AhManage());
				}
				
				/** 3 element: <configuration>.<ssid>.<security> */
				if(ssidObj.getSecurity() == null){
					ssidObj.setSecurity(new SsidObj.Security());
				}
				
//				/** 3 element: <configuration>.<ssid>.<user-profile-deny> */
//				if(ssidObj.getUserProfileDeny() == null){
//					ssidObj.setUserProfileDeny(new SecurityUserProfileDeny());
//				}
				
				/** 3 element: <configuration>.<ssid>.<roaming> */
				if(ssidObj.getRoaming() == null){
					ssidObj.setRoaming(new SsidObj.Roaming());
				}
				
				/** 3 element: <configuration>.<ssid>.<airscreen> */
				if(ssidObj.getAirscreen() == null){
					ssidObj.setAirscreen(new SsidObj.Airscreen());
				}
				
				/** 3 element: <configuration>.<ssid>.<multicast> */
				if(ssidObj.getMulticast() == null){
					ssidObj.setMulticast(new SsidMulticast());
				}
				
				/** 3 element: <configuration>.<ssid>.<rrm> */
				if(ssidObj.getRrm() == null){
					ssidObj.setRrm(new SsidRrm());
				}
				
				/** 3 element: <configuration>.<ssid>.<wnm> */
				if(ssidObj.getWnm() == null){
					ssidObj.setWnm(new AhEnable());
				}
				
				/** 4 element: <configuration>.<ssid>.<manage>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getManage().getAHDELTAASSISTANT() == null){
					ssidObj.getManage().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<additional-auth-method> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod() == null){
					ssidObj.getSecurity().setAdditionalAuthMethod(new SecurityAdditionalAuthMethod());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<screening> */
				if(ssidObj.getSecurity().getScreening() == null){
					ssidObj.getSecurity().setScreening(new SsidObj.Security.Screening());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<wlan> */
				if(ssidObj.getSecurity().getWlan() == null){
					ssidObj.getSecurity().setWlan(new SsidObj.Security.Wlan());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<aaa> */
				if(ssidObj.getSecurity().getAaa() == null){
					ssidObj.getSecurity().setAaa(new SecurityAaa());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<roaming> */
				if(ssidObj.getSecurity().getRoaming() == null){
					ssidObj.getSecurity().setRoaming(new SsidSecurityRoaming());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<local-cache> */
				if(ssidObj.getSecurity().getLocalCache() == null){
					ssidObj.getSecurity().setLocalCache(new SecurityLocalCache());
				}
				
				/** 4 element: <configuration>.<ssid>.<security>.<eap> */
				if(ssidObj.getSecurity().getEap() == null){
					ssidObj.getSecurity().setEap(new SecurityEap());
				}
				
				/** 4 element: <configuration>.<ssid>.<user-profile-deny>.<action> */
				if(ssidObj.getUserProfileDeny().getAction() == null){
					ssidObj.getUserProfileDeny().setAction(new UserProfileDenyAction());
				}
				
				/** 4 element: <configuration>.<ssid>.<roaming>.<cache> */
				if(ssidObj.getRoaming().getCache() == null){
					ssidObj.getRoaming().setCache(new SsidObj.Roaming.Cache());
				}
				
				/** 4 element: <configuration>.<ssid>.<airscreen>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getAirscreen().getAHDELTAASSISTANT() == null){
					ssidObj.getAirscreen().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<ssid>.<multicast>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getMulticast().getAHDELTAASSISTANT() == null){
					ssidObj.getMulticast().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<ssid>.<rrm>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getRrm().getAHDELTAASSISTANT() == null){
					ssidObj.getRrm().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 3 element: <configuration>.<ssid>.<wnm>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getWnm().getAHDELTAASSISTANT() == null){
					ssidObj.getWnm().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<mac-based-auth> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth() == null){
					ssidObj.getSecurity().getAdditionalAuthMethod().setMacBasedAuth(new SecurityAdditionalAuthMethod.MacBasedAuth());
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getAdditionalAuthMethod().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
//				/** 5 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<captive-web-portal> */
//				if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() == null){
//					SecurityAdditionalAuthMethod.CaptiveWebPortal capweb = 
//						new SecurityAdditionalAuthMethod.CaptiveWebPortal();
//					capweb.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
//					ssidObj.getSecurity().getAdditionalAuthMethod().setCaptiveWebPortal(capweb);
//				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<screening>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getScreening().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getScreening().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<wlan>.<dos> */
				if(ssidObj.getSecurity().getWlan().getDos() == null){
					ssidObj.getSecurity().getWlan().setDos(new SsidWlanDos());
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server> */
				if(ssidObj.getSecurity().getAaa().getRadiusServer() == null){
					ssidObj.getSecurity().getAaa().setRadiusServer(new SecurityAaa.RadiusServer());
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<roaming>.<cache> */
				if(ssidObj.getSecurity().getRoaming().getCache() == null){
					ssidObj.getSecurity().getRoaming().setCache(new SsidRoamingCache());
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<local-cache>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getLocalCache().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getLocalCache().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<ssid>.<security>.<eap>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getEap().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getEap().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<ban> */
				if(ssidObj.getUserProfileDeny().getAction().getBan() == null){
					ssidObj.getUserProfileDeny().getAction().setBan(new UserProfileDenyAction.Ban());
				}
				
				/** 5 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<ban-forever> */
				if(ssidObj.getUserProfileDeny().getAction().getBanForever() == null){
					ssidObj.getUserProfileDeny().getAction().setBanForever(new UpDenyBanDisconnect());
				}
				
				/** 5 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<disconnect> */
				if(ssidObj.getUserProfileDeny().getAction().getDisconnect() == null){
					ssidObj.getUserProfileDeny().getAction().setDisconnect(new UpDenyBanDisconnect());
				}
				
				/** 5 element: <configuration>.<ssid>.<roaming>.<cache>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getRoaming().getCache().getAHDELTAASSISTANT() == null){
					ssidObj.getRoaming().getCache().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
//				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<primary> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getPrimary() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().setPrimary(new RadiusServerType());
//				}
//				
//				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup1> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup1() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().setBackup1(new RadiusServerType());
//				}
//				
//				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup2> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup2() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().setBackup2(new RadiusServerType());
//				}
//				
//				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup3> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup3() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().setBackup3(new RadiusServerType());
//				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<accounting> */
				if(ssidObj.getSecurity().getAaa().getRadiusServer().getAccounting() == null){
					ssidObj.getSecurity().getAaa().getRadiusServer().setAccounting(new RadiusAccountingAll());
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAaa().getRadiusServer().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getAaa().getRadiusServer().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<roaming>.<cache>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getRoaming().getCache().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getRoaming().getCache().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<ssid-level> */
				if(ssidObj.getSecurity().getWlan().getDos().getSsidLevel() == null){
					ssidObj.getSecurity().getWlan().getDos().setSsidLevel(new DosSsidLevel());
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<station-level> */
				if(ssidObj.getSecurity().getWlan().getDos().getStationLevel() == null){
					ssidObj.getSecurity().getWlan().getDos().setStationLevel(new DosStationLevel());
				}
				
				/** 6 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<ban>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getUserProfileDeny().getAction().getBan().getAHDELTAASSISTANT() == null){
					ssidObj.getUserProfileDeny().getAction().getBan().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<ban-forever>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getUserProfileDeny().getAction().getBanForever().getAHDELTAASSISTANT() == null){
					ssidObj.getUserProfileDeny().getAction().getBanForever().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<ssid>.<user-profile-deny>.<action>.<disconnect>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getUserProfileDeny().getAction().getDisconnect().getAHDELTAASSISTANT() == null){
					ssidObj.getUserProfileDeny().getAction().getDisconnect().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() != null){
					if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough() == null){
						ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setPassThrough(
								new SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough()
						);
					}
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<captive-web-portal>.<walled-garden> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() != null){
					if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden() == null){
						ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setWalledGarden(new CwpWalledGarden());
					}
				}
				
				/** 6 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<mac-based-auth>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 7 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type> */
				if(ssidObj.getSecurity().getWlan().getDos().getSsidLevel().getFrameType() == null){
					ssidObj.getSecurity().getWlan().getDos().getSsidLevel().setFrameType(new DosSsidLevel.FrameType());
				}
				
				/** 7 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
				if(ssidObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType() == null){
					ssidObj.getSecurity().getWlan().getDos().getStationLevel().setFrameType(new DosStationLevel.FrameType());
				}
				
				/** 7 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() != null){
					if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough().getAHDELTAASSISTANT() == null){
						ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
						);
					}
				}
				
				/** 7 element: <configuration>.<ssid>.<security>.<additional-auth-method>.<captive-web-portal>.<walled-garden>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() != null){
					if(ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden().getAHDELTAASSISTANT() == null){
						ssidObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
						);
					}
				}
				
				/** 7 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<accounting>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getAaa().getRadiusServer().getAccounting().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getAaa().getRadiusServer().getAccounting().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
//				/** 7 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<primary>.<AH-DELTA-ASSISTANT> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getPrimary().getAHDELTAASSISTANT() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().getPrimary().setAHDELTAASSISTANT(
//							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//					);
//				}
//				
//				/** 7 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup1>.<AH-DELTA-ASSISTANT> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup1().getAHDELTAASSISTANT() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().getBackup1().setAHDELTAASSISTANT(
//							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//					);
//				}
//				
//				/** 7 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup2>.<AH-DELTA-ASSISTANT> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup2().getAHDELTAASSISTANT() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().getBackup2().setAHDELTAASSISTANT(
//							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//					);
//				}
//				
//				/** 7 element: <configuration>.<ssid>.<security>.<aaa>.<radius-server>.<backup3>.<AH-DELTA-ASSISTANT> */
//				if(ssidObj.getSecurity().getAaa().getRadiusServer().getBackup3().getAHDELTAASSISTANT() == null){
//					ssidObj.getSecurity().getAaa().getRadiusServer().getBackup3().setAHDELTAASSISTANT(
//							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
//					);
//				}
				
				/** 8 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getWlan().getDos().getSsidLevel().getFrameType().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getWlan().getDos().getSsidLevel().getFrameType().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 8 element: <configuration>.<ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<AH-DELTA-ASSISTANT> */
				if(ssidObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType().getAHDELTAASSISTANT() == null){
					ssidObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
			}
		}
	}
	
	private void fillMobilityPolicyTree(){
		
		if(configureObj.getMobilityPolicy() != null){
			
			for(MobilityPolicyObj mPolibyObj : configureObj.getMobilityPolicy()){
				
				/** 3 element: <configuration>.<mobility-policy>.<dnxp> */
				if(mPolibyObj.getDnxp() == null){
					mPolibyObj.setDnxp(new MobilityPolicyObj.Dnxp());
				}
				
				/** 3 element: <configuration>.<mobility-policy>.<inxp> */
				if(mPolibyObj.getInxp() == null){
					mPolibyObj.setInxp(new MobilityPolicyObj.Inxp());
				}
				
				/** 4 element: <configuration>.<mobility-policy>.<dnxp>.<AH-DELTA-ASSISTANT> */
				if(mPolibyObj.getDnxp().getAHDELTAASSISTANT() == null){
					mPolibyObj.getDnxp().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<mobility-policy>.<inxp>.<gre-tunnel> */
				if(mPolibyObj.getInxp().getGreTunnel() == null){
					mPolibyObj.getInxp().setGreTunnel(new MobilityPolicyObj.Inxp.GreTunnel());
				}
				
				/** 5 element: <configuration>.<mobility-policy>.<inxp>.<gre-tunnel>.<AH-DELTA-ASSISTANT> */
				if(mPolibyObj.getInxp().getGreTunnel().getAHDELTAASSISTANT() == null){
					mPolibyObj.getInxp().getGreTunnel().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
			}
		}
	}
	
	private void fillHiveTree(){
		
		if(configureObj.getHive() != null){
			
			for(HiveObj hiveObj : configureObj.getHive()){
				
				/** 3 element: <configuration>.<hive>.<manage> */
				if(hiveObj.getManage() == null){
					hiveObj.setManage(new AhManage());
				}
				
				/** 3 element: <configuration>.<hive>.<security> */
				if(hiveObj.getSecurity() == null){
					hiveObj.setSecurity(new HiveObj.Security());
				}
				
				/** 3 element: <configuration>.<hive>.<neighbor> */
				if(hiveObj.getNeighbor() == null){
					hiveObj.setNeighbor(new HiveObj.Neighbor());
				}
				
				/** 3 element: <configuration>.<hive>.<wlan-idp> */
				if(hiveObj.getWlanIdp() == null){
					hiveObj.setWlanIdp(new HiveWlanIdp());
				}
				
				/** 4 element: <configuration>.<hive>.<manage>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getManage().getAHDELTAASSISTANT() == null){
					hiveObj.getManage().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<hive>.<neighbor>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getNeighbor().getAHDELTAASSISTANT() == null){
					hiveObj.getNeighbor().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<hive>.<security>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getSecurity().getAHDELTAASSISTANT() == null){
					hiveObj.getSecurity().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
					);
				}
				
				/** 4 element: <configuration>.<hive>.<security>.<wlan> */
				if(hiveObj.getSecurity().getWlan() == null){
					hiveObj.getSecurity().setWlan(new HiveObj.Security.Wlan());
				}
				
				/** 4 element: <configuration>.<hive>.<wlan-idp>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getWlanIdp().getAHDELTAASSISTANT() == null){
					hiveObj.getWlanIdp().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<hive>.<security>.<wlan>.<dos> */
				if(hiveObj.getSecurity().getWlan().getDos() == null){
					hiveObj.getSecurity().getWlan().setDos(new HiveWlanDos());
				}
				
				/** 6 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<hive-level> */
				if(hiveObj.getSecurity().getWlan().getDos().getHiveLevel() == null){
					hiveObj.getSecurity().getWlan().getDos().setHiveLevel(new DosSsidLevel());
				}
				
				/** 6 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<station-level> */
				if(hiveObj.getSecurity().getWlan().getDos().getStationLevel() == null){
					hiveObj.getSecurity().getWlan().getDos().setStationLevel(new DosStationLevel());
				}
				
				/** 7 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type> */
				if(hiveObj.getSecurity().getWlan().getDos().getHiveLevel().getFrameType() == null){
					hiveObj.getSecurity().getWlan().getDos().getHiveLevel().setFrameType(new DosSsidLevel.FrameType());
				}
				
				/** 7 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
				if(hiveObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType() == null){
					hiveObj.getSecurity().getWlan().getDos().getStationLevel().setFrameType(new DosStationLevel.FrameType());
				}
				
				/** 8 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getSecurity().getWlan().getDos().getHiveLevel().getFrameType().getAHDELTAASSISTANT() == null){
					hiveObj.getSecurity().getWlan().getDos().getHiveLevel().getFrameType().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 8 element: <configuration>.<hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<AH-DELTA-ASSISTANT> */
				if(hiveObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType().getAHDELTAASSISTANT() == null){
					hiveObj.getSecurity().getWlan().getDos().getStationLevel().getFrameType().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
			}
		}
	}
	
	private void fillConsoleTree(){
		
		/** 1 element: <console> */
		if(configureObj.getConsole() == null){
			configureObj.setConsole(new ConsoleObj());
		}
		
		/** 2 element: <console>.<serial-port> */
		if(configureObj.getConsole().getSerialPort() == null){
			configureObj.getConsole().setSerialPort(new ConsoleObj.SerialPort());
		}
		
		/** 3 element: <console>.<serial-port>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getConsole().getSerialPort().getAHDELTAASSISTANT() == null){
			configureObj.getConsole().getSerialPort().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillSystemTree(){
		
		/** 1 element: <system> */
		if(configureObj.getSystem() == null){
			configureObj.setSystem(new SystemObj());
		}
		
		/** 2 element: <system>.<temperature> */
		if(configureObj.getSystem().getTemperature() == null){
			configureObj.getSystem().setTemperature(new SystemObj.Temperature() );
		}
		
		/** 2 element: <system>.<fans> */
		if(configureObj.getSystem().getFans() == null){
			configureObj.getSystem().setFans(new SystemFans() );
		}
		
		/** 2 element: <system>.<smart-poe> */
		if(configureObj.getSystem().getSmartPoe() == null){
			configureObj.getSystem().setSmartPoe(new SystemObj.SmartPoe());
		}
		
		/** 2 element: <system>.<led> */
		if(configureObj.getSystem().getLed() == null){
			configureObj.getSystem().setLed(new SystemObj.Led());
		}
		
		/** 2 element: <system>.<icmp-redirect> */
		if(configureObj.getSystem().getIcmpRedirect() == null){
			configureObj.getSystem().setIcmpRedirect(new SystemObj.IcmpRedirect());
		}
		
		/** 2 element: <system>.<web-server> */
		if(configureObj.getSystem().getWebServer() == null){
			configureObj.getSystem().setWebServer(new SystemWebServer());
		}
		
		/** 3 element: <system>.<temperature>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getTemperature().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getTemperature().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 3 element: <system>.<fans>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getFans().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getFans().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 3 element: <system>.<smart-poe>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getSmartPoe().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getSmartPoe().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 3 element: <system>.<led>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getLed().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getLed().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 3 element: <system>.<icmp-redirect>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getIcmpRedirect().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getIcmpRedirect().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <system>.<web-server>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getSystem().getWebServer().getAHDELTAASSISTANT() == null){
			configureObj.getSystem().getWebServer().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillCacTree(){
		
		/** 1 element: <cac> */
		if(configureObj.getCac() == null){
			configureObj.setCac(new CacObj());
		}
		
		/** 2 element: <cac>.<roaming> */
		if(configureObj.getCac().getRoaming() == null){
			configureObj.getCac().setRoaming(new CacObj.Roaming());
		}
		
		/** 3 element: <cac>.<roaming>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getCac().getRoaming().getAHDELTAASSISTANT() == null){
			configureObj.getCac().getRoaming().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillAccessConsoleTree(){
		
		/** 1 element: <access-console> */
		if(configureObj.getAccessConsole() == null){
			configureObj.setAccessConsole(new AccessConsoleObj());
		}
		
		/** 2 element: <access-console>.<security> */
		if(configureObj.getAccessConsole().getSecurity() == null){
			configureObj.getAccessConsole().setSecurity(new AccessConsoleObj.Security());
		}
		
		/** 3 element: <access-console>.<security>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAccessConsole().getSecurity().getAHDELTAASSISTANT() == null){
			configureObj.getAccessConsole().getSecurity().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillCdpTree(){
		
		/** 1 element: <configuration>.<cdp> */
		if(configureObj.getCdp() == null){
			configureObj.setCdp(new CdpObj());
		}
		
		if(configureObj.getCdp().getAHDELTAASSISTANT() == null){
			configureObj.getCdp().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
	}
	
	private void fillLldpTree(){
		
		/** 1 element: <configuration>.<lldp> */
		if(configureObj.getLldp() == null){
			configureObj.setLldp(new LldpObj());
		}
		
		if(configureObj.getLldp().getAHDELTAASSISTANT() == null){
			configureObj.getLldp().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** 2 element: <configuration>.<lldp>.<cdp> */
		if(configureObj.getLldp().getCdp() == null){
			configureObj.getLldp().setCdp(new LldpObj.Cdp());
		}
		
		/** 3 element: <configuration>.<lldp>.<cdp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLldp().getCdp().getAHDELTAASSISTANT() == null){
			configureObj.getLldp().getCdp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillUserGroupTree(){
		
		/** 1 element: <configuration>.<user-group> */
		for(UserGroupObj userGroupObj : configureObj.getUserGroup()){
			
			/** 2 element: <configuration>.<user-group>.<psk-format> */
			if(userGroupObj.getPskFormat() == null){
				userGroupObj.setPskFormat(new UserGroupObj.PskFormat());
			}
			
			/** 2 element: <configuration>.<user-group>.<psk-generation-method> */
			if(userGroupObj.getPskGenerationMethod() == null){
				userGroupObj.setPskGenerationMethod(new UserGroupObj.PskGenerationMethod());
			}
			
			/** 3 element: <configuration>.<user-group>.<psk-format>.<character-pattern> */
			if(userGroupObj.getPskFormat().getCharacterPattern() == null){
				userGroupObj.getPskFormat().setCharacterPattern(new UserGroupObj.PskFormat.CharacterPattern());
			}
			
			/** 3 element: <configuration>.<user-group>.<psk-generation-method>.<username-and-password> */
			if(userGroupObj.getPskGenerationMethod().getUsernameAndPassword() == null){
				userGroupObj.getPskGenerationMethod().setUsernameAndPassword(new UserGroupObj.PskGenerationMethod.UsernameAndPassword());
			}
			
			/** 4 element: <configuration>.<user-group>.<psk-format>.<character-pattern>.<AH-DELTA-ASSISTANT> */
			if(userGroupObj.getPskFormat().getCharacterPattern().getAHDELTAASSISTANT() == null){
				userGroupObj.getPskFormat().getCharacterPattern().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
			
			/** 4 element: <configuration>.<user-group>.<psk-generation-method>.<username-and-password>.<AH-DELTA-ASSISTANT> */
			if(userGroupObj.getPskGenerationMethod().getUsernameAndPassword().getAHDELTAASSISTANT() == null){
				userGroupObj.getPskGenerationMethod().getUsernameAndPassword().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void fillAutoPskUserGroupTree(){
		
		/** 1 element: <configuration>.<auto-psk-user-group> */
		for(AutoPskUserGroupObj userGroupObj : configureObj.getAutoPskUserGroup()){
			
			/** 2 element: <configuration>.<auto-psk-user-group>.<auto-generation> */
			if(userGroupObj.getAutoGeneration() == null){
				userGroupObj.setAutoGeneration(new AutoPskUserGroupAutoGeneration());
			}
			
			/** 3 element: <configuration>.<auto-psk-user-group>.<auto-generation>.<AH-DELTA-ASSISTANT> */
			if(userGroupObj.getAutoGeneration().getAHDELTAASSISTANT() == null){
				userGroupObj.getAutoGeneration().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void fillVpnTree(){
		
		/** 1 element: <configuration>.<vpn> */
		if(configureObj.getVpn() == null){
			configureObj.setVpn(new VpnObj());
		}
		
		/** 2 element: <configuration>.<vpn>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getVpn().getAHDELTAASSISTANT() == null){
			configureObj.getVpn().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 2 element: <configuration>.<vpn>.<ipsec-tunnel> */
		if(configureObj.getVpn().getIpsecTunnel() != null){
			for(VpnIpsecTunnel vpnTunnel : configureObj.getVpn().getIpsecTunnel()){
				
				/** 3 element: <configuration>.<vpn>.<ipsec-tunnel>.<peer-ike-id> */
				if(vpnTunnel.getPeerIkeId() == null){
					vpnTunnel.setPeerIkeId(new IpsecPeerIkeId());
				}
				
				/** 4 element: <configuration>.<vpn>.<ipsec-tunnel>.<peer-ike-id>.<AH-DELTA-ASSISTANT> */
				if(vpnTunnel.getPeerIkeId().getAHDELTAASSISTANT() == null){
					vpnTunnel.getPeerIkeId().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
					);
				}
			}
		}
	}
	
	private void fillAirscreenTree(){
		
		/** 1 element: <configuration>.<airscreen> */
		if(configureObj.getAirscreen() == null){
			configureObj.setAirscreen(new AirscreenObj());
		}
		
		/** 2 element: <configuration>.<airscreen>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getAirscreen().getAHDELTAASSISTANT() == null){
			configureObj.getAirscreen().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 2 element: <configuration>.<airscreen>.<source> */
		for(AirscreenSource source : configureObj.getAirscreen().getSource()){
			
			/** 3 element: <configuration>.<airscreen>.<source>.<type> */
			if(source.getType() == null){
				source.setType(new AirscreenSourceType());
			}
			
			/** 4 element: <configuration>.<airscreen>.<source>.<type>.<client> */
			if(source.getType().getClient() == null){
				source.getType().setClient(new AirscreenSourceTypeClient());
			}
			
			/** 5 element: <configuration>.<airscreen>.<source>.<type>.<client>.<AH-DELTA-ASSISTANT> */
			if(source.getType().getClient().getAHDELTAASSISTANT() == null){
				source.getType().getClient().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
		
		/** 2 element: <configuration>.<airscreen>.<behavior> */
		for(AirscreenBehavior behavior : configureObj.getAirscreen().getBehavior()){
			
			/** 3 element: <configuration>.<airscreen>.<behavior>.<type> */
			if(behavior.getType() == null){
				behavior.setType(new AirscreenBehaviorType());
			}
			
			/** 4 element: <configuration>.<airscreen>.<behavior>.<type>.<reconnection> */
			if(behavior.getType().getReconnection() == null){
				behavior.getType().setReconnection(new AirscreenBehaviorType.Reconnection());
			}
			
			/** 5 element: <configuration>.<airscreen>.<behavior>.<type>.<reconnection>.<AH-DELTA-ASSISTANT> */
			if(behavior.getType().getReconnection().getAHDELTAASSISTANT() == null){
				behavior.getType().getReconnection().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void fillPerformanceSentinelTree(){
		
		/** 1 element: <configuration>.<performance-sentinel> */
		if(configureObj.getPerformanceSentinel() == null){
			configureObj.setPerformanceSentinel(new PerformanceSentinelObj());
		}
		
		/** 2 element: <configuration>.<performance-sentinel>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getPerformanceSentinel().getAHDELTAASSISTANT() == null){
			configureObj.getPerformanceSentinel().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillSecurityObjectTree(){
		if(configureObj.getSecurityObject() != null){
			
			for(NetworkAccessSecurityObj securityObj : configureObj.getSecurityObject()){
				
				/** 1 element: <configuration>.<security-object>.<walled-garden> */
				if(securityObj.getWalledGarden() == null){
					securityObj.setWalledGarden(new CwpWalledGarden());
				}
				
//				/** 1 element: <configuration>.<security-object>.<user-profile-deny> */
//				if(securityObj.getUserProfileDeny() == null){
//					securityObj.setUserProfileDeny(new SecurityUserProfileDeny());
//				}
				
				/** 1 element: <configuration>.<security-object>.<security> */
				if(securityObj.getSecurity() == null){
					securityObj.setSecurity(new SecurityParameters());
				}
				
				/** 2 element: <configuration>.<security-object>.<walled-garden>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getWalledGarden().getAHDELTAASSISTANT() == null){
					securityObj.getWalledGarden().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 2 element: <configuration>.<security-object>.<user-profile-deny>.<action> */
				if(securityObj.getUserProfileDeny().getAction() == null){
					securityObj.getUserProfileDeny().setAction(new UserProfileDenyAction());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<eap> */
				if(securityObj.getSecurity().getEap() == null){
					securityObj.getSecurity().setEap(new SecurityEap());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<local-cache> */
				if(securityObj.getSecurity().getLocalCache() == null){
					securityObj.getSecurity().setLocalCache(new SecurityLocalCache());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<roaming> */
				if(securityObj.getSecurity().getRoaming() == null){
					securityObj.getSecurity().setRoaming(new SsidSecurityRoaming());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<aaa> */
				if(securityObj.getSecurity().getAaa() == null){
					securityObj.getSecurity().setAaa(new SecurityAaa());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<private-psk> */
				if(securityObj.getSecurity().getPrivatePsk() == null){
					securityObj.getSecurity().setPrivatePsk(new SecurityPrivatePsk());
				}
				
				
				/** 2 element: <configuration>.<security-object>.<security>.<additional-auth-method> */
				if(securityObj.getSecurity().getAdditionalAuthMethod() == null){
					securityObj.getSecurity().setAdditionalAuthMethod(new SecurityAdditionalAuthMethod());
				}
				
				/** 2 element: <configuration>.<security-object>.<security>.<ft> */
				if(securityObj.getSecurity().getFt() == null){
					securityObj.getSecurity().setFt(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** 3 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<ban> */
				if(securityObj.getUserProfileDeny().getAction().getBan() == null){
					securityObj.getUserProfileDeny().getAction().setBan(new UserProfileDenyAction.Ban());
				}
				
				/** 3 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<ban-forever> */
				if(securityObj.getUserProfileDeny().getAction().getBanForever() == null){
					securityObj.getUserProfileDeny().getAction().setBanForever(new UpDenyBanDisconnect());
				}
				
				/** 3 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<disconnect> */
				if(securityObj.getUserProfileDeny().getAction().getDisconnect() == null){
					securityObj.getUserProfileDeny().getAction().setDisconnect(new UpDenyBanDisconnect());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<eap>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getEap().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getEap().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<local-cache>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getLocalCache().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getLocalCache().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<roaming>.<cache> */
				if(securityObj.getSecurity().getRoaming().getCache() == null){
					securityObj.getSecurity().getRoaming().setCache(new SsidRoamingCache());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server> */
				if(securityObj.getSecurity().getAaa().getRadiusServer() == null){
					securityObj.getSecurity().getAaa().setRadiusServer(new SecurityAaa.RadiusServer());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<aaa>.<user-profile-mapping> */
				if(securityObj.getSecurity().getAaa().getUserProfileMapping() == null){
					securityObj.getSecurity().getAaa().setUserProfileMapping(new AaaUserProfileMapping());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<private-psk>.<radius-auth> */
				if(securityObj.getSecurity().getPrivatePsk().getRadiusAuth() == null){
					securityObj.getSecurity().getPrivatePsk().setRadiusAuth(new PpskAuthMethod());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<mac-based-auth> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().setMacBasedAuth(new SecurityAdditionalAuthMethod.MacBasedAuth());
				}
				
				/** 3 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<mobile-device-manager> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getMobileDeviceManager() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().setMobileDeviceManager(new AuthMethodMobileDeviceManager());
				}
				
//				/** 3 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal> */
//				if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() == null){
//					securityObj.getSecurity().getAdditionalAuthMethod().setCaptiveWebPortal(new SecurityAdditionalAuthMethod.CaptiveWebPortal());
//				}
				
				/** 4 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<ban>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getUserProfileDeny().getAction().getBan().getAHDELTAASSISTANT() == null){
					securityObj.getUserProfileDeny().getAction().getBan().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<ban-forever>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getUserProfileDeny().getAction().getBanForever().getAHDELTAASSISTANT() == null){
					securityObj.getUserProfileDeny().getAction().getBanForever().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<user-profile-deny>.<action>.<disconnect>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getUserProfileDeny().getAction().getDisconnect().getAHDELTAASSISTANT() == null){
					securityObj.getUserProfileDeny().getAction().getDisconnect().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<roaming>.<cache>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getRoaming().getCache().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getRoaming().getCache().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAaa().getRadiusServer().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAaa().getRadiusServer().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<aaa>.<user-profile-mapping>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAaa().getUserProfileMapping().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAaa().getUserProfileMapping().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())	
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server>.<accounting> */
				if(securityObj.getSecurity().getAaa().getRadiusServer().getAccounting() == null){
					securityObj.getSecurity().getAaa().getRadiusServer().setAccounting(new RadiusAccountingAll());
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server>.<inject> */
				if(securityObj.getSecurity().getAaa().getRadiusServer().getInject() == null){
					securityObj.getSecurity().getAaa().getRadiusServer().setInject(new RadiusNasInject());
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<mac-based-auth>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().getMacBasedAuth().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getMobileDeviceManager().getJss() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().getMobileDeviceManager().setJss(new MobileDeviceManagerJss());
				}
				
				/** 4 element: <configuration>.<security-object>.<security>.<private-psk>.<radius-auth><AH-DELTA-ASSISTANT>. */
				if(securityObj.getSecurity().getPrivatePsk().getRadiusAuth().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getPrivatePsk().getRadiusAuth().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal() != null){
					
					/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setPassThrough(new SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough());
					}
					
					/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getInternalPages() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setInternalPages(new SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages());
					}
					
					/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getLoginPageMethod() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setLoginPageMethod(new SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod());
					}
					
					/** 4 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<walled-garden> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().setWalledGarden(new CwpWalledGarden());
					}
					
					/** 5 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through>.<AH-DELTA-ASSISTANT> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough().getAHDELTAASSISTANT() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getPassThrough().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
						);
					}
					
					/** 5 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages>.<AH-DELTA-ASSISTANT> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getInternalPages().getAHDELTAASSISTANT() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getInternalPages().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
						);
					}
					
					/** 5 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method>.<AH-DELTA-ASSISTANT> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getLoginPageMethod().getAHDELTAASSISTANT() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getLoginPageMethod().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
						);
					}
					
					/** 5 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<walled-garden>.<AH-DELTA-ASSISTANT> */
					if(securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden().getAHDELTAASSISTANT() == null){
						securityObj.getSecurity().getAdditionalAuthMethod().getCaptiveWebPortal().getWalledGarden().setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
						);
					}
					
				}
				
				/** 5 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server>.<accounting>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAaa().getRadiusServer().getAccounting().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAaa().getRadiusServer().getAccounting().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<security-object>.<security>.<aaa>.<radius-server>.<inject>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAaa().getRadiusServer().getInject().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAaa().getRadiusServer().getInject().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 5 element: <configuration>.<security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<AH-DELTA-ASSISTANT> */
				if(securityObj.getSecurity().getAdditionalAuthMethod().getMobileDeviceManager().getJss().getAHDELTAASSISTANT() == null){
					securityObj.getSecurity().getAdditionalAuthMethod().getMobileDeviceManager().getJss().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
			}
		}
	}
	
	private void fillUserAttributePolicyTree(){
		if(configureObj.getMobileDevicePolicy() != null){
			
			for(MobileDevicePolicyObj devicePolicy : configureObj.getMobileDevicePolicy()){
				
				/** 1 element: <configuration>.<mobile-device-policy>.<apply> */
				if(devicePolicy.getApply() == null){
					devicePolicy.setApply(new MobileDevicePolicyApply());
				}
				
				/** 1 element: <configuration>.<mobile-device-policy>.<client-classification> */
				if(devicePolicy.getClientClassification() == null){
					devicePolicy.setClientClassification(new MobileDevicePolicyClientClassification());
				}
				
				/** 2 element: <configuration>.<mobile-device-policy>.<apply>.<AH-DELTA-ASSISTANT> */
				if(devicePolicy.getApply().getAHDELTAASSISTANT() == null){
					devicePolicy.getApply().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** 2 element: <configuration>.<mobile-device-policy>.<client-classification>.<AH-DELTA-ASSISTANT> */
				if(devicePolicy.getClientClassification().getAHDELTAASSISTANT() == null){
					devicePolicy.getClientClassification().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
			}
		}
	}
	
	private void fillDataCollectionTree(){
		
		if(configureObj.getDataCollection() == null){
			configureObj.setDataCollection(new DataCollectionObj());
		}
		
		if(configureObj.getDataCollection().getAHDELTAASSISTANT() == null){
			configureObj.getDataCollection().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillConfigTree(){
		
		/** 1 element: <configuration>.<config> */
		if(configureObj.getConfig() == null){
			configureObj.setConfig(new ConfigObj());
		}
		
		/** 2 element: <configuration>.<config>.<rollback> */
		if(configureObj.getConfig().getRollback() == null){
			configureObj.getConfig().setRollback(new ConfigRollback());
		}
		
		/** 3 element: <configuration>.<config>.<rollback>.<enable> */
		if(configureObj.getConfig().getRollback().getEnable() == null){
			configureObj.getConfig().getRollback().setEnable(new ConfigRollbackEnable());
		}
		
		/** 4 element: <configuration>.<config>.<rollback>.<enable>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getConfig().getRollback().getEnable().getAHDELTAASSISTANT() == null){
			configureObj.getConfig().getRollback().getEnable().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillReportTree(){
		
		/** 1 element: <configuration>.<report> */
		if(configureObj.getReport() == null){
			configureObj.setReport(new ReportObj());
		}
		
		/** 2 element: <configuration>.<report>.<statistic> */
		if(configureObj.getReport().getStatistic() == null){
			configureObj.getReport().setStatistic(new ReportStatistic());
		}
		
		/** 3 element: <configuration>.<report>.<statistic>.<alarm-threshold> */
		if(configureObj.getReport().getStatistic().getAlarmThreshold() == null){
			configureObj.getReport().getStatistic().setAlarmThreshold(new ReportStatisticAlarmThreshold());
		}
		
		/** 4 element: <configuration>.<report>.<statistic>.<alarm-threshold>.<interface> */
		if(configureObj.getReport().getStatistic().getAlarmThreshold().getInterface() == null){
			configureObj.getReport().getStatistic().getAlarmThreshold().setInterface(new ReportAlarmThresholdInterface());
		}
		
		/** 4 element: <configuration>.<report>.<statistic>.<alarm-threshold>.<client> */
		if(configureObj.getReport().getStatistic().getAlarmThreshold().getClient() == null){
			configureObj.getReport().getStatistic().getAlarmThreshold().setClient(new ReportAlarmThresholdClient());
		}
		
		/** 5 element: <configuration>.<report>.<statistic>.<alarm-threshold>.<interface>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getReport().getStatistic().getAlarmThreshold().getInterface().getAHDELTAASSISTANT() == null){
			configureObj.getReport().getStatistic().getAlarmThreshold().getInterface().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<report>.<statistic>.<alarm-threshold>.<client>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getReport().getStatistic().getAlarmThreshold().getClient().getAHDELTAASSISTANT() == null){
			configureObj.getReport().getStatistic().getAlarmThreshold().getClient().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillNetworkFirewallTree(){
		if(configureObj.getNetworkFirewall() == null || configureObj.getNetworkFirewall().getName() == null){
			return;
		}
		
		for(NetworkFirewallRule rule : configureObj.getNetworkFirewall().getName()){
			if(rule.getCr() != null){
				rule.getCr().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void fillWebSecurityProxyTree(){
		
		/** 1 element: <configuration>.<web-security-proxy> */
		if(configureObj.getWebSecurityProxy() == null){
			configureObj.setWebSecurityProxy(new WebSecurityProxyObj());
		}
		
		/** 2 element: <configuration>.<websense-v1> */
		if(configureObj.getWebSecurityProxy().getWebsenseV1() == null){
			configureObj.getWebSecurityProxy().setWebsenseV1(new WebSecurityProxyWebsense());
		}
		
		/** 2 element: <configuration>.<barracuda-v1> */
		if(configureObj.getWebSecurityProxy().getBarracudaV1() == null){
			configureObj.getWebSecurityProxy().setBarracudaV1(new WebSecurityProxyBarracuda());
		}
		
		/** 3 element: <configuration>.<websense-v1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getWebSecurityProxy().getWebsenseV1().getAHDELTAASSISTANT() == null){
			configureObj.getWebSecurityProxy().getWebsenseV1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<barracuda-v1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getWebSecurityProxy().getBarracudaV1().getAHDELTAASSISTANT() == null){
			configureObj.getWebSecurityProxy().getBarracudaV1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillUsbmodemTree(){
		
		/** 1 element: <configuration>.<usbmodem> */
		if(configureObj.getUsbmodem() == null){
			configureObj.setUsbmodem(new UsbmodemObj());
		}
		
		/** 2 element: <configuration>.<usbmodem>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getUsbmodem().getAHDELTAASSISTANT() == null){
			configureObj.getUsbmodem().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<usbmodem>.<modem-id> */
		if(configureObj.getUsbmodem().getModemId() != null){
			for(ModemId modeId : configureObj.getUsbmodem().getModemId()){
				if(modeId != null){
					/** 4 element: <configuration>.<usbmodem>.<modem-id>.<AH-DELTA-ASSISTANT> */
					if(modeId.getAHDELTAASSISTANT() == null){
						modeId.setAHDELTAASSISTANT(
								CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
						);
					}
				}
			}
		}
		
		/** 3 element: <configuration>.<usbmodem>.<mode> */
		if(configureObj.getUsbmodem().getMode() == null){
			configureObj.getUsbmodem().setMode(new ModemMode());
		}
		
		/** 4 element: <configuration>.<usbmodem>.<mode>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getUsbmodem().getMode().getAHDELTAASSISTANT() == null){
			configureObj.getUsbmodem().getMode().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillRoutingTree(){
		
		/** 1 element: <configuration>.<routing> */
		if(configureObj.getRouting() == null){
			configureObj.setRouting(new RoutingObj());
		}
		
		/** 2 element: <configuration>.<routing>.<route-request> */
		if(configureObj.getRouting().getRouteRequest() == null){
			configureObj.getRouting().setRouteRequest(new RoutingRouteRequest());
		}
		
		/** 2 element: <configuration>.<routing>.<protocol> */
		if(configureObj.getRouting().getProtocol() == null){
			configureObj.getRouting().setProtocol(new RoutingProtocol());
		}
		
		/** 3 element: <configuration>.<routing>.<route-request>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRouting().getRouteRequest().getAHDELTAASSISTANT() == null){
			configureObj.getRouting().getRouteRequest().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<routing>.<protocol>.<ripv2> */
		if(configureObj.getRouting().getProtocol().getRipv2() == null){
			configureObj.getRouting().getProtocol().setRipv2(new RoutingRipv2());
		}
		
		/** 3 element: <configuration>.<routing>.<protocol>.<ospf> */
		if(configureObj.getRouting().getProtocol().getOspf() == null){
			configureObj.getRouting().getProtocol().setOspf(new RoutingOspf());
		}
		
		/** 3 element: <configuration>.<routing>.<protocol>.<bgp> */
		if(configureObj.getRouting().getProtocol().getBgp() == null){
			configureObj.getRouting().getProtocol().setBgp(new RoutingBgp());
		}
		
		/** 3 element: <configuration>.<routing>.<policy> */
		if(configureObj.getRouting().getPolicy() != null){
			
			for(RoutingPolicy policy : configureObj.getRouting().getPolicy()){
				
				/** 4 element: <configuration>.<routing>.<policy>.<from> */
				if(policy.getFrom() == null){
					policy.setFrom(new RoutingPolicyFrom());
				}
				
				/** 5 element: <configuration>.<routing>.<policy>.<from>.<AH-DELTA-ASSISTANT> */
				if(policy.getFrom().getAHDELTAASSISTANT() == null){
					policy.getFrom().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
		}
		
		/** 4 element: <configuration>.<routing>.<protocol>.<ripv2>.<advertise> */
		if(configureObj.getRouting().getProtocol().getRipv2().getAdvertise() == null){
			configureObj.getRouting().getProtocol().getRipv2().setAdvertise(new RoutingAdvertise());
		}
		
		/** 4 element: <configuration>.<routing>.<protocol>.<ospf>.<advertise> */
		if(configureObj.getRouting().getProtocol().getOspf().getAdvertise() == null){
			configureObj.getRouting().getProtocol().getOspf().setAdvertise(new RoutingAdvertise());
		}
		
		/** 4 element: <configuration>.<routing>.<protocol>.<bgp>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRouting().getProtocol().getBgp().getAHDELTAASSISTANT() == null){
			configureObj.getRouting().getProtocol().getBgp().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<routing>.<protocol>.<ripv2>.<advertise>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRouting().getProtocol().getRipv2().getAdvertise().getAHDELTAASSISTANT() == null){
			configureObj.getRouting().getProtocol().getRipv2().getAdvertise().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 5 element: <configuration>.<routing>.<protocol>.<ospf>.<advertise>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getRouting().getProtocol().getOspf().getAdvertise().getAHDELTAASSISTANT() == null){
			configureObj.getRouting().getProtocol().getOspf().getAdvertise().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillLanTree(){
		/** 1 element: <configuration>.<lan> */
		if(configureObj.getLan() == null){
			configureObj.setLan(new LanObj());
		}
		
		/** 2 element: <configuration>.<lan>.<eth1> */
		if(configureObj.getLan().getEth1() == null){
			configureObj.getLan().setEth1(new LanEthx());
		}
		
		/** 2 element: <configuration>.<lan>.<eth2> */
		if(configureObj.getLan().getEth2() == null){
			configureObj.getLan().setEth2(new LanEthx());
		}
		
		/** 2 element: <configuration>.<lan>.<eth3> */
		if(configureObj.getLan().getEth3() == null){
			configureObj.getLan().setEth3(new LanEthx());
		}
		
		/** 2 element: <configuration>.<lan>.<eth4> */
		if(configureObj.getLan().getEth4() == null){
			configureObj.getLan().setEth4(new LanEthx());
		}
		
		/** 3 element: <configuration>.<lan>.<eth1>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLan().getEth1().getAHDELTAASSISTANT() == null){
			configureObj.getLan().getEth1().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<lan>.<eth2>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLan().getEth2().getAHDELTAASSISTANT() == null){
			configureObj.getLan().getEth2().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<lan>.<eth3>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLan().getEth3().getAHDELTAASSISTANT() == null){
			configureObj.getLan().getEth3().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
		
		/** 3 element: <configuration>.<lan>.<eth4>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getLan().getEth4().getAHDELTAASSISTANT() == null){
			configureObj.getLan().getEth4().setAHDELTAASSISTANT(
					CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
			);
		}
	}
	
	private void fillTrackTree(){
	
		if(configureObj.getTrack() != null){
			for(TrackObj trackObj : configureObj.getTrack()){
				
				/** 2 element: <configuration>.<track>.<action> */
				if(trackObj.getAction() == null){
					trackObj.setAction(new TrackAction());
				}
				
				/** 3 element: <configuration>.<track>.<action>.<AH-DELTA-ASSISTANT> */
				if(trackObj.getAction().getAHDELTAASSISTANT() == null){
					trackObj.getAction().setAHDELTAASSISTANT(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
			}
		}
	}
	
	private void fillMonitorTree(){
		
		/** 1 element: <configuration>.<monitor> */
		if(configureObj.getMonitor() == null)
			configureObj.setMonitor(new MonitorObj());
		
		/** 2 element: <configuration>.<monitor>.<AH-DELTA-ASSISTANT> */
		if(configureObj.getMonitor().getAHDELTAASSISTANT() == null)
			configureObj.getMonitor().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		
		if(configureObj.getMonitor().getSession() == null)
			return;
		
		for(MonitorSession session : configureObj.getMonitor().getSession()){
			
			/** 2 element: <configuration>.<monitor>.<source> */
			if(session.getSource() == null)
				session.setSource(new MonitorSessionSource());
			
			/** 2 element: <configuration>.<monitor>.<destination> */
			if(session.getDestination() == null)
				session.setDestination(new MonitorSessionDestination());
			
			/** 3 element: <configuration>.<monitor>.<source>.<AH-DELTA-ASSISTANT> */
			if(session.getSource().getAHDELTAASSISTANT() == null)
				session.getSource().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			
			/** 3 element: <configuration>.<monitor>.<destination>.<AH-DELTA-ASSISTANT> */
			if(session.getDestination().getAHDELTAASSISTANT() == null)
				session.getDestination().setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void fillStormControlTree(){
		
		{
			/** 2 element: <configuration>.<storm-control> */
			if(configureObj.getStormControl() == null){
				configureObj.setStormControl(new StormControlObj());
			}
		}
		
		{
			/** 3 element: <configuration>.<storm-control>.<rate-limit> */
			if(configureObj.getStormControl().getRateLimit() == null){
				configureObj.getStormControl().setRateLimit(new StormControlRateLimit());
			}
		}
		
		{
			/** 4 element: <configuration>.<storm-control>.<rate-limit>.<AH-DELTA-ASSISTANT> */
			if(configureObj.getStormControl().getRateLimit().getAHDELTAASSISTANT() == null){
				configureObj.getStormControl().getRateLimit().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void fillSpanningTree(){
		
		{
			/** 2 element: <configuration>.<spanning-tree> */
			if(configureObj.getSpanningTree() == null){
				configureObj.setSpanningTree(new SpanningTreeObj());
			}
		}
		
		{
			/** 3 element: <configuration>.<spanning-tree>.<AH-DELTA-ASSISTANT> */
			if(configureObj.getSpanningTree().getAHDELTAASSISTANT() == null){
				configureObj.getSpanningTree().setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
}
