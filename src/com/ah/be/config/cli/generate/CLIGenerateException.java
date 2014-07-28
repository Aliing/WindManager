package com.ah.be.config.cli.generate;

public class CLIGenerateException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CLIGenerateException(){
		super();
	}
	
	public CLIGenerateException(String message){
		super(message);
	}
	
	public CLIGenerateException(Throwable cause){
		super(cause);
	}
	
	public CLIGenerateException(String message, Throwable cause){
		super(message, cause);
	}
}
