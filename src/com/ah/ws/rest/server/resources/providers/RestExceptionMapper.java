package com.ah.ws.rest.server.resources.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ah.util.Tracer;
import com.ah.ws.rest.server.auth.exception.RestException;

@Provider
public class RestExceptionMapper implements ExceptionMapper<RestException> {

	private static final Tracer log	= new Tracer(RestExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(RestException exception) {
		log.error("RestExceptionMapper.toResponse()",
				"A RestException has occured, message :" + exception.getMessage(), exception);
		return Response.status(exception.getStatus()).entity(exception.getEntity()).build();
	}
}