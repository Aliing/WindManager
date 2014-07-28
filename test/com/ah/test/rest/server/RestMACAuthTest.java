package com.ah.test.rest.server;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.be.rest.server.models.MACAuthModel;
import com.ah.be.rest.server.models.ResultStatus;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class RestMACAuthTest {

	@Test
	public void createMACAuth() {
		ResultStatus resultStatus = new ResultStatus("Create MACAuth");
		try{
			String targetURL = "https://10.155.20.246/hm/rest/macAuth/create";
			XStream xStreamResponse = new XStream();
			xStreamResponse.processAnnotations(MACAuthModel.class);
			HttpClient client = new HttpClient();
			MACAuthModel macAuthModel = new MACAuthModel();
			macAuthModel.setMacAddress("tstmac");
			macAuthModel.setSchoolId("adf");
			macAuthModel.setStudentId("studentId");
			macAuthModel.setStudentName("studentName");
			String postStr = xStreamResponse.toXML(macAuthModel);
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, postStr, client);
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				String resultStr = responseModel.getResponseText();
				XStream xStreamRequest = new XStream();
				xStreamRequest.processAnnotations(ResultStatus.class);
				resultStatus = (ResultStatus)xStreamRequest.fromXML(resultStr);
			}			
		}catch(XStreamException exception){
			fail(exception.getMessage());
		}
		
		Assert.assertEquals("Success", resultStatus.getResultFlag());
	}
	
	@Test
	public void updateMACAuth() {
		ResultStatus resultStatus = new ResultStatus("Create MACAuth");
		try{
			String targetURL = "https://10.155.20.246/hm/rest/macAuth/update";
			XStream xStreamResponse = new XStream();
			xStreamResponse.processAnnotations(MACAuthModel.class);
			HttpClient client = new HttpClient();
			MACAuthModel macAuthModel = new MACAuthModel();
			macAuthModel.setMacAddress("tstmac");
			macAuthModel.setSchoolId("adf");
			macAuthModel.setStudentId("studentId");
			macAuthModel.setStudentName("studentName");
			String postStr = xStreamResponse.toXML(macAuthModel);
			ResponseModel responseModel = HttpToolkit.doPutXML(targetURL, postStr, client);
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				String resultStr = responseModel.getResponseText();
				XStream xStreamRequest = new XStream();
				xStreamRequest.processAnnotations(ResultStatus.class);
				resultStatus = (ResultStatus)xStreamRequest.fromXML(resultStr);
			}			
		}catch(XStreamException exception){
			fail(exception.getMessage());
		}
		
		Assert.assertEquals("Success", resultStatus.getResultFlag());
	}
	
	@Test
	public void deleteMACAuth() {
		ResultStatus resultStatus = new ResultStatus("Delete MACAuth");
		String targetURL = "https://10.155.20.246/hm/rest/macAuth/delete";
		try{
			List<MACAuthModel> macAuthModels = new ArrayList<MACAuthModel>();
			MACAuthModel macAuthModel1 = new MACAuthModel();
			macAuthModel1.setMacAddress("1");
			macAuthModel1.setSchoolId("1");
			macAuthModel1.setStudentId("1");
			macAuthModel1.setStudentName("1");
			
			MACAuthModel macAuthModel2 = new MACAuthModel();
			macAuthModel2.setMacAddress("2");
			macAuthModel2.setSchoolId("2");
			macAuthModel2.setStudentId("2");
			macAuthModel2.setStudentName("2");
			
			macAuthModels.add(macAuthModel1);
			macAuthModels.add(macAuthModel2);
			XStream xStreamRequest = new XStream();
			HttpClient client = new HttpClient();
			xStreamRequest.processAnnotations(MACAuthModel.class);
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, xStreamRequest.toXML(macAuthModels),client);
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				String resultStr = responseModel.getResponseText();
				XStream xStreamResponse = new XStream();
				xStreamResponse.processAnnotations(ResultStatus.class);
				resultStatus = (ResultStatus)xStreamResponse.fromXML(resultStr);
			}
			
		}catch(XStreamException exception){
			fail(exception.getMessage());
		}
		
		Assert.assertEquals("Success", resultStatus.getResultFlag());
	}
	
	
	@Test
	public void upsertMACAuth(){
		ResultStatus resultStatus = new ResultStatus("Upsert MACAuth");
		String targetURL = "https://10.155.20.246/hm/rest/macAuth/upsert";
		try{
			List<MACAuthModel> macAuthModels = new ArrayList<MACAuthModel>();
			MACAuthModel macAuthModel1 = new MACAuthModel();
			macAuthModel1.setMacAddress("1");
			macAuthModel1.setSchoolId("1");
			macAuthModel1.setStudentId("1");
			macAuthModel1.setStudentName("1");
			
			MACAuthModel macAuthModel2 = new MACAuthModel();
			macAuthModel2.setMacAddress("2");
			macAuthModel2.setSchoolId("2");
			macAuthModel2.setStudentId("2");
			macAuthModel2.setStudentName("2");
			
			macAuthModels.add(macAuthModel1);
			macAuthModels.add(macAuthModel2);
			XStream xStreamRequest = new XStream();
			xStreamRequest.processAnnotations(MACAuthModel.class);
			HttpClient client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, xStreamRequest.toXML(macAuthModels),client);
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				String resultStr = responseModel.getResponseText();
				XStream xStreamResponse = new XStream();
				xStreamResponse.processAnnotations(ResultStatus.class);
				resultStatus = (ResultStatus)xStreamResponse.fromXML(resultStr);
			}
			
		}catch(XStreamException exception){
			fail(exception.getMessage());
		}
		
		Assert.assertEquals("Success", resultStatus.getResultFlag());
	}


}
