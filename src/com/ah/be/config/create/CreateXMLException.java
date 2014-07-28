package com.ah.be.config.create;

import com.ah.be.common.NmsUtil;
import com.ah.ui.actions.Navigation;
import com.ah.util.Tracer;

/**
 * 
 * @author zhang
 * 
 */
public class CreateXMLException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(CreateXMLException.class
			.getSimpleName());

	private String profileKey;

	private String profileName;
	
	private String errorCLI;

	public CreateXMLException() {
		super();
	}

	public CreateXMLException(String message) {
		super(message);
	}

	public CreateXMLException(Throwable cause) {
		super(cause);
	}

	public CreateXMLException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getProfileKey() {
		return this.profileKey;
	}

	public void setProfileKey(String profileKey) {
		this.profileKey = profileKey;
	}

	public String getProfileName() {
		return this.profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public void setErrorCLI(String errCli){
		this.errorCLI = errCli;
	}
	
	public String getErrorCLI(){
		return this.errorCLI;
	}

	public String getMessage() {
		String profileMsg = null;
		String errorMsg = super.getMessage();
		log.info("getMessage", "profileKey:" + profileKey + ", profileName:"
				+ profileName);
		if (null != profileKey && null != profileName) {
			String featureName = Navigation.getFeatureName(profileKey);
			if (null != featureName) {
				profileMsg = NmsUtil.getUserMessage(
						"error.be.config.create.generateXml.locate",
						new String[] { featureName, profileName });
			}
		}
		return profileMsg == null ? errorMsg : (errorMsg + "\n" + profileMsg);
	}
}