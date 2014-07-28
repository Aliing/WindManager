package com.ah.ws.rest.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.ah.util.Tracer;
import com.ah.ws.rest.client.auth.BasicAuthFilter;
import com.ah.ws.rest.models.idm.IDMSalesforceAccount;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public class SalesforceResUtils extends BaseUtils implements PathConstant {

    private static final Tracer LOG = new Tracer(SalesforceResUtils.class.getSimpleName());

    private static SalesforceResUtils[] instances = new SalesforceResUtils[ALL];

    private static Map<String, SalesforceResUtils> idInstances = new HashMap<String, SalesforceResUtils>();

    public static String TRUE = "true";

    private SalesforceResUtils() {
        super(CLIENT_MODULE_SALESFORCE, NONE);
    }

    private SalesforceResUtils(String role) {
        super(CLIENT_MODULE_SALESFORCE, ROLE);
        setRole(role);
    }

    private SalesforceResUtils(byte[] secretkey) {
        super(CLIENT_MODULE_SALESFORCE, SKEY);
        setSkey(secretkey);
    }

    private SalesforceResUtils(String role, byte[] secretkey) {
        super(CLIENT_MODULE_SALESFORCE, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    private SalesforceResUtils(String id, String role, byte[] secretkey) {
        super(id, CLIENT_MODULE_SALESFORCE, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    public static SalesforceResUtils getInstance() {
        if (instances[NONE] == null || !instances[NONE].isMatchType(NONE)) {
            instances[NONE] = new SalesforceResUtils();
        }
        return instances[NONE];
    }

    public static SalesforceResUtils getInstance(String role) {
        if (instances[ROLE] == null || !instances[ROLE].isMatchType(ROLE)) {
            instances[ROLE] = new SalesforceResUtils(role);
        }
        instances[ROLE].setRole(role);
        return instances[ROLE];
    }

    public static SalesforceResUtils getInstance(byte[] secretkey) {
        if (instances[SKEY] == null || !instances[SKEY].isMatchType(SKEY)) {
            instances[SKEY] = new SalesforceResUtils(secretkey);
        }
        instances[SKEY].setSkey(secretkey);
        return instances[SKEY];
    }

    public static SalesforceResUtils getInstance(String role, byte[] secretkey) {
        if (instances[ROLE_SKEY] == null || !instances[ROLE_SKEY].isMatchType(ROLE_SKEY)) {
            instances[ROLE_SKEY] = new SalesforceResUtils(role, secretkey);
        }
        instances[ROLE_SKEY].setRole(role);instances[ROLE_SKEY].setSkey(secretkey);
        return instances[ROLE_SKEY];
    }

    public static SalesforceResUtils getNewInstance(String role, byte[] secretkey) {
        String id = role;
        if (StringUtils.isBlank(id)) {
            return getInstance(role, secretkey);
        }

        SalesforceResUtils inst = null;
        if (!idInstances.containsKey(id)) {
            inst = new SalesforceResUtils(id, role, secretkey);
            idInstances.put(id, inst);
        } else {
            inst = idInstances.get(id);
        }
        inst.setRole(role);inst.setSkey(secretkey);

        return inst;
    }


    public synchronized boolean createCustomer(String salesforeceURi, IDMSalesforceAccount customer) {

        boolean flag = false;
        String msg = "Call Salesforce API: create customer with IDM service on Salesforce";
        
        ClientResponse response = getHttpClient()
                .resource(UriBuilder.fromUri(salesforeceURi).build())
                .accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_XML)
                .put(ClientResponse.class, customer);

        Status status = response.getClientResponseStatus();
        if (status == Status.OK) {
            //===============for temp integration test======start=====
            String entity = response.getEntity(String.class);
            if (!StringUtils.isEmpty(entity) 
                    && (entity.indexOf("<success>true</success>") > 0
                    || entity.indexOf("<password>") > 0)) {
                msg += " successfully. Result=" + entity +" >> ";
                LOG.info("createCustomer", msg + ReflectionToStringBuilder.toString(customer));
                flag = true;
            } else {
                msg += " failed. " + entity;
                LOG.error("createCustomer", msg + ReflectionToStringBuilder.toString(customer));
            }
            //===============for temp integration test======end=====
            
            /*
            IDMSalesforceResponse result = response.getEntity(IDMSalesforceResponse.class);
            if (TRUE.equalsIgnoreCase(result.getSuccess())) {
                msg += " successfully.";
                LOG.info("createCustomer", msg);
                flag = true;
            } else {
                msg += " failed.";
                LOG.error("createCustomer", msg + ReflectionToStringBuilder.toString(customer));
            }
            */
        } else {
            String entity = response.getEntity(String.class);
            msg += " failed. Status: " + status + (StringUtils.isNotBlank(entity) ? ", Description: " +  entity : "");
            LOG.error("createCustomer", msg + ReflectionToStringBuilder.toString(customer));
        }
        return flag;
    }
    
	public static void main(String[] args) {
		
		String username = "aerohive-JCPR80";
		String password = "32ae9766-ea60-4e3c-998e-c1706bbb8d2a";
		String boomiserver = "https://connect.boomi.com/ws/simple/createIDMAccount2QA";
		
		String datetime = "hmol20140226"; // please make sure this is a unique string
		SalesforceResUtils salesforceRes = SalesforceResUtils.getInstance(
				username, password.getBytes(BasicAuthFilter.CHARACTER_SET));
		IDMSalesforceAccount customer = new IDMSalesforceAccount();
		customer.setName("xtong " + datetime + " IDM");
		customer.setIndustry("xtong+industry");
		customer.setFirstName("xtong+firstname");
		customer.setLastName("xtong+lastname");
//			customer.setPrimaryEmail("xtonghot+" + datetime + "@gmail.com");
		customer.setCurrentAdminEmail("xtonghot+" + datetime + "008@gmail.com");
		customer.setPhone("916-333-5555");
		customer.setTitle("xtong+title");
		customer.setState("WA");
		customer.setCountry("US");
		
		List<String> emails = new ArrayList<>();
		emails.add("xtonghot+" + datetime + "@gmail.com");
		emails.add("xtonghot+" + datetime + "02@gmail.com");
		customer.setEmails(emails);
//		customer.setCid("0000002631");
		
		// entitlment
		customer.setTotalUsers(50);
		customer.setTotalSMSBoughtLifeTime(3650);
		customer.setTotalSMSUsedLifeTime(0);
		customer.setSubscriptionStartDate("20130502 000000.000");
		customer.setSubscriptionEndDate("20140502 000000.000");
		customer.setDirectoryIntegration(true);
		customer.setDomain("");
		
		try {
			salesforceRes.createCustomer(boomiserver, customer);
		} catch (Exception e) {
			System.out.println("createCustomerWithIdm failed."+ e);
			LOG.error("createCustomerAndUserOnIdm", "create customer failed.", e);
		}
	}
}
