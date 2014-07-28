package com.ah.be.config.create.source;


public interface AdminConnectionAlarmInt {
	
	public boolean isEnableConnectionAlarm();
		
	public int getTxRetryThreshold();
	
	public int getTxRetryInterval();
	
	public int getTxFrameErrorThreshold();
	
	public int getTxFrameErrorInterval();
	
	public int getProbRequestThreshold();
	
	public int getProbRequestInterval();
		
	public int getIngressMulticastThreshold();
	
	public int getIngressMulticastInterval();
	
}