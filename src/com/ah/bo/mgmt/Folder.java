package com.ah.bo.mgmt;

import java.util.Collection;
import java.util.List;

import com.ah.bo.mgmt.impl.DeviceNode;

public interface Folder {

	public Long getId();

	public String getName();

	public void setName(String name);

	public Long getParentId();

	public void setParentId(Long parentId);

	public List<Long> getFolderIds();

	public void setFolderIds(List<Long> folderIds);

	public void addDeviceNode(DeviceNode deviceNode);

	public Collection<DeviceNode> getDeviceNodes();

	public DeviceNode getDeviceNode(String id);

	public DeviceNode removeDeviceNode(String id);

	public int getManagedUpApCount();

	public void setManagedUpApCount(int managedUpApCount);

	public int getManagedDownApCount();

	public void setManagedDownApCount(int managedDownApCount);

	public int getNewUpApCount();

	public void setNewUpApCount(int newUpApCount);

	public int getNewDownApCount();

	public void setNewDownApCount(int newDownApCount);

	public int getClientCount();

	public void setClientCount(int clientCount);

	public boolean isSelected();

	public void setSelected(boolean selected);
}
