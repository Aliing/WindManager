package com.ah.ui.actions.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.app.HmBePerformUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.BonjourGatewayMonitoring;
import com.ah.bo.network.BonjourRealm;
import com.ah.bo.network.BonjourServiceDetail;
import com.ah.bo.network.VlanGroup;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class BonjourGatewayMonitoringAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 3114352986079101184L;
	private static final Tracer log = new Tracer(BonjourGatewayMonitoringAction.class
			.getSimpleName());
	
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			updateTime = getCurrentTime();
			
			if("clickRealm".equals(operation)){
				List<Long> selectedBddDevices = null;
				if(selectedRealms != null){
					 selectedBddDevices = getAllBddDevices(selectedRealms.get(0));
				}
				prepareBonjourGateway(selectedBddDevices);
				return SUCCESS;
			} else if("clickBddDevice".equals(operation)){
				prepareBonjourGateway(getSelectedBddDevices());
				return SUCCESS;
			} else if ("editNetworkPolicy".equals(operation)) {
				long bgid = getBgId();
				BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoById(BonjourGatewayMonitoring.class, bgid);
				if(bonjourGatewayMonitoring != null){
					List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("macAddress", bonjourGatewayMonitoring.getMacAddress()), domainId,this);
					if(hiveAps != null && hiveAps.size() != 0){
						setNetworkId(hiveAps.get(0).getConfigTemplate().getId());
					}
				}
				return "configGuide2";
			} else if ("refresh".equals(operation)) {
				jsonObject = new JSONObject();
				int result;
				try {
					if("".equals(selectedRealmIds)){
						result = 3;
					} else {
						Collection<SimpleHiveAp> apList = null;
						if("".equals(selectedBddIds)){
							List<Long> bddIDs = getAllBddDevices(selectedRealmIds);
							apList = getHiveAPList(bddIDs);
							setSelectedBddMacs(null);
						} else {
							String[] ids = selectedBddIds.split(",");
							List<Long> bddIDs = new ArrayList<Long>(ids.length);
							for (String str_id : ids) {
								bddIDs.add(Long.parseLong(str_id));
							}
							apList = getHiveAPList(bddIDs);
						}
						
						result = HmBePerformUtil.syncRequestBonjourGateway(apList);
					}
				}catch (Exception e) {
					log.error("execute",
							"refreshClient operation catch exception", e);
					result = 1;
				}
				
				jsonObject.put("success", result);
				jsonObject.put("selectedBddMacs", getSelectedBddMacs());
				return "json";
			
			} else if ("view".equals(operation)) {
				List<Long> selelctedBddIds = new ArrayList<Long>();
				String[] macs = selectedBddMacs.split(",");
				for(int i=0;i<macs.length;i++){
					if("-1".equals(macs[i]) || "-2".equals(macs[i])){
						selelctedBddIds.add(Long.valueOf(macs[i]));
						continue;
					}
					BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class, "macAddress", macs[i], domainId);
					if(bonjourGatewayMonitoring != null){
						selelctedBddIds.add(bonjourGatewayMonitoring.getId());
					}
				}
				
				if(selectedRealms == null){
					selectedRealms = new ArrayList<String>();
				} 
				
				if(getAvailableRealms().size() > 0){
					if(selectedRealms.size() ==0){
						selectedRealms.add(getAvailableRealms().get(0).getId());
					} else {
						boolean realmExisted = false;
						for(CheckItem3 item :getAvailableRealms()){
							if(item.getId().equals(selectedRealms.get(0))){
								realmExisted = true;
								break;
							}
						}
						// if realm not existed,select first realm and select all bdd of first realm.
						if(!realmExisted){
							selectedRealms.set(0, getAvailableRealms().get(0).getId());
							selelctedBddIds=getAllBddDevices(selectedRealms.get(0)); 
						}
					}
				}
				
				prepareBonjourGateway(selelctedBddIds);
				
				//fix bug 23401
				removeNoneAvailable(); 
				
				return SUCCESS;
			} else if("modifyRealmName".equals(operation)){
				jsonObject = new JSONObject();
				BonjourRealm bRealm = QueryUtil.findBoByAttribute(BonjourRealm.class, "realmName", getRealmName(), domainId);
				MapContainerNode node = QueryUtil.findBoByAttribute(MapContainerNode.class, "mapName", getRealmName(),domainId);
				BonjourGatewayMonitoring monitor = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class, "realmId", getRealmName(),domainId);
				if(bRealm != null || monitor != null){
					jsonObject.put("resultStatus", false);
					jsonObject.put("resultMsg", "Realm Name already exists.");
					return "json";
				}
				
				if(node != null){
					jsonObject.put("resultStatus", false);
					jsonObject.put("resultMsg", "Realm Name already exists in topology map name.");
					return "json";
				}
				
				BonjourRealm realm = QueryUtil.findBoByAttribute(BonjourRealm.class, "realmId", getRealmId(), domainId);
				if(realm == null){
					realm = new BonjourRealm();
					realm.setRealmId(realmId);
					realm.setRealmName(realmName);
					realm.setOwner(getDomain());
					QueryUtil.createBo(realm);
				} else {
					realm.setRealmName(realmName);
					QueryUtil.updateBo(realm);
				}
				jsonObject.put("realmName", realmName);
				jsonObject.put("resultStatus", true);
				return "json";
			} else if(OPERATION_SORT.equals(operation)){
				setAvailableBddDevices(prepareBddDevices(getSelectedRealms()));
				ascending = ascending ? false : true;
				setBonjourServiceDetails(prepareServices(getSelectedBddDevices(),orderBy,ascending,chkShared));
				return SUCCESS;
			} else if("checkShared".equals(operation)){
				setAvailableBddDevices(prepareBddDevices(getSelectedRealms()));
				initSortParams();
				setBonjourServiceDetails(prepareServices(getSelectedBddDevices(),orderBy,ascending,chkShared));
				return SUCCESS;
			} else {
			
				if(selectedRealms == null){
					selectedRealms = new ArrayList<String>();
				} 
				
				if(getAvailableRealms().size() > 0){
					selectedRealms.add(0,getAvailableRealms().get(0).getId());
				}
				List<Long> selectedBddDevices = getAllBddDevices(selectedRealms.get(0));
				prepareBonjourGateway(selectedBddDevices);
				
				//fix bug 23401
				removeNoneAvailable(); 
				
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	// -------------------- Operation method ------------------------- //
	private String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
	
	private void removeNoneAvailable(){
		if(selectedRealms != null && selectedRealms.size() == 1){
			if("-1".equals(selectedRealms.get(0))){
				selectedRealms.remove(0);
			}
		}
		if(selectedBddDevices != null && selectedBddDevices.size() == 1){
			if(CHECK_ITEM_ID_NONE == selectedBddDevices.get(0)){
				selectedBddDevices.remove(0);
			}
		}
	}
	
	private Collection<SimpleHiveAp> getHiveAPList(List<Long> bddIDList) {
		// get ap mac at first
		Set<String> apMacSet = new HashSet<String>();
		String selectedBddMacs = "";
		for (Long bddID : bddIDList) {
			if(bddID<0){
				selectedBddMacs +=bddID.toString()+",";
				continue;
			}
			BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoById(BonjourGatewayMonitoring.class, bddID);
			if(bonjourGatewayMonitoring != null){
				apMacSet.add(bonjourGatewayMonitoring.getMacAddress());
			}
		}

		// get ap list
		Collection<SimpleHiveAp> apList = new ArrayList<SimpleHiveAp>(apMacSet
				.size());
		CacheMgmt cacheInstance = CacheMgmt.getInstance();
		
		for (String apMac : apMacSet) {
			SimpleHiveAp ap = cacheInstance.getSimpleHiveAp(apMac);
			apList.add(ap);
			selectedBddMacs +=apMac+",";
		}
		if(!"".equals(selectedBddMacs)){
			selectedBddMacs = selectedBddMacs.substring(0, selectedBddMacs.length()-1);
		}
		setSelectedBddMacs(selectedBddMacs);
		return apList;
	}
	
	private List<Long> getAllBddDevices(String realmId){
		List<Long> allBddDevices = new ArrayList<Long>();
		if("".equals(realmId)||realmId == null ){//|| "-1".equals(realmId)
			allBddDevices.add((long) CHECK_ITEM_ID_NONE);
		} else {
			List<BonjourGatewayMonitoring> bonjourGatewayMonitorings = QueryUtil.executeQuery(BonjourGatewayMonitoring.class, null, new FilterParams("realmId",realmId), domainId);
			for (BonjourGatewayMonitoring bonjourGatewayMonitoring : bonjourGatewayMonitorings) {
				allBddDevices.add(bonjourGatewayMonitoring.getId());
			}
			if(allBddDevices.size() == 0){
				allBddDevices.add((long) CHECK_ITEM_ID_NONE);
			} else {
				allBddDevices.add(0,(long)-2);
			}
		}
		return allBddDevices;
	}
	
	private void initSortParams(){
		orderBy = null;
		ascending=false;
	}
	
	private void prepareBonjourGateway(List<Long> selectedBddDevices){
		setAvailableBddDevices(prepareBddDevices(selectedRealms));
		initSortParams();
		setSelectedBddDevices(selectedBddDevices);
		setBonjourServiceDetails(prepareServices(selectedBddDevices,orderBy,ascending,chkShared));
	}
	
	public List<CheckItem> prepareBddDevices(String realmId){
		List<CheckItem> items = new ArrayList<CheckItem>();
		if("".equals(realmId)||realmId == null ){//|| "-1".equals(realmId)
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
			return items;
		}
		
		List<BonjourGatewayMonitoring> bonjourGatewayMonitorings = QueryUtil.executeQuery(BonjourGatewayMonitoring.class, null, new FilterParams("realmId",realmId), domainId); 
		for (BonjourGatewayMonitoring bonjourGatewayMonitoring : bonjourGatewayMonitorings) {
			List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("macAddress", bonjourGatewayMonitoring.getMacAddress()), domainId,this);
			String networkPolicyNameString="";
			if(hiveAps != null && hiveAps.size() != 0){
				if(hiveAps.get(0).getConfigTemplate() != null && hiveAps.get(0).getConfigTemplate().getBonjourGw() != null){
					networkPolicyNameString=hiveAps.get(0).getConfigTemplate().getBonjourGw().getBonjourGwName();
				}
			}
			
			CheckItem item = new CheckItem(bonjourGatewayMonitoring.getId(),"&nbsp;&nbsp;"+bonjourGatewayMonitoring.getHostName()+" ("+networkPolicyNameString+")");
			items.add(item);
		}
		
		if (items.size() == 0) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
				.getUserMessage("config.optionsTransfer.none")));
		} else {
			items.add(0, new CheckItem((long)-2, "All"));
		}	
		
		return items;
	}
	
	public List<CheckItem> prepareBddDevices(List<String> realmIds){
		List<CheckItem> items = new ArrayList<CheckItem>();
		if(realmIds == null || realmIds.size() == 0){
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
			return items;
		} else {
			items = prepareBddDevices(realmIds.get(0));
		}
		
		return items;
	}
	
	public List<CheckItem3> prepareRealms(){
		List<CheckItem3> items = new ArrayList<CheckItem3>();
		String sqlString  = "select distinct realmid from BONJOUR_GATEWAY_MONITORING where owner="+domainId;
		List<?> realmidList =QueryUtil.executeNativeQuery(sqlString);
		if(realmidList == null ){
			items.add(new CheckItem3("-1", MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		boolean isAllNull=true;
		for(Object realmId :realmidList){
			if(realmId == null){
				continue;
			}
			isAllNull = false;
			BonjourRealm realm = QueryUtil.findBoByAttribute(BonjourRealm.class, "realmId", realmId, domainId);
			if(realm == null){
				items.add(new CheckItem3(realmId.toString(),realmId.toString()));
			} else {
				items.add(new CheckItem3(realmId.toString(),realm.getRealmName()));
			}
		}
		if(isAllNull){
			items.add(new CheckItem3("-1", MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		
		return items;
	}
	
	private List<BonjourServiceDetail> prepareServices(List<Long> serviceIds,String orderBy,boolean ascending,boolean shared){
		 List<BonjourServiceDetail> bonjourServiceDetails = new ArrayList<BonjourServiceDetail>();
		 if(serviceIds == null){
			 return bonjourServiceDetails;
		 }
		 String idString ="";
		 for(Long id :serviceIds){
			 if(id<0){
				 continue;
			 }
			 idString += id.toString()+",";
		 }
		 if(!"".equals(idString)){
			 StringBuffer sqlBuffer = new StringBuffer();
			 sqlBuffer.append("select host,ip4,ip6,name,port,shared,text,type,vlan,vlanGroupName,shareRomoteBdd from bonjour_service_detail ");
			 sqlBuffer.append("where bonjour_gateway_monitoring_id in (");
			 sqlBuffer.append(idString.substring(0, idString.length()-1));
			 sqlBuffer.append(") ");
			 if(shared){
				 sqlBuffer.append(" and shared="+shared);
			 }
			 
			 if(orderBy == null || "".equals(orderBy)){
				 sqlBuffer.append(" order by shared desc,type ");
			 } else {
				 sqlBuffer.append(" order by " + orderBy + (ascending ? "" : " desc"));
			 }
			 List<?> details = QueryUtil.executeNativeQuery(sqlBuffer.toString());
			 
			 if(details != null){
				 for(Object object : details){
					 Object[] attributes = (Object[]) object;
					 BonjourServiceDetail detail = new BonjourServiceDetail();
					 detail.setHost(attributes[0] == null ? null :attributes[0].toString());
					 detail.setIp4(attributes[1] == null ? null :attributes[1].toString());
					 detail.setIp6(attributes[2] == null ? null :attributes[2].toString());
					 detail.setName((attributes[3] == null ? null :attributes[3].toString()));
					 detail.setPort(attributes[4] == null ? null :(Integer)attributes[4]);
					 detail.setShared(attributes[5] == null ? false :(Boolean)attributes[5]);
					 detail.setText(attributes[6] == null ? null :attributes[6].toString());
					 detail.setType(attributes[7] == null ? null :attributes[7].toString());
					 detail.setVlan(attributes[8] == null ? null :(Short)attributes[8]);
					 String vlanGroupName = attributes[9] == null ? null :attributes[9].toString();
					 List<String> vlanGroupNameList = new ArrayList<>();
					 if(vlanGroupName == null || "".equals(vlanGroupName)){ // before Essen Release
						 vlanGroupNameList.add(MgrUtil.getUserMessage("config.ipPolicy.any"));
					 } else {
						 String[] vlanGroupNames = vlanGroupName.split(BonjourServiceDetail.SEPARATOR_CHAR);
						 for(String vg : vlanGroupNames){
							 vlanGroupNameList.add(vg);
						 }
					 }
					 
					 List<VlanGroup> vlanGroups = new ArrayList<VlanGroup>();
					 for(int i=0; i<vlanGroupNameList.size(); i++){
						 String name = vlanGroupNameList.get(i);
						 List<VlanGroup> vlanGroupList = QueryUtil.executeQuery(VlanGroup.class, null, new FilterParams("vlanGroupName", name), getDomainId());
						 if(vlanGroupList == null || vlanGroupList.isEmpty()){
							 VlanGroup vlangroup = new VlanGroup();
							 vlangroup.setVlanGroupName(name);
							 vlanGroups.add(vlangroup);
						 } else {
							 VlanGroup vlangroup = vlanGroupList.get(0);
							 vlanGroups.add(vlangroup);
						 }
					 }
					
					 detail.setVlanGroups(vlanGroups);
					 
					 String remoteBdd = attributes[10] == null ? null :attributes[10].toString();
					 if(remoteBdd != null && !remoteBdd.isEmpty()){
						 StringBuilder remoteBddHostName = new StringBuilder();
						 String[] remoteBdds = remoteBdd.split(",");
						 for(String remoteBddIP : remoteBdds){
							 List<?> lists = QueryUtil.executeQuery("select bo.hostName from " + HiveAp.class.getSimpleName() + " bo", null, new FilterParams(
												"ipAddress", remoteBddIP),  domainId, null);
							 
							 if(lists != null && !lists.isEmpty()){
								 for(Object o : lists){
									 String hostName = (String) o;
									 remoteBddHostName.append(hostName);
									 remoteBddHostName.append(",");
								 }
							 } else {
								 remoteBddHostName.append(remoteBddIP).append(",");
							 }
						 }
						 detail.setShareRomoteBdd(remoteBddHostName.substring(0, remoteBddHostName.length()-1));
					 }
					
					 bonjourServiceDetails.add(detail);
				 }
			 }
		 }
		
		 return bonjourServiceDetails;
	}
	
	// -------------------- Override method -------------------------- //
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_BONJOUR_GATEWAY_MONITORING);
		setDataSource(BonjourGatewayMonitoring.class);
	}
	@Override
	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = findBos(this);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		
		if(bo instanceof BonjourGatewayMonitoring){
			BonjourGatewayMonitoring bonjourGatewayMonitoring = (BonjourGatewayMonitoring)bo;
			if(bonjourGatewayMonitoring.getBonjourServiceDetails() != null){
				bonjourGatewayMonitoring.getBonjourServiceDetails().size();
			}
		}
		if(bo instanceof ConfigTemplate){
			ConfigTemplate config = (ConfigTemplate)bo;
			if(config != null && config.getHiveProfile()!= null){
				config.getHiveProfile().getId();
			}
		}
		if(bo instanceof MapContainerNode){
			MapContainerNode mapContainerNode = (MapContainerNode)bo;
			if(mapContainerNode != null && null != mapContainerNode.getParentMap()) {
				mapContainerNode.getParentMap().getId();
			    Set<MapNode> children =  mapContainerNode.getChildNodes();
			    if(children != null){
			    	children.size();
			    	for (MapNode node : children) {
						if (node.isLeafNode()) {
							MapLeafNode leafNode = (MapLeafNode) node;
							if (null != leafNode.getHiveAp()) {
								leafNode.getHiveAp().getId();
								if(leafNode.getHiveAp().getConfigTemplate() != null){
									leafNode.getHiveAp().getConfigTemplate().getId();
									if(leafNode.getHiveAp().getConfigTemplate().getHiveProfile() != null){
										leafNode.getHiveAp().getConfigTemplate().getHiveProfile().getId();
									}
								}
							}
						}
					}
			    }
			}
		}
		if(bo instanceof HiveAp){
			HiveAp hiveAp = (HiveAp)bo;
			if(hiveAp.getConfigTemplate() != null){
				hiveAp.getConfigTemplate().getId();
				if(hiveAp.getConfigTemplate().getHiveProfile() != null){
					hiveAp.getConfigTemplate().getHiveProfile().getId();
				}
				ConfigTemplate configTemplate = hiveAp.getConfigTemplate();
				if(configTemplate.getBonjourGw() != null){
					configTemplate.getBonjourGw().getId();
				}
			}
			
			if(hiveAp.getMapContainer() != null && null != hiveAp.getMapContainer().getParentMap()) {
			    hiveAp.getMapContainer().getParentMap().getId();
			    Set<MapNode> children =  hiveAp.getMapContainer().getChildNodes();
			    if(children != null){
			    	children.size();
			    	for (MapNode node : children) {
						if (node.isLeafNode()) {
							MapLeafNode leafNode = (MapLeafNode) node;
							if (null != leafNode.getHiveAp()) {
								leafNode.getHiveAp().getId();
								if(leafNode.getHiveAp().getConfigTemplate() != null){
									leafNode.getHiveAp().getConfigTemplate().getId();
									if(leafNode.getHiveAp().getConfigTemplate().getHiveProfile() != null){
										leafNode.getHiveAp().getConfigTemplate().getHiveProfile().getId();
									}
								}
							}
						}
					}
			    }
			}
		}
	
		return null;
	}
	// -------------------- Fields -------------------------- //
	
	private List<String> selectedRealms;
	
	private List<CheckItem> availableBddDevices;
	
	private List<Long> selectedBddDevices;
	
	private List<BonjourServiceDetail> bonjourServiceDetails;
	
	private long bgId;
	
	private long networkId;
	
	private String realmId;
	
	private String realmName;
	
	private String orderBy;
	
	private boolean ascending;
	
	private boolean chkShared;
	
	private String selectedBddIds; // for refresh button
	
	private String selectedBddMacs; // for refresh button
	
	private String selectedRealmIds; // for refresh button
	
	private String updateTime;
	
	// -------------------- Getter/Setter -------------------------- //
	
	public int getServiceDetailLength(){
		return bonjourServiceDetails==null ? 0 : bonjourServiceDetails.size();
	}
	
	public boolean getItemOderby(){
		if(orderBy == null || "".equals(orderBy)){
			return false;
		}
		return true;
	}
	
	public List<CheckItem3> getAvailableRealms() {
		return prepareRealms();
	}
	
	public List<CheckItem> getAvailableBddDevices() {
		return availableBddDevices;
	}

	public void setAvailableBddDevices(List<CheckItem> availableBddDevices) {
		this.availableBddDevices = availableBddDevices;
	}

	public List<String> getSelectedRealms() {
		return selectedRealms;
	}

	public void setSelectedRealms(List<String> selectedRealms) {
		this.selectedRealms = selectedRealms;
	}

	public List<Long> getSelectedBddDevices() {
		return selectedBddDevices;
	}

	public void setSelectedBddDevices(List<Long> selectedBddDevices) {
		this.selectedBddDevices = selectedBddDevices;
	}

	public List<BonjourServiceDetail> getBonjourServiceDetails() {
		return bonjourServiceDetails;
	}

	public void setBonjourServiceDetails(
			List<BonjourServiceDetail> bonjourServiceDetails) {
		this.bonjourServiceDetails = bonjourServiceDetails;
	}

	public long getBgId() {
		return bgId;
	}

	public void setBgId(long bgId) {
		this.bgId = bgId;
	}

	public long getNetworkId() {
		return networkId;
	}

	public void setNetworkId(long networkId) {
		this.networkId = networkId;
	}

	public String getRealmId() {
		return realmId;
	}

	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isChkShared() {
		return chkShared;
	}

	public void setChkShared(boolean chkShared) {
		this.chkShared = chkShared;
	}

	public String getSelectedBddIds() {
		return selectedBddIds;
	}

	public void setSelectedBddIds(String selectedBddIds) {
		this.selectedBddIds = selectedBddIds;
	}

	public String getSelectedBddMacs() {
		return selectedBddMacs;
	}

	public void setSelectedBddMacs(String selectedBddMacs) {
		this.selectedBddMacs = selectedBddMacs;
	}

	public String getSelectedRealmIds() {
		return selectedRealmIds;
	}

	public void setSelectedRealmIds(String selectedRealmIds) {
		this.selectedRealmIds = selectedRealmIds;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
