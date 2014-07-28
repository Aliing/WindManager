package com.ah.be.performance.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ah.bo.HmBo;

public class BulkOperationInfo {
	static final public int 	OPERATION_TYPE_INSERT			= 1;
	static final public int		OPERATION_TYPE_UPDATE			= 2;
	static final public int		OPERATION_TYPE_DELETE			= 3;
	static final public int		OPERATION_TYPE_DELETE_INSERT	= 4;
	
	//bulk operation type
	private int	operationType = OPERATION_TYPE_INSERT;
	
	//for all operation type
	//class for table
	Class<? extends HmBo>				boClass;
		
	//the number of bulk in the same time
	private int bulkSyncCount = 0;
	
	//list for bulk insert
	private List<HmBo>	boList = new LinkedList<HmBo>();
	
	//last bulk timestamp
	private long lastBulkTime = System.currentTimeMillis();
	
	//total lost records for talbe
	private long lostRecords = 0;
	
	//for delete insert operation type
	//table name
	private String tableName = null;
	
	//delete condition field
	private String	fieldName = null;
	
	//delete condition value list 
	private Map<String,List<HmBo>> boListMap = new HashMap<String,List<HmBo>>();
	
	BulkOperationInfo() {
		
	}
	
	public int getBoSize() {
		int size = 0;
		switch (operationType) {
		case OPERATION_TYPE_INSERT:
			size = boList.size();
			break;
		case OPERATION_TYPE_DELETE_INSERT:
			Collection<List<HmBo>> boListSet = boListMap.values();
			for(List<HmBo> boList: boListSet) {
				size += boList.size();
			}
			break;
		default:
			break;
		}
		
		return size;
	}
	
	
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public Class<? extends HmBo> getBoClass() {
		return boClass;
	}
	public void setBoClass(Class<? extends HmBo> boClass) {
		this.boClass = boClass;
	}
	public List<HmBo> getBoList() {
		return boList;
	}
	
	public int getBulkSyncCount() {
		return bulkSyncCount;
	}

	public void setBulkSyncCount(int bulkSyncCount) {
		this.bulkSyncCount = bulkSyncCount;
	}

	public long getLastBulkTime() {
		return lastBulkTime;
	}
	public void setLastBulkTime(long lastBulkTime) {
		this.lastBulkTime = lastBulkTime;
	}

	public long getLostRecords() {
		return lostRecords;
	}

	public void setLostRecords(long lostRecords) {
		this.lostRecords = lostRecords;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Map<String, List<HmBo>> getBoListMap() {
		return boListMap;
	}

}

class BulkClassInfo {
	Class<? extends HmBo> boClass;
	
	//bulk operation type
	private int	operationType = BulkOperationInfo.OPERATION_TYPE_INSERT;
	
	private String	fieldName = null;

	public Class<? extends HmBo> getBoClass() {
		return boClass;
	}

	public void setBoClass(Class<? extends HmBo> boClass) {
		this.boClass = boClass;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
