package com.ah.ui.actions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.AhDirTools;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.IpNatMap;
import com.ah.bo.network.PortForwarding;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.SubnetworkDHCPCustom;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class VpnNetworksAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(VpnNetworksAction.class
			.getSimpleName());

	public static final int COLUMN_NAME = 1;
//	public static final int COLUMN_VLAN = 2;
	public static final int COLUMN_WEBSEC = 3;
	public static final int COLUMN_DNS = 4;
	public static final int COLUMN_SUBNET = 5;
	public static final int COLUMN_DHCP = 6;
	public static final int COLUMN_NTPSERVER = 7;
	public static final int COLUMN_LEASETIME = 8;
	public static final int COLUMN_DOMAINNAME = 9;
	public static final int COLUMN_DESCRIPTION = 10;
	public static final int COLUMN_NETWORK_TYPE = 11;
	public static final int COLUMN_LOCALSUBNET = 12;
	private static final String destinationPortNumberNotSameError="destinationPortNumberNotSameError";
	private static final String addDestinationPortNumberError="addDestinationPortNumberError";
	private static final String addIpAddressOurRangeError="addIpAddressOurRangeError";
	private static final String addPostionError="addPostionError";
	private static final String addProtocolError="addProtocolError";
	private static final String mailFileName = "ipnatmap.csv";
	private static final String udpFileName = "systemUDPPort.txt";
	private static final String tcpFileName= "systemTCPPort.txt";
	private static List<String> udpPortList=null;
	private static List<String> tcpPortList=null;
	private boolean overrideDNSService; 
	private Long overrideDNSServiceId;
	private boolean overrideDNS;
	
	//fix bug 26018
	 public static final String MARK_FOR_CANCELLING = "cancel";


	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.vpn.network.name";
			break;
//		case COLUMN_VLAN:
//			code = "config.vpn.network.vlan";
//			break;
		case COLUMN_WEBSEC:
			code = "config.vpn.network.webSecurity";
			break;
		case COLUMN_DNS:
			code = "config.vpn.network.dnsService";
			break;
		case COLUMN_SUBNET:
			code = "config.vpn.network.subnetwork";
			break;
		case COLUMN_DHCP:
			code = "config.vpn.network.enableDhcpTitle";
			break;
		case COLUMN_NTPSERVER:
			code = "config.vpn.network.ntpServerIp";
			break;
		case COLUMN_LEASETIME:
			code = "config.vpn.network.leaseTime";
			break;
		case COLUMN_DOMAINNAME:
			code = "config.vpn.network.domainName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.vpn.network.description";
			break;
		case COLUMN_NETWORK_TYPE:
		    code = "config.vpn.network.type";
		    break;
		case COLUMN_LOCALSUBNET:
		    code = "config.vpn.network.localSubnetwork";
		    break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(10);
		columns.add(new HmTableColumn(COLUMN_NAME));
//		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_WEBSEC));
		columns.add(new HmTableColumn(COLUMN_DNS));
		columns.add(new HmTableColumn(COLUMN_LOCALSUBNET));
		columns.add(new HmTableColumn(COLUMN_SUBNET));
		columns.add(new HmTableColumn(COLUMN_DHCP));
		columns.add(new HmTableColumn(COLUMN_NTPSERVER));
		columns.add(new HmTableColumn(COLUMN_LEASETIME));
		columns.add(new HmTableColumn(COLUMN_DOMAINNAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_NETWORK_TYPE));
		return columns;
	}

	@Override
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(9);
		columns.add(new HmTableColumn(COLUMN_NAME));
//		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_WEBSEC));
		columns.add(new HmTableColumn(COLUMN_DNS));
		columns.add(new HmTableColumn(COLUMN_LOCALSUBNET));
		columns.add(new HmTableColumn(COLUMN_SUBNET));
		columns.add(new HmTableColumn(COLUMN_DHCP));
		columns.add(new HmTableColumn(COLUMN_NTPSERVER));
		columns.add(new HmTableColumn(COLUMN_LEASETIME));
		columns.add(new HmTableColumn(COLUMN_DOMAINNAME));
		return columns;
	}
	
	@Override
	public String execute() throws Exception {
		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && "continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.vpn.network"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				VpnNetwork networkObj = new VpnNetwork();
				if(isBlnMgtNetwork()) {
				    networkObj.setNetworkType(VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT);
				}
                setSessionDataSource(networkObj);
				prepareDependentObjects();
				storeJsonContext();
				return getCertainReturnPath(INPUT, "networkOnly", "networkJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				prepareSaveObjects();
				setCustomNewOrApplyButton();
				if (checkNameExists("networkName", getDataSource()
						.getNetworkName())) {
					prepareDependentObjects();
					if(isJsonMode() && isContentShownInSubDrawer()) {
						return "networkOnly";
					} else if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.objectExists",
								getDataSource().getNetworkName()));
						return "json";
					} else {
						return returnResultKeyWord(INPUT,"networkJson");
					}
				}
				
				if (getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
					getDataSource().getSubItems().clear();
					getDataSource().getSubNetwokClass().clear();
					getDataSource().getReserveClass().clear();
				} else {
					getDataSource().setIpAddressSpace("");
					getDataSource().setGuestLeftReserved(0);
					getDataSource().setGuestRightReserved(0);
				}
				
				if(saveOrCancelSubnet.equals(MARK_FOR_CANCELLING)){
					removeSingleTableItemInCache("");
				}
				
				if(getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
				    getDataSource().setWebSecurity(VpnNetwork.VPN_NETWORK_WEBSECURITY_NONE);
				}
				
				if (!checkWebSecurity()){
					return getCertainReturnPath(INPUT, "networkOnly", "json");
				}
				
				if(!checkMgmtDHCPOption()) {
				    return getCertainReturnPath(INPUT, "networkOnly", "json");
				}
				
				if(isJsonMode() && isContentShownInSubDrawer()) {
					if ("create".equals(operation)) {
						try {
							Long newId = createBo(dataSource);
							prepareSubNetworkRes(newId, null);
							jsonObject = new JSONObject(); 
							jsonObject.put("t", true);
							jsonObject.put("newState", true);
							jsonObject.put("lanId", selectedLANId);
							jsonObject.put("newId", newId);
							return "json";
						} catch (Exception e) {
							addActionError(e.getMessage());
							return "networkOnly";
						}
					}
					return "json";
				} else if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getNetworkName());
					try {
						id = createBo(dataSource);
						prepareSubNetworkRes(id, null);
						if (isBlnMgtNetwork() && !isAMgtNetworkProperItem(id)) {
							String tmpStr[] = {getDataSource().getNetworkName(), "created", "Management Network"};
							jsonObject.put("warnMsg", MgrUtil.getUserMessage("warn.config.vpnnetwork.not.properFor", tmpStr));
						} 
						jsonObject.put("addedId", id);
						
					} catch (Exception e) {
						log.error("Error when create Network", e);
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					String result;
					if ("create".equals(operation)) {
						Long newId = createBo(dataSource);
						prepareSubNetworkRes(newId, null);
						result = prepareBoList();
						loadPageLazyData();
					} else {
						Long newId = createBo(dataSource);
						id = newId;
						prepareSubNetworkRes(newId, null);
						setUpdateContext(true);
						result = getLstForward();
					}
					return result;
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource==null) {
					loadPageLazyData();
					String rtnStr = prepareBoList();
					return getCertainReturnPath(rtnStr, "networkOnly", "networkJson");
				}
				//FIXME improve the performance
				boolean referenced = isUsedByReferencedTables(id, getDataSource().getOwner().getId());
                getDataSource().setReferenced(referenced);
				if(referenced) {
				    log.warn("The Network is used by other tables. [name:"
				            + getDataSource().getNetworkName() + ", type:"
				            + getDataSource().getNetworkType() + "]");
				}
				
				prepareDependentObjects();
				setCustomNewOrApplyButton();
				storeJsonContext();
				addLstTitle(getText("config.title.vpn.network.edit") + " '"
						+ getChangedName() + "'");
				return getCertainReturnPath(returnWord, "networkOnly", "networkJson");
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				if (dataSource == null) {
					prepareDependentObjects();
					return getCertainReturnPath(INPUT, "networkOnly", "json");
				}
				
				prepareSaveObjects();
				setCustomNewOrApplyButton();
				if (getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
					getDataSource().getSubItems().clear();
					getDataSource().getSubNetwokClass().clear();
					getDataSource().getReserveClass().clear();
				} else {
					getDataSource().setIpAddressSpace("");
					getDataSource().setGuestLeftReserved(0);
					getDataSource().setGuestRightReserved(0);
				}
                if (getDataSource().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
                    getDataSource().setWebSecurity(VpnNetwork.VPN_NETWORK_WEBSECURITY_NONE);
                }
                if(saveOrCancelSubnet.equals(MARK_FOR_CANCELLING)){
					removeSingleTableItemInCache("update");
				}
				if (!checkWebSecurity()){
					return getCertainReturnPath(INPUT, "networkOnly", "json");
				}
                if(!checkMgmtDHCPOption()) {
                    return getCertainReturnPath(INPUT, "networkOnly", "json");
                }
				
				VpnNetwork oldVpnNetwork = QueryUtil.findBoById(VpnNetwork.class, this.getDataSource().getId(), this);
				if ("update".equals(operation)) {
					String ret= updateBo();
					prepareSubNetworkRes(this.getDataSource().getId(), oldVpnNetwork);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("resultStatus", true);
						if (isBlnMgtNetwork() && !isAMgtNetworkProperItem(getDataSource().getId())) {
							String tmpStr[] = {getDataSource().getNetworkName(), "updated", "Management Network"};
							jsonObject.put("warnMsg", MgrUtil.getUserMessage("warn.config.vpnnetwork.not.properFor", tmpStr));
							// dont remove the not filter value
							//jsonObject.put("removeId", getDataSource().getId());
							jsonObject.put("parentDomID", getParentDomID());
						}
						return "json";
					} else {
						loadPageLazyData();
						return ret;
					}
				} else {
					updateBo(dataSource);
					prepareSubNetworkRes(this.getDataSource().getId(), oldVpnNetwork);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					String ret= prepareBoList();
					loadPageLazyData();
					return ret;
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				VpnNetwork profile = (VpnNetwork) findBoById(boClass, cloneId, this);
				if (null != profile) {
					profile.setOwner(null);
					profile.setId(null);
					profile.setVersion(null);
					profile.setDefaultFlag(false);
					profile.setNetworkName("");
					setCloneFields(profile, profile);
					setSessionDataSource(profile);
					prepareDependentObjects();
					addLstTitle(getText("config.title.vpn.network"));
					return getCertainReturnPath(INPUT, "networkOnly", "networkJson");
				} else {
					baseOperation();
					String ret= prepareBoList();
					loadPageLazyData();
					return getCertainReturnPath(ret, "networkOnly", "json");
				}
			} else if ("newDnsService".equals(operation)
					|| "editDnsService".equals(operation)) {
					//|| "newVlan".equals(operation) 
					//|| "editVlan".equals(operation)
				prepareSaveObjects();
				clearErrorsAndMessages();
				addLstForward("vpnNetworks");
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					String ret= prepareBoList();
					loadPageLazyData();
					return ret;
				} else {
					prepareSaveObjects();
					prepareDependentObjects();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return returnResultKeyWord(INPUT,"networkJson");
				}
				
			}  else if ("overrideDNS".equals(operation)) {
				if (dataSource == null) {
					String ret= prepareBoList();
					loadPageLazyData();
					return ret;
				} else {
					prepareSaveObjects();
					prepareDependentObjects();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					return returnResultKeyWord(INPUT,"networkJson");
				}
			} else if ("addCustom".equals(operation)) {
				if (dataSource == null) {
					String ret= prepareBoList();
					loadPageLazyData();
					return getCertainReturnPath(ret, "networkOnly", "networkJson");
				} else {
					if (!addSingleCustom()) {
						hideCustomCreateItem = "";
						hideCustomNewButton = "none";
					}
					prepareSaveObjects();
					removeSelectedCustom();
					prepareDependentObjects();
					return getCertainReturnPath(INPUT, "networkOnly", "networkJson");
				}
			} else if ("removeCustom".equals(operation)
					|| "removeCustomNone".equals(operation)) {
				hideCustomCreateItem = "removeCustomNone".equals(operation) ? ""
						: "none";
				hideCustomNewButton = "removeCustomNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					String ret= prepareBoList();
					loadPageLazyData();
					return getCertainReturnPath(ret, "networkOnly", "networkJson");
				} else {
					prepareSaveObjects();
					removeSelectedCustom();
					prepareDependentObjects();
					return getCertainReturnPath(INPUT, "networkOnly", "networkJson");
				}
			} else if ("removeSubnetwork".equals(operation)) {
				if (dataSource == null) {
					String ret= prepareBoList();
					loadPageLazyData();
					return getCertainReturnPath(ret, "networkOnly", "networkJson");
				} else {
					removeSelectedSubnetwork();
					jsonObject = new JSONObject();
					jsonObject.put("succ", true);
					jsonObject.put("table", fetchMainSubnetTable());
					return "json";
				}
			} else if ("newSubnet".equals(operation)){
				jsonObject=new JSONObject();
				getDataSource().getTmpPortForwardings().clear();
				// v: subkey
				jsonObject.put("v", generateSubnetKey());
				jsonObject.put("t1", "");
				jsonObject.put("t2", "");
				jsonObject.put("t3", fetchPortForwardingTable());
				return "json";
			} else if ("modifySubnetwork".equals(operation)){
				return setSubnetworkValues(true);
            } else if ("cloneSubnetwork".equals(operation)) {
                return setSubnetworkValues(false);
            } else if ("addSubnetClass".equals(operation)){
				jsonObject=new JSONObject();
				jsonObject.put("r", true);
				jsonObject.put("v", true);
				jsonObject.put("t", fetchSubnetClassTable());
				return "json";
			} else if ("addReserveIpClass".equals(operation)){
				jsonObject=new JSONObject();
				jsonObject.put("r", true);
				jsonObject.put("v", true);
				jsonObject.put("t", fetchIpReserveClassTable());
				return "json";
			}  else if ("addSubCustom".equals(operation)) {
				jsonObject=new JSONObject();
				
				if(!validateSubCustom()) {
					return "json";
				}
				
				SubnetworkDHCPCustom vc = new SubnetworkDHCPCustom();
				vc.setKey(getSubKey());
				vc.setNumber(subCustomNumber);
				vc.setType(subCustomType);
				vc.setValue(subCustomValue);
				getDataSource().getTmpSubNetworkDHCPCustoms().add(vc);
				jsonObject.put("succ", true);
				jsonObject.put("tableHTML", fetchSubnetCustomTable());
				
				return "json";
			} else if ("removeSubCustom".equals(operation)) {
				jsonObject=new JSONObject();
				removeSelectedSubCustoms();
				if (!getDataSource().getTmpSubNetworkDHCPCustoms().isEmpty()) {
					jsonObject.put("showTable", true);
					jsonObject.put("tableHTML", fetchSubnetCustomTable());
				}
				return "json";
			} else if ("addportForwarding".equals(operation)) {
				jsonObject = new JSONObject();
				boolean success=this.validatePort(jsonObject);
				if (success == false) {
					jsonObject.put("succ", false);
					return "json";
				}
				PortForwarding portForwarding = new PortForwarding();
				portForwarding.setKey(getSubKey());
				portForwarding
						.setDestinationPortNumber(subDestinationPortNumber);
				portForwarding.setPositionId(positionId);
				portForwarding
						.setInternalHostPortNumber(subInternalHostPortNumber);
				portForwarding.setProtocol(protocol);
				getDataSource().getTmpPortForwardings().add(portForwarding);
				jsonObject.put("succ", true);
				jsonObject.put("tableHTML", fetchPortForwardingTable());
				return "json";
			} else if ("removePortForwarding".equals(operation)) {
				jsonObject = new JSONObject();
				removeSelectedPortForwarding();
				if (getDataSource().getTmpPortForwardings().size() > 0) {
					jsonObject.put("showTable", true);
					jsonObject.put("tableHTML", fetchPortForwardingTable());
				}
				return "json";
			} else if("viewPortList".equals(operation)){
				jsonObject = new JSONObject();
				String portListStr=getPortListStr();
				jsonObject.put("portListStr", portListStr);
				return "json";
			} else if ("saveSubnet".equals(operation) || "newOverrideDNS".equals(operation) 
					|| "editOverrideDNS".equals(operation)){
				return saveSubNetFunction();
			} else if("getSubNetworkItem".equals(operation)) {
				return getSelectedSubItemValues();
			} else if("exportPortForwarding".equals(operation)){
				try{
					this.exportForwardingData(localNetWork, natNetWork, uniqueSubnetwork);
				}catch(Exception e){
					return "";
				}
				getInputPath();
				return "download";
			}else if("ipMappingIframeDiag".equals(operation)){
				return "ipMappingIframe";
			}else if("ipMappingResultDiag".equals(operation)){
				if(0==showType){
					showResultByPositionId();
				}else{
					showResultByBranchId();
				}
				return "ipMappingResult";
			} else if("newOverrideDNSService".equals(operation) 
					|| "editOverrideDNSService".equals(operation)){
				VpnNetworkSub networkSub = null;
				if ("newOverrideDNSService".equals(operation)) {
					for (VpnNetworkSub vs : getDataSource().getSubItems()) {
						if (vs.getKey() == getDataSource().getSubItems().size()) {
							networkSub = vs;
						}
					}
				} else {
					networkSub = getDataSource().getSubItems().get(
							getRuleOneIndex());
				}

				if (networkSub != null && networkSub.isOverrideDNSService()) {
					overrideDNS = networkSub.isOverrideDNSService();
					if (networkSub.getDnsService() != null) {
						overrideDNSServiceId = networkSub.getDnsService()
								.getId();
					}
				}
				prepareSaveObjects();
				clearErrorsAndMessages();
				addLstForward("vpnNetworks");
				return operation;
			}else {
				if("remove".equals(operation)){
					deleteSubNetworkResource(this.getAllSelectedIds());
				}
				baseOperation();
				String ret= prepareBoList();
				loadPageLazyData();
				return getCertainReturnPath(ret, "networkOnly", "networkJson");
			}
		} catch (Exception e) {
			String ret = prepareActionError(e);
			loadPageLazyData();
			return getCertainReturnPath(ret, "networkOnly", "networkJson");
		}
	}

    /**
     * Set the subnetwork values
     * 
     * @param modifyFlag - true meant it is modifying the subnetwork; else it is cloning the subnetwork. 
     * @return String of the {@link JSONObject}
     * @throws JSONException
     */
    private String setSubnetworkValues(boolean modifyFlag) throws JSONException {
        jsonObject=new JSONObject();
        VpnNetworkSub networkSub = null;
        if("newOverrideDNS".equals(operation)){
        	for(VpnNetworkSub vs : getDataSource().getSubItems()){
        		if(vs.getKey() == getSubKey()){
        			networkSub = vs;
        		}
        	}
        }else{
        	networkSub = getDataSource().getSubItems().get(getRuleOneIndex());
        }
        
        int subnetworkKey = modifyFlag ? networkSub.getKey() : generateSubnetKey();        
        List<SingleTableItem> addList = new ArrayList<SingleTableItem>();
        for(SingleTableItem item: getDataSource().getSubNetwokClass()){
        	if (item.getKey()==networkSub.getKey()) {
        		 if(!modifyFlag) {
        	        SingleTableItem cloneItem = item.clone();
                    cloneItem.setKey(subnetworkKey);
                    addList.add(cloneItem);
        	        jsonObject.put("cloneSubNetwokClass", item.getKey());
        	      }
        	}
        }
        getDataSource().getSubNetwokClass().addAll(addList);
        
        addList.clear();        
        for(SingleTableItem item: getDataSource().getReserveClass()){
        	if (item.getKey()==networkSub.getKey()) {
                if(!modifyFlag) {
                    SingleTableItem cloneItem = item.clone();
                    cloneItem.setKey(subnetworkKey);
                    addList.add(cloneItem);
                    jsonObject.put("cloneReserveClass", item.getKey());
                }
        	}
        }
        getDataSource().getReserveClass().addAll(addList);        
        getDataSource().getTmpSubNetworkDHCPCustoms().clear();
        for(SubnetworkDHCPCustom item: getDataSource().getSubnetworkDHCPCustoms()){
        	if (item.getKey()==networkSub.getKey()) {
                if(modifyFlag) {
                    getDataSource().getTmpSubNetworkDHCPCustoms().add(item);
                } else {
                    SubnetworkDHCPCustom cloneItem = item.clone();
                    cloneItem.setKey(subnetworkKey);
                    getDataSource().getTmpSubNetworkDHCPCustoms().add(cloneItem);
                }
        	}
        }
		getDataSource().getTmpPortForwardings().clear();
		for (PortForwarding item : getDataSource().getPortForwardings()) {
			if (item.getKey() == networkSub.getKey()) {
				if (modifyFlag) {
					getDataSource().getTmpPortForwardings().add(item);
				} else {
					PortForwarding cloneItem = item.clone();
					cloneItem.setKey(subnetworkKey);
					getDataSource().getTmpPortForwardings().add(cloneItem);
				}
			}
		}
        jsonObject.put("v", subnetworkKey);
        jsonObject.put("e1", networkSub.isSubnetClassification());
        jsonObject.put("e2", networkSub.isReserveClassification());
        jsonObject.put("ip", networkSub.getIpNetwork());
        jsonObject.put("le", networkSub.getLeftEnd());
        jsonObject.put("re", networkSub.getRightEnd());
        jsonObject.put("b", networkSub.isUniqueSubnetworkForEachBranches()? networkSub.getIpBranches() : "1");
        //jsonObject.put("t1", fetchSubnetClassTable());
        //jsonObject.put("t2", fetchIpReserveClassTable());
        jsonObject.put("t3", fetchPortForwardingTable());
		jsonObject.put("uniqueSubnetwork",
				networkSub.isUniqueSubnetworkForEachBranches());
		jsonObject.put("defaultGateway", networkSub.getDefaultGateway());
		jsonObject.put("enableNat", networkSub.isEnableNat());
		jsonObject.put("enablePortForwarding",
				networkSub.isEnablePortForwarding());
		
		jsonObject.put("localIpNetwork", networkSub.getLocalIpNetwork());
		
		// BR for GWL
		jsonObject.put("overrideDNSService", networkSub.isOverrideDNSService());
		overrideDNSService = networkSub.isOverrideDNSService();
		jsonObject.put("overrideDNSServiceId", networkSub.getDnsService() == null ? -1 : networkSub.getDnsService().getId());
		overrideDNSServiceId = networkSub.getDnsService() == null ? -1 : networkSub.getDnsService().getId();
        
        if (getDataSource().getSubNetwokClass().isEmpty()) {
        	jsonObject.put("nt1", true);
        } else {
        	jsonObject.put("nt1", false);
        }
        if (getDataSource().getReserveClass().isEmpty()) {
        	jsonObject.put("nt2", true);
        } else {
        	jsonObject.put("nt2", false);
        }
		if (getDataSource().getTmpPortForwardings().isEmpty()) {
			jsonObject.put("nt3", true);
		} else {
			jsonObject.put("nt3", false);
		}
        // DHCP settings
        jsonObject.put("enableDHCP", networkSub.isEnableDhcp());
        if(networkSub.isEnableDhcp()) {
        	jsonObject.put("leaseTime", networkSub.getLeaseTime());
        	jsonObject.put("ntpIP", null == networkSub.getNtpServerIp() ? "" : networkSub.getNtpServerIp());
        	jsonObject.put("dName", null == networkSub.getDomainName() ? "" : networkSub.getDomainName());
        	jsonObject.put("enableArpCheck", networkSub.isEnableArpCheck());
        	if(!getDataSource().getTmpSubNetworkDHCPCustoms().isEmpty()) {
        		jsonObject.put("tableHTML", fetchSubnetCustomTable());
        	}
        } else {
        	jsonObject.put("leaseTime", VpnNetworkSub.DEFAULT_LEASETIME);
        	jsonObject.put("ntpIP", "");
        	jsonObject.put("dName", "");
        }
        StringBuffer bf=new StringBuffer();
        bf.append("{subNetwokClass:[ ");
        List<SingleTableItem> list=getDataSource().getSubNetwokClass();
        for(int i=0;i<list.size();i++ ){
        	bf.append("{\"id\":"+i+",\"ruleKey\":"+list.get(i).getKey()+",\"type\":"+list.get(i).getType()+",\"tagValue\":\""+list.get(i).getTagValue(domainId)+"\"},");
        }        
        jsonObject.put("tt1", bf.toString().substring(0,bf.toString().length()-1)+"]}");
        bf=null;
        bf=new StringBuffer();
        bf.append("{reserveClass:[ ");
        list=getDataSource().getReserveClass();
        for(int i=0;i<list.size();i++ ){
        	bf.append("{\"id\":"+i+",\"value\":\""+list.get(i).getIpAddress()+"\",\"ruleKey\":"+list.get(i).getKey()+",\"type\":"+list.get(i).getType()+",\"tagValue\":\""+list.get(i).getTagValue(domainId)+"\"},");
        }
        jsonObject.put("tt2", bf.toString().substring(0,bf.toString().length()-1)+"]}"); 
        jsonObject.put("operation", operation);
        
        return "json";
    }

	public void loadPageLazyData(){
		if (page.isEmpty()){
			return;
		}
		Map<Long, VpnNetwork> vpnNetworkMap = new HashMap<Long, VpnNetwork>();
		String whereCon = "";
		int i=0;
		// Replace the 'LAZY' template with an empty one
		for (Object objectItem: page){
			VpnNetwork oneConfig =(VpnNetwork)objectItem;
			vpnNetworkMap.put(oneConfig.getId(), oneConfig);
			oneConfig.setVpnDnsService(new DnsServiceProfile());
//			oneConfig.setVlan(new Vlan());
			oneConfig.setSubItems(new ArrayList<VpnNetworkSub>());
			if (oneConfig.getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
				VpnNetworkSub vnpSubNet = new VpnNetworkSub();
				vnpSubNet.setIpNetwork(oneConfig.getIpAddressSpace());
				oneConfig.getSubItems().add(vnpSubNet);
			}
			whereCon = whereCon + oneConfig.getId();
			i++;
			if (page.size()!=i){
				whereCon = whereCon + ",";
			}
		}
		// Query for the template names only
//		String strSql="select a.id, a.vlan_id, b.vlanname, a.VPN_DNS_ID, c.serviceName, d.ipNetwork" +
//		", d.enableDhcp, d.ntpServerIp, d.leaseTime, d.domainName" +
//		" from VPN_NETWORK a " +
//		" inner join vlan b on a.vlan_id = b.id  " +
//		" left join DNS_SERVICE_PROFILE c on a.VPN_DNS_ID = c.id  " +
//		" left join VPN_NETWORK_SUBITEM d on  d.VPN_NETWORK_SUB_ID = a.id  " +
//		" where a.id in(" + whereCon + ")";
		
		String strSql="select a.id, a.VPN_DNS_ID, c.serviceName, d.ipNetwork" +
		", d.enableDhcp, d.ntpServerIp, d.leaseTime, d.domainName, d.localIpNetwork " +
		" from VPN_NETWORK a " +
		" left join DNS_SERVICE_PROFILE c on a.VPN_DNS_ID = c.id  " +
		" left join VPN_NETWORK_SUBITEM d on  d.VPN_NETWORK_SUB_ID = a.id  " +
		" where a.id in(" + whereCon + ")";
		
		List<?> templates = QueryUtil.executeNativeQuery(strSql);
		// Fill in the template names
		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());
//			Long vlanId = Long.valueOf(template[1].toString());
//			String vlanName = (String) template[2];
			Long dnsId=null;
			if (template[1]!=null && !template[1].toString().equals("")){
				dnsId = Long.valueOf(template[1].toString());
			}
			String dnsName = (String) template[2];
			String subIp = (String) template[3];
			String localSubIp = (String) template[8];
			
			boolean subEnableDHCP = false;
			String subNTPServer = "";
			int subLeaseTime = 0;
			String subDomain = "";
			if(null != template[4]) {
				subEnableDHCP = Boolean.parseBoolean(template[4].toString());
				if(subEnableDHCP) {
					if(null != template[5]) {
						subNTPServer = template[5].toString();
					}
					if(null != template[6]) {
						subLeaseTime = Integer.parseInt(template[6].toString());
					}
					if(null != template[7]) {
						subDomain = template[7].toString();
					}
				}
			}

			VpnNetwork templateSave = vpnNetworkMap.get(id);
			if (templateSave != null) {
//				templateSave.getVlan().setId(vlanId);
//				templateSave.getVlan().setVlanName(vlanName);
				templateSave.getVpnDnsService().setId(dnsId);
				templateSave.getVpnDnsService().setServiceName(dnsName);
				
				VpnNetworkSub vnpSubNet = new VpnNetworkSub();
				vnpSubNet.setIpNetwork(subIp);
				vnpSubNet.setLocalIpNetwork(localSubIp);
				vnpSubNet.setEnableDhcp(subEnableDHCP);
				vnpSubNet.setNtpServerIp(subNTPServer);
				vnpSubNet.setLeaseTime(subLeaseTime);
				vnpSubNet.setDomainName(subDomain);
				if (templateSave.getSubItems().isEmpty()) {
					templateSave.getSubItems().add(vnpSubNet);
				} else {
					if (subIp!=null && !subIp.equals("")) {
						templateSave.getSubItems().add(vnpSubNet);
					}
				}
			}
		}
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	private String getCertainReturnPath(String normalPath, String jsonModePath, String jsonModeDlgPath){
		if(isJsonMode() && isContentShownInDlg()) {
			return jsonModeDlgPath;
		} else if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}
	
	private String saveSubNetFunction() throws JSONException{
		jsonObject=new JSONObject();
		String[] ipMask = getSubNetwork().split("/");
		String[] natIpMask = getSubNatIpNetwork().split("/");
		for(VpnNetworkSub subnetwork:getDataSource().getSubItems()) {
			if(getSubKey() == subnetwork.getKey()) {
				if(subnetwork.isEnableDhcp()){
					subnetwork.setEnableArpCheck(isEnableArpCheck());
				}
				if(overrideDNSService){
					try {
						if(overrideDNSServiceId > 0){
							DnsServiceProfile dnsObj = findBoById(DnsServiceProfile.class, overrideDNSServiceId);
							subnetwork.setDnsService(dnsObj);
							if(dnsObj == null){
								overrideDNSService = false;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					subnetwork.setDnsService(null);
				}
				subnetwork.setOverrideDNSService(overrideDNSService);
				overrideDNS = false;
				continue;
			}
			if((StringUtils.isNotBlank(getSubNetwork()) && getSubNetwork()
					.equals(subnetwork.getLocalIpNetwork()))
					|| MgrUtil.checkIpInSameNetwork(getSubNetwork(),
							subnetwork.getLocalIpNetwork())) {
				jsonObject.put("r", false);
				jsonObject.put("name", MgrUtil.getUserMessage("error.config.network.subnetwork.exist", getSubNetwork()));
				return "json";
			}
			if (isSubEnableNat() && (getSubNatIpNetwork().equals(subnetwork.getIpNetwork())
					|| (StringUtils.isNotBlank(subnetwork.getIpNetwork()) && MgrUtil.checkIpInSameNetwork(getSubNatIpNetwork(), subnetwork.getIpNetwork())))) {
				jsonObject.put("r", false);
				jsonObject.put("natExist", MgrUtil.getUserMessage(
						"error.vpn.subnet.natnetwork.exist",
						getSubNatIpNetwork()));
				return "json";
			}
		}
		if(isSubEnablePortForwarding()){
			int positionValue=0;
			if(null!=positionRange && !"".equals(positionRange)){
				positionValue=Integer.parseInt(positionRange);
			}
			if(null!=getDataSource().getTmpPortForwardings() 
					 && !getDataSource().getTmpPortForwardings().isEmpty()){
				for(PortForwarding port:getDataSource().getTmpPortForwardings()){
					if(port.getPositionId()>positionValue){
						jsonObject.put("addPostionError", addPostionError);
						jsonObject.put("errorValue", port.getPositionId());
						jsonObject.put("r", false);
						return "json";
					}
				}
			}
			//validate portforwarding list>16 in one branch
			if(!validateBranch(jsonObject)){
				jsonObject.put("r", false);
				return "json";
			}

      }
		if (isSubEnableIpReserveClass()) {
			List<String> ipLst = new ArrayList<String>();
			if (!getDataSource().getReserveClass().isEmpty()){
				for(SingleTableItem vc: getDataSource().getReserveClass()){
					if(vc.getKey()!=getSubKey())continue;
					if (!MgrUtil.checkIpInSameSubnet(isSubUniqueSubnetworkForEachBranches() ? ipMask[0] : natIpMask[0],
							vc.getIpAddress(), isSubUniqueSubnetworkForEachBranches() ? AhDecoder.int2Netmask(Integer.parseInt(ipMask[1])) : AhDecoder.int2Netmask(Integer.parseInt(natIpMask[1])))){
						jsonObject.put("r", false);
						jsonObject.put("m", getText("error.config.network.dhcp.ip.pool.network", new String[]{vc.getIpAddress(),isSubUniqueSubnetworkForEachBranches()? getSubNetwork() : getSubNatIpNetwork() }));
						return "json";
					}
					ipLst.add(vc.getIpAddress());
				}
				String msg = "";
				if(isSubUniqueSubnetworkForEachBranches()) {
					msg = MgrUtil.checkIpInSameBranch(ipMask[0],
							AhDecoder.int2Netmask(Integer.parseInt(ipMask[1])),
							getSubBranch(), ipLst);
				} else {
					int subnetworksCount = (int) Math.pow(2, Integer.parseInt(ipMask[1]) - Integer.parseInt(natIpMask[1]));
					msg = MgrUtil.checkIpInSameBranch(natIpMask[0],
							AhDecoder.int2Netmask(Integer.parseInt(natIpMask[1])),
							subnetworksCount, ipLst);
				}
				if (!"".equals(msg)){
					jsonObject.put("r", false);
					jsonObject.put("m", msg);
					return "json";
				}
			}
		}
		jsonObject.put("modify", updateSubNetworkItem());
		removeAllSubnetKeyList(getSubKey());
		if (isSubEnableDhcp()) {
			getDataSource().getSubnetworkDHCPCustoms().addAll(getDataSource().getTmpSubNetworkDHCPCustoms());
		} else {
			getDataSource().getTmpSubNetworkDHCPCustoms().clear();
		}
//		if (isSubEnableNetClass()){
//			getDataSource().getSubNetwokClass().addAll(getDataSource().getTmpSubNetwokClass());
//		} else {
//			getDataSource().getTmpSubNetwokClass().clear();
//		}
//		if (isSubEnableIpReserveClass()) {
//			getDataSource().getReserveClass().addAll(getDataSource().getTmpReserveClass());
//		} else {
//			getDataSource().getTmpReserveClass().clear();
//		}
		if (isSubEnablePortForwarding()) {
			getDataSource().getPortForwardings().addAll(
					getDataSource().getTmpPortForwardings());
		} else {
			getDataSource().getTmpPortForwardings().clear();
		}
		
		if(!((boolean) jsonObject.get("modify")) && "newOverrideDNS".equals(operation)){
			try {
				return setSubnetworkValues(true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		jsonObject.put("t", fetchMainSubnetTable());
		jsonObject.put("operation", operation);
		jsonObject.put("r", true);
		return "json";
	}
	
	private boolean checkWebSecurity(){
		if (getDataSource().getWebSecurity()<=0){
			return true;
		}
		List<HMServicesSettings> list = QueryUtil.executeQuery(
				HMServicesSettings.class, null, new FilterParams("owner.id",
						getDomainId()));
		if (list.isEmpty()){
			addActionJsonError(getText("error.config.vpnnetwork.webSenseError", new String[]{"Web security settings"}));
			prepareDependentObjects();
			return false;
		}
		
		if (getDataSource().getWebSecurity()==VpnNetwork.VPN_NETWORK_WEBSECURITY_WEBSENSE){
			if (!list.get(0).isEnableWebsense()){
				addActionJsonError(getText("error.config.vpnnetwork.webSenseError", new String[]{"Websense web security settings"}));
				prepareDependentObjects();
				return false;	
			}
		}
		if (getDataSource().getWebSecurity()==VpnNetwork.VPN_NETWORK_WEBSECURITY_BARRACUDA){
			if (!list.get(0).isEnableBarracuda()){
				addActionJsonError(getText("error.config.vpnnetwork.webSenseError", new String[]{"Barracuda web security settings"}));
				prepareDependentObjects();
				return false;	
			}
		}
		return true;
	}
	
    private boolean checkMgmtDHCPOption() {
        if (getDataSource().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
            if(getDataSource().getSubItems().isEmpty()) {
                addActionJsonError(getText("warn.config.vpnnetwork.not.subnetwork"));
                prepareDependentObjects();
                return false;
            }
            for (VpnNetworkSub subItem : getDataSource().getSubItems()) {
                if (!subItem.isEnableDhcp()) {
                    addActionJsonError(getText("warn.config.vpnnetwork.not.one.enableDHCP",
                            new String[] { subItem.getIpNetwork() }));
                    prepareDependentObjects();
                    return false;
                }
            }
        }
        return true;
    }
	
	private void addActionJsonError(String errMsg) {
		if (isJsonMode() && isContentShownInDlg()) {
			try {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", errMsg);
			} catch (JSONException e) {
				reportActionError(e);
			}
		} else {
			addActionError(errMsg);
		}
	}
	
	public static void setCloneFields(VpnNetwork source, VpnNetwork destination){
		List<DhcpServerOptionsCustom> optionCustons = new ArrayList<DhcpServerOptionsCustom>();
		for (DhcpServerOptionsCustom tempClass : source.getCustomOptions()) {
			optionCustons.add(tempClass);
		}
		destination.setCustomOptions(optionCustons);

		List<VpnNetworkSub> netWorkSub = new ArrayList<VpnNetworkSub>();
		for (VpnNetworkSub tempClass : source.getSubItems()) {
			netWorkSub.add(tempClass);
		}
		destination.setSubItems(netWorkSub);
		
		List<SingleTableItem> vcs = new ArrayList<SingleTableItem>();
		for (SingleTableItem tempClass : source.getSubNetwokClass()) {
		    vcs.add(tempClass);
		}
		destination.setSubNetwokClass(vcs);
		
		List<SingleTableItem> vIps = new ArrayList<SingleTableItem>();
		for (SingleTableItem tempClass : source.getReserveClass()) {
			vIps.add(tempClass);
		}
		destination.setReserveClass(vIps);
		
		List<PortForwarding> portForwardings = new ArrayList<PortForwarding>();
		for (PortForwarding tempClass : source.getPortForwardings()) {
			portForwardings.add(tempClass);
		}
		destination.setPortForwardings(portForwardings);
		
		List<SubNetworkResource> subNetworkRes = new ArrayList<SubNetworkResource>();
		for (SubNetworkResource subResource : source.getSubNetworkRes()){
			subNetworkRes.add(subResource);
		}
		destination.setSubNetworkRes(subNetworkRes);
		
		List<SubnetworkDHCPCustom> subnetworkCustoms = new ArrayList<SubnetworkDHCPCustom>();
		for (SubnetworkDHCPCustom subnetworkCustom : source.getSubnetworkDHCPCustoms()){
			subnetworkCustoms.add(subnetworkCustom);
		}
		destination.setSubnetworkDHCPCustoms(subnetworkCustoms);
	}
	
	private String fetchMainSubnetTable(){
		if (getDataSource().getSubItems().isEmpty()) {
			return "";
		}
		StringBuilder sbf = new StringBuilder();
		sbf.append("<table class=\"view\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sbf.append("<tbody>");
		int i=0;
		for(VpnNetworkSub vs: getDataSource().getSubItems()){
			if ((i > 0 && i%2==0) || i == 1) {
				sbf.append("<tr class='even'>");
			} else {
				sbf.append("<tr class='odd'>");
			}
			sbf.append("<td class=\"listCheck\" width=\"25px\" style=\"padding-left: 0px;\"><input type=\"checkbox\" name=\"ruleIndices\" value=\"").append(i).append("\"></td>");
			sbf.append("<td class=\"list\" width=\"110px\"><a href=\"#\" onclick=\"modifySubnetwork(").append(i).append(") \">").append(vs.getIpNetwork()).append("</a></td>");
			sbf.append("<td class=\"list\" width=\"110px\">").append(vs.getIpNetwork()).append("</td>");
			sbf.append("<td class=\"list\" width=\"60px\">").append(vs.getIpBranches()).append("</td>");
			sbf.append("<td class=\"list\" width=\"90px\">").append(vs.getIpBranchesCount()).append("</td>");
			sbf.append("<td class=\"list\" width=\"40px\">").append(vs.getLeftEnd()).append("</td>");
			sbf.append("<td class=\"list\" width=\"40px\">").append(vs.getRightEnd()).append("</td>");
			sbf.append("<td class=\"list\" width=\"75px\">").append(vs.getRangeSize()).append("</td>");
			
			sbf.append("</tr>");
			
			if (vs.isSubnetClassification()) {
				sbf.append("<tr>");
				sbf.append("<td/><td colspan=\"5\">");
				sbf.append("<table>");
				sbf.append("<tbody>");
				sbf.append("<tr><td>");
				sbf.append("<script type=\"text/javascript\">insertFoldingLabelContext('").append(MgrUtil.getUserMessage("config.vpn.network.subLst.deviceClass")).append("','subNetworkClassifierRow_").append(i).append("');</script>");
				sbf.append("</td></tr>");
				sbf.append("<tr id=\"subNetworkClassifierRow_").append(i).append("\" style=\"display: none;\"><td style=\"padding-left: 20px\">").append(getDataSource().reserveItems2String(vs.getKey(), getDataSource().getSubNetwokClass(),domainId)).append("&nbsp;</td></tr>");
				sbf.append("</td></tr></tbody></table></td></tr>");
			} 
			
			if (vs.isReserveClassification()) {
				sbf.append("<tr>");
				sbf.append("<td/><td colspan=\"5\">");
				sbf.append("<table>");
				sbf.append("<tbody>");
				sbf.append("<tr><td>");
				sbf.append("<script type=\"text/javascript\">insertFoldingLabelContext('").append(MgrUtil.getUserMessage("config.vpn.network.subLst.ipReser")).append("','ipClassificationRow_").append(i).append("');</script>");
				sbf.append("</td></tr>");
				sbf.append("<tr id=\"ipClassificationRow_").append(i).append("\" style=\"display: none;\"><td style=\"padding-left: 20px\">").append(getDataSource().reserveItems2String(vs.getKey(), getDataSource().getReserveClass(),domainId)).append("&nbsp;</td></tr>");
				sbf.append("</td></tr></tbody></table></td></tr>");
			}

			i++;
		}
		sbf.append("</tbody>");
		sbf.append("</table>");
		return sbf.toString();
	}
	   
    private String fetchSubnetCustomTable(){
        if (getDataSource().getTmpSubNetworkDHCPCustoms().isEmpty()) {
            return "";
        }
        StringBuilder sbf = new StringBuilder();
        sbf.append("<table class=\"view\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed\">");
        sbf.append("<tbody>");
        int i=0;
        for(SubnetworkDHCPCustom vc: getDataSource().getTmpSubNetworkDHCPCustoms()){
            if (i%2==0) {
                sbf.append("<tr class='even'>");
            } else {
                sbf.append("<tr class='odd'>");
            }
            sbf.append("<td class=\"listCheck\" width=\"25px\"><input type=\"checkbox\" name=\"subCustomIndices\" value=\"").append(i).append("\"></td>");
            sbf.append("<td class=\"list\" width=\"110px\">").append(vc.getNumber()).append("</td>");
            sbf.append("<td class=\"list\" width=\"100px\">").append(vc.getStrType()).append("</td>");
            sbf.append("<td class=\"list\">").append("<span class=\"ellipsis\" title=\"").append(vc.getValue()).append("\" style=\"width:190px;\">").append(vc.getValue()).append("</span></td>");
            sbf.append("</tr>");
            i++;
        }
        sbf.append("</tbody>");
        sbf.append("</table>");
        return sbf.toString();
    }
	
	private String fetchPortForwardingTable() {
		if (getDataSource().getTmpPortForwardings().isEmpty()) {
			return "";
		}
		StringBuilder sbf = new StringBuilder();
		sbf.append("<table class=\"view\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed\">");
		sbf.append("<tbody>");
		int i = 0;
		for (PortForwarding vc : getDataSource().getTmpPortForwardings()) {
			if (i % 2 == 0) {
				sbf.append("<tr class='even'>");
			} else {
				sbf.append("<tr class='odd'>");
			}
			sbf.append(
					"<td class=\"listCheck\" width=\"25px\"><input type=\"checkbox\" name=\"PortForwardingIndices\" value=\"")
					.append(i).append("\"></td>");
			sbf.append("<td class=\"list\" width=\"120px\">")
					.append(vc.getDestinationPortNumber()).append("</td>");
			sbf.append("<td class=\"list\" width=\"120px\">")
					.append(vc.getPositionId()).append("</td>");
			sbf.append("<td class=\"list\" width=\"120px\">")
					.append(vc.getInternalHostPortNumber()).append("</td>");
			sbf.append("<td class=\"list\" width=\"100px\">")
			        .append(vc.getProtocolType()).append("</td>");
			sbf.append("</tr>");
			i++;
		}
		sbf.append("</tbody>");
		sbf.append("</table>");
		return sbf.toString();
	}
    
    private String fetchSubnetClassTable() {
        if (getDataSource().getSubNetwokClass().isEmpty()) {
            return "";
        }
        StringBuilder sbf = new StringBuilder();
        sbf.append("<table class=\"view\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        sbf.append("<tbody>");
        int i = 0;
        for (SingleTableItem vc : getDataSource().getSubNetwokClass()) {
            if (i % 2 == 0) {
                sbf.append("<tr class='even'>");
            } else {
                sbf.append("<tr class='odd'>");
            }
            sbf.append(
                    "<td class=\"listCheck\" width=\"25px\"><input type=\"checkbox\" name=\"subnetClassIndices\" value=\"")
                    .append(i).append("\"></td>");
            sbf.append("<td class=\"list\" width=\"120px\">").append(vc.getUseTypeName())
                    .append("</td>");
            String tdContent = "";
            if (vc.getType() == SingleTableItem.TYPE_MAP) {
                MapContainerNode location = vc.getLocation();
                if (null != location) {
                    tdContent = location.getMapName();
                }
            } else if (vc.getType() == SingleTableItem.TYPE_HIVEAPNAME) {
                tdContent = vc.getTypeName();
            } else {
                tdContent = vc.getClassifierName(domainId);
            }
            sbf.append("<td class=\"list\" width=\"240px\">").append(tdContent).append("</td>");
            sbf.append("</tr>");
            i++;
        }
        sbf.append("</tbody>");
        sbf.append("</table>");
        return sbf.toString();
    }
	
	private String fetchIpReserveClassTable(){
		if (getDataSource().getReserveClass().isEmpty()) {
			return "";
		}
		StringBuilder sbf = new StringBuilder();
		sbf.append("<table class=\"view\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sbf.append("<tbody>");
		int i=0;
		for(SingleTableItem vc: getDataSource().getReserveClass()){
			if (i%2==0) {
				sbf.append("<tr class='even'>");
			} else {
				sbf.append("<tr class='odd'>");
			}
			sbf.append("<td class=\"listCheck\" width=\"25px\"><input type=\"checkbox\" name=\"ipReserveClassIndices\" value=\"").append(i).append("\"></td>");
			sbf.append("<td class=\"list\" width=\"120px\">").append(vc.getIpAddress()).append("</td>");
			sbf.append("<td class=\"list\" width=\"120px\">").append(vc.getUseTypeName()).append("</td>");
			String tdContent = "";
			if(vc.getType() == SingleTableItem.TYPE_MAP) {
				MapContainerNode location = vc.getLocation();
				if(null != location) {
					tdContent = location.getMapName();
				}
			} else if(vc.getType() == SingleTableItem.TYPE_HIVEAPNAME) {
				tdContent = vc.getTypeName();
			} else {
				tdContent = vc.getClassifierName(domainId);
			}
			sbf.append("<td class=\"list\" width=\"240px\">").append(tdContent).append("</td>");
			sbf.append("</tr>");
			i++;
		}
		sbf.append("</tbody>");
		sbf.append("</table>");
		return sbf.toString();
	}

	private int generateSubnetKey(){
		for (int i=1; i<=16; i++) {
			boolean existFlg=false;
			for(VpnNetworkSub subClass: getDataSource().getSubItems()) {
				if (subClass.getKey()==i) {
					existFlg=true;
					break;
				}
			}
			if (!existFlg) {
				return i;
			}
		}
		return 0;
	}
	
	private void removeAllSubnetKeyList(int key) {
		List<SubnetworkDHCPCustom> tmpCustomList = new ArrayList<SubnetworkDHCPCustom>();
		for (SubnetworkDHCPCustom custom : getDataSource().getSubnetworkDHCPCustoms()) {
			if(custom.getKey() == key) {
				tmpCustomList.add(custom);
			}
		}
		getDataSource().getSubnetworkDHCPCustoms().removeAll(tmpCustomList);
		
//		List<SingleTableItem> removeList = new ArrayList<SingleTableItem>();
//		for (SingleTableItem nc : getDataSource().getSubNetwokClass()) {
//			if (nc.getKey()==getSubKey()) {
//				removeList.add(nc);
//			}
//		}
//		getDataSource().getSubNetwokClass().removeAll(removeList);
		
//		List<SingleTableItem> ipReserveList = new ArrayList<SingleTableItem>();
//		for (SingleTableItem nc : getDataSource().getReserveClass()) {
//			if (nc.getKey()==getSubKey()) {
//				ipReserveList.add(nc);
//			}
//		}
//		getDataSource().getReserveClass().removeAll(ipReserveList);

		List<PortForwarding> portForwardingList = new ArrayList<PortForwarding>();
		for (PortForwarding port : getDataSource().getPortForwardings()) {
			if (port.getKey() ==key) {
				portForwardingList.add(port);
			}
		}
		getDataSource().getPortForwardings().removeAll(portForwardingList);
	}
	
	private boolean updateSubNetworkItem(){
		boolean existSubItem = false;
		for(VpnNetworkSub vs: getDataSource().getSubItems()){
			if (vs.getKey()==getSubKey()) {
				setSubnetworkValue(vs);
				
				existSubItem=true;
			}
		}
		if (!existSubItem) {
			VpnNetworkSub vs = new VpnNetworkSub();
			vs.setKey(getSubKey());
			if(overrideDNSService){
				if(overrideDNSServiceId > 0){
					try {
						DnsServiceProfile dnsObj = findBoById(DnsServiceProfile.class, overrideDNSServiceId);
						vs.setDnsService(dnsObj);
						if(dnsObj == null){
							overrideDNSService = false;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				vs.setOverrideDNSService(overrideDNSService);
				overrideDNS = overrideDNSService;
			}
			setSubnetworkValue(vs);
			getDataSource().getSubItems().add(vs);
		}
		return existSubItem;
	}

	private void setSubnetworkValue(VpnNetworkSub vs) {
		vs.setReserveClassification(isSubEnableIpReserveClass());
		vs.setSubnetClassification(isSubEnableNetClass());
		
		vs.setEnableDhcp(isSubEnableDhcp());
		vs.setDefaultGateway(getSubDefaultGateway());
		vs.setUniqueSubnetworkForEachBranches(isSubUniqueSubnetworkForEachBranches());
		vs.setEnableNat(isSubEnableNat());
		vs.setEnablePortForwarding(isSubEnablePortForwarding());
		
		if(isSubEnableNat()) {
			vs.setIpNetwork(getSubNatIpNetwork());
			vs.setLocalIpNetwork(getSubNetwork());
		} else {
			vs.setIpNetwork(getSubNetwork());
			vs.setLocalIpNetwork(getSubNetwork());
		}
		
		if(isSubUniqueSubnetworkForEachBranches()) {
			vs.setIpBranches(getSubBranch());
		} else {
			int flagNetIndex = vs.getLocalIpNetwork().indexOf("/");
			int netMask = Integer.valueOf(vs.getLocalIpNetwork().substring(flagNetIndex + 1));
			int flagNatNetIndex = vs.getIpNetwork().indexOf("/");
			int natNetMask = Integer.valueOf(vs.getIpNetwork().substring(flagNatNetIndex + 1));
			int branches = (int) Math.pow(2, netMask - natNetMask);
			vs.setIpBranches(branches);
		}
		
		if(vs.isEnableDhcp()) {
			vs.setLeftEnd(getSubLeftEnd());
			vs.setRightEnd(getSubRightEnd());
			vs.setLeaseTime(getSubLeaseTime());
			vs.setEnableArpCheck(isEnableArpCheck());
			
			if(StringUtils.isNotBlank(subNtpServerIp)) {
				vs.setNtpServerIp(subNtpServerIp);
			} else {
			    vs.setNtpServerIp(null);
			}
			if(StringUtils.isNotBlank(subDomainName)) {
				vs.setDomainName(subDomainName);
			} else {
			    vs.setDomainName(null);
			}
		} else {
			vs.setLeftEnd(0);
			vs.setRightEnd(0);
			vs.setLeaseTime(VpnNetworkSub.DEFAULT_LEASETIME);
			vs.setNtpServerIp("");
			vs.setDomainName("");
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_VPN_NETWORK);
		setDataSource(VpnNetwork.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_VPN_NETWORK;
		preparePortList();
	}
	
  private void preparePortList(){
		if(null==udpPortList){
			udpPortList=getPortList(udpFileName);
		}
		if(null==tcpPortList){
			tcpPortList=getPortList(tcpFileName);
		}
  }
	@Override
	public VpnNetwork getDataSource() {
		return (VpnNetwork) dataSource;
	}
	public String getChangedName() {
		return getDataSource().getNetworkName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public void prepareDependentObjects() {
//		list_vlan = getBoCheckItems("vlanName", Vlan.class, null,CHECK_ITEM_BEGIN_BLANK,CHECK_ITEM_END_NO);
		list_dnsService = getBoCheckItems("serviceName", DnsServiceProfile.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
//		if (getDataSource().getVlan() != null) {
//			vlanId = getDataSource().getVlan().getId();
//			inputVlanIdValue = getDataSource().getVlan().getVlanName();
//		} else {
//			Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
//			vlanId = vlanClass.getId();
//			inputVlanIdValue=vlanClass.getVlanName();
//		}
		
		if(isOverrideDNS()){
			overrideDNSServiceId = dnsServiceId;
		}
		
		if (getDataSource().getVpnDnsService() != null) {
			dnsServiceId = getDataSource().getVpnDnsService().getId();
		}
	}
	
	public void prepareSaveObjects() throws Exception {
//		if (vlanId != null) {
//			if (vlanId==-1){
//				Vlan myVlan = CreateObjectAuto.createNewVlan(inputVlanIdValue,getDomain(),"");
//				if (myVlan!=null){
//					getDataSource().setVlan(myVlan);
//				}
//			} else {
//				Vlan tmpClass = findBoById(Vlan.class, vlanId);
//				getDataSource().setVlan(tmpClass);
//			}
//		}
		
		if(isOverrideDNS() && overrideDNSServiceId == null && !"update".equals(operation)){
			if(dnsServiceId != null){
				DnsServiceProfile tmpClass = findBoById(DnsServiceProfile.class, dnsServiceId);
				VpnNetworkSub subNetwork = null;
				if("continue".equals(operation)){
					for(VpnNetworkSub vs : getDataSource().getSubItems()){
						if(vs.getKey() > 0 && vs.getKey() == getDataSource().getSubItems().size()){
							subNetwork = vs;
							ruleOneIndex = vs.getKey() - 1;
						}
					}
				}else{
					subNetwork = getDataSource().getSubItems().get(getRuleOneIndex());
				}
				subNetwork.setDnsService(tmpClass);
				overrideDNSServiceId = dnsServiceId;
			}
			
		}else{
			if (dnsServiceId != null) {
				DnsServiceProfile tmpClass = findBoById(DnsServiceProfile.class, dnsServiceId);
				getDataSource().setVpnDnsService(tmpClass);
			}
		}
	}
	
	private boolean addSingleCustom() throws Exception {
		int totalCount = getDataSource().getCustomOptions().size();
		for (DhcpServerOptionsCustom custom : getDataSource().getCustomOptions()) {
			// the custom number cannot repeat
			if (custom.getNumber() == customNumber) {
				addActionError(MgrUtil
						.getUserMessage("error.addObjectExists"));
				return false;
			}
			if (custom.getNumber() == 225 || custom.getNumber() == 226) {
				totalCount--;
			}
		}
		// the max number of custom option is 8 except 225 and 226
		if (customNumber != 225 && customNumber != 226 && totalCount >= 8) {
			addActionError(MgrUtil
					.getUserMessage("error.entryLimit", new String[] {getText("config.network.object.dhcp.server.options.custom"), "8"})
					+ "(except 225 and 226)");
			return false;
		}
		DhcpServerOptionsCustom oneItem = new DhcpServerOptionsCustom();
		oneItem.setNumber(customNumber);
		oneItem.setType(customType);
		switch (customType) {
		case DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER:
			oneItem.setValue(integerValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_IP:
			oneItem.setValue(ipValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING:
			oneItem.setValue(strValue);
			break;
		case DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX:
			oneItem.setValue(hexValue);
			break;
		default:
			break;
		}
		getDataSource().getCustomOptions().add(oneItem);
		customNumber = 0;
		customType = DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
		integerValue = "";
		ipValue = "";
		strValue = "";
		hexValue = "";
		return true;
	}
	
	private void removeSelectedCustom() {
		if (customIndices != null) {
			Collection<DhcpServerOptionsCustom> removeList = new Vector<DhcpServerOptionsCustom>();
			for (String serviceIndex : customIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getCustomOptions().size()) {
						removeList.add(getDataSource().getCustomOptions().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getCustomOptions().removeAll(removeList);
		}
	}
	
	private void removeSelectedSubnetwork() {
		if (ruleIndices != null) {
			Collection<VpnNetworkSub> removeList = new Vector<VpnNetworkSub>();
			Set<Integer> keySet = new HashSet<Integer>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getSubItems().size()) {
						removeList.add(getDataSource().getSubItems().get(index));
						keySet.add(getDataSource().getSubItems().get(index).getKey());
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getSubItems().removeAll(removeList);
			for(Integer keyValue: keySet){
				removeAllSubnetKeyList(keyValue);
			}
		}
	}
	
//	private void removeSelectedSubnetClass() {
//		if (subnetClassIndices != null && subnetClassIndices.length()>0) {
//			List<SingleTableItem> removeList = new ArrayList<SingleTableItem>();
//			String[] calssIndices = subnetClassIndices.split(",");
//			for (String serviceIndex : calssIndices) {
//				try {
//					int index = Integer.parseInt(serviceIndex);
//					if (index < getDataSource().getTmpSubNetwokClass().size()) {
//						removeList.add(getDataSource().getTmpSubNetwokClass().get(index));
//					}
//				} catch (NumberFormatException e) {
//					// Bug in struts, shouldn't create a 'false' entry when no
//					// check boxes checked.
//					return;
//				}
//			}
//			getDataSource().getTmpSubNetwokClass().removeAll(removeList);
//		}
//	}
//	
//	private void removeSelectedIpReserveClass() {
//		if (ipReserveClassIndices != null && ipReserveClassIndices.length()>0) {
//			List<SingleTableItem> removeList = new ArrayList<SingleTableItem>();
//			String[] calssIndices = ipReserveClassIndices.split(",");
//			for (String serviceIndex : calssIndices) {
//				try {
//					int index = Integer.parseInt(serviceIndex);
//					if (index < getDataSource().getTmpReserveClass().size()) {
//						removeList.add(getDataSource().getTmpReserveClass().get(index));
//					}
//				} catch (NumberFormatException e) {
//					// Bug in struts, shouldn't create a 'false' entry when no
//					// check boxes checked.
//					return;
//				}
//			}
//			getDataSource().getTmpReserveClass().removeAll(removeList);
//		}
//	}
	
	private void removeSelectedSubCustoms() {
		if (subCustomIndices != null && !subCustomIndices.isEmpty()) {
			List<SubnetworkDHCPCustom> removeList = new ArrayList<SubnetworkDHCPCustom>();
			String[] calssIndices = subCustomIndices.split(",");
			for (String serviceIndex : calssIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getTmpSubNetworkDHCPCustoms().size()) {
						removeList.add(getDataSource().getTmpSubNetworkDHCPCustoms().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getTmpSubNetworkDHCPCustoms().removeAll(removeList);
		}
	}
	
	private void removeSelectedPortForwarding() {
		if (PortForwardingIndices != null && PortForwardingIndices.length() > 0) {
			List<PortForwarding> removeList = new ArrayList<PortForwarding>();
			String[] calssIndices = PortForwardingIndices.split(",");
			for (String serviceIndex : calssIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getTmpPortForwardings().size()) {
						removeList.add(getDataSource().getTmpPortForwardings()
								.get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getTmpPortForwardings().removeAll(removeList);
		}
	}
	
	// start for Network IP resource count (zhang)
	
	public static void prepareSubNetworkRes(Long newId, VpnNetwork oldVpnNetwork) throws Exception{
		VpnNetwork networkObj = QueryUtil.findBoById(VpnNetwork.class, newId, new VpnNetworksAction());
		if(oldVpnNetwork != null){
			boolean needUpdate = false;
			if(!networkObj.equalsSubNetwok(oldVpnNetwork)){
				networkObj.setSubNetworkRes(getSubNetworkResourceList(networkObj));
				copyNetworkMapping(networkObj, oldVpnNetwork);
				needUpdate = true;
			}
			if(!networkObj.equalsReserveClass(oldVpnNetwork)){
				initVIPIpAddress(networkObj);
				needUpdate = true;
			}
			//create new bo
			if(needUpdate){
				updateSubNetworkResourceBo(networkObj);
			}
//			//remove bo
//			List<Long> rmResourceList = new ArrayList<Long>();
//			for(SubNetworkResource subBoOld : oldVpnNetwork.getSubNetworkRes()){
//				boolean exists = false;
//				for(SubNetworkResource subBoNew : networkObj.getSubNetworkRes()){
//					if(subBoOld.getId().equals(subBoNew.getId())){
//						exists = true;
//						break;
//					}
//				}
//				if(!exists){
//					rmResourceList.add(subBoOld.getId());
//				}
//			}
//			try{
//				QueryUtil.removeBos(SubNetworkResource.class, rmResourceList);
//			}catch(Exception ex){
//				
//			}
		}else{
			networkObj.setSubNetworkRes(getSubNetworkResourceList(networkObj));
			initVIPIpAddress(networkObj);
			updateSubNetworkResourceBo(networkObj);
		}
	}
	
	public void deleteSubNetworkResource(Collection<Long> rmIds) throws Exception{
		if(rmIds == null || rmIds.isEmpty()){
			return;
		}
		/*String inSql = "";
		for(Long id : rmIds){
			if("".equals(inSql)){
				inSql += id;
			}else{
				inSql += ", " + id;
			}
		}
		if(!"".equals(inSql)){
			inSql = "(" + inSql + ")";
		}
		String sqlToFree = "delete from sub_network_resource bo1 where networkId in "+ inSql;
		QueryUtil.executeNativeUpdate(sqlToFree);*/
		QueryUtil.bulkRemoveBos(SubNetworkResource.class,new FilterParams("vpnNetwork.id",rmIds));
		
	}
	
	private static void updateSubNetworkResourceBo(VpnNetwork networkObj) throws Exception{
		if(networkObj == null || networkObj.getSubNetworkRes() == null){
			return;
		}
		for(SubNetworkResource subBo : networkObj.getSubNetworkRes()){
			try{
				subBo.setOwner(networkObj.getOwner());
				if(subBo.getId() == null){
					Long id = QueryUtil.createBo(subBo);
					subBo.setId(id);
				}else{
					/*String sql = "update SUB_NETWORK_RESOURCE " + 
							(subBo.getHiveApMac()==null? " set hiveApMac = null" : " set hiveApMac = '"+subBo.getHiveApMac()+"'") +
								", hiveApMgtx = "+subBo.getHiveApMgtx()+
								", status = "+subBo.getStatus()+
								", vipipaddress = "+subBo.isVipIpAddress()+
								", firstIp = '"+subBo.getFirstIp()+"'"+
								", ipEndLong = "+subBo.getIpEndLong()+
								", ipPoolEnd = '"+subBo.getIpPoolEnd()+"'"+
								", ipPoolStart = '"+subBo.getIpPoolStart()+"'"+
								", ipStartLong = "+subBo.getIpStartLong()+
								", network = '"+subBo.getNetwork()+"'"+
								", parentNetwork = '"+subBo.getParentNetwork()+"'"+
								" where id = "+subBo.getId();
					QueryUtil.executeNativeUpdate(sql);*/
					QueryUtil.updateBos(SubNetworkResource.class, 
								"hiveApMac = :s1 ," +
								"hiveApMgtx = :s2," +
								"status = :s3 ," +
								"vipipaddress = :s4," +
								"firstIp = :s5," +
								"ipEndLong = :s6," +
								"ipPoolEnd = :s7," +
								"ipPoolStart = :s8," +
								"ipStartLong = :s9," +
								"network = :s10," +
								"parentNetwork = :s11," + 
								"localNetwork = :s12," +
								"parentLocalNetwork = :s13", "id = :s14",new Object[]{
									subBo.getHiveApMac(),subBo.getHiveApMgtx(),
									subBo.getStatus(),subBo.isVipIpAddress(),
									subBo.getFirstIp(),subBo.getIpEndLong(),
									subBo.getIpPoolEnd(),subBo.getIpPoolStart(),
									subBo.getIpStartLong(),subBo.getNetwork(),
									subBo.getParentNetwork(),subBo.getLocalNetwork(),
									subBo.getParentLocalNetwork(),subBo.getId()
							} );
					
					
//					QueryUtil.updateBo(subBo);
				}
			}catch(Exception ex){
				log.error(ex);
			}
		}
	}
	
	private static void initVIPIpAddress(VpnNetwork newObj){
		if(newObj.getSubNetworkRes() != null){
			for(SubNetworkResource subSource : newObj.getSubNetworkRes()){
				subSource.setVipIpAddress(false);
			}
		}
		
		if(newObj.getReserveClass() != null){
			for(SingleTableItem item : newObj.getReserveClass()){
				String ip = item.getIpAddress();
				long ipLong = AhEncoder.ip2Long(ip);
				for(SubNetworkResource subSource : newObj.getSubNetworkRes()){
					if(ipLong >= subSource.getIpStartLong() && ipLong <= subSource.getIpEndLong()){
						subSource.setVipIpAddress(true);
					}
				}
			}
		}
	}
	
	private boolean validateSubCustom() throws JSONException {
		int totalCount = 0, subtractCount = 0;
		for (SubnetworkDHCPCustom custom : getDataSource().getTmpSubNetworkDHCPCustoms()) {
			if(getSubKey() == custom.getKey()) {
				totalCount++;
				// the custom number cannot repeat
				if (custom.getNumber() == subCustomNumber) {
					jsonObject.put("succ", false);
					jsonObject.put("msg", MgrUtil
							.getUserMessage("error.addObjectExists"));
					return false;
				}
				if (custom.getNumber() == 225 || custom.getNumber() == 226) {
					subtractCount++;
				}
			}
		}
		// the max number of custom option is 8 except 225 and 226
		if (subCustomNumber != 225 && subCustomNumber != 226 && (totalCount -subtractCount) >= 8) {
			jsonObject.put("succ", false);
			jsonObject.put("msg", MgrUtil.getUserMessage("error.entryLimit", 
							new String[] {getText("config.network.object.dhcp.server.options.custom"), "8"})
					+ "(except 225 and 226)");
			return false;
		}
		return true;
	}
	
	/**
	 * Bug 15004
	 * @author Yunzhi Lin
	 * - Time: Sep 26, 2011 6:12:49 PM
	 * @return json object
	 */
	private String getSelectedSubItemValues() {
		jsonObject = new JSONObject();
		try {
			String sql = "select ipNetwork, enableDhcp, ntpServerIp, leaseTime, domainName"
					+ " from VPN_NETWORK_SUBITEM where VPN_NETWORK_SUB_ID =" + selectedNetworkId
					+ " and ipNetwork = '" + selectedSubItem + "'";

			List<?> result = QueryUtil.executeNativeQuery(sql);
			log.debug("result=" + result);
			if (!result.isEmpty()) {
				Object[] data = (Object[]) result.get(0);
				jsonObject.put("succ", true);
				String dhcpValue = "Disabled";
				String ntpServerValue = "";
				String leaseTimeValue = "";
				String domainValue = "";
				if (null != data[1] && Boolean.parseBoolean(data[1].toString())) {
					dhcpValue = "Enabled";
					if (null != data[2]) {
						ntpServerValue = data[2].toString();
					}
					if (null != data[3]) {
						leaseTimeValue = data[3].toString();
					}
					if (null != data[4]) {
						domainValue = data[4].toString();
					}
				}
				jsonObject.put("dhcp", dhcpValue);
				jsonObject.put("ntpServer", ntpServerValue);
				jsonObject.put("leaseTime", leaseTimeValue);
				jsonObject.put("domainName", domainValue);
			}
		} catch (Exception e) {
			log.error("Error when query the Subitem values", e);
		}
		return "json";
	}
	
	private static void copyNetworkMapping(VpnNetwork newObj, VpnNetwork oldObj){
		if(oldObj == null || oldObj.getSubNetworkRes() == null){
			return;
		}
//		Map<Long, SubNetworkResource> sourceKeepMap = new HashMap<Long, SubNetworkResource>();
		List<Long> rmIds = new ArrayList<Long>();
		for(SubNetworkResource oldItem : oldObj.getSubNetworkRes()){
			boolean find = false;
			if(newObj != null && newObj.getSubNetworkRes() != null){
				for(SubNetworkResource newItem : newObj.getSubNetworkRes()){
					if(oldItem.getNetwork().equals(newItem.getNetwork()) && oldItem.getLocalNetwork().equals(newItem.getLocalNetwork())){
						newItem.setId(oldItem.getId());
						newItem.setHiveApMac(oldItem.getHiveApMac());
						newItem.setStatus(oldItem.getStatus());
						newItem.setHiveApMgtx(oldItem.getHiveApMgtx());
						
//						oldItem.setFirstIp(newItem.getFirstIp());
//						oldItem.setIpEndLong(newItem.getIpEndLong());
//						oldItem.setIpPoolEnd(newItem.getIpPoolEnd());
//						oldItem.setIpPoolStart(newItem.getIpPoolStart());
//						oldItem.setIpStartLong(newItem.getIpStartLong());
//						oldItem.setNetwork(newItem.getNetwork());
//						oldItem.setOwner(newItem.getOwner());
//						oldItem.setParentNetwork(newItem.getParentNetwork());
//						oldItem.setStatus(newItem.getStatus());
//						oldItem.setVersion(newItem.getVersion());
//						oldItem.setVipIpAddress(newItem.isVipIpAddress());
//						oldItem.setVpnNetwork(newItem.getVpnNetwork());

						find = true;
//						sourceKeepMap.put(oldItem.getId(), oldItem);
						break;
					}
				}
			}
			if(!find){
				rmIds.add(oldItem.getId());
			}
		}
		try{
			if(!rmIds.isEmpty()){
				QueryUtil.removeBos(SubNetworkResource.class, new FilterParams("id", rmIds));
			}
		}catch(Exception ex){
			log.error("remove SubNetworkResource fail:", ex);
		}
		
//		List<SubNetworkResource> resList = new ArrayList<SubNetworkResource>();
//		resList.addAll(sourceKeepMap.values());
//		if(newObj.getSubNetworkRes() != null){
//			for(SubNetworkResource subItem : newObj.getSubNetworkRes()){
//				if(subItem.getId() == null){
//					resList.add(subItem);
//				}
//			}
//		}
//		newObj.setSubNetworkRes(resList);
	}
	
	private static List<SubNetworkResource> getSubNetworkResourceList(VpnNetwork vpnNetwork){
		List<SubNetworkResource> resList = null;
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL
		        || vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			if(vpnNetwork.getSubItems() != null && !vpnNetwork.getSubItems().isEmpty()){
				resList = getSubBlocks(vpnNetwork);
			}
		}
		return resList;
	}
	
//	private static List<SubNetworkResource> getSubBlocks(String ipAndNetMask){
//		List<SubNetworkResource> resList = new ArrayList<SubNetworkResource>();
//		int flagIndex = ipAndNetMask.indexOf("/");
//		int mask = Integer.valueOf(ipAndNetMask.substring(flagIndex+1));
//		long allNum = (long)Math.pow(2, 32-mask);
//		for(String net : MgrUtil.splitNetworkToSubblocks(ipAndNetMask, 1)){
//			SubNetworkResource item = new SubNetworkResource();
//			item.setNetwork(net);
//			item.setParentNetwork(ipAndNetMask);
//			item.setFirstIp(getIpFromNetwork(net, 1));
//			item.setIpPoolStart(getIpFromNetwork(net, 2));
//			item.setIpPoolEnd(getIpFromNetwork(net, allNum-2));
//			item.setIpStartLong(AhEncoder.ip2Long(getIpFromNetwork(net, 0)));
//			item.setIpEndLong(AhEncoder.ip2Long(getIpFromNetwork(net, allNum))-1);
//			item.setVipIpAddress(false);
//			item.setStatus(SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE);
//			
//			resList.add(item);
//		}
//		return resList;
//	}
	
	private static List<SubNetworkResource> getSubBlocks(VpnNetwork newObj){
		List<SubNetworkResource> resList = new ArrayList<SubNetworkResource>();
		if (newObj == null || newObj.getSubItems() == null
				|| newObj.getSubItems().isEmpty()) {
			return resList;
		}
		for (VpnNetworkSub sub : newObj.getSubItems()) {
			String network = sub.getIpNetwork();
			int leftEnd = sub.getLeftEnd();
			int rightEnd = sub.getRightEnd();
			String localNetwork = sub.getLocalIpNetwork();
			byte defaultGateway = sub.getDefaultGateway();
			int getIpBranches = sub.getIpBranches();
			if(sub.isUniqueSubnetworkForEachBranches()) {
				List<String> localIpAddressList = null;
				if(sub.isEnableNat()) {
					localIpAddressList = MgrUtil.splitNetworkToSubblocks(localNetwork, getIpBranches);
				}
				int i = 0;
				for (String net : MgrUtil.splitNetworkToSubblocks(network,
						getIpBranches)) {
					int flagIndex = network.indexOf("/");
					int mask = Integer.valueOf(network.substring(flagIndex + 1));
					long allNum = (long) Math.pow(2, 32 - mask);
					long end = (allNum / getIpBranches) - rightEnd - 3;
					SubNetworkResource item = new SubNetworkResource();
					item.setNetwork(net);
					item.setParentNetwork(network);
					item.setFirstIp(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(net, 1) : getIpFromNetwork(net, allNum / getIpBranches - 2));
					item.setIpPoolStart(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(net, leftEnd + 2) : getIpFromNetwork(net, leftEnd + 1));
					item.setIpPoolEnd(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(net, end + 1) : getIpFromNetwork(net, end));
					item.setIpStartLong(AhEncoder.ip2Long(getIpFromNetwork(net, 0)));
					item.setIpEndLong(AhEncoder.ip2Long(getIpFromNetwork(net,
							allNum / getIpBranches)) - 1);
					item.setVipIpAddress(false);
					item.setStatus(SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE);
					item.setVpnNetwork(newObj);
					item.setEnableNat(false);
					item.setLocalNetwork(net);
					item.setParentLocalNetwork(network);
					
					if(sub.isEnableNat()) {
						String localNet = localIpAddressList.get(i);
						item.setEnableNat(true);
						item.setLocalNetwork(localNet);
						item.setParentLocalNetwork(localNetwork);
						item.setFirstIp(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNet, 1) : getIpFromNetwork(localNet, allNum / getIpBranches - 2));
						item.setIpPoolStart(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNet, leftEnd + 2) : getIpFromNetwork(localNet, leftEnd + 1));
						item.setIpPoolEnd(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNet, end + 1) : getIpFromNetwork(localNet, end));
						item.setIpStartLong(AhEncoder.ip2Long(getIpFromNetwork(localNet, 0)));
						item.setIpEndLong(AhEncoder.ip2Long(getIpFromNetwork(localNet,
								allNum / getIpBranches)) - 1);
					}

					resList.add(item);
					i++;
				}
			} else {
				int flagLocalNetIndex = localNetwork.indexOf("/");
				int localnetMask = Integer.valueOf(localNetwork.substring(flagLocalNetIndex + 1));
				List<String> natIpAddressList = MgrUtil.splitNetworkToSubblocks(network, getIpBranches);
				
				long allNum = (long) Math.pow(2, 32 - localnetMask);
				long end = allNum - rightEnd - 3;
				
				for(String net : natIpAddressList) {
					SubNetworkResource item = new SubNetworkResource();
					item.setNetwork(net);
					item.setParentNetwork(network);
					item.setFirstIp(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNetwork, 1) : getIpFromNetwork(localNetwork, allNum - 2));
					item.setIpPoolStart(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNetwork, leftEnd + 2) : getIpFromNetwork(localNetwork, leftEnd + 1));
					item.setIpPoolEnd(defaultGateway == VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP ? getIpFromNetwork(localNetwork, end + 1) : getIpFromNetwork(localNetwork, end));
					item.setIpStartLong(AhEncoder.ip2Long(getIpFromNetwork(net, 0)));
					item.setIpEndLong(AhEncoder.ip2Long(getIpFromNetwork(net, allNum)) - 1);
					item.setVipIpAddress(false);
					item.setVpnNetwork(newObj);
					
					item.setLocalNetwork(localNetwork);
					item.setParentLocalNetwork(localNetwork);
					item.setEnableNat(true);
					
					resList.add(item);
				}
			}
		}

		return resList;
	}
	
	private boolean validatePort(JSONObject jsonObject) throws Exception {
		// gained port
		List<String> list = new ArrayList<String>();
		if (protocol == PortForwarding.PROTOCOL_UDP) {
			list.addAll(udpPortList);
		} else if (protocol == PortForwarding.PROTOCOL_TCP) {
			list.addAll(tcpPortList);
		} else {
			list.addAll(udpPortList);
			list.addAll(tcpPortList);
		}
		List<String> portList = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			String portValue=list.get(i);
			if(portValue.indexOf("-")>=0){
				int startPort=Integer.parseInt(portValue.substring(0, portValue.indexOf("-")));
				int endPort=Integer.parseInt(portValue.substring(portValue.indexOf("-")+1));
				if(startPort>endPort){
					continue;
				}
				for(int j=startPort;j<=endPort;j++){
					portList.add(String.valueOf(j));
				}
			}else{
				portList.add(portValue);
			}
		}
		if (!portList.isEmpty()) {
			for (int i = 0; i < portList.size(); i++) {
				if (subDestinationPortNumber.equals(portList.get(i))) {
					jsonObject.put("errorMsg", addDestinationPortNumberError);
					return false;
				}
			}
		}
		// validate ipaddress list>=16 in one bransh
		if(!validateBranch(jsonObject)){
			return false;
		}
		List<String> destinationPortList = new ArrayList<String>();
		List<Integer> positionIdList = new ArrayList<Integer>();
		List<String> ipAddressDestinationPortList = new ArrayList<String>();
		Map<String, List<String>> positionIdDestinationPortMap = new HashMap<String, List<String>>();
		Map<String, List<Integer>> internalHostPortProtocolMap = new HashMap<String, List<Integer>>();
		if (getDataSource().getTmpPortForwardings().size() > 0) {
			for (PortForwarding portForward : getDataSource()
					.getTmpPortForwardings()) {
				// ipAddressList
				positionIdList.add(portForward.getPositionId());
				// destinationPortList
				destinationPortList.add(portForward.getDestinationPortNumber());
				// internalHostPortMap
				if (!positionIdDestinationPortMap.containsKey(portForward
						.getPositionId()
						+ ":"
						+ portForward.getInternalHostPortNumber())) {
					positionIdDestinationPortMap.put(
							portForward.getPositionId() + ":"
									+ portForward.getInternalHostPortNumber(),
							new ArrayList<String>());
				}
				positionIdDestinationPortMap.get(
						portForward.getPositionId() + ":"
								+ portForward.getInternalHostPortNumber()).add(
						portForward.getDestinationPortNumber());
				// internalHostPortProtocolMap
				if (!internalHostPortProtocolMap.containsKey(portForward
						.getDestinationPortNumber()
						+ ":"
						+ portForward.getPositionId()
						+ ":"
						+ portForward.getInternalHostPortNumber())) {
					internalHostPortProtocolMap.put(
							portForward.getDestinationPortNumber() + ":"
									+ portForward.getPositionId()
									+ ":"
									+ portForward.getInternalHostPortNumber(),
							new ArrayList<Integer>());
				}
				internalHostPortProtocolMap.get(
						portForward.getDestinationPortNumber() + ":"
								+ portForward.getPositionId() + ":"
								+ portForward.getInternalHostPortNumber()).add(
						portForward.getProtocol());
			}
			if (null == positionIdList || positionIdList.isEmpty()) {
				return true;
			}
			if (positionIdList.contains(positionId)) {
				ipAddressDestinationPortList = positionIdDestinationPortMap
						.get(positionId + ":"
								+ subInternalHostPortNumber);
				if (null != ipAddressDestinationPortList
						&& !ipAddressDestinationPortList.isEmpty()) {
					if (ipAddressDestinationPortList
							.contains(subDestinationPortNumber)) {
						destinationPortList
								.removeAll(ipAddressDestinationPortList);
					} else {
						jsonObject.put("errorMsg",
								destinationPortNumberNotSameError);
						return false;
					}
					if (!validateProtocol(internalHostPortProtocolMap)) {
						jsonObject.put("errorMsg", addProtocolError);
						return false;
					}
				}
			}
			if (destinationPortList.contains(subDestinationPortNumber)) {
				jsonObject.put("errorMsg", addDestinationPortNumberError);
				return false;
			}
		}
		return true;
	}
	
	public boolean validateBranch(JSONObject jsonObject) throws JSONException {
		boolean inBranchRange=true;
		List<PortForwarding> portList=new ArrayList<PortForwarding>();
		if(null!=getDataSource().getTmpPortForwardings()
				&& !getDataSource().getTmpPortForwardings().isEmpty()){
			for (PortForwarding port : getDataSource()
					.getTmpPortForwardings()) {
				portList.add(port);
				if(port.getProtocol() == PortForwarding.PROTOCOL_ANY){
					portList.add(port);
				}
			}
		}
		//save validate,portList size just equal to maximum number
		if(0==positionId){
			portList.remove(0);
		}
		if(portList.size()>=16){
			jsonObject.put("errorMsg", addIpAddressOurRangeError);
			inBranchRange=false;
		}
		return inBranchRange;
	}
	public boolean validateProtocol(
			Map<String, List<Integer>> internalHostPortProtocolMap) {
		List<Integer> protocolList = internalHostPortProtocolMap
				.get(subDestinationPortNumber + ":" + positionId
						+ ":" + subInternalHostPortNumber);
		if (null == protocolList || protocolList.isEmpty()) {
			return true;
		}
		if (protocolList.contains(protocol)
				|| protocolList.contains(PortForwarding.PROTOCOL_ANY)) {
			return false;
		}
		if (protocolList.contains(PortForwarding.PROTOCOL_TCP)
				&& protocolList.contains(PortForwarding.PROTOCOL_UDP)) {
			return false;
		}
		if ((protocolList.contains(PortForwarding.PROTOCOL_TCP) && protocol == PortForwarding.PROTOCOL_ANY)
				|| (protocolList.contains(PortForwarding.PROTOCOL_UDP) && protocol == PortForwarding.PROTOCOL_ANY)) {
			return false;
		}
		return true;
	}
	
	public void generalCurrentCvsFile(List<IpNatMap> ipNatMapList,FileWriter out,StringBuffer strOutput) throws Exception{
		if(null==ipNatMapList || ipNatMapList.isEmpty() || null==out){
			return;
		}
		strOutput = new StringBuffer();
		for(IpNatMap obj:ipNatMapList){
			//remove ip if it end with "0" or "255"
			String localIp=obj.getLocalIp();
			String localStr=localIp.substring(localIp.lastIndexOf(".")+1);
			if(localStr.equals("0") || localStr.equals("255")){
				continue;
			}
			strOutput.append(obj.getNatIp()).append(",");
			strOutput.append(obj.getLocalIp());
    		strOutput.append("\n");
		}
		out.write(strOutput.toString());
		out.flush();
	}
	private void exportForwardingData(String localNetwork,String natNetWork,
			     boolean isUniqueSubnetworkForEachBranches) throws Exception{
		if ("".equals(localNetwork) || null == localNetwork || "".equals(natNetWork)
				|| null == natNetWork) {
			return;
		}
		natNetWork=MgrUtil.getStartIpAddressValue(natNetWork);
		List<IpNatMap> ipNatMapList=new ArrayList<IpNatMap>();
		IpNatMap ipNatMap=null;
		//write data
		FileWriter out=null;
		try{
			String currentFileDir=AhPerformanceScheduleModule.fileDirPathCurrent + File.separator+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			File tmpFile = new File(currentFileDir+ File.separator + mailFileName);
			out = new FileWriter(tmpFile);
			StringBuffer strOutput = new StringBuffer();
			strOutput.append("NAT IP address,");
			strOutput.append("Local IP address");
			strOutput.append("\n");
			out.write(strOutput.toString());
			List<String> localNetWorkList=MgrUtil.splitNetworkToSubblocks(localNetWork,
					branchSize);
			List<String> natNetWorkList=MgrUtil.splitNetworkToSubblocks(natNetWork,
					branchSize);
			for(int i=0;i<localNetWorkList.size();i++){
				String localSubnetWork=localNetWorkList.get(i);
				String natSubnetWork=natNetWorkList.get(i);
				int flagNetIndex = localSubnetWork.indexOf("/");
				int netMask = Integer.valueOf(localSubnetWork.substring(flagNetIndex + 1));
				long allIpNum = (long) Math.pow(2, 32 - netMask);//all ip num
				long ipNum = AhEncoder.ip2Long(localSubnetWork.substring(0, localSubnetWork.indexOf("/")));
				long natIpNum=AhEncoder.ip2Long(natSubnetWork.substring(0, natSubnetWork.indexOf("/")));
				if(isUniqueSubnetworkForEachBranches){
					for(long j=1;j<allIpNum-1;j++){
					    ipNatMap=new IpNatMap();
					    long resIpNum = ipNum + j;
				    	long resNatIpNum=natIpNum+j;
				    	ipNatMap.setLocalIp(AhDecoder.long2Ip(resIpNum));//
				    	ipNatMap.setNatIp(AhDecoder.long2Ip(resNatIpNum));
				    	ipNatMapList.add(ipNatMap);
					}
				}else{
					 flagNetIndex = natSubnetWork.indexOf("/");
					 netMask = Integer.valueOf(natSubnetWork.substring(flagNetIndex + 1));
					 long allNatIpNum = (long) Math.pow(2, 32 - netMask);//all NatIp num
					 long index=1;
					 for(long k=1;k<allNatIpNum-1;k++){
						 ipNatMap=new IpNatMap();
						 long resIpNum = ipNum + index;
						 long resNatIpNum=natIpNum+k;
						 ipNatMap.setLocalIp(AhDecoder.long2Ip(resIpNum));
						 ipNatMap.setNatIp(AhDecoder.long2Ip(resNatIpNum));
						 ipNatMapList.add(ipNatMap);
		    			 if(index==allIpNum-2){
		    				 index=0;
		    				 k=k+2;
		    			 }
		    			 index++;
					 }
				}
				generalCurrentCvsFile(ipNatMapList,out,strOutput);
				ipNatMapList=new ArrayList<IpNatMap>();
			}
		}finally{
			if(null!=out){
				try{
					out.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
	}
	private String getPortListStr(){
		StringBuffer bf=new StringBuffer("");
		bf.append("[UDP]");
		bf.append("<table><tr>");
		int index=1;
		for(int i=0;i<udpPortList.size();i++){
			bf.append("<td width=\"120px\">"+udpPortList.get(i)+"</td>");
			if(index%4==0){
				bf.append("</tr>");
			}
			index++;
		}
		if(index%4!=0){
			bf.append("</tr>");
		}
		bf.append("</table>");
		bf.append("[TCP]");
		bf.append("<table><tr>");
		index=1;
		for(int i=0;i<tcpPortList.size();i++){
			bf.append("<td width=\"120px\">"+tcpPortList.get(i)+"</td>");
			if(index%4==0){
				bf.append("</tr>");
			}
			index++;
		}
		if(index%4!=0){
			bf.append("</tr>");
		}
		bf.append("</table>");
		return bf.toString();
	}
	
	private List<String> getPortList(String fileName){
		List<String> list=new ArrayList<String>();
		List<String> StrList=new ArrayList<String>();
		String path = AhDirTools.getHmRoot() + "resources" + File.separator + fileName;
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(path);
			properties.load(in);
		} catch (Exception e) {
			return list;
		}
		for(Object obj:properties.keySet().toArray()){
			String portValue=properties.get(obj).toString().trim();
		    if(StringUtils.isBlank(portValue)){
		    	continue;
		    }
		    if(portValue.indexOf("-")>=0){
		    	StrList.add(portValue);
		    }else{
		    	list.add(portValue);
		    }
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return Integer.parseInt(o1)-Integer.parseInt(o2);
			   }
			});
		list.addAll(StrList);
		return list;
	}
	
	private void showResultByBranchId(){
		String branchNetwork=MgrUtil.splitNetworkToSubblocks(localNetWork,
				branchSize).get(branchId-1);
		long ipNum = AhEncoder.ip2Long(branchNetwork.substring(0, branchNetwork.indexOf("/")))+1;
		branchResult=new ArrayList<CheckItem3>();
		long allPosition=Long.parseLong(positionRange);
		for(long i=1;i<=allPosition;i++){
			long index=ipNum+i;
			if(!firstIpIsGateWay){
				index=index-1;
			}
			CheckItem3 item=new CheckItem3(String.valueOf(i),AhDecoder.long2Ip(index));
			branchResult.add(item);
		}
	}
	private void showResultByPositionId(){
		List<String> branchNetWorkList=MgrUtil.splitNetworkToSubblocks(localNetWork,
				branchSize);
		if(!firstIpIsGateWay){
			positionId=positionId-1;
		}
		branchResult=new ArrayList<CheckItem3>();
		int index=1;
		CheckItem3 item;
		for(String network:branchNetWorkList){
			long ipNum = AhEncoder.ip2Long(network.substring(0, network.indexOf("/")))+1;
			if(uniqueSubnetwork){
				item=new CheckItem3(index+"-"+network,AhDecoder.long2Ip((ipNum+positionId)));
			}else{
				item=new CheckItem3(network,AhDecoder.long2Ip((ipNum+positionId)));
			}
			branchResult.add(item);
			index++;
		}
	}
	private static String getIpFromNetwork(String network, long index){
		int flagIndex = network.indexOf("/");
		String ipAddr = network.substring(0, flagIndex);
//		String mask = network.substring(flagIndex+1);
		long ipNum = AhEncoder.ip2Long(ipAddr);
		long resIpNum = ipNum + index;
		return AhDecoder.long2Ip(resIpNum);
	}
	
// end add for Network IP resource count (zhang)
	
	
	
	public EnumItem[] getEnumCustomType() {
		return DhcpServerOptionsCustom.ENUM_CUSTOM_TYYPE;
	}
	
	public EnumItem[] getEnumProtocol() {
		return PortForwarding.ENUM_PROTOCOL;
	}
	
	public String getHideDhcpDiv(){
		if (getDataSource().isEnableDhcp()) {
			return "";
		}
		return "none";
	}
	
	public String getHideSubnetworkDiv(){
		if (getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_INTERNAL
		        || getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return "";
		}
		return "none";
	}
	
	public String getHideIPAddressSpaceDiv(){
		if (getDataSource().getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST){
			return "";
		}
		return "none";
	}
	
	public String getHideEmptyMainSubnetTR(){
		if (!getDataSource().getSubItems().isEmpty()){
			return "none";
		} 
		return "";
	}
	
	private void setCustomNewOrApplyButton() {
		if (null != getDataSource().getCustomOptions()
				&& !getDataSource().getCustomOptions().isEmpty()) {
			hideCustomCreateItem = "none";
			hideCustomNewButton = "";
		} else {
			hideCustomCreateItem = "";
			hideCustomNewButton = "none";
		}
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public String getHideOverrideDNSService(){
		if (overrideDNSService) {
			return "";
		}
		return "none";
	}
	
//	private List<CheckItem> list_vlan;
	private List<CheckItem> list_dnsService;
	private Long dnsServiceId;
//	private Long vlanId;
//	private String inputVlanIdValue;
	
	
	private String hideCustomCreateItem = "";
	private String hideCustomNewButton = "none";
	private short customNumber;
	private short customType = DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
	private String integerValue;
	private String ipValue;
	private String strValue;
	private String hexValue;
	private Collection<String> customIndices;
	
	private Collection<String> ruleIndices;
	
	private int ruleOneIndex;
	
	// for subnet
	private int subKey;
	private String subNetwork;
	private int subBranch;
	
	private boolean subEnableNetClass;
	//
	private int subnetClassType;
	private Long subnetLocationId;
	private String subnetDeviceName;
	//
	private int subnetClassTag;
	private String subnetClassValue;
	private String subnetClassIndices;
	private String PortForwardingIndices;
	
	private boolean subEnableIpReserveClass;
	private String ipReserveClassIp;
    private int ipReserveClassType;
    private Long locationId;
    private String deviceName;
    private int ipReserveClassTag;
    private String ipReserveClassValue;
    private String ipReserveClassIndices;
    //fix bug 26018
    private String saveOrCancelSubnet;
    
	private boolean subUniqueSubnetworkForEachBranches;
	private byte subDefaultGateway;

	/*-----------DANT--------*/
	private boolean subEnableNat;
	private String subNatIpNetwork;

	/*----PORT FORWARDING----*/
	private boolean subEnablePortForwarding;
	private String subDestinationPortNumber;
	//private String subInternalHostIPAddress;
	private String subInternalHostPortNumber;
	private int    protocol;
	private int    positionId;
    private String localNetWork;
    private String natNetWork; 
    private boolean uniqueSubnetwork;
    private boolean enableNat;
    private String  positionRange;
    private int branchId;
    private int branchSize;
    private int showType;
    private List<CheckItem3> branchResult;
    private boolean firstIpIsGateWay;
	/*-----------DHCP--------*/
	private boolean subEnableDhcp;
	private int subLeftEnd;
	private int subRightEnd;
	private int subLeaseTime;
	private String subNtpServerIp;
	private String subDomainName;
	private String subCustomIndices;
	private boolean enableArpCheck;
	/*-----------Subnetwork Custom--------*/
	private short subCustomNumber;
	private short subCustomType;
	private String subCustomValue;
	
	/*-----------------------*/
	// selected LAN id(use for Networks Dialog)
	private Long selectedLANId;
	/*----------For list page-------------*/
	private long selectedNetworkId;
	private String selectedSubItem;
	
	public EnumItem[] getTagList(){
		return new EnumItem[]{new EnumItem(1,"Tag1"), new EnumItem(2,"Tag2"),new EnumItem(3,"Tag3")};
	}

	/**
	 * Don't display the network type list when the object which is set as 'Management' Type is used by other profiles. 
	 * 
	 * @author Yunzhi Lin
	 * - Time: Apr 28, 2012 3:02:54 PM
	 * @return <b>True</b> or <b>False</b>
	 */
	public boolean isDisplayNetworkType() {
	    if(null == getDataSource().getId()) {
	        return !isBlnMgtNetwork();
	    } else {
	        if(getDataSource().isReferenced() 
	                && getDataSource().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
	            return false;
	        }
	    }
	    return !isBlnMgtNetwork();
	}
	
	/**
	 * Should not allow to change the 'Management' type when the 
	 * 
	 * @author Yunzhi Lin
	 * - Time: Apr 28, 2012 3:07:08 PM
	 * @return -
	 */
	public EnumItem[] getNetworkType() {
	    if(null != getDataSource().getId() && getDataSource().isReferenced()) {
	        return new EnumItem[] {
	                new EnumItem(VpnNetwork.VPN_NETWORK_TYPE_INTERNAL, MgrUtil.getUserMessage("config.vpn.network.internalUse")),
	                new EnumItem(VpnNetwork.VPN_NETWORK_TYPE_GUEST, MgrUtil.getUserMessage("config.vpn.network.guestUse"))};
	    } else {
	        return new EnumItem[] {
	                new EnumItem(VpnNetwork.VPN_NETWORK_TYPE_INTERNAL, MgrUtil.getUserMessage("config.vpn.network.internalUse")),
	                new EnumItem(VpnNetwork.VPN_NETWORK_TYPE_GUEST, MgrUtil.getUserMessage("config.vpn.network.guestUse")),
	                new EnumItem(VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT, MgrUtil.getUserMessage("config.vpn.network.management"))};
	    }
	}
	
	public int getSubKey() {
		return subKey;
	}

	public void setSubKey(int subKey) {
		this.subKey = subKey;
	}

	public String getSubNetwork() {
		return subNetwork;
	}

	public void setSubNetwork(String subNetwork) {
		this.subNetwork = subNetwork;
	}

	public int getSubLeftEnd() {
		return subLeftEnd;
	}

	public void setSubLeftEnd(int subLeftEnd) {
		this.subLeftEnd = subLeftEnd;
	}

	public int getSubRightEnd() {
		return subRightEnd;
	}

	public void setSubRightEnd(int subRightEnd) {
		this.subRightEnd = subRightEnd;
	}

	public boolean isSubEnableNetClass() {
		return subEnableNetClass;
	}

	public void setSubEnableNetClass(boolean subEnableNetClass) {
		this.subEnableNetClass = subEnableNetClass;
	}

	public int getSubnetClassTag() {
		return subnetClassTag;
	}

	public void setSubnetClassTag(int subnetClassTag) {
		this.subnetClassTag = subnetClassTag;
	}

	public String getSubnetClassValue() {
		return subnetClassValue;
	}

	public void setSubnetClassValue(String subnetClassValue) {
		this.subnetClassValue = subnetClassValue;
	}

	public boolean isSubEnableIpReserveClass() {
		return subEnableIpReserveClass;
	}

	public void setSubEnableIpReserveClass(boolean subEnableIpReserveClass) {
		this.subEnableIpReserveClass = subEnableIpReserveClass;
	}

	public String getIpReserveClassIp() {
		return ipReserveClassIp;
	}

	public void setIpReserveClassIp(String ipReserveClassIp) {
		this.ipReserveClassIp = ipReserveClassIp;
	}

	public int getIpReserveClassTag() {
		return ipReserveClassTag;
	}

	public void setIpReserveClassTag(int ipReserveClassTag) {
		this.ipReserveClassTag = ipReserveClassTag;
	}

	public String getIpReserveClassValue() {
		return ipReserveClassValue;
	}

	public void setIpReserveClassValue(String ipReserveClassValue) {
		this.ipReserveClassValue = ipReserveClassValue;
	}

	public void setSubnetClassIndices(String subnetClassIndices) {
		this.subnetClassIndices = subnetClassIndices;
	}

	public void setIpReserveClassIndices(String ipReserveClassIndices) {
		this.ipReserveClassIndices = ipReserveClassIndices;
	}
	
	public String getSaveOrCancelSubnet() {
		return saveOrCancelSubnet;
	}

	public void setSaveOrCancelSubnet(String saveOrCancelSubnet) {
		this.saveOrCancelSubnet = saveOrCancelSubnet;
	}

	public int getIpReserveClassType() {
		return ipReserveClassType;
	}
	
	public void setIpReserveClassType(int ipReserveClassType) {
		this.ipReserveClassType = ipReserveClassType;
	}
	
	public Long getSelectedLANId() {
		return selectedLANId;
	}

	public void setSelectedLANId(Long selectedLANId) {
		this.selectedLANId = selectedLANId;
	}

	/*
	 * For Custom Options
	 */
	public int getCustomGridCount() {
		if (null == getDataSource().getCustomOptions()) {
			return 3;
		}
		return getDataSource().getCustomOptions().isEmpty() ? 3 : 0;
	}

	public String getDisplayCustomInt() {
		return DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER == customType ? "" : "none";
	}

	public String getDisplayCustomIp() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_IP == customType ? "" : "none";
	}

	public String getDisplayCustomStr() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING == customType ? "" : "none";
	}

	public String getDisplayCustomHex() {
		return DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX == customType ? "" : "none";
	}
	
	public String getHideCustomCreateItem() {
		return hideCustomCreateItem;
	}

	public void setHideCustomCreateItem(String hideCustomCreateItem) {
		this.hideCustomCreateItem = hideCustomCreateItem;
	}

	public String getHideCustomNewButton() {
		return hideCustomNewButton;
	}

	public void setHideCustomNewButton(String hideCustomNewButton) {
		this.hideCustomNewButton = hideCustomNewButton;
	}

	public short getCustomNumber() {
		return customNumber;
	}

	public String getHexValue()
	{
		return hexValue;
	}

	public void setHexValue(String hexValue)
	{
		this.hexValue = hexValue;
	}

	public void setCustomNumber(short customNumber) {
		this.customNumber = customNumber;
	}

	public short getCustomType() {
		return customType;
	}

	public void setCustomType(short customType) {
		this.customType = customType;
	}

	public String getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(String integerValue) {
		this.integerValue = integerValue;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public void setCustomIndices(Collection<String> customIndices) {
		this.customIndices = customIndices;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null != bo) {
			if (bo instanceof VpnNetwork) {
				VpnNetwork network = (VpnNetwork) bo;
//				if(network.getVlan()!=null) network.getVlan().getId();
				if(network.getCustomOptions()!=null) network.getCustomOptions().size();
				if(network.getReserveClass()!=null) network.getReserveClass().size();
				if(network.getSubNetwokClass()!=null) network.getSubNetwokClass().size();
				if(network.getVpnDnsService()!=null) network.getVpnDnsService().getId();
				if(network.getSubItems()!=null) network.getSubItems().size();
				if(network.getSubNetworkRes()!=null) network.getSubNetworkRes().size();
				if(network.getSubnetworkDHCPCustoms()!=null) network.getSubnetworkDHCPCustoms().size();
				if(network.getOwner() != null) network.getOwner().getId();
				if (network.getPortForwardings() != null)
					network.getPortForwardings().size();
				
				if(network.getSubItems() != null){
					for(VpnNetworkSub subnet : network.getSubItems()){
						if(subnet.getDnsService() != null){
							subnet.getDnsService().getId();
						}
					}
				}
			}
		}
		return null;
	}
	
	/*---------------------------*/
	public EnumItem[] getEnumWebSecurity() {
	    return VpnNetwork.ENUM_VPN_NETWORK_WEBSECURITY;
	}
	
	public EnumItem[] getIpReserveTypes() {
		return SingleTableItem.ENUM_ADDRESS_RESERVE_TYPE;
	}
	
	public List<CheckItem> getMapLocation() {
		List<CheckItem> listLocation = getMapListView();
		if (listLocation.isEmpty()) {
			listLocation.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return listLocation;
	}
	/*---------------------------*/

	public Long getDnsServiceId() {
		return dnsServiceId;
	}

	public void setDnsServiceId(Long dnsServiceId) {
		this.dnsServiceId = dnsServiceId;
	}

//	public Long getVlanId() {
//		return vlanId;
//	}
//
//	public void setVlanId(Long vlanId) {
//		this.vlanId = vlanId;
//	}
//
//	public String getInputVlanIdValue() {
//		return inputVlanIdValue;
//	}
//
//	public void setInputVlanIdValue(String inputVlanIdValue) {
//		this.inputVlanIdValue = inputVlanIdValue;
//	}
//
//	public List<CheckItem> getList_vlan() {
//		return list_vlan;
//	}

	public List<CheckItem> getList_dnsService() {
		return list_dnsService;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public int getRuleOneIndex() {
		return ruleOneIndex;
	}

	public void setRuleOneIndex(int ruleOneIndex) {
		this.ruleOneIndex = ruleOneIndex;
	}

	public int getSubBranch() {
		return subBranch;
	}

	public void setSubBranch(int subBranch) {
		this.subBranch = subBranch;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	private boolean blnMgtNetwork = false;
	
	public boolean isBlnMgtNetwork() {
		return blnMgtNetwork;
	}

	public void setBlnMgtNetwork(boolean blnMgtNetwork) {
		this.blnMgtNetwork = blnMgtNetwork;
	}

	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
		getDataSource().setBlnMgtNetwork(isBlnMgtNetwork());
	}
	
	private void restoreJsonContext() {
		setParentDomID(getDataSource().getParentDomID());
		setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
		setBlnMgtNetwork(getDataSource().isBlnMgtNetwork());
	}
	
	private boolean isAMgtNetworkProperItem(Long nwId) {
		VpnNetwork vpnnetwork = QueryUtil.findBoById(VpnNetwork.class, nwId, this);
		if (vpnnetwork.getNetworkType()!=VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
			return false;
		}
		if (vpnnetwork.getVpnDnsService()==null) {
			return false;
		}
		if (vpnnetwork.getSubItems() == null || vpnnetwork.getSubItems().isEmpty()){
			return false;
		}
		for (VpnNetworkSub vpnSub: vpnnetwork.getSubItems()) {
			if (!vpnSub.isEnableDhcp()) {
				return false;
			}
			if (vpnSub.getIpBranches()==1) {
				return false;
			}
		}
//		String mgtQueryStr="select distinct a.id, a.networkname  from vpn_network a, vpn_network_subitem b " +
//			"where a.networktype =1 and a.vpn_dns_id is not null and a.enableDhcp = true " +
//			"and a.id=b.vpn_network_sub_id and a.owner =" + getDomainId() +  " and a.id = " + nwId;
//		List<?> mgtNetworkLst= QueryUtil.executeNativeQuery(mgtQueryStr);
//		if (mgtNetworkLst != null && mgtNetworkLst.size() > 0) {
//			return true;
//		}
		return true;
	}

	public boolean isSubEnableDhcp() {
		return subEnableDhcp;
	}

	public void setSubEnableDhcp(boolean subEnableDhcp) {
		this.subEnableDhcp = subEnableDhcp;
	}

	public int getSubLeaseTime() {
		return subLeaseTime;
	}

	public void setSubLeaseTime(int subLeaseTime) {
		this.subLeaseTime = subLeaseTime;
	}

	public String getSubNtpServerIp() {
		return subNtpServerIp;
	}

	public void setSubNtpServerIp(String subNtpServerIp) {
		this.subNtpServerIp = subNtpServerIp;
	}

	public String getSubDomainName() {
		return subDomainName;
	}

	public void setSubDomainName(String subDomainName) {
		this.subDomainName = subDomainName;
	}

	public short getSubCustomNumber() {
		return subCustomNumber;
	}

	public void setSubCustomNumber(short subCustomNumber) {
		this.subCustomNumber = subCustomNumber;
	}

	public short getSubCustomType() {
		return subCustomType;
	}

	public void setSubCustomType(short subCustomType) {
		this.subCustomType = subCustomType;
	}

	public String getSubCustomValue() {
		return subCustomValue;
	}

	public void setSubCustomValue(String subCustomValue) {
		this.subCustomValue = subCustomValue;
	}

	public String getSubCustomIndices() {
		return subCustomIndices;
	}

	public void setSubCustomIndices(String subCustomIndices) {
		this.subCustomIndices = subCustomIndices;
	}

	public long getSelectedNetworkId() {
		return selectedNetworkId;
	}

	public void setSelectedNetworkId(long selectedNetworkId) {
		this.selectedNetworkId = selectedNetworkId;
	}

	public String getSelectedSubItem() {
		return selectedSubItem;
	}

	public void setSelectedSubItem(String selectedSubItem) {
		this.selectedSubItem = selectedSubItem;
	}

    public int getSubnetClassType() {
        return subnetClassType;
    }

    public void setSubnetClassType(int subnetClassType) {
        this.subnetClassType = subnetClassType;
    }

    public Long getSubnetLocationId() {
        return subnetLocationId;
    }

    public void setSubnetLocationId(Long subnetLocationId) {
        this.subnetLocationId = subnetLocationId;
    }

    public String getSubnetDeviceName() {
        return subnetDeviceName;
    }

    public void setSubnetDeviceName(String subnetDeviceName) {
        this.subnetDeviceName = subnetDeviceName;
    }

    public String getPortForwardingIndices() {
		return PortForwardingIndices;
	}

	public void setPortForwardingIndices(String portForwardingIndices) {
		PortForwardingIndices = portForwardingIndices;
	}

	public boolean isSubUniqueSubnetworkForEachBranches() {
		return subUniqueSubnetworkForEachBranches;
	}

	public void setSubUniqueSubnetworkForEachBranches(
			boolean subUniqueSubnetworkForEachBranches) {
		this.subUniqueSubnetworkForEachBranches = subUniqueSubnetworkForEachBranches;
	}

	public byte getSubDefaultGateway() {
		return subDefaultGateway;
	}

	public void setSubDefaultGateway(byte subDefaultGateway) {
		this.subDefaultGateway = subDefaultGateway;
	}

	public boolean isSubEnableNat() {
		return subEnableNat;
	}

	public void setSubEnableNat(boolean subEnableNat) {
		this.subEnableNat = subEnableNat;
	}
	
	public String getPositionRange() {
		return positionRange;
	}

	public void setPositionRange(String positionRange) {
		this.positionRange = positionRange;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getBranchSize() {
		return branchSize;
	}
	public void setBranchSize(int branchSize) {
		this.branchSize = branchSize;
	}
	
	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public List<CheckItem> getBranchList() {
		List<CheckItem> branchList = new ArrayList<CheckItem>();
		List<String> branchNetwork=MgrUtil.splitNetworkToSubblocks(localNetWork,
					branchSize);
		CheckItem item;
		for(long i=1;i<=branchSize;i++){
			int index=(int)i;
			if(uniqueSubnetwork){
				item=new CheckItem(i,index+"-"+branchNetwork.get(index-1));
			}else{
				item=new CheckItem(i,branchNetwork.get(index-1));
			}
			branchList.add(item);
		}
		return branchList;
	}
  public List<CheckItem> getPositionList(){
	  List<CheckItem> positionList = new ArrayList<CheckItem>();
	  for(int i=1;i<=Integer.parseInt(positionRange);i++){
		  CheckItem item=new CheckItem((long)i,String.valueOf(i));
		  positionList.add(item);
	  }
	  return positionList;
  }
	public List<CheckItem3> getBranchResult() {
		return branchResult;
	}

	public void setBranchResult(List<CheckItem3> branchResult) {
		this.branchResult = branchResult;
	}

	public List<EnumItem> getSelPositions() {
		 return PortForwarding.ENUM_POSITION();
	}
	public String getSubNatIpNetwork() {
		return subNatIpNetwork;
	}

	public boolean isFirstIpIsGateWay() {
		return firstIpIsGateWay;
	}

	public void setFirstIpIsGateWay(boolean firstIpIsGateWay) {
		this.firstIpIsGateWay = firstIpIsGateWay;
	}

	public void setSubNatIpNetwork(String subNatIpNetwork) {
		this.subNatIpNetwork = subNatIpNetwork;
	}

	public boolean isSubEnablePortForwarding() {
		return subEnablePortForwarding;
	}

	public void setSubEnablePortForwarding(boolean subEnablePortForwarding) {
		this.subEnablePortForwarding = subEnablePortForwarding;
	}

	public String getSubDestinationPortNumber() {
		return subDestinationPortNumber;
	}

	public void setSubDestinationPortNumber(String subDestinationPortNumber) {
		this.subDestinationPortNumber = subDestinationPortNumber;
	}

//	public String getSubInternalHostIPAddress() {
//		return subInternalHostIPAddress;
//	}
//
//	public void setSubInternalHostIPAddress(String subInternalHostIPAddress) {
//		this.subInternalHostIPAddress = subInternalHostIPAddress;
//	}

	public String getSubInternalHostPortNumber() {
		return subInternalHostPortNumber;
	}

	public void setSubInternalHostPortNumber(String subInternalHostPortNumber) {
		this.subInternalHostPortNumber = subInternalHostPortNumber;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public String getLocalNetWork() {
		localNetWork=MgrUtil.getStartIpAddressValue(localNetWork);//If network cannot be 2 division, modify the value
		return localNetWork;
	}

	public void setLocalNetWork(String localNetWork) {
		this.localNetWork = localNetWork;
	}

	public String getNatNetWork() {
		return natNetWork;
	}

	public void setNatNetWork(String natNetWork) {
		this.natNetWork = natNetWork;
	}

	public boolean isUniqueSubnetwork() {
		return uniqueSubnetwork;
	}

	public void setUniqueSubnetwork(boolean uniqueSubnetwork) {
		this.uniqueSubnetwork = uniqueSubnetwork;
	}

	public boolean isEnableNat() {
		return enableNat;
	}

	public void setEnableNat(boolean enableNat) {
		this.enableNat = enableNat;
	}

	public static String getMailfilename() {
		return mailFileName;
	}

	public Collection<String> getCustomIndices() {
		return customIndices;
	}

	public Collection<String> getRuleIndices() {
		return ruleIndices;
	}

	public String getSubnetClassIndices() {
		return subnetClassIndices;
	}

	public String getIpReserveClassIndices() {
		return ipReserveClassIndices;
	}

	public void setList_dnsService(List<CheckItem> list_dnsService) {
		this.list_dnsService = list_dnsService;
	}

	private List<String> getIgnoreTables() {
        List<String> ignoreTables = new ArrayList<String>();
        ignoreTables.add("vpn_network_subitem");
        ignoreTables.add("vpn_network_subnetclass");
        ignoreTables.add("vpn_network_ip_reserve_item");
        ignoreTables.add("vpn_network_custom");
        ignoreTables.add("vpn_network_subnet_customs");
        ignoreTables.add("sub_network_resource");
        return ignoreTables;
    }
	public String getLocalFileName() {
		return mailFileName;
	}
	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}
    private boolean isUsedByReferencedTables(long id, long domainId) {
        try {
            Map<String, String> tableMap = AhAppContainer.getBeDbModule().getTableReferences("vpn_network");
            long start = System.currentTimeMillis();
            if (null == tableMap) {
                return false;
            }
            List<String> ignoreTalbes = getIgnoreTables();
            for (String tableName : tableMap.keySet()) {
                if(ignoreTalbes.contains(tableName)) {
                    continue;
                }
                final String columnName = tableMap.get(tableName);
                // TODO How to find out the 'owner' column exist or not?
                List<?> list = QueryUtil.executeNativeQuery("select " + columnName + " from "
                        + tableName + " where " + columnName + " = " + id, 1);
                if (!list.isEmpty()) {
                    log.warn("The table '"+tableName+"' field - "+columnName+" is using the current Network Id="+id);
                    long end = System.currentTimeMillis();
                    log.debug("isUsedByReferencedTables", "cost "+(end - start)+" milli seconds.");
                    return true;
                }
            }
            long end = System.currentTimeMillis();
            log.debug("isUsedByReferencedTables", "cost "+(end - start)+" milli seconds.");
        } catch (Exception e) {
            log.error("Error to get the referenced tables.", e);
        }
        return false;
    }
    
    /*
     * The beginning of fixing bug 26018
     * Date:2013.5.8 Author:She
     * 
     */
    public void removeSingleTableItemInCache(String operTag) throws Exception{
    	if(operTag.equals("create")){
			getDataSource().getSubNetwokClass().clear();
			getDataSource().getReserveClass().clear();
    	}
    	if(operTag.equals("clone")){
    		return;
    	}
    	if(operTag.equals("update")){
    		Long id = this.getDataSource().getId();
    		VpnNetwork oneVpnNetwork = findBoById(VpnNetwork.class, id, this);
    		List<SingleTableItem> subNetworkClassInDB = new ArrayList<SingleTableItem>();
        	List<SingleTableItem> reserveClassInDB = new ArrayList<SingleTableItem>();
        	subNetworkClassInDB = oneVpnNetwork.getSubNetwokClass();
        	reserveClassInDB = oneVpnNetwork.getReserveClass();
    		getDataSource().getSubNetwokClass().clear();
    		getDataSource().getReserveClass().clear();
    		getDataSource().setSubNetwokClass(subNetworkClassInDB);
    		getDataSource().setReserveClass(reserveClassInDB);
    	}
    }

    /*
     * The end of fixing bug 26018
     */
    
	public boolean isOverrideDNSService() {
		return overrideDNSService;
	}

	public void setOverrideDNSService(boolean overrideDNSService) {
		this.overrideDNSService = overrideDNSService;
	}

	public Long getOverrideDNSServiceId() {
		return overrideDNSServiceId;
	}

	public void setOverrideDNSServiceId(Long overrideDNSServiceId) {
		this.overrideDNSServiceId = overrideDNSServiceId;
	}

	public boolean isOverrideDNS() {
		return overrideDNS;
	}

	public void setOverrideDNS(boolean overrideDNS) {
		this.overrideDNS = overrideDNS;
	}

	public boolean isEnableArpCheck() {
		return enableArpCheck;
	}

	public void setEnableArpCheck(boolean enableArpCheck) {
		this.enableArpCheck = enableArpCheck;
	}
}