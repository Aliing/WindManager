package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.UserInfo;
import com.ah.bo.admin.HmUserGroup;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeQueryVhmUsersResult extends BePortalHMPayloadResultEvent {

	private List<UserInfo> users;

	public List<UserInfo> getUsers() {
		return users;
	}

	public void setUsers(List<UserInfo> users) {
		this.users = users;
	}

	public BeQueryVhmUsersResult() {
		super();
		payloadResultType = RESULTTYPE_QUERY_VHMUSERS;
	}

	protected byte[] buildOperationResult() throws Exception {
		if (users == null || users.size() == 0) {
			throw new BeCommunicationEncodeException("users is a necessary field!");
		}
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(users.size());
		for (UserInfo user : users) {
			AhEncoder.putString(buf, user.getVhmName());
			AhEncoder.putString(buf, user.getUsername());
			AhEncoder.putString(buf, user.getFullname());
			AhEncoder.putString(buf, user.getEmailAddress());
			AhEncoder.putString(buf, user.getPassword());
			buf.putShort(user.getDefaultApp());
			buf.putShort(user.isVadAdmin() ? HmUserGroup.ADMINISTRATOR_ATTRIBUTE : user
					.getGroupAttribute());
			if (user.isVadAdmin()) {
				buf.put((byte) 0x01);
			} else {
				buf.put((byte) 0x00);
			}
			if (user.isDefaultFlag()) {
				buf.put((byte) 0x01);
			} else {
				buf.put((byte) 0x00);
			}
			AhEncoder.putString(buf, user.getTimeZone());
		}

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			users = new ArrayList<UserInfo>();
			int size = buf.getInt();
			for (int i = 0; i < size; i++) {
				UserInfo user = new UserInfo();
				user.setVhmName(AhDecoder.getString(buf));
				user.setUsername(AhDecoder.getString(buf));
				user.setFullname(AhDecoder.getString(buf));
				user.setEmailAddress(AhDecoder.getString(buf));
				user.setPassword(AhDecoder.getString(buf));
				user.setDefaultApp(buf.getShort());
				user.setGroupAttribute(buf.getShort());
				boolean isVAD = buf.get() == 1;
				if (isVAD) {
					user.setGroupAttribute((short)HmUserGroup.VAD_ATTRIBUTE);
				}
				if (buf.get() == 0x01) {
					user.setDefaultFlag(true);
				} else {
					user.setDefaultFlag(false);
				}
				user.setTimeZone(AhDecoder.getString(buf));
				users.add(user);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryVhmUsersResult parsePacket() catch exception", e);
		}
	}
}
