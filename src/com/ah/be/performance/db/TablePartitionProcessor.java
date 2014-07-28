package com.ah.be.performance.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.LicenseInfo;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.HibernateUtil;
import com.ah.util.MgrUtil;

public class TablePartitionProcessor implements Runnable {
	
	private final String			TABLE_PARTITION_CONF_FILE	= System.getenv("HM_ROOT") + "/WEB-INF/hmconf/table_partition_config.xml";			
	
	private final int				TIMER_INTERVAL = 1;
	
	static private boolean			enable_table_partition = true;
	
	private Map<Integer,TableCatalogInfo>			catalogMap = null;
	
	private Map<String, TablePartitionInfo>			tableMap = null;

	
	public TablePartitionProcessor() {	
		try {
			String value = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_PERFORMANCE,
					ConfigUtil.KEY_ENABLE_TABLE_PARTITION, "1");
			if(value.equalsIgnoreCase("0"))
				enable_table_partition = false;
		}catch (Exception e) {
		}
		
		tableMap = Collections.synchronizedMap(new HashMap<String,TablePartitionInfo>());
		catalogMap = Collections.synchronizedMap(new HashMap<Integer,TableCatalogInfo>());

		init();
	}
	
	
	static public boolean isEnableTablePartition() {
		return enable_table_partition;
	}

	private void init() {
		try {
			File f = new File(TABLE_PARTITION_CONF_FILE);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Element cata = null;
			
			int catalog = 1;
			for(Iterator<?> i = root.elementIterator(); i.hasNext();) {
				//for catalog
				cata = (Element)i.next();
				String enable = cata.elementText("enable");
				if(enable != null && enable.equalsIgnoreCase("false"))
					continue;
				int default_maxtime = Integer.parseInt(cata.elementTextTrim("default_maxtime"));
				int default_interval = Integer.parseInt(cata.elementTextTrim("default_interval"));
				int table_partition_number = Integer.parseInt(cata.elementTextTrim("table_partition_number"));
				int default_max_record = Integer.parseInt(cata.elementTextTrim("default_max_record"));
				int default_max_record_per_partition = Integer.parseInt(cata.elementTextTrim("default_max_record_per_partition"));
				int maxtime_policy = Integer.parseInt(cata.elementTextTrim("maxtime_policy"));
				int interval_policy = Integer.parseInt(cata.elementTextTrim("interval_policy"));
				int max_record_policy = Integer.parseInt(cata.elementTextTrim("max_record_policy"));
				
				addTableCatalogInfo(catalog,default_maxtime,default_interval,table_partition_number,
						default_max_record,default_max_record_per_partition,
						maxtime_policy,interval_policy,max_record_policy);
				
				List<?> tableElements =  cata.elements("table");
				//for table in catalog
				for(int j = 0; j < tableElements.size(); j++) {
					Element table = (Element)tableElements.get(j);
					String tableName = table.attributeValue("name");
					String schemaName = table.elementTextTrim("schemaname");
					String timeField = table.elementTextTrim("timefield");
					addTableInfo(schemaName,tableName,timeField,catalog);
				}
				
				catalog++;
			}
		} catch(Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"Fail to init table partition configure file",e);
		}
	}
	
	/**
	 * add table catalog info
	 * @param catalog
	 * @param maxtime
	 * @return
	 */
	private void addTableCatalogInfo(int catalog,int maxtime, int interval, int partitionNumber,
			int maxRecordNum, int maxRecordNumPerPartition, int maxTimePolicy,
			int intervalPolicy, int maxRecordPolicy) {
		synchronized(catalogMap) {
			TableCatalogInfo info = catalogMap.get(catalog);
			if(info == null) {
				info = new TableCatalogInfo();
			}
			info.setCatalog(catalog);
			info.setMaxtime(maxtime);
			info.setInterval(interval);
			info.setPartitionNumber(partitionNumber);
			info.setMaxRecordNum(maxRecordNum);
			info.setMaxRecordNumPerPartition(maxRecordNumPerPartition);
			info.setMaxTimePolicy(maxTimePolicy);
			info.setIntervalPolicy(intervalPolicy);
			info.setMaxRecordPolicy(maxRecordPolicy);
			catalogMap.put(catalog, info);
		return;
		}
	}
	/**
	 * add table info into table map
	 * @param schemaName
	 * @param tableName
	 * @param timeField
	 * @return
	 */
	private TablePartitionInfo addTableInfo(String schemaName,String tableName,String timeField,int catalog) {
		synchronized(tableMap) {
			String key = schemaName+tableName;
			TablePartitionInfo info = tableMap.get(key);
			if(info == null) {
				info = new TablePartitionInfo(schemaName.toLowerCase(),tableName.toLowerCase(),timeField.toLowerCase(),catalog);
			} else {
				info.setCatalog(catalog);
				info.setSchemaName(schemaName.toLowerCase());
				info.setTableName(tableName.toLowerCase());
				info.setTimeField(timeField.toLowerCase());
			}
			tableMap.put(key, info);
			return info;
		}
	}
	
	public void start() {
		if(enable_table_partition == false) {
			removeAllInheritTable();
			return;
		}
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> Table partition timer start...");
		//run table partition manager first when start
		tablePartitionManage();
		// start scheduler
		AhAppContainer.HmBe.getPerformModule().getTimerProcessor().registerTimer(this, -1,40,TIMER_INTERVAL*3600);
	}
	public void stop() {
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> Table partition timer stop");
	}
	
	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(getClass().getSimpleName());
			init();
			tablePartitionManage();
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,
					"TablePartitionProcessor:catch exception in run.", e);
		} catch (Error e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,
					"TablePartitionProcessor:catch error in run.", e);
		}
	}
	
	private void getMaxTimeByDeviceNum(int deviceNum,int recordNumberPerDevicePerDay,
			int maxRecordNum, TableCatalogInfo cataInfo) {
		int maxTime = 3;
		int recordNumberPerDay = deviceNum * recordNumberPerDevicePerDay;
		maxTime = maxRecordNum/recordNumberPerDay < 1 ? 1 : maxRecordNum/recordNumberPerDay;
		maxTime = maxTime > 30 ? 30 : maxTime;
		cataInfo.setMaxtime(maxTime*24);
	}
	/**
	 * apply max time policy
	 * @param cataInfo
	 * @param settings
	 * @param license
	 */
	private void applyMaxTimePolicy(TableCatalogInfo cataInfo,LogSettings settings,LicenseInfo license) {
		int maxTimePolicy = cataInfo.getMaxTimePolicy();
		switch(maxTimePolicy) {
		case TableCatalogInfo.MAXTIME_POLICY_DEFAULT:
			break;
		case TableCatalogInfo.MAXTIME_POLICY_RAW_CLIENT_STATS:
			if(license == null) {
				cataInfo.setMaxtime(settings.getMaxTimeTableCliSave()*24);
//				cataInfo.setInterval(settings.getIntervalTablePartCli());
			} else {
				getMaxTimeByDeviceNum(license.getHiveAps(),200,1000000,cataInfo);
				try {
					QueryUtil.updateBo(LogSettings.class,"maxTimeTableCliSave=:s1",
							new FilterParams("1=1",	new Object[] {cataInfo.getMaxtime()/24}));
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to update logsettings",e);
				}
			}
			break;
		case TableCatalogInfo.MAXTIME_POLICY_RAW_DEVICE_STATS:
			if(license == null) {
				cataInfo.setMaxtime(settings.getMaxTimeTablePerSave()*24);
//				cataInfo.setInterval(settings.getIntervalTablePartPer()*24);
			} else {
				getMaxTimeByDeviceNum(license.getHiveAps(),1000,3000000,cataInfo);
				try {
					QueryUtil.updateBo(LogSettings.class,"maxTimeTablePerSave=:s1",
							new FilterParams("1=1",	new Object[] {cataInfo.getMaxtime()/24}));
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to update logsettings",e);
				}
			}
			break;
		case TableCatalogInfo.MAXTIME_POLICY_RAW_STATS:
			cataInfo.setMaxtime(settings.getMaxOriginalCount());
			break;
		case TableCatalogInfo.MAXTIME_POLICY_HOUR_STATS:
			cataInfo.setMaxtime(settings.getMaxHourValue()*24);
			break;
		case TableCatalogInfo.MAXTIME_POLICY_DAY_STATS:
			cataInfo.setMaxtime(settings.getMaxDayValue()*7*24);
			break;
		case TableCatalogInfo.MAXTIME_POLICY_WEEK_STATS:
			cataInfo.setMaxtime(settings.getMaxWeekValue()*30*24);
			break;
		}
	}
	/**
	 * apply max record number policy
	 * @param cataInfo
	 * @param settings
	 */
	public void applyMaxRecordNumPolicy(TableCatalogInfo cataInfo, LogSettings settings) {
		int maxRecordNumPolicy = cataInfo.getMaxRecordPolicy();
		switch(maxRecordNumPolicy) {
		case TableCatalogInfo.MAXRECORD_NUM_DEFAULT:
			break;
		case TableCatalogInfo.MAXRECORD_NUM_HOUR:
			cataInfo.setMaxRecordNum(settings.getReportDbHourly());
			break;
		case TableCatalogInfo.MAXRECORD_NUM_DAY:
			cataInfo.setMaxRecordNum(settings.getReportDbDaily());
			break;
		case TableCatalogInfo.MAXRECORD_NUM_WEEK:
			cataInfo.setMaxRecordNum(settings.getReportDbWeekly());
			break;
		case TableCatalogInfo.MAXRECORD_NUM_CLIENT:
			cataInfo.setMaxRecordNum(settings.getMaxHistoryClientRecord());
			break;
		case TableCatalogInfo.MAXRECORD_NUM_PERF:
			cataInfo.setMaxRecordNum(settings.getMaxPerfRecord());
			break;
		}
	}
	/**
	 * table partition manager
	 */
	public void tablePartitionManage() {
		if(enable_table_partition == false)
			return;
		long begin = System.currentTimeMillis();
		
		//get data from logsettings
		LogSettings settings = null;
		List<LogSettings> listResult = null;
		try {
			listResult = QueryUtil.executeQuery(LogSettings.class, null, null);
			if(listResult != null && listResult.size() == 1) {
				settings = listResult.get(0);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to get info from logsettings",e);
		}
		if(settings == null) {
			settings = new LogSettings();
		}
		
		//get license
		LicenseInfo license = null;
		if (!NmsUtil.isHostedHMApplication()) {
			try {
				license = HmBeLicenseUtil.getLicenseInfo();
			} catch (Exception e) {
			}
		}
		
		//get catalog policy
		synchronized(catalogMap) {
			Collection<TableCatalogInfo> values = catalogMap.values();
			for(TableCatalogInfo cataInfo:values) {
				applyMaxTimePolicy(cataInfo,settings,license);
				applyMaxRecordNumPolicy(cataInfo,settings);
			}
		}
		
		//maintain by interval and time
		synchronized(tableMap) {
			Collection<TablePartitionInfo> values = tableMap.values();
			for(TablePartitionInfo info:values) {
				TableCatalogInfo catalogInfo = catalogMap.get(info.getCatalog());
				if(catalogInfo != null) {
					maintainTablePartition(catalogInfo.getMaxtime(),catalogInfo.getInterval(),
							catalogInfo.getMaxRecordNum(),catalogInfo.getMaxRecordNumPerPartition(),info);
				} else {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION, "Unknown table catalog " + info.getCatalog());
				}
			}
		}
		BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Table partition manager, eclipse time "+
				(System.currentTimeMillis()-begin)+"ms");
	}
	
	/**
	 * get table info by id
	 * @param schemaName
	 * @param tableName
	 * @return long[0] maxid ; long[1] minid; long[2] count; null if it has error
	 */
	private long[] getTableInfoById(String schemaName,String tableName) {
		long[] lRet = new long[3];
		lRet[0] = lRet[1] = lRet[2] = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(id), min(id) from ");
		sql.append(schemaName).append(".").append(tableName);
		try {
			List<?> list = QueryUtil.executeNativeQuery(sql.toString());
			if (list.isEmpty()) {
				return null;
			}
			Object[] obj = (Object[])list.get(0);
			if(obj[0] != null && obj[1] != null) {
				lRet[0] = Long.parseLong(obj[0].toString());
				lRet[1] = Long.parseLong(obj[1].toString());
				lRet[2] = lRet[0] - lRet[1] + 1;
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,
					"TablePartitionProcessor:Fail to get table info by id , sql is " + sql.toString(), e);
			return null;
		}
		return lRet;
	}
	/**
	 * unlik and drop child table by max record number
	 * @param schemaName
	 * @param tableName
	 * @param maxRecordNum
	 * @param inheritTableList
	 * @return whether remove child table
	 */
	private boolean removeChildTableByMaxRecordNumber(String schemaName, String tableName,String fullTableName,
			int maxRecordNum, List<String> inheritTableList) {
		if(maxRecordNum <= 0)
			return false;
		boolean bRemoved = false;
		long nRemovedId = 0;
		long maxId = 0, count = 0;
		//get total number and max id
		long[] lRet = getTableInfoById(schemaName,tableName);
		if(null == lRet)
			return bRemoved;
		count = lRet[2];
		if(count < maxRecordNum)
			return bRemoved;
		nRemovedId = lRet[0] - maxRecordNum;
		
		//the child table is sort by timestamp asc
		String childTableName = null;
		List<String> tableList = new ArrayList<String>();
		tableList.addAll(inheritTableList);
		for(int i = 0; i < tableList.size(); i++ ) {
			childTableName = tableList.get(i);
			//get the begin and end timestamp for child table
			long[] timeStamp = getTimeStampByTableName(childTableName,tableName,fullTableName);
			if( null == timeStamp) {
				break;
			}
			//check timestamp with current time
			long now = System.currentTimeMillis();
			if (timeStamp[0] < now && timeStamp[1] > now)
				break;
			
			//get the max(id) and count of child table
			lRet = getTableInfoById(schemaName,childTableName);
			if( null == lRet)
				break;
			maxId = lRet[0];
			count = lRet[2];
			if (maxId < nRemovedId) {
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Remove child table (Name:"+childTableName+";Record number:"
						+ lRet[2] +") for parent table " + fullTableName+ " because of exceed to max record number:" + maxRecordNum);
				unlinkChildTable(childTableName,tableName,schemaName);
				//remove child table
				removeChildTable(childTableName,schemaName);
				bRemoved = true;
				inheritTableList.remove(childTableName);
			} else if (maxId > 0) {
				//if the record number of table is not zero, break and don't check next child table
				break;
			}
		}
		return bRemoved;
	}
	
	/**
	 * get timestamp by inherit table name
	 * @param childTableName such as tablename_2012081200_2012081300
	 * @param parentTableName
	 * @param fullParentTableName
	 * @return
	 */
	private long[] getTimeStampByTableName(String childTableName,String parentTableName,String fullParentTableName) {
		Calendar resCalendar = Calendar.getInstance();
		
		
		long[]	timestampArray = new long[2];
		
		try {
			int beginIndex = childTableName.indexOf(parentTableName+"_");
			if(beginIndex == 0) {
				String timeString = childTableName.substring(beginIndex+parentTableName.length()+1);
				int year, month, day, hour;
				
				beginIndex = timeString.indexOf("_");
				if(beginIndex == -1) {
					throw new Exception("Unknow format");
				}
				//get begin time stamp
				String beginTimeString = timeString.substring(0,beginIndex);
				resCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
				resCalendar.setTimeInMillis(0);
				year = month = day = hour = 0;
				if(beginTimeString.length() >= 4)
					year = Integer.parseInt(beginTimeString.substring(0, 4));
				if(beginTimeString.length() >= 6)
					month = Integer.parseInt(beginTimeString.substring(4,6));
				if(beginTimeString.length() >= 8)
					day = Integer.parseInt(beginTimeString.substring(6,8));
				if(beginTimeString.length() >= 10)
					hour = Integer.parseInt(beginTimeString.substring(8,10));
				resCalendar.set(Calendar.YEAR, year);
				resCalendar.set(Calendar.MONTH, month - 1);
				resCalendar.set(Calendar.DAY_OF_MONTH, day);
				resCalendar.set(Calendar.HOUR_OF_DAY, hour);
				timestampArray[0] = resCalendar.getTimeInMillis();
				
				//get end time stamp
				String endTimeString = timeString.substring(beginIndex+1);
				resCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
				resCalendar.setTimeInMillis(0);
				year = month = day = hour = 0;
				if(endTimeString.length() >= 4)
					year = Integer.parseInt(endTimeString.substring(0, 4));
				if(endTimeString.length() >= 6)
					month = Integer.parseInt(endTimeString.substring(4,6));
				if(endTimeString.length() >= 8)
					day = Integer.parseInt(endTimeString.substring(6,8));
				if(endTimeString.length() >= 10)
					hour = Integer.parseInt(endTimeString.substring(8,10));
				resCalendar.set(Calendar.YEAR, year);
				resCalendar.set(Calendar.MONTH, month - 1);
				resCalendar.set(Calendar.DAY_OF_MONTH, day);
				resCalendar.set(Calendar.HOUR_OF_DAY, hour);
				timestampArray[1] = resCalendar.getTimeInMillis();
			} else
			{
				throw new Exception("Unknow format");
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,
					"Cannot get timestamp info of table partition for table " + fullParentTableName+"["
					+ childTableName + ":" + parentTableName + "]",e);
			return null;
		}
		
		return timestampArray;
	}
	
	/**
	 * get name by timestamp
	 * @param beginTimestamp
	 * @param endTimestamp
	 * @param parentName
	 * @return name, format is parentName_YYYYmmddhh_YYYYmmddhh
	 */
	private String getNameByTimeStamp(long beginTimestamp,long endTimestamp,String parentName) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(beginTimestamp);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		StringBuffer name = new StringBuffer();
		name.append(parentName).append("_");
		int value = 0;
		value = calendar.get(Calendar.YEAR);
		name.append(value);
		value = calendar.get(Calendar.MONTH)+1;
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		value = calendar.get(Calendar.DAY_OF_MONTH);
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		value = calendar.get(Calendar.HOUR_OF_DAY);
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		
		calendar.setTimeInMillis(endTimestamp);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		name.append("_");
		value = calendar.get(Calendar.YEAR);
		name.append(value);
		value = calendar.get(Calendar.MONTH)+1;
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		value = calendar.get(Calendar.DAY_OF_MONTH);
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		value = calendar.get(Calendar.HOUR_OF_DAY);
		if(value < 10)
			name.append("0").append(value);
		else
			name.append(value);
		
		return name.toString();
	}
	/**
	 * get exist inherit tables
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	private List<String> getExistInheritTables(String schemaName, String tableName) {
		StringBuffer sql = new StringBuffer();
		sql.append("select relname  from pg_inherits a , pg_class b where inhparent in (select a.oid from pg_class a ,pg_namespace b where a.relname = '");
		sql.append(tableName);
		sql.append("' and a.relnamespace=b.oid and b.nspname='");
		sql.append(schemaName);
		sql.append("') and a.inhrelid = b.oid order by relname asc");
		
		List<String> inheritTableList = new ArrayList<String>();
		try {
			List<?> resList = QueryUtil.executeNativeQuery(sql.toString());
			if(resList != null) {
				for (Object obj: resList) {
					inheritTableList.add(obj.toString());
				}
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get inherit table for table " + schemaName + "." + tableName
					+ ",SQL:" + sql.toString(),e);
		}
		return inheritTableList;
	}
	
	/**
	 * unlink chlid table
	 * @param childTableName
	 * @param parentTableName
	 * @param schemaName
	 */
	private void unlinkChildTable(String childTableName, String parentTableName, String schemaName) {
		StringBuffer sql = new StringBuffer();
		sql.append("ALTER table ");
		sql.append(schemaName).append(".").append(childTableName);
		sql.append(" NO INHERIT ");
		sql.append(schemaName).append(".").append(parentTableName);
		
		try {
			QueryUtil.executeNativeUpdate(sql.toString());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to unlink chlid table(" + childTableName + ") for table "+ schemaName + "." + parentTableName
					+ ",SQL:" + sql.toString(),e);
		}
		return;
	}
	
	/**
	 * get valid inherit tables
	 * @param maxtime	unit is hour
	 * @param interval	unit is hour
	 * @param maxRecordNum
	 * @param maxRecordNumPerPartition
	 * @param info
	 * @return null means inherit tables have no change
	 */
	private List<InheritTableInfo> getValidInheritTables(int maxtime, int interval,int maxRecordNum,
			int maxRecordNumPerPartition,TablePartitionInfo info) {
		long now = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now-maxtime*3600*1000L);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		//get existed inherit table and unlink too old and too new inherit table
		//and store all new inherit table into list
		List<InheritTableInfo> inheritInfoList = new ArrayList<InheritTableInfo>();
		boolean isChanged = false;
		List<String> inheritTableList = getExistInheritTables(info.getSchemaName(),info.getTableName());
		
		//unlik and drop child table by max record number
		isChanged = removeChildTableByMaxRecordNumber(info.getSchemaName(),info.getTableName(),
				info.getFullTableName(),maxRecordNum,inheritTableList);
		
		long oldestTimeStamp = 0;
		long latestTimeStamp = 0;
		long latestTableTimeStamp = now+(TIMER_INTERVAL+1)*3600*1000L;
		
		InheritTableInfo inheritTableInfo = null;
		InheritTableInfo previewInheritTableInfo = null;
		String tableName = null;
		long[] timeStamp = null;
		for(int i = 0; i < inheritTableList.size(); i++ ) {
			tableName = inheritTableList.get(i);
			timeStamp = getTimeStampByTableName(tableName,info.getTableName(),info.getFullTableName());
			if( null == timeStamp) {
				unlinkChildTable(tableName,info.getTableName(),info.getSchemaName());
				BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Format of child table name("+tableName+") for parent table " + info.getFullTableName()+" is wrong");
				isChanged = true;
				continue;
			}
			if(timeStamp[1] <= calendar.getTimeInMillis()) {
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Remove too old child table ("+tableName+") for parent table " + info.getFullTableName());
				unlinkChildTable(tableName,info.getTableName(),info.getSchemaName());
				//remove child table
				removeChildTable(tableName, info.getSchemaName());
				isChanged = true;
			} else if (timeStamp[0] > latestTableTimeStamp) {
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Remove too new child table ("+tableName+") for parent table " + info.getFullTableName());
				unlinkChildTable(tableName,info.getTableName(),info.getSchemaName());
				//remove child table
				removeChildTable(tableName, info.getSchemaName());
				isChanged = true;
			} else {
				previewInheritTableInfo = inheritTableInfo;
				inheritTableInfo = new InheritTableInfo();
				
				if(oldestTimeStamp == 0)
					oldestTimeStamp = timeStamp[0];
				latestTimeStamp = timeStamp[1];
				
				if( i >= (inheritTableList.size()-1)) {
					//if the table end time is too new, change the end time
					if ( latestTimeStamp  > (latestTableTimeStamp + interval*3600*1000L)) {
						latestTimeStamp = latestTableTimeStamp-(latestTableTimeStamp%(3600*1000L));
						inheritTableInfo.setOldTableName(tableName);
						tableName = getNameByTimeStamp(timeStamp[0],latestTimeStamp,info.getTableName());
						BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Change the table name from "+
								inheritTableInfo.getOldTableName()+" to " +inheritTableInfo.getTableName() + 
								" because the table end time is too new.");
					}
				}
				if(previewInheritTableInfo != null){
					//change the end time stamp if end times tamp is not equal with the begin time stamp of next child table
					if(previewInheritTableInfo.getEndTimestamp() != timeStamp[0]) {
						previewInheritTableInfo.setEndTimestamp(timeStamp[0]);
						previewInheritTableInfo.setOldTableName(previewInheritTableInfo.getTableName());
						previewInheritTableInfo.setTableName(getNameByTimeStamp(previewInheritTableInfo.getBeginTimestamp(),
								previewInheritTableInfo.getEndTimestamp(),info.getTableName()));
						isChanged = true;
						BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Change the table name from "+
								previewInheritTableInfo.getOldTableName()+" to " +previewInheritTableInfo.getTableName() + 
								" because end times tamp is not equal with the begin time stamp of next child table.");
					}
				}
				
				//add inherit table
				inheritTableInfo.setSchemaName(info.getSchemaName());
				inheritTableInfo.setTableName(tableName);
				inheritTableInfo.setParentTableName(info.getTableName());
				inheritTableInfo.setTimeField(info.getTimeField());
				inheritTableInfo.setBeginTimestamp(timeStamp[0]);
				inheritTableInfo.setEndTimestamp(latestTimeStamp);
				inheritInfoList.add(inheritTableInfo);
				
				//split the partition if record number of partition exceed to max record number per partition
				if(maxRecordNumPerPartition > 0) {
					long splitCheckTime = now;
					long splitTime = splitCheckTime - splitCheckTime%(3600*1000L) + 2*3600*1000L;
					if(splitCheckTime > inheritTableInfo.getBeginTimestamp() && splitTime < inheritTableInfo.getEndTimestamp()) {
						long count = 0;
						//get record number
						String tblName = inheritTableInfo.getOldTableName() != null ? inheritTableInfo.getOldTableName() :tableName; 
						long[] lRet = getTableInfoById(info.getSchemaName(),tblName);
						if(null != lRet) {
							count = lRet[2];
						}
						if(count >= maxRecordNumPerPartition) {
							//change the end time
							latestTimeStamp = splitTime;
							if(inheritTableInfo.getOldTableName() == null)
								inheritTableInfo.setOldTableName(tableName);
							inheritTableInfo.setEndTimestamp(latestTimeStamp);
							inheritTableInfo.setTableName(getNameByTimeStamp(timeStamp[0],latestTimeStamp,info.getTableName()));
							BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Change the table name from "+
									inheritTableInfo.getOldTableName()+" to " +inheritTableInfo.getTableName() + 
									" because record number of partition exceed to max record number per partition.");
							if( i < (inheritTableList.size()-1)) {
								//if the table is not the last partition, change the end time and add add a new partition
								inheritTableInfo = new InheritTableInfo();
								//add inherit table
								inheritTableInfo.setSchemaName(info.getSchemaName());
								inheritTableInfo.setTableName(getNameByTimeStamp(latestTimeStamp,timeStamp[1],info.getTableName()));
								inheritTableInfo.setParentTableName(info.getTableName());
								inheritTableInfo.setTimeField(info.getTimeField());
								inheritTableInfo.setBeginTimestamp(latestTimeStamp);
								inheritTableInfo.setEndTimestamp(timeStamp[1]);
								inheritTableInfo.setExisted(false);
								inheritInfoList.add(inheritTableInfo);
								latestTimeStamp = timeStamp[1];
							}
						}
					}
				}
			}
		}
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now-maxtime*3600*1000L);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
			
		if(0 == latestTimeStamp) {
			latestTimeStamp = calendar.getTimeInMillis();
		}
		
		//create inherit table
		while (latestTimeStamp < latestTableTimeStamp) {
			inheritTableInfo = new InheritTableInfo();
			inheritTableInfo.setSchemaName(info.getSchemaName());
			inheritTableInfo.setParentTableName(info.getTableName());
			inheritTableInfo.setTimeField(info.getTimeField());
			inheritTableInfo.setBeginTimestamp(latestTimeStamp);
			latestTimeStamp = latestTimeStamp + interval*3600*1000L;
			inheritTableInfo.setEndTimestamp(latestTimeStamp);
			inheritTableInfo.setExisted(false);
			inheritTableInfo.setTableName(getNameByTimeStamp(inheritTableInfo.getBeginTimestamp(),inheritTableInfo.getEndTimestamp(),info.getTableName()));
			inheritInfoList.add(inheritTableInfo);
			isChanged = true;
		}
		if(false == isChanged) {
//			return null;
		}
		return inheritInfoList;
	}
	
	/**
	 * remove chlid table
	 * @param childTableName
	 * @param schemaName
	 * @param schemaName
	 */
	private void removeChildTable(String childTableName, String schemaName) {
		StringBuffer sql = new StringBuffer();
		sql.append("DROP TABLE ");
		sql.append(schemaName).append(".").append(childTableName);
		
		try {
			QueryUtil.executeNativeUpdate(sql.toString());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to drop chlid table " + childTableName 
					+ ",SQL:" + sql.toString(),e);
		}
		return;
	}
	
	/**
	 * remove expired tables
	 * @param maxtime	unit is hour
	 * @param interval	unit is hour
	 * @param info
	 */
	private void removeExpiredTables(int maxtime, int interval,TablePartitionInfo info) {
		long now = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now-maxtime*3600*1000L-interval*3600*1000L);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		//get tables which are used inherit from table
		StringBuffer sql = new StringBuffer();
		sql.append("select tablename from pg_tables where schemaname= '");
		sql.append(info.getSchemaName());
		sql.append("' and tablename similar to '");
		sql.append(info.getTableName()).append("_[0-9]{4,10}_[0-9]{4,10}' order by tablename asc");
		
		List<String> tableList = new ArrayList<String>();
		try {
			List<?> resList = QueryUtil.executeNativeQuery(sql.toString());
			if(resList != null) {
				for (Object obj: resList) {
					tableList.add(obj.toString());
				}
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get all sub tables for table " + info.getSchemaName() + "." + info.getTableName()
					+ ",SQL:" + sql.toString(),e);
		}
		
		//remove expired table
		for(String name:tableList) {
			long[] tableTimeStamp = getTimeStampByTableName(name, info.getTableName(), info.getFullTableName());
			if(tableTimeStamp != null && tableTimeStamp[1] <= calendar.getTimeInMillis()) {
				//remove expired table
				removeChildTable(name,info.getSchemaName());
			}
			
		}
		
		return ;
	}
	
	/**
	 * get index definition of table
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getIndexDefinitionOfTable(String schemaName, String tableName) {
		//get the indexes definition of parent table
		StringBuffer sql = new StringBuffer();
		sql.append("select indexname,indexdef from pg_indexes where schemaname='");
		sql.append(schemaName).append("' and tablename='").append(tableName).append("'");
		List<Object[]> resList = null;
		try {
			resList = (List<Object[]>)QueryUtil.executeNativeQuery(sql.toString());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get index definition of  table " + schemaName + "." + tableName
					+ ",SQL:" + sql.toString(),e);
		}
		return resList;
	}
	/**
	 * create inherit table and create index for this inherit table
	 * @param inheritInfo
	 * 
	 */
	private void createInheritTable(InheritTableInfo inheritInfo,List<Object[]> indexDefList) {
		//create inherit table
		StringBuffer sql = new StringBuffer();
		sql.append("create table ");
		sql.append(inheritInfo.getSchemaName()).append(".").append(inheritInfo.getTableName());
		sql.append(" (check (").append(inheritInfo.getTimeField()).append(">=").append(inheritInfo.getBeginTimestamp());
		sql.append(" and ").append(inheritInfo.getTimeField()).append("<").append(inheritInfo.getEndTimestamp());
		sql.append(")) INHERITS (");
		sql.append(inheritInfo.getSchemaName()).append(".").append(inheritInfo.getParentTableName());
		sql.append(")");
		
		try {
			QueryUtil.executeNativeUpdate(sql.toString());
			BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Create child table(" 
					+ inheritInfo.getTableName() + ") of table "+ 
					 inheritInfo.getSchemaName() + "." + inheritInfo.getParentTableName());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to create child table(" 
					+ inheritInfo.getTableName() + ") of table "+ 
					 inheritInfo.getSchemaName() + "." + inheritInfo.getParentTableName()
					 + ",SQL:" + sql.toString(), e);
		}
		
		//create index for table
		try {
			if(null != indexDefList) {
				for (Object[] objs:indexDefList) {
					String indexName = objs[0].toString();
					String indexDef = objs[1].toString();
					String string_sql = indexDef.replaceAll("INDEX "+indexName,"INDEX "+getNameByTimeStamp(inheritInfo.getBeginTimestamp(),inheritInfo.getEndTimestamp(),indexName));
					if(inheritInfo.getSchemaName().equalsIgnoreCase("public")) {
						string_sql = string_sql.replace("ON "+inheritInfo.getParentTableName(), "ON "+inheritInfo.getTableName());
					} else {
						string_sql = string_sql.replace("ON "+inheritInfo.getSchemaName()+"."+inheritInfo.getParentTableName(), 
								"ON "+inheritInfo.getSchemaName()+"."+inheritInfo.getTableName());
					}
					QueryUtil.executeNativeUpdate(string_sql);
				}
				
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to create index  of table "+ 
					 inheritInfo.getSchemaName() + "." + inheritInfo.getTableName()
					 + ",SQL:" + sql.toString(),e);
		}
		return;
	}
	
	/**
	 * create trigger function
	 * @param schemaName
	 * @param tableName
	 * @param timeField
	 * @param triggerFunctionName
	 * @param inheritInfoList
	 */
	private void createTriggerFunction(String schemaName,String tableName,String timeField,
			String triggerFunctionName,List<InheritTableInfo> inheritInfoList) {
		StringBuffer sql = new StringBuffer();
		
		//lock the table when create trigger and rename the table
		sql.append("lock TABLE ").append(schemaName).append(".").append(tableName).append(" in EXCLUSIVE mode;\n");
		
		sql.append("CREATE OR REPLACE FUNCTION ").append(schemaName).append(".").append(triggerFunctionName).append("()\n");
		sql.append("RETURNS TRIGGER AS $$\n");
		sql.append("BEGIN\n");
		
		//generate SQL for create trigger function
		for (int i = 0; i < inheritInfoList.size(); i++) {
			InheritTableInfo inheritInfo = inheritInfoList.get(inheritInfoList.size() - i - 1);
			if (i == 0) {
				sql.append("	IF");
			}
			else {
				sql.append("	ELSIF");
			}
			sql.append(" (NEW.").append(timeField).append(" >= ").append(inheritInfo.getBeginTimestamp());
			sql.append(" and NEW.").append(timeField).append(" < ").append(inheritInfo.getEndTimestamp());
			sql.append(") THEN\n");
			sql.append("		INSERT INTO ").append(inheritInfo.getSchemaName()).append(".");
			sql.append(inheritInfo.getTableName()).append("  VALUES(NEW.*);\n");
			if(i == (inheritInfoList.size()-1)) {
				sql.append("	END IF;\n");
			}
		}
		
		sql.append("    RETURN NULL;\n");
		sql.append("END;\n");
		sql.append("$$\n");
		sql.append("LANGUAGE plpgsql;\n");
		
		//generate SQL to rename table name
		for (int i = 0; i < inheritInfoList.size(); i++) {
			InheritTableInfo inheritInfo = inheritInfoList.get(i);
			if(inheritInfo.getOldTableName() != null) {
				sql.append("ALTER TABLE ").append(inheritInfo.getOldTableName());
				sql.append(" RENAME TO ").append(inheritInfo.getTableName()).append(";\n");
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Rename child table name from  " 
						+ inheritInfo.getOldTableName() + "to " + inheritInfo.getTableName() + " of table "+ 
						schemaName + "." + tableName);
			}
		}
		try {
			QueryUtil.executeNativeUpdate(sql.toString());
			BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Create trigger function " 
					+ triggerFunctionName + " of table "+ 
					schemaName + "." + tableName);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to create trigger function " 
					+ triggerFunctionName + " of table "+ schemaName + "." + tableName
					+ ",SQL:" + sql.toString(),e);
		}
		
		//change the check constraint
		sql = new StringBuffer();
		for (int i = 0; i < inheritInfoList.size(); i++) {
			InheritTableInfo inheritInfo = inheritInfoList.get(i);
			if(inheritInfo.getOldTableName() != null) {
				sql.append("ALTER TABLE ").append(inheritInfo.getTableName());
				sql.append(" DROP CONSTRAINT IF EXISTS ").append(inheritInfo.getOldTableName()).append("_");
				sql.append(inheritInfo.getTimeField()).append("_check;\n");
				sql.append("ALTER TABLE ").append(inheritInfo.getTableName());
				sql.append(" add check (").append(inheritInfo.getTimeField()).append(">=").append(inheritInfo.getBeginTimestamp());
				sql.append(" and ").append(inheritInfo.getTimeField()).append("<").append(inheritInfo.getEndTimestamp());
				sql.append(");");
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Change the check constraint of table "+ 
						schemaName + "." + inheritInfo.getTableName());
			}
		}
		try {
			if(!sql.toString().equalsIgnoreCase("")) {
				QueryUtil.executeNativeUpdate(sql.toString());
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to change the check constraint of table "
					+ schemaName + "." + tableName
					+ ",SQL:" + sql.toString(),e);
		}
		return;
	}
	/**
	 * create trigger
	 * @param schemaName
	 * @param tableName
	 */
	private void createTrigger(String schemaName,String tableName,String triggerFunctionName) {
		StringBuffer triggerName = new StringBuffer();
		triggerName.append(schemaName).append("_").append(tableName).append("_insert_trigger");
		StringBuffer sql = new StringBuffer();
		
		try {
			//check existed of trigger
			sql.append("select tgname from pg_trigger where tgrelid in (select a.oid from pg_class a , pg_namespace b where a.relnamespace = b.oid and b.nspname='");
			sql.append(schemaName).append("' and relname='").append(tableName).append("') and tgname='");
			sql.append(triggerName.toString()).append("'");
			
			List<?> resList = QueryUtil.executeNativeQuery(sql.toString(), 1);
			if (resList.size() != 1) {
				//trigger is not exist
				//create trigger
				sql = new StringBuffer();
				sql.append("CREATE TRIGGER ").append(triggerName.toString()).append("\n");
				sql.append("BEFORE INSERT ON ").append(schemaName).append(".").append(tableName).append("\n");
				sql.append("FOR EACH ROW\n");
				sql.append("EXECUTE PROCEDURE ").append(schemaName).append(".").append(triggerFunctionName).append("()");
				QueryUtil.executeNativeUpdate(sql.toString());
				BeLogTools.info(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Create trigger(" 
					+ triggerName.toString() + ") of table "+ 
					 schemaName + "." + tableName);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Fail to create trigger(" 
					+ triggerName.toString() + ") of table "+ 
					 schemaName + "." + tableName
					 + ",SQL:" + sql.toString(),e);
		}
	}
	/**
	 * create inherit tables
	 * @param inheritTableList
	 */
	private void createInheritTables(TablePartitionInfo info,List<InheritTableInfo> inheritInfoList) {
		//get index definition of table
		List<Object[]> indexDefList = getIndexDefinitionOfTable(info.getSchemaName(),info.getTableName());
		
		//create inherit table
		for(InheritTableInfo inheritInfo: inheritInfoList) {
			if(!inheritInfo.isExisted())
				createInheritTable(inheritInfo,indexDefList);
		}
		
		//create trigger function
		StringBuffer triggerFunctionName = new StringBuffer();
		triggerFunctionName.append(info.getTableName()).append("_insert_trigger_function");
		createTriggerFunction(info.getSchemaName(),info.getTableName(),info.getTimeField(),
				triggerFunctionName.toString(),inheritInfoList);
		
		//create trigger
		createTrigger(info.getSchemaName(),info.getTableName(),triggerFunctionName.toString());
	}
	
	
	
	
	/**
	 * maintain table partition
	 * @param maxtime	unit is hour
	 * @param interval	unit is hour
	 * @param maxRecordNum
	 * @param maxRecordNumPerPartition
	 * @param info
	 */
	private void maintainTablePartition(int maxtime, int interval,int maxRecordNum,
			int maxRecordNumPerPartition,TablePartitionInfo info) {
		//get valid inherit tables and unlink invalid inherit table
		List<InheritTableInfo> inheritInfoList = getValidInheritTables(maxtime, interval, maxRecordNum,
				maxRecordNumPerPartition,info);
		
		if(null != inheritInfoList) {
			//create inherit tables
			createInheritTables(info,inheritInfoList);
			
			//remove expired tables
			removeExpiredTables(maxtime,interval,info);
		}
	}
	
	private class InheritTableInfo {
		private String	schemaName = null;
		private String	tableName = null;
		private String	parentTableName = null;
		private String	timeField = null;
		private long	beginTimestamp = 0;
		private long	endTimestamp = 0;
		private boolean	existed = true;
		
		//old table name if table name should be changed
		private String oldTableName = null;
		
		public String getSchemaName() {
			return schemaName;
		}
		public void setSchemaName(String schemaName) {
			this.schemaName = schemaName;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getParentTableName() {
			return parentTableName;
		}
		public void setParentTableName(String parentTableName) {
			this.parentTableName = parentTableName;
		}
		public String getTimeField() {
			return timeField;
		}
		public void setTimeField(String timeField) {
			this.timeField = timeField;
		}
		public long getBeginTimestamp() {
			return beginTimestamp;
		}
		public void setBeginTimestamp(long beginTimestamp) {
			this.beginTimestamp = beginTimestamp;
		}
		public long getEndTimestamp() {
			return endTimestamp;
		}
		public void setEndTimestamp(long endTimestamp) {
			this.endTimestamp = endTimestamp;
		}
		public boolean isExisted() {
			return existed;
		}
		public void setExisted(boolean existed) {
			this.existed = existed;
		}
		public String getOldTableName() {
			return oldTableName;
		}
		public void setOldTableName(String oldTableName) {
			this.oldTableName = oldTableName;
		}
		
	}
	
	/**
	 * remove all inherit table, this is for debug , please don't call this method
	 */
//	@Deprecated
	private void removeAllInheritTable() {
		synchronized(tableMap) {
			Collection<TablePartitionInfo> values = tableMap.values();
			for(TablePartitionInfo info:values) {
				//get tables which are used inherit from table
				StringBuffer sql = new StringBuffer();
				sql.append("select tablename from pg_tables where schemaname= '");
				sql.append(info.getSchemaName());
				sql.append("' and tablename similar to '");
				sql.append(info.getTableName()).append("_[0-9]{4,10}_[0-9]{4,10}' order by tablename asc");
				
				List<String> tableList = new ArrayList<String>();
				try {
					List<?> resList = QueryUtil.executeNativeQuery(sql.toString());
					if(resList != null) {
						for (Object obj: resList) {
							tableList.add(obj.toString());
						}
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get all sub tables for table " + info.getSchemaName() + "." + info.getTableName()
							+ ",SQL:" + sql.toString(),e);
				}
				
				//remove expired table
				for(String name:tableList) {
					//remove expired table
					removeChildTable(name,info.getSchemaName());
					
				}
				//remove trigger
				StringBuffer triggerName = new StringBuffer();
				triggerName.append(info.getSchemaName()).append("_").append(info.getTableName()).append("_insert_trigger");
				
				sql = new StringBuffer();
				sql.append("drop trigger ").append(triggerName).append(" on ").append(info.getTableName());
				try {
					QueryUtil.executeNativeUpdate(sql.toString());
					
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get all sub tables for table " + info.getSchemaName() + "." + info.getTableName()
							+ ",SQL:" + sql.toString(),e);
				}
				
				//remove trigger function
				StringBuffer triggerFunctionName = new StringBuffer();
				triggerFunctionName.append(info.getTableName()).append("_insert_trigger_function");
				
				sql = new StringBuffer();
				sql.append("drop function ").append(triggerFunctionName).append("()");
				
				try {
					QueryUtil.executeNativeUpdate(sql.toString());
					
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"TablePartitionProcessor:Cannot get all sub tables for table " + info.getSchemaName() + "." + info.getTableName()
							+ ",SQL:" + sql.toString(),e);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUtil.init(false);
		TablePartitionProcessor processor = new  TablePartitionProcessor();
//		processor.removeRedundantRecordsByIdAndTimeField("test", 1, "timestamp");
		processor.removeAllInheritTable();
//		processor.start();
//		processor.stop();
		processor.tablePartitionManage();
	}

}
