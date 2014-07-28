package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanInterfacesMode;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortMonitorProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.util.devices.impl.Device;

public class RestoreWiredPortTemplate extends RestoreVersionHelper{
    
    public static final boolean RESTORE_HM_BEFORE_ESSON = isRestoreBefore("6.0.1.0");
    
    private static List<HmUpgradeLog> upgradeLoglist;

    /**
     * Restore Port Template Profile.<br>
     * Need to restore below tables first:
     * <ul>
     * <li>{@link PortAccessProfile}</li>
     * </ul> <br>
     * And need to update the 'parentNPId' field after {@link ConfigTemplate} is restored
     * @author Yunzhi Lin
     * - Time: Nov 16, 2012 5:24:32 PM
     * @return True or False
     */
    public static boolean restorePortTemplate() {
        try {
            long startTime = System.currentTimeMillis();
            
            List<PortGroupProfile> templates = getAllPortTemplates();

            Map<String, List<PortBasicProfile>> basics = getAllPortBasicProfiles();
            
            Map<String, List<PortPseProfile>> portPseProfiles = getAllPortPseProfile();

            Map<String, List<PortMonitorProfile>> monitorProfiles = getAllPortMonitorProfile();
            
            Map<String, List<SingleTableItem>> tableItems=getAllSingleTableItems();

            if (null == templates) {
                AhRestoreDBTools
                        .logRestoreMsg("Error when restore PortTemplates: the Wired Port Template list is null");
                return false;
            } else {
                List<Long> oldIds = new ArrayList<Long>();
                for (PortGroupProfile temp : templates) {
                    if (!(null == basics || basics.isEmpty())) {
                        temp.setBasicProfiles(basics.get(temp.getId().toString()));
                    }
                    if (!(null == monitorProfiles || monitorProfiles.isEmpty())) {
                    	temp.setMonitorProfiles(monitorProfiles.get(temp.getId().toString()));
                    }
                    if(!(null == portPseProfiles || portPseProfiles.isEmpty())){
                    	temp.setPortPseProfiles(portPseProfiles.get(temp.getId().toString()));
                    }
                    if(!(null == tableItems || tableItems.isEmpty())){
                    	temp.setItems(tableItems.get(temp.getId().toString()));
                    }
                    oldIds.add(temp.getId());
                }

                QueryUtil.restoreBulkCreateBos(templates);

                for (int i = 0; i < templates.size(); i++) {
                    AhRestoreNewMapTools.setMapWiredPortTemplateId(
                            oldIds.get(i), templates.get(i).getId());
                }
                for (int i = 0; i < templates.size(); i++) {
                	updatePortTemplateInItemTable(templates.get(i).getId());
                }
                
                if(null != upgradeLoglist) {
                    try {
                        QueryUtil.restoreBulkCreateBos(upgradeLoglist);
                    } catch (Exception e) {
                        AhRestoreDBTools.logRestoreMsg("Insert the upgrade logs for device template", e);
                    } finally {
                        // release
                        upgradeLoglist = null;
                    }
                }
                
                AhRestoreDBTools.logRestoreMsg("Restore port_template_profile (portgroups_profile) end," +
                		" records=" + templates.size()
                        + ", cost time(ms)=" + (System.currentTimeMillis() - startTime));
            }
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Error when restore PortTemplates", e);
            return false;
        }
        return true;
    }
    
    private static void updatePortTemplateInItemTable(Long portGroupId){
    		PortGroupProfile currentPortGroup = QueryUtil.findBoById(PortGroupProfile.class,portGroupId);
    		List<SingleTableItem> tableItems=currentPortGroup.getItems();
    		for(SingleTableItem item:tableItems){
    			Long oldId=item.getNonGlobalId();
    			Long newId=AhRestoreNewMapTools.getMapWiredPortTemplateId(oldId);
    			if(newId!=null){
    				item.setNonGlobalId(newId);
    			}
    		}
    		
    		try {
    			QueryUtil.updateBo(currentPortGroup);
			} catch (Exception e) {
			    AhRestoreDBTools.logRestoreMsg("updatePortTemplateInItemTable() : update PortGroupProfile!", e);
			}
    }
    
    
    private static Map<String, List<PortBasicProfile>> getAllPortBasicProfiles()
            throws AhRestoreException, AhRestoreColNotExistException {
        Map<String, List<PortBasicProfile>> basicsMap = new HashMap<String, List<PortBasicProfile>>();
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of port_basic_profile.xml
         */
        final String tableName = "port_basic_profile";
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            AhRestoreDBTools
                    .logRestoreMsg("SAXReader cannot read port_basic_profile.xml file.");
            return null;
        }

        int rowCount = xmlParser.getRowCount();

        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {

            PortBasicProfile basic = new PortBasicProfile();

            /**
             * Set PORTGROUPS_ID
             */
            colName = "portgroups_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                continue;
            }

            String portgroupId = xmlParser.getColVal(i, colName);
            if (isIllegalString(portgroupId)) {
                continue;
            }

            Long newAccessId = null;
            colName = "accessprofile_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String accessprofile_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            if (NmsUtil.isNotBlankId(accessprofile_id)) {
                newAccessId = AhRestoreNewMapTools.getMapWiredPortAccessId(Long
                        .parseLong(accessprofile_id.trim()));
                PortAccessProfile acc = AhRestoreNewTools.CreateBoWithId(
                        PortAccessProfile.class, newAccessId);
                basic.setAccessProfile(acc);
            }
            if (null == newAccessId) {
                continue;
            }

            colName = "ethPorts";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String ethPorts = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            if(!isIllegalString(ethPorts)) {
                basic.setEthPorts(ethPorts);
            }

            colName = "sfpPorts";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String sfpPorts = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            if(!isIllegalString(sfpPorts)) {
                basic.setSfpPorts(sfpPorts);
            }

            colName = "usbPorts";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String usbPorts = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            if(!isIllegalString(usbPorts)) {
                //basic.setUsbPorts(usbPorts);
            }

            colName = "portChannel";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String portChannel = isColPresent ? xmlParser.getColVal(i, colName)
                    : "0";
            basic.setPortChannel((short) AhRestoreCommons
                    .convertInt(portChannel));

            colName = "enabledlinkAggregation";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledlinkAggregation = isColPresent ? xmlParser.getColVal(
                    i, colName) : "false";
            basic.setEnabledlinkAggregation(AhRestoreCommons
                    .convertStringToBoolean(enabledlinkAggregation));

            if (basicsMap.get(portgroupId) == null) {
                List<PortBasicProfile> portBasicProfiles = new ArrayList<PortBasicProfile>();;
                portBasicProfiles.add(basic);
                basicsMap.put(portgroupId, portBasicProfiles);
           } else {
               basicsMap.get(portgroupId).add(basic);
           }

        }
        return basicsMap;
    }
    
    private static Map<String, List<SingleTableItem>> getAllSingleTableItems()
            throws AhRestoreException, AhRestoreColNotExistException {
        Map<String, List<SingleTableItem>> itemsMap = new HashMap<String, List<SingleTableItem>>();
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of port_template_profile_item.xml
         */
        final String tableName = "port_template_profile_item";
        String idKey = "portprofiles_id";
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            AhRestoreDBTools
                    .logRestoreMsg("SAXReader cannot read port_template_profile_item.xml file.");
            return null;
        }

        int rowCount = xmlParser.getRowCount();

        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {

        	SingleTableItem singleItem= new SingleTableItem();

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
			 * Set configTemplateId
			 */
			colName = "configTemplateId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String configTemplateId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleItem.setConfigTemplateId(AhRestoreCommons.convertLong(configTemplateId));
			
			/**
			 * Set nonGlobalId
			 */
			colName = "nonGlobalId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String nonGlobalId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleItem.setNonGlobalId(AhRestoreCommons.convertLong(nonGlobalId));
			
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


			/**----------------------*/
            

            if (itemsMap.get(id) == null) {
                List<SingleTableItem> singleTableItemList = new ArrayList<SingleTableItem>();;
                singleTableItemList.add(singleItem);
                itemsMap.put(id, singleTableItemList);
           } else {
        	   itemsMap.get(id).add(singleItem);
           }

        }
        return itemsMap;
    }



    private static List<PortGroupProfile> getAllPortTemplates() throws AhRestoreException, AhRestoreColNotExistException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        final String tableName = "port_template_profile", tableNameOld = "portgroups_profile";
        boolean restoreRet = xmlParser.readXMLFile(tableNameOld);
        if(!restoreRet) {
            restoreRet = xmlParser.readXMLFile(tableName);
            if (!restoreRet) {
                return null;
            }
        }
        
        AhRestoreDBTools.logRestoreMsg("Restore " + tableName + " start...");
        
        int rowCount = xmlParser.getRowCount();
        List<PortGroupProfile> portTemplates= new ArrayList<PortGroupProfile>();
        boolean isColPresent;
        String colName;
        PortGroupProfile template;

        for (int i = 0; i < rowCount; i++) {
            template = new PortGroupProfile();

            colName = "id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
            template.setId(AhRestoreCommons.convertLong(id));

            colName = "owner";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            long ownerId = isColPresent ? AhRestoreCommons
                    .convertLong(xmlParser.getColVal(i, colName)) : 1;
            HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
            if (null == hmDomain) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table '"+tableName+"' data be lost, cause: 'owner' column is not available.");
                continue;
            }
            template.setOwner(hmDomain);

            colName = "name";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table '"+tableName+"' data be lost, cause: 'name' column is not exist.");
                continue;
            }
            String name = xmlParser.getColVal(i, colName);
            if (isIllegalString(name)) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table '"+tableName+"' data be lost, cause: 'name' column value is null.");
                continue;
            }
            template.setName(name.trim());

            colName = "description";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String description = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            template.setDescription(AhRestoreCommons
                    .convertString(description));
            
            /*--------- TODO maintain a map for this field then update it after NetworkPolicy is restored ---------------*/
            colName = "parentNPId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            Long parentNPId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName))
                    : null;
            template.setParentNPId(AhRestoreNewMapTools.getMapConfigTemplate(parentNPId));
            
            /*-------------- basic -------------------------*/
            colName = "deviceModels";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String deviceModels = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            template.setDeviceModels(AhRestoreCommons
                    .convertString(deviceModels));
            
            colName = "deviceType";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            short deviceType = isColPresent ? (short) AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName),
                            HiveAp.Device_TYPE_SWITCH)
                    : HiveAp.Device_TYPE_SWITCH;
            template.setDeviceType(deviceType);
            
            final String[] deviceModelArray = template.getDeviceModelStrs();
            if(null != deviceModelArray) {
                if(template.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
                        && ArrayUtils.contains(deviceModelArray, "" + HiveAp.HIVEAP_MODEL_SR2124P)) {
                    // add a upgrade log
                    HmUpgradeLog log = new HmUpgradeLog();
                    log.setFormerContent("The device template \"" + template.getName() + "\" is for " 
                    +  AhConstantUtil.getString(Device.NAME, HiveAp.HIVEAP_MODEL_SR2124P) +" functioning as Router.");
                    log.setPostContent("The device template will be discarded in this version.");
                    log.setRecommendAction("No action is required.");
                    log.setOwner(hmDomain);
                    log.setLogTime(new HmTimeStamp(System.currentTimeMillis(),hmDomain.getTimeZoneString()));
                    log.setAnnotation("Click to add an annotation");
                    
                    if(null == upgradeLoglist) {
                        upgradeLoglist = new ArrayList<>();
                    } 
                    upgradeLoglist.add(log);
                    // skip this data
                    AhRestoreDBTools.logRestoreMsg(log.getFormerContent()+log.getPostContent());
                    continue;
                }
            }
            
            colName = "portNum";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            short portNum = isColPresent ? (short) AhRestoreCommons.convertInt(
                    xmlParser.getColVal(i, colName), 24) : 24;
            template.setPortNum(portNum);
            
            colName = "loadBalanceMode";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            short loadBalanceMode = isColPresent ? (short) AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName)) : 0;
            template.setLoadBalanceMode(loadBalanceMode);
            
            portTemplates.add(template);
        }
        return portTemplates;
    }
    
    private static boolean isIllegalString(String str) {
        return StringUtils.isBlank(str) 
                || str.trim().equalsIgnoreCase("null")
                || str.trim().toLowerCase().startsWith("_null");
    }
    
    /**
     * Restore the Port Access Profile table.<br>
     * Need to restore below tables first:
     * <ul>
     * <li>{@link UserProfile}</li>
     * <li>{@link LocalUserGroup}</li>
     * <li>{@link RadiusAssignment}</li>
     * <li>{@link Cwp}</li>
     * <li>{@link ServiceFilter}</li>
     * <li>{@link Vlan}</li>
     * </ul>
     * 
     * @author Yunzhi Lin
     *         - Time: Nov 16, 2012 3:43:47 PM
     * @return True or False
     */
    public static boolean restorePortAccessProfile() {
        try {
            List<PortAccessProfile> accProfiles = getAllAccessProfiles();
            Map<String, Set<UserProfile>> authOkUserProfileMap = getAllUserProfiles(
                    "access_authok_userprofile", "access_profile_id",
                    "user_profile_id");
            Map<String, Set<UserProfile>> authOkDataUserProfileMap = getAllUserProfiles(
                    "access_authok_data_userprofile", "access_profile_id",
                    "user_profile_id");
            Map<String, Set<UserProfile>> authFailUserProfileMap = getAllUserProfiles(
                    "access_authfail_userprofile", "access_profile_id",
                    "user_profile_id");
            Map<String, Set<LocalUserGroup>> localUserGroupMap = getLocalLocalUserGroups(
                    "access_radius_user_group", "access_profile_id",
                    "local_user_group_id");

            if (null == accProfiles) {
                AhRestoreDBTools
                        .logRestoreMsg("Error when restore AccessProfile: the Access list is null");
                return false;
            } else {
                List<Long> olds = new ArrayList<Long>();
                for (PortAccessProfile temp : accProfiles) {
                    if (temp != null) {
                        if(!(null == authOkUserProfileMap || authOkUserProfileMap.isEmpty())) {
                            temp.setAuthOkUserProfile(authOkUserProfileMap.get(temp.getId().toString()));
                        }
                        if(!(null == authOkDataUserProfileMap || authOkDataUserProfileMap.isEmpty())) {
                            temp.setAuthOkDataUserProfile(authOkDataUserProfileMap.get(temp.getId().toString()));
                        }
                        if(!(null == authFailUserProfileMap || authFailUserProfileMap.isEmpty())) {
                            temp.setAuthFailUserProfile(authFailUserProfileMap.get(temp.getId().toString()));
                            if(temp.getAuthFailUserProfile() != null && !temp.getAuthFailUserProfile().isEmpty()){
                            	 temp.setEnabledSameVlan(false);
                            }
                        }
                        if(!(null == localUserGroupMap || localUserGroupMap.isEmpty())) {
                            temp.setRadiusUserGroups(localUserGroupMap.get(temp.getId().toString()));
                        }
                    }
                    olds.add(temp.getId());
                }

                QueryUtil.restoreBulkCreateBos(accProfiles);

                for (int i = 0; i < accProfiles.size(); i++) {
                    AhRestoreNewMapTools.setMapWiredPortAccessId(olds.get(i),
                            accProfiles.get(i).getId());
                }
            }
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Error when restore AccessProfile", e);
            return false;
        }
        return true;
    }

    private static List<PortAccessProfile> getAllAccessProfiles()
            throws AhRestoreException, AhRestoreColNotExistException {

        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        String tableName = "port_access_profile";
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        List<PortAccessProfile> accProfiles = new ArrayList<PortAccessProfile>();
        boolean isColPresent;
        String colName;
        PortAccessProfile accProfile;

        for (int i = 0; i < rowCount; i++) {
            accProfile = new PortAccessProfile();

            colName = "id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
            accProfile.setId(AhRestoreCommons.convertLong(id));

            colName = "owner";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            long ownerId = isColPresent ? AhRestoreCommons
                    .convertLong(xmlParser.getColVal(i, colName)) : 1;
            HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
            if (null == hmDomain) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'port_access_profile' data be lost, cause: 'owner' column is not available.");
                continue;
            }
            accProfile.setOwner(hmDomain);

            colName = "name";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'port_access_profile' data be lost, cause: 'name' column is not exist.");
                continue;
            }
            String name = xmlParser.getColVal(i, colName);
            if (isIllegalString(name)) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'port_access_profile' data be lost, cause: 'name' column value is null.");
                continue;
            }
            accProfile.setName(name.trim());

            colName = "description";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String description = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            accProfile.setDescription(AhRestoreCommons
                    .convertString(description));

            /*--------------Access Mode-------------------------*/
            colName = "portType";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int portType = AhRestoreCommons.convertInt(
                    xmlParser.getColVal(i, colName),
                    PortAccessProfile.PORT_TYPE_NONE);
            accProfile.setPortType((short) portType);
            
            colName = "product";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int product = PortAccessProfile.CHESAPEAKE;
            if(isColPresent) {
                product = AhRestoreCommons.convertInt(
                        xmlParser.getColVal(i, colName),
                        PortAccessProfile.CHESAPEAKE);
            }
            accProfile.setProduct((short) product);
            
            /*--------------Port Description---------------------------*/
            colName = "portDescription";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String portDescription = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            accProfile.setPortDescription(AhRestoreCommons
                    .convertString(portDescription));
            
            /*--------------Shutdown ports---------------------------*/
            colName = "shutDownPorts";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String shutDownPorts = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            accProfile.setShutDownPorts(AhRestoreCommons
                    .convertStringToBoolean(shutDownPorts));

            /*------------Authentication----------------*/
            colName = "enabledIDM";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledIDM = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
                    accProfile.setEnabledIDM(AhRestoreCommons
                            .convertStringToBoolean(enabledIDM));
                    
            colName = "enabled8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabled8021X = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
            accProfile.setEnabled8021X(AhRestoreCommons
                    .convertStringToBoolean(enabled8021X));

            colName = "first8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String first8021X = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            accProfile.setFirst8021X(AhRestoreCommons
                    .convertStringToBoolean(first8021X));

            colName = "interval8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int interval8021X = AhRestoreCommons.convertInt(
                    xmlParser.getColVal(i, colName), 30);
            accProfile.setInterval8021X(interval8021X);
            
            colName = "enabledMAC";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledMAC = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            accProfile.setEnabledMAC(AhRestoreCommons
                    .convertStringToBoolean(enabledMAC));

            colName = "enabledSameVlan";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledSameVlan = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnabledSameVlan(AhRestoreCommons
                    .convertStringToBoolean(enabledSameVlan));
            
            if(RestoreHiveAp.restore_from_glasgow_before
            		&& accProfile.getProduct() == PortAccessProfile.BRANCH_ROUTER
            		&& (accProfile.isEnabled8021X() || accProfile.isEnabledCWP() || accProfile.isEnabledMAC())){
            	accProfile.setEnabledSameVlan(true);
            }

            colName = "authProtocol";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int authProtocol = AhRestoreCommons.convertInt(
                    xmlParser.getColVal(i, colName), Cwp.AUTH_METHOD_PAP);
            accProfile.setAuthProtocol(authProtocol);

            colName = "radius_service_assign_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String radius_service_assign_id = isColPresent ? xmlParser
                    .getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(radius_service_assign_id)) {
                Long newRadiusServiceAssignId = AhRestoreNewMapTools
                        .getMapRadiusServerAssign(Long
                                .parseLong(radius_service_assign_id.trim()));
                RadiusAssignment radiusAssignment = AhRestoreNewTools
                        .CreateBoWithId(RadiusAssignment.class,
                                newRadiusServiceAssignId);
                accProfile.setRadiusAssignment(radiusAssignment);
            }

            colName = "enabledApAuth";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledApAuth = isColPresent ? xmlParser.getColVal(i,
                    colName) : "f";
            accProfile.setEnabledApAuth(AhRestoreCommons
                    .convertStringToBoolean(enabledApAuth));

            colName = "enabledCWP";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledCWP = isColPresent ? xmlParser.getColVal(i, colName)
                    : "f";
            accProfile.setEnabledCWP(AhRestoreCommons
                    .convertStringToBoolean(enabledCWP));

            colName = "cwp_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String cwp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(cwp_id)) {
                Long newCwpId = AhRestoreNewMapTools.getMapCapWebPortal(Long
                        .parseLong(cwp_id.trim()));
                Cwp cwp = AhRestoreNewTools.CreateBoWithId(Cwp.class, newCwpId);
                accProfile.setCwp(cwp);
            }

            colName = "voice_vlan_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String voice_vlan_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            if (NmsUtil.isNotBlankId(voice_vlan_id)) {
                Long newVoiceVlanId = AhRestoreNewMapTools.getMapVlan(Long
                        .parseLong(voice_vlan_id.trim()));
                if (null != newVoiceVlanId) {
                    Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,
                            newVoiceVlanId);
                    accProfile.setVoiceVlan(vlan);
                }
            }
            colName = "data_vlan_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String data_vlan_id = isColPresent ? xmlParser
                    .getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(data_vlan_id)) {
                Long newDataVlanId = AhRestoreNewMapTools.getMapVlan(Long
                        .parseLong(data_vlan_id.trim()));
                if (null != newDataVlanId) {
                    Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,
                            newDataVlanId);
                    accProfile.setDataVlan(vlan);
                }
            }
                    
            colName = "userprofile_def_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String userprofile_default_id = isColPresent ? xmlParser.getColVal(
                    i, colName) : "";
            if (NmsUtil.isNotBlankId(userprofile_default_id)) {
                Long newUserprofileDefaultId = AhRestoreNewMapTools
                        .getMapUserProfile(Long
                                .parseLong(userprofile_default_id.trim()));
                UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(
                        UserProfile.class, newUserprofileDefaultId);
                accProfile.setDefUserProfile(userprofile);
            }

            colName = "userprofile_selfreg_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String userprofile_selfreg_id = isColPresent ? xmlParser.getColVal(
                    i, colName) : "";
            if (NmsUtil.isNotBlankId(userprofile_selfreg_id)) {
                Long newUserprofileSelfRegId = AhRestoreNewMapTools
                        .getMapUserProfile(Long
                                .parseLong(userprofile_selfreg_id.trim()));
                UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(
                        UserProfile.class, newUserprofileSelfRegId);
                accProfile.setSelfRegUserProfile(userprofile);
            }
            
            colName = "userprofile_guest_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String userprofile_guest_id = isColPresent ? xmlParser.getColVal(
                    i, colName) : "";
            if (NmsUtil.isNotBlankId(userprofile_guest_id)) {
                Long newUserprofileGuestId = AhRestoreNewMapTools
                        .getMapUserProfile(Long
                                .parseLong(userprofile_guest_id.trim()));
                UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(
                        UserProfile.class, newUserprofileGuestId);
                accProfile.setGuestUserProfile(userprofile);
            }

            /*------------ 802.1X user profiles reassignment settings ----------------*/
            colName = "denyAction";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String denyAction = isColPresent ? xmlParser.getColVal(i, colName)
                    : "3";
            accProfile.setDenyAction((short) AhRestoreCommons
                    .convertInt(denyAction));

            colName = "actionTime";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String actionTime = isColPresent ? xmlParser.getColVal(i, colName)
                    : "60";
            accProfile.setActionTime(AhRestoreCommons.convertLong(actionTime));

            colName = "chkUserOnly";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String chkUserOnly = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            accProfile.setChkUserOnly(AhRestoreCommons
                    .convertStringToBoolean(chkUserOnly));

            colName = "chkDeauthenticate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String chkDeauthenticate = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setChkDeauthenticate(AhRestoreCommons
                    .convertStringToBoolean(chkDeauthenticate));

            colName = "enableosdection";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableosdection = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableOsDection(AhRestoreCommons
                    .convertStringToBoolean(enableosdection));

            /*------------ radius user groups and radius attribute settings ----------------*/
            colName = "enableAssignUserProfile";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableAssignUserProfile = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
            accProfile.setEnableAssignUserProfile(AhRestoreCommons
                    .convertStringToBoolean(enableAssignUserProfile));

            colName = "assignUserProfileAttributeId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String assignUserProfileAttributeId = isColPresent ? xmlParser
                    .getColVal(i, colName) : "0";
            accProfile.setAssignUserProfileAttributeId(AhRestoreCommons
                    .convertInt(assignUserProfileAttributeId));

            colName = "assignUserProfileVenderId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String assignUserProfileVenderId = isColPresent ? xmlParser
                    .getColVal(i, colName) : "0";
            accProfile.setAssignUserProfileVenderId(AhRestoreCommons
                    .convertInt(assignUserProfileVenderId));

            colName = "userProfileAttributeType";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int userProfileAttributeType = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 1) : 1;
            accProfile
                    .setUserProfileAttributeType((short) userProfileAttributeType);

            /*------------QOS Settings----------------*/
            colName = "qosClassificationMode";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String qosClassificationMode = isColPresent ? xmlParser.getColVal(i,
                    colName) : String.valueOf(PortAccessProfile.QOS_CLASSIFICATION_MODE_UNTRUSTED);
            accProfile.setQosClassificationMode((short)AhRestoreCommons
                    .convertInt(qosClassificationMode));
            
            colName = "qosClassificationTrustMode";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String qosClassificationTrustMode = isColPresent ? xmlParser.getColVal(i,
                    colName) : String.valueOf(PortAccessProfile.QOS_CLASSIFICATION_TRUST_DSCP);
            accProfile.setQosClassificationTrustMode((short)AhRestoreCommons
                    .convertInt(qosClassificationTrustMode));
            
            colName = "enableTrustedProiority";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableTrustedProiority = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableTrustedProiority(AhRestoreCommons
                    .convertStringToBoolean(enableTrustedProiority));
            
            colName = "trustedPriority";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int trustedPriority = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 2) : 2;
            accProfile
                    .setTrustedPriority((short) trustedPriority);
            
            colName = "untrustedPriority";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int untrustedPriority = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 2) : 2;
            accProfile
                    .setUntrustedPriority((short) untrustedPriority);
            
            colName = "enableQosMark";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableQosMark = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableQosMark(AhRestoreCommons
                    .convertStringToBoolean(enableQosMark));
            
            colName = "qosMarkMode";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int qosMarkMode = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 0) : PortAccessProfile.QOS_CLASSIFICATION_TRUST_DSCP;
            accProfile
                    .setQosMarkMode((short) qosMarkMode);
            
            colName = "enableEthLimitDownloadBandwidth";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableEthLimitDownloadBandwidth = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableEthLimitDownloadBandwidth(AhRestoreCommons
                    .convertStringToBoolean(enableEthLimitDownloadBandwidth));
            
            colName = "enableEthLimitUploadBandwidth";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableEthLimitUploadBandwidth = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableEthLimitUploadBandwidth(AhRestoreCommons
                    .convertStringToBoolean(enableEthLimitUploadBandwidth));
            
            colName = "ethLimitDownloadRate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int ethLimitDownloadRate = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 100) : 100;
            accProfile
                    .setEthLimitDownloadRate((short) ethLimitDownloadRate);
            
            colName = "ethLimitUploadRate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int ethLimitUploadRate = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 100) : 100;
            accProfile
                    .setEthLimitUploadRate((short) ethLimitUploadRate);
            
            colName = "enableUSBLimitDownloadBandwidth";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableUSBLimitDownloadBandwidth = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableUSBLimitDownloadBandwidth(AhRestoreCommons
                    .convertStringToBoolean(enableUSBLimitDownloadBandwidth));
            
            colName = "enableUSBLimitUploadBandwidth";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableUSBLimitUploadBandwidth = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            accProfile.setEnableUSBLimitUploadBandwidth(AhRestoreCommons
                    .convertStringToBoolean(enableUSBLimitUploadBandwidth));
            
            colName = "usbLimitDownloadRate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int usbLimitDownloadRate = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 100) : 100;
            accProfile
                    .setUsbLimitDownloadRate((short) usbLimitDownloadRate);
            
            colName = "usbLimitUploadRate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int usbLimitUploadRate = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName), 100) : 100;
            accProfile
                    .setUsbLimitUploadRate((short) usbLimitUploadRate);

            /*------------Optional----------------*/
            colName = "enableMDM";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableMDM = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            accProfile.setEnableMDM(AhRestoreCommons.convertStringToBoolean(enableMDM));
            
            colName = "configmdm_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String configMDM_id = isColPresent ? xmlParser
                    .getColVal(i, colName) : "";
            ConfigTemplateMdm config = null;
            if (NmsUtil.isNotBlankId(configMDM_id)) {
                Long newConfigMDM_id = AhRestoreNewMapTools
                        .getMapConfigTemplateMDM(Long.parseLong(configMDM_id
                                .trim()));
                if (null != newConfigMDM_id) {
                    config = AhRestoreNewTools.CreateBoWithId(
                            ConfigTemplateMdm.class, newConfigMDM_id);
                    accProfile.setConfigtempleMdm(config);
                }
            }
            
            colName = "enabledClientReport";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabledClientReport = isColPresent ? xmlParser.getColVal(i,
                    colName) : "f";
            accProfile.setEnabledClientReport(AhRestoreCommons.convertStringToBoolean(enabledClientReport));
            
            colName = "service_filter_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String service_filter_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            ServiceFilter serviceFilter = null;
            if (NmsUtil.isNotBlankId(service_filter_id)) {
                Long newServiceFilterId = AhRestoreNewMapTools
                        .getMapMgtServiceFilter(Long
                                .parseLong(service_filter_id.trim()));
                serviceFilter = AhRestoreNewTools.CreateBoWithId(
                        ServiceFilter.class, newServiceFilterId);
            }
            if (serviceFilter == null) {
                serviceFilter = QueryUtil.findBoByAttribute(
                        ServiceFilter.class, "defaultFlag", true);
            }
            accProfile.setServiceFilter(serviceFilter);
            
            colName = "authSequence";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            int authSequence = PortAccessProfile.AUTH_SEQUENCE_MAC_LAN_CWP;
            if(isColPresent) {
                authSequence = AhRestoreCommons.convertInt(
                        xmlParser.getColVal(i, colName),
                        PortAccessProfile.AUTH_SEQUENCE_MAC_LAN_CWP);
            }
            accProfile.setAuthSequence(authSequence);
            
            /*------------ VLAN settings ----------------*/
            colName = "native_vlan_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String native_vlan_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            if (NmsUtil.isNotBlankId(native_vlan_id)) {
                Long vlanId = AhRestoreNewMapTools.getMapVlan(Long
                        .parseLong(native_vlan_id.trim()));
                if (null != vlanId) {
                    Vlan nativeVlan = AhRestoreNewTools.CreateBoWithId(
                            Vlan.class, vlanId);
                    accProfile.setNativeVlan(nativeVlan);
                }
            }
            colName = "allowedVlan";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String allowedVlan = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if(!isIllegalString(allowedVlan)) {
                accProfile.setAllowedVlan(allowedVlan);
            }

            accProfiles.add(accProfile);
        }

        return accProfiles.isEmpty() ? null : accProfiles;
    }
    
    private static Map<String, Set<UserProfile>> getAllUserProfiles(
            String tableName, String columnName1, String columnName2)
            throws AhRestoreException, AhRestoreColNotExistException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of $tableName.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        Map<String, Set<UserProfile>> userProfileInfo = new HashMap<String, Set<UserProfile>>();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            colName = columnName1;
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (isIllegalString(profileId)) {
                continue;
            }

            /**
             * Set user_profile_id
             */
            colName = columnName2;
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                continue;
            }

            String userProfileId = xmlParser.getColVal(i, colName);
            if (isIllegalString(userProfileId)) {
                continue;
            }

            Long newUserProfileId = AhRestoreNewMapTools.getMapUserProfile(Long
                    .parseLong(userProfileId.trim()));
            UserProfile userProfile = AhRestoreNewTools.CreateBoWithId(
                    UserProfile.class, newUserProfileId);

            if (userProfile != null) {
                if (userProfileInfo.get(profileId) == null) {
                    Set<UserProfile> userProfileSet = new HashSet<UserProfile>();
                    userProfileSet.add(userProfile);
                    userProfileInfo.put(profileId, userProfileSet);
                } else {
                    userProfileInfo.get(profileId).add(userProfile);
                }
            }

        }

        return userProfileInfo;
    }
    
    private static Map<String, Set<LocalUserGroup>> getLocalLocalUserGroups(
            String tableName, String columnName1, String columnName2)
            throws AhRestoreColNotExistException, AhRestoreException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of $tableName.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);

        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        Map<String, Set<LocalUserGroup>> localUserGroupInfo = new HashMap<String, Set<LocalUserGroup>>();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            colName = columnName1;
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);

            if (!isColPresent) {
                /*
                 * The column must be exist in the table of $tableName
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (isIllegalString(profileId)) {
                continue;
            }

            /*
             * Set local_user_group_id
             */
            colName = columnName2;
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);

            if (!isColPresent) {
                /*
                 * The local_user_group_id column must be exist in the table of
                 * $tableName
                 */
                continue;
            }

            String groupId = xmlParser.getColVal(i, colName);
            if (isIllegalString(groupId)) {
                continue;
            }

            Long newGroupId = AhRestoreNewMapTools.getMapLocalUserGroup(Long
                    .parseLong(groupId.trim()));
            LocalUserGroup localUserGroup = AhRestoreNewTools.CreateBoWithId(
                    LocalUserGroup.class, newGroupId);

            if (localUserGroup != null) {
                if (localUserGroupInfo.get(profileId) == null) {
                    Set<LocalUserGroup> localUserGroupSet = new HashSet<LocalUserGroup>();
                    localUserGroupSet.add(localUserGroup);
                    localUserGroupInfo.put(profileId, localUserGroupSet);
                } else {
                    localUserGroupInfo.get(profileId).add(localUserGroup);
                }
            }
        }

        return localUserGroupInfo;
    }
    
	public static  Map<String, List<PortPseProfile>> getAllPortPseProfile() throws AhRestoreException, AhRestoreColNotExistException {
    	AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        String tableName = "PORT_PSE_PROFILE";
        Map<String, List<PortPseProfile>> map = new HashMap<String, List<PortPseProfile>>();
    	 boolean restoreRet = xmlParser.readXMLFile(tableName);
         if (!restoreRet) {
             AhRestoreDBTools
                     .logRestoreMsg("SAXReader cannot read PORT_PSE_PROFILE file.");
             return null;
         }

         int rowCount = xmlParser.getRowCount();

         boolean isColPresent;
         String colName;
         
         for (int i = 0; i < rowCount; i++) {

        	 PortPseProfile pse = new PortPseProfile();
        	 
        	 colName = "PORT_GROUP_PROFILE_ID";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             if (!isColPresent) {
                 continue;
             }

             String portgroupId = xmlParser.getColVal(i, colName);
             if (isIllegalString(portgroupId)) {
                 continue;
             }

             colName = "interfaceNum";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String interfaceNum = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if (isIllegalString(interfaceNum)) {
            	 AhRestoreDBTools
                 .logRestoreMsg("Restore table 'PORT_PSE_PROFILE' data be lost, cause: 'interfaceNum' column value is null.");
                 continue;
             }
             pse.setInterfaceNum((short)AhRestoreCommons.convertInt(interfaceNum));
             
             colName = "enabelIfPse";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String enabelIfPse = isColPresent ? xmlParser.getColVal(i, colName)
                     : "false";
             pse.setEnabelIfPse(AhRestoreCommons.convertStringToBoolean(enabelIfPse));
             
             /**
              * Set PSE_PROFILE_ID
              */
             colName = "PSE_PROFILE_ID";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String pseProfileId = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if (!isIllegalString(pseProfileId)) {
            	 Long newPseProfileId = AhRestoreNewMapTools.getMapPseProfile(Long
                         .parseLong(pseProfileId.trim()));
                 PseProfile pseProfile = AhRestoreNewTools.CreateBoWithId(
                		 PseProfile.class, newPseProfileId);
                 pse.setPseProfile(pseProfile);
             }
             
             if (map.get(portgroupId) == null) {
            	 List<PortPseProfile> portPseProfiles = new ArrayList<PortPseProfile>();;
            	 portPseProfiles.add(pse);
 				 map.put(portgroupId, portPseProfiles);
 			} else {
 				map.get(portgroupId).add(pse);
 			}
             
         }
         return map;
    }

	public static Map<String, List<PortMonitorProfile>> getAllPortMonitorProfile() throws AhRestoreException, AhRestoreColNotExistException {
    	AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        String tableName = "port_monitor_profile";
        Map<String, List<PortMonitorProfile>> map = new HashMap<String ,List<PortMonitorProfile>>();
        
    	 boolean restoreRet = xmlParser.readXMLFile(tableName);
         if (!restoreRet) {
             AhRestoreDBTools
                     .logRestoreMsg("SAXReader cannot read port_monitor_profile file.");
             return null;
         }

         int rowCount = xmlParser.getRowCount();

         boolean isColPresent;
         String colName;
         
         for (int i = 0; i < rowCount; i++) {

        	 PortMonitorProfile monitor = new PortMonitorProfile();
        	 
             /*Long newId = null;*/
             colName = "portgroups_id";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             if (!isColPresent) {
                 continue;
             }
             String portgroupId = isColPresent ? xmlParser.getColVal(i,
                     colName) : "";
             if (isIllegalString(portgroupId)) {
                 continue;
             }
             
            /* if (NmsUtil.isNotBlankId(portgroupId)) {
            	 newId = AhRestoreNewMapTools.getMapWiredPortTemplateId(Long
                         .parseLong(portgroupId.trim()));
               
             }
             if (null == newId) {
                 continue;
             }*/

             colName = "destinationPort";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String destinationPort = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if (isIllegalString(destinationPort)) {
            	 AhRestoreDBTools
                 .logRestoreMsg("Restore table 'port_monitor_profile' data be lost, cause: 'destinationPort' column value is null.");
                 continue;
             }
             
             destinationPort = replaceSFPPort(destinationPort);
             if(!NumberUtils.isNumber(destinationPort)){
            	 destinationPort = Short.toString(DeviceInfType.Gigabit.getFinalValue(AhRestoreCommons.convertInt(destinationPort.substring(3)), (short)-1));
             }
             try {
				monitor.setDestinationPort(Short.valueOf(destinationPort));
			} catch (NumberFormatException e) {
				AhRestoreDBTools
                .logRestoreMsg("Restore table 'port_monitor_profile' data be lost,cause:" + e);
			}
             
             colName = "ingressPort";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String ingressPort = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if(isIllegalString(ingressPort)){
            	 ingressPort = "";
             }
             ingressPort = replaceSFPPort(ingressPort);
             monitor.setIngressPort(ingressPort);
             
             colName = "egressPort";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String egressPort = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if(isIllegalString(egressPort)){
            	 egressPort = "";
             }
             egressPort = replaceSFPPort(egressPort);
             monitor.setEgressPort(egressPort);
             
             colName = "bothPort";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String bothPort = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if(isIllegalString(bothPort)){
            	 bothPort = "";
             }
             bothPort = replaceSFPPort(bothPort);
             monitor.setBothPort(bothPort);
             
             colName = "ingressVlan";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String ingressVlan = isColPresent ? xmlParser.getColVal(i, colName)
                     : null;
             if(isIllegalString(ingressVlan)){
            	 ingressVlan = "";
             }
             monitor.setIngressVlan(ingressVlan);
             
             colName = "enableMonitorSession";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String enableMonitorSession = isColPresent ? xmlParser.getColVal(i, colName)
                     : "false";
             monitor.setEnableMonitorSession(AhRestoreCommons
                     .convertStringToBoolean(enableMonitorSession));
             
             colName = "enablePorts";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String enablePorts = isColPresent ? xmlParser.getColVal(i, colName)
                     : "false";
             monitor.setEnablePorts(AhRestoreCommons
                     .convertStringToBoolean(enablePorts));
             
             colName = "enableVlans";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String enableVlans;
             
             //support upgrade for mirror redesign
             boolean RESTORE_HM_BEFORE_FIRENZE = RestoreHiveAp.isRestoreHmBeforeVersion("6.0.2.0");
             if(isColPresent){
            	 enableVlans = xmlParser.getColVal(i, colName);
             }else{
            	if(monitor.isEnableMonitorSession() && !monitor.getIngressVlan().isEmpty()){
            		enableVlans = "true";
            	}else{
            		enableVlans = "false";
            	}
             }
             monitor.setEnableVlans(AhRestoreCommons
                     .convertStringToBoolean(enableVlans));
             
             if(RESTORE_HM_BEFORE_FIRENZE){
				if (!monitor.getBothPort().isEmpty()
						|| !monitor.getEgressPort().isEmpty()
						|| !monitor.getIngressPort().isEmpty()) {
            		 monitor.setEnablePorts(true);
            	 }
             }
             
             if (map.get(portgroupId) == null) {
            	 List<PortMonitorProfile> monitorProfiles = new ArrayList<PortMonitorProfile>();;
            	 monitorProfiles.add(monitor);
 				 map.put(portgroupId, monitorProfiles);
 			} else {
 				map.get(portgroupId).add(monitor);
 			}
         }
         return map;
    }
	
    public static boolean restoreLANProfile() {
        if(RESTORE_HM_BEFORE_ESSON) {
            try {
                AhRestoreDBTools.logRestoreMsg("Try to restore LAN profile data before the 6.0 version.");
                
                List<LanProfile> lanProfiles = getAllLANProfiles();
                
                if (null == lanProfiles) {
                    AhRestoreDBTools.logRestoreMsg("Error when restoreLANProfile: the LAN list is null");
                    return false;
                } else {
                    Map<String, Set<VpnNetwork>> allNetworks = getAllVPNNetworks(
                            "lan_profile_regular_networks", "lan_profile_id",
                            "networks_id");
                    Map<String, Set<Vlan>> allVlans = getAllVlans(
                            "lan_profile_regular_vlan", "lan_profile_id",
                            "vlan_id");
                    Map<String, Set<UserProfile>> allRadiusUserProfile = getAllUserProfiles(
                            "lan_profile_user_profile", "lan_profile_id",
                            "user_profile_id");
                    Map<String, Set<LocalUserGroup>> allRadiusUserGroups = getLocalLocalUserGroups(
                            "lan_radius_user_group", "lan_profile_id",
                            "local_user_group_id");
                    
                    List<Long> lOldId = new ArrayList<Long>();
                    List<PortAccessProfile> accessProfiles = new ArrayList<>();
                    for (LanProfile tempLanProfile : lanProfiles) {
                        if (tempLanProfile != null) {
                            if (allNetworks != null) {
                                tempLanProfile.setRegularNetworks(allNetworks
                                        .get(tempLanProfile.getId().toString()));
                            }
                            if (allVlans != null) {
                                tempLanProfile.setRegularVlans(allVlans
                                        .get(tempLanProfile.getId().toString()));
                            }
                            if (allRadiusUserProfile != null) {
                                tempLanProfile.setRadiusUserProfile(allRadiusUserProfile
                                        .get(tempLanProfile.getId()
                                                .toString()));
                            }
                            if (allRadiusUserGroups != null) {
                                tempLanProfile.setRadiusUserGroups(allRadiusUserGroups
                                        .get(tempLanProfile.getId()
                                                .toString()));
                            }
                            // set the old id
                            lOldId.add(tempLanProfile.getId());
                            // bind the LAN profile with old id (the map will be used in Network policy)
                            AhRestoreNewMapTools.setMapLanProfileObj(tempLanProfile.getId(),
                                    tempLanProfile);
                            PortAccessProfile access = convertLAN2Access(tempLanProfile);
                            accessProfiles.add(access);
                        }
                    }
                    
                    QueryUtil.restoreBulkCreateBos(accessProfiles);
                    
                    for (int i = 0; i < accessProfiles.size(); i++) {
                        AhRestoreNewMapTools.setMapLanProfile(lOldId.get(i),
                                accessProfiles.get(i).getId());
                        // for restore the tagged Network <-> VLAN mapping
                        AhRestoreNewMapTools.setMapLanProfileReverse(accessProfiles.get(i).getId(), lOldId.get(i));
                    }
                }
            } catch (Exception e) {
                AhRestoreDBTools.logRestoreMsg("Error when restore LANs", e);
                return false;
            }
        }
        return true;
    }

    private static PortAccessProfile convertLAN2Access(LanProfile tempLanProfile) {
        if(null == tempLanProfile) {
            return null;
        } else {
            PortAccessProfile access = new PortAccessProfile();
            access.setOwner(tempLanProfile.getOwner());
            access.setName(tempLanProfile.getName());
            access.setDescription(tempLanProfile.getDescription());
            access.setPortType(tempLanProfile.isEnabled8021Q() ? PortAccessProfile.PORT_TYPE_8021Q : PortAccessProfile.PORT_TYPE_ACCESS);
            access.setProduct(PortAccessProfile.BRANCH_ROUTER);
            
            access.setEnabled8021X(tempLanProfile.isEnabled8021X());
            access.setEnabledMAC(tempLanProfile.isMacAuthEnabled());
            access.setFirst8021X(!tempLanProfile.isEnabled8021X() && tempLanProfile.isMacAuthEnabled() ? false : true);
            access.setAuthProtocol(tempLanProfile.getAuthProtocol());
            access.setRadiusAssignment(tempLanProfile.getRadiusAssignment());
            
            access.setEnabledCWP(tempLanProfile.isCwpSelectEnabled());
            access.setCwp(tempLanProfile.getCwp());
            
            access.setDefUserProfile(tempLanProfile.getUserProfileDefault());
            access.setSelfRegUserProfile(tempLanProfile.getUserProfileSelfReg());
            Set<UserProfile> authOkUserProfiles = tempLanProfile.getRadiusUserProfile();
            if(null != authOkUserProfiles && !authOkUserProfiles.isEmpty()) {
                access.setAuthOkUserProfile(authOkUserProfiles);
            }
            
            access.setServiceFilter(tempLanProfile.getServiceFilter());
            
            access.setAuthSequence(tempLanProfile.getAuthSequence());
            
            access.setNativeVlan(tempLanProfile.getNativeVlan());
            if(null != tempLanProfile.getNativeNetwork()) {
                Vlan vlanObj = null;
                final Long networkId = tempLanProfile.getNativeNetwork().getId();
                Long vlanId = AhRestoreNewMapTools.getMapNetworkObjectVlanWithNewID(networkId);
                if(vlanId!=null) {
                    vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,vlanId);
                }
                if(null != vlanObj) {
                    access.setNativeVlan(vlanObj);
                    // add to cache
                    AhRestoreNewMapTools.setMapVLANObjectNetwork(tempLanProfile.getId(), vlanId, networkId);
                }
            }
            if(tempLanProfile.isEnabled8021Q()) {
                // set Allowed VLANs
                Set<Vlan> allowedVLANs = tempLanProfile.getRegularVlans();
                Set<VpnNetwork> allowedNetworks = tempLanProfile.getRegularNetworks();
                if(null != allowedNetworks && !allowedNetworks.isEmpty()) {
                    if(null == allowedVLANs) {
                        allowedVLANs = new HashSet<>();
                    }
                    allowedVLANs.clear();
                    for (VpnNetwork vpnNetwork : allowedNetworks) {
                        Vlan vlanObj = null;
                        final Long networkId = vpnNetwork.getId();
                        Long vlanId = AhRestoreNewMapTools.getMapNetworkObjectVlanWithNewID(networkId);
                        if(vlanId!=null) {
                            vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,vlanId);
                            if(null != vlanObj) {
                                allowedVLANs.add(vlanObj);
                                // add to cache
                                AhRestoreNewMapTools.setMapVLANObjectNetwork(tempLanProfile.getId(), vlanId, networkId);
                            }
                        }
                    }
                }
                // get the global VLAN Id
                if(null != allowedVLANs) {
                    List<Integer> allowedVLANIds = new ArrayList<>();
                    for (Vlan vlan : allowedVLANs) {
                        if(null != vlan.getId()) {
                            Vlan allowedVlan = QueryUtil.findBoById(Vlan.class, vlan.getId(), new QueryBo() {
                                @Override
                                public Collection<HmBo> load(HmBo bo) {
                                    if (bo instanceof Vlan) {
                                        Vlan vlan = (Vlan) bo;
                                        if(null != vlan.getItems()) {
                                            vlan.getItems().size();
                                        }
                                    }
                                    return null;
                                }
                            });
                            if(null != allowedVlan) {
                                if(null != allowedVlan.getItems()) {
                                    for (SingleTableItem item : allowedVlan.getItems()) {
                                        if(item.getType() == SingleTableItem.TYPE_GLOBAL) {
                                            if(!allowedVLANIds.contains(item.getVlanId())) {
                                                allowedVLANIds.add(item.getVlanId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!allowedVLANIds.isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (Integer id : allowedVLANIds) {
                            builder.append(id);
                            builder.append(",");
                        }
                        builder.deleteCharAt(builder.length()-1);
                        access.setAllowedVlan(builder.toString());
                    }
                }
            }
            
            access.setDenyAction(tempLanProfile.getDenyAction());
            access.setActionTime(tempLanProfile.getActionTime());
            access.setChkUserOnly(tempLanProfile.isChkUserOnly());
            access.setEnableOsDection(tempLanProfile.isEnableOsDection());
            access.setChkDeauthenticate(tempLanProfile.isChkDeauthenticate());
            access.setEnableAssignUserProfile(tempLanProfile.isEnableAssignUserProfile());
            access.setAssignUserProfileAttributeId(tempLanProfile.getAssignUserProfileAttributeId());
            access.setAssignUserProfileVenderId(tempLanProfile.getAssignUserProfileVenderId());
            Set<LocalUserGroup> localRadiusUserGroups = tempLanProfile.getRadiusUserGroups();
            if(null != localRadiusUserGroups && !localRadiusUserGroups.isEmpty()) {
                access.setRadiusUserGroups(localRadiusUserGroups);
            }
            
            return access;
        }
    }

    private static List<LanProfile> getAllLANProfiles() throws AhRestoreException, AhRestoreColNotExistException {

        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        String tableName = "lan_profile";
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        List<LanProfile> lanProfiles = new ArrayList<LanProfile>();
        boolean isColPresent;
        String colName;
        LanProfile lanProfile;

        for (int i = 0; i < rowCount; i++) {
            lanProfile = new LanProfile();
            LanInterfacesMode interfaceMode = lanProfile.getLanInterfacesMode();

            colName = "id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
            lanProfile.setId(AhRestoreCommons.convertLong(id));

            colName = "owner";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            long ownerId = isColPresent ? AhRestoreCommons
                    .convertLong(xmlParser.getColVal(i, colName)) : 1;
            HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
            if (null == hmDomain) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'lan_profile' data be lost, cause: 'owner' column is not available.");
                continue;
            }
            lanProfile.setOwner(hmDomain);

            colName = "name";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'lan_profile' data be lost, cause: 'name' column is not exist.");
                continue;
            }
            String name = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(name)) {
                AhRestoreDBTools
                        .logRestoreMsg("Restore table 'lan_profile' data be lost, cause: 'name' column value is null.");
                continue;
            }
            lanProfile.setName(name.trim());

            colName = "description";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String description = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            lanProfile.setDescription(AhRestoreCommons
                    .convertString(description));

            /*--------------Interface Mode-------------------------*/
            colName = "eth0_on";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String ethxOn = isColPresent ? xmlParser.getColVal(i, colName) : "";
            interfaceMode.setEth0On(AhRestoreCommons
                    .convertStringToBoolean(ethxOn));
            colName = "eth1_on";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            ethxOn = isColPresent ? xmlParser.getColVal(i, colName) : "";
            interfaceMode.setEth1On(AhRestoreCommons
                    .convertStringToBoolean(ethxOn));
            colName = "eth2_on";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            ethxOn = isColPresent ? xmlParser.getColVal(i, colName) : "";
            interfaceMode.setEth2On(AhRestoreCommons
                    .convertStringToBoolean(ethxOn));
            colName = "eth3_on";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            ethxOn = isColPresent ? xmlParser.getColVal(i, colName) : "";
            interfaceMode.setEth3On(AhRestoreCommons
                    .convertStringToBoolean(ethxOn));
            colName = "eth4_on";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            ethxOn = isColPresent ? xmlParser.getColVal(i, colName) : "";
            interfaceMode.setEth4On(AhRestoreCommons
                    .convertStringToBoolean(ethxOn));

            /*--------------Access Mode-------------------------*/
            colName = "enabled8021Q";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabled8021Q = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
            lanProfile.setEnabled8021Q(AhRestoreCommons
                    .convertStringToBoolean(enabled8021Q));

            /*------------Access Security----------------*/
            colName = "enabled8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enabled8021X = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
            lanProfile.setEnabled8021X(AhRestoreCommons
                    .convertStringToBoolean(enabled8021X));

            colName = "cwpSelectEnabled";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String cwpSelectEnabled = isColPresent ? xmlParser.getColVal(i,
                    colName) : "f";
            lanProfile.setCwpSelectEnabled(AhRestoreCommons
                    .convertStringToBoolean(cwpSelectEnabled));

            colName = "userprofile_default_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String userprofile_default_id = isColPresent ? xmlParser.getColVal(
                    i, colName) : "";
            if (NmsUtil.isNotBlankId(userprofile_default_id)) {
                Long newUserprofileDefaultId = AhRestoreNewMapTools
                        .getMapUserProfile(Long
                                .parseLong(userprofile_default_id.trim()));
                UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(
                        UserProfile.class, newUserprofileDefaultId);
                lanProfile.setUserProfileDefault(userprofile);
            }

            colName = "userprofile_selfreg_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String userprofile_selfreg_id = isColPresent ? xmlParser.getColVal(
                    i, colName) : "";
            if (NmsUtil.isNotBlankId(userprofile_selfreg_id)) {
                Long newUserprofileSelfRegId = AhRestoreNewMapTools
                        .getMapUserProfile(Long
                                .parseLong(userprofile_selfreg_id.trim()));
                UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(
                        UserProfile.class, newUserprofileSelfRegId);
                lanProfile.setUserProfileSelfReg(userprofile);
            }

            colName = "cwp_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String cwp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(cwp_id)) {
                Long newCwpId = AhRestoreNewMapTools.getMapCapWebPortal(Long
                        .parseLong(cwp_id.trim()));
                Cwp cwp = AhRestoreNewTools.CreateBoWithId(Cwp.class, newCwpId);
                lanProfile.setCwp(cwp);
            }

            colName = "macauthenabled";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String macauthenabled = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            lanProfile.setMacAuthEnabled(AhRestoreCommons
                    .convertStringToBoolean(macauthenabled));

            colName = "authProtocol";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String authProtocol = isColPresent ? xmlParser
                    .getColVal(i, colName) : "1";
            lanProfile.setAuthProtocol(AhRestoreCommons
                    .convertInt(authProtocol));

            colName = "radius_service_assign_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String radius_service_assign_id = isColPresent ? xmlParser
                    .getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(radius_service_assign_id)) {
                Long newRadiusServiceAssignId = AhRestoreNewMapTools
                        .getMapRadiusServerAssign(Long
                                .parseLong(radius_service_assign_id.trim()));
                RadiusAssignment radiusAssignment = AhRestoreNewTools
                        .CreateBoWithId(RadiusAssignment.class,
                                newRadiusServiceAssignId);
                lanProfile.setRadiusAssignment(radiusAssignment);
            }

            /*------------DoS Prevention and Filters----------------*/
            Long newDosId;
            colName = "ip_dos_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String ip_dos_id = isColPresent ? xmlParser.getColVal(i, colName)
                    : "";
            if (NmsUtil.isNotBlankId(ip_dos_id)) {
                newDosId = AhRestoreNewMapTools.getMapDosPrevention(Long
                        .parseLong(ip_dos_id.trim()));
                if (null != newDosId) {
                    DosPrevention ipDos = AhRestoreNewTools.CreateBoWithId(
                            DosPrevention.class, newDosId);
                    lanProfile.setIpDos(ipDos);
                }
            }

            colName = "service_filter_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String service_filter_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            ServiceFilter serviceFilter = null;
            if (NmsUtil.isNotBlankId(service_filter_id)) {
                Long newServiceFilterId = AhRestoreNewMapTools
                        .getMapMgtServiceFilter(Long
                                .parseLong(service_filter_id.trim()));
                serviceFilter = AhRestoreNewTools.CreateBoWithId(
                        ServiceFilter.class, newServiceFilterId);
            }
            if (serviceFilter == null) {
                serviceFilter = QueryUtil.findBoByAttribute(
                        ServiceFilter.class, "defaultFlag", true);
            }
            lanProfile.setServiceFilter(serviceFilter);

            /*------------Advanced----------------*/
            colName = "authSequence";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String authSequence = isColPresent ? xmlParser
                    .getColVal(i, colName) : "0";
            lanProfile.setAuthSequence(AhRestoreCommons
                    .convertInt(authSequence));

            /*------------Native Network----------------*/
            colName = "native_network_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String native_network_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            if (NmsUtil.isNotBlankId(native_network_id)) {
                Long networkId = AhRestoreNewMapTools.getMapVpnNetwork(Long
                        .parseLong(native_network_id.trim()));
                if (null != networkId) {
                    VpnNetwork nativeNetwork = AhRestoreNewTools
                            .CreateBoWithId(VpnNetwork.class, networkId);
                    lanProfile.setNativeNetwork(nativeNetwork);
                }
            }

            /*------------Native VLAN----------------*/
            colName = "native_vlan_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String native_vlan_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            if (NmsUtil.isNotBlankId(native_vlan_id)) {
                Long vlanId = AhRestoreNewMapTools.getMapVlan(Long
                        .parseLong(native_vlan_id.trim()));
                if (null != vlanId) {
                    Vlan nativeVlan = AhRestoreNewTools.CreateBoWithId(
                            Vlan.class, vlanId);
                    lanProfile.setNativeVlan(nativeVlan);
                }
            }

            colName = "denyAction";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String denyAction = isColPresent ? xmlParser.getColVal(i, colName)
                    : "3";
            lanProfile.setDenyAction((short) AhRestoreCommons
                    .convertInt(denyAction));

            colName = "actionTime";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String actionTime = isColPresent ? xmlParser.getColVal(i, colName)
                    : "60";
            lanProfile.setActionTime(AhRestoreCommons.convertLong(actionTime));

            colName = "chkUserOnly";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String chkUserOnly = isColPresent ? xmlParser.getColVal(i, colName)
                    : "false";
            lanProfile.setChkUserOnly(AhRestoreCommons
                    .convertStringToBoolean(chkUserOnly));

            colName = "enableosdection";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableosdection = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            lanProfile.setEnableOsDection(AhRestoreCommons
                    .convertStringToBoolean(enableosdection));

            colName = "chkDeauthenticate";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String chkDeauthenticate = isColPresent ? xmlParser.getColVal(i,
                    colName) : "false";
            lanProfile.setChkDeauthenticate(AhRestoreCommons
                    .convertStringToBoolean(chkDeauthenticate));

            /*--RADIUS attribute mapping, assign user profile, Jianliang Chen, 2012-04-01--*/
            colName = "enableAssignUserProfile";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String enableAssignUserProfile = isColPresent ? xmlParser
                    .getColVal(i, colName) : "false";
            lanProfile.setEnableAssignUserProfile(AhRestoreCommons
                    .convertStringToBoolean(enableAssignUserProfile));

            colName = "assignUserProfileAttributeId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String assignUserProfileAttributeId = isColPresent ? xmlParser
                    .getColVal(i, colName) : "0";
            lanProfile.setAssignUserProfileAttributeId(AhRestoreCommons
                    .convertInt(assignUserProfileAttributeId));

            colName = "assignUserProfileVenderId";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            String assignUserProfileVenderId = isColPresent ? xmlParser
                    .getColVal(i, colName) : "0";
            lanProfile.setAssignUserProfileVenderId(AhRestoreCommons
                    .convertInt(assignUserProfileVenderId));

            lanProfiles.add(lanProfile);
        }

        return lanProfiles.isEmpty() ? null : lanProfiles;
    }

    private static Map<String, Set<VpnNetwork>> getAllVPNNetworks(String tableName, String... columnsName) throws AhRestoreException,
            AhRestoreColNotExistException {

        AhRestoreDBTools.logRestoreMsg("getAllVPNNetworks: for " + tableName);

        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        /**
         * Check validation of $tableName.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        Map<String, Set<VpnNetwork>> networksInfo = new HashMap<String, Set<VpnNetwork>>();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            /**
             * default set lan_profile_id
             */
            colName = (ArrayUtils.isEmpty(columnsName) ? "lan_profile_id"
                    : columnsName[0]);
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                /**
                 * The column must be exist in the table of $tableName
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(profileId)) {
                continue;
            }

            /**
             * default set networks_id
             */
            colName = (ArrayUtils.isEmpty(columnsName) ? "networks_id"
                    : columnsName[1]);
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                /**
                 * The column must be exist in the table of $tableName
                 */
                continue;
            }

            String networksId = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(networksId)) {
                continue;
            }

            Long newNetworksId = AhRestoreNewMapTools.getMapVpnNetwork(Long
                    .parseLong(networksId.trim()));
            VpnNetwork networks = AhRestoreNewTools.CreateBoWithId(
                    VpnNetwork.class, newNetworksId);

            if (networks != null) {
                if (networksInfo.get(profileId) == null) {
                    Set<VpnNetwork> networksSet = new HashSet<VpnNetwork>();
                    networksSet.add(networks);
                    networksInfo.put(profileId, networksSet);
                } else {
                    networksInfo.get(profileId).add(networks);
                }
            }
        }

        return networksInfo;
    }

    private static Map<String, Set<Vlan>> getAllVlans(String tableName, String... columnsName) throws AhRestoreException,
            AhRestoreColNotExistException {

        AhRestoreDBTools.logRestoreMsg("getAllVlans: for " + tableName);

        AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        /**
         * Check validation of $tableName.xml
         */
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        Map<String, Set<Vlan>> vlanMap = new HashMap<String, Set<Vlan>>();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            /**
             * default set lan_profile_id
             */
            colName = (ArrayUtils.isEmpty(columnsName) ? "lan_profile_id"
                    : columnsName[0]);
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                /**
                 * The column must be exist in the table of $tableName
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(profileId)) {
                continue;
            }

            /**
             * default set vlan_id
             */
            colName = (ArrayUtils.isEmpty(columnsName) ? "vlan_id"
                    : columnsName[1]);
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                /**
                 * The column must be exist in the table of $tableName
                 */
                continue;
            }

            String vlanId = xmlParser.getColVal(i, colName);
            if (StringUtils.isBlank(vlanId)) {
                continue;
            }

            Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long
                    .parseLong(vlanId.trim()));
            Vlan vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,
                    newVlanId);

            if (vlanObj != null) {
                if (vlanMap.get(profileId) == null) {
                    Set<Vlan> networksSet = new HashSet<Vlan>();
                    networksSet.add(vlanObj);
                    vlanMap.put(profileId, networksSet);
                } else {
                    vlanMap.get(profileId).add(vlanObj);
                }
            }
        }

        return vlanMap;
    }
    
    public static Map<String, Set<PortGroupProfile>> convertLANs2PortTemplate(List<ConfigTemplate> configTemplates) {
        try {
            Map<String, Set<LanProfile>> mapOfLANOnNetworkPolicy = getAllConfigTemplateLan();
            if(null != mapOfLANOnNetworkPolicy) {
                Map<String, Set<PortGroupProfile>> mapOfPortOnNetworkPolicy= new HashMap<>();
                for (String policyId : mapOfLANOnNetworkPolicy.keySet()) {
                    
                    Set<LanProfile> lanProfiles = mapOfLANOnNetworkPolicy.get(policyId);
                    if(!(null == lanProfiles || lanProfiles.isEmpty())) {
                        
                        ConfigTemplate networkPolicy = getNetworkPolicy(configTemplates, policyId);
                        if(null == networkPolicy) {
                            // skip, because unable to find the Network Policy by Id in mapping
                            continue;
                        }
                        
                        final boolean wirelessOnly = networkPolicy.getConfigType().isWirelessOnly();
                        // multiple LANs --> one or two PortTemplate
                        Set<PortGroupProfile> tempSet = new HashSet<>();
                        
                        // create for BR series as Router or BR100 as AP
                        PortGroupProfile portProfile = new PortGroupProfile();
                        Long eth1LANId = convertBRSeriesPortTemplate(portProfile, lanProfiles, wirelessOnly);
                        
                        if(StringUtils.isBlank(portProfile.getName())
                                || StringUtils.isBlank(portProfile.getDeviceModels())) {
                            // skip the NULL object for vHM restore, avoid empty name and device models
                            continue;
                        }
                        tempSet.add(portProfile);
                        
                        // wireless + router mode
                        if(!wirelessOnly) {
                            if(null != eth1LANId) {
                                // create for AP series if ETH1 is configured
                                PortGroupProfile apPortProfile = createPortTemplate4APSeries(portProfile, eth1LANId);
                                if(null != apPortProfile) {
                                    tempSet.add(apPortProfile);
                                }
                            }
                            // create for BR100 as AP
                            PortGroupProfile br100APProfile = new PortGroupProfile();
                            convertBRSeriesPortTemplate(br100APProfile, lanProfiles, true);
                            tempSet.add(br100APProfile);
                        }
                        
                        // avoid duplicate data (when one profile is using by different network policy)
                        Set<PortGroupProfile> createSet = new HashSet<>();
                        Set<PortGroupProfile> cacheSet = new HashSet<>();
                        for (PortGroupProfile profile : tempSet) {
                            List<PortGroupProfile> tempProfiles = findMatchPortTemplate(profile);
                            if(null == tempProfiles || tempProfiles.isEmpty()) {
                                createSet.add(profile);
                                cacheSet.add(profile);
                            } else {
                                final List<PortBasicProfile> basicProfiles = tempProfiles.get(0).getBasicProfiles();
                                if(basicProfiles.isEmpty()) {
                                    // not the same LANs for the network policy, need to create
                                    createSet.add(profile);
                                    cacheSet.add(profile);
                                } else {
                                    if(basicProfiles.size() == profile.getBasicProfiles().size()) {
                                        // handle the mutil-referent LANs by network policy, 
                                        // avoid same name device template (but contains different LANs) is overlapped.
                                        boolean isSame = true;
                                        for (PortBasicProfile basic : basicProfiles) {
                                            boolean flag = false;
                                            Long accessId = basic.getAccessProfile().getId();
                                            for (PortBasicProfile tmpBasic : profile.getBasicProfiles()) {
                                                if(accessId.compareTo(tmpBasic.getAccessProfile().getId()) == 0) {
                                                    flag = true;
                                                    break;
                                                }
                                            }
                                            if(!flag) {
                                                isSame = false;
                                                break;
                                            }
                                        }
                                        if(isSame) {
                                            cacheSet.add(tempProfiles.get(0));
                                        } else {
                                            // not the same LANs for the network policy, need to create
                                            createSet.add(profile);
                                            cacheSet.add(profile);
                                        }
                                    } else {
                                        // not the same LANs for the network policy, need to create
                                        createSet.add(profile);
                                        cacheSet.add(profile);
                                    }
                                }
                            }
                        }
                        QueryUtil.restoreBulkCreateBos(createSet);
                        
                        mapOfPortOnNetworkPolicy.put(policyId, cacheSet);
                    }
                }
                return mapOfPortOnNetworkPolicy;
            }
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Error when convert the LANProfile to PortTemplates", e);
        }
        return null;
    }

    private static List<PortGroupProfile> findMatchPortTemplate(
            PortGroupProfile profile) {
        FilterParams filterParams = new FilterParams(
                "name = :s1 AND deviceModels = :s2 AND deviceType= :s3 AND portNum = :s4",
                new Object[] { profile.getName(), profile.getDeviceModels(),
                        profile.getDeviceType(), profile.getPortNum() });
        List<PortGroupProfile> tempProfiles = QueryUtil.executeQuery(
                PortGroupProfile.class, null, filterParams, profile.getOwner().getId(), new QueryBo() {
                    @Override
                    public Collection<HmBo> load(HmBo bo) {
                        if (bo instanceof PortGroupProfile) {
                            PortGroupProfile profile = (PortGroupProfile) bo;
                            if (null != profile.getBasicProfiles()) {
                                profile.getBasicProfiles().size();
                                for (PortBasicProfile basic : profile.getBasicProfiles()) {
                                    if (null != basic.getAccessProfile()) {
                                        basic.getAccessProfile().getId();
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
        return tempProfiles;
    }

    private static ConfigTemplate getNetworkPolicy(List<ConfigTemplate> configTemplates,
            String policyId) {
        ConfigTemplate networkPolicy = null;
        for (ConfigTemplate configTemplate : configTemplates) {
            if(null != configTemplate && null != configTemplate.getId() 
                    && configTemplate.getId().toString().equals(policyId)) {
                networkPolicy = configTemplate;
                break;
            }
        }
        return networkPolicy;
    }

    private static PortGroupProfile createPortTemplate4APSeries(
            PortGroupProfile routerProfile, Long eth1lanId) {
        
        LanProfile realLAN = AhRestoreNewMapTools.getMapLanProfileObj(eth1lanId);
        if(null != realLAN && null != routerProfile.getOwner() 
                && StringUtils.isNotBlank(routerProfile.getName())) {
            // avoid exception
            PortGroupProfile apProfile = new PortGroupProfile();
            apProfile.setOwner(realLAN.getOwner());
            apProfile.setName(realLAN.getName()+"-AP");
            apProfile.setDeviceModels(HiveAp.HIVEAP_MODEL_330
                            + PortBasicProfile.PORTS_SPERATOR
                            + HiveAp.HIVEAP_MODEL_350);
            apProfile.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
            apProfile.setPortNum((short) 2);
            apProfile.setDescription("Upgrade from LAN: "+realLAN.getName());
            
            Long accessId = AhRestoreNewMapTools.getMapLanProfile(eth1lanId);
            if(null != accessId) {
                PortAccessProfile accessProfile = AhRestoreNewTools.CreateBoWithId(PortAccessProfile.class, accessId);
                if(null != accessProfile) {
                    PortBasicProfile basicProfile = new PortBasicProfile();
                    LanInterfacesMode infMode = realLAN.getLanInterfacesMode();

                    if (infMode.isEth1On()) {
                        basicProfile.setEthPorts("1");
                    } else {
                        // error!! the ETH1 is not enabled yet
                        return null;
                    }
                    
                    basicProfile.setAccessProfile(accessProfile);
                    
                    apProfile.getBasicProfiles().add(basicProfile);
                }
            }
            
            //FIXME now support to configure USB port now (auto bind with WAN port type)
            //autoBindWANPort(apProfile, realLAN.getOwner());
            
            return apProfile;
        }
        return null;
    }

    private static Long convertBRSeriesPortTemplate(PortGroupProfile portProfile,
            Set<LanProfile> lanProfiles, boolean br100AsAP) {
        Long lanProfileId = null;
        for (LanProfile tempLAN : lanProfiles) {
            LanProfile realLAN = AhRestoreNewMapTools.getMapLanProfileObj(tempLAN.getId());
            if(null == realLAN) {
                // skip the NULL object for vHM restore
                continue;
            }
            if(null == portProfile.getOwner()) {
                // initialize
                portProfile.setOwner(realLAN.getOwner());
                portProfile.setDescription("Upgrade from multi-LANs:");
                portProfile.setPortNum((short) 5);
                if(br100AsAP) {
                    portProfile.setName(realLAN.getName()+"-BR100");
                    portProfile.setDeviceModels(""+HiveAp.HIVEAP_MODEL_BR100);
                    portProfile.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
                } else {
                    portProfile.setName(realLAN.getName()+"-BR");
                    portProfile.setDeviceModels(HiveAp.HIVEAP_MODEL_BR100
                            + PortBasicProfile.PORTS_SPERATOR
                            + HiveAp.HIVEAP_MODEL_BR200
                            + PortBasicProfile.PORTS_SPERATOR
                            + HiveAp.HIVEAP_MODEL_BR200_WP
                            + PortBasicProfile.PORTS_SPERATOR
                            + HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
                    portProfile.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
                    
                    //FIXME now support to configure USB port now (auto bind with WAN port type)
                    //autoBindWANPort(portProfile, realLAN.getOwner());
                }
            }
            portProfile.setDescription(portProfile.getDescription() + " " + realLAN.getName());
            Long accessId = AhRestoreNewMapTools.getMapLanProfile(tempLAN.getId());
            if(null != accessId) {
                PortAccessProfile accessProfile = AhRestoreNewTools.CreateBoWithId(PortAccessProfile.class, accessId);
                if(null != accessProfile) {
                    PortBasicProfile basicProfile = new PortBasicProfile();
                    LanInterfacesMode infMode = realLAN.getLanInterfacesMode();

                    if (infMode.isEth1On() && !br100AsAP) {
                        // ETH1 enabled, need to create the device template for AP series
                        lanProfileId = tempLAN.getId();
                    }
                    
                    basicProfile.setEthPorts(convert2InfStr(infMode));
                    basicProfile.setAccessProfile(accessProfile);
                    
                    portProfile.getBasicProfiles().add(basicProfile);
                }
            }
        }
        return lanProfileId;
    }

    @Deprecated
    private static void autoBindWANPort(PortGroupProfile portProfile,
            HmDomain domain) {
        PortAccessProfile wanPortType = createPortTypeWan4BR(domain);
        if(null != wanPortType) {
            PortBasicProfile basicProfile = new PortBasicProfile();
            basicProfile.setUsbPorts(PortBasicProfile.DEFAULT_USB_PORTS);
            basicProfile.setAccessProfile(wanPortType);
            
            portProfile.getBasicProfiles().add(basicProfile);
        }
    }
    
    private static PortAccessProfile createPortTypeWan4BR(HmDomain domain){
    	// remove BeParaModule.PRE_DEFINED_PORTTYPE_WAN_ROUTER quick start policy, not support it now
    	return null;
    }

    private static String convert2InfStr(LanInterfacesMode infMode) {
        StringBuilder builder = new StringBuilder();
        if(infMode.isEth1On()) {
            builder.append("1");
            builder.append(PortBasicProfile.PORTS_SPERATOR);
        }
        if(infMode.isEth2On()) {
            builder.append("2");
            builder.append(PortBasicProfile.PORTS_SPERATOR);
        }
        if(infMode.isEth3On()) {
            builder.append("3");
            builder.append(PortBasicProfile.PORTS_SPERATOR);
        }
        if(infMode.isEth4On()) {
            builder.append("4");
            builder.append(PortBasicProfile.PORTS_SPERATOR);
        }
        if(builder.length() > 1) {
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private static Map<String, Set<LanProfile>> getAllConfigTemplateLan()
            throws AhRestoreColNotExistException, AhRestoreException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of config_template_lan.xml
         */
        boolean restoreRet = xmlParser.readXMLFile("config_template_lan");
        if (!restoreRet) {
            return null;
        }

        int rowCount = xmlParser.getRowCount();
        Map<String, Set<LanProfile>> lanInfo = new HashMap<String, Set<LanProfile>>();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            /**
             * Set config_template_id
             */
            colName = "config_template_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    "config_template_lan", colName);
            if (!isColPresent) {
                /**
                 * The config_template_id column must be exist in the table of
                 * config_template_lan
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (isIllegalString(profileId)) {
                continue;
            }

            /**
             * Set lanprofiles_id
             */
            colName = "lanprofiles_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    "config_template_lan", colName);
            if (!isColPresent) {
                /**
                 * The lanprofiles_id column must be exist in the table of
                 * config_template_lan
                 */
                continue;
            }

            String lanId = xmlParser.getColVal(i, colName);
            if (isIllegalString(lanId)) {
                continue;
            }

            LanProfile lanProfile = AhRestoreNewTools.CreateBoWithId(
                    LanProfile.class, Long.parseLong(lanId.trim()));

            if (lanProfile != null) {
                if (lanInfo.get(profileId) == null) {
                    Set<LanProfile> lanSet = new HashSet<LanProfile>();
                    lanSet.add(lanProfile);
                    lanInfo.put(profileId, lanSet);
                } else {
                    lanInfo.get(profileId).add(lanProfile);
                }
            }
        }

        return lanInfo;
    }
    
    public static String replaceSFPPort(String portStr){
    	if(portStr == null){
    		return portStr;
    	}
    	return portStr.replace("1001", String.valueOf(AhInterface.DEVICE_IF_TYPE_ETH25))
    				.replace("1002", String.valueOf(AhInterface.DEVICE_IF_TYPE_ETH26))
    				.replace("1003", String.valueOf(AhInterface.DEVICE_IF_TYPE_ETH27))
    				.replace("1004", String.valueOf(AhInterface.DEVICE_IF_TYPE_ETH28));
    }
}
