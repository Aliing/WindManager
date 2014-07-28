package com.ah.be.config.create.source.impl;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSnmpInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.SnmpProfileInt;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 * @author zhang
 *
 */
public class SnmpProfileImpl implements SnmpProfileInt {
	
	private static final Tracer log = new Tracer(SnmpProfileImpl.class
			.getSimpleName());
	
	private final HiveAp hiveAp;
	
	private final MgmtServiceSnmp mgmtServiceSnmp;
	private List<MgmtServiceSnmpInfo> snmpInfo;
	
	private List<SnmpInner> snmpInnerList = new ArrayList<SnmpInner>();
	private List<SnmpInner> trapHostV1;
	private List<SnmpInner> trapHostV2;
	private List<SnmpInner> readerV1;
	private List<SnmpInner> readerV2;
	private List<SnmpInner> readerAny;
	
	private final List<SnmpV3Info> trapHostV3 = new ArrayList<SnmpV3Info>();
	private final List<V3AdminInfo> trapHostAdminV3 = new ArrayList<V3AdminInfo>();
	private final List<V3AdminInfo> readerAdminV3 = new ArrayList<V3AdminInfo>();
	
//	private static final short SNMP_VERSION_V1 = MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V1;
//	private static final short SNMP_VERSION_V2 = 1;
	
//	private static final short SNMP_OPERATION_GET = 1;MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GET
//	private static final short SNMP_OPERATION_TRAP = 3;MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_TRAP
//	private static final short SNMP_OPERATION_GET_AND_TRAP = 2;MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP

	public SnmpProfileImpl (HiveAp hiveAp) throws CreateXMLException {
		this.hiveAp = hiveAp;
		
		mgmtServiceSnmp = hiveAp.getConfigTemplate().getMgmtServiceSnmp();
		if(mgmtServiceSnmp != null){
			snmpInfo = mgmtServiceSnmp.getSnmpInfo();
		}
		if(snmpInfo != null){
			loadSnmpInnerList(snmpInfo);
		}
		splitSnmpInnerList();
	}
	
	public String getSnmpGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceSnmp");
	}
	
	public String getSnmpName(){
		if(mgmtServiceSnmp != null){
			return mgmtServiceSnmp.getMgmtName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getUpdateTime(){
//		List<Object> snmpTimeList = new ArrayList<Object>();
//		snmpTimeList.add(hiveAp);
//		snmpTimeList.add(mgmtServiceSnmp);
//		if(snmpInfo != null){
//			for(MgmtServiceSnmpInfo infoObj : snmpInfo){
//				if(infoObj != null){
//					snmpTimeList.add(infoObj.getIpAddress());
//				}
//			}
//		}
//		return CLICommonFunc.getLastUpdateTime(snmpTimeList);
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public boolean isConfigSnmpLocation(){
		return (hiveAp.getLocation() != null && !"".equals(hiveAp.getLocation())) ||
			(hiveAp.isIncludeTopologyInfo() && hiveAp.getMapContainer() != null && hiveAp.getMapContainerName() != null && !"".equals(hiveAp.getMapContainerName()));
	}
	
	public String getSnmpLocation(){
		String location = "";
		if(hiveAp.getLocation() != null){
			location = hiveAp.getLocation().replace("@", "");
		}
		
		if(!hiveAp.isIncludeTopologyInfo()){
			return location;
		}
		
		String topoMap;
		if(hiveAp.getMapContainer() != null && hiveAp.getMapContainerName() != null && !"".equals(hiveAp.getMapContainerName()) ){
			topoMap = hiveAp.getMapContainerName();
		}else{
			topoMap = "";
		}
		
		if(StringUtils.isEmpty(topoMap)){
			return location;
		}else{
			return topoMap.startsWith("@") ? location + topoMap
					: location + "@" + topoMap;
		}
	}
	
	public boolean isConfigureSnmp(){
		return mgmtServiceSnmp != null;
	}
	
	public boolean isConfigureSnmpContact(){
		return mgmtServiceSnmp.getContact() != null && !"".equals(mgmtServiceSnmp.getContact());
	}
	
	public String getSnmpContact(){
		return mgmtServiceSnmp.getContact();
	}
	
	public boolean isConfigureSnmpReader(){
		return mgmtServiceSnmp.getEnableSnmp() && (!readerV1.isEmpty() || !readerV2.isEmpty() || !readerAdminV3.isEmpty() || !readerAny.isEmpty()) ;
	}
	
	public boolean isConfigureSnmpTrapHost(){
		return mgmtServiceSnmp.getEnableSnmp() && (!trapHostV1.isEmpty() || !trapHostV2.isEmpty() || !trapHostV3.isEmpty() || !trapHostAdminV3.isEmpty()) ;
	}
	
	public int getSnmpTrapHostV1Size(){
		return trapHostV1.size();
	}
	
	public int getSnmpTrapHostV2Size(){
		return trapHostV2.size();
	}
	
	public String getSnmpTrapV1Address(int index){
		return trapHostV1.get(index).getHostNameOrIp().get(0);
	}
	
	public boolean isConfigSnmpTrapV1Community(int index){
		String snmpV1Value = trapHostV1.get(index).getCommunity();
		return snmpV1Value != null && !"".equals(snmpV1Value);
	}
	
	public String getSnmpTrapV1Community(int index){
		return trapHostV1.get(index).getCommunity();
	}
	
	public String getSnmpTrapV2Address(int index){
		return trapHostV2.get(index).getHostNameOrIp().get(0);
	}
	
	public boolean isConfigSnmpTrapV2Community(int index){
		String snmpCommunityValue = getSnmpTrapV2Community(index);
		return snmpCommunityValue != null && !"".equals(snmpCommunityValue);
	}
	
	public String getSnmpTrapV2Community(int index){
		return trapHostV2.get(index).getCommunity();
	}
	
	public boolean isConfigureReaderV1(){
		return !readerV1.isEmpty();
	}
	
	public boolean isConfigureReaderV2(){
		return !readerV2.isEmpty();
	}
	
	public boolean isConfigureReaderAny(){
		return !readerAny.isEmpty();
	}
	
	public int getReaderV1CommunitySize(){
		return readerV1.size();
	}
	
	public int getReaderV1IpSize(int index){
		return readerV1.get(index).getHostNameOrIp().size();
	}
	
	public int getReaderV2CommunitySize(){
		return readerV2.size();
	}
	
	public int getReaderV2IpSize(int index){
		return readerV2.get(index).getHostNameOrIp().size();
	}
	
	public int getReaderAnyCommunitySize(){
		return readerAny.size();
	}
	
	public int getReaderAnyIpSize(int index){
		return readerAny.get(index).getHostNameOrIp().size();
	}
	
	public String getReaderV1Community(int i, int j){
		return readerV1.get(i).getCommunity() + " " + readerV1.get(i).getHostNameOrIp().get(j);
	}
	
	public String getReaderV2Community(int i, int j){
		return readerV2.get(i).getCommunity() + " " + readerV2.get(i).getHostNameOrIp().get(j);
	}
	
	public String getReaderAnyCommunity(int i, int j){
		return readerAny.get(i).getCommunity() + " " + readerAny.get(i).getHostNameOrIp().get(j);
	}
	
	public boolean isEnableOverCapwap(){
		return mgmtServiceSnmp.getEnableCapwap();
	}
	
	public boolean isEnableOverSnmp(){
		return mgmtServiceSnmp.getEnableSnmp();
	}
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isSnmpThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Snmp"});
						log.error("isEnableVpnTunnel", errMsg);
						throw new CreateXMLException(errMsg);
					}
					return true;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
	}
	
	//private function
	private void splitSnmpInnerList(){
		trapHostV1 = new ArrayList<SnmpInner>();
		trapHostV2 = new ArrayList<SnmpInner>();
		readerV1 = new ArrayList<SnmpInner>();
		readerV2 = new ArrayList<SnmpInner>();
		readerAny = new ArrayList<SnmpInner>();
		boolean isFound;
		
		for(SnmpInner snmpInnerObj : snmpInnerList){
			if(snmpInnerObj.getOperation() == EnumOperation.reader){
				if(snmpInnerObj.getVersion() == EnumVersion.v1 ){
					isFound = false;
					for(SnmpInner readerV1Obj : readerV1){
						if(snmpInnerObj.getCommunity().equals(readerV1Obj.getCommunity())){
							readerV1Obj.getHostNameOrIp().addAll(snmpInnerObj.getHostNameOrIp());
							isFound = true;
							break;
						}
					}
					if(!isFound){
						readerV1.add(snmpInnerObj);
					}
				}else if (snmpInnerObj.getVersion() == EnumVersion.v2c){
					isFound = false;
					for(SnmpInner readerV2Obj : readerV2){
						if(snmpInnerObj.getCommunity().equals(readerV2Obj.getCommunity())){
							readerV2Obj.getHostNameOrIp().addAll(snmpInnerObj.getHostNameOrIp());
							isFound = true;
							break;
						}
					}
					if(!isFound){
						readerV2.add(snmpInnerObj);
					}
				}else if (snmpInnerObj.getVersion() == EnumVersion.any){
					isFound = false;
					for(SnmpInner readerAnyObj : readerAny){
						if(snmpInnerObj.getCommunity().equals(readerAnyObj.getCommunity())){
							readerAnyObj.getHostNameOrIp().addAll(snmpInnerObj.getHostNameOrIp());
							isFound = true;
							break;
						}
					}
					if(!isFound){
						readerAny.add(snmpInnerObj);
					}
				}
			}else{
				if(snmpInnerObj.getVersion() == EnumVersion.v1 ){
					trapHostV1.add(snmpInnerObj);
				}else{
					trapHostV2.add(snmpInnerObj);
				}
			}
		}
	}
	
	public void loadDefaultSnmpInnerList(){
		SnmpInner snmpInnerHivecommunity = new SnmpInner(EnumOperation.reader, EnumVersion.any, "", "hivecommunity");
		snmpInnerList.add(snmpInnerHivecommunity);
	}
	
	private void loadSnmpInnerList(List<MgmtServiceSnmpInfo> snmpInfo) throws CreateXMLException {
		for(MgmtServiceSnmpInfo snmpInfoObj : snmpInfo){
			short versionSnmp = snmpInfoObj.getSnmpVersion();
			short operation = snmpInfoObj.getSnmpOperation();
			SingleTableItem ipItem = CLICommonFunc.getIpAddress(snmpInfoObj.getIpAddress(), hiveAp);
			String hostNameOrIp = ipItem.getIpAddress();
			String netMask = ipItem.getNetmask();
			
			if(netMask != null && !"".equals(netMask)){
				int netmast = CLICommonFunc.turnNetMaskToNum(netMask);
				if(netmast < 32){
					hostNameOrIp = hostNameOrIp + "/" + netmast;
				}
			}
			
//			String hostNameOrIp = ipItem.getIpAddress() + "/" + CLICommonFunc.turnNetMaskToNum(ipItem.getNetmask());
			if(mgmtServiceSnmp.getEnableCapwap() && hostNameOrIp != null && CLICommonFunc.filterNetmaskFromIpStr(hostNameOrIp).equals(NmsUtil.getCapwapServer(hiveAp, true))){
				continue;
			}

			EnumOperation enumOperation;
			EnumVersion enumVersion=null;
			SnmpInner snmpInner1, snmpInner2;
			if(versionSnmp == MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V1){
				enumVersion = EnumVersion.v1;
			}else if(versionSnmp == MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V2C){
				enumVersion = EnumVersion.v2c;
			}else if(versionSnmp == MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V3){
				enumVersion = EnumVersion.v3;
			}
			if(operation == MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GET){
				if(enumVersion == EnumVersion.v3){
					V3AdminInfo rdv3 = new V3AdminInfo();
					rdv3.setAdminName(snmpInfoObj.getUserName());
					String authStr;
					switch(snmpInfoObj.getAuthPassMethod()){
					case MgmtServiceSnmpInfo.PASS_AUTH_MD5 :
						authStr = "md5";
						break;
					case MgmtServiceSnmpInfo.PASS_AUTH_SHA :
						authStr = "sha";
						break;
					default :
						authStr = "";
					}
//					String authStr = (snmpInfoObj.getAuthPassMethod() == MgmtServiceSnmpInfo.PASS_AUTH_MD5) ? "md5" : "sha";
					rdv3.setAuth(authStr);
					rdv3.setAuthPas(snmpInfoObj.getAuthPass());
					String enc;
					switch(snmpInfoObj.getEncryPassMethod()){
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES :
							enc = "des";
							break;
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_AES :
							enc = "aes";
							break;
						default :
							enc = "";
					}
//					String enc = (snmpInfoObj.getEncryPassMethod() == MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES) ? "des" : "aes";
					rdv3.setEncryption(enc);
					rdv3.setEncryptionPas(snmpInfoObj.getEncryPass());
					readerAdminV3.add(rdv3);
				}else{
					enumOperation = EnumOperation.reader;
					snmpInner1 = new SnmpInner(enumOperation, enumVersion, hostNameOrIp, snmpInfoObj.getCommunity());
					snmpInnerList.add(snmpInner1);
				}
			}else if(operation == MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_TRAP){
				if(enumVersion == EnumVersion.v3){
					V3AdminInfo rdv3 = new V3AdminInfo();
					rdv3.setAdminName(snmpInfoObj.getUserName());
					String authStr;
					switch(snmpInfoObj.getAuthPassMethod()){
					case MgmtServiceSnmpInfo.PASS_AUTH_MD5 :
						authStr = "md5";
						break;
					case MgmtServiceSnmpInfo.PASS_AUTH_SHA :
						authStr = "sha";
						break;
					default :
						authStr = "";
					}
//					String authStr = (snmpInfoObj.getAuthPassMethod() == MgmtServiceSnmpInfo.PASS_AUTH_MD5) ? "md5" : "sha";
					rdv3.setAuth(authStr);
					rdv3.setAuthPas(snmpInfoObj.getAuthPass());
					String enc;
					switch(snmpInfoObj.getEncryPassMethod()){
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES :
							enc = "des";
							break;
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_AES :
							enc = "aes";
							break;
						default :
							enc = "";
					}
//					String enc = (snmpInfoObj.getEncryPassMethod() == MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES) ? "des" : "aes";
					rdv3.setEncryption(enc);
					rdv3.setEncryptionPas(snmpInfoObj.getEncryPass());
					trapHostAdminV3.add(rdv3);
					
					SnmpV3Info v3Info = new SnmpV3Info(CLICommonFunc.filterNetmaskFromIpStr(hostNameOrIp), snmpInfoObj.getUserName());
					trapHostV3.add(v3Info);
				}else{
					enumOperation = EnumOperation.trap_host;
					snmpInner1 = new SnmpInner(enumOperation, enumVersion, 
							CLICommonFunc.filterNetmaskFromIpStr(hostNameOrIp), snmpInfoObj.getCommunity());
					snmpInnerList.add(snmpInner1);
				}
			}else if(operation == MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP){
				if(enumVersion == EnumVersion.v3){
					V3AdminInfo rdv3 = new V3AdminInfo();
					rdv3.setAdminName(snmpInfoObj.getUserName());
					String authStr;
					switch(snmpInfoObj.getAuthPassMethod()){
					case MgmtServiceSnmpInfo.PASS_AUTH_MD5 :
						authStr = "md5";
						break;
					case MgmtServiceSnmpInfo.PASS_AUTH_SHA :
						authStr = "sha";
						break;
					default :
						authStr = "";
					}
//					String authStr = (snmpInfoObj.getAuthPassMethod() == MgmtServiceSnmpInfo.PASS_AUTH_MD5) ? "md5" : "sha";
					rdv3.setAuth(authStr);
					rdv3.setAuthPas(snmpInfoObj.getAuthPass());
					String enc;
					switch(snmpInfoObj.getEncryPassMethod()){
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES :
							enc = "des";
							break;
						case MgmtServiceSnmpInfo.PASS_ENCRYPTION_AES :
							enc = "aes";
							break;
						default :
							enc = "";
					}
//					String enc = (snmpInfoObj.getEncryPassMethod() == MgmtServiceSnmpInfo.PASS_ENCRYPTION_DES) ? "des" : "aes";
					rdv3.setEncryption(enc);
					rdv3.setEncryptionPas(snmpInfoObj.getEncryPass());
					trapHostAdminV3.add(rdv3);
					readerAdminV3.add(rdv3);
					
					SnmpV3Info v3Info = new SnmpV3Info(CLICommonFunc.filterNetmaskFromIpStr(hostNameOrIp), snmpInfoObj.getUserName());
					trapHostV3.add(v3Info);
				}else{
					enumOperation = EnumOperation.reader;
					snmpInner1 = new SnmpInner(enumOperation, enumVersion, hostNameOrIp, snmpInfoObj.getCommunity());
					snmpInnerList.add(snmpInner1);
					
					enumOperation = EnumOperation.trap_host;
					snmpInner2 = new SnmpInner(enumOperation, enumVersion, CLICommonFunc.filterNetmaskFromIpStr(hostNameOrIp), snmpInfoObj.getCommunity());
					snmpInnerList.add(snmpInner2);
				}
			}
		}
	}
	
	private static class SnmpInner{
		
		private EnumVersion version;
		
		private EnumOperation operation;
		
		private String community;
		
		private List<String> hostNameOrIp;
		
		public SnmpInner(EnumOperation operation, EnumVersion version, String hostNameOrIp, String community){
			this.version = version;
			this.operation = operation;
			this.community = community;
			this.getHostNameOrIp().add(hostNameOrIp);
		}
		
		public EnumVersion getVersion(){
			return version;
		}
		
		public EnumOperation getOperation(){
			return operation;
		}
		
		public String getCommunity(){
			return community;
		}
		
		public List<String> getHostNameOrIp(){
			if(hostNameOrIp == null){
				hostNameOrIp = new ArrayList<String>();
			}
			return hostNameOrIp;
		}
	}
	
	public static class SnmpV3Info{
		
		private final String ipAddr;
		
		private final String admin;
		
		public SnmpV3Info(String ipAddr, String admin){
			this.ipAddr = ipAddr;
			this.admin = admin;
		}
		
		public String getIpAddr(){
			return this.ipAddr;
		}
		
		public String getAdmin(){
			return this.admin;
		}
	}
	
	public static class V3AdminInfo{
		
		private String adminName;
		private String auth;
		private String authPas;
		private String encryption;
		private String encryptionPas;
		
		public V3AdminInfo(){
			
		}
		
		public void setAdminName(String adminName){
			this.adminName = adminName;
		}
		
		public String getAdminName(){
			return this.adminName;
		}
		
		public void setAuth(String auth){
			this.auth = auth;
		}
		
		public String getAuth(){
			if(this.auth == null){
				return "";
			}else{
				return this.auth;
			}
		}
		
		public void setAuthPas(String authPas){
			this.authPas = authPas;
		}
		
		public String getAuthPas(){
			if(this.authPas == null){
				return "";
			}else{
				return this.authPas;
			}
		}
		
		public void setEncryption(String encryption){
			this.encryption = encryption;
		}
		
		public String getEncryption(){
			if(this.encryption == null){
				return "";
			}
			return  this.encryption;
		}
		
		public void setEncryptionPas(String encryptionPas){
			this.encryptionPas = encryptionPas;
		}
		
		public String getEncryptionPas(){
			if(this.encryptionPas == null){
				return "";
			}
			return this.encryptionPas;
		}
	}
	
	public boolean isConfigReaderV3(){
		return !readerAdminV3.isEmpty();
	}
	
	public int getReaderV3Size(){
		return readerAdminV3.size();
	}
	
	public String getAdminName(int index, EnumOperation optEnum){
		if(optEnum == EnumOperation.reader){
			return readerAdminV3.get(index).getAdminName();
		}else{
			return trapHostAdminV3.get(index).getAdminName();
		}
	}
	
	public String getAuthValue(int index, EnumOperation optEnum){
		if(optEnum == EnumOperation.reader){
			return readerAdminV3.get(index).getAuth();
		}else{
			return trapHostAdminV3.get(index).getAuth();
		}
	}
	
	public String getAuthPas(int index, EnumOperation optEnum){
		if(optEnum == EnumOperation.reader){
			return readerAdminV3.get(index).getAuthPas();
		}else{
			return trapHostAdminV3.get(index).getAuthPas();
		}
	}
	
	public String getEncryption(int index, EnumOperation optEnum){
		if(optEnum == EnumOperation.reader){
			return readerAdminV3.get(index).getEncryption();
		}else{
			return trapHostAdminV3.get(index).getEncryption();
		}
	}
	
	public String getEncryptionPas(int index, EnumOperation optEnum){
		if(optEnum == EnumOperation.reader){
			return readerAdminV3.get(index).getEncryptionPas();
		}else{
			return trapHostAdminV3.get(index).getEncryptionPas();
		}
	}
	
	public int getV3TrapAdminSize(){
		return trapHostAdminV3.size();
	}
	
	public int getV3TrapSize(){
		return trapHostV3.size();
	}
	
	public String getV3TrapHostName(int index){
		return trapHostV3.get(index).getIpAddr();
	}
	
	public String getV3TrapHostAdmin(int index){
		return trapHostV3.get(index).getAdmin();
	}
	
	public String getV3TrapHostVpnTunnel(int index) throws CreateXMLException{
		if(this.isEnableVpnTunnel(trapHostV3.get(index).getIpAddr())){
			return " ";
		}else{
			return "";
		}
	}
	
//	public boolean isConfigEncryption(int index, EnumOperation optEnum){
//		String enc = this.getEncryption(index, optEnum);
//		String pas = this.getEncryptionPas(index, optEnum);
//		return pas != null && !"".equals(pas) && enc != null && !"".equals(enc);
//	}

}