package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeReportVhmMovingStatusEvent extends BePortalHMPayloadEvent {

	private static final long	serialVersionUID	= 1L;

	private VhmRumStatus		moveStatus;

	public BeReportVhmMovingStatusEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_REPORT_MOVING_STATUS;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (moveStatus == null) {
			throw new BeCommunicationEncodeException("moveStatus is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, moveStatus.getVhmName());
		AhEncoder.putString(buf, moveStatus.getSrcHmolAddress());
		AhEncoder.putString(buf, moveStatus.getDestHmolAddress());
		buf.putInt(moveStatus.getStatus());
		buf.putInt(moveStatus.getProcessStatus());
		buf.put(moveStatus.isSuccess() ? (byte) 1 : (byte) 0);
		AhEncoder.putString(buf, moveStatus.getFailureInfo());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		moveStatus = new VhmRumStatus();
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

		return null;
	}

	public VhmRumStatus getMoveStatus() {
		return moveStatus;
	}

	public void setMoveStatus(VhmRumStatus moveStatus) {
		this.moveStatus = moveStatus;
	}

}
