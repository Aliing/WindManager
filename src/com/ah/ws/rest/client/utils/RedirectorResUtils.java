package com.ah.ws.rest.client.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.ah.ws.rest.models.DeviceCounts;
import com.ah.ws.rest.models.VhmListModel;
import com.ah.ws.rest.models.DeviceOptModel;
import com.ah.ws.rest.models.ExceptionModel;
import com.ah.ws.rest.models.SerialNumberList;
import com.ah.ws.rest.models.SerialNumbers;
import com.ah.ws.rest.models.dto.Vhm;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;

public class RedirectorResUtils extends BaseUtils implements PathConstant {
	private static RedirectorResUtils[] instances = new RedirectorResUtils[ALL];

	private static Map<String, RedirectorResUtils> idInstances = new HashMap<String, RedirectorResUtils>();

	private RedirectorResUtils() {
		super(CLIENT_MODULE_REDIRECTOR, NONE);
	}

	private RedirectorResUtils(String role) {
		super(CLIENT_MODULE_REDIRECTOR, ROLE);
		setRole(role);
	}

	private RedirectorResUtils(byte[] secretkey) {
		super(CLIENT_MODULE_REDIRECTOR, SKEY);
		setSkey(secretkey);
	}

	private RedirectorResUtils(String role, byte[] secretkey) {
		super(CLIENT_MODULE_REDIRECTOR, ROLE_SKEY);
		setRole(role);setSkey(secretkey);
	}

	private RedirectorResUtils(String id, String role, byte[] secretkey) {
		super(id, CLIENT_MODULE_REDIRECTOR, ROLE_SKEY);
		setRole(role);setSkey(secretkey);
	}

	public static RedirectorResUtils getInstance() {
		if (instances[NONE] == null || !instances[NONE].isMatchType(NONE)) {
			instances[NONE] = new RedirectorResUtils();
		}
		return instances[NONE];
	}

	public static RedirectorResUtils getInstance(String role) {
		if (instances[ROLE] == null || !instances[ROLE].isMatchType(ROLE)) {
			instances[ROLE] = new RedirectorResUtils(role);
		}
		instances[ROLE].setRole(role);
		return instances[ROLE];
	}

	public static RedirectorResUtils getInstance(byte[] secretkey) {
		if (instances[SKEY] == null || !instances[SKEY].isMatchType(SKEY)) {
			instances[SKEY] = new RedirectorResUtils(secretkey);
		}
		instances[SKEY].setSkey(secretkey);
		return instances[SKEY];
	}

	public static RedirectorResUtils getInstance(String role, byte[] secretkey) {
		if (instances[ROLE_SKEY] == null || !instances[ROLE_SKEY].isMatchType(ROLE_SKEY)) {
			instances[ROLE_SKEY] = new RedirectorResUtils(role, secretkey);
		}
		instances[ROLE_SKEY].setRole(role);instances[ROLE_SKEY].setSkey(secretkey);
		return instances[ROLE_SKEY];
	}

	public static RedirectorResUtils getNewInstance(String role, byte[] secretkey) {
		String id = role;
		if (StringUtils.isBlank(id)) {
			return getInstance(role, secretkey);
		}

		RedirectorResUtils inst = null;
		if (!idInstances.containsKey(id)) {
			inst = new RedirectorResUtils(id, role, secretkey);
			idInstances.put(id, inst);
		} else {
			inst = idInstances.get(id);
		}
		inst.setRole(role);inst.setSkey(secretkey);

		return inst;
	}

	public synchronized DeviceCounts getDeviceCounts(String vhmid) throws Exception {
		GenericType<DeviceCounts> deviceCounts = new GenericType<DeviceCounts>() {};

		ClientResponse cr = getHttpClient()
				.resource(getUri().path(RED_DEVICE_COUNTS_PATH + vhmid).build())
				.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);

		Status status = cr.getClientResponseStatus();
		if (status != Status.OK) {
			ExceptionModel exceptionModel = getException(cr);
			throw new Exception(exceptionModel.getMessage());
		}

		return cr.getEntity(deviceCounts);
	}

	public synchronized List<SerialNumberList> importSerialNumbers(SerialNumbers sns) throws Exception {
		GenericType<List<SerialNumberList>> genericType = new GenericType<List<SerialNumberList>>(){};

		ClientResponse cr = getHttpClient()
				.resource(getUri().path(RED_SERIALNUMBERS_PATH).build())
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, sns);

		Status status = cr.getClientResponseStatus();
		if (status != Status.OK) {
			ExceptionModel exceptionModel = getException(cr);
			throw new Exception(exceptionModel.getMessage());
		}

		return cr.getEntity(genericType);
	}

	public synchronized List<SerialNumberList> removeSerialNumbers(SerialNumbers sns) throws Exception {
		GenericType<List<SerialNumberList>> genericType = new GenericType<List<SerialNumberList>>(){};

		ClientResponse cr = getHttpClient()
				.resource(getUri().path(RED_SERIALNUMBERS_PATH).build())
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class, sns);

		Status status = cr.getClientResponseStatus();
		if (status != Status.OK) {
			ExceptionModel exceptionModel = getException(cr);
			throw new Exception(exceptionModel.getMessage());
		}

		return cr.getEntity(genericType);
	}

	public synchronized List<DeviceOptModel> syncDeviceInventories(List<String> vhmIds) throws Exception {
		GenericType<List<DeviceOptModel>> genericType = new GenericType<List<DeviceOptModel>>(){};

		// Construct the request model
		VhmListModel vhmListModel = new VhmListModel();
		List<Vhm> vhmList = vhmListModel.getVhmList();
		for (String vhmId : vhmIds) {
			Vhm vhm = new Vhm();
			vhm.setVhmId(vhmId);
			vhmList.add(vhm);
		}

		ClientResponse cr = getHttpClient()
				.resource(getUri().path(RED_DEVICE_PATH).build())
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, vhmListModel);

		Status status = cr.getClientResponseStatus();
		if (status != Status.OK) {
			ExceptionModel exceptionModel = getException(cr);
			throw new Exception(exceptionModel.getMessage());
		}

		return cr.getEntity(genericType);
	};
}
