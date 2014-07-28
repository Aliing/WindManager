package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ReportProfileInt;
import com.ah.xml.be.config.ReportAlarmThresholdAirtimeConsumption;
import com.ah.xml.be.config.ReportAlarmThresholdClient;
import com.ah.xml.be.config.ReportAlarmThresholdCrcErrorRate;
import com.ah.xml.be.config.ReportAlarmThresholdInterface;
import com.ah.xml.be.config.ReportAlarmThresholdRxDropRate;
import com.ah.xml.be.config.ReportAlarmThresholdTxDropRate;
import com.ah.xml.be.config.ReportAlarmThresholdTxRetryRate;
import com.ah.xml.be.config.ReportObj;
import com.ah.xml.be.config.ReportStatistic;
import com.ah.xml.be.config.ReportStatisticAlarmThreshold;
import com.ah.xml.be.config.ReportStatisticPeriod;

public class CreateReportTree {
	
	private ReportProfileInt reportImpl;
	private GenerateXMLDebug oDebug;
	
	private ReportObj reportObj;
	
	private List<Object> reportChildLevel_1 = new ArrayList<Object>();
	private List<Object> reportChildLevel_2 = new ArrayList<Object>();
	private List<Object> reportChildLevel_3 = new ArrayList<Object>();

	public CreateReportTree(ReportProfileInt reportImpl, GenerateXMLDebug oDebug){
		this.reportImpl = reportImpl;
		this.oDebug = oDebug;
	}
	
	public ReportObj getReportObj(){
		return this.reportObj;
	}
	
	public void generate() throws Exception{
		reportObj = new ReportObj();
		generateReportLevel_1();
	}
	
	private void generateReportLevel_1() throws Exception{
		/**
		 * <report>			ReportObj
		 */
		
		/** element: <report>.<statistic> */
		ReportStatistic statObj = new ReportStatistic();
		reportChildLevel_1.add(statObj);
		reportObj.setStatistic(statObj);
		
		generateReportLevel_2();
	}
	
	private void generateReportLevel_2() throws Exception{
		/**
		 * <report>.<statistic>				ReportStatistic
		 */
		for(Object childObj : reportChildLevel_1){
			
			/** element: <report>.<statistic> */
			if(childObj instanceof ReportStatistic){
				ReportStatistic statObj = (ReportStatistic)childObj;
				
				/** element: <report>.<statistic>.<enable> */
				statObj.setEnable(CLICommonFunc.getAhOnlyAct(reportImpl.isEnableReportTree()));
				
				if(reportImpl.isEnableReportTree()){
					
					/** element: <report>.<statistic>.<period> */
					Object[][] periodParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getPeriodValue()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					statObj.setPeriod((ReportStatisticPeriod)
							CLICommonFunc.createObjectWithName(ReportStatisticPeriod.class, periodParm));
					
					/** element: <report>.<statistic>.<alarm-threshold> */
					ReportStatisticAlarmThreshold alarmObj = new ReportStatisticAlarmThreshold();
					reportChildLevel_2.add(alarmObj);
					statObj.setAlarmThreshold(alarmObj);
				}
			}
		}
		reportChildLevel_1.clear();
		generateReportLevel_3();
	}
	
	private void generateReportLevel_3() throws Exception{
		/**
		 * <report>.<statistic>.<alarm-threshold>				ReportStatisticAlarmThreshold
		 */
		for(Object childObj : reportChildLevel_2){
			
			/** element: <report>.<statistic>.<alarm-threshold> */
			if(childObj instanceof ReportStatisticAlarmThreshold){
				ReportStatisticAlarmThreshold alarmObj = (ReportStatisticAlarmThreshold)childObj;
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface> */
				ReportAlarmThresholdInterface interfaceObj = new ReportAlarmThresholdInterface();
				reportChildLevel_3.add(interfaceObj);
				alarmObj.setInterface(interfaceObj);
				
				/** element: <report>.<statistic>.<alarm-threshold>.<client> */
				ReportAlarmThresholdClient clientObj = new ReportAlarmThresholdClient();
				reportChildLevel_3.add(clientObj);
				alarmObj.setClient(clientObj);
			}
		}
		reportChildLevel_2.clear();
		generateReportLevel_4();
	}
	
	private void generateReportLevel_4() throws Exception{
		/**
		 * <report>.<statistic>.<alarm-threshold>.<interface>			ReportAlarmThresholdInterface
		 * <report>.<statistic>.<alarm-threshold>.<client>				ReportAlarmThresholdClient
		 */
		for(Object childObj : reportChildLevel_3){
			
			/** element: <report>.<statistic>.<alarm-threshold>.<interface> */
			if(childObj instanceof ReportAlarmThresholdInterface){
				ReportAlarmThresholdInterface interfaceObj = (ReportAlarmThresholdInterface)childObj;
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface>.<crc-error-rate> */
				Object[][] crcRateParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getCrcErrorRateValue()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interfaceObj.setCrcErrorRate((ReportAlarmThresholdCrcErrorRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdCrcErrorRate.class, crcRateParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface>.<tx-drop-rate> */
				Object[][] txRateParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getTxDropRateValue()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interfaceObj.setTxDropRate((ReportAlarmThresholdTxDropRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdTxDropRate.class, txRateParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface>.<rx-drop-rate> */
				Object[][] rxRateParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getRxDropRateValue()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interfaceObj.setRxDropRate((ReportAlarmThresholdRxDropRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdRxDropRate.class, rxRateParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface>.<tx-retry-rate> */
				Object[][] rxRetryParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getTxRetryRate()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interfaceObj.setTxRetryRate((ReportAlarmThresholdTxRetryRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdTxRetryRate.class, rxRetryParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<interface>.<airtime-consumption> */
				Object[][] atConsp = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getAirtimeConsumption()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interfaceObj.setAirtimeConsumption((ReportAlarmThresholdAirtimeConsumption)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdAirtimeConsumption.class, atConsp));
			}
			
			/** element: <report>.<statistic>.<alarm-threshold>.<client> */
			if(childObj instanceof ReportAlarmThresholdClient){
				ReportAlarmThresholdClient clientObj = (ReportAlarmThresholdClient)childObj;
				
				/** element: <report>.<statistic>.<alarm-threshold>.<client>.<tx-drop-rate> */
				Object[][] clTxRateParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getClientTxDropRate()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				clientObj.setTxDropRate((ReportAlarmThresholdTxDropRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdTxDropRate.class, clTxRateParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<client>.<rx-drop-rate> */
				Object[][] clRxRateParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getClientRxDropRate()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				clientObj.setRxDropRate((ReportAlarmThresholdRxDropRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdRxDropRate.class, clRxRateParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<client>.<tx-retry-rate> */
				Object[][] clTxRetryParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getClientTxRetryRate()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				clientObj.setTxRetryRate((ReportAlarmThresholdTxRetryRate)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdTxRetryRate.class, clTxRetryParm));
				
				/** element: <report>.<statistic>.<alarm-threshold>.<client>.<airtime-consumption> */
				Object[][] airtimeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, reportImpl.getClientAirtimeConsumption()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				clientObj.setAirtimeConsumption((ReportAlarmThresholdAirtimeConsumption)
						CLICommonFunc.createObjectWithName(ReportAlarmThresholdAirtimeConsumption.class, airtimeParm));
			}
		}
		reportChildLevel_3.clear();
	}
}
