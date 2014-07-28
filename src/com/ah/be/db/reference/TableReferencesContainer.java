package com.ah.be.db.reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class TableReferencesContainer implements Runnable {

    private static final Tracer log = new Tracer(TableReferencesContainer.class.getSimpleName());

    private ConcurrentMap<String, Map<String, String>> tableReferencesMap = new ConcurrentHashMap<String, Map<String,String>>();
    
    private final String[] tables = new String[]{"vpn_network"};
    
    private final ReentrantLock queryLock = new ReentrantLock();
    
    private Thread tableQueryThread;

    public void start() {
        tableQueryThread = new Thread(this);
        tableQueryThread.start();
    }
    
    public void stop() {
        tableQueryThread.interrupt();
        tableQueryThread = null;
    }
    
    public Map<String, String> getTableReferences(String tableName) {
        return tableReferencesMap.get(tableName);
    }
    
    @Override
    public void run() {
        MgrUtil.setTimerName(this.getClass().getSimpleName());
        
        if(HAUtil.isSlave()) {
            log.debug("In HA-Slave mode, do nothing.");
            return;
        }
        for (String table : tables) {
            Map<String, String> subMap = getReferencedTables(table, null);
            if(null != subMap) {
                tableReferencesMap.putIfAbsent(table, subMap);
            }
        }
    }

    /**
     * To assert whether the data is used by other tables, we need to find out which tables are referenced with the table.<br>
     * Every database contains the database basic information: e.g., constraints, foreign-key and so on.<br>
     * So we can get the information of the the referenced tables and the foreign-key columns from database.
     * 
     * @author Yunzhi Lin
     * - Time: Apr 27, 2012 8:04:03 PM
     * @param tableName
     * @param columnName The column will be 'id', if set Null.
     * @return Null or Map {key: ReferencedTable, value: ForeignKeyColumn}
     */
    private Map<String, String> getReferencedTables(String tableName, String columnName) {
        try {
            queryLock.lock();
            
            long start = System.currentTimeMillis();
            // get current database
            String getCurrentDatabase = "SELECT current_database()";
            List<?> list = QueryUtil.executeNativeQuery(getCurrentDatabase);
            if(list.isEmpty()) {
                return null;
            }
            String currentDatabase = (String) list.get(0);
            
            // get the referenced table and the foreign-key column name.
            String sqlSentence = "SELECT rc.constraint_catalog," 
                + "tc.table_name AS table_name," 
                + "kcu.column_name,"
                + "match_option,"
                + "update_rule,"
                + "delete_rule "
                + "FROM information_schema.referential_constraints AS rc "
                + "JOIN information_schema.table_constraints AS tc USING(constraint_catalog,constraint_schema,constraint_name) "
                + "JOIN information_schema.key_column_usage AS kcu USING(constraint_catalog,constraint_schema,constraint_name) "
                + "JOIN information_schema.key_column_usage AS ccu ON(ccu.constraint_catalog=rc.unique_constraint_catalog " 
                + "AND ccu.constraint_schema=rc.unique_constraint_schema " 
                + "AND ccu.constraint_name=rc.unique_constraint_name) "
                + "WHERE ccu.table_catalog='" + currentDatabase + "' "
                + "AND ccu.table_schema='public' "
                + "AND ccu.table_name='" + tableName + "' "
                + "AND ccu.column_name='" + (null == columnName ? "id" : columnName) +"'";
            list = QueryUtil.executeNativeQuery(sqlSentence);
            if(list.isEmpty()) {
                return null;
            }
            Map<String, String> tableMap = new HashMap<String, String>();
            for (Object object : list) {
                Object[] row = (Object[]) object;
                tableMap.put(row[1].toString(), row[2].toString());
            }
            long end = System.currentTimeMillis();
            log.debug("getReferencedTables", "find the references from "+tableName+" cost "+(end - start)+" milli seconds.");
            return tableMap.isEmpty() ? null : tableMap;
        } catch (Exception e) {
            log.error("getReferencedTables", "Error when query the datebase references.", e);
        } finally {
            queryLock.unlock();
        }
        return null;
    }
}
