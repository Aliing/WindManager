package com.ah.be.performance.db;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.Table;

import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.event.AhTimeoutEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhDeviceRebootHistory;
import com.ah.bo.performance.AhDeviceStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhPCIData;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhReportAppDataHour;
import com.ah.bo.performance.AhReportAppDataSeconds;
import com.ah.bo.performance.AhSwitchPortPeriodStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;

public class BulkOperationProcessor {
	
	static final private int								BULK_INSERT_SYNC_NUMBER = 2;
	static final private int								BULK_DELETE_INSERT_SYNC_NUMBER = 1;
	static final private int								MAX_BULK_RECORD_NUMBER = 1000;
	static final private int								MAX_RECORD_NUMBER = 100000;
	static final private int								BULK_OPERATION_INTERVAL = 5*1000;
	
	private int												processorThreadNum = 20;
	
	static private Map<String,BulkClassInfo> 				classMap = null;
	
	static private Map<String, BulkOperationInfo>			infoMap = null;
	
	private boolean											isContinue = true;
	
	static private BlockingQueue<BeBaseEvent> 				eventQueue = null;
	
	EventProcessorThread[]									processorThreadArray = null;
	
	
	public BulkOperationProcessor() {
		classMap = Collections.synchronizedMap(new HashMap<String,BulkClassInfo>());
		infoMap = Collections.synchronizedMap(new HashMap<String, BulkOperationInfo>(processorThreadNum));
		
		//init class map
		addInsertClassMap(AhClientStats.class);
		addInsertClassMap(AhAssociation.class);
		addInsertClassMap(AhACSPNeighbor.class);
		addInsertClassMap(AhInterfaceStats.class);
		addInsertClassMap(AhInterferenceStats.class);
		addInsertClassMap(AhNeighbor.class);
		addInsertClassMap(AhXIf.class);
		addInsertClassMap(AhVIfStats.class);
		addInsertClassMap(AhRadioAttribute.class);
		addInsertClassMap(AhRadioStats.class);
		addInsertClassMap(AhBandWidthSentinelHistory.class);
		addInsertClassMap(AhDeviceStats.class);
		//addInsertClassMap(AhAppDataHour.class);
		//addInsertClassMap(AhAppDataSeconds.class);
		addInsertClassMap(AhReportAppDataHour.class);
		addInsertClassMap(AhReportAppDataSeconds.class);
		addInsertClassMap(APConnectHistoryInfo.class);
		addInsertClassMap(AhSwitchPortPeriodStats.class);
		addInsertClassMap(AhDeviceRebootHistory.class);
		addInsertClassMap(AhPCIData.class);
		
		addDeleteInsertClassMap(AhLatestRadioAttribute.class,"apmac");
		addDeleteInsertClassMap(AhLatestNeighbor.class,"apmac");
		addDeleteInsertClassMap(AhLatestXif.class,"apmac");
		addDeleteInsertClassMap(AhLatestInterferenceStats.class,"apmac");
		addDeleteInsertClassMap(AhLatestACSPNeighbor.class,"apmac");
		
//		processorThreadNum = classMap.size();
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(processorThreadNum*10);
		
	}
	
	private void addInsertClassMap(Class<? extends HmBo> boClass) {
		try {
			BulkClassInfo classInfo = new BulkClassInfo();
			classInfo.setBoClass(boClass);
			classMap.put(boClass.getSimpleName(), classInfo);
		} catch (Exception e) {
		}
	}
	
	private void addDeleteInsertClassMap(Class<? extends HmBo> boClass,String fieldName) {
		try {
			BulkClassInfo classInfo = new BulkClassInfo();
			classInfo.setBoClass(boClass);
			classInfo.setFieldName(fieldName);
			classInfo.setOperationType(BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT);
			classMap.put(boClass.getSimpleName(), classInfo);
		} catch (Exception e) {
		}
	}
	
	private BulkOperationInfo addBulkInsertInfo(Class<? extends HmBo> boClass) {
		if(infoMap == null)
			return null;
		BulkOperationInfo info = infoMap.get(boClass.getSimpleName());
		if(info == null) {
			info = new BulkOperationInfo();
			info.setOperationType(BulkOperationInfo.OPERATION_TYPE_INSERT);
			info.setBoClass(boClass);
			infoMap.put(boClass.getSimpleName(), info);
		}
		return info;
	}
	
	private BulkOperationInfo addBulkDeleteInsertInfo(BulkClassInfo classInfo) {
		if(infoMap == null)
			return null;
		Class<? extends HmBo> boClass = classInfo.getBoClass();
		BulkOperationInfo info = infoMap.get(boClass.getSimpleName());
		if(info == null) {
			info = new BulkOperationInfo();
			info.setOperationType(BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT);
			info.setBoClass(boClass);
			
			//get table name
			String tableName = null;
			Annotation[] annos = boClass.getAnnotations();
			for (Annotation anno : annos) {
				if(anno.annotationType().getName().equalsIgnoreCase(Table.class.getName())){
					tableName = ((Table)anno).name().toLowerCase();
					break;
				}
			}
			info.setTableName(tableName);
			info.setFieldName(classInfo.getFieldName());
			infoMap.put(boClass.getSimpleName(), info);
		}
		return info;
	}
	
	/**
	 * add insert bo list
	 * @param boClass
	 * @param boList
	 * @return true, false if list is full for this table
	 */
	static public boolean addBoList(Class<? extends HmBo> boClass, Collection<? extends HmBo> boList) {
		if(boList == null || boList.size() <= 0 || infoMap == null)
			return true;
		boolean ret = true;
		synchronized(infoMap) {
			BulkOperationInfo info = infoMap.get(boClass.getSimpleName());
			if(info == null || info.getOperationType() != BulkOperationInfo.OPERATION_TYPE_INSERT) {
				BeLogTools.warn(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Cannot get bulk insert operation info for class" + boClass.getSimpleName());
				return false;
			}
			if(info.getBoList().size() > MAX_RECORD_NUMBER) {
				info.setLostRecords(info.getLostRecords()+boList.size());
				BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"List for class " + boClass.getSimpleName() + " is full, total discard "+info.getLostRecords()+" records");
				ret = false;
			}
			else {
				if(info.getBoList().size() <= 0)
					info.setLastBulkTime(System.currentTimeMillis());
				for(HmBo bo: boList) {
					if(bo.getClass() == boClass)
						info.getBoList().add(bo);
				}
			}
			AhTimeoutEvent timer = new AhTimeoutEvent();
			addEvent(timer);
		}
		return ret;
	}
	/**
	 * add delete insert bo list
	 * @param boClass
	 * @param boList
	 * @return true, false if list is full for this table
	 */
	static public boolean addDeleteInsertBoList(Class<? extends HmBo> boClass, Collection<? extends HmBo> boList,String keyField) {
		if(boList == null || infoMap == null)
			return true;
		boolean ret = true;
		synchronized(infoMap) {
			BulkOperationInfo info = infoMap.get(boClass.getSimpleName());
			if(info == null || info.getOperationType() != BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT) {
				BeLogTools.warn(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Cannot get bulk delete insert operation info for class" + boClass.getSimpleName());
				return false;
			}
			if(info.getBoSize() > MAX_RECORD_NUMBER) {
				info.setLostRecords(info.getLostRecords()+boList.size());
				BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"List for class " + boClass.getSimpleName() + " is full, total discard "+info.getLostRecords()+" records");
				ret = false;
			}
			else {
				if(info.getBoSize() <= 0)
					info.setLastBulkTime(System.currentTimeMillis());
				List<HmBo> infoBoList = info.getBoListMap().get(keyField);
				if(infoBoList != null) {
					infoBoList.clear();
				}
				else {
					infoBoList = new ArrayList<HmBo>(boList.size());
				}
				for(HmBo bo: boList) {
					if(bo.getClass() == boClass)
						infoBoList.add(bo);
				}
				info.getBoListMap().put(keyField, infoBoList);
			}
			AhTimeoutEvent timer = new AhTimeoutEvent();
			addEvent(timer);
		}
		return ret;
	}
	
	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	static public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,
					"BulkOperationProcessor.getEvent(): Exception while add event to queue",
					e);
		}
	}

	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeBaseEvent getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,
							"BulkOperationProcessor.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}
	
	public void start() {
		isContinue = true;
		
		for(BulkClassInfo classInfo : classMap.values()) {
			switch(classInfo.getOperationType()) {
			case BulkOperationInfo.OPERATION_TYPE_INSERT: 
				addBulkInsertInfo(classInfo.getBoClass());
				break;
			case BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT: 
				addBulkDeleteInsertInfo(classInfo);
				break;
			}
		}
		
		processorThreadArray = new EventProcessorThread[processorThreadNum];
		for(int i = 0; i < processorThreadNum; i++) {
			processorThreadArray[i] = new EventProcessorThread(i+1);
			processorThreadArray[i].start();
		}
	}

	public void stop() {
		isContinue = false;
		if(processorThreadArray != null) {
			AhTimeoutEvent timer = new AhTimeoutEvent();
			for(int i = 0; i < processorThreadNum; i++) {
				addEvent(timer);
			}
		}
	}

	
	private void bulkOperation(int index) {
		Set<String> keySet = infoMap.keySet();
		for(String name: keySet) {
			BulkOperationInfo info = null;
			int	operationType = 0;
			Class<? extends HmBo> boClass = null;
			String tableName = null;
			String fieldName = null;
			List<HmBo>  boList = new LinkedList<HmBo>();
			List<String> keyList = new ArrayList<String>();
			int boSize = 0;
			while(info == null || boSize > MAX_BULK_RECORD_NUMBER) {
				synchronized(infoMap) {
					info = infoMap.get(name);
					if(info == null)
						break;
					if(info.getBoSize() <= 0)
						break;
					else if (info.getBoSize() < MAX_BULK_RECORD_NUMBER) {
						if ((System.currentTimeMillis()-info.getLastBulkTime()) < BULK_OPERATION_INTERVAL)
							break;
					}
					boClass = info.getBoClass();
					operationType = info.getOperationType();
					tableName = info.getTableName();
					fieldName = info.getFieldName();
					
					if(operationType == BulkOperationInfo.OPERATION_TYPE_INSERT) {
						if(info.getBulkSyncCount() >= BULK_INSERT_SYNC_NUMBER)
							break;
						try {
							int lastIndex = info.getBoList().size()>MAX_BULK_RECORD_NUMBER ? MAX_BULK_RECORD_NUMBER:info.getBoList().size();
							List<HmBo> boListTemp = info.getBoList().subList(0, lastIndex);
							boList.addAll(boListTemp);
							boListTemp.clear();
						} catch (Exception e) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]: fail to move bolist", e);
							boList.addAll(info.getBoList());
						}
					} else if(operationType == BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT) {
						if(info.getBulkSyncCount() >= BULK_DELETE_INSERT_SYNC_NUMBER)
							break;
						try {
							Set<String> infoKeySet = info.getBoListMap().keySet();
							for(String infoKey: infoKeySet) {
								List<HmBo> infoBoList = info.getBoListMap().get(infoKey);
								keyList.add(infoKey);
								if(null != infoBoList) {
									boList.addAll(infoBoList);
									if(boList.size() >= MAX_BULK_RECORD_NUMBER)
										break;
								}
							}
							for(String infoKey: keyList) {
								info.getBoListMap().remove(infoKey);
							}
							
						} catch (Exception e) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]: fail to move bolistmap", e);
						}
					}
					info.setBulkSyncCount(info.getBulkSyncCount()+1);
					info.setLastBulkTime(System.currentTimeMillis());
				}
				//bulk operation
				switch(operationType) {
				case BulkOperationInfo.OPERATION_TYPE_INSERT:
					bulkInsert(boClass,boList,index);
					boList.clear();
					break;
				case BulkOperationInfo.OPERATION_TYPE_DELETE_INSERT:
					bulkDelete(boClass,tableName,fieldName,keyList,index);
					bulkInsert(boClass,boList,index);
					boList.clear();
					keyList.clear();
					break;
				default:
					break;
				}
				
				synchronized(infoMap) {
					info = infoMap.get(name);
					if(info == null)
						break;
					info.setBulkSyncCount(info.getBulkSyncCount()-1);
					info.setLastBulkTime(System.currentTimeMillis());
					boSize = info.getBoSize();
				}
			}
		}
	}
	
	private void bulkDelete(Class<? extends HmBo> boClass,String tableName,String fieldName,List<String> keyList,int index) {
		try {
			long begin = System.currentTimeMillis();
			StringBuffer sql = new StringBuffer();
			if (null != classMap.get(boClass.getSimpleName())) {
				if(keyList.size() > 0) {
					sql.append("delete from ").append(tableName).append(" where ").append(fieldName).append(" in (");
					for(int i = 0; i < keyList.size(); i++) {
						sql.append("'").append(keyList.get(i)).append("'");
						if(i != (keyList.size()-1))
							sql.append(",");
					}
					sql.append(")");
					QueryUtil.executeNativeUpdate(sql.toString());
				}
			} else {
				BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]: Unknow class "+boClass.getSimpleName());
			}
			
			BeLogTools.info(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]:Bulk delete  for "+boClass.getSimpleName()+" and for "+keyList.size()+" devices, eclipse "+(System.currentTimeMillis()-begin)+"ms:"+sql.toString());
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]:Fail to bulk delete for "+boClass.getSimpleName(), e);
		}
	}
	private void bulkInsert(Class<? extends HmBo> boClass, List<HmBo> boList,int index) {
		try {
			long begin = System.currentTimeMillis();
			if (null != classMap.get(boClass.getSimpleName())) {
				BulkUpdateUtil.bulkInsert(boClass, boList);
			} else {
				BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]: Unknow class "+boClass.getSimpleName());
			}
			
			BeLogTools.info(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]:Bulk Insert "+boList.size()+" records for "+boClass.getSimpleName()+", eclipse "+(System.currentTimeMillis()-begin)+"ms");
		} catch (SQLException e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Bulk operation processor ["+index+"]:Fail to bulk insert for "+boClass.getSimpleName()+", the record number is "+boList.size(), e);
		}
	}
	
	class EventProcessorThread extends Thread {
		private int index = 1;
		
		public EventProcessorThread(int index) {
			this.index = index;
		}
		@Override
		public void run() {
			this.setName("Bulk operation processor ["+index+"]");
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Bulk operation processor ["+index+"] - event processor is running...");

			while (isContinue) {
				try {
					BeBaseEvent event = getEvent();
					if(event == null)
						continue;
					switch(event.getEventType()) {
					case BeEventConst.AH_TIMEOUT_EVENT:
						bulkOperation(index);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Exception in Bulk operation processor thread ["+index+"]", e);
				} catch (Error e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE_BULKOPERATION,"Error in Bulk operation processor thread ["+index+"]", e);
				}
			}
			//bulk operation for remain list
			bulkOperation(index);
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Bulk operation processor ["+index+"] - event processor is shutdown.");
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BulkOperationProcessor processor = new BulkOperationProcessor();
			processor.start();
			
			Thread.sleep(5000);
			AhTimeoutEvent timer = new AhTimeoutEvent();
			addEvent(timer);
			Thread.sleep(5000);
			addEvent(timer);
			Thread.sleep(5000);
			processor.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

