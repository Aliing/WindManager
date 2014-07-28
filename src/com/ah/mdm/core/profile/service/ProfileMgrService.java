package com.ah.mdm.core.profile.service;

import java.io.InputStream;
import java.util.Set;

import com.ah.mdm.core.profile.entity.ConfigurationProfileInfo;
import com.ah.mdm.core.profile.entity.MdmObject;
import com.ah.mdm.core.profile.entity.ValidTimeInfo;

public interface ProfileMgrService
{
	/**
	 * Generate the content using XML format from profile object
	 * 
	 * @param profile
	 *            device profile
	 * @param isEncrypted
	 *            the return result whether is encrypted
	 * @return
	 * @throws BusinessException
	 */
	public String generateXml(ConfigurationProfileInfo profile, boolean isEncrypted) throws Exception;
	
	public boolean setMdmProfile(ValidTimeInfo validTimeInfo,ConfigurationProfileInfo profile,String customId);
	
	public boolean delMdmProfile(Set<String> profileNames,String customId) ;
	
	public MdmObject getMdmProfile(String profileName,String customId);
	
	public void getProfileInfoFromXml(InputStream in, ConfigurationProfileInfo profileInfo);

}
