package com.ah.ui.actions.home.clientManagement.service;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.ui.actions.home.clientManagement.entity.OnBoardCaEBO;
import com.ah.ui.actions.home.clientManagement.entity.SignedCertificateEBO;

public interface ICertificateGenSV {
	
	public String transEnableClientManageToXML(String customId,String clientManagementStatus);
	
	public String transCsrCertificateToXML(String customerId,String certType,String csrPayload);
	
	public OnBoardCaEBO transResponseToOnBoardCa(ResponseModel responseModel);
	
	public SignedCertificateEBO transResponseToSignedCertificate(ResponseModel responseModel);
	
	

}
