package com.ah.be.rest.server.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.ah.be.rest.server.business.IRestConstants;
import com.ah.be.rest.server.business.RestMACAuthService;
import com.ah.be.rest.server.exception.RestBaseException;
import com.ah.be.rest.server.models.MACAuthModel;
import com.ah.be.rest.server.models.ResultStatus;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

@Path("/macauth")
public class RestMACAuthResource extends BaseResource implements IRestConstants {

	private static final Tracer log = new Tracer(RestMACAuthResource.class.getSimpleName());

	@POST @Path("/singledelete")
	@Consumes({"application/xml,text/plain,text/xml"})
	@Produces("application/xml")
	public String singleDelete(@Context HttpServletRequest request, String postContent){
		String operation = OPERATION_MACAUTH_SINGLEDELETE;
		try{
			XStream xStream = new XStream();
			xStream.processAnnotations(MACAuthModel.class);
			MACAuthModel macAuthModels =  (MACAuthModel)xStream.fromXML(postContent);
			ResultStatus resultStatus = RestMACAuthService.singleDelete(macAuthModels, operation);
			return returnPresentation(ResultStatus.class, resultStatus);
		}catch(RestBaseException exception){
			return returnPresentation(ResultStatus.class, exception.getResultStatus());
		}
	}
	
	@POST @Path("/bulkdelete")
	@Consumes({"application/xml,text/plain,text/xml"})
	@Produces("application/xml")
	public String bulkDelete(@Context HttpServletRequest request, String postContent){
		String operation = OPERATION_MACAUTH_BULKDELETE;
		try{
			XStream xStream = new XStream();
			xStream.processAnnotations(MACAuthModel.class);
			List<MACAuthModel> macAuthModels =  (ArrayList<MACAuthModel>)xStream.fromXML(postContent);
			ResultStatus resultStatus = RestMACAuthService.bulkDelete(macAuthModels, operation);
			return returnPresentation(ResultStatus.class, resultStatus);
		}catch(RestBaseException exception){
			return returnPresentation(ResultStatus.class, exception.getResultStatus());
		}
	}

	@POST @Path("/bulkupsert")
	@Consumes({"application/xml,text/plain,text/xml"})
	@Produces("application/xml")
	public String blukUpsert(@Context HttpServletRequest request, String postContent) {
		String operation = OPERATION_MACAUTH_BULKUPSERT;
		try{
			XStream xStream = new XStream();
			xStream.processAnnotations(MACAuthModel.class);
			List<MACAuthModel> macAuthModels =  (ArrayList<MACAuthModel>)xStream.fromXML(postContent);
			ResultStatus resultStatus = RestMACAuthService.bulkUpsert(macAuthModels, operation);
			return returnPresentation(ResultStatus.class, resultStatus);
		}catch(XStreamException e){
			log.error("RestMACAuthResource","blukUpsert",e);
			return returnPresentation(ResultStatus.class, new ResultStatus(operation, ERROR, RESULT_ERROR_XMLPARSEERROR, 0));
		}catch(RestBaseException exception){
			return returnPresentation(ResultStatus.class, exception.getResultStatus());
		}
	}

	@POST @Path("/singleupsert")
	@Consumes({"application/xml,text/plain,text/xml"})
	@Produces("application/xml")
	public String singleUpsert(@Context HttpServletRequest request, String postContent) {
		String operation = OPERATION_MACAUTH_SINGLEUPSERT;
		try{
			XStream xStream = new XStream();
			xStream.processAnnotations(MACAuthModel.class);
			MACAuthModel macAuthModel =  (MACAuthModel)xStream.fromXML(postContent);
			ResultStatus resultStatus = RestMACAuthService.singleUpsert(macAuthModel, operation);
			return returnPresentation(ResultStatus.class, resultStatus);
		}catch(XStreamException e){
			log.error("RestMACAuthResource","singleUpsert",e);
			return returnPresentation(ResultStatus.class, new ResultStatus(operation, ERROR, RESULT_ERROR_XMLPARSEERROR, 0));
		}catch(RestBaseException exception){
			return returnPresentation(ResultStatus.class, exception.getResultStatus());
		}
	}

}