package com.ah.bo.mgmt;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jdbc.Work;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.app.DebugUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.UserProfile;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.HibernateUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public final class QueryUtil {

	private static final Tracer log = new Tracer(QueryUtil.class
			.getSimpleName());

	public static String getQuery(String query, SortParams sortParams,
			FilterParams filterParams, GroupByParams groupByParams,
			Long domainId, Collection<Long> defaultIds) {
		StringBuilder queryBuffer = new StringBuilder(query);
		boolean hasWhere = false;

		if (filterParams != null) {
			hasWhere = true;
			queryBuffer.append(" where ");
			if (filterParams.getName() != null
					&& null == filterParams.getValue()
					&& null == filterParams.getValues()) {
				queryBuffer.append(filterParams.getName()).append(" is null");
			} else if (filterParams.getValue() != null) {
				queryBuffer.append(filterParams.getName()).append(" = :s1");
			} else if (filterParams.getWhere() != null) {
				queryBuffer.append(filterParams.getWhere());
			} else {
				queryBuffer.append(filterParams.getName()).append(" in (:s1)");
			}
		}

		if (domainId != null) {
			// Add domain filter
			if (hasWhere) {
				queryBuffer.append(" and (");
			} else {
				hasWhere = true;
				queryBuffer.append(" where (");
			}
			String gdc = BoMgmt.getDomainMgmt().getGlobalDomainCondition();
			if (gdc == null) {
				queryBuffer.append(" owner.id = :o)");
			} else {
				queryBuffer.append(gdc);
				queryBuffer.append(" OR owner.id = :o)");
			}
		}

		if (defaultIds != null && defaultIds.size() > 0) {
			// Add default ids filter
			if (hasWhere) {
				queryBuffer.append(" and ");
			} else {
				hasWhere = true;
				queryBuffer.append(" where ");
			}
			queryBuffer.append("id not in (:d)");
		}

		if (groupByParams != null) {
			queryBuffer.append(groupByParams.getQuery());
		}

		if (sortParams != null) {
			queryBuffer.append(sortParams.getQuery());
		}

		return queryBuffer.toString();
	}

	public static Query createQuery(EntityManager em, String query,
			SortParams sortParams, FilterParams filterParams, Long domainId,
			Collection<Long> defaultIds) {
		Query jpQuery = em.createQuery(getQuery(query, sortParams,
				filterParams, null, domainId, defaultIds));
		addQueryParameters(jpQuery, filterParams, domainId, defaultIds);
		return jpQuery;
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			Collection<Long> defaultIds) {
		return createQuery(em, query, sortParams, filterParams,
				getDomainFilter(user), defaultIds);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			SortParams sortParams, FilterParams filterParams,
			GroupByParams groupByParams, Long domainId) {
		Query jpQuery = em.createQuery(getQuery(query, sortParams,
				filterParams, groupByParams, domainId, null));
		addQueryParameters(jpQuery, filterParams, domainId, null);
		return jpQuery;
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			FilterParams filterParams, Long domainId,
			Collection<Long> defaultIds) {
		return createQuery(em, query, null, filterParams, domainId, defaultIds);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			SortParams sortParams, FilterParams filterParams, Long domainId) {
		return createQuery(em, query, sortParams, filterParams, domainId, null);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			FilterParams filterParams, Long domainId) {
		return createQuery(em, query, null, filterParams, domainId, null);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			SortParams sortParams, FilterParams filterParams) {
		return createQuery(em, query, sortParams, filterParams, (Long) null,
				null);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static Query createQuery(EntityManager em, String query,
			FilterParams filterParams) {
		return createQuery(em, query, null, filterParams, (Long) null, null);
	}

	public static void addQueryParameters(Query jpQuery,
			FilterParams filterParams, Long domainId,
			Collection<Long> defaultIds) {
		if (domainId != null) {
			jpQuery.setParameter("o", domainId);
		}
		if (defaultIds != null && defaultIds.size() > 0) {
			// Add default values filter
			jpQuery.setParameter("d", defaultIds);
		}
		if (filterParams == null) {
			return;
		}
		if (filterParams.getValue() != null) {
			jpQuery.setParameter("s1", filterParams.getValue());
		} else if (filterParams.getWhere() != null) {
			for (int i = 1; i < filterParams.getBindings().length + 1; i++) {
				log.debug("addQueryParameters", "binding: " + "s" + i);
				jpQuery
						.setParameter("s" + i,
								filterParams.getBindings()[i - 1]);
			}
		} else if (filterParams.getValues() != null) {
			jpQuery.setParameter("s1", filterParams.getValues());
		}
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em,
			Class<? extends HmBo> boClass, SortParams sortParams,
			FilterParams filterParams, Long domainId) {
		return createQuery(em, "from " + boClass.getSimpleName() + " bo",
				sortParams, filterParams, domainId, null);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static Query createQuery(EntityManager em,
			Class<? extends HmBo> boClass, SortParams sortParams,
			FilterParams filterParams, HmUser user) {
		return createQuery(em, boClass, sortParams, filterParams,
				getDomainFilter(user));
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static Query createQuery(EntityManager em,
			Class<? extends HmBo> boClass, SortParams sortParams,
			FilterParams filterParams) {
		return createQuery(em, boClass, sortParams, filterParams, (Long) null);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, HmUser user, int maxResults) {
		return executeQuery(query, sortParams, filterParams,
				getDomainFilter(user), maxResults);
	}

	/*
	 * Derive filter domain from user context
	 */
	public static Long getDomainFilter(HmUser user) {
		if (user == null) {
			return null;
		}

		if (user.isRedirectUser()) {
			return user.getSwitchDomain().getId();
		}

		if (!user.isSuperUser()) {
			return user.getDomain().getId();
		} else if (user.getSwitchDomain() != null) {
			return user.getSwitchDomain().getId();
		}

		return null;
	}

	/*
	 * Derive filter domain from user context, for dependent objects. If no
	 * switch domain, use user's domain (same as user group domain), even for
	 * super user.
	 */
	public static Long getDependentDomainFilter(HmUser user) {
		if (user == null) {
			return null;
		}
		if (user.getSwitchDomain() != null) {
			return user.getSwitchDomain().getId();
		} else {
			return user.getDomain().getId();
		}
	}

	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, GroupByParams groupByParams,
			Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query jpQuery = createQuery(em, query, sortParams, filterParams,
					groupByParams, domainId);
			List<?> bos = jpQuery.setMaxResults(PagingImpl.MAX_RESULTS)
					.getResultList();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, Long domainId, int maxResults,
			QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query jpQuery = createQuery(em, query, sortParams, filterParams,
					domainId, null);
			List<?> bos = jpQuery.setMaxResults(maxResults).getResultList();
			if (null != queryBo) {
				for (Object bo : bos) {
					if (bo instanceof HmBo) {
						queryBo.load((HmBo) bo);
					} else if (bo instanceof Object[]){
						// load lazy object when query like "select bo.select bo.configTemplate,bo.mapContainer from HiveAp"
						Object[] obs = (Object[]) bo;
						for(Object o : obs){
							if (o instanceof HmBo) {
								queryBo.load((HmBo) o);
							}
						}
					}
				}
			}
			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, Long domainId, int maxResults) {
		return executeQuery(query, sortParams, filterParams, domainId,
				maxResults, null);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, Long domainId, QueryBo queryBo) {
		return executeQuery(query, sortParams, filterParams, domainId,
				Paging.MAX_RESULTS, queryBo);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, int maxResults) {
		return executeQuery(query, sortParams, filterParams,
				(Long) null, maxResults);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams, Long domainId) {
		return executeQuery(query, sortParams, filterParams, domainId,
				Paging.MAX_RESULTS);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static List<?> executeQuery(String query, SortParams sortParams,
			FilterParams filterParams) {
		return executeQuery(query, sortParams, filterParams, null);
	}

	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, Long domainId,
			QueryBo queryBo) {
		return (List<T>) executeQuery(
				"from " + boClass.getSimpleName() + " bo", sortParams,
				filterParams, domainId, PagingImpl.MAX_RESULTS, queryBo);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			int maxResults) {
		return (List<T>) executeQuery(
				"from " + boClass.getSimpleName() + " bo", sortParams,
				filterParams, user, maxResults);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, Long domainId,
			int maxResults) {
		return (List<T>) executeQuery(
				"from " + boClass.getSimpleName() + " bo", sortParams,
				filterParams, domainId, maxResults);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, HmUser user) {
		return executeQuery(boClass, sortParams, filterParams, user,
				Paging.MAX_RESULTS);
	}

	/*
	 * This function should be used if results need to be filtered by object
	 * owner, which is true for all persistent objects, except events and
	 * alarms, and possibly audit log entries and statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, Long domainId) {
		return executeQuery(boClass, sortParams, filterParams, domainId,
				Paging.MAX_RESULTS);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, int maxResults) {
		return executeQuery(boClass, sortParams, filterParams, (Long) null,
				maxResults);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static <T extends HmBo> List<T> executeQuery(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams) {
		return executeQuery(boClass, sortParams, filterParams,
				Paging.MAX_RESULTS);
	}

	/*
	 * This function should be avoided, except for persistent objects which have
	 * no ownership like events and alarms, and possibly audit log entries and
	 * statistics data.
	 */
	public static List<?> executeQuery(String query, int maxResults) {
		return executeQuery(query, maxResults, 0);
	}

	public static List<?> executeQuery(String query, int limit, int offset) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			List<?> bos = em.createQuery(query).setMaxResults(limit)
					.setFirstResult(offset).getResultList();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeQuery", "Execute query failed: limit = " + limit
					+ ", offset = " + offset, e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static List<?> executeNativeQuery(String sql) {
		return executeNativeQuery(sql, Paging.MAX_RESULTS);
	}

	public static List<?> executeNativeQuery(String sql, int limit) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			List<?> bos = em.createNativeQuery(sql).setMaxResults(limit)
					.getResultList();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeNativeQuery", "Execute native query failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static List<?> executeNativeQuery(String sql, int limit, int offset) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			List<?> bos = em.createNativeQuery(sql).setMaxResults(limit)
					.setFirstResult(offset).getResultList();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeNativeQuery", "Execute native query failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}
	
	public static List<?> executeNativeQuery(String sql, SortParams sortParams, FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			StringBuilder sb = new StringBuilder(QueryUtil.getQuery(sql, sortParams, filterParams, null, null, null));
			Query query = em.createNativeQuery(sb.toString());
			QueryUtil.addQueryParameters(query, filterParams, null, null);
			
			List<?> bos = query.setMaxResults(Paging.MAX_RESULTS).getResultList();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeNativeQuery", "Execute native query failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static boolean executeNativeStore(String sql) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		boolean defaultAutoCommit = true;
		try {
			conn = getConnection();
			defaultAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			boolean result = stmt.execute(sql);
			conn.commit();
			return result;
		} catch (Exception e) {
			log.error("executeNativeStore", "Execute native store failed.", e);
			try {
				if (conn != null && !conn.isClosed()) {
					conn.rollback();
				}
			} catch (SQLException sqle) {
				log.error("executeNativeStore", "Transaction rollback error.", sqle);
			}
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.error("executeNativeStore", "Close statement error.", e);
			}
			try {
				if (conn != null && !conn.isClosed()) {
					conn.setAutoCommit(defaultAutoCommit);
				}
			} catch (SQLException e) {
				log.error("executeNativeStore", "Set connection's auto-commit mode error.", e);
			}
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error("executeNativeStore", "Close connection error.", e);
			}
		}
	}

	public static int executeNativeUpdate(String sql) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		boolean defaultAutoCommit = true;
		try {
			conn = getConnection();
			defaultAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			int updates = stmt.executeUpdate(sql);
			conn.commit();
			return updates;
		} catch (Exception e) {
			log.error("executeNativeUpdate", "Execute native update failed.", e);
			try {
				if (conn != null && !conn.isClosed()) {
					conn.rollback();
				}
			} catch (SQLException sqle) {
				log.error("executeNativeUpdate", "Transaction rollback error.", sqle);
			}
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.error("executeNativeUpdate", "Close statement error.", e);
			}
			try {
				if (conn != null && !conn.isClosed()) {
					conn.setAutoCommit(defaultAutoCommit);
				}
			} catch (SQLException e) {
				log.error("executeNativeUpdate", "Set connection's auto-commit mode error.", e);
			}
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error("executeNativeUpdate", "Close connection error.", e);
			}
		}
	}

	public static int executeNativeUpdate(String updateSql, Object[] params) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query query = em.createNativeQuery(updateSql);
			if (null != params) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					query.setParameter("s" + (i + 1), param);
				}
			}
			int updates = query.executeUpdate();

			tx.commit();
			return updates;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("executeNativeUpdate", "Execute native update failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
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
			DebugUtil.commonDebugError("Fail to execute batch update:"+sql, e);
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
	 * Execute copy command of PostgreSQL
	 *
	 * @param sql -
	 * @param fileName -
	 */
	public static void executeCopy(String sql, String fileName) {
		Connection conn = null;
		FileReader reader = null;

		try {
			reader = new FileReader(fileName);
			conn = getConnection();
			C3P0ProxyConnection con = (C3P0ProxyConnection) conn;
			Method m = BaseConnection.class.getMethod("getCopyAPI", new Class[]{});
		    Object[] arg = new Object[] {};
		    CopyManager copyManager = (CopyManager) con.rawConnectionOperation(m, C3P0ProxyConnection.RAW_CONNECTION, arg);
			copyManager.copyIn(sql, reader);
		} catch (Exception e) {
			log.error("executeCopy", "Execute copy failed.", e);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error("executeCopy", "Connection close error.", e);
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe) {
				log.error("executeCopy", "I/O close error.", ioe);
			}
		}
	}

	/*
	 * Not used for now, in case JPA Query interface is not sufficient
	 */
	public static List<?> executeHibernateQuery(Class<? extends HmBo> boClass,
			FilterParams filterParams) {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			// session.setCacheMode(CacheMode.IGNORE);

			Criteria criteria = session.createCriteria(boClass);
			criteria.add(Restrictions.eq(filterParams.getName(), filterParams
					.getValue()));
			// criteria.setCacheMode(CacheMode.IGNORE);
			// criteria.uniqueResult();
			List<?> bos = criteria.list();
			// Query hqlQuery = session.createQuery(query);
			// hqlQuery.setCacheable(false);
			// hqlQuery.setMaxResults(Paging.MAX_RESULTS);
			// List bos = hqlQuery.list();
			// session.clear();

			tx.commit();
			return bos;
		} catch (RuntimeException e) {
			log.error("executeHibernateQuery",
					"Execute hibernate query failed.", e);
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (RuntimeException rbe) {
					log.error("executeHibernateQuery",
							"Transaction rollback error.", rbe);
				}
			}
			throw e;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public static long findRowCount(Class<? extends HmBo> boClass,
			FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query jpQuery = createQuery(em, "select count(bo) from "
					+ boClass.getSimpleName() + " bo", null, filterParams);
			Long rowCount = (Long) jpQuery.getSingleResult();
			log.debug("findRowCount", "Row count for "
					+ boClass.getSimpleName() + " is: " + rowCount);

			tx.commit();
			return rowCount;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findRowCount", "Find row count failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> Long findBoId(Class<T> boClass, SortParams sortParams,
			FilterParams filterParams, Long domainId) {
		List<?> ids = executeQuery("select id from " + boClass.getSimpleName(), sortParams, filterParams, domainId,
				1);
		if (ids != null
				&& ids.size() > 0
				&& ids.get(0) != null) {
			return Long.valueOf(ids.get(0).toString());
		}

		return null;
	}

	public static <T extends HmBo> List<Long> findBoIds(Class<T> boClass, SortParams sortParams,
			FilterParams filterParams, Long domainId) {
		List<?> ids = executeQuery("select id from " + boClass.getSimpleName(), sortParams, filterParams, domainId,
				Paging.MAX_RESULTS);
		if (ids != null
				&& !ids.isEmpty()) {
			List<Long> result = new ArrayList<>(ids.size());
			for (Object id : ids) {
				if (id != null) {
					result.add(Long.valueOf(id.toString()));
				}
			}
			return result;
		}

		return null;
	}

	public static <T extends HmBo> T findBoById(Class<T> boClass, Long id) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			T bo = em.find(boClass, id);
			if (null != bo && null != bo.getOwner() ) {
				// Just to trigger a load
				bo.getOwner().getId();
			}
			tx.commit();
			return bo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoById", "Find BO by id failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T findBoById(Class<T> boClass, Long id,
			QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			T bo = em.find(boClass, id);
			if (null != bo && null != bo.getOwner() ) {
				// Just to trigger a load
				bo.getOwner().getId();
			}
			queryBo.load(bo);
			tx.commit();
			return bo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoById", "Find BO by id failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static void createBoBase(HmBoBase hmBo) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			em.persist(hmBo);

			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("createBoBase", "Create BO failed.", e);
			if (e instanceof EntityExistsException
					|| (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException("Create object '" + hmBo.getLabel()
						+ "' failed.", e, HmMessageCodes.OBJECT_EXISTS,
						new String[] { hmBo.getLabel() });
			} else {
				log.error("createBoBase", "Create "
						+ hmBo.getClass().getSimpleName()
						+ " failed with unknown exception.");
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static Long createBo(HmBo hmBo) throws Exception {
		createBoBase(hmBo);
		return hmBo.getId();
	}

	public static void bulkCreateBos(Collection<? extends HmBo> hmBos)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		HmBo hmBo = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			for (HmBo bo : hmBos) {
				hmBo = bo;
				em.persist(hmBo);
			}

			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("bulkCreateBos", "Create BO(s) failed.", e);
			if (e instanceof EntityExistsException
					|| (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException("Create object '" + hmBo.getLabel()
						+ "' failed.", e, HmMessageCodes.OBJECT_EXISTS,
						new String[] { hmBo.getLabel() });
			} else {
				log.error("bulkCreateBos", "Create "
						+ hmBo.getClass().getSimpleName()
						+ " failed with unknown exception.");
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static void restoreBulkCreateBos(Collection<? extends HmBo> hmBos)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		HmBo hmBo = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			for (HmBo bo : hmBos) {
				hmBo = bo;
				em.persist(hmBo);
			}

			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("restoreBulkCreateBos", "Create BO(s) failed.", e);
			closeEntityManager(em);

			for (HmBo bo : hmBos) {
				try {
					bo.setId(null);
					createBo(bo);
				} catch (Exception ex) {
					//write log to restorelog.
					/*Tracer logs = new Tracer(QueryUtil.class,HmLogConst.M_RESTORE);
					logs.error("insert failed", createLogString(bo));*/
					try{
						AhRestoreDBTools.logRestoreErrorMsg("insert failed: " + createLogString(bo));
					} catch (Exception en) {
					}
				}
			}

			if (e instanceof EntityExistsException
					|| (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				log.error("restoreBulkCreateBos", "Create object '" + hmBo.getLabel()+ "' failed.");
//				throw new HmException("Create object '" + hmBo.getLabel()
//						+ "' failed.", e, HmMessageCodes.OBJECT_EXISTS,
//						new String[] { hmBo.getLabel() });
			} else {
				log.error("restoreBulkCreateBos", "Create "
						+ hmBo.getClass().getSimpleName()
						+ " failed with unknown exception.");
//				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	/**
	 * use java reflect to get fields and values.
	 * @param hb -
	 * @return String
	 * @author root
	 * @throws SecurityException -
	 * @throws InvocationTargetException -
	 * @throws IllegalArgumentException -
	 * @throws IllegalAccessException -
	 * @date Jul 31, 2012
	 */
	public static String createLogString(HmBo hb) throws SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String logStr = "";
		Method[] methods = hb.getClass().getMethods();
		for (Method m : methods) {
			String methodName = m.getName();
			if (methodName.indexOf("get") == 0) {
				logStr += methodName.replaceFirst("get", "") + "=(";
				Object value = m.invoke(hb);
				if (null != value)
					logStr += value.toString() + "); ";
				else
					logStr += "this value is null); ";
			}
		}
		return logStr;
	}

	public static int updateBo(Class<? extends HmBo> boClass, String setClause,
			FilterParams filterParams) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			StringBuilder queryBuffer = new StringBuilder("update "
					+ boClass.getSimpleName() + " set " + setClause);
			if (filterParams != null && filterParams.getWhere() != null) {
				queryBuffer.append(" where ").append(filterParams.getWhere());
			}
			Query query = em.createQuery(queryBuffer.toString());
			addQueryParameters(query, filterParams, null, null);
			int updates = query.executeUpdate();
			tx.commit();
			return updates;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("updateBo", "Update BO failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T updateBo(T hmBo) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			T mergedHmBo = em.merge(hmBo);

			tx.commit();
			return mergedHmBo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("updateBo", "Update BO failed.", e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hmBo.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
					.getCause().getCause() instanceof ConstraintViolationException)) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.error("updateBo", "Constraint: " + cve.getConstraintName());
				throw new HmException(
						"Update object '" + hmBo.getLabel() + "' failed.",
						e,
						HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hmBo.getLabel(), cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static Collection<HmBo> updateBo(HmBo hmBo, QueryBo queryBo)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// reattach hmBo
			HmBo mergedHmBo = em.merge(hmBo);
			Collection<HmBo> mergedHmBos = new Vector<>();
			mergedHmBos.add(mergedHmBo);
			Collection<HmBo> updatedHmBos = queryBo.load(mergedHmBo);
			if (updatedHmBos != null) {
				for (HmBo updatedHmBo : updatedHmBos) {
					mergedHmBos.add(em.merge(updatedHmBo));
				}
			}

			tx.commit();
			return mergedHmBos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("updateBo", "Update BO failed.", e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hmBo.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
					.getCause().getCause() instanceof ConstraintViolationException)) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.error("updateBo", "Constraint: " + cve.getConstraintName());
				throw new HmException(
						"Update object '" + hmBo.getLabel() + "' failed.",
						e,
						HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hmBo.getLabel(), cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static Collection<HmBo> bulkUpdateBos(
			Collection<? extends HmBo> hmBos) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		HmBo hmBo = null;
		Collection<HmBo> list = new ArrayList<>(hmBos.size());
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			for (HmBo bo : hmBos) {
				hmBo = bo;
				HmBo hb = em.merge(hmBo);
				list.add(hb);
			}
			tx.commit();
			return list;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("bulkUpdateBos", "Update BO(s) failed.", e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hmBo.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
					.getCause().getCause() instanceof ConstraintViolationException)) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.error("bulkUpdateBos", "Constraint: "
						+ cve.getConstraintName());
				throw new HmException(
						"Update object '" + hmBo.getLabel() + "' failed.",
						e,
						HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hmBo.getLabel(), cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static String getUpdateClause(Class<? extends HmBo> boClass,
			String setClause, String whereClause, Long domainId) {
		boolean hasWhere = false;
		StringBuilder updateBuffer = new StringBuilder();
		updateBuffer.append("update ").append(boClass.getSimpleName());
		updateBuffer.append(" set ").append(setClause);

		if (whereClause != null) {
			hasWhere = true;
			updateBuffer.append(" where (").append(whereClause).append(")");
		}

		if (domainId != null) {
			// Add domain filter
			if (hasWhere) {
				updateBuffer.append(" and ");
			} else {
				hasWhere = true;
				updateBuffer.append(" where ");
			}

			updateBuffer.append("owner.id = :o");
		}

		return updateBuffer.toString();
	}

	public static void addUpdateParameters(Query jpQuery, Object[] bindings,
			Long domainId) {
		for (int i = 1; i < bindings.length + 1; i++) {
			log.debug("addUpdateParameters", "binding: s" + i);
			jpQuery.setParameter("s" + i, bindings[i - 1]);
		}

		if (domainId != null) {
			jpQuery.setParameter("o", domainId);
		}
	}

	public static int updateBos(Class<? extends HmBo> boClass,
			String setClause, String whereClause, Object[] bindings,
			Long domainId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			String updateClause = getUpdateClause(boClass, setClause,
					whereClause, domainId);
			Query query = em.createQuery(updateClause);
			addUpdateParameters(query, bindings, domainId);
			int updates = query.executeUpdate();

			tx.commit();

			return updates;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("updateBos", "Update BO(s) failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static int updateBos(Class<? extends HmBo> boClass,
			String setClause, String whereClause, Object[] bindings)
			throws Exception {
		return updateBos(boClass, setClause, whereClause, bindings, null);
	}

	public static boolean removeBo(Class<? extends HmBo> boClass, long boId)
			throws Exception {
		boolean succeeded = false;
		EntityManager em = null;
		EntityTransaction tx = null;
		HmBo hmBo = null;
		Map<Long, String> removeIds = new HashMap<>();
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

		//	try {
				hmBo = em.getReference(boClass, boId);
				removeIds.put(hmBo.getId(), hmBo.getLabel());
				em.remove(hmBo);
		//	} catch (EntityNotFoundException e) {
		//		log.error("removeBo", "HmBo with id: " + boId
		//				+ " not found, must have been removed earlier.");
		//	}

			tx.commit();
			succeeded = true;
		} catch (RuntimeException e) {
			rollback(tx);
			if (e instanceof EntityNotFoundException) {
				log.error("removeBo", "HmBo with id: " + boId
						+ " not found, must have been removed earlier.");
				succeeded = true;
			} else {
				log.error("removeBo", "Remove BO failed.", e);
				if (e instanceof PersistenceException
						&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
								.getCause().getCause() instanceof ConstraintViolationException))
						&& hmBo != null) {
					String errLabel;
					try {
						String errMsg = e.getCause().getMessage();
						Long id = parseExceptionId(errMsg);
						errLabel = removeIds.get(id);
						if (errLabel == null) {
							errLabel = hmBo.getLabel();
						}
					} catch (Exception exc) {
						errLabel = hmBo.getLabel();
					}
					throw new HmException("Remove object " + hmBo.getId()
							+ " failed, stale object state.", e,
							HmMessageCodes.OBJECT_IN_USE, new String[] { errLabel });
				} else {
					throw e;
				}
			}
		} finally {
			closeEntityManager(em);
		}
		return succeeded;
	}

	public static int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		if (ids == null) {
			return 0;
		}
		int count = 0;
		AhEvent removedEvent = null;
		AhAlarm removedAlarm = null;
		EntityManager em = null;
		EntityTransaction tx = null;
		HmBo hmBo = null;
		Map<Long, String> removeIds = new HashMap<>();
		Long notExistObjectId = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			for (Long id : ids) {
			//	try {
					hmBo = em.getReference(boClass, id);
					notExistObjectId = id;
					removeIds.put(hmBo.getId(), hmBo.getLabel());

					// Relationship removal should be prior to user profile itself.
					if (hmBo instanceof UserProfile) {
						// device policy rule
					//	QueryUtil.executeNativeUpdate("DELETE FROM device_policy_rule WHERE userprofileid = " + id);
					//	Query query = em.createNativeQuery("DELETE FROM device_policy_rule WHERE userprofileid = " + id);
					//	query.executeUpdate();

						Query query = QueryUtil.createQuery(em, "select distinct bo from " + UserProfile.class.getSimpleName() + " as bo join bo.assignRules as joined",
								null, new FilterParams("joined.userProfileId", id));
						List<?> userProfiles = query.getResultList();

						if (userProfiles != null) {
							for (Object obj : userProfiles) {
								if (obj == null) {
									continue;
								}
								UserProfile userProfile = (UserProfile) obj;
								List<DevicePolicyRule> assignedRules = userProfile.getAssignRules();
	
								if (assignedRules != null
										&& !assignedRules.isEmpty()) {
									for (Iterator<DevicePolicyRule> devicePolicyRuleIter = assignedRules.iterator(); devicePolicyRuleIter.hasNext();) {
										DevicePolicyRule devicePolicyRule = devicePolicyRuleIter.next();
										Long userProfileId = devicePolicyRule.getUserProfileId();
		
										if (id.equals(userProfileId)) {
											devicePolicyRuleIter.remove();
										}
									}
		
									em.merge(userProfile);
								}
							}
						}

						// ad or ldap user group mapping
					//	QueryUtil.executeNativeUpdate("DELETE FROM radius_hiveap_ldap_user_profile WHERE userprofileid = " + hmBo.getId());
					//	query = em.createNativeQuery("DELETE FROM radius_hiveap_ldap_user_profile WHERE userprofileid = " + id);
					//	query.executeUpdate();

						query = QueryUtil.createQuery(em, "select distinct bo from " + RadiusOnHiveap.class.getSimpleName() + " as bo join bo.ldapOuUserProfiles as joined",
								null, new FilterParams("joined.userProfileId", id));
						List<?> radiusServices = query.getResultList();

						if (radiusServices != null) {
							for (Object obj : radiusServices) {
								if (obj == null) {
									continue;
								}
								RadiusOnHiveap radiusService = (RadiusOnHiveap) obj;
								List<LdapServerOuUserProfile> ldapOuUserProfiles = radiusService.getLdapOuUserProfiles();
	
								if (ldapOuUserProfiles != null
										&& !ldapOuUserProfiles.isEmpty()) {
									for (Iterator<LdapServerOuUserProfile> ldapOuUserProfileIter = ldapOuUserProfiles.iterator(); ldapOuUserProfileIter.hasNext();) {
										LdapServerOuUserProfile ldapServerOuUserProfile = ldapOuUserProfileIter.next();
										Long userProfileId = ldapServerOuUserProfile.getUserProfileId();
		
										if (id.equals(userProfileId)) {
											ldapOuUserProfileIter.remove();
										}
									}
		
									em.merge(radiusService);
								}
							}
						}
					}

					em.remove(hmBo);

					if (hmBo instanceof AhAlarm) {
						SystemStatusCache.getInstance().decrementAlarmCount(
								((AhAlarm) hmBo).getSeverity(),
								hmBo.getOwner().getId());
						if (removedAlarm == null) {
							removedAlarm = new AhAlarm();
						}
						// Don't use hmBo here because the class is enhanced
						removedAlarm.setId(hmBo.getId());
						// For AlarmPagingCache
						BoObserver.notifyListeners(new BoEvent<>(removedAlarm,
								BoEventType.REMOVED));
					} else if (hmBo instanceof AhEvent) {
						if (removedEvent == null) {
							removedEvent = new AhEvent();
						}
						// Don't use hmBo here because the class is enhanced
						removedEvent.setId(hmBo.getId());
						// For EventPagingCache
						BoObserver.notifyListeners(new BoEvent<>(removedEvent,
								BoEventType.REMOVED));
					}

					count++;
			//	} catch (EntityNotFoundException e) {
			//		log.error("removeBos", "HmBo with id: " + id
			//				+ " not found, must have been removed earlier.");
			//	}
			}

			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("removeBos", "Remove BO(s) failed.", e);
			
			if(e instanceof EntityNotFoundException){
				if(notExistObjectId != null){
					log.error("removeBo", "HmBo with id  " + notExistObjectId
							+ "is not found, must have been removed earlier.");
					if(ids.size() == 1){
						throw new HmException("The selected object has previously been removed.",e,HmMessageCodes.OBJECT_REMOVENOTEXIST,null);
					}else{
						throw new HmException("The selected objects have previously been removed.",e,HmMessageCodes.OBJECTS_REMOVENOTEXIST,null);
					}
				}
			}else if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))
					&& hmBo != null) {
				String errLabel;
				try {
					String errMsg = e.getCause().getMessage();
					Long id = parseExceptionId(errMsg);
					errLabel = removeIds.get(id);
					if (errLabel == null) {
						errLabel = hmBo.getLabel();
					}
				} catch (Exception exc) {
					errLabel = hmBo.getLabel();
				}
				throw new HmException("Remove object " + hmBo.getId()
						+ " failed, stale object state.", e, HmMessageCodes.OBJECT_IN_USE,
						new String[] { errLabel });
			} else{
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
		return count;
	}

	/*
	 * Remove BOs one by one, matching filter params and user domain, and
	 * excluding default objects.
	 */
	public static int removeBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, HmUser user, Collection<Long> defaultIds)
			throws Exception {
		int count = 0;
		String query = "select id from " + boClass.getSimpleName() + " bo";
		List<Long> boIds = (List<Long>) executeQuery(query, null, filterParams, user, 1000);
		if (defaultIds != null) {
			boIds.removeAll(defaultIds);
		}
		while (!boIds.isEmpty()) {
			log.info("removeBos", "Removing: " + boIds.size() + " bos.");
			count += removeBos(boClass, boIds);
			boIds = (List<Long>) executeQuery(query, null, filterParams, user, 1000);
			if (defaultIds != null) {
				boIds.removeAll(defaultIds);
			}
		}
		return count;
	}

	/*
	 * remove BO one by one, pure removal, no extra BO checking
	 *
	 * @param boClass
	 *            -
	 * @param filterParams
	 *            -
	 * @throws Exception
	 *             -
	 */
	public static void removeBos(Class<? extends HmBo> boClass,
			FilterParams filterParams) throws Exception {
		String query = "select id from " + boClass.getSimpleName() + " bo";
		List<Long> boIds = (List<Long>) executeQuery(query, null, filterParams,
				(HmUser) null, 500000);
//		if (boIds.size() <= 1000) {
//			removeBos(boClass, boIds);
//			return;
//		}
//
//		while (true) {
//			int toIndex = boIds.size() >= 1000 ? 1000 : boIds.size();
//			List<Long> tmpList = boIds.subList(0, toIndex);
//			removeBos(boClass, tmpList);
//			if (tmpList.size() < 1000) {
//				break;
//			}
//			boIds = boIds.subList(toIndex, boIds.size());
//		}

		while (!boIds.isEmpty()) {
			int toIndex = boIds.size() >= 1000 ? 1000 : boIds.size();
			List<Long> tmpList = boIds.subList(0, toIndex);
			removeBos(boClass, tmpList);
			boIds = boIds.subList(toIndex, boIds.size());
		}
	}

	/*
	 * Remove BOs in bulk, matching filter params and user domain, and excluding
	 * default objects. No notification for AhEvent and AhAlarm objects.
	 */
	public static int bulkRemoveBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, HmUser user, Collection<Long> defaultIds)
			throws Exception {
		int removeCount = 0;
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			Query jpQuery = createQuery(em, "delete from "
					+ boClass.getSimpleName(), null, filterParams, user,
					defaultIds);
			removeCount = jpQuery.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("bulkRemoveBos", "Remove BO(s) failed.", e);
			if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException(
						"Remove objects failed, stale object state.", e,
						HmMessageCodes.OBJECTS_IN_USE);
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
		return removeCount;
	}

	/**-----This method is only used to remove reports tables---**/
	/*
	 * Remove BOs in bulk, matching filter params and user domain, and excluding
	 * default objects. No notification for AhEvent and AhAlarm objects.
	 */
	public static int removeReportsBos(Class<?> boClass,
			FilterParams filterParams, HmUser user, Collection<Long> defaultIds)
			throws Exception {
		int removeCount = 0;
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			Query jpQuery = createQuery(em, "delete from "
					+ boClass.getSimpleName(), null, filterParams, user,
					defaultIds);
			removeCount = jpQuery.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("removeBos", "Remove BO(s) failed.", e);
			if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException(
						"Remove objects failed, stale object state.", e,
						HmMessageCodes.OBJECTS_IN_USE);
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
		return removeCount;
	}

	/*
	 * Remove BOs in bulk, matching filter params and user domain.
	 */
	public static int bulkRemoveBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Long domainId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query jpQuery = createQuery(em, "delete from "
					+ boClass.getSimpleName(), filterParams, domainId);
			int removeCount = jpQuery.executeUpdate();

			tx.commit();

			return removeCount;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("bulkRemoveBos", "Remove BO(s) failed.", e);
			if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException(
						"Remove objects failed, stale object state.", e,
						HmMessageCodes.OBJECTS_IN_USE);
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
	}

	/*
	 * Remove BOs in bulk, matching filter params.
	 */
	public static int bulkRemoveBos(Class<? extends HmBo> boClass,
			FilterParams filterParams) throws Exception {
		return bulkRemoveBos(boClass, filterParams, null);
	}

	/*
	 * Remove BOs in bulk, matching filter params and domain id, and excluding
	 * default objects. No notification for AhEvent and AhAlarm objects.
	 */
	public static int bulkRemoveBosByDomain(Class<? extends HmBo> boClass,
			FilterParams filterParams, Long domainId,
			Collection<Long> defaultIds) throws Exception {
		int removeCount = 0;
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			Query jpQuery = createQuery(em, "delete from "
					+ boClass.getSimpleName(), null, filterParams, domainId,
					defaultIds);
			removeCount = jpQuery.executeUpdate();
			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("bulkRemoveBosByDomain", "Remove BO(s) failed.", e);
			if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))) {
				throw new HmException(
						"Remove objects failed, stale object state.", e,
						HmMessageCodes.OBJECTS_IN_USE);
			} else {
				throw e;
			}
		} finally {
			closeEntityManager(em);
		}
		if (boClass.equals(AhEvent.class)) {
			// For EventPagingCache
			BoObserver.notifyListeners(new BoEvent<>(new AhEvent(),
					BoEventType.REMOVED));
		}
		return removeCount;
	}

	public static void removeBoBase(HmBoBase hmBo) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

		//	try {
				HmBoBase mergedHmBo = em.merge(hmBo);
				em.remove(mergedHmBo);
		//	} catch (EntityNotFoundException e) {
		//		log.error("removeBoBase", "HmBo with name: " + hmBo.getLabel()
		//				+ " not found, must have been removed earlier.");
		//	}

			tx.commit();
		} catch (RuntimeException e) {
			rollback(tx);
			if (e instanceof EntityNotFoundException) {
				log.error("removeBoBase", "HmBo with name: " + hmBo.getLabel()
						+ " not found, must have been removed earlier.");
			} else {
				log.error("removeBoBase", "Remove BO failed.", e);
				if (e instanceof PersistenceException
						&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
								.getCause().getCause() instanceof ConstraintViolationException))
						&& hmBo != null) {
					throw new HmException("Remove object " + hmBo.getLabel()
							+ " failed, stale object state.", e,
							HmMessageCodes.OBJECT_IN_USE, new String[] { hmBo
									.getLabel() });
				} else {
					throw e;
				}
			}
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T findBoByAttribute(Class<T> boClass,
			String attrName, Object attrValue) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			List<T> boIds = createQuery(em, boClass, null,
					new FilterParams(attrName, attrValue)).setMaxResults(1)
					.getResultList();
			T hmBo = !boIds.isEmpty() ? boIds.get(0) : null;

			tx.commit();

			return hmBo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoByAttribute", "Find BO by attribute failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T findBoByAttribute(Class<T> boClass,
			String attrName, Object attrValue, QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			List<T> boIds = createQuery(em, boClass, null,
					new FilterParams(attrName, attrValue)).setMaxResults(1)
					.getResultList();
			T hmBo = null;

			if (!boIds.isEmpty()) {
				hmBo = boIds.get(0);
				queryBo.load(hmBo);
			}

			tx.commit();

			return hmBo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoByAttribute", "Find BO by attribute failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T findBoByAttribute(Class<T> boClass,
			String attrName, Object attrValue, Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			List<T> boIds = createQuery(em, boClass, null,
					new FilterParams(attrName, attrValue), domainId)
					.setMaxResults(1).getResultList();
			T hmBo = !boIds.isEmpty() ? boIds.get(0) : null;

			tx.commit();

			return hmBo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoByAttribute", "Find BO by attribute failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> T findBoByAttribute(Class<T> boClass,
			String attrName, Object attrValue, Long domainId, QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			T hmBo = null;
			List<T> boIds = createQuery(em, boClass, null,
					new FilterParams(attrName, attrValue), domainId)
					.setMaxResults(1).getResultList();

			if (!boIds.isEmpty()) {
				hmBo = boIds.get(0);
				queryBo.load(hmBo);
			}

			tx.commit();

			return hmBo;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBoByAttribute", "Find BO by attribute failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> List<T> findBosByCondition(Class<T> boClass,
			String attrName, String condition) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			List<T> bos = em.createQuery(
					"from " + boClass.getSimpleName() + " where upper("
							+ attrName + ") like :s").setParameter("s",
					condition.toUpperCase()).getResultList();

			tx.commit();

			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBosByCondition", "Find BOs by condition failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> List<T> findBosByCondition(Class<T> boClass,
			String attrName, String condition, QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			List<T> bos = em.createQuery(
					"from " + boClass.getSimpleName() + " where upper("
							+ attrName + ") like :s").setParameter("s",
					condition.toUpperCase()).getResultList();

			for (T bo : bos) {
				queryBo.load(bo);
			}

			tx.commit();

			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBosByCondition", "Find BOs by condition failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> List<T> findBosByCondition(Class<T> boClass,
			String attrName, String condition, Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			Query jpQuery = em.createQuery("from " + boClass.getSimpleName()
					+ " where upper(" + attrName
					+ ") like :s and ("+BoMgmt.getDomainMgmt()
					.getGlobalDomainCondition()+" or owner.id = :o)");
			jpQuery.setParameter("s", condition.toUpperCase());
			jpQuery.setParameter("o", domainId);
			List<T> bos = jpQuery.getResultList();

			tx.commit();

			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBosByCondition", "Find BOs by condition failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static <T extends HmBo> List<T> findBosByCondition(Class<T> boClass,
			String attrName, String condition, Long domainId, QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
		//	em.flush(); // Before query

			Query jpQuery = em.createQuery("from " + boClass.getSimpleName()
					+ " where upper(" + attrName
					+ ") like :s and owner.id = :o");
			jpQuery.setParameter("s", condition.toUpperCase());
			jpQuery.setParameter("o", domainId);
			List<T> bos = jpQuery.getResultList();

			for (T bo : bos) {
				queryBo.load(bo);
			}

			tx.commit();

			return bos;
		} catch (RuntimeException e) {
			rollback(tx);
			log.error("findBosByCondition", "Find BOs by condition failed.", e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	public static void rollback(EntityTransaction tx) {
		if (tx != null && tx.isActive()) {
			try {
				tx.rollback();
			} catch (IllegalStateException ise) {
				log.error("rollback", "Transaction not active.", ise);
			} catch (PersistenceException pe) {
				log.error("rollback", "Transaction rollback error.", pe);
			}
		}
	}

	public static void closeEntityManager(EntityManager em) {
		if (em != null && em.isOpen()) {
			try {
				em.close();
			} catch (IllegalStateException ise) {
				log.error("closeEntityManager", "The entity manager container-managed." ,ise);
			}
		}
	}

	public static EntityManager getEntityManager() {
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) HibernateUtil.getEntityManagerFactory();
		EntityManagerImpl emi = (EntityManagerImpl) emfi.createEntityManager();
		Session session = emi.getSession();

		/*-
		Connection conn = session.connection();

		try {
			setJDBCConnectionMode(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}*/

		session.doWork(
			 new Work() {
				 @Override
				 public void execute(Connection conn) throws SQLException {
					 setJDBCConnectionMode(conn);
				 }
			 }
	 	);

		return emi;
	}

	public static Connection getConnection() {
		EntityManagerFactoryImpl emfi = (EntityManagerFactoryImpl) HibernateUtil.getEntityManagerFactory();
		SessionFactoryImplementor sfi = emfi.getSessionFactory();
		Session session = sfi.openSession();
		JdbcConnectionAccess jca = ((SessionImplementor) session).getJdbcConnectionAccess();

		try {
			Connection conn = jca.obtainConnection();
			setJDBCConnectionMode(conn);
			return conn;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static void setJDBCConnectionMode(Connection conn) throws SQLException {
		boolean isHAPassiveMode = HAUtil.isSlave();

		// Set JDBC connection in read-only mode when HM/HMOL is in HA passive mode.
		if (isHAPassiveMode) {
			if (!conn.isReadOnly()) {
				conn.setReadOnly(true);
			}
		} else {
			if (conn.isReadOnly()) {
				conn.setReadOnly(false);
			}
		}
	}
	
	private static Long parseExceptionId(String errMsg) throws Exception{
		Long id = -1L;
		try{
			errMsg = errMsg.substring(errMsg.indexOf("id='") + 4 , errMsg.length());
			id = Long.parseLong(errMsg.substring(0, errMsg.indexOf("'")));	
		}catch(Exception ex){
			try{
				id = Long.parseLong(errMsg.substring(errMsg.indexOf("#") + 1, errMsg.length() - 1));	
			}catch(Exception e){
				return id;
			}
		}
		return id;
	}

}