package com.ah.ws.rest.client.utils;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.ah.util.Tracer;
import com.ah.ws.rest.models.ga.CheckGAServiceRequest;
import com.ah.ws.rest.models.ga.CheckGAServiceResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;

public class GuestAnalyticsResUtils extends BaseUtils implements PathConstant {
    private static final Tracer LOG = new Tracer(GuestAnalyticsResUtils.class.getSimpleName());
    private static GuestAnalyticsResUtils[] instances = new GuestAnalyticsResUtils[ALL];
    private static Map<String, GuestAnalyticsResUtils> idInstances = new HashMap<String, GuestAnalyticsResUtils>();
    

    private GuestAnalyticsResUtils() {
        super(CLIENT_MODULE_GUESTANALYTICS, NONE);
    }

    private GuestAnalyticsResUtils(String role) {
        super(CLIENT_MODULE_GUESTANALYTICS, ROLE);
        setRole(role);
    }

    private GuestAnalyticsResUtils(byte[] secretkey) {
        super(CLIENT_MODULE_GUESTANALYTICS, SKEY);
        setSkey(secretkey);
    }

    private GuestAnalyticsResUtils(String role, byte[] secretkey) {
        super(CLIENT_MODULE_GUESTANALYTICS, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    private GuestAnalyticsResUtils(String id, String role, byte[] secretkey) {
        super(id, CLIENT_MODULE_GUESTANALYTICS, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    public static GuestAnalyticsResUtils getInstance() {
        if (instances[NONE] == null || !instances[NONE].isMatchType(NONE)) {
            instances[NONE] = new GuestAnalyticsResUtils();
        }
        return instances[NONE];
    }

    public static GuestAnalyticsResUtils getInstance(String role) {
        if (instances[ROLE] == null || !instances[ROLE].isMatchType(ROLE)) {
            instances[ROLE] = new GuestAnalyticsResUtils(role);
        }
        instances[ROLE].setRole(role);
        return instances[ROLE];
    }

    public static GuestAnalyticsResUtils getInstance(byte[] secretkey) {
        if (instances[SKEY] == null || !instances[SKEY].isMatchType(SKEY)) {
            instances[SKEY] = new GuestAnalyticsResUtils(secretkey);
        }
        instances[SKEY].setSkey(secretkey);
        return instances[SKEY];
    }

    public static GuestAnalyticsResUtils getInstance(String role, byte[] secretkey) {
        if (instances[ROLE_SKEY] == null || !instances[ROLE_SKEY].isMatchType(ROLE_SKEY)) {
            instances[ROLE_SKEY] = new GuestAnalyticsResUtils(role, secretkey);
        }
        instances[ROLE_SKEY].setRole(role);instances[ROLE_SKEY].setSkey(secretkey);
        return instances[ROLE_SKEY];
    }

    public static GuestAnalyticsResUtils getNewInstance(String role, byte[] secretkey) {
        String id = role;
        if (StringUtils.isBlank(id)) {
            return getInstance(role, secretkey);
        }

        GuestAnalyticsResUtils inst = null;
        if (!idInstances.containsKey(id)) {
            inst = new GuestAnalyticsResUtils(id, role, secretkey);
            idInstances.put(id, inst);
        } else {
            inst = idInstances.get(id);
        }
        inst.setRole(role);inst.setSkey(secretkey);

        return inst;
    }
    
    public synchronized CheckGAServiceResponse checkGAService(String uri, String cid, String vhmId) {
        CheckGAServiceRequest requestBody = new CheckGAServiceRequest();
        requestBody.setCid(cid);
        requestBody.setVhmId(vhmId);
        
        ApacheHttpClient httpClient = getHttpClient();
        httpClient.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 1000*60);
        
        ClientResponse response = httpClient
                .resource(UriBuilder.fromUri(uri).build())
                .type(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, requestBody);
        
        String msgText = "Call GA API: check social analytics service ";
        Status status = response.getClientResponseStatus();
        if (status == Status.OK) {
            LOG.info("checkGAService", msgText + "successfully.");
        } else {
            LOG.error("checkGAService", msgText + "fail. Status is " + status.getStatusCode());
            LOG.error("checkGAService", ReflectionToStringBuilder.toString(response));
        }
        CheckGAServiceResponse entity;
        try {
            entity = response.getEntity(CheckGAServiceResponse.class);
        } catch (ClientHandlerException | UniformInterfaceException e) {
            LOG.error("checkGAService", e);
            entity = new CheckGAServiceResponse();
            entity.setServiceStuts(99);
        }
        return entity;
    }
}
