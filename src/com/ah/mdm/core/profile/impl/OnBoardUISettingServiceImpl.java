package com.ah.mdm.core.profile.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.mdm.core.profile.entity.OnBoardUIInfo;
import com.ah.mdm.core.profile.service.OnBoardUISettingService;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.Tracer;

public class OnBoardUISettingServiceImpl implements OnBoardUISettingService {

	private static final Tracer logger = new Tracer(
			OnBoardUISettingServiceImpl.class.getSimpleName());
	public static final String PREIVEW_URL = "/api/hm/onboardui/preview";
	public static final String QUERY_URL = "/api/hm/onboardui/query";
	public static final String CUSTOMIZE_URL = "/api/hm/onboardui/customize";
	public static final String RESET_URL = "/api/hm/onboardui/reset";

	private HttpClient client = null;

	// private String getHostName(){
	// String hostName =
	// ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM,
	// ConfigUtil.KEY_URL_ROOT_PATH);
	// return hostName+REST_URL;
	// }
	@Override
	public OnBoardUIInfo getOnBoardUIPage(String customId) {
		OnBoardUIInfo uiObject = new OnBoardUIInfo();
		if (customId == null) {
			customId = "home";
		}
		boolean result = false;
		try {
			String uri = CertificateGenSV.getUrl() + QUERY_URL;
			client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(uri,
					getQueryPostStr(customId), client);
			if (responseModel == null) {
				throw new Exception();
			}
			if (HttpStatus.SC_OK == responseModel.getResponseCode()) {
				uiObject=parseToOnBoardUI(responseModel);
				result = true;
			}
			if (HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel
					.getResponseCode()) {
				logger.error("Can't find the server");

			}
		} catch (Exception e) {
			logger.error(OnBoardUISettingServiceImpl.class.getSimpleName(),
					"Failed response from MDM");
		}
		return uiObject;
	}

	private OnBoardUIInfo parseToOnBoardUI(ResponseModel responseModel) {
		OnBoardUIInfo onboardUI = new OnBoardUIInfo();
		String resultStr = responseModel.getResponseText();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					resultStr.getBytes());

			byte[] inOutb = new byte[in.available()];
			in.read(inOutb);
			in.close();
			Document document = null;
			SAXReader reader = new SAXReader();
			ByteArrayInputStream is = new ByteArrayInputStream(inOutb);
			document = reader.read(is);
			Element rootElm = document.getRootElement();
			Element pageInfoElm = rootElm.element("PageInfo");

			Element logoImageElm = pageInfoElm.element("LogoImage");
			String logoImageStr = logoImageElm.getText();
			onboardUI.setLogoImage(logoImageStr);
			
			Element horMainImageElm = pageInfoElm.element("HorMainImage");
			String horMainImageStr = horMainImageElm.getText();
			onboardUI.setHorMainImage(horMainImageStr);
			
			onboardUI.setVerMainImage(pageInfoElm.element("VerMainImage").getText());
			
			onboardUI.setClientInfoTitle(parseBase64ToStr(pageInfoElm.element("ClientInfoTitle").getText()));
			
			onboardUI.setUserNameLabel(parseBase64ToStr(pageInfoElm.element("UserNameLabel").getText()));
			
			onboardUI.setOwnerShipLabel(parseBase64ToStr(pageInfoElm.element("OwnerShipLabel").getText()));
			
			onboardUI.setCidLabel(parseBase64ToStr(pageInfoElm.element("CidLabel").getText()));
			
			onboardUI.setByodLabel(parseBase64ToStr(pageInfoElm.element("ByodLabel").getText()));
			
			onboardUI.setCidText(parseBase64ToStr(pageInfoElm.element("CidText").getText()));
			
			onboardUI.setByodText(parseBase64ToStr(pageInfoElm.element("ByodText").getText()));
			
			onboardUI.setAgreementText(parseBase64ToStr(pageInfoElm.element("AgreementText").getText()));
			
			onboardUI.setWelcomeText(parseBase64ToStr(pageInfoElm.element("WelcomeText").getText()));
			
			onboardUI.setTargetUrl(parseBase64ToStr(pageInfoElm.element("TargetUrl").getText()));
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return onboardUI;

	}
	
	private String parseBase64ToStr(String base64Str){
		return new String(Base64.decodeBase64(base64Str));
		
	}

	private String EncodeToBase64(String str){
		
		return Base64.encodeBase64String(str.getBytes());
		
	}
	
	private String getQueryPostStr(String customId) {
		String postStr = "<content version=\"" + this.getVersion() + "\">";
		postStr = postStr + "<CustomerId>" + customId + "</CustomerId>";
		postStr = postStr + "</content>";

		return postStr;

	}
	
	private String getResetPostStr(String customId) {
		String postStr = "<content version=\"" + this.getVersion() + "\">";
		postStr = postStr + "<CustomerId>" + customId + "</CustomerId>";
		postStr = postStr + "</content>";

		return postStr;

	}
	
	
	private String getCustomizePostStr(String customId,OnBoardUIInfo uiObject) {
		String postStr = "<content version=\"" + this.getVersion() + "\">";
		postStr = postStr + "<CustomerId>" + customId + "</CustomerId>";
		postStr = postStr + "<PageInfo>";
		postStr = postStr + "<LogoImage>"+uiObject.getLogoImage()+"</LogoImage>";
		postStr = postStr + "<HorMainImage>"+uiObject.getHorMainImage()+"</HorMainImage>";
		postStr = postStr + "<VerMainImage>"+uiObject.getVerMainImage()+"</VerMainImage>";
		//decode to base64
		postStr = postStr + "<ClientInfoTitle>"+EncodeToBase64(uiObject.getClientInfoTitle())+"</ClientInfoTitle>";
		postStr = postStr + "<UserNameLabel>"+EncodeToBase64(uiObject.getUserNameLabel())+"</UserNameLabel>";
		postStr = postStr + "<OwnerShipLabel>"+EncodeToBase64(uiObject.getOwnerShipLabel())+"</OwnerShipLabel>";
		postStr = postStr + "<CidLabel>"+EncodeToBase64(uiObject.getCidLabel())+"</CidLabel>";
		postStr = postStr + "<ByodLabel>"+EncodeToBase64(uiObject.getByodLabel())+"</ByodLabel>";
		postStr = postStr + "<CidText>"+EncodeToBase64(uiObject.getCidText())+"</CidText>";
		postStr = postStr + "<ByodText>"+EncodeToBase64(uiObject.getByodText())+"</ByodText>";
		postStr = postStr + "<AgreementText>"+EncodeToBase64(uiObject.getAgreementText())+"</AgreementText>";
		postStr = postStr + "<WelcomeText>"+EncodeToBase64(uiObject.getWelcomeText())+"</WelcomeText>";
		postStr = postStr + "<TargetUrl>"+EncodeToBase64(uiObject.getTargetUrl())+"</TargetUrl>";
		postStr = postStr + "</PageInfo>";
		postStr = postStr + "</content>";

		return postStr;
	}

	private String getPreviewPostStr(String customId,String pageName,OnBoardUIInfo uiObject) {
		String postStr = "<content version=\"" + this.getVersion() + "\">";
		postStr = postStr + "<CustomerId>" + customId + "</CustomerId>";
		postStr = postStr + "<PageIdentifier>" + pageName + "</PageIdentifier>";
		postStr = postStr + "<PageInfo>";
		postStr = postStr + "<LogoImage>"+uiObject.getLogoImage()+"</LogoImage>";
		postStr = postStr + "<HorMainImage>"+uiObject.getHorMainImage()+"</HorMainImage>";
		postStr = postStr + "<VerMainImage>"+uiObject.getVerMainImage()+"</VerMainImage>";
		
		postStr = postStr + "<ClientInfoTitle>"+EncodeToBase64(uiObject.getClientInfoTitle())+"</ClientInfoTitle>";
		postStr = postStr + "<UserNameLabel>"+EncodeToBase64(uiObject.getUserNameLabel())+"</UserNameLabel>";
		postStr = postStr + "<OwnerShipLabel>"+EncodeToBase64(uiObject.getOwnerShipLabel())+"</OwnerShipLabel>";
		postStr = postStr + "<CidLabel>"+EncodeToBase64(uiObject.getCidLabel())+"</CidLabel>";
		postStr = postStr + "<ByodLabel>"+EncodeToBase64(uiObject.getByodLabel())+"</ByodLabel>";
		postStr = postStr + "<CidText>"+EncodeToBase64(uiObject.getCidText())+"</CidText>";
		postStr = postStr + "<ByodText>"+EncodeToBase64(uiObject.getByodText())+"</ByodText>";
		postStr = postStr + "<AgreementText>"+EncodeToBase64(uiObject.getAgreementText())+"</AgreementText>";
		postStr = postStr + "<WelcomeText>"+EncodeToBase64(uiObject.getWelcomeText())+"</WelcomeText>";
		postStr = postStr + "<TargetUrl>"+EncodeToBase64(uiObject.getTargetUrl())+"</TargetUrl>";
		postStr = postStr + "</PageInfo>";
		postStr = postStr + "</content>";

		return postStr;
	}
	
	private String getVersion() {
		// String hostName =
		// ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM,
		// ConfigUtil.KEY_URL_ROOT_PATH);
		return "1.0";// TODO
	}

	@Override
	public boolean customizedOnBoardUIPage(String customId,OnBoardUIInfo uiObject) {
		boolean result = false;
		if (customId == null) {
			customId = "home";
		}
		try {
			String uri = CertificateGenSV.getUrl() + CUSTOMIZE_URL;
			client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(uri,
					getCustomizePostStr(customId,uiObject), client);
			if (responseModel == null) {
				throw new Exception();
			}
			if (HttpStatus.SC_OK == responseModel.getResponseCode()) {
				result = true;
			}
			if (HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel
					.getResponseCode()) {
				logger.error("Can't find the server");
				result = false;
			}
		} catch (Exception e) {
			logger.error(OnBoardUISettingServiceImpl.class.getSimpleName(),
					"Failed response from MDM");
			result = false;
		}
	return result;
	}

	@Override
	public boolean resetOnBoardUIPage(String customId) {
		if (customId == null) {
			customId = "home";
		}
		boolean result = false;
		try {
			String uri = CertificateGenSV.getUrl() + RESET_URL;
			client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(uri,
					getResetPostStr(customId), client);
			if (responseModel == null) {
				throw new Exception();
			}
			if (HttpStatus.SC_OK == responseModel.getResponseCode()) {
				result = true;
			}
			if (HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel
					.getResponseCode()) {
				logger.error("Can't find the server");

			}
		} catch (Exception e) {
			logger.error(OnBoardUISettingServiceImpl.class.getSimpleName(),
					"Failed response from MDM");
		}
		return result;
	}

	@Override
	public String previewOnBoardUIPage(String customId,String pageName,OnBoardUIInfo uiObject) {
		String htmlResult = "";
		if (customId == null) {
			customId = "home";
		}
		try {
			String uri = CertificateGenSV.getUrl() + PREIVEW_URL;
			client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(uri,
					getPreviewPostStr(customId,pageName,uiObject), client);
			if (responseModel == null) {
				throw new Exception();
			}
			if (HttpStatus.SC_OK == responseModel.getResponseCode()) {
				htmlResult=responseModel.getResponseText();
				//result = "";
			}
			if (HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel
					.getResponseCode()) {
				logger.error("Can't find the server");
				htmlResult = "";
			}
		} catch (Exception e) {
			logger.error(OnBoardUISettingServiceImpl.class.getSimpleName(),
					"Failed response from MDM");
			htmlResult = "";
		}
	return htmlResult;
	}

}
