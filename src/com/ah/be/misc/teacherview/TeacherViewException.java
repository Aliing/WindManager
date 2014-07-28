/**
 * @filename			TeacherViewException.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R1
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.misc.teacherview;

/**
 * 
 */
public class TeacherViewException extends Exception {
	private static final long serialVersionUID = 1L;

	public TeacherViewException() {
		super();
	}
	
	public TeacherViewException(String message) {
		super(message);
	}
	
	public TeacherViewException(Throwable throwable) {
		super(throwable);
	}
	
	public TeacherViewException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
