package com.ah.bo.useraccess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long serverId;
	private String label;	// RDN
	private String dn;		// parentDn + RDN
	private String domain;	// for AD's multi-domain
	private String parentDn;
	private int parentId;
	private int nodeId;
	private boolean dynamicLoadComplete;
	private boolean isRoot;
	private int nodeCount;
	private List<TreeNode> TreeNodes = new ArrayList<>();

	public boolean isDynamicLoadComplete() {
		return dynamicLoadComplete;
	}

	public void setDynamicLoadComplete(boolean dynamicLoadComplete) {
		this.dynamicLoadComplete = dynamicLoadComplete;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
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

	public String getParentDn() {
		return parentDn;
	}

	public void setParentDn(String parentDn) {
		this.parentDn = parentDn;
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

	public List<TreeNode> getTreeNodes() {
		return TreeNodes;
	}

	public void setTreeNodes(List<TreeNode> treeNodes) {
		TreeNodes = treeNodes;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}