package com.ah.ws.rest.server.resources.portal;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.models.ExceptionModel;
import com.ah.ws.rest.models.VhmLimitOperatorAccessSettings;
import com.ah.ws.rest.server.auth.exception.ApplicationException;
import com.ah.ws.rest.server.bussiness.UsersBussiness;

@Path(value = "/portal")
public class RestUsersResource {

    private static final Tracer LOG = new Tracer(RestUsersResource.class.getSimpleName());
    
    private static final ApplicationException HTTP_404_RESPONSE = new ApplicationException(
            Status.NOT_FOUND, new ExceptionModel(Status.NOT_FOUND, "The requested resource is not available."));

    /**
     * Provide an API for Portal to remove user and user settings from HMOL
     * 
     */
    @DELETE
    @Path("/users")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response removeUsers(List<String> userEmails)
            throws ApplicationException {
        if (!NmsUtil.isHostedHMApplication()) {
            LOG.error("The API is only available for HMOL.");
            throw HTTP_404_RESPONSE;
        }
        if (userEmails == null || userEmails.isEmpty()) {
            LOG.error("The userEmails is null or empty");
            throw HTTP_404_RESPONSE;
        }
        
        LOG.info("removeUsers", "Request parameters: " + Arrays.toString(userEmails.toArray()));
        String msg;
        try {
        	UsersBussiness.removeUsersFromHmUser(userEmails);
        	UsersBussiness.removeUserSettings(userEmails);
        	msg = "revmove users successfully.";
			return Response.ok().entity(msg).build();
        } catch (Exception e) {
        	msg = "revmove users failed.";
            LOG.error("removeUsers, " + msg, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }
    
    /**
     * method for retrieve VHM limit operator access settings
     * 
     * @param vhmId
     * @param userEmail
     * @throws ApplicationException
     */
    @GET
    @Path("/limitOpAccessSettings")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response retrieveVhmLimitOperatorAccessSettings(
			@QueryParam("vhmId") String vhmId,
			@QueryParam("userEmail") String userEmail)
			throws ApplicationException {

    	String msg = "";
    	if (StringUtils.isEmpty(vhmId)) {
    		msg  = "Required parameter vhmId is null.";
            LOG.error("retrieveVhmLimitOperatorAccessInfos, " + msg);
            throw new ApplicationException(
                    Status.NOT_ACCEPTABLE, new ExceptionModel(Status.NOT_ACCEPTABLE, msg));
    	}
    	
    	VhmLimitOperatorAccessSettings limitOperatorAccess = new VhmLimitOperatorAccessSettings();
    	
    	// get all available items base on VHM-ID(SSIDs, local user groups for PPSK)
    	List<String> avaliableSsids = UsersBussiness.getVhmAvaliableSsids(vhmId);
    	List<String> avaliableLocalUserGroups = UsersBussiness.getVhmAvaliableLocalUserGroups(vhmId);
    	limitOperatorAccess.setAvaliableSsids(avaliableSsids);
    	limitOperatorAccess.setAvaliableLocalUserGroups(avaliableLocalUserGroups);
    	
    	/*
    	 *  get user already selected items before VHM tied to CID, 
    	 *  only first time after tied to CID, userEmail is be set, otherwise is null
    	 */
    	if (!StringUtils.isEmpty(userEmail)) {
    		List<String> userSelectedSsids = UsersBussiness.getUserSelectedSsids(vhmId, userEmail);
    		List<String> userSelectedLocalUserGroups = UsersBussiness.getUserSelectedLocalUserGroups(vhmId, userEmail);
    		limitOperatorAccess.setUserSelectedSsids(userSelectedSsids);
    		limitOperatorAccess.setUserSelectedLocalUserGroups(userSelectedLocalUserGroups);
    	}
    	
    	// get VHM mode
    	HmStartConfig startConfig = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.vhmID", vhmId);
    	if (startConfig != null) {
    		limitOperatorAccess.setMode(startConfig.getModeType());
    	}
    	
    	return Response.ok().entity(limitOperatorAccess).build();
	}
}
