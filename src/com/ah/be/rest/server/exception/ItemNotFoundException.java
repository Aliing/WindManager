package com.ah.be.rest.server.exception;

import com.ah.be.rest.server.models.ResultStatus;

public class ItemNotFoundException extends RestBaseException {

	private static final long serialVersionUID = -8446210660222717306L;

	public ItemNotFoundException(){

	}

	public ItemNotFoundException(ResultStatus resultStatus){
		this.resultStatus = resultStatus;
	}
}
