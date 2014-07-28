package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.response.IllegalRequestResponse;

public class IllegalRequest extends AbstractRequest {

	/*
	 * This class is just used to deal with the requests which aren't able to be parsed/recognized by API-Engine.
	 */

	private static final long serialVersionUID = 1L;

	private String cause;

	public IllegalRequest() {
		super();
	}

	public IllegalRequest(String cause) {
		this.cause = cause;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return ILLEGAL_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "Illegal Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {

	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		IllegalRequestResponse response = new IllegalRequestResponse();

		return response.build(this);
	}

	@Override
	public void callback() {

	}

    //*********************************************************************************
    // Methods for API-Client to build request messages to API-Engine.
    //*********************************************************************************

}