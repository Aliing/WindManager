package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.ClientModeInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApPreferredSsid;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class ClientModeImpl implements ClientModeInt {

	protected HiveAp hiveAp;

	private List<HiveApPreferredSsid> preferredSsids;
	
	public ClientModeImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		preferredSsids = new ArrayList<HiveApPreferredSsid>();
		for (HiveApPreferredSsid simpleSsid : hiveAp.getWifiClientPreferredSsids()) {
			if (simpleSsid.getPreferredSsid() == null) {
				WifiClientPreferredSsid ssid = MgrUtil.getQueryEntity().findBoById(
						WifiClientPreferredSsid.class,
						simpleSsid.getPreferredId());
				simpleSsid.setPreferredSsid(ssid);
			}
			
			if (simpleSsid.getPreferredSsid() == null) {
				continue;
			}
			
			preferredSsids.add(simpleSsid);
		}
	}

	@Override
	public boolean isEnableClientMode() {
        return hiveAp.getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_WAN;
	}

	@Override
	public int getClientModeSsidSize() {
		return preferredSsids.size();
	}

	@Override
	public String getKeyValue(int index) {
		HiveApPreferredSsid ssid = getSsid(index);
		return ssid != null ? ssid.getPreferredSsid().getKeyValue() : null;
	}

	@Override
	public int getKeyType(int index) {
		HiveApPreferredSsid ssid = getSsid(index);
		return ssid != null ? ssid.getPreferredSsid().getKeyType() : null;
	}

	@Override
	public int getPriority(int index) {
		HiveApPreferredSsid ssid = getSsid(index);
		return ssid != null ? ssid.getPriority() : null;
	}

	@Override
	public String getSsidName(int index) {
		HiveApPreferredSsid ssid = getSsid(index);
		return ssid != null ? ssid.getPreferredSsid().getSsid() : null;
	}

	@Override
	public int getAccessMode(int index) {
		HiveApPreferredSsid ssid = getSsid(index);
		return ssid != null ? ssid.getPreferredSsid().getAccessMode() : null;
	}

    @Override
    public boolean isDynamicBandMode(int interfaceType) {
        return hiveAp.isEnableDynamicBandSwitch();
    }

    private HiveApPreferredSsid getSsid(int index){
		HiveApPreferredSsid ssid = null;
		if( index <= preferredSsids.size() ){
			ssid = preferredSsids.get(index);
		}
		
		return ssid;
	}

}
