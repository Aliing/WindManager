package com.ah.be.rest.client.exception;

public class OpenDNSException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public OpenDNSException(){
		
	}
	
	public OpenDNSException(String message){
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
