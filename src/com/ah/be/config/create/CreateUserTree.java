package com.ah.be.config.create;

import java.io.IOException;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.UserInt;
import com.ah.xml.be.config.UserObj;

/**
 * @author zhang
 * @version 2008-10-14 20:51:17
 */

public class CreateUserTree {
	
	private UserInt userImpl;
	private UserObj userObj;
	
	private GenerateXMLDebug oDebug;

	public CreateUserTree(UserInt userImpl, GenerateXMLDebug oDebug){
		this.userImpl = userImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws IOException{
		this.userObj = new UserObj();
		
		genereateUserLevel_1();
	}
	
	public UserObj getUserObj(){
		return this.userObj;
	}
	
	private void genereateUserLevel_1() throws IOException{
		/**
		 * <user>					UserObj
		 */
		
		/** element: <user>.<cr> */
		userObj.setCr("");
		
		/** element: <user>.<group> */
		oDebug.debug("/configuration/user",
				"group", GenerateXMLDebug.SET_NAME,
				userImpl.getUserGuiName(), userImpl.getUserName());
		userObj.setGroup(CLICommonFunc.createAhNameActObj(userImpl.getUserGroupName(), CLICommonFunc.getYesDefault()));
		
		/** element: <user>.<password> */
		oDebug.debug("/configuration/user",
				"password", GenerateXMLDebug.SET_VALUE,
				userImpl.getUserGuiName(), userImpl.getUserName());
		userObj.setPassword(CLICommonFunc.createAhEncryptedStringAct(userImpl.getUserGroupPassword(), CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"user", GenerateXMLDebug.SET_NAME,
				userImpl.getUserGuiName(), userImpl.getUserName());
		userObj.setName(userImpl.getUserName());
		
		/** attribute: operation */
		userObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
	}
}
