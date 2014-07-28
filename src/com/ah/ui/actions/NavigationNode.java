package com.ah.ui.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * @author Chris Scheers
 */
public class NavigationNode implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key, action, description, shortDescription, treeImage,
			styleClass, rowDisplay, tdStyleClass;

	private int rowIndex, childCount, indent = 0;

	private boolean expanded = false, collapsible = true, headerNode = false,
			summary = false, selectedTree = false;

	private List<NavigationNode> childNodes;

	private NavigationNode parentNode;

	public NavigationNode(String key) {
		this.key = key;
		childNodes = new ArrayList<NavigationNode>();
	}

	public String getKey() {
		return key;
	}

	public List<NavigationNode> getChildNodes() {
		return childNodes;
	}

	public void addChildNode(NavigationNode node) {
		childNodes.add(node);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public NavigationNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(NavigationNode parentNode) {
		this.parentNode = parentNode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTreeImage() {
		return treeImage;
	}

	public void setTreeImage(String treeImage) {
		this.treeImage = treeImage;
	}

	public boolean isHeaderNode() {
		return headerNode;
	}

	public void setHeaderNode(boolean headerNode) {
		this.headerNode = headerNode;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
		tdStyleClass = "";
	}

	public String getTdStyleClass() {
		return tdStyleClass;
	}

	public void setTdStyleClass(String tdStyleClass) {
		this.tdStyleClass = tdStyleClass;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public boolean isSummary() {
		return summary;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	public String getRowDisplay() {
		return rowDisplay;
	}

	public void setRowDisplay(String rowDisplay) {
		this.rowDisplay = rowDisplay;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isSelectedTree() {
		return selectedTree;
	}

	public void setSelectedTree(boolean selectedTree) {
		this.selectedTree = selectedTree;
	}

	public void removeChildNode(NavigationNode node) {
		childNodes.remove(node);
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof NavigationNode)) {
			return false;
		}

		final NavigationNode node = (NavigationNode) o;

		return key != null ? key.equalsIgnoreCase(node.key) : node.key == null;
	}
}