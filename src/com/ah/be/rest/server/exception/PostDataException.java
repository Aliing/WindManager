package com.ah.be.rest.server.exception;

import com.ah.be.rest.server.models.ResultStatus;

public class PostDataException extends RestBaseException {

	private static final long serialVersionUID = -4983223227640718916L;
	public PostDataException(){

	}

	public PostDataException(ResultStatus resultStatus){
		this.resultStatus = resultStatus;
	}
}
