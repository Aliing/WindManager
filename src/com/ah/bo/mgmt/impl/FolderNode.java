package com.ah.bo.mgmt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.Folder;

public class FolderNode implements Folder {

	public FolderNode(Long id) {
		this.id = id;
		this.parentId = null;
		folderIds = new ArrayList<Long>();
		deviceNodes = new HashMap<String, DeviceNode>();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Long getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public List<Long> getFolderIds() {
		return folderIds;
	}

	@Override
	public synchronized void setFolderIds(List<Long> folderIds) {
		this.folderIds = folderIds;
	}

	@Override
	public synchronized void addDeviceNode(DeviceNode deviceNode) {
		deviceNodes.put(deviceNode.id, deviceNode);
	}

	@Override
	public DeviceNode getDeviceNode(String id) {
		return deviceNodes.get(id);
	}

	@Override
	public synchronized DeviceNode removeDeviceNode(String id) {
		return deviceNodes.remove(id);
	}

	@Override
	public Collection<DeviceNode> getDeviceNodes() {
		return deviceNodes.values();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getManagedUpApCount() {
		return managedUpApCount;
	}

	@Override
	public void setManagedUpApCount(int managedUpApCount) {
		this.managedUpApCount = managedUpApCount;
	}

	@Override
	public int getManagedDownApCount() {
		return managedDownApCount;
	}

	@Override
	public void setManagedDownApCount(int managedDownApCount) {
		this.managedDownApCount = managedDownApCount;
	}

	@Override
	public int getNewUpApCount() {
		return newUpApCount;
	}

	@Override
	public void setNewUpApCount(int newUpApCount) {
		this.newUpApCount = newUpApCount;
	}

	@Override
	public int getNewDownApCount() {
		return newDownApCount;
	}

	@Override
	public void setNewDownApCount(int newDownApCount) {
		this.newDownApCount = newDownApCount;
	}

	@Override
	public int getClientCount() {
		return clientCount;
	}

	@Override
	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private Long id, parentId;
	private List<Long> folderIds;
	private Map<String, DeviceNode> deviceNodes;
	private String name;

	private int managedUpApCount, managedDownApCount, newUpApCount,
			newDownApCount, clientCount;
	private boolean selected;
}
