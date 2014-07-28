package com.ah.be.rest.ahmdm.server.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateRequest;
import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateResponse;
import com.ah.be.rest.ahmdm.server.service.MdmStatusUpdateService;
import com.ah.be.rest.ahmdm.server.service.MdmStatusUpdateServiceImpl;
import com.ah.be.rest.server.business.IRestConstants;
import com.ah.be.rest.server.resources.BaseResource;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;

@Path("/mdm")
public class MdmStatusUpdateResource extends BaseResource implements IRestConstants {

	private static final Tracer log = new Tracer(MdmStatusUpdateResource.class.getSimpleName());
	private MdmStatusUpdateService mdmStatusUpdateService = new MdmStatusUpdateServiceImpl();
	
	public MdmStatusUpdateService getMdmStatusUpdateService() {
		return mdmStatusUpdateService;
	}

	public void setMdmStatusUpdateService(MdmStatusUpdateService mdmStatusUpdateService) {
		this.mdmStatusUpdateService = mdmStatusUpdateService;
	}

	@POST @Path("/status/update")
	@Consumes("application/xml")
	@Produces("application/text")
	public String updateStatus(@Context HttpServletRequest request, String postContent){
		MdmStatusUpdateResponse resultStatus = null;
		try {
			XStream xStream = new XStream();
			xStream.processAnnotations(MdmStatusUpdateRequest.class);
			MdmStatusUpdateRequest req =  (MdmStatusUpdateRequest)xStream.fromXML(postContent);
			
			resultStatus = mdmStatusUpdateService.updateStatus(req);
//		} catch (XStreamException xse) {
//			return "Fail to deserialize" + xse.getMessage();
		} catch (Exception exception) {
			log.error(exception);
			resultStatus = new MdmStatusUpdateResponse();
			resultStatus.setResultCode(MdmStatusUpdateResponse.RESULT_OTHER_FAILURE);
		}

		return returnPresentation(MdmStatusUpdateResponse.class, resultStatus);
	}
}
