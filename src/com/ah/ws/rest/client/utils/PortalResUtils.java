package com.ah.ws.rest.client.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilderException;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.values.BooleanMsgPair;
import com.ah.ws.rest.client.auth.BasicAuthFilter;
import com.ah.ws.rest.models.CreateUpdateUser;
import com.ah.ws.rest.models.CustomerUserInfo;
import com.ah.ws.rest.models.DeleteResetUser;
import com.ah.ws.rest.models.ExceptionModel;
import com.ah.ws.rest.models.ga.GuestAnalyticsRequestResponse;
import com.ah.ws.rest.models.ga.GuestAnalyticsRequst;
import com.ah.ws.rest.models.idm.IDMTrialSettings;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

public class PortalResUtils extends BaseUtils implements PathConstant {
    private static final Tracer LOG = new Tracer(PortalResUtils.class.getSimpleName());
    private static PortalResUtils[] instances = new PortalResUtils[ALL];
    private static Map<String, PortalResUtils> idInstances = new HashMap<String, PortalResUtils>();

    private PortalResUtils() {
        super(CLIENT_MODULE_PORTAL, NONE);
    }

    private PortalResUtils(String role) {
        super(CLIENT_MODULE_PORTAL, ROLE);
        setRole(role);
    }

    private PortalResUtils(byte[] secretkey) {
        super(CLIENT_MODULE_PORTAL, SKEY);
        setSkey(secretkey);
    }

    private PortalResUtils(String role, byte[] secretkey) {
        super(CLIENT_MODULE_PORTAL, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    private PortalResUtils(String id, String role, byte[] secretkey) {
        super(id, CLIENT_MODULE_PORTAL, ROLE_SKEY);
        setRole(role);setSkey(secretkey);
    }

    public static PortalResUtils getInstance() {
        if (instances[NONE] == null || !instances[NONE].isMatchType(NONE)) {
            instances[NONE] = new PortalResUtils();
        }
        return instances[NONE];
    }

    public static PortalResUtils getInstance(String role) {
        if (instances[ROLE] == null || !instances[ROLE].isMatchType(ROLE)) {
            instances[ROLE] = new PortalResUtils(role);
        }
        instances[ROLE].setRole(role);
        return instances[ROLE];
    }

    public static PortalResUtils getInstance(byte[] secretkey) {
        if (instances[SKEY] == null || !instances[SKEY].isMatchType(SKEY)) {
            instances[SKEY] = new PortalResUtils(secretkey);
        }
        instances[SKEY].setSkey(secretkey);
        return instances[SKEY];
    }

    public static PortalResUtils getInstance(String role, byte[] secretkey) {
        if (instances[ROLE_SKEY] == null || !instances[ROLE_SKEY].isMatchType(ROLE_SKEY)) {
            instances[ROLE_SKEY] = new PortalResUtils(role, secretkey);
        }
        instances[ROLE_SKEY].setRole(role);instances[ROLE_SKEY].setSkey(secretkey);
        return instances[ROLE_SKEY];
    }

    public static PortalResUtils getNewInstance(String role, byte[] secretkey) {
        String id = role;
        if (StringUtils.isBlank(id)) {
            return getInstance(role, secretkey);
        }

        PortalResUtils inst = null;
        if (!idInstances.containsKey(id)) {
            inst = new PortalResUtils(id, role, secretkey);
            idInstances.put(id, inst);
        } else {
            inst = idInstances.get(id);
        }
        inst.setRole(role);inst.setSkey(secretkey);

        return inst;
    }
    
    public synchronized VHMCustomerInfo getVHMCustomerInfo(String vhmIdOrvhmEmail) throws Exception {
        if(StringUtils.isBlank(vhmIdOrvhmEmail)) {
            LOG.error("getVHMCustomerInfo",
                    "Call Portal API: get the vHM customer: " + vhmIdOrvhmEmail+ " information fail.");
            return null;
        }
        
        ClientResponse response;
        String msgText = "Call Portal API: get the vHM customer information successfully.";
        try {
        	response = getHttpClient()
                    .resource(getUri().path(POR_VHM_CUSTOMERINFORMATION_PATH).build())
                    .path(vhmIdOrvhmEmail)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);
        } catch (Exception ex) {
        	// debug info for CFD-301 begin
            String portalUrl = BaseUtils.REST_PATHS.get(CLIENT_MODULE_PORTAL);
            LOG.error("getVHMCustomerInfo", "Call Portal API: get the portal url: "+portalUrl);
            InetAddress address = InetAddress.getByName(portalUrl.substring(8, portalUrl.lastIndexOf(":443")));
            if (null != address) {
            	LOG.error("getVHMCustomerInfo", "Call Portal API: get the portal IP: "+address.getHostAddress());
            }
            // debug info for CFD-301 end
            msgText = "Call Portal API: get the vHM customer: "+ vhmIdOrvhmEmail 
                    +" information fail. " + ex.getMessage();
        	throw new Exception(msgText);
        }

        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            msgText = "Call Portal API: get the vHM customer: "+ vhmIdOrvhmEmail 
                    +" information fail. Status=" + status + ", " + response.getEntity(String.class);
            LOG.error("getVHMCustomerInfo", msgText);
            return null;
        }
        return response.getEntity(VHMCustomerInfo.class);
    }
    
    public synchronized BooleanMsgPair updateVHMCustomerInfo(VHMCustomerInfo customerInfo) {
        BooleanMsgPair msg;
        String msgText = "";
        try {
            if(null == customerInfo) {
                msgText = "Call Portal API: Update the Customer information failure. The customer information is Null" ;
                LOG.error("updateVHMCustomerInfo", msgText);
                msg = new BooleanMsgPair(false, msgText);
            } else {
                ClientResponse response = getHttpClient()
                        .resource(getUri().path(POR_VHM_CUSTOMERINFORMATION_PATH).build())
                        .type(MediaType.APPLICATION_JSON)
                        .put(ClientResponse.class, customerInfo);
                Status status = response.getClientResponseStatus();
                if (status == Status.OK) {
                    msgText = "Call Portal API: Update the Customer information successfully.";
                    LOG.info("updateVHMCustomerInfo", msgText);
                    msg = new BooleanMsgPair(true, msgText);
                } else {
                    msgText = "Call Portal API: Update the Customer information failure. " + response.getEntity(String.class);
                    LOG.error("updateVHMCustomerInfo", msgText);
                    msg = new BooleanMsgPair(false, msgText);
                }
            }
        } catch (UniformInterfaceException | ClientHandlerException
                | IllegalArgumentException | UriBuilderException e) {
            LOG.error("updateVHMCustomerInfo", "Call Portal API: Update VHM Customer " + customerInfo.getEmail() + " error.", e);
            msg = new BooleanMsgPair(false, e.getMessage());
        }
        return msg;
    }
    
    public synchronized IDMTrialSettings getIDMTrialSettings() throws Exception {
        ClientResponse response = getHttpClient()
                .resource(getUri().path(POR_IDM_TRIAL_SETTINGS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        String msgText = "Call Portal API: get the trial settings successfully.";
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            msgText = "Call Portal API: get the trial settings fail. " + response.getEntity(String.class);
            LOG.error("getIDMTrialSettings", msgText);
            return null;
        }
        return response.getEntity(IDMTrialSettings.class);
    }

    // request from HMOL
	public final static short OPT_TYPE_VHM = 2;

	/**
	 * get user under specified group from Portal
	 * 
	 * @param customerId
	 * @param productId
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public List<CustomerUserInfo> getVHMUsersByGroupName(
			String customerId, String productId, String groupName)
			throws Exception {

		GenericType<List<CustomerUserInfo>> genericType = new GenericType<List<CustomerUserInfo>>() {
		};

/*		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.put("optType", String.valueOf(OPT_TYPE_VHM));
		queryParams.put("customerId", customerId);
		queryParams.put("productId", productId);
		queryParams.put("groupName", groupName);*/
		Log.info("getVHMUsersByGroupName, Query params: optType="
				+ OPT_TYPE_VHM + ", customerId=" + customerId + ", productId="
				+ productId + ", groupName=" + groupName);
		
		ClientResponse response = getHttpClient()
				.resource(getUri().path(POR_VHM_USERS_ON_PORTAL_PATH).build())
				.queryParam("optType", String.valueOf(OPT_TYPE_VHM))
				.queryParam("customerId", customerId)
				.queryParam("productId", productId)
				.queryParam("groupName", groupName)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);

		String msg = "Call Portal API: get users from Portal";
		Status status = response.getClientResponseStatus();
		String statusCode = "";
		if (status == null) {
			msg += " failed. Portal's response status is null.";
			LOG.error("getVHMUsersByGroupName", msg);
			throw new Exception(msg);
		} else {
			statusCode = status.getStatusCode() + " (" +status.getReasonPhrase() + ")";
			LOG.info("getVHMUsersByGroupName", "Response status code : " + statusCode);
		}
		if (status != Status.OK) {
			ExceptionModel exceptionModel = getException(response);
        	msg += " failed. " + exceptionModel.getMessage();
            LOG.error("getVHMUsersByGroupName", msg);
			return null;
		}
		List<CustomerUserInfo> userList = response.getEntity(genericType);
		msg += " successfully.";
		LOG.info("getVHMUsersByGroupName", msg + " result users count is " + (userList != null ? userList.size() : 0));
		return userList;
	}
	
	public void createUserOnPortal(CreateUpdateUser users) throws Exception {

		String msg = "Call Portal API: Create user";
		LOG.info("createUserOnPortal", "Request params: " + users.toString());
        ClientResponse response = getHttpClient()
                .resource(getUri().path(POR_VHM_USERS_ON_PORTAL_PATH).build())
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, users);
        Status status = response.getClientResponseStatus();
		String statusCode = "";
		if (status == null) {
			msg += " failed. Portal's response status is null.";
			LOG.error("createUserOnPortal", msg);
			throw new Exception(msg);
		} else {
			statusCode = status.getStatusCode() + " (" +status.getReasonPhrase() + ")";
			LOG.info("createUserOnPortal", "Response status code : " + statusCode);
		}
        if (status == Status.OK) {
        	msg += " successfully.";
            LOG.info("createUserOnPortal", msg);
        } else {
			ExceptionModel exceptionModel = getException(response);
        	msg += " failed. " + exceptionModel.getMessage();
            LOG.error("createUserOnPortal", msg);
			throw new Exception(exceptionModel.getMessage());
        }
	}
	
	public void removeUserOnPortal(DeleteResetUser deleteUsers) throws Exception {
		String msg = "Call Portal API: Remove user";
		LOG.info("removeUserOnPortal", "Request params: " + deleteUsers.toString());
        ClientResponse response = getHttpClient()
                .resource(getUri().path(POR_VHM_USERS_ON_PORTAL_PATH).build())
                .type(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class, deleteUsers);
        Status status = response.getClientResponseStatus();
		String statusCode = "";
		if (status == null) {
			msg += " failed. Portal's response status is null.";
			LOG.error("removeUserOnPortal", msg);
			throw new Exception(msg);
		} else {
			statusCode = status.getStatusCode() + " (" +status.getReasonPhrase() + ")";
			LOG.info("removeUserOnPortal", "Response status code : " + statusCode);
		}
        if (status == Status.OK) {
        	msg += " successfully.";
            LOG.info("removeUserOnPortal", msg);
        } else {
			ExceptionModel exceptionModel = getException(response);
        	msg += " failed. " + exceptionModel.getMessage();
            LOG.error("createUserOnPortal", msg);
			throw new Exception(exceptionModel.getMessage());
        }
	}
	
	public void createACMProductByCustomerId(String custId, String vhmId) throws Exception {
		String msg = "Create ACM product in Portal";
		if (StringUtils.isBlank(custId)) {
			msg += " failed. The param customer id cannot be null.";
			LOG.error("createACMProductByCustomerId", msg);
			throw new Exception(msg);
		}
		LOG.info("createACMProductByCustomerId", "Request params: " + custId);
        ClientResponse response = getHttpClient()
                .resource(getUri().path("api/customers/"+custId+"/acm").build())
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        Status status = response.getClientResponseStatus();
		String statusCode = "";
		if (status == null) {
			msg += " failed. Portal's response status is null.";
			LOG.error("createACMProductByCustomerId", msg);
			throw new Exception(msg);
		} else {
			statusCode = status.getStatusCode() + " (" +status.getReasonPhrase() + ")";
			LOG.info("createACMProductByCustomerId", "Response status code : " + statusCode);
		}
        if (status == Status.OK) {
        	msg += " successfully.";
            LOG.info("createACMProductByCustomerId", msg);
        } else {
        	ExceptionModel exceptionModel = null;
        	try {
        		exceptionModel = getException(response);
        	} catch (Exception ex) {
        		msg += " failed. The Portal does not support this feature.";
	            LOG.error("createACMProductByCustomerId", msg);
	            throw new Exception("The Portal does not support this feature.");
        	}
			if (null != exceptionModel) {
				if (status == Status.INTERNAL_SERVER_ERROR) {
					msg += " failed. " + exceptionModel.getInternalErrorCode();
		            LOG.error("createACMProductByCustomerId", msg);
					if (exceptionModel.getInternalErrorCode() == -8) {
		            	throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.enable.send.customer.id.to.client.management", new String[]{vhmId, custId}));
		            } else if (exceptionModel.getInternalErrorCode() == -2) {
		            	throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.enable.add.product.to.customer.exist", new String[]{vhmId, custId}));
		            } else {
		            	throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.enable..add.product.to.customer.exc", new String[]{vhmId, custId}));
		            }
				}
	        	msg += " failed. " + exceptionModel.getMessage();
	            LOG.error("createACMProductByCustomerId", msg);
	            throw new Exception(exceptionModel.getMessage());
			}
        }
	}
	
	public synchronized GuestAnalyticsRequestResponse toggleGuestAnalytics(String vhmId, String customerId, boolean enabled) {
	    GuestAnalyticsRequst requestBody = new GuestAnalyticsRequst();
	    requestBody.setVhmId(vhmId);
	    requestBody.setOperation(enabled ? 1 : 0);
	    
        ClientResponse response = getHttpClient()
                .resource(getUri().path(POR_GA_TOGGLE_PATH).build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, requestBody);
        
        String msgText = "Call Portal API: " + (enabled ? "enable" : "disable") + " the social analytics settings ";
        Status status = response.getClientResponseStatus();
        if (status == Status.OK) {
            LOG.info("toggleGuestAnalytics", msgText + "successfully.");
        } else {
            LOG.error("toggleGuestAnalytics", msgText + "fail.");
        }
        
        GuestAnalyticsRequestResponse entity;
        try {
            entity = response.getEntity(GuestAnalyticsRequestResponse.class);
        } catch (ClientHandlerException | UniformInterfaceException e) {
            LOG.error("toggleGuestAnalytics", e);
            entity = new GuestAnalyticsRequestResponse();
            entity.setStatus(status.getStatusCode());
            entity.setMessage("Unknow");
        }
        return entity;
	}
	
	public static void main(String[] args) {
		String username = "hmol@portal";
		String password = "aerohive";
		
		PortalResUtils portalResUtils = PortalResUtils.getInstance(
				username, password.getBytes(BasicAuthFilter.CHARACTER_SET));
		try {
			// get teacher users
//			portalResUtils.getVHMUsersByGroupName("3003003015", "VHM-GJ200F", "Teacher");
			
			// create teacher
			CreateUpdateUser users = new CreateUpdateUser();
			users.setOptType(PortalResUtils.OPT_TYPE_VHM);
			users.setCustomerId("3003003015");
			users.setProductId("VHM-GJ200F");
			
			List<CustomerUserInfo> userInfos = new ArrayList<>();
			CustomerUserInfo user = new CustomerUserInfo();
			user.setUserEmail("xtonghot+301571103@gmail.com");
			user.setUserName("xtonghot+301571103");
			user.setDescription("description description");
			user.setI18n(1);
//			user.setGmCss(gmCss);
			user.setTimezone("America/Los_Angeles");
			user.setDateFormat((short)0);
			user.setTimeFormat((short)0);
			user.setGroupName("Teacher");
			user.setDefaultFlag(false);
			userInfos.add(user);
			users.setUsers(userInfos);
			portalResUtils.createUserOnPortal(users);
			
			// delete teacher
/*			DeleteResetUser deleteUsers = new DeleteResetUser();
			deleteUsers.setOptType(PortalResUtils.OPT_TYPE_VHM);
			deleteUsers.setCustomerId("3003003015");
			deleteUsers.setProductId("VHM-GJ200F");
			
			List<String> userEmails = new ArrayList<>();
			userEmails.add("xtonghot+301571103@gmail.com");
			deleteUsers.setUserEmails(userEmails.toArray());
			portalResUtils.removeUserOnPortal(deleteUsers);*/
			
			portalResUtils.createACMProductByCustomerId("1234567890", "VHM-123456");
			
		} catch (Exception e) {
			System.out.println("getVHMUsersByGroupName failed."+ e);
		}
	}
}
