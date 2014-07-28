package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.UserInfo;
import com.ah.bo.admin.HmUserGroup;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeCreateVHMEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-25 03:38:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCreateUserEvent extends BePortalHMPayloadEvent {

	public BeCreateUserEvent() {
		super();
		operationType = OPERATIONTYPE_USER_CREATE;
	}

	private UserInfo user;

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (user.getUsername() == null) {
			throw new BeCommunicationEncodeException("userName is a necessary field!");
		}

		if (user.getPassword() == null) {
			throw new BeCommunicationEncodeException("password is a necessary field!");
		}

		if (user.getEmailAddress() == null) {
			throw new BeCommunicationEncodeException("emailAddress is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, user.getUsername());
		AhEncoder.putString(buf, user.getPassword());
		AhEncoder.putString(buf, user.getFullname());
		AhEncoder.putString(buf, user.getEmailAddress());
		buf.putInt(user.isVadAdmin() ? HmUserGroup.ADMINISTRATOR_ATTRIBUTE : user
				.getGroupAttribute());
		AhEncoder.putString(buf, user.getVhmName());
		buf.put((byte) (user.isVadAdmin() ? 1 : 0));
		buf.putInt(user.getMaxAPNum());
		AhEncoder.putString(buf, user.getTimeZone());
		buf.put((byte) (user.isDefaultFlag() ? 1 : 0));
		buf.put((byte) (user.isAccessMyHive() ? 1 : 0));

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		user = new UserInfo();

		user.setUsername(AhDecoder.getString(buf));
		user.setPassword(AhDecoder.getString(buf));
		user.setFullname(AhDecoder.getString(buf));
		user.setEmailAddress(AhDecoder.getString(buf));
		user.setGroupAttribute((short) buf.getInt());
		user.setVhmName(AhDecoder.getString(buf));
		boolean isVAD = buf.get() == 1;
		if (isVAD) {
			user.setGroupAttribute((short)HmUserGroup.VAD_ATTRIBUTE);
		}
		user.setMaxAPNum(buf.getInt());
		user.setTimeZone(AhDecoder.getString(buf));
		user.setDefaultFlag(buf.get() == 1);
		
		return null;
	}

}
