package com.ah.be.common;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhClientSession;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class DBOperationUtil {
	static private ComboPooledDataSource ds = null;
	
	//"jdbc:hsqldb:mem/hm"
	//"jdbc:hsqldb:hsql://localhost/hm"
	//"jdbc:postgresql://10.155.20.68/hm"
	private static String DEFAULT_URL = "jdbc:hsqldb:mem/hm";
	
	private static Pattern 		postgresqlPattern 			= Pattern.compile("jdbc:postgresql:((//([a-zA-Z0-9_\\-.]+|\\[[a-fA-F0-9:]+])((:(\\d+))|))/|)([^\\s?]*).*$");

	//org.postgresql.Driver
	//org.hsqldb.jdbcDriver
	private static String DEFAULT_DRIVER = "org.hsqldb.jdbcDriver";
	
	private static String DEFAULT_USER = "sa";
	
	private static String DEFAULT_PASSWORD = "";
	
	private static String url			= null;
	
	private static String driverName	= null;
	
	private static String dbUserName	= null;
	
	private static String dbPassword	= null;
	 

	private static Map<String, MethodInfo> m_map_ActiveClientColumn = new Hashtable<String, MethodInfo>();
	
	
	public static void init() {
		try {
			//get configure
			url = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_MEMORYDB, ConfigUtil.KEY_MEMDB_URL,DEFAULT_URL);
			driverName = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_MEMORYDB, ConfigUtil.KEY_MEMDB_DRIVER,DEFAULT_DRIVER);
			dbUserName = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_MEMORYDB, ConfigUtil.KEY_MEMDB_USER,DEFAULT_USER);
			dbPassword = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_MEMORYDB, ConfigUtil.KEY_MEMDB_PASSWORD,DEFAULT_PASSWORD);
			
			ds = new ComboPooledDataSource();
			ds.setDriverClass(driverName);
			ds.setJdbcUrl(url);
			ds.setUser(dbUserName);
			ds.setPassword(dbPassword);
			
			ds.setInitialPoolSize(3);
			ds.setMinPoolSize(2);
			ds.setMaxPoolSize(20);
			ds.setAcquireIncrement(1);
			
			ds.setIdleConnectionTestPeriod(300);
			ds.setMaxIdleTime(86400);
		} catch (Exception e1) {
			DebugUtil.commonDebugError("Fail to init C3P0", e1);
		}
		try {
			Class.forName(driverName);
			Connection c = getConnection();
			if(c != null) {
				Statement stat = c.createStatement();
				
				stat.execute("drop table ah_clientsession if exists");
				stat
							.execute("CREATE TABLE PUBLIC.ah_clientsession (id bigint identity,apmac character varying(20),  apname character varying(32),  apserialnumber character varying(14),  applicationhealthscore smallint NOT NULL,  bandwidthsentinelstatus integer NOT NULL,  clientauthmethod smallint NOT NULL,  clientbssid character varying(255),  clientcwpused smallint NOT NULL,  clientchannel integer NOT NULL,  clientencryptionmethod smallint NOT NULL,  clienthostname character varying(32),  clientip character varying(255),  clientmacprotocol smallint NOT NULL,  clientmac character varying(20) NOT NULL,  clientosinfo character varying(255), os_option55 character varying(256),  clientssid character varying(32),  clientuserprofid integer NOT NULL,  clientusername character varying(255),  clientvlan integer NOT NULL,  comment1 character varying(32),  comment2 character varying(32),  connectstate smallint NOT NULL,  endtimestamp bigint NOT NULL,  endtimezone character varying(255),  ifindex integer NOT NULL,  ifname character varying(255),  ipnetworkconnectivityscore smallint NOT NULL,  mapid bigint,  memo character varying(255),email character varying(255),companyName character varying(255),  overallclienthealthscore smallint NOT NULL,  simulated boolean NOT NULL,  slaconnectscore smallint NOT NULL,  starttimestamp bigint NOT NULL,  starttimezone character varying(255), clientrssi integer NOT NULL, wirelessClient boolean NOT NULL, owner bigint NOT NULL, userprofilename character varying(255), SNR smallint  NOT NULL, clientMacBasedAuthUsed smallint  NOT NULL, managedStatus smallint NOT NULL);"); 
				
				stat.execute("CREATE INDEX idx_clientsession_clientmac  ON ah_clientsession (clientmac);");
				stat.execute("CREATE INDEX idx_clientsession_apmac  ON ah_clientsession (apmac);");
				stat.execute("CREATE INDEX idx_clientsession_clientusername ON ah_clientsession (clientusername);");
				stat.close();
				c.close();
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to init JDBC driver", e);
		}
		
		//init method map
		initColumnMethod(AhClientSession.class,AhConvertBOToSQL.CLIENTSESSION_FIELDS_ARRAY,m_map_ActiveClientColumn);
		//special handle
		m_map_ActiveClientColumn.remove("owner");
	}
	
	private   static void initColumnMethod(Class<?> boClass,String[] column_Objs,Map<String, MethodInfo> mapMethod) {
		mapMethod.clear();
		Method[] ms = boClass.getMethods();
		int j = 0, k = 0;
		for (j = 0; j < column_Objs.length; j++) {
			MethodInfo methodInfo = new MethodInfo();
			for (k = 0; k < ms.length; k++) {
				if (ms[k].getName().equalsIgnoreCase("set" + column_Objs[j])) {				
					Class<?>[] cs = ms[k].getParameterTypes();
					if (cs != null && cs.length == 1) {
						methodInfo.setParamClass(cs[0]);
					}
					methodInfo.setMethod(ms[k]);
				}
				if (ms[k].getName().equalsIgnoreCase("get" + column_Objs[j])) {				
//					Class<?>[] cs = ms[k].getParameterTypes();
//					if (cs != null && cs.length == 1) {
//						methodInfo.setParamClass(cs[0]);
//					}
					methodInfo.setGetMethod(ms[k]);
				}
			}
			mapMethod.put(column_Objs[j].toLowerCase(), methodInfo);
		}
	}
	
	
	public static void uninit() {
		try {
			if(ds != null) {
				executeUpdate("shutdown");
			    DataSources.destroy(ds);
			    ds = null;
			}
		} catch (Exception e) {
		}
	}
	
	public static Connection getConnection(){
		try {
            return ds.getConnection();
        } catch (SQLException e) {
        	DebugUtil.commonDebugError("Fail to get connection", e);
        	return null;
        }
	}

	public static String[] parseJDBCUrl(String url) {
		Matcher matcher = postgresqlPattern.matcher(url);
		if (matcher.matches()) {
			return new String[] { matcher.group(3), matcher.group(6),
					matcher.group(7) };
		} else {
			return null;
		}
	}

	/**
	 * execute sql, such as insert ,delete update which not return resultset.
	 * @param sql
	 * @return
	 */
	public static int executeUpdate(String sql) {
		int count = 0;
		if(sql == null || sql.equalsIgnoreCase(""))
			return count;
		Connection c = getConnection();
		if(c == null)
			return count;
		Statement stat = null;
		try {
			stat = c.createStatement();
			count = stat.executeUpdate(sql);
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute update", e);
		} finally {
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return count;
	}
	
	/**
	 * execute sql that have parameter, such as insert ,delete update which not return resultset
	 * @param sql
	 * @param args
	 * @return
	 */
	public static int executeUpdate(String sql,Object[] args) {
		int count = 0;
		if(sql == null || sql.equalsIgnoreCase(""))
			return count;
		Connection c = getConnection();
		if(c == null)
			return count;
		PreparedStatement stat = null;
		try {
			stat = c.prepareStatement(sql);
			if(args != null) {
				for(int index = 0; index < args.length; index++) {
					stat.setObject(index+1, args[index]);
				}
			}
			count = stat.executeUpdate();
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute prepared update", e);
		} finally {
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return count;
	}
	
	/**
	 * execute batch sql that have parameter, such as insert ,delete update which not return resultset
	 * @param sql
	 * @param argls
	 * @return
	 */
	public static int executeBatchUpdate(String sql,List<Object[]> argls) {
		int count = 0;
		if(sql == null || sql.equalsIgnoreCase(""))
			return count;
		Connection c = getConnection();
		if(c == null)
			return count;
		PreparedStatement stat = null;
		try {
			c.setAutoCommit(false);
			stat = c.prepareStatement(sql);
			for(Object[] objs: argls) {
				for(int index = 0; index < objs.length; index++) {
					stat.setObject(index+1, objs[index]);
				}
				stat.addBatch();
			}
			stat.executeBatch();
			c.commit();
			c.setAutoCommit(true);
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute batch update", e);
		} finally {
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return count;
	}
	
	/**
	 * execute query
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static List<?> executeQuery(String sql){
		List<Object> resultList = new ArrayList<Object>();
		if(sql == null || sql.equalsIgnoreCase(""))
			return resultList;
		Connection c = getConnection();
		if(c == null)
			return resultList;
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = c.createStatement();
			rs = stat.executeQuery(sql);
			int columnCount = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				if(columnCount > 1) {
					Object[] objs = new Object[columnCount];
					for(int i = 0; i < columnCount; i++) {
						objs[i] = rs.getObject(i+1);
					}
					resultList.add(objs);
				}
				else {
					resultList.add(rs.getObject(1));
				}
			}
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute query", e);
		} finally {
			try {
				if(null != rs)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return resultList;
	}
	/**
	 * execute query with parameters
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static List<?> executeQuery(String sql,Object[] args) {
		List<Object> resultList = new ArrayList<Object>();
		if(sql == null || sql.equalsIgnoreCase(""))
			return resultList;
		Connection c = getConnection();
		if(c == null)
			return resultList;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = c.prepareStatement(sql);
			if(args != null) {
				for(int index = 0; index < args.length; index++) {
					stat.setObject(index+1, args[index]);
				}
			}
			rs = stat.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				if(columnCount > 1) {
					Object[] objs = new Object[columnCount];
					for(int i = 0; i < columnCount; i++) {
						objs[i] = rs.getObject(i+1);
					}
					resultList.add(objs);
				}
				else {
					resultList.add(rs.getObject(1));
				}
			}
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute query", e);
		} finally {
			try {
				if(null != rs)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return resultList;
	}
	
	private static String getQuery(String sql,SortParams sortParams, 
			FilterParams filterParams, GroupByParams groupByParams,
			Long domainId, int maxResult,int offset) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(sql).append(" ");
		boolean hasWhere  = false;
		if (filterParams != null) {
			hasWhere = true;
			sqlBuffer.append(" where ");
			if (filterParams.getName() != null
					&& null == filterParams.getValue()
					&& null == filterParams.getValues()) {
				sqlBuffer.append(filterParams.getName()).append(" is null");
			} else if (filterParams.getValue() != null) {
				sqlBuffer.append(filterParams.getName()).append(" = ?");
			} else if (filterParams.getWhere() != null) {
				sqlBuffer.append(filterParams.getWhere());
			} else {
				sqlBuffer.append(filterParams.getName()).append(" in (");
				for(int k = 0; k < filterParams.getValues().size(); k++) {
					sqlBuffer.append("?");
					if(k != filterParams.getValues().size() - 1)
						sqlBuffer.append(",");
				}
				sqlBuffer.append(")");
			}
		}
		if (domainId != null) {
			// Add domain filter
			if (hasWhere) {
				sqlBuffer.append(" and ");
			} else {
				hasWhere = true;
				sqlBuffer.append(" where ");
			}
			sqlBuffer.append("owner=").append(domainId);
		}
		if (groupByParams != null) {
			sqlBuffer.append(groupByParams.getQuery());
		}
		
		if(sortParams != null) {
			sqlBuffer.append(" ").append(sortParams.getQuery());
		}
		if(maxResult != 0) {
			sqlBuffer.append(" limit ").append(maxResult);
		}
		if(offset != 0) {
			sqlBuffer.append(" offset ").append(offset);
		}
		//return sqlBuffer.toString();
		// used to support named arguments
		String resultSqlStr = sqlBuffer.toString();
		if (resultSqlStr != null && !"".equals(resultSqlStr)) {
			resultSqlStr = resultSqlStr.replaceAll(":s[\\d]+", "?");
		}
		return resultSqlStr;
	}
	private static void addQueryParameters(PreparedStatement stat,FilterParams filterParams) throws SQLException{
		if (filterParams != null) {
			if (filterParams.getValue() != null) {
				stat.setObject(1, filterParams.getValue());
			} else if (filterParams.getWhere() != null) {
				for(int index = 0; index < filterParams.getBindings().length; index++) {
					stat.setObject(index+1, filterParams.getBindings()[index]);
				}
			} else if (filterParams.getValues() != null) {
				Object[] objs = filterParams.getValues().toArray();
				for(int index = 0; index < objs.length; index++) {
					stat.setObject(index+1, objs[index]);
				}
			}
		}
	}
	/**
	 * execute query 
	 * @param sql
	 * @param sortParams
	 * @param filterParams
	 * @return
	 */
	public static List<?> executeQuery(String sql,SortParams sortParams, 
			FilterParams filterParams){
		return executeQuery(sql,sortParams,filterParams,null);
	}
	public static List<?> executeQuery(String sql,SortParams sortParams, 
			FilterParams filterParams, Long domainId){
		return executeQuery(sql,sortParams,filterParams,domainId,0);
	}
	public static List<?> executeQuery(String sql, SortParams sortParams,
			FilterParams filterParams, GroupByParams groupByParams,
			Long domainId) {
		return executeQuery(sql,sortParams,filterParams,groupByParams,domainId,0,0);
	}
	public static List<?> executeQuery(String sql,SortParams sortParams, 
			FilterParams filterParams, Long domainId, int maxResult){
		return executeQuery(sql,sortParams,filterParams,null,domainId,maxResult,0);
	}
	public static List<?> executeQuery(String sql,SortParams sortParams, 
			FilterParams filterParams, GroupByParams groupByParams,
			Long domainId, int maxResult,int offset){
		List<Object> resultList = new ArrayList<Object>();
		if(sql == null || sql.equalsIgnoreCase(""))
			return resultList;
		String sqlQuery = getQuery(sql,sortParams,filterParams,groupByParams,domainId,maxResult,offset);
		Connection c = getConnection();
		if(c == null)
			return resultList;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = c.prepareStatement(sqlQuery);
			
			addQueryParameters(stat,filterParams);
			
			rs = stat.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				if(columnCount > 1) {
					Object[] objs = new Object[columnCount];
					for(int i = 0; i < columnCount; i++) {
						objs[i] = rs.getObject(i+1);
					}
					resultList.add(objs);
				}
				else {
					resultList.add(rs.getObject(1));
				}
			}
		} catch (SQLException e) {
			DebugUtil.commonDebugError("Fail to execute query", e);
		} finally {
			try {
				if(null != rs)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return resultList;
	}
	
	/**
	 * execute query by class
	 * @param boClass
	 * @param sortParams
	 * @param filterParams
	 * @return
	 */
	public static <T extends HmBo> List<T>	executeQuery(Class<T> boClass,SortParams sortParams, 
			FilterParams filterParams) {
		return executeQuery(boClass,sortParams,filterParams,null,0);
	}
	
	public static <T extends HmBo> List<T>	executeQuery(Class<T> boClass,SortParams sortParams, 
			FilterParams filterParams, Long domainId) {
		return executeQuery(boClass,sortParams,filterParams,domainId,0);
	}
	@SuppressWarnings("unchecked")
	public static  <T extends HmBo> List<T>	executeQuery(Class<T> boClass,SortParams sortParams, 
			FilterParams filterParams, Long domainId, int maxResult) {
		return (List<T>)executeQuery(boClass,sortParams,filterParams,domainId,maxResult,0);
	}
	public static  List<?>	executeQuery(Class<?> boClass,SortParams sortParams, 
			FilterParams filterParams,Long domainId, int maxResult,int offset) {
		List<HmBo> rsList = new ArrayList<HmBo>();
		
		if(boClass != AhClientSession.class)
			return rsList;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select id,").append(AhConvertBOToSQL.CLIENTSESSION_FIELDS)
			.append(" from ah_clientsession ");
		
		String sqlQuery = getQuery(sqlBuffer.toString(),sortParams,filterParams,null,domainId,maxResult,offset);
		
		Connection c;
		c = getConnection();
		if(c == null)
			return rsList;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = c.prepareStatement(sqlQuery);
			addQueryParameters(stat,filterParams);
			rs = stat.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				AhClientSession bo = new AhClientSession();
				for(int i = 0; i < columnCount; i++) {
					String columnName = rs.getMetaData().getColumnName(i+1).toLowerCase();
					MethodInfo m = m_map_ActiveClientColumn.get(columnName);
					if (m != null) {
						if(m.getParamClass() == byte.class) {
							m.getMethod().invoke(bo, Byte.parseByte(rs.getObject(i+1).toString()));
						} else if(m.getParamClass() == short.class) {
							m.getMethod().invoke(bo, Short.parseShort(rs.getObject(i+1).toString()));
						}
						else {
							m.getMethod().invoke(bo, rs.getObject(i+1));
						}
					} else if(columnName.equalsIgnoreCase("owner")){
						Long owner = rs.getLong(i+1);
						bo.setOwner(CacheMgmt.getInstance().getCacheDomainById(owner));
					} else {
						DebugUtil.commonDebugError("Column name " + columnName + " has no set method in AhClientSession");
					}
				}
				rsList.add(bo);
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to execute query", e);
		} finally {
			try {
				if(null != rs)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return rsList;
	}
	
	public static  List<?>	executeQueryBos(String sqlQuery) throws Exception {
		List<HmBo> rsList = new ArrayList<HmBo>();
		Connection c;
		c = getConnection();
		if(c == null)
			return rsList;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = c.prepareStatement(sqlQuery);
			rs = stat.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				AhClientSession bo = new AhClientSession();
				for(int i = 0; i < columnCount; i++) {
					String columnName = rs.getMetaData().getColumnName(i+1).toLowerCase();
					MethodInfo m = m_map_ActiveClientColumn.get(columnName);
					if (m != null) {
						if(m.getParamClass() == byte.class) {
							m.getMethod().invoke(bo, Byte.parseByte(rs.getObject(i+1).toString()));
						} else if(m.getParamClass() == short.class) {
							m.getMethod().invoke(bo, Short.parseShort(rs.getObject(i+1).toString()));
						}
						else {
							m.getMethod().invoke(bo, rs.getObject(i+1));
						}
					} else if(columnName.equalsIgnoreCase("owner")){
						Long owner = rs.getLong(i+1);
						bo.setOwner(CacheMgmt.getInstance().getCacheDomainById(owner));
					} else {
						DebugUtil.commonDebugError("Column name " + columnName + " has no set method in AhClientSession");
					}
				}
				rsList.add(bo);
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("Fail to execute query", e);
			throw e;
		} finally {
			try {
				if(null != rs)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if(null != stat)
					stat.close();
			} catch (SQLException e) {
			}
			try {
				if(null != c)
					c.close();
			} catch (SQLException e) {
			}
		}
		return rsList;
	}
	
	public static <T extends HmBo> int	updateBO(T bo) {
		if(!(bo instanceof AhClientSession))
			return 0;
		StringBuffer sqlBuffer = new StringBuffer();
		AhClientSession client = (AhClientSession)bo;
		sqlBuffer.append("update ah_clientsession set ");
		
		boolean bFirst = true;
		for(int i = 1; i < AhConvertBOToSQL.CLIENTSESSION_FIELDS_ARRAY.length; i ++) {
			String columnName = AhConvertBOToSQL.CLIENTSESSION_FIELDS_ARRAY[i].toLowerCase();
			MethodInfo m = m_map_ActiveClientColumn.get(columnName);
			if (m != null) {
				Object value = null;
				boolean bUpdate = true;
				try {
					if(m.getGetMethod() != null) {
						value = m.getGetMethod().invoke(client, (Object[])null);
					}
					else if(columnName.equalsIgnoreCase("simulated"))
						value = client.isSimulated();
					else if(columnName.equalsIgnoreCase("wirelessclient"))
						value = client.isWirelessClient();
					else {
						bUpdate = false;
						DebugUtil.commonDebugError("Column name " + columnName + " has no get method in AhClientSession");
					}
					
					if(bUpdate) {
						if(!bFirst)
							sqlBuffer.append(",");
						
						sqlBuffer.append(columnName).append("=");
						if(null != value)
							sqlBuffer.append("'").append(value.toString()).append("'");
						else
							sqlBuffer.append("null");
						bFirst = false;
					}
				} catch (Exception e) {
					value = null;
				}
			}
		}
		sqlBuffer.append(" where id = ").append(bo.getId());
		return executeUpdate(sqlBuffer.toString());
	}
	/**
	 * find bo by id
	 * @param <T>
	 * @param boClass
	 * @param id
	 * @return
	 */
	public static <T extends HmBo> T findBoById(Class<T> boClass, Long id) {
		T bo = null;
		if(boClass != AhClientSession.class)
			return bo;
		
		List<T> rsList = executeQuery(boClass,null,new FilterParams("id",id));
		if(null == rsList || rsList.size() != 1)
			return bo;
		bo = rsList.get(0);
		return bo;
	}
	/**
	 * find row count
	 * @param boClass
	 * @param filterParams
	 * @return
	 */
	public static long findRowCount(Class<? extends HmBo> boClass,
			FilterParams filterParams) {
		long count = 0;
		if(boClass != AhClientSession.class)
			return count;
		List<?> rsList = executeQuery("select count(*) from ah_clientsession",null,filterParams);
		if(rsList == null || rsList.size() != 1)
			return count;
		try {
			count = Long.parseLong(rsList.get(0).toString());
		} catch (NumberFormatException e) {
		}
		return count;
	}
	static public void main(String[] args) {
		try {
			List<Long> values = new ArrayList<Long>();
			values.add(1L);
			values.add(2L);
//			QueryUtil.executeQuery(HmDomain.class, null, new FilterParams("id",values),0);
//			QueryUtil.executeQuery(HmDomain.class, null, new FilterParams("id", 2L));
//			QueryUtil.findBoById(MapContainerNode.class, 3L);
//			QueryUtil.findBoById(MapContainerNode.class, 3L);
//			QueryUtil.findBoById(MapLeafNode.class, 434196L);
//			QueryUtil.findBoById(MapLeafNode.class, 434196L);
//			DBOperationUtil.executeQuery("delete from ah_clientsession");
//			NameValuePair nv0 = new NameValuePair("FileType", String.valueOf(1));
//			NameValuePair nv1 = new NameValuePair("Filename", "dfssaaaaadafsd");
			
//			HttpFileTransferUtil.uploadDataCollectionFileToLS("d:/envi.txt", "001977000011");
//			AhClientSession client = new AhClientSession();
//			client = QueryUtil.findBoById(AhClientSession.class, 3L);
//			client.setSimulated(false);
//			DBOperationUtil.updateBO(client);

			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void testUpdate() {
//		try {
//			int nCount = 10;
//			int number = 1000;
//			long begin = 0;
//			for(int i = 0;  i < nCount; i++) {
//				begin = System.currentTimeMillis();
//				
//				byte newState = 1;
//				for(int j = 0; j < number; j++)  {
////				QueryUtil.updateBo(AhClientSession.class,
////						"clientosinfo = :s1",
////						new FilterParams("clientMac = :s2 and connectstate = :s3", new Object[] {
////								"tetsOd","0022FE000100",
////								AhClientSession.CONNECT_STATE_UP }));
//					DBOperationUtil.executeUpdate("update ah_clientsession set clientosinfo = ? where clientMac = ? and connectstate = ?",
//							new Object[] {
//							"tetsOd","0022FE000100",
//							AhClientSession.CONNECT_STATE_UP });
//				}
//				
//				System.out.println("eclipse time:"+(System.currentTimeMillis()-begin)+"ms");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}

class MethodInfo {
	Method method;
	
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

	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Class<?> getParamClass() {
		return paramClass;
	}
	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}
}