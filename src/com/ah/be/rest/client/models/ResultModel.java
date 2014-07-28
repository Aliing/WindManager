package com.ah.be.rest.client.models;


public abstract class ResultModel{
		
		public short getReturnCode() {
			return returnCode;
		}
		public void setReturnCode(short returnCode) {
			this.returnCode = returnCode;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public short returnCode=-1;
		public String message;


}
