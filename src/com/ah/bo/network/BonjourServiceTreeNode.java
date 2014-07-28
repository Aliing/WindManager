package com.ah.bo.network;

import java.util.ArrayList;
import java.util.List;

public class BonjourServiceTreeNode {

	private Long serviceId;
	private Long categoryId;
	private boolean isCategory;
	private int categoryType;
	private String label;	
	private String serviceType;
	private int parentId;
	private int nodeId;
	private boolean isRoot = false;
	private int nodeCount;
	private boolean isCustom;
	private List<BonjourServiceTreeNode> TreeNodes = new ArrayList<BonjourServiceTreeNode>();

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}

	public int getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(int categoryType) {
		this.categoryType = categoryType;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public List<BonjourServiceTreeNode> getTreeNodes() {
		return TreeNodes;
	}

	public void setTreeNodes(List<BonjourServiceTreeNode> treeNodes) {
		TreeNodes = treeNodes;
	}
	
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public boolean isCustom() {
		return isCustom;
	}

	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}
	
	
}
