package com.ah.be.rest.ahmdm.client;

import java.util.List;

import org.apache.commons.httpclient.Header;

import com.ah.be.rest.client.models.ResponseModel;

public interface IResponseFromMDM {
	
	public ResponseModel sendInfoToMDM(String uri,String XMLString) throws Exception;
	
	public ResponseModel sendInfoToMDM(String uri,String XMLString, List<Header> headers) throws Exception;
	
	public ResponseModel sendInfoToAcMWithAuth(String uri,String XMLString) throws Exception;

}
