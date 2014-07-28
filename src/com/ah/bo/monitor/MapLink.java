package com.ah.bo.monitor;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Parent;

/*
 * @author Chris Scheers
 */

@Embeddable
@SuppressWarnings("serial")
public class MapLink implements Serializable {
	@Parent
	private MapContainerNode parentMap;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FROM_NODE_ID", nullable = false)
	private MapLeafNode fromNode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TO_NODE_ID", nullable = false)
	private MapLeafNode toNode;

	private int fromRssi, toRssi;

	public int getFromRssi() {
		return fromRssi;
	}

	public void setFromRssi(int fromRssi) {
		this.fromRssi = fromRssi;
	}

	public int getToRssi() {
		return toRssi;
	}

	public void setToRssi(int toRssi) {
		this.toRssi = toRssi;
	}

	public MapLeafNode getFromNode() {
		return fromNode;
	}

	public void setFromNode(MapLeafNode fromNode) {
		this.fromNode = fromNode;
	}

	public MapLeafNode getToNode() {
		return toNode;
	}

	public void setToNode(MapLeafNode toNode) {
		this.toNode = toNode;
	}

	public MapContainerNode getParentMap() {
		return parentMap;
	}

	public void setParentMap(MapContainerNode parentMap) {
		this.parentMap = parentMap;
	}

	public String getKey() {
		return fromNode.getId() + "|" + toNode.getId();
	}

	public String getReverseKey() {
		return toNode.getId() + "|" + fromNode.getId();
	}
}
