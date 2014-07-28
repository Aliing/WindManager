package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.ReportProfileInt;
import com.ah.bo.hiveap.HiveAp;

public class ReportProfileImpl implements ReportProfileInt {
	
	private HiveAp hiveAp;
	
	public ReportProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}

	public boolean isEnableReportTree(){
		return this.hiveAp.getConfigTemplate().getEnableReportCollection();
	}
	
	public int getPeriodValue(){
		return this.hiveAp.getConfigTemplate().getCollectionInterval();
	}
	
	public int getCrcErrorRateValue(){
		return this.hiveAp.getConfigTemplate().getCollectionIfCrc();
	}
	
	public int getTxDropRateValue(){
		return this.hiveAp.getConfigTemplate().getCollectionIfTxDrop();
	}
	
	public int getRxDropRateValue(){
		return this.hiveAp.getConfigTemplate().getCollectionIfRxDrop();
	}
	
	public int getTxRetryRate(){
		if(this.hiveAp.is11acHiveAP()){
			return this.hiveAp.getDeviceTxRetry();
		}
		
		return this.hiveAp.getConfigTemplate().getCollectionIfTxRetry();
	}
	
	public int getAirtimeConsumption(){
		return this.hiveAp.getConfigTemplate().getCollectionIfAirtime();
	}
	
	public int getClientTxDropRate(){
		return this.hiveAp.getConfigTemplate().getCollectionClientTxDrop();
	}
	
	public int getClientRxDropRate(){
		return this.hiveAp.getConfigTemplate().getCollectionClientRxDrop();
	}
	
	public int getClientTxRetryRate(){
		if(this.hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_370
				|| this.hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_390){
			return this.hiveAp.getClientTxRetry();
		}
		
		return this.hiveAp.getConfigTemplate().getCollectionClientTxRetry();
	}
	
	public int getClientAirtimeConsumption(){
		return this.hiveAp.getConfigTemplate().getCollectionClientAirtime();
	}
}
