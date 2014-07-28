package com.ah.be.config.create.source;

public interface ClientModeInt {

	public boolean isEnableClientMode();
	
	public int getClientModeSsidSize();
	
	public int getKeyType(int index);
	
	public String getKeyValue(int index);
	
	public int getPriority(int index);
	
	public String getSsidName(int index);
	
	public int getAccessMode(int index);

    public boolean isDynamicBandMode(int interfaceType);
}
