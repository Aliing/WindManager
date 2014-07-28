package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.admin.HASettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.NtpProfileInt;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 * @author zhang
 *
 */
@SuppressWarnings("static-access")
public class NtpProfileImpl implements NtpProfileInt {
	
	private static final Tracer log = new Tracer(NtpProfileImpl.class
			.getSimpleName());

	private final HiveAp hiveAp;
	
	private final MgmtServiceTime mgmtServiceTime;
	private final List<String> ntpServerInfo = new ArrayList<String>();
	
	public NtpProfileImpl(HiveAp hiveAp) throws CreateXMLException{
		this.hiveAp = hiveAp;
		
		mgmtServiceTime = hiveAp.getConfigTemplate().getMgmtServiceTime();
		
		if(mgmtServiceTime != null){
			if(!mgmtServiceTime.getEnableClock() && mgmtServiceTime.getTimeInfo() != null){
				for(MgmtServiceTimeInfo timeInfo : mgmtServiceTime.getTimeInfo()){
					String ntpIp = CLICommonFunc.getIpAddress(timeInfo.getIpAddress(), hiveAp).getIpAddress();
					if(!isNtpServerExists(ntpIp)){
						ntpServerInfo.add(ntpIp);
					}
				}
			}else{
				HASettings haSet = null;
				List<HASettings> haList = MgrUtil.getQueryEntity().executeQuery(HASettings.class, null, null);
				
				if(!haList.isEmpty()){
					haSet = haList.get(0);
				}
				
				if(haSet != null && this.isEnableVpnTunnel()){
					if(haSet.isEnableHA()){
						String primaryNtp = haSet.getPrimaryMGTIP();
						String secondNtp = haSet.getSecondaryMGTIP();
						if(!isNtpServerExists(primaryNtp)){
							ntpServerInfo.add(primaryNtp);
						}
						if(!isNtpServerExists(secondNtp)){
							ntpServerInfo.add(secondNtp);
						}
					}else{
						String ntpIp = hiveAp.getDownloadInfo().getHmIpAddress();
						if(!isNtpServerExists(ntpIp)){
							ntpServerInfo.add(ntpIp);
						}
					}
				}else{
					String capwapIp1 = NmsUtil.getCapwapServer(hiveAp, true);
					String capwapIp2 = NmsUtil.getCapwapServer(hiveAp, false);
					if(capwapIp1 != null && !"".equals(capwapIp1) && !isNtpServerExists(capwapIp1)){
						ntpServerInfo.add(capwapIp1);
					}
					if(capwapIp2 != null && !"".equals(capwapIp2) && !isNtpServerExists(capwapIp2)){
						ntpServerInfo.add(capwapIp2);
					}
				}
			}
		}
	}
	
	private boolean isNtpServerExists(String serverIp){
		boolean found = false;
		for(String server : ntpServerInfo){
			if(server.equals(serverIp)){
				found = true;
				break;
			}
		}
		return found;
	}
	
	public String getNtpGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceTime");
	}
	
	public String getNtpName(){
		if(mgmtServiceTime != null){
			return mgmtServiceTime.getMgmtName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigureNtp(){
		return mgmtServiceTime != null;
	}
	
	public boolean isEnableNtpServer(){
		return mgmtServiceTime.getEnableNtp();
	}
	
	public int getIntervalValue(){
		return mgmtServiceTime.getInterval();
	}
	
	public int getNtpServerSize(){
		if(ntpServerInfo == null){
			return 0;
		}
		return ntpServerInfo.size();
	}
	
	public String getNtpServerAddress(int index) throws CreateXMLException{
		return ntpServerInfo.get(index);
	}
	
	public boolean isNtpServerFirst(int index){
		return index == 0;
	}
	
	public boolean isNtpServerSecond(int index){
		return index == 1;
	}
	
	public boolean isNtpServerThird(int index){
		return index == 2;
	}
	
	public boolean isNtpServerFourth(int index){
		return index == 3;
	}
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isNtpThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Ntp"});
						log.error("NtpProfileImpl.isEnableVpnTunnel", errMsg);
						throw new CreateXMLException(errMsg);
					}
					return true;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
	}
	
	private boolean isEnableVpnTunnel(){
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			return vpnClient != null && vpnClient.isNtpThroughTunnel();
		}else{
			return false;
		}
	}
	
//	public boolean isIpAddr(int index){
//		String regex = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(ntpServerInfo.get(index));
//		return matcher.matches();
//	}
	
}