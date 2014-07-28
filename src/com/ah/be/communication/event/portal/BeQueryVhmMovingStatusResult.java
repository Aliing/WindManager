package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeQueryVhmMovingStatusResult extends BePortalHMPayloadResultEvent {
	private static final long serialVersionUID = 1L;

	private List<VhmRumStatus> moveStatuses;

	public BeQueryVhmMovingStatusResult() {
		super();
		payloadResultType = RESULTTYPE_QUERY_VHM_MOVING_STATUS;
	}

	@Override
	protected byte[] buildOperationResult() throws Exception {
		if (moveStatuses == null || moveStatuses.size() == 0) {
			throw new BeCommunicationEncodeException("moveStatuses is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(moveStatuses.size());
		for (VhmRumStatus moveStatus : moveStatuses) {
			AhEncoder.putString(buf, moveStatus.getVhmName());
			AhEncoder.putString(buf, moveStatus.getSrcHmolAddress());
			AhEncoder.putString(buf, moveStatus.getDestHmolAddress());
			buf.putInt(moveStatus.getStatus());
			buf.putInt(moveStatus.getProcessStatus());
			buf.put(moveStatus.isSuccess() ? (byte) 1 : (byte) 0);
			AhEncoder.putString(buf, moveStatus.getFailureInfo());
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
			moveStatuses = new ArrayList<VhmRumStatus>();
			int size = buf.getInt();
			for (int i = 0; i < size; i++) {
				VhmRumStatus moveStatus = new VhmRumStatus();
				moveStatus.setVhmName(AhDecoder.getString(buf));
				moveStatus.setSrcHmolAddress(AhDecoder.getString(buf));
				moveStatus.setDestHmolAddress(AhDecoder.getString(buf));
				moveStatus.setStatus(buf.getInt());
				moveStatus.setProcessStatus(buf.getInt());
				if (buf.get() == (byte) 1) {
					moveStatus.setSuccess(true);
				} else {
					moveStatus.setSuccess(false);
				}
				moveStatus.setFailureInfo(AhDecoder.getString(buf));

				moveStatuses.add(moveStatus);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryVhmMovingStatusResult parsePacket() catch exception", e);
		}
	}

	public List<VhmRumStatus> getMoveStatuses() {
		return moveStatuses;
	}

	public void setMoveStatuses(List<VhmRumStatus> moveStatuses) {
		this.moveStatuses = moveStatuses;
	}

}
