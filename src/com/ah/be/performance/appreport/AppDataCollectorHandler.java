package com.ah.be.performance.appreport;

import java.util.List;

import com.ah.bo.ApReportData;

public interface AppDataCollectorHandler {
	
	ApReportData getSingleReportData(String apMac, byte[] data);
	
	boolean handToDataCollector(List<ApReportData> list) throws Exception;
	
}
