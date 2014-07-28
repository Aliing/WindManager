package com.ah.be.config.create.common;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("static-access")
public class CVGAndBRIpResourceManage {
	
	private static final Tracer log = new Tracer(CVGAndBRIpResourceManage.class.getSimpleName());
	
	
	public static synchronized List<SubNetworkResource> loadSubNetworkResource(HmDomain owner, SortParams sortParams, FilterParams filterParams, Long domainId){
		return MgrUtil.getQueryEntity().executeQuery(
				SubNetworkResource.class, sortParams, filterParams, domainId, new ConfigLazyQueryBo());
	}
	
	public static synchronized List<SubNetworkResource> loadSubNetworkResource(HmDomain owner, SortParams sortParams, FilterParams filterParams, Long domainId, int maxResults){
		return MgrUtil.getQueryEntity().executeQuery(
				SubNetworkResource.class, sortParams, filterParams, domainId, maxResults);
	}
	
	public static synchronized void updateSubNetworkResourceSucc(HmDomain owner, String macAddress, Long domainId) throws Exception  {
		
		//update all prepare use source to used.
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1",
				"hiveApMac = :s2 and status = :s3",
				new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_USED, 
					macAddress, SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_USE} );
				
		//update all prepare remove source to free.
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1, hiveApMgtx = -1, hiveApMac = null ",
				"hiveApMac = :s2 and status = :s3",
				new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE, 
					macAddress, SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_REMOVE} );
	}
	
	//update mapping between hiveApMgtx and SubNetworkResource
	public static synchronized void updateMgtxNetworkMapping(HmDomain owner, List<SubNetworkResource> subResource) throws Exception{
		for(SubNetworkResource resource : subResource){
			MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
					"hiveApMgtx = :s1",
					"id = :s2",
					new Object[]{resource.getHiveApMgtx(), 
						resource.getId()} );
		}
	}
	
	public static synchronized void updateSubNetworkResourceFaild(HmDomain owner, String macAddress) throws Exception {
		
		//update all prepare use source to free.
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1 , hiveApMgtx = -1, hiveApMac = null ",
				"hiveApMac = :s2 and status = :s3",
				new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE, 
					macAddress, SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_USE} );
		
		//update all prepare remove source to used.
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1 ",
				"hiveApMac = :s2 and status = :s3",
				new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_USED, 
					macAddress, SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_REMOVE} );
	}
	
	public static synchronized void updateSubNetworkResourceFree(HmDomain owner, String macAddress) throws Exception{
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1 , hiveApMgtx = -1, hiveApMac = null ","hiveApMac = :s2 ",new Object[]{
			        SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE,macAddress} );
	}
	
	public static synchronized void  prepareReleaseResource(HmDomain owner, String macAddress) throws Exception{
		MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
				"status = :s1",
				"hiveApMac = :s2 ",
				new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_REMOVE, macAddress} );
	}
	
	public static synchronized SubNetworkResource getVpnNetworkResExistsMap(HmDomain owner, String macAddress, VpnNetwork network, Long domainId){
		FilterParams filter = new FilterParams("hiveApMac = :s1 and vpnNetwork = :s2 and status = :s3 and hiveApMgtx > 0", 
				new Object[]{macAddress, network, SubNetworkResource.IP_SUBBLOCKS_STATUS_USED});
		List<SubNetworkResource> networkResList = loadSubNetworkResource(owner, null, filter, domainId, 1);
		
		if(networkResList == null || networkResList.isEmpty()){
			return null;
		}else{
			return networkResList.get(0);
		}
	}
	
	public static synchronized SubNetworkResource getVpnNetworkRes(VpnNetwork network, HiveAp hiveAp, boolean isView) throws CreateXMLException{
		if(network == null || hiveAp == null){
			return null;
		}
		
		// get SubNetworkResource already exists.
		SubNetworkResource resSource;
		FilterParams filter = new FilterParams("hiveApMac = :s1 and status = :s2 and vpnNetwork = :s3", 
				new Object[]{hiveAp.getMacAddress(), SubNetworkResource.IP_SUBBLOCKS_STATUS_USED, network});
		List<SubNetworkResource> existsRes = loadSubNetworkResource(hiveAp.getOwner(), null, filter, hiveAp.getOwner().getId());
		if(existsRes != null && !existsRes.isEmpty()){
			resSource = existsRes.get(0);
			resSource.setHiveApMac(hiveAp.getMacAddress());
			return resSource;
		}
		
		AssignAddress assignIp = getAssignAddress(network, hiveAp);
		// get SubNetworkResource from Allocate subnetworks by specific IP.
		if(assignIp.getSubBlockList() != null && !assignIp.getSubBlockList().isEmpty()){
			for(String ipAddress : assignIp.getSubBlockList()){
				long ipAddressLong = AhEncoder.ip2Long(ipAddress);
				FilterParams filter1 = new FilterParams("status = :s1 and ipStartLong <= :s2 and ipEndLong >= :s3 and vpnNetwork = :s4 and vipIpAddress = :s5", 
						new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE, ipAddressLong, ipAddressLong, network, true});
				List<SubNetworkResource> subRes = loadSubNetworkResource(hiveAp.getOwner(), null, filter1, hiveAp.getOwner().getId());
				if(subRes != null && !subRes.isEmpty()){
					resSource = subRes.get(0);
					resSource.setHiveApMac(hiveAp.getMacAddress());
					if(!isView){
						preemptionSubNetworkResource(hiveAp.getOwner(), resSource);
					}
					return resSource;
				}
			}
		}
		
		// get SubNetworkResource from Allocate subnetworks.
		if(assignIp.getNetWorkList() != null && !assignIp.getNetWorkList().isEmpty()){
			for(String ipAddress : assignIp.getNetWorkList()){
				FilterParams filter2 = new FilterParams("vpnNetwork = :s1 and status = :s2 and parentNetwork = :s3 and vipIpAddress = :s4", 
						new Object[]{network, SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE, ipAddress, false});
				List<SubNetworkResource> netRes = loadSubNetworkResource(hiveAp.getOwner(), null, filter2, hiveAp.getOwner().getId());
				if(netRes != null && !netRes.isEmpty()){
					resSource = netRes.get(0);
					resSource.setHiveApMac(hiveAp.getMacAddress());
					if(!isView){
						preemptionSubNetworkResource(hiveAp.getOwner(), resSource);
					}
					return resSource;
				}
			}
		}
		
//		String tag1 = hiveAp.getClassificationTag1() == null ? "" : hiveAp.getClassificationTag1();
//		String tag2 = hiveAp.getClassificationTag2() == null ? "" : hiveAp.getClassificationTag2();
//		String tag3 = hiveAp.getClassificationTag3() == null ? "" : hiveAp.getClassificationTag3();
		String[] errParams = {network.getNetworkName(), hiveAp.getHostName()};
		String errMsg = NmsUtil.getUserMessage(
				"error.be.config.create.noFreeNetwork", errParams);
		log.error("getVpnNetworkRes", errMsg);
		throw new CreateXMLException(errMsg);
	}
	
	public static void releaseUnusedSubNetworkResource(HmDomain owner, List<VpnNetwork> usedNetworks, String deviceMac){
		try {
			MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
					"status = :s1",
					"hiveApMac = :s2 and vpnNetwork not in (:s3)",
					new Object[]{SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_REMOVE,
						deviceMac,
						usedNetworks} );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void preemptionSubNetworkResource(HmDomain owner, SubNetworkResource resource) {
		try {
			MgrUtil.getQueryEntity().updateBos(SubNetworkResource.class, 
					"hiveApMac = :s1 , status = :s2 ","id = :s3",
						new Object[]{resource.getHiveApMac(),
				        	SubNetworkResource.IP_SUBBLOCKS_STATUS_PRE_USE,
				        	resource.getId()} );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static AssignAddress getAssignAddress(VpnNetwork network, HiveAp hiveAp){
		if(network == null || hiveAp == null){
			return null;
		}
		AssignAddress asAddr = new AssignAddress();
		
		//exact assign address sub block
		if(network.getReserveClass() != null && !network.getReserveClass().isEmpty()){
			List<SingleTableItem> ipSubBlock = getSingleTableItemList(network, hiveAp, true);
			if(ipSubBlock != null){
				for(SingleTableItem ipItem : ipSubBlock){
					asAddr.getSubBlockList().add(ipItem.getIpAddress());
				}
			}
		}
		
		List<SingleTableItem> ipSubClass = getSingleTableItemList(network, hiveAp, false);
		if(ipSubClass != null){
			for(SingleTableItem ipItem : ipSubClass){
				VpnNetworkSub subNetwork = getSubNetwork(network, ipItem.getKey());
				if(subNetwork != null){
					asAddr.getNetWorkList().add(subNetwork.getIpNetwork());
				}
			}
		}
		
		for(VpnNetworkSub subItem : network.getSubItems()){
			if(!subItem.isSubnetClassification()){
				asAddr.getNetWorkList().add(subItem.getIpNetwork());
			}
		}

		return asAddr;
	}
	
	private static VpnNetworkSub getSubNetwork(VpnNetwork network, int key){
		for(VpnNetworkSub subItem : network.getSubItems()){
			if(key == subItem.getKey()){
				return subItem;
			}
		}
		return null;
	}
	
	private static List<SingleTableItem> getSingleTableItemList(VpnNetwork network, HiveAp hiveAp, boolean vipIp){
		List<SingleTableItem> resList = new ArrayList<>();
		List<SingleTableItem> itemList = new ArrayList<>();
		if(network != null && network.getSubItems() != null){
			for(VpnNetworkSub subNet : network.getSubItems()){
				if(vipIp && subNet.isReserveClassification()){
					int key = subNet.getKey();
					for(SingleTableItem item : network.getReserveClass()){
						if(key == item.getKey()){
							itemList.add(item);
						}
					}
				}
				if(!vipIp){
					int key = subNet.getKey();
					boolean isFound = false;
					for(SingleTableItem item : network.getSubNetwokClass()){
						if(key == item.getKey()){
							itemList.add(item);
							isFound = true;
						}
					}
					if(!isFound){
						SingleTableItem item = new SingleTableItem();
						item.setType(SingleTableItem.TYPE_CLASSIFIER);
						item.setKey(key);
						itemList.add(item);
					}
				}
			}
		}
		
		for (SingleTableItem ipItem : itemList) {
			if(CLICommonFunc.isRuleMatch(ipItem, hiveAp)){
				resList.add(ipItem);
			}
		}
		
		return resList;
	}
	
	private static class AssignAddress {

		private List<String> subBlockList = new ArrayList<>();
		
		private List<String> netWorkList = new ArrayList<>();
		
		public List<String> getSubBlockList(){
			if(subBlockList == null){
				subBlockList = new ArrayList<>();
			}
			return subBlockList;
		}
		
		public List<String> getNetWorkList(){
			if(netWorkList == null){
				netWorkList = new ArrayList<>();
			}
			return netWorkList;
		}
	}

}