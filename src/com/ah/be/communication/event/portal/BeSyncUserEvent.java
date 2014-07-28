package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.UserInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeSyncUserEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-26 10:38:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeSyncUserEvent extends BePortalHMPayloadEvent {

	private String vhmName;

	private List<UserInfo> userList;

	public BeSyncUserEvent() {
		super();
		operationType = OPERATIONTYPE_USER_SYNC;
	}

	@Override
	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (vhmName == null) {
			throw new BeCommunicationEncodeException("vhmName is a necessary field!");
		}
		if (userList == null || userList.size() == 0) {
			throw new BeCommunicationEncodeException("userList is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, vhmName);
		buf.putShort((short) userList.size());
		for (UserInfo user : userList) {
			AhEncoder.putString(buf, user.getUsername());
			AhEncoder.putString(buf, user.getPassword());
			AhEncoder.putString(buf, user.getFullname());
			AhEncoder.putString(buf, user.getEmailAddress());
			buf.putShort(user.getGroupAttribute());
			buf.putInt(user.getMaxAPNum());
			AhEncoder.putString(buf, user.getTimeZone());
			buf.put((byte) (user.isDefaultFlag() ? 1 : 0));
		}

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	@Override
	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		vhmName = AhDecoder.getString(buf);
		short size = buf.getShort();
		userList = new ArrayList<UserInfo>(size);
		for (int i = 0; i < size; i++) {
			UserInfo user = new UserInfo();

			user.setUsername(AhDecoder.getString(buf));
			user.setPassword(AhDecoder.getString(buf));
			user.setFullname(AhDecoder.getString(buf));
			user.setEmailAddress(AhDecoder.getString(buf));
			user.setGroupAttribute(buf.getShort());
			user.setMaxAPNum(buf.getInt());
			user.setTimeZone(AhDecoder.getString(buf));
			user.setDefaultFlag(buf.get() == 1);

			userList.add(user);
		}

		return null;
	}

	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

}
