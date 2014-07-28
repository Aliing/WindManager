package com.ah.be.rest.server.exception;

import com.ah.be.rest.server.models.ResultStatus;

public class HmException extends RestBaseException {

	private static final long serialVersionUID = -5470162971281283640L;

	public HmException(){

	}

	public HmException(ResultStatus resultStatus){
		this.resultStatus = resultStatus;
	}
}
