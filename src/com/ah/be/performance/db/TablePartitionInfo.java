package com.ah.be.performance.db;

public class TablePartitionInfo {	
	//schema name
	String		schemaName = "public";
	//table name
	String		tableName = null;
	//full name
	String		fullTableName = null;
	//time file name
	String		timeField = null;
	//catalog
	int			catalog = 0;
	
	public TablePartitionInfo(String schemaName,String tableName,String timeField,int catalog) {
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.timeField = timeField;
		this.catalog = catalog;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTimeField() {
		return timeField;
	}
	
	public String getFullTableName() {
		if (fullTableName == null)
		{
			StringBuffer fullName = new StringBuffer();
			fullName.append(schemaName).append(".").append(tableName);
			fullTableName = fullName.toString();
			
		}
		return fullTableName;
	}

	public int getCatalog() {
		return catalog;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTimeField(String timeField) {
		this.timeField = timeField;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
	
}

class TableCatalogInfo {
	
	public static final int		TABLE_PARTITION_NUMBER		= 8;
	
	public static final int		MAX_RECORD_NUM_PER_PARTITION	=	100000;
	
	/**interval policy
	1: use default
	2: get from table partiton number and max time
	**/
	public static final int		INTERVAL_POLICY_INVALID = -1;
	public static final int		INTERVAL_POLICY_DEFAULT = 1;
	public static final int		INTERVAL_POLICY_PARTITION_NUMBER = 2;
	
	/**max time policy
	1: use default
	2: calculate raw client stats max time base on device number of license, if no license , get from table logsettings, field is maxtimetableclisave
	3: calculate raw device stats max time base on device number of license, if no license , get from table logsettings, field is maxtimetablepersave 
	4: raw stats data,get from table logsettings, field is MaxOriginalCount
	5: hour stats data, get from table logsettings, field is maxhourvalue
	6: day stats data, get from table logsettings, field is maxdayvalue
	7: week stats data, get from table logsettings, field is maxweekvalue
	*/
	public static final int		MAXTIME_POLICY_DEFAULT 			= 1;
	public static final int		MAXTIME_POLICY_RAW_CLIENT_STATS = 2;
	public static final int		MAXTIME_POLICY_RAW_DEVICE_STATS = 3;
	public static final int		MAXTIME_POLICY_RAW_STATS 		= 4;
	public static final int		MAXTIME_POLICY_HOUR_STATS 		= 5;
	public static final int		MAXTIME_POLICY_DAY_STATS 		= 6;
	public static final int		MAXTIME_POLICY_WEEK_STATS 		= 7;
	
	/** max record number policy
	1: use default
	2: get from table logsettings, field is reportdbhourly
	3: get from table logsettings, field is reportdbdaily
	4: get from table logsettings, field is reportdbweekly
	5: get from table logsettings, field is maxhistoryclientrecord
	6: get from table logsettings, field is maxperfrecord
	*/
	public static final int		MAXRECORD_NUM_DEFAULT			= 1;
	public static final int		MAXRECORD_NUM_HOUR				= 2;
	public static final int		MAXRECORD_NUM_DAY				= 3;
	public static final int		MAXRECORD_NUM_WEEK				= 4;
	public static final int		MAXRECORD_NUM_CLIENT			= 5;
	public static final int		MAXRECORD_NUM_PERF				= 6;
	
	//catalog
	private int catalog;
	//hours
	private int maxtime = 3*24;
	//hours
	private int	interval = 1*24;
	//partition number
	private int partitionNumber = TableCatalogInfo.TABLE_PARTITION_NUMBER;
	//max record number
	int			maxRecordNum = -1;
	//max record number for partition
	int			maxRecordNumPerPartition = TableCatalogInfo.MAX_RECORD_NUM_PER_PARTITION;
	//max time policy
	int			maxTimePolicy = 1;
	//interval policy
	int			intervalPolicy = INTERVAL_POLICY_INVALID;
	//max record policy
	int 		maxRecordPolicy = 1;
	
	public int getCatalog() {
		return catalog;
	}
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
	
	private void setInterval() {
		if(this.intervalPolicy == INTERVAL_POLICY_PARTITION_NUMBER)
			interval = maxtime/partitionNumber;
		if(interval < 1)
			interval = 1;
	}
	public int getMaxtime() {
		return maxtime;
	}
	public void setMaxtime(int maxtime) {
		this.maxtime = maxtime;
		setInterval();
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public void setPartitionNumber(int partitionNumber) {
		this.partitionNumber = partitionNumber;
		setInterval();
	}
	public int getMaxRecordNum() {
		return maxRecordNum;
	}
	public void setMaxRecordNum(int maxRecordNum) {
		this.maxRecordNum = maxRecordNum;
	}
	public int getMaxRecordNumPerPartition() {
		return maxRecordNumPerPartition;
	}
	public void setMaxRecordNumPerPartition(int maxRecordNumPerPartition) {
		this.maxRecordNumPerPartition = maxRecordNumPerPartition;
	}
	public int getMaxTimePolicy() {
		return maxTimePolicy;
	}
	public void setMaxTimePolicy(int maxTimePolicy) {
		this.maxTimePolicy = maxTimePolicy;
	}
	public int getIntervalPolicy() {
		return intervalPolicy;
	}
	public void setIntervalPolicy(int intervalPolicy) {
		this.intervalPolicy = intervalPolicy;
		setInterval();
	}
	public int getMaxRecordPolicy() {
		return maxRecordPolicy;
	}
	public void setMaxRecordPolicy(int maxRecordPolicy) {
		this.maxRecordPolicy = maxRecordPolicy;
	}
	public int getPartitionNumber() {
		return partitionNumber;
	}
}
