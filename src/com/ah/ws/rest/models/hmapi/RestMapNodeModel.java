package com.ah.ws.rest.models.hmapi;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.ah.bo.monitor.MapContainerNode;

@XmlRootElement(name = "mapNode")
public class RestMapNodeModel {
	private Long id;
	private Long parentId;
	private String mapName;
	private String mapType;
	private List<RestMapNodeModel> children;

	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapType() {
		return mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public List<RestMapNodeModel> getChildren() {
		return children;
	}

	public void setChildren(List<RestMapNodeModel> children) {
		this.children = children;
	}

	@XmlTransient
	public static String getMapTypeName(short type) {
		String typeName = "";
		if (type == MapContainerNode.MAP_TYPE_BUILDING) {
			typeName = "Building";
		} else if (type == MapContainerNode.MAP_TYPE_FLOOR) {
			typeName = "Floor";
		} else {
			typeName = "Folder";
		}
		return typeName;
	}
}
