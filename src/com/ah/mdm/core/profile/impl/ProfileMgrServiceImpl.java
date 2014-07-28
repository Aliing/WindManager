package com.ah.mdm.core.profile.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.Ostermiller.util.Base64;
import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.ConfigurationProfileInfo;
import com.ah.mdm.core.profile.entity.MdmObject;
import com.ah.mdm.core.profile.entity.ValidTimeInfo;
import com.ah.mdm.core.profile.payload.MobileConfigPayload;
import com.ah.mdm.core.profile.service.ProfileMgrService;
import com.ah.util.Tracer;

public class ProfileMgrServiceImpl implements ProfileMgrService
{
	private static final Tracer 				logger              = new Tracer(ProfileMgrServiceImpl.class.getSimpleName());
	public static final String					REST_URL 			= "/api/hm/profiles/";
	
	private String getHostName(){
		String hostName = ConfigUtil.getACMConfigServerUrl();
		return hostName+REST_URL;
	}
	
	private String getVersion(){
		String version = ConfigUtil.getVersion();
		return version;
	}
	
	private void fillToProfileItems(ConfigurationProfileInfo profile)
	{
		profile.getProfileInfoItems().clear();
		profile.getProfileInfoItems().addAll(profile.getAppLockProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getCalDavProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getCalendarSubscriptionProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getCardDavProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getCredentialsProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getEmailProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getExchangeProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getGlobalHttpProxyProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getIdentificationProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getLdapProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getScepProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getWebClipProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getWifiProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getApnProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getPasscodeProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getMdmProfileInfos());
		profile.getProfileInfoItems().addAll(profile.getVpnProfileInfos());
		if (profile.getRemovalPasscodeProfileInfo() != null)
		{
			profile.getProfileInfoItems().add(profile.getRemovalPasscodeProfileInfo());
		}
		profile.getProfileInfoItems().addAll(profile.getRestrictionsProfileInfos());

	}

	@Override
	public String generateXml(ConfigurationProfileInfo profile, boolean isEncrypted) throws Exception
	{
		if (profile == null)
		{
			throw new Exception("The profile does not exist! profile.save.parameter.invalid");
		}
		fillToProfileItems(profile);
		StringBuffer c = new MobileConfigPayload(profile).toPlist();
		logger.error(c.toString());
		String str = null;
		if (isEncrypted)
		{			
			try
			{
				str = Base64.encode(c.toString());
			} catch (Exception e)
			{
				logger.error("Exception occured: ", e);
			}
		} else
		{
			str = c.toString();
		}
		
		return str;
	}
	
	@Override
	public void getProfileInfoFromXml(InputStream in, ConfigurationProfileInfo profileInfo)
	{
		try
		{
			profileInfo = getConfigurationProfileInfoFromStream(in, profileInfo);
		}catch(Exception e){
			logger.error("Exception occured: ", e);
		}
	}
	
	private ConfigurationProfileInfo getConfigurationProfileInfoFromStream(InputStream in, ConfigurationProfileInfo profileInfo) throws Exception
	{
		byte[] inOutb = new byte[in.available()];
		in.read(inOutb);
		in.close();
		
		Document document = null;
		SAXReader reader = new SAXReader();
		ByteArrayInputStream is = new ByteArrayInputStream(inOutb);
		document = reader.read(is);
		Element e = (Element) document.selectNodes("/plist/dict").get(0);
		
		MobileConfigPayload payload = new MobileConfigPayload(profileInfo);
		payload.parse(e);
		
		fillFromProfileItems(profileInfo);
		
		return profileInfo;
	}
	
	/**
	 * Fill the profile info objects into each list from profileInfoItems
	 * 
	 * @param profileInfo
	 */
	private void fillFromProfileItems(ConfigurationProfileInfo profileInfo)
	{
		Field[] fields = profileInfo.getClass().getDeclaredFields();
		List<AbstractProfileInfo> items = profileInfo.getProfileInfoItems();
		for (AbstractProfileInfo abstractProfileInfo : items)
		{
			for (int i = 0; i < fields.length; i++)
			{	
				if (belongToChildProfileList(fields[i].getName(), abstractProfileInfo.getClass().getName()))
				{
					List<AbstractProfileInfo> list = getChildProfileListFromField(fields[i], profileInfo);
					list.add(abstractProfileInfo);
					break;
				}
				else
				{
					if(isTheSame(fields[i].getName(), abstractProfileInfo.getClass().getName()))
					{
						String fieldName = fields[i].getName();
						String setMethodName = "set" + (char) (fieldName.charAt(0) + 'A' - 'a') + fieldName.substring(1);
						
						try
						{
							Method setMethod = ConfigurationProfileInfo.class.getMethod(setMethodName, abstractProfileInfo.getClass());
							setMethod.invoke(profileInfo, abstractProfileInfo);
							break;
						} catch (Exception e)
						{
							logger.error("using set method error and can't fill the field in "+profileInfo.getDisplayName()+".",e);
						}
						
					}
					
				}
			}
		}
	}
	
	private boolean isTheSame(String field, String clazz)
	{
		if (field == null || clazz == null)
		{
			return false;
		}

		field = (char) (field.charAt(0) + 'A' - 'a') + field.substring(1);
		clazz = clazz.substring(clazz.lastIndexOf(".") + 1);
		return field.equals(clazz);
	}
	
	private boolean belongToChildProfileList(String field, String clazz)
	{
		if (field == null || clazz == null)
		{
			return false;
		}

		field = (char) (field.charAt(0) + 'A' - 'a') + field.substring(1, field.length() - 1);
		clazz = clazz.substring(clazz.lastIndexOf(".") + 1);
		return field.equals(clazz);
	}

	private List<AbstractProfileInfo> getChildProfileListFromField(Field field, ConfigurationProfileInfo profileInfo)
	{
		Class<?> clazz = profileInfo.getClass();
		String fieldName = field.getName();
		String methodName = "get" + (char) (fieldName.charAt(0) + 'A' - 'a') + fieldName.substring(1);
		Method getMethod = null;
		try
		{
			getMethod = clazz.getMethod(methodName);
		} catch (NoSuchMethodException e)
		{
			logger.error("Class " + clazz + " does not support this method: " + methodName, e);
		} catch (SecurityException e)
		{
			logger.error("SecurityException occured: ", e);
		}
		List<AbstractProfileInfo> profileInfoItems = null;
		try
		{
			if (getMethod != null)
			{
				profileInfoItems = (List<AbstractProfileInfo>) getMethod.invoke(profileInfo);
			}
		} catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException occured: ", e);
		} catch (IllegalArgumentException e)
		{
			logger.error("IllegalArgumentException occured: ", e);
		} catch (InvocationTargetException e)
		{
			logger.error("InvocationTargetException occured: ", e);
		}
		return profileInfoItems;
	}

	/**
	 * Add/update profiles 
	 */
	@Override
	public boolean setMdmProfile(ValidTimeInfo validTimeInfo,ConfigurationProfileInfo profile,String customId) 
	{
		boolean ret = false;
		if(customId == null){
			customId ="home";
		}
		try{
			String targetURL = this.getHostName() + "update";
			HttpClient client = new HttpClient();
			String postStr = "<content version=\""+ this.getVersion()+"\">";
			postStr = postStr +"<CustomerId>" + customId + "</CustomerId>";
			postStr = postStr + "<ProfileList>";
			postStr = postStr + "<Profile>";
			postStr = postStr + "<Rule type = \"" + validTimeInfo.getValidType() + "\">";
			if(1 == validTimeInfo.getValidType()){
				postStr = postStr + "<KeepTime>"+validTimeInfo.getKeepTime() + "</KeepTime>";
			}else if(2 == validTimeInfo.getValidType()){
				postStr = postStr + "<EffectiveTime start=\"" + validTimeInfo.getEffectiveStartTime() + "\" end=\"" + validTimeInfo.getEffectiveEndTime() + "\"/>";
			}
			postStr = postStr + "</Rule>";
			postStr = postStr + "<UPID>";
			postStr = postStr + profile.getUserProfileAttributeValue();
			postStr = postStr + "</UPID>";
			postStr = postStr + "<Data>";
			postStr = postStr + generateXml(profile, true);
			postStr = postStr + "</Data>";
			postStr = postStr + "</Profile>";
			postStr = postStr + "</ProfileList>";
			postStr = postStr + "</content>";
			
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, postStr, client);
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				ret = true;
				logger.info(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":setMdmProfile() method succeed");
			}else{
				logger.error(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":setMdmProfile() method failed");
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		return ret;
	}
	
	/**
	 * Remove profiles 
	 */
	@Override
	public boolean delMdmProfile(Set<String> profileNames,String customId) 
	{
		boolean ret = false;
		if(customId == null){
			customId ="home";
		}
		try{
			String targetURL = this.getHostName() + "remove";
			HttpClient client = new HttpClient();
			String postStr = "<content version=\""+ this.getVersion()+"\">";
			postStr = postStr +"<CustomerId>" + customId + "</CustomerId>";
			postStr = postStr + "<ProfileList>";
			for ( String profileName:profileNames){
				postStr = postStr + "<ProfileName>";
				postStr = postStr + profileName;
				postStr = postStr + "</ProfileName>";
			}
			postStr = postStr + "</ProfileList>";
			postStr = postStr + "</content>";
			
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, postStr, client);
 			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				ret = true;
				logger.info(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":delMdmProfile() method succeed");
			}else{
				logger.error(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":delMdmProfile() method failed");
			} 
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		return ret;
	}
	
	/**
	 * Get Profile From MDM
	 */
	@Override
	public MdmObject getMdmProfile(String profileName,String customId)
	{
		MdmObject object = new MdmObject();
		if(customId == null){
			customId ="home";
		}
		try{
			String targetURL = this.getHostName() + "query";
			HttpClient client = new HttpClient();
			String postStr = "<content version=\""+ this.getVersion()+"\">";
			postStr = postStr +"<CustomerId>" + customId + "</CustomerId>";
			postStr = postStr + "<ProfileList>";
			postStr = postStr + "<ProfileName>";
			postStr = postStr + profileName;
			postStr = postStr + "</ProfileName>";
			postStr = postStr + "</ProfileList>";
			postStr = postStr + "</content>";
			
			ResponseModel responseModel = HttpToolkit.doPostXML(targetURL, postStr, client);
 			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
 				String resultStr = responseModel.getResponseText();
 				ByteArrayInputStream in = new ByteArrayInputStream(resultStr.getBytes());
 				ConfigurationProfileInfo profileInfo = new ConfigurationProfileInfo();
 				ValidTimeInfo validTimeInfo = new ValidTimeInfo();
 			 					
				byte[] inOutb = new byte[in.available()];
				in.read(inOutb);
				in.close();
				Document document = null;
				SAXReader reader = new SAXReader();
				ByteArrayInputStream is = new ByteArrayInputStream(inOutb);
				document = reader.read(is);
				Element rootElm = document.getRootElement();
				Element profileElm = rootElm.element("ProfileList").element("Profile");
				
				String type = profileElm.element("Rule").attributeValue("type");
				validTimeInfo.setValidType(Integer.valueOf(type));
				Element keepTimeElm = profileElm.element("Rule").element("KeepTime");
				if(keepTimeElm != null){
					validTimeInfo.setKeepTime(keepTimeElm.getTextTrim());
				}
				Element EffectiveTimeElm = profileElm.element("Rule").element("EffectiveTime");
				if(EffectiveTimeElm != null){
					validTimeInfo.setEffectiveStartTime(EffectiveTimeElm.attributeValue("start"));
					validTimeInfo.setEffectiveEndTime(EffectiveTimeElm.attributeValue("end"));
				}
				
				String userProfileAttribute = profileElm.element("UPID").getTextTrim();	
				profileInfo.setUserProfileAttributeValue(Short.parseShort(userProfileAttribute));
 		 
				
 				String data = profileElm.element("Data").getTextTrim();	
 				getProfileInfoFromXml(new ByteArrayInputStream(Base64.decode(data).getBytes()),profileInfo);
 				
 				object.setConfigurationProfileInfo(profileInfo);
 				object.setValidTimeInfo(validTimeInfo); 		
 				
				logger.info(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":getMdmProfile() method succeed");
			}else{
				logger.error(ProfileMgrServiceImpl.class.getSimpleName()
						+ ":getMdmProfile() method failed");
			}

		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		} 
		return object;
	}
}