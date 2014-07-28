/**
 * @filename			SearchTable.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.util.ArrayList;
import java.util.List;

public class SearchTable {
	private int id;
	
	private String action;
	
	private int type;
	
	private String boClass;
	
	private String key;
	
	private String feature;
	
	private boolean isMac;
	
	private boolean isIp;
	
	private boolean isClient;
	
	private boolean isFullModeOnly;
	
	private boolean isHomeOnly;
	
	private String boolFor;
	
	private List<FilterParam> filters;
	
	private List<NameValuePair> urlParameters;
	
	public SearchTable() {
		this.setFilters(new ArrayList<FilterParam>());
		this.setUrlParameters(new ArrayList<NameValuePair>());		
	}

	/**
	 * getter of id
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * setter of id
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getter of action
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * setter of action
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * getter of type
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * setter of type
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * getter of boClass
	 * @return the boClass
	 */
	public String getBoClass() {
		return boClass;
	}

	/**
	 * setter of boClass
	 * @param boClass the boClass to set
	 */
	public void setBoClass(String boClass) {
		this.boClass = boClass;
	}

	/**
	 * getter of key
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * setter of key
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * getter of feature
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * setter of feature
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}

	/**
	 * getter of isMac
	 * @return the isMac
	 */
	public boolean isMac() {
		return isMac;
	}

	/**
	 * setter of isMac
	 * @param isMac the isMac to set
	 */
	public void setMac(boolean isMac) {
		this.isMac = isMac;
	}

	/**
	 * getter of boolFor
	 * @return the boolFor
	 */
	public String getBoolFor() {
		return boolFor;
	}

	/**
	 * setter of boolFor
	 * @param boolFor the boolFor to set
	 */
	public void setBoolFor(String boolFor) {
		this.boolFor = boolFor;
	}

	/**
	 * getter of filters
	 * @return the filters
	 */
	public List<FilterParam> getFilters() {
		return filters;
	}

	/**
	 * setter of filters
	 * @param filters the filters to set
	 */
	public void setFilters(List<FilterParam> filters) {
		this.filters = filters;
	}

	/**
	 * getter of urlParameters
	 * @return the urlParameters
	 */
	public List<NameValuePair> getUrlParameters() {
		return urlParameters;
	}

	/**
	 * setter of urlParameters
	 * @param urlParameters the urlParameters to set
	 */
	public void setUrlParameters(List<NameValuePair> urlParameters) {
		this.urlParameters = urlParameters;
	}
	
	public void addFilter(String name, String type, String value, String operator) {
		this.getFilters().add(new FilterParam(name, type, value, operator));
	}
	
	public void addUrlParameter(String name, String value) {
		this.getUrlParameters().add(new NameValuePair(name, value));
	}

	/**
	 * getter of isIp
	 * @return the isIp
	 */
	public boolean isIp() {
		return isIp;
	}

	/**
	 * setter of isIp
	 * @param isIp the isIp to set
	 */
	public void setIp(boolean isIp) {
		this.isIp = isIp;
	}

	/**
	 * getter of isClient
	 * @return the isClient
	 */
	public boolean isClient() {
		return isClient;
	}

	/**
	 * setter of isClient
	 * @param isClient the isClient to set
	 */
	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	/**
	 * getter of isFullModeOnly
	 * @return the isFullModeOnly
	 */
	public boolean isFullModeOnly() {
		return isFullModeOnly;
	}

	/**
	 * setter of isFullModeOnly
	 * @param isFullModeOnly the isFullModeOnly to set
	 */
	public void setFullModeOnly(boolean isFullModeOnly) {
		this.isFullModeOnly = isFullModeOnly;
	}

	/**
	 * getter of isHomeOnly
	 * @return the isHomeOnly
	 */
	public boolean isHomeOnly() {
		return isHomeOnly;
	}

	/**
	 * setter of isHomeOnly
	 * @param isHomeOnly the isHomeOnly to set
	 */
	public void setHomeOnly(boolean isHomeOnly) {
		this.isHomeOnly = isHomeOnly;
	}
}
