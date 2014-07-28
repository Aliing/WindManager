package com.ah.be.config.result.ap;

import com.ah.be.config.result.AhConfigGenerationResult;

public class AhApConfigGenerationResult extends AhConfigGenerationResult {
	
	private String hiveApMac;
	
	private String rootAdmin;
	
	private String rootPassword;

	public AhApConfigGenerationResult() {
		super();
	}

	public String getRootAdmin() {
		return rootAdmin;
	}

	public void setRootAdmin(String rootAdmin) {
		this.rootAdmin = rootAdmin;
	}

	public String getRootPassword() {
		return rootPassword;
	}

	public void setRootPassword(String rootPassword) {
		this.rootPassword = rootPassword;
	}

	public String getHiveApMac() {
		return hiveApMac;
	}

	public void setHiveApMac(String hiveApMac) {
		this.hiveApMac = hiveApMac;
	}
}