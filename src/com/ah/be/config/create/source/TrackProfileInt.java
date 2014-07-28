package com.ah.be.config.create.source;

import com.ah.xml.be.config.TrackMultiDstLogicValue;

/**
 * @author zhang
 * @version 2008-8-5  03:39:37
 */

public interface TrackProfileInt {
	
	public enum TrackAction{
		disableAccessRadio, enableAccessConsole, startMeshFailover
	}
	
	public boolean isConfigTrack();
	
	public String getTrackGuiName();

	public String getTrackName();
	
	public boolean isEnableTrack();
	
	public int getTrackIpSize();
	
	public String getTrackIpAddress(int index);
	
	public TrackMultiDstLogicValue getTrackLogic();
	
//	public boolean isUseDefaultValue();
	
	public int getTrackRetry();
	
	public int getTrackOldInterval();
	
	public int getTrackInterval();
	
	public int getTrackTimeOut();
	
	public boolean isConfigTrackAction(TrackAction action);
	
	public boolean isDisableTrackAction();
	
	public boolean isEnableDefaultGateway();
	
	public boolean isUseForWanTesting();
}
