package com.ah.ws.rest.client.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.ah.ws.rest.models.ExceptionModel;
import com.ah.ws.rest.models.SerialNumberList;
import com.ah.ws.rest.models.SerialNumbers;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.ClientResponse.Status;

public class LicenseResUtils extends BaseUtils implements PathConstant {
	private static LicenseResUtils[] instances = new LicenseResUtils[ALL];

	private static Map<String, LicenseResUtils> idInstances = new HashMap<String, LicenseResUtils>();

	private LicenseResUtils() {
		super(CLIENT_MODULE_LICENSESERVER, NONE);
	}

	private LicenseResUtils(String role) {
		super(CLIENT_MODULE_LICENSESERVER, ROLE);
		setRole(role);
	}

	private LicenseResUtils(byte[] secretkey) {
		super(CLIENT_MODULE_LICENSESERVER, SKEY);
		setSkey(secretkey);
	}

	private LicenseResUtils(String role, byte[] secretkey) {
		super(CLIENT_MODULE_LICENSESERVER, ROLE_SKEY);
		setRole(role);setSkey(secretkey);
	}

	private LicenseResUtils(String id, String role, byte[] secretkey) {
		super(id, CLIENT_MODULE_LICENSESERVER, ROLE_SKEY);
		setRole(role);setSkey(secretkey);
	}

	public static LicenseResUtils getInstance() {
		if (instances[NONE] == null || !instances[NONE].isMatchType(NONE)) {
			instances[NONE] = new LicenseResUtils();
		}
		return instances[NONE];
	}

	public static LicenseResUtils getInstance(String role) {
		if (instances[ROLE] == null || !instances[ROLE].isMatchType(ROLE)) {
			instances[ROLE] = new LicenseResUtils(role);
		}
		instances[ROLE].setRole(role);
		return instances[ROLE];
	}

	public static LicenseResUtils getInstance(byte[] secretkey) {
		if (instances[SKEY] == null || !instances[SKEY].isMatchType(SKEY)) {
			instances[SKEY] = new LicenseResUtils(secretkey);
		}
		instances[SKEY].setSkey(secretkey);
		return instances[SKEY];
	}

	public static LicenseResUtils getInstance(String role, byte[] secretkey) {
		if (instances[ROLE_SKEY] == null || !instances[ROLE_SKEY].isMatchType(ROLE_SKEY)) {
			instances[ROLE_SKEY] = new LicenseResUtils(role, secretkey);
		}
		instances[ROLE_SKEY].setRole(role);instances[ROLE_SKEY].setSkey(secretkey);
		return instances[ROLE_SKEY];
	}

	public static LicenseResUtils getNewInstance(String role, byte[] secretkey) {
		String id = role;
		if (StringUtils.isBlank(id)) {
			return getInstance(role, secretkey);
		}

		LicenseResUtils inst = null;
		if (!idInstances.containsKey(id)) {
			inst = new LicenseResUtils(id, role, secretkey);
			idInstances.put(id, inst);
		} else {
			inst = idInstances.get(id);
		}
		inst.setRole(role);inst.setSkey(secretkey);

		return inst;
	}

	public List<SerialNumberList> removeSerialNumbers(SerialNumbers sns) throws Exception {
		GenericType<List<SerialNumberList>> genericType = new GenericType<List<SerialNumberList>>(){};

		ClientResponse cr = getHttpClient()
				.resource(getUri().path(LIC_SERIALNUMBERS_PATH).build())
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
}
