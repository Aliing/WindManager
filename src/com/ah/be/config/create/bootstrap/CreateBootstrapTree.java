package com.ah.be.config.create.bootstrap;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.xml.be.config.*;

public class CreateBootstrapTree {

	private final BootstrapInt bootstrapImpl;
	private final Configuration bootstrapConfig;
	private final List<Object> bootstrapChildList_1 = new ArrayList<Object>();
	private final List<Object> bootstrapChildList_2 = new ArrayList<Object>();
	private final List<Object> bootstrapChildList_3 = new ArrayList<Object>();
	private final List<Object> bootstrapChildList_4 = new ArrayList<Object>();

	public CreateBootstrapTree(BootstrapInt bootstrapImpl) throws Exception {
		this.bootstrapImpl = bootstrapImpl;
		bootstrapConfig = new Configuration();
		generateBootstrapLevel_1();
	}

	public Configuration getBootStrapConfig() {
		return bootstrapConfig;
	}

	private void generateBootstrapLevel_1() throws Exception {
		/**
		 * <configuration> Configuration
		 */

		/** element: <configuration>.<hostname> */
		Object[][] hostNameParm = {
				{ CLICommonFunc.ATTRIBUTE_VALUE,
						bootstrapImpl.getHiveApHostName() },
				{ CLICommonFunc.ATTRIBUTE_OPERATION,
						CLICommonFunc.getYesDefault() } };
		bootstrapConfig.setHostname((HostnameObj) CLICommonFunc
				.createObjectWithName(HostnameObj.class, hostNameParm));

		/** element: <configuration>.<hive> */
		if (bootstrapImpl.isConfigureHiveProfile()) {
			HiveObj hiveObj = new HiveObj();
			bootstrapChildList_1.add(hiveObj);
			bootstrapConfig.getHive().add(hiveObj);
		}

		/** element: <configuration>.<interface> */
		InterfaceObj interfaceObj = new InterfaceObj();
		bootstrapChildList_1.add(interfaceObj);
		bootstrapConfig.setInterface(interfaceObj);

		/** element: <configuration>.<snmp> */
		if(bootstrapImpl.isConfigSnmp()){
			SnmpObj snmpObj = new SnmpObj();
			bootstrapChildList_1.add(snmpObj);
			bootstrapConfig.setSnmp(snmpObj);
		}
		
		/** element: <configuration>.<admin> */
		AdminObj adminObj = new AdminObj();
		bootstrapChildList_1.add(adminObj);
		bootstrapConfig.setAdmin(adminObj);

		/** element: <configuration>.<capwap> */
		CapwapObj capWapObj = new CapwapObj();
		bootstrapChildList_1.add(capWapObj);
		bootstrapConfig.setCapwap(capWapObj);
		
		/** element: <configuration>.<boot-param> */
		if (bootstrapImpl.isConfigBootParam()) {
			BootParamObj bootParam = new BootParamObj();
			bootstrapChildList_1.add(bootParam);
			bootstrapConfig.setBootParam(bootParam);
		}

		generateBootstrapLevel_2();
	}

	private void generateBootstrapLevel_2() throws Exception {
		/**
		 * <configuration>.<hive> HiveObj <configuration>.<interface>
		 * InterfaceObj <configuration>.<snmp> SnmpObj <configuration>.<admin>
		 * AdminObj <configuration>.<capwap> CapwapObj
		 * <configuration>.<boot-param>				BootParamObj
		 */
		for (Object childObj : bootstrapChildList_1) {
			/** element: <configuration>.<hive> */
			if (childObj instanceof HiveObj) {
				HiveObj hiveObj = (HiveObj) childObj;

				/** attribute: name */
				hiveObj.setName(bootstrapImpl.getHiveId());

				/** attribute: operation */
				hiveObj.setOperation(CLICommonFunc
						.getAhEnumActValue(CLICommonFunc.getYesDefault()));

				/** element: <configuration>.<hive>.<cr> */
				hiveObj.setCr("");

				/** element: <configuration>.<hive>.<native-vlan> */
				Object[][] nativeVlanParm = {
						{ CLICommonFunc.ATTRIBUTE_VALUE,
								bootstrapImpl.getHiveApNativeVlanId() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				hiveObj.setNativeVlan((HiveObj.NativeVlan) CLICommonFunc
						.createObjectWithName(HiveObj.NativeVlan.class,
								nativeVlanParm));

				/** element: <configuration>.<hive>.<password> */
				if (bootstrapImpl.isConfigureHivePassword()) {
					hiveObj.setPassword(CLICommonFunc
							.createAhEncryptedStringAct(bootstrapImpl
									.getHivePassword(), CLICommonFunc
									.getYesDefault()));
				}
			}

			/** element: <configuration>.<interface> */
			if (childObj instanceof InterfaceObj) {
				InterfaceObj interfaceObj = (InterfaceObj) childObj;

				/** element: <configuration>.<interface>.<mgt0> */
				Mgt mgt0 = new Mgt();
				bootstrapChildList_2.add(mgt0);
				interfaceObj.setMgt0(mgt0);
			}

			/** element: <configuration>.<snmp> */
			if (childObj instanceof SnmpObj) {
				SnmpObj snmpObj = (SnmpObj) childObj;

				/** element: <configuration>.<location> */
				Object[][] locationParm = {
						{ CLICommonFunc.ATTRIBUTE_VALUE,
								bootstrapImpl.getSnmpLocation() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				snmpObj.setLocation((SnmpObj.Location) CLICommonFunc
						.createObjectWithName(SnmpObj.Location.class,
								locationParm));
			}

			/** element: <configuration>.<admin> */
			if (childObj instanceof AdminObj) {
				AdminObj adminObj = (AdminObj) childObj;

				/** element: <configuration>.<admin>.<root-admin> */
				if (bootstrapImpl.isConfigureRootAdmin()) {
					AdminObj.RootAdmin rootAdminObj = new AdminObj.RootAdmin();
					bootstrapChildList_2.add(rootAdminObj);
					adminObj.setRootAdmin(rootAdminObj);
				}
			}

			/** element: <configuration>.<capwap> */
			if (childObj instanceof CapwapObj) {
				CapwapObj capwapObj = (CapwapObj) childObj;

				/** element: <configuration>.<capwap>.<client> */
				CapwapObj.Client clientObj = new CapwapObj.Client();
				bootstrapChildList_2.add(clientObj);
				capwapObj.setClient(clientObj);
			}
			
			/** element: <configuration>.<boot-param> */
			if (childObj instanceof BootParamObj){
				BootParamObj bootParam = (BootParamObj)childObj;
				
				/** attribute: operation */
				bootParam.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <configuration>.<boot-param>.<server> */
				bootParam.setServer(CLICommonFunc.createAhStringActObj(
						bootstrapImpl.getNetdumpServer(), CLICommonFunc.getYesDefault()));
				
				/** element: <configuration>.<boot-param>.<netdump> */
				BootParamNetdump netDumpObj = new BootParamNetdump();
				bootstrapChildList_2.add(netDumpObj);
				bootParam.setNetdump(netDumpObj);
			}
		}

		generateBootstrapLevel_3();
	}

	private void generateBootstrapLevel_3() throws Exception {
		/**
		 * <configuration>.<interface>.<mgt0> Mgt <configuration>.<admin>.<root-admin>
		 * AdminObj.RootAdmin <configuration>.<capwap>.<client>
		 * CapwapObj.Client
		 * <configuration>.<boot-param>.<netdump>			BootParamNetdump
		 */
		for (Object childObj : bootstrapChildList_2) {
			/** element: <configuration>.<interface>.<mgt0> */
			if (childObj instanceof Mgt) {
				Mgt mgt0 = (Mgt) childObj;

				/** element: <configuration>.<interface>.<mgt0>.<hive> */
				mgt0.setHive(CLICommonFunc.createAhNameActObj(bootstrapImpl
						.getHiveId(), CLICommonFunc.getYesDefault()));

				/** element: <configuration>.<interface>.<mgt0>.<vlan> */
				if (bootstrapImpl.isConfigureMgtVlan()) {
					Object[][] vlanParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE,
									bootstrapImpl.getHiveApMgtVlanId() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION,
									CLICommonFunc.getYesDefault() } };
					mgt0.setVlan((InterfaceVlan) CLICommonFunc.createObjectWithName(
							InterfaceVlan.class, vlanParm));
				}

				/** element: <interface>.<mgt0>.<native-vlan> */
				Object[][] nativeVlanParm = {
						{ CLICommonFunc.ATTRIBUTE_VALUE,
								bootstrapImpl.getHiveApNativeVlanId() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				mgt0.setNativeVlan((Mgt.NativeVlan) CLICommonFunc
						.createObjectWithName(Mgt.NativeVlan.class,
								nativeVlanParm));
			}

			/** element: <configuration>.<admin>.<root-admin> */
			if (childObj instanceof AdminObj.RootAdmin) {
				AdminObj.RootAdmin rootAdminObj = (AdminObj.RootAdmin) childObj;

				/** attribute: value */
				rootAdminObj.setValue(bootstrapImpl.getRootAdminUser());

				/** element: <configuration>.<admin>.<root-admin>.<password> */
				if (bootstrapImpl.isConfigureRootPassWord()) {
					Object[][] passwordParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE,
									bootstrapImpl.getRootAdminPassword() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION,
									CLICommonFunc.getYesDefault() } };
					rootAdminObj
							.setPassword((AdminObj.RootAdmin.Password) CLICommonFunc
									.createObjectWithName(
											AdminObj.RootAdmin.Password.class,
											passwordParm));
				}
			}

			/** element: <configuration>.<capwap>.<client> */
			if (childObj instanceof CapwapObj.Client) {
				CapwapObj.Client clientObj = (CapwapObj.Client) childObj;

				/** element: <configuration>.<capwap>.<client>.<dtls> */
				CapwapObj.Client.Dtls dtlsObj = new CapwapObj.Client.Dtls();
				bootstrapChildList_3.add(dtlsObj);
				clientObj.setDtls(dtlsObj);

				/** element: <configuration>.<capwap>.<client>.<neighbor> */
				CapwapObj.Client.Neighbor neighborObj = new CapwapObj.Client.Neighbor();
				bootstrapChildList_3.add(neighborObj);
				clientObj.setNeighbor(neighborObj);

				/** element: <configuration>.<capwap>.<client>.<server> */
				CapwapObj.Client.Server serverObj = new CapwapObj.Client.Server();
				bootstrapChildList_3.add(serverObj);
				clientObj.setServer(serverObj);
				
				/** element: <configuration>.<capwap>.<client>.<vhm-name> */
				clientObj.setVhmName(CLICommonFunc.createAhStringActObj(bootstrapImpl.getVhmName(), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <configuration>.<boot-param>.<netdump> */
			if(childObj instanceof BootParamNetdump){
				BootParamNetdump netdumpObj = (BootParamNetdump)childObj;
				
				/** element: <configuration>.<boot-param>.<netdump>.<AH-DELTA-ASSISTANT> */
				netdumpObj.setAHDELTAASSISTANT(
						CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
				);
				
				/** element: <configuration>.<boot-param>.<netdump>.<dump-file> */
				netdumpObj.setDumpFile(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				
				/** element: <configuration>.<boot-param>.<netdump>.<enable> */
				netdumpObj.setEnable(CLICommonFunc.getAhOnlyAct(bootstrapImpl.isEnableNetdump()));
			}
		}

		generateBootstrapLevel_4();
	}

	private void generateBootstrapLevel_4() throws Exception {
		/**
		 * <configuration>.<capwap>.<client>.<dtls> CapwapObj.Client.Dtls
		 * <configuration>.<capwap>.<client>.<neighbor>
		 * CapwapObj.Client.Neighbor <configuration>.<capwap>.<client>.<server>
		 * CapwapObj.Client.Server
		 */
		for (Object childObj : bootstrapChildList_3) {
			/** element: <configuration>.<capwap>.<client>.<dtls> */
			if (childObj instanceof CapwapObj.Client.Dtls) {
				CapwapObj.Client.Dtls dtlsObj = (CapwapObj.Client.Dtls) childObj;

				/** element: <configuration>.<capwap>.<client>.<dtls>.<enable> */
				CapwapObj.Client.Dtls.Enable enableObj = new CapwapObj.Client.Dtls.Enable();
				bootstrapChildList_4.add(enableObj);
				dtlsObj.setEnable(enableObj);

				/** element: <configuration>.<capwap>.<client>.<dtls>.<bootstrap-passphrase> */
				if (bootstrapImpl.isConfigCwpDtlsBootPassPhrase()) {
					dtlsObj.setBootstrapPassphrase(CLICommonFunc
							.createAhEncryptedStringAct(bootstrapImpl
									.getCwpDtlsBootPassPhrase(), CLICommonFunc
									.getYesDefault()));
				}
				
				/** element: <configuration>.<capwap>.<client>.<dtls>.<negotiation> */
				CapwapObj.Client.Dtls.Negotiation negotiationObj = new CapwapObj.Client.Dtls.Negotiation();
				bootstrapChildList_4.add(negotiationObj);
				dtlsObj.setNegotiation(negotiationObj);
			}

			/** element: <configuration>.<capwap>.<client>.<neighbor> */
			if (childObj instanceof CapwapObj.Client.Neighbor) {
				CapwapObj.Client.Neighbor neighborObj = (CapwapObj.Client.Neighbor) childObj;

				/** element: <configuration>.<capwap>.<client>.<neighbor>.<heartbeat> */
				CapwapObj.Client.Neighbor.Heartbeat heartbeatObj = new CapwapObj.Client.Neighbor.Heartbeat();
				bootstrapChildList_4.add(heartbeatObj);
				neighborObj.setHeartbeat(heartbeatObj);

				/** element: <configuration>.<capwap>.<client>.<neighbor>.<dead> */
				CapwapObj.Client.Neighbor.Dead deadObj = new CapwapObj.Client.Neighbor.Dead();
				bootstrapChildList_4.add(deadObj);
				neighborObj.setDead(deadObj);
			}

			/** element: <configuration>.<capwap>.<client>.<server> */
			if (childObj instanceof CapwapObj.Client.Server) {
				CapwapObj.Client.Server serverObj = (CapwapObj.Client.Server) childObj;
				
				/** element: <capwap>.<client>.<server>.<primary> */
				if(bootstrapImpl.isConfigCwpServerPrimary()){
					serverObj.setPrimary(this.createCapwapServer(bootstrapImpl.getCwpServerName()));
				}
				
				/** element: <capwap>.<client>.<server>.<backup> */
				if(bootstrapImpl.isConfigCwpServerSecond()){
					serverObj.setBackup(this.createCapwapServer(bootstrapImpl.getCwpServerNameSecond()));
				}
				
				/** element: <capwap>.<client>.<server>.<cr-primary> */
				if(bootstrapImpl.isConfigCwpServerPrimary()){
					serverObj.setCrPrimary(this.createCapwapServer(bootstrapImpl.getCwpServerName()));
				}
				
				/** element: <capwap>.<client>.<server>.<cr-backup> */
				if(bootstrapImpl.isConfigCwpServerSecond()){
					serverObj.setCrBackup(this.createCapwapServer(bootstrapImpl.getCwpServerNameSecond()));
				}

				/** element: <configuration>.<capwap>.<client>.<server>.<port> */
				CapwapObj.Client.Server.Port portObj = new CapwapObj.Client.Server.Port();
				bootstrapChildList_4.add(portObj);
				serverObj.setPort(portObj);
			}
		}

		generateBootstrapLevel_5();
	}

	private void generateBootstrapLevel_5() throws Exception {
		/**
		 * <configuration>.<capwap>.<client>.<neighbor>.<heartbeat>
		 * CapwapObj.Client.Neighbor.Heartbeat <configuration>.<capwap>.<client>.<server>.<port>
		 * CapwapObj.Client.Server.Port <configuration>.<capwap>.<client>.<neighbor>.<dead>
		 * CapwapObj.Client.Neighbor.Dead
		 * <configuration>.<capwap>.<client>.<dtls>.<enable>		CapwapObj.Client.Dtls.Enable
		 * <configuration>.<capwap>.<client>.<dtls>.<negotiation>		CapwapObj.Client.Dtls.Negotiation
		 */
		for (Object childObj : bootstrapChildList_4) {
			/** element: <configuration>.<capwap>.<client>.<neighbor>.<heartbeat> */
			if (childObj instanceof CapwapObj.Client.Neighbor.Heartbeat) {
				CapwapObj.Client.Neighbor.Heartbeat heartbeatObj = (CapwapObj.Client.Neighbor.Heartbeat) childObj;

				/** element: <configuration>.<capwap>.<client>.<neighbor>.<heartbeat>.<interval> */
				Object[][] intervlParm = {
						{ CLICommonFunc.ATTRIBUTE_NAME,
								bootstrapImpl.getCwpHeartbeatInterval() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				heartbeatObj
						.setInterval((CapwapObj.Client.Neighbor.Heartbeat.Interval) CLICommonFunc
								.createObjectWithName(
										CapwapObj.Client.Neighbor.Heartbeat.Interval.class,
										intervlParm));
			}

			/** element: <configuration>.<capwap>.<client>.<server>.<port> */
			if (childObj instanceof CapwapObj.Client.Server.Port) {
				CapwapObj.Client.Server.Port portObj = (CapwapObj.Client.Server.Port) childObj;

				/** attribute: value */
				portObj.setValue(bootstrapImpl.getCwpServerPort());

				/** attribute: operation */
				portObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc
						.getYesDefault()));

				/** element: <configuration>.<capwap>.<client>.<server>.<port>.<no-disconnect> */
				portObj.setNoDisconnect(CLICommonFunc
						.getAhOnlyAct(CLICommonFunc.getYesDefault()));
			}

			/** element: <configuration>.<capwap>.<client>.<neighbor>.<dead> */
			if (childObj instanceof CapwapObj.Client.Neighbor.Dead) {
				CapwapObj.Client.Neighbor.Dead deadObj = (CapwapObj.Client.Neighbor.Dead) childObj;

				/** element: <configuration>.<capwap>.<client>.<neighbor>.<dead>.<interval> */
				Object[][] deadInterParm = {
						{ CLICommonFunc.ATTRIBUTE_VALUE,
								bootstrapImpl.getCwpDeadInterval() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				deadObj
						.setInterval((CapwapObj.Client.Neighbor.Dead.Interval) CLICommonFunc
								.createObjectWithName(
										CapwapObj.Client.Neighbor.Dead.Interval.class,
										deadInterParm));
			}
			
			/** element: <configuration>.<capwap>.<client>.<dtls>.<enable> */
			if(childObj instanceof CapwapObj.Client.Dtls.Enable){
				CapwapObj.Client.Dtls.Enable enableObj = (CapwapObj.Client.Dtls.Enable)childObj;
				
				/** element: <configuration>.<capwap>.<client>.<dtls>.<enable>.<no-disconnect> */
				enableObj.setNoDisconnect(CLICommonFunc.getAhOnlyAct(bootstrapImpl.isEnableCwpDtls()));
			}
			
			/** element: <configuration>.<capwap>.<client>.<dtls>.<negotiation> */
			if(childObj instanceof CapwapObj.Client.Dtls.Negotiation){
				CapwapObj.Client.Dtls.Negotiation negotiationObj = (CapwapObj.Client.Dtls.Negotiation)childObj;
				
				/** element: <configuration>.<capwap>.<client>.<dtls>.<negotiation>.<enable> */
				negotiationObj.setEnable(CLICommonFunc.getAhOnlyAct(false));
			}
		}
	}
	
	private CapwapServer createCapwapServer(String ipAddr){
		CapwapServer server = new CapwapServer();
		CapwapServerName nameObj = new CapwapServerName();
		
		/** attribute: value */
		nameObj.setValue(ipAddr);
		
		/** attribute: operation */
		nameObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <no-disconnect> */
		nameObj.setNoDisconnect("");
		
		server.setName(nameObj);
		
		return server;
	}

}