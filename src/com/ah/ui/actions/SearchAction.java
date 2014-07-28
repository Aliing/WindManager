package com.ah.ui.actions;

import java.util.List;

import org.json.JSONObject;

import com.ah.be.search.IndexUtil;
import com.ah.be.search.ResultEntry;
import com.ah.be.search.SearchParameter;
import com.ah.be.search.SearchResultSet;
import com.ah.be.search.SearchUtil;
import com.ah.be.search.Target;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		SearchAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-3-31 02:28:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
@SuppressWarnings("serial")
public class SearchAction extends BaseAction {

	private static final Tracer	log	= new Tracer(SearchAction.class.getSimpleName());

	private boolean				includeConfig;

	private boolean				includeClient;

	private boolean				includeHiveAP;

	private boolean				includeFault;

	private boolean				includeAdmin;

	private boolean				includeTool;

	private String				searchGotoPage;

	private String				searchKey;

	private int					searchPageSize;

	private String				topoURL;

	public String execute() throws Exception {
		try {
			if ("hmSearch".equals(operation)) {
				jsonObject = new JSONObject();
				if(SearchUtil.isExceedPermitSearchAmount()) {
					jsonObject.put("errMsg", MgrUtil.getUserMessage("warn.global.search.concurrent.num.exceed"));
				} else {
					log.info("hmSearch", "Search Key is " + searchKey);
					
					int searchResultCount = hmSearch();
					
					jsonObject.put("success", searchResultCount > 0);
					if (searchResultCount > 0) {
						jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());
					}
				}

				return "json";
			} else if ("configSearch".equals(operation)) {
				jsonObject = new JSONObject();
				// only search configuration
				if(SearchUtil.isExceedPermitSearchAmount()) {
					jsonObject.put("errMsg", MgrUtil.getUserMessage("warn.global.search.concurrent.num.exceed"));
				} else {
					log.info("configSearch", "Search Key is " + searchKey);
					includeConfig = true;
					
					String rawSearchKey = searchKey.trim();
					searchKey = searchKey.trim().toLowerCase();
					
					int searchResultCount = doSearch(rawSearchKey, false, false);
					
					jsonObject.put("success", searchResultCount > 0);
					if (searchResultCount > 0) {
						jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());
					}
				}
				
				return "json";
			} else if ("closePanel".equals(operation)) {

				clearSearchResult();

				jsonObject = new JSONObject();
				jsonObject.put("success", true);

				return "json";
			} else if ("topoSearch".equals(operation)) {

				log.info("topoSearch", "Search Key is " + searchKey);

				boolean isSucc = false;
				int resultCount = topoSearch();
				if (resultCount > 0) {
					isSucc = prepareTopoURL();
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				jsonObject.put("url", topoURL);

				return "json";

			} else if (Navigation.OPERATION_PREVIOUS_PAGE.equals(operation)) {

			    if(null != getSearchResult()) {
			        getSearchResult().previousPage();
			        
			        jsonObject = new JSONObject();
			        jsonObject.put("success", true);
			        jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());
			        
			        removeLstTitle();
			    }

				return "json";

			} else if (Navigation.OPERATION_NEXT_PAGE.equals(operation)) {

			    if(null != getSearchResult()) {
			        getSearchResult().nextPage();
			        
			        jsonObject = new JSONObject();
			        jsonObject.put("success", true);
			        jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());
			        
			        removeLstTitle();
			    }

				return "json";

			} else if (Navigation.OPERATION_GOTO_PAGE.equals(operation)) {

				int index;
				try {
					index = Integer.parseInt(searchGotoPage);
				} catch (NumberFormatException e) {
					index = 1;
				}
				getSearchResult().gotoPage(index);

				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());

				removeLstTitle();

				return "json";

			} else if ("resizePage".equals(operation)) {

				getSearchResult().resizePage(searchPageSize);

				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put("url", getSearchResult().getPageResult().get(0).getUrl());

				removeLstTitle();

				return "json";
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}

	private static final String	KEYWORD_CONFIG	= "configuration=";

	private static final String	KEYWORD_AP		= "devices=";

	private static final String	KEYWORD_CLIENT	= "clients=";

	private static final String	KEYWORD_FAULT	= "events & alarms=";

	private static final String	KEYWORD_ADMIN	= "administration=";

	private static final String	KEYWORD_TOOL	= "tools=";

	private static final String	KEYWORD_IP		= "ip=";

	private static final String	KEYWORD_MAC		= "mac=";

	private int hmSearch() {

		String rawSearchKey = searchKey.trim();
		searchKey = searchKey.trim().toLowerCase();

		if (searchKey.indexOf(KEYWORD_CONFIG) >= 0) {
			includeConfig = true;
			includeTool = includeHiveAP = includeClient = includeFault = includeAdmin = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_CONFIG)
					+ KEYWORD_CONFIG.length());
		}

		if (searchKey.indexOf(KEYWORD_AP) >= 0) {
			includeHiveAP = true;
			includeTool = includeConfig = includeClient = includeFault = includeAdmin = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_AP) + KEYWORD_AP.length());
		}

		if (searchKey.indexOf(KEYWORD_CLIENT) >= 0) {
			includeClient = true;
			includeTool = includeConfig = includeHiveAP = includeFault = includeAdmin = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_CLIENT)
					+ KEYWORD_CLIENT.length());
		}

		if (searchKey.indexOf(KEYWORD_FAULT) >= 0) {
			includeFault = true;
			includeTool = includeConfig = includeHiveAP = includeClient = includeAdmin = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_FAULT)
					+ KEYWORD_FAULT.length());
		}

		if (searchKey.indexOf(KEYWORD_ADMIN) >= 0) {
			includeAdmin = true;
			includeTool = includeConfig = includeHiveAP = includeClient = includeFault = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_ADMIN)
					+ KEYWORD_ADMIN.length());
		}

		if (searchKey.indexOf(KEYWORD_TOOL) >= 0) {
			includeTool = true;
			includeAdmin = includeConfig = includeHiveAP = includeClient = includeFault = false;
			searchKey = searchKey
					.substring(searchKey.indexOf(KEYWORD_TOOL) + KEYWORD_TOOL.length());
		}

		boolean isIP = false;
		boolean isMac = false;
		if (searchKey.indexOf(KEYWORD_IP) >= 0) {
			isIP = true;
			includeTool = includeConfig = includeHiveAP = includeClient = includeAdmin = includeFault = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_IP) + KEYWORD_IP.length());
		}

		if (searchKey.indexOf(KEYWORD_MAC) >= 0) {
			isMac = true;
			includeTool = includeConfig = includeHiveAP = includeClient = includeAdmin = includeFault = false;
			searchKey = searchKey.substring(searchKey.indexOf(KEYWORD_MAC) + KEYWORD_MAC.length());
			searchKey = searchKey.replaceAll(":", "");
			searchKey = searchKey.replaceAll("-", "");
		}

		int searchCount = doSearch(rawSearchKey, isIP, isMac);

		return searchCount;
	}

	/**
	 * @author Yunzhi Lin
	 * - Time: Oct 14, 2011 3:49:50 PM
	 * @param rawSearchKey - keyword
	 * @param isIP  - is search IP address
	 * @param isMac - is search MAC
	 * @return the search result total sum
	 */
	private int doSearch(String rawSearchKey, boolean isIP, boolean isMac) {
		// remove space after filter key word
		searchKey = searchKey.trim();

		SearchParameter searchParameter = new SearchParameter();
		searchParameter.setAdmin(includeAdmin);
		searchParameter.setClient(includeClient);
		searchParameter.setConfiguration(includeConfig);
		searchParameter.setUserContext(getUserContext());
		searchParameter.setFault(includeFault);
		searchParameter.setHiveAP(includeHiveAP);
		searchParameter.setTool(includeTool);
		searchParameter.setIp(isIP);
		searchParameter.setKeyword(searchKey);
		searchParameter.setMac(isMac);
		searchParameter.setFullMode(isFullMode());
		log.debug(">>>>>>>>>>>>>>>>>>>START search user:"+searchParameter.getUserContext().getUserName()
				+ ", thread name:"+Thread.currentThread().getName());
		int searchCount = 0;
        try {
            searchCount = SearchUtil.search(searchParameter);
        } catch (Exception e) {
            log.error("Error when search the keyword = " + searchKey, e);
        }
		log.debug(">>>>>>>>>>>>>>>>>>>END search user:"+searchParameter.getUserContext().getUserName()
				+ ", thread name:"+Thread.currentThread().getName());
		if (searchCount > 0) {
			SearchResultSet searchResult = new SearchResultSet();
			searchResult.setTotalCount(searchCount);
			searchResult.setSearchKey(searchKey);
			searchResult.setSearchKeyShow(rawSearchKey);
			searchResult.setAdmin(includeAdmin);
			searchResult.setConfig(includeConfig);
			searchResult.setHiveap(includeHiveAP);
			searchResult.setClient(includeClient);
			searchResult.setFault(includeFault);
			searchResult.setTool(includeTool);
			searchResult.setUserContext(getUserContext());
			searchResult.init();

			setSearchResult(searchResult);
		}
		return searchCount;
	}

	private int topoSearch() {
		String rawSearchKey = searchKey;
		searchKey = searchKey.trim().toLowerCase();

		SearchParameter searchParameter = new SearchParameter();
		searchParameter.setKeyword(searchKey);
		searchParameter.setUserContext(getUserContext());
		searchParameter.setTopo(true);

		int searchCount = SearchUtil.search(searchParameter);

		if (searchCount > 0) {
			SearchResultSet searchResult = new SearchResultSet();
			searchResult.setSearchKey(searchKey);
			searchResult.setSearchKeyShow(rawSearchKey);

			setSearchResult(searchResult);
		}

		return searchCount;
	}

	private boolean prepareTopoURL() {
		FilterParams topoFilterParams = new FilterParams(
				"userName = :s1 and userDomainId = :s2 and type = :s3", new Object[] {
						userContext.getUserName(), userContext.getDomain().getId(),
						SearchParameter.TYPE_TOPO });
		List<Target> boList = QueryUtil.executeQuery(Target.class, null, topoFilterParams);
		if (boList.isEmpty()) {
			return false;
		}

		boList = boList.subList(0, 1);
		List<ResultEntry> resultList = IndexUtil.convertResult(boList, searchKey);
		topoURL = resultList.get(0).getUrl();
		return true;
	}

	public boolean isIncludeClient() {
		return includeClient;
	}

	public void setIncludeClient(boolean includeClient) {
		this.includeClient = includeClient;
	}

	public boolean isIncludeConfig() {
		return includeConfig;
	}

	public void setIncludeConfig(boolean includeConfig) {
		this.includeConfig = includeConfig;
	}

	public boolean isIncludeHiveAP() {
		return includeHiveAP;
	}

	public void setIncludeHiveAP(boolean includeHiveAP) {
		this.includeHiveAP = includeHiveAP;
	}

	public String getSearchGotoPage() {
		return searchGotoPage;
	}

	public void setSearchGotoPage(String searchGotoPage) {
		this.searchGotoPage = searchGotoPage;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public boolean isIncludeFault() {
		return includeFault;
	}

	public void setIncludeFault(boolean includeFault) {
		this.includeFault = includeFault;
	}

	public void setSearchPageSize(int searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public int getSearchPageSize() {
		return searchPageSize;
	}

	public boolean isIncludeAdmin() {
		return includeAdmin;
	}

	public void setIncludeAdmin(boolean includeAdmin) {
		this.includeAdmin = includeAdmin;
	}

	public String getTopoURL() {
		return topoURL;
	}

	public void setTopoURL(String topoURL) {
		this.topoURL = topoURL;
	}

	public boolean isIncludeTool() {
		return includeTool;
	}

	public void setIncludeTool(boolean includeTools) {
		this.includeTool = includeTools;
	}

}