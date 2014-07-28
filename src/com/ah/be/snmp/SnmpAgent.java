package com.ah.be.snmp;

import java.net.NetworkInterface;
import java.util.Collection;
import java.util.List;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.ah.be.app.DebugUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;

public class SnmpAgent {
	public static String 			NORMAL_CLOSE	 = System.getenv("CATALINA_HOME")+"/.normal_close";
	
	
	public static int SNMP_TRAP_PORT	= 162;
	
	public static String SNMP_COLDSTART = "1.3.6.1.6.3.1.1.5.1";
	public static String SNMP_WARMSTART = "1.3.6.1.6.3.1.1.5.2";
	public static String SNMP_LINKUP = "1.3.6.1.6.3.1.1.5.4";
	public static String SNMP_LINKDOWN = "1.3.6.1.6.3.1.1.5.3";
	
	public static String SNMP_IFINDEX = "1.3.6.1.2.1.2.2.1.1";
	public static String SNMP_IFADMINSTATUS = "1.3.6.1.2.1.2.2.1.7";
	public static String SNMP_IFOPERSTATUS = "1.3.6.1.2.1.2.2.1.8";
	
	
	public static boolean sendWarmStartTrap() {
		try {
			return sendSnmpTrap(SNMP_WARMSTART, null);
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to send warm start trap",e);
			return false;
		}
	}
	public static boolean sendColdStartTrap() {
		try {
			return sendSnmpTrap(SNMP_COLDSTART, null);
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to send cold start trap",e);
			return false;
		}
	}
	public static boolean sendLinkUpTrap(String ifname) {
		try {
			int vbsLength = 3;
			int ifIndex = getIfIndex(ifname);
			VariableBinding[] vbs = new VariableBinding[vbsLength];
			for(int i= 0; i < vbsLength; i++) {
				vbs[i] = new VariableBinding();
			}
			vbs[0].setOid(new OID(SNMP_IFINDEX));
			vbs[0].setVariable(new Integer32(ifIndex));
			vbs[1].setOid(new OID(SNMP_IFADMINSTATUS));
			vbs[1].setVariable(new Integer32(1));
			vbs[2].setOid(new OID(SNMP_IFOPERSTATUS));
			vbs[2].setVariable(new Integer32(1));
			
			return sendSnmpTrap(SNMP_LINKUP, vbs);
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to send link up trap",e);
			return false;
		}
	}
	public static boolean sendLinkDownTrap(String ifname) {
		try {
			int vbsLength = 3;
			int ifIndex = getIfIndex(ifname);
			VariableBinding[] vbs = new VariableBinding[vbsLength];
			for(int i= 0; i < vbsLength; i++) {
				vbs[i] = new VariableBinding();
			}
			vbs[0].setOid(new OID(SNMP_IFINDEX));
			vbs[0].setVariable(new Integer32(ifIndex));
			vbs[1].setOid(new OID(SNMP_IFADMINSTATUS));
			vbs[1].setVariable(new Integer32(2));
			vbs[2].setOid(new OID(SNMP_IFOPERSTATUS));
			vbs[2].setVariable(new Integer32(2));
			
			return sendSnmpTrap(SNMP_LINKDOWN, vbs);
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to send link up trap",e);
			return false;
		}
	}

	private static boolean sendSnmpTrap(String oid, VariableBinding[] vbs) throws Exception{
		boolean bRet = false;
		SnmpApi snmpApi = new SnmpApi();
		List<HMServicesSettings> boList = QueryUtil.executeQuery(HMServicesSettings.class,null,null,null,new QueryBo() {
            
            @Override
            public Collection<HmBo> load(HmBo bo) {
                if (bo instanceof HMServicesSettings) {
                    HMServicesSettings hmServicesSettings = (HMServicesSettings) bo;
                    IpAddress ipAddress = hmServicesSettings.getSnmpReceiverIP();
                    if (null != ipAddress && null != ipAddress.getItems())
                        ipAddress.getItems().size();
                }
                return null;
            }
        });
		for(HMServicesSettings bo:boList) {
			if(null == bo.getSnmpCommunity() || bo.getSnmpCommunity().equalsIgnoreCase(""))
				continue;
			snmpApi.setCommunity(bo.getSnmpCommunity());
			IpAddress address = bo.getSnmpReceiverIP();
			if(null == address)
				continue;
			List<SingleTableItem> items = address.getItems();
			if(null != items) {
				for(SingleTableItem item:items) {
					if(null == item.getIpAddress())
						continue;
					snmpApi.setAddress(item.getIpAddress(),SNMP_TRAP_PORT);
					snmpApi.sendSnmpTrap(oid, vbs);
					bRet = true;
				}
			}
		}
		return bRet;
	}
	
	public static int getIfIndex(String ifname)	{
		int ifindex = 0;
		try{
			NetworkInterface netface = NetworkInterface.getByName(ifname);
			/*
			 this string is the same with
			Info of eth0 :name:eth0 (VMware Virtual Ethernet Adapter for VMnet8) index: 2 addresses:
			/192.168.2.1;
			 */
			String netinfo = netface.toString();
			netinfo.indexOf("index:");
			String[] result = netinfo.split(" ");
			for(int i = 0; i < result.length; i++) {
				if(result[i].equalsIgnoreCase("index:")) {
					if(i <  result.length-1) {
						ifindex = Integer.parseInt(result[i+1]);
					}
				}
			}
		}
		catch(Exception e)
		{
		}
		return ifindex;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String ifname = "eth0";
			if(args.length >= 2)
				ifname = args[1];
			int ifindex = SnmpAgent.getIfIndex(ifname);
			System.out.println("Index of "+ifname+" is "+ifindex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}