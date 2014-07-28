package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.SnmpProfileInt;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateSnmpProfileTree {
	
	private SnmpProfileInt snmpImpl;
	private SnmpObj snmpObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> snmpChildList_1 = new ArrayList<Object>();
	private List<Object> snmpChildList_2 = new ArrayList<Object>();
	private List<Object> snmpChildList_3 = new ArrayList<Object>();
	
	private List<Object> snmpAdmin_1 = new ArrayList<Object>();
	private List<Object> snmpAdmin_2 = new ArrayList<Object>();
	private List<Object> snmpAdmin_3 = new ArrayList<Object>();

	public CreateSnmpProfileTree(SnmpProfileInt snmpImpl, GenerateXMLDebug oDebug) {
		this.snmpImpl = snmpImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(snmpImpl.isConfigSnmpLocation() || snmpImpl.isConfigureSnmp()){
			snmpObj = new SnmpObj();
			generateSnmpLevel_1();
		}
	}
	
	public SnmpObj getSnmpObj(){
		return snmpObj;
	}
	
	private void generateSnmpLevel_1() throws Exception{
		/**
		 * <snmp>	SnmpObj
		 */
		
		/** attribute: updateTime */
		snmpObj.setUpdateTime(snmpImpl.getUpdateTime());
		
		/** element: <snmp>.<location> */
		oDebug.debug("/configuration/snmp",
				"location", GenerateXMLDebug.CONFIG_ELEMENT,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		if(snmpImpl.isConfigSnmpLocation()){
			
			oDebug.debug("/configuration/snmp",
					"location", GenerateXMLDebug.SET_VALUE,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			Object[][] locationParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, snmpImpl.getSnmpLocation()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			snmpObj.setLocation(
					(SnmpObj.Location)CLICommonFunc.createObjectWithName(SnmpObj.Location.class, locationParm)
			);
		}
		
		if(snmpImpl.isConfigureSnmp()){
			
			/** element: <snmp>.<contact> */
			oDebug.debug("/configuration/snmp",
					"contact", GenerateXMLDebug.CONFIG_ELEMENT,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			if(snmpImpl.isConfigureSnmpContact()){
				
				oDebug.debug("/configuration/snmp",
						"contact", GenerateXMLDebug.SET_VALUE,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				Object[][] contactParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, snmpImpl.getSnmpContact()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				snmpObj.setContact(
						(SnmpObj.Contact)CLICommonFunc.createObjectWithName(SnmpObj.Contact.class, contactParm)
				);
			}
			
			/** element: <snmp>.<reader> */
			oDebug.debug("/configuration/snmp",
					"reader", GenerateXMLDebug.CONFIG_ELEMENT,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			if(snmpImpl.isConfigureSnmpReader()){
				SnmpObj.Reader readerObj = new SnmpObj.Reader();
				snmpChildList_1.add(readerObj);
				snmpObj.setReader(readerObj);
			}
			
			/** element: <snmp>.<trap-host> */
			oDebug.debug("/configuration/snmp",
					"trap-host", GenerateXMLDebug.CONFIG_ELEMENT,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			if(snmpImpl.isConfigureSnmpTrapHost()){
				SnmpObj.TrapHost trapHostObj = new SnmpObj.TrapHost();
				snmpChildList_1.add(trapHostObj);
				snmpObj.setTrapHost(trapHostObj);
			}
			
			/** element: <snmp>.<trap-info> */
			SnmpObj.TrapInfo trapInfoObj = new SnmpObj.TrapInfo();
			snmpChildList_1.add(trapInfoObj);
			snmpObj.setTrapInfo(trapInfoObj);
			
		}
		
			
		
		generateSnmpLevel_2();
	}
	
	private void generateSnmpLevel_2() throws Exception{
		/**
		 * <snmp>.<reader>		SnmpObj.Reader
		 * <snmp>.<trap-host>	SnmpObj.TrapHost
		 * <snmp>.<trap-info>	SnmpObj.TrapInfo
		 */
		for(Object childObj : snmpChildList_1){
			
			/** element: <snmp>.<reader> */
			if(childObj instanceof SnmpObj.Reader){
				SnmpObj.Reader readerObj = (SnmpObj.Reader)childObj;
				
				/** element: <snmp>.<reader>.<version> */
				SnmpObj.Reader.Version versionObj = new SnmpObj.Reader.Version();
				snmpChildList_2.add(versionObj);
				readerObj.setVersion(versionObj);
			}
			
			/** element: <snmp>.<trap-host> */
			if(childObj instanceof SnmpObj.TrapHost){
				SnmpObj.TrapHost trapHostObj = (SnmpObj.TrapHost)childObj;
				
				/** element: <snmp>.<trap-host>.<v1> */
				oDebug.debug("/configuration/snmp/trap-host",
						"v1", GenerateXMLDebug.CONFIG_ELEMENT,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				for(int i=0; i<snmpImpl.getSnmpTrapHostV1Size(); i++){
					trapHostObj.getV1().add(createSnmpTrapVersionV1(i));
				}
				
				/** element: <snmp>.<trap-host>.<v2c> */
				oDebug.debug("/configuration/snmp/trap-host",
						"v2c", GenerateXMLDebug.CONFIG_ELEMENT,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				for(int i=0; i<snmpImpl.getSnmpTrapHostV2Size(); i++){
					trapHostObj.getV2C().add(createSnmpTrapVersionV2(i));
				}
				
				/** element: <snmp>.<trap-host>.<v3> */
				SnmpTrapHostV3 v3Obj = new SnmpTrapHostV3();
				snmpChildList_2.add(v3Obj);
				trapHostObj.setV3(v3Obj);
			}
			
			/** element: <snmp>.<trap-info> */
			if(childObj instanceof SnmpObj.TrapInfo){
				SnmpObj.TrapInfo trapInfoObj = (SnmpObj.TrapInfo)childObj;
				
				/** element: <snmp>.<trap-info>.<over-capwap> */
				oDebug.debug("/configuration/snmp/trap-info",
						"over-capwap", GenerateXMLDebug.SET_OPERATION,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				trapInfoObj.setOverCapwap(CLICommonFunc.getAhOnlyAct(snmpImpl.isEnableOverCapwap()));
				
				/** element: <snmp>.<trap-info>.<over-snmp> */
				oDebug.debug("/configuration/snmp/trap-info",
						"over-snmp", GenerateXMLDebug.SET_OPERATION,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				trapInfoObj.setOverSnmp(CLICommonFunc.getAhOnlyAct(snmpImpl.isEnableOverSnmp()));
				
			}
		}
		snmpChildList_1.clear();
		generateSnmpLevel_3();
	}
	
	private void generateSnmpLevel_3() throws Exception{
		/**
		 * <snmp>.<reader>.<version>		SnmpObj.Reader.Version
		 * <snmp>.<trap-host>.<v3>			SnmpTrapHostV3
		 */
		for(Object childObj : snmpChildList_2){
			
			/** element: <snmp>.<reader>.<version> */
			if(childObj instanceof SnmpObj.Reader.Version){
				SnmpObj.Reader.Version versionObj = (SnmpObj.Reader.Version)childObj;
				
				/** element: <snmp>.<reader>.<version>.<v1> */
				oDebug.debug("/configuration/snmp/reader/version",
						"v1", GenerateXMLDebug.CONFIG_ELEMENT,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				if(snmpImpl.isConfigureReaderV1()){
					versionObj.setV1(createSnmpReaderV1());
				}
				
				/** element: <snmp>.<reader>.<version>.<v2C> */
				oDebug.debug("/configuration/snmp/reader/version",
						"v2c", GenerateXMLDebug.CONFIG_ELEMENT,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				if(snmpImpl.isConfigureReaderV2()){
					versionObj.setV2C(createSnmpReaderV2());
				}
				
				/** element: <snmp>.<reader>.<version>.<v3> */
				if(snmpImpl.isConfigReaderV3()){
					SnmpReaderV3 readerV3 = new SnmpReaderV3();
					snmpChildList_3.add(readerV3);
					versionObj.setV3(readerV3);
				}
				
				/** element: <snmp>.<reader>.<version>.<any> */
				oDebug.debug("/configuration/snmp/reader/version",
						"any", GenerateXMLDebug.CONFIG_ELEMENT,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				if(snmpImpl.isConfigureReaderAny()){
					versionObj.setV1(createSnmpReaderAny());
				}
			}
			
			/** element: <snmp>.<trap-host>.<v3> */
			if(childObj instanceof SnmpTrapHostV3){
				SnmpTrapHostV3 v3Obj = (SnmpTrapHostV3)childObj;
				
				/** element: <snmp>.<trap-host>.<v3>.<admin> */
				for(int index=0; index<snmpImpl.getV3TrapAdminSize(); index++){
					v3Obj.getAdmin().add(this.createSnmpV3Admin(index, SnmpProfileInt.EnumOperation.trap_host));
				}
				
				/** element: <snmp>.<trap-host>.<v3>.<cr> */
				for(int index=0; index<snmpImpl.getV3TrapSize(); index++){
					v3Obj.getCr().add(this.createTrapHostV3Host(index, SnmpProfileInt.EnumOperation.trap_host));
				}
			}
		}
		snmpChildList_2.clear();
		generateSnmpLevel_4();
	}
	
	private void generateSnmpLevel_4() throws Exception{
		/**
		 * <snmp>.<reader>.<version>.<v3>			SnmpReaderV3
		 */
		for(Object childObj : snmpChildList_3){
			
			/** element: <snmp>.<reader>.<version>.<v3> */
			if(childObj instanceof SnmpReaderV3){
				SnmpReaderV3 readerV3 = (SnmpReaderV3)childObj;
				
				/** element: <snmp>.<reader>.<version>.<v3>.<admin> */
				for(int i=0; i < snmpImpl.getReaderV3Size(); i++){
					readerV3.getAdmin().add(this.createSnmpV3Admin(i, SnmpProfileInt.EnumOperation.reader));
				}
			}
		}
		snmpChildList_3.clear();
	}
	
	private SnmpV3Admin createSnmpV3Admin(int index, SnmpProfileInt.EnumOperation optEnum) throws Exception{
		SnmpV3Admin adminObj = new SnmpV3Admin();
		
		/** attribute: operation */
		adminObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		adminObj.setName(snmpImpl.getAdminName(index, optEnum));
		
		/** element: <auth> */
		SnmpV3Auth auth = new SnmpV3Auth();
		snmpAdmin_1.add(auth);
		adminObj.setAuth(auth);
		
		generateSnmpV3Admin_1(index, optEnum);
		return adminObj;
	}
	
	private TrapHostV3Host createTrapHostV3Host(int index, SnmpProfileInt.EnumOperation optEnum) throws Exception{
		TrapHostV3Host v3Host = new TrapHostV3Host();
		
		/** attribute: name */
		v3Host.setName(snmpImpl.getV3TrapHostName(index));
		
		/** attribute: operation */
		v3Host.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <via-vpn-tunnel> */
		TrapHostV3ViaVpnTunnel viaVpnTunnel = new TrapHostV3ViaVpnTunnel();
		snmpAdmin_1.add(viaVpnTunnel);
		v3Host.setViaVpnTunnel(viaVpnTunnel);
		
		generateSnmpV3Admin_1(index, optEnum);
		
		return v3Host;
	}
	
	private void generateSnmpV3Admin_1(int index, SnmpProfileInt.EnumOperation optEnum) throws Exception{
		/**
		 * <auth>			SnmpV3Auth
		 * <via-vpn-tunnel>		TrapHostV3ViaVpnTunnel
		 */
		for(Object childObj : snmpAdmin_1){
			
			/** element: <auth> */
			if(childObj instanceof SnmpV3Auth){
				SnmpV3Auth authObj = (SnmpV3Auth)childObj;
				
				/** attribute: value */
				authObj.setValue(snmpImpl.getAuthValue(index, optEnum));
				
				/** element: <auth>.<password> */
				SnmpV3AuthPassword password = new SnmpV3AuthPassword();
				snmpAdmin_2.add(password);
				authObj.setPassword(password);
			}
			
			/** element: <via-vpn-tunnel> */
			if(childObj instanceof TrapHostV3ViaVpnTunnel){
				TrapHostV3ViaVpnTunnel vpnTunnelObj = (TrapHostV3ViaVpnTunnel)childObj;
				
				/** attribute: value */
				vpnTunnelObj.setValue(snmpImpl.getV3TrapHostVpnTunnel(index));
				
				/** element: <via-vpn-tunnel>.<admin> */
				vpnTunnelObj.setAdmin(CLICommonFunc.createAhName(snmpImpl.getV3TrapHostAdmin(index)));
			}
		}
		snmpAdmin_1.clear();
		generateSnmpV3Admin_2(index, optEnum);
	}
	
	private void generateSnmpV3Admin_2(int index, SnmpProfileInt.EnumOperation optEnum) throws Exception{
		/**
		 * <auth>.<password>				SnmpV3AuthPassword
		 */
		for(Object childObj : snmpAdmin_2){
			
			/** element: <auth>.<password> */
			if(childObj instanceof SnmpV3AuthPassword){
				SnmpV3AuthPassword authPas = (SnmpV3AuthPassword)childObj;
				
				/** attribute: value */
				authPas.setValue(AhConfigUtil.hiveApCommonEncrypt(snmpImpl.getAuthPas(index, optEnum)));
				
				/** attribute: encrypted */
				authPas.setEncrypted(1);
				
				/** element: <auth>.<password>.<encryption> */
//				if(snmpImpl.isConfigEncryption(index, optEnum)){
				SnmpV3Encryption encryption = new SnmpV3Encryption();
				snmpAdmin_3.add(encryption);
				authPas.setEncryption(encryption);
//				}
			}
		}
		snmpAdmin_2.clear();
		generateSnmpV3Admin_3(index, optEnum);
	}
	
	private void generateSnmpV3Admin_3(int index, SnmpProfileInt.EnumOperation optEnum) throws Exception{
		/**
		 * <auth>.<password>.<encryption>				SnmpV3Encryption
		 */
		for(Object childObj : snmpAdmin_3){
			
			/** element: <auth>.<password>.<encryption> */
			if(childObj instanceof SnmpV3Encryption){
				SnmpV3Encryption encryption = (SnmpV3Encryption)childObj;
				
				/** attribute: value */
				encryption.setValue(snmpImpl.getEncryption(index, optEnum));
				
				/** element: <auth>.<password>.<encryption>.<password> */
				encryption.setPassword(CLICommonFunc.createAhEncryptedString(snmpImpl.getEncryptionPas(index, optEnum)));
			}
		}
		snmpAdmin_3.clear();
	}
	
	private SnmpTrapVersion createSnmpTrapVersionV1(int index) throws CreateXMLException{
		SnmpTrapVersion snmpTrapV1 = new SnmpTrapVersion();
		
		/** attribute: name */
		oDebug.debug("/configuration/snmp/trap-host",
				"v1", GenerateXMLDebug.SET_NAME,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		snmpTrapV1.setName(snmpImpl.getSnmpTrapV1Address(index));
		
		/** attribute: operation */
		snmpTrapV1.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <community> */
		oDebug.debug("/configuration/snmp/trap-host/v1[@name='"+snmpTrapV1.getName()+"']",
				"community", GenerateXMLDebug.CONFIG_ELEMENT,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		if(snmpImpl.isConfigSnmpTrapV1Community(index)){
			
			oDebug.debug("/configuration/snmp/trap-host/v1[@name='"+snmpTrapV1.getName()+"']",
					"community", GenerateXMLDebug.SET_VALUE,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			snmpTrapV1.setCommunity(
					CLICommonFunc.getAhString(snmpImpl.getSnmpTrapV1Community(index))
			);
		}
		
		/** element: <via-vpn-tunnel> */
		oDebug.debug("/configuration/snmp/trap-host/v1[@name='"+snmpTrapV1.getName()+"']",
				"via-vpn-tunnel", GenerateXMLDebug.SET_OPERATION,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		if(snmpImpl.isEnableVpnTunnel(snmpTrapV1.getName())){
			snmpTrapV1.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		return snmpTrapV1;
	}
	
	private SnmpTrapVersion createSnmpTrapVersionV2(int index) throws CreateXMLException{
		SnmpTrapVersion snmpTrapV2 = new SnmpTrapVersion();
		
		/** attribute: name */
		oDebug.debug("/configuration/snmp/trap-host",
				"v2c", GenerateXMLDebug.SET_NAME,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		snmpTrapV2.setName(snmpImpl.getSnmpTrapV2Address(index));
		
		/** attribute: operation */
		snmpTrapV2.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <community> */
		oDebug.debug("/configuration/snmp/trap-host/v2c[@name='"+snmpTrapV2.getName()+"']",
				"community", GenerateXMLDebug.CONFIG_ELEMENT,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		if(snmpImpl.isConfigSnmpTrapV2Community(index)){
			
			oDebug.debug("/configuration/snmp/trap-host/v2c[@name='"+snmpTrapV2.getName()+"']",
					"community", GenerateXMLDebug.SET_VALUE,
					snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
			snmpTrapV2.setCommunity(
					CLICommonFunc.getAhString(snmpImpl.getSnmpTrapV2Community(index))
			);
		}
		
		/** element: <via-vpn-tunnel> */
		oDebug.debug("/configuration/snmp/trap-host/v2c[@name='"+snmpTrapV2.getName()+"']",
				"via-vpn-tunnel", GenerateXMLDebug.SET_OPERATION,
				snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
		if(snmpImpl.isEnableVpnTunnel(snmpTrapV2.getName())){
			snmpTrapV2.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		return snmpTrapV2;
	}
	
	private SnmpReaderCommunity createSnmpReaderV1(){
		SnmpReaderCommunity readerV1 = new SnmpReaderCommunity();
		
		for(int i=0; i<snmpImpl.getReaderV1CommunitySize(); i++){
			for(int j=0; j<snmpImpl.getReaderV1IpSize(i); j++){
//				SnmpReaderCommunity.Community comunityObj = new SnmpReaderCommunity.Community();
//				comunityObj.setName(snmpImpl.getReaderV1Community(i) + " " + snmpImpl.getReaderV1IpHost(i, j));
//				comunityObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
//				readerV1.getCommunity().add(comunityObj);
				
				oDebug.debug("/configuration/snmp/reader/version",
						"v1", GenerateXMLDebug.SET_NAME,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				readerV1.getCommunity().add(
						CLICommonFunc.createAhNameActValueQuoteProhibited(snmpImpl.getReaderV1Community(i, j), 
								CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
				);
			}
		}
		return readerV1;
	}
	
	private SnmpReaderCommunity createSnmpReaderV2(){
		SnmpReaderCommunity readerV2 = new SnmpReaderCommunity();
		
		for(int i=0; i<snmpImpl.getReaderV2CommunitySize(); i++){
			for(int j=0; j<snmpImpl.getReaderV2IpSize(i); j++){
//				SnmpReaderCommunity.Community comunityObj = new SnmpReaderCommunity.Community();
//				comunityObj.setName(snmpImpl.getReaderV2Community(i) + " " + snmpImpl.getReaderV2IpHost(i, j));
//				comunityObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
//				readerV2.getCommunity().add(comunityObj);

				oDebug.debug("/configuration/snmp/reader/version",
						"v2c", GenerateXMLDebug.SET_NAME,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				readerV2.getCommunity().add(
						CLICommonFunc.createAhNameActValueQuoteProhibited(snmpImpl.getReaderV2Community(i ,j), 
								CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
				);
			}
		}
		return readerV2;
	}
	
	private SnmpReaderCommunity createSnmpReaderAny(){
		SnmpReaderCommunity readerAny = new SnmpReaderCommunity();
		
		for(int i=0; i<snmpImpl.getReaderAnyCommunitySize(); i++){
			for(int j=0; j<snmpImpl.getReaderAnyIpSize(i); j++){
//				SnmpReaderCommunity.Community comunityObj = new SnmpReaderCommunity.Community();
//				comunityObj.setName(snmpImpl.getReaderV2Community(i) + " " + snmpImpl.getReaderV2IpHost(i, j));
//				comunityObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
//				readerV2.getCommunity().add(comunityObj);

				oDebug.debug("/configuration/snmp/reader/version",
						"any", GenerateXMLDebug.SET_NAME,
						snmpImpl.getSnmpGuiName(), snmpImpl.getSnmpName());
				readerAny.getCommunity().add(
						CLICommonFunc.createAhNameActValueQuoteProhibited(snmpImpl.getReaderAnyCommunity(i ,j), 
								CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
				);
			}
		}
		return readerAny;
	}
}
