/**
 * @filename			ResultEntry.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

public class ResultEntry {
	private String	url;

	private String	description;

	private Target	target;

	private String	searchKey;

	public ResultEntry() {
		super();
	}

	public ResultEntry(String url, String description) {
		super();
		this.url = url;
		this.description = description;
	}

	/**
	 * getter of url
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * setter of url
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * getter of description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setter of description
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return "[Result Entry] URL: " + this.url + ", description: " + this.description;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public String getDetail() {
		StringBuffer description = new StringBuffer();

		// // feature name
		// description.append("Feature: " + target.getFeature());
		//			
		String column = null;
		String value = null;

		// colomn name
		if (target instanceof ColumnTarget) {
			ColumnTarget colTarget = (ColumnTarget) target;

			column = "Table Column";
			value = colTarget.getColumn();
		}

		// field name
		if (target instanceof FieldTarget) {
			FieldTarget fieldTarget = (FieldTarget) target;

			column = "Page Field";
			value = fieldTarget.getField();
		}
		
		// entity
		if (target instanceof EntityTarget) {
			EntityTarget entityTarget = (EntityTarget) target;

			column = entityTarget.getFieldName();
			value = entityTarget.getFieldValue();
		}

		if (value == null) {
			column = "Feature";
			value = target.getFeature();
		}

		if (searchKey.contains("[") || searchKey.contains("]")) {
			value = value.toLowerCase().replace(
					searchKey.toLowerCase(),
					"<font style=\"color:#FFC727\"><b><i>" + searchKey.toLowerCase()
							+ "</b></i></font>");
		} else {
			String searchKeyReg = getSearchKeyReg(searchKey);
			value = value.replaceAll(searchKeyReg, "<font style=\"color:#FFC727\"><b><i>"
					+ searchKey + "</b></i></font>");
		}

		description.append("<font style=\"color:#AAAAAA\">");
		description.append("[");
		description.append(column + ": ");
		description.append("<font style=\"color:#999999\">");
		description.append(value);
		description.append("</font>");
		description.append("]");
		description.append("</font>");

		return description.toString();
	}

	private String getSearchKeyReg(String searchkey) {
		char[] lowerArray = searchkey.toLowerCase().toCharArray();
		char[] upperArray = searchkey.toUpperCase().toCharArray();
		StringBuffer regex = new StringBuffer();
		for (int i = 0; i < lowerArray.length; i++) {
			char c = lowerArray[i];
			if (isLetter(c)) {
				regex.append("(" + String.valueOf(c) + "|" + String.valueOf(upperArray[i]) + ")");
			} else {
				regex.append("[" + (String.valueOf(c)) + "]");
			}
		}

		return regex.toString();
	}

	private boolean isLetter(char c) {
		if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
			return true;
		}

		return false;
	}

	public String getTitle() {
		switch (target.getType()) {
		case SearchParameter.TYPE_CONFIGURATION:
			return "Config";

		case SearchParameter.TYPE_HIVEAP:
			return "HiveAP";

		case SearchParameter.TYPE_CLIENT:
			return "Client";

		case SearchParameter.TYPE_ADMIN:
			return "Admin";

		case SearchParameter.TYPE_FAULT:
			return "Fault";

		default:
			return "Others";
		}
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
}
