package com.ah.be.config.create.source.impl;

import java.io.IOException;

import com.ah.be.config.create.source.UserInt;
import com.ah.bo.useraccess.LocalUser;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-10-14 20:53:25
 */

public class UserImpl implements UserInt {
	
	private LocalUser localUser;
	
	public UserImpl(LocalUser localUser){
		this.localUser = localUser;
	}
	
	public String getUserGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.localUsers");
	}
	
	public String getUserGroupName(){
		return localUser.getLocalUserGroup().getGroupName();
	}
	
	public String getUserGroupPassword() throws IOException{
		return localUser.getLocalUserPassword();
	}
	
	public String getUserName(){
		return localUser.getUserName();
	}
}