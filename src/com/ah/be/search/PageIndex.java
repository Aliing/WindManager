/**
 * @filename			PageIndex.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R2
 *
 * Copyright (c) 2006-2010 Aerohive Co., Ltd.
 * All right reserved.
 */
package com.ah.be.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.ls.ClientTrustManager;
import com.ah.be.ls.HostnameVerify;
import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.FileManager;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * An Index for page resources
 */
public class PageIndex implements SearchEngine {

	public static final Tracer		log = new Tracer(PageIndex.class.getSimpleName());

	/*
	 * index files
	 *
	 * indexFiles[0]	-	numMap
	 * indexFiles[1]	-	alphaMap
	 * indexFiles[2]	-	numalphaMap
	 * indexFiles[3]	-	otherMap
	 */
	private final String[][] indexFiles = new String[4][INDEX_FILE_ARRAY_SIZE];

	/*
	 * index maps
	 *
	 * map for numeric
	 */
	private Map<String, DocumentList> numMap;

	/*
	 * map for alphabetic
	 */
	private Map<String, DocumentList> alphaMap;

	/*
	 * map for numeric - alphabetic
	 */
	private Map<String, DocumentList> numalphaMap;

	/*
	 * map for others
	 */
	private Map<String, DocumentList> otherMap;

	/*
	 * resources map
	 */
	private Map<String, Set<PageElement>> resources;

	private final static String INDEX_FINISHED_FLAG_FILE = AhDirTools.getHmRoot() +
									"resources" + File.separator +
									"search" + File.separator + "page_index_finished.flag";

	private final static String INDEX_TEMPORARY_RESULT_FILE = AhDirTools.getHmRoot() +
									"resources" + File.separator +
									"search" + File.separator + "tempHTML.htm";

	private final static String INDEX_PAGE_RESOURCES_FILE = AhDirTools.getHmRoot() +
									"resources" + File.separator +
									"search" + File.separator + "allPageResources.res";

	private final static String PAGE_ELEMENTS_FILE_SUFFIX = ".elements";

	private final static int PAGE_INDEX_DELAY = 5;	// unit: minutes

	/**
	 * The max size of indexed elements which will be kept in memory.
	 * The elements beyond this size will be dumped into file.
	 */
	private final static int PAGE_INDEX_MEMORY_SIZE = 5000;

	/**
	 * A limit of length of text element on page.
	 * The text beyond this limit will be broken and then index.
	 */
	private final static int PAGE_TEXT_MAX_LENGTH = 2048;

	private final static short PAGE_TYPE_NORMAL = 1;

	private final static short PAGE_TYPE_LIST_VIEW = 2;

	private SearchEngineImpl searchEngine;

	private SearchTable parsingTable;

	private static boolean hasIndexFinished;

	private SSLContext sc;

	private List<String> elementsFiles;

	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> scheduledTask;

	private HmUser userContext;

	public PageIndex() {
	    initParams();
	    load();
	}

	private void initParams() {
        numMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        alphaMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        numalphaMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        otherMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        resources = new HashMap<String, Set<PageElement>>();

    }

    /**
     * Initial the user context
     *
     * @author Yunzhi Lin
     * - Time: Mar 6, 2012 4:53:11 PM
     */
    private void initUserContext() {
        try {
            userContext = QueryUtil.findBoByAttribute(HmUser.class, "userName", HmUser.ADMIN_USER, new QueryBo() {
                @Override
                public Collection<HmBo> load(HmBo bo) {
                    if (bo instanceof HmUser) {
                        HmUser user = (HmUser) bo;
// cchen DONE
//                        if (user.getTableColumns() != null)
//                            user.getTableColumns().size();
//                        if (user.getTableSizes() != null)
//                            user.getTableSizes().size();
//                        if (user.getAutoRefreshs() != null)
//                            user.getAutoRefreshs().size();
                        if (user.getUserGroup().getInstancePermissions() != null)
                            user.getUserGroup().getInstancePermissions().size();
                        if (user.getUserGroup().getFeaturePermissions() != null)
                            user.getUserGroup().getFeaturePermissions().size();
                    }
                    return null;
                }
            });

            // fill table columns into hm_user
//            userContext.fillTableColumns();

            userContext.createTableViews();
            userContext.createTableSizeMappings();
        } catch (Exception e) {
            log.error("Error to get the user context ", e);
        }
    }

    /* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#search(com.ah.be.search.SearchParameter)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int search(SearchParameter searchParameter) {
		if(searchParameter == null) {
			return 0;
		}

		int foundCount = 0;
		String key = searchParameter.getKeyword();
		long t = System.currentTimeMillis();

//		if (StringUtils.isNumeric(key)) { // numeric
//			foundCount += search(this.numMap, key); // num
//			foundCount += search(this.numalphaMap, key); // num-alpha
//		} else if (StringUtils.isAlpha(key)) { // alphabetic
//			foundCount += search(this.alphaMap, key); // alpha
//			foundCount += search(this.numalphaMap, key); // num-alpha
//		} else if (StringUtils.isAlphanumeric(key)) { // num-alpha
//			foundCount += search(this.numalphaMap, key); // num-alpha
//		}
//
//		foundCount += search(this.otherMap, key); // num-alpha

		Iterator<?> it = this.resources.keySet().iterator();
		List<Target> foundTargets = new ArrayList<Target>(100);

		while(it.hasNext()) {
			String mapKey = (String)it.next();
			Set<PageElement> elements = this.resources.get(mapKey);
			SearchTable table = getTable(mapKey);

			if(!needSearchTable(table, searchParameter)) {
				continue ;
			}

			/*
			 * memory
			 */
			for(PageElement element : elements) {
				if(element.getValue().toLowerCase().indexOf(key) != -1) {
					extractTarget(foundTargets, table, element, searchParameter);
					foundCount++;

					if(foundTargets.size() > SEARCH_INSERT_STEP) {
						/*
						 * insert into database
						 */
						SearchUtil.saveTargets(foundTargets);
						foundTargets.clear();
					}
				}
			}

			/*
			 * disk file
			 */
			if(elementsFiles.contains(table.getKey())) {
				List<Object> objects = IndexUtil.read(table.getKey() + PAGE_ELEMENTS_FILE_SUFFIX);

				if(objects != null) {
					for(Object obj : objects) {
						Set<PageElement> fileElements = (Set<PageElement>)obj;

						for(PageElement element : fileElements) {
							if(element.getValue().toLowerCase().indexOf(key) != -1) {
								extractTarget(foundTargets, table, element, searchParameter);
								foundCount++;

								if(foundTargets.size() > SEARCH_INSERT_STEP) {
									/*
									 * insert into database
									 */
									SearchUtil.saveTargets(foundTargets);
									foundTargets.clear();
								}
							}
						}
					}
				}
			}
		}

		if(foundTargets.size() > 0) {
			/*
			 * insert into database
			 */
			SearchUtil.saveTargets(foundTargets);
		}

		log.debug("[HMSearch]PageIndex search:   " + foundCount + " items found!");
		log.debug("[HMSearch]it takes " + (System.currentTimeMillis() - t) + "ms to search page index.");
		return foundCount;
	}

	/* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#start()
	 */
	@Override
	public void start() {
		if(!needBuildIndex()) {
			return ;
		}

		initUserContext();

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduledTask = scheduler.schedule(new Runnable() {

			@Override
			public void run() {
				MgrUtil.setTimerName(this.getClass().getSimpleName());
				buildIndex();
			}

		}, PAGE_INDEX_DELAY, TimeUnit.MINUTES);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.search.SearchEngine#stop()
	 */
	@Override
	public void stop() {
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}

		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}

	/**
	 * getter of searchEngine
	 * @return the searchEngine
	 */
	public SearchEngineImpl getSearchEngine() {
		return searchEngine;
	}

	/**
	 * setter of searchEngine
	 * @param searchEngine the searchEngine to set
	 */
	public void setSearchEngine(SearchEngineImpl searchEngine) {
		this.searchEngine = searchEngine;
	}

	private void buildIndex() {
		long t = System.currentTimeMillis();

		List<SearchTable> tables = this.searchEngine.getPageIndexTables();

		for(SearchTable table : tables) {
//			log.info("building index for table: " + table.getFeature());
			indexTable(table);
		}

		savePageResources();
		setIndexFinishedFlag();

		log.info("[HM Search]Page Index finished. It totally took "
				+ (System.currentTimeMillis() - t)/1000 + "s to build the index.");
	}

	/**
	 * load resources into search engine
	 *
	 * @author Joseph Chen
	 */
	private void load() {
		/*
		 * load index files
		 */
//		loadIndexFiles();

		/*
		 * load maps to memory
		 */
//		loadMaps();

		/*
		 * load finished flag file
		 */
		loadFinishedFlag();

		/*
		 * load page resources
		 */
		loadPageResources();

		/*
		 * load elements files
		 */
		loadElementFiles();
	}

	private void loadIndexFiles() {
		log.info("loadIndexFiles", "begin loading index files.");

		File path = new File(SEARCH_RESOURCES_PATH);
		List<String> fileList = new ArrayList<String>();

		for(String file : path.list()) {
			if(file == null) {
				continue;
			}

			if(file.endsWith(".page")) {
				fileList.add(file);
			}
		}

		if(fileList.size() == 0) {
			log.info("loadIndexFiles", "no index file exists.");
			return ;
		}

		for(String file : fileList) {
			if(file == null || file.length() <6) {
				continue;
			}

			int i, j;
			try {
				i = Integer.valueOf(file.substring(1, file.indexOf("][")));
				j = Integer.valueOf(file.substring(file.indexOf("][") + 2, file.indexOf("].")));
				log.debug("loadIndexFile", "loaded index file: " + file);
				indexFiles[i][j] = SEARCH_RESOURCES_PATH + file;
			} catch(Exception e) {
				log.error("loadIndexFiles", "error in parsing file name", e);
			}
		}

		log.info("loadIndexFiles", "loading index files ends.");
	}

	/**
	 * load map in file which is not full.
	 * that means the file contains less index entries than the max limit
	 *
	 * @author Joseph Chen
	 */
	@SuppressWarnings("unchecked")
	private void loadMaps() {
		for(int row=0; row<indexFiles.length; row++) {
			int col=INDEX_FILE_ARRAY_SIZE - 1;
			/*
			 * from end to head, find one not null
			 */
			for(; col>=0; col--) {
				if(indexFiles[row][col] != null) {
					break;
				}
			}

			if(col == -1) { // no file
				continue;
			}

			/*
			 * load file
			 */
			List<Object> list = IndexUtil.read(indexFiles[row][col]);

			if(list == null) {
				continue ;
			}

			/*
			 * set maps
			 */
			for(Object obj : list) {
				if(obj == null) {
					continue;
				}

				Map<String, DocumentList> map = (Map<String, DocumentList>)obj;

				switch(row) {
				case 0:
					this.numMap = map;
					break;
				case 1:
					this.alphaMap = map;
					break;
				case 2:
					this.numalphaMap = map;
					break;
				case 3:
					this.otherMap = map;
					break;
				default:
					break;
				}
			}

			/*
			 * do not clear file
			 * because the map in memory will never be written back into the file
			 */
//			File file = new File(indexFiles[row][col]);
//			file.delete();
//			indexFiles[row][col] = null;
		}
	}

	private void putIntoMap(String key, IDocument element) {
		if(key == null) {
			return ;
		}
		key = key.trim();
		int fileRow;
		Map<String, DocumentList> map;

		if(StringUtils.isNumeric(key)){ // numeric
			fileRow = 0;
			map = this.numMap;
		} else if(StringUtils.isAlpha(key)) { // alphabetic
			fileRow = 1;
			map = this.alphaMap;
		} else if(StringUtils.isAlphanumeric(key)) { // alphabetic numeric
			fileRow = 2;
			map = this.numalphaMap;
		} else { // other
			fileRow = 3;
			map = this.otherMap;
		}

		putIntoMap(map, fileRow, key, element);
	}

	private void putIntoMap(Map<String, DocumentList> map, int fileRow, String key, IDocument element) {
		if(key == null) {
			return ;
		}

		key = key.toLowerCase();
		DocumentList oldIndex = map.get(key);

		if(oldIndex == null) { // key not in the map
			/*
			 * if map size exceeds the maximum, dump elements into file
			 */
			if(map.size() >= INDEX_MAX_MAP_SIZE) {
				int i=0;

				for(; i<indexFiles[fileRow].length; i++) {
					if(indexFiles[fileRow][i] == null) {
						break;
					}
				}

				indexFiles[fileRow][i] = SEARCH_RESOURCES_PATH + getMapFileName(fileRow, i);
				IndexUtil.save(map, indexFiles[fileRow][i]);
				map.clear();
				System.gc();
			}

			DocumentList list = new DocumentList(SearchEngine.INDEX_TYPE_PAGE);
			list.addDocument(element, fileRow, key);
			map.put(key, list);
		} else { // key already in the map
			/*
			 * key is found, add element to index
			 */
			oldIndex.addDocument(element, fileRow, key);
		}
	}

	private String getMapFileName(int i, int j) {
		StringBuffer buffer = new StringBuffer("[");

		buffer.append(i).append("][").append(j).append("]").append(".page");

		return buffer.toString();
	}

	public static boolean needBuildIndex() {
		return !hasIndexFinished;
	}

	public static boolean isLocalRequest(HttpServletRequest request) {
		String headerReferer = request.getHeader("referer");
		String headerUser = request.getHeader("user-agent");

		return (headerReferer != null && headerReferer.indexOf("aerohive") != -1)
				&& (headerUser != null && headerUser.indexOf("HMOL") != -1)
				&& request.getRemoteAddr().indexOf("127.0.0.1") != -1;
	}

	private void savePageResources() {
		dumpIntoFile();
		IndexUtil.save(this.resources, INDEX_PAGE_RESOURCES_FILE);
	}

	private void dumpIntoFile() {
		String filePath = AhDirTools.getHmRoot() +
							"resources" + File.separator +
							"search" + File.separator + "resources_dump.txt";

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(filePath, false));
			Iterator<?> it = this.resources.keySet().iterator();
			String line, tableId, type;
			Set<PageElement> elements;

			while(it.hasNext()) {
				tableId = (String)it.next();
				elements = this.resources.get(tableId);
				int flag = 0;

				for(PageElement element : elements) {
					line = "";
					type = element.getCategory() == PageDocument.CATEGORY_TABLE_HEADER ? "TableColumn" : "PageField";

					if(flag++ == 0) {
						SearchTable table = getTable(tableId);
						line += table.getAction() + "   " + table.getKey() + "\n";
					}

					line += "\t\t\t\t\t\t" + type + "\t\t\t" + element.getValue();
					writer.append(line);
					writer.newLine();
				}
			}
			writer.flush();
	        writer.close();
		} catch (IOException e) {
			log.error("Failed to read returned stream from web server into file: " + INDEX_TEMPORARY_RESULT_FILE, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadPageResources() {
		List<Object> list = IndexUtil.read(INDEX_PAGE_RESOURCES_FILE);

		if(list == null) {
			return ;
		}

		/*
		 * set maps
		 */
		for(Object obj : list) {
			if(obj == null) {
				continue;
			}

			this.resources = (Map<String, Set<PageElement>>)obj;
		}
	}

	private void loadFinishedFlag() {
		hasIndexFinished = new File(INDEX_FINISHED_FLAG_FILE).exists();
	}

	private void loadElementFiles() {
		elementsFiles = new ArrayList<String>();
		List<String> allFiles = null;

		try {
			allFiles = FileManager.getInstance()
				.getFileAndSubdirectoryNames(SEARCH_RESOURCES_PATH, (short)1, false);
		} catch (FileNotFoundException e) {
			log.error("Cannot get elements files from: " + SEARCH_RESOURCES_PATH, e);
		} catch (IllegalArgumentException e) {
			log.error("Cannot get elements files from: " + SEARCH_RESOURCES_PATH, e);
		} catch (BeNoPermissionException e) {
			log.error("Cannot get elements files from: " + SEARCH_RESOURCES_PATH, e);
		}

		if(allFiles == null) {
			return ;
		}

		for(String file : allFiles) {
			if(file == null) {
				continue;
			}

			if(file.endsWith(PAGE_ELEMENTS_FILE_SUFFIX)) {
				elementsFiles.add(file.substring(0, file.indexOf("PAGE_ELEMENTS_FILE_SUFFIX")));
			}
		}
	}

	private void setIndexFinishedFlag() {
		try {
			new File(INDEX_FINISHED_FLAG_FILE).createNewFile();
		} catch (IOException e) {
			log.error("Failed to create the finished flag file", e);
		}
	}

	private void indexTable(SearchTable table) {
		if(!needIndex(table)) {
			return ;
		}

		StringBuffer strUrl = new StringBuffer("https://127.0.0.1:"+NmsUtil.getWebServerRedirectPort()+"/hm/");
		strUrl.append(table.getAction()).append(".action");
		this.parsingTable = table;
		short parseType = getParseType(table);

		if(table.getUrlParameters() != null
				&& table.getUrlParameters().size() > 0) {
			strUrl.append("?")
				.append(this.searchEngine.spellURLParameter(table.getUrlParameters()));
		}

		URL requestUrl = null;

		try {
			requestUrl = new URL(strUrl.toString());
		} catch (MalformedURLException e) {
			log.error("URL <" + strUrl.toString() + "> is malformed.", e);
		}

		if(requestUrl != null) {
			if(sendRequest(requestUrl)) {
				parseResult(parseType);
			}
		}

		if(needIndexNewView(table)) {
			strUrl.append("?operation=new");

			try {
				requestUrl = new URL(strUrl.toString());
			} catch (MalformedURLException e) {
				log.error("URL <" + strUrl.toString() + "> is malformed.", e);
			}

			if(requestUrl != null) {
				if(sendRequest(requestUrl)) {
					parseResult(PAGE_TYPE_NORMAL);
				}
			}
		}
	}

	private boolean needIndex(SearchTable table) {
		String action = table.getAction();

		if("configurationMenu".equals(action)) {
			return false;
		}

		String key = table.getKey();

		if("macouiDictionary".equals(key)) {
			return false;
		}

		if(NmsUtil.isHMForOEM()) {
			if("mibFiles".equals(key)) {
				return false;
			}
		}

		return true;
	}

	private boolean needIndexNewView(SearchTable table) {
		if("null".equals(table.getBoClass())) {
			return false;
		}

		if("hiveAp".equals(table.getAction())
				&& !"null".equals(table.getBoClass())) {
			return false;
		}

		if("clientMonitor".equals(table.getAction())){
			return false;
		}

		if("idp".equals(table.getAction())
				|| "hiveApUpdateRts".equals(table.getAction())
				|| "clientSurvey".equals(table.getAction())) {
			return false;
		}

		return true;
	}

	private short getParseType(SearchTable table) {
		if("null".equals(table.getBoClass())) {
			if("events".equals(table.getAction())
					|| "alarms".equals(table.getAction())) {
				return PAGE_TYPE_LIST_VIEW;
			}

			return PAGE_TYPE_NORMAL;
		}

		return PAGE_TYPE_LIST_VIEW;
	}

	private boolean sendRequest(URL url) {
		if(sc == null) {
			try {
				getSSLContext();
			} catch (Exception e) {
				log.error("Failed to initialize an SSL context for HTTPS connection.", e);
				return false;
			}
		}

		HttpsURLConnection conn;

		try {
			conn = (HttpsURLConnection)   url.openConnection();
		} catch (IOException e) {
			log.error("Failed to open connection for URL(" + url.getPath() + ")", e);
			return false;
		}

		conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new HostnameVerify());
        addProperty(conn);

        try {
			conn.connect();
		} catch (IOException e) {
			log.error("Failed to connect web server for URL(" + url.getPath() + ")", e);
			return false;
		}

		InputStream is;

		try {
			is = conn.getInputStream();
		} catch (IOException e) {
			log.error("Failed to get the input stream of connection to URL(" + url.getPath() + ")", e);
			return false;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedWriter writer;
        String line;

		try {
			writer = new BufferedWriter(new FileWriter(INDEX_TEMPORARY_RESULT_FILE, false));

			while((line=br.readLine()) != null){
	            writer.append(line);
	            writer.newLine();
	        }

	        br.close();
	        is.close();
	        writer.flush();
	        writer.close();
	        conn.disconnect();
		} catch (IOException e) {
			log.error("Failed to read returned stream from web server into file: " + INDEX_TEMPORARY_RESULT_FILE, e);
			return false;
		}

        return true;
	}

	private void getSSLContext() throws Exception {
		if(sc != null) {
			return ;
		}

		sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new ClientTrustManager() }, new java.security.SecureRandom());
	}

	private void parseResult(short type) {
		Parser parser = null;

		try {
			parser = new Parser(INDEX_TEMPORARY_RESULT_FILE);
		} catch (ParserException e) {
			log.error("Cannot create a HMTL parser for " + INDEX_TEMPORARY_RESULT_FILE, e);
		}

		if(parser == null) {
			return ;
		}

		switch(type) {
		case PAGE_TYPE_NORMAL:
			parseNormalPage(parser);
			break;
		case PAGE_TYPE_LIST_VIEW:
			parseListView(parser);
			break;
		default:
			break;
		}
	}

	private void parseNormalPage(Parser parser) {
		NodeFilter formFilter = new NodeClassFilter(FormTag.class);
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { formFilter });
        NodeList nodeList;

		try {
			nodeList = parser.parse(lastFilter);

			for (int i = 0; i <= nodeList.size(); i++) {
	            if (nodeList.elementAt(i) instanceof FormTag) {
	            	FormTag form = (FormTag)nodeList.elementAt(i);

	            	for(NodeIterator iter = form.children(); iter.hasMoreNodes();){
	            		Node node = iter.nextNode();
		            	test(node);
		            }
	            }
	       }
		} catch (ParserException e) {
			log.error("Failed to parse returned page of feature: " + parsingTable.getFeature(), e);
		}
	}

	private void parseListView(Parser parser) {
		NodeVisitor visitor = new NodeVisitor() {
            public void visitTag(Tag tag) {
            	if(tag instanceof TableHeader) {
            		/*
            		 * <th><a href="/hm/hiveApUpdateRts.action?operation=sort&amp;orderBy=nodeId&amp;ascending=true"><b>Node ID</b></a></th>
            		 *
            		 * <th>Vendor</th>
            		 */
            		TableHeader header = (TableHeader)tag;

            		try {
						for(NodeIterator iter = header.children(); iter.hasMoreNodes();){
							Node node = iter.nextNode();

							if(node instanceof LinkTag) {
								if(node.getChildren() == null) {
									return ;
								}

								findTarget(node.getChildren().elementAt(1).toHtml(),
										PageDocument.CATEGORY_TABLE_HEADER);
							} else if(node instanceof TextNode) {
								String text = node.getText().trim();

								if(text.length() > 0) {
									findTarget(text, PageDocument.CATEGORY_TABLE_HEADER);
								}
							}
						}
					} catch (ParserException e) {
						log.error("Failed to parse list view of " + parsingTable.getFeature()
								+ ", HTML: " + header.toHtml(), e);
					}
            	}
            }
		};

		try {
			parser.visitAllNodesWith(visitor);
		} catch (ParserException e) {
			log.error("Failed to parse list view of " + this.parsingTable.getFeature(), e);
		}
	}

	private void findTarget(String resource, short type) {
		addResource(String.valueOf(parsingTable.getId()),
				new PageElement(resource, type));
//		putIntoMap(resource, new PageDocument(parsingTable.getAction(), type));
	}

	private void addProperty(URLConnection connection){
        connection.addRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/x-silverlight, */*");
        connection.setRequestProperty("Referer", "https://www.aerohive.com:8443/index.jsp");
        connection.setRequestProperty("Accept-Language", "zh-cn");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; HMOL 3.5r2; MSIE 6.0; Windows NT 5.1; SV1; Foxy/1; .NET CLR 2.0.50727;MEGAUPLOAD 1.0)");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
    }

	private void addResource(String key, PageElement element) {
		Set<PageElement> elements = this.resources.get(key);

		if(elements == null) { // action is not in the map
			Set<PageElement> newElements = new HashSet<PageElement>();
			newElements.add(element);
			this.resources.put(key, newElements);
		} else { // action is already in the map
			if(elements.size() >= PAGE_INDEX_MEMORY_SIZE) {
				/*
				 * dump elements into file
				 */
				dumpElements(elements);
				elements.add(element);
			} else {
				elements.add(element);
			}
		}
	}

	private void dumpElements(Set<PageElement> elements) {
		IndexUtil.save(elements, this.parsingTable.getKey() + PAGE_ELEMENTS_FILE_SUFFIX);
		elements.clear();
	}

	private void test(Node node) {
		if(node instanceof InputTag
				|| node instanceof StyleTag
				|| node instanceof ImageTag
				|| node instanceof RemarkNode) {
			return ;
		}

		if(node instanceof ScriptTag) {
			if(node.toHtml().contains("function")) {
			    return;
			} else if(node.toHtml().contains("insertFoldingLabelContext")) {
				extractFoldingLabel(node.toHtml());
			}

			return ;
		}

		String nodeText = node.getText();

		if(nodeText == null
				|| nodeText.trim().length() == 0) {
			return ;
		}

		nodeText = nodeText.trim();

//		if(isHiddenNode(nodeText)) {
//			return ;
//		}

		if(node instanceof TextNode) {
			if(isNeedless(nodeText)) {
				return ;
			}

			if(nodeText.length() > PAGE_TEXT_MAX_LENGTH) {
				for(int i = 0; i < nodeText.length()/PAGE_TEXT_MAX_LENGTH; i++) {
					if((i+1)*PAGE_TEXT_MAX_LENGTH >= nodeText.length()) {
						findTarget(nodeText.substring(i*PAGE_TEXT_MAX_LENGTH, nodeText.length())
								, PageDocument.CATEGORY_PAGE_ELEMENT);
						break;
					}

					findTarget(nodeText.substring(i*PAGE_TEXT_MAX_LENGTH, (i+1)*PAGE_TEXT_MAX_LENGTH)
							, PageDocument.CATEGORY_PAGE_ELEMENT);
				}
			} else {
				findTarget(nodeText, PageDocument.CATEGORY_PAGE_ELEMENT);
			}

			return ;
		}

		if(node.getChildren() == null
				|| node.getChildren().size() == 0) {
			return;
		}

		NodeList childList = node.getChildren();

		for(int i=0; i<childList.size(); i++) {
			Node childNode = childList.elementAt(i);

			test(childNode);
		}
	}

	private void extractFoldingLabel(String text) {
		String wanted = text.substring(text.indexOf("('")+2, text.indexOf("',"));
		findTarget(wanted, PageDocument.CATEGORY_PAGE_ELEMENT);
	}

	private boolean isNeedless(String text) {
		if(text.indexOf("Customize Table Columns") != -1
				|| text.indexOf("Available Columns") != -1
				|| text.indexOf("Selected Columns") != -1
				|| text.indexOf("Your request is being processed ...") != -1
				|| text.indexOf("No items were found") != -1
				|| text.indexOf("Contact Aerohive Sales or Support") != -1
				|| "&nbsp;".equals(text)) {

			return true;
		}

		return false;
	}

	private boolean isHiddenNode(String text) {
		if(text == null ||
				text.length() == 0) {
			return false;
		}

		if(text.contains("display")
				&& text.contains("none")) {
			return true;
		}

		return false;
	}

	private SearchTable getTable(String mapKey) {
		if(mapKey == null) {
			return null;
		}

		int tableId = Integer.parseInt(mapKey);
		List<SearchTable> tables = this.searchEngine.getPageIndexTables();

		for(SearchTable table : tables) {
			if(table.getId() == tableId) {
				return table;
			}
		}

		return null;
	}

	private void extractTarget(List<Target> buffer, SearchTable table, PageElement element,
			SearchParameter searchParameter) {
		Target target = null;

		switch(element.getCategory()) {
		case PageDocument.CATEGORY_TABLE_HEADER:
			target = getColumnTarget(table, element, searchParameter);
			break;
		case PageDocument.CATEGORY_PAGE_ELEMENT:
			target = getFieldTarget(table, element, searchParameter);
			break;
		default:
			break;
		}

		buffer.add(target);
	}

	private Target getColumnTarget(SearchTable table, PageElement element,
			SearchParameter searchParameter) {
		ColumnTarget target = new ColumnTarget(table.getAction(),
				table.getFeature(),
				table.getType(),
				this.searchEngine.spellURLParameter(table.getUrlParameters()),
				searchParameter.getUserContext().getUserName(),
				searchParameter.getUserContext().getDomain().getId(),
				null);
		target.setColumn(element.getValue());
		return target;
	}

	private Target getFieldTarget(SearchTable table, PageElement element,
			SearchParameter searchParameter) {
		FieldTarget target = new FieldTarget(table.getAction(),
				table.getFeature(),
				table.getType(),
				this.searchEngine.spellURLParameter(table.getUrlParameters()),
				searchParameter.getUserContext().getUserName(),
				searchParameter.getUserContext().getDomain().getId(),
				null);
		target.setField(element.getValue());
		return target;
	}

	private boolean needSearchTable(SearchTable table, SearchParameter param) {
	    if(null == table) {
	        return false;
	    }
		if(!param.isFullMode()) {
			if(table.isFullModeOnly()) {
				return false;
			}
		}

		if(!param.getUserContext().getDomain().isHomeDomain()
				|| param.getUserContext().getSwitchDomain() != null) {
			if (table.isHomeOnly()) {
				return false;
			}
		}

		if(!hasPermission(table, param.getUserContext())) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_ADMIN
				&& !param.isAdmin()) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_CLIENT
				&& !param.isClient()) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_CONFIGURATION
				&& !param.isConfiguration()) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_FAULT
				&& !param.isFault()) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_HIVEAP
				&& !param.isHiveAP()) {
			return false;
		}

		if(table.getType() == SearchParameter.TYPE_TOOL
				&& !param.isTool()) {
			return false;
		}

		return true;
	}

	private boolean hasPermission(SearchTable table, HmUser userContext) {
		 /*
		  * Teacher View enabled or not
		  */
		boolean tvEnabled = NmsUtil.isTeacherViewEnabled(userContext);

		if(!tvEnabled) {
			if(table.getKey() != null
					&& table.getKey().startsWith("tv")) {
				return false;
			}
		}

		Map<String, HmPermission> permissions = userContext
													.getUserGroup()
													.getFeaturePermissions();

		if(permissions.get(table.getKey()) == null) {
			return false;
		}

		return true;
	}

    public HmUser getUserContext() {
        if(null == userContext) {
            initUserContext();
        }
        return userContext;
    }

}