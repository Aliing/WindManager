package com.ah.bo.performance;

import com.ah.bo.HmBo;

public interface AhNewSLAStatsInterface extends HmBo {
	
	public int getApTotal_Red();
	public void setApTotal_Red(int apTotalRed);

	public int getApTotal_Yellow();
	public void setApTotal_Yellow(int apTotalYellow);

	public int getApSla_Red();
	public void setApSla_Red(int apSlaRed);

	public int getApSla_Yellow();
	public void setApSla_Yellow(int apSlaYellow);

	public int getApAirTime_Red();
	public void setApAirTime_Red(int apAirTimeRed);

	public int getApCrcError_Red();
	public void setApCrcError_Red(int apCrcErrorRed);

	public int getApRetry_Red();
	public void setApRetry_Red(int apRetryRed);
	
	public int getApTxDrop_Red();
	public void setApTxDrop_Red(int apTxDropRed);

	public int getApRxDrop_Red();
	public void setApRxDrop_Red(int apRxDropRed);

	public int getClientTotal_Red();
	public void setClientTotal_Red(int clientTotalRed);

	public int getClientTotal_Yellow();
	public void setClientTotal_Yellow(int clientTotalYellow);

	public int getClientSla_Red();
	public void setClientSla_Red(int clientSlaRed);
	
	public int getClientSla_Yellow();
	public void setClientSla_Yellow(int clientSlaYellow);

	public int getClientAirTime_Red();
	public void setClientAirTime_Red(int clientAirTimeRed);

	public int getClientScore_Red();
	public void setClientScore_Red(int clientScoreRed);
	
	public int getClientScore_Yellow();
	public void setClientScore_Yellow(int clientScoreYellow);
	
	public String getApMac();
	public void setApMac(String apMac);

	public String getApName();
	public void setApName(String apName);

	public int getClientCount();
	public void setClientCount(int clientCount);
}
