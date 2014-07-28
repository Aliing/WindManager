package com.ah.be.common.db;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.apache.log4j.Level;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhUserLoginSession;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.util.MgrUtil;
import com.mchange.v2.c3p0.C3P0ProxyConnection;

public class BulkUpdateUtil {
	
	private static byte 	BULK_BATCH_INSERT = 1;
	private static byte		BULK_COPY = 2;
	
	private static byte		bulk_type = BULK_COPY;

	/**
	 * bulk insert AhAssociation
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForAssociation(
			Collection<? extends HmBo> boList) throws SQLException {
		bulkInsert(AhAssociation.class,boList,updateInterface);
	}

	/**
	 * bulk insert AhNeighbor
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForNeighbor(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhNeighbor.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert AhInterfaceStats
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForInterfaceStats(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhInterfaceStats.class,boList,updateInterface);
	}
	/**
	 * bulk insert AhClientStats
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForClientStats(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhClientStats.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForEvent(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhEvent.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForAPConnectionHistory(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(APConnectHistoryInfo.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForMaxClientsCount(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhMaxClientsCount.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForAdminLoginSession(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhAdminLoginSession.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForUserLoginSession(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhUserLoginSession.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForInterference(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhInterferenceStats.class,boList,updateInterface);
	}
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForACSPNeighbor(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhACSPNeighbor.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boClass -
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertVPNReport(Class<? extends HmBo> boClass, Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(boClass,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForBandWidthSentinel(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhBandWidthSentinelHistory.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForClientSessionHistory(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhClientSessionHistory.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForRadioAttribute(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhRadioAttribute.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForRadioStats(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhRadioStats.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForXIf(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhXIf.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param boList -
	 * @throws SQLException -
	 */
	public static void bulkInsertForVIfStats(Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(AhVIfStats.class,boList,updateInterface);
	}
	
	/**
	 * bulk insert
	 * 
	 * @param Class<? extends HmBo>
	 * @param boList -
	 * 
	 * @throws SQLException -
	 */
	public static void bulkInsert(Class<? extends HmBo> boClass,Collection<? extends HmBo> boList)
			throws SQLException {
		bulkInsert(boClass,boList,updateInterface);
	}
	
	/**
	 * execute batch sql that have parameter, such as insert ,delete update
	 * which not return resultset
	 * 
	 * @param sql -
	 * @param argls -
	 * @throws SQLException -
	 */
	public static void bulkUpdate(String sql, List<Object[]> argls)
			throws SQLException {
		if (sql == null || sql.equalsIgnoreCase(""))
			return;
	//	EntityManagerFactoryImpl emf = null;
		Connection conn = null;
		PreparedStatement stmt = null;
	//	ConnectionProvider cp = null;
		boolean defaultAutoCommit = true;

		try {
		//	emf = (EntityManagerFactoryImpl) HibernateUtil
		//			.getEntityManagerFactory();
		//	cp = ((SessionFactoryImplementor) emf.getSessionFactory())
		//			.getConnectionProvider();
		//	conn = cp.getConnection();
			conn = QueryUtil.getConnection();
			defaultAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(sql);
			for (Object[] objs : argls) {
				for (int index = 0; index < objs.length; index++) {
					stmt.setObject(index + 1, objs[index]);
				}
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
		//	conn.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
			}
			MgrUtil.logExceptionCause(e);
			throw e;
		} catch (RuntimeException e) {
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {
			}
			try {
				if (conn != null) {
					conn.setAutoCommit(defaultAutoCommit);
				}
			} catch (SQLException e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}

	static private final String s1 = "SELECT a.attname as col_name,a.atttypid as col_type "
			+ "FROM  "
			+ "pg_catalog.pg_attribute a "
			+ "WHERE a.attnum > 0 AND NOT a.attisdropped AND a.attrelid = ( "
			+ "SELECT c.oid  "
			+ "FROM pg_catalog.pg_class c "
			+ "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace "
			+ "WHERE c.relname ~ '^(";
	static private final String s2 = ")$' "
			+ "AND pg_catalog.pg_table_is_visible(c.oid) " + ") ";

	static private final Map<String,TableInfo>	mapTable = new HashMap<String,TableInfo>();
	
	static private final DefaultBulkUpdate updateInterface = new DefaultBulkUpdate();
	
	static private TableInfo initTableInfo(Class<?> boClass,BulkUpdateInterface updateInterface) {
		String key = boClass.getName();
		TableInfo tableInfo = mapTable.get(key);
		if(tableInfo != null)
			return tableInfo;
		//get table name
		String tableName = null;
		Annotation[] annos = boClass.getAnnotations();
		for (Annotation anno : annos) {
			if(anno.annotationType().getName().equalsIgnoreCase(Table.class.getName())){
				tableName = ((Table)anno).name().toLowerCase();
				break;
			}
		}
		tableInfo = new TableInfo();
		//get column info
		String sql = s1 + tableName + s2;
		try {
			List<?> bos = QueryUtil.executeNativeQuery(sql);
			List<String> columnList = new ArrayList<String>(bos.size());
			for (Object bo : bos) {
				Object[] a = (Object[]) bo;
				String colName = (String) a[0];
				//skip id column
				if (!colName.equalsIgnoreCase("id")) {
					columnList.add(colName);
				}
			}
			tableInfo.setAFields(columnList.toArray(new String[columnList.size()]));
			tableInfo.setFields(getFields(tableInfo.getAFields()));
			tableInfo.setSql(getInsertSQL(tableName, tableInfo.getAFields(),tableInfo.getFields()));
			tableInfo.setCopySql(getCopySQL(tableName,tableInfo.getAFields()));
			tableInfo.setUpdateInterface(updateInterface);
			
			Method[] ms = boClass.getMethods();
			String[] fields = tableInfo.getAFields();
			int j, k;
			for (j = 0; j < fields.length; j++) {
				MethodInfo methodInfo = new MethodInfo();
				for (k = 0; k < ms.length; k++) {
					if (ms[k].getName().equalsIgnoreCase("get" + fields[j])) {				
						methodInfo.setGetMethod(ms[k]);
					}
				}
				tableInfo.getMapMethod().put(fields[j].toLowerCase(), methodInfo);
			}
			mapTable.put(key, tableInfo);
		} catch (Exception e) {
			log(Level.ERROR_INT,"Fail to init table info for" + key);
		}
		return tableInfo;
	}
	
	static private String getFields(String[] fieldArray) {
		int i;
		StringBuilder buffer = new StringBuilder();
		for (i = 0; i < fieldArray.length; i++) {
			buffer.append(fieldArray[i]);
			if (i != fieldArray.length - 1)
				buffer.append(",");
		}
		return buffer.toString();
	}

	static private String getInsertSQL(String tableName, String[] fieldArray,
			String fields) {
		int i;
		StringBuilder buffer = new StringBuilder();
		buffer.append("insert into ").append(tableName).append("(");
		buffer.append(fields).append(") values(");
		for (i = 0; i < fieldArray.length; i++) {
			buffer.append("?");
			if (i != fieldArray.length - 1)
				buffer.append(",");
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	static private String getCopySQL(String tableName, String[] fieldArray) {
		StringBuffer buf = new StringBuffer();
		buf.append("copy ").append(tableName).append("(");
		for(int i = 0; i < fieldArray.length; i++) {
			if(0 != i)
				buf.append(",");
			buf.append("\"").append(fieldArray[i]).append("\"");
		}
		buf.append(") from stdin with csv");
		return buf.toString();
	}
	
	
	public static void bulkBatchInsert(Class<?> boClass,
			Collection<? extends HmBo> boList,BulkUpdateInterface updateInterface) throws SQLException {
		if (boList == null || boList.size() == 0)
			return;
		TableInfo tableInfo = initTableInfo(boClass,updateInterface);
		if(null == tableInfo) {
			log(Level.ERROR_INT,"No table info for "+boClass.getSimpleName());
			return;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean defaultAutoCommit = true;

		try {
			conn = QueryUtil.getConnection();
			defaultAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(tableInfo.getSql());

			for (HmBo bo : boList) {
				if(bo.getClass() != boClass)
					continue;
				int pos = 1;
				Object[]  aValue = new Object[1];
				boolean used = false;
				for(String columnName:tableInfo.getAFields()) {
					aValue[0] = null;
					if(tableInfo.getUpdateInterface() != null)
						used = tableInfo.getUpdateInterface().getValue(bo, columnName, aValue);
					if(!used) {
						MethodInfo methodInfo = tableInfo.getMapMethod().get(columnName.toLowerCase());
						if(null == methodInfo) {
							log(Level.ERROR_INT, "There is no method info for column "+columnName);
						}
						else {
							if(methodInfo.getGetMethod() != null) {
								try {
									aValue[0] = methodInfo.getGetMethod().invoke(bo, (Object[])null);
								} catch (Exception e) {
//									log(Level.ERROR_INT,"Fail to invoke for column "+columnName,e);
								}
							}
						}
					}
					stmt.setObject(pos, aValue[0]);
					pos++;
				}
				stmt.addBatch();
			}

			stmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
			}
			MgrUtil.logExceptionCause(e);
			throw e;
		} catch (RuntimeException e) {
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {
			}
			try {
				if (conn != null) {
					conn.setAutoCommit(defaultAutoCommit);
				}
			} catch (SQLException e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}
	
	public static void bulkInsert(Class<? extends HmBo> boClass,
			Collection<? extends HmBo> boList,BulkUpdateInterface updateInterface) throws SQLException {
		if(bulk_type == BULK_COPY) {
			bulkCopy(boClass,boList,updateInterface);
		}
		else if (bulk_type == BULK_BATCH_INSERT) {
			bulkBatchInsert(boClass,boList,updateInterface);
		}
	}
	
	public static void bulkCopy(Class<?> boClass,
			Collection<? extends HmBo> boList,BulkUpdateInterface updateInterface) throws SQLException {
		if (boList == null || boList.size() == 0)
			return;
		TableInfo tableInfo = initTableInfo(boClass,updateInterface);
		if(null == tableInfo) {
			log(Level.ERROR_INT,"No table info for "+boClass.getSimpleName());
			return;
		}
		
		Connection conn = null;
		StringBuffer buf = new StringBuffer();
		
		try {
			conn = QueryUtil.getConnection();
			C3P0ProxyConnection con = (C3P0ProxyConnection) conn;
			Method m = BaseConnection.class.getMethod("getCopyAPI", new Class[]{});
		    Object[] arg = new Object[] {};
		    CopyManager copyManager = (CopyManager) con.rawConnectionOperation(m, C3P0ProxyConnection.RAW_CONNECTION, arg);
			
		    
			for (HmBo bo : boList) {
				if(bo.getClass() != boClass)
					continue;
				int pos = 1;
				Object[]  aValue = new Object[1];
				boolean used = false;
				for(String columnName:tableInfo.getAFields()) {
					aValue[0] = null;
					if(tableInfo.getUpdateInterface() != null)
						used = tableInfo.getUpdateInterface().getValue(bo, columnName, aValue);
					if(!used) {
						MethodInfo methodInfo = tableInfo.getMapMethod().get(columnName.toLowerCase());
						if(null == methodInfo) {
							log(Level.ERROR_INT, "There is no method info for column "+columnName);
						}
						else {
							if(methodInfo.getGetMethod() != null) {
								try {
									aValue[0] = methodInfo.getGetMethod().invoke(bo, (Object[])null);
								} catch (Exception e) {
//									log(Level.ERROR_INT,"Fail to invoke for column "+columnName,e);
								}
							}
						}
					}
					if ( pos != 1)
						buf.append(",");
					if(aValue[0] == null) {
					}
					else if(aValue[0] instanceof String) {
						String stringValue = (String)aValue[0];
						stringValue = stringValue.replace("\"", "\"\"");
						buf.append("\"").append(stringValue).append("\"");
					} else {
						buf.append(aValue[0]);
					}
					pos++;
				}
				buf.append("\n");
			}
			ByteArrayInputStream in = new ByteArrayInputStream(buf.toString().getBytes());
			copyManager.copyIn(tableInfo.getCopySql(), in);
		} catch (Exception e) {
			MgrUtil.logExceptionCause(e);
			log(Level.ERROR_INT,"Exception sql:"+tableInfo.getCopySql()+"------\n"+buf.toString());
			throw new SQLException(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
			}
		}
	}
	
	static private void log(int level,String msg,Throwable t) {
		BeLogTools.log(HmLogConst.M_COMMON,level,BulkUpdateUtil.class.getSimpleName()+":"+msg,t);
	}
	static private void log(int level,String msg) {
		log(level,msg,null);
	}
}

class TableInfo {
	String[] aFields = null;
	String fields = null;
	String sql = null;
	String copySql = null;
	
	BulkUpdateInterface updateInterface = null;

	Map<String, MethodInfo> mapMethod = new HashMap<String, MethodInfo>();

	public String[] getAFields() {
		return aFields;
	}

	public void setAFields(String[] fields) {
		aFields = fields;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getCopySql() {
		return copySql;
	}

	public void setCopySql(String copySql) {
		this.copySql = copySql;
	}

	public Map<String, MethodInfo> getMapMethod() {
		return mapMethod;
	}

	public void setMapMethod(Map<String, MethodInfo> mapMethod) {
		this.mapMethod = mapMethod;
	}

	public BulkUpdateInterface getUpdateInterface() {
		return updateInterface;
	}

	public void setUpdateInterface(BulkUpdateInterface updateInterface) {
		this.updateInterface = updateInterface;
	}
	
}

class MethodInfo {

	Method getMethod;

	Class<?> paramClass;

	public MethodInfo() {

	}

	public Method getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

}