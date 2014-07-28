package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.TrackProfileInt;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.TrackMultiDstLogicValue;

/**
 * @author zhang
 * @version 2008-8-5 15:51:27
 */

public class TrackProfileImpl implements TrackProfileInt {
	
	private MgmtServiceIPTrack ipTrack;

	public TrackProfileImpl(MgmtServiceIPTrack ipTrack){
		this.ipTrack = ipTrack;
	}
	
	public boolean isConfigTrack(){
		return ipTrack != null;
	}
	
	public String getTrackGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtIpTrack");
	}
	
	public String getTrackName(){
		if(ipTrack != null){
			return ipTrack.getTrackName();
		}else{
			return null;
		}
	}
	
	public boolean isEnableTrack(){
		return ipTrack.isEnableTrack();
	}
	
	public int getTrackIpSize(){
		if(ipTrack.getIpAddresses() == null || "".equals(ipTrack.getIpAddresses())){
			return 0;
		}else{
			return ipTrack.getIpAddressList().length;
		}
	}
	
	public String getTrackIpAddress(int index){
		return ipTrack.getIpAddressList()[index];
	}
	
	public TrackMultiDstLogicValue getTrackLogic(){
		if(ipTrack.getTrackLogic() == MgmtServiceIPTrack.IP_TRACK_LOGIC_OR){
			return TrackMultiDstLogicValue.OR;
		}else{
			return TrackMultiDstLogicValue.AND;
		}
	}
	
//	public boolean isUseDefaultValue(){
//		return ipTrack.isUseDefaultPara();
//	}
	
	public int getTrackRetry(){
		return ipTrack.getRetryTime();
	}
	
	public int getTrackOldInterval(){
		short interval = ipTrack.getInterval();
		short retry = ipTrack.getRetryTime();
		return (interval * (retry + 1)) + 1;
	}
	
	public int getTrackInterval(){
		return ipTrack.getInterval();
	}
	
	public int getTrackTimeOut(){
		return ipTrack.getInterval();
	}
	
	public boolean isConfigTrackAction(TrackAction action){
		if(action == TrackAction.disableAccessRadio){
			return ipTrack.isDisableRadio();
		}else if(action == TrackAction.enableAccessConsole){
			return ipTrack.isEnableAccess();
		}else{
			return ipTrack.isStartFailover();
		}
	}
	
	public boolean isDisableTrackAction(){
		return !(!ipTrack.isDisableRadio() && !ipTrack.isEnableAccess() && !ipTrack.isStartFailover());
	}
	
	public boolean isEnableDefaultGateway(){
		return ipTrack.isUseGateway();
	}
	
	public boolean isUseForWanTesting(){
		return ipTrack.isWanTesting();
	}
}
