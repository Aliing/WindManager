package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class AcmTroubleEBO {

	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("StatusCode")
	private String statusCode;
	
	@XStreamAlias("ApiKey")
	private String apiKey;
	
	public String getStatusCode(){
		return statusCode;
	}
	
	public void setStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
	
//	@XStreamAlias("GatewayServer")
//	private String gatewayServer;
//	
//	@XStreamAlias("PnsServer")
//	private String pnsServer;
//	
//	@XStreamAlias("HeartBeatServer")
//	private String heartBeatServer;
//	
//	@XStreamAlias("ScheduleServer")
//	private String scheduleServer;
//	
//	@XStreamAlias("MemCachedServer")
//	private String memCachedServer;
//	
//	@XStreamAlias("MQServer")
//	private String mQServer;
//	
//	@XStreamAlias("DBServer")
//	private String dBServer;
//	
//	public String getGatewayServer(){
//		return gatewayServer;
//	}
//	
//	public void setGatewayServer(String gatewayServer){
//		this.gatewayServer = gatewayServer;
//	}
//	
//	public String getPnsServer(){
//		return pnsServer;
//	}
//	
//	public void setPnsServer(String pnsServer){
//		this.pnsServer = pnsServer;
//	}
//	
//	public String getHeartBeatServer(){
//		return heartBeatServer;
//	}
//	
//	public void setHeartBeatServer(String heartBeatServer){
//		this.heartBeatServer = heartBeatServer;
//	}
//	
//	public String getScheduleServer(){
//		return scheduleServer;
//	}
//	
//	public void setScheduleServer(String scheduleServer){
//		this.scheduleServer = scheduleServer;
//	}
//	
//	public String getMemCachedServer(){
//		return memCachedServer;
//	}
//	
//	public void setMemCachedServer(String memCachedServer){
//		this.memCachedServer = memCachedServer;
//	}
//	
//	public String getMQServer(){
//		return mQServer;
//	}
//	
//	public void setMQServer(String mQServer){
//		this.mQServer = mQServer;
//	}
//	
//	public String getDBServer(){
//		return dBServer;
//	}
//	
//	public void setDBServer(String dBServer){
//		this.dBServer = dBServer;
//	}
	
}
