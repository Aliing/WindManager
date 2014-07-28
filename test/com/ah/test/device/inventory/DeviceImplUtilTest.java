package com.ah.test.device.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
import com.ah.ws.rest.models.DeviceOptModel;
import com.ah.ws.rest.models.SerialNumberList;

public class DeviceImplUtilTest implements DeviceUtils {

	@Override
	public List<SerialNumberList> addSerialNumbersToRedirector(
			List<String> apMappingListOk, HmDomain domain,
			boolean addToRedirector) throws Exception {
		return DEV_DEVICE_UTIL_IMPL.addSerialNumbersToRedirector(apMappingListOk, domain, addToRedirector);
	}

	@Override
	public List<DeviceInventory> addSerialNumberToHm(
			List<String> apMappingListOk, HmDomain domain) {
		return DEV_DEVICE_UTIL_IMPL.addSerialNumberToHm(apMappingListOk, domain);
	}

	@Override
	public List<String> removeSerialNumberFromHm(List<HiveAp> removeAps,
			Collection<Long> successRemovedLst, boolean generatelog, HmDomain domain) {
		return DEV_DEVICE_UTIL_IMPL.removeSerialNumberFromHm(removeAps, successRemovedLst, false, domain);
	}

	@Override
	public void generateAuditLog(short arg_Status, String arg_Comment,
			HmDomain domain) {
	}

	private Map<Long, String> getExistSerialPrefix4Domain() {
		Map<Long, String> result = new HashMap<>();
		String sql = "select max(substring(serialNumber from 11 for 3)) from device_inventory";
		List<?> maxSerials = QueryUtil.executeNativeQuery(sql);
		int maxValue = 0;
		if (maxSerials != null
				&& !maxSerials.isEmpty()
				&& maxSerials.get(0) != null) {
			maxValue = Integer.valueOf(maxSerials.get(0).toString());
		}
		if (maxValue >= 0) {
			for (int i = 1; i < maxValue + 2; i++) {
				result.put(-1L*i, this.getDomainSerialPrefixString(i));
			}
		}
		
		return result;
	}
	private String getDomainSerialPrefixString(int order) {
		return commonPrefix.concat(order < 10? "00"+order: order < 100? "0"+order: ""+order );
	}
	
	private boolean blnHasQueriedExistPrefix4Domain = false;
	private void prepareSerialPrefix4Domain() {
		if (!blnHasQueriedExistPrefix4Domain) {
			this.serialPrefix4Domain = this.getExistSerialPrefix4Domain();
			blnHasQueriedExistPrefix4Domain = true;
		}
	}
	
	private String commonPrefix = "1211111113";
	private Map<Long, String> serialPrefix4Domain = new HashMap<>();
	private String getDomainSerialPrefix(HmDomain domain) {
		this.prepareSerialPrefix4Domain();
		Long domainId = domain.getId();
		int curLen = serialPrefix4Domain.size();
		if (!serialPrefix4Domain.containsKey(domainId)) {
			serialPrefix4Domain.put(domainId, this.getDomainSerialPrefixString(curLen));
		}
		return serialPrefix4Domain.get(domainId);
	}
	
	private static Random RD = new Random();
	private List<DeviceInventory> getRandomInventories(HmDomain domain) {
		int maxNum = RD.nextInt(10) + 1;
		List<DeviceInventory> result = new ArrayList<>();
		for (int i = 0; i < maxNum; i++) {
			DeviceInventory device = new DeviceInventory();
			device.setSerialNumber(this.getDomainSerialPrefix(domain) + i);
			result.add(device);
		}
		return result;
	}
	@Override
	public boolean syncDeviceInventoriesWithRedirector(HmDomain domain) {
		String vhmId = domain.getVhmID();
		if (domain.isHomeDomain()) {
			vhmId = "home";
		}
		List<DeviceInventory> inventories = this.getRandomInventories(domain);
		for (DeviceInventory device : inventories) {
			device.setOwner(domain);
			device.setConnectStatus(Integer.valueOf(RD.nextInt(2) + 1).shortValue());
		}
		System.out.println("come to sync device inventories with Redirector for " + vhmId + ",  at " + new java.util.Date());
		return syncDeviceInventoriesWithRedirector(inventories, domain);
	}

	private DeviceUtils DEV_DEVICE_UTIL_IMPL = new DeviceImpUtils();
	@Override
	public boolean syncDeviceInventoriesWithRedirector(
			List<DeviceInventory> deviceInventories, HmDomain domain) {
		System.out.println("come to call real sync operation, update data in DB.");
		return DEV_DEVICE_UTIL_IMPL.syncDeviceInventoriesWithRedirector(deviceInventories, domain);
	}

	@Override
	public String getDeviceInventoryCSVString(
			List<DeviceInventory> deviceInventories, byte type, boolean easymode) {
		return DEV_DEVICE_UTIL_IMPL.getDeviceInventoryCSVString(deviceInventories, type,easymode);
	}

	@Override
	public List<DeviceOptModel> addSerialNumberToHm(
			List<DeviceOptModel> deviceList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeviceOptModel> removeSerialNumbersFromRedirector(
			List<DeviceOptModel> deviceList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeviceOptModel> syncSerialNumbersFromRedirector(
			List<DeviceOptModel> deviceList) {
		// TODO Auto-generated method stub
		return null;
	}

}
