/**
 *@filename		BeQueryGroupInfoFromHMResult.java
 *@version
 *@author		Fiona
 *@createtime	Aug 16, 2012 3:57:45 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@SuppressWarnings("serial")
public class BeQueryGroupInfoFromHMResult extends BePortalHMPayloadResultEvent {
	private Map<String, String>	userInfos;
	private List<String> groupNames;

	public BeQueryGroupInfoFromHMResult() {
		super();
		payloadResultType = RESULTTYPE_VHM_USER_GROUPS;
	}

	protected byte[] buildOperationResult() throws Exception {
		if (groupNames == null) {
			throw new BeCommunicationEncodeException("groupNames is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(groupNames.size());

		for (String name : groupNames) {
			AhEncoder.putString(buf, name);
		}
		
		buf.putInt(userInfos.size());
		
		for (String userName : userInfos.keySet()) {
			String groupName = userInfos.get(userName);
			AhEncoder.putString(buf, userName);
			AhEncoder.putString(buf, groupName);
		}

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			groupNames = new ArrayList<String>();
			int groupSize = buf.getInt();
			
			for (int i = 0; i < groupSize; i++) {
				groupNames.add(AhDecoder.getString(buf));
			}
			
			userInfos = new HashMap<String, String>();
			int size = buf.getInt();

			for (int i = 0; i < size; i++) {
				userInfos.put(AhDecoder.getString(buf), AhDecoder.getString(buf));
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryGroupInfoFromHMResult.parsePacket() catch exception", e);
		}
	}

	public Map<String, String> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(Map<String, String> userInfos) {
		this.userInfos = userInfos;
	}

	public List<String> getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}
	
}
