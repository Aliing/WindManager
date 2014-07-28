package com.ah.ws.rest.server.resources.redirector;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ah.ws.rest.models.DeviceOptModel;
import com.ah.ws.rest.server.auth.exception.ApplicationException;
import com.ah.ws.rest.server.bussiness.DevicesOperation;

@Path("/devices")
public class DevicesResource {

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<DeviceOptModel> removeDevices(List<DeviceOptModel> deviceList) throws ApplicationException {
		List<DeviceOptModel> rtnDeviceList = null;
		try {
			rtnDeviceList = DevicesOperation.removeDevices(deviceList);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		return rtnDeviceList;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<DeviceOptModel> addDevices(List<DeviceOptModel> deviceList) throws ApplicationException {
		List<DeviceOptModel> rtnDeviceList = null;
		try {
			rtnDeviceList = DevicesOperation.addDevices(deviceList);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		return rtnDeviceList;
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<DeviceOptModel> syncDevices(List<DeviceOptModel> deviceList) throws ApplicationException {
		List<DeviceOptModel> rtnDeviceList = null;
		try {
			rtnDeviceList = DevicesOperation.syncDevices(deviceList);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		return rtnDeviceList;
	}
}
