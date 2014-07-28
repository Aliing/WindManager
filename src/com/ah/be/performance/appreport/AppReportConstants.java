package com.ah.be.performance.appreport;

public interface AppReportConstants {
	
    public static final int FILE_LIMIT_NUM = 10000;
    
	public static final short FILE_TYPE_HOUR = 1;
	
	public static final short FILE_TYPE_SECOND = 2;
		
	public static final int RECORD_BATCH_NUM = 2000;
	
	public static final int RECORD_EVERY_LENGTH = 46;
	
	//file version length position 2 bytes
	public static final int VERSION_LENGTH = 2;
	
	public static final int RUN_LONG_INTERVAL_TIME = 10000;
	
	public static final int RUN_SHORT_INTERVAL_TIME = 1000;
	
	public static final int DISABLE_L7_SLEEPING_TIME = 30000;
	
	//bulk processor response false, parse processor will waiting it ready.
	public static final int WAITDB_INTERVAL_TIME = 1000;
	
	public static final int WAITDB_REPEAT_COUNT = 10;
	
	public static final String UNKNOWN = "Unknown";
	
}
