package com.ah.be.config.create.source;

public interface ReportProfileInt {

	public boolean isEnableReportTree();
	
	public int getPeriodValue();
	
	public int getCrcErrorRateValue();
	
	public int getTxDropRateValue();
	
	public int getRxDropRateValue();
	
	public int getTxRetryRate();
	
	public int getAirtimeConsumption();
	
	public int getClientTxDropRate();
	
	public int getClientRxDropRate();
	
	public int getClientTxRetryRate();
	
	public int getClientAirtimeConsumption();
}
