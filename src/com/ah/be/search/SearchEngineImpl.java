/**
 * @filename			SearchEngineImpl.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.file.XMLFileReadWriter;
import com.ah.be.search.querybo.SearchEngineLazyLoad;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.SingleTableItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class SearchEngineImpl implements SearchEngine, Runnable {

	public static final Tracer		log = new Tracer(SearchEngineImpl.class.getSimpleName());
	
	public static final String		SEARCH_CONTROL_FILE	= "hm_search_tables.xml";
	
	private SearchEngineLazyLoad loazyLoad = new SearchEngineLazyLoad();
	
	private Index index;
	
	private PageIndex pageIndex;
	
	/*
	 * cache global ignored fields
	 */
	private List<String> globalIgnores;
	
	/*
	 * map class <----> ignored field 
	 */
	private Map<String, List<String>> ignoreFields;
	
	/*
	 * cache search tables
	 */
	private List<SearchTable> configTables;
	private List<SearchTable> hiveapTables;
	private List<SearchTable> clientTables;
	private List<SearchTable> adminTables;
	private List<SearchTable> faultTables;
	private List<SearchTable> macTables;
	private List<SearchTable> ipTables;
	private List<SearchTable> topoTables;
	private List<SearchTable> toolTables;
	private List<SearchTable> fullModeOnlyTables;
	private List<SearchTable> homeOnlyTables;
	
	/*
	 * cache tables which do not contain vhm
	 */
	private Set<String>   tablesNoDomain;
	
	/*
	 * a scheduler to clear expired search results in database
	 */
	private ScheduledExecutorService			scheduler;

	/*
	 * in some BOs, there are classed embedded in the collection
	 * fileds( List, Set, Map). these classes are not included in 
	 * predefined tables in the XML file. however, they should be 
	 * searched.
	 * 
	 * e.g. in BO ConfigTemplate:
	 * private Map<Long, ConfigTemplateSsid> ssidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
	 * ConfigTemplateSsid is the embedded class
	 * 
	 */
	private List<String>	embeddedClasses;
		
	public SearchEngineImpl() {
        
        initParams();
        
        load();
        
        addEmbeddedClasses();
        
        // only allow indexing in StandAlone mode
        if(!NmsUtil.isHostedHMApplication()) {
            index = new Index();
        }
        pageIndex = new PageIndex();
        
        pageIndex.setSearchEngine(this);
	}
	
	private void initParams() {
        globalIgnores = new ArrayList<String>();
        configTables = new ArrayList<SearchTable>(50);
        hiveapTables = new ArrayList<SearchTable>();
        clientTables = new ArrayList<SearchTable>();
        adminTables = new ArrayList<SearchTable>();
        faultTables = new ArrayList<SearchTable>();
        topoTables = new ArrayList<SearchTable>();
        toolTables = new ArrayList<SearchTable>();
        macTables = new ArrayList<SearchTable>();
        ipTables = new ArrayList<SearchTable>();
        fullModeOnlyTables = new ArrayList<SearchTable>();
        homeOnlyTables = new ArrayList<SearchTable>();
        
        tablesNoDomain = new HashSet<String>();
        tablesNoDomain.add("vhmManagement");
        tablesNoDomain.add("routing");  
        
        ignoreFields = new HashMap<String, List<String>>();
        
        embeddedClasses = new ArrayList<String>();
    }

    private void addEmbeddedClasses() {
		/*
		 * in map
		 */
		embeddedClasses.add("ConfigTemplateSsid");
		embeddedClasses.add("ConfigTemplateQos");
		embeddedClasses.add("QosNetworkService");
		embeddedClasses.add("QosMacOui");
		embeddedClasses.add("QosSsid");
		embeddedClasses.add("AlgConfigurationInfo");
		embeddedClasses.add("RadioProfileWmmInfo");
		
		/*
		 * in list
		 */
		embeddedClasses.add("HiveApStaticRoute");
		embeddedClasses.add("HiveApDynamicRoute");
		embeddedClasses.add("HiveApUpdateItem");
		embeddedClasses.add("SingleTableItem");
		embeddedClasses.add("IdsPolicySsidProfile");
		embeddedClasses.add("IpPolicyRule");
		embeddedClasses.add("MacFilterInfo");
		embeddedClasses.add("MacPolicyRule");
		embeddedClasses.add("DhcpServerOptionsCustom");
		embeddedClasses.add("DhcpServerIpPool");
		embeddedClasses.add("VpnServiceCredential");
		embeddedClasses.add("ActiveDirectoryDomain");
		embeddedClasses.add("MgmtServiceDnsInfo");
		embeddedClasses.add("MgmtServiceSnmpInfo");
		embeddedClasses.add("MgmtServiceSyslogInfo");
		embeddedClasses.add("MgmtServiceTimeInfo");
		embeddedClasses.add("RadiusServer");
		embeddedClasses.add("ActiveDirectoryOrLdapInfo");
		embeddedClasses.add("RadiusHiveapAuth");
		embeddedClasses.add("WalledGardenItem");
		
		embeddedClasses.add("FirewallPolicyRule");
		
		/*
		 * in set
		 */
		embeddedClasses.add("MgmtServiceIPTrack");
		embeddedClasses.add("VlanDhcpServer");
	}

	/* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#run()
	 */
	@Override
	public void start() {
	    if(null != index) {
	        index.start();
	    }
		pageIndex.start();
		
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 3600, 3600, TimeUnit.SECONDS);
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#search(com.ah.be.search.SearchParameter)
	 */
	@Override
	public int search(SearchParameter searchParameter) {
		if(searchParameter == null) {
			return 0;
		}
		
		/*
		 * clear results of last search
		 */
		long t = System.currentTimeMillis();
		SearchUtil.clearSearchResults(searchParameter.getUserContext());
		log.debug("search", "it takes " + (System.currentTimeMillis() - t) + " ms to clean up results.");
		
		int foundCount = 0;
		
		/*
		 * page resources
		 */
		foundCount += pageIndex.search(searchParameter);
		
		/*
		 * feature name and data in database
		 */
		if(searchParameter.isMac()) {
			foundCount += searchTables(SearchParameter.TYPE_ONLY_MAC, searchParameter);
		} else if(searchParameter.isIp()) {
			foundCount += searchTables(SearchParameter.TYPE_ONLY_IP, searchParameter);
		} else {
			if(searchParameter.isConfiguration()) {
				foundCount += searchTables(SearchParameter.TYPE_CONFIGURATION, searchParameter);
			}
			
			if(searchParameter.isHiveAP()) {
				foundCount += searchTables(SearchParameter.TYPE_HIVEAP, searchParameter);
			}
			
			if(searchParameter.isClient()) {
				foundCount += searchTables(SearchParameter.TYPE_CLIENT, searchParameter);
			}
			
			if(searchParameter.isAdmin()) {
				foundCount += searchTables(SearchParameter.TYPE_ADMIN, searchParameter);
			}
			
			if(searchParameter.isFault()) {
				foundCount += searchTables(SearchParameter.TYPE_FAULT, searchParameter);
			}	
			
			if(searchParameter.isTopo()) {
				foundCount += searchTables(SearchParameter.TYPE_TOPO, searchParameter);
			}	
			
			if(searchParameter.isTool()) {
				foundCount += searchTables(SearchParameter.TYPE_TOOL, searchParameter);
			}	
		}
		
		/*
		 * alarm/event
		 */
		if(needSearchIndex(searchParameter)) {
			foundCount += index.search(searchParameter);
		}
		
		log.debug("It takes " + (System.currentTimeMillis() - t) + "ms to search. Total: " + foundCount + " targets found");
		
		return foundCount;
	}

	/* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#stop()
	 */
	@Override
	public void stop() {
	    if(null != index) {
	        index.stop();
	    }
		pageIndex.stop();
		
		/*
		 * shutdown executor of index
		 */
		if (!scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}
	
	public List<SearchTable> getPageIndexTables() {
		List<SearchTable> tables = new ArrayList<SearchTable>();
		
		tables.addAll(this.configTables);
		tables.addAll(this.hiveapTables);
		tables.addAll(this.clientTables);
		tables.addAll(this.adminTables);
		tables.addAll(this.toolTables);
		tables.addAll(this.faultTables);
		
		return tables;
	}

	/**
	 * load resources into search engine
	 * 
	 * @author Joseph Chen
	 */
	private void load() {
		/*
		 * load tables
		 */
		try {
			loadTables();
		} catch (DocumentException de) {
			log.error("load", "Failed to load tables search config file.", de);
		}
	}
	
	private void loadTables() throws DocumentException {
		log.info("load", "begin loading search tables.");
		
		File controlFile = new File(SEARCH_RESOURCES_PATH + SEARCH_CONTROL_FILE);
		
		if(!controlFile.exists()) {
			log.info("load", "control file is not existed.");
			return ;
		}
		
		Document document = XMLFileReadWriter.parser(controlFile);
		Element root = document.getRootElement();
		List<?> elements = root.elements();

		for (Object obj : elements) {
			Element element = (Element) obj;

			/*
			 * load tables
			 */
			if (element.getName().equalsIgnoreCase("tables")) {
				loadTables(element);
				continue;
			}

			/*
			 * load global ignores
			 */
			if (element.getName().equalsIgnoreCase("ignores")) {
				loadGlobalIgnores(element);
				continue;
			}
		}
		
		log.info("load", "loading search tables ends.");
	}
	
	private void loadGlobalIgnores(Element element) {
		if(element == null) {
			return ;
		}
		
		Iterator<?> it = element.elements().iterator();
		
		try {
		while(it.hasNext()) {
			Element subElement = (Element)it.next();
			this.globalIgnores.add(subElement.attributeValue("name"));
			
		}
		} catch(Exception ex) {
			log.error("Failed to load global ignores", ex);
		}
		
	}

	private void loadTables(Element element) {
		if(element == null) {
			return ;
		}
		
		Iterator<?> it = element.elements().iterator();
		
		int tableId = 1;
		Element e;
		SearchTable table;
		
		while(it.hasNext()) {
			e = (Element)it.next();
			table = new SearchTable();
			table.setId(tableId++);
			
			/*
			 * load table attributes
			 */
			loadTableAttributes(table, e);
			
			/*
			 * column, filter, urlparam
			 */
			loadTableSubElement(table, e);
			
			addTable(table);
		}
	}
	
	private void loadTableAttributes(SearchTable table, Element element) {
		if(table == null || element == null) {
			return ;
		}
		
		try {
		table.setAction(element.attributeValue("action"));
		table.setBoClass(element.attributeValue("bo"));
		table.setKey(element.attributeValue("key"));
		table.setFeature(element.attributeValue("node"));
		
		/*
		 * subject
		 */
		String subject = element.attributeValue("subject");
		
		// MAC
		table.setMac(subject.charAt(0) == '1');
		
		// IP
		table.setIp(subject.charAt(1) == '1');
		
		table.setBoolFor(element.attributeValue("bool"));
		table.setType(SearchParameter.getTypeFromString(element.attributeValue("type")));
		
		String mode = element.attributeValue("mode");
		
		if(mode.substring(0, 1).equals("1")) {
			table.setFullModeOnly(true);
		} else {
			table.setFullModeOnly(false);
		}
		
		if(mode.substring(1, 2).equals("1")) {
			table.setHomeOnly(true);
		} else {
			table.setHomeOnly(false);
		}
		} catch(Exception ex) {
			log.error("Failed to load attributes of table " + table.getBoClass(), ex);
		}		

	}
	
	private void loadTableSubElement(SearchTable table, Element element) {
		if(table == null || element == null) {
			return ;
		}
		
		Iterator<?> it = element.elements().iterator();
		Element e;
		
		while(it.hasNext()) {
			e = (Element)it.next();
			
			try {
				if(e.getName().equalsIgnoreCase("ignore")) {
					addIgnoreField(table.getBoClass(), e.getText());
				} else if(e.getName().equalsIgnoreCase("filter")) {
					String operator = getOperator(e.attributeValue("operator"));
					table.addFilter(e.attributeValue("name"), e.attributeValue("type"), e.getText(), operator);
				} else if(e.getName().equalsIgnoreCase("urlparam")) {
					table.addUrlParameter(e.attributeValue("name"), e.getText());
				}
			} catch(Exception ex) {
				log.error("Failed to load sub-elements of table " + table.getBoClass(), ex);
			}
		}
	}
	
	private String getOperator(String source) {
		String operator;
		
		if("1".equals(source)) {
			operator = ">";
		} else if("10".equals(source)) {
			operator = ">=";
		} else if("-1".equals(source)) {
			operator = "<";
		} else if("-10".equals(source)) {
			operator = "<=";
		} else {
			operator = "=";
		}
		
		return operator;
	}
	
	private void addIgnoreField(String className, String fieldName) {
		List<String> fields = ignoreFields.get(className);
		
		if(fields != null) { // className in the map
			fields.add(fieldName);
		} else { // className not in the map
			fields = new ArrayList<String>();
			fields.add(fieldName);
			ignoreFields.put(className, fields);
		}
	}
	private void addTable(SearchTable table) {
		if(table == null) {
			return ;
		}
		
		switch(table.getType()) {
		case SearchParameter.TYPE_CONFIGURATION:
			configTables.add(table);
			break;
		case SearchParameter.TYPE_HIVEAP:
			hiveapTables.add(table);
			break;
		case SearchParameter.TYPE_CLIENT:
			clientTables.add(table);
			break;
		case SearchParameter.TYPE_ADMIN:
			adminTables.add(table);
			break;
		case SearchParameter.TYPE_FAULT:
			faultTables.add(table);
			break;
		case SearchParameter.TYPE_TOPO:
			topoTables.add(table);
			break;
		case SearchParameter.TYPE_TOOL:
			toolTables.add(table);
			break;
		default:
			break;
		}
		
		if(table.isMac()) {
			macTables.add(table);
		}
		
		if(table.isIp()) {
			ipTables.add(table);
		}
		
		if(table.isFullModeOnly()) {
			fullModeOnlyTables.add(table);
		}
		
		if(table.isHomeOnly()) {
			homeOnlyTables.add(table);
		}
	}
	
	private int searchTables(int type, SearchParameter searchParam) {
		
		List<SearchTable> tables = null;
		
		/*
		 * get table list by type
		 */
		switch(type) {
		case SearchParameter.TYPE_CONFIGURATION:
			tables = this.configTables;
			break;
		case SearchParameter.TYPE_HIVEAP:
			tables = this.hiveapTables;
			break;
		case SearchParameter.TYPE_CLIENT:
			tables = this.clientTables;
			break;
		case SearchParameter.TYPE_ADMIN:
			tables = this.adminTables;
			break;
		case SearchParameter.TYPE_FAULT:
			tables = this.faultTables;
			break;
		case SearchParameter.TYPE_TOPO:
			tables = this.topoTables;
			break;
		case SearchParameter.TYPE_TOOL:
			tables = this.toolTables;
			break;
		case SearchParameter.TYPE_ONLY_MAC:
			tables = this.macTables;
			break;
		case SearchParameter.TYPE_ONLY_IP:
			tables = this.ipTables;
			break;
		
		default:
			break;
		}
		
		if(tables == null || tables.size() == 0) {
			return 0;
		}
		
		/*
		 * get permitted tables
		 */
		tables = getPermittedTables(tables, searchParam.getUserContext());
		
		/*
		 * search permitted tables
		 */
		return searchTables(tables, searchParam);
		
	}
	
	private List<SearchTable> getPermittedTables(List<SearchTable> tables, HmUser userContext){
		/*
		 * tables wouldn't be null or empty here
		 */
		if (userContext == null) {
			return tables;
		}
		
		List<SearchTable> permitted = new ArrayList<SearchTable>();
		 Map<String, HmPermission> permissions = userContext.getUserGroup().getFeaturePermissions();
		
		 /*
		  * Teacher View enabled or not
		  */
		 boolean tvEnabled = NmsUtil.isTeacherViewEnabled(userContext);
		 
		 /*
		  * switched domain or not
		  */
		 boolean isSwitchedDomain = userContext.getSwitchDomain() != null;
		 
		for(SearchTable table : tables) {
			if(!tvEnabled) {
				if(table.getKey() != null
						&& table.getKey().startsWith("tv")) {
					continue;
				}
			}
			
			if(isSwitchedDomain) {
				if(table.isHomeOnly()) {
					continue;
				}
			}
			
			if(permissions.get(table.getKey()) != null) {
				permitted.add(table);
			}
		}
		
		
		return permitted;
	}
	
	private int searchTables(List<SearchTable> tables, SearchParameter searchParam) {
		int foundCount = 0;
		
		for(SearchTable table : tables) {
			foundCount += searchTable(table, searchParam);
		}
		
		return foundCount;
	}
	
	private int searchTable(SearchTable table, SearchParameter searchParam) {
		if(!searchParam.isFullMode()) {
			if(table.isFullModeOnly()) {
				return 0;
			}
		}
		
		if(!searchParam.getUserContext().getDomain().isHomeDomain()) {
			if (table.isHomeOnly()) {
				return 0;
			}
		}
		
		int foundCount = 0;
		List<Target> featureColumns = new ArrayList<Target>(100);
		
		/*
		 * feature & column
		 */
		foundCount += searchFeatureAndColumns(featureColumns, table, searchParam);
		
		if(foundCount > 0 ) {
			/*
			 * save into database
			 */
			SearchUtil.saveTargets(featureColumns);
		}
		
		/*
		 * real data
		 */
		foundCount += searchData(table, searchParam);
		
		return foundCount;
	}
	
	private int searchFeatureAndColumns(List<Target> foundList, SearchTable table, SearchParameter searchParam) {
		if(searchParam == null
				|| searchParam.getKeyword() == null) {
			return 0;
		}
		
		String keyword = searchParam.getKeyword();
		int foundCount = 0;
		
		/*
		 * feature name
		 */
		String feature = table.getFeature();
		
		if(feature != null) {
			if(feature.toLowerCase().contains(keyword)) {
//				log.debug("Found feature: " + feature);
				String actionName = getActionName(table, searchParam);
				foundList.add(new Target(actionName,
						feature, 
						table.getType(),
						spellURLParameter(table.getUrlParameters()),
						searchParam.getUserContext().getUserName(),
						searchParam.getUserContext().getDomain().getId(),
						null));
				foundCount++;
			}
		}
		
		/*
		 * columns
		 */
//		List<String> columns = table.getColumns();
//		
//		if(columns == null || columns.size() == 0) {
//			return foundCount;
//		}
//		
//		for(String column : columns) {
//			if(column.toLowerCase().contains(keyword)) {
////				log.debug("Found column: " + column);
//				
//				ColumnTarget target = new ColumnTarget(table.getAction(),
//						feature, 
//						table.getType(),
//						spellURLParameter(table.getUrlParameters()),
//						searchParam.getUserContext().getUserName(),
//						searchParam.getUserContext().getDomain().getId(),
//						null);
//				target.setColumn(column);
//				foundList.add(target);
//				foundCount++;
//				break;
//			}
//		}
		
		return foundCount;
	}

	/**
	 * get the action name (handle some specific action for different app mode)
	 * @author Yunzhi Lin
	 * - Time: Oct 14, 2011 6:59:32 PM
	 * @param table
	 * @param searchParam
	 * @return action name
	 */
	private String getActionName(SearchTable table, SearchParameter searchParam) {
		String actionName = table.getAction();
		if("ssidProfiles".equals(actionName) && searchParam.isFullMode()) {
			actionName = "ssidProfilesFull";
		}
		return actionName;
	}
	
	/**
	 * search database using hibernate and Java reflection
	 *
	 * @param table -
	 * @param searchParam -
	 */
	private int searchData(SearchTable table, SearchParameter searchParam) {
		if(table == null || searchParam == null) {
			return 0;
		}
		
		if(table.getBoClass().equalsIgnoreCase("null")) {
			/*
			 * this table have no BOs
			 */
			return 0;
		}
		
//		log.debug("searchData", "Searching <" + table.getAction() + "> ...");
//		long t0 = System.currentTimeMillis();
		
		/*
		 * get BO class
		 */
		Class<?> boClass = null;
		
		try {
			boClass = Class.forName(table.getBoClass());
		} catch (ClassNotFoundException e) {
			log.error("searchData", 
					"BO class<" + table.getBoClass() + "> cannot be loaded.",
					e);
		}
		
		if(boClass == null) {
			return 0;
		}
		
		/*
		 * get filter parameters
		 */
		FilterParams filterParams = "com.ah.bo.performance.AhClientSession".equals(table.getBoClass()) ?
				getFilterParamsForJDBC(table.getFilters()) : getFilterParamsForHibernate(table.getFilters());
		
		/*
		 * get data out of database
		 */
		List<?> bos;

		/*
		 * paging
		 */
		Paging<?> page = new PagingImpl(boClass);
		page.setPageSize(SEARCH_QUERY_STEP);
		page.clearRowCount();
		SortParams sort = new SortParams("id");
		EntityTarget target;
		List<Target> targets = new ArrayList<Target>(2000);
		int foundCount = 0, searchCount = 0;

		while(page.hasNext()) {
			if(tablesNoDomain.contains(table.getAction())) {
				bos = page.next().executeQuery(sort, filterParams);
			} else {
				bos = page.next().executeQuery(sort, filterParams, 
						searchParam.getUserContext(), loazyLoad);
			}
			
			if(bos == null || bos.size() == 0) {
				break;
			}

			/*
			 * search each record
			 */
			try {
				for(Object bo : bos) {
					target = searchBO(bo, table, searchParam);
					HmBo hmbo = (HmBo)bo;
					
					if(target != null) {
						target.setBoId(hmbo.getId());
						
						if(((HmBo)bo).getOwner() != null) {
							target.setBoDomainId(hmbo.getOwner().getId());
						} else {
							target.setBoDomainId(null);
						}
						
						target.setUserDomainId(searchParam.getUserContext().getDomain().getId());
						target.setReference(getReference(bo, target));
						targets.add(target);
						foundCount++;
						
						if(targets.size() >= SEARCH_INSERT_STEP) {
							/*
							 * insert into database
							 */
							SearchUtil.saveTargets(targets);
							targets.clear();
						}
						
						searchCount++;
					}
				}
			} catch(Exception e) {
				log.error("searchData", "Error in searching table<" + table.getBoClass() + ">.", e);
			}

			bos.clear();
		}
				
		/*
		 * save into database
		 */
		SearchUtil.saveTargets(targets);
		
//		log.debug("it took " + (System.currentTimeMillis() - t0) + "ms to search <" + table.getAction() + ">.");
//		log.debug("Searched: " + searchCount + ", found: " + foundCount);
		
		return foundCount;
	}

	private EntityTarget searchBO(Object bo, SearchTable table, SearchParameter searchParam) throws Exception {
		if(bo == null || searchParam == null) {
			return null;
		}
		
		String keyword = searchParam.getKeyword();
		Class<?> c = bo.getClass();
		
		for(;!c.equals(Object.class); c=c.getSuperclass()) {
			/*
			 * only search needed classes
			 */
			if(!needSearchClass(c, searchParam)) {
				continue;
			}
			
			Field[] fields = c.getDeclaredFields();
			
			for(Field field : fields) {
				/*
				 * only search needed fields
				 */
				if(!needSearchField(c, field)) {
					continue;
				}
				
				field.setAccessible(true);
				Object value = field.get(bo);
				
				if(value == null) {
					continue;
				}
				
				Type type = field.getGenericType();

				if(type == int.class
						|| type == short.class
						|| type == byte.class
						|| type == long.class
						|| type == char.class
						|| type == float.class
						|| type == double.class
						|| type == String.class) {
					if((String.valueOf(value)).toLowerCase().contains(keyword)) {
//						log.debug("searchBO", "Found! " + c.getSimpleName() + " : " + field.getName() + " : " + value);
						return getTarget(bo, field, table, searchParam);
					}
					
				} else if(type == boolean.class) {
					
				} else if(type == Date.class) {
					String date = AhDateTimeUtil.getSpecifyDateTime((Date)value, 
							TimeZone.getDefault());
					
					if(date.toLowerCase().contains(keyword)) {
						return getTarget(bo, field, table, searchParam);
					}
				} else if(type == HmTimeStamp.class) {
					String date = AhDateTimeUtil.getFormattedDateTime((HmTimeStamp)value);
					
					if(date.toLowerCase().contains(keyword)) {
						return getTarget(bo, field, table, searchParam);
					}
				} else if(type instanceof ParameterizedType) {
					/*
					 * collections
					 */
					Iterator<?> it = null;
					
					if(value instanceof List
							|| value instanceof Set) {
						it = ((Collection<?>)value).iterator();
					}
					
					if(value instanceof Map) {
						it = (((Map<?,?>)value).values()).iterator();
					}
					
					EntityTarget target;
					
					while(it.hasNext()) {
						target = searchName(it.next(), table, searchParam);
						
						if(target != null) {
							return target;
						}
					}
				} else {
					/*
					 * the field is other BO or object
					 * just search the 'name' field
					 */
					EntityTarget target = searchName(value, table, searchParam);
					
					if(target != null) {
						return target;
					}
				}
			}
		}
		
		/*
		 * not found
		 */
		return null;
	}
	
	private EntityTarget searchName(Object bo, SearchTable table, SearchParameter searchParam) throws Exception {
		if(bo == null || searchParam == null) {
			return null;
		}
		
		String keyword = searchParam.getKeyword();
		Class<?> c = bo.getClass();
		
		/*
		 * only search needed classes
		 */
		if(!needSearchClass(c, searchParam)) {
			return null;
		}

		if(c.getSimpleName().contains("_$$_")) {
			// lazy object
			return searchLazyObject(bo, table, searchParam);
		}
		
		Field[] fields = c.getDeclaredFields();
		
		for(Field field : fields) {
		    if(HmBo.class.isAssignableFrom(field.getType())) {
		        // to search the HmBo items under list. E.g., FirewallPolicy-> FirewallPolicyRule -> UserProfile
		        if(!field.getType().getSimpleName().contains("_$$_")) {
                    field.setAccessible(true);
                    Object value = field.get(bo);

                    if (value == null) {
                        continue;
                    }
                    return searchName(value, table, searchParam);
		        }
		    } else {
		        String fieldName = field.getName();
		        
		        /*
		         * only search field of name
		         */
		        if(!fieldName.toLowerCase().contains("name")) {
		            continue;
		        }
		        
		        field.setAccessible(true);
		        Object value = field.get(bo);
		        
		        if(value == null) {
		            continue;
		        }
		        
		        if((String.valueOf(value)).toLowerCase().contains(keyword)) {
		            return getTarget(bo, field, table, searchParam);
		        }
		    }
		}
		
		return null;
	}
	
	private EntityTarget searchLazyObject(Object bo, SearchTable table, SearchParameter searchParam) throws Exception {
		/*
		 * for instance
		 * 
		 * Vlan_$$_javassist_43
		 * 	|
		 * 	|--handler
		 * 		|
		 * 		|--target=Vlan	
		 */
		if(bo == null) {
			return null;
		}
		
		Class<?> c = bo.getClass();
		
		for(;!c.equals(Object.class); c=c.getSuperclass()) {
			/*
			 * only search needed classes
			 */
			if(!needSearchClass(c, searchParam)) {
				continue;
			}
			
			Field[] fields = c.getDeclaredFields();
			
			for(Field field : fields) {
				/*
				 * only search needed fields
				 */
				if(!needSearchField(c, field)) {
					continue;
				}
	
				String name = field.getName();
				
				if(!(name.equals("target") 
						|| name.equals("handler")
						|| name.toLowerCase().contains("name"))) {
					continue;
				}
				
				field.setAccessible(true);
				Object value = field.get(bo);
				
				if(value == null) {
					continue;
				}
				
				Type type = field.getGenericType();

				if(type == String.class) {
					if((String.valueOf(value)).toLowerCase().contains(searchParam.getKeyword())) {
						return getTarget(bo, field, table, searchParam);
					}
				} else if(type == int.class
						|| type == short.class
						|| type == byte.class
						|| type == long.class
						|| type == char.class
						|| type == float.class
						|| type == double.class
						|| type == boolean.class
						|| type == Date.class
						|| type == HmTimeStamp.class) { 
					
				} else if(type instanceof ParameterizedType) {
					
				} else {
					
					EntityTarget target = searchLazyObject(value, table, searchParam);

					if(target != null) {
						return target;
					}
				}
			
			}			
		}
		
		return null;
	}
	
	/**
	 * get filter parameters for querying database
	 * the parameter format is only for Hibernate. 
	 * for example: select * from table_name where col_1 = :s1 and col_2 = :s2. 
	 * 
	 * @param filters -
	 * @return -
	 */
	private FilterParams getFilterParamsForHibernate(List<FilterParam> filters) {
		if(filters == null || filters.size() == 0) {
			return null;
		}
		
		StringBuilder where = new StringBuilder();
		List<Object> values = new ArrayList<Object>();
		
		for(int i=1; i<=filters.size(); i++) {
			FilterParam filter = filters.get(i-1);
			String name = filter.getName();
			String type = filter.getType();
			String value = filter.getValue();
			String operator = filter.getOperator();
			String[] splits = value.split(",");
			
			if(splits.length == 1) {
				where.append(name).append(operator).append(":s").append(i);
				values.add(getFilterValue(type, value));
			} else {
				where.append("( ");
				
				for(int j=1; j<=splits.length; j++) {
					where.append(name).append(operator).append(":s").append(i+j-1);

					if(j != splits.length) {
						where.append(" OR ");
					}
					
					values.add(getFilterValue(type, splits[j-1]));
				}
				
				where.append(")");
			}
			
			if(i != filters.size()) {
				where.append(" AND ");
			}
		}
			
		return new FilterParams(where.toString(), values.toArray());
	}
	
	/**
	 * get filter parameters for querying database
	 * the parameter format is only for JDBC. 
	 * for example: select * from table_name where col_1 = ? and col_2 = ?. 
	 * 
	 * @param filters -
	 * @return -
	 */
	private FilterParams getFilterParamsForJDBC(List<FilterParam> filters) {
		if(filters == null || filters.size() == 0) {
			return null;
		}
		
		StringBuilder where = new StringBuilder();
		List<Object> values = new ArrayList<Object>();
		
		for(int i=1; i<=filters.size(); i++) {
			FilterParam filter = filters.get(i-1);
			String name = filter.getName();
			String type = filter.getType();
			String value = filter.getValue();
			String operator = filter.getOperator();
			String[] splits = value.split(",");
			
			if(splits.length == 1) {
				where.append(name).append(operator).append("?");
				values.add(getFilterValue(type, value));
			} else {
				where.append("( ");
				
				for(int j=1; j<=splits.length; j++) {
					where.append(name).append(operator).append("?");

					if(j != splits.length) {
						where.append(" OR ");
					}
					
					values.add(getFilterValue(type, splits[j-1]));
				}
				
				where.append(")");
			}
			
			if(i != filters.size()) {
				where.append(" AND ");
			}
		}
			
		return new FilterParams(where.toString(), values.toArray());
	}
	
	private Object getFilterValue(String type, String value) {
		if("short".equalsIgnoreCase(type)) {
			return new Short(value);
		} else if("byte".equalsIgnoreCase(type)) {
			return new Byte(value);
		} else if("int".equalsIgnoreCase(type)) {
			return new Integer(value);
		} else if("long".equalsIgnoreCase(type)) {
			return new Long(value);
		} else if("String".equalsIgnoreCase(type)) {
			return value;
		} else if("DosType".equalsIgnoreCase(type)) {
			int tempType= Integer.parseInt(value);
			DosType dosType = null;
			
			switch(tempType) {
			case 0:
				dosType = DosType.MAC;
				break;
			case 1:
				dosType = DosType.MAC_STATION;
				break;
			case 2:
				dosType = DosType.IP;
				break;
			default:
				break;
			}
			
			return dosType;
		}
		
		return null;
	}
	
	private boolean needSearchClass(Class<?> c, SearchParameter param) {
		
		String className = c.getSimpleName();
		
		if(this.embeddedClasses.contains(className)) {
			return true;
		}
		
		if(className.contains("LazyInitializer")
		    || className.contains("$$_javassist")) {
			return true;
		}
		
		if(needSearchUnderMode(c, param)
				&& needSearchUnderHome(c, param)) {
			if(needSearchClass(this.configTables, c)) {
				return true;
			}
			
			if(needSearchClass(this.hiveapTables, c)) {
				return true;
			}
			
			if(needSearchClass(this.adminTables, c)) {
				return true;
			}
			
			if(needSearchClass(this.clientTables, c)) {
				return true;
			}
			
			if(needSearchClass(this.topoTables, c)) {
				return true;
			}
			
			if(needSearchClass(this.toolTables, c)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean needSearchUnderMode(Class<?>c, SearchParameter param) {
		/*
		 * if the search parameter is full mode, search all classes
		 * if the search parameter is express mode, and the class does not 
		 * belong to full mode only, search the class
		 * 
		 * else, need not search the class
		 */
		if(param.isFullMode()) {
			return true;
		} else {
			if(!needSearchClass(this.fullModeOnlyTables, c)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	private boolean needSearchUnderHome(Class<?>c, SearchParameter param) {
		if(param.getUserContext().getDomain().isHomeDomain()) {
			return true;
		} else {
			if(!needSearchClass(this.homeOnlyTables, c)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean needSearchClass(List<SearchTable> tables, Class<?> c) {
		if(tables == null) {
			return false;
		}
		
		for(SearchTable table : tables) {
			if(table.getBoClass().equalsIgnoreCase(c.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean needSearchField(Class<?> c, Field field) {
		if(Modifier.isStatic(field.getModifiers())) {
			return false;
		}
	
		if(field.isEnumConstant()) {
			return false;
		}
		
		String fieldName = field.getName();
		
		if(this.globalIgnores.contains(fieldName)) {
			return false;
		}
		
		/*
		 * javassist lazy initializer
		 * only search filed "target"
		 */
		if(c.getSimpleName().contains("LazyInitializer")
				&& !fieldName.equals("target")) {
			return false;
		}
		
		Type type = field.getGenericType();
		
		if(type == Tracer.class
				|| type == Long.class) {
			return false;
		}
		
		/*
		 * do not search password
		 */
		if(fieldName.toLowerCase().contains("password")) {
			return false;
		}
		
		/*
		 * to avoid dead lock
		 * MapContainerNode <--> MapNode
		 * Vlan <--> UserProfile
		 * UserProfileAttribute <--> UserProfile
		 * TunnelSetting <--> UserProfile
		 * IpPolicy <--> UserProfile
		 * MacPolicy <--> UserProfile
		 * HmUserGroup <--> HmUser
		 */

		for (String key : ignoreFields.keySet()) {
			if (key.equals(c.getName())) {
				List<String> values = ignoreFields.get(key);

				if (values == null) {
					continue;
				}

				if (values.contains(fieldName)) {
					return false;
				}
			}
		}
		
		if(c == SingleTableItem.class) {
			if(fieldName.equalsIgnoreCase("location")) {
				return false;
			}
		}
		
		if(c == HiveApUpdateResult.class) {
			if(fieldName.equalsIgnoreCase("items")) {
				return false;
			}
		}
		
		return true;
	}
	
	private EntityTarget getTarget(Object obj, 
			Field field, 
			SearchTable table, 
			SearchParameter searchParam) throws Exception {
		String actionName = getActionName(table, searchParam);
		EntityTarget target = new EntityTarget(actionName,
				table.getFeature(),
				table.getType(),
				spellURLParameter(table.getUrlParameters()),
				searchParam.getUserContext().getUserName(),
				searchParam.getUserContext().getDomain().getId(),
				null);
		
		target.setFieldName(field.getName());
		target.setFieldValue(String.valueOf(field.get(obj)));
		target.setReference(obj.getClass().getName());
		
		return target;
	}
	
	private String getReference(Object bo, EntityTarget target) {
		if(bo == null || target == null) {
			return null;
		}
		
		if(bo.getClass().getName().equals(target.getReference())) {
			return null;
		}
		
		/* 
		 * search all tables to find feature name
		 */
		// configuration
		String boClass = target.getReference();
		List<SearchTable> allTables = new ArrayList<SearchTable>(100);
		allTables.addAll(this.configTables);
		allTables.addAll(this.hiveapTables);
		allTables.addAll(this.clientTables);
		allTables.addAll(this.adminTables);
		allTables.addAll(this.topoTables);
		allTables.addAll(this.toolTables);
		
		for(SearchTable table : allTables) {
			if(table.getBoClass().equals(boClass)) {
				return table.getFeature();
			}
		}
		
		return null;
	}
	
	private boolean needSearchIndex(SearchParameter searchParameter) {
		
	    if(null == index) {
	        return false;
	    }
	    
		if(searchParameter.isFault()
				|| searchParameter.isMac()
				|| searchParameter.isIp()) {
			if(getPermittedTables(this.faultTables, searchParameter.getUserContext()).size() > 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void run() {
		/*
		 * calculate time
		 */
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		
		c.set(Calendar.HOUR, c.get(Calendar.HOUR) - SEARCH_RESULT_LIFE_TIME);
		
		/*
		 * filter
		 */
		FilterParams filter = new FilterParams("version <= :s1",
				new Object[] {c.getTime()});
		
		try {
			QueryUtil.removeBos(Target.class, filter);
		} catch (Exception e) {
			log.error("run", "Failed to clear search results.", e);
		}
	}
	
	public String spellURLParameter(List<NameValuePair> urlParams) {
		if(urlParams == null || urlParams.size() == 0) {
			return null;
		}
		
		StringBuilder url = new StringBuilder();
		int i = 0;
		
		for(NameValuePair param : urlParams) {
			if (i != 0) {
				url.append("&");
			}

			i++;
			url.append(param.getName()).append("=").append(param.getValue());
		}
		
		return url.toString();
	}

    public PageIndex getPageIndex() {
        return pageIndex;
    }

}