package com.ah.be.config.create.source;


public interface UsbmodemInt {
	public static enum UsbMode{
		ondemand, alwaysconnected, primarywan
	};
	
	public boolean isConfigUsb();

	public boolean isEnableUsbmodem();
	
	public boolean isConfigUsbModem(UsbMode mode);
	
	public int getUSBModemIdSize();
	
	public boolean isConfigUSBModem(int index);
	
	public String getModemId(int index);
	
	public String getApn(int index);
	
	public String getDialupNumber(int index);
	
	public String getDialupUsername(int index);
	
	public String getDialupPassword(int index);
	
	public boolean isConfigApn(int index);
	
	public boolean isConfigDialupNumber(int index);
	
	public boolean isConfigDialupUsername(int index);
	
	public boolean isConfigDialupPassword(int index);
	
	public boolean isUsbmodemPowerEnable();
	
	public String getUsbmodemNetworkMode();
}
