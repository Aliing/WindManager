package com.ah.ui.tags;

import java.util.Collection;

public class OptionsTransfer {
	private String leftTitle, rightTitle;

	private Collection leftOptions, rightOptions;

	private String listKey, listValue, name, width, size, actionPostfix;

	// for new/edit/remove simple objects in easy mode
	private String type, subType, callbackFn;
	
	private Long domainId;

	private int limitCount = 0;

	private boolean sort = false, withSort = false;
	
	private boolean operationEnable = true;
	
	

	public Collection getLeftOptions() {
		return leftOptions;
	}

	public Collection getRightOptions() {
		return rightOptions;
	}

	public String getName() {
		return name;
	}

	public String getListKey() {
		return listKey;
	}

	public String getListValue() {
		return listValue;
	}

	public String getLeftTitle() {
		return leftTitle;
	}

	public String getRightTitle() {
		return rightTitle;
	}

	public int getLimitCount() {
		return limitCount;
	}

	public String getWidth() {
		return width;
	}

	public boolean isSort() {
		return sort;
	}

	public String getActionPostfix() {
		return actionPostfix;
	}

	public String getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}

	public String getCallbackFn() {
		return callbackFn;
	}

	public Long getDomainId() {
		return domainId;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, int limitCount) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.limitCount = limitCount;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, int limitCount, String actionPostfix) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.limitCount = limitCount;
		this.actionPostfix = actionPostfix;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, int limitCount,
			String actionPostfix, String type, String subType,
			String callbackFn, Long domainId) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.limitCount = limitCount;
		this.actionPostfix = actionPostfix;
		this.type = type;
		this.subType = subType;
		this.callbackFn = callbackFn;
		this.domainId = domainId;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
	}
	
	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, String actionPostfix) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.actionPostfix = actionPostfix;
	}
	
	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, String actionPostfix, boolean withSort) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.actionPostfix = actionPostfix;
		this.withSort = withSort;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, String actionPostfix, String type, String subType,
			String callbackFn, Long domainId) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.actionPostfix = actionPostfix;
		this.type = type;
		this.subType = subType;
		this.callbackFn = callbackFn;
		this.domainId = domainId;
	}
	
	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, String actionPostfix, String type, String subType,
			String callbackFn, Long domainId,String width,boolean operationEnable) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.actionPostfix = actionPostfix;
		this.type = type;
		this.subType = subType;
		this.callbackFn = callbackFn;
		this.domainId = domainId;
		this.operationEnable = operationEnable;
		this.width = width;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, int limitCount, String width,
			String size, boolean sort) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.limitCount = limitCount;
		this.width = width;
		this.size = size;
		this.sort = sort;
	}

	public OptionsTransfer(String leftTitle, String rightTitle,
			Collection leftOptions, Collection rightOptions, String listKey,
			String listValue, String name, int limitCount, String width,
			String size, boolean sort, String actionPostfix) {
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.leftOptions = leftOptions;
		this.rightOptions = rightOptions;
		this.listKey = listKey;
		this.listValue = listValue;
		this.name = name;
		this.limitCount = limitCount;
		this.width = width;
		this.size = size;
		this.sort = sort;
		this.actionPostfix = actionPostfix;
	}

	public boolean getWithSort() {
		return withSort;
	}

	public void setWithSort(boolean withSort) {
		this.withSort = withSort;
	}

	public boolean isOperationEnable() {
		return operationEnable;
	}

	public void setOperationEnable(boolean operationEnable) {
		this.operationEnable = operationEnable;
	}
	
	public String getOperateDisabled(){
		return this.isOperationEnable() ? "" : "disabled";
	}

}
