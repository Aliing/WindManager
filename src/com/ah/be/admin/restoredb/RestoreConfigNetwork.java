/**
 *@filename		AhRestoreConfigNetwork.java
 *@version
 *@author		Fiona
 *@createtime	2007-11-7 PM 06:55:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *remove dns alg 2009-02-05
 */
package com.ah.be.admin.restoredb;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.LinuxNetConfigImpl;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourActiveService;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.BonjourServiceCategory;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.DhcpServerIpPool;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DnsSpecificSettings;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.MstpRegionPriority;
import com.ah.bo.network.NeighborsNameItem;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.OsVersion;
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.PortForwarding;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.SubnetworkDHCPCustom;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VlanGroup;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkClassification;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.home.HmServicesAction;
import com.ah.util.classifiertag.DefaultTagOrderComparator;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
public class RestoreConfigNetwork
{
    private final static short NETWORK_OBJECT_IP = 1;

	private final static short NETWORK_OBJECT_MAC = 2;

	private final static short NETWORK_OBJECT_VLAN = 3;

	private final static short NETWORK_OBJECT_ATTRIBUTE = 4;

	private final static short LOCATIONCLIENT_ITEM = 5;

	private final static short VPN_NETWORK_IP_RESERVATION_ITEM = 6;

	private final static short NETWORK_OBJECT_RADIUS_OPERATOR = 7;

	private final static short VPN_NETWORK_SUBNET_RESERVATION_ITEM = 8;

	public static final Map<String, IpAddress> needCreateNetworkObject = new HashMap<String, IpAddress>();
	
	private final static DefaultTagOrderComparator defaultComparator = new DefaultTagOrderComparator();
	public final static boolean restore_from_60r1_before = RestoreHiveAp.isRestoreHmBeforeVersion("6.0.1.0");

    public static final boolean RESTORE_BEFORE_DARKA_FLAG = NmsUtil.compareSoftwareVersion(
            NmsUtil.getHiveOSVersion(NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH
                    + File.separatorChar + ".." + File.separatorChar + "hivemanager.ver")),
            "5.1.1.0") < 0;

    public static final boolean RESTORE_BEFORE_CASABLANCA_FLAG = NmsUtil.compareSoftwareVersion(
            NmsUtil.getHiveOSVersion(NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH
                    + File.separatorChar + ".." + File.separatorChar + "hivemanager.ver")),
            "5.0.3.0") < 0;
    
    public static final boolean RESTORE_BEFORE_HOLLYWOOD_FLAG = NmsUtil.compareSoftwareVersion(
            NmsUtil.getHiveOSVersion(NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH
                    + File.separatorChar + ".." + File.separatorChar + "hivemanager.ver")),
            "6.2.1.0") < 0;
	
	//public static boolean restorePreVpnNetwork = false;

	/**
	 * Get all information from ip_address_item or mac_or_oui_item or vlan_item or attribute_item table
	 *
	 * @param objectType -
	 * @return List<SingleTableItem> all SingleTableItem
	 * @throws AhRestoreColNotExistException -
	 *             if .xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing .xml.
	 */
	private static List<SingleTableItem> getAllNetworkObjectItems(short objectType) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "";
		String idKey = "";

		switch(objectType)
		{
			case NETWORK_OBJECT_IP:
				tableName = "ip_address_item";
				idKey = "ip_address_id";
				break;
			case NETWORK_OBJECT_MAC:
				tableName = "mac_or_oui_item";
				idKey = "mac_or_oui_id";
				break;
			case NETWORK_OBJECT_VLAN:
				tableName = "vlan_item";
				idKey = "vlan_id";
				break;
			case NETWORK_OBJECT_ATTRIBUTE:
				tableName = "attribute_item";
				idKey = "attribute_id";
				break;
			case LOCATIONCLIENT_ITEM:
				tableName = "locationclient_item";
				idKey = "locationclientwatch_id";
				break;
			case VPN_NETWORK_IP_RESERVATION_ITEM:
				tableName = "vpn_network_ip_reserve_item";
				idKey = "vpn_network_reserveclass_id";
				break;
			case NETWORK_OBJECT_RADIUS_OPERATOR:
				tableName = "radius_attribute_item";
				idKey = "radius_attribute_id";
				break;
			case VPN_NETWORK_SUBNET_RESERVATION_ITEM:
			    tableName = "vpn_network_subnetclass";
			    idKey = "vpn_network_subnetclass_id";
			    break;

			default:
				break;
		}

		/**
		 * Check validation of tableName.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<SingleTableItem> items = new ArrayList<SingleTableItem>();

		boolean isColPresent;
		String colName;
		SingleTableItem singleItem;

		for (int i = 0; i < rowCount; i++)
		{
			singleItem = new SingleTableItem();

			/**
			 * Set id
			 */
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, idKey);
			String id = isColPresent ? xmlParser.getColVal(i, idKey) : "1";
			singleItem.setRestoreId(AhRestoreCommons.convertString(id));

			/**
			 * Set IpAddress
			 */
			colName = "ipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String ip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setIpAddress(AhRestoreCommons.convertString(ip));

			/**
			 * Set operatorname
			 */
			colName = "operatorname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String operatorname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setOperatorName(AhRestoreCommons.convertString(operatorname));


			/**
			 * Set netmask
			 */
			colName = "netmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String netmask = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setNetmask(AhRestoreCommons.convertString(netmask));

			/**
			 * Set typename
			 */
			colName = "typename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String typename = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setTypeName(AhRestoreCommons.convertString(typename));

			/**
			 * Set location_id
			 */
			colName = "location_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String location = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(location))
			{
				Long newMapId = AhRestoreNewMapTools.getMapMapContainer(AhRestoreCommons.convertLong(location));
				if(null != newMapId)
				{
					singleItem.setLocation(AhRestoreNewTools.CreateBoWithId(MapContainerNode.class, newMapId));
				}
			}

			/**
			 * Set tag1Checked
			 */
			colName = "tag1checked";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String check1 = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleItem.setTag1Checked(AhRestoreCommons.convertStringToBoolean(check1));

			/**
			 * Set tag1
			 */
			colName = "tag1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String tag1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setTag1(AhRestoreCommons.convertString(tag1));

			/**
			 * Set tag2Checked
			 */
			colName = "tag2checked";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String check2 = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleItem.setTag2Checked(AhRestoreCommons.convertStringToBoolean(check2));

			/**
			 * Set tag2
			 */
			colName = "tag2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String tag2 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setTag2(AhRestoreCommons.convertString(tag2));

			/**
			 * Set tag3Checked
			 */
			colName = "tag3checked";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String check3 = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleItem.setTag3Checked(AhRestoreCommons.convertStringToBoolean(check3));

			/**
			 * Set tag3
			 */
			colName = "tag3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String tag3 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setTag3(AhRestoreCommons.convertString(tag3));

			/**
			 * Set namespaceid
			 */
			colName = "namespaceid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String namespaceid = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SingleTableItem.TYPE_NAMESPACE_REALM);
			singleItem.setNameSpaceId((short)AhRestoreCommons.convertInt(namespaceid));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SingleTableItem.TYPE_GLOBAL);
			singleItem.setType((short)AhRestoreCommons.convertInt(type));

			/**
			 * Set vlanid
			 */
			colName = "vlanid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			int vlanid = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 1;
			singleItem.setVlanId(vlanid > 4094 ? 4094 : vlanid);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String comment = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setDescription(AhRestoreCommons.convertString(comment));

			/**
			 * Set macentry
			 */
			colName = "macentry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String macentry = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setMacEntry(AhRestoreCommons.convertString(macentry));

			/**
			 * Set attributevalue
			 */
			colName = "attributevalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String attributevalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setAttributeValue(AhRestoreCommons.convertString(attributevalue));

			/**
			 * Set macrangefrom
			 */
			colName = "macrangefrom";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String macrangefrom = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(macrangefrom)) {

				/**
				 * Set macrangeto
				 */
				colName = "macrangeto";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
				String macrangeto = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

				if (macrangefrom.length() != 12) {
					singleItem.setMacRangeFrom(singleItem.getMacEntry()+macrangefrom);
					singleItem.setMacRangeTo(singleItem.getMacEntry()+macrangeto);
					singleItem.setMacEntry("");
				} else {
					singleItem.setMacRangeFrom(macrangefrom);
					singleItem.setMacRangeTo(macrangeto);
				}
			}

			items.add(singleItem);
		}
		if(items.size() > 0&&VPN_NETWORK_SUBNET_RESERVATION_ITEM!=objectType&&VPN_NETWORK_IP_RESERVATION_ITEM!=objectType&&restore_from_60r1_before){
			 Collections.sort(items, defaultComparator);			
		}

		return items.size() > 0 ? items : null;
	}

	/**
	 * Get all information from ip_address table
	 *
	 * @param lstLogBo -
	 * @return List<IpAddress> all IpAddress BO
	 * @throws AhRestoreColNotExistException -
	 *             if ip_address.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing ip_address.xml.
	 */
	private static List<IpAddress> getAllIpAddress(List<HmUpgradeLog> lstLogBo) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ip_address.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ip_address");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<IpAddress> ipAddress = new ArrayList<IpAddress>();

		boolean isColPresent;
		String colName;
		List<SingleTableItem> items = getAllNetworkObjectItems(NETWORK_OBJECT_IP);
		IpAddress ipNetworkDTO;

		for (int i = 0; i < rowCount; i++)
		{
			ipNetworkDTO = new IpAddress();

			/**
			 * Set addressname
			 */
			colName = "addressname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_address", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_address' data be lost, cause: 'addressname' column is null.");
				continue;
			} else if (name.length() > HmBoBase.DEFAULT_STRING_LENGTH) {
				name = name.substring(0, HmBoBase.DEFAULT_STRING_LENGTH);
			}
			ipNetworkDTO.setAddressName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_address", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ipNetworkDTO.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set typeflag
			 */
			colName = "typeflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_address", colName);
			short typeflag = isColPresent ?  (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : IpAddress.TYPE_IP_ADDRESS;
			ipNetworkDTO.setTypeFlag(typeflag);

			/**
			 * Set defaultflag
			 */
			ipNetworkDTO.setDefaultFlag(false);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ip_address", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);

			// default ip does not need restore
			if (null == ownerDomain || HmDomain.GLOBAL_DOMAIN.equals(ownerDomain.getDomainName())) {

				// set default ip object new id to map
				String paraName = name;
				if (name.equals("10.0.0.0")) {
					paraName = "10.0.0.0/255.0.0.0";
				} else if (name.equals("172.16.0.0")) {
					paraName = "172.16.0.0/255.240.0.0";
				} else if (name.equals("192.168.0.0")) {
					paraName = "192.168.0.0/255.255.0.0";
				}
				List<IpAddress> ipList = QueryUtil.executeQuery(IpAddress.class, null, new FilterParams("addressName = :s1 AND owner.domainName = :s2",
					new Object[]{paraName, HmDomain.GLOBAL_DOMAIN}));
				IpAddress newIp = null;
				if (!ipList.isEmpty())
					newIp = ipList.get(0);
				if (null != newIp) {
					AhRestoreNewMapTools.setMapIpAdddress(ipNetworkDTO.getId(), newIp.getId());
				}
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_address' data be lost, cause: default ip does not need restore.");
				continue;
			}

            ipNetworkDTO.setOwner(ownerDomain);

			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				boolean bool = false;
				int count = 1;
				// get HiveManager IP
				NetConfigImplInterface netConfig = new LinuxNetConfigImpl();
				String defIp = netConfig.getHiveManagerIPAddr();
				String defNet = IpAddress.NETMASK_OF_SINGLE_IP;

				for (SingleTableItem item : items) {
					if (id.equals(item.getRestoreId())) {
						if (SingleTableItem.TYPE_GLOBAL == item.getType()) {
							bool = true;
						}
						defIp = item.getIpAddress();
						defNet = item.getNetmask();
						if (singleItem.size() > 0) {
							int j = 0;
							for(SingleTableItem oneitem : singleItem) {
								if(oneitem.getIpAddress().equals(defIp)) {
									j ++;
								}
							}
							if (j == singleItem.size()) {
								count = singleItem.size() + 1;
							}
						}
						singleItem.add(item);
					}
				}
				if (!bool) {
					SingleTableItem item = new SingleTableItem();
					if (count == singleItem.size()) {
						item.setIpAddress(defIp);
						item.setNetmask(defNet);
					} else {
						item.setIpAddress(netConfig.getHiveManagerIPAddr());
						item.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
					}
					item.setType(SingleTableItem.TYPE_GLOBAL);
					item.setDescription("global IP value");
					singleItem.add(item);
				}
				// get the need create network objects
				if (IpAddress.TYPE_IP_ADDRESS == typeflag) {

					// get need create network object items
					List<SingleTableItem> netItem = new ArrayList<SingleTableItem>();
					boolean needCreate = false;
					StringBuilder netmasks = new StringBuilder();
					for (SingleTableItem item : singleItem) {
						netmasks.append(item.getIpAddress()).append("/").append(item.getNetmask()).append(",  ");
						SingleTableItem newItem = item.clone();
						netItem.add(newItem);
						// the netmask is not default
						if (!IpAddress.NETMASK_OF_SINGLE_IP.equals(item.getNetmask())) {
							needCreate = true;
							item.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
						}
					}
					if (needCreate) {
						// add upgrade log
						HmUpgradeLog upgradeLog = new HmUpgradeLog();
						String netLog = netmasks.toString().trim();
						upgradeLog.setFormerContent("IP Object/Host Name \""+name+"\" had IP entry "+netLog.substring(0, netLog.length()-1)+".");
						upgradeLog.setPostContent("The netmask was removed from the IP entry and defined by the IP Object/Host Name category (IP Address).");
						upgradeLog.setRecommendAction("No action is required.");
						upgradeLog.setOwner(ownerDomain);
						upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
						upgradeLog.setAnnotation("Click to add an annotation");
						lstLogBo.add(upgradeLog);

						IpAddress mapIp = new IpAddress();
						mapIp.setAddressName(name);
						mapIp.setOwner(ownerDomain);
						mapIp.setItems(netItem);
						needCreateNetworkObject.put(id, mapIp);
					}
					
					changeIPAddressOfDNSServer(lstLogBo, ipNetworkDTO,
                            ownerDomain, singleItem);
				}
				ipNetworkDTO.setItems(singleItem);
			}
			ipAddress.add(ipNetworkDTO);
		}

		return ipAddress.size() > 0 ? ipAddress : null;
	}

    private static void changeIPAddressOfDNSServer(List<HmUpgradeLog> lstLogBo,
            IpAddress ipNetworkDTO, HmDomain ownerDomain,
            List<SingleTableItem> singleItem) {
        final String oldAddressName = ipNetworkDTO.getAddressName();
        
        if (checkDNSItem(ipNetworkDTO, singleItem, 0)) {
            generateUpgradeLog(lstLogBo, ipNetworkDTO, ownerDomain,
                    oldAddressName, "primary");
        } else if (checkDNSItem(ipNetworkDTO, singleItem, 1)) {
            generateUpgradeLog(lstLogBo, ipNetworkDTO, ownerDomain,
                    oldAddressName, "secondary");
        }
    }

    private static void generateUpgradeLog(List<HmUpgradeLog> lstLogBo,
            IpAddress ipNetworkDTO, HmDomain ownerDomain,
            final String oldAddressName, final String prefixDNSText) {
        HmUpgradeLog upgradeLog = new HmUpgradeLog();
        upgradeLog.setFormerContent("The IP address of default " + prefixDNSText + " DNS server was "
                + oldAddressName);
        upgradeLog
                .setPostContent("Its IP address was changed to " + ipNetworkDTO.getAddressName());
        upgradeLog.setRecommendAction("No action is required.");
        upgradeLog.setOwner(ownerDomain);
        upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),
                ownerDomain.getTimeZoneString()));
        upgradeLog.setAnnotation("Click to add an annotation");
        lstLogBo.add(upgradeLog);
    }

    public final static String[] oldDNSServers = {
            HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1_DEPRECATED,
            HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2_DEPRECATED };
    public final static String[] newDNSServers = {
            HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
            HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2 };
    private static boolean checkDNSItem(IpAddress ipNetworkDTO,
            List<SingleTableItem> singleItem, int index) {
        
        final String oldDNSVal = oldDNSServers[index];
        final String newDNSVal = newDNSServers[index];
        
        if (ipNetworkDTO.getAddressName().equals(oldDNSVal)) {
            ipNetworkDTO.setAddressName(newDNSVal);
            for (SingleTableItem item : singleItem) {
                if (item.getIpAddress().equals(oldDNSVal)) {
                    item.setIpAddress(newDNSVal);
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * Restore ip_address table
	 *
	 * @return true if table of ip_address restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreIpAdress()
	{
		try {
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<IpAddress> allIp = getAllIpAddress(lstLogBo);

			// the default value has been inserted before restore
			if(null != allIp) {
				List<Long> lOldId = new ArrayList<Long>();

				for (IpAddress ip : allIp) {
					lOldId.add(ip.getId());
				}

				QueryUtil.restoreBulkCreateBos(allIp);

				for(int i=0; i<allIp.size(); i++)
				{
					AhRestoreNewMapTools.setMapIpAdddress(lOldId.get(i), allIp.get(i).getId());
				}
			}
			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from mac_or_oui table
	 *
	 * @return List<MacOrOui> all MacOrOui BO
	 * @throws AhRestoreColNotExistException -
	 *             if mac_or_oui.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing mac_or_oui.xml.
	 */
	private static List<MacOrOui> getAllMacOrOui() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of mac_or_oui.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mac_or_oui");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<MacOrOui> macAddress = new ArrayList<MacOrOui>();

		boolean isColPresent;
		String colName;
		List<SingleTableItem> items = getAllNetworkObjectItems(NETWORK_OBJECT_MAC);
		MacOrOui macAddressDTO;

		overlap:
		for (int i = 0; i < rowCount; i++)
		{
			macAddressDTO = new MacOrOui();

			/**
			 * Set macorouiname
			 */
			colName = "macorouiname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_or_oui", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_or_oui' data be lost, cause: 'macorouiname' column is not exist.");
				continue;
			}
			macAddressDTO.setMacOrOuiName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_or_oui", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			macAddressDTO.setId(AhRestoreCommons.convertLong(id));




			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_or_oui", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "f";

			for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
				String[] macs = macInfo[1].split(",");
				for (String mac : macs) {
					String ahMac = BeParaModule.DEFAULT_MAC_OUI_NAME + "-" + mac;
					if (macInfo[0].equals(name) ||
							(BeParaModule.DEFAULT_MAC_OUI_NAME.equals(macInfo[0]) && ahMac.equals(name))) {
					// set default mac address or oui object new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("macorouiname", name);
					MacOrOui newMac = HmBeParaUtil.getDefaultProfile(MacOrOui.class, map);
					if (null != newMac) {
						AhRestoreNewMapTools.setMapMacAddress(macAddressDTO.getId(), newMac.getId());
					}
					continue overlap;
					}
				}
			}

			if (BeParaModule.DEFAULT_MAC_ADDRESS_NAME.equals(name) &&
					defaultflag.equalsIgnoreCase("t")) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("macorouiname", BeParaModule.DEFAULT_MAC_OUI_NAME + "-" + NmsUtil.getHiveApMacOui()[0]);

				MacOrOui newMac = HmBeParaUtil.getDefaultProfile(MacOrOui.class, map);
				if (null != newMac) {
					AhRestoreNewMapTools.setMapMacAddress(macAddressDTO.getId(), newMac.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			macAddressDTO.setDefaultFlag(false);

			/**
			 * Set typeflag
			 */
			colName = "typeflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_or_oui", colName);
			String typeflag = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(MacOrOui.TYPE_MAC_ADDRESS);
			macAddressDTO.setTypeFlag((short)AhRestoreCommons.convertInt(typeflag));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"mac_or_oui", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_or_oui' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			macAddressDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				for(SingleTableItem item : items)
				{
					if(id.equals(item.getRestoreId()))
					{
						singleItem.add(item);
					}
				}
				macAddressDTO.setItems(singleItem);
			}
			macAddress.add(macAddressDTO);
		}

		return macAddress.size() > 0 ? macAddress : null;
	}

	/**
	 * Restore mac_or_oui table
	 *
	 * @return true if table of mac_or_oui restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreMacOrOui()
	{
		try {
			List<MacOrOui> allMac = getAllMacOrOui();

			//  the default values have been inserted before restore
			if(null != allMac) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MacOrOui mac : allMac) {
					lOldId.add(mac.getId());
				}

				QueryUtil.restoreBulkCreateBos(allMac);

				for(int i=0; i<allMac.size(); i++)
				{
					AhRestoreNewMapTools.setMapMacAddress(lOldId.get(i), allMac.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from network_service table
	 *
	 * @return List<NetworkService> all NetworkService BO
	 * @throws AhRestoreColNotExistException -
	 *             if network_service.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing network_service.xml.
	 */
	private static List<NetworkService> getAllNetworkService(String strTableName) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of network_service.xml
		 */
		boolean restoreRet = xmlParser.readXMLOneFile(strTableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<NetworkService> netService = new ArrayList<NetworkService>();

		boolean isColPresent;
		String colName;
		NetworkService netServiceDTO;

		overlap:
		for (int i = 0; i < rowCount; i++)
		{
			netServiceDTO = new NetworkService();

			/**
			 * Set servicename
			 */
			colName = "servicename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'network_service' data be lost, cause: 'servicename' column is not exist.");
				continue;
			}
			netServiceDTO.setServiceName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			netServiceDTO.setId(AhRestoreCommons.convertLong(id));

			/**
			 * servicetype
			 */
			colName = "servicetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"network_service", colName);
			String servicetype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			netServiceDTO.setServiceType((short)AhRestoreCommons.convertInt(servicetype));
			
			// some default network service change to non-default from the older to 3.2b2
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			boolean defaultflag = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			if (defaultflag) {
				for (int j = 0; j < BeParaModule.OLD_DEFAULT_NETWORK_SERVICE.length; j++) {
					if (BeParaModule.OLD_DEFAULT_NETWORK_SERVICE[j].equalsIgnoreCase(name)) {
						name = BeParaModule.NEW_DEFAULT_NETWORK_SERVICE[j];
						// set default network service object new id to map
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("servicename", name);
						NetworkService newNet = HmBeParaUtil.getDefaultProfile(NetworkService.class, map);
						if (null != newNet) {
							AhRestoreNewMapTools.setMapNetworkService(netServiceDTO.getId(), newNet.getId());
						}
						continue overlap;
					}
				}
			}

			// the default profiles have existed in database
			for (String[] preServiceInfo : BeParaModule.NETWORK_PRE_DEFIND_SERVICES) {
				if (preServiceInfo[0].equalsIgnoreCase(name)) {
					name = preServiceInfo[0];
					// set default network service object new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("servicename", name);
					NetworkService newNet = HmBeParaUtil.getDefaultProfile(NetworkService.class, map);
					if (null != newNet) {
						AhRestoreNewMapTools.setMapNetworkService(netServiceDTO.getId(), newNet.getId());
					}
					continue overlap;
				}
			}

			/**
			 * Set defaultflag
			 */
			netServiceDTO.setDefaultFlag(false);
			
			/**
			 * Set cliDefaultFlag
			 */
			netServiceDTO.setCliDefaultFlag(false);

			/**
			 * Set idletimeout
			 */
			colName = "idletimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			String idletimeout = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			netServiceDTO.setIdleTimeout(AhRestoreCommons.convertInt(idletimeout));

			/**
			 * appid
			 */
			colName = "appid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"network_service", colName);
			String appid = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			netServiceDTO.setAppId(AhRestoreCommons.convertInt(appid));
			
			/**
			 * Set portnumber
			 */
			colName = "portnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			String portnumber = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			netServiceDTO.setPortNumber(AhRestoreCommons.convertInt(portnumber));

			/**
			 * Set protocolid
			 */
			colName = "protocolid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			if(netServiceDTO.getServiceType() == 2){
				String protocolid = isColPresent ? xmlParser.getColVal(i, colName) : "0";
				netServiceDTO.setProtocolId((short)AhRestoreCommons.convertInt(protocolid));
			}else{
				String protocolid = isColPresent ? xmlParser.getColVal(i, colName) : "4";
				netServiceDTO.setProtocolId((short)AhRestoreCommons.convertInt(protocolid));
			}

			/**
			 * Set algtype
			 */
			colName = "algtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			if(netServiceDTO.getServiceType() == 2){
				short algtype = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 0;
				netServiceDTO.setAlgType(algtype);
			}else{
				short algtype = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 1;
				netServiceDTO.setAlgType(algtype);
			}

			/**
			 * Set protocolnumber
			 */
			colName = "protocolnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			if(netServiceDTO.getServiceType() == 2){
				String protocolnumber = isColPresent ? xmlParser.getColVal(i, colName) : "0";
				netServiceDTO.setProtocolNumber(AhRestoreCommons.convertInt(protocolnumber));
			}else{
				String protocolnumber = isColPresent ? xmlParser.getColVal(i, colName) : "1";
				netServiceDTO.setProtocolNumber(AhRestoreCommons.convertInt(protocolnumber));
			}

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"network_service", colName);
			String comment = isColPresent ? xmlParser.getColVal(i, colName) : "";
			netServiceDTO.setDescription(AhRestoreCommons.convertString(comment));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"network_service", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'network_service' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			netServiceDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			if (2 == netServiceDTO.getServiceType() && netServiceDTO.getServiceName().length() > 32) {
                Application app = QueryUtil.findBoByAttribute(Application.class, "appCode", netServiceDTO.getAppId());
                if (null != app) {
                    String appName = NetworkService.L7_SERVICE_NAME_PREFIX+app.getShortName();
                    if(appName.length() > 32){
                        appName = appName.substring(0, 32);
                    }
                    netServiceDTO.setServiceName(appName);
                }
            }
			netService.add(netServiceDTO);
		}
		
		return netService.size() > 0 ? netService : null;
	}

	/**
	 * Restore network_service table
	 *
	 * @return true if table of network_service restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreNetworkService()
	{
		try {
			long start = System.currentTimeMillis();
			
			final String tableName = "network_service";
			int index = 0;
			
			long totalRecordCount = 0;

			while (true) {
				String fileName = tableName;
				if (index > 0) {
					fileName = tableName + "_" + index;
				}
				index++;

				List<NetworkService> allNetwork = getAllNetworkService(fileName);
				if (allNetwork == null) {
					break;
				}
				if (!allNetwork.isEmpty()) {
					totalRecordCount = totalRecordCount + allNetwork.size();
				}
			
				List<Long> lOldId = new ArrayList<Long>();
				for (NetworkService network : allNetwork) {
					lOldId.add(network.getId());
				}
				
				QueryUtil.restoreBulkCreateBos(allNetwork);

				for(int i=0; i<allNetwork.size(); i++)
				{
					AhRestoreNewMapTools.setMapNetworkService(lOldId.get(i), allNetwork.get(i).getId());
				}
			}
				
			long end = System.currentTimeMillis();
            AhRestoreDBTools.logRestoreMsg("Restore Network Service completely. Count:"
                    + totalRecordCount + ", cost:" + (end - start) + " ms.");
			
			
//			if(null != allNetwork) {
//				List<Long> lOldId = new ArrayList<Long>();
//				for (NetworkService network : allNetwork) {
//					lOldId.add(network.getId());
//				}
//				
//				QueryUtil.restoreBulkCreateBos(allNetwork);
//
//				for(int i=0; i<allNetwork.size(); i++)
//				{
//					AhRestoreNewMapTools.setMapNetworkService(lOldId.get(i), allNetwork.get(i).getId());
//				}
//				long end = System.currentTimeMillis();
//	            AhRestoreDBTools.logRestoreMsg("Restore Network Service completely. Count:"
//	                    + (null == allNetwork ? "0" : allNetwork.size()) + ", cost:" + (end - start) + " ms.");
//			}
			//init all domain L7 Application service
			Map<Long, HmDomain> hmDomainMap  = AhRestoreNewMapTools.hmDomainMap;
			if(!hmDomainMap.isEmpty() && hmDomainMap.size() > 0){
				List<Application> allAppList = QueryUtil.executeQuery(Application.class, null, 
						new FilterParams("appCode > :s1", new Object[] {0}));
				
				for(HmDomain hd: hmDomainMap.values()){
					if(hd.getDomainName().equals(HmDomain.GLOBAL_DOMAIN)){
						continue;
					}
					long appNum = QueryUtil.findRowCount(NetworkService.class, new FilterParams("serviceType = :s1 and owner.id = :s2",
							new Object[]{NetworkService.SERVICE_TYPE_L7, hd.getId()}));
					if(appNum == 0){
						List<NetworkService> insertAppServices = new ArrayList<NetworkService>();
						if(!allAppList.isEmpty()){
							for(Application app : allAppList){
								if(app.getAppCode() != 0){
										NetworkService serviceDto = new NetworkService();
										serviceDto.setServiceName(NetworkService.L7_SERVICE_NAME_PREFIX+app.getAppName());
										serviceDto.setProtocolNumber(0);
										serviceDto.setPortNumber(0);				
										serviceDto.setIdleTimeout(300);
										serviceDto.setDescription(app.getAppName());
										serviceDto.setAlgType((short)0);
										serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
										serviceDto.setAppId(app.getAppCode());
										serviceDto.setDefaultFlag(false);
										serviceDto.setOwner(hd);
										serviceDto.setCliDefaultFlag(false);
										insertAppServices.add(serviceDto);
								}
							}
						}
						QueryUtil.bulkCreateBos(insertAppServices);
					}
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from vlan table
	 *
	 * @return List<Vlan> all vlan BO
	 * @throws AhRestoreColNotExistException -
	 *             if vlan.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing vlan.xml.
	 */
	private static List<Vlan> getAllVlan() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vlan.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("vlan");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<Vlan> vlan = new ArrayList<Vlan>();

		boolean isColPresent;
		String colName;
		List<SingleTableItem> items = getAllNetworkObjectItems(NETWORK_OBJECT_VLAN);
		Vlan vlanDTO;

		for (int i = 0; i < rowCount; i++)
		{
			vlanDTO = new Vlan();

			/**
			 * Set vlanname
			 */
			colName = "vlanname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vlan' data be lost, cause: 'vlanname' column is null.");
				continue;
			}
			vlanDTO.setVlanName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			vlanDTO.setId(AhRestoreCommons.convertLong(id));

			if (name.equals("1")) {
				// set default vlan object new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("vlanname", name);
				Vlan newVlan = HmBeParaUtil.getDefaultProfile(Vlan.class, map);
				if (null != newVlan) {
					AhRestoreNewMapTools.setMapVlan(vlanDTO.getId(), newVlan.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			vlanDTO.setDefaultFlag(false);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"vlan", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain hmdom = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == hmdom || HmDomain.GLOBAL_DOMAIN.equals(hmdom.getDomainName()))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vlan' data be lost, cause: 'owner' column is  not available.");
				continue;
			}
			vlanDTO.setOwner(hmdom);

			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				boolean bool = false;
				int count = 1;
				int globalValue = 1;
				for (SingleTableItem item : items) {
					if (id.equals(item.getRestoreId())) {
						if (SingleTableItem.TYPE_GLOBAL == item.getType()) {
							bool = true;
						}
						globalValue = item.getVlanId();
						if (singleItem.size() > 0) {
							int j = 0;
							for(SingleTableItem oneitem : singleItem) {
								if(oneitem.getVlanId() == globalValue) {
									j ++;
								}
							}
							if (j == singleItem.size()) {
								count = singleItem.size() + 1;
							}
						}
						singleItem.add(item);
					}
				}
				if (!bool) {
					SingleTableItem item = new SingleTableItem();
					if (count == singleItem.size()) {
						item.setVlanId(globalValue);
					} else {
						item.setVlanId(1);
					}
					item.setType(SingleTableItem.TYPE_GLOBAL);
					item.setDescription("global VLAN value");
					singleItem.add(item);
				}
				vlanDTO.setItems(singleItem);
			}
			vlan.add(vlanDTO);
		}

		return vlan.size() > 0 ? vlan : null;
	}

	/**
	 * Restore vlan table
	 *
	 * @return true if table of vlan restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreVlan()
	{
		try {
			List<Vlan> allVlan = getAllVlan();

			// the default value has been inserted before restore
			if(null != allVlan) {
				List<Long> lOldId = new ArrayList<Long>();

				for (Vlan vlan : allVlan) {
					lOldId.add(vlan.getId());
				}

				QueryUtil.restoreBulkCreateBos(allVlan);

				for(int i=0; i<allVlan.size(); i++)
				{
					AhRestoreNewMapTools.setMapVlan(lOldId.get(i), allVlan.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Restore Radius Operator Attribute table
	 *
	 * @return true if table of Radius Operator Attribute restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadiusOperatorNameAttr(){
		try{
			List<RadiusAttrs> allRadiusAttrs = getAllRadiusAttrs();

			// the default value has been inserted before restore
			if(null != allRadiusAttrs) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusAttrs radiusAttr : allRadiusAttrs) {
					lOldId.add(radiusAttr.getId());
				}

				QueryUtil.restoreBulkCreateBos(allRadiusAttrs);

				for(int i=0; i<allRadiusAttrs.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadiusAttrs(lOldId.get(i), allRadiusAttrs.get(i).getId());
				}
			}
		}catch(Exception e){
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<RadiusAttrs> getAllRadiusAttrs()throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_operator_attribute.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_operator_attribute");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<RadiusAttrs> radiusAttrs = new ArrayList<RadiusAttrs>();

		boolean isColPresent;
		String colName;
		List<SingleTableItem> items = getAllNetworkObjectItems(NETWORK_OBJECT_RADIUS_OPERATOR);
		RadiusAttrs radiusAttrsDTO;

		for (int i = 0; i < rowCount; i++){
			radiusAttrsDTO = new RadiusAttrs();

			/**
			 * Set objectname
			 */
			colName = "objectname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_operator_attribute", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_operator_attribute' data be lost, cause: 'objectname' column is not exist.");
				continue;
			}
			radiusAttrsDTO.setObjectName(name);


			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_operator_attribute", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			radiusAttrsDTO.setId(AhRestoreCommons.convertLong(id));

			if (name.equals("1")) {
				// set default RadiusAttrs object new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("objectname", name);
				HmBeParaUtil.getDefaultProfile(RadiusAttrs.class, map);
				if (null != radiusAttrsDTO) {
					AhRestoreNewMapTools.setMapVlan(radiusAttrsDTO.getId(), radiusAttrsDTO.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			radiusAttrsDTO.setDefaultFlag(false);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_operator_attribute", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain hmdom = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == hmdom || HmDomain.GLOBAL_DOMAIN.equals(hmdom.getDomainName()))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_operator_attribute' data be lost, cause: 'owner' column is  not available.");
				continue;
			}
			radiusAttrsDTO.setOwner(hmdom);

			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				boolean bool = false;
				int count = 1;
				String operatorname ="";
				for (SingleTableItem item : items) {
					if (id.equals(item.getRestoreId())) {
						if (SingleTableItem.TYPE_GLOBAL == item.getType()) {
							bool = true;
						}
						operatorname = item.getOperatorName();
						if (singleItem.size() > 0) {
							int j = 0;
							for(SingleTableItem oneitem : singleItem) {
								if(oneitem.getOperatorName().equals(operatorname)) {
									j ++;
								}
							}
							if (j == singleItem.size()) {
								count = singleItem.size() + 1;
							}
						}
						singleItem.add(item);
					}
				}
				if (!bool) {
					SingleTableItem item = new SingleTableItem();
					if (count == singleItem.size()) {
						item.setOperatorName(operatorname);
					} else {
						item.setOperatorName("");
					}
					item.setNameSpaceId(SingleTableItem.TYPE_NAMESPACE_REALM);
					item.setType(SingleTableItem.TYPE_GLOBAL);
					item.setDescription("global RADIUSOPERATORNAME  value");
					singleItem.add(item);
				}
				radiusAttrsDTO.setItems(singleItem);
			}
			radiusAttrs.add(radiusAttrsDTO);
		}
		return radiusAttrs.size() > 0 ? radiusAttrs : null;
	}

	/**
	 * Get all information from user_profile_attribute table
	 *
	 * @return List<UserProfileAttribute> all UserProfileAttribute BO
	 * @throws AhRestoreColNotExistException -
	 *             if user_profile_attribute.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing user_profile_attribute.xml.
	 */
	private static List<UserProfileAttribute> getAllUserAttribute() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of user_profile_attribute.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("user_profile_attribute");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<UserProfileAttribute> userAttribute = new ArrayList<UserProfileAttribute>();

		boolean isColPresent;
		String colName;
		List<SingleTableItem> items = null;
		UserProfileAttribute userAttributeDTO;
		// the main table has records
		if (rowCount > 0) {
			items = getAllNetworkObjectItems(NETWORK_OBJECT_ATTRIBUTE);
		}

		for (int i = 0; i < rowCount; i++)
		{
			userAttributeDTO = new UserProfileAttribute();

			/**
			 * Set attributename
			 */
			colName = "attributename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"user_profile_attribute", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'user_profile_attribute' data be lost, cause: 'attributename' column is not exist.");
				continue;
			}
			userAttributeDTO.setAttributeName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"user_profile_attribute", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			userAttributeDTO.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"user_profile_attribute", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'user_profile_attribute' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			userAttributeDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				for(SingleTableItem item : items)
				{
					if(id.equals(item.getRestoreId()))
					{
						singleItem.add(item);
					}
				}
				userAttributeDTO.setItems(singleItem);
			}
			userAttribute.add(userAttributeDTO);
		}

		return userAttribute.size() > 0 ? userAttribute : null;
	}

	/**
	 * Restore user_profile_attribute table
	 *
	 * @return true if table of user_profile_attribute restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreUserAttribute()
	{
		try {
			List<UserProfileAttribute> allAttribute = getAllUserAttribute();

			// the default value has been inserted before restore
			if(null != allAttribute) {
				List<Long> lOldId = new ArrayList<Long>();

				for (UserProfileAttribute attribute : allAttribute) {
					lOldId.add(attribute.getId());
				}

				QueryUtil.restoreBulkCreateBos(allAttribute);

				for(int i=0; i<allAttribute.size(); i++)
				{
					AhRestoreNewMapTools.setMapUserAttribute(lOldId.get(i), allAttribute.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from access_console_mac_filter table
	 *
	 * @return Map<String, Set<MacFilter>> all MacFilter
	 * @throws AhRestoreColNotExistException -
	 *             if access_console_mac_filter.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing access_console_mac_filter.xml.
	 */
	private static Map<String, Set<MacFilter>> getAllConsoleMacInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of access_console_mac_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("access_console_mac_filter");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacFilter>> ruleInfo = new HashMap<String, Set<MacFilter>>();

		boolean isColPresent;
		String colName;
		Set<MacFilter> singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set access_console_id
			 */
			colName = "access_console_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console_mac_filter", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			singleInfo = ruleInfo.get(id);

			/**
			 * Set mac_filter_id
			 */
			colName = "mac_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console_mac_filter", colName);
			String mac_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			if (!"".equals(mac_id)) {
				Long filId = AhRestoreNewMapTools.getMapMacFilter(AhRestoreCommons.convertLong(mac_id));
				if(null != filId) {
					MacFilter sourceMac = AhRestoreNewTools.CreateBoWithId(MacFilter.class, filId);
					if(singleInfo == null) {
						singleInfo = new HashSet<MacFilter>();
						singleInfo.add(sourceMac);
						ruleInfo.put(id, singleInfo);
					} else {
						singleInfo.add(sourceMac);
					}
				}
			}
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from access_console table
	 *
	 * @return List<AccessConsole> all AccessConsole BO
	 * @throws AhRestoreColNotExistException -
	 *             if access_console.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing access_console.xml.
	 */
	private static List<AccessConsole> getAllAccessConsole()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of access_console.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("access_console");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in access_console table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AccessConsole> policy = new ArrayList<AccessConsole>();
		List<AccessConsole> policyInfoNameEmpty = new ArrayList<AccessConsole>();

		boolean isColPresent;
		String colName;
		AccessConsole singlePolicy;
		Map<String, Set<MacFilter>> allRule = null;
		// the main table must has records
		if (rowCount > 0) {
			allRule = getAllConsoleMacInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new AccessConsole();

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"access_console", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'access_console' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));
			
			/**
			 * Set consolename
			 */
			colName = "consolename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'access_console' will reset the name, cause: 'consolename' column value is null.");
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, singlePolicy.getOwner().getId());
				if (dm!=null) {
					name=dm.getDomainName();
					singlePolicy.setConsoleName(name);
					policyInfoNameEmpty.add(singlePolicy);
				} else {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'access_console' will lost data, cause: 'consolename' column value is null. domain ID:" + ownerId);
					continue;
				}
			}
			singlePolicy.setConsoleName(name);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set consolemode
			 */
			colName = "consolemode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			short consolemode = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: AccessConsole.ACCESS_CONSOLE_MODE_DISABLE;
			singlePolicy.setConsoleMode(consolemode);

			/**
			 * Set maxclient
			 */
			colName = "maxclient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			short maxclient = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 2;
			singlePolicy.setMaxClient(maxclient);

			/**
			 * Set mgmtkey
			 */
			colName = "mgmtkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			short mgmtkey = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: SsidProfile.KEY_MGMT_OPEN;
			singlePolicy.setMgmtKey(mgmtkey);

			/**
			 * Set encryption
			 */
			colName = "encryption";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			short encryption = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: SsidProfile.KEY_ENC_NONE;
			singlePolicy.setEncryption(encryption);

			/**
			 * Set hidessid
			 */
			colName = "hidessid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String hidessid = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setHideSsid(AhRestoreCommons.convertStringToBoolean(hidessid));

			/**
			 * Set enabletelnet
			 */
			colName = "enabletelnet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String enabletelnet = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singlePolicy.setEnableTelnet(AhRestoreCommons.convertStringToBoolean(enabletelnet));

			/**
			 * Set asciikey
			 */
			colName = "asciikey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			String asciikey = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setAsciiKey(AhRestoreCommons
				.convertString(asciikey));

			/**
			 * Set defaultaction
			 */
			colName = "defaultaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"access_console", colName);
			short defaultaction = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: MacFilter.FILTER_ACTION_PERMIT;
			singlePolicy.setDefaultAction(defaultaction);

			if (null != allRule) {
				singlePolicy.setMacFilters(allRule.get(id));
			}
			policy.add(singlePolicy);
		}
		
		// fix bug 27502
		List<AccessConsole> policyNameExist = new ArrayList<AccessConsole>();
		if (!policyInfoNameEmpty.isEmpty()) {
			for(AccessConsole hm: policyInfoNameEmpty) {
				for(AccessConsole h: policy) {
					if (!hm.getId().equals(h.getId()) 
							&& hm.getConsoleName().equals(h.getConsoleName())
							&& hm.getOwner().getId().equals(h.getOwner().getId())) {
						policyNameExist.add(hm);
						break;
					}
				}
			}
		}
		if (!policyNameExist.isEmpty()) {
			int i=1;
			for(AccessConsole h: policyNameExist){
				boolean loopFlg=true;
				while (loopFlg) {
					boolean existFlg = false;
					String hName = h.getConsoleName();
					if (hName.length()>28) {
						hName=hName.substring(0, 28) + "_" + i++;
					} else {
						hName=hName + "_" + i++;
					}
					for(AccessConsole hif: policy) {
						if (hif.getConsoleName().equals(hName)
								&& hif.getOwner().getId().equals(h.getOwner().getId())) {
							existFlg = true;
							break;
						}
					}
					if (existFlg==false) {
						loopFlg=false;
						h.setConsoleName(hName);
					}
				}
			}
		}
		for(AccessConsole h: policyNameExist){
			for(AccessConsole hif : policy){
				if(hif.getId().equals(h.getId())){
					hif.setConsoleName(h.getConsoleName());
				}
			}
		}
		
		//end fix bug 27502

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore access_console table
	 *
	 * @return true if table of access_console restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreAccessConsole()
	{
		try {
			List<AccessConsole> allConsole = getAllAccessConsole();
			if (null != allConsole) {
				List<Long> lOldId = new ArrayList<Long>();

				for (AccessConsole console : allConsole) {
					lOldId.add(console.getId());
				}

				QueryUtil.restoreBulkCreateBos(allConsole);

				for(int i=0; i<allConsole.size(); i++)
				{
					AhRestoreNewMapTools.setMapAccessConsole(lOldId.get(i), allConsole.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from pppoe table
	 *
	 * @return List<AccessConsole> all PPPoE BO
	 * @throws AhRestoreColNotExistException -
	 *             if pppoe.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing pppoe.xml.
	 */
	private static List<PPPoE> getAllPPPoE()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of pppoe.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("pppoe");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in pppoe table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PPPoE> policy = new ArrayList<PPPoE>();

		boolean isColPresent;
		String colName;
		PPPoE singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new PPPoE();

			/**
			 * Set pppoeName
			 */
			colName = "pppoeName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'pppoe' data be lost, cause: 'pppoeName' column is not exist.");
				continue;
			}
			singlePolicy.setPppoeName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"pppoe", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'pppoe' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String username = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePolicy.setUsername(username);

			/**
			 * Set password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String password = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePolicy.setPassword(password);

			/**
			 * Set domain
			 */
			colName = "domain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			String domain = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePolicy.setDomain(domain);

			/**
			 * Set encryptionMethod
			 */
			colName = "encryptionMethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"pppoe", colName);
			short encryptionMethod = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: PPPoE.ENCRYPTION_METHOD_CHAP;
			singlePolicy.setEncryptionMethod(encryptionMethod);

			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}

	/**
	 * Restore pppoe table
	 *
	 * @return true if table of pppoe restoration is success, false
	 *         otherwise.
	 */
	public static boolean restorePPPoE()
	{
		try {
			List<PPPoE> allPPPoE = getAllPPPoE();
			if (null != allPPPoE) {
				List<Long> lOldId = new ArrayList<Long>();

				for (PPPoE pppoe : allPPPoE) {
					lOldId.add(pppoe.getId());
				}

				QueryUtil.restoreBulkCreateBos(allPPPoE);

				for(int i=0; i<allPPPoE.size(); i++)
				{
					AhRestoreNewMapTools.setMapPPPoE(lOldId.get(i), allPPPoE.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from PSE_PROFILE table
	 *
	 * @return List<AccessConsole> all PSE_PROFILE BO
	 * @throws AhRestoreColNotExistException -
	 *             if PSE_PROFILE.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing PSE_PROFILE.xml.
	 */
	private static List<PseProfile> getAllPseProfile()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "PSE_PROFILE";
		
		/**
		 * Check validation of PSE_PROFILE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in PSE_PROFILE table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PseProfile> policy = new ArrayList<PseProfile>();

		boolean isColPresent;
		String colName;
		PseProfile singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * check whether is default pse profile
			 */
			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean defaultflag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			
			singlePolicy = new PseProfile();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'PSE_PROFILE' data be lost, cause: 'name' column is not exist.");
				continue;
			}
			singlePolicy.setName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'PSE_PROFILE' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singlePolicy.setOwner(ownerDomain);

			if (defaultflag) {
				// set default ip object new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("owner.id", ownerDomain.getId());
				PseProfile newPse = HmBeParaUtil.getDefaultProfile(PseProfile.class, map);
				if (null != newPse) {
					AhRestoreNewMapTools.setMapPseProfile(singlePolicy.getId(), newPse.getId());
					continue;
				}
			}
			
			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName): "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));


			/**
			 * Set powerMode
			 */
			colName = "powerMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short powerMode = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: AhInterface.ETH_PSE_8023at;
			singlePolicy.setPowerMode(powerMode);
			
			/**
			 * Set priority
			 */
			colName = "priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short priority = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: PseProfile.PRIORITY_LOW;
			singlePolicy.setPriority(priority);
			
			/**
			 * Set thresholdPower
			 */
			colName = "thresholdPower";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			String thresholdPower = isColPresent ? xmlParser.getColVal(i, colName): String.valueOf(PseProfile.THRESHOLD_POWER_AT);
			singlePolicy.setThresholdPower(AhRestoreCommons
				.convertInt(thresholdPower));

			
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}
	
	/**
	 * Restore pseProfile table
	 *
	 * @return true if table of pseProfile restoration is success, false
	 *         otherwise.
	 */
	public static boolean restorePseProfile()
	{
		try {
			List<PseProfile> allPseProfile = getAllPseProfile();
			if (null != allPseProfile) {
				List<Long> lOldId = new ArrayList<Long>();

				for (PseProfile pseProfile : allPseProfile) {
					lOldId.add(pseProfile.getId());
				}

				QueryUtil.restoreBulkCreateBos(allPseProfile);

				for(int i=0; i<allPseProfile.size(); i++)
				{
					AhRestoreNewMapTools.setMapPseProfile(lOldId.get(i), allPseProfile.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from BONJOUR_SERVICE_CATEGORY table
	 *
	 * @return List<BonjourServiceCategory> all BonjourServiceCategory BO
	 * @throws AhRestoreColNotExistException -
	 *             if BONJOUR_SERVICE_CATEGORY.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing BONJOUR_SERVICE_CATEGORY.xml.
	 */
	private static List<BonjourServiceCategory> getAllBonjourServiceCategory()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of BONJOUR_SERVICE_CATEGORY.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("BONJOUR_SERVICE_CATEGORY");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_SERVICE_CATEGORY table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<BonjourServiceCategory> policy = new ArrayList<BonjourServiceCategory>();

		boolean isColPresent;
		String colName;
		BonjourServiceCategory singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new BonjourServiceCategory();

			/**
			 * Set serviceCategoryName
			 */
			colName = "serviceCategoryName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE_CATEGORY", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE_CATEGORY", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_SERVICE_CATEGORY' data be lost, cause: 'serviceCategoryName' column is not exist.");
				continue;
			//default value
			} else{
				String[] categorys = BonjourServiceCategory.getServiceCategory();
				boolean bool = false;
				for(String category: categorys){
					if(name.equals(category)){
						bool = true;
						break;
					}
				}
				if(bool){
					singlePolicy = QueryUtil.findBoByAttribute(BonjourServiceCategory.class, "serviceCategoryName", name);
					AhRestoreNewMapTools.setMapBonjourGatewayCategory(AhRestoreCommons.convertLong(id), singlePolicy.getId());
					continue;
				}
			}

			singlePolicy.setServiceCategoryName(name);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"BONJOUR_SERVICE_CATEGORY", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_SERVICE_CATEGORY' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));


			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}

	/**
	 * Restore BONJOUR_SERVICE_CATEGORY table
	 *
	 * @return true if table of BONJOUR_SERVICE_CATEGORY restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreBonjourServiceCategory()
	{
		try {
			List<BonjourServiceCategory> allBonjourServiceCategory = getAllBonjourServiceCategory();
			if (null != allBonjourServiceCategory) {
				List<Long> lOldId = new ArrayList<Long>();

				for (BonjourServiceCategory category : allBonjourServiceCategory) {
					lOldId.add(category.getId());
				}

				QueryUtil.restoreBulkCreateBos(allBonjourServiceCategory);

				for(int i=0; i<allBonjourServiceCategory.size(); i++)
				{
					AhRestoreNewMapTools.setMapBonjourGatewayCategory(lOldId.get(i), allBonjourServiceCategory.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from BONJOUR_SERVICE table
	 *
	 * @return List<BonjourService> all BonjourService BO
	 * @throws AhRestoreColNotExistException -
	 *             if BONJOUR_SERVICE.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing BONJOUR_SERVICE.xml.
	 */
	private static List<BonjourService> getAllBonjourService()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of BONJOUR_SERVICE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("BONJOUR_SERVICE");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_SERVICE table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<BonjourService> policy = new ArrayList<BonjourService>();

		boolean isColPresent;
		String colName;
		BonjourService singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new BonjourService();

			/**
			 * Set serviceName
			 */
			colName = "serviceName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_SERVICE' data be lost, cause: 'serviceName' column is not exist.");
				continue;
			}
			singlePolicy.setServiceName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE", colName);
			String type = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePolicy.setType(type);
			if(type.equals("")){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_SERVICE' data be lost, cause: 'type' column is not exist.");
				continue;
			//default value
			} else {
				String[] serviceTypes = BonjourService.getDefaultBonjouServiceType();
				boolean bool = false;
				for(String serviceType : serviceTypes){
					if(type.equals(serviceType)){
						bool = true;
						break;
					}
				}
				if(bool){
					singlePolicy = QueryUtil.findBoByAttribute(BonjourService.class, "type", type);
					AhRestoreNewMapTools.setMapBonjourService(AhRestoreCommons.convertLong(id), singlePolicy.getId());
					continue;
				}
			} 

			/**
			 * Set typeId
			 */
			colName = "typeid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_SERVICE", colName);
			int typeid = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 1;
			singlePolicy.setTypeId(typeid);

			if(typeid < 20){
			   if("*._*._tcp".equals(type) || "*._*._udp".equals(type) || "*._*._*".equals(type)){
					singlePolicy = QueryUtil.findBoByAttribute(BonjourService.class, "type", type.concat("."));
					AhRestoreNewMapTools.setMapBonjourService(AhRestoreCommons.convertLong(id), singlePolicy.getId());
					continue;
				}
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"BONJOUR_SERVICE", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_SERVICE' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set BONJOUR_SERVICE_CATEGRORY_ID
			 */
			colName = "BONJOUR_SERVICE_CATEGRORY_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"BONJOUR_SERVICE", colName);
			String categoryId = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (categoryId != null
					&& !(categoryId.trim().equals(""))
					&& !(categoryId.trim().equalsIgnoreCase("null"))) {
				Long categoryId_new = AhRestoreNewMapTools
						.getMapBonjourGatewayCategory(AhRestoreCommons
								.convertLong(categoryId));
				if (null != categoryId_new) {
					singlePolicy.setBonjourServiceCategory((AhRestoreNewTools
							.CreateBoWithId(BonjourServiceCategory.class,
									categoryId_new)));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new BonjourServiceCategory id mapping to old id:"
									+ categoryId);
				}
			}


			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}

	/**
	 * Restore BONJOUR_SERVICE table
	 *
	 * @return true if table of BONJOUR_SERVICE restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreBonjourService()
	{
		try {
			List<BonjourService> allBonjourService = getAllBonjourService();
			if (null != allBonjourService) {
				List<Long> lOldId = new ArrayList<Long>();

				for (BonjourService service : allBonjourService) {
					lOldId.add(service.getId());
				}

				QueryUtil.restoreBulkCreateBos(allBonjourService);

				for(int i=0; i<allBonjourService.size(); i++)
				{
					AhRestoreNewMapTools.setMapBonjourService(lOldId.get(i), allBonjourService.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from VLAN_GROUP table
	 *
	 * @return List<BonjourService> all VlanGroup BO
	 * @throws AhRestoreColNotExistException -
	 *             if VLAN_GROUP.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing VLAN_GROUP.xml.
	 */
	private static List<VlanGroup> getAllVlanGroup()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of VLAN_GROUP.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("VLAN_GROUP");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_SERVICE table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<VlanGroup> policy = new ArrayList<VlanGroup>();

		boolean isColPresent;
		String colName;
		VlanGroup singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new VlanGroup();

			/**
			 * Set vlanGroupName
			 */
			colName = "vlanGroupName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"VLAN_GROUP", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'VLAN_GROUP' data be lost, cause: 'vlanGroupName' column is not exist.");
				continue;
			}
			singlePolicy.setVlanGroupName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"VLAN_GROUP", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"VLAN_GROUP", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'VLAN_GROUP' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"VLAN_GROUP", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singlePolicy.setDescription(AhRestoreCommons.convertString(description));
			
			/**
			 * Set vlans
			 */
			colName = "vlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"VLAN_GROUP", colName);
			String vlans = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singlePolicy.setVlans(AhRestoreCommons.convertString(vlans));
			
			/**
			 * Set defaultflag
			 */
			singlePolicy.setDefaultFlag(false);

			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}
	
	/**
	 * Restore Vlan Group table
	 *
	 * @return true if table of VLAN_GROUP restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreVlanGroup()
	{
		try {
			List<VlanGroup> allVlanGroup = getAllVlanGroup();
			if (null != allVlanGroup) {
				List<Long> lOldId = new ArrayList<Long>();

				for (VlanGroup vlanGroup : allVlanGroup) {
					lOldId.add(vlanGroup.getId());
				}

				QueryUtil.restoreBulkCreateBos(allVlanGroup);

				for(int i=0; i<allVlanGroup.size(); i++)
				{
					AhRestoreNewMapTools.setMapVlanGroup(lOldId.get(i), allVlanGroup.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from BonjourGatewaySettings table
	 *
	 * @return List<BonjourGatewaySettings> all BonjourGatewaySettings BO
	 * @throws AhRestoreColNotExistException -
	 *             if BONJOUR_GATEWAY_SETTINGS.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing BONJOUR_GATEWAY_SETTINGS.xml.
	 */
	private static List<BonjourGatewaySettings> getAllBonjourGatewayProfile(List<HmUpgradeLog> lstLogBo)
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of BONJOUR_GATEWAY_SETTINGS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("BONJOUR_GATEWAY_SETTINGS");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_GATEWAY_SETTINGS table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}

		List<BonjourGatewaySettings> policy = new ArrayList<BonjourGatewaySettings>();
		boolean isColPresent;
		String colName;
		BonjourGatewaySettings singlePolicy;
		Map<String, List<BonjourActiveService>> allActiveService = getAllBonjourActiveService();
		
		Map<String, List<BonjourFilterRule>> allRemovedRules = new HashMap<>();
		Map<String, List<BonjourFilterRule>> allFilterRule = getAllFilterRule(allRemovedRules);

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new BonjourGatewaySettings();

			/**
			 * Set bonjourGwName
			 */
			colName = "bonjourGwName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_GATEWAY_SETTINGS", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_GATEWAY_SETTINGS' data be lost, cause: 'bonjourGwName' column is not exist.");
				continue;
			}
			singlePolicy.setBonjourGwName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_GATEWAY_SETTINGS", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"BONJOUR_GATEWAY_SETTINGS", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'BONJOUR_GATEWAY_SETTINGS' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			
			singlePolicy.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_GATEWAY_SETTINGS", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set vlans
			 */
			colName = "vlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_GATEWAY_SETTINGS", colName);
			String vlans = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "1-4094";
			singlePolicy.setVlans(vlans);

			List<BonjourActiveService> _allActiveServices = allActiveService.get(AhRestoreCommons.convertString(id));
			
			/**
			 * Set bonjour filter rule
			 */
			if(allFilterRule == null){ //before Edinburgh release
				try{
					if(BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_APPLE.equals(name)){
						String[] addService = new String[]{
								BonjourService.REMOTE_AUDIO_OUTPUT_SERVICES_TYPE,
								BonjourService.HOME_SHARING_SERVICES_TYPE,
								BonjourService.APPLE_TV_SERVICES_TYPE
								
						};
						for(String type : addService){
							BonjourService service = QueryUtil.findBoByAttribute(BonjourService.class, "type",type, new LazyObjLoader());
							BonjourActiveService activeService = new BonjourActiveService();
							activeService.setBonjourService(service);
							_allActiveServices.add(activeService);
						}
					}
				} catch(Exception e){
					AhRestoreDBTools
					.logRestoreMsg("getAllBonjourGatewayProfile error!");
				}
				
				singlePolicy.setRules(getBonjouFilterRule(_allActiveServices));
			} else {
				singlePolicy.setRules(allFilterRule.get(AhRestoreCommons.convertString(id)));
			}
			
			
			if(allRemovedRules.size() > 0){
				List<BonjourFilterRule> removedRules =allRemovedRules.get(AhRestoreCommons.convertString(id));
				if(removedRules != null){
					for(BonjourFilterRule rule : removedRules){
						try {
							// add upgrade log
							HmUpgradeLog upgradeLog = new HmUpgradeLog();
							upgradeLog.setFormerContent("In HiveOS 6.1r6 and earlier, Bonjour gateway filter rules required you to set an action to deny or permit services from one VLAN group to another.");
							upgradeLog.setPostContent("You no longer set deny or permit actions; the default behavior is to deny services, and any rule you add automatically permits the specified service.");
							upgradeLog.setRecommendAction("No action is required.");
							upgradeLog.setOwner(ownerDomain);
							upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
							upgradeLog.setAnnotation("Click to add an annotation");
							lstLogBo.add(upgradeLog);
							
						} catch (Exception e) {
							AhRestoreDBTools
							.logRestoreMsg("Create HmUpgradeLog error for bonjour filter rule, service id is"
									+ rule.getRuleId());
						}
					}
				}
			}
			
			/**
			 * Set bonjour active service
			 */
			singlePolicy.setBonjourActiveServices(_allActiveServices);

			
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;

	}
	
	private static List<BonjourFilterRule> getBonjouFilterRule(List<BonjourActiveService> services){
		List<BonjourFilterRule> rules = new ArrayList<BonjourFilterRule>();
		if(null != services){
			short i = 1;
			for(BonjourActiveService service : services){
				BonjourFilterRule rule = new BonjourFilterRule();
				rule.setBonjourService(service.getBonjourService());
				//rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
				rule.setRuleId(i);
				i++;
//				rule.setFromVlanGroup(null);
//				rule.setMetric(null);
//				rule.setToVlanGroup(null);
				rules.add(rule);
			}
		}

		return rules;
	}

	/**
	 * Get all information from BONJOUR_ACTIVE_SERVICE table
	 *
	 * @return Map<String, List<BonjourActiveService>>
	 * @throws AhRestoreColNotExistException -
	 *             if BONJOUR_ACTIVE_SERVICE.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing BONJOUR_ACTIVE_SERVICE.xml.
	 */
	private static Map<String, List<BonjourActiveService>> getAllBonjourActiveService() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of BONJOUR_ACTIVE_SERVICE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("BONJOUR_ACTIVE_SERVICE");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_ACTIVE_SERVICE table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<BonjourActiveService>> bonjourActiveServices = new HashMap<String, List<BonjourActiveService>>();

		boolean isColPresent;
		String colName;
		BonjourActiveService singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new BonjourActiveService();

			/**
			 * Set BONJOUR_GATEWAY_SETTINGS_ID
			 */
			colName = "BONJOUR_GATEWAY_SETTINGS_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"BONJOUR_ACTIVE_SERVICE", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set BONJOUR_SERVICE_ID
			 */
			colName = "BONJOUR_SERVICE_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"BONJOUR_ACTIVE_SERVICE", colName);
			String serviceId = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (serviceId != null
					&& !(serviceId.trim().equals(""))
					&& !(serviceId.trim().equalsIgnoreCase("null"))) {
				Long serviceId_new = AhRestoreNewMapTools
						.getMapBonjourService(AhRestoreCommons
								.convertLong(serviceId));
				if (null != serviceId_new) {
					singleInfo.setBonjourService((AhRestoreNewTools
							.CreateBoWithId(BonjourService.class,
									serviceId_new)));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new singleInfo id mapping to old id:"
									+ serviceId);
				}
			}


			List<BonjourActiveService> activeServiceList = bonjourActiveServices.get(id);
			if (null == activeServiceList) {
				activeServiceList = new ArrayList<BonjourActiveService>();
				activeServiceList.add(singleInfo);
				bonjourActiveServices.put(id, activeServiceList);
			} else {
				activeServiceList.add(singleInfo);
			}
		}

		return bonjourActiveServices;
	}
	
	/**
	 * Get all information from BONJOUR_FILTER_RULE table
	 *
	 * @return Map<String, List<BonjourFilterRule>>
	 * @throws AhRestoreColNotExistException -
	 *             if BONJOUR_FILTER_RULE.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing BONJOUR_FILTER_RULE.xml.
	 */
	private static Map<String, List<BonjourFilterRule>> getAllFilterRule(Map<String, List<BonjourFilterRule>> removedRules) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of BONJOUR_ACTIVE_SERVICE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("BONJOUR_FILTER_RULE");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in BONJOUR_FILTER_RULE table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<BonjourFilterRule>> bonjourFilterRules = new HashMap<String, List<BonjourFilterRule>>();

		boolean isColPresent;
		String colName;
		BonjourFilterRule singleInfo;
		String tableName = "BONJOUR_FILTER_RULE";

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new BonjourFilterRule();
			
			
			colName = "BONJOUR_FILTER_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (StringUtils.isBlank(id)) {
				continue;
			}
			
			/**
			 * Set ruleId
			 */
			colName = "ruleId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, colName, colName);
			String ruleId = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setRuleId((short)AhRestoreCommons.convertInt(ruleId));

			/**
			 * Set metric
			 */
			colName = "metric";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, colName, colName);
			String metric = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(RestoreHiveAp.restore_from_gotham_before){
				int iMetric = AhRestoreCommons.convertInt(metric);
				if(iMetric > 100){
					metric = "100";
				}
			}
			singleInfo.setMetric(AhRestoreCommons.convertString(metric));
			
			
			colName = "FROM_VLAN_GROUP_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long fromVlanGroupId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			VlanGroup fromVlanGroup = getVlanGroup(fromVlanGroupId);
			singleInfo.setFromVlanGroup(fromVlanGroup);
			
			colName = "TO_VLAN_GROUP_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long toVlanGroupId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			VlanGroup toVlanGroup = getVlanGroup(toVlanGroupId);
			singleInfo.setToVlanGroup(toVlanGroup);
			
			/**
			 * Set realmName
			 */
			colName = "realmName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, colName, colName);
			String realmName = isColPresent ? xmlParser.getColVal(i, colName) : null;
			singleInfo.setRealmName(AhRestoreCommons.convertString(realmName));
			
			/**
			 * Set BONJOUR_SERVICE_ID
			 */
			colName = "BONJOUR_SERVICE_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String serviceId = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (serviceId != null
					&& !(serviceId.trim().equals(""))
					&& !(serviceId.trim().equalsIgnoreCase("null"))) {
				Long serviceId_new = AhRestoreNewMapTools
						.getMapBonjourService(AhRestoreCommons
								.convertLong(serviceId));
				if (null != serviceId_new) {
					singleInfo.setBonjourService((AhRestoreNewTools
							.CreateBoWithId(BonjourService.class,
									serviceId_new)));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new singleInfo id mapping to old id:"
									+ serviceId);
				}
			}
			
			/**
			 * Set filterAction
			 */
			colName = "filterAction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, colName, colName);
			String filterAction = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			//singleInfo.setFilterAction((short)AhRestoreCommons.convertInt(filterAction));
			
			if(RESTORE_BEFORE_HOLLYWOOD_FLAG){
				if("2".equals(filterAction)){
					List<BonjourFilterRule> removedBonjourFilterList = removedRules.get(id);
					if (null == removedBonjourFilterList) {
						removedBonjourFilterList = new ArrayList<BonjourFilterRule>();
						removedBonjourFilterList.add(singleInfo);
						removedRules.put(id, removedBonjourFilterList);
					} else {
						removedBonjourFilterList.add(singleInfo);
					}
					
					continue;
				}
			}
			
			List<BonjourFilterRule> bonjourFilterList = bonjourFilterRules.get(id);
			if (null == bonjourFilterList) {
				bonjourFilterList = new ArrayList<BonjourFilterRule>();
				bonjourFilterList.add(singleInfo);
				bonjourFilterRules.put(id, bonjourFilterList);
			} else {
				bonjourFilterList.add(singleInfo);
			}
		}

		return bonjourFilterRules;
	}
	
	private static VlanGroup getVlanGroup(Long vlanGroupId){
		VlanGroup valnGroup = null;
		if(null != vlanGroupId) {
			Long newVlanGroupId= AhRestoreNewMapTools.getMapVlanGroup(vlanGroupId);
			if(null != newVlanGroupId) {
				valnGroup = AhRestoreNewTools.CreateBoWithId(VlanGroup.class, newVlanGroupId);
			}
		}
		return valnGroup;
	}
	/**
	 * Restore bonjourGatewayProfile table
	 *
	 * @return true if table of bonjourGatewayProfile restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreBonjourGatewayProfile()
	{
		try {
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<BonjourGatewaySettings> allBonjourGatewaySettings = getAllBonjourGatewayProfile(lstLogBo);
			if (null != allBonjourGatewaySettings) {
				List<Long> lOldId = new ArrayList<Long>();

				for (BonjourGatewaySettings bgsettings : allBonjourGatewaySettings) {
					lOldId.add(bgsettings.getId());
				}

				QueryUtil.restoreBulkCreateBos(allBonjourGatewaySettings);

				for(int i=0; i<allBonjourGatewaySettings.size(); i++)
				{
					AhRestoreNewMapTools.setMapBonjourGatewaySetting(lOldId.get(i), allBonjourGatewaySettings.get(i).getId());
				}
			}
			
			/*
			 * insert or update the data to database
			 */
			
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("restore bonjour gateway profile upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
			
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from dhcp_server_ippool table
	 *
	 * @return Map<String, List<DhcpServerIpPool>> all IP Pools
	 * @throws AhRestoreColNotExistException -
	 *             if dhcp_server_ippool.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing dhcp_server_ippool.xml.
	 */
	private static Map<String, List<DhcpServerIpPool>> getAllDhcpIpPoolInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of dhcp_server_ippool.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("dhcp_server_ippool");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<DhcpServerIpPool>> ruleInfo = new HashMap<String, List<DhcpServerIpPool>>();

		boolean isColPresent;
		String colName;
		List<DhcpServerIpPool> singleInfo;
		String startip;
		String endip;
		DhcpServerIpPool singlePool;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set vlan_dhcp_server_id
			 */
			colName = "vlan_dhcp_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_ippool", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			singleInfo = ruleInfo.get(id);

			/**
			 * Set startip
			 */
			colName = "startip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_ippool", colName);
			startip = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			/**
			 * Set endip
			 */
			colName = "endip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_ippool", colName);
			endip = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePool = new DhcpServerIpPool();
			singlePool.setStartIp(startip);
			singlePool.setEndIp(endip);

			if(singleInfo == null) {
				singleInfo = new ArrayList<DhcpServerIpPool>();
				singleInfo.add(singlePool);
				ruleInfo.put(id, singleInfo);
			} else {
				singleInfo.add(singlePool);
			}
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from dhcp_server_custom table
	 *
	 * @return Map<String, List<DhcpServerOptionsCustom>> all DHCP custom options
	 * @throws AhRestoreColNotExistException -
	 *             if dhcp_server_custom.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing dhcp_server_custom.xml.
	 */
	private static Map<String, List<DhcpServerOptionsCustom>> getAllDhcpCustomInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of dhcp_server_custom.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("dhcp_server_custom");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<DhcpServerOptionsCustom>> ruleInfo = new HashMap<String, List<DhcpServerOptionsCustom>>();

		boolean isColPresent;
		String colName;
		List<DhcpServerOptionsCustom> singleInfo;
		DhcpServerOptionsCustom singleCustom;

		overloop:
		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set vlan_dhcp_server_id
			 */
			colName = "vlan_dhcp_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_custom", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			singleInfo = ruleInfo.get(id);
			singleCustom = new DhcpServerOptionsCustom();

			/**
			 * Set number
			 */
			colName = "number";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_custom", colName);
			short number = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 180;
			for (int limitNumber : DhcpServerOptionsCustom.CUSTOM_OPTION_LIMIT) {
				if (number == limitNumber) {
					continue overloop;
				}
			}
			singleCustom.setNumber(number);

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_custom", colName);
			short type = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
			singleCustom.setType(type);

			/**
			 * Set value
			 */
			colName = "value";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dhcp_server_custom", colName);
			String value = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName))
				: "0";
			singleCustom.setValue(value);

			if(singleInfo == null) {
				singleInfo = new ArrayList<DhcpServerOptionsCustom>();
				singleInfo.add(singleCustom);
				ruleInfo.put(id, singleInfo);
			} else {
				singleInfo.add(singleCustom);
			}
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from vlan_dhcp_server table
	 *
	 * @return List<VlanDhcpServer> all VlanDhcpServer BO
	 * @throws AhRestoreColNotExistException -
	 *             if vlan_dhcp_server.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing vlan_dhcp_server.xml.
	 */
	private static List<VlanDhcpServer> getAllVlanDhcpServer()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vlan_dhcp_server.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("vlan_dhcp_server");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in vlan_dhcp_server table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<VlanDhcpServer> policy = new ArrayList<VlanDhcpServer>();

		boolean isColPresent;
		String colName;
		VlanDhcpServer singlePolicy;
		Map<String, List<DhcpServerIpPool>> allPools = null;
		Map<String, List<DhcpServerOptionsCustom>> allCustoms = null;
		// the main table must has records
		if (rowCount > 0) {
			allPools = getAllDhcpIpPoolInfo();
			allCustoms = getAllDhcpCustomInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new VlanDhcpServer();

			/**
			 * Set profilename
			 */
			colName = "profilename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vlan_dhcp_server' data be lost, cause: 'profilename' column is not exist.");
				continue;
			}
			singlePolicy.setProfileName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"vlan_dhcp_server", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vlan_dhcp_server' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);

			singlePolicy.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set dhcpmgt
			 */
			colName = "dhcpmgt";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			short dhcpmgt = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 0;
			singlePolicy.setDhcpMgt(dhcpmgt);

			/**
			 * Set interfaceip
			 */
			colName = "interfaceip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String interfaceip = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setInterfaceIp(AhRestoreCommons.convertString(interfaceip));

			/**
			 * Set interfacenet
			 */
			colName = "interfacenet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String interfacenet = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setInterfaceNet(AhRestoreCommons.convertString(interfacenet));

			/**
			 * Set intervlan
			 */
			colName = "intervlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			int intervlan = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 1;
			singlePolicy.setInterVlan(intervlan);

			/**
			 * Set enableping
			 */
			colName = "enableping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			boolean enableping = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singlePolicy.setEnablePing(enableping);

			/**
			 * Set typeflag
			 */
			colName = "typeflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			short typeflag = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: VlanDhcpServer.ENABLE_DHCP_SERVER;
			singlePolicy.setTypeFlag(typeflag);

			/**
			 * Set authoritative
			 */
			colName = "authoritative";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			boolean authoritative = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singlePolicy.setAuthoritative(VlanDhcpServer.ENABLE_DHCP_RELAY == typeflag || authoritative);

			/**
			 * Set enablearp
			 */
			colName = "enablearp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			boolean enablearp = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singlePolicy.setEnableArp(enablearp);

			/**
			 * Set iphelper1
			 */
			colName = "iphelper1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String iphelper1 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setIpHelper1(AhRestoreCommons.convertString(iphelper1));

			/**
			 * Set iphelper2
			 */
			colName = "iphelper2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String iphelper2 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setIpHelper2(AhRestoreCommons.convertString(iphelper2));

			/**
			 * Set defaultgateway
			 */
			colName = "defaultgateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String defaultgateway = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDefaultGateway(AhRestoreCommons.convertString(defaultgateway));

			/**
			 * Set leasetime
			 */
			colName = "leasetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String leasetime = isColPresent ? xmlParser.getColVal(i, colName)
				: "86400";
			singlePolicy.setLeaseTime(AhRestoreCommons.convertString(leasetime));

			/**
			 * Set dhcpnetmask
			 */
			colName = "dhcpnetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String dhcpnetmask = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDhcpNetmask(AhRestoreCommons.convertString(dhcpnetmask));

			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String domainname = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDomainName(AhRestoreCommons.convertString(domainname));

			/**
			 * Set mtu
			 */
			colName = "mtu";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String mtu = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setMtu(AhRestoreCommons.convertString(mtu));

			/**
			 * Set dnsserver1
			 */
			colName = "dnsserver1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String dnsserver1 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDnsServer1(AhRestoreCommons.convertString(dnsserver1));

			/**
			 * Set dnsserver2
			 */
			colName = "dnsserver2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String dnsserver2 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDnsServer2(AhRestoreCommons.convertString(dnsserver2));

			/**
			 * Set dnsserver3
			 */
			colName = "dnsserver3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String dnsserver3 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDnsServer3(AhRestoreCommons.convertString(dnsserver3));

			/**
			 * Set ntpserver1
			 */
			colName = "ntpserver1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String ntpserver1 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setNtpServer1(AhRestoreCommons.convertString(ntpserver1));

			/**
			 * Set ntpserver2
			 */
			colName = "ntpserver2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String ntpserver2 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setNtpServer2(AhRestoreCommons.convertString(ntpserver2));

			/**
			 * Set pop3
			 */
			colName = "pop3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String pop3 = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setPop3(AhRestoreCommons.convertString(pop3));

			/**
			 * Set wins
			 */
			colName = "wins";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			if (isColPresent) {
				singlePolicy.setWins1(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));
			} else {
				/**
				 * Set wins1 from 3.2r2
				 */
				colName = "wins1";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"vlan_dhcp_server", colName);
				String wins1 = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
				singlePolicy.setWins1(AhRestoreCommons.convertString(wins1));

				/**
				 * Set wins2 from 3.2r2
				 */
				colName = "wins2";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"vlan_dhcp_server", colName);
				String wins2 = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
				singlePolicy.setWins2(AhRestoreCommons.convertString(wins2));
			}

			/**
			 * Set smtp
			 */
			colName = "smtp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String smtp = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setSmtp(AhRestoreCommons.convertString(smtp));

			/**
			 * Set logsrv
			 */
			colName = "logsrv";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			String logsrv = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setLogsrv(AhRestoreCommons.convertString(logsrv));

			/**
			 * Set natsupport
			 */
			colName = "natsupport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"vlan_dhcp_server", colName);
			boolean natsupport = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singlePolicy.setNatSupport(natsupport);

			if (typeflag == VlanDhcpServer.ENABLE_DHCP_SERVER) {
				if (null != allPools) {
					singlePolicy.setIpPools(allPools.get(id));
				}
				if (null != allCustoms) {
					singlePolicy.setCustoms(allCustoms.get(id));
				}

				// remove HiveManager IP or Name to custom from 3.2r2
				/**
				 * Set ipaddress_id
				 */
				colName = "ipaddress_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"vlan_dhcp_server", colName);
				DhcpServerOptionsCustom custom = null;
				DhcpServerOptionsCustom custom1 = null;
				if (isColPresent) {
					//Long ipaddress_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				//	if (!"".equals(ipaddress_id)) {
						Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)));
						if(null != newIp) {
							IpAddress ip = QueryUtil.findBoById(IpAddress.class, newIp, new LazyObjLoader());
							custom = new DhcpServerOptionsCustom();
							custom.setNumber(IpAddress.TYPE_HOST_NAME == ip.getTypeFlag() ? (short)225 : (short)226);
							custom.setType(IpAddress.TYPE_HOST_NAME == ip.getTypeFlag() ? DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING :
								DhcpServerOptionsCustom.CUSTOM_TYYPE_IP);
							for (SingleTableItem item : ip.getItems()) {
								if (SingleTableItem.TYPE_GLOBAL == item.getType()) {
									custom.setValue(item.getIpAddress());
									break;
								}
							}
						}
				//	}
				} else {
					/**
					 * Set hivemanagername
					 */
					colName = "hivemanagername";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"vlan_dhcp_server", colName);
					String hivemanagername = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
					if (!"".equals(hivemanagername)) {
						custom = new DhcpServerOptionsCustom();
						custom.setNumber((short)225);
						custom.setType(DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING);
						custom.setValue(hivemanagername.length() > 32 ? hivemanagername.substring(0, 32) : hivemanagername);
					}

					/**
					 * Set hivemanagerip
					 */
					colName = "hivemanagerip";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"vlan_dhcp_server", colName);
					String hivemanagerip = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
					if (!"".equals(hivemanagerip)) {
						custom1 = new DhcpServerOptionsCustom();
						custom1.setNumber((short)226);
						custom1.setType(DhcpServerOptionsCustom.CUSTOM_TYYPE_IP);
						custom1.setValue(hivemanagerip);
					}
				}
				List<DhcpServerOptionsCustom> customs = singlePolicy.getCustoms();
				if (null == customs) {
					customs = new ArrayList<DhcpServerOptionsCustom>();
				}
				if (null != custom) {
					customs.add(custom);
				}
				if (null != custom1) {
					customs.add(custom1);
				}
				if (null == singlePolicy.getCustoms() && customs.size() > 0) {
					singlePolicy.setCustoms(customs);
				}
			}
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore vlan_dhcp_server table
	 *
	 * @return true if table of vlan_dhcp_server restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreVlanDhcpServer()
	{
		try {
			List<VlanDhcpServer> allDhcps = getAllVlanDhcpServer();
			if (null != allDhcps) {
				List<Long> lOldId = new ArrayList<Long>();

				for (VlanDhcpServer dhcp : allDhcps) {
					lOldId.add(dhcp.getId());
				}

				QueryUtil.restoreBulkCreateBos(allDhcps);

				for(int i=0; i<allDhcps.size(); i++)
				{
					AhRestoreNewMapTools.setMapVlanDhcpServer(lOldId.get(i), allDhcps.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from lldp cdp profile table
	 *
	 * @return List<LLDPCDPProfile> all AccessConsole BO
	 * @throws AhRestoreColNotExistException -
	 *             if access_console.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing access_console.xml.
	 */
	private static List<LLDPCDPProfile> getAllLLDPCDPProfiles()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of lldpcdpprofile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("lldpcdpprofile");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in lldpcdpprofile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LLDPCDPProfile> profileList = new ArrayList<LLDPCDPProfile>();
		List<LLDPCDPProfile> profileListNameEmpty = new ArrayList<LLDPCDPProfile>();
		
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			LLDPCDPProfile profile = new LLDPCDPProfile();

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"lldpcdpprofile", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'lldpcdpprofile' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			profile.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			profile.setId(AhRestoreCommons.convertLong(id));
			
			/**
			 * Set profileName
			 */
			colName = "profileName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'lldpcdpprofile' will reset the hive name, cause: 'profileName' column value is null.");
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, profile.getOwner().getId());
				if (dm!=null) {
					name=dm.getDomainName();
					profile.setProfileName(name);
					profileListNameEmpty.add(profile);
				} else {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'lldpcdpprofile' will lost data, cause: 'profileName' column value is null. domain ID:" + ownerId);
				continue;
			}
			}
			profile.setProfileName(name);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			profile.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set enableLLDP
			 */
			colName = "enableLLDP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableLLDP = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableLLDP(enableLLDP);
			
			/**
			 * Set enableLLDPHostPorts
			 */
			colName = "enableLLDPHostPorts";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableLLDPHostPorts = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableLLDPHostPorts(enableLLDPHostPorts);
			
			/**
			 * Set enableLLDPNonHostPorts
			 */
			colName = "enableLLDPNonHostPorts";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableLLDPNonHostPorts = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableLLDPNonHostPorts(enableLLDPNonHostPorts);
			
			/**
			 * Set enableCDPHostPorts
			 */
			colName = "enableCDPHostPorts";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableCDPHostPorts = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableCDPHostPorts(enableCDPHostPorts);
			
			/**
			 * Set enableCDPNonHostPorts
			 */
			colName = "enableCDPNonHostPorts";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableCDPNonHostPorts = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableCDPNonHostPorts(enableCDPNonHostPorts);

			/**
			 * Set enableCDP
			 */
			colName = "enableCDP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			boolean enableCDP = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setEnableCDP(enableCDP);

			/**
			 * Set lldpReceiveOnly
			 */
			colName = "lldpReceiveOnly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "lldpcdpprofile", colName);
			boolean lldpReceiveOnly = isColPresent && AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName));
			profile.setLldpReceiveOnly(lldpReceiveOnly);

			/**
			 * Set lldpMaxEntries
			 */
			colName = "lldpMaxEntries";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int lldpMaxEntries = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 64;
			profile.setLldpMaxEntries(lldpMaxEntries);

			/**
			 * Set lldpHoldTime
			 */
			colName = "lldpHoldTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int lldpHoldTime = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 90;
			profile.setLldpHoldTime(lldpHoldTime);

			/**
			 * Set lldpTimer
			 */
			colName = "lldpTimer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int lldpTimer = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 30;
			profile.setLldpTimer(lldpTimer);

			/**
			 * Set lldpMaxPower
			 */
			colName = "lldpMaxPower";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int lldpMaxPower = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 154;
			profile.setLldpMaxPower(lldpMaxPower);

			/**
			 * Set cdpMaxEntries
			 */
			colName = "cdpMaxEntries";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int cdpMaxEntries = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 64;
			profile.setCdpMaxEntries(cdpMaxEntries);
			
			/**
			 * Set delayTime
			 */
			colName = "delayTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int delayTime = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 2;
			profile.setDelayTime(delayTime);
			
			/**
			 * Set repeatCount
			 */
			colName = "repeatCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"lldpcdpprofile", colName);
			int repeatCount = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: 3;
			profile.setRepeatCount(repeatCount);

			profileList.add(profile);
		}
		
		// fix bug 27502
		List<LLDPCDPProfile> profileListNameExist = new ArrayList<LLDPCDPProfile>();
		if (!profileListNameEmpty.isEmpty()) {
			for(LLDPCDPProfile hm: profileListNameEmpty) {
				for(LLDPCDPProfile h: profileList) {
					if (!hm.getId().equals(h.getId()) 
							&& hm.getProfileName().equals(h.getProfileName())
							&& hm.getOwner().getId().equals(h.getOwner().getId())) {
						profileListNameExist.add(hm);
						break;
					}
				}
			}
		}
		if (!profileListNameExist.isEmpty()) {
			int i=1;
			for(LLDPCDPProfile h: profileListNameExist){
				boolean loopFlg=true;
				while (loopFlg) {
					boolean existFlg = false;
					String hName = h.getProfileName();
					if (hName.length()>28) {
						hName=hName.substring(0, 28) + "_" + i++;
					} else {
						hName=hName + "_" + i++;
					}
					for(LLDPCDPProfile hif: profileList) {
						if (hif.getProfileName().equals(hName)
								&& hif.getOwner().getId().equals(h.getOwner().getId())) {
							existFlg = true;
							break;
						}
					}
					if (existFlg==false) {
						loopFlg=false;
						h.setProfileName(hName);
					}
				}
			}
		}
		for(LLDPCDPProfile h: profileListNameExist){
			for(LLDPCDPProfile hif : profileList){
				if(hif.getId().equals(h.getId())){
					hif.setProfileName(h.getProfileName());
				}
			}
		}
		
		//end fix bug 27502

		return profileList.size() > 0 ? profileList : null;
	}

	/**
	 * Restore lldp/cdp profile
	 *
	 * @return true if table of lldp/cdp profile restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreLLDPCDPProfile()
	{
		try {
			List<LLDPCDPProfile> profileList = getAllLLDPCDPProfiles();
			if (null != profileList) {
				List<Long> lOldId = new ArrayList<Long>();

				for (LLDPCDPProfile profile : profileList) {
					lOldId.add(profile.getId());
				}

				QueryUtil.restoreBulkCreateBos(profileList);

				for(int i=0; i<profileList.size(); i++)
				{
					AhRestoreNewMapTools.setLLDPCDPProfileMap(lOldId.get(i), profileList.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from locationclientwatch table
	 *
	 * @return List<LocationClientWatch> all LocationServer BO
	 * @throws AhRestoreColNotExistException -
	 *             if location_server.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing location_server.xml.
	 */
	private static List<LocationClientWatch> getAllLocationWatchList()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of locationclientwatch.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("locationclientwatch");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in locationclientwatch table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LocationClientWatch> list = new ArrayList<LocationClientWatch>();
		List<SingleTableItem> items = getAllNetworkObjectItems(LOCATIONCLIENT_ITEM);

		for (int i = 0; i < rowCount; i++)
		{
			LocationClientWatch clientWatch = new LocationClientWatch();

			/**
			 * Set defaultFlag
			 */
			String colName = "defaultFlag";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "locationclientwatch",
					colName);
			boolean defaultFlag = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"locationclientwatch", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			clientWatch.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"locationclientwatch", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'locationclientwatch' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			clientWatch.setOwner(ownerDomain);

			if (defaultFlag) {
				// set default ip object new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("owner.id", ownerDomain.getId());
				LocationClientWatch newLoc = HmBeParaUtil.getDefaultProfile(LocationClientWatch.class, map);
				if (null != newLoc) {
					AhRestoreNewMapTools.setMapIpAdddress(clientWatch.getId(), newLoc.getId());
					continue;
				}
			}
			clientWatch.setDefaultFlag(defaultFlag);

			/**
			 * Set name
			 */
			 colName = "name";
			 isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"locationclientwatch", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.trim().equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'locationclientwatch' data be lost, cause: 'name' column is not exist.");
				continue;
			}
			clientWatch.setName(name);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"locationclientwatch", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			clientWatch.setDescription(AhRestoreCommons
				.convertString(description));

			// items
			if(null != items)
			{
				List<SingleTableItem> singleItem = new ArrayList<SingleTableItem>();
				for(SingleTableItem item : items)
				{
					if(id.equals(item.getRestoreId()))
					{
						singleItem.add(item);
					}
				}
				clientWatch.setItems(singleItem);
			}

			list.add(clientWatch);
		}

		return list.size() > 0 ? list : null;
	}

	/**
	 * Restore locationclientwatch table
	 *
	 * @return true if table of location_server restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreLocationClientWatch()
	{
		try {
			List<LocationClientWatch> watchList = getAllLocationWatchList();
			if (null != watchList) {
				List<Long> lOldId = new ArrayList<Long>();

				for (LocationClientWatch watch : watchList) {
					lOldId.add(watch.getId());
				}

				QueryUtil.restoreBulkCreateBos(watchList);

				for(int i=0; i<watchList.size(); i++)
				{
					AhRestoreNewMapTools.setMapLocationClientWatch(lOldId.get(i), watchList.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from os_object_version table
	 *
	 * @return Map<String, List<OsObjectVersion>>
	 * @throws AhRestoreColNotExistException -
	 *             if os_object_version.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing os_object_version.xml.
	 */
	private static Map<String, List<OsObjectVersion>> getAllOsObjectVersion() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of os_object_version.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("os_object_version");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in os_object_version table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<OsObjectVersion>> osVersions = new HashMap<String, List<OsObjectVersion>>();

		boolean isColPresent;
		String colName;
		OsObjectVersion singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new OsObjectVersion();

			/**
			 * Set os_object_id
			 */
			colName = "os_object_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set osversion
			 */
			colName = "osversion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String osversion = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setOsVersion(AhRestoreCommons.convertString(osversion));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set option55
			 */
			colName = "option55";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String option55 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(option55));

			List<OsObjectVersion> versionList = osVersions.get(id);
			if (null == versionList) {
				versionList = new ArrayList<OsObjectVersion>();
				versionList.add(singleInfo);
				osVersions.put(id, versionList);
			} else {
				versionList.add(singleInfo);
			}
		}

		return osVersions;
	}

	/**
	 * Get all information from os_object_version_dhcp table
	 *
	 * @return Map<String, List<OsObjectVersion>>
	 * @throws AhRestoreColNotExistException -
	 *             if os_object_version_dhcp.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing os_object_version_dhcp.xml.
	 */
	private static Map<String, List<OsObjectVersion>> getAllDhcpOsObjectVersion() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of os_object_version_dhcp.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("os_object_version_dhcp");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in os_object_version_dhcp table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<OsObjectVersion>> osVersions = new HashMap<String, List<OsObjectVersion>>();

		boolean isColPresent;
		String colName;
		OsObjectVersion singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new OsObjectVersion();

			/**
			 * Set os_object_id
			 */
			colName = "os_object_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set osversion
			 */
			colName = "osversion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String osversion = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setOsVersion(AhRestoreCommons.convertString(osversion));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set option55
			 */
			colName = "option55";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object_version", colName);
			String option55 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setOption55(AhRestoreCommons.convertString(option55));

			List<OsObjectVersion> versionList = osVersions.get(id);
			if (null == versionList) {
				versionList = new ArrayList<OsObjectVersion>();
				versionList.add(singleInfo);
				osVersions.put(id, versionList);
			} else {
				versionList.add(singleInfo);
			}
		}

		return osVersions;
	}

	/**
	 * Get all information from os_object table
	 *
	 * @return List<OsObject> all OsObject BO
	 * @throws AhRestoreColNotExistException -
	 *             if os_object.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing os_object.xml.
	 */
	private static List<OsObject> getAllOsObject() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of os_object.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("os_object");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}

		List<OsObject> osList = new ArrayList<OsObject>();
		boolean isColPresent;
		String colName;
		Map<String, List<OsObjectVersion>> allversion = getAllOsObjectVersion();
		Map<String, List<OsObjectVersion>> allversiondhcp = getAllDhcpOsObjectVersion();

		OsObject osObj;

		overlap:
		for (int i = 0; i < rowCount; i++)
		{
			osObj = new OsObject();

			/**
			 * Set osname
			 */
			colName = "osname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_object' data be lost, cause: 'osname' column is not exist.");
				continue;
			}
			osObj.setOsName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_object", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			osObj.setId(AhRestoreCommons.convertLong(id));

			for (String osName : BeParaModule.DEFAULT_OS_OBJECTS_NAMES) {
				if (osName.equals(name)) {
					// set default os object new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("osName", name);
					OsObject newOb = HmBeParaUtil.getDefaultProfile(OsObject.class, map);
					if (null != newOb) {
						AhRestoreNewMapTools.setMapOsObject(osObj.getId(), newOb.getId());
					}
					continue overlap;
				}
			}
			/**
			 * Set defaultflag
			 */
			osObj.setDefaultFlag(false);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"os_object", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_object' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == owner) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_object' data be lost, cause: 'owner' column is not available.");
				continue;
			} else {
				osObj.setOwner(owner);
			}

			/**
			 * Set OsVersion Info
			 */
			osObj.setItems(allversion.get(AhRestoreCommons.convertString(id)));

			/**
			 * Set OsVersion Info
			 */
			if (allversiondhcp == null) { // Before Dakar release

			} else {
				osObj.setDhcpItems(allversiondhcp.get(AhRestoreCommons.convertString(id)));
			}




			osList.add(osObj);
		}

		return osList.isEmpty() ? null : osList;
	}

	/**
	 * Restore os_object table
	 *
	 * @return true if table of os_object restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreOsObject()
	{
		try {
			List<OsObject> allOs = getAllOsObject();

			if(null != allOs) {
				List<Long> lOldId = new ArrayList<Long>();

				for (OsObject os : allOs) {
					lOldId.add(os.getId());
				}

				QueryUtil.restoreBulkCreateBos(allOs);

				for(int i=0; i<allOs.size(); i++)
				{
					AhRestoreNewMapTools.setMapOsObject(lOldId.get(i), allOs.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from os_version table
	 *
	 * @return List<OsObject> all OsVersion BO
	 * @throws AhRestoreColNotExistException -
	 *             if os_version.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing os_version.xml.
	 */
	private static List<OsVersion> getAllOsVersion() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of os_object.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("os_version");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}

		List<OsVersion> osList = new ArrayList<OsVersion>();
		boolean isColPresent;
		String colName;
		OsVersion osVer;

		for (int i = 0; i < rowCount; i++)
		{
			osVer = new OsVersion();

			/**
			 * Set osVersion
			 */
			colName = "osVersion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_version", colName);
			String osVersion = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (osVersion.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_version' data be lost, cause: 'osVersion' column is not exist.");
				continue;
			}
			osVer.setOsVersion(osVersion);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_version", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			osVer.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"os_version", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_version' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == owner) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'os_version' data be lost, cause: 'owner' column is not available.");
				continue;
			} else {
				osVer.setOwner(owner);
			}
			
			/**
			 * Set option55
			 */
			colName = "option55";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"os_version", colName);
			String option55 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			osVer.setOption55(AhRestoreCommons.convertString(option55));


			osList.add(osVer);
		}

		return osList.isEmpty() ? null : osList;
	}

	/**
	 * Restore application table
	 * @return true if table of application restoration is success, false otherwise
	 */
	public static boolean restoreApplication() {
		try {
			List<Application> allApp = getAllApplication();
			
			if(null != allApp) {
				List<Long> lOldId = new ArrayList<Long>();
				Map<Integer, Long> oldMapAppCode = new HashMap<Integer, Long>();
			
				for(Application app : allApp) {
					lOldId.add(app.getId());
					oldMapAppCode.put(app.getAppCode(), app.getId());
				}
				
				QueryUtil.restoreBulkCreateBos(allApp);
				
				for(int i = 0; i < allApp.size(); i++) {
					AhRestoreNewMapTools.setMapApplication(lOldId.get(i), allApp.get(i).getId());
				}
			}	
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from application table
	 * @return List<Application> all application BO
	 * @throw AhRestoreColNotExistException -
	 * 				if application.xml is not exist.
	 * @throw AhRestoreException -
	 * 				if error in parsing application.xml
	 */
	private static List<Application> getAllApplication() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
			
		//Check validation of application.xml
		String tableName = "application";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if(!restoreRet) {
			return null;
		}
		
		int rowCount = xmlParser.getRowCount();
		if(0 >= rowCount){
			return null;
		}
		
		Map<Integer, Long> mapAppCode = new HashMap<Integer, Long>();
		try {
			List<?> allAppId = QueryUtil.executeNativeQuery("select application.id, application.appcode from application, hm_domain where application.owner= hm_domain.id and hm_domain.domainname='" + HmDomain.GLOBAL_DOMAIN +"'");
			for(Object obj : allAppId) {
				Object[] element = (Object[])obj;
				if(null != element[0] && null != element[1]) {
					mapAppCode.put((Integer)element[1], ((BigInteger)element[0]).longValue());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return null;
		}
		
		List<Application> appList = new ArrayList<Application>();
		boolean isColPresent;
		String colName;
		Application application;
		
		for (int i = 0; i < rowCount; i++) {
			application = new Application();
			
			//set defaultflag
			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String defaultFlag = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			/*if(defaultFlag.equals("")){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause:'defaultFlag' column is not exist.");
				continue;
			}*/
			application.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultFlag));
				
			//set appCode
			colName = "appCode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, 
					tableName, colName);
			String appCode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(appCode.equals("")) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'appCode' column is not exist.");
				continue;
			}
			application.setAppCode(AhRestoreCommons.convertInt(appCode));
					
			//set appName
			colName = "appName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String appName = isColPresent ? xmlParser.getColVal(i, colName): "";
			application.setAppName(appName);
			
			//set id
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			application.setId(AhRestoreCommons.convertLong(id));
					
			//judge default flag
			if(application.isDefaultFlag()) {						
				AhRestoreNewMapTools.setMapApplication(application.getId(), mapAppCode.get(application.getAppCode()));
				continue;
			}
		
			//set owner
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == owner) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			} else {
				application.setOwner(owner);
			}

			appList.add(application);
		}
		return appList.isEmpty() ? null : appList;
	}
	
	/**
	 * Restore application profile table
	 * @return true if table of applicationProfile restoration is success, false otherwise
	 */
	public static boolean restoreApplicationProfile() {
		try {
			List<ApplicationProfile> allAppProfile = getAllApplicationProfile();
			
			if(null != allAppProfile) {
				List<Long> lOldId = new ArrayList<Long>();
				Map<String, Long> oldMapProfile = new HashMap<String, Long>();
				
				for(ApplicationProfile appProfile : allAppProfile) {
					lOldId.add(appProfile.getId());
					oldMapProfile.put(appProfile.getProfileName(), appProfile.getId());
				}
								
				QueryUtil.restoreBulkCreateBos(allAppProfile);
				
				
				for(int i = 0; i < allAppProfile.size(); i++) {
					AhRestoreNewMapTools.setMapAppProfile(lOldId.get(i),allAppProfile.get(i).getId());
				}			
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private static Map<Long, Set<Application>> getAppProflie_App() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		String tableName = "appprofile_app";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if(!restoreRet)
			return null;
		
		int rowCount = xmlParser.getRowCount();
		if(0 >= rowCount)
			return null;
		
		Map<Long, Set<Application>> mapAppProfile_App = new HashMap<Long, Set<Application>>();
		String colName;
		boolean isColPresent;
		for(int i = 0; i < rowCount; i++) {
			colName = "profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(profile_id.equals("")) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'profile_id' column is not exist.");
				continue;
			}
			
			colName = "app_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String app_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(app_id.equals("")) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'app_id' column is not exist.");
				continue;				
			}
			
			Application application = (AhRestoreNewMapTools.getMapApplication(AhRestoreCommons.convertLong(app_id)));
			if(null == application) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'app_id' column is not valid.");
				continue;	
			}
			Long lProfile_Id = AhRestoreCommons.convertLong(profile_id);
			Set<Application> appList = mapAppProfile_App.get(lProfile_Id);
			if(null != appList) {
				appList.add(application);
			}
			else
			{
				appList = new HashSet<Application>();
				appList.add(application);
				mapAppProfile_App.put(lProfile_Id, appList);
			}
		}
		
		return mapAppProfile_App;
	}
	
	private static Map<Long, Set<CustomApplication>> getAppProflie_CustomApp() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		String tableName = "appprofile_custom_app";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if(!restoreRet)
			return null;
		
		int rowCount = xmlParser.getRowCount();
		if(0 >= rowCount)
			return null;
		
		Map<Long, Set<CustomApplication>> mapAppProfile_CustomApp = new HashMap<Long, Set<CustomApplication>>();
		String colName;
		boolean isColPresent;
		for(int i = 0; i < rowCount; i++) {
			colName = "profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(profile_id.equals("")) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'profile_id' column is not exist.");
				continue;
			}
			
			colName = "customapp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String app_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(app_id.equals("")) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'customapp_id' column is not exist.");
				continue;				
			}
			
			CustomApplication customApplication = (AhRestoreNewMapTools.getMapCustomApplication(AhRestoreCommons.convertLong(app_id)));
			if(null == customApplication) {
				BeLogTools.warn(BeLogTools.DEBUG,"Restore table '" + tableName +"' data be lost, cause: 'customapp_id' column is not valid.");
				continue;	
			}
			Long lProfile_Id = AhRestoreCommons.convertLong(profile_id);
			Set<CustomApplication> appList = mapAppProfile_CustomApp.get(lProfile_Id);
			if(null != appList) {
				appList.add(customApplication);
			}
			else
			{
				appList = new HashSet<CustomApplication>();
				appList.add(customApplication);
				mapAppProfile_CustomApp.put(lProfile_Id, appList);
			}
		}
		
		return mapAppProfile_CustomApp;
	}
	/**
	 * Get all information from application profile table
	 * @return List<ApplicationProfile> all application profile BO
	 * @throw AhRestoreColNotExistException -
	 * 	 			if applicationprofile.xml is not exist.
	 * @throw AhRestoreException -
	 * 				if error in parsing applicationprofile.xml
	 */
	private static List<ApplicationProfile> getAllApplicationProfile() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		//check validation of applicationprofile.xml
		String tableName = "application_profile";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if(!restoreRet) 
			return null;

		int rowCount = xmlParser.getRowCount();
		if(0 >= rowCount) 
			return null;
		
		Map<String, Long> newMapAppProfile = new HashMap<String, Long>();
		try {
			List<?> allAppId = QueryUtil.executeNativeQuery("select application_profile.id,application_profile.profilename from application_profile, hm_domain where application_profile.owner=hm_domain.id and hm_domain.domainname='" + HmDomain.GLOBAL_DOMAIN +"'");
			for(Object obj : allAppId) {
				Object[] element = (Object[])obj;
				if(null != element[0] && null != element[1]) {
					newMapAppProfile.put((String)element[1], ((BigInteger)element[0]).longValue());
				}				
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return null;
		}

		Map<Long, Set<Application>> mapAppProfile_App = getAppProflie_App();
		Map<Long, Set<CustomApplication>> mapAppProfile_CustomApp = getAppProflie_CustomApp();
		List<ApplicationProfile> appProfileList = new ArrayList<ApplicationProfile>();
		boolean isColPresent;
		String colName;
		ApplicationProfile appProfile;
		
		for(int i = 0; i < rowCount; i++) {
			appProfile = new ApplicationProfile();
			
			//set defaultflag
			colName = "defaultFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String defaultFlag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			/*if(defaultFlag.equals("")){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause:'defaultFlag' column is not exist.");
				continue;
			}*/
			appProfile.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultFlag));
			
			//set profilename
			colName = "profileName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String profileName = isColPresent ? xmlParser.getColVal(i, colName):"";
			if(profileName.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause:'profileName' column is not exist.");
				continue;
			}
			appProfile.setProfileName(profileName);
			
			//set id
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			Long lId = AhRestoreCommons.convertLong(id);
			appProfile.setId(lId);
			
			//set owner
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == owner) {
				BeLogTools.debug(HmLogConst.M_RESTORE,"Restore table '" + tableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			} else {
				appProfile.setOwner(owner);
			}	
			
			//set applicationList
			if(null != mapAppProfile_App) {
				Set<Application> appList = mapAppProfile_App.get(lId);
				if(null != appList)
					appProfile.setApplicationList(appList);			
			}		
			
			//set customappList
			if(null != mapAppProfile_CustomApp){
				Set<CustomApplication> customAppList = mapAppProfile_CustomApp.get(lId);
				if(null != customAppList)
					appProfile.setCustomApplicationList(customAppList);
			}
			
			//judge default flag
			if(appProfile.isDefaultFlag()) {
				AhRestoreNewMapTools.setMapAppProfile(appProfile.getId(), newMapAppProfile.get(appProfile.getProfileName()));
				continue;
			}
			appProfileList.add(appProfile);
		}
		return appProfileList.isEmpty() ? null : appProfileList;
	}	
	
	/**
	 * Restore os_version table
	 *
	 * @return true if table of os_version restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreOsVersion()
	{
		try {
			List<OsVersion> allOsVersion = getAllOsVersion();

			if(null != allOsVersion) {
				List<OsVersion> allOsVersionNew = new ArrayList<OsVersion>();
				//remove global osVersion fix bug 19239
				for(OsVersion osVersion : allOsVersion){
					long count = QueryUtil.findRowCount(OsVersion.class,
							new FilterParams("owner.domainName=:s1 and option55=:s2 and osVersion=:s3",
									new Object[]{HmDomain.GLOBAL_DOMAIN,osVersion.getOption55(),osVersion.getOsVersion()}));
					if(count <= 0){
						allOsVersionNew.add(osVersion);
					}
				}
				
				if(!allOsVersionNew.isEmpty()){
					List<Long> lOldId = new ArrayList<Long>();

					for (OsVersion osVer : allOsVersionNew) {
						lOldId.add(osVer.getId());
					}

					QueryUtil.restoreBulkCreateBos(allOsVersionNew);

					for(int i=0; i<allOsVersionNew.size(); i++)
					{
						AhRestoreNewMapTools.setMapOsVersion(lOldId.get(i), allOsVersionNew.get(i).getId());
					}
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from domain_name_item table
	 *
	 * @return Map<String, List<DomainNameItem>>
	 * @throws AhRestoreColNotExistException -
	 *             if domain_name_item.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing domain_name_item.xml.
	 */
	private static Map<String, List<DomainNameItem>> getAllDomainNames() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of domain_name_item.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("domain_name_item");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in domain_name_item table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<DomainNameItem>> domNames = new HashMap<String, List<DomainNameItem>>();

		boolean isColPresent;
		String colName;
		DomainNameItem singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new DomainNameItem();

			/**
			 * Set domain_object_id
			 */
			colName = "domain_object_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_name_item", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_name_item", colName);
			String domainname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDomainName(AhRestoreCommons.convertString(domainname));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_name_item", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			List<DomainNameItem> nameList = domNames.get(id);
			if (null == nameList) {
				nameList = new ArrayList<DomainNameItem>();
				nameList.add(singleInfo);
				domNames.put(id, nameList);
			} else {
				nameList.add(singleInfo);
			}
		}

		return domNames;
	}

	/**
	 * Get all information from domain_object table
	 *
	 * @return List<DomainObject> all DomainObject BO
	 * @throws AhRestoreColNotExistException -
	 *             if domain_object.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing domain_object.xml.
	 */
	private static List<DomainObject> getAllDomainObject() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of domain_object.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("domain_object");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}

		List<DomainObject> domList = new ArrayList<DomainObject>();
		boolean isColPresent;
		String colName;
		Map<String, List<DomainNameItem>> allName = getAllDomainNames();
		DomainObject domObj;

		for (int i = 0; i < rowCount; i++)
		{
			domObj = new DomainObject();

			/**
			 * Set objname
			 */
			colName = "objname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_object", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_object", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			domObj.setId(AhRestoreCommons.convertLong(id));

			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'domain_object' data be lost, cause: 'id' column is not exist.");
				continue;

			// default value
			} else if (HmServicesAction.WEBSENSEQUICKSTART.equals(name) || HmServicesAction.BARRACUDAQUICKSTART.equals(name)
				|| BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN.equals(name) || BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN.equals(name)) {
				domObj = QueryUtil.findBoByAttribute(DomainObject.class, "objName", name);
				AhRestoreNewMapTools.setMapDomainObject(AhRestoreCommons.convertLong(id), domObj.getId());
				continue;
			}
			domObj.setObjName(name);

			/**
			 * Set autoGenerateFlag
			 */
			colName = "autoGenerateFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_object", colName);
			boolean autoGenerateFlag = isColPresent &&  AhRestoreCommons.convertStringToBoolean(xmlParser
					.getColVal(i, colName));
			if(!autoGenerateFlag) autoGenerateFlag = domObj.isGenerated();
			domObj.setAutoGenerateFlag(autoGenerateFlag);

			/**
			 * Set objType
			 */
			colName = "objType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"domain_object", colName);
			short objType = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)): DomainObject.CLASSIFICATION_POLICY;
			domObj.setObjType(objType);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"domain_object", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain hmdom = AhRestoreNewMapTools.getHmDomain(ownerId);

			if(null == hmdom || HmDomain.GLOBAL_DOMAIN.equals(hmdom.getDomainName()))
			{
				if (name.equals("Quick-Start-WebSense-Whitelist")
						|| name.equals("Quick-Start-Barracuda-Whitelist")) {
					name = name.replace("Quick-Start", "QS");
					name = name.replace("WebSense", "Websense");
					domObj = QueryUtil.findBoByAttribute(DomainObject.class, "objName", name);
					if (null != domObj) {
						AhRestoreNewMapTools.setMapDomainObject(AhRestoreCommons.convertLong(id), domObj.getId());
					}
				}

				if (name.equals("QuickStart-WebSense-Whitelist")
						|| name.equals("QuickStart-Barracuda-Whitelist")) {
					name = name.replace("QuickStart", "QS");
					name = name.replace("WebSense", "Websense");
					domObj = QueryUtil.findBoByAttribute(DomainObject.class, "objName", name);
					if (null != domObj)
						AhRestoreNewMapTools.setMapDomainObject(AhRestoreCommons.convertLong(id), domObj.getId());
				}
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'domain_object' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			domObj.setOwner(hmdom);

			/**
			 * Set domain name Info
			 */
			domObj.setItems(allName.get(AhRestoreCommons.convertString(id)));

			domList.add(domObj);
		}
		
		return domList.isEmpty() ? null : domList;
	}

	/**
	 * Restore domain_object table
	 *
	 * @return true if table of domain_object restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreDomainObject()
	{
		try {
			List<DomainObject> allDom = getAllDomainObject();

			if(null != allDom) {
				List<Long> lOldId = new ArrayList<Long>();

				for (DomainObject dom : allDom) {
					lOldId.add(dom.getId());
				}

				QueryUtil.restoreBulkCreateBos(allDom);

				for(int i=0; i<allDom.size(); i++)
				{
					AhRestoreNewMapTools.setMapDomainObject(lOldId.get(i), allDom.get(i).getId());
					RestoreRoutingProfilePolicy.saveDomainNameItems(allDom.get(i).getId(), allDom.get(i).getItems());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get the ip object by old id
	 *
	 * @param oldId id -
	 * @param upgradeLog -
	 * @return IpAddress
	 */
	public static IpAddress getNewIpNetworkObj(String oldId, HmUpgradeLog upgradeLog) {
		IpAddress ipObj = needCreateNetworkObject.get(oldId);

		Long newId;
		try {
			// does not need create network object
			if (null == ipObj) {
				newId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(oldId));
			} else {
				String ipName = ipObj.getAddressName() + "_Network";

				// add log
				upgradeLog.setFormerContent("used the IP address and netmask in IP Object/Host Name \""+ipObj.getAddressName()+"\".");
				upgradeLog.setPostContent("A new IP Object/Host Name \""+ipName+"\" that defines the netmask of its IP entries by category (Network) was created and rebound to");

				IpAddress ipInDb = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", ipName, ipObj.getOwner().getId(), new LazyObjLoader());

				if (null == ipInDb) {
					IpAddress newIp = new IpAddress();
					newIp.setId(null);
					newIp.setVersion(null);
					newIp.setAddressName(ipName);
					newIp.setTypeFlag(IpAddress.TYPE_IP_NETWORK);
					newIp.setDefaultFlag(false);
					newIp.setOwner(ipObj.getOwner());
					List<SingleTableItem> items = new ArrayList<SingleTableItem>();
					items.addAll(ipObj.getItems());
					newIp.setItems(items);
					// create the new object
					newId = QueryUtil.createBo(newIp);
				} else {
					return ipInDb;
				}
			}
			if(null != newId) {
				return AhRestoreNewTools.CreateBoWithId(IpAddress.class, newId);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
		return null;
	}

	/**
	 * TA618: DNS profile restoration
	 * - Restore the table 'DNS_SERVICE_PROFILE'
	 * @author Yunzhi Lin
	 * - Time: Jul 22, 2011 11:42:42 AM
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean restoreDNSServices() {
		try {
			List<DnsServiceProfile> profileList = getAllDNSServicefiles();
			if (null != profileList) {
				List<Long> profileIds = new ArrayList<Long>();

				for (DnsServiceProfile profile : profileList) {
					profileIds.add(profile.getId());
				}

				QueryUtil.restoreBulkCreateBos(profileList);

				for(int i=0; i<profileList.size(); i++)
				{
					AhRestoreNewMapTools.setMapDNSServices(profileIds.get(i), profileList.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<DnsServiceProfile> getAllDNSServicefiles() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "dns_service_profile";

		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}
		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}

		boolean isColPresent;
		String colName;
		DnsServiceProfile dnsServiceProfile;
		Map<String,List<DnsSpecificSettings>> dnsSpecificMap = getAllDnsSpecifics();
		List<DnsServiceProfile> list = new ArrayList<DnsServiceProfile>();
		List<DnsServiceProfile> listNameEmpty = new ArrayList<DnsServiceProfile>();

		for (int i = 0; i < rowCount; i++) {
			dnsServiceProfile = new DnsServiceProfile();

			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			dnsServiceProfile.setId(AhRestoreCommons.convertLong(id));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == hmDomain || HmDomain.GLOBAL_DOMAIN.equals(hmDomain.getDomainName())) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dns_service_profile' data be lost, cause: 'owner' column is not avalilable.");
				continue;
			}
			dnsServiceProfile.setOwner(hmDomain);

			colName = "serviceName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'dns_service_profile' will reset the name, cause: 'serviceName' column value is null.");
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, dnsServiceProfile.getOwner().getId());
				if (dm!=null) {
					name=dm.getDomainName();
					dnsServiceProfile.setServiceName(name);
					listNameEmpty.add(dnsServiceProfile);
				} else {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'dns_service_profile' will lost data, cause: 'serviceName' column value is null. domain ID:" + ownerId);
				continue;
			}
			}
			dnsServiceProfile.setServiceName(name);

			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			dnsServiceProfile.setDescription(AhRestoreCommons.convertString(description));

			colName = "splitDNS";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			
			if(isColPresent){
				boolean flag = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				if(flag){
					dnsServiceProfile.setServiceType(DnsServiceProfile.SEPARATE_DNS);
				}else{
					dnsServiceProfile.setServiceType(DnsServiceProfile.SAME_DNS);
				}
			}else{
				colName = "serviceType";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				short serviceType = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : DnsServiceProfile.SEPARATE_DNS;
				dnsServiceProfile.setServiceType(serviceType);
			}
			
			colName = "internal_dns1_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long internalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			IpAddress ipAddress = getDNSAddress(internalDNSId);
			dnsServiceProfile.setInternalDns1(ipAddress);

			colName = "internal_dns2_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			internalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			ipAddress = getDNSAddress(internalDNSId);
			dnsServiceProfile.setInternalDns2(ipAddress);

			colName = "internal_dns3_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			internalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			ipAddress = getDNSAddress(internalDNSId);
			dnsServiceProfile.setInternalDns3(ipAddress);

			colName = "domain_object_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long domainObjectId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			if (null != domainObjectId) {
				DomainObject domainObj = AhRestoreNewMapTools.getMapDomainObject(domainObjectId);
				dnsServiceProfile.setDomainObj(domainObj);
			}

			colName = "externalServerType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int serverType = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : DnsServiceProfile.LOCAL_DNS_TYPE;
			dnsServiceProfile.setExternalServerType(serverType);

			colName = "external_dns1_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long externalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			ipAddress = null;
			ipAddress = getDNSAddress(externalDNSId);
			dnsServiceProfile.setExternalDns1(ipAddress);

			colName = "external_dns2_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			internalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			ipAddress = getDNSAddress(internalDNSId);
			dnsServiceProfile.setExternalDns2(ipAddress);

			colName = "external_dns3_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			internalDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			ipAddress = getDNSAddress(internalDNSId);
			dnsServiceProfile.setExternalDns3(ipAddress);

			// DNS_specific_settings
			if (dnsSpecificMap.get(AhRestoreCommons.convertString(id))==null) {
				dnsServiceProfile.setSpecificInfos(new ArrayList<DnsSpecificSettings>());
			} else {
				dnsServiceProfile.setSpecificInfos(dnsSpecificMap.get(AhRestoreCommons.convertString(id)));
			}

			list.add(dnsServiceProfile);
		}
		
		// fix bug 27502
		List<DnsServiceProfile> listNameExist = new ArrayList<DnsServiceProfile>();
		if (!listNameEmpty.isEmpty()) {
			for(DnsServiceProfile hm: listNameEmpty) {
				for(DnsServiceProfile h: list) {
					if (!hm.getId().equals(h.getId()) 
							&& hm.getServiceName().equals(h.getServiceName())
							&& hm.getOwner().getId().equals(h.getOwner().getId())) {
						listNameExist.add(hm);
						break;
					}
				}
			}
		}
		if (!listNameExist.isEmpty()) {
			int i=1;
			for(DnsServiceProfile h: listNameExist){
				boolean loopFlg=true;
				while (loopFlg) {
					boolean existFlg = false;
					String hName = h.getServiceName();
					if (hName.length()>28) {
						hName=hName.substring(0, 28) + "_" + i++;
					} else {
						hName=hName + "_" + i++;
					}
					for(DnsServiceProfile hif: list) {
						if (hif.getServiceName().equals(hName)
								&& hif.getOwner().getId().equals(h.getOwner().getId())) {
							existFlg = true;
							break;
						}
					}
					if (existFlg==false) {
						loopFlg=false;
						h.setServiceName(hName);
					}
				}
			}
		}
		for(DnsServiceProfile h: listNameExist){
			for(DnsServiceProfile hif : list){
				if(hif.getId().equals(h.getId())){
					hif.setServiceName(h.getServiceName());
				}
			}
		}
		
		//end fix bug 27502
				
		return list.isEmpty() ? null : list;
	}

	private static Map<String, List<DnsSpecificSettings>> getAllDnsSpecifics()
			throws AhRestoreException, AhRestoreColNotExistException {
		String tableName = "dns_specific_settings";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		Map<String, List<DnsSpecificSettings>> specificMap = new HashMap<String, List<DnsSpecificSettings>>();

		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return specificMap;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		DnsSpecificSettings specificDnsSettings;

		for (int i = 0; i < rowCount; i++) {
			specificDnsSettings = new DnsSpecificSettings();

			colName = "dns_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (StringUtils.isBlank(id)) {
				continue;
			}

			colName = "domainName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String domainname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			specificDnsSettings.setDomainName(AhRestoreCommons.convertString(domainname));

			colName = "specificDNS";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			Long sepcificDNSId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			IpAddress ipAddress = getDNSAddress(sepcificDNSId);
			specificDnsSettings.setDnsServer(ipAddress);

			List<DnsSpecificSettings> specificList = specificMap.get(id);
			if (null == specificList) {
				specificList = new ArrayList<DnsSpecificSettings>();
				specificList.add(specificDnsSettings);
				specificMap.put(id, specificList);
			} else {
				specificList.add(specificDnsSettings);
			}
		}
		return specificMap;
	}

	private static IpAddress getDNSAddress(Long dnsAddressId) {
		IpAddress ipAddress = null;
		if(null != dnsAddressId) {
			Long newDnsAddressId = AhRestoreNewMapTools.getMapIpAdddress(dnsAddressId);
			if(null != newDnsAddressId) {
				ipAddress = AhRestoreNewTools.CreateBoWithId(IpAddress.class, newDnsAddressId);
			}
		}
		return ipAddress;
	}

	private static Map<String,List<VpnNetworkSub>> getAllVpnNetworkSubList() throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName="vpn_network_subitem";
		/**
		 * Check validation of vpn_network_subitem.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<VpnNetworkSub>> subNetworksInfo = new HashMap<String, List<VpnNetworkSub>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++){

			/**
			 * Set vpn_network_sub_id
			 */
			colName = "vpn_network_sub_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network_subitem' data be lost, cause: 'vpn_network_sub_id' column is not exist.");
				/**
				 * The vpn_network_sub_id column must be exist in the table of vpn_network_subitem
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network_subitem' data be lost, cause: 'vpn_network_sub_id' column value is null.");
				continue;
			}

			/**
			 * Set key
			 */
			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network_subitem' data be lost, cause: 'key' column is not exist.");
				/**
				 * The key column must be exist in the table of vpn_network_subitem
				 */
				continue;
			}

			String keyId = xmlParser.getColVal(i, colName);
			if (keyId == null || keyId.trim().equals("")
				|| keyId.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network_subitem' data be lost, cause: 'key' column value is null.");
				continue;
			}

			VpnNetworkSub subNetWork= new VpnNetworkSub();
			subNetWork.setKey(AhRestoreCommons.convertInt(keyId));

			/**
			 * Set ipbranches
			 */
			colName = "ipbranches";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipbranches = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subNetWork.setIpBranches(AhRestoreCommons.convertInt(ipbranches));

			/**
			 * Set ipnetwork
			 */
			colName = "ipnetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipnetwork = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subNetWork.setIpNetwork(AhRestoreCommons.convertString(ipnetwork));

			/**
			 * Set leftend
			 */
			colName = "leftend";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String leftend = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subNetWork.setLeftEnd(AhRestoreCommons.convertInt(leftend));

			/**
			 * Set rightend
			 */
			colName = "rightend";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String rightend = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subNetWork.setRightEnd(AhRestoreCommons.convertInt(rightend));

			/**
			 * Set reserveclassification
			 */
			colName = "reserveclassification";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String reserveclassification = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subNetWork.setReserveClassification(AhRestoreCommons.convertStringToBoolean(reserveclassification));

			/**
			 * Set subnetclassification
			 */
			colName = "subnetclassification";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String subnetclassification = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subNetWork.setSubnetClassification(AhRestoreCommons.convertStringToBoolean(subnetclassification));

			/**
			 * set uniqueSubnetworkForEachBranches
			 */
			colName = "uniqueSubnetworkForEachBranches";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String uniqueSubnetworkForEachBranches = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			subNetWork.setUniqueSubnetworkForEachBranches(AhRestoreCommons.convertStringToBoolean(uniqueSubnetworkForEachBranches));
			
			/**
			 * set defaultGateway
			 */
			colName = "defaultGateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String defaultGateway = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subNetWork.setDefaultGateway((byte) AhRestoreCommons.convertInt(defaultGateway));
			
			/* ============ DNAT settings =============== */
			/**
			 * set enableNat
			 */
			colName = "enableNat";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enableNat = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subNetWork.setEnableNat(AhRestoreCommons.convertStringToBoolean(enableNat));
			
			/**
			 * Set localIpNetwork
			 */
			colName = "localIpNetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String localIpNetwork = isColPresent ? xmlParser.getColVal(i, colName) : AhRestoreCommons.convertString(ipnetwork);
			subNetWork.setLocalIpNetwork(AhRestoreCommons.convertString(localIpNetwork));
			
			/**
			 * set enablePortForwarding
			 */
			colName = "enablePortForwarding";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enablePortForwarding = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subNetWork.setEnablePortForwarding(AhRestoreCommons.convertStringToBoolean(enablePortForwarding));
			
			/* ============ DHCP settings =============== */
			/**
			 * Set enabledhcp
			 */
			colName = "enabledhcp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enabledhcp = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subNetWork.setEnableDhcp(AhRestoreCommons.convertStringToBoolean(enabledhcp));

			/**
			 * Set ntpserverip
			 */
			colName = "ntpserverip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ntpserverip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subNetWork.setNtpServerIp(AhRestoreCommons.convertString(ntpserverip));

			/**
			 * Set leasetime
			 */
			colName = "leasetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String leasetime = isColPresent ? xmlParser.getColVal(i, colName) : "86400";
			subNetWork.setLeaseTime(AhRestoreCommons.convertInt(leasetime));

			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String domainname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subNetWork.setDomainName(AhRestoreCommons.convertString(domainname));
			
			/**
			 * Set enableArpCheck
			 */
			colName = "enablearpcheck";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enableArpCheck = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			subNetWork.setEnableArpCheck(AhRestoreCommons.convertStringToBoolean(enableArpCheck));
			
			/**
			 * Set overrideDNSService
			 */
			colName = "overridednsservice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String overrideDns =  isColPresent ? xmlParser.getColVal(i, colName) : "false";
			subNetWork.setOverrideDNSService(AhRestoreCommons.convertStringToBoolean(overrideDns));
			
			/**
			 * Set vpn_dns_id
			 */
			colName = "vpn_dns_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String vpn_dns_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!vpn_dns_id.equals("") && !vpn_dns_id.trim().equalsIgnoreCase("null")) {
				Long newDnsId = AhRestoreNewMapTools.getMapDNSServices(Long.parseLong(vpn_dns_id.trim()));
				DnsServiceProfile dns = AhRestoreNewTools.CreateBoWithId(DnsServiceProfile.class, newDnsId);
				if (dns != null && subNetWork.isOverrideDNSService()) {
					subNetWork.setDnsService(dns);
				}
			}


			if (subNetWork != null) {
				if (subNetworksInfo.get(profileId) == null) {
					List<VpnNetworkSub> VpnNetworkSubLst= new ArrayList<VpnNetworkSub>();
					VpnNetworkSubLst.add(subNetWork);
					subNetworksInfo.put(profileId, VpnNetworkSubLst);
				} else {
					subNetworksInfo.get(profileId).add(subNetWork);
				}
			}
		}

		return subNetworksInfo;

	}

    private static Map<String,List<SingleTableItem>> getVpnSubNetwokClassListFromOldTable()
            throws AhRestoreException, AhRestoreColNotExistException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        String finalTableName = "vpn_network_subnetclass";
        /**
         * Check validation of vpn_network_subnetclass.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(finalTableName);
        if (!restoreRet) {
            return null;
        }

        Map<String, List<SingleTableItem>> subClassInfo = new HashMap<String, List<SingleTableItem>>();
        int rowCount = xmlParser.getRowCount();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {

            /**
             * Set vpn_network_subnetclass_id
             */
            colName = "vpn_network_subnetclass_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
            if (!isColPresent) {
                /**
                 * The vpn_network_subnetclass_id column must be exist in the
                 * table of vpn_network_subnetclass
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (profileId == null || profileId.trim().equals("")
                    || profileId.trim().equalsIgnoreCase("null")) {
                continue;
            }

            /**
             * Set key
             */
            colName = "key";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
            if (!isColPresent) {
                /**
                 * The key column must be exist in the table of
                 * vpn_network_subnetclass
                 */
                continue;
            }

            String keyId = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(keyId)) {
                continue;
            }

            SingleTableItem subClass = new SingleTableItem();
            subClass.setTag1Checked(false);
            subClass.setTag2Checked(false);
            subClass.setTag3Checked(false);
            subClass.setType(SingleTableItem.TYPE_CLASSIFIER);

            subClass.setRestoreId(profileId);
            subClass.setKey(AhRestoreCommons.convertInt(keyId));

            /**
             * Set tag
             */
            colName = "tag";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
            String tag = isColPresent ? xmlParser.getColVal(i, colName) : "0";
            int tagType = AhRestoreCommons.convertInt(tag);
            /**
             * Set tagvalue
             */
            colName = "tagvalue";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
            String tagvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";

            if (tagType == 1) {
                subClass.setTag1Checked(true);
                subClass.setTag1(tagvalue);
            } else if (tagType == 2) {
                subClass.setTag2Checked(true);
                subClass.setTag2(tagvalue);
            } else if (tagType == 3) {
                subClass.setTag3Checked(true);
                subClass.setTag3(tagvalue);
            }

            if (subClassInfo.get(profileId) == null) {
                List<SingleTableItem> subClassLst= new ArrayList<SingleTableItem>();
                subClassLst.add(subClass);
                subClassInfo.put(profileId, subClassLst);
            } else {
                subClassInfo.get(profileId).add(subClass);
            }
        }

        return subClassInfo;
    }

    @SuppressWarnings("unused")
    @Deprecated
	private static Map<String,List<VpnNetworkClassification>> getAllVpnSubNetwokClassList() throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName="vpn_network_subnetclass";
		/**
		 * Check validation of vpn_network_subnetclass.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<VpnNetworkClassification>> subClassInfo = new HashMap<String, List<VpnNetworkClassification>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++){

			/**
			 * Set vpn_network_subnetclass_id
			 */
			colName = "vpn_network_subnetclass_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				/**
				 * The vpn_network_subnetclass_id column must be exist in the table of vpn_network_subnetclass
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set key
			 */
			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				/**
				 * The key column must be exist in the table of vpn_network_subnetclass
				 */
				continue;
			}

			String keyId = xmlParser.getColVal(i, colName);
			if (keyId == null || keyId.trim().equals("")
				|| keyId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			VpnNetworkClassification subClass= new VpnNetworkClassification();
			subClass.setKey(AhRestoreCommons.convertInt(keyId));

			/**
			 * Set ipaddress
			 */
			colName = "ipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipaddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subClass.setIpAddress(AhRestoreCommons.convertString(ipaddress));

			/**
			 * Set tag
			 */
			colName = "tag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String tag = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subClass.setTag(AhRestoreCommons.convertInt(tag));

			/**
			 * Set tagvalue
			 */
			colName = "tagvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String tagvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subClass.setTagValue(AhRestoreCommons.convertString(tagvalue));

			if (subClassInfo.get(profileId) == null) {
				List<VpnNetworkClassification> subClassLst= new ArrayList<VpnNetworkClassification>();
				subClassLst.add(subClass);
				subClassInfo.put(profileId, subClassLst);
			} else {
				subClassInfo.get(profileId).add(subClass);
			}
		}

		return subClassInfo;
	}

//	private static Map<String,List<VpnNetworkClassification>> getAllVpnReserveClassList() throws AhRestoreException, AhRestoreColNotExistException {
//		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
//		String finalTableName="vpn_network_reserveclass";
//		/**
//		 * Check validation of vpn_network_reserveclass.xml
//		 */
//		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
//		if (!restoreRet)
//		{
//			return null;
//		}
//
//		int rowCount = xmlParser.getRowCount();
//		Map<String, List<VpnNetworkClassification>> subClassInfo = new HashMap<String, List<VpnNetworkClassification>>();
//		boolean isColPresent;
//		String colName;
//
//		for (int i = 0; i < rowCount; i++){
//
//			/**
//			 * Set vpn_network_reserveclass_id
//			 */
//			colName = "vpn_network_reserveclass_id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					finalTableName, colName);
//			if (!isColPresent)
//			{
//				/**
//				 * The vpn_network_reserveclass_id column must be exist in the table of vpn_network_reserveclass
//				 */
//				continue;
//			}
//
//			String profileId = xmlParser.getColVal(i, colName);
//			if (profileId == null || profileId.trim().equals("")
//				|| profileId.trim().equalsIgnoreCase("null"))
//			{
//				continue;
//			}
//
//			/**
//			 * Set key
//			 */
//			colName = "key";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					finalTableName, colName);
//			if (!isColPresent)
//			{
//				/**
//				 * The key column must be exist in the table of vpn_network_reserveclass
//				 */
//				continue;
//			}
//
//			String keyId = xmlParser.getColVal(i, colName);
//			if (keyId == null || keyId.trim().equals("")
//				|| keyId.trim().equalsIgnoreCase("null"))
//			{
//				continue;
//			}
//
//			VpnNetworkClassification subClass= new VpnNetworkClassification();
//			subClass.setKey(AhRestoreCommons.convertInt(keyId));
//
//			/**
//			 * Set ipaddress
//			 */
//			colName = "ipaddress";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					finalTableName, colName);
//			String ipaddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			subClass.setIpAddress(AhRestoreCommons.convertString(ipaddress));
//
//			/**
//			 * Set tag
//			 */
//			colName = "tag";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					finalTableName, colName);
//			String tag = isColPresent ? xmlParser.getColVal(i, colName) : "0";
//			subClass.setTag(AhRestoreCommons.convertInt(tag));
//
//			/**
//			 * Set tagvalue
//			 */
//			colName = "tagvalue";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					finalTableName, colName);
//			String tagvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			subClass.setTagValue(AhRestoreCommons.convertString(tagvalue));
//
//			if (subClass != null) {
//				if (subClassInfo.get(profileId) == null) {
//					List<VpnNetworkClassification> subClassLst= new ArrayList<VpnNetworkClassification>();
//					subClassLst.add(subClass);
//					subClassInfo.put(profileId, subClassLst);
//				} else {
//					subClassInfo.get(profileId).add(subClass);
//				}
//			}
//		}
//
//		return subClassInfo;
//	}
	private static Map<String,List<DhcpServerOptionsCustom>> getAllVpnCustomOptionsList() throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName="vpn_network_custom";
		/**
		 * Check validation of vpn_network_custom.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<DhcpServerOptionsCustom>> subCustomInfo = new HashMap<String, List<DhcpServerOptionsCustom>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++){

			/**
			 * Set vpn_network_custom_id
			 */
			colName = "vpn_network_custom_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				/**
				 * The vpn_network_custom_id column must be exist in the table of vpn_network_custom
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			DhcpServerOptionsCustom subCustom= new DhcpServerOptionsCustom();

			/**
			 * Set number
			 */
			colName = "number";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String number = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subCustom.setNumber(Short.valueOf(number));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subCustom.setType(Short.valueOf(type));

			/**
			 * Set value
			 */
			colName = "value";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String value = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subCustom.setValue(AhRestoreCommons.convertString(value));

			if (subCustomInfo.get(profileId) == null) {
				List<DhcpServerOptionsCustom> subCustomLst= new ArrayList<DhcpServerOptionsCustom>();
				subCustomLst.add(subCustom);
				subCustomInfo.put(profileId, subCustomLst);
			} else {
				subCustomInfo.get(profileId).add(subCustom);
			}
		}

		return subCustomInfo;

	}

	private static List<SubNetworkResource> getAllSubNetworkResList(String finalTableName) throws AhRestoreException, AhRestoreColNotExistException{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		boolean restoreRet = xmlParser.readXMLOneFile(finalTableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<SubNetworkResource> subResourceInfo = new ArrayList<SubNetworkResource>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++){

			SubNetworkResource subResource= new SubNetworkResource();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			subResource.setId(Long.valueOf(id));

			/**
			 * Set network
			 */
			colName = "network";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String network = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setNetwork(network);

			/**
			 * Set localNetwork
			 */
			colName = "localNetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String localNetwork = isColPresent ? xmlParser.getColVal(i, colName) : AhRestoreCommons.convertString(network);
			subResource.setLocalNetwork(AhRestoreCommons.convertString(localNetwork));
			
			/**
			 * Set parentNetwork
			 */
			colName = "parentNetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String parentNetwork = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setParentNetwork(AhRestoreCommons.convertString(parentNetwork));
			
			/**
			 * Set parentLocalNetwork
			 */
			colName = "parentLocalNetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String parentLocalNetwork = isColPresent ? xmlParser.getColVal(i, colName) : AhRestoreCommons.convertString(parentNetwork);
			subResource.setParentLocalNetwork(AhRestoreCommons.convertString(parentLocalNetwork));


			/**
			 * Set firstIp
			 */
			colName = "firstIp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String firstIp = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setFirstIp(AhRestoreCommons.convertString(firstIp));

			/**
			 * Set ipStartLong
			 */
			colName = "ipStartLong";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipStartLong = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subResource.setIpStartLong(AhRestoreCommons.convertLong(ipStartLong));

			/**
			 * Set ipEndLong
			 */
			colName = "ipEndLong";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipEndLong = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subResource.setIpEndLong(AhRestoreCommons.convertLong(ipEndLong));

			/**
			 * Set ipPoolStart
			 */
			colName = "ipPoolStart";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipPoolStart = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setIpPoolStart(AhRestoreCommons.convertString(ipPoolStart));

			/**
			 * Set ipPoolEnd
			 */
			colName = "ipPoolEnd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ipPoolEnd = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setIpPoolEnd(AhRestoreCommons.convertString(ipPoolEnd));

			/**
			 * Set hiveApMac
			 */
			colName = "hiveApMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String hiveApMac = isColPresent ? xmlParser.getColVal(i, colName) : null;
			subResource.setHiveApMac(AhRestoreCommons.convertString(hiveApMac));

			/**
			 * Set enableNat
			 */
			colName = "enableNat";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enableNat = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subResource.setEnableNat(AhRestoreCommons.convertStringToBoolean(enableNat));
			
			/**
			 * Set hiveApMgtx
			 */
			colName = "hiveApMgtx";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String hiveApMgtx = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			subResource.setHiveApMgtx(Short.valueOf(hiveApMgtx));

			/**
			 * Set hiveApMgtx
			 */
			colName = "status";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String status = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE);
			subResource.setStatus(Short.valueOf(status));

			/**
			 * Set vipIpAddress
			 */
			colName = "vipIpAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String vipIpAddress = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			subResource.setVipIpAddress(AhRestoreCommons.convertStringToBoolean(vipIpAddress));
			
			/**
			 * Set networkid
			 */
			colName = "networkid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String networkid = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (networkid != null
					&& !(networkid.trim().equals(""))
					&& !(networkid.trim().equalsIgnoreCase("null"))) {
				Long networkid_new = AhRestoreNewMapTools
						.getMapVpnNetwork(AhRestoreCommons
								.convertLong(networkid));
				if (null != networkid_new) {
					subResource.setVpnNetwork(AhRestoreNewTools
							.CreateBoWithId(VpnNetwork.class,
									networkid_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new VpnNetwork id mapping to old id:"
									+ networkid);
				}
			}


			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'sub_network_resource' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);

			subResource.setOwner(ownerDomain);



			subResourceInfo.add(subResource);

		}

		return subResourceInfo;
	}

	private static Map<String, List<SubnetworkDHCPCustom>> getAllSubnetworkCustomList()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName = "vpn_network_subnet_customs";
		/**
		 * Check validation of vpn_network_custom.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<SubnetworkDHCPCustom>> subCustomInfo = new HashMap<String, List<SubnetworkDHCPCustom>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			/**
			 * Set vpn_network_custom_id
			 */
			colName = "vpn_network_subnet_custom_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			if (!isColPresent) {
				/**
				 * The vpn_network_custom_id column must be exist in the table
				 * of vpn_network_custom
				 */
				continue;
			}
			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
					|| profileId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set key
			 */
			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			if (!isColPresent) {
				/**
				 * The key column must be exist in the table of
				 * vpn_network_subitem
				 */
				continue;
			}
			String keyId = xmlParser.getColVal(i, colName);
			if (keyId == null || keyId.trim().equals("") || keyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			SubnetworkDHCPCustom subCustom = new SubnetworkDHCPCustom();
			subCustom.setKey(AhRestoreCommons.convertInt(keyId));

			/**
			 * Set number
			 */
			colName = "number";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String number = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subCustom.setNumber(Short.valueOf(number));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			subCustom.setType(Short.valueOf(type));

			/**
			 * Set value
			 */
			colName = "value";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String value = isColPresent ? xmlParser.getColVal(i, colName) : "";
			subCustom.setValue(AhRestoreCommons.convertString(value));

			if (subCustomInfo.get(profileId) == null) {
				List<SubnetworkDHCPCustom> subCustomLst = new ArrayList<SubnetworkDHCPCustom>();
				subCustomLst.add(subCustom);
				subCustomInfo.put(profileId, subCustomLst);
			} else {
				subCustomInfo.get(profileId).add(subCustom);
			}
		}

		return subCustomInfo;
	}

	private static Map<String, List<PortForwarding>> getAllPortForwardingList()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName = "vpn_network_port_forwarding";
		/**
		 * Check validation of vpn_network_port_forwarding.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(finalTableName);
		if (!restoreRet) {
			return null;
		}
		
		int rowCount = xmlParser.getRowCount();
		Map<String, List<PortForwarding>> portForwardingInfo = new HashMap<String, List<PortForwarding>>();
		boolean isColPresent;
		String colName;
		
		for (int i = 0; i < rowCount; i++) {
			
			/**
			 * Set vpn_network_id
			 */
			colName = "vpn_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			if (!isColPresent) {
				/**
				 * The vpn_network_id column must be exist in the table
				 * of vpn_network_port_forwarding
				 */
				continue;
			}
			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
					|| profileId.trim().equalsIgnoreCase("null")) {
				continue;
			}
			
			/**
			 * Set key 
			 */
			colName = "key";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			if (!isColPresent) {
				/**
				 * The key column must be exist in the table of
				 * vpn_network_port_forwarding
				 */
				continue;
			}
			String keyId = xmlParser.getColVal(i, colName);
			if (null==keyId || "".equals(keyId.trim()) || keyId.trim().equalsIgnoreCase("null")) {
				continue;
			}
			PortForwarding port=new PortForwarding();
			port.setKey(AhRestoreCommons.convertInt(keyId));
			
			/**
			 * Set internalhostipaddress
			 */
			colName = "internalhostipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String ipAddress = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			port.setInternalHostIPAddress(ipAddress);
			
			/**
			 * Set internalhostportnumber
			 */
			colName = "internalhostportnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String internalPortNumber = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			port.setInternalHostPortNumber(internalPortNumber);
			
			/**
			 * Set destinationportnumber
			 */
			colName = "destinationportnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String destinationPortNumber = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			port.setDestinationPortNumber(destinationPortNumber);
			/**
			 * Set positionId
			 */
			colName = "positionId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String positionId = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			port.setPositionId(AhRestoreCommons.convertInt(positionId));
			/**
			 * Set protocol
			 */
			colName = "protocol";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, finalTableName, colName);
			String protocol = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			port.setProtocol(AhRestoreCommons.convertInt(protocol));
			if (null==portForwardingInfo.get(profileId)) {
				List<PortForwarding> portList = new ArrayList<PortForwarding>();
				portList.add(port);
				portForwardingInfo.put(profileId, portList);
			} else {
				portForwardingInfo.get(profileId).add(port);
			}
		}
		
		return portForwardingInfo;
	}
	
	private static List<VpnNetwork> getAllVpnNetworks() throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String finalTableName="vpn_network";
		/**
		 * Check validation of vpn_network.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("vpn_network");
		List<VpnNetwork> vpnNetworkInfo = new ArrayList<VpnNetwork>();

		if (!restoreRet) {
			// generate global object
//			for (HmDomain hmDom : AhRestoreNewMapTools.hmDomainMap.values()) {
//				StartHereAction.getVpnNetworkObj(hmDom.getDomainName(), hmDom.getId(), hmDom);
//			}
//			restorePreVpnNetwork = true;
			return null;
		}

		/**
		 * No one row data stored in vpn_network table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		VpnNetwork vpnNetworkDTO;

		for (int i = 0; i < rowCount; i++)
		{
			vpnNetworkDTO = new VpnNetwork();

			/**
			 * Set networkname
			 */
			colName = "networkname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network' data be lost, cause: 'networkname' column is not exist.");
				/**
				 * The networkname column must be exist in the table of vpn_network
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network' data be lost, cause: 'networkname' column value is null.");
				continue;
			}
			vpnNetworkDTO.setNetworkName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			vpnNetworkDTO.setId(Long.valueOf(id));

//			if (BeParaModule.DEFAULT_HIVEID_PROFILE_NAME.equals(name.trim())) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("hiveName", name);
//				HiveProfile newhiveProfile = HmBeParaUtil.getDefaultProfile(HiveProfile.class, map);
//				if (null != newhiveProfile) {
//					AhRestoreNewMapTools.setMapHives(AhRestoreCommons.convertLong(id), newhiveProfile.getId());
//				}
//				continue;
//			}

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (HmDomain.GLOBAL_DOMAIN.equals(ownerDomain.getDomainName())) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_network' data be lost, cause: 'owner' column value is global.");
				continue;
			}

			vpnNetworkDTO.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			vpnNetworkDTO.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String domainname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			vpnNetworkDTO.setDomainName(AhRestoreCommons.convertString(domainname));

			/**
			 * Set enabledhcp
			 */
			colName = "enabledhcp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enabledhcp = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			vpnNetworkDTO.setEnableDhcp(AhRestoreCommons.convertStringToBoolean(enabledhcp));

			/**
			 * Set leasetime
			 */
			colName = "leasetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String leasetime = isColPresent ? xmlParser.getColVal(i, colName) : "86400";
			vpnNetworkDTO.setLeaseTime(AhRestoreCommons.convertInt(leasetime));
			
			/**
			 * Set enableArpCheck
			 */
			colName = "enablearpcheck";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String enableArpCheck = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			vpnNetworkDTO.setEnableArpCheck(AhRestoreCommons.convertStringToBoolean(enableArpCheck));

			/**
			 * Set networktype
			 */
			colName = "networktype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String networktype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			int networkTypeInt = AhRestoreCommons.convertInt(networktype);
            vpnNetworkDTO.setNetworkType(networkTypeInt == 0 ? 1 : networkTypeInt);

			/**
			 * Set ntpserverip
			 */
			colName = "ntpserverip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String ntpserverip = isColPresent ? xmlParser.getColVal(i, colName) : "";
			vpnNetworkDTO.setNtpServerIp(AhRestoreCommons.convertString(ntpserverip));

			/**
			 * Set websecurity
			 */
			colName = "websecurity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String websecurity = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			vpnNetworkDTO.setWebSecurity(AhRestoreCommons.convertInt(websecurity));


			/**
			 * Set failConnectionOption
			 */
			if(vpnNetworkDTO.getWebSecurity() != VpnNetwork.VPN_NETWORK_WEBSECURITY_NONE) {
				colName = "failConnectionOption";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						finalTableName, colName);
				String failConnectionOption = isColPresent ? xmlParser.getColVal(i, colName) : "1";
				vpnNetworkDTO.setFailConnectionOption(AhRestoreCommons.convertInt(failConnectionOption));
			}
			// TODO for remove network object in user profile
			/**
			 * Set vlan_id
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			if (isColPresent) {
				String vlan_id = xmlParser.getColVal(i, colName);
				if (!vlan_id.equals("") && !vlan_id.trim().equalsIgnoreCase("null")) {
					AhRestoreNewMapTools.setMapNetworkObjectVlan(vpnNetworkDTO.getId(), Long.parseLong(vlan_id.trim()));
	//				Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(vlan_id.trim()));
	//				Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class, newVlanId);
	//				if (vlan != null) {
	//					vpnNetworkDTO.setVlan(vlan);
	//				}
				}
			}

			/**
			 * Set vpn_dns_id
			 */
			colName = "vpn_dns_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					finalTableName, colName);
			String vpn_dns_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!vpn_dns_id.equals("") && !vpn_dns_id.trim().equalsIgnoreCase("null")) {
				Long newDnsId = AhRestoreNewMapTools.getMapDNSServices(Long.parseLong(vpn_dns_id.trim()));
				DnsServiceProfile dns = AhRestoreNewTools.CreateBoWithId(DnsServiceProfile.class, newDnsId);
				if (dns != null) {
					vpnNetworkDTO.setVpnDnsService(dns);
				}
			}

			if(vpnNetworkDTO.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
				/**
				 * set ipAddressSpace
				 */
				colName = "ipAddressSpace";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						finalTableName, colName);
				String ipAddressSpace = isColPresent ? xmlParser.getColVal(i, colName) : "";
				vpnNetworkDTO.setIpAddressSpace(AhRestoreCommons.convertString(ipAddressSpace));

				colName = "guestLeftReserved";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						finalTableName, colName);
				String guestLeftReserved = isColPresent ? xmlParser.getColVal(i, colName) : "0";
				vpnNetworkDTO.setGuestLeftReserved(AhRestoreCommons.convertInt(guestLeftReserved));

				colName = "guestRightReserved";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						finalTableName, colName);
				String guestRightReserved = isColPresent ? xmlParser.getColVal(i, colName) : "0";
				vpnNetworkDTO.setGuestRightReserved(AhRestoreCommons.convertInt(guestRightReserved));
			}

			vpnNetworkInfo.add(vpnNetworkDTO);
		}

		return vpnNetworkInfo;
	}

	public static boolean restoreAllVpnNetworks() {
		try
		{
			List<VpnNetwork> allVpnNetworks = getAllVpnNetworks();
			Map<String, List<VpnNetworkSub>> allSubItem = getAllVpnNetworkSubList();

			// Handle the resotre for data struct modify.
			Map<String, List<SingleTableItem>> subnetReserveItemMap = null;
			List<SingleTableItem> subnetReserveItems = null;
			if(RESTORE_BEFORE_DARKA_FLAG) {
                subnetReserveItemMap = getVpnSubNetwokClassListFromOldTable();
			} else {
			    subnetReserveItems = getAllNetworkObjectItems(VPN_NETWORK_SUBNET_RESERVATION_ITEM);
			}

			List<SingleTableItem> ipAddressReserveItems = getAllNetworkObjectItems(VPN_NETWORK_IP_RESERVATION_ITEM);
			Map<String, List<DhcpServerOptionsCustom>> allCustom = getAllVpnCustomOptionsList();
			Map<String, List<SubnetworkDHCPCustom>> allCustoms = getAllSubnetworkCustomList();
			Map<String,List<PortForwarding>> allPortForwardings=getAllPortForwardingList();

			if(null == allVpnNetworks)
			{
				return false;
			}
			else
			{
				List<Long> lOldId = new ArrayList<Long>();

				for (VpnNetwork network : allVpnNetworks) {
					if (null == network.getId()) {
						break;
					}
					if (allSubItem!=null && allSubItem.get(network.getId().toString())!=null) {
						network.setSubItems(allSubItem.get(network.getId().toString()));
					}

                    if (null != subnetReserveItemMap
                            && null != subnetReserveItemMap.get(network.getId().toString())) {
                        network.setSubNetwokClass(subnetReserveItemMap.get(network.getId()
                                .toString()));
                    }
					if (subnetReserveItems!=null) {
					    List<SingleTableItem> list = new ArrayList<SingleTableItem>();
					    for (SingleTableItem singleTableItem : subnetReserveItems) {
					        if(singleTableItem.getRestoreId().equals(network.getId().toString())) {
					            list.add(singleTableItem);
					        }
					    }
					    network.setSubNetwokClass(list);
					}

					if (ipAddressReserveItems!=null) {
						List<SingleTableItem> list = new ArrayList<SingleTableItem>();
						for (SingleTableItem singleTableItem : ipAddressReserveItems) {
							if(singleTableItem.getRestoreId().equals(network.getId().toString())) {
								list.add(singleTableItem);
							}
						}
						network.setReserveClass(list);
					}

					if (allCustom!=null && allCustom.get(network.getId().toString())!=null) {
						network.setCustomOptions(allCustom.get(network.getId().toString()));
					}

//					if(allSubRes != null && allSubRes.get(network.getId().toString()) != null){
//						network.setSubNetworkRes(allSubRes.get(network.getId().toString()));
//					}

					if(allCustoms != null && allCustoms.get(network.getId().toString()) != null) {
						network.setSubnetworkDHCPCustoms(allCustoms.get(network.getId().toString()));
					}
                    if(null!=allPortForwardings && null!=allPortForwardings.get(network.getId().toString())){
                	  network.setPortForwardings(allPortForwardings.get(network.getId().toString()));
                    }

					lOldId.add(network.getId());
				}

				QueryUtil.restoreBulkCreateBos(allVpnNetworks);

				if (!lOldId.isEmpty()) {
					for(int i=0; i < allVpnNetworks.size(); ++i)
					{
						Long newId = allVpnNetworks.get(i).getId();
                        AhRestoreNewMapTools.setMapVpnNetwork(lOldId.get(i), newId);

						if(RESTORE_BEFORE_DARKA_FLAG) {
						    AhRestoreNewMapTools.setMapVpnNetworkBo(newId, allVpnNetworks.get(i));
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

    public static boolean restoreAllSubNetworkResource() {
        try {
            AhRestoreGetXML xmlParser = new AhRestoreGetXML();
            xmlParser.convertXMLfile("sub_network_resource");
            final String finalTableName = "convert_sub_network_resource";

            int index = 0;
            long totalCount = 0;

            while (true) {
                String fileName = finalTableName;
                if (index > 0) {
                    fileName = finalTableName + "_" + index;
                }
                index++;
                List<SubNetworkResource> allSubRes = getAllSubNetworkResList(fileName);

                if (null == allSubRes) {
                    // break if empty
                    break;
                } else {
                    List<Long> lOldId = new ArrayList<Long>();

                    for (SubNetworkResource sub : allSubRes) {
                        if (null == sub.getId()) {
                            break;
                        }

                        lOldId.add(sub.getId());
                    }

                    QueryUtil.restoreBulkCreateBos(allSubRes);

                    if (!lOldId.isEmpty()) {
                        final int size = allSubRes.size();
                        totalCount += size;
                        for (int i = 0; i < size; ++i) {
                            AhRestoreNewMapTools.setMapSubNetworkResource(
                                    lOldId.get(i), allSubRes.get(i).getId());
                        }
                    }
                }
            }
            AhRestoreDBTools.logRestoreMsg("Total " + finalTableName
                    + " count is :" + (totalCount == 0 ? "empty" : totalCount));
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg(e.getMessage());
            return false;
        }
        return true;
    }

	// ----------------------- restoreRoutingProfile start -------------------
	public static boolean restoreRoutingProfiles() {
		try {

			List<RoutingProfile> profileList = getAllRoutingProfileServicefiles();
			if (null != profileList) {
				List<Long> profileIds = new ArrayList<Long>();

				for (RoutingProfile profile : profileList) {
					profileIds.add(profile.getId());
				}

				QueryUtil.restoreBulkCreateBos(profileList);

				for(int i=0; i<profileList.size(); i++)
				{
					AhRestoreNewMapTools.setMapRoutingProfile(profileIds.get(i), profileList.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<RoutingProfile> getAllRoutingProfileServicefiles() throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "routing_profile";

		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}
		int rowCount = xmlParser.getRowCount();
		if (rowCount <= 0) {
			return null;
		}
		boolean isColPresent;
		String colName;
		RoutingProfile routingProfile;
		Map<String,List<NeighborsNameItem>> neighborsMap = getAllNeighbors();
		List<RoutingProfile> list = new ArrayList<RoutingProfile>();

		for (int i = 0; i < rowCount; i++) {
			routingProfile = new RoutingProfile();

			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			routingProfile.setId(AhRestoreCommons.convertLong(id));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == hmDomain) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '"+tableName+"' data be lost, cause: 'owner' column is  not available.");
				continue;
			}
			routingProfile.setOwner(hmDomain);

			colName = "enableDynamicRouting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableDynamicRouting = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName)) ;
			routingProfile.setEnableDynamicRouting(enableDynamicRouting);

			colName = "typeFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			short typeflag = isColPresent ?  (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : RoutingProfile.ENABLE_DRP_OSPF;
			routingProfile.setTypeFlag(typeflag);

			colName = "enableRouteLan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableRouteLan = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName)) ;
			routingProfile.setEnableRouteLan(enableRouteLan);

			colName = "enableRouteWan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableRouteWan = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName)) ;
			routingProfile.setEnableRouteWan(enableRouteWan);

			colName = "useMD5";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean flag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			routingProfile.setUseMD5(flag);

			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
			routingProfile.setPassword(AhRestoreCommons.convertString(password));

			colName = "area";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String area = isColPresent ? xmlParser.getColVal(i, colName) : "0.0.0.0";
			routingProfile.setArea(AhRestoreCommons.convertString(area));

			colName = "routerId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String routerId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			routingProfile.setRouterId(AhRestoreCommons.convertString(routerId));

			colName = "autonmousSysNm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String autonmousSysNm = isColPresent ? xmlParser.getColVal(i, colName) : "";
			routingProfile.setAutonmousSysNm(AhRestoreCommons.convertInt(autonmousSysNm));

			colName = "keepalive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String keepalive = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			routingProfile.setKeepalive(AhRestoreCommons.convertInt(keepalive));

			colName = "bgpRouterId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String bgpRouterId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			routingProfile.setBgpRouterId(AhRestoreCommons.convertString(bgpRouterId));

			//NEIGHBORS_NAME_ITEM
			if (neighborsMap != null) {
				routingProfile.setItems(neighborsMap.get(AhRestoreCommons.convertString(id)));
			}

			list.add(routingProfile);
		}
		return list.isEmpty() ? null : list;

	}

	private static Map<String,List<NeighborsNameItem>> getAllNeighbors() throws AhRestoreException, AhRestoreColNotExistException {
		String tableName = "neighbors_name_item";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<NeighborsNameItem>> neighborsMap = new HashMap<String, List<NeighborsNameItem>>();

		boolean isColPresent;
		String colName;
		NeighborsNameItem neighborsNameItem;

		for (int i = 0; i < rowCount; i++) {
			neighborsNameItem = new NeighborsNameItem();

			colName = "neighbors_object_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (StringUtils.isBlank(id)) {
				continue;
			}

			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			neighborsNameItem.setDescription(AhRestoreCommons.convertString(description));

			colName = "neighborsName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String neighborsName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			neighborsNameItem.setNeighborsName(AhRestoreCommons.convertString(neighborsName));

			List<NeighborsNameItem> neighborsList = neighborsMap.get(id);
			if (null == neighborsList) {
				neighborsList = new ArrayList<NeighborsNameItem>();
				neighborsList.add(neighborsNameItem);
				neighborsMap.put(id, neighborsList);
			} else {
				neighborsList.add(neighborsNameItem);
			}
		}
		return neighborsMap;
	}
	// ----------------------- restoreRoutingProfile end -------------------
	
	/**
	 * Restore WIFICLIENT_PREFERRED_SSID table
	 *
	 * @return true if table of WIFICLIENT_PREFERRED_SSID restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreWifiClientPreferredSsid()
	{
		try {
			List<WifiClientPreferredSsid> allWifiClientPreferredSsids = getAllWifiClientPreferredSsid();
			if (null != allWifiClientPreferredSsids) {
				List<Long> lOldId = new ArrayList<Long>();

				for (WifiClientPreferredSsid wifiClientPreferredSsid : allWifiClientPreferredSsids) {
					lOldId.add(wifiClientPreferredSsid.getId());
				}

				QueryUtil.restoreBulkCreateBos(allWifiClientPreferredSsids);

				for(int i=0; i<allWifiClientPreferredSsids.size(); i++)
				{
					AhRestoreNewMapTools.setMapWifiClientPreferredSsid(lOldId.get(i), allWifiClientPreferredSsids.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from WIFICLIENT_PREFERRED_SSID table
	 *
	 * @return List<BonjourService> all WifiClientPreferredSsid BO
	 * @throws AhRestoreColNotExistException -
	 *             if WIFICLIENT_PREFERRED_SSID.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing WIFICLIENT_PREFERRED_SSID.xml.
	 */
	private static List<WifiClientPreferredSsid> getAllWifiClientPreferredSsid()
		throws AhRestoreColNotExistException,
		AhRestoreException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of wificlient_preferred_ssid.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("wificlient_preferred_ssid");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in WIFICLIENT_PREFERRED_SSID table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<WifiClientPreferredSsid> wifiClientPreferredSsids = new ArrayList<WifiClientPreferredSsid>();

		boolean isColPresent;
		String colName;
		WifiClientPreferredSsid wifiClientPreferredSsid;

		for (int i = 0; i < rowCount; i++)
		{
			wifiClientPreferredSsid = new WifiClientPreferredSsid();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			if (!isColPresent)
			{
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'wificlient_preferred_ssid' data be lost, cause: 'id' column is not exist.");
				/**
				 * The ssid column must be exist in the table of wificlient_preferred_ssid
				 */
				continue;
			}

			String id = xmlParser.getColVal(i, colName);
			if (id == null || id.trim().equals("")
				|| id.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'wificlient_preferred_ssid' data be lost, cause: 'id' column value is null.");
				continue;
			}
			wifiClientPreferredSsid.setId(AhRestoreCommons.convertLong(id.trim()));
	
			/**
			 * Set ssid
			 */
			colName = "ssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			if (!isColPresent)
			{
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'wificlient_preferred_ssid' data be lost, cause: 'ssid' column is not exist.");
				/**
				 * The ssid column must be exist in the table of wificlient_preferred_ssid
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'wificlient_preferred_ssid' data be lost, cause: 'ssid' column value is null.");
				continue;
			}
			wifiClientPreferredSsid.setSsid(name.trim());
			
			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"wificlient_preferred_ssid", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			wifiClientPreferredSsid.setOwner(ownerDomain);

			/**
			 * Set mgmtkey
			 */
			colName = "mgmtkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			String mgmtkey = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			wifiClientPreferredSsid.setMgmtKey(AhRestoreCommons.convertInt(mgmtkey));
			
			/**
			 * Set keytype
			 */
			colName = "keytype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			String keytype = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			wifiClientPreferredSsid.setKeyType(AhRestoreCommons.convertInt(keytype));
			
			/**
			 * Set keyValue
			 */
			colName = "keyValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"wificlient_preferred_ssid", colName);
			String keyValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			wifiClientPreferredSsid.setKeyValue(AhRestoreCommons.convertString(keyValue));

			
			/**
			 * Set encryption
			 */
			colName = "encryption";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			String encryption = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			wifiClientPreferredSsid.setEncryption(AhRestoreCommons.convertInt(encryption));
			
			/**
			 * Set authentication
			 */
			colName = "authentication";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			String authentication = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			wifiClientPreferredSsid.setAuthentication(AhRestoreCommons.convertInt(authentication));
			
			/**
			 * Set accessmode
			 */
			colName = "accessmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);		
			String accessmode = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			wifiClientPreferredSsid.setAccessMode(AhRestoreCommons.convertInt(accessmode));
			
			/**
			 * Set comment
			 */
			colName = "comment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"wificlient_preferred_ssid", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			wifiClientPreferredSsid.setComment(AhRestoreCommons.convertString(description));

			wifiClientPreferredSsids.add(wifiClientPreferredSsid);
		}

		return wifiClientPreferredSsids.size() > 0 ? wifiClientPreferredSsids : null;

	}
	
	//restore Mstp Region
	public static List<MstpRegion> getAllMstpRegion() throws AhRestoreException, AhRestoreColNotExistException{
		List<MstpRegion> mapRegion = new ArrayList<MstpRegion>();
		
		String tableName = "mstp_region";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile(tableName);
        
     	 
         if (!restoreRet) {
             AhRestoreDBTools
                     .logRestoreMsg("SAXReader cannot read mstp_region.xml file.");
             return null;
         }

         int rowCount = xmlParser.getRowCount();

         boolean isColPresent;
         String colName;
         
         for (int i = 0; i < rowCount; i++) {
        	 MstpRegion mstpRegion = new MstpRegion();
        	 
        	  colName = "id";
              isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                      tableName, colName);
              String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
              
              if("".equals(id)) {
   				continue;
   			  }
              mstpRegion.setId(AhRestoreCommons.convertString2Long(id));
              
              colName = "regionName";
              isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                      tableName, colName);
              String regionname = isColPresent ? xmlParser.getColVal(i, colName)
                      : null;
              mstpRegion.setRegionName(regionname);
              
              colName = "hops";
              isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                      tableName, colName);
              String hops = isColPresent ? xmlParser.getColVal(i, colName) : "1";
              mstpRegion.setHops(AhRestoreCommons.convertInt(hops));
              
              colName = "description";
              isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                      tableName, colName);
              String description = isColPresent ? xmlParser.getColVal(i, colName)
                      : null;
              mstpRegion.setDescription(description);
              
              colName = "revision";
              isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                      tableName, colName);
              String revision = isColPresent ? xmlParser.getColVal(i, colName) : "1";
              mstpRegion.setRevision(AhRestoreCommons.convertInt(revision));
              
              colName = "owner";
  			  isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
  			  long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

  			  if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
  			  {
  				  BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is  not available.");
  				  continue;
  			  }

  			  mstpRegion.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
  			  
  			 mapRegion.add(mstpRegion);
         }
         
         return mapRegion;
	}
	
	public static Map<String, List<MstpRegionPriority>> getAllMstpRegionPriority () throws AhRestoreException, AhRestoreColNotExistException{
		Map<String, List<MstpRegionPriority>> mapMstpRegionPriority= new HashMap<String, List<MstpRegionPriority>> ();
		String tableName = "mstp_region_priority";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile(tableName);
        
     	 
         if (!restoreRet) {
             AhRestoreDBTools
                     .logRestoreMsg("SAXReader cannot read mstp_region_prioity.xml file.");
             return null;
         }

         int rowCount = xmlParser.getRowCount();

         boolean isColPresent;
         String colName;
         
         for (int i = 0; i < rowCount; i++) {
        	 MstpRegionPriority mstpRegionPriority = new MstpRegionPriority();
        	 
        	 colName = "mstp_region_id";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String mstp_region_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
             
             if("".equals(mstp_region_id)) {
 				continue;
 			 }
             
             colName = "vlan";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String vlan = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             mstpRegionPriority.setVlan(vlan);
             
             colName = "instance";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String instance = isColPresent ? xmlParser.getColVal(i, colName)
                     : "-1";
             mstpRegionPriority.setInstance(Short.valueOf((instance)));
             
             colName = "priority";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String priority = isColPresent ? xmlParser.getColVal(i, colName)
                     : "-1";
             mstpRegionPriority.setPriority(AhRestoreCommons.convertInt(priority));
             
             List<MstpRegionPriority> tempList = mapMstpRegionPriority.get(mstp_region_id);
 			 if (null == tempList) {
 				 tempList = new ArrayList<MstpRegionPriority>();
 				 tempList.add(mstpRegionPriority);
 				 mapMstpRegionPriority.put(mstp_region_id, tempList);
 			 } else {
 				 tempList.add(mstpRegionPriority);
 			 }
         }
         
		return mapMstpRegionPriority;
	}
	
	public static boolean restoreMstpRegion() throws Exception {
		List<MstpRegion> mapRegion = getAllMstpRegion();
		Map<String, List<MstpRegionPriority>> allpriorities = getAllMstpRegionPriority();
		if (null != mapRegion && !mapRegion.isEmpty()) {
			List<Long> profileIds = new ArrayList<Long>();

			for (MstpRegion profile : mapRegion) {
				if(allpriorities != null && !allpriorities.isEmpty()){
					List<MstpRegionPriority> list = allpriorities.get(profile.getId().toString());
					if(list != null && !list.isEmpty()){
						profile.setMstpRegionPriorityList(list);
					}
				}
				
				profileIds.add(profile.getId());
			}

			QueryUtil.restoreBulkCreateBos(mapRegion);

			for(int i = 0; i < mapRegion.size(); i++)
			{
				AhRestoreNewMapTools.setMstpRegions(profileIds.get(i), mapRegion.get(i).getId());
			}	
		}
		return true;
	}

	private static final class LazyObjLoader implements QueryBo {
        @Override
        public Collection<HmBo> load(HmBo bo) {
            if (bo instanceof IpAddress) {
                IpAddress ipAddress = (IpAddress) bo;
                if (null != ipAddress.getItems())
                    ipAddress.getItems().size();
            }
            return null;
        }
    }
}
