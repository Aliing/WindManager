package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * @author zhang
 * @version 2007-12-19 10:36:43 AM
 */

public interface AlgProfileInt {

	public enum AlgType {
		ftp, tftp, sip, dns, http
	}
	
	public String getWlanGuiName();
	
	public String getWlanName();
	
	public String getAlgGuiName();
	
	public String getAlgName();

	public String getApVersion();

	public boolean isConfigAlg() throws CreateXMLException;

	public String getUpdateTime();

//	public boolean isConfigAlgWithType(AlgType algType);

	public int getInactiveDataTimeout(AlgType algType);

	public int getMaxDuration(AlgType algType);

	public int getQos(AlgType algType);
	
	public boolean isAlgEnable(AlgType algType);

}