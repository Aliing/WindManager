package com.ah.be.config.create.source;

import com.ah.xml.be.config.CacheModeValue;
import com.ah.xml.be.config.ComboPatternValue;
import com.ah.xml.be.config.PasswordMethodValue;

/**
 * @author zhang
 * @version 2009-2-17 14:38:37
 */

public interface UserGroupProfileInt {
	
	public String getUserGroupName();
	
	public boolean isConfigUserGroupAttribute();
	
	public boolean isConfigUserGroupVlan();

	public int getUserGroupAttribute();
	
	public int getUserGroupVlan();
	
	public int getUserGroupReauthInterval();
	
	public CacheModeValue getCacheModeValue();
	
	public boolean isPskPasswordOnly();
	
	public boolean isPskUserAndPassword();
	
	public boolean isConfigConcateCharacters();
	
	public String getConcateCharacters();
	
	public boolean isPskGroup();
	
	public boolean isConfigPskStartTime();
	
	public String getPskGroupStartTime();
	
	public boolean isConfigPskExpiredTime();
	
	public String getPskGroupExpiredTime();
	
	public boolean isDigits();
	
	public boolean isLetters();
	
	public boolean isSpecialCharacters();
	
	public ComboPatternValue getComboPatternValue();
	
	public boolean isPskGroupAuto();
	
	public String getPskUserPrefix();
	
	public boolean isConfigPskUserSecret();
	
	public String getPskUserSecret();
	
	public boolean isConfigPskUserLocation();
	
	public String getPskUserLocation();
	
	public int getPskPasswordLength();
	
	public boolean isConfigPskSchedule();
	
	public String getPskSchedule();
	
//	public boolean isConfigIndexRange();
//	
//	public String getAutoPskIndexRange();
	
	public PasswordMethodValue getPasswordMethod();
	
//	public int getRevokeUserSize();
//	
//	public String getRevokeUserValue(int index);
	
	public boolean isVoiceDevice();
}
