package com.ah.ws.rest.server.resources.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ah.util.Tracer;
import com.ah.ws.rest.models.ExceptionModel;

@Provider
public class RootExceptionMapper implements ExceptionMapper<Exception> {

	private static final Tracer log	= new Tracer(RootExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(Exception exception) {
		log.error("RootExceptionMapper.toResponse()",
				"A RootException has occured, message :" + exception.getMessage(), exception);

		Status status = Status.INTERNAL_SERVER_ERROR;
		ExceptionModel entity = null;

		if (exception instanceof WebApplicationException) {
			WebApplicationException webEx = (WebApplicationException) exception;

			status = Status.fromStatusCode(webEx.getResponse().getStatus());
			entity = new ExceptionModel(status, webEx.getMessage());
		} else {
			entity = new ExceptionModel(status, exception.getMessage());
		}

		return Response
				.status(status)
				.entity(entity).build();
	}
}