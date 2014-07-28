package com.ah.be.rest.client.models.opendns;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenDNSModel {
	
	public static final String CONSTANT_STATUS = "status";
	public static final String CONSTANT_ERROR = "error";
	public static final String CONSTANT_ERROR_MESSAGE = "error_message";
	public static final String CONSTANT_TOKEN = "token";
	public static final String CONSTANT_DEVICEID = "device_id";
	public static final String CONSTANT_RESPONSE = "response";
	
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_INTERNAL_SERVER_ERROR = "Internal Server Error";
	
	public static final int ERROR_CODE_DEVICE_NOT_EXISTS = 4006;
	
	private boolean successFlag = false;
	
	private String token;
	
	private String deviceId;
	
	private String deviceKey;
	
	private String deviceLabel;
	
	private int error_code;
	
	private String error_message;

	public boolean isSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}
	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}	
	
	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	
	public OpenDNSModel tranformJsonToLoginModel(JSONObject jsonObj) throws JSONException{
		if(RESULT_SUCCESS.equals(jsonObj.getString(CONSTANT_STATUS))){
			this.successFlag = true;
			this.token = jsonObj.getJSONObject(CONSTANT_RESPONSE).getString(CONSTANT_TOKEN);
		}else{
			this.successFlag = false;
			this.error_code = jsonObj.getInt(CONSTANT_ERROR);
			this.error_message = jsonObj.getString(CONSTANT_ERROR_MESSAGE);
		}
		return this;
	}
	
	public OpenDNSModel tranformJsonToDeviceModel(JSONObject jsonObj) throws JSONException{			
		if(RESULT_SUCCESS.equals(jsonObj.getString(CONSTANT_STATUS))){
			this.successFlag = true;
			this.deviceId = jsonObj.getJSONObject(CONSTANT_RESPONSE).getString(CONSTANT_DEVICEID);
		}else{
			this.successFlag = false;
			this.error_code = jsonObj.getInt(CONSTANT_ERROR);
			this.error_message = jsonObj.getString(CONSTANT_ERROR_MESSAGE);
		}
		return this;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getDeviceLabel() {
		return deviceLabel;
	}

	public void setDeviceLabel(String deviceLabel) {
		this.deviceLabel = deviceLabel;
	}
}
