/**
 * @filename			IndexOutputStream.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * An object output stream supporting object appending
 */
public class IndexOutputStream extends ObjectOutputStream {

	protected IndexOutputStream() throws IOException, SecurityException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public IndexOutputStream(OutputStream os) throws IOException, SecurityException{
		super(os);
	}
	
	
	protected void writeStreamHeader() throws IOException 
	{ 
		super.reset();
	} 

}
