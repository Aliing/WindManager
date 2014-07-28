package com.ah.ws.rest.server.bussiness;

import java.util.List;

import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
import com.ah.ws.rest.models.DeviceOptModel;

public abstract class DevicesOperation {

	public static List<DeviceOptModel> addDevices(List<DeviceOptModel> deviceList) {
		DeviceUtils diu = DeviceImpUtils.getInstance();
		return diu.addSerialNumberToHm(deviceList);
	}

	public static List<DeviceOptModel> removeDevices(List<DeviceOptModel> deviceList) {
		DeviceUtils diu = DeviceImpUtils.getInstance();
		return diu.removeSerialNumbersFromRedirector(deviceList);
	}

	public static List<DeviceOptModel> syncDevices(List<DeviceOptModel> deviceList) {
		DeviceUtils diu = DeviceImpUtils.getInstance();
		return diu.syncSerialNumbersFromRedirector(deviceList);
	}
}
