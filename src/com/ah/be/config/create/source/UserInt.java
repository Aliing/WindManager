package com.ah.be.config.create.source;

import java.io.IOException;

/**
 * @author zhang
 * @version 2008-10-14 20:52:56
 */

public interface UserInt {
	
	public String getUserGuiName();
	
	public String getUserGroupName();
	
	public String getUserGroupPassword() throws IOException;
	
	public String getUserName();
}
